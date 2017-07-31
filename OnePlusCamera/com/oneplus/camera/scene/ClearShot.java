package com.oneplus.camera.scene;

import android.graphics.drawable.Drawable;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.media.MediaType;

public final class ClearShot
  extends PhotoScene
{
  public static final String ID = "ClearShot";
  
  ClearShot(CameraActivity paramCameraActivity)
  {
    super(paramCameraActivity, "ClearShot", 11, 19);
  }
  
  protected void checkSceneModeValid(Camera paramCamera)
  {
    if ((paramCamera != null) && (paramCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT))
    {
      disable();
      return;
    }
    super.checkSceneModeValid(paramCamera);
  }
  
  public String getDisplayName()
  {
    return getCameraActivity().getString(2131558509);
  }
  
  public Drawable getImage(Scene.ImageUsage paramImageUsage)
  {
    switch (-getcom-oneplus-camera-scene-Scene$ImageUsageSwitchesValues()[paramImageUsage.ordinal()])
    {
    default: 
      return null;
    case 1: 
      return getCameraActivity().getDrawable(2130837784);
    }
    return getCameraActivity().getDrawable(2130838232);
  }
  
  protected void onCameraChanged(Camera paramCamera)
  {
    if ((paramCamera != null) && (paramCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT))
    {
      disable();
      return;
    }
    super.onCameraChanged(paramCamera);
  }
  
  protected void onMediaTypeChanged(MediaType paramMediaType)
  {
    Camera localCamera = getCamera();
    if ((localCamera != null) && (localCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT))
    {
      disable();
      return;
    }
    super.onMediaTypeChanged(paramMediaType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/ClearShot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */