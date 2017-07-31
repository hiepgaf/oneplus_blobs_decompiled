package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.PatternMatcher;

public class PathPermission
  extends PatternMatcher
{
  public static final Parcelable.Creator<PathPermission> CREATOR = new Parcelable.Creator()
  {
    public PathPermission createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PathPermission(paramAnonymousParcel);
    }
    
    public PathPermission[] newArray(int paramAnonymousInt)
    {
      return new PathPermission[paramAnonymousInt];
    }
  };
  private final String mReadPermission;
  private final String mWritePermission;
  
  public PathPermission(Parcel paramParcel)
  {
    super(paramParcel);
    this.mReadPermission = paramParcel.readString();
    this.mWritePermission = paramParcel.readString();
  }
  
  public PathPermission(String paramString1, int paramInt, String paramString2, String paramString3)
  {
    super(paramString1, paramInt);
    this.mReadPermission = paramString2;
    this.mWritePermission = paramString3;
  }
  
  public String getReadPermission()
  {
    return this.mReadPermission;
  }
  
  public String getWritePermission()
  {
    return this.mWritePermission;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mReadPermission);
    paramParcel.writeString(this.mWritePermission);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PathPermission.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */