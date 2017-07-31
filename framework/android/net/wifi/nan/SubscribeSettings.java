package android.net.wifi.nan;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SubscribeSettings
  implements Parcelable
{
  public static final Parcelable.Creator<SubscribeSettings> CREATOR = new Parcelable.Creator()
  {
    public SubscribeSettings createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SubscribeSettings(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), null);
    }
    
    public SubscribeSettings[] newArray(int paramAnonymousInt)
    {
      return new SubscribeSettings[paramAnonymousInt];
    }
  };
  public static final int SUBSCRIBE_TYPE_ACTIVE = 1;
  public static final int SUBSCRIBE_TYPE_PASSIVE = 0;
  public final int mSubscribeCount;
  public final int mSubscribeType;
  public final int mTtlSec;
  
  private SubscribeSettings(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSubscribeType = paramInt1;
    this.mSubscribeCount = paramInt2;
    this.mTtlSec = paramInt3;
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
    if (!(paramObject instanceof SubscribeSettings)) {
      return false;
    }
    if ((this.mSubscribeType == ((SubscribeSettings)paramObject).mSubscribeType) && (this.mSubscribeCount == ((SubscribeSettings)paramObject).mSubscribeCount)) {
      return this.mTtlSec == ((SubscribeSettings)paramObject).mTtlSec;
    }
    return false;
  }
  
  public int hashCode()
  {
    return ((this.mSubscribeType + 527) * 31 + this.mSubscribeCount) * 31 + this.mTtlSec;
  }
  
  public String toString()
  {
    return "SubscribeSettings [mSubscribeType=" + this.mSubscribeType + ", mSubscribeCount=" + this.mSubscribeCount + ", mTtlSec=" + this.mTtlSec + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSubscribeType);
    paramParcel.writeInt(this.mSubscribeCount);
    paramParcel.writeInt(this.mTtlSec);
  }
  
  public static final class Builder
  {
    int mSubscribeCount;
    int mSubscribeType;
    int mTtlSec;
    
    public SubscribeSettings build()
    {
      return new SubscribeSettings(this.mSubscribeType, this.mSubscribeCount, this.mTtlSec, null);
    }
    
    public Builder setSubscribeCount(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("Invalid subscribeCount - must be non-negative");
      }
      this.mSubscribeCount = paramInt;
      return this;
    }
    
    public Builder setSubscribeType(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 1)) {
        throw new IllegalArgumentException("Invalid subscribeType - " + paramInt);
      }
      this.mSubscribeType = paramInt;
      return this;
    }
    
    public Builder setTtlSec(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("Invalid ttlSec - must be non-negative");
      }
      this.mTtlSec = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/SubscribeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */