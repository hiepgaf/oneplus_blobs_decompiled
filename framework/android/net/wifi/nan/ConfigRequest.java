package android.net.wifi.nan;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ConfigRequest
  implements Parcelable
{
  public static final int CLUSTER_ID_MAX = 65535;
  public static final int CLUSTER_ID_MIN = 0;
  public static final Parcelable.Creator<ConfigRequest> CREATOR = new Parcelable.Creator()
  {
    public ConfigRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      if (paramAnonymousParcel.readInt() != 0) {}
      for (boolean bool = true;; bool = false) {
        return new ConfigRequest(bool, paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), null);
      }
    }
    
    public ConfigRequest[] newArray(int paramAnonymousInt)
    {
      return new ConfigRequest[paramAnonymousInt];
    }
  };
  public final int mClusterHigh;
  public final int mClusterLow;
  public final int mMasterPreference;
  public final boolean mSupport5gBand;
  
  private ConfigRequest(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSupport5gBand = paramBoolean;
    this.mMasterPreference = paramInt1;
    this.mClusterLow = paramInt2;
    this.mClusterHigh = paramInt3;
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
    if (!(paramObject instanceof ConfigRequest)) {
      return false;
    }
    if ((this.mSupport5gBand == ((ConfigRequest)paramObject).mSupport5gBand) && (this.mMasterPreference == ((ConfigRequest)paramObject).mMasterPreference) && (this.mClusterLow == ((ConfigRequest)paramObject).mClusterLow)) {
      return this.mClusterHigh == ((ConfigRequest)paramObject).mClusterHigh;
    }
    return false;
  }
  
  public int hashCode()
  {
    if (this.mSupport5gBand) {}
    for (int i = 1;; i = 0) {
      return (((i + 527) * 31 + this.mMasterPreference) * 31 + this.mClusterLow) * 31 + this.mClusterHigh;
    }
  }
  
  public String toString()
  {
    return "ConfigRequest [mSupport5gBand=" + this.mSupport5gBand + ", mMasterPreference=" + this.mMasterPreference + ", mClusterLow=" + this.mClusterLow + ", mClusterHigh=" + this.mClusterHigh + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mSupport5gBand) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.mMasterPreference);
      paramParcel.writeInt(this.mClusterLow);
      paramParcel.writeInt(this.mClusterHigh);
      return;
    }
  }
  
  public static final class Builder
  {
    private int mClusterHigh = 65535;
    private int mClusterLow = 0;
    private int mMasterPreference = 0;
    private boolean mSupport5gBand = false;
    
    public ConfigRequest build()
    {
      if (this.mClusterLow > this.mClusterHigh) {
        throw new IllegalArgumentException("Invalid argument combination - must have Cluster Low <= Cluster High");
      }
      return new ConfigRequest(this.mSupport5gBand, this.mMasterPreference, this.mClusterLow, this.mClusterHigh, null);
    }
    
    public Builder setClusterHigh(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("Cluster specification must be non-negative");
      }
      if (paramInt > 65535) {
        throw new IllegalArgumentException("Cluster specification must not exceed 0xFFFF");
      }
      this.mClusterHigh = paramInt;
      return this;
    }
    
    public Builder setClusterLow(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("Cluster specification must be non-negative");
      }
      if (paramInt > 65535) {
        throw new IllegalArgumentException("Cluster specification must not exceed 0xFFFF");
      }
      this.mClusterLow = paramInt;
      return this;
    }
    
    public Builder setMasterPreference(int paramInt)
    {
      if (paramInt < 0) {
        throw new IllegalArgumentException("Master Preference specification must be non-negative");
      }
      if ((paramInt == 1) || (paramInt == 255)) {}
      while (paramInt > 255) {
        throw new IllegalArgumentException("Master Preference specification must not exceed 255 or use 1 or 255 (reserved values)");
      }
      this.mMasterPreference = paramInt;
      return this;
    }
    
    public Builder setSupport5gBand(boolean paramBoolean)
    {
      this.mSupport5gBand = paramBoolean;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/ConfigRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */