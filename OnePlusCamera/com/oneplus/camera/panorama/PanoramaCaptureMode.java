package com.oneplus.camera.panorama;

import android.graphics.drawable.Drawable;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.capturemode.CaptureMode.ImageUsage;
import com.oneplus.camera.capturemode.ComponentBasedCaptureMode;
import com.oneplus.camera.media.MediaType;

public final class PanoramaCaptureMode
  extends ComponentBasedCaptureMode<PanoramaUI>
{
  PanoramaCaptureMode(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity, "Panorama", "panorama", PanoramaUI.class, MediaType.PHOTO);
    setReadOnly(PROP_TARGET_CAMERA_LENS_FACING, Camera.LensFacing.BACK);
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558472);
  }
  
  public Drawable getImage(CaptureMode.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-capturemode-CaptureMode$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    }
    return getCameraActivity().getDrawable(2130837538);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/panorama/PanoramaCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */