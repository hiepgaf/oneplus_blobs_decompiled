package com.oneplus.camera;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureRequest.Key;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.MeteringRectangle;
import android.media.AudioManager;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.camera.media.ImagePlane;
import com.oneplus.camera.media.YuvUtils;
import com.oneplus.util.AutomaticId;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class CameraImpl
  extends BaseCamera
{
  private static final Semaphore CAMERA_SEMAPHORE = new Semaphore(1);
  private static final boolean DEBUG_DUMP_CAPTURE_REQUEST = false;
  private static final long EXPOSURE_TIME_PREVIEW_MAX = 200000000L;
  private static final int FLAG_ON_PREVIEW_RESTART = 1;
  private static final long INTERNAL_PREVIEW_CALLBACK_PROFILE = 2000L;
  private static final long INTERVAL_OPEN_DEVICE_RETRY = 100L;
  private static final byte JPEG_QUALITY_BURST = 90;
  private static final int MSG_AF_COMPLETE_TIMEOUT = 10012;
  private static final int MSG_AF_START_TIMEOUT = 10011;
  private static final int MSG_CAPTURE_SESSION_CLOSE_TIMEOUT = 10020;
  private static final int MSG_FLASH_PRECAPTURE_TIMEOUT = 10030;
  private static final int MSG_PREVIEW_FRAME_RECEIVED = 10000;
  private static final int MSG_PREVIEW_FRAME_TIMEOUT = 10001;
  private static final int MSG_START_AF = 10010;
  private static final boolean PROFILE_PREVIEW_CALLBACK = true;
  private static final long TIMEOUT_AF_COMPLETE = 5000L;
  private static final long TIMEOUT_AF_START = 5000L;
  private static final long TIMEOUT_CAPTURE_SESSION_CLOSED = 1000L;
  private static final long TIMEOUT_OPEN_DEVICE_RETRY = 5000L;
  private static final long TIMEOUT_PRECAPTURE_FLASH = 5000L;
  private static final long TIMEOUT_PREVIEW_FRAME = 3000L;
  private List<Camera.MeteringRect> m_AeRegions = Collections.EMPTY_LIST;
  private List<Camera.MeteringRect> m_AfRegions = Collections.EMPTY_LIST;
  private int m_AwbMode = 1;
  private CameraCharacteristics m_CameraCharacteristics;
  private final CameraInfo m_CameraInfo;
  private final CameraManager m_CameraManager;
  private Handle m_CaptureHandle;
  private CameraCaptureSession m_CaptureSession;
  private final CameraCaptureSession.StateCallback m_CaptureSessionCallback = new CameraCaptureSession.StateCallback()
  {
    public void onClosed(CameraCaptureSession paramAnonymousCameraCaptureSession)
    {
      CameraImpl.-wrap3(CameraImpl.this, paramAnonymousCameraCaptureSession);
    }
    
    public void onConfigureFailed(CameraCaptureSession paramAnonymousCameraCaptureSession)
    {
      CameraImpl.-wrap4(CameraImpl.this, paramAnonymousCameraCaptureSession);
    }
    
    public void onConfigured(CameraCaptureSession paramAnonymousCameraCaptureSession)
    {
      CameraImpl.-wrap5(CameraImpl.this, paramAnonymousCameraCaptureSession);
    }
  };
  private OperationState m_CaptureSessionState = OperationState.STOPPED;
  private Range<Integer> m_DefaultPhotoPreviewFpsRange;
  private Range<Integer> m_DefaultVideoPreviewFpsRange;
  private CameraDevice m_Device;
  private final CameraDevice.StateCallback m_DeviceStateCallback = new CameraDevice.StateCallback()
  {
    public void onDisconnected(CameraDevice paramAnonymousCameraDevice)
    {
      CameraImpl.-wrap7(CameraImpl.this, paramAnonymousCameraDevice, 0, true);
    }
    
    public void onError(CameraDevice paramAnonymousCameraDevice, int paramAnonymousInt)
    {
      CameraImpl.-wrap7(CameraImpl.this, paramAnonymousCameraDevice, paramAnonymousInt, false);
    }
    
    public void onOpened(CameraDevice paramAnonymousCameraDevice)
    {
      CameraImpl.-wrap8(CameraImpl.this, paramAnonymousCameraDevice);
    }
  };
  private float m_DigitalZoom = 1.0F;
  private float m_ExposureCompensation;
  private long m_ExposureTime = -1L;
  private int m_FaceListIndex;
  private final List<Camera.Face>[] m_FaceLists = new List[2];
  private FlashMode m_FlashMode = FlashMode.OFF;
  private FocusMode m_FocusMode = FocusMode.DISABLED;
  private float m_FocusValue = -1.0F;
  private int m_ISOValue = -1;
  private final String m_Id;
  private boolean m_IsAELocked;
  private boolean m_IsAutoFocusStarting;
  private boolean m_IsAutoFocusTimeout;
  private boolean m_IsAwbLocked;
  private boolean m_IsCaptureSequenceCompleted;
  private boolean m_IsCaptureSessionClosed = false;
  private boolean m_IsCaptureStartedReceived;
  private boolean m_IsFaceDetectionEnabled = true;
  private boolean m_IsPreCaptureFlashComplete;
  private boolean m_IsPreCaptureFlashTimeout;
  private boolean m_IsPreCaptureFlashTriggered;
  private volatile boolean m_IsPreviewReceived;
  private boolean m_IsPreviewStoppedForCapture;
  private boolean m_IsRawCaptureEnabled;
  private boolean m_IsRecordingMode;
  private int m_JpegQuality = -1;
  private long m_LastPreviewCbProfileTime;
  private int m_LastRawFocusState = 0;
  private MediaRecorder m_MediaRecorder;
  private long m_OpenCameraStartTime;
  private FlashMode m_PendingFlashMode;
  private final ImageReader.OnImageAvailableListener m_PictureAvailableListener = new ImageReader.OnImageAvailableListener()
  {
    /* Error */
    public void onImageAvailable(ImageReader paramAnonymousImageReader)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 28	android/media/ImageReader:acquireLatestImage	()Landroid/media/Image;
      //   4: astore_1
      //   5: aload_1
      //   6: ifnull +11 -> 17
      //   9: aload_0
      //   10: getfield 14	com/oneplus/camera/CameraImpl$3:this$0	Lcom/oneplus/camera/CameraImpl;
      //   13: aload_1
      //   14: invokestatic 32	com/oneplus/camera/CameraImpl:-wrap9	(Lcom/oneplus/camera/CameraImpl;Landroid/media/Image;)V
      //   17: aload_1
      //   18: ifnull +7 -> 25
      //   21: aload_1
      //   22: invokevirtual 37	android/media/Image:close	()V
      //   25: return
      //   26: astore_1
      //   27: aconst_null
      //   28: astore_1
      //   29: goto -24 -> 5
      //   32: astore_2
      //   33: aload_1
      //   34: ifnull +7 -> 41
      //   37: aload_1
      //   38: invokevirtual 37	android/media/Image:close	()V
      //   41: aload_2
      //   42: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	43	0	this	3
      //   0	43	1	paramAnonymousImageReader	ImageReader
      //   32	10	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   0	5	26	java/lang/Throwable
      //   9	17	32	finally
    }
  };
  private final CameraCaptureSession.CaptureCallback m_PictureCaptureCallback = new CameraCaptureSession.CaptureCallback()
  {
    public void onCaptureCompleted(CameraCaptureSession paramAnonymousCameraCaptureSession, CaptureRequest paramAnonymousCaptureRequest, TotalCaptureResult paramAnonymousTotalCaptureResult)
    {
      CameraImpl.-wrap1(CameraImpl.this, paramAnonymousCameraCaptureSession, paramAnonymousCaptureRequest, paramAnonymousTotalCaptureResult, null);
    }
    
    public void onCaptureFailed(CameraCaptureSession paramAnonymousCameraCaptureSession, CaptureRequest paramAnonymousCaptureRequest, CaptureFailure paramAnonymousCaptureFailure)
    {
      long l = paramAnonymousCaptureFailure.getFrameNumber();
      int i = paramAnonymousCaptureFailure.getReason();
      Log.e(CameraImpl.-get0(CameraImpl.this), "onCaptureFailed() - Frame index : " + l + ", reason : " + i);
    }
    
    public void onCaptureProgressed(CameraCaptureSession paramAnonymousCameraCaptureSession, CaptureRequest paramAnonymousCaptureRequest, CaptureResult paramAnonymousCaptureResult)
    {
      Log.w(CameraImpl.-get0(CameraImpl.this), "onCaptureProgressed");
    }
    
    public void onCaptureSequenceCompleted(CameraCaptureSession paramAnonymousCameraCaptureSession, int paramAnonymousInt, long paramAnonymousLong)
    {
      CameraImpl.-wrap2(CameraImpl.this);
    }
    
    public void onCaptureStarted(CameraCaptureSession paramAnonymousCameraCaptureSession, CaptureRequest paramAnonymousCaptureRequest, long paramAnonymousLong1, long paramAnonymousLong2)
    {
      CameraImpl.-wrap6(CameraImpl.this, paramAnonymousCameraCaptureSession, paramAnonymousCaptureRequest, paramAnonymousLong1, paramAnonymousLong2);
    }
  };
  private CaptureRequest m_PictureCaptureRequest;
  private ImageReader m_PictureReader;
  private Size m_PictureSize = new Size(0, 0);
  private Surface m_PictureSurface;
  private byte[] m_PreviewCallbackBuffer;
  private int m_PreviewCallbackCount;
  private ImageReader m_PreviewCallbackReader;
  private final ImageReader.OnImageAvailableListener m_PreviewCallbackReaderCallback = new ImageReader.OnImageAvailableListener()
  {
    public void onImageAvailable(ImageReader paramAnonymousImageReader)
    {
      CameraImpl.-wrap12(CameraImpl.this);
    }
  };
  private Surface m_PreviewCallbackSurface;
  private final CameraCaptureSession.CaptureCallback m_PreviewCaptureCallback = new CameraCaptureSession.CaptureCallback()
  {
    public void onCaptureCompleted(CameraCaptureSession paramAnonymousCameraCaptureSession, CaptureRequest paramAnonymousCaptureRequest, TotalCaptureResult paramAnonymousTotalCaptureResult)
    {
      CameraImpl.-wrap11(CameraImpl.this, paramAnonymousTotalCaptureResult);
    }
  };
  private Range<Integer> m_PreviewFpsRange;
  private CaptureRequest.Builder m_PreviewRequestBuilder;
  private Size m_PreviewSize = new Size(0, 0);
  private Surface m_PreviewSurface;
  private AutoExposureState m_PreviousAeState;
  private int m_PreviousFlashState;
  private final ImageReader.OnImageAvailableListener m_RawCallback = new ImageReader.OnImageAvailableListener()
  {
    /* Error */
    public void onImageAvailable(ImageReader paramAnonymousImageReader)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 28	android/media/ImageReader:acquireLatestImage	()Landroid/media/Image;
      //   4: astore_1
      //   5: aload_1
      //   6: ifnull +11 -> 17
      //   9: aload_0
      //   10: getfield 14	com/oneplus/camera/CameraImpl$7:this$0	Lcom/oneplus/camera/CameraImpl;
      //   13: aload_1
      //   14: invokestatic 32	com/oneplus/camera/CameraImpl:-wrap13	(Lcom/oneplus/camera/CameraImpl;Landroid/media/Image;)V
      //   17: aload_1
      //   18: ifnull +7 -> 25
      //   21: aload_1
      //   22: invokevirtual 37	android/media/Image:close	()V
      //   25: return
      //   26: astore_1
      //   27: aconst_null
      //   28: astore_1
      //   29: goto -24 -> 5
      //   32: astore_2
      //   33: aload_1
      //   34: ifnull +7 -> 41
      //   37: aload_1
      //   38: invokevirtual 37	android/media/Image:close	()V
      //   41: aload_2
      //   42: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	43	0	this	7
      //   0	43	1	paramAnonymousImageReader	ImageReader
      //   32	10	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   0	5	26	java/lang/Throwable
      //   9	17	32	finally
    }
  };
  private ImageReader m_RawReader;
  private Surface m_RawSurface;
  private int m_ReceivedCaptureCompletedCount;
  private final Queue<CaptureResult> m_ReceivedCaptureCompletedResults = new LinkedList();
  private int m_ReceivedCaptureStartedCount;
  private final Queue<CaptureResult> m_ReceivedCaptureStartedResults = new LinkedList();
  private int m_ReceivedPictureCount;
  private final Queue<ImagePlane[]> m_ReceivedPictures = new LinkedList();
  private int m_ReceivedRawPictureCount;
  private final Queue<ImagePlane[]> m_ReceivedRawPictures = new LinkedList();
  private int m_SceneMode = 0;
  private volatile Camera.State m_State = Camera.State.CLOSED;
  private int m_TargetCapturedFrameCount;
  private final List m_TempList = new ArrayList();
  private final List<Surface> m_TempSurfaces = new ArrayList();
  private Size m_VideoSize = new Size(0, 0);
  private Surface m_VideoSurface;
  
  public CameraImpl(Context paramContext, CameraManager paramCameraManager, String paramString, CameraInfo paramCameraInfo)
  {
    super(paramContext, paramCameraInfo);
    Log.w(this.TAG, "CameraImpl() - ID : " + paramString);
    this.m_CameraManager = paramCameraManager;
    this.m_CameraInfo = paramCameraInfo;
    this.m_Id = paramString;
    paramContext = (List)get(PROP_PICTURE_SIZES);
    if (!paramContext.isEmpty()) {
      this.m_PictureSize = ((Size)paramContext.get(0));
    }
    paramContext = (List)paramCameraInfo.get(CameraInfo.PROP_TARGET_FPS_RANGES);
    setReadOnly(PROP_PREVIEW_FPS_RANGES, paramContext);
    int i = paramContext.size() - 1;
    int j;
    while (i >= 0)
    {
      paramCameraManager = (Range)paramContext.get(i);
      if ((((Integer)paramCameraManager.getUpper()).intValue() == 30) && (((Integer)paramCameraManager.getLower()).intValue() < 30))
      {
        j = ((Integer)paramCameraManager.getLower()).intValue();
        if ((this.m_DefaultVideoPreviewFpsRange == null) || (j - 20 <= Math.abs(((Integer)this.m_DefaultVideoPreviewFpsRange.getLower()).intValue() - 20))) {
          this.m_DefaultVideoPreviewFpsRange = paramCameraManager;
        }
      }
      i -= 1;
    }
    if (this.m_DefaultVideoPreviewFpsRange != null)
    {
      Log.v(this.TAG, "CameraImpl() - Default video FPS range : ", this.m_DefaultVideoPreviewFpsRange);
      paramContext = (List)get(PROP_FOCUS_MODES);
      if (!paramContext.contains(FocusMode.CONTINUOUS_AF)) {
        break label654;
      }
      this.m_FocusMode = FocusMode.CONTINUOUS_AF;
    }
    for (;;)
    {
      j = ((Integer)paramCameraInfo.get(CameraInfo.PROP_MAX_FACE_COUNT)).intValue();
      if (j <= 0) {
        break label676;
      }
      Log.v(this.TAG, "CameraImpl() - Max face count : ", Integer.valueOf(j));
      i = this.m_FaceLists.length - 1;
      while (i >= 0)
      {
        this.m_FaceLists[i] = new ArrayList(j);
        i -= 1;
      }
      Log.w(this.TAG, "CameraImpl() - No suitable FPS range for video");
      break;
      label654:
      if (paramContext.contains(FocusMode.NORMAL_AF)) {
        this.m_FocusMode = FocusMode.NORMAL_AF;
      }
    }
    label676:
    Log.w(this.TAG, "CameraImpl() - Face detection is unsupported");
    i = this.m_FaceLists.length - 1;
    while (i >= 0)
    {
      this.m_FaceLists[i] = Collections.EMPTY_LIST;
      i -= 1;
    }
    try
    {
      Class.forName("android.hardware.camera2.params&Face").getDeclaredMethod("getIsSmile", new Class[0]);
      setReadOnly(PROP_IS_SMILE_CAPTURE_SUPPORTED, Boolean.valueOf(true));
      Log.v(this.TAG, "CameraImpl() - smile capture is supported");
      setReadOnly(PROP_FOCUS_STEP, Float.valueOf(0.2F));
      return;
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        Log.w(this.TAG, "CameraImpl() - Failed to get smile capture information", paramContext);
        setReadOnly(PROP_IS_SMILE_CAPTURE_SUPPORTED, Boolean.valueOf(false));
      }
    }
  }
  
  private void addPreviewReceivedHandler(EventHandler<CameraCaptureEventArgs> paramEventHandler)
  {
    if (hasHandlers(EVENT_PREVIEW_RECEIVED)) {}
    for (int i = 0;; i = 1)
    {
      super.addHandler(EVENT_PREVIEW_RECEIVED, paramEventHandler);
      if ((i != 0) && (this.m_PreviewRequestBuilder != null) && (this.m_PreviewCallbackSurface != null))
      {
        Log.v(this.TAG, "addPreviewReceivedHandler() - Add preview call-back surface");
        this.m_PreviewRequestBuilder.addTarget(this.m_PreviewCallbackSurface);
        if (get(PROP_PREVIEW_STATE) == OperationState.STARTED) {
          startPreviewRequestDirectly();
        }
      }
      return;
    }
  }
  
  private boolean applyAELock(boolean paramBoolean, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    paramBuilder.set(CaptureRequest.CONTROL_AE_LOCK, Boolean.valueOf(paramBoolean));
    return true;
  }
  
  private boolean applyAERegions(List<Camera.MeteringRect> paramList, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    this.m_TempList.clear();
    List localList = this.m_TempList;
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      MeteringRectangle localMeteringRectangle = createMeteringRectangle((Camera.MeteringRect)paramList.get(i));
      if (localMeteringRectangle != null) {
        localList.add(localMeteringRectangle);
      }
      i -= 1;
    }
    if (localList.isEmpty())
    {
      paramList = new MeteringRectangle[1];
      paramList[0] = new MeteringRectangle(0, 0, 0, 0, 0);
    }
    for (;;)
    {
      paramBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, paramList);
      return true;
      paramList = new MeteringRectangle[localList.size()];
      localList.toArray(paramList);
    }
  }
  
  private boolean applyAfRegions(List<Camera.MeteringRect> paramList, CaptureRequest.Builder paramBuilder)
  {
    switch (-getcom-oneplus-camera-FocusModeSwitchesValues()[((FocusMode)get(PROP_FOCUS_MODE)).ordinal()])
    {
    case 2: 
    case 3: 
    default: 
      return false;
    }
    if (paramBuilder == null) {
      return false;
    }
    this.m_TempList.clear();
    List localList = this.m_TempList;
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      MeteringRectangle localMeteringRectangle = createMeteringRectangle((Camera.MeteringRect)paramList.get(i));
      if (localMeteringRectangle != null) {
        localList.add(localMeteringRectangle);
      }
      i -= 1;
    }
    if (localList.isEmpty())
    {
      paramList = new MeteringRectangle[1];
      paramList[0] = new MeteringRectangle(0, 0, 0, 0, 0);
    }
    for (;;)
    {
      paramBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, paramList);
      return true;
      paramList = new MeteringRectangle[localList.size()];
      localList.toArray(paramList);
    }
  }
  
  private boolean applyAwbLock(boolean paramBoolean, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    paramBuilder.set(CaptureRequest.CONTROL_AWB_LOCK, Boolean.valueOf(paramBoolean));
    return true;
  }
  
  private boolean applyAwbMode(int paramInt, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder != null)
    {
      switch (paramInt)
      {
      default: 
        paramBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(1));
      }
      for (;;)
      {
        paramBuilder.set(CaptureRequest.CONTROL_AWB_MODE, Integer.valueOf(paramInt));
        return true;
        if (this.m_SceneMode == 0) {
          paramBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(1));
        } else {
          paramBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(2));
        }
      }
    }
    return false;
  }
  
  private boolean applyExposureCompensation(float paramFloat, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    int i = Math.round(paramFloat / ((Float)get(PROP_EXPOSURE_COMPENSATION_STEP)).floatValue());
    paramBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf(i));
    return true;
  }
  
  private boolean applyExposureTime(long paramLong, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    if (paramLong == -1L)
    {
      setFlashMode(this.m_FlashMode, paramBuilder);
      paramBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, Long.valueOf(-1L));
      return true;
    }
    paramBuilder.set(CaptureRequest.FLASH_MODE, Integer.valueOf(0));
    paramBuilder.set(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(0));
    long l = paramLong;
    if (((Integer)paramBuilder.get(CaptureRequest.CONTROL_CAPTURE_INTENT)).intValue() == 1)
    {
      l = paramLong;
      if (paramLong > 200000000L)
      {
        Log.v(this.TAG, "applyExposureTime() - Exposure time is ", Long.valueOf(paramLong), ", lower to ", Long.valueOf(200000000L), " nano secs");
        l = 200000000L;
      }
    }
    paramBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, Long.valueOf(l));
    return true;
  }
  
  private boolean applyFaceDetection(boolean paramBoolean, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    if ((!paramBoolean) || (this.m_IsRecordingMode)) {
      paramBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, Integer.valueOf(0));
    }
    for (;;)
    {
      return true;
      paramBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, Integer.valueOf(2));
    }
  }
  
  private boolean applyFocusDistance(float paramFloat, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    if (paramFloat == -1.0F) {
      paramBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, Float.valueOf(-1.0F));
    }
    for (;;)
    {
      return true;
      if (((Range)get(PROP_FOCUS_RANGE)).contains(Float.valueOf(paramFloat))) {
        paramBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, Float.valueOf(paramFloat));
      }
    }
  }
  
  private boolean applyFocusMode(FocusMode paramFocusMode, CaptureRequest.Builder paramBuilder)
  {
    int i;
    switch (-getcom-oneplus-camera-FocusModeSwitchesValues()[paramFocusMode.ordinal()])
    {
    default: 
      Log.e(this.TAG, "applyFocusMode() - Unknown focus mode : " + get(PROP_FOCUS_MODE));
      return false;
    case 2: 
      i = 0;
    }
    while (paramBuilder != null)
    {
      paramBuilder.set(CaptureRequest.CONTROL_AF_MODE, Integer.valueOf(i));
      return true;
      i = 1;
      continue;
      if (this.m_IsRecordingMode) {}
      for (int j = 3;; j = 4)
      {
        i = j;
        if (paramBuilder == null) {
          break;
        }
        paramBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(0));
        i = j;
        break;
      }
      i = 0;
    }
    return false;
  }
  
  private boolean applyISO(int paramInt, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    if (paramInt == -1)
    {
      setFlashMode(this.m_FlashMode, paramBuilder);
      paramBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, Integer.valueOf(-1));
    }
    for (;;)
    {
      return true;
      paramBuilder.set(CaptureRequest.FLASH_MODE, Integer.valueOf(0));
      paramBuilder.set(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(0));
      paramBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, Integer.valueOf(paramInt));
    }
  }
  
  private boolean applyPreviewFpsRange(Range<Integer> paramRange, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    if (paramRange != null) {
      paramBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, paramRange);
    }
    for (;;)
    {
      return true;
      if (!this.m_IsRecordingMode)
      {
        if (this.m_DefaultPhotoPreviewFpsRange != null) {
          paramBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, this.m_DefaultPhotoPreviewFpsRange);
        } else {
          Log.e(this.TAG, "applyPreviewFpsRange() - No default photo preview FPS range");
        }
      }
      else if (this.m_DefaultVideoPreviewFpsRange != null) {
        paramBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, this.m_DefaultVideoPreviewFpsRange);
      } else {
        Log.e(this.TAG, "applyPreviewFpsRange() - No default video preview FPS range");
      }
    }
  }
  
  private boolean applyScalerCropRegion(float paramFloat, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder == null) {
      return false;
    }
    paramBuilder.set(CaptureRequest.SCALER_CROP_REGION, mappingZoomToScalerRegion(paramFloat));
    return true;
  }
  
  private boolean applySceneMode(int paramInt, CaptureRequest.Builder paramBuilder)
  {
    if (paramBuilder != null)
    {
      if (paramInt == 0) {
        paramBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(1));
      }
      for (;;)
      {
        paramBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, Integer.valueOf(paramInt));
        return true;
        paramBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(2));
      }
    }
    return false;
  }
  
  private boolean applyToPreview()
  {
    if (get(PROP_PREVIEW_STATE) == OperationState.STARTED)
    {
      if ((get(PROP_CAPTURE_STATE) == OperationState.STARTED) && (this.m_TargetCapturedFrameCount != 1))
      {
        Log.w(this.TAG, "applyToPreview() - Capturing burst photos");
        return false;
      }
      if (!startPreviewRequestDirectly()) {
        Log.e(this.TAG, "applyToPreview() - Fail to apply new request to preview");
      }
    }
    return true;
  }
  
  private boolean captureInternal()
  {
    return captureInternal(0);
  }
  
  /* Error */
  private boolean captureInternal(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getstatic 866	com/oneplus/camera/CameraImpl:PROP_CAPTURE_STATE	Lcom/oneplus/base/PropertyKey;
    //   4: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   7: getstatic 311	com/oneplus/camera/OperationState:STARTING	Lcom/oneplus/camera/OperationState;
    //   10: if_acmpeq +38 -> 48
    //   13: aload_0
    //   14: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   17: new 514	java/lang/StringBuilder
    //   20: dup
    //   21: invokespecial 515	java/lang/StringBuilder:<init>	()V
    //   24: ldc_w 876
    //   27: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   30: aload_0
    //   31: getstatic 866	com/oneplus/camera/CameraImpl:PROP_CAPTURE_STATE	Lcom/oneplus/base/PropertyKey;
    //   34: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   37: invokevirtual 825	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   40: invokevirtual 525	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   43: invokestatic 828	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   46: iconst_0
    //   47: ireturn
    //   48: aload_0
    //   49: getstatic 686	com/oneplus/camera/CameraImpl:PROP_PREVIEW_STATE	Lcom/oneplus/base/PropertyKey;
    //   52: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   55: getstatic 308	com/oneplus/camera/OperationState:STARTED	Lcom/oneplus/camera/OperationState;
    //   58: if_acmpeq +38 -> 96
    //   61: aload_0
    //   62: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   65: new 514	java/lang/StringBuilder
    //   68: dup
    //   69: invokespecial 515	java/lang/StringBuilder:<init>	()V
    //   72: ldc_w 878
    //   75: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: aload_0
    //   79: getstatic 686	com/oneplus/camera/CameraImpl:PROP_PREVIEW_STATE	Lcom/oneplus/base/PropertyKey;
    //   82: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   85: invokevirtual 825	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   88: invokevirtual 525	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokestatic 828	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   94: iconst_0
    //   95: ireturn
    //   96: aload_0
    //   97: invokespecial 881	com/oneplus/camera/CameraImpl:isRawCaptureNeeded	()Z
    //   100: ifeq +23 -> 123
    //   103: aload_0
    //   104: getfield 868	com/oneplus/camera/CameraImpl:m_TargetCapturedFrameCount	I
    //   107: iconst_1
    //   108: if_icmpne +15 -> 123
    //   111: aload_0
    //   112: getstatic 884	com/oneplus/camera/CameraImpl:PROP_IS_CAPTURING_RAW_PHOTO	Lcom/oneplus/base/PropertyKey;
    //   115: iconst_1
    //   116: invokestatic 639	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   119: invokevirtual 565	com/oneplus/camera/CameraImpl:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   122: pop
    //   123: aload_0
    //   124: getstatic 887	com/oneplus/camera/CameraImpl:PROP_HARDWARE_LEVEL	Lcom/oneplus/base/PropertyKey;
    //   127: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   130: checkcast 889	com/oneplus/camera/Camera$HardwareLevel
    //   133: getstatic 893	com/oneplus/camera/Camera$HardwareLevel:LEGACY	Lcom/oneplus/camera/Camera$HardwareLevel;
    //   136: if_acmpeq +18 -> 154
    //   139: aload_0
    //   140: getfield 868	com/oneplus/camera/CameraImpl:m_TargetCapturedFrameCount	I
    //   143: iconst_1
    //   144: if_icmpne +10 -> 154
    //   147: aload_0
    //   148: getfield 895	com/oneplus/camera/CameraImpl:m_IsPreCaptureFlashTriggered	Z
    //   151: ifeq +417 -> 568
    //   154: aload_0
    //   155: getfield 895	com/oneplus/camera/CameraImpl:m_IsPreCaptureFlashTriggered	Z
    //   158: ifeq +561 -> 719
    //   161: iconst_0
    //   162: istore_2
    //   163: aload_0
    //   164: iconst_0
    //   165: putfield 897	com/oneplus/camera/CameraImpl:m_IsPreviewStoppedForCapture	Z
    //   168: aload_0
    //   169: getfield 442	com/oneplus/camera/CameraImpl:m_ExposureTime	J
    //   172: ldc2_w 439
    //   175: lcmp
    //   176: ifne +11 -> 187
    //   179: aload_0
    //   180: getfield 457	com/oneplus/camera/CameraImpl:m_ISOValue	I
    //   183: iconst_m1
    //   184: if_icmpeq +540 -> 724
    //   187: aload_0
    //   188: iconst_1
    //   189: putfield 897	com/oneplus/camera/CameraImpl:m_IsPreviewStoppedForCapture	Z
    //   192: iconst_0
    //   193: istore_2
    //   194: aconst_null
    //   195: astore 7
    //   197: aconst_null
    //   198: astore 8
    //   200: aconst_null
    //   201: astore 6
    //   203: aload_0
    //   204: getfield 899	com/oneplus/camera/CameraImpl:m_VideoSurface	Landroid/view/Surface;
    //   207: ifnull +530 -> 737
    //   210: aload_0
    //   211: getfield 805	com/oneplus/camera/CameraImpl:m_IsRecordingMode	Z
    //   214: ifeq +523 -> 737
    //   217: aload 8
    //   219: astore 6
    //   221: aload_0
    //   222: getfield 901	com/oneplus/camera/CameraImpl:m_Device	Landroid/hardware/camera2/CameraDevice;
    //   225: iconst_4
    //   226: invokevirtual 907	android/hardware/camera2/CameraDevice:createCaptureRequest	(I)Landroid/hardware/camera2/CaptureRequest$Builder;
    //   229: astore 7
    //   231: aload 7
    //   233: astore 6
    //   235: aload_0
    //   236: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   239: ldc_w 909
    //   242: invokestatic 643	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   245: aload 7
    //   247: astore 6
    //   249: aload 6
    //   251: astore 8
    //   253: aload 6
    //   255: ifnonnull +23 -> 278
    //   258: aload_0
    //   259: getfield 901	com/oneplus/camera/CameraImpl:m_Device	Landroid/hardware/camera2/CameraDevice;
    //   262: iconst_2
    //   263: invokevirtual 907	android/hardware/camera2/CameraDevice:createCaptureRequest	(I)Landroid/hardware/camera2/CaptureRequest$Builder;
    //   266: astore 8
    //   268: aload_0
    //   269: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   272: ldc_w 911
    //   275: invokestatic 643	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   278: aload_0
    //   279: getfield 897	com/oneplus/camera/CameraImpl:m_IsPreviewStoppedForCapture	Z
    //   282: ifne +12 -> 294
    //   285: aload 8
    //   287: aload_0
    //   288: getfield 913	com/oneplus/camera/CameraImpl:m_PreviewSurface	Landroid/view/Surface;
    //   291: invokevirtual 683	android/hardware/camera2/CaptureRequest$Builder:addTarget	(Landroid/view/Surface;)V
    //   294: aload 8
    //   296: aload_0
    //   297: getfield 915	com/oneplus/camera/CameraImpl:m_PictureSurface	Landroid/view/Surface;
    //   300: invokevirtual 683	android/hardware/camera2/CaptureRequest$Builder:addTarget	(Landroid/view/Surface;)V
    //   303: aload_0
    //   304: getfield 899	com/oneplus/camera/CameraImpl:m_VideoSurface	Landroid/view/Surface;
    //   307: ifnull +12 -> 319
    //   310: aload 8
    //   312: aload_0
    //   313: getfield 899	com/oneplus/camera/CameraImpl:m_VideoSurface	Landroid/view/Surface;
    //   316: invokevirtual 683	android/hardware/camera2/CaptureRequest$Builder:addTarget	(Landroid/view/Surface;)V
    //   319: aload_0
    //   320: invokespecial 881	com/oneplus/camera/CameraImpl:isRawCaptureNeeded	()Z
    //   323: ifeq +37 -> 360
    //   326: aload_0
    //   327: getfield 868	com/oneplus/camera/CameraImpl:m_TargetCapturedFrameCount	I
    //   330: iconst_1
    //   331: if_icmpne +29 -> 360
    //   334: aload_0
    //   335: getfield 917	com/oneplus/camera/CameraImpl:m_RawSurface	Landroid/view/Surface;
    //   338: ifnull +22 -> 360
    //   341: aload_0
    //   342: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   345: ldc_w 919
    //   348: invokestatic 643	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   351: aload 8
    //   353: aload_0
    //   354: getfield 917	com/oneplus/camera/CameraImpl:m_RawSurface	Landroid/view/Surface;
    //   357: invokevirtual 683	android/hardware/camera2/CaptureRequest$Builder:addTarget	(Landroid/view/Surface;)V
    //   360: aload_0
    //   361: aload 8
    //   363: invokespecial 923	com/oneplus/camera/CameraImpl:prepareCaptureRequestParams	(Landroid/hardware/camera2/CaptureRequest$Builder;)V
    //   366: aload_0
    //   367: getfield 899	com/oneplus/camera/CameraImpl:m_VideoSurface	Landroid/view/Surface;
    //   370: ifnull +18 -> 388
    //   373: aload_0
    //   374: getfield 805	com/oneplus/camera/CameraImpl:m_IsRecordingMode	Z
    //   377: ifeq +11 -> 388
    //   380: aload_0
    //   381: aconst_null
    //   382: aload 8
    //   384: invokespecial 925	com/oneplus/camera/CameraImpl:applyPreviewFpsRange	(Landroid/util/Range;Landroid/hardware/camera2/CaptureRequest$Builder;)Z
    //   387: pop
    //   388: aload_0
    //   389: getfield 868	com/oneplus/camera/CameraImpl:m_TargetCapturedFrameCount	I
    //   392: iconst_1
    //   393: if_icmpeq +15 -> 408
    //   396: aload 8
    //   398: getstatic 862	android/hardware/camera2/CaptureRequest:CONTROL_SCENE_MODE	Landroid/hardware/camera2/CaptureRequest$Key;
    //   401: iconst_0
    //   402: invokestatic 612	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   405: invokevirtual 703	android/hardware/camera2/CaptureRequest$Builder:set	(Landroid/hardware/camera2/CaptureRequest$Key;Ljava/lang/Object;)V
    //   408: aload_0
    //   409: getstatic 928	com/oneplus/camera/CameraImpl:PROP_PICTURE_SIZE	Lcom/oneplus/base/PropertyKey;
    //   412: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   415: checkcast 469	android/util/Size
    //   418: astore 10
    //   420: aload_0
    //   421: getfield 535	com/oneplus/camera/CameraImpl:m_CameraInfo	Lcom/oneplus/camera/CameraInfo;
    //   424: getstatic 931	com/oneplus/camera/CameraInfo:PROP_THUMBNAIL_SIZES	Lcom/oneplus/base/PropertyKey;
    //   427: invokevirtual 558	com/oneplus/camera/CameraInfo:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   430: checkcast 444	java/util/List
    //   433: astore 11
    //   435: aconst_null
    //   436: astore 9
    //   438: aconst_null
    //   439: astore 6
    //   441: iconst_0
    //   442: istore_3
    //   443: aload 9
    //   445: astore 7
    //   447: aload 10
    //   449: ifnull +360 -> 809
    //   452: aload 9
    //   454: astore 7
    //   456: aload 11
    //   458: ifnull +351 -> 809
    //   461: aload 10
    //   463: invokestatic 936	com/oneplus/util/AspectRatio:get	(Landroid/util/Size;)Lcom/oneplus/util/AspectRatio;
    //   466: astore 12
    //   468: aload 11
    //   470: invokeinterface 568 1 0
    //   475: iconst_1
    //   476: isub
    //   477: istore_2
    //   478: aload 6
    //   480: astore 7
    //   482: iload_2
    //   483: iflt +326 -> 809
    //   486: aload 11
    //   488: iload_2
    //   489: invokeinterface 552 2 0
    //   494: checkcast 469	android/util/Size
    //   497: astore 9
    //   499: aload 9
    //   501: invokevirtual 939	android/util/Size:getWidth	()I
    //   504: aload 9
    //   506: invokevirtual 942	android/util/Size:getHeight	()I
    //   509: imul
    //   510: istore 5
    //   512: aload 9
    //   514: invokestatic 936	com/oneplus/util/AspectRatio:get	(Landroid/util/Size;)Lcom/oneplus/util/AspectRatio;
    //   517: astore 13
    //   519: iload_3
    //   520: istore 4
    //   522: aload 6
    //   524: astore 7
    //   526: aload 13
    //   528: aload 12
    //   530: if_acmpne +24 -> 554
    //   533: iload_3
    //   534: istore 4
    //   536: aload 6
    //   538: astore 7
    //   540: iload 5
    //   542: iload_3
    //   543: if_icmple +11 -> 554
    //   546: aload 9
    //   548: astore 7
    //   550: iload 5
    //   552: istore 4
    //   554: iload_2
    //   555: iconst_1
    //   556: isub
    //   557: istore_2
    //   558: iload 4
    //   560: istore_3
    //   561: aload 7
    //   563: astore 6
    //   565: goto -87 -> 478
    //   568: aload_0
    //   569: getfield 448	com/oneplus/camera/CameraImpl:m_FlashMode	Lcom/oneplus/camera/FlashMode;
    //   572: getstatic 248	com/oneplus/camera/FlashMode:ON	Lcom/oneplus/camera/FlashMode;
    //   575: if_acmpeq +49 -> 624
    //   578: aload_0
    //   579: getfield 448	com/oneplus/camera/CameraImpl:m_FlashMode	Lcom/oneplus/camera/FlashMode;
    //   582: getstatic 241	com/oneplus/camera/FlashMode:AUTO	Lcom/oneplus/camera/FlashMode;
    //   585: if_acmpne -431 -> 154
    //   588: aload_0
    //   589: getstatic 945	com/oneplus/camera/CameraImpl:PROP_AE_STATE	Lcom/oneplus/base/PropertyKey;
    //   592: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   595: getstatic 950	com/oneplus/camera/AutoExposureState:FLASH_REQUIRED	Lcom/oneplus/camera/AutoExposureState;
    //   598: if_acmpeq +26 -> 624
    //   601: aload_0
    //   602: getstatic 945	com/oneplus/camera/CameraImpl:PROP_AE_STATE	Lcom/oneplus/base/PropertyKey;
    //   605: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   608: getstatic 953	com/oneplus/camera/AutoExposureState:SEARCHING	Lcom/oneplus/camera/AutoExposureState;
    //   611: if_acmpne -457 -> 154
    //   614: aload_0
    //   615: getfield 955	com/oneplus/camera/CameraImpl:m_PreviousAeState	Lcom/oneplus/camera/AutoExposureState;
    //   618: getstatic 950	com/oneplus/camera/AutoExposureState:FLASH_REQUIRED	Lcom/oneplus/camera/AutoExposureState;
    //   621: if_acmpne -467 -> 154
    //   624: aload_0
    //   625: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   628: ldc_w 957
    //   631: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   634: aload_0
    //   635: getfield 901	com/oneplus/camera/CameraImpl:m_Device	Landroid/hardware/camera2/CameraDevice;
    //   638: iconst_5
    //   639: invokevirtual 907	android/hardware/camera2/CameraDevice:createCaptureRequest	(I)Landroid/hardware/camera2/CaptureRequest$Builder;
    //   642: astore 6
    //   644: aload 6
    //   646: aload_0
    //   647: getfield 913	com/oneplus/camera/CameraImpl:m_PreviewSurface	Landroid/view/Surface;
    //   650: invokevirtual 683	android/hardware/camera2/CaptureRequest$Builder:addTarget	(Landroid/view/Surface;)V
    //   653: aload_0
    //   654: aload 6
    //   656: invokespecial 923	com/oneplus/camera/CameraImpl:prepareCaptureRequestParams	(Landroid/hardware/camera2/CaptureRequest$Builder;)V
    //   659: aload 6
    //   661: getstatic 960	android/hardware/camera2/CaptureRequest:CONTROL_AE_PRECAPTURE_TRIGGER	Landroid/hardware/camera2/CaptureRequest$Key;
    //   664: iconst_1
    //   665: invokestatic 612	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   668: invokevirtual 703	android/hardware/camera2/CaptureRequest$Builder:set	(Landroid/hardware/camera2/CaptureRequest$Key;Ljava/lang/Object;)V
    //   671: aload_0
    //   672: sipush 10030
    //   675: ldc2_w 68
    //   678: invokestatic 966	com/oneplus/base/HandlerUtils:sendMessage	(Lcom/oneplus/base/HandlerObject;IJ)Z
    //   681: pop
    //   682: aload_0
    //   683: getfield 968	com/oneplus/camera/CameraImpl:m_CaptureSession	Landroid/hardware/camera2/CameraCaptureSession;
    //   686: aload 6
    //   688: invokevirtual 972	android/hardware/camera2/CaptureRequest$Builder:build	()Landroid/hardware/camera2/CaptureRequest;
    //   691: new 24	com/oneplus/camera/CameraImpl$9
    //   694: dup
    //   695: aload_0
    //   696: invokespecial 973	com/oneplus/camera/CameraImpl$9:<init>	(Lcom/oneplus/camera/CameraImpl;)V
    //   699: aload_0
    //   700: invokevirtual 977	com/oneplus/camera/CameraImpl:getHandler	()Landroid/os/Handler;
    //   703: invokevirtual 983	android/hardware/camera2/CameraCaptureSession:capture	(Landroid/hardware/camera2/CaptureRequest;Landroid/hardware/camera2/CameraCaptureSession$CaptureCallback;Landroid/os/Handler;)I
    //   706: pop
    //   707: iconst_1
    //   708: ireturn
    //   709: astore 6
    //   711: aload 6
    //   713: invokevirtual 986	android/hardware/camera2/CameraAccessException:printStackTrace	()V
    //   716: goto -562 -> 154
    //   719: iconst_1
    //   720: istore_2
    //   721: goto -558 -> 163
    //   724: aload_0
    //   725: getfield 450	com/oneplus/camera/CameraImpl:m_FocusMode	Lcom/oneplus/camera/FocusMode;
    //   728: getstatic 269	com/oneplus/camera/FocusMode:MANUAL	Lcom/oneplus/camera/FocusMode;
    //   731: if_acmpne -537 -> 194
    //   734: goto -547 -> 187
    //   737: iload_2
    //   738: ifeq -489 -> 249
    //   741: aload 7
    //   743: astore 6
    //   745: aload_0
    //   746: getfield 901	com/oneplus/camera/CameraImpl:m_Device	Landroid/hardware/camera2/CameraDevice;
    //   749: iconst_5
    //   750: invokevirtual 907	android/hardware/camera2/CameraDevice:createCaptureRequest	(I)Landroid/hardware/camera2/CaptureRequest$Builder;
    //   753: astore 7
    //   755: aload 7
    //   757: astore 6
    //   759: aload_0
    //   760: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   763: ldc_w 988
    //   766: invokestatic 643	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   769: aload 7
    //   771: astore 6
    //   773: goto -524 -> 249
    //   776: astore 7
    //   778: goto -529 -> 249
    //   781: astore 6
    //   783: aload_0
    //   784: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   787: ldc_w 990
    //   790: aload 6
    //   792: invokestatic 992	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   795: aload_0
    //   796: getstatic 884	com/oneplus/camera/CameraImpl:PROP_IS_CAPTURING_RAW_PHOTO	Lcom/oneplus/base/PropertyKey;
    //   799: iconst_0
    //   800: invokestatic 639	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   803: invokevirtual 565	com/oneplus/camera/CameraImpl:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   806: pop
    //   807: iconst_0
    //   808: ireturn
    //   809: aload 7
    //   811: ifnull +181 -> 992
    //   814: aload_0
    //   815: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   818: ldc_w 994
    //   821: aload 7
    //   823: invokestatic 596	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   826: aload 8
    //   828: getstatic 997	android/hardware/camera2/CaptureRequest:JPEG_THUMBNAIL_SIZE	Landroid/hardware/camera2/CaptureRequest$Key;
    //   831: aload 7
    //   833: invokevirtual 703	android/hardware/camera2/CaptureRequest$Builder:set	(Landroid/hardware/camera2/CaptureRequest$Key;Ljava/lang/Object;)V
    //   836: aload_0
    //   837: getfield 868	com/oneplus/camera/CameraImpl:m_TargetCapturedFrameCount	I
    //   840: iconst_1
    //   841: if_icmpne +260 -> 1101
    //   844: aload_0
    //   845: getfield 459	com/oneplus/camera/CameraImpl:m_JpegQuality	I
    //   848: ifge +234 -> 1082
    //   851: aload_0
    //   852: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   855: ldc_w 999
    //   858: invokestatic 643	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   861: aload_0
    //   862: getstatic 1002	com/oneplus/camera/CameraImpl:PROP_PICTURE_ROTATION	Lcom/oneplus/base/PropertyKey;
    //   865: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   868: checkcast 1004	com/oneplus/base/Rotation
    //   871: invokevirtual 1007	com/oneplus/base/Rotation:getDeviceOrientation	()I
    //   874: istore_3
    //   875: iload_3
    //   876: istore_2
    //   877: aload_0
    //   878: getstatic 1010	com/oneplus/camera/CameraImpl:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   881: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   884: getstatic 1016	com/oneplus/camera/Camera$LensFacing:FRONT	Lcom/oneplus/camera/Camera$LensFacing;
    //   887: if_acmpne +6 -> 893
    //   890: iload_3
    //   891: ineg
    //   892: istore_2
    //   893: aload 8
    //   895: getstatic 1019	android/hardware/camera2/CaptureRequest:JPEG_ORIENTATION	Landroid/hardware/camera2/CaptureRequest$Key;
    //   898: aload_0
    //   899: getstatic 1022	com/oneplus/camera/CameraImpl:PROP_SENSOR_ORIENTATION	Lcom/oneplus/base/PropertyKey;
    //   902: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   905: checkcast 576	java/lang/Integer
    //   908: invokevirtual 579	java/lang/Integer:intValue	()I
    //   911: iload_2
    //   912: iadd
    //   913: sipush 360
    //   916: iadd
    //   917: sipush 360
    //   920: irem
    //   921: invokestatic 612	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   924: invokevirtual 703	android/hardware/camera2/CaptureRequest$Builder:set	(Landroid/hardware/camera2/CaptureRequest$Key;Ljava/lang/Object;)V
    //   927: aload_0
    //   928: getstatic 1025	com/oneplus/camera/CameraImpl:PROP_LOCATION	Lcom/oneplus/base/PropertyKey;
    //   931: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   934: checkcast 1027	android/location/Location
    //   937: astore 6
    //   939: aload 6
    //   941: ifnull +13 -> 954
    //   944: aload 8
    //   946: getstatic 1030	android/hardware/camera2/CaptureRequest:JPEG_GPS_LOCATION	Landroid/hardware/camera2/CaptureRequest$Key;
    //   949: aload 6
    //   951: invokevirtual 703	android/hardware/camera2/CaptureRequest$Builder:set	(Landroid/hardware/camera2/CaptureRequest$Key;Ljava/lang/Object;)V
    //   954: aload_0
    //   955: aload 8
    //   957: invokevirtual 972	android/hardware/camera2/CaptureRequest$Builder:build	()Landroid/hardware/camera2/CaptureRequest;
    //   960: putfield 1032	com/oneplus/camera/CameraImpl:m_PictureCaptureRequest	Landroid/hardware/camera2/CaptureRequest;
    //   963: aload_0
    //   964: getfield 897	com/oneplus/camera/CameraImpl:m_IsPreviewStoppedForCapture	Z
    //   967: ifeq +150 -> 1117
    //   970: iload_1
    //   971: iconst_1
    //   972: iand
    //   973: ifne +144 -> 1117
    //   976: aload_0
    //   977: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   980: ldc_w 1034
    //   983: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   986: aload_0
    //   987: invokespecial 1037	com/oneplus/camera/CameraImpl:restartCaptureSession	()V
    //   990: iconst_1
    //   991: ireturn
    //   992: aload 10
    //   994: invokevirtual 939	android/util/Size:getWidth	()I
    //   997: aload 10
    //   999: invokevirtual 942	android/util/Size:getHeight	()I
    //   1002: bipush 100
    //   1004: bipush 100
    //   1006: iconst_1
    //   1007: invokestatic 1043	com/oneplus/util/SizeUtils:getRatioStretchedSize	(IIIIZ)Landroid/util/Size;
    //   1010: astore 7
    //   1012: aload_0
    //   1013: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   1016: new 514	java/lang/StringBuilder
    //   1019: dup
    //   1020: invokespecial 515	java/lang/StringBuilder:<init>	()V
    //   1023: ldc_w 1045
    //   1026: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1029: aload 7
    //   1031: invokevirtual 825	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1034: invokevirtual 525	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1037: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   1040: goto -214 -> 826
    //   1043: astore 6
    //   1045: aload_0
    //   1046: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   1049: ldc_w 1047
    //   1052: aload 6
    //   1054: invokestatic 992	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1057: aload_0
    //   1058: getstatic 866	com/oneplus/camera/CameraImpl:PROP_CAPTURE_STATE	Lcom/oneplus/base/PropertyKey;
    //   1061: getstatic 314	com/oneplus/camera/OperationState:STOPPED	Lcom/oneplus/camera/OperationState;
    //   1064: invokevirtual 565	com/oneplus/camera/CameraImpl:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1067: pop
    //   1068: aload_0
    //   1069: getstatic 884	com/oneplus/camera/CameraImpl:PROP_IS_CAPTURING_RAW_PHOTO	Lcom/oneplus/base/PropertyKey;
    //   1072: iconst_0
    //   1073: invokestatic 639	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1076: invokevirtual 565	com/oneplus/camera/CameraImpl:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1079: pop
    //   1080: iconst_0
    //   1081: ireturn
    //   1082: aload 8
    //   1084: getstatic 1050	android/hardware/camera2/CaptureRequest:JPEG_QUALITY	Landroid/hardware/camera2/CaptureRequest$Key;
    //   1087: aload_0
    //   1088: getfield 459	com/oneplus/camera/CameraImpl:m_JpegQuality	I
    //   1091: i2b
    //   1092: invokestatic 1055	java/lang/Byte:valueOf	(B)Ljava/lang/Byte;
    //   1095: invokevirtual 703	android/hardware/camera2/CaptureRequest$Builder:set	(Landroid/hardware/camera2/CaptureRequest$Key;Ljava/lang/Object;)V
    //   1098: goto -237 -> 861
    //   1101: aload 8
    //   1103: getstatic 1050	android/hardware/camera2/CaptureRequest:JPEG_QUALITY	Landroid/hardware/camera2/CaptureRequest$Key;
    //   1106: bipush 90
    //   1108: invokestatic 1055	java/lang/Byte:valueOf	(B)Ljava/lang/Byte;
    //   1111: invokevirtual 703	android/hardware/camera2/CaptureRequest$Builder:set	(Landroid/hardware/camera2/CaptureRequest$Key;Ljava/lang/Object;)V
    //   1114: goto -253 -> 861
    //   1117: aload_0
    //   1118: getfield 868	com/oneplus/camera/CameraImpl:m_TargetCapturedFrameCount	I
    //   1121: iconst_1
    //   1122: if_icmpne +36 -> 1158
    //   1125: aload_0
    //   1126: getfield 968	com/oneplus/camera/CameraImpl:m_CaptureSession	Landroid/hardware/camera2/CameraCaptureSession;
    //   1129: aload_0
    //   1130: getfield 1032	com/oneplus/camera/CameraImpl:m_PictureCaptureRequest	Landroid/hardware/camera2/CaptureRequest;
    //   1133: aload_0
    //   1134: getfield 467	com/oneplus/camera/CameraImpl:m_PictureCaptureCallback	Landroid/hardware/camera2/CameraCaptureSession$CaptureCallback;
    //   1137: aload_0
    //   1138: invokevirtual 977	com/oneplus/camera/CameraImpl:getHandler	()Landroid/os/Handler;
    //   1141: invokevirtual 983	android/hardware/camera2/CameraCaptureSession:capture	(Landroid/hardware/camera2/CaptureRequest;Landroid/hardware/camera2/CameraCaptureSession$CaptureCallback;Landroid/os/Handler;)I
    //   1144: pop
    //   1145: aload_0
    //   1146: getstatic 866	com/oneplus/camera/CameraImpl:PROP_CAPTURE_STATE	Lcom/oneplus/base/PropertyKey;
    //   1149: getstatic 308	com/oneplus/camera/OperationState:STARTED	Lcom/oneplus/camera/OperationState;
    //   1152: invokevirtual 565	com/oneplus/camera/CameraImpl:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1155: pop
    //   1156: iconst_1
    //   1157: ireturn
    //   1158: aload_0
    //   1159: getfield 968	com/oneplus/camera/CameraImpl:m_CaptureSession	Landroid/hardware/camera2/CameraCaptureSession;
    //   1162: aload_0
    //   1163: getfield 1032	com/oneplus/camera/CameraImpl:m_PictureCaptureRequest	Landroid/hardware/camera2/CaptureRequest;
    //   1166: aload_0
    //   1167: getfield 467	com/oneplus/camera/CameraImpl:m_PictureCaptureCallback	Landroid/hardware/camera2/CameraCaptureSession$CaptureCallback;
    //   1170: aload_0
    //   1171: invokevirtual 977	com/oneplus/camera/CameraImpl:getHandler	()Landroid/os/Handler;
    //   1174: invokevirtual 1058	android/hardware/camera2/CameraCaptureSession:setRepeatingRequest	(Landroid/hardware/camera2/CaptureRequest;Landroid/hardware/camera2/CameraCaptureSession$CaptureCallback;Landroid/os/Handler;)I
    //   1177: pop
    //   1178: goto -33 -> 1145
    //   1181: astore 6
    //   1183: aload_0
    //   1184: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   1187: ldc_w 1060
    //   1190: aload 6
    //   1192: invokestatic 992	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1195: aload_0
    //   1196: getstatic 866	com/oneplus/camera/CameraImpl:PROP_CAPTURE_STATE	Lcom/oneplus/base/PropertyKey;
    //   1199: getstatic 314	com/oneplus/camera/OperationState:STOPPED	Lcom/oneplus/camera/OperationState;
    //   1202: invokevirtual 565	com/oneplus/camera/CameraImpl:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1205: pop
    //   1206: aload_0
    //   1207: getstatic 884	com/oneplus/camera/CameraImpl:PROP_IS_CAPTURING_RAW_PHOTO	Lcom/oneplus/base/PropertyKey;
    //   1210: iconst_0
    //   1211: invokestatic 639	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1214: invokevirtual 565	com/oneplus/camera/CameraImpl:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   1217: pop
    //   1218: iconst_0
    //   1219: ireturn
    //   1220: astore 7
    //   1222: goto -973 -> 249
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1225	0	this	CameraImpl
    //   0	1225	1	paramInt	int
    //   162	751	2	i	int
    //   442	449	3	j	int
    //   520	39	4	k	int
    //   510	41	5	m	int
    //   201	486	6	localObject1	Object
    //   709	3	6	localCameraAccessException	android.hardware.camera2.CameraAccessException
    //   743	29	6	localObject2	Object
    //   781	10	6	localThrowable1	Throwable
    //   937	13	6	localLocation	android.location.Location
    //   1043	10	6	localThrowable2	Throwable
    //   1181	10	6	localThrowable3	Throwable
    //   195	575	7	localObject3	Object
    //   776	56	7	localThrowable4	Throwable
    //   1010	20	7	localSize1	Size
    //   1220	1	7	localThrowable5	Throwable
    //   198	904	8	localObject4	Object
    //   436	111	9	localSize2	Size
    //   418	580	10	localSize3	Size
    //   433	54	11	localList	List
    //   466	63	12	localAspectRatio1	com.oneplus.util.AspectRatio
    //   517	10	13	localAspectRatio2	com.oneplus.util.AspectRatio
    // Exception table:
    //   from	to	target	type
    //   634	707	709	android/hardware/camera2/CameraAccessException
    //   745	755	776	java/lang/Throwable
    //   759	769	776	java/lang/Throwable
    //   258	278	781	java/lang/Throwable
    //   278	294	1043	java/lang/Throwable
    //   294	319	1043	java/lang/Throwable
    //   319	360	1043	java/lang/Throwable
    //   360	388	1043	java/lang/Throwable
    //   388	408	1043	java/lang/Throwable
    //   408	435	1043	java/lang/Throwable
    //   461	478	1043	java/lang/Throwable
    //   486	519	1043	java/lang/Throwable
    //   814	826	1043	java/lang/Throwable
    //   826	861	1043	java/lang/Throwable
    //   861	875	1043	java/lang/Throwable
    //   877	890	1043	java/lang/Throwable
    //   893	939	1043	java/lang/Throwable
    //   944	954	1043	java/lang/Throwable
    //   954	963	1043	java/lang/Throwable
    //   992	1040	1043	java/lang/Throwable
    //   1082	1098	1043	java/lang/Throwable
    //   1101	1114	1043	java/lang/Throwable
    //   963	970	1181	java/lang/Throwable
    //   976	990	1181	java/lang/Throwable
    //   1117	1145	1181	java/lang/Throwable
    //   1158	1178	1181	java/lang/Throwable
    //   221	231	1220	java/lang/Throwable
    //   235	245	1220	java/lang/Throwable
  }
  
  private Camera.State changeState(Camera.State paramState)
  {
    Camera.State localState = this.m_State;
    if (localState != paramState)
    {
      this.m_State = paramState;
      notifyPropertyChanged(PROP_STATE, localState, paramState);
    }
    return this.m_State;
  }
  
  private void close(CameraDevice paramCameraDevice)
  {
    if (paramCameraDevice != null) {}
    try
    {
      Log.w(this.TAG, "close() - Close '" + this.m_Id + "' [start]");
      paramCameraDevice.close();
      return;
    }
    catch (Throwable paramCameraDevice)
    {
      Log.e(this.TAG, "close() - Fail to close '" + this.m_Id + "'", paramCameraDevice);
      return;
    }
    finally
    {
      Log.w(this.TAG, "close() - Close '" + this.m_Id + "' [end]");
    }
  }
  
  private void closeInternal()
  {
    if (this.m_Device != null)
    {
      close(this.m_Device);
      this.m_Device = null;
      CAMERA_SEMAPHORE.release();
    }
    changeState(Camera.State.CLOSED);
  }
  
  private ImagePlane[] copyImage(Image paramImage)
  {
    return copyImage(paramImage, null);
  }
  
  private ImagePlane[] copyImage(Image paramImage, byte[][] paramArrayOfByte)
  {
    if (paramImage == null)
    {
      Log.e(this.TAG, "copyImage() - No image");
      return new ImagePlane[0];
    }
    for (;;)
    {
      ImagePlane[] arrayOfImagePlane;
      int i;
      try
      {
        Image.Plane[] arrayOfPlane = paramImage.getPlanes();
        arrayOfImagePlane = new ImagePlane[arrayOfPlane.length];
        i = arrayOfImagePlane.length - 1;
        if (i >= 0)
        {
          if ((paramArrayOfByte == null) || (paramArrayOfByte.length < i)) {
            break label165;
          }
          paramImage = paramArrayOfByte[i];
          Image.Plane localPlane = arrayOfPlane[i];
          ByteBuffer localByteBuffer = localPlane.getBuffer();
          if ((paramImage != null) && (paramImage.length >= localByteBuffer.capacity()))
          {
            localByteBuffer.get(paramImage, 0, localByteBuffer.capacity());
            arrayOfImagePlane[i] = new ImagePlane(paramImage, localPlane.getPixelStride(), localPlane.getRowStride());
          }
          else
          {
            arrayOfImagePlane[i] = new ImagePlane(localPlane);
          }
        }
      }
      catch (Throwable paramImage)
      {
        Log.e(this.TAG, "copyImage() - Fail to copy image", paramImage);
        return new ImagePlane[0];
      }
      return arrayOfImagePlane;
      i -= 1;
      continue;
      label165:
      paramImage = null;
    }
  }
  
  private ImagePlane[] copyImageAsNV21(Image paramImage)
  {
    if (paramImage == null)
    {
      Log.e(this.TAG, "copyImageAsNV21() - No image");
      return new ImagePlane[0];
    }
    try
    {
      if (paramImage.getFormat() == 35)
      {
        byte[] arrayOfByte = new byte[paramImage.getWidth() * paramImage.getHeight() * 3 / 2];
        YuvUtils.multiPlaneYuvToNV21(paramImage, arrayOfByte);
        return new ImagePlane[] { new ImagePlane(arrayOfByte, 1, paramImage.getWidth()) };
      }
      Log.e(this.TAG, "copyImageAsNV21() - Unsupported image format : " + paramImage.getFormat());
      return new ImagePlane[0];
    }
    catch (Throwable paramImage)
    {
      Log.e(this.TAG, "copyImageAsNV21() - Fail to copy image", paramImage);
    }
    return new ImagePlane[0];
  }
  
  private MeteringRectangle createMeteringRectangle(Camera.MeteringRect paramMeteringRect)
  {
    if (paramMeteringRect.isIgnorable()) {
      return null;
    }
    Size localSize = (Size)get(PROP_SENSOR_SIZE);
    int i = (int)(paramMeteringRect.getLeft() * localSize.getWidth() + 0.5F);
    int j = (int)(paramMeteringRect.getTop() * localSize.getHeight() + 0.5F);
    return new MeteringRectangle(i, j, (int)(paramMeteringRect.getRight() * localSize.getWidth() + 0.5F) - i, (int)(paramMeteringRect.getBottom() * localSize.getHeight() + 0.5F) - j, (int)(paramMeteringRect.getWeight() * 1000.0F) + 0);
  }
  
  private void dumpCaptureRequest(String paramString, CaptureRequest paramCaptureRequest)
  {
    if (paramCaptureRequest != null)
    {
      Log.v(this.TAG, "dumpCaptureRequest() - >>>>>> Start: ", paramString);
      paramString = paramCaptureRequest.getKeys().iterator();
      if (paramString.hasNext())
      {
        CaptureRequest.Key localKey = (CaptureRequest.Key)paramString.next();
        StringBuilder localStringBuilder = new StringBuilder();
        Object localObject = paramCaptureRequest.get(localKey);
        if (!localObject.getClass().isArray()) {
          localStringBuilder.append(localKey.getName()).append("=").append(localObject);
        }
        for (;;)
        {
          Log.v(this.TAG, "dumpCaptureRequest() - Request: ", localStringBuilder.toString());
          break;
          localStringBuilder.append(localKey.getName()).append("=").append(Arrays.toString((Object[])localObject));
        }
      }
      Log.v(this.TAG, "dumpCaptureRequest() - <<<<<< End");
    }
  }
  
  private CameraCharacteristics getCameraCharacteristic()
  {
    if (this.m_CameraManager == null)
    {
      Log.e(this.TAG, "getCameraCharacteristic() - camera manager is null");
      return null;
    }
    try
    {
      if (this.m_CameraCharacteristics == null) {
        this.m_CameraCharacteristics = this.m_CameraManager.getCameraCharacteristics(this.m_Id);
      }
      return this.m_CameraCharacteristics;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "getCameraCharacteristic() - failed to get camera characteristic", localThrowable);
      }
    }
  }
  
  private boolean isRawCaptureNeeded()
  {
    if ((!((Boolean)get(PROP_IS_RAW_CAPTURE_SUPPORTED)).booleanValue()) || (this.m_IsRecordingMode)) {}
    while ((!this.m_IsRawCaptureEnabled) || (this.m_SceneMode != 0)) {
      return false;
    }
    return true;
  }
  
  private Rect mappingZoomToScalerRegion(float paramFloat)
  {
    Size localSize = (Size)get(Camera.PROP_SENSOR_SIZE);
    Rect localRect = new Rect(0, 0, (int)(localSize.getWidth() / paramFloat), (int)(localSize.getHeight() / paramFloat));
    localRect.offset((localSize.getWidth() - localRect.right) / 2, (localSize.getHeight() - localRect.bottom) / 2);
    return localRect;
  }
  
  private void onAutoFocusStartTimeout()
  {
    if (this.m_IsAutoFocusStarting)
    {
      Log.e(this.TAG, "onAutoFocusStartTimeout()");
      this.m_IsAutoFocusStarting = false;
      setReadOnly(PROP_FOCUS_STATE, FocusState.INACTIVE);
    }
  }
  
  private void onAutoFocusTimeout()
  {
    if (get(PROP_FOCUS_STATE) == FocusState.SCANNING)
    {
      Log.e(this.TAG, "onAutoFocusTimeout()");
      if (this.m_PreviewRequestBuilder != null)
      {
        this.m_PreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(2));
        startPreviewRequestDirectly();
      }
      this.m_IsAutoFocusTimeout = true;
      setReadOnly(PROP_FOCUS_STATE, FocusState.UNFOCUSED);
    }
  }
  
  private void onCaptureCompleted(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, TotalCaptureResult paramTotalCaptureResult, CaptureFailure paramCaptureFailure)
  {
    int i = 0;
    paramCameraCaptureSession = (OperationState)get(PROP_CAPTURE_STATE);
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[paramCameraCaptureSession.ordinal()])
    {
    case 2: 
    case 3: 
    default: 
      Log.e(this.TAG, "onCaptureCompleted() - Capture state is " + paramCameraCaptureSession);
      return;
    }
    if (this.m_CaptureHandle == null)
    {
      Log.e(this.TAG, "onCaptureCompleted() - No capture handle");
      return;
    }
    updatePropertyState(paramTotalCaptureResult);
    this.m_ReceivedCaptureCompletedCount += 1;
    Log.v(this.TAG, "onCaptureCompleted() - Index : ", Integer.valueOf(this.m_ReceivedCaptureCompletedCount - 1));
    if (paramCaptureFailure == null) {
      i = 1;
    }
    if ((i == 0) && (get(PROP_CAPTURE_STATE) != OperationState.STOPPING)) {
      Log.e(this.TAG, "onCaptureCompleted() - Capture failed");
    }
    if ((this.m_TargetCapturedFrameCount > 0) && (this.m_ReceivedCaptureCompletedCount > this.m_TargetCapturedFrameCount))
    {
      Log.w(this.TAG, "onCaptureCompleted() - Unexpected call-back, drop");
      return;
    }
    paramCaptureRequest = null;
    paramCaptureFailure = null;
    if (i != 0)
    {
      if ((ImagePlane[])this.m_ReceivedPictures.peek() == null)
      {
        Log.w(this.TAG, "onCaptureCompleted() - Wait for picture");
        this.m_ReceivedCaptureCompletedResults.add(paramTotalCaptureResult);
        return;
      }
      paramCameraCaptureSession = paramCaptureFailure;
      if (isRawCaptureNeeded())
      {
        paramCameraCaptureSession = paramCaptureFailure;
        if (((Boolean)get(PROP_IS_CAPTURING_RAW_PHOTO)).booleanValue())
        {
          paramCaptureRequest = (ImagePlane[])this.m_ReceivedRawPictures.poll();
          paramCameraCaptureSession = paramCaptureRequest;
          if (paramCaptureRequest == null)
          {
            Log.w(this.TAG, "onCaptureCompleted() - Wait for raw picture");
            this.m_ReceivedCaptureCompletedResults.add(paramTotalCaptureResult);
            return;
          }
        }
      }
      paramCaptureFailure = (ImagePlane[])this.m_ReceivedPictures.poll();
      paramCaptureRequest = paramCameraCaptureSession;
    }
    for (paramCameraCaptureSession = paramCaptureFailure;; paramCameraCaptureSession = null)
    {
      onPictureReceived(paramTotalCaptureResult, paramCameraCaptureSession, paramCaptureRequest);
      return;
    }
  }
  
  private void onCaptureCompleted(boolean paramBoolean)
  {
    Log.w(this.TAG, "onCaptureCompleted()");
    this.m_ReceivedCaptureStartedResults.clear();
    this.m_ReceivedCaptureCompletedResults.clear();
    this.m_ReceivedPictures.clear();
    this.m_ReceivedRawPictures.clear();
    this.m_IsPreCaptureFlashComplete = false;
    this.m_IsPreCaptureFlashTimeout = false;
    this.m_IsPreCaptureFlashTriggered = false;
    this.m_IsCaptureStartedReceived = false;
    this.m_ReceivedCaptureStartedCount = 0;
    this.m_ReceivedCaptureCompletedCount = 0;
    this.m_ReceivedPictureCount = 0;
    this.m_ReceivedRawPictureCount = 0;
    this.m_CaptureHandle = null;
    this.m_TargetCapturedFrameCount = 0;
    this.m_IsCaptureSequenceCompleted = false;
    setReadOnly(PROP_CAPTURE_STATE, OperationState.STOPPED);
    setReadOnly(PROP_IS_CAPTURING_RAW_PHOTO, Boolean.valueOf(false));
    if (get(PROP_PREVIEW_STATE) == OperationState.STARTED)
    {
      if (!this.m_IsPreviewStoppedForCapture) {
        break label195;
      }
      Log.v(this.TAG, "onCaptureCompleted() - Restart capture session");
      restartCaptureSession();
    }
    label195:
    do
    {
      for (;;)
      {
        if (paramBoolean)
        {
          if (this.m_CaptureSessionState != OperationState.STOPPING) {
            break;
          }
          Log.w(this.TAG, "onCaptureCompleted() - Stop capture session");
          this.m_CaptureSessionState = OperationState.STARTED;
          stopCaptureSession(false);
        }
        return;
        if (this.m_TargetCapturedFrameCount != 1)
        {
          Log.v(this.TAG, "onCaptureCompleted() - Restart preview");
          startPreviewRequestDirectly();
        }
      }
    } while (get(PROP_PREVIEW_STATE) != OperationState.STOPPING);
    Log.w(this.TAG, "onCaptureCompleted() - Continue stopping preview");
    stopCaptureSession(false);
  }
  
  private void onCaptureSequenceCompleted()
  {
    Log.v(this.TAG, "onCaptureSequenceCompleted()");
    this.m_IsCaptureSequenceCompleted = true;
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_CAPTURE_STATE)).ordinal()])
    {
    case 2: 
    case 3: 
    default: 
      return;
    case 1: 
      if (!this.m_ReceivedCaptureCompletedResults.isEmpty())
      {
        Log.w(this.TAG, "onCaptureSequenceCompleted() - Wait for picture");
        return;
      }
      break;
    }
    onCaptureCompleted(true);
  }
  
  private void onCaptureSessionClosed(CameraCaptureSession paramCameraCaptureSession)
  {
    if (this.m_CaptureSession != paramCameraCaptureSession)
    {
      Log.e(this.TAG, "onCaptureSessionClosed() - Unknown session : " + paramCameraCaptureSession);
      return;
    }
    Log.w(this.TAG, "onCaptureSessionClosed() - Session : " + paramCameraCaptureSession);
    getHandler().removeMessages(10001);
    getHandler().removeMessages(10020);
    if (this.m_PictureSurface != null)
    {
      this.m_PictureSurface.release();
      this.m_PictureSurface = null;
    }
    if (this.m_PictureReader != null)
    {
      this.m_PictureReader.setOnImageAvailableListener(null, null);
      this.m_PictureReader.close();
      this.m_PictureReader = null;
    }
    if (this.m_RawSurface != null)
    {
      this.m_RawSurface.release();
      this.m_RawSurface = null;
    }
    if (this.m_RawReader != null)
    {
      this.m_RawReader.setOnImageAvailableListener(null, null);
      this.m_RawReader.close();
      this.m_RawReader = null;
    }
    if (!this.m_TempSurfaces.isEmpty())
    {
      int i = this.m_TempSurfaces.size() - 1;
      while (i >= 0)
      {
        ((Surface)this.m_TempSurfaces.get(i)).release();
        i -= 1;
      }
      this.m_TempSurfaces.clear();
    }
    getHandler().removeMessages(10010);
    this.m_PreviewSurface = null;
    this.m_CaptureSession = null;
    this.m_CaptureSessionState = OperationState.STOPPED;
    this.m_IsAutoFocusTimeout = false;
    this.m_IsPreCaptureFlashTriggered = false;
    this.m_PreviewCallbackCount = 0;
    this.m_LastPreviewCbProfileTime = 0L;
    if (this.m_IsPreviewReceived)
    {
      this.m_IsPreviewReceived = false;
      notifyPropertyChanged(PROP_IS_PREVIEW_RECEIVED, Boolean.valueOf(true), Boolean.valueOf(false));
    }
    this.m_PreviewRequestBuilder = null;
    if (this.m_PreviewCallbackSurface != null)
    {
      this.m_PreviewCallbackSurface.release();
      this.m_PreviewCallbackSurface = null;
    }
    if (this.m_PreviewCallbackReader != null)
    {
      this.m_PreviewCallbackReader.setOnImageAvailableListener(null, null);
      this.m_PreviewCallbackReader.close();
      this.m_PreviewCallbackReader = null;
    }
    this.m_PreviewCallbackBuffer = null;
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    }
    for (;;)
    {
      setReadOnly(PROP_PREVIEW_STATE, OperationState.STOPPED);
      if (this.m_State == Camera.State.CLOSING)
      {
        Log.w(this.TAG, "onCaptureSessionClosed() - Close camera");
        closeInternal();
      }
      return;
      Log.w(this.TAG, "onCaptureSessionClosed() - Restart capture session immediately");
      if (startCaptureSession()) {
        return;
      }
      Log.e(this.TAG, "onCaptureSessionClosed() - Fail to restart capture session");
    }
  }
  
  private void onCaptureSessionConfigureFailed(CameraCaptureSession paramCameraCaptureSession)
  {
    if (paramCameraCaptureSession != null) {
      paramCameraCaptureSession.close();
    }
    if (this.m_CaptureSessionState != OperationState.STARTING)
    {
      Log.w(this.TAG, "onCaptureSessionConfigured() - Current session state is " + this.m_CaptureSessionState);
      return;
    }
    Log.e(this.TAG, "onCaptureSessionConfigureFailed()");
    this.m_CaptureSessionState = OperationState.STOPPED;
    if (get(PROP_PREVIEW_STATE) == OperationState.STARTING)
    {
      Log.e(this.TAG, "onCaptureSessionConfigureFailed() - Fail to create capture session, cancel starting preview");
      setReadOnly(PROP_PREVIEW_STATE, OperationState.STOPPED);
    }
    this.m_CaptureSession = paramCameraCaptureSession;
    onCaptureSessionClosed(paramCameraCaptureSession);
  }
  
  private void onCaptureSessionConfigured(CameraCaptureSession paramCameraCaptureSession)
  {
    if (this.m_CaptureSessionState != OperationState.STARTING)
    {
      Log.e(this.TAG, "onCaptureSessionConfigured() - Current session state is " + this.m_CaptureSessionState);
      paramCameraCaptureSession.close();
      if (this.m_CaptureSessionState == OperationState.STOPPING)
      {
        this.m_CaptureSession = paramCameraCaptureSession;
        onCaptureSessionClosed(paramCameraCaptureSession);
      }
      return;
    }
    Log.w(this.TAG, "onCaptureSessionConfigured() - Session : " + paramCameraCaptureSession);
    this.m_CaptureSessionState = OperationState.STARTED;
    this.m_CaptureSession = paramCameraCaptureSession;
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    default: 
      return;
    case 2: 
      startPreviewRequest();
      return;
    }
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_CAPTURE_STATE)).ordinal()])
    {
    default: 
      return;
    case 2: 
      HandlerUtils.post(this, new Runnable()
      {
        public void run()
        {
          CameraImpl.-wrap0(CameraImpl.this, 1);
        }
      }, 100L);
      return;
    }
    startPreviewRequest();
  }
  
  private void onCaptureStarted(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, long paramLong1, long paramLong2)
  {
    paramCameraCaptureSession = (OperationState)get(PROP_CAPTURE_STATE);
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[paramCameraCaptureSession.ordinal()])
    {
    case 2: 
    case 3: 
    default: 
      Log.e(this.TAG, "onCaptureStarted() - Capture state is " + paramCameraCaptureSession);
      return;
    }
    if (this.m_CaptureHandle == null)
    {
      Log.e(this.TAG, "onCaptureStarted() - No capture handle");
      return;
    }
    Log.v(this.TAG, "onCaptureStarted() - Index : ", Integer.valueOf(this.m_ReceivedCaptureStartedCount));
    this.m_ReceivedCaptureStartedCount += 1;
    this.m_IsCaptureStartedReceived = true;
    if ((this.m_TargetCapturedFrameCount > 0) && (this.m_ReceivedCaptureStartedCount > this.m_TargetCapturedFrameCount))
    {
      Log.w(this.TAG, "onCaptureStarted() - Unexpected call-back, drop");
      return;
    }
    raise(EVENT_SHUTTER, CameraCaptureEventArgs.obtain(this.m_CaptureHandle, null, this.m_ReceivedCaptureStartedCount - 1));
  }
  
  private void onDeviceError(CameraDevice paramCameraDevice, int paramInt, boolean paramBoolean)
  {
    if (this.m_State == Camera.State.OPENING)
    {
      long l = SystemClock.elapsedRealtime() - this.m_OpenCameraStartTime;
      CAMERA_SEMAPHORE.release();
      if (l < 4900L)
      {
        Log.w(this.TAG, "onDeviceError() - Retry opening camera, elapsed time : " + l);
        try
        {
          Thread.sleep(100L);
          if (openInternal(0)) {
            return;
          }
        }
        catch (Throwable paramCameraDevice)
        {
          for (;;) {}
        }
      }
      Log.e(this.TAG, "onDeviceError() - Cannot open camera in " + l + "ms");
      if (get(PROP_PREVIEW_STATE) == OperationState.STARTING)
      {
        Log.e(this.TAG, "onDeviceError() - Cancel preview starting");
        setReadOnly(PROP_PREVIEW_STATE, OperationState.STOPPED);
      }
      if (!paramBoolean) {
        break label391;
      }
      Log.e(this.TAG, "onDeviceError() - Camera '" + this.m_Id + "' disconnected");
      raise(EVENT_OPEN_FAILED, new CameraOpenFailedEventArgs(this, -2));
    }
    for (;;)
    {
      if (this.m_State == Camera.State.OPENING) {
        changeState(Camera.State.CLOSED);
      }
      return;
      Log.w(this.TAG, "onDeviceError() - Current state is " + this.m_State);
      raise(EVENT_ERROR, EventArgs.EMPTY);
      close(0);
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue())
      {
        changeState(Camera.State.UNAVAILABLE);
        switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_CAPTURE_STATE)).ordinal()])
        {
        }
      }
      for (;;)
      {
        stopCaptureSession(true);
        return;
        if (this.m_State != Camera.State.CLOSING) {
          break;
        }
        raise(EVENT_OPEN_CANCELLED, EventArgs.EMPTY);
        changeState(Camera.State.CLOSED);
        break;
        Log.e(this.TAG, "onDeviceError() - Stop capture directly");
        onCaptureCompleted(false);
      }
      label391:
      Log.e(this.TAG, "onDeviceError() - Fail to open camera '" + this.m_Id + "', error : " + paramInt);
      raise(EVENT_OPEN_FAILED, new CameraOpenFailedEventArgs(this, paramInt));
    }
  }
  
  private void onDeviceOpened(CameraDevice paramCameraDevice)
  {
    if (this.m_State != Camera.State.OPENING)
    {
      Log.w(this.TAG, "onDeviceOpened() - Current state is " + this.m_State);
      close(paramCameraDevice);
      CAMERA_SEMAPHORE.release();
      if (((Boolean)get(PROP_IS_RELEASED)).booleanValue()) {
        changeState(Camera.State.UNAVAILABLE);
      }
      while (this.m_State != Camera.State.CLOSING) {
        return;
      }
      raise(EVENT_OPEN_CANCELLED, EventArgs.EMPTY);
      changeState(Camera.State.CLOSED);
      return;
    }
    Log.w(this.TAG, "onDeviceOpened() - Camera ID : '" + this.m_Id + "', Device : " + paramCameraDevice);
    this.m_Device = paramCameraDevice;
    changeState(Camera.State.OPENED);
    if ((get(PROP_PREVIEW_STATE) == OperationState.STARTING) && (!startCaptureSession()))
    {
      Log.e(this.TAG, "onDeviceOpened() - Fail to start capture session");
      setReadOnly(PROP_PREVIEW_STATE, OperationState.STOPPED);
    }
  }
  
  private void onFirstPreviewFrameReceived()
  {
    if ((this.m_IsPreviewReceived) || (this.m_IsCaptureSessionClosed)) {
      return;
    }
    Log.v(this.TAG, "onFirstPreviewFrameReceived()");
    getHandler().removeMessages(10001);
    if (this.m_CaptureSessionState == OperationState.STOPPING)
    {
      Log.w(this.TAG, "onFirstPreviewFrameReceived() - Continue stopping capture session");
      this.m_CaptureSessionState = OperationState.STARTED;
      stopCaptureSession(false);
      return;
    }
    OperationState localOperationState = (OperationState)get(PROP_PREVIEW_STATE);
    if (localOperationState != OperationState.STARTED)
    {
      Log.w(this.TAG, "onFirstPreviewFrameReceived() - Preview state is " + localOperationState);
      return;
    }
    this.m_IsPreviewReceived = true;
    notifyPropertyChanged(PROP_IS_PREVIEW_RECEIVED, Boolean.valueOf(false), Boolean.valueOf(true));
  }
  
  private void onPictureReceived(CaptureResult paramCaptureResult, ImagePlane[] paramArrayOfImagePlane1, ImagePlane[] paramArrayOfImagePlane2)
  {
    Object localObject2 = (OperationState)get(PROP_CAPTURE_STATE);
    int j;
    int i;
    label45:
    int m;
    int k;
    label101:
    label124:
    Object localObject1;
    if ((paramArrayOfImagePlane1 == null) || (paramArrayOfImagePlane1.length == 0))
    {
      j = 1;
      if ((this.m_TargetCapturedFrameCount <= 0) || (this.m_ReceivedPictureCount < this.m_TargetCapturedFrameCount)) {
        break label351;
      }
      i = 1;
      m = j;
      k = i;
      if (isRawCaptureNeeded())
      {
        m = j;
        k = i;
        if (((Boolean)get(PROP_IS_CAPTURING_RAW_PHOTO)).booleanValue())
        {
          if ((j == 0) && (paramArrayOfImagePlane2 != null) && (paramArrayOfImagePlane2.length != 0)) {
            break label357;
          }
          j = 1;
          if ((i == 0) || (this.m_ReceivedRawPictureCount < this.m_TargetCapturedFrameCount)) {
            break label363;
          }
          k = 1;
          m = j;
        }
      }
      localObject1 = localObject2;
      if (localObject2 == OperationState.STARTED) {
        if (k == 0)
        {
          localObject1 = localObject2;
          if (m == 0) {}
        }
        else
        {
          if (m == 0) {
            break label373;
          }
          Log.e(this.TAG, "onPictureReceived() - Capture failed, start completing capture");
          label165:
          localObject1 = OperationState.STOPPING;
          stopCaptureInternal(false);
        }
      }
      if (m != 0) {
        break label386;
      }
      i = ((Integer)get(PROP_PICTURE_FORMAT)).intValue();
      localObject2 = AutomaticId.generate(this.TAG);
      Size localSize = (Size)get(PROP_PICTURE_SIZE);
      long l = System.currentTimeMillis();
      raise(EVENT_PICTURE_RECEIVED, CameraCaptureEventArgs.obtain(this.m_CaptureHandle, (String)localObject2, this.m_ReceivedPictureCount - 1, i, localSize, paramArrayOfImagePlane1, paramCaptureResult, l));
      if ((isRawCaptureNeeded()) && (((Boolean)get(PROP_IS_CAPTURING_RAW_PHOTO)).booleanValue()))
      {
        paramArrayOfImagePlane1 = (Size)get(Camera.PROP_SENSOR_SIZE);
        raise(EVENT_RAW_PICTURE_RECEIVED, CameraCaptureEventArgs.obtain(this.m_CaptureHandle, (String)localObject2, this.m_ReceivedRawPictureCount - 1, 32, paramArrayOfImagePlane1, paramArrayOfImagePlane2, paramCaptureResult, l));
      }
    }
    for (;;)
    {
      if (((k != 0) || (m != 0)) && (localObject1 == OperationState.STOPPING) && (this.m_IsCaptureSequenceCompleted)) {
        onCaptureCompleted(true);
      }
      return;
      j = 0;
      break;
      label351:
      i = 0;
      break label45;
      label357:
      j = 0;
      break label101;
      label363:
      k = 0;
      m = j;
      break label124;
      label373:
      Log.w(this.TAG, "onPictureReceived() - Frame count reached, start completing capture");
      break label165;
      label386:
      raise(EVENT_CAPTURE_FAILED, CameraCaptureEventArgs.obtain(this.m_CaptureHandle, null, this.m_ReceivedPictureCount - 1));
    }
  }
  
  private void onPictureReceived(Image paramImage)
  {
    Object localObject = (OperationState)get(PROP_CAPTURE_STATE);
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[localObject.ordinal()])
    {
    case 2: 
    case 3: 
    default: 
      Log.w(this.TAG, "onPictureReceived() - Capture state is " + localObject);
      return;
    }
    if (this.m_CaptureHandle == null)
    {
      Log.e(this.TAG, "onPictureReceived() - No capture handle");
      return;
    }
    if (((Integer)get(PROP_PICTURE_FORMAT)).intValue() != 17) {}
    for (paramImage = copyImage(paramImage);; paramImage = copyImageAsNV21(paramImage))
    {
      this.m_ReceivedPictureCount += 1;
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("{ ");
      int i = 0;
      while (i < paramImage.length)
      {
        if (i > 0) {
          ((StringBuilder)localObject).append(", ");
        }
        ((StringBuilder)localObject).append(paramImage[i].getData().length);
        i += 1;
      }
    }
    ((StringBuilder)localObject).append(" }");
    Log.v(this.TAG, "onPictureReceived() - Index : ", Integer.valueOf(this.m_ReceivedPictureCount - 1), ", picture data size : ", localObject);
    if ((this.m_TargetCapturedFrameCount > 0) && (this.m_ReceivedPictureCount > this.m_TargetCapturedFrameCount))
    {
      Log.w(this.TAG, "onPictureReceived() - Unexpected picture, drop");
      return;
    }
    if ((CaptureResult)this.m_ReceivedCaptureCompletedResults.peek() == null)
    {
      this.m_ReceivedPictures.add(paramImage);
      Log.w(this.TAG, "onPictureReceived() - Received picture before capture completed");
      return;
    }
    ImagePlane[] arrayOfImagePlane = null;
    localObject = arrayOfImagePlane;
    if (isRawCaptureNeeded())
    {
      localObject = arrayOfImagePlane;
      if (((Boolean)get(PROP_IS_CAPTURING_RAW_PHOTO)).booleanValue())
      {
        arrayOfImagePlane = (ImagePlane[])this.m_ReceivedRawPictures.poll();
        localObject = arrayOfImagePlane;
        if (arrayOfImagePlane == null)
        {
          Log.w(this.TAG, "onPictureReceived() - Wait for raw picture");
          this.m_ReceivedPictures.add(paramImage);
          return;
        }
      }
    }
    onPictureReceived((CaptureResult)this.m_ReceivedCaptureCompletedResults.poll(), paramImage, (ImagePlane[])localObject);
  }
  
  private void onPreCaptureFlashComplete()
  {
    if ((this.m_IsPreCaptureFlashTimeout) && (this.m_IsPreCaptureFlashComplete))
    {
      Log.w(this.TAG, "onPreCaptureFlashComplete() - Capture for pre-capture flash timeout");
      this.m_IsPreCaptureFlashTriggered = true;
      captureInternal();
    }
  }
  
  private void onPreviewCaptureCompleted(CaptureResult paramCaptureResult)
  {
    if ((!this.m_IsPreviewReceived) && (this.m_IsRecordingMode) && (this.m_VideoSurface != null)) {
      onFirstPreviewFrameReceived();
    }
    AutoExposureState localAutoExposureState = (AutoExposureState)get(PROP_AE_STATE);
    updatePropertyState(paramCaptureResult);
    int i = 0;
    if (localAutoExposureState != get(PROP_AE_STATE)) {
      i = 1;
    }
    if ((localAutoExposureState == AutoExposureState.PRECAPTURE) && (i != 0) && (get(PROP_CAPTURE_STATE) == OperationState.STARTING))
    {
      Log.w(this.TAG, "onPreviewCaptureCompleted() - Pre-capture flash completed, capture photo");
      HandlerUtils.removeMessages(this, 10030);
      this.m_IsPreCaptureFlashTriggered = true;
      captureInternal();
    }
  }
  
  private void onPreviewFrameReceived()
  {
    boolean bool = hasHandlers(EVENT_PREVIEW_RECEIVED);
    Object localObject3 = null;
    Object localObject4 = null;
    Image localImage;
    Object localObject1;
    if (this.m_PreviewCallbackReader != null)
    {
      localImage = this.m_PreviewCallbackReader.acquireLatestImage();
      localObject1 = localObject4;
      if (bool)
      {
        localObject1 = localObject4;
        if (localImage == null) {}
      }
    }
    for (;;)
    {
      long l1;
      try
      {
        i = localImage.getWidth();
        int j = i * localImage.getHeight() * 3 / 2;
        if ((this.m_PreviewCallbackBuffer == null) || (this.m_PreviewCallbackBuffer.length != j)) {
          this.m_PreviewCallbackBuffer = new byte[j];
        }
        YuvUtils.multiPlaneYuvToNV21(localImage, this.m_PreviewCallbackBuffer);
        localObject1 = new ImagePlane[1];
        localObject1[0] = new ImagePlane(this.m_PreviewCallbackBuffer, 1, i);
        localObject3 = localObject1;
        if (localImage != null)
        {
          localImage.close();
          localObject3 = localObject1;
        }
        if (get(PROP_PREVIEW_STATE) == OperationState.STARTED)
        {
          i = 1;
          if ((!bool) && (this.m_PreviewRequestBuilder != null) && (this.m_PreviewCallbackSurface != null))
          {
            Log.v(this.TAG, "onPreviewFrameReceived() - Remove preview call-back surface");
            this.m_PreviewRequestBuilder.removeTarget(this.m_PreviewCallbackSurface);
            if (i != 0) {
              startPreviewRequestDirectly();
            }
          }
          l1 = SystemClock.elapsedRealtime();
          long l2 = l1 - this.m_LastPreviewCbProfileTime;
          this.m_PreviewCallbackCount += 1;
          if ((this.m_LastPreviewCbProfileTime <= 0L) || (l2 < 2000L)) {
            break label337;
          }
          double d = this.m_PreviewCallbackCount / l2;
          this.m_PreviewCallbackCount = 0;
          this.m_LastPreviewCbProfileTime = l1;
          Log.v(this.TAG, String.format(Locale.US, "onPreviewFrameReceived() - FPS : %.1f", new Object[] { Double.valueOf(d * 1000.0D) }));
          if (!this.m_IsPreviewReceived) {
            onFirstPreviewFrameReceived();
          }
          if (i != 0) {
            break;
          }
          return;
        }
      }
      finally
      {
        if (localImage != null) {
          localImage.close();
        }
      }
      int i = 0;
      continue;
      label337:
      if (this.m_LastPreviewCbProfileTime <= 0L) {
        this.m_LastPreviewCbProfileTime = l1;
      }
    }
    if ((bool) && (localObject3 != null)) {
      raise(EVENT_PREVIEW_RECEIVED, CameraCaptureEventArgs.obtain(null, null, -1, 17, this.m_PreviewSize, (ImagePlane[])localObject3, null, System.currentTimeMillis()));
    }
  }
  
  private void onRawPictureReceived(Image paramImage)
  {
    Object localObject = (OperationState)get(PROP_CAPTURE_STATE);
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[localObject.ordinal()])
    {
    case 2: 
    case 3: 
    default: 
      Log.w(this.TAG, "onRawPictureReceived() - Capture state is " + localObject);
      return;
    }
    if (this.m_CaptureHandle == null)
    {
      Log.e(this.TAG, "onRawPictureReceived() - No capture handle");
      return;
    }
    paramImage = copyImage(paramImage);
    this.m_ReceivedRawPictureCount += 1;
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("{ ");
    int i = 0;
    while (i < paramImage.length)
    {
      if (i > 0) {
        ((StringBuilder)localObject).append(", ");
      }
      ((StringBuilder)localObject).append(paramImage[i].getData().length);
      i += 1;
    }
    ((StringBuilder)localObject).append(" }");
    Log.v(this.TAG, "onRawPictureReceived() - Index : ", Integer.valueOf(this.m_ReceivedRawPictureCount - 1), ", picture data size : ", localObject);
    if ((this.m_TargetCapturedFrameCount > 0) && (this.m_ReceivedRawPictureCount > this.m_TargetCapturedFrameCount))
    {
      Log.w(this.TAG, "onRawPictureReceived() - Unexpected picture, drop");
      return;
    }
    if ((CaptureResult)this.m_ReceivedCaptureCompletedResults.peek() == null)
    {
      this.m_ReceivedRawPictures.add(paramImage);
      Log.w(this.TAG, "onRawPictureReceived() - Received picture before capture completed");
      return;
    }
    localObject = (ImagePlane[])this.m_ReceivedPictures.poll();
    if (localObject == null)
    {
      Log.w(this.TAG, "onRawPictureReceived() - Wait for picture");
      this.m_ReceivedRawPictures.add(paramImage);
      return;
    }
    onPictureReceived((CaptureResult)this.m_ReceivedCaptureCompletedResults.poll(), (ImagePlane[])localObject, paramImage);
  }
  
  /* Error */
  private boolean openInternal(int paramInt)
  {
    // Byte code:
    //   0: getstatic 410	com/oneplus/camera/CameraImpl:CAMERA_SEMAPHORE	Ljava/util/concurrent/Semaphore;
    //   3: invokevirtual 1642	java/util/concurrent/Semaphore:tryAcquire	()Z
    //   6: ifne +29 -> 35
    //   9: aload_0
    //   10: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   13: ldc_w 1644
    //   16: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   19: getstatic 410	com/oneplus/camera/CameraImpl:CAMERA_SEMAPHORE	Ljava/util/concurrent/Semaphore;
    //   22: invokevirtual 1647	java/util/concurrent/Semaphore:acquire	()V
    //   25: aload_0
    //   26: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   29: ldc_w 1649
    //   32: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   35: aload_0
    //   36: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   39: new 514	java/lang/StringBuilder
    //   42: dup
    //   43: invokespecial 515	java/lang/StringBuilder:<init>	()V
    //   46: ldc_w 1651
    //   49: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   52: aload_0
    //   53: getfield 537	com/oneplus/camera/CameraImpl:m_Id	Ljava/lang/String;
    //   56: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: ldc_w 1082
    //   62: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: invokevirtual 525	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   68: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   71: aload_0
    //   72: getfield 533	com/oneplus/camera/CameraImpl:m_CameraManager	Landroid/hardware/camera2/CameraManager;
    //   75: aload_0
    //   76: getfield 537	com/oneplus/camera/CameraImpl:m_Id	Ljava/lang/String;
    //   79: aload_0
    //   80: getfield 436	com/oneplus/camera/CameraImpl:m_DeviceStateCallback	Landroid/hardware/camera2/CameraDevice$StateCallback;
    //   83: aload_0
    //   84: invokevirtual 977	com/oneplus/camera/CameraImpl:getHandler	()Landroid/os/Handler;
    //   87: invokevirtual 1655	android/hardware/camera2/CameraManager:openCamera	(Ljava/lang/String;Landroid/hardware/camera2/CameraDevice$StateCallback;Landroid/os/Handler;)V
    //   90: iconst_1
    //   91: ireturn
    //   92: astore_2
    //   93: aload_0
    //   94: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   97: ldc_w 1657
    //   100: aload_2
    //   101: invokestatic 992	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   104: iconst_0
    //   105: ireturn
    //   106: astore_2
    //   107: aload_0
    //   108: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   111: new 514	java/lang/StringBuilder
    //   114: dup
    //   115: invokespecial 515	java/lang/StringBuilder:<init>	()V
    //   118: ldc_w 1659
    //   121: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload_0
    //   125: getfield 537	com/oneplus/camera/CameraImpl:m_Id	Ljava/lang/String;
    //   128: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: ldc_w 1082
    //   134: invokevirtual 521	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: invokevirtual 525	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   140: aload_2
    //   141: invokestatic 992	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   144: getstatic 410	com/oneplus/camera/CameraImpl:CAMERA_SEMAPHORE	Ljava/util/concurrent/Semaphore;
    //   147: invokevirtual 1088	java/util/concurrent/Semaphore:release	()V
    //   150: aload_0
    //   151: getstatic 1472	com/oneplus/camera/CameraImpl:EVENT_OPEN_FAILED	Lcom/oneplus/base/EventKey;
    //   154: new 1474	com/oneplus/camera/CameraOpenFailedEventArgs
    //   157: dup
    //   158: aload_0
    //   159: bipush -4
    //   161: invokespecial 1477	com/oneplus/camera/CameraOpenFailedEventArgs:<init>	(Lcom/oneplus/camera/Camera;I)V
    //   164: invokevirtual 1435	com/oneplus/camera/CameraImpl:raise	(Lcom/oneplus/base/EventKey;Lcom/oneplus/base/EventArgs;)V
    //   167: iconst_0
    //   168: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	this	CameraImpl
    //   0	169	1	paramInt	int
    //   92	9	2	localInterruptedException	InterruptedException
    //   106	35	2	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   19	35	92	java/lang/InterruptedException
    //   35	90	106	java/lang/Throwable
  }
  
  private void prepareCaptureRequestParams(CaptureRequest.Builder paramBuilder)
  {
    setFlashMode(this.m_FlashMode, paramBuilder);
    applyAELock(this.m_IsAELocked, paramBuilder);
    applyAERegions(this.m_AeRegions, paramBuilder);
    applyExposureCompensation(this.m_ExposureCompensation, paramBuilder);
    applyFocusMode(this.m_FocusMode, paramBuilder);
    applyAfRegions(this.m_AfRegions, paramBuilder);
    applySceneMode(this.m_SceneMode, paramBuilder);
    applyAwbMode(this.m_AwbMode, paramBuilder);
    applyAwbLock(this.m_IsAwbLocked, paramBuilder);
    applyExposureTime(this.m_ExposureTime, paramBuilder);
    applyFocusDistance(this.m_FocusValue, paramBuilder);
    applyISO(this.m_ISOValue, paramBuilder);
    if (this.m_PreviewFpsRange != null)
    {
      Log.v(this.TAG, "prepareCaptureRequestParams() - FPS range : ", this.m_PreviewFpsRange);
      paramBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, this.m_PreviewFpsRange);
    }
    applyScalerCropRegion(this.m_DigitalZoom, paramBuilder);
    applyFaceDetection(this.m_IsFaceDetectionEnabled, paramBuilder);
  }
  
  private Surface prepareSurface(Object paramObject)
  {
    if ((paramObject instanceof SurfaceHolder)) {
      return ((SurfaceHolder)paramObject).getSurface();
    }
    if ((paramObject instanceof SurfaceTexture))
    {
      paramObject = new Surface((SurfaceTexture)paramObject);
      this.m_TempSurfaces.add(paramObject);
      return (Surface)paramObject;
    }
    if ((paramObject instanceof ImageReader)) {
      return ((ImageReader)paramObject).getSurface();
    }
    if ((paramObject instanceof Surface)) {
      return (Surface)paramObject;
    }
    Log.e(this.TAG, "prepareSurface() - Unsupported receiver : " + paramObject);
    return null;
  }
  
  private void removePreviewReceivedHandler(EventHandler<CameraCaptureEventArgs> paramEventHandler)
  {
    super.removeHandler(EVENT_PREVIEW_RECEIVED, paramEventHandler);
    if ((!hasHandlers(EVENT_PREVIEW_RECEIVED)) && (this.m_PreviewRequestBuilder != null) && (this.m_PreviewCallbackSurface != null))
    {
      Log.v(this.TAG, "removePreviewReceivedHandler() - Remove preview call-back surface");
      this.m_PreviewRequestBuilder.removeTarget(this.m_PreviewCallbackSurface);
      this.m_PreviewCallbackCount = 0;
      this.m_LastPreviewCbProfileTime = 0L;
      if (get(PROP_PREVIEW_STATE) == OperationState.STARTED) {
        startPreviewRequestDirectly();
      }
    }
  }
  
  private void restartCaptureSession()
  {
    if (get(PROP_PREVIEW_STATE) != OperationState.STARTED) {
      return;
    }
    Log.v(this.TAG, "restartCaptureSession()");
    this.m_CaptureSessionState = OperationState.STOPPING;
    stopDriverCaptureSession();
    onCaptureSessionClosed(this.m_CaptureSession);
  }
  
  private boolean setAELockedProp(boolean paramBoolean)
  {
    boolean bool = false;
    verifyAccess();
    if (this.m_IsAELocked == paramBoolean) {
      return false;
    }
    this.m_IsAELocked = paramBoolean;
    if (applyAELock(paramBoolean, this.m_PreviewRequestBuilder)) {
      applyToPreview();
    }
    PropertyKey localPropertyKey = PROP_IS_AE_LOCKED;
    if (paramBoolean) {}
    for (;;)
    {
      return notifyPropertyChanged(localPropertyKey, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      bool = true;
    }
  }
  
  private boolean setAERegionsProp(List<Camera.MeteringRect> paramList)
  {
    verifyAccess();
    List localList;
    if (paramList == null)
    {
      paramList = Collections.EMPTY_LIST;
      localList = this.m_AeRegions;
      this.m_AeRegions = paramList;
      if ((applyAERegions(paramList, this.m_PreviewRequestBuilder)) && (!this.m_IsAELocked)) {
        break label92;
      }
    }
    for (;;)
    {
      return notifyPropertyChanged(PROP_AE_REGIONS, localList, paramList);
      if (paramList.size() > ((Integer)get(PROP_MAX_AE_REGION_COUNT)).intValue()) {
        throw new IllegalArgumentException("Too many AE regions");
      }
      paramList = Collections.unmodifiableList(paramList);
      break;
      label92:
      applyToPreview();
    }
  }
  
  private boolean setAFRegionsProp(List<Camera.MeteringRect> paramList)
  {
    verifyAccess();
    if (paramList == null) {}
    for (paramList = Collections.EMPTY_LIST;; paramList = Collections.unmodifiableList(paramList))
    {
      Log.v(this.TAG, "setAFRegionsProp() - Regions : ", paramList);
      List localList = this.m_AfRegions;
      this.m_AfRegions = paramList;
      if (applyAfRegions(paramList, this.m_PreviewRequestBuilder)) {
        applyToPreview();
      }
      return notifyPropertyChanged(PROP_AF_REGIONS, localList, paramList);
      if (paramList.size() > ((Integer)get(PROP_MAX_AF_REGION_COUNT)).intValue()) {
        throw new IllegalArgumentException("Too many AF regions");
      }
    }
  }
  
  private boolean setAWBModeProp(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    if (this.m_AwbMode == paramInt) {
      return true;
    }
    if (!((List)get(PROP_AWB_MODES)).contains(Integer.valueOf(paramInt)))
    {
      Log.e(this.TAG, "setAWBModeProp() - Invalid awb mode : " + paramInt);
      return false;
    }
    Log.v(this.TAG, "setAWBModeProp() - Awb mode : ", Integer.valueOf(paramInt));
    int i = this.m_AwbMode;
    this.m_AwbMode = paramInt;
    if (applyAwbMode(paramInt, this.m_PreviewRequestBuilder)) {
      applyToPreview();
    }
    return notifyPropertyChanged(PROP_AWB_MODE, Integer.valueOf(i), Integer.valueOf(paramInt));
  }
  
  private void setAudioSourceParams(String paramString)
  {
    ((AudioManager)getContext().getSystemService("audio")).setParameters(paramString);
  }
  
  private boolean setAwbLockedProp(boolean paramBoolean)
  {
    boolean bool = false;
    verifyAccess();
    if (this.m_IsAwbLocked == paramBoolean) {
      return false;
    }
    this.m_IsAwbLocked = paramBoolean;
    if (applyAwbLock(paramBoolean, this.m_PreviewRequestBuilder)) {
      applyToPreview();
    }
    PropertyKey localPropertyKey = PROP_IS_AWB_LOCKED;
    if (paramBoolean) {}
    for (;;)
    {
      return notifyPropertyChanged(localPropertyKey, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      bool = true;
    }
  }
  
  private boolean setDigitalZoomProp(float paramFloat)
  {
    verifyAccess();
    verifyReleaseState();
    this.m_DigitalZoom = paramFloat;
    if (this.m_DigitalZoom < 1.0F) {
      this.m_DigitalZoom = 1.0F;
    }
    if (applyScalerCropRegion(this.m_DigitalZoom, this.m_PreviewRequestBuilder)) {
      applyToPreview();
    }
    return super.set(PROP_DIGITAL_ZOOM, Float.valueOf(this.m_DigitalZoom));
  }
  
  private boolean setExposureCompensationProp(float paramFloat)
  {
    verifyAccess();
    verifyReleaseState();
    Range localRange = (Range)get(PROP_EXPOSURE_COMPENSATION_RANGE);
    if (paramFloat < ((Float)localRange.getLower()).floatValue()) {
      f = ((Float)localRange.getLower()).floatValue();
    }
    for (;;)
    {
      paramFloat = ((Float)get(PROP_EXPOSURE_COMPENSATION_STEP)).floatValue();
      paramFloat = Math.round(f / paramFloat) * paramFloat;
      if (Math.abs(this.m_ExposureCompensation - paramFloat) >= 0.001D) {
        break;
      }
      return false;
      f = paramFloat;
      if (paramFloat > ((Float)localRange.getUpper()).floatValue()) {
        f = ((Float)localRange.getUpper()).floatValue();
      }
    }
    Log.v(this.TAG, "setExposureCompensationProp() - EV : ", Float.valueOf(paramFloat));
    float f = this.m_ExposureCompensation;
    this.m_ExposureCompensation = paramFloat;
    if (applyExposureCompensation(paramFloat, this.m_PreviewRequestBuilder)) {
      applyToPreview();
    }
    return notifyPropertyChanged(PROP_EXPOSURE_COMPENSATION, Float.valueOf(f), Float.valueOf(paramFloat));
  }
  
  private boolean setExposureTimeProp(long paramLong)
  {
    verifyAccess();
    verifyReleaseState();
    if (this.m_ExposureTime == paramLong) {
      return true;
    }
    Range localRange = (Range)get(PROP_EXPOSURE_TIME_NANOS_RANGE);
    long l = paramLong;
    if (paramLong != -1L)
    {
      if (paramLong <= ((Long)localRange.getUpper()).longValue()) {
        break label136;
      }
      Log.v(this.TAG, "setExposureTimeProp() - Exposure time is too large, trim to upper");
      l = ((Long)localRange.getUpper()).longValue();
    }
    for (;;)
    {
      Log.v(this.TAG, "setExposureTimeProp() - Exposure time : ", Long.valueOf(l));
      paramLong = this.m_ExposureTime;
      this.m_ExposureTime = l;
      if (applyExposureTime(l, this.m_PreviewRequestBuilder)) {
        applyToPreview();
      }
      return notifyPropertyChanged(PROP_EXPOSURE_TIME_NANOS, Long.valueOf(paramLong), Long.valueOf(l));
      label136:
      l = paramLong;
      if (paramLong < ((Long)localRange.getLower()).longValue())
      {
        Log.v(this.TAG, "setExposureTimeProp() - Exposure time is too small, trim to lower");
        l = ((Long)localRange.getLower()).longValue();
      }
    }
  }
  
  private boolean setFaceDetectionProp(boolean paramBoolean)
  {
    boolean bool = false;
    verifyAccess();
    if (this.m_IsFaceDetectionEnabled == paramBoolean) {
      return false;
    }
    this.m_IsFaceDetectionEnabled = paramBoolean;
    if (applyFaceDetection(paramBoolean, this.m_PreviewRequestBuilder)) {
      applyToPreview();
    }
    PropertyKey localPropertyKey = PROP_IS_FACE_DETECTION_ENABLED;
    if (paramBoolean) {}
    for (;;)
    {
      return notifyPropertyChanged(localPropertyKey, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      bool = true;
    }
  }
  
  private void setFlashMode(FlashMode paramFlashMode, CaptureRequest.Builder paramBuilder)
  {
    int k = 1;
    if ((this.m_ExposureTime != -1L) || (this.m_ISOValue != -1)) {
      k = 0;
    }
    this.m_PendingFlashMode = null;
    int i;
    int j;
    if (paramBuilder != null) {
      switch (-getcom-oneplus-camera-FlashModeSwitchesValues()[paramFlashMode.ordinal()])
      {
      default: 
        throw new RuntimeException("Unsupported flash mode : " + paramFlashMode + ".");
      case 1: 
        if (this.m_FlashMode == FlashMode.TORCH)
        {
          i = 1;
          j = 0;
          this.m_PendingFlashMode = FlashMode.AUTO;
        }
        break;
      }
    }
    for (;;)
    {
      if (k == 0)
      {
        i = 0;
        j = 0;
      }
      paramBuilder.set(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(i));
      paramBuilder.set(CaptureRequest.FLASH_MODE, Integer.valueOf(j));
      this.m_FlashMode = paramFlashMode;
      return;
      i = 2;
      j = 1;
      continue;
      i = 1;
      j = 0;
      continue;
      i = 3;
      j = 1;
      continue;
      i = 1;
      j = 2;
    }
  }
  
  private boolean setFlashModeProp(FlashMode paramFlashMode)
  {
    verifyAccess();
    FlashMode localFlashMode = this.m_FlashMode;
    if (localFlashMode == paramFlashMode) {
      return false;
    }
    if ((!((Boolean)get(PROP_HAS_FLASH)).booleanValue()) && (paramFlashMode != FlashMode.OFF))
    {
      Log.e(this.TAG, "setFlashModeProp() - No flash on camera '" + this.m_Id + "'");
      return false;
    }
    Log.v(this.TAG, "setFlashModeProp() - Flash mode : ", paramFlashMode);
    setFlashMode(paramFlashMode, this.m_PreviewRequestBuilder);
    applyToPreview();
    return notifyPropertyChanged(PROP_FLASH_MODE, localFlashMode, paramFlashMode);
  }
  
  private boolean setFocusModeProp(FocusMode paramFocusMode)
  {
    verifyAccess();
    if (paramFocusMode == null) {
      throw new IllegalArgumentException("No focus mode specified");
    }
    List localList = (List)get(PROP_FOCUS_MODES);
    FocusMode localFocusMode = paramFocusMode;
    if (!localList.contains(paramFocusMode))
    {
      if (!localList.contains(FocusMode.CONTINUOUS_AF)) {
        break label97;
      }
      paramFocusMode = FocusMode.CONTINUOUS_AF;
    }
    for (;;)
    {
      Log.e(this.TAG, "setFocusModeProp() - Invalid focus mode, change to " + paramFocusMode);
      localFocusMode = paramFocusMode;
      if (this.m_FocusMode != localFocusMode) {
        break;
      }
      return false;
      label97:
      if (localList.contains(FocusMode.NORMAL_AF)) {
        paramFocusMode = FocusMode.NORMAL_AF;
      } else {
        paramFocusMode = FocusMode.DISABLED;
      }
    }
    Log.v(this.TAG, "setFocusModeProp() - Focus mode : ", localFocusMode);
    paramFocusMode = this.m_FocusMode;
    this.m_FocusMode = localFocusMode;
    if ((applyFocusMode(localFocusMode, this.m_PreviewRequestBuilder)) && (this.m_CaptureSessionState == OperationState.STARTED)) {
      applyToPreview();
    }
    return notifyPropertyChanged(PROP_FOCUS_MODE, paramFocusMode, localFocusMode);
  }
  
  private boolean setFocusProp(float paramFloat)
  {
    verifyAccess();
    verifyReleaseState();
    if (Math.abs(this.m_FocusValue - paramFloat) < 0.1F) {
      return true;
    }
    Range localRange = (Range)get(PROP_FOCUS_RANGE);
    float f = paramFloat;
    if (paramFloat != -1.0F)
    {
      if (paramFloat <= ((Float)localRange.getUpper()).floatValue()) {
        break label140;
      }
      Log.v(this.TAG, "setFocusProp() - Focus value is too large, trim to upper");
      f = ((Float)localRange.getUpper()).floatValue();
    }
    for (;;)
    {
      Log.v(this.TAG, "setFocusProp() - Focus : ", Float.valueOf(f));
      paramFloat = this.m_FocusValue;
      this.m_FocusValue = f;
      if (applyFocusDistance(f, this.m_PreviewRequestBuilder)) {
        applyToPreview();
      }
      return notifyPropertyChanged(PROP_FOCUS, Float.valueOf(paramFloat), Float.valueOf(f));
      label140:
      f = paramFloat;
      if (paramFloat < ((Float)localRange.getLower()).floatValue())
      {
        Log.v(this.TAG, "setFocusProp() - Focus value is too small, trim to lower");
        f = ((Float)localRange.getLower()).floatValue();
      }
    }
  }
  
  private boolean setISOProp(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    if (this.m_ISOValue == paramInt) {
      return true;
    }
    Range localRange = (Range)get(PROP_ISO_RANGE);
    int i = paramInt;
    if (paramInt != -1)
    {
      if (paramInt <= ((Integer)localRange.getUpper()).intValue()) {
        break label128;
      }
      Log.v(this.TAG, "setISOProp() - ISO value is too large, trim to upper");
      i = ((Integer)localRange.getUpper()).intValue();
    }
    for (;;)
    {
      Log.v(this.TAG, "setISOProp() - ISO value : ", Integer.valueOf(i));
      paramInt = this.m_ISOValue;
      this.m_ISOValue = i;
      if (applyISO(i, this.m_PreviewRequestBuilder)) {
        applyToPreview();
      }
      return notifyPropertyChanged(PROP_ISO, Integer.valueOf(paramInt), Integer.valueOf(i));
      label128:
      i = paramInt;
      if (paramInt < ((Integer)localRange.getLower()).intValue())
      {
        Log.v(this.TAG, "setISOProp() - ISO value is too small, trim to lower");
        i = ((Integer)localRange.getLower()).intValue();
      }
    }
  }
  
  private boolean setJpegQualityProp(int paramInt)
  {
    verifyAccess();
    if ((paramInt <= 0) || (paramInt > 100)) {
      return false;
    }
    this.m_JpegQuality = paramInt;
    return super.set(PROP_JPEG_QUALITY, Integer.valueOf(paramInt));
  }
  
  private boolean setPictureSize(Size paramSize)
  {
    if (paramSize == null) {
      throw new IllegalArgumentException("No picture size specified.");
    }
    if (!((List)get(PROP_PICTURE_SIZES)).contains(paramSize))
    {
      Log.e(this.TAG, "setPictureSize() - Size " + paramSize + " is not contained in size list");
      throw new IllegalArgumentException("Invalid picture size.");
    }
    if (this.m_PictureSize.equals(paramSize)) {
      return false;
    }
    Log.w(this.TAG, "setPictureSize() - Size : " + paramSize);
    Size localSize = this.m_PictureSize;
    this.m_PictureSize = paramSize;
    notifyPropertyChanged(PROP_PICTURE_SIZE, localSize, paramSize);
    if ((this.m_CaptureSessionState != OperationState.STOPPING) && (this.m_CaptureSessionState != OperationState.STOPPED))
    {
      Log.w(this.TAG, "setPictureSize() - Restart capture session to apply new picture size");
      stopCaptureSession(false);
      startCaptureSession();
    }
    return true;
  }
  
  private boolean setPreviewFpsRangeProp(Range<Integer> paramRange)
  {
    verifyAccess();
    verifyReleaseState();
    if ((this.m_PreviewFpsRange == paramRange) || ((this.m_PreviewFpsRange != null) && (this.m_PreviewFpsRange.equals(paramRange)))) {
      return false;
    }
    if ((paramRange == null) || (((List)get(PROP_PREVIEW_FPS_RANGES)).contains(paramRange))) {}
    for (;;)
    {
      Log.v(this.TAG, "setPreviewFpsRangeProp() - FPS range : " + paramRange);
      Range localRange = this.m_PreviewFpsRange;
      this.m_PreviewFpsRange = paramRange;
      if (applyPreviewFpsRange(paramRange, this.m_PreviewRequestBuilder)) {
        applyToPreview();
      }
      return notifyPropertyChanged(PROP_PREVIEW_FPS_RANGE, localRange, paramRange);
      Log.e(this.TAG, "setPreviewFpsRangeProp() - Invalid range : " + paramRange);
    }
  }
  
  private boolean setPreviewReceiver(Object paramObject)
  {
    verifyAccess();
    if (get(PROP_PREVIEW_RECEIVER) == paramObject) {
      return false;
    }
    if (get(PROP_PREVIEW_STATE) != OperationState.STOPPED)
    {
      Log.e(this.TAG, "setPreviewReceiver() - Preview state is " + get(PROP_PREVIEW_STATE));
      throw new RuntimeException("Cannot change preview receiver when preview state is not STOPPED.");
    }
    stopCaptureSession(false);
    if (this.m_PreviewRequestBuilder != null)
    {
      if (this.m_PreviewSurface != null)
      {
        this.m_PreviewRequestBuilder.removeTarget(this.m_PreviewSurface);
        if (this.m_TempSurfaces.remove(this.m_PreviewSurface)) {
          this.m_PreviewSurface.release();
        }
        this.m_PreviewSurface = null;
      }
      if (paramObject != null)
      {
        Surface localSurface = prepareSurface(paramObject);
        if (localSurface == null) {
          break label165;
        }
        this.m_PreviewRequestBuilder.addTarget(localSurface);
      }
    }
    super.set(PROP_PREVIEW_RECEIVER, paramObject);
    return true;
    label165:
    Log.e(this.TAG, "setPreviewReceiver() - Fail to prepare Surface");
    throw new RuntimeException("Invalid preview receiver.");
  }
  
  private boolean setPreviewSizeProp(Size paramSize)
  {
    verifyAccess();
    verifyReleaseState();
    if (paramSize == null) {
      throw new IllegalArgumentException("No preview size");
    }
    Size localSize = this.m_PreviewSize;
    if (paramSize.equals(localSize)) {
      return false;
    }
    if (!((List)get(PROP_PREVIEW_SIZES)).contains(paramSize))
    {
      Log.e(this.TAG, "setPreviewSizeProp() - Invalid preview size : " + paramSize);
      return false;
    }
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    }
    for (int i = 0;; i = 1)
    {
      this.m_PreviewSize = paramSize;
      if (i != 0)
      {
        Log.w(this.TAG, "setPreviewSizeProp() - Restart preview");
        startPreview(0);
      }
      return notifyPropertyChanged(PROP_PREVIEW_SIZE, localSize, paramSize);
      Log.w(this.TAG, "setPreviewSizeProp() - Stop preview to change preview size");
      stopPreview(0);
    }
  }
  
  private boolean setRawCaptureProp(boolean paramBoolean)
  {
    boolean bool = false;
    Log.w(this.TAG, "setRawCaptureProp() - isEnabled is " + paramBoolean);
    verifyAccess();
    if (this.m_IsRawCaptureEnabled == paramBoolean) {
      return false;
    }
    this.m_IsRawCaptureEnabled = paramBoolean;
    PropertyKey localPropertyKey = PROP_IS_RAW_CAPTURE_ENABLED;
    if (paramBoolean) {}
    for (;;)
    {
      return notifyPropertyChanged(localPropertyKey, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      bool = true;
    }
  }
  
  private boolean setRecordingModeProp(boolean paramBoolean)
  {
    verifyAccess();
    if (this.m_IsRecordingMode == paramBoolean) {
      return false;
    }
    if (get(PROP_CAPTURE_STATE) != OperationState.STOPPED)
    {
      Log.e(this.TAG, "setRecordingModeProp() - Current capture state is " + get(PROP_CAPTURE_STATE));
      throw new IllegalStateException("Cannot change recording mode due to current capture state.");
    }
    Log.w(this.TAG, "setRecordingModeProp() - Recording mode : " + paramBoolean);
    int i;
    PropertyKey localPropertyKey;
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    default: 
      i = 0;
      this.m_IsRecordingMode = paramBoolean;
      if (i != 0)
      {
        Log.w(this.TAG, "setRecordingModeProp() - Restart preview");
        startPreview(0);
      }
      localPropertyKey = PROP_IS_RECORDING_MODE;
      if (!paramBoolean) {
        break;
      }
    }
    for (boolean bool = false;; bool = true)
    {
      return notifyPropertyChanged(localPropertyKey, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      Log.w(this.TAG, "setRecordingModeProp() - Stop preview to change recording mode");
      stopPreview(0);
      i = 1;
      break;
    }
  }
  
  private boolean setSceneModeProp(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    if (this.m_SceneMode == paramInt) {
      return true;
    }
    if ((!((List)get(PROP_SCENE_MODES)).contains(Integer.valueOf(paramInt))) && (paramInt != 0))
    {
      Log.e(this.TAG, "setSceneModeProp() - Invalid scene mode : " + paramInt);
      return false;
    }
    Log.v(this.TAG, "setSceneModeProp() - Scene mode : ", Integer.valueOf(paramInt));
    if ((this.m_PreviewRequestBuilder != null) && (applySceneMode(paramInt, this.m_PreviewRequestBuilder))) {
      applyToPreview();
    }
    int i = this.m_SceneMode;
    this.m_SceneMode = paramInt;
    return notifyPropertyChanged(PROP_SCENE_MODE, Integer.valueOf(i), Integer.valueOf(paramInt));
  }
  
  private boolean setVideoSizeProp(Size paramSize)
  {
    verifyAccess();
    verifyReleaseState();
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    }
    while (paramSize == null)
    {
      throw new IllegalArgumentException("No video size.");
      if (this.m_IsRecordingMode) {
        Log.v(this.TAG, "setVideoSizeProp() - Apply video size when starting preview next time");
      }
    }
    if (!((List)get(PROP_VIDEO_SIZES)).contains(paramSize))
    {
      Log.e(this.TAG, "setVideoSizeProp() - Invalid video size : " + paramSize);
      return false;
    }
    if (this.m_VideoSize.equals(paramSize)) {
      return false;
    }
    Log.v(this.TAG, "setVideoSizeProp() - Video size : ", paramSize);
    Size localSize = this.m_VideoSize;
    this.m_VideoSize = paramSize;
    return notifyPropertyChanged(PROP_VIDEO_SIZE, localSize, paramSize);
  }
  
  private void startAutoFocus()
  {
    if (this.m_PreviewRequestBuilder == null) {
      return;
    }
    int i;
    if (get(PROP_PREVIEW_STATE) == OperationState.STARTED) {
      i = 1;
    }
    for (;;)
    {
      if (i != 0) {}
      Log.v(this.TAG, "startAutoFocus()");
      if (i != 0)
      {
        switch (-getcom-oneplus-camera-FocusStateSwitchesValues()[((FocusState)get(PROP_FOCUS_STATE)).ordinal()])
        {
        default: 
          Log.v(this.TAG, "startAutoFocus() - Trigger AF");
          this.m_PreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(1));
        }
        try
        {
          CaptureRequest localCaptureRequest = this.m_PreviewRequestBuilder.build();
          this.m_CaptureSession.capture(localCaptureRequest, this.m_PreviewCaptureCallback, getHandler());
          this.m_PreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(0));
          if (get(PROP_FOCUS_STATE) == FocusState.SCANNING)
          {
            setReadOnly(PROP_FOCUS_STATE, FocusState.INACTIVE);
            setReadOnly(PROP_FOCUS_STATE, FocusState.STARTING);
            setReadOnly(PROP_FOCUS_STATE, FocusState.SCANNING);
            return;
            i = 0;
            continue;
            Log.v(this.TAG, "startAutoFocus() - Cancel current auto-focus");
            try
            {
              cancelAutoFocus(0);
              setReadOnly(PROP_FOCUS_STATE, FocusState.INACTIVE);
            }
            catch (Throwable localThrowable1)
            {
              for (;;)
              {
                Log.w(this.TAG, "startAutoFocus() - Fail to cancel auto-focus", localThrowable1);
              }
            }
          }
        }
        catch (Throwable localThrowable2)
        {
          for (;;)
          {
            Log.e(this.TAG, "startAutoFocus() - Fail to start autofocus", localThrowable2);
          }
          this.m_IsAutoFocusStarting = true;
          HandlerUtils.sendMessage(this, 10011, true, 5000L);
          setReadOnly(PROP_FOCUS_STATE, FocusState.STARTING);
          return;
        }
      }
    }
    this.m_PreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(0));
  }
  
  private boolean startCaptureSession()
  {
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_CaptureSessionState.ordinal()])
    {
    default: 
      Log.e(this.TAG, "startCaptureSession() - Current session state is " + this.m_CaptureSessionState);
      return false;
    case 1: 
    case 2: 
      return true;
    }
    if ((this.m_PreviewSize.getWidth() <= 0) || (this.m_PreviewSize.getHeight() <= 0))
    {
      Log.e(this.TAG, "startCaptureSession() - Empty preview size");
      return false;
    }
    Size localSize = this.m_PictureSize;
    if ((localSize.getWidth() <= 0) || (localSize.getHeight() <= 0))
    {
      Log.e(this.TAG, "startCaptureSession() - Empty picture size");
      return false;
    }
    int j = ((Integer)get(PROP_PICTURE_FORMAT)).intValue();
    int i = j;
    switch (j)
    {
    default: 
      Log.e(this.TAG, "startCaptureSession() - Unknown picture format : " + j);
      return false;
    case 17: 
      i = 35;
    }
    ArrayList localArrayList = new ArrayList();
    this.m_PreviewSurface = prepareSurface(get(PROP_PREVIEW_RECEIVER));
    if (this.m_PreviewSurface == null)
    {
      Log.e(this.TAG, "startCaptureSession() - Fail to prepare Surface for preview");
      return false;
    }
    localArrayList.add(this.m_PreviewSurface);
    if ((this.m_IsRecordingMode) && (this.m_VideoSurface != null))
    {
      Log.v(this.TAG, "startCaptureSession() - Video surface : ", this.m_VideoSurface);
      localArrayList.add(this.m_VideoSurface);
      if (this.m_IsRecordingMode) {
        break label779;
      }
      int k = localSize.getWidth();
      int m = localSize.getHeight();
      if (i != 256) {
        break label774;
      }
      j = 2;
      label352:
      this.m_PictureReader = ImageReader.newInstance(k, m, i, j);
      label364:
      Log.v(this.TAG, "startCaptureSession() - Picture reader buffer size : ", this.m_PictureReader.getWidth() + "x" + this.m_PictureReader.getHeight());
      this.m_PictureReader.setOnImageAvailableListener(this.m_PictureAvailableListener, getHandler());
      this.m_PictureSurface = this.m_PictureReader.getSurface();
      localArrayList.add(this.m_PictureSurface);
      if (isRawCaptureNeeded())
      {
        Log.v(this.TAG, "startCaptureSession() - add rawSurface into surfaces list");
        localSize = (Size)get(Camera.PROP_SENSOR_SIZE);
        this.m_RawReader = ImageReader.newInstance(localSize.getWidth(), localSize.getHeight(), 32, 1);
        this.m_RawReader.setOnImageAvailableListener(this.m_RawCallback, getHandler());
        this.m_RawSurface = this.m_RawReader.getSurface();
        localArrayList.add(this.m_RawSurface);
      }
    }
    for (;;)
    {
      try
      {
        if (this.m_IsRecordingMode) {
          continue;
        }
        this.m_PreviewRequestBuilder = this.m_Device.createCaptureRequest(1);
        this.m_DefaultPhotoPreviewFpsRange = ((Range)this.m_PreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE));
        this.m_PreviewRequestBuilder.addTarget(this.m_PreviewSurface);
        if ((!this.m_IsRecordingMode) || (this.m_VideoSurface == null)) {
          continue;
        }
        this.m_PreviewRequestBuilder.addTarget(this.m_VideoSurface);
      }
      catch (Throwable localThrowable2)
      {
        label774:
        label779:
        Log.e(this.TAG, "startCaptureSession() - Fail to create preview request builder", localThrowable2);
        continue;
        Log.v(this.TAG, "startCaptureSession() - Add preview call-back surface");
        this.m_PreviewRequestBuilder.addTarget(this.m_PreviewCallbackSurface);
        continue;
      }
      prepareCaptureRequestParams(this.m_PreviewRequestBuilder);
      applyPreviewFpsRange(this.m_PreviewFpsRange, this.m_PreviewRequestBuilder);
      try
      {
        Log.w(this.TAG, "startCaptureSession() - Create capture session for camera '" + this.m_Id + "'");
        this.m_Device.createCaptureSession(localArrayList, this.m_CaptureSessionCallback, getHandler());
        this.m_IsCaptureSessionClosed = false;
        this.m_CaptureSessionState = OperationState.STARTING;
        getHandler().sendEmptyMessageDelayed(10001, 3000L);
        return true;
      }
      catch (Throwable localThrowable1)
      {
        Log.e(this.TAG, "startCaptureSession() - Fail to create capture session for camera '" + this.m_Id + "'", localThrowable1);
      }
      this.m_PreviewCallbackReader = ImageReader.newInstance(this.m_PreviewSize.getWidth(), this.m_PreviewSize.getHeight(), 35, 2);
      this.m_PreviewCallbackReader.setOnImageAvailableListener(this.m_PreviewCallbackReaderCallback, getHandler());
      this.m_PreviewCallbackSurface = this.m_PreviewCallbackReader.getSurface();
      localArrayList.add(this.m_PreviewCallbackSurface);
      break;
      j = 1;
      break label352;
      if ((this.m_VideoSize.getWidth() > 0) && (this.m_VideoSize.getHeight() > 0))
      {
        this.m_PictureReader = ImageReader.newInstance(this.m_VideoSize.getWidth(), this.m_VideoSize.getHeight(), i, 1);
        break label364;
      }
      this.m_PictureReader = ImageReader.newInstance(this.m_PreviewSize.getWidth(), this.m_PreviewSize.getHeight(), i, 1);
      break label364;
      Log.v(this.TAG, "startCaptureSession() - Create request builder for video recording");
      this.m_PreviewRequestBuilder = this.m_Device.createCaptureRequest(3);
    }
    return false;
  }
  
  private boolean startPreviewRequest()
  {
    if (this.m_CaptureSessionState != OperationState.STARTED)
    {
      Log.e(this.TAG, "startPreviewRequest() - Capture session state is " + this.m_CaptureSessionState);
      return false;
    }
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "startPreviewRequest() - Preview state is " + get(PROP_PREVIEW_STATE));
      return false;
    }
    Log.w(this.TAG, "startPreviewRequest() - Start preview request for camera '" + this.m_Id + "'");
    if (startPreviewRequestDirectly())
    {
      if (this.m_FocusMode == FocusMode.CONTINUOUS_AF) {
        cancelAutoFocus(0);
      }
      setReadOnly(PROP_PREVIEW_STATE, OperationState.STARTED);
      return true;
    }
    setReadOnly(PROP_PREVIEW_STATE, OperationState.STOPPED);
    return false;
  }
  
  private boolean startPreviewRequestDirectly()
  {
    try
    {
      Log.v(this.TAG, "startPreviewRequestDirectly()");
      this.m_CaptureSession.setRepeatingRequest(this.m_PreviewRequestBuilder.build(), this.m_PreviewCaptureCallback, getHandler());
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "startPreviewRequestDirectly() - Fail to start preview for camera '" + this.m_Id + "'", localThrowable);
    }
    return false;
  }
  
  private void stopCaptureInternal(boolean paramBoolean)
  {
    if (this.m_CaptureHandle == null)
    {
      Log.e(this.TAG, "stopCaptureInternal() - No capture handle");
      return;
    }
    OperationState localOperationState = (OperationState)get(PROP_CAPTURE_STATE);
    int j;
    int i;
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[localOperationState.ordinal()])
    {
    case 1: 
    default: 
      setReadOnly(PROP_CAPTURE_STATE, OperationState.STOPPING);
      getHandler().removeMessages(10030);
      j = 0;
      i = j;
      if ((localOperationState == OperationState.STARTED) && (this.m_TargetCapturedFrameCount == 1)) {
        break;
      }
    case 3: 
    case 2: 
    case 4: 
      try
      {
        Log.w(this.TAG, "stopCaptureInternal() - Stop repeating request");
        this.m_CaptureSession.stopRepeating();
        i = j;
      }
      catch (Throwable localThrowable1)
      {
        for (;;)
        {
          label128:
          Log.e(this.TAG, "stopCaptureInternal() - Fail to stop repeating", localThrowable1);
          i = j;
        }
      }
      if (i != 0)
      {
        Log.w(this.TAG, "stopCaptureInternal() - Stop capture directly");
        onCaptureCompleted(true);
      }
      break;
    }
    while (this.m_IsCaptureStartedReceived)
    {
      return;
      return;
      Log.w(this.TAG, "stopCaptureInternal() - Stop while starting");
      break;
      return;
      i = j;
      if (!paramBoolean) {
        break label128;
      }
      try
      {
        Log.w(this.TAG, "stopCaptureInternal() - Abort captures");
        this.m_CaptureSession.abortCaptures();
        setReadOnly(PROP_IS_CAPTURING_RAW_PHOTO, Boolean.valueOf(false));
        i = 1;
      }
      catch (Throwable localThrowable2)
      {
        for (;;)
        {
          Log.e(this.TAG, "stopCaptureInternal() - Fail to abort captures", localThrowable2);
        }
      }
    }
    Log.w(this.TAG, "stopCaptureInternal() - Capture call-back not received yet, stop capture directly");
    onCaptureCompleted(true);
  }
  
  private void stopCaptureSession(boolean paramBoolean)
  {
    if (this.m_IsCaptureSessionClosed)
    {
      if ((paramBoolean) && (getHandler().hasMessages(10020)))
      {
        Log.w(this.TAG, "stopCaptureSession() - Stop waiting for call-back");
        getHandler().removeMessages(10020);
        onCaptureSessionClosed(this.m_CaptureSession);
        return;
      }
      Log.w(this.TAG, "stopCaptureSession() - CaptureSession is already closed");
      return;
    }
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_CaptureSessionState.ordinal()])
    {
    case 1: 
    default: 
      Log.w(this.TAG, "stopCaptureSession() - Stop capture session for camera '" + this.m_Id + "'");
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_CAPTURE_STATE)).ordinal()])
      {
      case 3: 
      default: 
        label188:
        switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
        {
        default: 
          Log.w(this.TAG, "stopCaptureSession() - Stop preview directly");
          setReadOnly(PROP_PREVIEW_STATE, OperationState.STOPPING);
        }
        this.m_CaptureSessionState = OperationState.STOPPING;
        if ((!paramBoolean) && (!this.m_IsPreviewReceived)) {}
        break;
      }
      break;
    }
    while (!getHandler().hasMessages(10001))
    {
      stopDriverCaptureSession();
      if (!paramBoolean) {
        break label408;
      }
      onCaptureSessionClosed(this.m_CaptureSession);
      return;
      return;
      if (paramBoolean) {
        break;
      }
      return;
      Log.w(this.TAG, "stopCaptureSession() - Stop while starting");
      this.m_CaptureSessionState = OperationState.STOPPING;
      return;
      Log.w(this.TAG, "stopCaptureSession() - Stop capture and wait for completion");
      this.m_CaptureSessionState = OperationState.STOPPING;
      stopCaptureInternal(true);
      if (get(PROP_CAPTURE_STATE) == OperationState.STOPPED) {
        break label188;
      }
      return;
      Log.w(this.TAG, "stopCaptureSession() - Stop while starting capture, stop capture directly");
      onCaptureCompleted(false);
      break label188;
      Log.w(this.TAG, "stopCaptureSession() - Wait for capture completion");
      if (paramBoolean) {
        break label188;
      }
      this.m_CaptureSessionState = OperationState.STOPPING;
      return;
    }
    Log.w(this.TAG, "stopCaptureSession() - Wait for first preview frame");
    return;
    label408:
    getHandler().sendEmptyMessageDelayed(10020, 1000L);
  }
  
  private void stopDriverCaptureSession()
  {
    try
    {
      Log.w(this.TAG, "stopDriverCaptureSession() - Stop repeating");
      this.m_CaptureSession.stopRepeating();
    }
    catch (Throwable localThrowable1)
    {
      try
      {
        for (;;)
        {
          Log.w(this.TAG, "stopDriverCaptureSession() - Abort captures");
          this.m_CaptureSession.abortCaptures();
          try
          {
            Log.w(this.TAG, "stopDriverCaptureSession() - Close session");
            this.m_CaptureSession.close();
            this.m_IsCaptureSessionClosed = true;
            return;
          }
          catch (Throwable localThrowable3)
          {
            Log.w(this.TAG, "stopDriverCaptureSession() - Fail to close captures", localThrowable3);
          }
          localThrowable1 = localThrowable1;
          Log.w(this.TAG, "stopDriverCaptureSession() - Fail to stop repeating", localThrowable1);
        }
      }
      catch (Throwable localThrowable2)
      {
        for (;;)
        {
          Log.w(this.TAG, "stopDriverCaptureSession() - Fail to abort captures", localThrowable2);
        }
      }
    }
  }
  
  private boolean updateAEState(CaptureResult paramCaptureResult)
  {
    Object localObject = (Integer)paramCaptureResult.get(CaptureResult.CONTROL_AE_STATE);
    paramCaptureResult = (CaptureResult)localObject;
    if (localObject == null) {
      paramCaptureResult = Integer.valueOf(0);
    }
    localObject = (AutoExposureState)get(PROP_AE_STATE);
    boolean bool;
    switch (paramCaptureResult.intValue())
    {
    default: 
      Log.w(this.TAG, "updateAEState() - Unknown AE state : " + paramCaptureResult);
      bool = setReadOnly(PROP_AE_STATE, AutoExposureState.INACTIVE);
    }
    for (;;)
    {
      if (bool) {
        this.m_PreviousAeState = ((AutoExposureState)localObject);
      }
      return bool;
      bool = setReadOnly(PROP_AE_STATE, AutoExposureState.CONVERGED);
      continue;
      bool = setReadOnly(PROP_AE_STATE, AutoExposureState.FLASH_REQUIRED);
      continue;
      bool = setReadOnly(PROP_AE_STATE, AutoExposureState.LOCKED);
      continue;
      bool = setReadOnly(PROP_AE_STATE, AutoExposureState.PRECAPTURE);
      continue;
      bool = setReadOnly(PROP_AE_STATE, AutoExposureState.SEARCHING);
      continue;
      bool = setReadOnly(PROP_AE_STATE, AutoExposureState.INACTIVE);
    }
  }
  
  private void updateFaceDetectionResult(CaptureResult paramCaptureResult)
  {
    Face[] arrayOfFace = (Face[])paramCaptureResult.get(CaptureResult.STATISTICS_FACES);
    List localList1 = this.m_FaceLists[this.m_FaceListIndex];
    if (((arrayOfFace == null) || (arrayOfFace.length == 0)) && (localList1.isEmpty())) {
      return;
    }
    this.m_FaceListIndex = ((this.m_FaceListIndex + 1) % this.m_FaceLists.length);
    List localList2 = this.m_FaceLists[this.m_FaceListIndex];
    if ((arrayOfFace != null) && (arrayOfFace.length > 0))
    {
      paramCaptureResult = (Rect)paramCaptureResult.get(CaptureResult.SCALER_CROP_REGION);
      i = arrayOfFace.length - 1;
      while (i >= 0)
      {
        localList2.add(Camera.Face.obtain(paramCaptureResult, arrayOfFace[i]));
        i -= 1;
      }
    }
    notifyPropertyChanged(PROP_FACES, localList1, localList2);
    int i = localList1.size() - 1;
    while (i >= 0)
    {
      ((Camera.Face)localList1.get(i)).recycle();
      i -= 1;
    }
    localList1.clear();
  }
  
  private void updateFlashState(CaptureResult paramCaptureResult)
  {
    paramCaptureResult = (Integer)paramCaptureResult.get(CaptureResult.FLASH_STATE);
    if (paramCaptureResult == null) {
      return;
    }
    int i = paramCaptureResult.intValue();
    if (i != this.m_PreviousFlashState) {
      switch (i)
      {
      default: 
        Log.e(this.TAG, "onPreviewCaptureCompleted() - Unknow flash state : " + i);
      }
    }
    for (;;)
    {
      this.m_PreviousFlashState = i;
      return;
      Log.v(this.TAG, "onPreviewCaptureCompleted() - Charging");
      continue;
      Log.v(this.TAG, "onPreviewCaptureCompleted() - Fired");
      continue;
      Log.v(this.TAG, "onPreviewCaptureCompleted() - Partial");
      continue;
      Log.v(this.TAG, "onPreviewCaptureCompleted() - Ready");
      if (this.m_PendingFlashMode != null)
      {
        Log.v(this.TAG, "onPreviewCaptureCompleted() - Ready, set pending flash mode : ", this.m_PendingFlashMode);
        setFlashMode(this.m_PendingFlashMode, this.m_PreviewRequestBuilder);
        applyToPreview();
        continue;
        Log.v(this.TAG, "onPreviewCaptureCompleted() - Unavailable");
      }
    }
  }
  
  private void updateFocalLength(CaptureResult paramCaptureResult)
  {
    setReadOnly(PROP_FOCAL_LENGTH, (Float)paramCaptureResult.get(CaptureResult.LENS_FOCAL_LENGTH));
  }
  
  private void updateFocusState(CaptureResult paramCaptureResult)
  {
    Integer localInteger = (Integer)paramCaptureResult.get(CaptureResult.CONTROL_AF_STATE);
    paramCaptureResult = localInteger;
    if (localInteger == null) {
      paramCaptureResult = Integer.valueOf(0);
    }
    int i = this.m_LastRawFocusState;
    this.m_LastRawFocusState = paramCaptureResult.intValue();
    if (this.m_IsAutoFocusStarting)
    {
      switch (paramCaptureResult.intValue())
      {
      }
      do
      {
        do
        {
          do
          {
            return;
            this.m_IsAutoFocusStarting = false;
            getHandler().removeMessages(10011);
          } while ((this.m_IsAutoFocusTimeout) || (!setReadOnly(PROP_FOCUS_STATE, FocusState.SCANNING)));
          getHandler().sendEmptyMessageDelayed(10012, 5000L);
          return;
        } while (i == paramCaptureResult.intValue());
        this.m_IsAutoFocusStarting = false;
        this.m_IsAutoFocusTimeout = false;
        getHandler().removeMessages(10011);
        getHandler().removeMessages(10012);
        setReadOnly(PROP_FOCUS_STATE, FocusState.SCANNING);
        setReadOnly(PROP_FOCUS_STATE, FocusState.FOCUSED);
        return;
      } while (i == paramCaptureResult.intValue());
      this.m_IsAutoFocusStarting = false;
      this.m_IsAutoFocusTimeout = false;
      getHandler().removeMessages(10011);
      getHandler().removeMessages(10012);
      setReadOnly(PROP_FOCUS_STATE, FocusState.SCANNING);
      setReadOnly(PROP_FOCUS_STATE, FocusState.UNFOCUSED);
      return;
    }
    switch (paramCaptureResult.intValue())
    {
    default: 
      Log.w(this.TAG, "updateFocusState() - Unknown AF state : " + paramCaptureResult);
      this.m_IsAutoFocusTimeout = false;
      if (this.m_IsAutoFocusStarting)
      {
        this.m_IsAutoFocusStarting = false;
        getHandler().removeMessages(10011);
      }
      getHandler().removeMessages(10012);
      setReadOnly(PROP_FOCUS_STATE, FocusState.INACTIVE);
      return;
    case 1: 
    case 3: 
      setReadOnly(PROP_FOCUS_STATE, FocusState.SCANNING);
      return;
    case 0: 
      this.m_IsAutoFocusStarting = false;
      this.m_IsAutoFocusTimeout = false;
      getHandler().removeMessages(10011);
      getHandler().removeMessages(10012);
      setReadOnly(PROP_FOCUS_STATE, FocusState.INACTIVE);
      return;
    case 2: 
    case 4: 
      this.m_IsAutoFocusStarting = false;
      this.m_IsAutoFocusTimeout = false;
      getHandler().removeMessages(10011);
      getHandler().removeMessages(10012);
      setReadOnly(PROP_FOCUS_STATE, FocusState.FOCUSED);
      return;
    }
    this.m_IsAutoFocusStarting = false;
    this.m_IsAutoFocusTimeout = false;
    getHandler().removeMessages(10011);
    getHandler().removeMessages(10012);
    setReadOnly(PROP_FOCUS_STATE, FocusState.UNFOCUSED);
  }
  
  private void updatePropertyState(CaptureResult paramCaptureResult)
  {
    updateAEState(paramCaptureResult);
    updateFlashState(paramCaptureResult);
    updateFocusState(paramCaptureResult);
    updateFocalLength(paramCaptureResult);
    if (this.m_IsFaceDetectionEnabled) {
      updateFaceDetectionResult(paramCaptureResult);
    }
  }
  
  public <TArgs extends EventArgs> void addHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    if (paramEventKey == EVENT_PREVIEW_RECEIVED)
    {
      addPreviewReceivedHandler(paramEventHandler);
      return;
    }
    super.addHandler(paramEventKey, paramEventHandler);
  }
  
  public boolean bindMediaRecorder(MediaRecorder paramMediaRecorder, int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    if (get(PROP_CAPTURE_STATE) != OperationState.STOPPED)
    {
      Log.e(this.TAG, "bindMediaRecorder() - Current capture state is " + get(PROP_CAPTURE_STATE));
      return false;
    }
    this.m_MediaRecorder = paramMediaRecorder;
    try
    {
      if (get(PROP_LENS_FACING) == Camera.LensFacing.FRONT) {
        setAudioSourceParams("camerarecorder=facing_front");
      }
      for (;;)
      {
        paramMediaRecorder.setVideoSource(2);
        return true;
        setAudioSourceParams("camerarecorder=facing_back");
      }
      return false;
    }
    catch (Throwable paramMediaRecorder)
    {
      Log.e(this.TAG, "bindMediaRecorder() - Error when set video source", paramMediaRecorder);
      setAudioSourceParams("camerarecorder=none");
      this.m_MediaRecorder = null;
    }
  }
  
  public SizeF calculateViewAngles(Rect paramRect, int paramInt)
  {
    float f2 = ((Float)get(PROP_FOCAL_LENGTH)).floatValue();
    Object localObject = (SizeF)get(PROP_SENSOR_PHYSICAL_SIZE);
    float f1 = (float)(Math.atan(((SizeF)localObject).getWidth() / f2 / 2.0F) * 2.0D / 3.141592653589793D * 180.0D);
    f2 = (float)(Math.atan(((SizeF)localObject).getHeight() / f2 / 2.0F) * 2.0D / 3.141592653589793D * 180.0D);
    if (paramRect == null) {
      return new SizeF(f1, f2);
    }
    localObject = (Size)get(PROP_SENSOR_SIZE_FULL);
    return new SizeF(paramRect.width() / ((Size)localObject).getWidth() * f1, paramRect.height() / ((Size)localObject).getHeight() * f2);
  }
  
  public void cancelAutoFocus(int paramInt)
  {
    verifyAccess();
    if ((this.m_PreviewRequestBuilder != null) && (this.m_CaptureSessionState == OperationState.STARTED))
    {
      Log.v(this.TAG, "cancelAutoFocus()");
      this.m_PreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(2));
    }
    try
    {
      this.m_CaptureSession.capture(this.m_PreviewRequestBuilder.build(), this.m_PreviewCaptureCallback, getHandler());
      this.m_PreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(0));
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "cancelAutoFocus() - Fail to cancel autofocus", localThrowable);
      }
    }
  }
  
  public Handle capture(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0)
    {
      Log.e(this.TAG, "capture() - Invalid frame count : " + paramInt1);
      return null;
    }
    paramInt2 = 0;
    verifyAccess();
    if (get(PROP_CAPTURE_STATE) != OperationState.STOPPED)
    {
      Log.e(this.TAG, "capture() - Capture state is " + get(PROP_CAPTURE_STATE));
      return null;
    }
    switch (-getcom-oneplus-camera-Camera$StateSwitchesValues()[this.m_State.ordinal()])
    {
    default: 
      Log.e(this.TAG, "capture() - Current state is " + this.m_State);
      return null;
    case 4: 
      Log.w(this.TAG, "capture() - Opening camera, capture later");
      paramInt2 = 1;
    }
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "capture() - Preview state is " + get(PROP_PREVIEW_STATE));
      return null;
    case 2: 
      Log.w(this.TAG, "capture() - Starting preview, capture later");
      paramInt2 = 1;
    }
    setReadOnly(PROP_CAPTURE_STATE, OperationState.STARTING);
    this.m_CaptureHandle = new Handle("Capture")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraImpl.-wrap14(CameraImpl.this, true);
      }
    };
    this.m_TargetCapturedFrameCount = paramInt1;
    if ((paramInt2 != 0) || (captureInternal())) {
      return this.m_CaptureHandle;
    }
    return null;
  }
  
  public void close(int paramInt)
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-Camera$StateSwitchesValues()[this.m_State.ordinal()])
    {
    case 3: 
    default: 
      if (changeState(Camera.State.CLOSING) != Camera.State.CLOSING)
      {
        Log.w(this.TAG, "close() - Close process has been interrupted");
        return;
      }
      break;
    case 1: 
    case 2: 
    case 5: 
      return;
    case 4: 
      Log.w(this.TAG, "close() - Close while opening");
      changeState(Camera.State.CLOSING);
      return;
    }
    stopCaptureSession(true);
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_CAPTURE_STATE)).ordinal()])
    {
    }
    for (;;)
    {
      closeInternal();
      return;
      Log.w(this.TAG, "close() - Force stopping capture");
      onCaptureCompleted(false);
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_AE_REGIONS) {
      return this.m_AeRegions;
    }
    if (paramPropertyKey == PROP_AF_REGIONS) {
      return this.m_AfRegions;
    }
    if (paramPropertyKey == PROP_AWB_MODE) {
      return Integer.valueOf(this.m_AwbMode);
    }
    if (paramPropertyKey == PROP_CAMERA_CHARACTERISTICS) {
      return getCameraCharacteristic();
    }
    if (paramPropertyKey == PROP_EXPOSURE_COMPENSATION) {
      return Float.valueOf(this.m_ExposureCompensation);
    }
    if (paramPropertyKey == PROP_EXPOSURE_TIME_NANOS) {
      return Long.valueOf(this.m_ExposureTime);
    }
    if (paramPropertyKey == PROP_FACES) {
      return this.m_FaceLists[this.m_FaceListIndex];
    }
    if (paramPropertyKey == PROP_FLASH_MODE) {
      return this.m_FlashMode;
    }
    if (paramPropertyKey == PROP_FOCUS) {
      return Float.valueOf(this.m_FocusValue);
    }
    if (paramPropertyKey == PROP_FOCUS_MODE) {
      return this.m_FocusMode;
    }
    if (paramPropertyKey == PROP_ID) {
      return this.m_Id;
    }
    if (paramPropertyKey == PROP_IS_AE_LOCKED) {
      return Boolean.valueOf(this.m_IsAELocked);
    }
    if (paramPropertyKey == PROP_IS_AWB_LOCKED) {
      return Boolean.valueOf(this.m_IsAwbLocked);
    }
    if (paramPropertyKey == PROP_IS_FACE_DETECTION_ENABLED) {
      return Boolean.valueOf(this.m_IsFaceDetectionEnabled);
    }
    if (paramPropertyKey == PROP_IS_RAW_CAPTURE_ENABLED) {
      return Boolean.valueOf(this.m_IsRawCaptureEnabled);
    }
    if (paramPropertyKey == PROP_IS_RECORDING_MODE) {
      return Boolean.valueOf(this.m_IsRecordingMode);
    }
    if (paramPropertyKey == PROP_ISO) {
      return Integer.valueOf(this.m_ISOValue);
    }
    if (paramPropertyKey == PROP_JPEG_QUALITY)
    {
      if (this.m_JpegQuality >= 0) {
        return Integer.valueOf(this.m_JpegQuality);
      }
      return (TValue)PROP_JPEG_QUALITY.defaultValue;
    }
    if (paramPropertyKey == PROP_PREVIEW_SIZE) {
      return this.m_PreviewSize;
    }
    if (paramPropertyKey == PROP_PICTURE_SIZE) {
      return this.m_PictureSize;
    }
    if (paramPropertyKey == PROP_PREVIEW_FPS_RANGE) {
      return this.m_PreviewFpsRange;
    }
    if (paramPropertyKey == PROP_SCENE_MODE) {
      return Integer.valueOf(this.m_SceneMode);
    }
    if (paramPropertyKey == PROP_STATE) {
      return this.m_State;
    }
    if (paramPropertyKey == PROP_VIDEO_SIZE) {
      return this.m_VideoSize;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public BokehDebugInfo[] getBokehDebugInfo()
  {
    return new BokehDebugInfo[0];
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10012: 
      onAutoFocusTimeout();
      return;
    case 10011: 
      onAutoFocusStartTimeout();
      return;
    case 10020: 
      Log.e(this.TAG, "handleMessage() - Capture session close timeout");
      onCaptureSessionClosed(this.m_CaptureSession);
      return;
    case 10000: 
      onPreviewFrameReceived();
      return;
    case 10001: 
      Log.e(this.TAG, "handleMessage() - First preview frame timeout");
      onFirstPreviewFrameReceived();
      return;
    case 10010: 
      startAutoFocus();
      return;
    }
    Log.e(this.TAG, "handleMessage() - Pre-capture flash timeout");
    this.m_IsPreCaptureFlashTimeout = true;
    onPreCaptureFlashComplete();
  }
  
  protected void onRelease()
  {
    if (this.m_State == Camera.State.CLOSED) {
      changeState(Camera.State.UNAVAILABLE);
    }
    super.onRelease();
  }
  
  public boolean open(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    switch (-getcom-oneplus-camera-Camera$StateSwitchesValues()[this.m_State.ordinal()])
    {
    default: 
      Log.e(this.TAG, "open() - Invalid state : " + this.m_State);
      return false;
    case 3: 
    case 4: 
      return true;
    case 2: 
      Log.w(this.TAG, "open() - Open while closing");
      return changeState(Camera.State.OPENING) == Camera.State.OPENING;
    }
    this.m_OpenCameraStartTime = SystemClock.elapsedRealtime();
    if (!openInternal(paramInt)) {
      return false;
    }
    return changeState(Camera.State.OPENING) == Camera.State.OPENING;
  }
  
  public boolean pauseVideoRecording(int paramInt)
  {
    verifyAccess();
    if (this.m_MediaRecorder == null)
    {
      Log.e(this.TAG, "pauseVideoRecording() - No media recorder");
      return false;
    }
    try
    {
      Log.v(this.TAG, "pauseVideoRecording()");
      this.m_MediaRecorder.pause();
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "pauseVideoRecording() - Fail to pause", localThrowable);
    }
    return false;
  }
  
  public <TArgs extends EventArgs> void removeHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    if (paramEventKey == EVENT_PREVIEW_RECEIVED)
    {
      removePreviewReceivedHandler(paramEventHandler);
      return;
    }
    super.removeHandler(paramEventKey, paramEventHandler);
  }
  
  public boolean resumeVideoRecording(int paramInt)
  {
    verifyAccess();
    if (this.m_MediaRecorder == null)
    {
      Log.e(this.TAG, "resumeVideoRecording() - No media recorder");
      return false;
    }
    try
    {
      Log.v(this.TAG, "resumeVideoRecording()");
      this.m_MediaRecorder.resume();
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "resumeVideoRecording() - Fail to resume", localThrowable);
    }
    return false;
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_AE_REGIONS) {
      return setAERegionsProp((List)paramTValue);
    }
    if (paramPropertyKey == PROP_AF_REGIONS) {
      return setAFRegionsProp((List)paramTValue);
    }
    if (paramPropertyKey == PROP_AWB_MODE) {
      return setAWBModeProp(((Integer)paramTValue).intValue());
    }
    if (paramPropertyKey == PROP_DIGITAL_ZOOM) {
      setDigitalZoomProp(((Float)paramTValue).floatValue());
    }
    if (paramPropertyKey == PROP_EXPOSURE_COMPENSATION) {
      return setExposureCompensationProp(((Float)paramTValue).floatValue());
    }
    if (paramPropertyKey == PROP_EXPOSURE_TIME_NANOS) {
      return setExposureTimeProp(((Long)paramTValue).longValue());
    }
    if (paramPropertyKey == PROP_FLASH_MODE) {
      return setFlashModeProp((FlashMode)paramTValue);
    }
    if (paramPropertyKey == PROP_FOCUS) {
      return setFocusProp(((Float)paramTValue).floatValue());
    }
    if (paramPropertyKey == PROP_FOCUS_MODE) {
      return setFocusModeProp((FocusMode)paramTValue);
    }
    if (paramPropertyKey == PROP_IS_AE_LOCKED) {
      return setAELockedProp(((Boolean)paramTValue).booleanValue());
    }
    if (paramPropertyKey == PROP_IS_AWB_LOCKED) {
      return setAwbLockedProp(((Boolean)paramTValue).booleanValue());
    }
    if (paramPropertyKey == PROP_IS_FACE_DETECTION_ENABLED) {
      return setFaceDetectionProp(((Boolean)paramTValue).booleanValue());
    }
    if (paramPropertyKey == PROP_ISO) {
      return setISOProp(((Integer)paramTValue).intValue());
    }
    if (paramPropertyKey == PROP_IS_RAW_CAPTURE_ENABLED) {
      return setRawCaptureProp(((Boolean)paramTValue).booleanValue());
    }
    if (paramPropertyKey == PROP_IS_RECORDING_MODE) {
      return setRecordingModeProp(((Boolean)paramTValue).booleanValue());
    }
    if (paramPropertyKey == PROP_JPEG_QUALITY) {
      return setJpegQualityProp(((Integer)paramTValue).intValue());
    }
    if (paramPropertyKey == PROP_PICTURE_SIZE) {
      return setPictureSize((Size)paramTValue);
    }
    if (paramPropertyKey == PROP_PREVIEW_FPS_RANGE) {
      return setPreviewFpsRangeProp((Range)paramTValue);
    }
    if (paramPropertyKey == PROP_PREVIEW_SIZE) {
      return setPreviewSizeProp((Size)paramTValue);
    }
    if (paramPropertyKey == PROP_PREVIEW_RECEIVER) {
      return setPreviewReceiver(paramTValue);
    }
    if (paramPropertyKey == PROP_SCENE_MODE) {
      return setSceneModeProp(((Integer)paramTValue).intValue());
    }
    if (paramPropertyKey == PROP_VIDEO_SIZE) {
      return setVideoSizeProp((Size)paramTValue);
    }
    return super.set(paramPropertyKey, paramTValue);
  }
  
  public boolean startAutoFocus(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    if (get(PROP_PREVIEW_STATE) != OperationState.STARTED)
    {
      Log.w(this.TAG, "startAutoFocus() - Preview state is " + get(PROP_PREVIEW_STATE));
      return false;
    }
    if (!getHandler().hasMessages(10010)) {
      getHandler().sendEmptyMessage(10010);
    }
    return true;
  }
  
  public boolean startPreview(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    if ((this.m_State != Camera.State.OPENED) && (this.m_State != Camera.State.OPENING))
    {
      Log.e(this.TAG, "startPreview() - Camera state is " + this.m_State);
      return false;
    }
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    case 3: 
    default: 
      if (this.m_State != Camera.State.OPENED) {
        break label221;
      }
      if (this.m_CaptureSessionState == OperationState.STARTED)
      {
        setReadOnly(PROP_PREVIEW_STATE, OperationState.STARTING);
        return startPreviewRequest();
      }
      break;
    case 1: 
    case 2: 
      return true;
    case 4: 
      if (this.m_CaptureSessionState == OperationState.STARTED)
      {
        Log.d(this.TAG, "startPreview() - Start on started.");
        setReadOnly(PROP_PREVIEW_STATE, OperationState.STARTED);
        return true;
      }
      Log.w(this.TAG, "startPreview() - Start while stopping");
      setReadOnly(PROP_PREVIEW_STATE, OperationState.STARTING);
      return true;
    }
    if (!startCaptureSession())
    {
      Log.e(this.TAG, "startPreview() - Fail to start capture session");
      return false;
      label221:
      Log.w(this.TAG, "startPreview() - Start preview while opening camera");
    }
    setReadOnly(PROP_PREVIEW_STATE, OperationState.STARTING);
    return true;
  }
  
  /* Error */
  public boolean startVideoRecording(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 1727	com/oneplus/camera/CameraImpl:verifyAccess	()V
    //   4: aload_0
    //   5: invokevirtual 1767	com/oneplus/camera/CameraImpl:verifyReleaseState	()V
    //   8: aload_0
    //   9: getfield 2236	com/oneplus/camera/CameraImpl:m_MediaRecorder	Landroid/media/MediaRecorder;
    //   12: ifnonnull +15 -> 27
    //   15: aload_0
    //   16: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   19: ldc_w 2451
    //   22: invokestatic 828	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   25: iconst_0
    //   26: ireturn
    //   27: invokestatic 1282	com/oneplus/camera/CameraImpl:-getcom-oneplus-camera-OperationStateSwitchesValues	()[I
    //   30: aload_0
    //   31: getstatic 686	com/oneplus/camera/CameraImpl:PROP_PREVIEW_STATE	Lcom/oneplus/base/PropertyKey;
    //   34: invokevirtual 545	com/oneplus/camera/CameraImpl:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   37: checkcast 302	com/oneplus/camera/OperationState
    //   40: invokevirtual 309	com/oneplus/camera/OperationState:ordinal	()I
    //   43: iaload
    //   44: tableswitch	default:+24->68, 1:+66->110, 2:+66->110
    //   68: iconst_0
    //   69: istore_1
    //   70: aload_0
    //   71: aload_0
    //   72: getfield 2236	com/oneplus/camera/CameraImpl:m_MediaRecorder	Landroid/media/MediaRecorder;
    //   75: invokevirtual 2452	android/media/MediaRecorder:getSurface	()Landroid/view/Surface;
    //   78: putfield 899	com/oneplus/camera/CameraImpl:m_VideoSurface	Landroid/view/Surface;
    //   81: iload_1
    //   82: ifeq +19 -> 101
    //   85: aload_0
    //   86: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   89: ldc_w 2454
    //   92: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   95: aload_0
    //   96: iconst_0
    //   97: invokevirtual 1958	com/oneplus/camera/CameraImpl:startPreview	(I)Z
    //   100: pop
    //   101: aload_0
    //   102: getfield 2236	com/oneplus/camera/CameraImpl:m_MediaRecorder	Landroid/media/MediaRecorder;
    //   105: invokevirtual 2457	android/media/MediaRecorder:start	()V
    //   108: iconst_1
    //   109: ireturn
    //   110: aload_0
    //   111: getfield 805	com/oneplus/camera/CameraImpl:m_IsRecordingMode	Z
    //   114: ifeq +23 -> 137
    //   117: aload_0
    //   118: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   121: ldc_w 2459
    //   124: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   127: aload_0
    //   128: iconst_0
    //   129: invokevirtual 1966	com/oneplus/camera/CameraImpl:stopPreview	(I)V
    //   132: iconst_1
    //   133: istore_1
    //   134: goto -64 -> 70
    //   137: aload_0
    //   138: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   141: ldc_w 2461
    //   144: invokestatic 531	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   147: iconst_0
    //   148: istore_1
    //   149: goto -79 -> 70
    //   152: astore_2
    //   153: aload_0
    //   154: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   157: ldc_w 2463
    //   160: aload_2
    //   161: invokestatic 992	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   164: iconst_0
    //   165: ireturn
    //   166: astore_2
    //   167: aload_0
    //   168: getfield 198	com/oneplus/camera/CameraImpl:TAG	Ljava/lang/String;
    //   171: ldc_w 2465
    //   174: aload_2
    //   175: invokestatic 992	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   178: aload_0
    //   179: ldc_w 2251
    //   182: invokespecial 2240	com/oneplus/camera/CameraImpl:setAudioSourceParams	(Ljava/lang/String;)V
    //   185: iconst_0
    //   186: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	187	0	this	CameraImpl
    //   0	187	1	paramInt	int
    //   152	9	2	localThrowable1	Throwable
    //   166	9	2	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   70	81	152	java/lang/Throwable
    //   101	108	166	java/lang/Throwable
  }
  
  public void stopPreview(int paramInt)
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_PREVIEW_STATE)).ordinal()])
    {
    case 1: 
    default: 
      setReadOnly(PROP_PREVIEW_STATE, OperationState.STOPPING);
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_CAPTURE_STATE)).ordinal()])
      {
      }
      break;
    }
    for (;;)
    {
      stopCaptureSession(false);
      return;
      return;
      Log.w(this.TAG, "stopPreview() - Stop while starting");
      break;
      Log.w(this.TAG, "stopPreview() - Wait for capture stop");
      return;
      Log.w(this.TAG, "stopPreview() - Cancel capture");
      stopCaptureInternal(true);
      continue;
      Log.w(this.TAG, "stopPreview() - Stop capture and wait for stop");
      stopCaptureInternal(true);
      if (get(PROP_CAPTURE_STATE) != OperationState.STOPPED) {
        return;
      }
      Log.w(this.TAG, "stopPreview() - Capture stopped immediately");
    }
  }
  
  public boolean stopVideoRecording(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    if (this.m_MediaRecorder == null)
    {
      Log.w(this.TAG, "stopVideoRecording() - No media recorder");
      return false;
    }
    try
    {
      stopPreview(0);
      Log.w(this.TAG, "stopVideoRecording() - MediaRecorder.stop [start]");
      this.m_MediaRecorder.stop();
      Log.w(this.TAG, "stopVideoRecording() - MediaRecorder.stop [end]");
      return true;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "stopVideoRecording() - Fail to stop recorder", localThrowable);
      return false;
    }
    finally
    {
      setAudioSourceParams("camerarecorder=none");
    }
  }
  
  public String toString()
  {
    return "Camera2[ID=" + this.m_Id + ", Facing=" + get(PROP_LENS_FACING) + "]";
  }
  
  public boolean unbindMediaRecorder(int paramInt)
  {
    verifyAccess();
    verifyReleaseState();
    this.m_MediaRecorder = null;
    this.m_VideoSurface = null;
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */