package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SdpMasRecord
  implements Parcelable
{
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public SdpMasRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SdpMasRecord(paramAnonymousParcel);
    }
    
    public SdpRecord[] newArray(int paramAnonymousInt)
    {
      return new SdpRecord[paramAnonymousInt];
    }
  };
  private final int mL2capPsm;
  private final int mMasInstanceId;
  private final int mProfileVersion;
  private final int mRfcommChannelNumber;
  private final String mServiceName;
  private final int mSupportedFeatures;
  private final int mSupportedMessageTypes;
  
  public SdpMasRecord(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString)
  {
    this.mMasInstanceId = paramInt1;
    this.mL2capPsm = paramInt2;
    this.mRfcommChannelNumber = paramInt3;
    this.mProfileVersion = paramInt4;
    this.mSupportedFeatures = paramInt5;
    this.mSupportedMessageTypes = paramInt6;
    this.mServiceName = paramString;
  }
  
  public SdpMasRecord(Parcel paramParcel)
  {
    this.mMasInstanceId = paramParcel.readInt();
    this.mL2capPsm = paramParcel.readInt();
    this.mRfcommChannelNumber = paramParcel.readInt();
    this.mProfileVersion = paramParcel.readInt();
    this.mSupportedFeatures = paramParcel.readInt();
    this.mSupportedMessageTypes = paramParcel.readInt();
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
  
  public int getMasInstanceId()
  {
    return this.mMasInstanceId;
  }
  
  public int getProfileVersion()
  {
    return this.mProfileVersion;
  }
  
  public int getRfcommCannelNumber()
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
  
  public int getSupportedMessageTypes()
  {
    return this.mSupportedMessageTypes;
  }
  
  public boolean msgSupported(int paramInt)
  {
    boolean bool = false;
    if ((this.mSupportedMessageTypes & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    Object localObject2 = "Bluetooth MAS SDP Record:\n";
    if (this.mMasInstanceId != -1) {
      localObject2 = "Bluetooth MAS SDP Record:\n" + "Mas Instance Id: " + this.mMasInstanceId + "\n";
    }
    Object localObject1 = localObject2;
    if (this.mRfcommChannelNumber != -1) {
      localObject1 = (String)localObject2 + "RFCOMM Chan Number: " + this.mRfcommChannelNumber + "\n";
    }
    localObject2 = localObject1;
    if (this.mL2capPsm != -1) {
      localObject2 = (String)localObject1 + "L2CAP PSM: " + this.mL2capPsm + "\n";
    }
    localObject1 = localObject2;
    if (this.mServiceName != null) {
      localObject1 = (String)localObject2 + "Service Name: " + this.mServiceName + "\n";
    }
    localObject2 = localObject1;
    if (this.mProfileVersion != -1) {
      localObject2 = (String)localObject1 + "Profile version: " + this.mProfileVersion + "\n";
    }
    localObject1 = localObject2;
    if (this.mSupportedMessageTypes != -1) {
      localObject1 = (String)localObject2 + "Supported msg types: " + this.mSupportedMessageTypes + "\n";
    }
    localObject2 = localObject1;
    if (this.mSupportedFeatures != -1) {
      localObject2 = (String)localObject1 + "Supported features: " + this.mSupportedFeatures + "\n";
    }
    return (String)localObject2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mMasInstanceId);
    paramParcel.writeInt(this.mL2capPsm);
    paramParcel.writeInt(this.mRfcommChannelNumber);
    paramParcel.writeInt(this.mProfileVersion);
    paramParcel.writeInt(this.mSupportedFeatures);
    paramParcel.writeInt(this.mSupportedMessageTypes);
    paramParcel.writeString(this.mServiceName);
  }
  
  public static final class MessageType
  {
    public static final int EMAIL = 1;
    public static final int MMS = 8;
    public static final int SMS_CDMA = 4;
    public static final int SMS_GSM = 2;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/SdpMasRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */