package android.os;

public abstract class PowerManagerInternal
{
  public static final int POWER_HINT_INTERACTION = 2;
  public static final int POWER_HINT_LAUNCH = 8;
  public static final int POWER_HINT_SUSTAINED_PERFORMANCE_MODE = 6;
  public static final int WAKEFULNESS_ASLEEP = 0;
  public static final int WAKEFULNESS_AWAKE = 1;
  public static final int WAKEFULNESS_DOZING = 3;
  public static final int WAKEFULNESS_DREAMING = 2;
  
  public static boolean isInteractive(int paramInt)
  {
    return (paramInt == 1) || (paramInt == 2);
  }
  
  public static String wakefulnessToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return Integer.toString(paramInt);
    case 0: 
      return "Asleep";
    case 1: 
      return "Awake";
    case 2: 
      return "Dreaming";
    }
    return "Dozing";
  }
  
  public abstract boolean getLowPowerModeEnabled();
  
  public abstract void powerHint(int paramInt1, int paramInt2);
  
  public abstract void registerLowPowerModeObserver(LowPowerModeListener paramLowPowerModeListener);
  
  public abstract void setButtonBrightnessOverrideFromWindowManager(int paramInt);
  
  public abstract void setDeviceIdleAggressive(boolean paramBoolean);
  
  public abstract boolean setDeviceIdleMode(boolean paramBoolean);
  
  public abstract void setDeviceIdleState(int paramInt);
  
  public abstract void setDeviceIdleTempWhitelist(int[] paramArrayOfInt);
  
  public abstract void setDeviceIdleWhitelist(int[] paramArrayOfInt);
  
  public abstract void setDozeOverrideFromDreamManager(int paramInt1, int paramInt2);
  
  public abstract boolean setLightDeviceIdleMode(boolean paramBoolean);
  
  public abstract void setMaximumScreenOffTimeoutFromDeviceAdmin(int paramInt);
  
  public abstract void setScreenBrightnessOverrideFromWindowManager(int paramInt);
  
  public abstract void setUserActivityTimeoutOverrideFromWindowManager(long paramLong);
  
  public abstract void setUserInactiveOverrideFromWindowManager();
  
  public abstract void uidGone(int paramInt);
  
  public abstract void updateUidProcState(int paramInt1, int paramInt2);
  
  public static abstract interface LowPowerModeListener
  {
    public abstract void onLowPowerModeChanged(boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/PowerManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */