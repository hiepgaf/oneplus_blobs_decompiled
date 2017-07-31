package com.oneplus.camera.ui;

import android.graphics.drawable.Drawable;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;

public abstract interface CaptureButtons
  extends Component
{
  public static final EventKey<CaptureButtonEventArgs> EVENT_BUTTON_PRESSED = new EventKey("ButtonPressed", CaptureButtonEventArgs.class, CaptureButtons.class);
  public static final EventKey<CaptureButtonEventArgs> EVENT_BUTTON_RELEASED = new EventKey("ButtonReleased", CaptureButtonEventArgs.class, CaptureButtons.class);
  public static final int FLAG_NO_ANIMATION = 1;
  
  public abstract void performButtonClick(Button paramButton, int paramInt);
  
  public abstract Handle setButtonBackground(Button paramButton, Drawable paramDrawable, int paramInt);
  
  public abstract Handle setButtonIcon(Button paramButton, Drawable paramDrawable, int paramInt);
  
  public abstract Handle setButtonStyle(Button paramButton, ButtonStyle paramButtonStyle, int paramInt);
  
  public abstract Handle setButtonVisibility(Button paramButton, boolean paramBoolean, int paramInt);
  
  public static enum Button
  {
    PRIMARY;
  }
  
  public static enum ButtonStyle
  {
    NORMAL,  SMALL;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CaptureButtons.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */