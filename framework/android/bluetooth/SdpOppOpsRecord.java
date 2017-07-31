package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public class SdpOppOpsRecord
  implements Parcelable
{
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public SdpOppOpsRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SdpOppOpsRecord(paramAnonymousParcel);
    }
    
    public SdpOppOpsRecord[] newArray(int paramAnonymousInt)
    {
      return new SdpOppOpsRecord[paramAnonymousInt];
    }
  };
  private final byte[] mFormatsList;
  private final int mL2capPsm;
  private final int mProfileVersion;
  private final int mRfcommChannel;
  private final String mServiceName;
  
  public SdpOppOpsRecord(Parcel paramParcel)
  {
    this.mRfcommChannel = paramParcel.readInt();
    this.mL2capPsm = paramParcel.readInt();
    this.mProfileVersion = paramParcel.readInt();
    this.mServiceName = paramParcel.readString();
    int i = paramParcel.readInt();
    if (i > 0)
    {
      byte[] arrayOfByte = new byte[i];
      paramParcel.readByteArray(arrayOfByte);
      this.mFormatsList = arrayOfByte;
      return;
    }
    this.mFormatsList = null;
  }
  
  public SdpOppOpsRecord(String paramString, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    this.mServiceName = paramString;
    this.mRfcommChannel = paramInt1;
    this.mL2capPsm = paramInt2;
    this.mProfileVersion = paramInt3;
    this.mFormatsList = paramArrayOfByte;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getFormatsList()
  {
    return this.mFormatsList;
  }
  
  public int getL2capPsm()
  {
    return this.mL2capPsm;
  }
  
  public int getProfileVersion()
  {
    return this.mProfileVersion;
  }
  
  public int getRfcommChannel()
  {
    return this.mRfcommChannel;
  }
  
  public String getServiceName()
  {
    return this.mServiceName;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Bluetooth OPP Server SDP Record:\n");
    localStringBuilder.append("  RFCOMM Chan Number: ").append(this.mRfcommChannel);
    localStringBuilder.append("\n  L2CAP PSM: ").append(this.mL2capPsm);
    localStringBuilder.append("\n  Profile version: ").append(this.mProfileVersion);
    localStringBuilder.append("\n  Service Name: ").append(this.mServiceName);
    localStringBuilder.append("\n  Formats List: ").append(Arrays.toString(this.mFormatsList));
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRfcommChannel);
    paramParcel.writeInt(this.mL2capPsm);
    paramParcel.writeInt(this.mProfileVersion);
    paramParcel.writeString(this.mServiceName);
    if ((this.mFormatsList != null) && (this.mFormatsList.length > 0))
    {
      paramParcel.writeInt(this.mFormatsList.length);
      paramParcel.writeByteArray(this.mFormatsList);
      return;
    }
    paramParcel.writeInt(0);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/SdpOppOpsRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */