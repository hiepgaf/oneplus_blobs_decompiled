package com.oneplus.camera;

import android.content.Context;
import android.util.Size;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.manual.ManualCaptureMode;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.panorama.PanoramaCaptureMode;
import com.oneplus.camera.panorama.PanoramaUI;
import com.oneplus.camera.scene.Scene;
import com.oneplus.camera.scene.SceneManager;
import com.oneplus.camera.ui.CameraPreviewGrid.GridType;
import com.oneplus.camera.ui.CaptureModeSwitcher;
import com.oneplus.camera.ui.CaptureModeSwitcher.SwitchCaptureMode;
import com.oneplus.camera.ui.FocusExposureIndicator;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

final class AppTrackerImpl
  extends CameraComponent
{
  public static final int APP_TRACKER_CAMERA_CAPTURE = 0;
  public static final int APP_TRACKER_CAMERA_CAPTURE_MANUAL = 1;
  public static final int APP_TRACKER_CAMERA_CAPTURE_PANORAMA = 3;
  public static final int APP_TRACKER_CAMERA_LAUNCH = 2;
  public static final int APP_TRACKER_CAMERA_SWITCH_CAPTURE_MODE = 4;
  private static final String ONEPLUS_ODM_APP_TRACKER = "net.oneplus.odm.insight.tracker.AppTracker";
  private static final String ONEPLUS_ODM_APP_TRACKER_ONEVENT_METHOD = "onEvent";
  private Object m_AppTracker = null;
  private Integer m_BurstCount = Integer.valueOf(1);
  private Camera m_Camera;
  private CameraActivity m_CameraActivity;
  private CaptureModeManager m_CaptureModeManager;
  private CaptureModeSwitcher m_CaptureModeSwitcher;
  private CaptureTrigger m_CaptureTrigger;
  private ExposureController m_ExposureController;
  private FlashController m_FlashController;
  private FocusExposureIndicator m_FocusExposureIndicator;
  private PanoramaUI m_PanoramaUI;
  private SceneManager m_SceneManager;
  private Settings m_Settings;
  private Method m_TrackOnEvent = null;
  private Map<String, String> m_TrackerData;
  private ZoomController m_ZoomController;
  
  public AppTrackerImpl(CameraActivity paramCameraActivity)
  {
    super("App Tracker", paramCameraActivity, false);
  }
  
  private String getDuration(Long paramLong)
  {
    if (paramLong.longValue() <= 0L) {
      return "0s";
    }
    if (paramLong.longValue() <= 10L) {
      return "<= 10s";
    }
    if (paramLong.longValue() <= 30L) {
      return "11s~30s";
    }
    if (paramLong.longValue() <= 60L) {
      return "30s~60s";
    }
    if (paramLong.longValue() <= 300L) {
      return "61s~300s";
    }
    if (paramLong.longValue() <= 600L) {
      return "301s~600s";
    }
    return "> 600s";
  }
  
  private String getPanoramaLastResult(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN_ERROR";
    case 0: 
      return "SUCCESS";
    case -12: 
      return "BIG_DISPLACEMENT";
    case -10: 
      return "WRONG_DIRECTION";
    }
    return "MOVE_TOO_FAST";
  }
  
  private boolean trackerCameraCapture()
  {
    this.m_Settings = ((Settings)this.m_CameraActivity.get(CameraActivity.PROP_SETTINGS));
    this.m_Camera = ((Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA));
    if (this.m_ExposureController == null) {
      this.m_ExposureController = ((ExposureController)findComponent(ExposureController.class));
    }
    if ((this.m_Camera == null) || (this.m_SceneManager == null)) {}
    while ((this.m_FlashController == null) || (this.m_ZoomController == null) || (this.m_CaptureModeManager == null) || (this.m_Settings == null) || (this.m_ExposureController == null)) {
      return false;
    }
    this.m_TrackerData.put("CaptureMode", (String)((CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE)).get(CaptureMode.PROP_ID));
    this.m_TrackerData.put("MediaType", ((MediaType)this.m_CameraActivity.get(CameraActivity.PROP_MEDIA_TYPE)).toString());
    this.m_TrackerData.put("CameraLensFacing", ((Camera.LensFacing)this.m_Camera.get(Camera.PROP_LENS_FACING)).toString());
    this.m_TrackerData.put("Scene", (String)((Scene)this.m_SceneManager.get(SceneManager.PROP_SCENE)).get(BasicMode.PROP_ID));
    this.m_TrackerData.put("SelfTimer", ((Long)this.m_CameraActivity.get(CameraActivity.PROP_SELF_TIMER_INTERVAL)).toString());
    this.m_TrackerData.put("DigitalZoom", ((Float)this.m_ZoomController.get(ZoomController.PROP_DIGITAL_ZOOM)).toString());
    this.m_TrackerData.put("FlashMode", ((FlashMode)this.m_FlashController.get(FlashController.PROP_FLASH_MODE)).toString());
    this.m_TrackerData.put("ExposureCompensation", ((Float)this.m_ExposureController.get(ExposureController.PROP_EXPOSURE_COMPENSATION)).toString());
    this.m_TrackerData.put("PictureSize", ((Size)this.m_Camera.get(Camera.PROP_PICTURE_SIZE)).toString());
    this.m_TrackerData.put("VideoSize", ((Size)this.m_Camera.get(Camera.PROP_VIDEO_SIZE)).toString());
    this.m_TrackerData.put("PictureCount", this.m_BurstCount.toString());
    this.m_TrackerData.put("Duration", getDuration((Long)this.m_CameraActivity.get(CameraActivity.PROP_ELAPSED_RECORDING_SECONDS)));
    this.m_TrackerData.put("IsBurst", ((Boolean)this.m_CameraActivity.get(CameraActivity.PROP_IS_BURST_PHOTO_ON_CAPTURE)).toString());
    Map localMap = this.m_TrackerData;
    if (this.m_CameraActivity.get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.VIDEO)
    {
      str = "True";
      localMap.put("IsVideoSnapshot", str);
      localMap = this.m_TrackerData;
      if (this.m_Settings.getEnum("Grid.Type", CameraPreviewGrid.GridType.class, CameraPreviewGrid.GridType.NONE) == CameraPreviewGrid.GridType.NONE) {
        break label682;
      }
      str = "True";
      label566:
      localMap.put("IsGridOn", str);
      localMap = this.m_TrackerData;
      if (!this.m_Settings.getBoolean("Location.Save")) {
        break label689;
      }
      str = "True";
      label599:
      localMap.put("IsLocationOn", str);
      localMap = this.m_TrackerData;
      if (!this.m_Settings.getBoolean("ShutterSound")) {
        break label696;
      }
      str = "True";
      label632:
      localMap.put("IsShutterSoundOn", str);
      localMap = this.m_TrackerData;
      if (this.m_CaptureTrigger != CaptureTrigger.HW_BUTTON) {
        break label703;
      }
    }
    label682:
    label689:
    label696:
    label703:
    for (String str = "True";; str = "False")
    {
      localMap.put("IsTriggeredByHwButton", str);
      return true;
      str = "False";
      break;
      str = "False";
      break label566;
      str = "False";
      break label599;
      str = "False";
      break label632;
    }
  }
  
  private boolean trackerCameraCaptureManual()
  {
    this.m_Camera = ((Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA));
    if (this.m_FocusExposureIndicator == null) {
      this.m_FocusExposureIndicator = ((FocusExposureIndicator)findComponent(FocusExposureIndicator.class));
    }
    if ((this.m_Camera == null) || (this.m_FocusExposureIndicator == null)) {
      return false;
    }
    Map localMap = this.m_TrackerData;
    if (((Integer)this.m_Camera.get(Camera.PROP_ISO)).intValue() == -1)
    {
      str = "Auto";
      localMap.put("ISO", str);
      this.m_TrackerData.put("WhiteBalance", ((Integer)this.m_Camera.get(Camera.PROP_AWB_MODE)).toString());
      localMap = this.m_TrackerData;
      if (this.m_Camera.get(Camera.PROP_FOCUS_MODE) == FocusMode.MANUAL) {
        break label220;
      }
    }
    label220:
    for (String str = "Auto";; str = "Manual")
    {
      localMap.put("Focus", str);
      this.m_TrackerData.put("IsAeAfSeparated", ((Boolean)this.m_FocusExposureIndicator.get(FocusExposureIndicator.PROP_IS_FOCUS_EXPOSURE_SEPARATED)).toString());
      return true;
      str = ((Integer)this.m_Camera.get(Camera.PROP_ISO)).toString();
      break;
    }
  }
  
  private boolean trackerCameraCapturePanorama()
  {
    if (this.m_PanoramaUI == null) {
      this.m_PanoramaUI = ((PanoramaUI)findComponent(PanoramaUI.class));
    }
    if (this.m_PanoramaUI == null) {
      return false;
    }
    this.m_TrackerData.put("Result", getPanoramaLastResult(((Integer)this.m_PanoramaUI.get(PanoramaUI.PROP_LAST_FRAME_ADD_RESULT)).intValue()));
    this.m_TrackerData.put("IsStoppedByUser", ((Boolean)this.m_PanoramaUI.get(PanoramaUI.PROP_IS_STOPPED_BY_USER)).toString());
    this.m_TrackerData.put("CapturedLength", ((Float)this.m_PanoramaUI.get(PanoramaUI.PROP_PANORAMA_CAPTURE_LENGTH)).toString().substring(0, 3));
    return true;
  }
  
  private boolean trackerCameraLaunch()
  {
    this.m_TrackerData.put("StartMode", this.m_CameraActivity.getStartMode().toString());
    return true;
  }
  
  private boolean trackerCameraSwitchCaptureMode()
  {
    if (this.m_CaptureModeSwitcher == null) {
      this.m_CaptureModeSwitcher = ((CaptureModeSwitcher)findComponent(CaptureModeSwitcher.class));
    }
    if (this.m_CaptureModeSwitcher == null) {
      return false;
    }
    this.m_TrackerData.put("Camera.SwitchCaptureMode", ((CaptureModeSwitcher.SwitchCaptureMode)this.m_CaptureModeSwitcher.get(CaptureModeSwitcher.PROP_SWITCH_CAPTURE_MODE)).toString());
    return true;
  }
  
  public void onEvent(int paramInt)
  {
    if ((this.m_AppTracker == null) || (this.m_TrackOnEvent == null))
    {
      Log.e(this.TAG, "onEvent failed");
      return;
    }
    this.m_TrackerData.clear();
    String str = null;
    boolean bool = false;
    switch (paramInt)
    {
    }
    while ((!bool) || (str == null))
    {
      Log.w(this.TAG, "Tracker onEvent failed, aciton :" + paramInt + ", trackerAction :" + str);
      return;
      str = "Camera.Capture";
      bool = trackerCameraCapture();
      continue;
      str = "Camera.Capture.Manua";
      bool = trackerCameraCaptureManual();
      continue;
      str = "Camera.Launch";
      bool = trackerCameraLaunch();
      continue;
      str = "Camera.Capture.Panorama";
      bool = trackerCameraCapturePanorama();
      continue;
      str = "Camera.SwitchCaptureMode";
      bool = trackerCameraSwitchCaptureMode();
    }
    try
    {
      this.m_TrackOnEvent.invoke(this.m_AppTracker, new Object[] { str, this.m_TrackerData });
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  protected void onInitialize()
  {
    try
    {
      Class localClass = Class.forName("net.oneplus.odm.insight.tracker.AppTracker");
      Constructor localConstructor = localClass.getConstructor(new Class[] { Context.class });
      this.m_TrackOnEvent = localClass.getMethod("onEvent", new Class[] { String.class, Map.class });
      this.m_AppTracker = localConstructor.newInstance(new Object[] { getCameraActivity() });
      this.m_TrackerData = new HashMap();
      this.m_CameraActivity = getCameraActivity();
      this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
      this.m_Settings = ((Settings)this.m_CameraActivity.get(CameraActivity.PROP_SETTINGS));
      this.m_SceneManager = ((SceneManager)findComponent(SceneManager.class));
      this.m_ZoomController = ((ZoomController)findComponent(ZoomController.class));
      this.m_FlashController = ((FlashController)findComponent(FlashController.class));
      this.m_ExposureController = ((ExposureController)findComponent(ExposureController.class));
      this.m_CameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
        {
          if (paramAnonymousPropertyChangeEventArgs.getNewValue() == PhotoCaptureState.STOPPING)
          {
            AppTrackerImpl.this.onEvent(0);
            AppTrackerImpl.-set0(AppTrackerImpl.this, Integer.valueOf(1));
            paramAnonymousPropertySource = (CaptureMode)AppTrackerImpl.-get0(AppTrackerImpl.this).get(CaptureModeManager.PROP_CAPTURE_MODE);
            if ((paramAnonymousPropertySource instanceof ManualCaptureMode)) {
              AppTrackerImpl.this.onEvent(1);
            }
            if ((paramAnonymousPropertySource instanceof PanoramaCaptureMode)) {
              AppTrackerImpl.this.onEvent(3);
            }
          }
        }
      });
      this.m_CameraActivity.addCallback(CameraActivity.PROP_IS_LAUNCHING, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
            AppTrackerImpl.this.onEvent(2);
          }
        }
      });
      this.m_CameraActivity.addHandler(CameraActivity.EVENT_BURST_PHOTO_RECEIVED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
        {
          AppTrackerImpl.-set0(AppTrackerImpl.this, Integer.valueOf(paramAnonymousCaptureEventArgs.getFrameIndex() + 1));
        }
      });
      this.m_CameraActivity.addHandler(CameraActivity.EVENT_CAPTURE_STARTED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
        {
          AppTrackerImpl.-set1(AppTrackerImpl.this, paramAnonymousCaptureEventArgs.getCaptureTrigger());
        }
      });
      this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
        {
          AppTrackerImpl.this.onEvent(4);
        }
      });
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      this.m_AppTracker = null;
      this.m_TrackOnEvent = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/AppTrackerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */