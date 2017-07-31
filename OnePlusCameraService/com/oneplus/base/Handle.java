package com.oneplus.base;

public abstract class Handle
{
  private volatile boolean m_IsClosed;
  public final String name;
  
  protected Handle(String paramString)
  {
    this.name = paramString;
  }
  
  public static <T extends Handle> T close(T paramT)
  {
    return close(paramT, 0);
  }
  
  public static <T extends Handle> T close(T paramT, int paramInt)
  {
    if (paramT != null) {}
    try
    {
      boolean bool = paramT.m_IsClosed;
      if (bool) {
        return null;
      }
      paramT.m_IsClosed = true;
      paramT.onClose(paramInt);
      return null;
    }
    finally {}
  }
  
  public static boolean isValid(Handle paramHandle)
  {
    return (paramHandle != null) && (!paramHandle.m_IsClosed);
  }
  
  protected final void closeDirectly()
  {
    try
    {
      this.m_IsClosed = true;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected abstract void onClose(int paramInt);
  
  public String toString()
  {
    return this.name + " [" + hashCode() + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/Handle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */