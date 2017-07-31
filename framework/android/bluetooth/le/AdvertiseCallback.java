package android.bluetooth.le;

public abstract class AdvertiseCallback
{
  public static final int ADVERTISE_FAILED_ALREADY_STARTED = 3;
  public static final int ADVERTISE_FAILED_DATA_TOO_LARGE = 1;
  public static final int ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 5;
  public static final int ADVERTISE_FAILED_INTERNAL_ERROR = 4;
  public static final int ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 2;
  public static final int ADVERTISE_SUCCESS = 0;
  
  public void onStartFailure(int paramInt) {}
  
  public void onStartSuccess(AdvertiseSettings paramAdvertiseSettings) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/AdvertiseCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */