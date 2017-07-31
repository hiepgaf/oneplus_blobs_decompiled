package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;

public final class IpManagerEvent
  implements Parcelable
{
  public static final int COMPLETE_LIFECYCLE = 3;
  public static final Parcelable.Creator<IpManagerEvent> CREATOR = new Parcelable.Creator()
  {
    public IpManagerEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new IpManagerEvent(paramAnonymousParcel, null);
    }
    
    public IpManagerEvent[] newArray(int paramAnonymousInt)
    {
      return new IpManagerEvent[paramAnonymousInt];
    }
  };
  public static final int ERROR_STARTING_IPREACHABILITYMONITOR = 6;
  public static final int ERROR_STARTING_IPV4 = 4;
  public static final int ERROR_STARTING_IPV6 = 5;
  public static final int PROVISIONING_FAIL = 2;
  public static final int PROVISIONING_OK = 1;
  public final long durationMs;
  public final int eventType;
  public final String ifName;
  
  private IpManagerEvent(Parcel paramParcel)
  {
    this.ifName = paramParcel.readString();
    this.eventType = paramParcel.readInt();
    this.durationMs = paramParcel.readLong();
  }
  
  public IpManagerEvent(String paramString, int paramInt, long paramLong)
  {
    this.ifName = paramString;
    this.eventType = paramInt;
    this.durationMs = paramLong;
  }
  
  public static void logEvent(int paramInt, String paramString, long paramLong) {}
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return String.format("IpManagerEvent(%s, %s, %dms)", new Object[] { this.ifName, Decoder.constants.get(this.eventType), Long.valueOf(this.durationMs) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.ifName);
    paramParcel.writeInt(this.eventType);
    paramParcel.writeLong(this.durationMs);
  }
  
  static final class Decoder
  {
    static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[] { IpManagerEvent.class }, new String[] { "PROVISIONING_", "COMPLETE_", "ERROR_" });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/IpManagerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */