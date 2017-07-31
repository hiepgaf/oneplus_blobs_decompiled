package com.oneplus.camera.ui;

import com.oneplus.base.EventArgs;

public class CaptureButtonEventArgs
  extends EventArgs
{
  private final CaptureButtons.Button m_Button;
  
  public CaptureButtonEventArgs(CaptureButtons.Button paramButton)
  {
    this.m_Button = paramButton;
  }
  
  public final CaptureButtons.Button getButton()
  {
    return this.m_Button;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CaptureButtonEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */