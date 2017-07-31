package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SdpSapsRecord
  implements Parcelable
{
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public SdpSapsRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SdpSapsRecord(paramAnonymousParcel);
    }
    
    public SdpRecord[] newArray(int paramAnonymousInt)
    {
      return new SdpRecord[paramAnonymousInt];
    }
  };
  private final int mProfileVersion;
  private final int mRfcommChannelNumber;
  private final String mServiceName;
  
  public SdpSapsRecord(int paramInt1, int paramInt2, String paramString)
  {
    this.mRfcommChannelNumber = paramInt1;
    this.mProfileVersion = paramInt2;
    this.mServiceName = paramString;
  }
  
  public SdpSapsRecord(Parcel paramParcel)
  {
    this.mRfcommChannelNumber = paramParcel.readInt();
    this.mProfileVersion = paramParcel.readInt();
    this.mServiceName = paramParcel.readString();
  }
  
  public int describeContents()
  {
    return 0;
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
  
  public String toString()
  {
    Object localObject2 = "Bluetooth MAS SDP Record:\n";
    if (this.mRfcommChannelNumber != -1) {
      localObject2 = "Bluetooth MAS SDP Record:\n" + "RFCOMM Chan Number: " + this.mRfcommChannelNumber + "\n";
    }
    Object localObject1 = localObject2;
    if (this.mServiceName != null) {
      localObject1 = (String)localObject2 + "Service Name: " + this.mServiceName + "\n";
    }
    localObject2 = localObject1;
    if (this.mProfileVersion != -1) {
      localObject2 = (String)localObject1 + "Profile version: " + this.mProfileVersion + "\n";
    }
    return (String)localObject2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRfcommChannelNumber);
    paramParcel.writeInt(this.mProfileVersion);
    paramParcel.writeString(this.mServiceName);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/SdpSapsRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */