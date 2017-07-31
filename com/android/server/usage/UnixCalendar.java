package com.android.server.usage;

public class UnixCalendar
{
  public static final long DAY_IN_MILLIS = 86400000L;
  public static final long MONTH_IN_MILLIS = 2592000000L;
  public static final long WEEK_IN_MILLIS = 604800000L;
  public static final long YEAR_IN_MILLIS = 31536000000L;
  private long mTime;
  
  public UnixCalendar(long paramLong)
  {
    this.mTime = paramLong;
  }
  
  public void addDays(int paramInt)
  {
    this.mTime += paramInt * 86400000L;
  }
  
  public void addMonths(int paramInt)
  {
    this.mTime += paramInt * 2592000000L;
  }
  
  public void addWeeks(int paramInt)
  {
    this.mTime += paramInt * 604800000L;
  }
  
  public void addYears(int paramInt)
  {
    this.mTime += paramInt * 31536000000L;
  }
  
  public long getTimeInMillis()
  {
    return this.mTime;
  }
  
  public void setTimeInMillis(long paramLong)
  {
    this.mTime = paramLong;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usage/UnixCalendar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */