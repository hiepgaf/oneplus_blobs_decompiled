package com.oneplus.camera.capturemode;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.IntentEventArgs;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.media.MediaType;

public class VideoCaptureMode
  extends SimpleCaptureMode
{
  private final EventHandler<IntentEventArgs> m_PrepareAdvancedSettingsHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<IntentEventArgs> paramAnonymousEventKey, IntentEventArgs paramAnonymousIntentEventArgs)
    {
      VideoCaptureMode.-wrap0(VideoCaptureMode.this, paramAnonymousIntentEventArgs.getIntent());
    }
  };
  
  public VideoCaptureMode(CameraActivity paramCameraActivity)
  {
    this(paramCameraActivity, "Video");
  }
  
  public VideoCaptureMode(CameraActivity paramCameraActivity, String paramString)
  {
    super(paramCameraActivity, "Video", MediaType.VIDEO, paramString);
  }
  
  private void onPrepareAdvancedSettings(Intent paramIntent)
  {
    Camera localCamera = getCamera();
    if ((localCamera != null) && (((Boolean)localCamera.get(Camera.PROP_IS_HIGH_VIDEO_FRAME_RATE_SUPPORTED)).booleanValue())) {
      paramIntent.putExtra("IsVideFrameRateVisible", true);
    }
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558476);
  }
  
  public Drawable getImage(CaptureMode.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-capturemode-CaptureMode$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    case 1: 
      return getCameraActivity().getDrawable(2130837542);
    }
    return getCameraActivity().getDrawable(2130837548);
  }
  
  protected boolean onEnter(CaptureMode paramCaptureMode, int paramInt)
  {
    if (!super.onEnter(paramCaptureMode, paramInt)) {
      return false;
    }
    getCameraActivity().addHandler(OPCameraActivity.EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE, this.m_PrepareAdvancedSettingsHandler);
    return true;
  }
  
  protected void onExit(CaptureMode paramCaptureMode, int paramInt)
  {
    super.onExit(paramCaptureMode, paramInt);
    if (this.m_PrepareAdvancedSettingsHandler != null) {
      getCameraActivity().removeHandler(OPCameraActivity.EVENT_PREPARE_ADVANCED_SETTING_ACTIVITY_EXTRA_BUNDLE, this.m_PrepareAdvancedSettingsHandler);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/VideoCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */