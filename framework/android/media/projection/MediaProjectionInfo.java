package android.media.projection;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import java.util.Objects;

public final class MediaProjectionInfo
  implements Parcelable
{
  public static final Parcelable.Creator<MediaProjectionInfo> CREATOR = new Parcelable.Creator()
  {
    public MediaProjectionInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MediaProjectionInfo(paramAnonymousParcel);
    }
    
    public MediaProjectionInfo[] newArray(int paramAnonymousInt)
    {
      return new MediaProjectionInfo[paramAnonymousInt];
    }
  };
  private final String mPackageName;
  private final UserHandle mUserHandle;
  
  public MediaProjectionInfo(Parcel paramParcel)
  {
    this.mPackageName = paramParcel.readString();
    this.mUserHandle = UserHandle.readFromParcel(paramParcel);
  }
  
  public MediaProjectionInfo(String paramString, UserHandle paramUserHandle)
  {
    this.mPackageName = paramString;
    this.mUserHandle = paramUserHandle;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof MediaProjectionInfo))
    {
      paramObject = (MediaProjectionInfo)paramObject;
      if (Objects.equals(((MediaProjectionInfo)paramObject).mPackageName, this.mPackageName)) {
        bool = Objects.equals(((MediaProjectionInfo)paramObject).mUserHandle, this.mUserHandle);
      }
      return bool;
    }
    return false;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public UserHandle getUserHandle()
  {
    return this.mUserHandle;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mPackageName, this.mUserHandle });
  }
  
  public String toString()
  {
    return "MediaProjectionInfo{mPackageName=" + this.mPackageName + ", mUserHandle=" + this.mUserHandle + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    UserHandle.writeToParcel(this.mUserHandle, paramParcel);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/projection/MediaProjectionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */