package com.android.server.pm;

import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageUserState;
import android.util.ArraySet;
import android.util.SparseArray;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

abstract class PackageSettingBase
  extends SettingBase
{
  private static final PackageUserState DEFAULT_USER_STATE = new PackageUserState();
  static final int PKG_INSTALL_COMPLETE = 1;
  static final int PKG_INSTALL_INCOMPLETE = 0;
  List<String> childPackageNames;
  File codePath;
  String codePathString;
  String cpuAbiOverrideString;
  long firstInstallTime;
  boolean installPermissionsFixed;
  int installStatus = 1;
  String installerPackageName;
  boolean isOrphaned;
  PackageKeySetData keySetData = new PackageKeySetData();
  long lastUpdateTime;
  @Deprecated
  String legacyNativeLibraryPathString;
  final String name;
  Set<String> oldCodePaths;
  PackageSettingBase origPackage;
  String parentPackageName;
  String primaryCpuAbiString;
  final String realName;
  File resourcePath;
  String resourcePathString;
  String secondaryCpuAbiString;
  PackageSignatures signatures = new PackageSignatures();
  long timeStamp;
  boolean uidError;
  private final SparseArray<PackageUserState> userState = new SparseArray();
  IntentFilterVerificationInfo verificationInfo;
  int versionCode;
  String volumeUuid;
  
  PackageSettingBase(PackageSettingBase paramPackageSettingBase)
  {
    super(paramPackageSettingBase);
    this.name = paramPackageSettingBase.name;
    this.realName = paramPackageSettingBase.realName;
    this.codePath = paramPackageSettingBase.codePath;
    this.codePathString = paramPackageSettingBase.codePathString;
    this.resourcePath = paramPackageSettingBase.resourcePath;
    this.resourcePathString = paramPackageSettingBase.resourcePathString;
    this.legacyNativeLibraryPathString = paramPackageSettingBase.legacyNativeLibraryPathString;
    this.primaryCpuAbiString = paramPackageSettingBase.primaryCpuAbiString;
    this.secondaryCpuAbiString = paramPackageSettingBase.secondaryCpuAbiString;
    this.cpuAbiOverrideString = paramPackageSettingBase.cpuAbiOverrideString;
    this.timeStamp = paramPackageSettingBase.timeStamp;
    this.firstInstallTime = paramPackageSettingBase.firstInstallTime;
    this.lastUpdateTime = paramPackageSettingBase.lastUpdateTime;
    this.versionCode = paramPackageSettingBase.versionCode;
    this.uidError = paramPackageSettingBase.uidError;
    this.signatures = new PackageSignatures(paramPackageSettingBase.signatures);
    this.installPermissionsFixed = paramPackageSettingBase.installPermissionsFixed;
    this.userState.clear();
    int i = 0;
    while (i < paramPackageSettingBase.userState.size())
    {
      this.userState.put(paramPackageSettingBase.userState.keyAt(i), new PackageUserState((PackageUserState)paramPackageSettingBase.userState.valueAt(i)));
      i += 1;
    }
    this.installStatus = paramPackageSettingBase.installStatus;
    this.origPackage = paramPackageSettingBase.origPackage;
    this.installerPackageName = paramPackageSettingBase.installerPackageName;
    this.isOrphaned = paramPackageSettingBase.isOrphaned;
    this.volumeUuid = paramPackageSettingBase.volumeUuid;
    this.keySetData = new PackageKeySetData(paramPackageSettingBase.keySetData);
    this.parentPackageName = paramPackageSettingBase.parentPackageName;
    if (paramPackageSettingBase.childPackageNames != null) {}
    for (paramPackageSettingBase = new ArrayList(paramPackageSettingBase.childPackageNames);; paramPackageSettingBase = null)
    {
      this.childPackageNames = paramPackageSettingBase;
      return;
    }
  }
  
  PackageSettingBase(String paramString1, String paramString2, File paramFile1, File paramFile2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt1, int paramInt2, int paramInt3, String paramString7, List<String> paramList)
  {
    super(paramInt2, paramInt3);
    this.name = paramString1;
    this.realName = paramString2;
    this.parentPackageName = paramString7;
    if (paramList != null) {}
    for (paramString1 = new ArrayList(paramList);; paramString1 = null)
    {
      this.childPackageNames = paramString1;
      init(paramFile1, paramFile2, paramString3, paramString4, paramString5, paramString6, paramInt1);
      return;
    }
  }
  
  private PackageUserState modifyUserState(int paramInt)
  {
    PackageUserState localPackageUserState2 = (PackageUserState)this.userState.get(paramInt);
    PackageUserState localPackageUserState1 = localPackageUserState2;
    if (localPackageUserState2 == null)
    {
      localPackageUserState1 = new PackageUserState();
      this.userState.put(paramInt, localPackageUserState1);
    }
    return localPackageUserState1;
  }
  
  void addDisabledComponent(String paramString, int paramInt)
  {
    modifyUserStateComponents(paramInt, true, false).disabledComponents.add(paramString);
  }
  
  void addEnabledComponent(String paramString, int paramInt)
  {
    modifyUserStateComponents(paramInt, false, true).enabledComponents.add(paramString);
  }
  
  void clearDomainVerificationStatusForUser(int paramInt)
  {
    modifyUserState(paramInt).domainVerificationStatus = 0;
  }
  
  public void copyFrom(PackageSettingBase paramPackageSettingBase)
  {
    this.mPermissionsState.copyFrom(paramPackageSettingBase.mPermissionsState);
    this.primaryCpuAbiString = paramPackageSettingBase.primaryCpuAbiString;
    this.secondaryCpuAbiString = paramPackageSettingBase.secondaryCpuAbiString;
    this.cpuAbiOverrideString = paramPackageSettingBase.cpuAbiOverrideString;
    this.timeStamp = paramPackageSettingBase.timeStamp;
    this.firstInstallTime = paramPackageSettingBase.firstInstallTime;
    this.lastUpdateTime = paramPackageSettingBase.lastUpdateTime;
    this.signatures = paramPackageSettingBase.signatures;
    this.installPermissionsFixed = paramPackageSettingBase.installPermissionsFixed;
    this.userState.clear();
    int i = 0;
    while (i < paramPackageSettingBase.userState.size())
    {
      this.userState.put(paramPackageSettingBase.userState.keyAt(i), (PackageUserState)paramPackageSettingBase.userState.valueAt(i));
      i += 1;
    }
    this.installStatus = paramPackageSettingBase.installStatus;
    this.keySetData = paramPackageSettingBase.keySetData;
    this.verificationInfo = paramPackageSettingBase.verificationInfo;
    this.installerPackageName = paramPackageSettingBase.installerPackageName;
    this.volumeUuid = paramPackageSettingBase.volumeUuid;
  }
  
  boolean disableComponentLPw(String paramString, int paramInt)
  {
    PackageUserState localPackageUserState = modifyUserStateComponents(paramInt, true, false);
    if (localPackageUserState.enabledComponents != null) {}
    for (boolean bool = localPackageUserState.enabledComponents.remove(paramString);; bool = false) {
      return bool | localPackageUserState.disabledComponents.add(paramString);
    }
  }
  
  boolean enableComponentLPw(String paramString, int paramInt)
  {
    PackageUserState localPackageUserState = modifyUserStateComponents(paramInt, false, true);
    if (localPackageUserState.disabledComponents != null) {}
    for (boolean bool = localPackageUserState.disabledComponents.remove(paramString);; bool = false) {
      return bool | localPackageUserState.enabledComponents.add(paramString);
    }
  }
  
  boolean getBlockUninstall(int paramInt)
  {
    return readUserState(paramInt).blockUninstall;
  }
  
  long getCeDataInode(int paramInt)
  {
    return readUserState(paramInt).ceDataInode;
  }
  
  int getCurrentEnabledStateLPr(String paramString, int paramInt)
  {
    PackageUserState localPackageUserState = readUserState(paramInt);
    if ((localPackageUserState.enabledComponents != null) && (localPackageUserState.enabledComponents.contains(paramString))) {
      return 1;
    }
    if ((localPackageUserState.disabledComponents != null) && (localPackageUserState.disabledComponents.contains(paramString))) {
      return 2;
    }
    return 0;
  }
  
  ArraySet<String> getDisabledComponents(int paramInt)
  {
    return readUserState(paramInt).disabledComponents;
  }
  
  long getDomainVerificationStatusForUser(int paramInt)
  {
    PackageUserState localPackageUserState = readUserState(paramInt);
    return localPackageUserState.appLinkGeneration | localPackageUserState.domainVerificationStatus << 32;
  }
  
  int getEnabled(int paramInt)
  {
    return readUserState(paramInt).enabled;
  }
  
  ArraySet<String> getEnabledComponents(int paramInt)
  {
    return readUserState(paramInt).enabledComponents;
  }
  
  boolean getHidden(int paramInt)
  {
    return readUserState(paramInt).hidden;
  }
  
  public int getInstallStatus()
  {
    return this.installStatus;
  }
  
  boolean getInstalled(int paramInt)
  {
    return readUserState(paramInt).installed;
  }
  
  public String getInstallerPackageName()
  {
    return this.installerPackageName;
  }
  
  IntentFilterVerificationInfo getIntentFilterVerificationInfo()
  {
    return this.verificationInfo;
  }
  
  String getLastDisabledAppCaller(int paramInt)
  {
    return readUserState(paramInt).lastDisableAppCaller;
  }
  
  boolean getNotLaunched(int paramInt)
  {
    return readUserState(paramInt).notLaunched;
  }
  
  boolean getStopped(int paramInt)
  {
    return readUserState(paramInt).stopped;
  }
  
  boolean getSuspended(int paramInt)
  {
    return readUserState(paramInt).suspended;
  }
  
  public String getVolumeUuid()
  {
    return this.volumeUuid;
  }
  
  void init(File paramFile1, File paramFile2, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    this.codePath = paramFile1;
    this.codePathString = paramFile1.toString();
    this.resourcePath = paramFile2;
    this.resourcePathString = paramFile2.toString();
    this.legacyNativeLibraryPathString = paramString1;
    this.primaryCpuAbiString = paramString2;
    this.secondaryCpuAbiString = paramString3;
    this.cpuAbiOverrideString = paramString4;
    this.versionCode = paramInt;
  }
  
  boolean isAnyInstalled(int[] paramArrayOfInt)
  {
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (readUserState(paramArrayOfInt[i]).installed) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  PackageUserState modifyUserStateComponents(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    PackageUserState localPackageUserState = modifyUserState(paramInt);
    if ((paramBoolean1) && (localPackageUserState.disabledComponents == null)) {
      localPackageUserState.disabledComponents = new ArraySet(1);
    }
    if ((paramBoolean2) && (localPackageUserState.enabledComponents == null)) {
      localPackageUserState.enabledComponents = new ArraySet(1);
    }
    return localPackageUserState;
  }
  
  int[] queryInstalledUsers(int[] paramArrayOfInt, boolean paramBoolean)
  {
    int m = 0;
    int j = 0;
    int n = paramArrayOfInt.length;
    int i = 0;
    int k;
    while (i < n)
    {
      k = j;
      if (getInstalled(paramArrayOfInt[i]) == paramBoolean) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    int[] arrayOfInt = new int[j];
    j = 0;
    n = paramArrayOfInt.length;
    i = m;
    while (i < n)
    {
      m = paramArrayOfInt[i];
      k = j;
      if (getInstalled(m) == paramBoolean)
      {
        arrayOfInt[j] = m;
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return arrayOfInt;
  }
  
  public PackageUserState readUserState(int paramInt)
  {
    PackageUserState localPackageUserState = (PackageUserState)this.userState.get(paramInt);
    if (localPackageUserState != null) {
      return localPackageUserState;
    }
    return DEFAULT_USER_STATE;
  }
  
  void removeUser(int paramInt)
  {
    this.userState.delete(paramInt);
  }
  
  boolean restoreComponentLPw(String paramString, int paramInt)
  {
    PackageUserState localPackageUserState = modifyUserStateComponents(paramInt, true, true);
    boolean bool1;
    if (localPackageUserState.disabledComponents != null)
    {
      bool1 = localPackageUserState.disabledComponents.remove(paramString);
      if (localPackageUserState.enabledComponents == null) {
        break label56;
      }
    }
    label56:
    for (boolean bool2 = localPackageUserState.enabledComponents.remove(paramString);; bool2 = false)
    {
      return bool1 | bool2;
      bool1 = false;
      break;
    }
  }
  
  void setBlockUninstall(boolean paramBoolean, int paramInt)
  {
    modifyUserState(paramInt).blockUninstall = paramBoolean;
  }
  
  void setCeDataInode(long paramLong, int paramInt)
  {
    modifyUserState(paramInt).ceDataInode = paramLong;
  }
  
  void setDisabledComponents(ArraySet<String> paramArraySet, int paramInt)
  {
    modifyUserState(paramInt).disabledComponents = paramArraySet;
  }
  
  void setDisabledComponentsCopy(ArraySet<String> paramArraySet, int paramInt)
  {
    ArraySet localArraySet = null;
    PackageUserState localPackageUserState = modifyUserState(paramInt);
    if (paramArraySet != null) {
      localArraySet = new ArraySet(paramArraySet);
    }
    localPackageUserState.disabledComponents = localArraySet;
  }
  
  void setDomainVerificationStatusForUser(int paramInt1, int paramInt2, int paramInt3)
  {
    PackageUserState localPackageUserState = modifyUserState(paramInt3);
    localPackageUserState.domainVerificationStatus = paramInt1;
    if (paramInt1 == 2) {
      localPackageUserState.appLinkGeneration = paramInt2;
    }
  }
  
  void setEnabled(int paramInt1, int paramInt2, String paramString)
  {
    PackageUserState localPackageUserState = modifyUserState(paramInt2);
    localPackageUserState.enabled = paramInt1;
    localPackageUserState.lastDisableAppCaller = paramString;
  }
  
  void setEnabledComponents(ArraySet<String> paramArraySet, int paramInt)
  {
    modifyUserState(paramInt).enabledComponents = paramArraySet;
  }
  
  void setEnabledComponentsCopy(ArraySet<String> paramArraySet, int paramInt)
  {
    ArraySet localArraySet = null;
    PackageUserState localPackageUserState = modifyUserState(paramInt);
    if (paramArraySet != null) {
      localArraySet = new ArraySet(paramArraySet);
    }
    localPackageUserState.enabledComponents = localArraySet;
  }
  
  void setHidden(boolean paramBoolean, int paramInt)
  {
    modifyUserState(paramInt).hidden = paramBoolean;
  }
  
  public void setInstallStatus(int paramInt)
  {
    this.installStatus = paramInt;
  }
  
  void setInstalled(boolean paramBoolean, int paramInt)
  {
    modifyUserState(paramInt).installed = paramBoolean;
  }
  
  public void setInstallerPackageName(String paramString)
  {
    this.installerPackageName = paramString;
  }
  
  void setIntentFilterVerificationInfo(IntentFilterVerificationInfo paramIntentFilterVerificationInfo)
  {
    this.verificationInfo = paramIntentFilterVerificationInfo;
  }
  
  void setNotLaunched(boolean paramBoolean, int paramInt)
  {
    modifyUserState(paramInt).notLaunched = paramBoolean;
  }
  
  void setStopped(boolean paramBoolean, int paramInt)
  {
    modifyUserState(paramInt).stopped = paramBoolean;
  }
  
  void setSuspended(boolean paramBoolean, int paramInt)
  {
    modifyUserState(paramInt).suspended = paramBoolean;
  }
  
  public void setTimeStamp(long paramLong)
  {
    this.timeStamp = paramLong;
  }
  
  void setUserState(int paramInt1, long paramLong, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, String paramString, ArraySet<String> paramArraySet1, ArraySet<String> paramArraySet2, boolean paramBoolean6, int paramInt3, int paramInt4)
  {
    PackageUserState localPackageUserState = modifyUserState(paramInt1);
    localPackageUserState.ceDataInode = paramLong;
    localPackageUserState.enabled = paramInt2;
    localPackageUserState.installed = paramBoolean1;
    localPackageUserState.stopped = paramBoolean2;
    localPackageUserState.notLaunched = paramBoolean3;
    localPackageUserState.hidden = paramBoolean4;
    localPackageUserState.suspended = paramBoolean5;
    localPackageUserState.lastDisableAppCaller = paramString;
    localPackageUserState.enabledComponents = paramArraySet1;
    localPackageUserState.disabledComponents = paramArraySet2;
    localPackageUserState.blockUninstall = paramBoolean6;
    localPackageUserState.domainVerificationStatus = paramInt3;
    localPackageUserState.appLinkGeneration = paramInt4;
  }
  
  public void setVolumeUuid(String paramString)
  {
    this.volumeUuid = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/PackageSettingBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */