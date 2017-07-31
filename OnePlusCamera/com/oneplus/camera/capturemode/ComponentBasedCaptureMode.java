package com.oneplus.camera.capturemode;

import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.ModeUI;
import com.oneplus.camera.media.MediaType;

public abstract class ComponentBasedCaptureMode<TComponent extends ModeUI<?>>
  extends BasicCaptureMode
{
  private TComponent m_Component;
  private final Class<? extends TComponent> m_ComponentClass;
  
  protected ComponentBasedCaptureMode(CameraActivity paramCameraActivity, String paramString1, String paramString2, Class<? extends TComponent> paramClass, MediaType paramMediaType)
  {
    super(paramCameraActivity, paramString1, paramString2, paramMediaType);
    if (paramClass == null) {
      throw new IllegalArgumentException("No component type.");
    }
    this.m_ComponentClass = paramClass;
  }
  
  protected boolean onCallComponentEnter(CaptureMode paramCaptureMode, int paramInt1, int paramInt2)
  {
    if (this.m_Component == null)
    {
      Log.e(this.TAG, "onCallComponentEnter() - No component to call");
      return false;
    }
    return this.m_Component.enter(paramInt2);
  }
  
  protected void onCallComponentExit(CaptureMode paramCaptureMode, int paramInt1, int paramInt2)
  {
    if (this.m_Component != null) {
      this.m_Component.exit(paramInt2);
    }
  }
  
  protected boolean onEnter(CaptureMode paramCaptureMode, int paramInt)
  {
    if (this.m_Component == null) {
      this.m_Component = ((ModeUI)getCameraActivity().findComponent(this.m_ComponentClass));
    }
    return onCallComponentEnter(paramCaptureMode, paramInt, 0);
  }
  
  protected void onExit(CaptureMode paramCaptureMode, int paramInt)
  {
    onCallComponentExit(paramCaptureMode, paramInt, 0);
  }
  
  protected void onRelease()
  {
    this.m_Component = null;
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/capturemode/ComponentBasedCaptureMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */