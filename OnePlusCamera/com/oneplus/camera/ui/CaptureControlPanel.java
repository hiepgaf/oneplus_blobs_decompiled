package com.oneplus.camera.ui;

import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;

public abstract interface CaptureControlPanel
  extends Component
{
  public abstract Handle setPanelStyle(Style paramStyle, int paramInt);
  
  public static enum Style
  {
    DEFAULT,  TRANSPARENT;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CaptureControlPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */