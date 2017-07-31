package android.net.metrics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.android.internal.util.MessageUtils;

public final class IpReachabilityEvent
  implements Parcelable
{
  public static final Parcelable.Creator<IpReachabilityEvent> CREATOR = new Parcelable.Creator()
  {
    public IpReachabilityEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new IpReachabilityEvent(paramAnonymousParcel, null);
    }
    
    public IpReachabilityEvent[] newArray(int paramAnonymousInt)
    {
      return new IpReachabilityEvent[paramAnonymousInt];
    }
  };
  public static final int NUD_FAILED = 512;
  public static final int NUD_FAILED_ORGANIC = 1024;
  public static final int PROBE = 256;
  public static final int PROVISIONING_LOST = 768;
  public static final int PROVISIONING_LOST_ORGANIC = 1280;
  public final int eventType;
  public final String ifName;
  
  private IpReachabilityEvent(Parcel paramParcel)
  {
    this.ifName = paramParcel.readString();
    this.eventType = paramParcel.readInt();
  }
  
  public IpReachabilityEvent(String paramString, int paramInt)
  {
    this.ifName = paramString;
    this.eventType = paramInt;
  }
  
  public static void logNudFailed(String paramString) {}
  
  public static void logProbeEvent(String paramString, int paramInt) {}
  
  public static void logProvisioningLost(String paramString) {}
  
  public static int nudFailureEventType(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      if (paramBoolean2) {
        return 768;
      }
      return 512;
    }
    if (paramBoolean2) {
      return 1280;
    }
    return 1024;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    int i = this.eventType;
    int j = this.eventType;
    String str = (String)Decoder.constants.get(i & 0xFF00);
    return String.format("IpReachabilityEvent(%s, %s:%02x)", new Object[] { this.ifName, str, Integer.valueOf(j & 0xFF) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.ifName);
    paramParcel.writeInt(this.eventType);
  }
  
  static final class Decoder
  {
    static final SparseArray<String> constants = MessageUtils.findMessageNames(new Class[] { IpReachabilityEvent.class }, new String[] { "PROBE", "PROVISIONING_", "NUD_" });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/IpReachabilityEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */