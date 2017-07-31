package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class DnsEvent
  implements Parcelable
{
  public static final Parcelable.Creator<DnsEvent> CREATOR = new Parcelable.Creator()
  {
    public DnsEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DnsEvent(paramAnonymousParcel, null);
    }
    
    public DnsEvent[] newArray(int paramAnonymousInt)
    {
      return new DnsEvent[paramAnonymousInt];
    }
  };
  public final byte[] eventTypes;
  public final int[] latenciesMs;
  public final int netId;
  public final byte[] returnCodes;
  
  public DnsEvent(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt)
  {
    this.netId = paramInt;
    this.eventTypes = paramArrayOfByte1;
    this.returnCodes = paramArrayOfByte2;
    this.latenciesMs = paramArrayOfInt;
  }
  
  private DnsEvent(Parcel paramParcel)
  {
    this.netId = paramParcel.readInt();
    this.eventTypes = paramParcel.createByteArray();
    this.returnCodes = paramParcel.createByteArray();
    this.latenciesMs = paramParcel.createIntArray();
  }
  
  public static void logEvent(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt) {}
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return String.format("DnsEvent(%d, %d events)", new Object[] { Integer.valueOf(this.netId), Integer.valueOf(this.eventTypes.length) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.netId);
    paramParcel.writeByteArray(this.eventTypes);
    paramParcel.writeByteArray(this.returnCodes);
    paramParcel.writeIntArray(this.latenciesMs);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/DnsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */