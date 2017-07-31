package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public class SdpRecord
  implements Parcelable
{
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public SdpRecord createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SdpRecord(paramAnonymousParcel);
    }
    
    public SdpRecord[] newArray(int paramAnonymousInt)
    {
      return new SdpRecord[paramAnonymousInt];
    }
  };
  private final byte[] mRawData;
  private final int mRawSize;
  
  public SdpRecord(int paramInt, byte[] paramArrayOfByte)
  {
    this.mRawData = paramArrayOfByte;
    this.mRawSize = paramInt;
  }
  
  public SdpRecord(Parcel paramParcel)
  {
    this.mRawSize = paramParcel.readInt();
    this.mRawData = new byte[this.mRawSize];
    paramParcel.readByteArray(this.mRawData);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getRawData()
  {
    return this.mRawData;
  }
  
  public int getRawSize()
  {
    return this.mRawSize;
  }
  
  public String toString()
  {
    return "BluetoothSdpRecord [rawData=" + Arrays.toString(this.mRawData) + ", rawSize=" + this.mRawSize + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRawSize);
    paramParcel.writeByteArray(this.mRawData);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/SdpRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */