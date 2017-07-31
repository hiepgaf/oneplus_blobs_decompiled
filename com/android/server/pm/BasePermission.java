package com.android.server.pm;

import android.content.pm.PackageParser.Permission;
import android.content.pm.PermissionInfo;
import android.os.UserHandle;

final class BasePermission
{
  static final int TYPE_BUILTIN = 1;
  static final int TYPE_DYNAMIC = 2;
  static final int TYPE_NORMAL = 0;
  private int[] gids;
  final String name;
  PackageSettingBase packageSetting;
  PermissionInfo pendingInfo;
  private boolean perUser;
  PackageParser.Permission perm;
  int protectionLevel;
  String sourcePackage;
  final int type;
  int uid;
  
  BasePermission(String paramString1, String paramString2, int paramInt)
  {
    this.name = paramString1;
    this.sourcePackage = paramString2;
    this.type = paramInt;
    this.protectionLevel = 2;
  }
  
  public int[] computeGids(int paramInt)
  {
    if (this.perUser)
    {
      int[] arrayOfInt = new int[this.gids.length];
      int i = 0;
      while (i < this.gids.length)
      {
        arrayOfInt[i] = UserHandle.getUid(paramInt, this.gids[i]);
        i += 1;
      }
      return arrayOfInt;
    }
    return this.gids;
  }
  
  public boolean isDevelopment()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((this.protectionLevel & 0xF) == 2)
    {
      bool1 = bool2;
      if ((this.protectionLevel & 0x20) != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isRuntime()
  {
    return (this.protectionLevel & 0xF) == 1;
  }
  
  public void setGids(int[] paramArrayOfInt, boolean paramBoolean)
  {
    this.gids = paramArrayOfInt;
    this.perUser = paramBoolean;
  }
  
  public String toString()
  {
    return "BasePermission{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/BasePermission.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */