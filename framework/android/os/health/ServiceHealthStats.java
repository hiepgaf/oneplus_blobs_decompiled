package android.os.health;

public final class ServiceHealthStats
{
  public static final HealthKeys.Constants CONSTANTS = new HealthKeys.Constants(ServiceHealthStats.class);
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_LAUNCH_COUNT = 50002;
  @HealthKeys.Constant(type=1)
  public static final int MEASUREMENT_START_SERVICE_COUNT = 50001;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/health/ServiceHealthStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */