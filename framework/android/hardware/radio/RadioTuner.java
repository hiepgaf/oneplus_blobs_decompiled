package android.hardware.radio;

public abstract class RadioTuner
{
  public static final int DIRECTION_DOWN = 1;
  public static final int DIRECTION_UP = 0;
  public static final int ERROR_CANCELLED = 2;
  public static final int ERROR_CONFIG = 4;
  public static final int ERROR_HARDWARE_FAILURE = 0;
  public static final int ERROR_SCAN_TIMEOUT = 3;
  public static final int ERROR_SERVER_DIED = 1;
  
  public abstract int cancel();
  
  public abstract void close();
  
  public abstract int getConfiguration(RadioManager.BandConfig[] paramArrayOfBandConfig);
  
  public abstract boolean getMute();
  
  public abstract int getProgramInformation(RadioManager.ProgramInfo[] paramArrayOfProgramInfo);
  
  public abstract boolean hasControl();
  
  public abstract boolean isAntennaConnected();
  
  public abstract int scan(int paramInt, boolean paramBoolean);
  
  public abstract int setConfiguration(RadioManager.BandConfig paramBandConfig);
  
  public abstract int setMute(boolean paramBoolean);
  
  public abstract int step(int paramInt, boolean paramBoolean);
  
  public abstract int tune(int paramInt1, int paramInt2);
  
  public static abstract class Callback
  {
    public void onAntennaState(boolean paramBoolean) {}
    
    public void onConfigurationChanged(RadioManager.BandConfig paramBandConfig) {}
    
    public void onControlChanged(boolean paramBoolean) {}
    
    public void onEmergencyAnnouncement(boolean paramBoolean) {}
    
    public void onError(int paramInt) {}
    
    public void onMetadataChanged(RadioMetadata paramRadioMetadata) {}
    
    public void onProgramInfoChanged(RadioManager.ProgramInfo paramProgramInfo) {}
    
    public void onTrafficAnnouncement(boolean paramBoolean) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/radio/RadioTuner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */