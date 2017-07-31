package android.os;

public class HandlerThread
  extends Thread
{
  Looper mLooper;
  int mPriority;
  int mTid = -1;
  
  public HandlerThread(String paramString)
  {
    super(paramString);
    this.mPriority = 0;
  }
  
  public HandlerThread(String paramString, int paramInt)
  {
    super(paramString);
    this.mPriority = paramInt;
  }
  
  public Looper getLooper()
  {
    if (!isAlive()) {
      return null;
    }
    try
    {
      while (isAlive())
      {
        Looper localLooper = this.mLooper;
        if (localLooper != null) {
          break;
        }
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      return this.mLooper;
    }
    finally {}
  }
  
  public int getThreadId()
  {
    return this.mTid;
  }
  
  protected void onLooperPrepared() {}
  
  public boolean quit()
  {
    Looper localLooper = getLooper();
    if (localLooper != null)
    {
      localLooper.quit();
      return true;
    }
    return false;
  }
  
  public boolean quitSafely()
  {
    Looper localLooper = getLooper();
    if (localLooper != null)
    {
      localLooper.quitSafely();
      return true;
    }
    return false;
  }
  
  public void run()
  {
    this.mTid = Process.myTid();
    Looper.prepare();
    try
    {
      this.mLooper = Looper.myLooper();
      notifyAll();
      Process.setThreadPriority(this.mPriority);
      onLooperPrepared();
      Looper.loop();
      this.mTid = -1;
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/HandlerThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */