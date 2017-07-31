package com.android.server.notification;

import android.service.notification.ZenModeConfig.ScheduleInfo;
import android.util.ArraySet;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

public class ScheduleCalendar
{
  private final Calendar mCalendar = Calendar.getInstance();
  private final ArraySet<Integer> mDays = new ArraySet();
  private ZenModeConfig.ScheduleInfo mSchedule;
  
  private long addDays(long paramLong, int paramInt)
  {
    this.mCalendar.setTimeInMillis(paramLong);
    this.mCalendar.add(5, paramInt);
    return this.mCalendar.getTimeInMillis();
  }
  
  private int getDayOfWeek(long paramLong)
  {
    this.mCalendar.setTimeInMillis(paramLong);
    return this.mCalendar.get(7);
  }
  
  private long getNextTime(long paramLong, int paramInt1, int paramInt2)
  {
    long l2 = getTime(paramLong, paramInt1, paramInt2);
    long l1 = l2;
    if (l2 <= paramLong) {
      l1 = addDays(l2, 1);
    }
    return l1;
  }
  
  private long getTime(long paramLong, int paramInt1, int paramInt2)
  {
    this.mCalendar.setTimeInMillis(paramLong);
    this.mCalendar.set(11, paramInt1);
    this.mCalendar.set(12, paramInt2);
    this.mCalendar.set(13, 0);
    this.mCalendar.set(14, 0);
    return this.mCalendar.getTimeInMillis();
  }
  
  private boolean isInSchedule(int paramInt, long paramLong1, long paramLong2, long paramLong3)
  {
    boolean bool2 = false;
    int i = getDayOfWeek(paramLong1);
    paramLong2 = addDays(paramLong2, paramInt);
    paramLong3 = addDays(paramLong3, paramInt);
    boolean bool1 = bool2;
    if (this.mDays.contains(Integer.valueOf((i - 1 + paramInt % 7 + 7) % 7 + 1)))
    {
      bool1 = bool2;
      if (paramLong1 >= paramLong2)
      {
        bool1 = bool2;
        if (paramLong1 < paramLong3) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private void updateDays()
  {
    this.mDays.clear();
    if ((this.mSchedule != null) && (this.mSchedule.days != null))
    {
      int i = 0;
      while (i < this.mSchedule.days.length)
      {
        this.mDays.add(Integer.valueOf(this.mSchedule.days[i]));
        i += 1;
      }
    }
  }
  
  public long getNextChangeTime(long paramLong)
  {
    if (this.mSchedule == null) {
      return 0L;
    }
    return Math.min(getNextTime(paramLong, this.mSchedule.startHour, this.mSchedule.startMinute), getNextTime(paramLong, this.mSchedule.endHour, this.mSchedule.endMinute));
  }
  
  public boolean isInSchedule(long paramLong)
  {
    if ((this.mSchedule == null) || (this.mDays.size() == 0)) {
      return false;
    }
    long l3 = getTime(paramLong, this.mSchedule.startHour, this.mSchedule.startMinute);
    long l2 = getTime(paramLong, this.mSchedule.endHour, this.mSchedule.endMinute);
    long l1 = l2;
    if (l2 <= l3) {
      l1 = addDays(l2, 1);
    }
    if (!isInSchedule(-1, paramLong, l3, l1)) {
      return isInSchedule(0, paramLong, l3, l1);
    }
    return true;
  }
  
  public void maybeSetNextAlarm(long paramLong1, long paramLong2)
  {
    if ((this.mSchedule != null) && (this.mSchedule.exitAtAlarm) && (paramLong1 > this.mSchedule.nextAlarm)) {
      this.mSchedule.nextAlarm = paramLong2;
    }
  }
  
  public void setSchedule(ZenModeConfig.ScheduleInfo paramScheduleInfo)
  {
    if (Objects.equals(this.mSchedule, paramScheduleInfo)) {
      return;
    }
    this.mSchedule = paramScheduleInfo;
    updateDays();
  }
  
  public void setTimeZone(TimeZone paramTimeZone)
  {
    this.mCalendar.setTimeZone(paramTimeZone);
  }
  
  public boolean shouldExitForAlarm(long paramLong)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mSchedule.exitAtAlarm)
    {
      bool1 = bool2;
      if (this.mSchedule.nextAlarm != 0L)
      {
        bool1 = bool2;
        if (paramLong >= this.mSchedule.nextAlarm) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public String toString()
  {
    return "ScheduleCalendar[mDays=" + this.mDays + ", mSchedule=" + this.mSchedule + "]";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/notification/ScheduleCalendar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */