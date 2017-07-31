package android.hardware.hdmi;

public abstract class HdmiRecordListener
{
  public void onClearTimerRecordingResult(int paramInt1, int paramInt2) {}
  
  public void onOneTouchRecordResult(int paramInt1, int paramInt2) {}
  
  public abstract HdmiRecordSources.RecordSource onOneTouchRecordSourceRequested(int paramInt);
  
  public void onTimerRecordingResult(int paramInt, TimerStatusData paramTimerStatusData) {}
  
  public static class TimerStatusData
  {
    private int mDurationHour;
    private int mDurationMinute;
    private int mExtraError;
    private int mMediaInfo;
    private int mNotProgrammedError;
    private boolean mOverlapped;
    private boolean mProgrammed;
    private int mProgrammedInfo;
    
    private static int bcdByteToInt(byte paramByte)
    {
      return (paramByte >> 4 & 0xF) * 10 + paramByte & 0xF;
    }
    
    static TimerStatusData parseFrom(int paramInt)
    {
      boolean bool2 = true;
      TimerStatusData localTimerStatusData = new TimerStatusData();
      boolean bool1;
      if ((paramInt >> 31 & 0x1) != 0)
      {
        bool1 = true;
        localTimerStatusData.mOverlapped = bool1;
        localTimerStatusData.mMediaInfo = (paramInt >> 29 & 0x3);
        if ((paramInt >> 28 & 0x1) == 0) {
          break label118;
        }
        bool1 = bool2;
        label47:
        localTimerStatusData.mProgrammed = bool1;
        if (!localTimerStatusData.mProgrammed) {
          break label123;
        }
        localTimerStatusData.mProgrammedInfo = (paramInt >> 24 & 0xF);
        localTimerStatusData.mDurationHour = bcdByteToInt((byte)(paramInt >> 16 & 0xFF));
      }
      for (localTimerStatusData.mDurationMinute = bcdByteToInt((byte)(paramInt >> 8 & 0xFF));; localTimerStatusData.mDurationMinute = bcdByteToInt((byte)(paramInt >> 8 & 0xFF)))
      {
        localTimerStatusData.mExtraError = (paramInt & 0xFF);
        return localTimerStatusData;
        bool1 = false;
        break;
        label118:
        bool1 = false;
        break label47;
        label123:
        localTimerStatusData.mNotProgrammedError = (paramInt >> 24 & 0xF);
        localTimerStatusData.mDurationHour = bcdByteToInt((byte)(paramInt >> 16 & 0xFF));
      }
    }
    
    public int getDurationHour()
    {
      return this.mDurationHour;
    }
    
    public int getDurationMinute()
    {
      return this.mDurationMinute;
    }
    
    public int getExtraError()
    {
      return this.mExtraError;
    }
    
    public int getMediaInfo()
    {
      return this.mMediaInfo;
    }
    
    public int getNotProgammedError()
    {
      if (isProgrammed()) {
        throw new IllegalStateException("Has no not-programmed error. Call getProgrammedInfo() instead.");
      }
      return this.mNotProgrammedError;
    }
    
    public int getProgrammedInfo()
    {
      if (!isProgrammed()) {
        throw new IllegalStateException("No programmed info. Call getNotProgammedError() instead.");
      }
      return this.mProgrammedInfo;
    }
    
    public boolean isOverlapped()
    {
      return this.mOverlapped;
    }
    
    public boolean isProgrammed()
    {
      return this.mProgrammed;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiRecordListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */