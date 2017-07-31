package com.android.server.pm;

import android.util.ArraySet;
import java.util.Iterator;

final class SharedUserSetting
  extends SettingBase
{
  final String name;
  final ArraySet<PackageSetting> packages = new ArraySet();
  final PackageSignatures signatures = new PackageSignatures();
  int uidFlags;
  int uidPrivateFlags;
  int userId;
  
  SharedUserSetting(String paramString, int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    this.uidFlags = paramInt1;
    this.uidPrivateFlags = paramInt2;
    this.name = paramString;
  }
  
  void addPackage(PackageSetting paramPackageSetting)
  {
    if (this.packages.add(paramPackageSetting))
    {
      setFlags(this.pkgFlags | paramPackageSetting.pkgFlags);
      setPrivateFlags(this.pkgPrivateFlags | paramPackageSetting.pkgPrivateFlags);
    }
  }
  
  void removePackage(PackageSetting paramPackageSetting)
  {
    if (this.packages.remove(paramPackageSetting))
    {
      int i;
      if ((this.pkgFlags & paramPackageSetting.pkgFlags) != 0)
      {
        i = this.uidFlags;
        Iterator localIterator = this.packages.iterator();
        while (localIterator.hasNext()) {
          i |= ((PackageSetting)localIterator.next()).pkgFlags;
        }
        setFlags(i);
      }
      if ((this.pkgPrivateFlags & paramPackageSetting.pkgPrivateFlags) != 0)
      {
        i = this.uidPrivateFlags;
        paramPackageSetting = this.packages.iterator();
        while (paramPackageSetting.hasNext()) {
          i |= ((PackageSetting)paramPackageSetting.next()).pkgPrivateFlags;
        }
        setPrivateFlags(i);
      }
    }
  }
  
  public String toString()
  {
    return "SharedUserSetting{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "/" + this.userId + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/SharedUserSetting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */