package com.android.server.input;

public final class InputApplicationHandle
{
  public final Object appWindowToken;
  public long dispatchingTimeoutNanos;
  public String name;
  private long ptr;
  
  public InputApplicationHandle(Object paramObject)
  {
    this.appWindowToken = paramObject;
  }
  
  private native void nativeDispose();
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nativeDispose();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/input/InputApplicationHandle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */