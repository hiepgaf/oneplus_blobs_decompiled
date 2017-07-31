package android.hardware;

import android.app.ActivityThread;
import android.app.Application;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import android.media.IAudioService;
import android.media.IAudioService.Stub;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Element.DataKind;
import android.renderscript.Element.DataType;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.renderscript.Type.Builder;
import android.system.OsConstants;
import android.text.TextUtils.SimpleStringSplitter;
import android.text.TextUtils.StringSplitter;
import android.util.Log;
import android.util.OpFeatures;
import android.util.Permission;
import android.util.SeempLog;
import android.view.Surface;
import android.view.SurfaceHolder;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

@Deprecated
public class Camera
{
  @Deprecated
  public static final String ACTION_NEW_PICTURE = "android.hardware.action.NEW_PICTURE";
  @Deprecated
  public static final String ACTION_NEW_VIDEO = "android.hardware.action.NEW_VIDEO";
  public static final int CAMERA_ERROR_EVICTED = 2;
  public static final int CAMERA_ERROR_SERVER_DIED = 100;
  public static final int CAMERA_ERROR_UNKNOWN = 1;
  private static final int CAMERA_FACE_DETECTION_HW = 0;
  private static final int CAMERA_FACE_DETECTION_SW = 1;
  public static final int CAMERA_HAL_API_VERSION_1_0 = 256;
  private static final int CAMERA_HAL_API_VERSION_NORMAL_CONNECT = -2;
  private static final int CAMERA_HAL_API_VERSION_OP_SERVICE = -100;
  private static final int CAMERA_HAL_API_VERSION_UNSPECIFIED = -1;
  private static final int CAMERA_MSG_AEC = 65536;
  private static final int CAMERA_MSG_COMPRESSED_IMAGE = 256;
  private static final int CAMERA_MSG_DNG_IMAGE = 131072;
  private static final int CAMERA_MSG_DNG_META_DATA = 262144;
  private static final int CAMERA_MSG_ERROR = 1;
  private static final int CAMERA_MSG_FOCUS = 4;
  private static final int CAMERA_MSG_FOCUS_MOVE = 2048;
  private static final int CAMERA_MSG_IN_PROCESSING = 524288;
  private static final int CAMERA_MSG_META_DATA = 8192;
  private static final int CAMERA_MSG_POSTVIEW_FRAME = 64;
  private static final int CAMERA_MSG_PREVIEW_FRAME = 16;
  private static final int CAMERA_MSG_PREVIEW_METADATA = 1024;
  private static final int CAMERA_MSG_RAW_IMAGE = 128;
  private static final int CAMERA_MSG_RAW_IMAGE_NOTIFY = 512;
  private static final int CAMERA_MSG_SHUTTER = 2;
  private static final int CAMERA_MSG_STATE_CALLBACK = 1048576;
  private static final int CAMERA_MSG_STATS_DATA = 4096;
  private static final int CAMERA_MSG_VIDEO_FRAME = 32;
  private static final int CAMERA_MSG_ZOOM = 8;
  private static final int EACCESS = -13;
  private static final int EBUSY = -16;
  private static final int EINVAL = -22;
  private static final int ENODEV = -19;
  private static final int ENOSYS = -38;
  private static final int EOPNOTSUPP = -95;
  private static final int EUSERS = -87;
  private static final int NO_ERROR = 0;
  private static final String TAG = "Camera";
  private AECallback mAECallback;
  private AutoFocusCallback mAutoFocusCallback;
  private final Object mAutoFocusCallbackLock = new Object();
  private AutoFocusMoveCallback mAutoFocusMoveCallback;
  private CameraDataCallback mCameraDataCallback;
  private CameraMetaDataCallback mCameraMetaDataCallback;
  private CameraStateCallback mCameraStateCallback;
  private ErrorCallback mErrorCallback;
  private EventHandler mEventHandler;
  private boolean mFaceDetectionRunning = false;
  private FaceDetectionListener mFaceListener;
  private boolean mIsOPService = false;
  private PictureCallback mJpegCallback;
  private CameraMetadataNative mMetadata;
  private long mMetadataPtr;
  private long mNativeContext;
  private PictureCallback mOPServiceJpegCallback = null;
  private boolean mOneShot;
  private OneplusCallback mOneplusCallback;
  private PictureCallback mPostviewCallback;
  private PreviewCallback mPreviewCallback;
  private ProcessCallback mProcessCallback;
  private PictureCallback mRawImageCallback;
  private ShutterCallback mShutterCallback;
  private boolean mUsingPreviewAllocation;
  private boolean mWithBuffer;
  private OnZoomChangeListener mZoomListener;
  
  Camera() {}
  
  Camera(int paramInt)
  {
    if (paramInt >= getNumberOfCameras()) {
      throw new RuntimeException("Unknown camera ID");
    }
    paramInt = cameraInitNormal(paramInt);
    if (checkInitErrors(paramInt))
    {
      if (paramInt == -OsConstants.EACCES) {
        throw new RuntimeException("Fail to connect to camera service");
      }
      if (paramInt == 19) {
        throw new RuntimeException("Camera initialization failed");
      }
      throw new RuntimeException("Unknown camera error");
    }
  }
  
  private Camera(int paramInt1, int paramInt2)
  {
    paramInt1 = cameraInitVersion(paramInt1, paramInt2);
    if (checkInitErrors(paramInt1))
    {
      if (paramInt1 == -OsConstants.EACCES) {
        throw new RuntimeException("Fail to connect to camera service");
      }
      if (paramInt1 == 19) {
        throw new RuntimeException("Camera initialization failed");
      }
      if (paramInt1 == 38) {
        throw new RuntimeException("Camera initialization failed because some methods are not implemented");
      }
      if (paramInt1 == 95) {
        throw new RuntimeException("Camera initialization failed because the hal version is not supported by this device");
      }
      if (paramInt1 == 22) {
        throw new RuntimeException("Camera initialization failed because the input arugments are invalid");
      }
      if (paramInt1 == 16) {
        throw new RuntimeException("Camera initialization failed because the camera device was already opened");
      }
      if (paramInt1 == 87) {
        throw new RuntimeException("Camera initialization failed because the max number of camera devices were already opened");
      }
      throw new RuntimeException("Unknown camera error");
    }
  }
  
  private final native void _addCallbackBuffer(byte[] paramArrayOfByte, int paramInt);
  
  private final native boolean _enableShutterSound(boolean paramBoolean);
  
  private static native void _getCameraInfo(int paramInt, CameraInfo paramCameraInfo);
  
  public static native int _getNumberOfCameras();
  
  private final native void _startFaceDetection(int paramInt);
  
  private final native void _stopFaceDetection();
  
  private final native void _stopPreview();
  
  private final void addCallbackBuffer(byte[] paramArrayOfByte, int paramInt)
  {
    if ((paramInt != 16) && (paramInt != 128)) {
      throw new IllegalArgumentException("Unsupported message type: " + paramInt);
    }
    _addCallbackBuffer(paramArrayOfByte, paramInt);
  }
  
  private static int byteToInt(byte[] paramArrayOfByte, int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < 4)
    {
      j += ((paramArrayOfByte[(3 - i + paramInt)] & 0xFF) << (3 - i) * 8);
      i += 1;
    }
    return j;
  }
  
  private int cameraInitNormal(int paramInt)
  {
    return cameraInitVersion(paramInt, -2);
  }
  
  private int cameraInitVersion(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    this.mShutterCallback = null;
    this.mRawImageCallback = null;
    this.mJpegCallback = null;
    this.mPreviewCallback = null;
    this.mPostviewCallback = null;
    this.mUsingPreviewAllocation = false;
    this.mZoomListener = null;
    this.mCameraDataCallback = null;
    this.mCameraMetaDataCallback = null;
    this.mOneplusCallback = null;
    this.mProcessCallback = null;
    Object localObject1 = Looper.myLooper();
    if (localObject1 != null) {
      this.mEventHandler = new EventHandler(this, (Looper)localObject1);
    }
    for (;;)
    {
      localObject1 = ActivityThread.currentOpPackageName();
      Object localObject2 = SystemProperties.get("camera.hal1.packagelist", "");
      int i = paramInt2;
      if (((String)localObject2).length() > 0)
      {
        TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        localSimpleStringSplitter.setString((String)localObject2);
        localObject2 = localSimpleStringSplitter.iterator();
        do
        {
          i = paramInt2;
          if (!((Iterator)localObject2).hasNext()) {
            break;
          }
        } while (!((String)localObject1).equals((String)((Iterator)localObject2).next()));
        i = 256;
      }
      if (i == -100) {
        bool = true;
      }
      this.mIsOPService = bool;
      return native_setup(new WeakReference(this), paramInt1, i, (String)localObject1);
      localObject1 = Looper.getMainLooper();
      if (localObject1 != null) {
        this.mEventHandler = new EventHandler(this, (Looper)localObject1);
      } else {
        this.mEventHandler = null;
      }
    }
  }
  
  public static boolean checkInitErrors(int paramInt)
  {
    boolean bool = false;
    if (paramInt != 0) {
      bool = true;
    }
    return bool;
  }
  
  private native void enableFocusMoveCallback(int paramInt);
  
  public static void getCameraInfo(int paramInt, CameraInfo paramCameraInfo)
  {
    if (paramInt >= getNumberOfCameras()) {
      throw new RuntimeException("Unknown camera ID");
    }
    _getCameraInfo(paramInt, paramCameraInfo);
    IAudioService localIAudioService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    try
    {
      if (localIAudioService.isCameraSoundForced()) {
        paramCameraInfo.canDisableShutterSound = false;
      }
      return;
    }
    catch (RemoteException paramCameraInfo)
    {
      Log.e("Camera", "Audio service is unavailable for queries");
    }
  }
  
  public static Parameters getEmptyParameters()
  {
    Camera localCamera = new Camera();
    localCamera.getClass();
    return new Parameters(null);
  }
  
  public static int getNumberOfCameras()
  {
    int j = 0;
    String str = ActivityThread.currentOpPackageName();
    Object localObject = SystemProperties.get("camera.aux.packagelist");
    int i = j;
    if (((String)localObject).length() > 0)
    {
      TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
      localSimpleStringSplitter.setString((String)localObject);
      localObject = localSimpleStringSplitter.iterator();
      do
      {
        i = j;
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
      } while (!str.equals((String)((Iterator)localObject).next()));
      i = 1;
    }
    int k = _getNumberOfCameras();
    j = k;
    if (i == 0)
    {
      j = k;
      if (k > 2) {
        j = 2;
      }
    }
    return j;
  }
  
  public static Parameters getParametersCopy(Parameters paramParameters)
  {
    if (paramParameters == null) {
      throw new NullPointerException("parameters must not be null");
    }
    Object localObject = Parameters.-wrap0(paramParameters);
    localObject.getClass();
    localObject = new Parameters((Camera)localObject, null);
    ((Parameters)localObject).copyFrom(paramParameters);
    return (Parameters)localObject;
  }
  
  private final native void native_autoFocus();
  
  private final native void native_cancelAutoFocus();
  
  private final native String native_getParameters();
  
  private final native void native_release();
  
  private final native void native_sendHistogramData();
  
  private final native void native_sendMetaData();
  
  private final native void native_setHistogramMode(boolean paramBoolean);
  
  private final native void native_setLongshot(boolean paramBoolean);
  
  private final native void native_setMetadataCb(boolean paramBoolean);
  
  private final native void native_setParameters(String paramString);
  
  private final native int native_setup(Object paramObject, int paramInt1, int paramInt2, String paramString);
  
  private final native void native_takePicture(int paramInt);
  
  public static Camera open()
  {
    int j;
    CameraInfo localCameraInfo;
    int i;
    if ((!OpFeatures.isSupport(new int[] { 12 })) || (requestCameraPermission()))
    {
      j = getNumberOfCameras();
      localCameraInfo = new CameraInfo();
      i = 0;
    }
    while (i < j)
    {
      getCameraInfo(i, localCameraInfo);
      if (localCameraInfo.facing == 0)
      {
        return new Camera(i);
        return null;
      }
      i += 1;
    }
    return null;
  }
  
  public static Camera open(int paramInt)
  {
    if ((!OpFeatures.isSupport(new int[] { 12 })) || (requestCameraPermission())) {
      return new Camera(paramInt);
    }
    return null;
  }
  
  public static Camera openLegacy(int paramInt1, int paramInt2)
  {
    if (paramInt2 < 256) {
      throw new IllegalArgumentException("Invalid HAL version " + paramInt2);
    }
    return new Camera(paramInt1, paramInt2);
  }
  
  public static Camera openOPService()
  {
    return new Camera(-1, -100);
  }
  
  public static Camera openUninitialized()
  {
    return new Camera();
  }
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (Camera)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (((Camera)paramObject1).mEventHandler != null)
    {
      paramObject2 = ((Camera)paramObject1).mEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((Camera)paramObject1).mEventHandler.sendMessage((Message)paramObject2);
    }
  }
  
  private static boolean requestCameraPermission()
  {
    try
    {
      boolean bool = new Permission(ActivityThread.currentApplication().getApplicationContext()).requestPermissionAuto("android.permission.CAMERA");
      return bool;
    }
    catch (Exception localException)
    {
      Log.e("Camera", "request CAMERA permission fail");
      localException.printStackTrace();
    }
    return false;
  }
  
  private final native void setHasPreviewCallback(boolean paramBoolean1, boolean paramBoolean2);
  
  private final native void setPreviewCallbackSurface(Surface paramSurface);
  
  public final void addCallbackBuffer(byte[] paramArrayOfByte)
  {
    _addCallbackBuffer(paramArrayOfByte, 16);
  }
  
  public final void addDngImageCallbackBuffer(byte[] paramArrayOfByte)
  {
    _addCallbackBuffer(paramArrayOfByte, 131072);
  }
  
  public final void addRawImageCallbackBuffer(byte[] paramArrayOfByte)
  {
    addCallbackBuffer(paramArrayOfByte, 128);
  }
  
  public final void autoFocus(AutoFocusCallback paramAutoFocusCallback)
  {
    synchronized (this.mAutoFocusCallbackLock)
    {
      this.mAutoFocusCallback = paramAutoFocusCallback;
      native_autoFocus();
      return;
    }
  }
  
  public int cameraInitUnspecified(int paramInt)
  {
    return cameraInitVersion(paramInt, -1);
  }
  
  public final void cancelAutoFocus()
  {
    synchronized (this.mAutoFocusCallbackLock)
    {
      this.mAutoFocusCallback = null;
      native_cancelAutoFocus();
      this.mEventHandler.removeMessages(4);
      return;
    }
  }
  
  public final Allocation createPreviewAllocation(RenderScript paramRenderScript, int paramInt)
    throws RSIllegalArgumentException
  {
    Size localSize = getParameters().getPreviewSize();
    Type.Builder localBuilder = new Type.Builder(paramRenderScript, Element.createPixel(paramRenderScript, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
    localBuilder.setYuvFormat(842094169);
    localBuilder.setX(localSize.width);
    localBuilder.setY(localSize.height);
    return Allocation.createTyped(paramRenderScript, localBuilder.create(), paramInt | 0x20);
  }
  
  public final boolean disableShutterSound()
  {
    return _enableShutterSound(false);
  }
  
  public final boolean enableShutterSound(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      IAudioService localIAudioService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
      try
      {
        boolean bool = localIAudioService.isCameraSoundForced();
        if (bool) {
          return false;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("Camera", "Audio service is unavailable for queries");
      }
    }
    return _enableShutterSound(paramBoolean);
  }
  
  protected void finalize()
  {
    release();
  }
  
  public int getCurrentFocusPosition()
  {
    Parameters localParameters = new Parameters(null);
    localParameters.unflatten(native_getParameters());
    int i = -1;
    if (localParameters.getCurrentFocusPosition() != null) {
      i = Integer.parseInt(localParameters.getCurrentFocusPosition());
    }
    return i;
  }
  
  public Parameters getParameters()
  {
    Parameters localParameters = new Parameters(null);
    localParameters.unflatten(native_getParameters());
    return localParameters;
  }
  
  public int getWBCurrentCCT()
  {
    Parameters localParameters = new Parameters(null);
    localParameters.unflatten(native_getParameters());
    int i = 0;
    if (localParameters.getWBCurrentCCT() != null) {
      i = Integer.parseInt(localParameters.getWBCurrentCCT());
    }
    return i;
  }
  
  public final native void lock();
  
  public final native boolean previewEnabled();
  
  public final native void reconnect()
    throws IOException;
  
  public final void release()
  {
    native_release();
    this.mFaceDetectionRunning = false;
  }
  
  public final void sendHistogramData()
  {
    native_sendHistogramData();
  }
  
  public final void sendMetaData()
  {
    native_sendMetaData();
  }
  
  public void setAECallback(AECallback paramAECallback)
  {
    this.mAECallback = paramAECallback;
  }
  
  public void setAutoFocusMoveCallback(AutoFocusMoveCallback paramAutoFocusMoveCallback)
  {
    this.mAutoFocusMoveCallback = paramAutoFocusMoveCallback;
    if (this.mAutoFocusMoveCallback != null) {}
    for (int i = 1;; i = 0)
    {
      enableFocusMoveCallback(i);
      return;
    }
  }
  
  public final void setCameraStateCallback(CameraStateCallback paramCameraStateCallback)
  {
    this.mCameraStateCallback = paramCameraStateCallback;
  }
  
  public final native void setDisplayOrientation(int paramInt);
  
  public final void setErrorCallback(ErrorCallback paramErrorCallback)
  {
    this.mErrorCallback = paramErrorCallback;
  }
  
  public final void setFaceDetectionListener(FaceDetectionListener paramFaceDetectionListener)
  {
    this.mFaceListener = paramFaceDetectionListener;
  }
  
  public final void setHistogramMode(CameraDataCallback paramCameraDataCallback)
  {
    this.mCameraDataCallback = paramCameraDataCallback;
    if (paramCameraDataCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      native_setHistogramMode(bool);
      return;
    }
  }
  
  public final void setLongshot(boolean paramBoolean)
  {
    native_setLongshot(paramBoolean);
  }
  
  public final void setMetadataCb(CameraMetaDataCallback paramCameraMetaDataCallback)
  {
    this.mCameraMetaDataCallback = paramCameraMetaDataCallback;
    if (paramCameraMetaDataCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      native_setMetadataCb(bool);
      return;
    }
  }
  
  public void setOPJpegCallback(PictureCallback paramPictureCallback)
  {
    this.mOPServiceJpegCallback = paramPictureCallback;
  }
  
  public final void setOneShotPreviewCallback(PreviewCallback paramPreviewCallback)
  {
    boolean bool = true;
    SeempLog.record(68);
    this.mPreviewCallback = paramPreviewCallback;
    this.mOneShot = true;
    this.mWithBuffer = false;
    if (paramPreviewCallback != null) {
      this.mUsingPreviewAllocation = false;
    }
    if (paramPreviewCallback != null) {}
    for (;;)
    {
      setHasPreviewCallback(bool, false);
      return;
      bool = false;
    }
  }
  
  public final void setOneplusCallback(OneplusCallback paramOneplusCallback)
  {
    this.mOneplusCallback = paramOneplusCallback;
  }
  
  public void setParameters(Parameters paramParameters)
  {
    if (this.mUsingPreviewAllocation)
    {
      Size localSize1 = paramParameters.getPreviewSize();
      Size localSize2 = getParameters().getPreviewSize();
      if ((localSize1.width != localSize2.width) || (localSize1.height != localSize2.height)) {
        throw new IllegalStateException("Cannot change preview size while a preview allocation is configured.");
      }
    }
    native_setParameters(paramParameters.flatten());
  }
  
  public final void setPreviewCallback(PreviewCallback paramPreviewCallback)
  {
    SeempLog.record(66);
    this.mPreviewCallback = paramPreviewCallback;
    this.mOneShot = false;
    this.mWithBuffer = false;
    if (paramPreviewCallback != null) {
      this.mUsingPreviewAllocation = false;
    }
    if (paramPreviewCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      setHasPreviewCallback(bool, false);
      return;
    }
  }
  
  public final void setPreviewCallbackAllocation(Allocation paramAllocation)
    throws IOException
  {
    Size localSize = null;
    if (paramAllocation != null)
    {
      localSize = getParameters().getPreviewSize();
      if ((localSize.width != paramAllocation.getType().getX()) || (localSize.height != paramAllocation.getType().getY())) {
        throw new IllegalArgumentException("Allocation dimensions don't match preview dimensions: Allocation is " + paramAllocation.getType().getX() + ", " + paramAllocation.getType().getY() + ". Preview is " + localSize.width + ", " + localSize.height);
      }
      if ((paramAllocation.getUsage() & 0x20) == 0) {
        throw new IllegalArgumentException("Allocation usage does not include USAGE_IO_INPUT");
      }
      if (paramAllocation.getType().getElement().getDataKind() != Element.DataKind.PIXEL_YUV) {
        throw new IllegalArgumentException("Allocation is not of a YUV type");
      }
      paramAllocation = paramAllocation.getSurface();
      this.mUsingPreviewAllocation = true;
    }
    for (;;)
    {
      setPreviewCallbackSurface(paramAllocation);
      return;
      this.mUsingPreviewAllocation = false;
      paramAllocation = localSize;
    }
  }
  
  public final void setPreviewCallbackWithBuffer(PreviewCallback paramPreviewCallback)
  {
    boolean bool = false;
    SeempLog.record(67);
    this.mPreviewCallback = paramPreviewCallback;
    this.mOneShot = false;
    this.mWithBuffer = true;
    if (paramPreviewCallback != null) {
      this.mUsingPreviewAllocation = false;
    }
    if (paramPreviewCallback != null) {
      bool = true;
    }
    setHasPreviewCallback(bool, true);
  }
  
  public final void setPreviewDisplay(SurfaceHolder paramSurfaceHolder)
    throws IOException
  {
    if (paramSurfaceHolder != null)
    {
      setPreviewSurface(paramSurfaceHolder.getSurface());
      return;
    }
    setPreviewSurface((Surface)null);
  }
  
  public final native void setPreviewSurface(Surface paramSurface)
    throws IOException;
  
  public final native void setPreviewTexture(SurfaceTexture paramSurfaceTexture)
    throws IOException;
  
  public final void setProcessCallback(ProcessCallback paramProcessCallback)
  {
    this.mProcessCallback = paramProcessCallback;
  }
  
  public final void setZoomChangeListener(OnZoomChangeListener paramOnZoomChangeListener)
  {
    this.mZoomListener = paramOnZoomChangeListener;
  }
  
  public final void startFaceDetection()
  {
    if (this.mFaceDetectionRunning) {
      throw new RuntimeException("Face detection is already running");
    }
    _startFaceDetection(0);
    this.mFaceDetectionRunning = true;
  }
  
  public final native void startPreview();
  
  public final native void startSmoothZoom(int paramInt);
  
  public final void stopFaceDetection()
  {
    _stopFaceDetection();
    this.mFaceDetectionRunning = false;
  }
  
  public final void stopPreview()
  {
    _stopPreview();
    this.mFaceDetectionRunning = false;
    this.mShutterCallback = null;
    this.mProcessCallback = null;
    this.mRawImageCallback = null;
    this.mPostviewCallback = null;
    this.mJpegCallback = null;
    synchronized (this.mAutoFocusCallbackLock)
    {
      this.mAutoFocusCallback = null;
      this.mAutoFocusMoveCallback = null;
      return;
    }
  }
  
  public final native void stopSmoothZoom();
  
  public final void takePicture(ShutterCallback paramShutterCallback, PictureCallback paramPictureCallback1, PictureCallback paramPictureCallback2)
  {
    SeempLog.record(65);
    if ((!OpFeatures.isSupport(new int[] { 12 })) || (requestCameraPermission()))
    {
      takePicture(paramShutterCallback, paramPictureCallback1, null, paramPictureCallback2);
      return;
    }
  }
  
  public final void takePicture(ShutterCallback paramShutterCallback, PictureCallback paramPictureCallback1, PictureCallback paramPictureCallback2, PictureCallback paramPictureCallback3)
  {
    SeempLog.record(65);
    this.mShutterCallback = paramShutterCallback;
    this.mRawImageCallback = paramPictureCallback1;
    this.mPostviewCallback = paramPictureCallback2;
    this.mJpegCallback = paramPictureCallback3;
    int j = 0;
    if (this.mShutterCallback != null) {
      j = 2;
    }
    int i = j;
    if (this.mRawImageCallback != null) {
      i = j | 0x80;
    }
    j = i;
    if (this.mPostviewCallback != null) {
      j = i | 0x40;
    }
    i = j;
    if (this.mJpegCallback != null) {
      i = j | 0x100;
    }
    j = i;
    if (this.mOneplusCallback != null)
    {
      this.mMetadata = new CameraMetadataNative();
      if (this.mMetadata == null) {
        break label185;
      }
    }
    label185:
    for (this.mMetadataPtr = this.mMetadata.getNativeCameraMetadata();; this.mMetadataPtr = 0L)
    {
      j = i | 0x40000 | 0x20000;
      Log.d("Camera", "enable dng capture");
      i = j;
      if (this.mProcessCallback != null) {
        i = j | 0x80000;
      }
      native_takePicture(i);
      this.mFaceDetectionRunning = false;
      return;
    }
  }
  
  public final native void unlock();
  
  @Deprecated
  public static abstract interface AECallback
  {
    public abstract void onAEChanged(int[] paramArrayOfInt, Camera paramCamera);
  }
  
  @Deprecated
  public static class Area
  {
    public Rect rect;
    public int weight;
    
    public Area(Rect paramRect, int paramInt)
    {
      this.rect = paramRect;
      this.weight = paramInt;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = false;
      if (!(paramObject instanceof Area)) {
        return false;
      }
      paramObject = (Area)paramObject;
      if (this.rect == null)
      {
        if (((Area)paramObject).rect != null) {
          return false;
        }
      }
      else if (!this.rect.equals(((Area)paramObject).rect)) {
        return false;
      }
      if (this.weight == ((Area)paramObject).weight) {
        bool = true;
      }
      return bool;
    }
  }
  
  @Deprecated
  public static abstract interface AutoFocusCallback
  {
    public abstract void onAutoFocus(boolean paramBoolean, Camera paramCamera);
  }
  
  @Deprecated
  public static abstract interface AutoFocusMoveCallback
  {
    public abstract void onAutoFocusMoving(boolean paramBoolean, Camera paramCamera);
  }
  
  public static abstract interface CameraDataCallback
  {
    public abstract void onCameraData(int[] paramArrayOfInt, Camera paramCamera);
  }
  
  @Deprecated
  public static class CameraInfo
  {
    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_SUPPORT_MODE_NONZSL = 3;
    public static final int CAMERA_SUPPORT_MODE_ZSL = 2;
    public boolean canDisableShutterSound;
    public int facing;
    public int orientation;
  }
  
  public static abstract interface CameraMetaDataCallback
  {
    public abstract void onCameraMetaData(byte[] paramArrayOfByte, Camera paramCamera);
  }
  
  public static abstract interface CameraStateCallback
  {
    public abstract void onCameraStateChanged(byte[] paramArrayOfByte, Camera paramCamera);
  }
  
  public class Coordinate
  {
    public int xCoordinate;
    public int yCoordinate;
    
    public Coordinate(int paramInt1, int paramInt2)
    {
      this.xCoordinate = paramInt1;
      this.yCoordinate = paramInt2;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof Coordinate)) {
        return false;
      }
      boolean bool1 = bool2;
      if (this.xCoordinate == ((Coordinate)paramObject).xCoordinate)
      {
        bool1 = bool2;
        if (this.yCoordinate == ((Coordinate)paramObject).yCoordinate) {
          bool1 = true;
        }
      }
      return bool1;
    }
  }
  
  @Deprecated
  public static abstract interface ErrorCallback
  {
    public abstract void onError(int paramInt, Camera paramCamera);
  }
  
  private class EventHandler
    extends Handler
  {
    private final Camera mCamera;
    
    public EventHandler(Camera paramCamera, Looper paramLooper)
    {
      super();
      this.mCamera = paramCamera;
    }
    
    public void handleMessage(Message paramMessage)
    {
      label398:
      boolean bool;
      int i;
      switch (paramMessage.what)
      {
      default: 
        Log.e("Camera", "Unknown message type " + paramMessage.what);
        return;
      case 2: 
        if (Camera.-get19(Camera.this) != null) {
          Camera.-get19(Camera.this).onShutter();
        }
        return;
      case 524288: 
        if (Camera.-get17(Camera.this) != null) {
          Camera.-get17(Camera.this).onProcess();
        }
        return;
      case 128: 
        if (Camera.-get18(Camera.this) != null) {
          Camera.-get18(Camera.this).onPictureTaken((byte[])paramMessage.obj, this.mCamera);
        }
        return;
      case 256: 
        if (Camera.-get10(Camera.this) != null) {
          Camera.-get10(Camera.this).onPictureTaken((byte[])paramMessage.obj, this.mCamera);
        }
        if (Camera.-get9(Camera.this))
        {
          Log.d("Camera", "op jpeg callback");
          if (Camera.-get12(Camera.this) != null) {
            Camera.-get12(Camera.this).onPictureTaken((byte[])paramMessage.obj, this.mCamera);
          }
        }
        return;
      case 16: 
        ??? = Camera.-get16(Camera.this);
        if (??? != null)
        {
          if (!Camera.-get13(Camera.this)) {
            break label398;
          }
          Camera.-set0(Camera.this, null);
        }
        for (;;)
        {
          ((Camera.PreviewCallback)???).onPreviewFrame((byte[])paramMessage.obj, this.mCamera);
          return;
          if (!Camera.-get20(Camera.this)) {
            Camera.-wrap1(Camera.this, true, false);
          }
        }
      case 64: 
        if (Camera.-get15(Camera.this) != null) {
          Camera.-get15(Camera.this).onPictureTaken((byte[])paramMessage.obj, this.mCamera);
        }
        return;
      case 4: 
        for (;;)
        {
          synchronized (Camera.-get2(Camera.this))
          {
            Camera.AutoFocusCallback localAutoFocusCallback = Camera.-get1(Camera.this);
            if (localAutoFocusCallback != null)
            {
              if (paramMessage.arg1 == 0)
              {
                bool = false;
                localAutoFocusCallback.onAutoFocus(bool, this.mCamera);
              }
            }
            else {
              return;
            }
          }
          bool = true;
        }
      case 8: 
        if (Camera.-get21(Camera.this) != null)
        {
          ??? = Camera.-get21(Camera.this);
          i = paramMessage.arg1;
          if (paramMessage.arg2 == 0) {
            break label568;
          }
        }
        for (bool = true;; bool = false)
        {
          ((Camera.OnZoomChangeListener)???).onZoomChange(i, bool, this.mCamera);
          return;
        }
      case 1024: 
        if (Camera.-get8(Camera.this) != null) {
          Camera.-get8(Camera.this).onFaceDetection((Camera.Face[])paramMessage.obj, this.mCamera);
        }
        return;
      case 1: 
        Log.e("Camera", "Error " + paramMessage.arg1);
        if (Camera.-get7(Camera.this) != null) {
          Camera.-get7(Camera.this).onError(paramMessage.arg1, this.mCamera);
        }
        return;
      case 2048: 
        if (Camera.-get3(Camera.this) != null)
        {
          ??? = Camera.-get3(Camera.this);
          if (paramMessage.arg1 != 0) {
            break label710;
          }
        }
        for (bool = false;; bool = true)
        {
          ((Camera.AutoFocusMoveCallback)???).onAutoFocusMoving(bool, this.mCamera);
          return;
        }
      case 4096: 
        ??? = new int['ā'];
        i = 0;
        while (i < 257)
        {
          ???[i] = Camera.-wrap0((byte[])paramMessage.obj, i * 4);
          i += 1;
        }
        if (Camera.-get4(Camera.this) != null) {
          Camera.-get4(Camera.this).onCameraData((int[])???, this.mCamera);
        }
        return;
      case 8192: 
        if (Camera.-get5(Camera.this) != null) {
          Camera.-get5(Camera.this).onCameraMetaData((byte[])paramMessage.obj, this.mCamera);
        }
        return;
      case 65536: 
        if (Camera.-get0(Camera.this) != null)
        {
          i = paramMessage.arg1;
          int j = paramMessage.arg2;
          paramMessage = Camera.-get0(Camera.this);
          ??? = this.mCamera;
          paramMessage.onAEChanged(new int[] { i, j }, (Camera)???);
        }
        return;
      case 131072: 
        Log.d("Camera", "CAMERA_MSG_DNG_IMAGE");
        if (Camera.-get14(Camera.this) != null)
        {
          Camera.-get14(Camera.this).onDngImage((byte[])paramMessage.obj, this.mCamera);
          return;
        }
        Log.d("Camera", "mOneplusCallback is null");
        return;
      case 262144: 
        label568:
        label710:
        Log.d("Camera", "CAMERA_MSG_DNG_META_DATA");
        if (Camera.-get14(Camera.this) != null)
        {
          paramMessage = new CameraCharacteristics(new CameraMetadataNative(Camera.-get11(Camera.this)));
          ??? = new CaptureResult(new CameraMetadataNative(Camera.-get11(Camera.this)), new CaptureRequest.Builder(new CameraMetadataNative(Camera.-get11(Camera.this)), false, 0).build(), new CaptureResultExtras(-1, -1, -1, -1, -1L, -1, -1));
          Camera.-get14(Camera.this).onDngMetadata(paramMessage, (CaptureResult)???, this.mCamera);
          return;
        }
        Log.d("Camera", "mOneplusCallback is null");
        return;
      }
      Log.d("Camera", "CAMERA_MSG_STATE_CALLBACK");
      if (Camera.-get6(Camera.this) != null)
      {
        Camera.-get6(Camera.this).onCameraStateChanged((byte[])paramMessage.obj, this.mCamera);
        return;
      }
      Log.d("Camera", "mCameraStateCallback is null");
    }
  }
  
  @Deprecated
  public static class Face
  {
    public int blinkDetected = 0;
    public int faceRecognised = 0;
    public int id = -1;
    public int isSmile = 0;
    public Point leftEye = null;
    public Point mouth = null;
    public Rect rect;
    public Point rightEye = null;
    public int score;
    public int smileDegree = 0;
    public int smileScore = 0;
    
    public int getIsSmile()
    {
      return this.isSmile;
    }
  }
  
  @Deprecated
  public static abstract interface FaceDetectionListener
  {
    public abstract void onFaceDetection(Camera.Face[] paramArrayOfFace, Camera paramCamera);
  }
  
  @Deprecated
  public static abstract interface OnZoomChangeListener
  {
    public abstract void onZoomChange(int paramInt, boolean paramBoolean, Camera paramCamera);
  }
  
  public static abstract interface OneplusCallback
  {
    public abstract void onDngImage(byte[] paramArrayOfByte, Camera paramCamera);
    
    public abstract void onDngMetadata(CameraCharacteristics paramCameraCharacteristics, CaptureResult paramCaptureResult, Camera paramCamera);
  }
  
  @Deprecated
  public class Parameters
  {
    public static final String AE_BRACKET = "AE-Bracket";
    public static final String AE_BRACKET_HDR = "HDR";
    public static final String AE_BRACKET_HDR_OFF = "Off";
    public static final String ANTIBANDING_50HZ = "50hz";
    public static final String ANTIBANDING_60HZ = "60hz";
    public static final String ANTIBANDING_AUTO = "auto";
    public static final String ANTIBANDING_OFF = "off";
    public static final String AUTO_EXPOSURE_CENTER_WEIGHTED = "center-weighted";
    public static final String AUTO_EXPOSURE_FRAME_AVG = "frame-average";
    public static final String AUTO_EXPOSURE_SPOT_METERING = "spot-metering";
    public static final String CONTINUOUS_AF_OFF = "caf-off";
    public static final String CONTINUOUS_AF_ON = "caf-on";
    public static final String DENOISE_OFF = "denoise-off";
    public static final String DENOISE_ON = "denoise-on";
    public static final String EFFECT_AQUA = "aqua";
    public static final String EFFECT_BLACKBOARD = "blackboard";
    public static final String EFFECT_MONO = "mono";
    public static final String EFFECT_NEGATIVE = "negative";
    public static final String EFFECT_NONE = "none";
    public static final String EFFECT_POSTERIZE = "posterize";
    public static final String EFFECT_SEPIA = "sepia";
    public static final String EFFECT_SOLARIZE = "solarize";
    public static final String EFFECT_WHITEBOARD = "whiteboard";
    public static final String FACE_DETECTION_OFF = "off";
    public static final String FACE_DETECTION_ON = "on";
    private static final String FALSE = "false";
    public static final String FLASH_MODE_AUTO = "auto";
    public static final String FLASH_MODE_OFF = "off";
    public static final String FLASH_MODE_ON = "on";
    public static final String FLASH_MODE_RED_EYE = "red-eye";
    public static final String FLASH_MODE_TORCH = "torch";
    public static final int FOCUS_DISTANCE_FAR_INDEX = 2;
    public static final int FOCUS_DISTANCE_NEAR_INDEX = 0;
    public static final int FOCUS_DISTANCE_OPTIMAL_INDEX = 1;
    public static final String FOCUS_MODE_AUTO = "auto";
    public static final String FOCUS_MODE_CONTINUOUS_PICTURE = "continuous-picture";
    public static final String FOCUS_MODE_CONTINUOUS_VIDEO = "continuous-video";
    public static final String FOCUS_MODE_EDOF = "edof";
    public static final String FOCUS_MODE_FIXED = "fixed";
    public static final String FOCUS_MODE_INFINITY = "infinity";
    public static final String FOCUS_MODE_MACRO = "macro";
    public static final String FOCUS_MODE_MANUAL_POSITION = "manual";
    public static final String FOCUS_MODE_NORMAL = "normal";
    public static final String HISTOGRAM_DISABLE = "disable";
    public static final String HISTOGRAM_ENABLE = "enable";
    public static final String ISO_100 = "ISO100";
    public static final String ISO_1600 = "ISO1600";
    public static final String ISO_200 = "ISO200";
    public static final String ISO_3200 = "ISO3200";
    public static final String ISO_400 = "ISO400";
    public static final String ISO_800 = "ISO800";
    public static final String ISO_AUTO = "auto";
    public static final String ISO_HJR = "ISO_HJR";
    private static final String KEY_ANTIBANDING = "antibanding";
    private static final String KEY_AUTO_EXPOSURE_LOCK = "auto-exposure-lock";
    private static final String KEY_AUTO_EXPOSURE_LOCK_SUPPORTED = "auto-exposure-lock-supported";
    private static final String KEY_AUTO_WHITEBALANCE_LOCK = "auto-whitebalance-lock";
    private static final String KEY_AUTO_WHITEBALANCE_LOCK_SUPPORTED = "auto-whitebalance-lock-supported";
    private static final String KEY_EFFECT = "effect";
    private static final String KEY_EXPOSURE_COMPENSATION = "exposure-compensation";
    private static final String KEY_EXPOSURE_COMPENSATION_STEP = "exposure-compensation-step";
    private static final String KEY_FLASH_MODE = "flash-mode";
    private static final String KEY_FOCAL_LENGTH = "focal-length";
    private static final String KEY_FOCUS_AREAS = "focus-areas";
    private static final String KEY_FOCUS_DISTANCES = "focus-distances";
    private static final String KEY_FOCUS_MODE = "focus-mode";
    private static final String KEY_GPS_ALTITUDE = "gps-altitude";
    private static final String KEY_GPS_LATITUDE = "gps-latitude";
    private static final String KEY_GPS_LONGITUDE = "gps-longitude";
    private static final String KEY_GPS_PROCESSING_METHOD = "gps-processing-method";
    private static final String KEY_GPS_TIMESTAMP = "gps-timestamp";
    private static final String KEY_HORIZONTAL_VIEW_ANGLE = "horizontal-view-angle";
    private static final String KEY_JPEG_QUALITY = "jpeg-quality";
    private static final String KEY_JPEG_THUMBNAIL_HEIGHT = "jpeg-thumbnail-height";
    private static final String KEY_JPEG_THUMBNAIL_QUALITY = "jpeg-thumbnail-quality";
    private static final String KEY_JPEG_THUMBNAIL_SIZE = "jpeg-thumbnail-size";
    private static final String KEY_JPEG_THUMBNAIL_WIDTH = "jpeg-thumbnail-width";
    private static final String KEY_MAX_EXPOSURE_COMPENSATION = "max-exposure-compensation";
    private static final String KEY_MAX_NUM_DETECTED_FACES_HW = "max-num-detected-faces-hw";
    private static final String KEY_MAX_NUM_DETECTED_FACES_SW = "max-num-detected-faces-sw";
    private static final String KEY_MAX_NUM_FOCUS_AREAS = "max-num-focus-areas";
    private static final String KEY_MAX_NUM_METERING_AREAS = "max-num-metering-areas";
    private static final String KEY_MAX_ZOOM = "max-zoom";
    private static final String KEY_METERING_AREAS = "metering-areas";
    private static final String KEY_MIN_EXPOSURE_COMPENSATION = "min-exposure-compensation";
    private static final String KEY_PICTURE_FORMAT = "picture-format";
    private static final String KEY_PICTURE_SIZE = "picture-size";
    private static final String KEY_PREFERRED_PREVIEW_SIZE_FOR_VIDEO = "preferred-preview-size-for-video";
    private static final String KEY_PREVIEW_FORMAT = "preview-format";
    private static final String KEY_PREVIEW_FPS_RANGE = "preview-fps-range";
    private static final String KEY_PREVIEW_FRAME_RATE = "preview-frame-rate";
    private static final String KEY_PREVIEW_SIZE = "preview-size";
    public static final String KEY_QC_AE_BRACKET_HDR = "ae-bracket-hdr";
    private static final String KEY_QC_AUTO_EXPOSURE = "auto-exposure";
    private static final String KEY_QC_AUTO_HDR_ENABLE = "auto-hdr-enable";
    private static final String KEY_QC_CAMERA_MODE = "camera-mode";
    private static final String KEY_QC_CONTINUOUS_AF = "continuous-af";
    private static final String KEY_QC_CONTRAST = "contrast";
    private static final String KEY_QC_DENOISE = "denoise";
    private static final String KEY_QC_EXIF_DATETIME = "exif-datetime";
    private static final String KEY_QC_EXPOSURE_TIME = "exposure-time";
    private static final String KEY_QC_FACE_DETECTION = "face-detection";
    private static final String KEY_QC_GPS_ALTITUDE_REF = "gps-altitude-ref";
    private static final String KEY_QC_GPS_LATITUDE_REF = "gps-latitude-ref";
    private static final String KEY_QC_GPS_LONGITUDE_REF = "gps-longitude-ref";
    private static final String KEY_QC_GPS_STATUS = "gps-status";
    private static final String KEY_QC_HFR_SIZE = "hfr-size";
    private static final String KEY_QC_HISTOGRAM = "histogram";
    private static final String KEY_QC_ISO_MODE = "iso";
    private static final String KEY_QC_LENSSHADE = "lensshade";
    private static final String KEY_QC_MANUAL_FOCUS_POSITION = "manual-focus-position";
    private static final String KEY_QC_MANUAL_FOCUS_POS_TYPE = "manual-focus-pos-type";
    private static final String KEY_QC_MAX_CONTRAST = "max-contrast";
    private static final String KEY_QC_MAX_EXPOSURE_TIME = "max-exposure-time";
    private static final String KEY_QC_MAX_SATURATION = "max-saturation";
    private static final String KEY_QC_MAX_SHARPNESS = "max-sharpness";
    private static final String KEY_QC_MAX_WB_CCT = "max-wb-cct";
    private static final String KEY_QC_MEMORY_COLOR_ENHANCEMENT = "mce";
    private static final String KEY_QC_MIN_EXPOSURE_TIME = "min-exposure-time";
    private static final String KEY_QC_MIN_WB_CCT = "min-wb-cct";
    private static final String KEY_QC_POWER_MODE = "power-mode";
    private static final String KEY_QC_POWER_MODE_SUPPORTED = "power-mode-supported";
    private static final String KEY_QC_PREVIEW_FRAME_RATE_AUTO_MODE = "frame-rate-auto";
    private static final String KEY_QC_PREVIEW_FRAME_RATE_FIXED_MODE = "frame-rate-fixed";
    private static final String KEY_QC_PREVIEW_FRAME_RATE_MODE = "preview-frame-rate-mode";
    private static final String KEY_QC_REDEYE_REDUCTION = "redeye-reduction";
    private static final String KEY_QC_SATURATION = "saturation";
    private static final String KEY_QC_SCENE_DETECT = "scene-detect";
    private static final String KEY_QC_SELECTABLE_ZONE_AF = "selectable-zone-af";
    private static final String KEY_QC_SHARPNESS = "sharpness";
    private static final String KEY_QC_SKIN_TONE_ENHANCEMENT = "skinToneEnhancement";
    private static final String KEY_QC_SMILE_DETECTION = "smile-detection";
    private static final String KEY_QC_TOUCH_AF_AEC = "touch-af-aec";
    private static final String KEY_QC_TOUCH_INDEX_AEC = "touch-index-aec";
    private static final String KEY_QC_TOUCH_INDEX_AF = "touch-index-af";
    private static final String KEY_QC_VIDEO_HDR = "video-hdr";
    private static final String KEY_QC_VIDEO_HIGH_FRAME_RATE = "video-hfr";
    private static final String KEY_QC_VIDEO_ROTATION = "video-rotation";
    private static final String KEY_QC_WB_MANUAL_CCT = "wb-manual-cct";
    private static final String KEY_QC_ZSL = "zsl";
    private static final String KEY_RECORDING_HINT = "recording-hint";
    private static final String KEY_ROTATION = "rotation";
    private static final String KEY_SCENE_MODE = "scene-mode";
    private static final String KEY_SMOOTH_ZOOM_SUPPORTED = "smooth-zoom-supported";
    private static final String KEY_VERTICAL_VIEW_ANGLE = "vertical-view-angle";
    private static final String KEY_VIDEO_SIZE = "video-size";
    private static final String KEY_VIDEO_SNAPSHOT_SUPPORTED = "video-snapshot-supported";
    private static final String KEY_VIDEO_STABILIZATION = "video-stabilization";
    private static final String KEY_VIDEO_STABILIZATION_SUPPORTED = "video-stabilization-supported";
    private static final String KEY_WHITE_BALANCE = "whitebalance";
    private static final String KEY_ZOOM = "zoom";
    private static final String KEY_ZOOM_RATIOS = "zoom-ratios";
    private static final String KEY_ZOOM_SUPPORTED = "zoom-supported";
    public static final String LENSSHADE_DISABLE = "disable";
    public static final String LENSSHADE_ENABLE = "enable";
    public static final String LOW_POWER = "Low_Power";
    private static final int MANUAL_FOCUS_POS_TYPE_DAC = 1;
    private static final int MANUAL_FOCUS_POS_TYPE_INDEX = 0;
    public static final String MCE_DISABLE = "disable";
    public static final String MCE_ENABLE = "enable";
    public static final String NORMAL_POWER = "Normal_Power";
    private static final String PIXEL_FORMAT_BAYER_RGGB = "bayer-rggb";
    private static final String PIXEL_FORMAT_JPEG = "jpeg";
    private static final String PIXEL_FORMAT_NV12 = "nv12";
    private static final String PIXEL_FORMAT_RAW = "raw";
    private static final String PIXEL_FORMAT_RGB565 = "rgb565";
    private static final String PIXEL_FORMAT_YUV420P = "yuv420p";
    private static final String PIXEL_FORMAT_YUV420SP = "yuv420sp";
    private static final String PIXEL_FORMAT_YUV420SP_ADRENO = "yuv420sp-adreno";
    private static final String PIXEL_FORMAT_YUV422I = "yuv422i-yuyv";
    private static final String PIXEL_FORMAT_YUV422SP = "yuv422sp";
    private static final String PIXEL_FORMAT_YV12 = "yv12";
    public static final int PREVIEW_FPS_MAX_INDEX = 1;
    public static final int PREVIEW_FPS_MIN_INDEX = 0;
    public static final String REDEYE_REDUCTION_DISABLE = "disable";
    public static final String REDEYE_REDUCTION_ENABLE = "enable";
    public static final String SCENE_DETECT_OFF = "off";
    public static final String SCENE_DETECT_ON = "on";
    public static final String SCENE_MODE_ACTION = "action";
    public static final String SCENE_MODE_ASD = "asd";
    public static final String SCENE_MODE_AUTO = "auto";
    public static final String SCENE_MODE_BACKLIGHT = "backlight";
    public static final String SCENE_MODE_BARCODE = "barcode";
    public static final String SCENE_MODE_BEACH = "beach";
    public static final String SCENE_MODE_CANDLELIGHT = "candlelight";
    public static final String SCENE_MODE_FIREWORKS = "fireworks";
    public static final String SCENE_MODE_FLOWERS = "flowers";
    public static final String SCENE_MODE_HDR = "hdr";
    public static final String SCENE_MODE_LANDSCAPE = "landscape";
    public static final String SCENE_MODE_NIGHT = "night";
    public static final String SCENE_MODE_NIGHT_PORTRAIT = "night-portrait";
    public static final String SCENE_MODE_PARTY = "party";
    public static final String SCENE_MODE_PORTRAIT = "portrait";
    public static final String SCENE_MODE_SNOW = "snow";
    public static final String SCENE_MODE_SPORTS = "sports";
    public static final String SCENE_MODE_STEADYPHOTO = "steadyphoto";
    public static final String SCENE_MODE_SUNSET = "sunset";
    public static final String SCENE_MODE_THEATRE = "theatre";
    public static final String SELECTABLE_ZONE_AF_AUTO = "auto";
    public static final String SELECTABLE_ZONE_AF_CENTER_WEIGHTED = "center-weighted";
    public static final String SELECTABLE_ZONE_AF_FRAME_AVERAGE = "frame-average";
    public static final String SELECTABLE_ZONE_AF_SPOTMETERING = "spot-metering";
    public static final String SKIN_TONE_ENHANCEMENT_DISABLE = "disable";
    public static final String SKIN_TONE_ENHANCEMENT_ENABLE = "enable";
    private static final String SUPPORTED_VALUES_SUFFIX = "-values";
    public static final String TOUCH_AF_AEC_OFF = "touch-off";
    public static final String TOUCH_AF_AEC_ON = "touch-on";
    private static final String TRUE = "true";
    public static final String VIDEO_HFR_2X = "60";
    public static final String VIDEO_HFR_3X = "90";
    public static final String VIDEO_HFR_4X = "120";
    public static final String VIDEO_HFR_OFF = "off";
    public static final String VIDEO_ROTATION_0 = "0";
    public static final String VIDEO_ROTATION_180 = "180";
    public static final String VIDEO_ROTATION_270 = "270";
    public static final String VIDEO_ROTATION_90 = "90";
    public static final String WHITE_BALANCE_AUTO = "auto";
    public static final String WHITE_BALANCE_CLOUDY_DAYLIGHT = "cloudy-daylight";
    public static final String WHITE_BALANCE_DAYLIGHT = "daylight";
    public static final String WHITE_BALANCE_FLUORESCENT = "fluorescent";
    public static final String WHITE_BALANCE_INCANDESCENT = "incandescent";
    public static final String WHITE_BALANCE_MANUAL_CCT = "manual-cct";
    public static final String WHITE_BALANCE_SHADE = "shade";
    public static final String WHITE_BALANCE_TWILIGHT = "twilight";
    public static final String WHITE_BALANCE_WARM_FLUORESCENT = "warm-fluorescent";
    public static final String ZSL_OFF = "off";
    public static final String ZSL_ON = "on";
    private final LinkedHashMap<String, String> mMap = new LinkedHashMap(64);
    
    private Parameters() {}
    
    private String cameraFormatForPixelFormat(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return null;
      case 16: 
        return "yuv422sp";
      case 17: 
        return "yuv420sp";
      case 20: 
        return "yuv422i-yuyv";
      case 842094169: 
        return "yuv420p";
      case 4: 
        return "rgb565";
      }
      return "jpeg";
    }
    
    private float getFloat(String paramString, float paramFloat)
    {
      try
      {
        float f = Float.parseFloat((String)this.mMap.get(paramString));
        return f;
      }
      catch (NumberFormatException paramString) {}
      return paramFloat;
    }
    
    private int getInt(String paramString, int paramInt)
    {
      try
      {
        int i = Integer.parseInt((String)this.mMap.get(paramString));
        return i;
      }
      catch (NumberFormatException paramString) {}
      return paramInt;
    }
    
    private Camera getOuter()
    {
      return Camera.this;
    }
    
    private int pixelFormatForCameraFormat(String paramString)
    {
      if (paramString == null) {
        return 0;
      }
      if (paramString.equals("yuv422sp")) {
        return 16;
      }
      if (paramString.equals("yuv420sp")) {
        return 17;
      }
      if (paramString.equals("yuv422i-yuyv")) {
        return 20;
      }
      if (paramString.equals("yuv420p")) {
        return 842094169;
      }
      if (paramString.equals("rgb565")) {
        return 4;
      }
      if (paramString.equals("jpeg")) {
        return 256;
      }
      return 0;
    }
    
    private void put(String paramString1, String paramString2)
    {
      this.mMap.remove(paramString1);
      this.mMap.put(paramString1, paramString2);
    }
    
    private boolean same(String paramString1, String paramString2)
    {
      if ((paramString1 == null) && (paramString2 == null)) {
        return true;
      }
      return (paramString1 != null) && (paramString1.equals(paramString2));
    }
    
    private void set(String paramString, List<Camera.Area> paramList)
    {
      if (paramList == null)
      {
        set(paramString, "(0,0,0,0,0)");
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      while (i < paramList.size())
      {
        Camera.Area localArea = (Camera.Area)paramList.get(i);
        Rect localRect = localArea.rect;
        localStringBuilder.append('(');
        localStringBuilder.append(localRect.left);
        localStringBuilder.append(',');
        localStringBuilder.append(localRect.top);
        localStringBuilder.append(',');
        localStringBuilder.append(localRect.right);
        localStringBuilder.append(',');
        localStringBuilder.append(localRect.bottom);
        localStringBuilder.append(',');
        localStringBuilder.append(localArea.weight);
        localStringBuilder.append(')');
        if (i != paramList.size() - 1) {
          localStringBuilder.append(',');
        }
        i += 1;
      }
      set(paramString, localStringBuilder.toString());
    }
    
    private ArrayList<String> split(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      Object localObject = new TextUtils.SimpleStringSplitter(',');
      ((TextUtils.StringSplitter)localObject).setString(paramString);
      paramString = new ArrayList();
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        paramString.add((String)((Iterator)localObject).next());
      }
      return paramString;
    }
    
    private ArrayList<Camera.Area> splitArea(String paramString)
    {
      if ((paramString == null) || (paramString.charAt(0) != '(')) {}
      while (paramString.charAt(paramString.length() - 1) != ')')
      {
        Log.e("Camera", "Invalid area string=" + paramString);
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      int i = 1;
      Object localObject = new int[5];
      int j;
      do
      {
        int k = paramString.indexOf("),(", i);
        j = k;
        if (k == -1) {
          j = paramString.length() - 1;
        }
        splitInt(paramString.substring(i, j), (int[])localObject);
        localArrayList.add(new Camera.Area(new Rect(localObject[0], localObject[1], localObject[2], localObject[3]), localObject[4]));
        i = j + 3;
      } while (j != paramString.length() - 1);
      if (localArrayList.size() == 0) {
        return null;
      }
      if (localArrayList.size() == 1)
      {
        paramString = (Camera.Area)localArrayList.get(0);
        localObject = paramString.rect;
        if ((((Rect)localObject).left == 0) && (((Rect)localObject).top == 0) && (((Rect)localObject).right == 0) && (((Rect)localObject).bottom == 0) && (paramString.weight == 0)) {
          return null;
        }
      }
      return localArrayList;
    }
    
    private ArrayList<Camera.Coordinate> splitCoordinate(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      Object localObject = new TextUtils.SimpleStringSplitter(',');
      ((TextUtils.StringSplitter)localObject).setString(paramString);
      paramString = new ArrayList();
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        Camera.Coordinate localCoordinate = strToCoordinate((String)((Iterator)localObject).next());
        if (localCoordinate != null) {
          paramString.add(localCoordinate);
        }
      }
      if (paramString.size() == 0) {
        return null;
      }
      return paramString;
    }
    
    private void splitFloat(String paramString, float[] paramArrayOfFloat)
    {
      if (paramString == null) {
        return;
      }
      TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
      localSimpleStringSplitter.setString(paramString);
      int i = 0;
      paramString = localSimpleStringSplitter.iterator();
      while (paramString.hasNext())
      {
        paramArrayOfFloat[i] = Float.parseFloat((String)paramString.next());
        i += 1;
      }
    }
    
    private ArrayList<Integer> splitInt(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      Object localObject = new TextUtils.SimpleStringSplitter(',');
      ((TextUtils.StringSplitter)localObject).setString(paramString);
      paramString = new ArrayList();
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        paramString.add(Integer.valueOf(Integer.parseInt((String)((Iterator)localObject).next())));
      }
      if (paramString.size() == 0) {
        return null;
      }
      return paramString;
    }
    
    private void splitInt(String paramString, int[] paramArrayOfInt)
    {
      if (paramString == null) {
        return;
      }
      TextUtils.SimpleStringSplitter localSimpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
      localSimpleStringSplitter.setString(paramString);
      int i = 0;
      paramString = localSimpleStringSplitter.iterator();
      while (paramString.hasNext())
      {
        paramArrayOfInt[i] = Integer.parseInt((String)paramString.next());
        i += 1;
      }
    }
    
    private ArrayList<int[]> splitRange(String paramString)
    {
      if ((paramString == null) || (paramString.charAt(0) != '(')) {}
      while (paramString.charAt(paramString.length() - 1) != ')')
      {
        Log.e("Camera", "Invalid range list string=" + paramString);
        return null;
      }
      ArrayList localArrayList = new ArrayList();
      int i = 1;
      int j;
      do
      {
        int[] arrayOfInt = new int[2];
        int k = paramString.indexOf("),(", i);
        j = k;
        if (k == -1) {
          j = paramString.length() - 1;
        }
        splitInt(paramString.substring(i, j), arrayOfInt);
        localArrayList.add(arrayOfInt);
        i = j + 3;
      } while (j != paramString.length() - 1);
      if (localArrayList.size() == 0) {
        return null;
      }
      return localArrayList;
    }
    
    private ArrayList<Camera.Size> splitSize(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      Object localObject = new TextUtils.SimpleStringSplitter(',');
      ((TextUtils.StringSplitter)localObject).setString(paramString);
      paramString = new ArrayList();
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        Camera.Size localSize = strToSize((String)((Iterator)localObject).next());
        if (localSize != null) {
          paramString.add(localSize);
        }
      }
      if (paramString.size() == 0) {
        return null;
      }
      return paramString;
    }
    
    private Camera.Coordinate strToCoordinate(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      int i = paramString.indexOf('x');
      if (i != -1)
      {
        String str = paramString.substring(0, i);
        paramString = paramString.substring(i + 1);
        return new Camera.Coordinate(Camera.this, Integer.parseInt(str), Integer.parseInt(paramString));
      }
      Log.e("Camera", "Invalid Coordinate parameter string=" + paramString);
      return null;
    }
    
    private Camera.Size strToSize(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      int i = paramString.indexOf('x');
      if (i != -1)
      {
        String str = paramString.substring(0, i);
        paramString = paramString.substring(i + 1);
        return new Camera.Size(Camera.this, Integer.parseInt(str), Integer.parseInt(paramString));
      }
      Log.e("Camera", "Invalid size parameter string=" + paramString);
      return null;
    }
    
    public void copyFrom(Parameters paramParameters)
    {
      if (paramParameters == null) {
        throw new NullPointerException("other must not be null");
      }
      this.mMap.putAll(paramParameters.mMap);
    }
    
    @Deprecated
    public void dump()
    {
      Log.e("Camera", "dump: size=" + this.mMap.size());
      Iterator localIterator = this.mMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Log.e("Camera", "dump: " + str + "=" + (String)this.mMap.get(str));
      }
    }
    
    public String flatten()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      Iterator localIterator = this.mMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localStringBuilder.append(str);
        localStringBuilder.append("=");
        localStringBuilder.append((String)this.mMap.get(str));
        localStringBuilder.append(";");
      }
      localStringBuilder.deleteCharAt(localStringBuilder.length() - 1);
      return localStringBuilder.toString();
    }
    
    public String get(String paramString)
    {
      return (String)this.mMap.get(paramString);
    }
    
    public String getAEBracket()
    {
      return get("ae-bracket-hdr");
    }
    
    public String getAntibanding()
    {
      return get("antibanding");
    }
    
    public String getAutoExposure()
    {
      return get("auto-exposure");
    }
    
    public boolean getAutoExposureLock()
    {
      return "true".equals(get("auto-exposure-lock"));
    }
    
    public boolean getAutoWhiteBalanceLock()
    {
      return "true".equals(get("auto-whitebalance-lock"));
    }
    
    public String getCameraMode()
    {
      return get("camera-mode");
    }
    
    public String getColorEffect()
    {
      return get("effect");
    }
    
    public String getContinuousAf()
    {
      return get("continuous-af");
    }
    
    public int getContrast()
    {
      return getInt("contrast");
    }
    
    public String getCurrentFocusPosition()
    {
      return get("manual-focus-position");
    }
    
    public String getDenoise()
    {
      return get("denoise");
    }
    
    public int getExposureCompensation()
    {
      return getInt("exposure-compensation", 0);
    }
    
    public float getExposureCompensationStep()
    {
      return getFloat("exposure-compensation-step", 0.0F);
    }
    
    public String getExposureTime()
    {
      return get("exposure-time");
    }
    
    public String getFaceDetectionMode()
    {
      return get("face-detection");
    }
    
    public String getFlashMode()
    {
      if (getSupportedFlashModes() == null) {
        return null;
      }
      return get("flash-mode");
    }
    
    public float getFocalLength()
    {
      return Float.parseFloat(get("focal-length"));
    }
    
    public List<Camera.Area> getFocusAreas()
    {
      return splitArea(get("focus-areas"));
    }
    
    public void getFocusDistances(float[] paramArrayOfFloat)
    {
      if ((paramArrayOfFloat == null) || (paramArrayOfFloat.length != 3)) {
        throw new IllegalArgumentException("output must be a float array with three elements.");
      }
      splitFloat(get("focus-distances"), paramArrayOfFloat);
    }
    
    public String getFocusMode()
    {
      return get("focus-mode");
    }
    
    public float getHorizontalViewAngle()
    {
      return Float.parseFloat(get("horizontal-view-angle"));
    }
    
    public String getISOValue()
    {
      return get("iso");
    }
    
    public int getInt(String paramString)
    {
      return Integer.parseInt((String)this.mMap.get(paramString));
    }
    
    public int getJpegQuality()
    {
      return getInt("jpeg-quality");
    }
    
    public int getJpegThumbnailQuality()
    {
      return getInt("jpeg-thumbnail-quality");
    }
    
    public Camera.Size getJpegThumbnailSize()
    {
      return new Camera.Size(Camera.this, getInt("jpeg-thumbnail-width"), getInt("jpeg-thumbnail-height"));
    }
    
    public String getLensShade()
    {
      return get("lensshade");
    }
    
    public int getMaxContrast()
    {
      return getInt("max-contrast");
    }
    
    public int getMaxExposureCompensation()
    {
      return getInt("max-exposure-compensation", 0);
    }
    
    public String getMaxExposureTime()
    {
      return get("max-exposure-time");
    }
    
    public int getMaxNumDetectedFaces()
    {
      return getInt("max-num-detected-faces-hw", 0);
    }
    
    public int getMaxNumFocusAreas()
    {
      return getInt("max-num-focus-areas", 0);
    }
    
    public int getMaxNumMeteringAreas()
    {
      return getInt("max-num-metering-areas", 0);
    }
    
    public int getMaxSaturation()
    {
      return getInt("max-saturation");
    }
    
    public int getMaxSharpness()
    {
      return getInt("max-sharpness");
    }
    
    public String getMaxWBCCT()
    {
      return get("max-wb-cct");
    }
    
    public int getMaxZoom()
    {
      return getInt("max-zoom", 0);
    }
    
    public String getMemColorEnhance()
    {
      return get("mce");
    }
    
    public List<Camera.Area> getMeteringAreas()
    {
      return splitArea(get("metering-areas"));
    }
    
    public int getMinExposureCompensation()
    {
      return getInt("min-exposure-compensation", 0);
    }
    
    public String getMinExposureTime()
    {
      return get("min-exposure-time");
    }
    
    public int getPictureFormat()
    {
      return pixelFormatForCameraFormat(get("picture-format"));
    }
    
    public Camera.Size getPictureSize()
    {
      return strToSize(get("picture-size"));
    }
    
    public String getPowerMode()
    {
      return get("power-mode");
    }
    
    public Camera.Size getPreferredPreviewSizeForVideo()
    {
      return strToSize(get("preferred-preview-size-for-video"));
    }
    
    public int getPreviewFormat()
    {
      return pixelFormatForCameraFormat(get("preview-format"));
    }
    
    public void getPreviewFpsRange(int[] paramArrayOfInt)
    {
      if ((paramArrayOfInt == null) || (paramArrayOfInt.length != 2)) {
        throw new IllegalArgumentException("range must be an array with two elements.");
      }
      splitInt(get("preview-fps-range"), paramArrayOfInt);
    }
    
    @Deprecated
    public int getPreviewFrameRate()
    {
      return getInt("preview-frame-rate");
    }
    
    public String getPreviewFrameRateMode()
    {
      return get("preview-frame-rate-mode");
    }
    
    public Camera.Size getPreviewSize()
    {
      return strToSize(get("preview-size"));
    }
    
    public String getRedeyeReductionMode()
    {
      return get("redeye-reduction");
    }
    
    public int getSaturation()
    {
      return getInt("saturation");
    }
    
    public String getSceneDetectMode()
    {
      return get("scene-detect");
    }
    
    public String getSceneMode()
    {
      return get("scene-mode");
    }
    
    public String getSelectableZoneAf()
    {
      return get("selectable-zone-af");
    }
    
    public int getSharpness()
    {
      return getInt("sharpness");
    }
    
    public List<String> getSupportedAntibanding()
    {
      return split(get("antibanding-values"));
    }
    
    public List<String> getSupportedAutoexposure()
    {
      return split(get("auto-exposure-values"));
    }
    
    public List<String> getSupportedColorEffects()
    {
      return split(get("effect-values"));
    }
    
    public List<String> getSupportedContinuousAfModes()
    {
      return split(get("continuous-af-values"));
    }
    
    public List<String> getSupportedDenoiseModes()
    {
      return split(get("denoise-values"));
    }
    
    public List<String> getSupportedFaceDetectionModes()
    {
      return split(get("face-detection-values"));
    }
    
    public List<String> getSupportedFlashModes()
    {
      String str = ActivityThread.currentPackageName();
      if (str != null) {}
      for (boolean bool = str.equals("com.oneplus.camera");; bool = false)
      {
        str = getFocusMode();
        if ((str != null) && (str.equals("fixed")) && (!bool)) {
          break;
        }
        return split(get("flash-mode-values"));
      }
      return null;
    }
    
    public List<String> getSupportedFocusModes()
    {
      return split(get("focus-mode-values"));
    }
    
    public List<Camera.Size> getSupportedHfrSizes()
    {
      return splitSize(get("hfr-size-values"));
    }
    
    public List<String> getSupportedHistogramModes()
    {
      return split(get("histogram-values"));
    }
    
    public List<String> getSupportedIsoValues()
    {
      return split(get("iso-values"));
    }
    
    public List<Camera.Size> getSupportedJpegThumbnailSizes()
    {
      return splitSize(get("jpeg-thumbnail-size-values"));
    }
    
    public List<String> getSupportedLensShadeModes()
    {
      return split(get("lensshade-values"));
    }
    
    public List<String> getSupportedMemColorEnhanceModes()
    {
      return split(get("mce-values"));
    }
    
    public List<Integer> getSupportedPictureFormats()
    {
      Object localObject = get("picture-format-values");
      ArrayList localArrayList = new ArrayList();
      localObject = split((String)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        int i = pixelFormatForCameraFormat((String)((Iterator)localObject).next());
        if (i != 0) {
          localArrayList.add(Integer.valueOf(i));
        }
      }
      return localArrayList;
    }
    
    public List<Camera.Size> getSupportedPictureSizes()
    {
      return splitSize(get("picture-size-values"));
    }
    
    public List<Integer> getSupportedPreviewFormats()
    {
      Object localObject = get("preview-format-values");
      ArrayList localArrayList = new ArrayList();
      localObject = split((String)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        int i = pixelFormatForCameraFormat((String)((Iterator)localObject).next());
        if (i != 0) {
          localArrayList.add(Integer.valueOf(i));
        }
      }
      return localArrayList;
    }
    
    public List<int[]> getSupportedPreviewFpsRange()
    {
      return splitRange(get("preview-fps-range-values"));
    }
    
    public List<String> getSupportedPreviewFrameRateModes()
    {
      return split(get("preview-frame-rate-mode-values"));
    }
    
    @Deprecated
    public List<Integer> getSupportedPreviewFrameRates()
    {
      return splitInt(get("preview-frame-rate-values"));
    }
    
    public List<Camera.Size> getSupportedPreviewSizes()
    {
      return splitSize(get("preview-size-values"));
    }
    
    public List<String> getSupportedRedeyeReductionModes()
    {
      return split(get("redeye-reduction-values"));
    }
    
    public List<String> getSupportedSceneDetectModes()
    {
      return split(get("scene-detect-values"));
    }
    
    public List<String> getSupportedSceneModes()
    {
      return split(get("scene-mode-values"));
    }
    
    public List<String> getSupportedSelectableZoneAf()
    {
      return split(get("selectable-zone-af-values"));
    }
    
    public List<String> getSupportedSkinToneEnhancementModes()
    {
      return split(get("skinToneEnhancement-values"));
    }
    
    public List<String> getSupportedTouchAfAec()
    {
      return split(get("touch-af-aec-values"));
    }
    
    public List<String> getSupportedVideoHDRModes()
    {
      return split(get("video-hdr-values"));
    }
    
    public List<String> getSupportedVideoHighFrameRateModes()
    {
      return split(get("video-hfr-values"));
    }
    
    public List<String> getSupportedVideoRotationValues()
    {
      return split(get("video-rotation-values"));
    }
    
    public List<Camera.Size> getSupportedVideoSizes()
    {
      return splitSize(get("video-size-values"));
    }
    
    public List<String> getSupportedWhiteBalance()
    {
      return split(get("whitebalance-values"));
    }
    
    public List<String> getSupportedZSLModes()
    {
      return split(get("zsl-values"));
    }
    
    public String getTouchAfAec()
    {
      return get("touch-af-aec");
    }
    
    public Camera.Coordinate getTouchIndexAec()
    {
      return strToCoordinate(get("touch-index-aec"));
    }
    
    public Camera.Coordinate getTouchIndexAf()
    {
      return strToCoordinate(get("touch-index-af"));
    }
    
    public float getVerticalViewAngle()
    {
      return Float.parseFloat(get("vertical-view-angle"));
    }
    
    public String getVideoHDRMode()
    {
      return get("video-hdr");
    }
    
    public String getVideoHighFrameRate()
    {
      return get("video-hfr");
    }
    
    public String getVideoRotation()
    {
      return get("video-rotation");
    }
    
    public boolean getVideoStabilization()
    {
      return "true".equals(get("video-stabilization"));
    }
    
    public String getWBCurrentCCT()
    {
      return get("wb-manual-cct");
    }
    
    public String getWBMinCCT()
    {
      return get("min-wb-cct");
    }
    
    public String getWhiteBalance()
    {
      return get("whitebalance");
    }
    
    public String getZSLMode()
    {
      return get("zsl");
    }
    
    public int getZoom()
    {
      return getInt("zoom", 0);
    }
    
    public List<Integer> getZoomRatios()
    {
      return splitInt(get("zoom-ratios"));
    }
    
    public boolean isAutoExposureLockSupported()
    {
      return "true".equals(get("auto-exposure-lock-supported"));
    }
    
    public boolean isAutoWhiteBalanceLockSupported()
    {
      return "true".equals(get("auto-whitebalance-lock-supported"));
    }
    
    public boolean isPowerModeSupported()
    {
      return "true".equals(get("power-mode-supported"));
    }
    
    public boolean isSmoothZoomSupported()
    {
      return "true".equals(get("smooth-zoom-supported"));
    }
    
    public boolean isSupportedSmileDetection()
    {
      return "true".equals(get("smile-detection"));
    }
    
    public boolean isVideoSnapshotSupported()
    {
      return "true".equals(get("video-snapshot-supported"));
    }
    
    public boolean isVideoStabilizationSupported()
    {
      return "true".equals(get("video-stabilization-supported"));
    }
    
    public boolean isZoomSupported()
    {
      return "true".equals(get("zoom-supported"));
    }
    
    public void remove(String paramString)
    {
      this.mMap.remove(paramString);
    }
    
    public void removeGpsData()
    {
      remove("gps-latitude-ref");
      remove("gps-latitude");
      remove("gps-longitude-ref");
      remove("gps-longitude");
      remove("gps-altitude-ref");
      remove("gps-altitude");
      remove("gps-timestamp");
      remove("gps-processing-method");
    }
    
    public boolean same(Parameters paramParameters)
    {
      if (this == paramParameters) {
        return true;
      }
      if (paramParameters != null) {
        return this.mMap.equals(paramParameters.mMap);
      }
      return false;
    }
    
    public void set(String paramString, int paramInt)
    {
      put(paramString, Integer.toString(paramInt));
    }
    
    public void set(String paramString1, String paramString2)
    {
      if ((paramString1.indexOf('=') != -1) || (paramString1.indexOf(';') != -1)) {}
      while (paramString1.indexOf(0) != -1)
      {
        Log.e("Camera", "Key \"" + paramString1 + "\" contains invalid character (= or ; or \\0)");
        return;
      }
      if ((paramString2.indexOf('=') != -1) || (paramString2.indexOf(';') != -1)) {}
      while (paramString2.indexOf(0) != -1)
      {
        Log.e("Camera", "Value \"" + paramString2 + "\" contains invalid character (= or ; or \\0)");
        return;
      }
      put(paramString1, paramString2);
    }
    
    public void setAEBracket(String paramString)
    {
      set("ae-bracket-hdr", paramString);
    }
    
    public void setAntibanding(String paramString)
    {
      set("antibanding", paramString);
    }
    
    public void setAutoExposure(String paramString)
    {
      set("auto-exposure", paramString);
    }
    
    public void setAutoExposureLock(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (String str = "true";; str = "false")
      {
        set("auto-exposure-lock", str);
        return;
      }
    }
    
    public void setAutoHDRMode(String paramString)
    {
      set("auto-hdr-enable", paramString);
    }
    
    public void setAutoWhiteBalanceLock(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (String str = "true";; str = "false")
      {
        set("auto-whitebalance-lock", str);
        return;
      }
    }
    
    public void setCameraMode(int paramInt)
    {
      set("camera-mode", paramInt);
    }
    
    public void setColorEffect(String paramString)
    {
      set("effect", paramString);
    }
    
    public void setContinuousAf(String paramString)
    {
      set("continuous-af", paramString);
    }
    
    public void setContrast(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > getMaxContrast())) {
        throw new IllegalArgumentException("Invalid Contrast " + paramInt);
      }
      set("contrast", String.valueOf(paramInt));
    }
    
    public void setDenoise(String paramString)
    {
      set("denoise", paramString);
    }
    
    public void setExifDateTime(String paramString)
    {
      set("exif-datetime", paramString);
    }
    
    public void setExposureCompensation(int paramInt)
    {
      set("exposure-compensation", paramInt);
    }
    
    public void setExposureTime(int paramInt)
    {
      set("exposure-time", Integer.toString(paramInt));
    }
    
    public void setFaceDetectionMode(String paramString)
    {
      set("face-detection", paramString);
    }
    
    public void setFlashMode(String paramString)
    {
      set("flash-mode", paramString);
    }
    
    public void setFocusAreas(List<Camera.Area> paramList)
    {
      set("focus-areas", paramList);
    }
    
    public void setFocusMode(String paramString)
    {
      set("focus-mode", paramString);
    }
    
    public void setFocusPosition(int paramInt1, int paramInt2)
    {
      set("manual-focus-pos-type", Integer.toString(paramInt1));
      set("manual-focus-position", Integer.toString(paramInt2));
    }
    
    public void setGpsAltitude(double paramDouble)
    {
      set("gps-altitude", Double.toString(paramDouble));
    }
    
    public void setGpsAltitudeRef(double paramDouble)
    {
      set("gps-altitude-ref", Double.toString(paramDouble));
    }
    
    public void setGpsLatitude(double paramDouble)
    {
      set("gps-latitude", Double.toString(paramDouble));
    }
    
    public void setGpsLatitudeRef(String paramString)
    {
      set("gps-latitude-ref", paramString);
    }
    
    public void setGpsLongitude(double paramDouble)
    {
      set("gps-longitude", Double.toString(paramDouble));
    }
    
    public void setGpsLongitudeRef(String paramString)
    {
      set("gps-longitude-ref", paramString);
    }
    
    public void setGpsProcessingMethod(String paramString)
    {
      set("gps-processing-method", paramString);
    }
    
    public void setGpsStatus(double paramDouble)
    {
      set("gps-status", Double.toString(paramDouble));
    }
    
    public void setGpsTimestamp(long paramLong)
    {
      set("gps-timestamp", Long.toString(paramLong));
    }
    
    public void setISOValue(String paramString)
    {
      set("iso", paramString);
    }
    
    public void setJpegQuality(int paramInt)
    {
      set("jpeg-quality", paramInt);
    }
    
    public void setJpegThumbnailQuality(int paramInt)
    {
      set("jpeg-thumbnail-quality", paramInt);
    }
    
    public void setJpegThumbnailSize(int paramInt1, int paramInt2)
    {
      set("jpeg-thumbnail-width", paramInt1);
      set("jpeg-thumbnail-height", paramInt2);
    }
    
    public void setLensShade(String paramString)
    {
      set("lensshade", paramString);
    }
    
    public void setMemColorEnhance(String paramString)
    {
      set("mce", paramString);
    }
    
    public void setMeteringAreas(List<Camera.Area> paramList)
    {
      set("metering-areas", paramList);
    }
    
    public void setPictureFormat(int paramInt)
    {
      String str = cameraFormatForPixelFormat(paramInt);
      if (str == null) {
        throw new IllegalArgumentException("Invalid pixel_format=" + paramInt);
      }
      set("picture-format", str);
    }
    
    public void setPictureSize(int paramInt1, int paramInt2)
    {
      set("picture-size", Integer.toString(paramInt1) + "x" + Integer.toString(paramInt2));
    }
    
    public void setPowerMode(String paramString)
    {
      set("power-mode", paramString);
    }
    
    public void setPreviewFormat(int paramInt)
    {
      String str = cameraFormatForPixelFormat(paramInt);
      if (str == null) {
        throw new IllegalArgumentException("Invalid pixel_format=" + paramInt);
      }
      set("preview-format", str);
    }
    
    public void setPreviewFpsRange(int paramInt1, int paramInt2)
    {
      set("preview-fps-range", "" + paramInt1 + "," + paramInt2);
    }
    
    @Deprecated
    public void setPreviewFrameRate(int paramInt)
    {
      set("preview-frame-rate", paramInt);
    }
    
    public void setPreviewFrameRateMode(String paramString)
    {
      set("preview-frame-rate-mode", paramString);
    }
    
    public void setPreviewSize(int paramInt1, int paramInt2)
    {
      set("preview-size", Integer.toString(paramInt1) + "x" + Integer.toString(paramInt2));
    }
    
    public void setRecordingHint(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (String str = "true";; str = "false")
      {
        set("recording-hint", str);
        return;
      }
    }
    
    public void setRedeyeReductionMode(String paramString)
    {
      set("redeye-reduction", paramString);
    }
    
    public void setRotation(int paramInt)
    {
      if ((paramInt == 0) || (paramInt == 90)) {}
      while ((paramInt == 180) || (paramInt == 270))
      {
        set("rotation", Integer.toString(paramInt));
        return;
      }
      throw new IllegalArgumentException("Invalid rotation=" + paramInt);
    }
    
    public void setSaturation(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > getMaxSaturation())) {
        throw new IllegalArgumentException("Invalid Saturation " + paramInt);
      }
      set("saturation", String.valueOf(paramInt));
    }
    
    public void setSceneDetectMode(String paramString)
    {
      set("scene-detect", paramString);
    }
    
    public void setSceneMode(String paramString)
    {
      set("scene-mode", paramString);
    }
    
    public void setSelectableZoneAf(String paramString)
    {
      set("selectable-zone-af", paramString);
    }
    
    public void setSharpness(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > getMaxSharpness())) {
        throw new IllegalArgumentException("Invalid Sharpness " + paramInt);
      }
      set("sharpness", String.valueOf(paramInt));
    }
    
    public void setTouchAfAec(String paramString)
    {
      set("touch-af-aec", paramString);
    }
    
    public void setTouchIndexAec(int paramInt1, int paramInt2)
    {
      set("touch-index-aec", Integer.toString(paramInt1) + "x" + Integer.toString(paramInt2));
    }
    
    public void setTouchIndexAf(int paramInt1, int paramInt2)
    {
      set("touch-index-af", Integer.toString(paramInt1) + "x" + Integer.toString(paramInt2));
    }
    
    public void setVideoHDRMode(String paramString)
    {
      set("video-hdr", paramString);
    }
    
    public void setVideoHighFrameRate(String paramString)
    {
      set("video-hfr", paramString);
    }
    
    public void setVideoRotation(String paramString)
    {
      set("video-rotation", paramString);
    }
    
    public void setVideoStabilization(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (String str = "true";; str = "false")
      {
        set("video-stabilization", str);
        return;
      }
    }
    
    public void setWBManualCCT(int paramInt)
    {
      set("wb-manual-cct", Integer.toString(paramInt));
    }
    
    public void setWhiteBalance(String paramString)
    {
      if (same(paramString, get("whitebalance"))) {
        return;
      }
      set("whitebalance", paramString);
      set("auto-whitebalance-lock", "false");
    }
    
    public void setZSLMode(String paramString)
    {
      set("zsl", paramString);
    }
    
    public void setZoom(int paramInt)
    {
      set("zoom", paramInt);
    }
    
    public void unflatten(String paramString)
    {
      this.mMap.clear();
      Object localObject = new TextUtils.SimpleStringSplitter(';');
      ((TextUtils.StringSplitter)localObject).setString(paramString);
      paramString = ((Iterable)localObject).iterator();
      while (paramString.hasNext())
      {
        String str = (String)paramString.next();
        int i = str.indexOf('=');
        if (i != -1)
        {
          localObject = str.substring(0, i);
          str = str.substring(i + 1);
          this.mMap.put(localObject, str);
        }
      }
    }
  }
  
  @Deprecated
  public static abstract interface PictureCallback
  {
    public abstract void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera);
  }
  
  @Deprecated
  public static abstract interface PreviewCallback
  {
    public abstract void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera);
  }
  
  public static abstract interface ProcessCallback
  {
    public abstract void onProcess();
  }
  
  @Deprecated
  public static abstract interface ShutterCallback
  {
    public abstract void onShutter();
  }
  
  @Deprecated
  public class Size
  {
    public int height;
    public int width;
    
    public Size(int paramInt1, int paramInt2)
    {
      this.width = paramInt1;
      this.height = paramInt2;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if (!(paramObject instanceof Size)) {
        return false;
      }
      paramObject = (Size)paramObject;
      boolean bool1 = bool2;
      if (this.width == ((Size)paramObject).width)
      {
        bool1 = bool2;
        if (this.height == ((Size)paramObject).height) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public int hashCode()
    {
      return this.width * 32713 + this.height;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/Camera.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */