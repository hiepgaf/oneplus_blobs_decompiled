package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class BluetoothAudioConfig
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothAudioConfig> CREATOR = new Parcelable.Creator()
  {
    public BluetoothAudioConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothAudioConfig(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public BluetoothAudioConfig[] newArray(int paramAnonymousInt)
    {
      return new BluetoothAudioConfig[paramAnonymousInt];
    }
  };
  private final int mAudioFormat;
  private final int mChannelConfig;
  private final int mSampleRate;
  
  public BluetoothAudioConfig(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSampleRate = paramInt1;
    this.mChannelConfig = paramInt2;
    this.mAudioFormat = paramInt3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof BluetoothAudioConfig))
    {
      paramObject = (BluetoothAudioConfig)paramObject;
      boolean bool1 = bool2;
      if (((BluetoothAudioConfig)paramObject).mSampleRate == this.mSampleRate)
      {
        bool1 = bool2;
        if (((BluetoothAudioConfig)paramObject).mChannelConfig == this.mChannelConfig)
        {
          bool1 = bool2;
          if (((BluetoothAudioConfig)paramObject).mAudioFormat == this.mAudioFormat) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public int getAudioFormat()
  {
    return this.mAudioFormat;
  }
  
  public int getChannelConfig()
  {
    return this.mChannelConfig;
  }
  
  public int getSampleRate()
  {
    return this.mSampleRate;
  }
  
  public int hashCode()
  {
    return this.mSampleRate | this.mChannelConfig << 24 | this.mAudioFormat << 28;
  }
  
  public String toString()
  {
    return "{mSampleRate:" + this.mSampleRate + ",mChannelConfig:" + this.mChannelConfig + ",mAudioFormat:" + this.mAudioFormat + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSampleRate);
    paramParcel.writeInt(this.mChannelConfig);
    paramParcel.writeInt(this.mAudioFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothAudioConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */