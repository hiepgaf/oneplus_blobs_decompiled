package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.Objects;

public class RssiCurve
  implements Parcelable
{
  public static final Parcelable.Creator<RssiCurve> CREATOR = new Parcelable.Creator()
  {
    public RssiCurve createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RssiCurve(paramAnonymousParcel, null);
    }
    
    public RssiCurve[] newArray(int paramAnonymousInt)
    {
      return new RssiCurve[paramAnonymousInt];
    }
  };
  private static final int DEFAULT_ACTIVE_NETWORK_RSSI_BOOST = 25;
  public final int activeNetworkRssiBoost;
  public final int bucketWidth;
  public final byte[] rssiBuckets;
  public final int start;
  
  public RssiCurve(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    this(paramInt1, paramInt2, paramArrayOfByte, 25);
  }
  
  public RssiCurve(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
  {
    this.start = paramInt1;
    this.bucketWidth = paramInt2;
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      throw new IllegalArgumentException("rssiBuckets must be at least one element large.");
    }
    this.rssiBuckets = paramArrayOfByte;
    this.activeNetworkRssiBoost = paramInt3;
  }
  
  private RssiCurve(Parcel paramParcel)
  {
    this.start = paramParcel.readInt();
    this.bucketWidth = paramParcel.readInt();
    this.rssiBuckets = new byte[paramParcel.readInt()];
    paramParcel.readByteArray(this.rssiBuckets);
    this.activeNetworkRssiBoost = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (RssiCurve)paramObject;
    if ((this.start == ((RssiCurve)paramObject).start) && (this.bucketWidth == ((RssiCurve)paramObject).bucketWidth) && (Arrays.equals(this.rssiBuckets, ((RssiCurve)paramObject).rssiBuckets))) {
      return this.activeNetworkRssiBoost == ((RssiCurve)paramObject).activeNetworkRssiBoost;
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.start), Integer.valueOf(this.bucketWidth), this.rssiBuckets, Integer.valueOf(this.activeNetworkRssiBoost) });
  }
  
  public byte lookupScore(int paramInt)
  {
    return lookupScore(paramInt, false);
  }
  
  public byte lookupScore(int paramInt, boolean paramBoolean)
  {
    int i = paramInt;
    if (paramBoolean) {
      i = paramInt + this.activeNetworkRssiBoost;
    }
    i = (i - this.start) / this.bucketWidth;
    if (i < 0) {
      paramInt = 0;
    }
    for (;;)
    {
      return this.rssiBuckets[paramInt];
      paramInt = i;
      if (i > this.rssiBuckets.length - 1) {
        paramInt = this.rssiBuckets.length - 1;
      }
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("RssiCurve[start=").append(this.start).append(",bucketWidth=").append(this.bucketWidth).append(",activeNetworkRssiBoost=").append(this.activeNetworkRssiBoost);
    localStringBuilder.append(",buckets=");
    int i = 0;
    while (i < this.rssiBuckets.length)
    {
      localStringBuilder.append(this.rssiBuckets[i]);
      if (i < this.rssiBuckets.length - 1) {
        localStringBuilder.append(",");
      }
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.start);
    paramParcel.writeInt(this.bucketWidth);
    paramParcel.writeInt(this.rssiBuckets.length);
    paramParcel.writeByteArray(this.rssiBuckets);
    paramParcel.writeInt(this.activeNetworkRssiBoost);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/RssiCurve.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */