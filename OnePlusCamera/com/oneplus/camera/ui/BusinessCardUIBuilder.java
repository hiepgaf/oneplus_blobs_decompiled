package com.oneplus.camera.ui;

import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public class BusinessCardUIBuilder
  extends UIComponentBuilder
{
  public BusinessCardUIBuilder()
  {
    super(ComponentCreationPriority.LAUNCH, BusinessCardUI.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity.isBusinessCardMode()) {
      return new BusinessCardUI(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/BusinessCardUIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */