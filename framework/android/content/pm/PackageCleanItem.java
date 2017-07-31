package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PackageCleanItem
  implements Parcelable
{
  public static final Parcelable.Creator<PackageCleanItem> CREATOR = new Parcelable.Creator()
  {
    public PackageCleanItem createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PackageCleanItem(paramAnonymousParcel, null);
    }
    
    public PackageCleanItem[] newArray(int paramAnonymousInt)
    {
      return new PackageCleanItem[paramAnonymousInt];
    }
  };
  public final boolean andCode;
  public final String packageName;
  public final int userId;
  
  public PackageCleanItem(int paramInt, String paramString, boolean paramBoolean)
  {
    this.userId = paramInt;
    this.packageName = paramString;
    this.andCode = paramBoolean;
  }
  
  private PackageCleanItem(Parcel paramParcel)
  {
    this.userId = paramParcel.readInt();
    this.packageName = paramParcel.readString();
    if (paramParcel.readInt() != 0) {
      bool = true;
    }
    this.andCode = bool;
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
    if (paramObject != null) {
      try
      {
        paramObject = (PackageCleanItem)paramObject;
        if ((this.userId == ((PackageCleanItem)paramObject).userId) && (this.packageName.equals(((PackageCleanItem)paramObject).packageName)))
        {
          boolean bool1 = this.andCode;
          boolean bool2 = ((PackageCleanItem)paramObject).andCode;
          return bool1 == bool2;
        }
        return false;
      }
      catch (ClassCastException paramObject) {}
    }
    return false;
  }
  
  public int hashCode()
  {
    int j = this.userId;
    int k = this.packageName.hashCode();
    if (this.andCode) {}
    for (int i = 1;; i = 0) {
      return ((j + 527) * 31 + k) * 31 + i;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.userId);
    paramParcel.writeString(this.packageName);
    if (this.andCode) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageCleanItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */