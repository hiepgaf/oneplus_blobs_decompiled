package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RssiPacketCountInfo
  implements Parcelable
{
  public static final Parcelable.Creator<RssiPacketCountInfo> CREATOR = new Parcelable.Creator()
  {
    public RssiPacketCountInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RssiPacketCountInfo(paramAnonymousParcel, null);
    }
    
    public RssiPacketCountInfo[] newArray(int paramAnonymousInt)
    {
      return new RssiPacketCountInfo[paramAnonymousInt];
    }
  };
  public int rssi;
  public int rxgood;
  public int txbad;
  public int txgood;
  
  public RssiPacketCountInfo()
  {
    this.rxgood = 0;
    this.txbad = 0;
    this.txgood = 0;
    this.rssi = 0;
  }
  
  private RssiPacketCountInfo(Parcel paramParcel)
  {
    this.rssi = paramParcel.readInt();
    this.txgood = paramParcel.readInt();
    this.txbad = paramParcel.readInt();
    this.rxgood = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.rssi);
    paramParcel.writeInt(this.txgood);
    paramParcel.writeInt(this.txbad);
    paramParcel.writeInt(this.rxgood);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/RssiPacketCountInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */