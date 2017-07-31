package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public class ContextHubMessage
{
  public static final Parcelable.Creator<ContextHubMessage> CREATOR = new Parcelable.Creator()
  {
    public ContextHubMessage createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ContextHubMessage(paramAnonymousParcel, null);
    }
    
    public ContextHubMessage[] newArray(int paramAnonymousInt)
    {
      return new ContextHubMessage[paramAnonymousInt];
    }
  };
  private static final String TAG = "ContextHubMessage";
  private byte[] mData;
  private int mType;
  private int mVersion;
  
  public ContextHubMessage(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    this.mType = paramInt1;
    this.mVersion = paramInt2;
    this.mData = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  private ContextHubMessage(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mVersion = paramParcel.readInt();
    this.mData = new byte[paramParcel.readInt()];
    paramParcel.readByteArray(this.mData);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getData()
  {
    return Arrays.copyOf(this.mData, this.mData.length);
  }
  
  public int getMsgType()
  {
    return this.mType;
  }
  
  public int getVersion()
  {
    return this.mVersion;
  }
  
  public void setMsgData(byte[] paramArrayOfByte)
  {
    this.mData = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public void setMsgType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public void setVersion(int paramInt)
  {
    this.mVersion = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mVersion);
    paramParcel.writeInt(this.mData.length);
    paramParcel.writeByteArray(this.mData);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/ContextHubMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */