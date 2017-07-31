package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;

public class UserInfo
  implements Parcelable
{
  public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator()
  {
    public UserInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UserInfo(paramAnonymousParcel, null);
    }
    
    public UserInfo[] newArray(int paramAnonymousInt)
    {
      return new UserInfo[paramAnonymousInt];
    }
  };
  public static final int FLAG_ADMIN = 2;
  public static final int FLAG_DEMO = 512;
  public static final int FLAG_DISABLED = 64;
  public static final int FLAG_EPHEMERAL = 256;
  public static final int FLAG_GUEST = 4;
  public static final int FLAG_INITIALIZED = 16;
  public static final int FLAG_MANAGED_PROFILE = 32;
  public static final int FLAG_MASK_USER_TYPE = 65535;
  public static final int FLAG_PRIMARY = 1;
  public static final int FLAG_QUIET_MODE = 128;
  public static final int FLAG_RESTRICTED = 8;
  public static final int NO_PROFILE_GROUP_ID = -10000;
  public long creationTime;
  public int flags;
  public boolean guestToRemove;
  public String iconPath;
  public int id;
  public String lastLoggedInFingerprint;
  public long lastLoggedInTime;
  public String name;
  public boolean partial;
  public int profileGroupId;
  public int restrictedProfileParentId;
  public int serialNumber;
  
  public UserInfo() {}
  
  public UserInfo(int paramInt1, String paramString, int paramInt2)
  {
    this(paramInt1, paramString, null, paramInt2);
  }
  
  public UserInfo(int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    this.id = paramInt1;
    this.name = paramString1;
    this.flags = paramInt2;
    this.iconPath = paramString2;
    this.profileGroupId = 55536;
    this.restrictedProfileParentId = 55536;
  }
  
  public UserInfo(UserInfo paramUserInfo)
  {
    this.name = paramUserInfo.name;
    this.iconPath = paramUserInfo.iconPath;
    this.id = paramUserInfo.id;
    this.flags = paramUserInfo.flags;
    this.serialNumber = paramUserInfo.serialNumber;
    this.creationTime = paramUserInfo.creationTime;
    this.lastLoggedInTime = paramUserInfo.lastLoggedInTime;
    this.lastLoggedInFingerprint = paramUserInfo.lastLoggedInFingerprint;
    this.partial = paramUserInfo.partial;
    this.profileGroupId = paramUserInfo.profileGroupId;
    this.restrictedProfileParentId = paramUserInfo.restrictedProfileParentId;
    this.guestToRemove = paramUserInfo.guestToRemove;
  }
  
  private UserInfo(Parcel paramParcel)
  {
    this.id = paramParcel.readInt();
    this.name = paramParcel.readString();
    this.iconPath = paramParcel.readString();
    this.flags = paramParcel.readInt();
    this.serialNumber = paramParcel.readInt();
    this.creationTime = paramParcel.readLong();
    this.lastLoggedInTime = paramParcel.readLong();
    this.lastLoggedInFingerprint = paramParcel.readString();
    if (paramParcel.readInt() != 0)
    {
      bool1 = true;
      this.partial = bool1;
      this.profileGroupId = paramParcel.readInt();
      if (paramParcel.readInt() == 0) {
        break label120;
      }
    }
    label120:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.guestToRemove = bool1;
      this.restrictedProfileParentId = paramParcel.readInt();
      return;
      bool1 = false;
      break;
    }
  }
  
  public static boolean isSystemOnly(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 0) {
      bool = UserManager.isSplitSystemUser();
    }
    return bool;
  }
  
  public boolean canHaveProfile()
  {
    if ((isManagedProfile()) || (isGuest()) || (isRestricted())) {
      return false;
    }
    if (UserManager.isSplitSystemUser()) {
      return this.id != 0;
    }
    return this.id == 0;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public UserHandle getUserHandle()
  {
    return new UserHandle(this.id);
  }
  
  public boolean isAdmin()
  {
    return (this.flags & 0x2) == 2;
  }
  
  public boolean isDemo()
  {
    return (this.flags & 0x200) == 512;
  }
  
  public boolean isEnabled()
  {
    return (this.flags & 0x40) != 64;
  }
  
  public boolean isEphemeral()
  {
    return (this.flags & 0x100) == 256;
  }
  
  public boolean isGuest()
  {
    return (this.flags & 0x4) == 4;
  }
  
  public boolean isInitialized()
  {
    return (this.flags & 0x10) == 16;
  }
  
  public boolean isManagedProfile()
  {
    return (this.flags & 0x20) == 32;
  }
  
  public boolean isPrimary()
  {
    return (this.flags & 0x1) == 1;
  }
  
  public boolean isQuietModeEnabled()
  {
    return (this.flags & 0x80) == 128;
  }
  
  public boolean isRestricted()
  {
    return (this.flags & 0x8) == 8;
  }
  
  public boolean isSystemOnly()
  {
    return isSystemOnly(this.id);
  }
  
  public boolean supportsSwitchTo()
  {
    if ((!isEphemeral()) || (isEnabled()))
    {
      if (isManagedProfile()) {
        return SystemProperties.getBoolean("fw.show_hidden_users", false);
      }
    }
    else {
      return false;
    }
    return true;
  }
  
  public boolean supportsSwitchToByUser()
  {
    boolean bool = false;
    if ((!UserManager.isSplitSystemUser()) || (this.id != 0)) {
      bool = supportsSwitchTo();
    }
    return bool;
  }
  
  public String toString()
  {
    return "UserInfo{" + this.id + ":" + this.name + ":" + Integer.toHexString(this.flags) + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeInt(this.id);
    paramParcel.writeString(this.name);
    paramParcel.writeString(this.iconPath);
    paramParcel.writeInt(this.flags);
    paramParcel.writeInt(this.serialNumber);
    paramParcel.writeLong(this.creationTime);
    paramParcel.writeLong(this.lastLoggedInTime);
    paramParcel.writeString(this.lastLoggedInFingerprint);
    if (this.partial)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.profileGroupId);
      if (!this.guestToRemove) {
        break label116;
      }
    }
    label116:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.restrictedProfileParentId);
      return;
      paramInt = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/UserInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */