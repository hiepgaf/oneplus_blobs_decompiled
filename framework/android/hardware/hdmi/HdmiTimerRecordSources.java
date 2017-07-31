package android.hardware.hdmi;

import android.util.Log;

public class HdmiTimerRecordSources
{
  private static final int EXTERNAL_SOURCE_SPECIFIER_EXTERNAL_PHYSICAL_ADDRESS = 5;
  private static final int EXTERNAL_SOURCE_SPECIFIER_EXTERNAL_PLUG = 4;
  public static final int RECORDING_SEQUENCE_REPEAT_FRIDAY = 32;
  private static final int RECORDING_SEQUENCE_REPEAT_MASK = 127;
  public static final int RECORDING_SEQUENCE_REPEAT_MONDAY = 2;
  public static final int RECORDING_SEQUENCE_REPEAT_ONCE_ONLY = 0;
  public static final int RECORDING_SEQUENCE_REPEAT_SATUREDAY = 64;
  public static final int RECORDING_SEQUENCE_REPEAT_SUNDAY = 1;
  public static final int RECORDING_SEQUENCE_REPEAT_THURSDAY = 16;
  public static final int RECORDING_SEQUENCE_REPEAT_TUESDAY = 4;
  public static final int RECORDING_SEQUENCE_REPEAT_WEDNESDAY = 8;
  private static final String TAG = "HdmiTimerRecordingSources";
  
  private static void checkDurationValue(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > 99)) {
      throw new IllegalArgumentException("Hour should be in rage of [0, 99]:" + paramInt1);
    }
    if ((paramInt2 < 0) || (paramInt2 > 59)) {
      throw new IllegalArgumentException("minute should be in rage of [0, 59]:" + paramInt2);
    }
  }
  
  private static void checkTimeValue(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > 23)) {
      throw new IllegalArgumentException("Hour should be in rage of [0, 23]:" + paramInt1);
    }
    if ((paramInt2 < 0) || (paramInt2 > 59)) {
      throw new IllegalArgumentException("Minute should be in rage of [0, 59]:" + paramInt2);
    }
  }
  
  public static boolean checkTimerRecordSource(int paramInt, byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length - 7;
    switch (paramInt)
    {
    default: 
      return false;
    case 1: 
      return 7 == i;
    case 2: 
      return 4 == i;
    }
    paramInt = paramArrayOfByte[7];
    if (paramInt == 4) {
      return 2 == i;
    }
    if (paramInt == 5) {
      return 3 == i;
    }
    return false;
  }
  
  private static void checkTimerRecordSourceInputs(TimerInfo paramTimerInfo, HdmiRecordSources.RecordSource paramRecordSource)
  {
    if (paramTimerInfo == null)
    {
      Log.w("HdmiTimerRecordingSources", "TimerInfo should not be null.");
      throw new IllegalArgumentException("TimerInfo should not be null.");
    }
    if (paramRecordSource == null)
    {
      Log.w("HdmiTimerRecordingSources", "source should not be null.");
      throw new IllegalArgumentException("source should not be null.");
    }
  }
  
  public static Duration durationOf(int paramInt1, int paramInt2)
  {
    checkDurationValue(paramInt1, paramInt2);
    return new Duration(paramInt1, paramInt2, null);
  }
  
  public static TimerRecordSource ofAnalogueSource(TimerInfo paramTimerInfo, HdmiRecordSources.AnalogueServiceSource paramAnalogueServiceSource)
  {
    checkTimerRecordSourceInputs(paramTimerInfo, paramAnalogueServiceSource);
    return new TimerRecordSource(paramTimerInfo, paramAnalogueServiceSource, null);
  }
  
  public static TimerRecordSource ofDigitalSource(TimerInfo paramTimerInfo, HdmiRecordSources.DigitalServiceSource paramDigitalServiceSource)
  {
    checkTimerRecordSourceInputs(paramTimerInfo, paramDigitalServiceSource);
    return new TimerRecordSource(paramTimerInfo, paramDigitalServiceSource, null);
  }
  
  public static TimerRecordSource ofExternalPhysicalAddress(TimerInfo paramTimerInfo, HdmiRecordSources.ExternalPhysicalAddress paramExternalPhysicalAddress)
  {
    checkTimerRecordSourceInputs(paramTimerInfo, paramExternalPhysicalAddress);
    return new TimerRecordSource(paramTimerInfo, new ExternalSourceDecorator(paramExternalPhysicalAddress, 5, null), null);
  }
  
  public static TimerRecordSource ofExternalPlug(TimerInfo paramTimerInfo, HdmiRecordSources.ExternalPlugData paramExternalPlugData)
  {
    checkTimerRecordSourceInputs(paramTimerInfo, paramExternalPlugData);
    return new TimerRecordSource(paramTimerInfo, new ExternalSourceDecorator(paramExternalPlugData, 4, null), null);
  }
  
  public static Time timeOf(int paramInt1, int paramInt2)
  {
    checkTimeValue(paramInt1, paramInt2);
    return new Time(paramInt1, paramInt2, null);
  }
  
  public static TimerInfo timerInfoOf(int paramInt1, int paramInt2, Time paramTime, Duration paramDuration, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt1 > 31)) {
      throw new IllegalArgumentException("Day of month should be in range of [0, 31]:" + paramInt1);
    }
    if ((paramInt2 < 1) || (paramInt2 > 12)) {
      throw new IllegalArgumentException("Month of year should be in range of [1, 12]:" + paramInt2);
    }
    checkTimeValue(paramTime.mHour, paramTime.mMinute);
    checkDurationValue(paramDuration.mHour, paramDuration.mMinute);
    if ((paramInt3 != 0) && ((paramInt3 & 0xFFFFFF80) != 0)) {
      throw new IllegalArgumentException("Invalid reecording sequence value:" + paramInt3);
    }
    return new TimerInfo(paramInt1, paramInt2, paramTime, paramDuration, paramInt3, null);
  }
  
  public static final class Duration
    extends HdmiTimerRecordSources.TimeUnit
  {
    private Duration(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
  }
  
  private static class ExternalSourceDecorator
    extends HdmiRecordSources.RecordSource
  {
    private final int mExternalSourceSpecifier;
    private final HdmiRecordSources.RecordSource mRecordSource;
    
    private ExternalSourceDecorator(HdmiRecordSources.RecordSource paramRecordSource, int paramInt)
    {
      super(paramRecordSource.getDataSize(false) + 1);
      this.mRecordSource = paramRecordSource;
      this.mExternalSourceSpecifier = paramInt;
    }
    
    int extraParamToByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      paramArrayOfByte[paramInt] = ((byte)this.mExternalSourceSpecifier);
      this.mRecordSource.toByteArray(false, paramArrayOfByte, paramInt + 1);
      return getDataSize(false);
    }
  }
  
  public static final class Time
    extends HdmiTimerRecordSources.TimeUnit
  {
    private Time(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
  }
  
  static class TimeUnit
  {
    final int mHour;
    final int mMinute;
    
    TimeUnit(int paramInt1, int paramInt2)
    {
      this.mHour = paramInt1;
      this.mMinute = paramInt2;
    }
    
    static byte toBcdByte(int paramInt)
    {
      return (byte)(paramInt / 10 % 10 << 4 | paramInt % 10);
    }
    
    int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      paramArrayOfByte[paramInt] = toBcdByte(this.mHour);
      paramArrayOfByte[(paramInt + 1)] = toBcdByte(this.mMinute);
      return 2;
    }
  }
  
  public static final class TimerInfo
  {
    private static final int BASIC_INFO_SIZE = 7;
    private static final int DAY_OF_MONTH_SIZE = 1;
    private static final int DURATION_SIZE = 2;
    private static final int MONTH_OF_YEAR_SIZE = 1;
    private static final int RECORDING_SEQUENCE_SIZE = 1;
    private static final int START_TIME_SIZE = 2;
    private final int mDayOfMonth;
    private final HdmiTimerRecordSources.Duration mDuration;
    private final int mMonthOfYear;
    private final int mRecordingSequence;
    private final HdmiTimerRecordSources.Time mStartTime;
    
    private TimerInfo(int paramInt1, int paramInt2, HdmiTimerRecordSources.Time paramTime, HdmiTimerRecordSources.Duration paramDuration, int paramInt3)
    {
      this.mDayOfMonth = paramInt1;
      this.mMonthOfYear = paramInt2;
      this.mStartTime = paramTime;
      this.mDuration = paramDuration;
      this.mRecordingSequence = paramInt3;
    }
    
    int getDataSize()
    {
      return 7;
    }
    
    int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      paramArrayOfByte[paramInt] = ((byte)this.mDayOfMonth);
      paramInt += 1;
      paramArrayOfByte[paramInt] = ((byte)this.mMonthOfYear);
      paramInt += 1;
      paramInt += this.mStartTime.toByteArray(paramArrayOfByte, paramInt);
      paramArrayOfByte[(paramInt + this.mDuration.toByteArray(paramArrayOfByte, paramInt))] = ((byte)this.mRecordingSequence);
      return getDataSize();
    }
  }
  
  public static final class TimerRecordSource
  {
    private final HdmiRecordSources.RecordSource mRecordSource;
    private final HdmiTimerRecordSources.TimerInfo mTimerInfo;
    
    private TimerRecordSource(HdmiTimerRecordSources.TimerInfo paramTimerInfo, HdmiRecordSources.RecordSource paramRecordSource)
    {
      this.mTimerInfo = paramTimerInfo;
      this.mRecordSource = paramRecordSource;
    }
    
    int getDataSize()
    {
      return this.mTimerInfo.getDataSize() + this.mRecordSource.getDataSize(false);
    }
    
    int toByteArray(byte[] paramArrayOfByte, int paramInt)
    {
      int i = this.mTimerInfo.toByteArray(paramArrayOfByte, paramInt);
      this.mRecordSource.toByteArray(false, paramArrayOfByte, paramInt + i);
      return getDataSize();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiTimerRecordSources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */