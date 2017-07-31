package android.os;

public final class FactoryTest
{
  public static final int FACTORY_TEST_HIGH_LEVEL = 2;
  public static final int FACTORY_TEST_LOW_LEVEL = 1;
  public static final int FACTORY_TEST_OFF = 0;
  
  public static int getMode()
  {
    return SystemProperties.getInt("ro.factorytest", 0);
  }
  
  public static boolean isLongPressOnPowerOffEnabled()
  {
    boolean bool = false;
    if (SystemProperties.getInt("factory.long_press_power_off", 0) != 0) {
      bool = true;
    }
    return bool;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/FactoryTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */