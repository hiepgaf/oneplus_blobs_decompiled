package android.net.wifi.nan;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public class PublishData
  implements Parcelable
{
  public static final Parcelable.Creator<PublishData> CREATOR = new Parcelable.Creator()
  {
    public PublishData createFromParcel(Parcel paramAnonymousParcel)
    {
      String str = paramAnonymousParcel.readString();
      int i = paramAnonymousParcel.readInt();
      byte[] arrayOfByte1 = new byte[i];
      if (i != 0) {
        paramAnonymousParcel.readByteArray(arrayOfByte1);
      }
      int j = paramAnonymousParcel.readInt();
      byte[] arrayOfByte2 = new byte[j];
      if (j != 0) {
        paramAnonymousParcel.readByteArray(arrayOfByte2);
      }
      int k = paramAnonymousParcel.readInt();
      byte[] arrayOfByte3 = new byte[k];
      if (k != 0) {
        paramAnonymousParcel.readByteArray(arrayOfByte3);
      }
      return new PublishData(str, arrayOfByte1, i, arrayOfByte2, j, arrayOfByte3, k, null);
    }
    
    public PublishData[] newArray(int paramAnonymousInt)
    {
      return new PublishData[paramAnonymousInt];
    }
  };
  public final byte[] mRxFilter;
  public final int mRxFilterLength;
  public final String mServiceName;
  public final byte[] mServiceSpecificInfo;
  public final int mServiceSpecificInfoLength;
  public final byte[] mTxFilter;
  public final int mTxFilterLength;
  
  private PublishData(String paramString, byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3)
  {
    this.mServiceName = paramString;
    this.mServiceSpecificInfoLength = paramInt1;
    this.mServiceSpecificInfo = paramArrayOfByte1;
    this.mTxFilterLength = paramInt2;
    this.mTxFilter = paramArrayOfByte2;
    this.mRxFilterLength = paramInt3;
    this.mRxFilter = paramArrayOfByte3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PublishData)) {
      return false;
    }
    if ((!this.mServiceName.equals(((PublishData)paramObject).mServiceName)) || (this.mServiceSpecificInfoLength != ((PublishData)paramObject).mServiceSpecificInfoLength)) {}
    while ((this.mTxFilterLength != ((PublishData)paramObject).mTxFilterLength) || (this.mRxFilterLength != ((PublishData)paramObject).mRxFilterLength)) {
      return false;
    }
    int i;
    if ((this.mServiceSpecificInfo != null) && (((PublishData)paramObject).mServiceSpecificInfo != null)) {
      i = 0;
    }
    while (i < this.mServiceSpecificInfoLength)
    {
      if (this.mServiceSpecificInfo[i] != paramObject.mServiceSpecificInfo[i]) {
        return false;
      }
      i += 1;
      continue;
      if (this.mServiceSpecificInfoLength != 0) {
        return false;
      }
    }
    if ((this.mTxFilter != null) && (((PublishData)paramObject).mTxFilter != null)) {
      i = 0;
    }
    while (i < this.mTxFilterLength)
    {
      if (this.mTxFilter[i] != paramObject.mTxFilter[i]) {
        return false;
      }
      i += 1;
      continue;
      if (this.mTxFilterLength != 0) {
        return false;
      }
    }
    if ((this.mRxFilter != null) && (((PublishData)paramObject).mRxFilter != null)) {
      i = 0;
    }
    while (i < this.mRxFilterLength)
    {
      if (this.mRxFilter[i] != paramObject.mRxFilter[i]) {
        return false;
      }
      i += 1;
      continue;
      if (this.mRxFilterLength != 0) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    return ((((((this.mServiceName.hashCode() + 527) * 31 + this.mServiceSpecificInfoLength) * 31 + Arrays.hashCode(this.mServiceSpecificInfo)) * 31 + this.mTxFilterLength) * 31 + Arrays.hashCode(this.mTxFilter)) * 31 + this.mRxFilterLength) * 31 + Arrays.hashCode(this.mRxFilter);
  }
  
  public String toString()
  {
    return "PublishData [mServiceName='" + this.mServiceName + "', mServiceSpecificInfo='" + new String(this.mServiceSpecificInfo, 0, this.mServiceSpecificInfoLength) + "', mTxFilter=" + new TlvBufferUtils.TlvIterable(0, 1, this.mTxFilter, this.mTxFilterLength).toString() + ", mRxFilter=" + new TlvBufferUtils.TlvIterable(0, 1, this.mRxFilter, this.mRxFilterLength).toString() + "']";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mServiceName);
    paramParcel.writeInt(this.mServiceSpecificInfoLength);
    if (this.mServiceSpecificInfoLength != 0) {
      paramParcel.writeByteArray(this.mServiceSpecificInfo, 0, this.mServiceSpecificInfoLength);
    }
    paramParcel.writeInt(this.mTxFilterLength);
    if (this.mTxFilterLength != 0) {
      paramParcel.writeByteArray(this.mTxFilter, 0, this.mTxFilterLength);
    }
    paramParcel.writeInt(this.mRxFilterLength);
    if (this.mRxFilterLength != 0) {
      paramParcel.writeByteArray(this.mRxFilter, 0, this.mRxFilterLength);
    }
  }
  
  public static final class Builder
  {
    private byte[] mRxFilter = new byte[0];
    private int mRxFilterLength;
    private String mServiceName;
    private byte[] mServiceSpecificInfo = new byte[0];
    private int mServiceSpecificInfoLength;
    private byte[] mTxFilter = new byte[0];
    private int mTxFilterLength;
    
    public PublishData build()
    {
      return new PublishData(this.mServiceName, this.mServiceSpecificInfo, this.mServiceSpecificInfoLength, this.mTxFilter, this.mTxFilterLength, this.mRxFilter, this.mRxFilterLength, null);
    }
    
    public Builder setRxFilter(byte[] paramArrayOfByte, int paramInt)
    {
      if ((paramInt != 0) && ((paramArrayOfByte == null) || (paramArrayOfByte.length < paramInt))) {
        throw new IllegalArgumentException("Non-matching combination of rxFilter and rxFilterLength");
      }
      this.mRxFilter = paramArrayOfByte;
      this.mRxFilterLength = paramInt;
      return this;
    }
    
    public Builder setServiceName(String paramString)
    {
      this.mServiceName = paramString;
      return this;
    }
    
    public Builder setServiceSpecificInfo(String paramString)
    {
      this.mServiceSpecificInfoLength = paramString.length();
      this.mServiceSpecificInfo = paramString.getBytes();
      return this;
    }
    
    public Builder setServiceSpecificInfo(byte[] paramArrayOfByte, int paramInt)
    {
      if ((paramInt != 0) && ((paramArrayOfByte == null) || (paramArrayOfByte.length < paramInt))) {
        throw new IllegalArgumentException("Non-matching combination of serviceSpecificInfo and serviceSpecificInfoLength");
      }
      this.mServiceSpecificInfoLength = paramInt;
      this.mServiceSpecificInfo = paramArrayOfByte;
      return this;
    }
    
    public Builder setTxFilter(byte[] paramArrayOfByte, int paramInt)
    {
      if ((paramInt != 0) && ((paramArrayOfByte == null) || (paramArrayOfByte.length < paramInt))) {
        throw new IllegalArgumentException("Non-matching combination of txFilter and txFilterLength");
      }
      this.mTxFilter = paramArrayOfByte;
      this.mTxFilterLength = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/PublishData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */