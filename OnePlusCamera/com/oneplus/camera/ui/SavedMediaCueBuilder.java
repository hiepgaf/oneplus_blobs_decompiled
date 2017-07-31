package com.oneplus.camera.ui;

import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class SavedMediaCueBuilder
  extends UIComponentBuilder
{
  public SavedMediaCueBuilder()
  {
    super(SavedMediaCue.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    return new SavedMediaCue(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SavedMediaCueBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */