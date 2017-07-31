package com.android.server.pm;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PackageDeleteObserver;
import android.app.PackageInstallObserver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageInstaller.Stub;
import android.content.pm.IPackageInstallerCallback;
import android.content.pm.IPackageInstallerSession;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller.SessionInfo;
import android.content.pm.PackageInstaller.SessionParams;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.ExceptionUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageHelper;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.ImageUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.XmlUtils;
import com.android.server.IoThread;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class PackageInstallerService
  extends IPackageInstaller.Stub
{
  private static final String ATTR_ABI_OVERRIDE = "abiOverride";
  @Deprecated
  private static final String ATTR_APP_ICON = "appIcon";
  private static final String ATTR_APP_LABEL = "appLabel";
  private static final String ATTR_APP_PACKAGE_NAME = "appPackageName";
  private static final String ATTR_CREATED_MILLIS = "createdMillis";
  private static final String ATTR_INSTALLER_PACKAGE_NAME = "installerPackageName";
  private static final String ATTR_INSTALLER_UID = "installerUid";
  private static final String ATTR_INSTALL_FLAGS = "installFlags";
  private static final String ATTR_INSTALL_LOCATION = "installLocation";
  private static final String ATTR_MODE = "mode";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_ORIGINATING_UID = "originatingUid";
  private static final String ATTR_ORIGINATING_URI = "originatingUri";
  private static final String ATTR_PREPARED = "prepared";
  private static final String ATTR_REFERRER_URI = "referrerUri";
  private static final String ATTR_SEALED = "sealed";
  private static final String ATTR_SESSION_ID = "sessionId";
  private static final String ATTR_SESSION_STAGE_CID = "sessionStageCid";
  private static final String ATTR_SESSION_STAGE_DIR = "sessionStageDir";
  private static final String ATTR_SIZE_BYTES = "sizeBytes";
  private static final String ATTR_USER_ID = "userId";
  private static final String ATTR_VOLUME_UUID = "volumeUuid";
  private static final boolean LOGD = false;
  private static final long MAX_ACTIVE_SESSIONS = 1024L;
  private static final long MAX_AGE_MILLIS = 259200000L;
  private static final long MAX_HISTORICAL_SESSIONS = 1048576L;
  private static final String TAG = "PackageInstaller";
  private static final String TAG_GRANTED_RUNTIME_PERMISSION = "granted-runtime-permission";
  private static final String TAG_SESSION = "session";
  private static final String TAG_SESSIONS = "sessions";
  private static final FilenameFilter sStageFilter = new FilenameFilter()
  {
    public boolean accept(File paramAnonymousFile, String paramAnonymousString)
    {
      return PackageInstallerService.isStageName(paramAnonymousString);
    }
  };
  @GuardedBy("mSessions")
  private final SparseBooleanArray mAllocatedSessions = new SparseBooleanArray();
  private AppOpsManager mAppOps;
  private final Callbacks mCallbacks;
  private final Context mContext;
  @GuardedBy("mSessions")
  private final SparseArray<PackageInstallerSession> mHistoricalSessions = new SparseArray();
  private final Handler mInstallHandler;
  private final HandlerThread mInstallThread;
  private final InternalCallback mInternalCallback = new InternalCallback();
  @GuardedBy("mSessions")
  private final SparseBooleanArray mLegacySessions = new SparseBooleanArray();
  private final PackageManagerService mPm;
  private final Random mRandom = new SecureRandom();
  @GuardedBy("mSessions")
  private final SparseArray<PackageInstallerSession> mSessions = new SparseArray();
  private final File mSessionsDir;
  private final AtomicFile mSessionsFile;
  
  public PackageInstallerService(Context arg1, PackageManagerService paramPackageManagerService)
  {
    this.mContext = ???;
    this.mPm = paramPackageManagerService;
    this.mInstallThread = new HandlerThread("PackageInstaller");
    this.mInstallThread.start();
    this.mInstallHandler = new Handler(this.mInstallThread.getLooper());
    this.mCallbacks = new Callbacks(this.mInstallThread.getLooper());
    this.mSessionsFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "install_sessions.xml"));
    this.mSessionsDir = new File(Environment.getDataSystemDirectory(), "install_sessions");
    this.mSessionsDir.mkdirs();
    synchronized (this.mSessions)
    {
      readSessionsLocked();
      reconcileStagesLocked(StorageManager.UUID_PRIVATE_INTERNAL, false);
      reconcileStagesLocked(StorageManager.UUID_PRIVATE_INTERNAL, true);
      paramPackageManagerService = newArraySet(this.mSessionsDir.listFiles());
      int i = 0;
      while (i < this.mSessions.size())
      {
        paramPackageManagerService.remove(buildAppIconFile(((PackageInstallerSession)this.mSessions.valueAt(i)).sessionId));
        i += 1;
      }
      paramPackageManagerService = paramPackageManagerService.iterator();
      if (paramPackageManagerService.hasNext())
      {
        File localFile = (File)paramPackageManagerService.next();
        Slog.w("PackageInstaller", "Deleting orphan icon " + localFile);
        localFile.delete();
      }
    }
  }
  
  private int allocateSessionIdLocked()
  {
    int i = 0;
    for (;;)
    {
      int j = this.mRandom.nextInt(2147483646) + 1;
      if (!this.mAllocatedSessions.get(j, false))
      {
        this.mAllocatedSessions.put(j, true);
        return j;
      }
      if (i >= 32) {
        break;
      }
      i += 1;
    }
    throw new IllegalStateException("Failed to allocate session ID");
  }
  
  private File buildAppIconFile(int paramInt)
  {
    return new File(this.mSessionsDir, "app_icon." + paramInt + ".png");
  }
  
  private String buildExternalStageCid(int paramInt)
  {
    return "smdl" + paramInt + ".tmp";
  }
  
  private File buildStageDir(String paramString, int paramInt, boolean paramBoolean)
  {
    return new File(buildStagingDir(paramString, paramBoolean), "vmdl" + paramInt + ".tmp");
  }
  
  private File buildStagingDir(String paramString, boolean paramBoolean)
  {
    if (paramBoolean) {
      return Environment.getDataAppEphemeralDirectory(paramString);
    }
    return Environment.getDataAppDirectory(paramString);
  }
  
  private static Notification buildSuccessNotification(Context paramContext, String paramString1, String paramString2, int paramInt)
  {
    Object localObject1 = null;
    try
    {
      localObject2 = AppGlobals.getPackageManager().getPackageInfo(paramString2, 0, paramInt);
      localObject1 = localObject2;
    }
    catch (RemoteException localRemoteException)
    {
      Object localObject2;
      for (;;) {}
    }
    if ((localObject1 == null) || (((PackageInfo)localObject1).applicationInfo == null))
    {
      Slog.w("PackageInstaller", "Notification not built for package: " + paramString2);
      return null;
    }
    localObject2 = paramContext.getPackageManager();
    paramString2 = ImageUtils.buildScaledBitmap(((PackageInfo)localObject1).applicationInfo.loadIcon((PackageManager)localObject2), paramContext.getResources().getDimensionPixelSize(17104901), paramContext.getResources().getDimensionPixelSize(17104902));
    localObject1 = ((PackageInfo)localObject1).applicationInfo.loadLabel((PackageManager)localObject2);
    return new Notification.Builder(paramContext).setSmallIcon(17302278).setColor(paramContext.getResources().getColor(17170523)).setContentTitle((CharSequence)localObject1).setContentText(paramString1).setStyle(new Notification.BigTextStyle().bigText(paramString1)).setLargeIcon(paramString2).build();
  }
  
  private int createSessionInternal(PackageInstaller.SessionParams arg1, String paramString, int paramInt)
    throws IOException
  {
    int i = Binder.getCallingUid();
    this.mPm.enforceCrossUserPermission(i, paramInt, true, true, "createSession");
    if (this.mPm.isUserRestricted(paramInt, "no_install_apps")) {
      throw new SecurityException("User restriction prevents installing");
    }
    if ((i == 2000) || (i == 0)) {}
    for (???.installFlags |= 0x20; ((???.installFlags & 0x100) != 0) && (this.mContext.checkCallingOrSelfPermission("android.permission.INSTALL_GRANT_RUNTIME_PERMISSIONS") == -1); ???.installFlags |= 0x2)
    {
      throw new SecurityException("You need the android.permission.INSTALL_GRANT_RUNTIME_PERMISSIONS permission to use the PackageManager.INSTALL_GRANT_RUNTIME_PERMISSIONS flag");
      this.mAppOps.checkPackage(i, paramString);
      ???.installFlags &= 0xFFFFFFDF;
      ???.installFlags &= 0xFFFFFFBF;
    }
    if (???.appIcon != null)
    {
      j = ((ActivityManager)this.mContext.getSystemService("activity")).getLauncherLargeIconSize();
      if ((???.appIcon.getWidth() > j * 2) || (???.appIcon.getHeight() > j * 2)) {
        ???.appIcon = Bitmap.createScaledBitmap(???.appIcon, j, j, true);
      }
    }
    switch (???.mode)
    {
    default: 
      throw new IllegalArgumentException("Invalid install mode: " + ???.mode);
    }
    if ((???.installFlags & 0x10) != 0)
    {
      if (!PackageHelper.fitsOnInternal(this.mContext, ???.sizeBytes)) {
        throw new IOException("No suitable internal storage available");
      }
    }
    else if ((???.installFlags & 0x8) != 0)
    {
      if (!PackageHelper.fitsOnExternal(this.mContext, ???.sizeBytes)) {
        throw new IOException("No suitable external storage available");
      }
    }
    else
    {
      if ((???.installFlags & 0x200) == 0) {
        break label421;
      }
      ???.setInstallFlagsInternal();
    }
    for (;;)
    {
      synchronized (this.mSessions)
      {
        if (getSessionCount(this.mSessions, i) < 1024L) {
          break label469;
        }
        throw new IllegalStateException("Too many active sessions for UID " + i);
      }
      label421:
      ???.setInstallFlagsInternal();
      l = Binder.clearCallingIdentity();
      label469:
      try
      {
        ???.volumeUuid = PackageHelper.resolveInstallVolume(this.mContext, ???.appPackageName, ???.installLocation, ???.sizeBytes);
        Binder.restoreCallingIdentity(l);
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    throw new IllegalStateException("Too many historical sessions for UID " + i);
    int j = allocateSessionIdLocked();
    long l = System.currentTimeMillis();
    ??? = null;
    String str = null;
    boolean bool;
    if ((???.installFlags & 0x10) != 0) {
      if ((???.installFlags & 0x800) != 0) {
        bool = true;
      }
    }
    for (;;)
    {
      ??? = buildStageDir(???.volumeUuid, j, bool);
      paramString = new PackageInstallerSession(this.mInternalCallback, this.mContext, this.mPm, this.mInstallThread.getLooper(), j, paramInt, paramString, i, ???, l, (File)???, str, false, false);
      synchronized (this.mSessions)
      {
        this.mSessions.put(j, paramString);
        Callbacks.-wrap2(this.mCallbacks, paramString.sessionId, paramString.userId);
        writeSessionsAsync();
        return j;
        bool = false;
        continue;
        str = buildExternalStageCid(j);
      }
    }
  }
  
  private static int getSessionCount(SparseArray<PackageInstallerSession> paramSparseArray, int paramInt)
  {
    int j = 0;
    int m = paramSparseArray.size();
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (((PackageInstallerSession)paramSparseArray.valueAt(i)).installerUid == paramInt) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  private boolean isCallingUidOwner(PackageInstallerSession paramPackageInstallerSession)
  {
    int i = Binder.getCallingUid();
    if (i == 0) {
      return true;
    }
    return (paramPackageInstallerSession != null) && (i == paramPackageInstallerSession.installerUid);
  }
  
  public static boolean isStageName(String paramString)
  {
    boolean bool1;
    if (paramString.startsWith("vmdl"))
    {
      bool1 = paramString.endsWith(".tmp");
      if (!paramString.startsWith("smdl")) {
        break label59;
      }
    }
    label59:
    for (boolean bool2 = paramString.endsWith(".tmp");; bool2 = false)
    {
      boolean bool3 = paramString.startsWith("smdl2tmp");
      if ((bool1) || (bool2)) {
        break label64;
      }
      return bool3;
      bool1 = false;
      break;
    }
    label64:
    return true;
  }
  
  public static <E> ArraySet<E> newArraySet(E... paramVarArgs)
  {
    ArraySet localArraySet = new ArraySet();
    if (paramVarArgs != null)
    {
      localArraySet.ensureCapacity(paramVarArgs.length);
      Collections.addAll(localArraySet, paramVarArgs);
    }
    return localArraySet;
  }
  
  private IPackageInstallerSession openSessionInternal(int paramInt)
    throws IOException
  {
    synchronized (this.mSessions)
    {
      PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.get(paramInt);
      if ((localPackageInstallerSession != null) && (isCallingUidOwner(localPackageInstallerSession)))
      {
        localPackageInstallerSession.open();
        return localPackageInstallerSession;
      }
      throw new SecurityException("Caller has no access to session " + paramInt);
    }
  }
  
  static void prepareExternalStageCid(String paramString, long paramLong)
    throws IOException
  {
    if (PackageHelper.createSdDir(paramLong, paramString, PackageManagerService.getEncryptKey(), 1000, true) == null) {
      throw new IOException("Failed to create session cid: " + paramString);
    }
  }
  
  static void prepareStageDir(File paramFile)
    throws IOException
  {
    if (paramFile.exists()) {
      throw new IOException("Session dir already exists: " + paramFile);
    }
    try
    {
      Os.mkdir(paramFile.getAbsolutePath(), 493);
      Os.chmod(paramFile.getAbsolutePath(), 493);
      if (!SELinux.restorecon(paramFile)) {
        throw new IOException("Failed to restorecon session dir: " + paramFile);
      }
    }
    catch (ErrnoException localErrnoException)
    {
      throw new IOException("Failed to prepare session dir: " + paramFile, localErrnoException);
    }
  }
  
  private static String[] readGrantedRuntimePermissions(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    Object localObject1 = null;
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4) && ("granted-runtime-permission".equals(paramXmlPullParser.getName())))
      {
        String str = XmlUtils.readStringAttribute(paramXmlPullParser, "name");
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        ((List)localObject2).add(str);
        localObject1 = localObject2;
      }
    }
    if (localObject1 == null) {
      return null;
    }
    paramXmlPullParser = new String[((List)localObject1).size()];
    ((List)localObject1).toArray(paramXmlPullParser);
    return paramXmlPullParser;
  }
  
  private PackageInstallerSession readSessionLocked(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int i = XmlUtils.readIntAttribute(paramXmlPullParser, "sessionId");
    int j = XmlUtils.readIntAttribute(paramXmlPullParser, "userId");
    String str1 = XmlUtils.readStringAttribute(paramXmlPullParser, "installerPackageName");
    int k = XmlUtils.readIntAttribute(paramXmlPullParser, "installerUid", this.mPm.getPackageUid(str1, 8192, j));
    long l = XmlUtils.readLongAttribute(paramXmlPullParser, "createdMillis");
    Object localObject = XmlUtils.readStringAttribute(paramXmlPullParser, "sessionStageDir");
    if (localObject != null) {}
    for (localObject = new File((String)localObject);; localObject = null)
    {
      String str2 = XmlUtils.readStringAttribute(paramXmlPullParser, "sessionStageCid");
      boolean bool1 = XmlUtils.readBooleanAttribute(paramXmlPullParser, "prepared", true);
      boolean bool2 = XmlUtils.readBooleanAttribute(paramXmlPullParser, "sealed");
      PackageInstaller.SessionParams localSessionParams = new PackageInstaller.SessionParams(-1);
      localSessionParams.mode = XmlUtils.readIntAttribute(paramXmlPullParser, "mode");
      localSessionParams.installFlags = XmlUtils.readIntAttribute(paramXmlPullParser, "installFlags");
      localSessionParams.installLocation = XmlUtils.readIntAttribute(paramXmlPullParser, "installLocation");
      localSessionParams.sizeBytes = XmlUtils.readLongAttribute(paramXmlPullParser, "sizeBytes");
      localSessionParams.appPackageName = XmlUtils.readStringAttribute(paramXmlPullParser, "appPackageName");
      localSessionParams.appIcon = XmlUtils.readBitmapAttribute(paramXmlPullParser, "appIcon");
      localSessionParams.appLabel = XmlUtils.readStringAttribute(paramXmlPullParser, "appLabel");
      localSessionParams.originatingUri = XmlUtils.readUriAttribute(paramXmlPullParser, "originatingUri");
      localSessionParams.originatingUid = XmlUtils.readIntAttribute(paramXmlPullParser, "originatingUid", -1);
      localSessionParams.referrerUri = XmlUtils.readUriAttribute(paramXmlPullParser, "referrerUri");
      localSessionParams.abiOverride = XmlUtils.readStringAttribute(paramXmlPullParser, "abiOverride");
      localSessionParams.volumeUuid = XmlUtils.readStringAttribute(paramXmlPullParser, "volumeUuid");
      localSessionParams.grantedRuntimePermissions = readGrantedRuntimePermissions(paramXmlPullParser);
      paramXmlPullParser = buildAppIconFile(i);
      if (paramXmlPullParser.exists())
      {
        localSessionParams.appIcon = BitmapFactory.decodeFile(paramXmlPullParser.getAbsolutePath());
        localSessionParams.appIconLastModified = paramXmlPullParser.lastModified();
      }
      return new PackageInstallerSession(this.mInternalCallback, this.mContext, this.mPm, this.mInstallThread.getLooper(), i, j, str1, k, localSessionParams, l, (File)localObject, str2, bool1, bool2);
    }
  }
  
  private void readSessionsLocked()
  {
    this.mSessions.clear();
    Object localObject1 = null;
    localObject4 = null;
    localObject5 = null;
    for (;;)
    {
      try
      {
        localFileInputStream = this.mSessionsFile.openRead();
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        XmlPullParser localXmlPullParser = Xml.newPullParser();
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        localXmlPullParser.setInput(localFileInputStream, StandardCharsets.UTF_8.name());
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        i = localXmlPullParser.next();
        if (i == 1) {
          continue;
        }
        if (i != 2) {
          continue;
        }
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        if (!"session".equals(localXmlPullParser.getName())) {
          continue;
        }
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        localPackageInstallerSession = readSessionLocked(localXmlPullParser);
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        if (System.currentTimeMillis() - localPackageInstallerSession.createdMillis < 259200000L) {
          continue;
        }
        localObject5 = localFileInputStream;
        localObject1 = localFileInputStream;
        localObject4 = localFileInputStream;
        Slog.w("PackageInstaller", "Abandoning old session first created at " + localPackageInstallerSession.createdMillis);
        i = 0;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        FileInputStream localFileInputStream;
        PackageInstallerSession localPackageInstallerSession;
        return;
        int i = 1;
        continue;
        localObject5 = localFileInputStream;
        localObject2 = localFileInputStream;
        localObject4 = localFileInputStream;
        this.mHistoricalSessions.put(localPackageInstallerSession.sessionId, localPackageInstallerSession);
        continue;
      }
      catch (IOException|XmlPullParserException localIOException)
      {
        Object localObject2;
        localObject4 = localObject2;
        Slog.wtf("PackageInstaller", "Failed reading install sessions", localIOException);
        return;
        IoUtils.closeQuietly(localIOException);
        return;
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject4);
      }
      if (i == 0) {
        continue;
      }
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      localObject4 = localFileInputStream;
      this.mSessions.put(localPackageInstallerSession.sessionId, localPackageInstallerSession);
      localObject5 = localFileInputStream;
      localObject1 = localFileInputStream;
      localObject4 = localFileInputStream;
      this.mAllocatedSessions.put(localPackageInstallerSession.sessionId, true);
    }
  }
  
  private void reconcileStagesLocked(String arg1, boolean paramBoolean)
  {
    ??? = newArraySet(buildStagingDir(???, paramBoolean).listFiles(sStageFilter));
    int i = 0;
    while (i < this.mSessions.size())
    {
      ???.remove(((PackageInstallerSession)this.mSessions.valueAt(i)).stageDir);
      i += 1;
    }
    Iterator localIterator = ???.iterator();
    while (localIterator.hasNext())
    {
      File localFile = (File)localIterator.next();
      Slog.w("PackageInstaller", "Deleting orphan stage " + localFile);
      synchronized (this.mPm.mInstallLock)
      {
        this.mPm.removeCodePathLI(localFile);
      }
    }
  }
  
  private static void writeGrantedRuntimePermissions(XmlSerializer paramXmlSerializer, String[] paramArrayOfString)
    throws IOException
  {
    if (paramArrayOfString != null)
    {
      int i = 0;
      int j = paramArrayOfString.length;
      while (i < j)
      {
        String str = paramArrayOfString[i];
        paramXmlSerializer.startTag(null, "granted-runtime-permission");
        XmlUtils.writeStringAttribute(paramXmlSerializer, "name", str);
        paramXmlSerializer.endTag(null, "granted-runtime-permission");
        i += 1;
      }
    }
  }
  
  /* Error */
  private void writeSessionLocked(XmlSerializer paramXmlSerializer, PackageInstallerSession paramPackageInstallerSession)
    throws IOException
  {
    // Byte code:
    //   0: aload_2
    //   1: getfield 907	com/android/server/pm/PackageInstallerSession:params	Landroid/content/pm/PackageInstaller$SessionParams;
    //   4: astore 5
    //   6: aload_1
    //   7: aconst_null
    //   8: ldc 112
    //   10: invokeinterface 894 3 0
    //   15: pop
    //   16: aload_1
    //   17: ldc 75
    //   19: aload_2
    //   20: getfield 295	com/android/server/pm/PackageInstallerSession:sessionId	I
    //   23: invokestatic 911	com/android/internal/util/XmlUtils:writeIntAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;I)V
    //   26: aload_1
    //   27: ldc 87
    //   29: aload_2
    //   30: getfield 630	com/android/server/pm/PackageInstallerSession:userId	I
    //   33: invokestatic 911	com/android/internal/util/XmlUtils:writeIntAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;I)V
    //   36: aload_1
    //   37: ldc 42
    //   39: aload_2
    //   40: getfield 913	com/android/server/pm/PackageInstallerSession:installerPackageName	Ljava/lang/String;
    //   43: invokestatic 898	com/android/internal/util/XmlUtils:writeStringAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;)V
    //   46: aload_1
    //   47: ldc 45
    //   49: aload_2
    //   50: getfield 638	com/android/server/pm/PackageInstallerSession:installerUid	I
    //   53: invokestatic 911	com/android/internal/util/XmlUtils:writeIntAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;I)V
    //   56: aload_1
    //   57: ldc 39
    //   59: aload_2
    //   60: getfield 854	com/android/server/pm/PackageInstallerSession:createdMillis	J
    //   63: invokestatic 917	com/android/internal/util/XmlUtils:writeLongAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;J)V
    //   66: aload_2
    //   67: getfield 877	com/android/server/pm/PackageInstallerSession:stageDir	Ljava/io/File;
    //   70: ifnull +16 -> 86
    //   73: aload_1
    //   74: ldc 81
    //   76: aload_2
    //   77: getfield 877	com/android/server/pm/PackageInstallerSession:stageDir	Ljava/io/File;
    //   80: invokevirtual 699	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   83: invokestatic 898	com/android/internal/util/XmlUtils:writeStringAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;)V
    //   86: aload_2
    //   87: getfield 920	com/android/server/pm/PackageInstallerSession:stageCid	Ljava/lang/String;
    //   90: ifnull +13 -> 103
    //   93: aload_1
    //   94: ldc 78
    //   96: aload_2
    //   97: getfield 920	com/android/server/pm/PackageInstallerSession:stageCid	Ljava/lang/String;
    //   100: invokestatic 898	com/android/internal/util/XmlUtils:writeStringAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;)V
    //   103: aload_1
    //   104: ldc 66
    //   106: aload_2
    //   107: invokevirtual 923	com/android/server/pm/PackageInstallerSession:isPrepared	()Z
    //   110: invokestatic 927	com/android/internal/util/XmlUtils:writeBooleanAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Z)V
    //   113: aload_1
    //   114: ldc 72
    //   116: aload_2
    //   117: invokevirtual 930	com/android/server/pm/PackageInstallerSession:isSealed	()Z
    //   120: invokestatic 927	com/android/internal/util/XmlUtils:writeBooleanAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Z)V
    //   123: aload_1
    //   124: ldc 54
    //   126: aload 5
    //   128: getfield 563	android/content/pm/PackageInstaller$SessionParams:mode	I
    //   131: invokestatic 911	com/android/internal/util/XmlUtils:writeIntAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;I)V
    //   134: aload_1
    //   135: ldc 48
    //   137: aload 5
    //   139: getfield 519	android/content/pm/PackageInstaller$SessionParams:installFlags	I
    //   142: invokestatic 911	com/android/internal/util/XmlUtils:writeIntAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;I)V
    //   145: aload_1
    //   146: ldc 51
    //   148: aload 5
    //   150: getfield 601	android/content/pm/PackageInstaller$SessionParams:installLocation	I
    //   153: invokestatic 911	com/android/internal/util/XmlUtils:writeIntAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;I)V
    //   156: aload_1
    //   157: ldc 84
    //   159: aload 5
    //   161: getfield 570	android/content/pm/PackageInstaller$SessionParams:sizeBytes	J
    //   164: invokestatic 917	com/android/internal/util/XmlUtils:writeLongAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;J)V
    //   167: aload_1
    //   168: ldc 36
    //   170: aload 5
    //   172: getfield 599	android/content/pm/PackageInstaller$SessionParams:appPackageName	Ljava/lang/String;
    //   175: invokestatic 898	com/android/internal/util/XmlUtils:writeStringAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;)V
    //   178: aload_1
    //   179: ldc 33
    //   181: aload 5
    //   183: getfield 790	android/content/pm/PackageInstaller$SessionParams:appLabel	Ljava/lang/String;
    //   186: invokestatic 898	com/android/internal/util/XmlUtils:writeStringAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;)V
    //   189: aload_1
    //   190: ldc 63
    //   192: aload 5
    //   194: getfield 797	android/content/pm/PackageInstaller$SessionParams:originatingUri	Landroid/net/Uri;
    //   197: invokestatic 934	com/android/internal/util/XmlUtils:writeUriAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Landroid/net/Uri;)V
    //   200: aload_1
    //   201: ldc 60
    //   203: aload 5
    //   205: getfield 799	android/content/pm/PackageInstaller$SessionParams:originatingUid	I
    //   208: invokestatic 911	com/android/internal/util/XmlUtils:writeIntAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;I)V
    //   211: aload_1
    //   212: ldc 69
    //   214: aload 5
    //   216: getfield 801	android/content/pm/PackageInstaller$SessionParams:referrerUri	Landroid/net/Uri;
    //   219: invokestatic 934	com/android/internal/util/XmlUtils:writeUriAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Landroid/net/Uri;)V
    //   222: aload_1
    //   223: ldc 26
    //   225: aload 5
    //   227: getfield 803	android/content/pm/PackageInstaller$SessionParams:abiOverride	Ljava/lang/String;
    //   230: invokestatic 898	com/android/internal/util/XmlUtils:writeStringAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;)V
    //   233: aload_1
    //   234: ldc 90
    //   236: aload 5
    //   238: getfield 607	android/content/pm/PackageInstaller$SessionParams:volumeUuid	Ljava/lang/String;
    //   241: invokestatic 898	com/android/internal/util/XmlUtils:writeStringAttribute	(Lorg/xmlpull/v1/XmlSerializer;Ljava/lang/String;Ljava/lang/String;)V
    //   244: aload_0
    //   245: aload_2
    //   246: getfield 295	com/android/server/pm/PackageInstallerSession:sessionId	I
    //   249: invokespecial 173	com/android/server/pm/PackageInstallerService:buildAppIconFile	(I)Ljava/io/File;
    //   252: astore 6
    //   254: aload 5
    //   256: getfield 538	android/content/pm/PackageInstaller$SessionParams:appIcon	Landroid/graphics/Bitmap;
    //   259: ifnonnull +37 -> 296
    //   262: aload 6
    //   264: invokevirtual 694	java/io/File:exists	()Z
    //   267: ifeq +29 -> 296
    //   270: aload 6
    //   272: invokevirtual 341	java/io/File:delete	()Z
    //   275: pop
    //   276: aload_1
    //   277: aload 5
    //   279: getfield 809	android/content/pm/PackageInstaller$SessionParams:grantedRuntimePermissions	[Ljava/lang/String;
    //   282: invokestatic 936	com/android/server/pm/PackageInstallerService:writeGrantedRuntimePermissions	(Lorg/xmlpull/v1/XmlSerializer;[Ljava/lang/String;)V
    //   285: aload_1
    //   286: aconst_null
    //   287: ldc 112
    //   289: invokeinterface 901 3 0
    //   294: pop
    //   295: return
    //   296: aload 5
    //   298: getfield 538	android/content/pm/PackageInstaller$SessionParams:appIcon	Landroid/graphics/Bitmap;
    //   301: ifnull -25 -> 276
    //   304: aload 6
    //   306: invokevirtual 818	java/io/File:lastModified	()J
    //   309: aload 5
    //   311: getfield 821	android/content/pm/PackageInstaller$SessionParams:appIconLastModified	J
    //   314: lcmp
    //   315: ifeq -39 -> 276
    //   318: aconst_null
    //   319: astore_2
    //   320: aconst_null
    //   321: astore 4
    //   323: new 938	java/io/FileOutputStream
    //   326: dup
    //   327: aload 6
    //   329: invokespecial 939	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   332: astore_3
    //   333: aload 5
    //   335: getfield 538	android/content/pm/PackageInstaller$SessionParams:appIcon	Landroid/graphics/Bitmap;
    //   338: getstatic 945	android/graphics/Bitmap$CompressFormat:PNG	Landroid/graphics/Bitmap$CompressFormat;
    //   341: bipush 90
    //   343: aload_3
    //   344: invokevirtual 949	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   347: pop
    //   348: aload_3
    //   349: invokestatic 865	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   352: aload 5
    //   354: aload 6
    //   356: invokevirtual 818	java/io/File:lastModified	()J
    //   359: putfield 821	android/content/pm/PackageInstaller$SessionParams:appIconLastModified	J
    //   362: goto -86 -> 276
    //   365: astore_2
    //   366: aload 4
    //   368: astore_3
    //   369: aload_2
    //   370: astore 4
    //   372: aload_3
    //   373: astore_2
    //   374: ldc 106
    //   376: new 318	java/lang/StringBuilder
    //   379: dup
    //   380: invokespecial 319	java/lang/StringBuilder:<init>	()V
    //   383: ldc_w 951
    //   386: invokevirtual 325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   389: aload 6
    //   391: invokevirtual 328	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   394: ldc_w 953
    //   397: invokevirtual 325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   400: aload 4
    //   402: invokevirtual 956	java/io/IOException:getMessage	()Ljava/lang/String;
    //   405: invokevirtual 325	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   408: invokevirtual 332	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   411: invokestatic 338	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   414: pop
    //   415: aload_3
    //   416: invokestatic 865	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   419: goto -67 -> 352
    //   422: astore_1
    //   423: aload_2
    //   424: invokestatic 865	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   427: aload_1
    //   428: athrow
    //   429: astore_1
    //   430: aload_3
    //   431: astore_2
    //   432: goto -9 -> 423
    //   435: astore 4
    //   437: goto -65 -> 372
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	440	0	this	PackageInstallerService
    //   0	440	1	paramXmlSerializer	XmlSerializer
    //   0	440	2	paramPackageInstallerSession	PackageInstallerSession
    //   332	99	3	localObject	Object
    //   321	80	4	localPackageInstallerSession	PackageInstallerSession
    //   435	1	4	localIOException	IOException
    //   4	349	5	localSessionParams	PackageInstaller.SessionParams
    //   252	138	6	localFile	File
    // Exception table:
    //   from	to	target	type
    //   323	333	365	java/io/IOException
    //   323	333	422	finally
    //   374	415	422	finally
    //   333	348	429	finally
    //   333	348	435	java/io/IOException
  }
  
  private void writeSessionsAsync()
  {
    IoThread.getHandler().post(new Runnable()
    {
      public void run()
      {
        synchronized (PackageInstallerService.-get3(PackageInstallerService.this))
        {
          PackageInstallerService.-wrap3(PackageInstallerService.this);
          return;
        }
      }
    });
  }
  
  private void writeSessionsLocked()
  {
    Object localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = this.mSessionsFile.startWrite();
      localObject = localFileOutputStream;
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localObject = localFileOutputStream;
      localFastXmlSerializer.setOutput(localFileOutputStream, StandardCharsets.UTF_8.name());
      localObject = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "sessions");
      localObject = localFileOutputStream;
      int j = this.mSessions.size();
      int i = 0;
      while (i < j)
      {
        localObject = localFileOutputStream;
        writeSessionLocked(localFastXmlSerializer, (PackageInstallerSession)this.mSessions.valueAt(i));
        i += 1;
      }
      localObject = localFileOutputStream;
      localFastXmlSerializer.endTag(null, "sessions");
      localObject = localFileOutputStream;
      localFastXmlSerializer.endDocument();
      localObject = localFileOutputStream;
      this.mSessionsFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (IOException localIOException)
    {
      while (localObject == null) {}
      this.mSessionsFile.failWrite((FileOutputStream)localObject);
    }
  }
  
  public void abandonSession(int paramInt)
  {
    synchronized (this.mSessions)
    {
      PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.get(paramInt);
      if ((localPackageInstallerSession != null) && (isCallingUidOwner(localPackageInstallerSession)))
      {
        localPackageInstallerSession.abandon();
        return;
      }
      throw new SecurityException("Caller has no access to session " + paramInt);
    }
  }
  
  @Deprecated
  public String allocateExternalStageCidLegacy()
  {
    synchronized (this.mSessions)
    {
      int i = allocateSessionIdLocked();
      this.mLegacySessions.put(i, true);
      String str = "smdl" + i + ".tmp";
      return str;
    }
  }
  
  @Deprecated
  public File allocateStageDirLegacy(String paramString, boolean paramBoolean)
    throws IOException
  {
    synchronized (this.mSessions)
    {
      try
      {
        int i = allocateSessionIdLocked();
        this.mLegacySessions.put(i, true);
        paramString = buildStageDir(paramString, i, paramBoolean);
        prepareStageDir(paramString);
        return paramString;
      }
      catch (IllegalStateException paramString)
      {
        throw new IOException(paramString);
      }
    }
  }
  
  public int createSession(PackageInstaller.SessionParams paramSessionParams, String paramString, int paramInt)
  {
    try
    {
      paramInt = createSessionInternal(paramSessionParams, paramString, paramInt);
      return paramInt;
    }
    catch (IOException paramSessionParams)
    {
      throw ExceptionUtils.wrap(paramSessionParams);
    }
  }
  
  void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mSessions)
    {
      paramIndentingPrintWriter.println("Active install sessions:");
      paramIndentingPrintWriter.increaseIndent();
      int j = this.mSessions.size();
      int i = 0;
      while (i < j)
      {
        ((PackageInstallerSession)this.mSessions.valueAt(i)).dump(paramIndentingPrintWriter);
        paramIndentingPrintWriter.println();
        i += 1;
      }
      paramIndentingPrintWriter.println();
      paramIndentingPrintWriter.decreaseIndent();
      paramIndentingPrintWriter.println("Historical install sessions:");
      paramIndentingPrintWriter.increaseIndent();
      j = this.mHistoricalSessions.size();
      i = 0;
      while (i < j)
      {
        ((PackageInstallerSession)this.mHistoricalSessions.valueAt(i)).dump(paramIndentingPrintWriter);
        paramIndentingPrintWriter.println();
        i += 1;
      }
      paramIndentingPrintWriter.println();
      paramIndentingPrintWriter.decreaseIndent();
      paramIndentingPrintWriter.println("Legacy install sessions:");
      paramIndentingPrintWriter.increaseIndent();
      paramIndentingPrintWriter.println(this.mLegacySessions.toString());
      paramIndentingPrintWriter.decreaseIndent();
      return;
    }
  }
  
  public ParceledListSlice<PackageInstaller.SessionInfo> getAllSessions(int paramInt)
  {
    this.mPm.enforceCrossUserPermission(Binder.getCallingUid(), paramInt, true, false, "getAllSessions");
    ArrayList localArrayList = new ArrayList();
    SparseArray localSparseArray = this.mSessions;
    int i = 0;
    try
    {
      while (i < this.mSessions.size())
      {
        PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.valueAt(i);
        if (localPackageInstallerSession.userId == paramInt) {
          localArrayList.add(localPackageInstallerSession.generateInfo());
        }
        i += 1;
      }
      return new ParceledListSlice(localArrayList);
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public ParceledListSlice<PackageInstaller.SessionInfo> getMySessions(String paramString, int paramInt)
  {
    this.mPm.enforceCrossUserPermission(Binder.getCallingUid(), paramInt, true, false, "getMySessions");
    this.mAppOps.checkPackage(Binder.getCallingUid(), paramString);
    ArrayList localArrayList = new ArrayList();
    SparseArray localSparseArray = this.mSessions;
    int i = 0;
    try
    {
      while (i < this.mSessions.size())
      {
        PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.valueAt(i);
        if ((Objects.equals(localPackageInstallerSession.installerPackageName, paramString)) && (localPackageInstallerSession.userId == paramInt)) {
          localArrayList.add(localPackageInstallerSession.generateInfo());
        }
        i += 1;
      }
      return new ParceledListSlice(localArrayList);
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public PackageInstaller.SessionInfo getSessionInfo(int paramInt)
  {
    PackageInstaller.SessionInfo localSessionInfo = null;
    synchronized (this.mSessions)
    {
      PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.get(paramInt);
      if (localPackageInstallerSession != null) {
        localSessionInfo = localPackageInstallerSession.generateInfo();
      }
      return localSessionInfo;
    }
  }
  
  public void onPrivateVolumeMounted(String paramString)
  {
    synchronized (this.mSessions)
    {
      reconcileStagesLocked(paramString, false);
      return;
    }
  }
  
  public void onSecureContainersAvailable()
  {
    for (;;)
    {
      synchronized (this.mSessions)
      {
        Object localObject1 = new ArraySet();
        Object localObject3 = PackageHelper.getSecureContainerList();
        i = 0;
        int j = localObject3.length;
        if (i >= j) {
          break label193;
        }
        String str = localObject3[i];
        if (!isStageName(str)) {
          break label186;
        }
        ((ArraySet)localObject1).add(str);
        break label186;
        if (i < this.mSessions.size())
        {
          localObject3 = ((PackageInstallerSession)this.mSessions.valueAt(i)).stageCid;
          if (!((ArraySet)localObject1).remove(localObject3)) {
            break label198;
          }
          PackageHelper.mountSdDir((String)localObject3, PackageManagerService.getEncryptKey(), 1000);
          break label198;
        }
        localObject1 = ((Iterable)localObject1).iterator();
        if (((Iterator)localObject1).hasNext())
        {
          localObject3 = (String)((Iterator)localObject1).next();
          Slog.w("PackageInstaller", "Deleting orphan container " + (String)localObject3);
          PackageHelper.destroySdDir((String)localObject3);
        }
      }
      return;
      label186:
      i += 1;
      continue;
      label193:
      int i = 0;
      continue;
      label198:
      i += 1;
    }
  }
  
  public IPackageInstallerSession openSession(int paramInt)
  {
    try
    {
      IPackageInstallerSession localIPackageInstallerSession = openSessionInternal(paramInt);
      return localIPackageInstallerSession;
    }
    catch (IOException localIOException)
    {
      throw ExceptionUtils.wrap(localIOException);
    }
  }
  
  public void registerCallback(IPackageInstallerCallback paramIPackageInstallerCallback, int paramInt)
  {
    this.mPm.enforceCrossUserPermission(Binder.getCallingUid(), paramInt, true, false, "registerCallback");
    this.mCallbacks.register(paramIPackageInstallerCallback, paramInt);
  }
  
  public void setPermissionsResult(int paramInt, boolean paramBoolean)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.INSTALL_PACKAGES", "PackageInstaller");
    synchronized (this.mSessions)
    {
      PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.get(paramInt);
      if (localPackageInstallerSession != null) {
        localPackageInstallerSession.setPermissionsResult(paramBoolean);
      }
      return;
    }
  }
  
  public void systemReady()
  {
    this.mAppOps = ((AppOpsManager)this.mContext.getSystemService(AppOpsManager.class));
  }
  
  public void uninstall(String paramString1, String paramString2, int paramInt1, IntentSender paramIntentSender, int paramInt2)
  {
    int i = Binder.getCallingUid();
    this.mPm.enforceCrossUserPermission(i, paramInt2, true, true, "uninstall");
    if ((i != 2000) && (i != 0)) {
      this.mAppOps.checkPackage(i, paramString2);
    }
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)this.mContext.getSystemService("device_policy");
    if (localDevicePolicyManager != null) {}
    for (boolean bool = localDevicePolicyManager.isDeviceOwnerAppOnCallingUser(paramString2);; bool = false)
    {
      paramString2 = new PackageDeleteObserverAdapter(this.mContext, paramIntentSender, paramString1, bool, paramInt2);
      if (this.mContext.checkCallingOrSelfPermission("android.permission.DELETE_PACKAGES") != 0) {
        break;
      }
      this.mPm.deletePackage(paramString1, paramString2.getBinder(), paramInt2, paramInt1);
      return;
    }
    if (bool)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        this.mPm.deletePackage(paramString1, paramString2.getBinder(), paramInt2, paramInt1);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    paramIntentSender = new Intent("android.intent.action.UNINSTALL_PACKAGE");
    paramIntentSender.setData(Uri.fromParts("package", paramString1, null));
    paramIntentSender.putExtra("android.content.pm.extra.CALLBACK", paramString2.getBinder().asBinder());
    paramString2.onUserActionRequired(paramIntentSender);
  }
  
  public void unregisterCallback(IPackageInstallerCallback paramIPackageInstallerCallback)
  {
    this.mCallbacks.unregister(paramIPackageInstallerCallback);
  }
  
  public void updateSessionAppIcon(int paramInt, Bitmap paramBitmap)
  {
    synchronized (this.mSessions)
    {
      PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.get(paramInt);
      if ((localPackageInstallerSession != null) && (isCallingUidOwner(localPackageInstallerSession)))
      {
        Bitmap localBitmap = paramBitmap;
        if (paramBitmap != null)
        {
          paramInt = ((ActivityManager)this.mContext.getSystemService("activity")).getLauncherLargeIconSize();
          if (paramBitmap.getWidth() <= paramInt * 2)
          {
            localBitmap = paramBitmap;
            if (paramBitmap.getHeight() <= paramInt * 2) {}
          }
          else
          {
            localBitmap = Bitmap.createScaledBitmap(paramBitmap, paramInt, paramInt, true);
          }
        }
        localPackageInstallerSession.params.appIcon = localBitmap;
        localPackageInstallerSession.params.appIconLastModified = -1L;
        this.mInternalCallback.onSessionBadgingChanged(localPackageInstallerSession);
        return;
      }
      throw new SecurityException("Caller has no access to session " + paramInt);
    }
  }
  
  public void updateSessionAppLabel(int paramInt, String paramString)
  {
    synchronized (this.mSessions)
    {
      PackageInstallerSession localPackageInstallerSession = (PackageInstallerSession)this.mSessions.get(paramInt);
      if ((localPackageInstallerSession != null) && (isCallingUidOwner(localPackageInstallerSession)))
      {
        localPackageInstallerSession.params.appLabel = paramString;
        this.mInternalCallback.onSessionBadgingChanged(localPackageInstallerSession);
        return;
      }
      throw new SecurityException("Caller has no access to session " + paramInt);
    }
  }
  
  private static class Callbacks
    extends Handler
  {
    private static final int MSG_SESSION_ACTIVE_CHANGED = 3;
    private static final int MSG_SESSION_BADGING_CHANGED = 2;
    private static final int MSG_SESSION_CREATED = 1;
    private static final int MSG_SESSION_FINISHED = 5;
    private static final int MSG_SESSION_PROGRESS_CHANGED = 4;
    private final RemoteCallbackList<IPackageInstallerCallback> mCallbacks = new RemoteCallbackList();
    
    public Callbacks(Looper paramLooper)
    {
      super();
    }
    
    private void invokeCallback(IPackageInstallerCallback paramIPackageInstallerCallback, Message paramMessage)
      throws RemoteException
    {
      int i = paramMessage.arg1;
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        paramIPackageInstallerCallback.onSessionCreated(i);
        return;
      case 2: 
        paramIPackageInstallerCallback.onSessionBadgingChanged(i);
        return;
      case 3: 
        paramIPackageInstallerCallback.onSessionActiveChanged(i, ((Boolean)paramMessage.obj).booleanValue());
        return;
      case 4: 
        paramIPackageInstallerCallback.onSessionProgressChanged(i, ((Float)paramMessage.obj).floatValue());
        return;
      }
      paramIPackageInstallerCallback.onSessionFinished(i, ((Boolean)paramMessage.obj).booleanValue());
    }
    
    private void notifySessionActiveChanged(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      obtainMessage(3, paramInt1, paramInt2, Boolean.valueOf(paramBoolean)).sendToTarget();
    }
    
    private void notifySessionBadgingChanged(int paramInt1, int paramInt2)
    {
      obtainMessage(2, paramInt1, paramInt2).sendToTarget();
    }
    
    private void notifySessionCreated(int paramInt1, int paramInt2)
    {
      obtainMessage(1, paramInt1, paramInt2).sendToTarget();
    }
    
    private void notifySessionProgressChanged(int paramInt1, int paramInt2, float paramFloat)
    {
      obtainMessage(4, paramInt1, paramInt2, Float.valueOf(paramFloat)).sendToTarget();
    }
    
    public void handleMessage(Message paramMessage)
    {
      int j = paramMessage.arg2;
      int k = this.mCallbacks.beginBroadcast();
      int i = 0;
      for (;;)
      {
        if (i < k)
        {
          IPackageInstallerCallback localIPackageInstallerCallback = (IPackageInstallerCallback)this.mCallbacks.getBroadcastItem(i);
          if (j == ((UserHandle)this.mCallbacks.getBroadcastCookie(i)).getIdentifier()) {}
          try
          {
            invokeCallback(localIPackageInstallerCallback, paramMessage);
            i += 1;
          }
          catch (RemoteException localRemoteException)
          {
            for (;;) {}
          }
        }
      }
      this.mCallbacks.finishBroadcast();
    }
    
    public void notifySessionFinished(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      obtainMessage(5, paramInt1, paramInt2, Boolean.valueOf(paramBoolean)).sendToTarget();
    }
    
    public void register(IPackageInstallerCallback paramIPackageInstallerCallback, int paramInt)
    {
      this.mCallbacks.register(paramIPackageInstallerCallback, new UserHandle(paramInt));
    }
    
    public void unregister(IPackageInstallerCallback paramIPackageInstallerCallback)
    {
      this.mCallbacks.unregister(paramIPackageInstallerCallback);
    }
  }
  
  class InternalCallback
  {
    InternalCallback() {}
    
    public void onSessionActiveChanged(PackageInstallerSession paramPackageInstallerSession, boolean paramBoolean)
    {
      PackageInstallerService.Callbacks.-wrap0(PackageInstallerService.-get0(PackageInstallerService.this), paramPackageInstallerSession.sessionId, paramPackageInstallerSession.userId, paramBoolean);
    }
    
    public void onSessionBadgingChanged(PackageInstallerSession paramPackageInstallerSession)
    {
      PackageInstallerService.Callbacks.-wrap1(PackageInstallerService.-get0(PackageInstallerService.this), paramPackageInstallerSession.sessionId, paramPackageInstallerSession.userId);
      PackageInstallerService.-wrap2(PackageInstallerService.this);
    }
    
    public void onSessionFinished(final PackageInstallerSession paramPackageInstallerSession, boolean paramBoolean)
    {
      PackageInstallerService.-get0(PackageInstallerService.this).notifySessionFinished(paramPackageInstallerSession.sessionId, paramPackageInstallerSession.userId, paramBoolean);
      PackageInstallerService.-get2(PackageInstallerService.this).post(new Runnable()
      {
        public void run()
        {
          synchronized (PackageInstallerService.-get3(PackageInstallerService.this))
          {
            PackageInstallerService.-get3(PackageInstallerService.this).remove(paramPackageInstallerSession.sessionId);
            PackageInstallerService.-get1(PackageInstallerService.this).put(paramPackageInstallerSession.sessionId, paramPackageInstallerSession);
            File localFile = PackageInstallerService.-wrap1(PackageInstallerService.this, paramPackageInstallerSession.sessionId);
            if (localFile.exists()) {
              localFile.delete();
            }
            PackageInstallerService.-wrap3(PackageInstallerService.this);
            return;
          }
        }
      });
    }
    
    public void onSessionPrepared(PackageInstallerSession paramPackageInstallerSession)
    {
      PackageInstallerService.-wrap2(PackageInstallerService.this);
    }
    
    public void onSessionProgressChanged(PackageInstallerSession paramPackageInstallerSession, float paramFloat)
    {
      PackageInstallerService.Callbacks.-wrap3(PackageInstallerService.-get0(PackageInstallerService.this), paramPackageInstallerSession.sessionId, paramPackageInstallerSession.userId, paramFloat);
    }
    
    public void onSessionSealedBlocking(PackageInstallerSession arg1)
    {
      synchronized (PackageInstallerService.-get3(PackageInstallerService.this))
      {
        PackageInstallerService.-wrap3(PackageInstallerService.this);
        return;
      }
    }
  }
  
  static class PackageDeleteObserverAdapter
    extends PackageDeleteObserver
  {
    private final Context mContext;
    private final Notification mNotification;
    private final String mPackageName;
    private final IntentSender mTarget;
    
    public PackageDeleteObserverAdapter(Context paramContext, IntentSender paramIntentSender, String paramString, boolean paramBoolean, int paramInt)
    {
      this.mContext = paramContext;
      this.mTarget = paramIntentSender;
      this.mPackageName = paramString;
      if (paramBoolean)
      {
        this.mNotification = PackageInstallerService.-wrap0(this.mContext, this.mContext.getResources().getString(17040844), paramString, paramInt);
        return;
      }
      this.mNotification = null;
    }
    
    public void onPackageDeleted(String paramString1, int paramInt, String paramString2)
    {
      if ((1 == paramInt) && (this.mNotification != null)) {
        ((NotificationManager)this.mContext.getSystemService("notification")).notify(paramString1, 0, this.mNotification);
      }
      paramString1 = new Intent();
      paramString1.putExtra("android.content.pm.extra.PACKAGE_NAME", this.mPackageName);
      paramString1.putExtra("android.content.pm.extra.STATUS", PackageManager.deleteStatusToPublicStatus(paramInt));
      paramString1.putExtra("android.content.pm.extra.STATUS_MESSAGE", PackageManager.deleteStatusToString(paramInt, paramString2));
      paramString1.putExtra("android.content.pm.extra.LEGACY_STATUS", paramInt);
      try
      {
        this.mTarget.sendIntent(this.mContext, 0, paramString1, null, null);
        return;
      }
      catch (IntentSender.SendIntentException paramString1) {}
    }
    
    public void onUserActionRequired(Intent paramIntent)
    {
      Intent localIntent = new Intent();
      localIntent.putExtra("android.content.pm.extra.PACKAGE_NAME", this.mPackageName);
      localIntent.putExtra("android.content.pm.extra.STATUS", -1);
      localIntent.putExtra("android.intent.extra.INTENT", paramIntent);
      try
      {
        this.mTarget.sendIntent(this.mContext, 0, localIntent, null, null);
        return;
      }
      catch (IntentSender.SendIntentException paramIntent) {}
    }
  }
  
  static class PackageInstallObserverAdapter
    extends PackageInstallObserver
  {
    private final Context mContext;
    private final int mSessionId;
    private final boolean mShowNotification;
    private final IntentSender mTarget;
    private final int mUserId;
    
    public PackageInstallObserverAdapter(Context paramContext, IntentSender paramIntentSender, int paramInt1, boolean paramBoolean, int paramInt2)
    {
      this.mContext = paramContext;
      this.mTarget = paramIntentSender;
      this.mSessionId = paramInt1;
      this.mShowNotification = paramBoolean;
      this.mUserId = paramInt2;
    }
    
    public void onPackageInstalled(String paramString1, int paramInt, String paramString2, Bundle paramBundle)
    {
      boolean bool;
      Object localObject;
      Resources localResources;
      if ((1 == paramInt) && (this.mShowNotification))
      {
        if (paramBundle == null) {
          break label203;
        }
        bool = paramBundle.getBoolean("android.intent.extra.REPLACING");
        localObject = this.mContext;
        localResources = this.mContext.getResources();
        if (!bool) {
          break label209;
        }
      }
      label203:
      label209:
      for (int i = 17040843;; i = 17040842)
      {
        localObject = PackageInstallerService.-wrap0((Context)localObject, localResources.getString(i), paramString1, this.mUserId);
        if (localObject != null) {
          ((NotificationManager)this.mContext.getSystemService("notification")).notify(paramString1, 0, (Notification)localObject);
        }
        localObject = new Intent();
        ((Intent)localObject).putExtra("android.content.pm.extra.PACKAGE_NAME", paramString1);
        ((Intent)localObject).putExtra("android.content.pm.extra.SESSION_ID", this.mSessionId);
        ((Intent)localObject).putExtra("android.content.pm.extra.STATUS", PackageManager.installStatusToPublicStatus(paramInt));
        ((Intent)localObject).putExtra("android.content.pm.extra.STATUS_MESSAGE", PackageManager.installStatusToString(paramInt, paramString2));
        ((Intent)localObject).putExtra("android.content.pm.extra.LEGACY_STATUS", paramInt);
        if (paramBundle != null)
        {
          paramString1 = paramBundle.getString("android.content.pm.extra.FAILURE_EXISTING_PACKAGE");
          if (!TextUtils.isEmpty(paramString1)) {
            ((Intent)localObject).putExtra("android.content.pm.extra.OTHER_PACKAGE_NAME", paramString1);
          }
        }
        try
        {
          this.mTarget.sendIntent(this.mContext, 0, (Intent)localObject, null, null);
          return;
        }
        catch (IntentSender.SendIntentException paramString1) {}
        bool = false;
        break;
      }
    }
    
    public void onUserActionRequired(Intent paramIntent)
    {
      Intent localIntent = new Intent();
      localIntent.putExtra("android.content.pm.extra.SESSION_ID", this.mSessionId);
      localIntent.putExtra("android.content.pm.extra.STATUS", -1);
      localIntent.putExtra("android.intent.extra.INTENT", paramIntent);
      try
      {
        this.mTarget.sendIntent(this.mContext, 0, localIntent, null, null);
        return;
      }
      catch (IntentSender.SendIntentException paramIntent) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageInstallerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */