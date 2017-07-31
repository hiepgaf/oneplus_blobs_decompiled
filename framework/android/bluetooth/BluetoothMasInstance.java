package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class BluetoothMasInstance
  implements Parcelable
{
  public static final Parcelable.Creator<BluetoothMasInstance> CREATOR = new Parcelable.Creator()
  {
    public BluetoothMasInstance createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BluetoothMasInstance(paramAnonymousParcel.readInt(), paramAnonymousParcel.readString(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public BluetoothMasInstance[] newArray(int paramAnonymousInt)
    {
      return new BluetoothMasInstance[paramAnonymousInt];
    }
  };
  private final int mChannel;
  private final int mId;
  private final int mMsgTypes;
  private final String mName;
  
  public BluetoothMasInstance(int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    this.mId = paramInt1;
    this.mName = paramString;
    this.mChannel = paramInt2;
    this.mMsgTypes = paramInt3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof BluetoothMasInstance))
    {
      if (this.mId == ((BluetoothMasInstance)paramObject).mId) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public int getChannel()
  {
    return this.mChannel;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getMsgTypes()
  {
    return this.mMsgTypes;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int hashCode()
  {
    return this.mId + (this.mChannel << 8) + (this.mMsgTypes << 16);
  }
  
  public boolean msgSupported(int paramInt)
  {
    boolean bool = false;
    if ((this.mMsgTypes & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    return Integer.toString(this.mId) + ":" + this.mName + ":" + this.mChannel + ":" + Integer.toHexString(this.mMsgTypes);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mId);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mChannel);
    paramParcel.writeInt(this.mMsgTypes);
  }
  
  public static final class MessageType
  {
    public static final int EMAIL = 1;
    public static final int MMS = 8;
    public static final int SMS_CDMA = 4;
    public static final int SMS_GSM = 2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothMasInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */