package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UsbConfiguration
  implements Parcelable
{
  private static final int ATTR_REMOTE_WAKEUP = 32;
  private static final int ATTR_SELF_POWERED = 64;
  public static final Parcelable.Creator<UsbConfiguration> CREATOR = new Parcelable.Creator()
  {
    public UsbConfiguration createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      Object localObject = paramAnonymousParcel.readString();
      int j = paramAnonymousParcel.readInt();
      int k = paramAnonymousParcel.readInt();
      paramAnonymousParcel = paramAnonymousParcel.readParcelableArray(UsbInterface.class.getClassLoader());
      localObject = new UsbConfiguration(i, (String)localObject, j, k);
      ((UsbConfiguration)localObject).setInterfaces(paramAnonymousParcel);
      return (UsbConfiguration)localObject;
    }
    
    public UsbConfiguration[] newArray(int paramAnonymousInt)
    {
      return new UsbConfiguration[paramAnonymousInt];
    }
  };
  private final int mAttributes;
  private final int mId;
  private Parcelable[] mInterfaces;
  private final int mMaxPower;
  private final String mName;
  
  public UsbConfiguration(int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    this.mId = paramInt1;
    this.mName = paramString;
    this.mAttributes = paramInt2;
    this.mMaxPower = paramInt3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public UsbInterface getInterface(int paramInt)
  {
    return (UsbInterface)this.mInterfaces[paramInt];
  }
  
  public int getInterfaceCount()
  {
    return this.mInterfaces.length;
  }
  
  public int getMaxPower()
  {
    return this.mMaxPower * 2;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public boolean isRemoteWakeup()
  {
    boolean bool = false;
    if ((this.mAttributes & 0x20) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSelfPowered()
  {
    boolean bool = false;
    if ((this.mAttributes & 0x40) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void setInterfaces(Parcelable[] paramArrayOfParcelable)
  {
    this.mInterfaces = paramArrayOfParcelable;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("UsbConfiguration[mId=" + this.mId + ",mName=" + this.mName + ",mAttributes=" + this.mAttributes + ",mMaxPower=" + this.mMaxPower + ",mInterfaces=[");
    int i = 0;
    while (i < this.mInterfaces.length)
    {
      localStringBuilder.append("\n");
      localStringBuilder.append(this.mInterfaces[i].toString());
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mId);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mAttributes);
    paramParcel.writeInt(this.mMaxPower);
    paramParcel.writeParcelableArray(this.mInterfaces, 0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */