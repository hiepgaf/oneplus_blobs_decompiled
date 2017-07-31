package android.bluetooth.le;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class AdvertiseSettings
  implements Parcelable
{
  public static final int ADVERTISE_MODE_BALANCED = 1;
  public static final int ADVERTISE_MODE_LOW_LATENCY = 2;
  public static final int ADVERTISE_MODE_LOW_POWER = 0;
  public static final int ADVERTISE_TX_POWER_HIGH = 3;
  public static final int ADVERTISE_TX_POWER_LOW = 1;
  public static final int ADVERTISE_TX_POWER_MEDIUM = 2;
  public static final int ADVERTISE_TX_POWER_ULTRA_LOW = 0;
  public static final Parcelable.Creator<AdvertiseSettings> CREATOR = new Parcelable.Creator()
  {
    public AdvertiseSettings createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AdvertiseSettings(paramAnonymousParcel, null);
    }
    
    public AdvertiseSettings[] newArray(int paramAnonymousInt)
    {
      return new AdvertiseSettings[paramAnonymousInt];
    }
  };
  private static final int LIMITED_ADVERTISING_MAX_MILLIS = 180000;
  private final boolean mAdvertiseConnectable;
  private final int mAdvertiseMode;
  private final int mAdvertiseTimeoutMillis;
  private final int mAdvertiseTxPowerLevel;
  
  private AdvertiseSettings(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    this.mAdvertiseMode = paramInt1;
    this.mAdvertiseTxPowerLevel = paramInt2;
    this.mAdvertiseConnectable = paramBoolean;
    this.mAdvertiseTimeoutMillis = paramInt3;
  }
  
  private AdvertiseSettings(Parcel paramParcel)
  {
    this.mAdvertiseMode = paramParcel.readInt();
    this.mAdvertiseTxPowerLevel = paramParcel.readInt();
    if (paramParcel.readInt() != 0) {
      bool = true;
    }
    this.mAdvertiseConnectable = bool;
    this.mAdvertiseTimeoutMillis = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getMode()
  {
    return this.mAdvertiseMode;
  }
  
  public int getTimeout()
  {
    return this.mAdvertiseTimeoutMillis;
  }
  
  public int getTxPowerLevel()
  {
    return this.mAdvertiseTxPowerLevel;
  }
  
  public boolean isConnectable()
  {
    return this.mAdvertiseConnectable;
  }
  
  public String toString()
  {
    return "Settings [mAdvertiseMode=" + this.mAdvertiseMode + ", mAdvertiseTxPowerLevel=" + this.mAdvertiseTxPowerLevel + ", mAdvertiseConnectable=" + this.mAdvertiseConnectable + ", mAdvertiseTimeoutMillis=" + this.mAdvertiseTimeoutMillis + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mAdvertiseMode);
    paramParcel.writeInt(this.mAdvertiseTxPowerLevel);
    if (this.mAdvertiseConnectable) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.mAdvertiseTimeoutMillis);
      return;
    }
  }
  
  public static final class Builder
  {
    private boolean mConnectable = true;
    private int mMode = 0;
    private int mTimeoutMillis = 0;
    private int mTxPowerLevel = 2;
    
    public AdvertiseSettings build()
    {
      return new AdvertiseSettings(this.mMode, this.mTxPowerLevel, this.mConnectable, this.mTimeoutMillis, null);
    }
    
    public Builder setAdvertiseMode(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 2)) {
        throw new IllegalArgumentException("unknown mode " + paramInt);
      }
      this.mMode = paramInt;
      return this;
    }
    
    public Builder setConnectable(boolean paramBoolean)
    {
      this.mConnectable = paramBoolean;
      return this;
    }
    
    public Builder setTimeout(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 180000)) {
        throw new IllegalArgumentException("timeoutMillis invalid (must be 0-180000 milliseconds)");
      }
      this.mTimeoutMillis = paramInt;
      return this;
    }
    
    public Builder setTxPowerLevel(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 3)) {
        throw new IllegalArgumentException("unknown tx power level " + paramInt);
      }
      this.mTxPowerLevel = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/AdvertiseSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */