package android.net.util;

import android.os.SystemClock;

public class Stopwatch
{
  private long mStartTimeMs;
  private long mStopTimeMs;
  
  public boolean isRunning()
  {
    return (isStarted()) && (!isStopped());
  }
  
  public boolean isStarted()
  {
    return this.mStartTimeMs > 0L;
  }
  
  public boolean isStopped()
  {
    return this.mStopTimeMs > 0L;
  }
  
  public long lap()
  {
    if (isRunning()) {
      return SystemClock.elapsedRealtime() - this.mStartTimeMs;
    }
    return stop();
  }
  
  public void reset()
  {
    this.mStartTimeMs = 0L;
    this.mStopTimeMs = 0L;
  }
  
  public Stopwatch start()
  {
    if (!isStarted()) {
      this.mStartTimeMs = SystemClock.elapsedRealtime();
    }
    return this;
  }
  
  public long stop()
  {
    if (isRunning()) {
      this.mStopTimeMs = SystemClock.elapsedRealtime();
    }
    return this.mStopTimeMs - this.mStartTimeMs;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/util/Stopwatch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */