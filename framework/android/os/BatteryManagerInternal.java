package android.os;

public abstract class BatteryManagerInternal
{
  public abstract int getBatteryLevel();
  
  public abstract boolean getBatteryLevelLow();
  
  public abstract int getInvalidCharger();
  
  public abstract int getPlugType();
  
  public abstract boolean isPowered(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/BatteryManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */