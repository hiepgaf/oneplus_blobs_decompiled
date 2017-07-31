package com.oneplus.camera.scene;

import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.FlashController;
import com.oneplus.camera.FlashController.FlashDisabledReason;
import com.oneplus.camera.OperationState;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.media.MediaType;
import java.util.List;

public abstract class PhotoScene
  extends BasicScene
{
  protected static final int FLAG_DISABLE_BURST_CAPTURE = 4;
  protected static final int FLAG_DISABLE_FLASH = 1;
  protected static final int FLAG_DISABLE_SELF_TIMER = 8;
  protected static final int FLAG_LOCK_FOCUS_BEFORE_CAPTURE = 16;
  protected static final int FLAG_LONG_CAPTURE_TIME = 2;
  private Handle m_BurstDisableHandle;
  private Camera m_Camera;
  PropertyChangedCallback<OperationState> m_CameraPreviewStateCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
    {
      if (paramAnonymousPropertyChangeEventArgs.getNewValue() == OperationState.STARTED)
      {
        paramAnonymousPropertySource = PhotoScene.-wrap0(PhotoScene.this);
        PhotoScene.this.checkSceneModeValid(paramAnonymousPropertySource);
      }
    }
  };
  private long m_CaptureDelayTime;
  private Handle m_CaptureDelayTimeHandle;
  private CaptureModeManager m_CaptureModeManager;
  private final int m_Flags;
  private FlashController m_FlashController;
  private Handle m_FlashDisableHandle;
  PropertyChangedCallback<Boolean> m_IsCameraSwitchingCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
      {
        paramAnonymousPropertySource = PhotoScene.-wrap0(PhotoScene.this);
        PhotoScene.this.checkSceneModeValid(paramAnonymousPropertySource);
      }
    }
  };
  private Handle m_PreCaptureFocusLockReqHandle;
  private final Integer m_SceneMode;
  private Handle m_SelfTimerDisableHandle;
  
  protected PhotoScene(CameraActivity paramCameraActivity, String paramString, int paramInt)
  {
    this(paramCameraActivity, paramString, null, paramInt);
  }
  
  protected PhotoScene(CameraActivity paramCameraActivity, String paramString, int paramInt1, int paramInt2)
  {
    this(paramCameraActivity, paramString, Integer.valueOf(paramInt1), paramInt2);
  }
  
  private PhotoScene(CameraActivity paramCameraActivity, String paramString, Integer paramInteger, int paramInt)
  {
    super(paramCameraActivity, paramString);
    this.m_SceneMode = paramInteger;
    this.m_Flags = paramInt;
    paramCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_SWITCHING, this.m_IsCameraSwitchingCallback);
    paramCameraActivity.addCallback(CameraActivity.PROP_CAMERA_PREVIEW_STATE, this.m_CameraPreviewStateCallback);
  }
  
  protected void checkSceneModeValid(Camera paramCamera)
  {
    if (this.m_SceneMode != null)
    {
      if ((paramCamera != null) && (((List)paramCamera.get(Camera.PROP_SCENE_MODES)).contains(this.m_SceneMode)) && (getMediaType() == MediaType.PHOTO)) {
        enable();
      }
    }
    else {
      return;
    }
    disable();
  }
  
  public boolean needToShowProcessingDialog()
  {
    return (this.m_Flags & 0x2) != 0;
  }
  
  protected void onCameraChanged(Camera paramCamera)
  {
    super.onCameraChanged(paramCamera);
    if (this.m_SceneMode != null) {
      if ((this.m_Camera != null) && (!HandlerUtils.post(paramCamera, new SceneApplyRunnable(this.m_Camera, 0)))) {
        break label168;
      }
    }
    for (;;)
    {
      if (isEntered()) {
        this.m_Camera = paramCamera;
      }
      if ((paramCamera == null) || (!((List)paramCamera.get(Camera.PROP_SCENE_MODES)).contains(this.m_SceneMode)) || (getMediaType() != MediaType.PHOTO)) {
        break label190;
      }
      enable();
      if (this.m_CaptureModeManager == null) {
        this.m_CaptureModeManager = ((CaptureModeManager)getCameraActivity().findComponent(CaptureModeManager.class));
      }
      if ((!((Boolean)this.m_CaptureModeManager.get(CaptureModeManager.PROP_IS_CAPTURE_MODE_SWITCHING)).booleanValue()) && (isEntered()) && (!HandlerUtils.post(paramCamera, new SceneApplyRunnable(paramCamera, this.m_SceneMode.intValue())))) {
        break;
      }
      return;
      label168:
      Log.e(this.TAG, "onCameraChanged() - Fail to reset scene mode asynchronously");
    }
    Log.e(this.TAG, "onCameraChanged() - Fail to set scene mode asynchronously");
    return;
    label190:
    disable();
  }
  
  protected boolean onEnter(Scene paramScene, int paramInt)
  {
    if (this.m_SceneMode == null) {
      return true;
    }
    this.m_Camera = getCamera();
    if (this.m_Camera == null)
    {
      Log.e(this.TAG, "onEnter() - No camera to enter scene");
      return false;
    }
    if (!HandlerUtils.post(this.m_Camera, new SceneApplyRunnable(this.m_Camera, this.m_SceneMode.intValue())))
    {
      Log.e(this.TAG, "onEnter() - Fail to perform cross-thread operation");
      return false;
    }
    if ((this.m_Flags & 0x1) != 0)
    {
      if (this.m_FlashController == null) {
        this.m_FlashController = ((FlashController)getCameraActivity().findComponent(FlashController.class));
      }
      if (this.m_FlashController == null) {
        break label219;
      }
      this.m_FlashDisableHandle = this.m_FlashController.disableFlash(FlashController.FlashDisabledReason.NOT_SUPPORTED_IN_SCENE, 0);
    }
    for (;;)
    {
      if ((this.m_Flags & 0x4) != 0) {
        this.m_BurstDisableHandle = getCameraActivity().disableBurstPhotoCapture();
      }
      if ((this.m_Flags & 0x8) != 0) {
        this.m_SelfTimerDisableHandle = getCameraActivity().disableSelfTimer();
      }
      if ((this.m_Flags & 0x10) != 0) {
        this.m_PreCaptureFocusLockReqHandle = getCameraActivity().requestPreCaptureFocusLock();
      }
      if (this.m_CaptureDelayTime > 0L) {
        this.m_CaptureDelayTimeHandle = getCameraActivity().setCaptureDelayTime(this.m_CaptureDelayTime);
      }
      return true;
      label219:
      Log.e(this.TAG, "onEnter() - No FlashController interface");
    }
  }
  
  protected void onExit(Scene paramScene, int paramInt)
  {
    if (this.m_SceneMode == null) {
      return;
    }
    if (this.m_Camera == null)
    {
      Log.w(this.TAG, "onExit() - No camera to exit scene");
      return;
    }
    paramScene = this.m_Camera;
    this.m_Camera = null;
    if (!HandlerUtils.post(paramScene, new SceneApplyRunnable(paramScene, 0))) {
      Log.e(this.TAG, "onExit() - Fail to perform cross-thread operation");
    }
    this.m_FlashDisableHandle = Handle.close(this.m_FlashDisableHandle);
    this.m_BurstDisableHandle = Handle.close(this.m_BurstDisableHandle);
    this.m_SelfTimerDisableHandle = Handle.close(this.m_SelfTimerDisableHandle);
    this.m_PreCaptureFocusLockReqHandle = Handle.close(this.m_PreCaptureFocusLockReqHandle);
    this.m_CaptureDelayTimeHandle = Handle.close(this.m_CaptureDelayTimeHandle);
  }
  
  protected void onMediaTypeChanged(MediaType paramMediaType)
  {
    super.onMediaTypeChanged(paramMediaType);
    if (paramMediaType == MediaType.PHOTO)
    {
      if (this.m_SceneMode != null)
      {
        paramMediaType = getCamera();
        if ((paramMediaType != null) && (((List)paramMediaType.get(Camera.PROP_SCENE_MODES)).contains(this.m_SceneMode)))
        {
          enable();
          return;
        }
        disable();
        return;
      }
      enable();
      return;
    }
    disable();
  }
  
  protected void onRelease()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.removeCallback(CameraActivity.PROP_IS_CAMERA_SWITCHING, this.m_IsCameraSwitchingCallback);
    localCameraActivity.removeCallback(CameraActivity.PROP_CAMERA_PREVIEW_STATE, this.m_CameraPreviewStateCallback);
    super.onRelease();
  }
  
  protected void setCaptureDelayTime(long paramLong)
  {
    verifyAccess();
    if (this.m_CaptureDelayTime == paramLong) {
      return;
    }
    this.m_CaptureDelayTime = paramLong;
    this.m_CaptureDelayTimeHandle = Handle.close(this.m_CaptureDelayTimeHandle);
    if ((isEntered()) && (paramLong > 0L)) {
      this.m_CaptureDelayTimeHandle = getCameraActivity().setCaptureDelayTime(paramLong);
    }
  }
  
  private final class SceneApplyRunnable
    implements Runnable
  {
    private final Camera m_Camera;
    private final int m_SceneMode;
    
    public SceneApplyRunnable(Camera paramCamera, int paramInt)
    {
      this.m_Camera = paramCamera;
      this.m_SceneMode = paramInt;
    }
    
    public void run()
    {
      try
      {
        this.m_Camera.set(Camera.PROP_SCENE_MODE, Integer.valueOf(this.m_SceneMode));
        return;
      }
      catch (Throwable localThrowable)
      {
        Log.e(PhotoScene.-get0(PhotoScene.this), "Fail to set scene mode to " + this.m_SceneMode, localThrowable);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/PhotoScene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */