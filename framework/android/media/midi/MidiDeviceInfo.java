package android.media.midi;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class MidiDeviceInfo
  implements Parcelable
{
  public static final Parcelable.Creator<MidiDeviceInfo> CREATOR = new Parcelable.Creator()
  {
    public MidiDeviceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      int j = paramAnonymousParcel.readInt();
      int k = paramAnonymousParcel.readInt();
      int m = paramAnonymousParcel.readInt();
      String[] arrayOfString1 = paramAnonymousParcel.createStringArray();
      String[] arrayOfString2 = paramAnonymousParcel.createStringArray();
      Bundle localBundle = paramAnonymousParcel.readBundle();
      if (paramAnonymousParcel.readInt() == 1) {}
      for (boolean bool = true;; bool = false) {
        return new MidiDeviceInfo(i, j, k, m, arrayOfString1, arrayOfString2, localBundle, bool);
      }
    }
    
    public MidiDeviceInfo[] newArray(int paramAnonymousInt)
    {
      return new MidiDeviceInfo[paramAnonymousInt];
    }
  };
  public static final String PROPERTY_ALSA_CARD = "alsa_card";
  public static final String PROPERTY_ALSA_DEVICE = "alsa_device";
  public static final String PROPERTY_BLUETOOTH_DEVICE = "bluetooth_device";
  public static final String PROPERTY_MANUFACTURER = "manufacturer";
  public static final String PROPERTY_NAME = "name";
  public static final String PROPERTY_PRODUCT = "product";
  public static final String PROPERTY_SERIAL_NUMBER = "serial_number";
  public static final String PROPERTY_SERVICE_INFO = "service_info";
  public static final String PROPERTY_USB_DEVICE = "usb_device";
  public static final String PROPERTY_VERSION = "version";
  private static final String TAG = "MidiDeviceInfo";
  public static final int TYPE_BLUETOOTH = 3;
  public static final int TYPE_USB = 1;
  public static final int TYPE_VIRTUAL = 2;
  private final int mId;
  private final int mInputPortCount;
  private final String[] mInputPortNames;
  private final boolean mIsPrivate;
  private final int mOutputPortCount;
  private final String[] mOutputPortNames;
  private final Bundle mProperties;
  private final int mType;
  
  public MidiDeviceInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String[] paramArrayOfString1, String[] paramArrayOfString2, Bundle paramBundle, boolean paramBoolean)
  {
    this.mType = paramInt1;
    this.mId = paramInt2;
    this.mInputPortCount = paramInt3;
    this.mOutputPortCount = paramInt4;
    if (paramArrayOfString1 == null)
    {
      this.mInputPortNames = new String[paramInt3];
      if (paramArrayOfString2 != null) {
        break label74;
      }
    }
    label74:
    for (this.mOutputPortNames = new String[paramInt4];; this.mOutputPortNames = paramArrayOfString2)
    {
      this.mProperties = paramBundle;
      this.mIsPrivate = paramBoolean;
      return;
      this.mInputPortNames = paramArrayOfString1;
      break;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof MidiDeviceInfo))
    {
      if (((MidiDeviceInfo)paramObject).mId == this.mId) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getInputPortCount()
  {
    return this.mInputPortCount;
  }
  
  public int getOutputPortCount()
  {
    return this.mOutputPortCount;
  }
  
  public PortInfo[] getPorts()
  {
    PortInfo[] arrayOfPortInfo = new PortInfo[this.mInputPortCount + this.mOutputPortCount];
    int i = 0;
    int j = 0;
    while (j < this.mInputPortCount)
    {
      arrayOfPortInfo[i] = new PortInfo(1, j, this.mInputPortNames[j]);
      j += 1;
      i += 1;
    }
    j = 0;
    while (j < this.mOutputPortCount)
    {
      arrayOfPortInfo[i] = new PortInfo(2, j, this.mOutputPortNames[j]);
      j += 1;
      i += 1;
    }
    return arrayOfPortInfo;
  }
  
  public Bundle getProperties()
  {
    return this.mProperties;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public int hashCode()
  {
    return this.mId;
  }
  
  public boolean isPrivate()
  {
    return this.mIsPrivate;
  }
  
  public String toString()
  {
    this.mProperties.getString("name");
    return "MidiDeviceInfo[mType=" + this.mType + ",mInputPortCount=" + this.mInputPortCount + ",mOutputPortCount=" + this.mOutputPortCount + ",mProperties=" + this.mProperties + ",mIsPrivate=" + this.mIsPrivate;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mId);
    paramParcel.writeInt(this.mInputPortCount);
    paramParcel.writeInt(this.mOutputPortCount);
    paramParcel.writeStringArray(this.mInputPortNames);
    paramParcel.writeStringArray(this.mOutputPortNames);
    paramParcel.writeBundle(this.mProperties);
    if (this.mIsPrivate) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
  
  public static final class PortInfo
  {
    public static final int TYPE_INPUT = 1;
    public static final int TYPE_OUTPUT = 2;
    private final String mName;
    private final int mPortNumber;
    private final int mPortType;
    
    PortInfo(int paramInt1, int paramInt2, String paramString)
    {
      this.mPortType = paramInt1;
      this.mPortNumber = paramInt2;
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      this.mName = str;
    }
    
    public String getName()
    {
      return this.mName;
    }
    
    public int getPortNumber()
    {
      return this.mPortNumber;
    }
    
    public int getType()
    {
      return this.mPortType;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiDeviceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */