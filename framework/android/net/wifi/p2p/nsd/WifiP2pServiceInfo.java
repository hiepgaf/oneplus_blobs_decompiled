package android.net.wifi.p2p.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class WifiP2pServiceInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pServiceInfo> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pServiceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      ArrayList localArrayList = new ArrayList();
      paramAnonymousParcel.readStringList(localArrayList);
      return new WifiP2pServiceInfo(localArrayList);
    }
    
    public WifiP2pServiceInfo[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pServiceInfo[paramAnonymousInt];
    }
  };
  public static final int SERVICE_TYPE_ALL = 0;
  public static final int SERVICE_TYPE_BONJOUR = 1;
  public static final int SERVICE_TYPE_UPNP = 2;
  public static final int SERVICE_TYPE_VENDOR_SPECIFIC = 255;
  public static final int SERVICE_TYPE_WS_DISCOVERY = 3;
  private List<String> mQueryList;
  
  protected WifiP2pServiceInfo(List<String> paramList)
  {
    if (paramList == null) {
      throw new IllegalArgumentException("query list cannot be null");
    }
    this.mQueryList = paramList;
  }
  
  static String bin2HexStr(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    int j = paramArrayOfByte.length;
    while (i < j)
    {
      int k = paramArrayOfByte[i];
      try
      {
        String str = Integer.toHexString(k & 0xFF);
        if (str.length() == 1) {
          localStringBuffer.append('0');
        }
        localStringBuffer.append(str);
        i += 1;
      }
      catch (Exception paramArrayOfByte)
      {
        paramArrayOfByte.printStackTrace();
        return null;
      }
    }
    return localStringBuffer.toString();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof WifiP2pServiceInfo)) {
      return false;
    }
    return this.mQueryList.equals(((WifiP2pServiceInfo)paramObject).mQueryList);
  }
  
  public List<String> getSupplicantQueryList()
  {
    return this.mQueryList;
  }
  
  public int hashCode()
  {
    if (this.mQueryList == null) {}
    for (int i = 0;; i = this.mQueryList.hashCode()) {
      return i + 527;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeStringList(this.mQueryList);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/nsd/WifiP2pServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */