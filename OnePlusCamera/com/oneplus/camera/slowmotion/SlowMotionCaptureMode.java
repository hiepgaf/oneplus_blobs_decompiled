package com.oneplus.camera.slowmotion;

import android.graphics.drawable.Drawable;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureMode.ImageUsage;
import com.oneplus.camera.capturemode.ComponentBasedCaptureMode;
import com.oneplus.camera.media.MediaType;

final class SlowMotionCaptureMode
  extends ComponentBasedCaptureMode<SlowMotionUI>
{
  private final PropertyChangedCallback<Camera> m_CameraChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
    {
      SlowMotionCaptureMode.-wrap0(SlowMotionCaptureMode.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  
  SlowMotionCaptureMode(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity, "Slow-motion", "slowmotion", SlowMotionUI.class, MediaType.VIDEO);
    setReadOnly(PROP_TARGET_CAMERA_LENS_FACING, Camera.LensFacing.BACK);
  }
  
  private void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    setSpecialVideoMode(getCamera(), isEntered());
  }
  
  private void setSpecialVideoMode(final Camera paramCamera, final boolean paramBoolean)
  {
    if (paramCamera == null)
    {
      Log.w(this.TAG, "setSpecialVideoMode() - camera is null");
      return;
    }
    Log.d(this.TAG, "setSpecialVideoMode() - isSpecial :" + paramBoolean);
    HandlerUtils.post(paramCamera, new Runnable()
    {
      public void run()
      {
        paramCamera.set(Camera.PROP_IS_SPECIAL_VIDEO_MODE, Boolean.valueOf(paramBoolean));
      }
    });
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558474);
  }
  
  public Drawable getImage(CaptureMode.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-capturemode-CaptureMode$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    }
    return getCameraActivity().getDrawable(2130837540);
  }
  
  protected boolean onEnter(CaptureMode paramCaptureMode, int paramInt)
  {
    if (!super.onEnter(paramCaptureMode, paramInt)) {
      return false;
    }
    getCameraActivity().addCallback(CameraActivity.PROP_CAMERA, this.m_CameraChangedCallback);
    setSpecialVideoMode(getCamera(), true);
    return true;
  }
  
  protected void onExit(CaptureMode paramCaptureMode, int paramInt)
  {
    super.onExit(paramCaptureMode, paramInt);
    getCameraActivity().removeCallback(CameraActivity.PROP_CAMERA, this.m_CameraChangedCallback);
    setSpecialVideoMode(getCamera(), false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/slowmotion/SlowMotionCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */