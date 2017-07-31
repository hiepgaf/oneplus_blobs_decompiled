package com.oneplus.camera.ui;

import android.graphics.PointF;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;

public abstract interface TouchAutoExposureUI
  extends Component
{
  public static final EventKey<EventArgs> EVENT_TOUCH_AE = new EventKey("TouchAE", EventArgs.class, TouchAutoFocusUI.class);
  
  public abstract Handle disableTouchLockExposure();
  
  public abstract Handle touchAutoExposure(float paramFloat1, float paramFloat2);
  
  public abstract Handle touchAutoExposure(PointF paramPointF);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/TouchAutoExposureUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */