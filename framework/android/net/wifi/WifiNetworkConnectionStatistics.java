package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class WifiNetworkConnectionStatistics
  implements Parcelable
{
  public static final Parcelable.Creator<WifiNetworkConnectionStatistics> CREATOR = new Parcelable.Creator()
  {
    public WifiNetworkConnectionStatistics createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WifiNetworkConnectionStatistics(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public WifiNetworkConnectionStatistics[] newArray(int paramAnonymousInt)
    {
      return new WifiNetworkConnectionStatistics[paramAnonymousInt];
    }
  };
  private static final String TAG = "WifiNetworkConnnectionStatistics";
  public int numConnection;
  public int numUsage;
  
  public WifiNetworkConnectionStatistics() {}
  
  public WifiNetworkConnectionStatistics(int paramInt1, int paramInt2)
  {
    this.numConnection = paramInt1;
    this.numUsage = paramInt2;
  }
  
  public WifiNetworkConnectionStatistics(WifiNetworkConnectionStatistics paramWifiNetworkConnectionStatistics)
  {
    this.numConnection = paramWifiNetworkConnectionStatistics.numConnection;
    this.numUsage = paramWifiNetworkConnectionStatistics.numUsage;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("c=").append(this.numConnection);
    localStringBuilder.append(" u=").append(this.numUsage);
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.numConnection);
    paramParcel.writeInt(this.numUsage);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiNetworkConnectionStatistics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */