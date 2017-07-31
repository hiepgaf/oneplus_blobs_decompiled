package android.hardware.camera2.legacy;

import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.MutableLong;
import android.util.Pair;
import android.view.Surface;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CaptureCollector
{
  private static final boolean DEBUG = false;
  private static final int FLAG_RECEIVED_ALL_JPEG = 3;
  private static final int FLAG_RECEIVED_ALL_PREVIEW = 12;
  private static final int FLAG_RECEIVED_JPEG = 1;
  private static final int FLAG_RECEIVED_JPEG_TS = 2;
  private static final int FLAG_RECEIVED_PREVIEW = 4;
  private static final int FLAG_RECEIVED_PREVIEW_TS = 8;
  private static final int MAX_JPEGS_IN_FLIGHT = 1;
  private static final String TAG = "CaptureCollector";
  private final TreeSet<CaptureHolder> mActiveRequests;
  private final ArrayList<CaptureHolder> mCompletedRequests = new ArrayList();
  private final CameraDeviceState mDeviceState;
  private int mInFlight = 0;
  private int mInFlightPreviews = 0;
  private final Condition mIsEmpty;
  private final ArrayDeque<CaptureHolder> mJpegCaptureQueue;
  private final ArrayDeque<CaptureHolder> mJpegProduceQueue;
  private final ReentrantLock mLock = new ReentrantLock();
  private final int mMaxInFlight;
  private final Condition mNotFull;
  private final ArrayDeque<CaptureHolder> mPreviewCaptureQueue;
  private final ArrayDeque<CaptureHolder> mPreviewProduceQueue;
  private final Condition mPreviewsEmpty;
  
  public CaptureCollector(int paramInt, CameraDeviceState paramCameraDeviceState)
  {
    this.mMaxInFlight = paramInt;
    this.mJpegCaptureQueue = new ArrayDeque(1);
    this.mJpegProduceQueue = new ArrayDeque(1);
    this.mPreviewCaptureQueue = new ArrayDeque(this.mMaxInFlight);
    this.mPreviewProduceQueue = new ArrayDeque(this.mMaxInFlight);
    this.mActiveRequests = new TreeSet();
    this.mIsEmpty = this.mLock.newCondition();
    this.mNotFull = this.mLock.newCondition();
    this.mPreviewsEmpty = this.mLock.newCondition();
    this.mDeviceState = paramCameraDeviceState;
  }
  
  private void onPreviewCompleted()
  {
    this.mInFlightPreviews -= 1;
    if (this.mInFlightPreviews < 0) {
      throw new IllegalStateException("More preview captures completed than requests queued.");
    }
    if (this.mInFlightPreviews == 0) {
      this.mPreviewsEmpty.signalAll();
    }
  }
  
  private void onRequestCompleted(CaptureHolder paramCaptureHolder)
  {
    CaptureHolder.-get0(paramCaptureHolder);
    this.mInFlight -= 1;
    if (this.mInFlight < 0) {
      throw new IllegalStateException("More captures completed than requests queued.");
    }
    this.mCompletedRequests.add(paramCaptureHolder);
    this.mActiveRequests.remove(paramCaptureHolder);
    this.mNotFull.signalAll();
    if (this.mInFlight == 0) {
      this.mIsEmpty.signalAll();
    }
  }
  
  private boolean removeRequestIfCompleted(RequestHolder paramRequestHolder, MutableLong paramMutableLong)
  {
    int i = 0;
    Iterator localIterator = this.mCompletedRequests.iterator();
    while (localIterator.hasNext())
    {
      CaptureHolder localCaptureHolder = (CaptureHolder)localIterator.next();
      if (CaptureHolder.-get0(localCaptureHolder).equals(paramRequestHolder))
      {
        paramMutableLong.value = CaptureHolder.-get1(localCaptureHolder);
        this.mCompletedRequests.remove(i);
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void failAll()
  {
    ReentrantLock localReentrantLock = this.mLock;
    localReentrantLock.lock();
    try
    {
      for (;;)
      {
        CaptureHolder localCaptureHolder = (CaptureHolder)this.mActiveRequests.pollFirst();
        if (localCaptureHolder == null) {
          break;
        }
        localCaptureHolder.setPreviewFailed();
        localCaptureHolder.setJpegFailed();
      }
      this.mPreviewCaptureQueue.clear();
    }
    finally
    {
      localReentrantLock.unlock();
    }
    this.mPreviewProduceQueue.clear();
    this.mJpegCaptureQueue.clear();
    this.mJpegProduceQueue.clear();
    localReentrantLock.unlock();
  }
  
  /* Error */
  public void failNextJpeg()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 82	android/hardware/camera2/legacy/CaptureCollector:mLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore 4
    //   6: aload 4
    //   8: invokevirtual 178	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   11: aload_0
    //   12: getfield 95	android/hardware/camera2/legacy/CaptureCollector:mJpegCaptureQueue	Ljava/util/ArrayDeque;
    //   15: invokevirtual 197	java/util/ArrayDeque:peek	()Ljava/lang/Object;
    //   18: checkcast 6	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder
    //   21: astore_2
    //   22: aload_0
    //   23: getfield 97	android/hardware/camera2/legacy/CaptureCollector:mJpegProduceQueue	Ljava/util/ArrayDeque;
    //   26: invokevirtual 197	java/util/ArrayDeque:peek	()Ljava/lang/Object;
    //   29: checkcast 6	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder
    //   32: astore_3
    //   33: aload_2
    //   34: ifnonnull +46 -> 80
    //   37: aload_3
    //   38: astore_2
    //   39: aload_2
    //   40: ifnull +34 -> 74
    //   43: aload_0
    //   44: getfield 95	android/hardware/camera2/legacy/CaptureCollector:mJpegCaptureQueue	Ljava/util/ArrayDeque;
    //   47: aload_2
    //   48: invokevirtual 198	java/util/ArrayDeque:remove	(Ljava/lang/Object;)Z
    //   51: pop
    //   52: aload_0
    //   53: getfield 97	android/hardware/camera2/legacy/CaptureCollector:mJpegProduceQueue	Ljava/util/ArrayDeque;
    //   56: aload_2
    //   57: invokevirtual 198	java/util/ArrayDeque:remove	(Ljava/lang/Object;)Z
    //   60: pop
    //   61: aload_0
    //   62: getfield 106	android/hardware/camera2/legacy/CaptureCollector:mActiveRequests	Ljava/util/TreeSet;
    //   65: aload_2
    //   66: invokevirtual 140	java/util/TreeSet:remove	(Ljava/lang/Object;)Z
    //   69: pop
    //   70: aload_2
    //   71: invokevirtual 187	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder:setJpegFailed	()V
    //   74: aload 4
    //   76: invokevirtual 190	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   79: return
    //   80: aload_3
    //   81: ifnonnull +6 -> 87
    //   84: goto -45 -> 39
    //   87: aload_2
    //   88: aload_3
    //   89: invokevirtual 202	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder:compareTo	(Landroid/hardware/camera2/legacy/CaptureCollector$CaptureHolder;)I
    //   92: istore_1
    //   93: iload_1
    //   94: ifgt +6 -> 100
    //   97: goto -58 -> 39
    //   100: aload_3
    //   101: astore_2
    //   102: goto -63 -> 39
    //   105: astore_2
    //   106: aload 4
    //   108: invokevirtual 190	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   111: aload_2
    //   112: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	113	0	this	CaptureCollector
    //   92	2	1	i	int
    //   21	81	2	localObject1	Object
    //   105	7	2	localObject2	Object
    //   32	69	3	localCaptureHolder	CaptureHolder
    //   4	103	4	localReentrantLock	ReentrantLock
    // Exception table:
    //   from	to	target	type
    //   11	33	105	finally
    //   43	74	105	finally
    //   87	93	105	finally
  }
  
  /* Error */
  public void failNextPreview()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 82	android/hardware/camera2/legacy/CaptureCollector:mLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore 4
    //   6: aload 4
    //   8: invokevirtual 178	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   11: aload_0
    //   12: getfield 99	android/hardware/camera2/legacy/CaptureCollector:mPreviewCaptureQueue	Ljava/util/ArrayDeque;
    //   15: invokevirtual 197	java/util/ArrayDeque:peek	()Ljava/lang/Object;
    //   18: checkcast 6	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder
    //   21: astore_2
    //   22: aload_0
    //   23: getfield 101	android/hardware/camera2/legacy/CaptureCollector:mPreviewProduceQueue	Ljava/util/ArrayDeque;
    //   26: invokevirtual 197	java/util/ArrayDeque:peek	()Ljava/lang/Object;
    //   29: checkcast 6	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder
    //   32: astore_3
    //   33: aload_2
    //   34: ifnonnull +46 -> 80
    //   37: aload_3
    //   38: astore_2
    //   39: aload_2
    //   40: ifnull +34 -> 74
    //   43: aload_0
    //   44: getfield 99	android/hardware/camera2/legacy/CaptureCollector:mPreviewCaptureQueue	Ljava/util/ArrayDeque;
    //   47: aload_2
    //   48: invokevirtual 198	java/util/ArrayDeque:remove	(Ljava/lang/Object;)Z
    //   51: pop
    //   52: aload_0
    //   53: getfield 101	android/hardware/camera2/legacy/CaptureCollector:mPreviewProduceQueue	Ljava/util/ArrayDeque;
    //   56: aload_2
    //   57: invokevirtual 198	java/util/ArrayDeque:remove	(Ljava/lang/Object;)Z
    //   60: pop
    //   61: aload_0
    //   62: getfield 106	android/hardware/camera2/legacy/CaptureCollector:mActiveRequests	Ljava/util/TreeSet;
    //   65: aload_2
    //   66: invokevirtual 140	java/util/TreeSet:remove	(Ljava/lang/Object;)Z
    //   69: pop
    //   70: aload_2
    //   71: invokevirtual 184	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder:setPreviewFailed	()V
    //   74: aload 4
    //   76: invokevirtual 190	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   79: return
    //   80: aload_3
    //   81: ifnonnull +6 -> 87
    //   84: goto -45 -> 39
    //   87: aload_2
    //   88: aload_3
    //   89: invokevirtual 202	android/hardware/camera2/legacy/CaptureCollector$CaptureHolder:compareTo	(Landroid/hardware/camera2/legacy/CaptureCollector$CaptureHolder;)I
    //   92: istore_1
    //   93: iload_1
    //   94: ifgt +6 -> 100
    //   97: goto -58 -> 39
    //   100: aload_3
    //   101: astore_2
    //   102: goto -63 -> 39
    //   105: astore_2
    //   106: aload 4
    //   108: invokevirtual 190	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   111: aload_2
    //   112: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	113	0	this	CaptureCollector
    //   92	2	1	i	int
    //   21	81	2	localObject1	Object
    //   105	7	2	localObject2	Object
    //   32	69	3	localCaptureHolder	CaptureHolder
    //   4	103	4	localReentrantLock	ReentrantLock
    // Exception table:
    //   from	to	target	type
    //   11	33	105	finally
    //   43	74	105	finally
    //   87	93	105	finally
  }
  
  /* Error */
  public boolean hasPendingPreviewCaptures()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 82	android/hardware/camera2/legacy/CaptureCollector:mLock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: astore_2
    //   5: aload_2
    //   6: invokevirtual 178	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   9: aload_0
    //   10: getfield 99	android/hardware/camera2/legacy/CaptureCollector:mPreviewCaptureQueue	Ljava/util/ArrayDeque;
    //   13: invokevirtual 207	java/util/ArrayDeque:isEmpty	()Z
    //   16: istore_1
    //   17: iload_1
    //   18: ifeq +11 -> 29
    //   21: iconst_0
    //   22: istore_1
    //   23: aload_2
    //   24: invokevirtual 190	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   27: iload_1
    //   28: ireturn
    //   29: iconst_1
    //   30: istore_1
    //   31: goto -8 -> 23
    //   34: astore_3
    //   35: aload_2
    //   36: invokevirtual 190	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   39: aload_3
    //   40: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	CaptureCollector
    //   16	15	1	bool	boolean
    //   4	32	2	localReentrantLock	ReentrantLock
    //   34	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	17	34	finally
  }
  
  public RequestHolder jpegCaptured(long paramLong)
  {
    ReentrantLock localReentrantLock = this.mLock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = (CaptureHolder)this.mJpegCaptureQueue.poll();
      if (localObject1 == null)
      {
        Log.w("CaptureCollector", "jpegCaptured called with no jpeg request on queue!");
        return null;
      }
      ((CaptureHolder)localObject1).setJpegTimestamp(paramLong);
      localObject1 = CaptureHolder.-get0((CaptureHolder)localObject1);
      return (RequestHolder)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public Pair<RequestHolder, Long> jpegProduced()
  {
    ReentrantLock localReentrantLock = this.mLock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = (CaptureHolder)this.mJpegProduceQueue.poll();
      if (localObject1 == null)
      {
        Log.w("CaptureCollector", "jpegProduced called with no jpeg request on queue!");
        return null;
      }
      ((CaptureHolder)localObject1).setJpegProduced();
      localObject1 = new Pair(CaptureHolder.-get0((CaptureHolder)localObject1), Long.valueOf(CaptureHolder.-get1((CaptureHolder)localObject1)));
      return (Pair<RequestHolder, Long>)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public Pair<RequestHolder, Long> previewCaptured(long paramLong)
  {
    ReentrantLock localReentrantLock = this.mLock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = (CaptureHolder)this.mPreviewCaptureQueue.poll();
      if (localObject1 == null) {
        return null;
      }
      ((CaptureHolder)localObject1).setPreviewTimestamp(paramLong);
      localObject1 = new Pair(CaptureHolder.-get0((CaptureHolder)localObject1), Long.valueOf(CaptureHolder.-get1((CaptureHolder)localObject1)));
      return (Pair<RequestHolder, Long>)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public RequestHolder previewProduced()
  {
    ReentrantLock localReentrantLock = this.mLock;
    localReentrantLock.lock();
    try
    {
      Object localObject1 = (CaptureHolder)this.mPreviewProduceQueue.poll();
      if (localObject1 == null)
      {
        Log.w("CaptureCollector", "previewProduced called with no preview request on queue!");
        return null;
      }
      ((CaptureHolder)localObject1).setPreviewProduced();
      localObject1 = CaptureHolder.-get0((CaptureHolder)localObject1);
      return (RequestHolder)localObject1;
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean queueRequest(RequestHolder paramRequestHolder, LegacyRequest paramLegacyRequest, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    paramRequestHolder = new CaptureHolder(paramRequestHolder, paramLegacyRequest);
    long l = paramTimeUnit.toNanos(paramLong);
    paramLegacyRequest = this.mLock;
    paramLegacyRequest.lock();
    for (;;)
    {
      try
      {
        if (!paramRequestHolder.needsJpeg)
        {
          bool = paramRequestHolder.needsPreview;
          if (bool) {
            break;
          }
          throw new IllegalStateException("Request must target at least one output surface!");
        }
      }
      finally
      {
        paramLegacyRequest.unlock();
      }
      boolean bool = true;
    }
    paramLong = l;
    int i;
    if (paramRequestHolder.needsJpeg)
    {
      for (paramLong = l;; paramLong = this.mIsEmpty.awaitNanos(paramLong))
      {
        i = this.mInFlight;
        if (i <= 0) {
          break;
        }
        if (paramLong <= 0L)
        {
          paramLegacyRequest.unlock();
          return false;
        }
      }
      this.mJpegCaptureQueue.add(paramRequestHolder);
      this.mJpegProduceQueue.add(paramRequestHolder);
    }
    if (paramRequestHolder.needsPreview)
    {
      for (;;)
      {
        i = this.mInFlight;
        int j = this.mMaxInFlight;
        if (i < j) {
          break;
        }
        if (paramLong <= 0L)
        {
          paramLegacyRequest.unlock();
          return false;
        }
        paramLong = this.mNotFull.awaitNanos(paramLong);
      }
      this.mPreviewCaptureQueue.add(paramRequestHolder);
      this.mPreviewProduceQueue.add(paramRequestHolder);
      this.mInFlightPreviews += 1;
    }
    this.mActiveRequests.add(paramRequestHolder);
    this.mInFlight += 1;
    paramLegacyRequest.unlock();
    return true;
  }
  
  public boolean waitForEmpty(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    paramLong = paramTimeUnit.toNanos(paramLong);
    paramTimeUnit = this.mLock;
    paramTimeUnit.lock();
    try
    {
      for (;;)
      {
        int i = this.mInFlight;
        if (i <= 0) {
          break;
        }
        if (paramLong <= 0L) {
          return false;
        }
        paramLong = this.mIsEmpty.awaitNanos(paramLong);
      }
      return true;
    }
    finally
    {
      paramTimeUnit.unlock();
    }
  }
  
  public boolean waitForPreviewsEmpty(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    paramLong = paramTimeUnit.toNanos(paramLong);
    paramTimeUnit = this.mLock;
    paramTimeUnit.lock();
    try
    {
      for (;;)
      {
        int i = this.mInFlightPreviews;
        if (i <= 0) {
          break;
        }
        if (paramLong <= 0L) {
          return false;
        }
        paramLong = this.mPreviewsEmpty.awaitNanos(paramLong);
      }
      return true;
    }
    finally
    {
      paramTimeUnit.unlock();
    }
  }
  
  public boolean waitForRequestCompleted(RequestHolder paramRequestHolder, long paramLong, TimeUnit paramTimeUnit, MutableLong paramMutableLong)
    throws InterruptedException
  {
    paramLong = paramTimeUnit.toNanos(paramLong);
    paramTimeUnit = this.mLock;
    paramTimeUnit.lock();
    try
    {
      for (;;)
      {
        boolean bool = removeRequestIfCompleted(paramRequestHolder, paramMutableLong);
        if (bool) {
          break;
        }
        if (paramLong <= 0L) {
          return false;
        }
        paramLong = this.mNotFull.awaitNanos(paramLong);
      }
      return true;
    }
    finally
    {
      paramTimeUnit.unlock();
    }
  }
  
  private class CaptureHolder
    implements Comparable<CaptureHolder>
  {
    private boolean mCompleted = false;
    private boolean mFailedJpeg = false;
    private boolean mFailedPreview = false;
    private boolean mHasStarted = false;
    private final LegacyRequest mLegacy;
    private boolean mPreviewCompleted = false;
    private int mReceivedFlags = 0;
    private final RequestHolder mRequest;
    private long mTimestamp = 0L;
    public final boolean needsJpeg;
    public final boolean needsPreview;
    
    public CaptureHolder(RequestHolder paramRequestHolder, LegacyRequest paramLegacyRequest)
    {
      this.mRequest = paramRequestHolder;
      this.mLegacy = paramLegacyRequest;
      this.needsJpeg = paramRequestHolder.hasJpegTargets();
      this.needsPreview = paramRequestHolder.hasPreviewTargets();
    }
    
    public int compareTo(CaptureHolder paramCaptureHolder)
    {
      if (this.mRequest.getFrameNumber() > paramCaptureHolder.mRequest.getFrameNumber()) {
        return 1;
      }
      if (this.mRequest.getFrameNumber() == paramCaptureHolder.mRequest.getFrameNumber()) {
        return 0;
      }
      return -1;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if ((paramObject instanceof CaptureHolder))
      {
        bool1 = bool2;
        if (compareTo((CaptureHolder)paramObject) == 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public boolean isCompleted()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.needsJpeg == isJpegCompleted())
      {
        bool1 = bool2;
        if (this.needsPreview == isPreviewCompleted()) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public boolean isJpegCompleted()
    {
      return (this.mReceivedFlags & 0x3) == 3;
    }
    
    public boolean isPreviewCompleted()
    {
      return (this.mReceivedFlags & 0xC) == 12;
    }
    
    public void setJpegFailed()
    {
      if ((!this.needsJpeg) || (isJpegCompleted())) {
        return;
      }
      this.mFailedJpeg = true;
      this.mReceivedFlags |= 0x1;
      this.mReceivedFlags |= 0x2;
      tryComplete();
    }
    
    public void setJpegProduced()
    {
      if (!this.needsJpeg) {
        throw new IllegalStateException("setJpegProduced called for capture with no jpeg targets.");
      }
      if (isCompleted()) {
        throw new IllegalStateException("setJpegProduced called on already completed request.");
      }
      this.mReceivedFlags |= 0x1;
      tryComplete();
    }
    
    public void setJpegTimestamp(long paramLong)
    {
      if (!this.needsJpeg) {
        throw new IllegalStateException("setJpegTimestamp called for capture with no jpeg targets.");
      }
      if (isCompleted()) {
        throw new IllegalStateException("setJpegTimestamp called on already completed request.");
      }
      this.mReceivedFlags |= 0x2;
      if (this.mTimestamp == 0L) {
        this.mTimestamp = paramLong;
      }
      if (!this.mHasStarted)
      {
        this.mHasStarted = true;
        CaptureCollector.-get0(CaptureCollector.this).setCaptureStart(this.mRequest, this.mTimestamp, -1);
      }
      tryComplete();
    }
    
    public void setPreviewFailed()
    {
      if ((!this.needsPreview) || (isPreviewCompleted())) {
        return;
      }
      this.mFailedPreview = true;
      this.mReceivedFlags |= 0x4;
      this.mReceivedFlags |= 0x8;
      tryComplete();
    }
    
    public void setPreviewProduced()
    {
      if (!this.needsPreview) {
        throw new IllegalStateException("setPreviewProduced called for capture with no preview targets.");
      }
      if (isCompleted()) {
        throw new IllegalStateException("setPreviewProduced called on already completed request.");
      }
      this.mReceivedFlags |= 0x4;
      tryComplete();
    }
    
    public void setPreviewTimestamp(long paramLong)
    {
      if (!this.needsPreview) {
        throw new IllegalStateException("setPreviewTimestamp called for capture with no preview targets.");
      }
      if (isCompleted()) {
        throw new IllegalStateException("setPreviewTimestamp called on already completed request.");
      }
      this.mReceivedFlags |= 0x8;
      if (this.mTimestamp == 0L) {
        this.mTimestamp = paramLong;
      }
      if ((!this.needsJpeg) && (!this.mHasStarted))
      {
        this.mHasStarted = true;
        CaptureCollector.-get0(CaptureCollector.this).setCaptureStart(this.mRequest, this.mTimestamp, -1);
      }
      tryComplete();
    }
    
    public void tryComplete()
    {
      if ((!this.mPreviewCompleted) && (this.needsPreview) && (isPreviewCompleted()))
      {
        CaptureCollector.-wrap0(CaptureCollector.this);
        this.mPreviewCompleted = true;
      }
      if ((!isCompleted()) || (this.mCompleted)) {
        return;
      }
      if ((this.mFailedPreview) || (this.mFailedJpeg))
      {
        if (this.mHasStarted) {
          break label110;
        }
        this.mRequest.failRequest();
        CaptureCollector.-get0(CaptureCollector.this).setCaptureStart(this.mRequest, this.mTimestamp, 3);
      }
      for (;;)
      {
        CaptureCollector.-wrap1(CaptureCollector.this, this);
        this.mCompleted = true;
        return;
        label110:
        Iterator localIterator = this.mRequest.getRequest().getTargets().iterator();
        while (localIterator.hasNext())
        {
          Surface localSurface = (Surface)localIterator.next();
          try
          {
            if (!this.mRequest.jpegType(localSurface)) {
              break label213;
            }
            if (!this.mFailedJpeg) {
              continue;
            }
            CaptureCollector.-get0(CaptureCollector.this).setCaptureResult(this.mRequest, null, 5, localSurface);
          }
          catch (LegacyExceptionUtils.BufferQueueAbandonedException localBufferQueueAbandonedException)
          {
            Log.e("CaptureCollector", "Unexpected exception when querying Surface: " + localBufferQueueAbandonedException);
          }
          continue;
          label213:
          if (this.mFailedPreview) {
            CaptureCollector.-get0(CaptureCollector.this).setCaptureResult(this.mRequest, null, 5, localBufferQueueAbandonedException);
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/CaptureCollector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */