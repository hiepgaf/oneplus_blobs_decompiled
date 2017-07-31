package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public final class DataUsageRequest
  implements Parcelable
{
  public static final Parcelable.Creator<DataUsageRequest> CREATOR = new Parcelable.Creator()
  {
    public DataUsageRequest createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DataUsageRequest(paramAnonymousParcel.readInt(), (NetworkTemplate)paramAnonymousParcel.readParcelable(null), paramAnonymousParcel.readLong());
    }
    
    public DataUsageRequest[] newArray(int paramAnonymousInt)
    {
      return new DataUsageRequest[paramAnonymousInt];
    }
  };
  public static final String PARCELABLE_KEY = "DataUsageRequest";
  public static final int REQUEST_ID_UNSET = 0;
  public final int requestId;
  public final NetworkTemplate template;
  public final long thresholdInBytes;
  
  public DataUsageRequest(int paramInt, NetworkTemplate paramNetworkTemplate, long paramLong)
  {
    this.requestId = paramInt;
    this.template = paramNetworkTemplate;
    this.thresholdInBytes = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (!(paramObject instanceof DataUsageRequest)) {
      return false;
    }
    paramObject = (DataUsageRequest)paramObject;
    boolean bool1 = bool2;
    if (((DataUsageRequest)paramObject).requestId == this.requestId)
    {
      bool1 = bool2;
      if (Objects.equals(((DataUsageRequest)paramObject).template, this.template))
      {
        bool1 = bool2;
        if (((DataUsageRequest)paramObject).thresholdInBytes == this.thresholdInBytes) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.requestId), this.template, Long.valueOf(this.thresholdInBytes) });
  }
  
  public String toString()
  {
    return "DataUsageRequest [ requestId=" + this.requestId + ", networkTemplate=" + this.template + ", thresholdInBytes=" + this.thresholdInBytes + " ]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.requestId);
    paramParcel.writeParcelable(this.template, paramInt);
    paramParcel.writeLong(this.thresholdInBytes);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/DataUsageRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */