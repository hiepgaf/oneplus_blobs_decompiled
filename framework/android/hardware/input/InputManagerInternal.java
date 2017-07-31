package android.hardware.input;

import android.hardware.display.DisplayViewport;
import android.view.InputEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;

public abstract class InputManagerInternal
{
  public abstract boolean injectInputEvent(InputEvent paramInputEvent, int paramInt1, int paramInt2);
  
  public abstract void onInputMethodSubtypeChanged(int paramInt, InputMethodInfo paramInputMethodInfo, InputMethodSubtype paramInputMethodSubtype);
  
  public abstract void setDisplayViewports(DisplayViewport paramDisplayViewport1, DisplayViewport paramDisplayViewport2);
  
  public abstract void setInteractive(boolean paramBoolean);
  
  public abstract void setPulseGestureEnabled(boolean paramBoolean);
  
  public abstract void toggleCapsLock(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/input/InputManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */