package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class OobData
  implements Parcelable
{
  public static final Parcelable.Creator<OobData> CREATOR = new Parcelable.Creator()
  {
    public OobData createFromParcel(Parcel paramAnonymousParcel)
    {
      return new OobData(paramAnonymousParcel, null);
    }
    
    public OobData[] newArray(int paramAnonymousInt)
    {
      return new OobData[paramAnonymousInt];
    }
  };
  private byte[] securityManagerTk;
  
  public OobData() {}
  
  private OobData(Parcel paramParcel)
  {
    this.securityManagerTk = paramParcel.createByteArray();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getSecurityManagerTk()
  {
    return this.securityManagerTk;
  }
  
  public void setSecurityManagerTk(byte[] paramArrayOfByte)
  {
    this.securityManagerTk = paramArrayOfByte;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeByteArray(this.securityManagerTk);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/OobData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */