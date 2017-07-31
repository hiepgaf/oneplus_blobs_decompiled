package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class WpsInfo
  implements Parcelable
{
  public static final Parcelable.Creator<WpsInfo> CREATOR = new Parcelable.Creator()
  {
    public WpsInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      WpsInfo localWpsInfo = new WpsInfo();
      localWpsInfo.setup = paramAnonymousParcel.readInt();
      localWpsInfo.BSSID = paramAnonymousParcel.readString();
      localWpsInfo.pin = paramAnonymousParcel.readString();
      return localWpsInfo;
    }
    
    public WpsInfo[] newArray(int paramAnonymousInt)
    {
      return new WpsInfo[paramAnonymousInt];
    }
  };
  public static final int DISPLAY = 1;
  public static final int INVALID = 4;
  public static final int KEYPAD = 2;
  public static final int LABEL = 3;
  public static final int PBC = 0;
  public String BSSID;
  public String pin;
  public int setup;
  
  public WpsInfo()
  {
    this.setup = 4;
    this.BSSID = null;
    this.pin = null;
  }
  
  public WpsInfo(WpsInfo paramWpsInfo)
  {
    if (paramWpsInfo != null)
    {
      this.setup = paramWpsInfo.setup;
      this.BSSID = paramWpsInfo.BSSID;
      this.pin = paramWpsInfo.pin;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(" setup: ").append(this.setup);
    localStringBuffer.append('\n');
    localStringBuffer.append(" BSSID: ").append(this.BSSID);
    localStringBuffer.append('\n');
    localStringBuffer.append(" pin: ").append(this.pin);
    localStringBuffer.append('\n');
    return localStringBuffer.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.setup);
    paramParcel.writeString(this.BSSID);
    paramParcel.writeString(this.pin);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WpsInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */