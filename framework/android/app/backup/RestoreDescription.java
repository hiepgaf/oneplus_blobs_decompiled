package android.app.backup;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class RestoreDescription
  implements Parcelable
{
  public static final Parcelable.Creator<RestoreDescription> CREATOR = new Parcelable.Creator()
  {
    public RestoreDescription createFromParcel(Parcel paramAnonymousParcel)
    {
      RestoreDescription localRestoreDescription = new RestoreDescription(paramAnonymousParcel, null);
      paramAnonymousParcel = localRestoreDescription;
      if ("".equals(RestoreDescription.-get0(localRestoreDescription))) {
        paramAnonymousParcel = RestoreDescription.NO_MORE_PACKAGES;
      }
      return paramAnonymousParcel;
    }
    
    public RestoreDescription[] newArray(int paramAnonymousInt)
    {
      return new RestoreDescription[paramAnonymousInt];
    }
  };
  public static final RestoreDescription NO_MORE_PACKAGES = new RestoreDescription("", 0);
  private static final String NO_MORE_PACKAGES_SENTINEL = "";
  public static final int TYPE_FULL_STREAM = 2;
  public static final int TYPE_KEY_VALUE = 1;
  private final int mDataType;
  private final String mPackageName;
  
  private RestoreDescription(Parcel paramParcel)
  {
    this.mPackageName = paramParcel.readString();
    this.mDataType = paramParcel.readInt();
  }
  
  public RestoreDescription(String paramString, int paramInt)
  {
    this.mPackageName = paramString;
    this.mDataType = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getDataType()
  {
    return this.mDataType;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("RestoreDescription{").append(this.mPackageName).append(" : ");
    if (this.mDataType == 1) {}
    for (String str = "KEY_VALUE";; str = "STREAM") {
      return str + '}';
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeInt(this.mDataType);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/RestoreDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */