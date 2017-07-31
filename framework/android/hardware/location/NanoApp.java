package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class NanoApp
{
  public static final Parcelable.Creator<NanoApp> CREATOR = new Parcelable.Creator()
  {
    public NanoApp createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NanoApp(paramAnonymousParcel, null);
    }
    
    public NanoApp[] newArray(int paramAnonymousInt)
    {
      return new NanoApp[paramAnonymousInt];
    }
  };
  private final String TAG = "NanoApp";
  private final String UNKNOWN = "Unknown";
  private byte[] mAppBinary;
  private int mAppId;
  private boolean mAppIdSet;
  private int mAppVersion;
  private String mName;
  private int mNeededExecMemBytes;
  private int mNeededReadMemBytes;
  private int[] mNeededSensors;
  private int mNeededWriteMemBytes;
  private int[] mOutputEvents;
  private String mPublisher;
  
  public NanoApp()
  {
    this(0, null);
    this.mAppIdSet = false;
  }
  
  public NanoApp(int paramInt, byte[] paramArrayOfByte)
  {
    this.mPublisher = "Unknown";
    this.mName = "Unknown";
    this.mAppId = paramInt;
    this.mAppIdSet = true;
    this.mAppVersion = 0;
    this.mNeededReadMemBytes = 0;
    this.mNeededWriteMemBytes = 0;
    this.mNeededExecMemBytes = 0;
    this.mNeededSensors = new int[0];
    this.mOutputEvents = new int[0];
    this.mAppBinary = paramArrayOfByte;
  }
  
  private NanoApp(Parcel paramParcel)
  {
    this.mPublisher = paramParcel.readString();
    this.mName = paramParcel.readString();
    this.mAppId = paramParcel.readInt();
    this.mAppVersion = paramParcel.readInt();
    this.mNeededReadMemBytes = paramParcel.readInt();
    this.mNeededWriteMemBytes = paramParcel.readInt();
    this.mNeededExecMemBytes = paramParcel.readInt();
    this.mNeededSensors = new int[paramParcel.readInt()];
    paramParcel.readIntArray(this.mNeededSensors);
    this.mOutputEvents = new int[paramParcel.readInt()];
    paramParcel.readIntArray(this.mOutputEvents);
    this.mAppBinary = new byte[paramParcel.readInt()];
    paramParcel.readByteArray(this.mAppBinary);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getAppBinary()
  {
    return this.mAppBinary;
  }
  
  public int getAppId()
  {
    return this.mAppId;
  }
  
  public int getAppVersion()
  {
    return this.mAppVersion;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public int getNeededExecMemBytes()
  {
    return this.mNeededExecMemBytes;
  }
  
  public int getNeededReadMemBytes()
  {
    return this.mNeededReadMemBytes;
  }
  
  public int[] getNeededSensors()
  {
    return this.mNeededSensors;
  }
  
  public int getNeededWriteMemBytes()
  {
    return this.mNeededWriteMemBytes;
  }
  
  public int[] getOutputEvents()
  {
    return this.mOutputEvents;
  }
  
  public String getPublisher()
  {
    return this.mPublisher;
  }
  
  public void setAppBinary(byte[] paramArrayOfByte)
  {
    this.mAppBinary = paramArrayOfByte;
  }
  
  public void setAppId(int paramInt)
  {
    this.mAppId = paramInt;
    this.mAppIdSet = true;
  }
  
  public void setAppVersion(int paramInt)
  {
    this.mAppVersion = paramInt;
  }
  
  public void setName(String paramString)
  {
    this.mName = paramString;
  }
  
  public void setNeededExecMemBytes(int paramInt)
  {
    this.mNeededExecMemBytes = paramInt;
  }
  
  public void setNeededReadMemBytes(int paramInt)
  {
    this.mNeededReadMemBytes = paramInt;
  }
  
  public void setNeededSensors(int[] paramArrayOfInt)
  {
    this.mNeededSensors = paramArrayOfInt;
  }
  
  public void setNeededWriteMemBytes(int paramInt)
  {
    this.mNeededWriteMemBytes = paramInt;
  }
  
  public void setOutputEvents(int[] paramArrayOfInt)
  {
    this.mOutputEvents = paramArrayOfInt;
  }
  
  public void setPublisher(String paramString)
  {
    this.mPublisher = paramString;
  }
  
  public String toString()
  {
    String str = "Id : " + this.mAppId;
    str = str + ", Version : " + this.mAppVersion;
    str = str + ", Name : " + this.mName;
    return str + ", Publisher : " + this.mPublisher;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.mAppBinary == null) {
      throw new IllegalStateException("Must set non-null AppBinary for nanoapp " + this.mName);
    }
    if (!this.mAppIdSet) {
      throw new IllegalStateException("Must set AppId for nanoapp " + this.mName);
    }
    paramParcel.writeString(this.mPublisher);
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mAppId);
    paramParcel.writeInt(this.mAppVersion);
    paramParcel.writeInt(this.mNeededReadMemBytes);
    paramParcel.writeInt(this.mNeededWriteMemBytes);
    paramParcel.writeInt(this.mNeededExecMemBytes);
    paramParcel.writeInt(this.mNeededSensors.length);
    paramParcel.writeIntArray(this.mNeededSensors);
    paramParcel.writeInt(this.mOutputEvents.length);
    paramParcel.writeIntArray(this.mOutputEvents);
    paramParcel.writeInt(this.mAppBinary.length);
    paramParcel.writeByteArray(this.mAppBinary);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/NanoApp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */