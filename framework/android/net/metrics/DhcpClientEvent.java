package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class DhcpClientEvent
  implements Parcelable
{
  public static final Parcelable.Creator<DhcpClientEvent> CREATOR = new Parcelable.Creator()
  {
    public DhcpClientEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DhcpClientEvent(paramAnonymousParcel, null);
    }
    
    public DhcpClientEvent[] newArray(int paramAnonymousInt)
    {
      return new DhcpClientEvent[paramAnonymousInt];
    }
  };
  public static final String INITIAL_BOUND = "InitialBoundState";
  public static final String RENEWING_BOUND = "RenewingBoundState";
  public final int durationMs;
  public final String ifName;
  public final String msg;
  
  private DhcpClientEvent(Parcel paramParcel)
  {
    this.ifName = paramParcel.readString();
    this.msg = paramParcel.readString();
    this.durationMs = paramParcel.readInt();
  }
  
  public DhcpClientEvent(String paramString1, String paramString2, int paramInt)
  {
    this.ifName = paramString1;
    this.msg = paramString2;
    this.durationMs = paramInt;
  }
  
  public static void logStateEvent(String paramString1, String paramString2) {}
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return String.format("DhcpClientEvent(%s, %s, %dms)", new Object[] { this.ifName, this.msg, Integer.valueOf(this.durationMs) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.ifName);
    paramParcel.writeString(this.msg);
    paramParcel.writeInt(this.durationMs);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/DhcpClientEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */