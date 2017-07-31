package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class LongMediaProcessingUIBuilder
  extends UIComponentBuilder
{
  public LongMediaProcessingUIBuilder()
  {
    super(LongMediaProcessingUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new LongMediaProcessingUI(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/LongMediaProcessingUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */