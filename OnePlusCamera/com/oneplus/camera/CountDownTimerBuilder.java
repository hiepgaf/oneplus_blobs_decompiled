package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public class CountDownTimerBuilder
  extends UIComponentBuilder
{
  public CountDownTimerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, CountDownTimerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new CountDownTimerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CountDownTimerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */