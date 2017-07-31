package android.net.wifi.nan;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PublishSettings
  implements Parcelable
{
  public static final Parcelable.Creator<PublishSettings> CREATOR = new Parcelable.Creator()
  {
    public PublishSettings createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PublishSettings(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), null);
    }
    
    public PublishSettings[] newArray(int paramAnonymousInt)
    {
      return new PublishSettings[paramAnonymousInt];
    }
  };
  public static final int PUBLISH_TYPE_SOLICITED = 1;
  public static final int PUBLISH_TYPE_UNSOLICITED = 0;
  public final int mPublishCount;
  public final int mPublishType;
  public final int mTtlSec;
  
  private PublishSettings(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mPublishType = paramInt1;
    this.mPublishCount = paramInt2;
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
    if (!(paramObject instanceof PublishSettings)) {
      return false;
    }
    if ((this.mPublishType == ((PublishSettings)paramObject).mPublishType) && (this.mPublishCount == ((PublishSettings)paramObject).mPublishCount)) {
      return this.mTtlSec == ((PublishSettings)paramObject).mTtlSec;
    }
    return false;
  }
  
  public int hashCode()
  {
    return ((this.mPublishType + 527) * 31 + this.mPublishCount) * 31 + this.mTtlSec;
  }
  
  public String toString()
  {
    return "PublishSettings [mPublishType=" + this.mPublishType + ", mPublishCount=" + this.mPublishCount + ", mTtlSec=" + this.mTtlSec + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mPublishType);
    paramParcel.writeInt(this.mPublishCount);
    paramParcel.writeInt(this.mTtlSec);
  }
  
  public static final class Builder
  {
    int mPublishCount;
    int mPublishType;
    int mTtlSec;
    
    public PublishSettings build()
    {
      return new PublishSettings(this.mPublishType, this.mPublishCount, this.mTtlSec, null);
    }
    
    public Builder setPublishCount(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("Invalid publishCount - must be non-negative");
      }
      this.mPublishCount = paramInt;
      return this;
    }
    
    public Builder setPublishType(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 1)) {
        throw new IllegalArgumentException("Invalid publishType - " + paramInt);
      }
      this.mPublishType = paramInt;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/PublishSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */