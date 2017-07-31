package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.dispatch.ArgumentReplacingDispatcher;
import android.hardware.camera2.dispatch.BroadcastDispatcher;
import android.hardware.camera2.dispatch.Dispatchable;
import android.hardware.camera2.dispatch.DuckTypingDispatcher;
import android.hardware.camera2.dispatch.HandlerDispatcher;
import android.hardware.camera2.dispatch.InvokeDispatcher;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.utils.TaskDrainer;
import android.hardware.camera2.utils.TaskDrainer.DrainListener;
import android.hardware.camera2.utils.TaskSingleDrainer;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.Iterator;
import java.util.List;

public class CameraCaptureSessionImpl
  extends CameraCaptureSession
  implements CameraCaptureSessionCore
{
  private static final boolean DEBUG = false;
  private static final String TAG = "CameraCaptureSession";
  private final TaskSingleDrainer mAbortDrainer;
  private volatile boolean mAborting;
  private boolean mClosed = false;
  private final boolean mConfigureSuccess;
  private final Handler mDeviceHandler;
  private final CameraDeviceImpl mDeviceImpl;
  private final int mId;
  private final String mIdString;
  private final TaskSingleDrainer mIdleDrainer;
  private final Surface mInput;
  private final List<Surface> mOutputs;
  private final TaskDrainer<Integer> mSequenceDrainer;
  private boolean mSkipUnconfigure = false;
  private final CameraCaptureSession.StateCallback mStateCallback;
  private final Handler mStateHandler;
  
  CameraCaptureSessionImpl(int paramInt, Surface paramSurface, List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler1, CameraDeviceImpl paramCameraDeviceImpl, Handler paramHandler2, boolean paramBoolean)
  {
    if ((paramList == null) || (paramList.isEmpty())) {
      throw new IllegalArgumentException("outputs must be a non-null, non-empty list");
    }
    if (paramStateCallback == null) {
      throw new IllegalArgumentException("callback must not be null");
    }
    this.mId = paramInt;
    this.mIdString = String.format("Session %d: ", new Object[] { Integer.valueOf(this.mId) });
    this.mOutputs = paramList;
    this.mInput = paramSurface;
    this.mStateHandler = CameraDeviceImpl.checkHandler(paramHandler1);
    this.mStateCallback = createUserStateCallbackProxy(this.mStateHandler, paramStateCallback);
    this.mDeviceHandler = ((Handler)Preconditions.checkNotNull(paramHandler2, "deviceStateHandler must not be null"));
    this.mDeviceImpl = ((CameraDeviceImpl)Preconditions.checkNotNull(paramCameraDeviceImpl, "deviceImpl must not be null"));
    this.mSequenceDrainer = new TaskDrainer(this.mDeviceHandler, new SequenceDrainListener(null), "seq");
    this.mIdleDrainer = new TaskSingleDrainer(this.mDeviceHandler, new IdleDrainListener(null), "idle");
    this.mAbortDrainer = new TaskSingleDrainer(this.mDeviceHandler, new AbortDrainListener(null), "abort");
    if (paramBoolean)
    {
      this.mStateCallback.onConfigured(this);
      this.mConfigureSuccess = true;
      return;
    }
    this.mStateCallback.onConfigureFailed(this);
    this.mClosed = true;
    Log.e("CameraCaptureSession", this.mIdString + "Failed to create capture session; configuration failed");
    this.mConfigureSuccess = false;
  }
  
  private int addPendingSequence(int paramInt)
  {
    this.mSequenceDrainer.taskStarted(Integer.valueOf(paramInt));
    return paramInt;
  }
  
  private void checkNotClosed()
  {
    if (this.mClosed) {
      throw new IllegalStateException("Session has been closed; further changes are illegal.");
    }
  }
  
  private CameraDeviceImpl.CaptureCallback createCaptureCallbackProxy(Handler paramHandler, CameraCaptureSession.CaptureCallback paramCaptureCallback)
  {
    Object localObject = new CameraDeviceImpl.CaptureCallback()
    {
      public void onCaptureSequenceAborted(CameraDevice paramAnonymousCameraDevice, int paramAnonymousInt)
      {
        CameraCaptureSessionImpl.-wrap0(CameraCaptureSessionImpl.this, paramAnonymousInt);
      }
      
      public void onCaptureSequenceCompleted(CameraDevice paramAnonymousCameraDevice, int paramAnonymousInt, long paramAnonymousLong)
      {
        CameraCaptureSessionImpl.-wrap0(CameraCaptureSessionImpl.this, paramAnonymousInt);
      }
    };
    if (paramCaptureCallback == null) {
      return (CameraDeviceImpl.CaptureCallback)localObject;
    }
    localObject = new InvokeDispatcher(localObject);
    return new CallbackProxies.DeviceCaptureCallbackProxy(new BroadcastDispatcher(new Dispatchable[] { new ArgumentReplacingDispatcher(new DuckTypingDispatcher(new HandlerDispatcher(new InvokeDispatcher(paramCaptureCallback), paramHandler), CameraCaptureSession.CaptureCallback.class), 0, this), localObject }));
  }
  
  private CameraCaptureSession.StateCallback createUserStateCallbackProxy(Handler paramHandler, CameraCaptureSession.StateCallback paramStateCallback)
  {
    return new CallbackProxies.SessionStateCallbackProxy(new HandlerDispatcher(new InvokeDispatcher(paramStateCallback), paramHandler));
  }
  
  private void finishPendingSequence(int paramInt)
  {
    try
    {
      this.mSequenceDrainer.taskFinished(Integer.valueOf(paramInt));
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Log.w("CameraCaptureSession", localIllegalStateException.getMessage());
    }
  }
  
  /* Error */
  public void abortCaptures()
    throws CameraAccessException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 280	android/hardware/camera2/impl/CameraCaptureSessionImpl:checkNotClosed	()V
    //   6: aload_0
    //   7: getfield 60	android/hardware/camera2/impl/CameraCaptureSessionImpl:mAborting	Z
    //   10: ifeq +35 -> 45
    //   13: ldc 26
    //   15: new 194	java/lang/StringBuilder
    //   18: dup
    //   19: invokespecial 195	java/lang/StringBuilder:<init>	()V
    //   22: aload_0
    //   23: getfield 68	android/hardware/camera2/impl/CameraCaptureSessionImpl:mIdString	Ljava/lang/String;
    //   26: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: ldc_w 282
    //   32: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: invokevirtual 205	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   38: invokestatic 275	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   41: pop
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: aload_0
    //   46: iconst_1
    //   47: putfield 60	android/hardware/camera2/impl/CameraCaptureSessionImpl:mAborting	Z
    //   50: aload_0
    //   51: getfield 55	android/hardware/camera2/impl/CameraCaptureSessionImpl:mAbortDrainer	Landroid/hardware/camera2/utils/TaskSingleDrainer;
    //   54: invokevirtual 284	android/hardware/camera2/utils/TaskSingleDrainer:taskStarted	()V
    //   57: aload_0
    //   58: monitorexit
    //   59: aload_0
    //   60: getfield 64	android/hardware/camera2/impl/CameraCaptureSessionImpl:mDeviceImpl	Landroid/hardware/camera2/impl/CameraDeviceImpl;
    //   63: getfield 288	android/hardware/camera2/impl/CameraDeviceImpl:mInterfaceLock	Ljava/lang/Object;
    //   66: astore_1
    //   67: aload_1
    //   68: monitorenter
    //   69: aload_0
    //   70: monitorenter
    //   71: aload_0
    //   72: getfield 64	android/hardware/camera2/impl/CameraCaptureSessionImpl:mDeviceImpl	Landroid/hardware/camera2/impl/CameraDeviceImpl;
    //   75: invokevirtual 291	android/hardware/camera2/impl/CameraDeviceImpl:flush	()V
    //   78: aload_0
    //   79: monitorexit
    //   80: aload_1
    //   81: monitorexit
    //   82: return
    //   83: astore_1
    //   84: aload_0
    //   85: monitorexit
    //   86: aload_1
    //   87: athrow
    //   88: astore_2
    //   89: aload_0
    //   90: monitorexit
    //   91: aload_2
    //   92: athrow
    //   93: astore_2
    //   94: aload_1
    //   95: monitorexit
    //   96: aload_2
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	CameraCaptureSessionImpl
    //   83	12	1	localObject2	Object
    //   88	4	2	localObject3	Object
    //   93	4	2	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   2	42	83	finally
    //   45	57	83	finally
    //   71	78	88	finally
    //   69	71	93	finally
    //   78	80	93	finally
    //   89	93	93	finally
  }
  
  public int capture(CaptureRequest paramCaptureRequest, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (paramCaptureRequest == null) {
      try
      {
        throw new IllegalArgumentException("request must not be null");
      }
      finally {}
    }
    if ((!paramCaptureRequest.isReprocess()) || (isReprocessable()))
    {
      if ((paramCaptureRequest.isReprocess()) && (paramCaptureRequest.getReprocessableSessionId() != this.mId)) {
        throw new IllegalArgumentException("capture request was created for another session");
      }
    }
    else {
      throw new IllegalArgumentException("this capture session cannot handle reprocess requests");
    }
    checkNotClosed();
    paramHandler = CameraDeviceImpl.checkHandler(paramHandler, paramCaptureCallback);
    int i = addPendingSequence(this.mDeviceImpl.capture(paramCaptureRequest, createCaptureCallbackProxy(paramHandler, paramCaptureCallback), this.mDeviceHandler));
    return i;
  }
  
  public int captureBurst(List<CaptureRequest> paramList, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (paramList == null) {
      try
      {
        throw new IllegalArgumentException("Requests must not be null");
      }
      finally {}
    }
    if (paramList.isEmpty()) {
      throw new IllegalArgumentException("Requests must have at least one element");
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      CaptureRequest localCaptureRequest = (CaptureRequest)localIterator.next();
      if (localCaptureRequest.isReprocess())
      {
        if (!isReprocessable()) {
          throw new IllegalArgumentException("This capture session cannot handle reprocess requests");
        }
        if (localCaptureRequest.getReprocessableSessionId() != this.mId) {
          throw new IllegalArgumentException("Capture request was created for another session");
        }
      }
    }
    checkNotClosed();
    paramHandler = CameraDeviceImpl.checkHandler(paramHandler, paramCaptureCallback);
    int i = addPendingSequence(this.mDeviceImpl.captureBurst(paramList, createCaptureCallbackProxy(paramHandler, paramCaptureCallback), this.mDeviceHandler));
    return i;
  }
  
  /* Error */
  public void close()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 93	android/hardware/camera2/impl/CameraCaptureSessionImpl:mClosed	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: iconst_1
    //   16: putfield 93	android/hardware/camera2/impl/CameraCaptureSessionImpl:mClosed	Z
    //   19: aload_0
    //   20: monitorexit
    //   21: aload_0
    //   22: getfield 64	android/hardware/camera2/impl/CameraCaptureSessionImpl:mDeviceImpl	Landroid/hardware/camera2/impl/CameraDeviceImpl;
    //   25: getfield 288	android/hardware/camera2/impl/CameraDeviceImpl:mInterfaceLock	Ljava/lang/Object;
    //   28: astore_2
    //   29: aload_2
    //   30: monitorenter
    //   31: aload_0
    //   32: monitorenter
    //   33: aload_0
    //   34: getfield 64	android/hardware/camera2/impl/CameraCaptureSessionImpl:mDeviceImpl	Landroid/hardware/camera2/impl/CameraDeviceImpl;
    //   37: invokevirtual 355	android/hardware/camera2/impl/CameraDeviceImpl:stopRepeating	()V
    //   40: aload_0
    //   41: monitorexit
    //   42: aload_2
    //   43: monitorexit
    //   44: aload_0
    //   45: monitorenter
    //   46: aload_0
    //   47: getfield 168	android/hardware/camera2/impl/CameraCaptureSessionImpl:mSequenceDrainer	Landroid/hardware/camera2/utils/TaskDrainer;
    //   50: invokevirtual 358	android/hardware/camera2/utils/TaskDrainer:beginDrain	()V
    //   53: aload_0
    //   54: monitorexit
    //   55: return
    //   56: astore_2
    //   57: aload_0
    //   58: monitorexit
    //   59: aload_2
    //   60: athrow
    //   61: astore_3
    //   62: ldc 26
    //   64: new 194	java/lang/StringBuilder
    //   67: dup
    //   68: invokespecial 195	java/lang/StringBuilder:<init>	()V
    //   71: aload_0
    //   72: getfield 68	android/hardware/camera2/impl/CameraCaptureSessionImpl:mIdString	Ljava/lang/String;
    //   75: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: ldc_w 360
    //   81: invokevirtual 199	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: invokevirtual 205	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   87: aload_3
    //   88: invokestatic 363	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   91: pop
    //   92: goto -52 -> 40
    //   95: astore_3
    //   96: aload_0
    //   97: monitorexit
    //   98: aload_3
    //   99: athrow
    //   100: astore_3
    //   101: aload_2
    //   102: monitorexit
    //   103: aload_3
    //   104: athrow
    //   105: astore_3
    //   106: aload_0
    //   107: getfield 78	android/hardware/camera2/impl/CameraCaptureSessionImpl:mStateCallback	Landroid/hardware/camera2/CameraCaptureSession$StateCallback;
    //   110: aload_0
    //   111: invokevirtual 366	android/hardware/camera2/CameraCaptureSession$StateCallback:onClosed	(Landroid/hardware/camera2/CameraCaptureSession;)V
    //   114: aload_0
    //   115: monitorexit
    //   116: aload_2
    //   117: monitorexit
    //   118: return
    //   119: astore_2
    //   120: aload_0
    //   121: monitorexit
    //   122: aload_2
    //   123: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	this	CameraCaptureSessionImpl
    //   6	2	1	bool	boolean
    //   28	15	2	localObject1	Object
    //   56	61	2	localObject2	Object
    //   119	4	2	localObject3	Object
    //   61	27	3	localCameraAccessException	CameraAccessException
    //   95	4	3	localObject4	Object
    //   100	4	3	localObject5	Object
    //   105	1	3	localIllegalStateException	IllegalStateException
    // Exception table:
    //   from	to	target	type
    //   2	7	56	finally
    //   14	19	56	finally
    //   33	40	61	android/hardware/camera2/CameraAccessException
    //   33	40	95	finally
    //   62	92	95	finally
    //   106	114	95	finally
    //   31	33	100	finally
    //   40	42	100	finally
    //   96	100	100	finally
    //   114	116	100	finally
    //   33	40	105	java/lang/IllegalStateException
    //   46	53	119	finally
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void finishDeferredConfiguration(List<OutputConfiguration> paramList)
    throws CameraAccessException
  {
    this.mDeviceImpl.finishDeferredConfig(paramList);
  }
  
  public CameraDevice getDevice()
  {
    return this.mDeviceImpl;
  }
  
  public CameraDeviceImpl.StateCallbackKK getDeviceStateCallback()
  {
    new CameraDeviceImpl.StateCallbackKK()
    {
      private boolean mActive = false;
      private boolean mBusy = false;
      
      public void onActive(CameraDevice paramAnonymousCameraDevice)
      {
        CameraCaptureSessionImpl.-get4(CameraCaptureSessionImpl.this).taskStarted();
        this.mActive = true;
        CameraCaptureSessionImpl.-get6(CameraCaptureSessionImpl.this).onActive(jdField_this);
      }
      
      public void onBusy(CameraDevice paramAnonymousCameraDevice)
      {
        this.mBusy = true;
      }
      
      public void onDisconnected(CameraDevice paramAnonymousCameraDevice)
      {
        CameraCaptureSessionImpl.this.close();
      }
      
      public void onError(CameraDevice paramAnonymousCameraDevice, int paramAnonymousInt)
      {
        Log.wtf("CameraCaptureSession", CameraCaptureSessionImpl.-get3(CameraCaptureSessionImpl.this) + "Got device error " + paramAnonymousInt);
      }
      
      public void onIdle(CameraDevice arg1)
      {
        synchronized (jdField_this)
        {
          boolean bool = CameraCaptureSessionImpl.-get1(CameraCaptureSessionImpl.this);
          if ((this.mBusy) && (bool)) {
            CameraCaptureSessionImpl.-get0(CameraCaptureSessionImpl.this).taskFinished();
          }
        }
        synchronized (jdField_this)
        {
          CameraCaptureSessionImpl.-set0(CameraCaptureSessionImpl.this, false);
          if (this.mActive) {
            CameraCaptureSessionImpl.-get4(CameraCaptureSessionImpl.this).taskFinished();
          }
          this.mBusy = false;
          this.mActive = false;
          CameraCaptureSessionImpl.-get6(CameraCaptureSessionImpl.this).onReady(jdField_this);
          return;
          localObject1 = finally;
          throw ((Throwable)localObject1);
        }
      }
      
      public void onOpened(CameraDevice paramAnonymousCameraDevice)
      {
        throw new AssertionError("Camera must already be open before creating a session");
      }
      
      public void onSurfacePrepared(Surface paramAnonymousSurface)
      {
        CameraCaptureSessionImpl.-get6(CameraCaptureSessionImpl.this).onSurfacePrepared(jdField_this, paramAnonymousSurface);
      }
      
      public void onUnconfigured(CameraDevice paramAnonymousCameraDevice) {}
    };
  }
  
  public Surface getInputSurface()
  {
    return this.mInput;
  }
  
  public boolean isAborting()
  {
    return this.mAborting;
  }
  
  public boolean isReprocessable()
  {
    return this.mInput != null;
  }
  
  public void prepare(int paramInt, Surface paramSurface)
    throws CameraAccessException
  {
    this.mDeviceImpl.prepare(paramInt, paramSurface);
  }
  
  public void prepare(Surface paramSurface)
    throws CameraAccessException
  {
    this.mDeviceImpl.prepare(paramSurface);
  }
  
  public void replaceSessionClose()
  {
    try
    {
      this.mSkipUnconfigure = true;
      close();
      return;
    }
    finally {}
  }
  
  public int setRepeatingBurst(List<CaptureRequest> paramList, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (paramList == null) {
      try
      {
        throw new IllegalArgumentException("requests must not be null");
      }
      finally {}
    }
    if (paramList.isEmpty()) {
      throw new IllegalArgumentException("requests must have at least one element");
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext()) {
      if (((CaptureRequest)localIterator.next()).isReprocess()) {
        throw new IllegalArgumentException("repeating reprocess burst requests are not supported");
      }
    }
    checkNotClosed();
    paramHandler = CameraDeviceImpl.checkHandler(paramHandler, paramCaptureCallback);
    int i = addPendingSequence(this.mDeviceImpl.setRepeatingBurst(paramList, createCaptureCallbackProxy(paramHandler, paramCaptureCallback), this.mDeviceHandler));
    return i;
  }
  
  public int setRepeatingRequest(CaptureRequest paramCaptureRequest, CameraCaptureSession.CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (paramCaptureRequest == null) {
      try
      {
        throw new IllegalArgumentException("request must not be null");
      }
      finally {}
    }
    if (paramCaptureRequest.isReprocess()) {
      throw new IllegalArgumentException("repeating reprocess requests are not supported");
    }
    checkNotClosed();
    paramHandler = CameraDeviceImpl.checkHandler(paramHandler, paramCaptureCallback);
    int i = addPendingSequence(this.mDeviceImpl.setRepeatingRequest(paramCaptureRequest, createCaptureCallbackProxy(paramHandler, paramCaptureCallback), this.mDeviceHandler));
    return i;
  }
  
  public void stopRepeating()
    throws CameraAccessException
  {
    try
    {
      checkNotClosed();
      this.mDeviceImpl.stopRepeating();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void tearDown(Surface paramSurface)
    throws CameraAccessException
  {
    this.mDeviceImpl.tearDown(paramSurface);
  }
  
  private class AbortDrainListener
    implements TaskDrainer.DrainListener
  {
    private AbortDrainListener() {}
    
    public void onDrained()
    {
      synchronized (CameraCaptureSessionImpl.this)
      {
        boolean bool = CameraCaptureSessionImpl.-get5(CameraCaptureSessionImpl.this);
        if (bool) {
          return;
        }
        CameraCaptureSessionImpl.-get4(CameraCaptureSessionImpl.this).beginDrain();
        return;
      }
    }
  }
  
  private class IdleDrainListener
    implements TaskDrainer.DrainListener
  {
    private IdleDrainListener() {}
    
    public void onDrained()
    {
      synchronized (CameraCaptureSessionImpl.-get2(CameraCaptureSessionImpl.this).mInterfaceLock)
      {
        synchronized (CameraCaptureSessionImpl.this)
        {
          boolean bool = CameraCaptureSessionImpl.-get5(CameraCaptureSessionImpl.this);
          if (bool) {
            return;
          }
        }
      }
      try
      {
        CameraCaptureSessionImpl.-get2(CameraCaptureSessionImpl.this).configureStreamsChecked(null, null, false);
        return;
      }
      catch (CameraAccessException localCameraAccessException)
      {
        for (;;)
        {
          Log.e("CameraCaptureSession", CameraCaptureSessionImpl.-get3(CameraCaptureSessionImpl.this) + "Exception while unconfiguring outputs: ", localCameraAccessException);
        }
        localObject3 = finally;
        throw ((Throwable)localObject3);
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
      catch (IllegalStateException localIllegalStateException)
      {
        for (;;) {}
      }
    }
  }
  
  private class SequenceDrainListener
    implements TaskDrainer.DrainListener
  {
    private SequenceDrainListener() {}
    
    public void onDrained()
    {
      CameraCaptureSessionImpl.-get6(CameraCaptureSessionImpl.this).onClosed(CameraCaptureSessionImpl.this);
      if (CameraCaptureSessionImpl.-get5(CameraCaptureSessionImpl.this)) {
        return;
      }
      CameraCaptureSessionImpl.-get0(CameraCaptureSessionImpl.this).beginDrain();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/CameraCaptureSessionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */