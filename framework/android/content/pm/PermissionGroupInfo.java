package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class PermissionGroupInfo
  extends PackageItemInfo
  implements Parcelable
{
  public static final Parcelable.Creator<PermissionGroupInfo> CREATOR = new Parcelable.Creator()
  {
    public PermissionGroupInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PermissionGroupInfo(paramAnonymousParcel, null);
    }
    
    public PermissionGroupInfo[] newArray(int paramAnonymousInt)
    {
      return new PermissionGroupInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_PERSONAL_INFO = 1;
  public int descriptionRes;
  public int flags;
  public CharSequence nonLocalizedDescription;
  public int priority;
  
  public PermissionGroupInfo() {}
  
  public PermissionGroupInfo(PermissionGroupInfo paramPermissionGroupInfo)
  {
    super(paramPermissionGroupInfo);
    this.descriptionRes = paramPermissionGroupInfo.descriptionRes;
    this.nonLocalizedDescription = paramPermissionGroupInfo.nonLocalizedDescription;
    this.flags = paramPermissionGroupInfo.flags;
    this.priority = paramPermissionGroupInfo.priority;
  }
  
  private PermissionGroupInfo(Parcel paramParcel)
  {
    super(paramParcel);
    this.descriptionRes = paramParcel.readInt();
    this.nonLocalizedDescription = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    this.flags = paramParcel.readInt();
    this.priority = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence loadDescription(PackageManager paramPackageManager)
  {
    if (this.nonLocalizedDescription != null) {
      return this.nonLocalizedDescription;
    }
    if (this.descriptionRes != 0)
    {
      paramPackageManager = paramPackageManager.getText(this.packageName, this.descriptionRes, null);
      if (paramPackageManager != null) {
        return paramPackageManager;
      }
    }
    return null;
  }
  
  public String toString()
  {
    return "PermissionGroupInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + " flgs=0x" + Integer.toHexString(this.flags) + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeInt(this.descriptionRes);
    TextUtils.writeToParcel(this.nonLocalizedDescription, paramParcel, paramInt);
    paramParcel.writeInt(this.flags);
    paramParcel.writeInt(this.priority);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PermissionGroupInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */