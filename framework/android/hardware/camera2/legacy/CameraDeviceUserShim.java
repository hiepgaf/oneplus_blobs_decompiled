package android.hardware.camera2.legacy;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.system.OsConstants;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;

public class CameraDeviceUserShim
  implements ICameraDeviceUser
{
  private static final boolean DEBUG = false;
  private static final int OPEN_CAMERA_TIMEOUT_MS = 5000;
  private static final String TAG = "CameraDeviceUserShim";
  private final CameraCallbackThread mCameraCallbacks;
  private final CameraCharacteristics mCameraCharacteristics;
  private final CameraLooper mCameraInit;
  private final Object mConfigureLock = new Object();
  private boolean mConfiguring;
  private final LegacyCameraDevice mLegacyDevice;
  private int mSurfaceIdCounter;
  private final SparseArray<Surface> mSurfaces;
  
  protected CameraDeviceUserShim(int paramInt, LegacyCameraDevice paramLegacyCameraDevice, CameraCharacteristics paramCameraCharacteristics, CameraLooper paramCameraLooper, CameraCallbackThread paramCameraCallbackThread)
  {
    this.mLegacyDevice = paramLegacyCameraDevice;
    this.mConfiguring = false;
    this.mSurfaces = new SparseArray();
    this.mCameraCharacteristics = paramCameraCharacteristics;
    this.mCameraInit = paramCameraLooper;
    this.mCameraCallbacks = paramCameraCallbackThread;
    this.mSurfaceIdCounter = 0;
  }
  
  public static CameraDeviceUserShim connectBinderShim(ICameraDeviceCallbacks paramICameraDeviceCallbacks, int paramInt)
  {
    CameraLooper localCameraLooper = new CameraLooper(paramInt);
    paramICameraDeviceCallbacks = new CameraCallbackThread(paramICameraDeviceCallbacks);
    int i = localCameraLooper.waitForOpen(5000);
    Camera localCamera = localCameraLooper.getCamera();
    LegacyExceptionUtils.throwOnServiceError(i);
    localCamera.disableShutterSound();
    Object localObject = new Camera.CameraInfo();
    Camera.getCameraInfo(paramInt, (Camera.CameraInfo)localObject);
    try
    {
      Camera.Parameters localParameters = localCamera.getParameters();
      localObject = LegacyMetadataMapper.createCharacteristics(localParameters, (Camera.CameraInfo)localObject);
      return new CameraDeviceUserShim(paramInt, new LegacyCameraDevice(paramInt, localCamera, (CameraCharacteristics)localObject, paramICameraDeviceCallbacks), (CameraCharacteristics)localObject, localCameraLooper, paramICameraDeviceCallbacks);
    }
    catch (RuntimeException paramICameraDeviceCallbacks)
    {
      throw new ServiceSpecificException(10, "Unable to get initial parameters: " + paramICameraDeviceCallbacks.getMessage());
    }
  }
  
  private static int translateErrorsFromCamera1(int paramInt)
  {
    if (paramInt == -OsConstants.EACCES) {
      return 1;
    }
    return paramInt;
  }
  
  public IBinder asBinder()
  {
    return null;
  }
  
  public void beginConfigure()
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot begin configure, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot begin configure, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot begin configure, configuration change already in progress.");
        throw new ServiceSpecificException(10, "Cannot begin configure, configuration change already in progress.");
      }
    }
    this.mConfiguring = true;
  }
  
  public long cancelRequest(int paramInt)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot cancel request, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot cancel request, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot cancel request, configuration change in progress.");
        throw new ServiceSpecificException(10, "Cannot cancel request, configuration change in progress.");
      }
    }
    return this.mLegacyDevice.cancelRequest(paramInt);
  }
  
  public CameraMetadataNative createDefaultRequest(int paramInt)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot create default request, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot create default request, device has been closed.");
    }
    try
    {
      CameraMetadataNative localCameraMetadataNative = LegacyMetadataMapper.createRequestTemplate(this.mCameraCharacteristics, paramInt);
      return localCameraMetadataNative;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.e("CameraDeviceUserShim", "createDefaultRequest - invalid templateId specified");
      throw new ServiceSpecificException(3, "createDefaultRequest - invalid templateId specified");
    }
  }
  
  public int createInputStream(int paramInt1, int paramInt2, int paramInt3)
  {
    Log.e("CameraDeviceUserShim", "Creating input stream is not supported on legacy devices");
    throw new ServiceSpecificException(10, "Creating input stream is not supported on legacy devices");
  }
  
  public int createStream(OutputConfiguration paramOutputConfiguration)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot create stream, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot create stream, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (!this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot create stream, beginConfigure hasn't been called yet.");
        throw new ServiceSpecificException(10, "Cannot create stream, beginConfigure hasn't been called yet.");
      }
    }
    if (paramOutputConfiguration.getRotation() != 0)
    {
      Log.e("CameraDeviceUserShim", "Cannot create stream, stream rotation is not supported.");
      throw new ServiceSpecificException(3, "Cannot create stream, stream rotation is not supported.");
    }
    int i = this.mSurfaceIdCounter + 1;
    this.mSurfaceIdCounter = i;
    this.mSurfaces.put(i, paramOutputConfiguration.getSurface());
    return i;
  }
  
  public void deleteStream(int paramInt)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot delete stream, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot delete stream, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (!this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot delete stream, no configuration change in progress.");
        throw new ServiceSpecificException(10, "Cannot delete stream, no configuration change in progress.");
      }
    }
    int i = this.mSurfaces.indexOfKey(paramInt);
    if (i < 0)
    {
      String str = "Cannot delete stream, stream id " + paramInt + " doesn't exist.";
      Log.e("CameraDeviceUserShim", str);
      throw new ServiceSpecificException(3, str);
    }
    this.mSurfaces.removeAt(i);
  }
  
  public void disconnect()
  {
    if (this.mLegacyDevice.isClosed()) {
      Log.w("CameraDeviceUserShim", "Cannot disconnect, device has already been closed.");
    }
    try
    {
      this.mLegacyDevice.close();
      return;
    }
    finally
    {
      this.mCameraInit.close();
      this.mCameraCallbacks.close();
    }
  }
  
  public void endConfigure(boolean paramBoolean)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot end configure, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot end configure, device has been closed.");
    }
    Object localObject1 = null;
    synchronized (this.mConfigureLock)
    {
      if (!this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot end configure, no configuration change in progress.");
        throw new ServiceSpecificException(10, "Cannot end configure, no configuration change in progress.");
      }
    }
    SparseArray localSparseArray;
    if (this.mSurfaces != null) {
      localSparseArray = this.mSurfaces.clone();
    }
    this.mConfiguring = false;
    this.mLegacyDevice.configureOutputs(localSparseArray);
  }
  
  public long flush()
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot flush, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot flush, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot flush, configuration change in progress.");
        throw new ServiceSpecificException(10, "Cannot flush, configuration change in progress.");
      }
    }
    return this.mLegacyDevice.flush();
  }
  
  public CameraMetadataNative getCameraInfo()
  {
    Log.e("CameraDeviceUserShim", "getCameraInfo unimplemented.");
    return null;
  }
  
  public Surface getInputSurface()
  {
    Log.e("CameraDeviceUserShim", "Getting input surface is not supported on legacy devices");
    throw new ServiceSpecificException(10, "Getting input surface is not supported on legacy devices");
  }
  
  public void prepare(int paramInt)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot prepare stream, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot prepare stream, device has been closed.");
    }
    this.mCameraCallbacks.onPrepared(paramInt);
  }
  
  public void prepare2(int paramInt1, int paramInt2)
  {
    prepare(paramInt2);
  }
  
  public void setDeferredConfiguration(int paramInt, OutputConfiguration paramOutputConfiguration)
  {
    Log.e("CameraDeviceUserShim", "Set deferred configuration is not supported on legacy devices");
    throw new ServiceSpecificException(10, "Set deferred configuration is not supported on legacy devices");
  }
  
  public SubmitInfo submitRequest(CaptureRequest paramCaptureRequest, boolean paramBoolean)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot submit request, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot submit request, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot submit request, configuration change in progress.");
        throw new ServiceSpecificException(10, "Cannot submit request, configuration change in progress.");
      }
    }
    return this.mLegacyDevice.submitRequest(paramCaptureRequest, paramBoolean);
  }
  
  public SubmitInfo submitRequestList(CaptureRequest[] paramArrayOfCaptureRequest, boolean paramBoolean)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot submit request list, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot submit request list, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot submit request, configuration change in progress.");
        throw new ServiceSpecificException(10, "Cannot submit request, configuration change in progress.");
      }
    }
    return this.mLegacyDevice.submitRequestList(paramArrayOfCaptureRequest, paramBoolean);
  }
  
  public void tearDown(int paramInt)
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot tear down stream, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot tear down stream, device has been closed.");
    }
  }
  
  public void waitUntilIdle()
    throws RemoteException
  {
    if (this.mLegacyDevice.isClosed())
    {
      Log.e("CameraDeviceUserShim", "Cannot wait until idle, device has been closed.");
      throw new ServiceSpecificException(4, "Cannot wait until idle, device has been closed.");
    }
    synchronized (this.mConfigureLock)
    {
      if (this.mConfiguring)
      {
        Log.e("CameraDeviceUserShim", "Cannot wait until idle, configuration change in progress.");
        throw new ServiceSpecificException(10, "Cannot wait until idle, configuration change in progress.");
      }
    }
    this.mLegacyDevice.waitUntilIdle();
  }
  
  private static class CameraCallbackThread
    implements ICameraDeviceCallbacks
  {
    private static final int CAMERA_ERROR = 0;
    private static final int CAMERA_IDLE = 1;
    private static final int CAPTURE_STARTED = 2;
    private static final int PREPARED = 4;
    private static final int REPEATING_REQUEST_ERROR = 5;
    private static final int RESULT_RECEIVED = 3;
    private final ICameraDeviceCallbacks mCallbacks;
    private Handler mHandler;
    private final HandlerThread mHandlerThread;
    
    public CameraCallbackThread(ICameraDeviceCallbacks paramICameraDeviceCallbacks)
    {
      this.mCallbacks = paramICameraDeviceCallbacks;
      this.mHandlerThread = new HandlerThread("LegacyCameraCallback");
      this.mHandlerThread.start();
    }
    
    private Handler getHandler()
    {
      if (this.mHandler == null) {
        this.mHandler = new CallbackHandler(this.mHandlerThread.getLooper());
      }
      return this.mHandler;
    }
    
    public IBinder asBinder()
    {
      return null;
    }
    
    public void close()
    {
      this.mHandlerThread.quitSafely();
    }
    
    public void onCaptureStarted(CaptureResultExtras paramCaptureResultExtras, long paramLong)
    {
      paramCaptureResultExtras = getHandler().obtainMessage(2, (int)(paramLong & 0xFFFFFFFF), (int)(paramLong >> 32 & 0xFFFFFFFF), paramCaptureResultExtras);
      getHandler().sendMessage(paramCaptureResultExtras);
    }
    
    public void onDeviceError(int paramInt, CaptureResultExtras paramCaptureResultExtras)
    {
      paramCaptureResultExtras = getHandler().obtainMessage(0, paramInt, 0, paramCaptureResultExtras);
      getHandler().sendMessage(paramCaptureResultExtras);
    }
    
    public void onDeviceIdle()
    {
      Message localMessage = getHandler().obtainMessage(1);
      getHandler().sendMessage(localMessage);
    }
    
    public void onPrepared(int paramInt)
    {
      Message localMessage = getHandler().obtainMessage(4, paramInt, 0);
      getHandler().sendMessage(localMessage);
    }
    
    public void onRepeatingRequestError(long paramLong)
    {
      Message localMessage = getHandler().obtainMessage(5, (int)(paramLong & 0xFFFFFFFF), (int)(paramLong >> 32 & 0xFFFFFFFF));
      getHandler().sendMessage(localMessage);
    }
    
    public void onResultReceived(CameraMetadataNative paramCameraMetadataNative, CaptureResultExtras paramCaptureResultExtras)
    {
      paramCameraMetadataNative = getHandler().obtainMessage(3, new Object[] { paramCameraMetadataNative, paramCaptureResultExtras });
      getHandler().sendMessage(paramCameraMetadataNative);
    }
    
    private class CallbackHandler
      extends Handler
    {
      public CallbackHandler(Looper paramLooper)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        for (;;)
        {
          try
          {
            switch (paramMessage.what)
            {
            case 0: 
              throw new IllegalArgumentException("Unknown callback message " + paramMessage.what);
            }
          }
          catch (RemoteException localRemoteException)
          {
            throw new IllegalStateException("Received remote exception during camera callback " + paramMessage.what, localRemoteException);
          }
          int i = paramMessage.arg1;
          Object localObject1 = (CaptureResultExtras)paramMessage.obj;
          CameraDeviceUserShim.CameraCallbackThread.-get0(CameraDeviceUserShim.CameraCallbackThread.this).onDeviceError(i, (CaptureResultExtras)localObject1);
          return;
          CameraDeviceUserShim.CameraCallbackThread.-get0(CameraDeviceUserShim.CameraCallbackThread.this).onDeviceIdle();
          return;
          long l1 = paramMessage.arg2;
          long l2 = paramMessage.arg1;
          localObject1 = (CaptureResultExtras)paramMessage.obj;
          CameraDeviceUserShim.CameraCallbackThread.-get0(CameraDeviceUserShim.CameraCallbackThread.this).onCaptureStarted((CaptureResultExtras)localObject1, (l1 & 0xFFFFFFFF) << 32 | l2 & 0xFFFFFFFF);
          return;
          Object localObject2 = (Object[])paramMessage.obj;
          localObject1 = (CameraMetadataNative)localObject2[0];
          localObject2 = (CaptureResultExtras)localObject2[1];
          CameraDeviceUserShim.CameraCallbackThread.-get0(CameraDeviceUserShim.CameraCallbackThread.this).onResultReceived((CameraMetadataNative)localObject1, (CaptureResultExtras)localObject2);
          return;
          i = paramMessage.arg1;
          CameraDeviceUserShim.CameraCallbackThread.-get0(CameraDeviceUserShim.CameraCallbackThread.this).onPrepared(i);
          return;
          l1 = paramMessage.arg2;
          l2 = paramMessage.arg1;
          CameraDeviceUserShim.CameraCallbackThread.-get0(CameraDeviceUserShim.CameraCallbackThread.this).onRepeatingRequestError((l1 & 0xFFFFFFFF) << 32 | l2 & 0xFFFFFFFF);
          return;
        }
      }
    }
  }
  
  private static class CameraLooper
    implements Runnable, AutoCloseable
  {
    private final Camera mCamera = Camera.openUninitialized();
    private final int mCameraId;
    private volatile int mInitErrors;
    private Looper mLooper;
    private final ConditionVariable mStartDone = new ConditionVariable();
    private final Thread mThread;
    
    public CameraLooper(int paramInt)
    {
      this.mCameraId = paramInt;
      this.mThread = new Thread(this);
      this.mThread.start();
    }
    
    public void close()
    {
      if (this.mLooper == null) {
        return;
      }
      this.mLooper.quitSafely();
      try
      {
        this.mThread.join();
        this.mLooper = null;
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new AssertionError(localInterruptedException);
      }
    }
    
    public Camera getCamera()
    {
      return this.mCamera;
    }
    
    public void run()
    {
      Looper.prepare();
      this.mLooper = Looper.myLooper();
      this.mInitErrors = this.mCamera.cameraInitUnspecified(this.mCameraId);
      this.mStartDone.open();
      Looper.loop();
    }
    
    public int waitForOpen(int paramInt)
    {
      if (!this.mStartDone.block(paramInt))
      {
        Log.e("CameraDeviceUserShim", "waitForOpen - Camera failed to open after timeout of 5000 ms");
        try
        {
          this.mCamera.release();
          throw new ServiceSpecificException(10);
        }
        catch (RuntimeException localRuntimeException)
        {
          for (;;)
          {
            Log.e("CameraDeviceUserShim", "connectBinderShim - Failed to release camera after timeout ", localRuntimeException);
          }
        }
      }
      return this.mInitErrors;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/CameraDeviceUserShim.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */