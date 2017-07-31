package android.net.wifi.p2p.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Locale;

public class WifiP2pServiceRequest
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pServiceRequest> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pServiceRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      return new WifiP2pServiceRequest(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readString(), null);
    }
    
    public WifiP2pServiceRequest[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pServiceRequest[paramAnonymousInt];
    }
  };
  private int mLength;
  private int mProtocolType;
  private String mQuery;
  private int mTransId;
  
  private WifiP2pServiceRequest(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    this.mProtocolType = paramInt1;
    this.mLength = paramInt2;
    this.mTransId = paramInt3;
    this.mQuery = paramString;
  }
  
  protected WifiP2pServiceRequest(int paramInt, String paramString)
  {
    validateQuery(paramString);
    this.mProtocolType = paramInt;
    this.mQuery = paramString;
    if (paramString != null)
    {
      this.mLength = (paramString.length() / 2 + 2);
      return;
    }
    this.mLength = 2;
  }
  
  public static WifiP2pServiceRequest newInstance(int paramInt)
  {
    return new WifiP2pServiceRequest(paramInt, null);
  }
  
  public static WifiP2pServiceRequest newInstance(int paramInt, String paramString)
  {
    return new WifiP2pServiceRequest(paramInt, paramString);
  }
  
  private void validateQuery(String paramString)
  {
    if (paramString == null) {
      return;
    }
    if (paramString.length() % 2 == 1) {
      throw new IllegalArgumentException("query size is invalid. query=" + paramString);
    }
    if (paramString.length() / 2 > 65535) {
      throw new IllegalArgumentException("query size is too large. len=" + paramString.length());
    }
    paramString = paramString.toLowerCase(Locale.ROOT);
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    int j = arrayOfChar.length;
    while (i < j)
    {
      int k = arrayOfChar[i];
      if (((k < 48) || (k > 57)) && ((k < 97) || (k > 102))) {
        throw new IllegalArgumentException("query should be hex string. query=" + paramString);
      }
      i += 1;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof WifiP2pServiceRequest)) {
      return false;
    }
    if ((((WifiP2pServiceRequest)paramObject).mProtocolType != this.mProtocolType) || (((WifiP2pServiceRequest)paramObject).mLength != this.mLength)) {
      return false;
    }
    if ((((WifiP2pServiceRequest)paramObject).mQuery == null) && (this.mQuery == null)) {
      return true;
    }
    if (((WifiP2pServiceRequest)paramObject).mQuery != null) {
      return ((WifiP2pServiceRequest)paramObject).mQuery.equals(this.mQuery);
    }
    return false;
  }
  
  public String getSupplicantQuery()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(String.format(Locale.US, "%02x", new Object[] { Integer.valueOf(this.mLength & 0xFF) }));
    localStringBuffer.append(String.format(Locale.US, "%02x", new Object[] { Integer.valueOf(this.mLength >> 8 & 0xFF) }));
    localStringBuffer.append(String.format(Locale.US, "%02x", new Object[] { Integer.valueOf(this.mProtocolType) }));
    localStringBuffer.append(String.format(Locale.US, "%02x", new Object[] { Integer.valueOf(this.mTransId) }));
    if (this.mQuery != null) {
      localStringBuffer.append(this.mQuery);
    }
    return localStringBuffer.toString();
  }
  
  public int getTransactionId()
  {
    return this.mTransId;
  }
  
  public int hashCode()
  {
    int j = this.mProtocolType;
    int k = this.mLength;
    if (this.mQuery == null) {}
    for (int i = 0;; i = this.mQuery.hashCode()) {
      return ((j + 527) * 31 + k) * 31 + i;
    }
  }
  
  public void setTransactionId(int paramInt)
  {
    this.mTransId = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mProtocolType);
    paramParcel.writeInt(this.mLength);
    paramParcel.writeInt(this.mTransId);
    paramParcel.writeString(this.mQuery);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/nsd/WifiP2pServiceRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */