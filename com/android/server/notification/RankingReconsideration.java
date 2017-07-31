package com.android.server.notification;

import java.util.concurrent.TimeUnit;

public abstract class RankingReconsideration
  implements Runnable
{
  private static final int CANCELLED = 3;
  private static final int DONE = 2;
  private static final long IMMEDIATE = 0L;
  private static final int RUNNING = 1;
  private static final int START = 0;
  private long mDelay;
  protected String mKey;
  private int mState;
  
  public RankingReconsideration(String paramString)
  {
    this(paramString, 0L);
  }
  
  public RankingReconsideration(String paramString, long paramLong)
  {
    this.mDelay = paramLong;
    this.mKey = paramString;
    this.mState = 0;
  }
  
  public abstract void applyChangesLocked(NotificationRecord paramNotificationRecord);
  
  public boolean cancel(boolean paramBoolean)
  {
    if (this.mState == 0)
    {
      this.mState = 3;
      return true;
    }
    return false;
  }
  
  public long getDelay(TimeUnit paramTimeUnit)
  {
    return paramTimeUnit.convert(this.mDelay, TimeUnit.MILLISECONDS);
  }
  
  public String getKey()
  {
    return this.mKey;
  }
  
  public boolean isCancelled()
  {
    return this.mState == 3;
  }
  
  public boolean isDone()
  {
    return this.mState == 2;
  }
  
  public void run()
  {
    if (this.mState == 0)
    {
      this.mState = 1;
      work();
      this.mState = 2;
    }
    try
    {
      notifyAll();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public abstract void work();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/RankingReconsideration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */