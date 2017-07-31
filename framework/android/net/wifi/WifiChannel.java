package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class WifiChannel
  implements Parcelable
{
  public static final Parcelable.Creator<WifiChannel> CREATOR = new Parcelable.Creator()
  {
    public WifiChannel createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool = false;
      WifiChannel localWifiChannel = new WifiChannel();
      localWifiChannel.freqMHz = paramAnonymousParcel.readInt();
      localWifiChannel.channelNum = paramAnonymousParcel.readInt();
      if (paramAnonymousParcel.readInt() != 0) {
        bool = true;
      }
      localWifiChannel.isDFS = bool;
      return localWifiChannel;
    }
    
    public WifiChannel[] newArray(int paramAnonymousInt)
    {
      return new WifiChannel[paramAnonymousInt];
    }
  };
  private static final int MAX_CHANNEL_NUM = 196;
  private static final int MAX_FREQ_MHZ = 5825;
  private static final int MIN_CHANNEL_NUM = 1;
  private static final int MIN_FREQ_MHZ = 2412;
  public int channelNum;
  public int freqMHz;
  public boolean isDFS;
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean isValid()
  {
    if ((this.freqMHz < 2412) || (this.freqMHz > 5825)) {
      return false;
    }
    return (this.channelNum >= 1) && (this.channelNum <= 196);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.freqMHz);
    paramParcel.writeInt(this.channelNum);
    if (this.isDFS) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */