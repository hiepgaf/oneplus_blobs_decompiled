package android.print;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;

public final class PrinterId
  implements Parcelable
{
  public static final Parcelable.Creator<PrinterId> CREATOR = new Parcelable.Creator()
  {
    public PrinterId createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrinterId(paramAnonymousParcel, null);
    }
    
    public PrinterId[] newArray(int paramAnonymousInt)
    {
      return new PrinterId[paramAnonymousInt];
    }
  };
  private final String mLocalId;
  private final ComponentName mServiceName;
  
  public PrinterId(ComponentName paramComponentName, String paramString)
  {
    this.mServiceName = paramComponentName;
    this.mLocalId = paramString;
  }
  
  private PrinterId(Parcel paramParcel)
  {
    this.mServiceName = ((ComponentName)Preconditions.checkNotNull((ComponentName)paramParcel.readParcelable(null)));
    this.mLocalId = ((String)Preconditions.checkNotNull(paramParcel.readString()));
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
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (PrinterId)paramObject;
    if (!this.mServiceName.equals(((PrinterId)paramObject).mServiceName)) {
      return false;
    }
    return this.mLocalId.equals(((PrinterId)paramObject).mLocalId);
  }
  
  public String getLocalId()
  {
    return this.mLocalId;
  }
  
  public ComponentName getServiceName()
  {
    return this.mServiceName;
  }
  
  public int hashCode()
  {
    return (this.mServiceName.hashCode() + 31) * 31 + this.mLocalId.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PrinterId{");
    localStringBuilder.append("serviceName=").append(this.mServiceName.flattenToString());
    localStringBuilder.append(", localId=").append(this.mLocalId);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mServiceName, paramInt);
    paramParcel.writeString(this.mLocalId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrinterId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */