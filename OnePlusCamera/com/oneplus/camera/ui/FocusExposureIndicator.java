package com.oneplus.camera.ui;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface FocusExposureIndicator
  extends Component
{
  public static final PropertyKey<Boolean> PROP_CAN_FOCUS_EXPOSURE_SEPARATED = new PropertyKey("CanFocusExposureSeparated", Boolean.class, FocusExposureIndicator.class, 2, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_FOCUS_EXPOSURE_SEPARATED = new PropertyKey("IsFocusExposureSeparated", Boolean.class, FocusExposureIndicator.class, Boolean.valueOf(false));
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/FocusExposureIndicator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */