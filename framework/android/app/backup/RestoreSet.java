package android.app.backup;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RestoreSet
  implements Parcelable
{
  public static final Parcelable.Creator<RestoreSet> CREATOR = new Parcelable.Creator()
  {
    public RestoreSet createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RestoreSet(paramAnonymousParcel, null);
    }
    
    public RestoreSet[] newArray(int paramAnonymousInt)
    {
      return new RestoreSet[paramAnonymousInt];
    }
  };
  public String device;
  public String name;
  public long token;
  
  public RestoreSet() {}
  
  private RestoreSet(Parcel paramParcel)
  {
    this.name = paramParcel.readString();
    this.device = paramParcel.readString();
    this.token = paramParcel.readLong();
  }
  
  public RestoreSet(String paramString1, String paramString2, long paramLong)
  {
    this.name = paramString1;
    this.device = paramString2;
    this.token = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.name);
    paramParcel.writeString(this.device);
    paramParcel.writeLong(this.token);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/RestoreSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */