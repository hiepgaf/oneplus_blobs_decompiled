package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.Preconditions;
import java.util.UUID;

public final class PrintJobId
  implements Parcelable
{
  public static final Parcelable.Creator<PrintJobId> CREATOR = new Parcelable.Creator()
  {
    public PrintJobId createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrintJobId((String)Preconditions.checkNotNull(paramAnonymousParcel.readString()));
    }
    
    public PrintJobId[] newArray(int paramAnonymousInt)
    {
      return new PrintJobId[paramAnonymousInt];
    }
  };
  private final String mValue;
  
  public PrintJobId()
  {
    this(UUID.randomUUID().toString());
  }
  
  public PrintJobId(String paramString)
  {
    this.mValue = paramString;
  }
  
  public static PrintJobId unflattenFromString(String paramString)
  {
    return new PrintJobId(paramString);
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
    paramObject = (PrintJobId)paramObject;
    return this.mValue.equals(((PrintJobId)paramObject).mValue);
  }
  
  public String flattenToString()
  {
    return this.mValue;
  }
  
  public int hashCode()
  {
    return this.mValue.hashCode() + 31;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/print/PrintJobId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */