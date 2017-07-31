package android.os;

import android.app.AppGlobals;
import android.content.Context;
import android.util.Log;
import com.android.internal.util.TypedProperties;
import dalvik.bytecode.OpcodeInfo;
import dalvik.system.VMDebug;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

public final class Debug
{
  private static final String DEFAULT_TRACE_BODY = "dmtrace";
  private static final String DEFAULT_TRACE_EXTENSION = ".trace";
  public static final int MEMINFO_BUFFERS = 2;
  public static final int MEMINFO_CACHED = 3;
  public static final int MEMINFO_COUNT = 13;
  public static final int MEMINFO_FREE = 1;
  public static final int MEMINFO_KERNEL_STACK = 12;
  public static final int MEMINFO_MAPPED = 9;
  public static final int MEMINFO_PAGE_TABLES = 11;
  public static final int MEMINFO_SHMEM = 4;
  public static final int MEMINFO_SLAB = 5;
  public static final int MEMINFO_SWAP_FREE = 7;
  public static final int MEMINFO_SWAP_TOTAL = 6;
  public static final int MEMINFO_TOTAL = 0;
  public static final int MEMINFO_VM_ALLOC_USED = 10;
  public static final int MEMINFO_ZRAM_TOTAL = 8;
  private static final int MIN_DEBUGGER_IDLE = 1300;
  public static final int SHOW_CLASSLOADER = 2;
  public static final int SHOW_FULL_DETAIL = 1;
  public static final int SHOW_INITIALIZED = 4;
  private static final int SPIN_DELAY = 200;
  private static final String SYSFS_QEMU_TRACE_STATE = "/sys/qemu_trace/state";
  private static final String TAG = "Debug";
  @Deprecated
  public static final int TRACE_COUNT_ALLOCS = 1;
  private static final TypedProperties debugProperties = null;
  private static volatile boolean mWaiting = false;
  
  public static final boolean cacheRegisterMap(String paramString)
  {
    return VMDebug.cacheRegisterMap(paramString);
  }
  
  @Deprecated
  public static void changeDebugPort(int paramInt) {}
  
  public static long countInstancesOfClass(Class paramClass)
  {
    return VMDebug.countInstancesOfClass(paramClass, true);
  }
  
  public static void dumpHprofData(String paramString)
    throws IOException
  {
    VMDebug.dumpHprofData(paramString);
  }
  
  public static void dumpHprofData(String paramString, FileDescriptor paramFileDescriptor)
    throws IOException
  {
    VMDebug.dumpHprofData(paramString, paramFileDescriptor);
  }
  
  public static void dumpHprofDataDdms() {}
  
  public static native void dumpNativeBacktraceToFileTimeout(int paramInt1, String paramString, int paramInt2);
  
  public static native void dumpNativeHeap(FileDescriptor paramFileDescriptor);
  
  public static final void dumpReferenceTables() {}
  
  public static boolean dumpService(String paramString, FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
  {
    IBinder localIBinder = ServiceManager.getService(paramString);
    if (localIBinder == null)
    {
      Log.e("Debug", "Can't find service to dump: " + paramString);
      return false;
    }
    try
    {
      localIBinder.dump(paramFileDescriptor, paramArrayOfString);
      return true;
    }
    catch (RemoteException paramFileDescriptor)
    {
      Log.e("Debug", "Can't dump service: " + paramString, paramFileDescriptor);
    }
    return false;
  }
  
  public static void enableEmulatorTraceOutput() {}
  
  /* Error */
  private static boolean fieldTypeMatches(Field paramField, Class<?> paramClass)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 170	java/lang/reflect/Field:getType	()Ljava/lang/Class;
    //   4: astore_0
    //   5: aload_0
    //   6: aload_1
    //   7: if_acmpne +5 -> 12
    //   10: iconst_1
    //   11: ireturn
    //   12: aload_1
    //   13: ldc -84
    //   15: invokevirtual 178	java/lang/Class:getField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
    //   18: astore_1
    //   19: aload_1
    //   20: aconst_null
    //   21: invokevirtual 182	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   24: checkcast 174	java/lang/Class
    //   27: astore_1
    //   28: aload_0
    //   29: aload_1
    //   30: if_acmpne +8 -> 38
    //   33: iconst_1
    //   34: ireturn
    //   35: astore_0
    //   36: iconst_0
    //   37: ireturn
    //   38: iconst_0
    //   39: ireturn
    //   40: astore_0
    //   41: iconst_0
    //   42: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	43	0	paramField	Field
    //   0	43	1	paramClass	Class<?>
    // Exception table:
    //   from	to	target	type
    //   12	19	35	java/lang/NoSuchFieldException
    //   19	28	40	java/lang/IllegalAccessException
  }
  
  private static String fixTracePath(String paramString)
  {
    if (paramString != null)
    {
      localObject = paramString;
      if (paramString.charAt(0) == '/') {}
    }
    else
    {
      localObject = AppGlobals.getInitialApplication();
      if (localObject == null) {
        break label81;
      }
      localObject = ((Context)localObject).getExternalFilesDir(null);
      if (paramString != null) {
        break label88;
      }
    }
    label81:
    label88:
    for (Object localObject = new File((File)localObject, "dmtrace").getAbsolutePath();; localObject = new File((File)localObject, paramString).getAbsolutePath())
    {
      paramString = (String)localObject;
      if (!((String)localObject).endsWith(".trace")) {
        paramString = (String)localObject + ".trace";
      }
      return paramString;
      localObject = Environment.getExternalStorageDirectory();
      break;
    }
  }
  
  public static final native int getBinderDeathObjectCount();
  
  public static final native int getBinderLocalObjectCount();
  
  public static final native int getBinderProxyObjectCount();
  
  public static native int getBinderReceivedTransactions();
  
  public static native int getBinderSentTransactions();
  
  public static String getCaller()
  {
    return getCaller(Thread.currentThread().getStackTrace(), 0);
  }
  
  private static String getCaller(StackTraceElement[] paramArrayOfStackTraceElement, int paramInt)
  {
    if (paramInt + 4 >= paramArrayOfStackTraceElement.length) {
      return "<bottom of call stack>";
    }
    paramArrayOfStackTraceElement = paramArrayOfStackTraceElement[(paramInt + 4)];
    return paramArrayOfStackTraceElement.getClassName() + "." + paramArrayOfStackTraceElement.getMethodName() + ":" + paramArrayOfStackTraceElement.getLineNumber();
  }
  
  public static String getCallers(int paramInt)
  {
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (i < paramInt)
    {
      localStringBuffer.append(getCaller(arrayOfStackTraceElement, i)).append(" ");
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  public static String getCallers(int paramInt1, int paramInt2)
  {
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramInt1;
    while (i < paramInt2 + paramInt1)
    {
      localStringBuffer.append(getCaller(arrayOfStackTraceElement, i)).append(" ");
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  public static String getCallers(int paramInt, String paramString)
  {
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (i < paramInt)
    {
      localStringBuffer.append(paramString).append(getCaller(arrayOfStackTraceElement, i)).append("\n");
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  @Deprecated
  public static int getGlobalAllocCount()
  {
    return VMDebug.getAllocCount(1);
  }
  
  @Deprecated
  public static int getGlobalAllocSize()
  {
    return VMDebug.getAllocCount(2);
  }
  
  @Deprecated
  public static int getGlobalClassInitCount()
  {
    return VMDebug.getAllocCount(32);
  }
  
  @Deprecated
  public static int getGlobalClassInitTime()
  {
    return VMDebug.getAllocCount(64);
  }
  
  @Deprecated
  public static int getGlobalExternalAllocCount()
  {
    return 0;
  }
  
  @Deprecated
  public static int getGlobalExternalAllocSize()
  {
    return 0;
  }
  
  @Deprecated
  public static int getGlobalExternalFreedCount()
  {
    return 0;
  }
  
  @Deprecated
  public static int getGlobalExternalFreedSize()
  {
    return 0;
  }
  
  @Deprecated
  public static int getGlobalFreedCount()
  {
    return VMDebug.getAllocCount(4);
  }
  
  @Deprecated
  public static int getGlobalFreedSize()
  {
    return VMDebug.getAllocCount(8);
  }
  
  @Deprecated
  public static int getGlobalGcInvocationCount()
  {
    return VMDebug.getAllocCount(16);
  }
  
  public static int getLoadedClassCount()
  {
    return VMDebug.getLoadedClassCount();
  }
  
  public static native void getMemInfo(long[] paramArrayOfLong);
  
  public static native void getMemoryInfo(int paramInt, MemoryInfo paramMemoryInfo);
  
  public static native void getMemoryInfo(MemoryInfo paramMemoryInfo);
  
  public static int getMethodTracingMode()
  {
    return VMDebug.getMethodTracingMode();
  }
  
  public static native long getNativeHeapAllocatedSize();
  
  public static native long getNativeHeapFreeSize();
  
  public static native long getNativeHeapSize();
  
  public static native long getPss();
  
  public static native long getPss(int paramInt, long[] paramArrayOfLong1, long[] paramArrayOfLong2);
  
  public static String getRuntimeStat(String paramString)
  {
    return VMDebug.getRuntimeStat(paramString);
  }
  
  public static Map<String, String> getRuntimeStats()
  {
    return VMDebug.getRuntimeStats();
  }
  
  @Deprecated
  public static int getThreadAllocCount()
  {
    return VMDebug.getAllocCount(65536);
  }
  
  @Deprecated
  public static int getThreadAllocSize()
  {
    return VMDebug.getAllocCount(131072);
  }
  
  @Deprecated
  public static int getThreadExternalAllocCount()
  {
    return 0;
  }
  
  @Deprecated
  public static int getThreadExternalAllocSize()
  {
    return 0;
  }
  
  @Deprecated
  public static int getThreadGcInvocationCount()
  {
    return VMDebug.getAllocCount(1048576);
  }
  
  public static native String getUnreachableMemory(int paramInt, boolean paramBoolean);
  
  public static String[] getVmFeatureList()
  {
    return VMDebug.getVmFeatureList();
  }
  
  public static boolean isDebuggerConnected()
  {
    return VMDebug.isDebuggerConnected();
  }
  
  private static void modifyFieldIfSet(Field paramField, TypedProperties paramTypedProperties, String paramString)
  {
    if (paramField.getType() == String.class)
    {
      int i = paramTypedProperties.getStringInfo(paramString);
      switch (i)
      {
      default: 
        throw new IllegalStateException("Unexpected getStringInfo(" + paramString + ") return value " + i);
      case 0: 
        try
        {
          paramField.set(null, null);
          return;
        }
        catch (IllegalAccessException paramField)
        {
          throw new IllegalArgumentException("Cannot set field for " + paramString, paramField);
        }
      case -1: 
        return;
      case -2: 
        throw new IllegalArgumentException("Type of " + paramString + " " + " does not match field type (" + paramField.getType() + ")");
      }
    }
    paramTypedProperties = paramTypedProperties.get(paramString);
    if (paramTypedProperties != null) {
      if (!fieldTypeMatches(paramField, paramTypedProperties.getClass())) {
        throw new IllegalArgumentException("Type of " + paramString + " (" + paramTypedProperties.getClass() + ") " + " does not match field type (" + paramField.getType() + ")");
      }
    }
    try
    {
      paramField.set(null, paramTypedProperties);
      return;
    }
    catch (IllegalAccessException paramField)
    {
      throw new IllegalArgumentException("Cannot set field for " + paramString, paramField);
    }
  }
  
  public static void printLoadedClasses(int paramInt)
  {
    VMDebug.printLoadedClasses(paramInt);
  }
  
  @Deprecated
  public static void resetAllCounts()
  {
    VMDebug.resetAllocCount(-1);
  }
  
  @Deprecated
  public static void resetGlobalAllocCount()
  {
    VMDebug.resetAllocCount(1);
  }
  
  @Deprecated
  public static void resetGlobalAllocSize()
  {
    VMDebug.resetAllocCount(2);
  }
  
  @Deprecated
  public static void resetGlobalClassInitCount()
  {
    VMDebug.resetAllocCount(32);
  }
  
  @Deprecated
  public static void resetGlobalClassInitTime()
  {
    VMDebug.resetAllocCount(64);
  }
  
  @Deprecated
  public static void resetGlobalExternalAllocCount() {}
  
  @Deprecated
  public static void resetGlobalExternalAllocSize() {}
  
  @Deprecated
  public static void resetGlobalExternalFreedCount() {}
  
  @Deprecated
  public static void resetGlobalExternalFreedSize() {}
  
  @Deprecated
  public static void resetGlobalFreedCount()
  {
    VMDebug.resetAllocCount(4);
  }
  
  @Deprecated
  public static void resetGlobalFreedSize()
  {
    VMDebug.resetAllocCount(8);
  }
  
  @Deprecated
  public static void resetGlobalGcInvocationCount()
  {
    VMDebug.resetAllocCount(16);
  }
  
  @Deprecated
  public static void resetThreadAllocCount()
  {
    VMDebug.resetAllocCount(65536);
  }
  
  @Deprecated
  public static void resetThreadAllocSize()
  {
    VMDebug.resetAllocCount(131072);
  }
  
  @Deprecated
  public static void resetThreadExternalAllocCount() {}
  
  @Deprecated
  public static void resetThreadExternalAllocSize() {}
  
  @Deprecated
  public static void resetThreadGcInvocationCount()
  {
    VMDebug.resetAllocCount(1048576);
  }
  
  @Deprecated
  public static int setAllocationLimit(int paramInt)
  {
    return -1;
  }
  
  public static void setFieldsOn(Class<?> paramClass)
  {
    setFieldsOn(paramClass, false);
  }
  
  public static void setFieldsOn(Class<?> paramClass, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder().append("setFieldsOn(");
    if (paramClass == null) {}
    for (paramClass = "null";; paramClass = paramClass.getName())
    {
      Log.wtf("Debug", paramClass + ") called in non-DEBUG build");
      return;
    }
  }
  
  @Deprecated
  public static int setGlobalAllocationLimit(int paramInt)
  {
    return -1;
  }
  
  @Deprecated
  public static void startAllocCounting() {}
  
  public static void startMethodTracing()
  {
    VMDebug.startMethodTracing(fixTracePath(null), 0, 0, false, 0);
  }
  
  public static void startMethodTracing(String paramString)
  {
    startMethodTracing(paramString, 0, 0);
  }
  
  public static void startMethodTracing(String paramString, int paramInt)
  {
    startMethodTracing(paramString, paramInt, 0);
  }
  
  public static void startMethodTracing(String paramString, int paramInt1, int paramInt2)
  {
    VMDebug.startMethodTracing(fixTracePath(paramString), paramInt1, paramInt2, false, 0);
  }
  
  public static void startMethodTracing(String paramString, FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2)
  {
    VMDebug.startMethodTracing(paramString, paramFileDescriptor, paramInt1, paramInt2, false, 0);
  }
  
  public static void startMethodTracingDdms(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    VMDebug.startMethodTracingDdms(paramInt1, paramInt2, paramBoolean, paramInt3);
  }
  
  public static void startMethodTracingSampling(String paramString, int paramInt1, int paramInt2)
  {
    VMDebug.startMethodTracing(fixTracePath(paramString), paramInt1, 0, true, paramInt2);
  }
  
  /* Error */
  public static void startNativeTracing()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_1
    //   4: new 452	com/android/internal/util/FastPrintWriter
    //   7: dup
    //   8: new 454	java/io/FileOutputStream
    //   11: dup
    //   12: ldc 61
    //   14: invokespecial 455	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   17: invokespecial 458	com/android/internal/util/FastPrintWriter:<init>	(Ljava/io/OutputStream;)V
    //   20: astore_0
    //   21: aload_0
    //   22: ldc_w 460
    //   25: invokevirtual 465	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   28: aload_0
    //   29: ifnull +7 -> 36
    //   32: aload_0
    //   33: invokevirtual 468	java/io/PrintWriter:close	()V
    //   36: invokestatic 158	dalvik/system/VMDebug:startEmulatorTracing	()V
    //   39: return
    //   40: astore_0
    //   41: aload_1
    //   42: astore_0
    //   43: aload_0
    //   44: ifnull -8 -> 36
    //   47: aload_0
    //   48: invokevirtual 468	java/io/PrintWriter:close	()V
    //   51: goto -15 -> 36
    //   54: astore_1
    //   55: aload_2
    //   56: astore_0
    //   57: aload_0
    //   58: ifnull +7 -> 65
    //   61: aload_0
    //   62: invokevirtual 468	java/io/PrintWriter:close	()V
    //   65: aload_1
    //   66: athrow
    //   67: astore_1
    //   68: goto -11 -> 57
    //   71: astore_1
    //   72: goto -29 -> 43
    // Local variable table:
    //   start	length	slot	name	signature
    //   20	13	0	localFastPrintWriter	com.android.internal.util.FastPrintWriter
    //   40	1	0	localException1	Exception
    //   42	20	0	localObject1	Object
    //   3	39	1	localObject2	Object
    //   54	12	1	localObject3	Object
    //   67	1	1	localObject4	Object
    //   71	1	1	localException2	Exception
    //   1	55	2	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   4	21	40	java/lang/Exception
    //   4	21	54	finally
    //   21	28	67	finally
    //   21	28	71	java/lang/Exception
  }
  
  @Deprecated
  public static void stopAllocCounting() {}
  
  public static void stopMethodTracing() {}
  
  /* Error */
  public static void stopNativeTracing()
  {
    // Byte code:
    //   0: invokestatic 478	dalvik/system/VMDebug:stopEmulatorTracing	()V
    //   3: aconst_null
    //   4: astore_2
    //   5: aconst_null
    //   6: astore_1
    //   7: new 452	com/android/internal/util/FastPrintWriter
    //   10: dup
    //   11: new 454	java/io/FileOutputStream
    //   14: dup
    //   15: ldc 61
    //   17: invokespecial 455	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   20: invokespecial 458	com/android/internal/util/FastPrintWriter:<init>	(Ljava/io/OutputStream;)V
    //   23: astore_0
    //   24: aload_0
    //   25: ldc_w 480
    //   28: invokevirtual 465	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   31: aload_0
    //   32: ifnull +7 -> 39
    //   35: aload_0
    //   36: invokevirtual 468	java/io/PrintWriter:close	()V
    //   39: return
    //   40: astore_0
    //   41: aload_1
    //   42: astore_0
    //   43: aload_0
    //   44: ifnull -5 -> 39
    //   47: aload_0
    //   48: invokevirtual 468	java/io/PrintWriter:close	()V
    //   51: return
    //   52: astore_1
    //   53: aload_2
    //   54: astore_0
    //   55: aload_0
    //   56: ifnull +7 -> 63
    //   59: aload_0
    //   60: invokevirtual 468	java/io/PrintWriter:close	()V
    //   63: aload_1
    //   64: athrow
    //   65: astore_1
    //   66: goto -11 -> 55
    //   69: astore_1
    //   70: goto -27 -> 43
    // Local variable table:
    //   start	length	slot	name	signature
    //   23	13	0	localFastPrintWriter	com.android.internal.util.FastPrintWriter
    //   40	1	0	localException1	Exception
    //   42	18	0	localObject1	Object
    //   6	36	1	localObject2	Object
    //   52	12	1	localObject3	Object
    //   65	1	1	localObject4	Object
    //   69	1	1	localException2	Exception
    //   4	50	2	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   7	24	40	java/lang/Exception
    //   7	24	52	finally
    //   24	31	65	finally
    //   24	31	69	java/lang/Exception
  }
  
  public static long threadCpuTimeNanos()
  {
    return VMDebug.threadCpuTimeNanos();
  }
  
  public static void waitForDebugger()
  {
    if (!VMDebug.isDebuggingEnabled()) {
      return;
    }
    if (isDebuggerConnected()) {
      return;
    }
    System.out.println("Sending WAIT chunk");
    DdmServer.sendChunk(new Chunk(ChunkHandler.type("WAIT"), new byte[] { 0 }, 0, 1));
    mWaiting = true;
    while (!isDebuggerConnected()) {
      try
      {
        Thread.sleep(200L);
      }
      catch (InterruptedException localInterruptedException1) {}
    }
    mWaiting = false;
    System.out.println("Debugger has connected");
    long l;
    for (;;)
    {
      l = VMDebug.lastDebuggerActivity();
      if (l < 0L)
      {
        System.out.println("debugger detached?");
        return;
      }
      if (l >= 1300L) {
        break;
      }
      System.out.println("waiting for debugger to settle...");
      try
      {
        Thread.sleep(200L);
      }
      catch (InterruptedException localInterruptedException2) {}
    }
    System.out.println("debugger has settled (" + l + ")");
  }
  
  public static boolean waitingForDebugger()
  {
    return mWaiting;
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.FIELD})
  public static @interface DebugProperty {}
  
  @Deprecated
  public static class InstructionCount
  {
    private static final int NUM_INSTR = OpcodeInfo.MAXIMUM_PACKED_VALUE + 1;
    private int[] mCounts = new int[NUM_INSTR];
    
    public boolean collect()
    {
      try
      {
        VMDebug.stopInstructionCounting();
        VMDebug.getInstructionCount(this.mCounts);
        return true;
      }
      catch (UnsupportedOperationException localUnsupportedOperationException) {}
      return false;
    }
    
    public int globalMethodInvocations()
    {
      int j = 0;
      int i = 0;
      while (i < NUM_INSTR)
      {
        int k = j;
        if (OpcodeInfo.isInvoke(i)) {
          k = j + this.mCounts[i];
        }
        i += 1;
        j = k;
      }
      return j;
    }
    
    public int globalTotal()
    {
      int j = 0;
      int i = 0;
      while (i < NUM_INSTR)
      {
        j += this.mCounts[i];
        i += 1;
      }
      return j;
    }
    
    public boolean resetAndStart()
    {
      try
      {
        VMDebug.startInstructionCounting();
        VMDebug.resetInstructionCount();
        return true;
      }
      catch (UnsupportedOperationException localUnsupportedOperationException) {}
      return false;
    }
  }
  
  public static class MemoryInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<MemoryInfo> CREATOR = new Parcelable.Creator()
    {
      public Debug.MemoryInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new Debug.MemoryInfo(paramAnonymousParcel, null);
      }
      
      public Debug.MemoryInfo[] newArray(int paramAnonymousInt)
      {
        return new Debug.MemoryInfo[paramAnonymousInt];
      }
    };
    public static final int HEAP_DALVIK = 1;
    public static final int HEAP_NATIVE = 2;
    public static final int HEAP_UNKNOWN = 0;
    public static final int NUM_CATEGORIES = 8;
    public static final int NUM_DVK_STATS = 8;
    public static final int NUM_OTHER_STATS = 17;
    public static final int OTHER_APK = 8;
    public static final int OTHER_ART = 12;
    public static final int OTHER_ASHMEM = 3;
    public static final int OTHER_CURSOR = 2;
    public static final int OTHER_DALVIK_ACCOUNTING = 20;
    public static final int OTHER_DALVIK_CODE_CACHE = 21;
    public static final int OTHER_DALVIK_INDIRECT_REFERENCE_TABLE = 24;
    public static final int OTHER_DALVIK_LARGE = 18;
    public static final int OTHER_DALVIK_LINEARALLOC = 19;
    public static final int OTHER_DALVIK_NON_MOVING = 23;
    public static final int OTHER_DALVIK_NORMAL = 17;
    public static final int OTHER_DALVIK_OTHER = 0;
    public static final int OTHER_DALVIK_ZYGOTE = 22;
    public static final int OTHER_DEX = 10;
    public static final int OTHER_GL = 15;
    public static final int OTHER_GL_DEV = 4;
    public static final int OTHER_GRAPHICS = 14;
    public static final int OTHER_JAR = 7;
    public static final int OTHER_OAT = 11;
    public static final int OTHER_OTHER_MEMTRACK = 16;
    public static final int OTHER_SO = 6;
    public static final int OTHER_STACK = 1;
    public static final int OTHER_TTF = 9;
    public static final int OTHER_UNKNOWN_DEV = 5;
    public static final int OTHER_UNKNOWN_MAP = 13;
    public static final int offsetPrivateClean = 4;
    public static final int offsetPrivateDirty = 2;
    public static final int offsetPss = 0;
    public static final int offsetSharedClean = 5;
    public static final int offsetSharedDirty = 3;
    public static final int offsetSwappablePss = 1;
    public static final int offsetSwappedOut = 6;
    public static final int offsetSwappedOutPss = 7;
    public int dalvikPrivateClean;
    public int dalvikPrivateDirty;
    public int dalvikPss;
    public int dalvikSharedClean;
    public int dalvikSharedDirty;
    public int dalvikSwappablePss;
    public int dalvikSwappedOut;
    public int dalvikSwappedOutPss;
    public boolean hasSwappedOutPss;
    public int nativePrivateClean;
    public int nativePrivateDirty;
    public int nativePss;
    public int nativeSharedClean;
    public int nativeSharedDirty;
    public int nativeSwappablePss;
    public int nativeSwappedOut;
    public int nativeSwappedOutPss;
    public int otherPrivateClean;
    public int otherPrivateDirty;
    public int otherPss;
    public int otherSharedClean;
    public int otherSharedDirty;
    private int[] otherStats = new int['È'];
    public int otherSwappablePss;
    public int otherSwappedOut;
    public int otherSwappedOutPss;
    
    public MemoryInfo() {}
    
    private MemoryInfo(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public static String getOtherLabel(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "????";
      case 0: 
        return "Dalvik Other";
      case 1: 
        return "Stack";
      case 2: 
        return "Cursor";
      case 3: 
        return "Ashmem";
      case 4: 
        return "Gfx dev";
      case 5: 
        return "Other dev";
      case 6: 
        return ".so mmap";
      case 7: 
        return ".jar mmap";
      case 8: 
        return ".apk mmap";
      case 9: 
        return ".ttf mmap";
      case 10: 
        return ".dex mmap";
      case 11: 
        return ".oat mmap";
      case 12: 
        return ".art mmap";
      case 13: 
        return "Other mmap";
      case 14: 
        return "EGL mtrack";
      case 15: 
        return "GL mtrack";
      case 16: 
        return "Other mtrack";
      case 17: 
        return ".Heap";
      case 18: 
        return ".LOS";
      case 19: 
        return ".LinearAlloc";
      case 20: 
        return ".GC";
      case 21: 
        return ".JITCache";
      case 22: 
        return ".Zygote";
      case 23: 
        return ".NonMoving";
      }
      return ".IndirectRef";
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public String getMemoryStat(String paramString)
    {
      if (paramString.equals("summary.java-heap")) {
        return Integer.toString(getSummaryJavaHeap());
      }
      if (paramString.equals("summary.native-heap")) {
        return Integer.toString(getSummaryNativeHeap());
      }
      if (paramString.equals("summary.code")) {
        return Integer.toString(getSummaryCode());
      }
      if (paramString.equals("summary.stack")) {
        return Integer.toString(getSummaryStack());
      }
      if (paramString.equals("summary.graphics")) {
        return Integer.toString(getSummaryGraphics());
      }
      if (paramString.equals("summary.private-other")) {
        return Integer.toString(getSummaryPrivateOther());
      }
      if (paramString.equals("summary.system")) {
        return Integer.toString(getSummarySystem());
      }
      if (paramString.equals("summary.total-pss")) {
        return Integer.toString(getSummaryTotalPss());
      }
      if (paramString.equals("summary.total-swap")) {
        return Integer.toString(getSummaryTotalSwap());
      }
      return null;
    }
    
    public Map<String, String> getMemoryStats()
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("summary.java-heap", Integer.toString(getSummaryJavaHeap()));
      localHashMap.put("summary.native-heap", Integer.toString(getSummaryNativeHeap()));
      localHashMap.put("summary.code", Integer.toString(getSummaryCode()));
      localHashMap.put("summary.stack", Integer.toString(getSummaryStack()));
      localHashMap.put("summary.graphics", Integer.toString(getSummaryGraphics()));
      localHashMap.put("summary.private-other", Integer.toString(getSummaryPrivateOther()));
      localHashMap.put("summary.system", Integer.toString(getSummarySystem()));
      localHashMap.put("summary.total-pss", Integer.toString(getSummaryTotalPss()));
      localHashMap.put("summary.total-swap", Integer.toString(getSummaryTotalSwap()));
      return localHashMap;
    }
    
    public int getOtherPrivate(int paramInt)
    {
      return getOtherPrivateClean(paramInt) + getOtherPrivateDirty(paramInt);
    }
    
    public int getOtherPrivateClean(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 4)];
    }
    
    public int getOtherPrivateDirty(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 2)];
    }
    
    public int getOtherPss(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 0)];
    }
    
    public int getOtherSharedClean(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 5)];
    }
    
    public int getOtherSharedDirty(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 3)];
    }
    
    public int getOtherSwappablePss(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 1)];
    }
    
    public int getOtherSwappedOut(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 6)];
    }
    
    public int getOtherSwappedOutPss(int paramInt)
    {
      return this.otherStats[(paramInt * 8 + 7)];
    }
    
    public int getSummaryCode()
    {
      return getOtherPrivate(6) + getOtherPrivate(7) + getOtherPrivate(8) + getOtherPrivate(9) + getOtherPrivate(10) + getOtherPrivate(11);
    }
    
    public int getSummaryGraphics()
    {
      return getOtherPrivate(4) + getOtherPrivate(14) + getOtherPrivate(15);
    }
    
    public int getSummaryJavaHeap()
    {
      return this.dalvikPrivateDirty + getOtherPrivate(12);
    }
    
    public int getSummaryNativeHeap()
    {
      return this.nativePrivateDirty;
    }
    
    public int getSummaryPrivateOther()
    {
      return getTotalPrivateClean() + getTotalPrivateDirty() - getSummaryJavaHeap() - getSummaryNativeHeap() - getSummaryCode() - getSummaryStack() - getSummaryGraphics();
    }
    
    public int getSummaryStack()
    {
      return getOtherPrivateDirty(1);
    }
    
    public int getSummarySystem()
    {
      return getTotalPss() - getTotalPrivateClean() - getTotalPrivateDirty();
    }
    
    public int getSummaryTotalPss()
    {
      return getTotalPss();
    }
    
    public int getSummaryTotalSwap()
    {
      return getTotalSwappedOut();
    }
    
    public int getSummaryTotalSwapPss()
    {
      return getTotalSwappedOutPss();
    }
    
    public int getTotalPrivateClean()
    {
      return this.dalvikPrivateClean + this.nativePrivateClean + this.otherPrivateClean;
    }
    
    public int getTotalPrivateDirty()
    {
      return this.dalvikPrivateDirty + this.nativePrivateDirty + this.otherPrivateDirty;
    }
    
    public int getTotalPss()
    {
      return this.dalvikPss + this.nativePss + this.otherPss + getTotalSwappedOutPss();
    }
    
    public int getTotalSharedClean()
    {
      return this.dalvikSharedClean + this.nativeSharedClean + this.otherSharedClean;
    }
    
    public int getTotalSharedDirty()
    {
      return this.dalvikSharedDirty + this.nativeSharedDirty + this.otherSharedDirty;
    }
    
    public int getTotalSwappablePss()
    {
      return this.dalvikSwappablePss + this.nativeSwappablePss + this.otherSwappablePss;
    }
    
    public int getTotalSwappedOut()
    {
      return this.dalvikSwappedOut + this.nativeSwappedOut + this.otherSwappedOut;
    }
    
    public int getTotalSwappedOutPss()
    {
      return this.dalvikSwappedOutPss + this.nativeSwappedOutPss + this.otherSwappedOutPss;
    }
    
    public int getTotalUss()
    {
      return this.dalvikPrivateClean + this.dalvikPrivateDirty + this.nativePrivateClean + this.nativePrivateDirty + this.otherPrivateClean + this.otherPrivateDirty;
    }
    
    public boolean hasSwappedOutPss()
    {
      return this.hasSwappedOutPss;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      boolean bool = false;
      this.dalvikPss = paramParcel.readInt();
      this.dalvikSwappablePss = paramParcel.readInt();
      this.dalvikPrivateDirty = paramParcel.readInt();
      this.dalvikSharedDirty = paramParcel.readInt();
      this.dalvikPrivateClean = paramParcel.readInt();
      this.dalvikSharedClean = paramParcel.readInt();
      this.dalvikSwappedOut = paramParcel.readInt();
      this.nativePss = paramParcel.readInt();
      this.nativeSwappablePss = paramParcel.readInt();
      this.nativePrivateDirty = paramParcel.readInt();
      this.nativeSharedDirty = paramParcel.readInt();
      this.nativePrivateClean = paramParcel.readInt();
      this.nativeSharedClean = paramParcel.readInt();
      this.nativeSwappedOut = paramParcel.readInt();
      this.otherPss = paramParcel.readInt();
      this.otherSwappablePss = paramParcel.readInt();
      this.otherPrivateDirty = paramParcel.readInt();
      this.otherSharedDirty = paramParcel.readInt();
      this.otherPrivateClean = paramParcel.readInt();
      this.otherSharedClean = paramParcel.readInt();
      this.otherSwappedOut = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.hasSwappedOutPss = bool;
      this.otherSwappedOutPss = paramParcel.readInt();
      this.otherStats = paramParcel.createIntArray();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.dalvikPss);
      paramParcel.writeInt(this.dalvikSwappablePss);
      paramParcel.writeInt(this.dalvikPrivateDirty);
      paramParcel.writeInt(this.dalvikSharedDirty);
      paramParcel.writeInt(this.dalvikPrivateClean);
      paramParcel.writeInt(this.dalvikSharedClean);
      paramParcel.writeInt(this.dalvikSwappedOut);
      paramParcel.writeInt(this.nativePss);
      paramParcel.writeInt(this.nativeSwappablePss);
      paramParcel.writeInt(this.nativePrivateDirty);
      paramParcel.writeInt(this.nativeSharedDirty);
      paramParcel.writeInt(this.nativePrivateClean);
      paramParcel.writeInt(this.nativeSharedClean);
      paramParcel.writeInt(this.nativeSwappedOut);
      paramParcel.writeInt(this.otherPss);
      paramParcel.writeInt(this.otherSwappablePss);
      paramParcel.writeInt(this.otherPrivateDirty);
      paramParcel.writeInt(this.otherSharedDirty);
      paramParcel.writeInt(this.otherPrivateClean);
      paramParcel.writeInt(this.otherSharedClean);
      paramParcel.writeInt(this.otherSwappedOut);
      if (this.hasSwappedOutPss) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.otherSwappedOutPss);
        paramParcel.writeIntArray(this.otherStats);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Debug.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */