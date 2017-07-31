package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;

public final class ValidationProbeEvent
  implements Parcelable
{
  public static final Parcelable.Creator<ValidationProbeEvent> CREATOR = new Parcelable.Creator()
  {
    public ValidationProbeEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ValidationProbeEvent(paramAnonymousParcel, null);
    }
    
    public ValidationProbeEvent[] newArray(int paramAnonymousInt)
    {
      return new ValidationProbeEvent[paramAnonymousInt];
    }
  };
  public static final int DNS_FAILURE = 0;
  public static final int DNS_SUCCESS = 1;
  public static final int PROBE_DNS = 0;
  public static final int PROBE_FALLBACK = 4;
  public static final int PROBE_HTTP = 1;
  public static final int PROBE_HTTPS = 2;
  public static final int PROBE_PAC = 3;
  public final long durationMs;
  public final int netId;
  public final int probeType;
  public final int returnCode;
  
  public ValidationProbeEvent(int paramInt1, long paramLong, int paramInt2, int paramInt3)
  {
    this.netId = paramInt1;
    this.durationMs = paramLong;
    this.probeType = paramInt2;
    this.returnCode = paramInt3;
  }
  
  private ValidationProbeEvent(Parcel paramParcel)
  {
    this.netId = paramParcel.readInt();
    this.durationMs = paramParcel.readLong();
    this.probeType = paramParcel.readInt();
    this.returnCode = paramParcel.readInt();
  }
  
  public static String getProbeName(int paramInt)
  {
    return (String)Decoder.constants.get(paramInt, "PROBE_???");
  }
  
  public static void logEvent(int paramInt1, long paramLong, int paramInt2, int paramInt3) {}
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return String.format("ValidationProbeEvent(%d, %s:%d, %dms)", new Object[] { Integer.valueOf(this.netId), getProbeName(this.probeType), Integer.valueOf(this.returnCode), Long.valueOf(this.durationMs) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.netId);
    paramParcel.writeLong(this.durationMs);
    paramParcel.writeInt(this.probeType);
    paramParcel.writeInt(this.returnCode);
  }
  
  static final class Decoder
  {
    static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[] { ValidationProbeEvent.class }, new String[] { "PROBE_" });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/ValidationProbeEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */