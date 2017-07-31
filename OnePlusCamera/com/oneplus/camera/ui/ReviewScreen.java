package com.oneplus.camera.ui;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface ReviewScreen
  extends Component
{
  public static final int FLAG_WAIT_FOR_CAMERA_PREVIEW = 1;
  public static final PropertyKey<Boolean> PROP_IS_VISIBLE = new PropertyKey("IsVisible", Boolean.class, ReviewScreen.class, Boolean.valueOf(false));
  public static final int RESULT_CANCEL = 0;
  public static final int RESULT_OK = 1;
  public static final int RESULT_RETAKE = 2;
  
  public abstract Handle showReviewScreen();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ReviewScreen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */