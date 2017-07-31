package android.media.tv;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class TvContentRatingSystemInfo
  implements Parcelable
{
  public static final Parcelable.Creator<TvContentRatingSystemInfo> CREATOR = new Parcelable.Creator()
  {
    public TvContentRatingSystemInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TvContentRatingSystemInfo(paramAnonymousParcel, null);
    }
    
    public TvContentRatingSystemInfo[] newArray(int paramAnonymousInt)
    {
      return new TvContentRatingSystemInfo[paramAnonymousInt];
    }
  };
  private final ApplicationInfo mApplicationInfo;
  private final Uri mXmlUri;
  
  private TvContentRatingSystemInfo(Uri paramUri, ApplicationInfo paramApplicationInfo)
  {
    this.mXmlUri = paramUri;
    this.mApplicationInfo = paramApplicationInfo;
  }
  
  private TvContentRatingSystemInfo(Parcel paramParcel)
  {
    this.mXmlUri = ((Uri)paramParcel.readParcelable(null));
    this.mApplicationInfo = ((ApplicationInfo)paramParcel.readParcelable(null));
  }
  
  public static final TvContentRatingSystemInfo createTvContentRatingSystemInfo(int paramInt, ApplicationInfo paramApplicationInfo)
  {
    return new TvContentRatingSystemInfo(new Uri.Builder().scheme("android.resource").authority(paramApplicationInfo.packageName).appendPath(String.valueOf(paramInt)).build(), paramApplicationInfo);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public final Uri getXmlUri()
  {
    return this.mXmlUri;
  }
  
  public final boolean isSystemDefined()
  {
    boolean bool = false;
    if ((this.mApplicationInfo.flags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mXmlUri, paramInt);
    paramParcel.writeParcelable(this.mApplicationInfo, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvContentRatingSystemInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */