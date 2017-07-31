package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public class ReviewScreenBuilder
  extends UIComponentBuilder
{
  public ReviewScreenBuilder()
  {
    super(ReviewScreenImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity.isServiceMode()) {
      return new ReviewScreenImpl(paramCameraActivity);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ReviewScreenBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */