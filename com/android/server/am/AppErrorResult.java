package com.android.server.am;

final class AppErrorResult
{
  boolean mHasResult = false;
  int mResult;
  
  public int get()
  {
    try
    {
      for (;;)
      {
        boolean bool = this.mHasResult;
        if (bool) {
          break;
        }
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      return this.mResult;
    }
    finally {}
  }
  
  public void set(int paramInt)
  {
    try
    {
      this.mHasResult = true;
      this.mResult = paramInt;
      notifyAll();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/AppErrorResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */