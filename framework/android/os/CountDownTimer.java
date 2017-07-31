package android.os;

public abstract class CountDownTimer
{
  private static final int MSG = 1;
  private boolean mCancelled = false;
  private final long mCountdownInterval;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message arg1)
    {
      for (;;)
      {
        synchronized (CountDownTimer.this)
        {
          boolean bool = CountDownTimer.-get0(CountDownTimer.this);
          if (bool) {
            return;
          }
          l1 = CountDownTimer.-get2(CountDownTimer.this) - SystemClock.elapsedRealtime();
          if (l1 <= 0L)
          {
            CountDownTimer.this.onFinish();
            return;
          }
          if (l1 < CountDownTimer.-get1(CountDownTimer.this)) {
            sendMessageDelayed(obtainMessage(1), l1);
          }
        }
        long l2 = SystemClock.elapsedRealtime();
        CountDownTimer.this.onTick(l1);
        for (long l1 = CountDownTimer.-get1(CountDownTimer.this) + l2 - SystemClock.elapsedRealtime(); l1 < 0L; l1 += CountDownTimer.-get1(CountDownTimer.this)) {}
        sendMessageDelayed(obtainMessage(1), l1);
      }
    }
  };
  private final long mMillisInFuture;
  private long mStopTimeInFuture;
  
  public CountDownTimer(long paramLong1, long paramLong2)
  {
    this.mMillisInFuture = paramLong1;
    this.mCountdownInterval = paramLong2;
  }
  
  public final void cancel()
  {
    try
    {
      this.mCancelled = true;
      this.mHandler.removeMessages(1);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public abstract void onFinish();
  
  public abstract void onTick(long paramLong);
  
  public final CountDownTimer start()
  {
    try
    {
      this.mCancelled = false;
      if (this.mMillisInFuture <= 0L)
      {
        onFinish();
        return this;
      }
      this.mStopTimeInFuture = (SystemClock.elapsedRealtime() + this.mMillisInFuture);
      this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
      return this;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/CountDownTimer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */