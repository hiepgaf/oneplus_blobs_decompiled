package android.security.keymaster;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ExportResult
  implements Parcelable
{
  public static final Parcelable.Creator<ExportResult> CREATOR = new Parcelable.Creator()
  {
    public ExportResult createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ExportResult(paramAnonymousParcel);
    }
    
    public ExportResult[] newArray(int paramAnonymousInt)
    {
      return new ExportResult[paramAnonymousInt];
    }
  };
  public final byte[] exportData;
  public final int resultCode;
  
  protected ExportResult(Parcel paramParcel)
  {
    this.resultCode = paramParcel.readInt();
    this.exportData = paramParcel.createByteArray();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.resultCode);
    paramParcel.writeByteArray(this.exportData);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/security/keymaster/ExportResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */