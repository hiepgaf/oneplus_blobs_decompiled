package android.net.wifi.p2p;

import android.net.wifi.WpsInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class WifiP2pConfig
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pConfig> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiP2pConfig localWifiP2pConfig = new WifiP2pConfig();
      localWifiP2pConfig.deviceAddress = paramAnonymousParcel.readString();
      localWifiP2pConfig.wps = ((WpsInfo)paramAnonymousParcel.readParcelable(null));
      localWifiP2pConfig.groupOwnerIntent = paramAnonymousParcel.readInt();
      localWifiP2pConfig.netId = paramAnonymousParcel.readInt();
      return localWifiP2pConfig;
    }
    
    public WifiP2pConfig[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pConfig[paramAnonymousInt];
    }
  };
  public static final int MAX_GROUP_OWNER_INTENT = 15;
  public static final int MIN_GROUP_OWNER_INTENT = 0;
  public String deviceAddress = "";
  public int groupOwnerIntent = -1;
  public int netId = -2;
  public WpsInfo wps;
  
  public WifiP2pConfig()
  {
    this.wps = new WpsInfo();
    this.wps.setup = 0;
  }
  
  public WifiP2pConfig(WifiP2pConfig paramWifiP2pConfig)
  {
    if (paramWifiP2pConfig != null)
    {
      this.deviceAddress = paramWifiP2pConfig.deviceAddress;
      this.wps = new WpsInfo(paramWifiP2pConfig.wps);
      this.groupOwnerIntent = paramWifiP2pConfig.groupOwnerIntent;
      this.netId = paramWifiP2pConfig.netId;
    }
  }
  
  public WifiP2pConfig(String paramString)
    throws IllegalArgumentException
  {
    paramString = paramString.split(" ");
    if ((paramString.length >= 2) && (paramString[0].equals("P2P-GO-NEG-REQUEST")))
    {
      this.deviceAddress = paramString[1];
      this.wps = new WpsInfo();
      if (paramString.length > 2) {
        paramString = paramString[2].split("=");
      }
    }
    try
    {
      i = Integer.parseInt(paramString[1]);
      switch (i)
      {
      case 2: 
      case 3: 
      default: 
        this.wps.setup = 0;
        return;
        throw new IllegalArgumentException("Malformed supplicant event");
      }
    }
    catch (NumberFormatException paramString)
    {
      for (;;)
      {
        int i = 0;
      }
      this.wps.setup = 1;
      return;
    }
    this.wps.setup = 0;
    return;
    this.wps.setup = 2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void invalidate()
  {
    this.deviceAddress = "";
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("\n address: ").append(this.deviceAddress);
    localStringBuffer.append("\n wps: ").append(this.wps);
    localStringBuffer.append("\n groupOwnerIntent: ").append(this.groupOwnerIntent);
    localStringBuffer.append("\n persist: ").append(this.netId);
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.deviceAddress);
    paramParcel.writeParcelable(this.wps, paramInt);
    paramParcel.writeInt(this.groupOwnerIntent);
    paramParcel.writeInt(this.netId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */