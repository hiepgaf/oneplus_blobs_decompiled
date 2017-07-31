package com.oneplus.camera.ui;

import android.os.Build.VERSION;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public class DynamicShortcutsManagerBuilder
  extends UIComponentBuilder
{
  public DynamicShortcutsManagerBuilder()
  {
    super(DynamicShortcutsManager.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if ((Build.VERSION.SDK_INT <= 24) || (paramCameraActivity.isServiceMode())) {
      return null;
    }
    return new DynamicShortcutsManager(paramCameraActivity);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/DynamicShortcutsManagerBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */