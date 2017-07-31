package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.security.InvalidParameterException;

public class GpsNavigationMessage
  implements Parcelable
{
  public static final Parcelable.Creator<GpsNavigationMessage> CREATOR = new Parcelable.Creator()
  {
    public GpsNavigationMessage createFromParcel(Parcel paramAnonymousParcel)
    {
      GpsNavigationMessage localGpsNavigationMessage = new GpsNavigationMessage();
      localGpsNavigationMessage.setType(paramAnonymousParcel.readByte());
      localGpsNavigationMessage.setPrn(paramAnonymousParcel.readByte());
      localGpsNavigationMessage.setMessageId((short)paramAnonymousParcel.readInt());
      localGpsNavigationMessage.setSubmessageId((short)paramAnonymousParcel.readInt());
      byte[] arrayOfByte = new byte[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readByteArray(arrayOfByte);
      localGpsNavigationMessage.setData(arrayOfByte);
      if (paramAnonymousParcel.dataAvail() >= 32)
      {
        localGpsNavigationMessage.setStatus((short)paramAnonymousParcel.readInt());
        return localGpsNavigationMessage;
      }
      localGpsNavigationMessage.setStatus((short)0);
      return localGpsNavigationMessage;
    }
    
    public GpsNavigationMessage[] newArray(int paramAnonymousInt)
    {
      return new GpsNavigationMessage[paramAnonymousInt];
    }
  };
  private static final byte[] EMPTY_ARRAY = new byte[0];
  public static final short STATUS_PARITY_PASSED = 1;
  public static final short STATUS_PARITY_REBUILT = 2;
  public static final short STATUS_UNKNOWN = 0;
  public static final byte TYPE_CNAV2 = 4;
  public static final byte TYPE_L1CA = 1;
  public static final byte TYPE_L2CNAV = 2;
  public static final byte TYPE_L5CNAV = 3;
  public static final byte TYPE_UNKNOWN = 0;
  private byte[] mData;
  private short mMessageId;
  private byte mPrn;
  private short mStatus;
  private short mSubmessageId;
  private byte mType;
  
  GpsNavigationMessage()
  {
    initialize();
  }
  
  private String getStatusString()
  {
    switch (this.mStatus)
    {
    default: 
      return "<Invalid:" + this.mStatus + ">";
    case 0: 
      return "Unknown";
    case 1: 
      return "ParityPassed";
    }
    return "ParityRebuilt";
  }
  
  private String getTypeString()
  {
    switch (this.mType)
    {
    default: 
      return "<Invalid:" + this.mType + ">";
    case 0: 
      return "Unknown";
    case 1: 
      return "L1 C/A";
    case 2: 
      return "L2-CNAV";
    case 3: 
      return "L5-CNAV";
    }
    return "CNAV-2";
  }
  
  private void initialize()
  {
    this.mType = 0;
    this.mPrn = 0;
    this.mMessageId = -1;
    this.mSubmessageId = -1;
    this.mData = EMPTY_ARRAY;
    this.mStatus = 0;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public byte[] getData()
  {
    return this.mData;
  }
  
  public short getMessageId()
  {
    return this.mMessageId;
  }
  
  public byte getPrn()
  {
    return this.mPrn;
  }
  
  public short getStatus()
  {
    return this.mStatus;
  }
  
  public short getSubmessageId()
  {
    return this.mSubmessageId;
  }
  
  public byte getType()
  {
    return this.mType;
  }
  
  public void reset()
  {
    initialize();
  }
  
  public void set(GpsNavigationMessage paramGpsNavigationMessage)
  {
    this.mType = paramGpsNavigationMessage.mType;
    this.mPrn = paramGpsNavigationMessage.mPrn;
    this.mMessageId = paramGpsNavigationMessage.mMessageId;
    this.mSubmessageId = paramGpsNavigationMessage.mSubmessageId;
    this.mData = paramGpsNavigationMessage.mData;
    this.mStatus = paramGpsNavigationMessage.mStatus;
  }
  
  public void setData(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new InvalidParameterException("Data must be a non-null array");
    }
    this.mData = paramArrayOfByte;
  }
  
  public void setMessageId(short paramShort)
  {
    this.mMessageId = paramShort;
  }
  
  public void setPrn(byte paramByte)
  {
    this.mPrn = paramByte;
  }
  
  public void setStatus(short paramShort)
  {
    this.mStatus = paramShort;
  }
  
  public void setSubmessageId(short paramShort)
  {
    this.mSubmessageId = paramShort;
  }
  
  public void setType(byte paramByte)
  {
    this.mType = paramByte;
  }
  
  public String toString()
  {
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder("GpsNavigationMessage:\n");
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Type", getTypeString() }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Prn", Byte.valueOf(this.mPrn) }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Status", getStatusString() }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "MessageId", Short.valueOf(this.mMessageId) }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "SubmessageId", Short.valueOf(this.mSubmessageId) }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Data", "{" }));
    String str = "        ";
    byte[] arrayOfByte = this.mData;
    int j = arrayOfByte.length;
    while (i < j)
    {
      int k = arrayOfByte[i];
      localStringBuilder.append(str);
      localStringBuilder.append(k);
      str = ", ";
      i += 1;
    }
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeByte(this.mType);
    paramParcel.writeByte(this.mPrn);
    paramParcel.writeInt(this.mMessageId);
    paramParcel.writeInt(this.mSubmessageId);
    paramParcel.writeInt(this.mData.length);
    paramParcel.writeByteArray(this.mData);
    paramParcel.writeInt(this.mStatus);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GpsNavigationMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */