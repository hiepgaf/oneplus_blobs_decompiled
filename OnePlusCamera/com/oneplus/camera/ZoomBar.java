package com.oneplus.camera;

import com.oneplus.base.EventArgs;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface ZoomBar
  extends Component
{
  public static final EventKey<EventArgs> EVENT_ZOOM_VALUE_CLICK = new EventKey("ZoomValueClick", EventArgs.class, ZoomBar.class);
  public static final EventKey<EventArgs> EVENT_ZOOM_VALUE_DRAGED = new EventKey("IsZoomValueDraged", EventArgs.class, ZoomBar.class);
  public static final EventKey<EventArgs> EVENT_ZOOM_VALUE_LONG_CLICK = new EventKey("ZoomValueLongClick", EventArgs.class, ZoomBar.class);
  public static final PropertyKey<Boolean> PROP_IS_ZOOM_VALUE_VISIBLE = new PropertyKey("IsZoomValueVisible", Boolean.class, ZoomBar.class, Boolean.valueOf(true));
  public static final PropertyKey<Boolean> PROP_IS_ZOOM_WHEEL_VISIBLE = new PropertyKey("IsZoomWheelVisible", Boolean.class, ZoomBar.class, Boolean.valueOf(false));
  
  public abstract Handle setZoomBarVisibility(boolean paramBoolean, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ZoomBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */