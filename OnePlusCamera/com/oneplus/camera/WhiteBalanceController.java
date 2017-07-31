package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface WhiteBalanceController
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_AWB_LOCKED = new PropertyKey("IsAwbLocked", Boolean.class, WhiteBalanceController.class, Boolean.valueOf(false));
  
  public abstract Handle lockAutoWhiteBalance(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/WhiteBalanceController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */