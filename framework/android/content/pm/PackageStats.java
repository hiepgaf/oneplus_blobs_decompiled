package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;

public class PackageStats
  implements Parcelable
{
  public static final Parcelable.Creator<PackageStats> CREATOR = new Parcelable.Creator()
  {
    public PackageStats createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PackageStats(paramAnonymousParcel);
    }
    
    public PackageStats[] newArray(int paramAnonymousInt)
    {
      return new PackageStats[paramAnonymousInt];
    }
  };
  public long cacheSize;
  public long codeSize;
  public long dataSize;
  public long externalCacheSize;
  public long externalCodeSize;
  public long externalDataSize;
  public long externalMediaSize;
  public long externalObbSize;
  public String packageName;
  public int userHandle;
  
  public PackageStats(PackageStats paramPackageStats)
  {
    this.packageName = paramPackageStats.packageName;
    this.userHandle = paramPackageStats.userHandle;
    this.codeSize = paramPackageStats.codeSize;
    this.dataSize = paramPackageStats.dataSize;
    this.cacheSize = paramPackageStats.cacheSize;
    this.externalCodeSize = paramPackageStats.externalCodeSize;
    this.externalDataSize = paramPackageStats.externalDataSize;
    this.externalCacheSize = paramPackageStats.externalCacheSize;
    this.externalMediaSize = paramPackageStats.externalMediaSize;
    this.externalObbSize = paramPackageStats.externalObbSize;
  }
  
  public PackageStats(Parcel paramParcel)
  {
    this.packageName = paramParcel.readString();
    this.userHandle = paramParcel.readInt();
    this.codeSize = paramParcel.readLong();
    this.dataSize = paramParcel.readLong();
    this.cacheSize = paramParcel.readLong();
    this.externalCodeSize = paramParcel.readLong();
    this.externalDataSize = paramParcel.readLong();
    this.externalCacheSize = paramParcel.readLong();
    this.externalMediaSize = paramParcel.readLong();
    this.externalObbSize = paramParcel.readLong();
  }
  
  public PackageStats(String paramString)
  {
    this.packageName = paramString;
    this.userHandle = UserHandle.myUserId();
  }
  
  public PackageStats(String paramString, int paramInt)
  {
    this.packageName = paramString;
    this.userHandle = paramInt;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("PackageStats{");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" ");
    localStringBuilder.append(this.packageName);
    if (this.codeSize != 0L)
    {
      localStringBuilder.append(" code=");
      localStringBuilder.append(this.codeSize);
    }
    if (this.dataSize != 0L)
    {
      localStringBuilder.append(" data=");
      localStringBuilder.append(this.dataSize);
    }
    if (this.cacheSize != 0L)
    {
      localStringBuilder.append(" cache=");
      localStringBuilder.append(this.cacheSize);
    }
    if (this.externalCodeSize != 0L)
    {
      localStringBuilder.append(" extCode=");
      localStringBuilder.append(this.externalCodeSize);
    }
    if (this.externalDataSize != 0L)
    {
      localStringBuilder.append(" extData=");
      localStringBuilder.append(this.externalDataSize);
    }
    if (this.externalCacheSize != 0L)
    {
      localStringBuilder.append(" extCache=");
      localStringBuilder.append(this.externalCacheSize);
    }
    if (this.externalMediaSize != 0L)
    {
      localStringBuilder.append(" media=");
      localStringBuilder.append(this.externalMediaSize);
    }
    if (this.externalObbSize != 0L)
    {
      localStringBuilder.append(" obb=");
      localStringBuilder.append(this.externalObbSize);
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.packageName);
    paramParcel.writeInt(this.userHandle);
    paramParcel.writeLong(this.codeSize);
    paramParcel.writeLong(this.dataSize);
    paramParcel.writeLong(this.cacheSize);
    paramParcel.writeLong(this.externalCodeSize);
    paramParcel.writeLong(this.externalDataSize);
    paramParcel.writeLong(this.externalCacheSize);
    paramParcel.writeLong(this.externalMediaSize);
    paramParcel.writeLong(this.externalObbSize);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */