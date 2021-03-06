package android.os.health;

public final class ProcessHealthStats
{
  public static final HealthKeys.Constants CONSTANTS = new HealthKeys.Constants(ProcessHealthStats.class);
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_ANR_COUNT = 30005;
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_CRASHES_COUNT = 30004;
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_FOREGROUND_MS = 30006;
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_STARTS_COUNT = 30003;
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_SYSTEM_TIME_MS = 30002;
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_USER_TIME_MS = 30001;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/ProcessHealthStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */