package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class WifiDevice
  implements Parcelable
{
  private static final String AP_STA_CONNECTED_STR = "AP-STA-CONNECTED";
  private static final String AP_STA_DISCONNECTED_STR = "AP-STA-DISCONNECTED";
  public static final int BLACKLISTED = 2;
  public static final int CONNECTED = 1;
  public static final Parcelable.Creator<WifiDevice> CREATOR = new Parcelable.Creator()
  {
    public WifiDevice createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiDevice localWifiDevice = new WifiDevice();
      localWifiDevice.deviceAddress = paramAnonymousParcel.readString();
      localWifiDevice.deviceName = paramAnonymousParcel.readString();
      localWifiDevice.deviceState = paramAnonymousParcel.readInt();
      localWifiDevice.connectedTime = paramAnonymousParcel.readLong();
      return localWifiDevice;
    }
    
    public WifiDevice[] newArray(int paramAnonymousInt)
    {
      return new WifiDevice[paramAnonymousInt];
    }
  };
  public static final int DISCONNECTED = 0;
  public long connectedTime = 0L;
  public String deviceAddress = "";
  public String deviceName = "";
  public int deviceState = 0;
  
  public WifiDevice() {}
  
  public WifiDevice(String paramString)
    throws IllegalArgumentException
  {
    paramString = paramString.split(" ");
    if (paramString.length < 2) {
      throw new IllegalArgumentException();
    }
    if (paramString[0].indexOf("AP-STA-CONNECTED") != -1) {}
    for (this.deviceState = 1;; this.deviceState = 0)
    {
      this.deviceAddress = paramString[1];
      return;
      if (paramString[0].indexOf("AP-STA-DISCONNECTED") == -1) {
        break;
      }
    }
    throw new IllegalArgumentException();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject != null) && ((paramObject instanceof WifiDevice)))
    {
      if (this.deviceAddress == null)
      {
        if (((WifiDevice)paramObject).deviceAddress == null) {
          bool = true;
        }
        return bool;
      }
    }
    else {
      return false;
    }
    return this.deviceAddress.equals(((WifiDevice)paramObject).deviceAddress);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.deviceAddress);
    paramParcel.writeString(this.deviceName);
    paramParcel.writeInt(this.deviceState);
    paramParcel.writeLong(this.connectedTime);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */