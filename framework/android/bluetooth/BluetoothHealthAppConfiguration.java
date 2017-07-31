package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class BluetoothHealthAppConfiguration
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothHealthAppConfiguration> CREATOR = new Parcelable.Creator()
  {
    public BluetoothHealthAppConfiguration createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothHealthAppConfiguration(paramAnonymousParcel.readString(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public BluetoothHealthAppConfiguration[] newArray(int paramAnonymousInt)
    {
      return new BluetoothHealthAppConfiguration[paramAnonymousInt];
    }
  };
  private final int mChannelType;
  private final int mDataType;
  private final String mName;
  private final int mRole;
  
  BluetoothHealthAppConfiguration(String paramString, int paramInt)
  {
    this.mName = paramString;
    this.mDataType = paramInt;
    this.mRole = 2;
    this.mChannelType = 12;
  }
  
  BluetoothHealthAppConfiguration(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mName = paramString;
    this.mDataType = paramInt1;
    this.mRole = paramInt2;
    this.mChannelType = paramInt3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof BluetoothHealthAppConfiguration))
    {
      paramObject = (BluetoothHealthAppConfiguration)paramObject;
      if (this.mName == null) {
        return false;
      }
      boolean bool1 = bool2;
      if (this.mName.equals(((BluetoothHealthAppConfiguration)paramObject).getName()))
      {
        bool1 = bool2;
        if (this.mDataType == ((BluetoothHealthAppConfiguration)paramObject).getDataType())
        {
          bool1 = bool2;
          if (this.mRole == ((BluetoothHealthAppConfiguration)paramObject).getRole())
          {
            bool1 = bool2;
            if (this.mChannelType == ((BluetoothHealthAppConfiguration)paramObject).getChannelType()) {
              bool1 = true;
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public int getChannelType()
  {
    return this.mChannelType;
  }
  
  public int getDataType()
  {
    return this.mDataType;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getRole()
  {
    return this.mRole;
  }
  
  public int hashCode()
  {
    if (this.mName != null) {}
    for (int i = this.mName.hashCode();; i = 0) {
      return (((i + 527) * 31 + this.mDataType) * 31 + this.mRole) * 31 + this.mChannelType;
    }
  }
  
  public String toString()
  {
    return "BluetoothHealthAppConfiguration [mName = " + this.mName + ",mDataType = " + this.mDataType + ", mRole = " + this.mRole + ",mChannelType = " + this.mChannelType + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mDataType);
    paramParcel.writeInt(this.mRole);
    paramParcel.writeInt(this.mChannelType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothHealthAppConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */