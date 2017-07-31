package com.oneplus.camera.ui;

import com.oneplus.base.Settings;
import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.UIComponentBuilder;

public final class CameraWizardBuilder
  extends UIComponentBuilder
{
  public CameraWizardBuilder()
  {
    super(ComponentCreationPriority.HIGH, CameraWizardImpl.class);
  }
  
  protected CameraComponent create(CameraActivity paramCameraActivity)
  {
    if (paramCameraActivity.isServiceMode()) {
      return null;
    }
    Settings localSettings = (Settings)paramCameraActivity.get(CameraActivity.PROP_SETTINGS);
    int k = 0;
    String[] arrayOfString = CameraWizardImpl.SETTINGS_KEY_WIZARD_LIST;
    int m = arrayOfString.length;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < m)
      {
        if (!localSettings.getBoolean(arrayOfString[i], false)) {
          j = 1;
        }
      }
      else
      {
        if (j == 0) {
          break;
        }
        return new CameraWizardImpl(paramCameraActivity);
      }
      i += 1;
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CameraWizardBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */