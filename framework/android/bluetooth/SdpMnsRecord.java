package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SdpMnsRecord
  implements Parcelable
{
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public SdpMnsRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SdpMnsRecord(paramAnonymousParcel);
    }
    
    public SdpMnsRecord[] newArray(int paramAnonymousInt)
    {
      return new SdpMnsRecord[paramAnonymousInt];
    }
  };
  private final int mL2capPsm;
  private final int mProfileVersion;
  private final int mRfcommChannelNumber;
  private final String mServiceName;
  private final int mSupportedFeatures;
  
  public SdpMnsRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
  {
    this.mL2capPsm = paramInt1;
    this.mRfcommChannelNumber = paramInt2;
    this.mSupportedFeatures = paramInt4;
    this.mServiceName = paramString;
    this.mProfileVersion = paramInt3;
  }
  
  public SdpMnsRecord(Parcel paramParcel)
  {
    this.mRfcommChannelNumber = paramParcel.readInt();
    this.mL2capPsm = paramParcel.readInt();
    this.mServiceName = paramParcel.readString();
    this.mSupportedFeatures = paramParcel.readInt();
    this.mProfileVersion = paramParcel.readInt();
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
    if (this.mServiceName != null) {
      localObject2 = (String)localObject1 + "Service Name: " + this.mServiceName + "\n";
    }
    localObject1 = localObject2;
    if (this.mSupportedFeatures != -1) {
      localObject1 = (String)localObject2 + "Supported features: " + this.mSupportedFeatures + "\n";
    }
    localObject2 = localObject1;
    if (this.mProfileVersion != -1) {
      localObject2 = (String)localObject1 + "Profile_version: " + this.mProfileVersion + "\n";
    }
    return (String)localObject2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRfcommChannelNumber);
    paramParcel.writeInt(this.mL2capPsm);
    paramParcel.writeString(this.mServiceName);
    paramParcel.writeInt(this.mSupportedFeatures);
    paramParcel.writeInt(this.mProfileVersion);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/SdpMnsRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */