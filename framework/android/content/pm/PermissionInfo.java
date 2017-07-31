package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class PermissionInfo
  extends PackageItemInfo
  implements Parcelable
{
  public static final Parcelable.Creator<PermissionInfo> CREATOR = new Parcelable.Creator()
  {
    public PermissionInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PermissionInfo(paramAnonymousParcel, null);
    }
    
    public PermissionInfo[] newArray(int paramAnonymousInt)
    {
      return new PermissionInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_COSTS_MONEY = 1;
  public static final int FLAG_INSTALLED = 1073741824;
  public static final int FLAG_REMOVED = 2;
  public static final int PROTECTION_DANGEROUS = 1;
  public static final int PROTECTION_FLAG_APPOP = 64;
  public static final int PROTECTION_FLAG_DEVELOPMENT = 32;
  public static final int PROTECTION_FLAG_INSTALLER = 256;
  public static final int PROTECTION_FLAG_PRE23 = 128;
  public static final int PROTECTION_FLAG_PREINSTALLED = 1024;
  public static final int PROTECTION_FLAG_PRIVILEGED = 16;
  public static final int PROTECTION_FLAG_SETUP = 2048;
  @Deprecated
  public static final int PROTECTION_FLAG_SYSTEM = 16;
  public static final int PROTECTION_FLAG_VERIFIER = 512;
  public static final int PROTECTION_MASK_BASE = 15;
  public static final int PROTECTION_MASK_FLAGS = 4080;
  public static final int PROTECTION_NORMAL = 0;
  public static final int PROTECTION_SIGNATURE = 2;
  @Deprecated
  public static final int PROTECTION_SIGNATURE_OR_SYSTEM = 3;
  public int descriptionRes;
  public int flags;
  public String group;
  public CharSequence nonLocalizedDescription;
  public int protectionLevel;
  
  public PermissionInfo() {}
  
  public PermissionInfo(PermissionInfo paramPermissionInfo)
  {
    super(paramPermissionInfo);
    this.protectionLevel = paramPermissionInfo.protectionLevel;
    this.flags = paramPermissionInfo.flags;
    this.group = paramPermissionInfo.group;
    this.descriptionRes = paramPermissionInfo.descriptionRes;
    this.nonLocalizedDescription = paramPermissionInfo.nonLocalizedDescription;
  }
  
  private PermissionInfo(Parcel paramParcel)
  {
    super(paramParcel);
    this.protectionLevel = paramParcel.readInt();
    this.flags = paramParcel.readInt();
    this.group = paramParcel.readString();
    this.descriptionRes = paramParcel.readInt();
    this.nonLocalizedDescription = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
  }
  
  public static int fixProtectionLevel(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 3) {
      i = 18;
    }
    return i;
  }
  
  public static String protectionToString(int paramInt)
  {
    Object localObject1 = "????";
    switch (paramInt & 0xF)
    {
    }
    for (;;)
    {
      Object localObject2 = localObject1;
      if ((paramInt & 0x10) != 0) {
        localObject2 = (String)localObject1 + "|privileged";
      }
      localObject1 = localObject2;
      if ((paramInt & 0x20) != 0) {
        localObject1 = (String)localObject2 + "|development";
      }
      localObject2 = localObject1;
      if ((paramInt & 0x40) != 0) {
        localObject2 = (String)localObject1 + "|appop";
      }
      localObject1 = localObject2;
      if ((paramInt & 0x80) != 0) {
        localObject1 = (String)localObject2 + "|pre23";
      }
      localObject2 = localObject1;
      if ((paramInt & 0x100) != 0) {
        localObject2 = (String)localObject1 + "|installer";
      }
      localObject1 = localObject2;
      if ((paramInt & 0x200) != 0) {
        localObject1 = (String)localObject2 + "|verifier";
      }
      localObject2 = localObject1;
      if ((paramInt & 0x400) != 0) {
        localObject2 = (String)localObject1 + "|preinstalled";
      }
      localObject1 = localObject2;
      if ((paramInt & 0x800) != 0) {
        localObject1 = (String)localObject2 + "|setup";
      }
      return (String)localObject1;
      localObject1 = "dangerous";
      continue;
      localObject1 = "normal";
      continue;
      localObject1 = "signature";
      continue;
      localObject1 = "signatureOrSystem";
    }
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
    return "PermissionInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt);
    paramParcel.writeInt(this.protectionLevel);
    paramParcel.writeInt(this.flags);
    paramParcel.writeString(this.group);
    paramParcel.writeInt(this.descriptionRes);
    TextUtils.writeToParcel(this.nonLocalizedDescription, paramParcel, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PermissionInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */