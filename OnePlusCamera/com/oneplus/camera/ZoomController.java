package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface ZoomController
  extends Component
{
  public static final PropertyKey<Float> PROP_DIGITAL_ZOOM = new PropertyKey("DigitalZoom", Float.class, ZoomController.class, 2, Float.valueOf(1.0F));
  public static final PropertyKey<Boolean> PROP_IS_DIGITAL_ZOOM_SUPPORTED = new PropertyKey("IsDigitalZoomSupported", Boolean.class, ZoomController.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_ZOOM_LOCKED = new PropertyKey("IsZoomLocked", Boolean.class, ZoomController.class, Boolean.valueOf(false));
  public static final PropertyKey<Float> PROP_MAX_DIGITAL_ZOOM = new PropertyKey("MaxDigitalZoom", Float.class, ZoomController.class, Float.valueOf(1.0F));
  
  public abstract Handle lockZoom(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ZoomController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */