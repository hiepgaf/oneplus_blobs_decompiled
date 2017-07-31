package com.oneplus.camera.ui;

import android.view.InputDevice;
import android.view.KeyEvent;
import com.oneplus.base.EventArgs;
import com.oneplus.base.Log;
import java.lang.reflect.Method;

public class KeyEventArgs
  extends EventArgs
{
  private static final String TAG = KeyEventArgs.class.getSimpleName();
  private boolean m_IsExternal;
  private Method m_IsExternalMethod;
  private int m_KeyCode;
  private KeyEvent m_KeyEvent;
  private int m_RepeatCount;
  
  public KeyEventArgs(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent == null) {
      throw new IllegalArgumentException("No key event");
    }
    this.m_KeyEvent = paramKeyEvent;
    this.m_KeyCode = paramKeyEvent.getKeyCode();
    this.m_RepeatCount = paramKeyEvent.getRepeatCount();
  }
  
  public final int getKeyCode()
  {
    return this.m_KeyCode;
  }
  
  public final KeyEvent getKeyEvent()
  {
    return this.m_KeyEvent;
  }
  
  public final int getRepeatCount()
  {
    return this.m_RepeatCount;
  }
  
  public boolean isExternal()
  {
    if (this.m_IsExternalMethod == null) {
      try
      {
        InputDevice localInputDevice = this.m_KeyEvent.getDevice();
        this.m_IsExternalMethod = InputDevice.class.getMethod("isExternal", new Class[0]);
        this.m_IsExternal = ((Boolean)this.m_IsExternalMethod.invoke(localInputDevice, new Object[0])).booleanValue();
        boolean bool = this.m_IsExternal;
        return bool;
      }
      catch (Throwable localThrowable)
      {
        Log.e(TAG, "isExternal - check KeyEvent failed", localThrowable);
        return false;
      }
    }
    return this.m_IsExternal;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/KeyEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */