package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UsbEndpoint
  implements Parcelable
{
  public static final Parcelable.Creator<UsbEndpoint> CREATOR = new Parcelable.Creator()
  {
    public UsbEndpoint createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UsbEndpoint(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public UsbEndpoint[] newArray(int paramAnonymousInt)
    {
      return new UsbEndpoint[paramAnonymousInt];
    }
  };
  private final int mAddress;
  private final int mAttributes;
  private final int mInterval;
  private final int mMaxPacketSize;
  
  public UsbEndpoint(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mAddress = paramInt1;
    this.mAttributes = paramInt2;
    this.mMaxPacketSize = paramInt3;
    this.mInterval = paramInt4;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAddress()
  {
    return this.mAddress;
  }
  
  public int getAttributes()
  {
    return this.mAttributes;
  }
  
  public int getDirection()
  {
    return this.mAddress & 0x80;
  }
  
  public int getEndpointNumber()
  {
    return this.mAddress & 0xF;
  }
  
  public int getInterval()
  {
    return this.mInterval;
  }
  
  public int getMaxPacketSize()
  {
    return this.mMaxPacketSize;
  }
  
  public int getType()
  {
    return this.mAttributes & 0x3;
  }
  
  public String toString()
  {
    return "UsbEndpoint[mAddress=" + this.mAddress + ",mAttributes=" + this.mAttributes + ",mMaxPacketSize=" + this.mMaxPacketSize + ",mInterval=" + this.mInterval + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mAddress);
    paramParcel.writeInt(this.mAttributes);
    paramParcel.writeInt(this.mMaxPacketSize);
    paramParcel.writeInt(this.mInterval);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbEndpoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */