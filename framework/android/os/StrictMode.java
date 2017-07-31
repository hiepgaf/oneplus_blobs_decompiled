package android.os;

import android.animation.ValueAnimator;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.ApplicationErrorReport.CrashInfo;
import android.app.IActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Printer;
import android.util.Singleton;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import com.android.internal.os.RuntimeInit;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.HexDump;
import dalvik.system.BlockGuard;
import dalvik.system.BlockGuard.BlockGuardPolicyException;
import dalvik.system.BlockGuard.Policy;
import dalvik.system.CloseGuard;
import dalvik.system.CloseGuard.Reporter;
import dalvik.system.VMDebug;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class StrictMode
{
  private static final int ALL_THREAD_DETECT_BITS = 31;
  private static final int ALL_VM_DETECT_BITS = 32512;
  private static final String CLEARTEXT_PROPERTY = "persist.sys.strictmode.clear";
  public static final int DETECT_CUSTOM = 8;
  public static final int DETECT_DISK_READ = 2;
  public static final int DETECT_DISK_WRITE = 1;
  public static final int DETECT_NETWORK = 4;
  public static final int DETECT_RESOURCE_MISMATCH = 16;
  public static final int DETECT_VM_ACTIVITY_LEAKS = 1024;
  private static final int DETECT_VM_CLEARTEXT_NETWORK = 16384;
  public static final int DETECT_VM_CLOSABLE_LEAKS = 512;
  public static final int DETECT_VM_CURSOR_LEAKS = 256;
  private static final int DETECT_VM_FILE_URI_EXPOSURE = 8192;
  private static final int DETECT_VM_INSTANCE_LEAKS = 2048;
  public static final int DETECT_VM_REGISTRATION_LEAKS = 4096;
  public static final String DISABLE_PROPERTY = "persist.sys.strictmode.disable";
  private static final HashMap<Class, Integer> EMPTY_CLASS_LIMIT_MAP;
  private static final boolean IS_ENG_BUILD;
  private static final boolean IS_USER_BUILD;
  private static final boolean LOG_V = Log.isLoggable("StrictMode", 2);
  private static final int MAX_OFFENSES_PER_LOOP = 10;
  private static final int MAX_SPAN_TAGS = 20;
  private static final long MIN_DIALOG_INTERVAL_MS = 30000L;
  private static final long MIN_LOG_INTERVAL_MS = 1000L;
  public static final int NETWORK_POLICY_ACCEPT = 0;
  public static final int NETWORK_POLICY_LOG = 1;
  public static final int NETWORK_POLICY_REJECT = 2;
  private static final Span NO_OP_SPAN;
  public static final int PENALTY_DEATH = 262144;
  public static final int PENALTY_DEATH_ON_CLEARTEXT_NETWORK = 33554432;
  public static final int PENALTY_DEATH_ON_FILE_URI_EXPOSURE = 67108864;
  public static final int PENALTY_DEATH_ON_NETWORK = 16777216;
  public static final int PENALTY_DIALOG = 131072;
  public static final int PENALTY_DROPBOX = 2097152;
  public static final int PENALTY_FLASH = 1048576;
  public static final int PENALTY_GATHER = 4194304;
  public static final int PENALTY_LOG = 65536;
  private static final String TAG = "StrictMode";
  private static final int THREAD_PENALTY_MASK = 24576000;
  public static final String VISUAL_PROPERTY = "persist.sys.strictmode.visual";
  private static final int VM_PENALTY_MASK = 103088128;
  private static final ThreadLocal<ArrayList<ViolationInfo>> gatheredViolations;
  private static final AtomicInteger sDropboxCallsInFlight;
  private static final HashMap<Class, Integer> sExpectedActivityInstanceCount = new HashMap();
  private static boolean sIsIdlerRegistered;
  private static long sLastInstanceCountCheckMillis;
  private static final HashMap<Integer, Long> sLastVmViolationTime;
  private static final MessageQueue.IdleHandler sProcessIdleHandler;
  private static final ThreadLocal<ThreadSpanState> sThisThreadSpanState;
  private static volatile VmPolicy sVmPolicy;
  private static volatile int sVmPolicyMask;
  private static Singleton<IWindowManager> sWindowManager;
  private static final ThreadLocal<AndroidBlockGuardPolicy> threadAndroidPolicy;
  private static final ThreadLocal<Handler> threadHandler;
  private static final ThreadLocal<ArrayList<ViolationInfo>> violationsBeingTimed;
  
  static
  {
    IS_USER_BUILD = "user".equals(Build.TYPE);
    IS_ENG_BUILD = "eng".equals(Build.TYPE);
    EMPTY_CLASS_LIMIT_MAP = new HashMap();
    sVmPolicyMask = 0;
    sVmPolicy = VmPolicy.LAX;
    sDropboxCallsInFlight = new AtomicInteger(0);
    gatheredViolations = new ThreadLocal()
    {
      protected ArrayList<StrictMode.ViolationInfo> initialValue()
      {
        return null;
      }
    };
    violationsBeingTimed = new ThreadLocal()
    {
      protected ArrayList<StrictMode.ViolationInfo> initialValue()
      {
        return new ArrayList();
      }
    };
    threadHandler = new ThreadLocal()
    {
      protected Handler initialValue()
      {
        return new Handler();
      }
    };
    threadAndroidPolicy = new ThreadLocal()
    {
      protected StrictMode.AndroidBlockGuardPolicy initialValue()
      {
        return new StrictMode.AndroidBlockGuardPolicy(0);
      }
    };
    sLastInstanceCountCheckMillis = 0L;
    sIsIdlerRegistered = false;
    sProcessIdleHandler = new MessageQueue.IdleHandler()
    {
      public boolean queueIdle()
      {
        long l = SystemClock.uptimeMillis();
        if (l - StrictMode.-get4() > 30000L)
        {
          StrictMode.-set0(l);
          StrictMode.conditionallyCheckInstanceCounts();
        }
        return true;
      }
    };
    sLastVmViolationTime = new HashMap();
    NO_OP_SPAN = new Span()
    {
      public void finish() {}
    };
    sThisThreadSpanState = new ThreadLocal()
    {
      protected StrictMode.ThreadSpanState initialValue()
      {
        return new StrictMode.ThreadSpanState(null);
      }
    };
    sWindowManager = new Singleton()
    {
      protected IWindowManager create()
      {
        return IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
      }
    };
  }
  
  public static ThreadPolicy allowThreadDiskReads()
  {
    int i = getThreadPolicyMask();
    int j = i & 0xFFFFFFFD;
    if (j != i) {
      setThreadPolicyMask(j);
    }
    return new ThreadPolicy(i, null);
  }
  
  public static ThreadPolicy allowThreadDiskWrites()
  {
    int i = getThreadPolicyMask();
    int j = i & 0xFFFFFFFC;
    if (j != i) {
      setThreadPolicyMask(j);
    }
    return new ThreadPolicy(i, null);
  }
  
  private static boolean amTheSystemServerProcess()
  {
    if (Process.myUid() != 1000) {
      return false;
    }
    Object localObject = new Throwable();
    ((Throwable)localObject).fillInStackTrace();
    localObject = ((Throwable)localObject).getStackTrace();
    int j = localObject.length;
    int i = 0;
    while (i < j)
    {
      String str = localObject[i].getClassName();
      if ((str != null) && (str.startsWith("com.android.server."))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  static void clearGatheredViolations()
  {
    gatheredViolations.set(null);
  }
  
  public static void conditionallyCheckInstanceCounts()
  {
    VmPolicy localVmPolicy = getVmPolicy();
    int i = localVmPolicy.classInstanceLimit.size();
    if (i == 0) {
      return;
    }
    System.gc();
    System.runFinalization();
    System.gc();
    Class[] arrayOfClass = (Class[])localVmPolicy.classInstanceLimit.keySet().toArray(new Class[i]);
    long[] arrayOfLong = VMDebug.countInstancesOfClasses(arrayOfClass, false);
    i = 0;
    while (i < arrayOfClass.length)
    {
      Object localObject = arrayOfClass[i];
      int j = ((Integer)localVmPolicy.classInstanceLimit.get(localObject)).intValue();
      long l = arrayOfLong[i];
      if (l > j)
      {
        localObject = new InstanceCountViolation((Class)localObject, l, j);
        onVmPolicyViolation(((Throwable)localObject).getMessage(), (Throwable)localObject);
      }
      i += 1;
    }
  }
  
  public static boolean conditionallyEnableDebugLogging()
  {
    if (SystemProperties.getBoolean("persist.sys.strictmode.visual", false)) {
      if (amTheSystemServerProcess()) {
        i = 0;
      }
    }
    for (;;)
    {
      boolean bool = SystemProperties.getBoolean("persist.sys.strictmode.disable", false);
      if ((i != 0) || ((!IS_USER_BUILD) && (!bool))) {
        break;
      }
      setCloseGuardEnabled(false);
      return false;
      i = 1;
      continue;
      i = 0;
    }
    int j = i;
    if (IS_ENG_BUILD) {
      j = 1;
    }
    int i = 7;
    if (!IS_USER_BUILD) {
      i = 2097159;
    }
    int k = i;
    if (j != 0) {
      k = i | 0x100000;
    }
    setThreadPolicyMask(k);
    if (IS_USER_BUILD) {
      setCloseGuardEnabled(false);
    }
    for (;;)
    {
      return true;
      StrictMode.VmPolicy.Builder localBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyDropBox();
      if (IS_ENG_BUILD) {
        localBuilder.penaltyLog();
      }
      setVmPolicy(localBuilder.build());
      setCloseGuardEnabled(vmClosableObjectLeaksEnabled());
    }
  }
  
  public static void decrementExpectedActivityCount(Class paramClass)
  {
    if (paramClass == null) {
      return;
    }
    for (;;)
    {
      try
      {
        i = sVmPolicy.mask;
        if ((i & 0x400) == 0) {
          return;
        }
        Integer localInteger = (Integer)sExpectedActivityInstanceCount.get(paramClass);
        if (localInteger == null) {
          break label157;
        }
        if (localInteger.intValue() == 0)
        {
          break label157;
          if (i == 0)
          {
            sExpectedActivityInstanceCount.remove(paramClass);
            i += 1;
            if (InstanceTracker.getInstanceCount(paramClass) > i) {
              break label115;
            }
          }
        }
        else
        {
          i = localInteger.intValue() - 1;
          continue;
        }
        sExpectedActivityInstanceCount.put(paramClass, Integer.valueOf(i));
        continue;
        System.gc();
      }
      finally {}
      label115:
      System.runFinalization();
      System.gc();
      long l = VMDebug.countInstancesOfClass(paramClass, false);
      if (l > i)
      {
        paramClass = new InstanceCountViolation(paramClass, l, i);
        onVmPolicyViolation(paramClass.getMessage(), paramClass);
      }
      return;
      label157:
      int i = 0;
    }
  }
  
  public static void disableDeathOnFileUriExposure()
  {
    sVmPolicyMask &= 0xFBFFDFFF;
  }
  
  private static void dropboxViolationAsync(final int paramInt, final ViolationInfo paramViolationInfo)
  {
    int i = sDropboxCallsInFlight.incrementAndGet();
    if (i > 20)
    {
      sDropboxCallsInFlight.decrementAndGet();
      return;
    }
    if (LOG_V) {
      Log.d("StrictMode", "Dropboxing async; in-flight=" + i);
    }
    new Thread("callActivityManagerForStrictModeDropbox")
    {
      public void run()
      {
        Process.setThreadPriority(10);
        for (;;)
        {
          try
          {
            localIActivityManager = ActivityManagerNative.getDefault();
            if (localIActivityManager != null) {
              continue;
            }
            Log.d("StrictMode", "No activity manager; failed to Dropbox violation.");
          }
          catch (RemoteException localRemoteException)
          {
            IActivityManager localIActivityManager;
            int i;
            if ((localRemoteException instanceof DeadObjectException)) {
              continue;
            }
            Log.e("StrictMode", "RemoteException handling StrictMode violation", localRemoteException);
            continue;
          }
          i = StrictMode.-get3().decrementAndGet();
          if (StrictMode.-get1()) {
            Log.d("StrictMode", "Dropbox complete; in-flight=" + i);
          }
          return;
          localIActivityManager.handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), paramInt, paramViolationInfo);
        }
      }
    }.start();
  }
  
  public static void enableDeathOnFileUriExposure()
  {
    sVmPolicyMask |= 0x4002000;
  }
  
  public static void enableDeathOnNetwork()
  {
    setThreadPolicyMask(getThreadPolicyMask() | 0x4 | 0x1000000);
  }
  
  public static void enableDefaults()
  {
    setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
    setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }
  
  public static Span enterCriticalSpan(String paramString)
  {
    if (IS_USER_BUILD) {
      return NO_OP_SPAN;
    }
    if ((paramString == null) || (paramString.isEmpty())) {
      throw new IllegalArgumentException("name must be non-null and non-empty");
    }
    synchronized ((ThreadSpanState)sThisThreadSpanState.get())
    {
      if (???.mFreeListHead != null)
      {
        localSpan = ???.mFreeListHead;
        ???.mFreeListHead = Span.-get1(localSpan);
        ???.mFreeListSize -= 1;
        Span.-set1(localSpan, paramString);
        Span.-set0(localSpan, SystemClock.uptimeMillis());
        Span.-set2(localSpan, ???.mActiveHead);
        Span.-set3(localSpan, null);
        ???.mActiveHead = localSpan;
        ???.mActiveSize += 1;
        if (Span.-get1(localSpan) != null) {
          Span.-set3(Span.-get1(localSpan), localSpan);
        }
        if (LOG_V) {
          Log.d("StrictMode", "Span enter=" + paramString + "; size=" + ???.mActiveSize);
        }
        return localSpan;
      }
      Span localSpan = new Span(???);
    }
  }
  
  private static void executeDeathPenalty(ViolationInfo paramViolationInfo)
  {
    int i = parseViolationFromMessage(paramViolationInfo.crashInfo.exceptionMessage);
    throw new StrictModeViolation(paramViolationInfo.policy, i, null);
  }
  
  public static ThreadPolicy getThreadPolicy()
  {
    return new ThreadPolicy(getThreadPolicyMask(), null);
  }
  
  public static int getThreadPolicyMask()
  {
    return BlockGuard.getThreadPolicy().getPolicyMask();
  }
  
  public static VmPolicy getVmPolicy()
  {
    try
    {
      VmPolicy localVmPolicy = sVmPolicy;
      return localVmPolicy;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  static boolean hasGatheredViolations()
  {
    return gatheredViolations.get() != null;
  }
  
  /* Error */
  public static void incrementExpectedActivityCount(Class paramClass)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +4 -> 5
    //   4: return
    //   5: ldc 2
    //   7: monitorenter
    //   8: getstatic 289	android/os/StrictMode:sVmPolicy	Landroid/os/StrictMode$VmPolicy;
    //   11: getfield 458	android/os/StrictMode$VmPolicy:mask	I
    //   14: istore_1
    //   15: iload_1
    //   16: sipush 1024
    //   19: iand
    //   20: ifne +7 -> 27
    //   23: ldc 2
    //   25: monitorexit
    //   26: return
    //   27: getstatic 313	android/os/StrictMode:sExpectedActivityInstanceCount	Ljava/util/HashMap;
    //   30: aload_0
    //   31: invokevirtual 403	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   34: checkcast 405	java/lang/Integer
    //   37: astore_2
    //   38: aload_2
    //   39: ifnonnull +21 -> 60
    //   42: iconst_1
    //   43: istore_1
    //   44: getstatic 313	android/os/StrictMode:sExpectedActivityInstanceCount	Ljava/util/HashMap;
    //   47: aload_0
    //   48: iload_1
    //   49: invokestatic 469	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   52: invokevirtual 473	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   55: pop
    //   56: ldc 2
    //   58: monitorexit
    //   59: return
    //   60: aload_2
    //   61: invokevirtual 408	java/lang/Integer:intValue	()I
    //   64: istore_1
    //   65: iload_1
    //   66: iconst_1
    //   67: iadd
    //   68: istore_1
    //   69: goto -25 -> 44
    //   72: astore_0
    //   73: ldc 2
    //   75: monitorexit
    //   76: aload_0
    //   77: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	78	0	paramClass	Class
    //   14	55	1	i	int
    //   37	24	2	localInteger	Integer
    // Exception table:
    //   from	to	target	type
    //   8	15	72	finally
    //   27	38	72	finally
    //   44	56	72	finally
    //   60	65	72	finally
  }
  
  public static void noteDiskRead()
  {
    BlockGuard.Policy localPolicy = BlockGuard.getThreadPolicy();
    if (!(localPolicy instanceof AndroidBlockGuardPolicy)) {
      return;
    }
    ((AndroidBlockGuardPolicy)localPolicy).onReadFromDisk();
  }
  
  public static void noteDiskWrite()
  {
    BlockGuard.Policy localPolicy = BlockGuard.getThreadPolicy();
    if (!(localPolicy instanceof AndroidBlockGuardPolicy)) {
      return;
    }
    ((AndroidBlockGuardPolicy)localPolicy).onWriteToDisk();
  }
  
  public static void noteResourceMismatch(Object paramObject)
  {
    BlockGuard.Policy localPolicy = BlockGuard.getThreadPolicy();
    if (!(localPolicy instanceof AndroidBlockGuardPolicy)) {
      return;
    }
    ((AndroidBlockGuardPolicy)localPolicy).onResourceMismatch(paramObject);
  }
  
  public static void noteSlowCall(String paramString)
  {
    BlockGuard.Policy localPolicy = BlockGuard.getThreadPolicy();
    if (!(localPolicy instanceof AndroidBlockGuardPolicy)) {
      return;
    }
    ((AndroidBlockGuardPolicy)localPolicy).onCustomSlowCall(paramString);
  }
  
  private static void onBinderStrictModePolicyChange(int paramInt)
  {
    setBlockGuardPolicy(paramInt);
  }
  
  public static void onCleartextNetworkDetected(byte[] paramArrayOfByte)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramArrayOfByte != null)
    {
      if ((paramArrayOfByte.length < 20) || ((paramArrayOfByte[0] & 0xF0) != 64)) {
        break label143;
      }
      localObject1 = new byte[4];
      System.arraycopy(paramArrayOfByte, 16, (byte[])localObject1, 0, 4);
    }
    for (;;)
    {
      int i = Process.myUid();
      String str = "Detected cleartext network traffic from UID " + i;
      localObject2 = str;
      if (localObject1 != null) {}
      try
      {
        localObject2 = "Detected cleartext network traffic from UID " + i + " to " + InetAddress.getByAddress((byte[])localObject1);
        if ((sVmPolicyMask & 0x2000000) != 0) {}
        for (boolean bool = true;; bool = false)
        {
          onVmPolicyViolation(HexDump.dumpHexString(paramArrayOfByte).trim(), new Throwable((String)localObject2), bool);
          return;
          label143:
          localObject1 = localObject2;
          if (paramArrayOfByte.length < 40) {
            break;
          }
          localObject1 = localObject2;
          if ((paramArrayOfByte[0] & 0xF0) != 96) {
            break;
          }
          localObject1 = new byte[16];
          System.arraycopy(paramArrayOfByte, 24, (byte[])localObject1, 0, 16);
          break;
        }
      }
      catch (UnknownHostException localUnknownHostException)
      {
        for (;;)
        {
          localObject2 = str;
        }
      }
    }
  }
  
  public static void onFileUriExposed(Uri paramUri, String paramString)
  {
    paramUri = paramUri + " exposed beyond app through " + paramString;
    if ((sVmPolicyMask & 0x4000000) != 0) {
      throw new FileUriExposedException(paramUri);
    }
    onVmPolicyViolation(null, new Throwable(paramUri));
  }
  
  public static void onIntentReceiverLeaked(Throwable paramThrowable)
  {
    onVmPolicyViolation(null, paramThrowable);
  }
  
  public static void onServiceConnectionLeaked(Throwable paramThrowable)
  {
    onVmPolicyViolation(null, paramThrowable);
  }
  
  public static void onSqliteObjectLeaked(String paramString, Throwable paramThrowable)
  {
    onVmPolicyViolation(paramString, paramThrowable);
  }
  
  public static void onVmPolicyViolation(String paramString, Throwable paramThrowable)
  {
    onVmPolicyViolation(paramString, paramThrowable, false);
  }
  
  public static void onVmPolicyViolation(String paramString, Throwable paramThrowable, boolean paramBoolean)
  {
    if ((sVmPolicyMask & 0x200000) != 0) {
      i = 1;
    }
    label20:
    int j;
    ViolationInfo localViolationInfo;
    for (;;)
    {
      Integer localInteger;
      long l3;
      long l1;
      long l2;
      if ((sVmPolicyMask & 0x40000) == 0)
      {
        if ((sVmPolicyMask & 0x10000) == 0) {
          break label270;
        }
        j = 1;
        localViolationInfo = new ViolationInfo(paramString, paramThrowable, sVmPolicyMask);
        localViolationInfo.numAnimationsRunning = 0;
        localViolationInfo.tags = null;
        localViolationInfo.broadcastIntentAction = null;
        localInteger = Integer.valueOf(localViolationInfo.hashCode());
        l3 = SystemClock.uptimeMillis();
        l1 = 0L;
        l2 = Long.MAX_VALUE;
      }
      synchronized (sLastVmViolationTime)
      {
        if (sLastVmViolationTime.containsKey(localInteger))
        {
          l1 = ((Long)sLastVmViolationTime.get(localInteger)).longValue();
          l2 = l3 - l1;
        }
        if (l2 > 1000L) {
          sLastVmViolationTime.put(localInteger, Long.valueOf(l3));
        }
        if ((j != 0) && (l2 > 1000L)) {
          Log.e("StrictMode", paramString, paramThrowable);
        }
        j = 0x200000 | sVmPolicyMask & 0x7F00;
        if ((i == 0) || (paramBoolean)) {
          if ((i != 0) && (l1 == 0L)) {
            i = getThreadPolicyMask();
          }
        }
        try
        {
          setThreadPolicyMask(0);
          ActivityManagerNative.getDefault().handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), j, localViolationInfo);
        }
        catch (RemoteException paramString)
        {
          boolean bool = paramString instanceof DeadObjectException;
          if (!bool) {
            break label309;
          }
          for (;;)
          {
            setThreadPolicyMask(i);
            break;
            Log.e("StrictMode", "RemoteException trying to handle StrictMode violation", paramString);
          }
        }
        finally
        {
          setThreadPolicyMask(i);
        }
        if (paramBoolean)
        {
          System.err.println("StrictMode VmPolicy violation with POLICY_DEATH; shutting down.");
          Process.killProcess(Process.myPid());
          System.exit(10);
        }
        return;
        i = 0;
        continue;
        paramBoolean = true;
        break label20;
        label270:
        j = 0;
      }
    }
    dropboxViolationAsync(j, localViolationInfo);
  }
  
  public static void onWebViewMethodCalledOnWrongThread(Throwable paramThrowable)
  {
    onVmPolicyViolation(null, paramThrowable);
  }
  
  private static int parsePolicyFromMessage(String paramString)
  {
    int i;
    if ((paramString != null) && (paramString.startsWith("policy=")))
    {
      i = paramString.indexOf(' ');
      if (i == -1) {
        return 0;
      }
    }
    else
    {
      return 0;
    }
    paramString = paramString.substring(7, i);
    try
    {
      i = Integer.parseInt(paramString);
      return i;
    }
    catch (NumberFormatException paramString) {}
    return 0;
  }
  
  private static int parseViolationFromMessage(String paramString)
  {
    if (paramString == null) {
      return 0;
    }
    int i = paramString.indexOf("violation=");
    if (i == -1) {
      return 0;
    }
    int k = i + "violation=".length();
    int j = paramString.indexOf(' ', k);
    i = j;
    if (j == -1) {
      i = paramString.length();
    }
    paramString = paramString.substring(k, i);
    try
    {
      i = Integer.parseInt(paramString);
      return i;
    }
    catch (NumberFormatException paramString) {}
    return 0;
  }
  
  static void readAndHandleBinderCallViolations(Parcel paramParcel)
  {
    Object localObject1 = new StringWriter();
    Object localObject2 = new FastPrintWriter((Writer)localObject1, false, 256);
    new LogStackTrace(null).printStackTrace((PrintWriter)localObject2);
    ((PrintWriter)localObject2).flush();
    localObject1 = ((StringWriter)localObject1).toString();
    int k = getThreadPolicyMask();
    int i;
    int m;
    int j;
    if ((0x400000 & k) != 0)
    {
      i = 1;
      m = paramParcel.readInt();
      j = 0;
    }
    for (;;)
    {
      if (j >= m) {
        return;
      }
      if (LOG_V) {
        Log.d("StrictMode", "strict mode violation stacks read from binder call.  i=" + j);
      }
      if (i != 0)
      {
        bool = false;
        label115:
        localObject2 = new ViolationInfo(paramParcel, bool);
        if ((((ViolationInfo)localObject2).crashInfo.stackTrace == null) || (((ViolationInfo)localObject2).crashInfo.stackTrace.length() <= 30000)) {
          break label279;
        }
        localObject1 = ((ViolationInfo)localObject2).crashInfo.stackTrace.substring(0, 256);
        j += 1;
        label176:
        if (j >= m) {
          break label224;
        }
        if (i == 0) {
          break label218;
        }
      }
      label218:
      for (boolean bool = false;; bool = true)
      {
        new ViolationInfo(paramParcel, bool);
        j += 1;
        break label176;
        i = 0;
        break;
        bool = true;
        break label115;
      }
      label224:
      clearGatheredViolations();
      Slog.wtfStack("StrictMode", "Stack is too large: numViolations=" + m + " policy=#" + Integer.toHexString(k) + " front=" + (String)localObject1);
      return;
      label279:
      Object localObject3 = ((ViolationInfo)localObject2).crashInfo;
      ((ApplicationErrorReport.CrashInfo)localObject3).stackTrace = (((ApplicationErrorReport.CrashInfo)localObject3).stackTrace + "# via Binder call with stack:\n" + (String)localObject1);
      localObject3 = BlockGuard.getThreadPolicy();
      if ((localObject3 instanceof AndroidBlockGuardPolicy)) {
        ((AndroidBlockGuardPolicy)localObject3).handleViolationWithTimingAttempt((ViolationInfo)localObject2);
      }
      j += 1;
    }
  }
  
  private static void setBlockGuardPolicy(int paramInt)
  {
    if (paramInt == 0)
    {
      BlockGuard.setThreadPolicy(BlockGuard.LAX_POLICY);
      return;
    }
    Object localObject = BlockGuard.getThreadPolicy();
    if ((localObject instanceof AndroidBlockGuardPolicy)) {
      localObject = (AndroidBlockGuardPolicy)localObject;
    }
    for (;;)
    {
      ((AndroidBlockGuardPolicy)localObject).setPolicyMask(paramInt);
      return;
      localObject = (AndroidBlockGuardPolicy)threadAndroidPolicy.get();
      BlockGuard.setThreadPolicy((BlockGuard.Policy)localObject);
    }
  }
  
  private static void setCloseGuardEnabled(boolean paramBoolean)
  {
    if (!(CloseGuard.getReporter() instanceof AndroidCloseGuardReporter)) {
      CloseGuard.setReporter(new AndroidCloseGuardReporter(null));
    }
    CloseGuard.setEnabled(paramBoolean);
  }
  
  public static void setThreadPolicy(ThreadPolicy paramThreadPolicy)
  {
    setThreadPolicyMask(paramThreadPolicy.mask);
  }
  
  private static void setThreadPolicyMask(int paramInt)
  {
    setBlockGuardPolicy(paramInt);
    Binder.setThreadStrictModePolicy(paramInt);
  }
  
  /* Error */
  public static void setVmPolicy(VmPolicy paramVmPolicy)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: putstatic 289	android/os/StrictMode:sVmPolicy	Landroid/os/StrictMode$VmPolicy;
    //   7: aload_0
    //   8: getfield 458	android/os/StrictMode$VmPolicy:mask	I
    //   11: putstatic 284	android/os/StrictMode:sVmPolicyMask	I
    //   14: invokestatic 453	android/os/StrictMode:vmClosableObjectLeaksEnabled	()Z
    //   17: invokestatic 431	android/os/StrictMode:setCloseGuardEnabled	(Z)V
    //   20: invokestatic 877	android/os/Looper:getMainLooper	()Landroid/os/Looper;
    //   23: astore_2
    //   24: aload_2
    //   25: ifnull +38 -> 63
    //   28: aload_2
    //   29: getfield 881	android/os/Looper:mQueue	Landroid/os/MessageQueue;
    //   32: astore_2
    //   33: aload_0
    //   34: getfield 368	android/os/StrictMode$VmPolicy:classInstanceLimit	Ljava/util/HashMap;
    //   37: invokevirtual 371	java/util/HashMap:size	()I
    //   40: ifeq +12 -> 52
    //   43: getstatic 284	android/os/StrictMode:sVmPolicyMask	I
    //   46: ldc -91
    //   48: iand
    //   49: ifne +75 -> 124
    //   52: aload_2
    //   53: getstatic 304	android/os/StrictMode:sProcessIdleHandler	Landroid/os/MessageQueue$IdleHandler;
    //   56: invokevirtual 887	android/os/MessageQueue:removeIdleHandler	(Landroid/os/MessageQueue$IdleHandler;)V
    //   59: iconst_0
    //   60: putstatic 301	android/os/StrictMode:sIsIdlerRegistered	Z
    //   63: iconst_0
    //   64: istore_1
    //   65: getstatic 284	android/os/StrictMode:sVmPolicyMask	I
    //   68: sipush 16384
    //   71: iand
    //   72: ifeq +24 -> 96
    //   75: getstatic 284	android/os/StrictMode:sVmPolicyMask	I
    //   78: ldc -117
    //   80: iand
    //   81: ifne +94 -> 175
    //   84: getstatic 284	android/os/StrictMode:sVmPolicyMask	I
    //   87: ldc -115
    //   89: iand
    //   90: ifeq +60 -> 150
    //   93: goto +82 -> 175
    //   96: ldc_w 889
    //   99: invokestatic 895	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   102: invokestatic 901	android/os/INetworkManagementService$Stub:asInterface	(Landroid/os/IBinder;)Landroid/os/INetworkManagementService;
    //   105: astore_0
    //   106: aload_0
    //   107: ifnull +48 -> 155
    //   110: aload_0
    //   111: invokestatic 330	android/os/Process:myUid	()I
    //   114: iload_1
    //   115: invokeinterface 907 3 0
    //   120: ldc 2
    //   122: monitorexit
    //   123: return
    //   124: getstatic 301	android/os/StrictMode:sIsIdlerRegistered	Z
    //   127: ifne -64 -> 63
    //   130: aload_2
    //   131: getstatic 304	android/os/StrictMode:sProcessIdleHandler	Landroid/os/MessageQueue$IdleHandler;
    //   134: invokevirtual 910	android/os/MessageQueue:addIdleHandler	(Landroid/os/MessageQueue$IdleHandler;)V
    //   137: iconst_1
    //   138: putstatic 301	android/os/StrictMode:sIsIdlerRegistered	Z
    //   141: goto -78 -> 63
    //   144: astore_0
    //   145: ldc 2
    //   147: monitorexit
    //   148: aload_0
    //   149: athrow
    //   150: iconst_1
    //   151: istore_1
    //   152: goto -56 -> 96
    //   155: iload_1
    //   156: ifeq -36 -> 120
    //   159: ldc -98
    //   161: ldc_w 912
    //   164: invokestatic 915	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   167: pop
    //   168: goto -48 -> 120
    //   171: astore_0
    //   172: goto -52 -> 120
    //   175: iconst_2
    //   176: istore_1
    //   177: goto -81 -> 96
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	paramVmPolicy	VmPolicy
    //   64	113	1	i	int
    //   23	108	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	24	144	finally
    //   28	52	144	finally
    //   52	63	144	finally
    //   65	93	144	finally
    //   96	106	144	finally
    //   110	120	144	finally
    //   124	141	144	finally
    //   159	168	144	finally
    //   110	120	171	android/os/RemoteException
  }
  
  private static boolean tooManyViolationsThisLoop()
  {
    return ((ArrayList)violationsBeingTimed.get()).size() >= 10;
  }
  
  public static Object trackActivity(Object paramObject)
  {
    return new InstanceTracker(paramObject);
  }
  
  public static boolean vmCleartextNetworkEnabled()
  {
    boolean bool = false;
    if ((sVmPolicyMask & 0x4000) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean vmClosableObjectLeaksEnabled()
  {
    boolean bool = false;
    if ((sVmPolicyMask & 0x200) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean vmFileUriExposureEnabled()
  {
    boolean bool = false;
    if ((sVmPolicyMask & 0x2000) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean vmRegistrationLeaksEnabled()
  {
    boolean bool = false;
    if ((sVmPolicyMask & 0x1000) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean vmSqliteObjectLeaksEnabled()
  {
    boolean bool = false;
    if ((sVmPolicyMask & 0x100) != 0) {
      bool = true;
    }
    return bool;
  }
  
  static void writeGatheredViolationsToParcel(Parcel paramParcel)
  {
    ArrayList localArrayList = (ArrayList)gatheredViolations.get();
    if (localArrayList == null) {
      paramParcel.writeInt(0);
    }
    for (;;)
    {
      gatheredViolations.set(null);
      return;
      paramParcel.writeInt(localArrayList.size());
      int i = 0;
      while (i < localArrayList.size())
      {
        int j = paramParcel.dataPosition();
        ((ViolationInfo)localArrayList.get(i)).writeToParcel(paramParcel, 0);
        if (paramParcel.dataPosition() - j > 10240) {
          Slog.d("StrictMode", "Wrote violation #" + i + " of " + localArrayList.size() + ": " + (paramParcel.dataPosition() - j) + " bytes");
        }
        i += 1;
      }
      if (LOG_V) {
        Log.d("StrictMode", "wrote violations to response parcel; num=" + localArrayList.size());
      }
      localArrayList.clear();
    }
  }
  
  private static class AndroidBlockGuardPolicy
    implements BlockGuard.Policy
  {
    private ArrayMap<Integer, Long> mLastViolationTime;
    private int mPolicyMask;
    
    public AndroidBlockGuardPolicy(int paramInt)
    {
      this.mPolicyMask = paramInt;
    }
    
    public int getPolicyMask()
    {
      return this.mPolicyMask;
    }
    
    void handleViolation(StrictMode.ViolationInfo paramViolationInfo)
    {
      if ((paramViolationInfo == null) || (paramViolationInfo.crashInfo == null)) {}
      while (paramViolationInfo.crashInfo.stackTrace == null)
      {
        Log.wtf("StrictMode", "unexpected null stacktrace");
        return;
      }
      if (StrictMode.-get1()) {
        Log.d("StrictMode", "handleViolation; policy=" + paramViolationInfo.policy);
      }
      Object localObject2;
      if ((paramViolationInfo.policy & 0x400000) != 0)
      {
        localObject2 = (ArrayList)StrictMode.-get2().get();
        if (localObject2 == null)
        {
          localObject1 = new ArrayList(1);
          StrictMode.-get2().set(localObject1);
        }
        do
        {
          localObject2 = ((Iterable)localObject1).iterator();
          StrictMode.ViolationInfo localViolationInfo;
          do
          {
            if (!((Iterator)localObject2).hasNext()) {
              break;
            }
            localViolationInfo = (StrictMode.ViolationInfo)((Iterator)localObject2).next();
          } while (!paramViolationInfo.crashInfo.stackTrace.equals(localViolationInfo.crashInfo.stackTrace));
          return;
          localObject1 = localObject2;
        } while (((ArrayList)localObject2).size() < 5);
        return;
        ((ArrayList)localObject1).add(paramViolationInfo);
        return;
      }
      Object localObject1 = Integer.valueOf(paramViolationInfo.hashCode());
      long l1 = 0L;
      long l2;
      label260:
      label330:
      int j;
      if (this.mLastViolationTime != null)
      {
        localObject2 = (Long)this.mLastViolationTime.get(localObject1);
        if (localObject2 != null) {
          l1 = ((Long)localObject2).longValue();
        }
        l2 = SystemClock.uptimeMillis();
        this.mLastViolationTime.put(localObject1, Long.valueOf(l2));
        if (l1 != 0L) {
          break label445;
        }
        l2 = Long.MAX_VALUE;
        if (((paramViolationInfo.policy & 0x10000) != 0) && (l2 > 1000L))
        {
          if (paramViolationInfo.durationMillis == -1) {
            break label455;
          }
          Log.d("StrictMode", "StrictMode policy violation; ~duration=" + paramViolationInfo.durationMillis + " ms: " + paramViolationInfo.crashInfo.stackTrace);
        }
        j = 0;
        i = j;
        if ((paramViolationInfo.policy & 0x20000) != 0)
        {
          i = j;
          if (l2 > 30000L) {
            i = 131072;
          }
        }
        j = i;
        if ((paramViolationInfo.policy & 0x200000) != 0)
        {
          j = i;
          if (l1 == 0L) {
            j = i | 0x200000;
          }
        }
        if (j == 0) {
          break label516;
        }
        j |= StrictMode.-wrap1(paramViolationInfo.crashInfo.exceptionMessage);
        k = StrictMode.getThreadPolicyMask();
        if ((paramViolationInfo.policy & 0x1770000) != 2097152) {
          break label489;
        }
      }
      label445:
      label455:
      label489:
      for (int i = 1;; i = 0)
      {
        if (i == 0) {
          break label494;
        }
        StrictMode.-wrap2(j, paramViolationInfo);
        return;
        this.mLastViolationTime = new ArrayMap(1);
        break;
        l2 -= l1;
        break label260;
        Log.d("StrictMode", "StrictMode policy violation: " + paramViolationInfo.crashInfo.stackTrace);
        break label330;
      }
      try
      {
        label494:
        StrictMode.-wrap4(0);
        ActivityManagerNative.getDefault().handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), j, paramViolationInfo);
      }
      catch (RemoteException localRemoteException)
      {
        label516:
        boolean bool = localRemoteException instanceof DeadObjectException;
        if (!bool) {
          break label553;
        }
        for (;;)
        {
          StrictMode.-wrap4(k);
          break;
          Log.e("StrictMode", "RemoteException trying to handle StrictMode violation", localRemoteException);
        }
      }
      finally
      {
        StrictMode.-wrap4(k);
      }
      if ((paramViolationInfo.policy & 0x40000) != 0) {
        StrictMode.-wrap3(paramViolationInfo);
      }
    }
    
    void handleViolationWithTimingAttempt(StrictMode.ViolationInfo paramViolationInfo)
    {
      final IWindowManager localIWindowManager = null;
      if ((Looper.myLooper() == null) || ((paramViolationInfo.policy & 0x1770000) == 262144))
      {
        paramViolationInfo.durationMillis = -1;
        handleViolation(paramViolationInfo);
        return;
      }
      final ArrayList localArrayList = (ArrayList)StrictMode.-get8().get();
      if (localArrayList.size() >= 10) {
        return;
      }
      localArrayList.add(paramViolationInfo);
      if (localArrayList.size() > 1) {
        return;
      }
      if ((paramViolationInfo.policy & 0x100000) != 0) {
        localIWindowManager = (IWindowManager)StrictMode.-get6().get();
      }
      if (localIWindowManager != null) {}
      try
      {
        localIWindowManager.showStrictModeViolation(true);
        ((Handler)StrictMode.-get7().get()).postAtFrontOfQueue(new Runnable()
        {
          public void run()
          {
            long l = SystemClock.uptimeMillis();
            if (localIWindowManager != null) {}
            try
            {
              localIWindowManager.showStrictModeViolation(false);
              int i = 0;
              while (i < localArrayList.size())
              {
                StrictMode.ViolationInfo localViolationInfo = (StrictMode.ViolationInfo)localArrayList.get(i);
                localViolationInfo.violationNumThisLoop = (i + 1);
                localViolationInfo.durationMillis = ((int)(l - localViolationInfo.violationUptimeMillis));
                StrictMode.AndroidBlockGuardPolicy.this.handleViolation(localViolationInfo);
                i += 1;
              }
              localArrayList.clear();
              return;
            }
            catch (RemoteException localRemoteException)
            {
              for (;;) {}
            }
          }
        });
        return;
      }
      catch (RemoteException paramViolationInfo)
      {
        for (;;) {}
      }
    }
    
    void onCustomSlowCall(String paramString)
    {
      if ((this.mPolicyMask & 0x8) == 0) {
        return;
      }
      if (StrictMode.-wrap0()) {
        return;
      }
      paramString = new StrictMode.StrictModeCustomViolation(this.mPolicyMask, paramString);
      paramString.fillInStackTrace();
      startHandlingViolationException(paramString);
    }
    
    public void onNetwork()
    {
      if ((this.mPolicyMask & 0x4) == 0) {
        return;
      }
      if ((this.mPolicyMask & 0x1000000) != 0) {
        throw new NetworkOnMainThreadException();
      }
      if (StrictMode.-wrap0()) {
        return;
      }
      StrictMode.StrictModeNetworkViolation localStrictModeNetworkViolation = new StrictMode.StrictModeNetworkViolation(this.mPolicyMask);
      localStrictModeNetworkViolation.fillInStackTrace();
      startHandlingViolationException(localStrictModeNetworkViolation);
    }
    
    public void onReadFromDisk()
    {
      if ((this.mPolicyMask & 0x2) == 0) {
        return;
      }
      if (StrictMode.-wrap0()) {
        return;
      }
      StrictMode.StrictModeDiskReadViolation localStrictModeDiskReadViolation = new StrictMode.StrictModeDiskReadViolation(this.mPolicyMask);
      localStrictModeDiskReadViolation.fillInStackTrace();
      startHandlingViolationException(localStrictModeDiskReadViolation);
    }
    
    void onResourceMismatch(Object paramObject)
    {
      if ((this.mPolicyMask & 0x10) == 0) {
        return;
      }
      if (StrictMode.-wrap0()) {
        return;
      }
      paramObject = new StrictMode.StrictModeResourceMismatchViolation(this.mPolicyMask, paramObject);
      ((BlockGuard.BlockGuardPolicyException)paramObject).fillInStackTrace();
      startHandlingViolationException((BlockGuard.BlockGuardPolicyException)paramObject);
    }
    
    public void onWriteToDisk()
    {
      if ((this.mPolicyMask & 0x1) == 0) {
        return;
      }
      if (StrictMode.-wrap0()) {
        return;
      }
      StrictMode.StrictModeDiskWriteViolation localStrictModeDiskWriteViolation = new StrictMode.StrictModeDiskWriteViolation(this.mPolicyMask);
      localStrictModeDiskWriteViolation.fillInStackTrace();
      startHandlingViolationException(localStrictModeDiskWriteViolation);
    }
    
    public void setPolicyMask(int paramInt)
    {
      this.mPolicyMask = paramInt;
    }
    
    void startHandlingViolationException(BlockGuard.BlockGuardPolicyException paramBlockGuardPolicyException)
    {
      paramBlockGuardPolicyException = new StrictMode.ViolationInfo(paramBlockGuardPolicyException, paramBlockGuardPolicyException.getPolicy());
      paramBlockGuardPolicyException.violationUptimeMillis = SystemClock.uptimeMillis();
      handleViolationWithTimingAttempt(paramBlockGuardPolicyException);
    }
    
    public String toString()
    {
      return "AndroidBlockGuardPolicy; mPolicyMask=" + this.mPolicyMask;
    }
  }
  
  private static class AndroidCloseGuardReporter
    implements CloseGuard.Reporter
  {
    public void report(String paramString, Throwable paramThrowable)
    {
      StrictMode.onVmPolicyViolation(paramString, paramThrowable);
    }
  }
  
  private static class InstanceCountViolation
    extends Throwable
  {
    private static final StackTraceElement[] FAKE_STACK = { new StackTraceElement("android.os.StrictMode", "setClassInstanceLimit", "StrictMode.java", 1) };
    final Class mClass;
    final long mInstances;
    final int mLimit;
    
    public InstanceCountViolation(Class paramClass, long paramLong, int paramInt)
    {
      super();
      setStackTrace(FAKE_STACK);
      this.mClass = paramClass;
      this.mInstances = paramLong;
      this.mLimit = paramInt;
    }
  }
  
  private static final class InstanceTracker
  {
    private static final HashMap<Class<?>, Integer> sInstanceCounts = new HashMap();
    private final Class<?> mKlass;
    
    public InstanceTracker(Object arg1)
    {
      this.mKlass = ???.getClass();
      synchronized (sInstanceCounts)
      {
        Integer localInteger = (Integer)sInstanceCounts.get(this.mKlass);
        if (localInteger != null)
        {
          i = localInteger.intValue() + 1;
          sInstanceCounts.put(this.mKlass, Integer.valueOf(i));
          return;
        }
        int i = 1;
      }
    }
    
    public static int getInstanceCount(Class<?> paramClass)
    {
      synchronized (sInstanceCounts)
      {
        paramClass = (Integer)sInstanceCounts.get(paramClass);
        if (paramClass != null)
        {
          i = paramClass.intValue();
          return i;
        }
        int i = 0;
      }
    }
    
    /* Error */
    protected void finalize()
      throws Throwable
    {
      // Byte code:
      //   0: getstatic 22	android/os/StrictMode$InstanceTracker:sInstanceCounts	Ljava/util/HashMap;
      //   3: astore_2
      //   4: aload_2
      //   5: monitorenter
      //   6: getstatic 22	android/os/StrictMode$InstanceTracker:sInstanceCounts	Ljava/util/HashMap;
      //   9: aload_0
      //   10: getfield 31	android/os/StrictMode$InstanceTracker:mKlass	Ljava/lang/Class;
      //   13: invokevirtual 35	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   16: checkcast 37	java/lang/Integer
      //   19: astore_3
      //   20: aload_3
      //   21: ifnull +29 -> 50
      //   24: aload_3
      //   25: invokevirtual 41	java/lang/Integer:intValue	()I
      //   28: iconst_1
      //   29: isub
      //   30: istore_1
      //   31: iload_1
      //   32: ifle +25 -> 57
      //   35: getstatic 22	android/os/StrictMode$InstanceTracker:sInstanceCounts	Ljava/util/HashMap;
      //   38: aload_0
      //   39: getfield 31	android/os/StrictMode$InstanceTracker:mKlass	Ljava/lang/Class;
      //   42: iload_1
      //   43: invokestatic 45	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   46: invokevirtual 49	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   49: pop
      //   50: aload_2
      //   51: monitorexit
      //   52: aload_0
      //   53: invokespecial 58	java/lang/Object:finalize	()V
      //   56: return
      //   57: getstatic 22	android/os/StrictMode$InstanceTracker:sInstanceCounts	Ljava/util/HashMap;
      //   60: aload_0
      //   61: getfield 31	android/os/StrictMode$InstanceTracker:mKlass	Ljava/lang/Class;
      //   64: invokevirtual 61	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
      //   67: pop
      //   68: goto -18 -> 50
      //   71: astore_3
      //   72: aload_2
      //   73: monitorexit
      //   74: aload_3
      //   75: athrow
      //   76: astore_2
      //   77: aload_0
      //   78: invokespecial 58	java/lang/Object:finalize	()V
      //   81: aload_2
      //   82: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	83	0	this	InstanceTracker
      //   30	13	1	i	int
      //   76	6	2	localObject1	Object
      //   19	6	3	localInteger	Integer
      //   71	4	3	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   6	20	71	finally
      //   24	31	71	finally
      //   35	50	71	finally
      //   57	68	71	finally
      //   0	6	76	finally
      //   50	52	76	finally
      //   72	76	76	finally
    }
  }
  
  private static class LogStackTrace
    extends Exception
  {}
  
  public static class Span
  {
    private final StrictMode.ThreadSpanState mContainerState;
    private long mCreateMillis;
    private String mName;
    private Span mNext;
    private Span mPrev;
    
    protected Span()
    {
      this.mContainerState = null;
    }
    
    Span(StrictMode.ThreadSpanState paramThreadSpanState)
    {
      this.mContainerState = paramThreadSpanState;
    }
    
    public void finish()
    {
      synchronized (this.mContainerState)
      {
        String str = this.mName;
        if (str == null) {
          return;
        }
        if (this.mPrev != null) {
          this.mPrev.mNext = this.mNext;
        }
        if (this.mNext != null) {
          this.mNext.mPrev = this.mPrev;
        }
        if (???.mActiveHead == this) {
          ???.mActiveHead = this.mNext;
        }
        ???.mActiveSize -= 1;
        if (StrictMode.-get1()) {
          Log.d("StrictMode", "Span finished=" + this.mName + "; size=" + ???.mActiveSize);
        }
        this.mCreateMillis = -1L;
        this.mName = null;
        this.mPrev = null;
        this.mNext = null;
        if (???.mFreeListSize < 5)
        {
          this.mNext = ???.mFreeListHead;
          ???.mFreeListHead = this;
          ???.mFreeListSize += 1;
        }
        return;
      }
    }
  }
  
  private static class StrictModeCustomViolation
    extends StrictMode.StrictModeViolation
  {
    public StrictModeCustomViolation(int paramInt, String paramString)
    {
      super(8, paramString);
    }
  }
  
  private static class StrictModeDiskReadViolation
    extends StrictMode.StrictModeViolation
  {
    public StrictModeDiskReadViolation(int paramInt)
    {
      super(2, null);
    }
  }
  
  private static class StrictModeDiskWriteViolation
    extends StrictMode.StrictModeViolation
  {
    public StrictModeDiskWriteViolation(int paramInt)
    {
      super(1, null);
    }
  }
  
  public static class StrictModeNetworkViolation
    extends StrictMode.StrictModeViolation
  {
    public StrictModeNetworkViolation(int paramInt)
    {
      super(4, null);
    }
  }
  
  private static class StrictModeResourceMismatchViolation
    extends StrictMode.StrictModeViolation
  {
    public StrictModeResourceMismatchViolation(int paramInt, Object paramObject)
    {
      super(16, str);
    }
  }
  
  public static class StrictModeViolation
    extends BlockGuard.BlockGuardPolicyException
  {
    public StrictModeViolation(int paramInt1, int paramInt2, String paramString)
    {
      super(paramInt2, paramString);
    }
  }
  
  public static final class ThreadPolicy
  {
    public static final ThreadPolicy LAX = new ThreadPolicy(0);
    final int mask;
    
    private ThreadPolicy(int paramInt)
    {
      this.mask = paramInt;
    }
    
    public String toString()
    {
      return "[StrictMode.ThreadPolicy; mask=" + this.mask + "]";
    }
    
    public static final class Builder
    {
      private int mMask = 0;
      
      public Builder()
      {
        this.mMask = 0;
      }
      
      public Builder(StrictMode.ThreadPolicy paramThreadPolicy)
      {
        this.mMask = paramThreadPolicy.mask;
      }
      
      private Builder disable(int paramInt)
      {
        this.mMask &= paramInt;
        return this;
      }
      
      private Builder enable(int paramInt)
      {
        this.mMask |= paramInt;
        return this;
      }
      
      public StrictMode.ThreadPolicy build()
      {
        if ((this.mMask != 0) && ((this.mMask & 0x270000) == 0)) {
          penaltyLog();
        }
        return new StrictMode.ThreadPolicy(this.mMask, null);
      }
      
      public Builder detectAll()
      {
        return enable(31);
      }
      
      public Builder detectCustomSlowCalls()
      {
        return enable(8);
      }
      
      public Builder detectDiskReads()
      {
        return enable(2);
      }
      
      public Builder detectDiskWrites()
      {
        return enable(1);
      }
      
      public Builder detectNetwork()
      {
        return enable(4);
      }
      
      public Builder detectResourceMismatches()
      {
        return enable(16);
      }
      
      public Builder penaltyDeath()
      {
        return enable(262144);
      }
      
      public Builder penaltyDeathOnNetwork()
      {
        return enable(16777216);
      }
      
      public Builder penaltyDialog()
      {
        return enable(131072);
      }
      
      public Builder penaltyDropBox()
      {
        return enable(2097152);
      }
      
      public Builder penaltyFlashScreen()
      {
        return enable(1048576);
      }
      
      public Builder penaltyLog()
      {
        return enable(65536);
      }
      
      public Builder permitAll()
      {
        return disable(31);
      }
      
      public Builder permitCustomSlowCalls()
      {
        return disable(8);
      }
      
      public Builder permitDiskReads()
      {
        return disable(2);
      }
      
      public Builder permitDiskWrites()
      {
        return disable(1);
      }
      
      public Builder permitNetwork()
      {
        return disable(4);
      }
      
      public Builder permitResourceMismatches()
      {
        return disable(16);
      }
    }
  }
  
  private static class ThreadSpanState
  {
    public StrictMode.Span mActiveHead;
    public int mActiveSize;
    public StrictMode.Span mFreeListHead;
    public int mFreeListSize;
  }
  
  public static class ViolationInfo
  {
    public String broadcastIntentAction;
    public final ApplicationErrorReport.CrashInfo crashInfo;
    public int durationMillis = -1;
    public String message;
    public int numAnimationsRunning = 0;
    public long numInstances = -1L;
    public final int policy;
    public String[] tags;
    public int violationNumThisLoop;
    public long violationUptimeMillis;
    
    public ViolationInfo()
    {
      this.crashInfo = null;
      this.policy = 0;
    }
    
    public ViolationInfo(Parcel paramParcel)
    {
      this(paramParcel, false);
    }
    
    public ViolationInfo(Parcel paramParcel, boolean paramBoolean)
    {
      this.message = paramParcel.readString();
      this.crashInfo = new ApplicationErrorReport.CrashInfo(paramParcel);
      int i = paramParcel.readInt();
      if (paramBoolean) {}
      for (this.policy = (0xFFBFFFFF & i);; this.policy = i)
      {
        this.durationMillis = paramParcel.readInt();
        this.violationNumThisLoop = paramParcel.readInt();
        this.numAnimationsRunning = paramParcel.readInt();
        this.violationUptimeMillis = paramParcel.readLong();
        this.numInstances = paramParcel.readLong();
        this.broadcastIntentAction = paramParcel.readString();
        this.tags = paramParcel.readStringArray();
        return;
      }
    }
    
    public ViolationInfo(String paramString, Throwable paramThrowable, int paramInt)
    {
      this.message = paramString;
      this.crashInfo = new ApplicationErrorReport.CrashInfo(paramThrowable);
      this.violationUptimeMillis = SystemClock.uptimeMillis();
      this.policy = paramInt;
      this.numAnimationsRunning = ValueAnimator.getCurrentAnimationsCount();
      paramString = ActivityThread.getIntentBeingBroadcast();
      if (paramString != null) {
        this.broadcastIntentAction = paramString.getAction();
      }
      StrictMode.ThreadSpanState localThreadSpanState = (StrictMode.ThreadSpanState)StrictMode.-get5().get();
      if ((paramThrowable instanceof StrictMode.InstanceCountViolation)) {
        this.numInstances = ((StrictMode.InstanceCountViolation)paramThrowable).mInstances;
      }
      try
      {
        int i = localThreadSpanState.mActiveSize;
        paramInt = i;
        if (i > 20) {
          paramInt = 20;
        }
        if (paramInt != 0)
        {
          this.tags = new String[paramInt];
          paramString = localThreadSpanState.mActiveHead;
          i = 0;
          while ((paramString != null) && (i < paramInt))
          {
            this.tags[i] = StrictMode.Span.-get0(paramString);
            i += 1;
            paramString = StrictMode.Span.-get1(paramString);
          }
        }
        return;
      }
      finally {}
    }
    
    public ViolationInfo(Throwable paramThrowable, int paramInt)
    {
      this(null, paramThrowable, paramInt);
    }
    
    public void dump(Printer paramPrinter, String paramString)
    {
      int j = 0;
      this.crashInfo.dump(paramPrinter, paramString);
      paramPrinter.println(paramString + "policy: " + this.policy);
      if (this.durationMillis != -1) {
        paramPrinter.println(paramString + "durationMillis: " + this.durationMillis);
      }
      if (this.numInstances != -1L) {
        paramPrinter.println(paramString + "numInstances: " + this.numInstances);
      }
      if (this.violationNumThisLoop != 0) {
        paramPrinter.println(paramString + "violationNumThisLoop: " + this.violationNumThisLoop);
      }
      if (this.numAnimationsRunning != 0) {
        paramPrinter.println(paramString + "numAnimationsRunning: " + this.numAnimationsRunning);
      }
      paramPrinter.println(paramString + "violationUptimeMillis: " + this.violationUptimeMillis);
      if (this.broadcastIntentAction != null) {
        paramPrinter.println(paramString + "broadcastIntentAction: " + this.broadcastIntentAction);
      }
      if (this.tags != null)
      {
        String[] arrayOfString = this.tags;
        int k = arrayOfString.length;
        int i = 0;
        while (j < k)
        {
          String str = arrayOfString[j];
          paramPrinter.println(paramString + "tag[" + i + "]: " + str);
          j += 1;
          i += 1;
        }
      }
    }
    
    public int hashCode()
    {
      int m = 0;
      int i = this.crashInfo.stackTrace.hashCode() + 629;
      int j = i;
      if (this.numAnimationsRunning != 0) {
        j = i * 37;
      }
      i = j;
      if (this.broadcastIntentAction != null) {
        i = j * 37 + this.broadcastIntentAction.hashCode();
      }
      int k = i;
      if (this.tags != null)
      {
        String[] arrayOfString = this.tags;
        int n = arrayOfString.length;
        j = m;
        for (;;)
        {
          k = i;
          if (j >= n) {
            break;
          }
          i = i * 37 + arrayOfString[j].hashCode();
          j += 1;
        }
      }
      return k;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.message);
      this.crashInfo.writeToParcel(paramParcel, paramInt);
      paramInt = paramParcel.dataPosition();
      paramParcel.writeInt(this.policy);
      paramParcel.writeInt(this.durationMillis);
      paramParcel.writeInt(this.violationNumThisLoop);
      paramParcel.writeInt(this.numAnimationsRunning);
      paramParcel.writeLong(this.violationUptimeMillis);
      paramParcel.writeLong(this.numInstances);
      paramParcel.writeString(this.broadcastIntentAction);
      paramParcel.writeStringArray(this.tags);
      if (paramParcel.dataPosition() - paramInt > 10240)
      {
        Slog.d("StrictMode", "VIO: policy=" + this.policy + " dur=" + this.durationMillis + " numLoop=" + this.violationNumThisLoop + " anim=" + this.numAnimationsRunning + " uptime=" + this.violationUptimeMillis + " numInst=" + this.numInstances);
        Slog.d("StrictMode", "VIO: action=" + this.broadcastIntentAction);
        Slog.d("StrictMode", "VIO: tags=" + Arrays.toString(this.tags));
        Slog.d("StrictMode", "VIO: TOTAL BYTES WRITTEN: " + (paramParcel.dataPosition() - paramInt));
      }
    }
  }
  
  public static final class VmPolicy
  {
    public static final VmPolicy LAX = new VmPolicy(0, StrictMode.-get0());
    final HashMap<Class, Integer> classInstanceLimit;
    final int mask;
    
    private VmPolicy(int paramInt, HashMap<Class, Integer> paramHashMap)
    {
      if (paramHashMap == null) {
        throw new NullPointerException("classInstanceLimit == null");
      }
      this.mask = paramInt;
      this.classInstanceLimit = paramHashMap;
    }
    
    public String toString()
    {
      return "[StrictMode.VmPolicy; mask=" + this.mask + "]";
    }
    
    public static final class Builder
    {
      private HashMap<Class, Integer> mClassInstanceLimit;
      private boolean mClassInstanceLimitNeedCow = false;
      private int mMask;
      
      public Builder()
      {
        this.mMask = 0;
      }
      
      public Builder(StrictMode.VmPolicy paramVmPolicy)
      {
        this.mMask = paramVmPolicy.mask;
        this.mClassInstanceLimitNeedCow = true;
        this.mClassInstanceLimit = paramVmPolicy.classInstanceLimit;
      }
      
      private Builder enable(int paramInt)
      {
        this.mMask |= paramInt;
        return this;
      }
      
      public StrictMode.VmPolicy build()
      {
        if ((this.mMask != 0) && ((this.mMask & 0x270000) == 0)) {
          penaltyLog();
        }
        int i = this.mMask;
        if (this.mClassInstanceLimit != null) {}
        for (HashMap localHashMap = this.mClassInstanceLimit;; localHashMap = StrictMode.-get0()) {
          return new StrictMode.VmPolicy(i, localHashMap, null);
        }
      }
      
      public Builder detectActivityLeaks()
      {
        return enable(1024);
      }
      
      public Builder detectAll()
      {
        int i = 14080;
        if (SystemProperties.getBoolean("persist.sys.strictmode.clear", false)) {
          i = 30464;
        }
        return enable(i);
      }
      
      public Builder detectCleartextNetwork()
      {
        return enable(16384);
      }
      
      public Builder detectFileUriExposure()
      {
        return enable(8192);
      }
      
      public Builder detectLeakedClosableObjects()
      {
        return enable(512);
      }
      
      public Builder detectLeakedRegistrationObjects()
      {
        return enable(4096);
      }
      
      public Builder detectLeakedSqlLiteObjects()
      {
        return enable(256);
      }
      
      public Builder penaltyDeath()
      {
        return enable(262144);
      }
      
      public Builder penaltyDeathOnCleartextNetwork()
      {
        return enable(33554432);
      }
      
      public Builder penaltyDeathOnFileUriExposure()
      {
        return enable(67108864);
      }
      
      public Builder penaltyDropBox()
      {
        return enable(2097152);
      }
      
      public Builder penaltyLog()
      {
        return enable(65536);
      }
      
      public Builder setClassInstanceLimit(Class paramClass, int paramInt)
      {
        if (paramClass == null) {
          throw new NullPointerException("klass == null");
        }
        if (this.mClassInstanceLimitNeedCow)
        {
          if ((this.mClassInstanceLimit.containsKey(paramClass)) && (((Integer)this.mClassInstanceLimit.get(paramClass)).intValue() == paramInt)) {
            return this;
          }
          this.mClassInstanceLimitNeedCow = false;
          this.mClassInstanceLimit = ((HashMap)this.mClassInstanceLimit.clone());
        }
        for (;;)
        {
          this.mMask |= 0x800;
          this.mClassInstanceLimit.put(paramClass, Integer.valueOf(paramInt));
          return this;
          if (this.mClassInstanceLimit == null) {
            this.mClassInstanceLimit = new HashMap();
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/StrictMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */