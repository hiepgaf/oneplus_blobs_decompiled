package com.oneplus.camera;

import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.camera.media.MediaType;

public abstract class CameraComponent
  extends CameraThreadComponent
{
  private final CameraActivity m_CameraActivity;
  private final boolean m_IsCameraThreadComponent;
  
  protected CameraComponent(String paramString, CameraActivity paramCameraActivity, boolean paramBoolean)
  {
    super(paramString, paramCameraActivity, paramCameraActivity.getCameraThread(), paramBoolean);
    this.m_CameraActivity = paramCameraActivity;
    this.m_IsCameraThreadComponent = false;
  }
  
  protected CameraComponent(String paramString, CameraActivity paramCameraActivity, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramString, paramCameraActivity, paramCameraActivity.getCameraThread(), paramBoolean1, paramBoolean2);
    this.m_CameraActivity = paramCameraActivity;
    this.m_IsCameraThreadComponent = false;
  }
  
  protected CameraComponent(String paramString, CameraThread paramCameraThread, boolean paramBoolean)
  {
    super(paramString, paramCameraThread, paramBoolean);
    this.m_CameraActivity = ((CameraActivity)paramCameraThread.getContext());
    this.m_IsCameraThreadComponent = true;
  }
  
  protected CameraComponent(String paramString, CameraThread paramCameraThread, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramString, paramCameraThread, paramBoolean1, paramBoolean2);
    this.m_CameraActivity = ((CameraActivity)paramCameraThread.getContext());
    this.m_IsCameraThreadComponent = true;
  }
  
  protected Camera getCamera()
  {
    if (this.m_IsCameraThreadComponent) {
      return super.getCamera();
    }
    return (Camera)this.m_CameraActivity.get(CameraActivity.PROP_CAMERA);
  }
  
  public final CameraActivity getCameraActivity()
  {
    return this.m_CameraActivity;
  }
  
  protected MediaType getMediaType()
  {
    if (this.m_IsCameraThreadComponent) {
      return super.getMediaType();
    }
    return (MediaType)this.m_CameraActivity.get(CameraActivity.PROP_MEDIA_TYPE);
  }
  
  protected ScreenSize getScreenSize()
  {
    if (this.m_IsCameraThreadComponent) {
      return super.getScreenSize();
    }
    return (ScreenSize)this.m_CameraActivity.get(CameraActivity.PROP_SCREEN_SIZE);
  }
  
  protected final Settings getSettings()
  {
    return (Settings)this.m_CameraActivity.get(CameraActivity.PROP_SETTINGS);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */