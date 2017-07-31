package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;

public final class NetworkEvent
  implements Parcelable
{
  public static final Parcelable.Creator<NetworkEvent> CREATOR = new Parcelable.Creator()
  {
    public NetworkEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NetworkEvent(paramAnonymousParcel, null);
    }
    
    public NetworkEvent[] newArray(int paramAnonymousInt)
    {
      return new NetworkEvent[paramAnonymousInt];
    }
  };
  public static final int NETWORK_CAPTIVE_PORTAL_FOUND = 4;
  public static final int NETWORK_CONNECTED = 1;
  public static final int NETWORK_DISCONNECTED = 7;
  public static final int NETWORK_LINGER = 5;
  public static final int NETWORK_UNLINGER = 6;
  public static final int NETWORK_VALIDATED = 2;
  public static final int NETWORK_VALIDATION_FAILED = 3;
  public final long durationMs;
  public final int eventType;
  public final int netId;
  
  public NetworkEvent(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, 0L);
  }
  
  public NetworkEvent(int paramInt1, int paramInt2, long paramLong)
  {
    this.netId = paramInt1;
    this.eventType = paramInt2;
    this.durationMs = paramLong;
  }
  
  private NetworkEvent(Parcel paramParcel)
  {
    this.netId = paramParcel.readInt();
    this.eventType = paramParcel.readInt();
    this.durationMs = paramParcel.readLong();
  }
  
  public static void logCaptivePortalFound(int paramInt, long paramLong) {}
  
  public static void logEvent(int paramInt1, int paramInt2) {}
  
  public static void logValidated(int paramInt, long paramLong) {}
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return String.format("NetworkEvent(%d, %s, %dms)", new Object[] { Integer.valueOf(this.netId), Decoder.constants.get(this.eventType), Long.valueOf(this.durationMs) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.netId);
    paramParcel.writeInt(this.eventType);
    paramParcel.writeLong(this.durationMs);
  }
  
  static final class Decoder
  {
    static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[] { NetworkEvent.class }, new String[] { "NETWORK_" });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/NetworkEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */