package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UsbDevice
  implements Parcelable
{
  public static final Parcelable.Creator<UsbDevice> CREATOR = new Parcelable.Creator()
  {
    public UsbDevice createFromParcel(Parcel paramAnonymousParcel)
    {
      Object localObject = paramAnonymousParcel.readString();
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      int k = paramAnonymousParcel.readInt();
      int m = paramAnonymousParcel.readInt();
      int n = paramAnonymousParcel.readInt();
      String str1 = paramAnonymousParcel.readString();
      String str2 = paramAnonymousParcel.readString();
      String str3 = paramAnonymousParcel.readString();
      String str4 = paramAnonymousParcel.readString();
      paramAnonymousParcel = paramAnonymousParcel.readParcelableArray(UsbInterface.class.getClassLoader());
      localObject = new UsbDevice((String)localObject, i, j, k, m, n, str1, str2, str3, str4);
      ((UsbDevice)localObject).setConfigurations(paramAnonymousParcel);
      return (UsbDevice)localObject;
    }
    
    public UsbDevice[] newArray(int paramAnonymousInt)
    {
      return new UsbDevice[paramAnonymousInt];
    }
  };
  private static final boolean DEBUG = false;
  private static final String TAG = "UsbDevice";
  private final int mClass;
  private Parcelable[] mConfigurations;
  private UsbInterface[] mInterfaces;
  private final String mManufacturerName;
  private final String mName;
  private final int mProductId;
  private final String mProductName;
  private final int mProtocol;
  private final String mSerialNumber;
  private final int mSubclass;
  private final int mVendorId;
  private final String mVersion;
  
  public UsbDevice(String paramString1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    this.mName = paramString1;
    this.mVendorId = paramInt1;
    this.mProductId = paramInt2;
    this.mClass = paramInt3;
    this.mSubclass = paramInt4;
    this.mProtocol = paramInt5;
    this.mManufacturerName = paramString2;
    this.mProductName = paramString3;
    this.mVersion = paramString4;
    this.mSerialNumber = paramString5;
  }
  
  public static int getDeviceId(String paramString)
  {
    return native_get_device_id(paramString);
  }
  
  public static String getDeviceName(int paramInt)
  {
    return native_get_device_name(paramInt);
  }
  
  private UsbInterface[] getInterfaceList()
  {
    if (this.mInterfaces == null)
    {
      int m = this.mConfigurations.length;
      int j = 0;
      int i = 0;
      while (i < m)
      {
        j += ((UsbConfiguration)this.mConfigurations[i]).getInterfaceCount();
        i += 1;
      }
      this.mInterfaces = new UsbInterface[j];
      i = 0;
      j = 0;
      while (j < m)
      {
        UsbConfiguration localUsbConfiguration = (UsbConfiguration)this.mConfigurations[j];
        int n = localUsbConfiguration.getInterfaceCount();
        int k = 0;
        while (k < n)
        {
          this.mInterfaces[i] = localUsbConfiguration.getInterface(k);
          k += 1;
          i += 1;
        }
        j += 1;
      }
    }
    return this.mInterfaces;
  }
  
  private static native int native_get_device_id(String paramString);
  
  private static native String native_get_device_name(int paramInt);
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof UsbDevice)) {
      return ((UsbDevice)paramObject).mName.equals(this.mName);
    }
    if ((paramObject instanceof String)) {
      return ((String)paramObject).equals(this.mName);
    }
    return false;
  }
  
  public UsbConfiguration getConfiguration(int paramInt)
  {
    return (UsbConfiguration)this.mConfigurations[paramInt];
  }
  
  public int getConfigurationCount()
  {
    return this.mConfigurations.length;
  }
  
  public int getDeviceClass()
  {
    return this.mClass;
  }
  
  public int getDeviceId()
  {
    return getDeviceId(this.mName);
  }
  
  public String getDeviceName()
  {
    return this.mName;
  }
  
  public int getDeviceProtocol()
  {
    return this.mProtocol;
  }
  
  public int getDeviceSubclass()
  {
    return this.mSubclass;
  }
  
  public UsbInterface getInterface(int paramInt)
  {
    return getInterfaceList()[paramInt];
  }
  
  public int getInterfaceCount()
  {
    return getInterfaceList().length;
  }
  
  public String getManufacturerName()
  {
    return this.mManufacturerName;
  }
  
  public int getProductId()
  {
    return this.mProductId;
  }
  
  public String getProductName()
  {
    return this.mProductName;
  }
  
  public String getSerialNumber()
  {
    return this.mSerialNumber;
  }
  
  public int getVendorId()
  {
    return this.mVendorId;
  }
  
  public String getVersion()
  {
    return this.mVersion;
  }
  
  public int hashCode()
  {
    return this.mName.hashCode();
  }
  
  public void setConfigurations(Parcelable[] paramArrayOfParcelable)
  {
    this.mConfigurations = paramArrayOfParcelable;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("UsbDevice[mName=" + this.mName + ",mVendorId=" + this.mVendorId + ",mProductId=" + this.mProductId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mManufacturerName=" + this.mManufacturerName + ",mProductName=" + this.mProductName + ",mVersion=" + this.mVersion + ",mSerialNumber=" + this.mSerialNumber + ",mConfigurations=[");
    int i = 0;
    while (i < this.mConfigurations.length)
    {
      localStringBuilder.append("\n");
      localStringBuilder.append(this.mConfigurations[i].toString());
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mVendorId);
    paramParcel.writeInt(this.mProductId);
    paramParcel.writeInt(this.mClass);
    paramParcel.writeInt(this.mSubclass);
    paramParcel.writeInt(this.mProtocol);
    paramParcel.writeString(this.mManufacturerName);
    paramParcel.writeString(this.mProductName);
    paramParcel.writeString(this.mVersion);
    paramParcel.writeString(this.mSerialNumber);
    paramParcel.writeParcelableArray(this.mConfigurations, 0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */