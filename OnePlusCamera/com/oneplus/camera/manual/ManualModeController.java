package com.oneplus.camera.manual;

import android.os.Message;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraThread;
import com.oneplus.camera.FocusMode;
import com.oneplus.camera.ModeController;

final class ManualModeController
  extends ModeController<ManualModeUI>
{
  private static final int FLAG_NOT_BACKUP_VALUE = 1;
  static final int MSG_SET_AWB = 30041;
  static final int MSG_SET_COLOR_TEMPERATURE = 30046;
  static final int MSG_SET_EV = 30045;
  static final int MSG_SET_EXPOSURE = 30042;
  static final int MSG_SET_FOCUS = 30043;
  static final int MSG_SET_ISO = 30044;
  private final PropertyChangedCallback m_ActivePictureInfoChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
    {
      if (ManualModeController.-wrap0(ManualModeController.this)) {
        HandlerUtils.sendMessage(ManualModeController.-wrap1(ManualModeController.this), 10001);
      }
    }
  };
  private int m_AwbMode = 1;
  private int m_ColorTemperature = 0;
  private float m_EV = 0.0F;
  private long m_Exposure = -1L;
  private float m_Focus = -1.0F;
  private int m_ISO = -1;
  
  ManualModeController(CameraThread paramCameraThread)
  {
    super("Manual Mode Controller", paramCameraThread);
  }
  
  private void applyAutoValues()
  {
    if (!isColorTemperatureSupported()) {
      setAwb(1, 1);
    }
    for (;;)
    {
      setExposure(-1L, 1);
      setFocus(-1.0F, 1);
      setISO(-1, 1);
      setEV(0.0F, 1);
      return;
      setColorTemperature(0, 1);
    }
  }
  
  private void applyLastValues()
  {
    if (!isColorTemperatureSupported()) {
      setAwb(this.m_AwbMode, 1);
    }
    for (;;)
    {
      setExposure(this.m_Exposure, 1);
      setFocus(this.m_Focus, 1);
      setISO(this.m_ISO, 1);
      setEV(this.m_EV, 1);
      return;
      setColorTemperature(this.m_ColorTemperature, 1);
    }
  }
  
  private void disableManualCapture(Camera paramCamera)
  {
    if (paramCamera == null)
    {
      Log.w(this.TAG, "disableManualCapture() - No camera");
      return;
    }
    paramCamera.removeCallback(Camera.PROP_ACTIVE_COLOR_TEMPERATURE, this.m_ActivePictureInfoChangedCB);
    paramCamera.removeCallback(Camera.PROP_ACTIVE_EXPOSURE_COMPENSATION, this.m_ActivePictureInfoChangedCB);
    paramCamera.removeCallback(Camera.PROP_ACTIVE_EXPOSURE_TIME_NANOS, this.m_ActivePictureInfoChangedCB);
    paramCamera.removeCallback(Camera.PROP_ACTIVE_ISO, this.m_ActivePictureInfoChangedCB);
    paramCamera.set(Camera.PROP_IS_MANUAL_CAPTURE, Boolean.valueOf(false));
  }
  
  private void enableManualCapture(Camera paramCamera)
  {
    if (paramCamera == null)
    {
      Log.w(this.TAG, "enableManualCapture() - No camera");
      return;
    }
    paramCamera.set(Camera.PROP_IS_MANUAL_CAPTURE, Boolean.valueOf(true));
    paramCamera.addCallback(Camera.PROP_ACTIVE_COLOR_TEMPERATURE, this.m_ActivePictureInfoChangedCB);
    paramCamera.addCallback(Camera.PROP_ACTIVE_EXPOSURE_COMPENSATION, this.m_ActivePictureInfoChangedCB);
    paramCamera.addCallback(Camera.PROP_ACTIVE_EXPOSURE_TIME_NANOS, this.m_ActivePictureInfoChangedCB);
    paramCamera.addCallback(Camera.PROP_ACTIVE_ISO, this.m_ActivePictureInfoChangedCB);
  }
  
  private boolean isColorTemperatureSupported()
  {
    Camera localCamera = getCamera();
    if (localCamera != null) {
      return ((Boolean)localCamera.get(Camera.PROP_IS_COLOR_TEMPERATURE_SUPPORTED)).booleanValue();
    }
    Log.d(this.TAG, "isColorTemperatureSupported() - camera is null.");
    return false;
  }
  
  private void setAwb(int paramInt1, int paramInt2)
  {
    Log.d(this.TAG, "setAwb() - value : " + paramInt1);
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.w(this.TAG, "setAwb() - Cannot get camera");
      return;
    }
    if ((paramInt2 & 0x1) == 0) {
      this.m_AwbMode = paramInt1;
    }
    localCamera.set(Camera.PROP_AWB_MODE, Integer.valueOf(paramInt1));
  }
  
  private void setColorTemperature(int paramInt1, int paramInt2)
  {
    Log.d(this.TAG, "setColorTemperature() - value : " + paramInt1);
    if (paramInt1 == 0) {}
    Camera localCamera;
    for (int i = 1;; i = 101)
    {
      setAwb(i, paramInt2);
      localCamera = getCamera();
      if (localCamera != null) {
        break;
      }
      Log.w(this.TAG, "setColorTemperature() - Cannot get camera");
      return;
    }
    if ((paramInt2 & 0x1) == 0) {
      this.m_ColorTemperature = paramInt1;
    }
    localCamera.set(Camera.PROP_COLOR_TEMPERATURE, Integer.valueOf(paramInt1));
  }
  
  private void setEV(float paramFloat, int paramInt)
  {
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.w(this.TAG, "setEV() - Cannot get camera");
      return;
    }
    if ((paramInt & 0x1) == 0) {
      this.m_EV = paramFloat;
    }
    localCamera.set(Camera.PROP_EXPOSURE_COMPENSATION, Float.valueOf(paramFloat));
  }
  
  private void setExposure(long paramLong, int paramInt)
  {
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.w(this.TAG, "setExposure() - Cannot get camera");
      return;
    }
    if ((paramInt & 0x1) == 0) {
      this.m_Exposure = paramLong;
    }
    localCamera.set(Camera.PROP_EXPOSURE_TIME_NANOS, Long.valueOf(paramLong));
  }
  
  private void setFocus(float paramFloat, int paramInt)
  {
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.w(this.TAG, "setFocus() - Cannot get camera");
      return;
    }
    if (paramFloat != -1.0F) {
      localCamera.set(Camera.PROP_FOCUS_MODE, FocusMode.MANUAL);
    }
    for (;;)
    {
      if ((paramInt & 0x1) == 0) {
        this.m_Focus = paramFloat;
      }
      localCamera.set(Camera.PROP_FOCUS, Float.valueOf(paramFloat));
      return;
      localCamera.set(Camera.PROP_FOCUS_MODE, FocusMode.CONTINUOUS_AF);
    }
  }
  
  private void setISO(int paramInt1, int paramInt2)
  {
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.w(this.TAG, "setISO() - Cannot get camera");
      return;
    }
    if ((paramInt2 & 0x1) == 0) {
      this.m_ISO = paramInt1;
    }
    localCamera.set(Camera.PROP_ISO, Integer.valueOf(paramInt1));
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 30041: 
      setAwb(paramMessage.arg1, 0);
      return;
    case 30042: 
      setExposure(((Long)paramMessage.obj).longValue(), 0);
      return;
    case 30043: 
      setFocus(((Float)paramMessage.obj).floatValue(), 0);
      return;
    case 30044: 
      setISO(paramMessage.arg1, 0);
      return;
    case 30045: 
      setEV(((Float)paramMessage.obj).floatValue(), 0);
      return;
    }
    setColorTemperature(paramMessage.arg1, 0);
  }
  
  protected void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    super.onCameraChanged(paramCamera1, paramCamera2);
    if (isEntered())
    {
      disableManualCapture(paramCamera1);
      enableManualCapture(paramCamera2);
    }
  }
  
  protected boolean onEnter(int paramInt)
  {
    super.onEnter(paramInt);
    enableManualCapture(getCamera());
    applyLastValues();
    return true;
  }
  
  protected void onExit(int paramInt)
  {
    applyAutoValues();
    disableManualCapture(getCamera());
    super.onExit(paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/ManualModeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */