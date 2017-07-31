package android.content.pm;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class EphemeralApplicationInfo
  implements Parcelable
{
  public static final Parcelable.Creator<EphemeralApplicationInfo> CREATOR = new Parcelable.Creator()
  {
    public EphemeralApplicationInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new EphemeralApplicationInfo(paramAnonymousParcel, null);
    }
    
    public EphemeralApplicationInfo[] newArray(int paramAnonymousInt)
    {
      return new EphemeralApplicationInfo[0];
    }
  };
  private final ApplicationInfo mApplicationInfo;
  private final String[] mGrantedPermissions;
  private final CharSequence mLabelText;
  private final String mPackageName;
  private final String[] mRequestedPermissions;
  
  public EphemeralApplicationInfo(ApplicationInfo paramApplicationInfo, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    this.mApplicationInfo = paramApplicationInfo;
    this.mPackageName = null;
    this.mLabelText = null;
    this.mRequestedPermissions = paramArrayOfString1;
    this.mGrantedPermissions = paramArrayOfString2;
  }
  
  private EphemeralApplicationInfo(Parcel paramParcel)
  {
    this.mPackageName = paramParcel.readString();
    this.mLabelText = paramParcel.readCharSequence();
    this.mRequestedPermissions = paramParcel.readStringArray();
    this.mGrantedPermissions = paramParcel.createStringArray();
    this.mApplicationInfo = ((ApplicationInfo)paramParcel.readParcelable(null));
  }
  
  public EphemeralApplicationInfo(String paramString, CharSequence paramCharSequence, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    this.mApplicationInfo = null;
    this.mPackageName = paramString;
    this.mLabelText = paramCharSequence;
    this.mRequestedPermissions = paramArrayOfString1;
    this.mGrantedPermissions = paramArrayOfString2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String[] getGrantedPermissions()
  {
    return this.mGrantedPermissions;
  }
  
  public String getPackageName()
  {
    if (this.mApplicationInfo != null) {
      return this.mApplicationInfo.packageName;
    }
    return this.mPackageName;
  }
  
  public String[] getRequestedPermissions()
  {
    return this.mRequestedPermissions;
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    if (this.mApplicationInfo != null) {
      return this.mApplicationInfo.loadIcon(paramPackageManager);
    }
    return paramPackageManager.getEphemeralApplicationIcon(this.mPackageName);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    if (this.mApplicationInfo != null) {
      return this.mApplicationInfo.loadLabel(paramPackageManager);
    }
    return this.mLabelText;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeCharSequence(this.mLabelText);
    paramParcel.writeStringArray(this.mRequestedPermissions);
    paramParcel.writeStringArray(this.mGrantedPermissions);
    paramParcel.writeParcelable(this.mApplicationInfo, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/EphemeralApplicationInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */