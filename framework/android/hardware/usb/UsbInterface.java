package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UsbInterface
  implements Parcelable
{
  public static final Parcelable.Creator<UsbInterface> CREATOR = new Parcelable.Creator()
  {
    public UsbInterface createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      Object localObject = paramAnonymousParcel.readString();
      int k = paramAnonymousParcel.readInt();
      int m = paramAnonymousParcel.readInt();
      int n = paramAnonymousParcel.readInt();
      paramAnonymousParcel = paramAnonymousParcel.readParcelableArray(UsbEndpoint.class.getClassLoader());
      localObject = new UsbInterface(i, j, (String)localObject, k, m, n);
      ((UsbInterface)localObject).setEndpoints(paramAnonymousParcel);
      return (UsbInterface)localObject;
    }
    
    public UsbInterface[] newArray(int paramAnonymousInt)
    {
      return new UsbInterface[paramAnonymousInt];
    }
  };
  private final int mAlternateSetting;
  private final int mClass;
  private Parcelable[] mEndpoints;
  private final int mId;
  private final String mName;
  private final int mProtocol;
  private final int mSubclass;
  
  public UsbInterface(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5)
  {
    this.mId = paramInt1;
    this.mAlternateSetting = paramInt2;
    this.mName = paramString;
    this.mClass = paramInt3;
    this.mSubclass = paramInt4;
    this.mProtocol = paramInt5;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAlternateSetting()
  {
    return this.mAlternateSetting;
  }
  
  public UsbEndpoint getEndpoint(int paramInt)
  {
    return (UsbEndpoint)this.mEndpoints[paramInt];
  }
  
  public int getEndpointCount()
  {
    return this.mEndpoints.length;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getInterfaceClass()
  {
    return this.mClass;
  }
  
  public int getInterfaceProtocol()
  {
    return this.mProtocol;
  }
  
  public int getInterfaceSubclass()
  {
    return this.mSubclass;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public void setEndpoints(Parcelable[] paramArrayOfParcelable)
  {
    this.mEndpoints = paramArrayOfParcelable;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("UsbInterface[mId=" + this.mId + ",mAlternateSetting=" + this.mAlternateSetting + ",mName=" + this.mName + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mEndpoints=[");
    int i = 0;
    while (i < this.mEndpoints.length)
    {
      localStringBuilder.append("\n");
      localStringBuilder.append(this.mEndpoints[i].toString());
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mId);
    paramParcel.writeInt(this.mAlternateSetting);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mClass);
    paramParcel.writeInt(this.mSubclass);
    paramParcel.writeInt(this.mProtocol);
    paramParcel.writeParcelableArray(this.mEndpoints, 0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */