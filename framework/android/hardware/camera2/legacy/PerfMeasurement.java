package android.hardware.camera2.legacy;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class PerfMeasurement
{
  public static final int DEFAULT_MAX_QUERIES = 3;
  private static final long FAILED_TIMING = -2L;
  private static final long NO_DURATION_YET = -1L;
  private static final String TAG = "PerfMeasurement";
  private ArrayList<Long> mCollectedCpuDurations = new ArrayList();
  private ArrayList<Long> mCollectedGpuDurations = new ArrayList();
  private ArrayList<Long> mCollectedTimestamps = new ArrayList();
  private int mCompletedQueryCount = 0;
  private Queue<Long> mCpuDurationsQueue = new LinkedList();
  private final long mNativeContext;
  private long mStartTimeNs;
  private Queue<Long> mTimestampQueue = new LinkedList();
  
  public PerfMeasurement()
  {
    this.mNativeContext = nativeCreateContext(3);
  }
  
  public PerfMeasurement(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("maxQueries is less than 1");
    }
    this.mNativeContext = nativeCreateContext(paramInt);
  }
  
  private long getNextGlDuration()
  {
    long l = nativeGetNextGlDuration(this.mNativeContext);
    if (l > 0L) {
      this.mCompletedQueryCount += 1;
    }
    return l;
  }
  
  public static boolean isGlTimingSupported()
  {
    return nativeQuerySupport();
  }
  
  private static native long nativeCreateContext(int paramInt);
  
  private static native void nativeDeleteContext(long paramLong);
  
  protected static native long nativeGetNextGlDuration(long paramLong);
  
  private static native boolean nativeQuerySupport();
  
  protected static native void nativeStartGlTimer(long paramLong);
  
  protected static native void nativeStopGlTimer(long paramLong);
  
  public void addTimestamp(long paramLong)
  {
    this.mTimestampQueue.add(Long.valueOf(paramLong));
  }
  
  /* Error */
  public void dumpPerformanceData(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 7
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore 6
    //   12: new 102	java/io/BufferedWriter
    //   15: dup
    //   16: new 104	java/io/FileWriter
    //   19: dup
    //   20: aload_1
    //   21: invokespecial 105	java/io/FileWriter:<init>	(Ljava/lang/String;)V
    //   24: invokespecial 108	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   27: astore_3
    //   28: aload_3
    //   29: ldc 110
    //   31: invokevirtual 113	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   34: iconst_0
    //   35: istore_2
    //   36: iload_2
    //   37: aload_0
    //   38: getfield 41	android/hardware/camera2/legacy/PerfMeasurement:mCollectedGpuDurations	Ljava/util/ArrayList;
    //   41: invokevirtual 117	java/util/ArrayList:size	()I
    //   44: if_icmpge +56 -> 100
    //   47: aload_3
    //   48: ldc 119
    //   50: iconst_3
    //   51: anewarray 4	java/lang/Object
    //   54: dup
    //   55: iconst_0
    //   56: aload_0
    //   57: getfield 45	android/hardware/camera2/legacy/PerfMeasurement:mCollectedTimestamps	Ljava/util/ArrayList;
    //   60: iload_2
    //   61: invokevirtual 123	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   64: aastore
    //   65: dup
    //   66: iconst_1
    //   67: aload_0
    //   68: getfield 41	android/hardware/camera2/legacy/PerfMeasurement:mCollectedGpuDurations	Ljava/util/ArrayList;
    //   71: iload_2
    //   72: invokevirtual 123	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   75: aastore
    //   76: dup
    //   77: iconst_2
    //   78: aload_0
    //   79: getfield 43	android/hardware/camera2/legacy/PerfMeasurement:mCollectedCpuDurations	Ljava/util/ArrayList;
    //   82: iload_2
    //   83: invokevirtual 123	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   86: aastore
    //   87: invokestatic 129	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   90: invokevirtual 113	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   93: iload_2
    //   94: iconst_1
    //   95: iadd
    //   96: istore_2
    //   97: goto -61 -> 36
    //   100: aload_0
    //   101: getfield 45	android/hardware/camera2/legacy/PerfMeasurement:mCollectedTimestamps	Ljava/util/ArrayList;
    //   104: invokevirtual 132	java/util/ArrayList:clear	()V
    //   107: aload_0
    //   108: getfield 41	android/hardware/camera2/legacy/PerfMeasurement:mCollectedGpuDurations	Ljava/util/ArrayList;
    //   111: invokevirtual 132	java/util/ArrayList:clear	()V
    //   114: aload_0
    //   115: getfield 43	android/hardware/camera2/legacy/PerfMeasurement:mCollectedCpuDurations	Ljava/util/ArrayList;
    //   118: invokevirtual 132	java/util/ArrayList:clear	()V
    //   121: aload 7
    //   123: astore 4
    //   125: aload_3
    //   126: ifnull +11 -> 137
    //   129: aload_3
    //   130: invokevirtual 135	java/io/BufferedWriter:close	()V
    //   133: aload 7
    //   135: astore 4
    //   137: aload 4
    //   139: ifnull +47 -> 186
    //   142: aload 4
    //   144: athrow
    //   145: astore_3
    //   146: ldc 18
    //   148: new 137	java/lang/StringBuilder
    //   151: dup
    //   152: invokespecial 138	java/lang/StringBuilder:<init>	()V
    //   155: ldc -116
    //   157: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: aload_1
    //   161: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: ldc -110
    //   166: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: aload_3
    //   170: invokevirtual 149	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   173: invokevirtual 153	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   176: invokestatic 159	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   179: pop
    //   180: return
    //   181: astore 4
    //   183: goto -46 -> 137
    //   186: return
    //   187: astore 4
    //   189: aload 6
    //   191: astore_3
    //   192: aload 4
    //   194: athrow
    //   195: astore 6
    //   197: aload 4
    //   199: astore 5
    //   201: aload 6
    //   203: astore 4
    //   205: aload 5
    //   207: astore 6
    //   209: aload_3
    //   210: ifnull +11 -> 221
    //   213: aload_3
    //   214: invokevirtual 135	java/io/BufferedWriter:close	()V
    //   217: aload 5
    //   219: astore 6
    //   221: aload 6
    //   223: ifnull +29 -> 252
    //   226: aload 6
    //   228: athrow
    //   229: aload 5
    //   231: astore 6
    //   233: aload 5
    //   235: aload_3
    //   236: if_acmpeq -15 -> 221
    //   239: aload 5
    //   241: aload_3
    //   242: invokevirtual 163	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   245: aload 5
    //   247: astore 6
    //   249: goto -28 -> 221
    //   252: aload 4
    //   254: athrow
    //   255: astore 6
    //   257: aload 4
    //   259: astore_3
    //   260: aload 6
    //   262: astore 4
    //   264: goto -59 -> 205
    //   267: astore 4
    //   269: goto -64 -> 205
    //   272: astore 4
    //   274: goto -82 -> 192
    //   277: astore_3
    //   278: goto -132 -> 146
    //   281: astore_3
    //   282: aload 5
    //   284: ifnonnull -55 -> 229
    //   287: aload_3
    //   288: astore 6
    //   290: goto -69 -> 221
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	293	0	this	PerfMeasurement
    //   0	293	1	paramString	String
    //   35	62	2	i	int
    //   27	103	3	localBufferedWriter	java.io.BufferedWriter
    //   145	25	3	localIOException1	java.io.IOException
    //   191	69	3	localObject1	Object
    //   277	1	3	localIOException2	java.io.IOException
    //   281	7	3	localThrowable1	Throwable
    //   7	136	4	localObject2	Object
    //   181	1	4	localThrowable2	Throwable
    //   187	11	4	localThrowable3	Throwable
    //   203	60	4	localObject3	Object
    //   267	1	4	localObject4	Object
    //   272	1	4	localThrowable4	Throwable
    //   1	282	5	localObject5	Object
    //   10	180	6	localObject6	Object
    //   195	7	6	localObject7	Object
    //   207	41	6	localObject8	Object
    //   255	6	6	localObject9	Object
    //   288	1	6	localObject10	Object
    //   4	130	7	localObject11	Object
    // Exception table:
    //   from	to	target	type
    //   129	133	145	java/io/IOException
    //   142	145	145	java/io/IOException
    //   129	133	181	java/lang/Throwable
    //   12	28	187	java/lang/Throwable
    //   192	195	195	finally
    //   12	28	255	finally
    //   28	34	267	finally
    //   36	93	267	finally
    //   100	121	267	finally
    //   28	34	272	java/lang/Throwable
    //   36	93	272	java/lang/Throwable
    //   100	121	272	java/lang/Throwable
    //   213	217	277	java/io/IOException
    //   226	229	277	java/io/IOException
    //   239	245	277	java/io/IOException
    //   252	255	277	java/io/IOException
    //   213	217	281	java/lang/Throwable
  }
  
  protected void finalize()
  {
    nativeDeleteContext(this.mNativeContext);
  }
  
  public int getCompletedQueryCount()
  {
    return this.mCompletedQueryCount;
  }
  
  public void startTimer()
  {
    nativeStartGlTimer(this.mNativeContext);
    this.mStartTimeNs = SystemClock.elapsedRealtimeNanos();
  }
  
  public void stopTimer()
  {
    long l2 = -1L;
    long l1 = SystemClock.elapsedRealtimeNanos();
    this.mCpuDurationsQueue.add(Long.valueOf(l1 - this.mStartTimeNs));
    nativeStopGlTimer(this.mNativeContext);
    long l3 = getNextGlDuration();
    ArrayList localArrayList;
    if (l3 > 0L)
    {
      this.mCollectedGpuDurations.add(Long.valueOf(l3));
      localArrayList = this.mCollectedTimestamps;
      if (!this.mTimestampQueue.isEmpty()) {
        break label176;
      }
      l1 = -1L;
      localArrayList.add(Long.valueOf(l1));
      localArrayList = this.mCollectedCpuDurations;
      if (!this.mCpuDurationsQueue.isEmpty()) {
        break label195;
      }
    }
    label176:
    label195:
    for (l1 = l2;; l1 = ((Long)this.mCpuDurationsQueue.poll()).longValue())
    {
      localArrayList.add(Long.valueOf(l1));
      if (l3 == -2L)
      {
        if (!this.mTimestampQueue.isEmpty()) {
          this.mTimestampQueue.poll();
        }
        if (!this.mCpuDurationsQueue.isEmpty()) {
          this.mCpuDurationsQueue.poll();
        }
      }
      return;
      l1 = ((Long)this.mTimestampQueue.poll()).longValue();
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/PerfMeasurement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */