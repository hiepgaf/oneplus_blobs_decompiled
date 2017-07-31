package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface FocusController
  extends Component
{
  public static final int FLAG_CONTINOUS_AF = 2;
  public static final int FLAG_SINGLE_AF = 1;
  public static final PropertyKey<List<Camera.MeteringRect>> PROP_AF_REGIONS = new PropertyKey("AFRegions", List.class, FocusController.class, Collections.EMPTY_LIST);
  public static final PropertyKey<Boolean> PROP_CAN_CHANGE_FOCUS = new PropertyKey("CanChangeFocus", Boolean.class, FocusController.class, Boolean.valueOf(false));
  public static final PropertyKey<FocusMode> PROP_FOCUS_MODE = new PropertyKey("FocusMode", FocusMode.class, FocusController.class, FocusMode.DISABLED);
  public static final PropertyKey<FocusState> PROP_FOCUS_STATE = new PropertyKey("FocusState", FocusState.class, FocusController.class, FocusState.INACTIVE);
  public static final PropertyKey<Boolean> PROP_IS_FOCUS_LOCKED = new PropertyKey("IsFocusLocked", Boolean.class, FocusController.class, Boolean.valueOf(false));
  
  public abstract Handle lockFocus(int paramInt);
  
  public abstract Handle startAutoFocus(List<Camera.MeteringRect> paramList, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FocusController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */