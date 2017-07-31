package com.oneplus.camera;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Range;
import android.util.Rational;
import android.view.MotionEvent;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.capturemode.PhotoCaptureMode;
import com.oneplus.camera.location.LocationManager;
import com.oneplus.camera.manual.ExposureTimeKnobView;
import com.oneplus.camera.manual.ISOKnobView;
import com.oneplus.camera.manual.ManualCaptureMode;
import com.oneplus.camera.manual.ManualModeUI;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.camera.scene.AutoHdrScene;
import com.oneplus.camera.scene.ClearShot;
import com.oneplus.camera.scene.HdrScene;
import com.oneplus.camera.scene.Scene;
import com.oneplus.camera.scene.SceneManager;
import com.oneplus.camera.ui.CameraPreviewGrid;
import com.oneplus.camera.ui.CameraPreviewGrid.GridType;
import com.oneplus.camera.ui.CameraPreviewOverlay;
import com.oneplus.camera.ui.GestureDetector;
import com.oneplus.camera.ui.TouchAutoFocusUI;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AutoTestService
  extends Service
{
  public static final String ACTION_LOCK_FOCUS = "LockFocus";
  public static final String ACTION_SLIDE_DOWN = "SlideDown";
  public static final String ACTION_SLIDE_LEFT = "SlideLeft";
  public static final String ACTION_SLIDE_RIGHT = "SlideRight";
  public static final String ACTION_SLIDE_UP = "SlideUp";
  public static final String ACTION_START_PHOTO_CAPTURE = "StartPhotoCapture";
  public static final String ACTION_START_VIDEO_CAPTURE = "StartVideoCapture";
  public static final String ACTION_STOP_PHOTO_CAPTURE = "StopPhotoCapture";
  public static final String ACTION_STOP_VIDEO_CAPTURE = "StopVideoCapture";
  public static final String ACTION_UNLOCK_FOCUS = "UnLockFocus";
  public static final int FLAG_BURST = 1;
  private static final Map<Integer, WeakReference<AutoTestService>> INSTANCES = new ConcurrentHashMap();
  public static final int MSG_UPDATE_BURST_CAPTURE_HANDLE_LIST = 10001;
  private static volatile int NEXT_ID = 1;
  public static final String START_MODE_NORMAL = "Normal";
  public static final String START_MODE_PHOTO = "Photo";
  public static final String START_MODE_VIDEO = "Video";
  public static final String STATE_KEY_AVAILABLE_AWB_VALUES = "AvailableWb";
  public static final String STATE_KEY_AVAILABLE_CAMERA_LENS_FACING = "AvailableCameraLensFacings";
  public static final String STATE_KEY_AVAILABLE_CAPTURE_MODES = "AvailableCaptureModes";
  public static final String STATE_KEY_AVAILABLE_EXPOSURE_TIMES = "AvailableExposureTimes";
  public static final String STATE_KEY_AVAILABLE_FACE_BEAUTY_VALUES = "AvailableFaceBeautyValues";
  public static final String STATE_KEY_AVAILABLE_FLASH_MODES = "AvailableFlashModes";
  public static final String STATE_KEY_AVAILABLE_FOCUS_VALUES = "AvailableFocusValues";
  public static final String STATE_KEY_AVAILABLE_ISO_VALUES = "AvailableIsoValues";
  public static final String STATE_KEY_AVAILABLE_PHOTO_SIZES = "AvailablePhotoSizes";
  public static final String STATE_KEY_AVAILABLE_SCENES = "AvailableScenes";
  public static final String STATE_KEY_AVAILABLE_VIDEO_SIZES = "AvailableVideoSizes";
  public static final String STATE_KEY_AWB = "Awb";
  public static final String STATE_KEY_CAMERA_LENS_FACING = "CameraLensFacing";
  public static final String STATE_KEY_CAMERA_PREVIEW_STATE = "CameraPreviewState";
  public static final String STATE_KEY_CAPTURE_MODE = "CaptureMode";
  public static final String STATE_KEY_DIGITAL_ZOOM = "DigitalZoom";
  public static final String STATE_KEY_EXPOSURE = "Exposure";
  public static final String STATE_KEY_EXPOSURE_COMPENSATION = "ExposureCompensataion";
  public static final String STATE_KEY_FACE_BEAUTY_VALUE = "FaceBeautyValue";
  public static final String STATE_KEY_FLASH_MODES = "FlashMode";
  public static final String STATE_KEY_FOCUS = "Focus";
  public static final String STATE_KEY_FOCUS_STATE = "FocusState";
  public static final String STATE_KEY_ISO = "Iso";
  public static final String STATE_KEY_IS_FACE_BEAUTY_ENABLED = "IsFaceBeautyEnabled";
  public static final String STATE_KEY_IS_GRID_VISIBLE = "IsGridVisible";
  public static final String STATE_KEY_IS_MIRRORED = "IsMirrored";
  public static final String STATE_KEY_IS_RAW_ENABLED = "IsRawEnabled";
  public static final String STATE_KEY_IS_READY = "IsReady";
  public static final String STATE_KEY_IS_SAVING_LOCATION = "IsSavingLocation";
  public static final String STATE_KEY_IS_SAVING_MEDIA = "IsSavingMedia";
  public static final String STATE_KEY_IS_SHUTTER_SOUND_NEEDED = "IsShutterSoundNeeded";
  public static final String STATE_KEY_IS_SMILE_CAPTURE_ENABLED = "IsSmileCaptureEnabled";
  public static final String STATE_KEY_LAST_SAVED_MEDIA = "LastSavedMedia";
  public static final String STATE_KEY_MAX_DIGITAL_ZOOM = "MaxDigitalZoom";
  public static final String STATE_KEY_PHOTO_CAPTURE_STATE = "PhotoCaptureState";
  public static final String STATE_KEY_PHOTO_SIZE = "PhotoSize";
  public static final String STATE_KEY_SCENE = "Scene";
  public static final String STATE_KEY_SELF_TIMER_INTERVAL = "SelfTimerInterval";
  public static final String STATE_KEY_VIDEO_CAPTURE_STATE = "VideoCaptureState";
  public static final String STATE_KEY_VIDEO_SIZE = "VideoSize";
  private static final String TAG = "CameraAutoTestService";
  private final PropertyChangedCallback<BaseActivity.State> m_ActivityStateChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
    {
      AutoTestService.-wrap16(AutoTestService.this, (BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final IAutoTestService.Stub m_Binder = new IAutoTestService.Stub()
  {
    public boolean getBooleanState(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws RemoteException
    {
      return AutoTestService.-wrap0(AutoTestService.this, paramAnonymousString, paramAnonymousBoolean);
    }
    
    public float getFloatState(String paramAnonymousString, float paramAnonymousFloat)
      throws RemoteException
    {
      return AutoTestService.-wrap10(AutoTestService.this, paramAnonymousString, paramAnonymousFloat);
    }
    
    public int getIntState(String paramAnonymousString, int paramAnonymousInt)
      throws RemoteException
    {
      return AutoTestService.-wrap11(AutoTestService.this, paramAnonymousString, paramAnonymousInt);
    }
    
    public long getLongState(String paramAnonymousString, long paramAnonymousLong)
      throws RemoteException
    {
      return AutoTestService.-wrap13(AutoTestService.this, paramAnonymousString, paramAnonymousLong);
    }
    
    public String getStringState(String paramAnonymousString1, String paramAnonymousString2)
      throws RemoteException
    {
      return AutoTestService.-wrap12(AutoTestService.this, paramAnonymousString1, paramAnonymousString2);
    }
    
    public boolean isActivityAttached()
      throws RemoteException
    {
      return AutoTestService.-get13(AutoTestService.this);
    }
    
    public boolean performAction(String paramAnonymousString, int paramAnonymousInt)
      throws RemoteException
    {
      return AutoTestService.-wrap1(AutoTestService.this, paramAnonymousString, paramAnonymousInt);
    }
    
    public boolean setBooleanState(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws RemoteException
    {
      return AutoTestService.-wrap2(AutoTestService.this, paramAnonymousString, paramAnonymousBoolean);
    }
    
    public boolean setFloatState(String paramAnonymousString, float paramAnonymousFloat)
      throws RemoteException
    {
      return AutoTestService.-wrap3(AutoTestService.this, paramAnonymousString, paramAnonymousFloat);
    }
    
    public boolean setIntState(String paramAnonymousString, int paramAnonymousInt)
      throws RemoteException
    {
      return AutoTestService.-wrap4(AutoTestService.this, paramAnonymousString, paramAnonymousInt);
    }
    
    public boolean setLongState(String paramAnonymousString, long paramAnonymousLong)
      throws RemoteException
    {
      return AutoTestService.-wrap5(AutoTestService.this, paramAnonymousString, paramAnonymousLong);
    }
    
    public boolean setStringState(String paramAnonymousString1, String paramAnonymousString2)
      throws RemoteException
    {
      return AutoTestService.-wrap6(AutoTestService.this, paramAnonymousString1, paramAnonymousString2);
    }
    
    public boolean start(String paramAnonymousString, int paramAnonymousInt)
      throws RemoteException
    {
      return AutoTestService.-wrap9(AutoTestService.this, paramAnonymousString, paramAnonymousInt);
    }
    
    public boolean startAutoFocus(float paramAnonymousFloat1, float paramAnonymousFloat2)
      throws RemoteException
    {
      return AutoTestService.-wrap7(AutoTestService.this, paramAnonymousFloat1, paramAnonymousFloat2);
    }
    
    public boolean startCameraActivity(String paramAnonymousString, int paramAnonymousInt)
      throws RemoteException
    {
      return AutoTestService.-wrap8(AutoTestService.this, paramAnonymousString, paramAnonymousInt);
    }
    
    public void stop()
      throws RemoteException
    {
      AutoTestService.-wrap21(AutoTestService.this);
    }
  };
  private List<CaptureHandle> m_BurstCaptureHandleList = new ArrayList();
  private HashMap<CaptureHandle, BurstInfo> m_BurstCaptureInfoMap = new HashMap();
  private final EventHandler<CaptureEventArgs> m_BurstPhotoReceivedCB = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
    {
      AutoTestService.-wrap17(AutoTestService.this, paramAnonymousCaptureEventArgs);
    }
  };
  private volatile OPCameraActivity m_CameraActivity;
  private CameraPreviewGrid m_CameraPreviewGrid;
  private CameraPreviewOverlay m_CameraPreviewOverlay;
  private List<CaptureHandle> m_CaptureHandleList = new ArrayList();
  private CaptureModeManager m_CaptureModeManager;
  private ExposureController m_ExposureController;
  private FaceBeautyController m_FaceBeautyController;
  private FlashController m_FlashController;
  private FocusController m_FocusController;
  private Handle m_FocusLockHandle;
  private GestureDetector m_GestureDetector;
  private Handler m_Handler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        super.handleMessage(paramAnonymousMessage);
      }
      do
      {
        return;
        paramAnonymousMessage = (CaptureHandle)paramAnonymousMessage.obj;
        localObject = (AutoTestService.BurstInfo)AutoTestService.-get1(AutoTestService.this).get(paramAnonymousMessage);
      } while ((localObject == null) || (!((AutoTestService.BurstInfo)localObject).isFinished()));
      Object localObject = AutoTestService.-get0(AutoTestService.this).iterator();
      while (((Iterator)localObject).hasNext())
      {
        CaptureHandle localCaptureHandle = (CaptureHandle)((Iterator)localObject).next();
        if (localCaptureHandle.getInternalCaptureHandle().equals(paramAnonymousMessage)) {
          AutoTestService.-get0(AutoTestService.this).remove(localCaptureHandle);
        }
      }
      AutoTestService.-get1(AutoTestService.this).remove(paramAnonymousMessage);
    }
  };
  private final int m_Id;
  private volatile boolean m_IsActivityAttached;
  private volatile boolean m_IsStartingActivity;
  private MediaEventArgs m_LastSavedMedia;
  private LocationManager m_LocationManager;
  private final Object m_Lock = new Object();
  private final EventHandler<MediaEventArgs> m_MediaSavedCB = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
    {
      AutoTestService.-wrap20(AutoTestService.this, paramAnonymousMediaEventArgs);
    }
  };
  private final EventHandler<MediaEventArgs> m_MediaSavedCancelledCB = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
    {
      AutoTestService.-wrap18(AutoTestService.this, paramAnonymousMediaEventArgs);
    }
  };
  private final EventHandler<MediaEventArgs> m_MediaSavedFailedCB = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
    {
      AutoTestService.-wrap19(AutoTestService.this, paramAnonymousMediaEventArgs);
    }
  };
  private CaptureHandle m_PhotoCaptureHandle;
  private ResolutionManager m_ResolutionManager;
  private SceneManager m_SceneManager;
  private Settings m_Settings;
  private SmileCaptureController m_SmileCaptureController;
  private Resolution m_TargetResolution;
  private CaptureHandle m_VideoCaptureHandle;
  private ZoomController m_ZoomController;
  
  public AutoTestService()
  {
    try
    {
      int i = NEXT_ID;
      NEXT_ID = i + 1;
      this.m_Id = i;
      INSTANCES.put(Integer.valueOf(this.m_Id), new WeakReference(this));
      return;
    }
    finally {}
  }
  
  private void attachToActivity(final CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity == null) {
      return;
    }
    if (!paramCameraActivity.isDependencyThread())
    {
      HandlerUtils.post(paramCameraActivity, new Runnable()
      {
        public void run()
        {
          AutoTestService.-wrap14(AutoTestService.this, paramCameraActivity);
        }
      });
      return;
    }
    Log.v("CameraAutoTestService", "attachToActivity()");
    paramCameraActivity.addCallback(CameraActivity.PROP_STATE, this.m_ActivityStateChangedCB);
    paramCameraActivity.addHandler(CameraActivity.EVENT_BURST_PHOTO_RECEIVED, this.m_BurstPhotoReceivedCB);
    paramCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVED, this.m_MediaSavedCB);
    paramCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVE_CANCELLED, this.m_MediaSavedCancelledCB);
    paramCameraActivity.addHandler(CameraActivity.EVENT_MEDIA_SAVE_FAILED, this.m_MediaSavedFailedCB);
    this.m_Settings = new Settings(paramCameraActivity, null, true);
    this.m_CameraPreviewGrid = ((CameraPreviewGrid)paramCameraActivity.findComponent(CameraPreviewGrid.class));
    this.m_CameraPreviewOverlay = ((CameraPreviewOverlay)paramCameraActivity.findComponent(CameraPreviewOverlay.class));
    this.m_CaptureModeManager = ((CaptureModeManager)paramCameraActivity.findComponent(CaptureModeManager.class));
    this.m_ExposureController = ((ExposureController)paramCameraActivity.findComponent(ExposureController.class));
    this.m_FaceBeautyController = ((FaceBeautyController)paramCameraActivity.findComponent(FaceBeautyController.class));
    this.m_FlashController = ((FlashController)paramCameraActivity.findComponent(FlashController.class));
    this.m_FocusController = ((FocusController)paramCameraActivity.findComponent(FocusController.class));
    this.m_GestureDetector = ((GestureDetector)paramCameraActivity.findComponent(GestureDetector.class));
    this.m_LocationManager = ((LocationManager)paramCameraActivity.findComponent(LocationManager.class));
    this.m_ResolutionManager = ((ResolutionManager)paramCameraActivity.findComponent(ResolutionManager.class));
    this.m_SceneManager = ((SceneManager)paramCameraActivity.findComponent(SceneManager.class));
    this.m_SmileCaptureController = ((SmileCaptureController)paramCameraActivity.findComponent(SmileCaptureController.class));
    this.m_ZoomController = ((ZoomController)paramCameraActivity.findComponent(ZoomController.class));
    this.m_IsActivityAttached = true;
  }
  
  private void detachFromActivity(final CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity == null) {
      return;
    }
    if (!paramCameraActivity.isDependencyThread())
    {
      HandlerUtils.post(paramCameraActivity, new Runnable()
      {
        public void run()
        {
          AutoTestService.-wrap15(AutoTestService.this, paramCameraActivity);
        }
      });
      return;
    }
    Log.v("CameraAutoTestService", "detachFromActivity()");
    paramCameraActivity.removeCallback(CameraActivity.PROP_STATE, this.m_ActivityStateChangedCB);
    paramCameraActivity.removeHandler(CameraActivity.EVENT_BURST_PHOTO_RECEIVED, this.m_BurstPhotoReceivedCB);
    paramCameraActivity.removeHandler(CameraActivity.EVENT_MEDIA_SAVED, this.m_MediaSavedCB);
    paramCameraActivity.removeHandler(CameraActivity.EVENT_MEDIA_SAVE_CANCELLED, this.m_MediaSavedCancelledCB);
    paramCameraActivity.removeHandler(CameraActivity.EVENT_MEDIA_SAVE_FAILED, this.m_MediaSavedFailedCB);
    this.m_BurstCaptureHandleList.clear();
    this.m_CaptureHandleList.clear();
    this.m_LastSavedMedia = null;
    this.m_IsActivityAttached = false;
  }
  
  static AutoTestService fromId(int paramInt)
  {
    Object localObject = (WeakReference)INSTANCES.get(Integer.valueOf(paramInt));
    if (localObject != null)
    {
      localObject = (AutoTestService)((WeakReference)localObject).get();
      if (localObject != null) {
        return (AutoTestService)localObject;
      }
      INSTANCES.remove(Integer.valueOf(paramInt));
    }
    return null;
  }
  
  private boolean getBooleanState(String paramString, boolean paramBoolean)
    throws RemoteException
  {
    boolean bool1 = true;
    paramBoolean = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool2 = false;
    if (paramString == null) {
      throw new RemoteException("No state key");
    }
    if (paramString.equals("IsFaceBeautyEnabled"))
    {
      if ((this.m_FaceBeautyController != null) && (((Boolean)this.m_FaceBeautyController.get(FaceBeautyController.PROP_IS_SUPPORTED)).booleanValue())) {
        return ((Boolean)this.m_FaceBeautyController.get(FaceBeautyController.PROP_IS_ACTIVATED)).booleanValue();
      }
    }
    else
    {
      if (paramString.equals("IsGridVisible"))
      {
        paramBoolean = bool2;
        if (this.m_CameraPreviewGrid != null)
        {
          paramBoolean = bool2;
          if (this.m_CameraPreviewGrid.get(CameraPreviewGrid.PROP_GRID_TYPE) != CameraPreviewGrid.GridType.NONE) {
            paramBoolean = true;
          }
        }
        return paramBoolean;
      }
      if (paramString.equals("IsMirrored"))
      {
        if (this.m_Settings != null) {
          paramBoolean = this.m_Settings.getBoolean("IsMirrored");
        }
        return paramBoolean;
      }
      if (paramString.equals("IsRawEnabled"))
      {
        if ((this.m_CaptureModeManager != null) && (this.m_CameraActivity != null)) {
          break label357;
        }
        return false;
      }
      if (paramString.equals("IsReady"))
      {
        if (this.m_CameraActivity == null) {
          break label423;
        }
        return true;
      }
      if (paramString.equals("IsSavingLocation"))
      {
        paramBoolean = bool3;
        if (this.m_LocationManager != null) {
          paramBoolean = ((Boolean)this.m_LocationManager.get(LocationManager.PROP_IS_LOCATION_LISTENER_STARTED)).booleanValue();
        }
        return paramBoolean;
      }
      if (paramString.equals("IsSavingMedia"))
      {
        paramBoolean = bool1;
        if (this.m_CaptureHandleList.isEmpty())
        {
          paramBoolean = bool1;
          if (this.m_BurstCaptureHandleList.isEmpty()) {
            paramBoolean = false;
          }
        }
        return paramBoolean;
      }
      if (paramString.equals("IsShutterSoundNeeded"))
      {
        paramBoolean = bool4;
        if (this.m_Settings != null) {
          paramBoolean = this.m_Settings.getBoolean("ShutterSound");
        }
        return paramBoolean;
      }
      if (!paramString.equals("IsSmileCaptureEnabled")) {
        break label425;
      }
      paramBoolean = bool5;
      if (this.m_SmileCaptureController != null) {
        paramBoolean = ((Boolean)this.m_SmileCaptureController.get(SmileCaptureController.PROP_IS_SMILE_CAPTURE_ENABLED)).booleanValue();
      }
      return paramBoolean;
    }
    return false;
    label357:
    paramString = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
    if (((paramString instanceof PhotoCaptureMode)) || ((paramString instanceof ManualCaptureMode))) {
      return (this.m_Settings != null) && (this.m_Settings.getBoolean("RawCapture")) && (!this.m_CameraActivity.isServiceMode());
    }
    return false;
    label423:
    return false;
    label425:
    return false;
  }
  
  private float getFloatState(String paramString, float paramFloat)
    throws RemoteException
  {
    if (paramString.equals("DigitalZoom"))
    {
      if (this.m_ZoomController == null) {
        return paramFloat;
      }
    }
    else
    {
      if (paramString.equals("ExposureCompensataion"))
      {
        if (this.m_ExposureController != null) {
          break label91;
        }
        return paramFloat;
      }
      if (paramString.equals("Focus"))
      {
        if (this.m_CameraActivity != null) {
          break label159;
        }
        return paramFloat;
      }
      if (!paramString.equals("MaxDigitalZoom")) {
        return paramFloat;
      }
      if (this.m_ZoomController != null) {
        break label193;
      }
      return paramFloat;
    }
    return ((Float)this.m_ZoomController.get(ZoomController.PROP_DIGITAL_ZOOM)).floatValue();
    label91:
    paramFloat = ((Float)this.m_ExposureController.get(ExposureController.PROP_EXPOSURE_COMPENSATION)).floatValue();
    paramString = (Range)this.m_ExposureController.get(ExposureController.PROP_EXPOSURE_COMPENSATION_RANGE);
    if (paramFloat >= 0.0F) {
      return paramFloat / ((Float)paramString.getUpper()).floatValue();
    }
    return -paramFloat / ((Float)paramString.getLower()).floatValue();
    label159:
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null)
    {
      Log.w("CameraAutoTestService", "getIntState() - manualModeUi is null");
      return paramFloat;
    }
    return paramString.getFocus();
    label193:
    return ((Float)this.m_ZoomController.get(ZoomController.PROP_MAX_DIGITAL_ZOOM)).floatValue();
    return paramFloat;
  }
  
  private int getIntState(String paramString, int paramInt)
    throws RemoteException
  {
    if (paramString.equals("Awb"))
    {
      if (this.m_CameraActivity == null) {
        return paramInt;
      }
    }
    else
    {
      if (paramString.equals("Iso"))
      {
        if (this.m_CameraActivity != null) {
          break label88;
        }
        return paramInt;
      }
      if (!paramString.equals("FaceBeautyValue")) {
        return paramInt;
      }
      if (this.m_CameraActivity != null) {
        break label122;
      }
      return paramInt;
    }
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null)
    {
      Log.w("CameraAutoTestService", "getIntState() - manualModeUi is null");
      return paramInt;
    }
    return paramString.getAwb();
    label88:
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null)
    {
      Log.w("CameraAutoTestService", "getIntState() - manualModeUi is null");
      return paramInt;
    }
    return paramString.getIso();
    label122:
    paramString = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (paramString != null) {
      return ((Integer)paramString.get(Camera.PROP_FACE_BEAUTY_VALUE)).intValue();
    }
    return paramInt;
    return paramInt;
  }
  
  private long getLongState(String paramString, long paramLong)
    throws RemoteException
  {
    if (paramString.equals("Exposure"))
    {
      if (this.m_CameraActivity == null) {
        return paramLong;
      }
    }
    else
    {
      if (!paramString.equals("SelfTimerInterval")) {
        return paramLong;
      }
      if (this.m_CameraActivity != null) {
        break label70;
      }
      return paramLong;
    }
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null)
    {
      Log.w("CameraAutoTestService", "getLongState() - manualModeUi is null");
      return paramLong;
    }
    return paramString.getExposure();
    label70:
    return ((Long)this.m_CameraActivity.get(CameraActivity.PROP_SELF_TIMER_INTERVAL)).longValue();
    return paramLong;
  }
  
  private String getStringState(String paramString1, String paramString2)
    throws RemoteException
  {
    if (paramString1.equals("AvailableCameraLensFacings"))
    {
      if (this.m_CameraActivity == null) {
        return paramString2;
      }
    }
    else
    {
      if (paramString1.equals("AvailableCaptureModes"))
      {
        if (this.m_CaptureModeManager != null) {
          break label482;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailableExposureTimes"))
      {
        if (this.m_CameraActivity != null) {
          break label561;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailableFaceBeautyValues"))
      {
        if (this.m_CameraActivity != null) {
          break label786;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailableFlashModes"))
      {
        if ((this.m_FlashController != null) && (this.m_CameraActivity != null)) {
          break label877;
        }
        return FlashMode.OFF.name();
      }
      if (paramString1.equals("AvailableFocusValues"))
      {
        if (this.m_CameraActivity != null) {
          break label1006;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailableIsoValues"))
      {
        if (this.m_CameraActivity != null) {
          break label1223;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailablePhotoSizes"))
      {
        if (this.m_ResolutionManager != null) {
          break label1371;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailableScenes"))
      {
        if (this.m_SceneManager != null) {
          break label1439;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailableVideoSizes"))
      {
        if (this.m_ResolutionManager != null) {
          break label1507;
        }
        return paramString2;
      }
      if (paramString1.equals("AvailableWb"))
      {
        if (this.m_CameraActivity != null) {
          break label1575;
        }
        return paramString2;
      }
      if (paramString1.equals("CameraLensFacing"))
      {
        if (this.m_CameraActivity != null) {
          break label1676;
        }
        return paramString2;
      }
      if (paramString1.equals("CaptureMode"))
      {
        if (this.m_CaptureModeManager != null) {
          break label1712;
        }
        return paramString2;
      }
      if (paramString1.equals("PhotoSize"))
      {
        if (this.m_ResolutionManager != null) {
          break label1739;
        }
        return paramString2;
      }
      if (paramString1.equals("VideoSize"))
      {
        if (this.m_ResolutionManager != null) {
          break label1766;
        }
        return paramString2;
      }
      if (paramString1.equals("Scene"))
      {
        if (this.m_SceneManager != null) {
          break label1793;
        }
        return paramString2;
      }
      if (paramString1.equals("PhotoCaptureState"))
      {
        if (this.m_CameraActivity != null) {
          break label1820;
        }
        return paramString2;
      }
      if (paramString1.equals("VideoCaptureState"))
      {
        if (this.m_CameraActivity != null) {
          break label1845;
        }
        return paramString2;
      }
      if (paramString1.equals("LastSavedMedia"))
      {
        if (this.m_LastSavedMedia != null) {
          break label1870;
        }
        return paramString2;
      }
      if (paramString1.equals("FlashMode"))
      {
        if (this.m_FlashController != null) {
          break label1878;
        }
        return paramString2;
      }
      if (paramString1.equals("FocusState"))
      {
        if (this.m_FocusController != null) {
          break label1897;
        }
        return paramString2;
      }
      if (!paramString1.equals("CameraPreviewState")) {
        return paramString2;
      }
      if (this.m_CameraActivity != null) {
        break label1916;
      }
      return paramString2;
    }
    paramString2 = (List)this.m_CameraActivity.get(CameraActivity.PROP_AVAILABLE_CAMERAS);
    paramString1 = new StringBuffer();
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext()) {
      paramString1.append(((Camera)paramString2.next()).get(Camera.PROP_LENS_FACING)).append(";");
    }
    return paramString1.toString();
    label482:
    paramString2 = (List)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODES);
    paramString1 = new StringBuffer();
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext()) {
      paramString1.append((String)((CaptureMode)paramString2.next()).get(CaptureMode.PROP_ID)).append(";");
    }
    return paramString1.toString();
    label561:
    paramString1 = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    int i;
    if (paramString1 != null)
    {
      localObject = (Range)paramString1.get(Camera.PROP_EXPOSURE_TIME_NANOS_RANGE);
      if ((localObject == null) || ((((Long)((Range)localObject).getLower()).longValue() == 0L) && (((Long)((Range)localObject).getUpper()).longValue() == 0L))) {
        return paramString2;
      }
      paramString2 = new StringBuffer();
      i = 0;
      if (i < ExposureTimeKnobView.EXPOSURE_TIME_CANDIDATES.length)
      {
        paramString1 = ExposureTimeKnobView.EXPOSURE_TIME_CANDIDATES[i];
        if (paramString1.contains("/"))
        {
          paramString1 = Long.valueOf((Rational.parseRational(paramString1).doubleValue() * 1000.0D * 1000.0D * 1000.0D));
          label693:
          if ((paramString1.longValue() >= ((Long)((Range)localObject).getLower()).longValue()) && (paramString1.longValue() <= ((Long)((Range)localObject).getUpper()).longValue())) {
            break label764;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          paramString1 = Long.valueOf((Double.parseDouble(paramString1) * 1000.0D * 1000.0D * 1000.0D));
          break label693;
          label764:
          paramString2.append(paramString1).append(";");
        }
      }
      return paramString2.toString();
    }
    return paramString2;
    label786:
    paramString1 = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (paramString1 != null)
    {
      paramString1 = (List)paramString1.get(Camera.PROP_FACE_BEAUTY_VALUE_LIST);
      if (paramString1 == null) {
        return paramString2;
      }
      paramString2 = new StringBuffer();
      paramString1 = paramString1.iterator();
      while (paramString1.hasNext()) {
        paramString2.append((Integer)paramString1.next()).append(";");
      }
      return paramString2.toString();
    }
    return paramString2;
    label877:
    if (((Boolean)this.m_FlashController.get(FlashController.PROP_HAS_FLASH)).booleanValue())
    {
      paramString1 = new StringBuffer();
      if (this.m_CameraActivity.get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.PHOTO)
      {
        paramString1.append(FlashMode.AUTO.name()).append(";").append(FlashMode.ON.name()).append(";").append(FlashMode.OFF.name());
        return paramString1.toString();
      }
      paramString1.append(FlashMode.TORCH.name()).append(";").append(FlashMode.OFF.name());
      return paramString1.toString();
    }
    return FlashMode.OFF.name();
    label1006:
    Object localObject = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (localObject != null)
    {
      paramString1 = (Range)((Camera)localObject).get(Camera.PROP_FOCUS_RANGE);
      if ((paramString1 == null) || (paramString1.getLower() == paramString1.getUpper())) {
        return paramString2;
      }
      paramString2 = new StringBuffer();
      float f2 = ((Float)((Camera)localObject).get(Camera.PROP_FOCUS_STEP)).floatValue();
      localObject = new ArrayList();
      for (float f1 = ((Float)paramString1.getUpper()).floatValue(); f1 >= ((Float)paramString1.getLower()).floatValue(); f1 -= f2) {
        ((List)localObject).add(Float.valueOf(f1));
      }
      if (((List)localObject).size() > 0) {
        ((List)localObject).set(((List)localObject).size() - 1, (Float)paramString1.getLower());
      }
      i = 0;
      while (i < ((List)localObject).size())
      {
        paramString2.append(((List)localObject).get(i)).append(";");
        i += 1;
      }
      return paramString2.toString();
    }
    return paramString2;
    label1223:
    paramString1 = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (paramString1 != null)
    {
      paramString1 = (Range)paramString1.get(Camera.PROP_ISO_RANGE);
      if ((paramString1 == null) || (paramString1.getLower() == paramString1.getUpper())) {
        return paramString2;
      }
      paramString2 = new StringBuffer();
      i = 0;
      if (i < ISOKnobView.ISO_CANDIDATES.length)
      {
        localObject = ISOKnobView.ISO_CANDIDATES[i];
        int j = Integer.parseInt((String)localObject);
        if ((j < ((Integer)paramString1.getLower()).intValue()) || (j - 50 > ((Integer)paramString1.getUpper()).intValue())) {}
        for (;;)
        {
          i += 1;
          break;
          paramString2.append((String)localObject).append(";");
        }
      }
      return paramString2.toString();
    }
    return paramString2;
    label1371:
    paramString2 = (List)this.m_ResolutionManager.get(ResolutionManager.PROP_PHOTO_RESOLUTION_LIST);
    paramString1 = new StringBuffer();
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext()) {
      paramString1.append((Resolution)paramString2.next()).append(";");
    }
    return paramString1.toString();
    label1439:
    paramString2 = (List)this.m_SceneManager.get(SceneManager.PROP_SCENES);
    paramString1 = new StringBuffer();
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext()) {
      paramString1.append((Scene)paramString2.next()).append(";");
    }
    return paramString1.toString();
    label1507:
    paramString2 = (List)this.m_ResolutionManager.get(ResolutionManager.PROP_VIDEO_RESOLUTION_LIST);
    paramString1 = new StringBuffer();
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext()) {
      paramString1.append((Resolution)paramString2.next()).append(";");
    }
    return paramString1.toString();
    label1575:
    paramString1 = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (paramString1 != null)
    {
      paramString1 = (List)paramString1.get(Camera.PROP_AWB_MODES);
      if ((paramString1 == null) || (paramString1.size() < 1)) {
        return paramString2;
      }
      paramString2 = new StringBuffer();
      paramString1 = paramString1.iterator();
      while (paramString1.hasNext()) {
        paramString2.append((Integer)paramString1.next()).append(";");
      }
      return paramString2.toString();
    }
    return paramString2;
    label1676:
    paramString1 = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (paramString1 != null) {
      return ((Camera.LensFacing)paramString1.get(Camera.PROP_LENS_FACING)).toString();
    }
    return paramString2;
    label1712:
    paramString1 = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
    if (paramString1 != null) {
      return paramString1.toString();
    }
    return paramString2;
    label1739:
    paramString1 = (Resolution)this.m_ResolutionManager.get(ResolutionManager.PROP_PHOTO_RESOLUTION);
    if (paramString1 != null) {
      return paramString1.toString();
    }
    return paramString2;
    label1766:
    paramString1 = (Resolution)this.m_ResolutionManager.get(ResolutionManager.PROP_VIDEO_RESOLUTION);
    if (paramString1 != null) {
      return paramString1.toString();
    }
    return paramString2;
    label1793:
    paramString1 = (Scene)this.m_SceneManager.get(SceneManager.PROP_SCENE);
    if (paramString1 != null) {
      return paramString1.toString();
    }
    return paramString2;
    label1820:
    paramString1 = (PhotoCaptureState)this.m_CameraActivity.get(CameraActivity.PROP_PHOTO_CAPTURE_STATE);
    if (paramString1 != null) {
      return paramString1.toString();
    }
    return paramString2;
    label1845:
    paramString1 = (VideoCaptureState)this.m_CameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE);
    if (paramString1 != null) {
      return paramString1.toString();
    }
    return paramString2;
    label1870:
    return this.m_LastSavedMedia.getFilePath();
    label1878:
    return ((FlashMode)this.m_FlashController.get(FlashController.PROP_FLASH_MODE)).toString();
    label1897:
    return ((FocusState)this.m_FocusController.get(FocusController.PROP_FOCUS_STATE)).toString();
    label1916:
    return ((OperationState)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA_PREVIEW_STATE)).toString();
    return paramString2;
  }
  
  private void onActivityStateChanged(BaseActivity.State arg1)
  {
    Object localObject1;
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[???.ordinal()])
    {
    default: 
      return;
    case 2: 
      localObject1 = this.m_Lock;
      ??? = (BaseActivity.State)localObject1;
    }
    for (;;)
    {
      try
      {
        if (!this.m_IsStartingActivity)
        {
          Log.w("CameraAutoTestService", "onActivityStateChanged() - Activity restarted, unbind");
          detachFromActivity(this.m_CameraActivity);
          this.m_CameraActivity = null;
          ??? = (BaseActivity.State)localObject1;
        }
        return;
      }
      finally
      {
        ??? = finally;
        throw ???;
      }
      synchronized (this.m_Lock)
      {
        Log.w("CameraAutoTestService", "onActivityStateChanged() - Activity destroying, unbind");
        detachFromActivity(this.m_CameraActivity);
        this.m_CameraActivity = null;
      }
    }
  }
  
  private void onBurstPhotoReceived(CaptureEventArgs paramCaptureEventArgs)
  {
    BurstInfo localBurstInfo2 = (BurstInfo)this.m_BurstCaptureInfoMap.get(paramCaptureEventArgs.getCaptureHandle());
    BurstInfo localBurstInfo1 = localBurstInfo2;
    if (localBurstInfo2 == null) {
      localBurstInfo1 = new BurstInfo();
    }
    localBurstInfo1.increaseReceivedCount();
    this.m_BurstCaptureInfoMap.put(paramCaptureEventArgs.getCaptureHandle(), localBurstInfo1);
  }
  
  private void onBurstPhotoSaveFailed(CaptureHandle paramCaptureHandle)
  {
    Iterator localIterator = this.m_BurstCaptureHandleList.iterator();
    while (localIterator.hasNext())
    {
      CaptureHandle localCaptureHandle = ((CaptureHandle)localIterator.next()).getInternalCaptureHandle();
      if (paramCaptureHandle.equals(localCaptureHandle))
      {
        paramCaptureHandle = (BurstInfo)this.m_BurstCaptureInfoMap.get(localCaptureHandle);
        if (paramCaptureHandle != null)
        {
          paramCaptureHandle.decreaseReceivedCount();
          this.m_BurstCaptureInfoMap.put(localCaptureHandle, paramCaptureHandle);
        }
        this.m_Handler.removeMessages(10001);
        if ((paramCaptureHandle != null) && (paramCaptureHandle.isFinished()))
        {
          paramCaptureHandle = Message.obtain(this.m_Handler, 10001, 0, 0, localCaptureHandle);
          this.m_Handler.sendMessageDelayed(paramCaptureHandle, 500L);
        }
      }
    }
  }
  
  private void onMediaSaveCancelled(MediaEventArgs paramMediaEventArgs)
  {
    if (!paramMediaEventArgs.getCaptureHandle().isBurstPhotoCapture())
    {
      Iterator localIterator = this.m_CaptureHandleList.iterator();
      while (localIterator.hasNext())
      {
        CaptureHandle localCaptureHandle1 = (CaptureHandle)localIterator.next();
        CaptureHandle localCaptureHandle2 = localCaptureHandle1.getInternalCaptureHandle();
        if (paramMediaEventArgs.getCaptureHandle().equals(localCaptureHandle2)) {
          this.m_CaptureHandleList.remove(localCaptureHandle1);
        }
      }
      return;
    }
    onBurstPhotoSaveFailed(paramMediaEventArgs.getCaptureHandle());
  }
  
  private void onMediaSaveFailed(MediaEventArgs paramMediaEventArgs)
  {
    if (!paramMediaEventArgs.getCaptureHandle().isBurstPhotoCapture())
    {
      Iterator localIterator = this.m_CaptureHandleList.iterator();
      while (localIterator.hasNext())
      {
        CaptureHandle localCaptureHandle1 = (CaptureHandle)localIterator.next();
        CaptureHandle localCaptureHandle2 = localCaptureHandle1.getInternalCaptureHandle();
        if (paramMediaEventArgs.getCaptureHandle().equals(localCaptureHandle2)) {
          this.m_CaptureHandleList.remove(localCaptureHandle1);
        }
      }
      return;
    }
    onBurstPhotoSaveFailed(paramMediaEventArgs.getCaptureHandle());
  }
  
  private void onMediaSaved(MediaEventArgs paramMediaEventArgs)
  {
    Object localObject1;
    Object localObject2;
    if (!paramMediaEventArgs.getCaptureHandle().isBurstPhotoCapture())
    {
      localObject1 = this.m_CaptureHandleList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (CaptureHandle)((Iterator)localObject1).next();
        CaptureHandle localCaptureHandle = ((CaptureHandle)localObject2).getInternalCaptureHandle();
        if (paramMediaEventArgs.getCaptureHandle().equals(localCaptureHandle))
        {
          this.m_LastSavedMedia = paramMediaEventArgs;
          this.m_CaptureHandleList.remove(localObject2);
        }
        return;
        break label84;
      }
    }
    label84:
    do
    {
      do
      {
        do
        {
          localObject2 = this.m_BurstCaptureHandleList.iterator();
        } while (!((Iterator)localObject2).hasNext());
        localObject1 = ((CaptureHandle)((Iterator)localObject2).next()).getInternalCaptureHandle();
        if (!paramMediaEventArgs.getCaptureHandle().equals(localObject1)) {
          break;
        }
        localObject2 = (BurstInfo)this.m_BurstCaptureInfoMap.get(localObject1);
      } while (localObject2 == null);
      this.m_LastSavedMedia = paramMediaEventArgs;
      ((BurstInfo)localObject2).increaseSaveDCount();
      this.m_BurstCaptureInfoMap.put(localObject1, localObject2);
      this.m_Handler.removeMessages(10001);
    } while (!((BurstInfo)localObject2).isFinished());
    paramMediaEventArgs = Message.obtain(this.m_Handler, 10001, 0, 0, localObject1);
    this.m_Handler.sendMessageDelayed(paramMediaEventArgs, 500L);
  }
  
  private boolean performAction(String paramString, final int paramInt)
    throws RemoteException
  {
    if (paramString.equals("LockFocus"))
    {
      if ((!Handle.isValid(this.m_FocusLockHandle)) && (this.m_FocusController != null))
      {
        this.m_CameraActivity.getHandler().post(new Runnable()
        {
          public void run()
          {
            AutoTestService.-set0(AutoTestService.this, AutoTestService.-get10(AutoTestService.this).lockFocus(0));
          }
        });
        return true;
      }
    }
    else
    {
      if (paramString.equals("SlideUp")) {}
      while ((paramString.equals("SlideDown")) || (paramString.equals("SlideLeft")) || (paramString.equals("SlideRight"))) {
        return simulateSlide(paramString);
      }
      if (paramString.equals("StartPhotoCapture"))
      {
        if (this.m_CameraActivity != null) {
          break label190;
        }
        return false;
      }
      if (paramString.equals("StopPhotoCapture"))
      {
        if (Handle.isValid(this.m_PhotoCaptureHandle)) {
          break label212;
        }
        return false;
      }
      if (paramString.equals("StartVideoCapture"))
      {
        if (this.m_CameraActivity != null) {
          break label233;
        }
        return false;
      }
      if (paramString.equals("StopVideoCapture"))
      {
        if (Handle.isValid(this.m_VideoCaptureHandle)) {
          break label254;
        }
        return false;
      }
      if (!paramString.equals("UnLockFocus")) {
        break label296;
      }
      if (Handle.isValid(this.m_FocusLockHandle)) {
        break label275;
      }
      return false;
    }
    return false;
    label190:
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        if (AutoTestService.-get2(AutoTestService.this) == null) {
          return;
        }
        int i = 1;
        if ((paramInt & 0x1) != 0) {
          i = 20;
        }
        AutoTestService.-set1(AutoTestService.this, AutoTestService.-get2(AutoTestService.this).capturePhoto(i));
        if (Handle.isValid(AutoTestService.-get14(AutoTestService.this)))
        {
          if (i == 1)
          {
            AutoTestService.-get5(AutoTestService.this).add(AutoTestService.-get14(AutoTestService.this));
            return;
          }
          AutoTestService.-get0(AutoTestService.this).add(AutoTestService.-get14(AutoTestService.this));
          return;
        }
        Log.w("CameraAutoTestService", "performAction() - Take picture failed.");
      }
    });
    return true;
    label212:
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        Handle.close(AutoTestService.-get14(AutoTestService.this));
      }
    });
    return true;
    label233:
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        AutoTestService.-set2(AutoTestService.this, AutoTestService.-get2(AutoTestService.this).captureVideo());
        if (Handle.isValid(AutoTestService.-get19(AutoTestService.this)))
        {
          AutoTestService.-get5(AutoTestService.this).add(AutoTestService.-get19(AutoTestService.this));
          return;
        }
        Log.w("CameraAutoTestService", "performAction() - Video capture failed.");
      }
    });
    return true;
    label254:
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        Handle.close(AutoTestService.-get19(AutoTestService.this));
      }
    });
    return true;
    label275:
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        Handle.close(AutoTestService.-get11(AutoTestService.this));
      }
    });
    return true;
    label296:
    return false;
  }
  
  private void sendTouchEvent(final MotionEvent paramMotionEvent)
  {
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        if (AutoTestService.-get12(AutoTestService.this) != null) {
          AutoTestService.-get12(AutoTestService.this).handleTouchEvent(paramMotionEvent);
        }
      }
    });
  }
  
  private boolean setBooleanPreference(String paramString, boolean paramBoolean)
  {
    if (this.m_Settings == null) {
      return false;
    }
    this.m_Settings.set(paramString, Boolean.valueOf(paramBoolean));
    return true;
  }
  
  private boolean setBooleanState(String paramString, final boolean paramBoolean)
    throws RemoteException
  {
    if (paramString == null) {
      throw new RemoteException("No state key");
    }
    final Object localObject = (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
    if (paramString.equals("IsFaceBeautyEnabled"))
    {
      if (this.m_FaceBeautyController == null) {
        return false;
      }
    }
    else
    {
      if (paramString.equals("IsGridVisible"))
      {
        if (this.m_CameraPreviewGrid != null) {
          break label208;
        }
        return false;
      }
      if (paramString.equals("IsMirrored")) {
        return setBooleanPreference("IsMirrored", paramBoolean);
      }
      if (paramString.equals("IsRawEnabled"))
      {
        if (this.m_Settings != null) {
          break label267;
        }
        return false;
      }
      if (paramString.equals("IsSavingLocation")) {
        return setBooleanPreference("Location.Save", paramBoolean);
      }
      if (paramString.equals("IsShutterSoundNeeded")) {
        return setBooleanPreference("ShutterSound", paramBoolean);
      }
      if (!paramString.equals("IsSmileCaptureEnabled")) {
        break label340;
      }
      if (this.m_SmileCaptureController != null) {
        break label309;
      }
      return false;
    }
    if (!((Boolean)this.m_FaceBeautyController.get(FaceBeautyController.PROP_IS_SUPPORTED)).booleanValue()) {
      return false;
    }
    if (this.m_CameraActivity != null)
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          if (paramBoolean)
          {
            AutoTestService.-get8(AutoTestService.this).activate(0);
            return;
          }
          AutoTestService.-get8(AutoTestService.this).deactivate(0);
        }
      });
      return true;
    }
    return false;
    label208:
    if (this.m_CameraActivity != null)
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          CameraPreviewGrid localCameraPreviewGrid;
          PropertyKey localPropertyKey;
          if (AutoTestService.-get3(AutoTestService.this) != null)
          {
            localCameraPreviewGrid = AutoTestService.-get3(AutoTestService.this);
            localPropertyKey = CameraPreviewGrid.PROP_GRID_TYPE;
            if (!paramBoolean) {
              break label65;
            }
          }
          label65:
          for (CameraPreviewGrid.GridType localGridType = CameraPreviewGrid.GridType.UNIFORM_3x3;; localGridType = CameraPreviewGrid.GridType.NONE)
          {
            localCameraPreviewGrid.set(localPropertyKey, localGridType);
            if (AutoTestService.-get4(AutoTestService.this) != null) {
              AutoTestService.-get4(AutoTestService.this).invalidateCameraPreviewOverlay();
            }
            return;
          }
        }
      });
      localObject = this.m_Settings;
      if (paramBoolean) {}
      for (paramString = CameraPreviewGrid.GridType.UNIFORM_3x3;; paramString = CameraPreviewGrid.GridType.NONE)
      {
        ((Settings)localObject).set("Grid.Type", paramString);
        return true;
      }
    }
    return false;
    label267:
    if (localObject != null)
    {
      ((Camera)localObject).getHandler().post(new Runnable()
      {
        public void run()
        {
          localObject.set(Camera.PROP_IS_RAW_CAPTURE_ENABLED, Boolean.valueOf(paramBoolean));
        }
      });
      this.m_Settings.set("RawCapture", Boolean.valueOf(paramBoolean));
      return true;
    }
    return false;
    label309:
    if (this.m_CameraActivity != null)
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          if (AutoTestService.-get17(AutoTestService.this) != null) {
            AutoTestService.-get17(AutoTestService.this).set(SmileCaptureController.PROP_IS_SMILE_CAPTURE_ENABLED, Boolean.valueOf(paramBoolean));
          }
        }
      });
      return true;
    }
    return false;
    label340:
    return false;
  }
  
  private boolean setFloatState(final String paramString, final float paramFloat)
    throws RemoteException
  {
    if (paramString == null) {
      throw new RemoteException("No state key");
    }
    if (paramString.equals("DigitalZoom"))
    {
      if (this.m_ZoomController == null) {
        return false;
      }
    }
    else
    {
      if (paramString.equals("ExposureCompensataion"))
      {
        if ((this.m_ExposureController != null) && (this.m_CameraActivity != null)) {
          break label98;
        }
        return false;
      }
      if (!paramString.equals("Focus")) {
        break label163;
      }
      if (this.m_CameraActivity != null) {
        break label120;
      }
      return false;
    }
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        AutoTestService.-get20(AutoTestService.this).set(ZoomController.PROP_DIGITAL_ZOOM, Float.valueOf(paramFloat));
      }
    });
    return true;
    label98:
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        Range localRange = (Range)AutoTestService.-get7(AutoTestService.this).get(ExposureController.PROP_EXPOSURE_COMPENSATION_RANGE);
        float f2;
        if (paramFloat >= 0.0F) {
          f2 = paramFloat;
        }
        for (float f1 = ((Float)localRange.getUpper()).floatValue();; f1 = ((Float)localRange.getLower()).floatValue())
        {
          AutoTestService.-get7(AutoTestService.this).set(ExposureController.PROP_EXPOSURE_COMPENSATION, Float.valueOf(f2 * f1));
          return;
          f2 = -paramFloat;
        }
      }
    });
    return true;
    label120:
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null) {
      return false;
    }
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        paramString.setFocus(paramFloat);
      }
    });
    return true;
    label163:
    return false;
  }
  
  private boolean setIntState(final String paramString, final int paramInt)
    throws RemoteException
  {
    if (paramString.equals("Awb"))
    {
      if (this.m_CameraActivity == null) {
        return false;
      }
    }
    else
    {
      if (paramString.equals("FaceBeautyValue"))
      {
        if ((this.m_FaceBeautyController != null) && (this.m_CameraActivity != null)) {
          break label113;
        }
        return false;
      }
      if (!paramString.equals("Iso")) {
        break label219;
      }
      if (this.m_CameraActivity != null) {
        break label167;
      }
      Log.w("CameraAutoTestService", "setIntState() - m_CameraActivity is null");
      return false;
    }
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null) {
      return false;
    }
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        paramString.setAwb(paramInt);
      }
    });
    return true;
    label113:
    if (((Boolean)this.m_FaceBeautyController.get(FaceBeautyController.PROP_IS_ACTIVATED)).booleanValue())
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          AutoTestService.-get8(AutoTestService.this).set(FaceBeautyController.PROP_VALUE, Integer.valueOf(paramInt));
        }
      });
      return true;
    }
    Log.w("CameraAutoTestService", "setIntState() - STATE_KEY_FACE_BEAUTY_VALUE, not enable face beauty.");
    return false;
    label167:
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null)
    {
      Log.w("CameraAutoTestService", "setIntState() - manualModeUi is null");
      return false;
    }
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        paramString.setIso(paramInt);
      }
    });
    return true;
    label219:
    return false;
  }
  
  private boolean setLongState(final String paramString, final long paramLong)
    throws RemoteException
  {
    if (paramString.equals("Exposure"))
    {
      if (this.m_CameraActivity == null) {
        return false;
      }
    }
    else
    {
      if (!paramString.equals("SelfTimerInterval")) {
        break label101;
      }
      if (this.m_CameraActivity != null) {
        break label79;
      }
      return false;
    }
    paramString = (ManualModeUI)this.m_CameraActivity.findComponent(ManualModeUI.class);
    if (paramString == null) {
      return false;
    }
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        paramString.setExposure(paramLong);
      }
    });
    return true;
    label79:
    this.m_CameraActivity.getHandler().post(new Runnable()
    {
      public void run()
      {
        AutoTestService.-get2(AutoTestService.this).set(CameraActivity.PROP_SELF_TIMER_INTERVAL, Long.valueOf(paramLong));
      }
    });
    return true;
    label101:
    return false;
  }
  
  private boolean setStringState(final String paramString1, String paramString2)
    throws RemoteException
  {
    if (paramString1 == null) {
      throw new RemoteException("No state key");
    }
    if (this.m_CameraActivity == null) {
      return false;
    }
    if (paramString1.equals("PhotoSize"))
    {
      if (this.m_ResolutionManager == null) {
        return false;
      }
    }
    else
    {
      if (paramString1.equals("CameraLensFacing"))
      {
        if (this.m_CameraActivity != null) {
          break label232;
        }
        return false;
      }
      if (paramString1.equals("CaptureMode"))
      {
        if (this.m_CaptureModeManager != null) {
          break label302;
        }
        return false;
      }
      if (paramString1.equals("FlashMode"))
      {
        if (this.m_FlashController != null) {
          break label385;
        }
        return false;
      }
      if (paramString1.equals("Scene"))
      {
        if (this.m_SceneManager != null) {
          break label575;
        }
        return false;
      }
      if (!paramString1.equals("VideoSize")) {
        break label782;
      }
      if (this.m_ResolutionManager != null) {
        break label689;
      }
      return false;
    }
    paramString1 = (List)this.m_ResolutionManager.get(ResolutionManager.PROP_PHOTO_RESOLUTION_LIST);
    this.m_TargetResolution = null;
    paramString1 = paramString1.iterator();
    final Object localObject;
    while (paramString1.hasNext())
    {
      localObject = (Resolution)paramString1.next();
      if (((Resolution)localObject).toString().equals(paramString2)) {
        this.m_TargetResolution = ((Resolution)localObject);
      }
    }
    if ((this.m_TargetResolution != null) && (this.m_CameraActivity != null))
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          AutoTestService.-get15(AutoTestService.this).set(ResolutionManager.PROP_PHOTO_RESOLUTION, AutoTestService.-get18(AutoTestService.this));
        }
      });
      return true;
    }
    return false;
    label232:
    if (paramString2.equalsIgnoreCase(Camera.LensFacing.BACK.name())) {
      paramString1 = Camera.LensFacing.BACK;
    }
    while (paramString1 != null)
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          AutoTestService.-get2(AutoTestService.this).switchCamera(paramString1);
        }
      });
      return true;
      if (paramString2.equalsIgnoreCase(Camera.LensFacing.FRONT.name())) {
        paramString1 = Camera.LensFacing.FRONT;
      } else {
        paramString1 = null;
      }
    }
    return false;
    label302:
    paramString1 = ((List)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODES)).iterator();
    while (paramString1.hasNext())
    {
      localObject = (CaptureMode)paramString1.next();
      if (((String)((CaptureMode)localObject).get(CaptureMode.PROP_ID)).equalsIgnoreCase(paramString2))
      {
        this.m_CameraActivity.getHandler().post(new Runnable()
        {
          public void run()
          {
            AutoTestService.-get6(AutoTestService.this).setCaptureMode(localObject, 0);
          }
        });
        return true;
      }
    }
    return false;
    label385:
    if (this.m_SceneManager == null) {
      return false;
    }
    if (((Boolean)this.m_FlashController.get(FlashController.PROP_HAS_FLASH)).booleanValue())
    {
      if (this.m_CameraActivity.get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.PHOTO)
      {
        if (FlashMode.AUTO.name().equalsIgnoreCase(paramString2)) {
          paramString1 = FlashMode.AUTO;
        }
        for (;;)
        {
          this.m_CameraActivity.getHandler().post(new Runnable()
          {
            public void run()
            {
              Object localObject2 = (List)AutoTestService.-get16(AutoTestService.this).get(SceneManager.PROP_SCENES);
              Object localObject1 = null;
              Object localObject3 = ((Iterable)localObject2).iterator();
              do
              {
                localObject2 = localObject1;
                if (!((Iterator)localObject3).hasNext()) {
                  break;
                }
                localObject2 = (Scene)((Iterator)localObject3).next();
              } while (!(localObject2 instanceof AutoHdrScene));
              Scene localScene = (Scene)AutoTestService.-get16(AutoTestService.this).get(SceneManager.PROP_SCENE);
              localObject3 = localScene;
              if ((localScene instanceof AutoHdrScene))
              {
                localObject1 = localObject3;
                if (paramString1 == FlashMode.ON) {
                  localObject1 = Scene.NO_SCENE;
                }
              }
              for (;;)
              {
                Log.v("CameraAutoTestService", "STATE_KEY_FLASH_MODES - target flash: ", paramString1, ", current scene: ", localScene, ", target scene: ", localObject1);
                if (localObject1 != localScene) {
                  AutoTestService.-get16(AutoTestService.this).setScene((Scene)localObject1, 0);
                }
                AutoTestService.-get9(AutoTestService.this).setFlashMode(paramString1, 0);
                return;
                if (!(localScene instanceof HdrScene))
                {
                  localObject1 = localObject3;
                  if (!(localScene instanceof ClearShot)) {}
                }
                else if (paramString1 == FlashMode.ON)
                {
                  localObject1 = Scene.NO_SCENE;
                }
                else
                {
                  localObject1 = localObject3;
                  if (paramString1 == FlashMode.AUTO)
                  {
                    localObject1 = localObject3;
                    if (localObject2 != null) {
                      localObject1 = localObject2;
                    }
                  }
                }
              }
            }
          });
          return true;
          if (FlashMode.ON.name().equalsIgnoreCase(paramString2))
          {
            paramString1 = FlashMode.ON;
          }
          else
          {
            if (!FlashMode.OFF.name().equalsIgnoreCase(paramString2)) {
              break;
            }
            paramString1 = FlashMode.OFF;
          }
        }
        return false;
      }
      if (FlashMode.TORCH.name().equalsIgnoreCase(paramString2)) {}
      for (paramString1 = FlashMode.TORCH;; paramString1 = FlashMode.OFF)
      {
        this.m_CameraActivity.getHandler().post(new Runnable()
        {
          public void run()
          {
            AutoTestService.-get9(AutoTestService.this).setFlashMode(paramString1, 0);
          }
        });
        return true;
        if (!FlashMode.OFF.name().equalsIgnoreCase(paramString2)) {
          break;
        }
      }
      return false;
    }
    return false;
    label575:
    if ("(No scene)".equals(paramString2))
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          AutoTestService.-get16(AutoTestService.this).setScene(Scene.NO_SCENE, 0);
        }
      });
      return true;
    }
    paramString1 = ((List)this.m_SceneManager.get(SceneManager.PROP_SCENES)).iterator();
    while (paramString1.hasNext())
    {
      localObject = (Scene)paramString1.next();
      if (((String)((Scene)localObject).get(Scene.PROP_ID)).equalsIgnoreCase(paramString2))
      {
        this.m_CameraActivity.getHandler().post(new Runnable()
        {
          public void run()
          {
            AutoTestService.-get16(AutoTestService.this).setScene(localObject, 0);
          }
        });
        return true;
      }
    }
    return false;
    label689:
    paramString1 = (List)this.m_ResolutionManager.get(ResolutionManager.PROP_VIDEO_RESOLUTION_LIST);
    this.m_TargetResolution = null;
    paramString1 = paramString1.iterator();
    while (paramString1.hasNext())
    {
      localObject = (Resolution)paramString1.next();
      if (((Resolution)localObject).toString().equals(paramString2)) {
        this.m_TargetResolution = ((Resolution)localObject);
      }
    }
    if (this.m_TargetResolution != null)
    {
      this.m_CameraActivity.getHandler().post(new Runnable()
      {
        public void run()
        {
          AutoTestService.-get15(AutoTestService.this).set(ResolutionManager.PROP_VIDEO_RESOLUTION, AutoTestService.-get18(AutoTestService.this));
        }
      });
      return true;
    }
    return false;
    label782:
    return false;
  }
  
  private boolean simulateSlide(String paramString)
  {
    if (this.m_GestureDetector == null)
    {
      Log.e("CameraAutoTestService", "simulateSlide() - There is no gesture detector");
      return false;
    }
    try
    {
      MotionEvent localMotionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, 540.0F, 960.0F, 0);
      sendTouchEvent(localMotionEvent);
      Thread.sleep(20L);
      int i = 1;
      if (i <= 10)
      {
        if (paramString.equals("SlideUp")) {
          localMotionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, 540.0F, 960.0F - i * 25, 0);
        }
        for (;;)
        {
          sendTouchEvent(localMotionEvent);
          Thread.sleep(20L);
          i += 1;
          break;
          if (paramString.equals("SlideDown")) {
            localMotionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, 540.0F, 960.0F + i * 25, 0);
          } else if (paramString.equals("SlideLeft")) {
            localMotionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, 540.0F - i * 25, 960.0F, 0);
          } else if (paramString.equals("SlideRight")) {
            localMotionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, 540.0F + i * 25, 960.0F, 0);
          }
        }
      }
      return true;
    }
    catch (Throwable paramString)
    {
      Log.e("CameraAutoTestService", "simulateSlide() - Error occrued.", paramString);
    }
  }
  
  private boolean start(String paramString, int paramInt)
  {
    synchronized (this.m_Lock)
    {
      if (this.m_IsStartingActivity)
      {
        Log.e("CameraAutoTestService", "start() - Already starting");
        return false;
      }
      if (paramString == null)
      {
        Log.e("CameraAutoTestService", "start() - No start mode");
        return false;
      }
      this.m_CameraActivity = null;
      this.m_IsStartingActivity = true;
      Log.v("CameraAutoTestService", "start() - Mode : ", paramString);
      boolean bool = startCameraActivityInternal(paramString, 0);
      return bool;
    }
  }
  
  /* Error */
  private boolean startAutoFocus(final float paramFloat1, final float paramFloat2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 376	com/oneplus/camera/AutoTestService:m_CameraActivity	Lcom/oneplus/camera/OPCameraActivity;
    //   4: ifnonnull +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_0
    //   10: getfield 376	com/oneplus/camera/AutoTestService:m_CameraActivity	Lcom/oneplus/camera/OPCameraActivity;
    //   13: ldc_w 1334
    //   16: invokevirtual 858	com/oneplus/camera/OPCameraActivity:findComponent	(Ljava/lang/Class;)Lcom/oneplus/base/component/Component;
    //   19: checkcast 1334	com/oneplus/camera/ui/TouchAutoFocusUI
    //   22: astore 5
    //   24: aload 5
    //   26: ifnonnull +5 -> 31
    //   29: iconst_0
    //   30: ireturn
    //   31: iconst_1
    //   32: anewarray 778	java/lang/Boolean
    //   35: astore 4
    //   37: aload 4
    //   39: iconst_0
    //   40: iconst_0
    //   41: invokestatic 1211	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   44: aastore
    //   45: aload 4
    //   47: monitorenter
    //   48: aload_0
    //   49: getfield 376	com/oneplus/camera/AutoTestService:m_CameraActivity	Lcom/oneplus/camera/OPCameraActivity;
    //   52: invokevirtual 1188	com/oneplus/camera/OPCameraActivity:getHandler	()Landroid/os/Handler;
    //   55: new 66	com/oneplus/camera/AutoTestService$37
    //   58: dup
    //   59: aload_0
    //   60: aload 4
    //   62: aload 5
    //   64: fload_1
    //   65: fload_2
    //   66: invokespecial 1337	com/oneplus/camera/AutoTestService$37:<init>	(Lcom/oneplus/camera/AutoTestService;[Ljava/lang/Boolean;Lcom/oneplus/camera/ui/TouchAutoFocusUI;FF)V
    //   69: invokevirtual 1192	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   72: pop
    //   73: ldc_w 258
    //   76: ldc_w 1339
    //   79: invokestatic 863	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   82: aload 4
    //   84: ldc2_w 1340
    //   87: invokevirtual 1344	java/lang/Object:wait	(J)V
    //   90: ldc_w 258
    //   93: ldc_w 1346
    //   96: invokestatic 863	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   99: aload 4
    //   101: iconst_0
    //   102: aaload
    //   103: invokevirtual 781	java/lang/Boolean:booleanValue	()Z
    //   106: istore_3
    //   107: aload 4
    //   109: monitorexit
    //   110: iload_3
    //   111: ireturn
    //   112: astore 5
    //   114: ldc_w 258
    //   117: ldc_w 1348
    //   120: aload 5
    //   122: invokestatic 1321	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   125: aload 4
    //   127: monitorexit
    //   128: iconst_0
    //   129: ireturn
    //   130: astore 5
    //   132: aload 4
    //   134: monitorexit
    //   135: aload 5
    //   137: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	138	0	this	AutoTestService
    //   0	138	1	paramFloat1	float
    //   0	138	2	paramFloat2	float
    //   106	5	3	bool	boolean
    //   35	98	4	arrayOfBoolean	Boolean[]
    //   22	41	5	localTouchAutoFocusUI	TouchAutoFocusUI
    //   112	9	5	localInterruptedException	InterruptedException
    //   130	6	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   73	107	112	java/lang/InterruptedException
    //   48	73	130	finally
    //   73	107	130	finally
    //   114	125	130	finally
  }
  
  private boolean startCameraActivityInternal(String paramString, int paramInt)
  {
    Log.d("CameraAutoTestService", "startCameraActivityInternal()");
    Intent localIntent = new Intent();
    if (paramString.equals("Normal")) {
      localIntent.setAction("android.intent.action.MAIN");
    }
    for (;;)
    {
      localIntent.addFlags(268435456);
      localIntent.setClass(getApplicationContext(), OPCameraActivity.class);
      localIntent.putExtra("com.oneplus.camera.OPCameraActivity.AutoTestServiceId", this.m_Id);
      try
      {
        startActivity(localIntent);
        return true;
      }
      catch (Throwable paramString)
      {
        Log.e("CameraAutoTestService", "startCameraActivityInternal() - Fail to start activity", paramString);
      }
      if (paramString.equals("Photo"))
      {
        localIntent.setAction("android.media.action.STILL_IMAGE_CAMERA");
      }
      else
      {
        if (!paramString.equals("Video")) {
          break;
        }
        localIntent.setAction("android.media.action.VIDEO_CAMERA");
      }
    }
    Log.e("CameraAutoTestService", "startCameraActivityInternal() - Unknown mode");
    return false;
    return false;
  }
  
  private void stop()
  {
    synchronized (this.m_Lock)
    {
      if (this.m_CameraActivity == null)
      {
        if (this.m_IsStartingActivity)
        {
          Log.w("CameraAutoTestService", "stop() - Stop while starting");
          this.m_IsStartingActivity = false;
        }
        return;
      }
      Log.v("CameraAutoTestService", "stop()");
      final OPCameraActivity localOPCameraActivity = this.m_CameraActivity;
      detachFromActivity(localOPCameraActivity);
      HandlerUtils.post(this.m_CameraActivity, new Runnable()
      {
        public void run()
        {
          Log.w("CameraAutoTestService", "stop() - Finish activity by service");
          localOPCameraActivity.finishAndRemoveTask();
        }
      });
      this.m_CameraActivity = null;
      return;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    INSTANCES.remove(Integer.valueOf(this.m_Id));
    super.finalize();
  }
  
  final void notifyActivityReady(OPCameraActivity paramOPCameraActivity)
  {
    synchronized (this.m_Lock)
    {
      if (!this.m_IsStartingActivity)
      {
        OPCameraActivity localOPCameraActivity = this.m_CameraActivity;
        if (localOPCameraActivity != null) {
          return;
        }
      }
      Log.v("CameraAutoTestService", "notifyActivityReady()");
      attachToActivity(paramOPCameraActivity);
      this.m_IsStartingActivity = false;
      this.m_CameraActivity = paramOPCameraActivity;
      return;
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    Log.v("CameraAutoTestService", "onBind()");
    return this.m_Binder;
  }
  
  public void onCreate()
  {
    super.onCreate();
  }
  
  public boolean onUnbind(Intent paramIntent)
  {
    Log.v("CameraAutoTestService", "onUnbind()");
    INSTANCES.remove(Integer.valueOf(this.m_Id));
    return super.onUnbind(paramIntent);
  }
  
  private static class BurstInfo
  {
    private int receviedCount = 0;
    private int savedCount = 0;
    
    public void decreaseReceivedCount()
    {
      this.receviedCount -= 1;
      if (this.receviedCount < 0) {
        this.receviedCount = 0;
      }
    }
    
    public void increaseReceivedCount()
    {
      this.receviedCount += 1;
    }
    
    public void increaseSaveDCount()
    {
      this.savedCount += 1;
    }
    
    public boolean isFinished()
    {
      return this.savedCount == this.receviedCount;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/AutoTestService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */