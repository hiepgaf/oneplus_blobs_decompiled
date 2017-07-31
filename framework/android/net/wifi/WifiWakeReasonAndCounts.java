package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class WifiWakeReasonAndCounts
  implements Parcelable
{
  public static final Parcelable.Creator<WifiWakeReasonAndCounts> CREATOR = new Parcelable.Creator()
  {
    public WifiWakeReasonAndCounts createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiWakeReasonAndCounts localWifiWakeReasonAndCounts = new WifiWakeReasonAndCounts();
      localWifiWakeReasonAndCounts.totalCmdEventWake = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.totalDriverFwLocalWake = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.totalRxDataWake = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.rxUnicast = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.rxMulticast = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.rxBroadcast = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.icmp = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.icmp6 = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.icmp6Ra = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.icmp6Na = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.icmp6Ns = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.ipv4RxMulticast = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.ipv6Multicast = paramAnonymousParcel.readInt();
      localWifiWakeReasonAndCounts.otherRxMulticast = paramAnonymousParcel.readInt();
      paramAnonymousParcel.readIntArray(localWifiWakeReasonAndCounts.cmdEventWakeCntArray);
      paramAnonymousParcel.readIntArray(localWifiWakeReasonAndCounts.driverFWLocalWakeCntArray);
      return localWifiWakeReasonAndCounts;
    }
    
    public WifiWakeReasonAndCounts[] newArray(int paramAnonymousInt)
    {
      return new WifiWakeReasonAndCounts[paramAnonymousInt];
    }
  };
  private static final String TAG = "WifiWakeReasonAndCounts";
  public int[] cmdEventWakeCntArray;
  public int[] driverFWLocalWakeCntArray;
  public int icmp;
  public int icmp6;
  public int icmp6Na;
  public int icmp6Ns;
  public int icmp6Ra;
  public int ipv4RxMulticast;
  public int ipv6Multicast;
  public int otherRxMulticast;
  public int rxBroadcast;
  public int rxMulticast;
  public int rxUnicast;
  public int totalCmdEventWake;
  public int totalDriverFwLocalWake;
  public int totalRxDataWake;
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(" totalCmdEventWake ").append(this.totalCmdEventWake);
    localStringBuffer.append(" totalDriverFwLocalWake ").append(this.totalDriverFwLocalWake);
    localStringBuffer.append(" totalRxDataWake ").append(this.totalRxDataWake);
    localStringBuffer.append(" rxUnicast ").append(this.rxUnicast);
    localStringBuffer.append(" rxMulticast ").append(this.rxMulticast);
    localStringBuffer.append(" rxBroadcast ").append(this.rxBroadcast);
    localStringBuffer.append(" icmp ").append(this.icmp);
    localStringBuffer.append(" icmp6 ").append(this.icmp6);
    localStringBuffer.append(" icmp6Ra ").append(this.icmp6Ra);
    localStringBuffer.append(" icmp6Na ").append(this.icmp6Na);
    localStringBuffer.append(" icmp6Ns ").append(this.icmp6Ns);
    localStringBuffer.append(" ipv4RxMulticast ").append(this.ipv4RxMulticast);
    localStringBuffer.append(" ipv6Multicast ").append(this.ipv6Multicast);
    localStringBuffer.append(" otherRxMulticast ").append(this.otherRxMulticast);
    int i = 0;
    while (i < this.cmdEventWakeCntArray.length)
    {
      localStringBuffer.append(" cmdEventWakeCntArray[" + i + "] " + this.cmdEventWakeCntArray[i]);
      i += 1;
    }
    i = 0;
    while (i < this.driverFWLocalWakeCntArray.length)
    {
      localStringBuffer.append(" driverFWLocalWakeCntArray[" + i + "] " + this.driverFWLocalWakeCntArray[i]);
      i += 1;
    }
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.totalCmdEventWake);
    paramParcel.writeInt(this.totalDriverFwLocalWake);
    paramParcel.writeInt(this.totalRxDataWake);
    paramParcel.writeInt(this.rxUnicast);
    paramParcel.writeInt(this.rxMulticast);
    paramParcel.writeInt(this.rxBroadcast);
    paramParcel.writeInt(this.icmp);
    paramParcel.writeInt(this.icmp6);
    paramParcel.writeInt(this.icmp6Ra);
    paramParcel.writeInt(this.icmp6Na);
    paramParcel.writeInt(this.icmp6Ns);
    paramParcel.writeInt(this.ipv4RxMulticast);
    paramParcel.writeInt(this.ipv6Multicast);
    paramParcel.writeInt(this.otherRxMulticast);
    paramParcel.writeIntArray(this.cmdEventWakeCntArray);
    paramParcel.writeIntArray(this.driverFWLocalWakeCntArray);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiWakeReasonAndCounts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */