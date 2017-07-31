package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Iterator;

public class WifiConnectionStatistics
  implements Parcelable
{
  public static final Parcelable.Creator<WifiConnectionStatistics> CREATOR = new Parcelable.Creator()
  {
    public WifiConnectionStatistics createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiConnectionStatistics localWifiConnectionStatistics = new WifiConnectionStatistics();
      localWifiConnectionStatistics.num24GhzConnected = paramAnonymousParcel.readInt();
      localWifiConnectionStatistics.num5GhzConnected = paramAnonymousParcel.readInt();
      localWifiConnectionStatistics.numAutoJoinAttempt = paramAnonymousParcel.readInt();
      localWifiConnectionStatistics.numAutoRoamAttempt = paramAnonymousParcel.readInt();
      localWifiConnectionStatistics.numWifiManagerJoinAttempt = paramAnonymousParcel.readInt();
      int i = paramAnonymousParcel.readInt();
      while (i > 0)
      {
        String str = paramAnonymousParcel.readString();
        WifiNetworkConnectionStatistics localWifiNetworkConnectionStatistics = new WifiNetworkConnectionStatistics(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
        localWifiConnectionStatistics.untrustedNetworkHistory.put(str, localWifiNetworkConnectionStatistics);
        i -= 1;
      }
      return localWifiConnectionStatistics;
    }
    
    public WifiConnectionStatistics[] newArray(int paramAnonymousInt)
    {
      return new WifiConnectionStatistics[paramAnonymousInt];
    }
  };
  private static final String TAG = "WifiConnnectionStatistics";
  public int num24GhzConnected;
  public int num5GhzConnected;
  public int numAutoJoinAttempt;
  public int numAutoRoamAttempt;
  public int numWifiManagerJoinAttempt;
  public HashMap<String, WifiNetworkConnectionStatistics> untrustedNetworkHistory = new HashMap();
  
  public WifiConnectionStatistics() {}
  
  public WifiConnectionStatistics(WifiConnectionStatistics paramWifiConnectionStatistics)
  {
    if (paramWifiConnectionStatistics != null) {
      this.untrustedNetworkHistory.putAll(paramWifiConnectionStatistics.untrustedNetworkHistory);
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void incrementOrAddUntrusted(String paramString, int paramInt1, int paramInt2)
  {
    if (TextUtils.isEmpty(paramString)) {
      return;
    }
    WifiNetworkConnectionStatistics localWifiNetworkConnectionStatistics2;
    if (this.untrustedNetworkHistory.containsKey(paramString))
    {
      localWifiNetworkConnectionStatistics2 = (WifiNetworkConnectionStatistics)this.untrustedNetworkHistory.get(paramString);
      localWifiNetworkConnectionStatistics1 = localWifiNetworkConnectionStatistics2;
      if (localWifiNetworkConnectionStatistics2 != null)
      {
        localWifiNetworkConnectionStatistics2.numConnection += paramInt1;
        localWifiNetworkConnectionStatistics2.numUsage += paramInt2;
      }
    }
    for (WifiNetworkConnectionStatistics localWifiNetworkConnectionStatistics1 = localWifiNetworkConnectionStatistics2;; localWifiNetworkConnectionStatistics1 = new WifiNetworkConnectionStatistics(paramInt1, paramInt2))
    {
      if (localWifiNetworkConnectionStatistics1 != null) {
        this.untrustedNetworkHistory.put(paramString, localWifiNetworkConnectionStatistics1);
      }
      return;
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Connected on: 2.4Ghz=").append(this.num24GhzConnected);
    localStringBuilder.append(" 5Ghz=").append(this.num5GhzConnected).append("\n");
    localStringBuilder.append(" join=").append(this.numWifiManagerJoinAttempt);
    localStringBuilder.append("\\").append(this.numAutoJoinAttempt).append("\n");
    localStringBuilder.append(" roam=").append(this.numAutoRoamAttempt).append("\n");
    Iterator localIterator = this.untrustedNetworkHistory.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      WifiNetworkConnectionStatistics localWifiNetworkConnectionStatistics = (WifiNetworkConnectionStatistics)this.untrustedNetworkHistory.get(str);
      if (localWifiNetworkConnectionStatistics != null) {
        localStringBuilder.append(str).append(" ").append(localWifiNetworkConnectionStatistics.toString()).append("\n");
      }
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.num24GhzConnected);
    paramParcel.writeInt(this.num5GhzConnected);
    paramParcel.writeInt(this.numAutoJoinAttempt);
    paramParcel.writeInt(this.numAutoRoamAttempt);
    paramParcel.writeInt(this.numWifiManagerJoinAttempt);
    paramParcel.writeInt(this.untrustedNetworkHistory.size());
    Iterator localIterator = this.untrustedNetworkHistory.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      WifiNetworkConnectionStatistics localWifiNetworkConnectionStatistics = (WifiNetworkConnectionStatistics)this.untrustedNetworkHistory.get(str);
      paramParcel.writeString(str);
      paramParcel.writeInt(localWifiNetworkConnectionStatistics.numConnection);
      paramParcel.writeInt(localWifiNetworkConnectionStatistics.numUsage);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiConnectionStatistics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */