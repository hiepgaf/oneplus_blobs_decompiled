package android.os;

import java.util.ArrayList;
import java.util.Iterator;

public class PerformanceCollector
{
  public static final String METRIC_KEY_CPU_TIME = "cpu_time";
  public static final String METRIC_KEY_EXECUTION_TIME = "execution_time";
  public static final String METRIC_KEY_GC_INVOCATION_COUNT = "gc_invocation_count";
  public static final String METRIC_KEY_GLOBAL_ALLOC_COUNT = "global_alloc_count";
  public static final String METRIC_KEY_GLOBAL_ALLOC_SIZE = "global_alloc_size";
  public static final String METRIC_KEY_GLOBAL_FREED_COUNT = "global_freed_count";
  public static final String METRIC_KEY_GLOBAL_FREED_SIZE = "global_freed_size";
  public static final String METRIC_KEY_ITERATIONS = "iterations";
  public static final String METRIC_KEY_JAVA_ALLOCATED = "java_allocated";
  public static final String METRIC_KEY_JAVA_FREE = "java_free";
  public static final String METRIC_KEY_JAVA_PRIVATE_DIRTY = "java_private_dirty";
  public static final String METRIC_KEY_JAVA_PSS = "java_pss";
  public static final String METRIC_KEY_JAVA_SHARED_DIRTY = "java_shared_dirty";
  public static final String METRIC_KEY_JAVA_SIZE = "java_size";
  public static final String METRIC_KEY_LABEL = "label";
  public static final String METRIC_KEY_NATIVE_ALLOCATED = "native_allocated";
  public static final String METRIC_KEY_NATIVE_FREE = "native_free";
  public static final String METRIC_KEY_NATIVE_PRIVATE_DIRTY = "native_private_dirty";
  public static final String METRIC_KEY_NATIVE_PSS = "native_pss";
  public static final String METRIC_KEY_NATIVE_SHARED_DIRTY = "native_shared_dirty";
  public static final String METRIC_KEY_NATIVE_SIZE = "native_size";
  public static final String METRIC_KEY_OTHER_PRIVATE_DIRTY = "other_private_dirty";
  public static final String METRIC_KEY_OTHER_PSS = "other_pss";
  public static final String METRIC_KEY_OTHER_SHARED_DIRTY = "other_shared_dirty";
  public static final String METRIC_KEY_PRE_RECEIVED_TRANSACTIONS = "pre_received_transactions";
  public static final String METRIC_KEY_PRE_SENT_TRANSACTIONS = "pre_sent_transactions";
  public static final String METRIC_KEY_RECEIVED_TRANSACTIONS = "received_transactions";
  public static final String METRIC_KEY_SENT_TRANSACTIONS = "sent_transactions";
  private long mCpuTime;
  private long mExecTime;
  private Bundle mPerfMeasurement;
  private Bundle mPerfSnapshot;
  private PerformanceResultsWriter mPerfWriter;
  private long mSnapshotCpuTime;
  private long mSnapshotExecTime;
  
  public PerformanceCollector() {}
  
  public PerformanceCollector(PerformanceResultsWriter paramPerformanceResultsWriter)
  {
    setPerformanceResultsWriter(paramPerformanceResultsWriter);
  }
  
  private void endPerformanceSnapshot()
  {
    this.mSnapshotCpuTime = (Process.getElapsedCpuTime() - this.mSnapshotCpuTime);
    this.mSnapshotExecTime = (SystemClock.uptimeMillis() - this.mSnapshotExecTime);
    stopAllocCounting();
    long l1 = Debug.getNativeHeapSize() / 1024L;
    long l2 = Debug.getNativeHeapAllocatedSize() / 1024L;
    long l3 = Debug.getNativeHeapFreeSize() / 1024L;
    Debug.MemoryInfo localMemoryInfo = new Debug.MemoryInfo();
    Debug.getMemoryInfo(localMemoryInfo);
    Object localObject = Runtime.getRuntime();
    long l4 = ((Runtime)localObject).totalMemory() / 1024L;
    long l5 = ((Runtime)localObject).freeMemory() / 1024L;
    localObject = getBinderCounts();
    Iterator localIterator = ((BaseBundle)localObject).keySet().iterator();
    String str;
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      this.mPerfSnapshot.putLong(str, ((BaseBundle)localObject).getLong(str));
    }
    localObject = getAllocCounts();
    localIterator = ((BaseBundle)localObject).keySet().iterator();
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      this.mPerfSnapshot.putLong(str, ((BaseBundle)localObject).getLong(str));
    }
    this.mPerfSnapshot.putLong("execution_time", this.mSnapshotExecTime);
    this.mPerfSnapshot.putLong("cpu_time", this.mSnapshotCpuTime);
    this.mPerfSnapshot.putLong("native_size", l1);
    this.mPerfSnapshot.putLong("native_allocated", l2);
    this.mPerfSnapshot.putLong("native_free", l3);
    this.mPerfSnapshot.putLong("native_pss", localMemoryInfo.nativePss);
    this.mPerfSnapshot.putLong("native_private_dirty", localMemoryInfo.nativePrivateDirty);
    this.mPerfSnapshot.putLong("native_shared_dirty", localMemoryInfo.nativeSharedDirty);
    this.mPerfSnapshot.putLong("java_size", l4);
    this.mPerfSnapshot.putLong("java_allocated", l4 - l5);
    this.mPerfSnapshot.putLong("java_free", l5);
    this.mPerfSnapshot.putLong("java_pss", localMemoryInfo.dalvikPss);
    this.mPerfSnapshot.putLong("java_private_dirty", localMemoryInfo.dalvikPrivateDirty);
    this.mPerfSnapshot.putLong("java_shared_dirty", localMemoryInfo.dalvikSharedDirty);
    this.mPerfSnapshot.putLong("other_pss", localMemoryInfo.otherPss);
    this.mPerfSnapshot.putLong("other_private_dirty", localMemoryInfo.otherPrivateDirty);
    this.mPerfSnapshot.putLong("other_shared_dirty", localMemoryInfo.otherSharedDirty);
  }
  
  private static Bundle getAllocCounts()
  {
    Bundle localBundle = new Bundle();
    localBundle.putLong("global_alloc_count", Debug.getGlobalAllocCount());
    localBundle.putLong("global_alloc_size", Debug.getGlobalAllocSize());
    localBundle.putLong("global_freed_count", Debug.getGlobalFreedCount());
    localBundle.putLong("global_freed_size", Debug.getGlobalFreedSize());
    localBundle.putLong("gc_invocation_count", Debug.getGlobalGcInvocationCount());
    return localBundle;
  }
  
  private static Bundle getBinderCounts()
  {
    Bundle localBundle = new Bundle();
    localBundle.putLong("sent_transactions", Debug.getBinderSentTransactions());
    localBundle.putLong("received_transactions", Debug.getBinderReceivedTransactions());
    return localBundle;
  }
  
  private static void startAllocCounting()
  {
    Runtime.getRuntime().gc();
    Runtime.getRuntime().runFinalization();
    Runtime.getRuntime().gc();
    Debug.resetAllCounts();
    Debug.startAllocCounting();
  }
  
  private void startPerformanceSnapshot()
  {
    this.mPerfSnapshot = new Bundle();
    Bundle localBundle = getBinderCounts();
    Iterator localIterator = localBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.mPerfSnapshot.putLong("pre_" + str, localBundle.getLong(str));
    }
    startAllocCounting();
    this.mSnapshotExecTime = SystemClock.uptimeMillis();
    this.mSnapshotCpuTime = Process.getElapsedCpuTime();
  }
  
  private static void stopAllocCounting()
  {
    Runtime.getRuntime().gc();
    Runtime.getRuntime().runFinalization();
    Runtime.getRuntime().gc();
    Debug.stopAllocCounting();
  }
  
  public Bundle addIteration(String paramString)
  {
    this.mCpuTime = (Process.getElapsedCpuTime() - this.mCpuTime);
    this.mExecTime = (SystemClock.uptimeMillis() - this.mExecTime);
    Bundle localBundle = new Bundle();
    localBundle.putString("label", paramString);
    localBundle.putLong("execution_time", this.mExecTime);
    localBundle.putLong("cpu_time", this.mCpuTime);
    this.mPerfMeasurement.getParcelableArrayList("iterations").add(localBundle);
    this.mExecTime = SystemClock.uptimeMillis();
    this.mCpuTime = Process.getElapsedCpuTime();
    return localBundle;
  }
  
  public void addMeasurement(String paramString, float paramFloat)
  {
    if (this.mPerfWriter != null) {
      this.mPerfWriter.writeMeasurement(paramString, paramFloat);
    }
  }
  
  public void addMeasurement(String paramString, long paramLong)
  {
    if (this.mPerfWriter != null) {
      this.mPerfWriter.writeMeasurement(paramString, paramLong);
    }
  }
  
  public void addMeasurement(String paramString1, String paramString2)
  {
    if (this.mPerfWriter != null) {
      this.mPerfWriter.writeMeasurement(paramString1, paramString2);
    }
  }
  
  public void beginSnapshot(String paramString)
  {
    if (this.mPerfWriter != null) {
      this.mPerfWriter.writeBeginSnapshot(paramString);
    }
    startPerformanceSnapshot();
  }
  
  public Bundle endSnapshot()
  {
    endPerformanceSnapshot();
    if (this.mPerfWriter != null) {
      this.mPerfWriter.writeEndSnapshot(this.mPerfSnapshot);
    }
    return this.mPerfSnapshot;
  }
  
  public void setPerformanceResultsWriter(PerformanceResultsWriter paramPerformanceResultsWriter)
  {
    this.mPerfWriter = paramPerformanceResultsWriter;
  }
  
  public void startTiming(String paramString)
  {
    if (this.mPerfWriter != null) {
      this.mPerfWriter.writeStartTiming(paramString);
    }
    this.mPerfMeasurement = new Bundle();
    this.mPerfMeasurement.putParcelableArrayList("iterations", new ArrayList());
    this.mExecTime = SystemClock.uptimeMillis();
    this.mCpuTime = Process.getElapsedCpuTime();
  }
  
  public Bundle stopTiming(String paramString)
  {
    addIteration(paramString);
    if (this.mPerfWriter != null) {
      this.mPerfWriter.writeStopTiming(this.mPerfMeasurement);
    }
    return this.mPerfMeasurement;
  }
  
  public static abstract interface PerformanceResultsWriter
  {
    public abstract void writeBeginSnapshot(String paramString);
    
    public abstract void writeEndSnapshot(Bundle paramBundle);
    
    public abstract void writeMeasurement(String paramString, float paramFloat);
    
    public abstract void writeMeasurement(String paramString, long paramLong);
    
    public abstract void writeMeasurement(String paramString1, String paramString2);
    
    public abstract void writeStartTiming(String paramString);
    
    public abstract void writeStopTiming(Bundle paramBundle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/PerformanceCollector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */