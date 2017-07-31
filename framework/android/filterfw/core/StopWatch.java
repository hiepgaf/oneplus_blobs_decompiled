package android.filterfw.core;

import android.os.SystemClock;
import android.util.Log;

class StopWatch
{
  private int STOP_WATCH_LOGGING_PERIOD = 200;
  private String TAG = "MFF";
  private String mName;
  private int mNumCalls;
  private long mStartTime;
  private long mTotalTime;
  
  public StopWatch(String paramString)
  {
    this.mName = paramString;
    this.mStartTime = -1L;
    this.mTotalTime = 0L;
    this.mNumCalls = 0;
  }
  
  public void start()
  {
    if (this.mStartTime != -1L) {
      throw new RuntimeException("Calling start with StopWatch already running");
    }
    this.mStartTime = SystemClock.elapsedRealtime();
  }
  
  public void stop()
  {
    if (this.mStartTime == -1L) {
      throw new RuntimeException("Calling stop with StopWatch already stopped");
    }
    long l = SystemClock.elapsedRealtime();
    this.mTotalTime += l - this.mStartTime;
    this.mNumCalls += 1;
    this.mStartTime = -1L;
    if (this.mNumCalls % this.STOP_WATCH_LOGGING_PERIOD == 0)
    {
      Log.i(this.TAG, "AVG ms/call " + this.mName + ": " + String.format("%.1f", new Object[] { Float.valueOf((float)this.mTotalTime * 1.0F / this.mNumCalls) }));
      this.mTotalTime = 0L;
      this.mNumCalls = 0;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/StopWatch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */