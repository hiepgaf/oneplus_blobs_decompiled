package com.oneplus.camera.capturemode;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CaptureEventArgs;
import com.oneplus.camera.IntentEventArgs;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.OperationState;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.scene.AutoHdrScene;
import com.oneplus.camera.scene.PhotoScene;
import com.oneplus.camera.scene.Scene;
import com.oneplus.camera.scene.SceneEventArgs;
import com.oneplus.camera.scene.SceneManager;
import com.oneplus.camera.ui.ProcessingDialog;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PhotoCaptureMode
  extends SimpleCaptureMode
{
  private static final long DURATION_SHOW_PROCESSING_DIALOG_DELAY = 1200L;
  private static final String SETTINGS_KEY_SCENE_BACK = "Scene.Back";
  private static final String SETTINGS_KEY_SCENE_FRONT = "Scene.Front";
  private final PropertyChangedCallback<BaseActivity.State> m_ActivityStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
    {
      PhotoCaptureMode.-wrap0(PhotoCaptureMode.this, (BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final PropertyChangedCallback<Camera> m_CameraChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
    {
      PhotoCaptureMode.-wrap1(PhotoCaptureMode.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final PropertyChangedCallback<OperationState> m_CameraPreviewStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
    {
      PhotoCaptureMode.-wrap2(PhotoCaptureMode.this, (OperationState)paramAnonymousPropertyChangeEventArgs.getOldValue(), (OperationState)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final PropertyChangedCallback<PhotoCaptureState> m_CaptureStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
    {
      PhotoCaptureMode.-wrap4(PhotoCaptureMode.this, (PhotoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private boolean m_IsSceneResetNeeded;
  private final PropertyChangedCallback<Boolean> m_KeepLastCaptureSettingsCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      PhotoCaptureMode.-wrap3(PhotoCaptureMode.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  private final EventHandler<IntentEventArgs> m_PrepareAdvancedSettingsHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<IntentEventArgs> paramAnonymousEventKey, IntentEventArgs paramAnonymousIntentEventArgs)
    {
      PhotoCaptureMode.-wrap5(PhotoCaptureMode.this, paramAnonymousIntentEventArgs.getIntent());
    }
  };
  private ProcessingDialog m_ProcessingDialog;
  private Handle m_ProcessingDialogHandle;
  private final EventHandler<SceneEventArgs> m_SceneAddedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<SceneEventArgs> paramAnonymousEventKey, SceneEventArgs paramAnonymousSceneEventArgs)
    {
      PhotoCaptureMode.-wrap6(PhotoCaptureMode.this, paramAnonymousSceneEventArgs.getScene());
    }
  };
  private final PropertyChangedCallback<Scene> m_SceneChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Scene> paramAnonymousPropertyKey, PropertyChangeEventArgs<Scene> paramAnonymousPropertyChangeEventArgs)
    {
      PhotoCaptureMode.-wrap8(PhotoCaptureMode.this, (Scene)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private Map<Camera.LensFacing, String> m_SceneIds = new HashMap();
  private SceneManager m_SceneManager;
  private final PropertyChangedCallback<Scene> m_SceneUserSelectedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Scene> paramAnonymousPropertyKey, PropertyChangeEventArgs<Scene> paramAnonymousPropertyChangeEventArgs)
    {
      PhotoCaptureMode.-wrap7(PhotoCaptureMode.this, (Scene)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private Settings m_Settings;
  private final Runnable m_ShowProcessingDialogRunnable = new Runnable()
  {
    public void run()
    {
      PhotoCaptureMode.-wrap10(PhotoCaptureMode.this);
    }
  };
  private final EventHandler<CaptureEventArgs> m_ShutterHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
    {
      PhotoCaptureMode.this.onShutter(paramAnonymousCaptureEventArgs);
    }
  };
  private boolean m_WaitForSceneModeReady;
  
  static
  {
    Settings.addPrivateKey("Scene.Back");
    Settings.addPrivateKey("Scene.Front");
  }
  
  public PhotoCaptureMode(CameraActivity paramCameraActivity)
  {
    this(paramCameraActivity, "photo");
  }
  
  public PhotoCaptureMode(CameraActivity paramCameraActivity, String paramString)
  {
    super(paramCameraActivity, "Photo", MediaType.PHOTO, paramString);
    paramCameraActivity.addCallback(CameraActivity.PROP_CAMERA, this.m_CameraChangedCallback);
    paramCameraActivity.addCallback(CameraActivity.PROP_CAMERA_PREVIEW_STATE, this.m_CameraPreviewStateChangedCallback);
    paramCameraActivity.addCallback(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS, this.m_KeepLastCaptureSettingsCallback);
    paramCameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, this.m_CaptureStateChangedCallback);
    paramCameraActivity.addHandler(CameraActivity.EVENT_SHUTTER, this.m_ShutterHandler);
    this.m_IsSceneResetNeeded = true;
    ComponentUtils.findComponent(paramCameraActivity, SceneManager.class, paramCameraActivity, new ComponentSearchCallback()
    {
      public void onComponentFound(SceneManager paramAnonymousSceneManager)
      {
        PhotoCaptureMode.-wrap9(PhotoCaptureMode.this, paramAnonymousSceneManager);
      }
    });
  }
  
  private void applyScene(Camera paramCamera)
  {
    if (this.m_SceneManager == null)
    {
      Log.w(this.TAG, "applyScene() - No SceneManager interface");
      return;
    }
    if (paramCamera == null)
    {
      Log.w(this.TAG, "applyScene() - No camera");
      return;
    }
    Object localObject1 = (OperationState)getCameraActivity().get(CameraActivity.PROP_CAMERA_PREVIEW_STATE);
    Object localObject2;
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[localObject1.ordinal()])
    {
    default: 
      if (this.m_IsSceneResetNeeded)
      {
        resetToDefaultScene();
        this.m_IsSceneResetNeeded = false;
      }
      localObject2 = (Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING);
      paramCamera = (String)this.m_SceneIds.get(localObject2);
      if (paramCamera == null)
      {
        localObject1 = (Settings)getCameraActivity().get(CameraActivity.PROP_SETTINGS);
        switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[localObject2.ordinal()])
        {
        default: 
          Log.v(this.TAG, "applyScene() - Scene id is empty, use settings: ", paramCamera);
          label179:
          if (this.m_SceneManager != null)
          {
            if (paramCamera != null) {
              break label345;
            }
            paramCamera = (List)this.m_SceneManager.get(SceneManager.PROP_SCENES);
            localObject1 = null;
            Iterator localIterator = paramCamera.iterator();
            do
            {
              paramCamera = (Camera)localObject1;
              if (!localIterator.hasNext()) {
                break;
              }
              paramCamera = (Scene)localIterator.next();
            } while (!(paramCamera instanceof AutoHdrScene));
            if (paramCamera == null) {
              break label328;
            }
            this.m_SceneManager.setScene(paramCamera, 0);
            label262:
            this.m_SceneIds.put(localObject2, "Auto-HDR");
          }
          break;
        }
      }
      break;
    }
    label328:
    label345:
    label488:
    for (;;)
    {
      return;
      Log.v(this.TAG, "applyScene() - Preview stopped, wait for preview stating");
      this.m_WaitForSceneModeReady = true;
      return;
      paramCamera = ((Settings)localObject1).getString("Scene.Back");
      break;
      paramCamera = ((Settings)localObject1).getString("Scene.Front");
      break;
      Log.v(this.TAG, "applyScene() - Scene id: ", paramCamera);
      break label179;
      this.m_SceneManager.setScene(Scene.NO_SCENE, 0);
      break label262;
      if (paramCamera.equals(Scene.NO_SCENE.get(Scene.PROP_ID)))
      {
        this.m_SceneManager.setScene(Scene.NO_SCENE, 0);
        return;
      }
      localObject1 = (List)this.m_SceneManager.get(SceneManager.PROP_SCENES);
      int i = ((List)localObject1).size() - 1;
      for (;;)
      {
        if (i < 0) {
          break label488;
        }
        localObject2 = (Scene)((List)localObject1).get(i);
        if (((String)((Scene)localObject2).get(Scene.PROP_ID)).equals(paramCamera))
        {
          if (this.m_SceneManager.setScene((Scene)localObject2, 0)) {
            break;
          }
          Log.e(this.TAG, "applyScene() - Fail to change scene to " + localObject2);
          return;
        }
        i -= 1;
      }
    }
  }
  
  private boolean isRawCaptureEnabled()
  {
    return false;
  }
  
  private void onActivityStateChanged(BaseActivity.State paramState)
  {
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[paramState.ordinal()])
    {
    default: 
      return;
    }
    Log.w(this.TAG, "onActivityStateChanged() - isRawCaptureEnabled : " + isRawCaptureEnabled());
    setRawCaptureState(getCamera(), isRawCaptureEnabled());
  }
  
  private void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    if (isEntered())
    {
      Log.v(this.TAG, "onCameraChanged() - Reset raw capture property");
      setRawCaptureState(paramCamera1, false);
      setRawCaptureState(paramCamera2, isRawCaptureEnabled());
      applyScene(paramCamera2);
    }
  }
  
  private void onCameraPreviewStateChanged(OperationState paramOperationState1, OperationState paramOperationState2)
  {
    if ((paramOperationState2 == OperationState.STARTING) && (this.m_WaitForSceneModeReady))
    {
      applyScene(getCamera());
      this.m_WaitForSceneModeReady = false;
    }
  }
  
  private void onKeepLastCaptureSettingsChanged(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.m_IsSceneResetNeeded = true;
      if (isEntered()) {
        applyScene(getCamera());
      }
    }
  }
  
  private void onPhotoCaptureStateChanged(PhotoCaptureState paramPhotoCaptureState)
  {
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[paramPhotoCaptureState.ordinal()])
    {
    default: 
      return;
    }
    if (this.m_ShowProcessingDialogRunnable != null) {
      HandlerUtils.removeCallbacks(getCameraActivity(), this.m_ShowProcessingDialogRunnable);
    }
    this.m_ProcessingDialogHandle = Handle.close(this.m_ProcessingDialogHandle);
  }
  
  private void onPrepareAdvancedSettings(Intent paramIntent)
  {
    Camera localCamera = getCamera();
    if (localCamera != null) {
      if (((Boolean)localCamera.get(Camera.PROP_IS_RAW_CAPTURE_SUPPORTED)).booleanValue()) {
        if (!getCameraActivity().isServiceMode()) {
          break label76;
        }
      }
    }
    label76:
    for (boolean bool = false;; bool = true)
    {
      paramIntent.putExtra("IsRawCaptureVisible", bool);
      if (((Boolean)localCamera.get(Camera.PROP_IS_SMILE_CAPTURE_SUPPORTED)).booleanValue()) {
        paramIntent.putExtra("IsSmileCaptureVisible", true);
      }
      return;
    }
  }
  
  private void onSceneAdded(Scene paramScene)
  {
    Object localObject = getCamera();
    if (localObject == null) {
      return;
    }
    if (!isEntered()) {
      return;
    }
    localObject = (String)this.m_SceneIds.get(((Camera)localObject).get(Camera.PROP_LENS_FACING));
    if (localObject == null) {
      return;
    }
    if (((String)localObject).equals(paramScene.get(Scene.PROP_ID)))
    {
      Log.v(this.TAG, "onSceneAdded() - Change scene to ", paramScene);
      if (!this.m_SceneManager.setScene(paramScene, 0)) {
        Log.e(this.TAG, "onSceneAdded() - Fail to change scene to " + paramScene);
      }
    }
  }
  
  private void onSceneChanged(Scene paramScene)
  {
    if (!isEntered()) {
      return;
    }
    Camera localCamera = getCamera();
    if (localCamera == null) {
      return;
    }
    if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_CAMERA_SWITCHING)).booleanValue())
    {
      Log.v(this.TAG, "onSceneChanged() - Ignore when switching camera");
      return;
    }
    this.m_SceneIds.put((Camera.LensFacing)localCamera.get(Camera.PROP_LENS_FACING), (String)paramScene.get(Scene.PROP_ID));
  }
  
  private void onSceneChangedByUser(Scene paramScene)
  {
    if (!isEntered()) {
      return;
    }
    Camera localCamera = getCamera();
    if (localCamera == null) {
      return;
    }
    Object localObject = getCameraActivity();
    if (((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_CAMERA_SWITCHING)).booleanValue())
    {
      Log.v(this.TAG, "onSceneChangedByUser() - Ignore when switching camera");
      return;
    }
    localObject = (Settings)((CameraActivity)localObject).get(CameraActivity.PROP_SETTINGS);
    paramScene = (String)paramScene.get(Scene.PROP_ID);
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((Camera.LensFacing)localCamera.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      return;
    case 1: 
      ((Settings)localObject).set("Scene.Back", paramScene);
      return;
    }
    ((Settings)localObject).set("Scene.Front", paramScene);
  }
  
  private void onSceneManagerFound(SceneManager paramSceneManager)
  {
    this.m_SceneManager = paramSceneManager;
    paramSceneManager.addCallback(SceneManager.PROP_SCENE, this.m_SceneChangedCallback);
    paramSceneManager.addCallback(SceneManager.PROP_SCENE_USER_SELECTED, this.m_SceneUserSelectedCallback);
    paramSceneManager.addHandler(SceneManager.EVENT_SCENE_ADDED, this.m_SceneAddedHandler);
    if (isEntered()) {
      applyScene(getCamera());
    }
  }
  
  private void resetToDefaultScene()
  {
    String str1 = (String)Scene.NO_SCENE.get(Scene.PROP_ID);
    Settings localSettings = (Settings)getCameraActivity().get(CameraActivity.PROP_SETTINGS);
    String str2 = localSettings.getString("Scene.Back");
    String str3;
    if ((str2 == null) || (str2.equals(str1)))
    {
      str3 = localSettings.getString("Scene.Front");
      if ((str3 != null) && (!str3.equals(str1))) {
        break label122;
      }
    }
    for (;;)
    {
      Log.v(this.TAG, "resetToDefaultScene() - Reset to default scene: ", this.m_SceneIds, ", settings back: ", str2, ", settings front: ", str3);
      return;
      localSettings.set("Scene.Back", "Auto-HDR");
      this.m_SceneIds.put(Camera.LensFacing.BACK, "Auto-HDR");
      break;
      label122:
      localSettings.set("Scene.Front", "Auto-HDR");
      this.m_SceneIds.put(Camera.LensFacing.FRONT, "Auto-HDR");
    }
  }
  
  private void setRawCaptureState(final Camera paramCamera, final boolean paramBoolean)
  {
    if (paramCamera == null)
    {
      Log.w(this.TAG, "setRawCaptureState() - camera is null");
      return;
    }
    Log.v(this.TAG, "setRawCaptureState() - Set raw capture of ", paramCamera.get(Camera.PROP_LENS_FACING), " to ", Boolean.valueOf(paramBoolean));
    HandlerUtils.post(paramCamera, new Runnable()
    {
      public void run()
      {
        paramCamera.set(Camera.PROP_IS_RAW_CAPTURE_ENABLED, Boolean.valueOf(paramBoolean));
      }
    });
  }
  
  private void showProcessingDialog()
  {
    if (Handle.isValid(this.m_ProcessingDialogHandle)) {
      return;
    }
    if (this.m_ProcessingDialog == null)
    {
      this.m_ProcessingDialog = ((ProcessingDialog)getCameraActivity().findComponent(ProcessingDialog.class));
      if (this.m_ProcessingDialog == null)
      {
        Log.w(this.TAG, "showProcessingDialog() - No ProcessingDialog interface");
        return;
      }
    }
    this.m_ProcessingDialogHandle = this.m_ProcessingDialog.showProcessingDialog(null, 0);
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558473);
  }
  
  public Drawable getImage(CaptureMode.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-capturemode-CaptureMode$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    case 1: 
      return getCameraActivity().getDrawable(2130837539);
    }
    return getCameraActivity().getDrawable(2130837547);
  }
  
  protected boolean onEnter(CaptureMode paramCaptureMode, int paramInt)
  {
    if (!super.onEnter(paramCaptureMode, paramInt)) {
      return false;
    }
    paramCaptureMode = getCameraActivity();
    this.m_Settings = new Settings(paramCaptureMode, null, true);
    applyScene(getCamera());
    paramCaptureMode.addCallback(CameraActivity.PROP_STATE, this.m_ActivityStateChangedCallback);
    paramCaptureMode.addHandler(OPCameraActivity.EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE, this.m_PrepareAdvancedSettingsHandler);
    Log.w(this.TAG, "onEnter() - isRawCaptureEnabled : " + isRawCaptureEnabled());
    setRawCaptureState(getCamera(), isRawCaptureEnabled());
    return true;
  }
  
  protected void onExit(CaptureMode paramCaptureMode, int paramInt)
  {
    super.onExit(paramCaptureMode, paramInt);
    paramCaptureMode = getCameraActivity();
    paramCaptureMode.removeCallback(CameraActivity.PROP_STATE, this.m_ActivityStateChangedCallback);
    paramCaptureMode.removeHandler(OPCameraActivity.EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE, this.m_PrepareAdvancedSettingsHandler);
    Log.w(this.TAG, "onExit() - reset raw capture property");
    setRawCaptureState(getCamera(), false);
  }
  
  protected void onRelease()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.removeCallback(CameraActivity.PROP_CAMERA, this.m_CameraChangedCallback);
    localCameraActivity.removeCallback(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS, this.m_KeepLastCaptureSettingsCallback);
    localCameraActivity.removeCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, this.m_CaptureStateChangedCallback);
    localCameraActivity.removeHandler(CameraActivity.EVENT_SHUTTER, this.m_ShutterHandler);
    if (this.m_SceneManager != null)
    {
      this.m_SceneManager.removeCallback(SceneManager.PROP_SCENE, this.m_SceneChangedCallback);
      this.m_SceneManager.removeCallback(SceneManager.PROP_SCENE_USER_SELECTED, this.m_SceneUserSelectedCallback);
      this.m_SceneManager.removeHandler(SceneManager.EVENT_SCENE_ADDED, this.m_SceneAddedHandler);
    }
    if (this.m_Settings != null) {
      this.m_Settings.release();
    }
    super.onRelease();
  }
  
  protected void onShutter(CaptureEventArgs paramCaptureEventArgs)
  {
    if (!isEntered()) {
      return;
    }
    if (paramCaptureEventArgs.isBurstPhotoCapture()) {
      return;
    }
    if (this.m_SceneManager != null)
    {
      paramCaptureEventArgs = (Scene)this.m_SceneManager.get(SceneManager.PROP_SCENE);
      if ((paramCaptureEventArgs instanceof PhotoScene))
      {
        if (((PhotoScene)paramCaptureEventArgs).needToShowProcessingDialog()) {}
      }
      else {
        Log.v(this.TAG, "onShutter() - Invalid scene in photo capture mode, scene : ", paramCaptureEventArgs);
      }
    }
    else
    {
      Log.v(this.TAG, "onShutter() - No scene manager");
      return;
    }
    paramCaptureEventArgs = getCameraActivity();
    HandlerUtils.removeCallbacks(paramCaptureEventArgs, this.m_ShowProcessingDialogRunnable);
    HandlerUtils.post(paramCaptureEventArgs, this.m_ShowProcessingDialogRunnable, 1200L);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/PhotoCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */