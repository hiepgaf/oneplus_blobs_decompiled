package com.android.server.pm;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.IPackageInstallObserver2.Stub;
import android.content.pm.IPackageInstallerSession.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller.SessionInfo;
import android.content.pm.PackageInstaller.SessionParams;
import android.content.pm.PackageParser;
import android.content.pm.PackageParser.ApkLite;
import android.content.pm.PackageParser.PackageLite;
import android.content.pm.PackageParser.PackageParserException;
import android.content.pm.Signature;
import android.os.Binder;
import android.os.Bundle;
import android.os.FileBridge;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.system.StructStat;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.ExceptionUtils;
import android.util.MathUtils;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.content.NativeLibraryHelper.Handle;
import com.android.internal.content.PackageHelper;
import com.android.internal.os.InstallerConnection.InstallerException;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.IOException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoUtils;
import libcore.io.Libcore;

public class PackageInstallerSession
  extends IPackageInstallerSession.Stub
{
  private static final boolean LOGD = true;
  private static final int MSG_COMMIT = 0;
  private static final String REMOVE_SPLIT_MARKER_EXTENSION = ".removed";
  private static final String TAG = "PackageInstaller";
  private static final FileFilter sAddedFilter = new FileFilter()
  {
    public boolean accept(File paramAnonymousFile)
    {
      if (paramAnonymousFile.isDirectory()) {
        return false;
      }
      return !paramAnonymousFile.getName().endsWith(".removed");
    }
  };
  private static final FileFilter sRemovedFilter = new FileFilter()
  {
    public boolean accept(File paramAnonymousFile)
    {
      if (paramAnonymousFile.isDirectory()) {
        return false;
      }
      return paramAnonymousFile.getName().endsWith(".removed");
    }
  };
  final long createdMillis;
  final int defaultContainerGid;
  final String installerPackageName;
  final int installerUid;
  private final AtomicInteger mActiveCount = new AtomicInteger();
  @GuardedBy("mLock")
  private ArrayList<FileBridge> mBridges = new ArrayList();
  private final PackageInstallerService.InternalCallback mCallback;
  private Certificate[][] mCertificates;
  @GuardedBy("mLock")
  private float mClientProgress = 0.0F;
  private final Context mContext;
  @GuardedBy("mLock")
  private boolean mDestroyed = false;
  private String mFinalMessage;
  private int mFinalStatus;
  private final Handler mHandler;
  private final Handler.Callback mHandlerCallback = new Handler.Callback()
  {
    public boolean handleMessage(Message paramAnonymousMessage)
    {
      Object localObject2 = PackageInstallerSession.-get1(PackageInstallerSession.this).getPackageInfo(PackageInstallerSession.this.params.appPackageName, 64, PackageInstallerSession.this.userId);
      ApplicationInfo localApplicationInfo = PackageInstallerSession.-get1(PackageInstallerSession.this).getApplicationInfo(PackageInstallerSession.this.params.appPackageName, 0, PackageInstallerSession.this.userId);
      synchronized (PackageInstallerSession.-get0(PackageInstallerSession.this))
      {
        if (paramAnonymousMessage.obj != null) {
          PackageInstallerSession.-set0(PackageInstallerSession.this, (IPackageInstallObserver2)paramAnonymousMessage.obj);
        }
        try
        {
          PackageInstallerSession.-wrap0(PackageInstallerSession.this, (PackageInfo)localObject2, localApplicationInfo);
          return true;
        }
        catch (PackageManagerException paramAnonymousMessage)
        {
          for (;;)
          {
            localObject2 = ExceptionUtils.getCompleteMessage(paramAnonymousMessage);
            Slog.e("PackageInstaller", "Commit of session " + PackageInstallerSession.this.sessionId + " failed: " + (String)localObject2);
            PackageInstallerSession.-wrap1(PackageInstallerSession.this);
            PackageInstallerSession.-wrap2(PackageInstallerSession.this, paramAnonymousMessage.error, (String)localObject2, null);
          }
        }
      }
    }
  };
  @GuardedBy("mLock")
  private File mInheritedFilesBase;
  @GuardedBy("mLock")
  private float mInternalProgress = 0.0F;
  private final boolean mIsInstallerDeviceOwner;
  private final Object mLock = new Object();
  private String mPackageName;
  @GuardedBy("mLock")
  private boolean mPermissionsAccepted = false;
  private final PackageManagerService mPm;
  @GuardedBy("mLock")
  private boolean mPrepared = false;
  @GuardedBy("mLock")
  private float mProgress = 0.0F;
  @GuardedBy("mLock")
  private boolean mRelinquished = false;
  @GuardedBy("mLock")
  private IPackageInstallObserver2 mRemoteObserver;
  @GuardedBy("mLock")
  private float mReportedProgress = -1.0F;
  @GuardedBy("mLock")
  private File mResolvedBaseFile;
  @GuardedBy("mLock")
  private final List<File> mResolvedInheritedFiles = new ArrayList();
  @GuardedBy("mLock")
  private final List<String> mResolvedInstructionSets = new ArrayList();
  @GuardedBy("mLock")
  private File mResolvedStageDir;
  @GuardedBy("mLock")
  private final List<File> mResolvedStagedFiles = new ArrayList();
  @GuardedBy("mLock")
  private boolean mSealed = false;
  private Signature[] mSignatures;
  private int mVersionCode;
  final PackageInstaller.SessionParams params;
  final int sessionId;
  final String stageCid;
  final File stageDir;
  final int userId;
  
  public PackageInstallerSession(PackageInstallerService.InternalCallback paramInternalCallback, Context paramContext, PackageManagerService paramPackageManagerService, Looper paramLooper, int paramInt1, int paramInt2, String paramString1, int paramInt3, PackageInstaller.SessionParams paramSessionParams, long paramLong, File paramFile, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mCallback = paramInternalCallback;
    this.mContext = paramContext;
    this.mPm = paramPackageManagerService;
    this.mHandler = new Handler(paramLooper, this.mHandlerCallback);
    this.sessionId = paramInt1;
    this.userId = paramInt2;
    this.installerPackageName = paramString1;
    this.installerUid = paramInt3;
    this.params = paramSessionParams;
    this.createdMillis = paramLong;
    this.stageDir = paramFile;
    this.stageCid = paramString2;
    if (paramFile == null)
    {
      paramInt1 = 1;
      if (paramString2 != null) {
        break label247;
      }
    }
    label247:
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      if (paramInt1 != paramInt2) {
        break label253;
      }
      throw new IllegalArgumentException("Exactly one of stageDir or stageCid stage must be set");
      paramInt1 = 0;
      break;
    }
    label253:
    this.mPrepared = paramBoolean1;
    this.mSealed = paramBoolean2;
    paramInternalCallback = (DevicePolicyManager)this.mContext.getSystemService("device_policy");
    if (this.mPm.checkUidPermission("android.permission.INSTALL_PACKAGES", paramInt3) == 0)
    {
      paramInt1 = 1;
      if (paramInt3 != 0) {
        break label399;
      }
      paramInt2 = 1;
      label303:
      if ((paramSessionParams.installFlags & 0x400) == 0) {
        break label405;
      }
      paramInt3 = 1;
      label318:
      if (paramInternalCallback == null) {
        break label411;
      }
      paramBoolean1 = paramInternalCallback.isDeviceOwnerAppOnCallingUser(paramString1);
      label330:
      this.mIsInstallerDeviceOwner = paramBoolean1;
      if (((paramInt1 != 0) || (paramInt2 != 0) || (this.mIsInstallerDeviceOwner)) && (paramInt3 == 0)) {
        break label417;
      }
    }
    label399:
    label405:
    label411:
    label417:
    for (this.mPermissionsAccepted = false;; this.mPermissionsAccepted = true)
    {
      paramLong = Binder.clearCallingIdentity();
      try
      {
        this.defaultContainerGid = UserHandle.getSharedAppGid(this.mPm.getPackageUid("com.android.defcontainer", 1048576, 0));
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(paramLong);
      }
      paramInt1 = 0;
      break;
      paramInt2 = 0;
      break label303;
      paramInt3 = 0;
      break label318;
      paramBoolean1 = false;
      break label330;
    }
  }
  
  private void assertApkConsistent(String paramString, PackageParser.ApkLite paramApkLite)
    throws PackageManagerException
  {
    if (!this.mPackageName.equals(paramApkLite.packageName)) {
      throw new PackageManagerException(-2, paramString + " package " + paramApkLite.packageName + " inconsistent with " + this.mPackageName);
    }
    if ((this.params.appPackageName == null) || (this.params.appPackageName.equals(paramApkLite.packageName)))
    {
      if (this.mVersionCode != paramApkLite.versionCode) {
        throw new PackageManagerException(-2, paramString + " version code " + paramApkLite.versionCode + " inconsistent with " + this.mVersionCode);
      }
    }
    else {
      throw new PackageManagerException(-2, paramString + " specified package " + this.params.appPackageName + " inconsistent with " + paramApkLite.packageName);
    }
    if (!Signature.areExactMatch(this.mSignatures, paramApkLite.signatures)) {
      throw new PackageManagerException(-2, paramString + " signatures are inconsistent");
    }
  }
  
  private void assertPreparedAndNotSealed(String paramString)
  {
    synchronized (this.mLock)
    {
      if (!this.mPrepared) {
        throw new IllegalStateException(paramString + " before prepared");
      }
    }
    if (this.mSealed) {
      throw new SecurityException(paramString + " not allowed after commit");
    }
  }
  
  private long calculateInstalledSize()
    throws PackageManagerException
  {
    Preconditions.checkNotNull(this.mResolvedBaseFile);
    ArrayList localArrayList;
    Iterator localIterator;
    File localFile;
    try
    {
      PackageParser.ApkLite localApkLite = PackageParser.parseApkLite(this.mResolvedBaseFile, 0);
      localArrayList = new ArrayList();
      localIterator = this.mResolvedStagedFiles.iterator();
      while (localIterator.hasNext())
      {
        localFile = (File)localIterator.next();
        if (!this.mResolvedBaseFile.equals(localFile)) {
          localArrayList.add(localFile.getAbsolutePath());
        }
      }
      localIterator = this.mResolvedInheritedFiles.iterator();
    }
    catch (PackageParser.PackageParserException localPackageParserException)
    {
      throw PackageManagerException.from(localPackageParserException);
    }
    while (localIterator.hasNext())
    {
      localFile = (File)localIterator.next();
      if (!this.mResolvedBaseFile.equals(localFile)) {
        localArrayList.add(localFile.getAbsolutePath());
      }
    }
    PackageParser.PackageLite localPackageLite = new PackageParser.PackageLite(null, localPackageParserException, null, (String[])localArrayList.toArray(new String[localArrayList.size()]), null);
    if ((this.params.installFlags & 0x1) != 0) {}
    for (boolean bool = true;; bool = false) {
      try
      {
        long l = PackageHelper.calculateInstalledSize(localPackageLite, bool, this.params.abiOverride);
        return l;
      }
      catch (IOException localIOException)
      {
        throw new PackageManagerException(-2, "Failed to calculate install size", localIOException);
      }
    }
  }
  
  /* Error */
  private void commitLocked(PackageInfo paramPackageInfo, ApplicationInfo paramApplicationInfo)
    throws PackageManagerException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 158	com/android/server/pm/PackageInstallerSession:mDestroyed	Z
    //   4: ifeq +16 -> 20
    //   7: new 265	com/android/server/pm/PackageManagerException
    //   10: dup
    //   11: bipush -110
    //   13: ldc_w 418
    //   16: invokespecial 296	com/android/server/pm/PackageManagerException:<init>	(ILjava/lang/String;)V
    //   19: athrow
    //   20: aload_0
    //   21: getfield 152	com/android/server/pm/PackageInstallerSession:mSealed	Z
    //   24: ifne +16 -> 40
    //   27: new 265	com/android/server/pm/PackageManagerException
    //   30: dup
    //   31: bipush -110
    //   33: ldc_w 420
    //   36: invokespecial 296	com/android/server/pm/PackageManagerException:<init>	(ILjava/lang/String;)V
    //   39: athrow
    //   40: aload_0
    //   41: invokespecial 424	com/android/server/pm/PackageInstallerSession:resolveStageDir	()Ljava/io/File;
    //   44: pop
    //   45: aload_0
    //   46: aload_1
    //   47: aload_2
    //   48: invokespecial 427	com/android/server/pm/PackageInstallerSession:validateInstallLocked	(Landroid/content/pm/PackageInfo;Landroid/content/pm/ApplicationInfo;)V
    //   51: aload_0
    //   52: getfield 267	com/android/server/pm/PackageInstallerSession:mPackageName	Ljava/lang/String;
    //   55: invokestatic 349	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   58: pop
    //   59: aload_0
    //   60: getfield 313	com/android/server/pm/PackageInstallerSession:mSignatures	[Landroid/content/pm/Signature;
    //   63: invokestatic 349	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   66: pop
    //   67: aload_0
    //   68: getfield 343	com/android/server/pm/PackageInstallerSession:mResolvedBaseFile	Ljava/io/File;
    //   71: invokestatic 349	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   74: pop
    //   75: aload_0
    //   76: getfield 154	com/android/server/pm/PackageInstallerSession:mPermissionsAccepted	Z
    //   79: ifne +71 -> 150
    //   82: new 429	android/content/Intent
    //   85: dup
    //   86: ldc_w 431
    //   89: invokespecial 432	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   92: astore_1
    //   93: aload_1
    //   94: aload_0
    //   95: getfield 177	com/android/server/pm/PackageInstallerSession:mContext	Landroid/content/Context;
    //   98: invokevirtual 436	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   101: invokevirtual 441	android/content/pm/PackageManager:getPermissionControllerPackageName	()Ljava/lang/String;
    //   104: invokevirtual 445	android/content/Intent:setPackage	(Ljava/lang/String;)Landroid/content/Intent;
    //   107: pop
    //   108: aload_1
    //   109: ldc_w 447
    //   112: aload_0
    //   113: getfield 186	com/android/server/pm/PackageInstallerSession:sessionId	I
    //   116: invokevirtual 451	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   119: pop
    //   120: aload_0
    //   121: getfield 102	com/android/server/pm/PackageInstallerSession:mRemoteObserver	Landroid/content/pm/IPackageInstallObserver2;
    //   124: aload_1
    //   125: invokeinterface 457 2 0
    //   130: aload_0
    //   131: invokevirtual 460	com/android/server/pm/PackageInstallerSession:close	()V
    //   134: return
    //   135: astore_1
    //   136: new 265	com/android/server/pm/PackageManagerException
    //   139: dup
    //   140: bipush -18
    //   142: ldc_w 462
    //   145: aload_1
    //   146: invokespecial 414	com/android/server/pm/PackageManagerException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   149: athrow
    //   150: aload_0
    //   151: getfield 200	com/android/server/pm/PackageInstallerSession:stageCid	Ljava/lang/String;
    //   154: ifnull +16 -> 170
    //   157: aload_0
    //   158: invokespecial 464	com/android/server/pm/PackageInstallerSession:calculateInstalledSize	()J
    //   161: lstore_3
    //   162: aload_0
    //   163: getfield 200	com/android/server/pm/PackageInstallerSession:stageCid	Ljava/lang/String;
    //   166: lload_3
    //   167: invokestatic 468	com/android/server/pm/PackageInstallerSession:resizeContainer	(Ljava/lang/String;J)V
    //   170: aload_0
    //   171: getfield 194	com/android/server/pm/PackageInstallerSession:params	Landroid/content/pm/PackageInstaller$SessionParams;
    //   174: getfield 471	android/content/pm/PackageInstaller$SessionParams:mode	I
    //   177: iconst_2
    //   178: if_icmpne +141 -> 319
    //   181: aload_0
    //   182: getfield 167	com/android/server/pm/PackageInstallerSession:mResolvedInheritedFiles	Ljava/util/List;
    //   185: astore_1
    //   186: aload_0
    //   187: invokespecial 424	com/android/server/pm/PackageInstallerSession:resolveStageDir	()Ljava/io/File;
    //   190: astore_2
    //   191: ldc 25
    //   193: new 280	java/lang/StringBuilder
    //   196: dup
    //   197: invokespecial 281	java/lang/StringBuilder:<init>	()V
    //   200: ldc_w 473
    //   203: invokevirtual 285	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: aload_0
    //   207: getfield 167	com/android/server/pm/PackageInstallerSession:mResolvedInheritedFiles	Ljava/util/List;
    //   210: invokevirtual 476	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   213: invokevirtual 293	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   216: invokestatic 482	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   219: pop
    //   220: aload_0
    //   221: getfield 167	com/android/server/pm/PackageInstallerSession:mResolvedInheritedFiles	Ljava/util/List;
    //   224: invokeinterface 485 1 0
    //   229: ifne +36 -> 265
    //   232: aload_0
    //   233: getfield 487	com/android/server/pm/PackageInstallerSession:mInheritedFilesBase	Ljava/io/File;
    //   236: ifnonnull +29 -> 265
    //   239: new 328	java/lang/IllegalStateException
    //   242: dup
    //   243: ldc_w 489
    //   246: invokespecial 331	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   249: athrow
    //   250: astore_1
    //   251: new 265	com/android/server/pm/PackageManagerException
    //   254: dup
    //   255: bipush -4
    //   257: ldc_w 491
    //   260: aload_1
    //   261: invokespecial 414	com/android/server/pm/PackageManagerException:<init>	(ILjava/lang/String;Ljava/lang/Throwable;)V
    //   264: athrow
    //   265: aload_0
    //   266: aload_1
    //   267: aload_2
    //   268: invokespecial 495	com/android/server/pm/PackageInstallerSession:isLinkPossible	(Ljava/util/List;Ljava/io/File;)Z
    //   271: ifeq +158 -> 429
    //   274: aload_0
    //   275: getfield 169	com/android/server/pm/PackageInstallerSession:mResolvedInstructionSets	Ljava/util/List;
    //   278: invokeinterface 485 1 0
    //   283: ifne +26 -> 309
    //   286: new 373	java/io/File
    //   289: dup
    //   290: aload_2
    //   291: ldc_w 497
    //   294: invokespecial 500	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   297: astore 5
    //   299: aload_0
    //   300: aload_0
    //   301: getfield 169	com/android/server/pm/PackageInstallerSession:mResolvedInstructionSets	Ljava/util/List;
    //   304: aload 5
    //   306: invokespecial 504	com/android/server/pm/PackageInstallerSession:createOatDirs	(Ljava/util/List;Ljava/io/File;)V
    //   309: aload_0
    //   310: aload_1
    //   311: aload_2
    //   312: aload_0
    //   313: getfield 487	com/android/server/pm/PackageInstallerSession:mInheritedFilesBase	Ljava/io/File;
    //   316: invokespecial 508	com/android/server/pm/PackageInstallerSession:linkFiles	(Ljava/util/List;Ljava/io/File;Ljava/io/File;)V
    //   319: aload_0
    //   320: ldc_w 509
    //   323: putfield 143	com/android/server/pm/PackageInstallerSession:mInternalProgress	F
    //   326: aload_0
    //   327: iconst_1
    //   328: invokespecial 513	com/android/server/pm/PackageInstallerSession:computeProgressLocked	(Z)V
    //   331: aload_0
    //   332: getfield 515	com/android/server/pm/PackageInstallerSession:mResolvedStageDir	Ljava/io/File;
    //   335: aload_0
    //   336: getfield 194	com/android/server/pm/PackageInstallerSession:params	Landroid/content/pm/PackageInstaller$SessionParams;
    //   339: getfield 404	android/content/pm/PackageInstaller$SessionParams:abiOverride	Ljava/lang/String;
    //   342: invokestatic 518	com/android/server/pm/PackageInstallerSession:extractNativeLibraries	(Ljava/io/File;Ljava/lang/String;)V
    //   345: aload_0
    //   346: getfield 200	com/android/server/pm/PackageInstallerSession:stageCid	Ljava/lang/String;
    //   349: ifnull +11 -> 360
    //   352: aload_0
    //   353: aload_0
    //   354: getfield 200	com/android/server/pm/PackageInstallerSession:stageCid	Ljava/lang/String;
    //   357: invokespecial 521	com/android/server/pm/PackageInstallerSession:finalizeAndFixContainer	(Ljava/lang/String;)V
    //   360: new 12	com/android/server/pm/PackageInstallerSession$4
    //   363: dup
    //   364: aload_0
    //   365: invokespecial 522	com/android/server/pm/PackageInstallerSession$4:<init>	(Lcom/android/server/pm/PackageInstallerSession;)V
    //   368: astore_2
    //   369: aload_0
    //   370: getfield 194	com/android/server/pm/PackageInstallerSession:params	Landroid/content/pm/PackageInstaller$SessionParams;
    //   373: getfield 230	android/content/pm/PackageInstaller$SessionParams:installFlags	I
    //   376: bipush 64
    //   378: iand
    //   379: ifeq +58 -> 437
    //   382: getstatic 526	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   385: astore_1
    //   386: aload_0
    //   387: iconst_1
    //   388: putfield 156	com/android/server/pm/PackageInstallerSession:mRelinquished	Z
    //   391: aload_0
    //   392: getfield 98	com/android/server/pm/PackageInstallerSession:mPm	Lcom/android/server/pm/PackageManagerService;
    //   395: aload_0
    //   396: getfield 267	com/android/server/pm/PackageInstallerSession:mPackageName	Ljava/lang/String;
    //   399: aload_0
    //   400: getfield 198	com/android/server/pm/PackageInstallerSession:stageDir	Ljava/io/File;
    //   403: aload_0
    //   404: getfield 200	com/android/server/pm/PackageInstallerSession:stageCid	Ljava/lang/String;
    //   407: aload_2
    //   408: aload_0
    //   409: getfield 194	com/android/server/pm/PackageInstallerSession:params	Landroid/content/pm/PackageInstaller$SessionParams;
    //   412: aload_0
    //   413: getfield 190	com/android/server/pm/PackageInstallerSession:installerPackageName	Ljava/lang/String;
    //   416: aload_0
    //   417: getfield 192	com/android/server/pm/PackageInstallerSession:installerUid	I
    //   420: aload_1
    //   421: aload_0
    //   422: getfield 528	com/android/server/pm/PackageInstallerSession:mCertificates	[[Ljava/security/cert/Certificate;
    //   425: invokevirtual 532	com/android/server/pm/PackageManagerService:installStage	(Ljava/lang/String;Ljava/io/File;Ljava/lang/String;Landroid/content/pm/IPackageInstallObserver2;Landroid/content/pm/PackageInstaller$SessionParams;Ljava/lang/String;ILandroid/os/UserHandle;[[Ljava/security/cert/Certificate;)V
    //   428: return
    //   429: aload_1
    //   430: aload_2
    //   431: invokestatic 535	com/android/server/pm/PackageInstallerSession:copyFiles	(Ljava/util/List;Ljava/io/File;)V
    //   434: goto -115 -> 319
    //   437: new 251	android/os/UserHandle
    //   440: dup
    //   441: aload_0
    //   442: getfield 188	com/android/server/pm/PackageInstallerSession:userId	I
    //   445: invokespecial 538	android/os/UserHandle:<init>	(I)V
    //   448: astore_1
    //   449: goto -63 -> 386
    //   452: astore_1
    //   453: goto -323 -> 130
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	456	0	this	PackageInstallerSession
    //   0	456	1	paramPackageInfo	PackageInfo
    //   0	456	2	paramApplicationInfo	ApplicationInfo
    //   161	6	3	l	long
    //   297	8	5	localFile	File
    // Exception table:
    //   from	to	target	type
    //   40	45	135	java/io/IOException
    //   181	250	250	java/io/IOException
    //   265	309	250	java/io/IOException
    //   309	319	250	java/io/IOException
    //   429	434	250	java/io/IOException
    //   120	130	452	android/os/RemoteException
  }
  
  private void computeProgressLocked(boolean paramBoolean)
  {
    this.mProgress = (MathUtils.constrain(this.mClientProgress * 0.8F, 0.0F, 0.8F) + MathUtils.constrain(this.mInternalProgress * 0.2F, 0.0F, 0.2F));
    if ((paramBoolean) || (Math.abs(this.mProgress - this.mReportedProgress) >= 0.01D))
    {
      this.mReportedProgress = this.mProgress;
      this.mCallback.onSessionProgressChanged(this, this.mProgress);
    }
  }
  
  private static void copyFiles(List<File> paramList, File paramFile)
    throws IOException
  {
    Object localObject = paramFile.listFiles();
    int i = 0;
    int j = localObject.length;
    while (i < j)
    {
      localIterator = localObject[i];
      if (localIterator.getName().endsWith(".tmp")) {
        localIterator.delete();
      }
      i += 1;
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      File localFile = (File)localIterator.next();
      localObject = File.createTempFile("inherit", ".tmp", paramFile);
      Slog.d("PackageInstaller", "Copying " + localFile + " to " + localObject);
      if (!FileUtils.copyFile(localFile, (File)localObject)) {
        throw new IOException("Failed to copy " + localFile + " to " + localObject);
      }
      try
      {
        android.system.Os.chmod(((File)localObject).getAbsolutePath(), 420);
        localFile = new File(paramFile, localFile.getName());
        Slog.d("PackageInstaller", "Renaming " + localObject + " to " + localFile);
        if (!((File)localObject).renameTo(localFile)) {
          throw new IOException("Failed to rename " + localObject + " to " + localFile);
        }
      }
      catch (ErrnoException paramList)
      {
        throw new IOException("Failed to chmod " + localObject);
      }
    }
    Slog.d("PackageInstaller", "Copied " + paramList.size() + " files into " + paramFile);
  }
  
  private void createOatDirs(List<String> paramList, File paramFile)
    throws PackageManagerException
  {
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      String str = (String)paramList.next();
      try
      {
        this.mPm.mInstaller.createOatDir(paramFile.getAbsolutePath(), str);
      }
      catch (InstallerConnection.InstallerException paramList)
      {
        throw PackageManagerException.from(paramList);
      }
    }
  }
  
  private void createRemoveSplitMarker(String paramString)
    throws IOException
  {
    try
    {
      paramString = paramString + ".removed";
      if (!FileUtils.isValidExtFilename(paramString)) {
        throw new IllegalArgumentException("Invalid marker: " + paramString);
      }
    }
    catch (ErrnoException paramString)
    {
      throw paramString.rethrowAsIOException();
    }
    paramString = new File(resolveStageDir(), paramString);
    paramString.createNewFile();
    android.system.Os.chmod(paramString.getAbsolutePath(), 0);
  }
  
  private void destroyInternal()
  {
    synchronized (this.mLock)
    {
      this.mSealed = true;
      this.mDestroyed = true;
      Iterator localIterator = this.mBridges.iterator();
      if (localIterator.hasNext()) {
        ((FileBridge)localIterator.next()).forceClose();
      }
    }
    if (this.stageDir != null) {}
    try
    {
      this.mPm.mInstaller.rmPackageDir(this.stageDir.getAbsolutePath());
      if (this.stageCid != null) {
        PackageHelper.destroySdDir(this.stageCid);
      }
      return;
    }
    catch (InstallerConnection.InstallerException localInstallerException)
    {
      for (;;) {}
    }
  }
  
  private void dispatchSessionFinished(int paramInt, String paramString, Bundle paramBundle)
  {
    this.mFinalStatus = paramInt;
    this.mFinalMessage = paramString;
    if (this.mRemoteObserver != null) {}
    try
    {
      this.mRemoteObserver.onPackageInstalled(this.mPackageName, paramInt, paramString, paramBundle);
      if (paramInt == 1) {}
      for (boolean bool = true;; bool = false)
      {
        this.mCallback.onSessionFinished(this, bool);
        return;
      }
    }
    catch (RemoteException paramString)
    {
      for (;;) {}
    }
  }
  
  private void dumpLocked(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("Session " + this.sessionId + ":");
    paramIndentingPrintWriter.increaseIndent();
    paramIndentingPrintWriter.printPair("userId", Integer.valueOf(this.userId));
    paramIndentingPrintWriter.printPair("installerPackageName", this.installerPackageName);
    paramIndentingPrintWriter.printPair("installerUid", Integer.valueOf(this.installerUid));
    paramIndentingPrintWriter.printPair("createdMillis", Long.valueOf(this.createdMillis));
    paramIndentingPrintWriter.printPair("stageDir", this.stageDir);
    paramIndentingPrintWriter.printPair("stageCid", this.stageCid);
    paramIndentingPrintWriter.println();
    this.params.dump(paramIndentingPrintWriter);
    paramIndentingPrintWriter.printPair("mClientProgress", Float.valueOf(this.mClientProgress));
    paramIndentingPrintWriter.printPair("mProgress", Float.valueOf(this.mProgress));
    paramIndentingPrintWriter.printPair("mSealed", Boolean.valueOf(this.mSealed));
    paramIndentingPrintWriter.printPair("mPermissionsAccepted", Boolean.valueOf(this.mPermissionsAccepted));
    paramIndentingPrintWriter.printPair("mRelinquished", Boolean.valueOf(this.mRelinquished));
    paramIndentingPrintWriter.printPair("mDestroyed", Boolean.valueOf(this.mDestroyed));
    paramIndentingPrintWriter.printPair("mBridges", Integer.valueOf(this.mBridges.size()));
    paramIndentingPrintWriter.printPair("mFinalStatus", Integer.valueOf(this.mFinalStatus));
    paramIndentingPrintWriter.printPair("mFinalMessage", this.mFinalMessage);
    paramIndentingPrintWriter.println();
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  private static void extractNativeLibraries(File paramFile, String paramString)
    throws PackageManagerException
  {
    File localFile2 = new File(paramFile, "lib");
    NativeLibraryHelper.removeNativeBinariesFromDirLI(localFile2, true);
    Object localObject = null;
    File localFile1 = null;
    try
    {
      paramFile = NativeLibraryHelper.Handle.create(paramFile);
      localFile1 = paramFile;
      localObject = paramFile;
      int i = NativeLibraryHelper.copyNativeBinariesWithOverride(paramFile, localFile2, paramString);
      if (i != 1)
      {
        localFile1 = paramFile;
        localObject = paramFile;
        throw new PackageManagerException(i, "Failed to extract native libraries, res=" + i);
      }
    }
    catch (IOException paramFile)
    {
      localObject = localFile1;
      throw new PackageManagerException(-110, "Failed to extract native libraries", paramFile);
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject);
    }
    IoUtils.closeQuietly(paramFile);
  }
  
  private void finalizeAndFixContainer(String paramString)
    throws PackageManagerException
  {
    if (!PackageHelper.finalizeSdDir(paramString)) {
      throw new PackageManagerException(-18, "Failed to finalize container " + paramString);
    }
    if (!PackageHelper.fixSdPermissions(paramString, this.defaultContainerGid, null)) {
      throw new PackageManagerException(-18, "Failed to fix permissions on container " + paramString);
    }
  }
  
  private static String getRelativePath(File paramFile1, File paramFile2)
    throws IOException
  {
    paramFile1 = paramFile1.getAbsolutePath();
    paramFile2 = paramFile2.getAbsolutePath();
    if (paramFile1.contains("/.")) {
      throw new IOException("Invalid path (was relative) : " + paramFile1);
    }
    if (paramFile1.startsWith(paramFile2)) {
      return paramFile1.substring(paramFile2.length());
    }
    throw new IOException("File: " + paramFile1 + " outside base: " + paramFile2);
  }
  
  private boolean isLinkPossible(List<File> paramList, File paramFile)
  {
    try
    {
      paramFile = android.system.Os.stat(paramFile.getAbsolutePath());
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        long l1 = android.system.Os.stat(((File)paramList.next()).getAbsolutePath()).st_dev;
        long l2 = paramFile.st_dev;
        if (l1 != l2) {
          return false;
        }
      }
    }
    catch (ErrnoException paramList)
    {
      Slog.w("PackageInstaller", "Failed to detect if linking possible: " + paramList);
      return false;
    }
    return true;
  }
  
  private void linkFiles(List<File> paramList, File paramFile1, File paramFile2)
    throws IOException
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = getRelativePath((File)localIterator.next(), paramFile2);
      try
      {
        this.mPm.mInstaller.linkFile(str, paramFile2.getAbsolutePath(), paramFile1.getAbsolutePath());
      }
      catch (InstallerConnection.InstallerException paramList)
      {
        throw new IOException("failed linkOrCreateDir(" + str + ", " + paramFile2 + ", " + paramFile1 + ")", paramList);
      }
    }
    Slog.d("PackageInstaller", "Linked " + paramList.size() + " files into " + paramFile1);
  }
  
  private ParcelFileDescriptor openReadInternal(String paramString)
    throws IOException
  {
    assertPreparedAndNotSealed("openRead");
    try
    {
      if (!FileUtils.isValidExtFilename(paramString)) {
        throw new IllegalArgumentException("Invalid name: " + paramString);
      }
    }
    catch (ErrnoException paramString)
    {
      throw paramString.rethrowAsIOException();
    }
    paramString = new File(resolveStageDir(), paramString);
    paramString = new ParcelFileDescriptor(Libcore.os.open(paramString.getAbsolutePath(), OsConstants.O_RDONLY, 0));
    return paramString;
  }
  
  private ParcelFileDescriptor openWriteInternal(String paramString, long paramLong1, long paramLong2)
    throws IOException
  {
    FileBridge localFileBridge;
    synchronized (this.mLock)
    {
      assertPreparedAndNotSealed("openWrite");
      localFileBridge = new FileBridge();
      this.mBridges.add(localFileBridge);
      try
      {
        if (!FileUtils.isValidExtFilename(paramString)) {
          throw new IllegalArgumentException("Invalid name: " + paramString);
        }
      }
      catch (ErrnoException paramString)
      {
        throw paramString.rethrowAsIOException();
      }
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      paramString = new File(resolveStageDir(), paramString);
      Binder.restoreCallingIdentity(l);
      ??? = Libcore.os.open(paramString.getAbsolutePath(), OsConstants.O_CREAT | OsConstants.O_WRONLY, 420);
      android.system.Os.chmod(paramString.getAbsolutePath(), 420);
      if (paramLong2 > 0L)
      {
        l = paramLong2 - Libcore.os.fstat((FileDescriptor)???).st_size;
        if ((this.stageDir != null) && (l > 0L)) {
          this.mPm.freeStorage(this.params.volumeUuid, l);
        }
        Libcore.os.posix_fallocate((FileDescriptor)???, 0L, paramLong2);
      }
      if (paramLong1 > 0L) {
        Libcore.os.lseek((FileDescriptor)???, paramLong1, OsConstants.SEEK_SET);
      }
      localFileBridge.setTargetFile((FileDescriptor)???);
      localFileBridge.start();
      return new ParcelFileDescriptor(localFileBridge.getClientSocket());
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private static void resizeContainer(String paramString, long paramLong)
    throws PackageManagerException
  {
    String str = PackageHelper.getSdDir(paramString);
    if (str == null) {
      throw new PackageManagerException(-18, "Failed to find mounted " + paramString);
    }
    long l = new File(str).getTotalSpace();
    if (l > paramLong)
    {
      Slog.w("PackageInstaller", "Current size " + l + " is larger than target size " + paramLong + "; skipping resize");
      return;
    }
    if (!PackageHelper.unMountSdDir(paramString)) {
      throw new PackageManagerException(-18, "Failed to unmount " + paramString + " before resize");
    }
    if (!PackageHelper.resizeSdDir(paramLong, paramString, PackageManagerService.getEncryptKey())) {
      throw new PackageManagerException(-18, "Failed to resize " + paramString + " to " + paramLong + " bytes");
    }
    if (PackageHelper.mountSdDir(paramString, PackageManagerService.getEncryptKey(), 1000, false) == null) {
      throw new PackageManagerException(-18, "Failed to mount " + paramString + " after resize");
    }
  }
  
  private File resolveStageDir()
    throws IOException
  {
    synchronized (this.mLock)
    {
      if (this.mResolvedStageDir == null)
      {
        if (this.stageDir != null) {
          this.mResolvedStageDir = this.stageDir;
        }
      }
      else
      {
        localObject2 = this.mResolvedStageDir;
        return (File)localObject2;
      }
      Object localObject2 = PackageHelper.getSdDir(this.stageCid);
      if (localObject2 != null) {
        this.mResolvedStageDir = new File((String)localObject2);
      }
    }
    throw new IOException("Failed to resolve path to container " + this.stageCid);
  }
  
  private void validateInstallLocked(PackageInfo paramPackageInfo, ApplicationInfo paramApplicationInfo)
    throws PackageManagerException
  {
    this.mPackageName = null;
    this.mVersionCode = -1;
    this.mSignatures = null;
    this.mResolvedBaseFile = null;
    this.mResolvedStagedFiles.clear();
    this.mResolvedInheritedFiles.clear();
    Object localObject1 = this.mResolvedStageDir.listFiles(sRemovedFilter);
    ArrayList localArrayList = new ArrayList();
    if (!ArrayUtils.isEmpty((Object[])localObject1))
    {
      i = 0;
      j = localObject1.length;
      while (i < j)
      {
        localObject2 = localObject1[i].getName();
        localArrayList.add(((String)localObject2).substring(0, ((String)localObject2).length() - ".removed".length()));
        i += 1;
      }
    }
    Object localObject3 = this.mResolvedStageDir.listFiles(sAddedFilter);
    if ((ArrayUtils.isEmpty((Object[])localObject3)) && (localArrayList.size() == 0)) {
      throw new PackageManagerException(-2, "No packages staged");
    }
    Object localObject2 = new ArraySet();
    int i = 0;
    int j = localObject3.length;
    while (i < j)
    {
      File localFile = localObject3[i];
      PackageParser.ApkLite localApkLite;
      try
      {
        localApkLite = PackageParser.parseApkLite(localFile, 256);
        if (!((ArraySet)localObject2).add(localApkLite.splitName)) {
          throw new PackageManagerException(-2, "Split " + localApkLite.splitName + " was defined multiple times");
        }
      }
      catch (PackageParser.PackageParserException paramPackageInfo)
      {
        throw PackageManagerException.from(paramPackageInfo);
      }
      if (this.mPackageName == null)
      {
        this.mPackageName = localApkLite.packageName;
        this.mVersionCode = localApkLite.versionCode;
      }
      if (this.mSignatures == null)
      {
        this.mSignatures = localApkLite.signatures;
        this.mCertificates = localApkLite.certificates;
      }
      assertApkConsistent(String.valueOf(localFile), localApkLite);
      if (localApkLite.splitName == null) {}
      for (localObject1 = "base.apk"; !FileUtils.isValidExtFilename((String)localObject1); localObject1 = "split_" + localApkLite.splitName + ".apk") {
        throw new PackageManagerException(-2, "Invalid filename: " + (String)localObject1);
      }
      localObject1 = new File(this.mResolvedStageDir, (String)localObject1);
      if (!localFile.equals(localObject1)) {
        localFile.renameTo((File)localObject1);
      }
      if (localApkLite.splitName == null) {
        this.mResolvedBaseFile = ((File)localObject1);
      }
      this.mResolvedStagedFiles.add(localObject1);
      i += 1;
    }
    if (localArrayList.size() > 0)
    {
      localObject1 = localArrayList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject3 = (String)((Iterator)localObject1).next();
        if (!ArrayUtils.contains(paramPackageInfo.splitNames, localObject3)) {
          throw new PackageManagerException(-2, "Split not found: " + (String)localObject3);
        }
      }
      if (this.mPackageName == null)
      {
        this.mPackageName = paramPackageInfo.packageName;
        this.mVersionCode = paramPackageInfo.versionCode;
      }
      if (this.mSignatures == null) {
        this.mSignatures = paramPackageInfo.signatures;
      }
    }
    if (this.params.mode == 1)
    {
      if (!((ArraySet)localObject2).contains(null)) {
        throw new PackageManagerException(-2, "Full install must include a base package");
      }
    }
    else
    {
      if (paramApplicationInfo == null) {
        throw new PackageManagerException(-2, "Missing existing base package for " + this.mPackageName);
      }
      for (;;)
      {
        try
        {
          paramPackageInfo = PackageParser.parsePackageLite(new File(paramApplicationInfo.getCodePath()), 0);
          localObject1 = PackageParser.parseApkLite(new File(paramApplicationInfo.getBaseCodePath()), 256);
          assertApkConsistent("Existing base", (PackageParser.ApkLite)localObject1);
          if (this.mResolvedBaseFile == null)
          {
            this.mResolvedBaseFile = new File(paramApplicationInfo.getBaseCodePath());
            this.mResolvedInheritedFiles.add(this.mResolvedBaseFile);
          }
          if (ArrayUtils.isEmpty(paramPackageInfo.splitNames)) {
            break;
          }
          i = 0;
          if (i >= paramPackageInfo.splitNames.length) {
            break;
          }
          localObject1 = paramPackageInfo.splitNames[i];
          localObject3 = new File(paramPackageInfo.splitCodePaths[i]);
          boolean bool = localArrayList.contains(localObject1);
          if ((((ArraySet)localObject2).contains(localObject1)) || (bool)) {
            i += 1;
          } else {
            this.mResolvedInheritedFiles.add(localObject3);
          }
        }
        catch (PackageParser.PackageParserException paramPackageInfo)
        {
          throw PackageManagerException.from(paramPackageInfo);
        }
      }
      paramPackageInfo = new File(paramApplicationInfo.getBaseCodePath()).getParentFile();
      this.mInheritedFilesBase = paramPackageInfo;
      paramPackageInfo = new File(paramPackageInfo, "oat");
      if (paramPackageInfo.exists())
      {
        paramPackageInfo = paramPackageInfo.listFiles();
        if ((paramPackageInfo != null) && (paramPackageInfo.length > 0))
        {
          paramApplicationInfo = InstructionSets.getAllDexCodeInstructionSets();
          i = 0;
          j = paramPackageInfo.length;
          if (i < j)
          {
            localObject1 = paramPackageInfo[i];
            if (!ArrayUtils.contains(paramApplicationInfo, ((File)localObject1).getName())) {}
            for (;;)
            {
              i += 1;
              break;
              this.mResolvedInstructionSets.add(((File)localObject1).getName());
              localObject1 = Arrays.asList(((File)localObject1).listFiles());
              if (!((List)localObject1).isEmpty()) {
                this.mResolvedInheritedFiles.addAll((Collection)localObject1);
              }
            }
          }
        }
      }
    }
  }
  
  public void abandon()
  {
    if (this.mRelinquished)
    {
      Slog.d("PackageInstaller", "Ignoring abandon after commit relinquished control");
      return;
    }
    destroyInternal();
    dispatchSessionFinished(-115, "Session was abandoned", null);
  }
  
  public void addClientProgress(float paramFloat)
  {
    synchronized (this.mLock)
    {
      setClientProgress(this.mClientProgress + paramFloat);
      return;
    }
  }
  
  public void close()
  {
    if (this.mActiveCount.decrementAndGet() == 0) {
      this.mCallback.onSessionActiveChanged(this, false);
    }
  }
  
  public void commit(IntentSender paramIntentSender)
  {
    Preconditions.checkNotNull(paramIntentSender);
    boolean bool;
    synchronized (this.mLock)
    {
      bool = this.mSealed;
      if (this.mSealed) {
        break label82;
      }
      Iterator localIterator = this.mBridges.iterator();
      while (localIterator.hasNext()) {
        if (!((FileBridge)localIterator.next()).isClosed()) {
          throw new SecurityException("Files still open");
        }
      }
    }
    this.mSealed = true;
    label82:
    this.mClientProgress = 1.0F;
    computeProgressLocked(true);
    if (!bool) {
      this.mCallback.onSessionSealedBlocking(this);
    }
    this.mActiveCount.incrementAndGet();
    paramIntentSender = new PackageInstallerService.PackageInstallObserverAdapter(this.mContext, paramIntentSender, this.sessionId, this.mIsInstallerDeviceOwner, this.userId);
    this.mHandler.obtainMessage(0, paramIntentSender.getBinder()).sendToTarget();
  }
  
  void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      dumpLocked(paramIndentingPrintWriter);
      return;
    }
  }
  
  public PackageInstaller.SessionInfo generateInfo()
  {
    String str = null;
    boolean bool = false;
    PackageInstaller.SessionInfo localSessionInfo = new PackageInstaller.SessionInfo();
    synchronized (this.mLock)
    {
      localSessionInfo.sessionId = this.sessionId;
      localSessionInfo.installerPackageName = this.installerPackageName;
      if (this.mResolvedBaseFile != null) {
        str = this.mResolvedBaseFile.getAbsolutePath();
      }
      localSessionInfo.resolvedBaseCodePath = str;
      localSessionInfo.progress = this.mProgress;
      localSessionInfo.sealed = this.mSealed;
      if (this.mActiveCount.get() > 0) {
        bool = true;
      }
      localSessionInfo.active = bool;
      localSessionInfo.mode = this.params.mode;
      localSessionInfo.sizeBytes = this.params.sizeBytes;
      localSessionInfo.appPackageName = this.params.appPackageName;
      localSessionInfo.appIcon = this.params.appIcon;
      localSessionInfo.appLabel = this.params.appLabel;
      return localSessionInfo;
    }
  }
  
  public String[] getNames()
  {
    assertPreparedAndNotSealed("getNames");
    try
    {
      String[] arrayOfString = resolveStageDir().list();
      return arrayOfString;
    }
    catch (IOException localIOException)
    {
      throw ExceptionUtils.wrap(localIOException);
    }
  }
  
  public boolean isPrepared()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mPrepared;
      return bool;
    }
  }
  
  public boolean isSealed()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mSealed;
      return bool;
    }
  }
  
  public void open()
    throws IOException
  {
    if (this.mActiveCount.getAndIncrement() == 0) {
      this.mCallback.onSessionActiveChanged(this, true);
    }
    for (;;)
    {
      long l;
      synchronized (this.mLock)
      {
        if (!this.mPrepared)
        {
          if (this.stageDir != null)
          {
            PackageInstallerService.prepareStageDir(this.stageDir);
            this.mPrepared = true;
            this.mCallback.onSessionPrepared(this);
          }
        }
        else {
          return;
        }
        if (this.stageCid == null) {
          break;
        }
        l = Binder.clearCallingIdentity();
      }
      try
      {
        PackageInstallerService.prepareExternalStageCid(this.stageCid, this.params.sizeBytes);
        Binder.restoreCallingIdentity(l);
        this.mInternalProgress = 0.25F;
        computeProgressLocked(true);
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    localObject2 = finally;
    throw ((Throwable)localObject2);
    throw new IllegalArgumentException("Exactly one of stageDir or stageCid stage must be set");
  }
  
  public ParcelFileDescriptor openRead(String paramString)
  {
    try
    {
      paramString = openReadInternal(paramString);
      return paramString;
    }
    catch (IOException paramString)
    {
      throw ExceptionUtils.wrap(paramString);
    }
  }
  
  public ParcelFileDescriptor openWrite(String paramString, long paramLong1, long paramLong2)
  {
    try
    {
      paramString = openWriteInternal(paramString, paramLong1, paramLong2);
      return paramString;
    }
    catch (IOException paramString)
    {
      throw ExceptionUtils.wrap(paramString);
    }
  }
  
  public void removeSplit(String paramString)
  {
    if (TextUtils.isEmpty(this.params.appPackageName)) {
      throw new IllegalStateException("Must specify package name to remove a split");
    }
    try
    {
      createRemoveSplitMarker(paramString);
      return;
    }
    catch (IOException paramString)
    {
      throw ExceptionUtils.wrap(paramString);
    }
  }
  
  public void setClientProgress(float paramFloat)
  {
    synchronized (this.mLock)
    {
      if (this.mClientProgress == 0.0F)
      {
        bool = true;
        this.mClientProgress = paramFloat;
        computeProgressLocked(bool);
        return;
      }
      boolean bool = false;
    }
  }
  
  void setPermissionsResult(boolean paramBoolean)
  {
    if (!this.mSealed) {
      throw new SecurityException("Must be sealed to accept permissions");
    }
    if (paramBoolean) {
      synchronized (this.mLock)
      {
        this.mPermissionsAccepted = true;
        this.mHandler.obtainMessage(0).sendToTarget();
        return;
      }
    }
    destroyInternal();
    dispatchSessionFinished(-115, "User rejected permissions", null);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageInstallerSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */