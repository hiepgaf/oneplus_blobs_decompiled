package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UsbAccessory
  implements Parcelable
{
  public static final Parcelable.Creator<UsbAccessory> CREATOR = new Parcelable.Creator()
  {
    public UsbAccessory createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UsbAccessory(paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readString());
    }
    
    public UsbAccessory[] newArray(int paramAnonymousInt)
    {
      return new UsbAccessory[paramAnonymousInt];
    }
  };
  public static final int DESCRIPTION_STRING = 2;
  public static final int MANUFACTURER_STRING = 0;
  public static final int MODEL_STRING = 1;
  public static final int SERIAL_STRING = 5;
  private static final String TAG = "UsbAccessory";
  public static final int URI_STRING = 4;
  public static final int VERSION_STRING = 3;
  private final String mDescription;
  private final String mManufacturer;
  private final String mModel;
  private final String mSerial;
  private final String mUri;
  private final String mVersion;
  
  public UsbAccessory(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    this.mManufacturer = paramString1;
    this.mModel = paramString2;
    this.mDescription = paramString3;
    this.mVersion = paramString4;
    this.mUri = paramString5;
    this.mSerial = paramString6;
  }
  
  public UsbAccessory(String[] paramArrayOfString)
  {
    this.mManufacturer = paramArrayOfString[0];
    this.mModel = paramArrayOfString[1];
    this.mDescription = paramArrayOfString[2];
    this.mVersion = paramArrayOfString[3];
    this.mUri = paramArrayOfString[4];
    this.mSerial = paramArrayOfString[5];
  }
  
  private static boolean compare(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return paramString2 == null;
    }
    return paramString1.equals(paramString2);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if ((paramObject instanceof UsbAccessory))
    {
      paramObject = (UsbAccessory)paramObject;
      boolean bool1 = bool2;
      if (compare(this.mManufacturer, ((UsbAccessory)paramObject).getManufacturer()))
      {
        bool1 = bool2;
        if (compare(this.mModel, ((UsbAccessory)paramObject).getModel()))
        {
          bool1 = bool2;
          if (compare(this.mDescription, ((UsbAccessory)paramObject).getDescription()))
          {
            bool1 = bool2;
            if (compare(this.mVersion, ((UsbAccessory)paramObject).getVersion()))
            {
              bool1 = bool2;
              if (compare(this.mUri, ((UsbAccessory)paramObject).getUri())) {
                bool1 = compare(this.mSerial, ((UsbAccessory)paramObject).getSerial());
              }
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public String getManufacturer()
  {
    return this.mManufacturer;
  }
  
  public String getModel()
  {
    return this.mModel;
  }
  
  public String getSerial()
  {
    return this.mSerial;
  }
  
  public String getUri()
  {
    return this.mUri;
  }
  
  public String getVersion()
  {
    return this.mVersion;
  }
  
  public int hashCode()
  {
    int i1 = 0;
    int i;
    int j;
    label21:
    int k;
    label30:
    int m;
    label40:
    int n;
    if (this.mManufacturer == null)
    {
      i = 0;
      if (this.mModel != null) {
        break label83;
      }
      j = 0;
      if (this.mDescription != null) {
        break label94;
      }
      k = 0;
      if (this.mVersion != null) {
        break label105;
      }
      m = 0;
      if (this.mUri != null) {
        break label117;
      }
      n = 0;
      label50:
      if (this.mSerial != null) {
        break label129;
      }
    }
    for (;;)
    {
      return n ^ j ^ i ^ k ^ m ^ i1;
      i = this.mManufacturer.hashCode();
      break;
      label83:
      j = this.mModel.hashCode();
      break label21;
      label94:
      k = this.mDescription.hashCode();
      break label30;
      label105:
      m = this.mVersion.hashCode();
      break label40;
      label117:
      n = this.mUri.hashCode();
      break label50;
      label129:
      i1 = this.mSerial.hashCode();
    }
  }
  
  public String toString()
  {
    return "UsbAccessory[mManufacturer=" + this.mManufacturer + ", mModel=" + this.mModel + ", mDescription=" + this.mDescription + ", mVersion=" + this.mVersion + ", mUri=" + this.mUri + ", mSerial=" + this.mSerial + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mManufacturer);
    paramParcel.writeString(this.mModel);
    paramParcel.writeString(this.mDescription);
    paramParcel.writeString(this.mVersion);
    paramParcel.writeString(this.mUri);
    paramParcel.writeString(this.mSerial);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbAccessory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */