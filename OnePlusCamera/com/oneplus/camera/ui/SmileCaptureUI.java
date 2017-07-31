package com.oneplus.camera.ui;

import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.SmileCaptureController;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.capturemode.PhotoCaptureMode;
import com.oneplus.camera.manual.ManualCaptureMode;

final class SmileCaptureUI
  extends UIComponent
{
  private static final int DELAY_TIME_TO_HIDE_TOAST = 1000;
  private static final int MSG_HIDE_TOAST = 10001;
  private CaptureModeManager m_CaptureModeManager;
  private OnScreenHint m_OnScreenHint;
  private View.OnClickListener m_OnToastClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!Handle.isValid(SmileCaptureUI.-get1(SmileCaptureUI.this))) {
        return;
      }
      paramAnonymousView = SmileCaptureUI.-get0(SmileCaptureUI.this);
      PropertyKey localPropertyKey = SmileCaptureController.PROP_IS_SMILE_CAPTURE_ENABLED;
      if (SmileCaptureUI.-wrap0(SmileCaptureUI.this)) {}
      for (boolean bool = false;; bool = true)
      {
        paramAnonymousView.set(localPropertyKey, Boolean.valueOf(bool));
        SmileCaptureUI.-wrap2(SmileCaptureUI.this);
        return;
      }
    }
  };
  private SmileCaptureController m_SmileCaptureController;
  private Handle m_SmileCaptureToastHandle;
  
  SmileCaptureUI(CameraActivity paramCameraActivity)
  {
    super("SmileCapture UI", paramCameraActivity, true);
  }
  
  private String getSmileToastString(boolean paramBoolean)
  {
    CameraActivity localCameraActivity = getCameraActivity();
    if (paramBoolean) {}
    for (int i = 2131558601;; i = 2131558602) {
      return localCameraActivity.getString(i);
    }
  }
  
  private boolean isSmileCaptureEnabled()
  {
    if (this.m_SmileCaptureController != null)
    {
      boolean bool = true;
      CaptureMode localCaptureMode;
      if (this.m_CaptureModeManager != null)
      {
        localCaptureMode = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
        if ((localCaptureMode instanceof ManualCaptureMode)) {
          break label67;
        }
      }
      label67:
      for (bool = localCaptureMode instanceof PhotoCaptureMode; ((Boolean)this.m_SmileCaptureController.get(SmileCaptureController.PROP_IS_SMILE_CAPTURE_ENABLED)).booleanValue(); bool = true) {
        return bool;
      }
      return false;
    }
    return false;
  }
  
  private void onActivityStateChanged(BaseActivity.State paramState)
  {
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[paramState.ordinal()])
    {
    default: 
      return;
    }
    updateToastVisibility();
  }
  
  private void showSmileCaptureToast()
  {
    if (!Handle.isValid(this.m_SmileCaptureToastHandle)) {
      this.m_SmileCaptureToastHandle = this.m_OnScreenHint.showHint(getSmileToastString(isSmileCaptureEnabled()), this.m_OnToastClickListener, 20);
    }
  }
  
  private void updateToastContent()
  {
    if (Handle.isValid(this.m_SmileCaptureToastHandle))
    {
      Log.v(this.TAG, "updateToastContent() -  updateToastContent : " + isSmileCaptureEnabled());
      HandlerUtils.removeMessages(this, 10001);
      this.m_OnScreenHint.updateHint(this.m_SmileCaptureToastHandle, getSmileToastString(isSmileCaptureEnabled()), 0);
      if (!isSmileCaptureEnabled()) {
        HandlerUtils.sendMessage(this, 10001, 1000L);
      }
    }
  }
  
  private void updateToastVisibility()
  {
    Log.v(this.TAG, "updateToastVisibility() -  isSmileCaptureEnabled : " + isSmileCaptureEnabled());
    if (isSmileCaptureEnabled())
    {
      showSmileCaptureToast();
      return;
    }
    Handle.close(this.m_SmileCaptureToastHandle);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    updateToastVisibility();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_SmileCaptureController = ((SmileCaptureController)findComponent(SmileCaptureController.class));
    this.m_OnScreenHint = ((OnScreenHint)findComponent(OnScreenHint.class));
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        SmileCaptureUI.-wrap1(SmileCaptureUI.this, (BaseActivity.State)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_READY_TO_CAPTURE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          SmileCaptureUI.-wrap3(SmileCaptureUI.this);
        }
      }
    });
    updateToastVisibility();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SmileCaptureUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */