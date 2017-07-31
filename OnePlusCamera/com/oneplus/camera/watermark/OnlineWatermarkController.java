package com.oneplus.camera.watermark;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface OnlineWatermarkController
  extends Component
{
  public static final int FLAG_IS_BOKEH = 1;
  public static final PropertyKey<Boolean> PROP_IS_SUPPORTED = new PropertyKey("IsSupported", Boolean.class, OnlineWatermarkControllerImpl.class, 1, null);
  
  public abstract boolean enter(int paramInt);
  
  public abstract void exit(int paramInt);
  
  public abstract void setWatermark(Watermark paramWatermark, String paramString);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/watermark/OnlineWatermarkController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */