package com.oneplus.base;

import android.content.Intent;

public class IntentEventArgs
  extends EventArgs
{
  private final Intent m_Intent;
  
  public IntentEventArgs(Intent paramIntent)
  {
    this.m_Intent = paramIntent;
  }
  
  public final Intent getIntent()
  {
    return this.m_Intent;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/IntentEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */