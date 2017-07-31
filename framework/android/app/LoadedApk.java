package android.app;

import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentReceiver.Stub;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.DisplayAdjustments;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class LoadedApk
{
  private static final String TAG = "LoadedApk";
  private final ActivityThread mActivityThread;
  private String mAppDir;
  private Application mApplication;
  private ApplicationInfo mApplicationInfo;
  private final ClassLoader mBaseClassLoader;
  private ClassLoader mClassLoader;
  int mClientCount = 0;
  private File mCredentialProtectedDataDirFile;
  private String mDataDir;
  private File mDataDirFile;
  private File mDeviceProtectedDataDirFile;
  private final DisplayAdjustments mDisplayAdjustments = new DisplayAdjustments();
  private final boolean mIncludeCode;
  private String mLibDir;
  private String[] mOverlayDirs;
  final String mPackageName;
  private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mReceivers = new ArrayMap();
  private final boolean mRegisterPackage;
  private String mResDir;
  Resources mResources;
  private final boolean mSecurityViolation;
  private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mServices = new ArrayMap();
  private String[] mSharedLibraries;
  private String[] mSplitAppDirs;
  private String[] mSplitResDirs;
  private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mUnboundServices = new ArrayMap();
  private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mUnregisteredReceivers = new ArrayMap();
  
  static
  {
    if (LoadedApk.class.desiredAssertionStatus()) {}
    for (boolean bool = false;; bool = true)
    {
      -assertionsDisabled = bool;
      return;
    }
  }
  
  LoadedApk(ActivityThread paramActivityThread)
  {
    this.mActivityThread = paramActivityThread;
    this.mApplicationInfo = new ApplicationInfo();
    this.mApplicationInfo.packageName = "android";
    this.mPackageName = "android";
    this.mAppDir = null;
    this.mResDir = null;
    this.mSplitAppDirs = null;
    this.mSplitResDirs = null;
    this.mOverlayDirs = null;
    this.mSharedLibraries = null;
    this.mDataDir = null;
    this.mDataDirFile = null;
    this.mDeviceProtectedDataDirFile = null;
    this.mCredentialProtectedDataDirFile = null;
    this.mLibDir = null;
    this.mBaseClassLoader = null;
    this.mSecurityViolation = false;
    this.mIncludeCode = true;
    this.mRegisterPackage = false;
    this.mClassLoader = ClassLoader.getSystemClassLoader();
    this.mResources = Resources.getSystem();
  }
  
  public LoadedApk(ActivityThread paramActivityThread, ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.mActivityThread = paramActivityThread;
    setApplicationInfo(paramApplicationInfo);
    this.mPackageName = paramApplicationInfo.packageName;
    this.mBaseClassLoader = paramClassLoader;
    this.mSecurityViolation = paramBoolean1;
    this.mIncludeCode = paramBoolean2;
    this.mRegisterPackage = paramBoolean3;
    this.mDisplayAdjustments.setCompatibilityInfo(paramCompatibilityInfo);
  }
  
  private static ApplicationInfo adjustNativeLibraryPaths(ApplicationInfo paramApplicationInfo)
  {
    if ((paramApplicationInfo.primaryCpuAbi != null) && (paramApplicationInfo.secondaryCpuAbi != null))
    {
      String str2 = VMRuntime.getRuntime().vmInstructionSet();
      Object localObject = VMRuntime.getInstructionSet(paramApplicationInfo.secondaryCpuAbi);
      String str1 = SystemProperties.get("ro.dalvik.vm.isa." + (String)localObject);
      if (str1.isEmpty()) {}
      while (str2.equals(localObject))
      {
        paramApplicationInfo = new ApplicationInfo(paramApplicationInfo);
        paramApplicationInfo.nativeLibraryDir = paramApplicationInfo.secondaryNativeLibraryDir;
        paramApplicationInfo.primaryCpuAbi = paramApplicationInfo.secondaryCpuAbi;
        return paramApplicationInfo;
        localObject = str1;
      }
    }
    return paramApplicationInfo;
  }
  
  private void createOrUpdateClassLoaderLocked(List<String> paramList)
  {
    if (this.mPackageName.equals("android"))
    {
      if (this.mClassLoader != null) {
        return;
      }
      if (this.mBaseClassLoader != null)
      {
        this.mClassLoader = this.mBaseClassLoader;
        return;
      }
      this.mClassLoader = ClassLoader.getSystemClassLoader();
      return;
    }
    if (!Objects.equals(this.mPackageName, ActivityThread.currentPackageName())) {
      VMRuntime.getRuntime().vmInstructionSet();
    }
    Object localObject2;
    Object localObject1;
    boolean bool;
    String str1;
    for (;;)
    {
      try
      {
        ActivityThread.getPackageManager().notifyPackageUse(this.mPackageName, 6);
        if (this.mRegisterPackage) {}
        bool = true;
      }
      catch (RemoteException paramList)
      {
        try
        {
          ActivityManagerNative.getDefault().addPackageDependency(this.mPackageName);
          localObject2 = new ArrayList(10);
          localObject1 = new ArrayList(10);
          makePaths(this.mActivityThread, this.mApplicationInfo, (List)localObject2, (List)localObject1);
          if (!this.mApplicationInfo.isSystemApp()) {
            break label285;
          }
          if (!this.mApplicationInfo.isUpdatedSystemApp()) {
            break label279;
          }
          bool = false;
          str2 = this.mDataDir;
          str1 = str2;
          if (bool) {
            str1 = str2 + File.pathSeparator + System.getProperty("java.library.path");
          }
          localObject1 = TextUtils.join(File.pathSeparator, (Iterable)localObject1);
          if (this.mIncludeCode) {
            break;
          }
          if (this.mClassLoader == null)
          {
            paramList = StrictMode.allowThreadDiskReads();
            this.mClassLoader = ApplicationLoaders.getDefault().getClassLoader("", this.mApplicationInfo.targetSdkVersion, bool, (String)localObject1, str1, this.mBaseClassLoader);
            StrictMode.setThreadPolicy(paramList);
          }
          return;
        }
        catch (RemoteException paramList)
        {
          throw paramList.rethrowFromSystemServer();
        }
        paramList = paramList;
        throw paramList.rethrowFromSystemServer();
      }
      label279:
      continue;
      label285:
      bool = false;
    }
    if (((List)localObject2).size() == 1) {}
    for (String str2 = (String)((List)localObject2).get(0);; str2 = TextUtils.join(File.pathSeparator, (Iterable)localObject2))
    {
      int i = 0;
      if (this.mClassLoader == null)
      {
        localObject2 = StrictMode.allowThreadDiskReads();
        this.mClassLoader = ApplicationLoaders.getDefault().getClassLoader(str2, this.mApplicationInfo.targetSdkVersion, bool, (String)localObject1, str1, this.mBaseClassLoader);
        StrictMode.setThreadPolicy((StrictMode.ThreadPolicy)localObject2);
        i = 1;
      }
      int j = i;
      if (paramList != null)
      {
        j = i;
        if (paramList.size() > 0)
        {
          paramList = TextUtils.join(File.pathSeparator, paramList);
          ApplicationLoaders.getDefault().addPath(this.mClassLoader, paramList);
          j = 1;
        }
      }
      if ((j != 0) && (!ActivityThread.isSystem())) {
        break;
      }
      return;
    }
    setupJitProfileSupport();
  }
  
  private static String[] getLibrariesFor(String paramString)
  {
    try
    {
      paramString = ActivityThread.getPackageManager().getApplicationInfo(paramString, 1024, UserHandle.myUserId());
      if (paramString == null) {
        return null;
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return paramString.sharedLibraryFiles;
  }
  
  private static File getPrimaryProfileFile(String paramString)
  {
    return new File(Environment.getDataProfilesDePackageDirectory(UserHandle.myUserId(), paramString), "primary.prof");
  }
  
  private void initializeJavaContextClassLoader()
  {
    Object localObject1 = ActivityThread.getPackageManager();
    try
    {
      localObject1 = ((IPackageManager)localObject1).getPackageInfo(this.mPackageName, 268435456, UserHandle.myUserId());
      if (localObject1 == null) {
        throw new IllegalStateException("Unable to get package info for " + this.mPackageName + "; is package not installed?");
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    int j;
    int i;
    if (localRemoteException.sharedUserId != null)
    {
      j = 1;
      if (localRemoteException.applicationInfo == null) {
        break label138;
      }
      if (!this.mPackageName.equals(localRemoteException.applicationInfo.processName)) {
        break label133;
      }
      i = 0;
      label103:
      if (j != 0) {
        break label143;
      }
      label107:
      if (i == 0) {
        break label148;
      }
    }
    label133:
    label138:
    label143:
    label148:
    for (Object localObject2 = new WarningContextClassLoader(null);; localObject2 = this.mClassLoader)
    {
      Thread.currentThread().setContextClassLoader((ClassLoader)localObject2);
      return;
      j = 0;
      break;
      i = 1;
      break label103;
      i = 0;
      break label103;
      i = 1;
      break label107;
    }
  }
  
  private boolean isSystemApp(ApplicationInfo paramApplicationInfo)
  {
    if ((paramApplicationInfo.uid < 10000) || ((paramApplicationInfo.flags & 0x1) != 0)) {}
    while ((paramApplicationInfo.flags & 0x80) != 0) {
      return true;
    }
    return false;
  }
  
  public static void makePaths(ActivityThread paramActivityThread, ApplicationInfo paramApplicationInfo, List<String> paramList1, List<String> paramList2)
  {
    String str1 = paramApplicationInfo.sourceDir;
    Object localObject1 = paramApplicationInfo.splitSourceDirs;
    Object localObject2 = paramApplicationInfo.nativeLibraryDir;
    String[] arrayOfString1 = paramApplicationInfo.sharedLibraryFiles;
    paramList1.clear();
    paramList1.add(str1);
    if (localObject1 != null) {
      Collections.addAll(paramList1, (Object[])localObject1);
    }
    if (paramList2 != null) {
      paramList2.clear();
    }
    String str2 = paramActivityThread.mInstrumentationPackageName;
    String str3 = paramActivityThread.mInstrumentationAppDir;
    String[] arrayOfString2 = paramActivityThread.mInstrumentationSplitAppDirs;
    String str4 = paramActivityThread.mInstrumentationLibDir;
    String str5 = paramActivityThread.mInstrumentedAppDir;
    String[] arrayOfString3 = paramActivityThread.mInstrumentedSplitAppDirs;
    String str6 = paramActivityThread.mInstrumentedLibDir;
    localObject1 = null;
    if (!str1.equals(str3))
    {
      paramActivityThread = (ActivityThread)localObject1;
      if (!str1.equals(str5)) {}
    }
    else
    {
      paramList1.clear();
      paramList1.add(str3);
      if (arrayOfString2 != null) {
        Collections.addAll(paramList1, arrayOfString2);
      }
      if (!str3.equals(str5))
      {
        paramList1.add(str5);
        if (arrayOfString3 != null) {
          Collections.addAll(paramList1, arrayOfString3);
        }
      }
      if (paramList2 != null)
      {
        paramList2.add(str4);
        if (!str4.equals(str6)) {
          paramList2.add(str6);
        }
      }
      paramActivityThread = (ActivityThread)localObject1;
      if (!str5.equals(str3)) {
        paramActivityThread = getLibrariesFor(str2);
      }
    }
    if (paramList2 != null)
    {
      if (paramList2.isEmpty()) {
        paramList2.add(localObject2);
      }
      if (paramApplicationInfo.primaryCpuAbi != null)
      {
        if (paramApplicationInfo.targetSdkVersion <= 23)
        {
          localObject2 = new StringBuilder().append("/system/fake-libs");
          if (!VMRuntime.is64BitAbi(paramApplicationInfo.primaryCpuAbi)) {
            break label391;
          }
        }
        label391:
        for (localObject1 = "64";; localObject1 = "")
        {
          paramList2.add((String)localObject1);
          localObject1 = paramList1.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (String)((Iterator)localObject1).next();
            paramList2.add((String)localObject2 + "!/lib/" + paramApplicationInfo.primaryCpuAbi);
          }
        }
      }
      if ((paramApplicationInfo.isSystemApp()) && (!paramApplicationInfo.isUpdatedSystemApp())) {
        break label466;
      }
    }
    int i;
    int j;
    while (arrayOfString1 != null)
    {
      i = 0;
      j = arrayOfString1.length;
      while (i < j)
      {
        paramApplicationInfo = arrayOfString1[i];
        if (!paramList1.contains(paramApplicationInfo)) {
          paramList1.add(0, paramApplicationInfo);
        }
        i += 1;
      }
      label466:
      paramList2.add(System.getProperty("java.library.path"));
    }
    if (paramActivityThread != null)
    {
      i = 0;
      j = paramActivityThread.length;
      while (i < j)
      {
        paramApplicationInfo = paramActivityThread[i];
        if (!paramList1.contains(paramApplicationInfo)) {
          paramList1.add(0, paramApplicationInfo);
        }
        i += 1;
      }
    }
  }
  
  /* Error */
  private void rewriteRValues(ClassLoader paramClassLoader, String paramString, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: new 202	java/lang/StringBuilder
    //   4: dup
    //   5: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   8: aload_2
    //   9: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12: ldc_w 510
    //   15: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   18: invokevirtual 212	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   21: invokevirtual 514	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   24: astore_1
    //   25: aload_1
    //   26: ldc_w 516
    //   29: iconst_1
    //   30: anewarray 80	java/lang/Class
    //   33: dup
    //   34: iconst_0
    //   35: getstatic 522	java/lang/Integer:TYPE	Ljava/lang/Class;
    //   38: aastore
    //   39: invokevirtual 526	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   42: astore_1
    //   43: aload_1
    //   44: aconst_null
    //   45: iconst_1
    //   46: anewarray 4	java/lang/Object
    //   49: dup
    //   50: iconst_0
    //   51: iload_3
    //   52: invokestatic 530	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   55: aastore
    //   56: invokevirtual 536	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   59: pop
    //   60: return
    //   61: astore_1
    //   62: ldc 37
    //   64: new 202	java/lang/StringBuilder
    //   67: dup
    //   68: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   71: ldc_w 538
    //   74: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: aload_2
    //   78: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   81: invokevirtual 212	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: invokestatic 544	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   87: pop
    //   88: return
    //   89: astore_1
    //   90: return
    //   91: astore_1
    //   92: aload_1
    //   93: invokevirtual 548	java/lang/reflect/InvocationTargetException:getCause	()Ljava/lang/Throwable;
    //   96: astore_1
    //   97: new 550	java/lang/RuntimeException
    //   100: dup
    //   101: new 202	java/lang/StringBuilder
    //   104: dup
    //   105: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   108: ldc_w 552
    //   111: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: aload_2
    //   115: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: invokevirtual 212	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   121: aload_1
    //   122: invokespecial 555	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   125: athrow
    //   126: astore_1
    //   127: goto -30 -> 97
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	LoadedApk
    //   0	130	1	paramClassLoader	ClassLoader
    //   0	130	2	paramString	String
    //   0	130	3	paramInt	int
    // Exception table:
    //   from	to	target	type
    //   0	25	61	java/lang/ClassNotFoundException
    //   25	43	89	java/lang/NoSuchMethodException
    //   43	60	91	java/lang/reflect/InvocationTargetException
    //   43	60	126	java/lang/IllegalAccessException
  }
  
  private void setApplicationInfo(ApplicationInfo paramApplicationInfo)
  {
    int i = Process.myUid();
    ApplicationInfo localApplicationInfo = adjustNativeLibraryPaths(paramApplicationInfo);
    this.mApplicationInfo = localApplicationInfo;
    this.mAppDir = localApplicationInfo.sourceDir;
    if (localApplicationInfo.uid == i)
    {
      paramApplicationInfo = localApplicationInfo.sourceDir;
      this.mResDir = paramApplicationInfo;
      this.mSplitAppDirs = localApplicationInfo.splitSourceDirs;
      if (localApplicationInfo.uid != i) {
        break label140;
      }
    }
    label140:
    for (paramApplicationInfo = localApplicationInfo.splitSourceDirs;; paramApplicationInfo = localApplicationInfo.splitPublicSourceDirs)
    {
      this.mSplitResDirs = paramApplicationInfo;
      this.mOverlayDirs = localApplicationInfo.resourceDirs;
      this.mSharedLibraries = localApplicationInfo.sharedLibraryFiles;
      this.mDataDir = localApplicationInfo.dataDir;
      this.mLibDir = localApplicationInfo.nativeLibraryDir;
      this.mDataDirFile = FileUtils.newFileOrNull(localApplicationInfo.dataDir);
      this.mDeviceProtectedDataDirFile = FileUtils.newFileOrNull(localApplicationInfo.deviceProtectedDataDir);
      this.mCredentialProtectedDataDirFile = FileUtils.newFileOrNull(localApplicationInfo.credentialProtectedDataDir);
      return;
      paramApplicationInfo = localApplicationInfo.publicSourceDir;
      break;
    }
  }
  
  private void setupJitProfileSupport()
  {
    if (!SystemProperties.getBoolean("dalvik.vm.usejitprofiles", false)) {
      return;
    }
    if (this.mApplicationInfo.uid != Process.myUid()) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    if ((this.mApplicationInfo.flags & 0x4) != 0) {
      localArrayList.add(this.mApplicationInfo.sourceDir);
    }
    if (this.mApplicationInfo.splitSourceDirs != null) {
      Collections.addAll(localArrayList, this.mApplicationInfo.splitSourceDirs);
    }
    if (localArrayList.isEmpty()) {
      return;
    }
    File localFile1 = getPrimaryProfileFile(this.mPackageName);
    File localFile2 = Environment.getDataProfilesDeForeignDexDirectory(UserHandle.myUserId());
    VMRuntime.registerAppInfo(localFile1.getPath(), this.mApplicationInfo.dataDir, (String[])localArrayList.toArray(new String[localArrayList.size()]), localFile2.getPath());
  }
  
  public IIntentReceiver forgetReceiverDispatcher(Context paramContext, BroadcastReceiver paramBroadcastReceiver)
  {
    synchronized (this.mReceivers)
    {
      Object localObject = (ArrayMap)this.mReceivers.get(paramContext);
      if (localObject != null)
      {
        ReceiverDispatcher localReceiverDispatcher = (ReceiverDispatcher)((ArrayMap)localObject).get(paramBroadcastReceiver);
        if (localReceiverDispatcher != null)
        {
          ((ArrayMap)localObject).remove(paramBroadcastReceiver);
          if (((ArrayMap)localObject).size() == 0) {
            this.mReceivers.remove(paramContext);
          }
          if (paramBroadcastReceiver.getDebugUnregister())
          {
            ArrayMap localArrayMap1 = (ArrayMap)this.mUnregisteredReceivers.get(paramContext);
            localObject = localArrayMap1;
            if (localArrayMap1 == null)
            {
              localObject = new ArrayMap();
              this.mUnregisteredReceivers.put(paramContext, localObject);
            }
            paramContext = new IllegalArgumentException("Originally unregistered here:");
            paramContext.fillInStackTrace();
            localReceiverDispatcher.setUnregisterLocation(paramContext);
            ((ArrayMap)localObject).put(paramBroadcastReceiver, localReceiverDispatcher);
          }
          localReceiverDispatcher.mForgotten = true;
          paramContext = localReceiverDispatcher.getIIntentReceiver();
          return paramContext;
        }
      }
      localObject = (ArrayMap)this.mUnregisteredReceivers.get(paramContext);
      if (localObject != null)
      {
        localObject = (ReceiverDispatcher)((ArrayMap)localObject).get(paramBroadcastReceiver);
        if (localObject != null)
        {
          paramContext = ((ReceiverDispatcher)localObject).getUnregisterLocation();
          throw new IllegalArgumentException("Unregistering Receiver " + paramBroadcastReceiver + " that was already unregistered", paramContext);
        }
      }
    }
    if (paramContext == null) {
      throw new IllegalStateException("Unbinding Receiver " + paramBroadcastReceiver + " from Context that is no longer in use: " + paramContext);
    }
    throw new IllegalArgumentException("Receiver not registered: " + paramBroadcastReceiver);
  }
  
  public final IServiceConnection forgetServiceDispatcher(Context paramContext, ServiceConnection paramServiceConnection)
  {
    synchronized (this.mServices)
    {
      Object localObject = (ArrayMap)this.mServices.get(paramContext);
      if (localObject != null)
      {
        ServiceDispatcher localServiceDispatcher = (ServiceDispatcher)((ArrayMap)localObject).get(paramServiceConnection);
        if (localServiceDispatcher != null)
        {
          ((ArrayMap)localObject).remove(paramServiceConnection);
          localServiceDispatcher.doForget();
          if (((ArrayMap)localObject).size() == 0) {
            this.mServices.remove(paramContext);
          }
          if ((localServiceDispatcher.getFlags() & 0x2) != 0)
          {
            ArrayMap localArrayMap1 = (ArrayMap)this.mUnboundServices.get(paramContext);
            localObject = localArrayMap1;
            if (localArrayMap1 == null)
            {
              localObject = new ArrayMap();
              this.mUnboundServices.put(paramContext, localObject);
            }
            paramContext = new IllegalArgumentException("Originally unbound here:");
            paramContext.fillInStackTrace();
            localServiceDispatcher.setUnbindLocation(paramContext);
            ((ArrayMap)localObject).put(paramServiceConnection, localServiceDispatcher);
          }
          paramContext = localServiceDispatcher.getIServiceConnection();
          return paramContext;
        }
      }
      localObject = (ArrayMap)this.mUnboundServices.get(paramContext);
      if (localObject != null)
      {
        localObject = (ServiceDispatcher)((ArrayMap)localObject).get(paramServiceConnection);
        if (localObject != null)
        {
          paramContext = ((ServiceDispatcher)localObject).getUnbindLocation();
          throw new IllegalArgumentException("Unbinding Service " + paramServiceConnection + " that was already unbound", paramContext);
        }
      }
    }
    if (paramContext == null) {
      throw new IllegalStateException("Unbinding Service " + paramServiceConnection + " from Context that is no longer in use: " + paramContext);
    }
    throw new IllegalArgumentException("Service not registered: " + paramServiceConnection);
  }
  
  public String getAppDir()
  {
    return this.mAppDir;
  }
  
  Application getApplication()
  {
    return this.mApplication;
  }
  
  public ApplicationInfo getApplicationInfo()
  {
    return this.mApplicationInfo;
  }
  
  public AssetManager getAssets(ActivityThread paramActivityThread)
  {
    return getResources(paramActivityThread).getAssets();
  }
  
  public ClassLoader getClassLoader()
  {
    try
    {
      if (this.mClassLoader == null) {
        createOrUpdateClassLoaderLocked(null);
      }
      ClassLoader localClassLoader = this.mClassLoader;
      return localClassLoader;
    }
    finally {}
  }
  
  public CompatibilityInfo getCompatibilityInfo()
  {
    return this.mDisplayAdjustments.getCompatibilityInfo();
  }
  
  public File getCredentialProtectedDataDirFile()
  {
    return this.mCredentialProtectedDataDirFile;
  }
  
  public String getDataDir()
  {
    return this.mDataDir;
  }
  
  public File getDataDirFile()
  {
    return this.mDataDirFile;
  }
  
  public File getDeviceProtectedDataDirFile()
  {
    return this.mDeviceProtectedDataDirFile;
  }
  
  public String getLibDir()
  {
    return this.mLibDir;
  }
  
  public String[] getOverlayDirs()
  {
    return this.mOverlayDirs;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  /* Error */
  public IIntentReceiver getReceiverDispatcher(BroadcastReceiver paramBroadcastReceiver, Context paramContext, Handler paramHandler, Instrumentation paramInstrumentation, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 101	android/app/LoadedApk:mReceivers	Landroid/util/ArrayMap;
    //   4: astore 9
    //   6: aload 9
    //   8: monitorenter
    //   9: aconst_null
    //   10: astore 6
    //   12: iload 5
    //   14: ifeq +159 -> 173
    //   17: aload_0
    //   18: getfield 101	android/app/LoadedApk:mReceivers	Landroid/util/ArrayMap;
    //   21: aload_2
    //   22: invokevirtual 615	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   25: checkcast 98	android/util/ArrayMap
    //   28: astore 7
    //   30: aload 7
    //   32: astore 6
    //   34: aload 7
    //   36: ifnull +137 -> 173
    //   39: aload 7
    //   41: aload_1
    //   42: invokevirtual 615	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   45: checkcast 6	android/app/LoadedApk$ReceiverDispatcher
    //   48: astore 8
    //   50: aload 7
    //   52: astore 6
    //   54: aload 8
    //   56: astore 7
    //   58: aload 7
    //   60: ifnonnull +75 -> 135
    //   63: new 6	android/app/LoadedApk$ReceiverDispatcher
    //   66: dup
    //   67: aload_1
    //   68: aload_2
    //   69: aload_3
    //   70: aload 4
    //   72: iload 5
    //   74: invokespecial 725	android/app/LoadedApk$ReceiverDispatcher:<init>	(Landroid/content/BroadcastReceiver;Landroid/content/Context;Landroid/os/Handler;Landroid/app/Instrumentation;Z)V
    //   77: astore_3
    //   78: iload 5
    //   80: ifeq +88 -> 168
    //   83: aload 6
    //   85: ifnonnull +77 -> 162
    //   88: new 98	android/util/ArrayMap
    //   91: dup
    //   92: invokespecial 99	android/util/ArrayMap:<init>	()V
    //   95: astore 4
    //   97: aload_0
    //   98: getfield 101	android/app/LoadedApk:mReceivers	Landroid/util/ArrayMap;
    //   101: aload_2
    //   102: aload 4
    //   104: invokevirtual 628	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   107: pop
    //   108: aload 4
    //   110: astore_2
    //   111: aload_2
    //   112: aload_1
    //   113: aload_3
    //   114: invokevirtual 628	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   117: pop
    //   118: aload_3
    //   119: astore_1
    //   120: aload_1
    //   121: iconst_0
    //   122: putfield 643	android/app/LoadedApk$ReceiverDispatcher:mForgotten	Z
    //   125: aload_1
    //   126: invokevirtual 647	android/app/LoadedApk$ReceiverDispatcher:getIIntentReceiver	()Landroid/content/IIntentReceiver;
    //   129: astore_1
    //   130: aload 9
    //   132: monitorexit
    //   133: aload_1
    //   134: areturn
    //   135: aload 7
    //   137: aload_2
    //   138: aload_3
    //   139: invokevirtual 729	android/app/LoadedApk$ReceiverDispatcher:validate	(Landroid/content/Context;Landroid/os/Handler;)V
    //   142: aload 7
    //   144: astore_1
    //   145: goto -25 -> 120
    //   148: astore_1
    //   149: aload 9
    //   151: monitorexit
    //   152: aload_1
    //   153: athrow
    //   154: astore_1
    //   155: goto -6 -> 149
    //   158: astore_1
    //   159: goto -10 -> 149
    //   162: aload 6
    //   164: astore_2
    //   165: goto -54 -> 111
    //   168: aload_3
    //   169: astore_1
    //   170: goto -50 -> 120
    //   173: aconst_null
    //   174: astore 7
    //   176: goto -118 -> 58
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	179	0	this	LoadedApk
    //   0	179	1	paramBroadcastReceiver	BroadcastReceiver
    //   0	179	2	paramContext	Context
    //   0	179	3	paramHandler	Handler
    //   0	179	4	paramInstrumentation	Instrumentation
    //   0	179	5	paramBoolean	boolean
    //   10	153	6	localObject1	Object
    //   28	147	7	localObject2	Object
    //   48	7	8	localReceiverDispatcher	ReceiverDispatcher
    //   4	146	9	localArrayMap	ArrayMap
    // Exception table:
    //   from	to	target	type
    //   17	30	148	finally
    //   39	50	148	finally
    //   97	108	148	finally
    //   111	118	148	finally
    //   120	130	148	finally
    //   63	78	154	finally
    //   135	142	154	finally
    //   88	97	158	finally
  }
  
  public String getResDir()
  {
    return this.mResDir;
  }
  
  public Resources getResources(ActivityThread paramActivityThread)
  {
    if (this.mResources == null) {
      this.mResources = paramActivityThread.getTopLevelResources(ActivityThread.currentPackageName(), this.mResDir, this.mSplitResDirs, this.mOverlayDirs, this.mApplicationInfo.sharedLibraryFiles, 0, this);
    }
    return this.mResources;
  }
  
  public final IServiceConnection getServiceDispatcher(ServiceConnection paramServiceConnection, Context paramContext, Handler paramHandler, int paramInt)
  {
    for (;;)
    {
      synchronized (this.mServices)
      {
        ArrayMap localArrayMap1 = (ArrayMap)this.mServices.get(paramContext);
        if (localArrayMap1 != null)
        {
          localServiceDispatcher = (ServiceDispatcher)localArrayMap1.get(paramServiceConnection);
          if (localServiceDispatcher == null) {}
          try
          {
            localServiceDispatcher = new ServiceDispatcher(paramServiceConnection, paramContext, paramHandler, paramInt);
            paramHandler = localArrayMap1;
            if (localArrayMap1 == null)
            {
              paramHandler = new ArrayMap();
              this.mServices.put(paramContext, paramHandler);
            }
            paramHandler.put(paramServiceConnection, localServiceDispatcher);
            paramServiceConnection = localServiceDispatcher;
            paramServiceConnection = paramServiceConnection.getIServiceConnection();
            return paramServiceConnection;
          }
          finally
          {
            continue;
          }
          localServiceDispatcher.validate(paramContext, paramHandler);
          paramServiceConnection = localServiceDispatcher;
        }
      }
      ServiceDispatcher localServiceDispatcher = null;
    }
  }
  
  public String[] getSplitAppDirs()
  {
    return this.mSplitAppDirs;
  }
  
  public String[] getSplitResDirs()
  {
    return this.mSplitResDirs;
  }
  
  public int getTargetSdkVersion()
  {
    return this.mApplicationInfo.targetSdkVersion;
  }
  
  void installSystemApplicationInfo(ApplicationInfo paramApplicationInfo, ClassLoader paramClassLoader)
  {
    if ((!-assertionsDisabled) && (!paramApplicationInfo.packageName.equals("android"))) {
      throw new AssertionError();
    }
    this.mApplicationInfo = paramApplicationInfo;
    this.mClassLoader = paramClassLoader;
  }
  
  public boolean isSecurityViolation()
  {
    return this.mSecurityViolation;
  }
  
  /* Error */
  public Application makeApplication(boolean paramBoolean, Instrumentation paramInstrumentation)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 695	android/app/LoadedApk:mApplication	Landroid/app/Application;
    //   4: ifnull +8 -> 12
    //   7: aload_0
    //   8: getfield 695	android/app/LoadedApk:mApplication	Landroid/app/Application;
    //   11: areturn
    //   12: ldc2_w 754
    //   15: ldc_w 756
    //   18: invokestatic 762	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   21: aconst_null
    //   22: astore 8
    //   24: aload_0
    //   25: getfield 116	android/app/LoadedApk:mApplicationInfo	Landroid/content/pm/ApplicationInfo;
    //   28: getfield 765	android/content/pm/ApplicationInfo:className	Ljava/lang/String;
    //   31: astore 6
    //   33: iload_1
    //   34: ifne +12 -> 46
    //   37: aload 6
    //   39: astore 7
    //   41: aload 6
    //   43: ifnonnull +8 -> 51
    //   46: ldc_w 767
    //   49: astore 7
    //   51: aload 8
    //   53: astore 6
    //   55: aload_0
    //   56: invokevirtual 769	android/app/LoadedApk:getClassLoader	()Ljava/lang/ClassLoader;
    //   59: astore 9
    //   61: aload 8
    //   63: astore 6
    //   65: aload_0
    //   66: getfield 123	android/app/LoadedApk:mPackageName	Ljava/lang/String;
    //   69: ldc 118
    //   71: invokevirtual 226	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   74: ifne +34 -> 108
    //   77: aload 8
    //   79: astore 6
    //   81: ldc2_w 754
    //   84: ldc_w 770
    //   87: invokestatic 762	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   90: aload 8
    //   92: astore 6
    //   94: aload_0
    //   95: invokespecial 772	android/app/LoadedApk:initializeJavaContextClassLoader	()V
    //   98: aload 8
    //   100: astore 6
    //   102: ldc2_w 754
    //   105: invokestatic 776	android/os/Trace:traceEnd	(J)V
    //   108: aload 8
    //   110: astore 6
    //   112: aload_0
    //   113: getfield 111	android/app/LoadedApk:mActivityThread	Landroid/app/ActivityThread;
    //   116: aload_0
    //   117: invokestatic 782	android/app/ContextImpl:createAppContext	(Landroid/app/ActivityThread;Landroid/app/LoadedApk;)Landroid/app/ContextImpl;
    //   120: astore 10
    //   122: aload 8
    //   124: astore 6
    //   126: aload_0
    //   127: getfield 111	android/app/LoadedApk:mActivityThread	Landroid/app/ActivityThread;
    //   130: getfield 786	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   133: aload 9
    //   135: aload 7
    //   137: aload 10
    //   139: invokevirtual 792	android/app/Instrumentation:newApplication	(Ljava/lang/ClassLoader;Ljava/lang/String;Landroid/content/Context;)Landroid/app/Application;
    //   142: astore 9
    //   144: aload 9
    //   146: astore 6
    //   148: aload 10
    //   150: aload 9
    //   152: invokevirtual 796	android/app/ContextImpl:setOuterContext	(Landroid/content/Context;)V
    //   155: aload 9
    //   157: astore 8
    //   159: aload 9
    //   161: astore 6
    //   163: iconst_1
    //   164: newarray <illegal type>
    //   166: dup
    //   167: iconst_0
    //   168: bipush 12
    //   170: iastore
    //   171: invokestatic 802	android/util/OpFeatures:isSupport	([I)Z
    //   174: ifeq +38 -> 212
    //   177: aload 9
    //   179: astore 8
    //   181: aload 9
    //   183: astore 6
    //   185: aload_0
    //   186: aload 9
    //   188: invokevirtual 806	android/app/Application:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   191: invokespecial 808	android/app/LoadedApk:isSystemApp	(Landroid/content/pm/ApplicationInfo;)Z
    //   194: ifne +18 -> 212
    //   197: aload 9
    //   199: astore 6
    //   201: aload_0
    //   202: getfield 111	android/app/LoadedApk:mActivityThread	Landroid/app/ActivityThread;
    //   205: invokevirtual 811	android/app/ActivityThread:setShowPermissionRequest	()V
    //   208: aload 9
    //   210: astore 8
    //   212: aload_0
    //   213: getfield 111	android/app/LoadedApk:mActivityThread	Landroid/app/ActivityThread;
    //   216: getfield 815	android/app/ActivityThread:mAllApplications	Ljava/util/ArrayList;
    //   219: aload 8
    //   221: invokevirtual 816	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   224: pop
    //   225: aload_0
    //   226: aload 8
    //   228: putfield 695	android/app/LoadedApk:mApplication	Landroid/app/Application;
    //   231: aload_2
    //   232: ifnull +9 -> 241
    //   235: aload_2
    //   236: aload 8
    //   238: invokevirtual 820	android/app/Instrumentation:callApplicationOnCreate	(Landroid/app/Application;)V
    //   241: aload_0
    //   242: aload_0
    //   243: getfield 111	android/app/LoadedApk:mActivityThread	Landroid/app/ActivityThread;
    //   246: invokevirtual 822	android/app/LoadedApk:getAssets	(Landroid/app/ActivityThread;)Landroid/content/res/AssetManager;
    //   249: invokevirtual 828	android/content/res/AssetManager:getAssignedPackageIdentifiers	()Landroid/util/SparseArray;
    //   252: astore_2
    //   253: aload_2
    //   254: invokevirtual 831	android/util/SparseArray:size	()I
    //   257: istore 4
    //   259: iconst_0
    //   260: istore_3
    //   261: iload_3
    //   262: iload 4
    //   264: if_icmpge +195 -> 459
    //   267: aload_2
    //   268: iload_3
    //   269: invokevirtual 835	android/util/SparseArray:keyAt	(I)I
    //   272: istore 5
    //   274: iload 5
    //   276: iconst_1
    //   277: if_icmpeq +10 -> 287
    //   280: iload 5
    //   282: bipush 127
    //   284: if_icmpne +154 -> 438
    //   287: iload_3
    //   288: iconst_1
    //   289: iadd
    //   290: istore_3
    //   291: goto -30 -> 261
    //   294: astore 9
    //   296: aload 6
    //   298: astore 8
    //   300: aload_0
    //   301: getfield 111	android/app/LoadedApk:mActivityThread	Landroid/app/ActivityThread;
    //   304: getfield 786	android/app/ActivityThread:mInstrumentation	Landroid/app/Instrumentation;
    //   307: aload 6
    //   309: aload 9
    //   311: invokevirtual 839	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   314: ifne -102 -> 212
    //   317: ldc2_w 754
    //   320: invokestatic 776	android/os/Trace:traceEnd	(J)V
    //   323: new 550	java/lang/RuntimeException
    //   326: dup
    //   327: new 202	java/lang/StringBuilder
    //   330: dup
    //   331: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   334: ldc_w 841
    //   337: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   340: aload 7
    //   342: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   345: ldc_w 843
    //   348: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   351: aload 9
    //   353: invokevirtual 844	java/lang/Exception:toString	()Ljava/lang/String;
    //   356: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   359: invokevirtual 212	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   362: aload 9
    //   364: invokespecial 555	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   367: athrow
    //   368: astore 6
    //   370: aload_2
    //   371: aload 8
    //   373: aload 6
    //   375: invokevirtual 839	android/app/Instrumentation:onException	(Ljava/lang/Object;Ljava/lang/Throwable;)Z
    //   378: ifne -137 -> 241
    //   381: ldc2_w 754
    //   384: invokestatic 776	android/os/Trace:traceEnd	(J)V
    //   387: new 550	java/lang/RuntimeException
    //   390: dup
    //   391: new 202	java/lang/StringBuilder
    //   394: dup
    //   395: invokespecial 203	java/lang/StringBuilder:<init>	()V
    //   398: ldc_w 846
    //   401: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   404: aload 8
    //   406: invokevirtual 850	android/app/Application:getClass	()Ljava/lang/Class;
    //   409: invokevirtual 853	java/lang/Class:getName	()Ljava/lang/String;
    //   412: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   415: ldc_w 843
    //   418: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   421: aload 6
    //   423: invokevirtual 844	java/lang/Exception:toString	()Ljava/lang/String;
    //   426: invokevirtual 209	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   429: invokevirtual 212	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   432: aload 6
    //   434: invokespecial 555	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   437: athrow
    //   438: aload_0
    //   439: aload_0
    //   440: invokevirtual 769	android/app/LoadedApk:getClassLoader	()Ljava/lang/ClassLoader;
    //   443: aload_2
    //   444: iload_3
    //   445: invokevirtual 856	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   448: checkcast 219	java/lang/String
    //   451: iload 5
    //   453: invokespecial 858	android/app/LoadedApk:rewriteRValues	(Ljava/lang/ClassLoader;Ljava/lang/String;I)V
    //   456: goto -169 -> 287
    //   459: ldc2_w 754
    //   462: invokestatic 776	android/os/Trace:traceEnd	(J)V
    //   465: aload 8
    //   467: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	468	0	this	LoadedApk
    //   0	468	1	paramBoolean	boolean
    //   0	468	2	paramInstrumentation	Instrumentation
    //   260	185	3	i	int
    //   257	8	4	j	int
    //   272	180	5	k	int
    //   31	277	6	localObject1	Object
    //   368	65	6	localException1	Exception
    //   39	302	7	localObject2	Object
    //   22	444	8	localObject3	Object
    //   59	150	9	localObject4	Object
    //   294	69	9	localException2	Exception
    //   120	29	10	localContextImpl	ContextImpl
    // Exception table:
    //   from	to	target	type
    //   55	61	294	java/lang/Exception
    //   65	77	294	java/lang/Exception
    //   81	90	294	java/lang/Exception
    //   94	98	294	java/lang/Exception
    //   102	108	294	java/lang/Exception
    //   112	122	294	java/lang/Exception
    //   126	144	294	java/lang/Exception
    //   148	155	294	java/lang/Exception
    //   163	177	294	java/lang/Exception
    //   185	197	294	java/lang/Exception
    //   201	208	294	java/lang/Exception
    //   235	241	368	java/lang/Exception
  }
  
  public void removeContextRegistrations(Context paramContext, String paramString1, String paramString2)
  {
    boolean bool = StrictMode.vmRegistrationLeaksEnabled();
    ArrayMap localArrayMap2;
    int i;
    Object localObject1;
    Object localObject2;
    synchronized (this.mReceivers)
    {
      localArrayMap2 = (ArrayMap)this.mReceivers.remove(paramContext);
      if (localArrayMap2 != null)
      {
        i = 0;
        for (;;)
        {
          if (i < localArrayMap2.size())
          {
            localObject1 = (ReceiverDispatcher)localArrayMap2.valueAt(i);
            localObject2 = new IntentReceiverLeaked(paramString2 + " " + paramString1 + " has leaked IntentReceiver " + ((ReceiverDispatcher)localObject1).getIntentReceiver() + " that was " + "originally registered here. Are you missing a " + "call to unregisterReceiver()?");
            ((IntentReceiverLeaked)localObject2).setStackTrace(((ReceiverDispatcher)localObject1).getLocation().getStackTrace());
            Slog.e("ActivityThread", ((IntentReceiverLeaked)localObject2).getMessage(), (Throwable)localObject2);
            if (bool) {
              StrictMode.onIntentReceiverLeaked((Throwable)localObject2);
            }
            try
            {
              ActivityManagerNative.getDefault().unregisterReceiver(((ReceiverDispatcher)localObject1).getIIntentReceiver());
              i += 1;
            }
            catch (RemoteException paramContext)
            {
              throw paramContext.rethrowFromSystemServer();
            }
          }
        }
      }
    }
    this.mUnregisteredReceivers.remove(paramContext);
    synchronized (this.mServices)
    {
      localArrayMap2 = (ArrayMap)this.mServices.remove(paramContext);
      if (localArrayMap2 != null)
      {
        i = 0;
        for (;;)
        {
          if (i < localArrayMap2.size())
          {
            localObject1 = (ServiceDispatcher)localArrayMap2.valueAt(i);
            localObject2 = new ServiceConnectionLeaked(paramString2 + " " + paramString1 + " has leaked ServiceConnection " + ((ServiceDispatcher)localObject1).getServiceConnection() + " that was originally bound here");
            ((ServiceConnectionLeaked)localObject2).setStackTrace(((ServiceDispatcher)localObject1).getLocation().getStackTrace());
            Slog.e("ActivityThread", ((ServiceConnectionLeaked)localObject2).getMessage(), (Throwable)localObject2);
            if (bool) {
              StrictMode.onServiceConnectionLeaked((Throwable)localObject2);
            }
            try
            {
              ActivityManagerNative.getDefault().unbindService(((ServiceDispatcher)localObject1).getIServiceConnection());
              ((ServiceDispatcher)localObject1).doForget();
              i += 1;
            }
            catch (RemoteException paramContext)
            {
              throw paramContext.rethrowFromSystemServer();
            }
          }
        }
      }
    }
    this.mUnboundServices.remove(paramContext);
  }
  
  public void setCompatibilityInfo(CompatibilityInfo paramCompatibilityInfo)
  {
    this.mDisplayAdjustments.setCompatibilityInfo(paramCompatibilityInfo);
  }
  
  public void updateApplicationInfo(ApplicationInfo paramApplicationInfo, List<String> paramList)
  {
    setApplicationInfo(paramApplicationInfo);
    Object localObject = new ArrayList();
    makePaths(this.mActivityThread, paramApplicationInfo, (List)localObject, null);
    ArrayList localArrayList = new ArrayList(((List)localObject).size());
    if (paramList != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str1 = (String)((Iterator)localObject).next();
        String str2 = str1.substring(str1.lastIndexOf(File.separator));
        int j = 0;
        Iterator localIterator = paramList.iterator();
        do
        {
          i = j;
          if (!localIterator.hasNext()) {
            break;
          }
        } while (!str2.equals(((String)localIterator.next()).substring(str1.lastIndexOf(File.separator))));
        int i = 1;
        if (i == 0) {
          localArrayList.add(str1);
        }
      }
    }
    localArrayList.addAll((Collection)localObject);
    try
    {
      createOrUpdateClassLoaderLocked(localArrayList);
      if (this.mResources != null) {
        this.mResources = this.mActivityThread.getTopLevelResources(paramApplicationInfo.processName, this.mResDir, this.mSplitResDirs, this.mOverlayDirs, this.mApplicationInfo.sharedLibraryFiles, 0, this);
      }
      return;
    }
    finally
    {
      paramApplicationInfo = finally;
      throw paramApplicationInfo;
    }
  }
  
  static final class ReceiverDispatcher
  {
    final Handler mActivityThread;
    final Context mContext;
    boolean mForgotten;
    final IIntentReceiver.Stub mIIntentReceiver;
    final Instrumentation mInstrumentation;
    final IntentReceiverLeaked mLocation;
    final BroadcastReceiver mReceiver;
    final boolean mRegistered;
    RuntimeException mUnregisterLocation;
    
    ReceiverDispatcher(BroadcastReceiver paramBroadcastReceiver, Context paramContext, Handler paramHandler, Instrumentation paramInstrumentation, boolean paramBoolean)
    {
      if (paramHandler == null) {
        throw new NullPointerException("Handler must not be null");
      }
      if (paramBoolean) {}
      for (boolean bool = false;; bool = true)
      {
        this.mIIntentReceiver = new InnerReceiver(this, bool);
        this.mReceiver = paramBroadcastReceiver;
        this.mContext = paramContext;
        this.mActivityThread = paramHandler;
        this.mInstrumentation = paramInstrumentation;
        this.mRegistered = paramBoolean;
        this.mLocation = new IntentReceiverLeaked(null);
        this.mLocation.fillInStackTrace();
        return;
      }
    }
    
    IIntentReceiver getIIntentReceiver()
    {
      return this.mIIntentReceiver;
    }
    
    BroadcastReceiver getIntentReceiver()
    {
      return this.mReceiver;
    }
    
    IntentReceiverLeaked getLocation()
    {
      return this.mLocation;
    }
    
    RuntimeException getUnregisterLocation()
    {
      return this.mUnregisterLocation;
    }
    
    public void performReceive(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
    {
      paramString = new Args(paramIntent, paramInt1, paramString, paramBundle, paramBoolean1, paramBoolean2, paramInt2);
      if (paramIntent == null) {
        Log.wtf("LoadedApk", "Null intent received");
      }
      if ((paramIntent != null) && (this.mActivityThread.post(paramString))) {}
      while ((!this.mRegistered) || (!paramBoolean1)) {
        return;
      }
      paramString.sendFinished(ActivityManagerNative.getDefault());
    }
    
    void setUnregisterLocation(RuntimeException paramRuntimeException)
    {
      this.mUnregisterLocation = paramRuntimeException;
    }
    
    void validate(Context paramContext, Handler paramHandler)
    {
      if (this.mContext != paramContext) {
        throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing Context (was " + this.mContext + " now " + paramContext + ")");
      }
      if (this.mActivityThread != paramHandler) {
        throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing handler (was " + this.mActivityThread + " now " + paramHandler + ")");
      }
    }
    
    final class Args
      extends BroadcastReceiver.PendingResult
      implements Runnable
    {
      private Intent mCurIntent;
      private boolean mDispatched;
      private final boolean mOrdered;
      
      public Args(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2) {}
      
      public void run()
      {
        BroadcastReceiver localBroadcastReceiver = LoadedApk.ReceiverDispatcher.this.mReceiver;
        bool = this.mOrdered;
        localIActivityManager = ActivityManagerNative.getDefault();
        localIntent = this.mCurIntent;
        if (localIntent == null) {
          Log.wtf("LoadedApk", "Null intent being dispatched, mDispatched=" + this.mDispatched);
        }
        this.mCurIntent = null;
        this.mDispatched = true;
        if ((localBroadcastReceiver == null) || (localIntent == null)) {}
        while (LoadedApk.ReceiverDispatcher.this.mForgotten)
        {
          if ((LoadedApk.ReceiverDispatcher.this.mRegistered) && (bool)) {
            sendFinished(localIActivityManager);
          }
          return;
        }
        if (localIntent.getAction() != null) {
          Trace.traceBegin(64L, "broadcastReceiveReg" + localIntent.getAction() + " receiver =" + localBroadcastReceiver);
        }
        for (;;)
        {
          try
          {
            ClassLoader localClassLoader = LoadedApk.ReceiverDispatcher.this.mReceiver.getClass().getClassLoader();
            localIntent.setExtrasClassLoader(localClassLoader);
            localIntent.prepareToEnterProcess();
            setExtrasClassLoader(localClassLoader);
            localBroadcastReceiver.setPendingResult(this);
            long l = SystemClock.uptimeMillis();
            if (ActivityThread.DEBUG_ONEPLUS) {
              Log.i("LoadedApk", localBroadcastReceiver + " onReceive " + localIntent.getAction() + " start");
            }
            localBroadcastReceiver.onReceive(LoadedApk.ReceiverDispatcher.this.mContext, localIntent);
            if (ActivityThread.DEBUG_ONEPLUS) {
              Log.i("LoadedApk", localBroadcastReceiver + " onReceive " + localIntent.getAction() + " in " + (SystemClock.uptimeMillis() - l) + "ms");
            }
          }
          catch (Exception localException)
          {
            if ((!LoadedApk.ReceiverDispatcher.this.mRegistered) || (!bool)) {
              continue;
            }
            sendFinished(localIActivityManager);
            if ((LoadedApk.ReceiverDispatcher.this.mInstrumentation != null) && (LoadedApk.ReceiverDispatcher.this.mInstrumentation.onException(LoadedApk.ReceiverDispatcher.this.mReceiver, localException))) {
              continue;
            }
            Trace.traceEnd(64L);
            throw new RuntimeException("Error receiving broadcast " + localIntent + " in " + LoadedApk.ReceiverDispatcher.this.mReceiver, localException);
          }
          if (localBroadcastReceiver.getPendingResult() != null) {
            finish();
          }
          Trace.traceEnd(64L);
          return;
          Trace.traceBegin(64L, "broadcastReceiveRegnull  receiver = " + localBroadcastReceiver);
        }
      }
    }
    
    static final class InnerReceiver
      extends IIntentReceiver.Stub
    {
      final WeakReference<LoadedApk.ReceiverDispatcher> mDispatcher;
      final LoadedApk.ReceiverDispatcher mStrongRef;
      
      InnerReceiver(LoadedApk.ReceiverDispatcher paramReceiverDispatcher, boolean paramBoolean)
      {
        this.mDispatcher = new WeakReference(paramReceiverDispatcher);
        if (paramBoolean) {}
        for (;;)
        {
          this.mStrongRef = paramReceiverDispatcher;
          return;
          paramReceiverDispatcher = null;
        }
      }
      
      public void performReceive(Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
      {
        if (paramIntent == null) {
          Log.wtf("LoadedApk", "Null intent received");
        }
        for (Object localObject = null; localObject != null; localObject = (LoadedApk.ReceiverDispatcher)this.mDispatcher.get())
        {
          ((LoadedApk.ReceiverDispatcher)localObject).performReceive(paramIntent, paramInt1, paramString, paramBundle, paramBoolean1, paramBoolean2, paramInt2);
          return;
        }
        localObject = ActivityManagerNative.getDefault();
        if (paramBundle != null) {}
        try
        {
          paramBundle.setAllowFds(false);
          ((IActivityManager)localObject).finishReceiver(this, paramInt1, paramString, paramBundle, false, paramIntent.getFlags());
          return;
        }
        catch (RemoteException paramIntent)
        {
          throw paramIntent.rethrowFromSystemServer();
        }
      }
    }
  }
  
  static final class ServiceDispatcher
  {
    private final ArrayMap<ComponentName, ConnectionInfo> mActiveConnections = new ArrayMap();
    private final Handler mActivityThread;
    private final ServiceConnection mConnection;
    private final Context mContext;
    private final int mFlags;
    private boolean mForgotten;
    private final InnerConnection mIServiceConnection = new InnerConnection(this);
    private final ServiceConnectionLeaked mLocation;
    private RuntimeException mUnbindLocation;
    
    ServiceDispatcher(ServiceConnection paramServiceConnection, Context paramContext, Handler paramHandler, int paramInt)
    {
      this.mConnection = paramServiceConnection;
      this.mContext = paramContext;
      this.mActivityThread = paramHandler;
      this.mLocation = new ServiceConnectionLeaked(null);
      this.mLocation.fillInStackTrace();
      this.mFlags = paramInt;
    }
    
    public void connected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if (this.mActivityThread != null)
      {
        this.mActivityThread.post(new RunConnection(paramComponentName, paramIBinder, 0));
        return;
      }
      doConnected(paramComponentName, paramIBinder);
    }
    
    public void death(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if (this.mActivityThread != null)
      {
        this.mActivityThread.post(new RunConnection(paramComponentName, paramIBinder, 1));
        return;
      }
      doDeath(paramComponentName, paramIBinder);
    }
    
    /* Error */
    public void doConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 95	android/app/LoadedApk$ServiceDispatcher:mForgotten	Z
      //   6: istore_3
      //   7: iload_3
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: aload_0
      //   15: getfield 48	android/app/LoadedApk$ServiceDispatcher:mActiveConnections	Landroid/util/ArrayMap;
      //   18: aload_1
      //   19: invokevirtual 99	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   22: checkcast 9	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo
      //   25: astore 4
      //   27: aload 4
      //   29: ifnull +19 -> 48
      //   32: aload 4
      //   34: getfield 103	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo:binder	Landroid/os/IBinder;
      //   37: astore 5
      //   39: aload 5
      //   41: aload_2
      //   42: if_acmpne +6 -> 48
      //   45: aload_0
      //   46: monitorexit
      //   47: return
      //   48: aload_2
      //   49: ifnull +125 -> 174
      //   52: new 9	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo
      //   55: dup
      //   56: aconst_null
      //   57: invokespecial 106	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo:<init>	(Landroid/app/LoadedApk$ServiceDispatcher$ConnectionInfo;)V
      //   60: astore 5
      //   62: aload 5
      //   64: aload_2
      //   65: putfield 103	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo:binder	Landroid/os/IBinder;
      //   68: aload 5
      //   70: new 12	android/app/LoadedApk$ServiceDispatcher$DeathMonitor
      //   73: dup
      //   74: aload_0
      //   75: aload_1
      //   76: aload_2
      //   77: invokespecial 109	android/app/LoadedApk$ServiceDispatcher$DeathMonitor:<init>	(Landroid/app/LoadedApk$ServiceDispatcher;Landroid/content/ComponentName;Landroid/os/IBinder;)V
      //   80: putfield 113	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo:deathMonitor	Landroid/os/IBinder$DeathRecipient;
      //   83: aload_2
      //   84: aload 5
      //   86: getfield 113	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo:deathMonitor	Landroid/os/IBinder$DeathRecipient;
      //   89: iconst_0
      //   90: invokeinterface 119 3 0
      //   95: aload_0
      //   96: getfield 48	android/app/LoadedApk$ServiceDispatcher:mActiveConnections	Landroid/util/ArrayMap;
      //   99: aload_1
      //   100: aload 5
      //   102: invokevirtual 123	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   105: pop
      //   106: aload 4
      //   108: ifnull +20 -> 128
      //   111: aload 4
      //   113: getfield 103	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo:binder	Landroid/os/IBinder;
      //   116: aload 4
      //   118: getfield 113	android/app/LoadedApk$ServiceDispatcher$ConnectionInfo:deathMonitor	Landroid/os/IBinder$DeathRecipient;
      //   121: iconst_0
      //   122: invokeinterface 127 3 0
      //   127: pop
      //   128: aload_0
      //   129: monitorexit
      //   130: aload 4
      //   132: ifnull +13 -> 145
      //   135: aload_0
      //   136: getfield 55	android/app/LoadedApk$ServiceDispatcher:mConnection	Landroid/content/ServiceConnection;
      //   139: aload_1
      //   140: invokeinterface 133 2 0
      //   145: aload_2
      //   146: ifnull +14 -> 160
      //   149: aload_0
      //   150: getfield 55	android/app/LoadedApk$ServiceDispatcher:mConnection	Landroid/content/ServiceConnection;
      //   153: aload_1
      //   154: aload_2
      //   155: invokeinterface 136 3 0
      //   160: return
      //   161: astore_2
      //   162: aload_0
      //   163: getfield 48	android/app/LoadedApk$ServiceDispatcher:mActiveConnections	Landroid/util/ArrayMap;
      //   166: aload_1
      //   167: invokevirtual 139	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
      //   170: pop
      //   171: aload_0
      //   172: monitorexit
      //   173: return
      //   174: aload_0
      //   175: getfield 48	android/app/LoadedApk$ServiceDispatcher:mActiveConnections	Landroid/util/ArrayMap;
      //   178: aload_1
      //   179: invokevirtual 139	android/util/ArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
      //   182: pop
      //   183: goto -77 -> 106
      //   186: astore_1
      //   187: aload_0
      //   188: monitorexit
      //   189: aload_1
      //   190: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	191	0	this	ServiceDispatcher
      //   0	191	1	paramComponentName	ComponentName
      //   0	191	2	paramIBinder	IBinder
      //   6	2	3	bool	boolean
      //   25	106	4	localConnectionInfo	ConnectionInfo
      //   37	64	5	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   83	106	161	android/os/RemoteException
      //   2	7	186	finally
      //   14	27	186	finally
      //   32	39	186	finally
      //   52	83	186	finally
      //   83	106	186	finally
      //   111	128	186	finally
      //   162	171	186	finally
      //   174	183	186	finally
    }
    
    public void doDeath(ComponentName paramComponentName, IBinder paramIBinder)
    {
      try
      {
        ConnectionInfo localConnectionInfo = (ConnectionInfo)this.mActiveConnections.get(paramComponentName);
        if (localConnectionInfo != null)
        {
          IBinder localIBinder = localConnectionInfo.binder;
          if (localIBinder == paramIBinder) {}
        }
        else
        {
          return;
        }
        this.mActiveConnections.remove(paramComponentName);
        localConnectionInfo.binder.unlinkToDeath(localConnectionInfo.deathMonitor, 0);
        this.mConnection.onServiceDisconnected(paramComponentName);
        return;
      }
      finally {}
    }
    
    void doForget()
    {
      int i = 0;
      try
      {
        while (i < this.mActiveConnections.size())
        {
          ConnectionInfo localConnectionInfo = (ConnectionInfo)this.mActiveConnections.valueAt(i);
          localConnectionInfo.binder.unlinkToDeath(localConnectionInfo.deathMonitor, 0);
          i += 1;
        }
        this.mActiveConnections.clear();
        this.mForgotten = true;
        return;
      }
      finally {}
    }
    
    int getFlags()
    {
      return this.mFlags;
    }
    
    IServiceConnection getIServiceConnection()
    {
      return this.mIServiceConnection;
    }
    
    ServiceConnectionLeaked getLocation()
    {
      return this.mLocation;
    }
    
    ServiceConnection getServiceConnection()
    {
      return this.mConnection;
    }
    
    RuntimeException getUnbindLocation()
    {
      return this.mUnbindLocation;
    }
    
    void setUnbindLocation(RuntimeException paramRuntimeException)
    {
      this.mUnbindLocation = paramRuntimeException;
    }
    
    void validate(Context paramContext, Handler paramHandler)
    {
      if (this.mContext != paramContext) {
        throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing Context (was " + this.mContext + " now " + paramContext + ")");
      }
      if (this.mActivityThread != paramHandler) {
        throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing handler (was " + this.mActivityThread + " now " + paramHandler + ")");
      }
    }
    
    private static class ConnectionInfo
    {
      IBinder binder;
      IBinder.DeathRecipient deathMonitor;
    }
    
    private final class DeathMonitor
      implements IBinder.DeathRecipient
    {
      final ComponentName mName;
      final IBinder mService;
      
      DeathMonitor(ComponentName paramComponentName, IBinder paramIBinder)
      {
        this.mName = paramComponentName;
        this.mService = paramIBinder;
      }
      
      public void binderDied()
      {
        LoadedApk.ServiceDispatcher.this.death(this.mName, this.mService);
      }
    }
    
    private static class InnerConnection
      extends IServiceConnection.Stub
    {
      final WeakReference<LoadedApk.ServiceDispatcher> mDispatcher;
      
      InnerConnection(LoadedApk.ServiceDispatcher paramServiceDispatcher)
      {
        this.mDispatcher = new WeakReference(paramServiceDispatcher);
      }
      
      public void connected(ComponentName paramComponentName, IBinder paramIBinder)
        throws RemoteException
      {
        LoadedApk.ServiceDispatcher localServiceDispatcher = (LoadedApk.ServiceDispatcher)this.mDispatcher.get();
        if (localServiceDispatcher != null) {
          localServiceDispatcher.connected(paramComponentName, paramIBinder);
        }
      }
    }
    
    private final class RunConnection
      implements Runnable
    {
      final int mCommand;
      final ComponentName mName;
      final IBinder mService;
      
      RunConnection(ComponentName paramComponentName, IBinder paramIBinder, int paramInt)
      {
        this.mName = paramComponentName;
        this.mService = paramIBinder;
        this.mCommand = paramInt;
      }
      
      public void run()
      {
        if (this.mCommand == 0) {
          LoadedApk.ServiceDispatcher.this.doConnected(this.mName, this.mService);
        }
        while (this.mCommand != 1) {
          return;
        }
        LoadedApk.ServiceDispatcher.this.doDeath(this.mName, this.mService);
      }
    }
  }
  
  private static class WarningContextClassLoader
    extends ClassLoader
  {
    private static boolean warned = false;
    
    private void warn(String paramString)
    {
      if (warned) {
        return;
      }
      warned = true;
      Thread.currentThread().setContextClassLoader(getParent());
      Slog.w("ActivityThread", "ClassLoader." + paramString + ": " + "The class loader returned by " + "Thread.getContextClassLoader() may fail for processes " + "that host multiple applications. You should explicitly " + "specify a context class loader. For example: " + "Thread.setContextClassLoader(getClass().getClassLoader());");
    }
    
    public void clearAssertionStatus()
    {
      warn("clearAssertionStatus");
      getParent().clearAssertionStatus();
    }
    
    public URL getResource(String paramString)
    {
      warn("getResource");
      return getParent().getResource(paramString);
    }
    
    public InputStream getResourceAsStream(String paramString)
    {
      warn("getResourceAsStream");
      return getParent().getResourceAsStream(paramString);
    }
    
    public Enumeration<URL> getResources(String paramString)
      throws IOException
    {
      warn("getResources");
      return getParent().getResources(paramString);
    }
    
    public Class<?> loadClass(String paramString)
      throws ClassNotFoundException
    {
      warn("loadClass");
      return getParent().loadClass(paramString);
    }
    
    public void setClassAssertionStatus(String paramString, boolean paramBoolean)
    {
      warn("setClassAssertionStatus");
      getParent().setClassAssertionStatus(paramString, paramBoolean);
    }
    
    public void setDefaultAssertionStatus(boolean paramBoolean)
    {
      warn("setDefaultAssertionStatus");
      getParent().setDefaultAssertionStatus(paramBoolean);
    }
    
    public void setPackageAssertionStatus(String paramString, boolean paramBoolean)
    {
      warn("setPackageAssertionStatus");
      getParent().setPackageAssertionStatus(paramString, paramBoolean);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/LoadedApk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */