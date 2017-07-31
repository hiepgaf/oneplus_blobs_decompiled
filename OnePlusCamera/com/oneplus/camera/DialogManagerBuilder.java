package com.oneplus.camera;

import com.oneplus.base.component.ComponentCreationPriority;

public final class DialogManagerBuilder
  extends UIComponentBuilder
{
  public DialogManagerBuilder()
  {
    super(ComponentCreationPriority.ON_DEMAND, DialogManagerImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new DialogManagerImpl(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/DialogManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */