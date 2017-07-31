package com.oneplus.gallery;

import com.oneplus.base.EventArgs;

public class ActionItemEventArgs
  extends EventArgs
{
  private final String m_Id;
  
  public ActionItemEventArgs(String paramString)
  {
    this.m_Id = paramString;
  }
  
  public final String getActionId()
  {
    return this.m_Id;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/ActionItemEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */