package com.android.server.pm;

import android.content.pm.PackageParser.Package;
import java.io.File;
import java.util.List;

final class PackageSetting
  extends PackageSettingBase
{
  int appId;
  PackageParser.Package pkg;
  SharedUserSetting sharedUser;
  
  PackageSetting(PackageSetting paramPackageSetting)
  {
    super(paramPackageSetting);
    this.appId = paramPackageSetting.appId;
    this.pkg = paramPackageSetting.pkg;
    this.sharedUser = paramPackageSetting.sharedUser;
  }
  
  PackageSetting(String paramString1, String paramString2, File paramFile1, File paramFile2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt1, int paramInt2, int paramInt3, String paramString7, List<String> paramList)
  {
    super(paramString1, paramString2, paramFile1, paramFile2, paramString3, paramString4, paramString5, paramString6, paramInt1, paramInt2, paramInt3, paramString7, paramList);
  }
  
  public PermissionsState getPermissionsState()
  {
    if (this.sharedUser != null) {
      return this.sharedUser.getPermissionsState();
    }
    return super.getPermissionsState();
  }
  
  public boolean isForwardLocked()
  {
    boolean bool = false;
    if ((this.pkgPrivateFlags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isMatch(int paramInt)
  {
    if ((0x100000 & paramInt) != 0) {
      return isSystem();
    }
    return true;
  }
  
  public boolean isPrivileged()
  {
    boolean bool = false;
    if ((this.pkgPrivateFlags & 0x8) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isSharedUser()
  {
    return this.sharedUser != null;
  }
  
  public boolean isSystem()
  {
    boolean bool = false;
    if ((this.pkgFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    return "PackageSetting{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "/" + this.appId + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageSetting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */