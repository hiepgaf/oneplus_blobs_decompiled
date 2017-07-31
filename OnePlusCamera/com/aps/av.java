package com.aps;

public final class av
  extends Thread
{
  public final void run()
  {
    try
    {
      for (;;)
      {
        if (!y.c(this.a)) {
          return;
        }
        y.a(this.a, y.g(this.a), 1, System.currentTimeMillis());
        try
        {
          Thread.sleep(y.h(this.a));
        }
        catch (Exception localException1) {}
      }
      return;
    }
    catch (Exception localException2) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/av.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */