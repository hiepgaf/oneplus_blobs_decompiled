package com.android.server.twilight;

import android.text.format.DateFormat;
import java.util.Calendar;

public final class TwilightState
{
  private final long mSunriseTimeMillis;
  private final long mSunsetTimeMillis;
  
  TwilightState(long paramLong1, long paramLong2)
  {
    this.mSunriseTimeMillis = paramLong1;
    this.mSunsetTimeMillis = paramLong2;
  }
  
  public boolean equals(TwilightState paramTwilightState)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramTwilightState != null)
    {
      bool1 = bool2;
      if (this.mSunriseTimeMillis == paramTwilightState.mSunriseTimeMillis)
      {
        bool1 = bool2;
        if (this.mSunsetTimeMillis == paramTwilightState.mSunsetTimeMillis) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof TwilightState)) {
      return equals((TwilightState)paramObject);
    }
    return false;
  }
  
  public int hashCode()
  {
    return Long.hashCode(this.mSunriseTimeMillis) ^ Long.hashCode(this.mSunsetTimeMillis);
  }
  
  public boolean isNight()
  {
    long l = System.currentTimeMillis();
    if (this.mSunsetTimeMillis < this.mSunriseTimeMillis) {
      return (l > this.mSunsetTimeMillis) && (l < this.mSunriseTimeMillis);
    }
    return (l < this.mSunriseTimeMillis) || (l > this.mSunsetTimeMillis);
  }
  
  public Calendar sunrise()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(this.mSunriseTimeMillis);
    return localCalendar;
  }
  
  public long sunriseTimeMillis()
  {
    return this.mSunriseTimeMillis;
  }
  
  public Calendar sunset()
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(this.mSunsetTimeMillis);
    return localCalendar;
  }
  
  public long sunsetTimeMillis()
  {
    return this.mSunsetTimeMillis;
  }
  
  public String toString()
  {
    return "TwilightState { sunrise=" + DateFormat.format("MM-dd HH:mm", this.mSunriseTimeMillis) + " sunset=" + DateFormat.format("MM-dd HH:mm", this.mSunsetTimeMillis) + " }";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/twilight/TwilightState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */