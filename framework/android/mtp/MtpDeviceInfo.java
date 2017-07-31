package android.mtp;

public class MtpDeviceInfo
{
  private int[] mEventsSupported;
  private String mManufacturer;
  private String mModel;
  private int[] mOperationsSupported;
  private String mSerialNumber;
  private String mVersion;
  
  private static boolean isSupported(int[] paramArrayOfInt, int paramInt)
  {
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public final int[] getEventsSupported()
  {
    return this.mEventsSupported;
  }
  
  public final String getManufacturer()
  {
    return this.mManufacturer;
  }
  
  public final String getModel()
  {
    return this.mModel;
  }
  
  public final int[] getOperationsSupported()
  {
    return this.mOperationsSupported;
  }
  
  public final String getSerialNumber()
  {
    return this.mSerialNumber;
  }
  
  public final String getVersion()
  {
    return this.mVersion;
  }
  
  public boolean isEventSupported(int paramInt)
  {
    return isSupported(this.mEventsSupported, paramInt);
  }
  
  public boolean isOperationSupported(int paramInt)
  {
    return isSupported(this.mOperationsSupported, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpDeviceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */