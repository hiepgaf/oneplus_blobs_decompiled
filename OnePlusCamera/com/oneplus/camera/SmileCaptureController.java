package com.oneplus.camera;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface SmileCaptureController
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_SMILE_CAPTURE_ENABLED = new PropertyKey("IsSmileCaptureEnabled", Boolean.class, SmileCaptureController.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_SMILE_CAPTURING = new PropertyKey("IsSmileCapturing", Boolean.class, SmileCaptureController.class, Boolean.valueOf(false));
  public static final String SETTINGS_KEY_SMILE_CAPTURE_BACK = "SmileCapture.Back";
  public static final String SETTINGS_KEY_SMILE_CAPTURE_FRONT = "SmileCapture.Front";
  public static final String SETTINGS_KEY_SMILE_CAPTURE_TIMER_BACK = "SmileCapture.Timer.Back";
  public static final String SETTINGS_KEY_SMILE_CAPTURE_TIMER_FRONT = "SmileCapture.Timer.Front";
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/SmileCaptureController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */