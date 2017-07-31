package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SdpPseRecord
  implements Parcelable
{
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public SdpPseRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SdpPseRecord(paramAnonymousParcel);
    }
    
    public SdpPseRecord[] newArray(int paramAnonymousInt)
    {
      return new SdpPseRecord[paramAnonymousInt];
    }
  };
  private final int mL2capPsm;
  private final int mProfileVersion;
  private final int mRfcommChannelNumber;
  private final String mServiceName;
  private final int mSupportedFeatures;
  private final int mSupportedRepositories;
  
  public SdpPseRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString)
  {
    this.mL2capPsm = paramInt1;
    this.mRfcommChannelNumber = paramInt2;
    this.mProfileVersion = paramInt3;
    this.mSupportedFeatures = paramInt4;
    this.mSupportedRepositories = paramInt5;
    this.mServiceName = paramString;
  }
  
  public SdpPseRecord(Parcel paramParcel)
  {
    this.mRfcommChannelNumber = paramParcel.readInt();
    this.mL2capPsm = paramParcel.readInt();
    this.mProfileVersion = paramParcel.readInt();
    this.mSupportedFeatures = paramParcel.readInt();
    this.mSupportedRepositories = paramParcel.readInt();
    this.mServiceName = paramParcel.readString();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getL2capPsm()
  {
    return this.mL2capPsm;
  }
  
  public int getProfileVersion()
  {
    return this.mProfileVersion;
  }
  
  public int getRfcommChannelNumber()
  {
    return this.mRfcommChannelNumber;
  }
  
  public String getServiceName()
  {
    return this.mServiceName;
  }
  
  public int getSupportedFeatures()
  {
    return this.mSupportedFeatures;
  }
  
  public int getSupportedRepositories()
  {
    return this.mSupportedRepositories;
  }
  
  public String toString()
  {
    Object localObject2 = "Bluetooth MNS SDP Record:\n";
    if (this.mRfcommChannelNumber != -1) {
      localObject2 = "Bluetooth MNS SDP Record:\n" + "RFCOMM Chan Number: " + this.mRfcommChannelNumber + "\n";
    }
    Object localObject1 = localObject2;
    if (this.mL2capPsm != -1) {
      localObject1 = (String)localObject2 + "L2CAP PSM: " + this.mL2capPsm + "\n";
    }
    localObject2 = localObject1;
    if (this.mProfileVersion != -1) {
      localObject2 = (String)localObject1 + "profile version: " + this.mProfileVersion + "\n";
    }
    localObject1 = localObject2;
    if (this.mServiceName != null) {
      localObject1 = (String)localObject2 + "Service Name: " + this.mServiceName + "\n";
    }
    localObject2 = localObject1;
    if (this.mSupportedFeatures != -1) {
      localObject2 = (String)localObject1 + "Supported features: " + this.mSupportedFeatures + "\n";
    }
    localObject1 = localObject2;
    if (this.mSupportedRepositories != -1) {
      localObject1 = (String)localObject2 + "Supported repositories: " + this.mSupportedRepositories + "\n";
    }
    return (String)localObject1;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRfcommChannelNumber);
    paramParcel.writeInt(this.mL2capPsm);
    paramParcel.writeInt(this.mProfileVersion);
    paramParcel.writeInt(this.mSupportedFeatures);
    paramParcel.writeInt(this.mSupportedRepositories);
    paramParcel.writeString(this.mServiceName);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/SdpPseRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */