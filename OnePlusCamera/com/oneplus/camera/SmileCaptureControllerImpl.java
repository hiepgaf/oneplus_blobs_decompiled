package com.oneplus.camera;

import android.os.Message;
import android.util.Log;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Settings;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.ui.CaptureButtons;
import com.oneplus.camera.ui.CaptureButtons.Button;
import java.util.Iterator;
import java.util.List;

final class SmileCaptureControllerImpl
  extends CameraComponent
  implements SmileCaptureController
{
  private static final int COOL_DOWN_TIME_AFTER_TAKING_SMILE_CAPTURE = 500;
  private static final int DELAY_FOR_TAKING_SMILE_CAPTURE = 200;
  private static final int MSG_END_COOL_DOWN = 10001;
  private CaptureButtons m_CaptureButtons;
  private FaceTracker m_FaceTracker;
  private long m_FirstSmileCallbackTimeStamp = -1L;
  private FocusController m_FocusController;
  private boolean m_IsCoolingDown;
  private boolean m_IsSmileCaptureEnabled;
  private boolean m_IsSmileCapturing;
  private CaptureHandle m_PhotoCaptureHandle;
  
  static
  {
    Settings.setGlobalDefaultValue("SmileCapture.Back", Boolean.valueOf(false));
    Settings.setGlobalDefaultValue("SmileCapture.Front", Boolean.valueOf(false));
  }
  
  SmileCaptureControllerImpl(CameraActivity paramCameraActivity)
  {
    super("Smile capture controller", paramCameraActivity, true);
  }
  
  private boolean isSmileCaptureEnabled()
  {
    return this.m_IsSmileCaptureEnabled;
  }
  
  private void onFacesChanged(List<Camera.Face> paramList)
  {
    if (!isSmileCaptureEnabled()) {
      return;
    }
    if ((paramList == null) || (paramList.isEmpty()))
    {
      Log.w(this.TAG, "onFacesChanged() - faces is empty");
      resetFirstCallbackTime();
      return;
    }
    if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue())
    {
      Log.w(this.TAG, "onFacesChanged() - selftimer is runnning");
      resetFirstCallbackTime();
      return;
    }
    if (this.m_IsCoolingDown)
    {
      Log.w(this.TAG, "onFacesChanged() - is cooling down");
      resetFirstCallbackTime();
      return;
    }
    int i = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext()) {
      if (((Camera.Face)localIterator.next()).isSmiling()) {
        i += 1;
      }
    }
    Log.v(this.TAG, "onFacesChanged() - smilingCount : " + i + ", faces.size() : " + paramList.size());
    if ((i > 0) && (i >= paramList.size() / 2))
    {
      if (this.m_FirstSmileCallbackTimeStamp > 0L)
      {
        long l = Math.abs(System.currentTimeMillis() - this.m_FirstSmileCallbackTimeStamp);
        Log.v(this.TAG, "onFacesChanged() - timeDiff : " + l);
        if (l >= 200L)
        {
          triggerSmileCapture();
          return;
        }
        Log.v(this.TAG, "onFacesChanged() - waiting for smile capture");
        return;
      }
      Log.v(this.TAG, "onFacesChanged() - get first smile callback");
      this.m_FirstSmileCallbackTimeStamp = System.currentTimeMillis();
      return;
    }
    Log.v(this.TAG, "onFacesChanged() - not enough smile");
    resetFirstCallbackTime();
  }
  
  private void onFocusStateChanged(FocusState paramFocusState)
  {
    if (!isSmileCaptureEnabled()) {
      return;
    }
    if (paramFocusState == FocusState.SCANNING) {
      resetFirstCallbackTime();
    }
  }
  
  private void onKeepLastCaptureSettingsChanged(boolean paramBoolean)
  {
    if (paramBoolean) {
      return;
    }
    if (((Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA)).get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT) {}
    for (String str = "SmileCapture.Front";; str = "SmileCapture.Back")
    {
      getSettings().set(str, Boolean.valueOf(false));
      return;
    }
  }
  
  private void resetFirstCallbackTime()
  {
    Log.v(this.TAG, "resetFirstCallbackTime()");
    this.m_FirstSmileCallbackTimeStamp = -1L;
  }
  
  private boolean setSmileCaptureEnabledProp(boolean paramBoolean)
  {
    if (this.m_IsSmileCaptureEnabled == paramBoolean) {
      return false;
    }
    boolean bool = this.m_IsSmileCaptureEnabled;
    this.m_IsSmileCaptureEnabled = paramBoolean;
    if (((Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA)).get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT) {}
    for (String str = "SmileCapture.Front";; str = "SmileCapture.Back")
    {
      getSettings().set(str, Boolean.valueOf(paramBoolean));
      return notifyPropertyChanged(PROP_IS_SMILE_CAPTURE_ENABLED, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
    }
  }
  
  private void triggerSmileCapture()
  {
    Log.v(this.TAG, "triggerSmileCapture() - start");
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue())
    {
      Log.w(this.TAG, "triggerSmileCapture() - isReadyToCapture is false");
      return;
    }
    resetFirstCallbackTime();
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_CAPTURE_UI_ENABLED)).booleanValue())
    {
      Log.w(this.TAG, "triggerSmileCapture() - Capture UI is disabled");
      return;
    }
    this.m_IsSmileCapturing = true;
    if (((Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA)).get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT) {}
    for (String str = "SmileCapture.Timer.Front";; str = "SmileCapture.Timer.Back")
    {
      long l1 = Math.max(0L, getSettings().getLong(str, 2L));
      Log.d(this.TAG, "triggerSmileCapture() - get(PROP_SMILE_CAPTURE_WAITING_DURATION) : " + l1);
      long l2 = ((Long)getCameraActivity().get(CameraActivity.PROP_SELF_TIMER_INTERVAL)).longValue();
      getCameraActivity().set(CameraActivity.PROP_SELF_TIMER_INTERVAL, Long.valueOf(l1));
      if (this.m_CaptureButtons != null) {
        this.m_CaptureButtons.performButtonClick(CaptureButtons.Button.PRIMARY, 0);
      }
      getCameraActivity().set(CameraActivity.PROP_SELF_TIMER_INTERVAL, Long.valueOf(l2));
      Log.v(this.TAG, "triggerSmileCapture() - end");
      return;
    }
  }
  
  private void updateSmileCaptureEnableState()
  {
    Object localObject = getCamera();
    if (localObject == null)
    {
      Log.w(this.TAG, "updateSmileCaptureEnableState() - Camera is null");
      return;
    }
    int i = 1;
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((Camera.LensFacing)localObject.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      i = 0;
    }
    int j = i;
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[((MediaType)getCameraActivity().get(CameraActivity.PROP_MEDIA_TYPE)).ordinal()])
    {
    default: 
      j = i;
    case 1: 
      if (j != 0) {
        if (((Camera)getCameraActivity().get(CameraActivity.PROP_CAMERA)).get(Camera.PROP_LENS_FACING) != Camera.LensFacing.FRONT) {
          break label164;
        }
      }
      break;
    }
    label164:
    for (localObject = "SmileCapture.Front";; localObject = "SmileCapture.Back")
    {
      setSmileCaptureEnabledProp(getSettings().getBoolean((String)localObject, false));
      return;
      j = 0;
      break;
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_SMILE_CAPTURE_ENABLED) {
      return Boolean.valueOf(isSmileCaptureEnabled());
    }
    if (paramPropertyKey == PROP_IS_SMILE_CAPTURING) {
      return Boolean.valueOf(this.m_IsSmileCapturing);
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    this.m_IsCoolingDown = false;
  }
  
  protected void onDeinitialize()
  {
    resetFirstCallbackTime();
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_CaptureButtons = ((CaptureButtons)findComponent(CaptureButtons.class));
    this.m_FaceTracker = ((FaceTracker)findComponent(FaceTracker.class));
    this.m_FocusController = ((FocusController)findComponent(FocusController.class));
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        default: 
          return;
        }
        SmileCaptureControllerImpl.-set0(SmileCaptureControllerImpl.this, false);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        SmileCaptureControllerImpl.-wrap2(SmileCaptureControllerImpl.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        SmileCaptureControllerImpl.-wrap3(SmileCaptureControllerImpl.this);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_MEDIA_TYPE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<MediaType> paramAnonymousPropertyKey, PropertyChangeEventArgs<MediaType> paramAnonymousPropertyChangeEventArgs)
      {
        SmileCaptureControllerImpl.-wrap3(SmileCaptureControllerImpl.this);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.RESUMING) {
          SmileCaptureControllerImpl.-wrap3(SmileCaptureControllerImpl.this);
        }
      }
    });
    if (this.m_FaceTracker != null) {
      this.m_FaceTracker.addCallback(FaceTracker.PROP_FACES, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.Face>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.Face>> paramAnonymousPropertyChangeEventArgs)
        {
          SmileCaptureControllerImpl.-wrap0(SmileCaptureControllerImpl.this, (List)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
    }
    if (this.m_FocusController != null) {
      this.m_FocusController.addCallback(FocusController.PROP_FOCUS_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<FocusState> paramAnonymousPropertyKey, PropertyChangeEventArgs<FocusState> paramAnonymousPropertyChangeEventArgs)
        {
          SmileCaptureControllerImpl.-wrap1(SmileCaptureControllerImpl.this, (FocusState)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
    }
    onKeepLastCaptureSettingsChanged(((Boolean)localCameraActivity.get(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue());
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_IS_SMILE_CAPTURE_ENABLED) {
      return setSmileCaptureEnabledProp(((Boolean)paramTValue).booleanValue());
    }
    return super.set(paramPropertyKey, paramTValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/SmileCaptureControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */