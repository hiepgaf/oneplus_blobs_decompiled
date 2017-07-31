package android.hardware.camera2.legacy;

import android.util.Log;
import android.util.Pair;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;

public class RequestQueue
{
  private static final long INVALID_FRAME = -1L;
  private static final String TAG = "RequestQueue";
  private long mCurrentFrameNumber = 0L;
  private long mCurrentRepeatingFrameNumber = -1L;
  private int mCurrentRequestId = 0;
  private final List<Long> mJpegSurfaceIds;
  private BurstHolder mRepeatingRequest = null;
  private final ArrayDeque<BurstHolder> mRequestQueue = new ArrayDeque();
  
  public RequestQueue(List<Long> paramList)
  {
    this.mJpegSurfaceIds = paramList;
  }
  
  private long calculateLastFrame(int paramInt)
  {
    long l1 = this.mCurrentFrameNumber;
    Iterator localIterator = this.mRequestQueue.iterator();
    while (localIterator.hasNext())
    {
      BurstHolder localBurstHolder = (BurstHolder)localIterator.next();
      long l2 = l1 + localBurstHolder.getNumberOfRequests();
      l1 = l2;
      if (localBurstHolder.getRequestId() == paramInt) {
        return l2 - 1L;
      }
    }
    throw new IllegalStateException("At least one request must be in the queue to calculate frame number");
  }
  
  public Pair<BurstHolder, Long> getNext()
  {
    try
    {
      Object localObject3 = (BurstHolder)this.mRequestQueue.poll();
      Object localObject1 = localObject3;
      if (localObject3 == null)
      {
        localObject1 = localObject3;
        if (this.mRepeatingRequest != null)
        {
          localObject1 = this.mRepeatingRequest;
          this.mCurrentRepeatingFrameNumber = (this.mCurrentFrameNumber + ((BurstHolder)localObject1).getNumberOfRequests());
        }
      }
      if (localObject1 == null) {
        return null;
      }
      localObject3 = new Pair(localObject1, Long.valueOf(this.mCurrentFrameNumber));
      this.mCurrentFrameNumber += ((BurstHolder)localObject1).getNumberOfRequests();
      return (Pair<BurstHolder, Long>)localObject3;
    }
    finally {}
  }
  
  public long stopRepeating()
  {
    try
    {
      if (this.mRepeatingRequest == null)
      {
        Log.e("RequestQueue", "cancel failed: no repeating request exists.");
        return -1L;
      }
      long l = stopRepeating(this.mRepeatingRequest.getRequestId());
      return l;
    }
    finally {}
  }
  
  /* Error */
  public long stopRepeating(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc2_w 7
    //   5: lstore_2
    //   6: aload_0
    //   7: getfield 31	android/hardware/camera2/legacy/RequestQueue:mRepeatingRequest	Landroid/hardware/camera2/legacy/BurstHolder;
    //   10: ifnull +63 -> 73
    //   13: aload_0
    //   14: getfield 31	android/hardware/camera2/legacy/RequestQueue:mRepeatingRequest	Landroid/hardware/camera2/legacy/BurstHolder;
    //   17: invokevirtual 74	android/hardware/camera2/legacy/BurstHolder:getRequestId	()I
    //   20: iload_1
    //   21: if_icmpne +52 -> 73
    //   24: aload_0
    //   25: aconst_null
    //   26: putfield 31	android/hardware/camera2/legacy/RequestQueue:mRepeatingRequest	Landroid/hardware/camera2/legacy/BurstHolder;
    //   29: aload_0
    //   30: getfield 40	android/hardware/camera2/legacy/RequestQueue:mCurrentRepeatingFrameNumber	J
    //   33: ldc2_w 7
    //   36: lcmp
    //   37: ifne +26 -> 63
    //   40: ldc2_w 7
    //   43: lstore_2
    //   44: aload_0
    //   45: ldc2_w 7
    //   48: putfield 40	android/hardware/camera2/legacy/RequestQueue:mCurrentRepeatingFrameNumber	J
    //   51: ldc 12
    //   53: ldc 112
    //   55: invokestatic 115	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   58: pop
    //   59: aload_0
    //   60: monitorexit
    //   61: lload_2
    //   62: lreturn
    //   63: aload_0
    //   64: getfield 40	android/hardware/camera2/legacy/RequestQueue:mCurrentRepeatingFrameNumber	J
    //   67: lconst_1
    //   68: lsub
    //   69: lstore_2
    //   70: goto -26 -> 44
    //   73: ldc 12
    //   75: new 117	java/lang/StringBuilder
    //   78: dup
    //   79: invokespecial 118	java/lang/StringBuilder:<init>	()V
    //   82: ldc 120
    //   84: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: iload_1
    //   88: invokevirtual 127	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   91: invokevirtual 131	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   94: invokestatic 108	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   97: pop
    //   98: goto -39 -> 59
    //   101: astore 4
    //   103: aload_0
    //   104: monitorexit
    //   105: aload 4
    //   107: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	this	RequestQueue
    //   0	108	1	paramInt	int
    //   5	65	2	l	long
    //   101	5	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   6	40	101	finally
    //   44	59	101	finally
    //   63	70	101	finally
    //   73	98	101	finally
  }
  
  /* Error */
  public android.hardware.camera2.utils.SubmitInfo submit(android.hardware.camera2.CaptureRequest[] paramArrayOfCaptureRequest, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 42	android/hardware/camera2/legacy/RequestQueue:mCurrentRequestId	I
    //   6: istore_3
    //   7: aload_0
    //   8: iload_3
    //   9: iconst_1
    //   10: iadd
    //   11: putfield 42	android/hardware/camera2/legacy/RequestQueue:mCurrentRequestId	I
    //   14: new 67	android/hardware/camera2/legacy/BurstHolder
    //   17: dup
    //   18: iload_3
    //   19: iload_2
    //   20: aload_1
    //   21: aload_0
    //   22: getfield 44	android/hardware/camera2/legacy/RequestQueue:mJpegSurfaceIds	Ljava/util/List;
    //   25: invokespecial 136	android/hardware/camera2/legacy/BurstHolder:<init>	(IZ[Landroid/hardware/camera2/CaptureRequest;Ljava/util/Collection;)V
    //   28: astore_1
    //   29: ldc2_w 7
    //   32: lstore 4
    //   34: aload_1
    //   35: invokevirtual 139	android/hardware/camera2/legacy/BurstHolder:isRepeating	()Z
    //   38: ifeq +72 -> 110
    //   41: ldc 12
    //   43: ldc -115
    //   45: invokestatic 115	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   48: pop
    //   49: aload_0
    //   50: getfield 31	android/hardware/camera2/legacy/RequestQueue:mRepeatingRequest	Landroid/hardware/camera2/legacy/BurstHolder;
    //   53: ifnull +19 -> 72
    //   56: aload_0
    //   57: getfield 40	android/hardware/camera2/legacy/RequestQueue:mCurrentRepeatingFrameNumber	J
    //   60: ldc2_w 7
    //   63: lcmp
    //   64: ifne +35 -> 99
    //   67: ldc2_w 7
    //   70: lstore 4
    //   72: aload_0
    //   73: ldc2_w 7
    //   76: putfield 40	android/hardware/camera2/legacy/RequestQueue:mCurrentRepeatingFrameNumber	J
    //   79: aload_0
    //   80: aload_1
    //   81: putfield 31	android/hardware/camera2/legacy/RequestQueue:mRepeatingRequest	Landroid/hardware/camera2/legacy/BurstHolder;
    //   84: new 143	android/hardware/camera2/utils/SubmitInfo
    //   87: dup
    //   88: iload_3
    //   89: lload 4
    //   91: invokespecial 146	android/hardware/camera2/utils/SubmitInfo:<init>	(IJ)V
    //   94: astore_1
    //   95: aload_0
    //   96: monitorexit
    //   97: aload_1
    //   98: areturn
    //   99: aload_0
    //   100: getfield 40	android/hardware/camera2/legacy/RequestQueue:mCurrentRepeatingFrameNumber	J
    //   103: lconst_1
    //   104: lsub
    //   105: lstore 4
    //   107: goto -35 -> 72
    //   110: aload_0
    //   111: getfield 36	android/hardware/camera2/legacy/RequestQueue:mRequestQueue	Ljava/util/ArrayDeque;
    //   114: aload_1
    //   115: invokevirtual 150	java/util/ArrayDeque:offer	(Ljava/lang/Object;)Z
    //   118: pop
    //   119: aload_0
    //   120: aload_1
    //   121: invokevirtual 74	android/hardware/camera2/legacy/BurstHolder:getRequestId	()I
    //   124: invokespecial 152	android/hardware/camera2/legacy/RequestQueue:calculateLastFrame	(I)J
    //   127: lstore 4
    //   129: goto -45 -> 84
    //   132: astore_1
    //   133: aload_0
    //   134: monitorexit
    //   135: aload_1
    //   136: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	137	0	this	RequestQueue
    //   0	137	1	paramArrayOfCaptureRequest	android.hardware.camera2.CaptureRequest[]
    //   0	137	2	paramBoolean	boolean
    //   6	83	3	i	int
    //   32	96	4	l	long
    // Exception table:
    //   from	to	target	type
    //   2	29	132	finally
    //   34	49	132	finally
    //   49	67	132	finally
    //   72	84	132	finally
    //   84	95	132	finally
    //   99	107	132	finally
    //   110	129	132	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/RequestQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */