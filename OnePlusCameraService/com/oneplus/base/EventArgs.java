package com.oneplus.base;

public class EventArgs
  implements Cloneable
{
  public static final EventArgs EMPTY = new EventArgs();
  private volatile boolean m_IsHandled;
  
  protected final void clearHandledState()
  {
    this.m_IsHandled = false;
  }
  
  public EventArgs clone()
  {
    try
    {
      EventArgs localEventArgs = (EventArgs)super.clone();
      return localEventArgs;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException);
    }
  }
  
  public final boolean isHandled()
  {
    return this.m_IsHandled;
  }
  
  public final void setHandled()
  {
    this.m_IsHandled = true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/EventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */