package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WifiP2pInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pInfo> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool2 = false;
      WifiP2pInfo localWifiP2pInfo = new WifiP2pInfo();
      if (paramAnonymousParcel.readByte() == 1) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        localWifiP2pInfo.groupFormed = bool1;
        bool1 = bool2;
        if (paramAnonymousParcel.readByte() == 1) {
          bool1 = true;
        }
        localWifiP2pInfo.isGroupOwner = bool1;
        if (paramAnonymousParcel.readByte() == 1) {}
        try
        {
          localWifiP2pInfo.groupOwnerAddress = InetAddress.getByAddress(paramAnonymousParcel.createByteArray());
          return localWifiP2pInfo;
        }
        catch (UnknownHostException paramAnonymousParcel) {}
      }
      return localWifiP2pInfo;
    }
    
    public WifiP2pInfo[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pInfo[paramAnonymousInt];
    }
  };
  public boolean groupFormed;
  public InetAddress groupOwnerAddress;
  public boolean isGroupOwner;
  
  public WifiP2pInfo() {}
  
  public WifiP2pInfo(WifiP2pInfo paramWifiP2pInfo)
  {
    if (paramWifiP2pInfo != null)
    {
      this.groupFormed = paramWifiP2pInfo.groupFormed;
      this.isGroupOwner = paramWifiP2pInfo.isGroupOwner;
      this.groupOwnerAddress = paramWifiP2pInfo.groupOwnerAddress;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("groupFormed: ").append(this.groupFormed).append(" isGroupOwner: ").append(this.isGroupOwner).append(" groupOwnerAddress: ").append(this.groupOwnerAddress);
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.groupFormed)
    {
      b = 1;
      paramParcel.writeByte(b);
      if (!this.isGroupOwner) {
        break label57;
      }
    }
    label57:
    for (byte b = 1;; b = 0)
    {
      paramParcel.writeByte(b);
      if (this.groupOwnerAddress == null) {
        break label62;
      }
      paramParcel.writeByte((byte)1);
      paramParcel.writeByteArray(this.groupOwnerAddress.getAddress());
      return;
      b = 0;
      break;
    }
    label62:
    paramParcel.writeByte((byte)0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */