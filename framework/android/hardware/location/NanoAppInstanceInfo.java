package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import libcore.util.EmptyArray;

public class NanoAppInstanceInfo
{
  public static final Parcelable.Creator<NanoAppInstanceInfo> CREATOR = new Parcelable.Creator()
  {
    public NanoAppInstanceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new NanoAppInstanceInfo(paramAnonymousParcel, null);
    }
    
    public NanoAppInstanceInfo[] newArray(int paramAnonymousInt)
    {
      return new NanoAppInstanceInfo[paramAnonymousInt];
    }
  };
  private long mAppId;
  private int mAppVersion;
  private int mContexthubId;
  private int mHandle;
  private String mName;
  private int mNeededExecMemBytes;
  private int mNeededReadMemBytes;
  private int[] mNeededSensors;
  private int mNeededWriteMemBytes;
  private int[] mOutputEvents;
  private String mPublisher;
  
  public NanoAppInstanceInfo()
  {
    this.mNeededSensors = EmptyArray.INT;
    this.mOutputEvents = EmptyArray.INT;
  }
  
  private NanoAppInstanceInfo(Parcel paramParcel)
  {
    this.mPublisher = paramParcel.readString();
    this.mName = paramParcel.readString();
    this.mAppId = paramParcel.readLong();
    this.mAppVersion = paramParcel.readInt();
    this.mNeededReadMemBytes = paramParcel.readInt();
    this.mNeededWriteMemBytes = paramParcel.readInt();
    this.mNeededExecMemBytes = paramParcel.readInt();
    this.mNeededSensors = new int[paramParcel.readInt()];
    paramParcel.readIntArray(this.mNeededSensors);
    this.mOutputEvents = new int[paramParcel.readInt()];
    paramParcel.readIntArray(this.mOutputEvents);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getAppId()
  {
    return this.mAppId;
  }
  
  public int getAppVersion()
  {
    return this.mAppVersion;
  }
  
  public int getContexthubId()
  {
    return this.mContexthubId;
  }
  
  public int getHandle()
  {
    return this.mHandle;
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
  
  public void setAppId(long paramLong)
  {
    this.mAppId = paramLong;
  }
  
  public void setAppVersion(int paramInt)
  {
    this.mAppVersion = paramInt;
  }
  
  public void setContexthubId(int paramInt)
  {
    this.mContexthubId = paramInt;
  }
  
  public void setHandle(int paramInt)
  {
    this.mHandle = paramInt;
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
    if (paramArrayOfInt != null) {}
    for (;;)
    {
      this.mNeededSensors = paramArrayOfInt;
      return;
      paramArrayOfInt = EmptyArray.INT;
    }
  }
  
  public void setNeededWriteMemBytes(int paramInt)
  {
    this.mNeededWriteMemBytes = paramInt;
  }
  
  public void setOutputEvents(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt != null) {}
    for (;;)
    {
      this.mOutputEvents = paramArrayOfInt;
      return;
      paramArrayOfInt = EmptyArray.INT;
    }
  }
  
  public void setPublisher(String paramString)
  {
    this.mPublisher = paramString;
  }
  
  public String toString()
  {
    String str = "handle : " + this.mHandle;
    str = str + ", Id : 0x" + Long.toHexString(this.mAppId);
    str = str + ", Version : " + this.mAppVersion;
    str = str + ", Name : " + this.mName;
    return str + ", Publisher : " + this.mPublisher;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPublisher);
    paramParcel.writeString(this.mName);
    paramParcel.writeLong(this.mAppId);
    paramParcel.writeInt(this.mAppVersion);
    paramParcel.writeInt(this.mContexthubId);
    paramParcel.writeInt(this.mNeededReadMemBytes);
    paramParcel.writeInt(this.mNeededWriteMemBytes);
    paramParcel.writeInt(this.mNeededExecMemBytes);
    paramParcel.writeInt(this.mNeededSensors.length);
    paramParcel.writeIntArray(this.mNeededSensors);
    paramParcel.writeInt(this.mOutputEvents.length);
    paramParcel.writeIntArray(this.mOutputEvents);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/NanoAppInstanceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */