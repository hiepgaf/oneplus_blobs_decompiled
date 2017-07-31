package com.oneplus.camera.scene;

import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.ModeUI;

public abstract class ComponentBasedScene<TComponent extends ModeUI<?>>
  extends BasicScene
{
  private TComponent m_Component;
  private final Class<? extends TComponent> m_ComponentClass;
  
  protected ComponentBasedScene(CameraActivity paramCameraActivity, String paramString, Class<? extends TComponent> paramClass)
  {
    super(paramCameraActivity, paramString);
    if (paramClass == null) {
      throw new IllegalArgumentException("No component type.");
    }
    this.m_ComponentClass = paramClass;
  }
  
  protected boolean onCallComponentEnter(Scene paramScene, int paramInt1, int paramInt2)
  {
    if (this.m_Component == null)
    {
      Log.e(this.TAG, "onCallComponentEnter() - No component to call");
      return false;
    }
    return this.m_Component.enter(paramInt2);
  }
  
  protected void onCallComponentExit(Scene paramScene, int paramInt1, int paramInt2)
  {
    if (this.m_Component != null) {
      this.m_Component.exit(paramInt2);
    }
  }
  
  protected boolean onEnter(Scene paramScene, int paramInt)
  {
    if (this.m_Component == null) {
      this.m_Component = ((ModeUI)getCameraActivity().findComponent(this.m_ComponentClass));
    }
    return onCallComponentEnter(paramScene, paramInt, 0);
  }
  
  protected void onExit(Scene paramScene, int paramInt)
  {
    onCallComponentExit(paramScene, paramInt, 0);
  }
  
  protected void onRelease()
  {
    this.m_Component = null;
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/ComponentBasedScene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */