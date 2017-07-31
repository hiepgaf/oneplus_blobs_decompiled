package android.net.metrics;

import android.net.NetworkCapabilities;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class DefaultNetworkEvent
  implements Parcelable
{
  public static final Parcelable.Creator<DefaultNetworkEvent> CREATOR = new Parcelable.Creator()
  {
    public DefaultNetworkEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DefaultNetworkEvent(paramAnonymousParcel, null);
    }
    
    public DefaultNetworkEvent[] newArray(int paramAnonymousInt)
    {
      return new DefaultNetworkEvent[paramAnonymousInt];
    }
  };
  public final int netId;
  public final boolean prevIPv4;
  public final boolean prevIPv6;
  public final int prevNetId;
  public final int[] transportTypes;
  
  public DefaultNetworkEvent(int paramInt1, int[] paramArrayOfInt, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.netId = paramInt1;
    this.transportTypes = paramArrayOfInt;
    this.prevNetId = paramInt2;
    this.prevIPv4 = paramBoolean1;
    this.prevIPv6 = paramBoolean2;
  }
  
  private DefaultNetworkEvent(Parcel paramParcel)
  {
    this.netId = paramParcel.readInt();
    this.transportTypes = paramParcel.createIntArray();
    this.prevNetId = paramParcel.readInt();
    if (paramParcel.readByte() > 0)
    {
      bool1 = true;
      this.prevIPv4 = bool1;
      if (paramParcel.readByte() <= 0) {
        break label64;
      }
    }
    label64:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.prevIPv6 = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  private String ipSupport()
  {
    if ((this.prevIPv4) && (this.prevIPv6)) {
      return "DUAL";
    }
    if (this.prevIPv6) {
      return "IPv6";
    }
    if (this.prevIPv4) {
      return "IPv4";
    }
    return "NONE";
  }
  
  public static void logEvent(int paramInt1, int[] paramArrayOfInt, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {}
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    Object localObject2 = String.valueOf(this.prevNetId);
    String str = String.valueOf(this.netId);
    Object localObject1 = localObject2;
    if (this.prevNetId != 0) {
      localObject1 = (String)localObject2 + ":" + ipSupport();
    }
    localObject2 = str;
    if (this.netId != 0) {
      localObject2 = str + ":" + NetworkCapabilities.transportNamesOf(this.transportTypes);
    }
    return String.format("DefaultNetworkEvent(%s -> %s)", new Object[] { localObject1, localObject2 });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    byte b2 = 1;
    paramParcel.writeInt(this.netId);
    paramParcel.writeIntArray(this.transportTypes);
    paramParcel.writeInt(this.prevNetId);
    if (this.prevIPv4)
    {
      b1 = 1;
      paramParcel.writeByte(b1);
      if (!this.prevIPv6) {
        break label62;
      }
    }
    label62:
    for (byte b1 = b2;; b1 = 0)
    {
      paramParcel.writeByte(b1);
      return;
      b1 = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/metrics/DefaultNetworkEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */