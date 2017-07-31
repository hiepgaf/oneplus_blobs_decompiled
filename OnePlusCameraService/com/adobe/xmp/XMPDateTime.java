package com.adobe.xmp;

import java.util.Calendar;
import java.util.TimeZone;

public abstract interface XMPDateTime
  extends Comparable
{
  public abstract Calendar getCalendar();
  
  public abstract int getDay();
  
  public abstract int getHour();
  
  public abstract String getISO8601String();
  
  public abstract int getMinute();
  
  public abstract int getMonth();
  
  public abstract int getNanoSecond();
  
  public abstract int getSecond();
  
  public abstract TimeZone getTimeZone();
  
  public abstract int getYear();
  
  public abstract void setDay(int paramInt);
  
  public abstract void setHour(int paramInt);
  
  public abstract void setMinute(int paramInt);
  
  public abstract void setMonth(int paramInt);
  
  public abstract void setNanoSecond(int paramInt);
  
  public abstract void setSecond(int paramInt);
  
  public abstract void setTimeZone(TimeZone paramTimeZone);
  
  public abstract void setYear(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/adobe/xmp/XMPDateTime.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */