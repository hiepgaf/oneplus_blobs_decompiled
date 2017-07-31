package android.content.pm;

import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;

public class PackageUserState
{
  public int appLinkGeneration;
  public boolean blockUninstall;
  public long ceDataInode;
  public ArraySet<String> disabledComponents;
  public int domainVerificationStatus;
  public int enabled;
  public ArraySet<String> enabledComponents;
  public boolean hidden;
  public boolean installed;
  public String lastDisableAppCaller;
  public boolean notLaunched;
  public boolean stopped;
  public boolean suspended;
  
  public PackageUserState()
  {
    this.installed = true;
    this.hidden = false;
    this.suspended = false;
    this.enabled = 0;
    this.domainVerificationStatus = 0;
  }
  
  public PackageUserState(PackageUserState paramPackageUserState)
  {
    this.ceDataInode = paramPackageUserState.ceDataInode;
    this.installed = paramPackageUserState.installed;
    this.stopped = paramPackageUserState.stopped;
    this.notLaunched = paramPackageUserState.notLaunched;
    this.hidden = paramPackageUserState.hidden;
    this.suspended = paramPackageUserState.suspended;
    this.blockUninstall = paramPackageUserState.blockUninstall;
    this.enabled = paramPackageUserState.enabled;
    this.lastDisableAppCaller = paramPackageUserState.lastDisableAppCaller;
    this.domainVerificationStatus = paramPackageUserState.domainVerificationStatus;
    this.appLinkGeneration = paramPackageUserState.appLinkGeneration;
    this.disabledComponents = ArrayUtils.cloneOrNull(paramPackageUserState.disabledComponents);
    this.enabledComponents = ArrayUtils.cloneOrNull(paramPackageUserState.enabledComponents);
  }
  
  public boolean isEnabled(ComponentInfo paramComponentInfo, int paramInt)
  {
    if ((paramInt & 0x200) != 0) {
      return true;
    }
    switch (this.enabled)
    {
    }
    while (ArrayUtils.contains(this.enabledComponents, paramComponentInfo.name))
    {
      return true;
      return false;
      if ((0x8000 & paramInt) == 0) {
        return false;
      }
      if (!paramComponentInfo.applicationInfo.enabled) {
        return false;
      }
    }
    if (ArrayUtils.contains(this.disabledComponents, paramComponentInfo.name)) {
      return false;
    }
    return paramComponentInfo.enabled;
  }
  
  public boolean isInstalled(int paramInt)
  {
    return ((this.installed) && (!this.hidden)) || ((paramInt & 0x2000) != 0);
  }
  
  public boolean isMatch(ComponentInfo paramComponentInfo, int paramInt)
  {
    if (!isInstalled(paramInt)) {
      return false;
    }
    if (!isEnabled(paramComponentInfo, paramInt)) {
      return false;
    }
    if (((0x100000 & paramInt) != 0) && (!paramComponentInfo.applicationInfo.isSystemApp())) {
      return false;
    }
    int i;
    if ((0x40000 & paramInt) != 0) {
      if (paramComponentInfo.directBootAware)
      {
        i = 0;
        if ((0x80000 & paramInt) == 0) {
          break label86;
        }
      }
    }
    label86:
    for (boolean bool = paramComponentInfo.directBootAware;; bool = false)
    {
      if (i != 0) {
        break label92;
      }
      return bool;
      i = 1;
      break;
      i = 0;
      break;
    }
    label92:
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageUserState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */