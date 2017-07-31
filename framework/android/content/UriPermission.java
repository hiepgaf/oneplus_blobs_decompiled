package android.content;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class UriPermission
  implements Parcelable
{
  public static final Parcelable.Creator<UriPermission> CREATOR = new Parcelable.Creator()
  {
    public UriPermission createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UriPermission(paramAnonymousParcel);
    }
    
    public UriPermission[] newArray(int paramAnonymousInt)
    {
      return new UriPermission[paramAnonymousInt];
    }
  };
  public static final long INVALID_TIME = Long.MIN_VALUE;
  private final int mModeFlags;
  private final long mPersistedTime;
  private final Uri mUri;
  
  public UriPermission(Uri paramUri, int paramInt, long paramLong)
  {
    this.mUri = paramUri;
    this.mModeFlags = paramInt;
    this.mPersistedTime = paramLong;
  }
  
  public UriPermission(Parcel paramParcel)
  {
    this.mUri = ((Uri)paramParcel.readParcelable(null));
    this.mModeFlags = paramParcel.readInt();
    this.mPersistedTime = paramParcel.readLong();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getPersistedTime()
  {
    return this.mPersistedTime;
  }
  
  public Uri getUri()
  {
    return this.mUri;
  }
  
  public boolean isReadPermission()
  {
    boolean bool = false;
    if ((this.mModeFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isWritePermission()
  {
    boolean bool = false;
    if ((this.mModeFlags & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    return "UriPermission {uri=" + this.mUri + ", modeFlags=" + this.mModeFlags + ", persistedTime=" + this.mPersistedTime + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mUri, paramInt);
    paramParcel.writeInt(this.mModeFlags);
    paramParcel.writeLong(this.mPersistedTime);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/UriPermission.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */