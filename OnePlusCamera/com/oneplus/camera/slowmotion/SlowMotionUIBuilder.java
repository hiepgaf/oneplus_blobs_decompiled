package com.oneplus.camera.slowmotion;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class SlowMotionUIBuilder
  extends UIComponentBuilder
{
  public SlowMotionUIBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, SlowMotionUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new SlowMotionUI(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/slowmotion/SlowMotionUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */