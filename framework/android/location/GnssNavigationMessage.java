package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.security.InvalidParameterException;

public final class GnssNavigationMessage
  implements Parcelable
{
  public static final Parcelable.Creator<GnssNavigationMessage> CREATOR = new Parcelable.Creator()
  {
    public GnssNavigationMessage createFromParcel(Parcel paramAnonymousParcel)
    {
      GnssNavigationMessage localGnssNavigationMessage = new GnssNavigationMessage();
      localGnssNavigationMessage.setType(paramAnonymousParcel.readInt());
      localGnssNavigationMessage.setSvid(paramAnonymousParcel.readInt());
      localGnssNavigationMessage.setMessageId(paramAnonymousParcel.readInt());
      localGnssNavigationMessage.setSubmessageId(paramAnonymousParcel.readInt());
      byte[] arrayOfByte = new byte[paramAnonymousParcel.readInt()];
      paramAnonymousParcel.readByteArray(arrayOfByte);
      localGnssNavigationMessage.setData(arrayOfByte);
      localGnssNavigationMessage.setStatus(paramAnonymousParcel.readInt());
      return localGnssNavigationMessage;
    }
    
    public GnssNavigationMessage[] newArray(int paramAnonymousInt)
    {
      return new GnssNavigationMessage[paramAnonymousInt];
    }
  };
  private static final byte[] EMPTY_ARRAY = new byte[0];
  public static final int STATUS_PARITY_PASSED = 1;
  public static final int STATUS_PARITY_REBUILT = 2;
  public static final int STATUS_UNKNOWN = 0;
  public static final int TYPE_BDS_D1 = 1281;
  public static final int TYPE_BDS_D2 = 1282;
  public static final int TYPE_GAL_F = 1538;
  public static final int TYPE_GAL_I = 1537;
  public static final int TYPE_GLO_L1CA = 769;
  public static final int TYPE_GPS_CNAV2 = 260;
  public static final int TYPE_GPS_L1CA = 257;
  public static final int TYPE_GPS_L2CNAV = 258;
  public static final int TYPE_GPS_L5CNAV = 259;
  public static final int TYPE_UNKNOWN = 0;
  private byte[] mData;
  private int mMessageId;
  private int mStatus;
  private int mSubmessageId;
  private int mSvid;
  private int mType;
  
  public GnssNavigationMessage()
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
    case 257: 
      return "GPS L1 C/A";
    case 258: 
      return "GPS L2-CNAV";
    case 259: 
      return "GPS L5-CNAV";
    case 260: 
      return "GPS CNAV2";
    case 769: 
      return "Glonass L1 C/A";
    case 1281: 
      return "Beidou D1";
    case 1282: 
      return "Beidou D2";
    case 1537: 
      return "Galileo I";
    }
    return "Galileo F";
  }
  
  private void initialize()
  {
    this.mType = 0;
    this.mSvid = 0;
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
  
  public int getMessageId()
  {
    return this.mMessageId;
  }
  
  public int getStatus()
  {
    return this.mStatus;
  }
  
  public int getSubmessageId()
  {
    return this.mSubmessageId;
  }
  
  public int getSvid()
  {
    return this.mSvid;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public void reset()
  {
    initialize();
  }
  
  public void set(GnssNavigationMessage paramGnssNavigationMessage)
  {
    this.mType = paramGnssNavigationMessage.mType;
    this.mSvid = paramGnssNavigationMessage.mSvid;
    this.mMessageId = paramGnssNavigationMessage.mMessageId;
    this.mSubmessageId = paramGnssNavigationMessage.mSubmessageId;
    this.mData = paramGnssNavigationMessage.mData;
    this.mStatus = paramGnssNavigationMessage.mStatus;
  }
  
  public void setData(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new InvalidParameterException("Data must be a non-null array");
    }
    this.mData = paramArrayOfByte;
  }
  
  public void setMessageId(int paramInt)
  {
    this.mMessageId = paramInt;
  }
  
  public void setStatus(int paramInt)
  {
    this.mStatus = paramInt;
  }
  
  public void setSubmessageId(int paramInt)
  {
    this.mSubmessageId = paramInt;
  }
  
  public void setSvid(int paramInt)
  {
    this.mSvid = paramInt;
  }
  
  public void setType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public String toString()
  {
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder("GnssNavigationMessage:\n");
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Type", getTypeString() }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Svid", Integer.valueOf(this.mSvid) }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "Status", getStatusString() }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "MessageId", Integer.valueOf(this.mMessageId) }));
    localStringBuilder.append(String.format("   %-15s = %s\n", new Object[] { "SubmessageId", Integer.valueOf(this.mSubmessageId) }));
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
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mSvid);
    paramParcel.writeInt(this.mMessageId);
    paramParcel.writeInt(this.mSubmessageId);
    paramParcel.writeInt(this.mData.length);
    paramParcel.writeByteArray(this.mData);
    paramParcel.writeInt(this.mStatus);
  }
  
  public static abstract class Callback
  {
    public static final int STATUS_LOCATION_DISABLED = 2;
    public static final int STATUS_NOT_SUPPORTED = 0;
    public static final int STATUS_READY = 1;
    
    public void onGnssNavigationMessageReceived(GnssNavigationMessage paramGnssNavigationMessage) {}
    
    public void onStatusChanged(int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssNavigationMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */