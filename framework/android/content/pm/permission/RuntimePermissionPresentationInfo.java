package android.content.pm.permission;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class RuntimePermissionPresentationInfo
  implements Parcelable
{
  public static final Parcelable.Creator<RuntimePermissionPresentationInfo> CREATOR = new Parcelable.Creator()
  {
    public RuntimePermissionPresentationInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RuntimePermissionPresentationInfo(paramAnonymousParcel, null);
    }
    
    public RuntimePermissionPresentationInfo[] newArray(int paramAnonymousInt)
    {
      return new RuntimePermissionPresentationInfo[paramAnonymousInt];
    }
  };
  private static final int FLAG_GRANTED = 1;
  private static final int FLAG_STANDARD = 2;
  private final int mFlags;
  private final CharSequence mLabel;
  
  private RuntimePermissionPresentationInfo(Parcel paramParcel)
  {
    this.mLabel = paramParcel.readCharSequence();
    this.mFlags = paramParcel.readInt();
  }
  
  public RuntimePermissionPresentationInfo(CharSequence paramCharSequence, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mLabel = paramCharSequence;
    int i = 0;
    if (paramBoolean1) {
      i = 1;
    }
    int j = i;
    if (paramBoolean2) {
      j = i | 0x2;
    }
    this.mFlags = j;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public boolean isGranted()
  {
    boolean bool = false;
    if ((this.mFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isStandard()
  {
    boolean bool = false;
    if ((this.mFlags & 0x2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeCharSequence(this.mLabel);
    paramParcel.writeInt(this.mFlags);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/permission/RuntimePermissionPresentationInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */