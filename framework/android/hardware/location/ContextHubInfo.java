package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public class ContextHubInfo
{
  public static final Parcelable.Creator<ContextHubInfo> CREATOR = new Parcelable.Creator()
  {
    public ContextHubInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ContextHubInfo(paramAnonymousParcel, null);
    }
    
    public ContextHubInfo[] newArray(int paramAnonymousInt)
    {
      return new ContextHubInfo[paramAnonymousInt];
    }
  };
  private int mId;
  private int mMaxPacketLengthBytes;
  private MemoryRegion[] mMemoryRegions;
  private String mName;
  private float mPeakMips;
  private float mPeakPowerDrawMw;
  private int mPlatformVersion;
  private float mSleepPowerDrawMw;
  private int mStaticSwVersion;
  private float mStoppedPowerDrawMw;
  private int[] mSupportedSensors;
  private String mToolchain;
  private int mToolchainVersion;
  private String mVendor;
  
  public ContextHubInfo() {}
  
  private ContextHubInfo(Parcel paramParcel)
  {
    this.mId = paramParcel.readInt();
    this.mName = paramParcel.readString();
    this.mVendor = paramParcel.readString();
    this.mToolchain = paramParcel.readString();
    this.mPlatformVersion = paramParcel.readInt();
    this.mToolchainVersion = paramParcel.readInt();
    this.mStaticSwVersion = paramParcel.readInt();
    this.mPeakMips = paramParcel.readFloat();
    this.mStoppedPowerDrawMw = paramParcel.readFloat();
    this.mSleepPowerDrawMw = paramParcel.readFloat();
    this.mPeakPowerDrawMw = paramParcel.readFloat();
    this.mMaxPacketLengthBytes = paramParcel.readInt();
    this.mSupportedSensors = new int[paramParcel.readInt()];
    paramParcel.readIntArray(this.mSupportedSensors);
    this.mMemoryRegions = ((MemoryRegion[])paramParcel.createTypedArray(MemoryRegion.CREATOR));
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public int getMaxPacketLengthBytes()
  {
    return this.mMaxPacketLengthBytes;
  }
  
  public MemoryRegion[] getMemoryRegions()
  {
    return (MemoryRegion[])Arrays.copyOf(this.mMemoryRegions, this.mMemoryRegions.length);
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public float getPeakMips()
  {
    return this.mPeakMips;
  }
  
  public float getPeakPowerDrawMw()
  {
    return this.mPeakPowerDrawMw;
  }
  
  public int getPlatformVersion()
  {
    return this.mPlatformVersion;
  }
  
  public float getSleepPowerDrawMw()
  {
    return this.mSleepPowerDrawMw;
  }
  
  public int getStaticSwVersion()
  {
    return this.mStaticSwVersion;
  }
  
  public float getStoppedPowerDrawMw()
  {
    return this.mStoppedPowerDrawMw;
  }
  
  public int[] getSupportedSensors()
  {
    return Arrays.copyOf(this.mSupportedSensors, this.mSupportedSensors.length);
  }
  
  public String getToolchain()
  {
    return this.mToolchain;
  }
  
  public int getToolchainVersion()
  {
    return this.mToolchainVersion;
  }
  
  public String getVendor()
  {
    return this.mVendor;
  }
  
  public void setId(int paramInt)
  {
    this.mId = paramInt;
  }
  
  public void setMaxPacketLenBytes(int paramInt)
  {
    this.mMaxPacketLengthBytes = paramInt;
  }
  
  public void setMemoryRegions(MemoryRegion[] paramArrayOfMemoryRegion)
  {
    this.mMemoryRegions = ((MemoryRegion[])Arrays.copyOf(paramArrayOfMemoryRegion, paramArrayOfMemoryRegion.length));
  }
  
  public void setName(String paramString)
  {
    this.mName = paramString;
  }
  
  public void setPeakMips(float paramFloat)
  {
    this.mPeakMips = paramFloat;
  }
  
  public void setPeakPowerDrawMw(float paramFloat)
  {
    this.mPeakPowerDrawMw = paramFloat;
  }
  
  public void setPlatformVersion(int paramInt)
  {
    this.mPlatformVersion = paramInt;
  }
  
  public void setSleepPowerDrawMw(float paramFloat)
  {
    this.mSleepPowerDrawMw = paramFloat;
  }
  
  public void setStaticSwVersion(int paramInt)
  {
    this.mStaticSwVersion = paramInt;
  }
  
  public void setStoppedPowerDrawMw(float paramFloat)
  {
    this.mStoppedPowerDrawMw = paramFloat;
  }
  
  public void setSupportedSensors(int[] paramArrayOfInt)
  {
    this.mSupportedSensors = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
  }
  
  public void setToolchain(String paramString)
  {
    this.mToolchain = paramString;
  }
  
  public void setToolchainVersion(int paramInt)
  {
    this.mToolchainVersion = paramInt;
  }
  
  public void setVendor(String paramString)
  {
    this.mVendor = paramString;
  }
  
  public String toString()
  {
    String str = "" + "Id : " + this.mId;
    str = str + ", Name : " + this.mName;
    str = str + "\n\tVendor : " + this.mVendor;
    str = str + ", ToolChain : " + this.mToolchain;
    str = str + "\n\tPlatformVersion : " + this.mPlatformVersion;
    str = str + ", StaticSwVersion : " + this.mStaticSwVersion;
    str = str + "\n\tPeakMips : " + this.mPeakMips;
    str = str + ", StoppedPowerDraw : " + this.mStoppedPowerDrawMw + " mW";
    str = str + ", PeakPowerDraw : " + this.mPeakPowerDrawMw + " mW";
    str = str + ", MaxPacketLength : " + this.mMaxPacketLengthBytes + " Bytes";
    str = str + "\n\tSupported sensors : " + Arrays.toString(this.mSupportedSensors);
    return str + "\n\tMemory Regions : " + Arrays.toString(this.mMemoryRegions);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mId);
    paramParcel.writeString(this.mName);
    paramParcel.writeString(this.mVendor);
    paramParcel.writeString(this.mToolchain);
    paramParcel.writeInt(this.mPlatformVersion);
    paramParcel.writeInt(this.mToolchainVersion);
    paramParcel.writeInt(this.mStaticSwVersion);
    paramParcel.writeFloat(this.mPeakMips);
    paramParcel.writeFloat(this.mStoppedPowerDrawMw);
    paramParcel.writeFloat(this.mSleepPowerDrawMw);
    paramParcel.writeFloat(this.mPeakPowerDrawMw);
    paramParcel.writeInt(this.mMaxPacketLengthBytes);
    paramParcel.writeInt(this.mSupportedSensors.length);
    paramParcel.writeIntArray(this.mSupportedSensors);
    paramParcel.writeTypedArray(this.mMemoryRegions, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/ContextHubInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */