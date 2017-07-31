package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IPackageManager
  extends IInterface
{
  public abstract boolean activitySupportsIntent(ComponentName paramComponentName, Intent paramIntent, String paramString)
    throws RemoteException;
  
  public abstract void addCrossProfileIntentFilter(IntentFilter paramIntentFilter, String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void addOnPermissionsChangeListener(IOnPermissionsChangeListener paramIOnPermissionsChangeListener)
    throws RemoteException;
  
  public abstract boolean addPermission(PermissionInfo paramPermissionInfo)
    throws RemoteException;
  
  public abstract boolean addPermissionAsync(PermissionInfo paramPermissionInfo)
    throws RemoteException;
  
  public abstract void addPersistentPreferredActivity(IntentFilter paramIntentFilter, ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract void addPreferredActivity(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
    throws RemoteException;
  
  public abstract boolean canForwardTo(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract String[] canonicalToCurrentPackageNames(String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void checkPackageStartable(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int checkPermission(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract int checkSignatures(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract int checkUidPermission(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int checkUidSignatures(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void clearApplicationProfileData(String paramString)
    throws RemoteException;
  
  public abstract void clearApplicationUserData(String paramString, IPackageDataObserver paramIPackageDataObserver, int paramInt)
    throws RemoteException;
  
  public abstract void clearCrossProfileIntentFilters(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void clearPackagePersistentPreferredActivities(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void clearPackagePreferredActivities(String paramString)
    throws RemoteException;
  
  public abstract String[] currentToCanonicalPackageNames(String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void deleteApplicationCacheFiles(String paramString, IPackageDataObserver paramIPackageDataObserver)
    throws RemoteException;
  
  public abstract void deleteApplicationCacheFilesAsUser(String paramString, int paramInt, IPackageDataObserver paramIPackageDataObserver)
    throws RemoteException;
  
  public abstract void deletePackage(String paramString, IPackageDeleteObserver2 paramIPackageDeleteObserver2, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void deletePackageAsUser(String paramString, IPackageDeleteObserver paramIPackageDeleteObserver, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void dumpProfiles(String paramString)
    throws RemoteException;
  
  public abstract void enterSafeMode()
    throws RemoteException;
  
  public abstract void extendVerificationTimeout(int paramInt1, int paramInt2, long paramLong)
    throws RemoteException;
  
  public abstract void finishPackageInstall(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void flushPackageRestrictionsAsUser(int paramInt)
    throws RemoteException;
  
  public abstract void forceDexOpt(String paramString)
    throws RemoteException;
  
  public abstract void freeStorage(String paramString, long paramLong, IntentSender paramIntentSender)
    throws RemoteException;
  
  public abstract void freeStorageAndNotify(String paramString, long paramLong, IPackageDataObserver paramIPackageDataObserver)
    throws RemoteException;
  
  public abstract ActivityInfo getActivityInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice getAllIntentFilters(String paramString)
    throws RemoteException;
  
  public abstract List<String> getAllPackages()
    throws RemoteException;
  
  public abstract ParceledListSlice getAllPermissionGroups(int paramInt)
    throws RemoteException;
  
  public abstract String[] getAppOpPermissionPackages(String paramString)
    throws RemoteException;
  
  public abstract int getApplicationEnabledSetting(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean getApplicationHiddenSettingAsUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ApplicationInfo getApplicationInfo(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean getBlockUninstallForUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getComponentEnabledSetting(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract byte[] getDefaultAppsBackup(int paramInt)
    throws RemoteException;
  
  public abstract String getDefaultBrowserPackageName(int paramInt)
    throws RemoteException;
  
  public abstract byte[] getEphemeralApplicationCookie(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract Bitmap getEphemeralApplicationIcon(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getEphemeralApplications(int paramInt)
    throws RemoteException;
  
  public abstract int getFlagsForUid(int paramInt)
    throws RemoteException;
  
  public abstract ComponentName getHomeActivities(List<ResolveInfo> paramList)
    throws RemoteException;
  
  public abstract int getInstallLocation()
    throws RemoteException;
  
  public abstract ParceledListSlice getInstalledApplications(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice getInstalledPackages(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract String getInstallerPackageName(String paramString)
    throws RemoteException;
  
  public abstract InstrumentationInfo getInstrumentationInfo(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract byte[] getIntentFilterVerificationBackup(int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getIntentFilterVerifications(String paramString)
    throws RemoteException;
  
  public abstract int getIntentVerificationStatus(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract KeySet getKeySetByAlias(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract ResolveInfo getLastChosenActivity(Intent paramIntent, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getMoveStatus(int paramInt)
    throws RemoteException;
  
  public abstract String getNameForUid(int paramInt)
    throws RemoteException;
  
  public abstract int[] getPackageGids(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract PackageInfo getPackageInfo(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract IPackageInstaller getPackageInstaller()
    throws RemoteException;
  
  public abstract void getPackageSizeInfo(String paramString, int paramInt, IPackageStatsObserver paramIPackageStatsObserver)
    throws RemoteException;
  
  public abstract int getPackageUid(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract String[] getPackagesForUid(int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getPackagesHoldingPermissions(String[] paramArrayOfString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract String getPermissionControllerPackageName()
    throws RemoteException;
  
  public abstract int getPermissionFlags(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract byte[] getPermissionGrantBackup(int paramInt)
    throws RemoteException;
  
  public abstract PermissionGroupInfo getPermissionGroupInfo(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract PermissionInfo getPermissionInfo(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getPersistentApplications(int paramInt)
    throws RemoteException;
  
  public abstract int getPreferredActivities(List<IntentFilter> paramList, List<ComponentName> paramList1, String paramString)
    throws RemoteException;
  
  public abstract byte[] getPreferredActivityBackup(int paramInt)
    throws RemoteException;
  
  public abstract List<String> getPreviousCodePaths(String paramString)
    throws RemoteException;
  
  public abstract int getPrivateFlagsForUid(int paramInt)
    throws RemoteException;
  
  public abstract ProviderInfo getProviderInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ActivityInfo getReceiverInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ServiceInfo getServiceInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract String getServicesSystemSharedLibraryPackageName()
    throws RemoteException;
  
  public abstract String getSharedSystemSharedLibraryPackageName()
    throws RemoteException;
  
  public abstract KeySet getSigningKeySet(String paramString)
    throws RemoteException;
  
  public abstract ParceledListSlice getSystemAvailableFeatures()
    throws RemoteException;
  
  public abstract String[] getSystemSharedLibraryNames()
    throws RemoteException;
  
  public abstract int getUidForSharedUser(String paramString)
    throws RemoteException;
  
  public abstract VerifierDeviceIdentity getVerifierDeviceIdentity()
    throws RemoteException;
  
  public abstract void grantDefaultPermissionsToEnabledCarrierApps(String[] paramArrayOfString, int paramInt)
    throws RemoteException;
  
  public abstract void grantRuntimePermission(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void grantSystemAppPermissions(int paramInt)
    throws RemoteException;
  
  public abstract boolean hasSystemFeature(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean hasSystemUidErrors()
    throws RemoteException;
  
  public abstract boolean inCompatConfigList(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract int installExistingPackageAsUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void installPackageAsUser(String paramString1, IPackageInstallObserver2 paramIPackageInstallObserver2, int paramInt1, String paramString2, int paramInt2)
    throws RemoteException;
  
  public abstract boolean isEphemeralApplication(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isFirstBoot()
    throws RemoteException;
  
  public abstract boolean isOnlyCoreApps()
    throws RemoteException;
  
  public abstract boolean isPackageAvailable(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isPackageDeviceAdminOnAnyUser(String paramString)
    throws RemoteException;
  
  public abstract boolean isPackageSignedByKeySet(String paramString, KeySet paramKeySet)
    throws RemoteException;
  
  public abstract boolean isPackageSignedByKeySetExactly(String paramString, KeySet paramKeySet)
    throws RemoteException;
  
  public abstract boolean isPackageSuspendedForUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isPermissionEnforced(String paramString)
    throws RemoteException;
  
  public abstract boolean isPermissionRevokedByPolicy(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract boolean isProtectedBroadcast(String paramString)
    throws RemoteException;
  
  public abstract boolean isSafeMode()
    throws RemoteException;
  
  public abstract boolean isStorageLow()
    throws RemoteException;
  
  public abstract boolean isUidPrivileged(int paramInt)
    throws RemoteException;
  
  public abstract boolean isUpgrade()
    throws RemoteException;
  
  public abstract void logAppProcessStartIfNeeded(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2)
    throws RemoteException;
  
  public abstract int movePackage(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract int movePrimaryStorage(String paramString)
    throws RemoteException;
  
  public abstract PackageCleanItem nextPackageToClean(PackageCleanItem paramPackageCleanItem)
    throws RemoteException;
  
  public abstract void notifyPackageUse(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean performDexOpt(String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract boolean performDexOptIfNeeded(String paramString)
    throws RemoteException;
  
  public abstract boolean performDexOptMode(String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void performFstrimIfNeeded()
    throws RemoteException;
  
  public abstract ParceledListSlice queryContentProviders(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice queryInstrumentation(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice queryIntentActivities(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice queryIntentActivityOptions(ComponentName paramComponentName, Intent[] paramArrayOfIntent, String[] paramArrayOfString, Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice queryIntentContentProviders(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice queryIntentReceivers(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice queryIntentServices(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ParceledListSlice queryPermissionsByGroup(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void querySyncProviders(List<String> paramList, List<ProviderInfo> paramList1)
    throws RemoteException;
  
  public abstract void registerMoveCallback(IPackageMoveObserver paramIPackageMoveObserver)
    throws RemoteException;
  
  public abstract void removeOnPermissionsChangeListener(IOnPermissionsChangeListener paramIOnPermissionsChangeListener)
    throws RemoteException;
  
  public abstract void removePermission(String paramString)
    throws RemoteException;
  
  public abstract void replacePreferredActivity(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
    throws RemoteException;
  
  public abstract void resetApplicationPermissions(int paramInt)
    throws RemoteException;
  
  public abstract void resetApplicationPreferences(int paramInt)
    throws RemoteException;
  
  public abstract void resetRuntimePermissions()
    throws RemoteException;
  
  public abstract ProviderInfo resolveContentProvider(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ResolveInfo resolveIntent(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ResolveInfo resolveService(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void restoreDefaultApps(byte[] paramArrayOfByte, int paramInt)
    throws RemoteException;
  
  public abstract void restoreIntentFilterVerification(byte[] paramArrayOfByte, int paramInt)
    throws RemoteException;
  
  public abstract void restorePermissionGrants(byte[] paramArrayOfByte, int paramInt)
    throws RemoteException;
  
  public abstract void restorePreferredActivities(byte[] paramArrayOfByte, int paramInt)
    throws RemoteException;
  
  public abstract void revokeRuntimePermission(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void setApplicationEnabledSetting(String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
    throws RemoteException;
  
  public abstract boolean setApplicationHiddenSettingAsUser(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract boolean setBlockUninstallForUser(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setComponentEnabledSetting(ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract boolean setDefaultBrowserPackageName(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean setEphemeralApplicationCookie(String paramString, byte[] paramArrayOfByte, int paramInt)
    throws RemoteException;
  
  public abstract void setHomeActivity(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean setInstallLocation(int paramInt)
    throws RemoteException;
  
  public abstract void setInstallerPackageName(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setLastChosenActivity(Intent paramIntent, String paramString, int paramInt1, IntentFilter paramIntentFilter, int paramInt2, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void setPackageStoppedState(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract String[] setPackagesSuspendedAsUser(String[] paramArrayOfString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setPermissionEnforced(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setRequiredForSystemUser(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean shouldShowRequestPermissionRationale(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void systemReady()
    throws RemoteException;
  
  public abstract void unregisterMoveCallback(IPackageMoveObserver paramIPackageMoveObserver)
    throws RemoteException;
  
  public abstract void updateExternalMediaStatus(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract boolean updateIntentVerificationStatus(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void updatePackagesIfNeeded()
    throws RemoteException;
  
  public abstract void updatePermissionFlags(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void updatePermissionFlagsForAllApps(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void verifyIntentFilter(int paramInt1, int paramInt2, List<String> paramList)
    throws RemoteException;
  
  public abstract void verifyPendingInstall(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPackageManager
  {
    private static final String DESCRIPTOR = "android.content.pm.IPackageManager";
    static final int TRANSACTION_activitySupportsIntent = 14;
    static final int TRANSACTION_addCrossProfileIntentFilter = 72;
    static final int TRANSACTION_addOnPermissionsChangeListener = 151;
    static final int TRANSACTION_addPermission = 20;
    static final int TRANSACTION_addPermissionAsync = 122;
    static final int TRANSACTION_addPersistentPreferredActivity = 70;
    static final int TRANSACTION_addPreferredActivity = 66;
    static final int TRANSACTION_canForwardTo = 41;
    static final int TRANSACTION_canonicalToCurrentPackageNames = 7;
    static final int TRANSACTION_checkPackageStartable = 1;
    static final int TRANSACTION_checkPermission = 18;
    static final int TRANSACTION_checkSignatures = 30;
    static final int TRANSACTION_checkUidPermission = 19;
    static final int TRANSACTION_checkUidSignatures = 31;
    static final int TRANSACTION_clearApplicationProfileData = 98;
    static final int TRANSACTION_clearApplicationUserData = 97;
    static final int TRANSACTION_clearCrossProfileIntentFilters = 73;
    static final int TRANSACTION_clearPackagePersistentPreferredActivities = 71;
    static final int TRANSACTION_clearPackagePreferredActivities = 68;
    static final int TRANSACTION_currentToCanonicalPackageNames = 6;
    static final int TRANSACTION_deleteApplicationCacheFiles = 95;
    static final int TRANSACTION_deleteApplicationCacheFilesAsUser = 96;
    static final int TRANSACTION_deletePackage = 61;
    static final int TRANSACTION_deletePackageAsUser = 60;
    static final int TRANSACTION_dumpProfiles = 113;
    static final int TRANSACTION_enterSafeMode = 103;
    static final int TRANSACTION_extendVerificationTimeout = 127;
    static final int TRANSACTION_finishPackageInstall = 58;
    static final int TRANSACTION_flushPackageRestrictionsAsUser = 91;
    static final int TRANSACTION_forceDexOpt = 114;
    static final int TRANSACTION_freeStorage = 94;
    static final int TRANSACTION_freeStorageAndNotify = 93;
    static final int TRANSACTION_getActivityInfo = 13;
    static final int TRANSACTION_getAllIntentFilters = 132;
    static final int TRANSACTION_getAllPackages = 32;
    static final int TRANSACTION_getAllPermissionGroups = 11;
    static final int TRANSACTION_getAppOpPermissionPackages = 39;
    static final int TRANSACTION_getApplicationEnabledSetting = 89;
    static final int TRANSACTION_getApplicationHiddenSettingAsUser = 143;
    static final int TRANSACTION_getApplicationInfo = 12;
    static final int TRANSACTION_getBlockUninstallForUser = 146;
    static final int TRANSACTION_getComponentEnabledSetting = 87;
    static final int TRANSACTION_getDefaultAppsBackup = 78;
    static final int TRANSACTION_getDefaultBrowserPackageName = 134;
    static final int TRANSACTION_getEphemeralApplicationCookie = 157;
    static final int TRANSACTION_getEphemeralApplicationIcon = 159;
    static final int TRANSACTION_getEphemeralApplications = 156;
    static final int TRANSACTION_getFlagsForUid = 36;
    static final int TRANSACTION_getHomeActivities = 84;
    static final int TRANSACTION_getInstallLocation = 124;
    static final int TRANSACTION_getInstalledApplications = 50;
    static final int TRANSACTION_getInstalledPackages = 48;
    static final int TRANSACTION_getInstallerPackageName = 62;
    static final int TRANSACTION_getInstrumentationInfo = 55;
    static final int TRANSACTION_getIntentFilterVerificationBackup = 80;
    static final int TRANSACTION_getIntentFilterVerifications = 131;
    static final int TRANSACTION_getIntentVerificationStatus = 129;
    static final int TRANSACTION_getKeySetByAlias = 147;
    static final int TRANSACTION_getLastChosenActivity = 64;
    static final int TRANSACTION_getMoveStatus = 117;
    static final int TRANSACTION_getNameForUid = 34;
    static final int TRANSACTION_getPackageGids = 5;
    static final int TRANSACTION_getPackageInfo = 3;
    static final int TRANSACTION_getPackageInstaller = 144;
    static final int TRANSACTION_getPackageSizeInfo = 99;
    static final int TRANSACTION_getPackageUid = 4;
    static final int TRANSACTION_getPackagesForUid = 33;
    static final int TRANSACTION_getPackagesHoldingPermissions = 49;
    static final int TRANSACTION_getPermissionControllerPackageName = 155;
    static final int TRANSACTION_getPermissionFlags = 25;
    static final int TRANSACTION_getPermissionGrantBackup = 82;
    static final int TRANSACTION_getPermissionGroupInfo = 10;
    static final int TRANSACTION_getPermissionInfo = 8;
    static final int TRANSACTION_getPersistentApplications = 51;
    static final int TRANSACTION_getPreferredActivities = 69;
    static final int TRANSACTION_getPreferredActivityBackup = 76;
    static final int TRANSACTION_getPreviousCodePaths = 165;
    static final int TRANSACTION_getPrivateFlagsForUid = 37;
    static final int TRANSACTION_getProviderInfo = 17;
    static final int TRANSACTION_getReceiverInfo = 15;
    static final int TRANSACTION_getServiceInfo = 16;
    static final int TRANSACTION_getServicesSystemSharedLibraryPackageName = 162;
    static final int TRANSACTION_getSharedSystemSharedLibraryPackageName = 163;
    static final int TRANSACTION_getSigningKeySet = 148;
    static final int TRANSACTION_getSystemAvailableFeatures = 101;
    static final int TRANSACTION_getSystemSharedLibraryNames = 100;
    static final int TRANSACTION_getUidForSharedUser = 35;
    static final int TRANSACTION_getVerifierDeviceIdentity = 135;
    static final int TRANSACTION_grantDefaultPermissionsToEnabledCarrierApps = 153;
    static final int TRANSACTION_grantRuntimePermission = 22;
    static final int TRANSACTION_grantSystemAppPermissions = 166;
    static final int TRANSACTION_hasSystemFeature = 102;
    static final int TRANSACTION_hasSystemUidErrors = 106;
    static final int TRANSACTION_inCompatConfigList = 168;
    static final int TRANSACTION_installExistingPackageAsUser = 125;
    static final int TRANSACTION_installPackageAsUser = 57;
    static final int TRANSACTION_isEphemeralApplication = 160;
    static final int TRANSACTION_isFirstBoot = 136;
    static final int TRANSACTION_isOnlyCoreApps = 137;
    static final int TRANSACTION_isPackageAvailable = 2;
    static final int TRANSACTION_isPackageDeviceAdminOnAnyUser = 164;
    static final int TRANSACTION_isPackageSignedByKeySet = 149;
    static final int TRANSACTION_isPackageSignedByKeySetExactly = 150;
    static final int TRANSACTION_isPackageSuspendedForUser = 75;
    static final int TRANSACTION_isPermissionEnforced = 140;
    static final int TRANSACTION_isPermissionRevokedByPolicy = 154;
    static final int TRANSACTION_isProtectedBroadcast = 29;
    static final int TRANSACTION_isSafeMode = 104;
    static final int TRANSACTION_isStorageLow = 141;
    static final int TRANSACTION_isUidPrivileged = 38;
    static final int TRANSACTION_isUpgrade = 138;
    static final int TRANSACTION_logAppProcessStartIfNeeded = 90;
    static final int TRANSACTION_movePackage = 120;
    static final int TRANSACTION_movePrimaryStorage = 121;
    static final int TRANSACTION_nextPackageToClean = 116;
    static final int TRANSACTION_notifyPackageUse = 109;
    static final int TRANSACTION_performDexOpt = 111;
    static final int TRANSACTION_performDexOptIfNeeded = 110;
    static final int TRANSACTION_performDexOptMode = 112;
    static final int TRANSACTION_performFstrimIfNeeded = 107;
    static final int TRANSACTION_queryContentProviders = 54;
    static final int TRANSACTION_queryInstrumentation = 56;
    static final int TRANSACTION_queryIntentActivities = 42;
    static final int TRANSACTION_queryIntentActivityOptions = 43;
    static final int TRANSACTION_queryIntentContentProviders = 47;
    static final int TRANSACTION_queryIntentReceivers = 44;
    static final int TRANSACTION_queryIntentServices = 46;
    static final int TRANSACTION_queryPermissionsByGroup = 9;
    static final int TRANSACTION_querySyncProviders = 53;
    static final int TRANSACTION_registerMoveCallback = 118;
    static final int TRANSACTION_removeOnPermissionsChangeListener = 152;
    static final int TRANSACTION_removePermission = 21;
    static final int TRANSACTION_replacePreferredActivity = 67;
    static final int TRANSACTION_resetApplicationPermissions = 167;
    static final int TRANSACTION_resetApplicationPreferences = 63;
    static final int TRANSACTION_resetRuntimePermissions = 24;
    static final int TRANSACTION_resolveContentProvider = 52;
    static final int TRANSACTION_resolveIntent = 40;
    static final int TRANSACTION_resolveService = 45;
    static final int TRANSACTION_restoreDefaultApps = 79;
    static final int TRANSACTION_restoreIntentFilterVerification = 81;
    static final int TRANSACTION_restorePermissionGrants = 83;
    static final int TRANSACTION_restorePreferredActivities = 77;
    static final int TRANSACTION_revokeRuntimePermission = 23;
    static final int TRANSACTION_setApplicationEnabledSetting = 88;
    static final int TRANSACTION_setApplicationHiddenSettingAsUser = 142;
    static final int TRANSACTION_setBlockUninstallForUser = 145;
    static final int TRANSACTION_setComponentEnabledSetting = 86;
    static final int TRANSACTION_setDefaultBrowserPackageName = 133;
    static final int TRANSACTION_setEphemeralApplicationCookie = 158;
    static final int TRANSACTION_setHomeActivity = 85;
    static final int TRANSACTION_setInstallLocation = 123;
    static final int TRANSACTION_setInstallerPackageName = 59;
    static final int TRANSACTION_setLastChosenActivity = 65;
    static final int TRANSACTION_setPackageStoppedState = 92;
    static final int TRANSACTION_setPackagesSuspendedAsUser = 74;
    static final int TRANSACTION_setPermissionEnforced = 139;
    static final int TRANSACTION_setRequiredForSystemUser = 161;
    static final int TRANSACTION_shouldShowRequestPermissionRationale = 28;
    static final int TRANSACTION_systemReady = 105;
    static final int TRANSACTION_unregisterMoveCallback = 119;
    static final int TRANSACTION_updateExternalMediaStatus = 115;
    static final int TRANSACTION_updateIntentVerificationStatus = 130;
    static final int TRANSACTION_updatePackagesIfNeeded = 108;
    static final int TRANSACTION_updatePermissionFlags = 26;
    static final int TRANSACTION_updatePermissionFlagsForAllApps = 27;
    static final int TRANSACTION_verifyIntentFilter = 128;
    static final int TRANSACTION_verifyPendingInstall = 126;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IPackageManager");
    }
    
    public static IPackageManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IPackageManager");
      if ((localIInterface != null) && ((localIInterface instanceof IPackageManager))) {
        return (IPackageManager)localIInterface;
      }
      return new Proxy(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject1;
      label1930:
      Object localObject2;
      label1986:
      label2024:
      label2030:
      label2104:
      label2181:
      label2258:
      label2386:
      label3017:
      label3096:
      label3174:
      Object localObject3;
      label3248:
      label3300:
      label3306:
      label3387:
      label3468:
      label3549:
      label3630:
      label4043:
      label4387:
      label4454:
      label4506:
      label4512:
      label4609:
      label4707:
      label4865:
      boolean bool2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.pm.IPackageManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        checkPackageStartable(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isPackageAvailable(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPackageInfo(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getPackageUid(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPackageGids(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = currentToCanonicalPackageNames(paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = canonicalToCurrentPackageNames(paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPermissionInfo(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 9: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = queryPermissionsByGroup(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 10: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPermissionGroupInfo(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 11: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getAllPermissionGroups(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 12: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getApplicationInfo(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 13: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getActivityInfo((ComponentName)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1930;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 14: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2024;
          }
          localObject2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          bool1 = activitySupportsIntent((ComponentName)localObject1, (Intent)localObject2, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2030;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label1986;
        }
      case 15: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getReceiverInfo((ComponentName)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2104;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 16: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getServiceInfo((ComponentName)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2181;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 17: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getProviderInfo((ComponentName)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2258;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 18: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = checkPermission(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = checkUidPermission(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (PermissionInfo)PermissionInfo.CREATOR.createFromParcel(paramParcel1);
          bool1 = addPermission(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2386;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 21: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        removePermission(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        grantRuntimePermission(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        revokeRuntimePermission(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        resetRuntimePermissions();
        paramParcel2.writeNoException();
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getPermissionFlags(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        updatePermissionFlags(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        updatePermissionFlagsForAllApps(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = shouldShowRequestPermissionRationale(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isProtectedBroadcast(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 30: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = checkSignatures(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 31: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = checkUidSignatures(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getAllPackages();
        paramParcel2.writeNoException();
        paramParcel2.writeStringList(paramParcel1);
        return true;
      case 33: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPackagesForUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 34: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getNameForUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 35: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getUidForSharedUser(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 36: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getFlagsForUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 37: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getPrivateFlagsForUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 38: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isUidPrivileged(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 39: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getAppOpPermissionPackages(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 40: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = resolveIntent((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3017;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 41: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          bool1 = canForwardTo((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool1) {
            break label3096;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 42: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = queryIntentActivities((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3174;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 43: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject3 = (Intent[])paramParcel1.createTypedArray(Intent.CREATOR);
          String[] arrayOfString = paramParcel1.createStringArray();
          if (paramParcel1.readInt() == 0) {
            break label3300;
          }
          localObject2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = queryIntentActivityOptions((ComponentName)localObject1, (Intent[])localObject3, arrayOfString, (Intent)localObject2, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3306;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label3248;
          paramParcel2.writeInt(0);
        }
      case 44: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = queryIntentReceivers((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3387;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 45: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = resolveService((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3468;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 46: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = queryIntentServices((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3549;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 47: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = queryIntentContentProviders((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3630;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 48: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getInstalledPackages(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 49: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPackagesHoldingPermissions(paramParcel1.createStringArray(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 50: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getInstalledApplications(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 51: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPersistentApplications(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 52: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = resolveContentProvider(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 53: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.createStringArrayList();
        paramParcel1 = paramParcel1.createTypedArrayList(ProviderInfo.CREATOR);
        querySyncProviders((List)localObject1, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeStringList((List)localObject1);
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 54: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = queryContentProviders(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 55: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getInstrumentationInfo((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label4043;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 56: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = queryInstrumentation(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 57: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        installPackageAsUser(paramParcel1.readString(), IPackageInstallObserver2.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 58: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          finishPackageInstall(paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 59: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        setInstallerPackageName(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 60: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        deletePackageAsUser(paramParcel1.readString(), IPackageDeleteObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 61: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        deletePackage(paramParcel1.readString(), IPackageDeleteObserver2.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 62: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getInstallerPackageName(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 63: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        resetApplicationPreferences(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 64: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getLastChosenActivity((Intent)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label4387;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 65: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          localObject3 = paramParcel1.readString();
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label4506;
          }
          localObject2 = (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1);
          paramInt2 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label4512;
          }
        }
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setLastChosenActivity((Intent)localObject1, (String)localObject3, paramInt1, (IntentFilter)localObject2, paramInt2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label4454;
        }
      case 66: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          localObject3 = (ComponentName[])paramParcel1.createTypedArray(ComponentName.CREATOR);
          if (paramParcel1.readInt() == 0) {
            break label4609;
          }
        }
        for (localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          addPreferredActivity((IntentFilter)localObject1, paramInt1, (ComponentName[])localObject3, (ComponentName)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 67: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          localObject3 = (ComponentName[])paramParcel1.createTypedArray(ComponentName.CREATOR);
          if (paramParcel1.readInt() == 0) {
            break label4707;
          }
        }
        for (localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          replacePreferredActivity((IntentFilter)localObject1, paramInt1, (ComponentName[])localObject3, (ComponentName)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 68: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        clearPackagePreferredActivities(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 69: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = new ArrayList();
        localObject2 = new ArrayList();
        paramInt1 = getPreferredActivities((List)localObject1, (List)localObject2, paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        paramParcel2.writeTypedList((List)localObject1);
        paramParcel2.writeTypedList((List)localObject2);
        return true;
      case 70: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label4865;
          }
        }
        for (localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          addPersistentPreferredActivity((IntentFilter)localObject1, (ComponentName)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 71: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        clearPackagePersistentPreferredActivities(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 72: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          addCrossProfileIntentFilter((IntentFilter)localObject1, paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 73: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        clearCrossProfileIntentFilters(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 74: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.createStringArray();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          paramParcel1 = setPackagesSuspendedAsUser((String[])localObject1, bool1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeStringArray(paramParcel1);
          return true;
        }
      case 75: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isPackageSuspendedForUser(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 76: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPreferredActivityBackup(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 77: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        restorePreferredActivities(paramParcel1.createByteArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 78: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getDefaultAppsBackup(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 79: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        restoreDefaultApps(paramParcel1.createByteArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 80: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getIntentFilterVerificationBackup(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 81: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        restoreIntentFilterVerification(paramParcel1.createByteArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 82: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPermissionGrantBackup(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 83: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        restorePermissionGrants(paramParcel1.createByteArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 84: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = new ArrayList();
        localObject1 = getHomeActivities(paramParcel1);
        paramParcel2.writeNoException();
        if (localObject1 != null)
        {
          paramParcel2.writeInt(1);
          ((ComponentName)localObject1).writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          paramParcel2.writeTypedList(paramParcel1);
          return true;
          paramParcel2.writeInt(0);
        }
      case 85: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setHomeActivity((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 86: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setComponentEnabledSetting((ComponentName)localObject1, paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 87: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramInt1 = getComponentEnabledSetting((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 88: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        setApplicationEnabledSetting(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 89: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getApplicationEnabledSetting(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 90: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        logAppProcessStartIfNeeded(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 91: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        flushPackageRestrictionsAsUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 92: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setPackageStoppedState((String)localObject1, bool1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 93: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        freeStorageAndNotify(paramParcel1.readString(), paramParcel1.readLong(), IPackageDataObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 94: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        long l = paramParcel1.readLong();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (IntentSender)IntentSender.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          freeStorage((String)localObject1, l, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 95: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        deleteApplicationCacheFiles(paramParcel1.readString(), IPackageDataObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 96: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        deleteApplicationCacheFilesAsUser(paramParcel1.readString(), paramParcel1.readInt(), IPackageDataObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 97: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        clearApplicationUserData(paramParcel1.readString(), IPackageDataObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 98: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        clearApplicationProfileData(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 99: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        getPackageSizeInfo(paramParcel1.readString(), paramParcel1.readInt(), IPackageStatsObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 100: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getSystemSharedLibraryNames();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 101: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getSystemAvailableFeatures();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 102: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = hasSystemFeature(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 103: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        enterSafeMode();
        paramParcel2.writeNoException();
        return true;
      case 104: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isSafeMode();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 105: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        systemReady();
        paramParcel2.writeNoException();
        return true;
      case 106: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = hasSystemUidErrors();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 107: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        performFstrimIfNeeded();
        paramParcel2.writeNoException();
        return true;
      case 108: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        updatePackagesIfNeeded();
        paramParcel2.writeNoException();
        return true;
      case 109: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        notifyPackageUse(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 110: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = performDexOptIfNeeded(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 111: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label6266;
          }
          bool2 = true;
          bool1 = performDexOpt((String)localObject1, bool1, paramInt1, bool2);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label6272;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
          bool2 = false;
          break label6229;
        }
      case 112: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label6353;
          }
          bool2 = true;
          bool1 = performDexOptMode((String)localObject1, bool1, (String)localObject2, bool2);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label6359;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
          bool2 = false;
          break label6315;
        }
      case 113: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        dumpProfiles(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 114: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        forceDexOpt(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 115: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label6450;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          updateExternalMediaStatus(bool1, bool2);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 116: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (PackageCleanItem)PackageCleanItem.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = nextPackageToClean(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label6514;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 117: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getMoveStatus(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 118: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        registerMoveCallback(IPackageMoveObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 119: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        unregisterMoveCallback(IPackageMoveObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 120: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = movePackage(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 121: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = movePrimaryStorage(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 122: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (PermissionInfo)PermissionInfo.CREATOR.createFromParcel(paramParcel1);
          bool1 = addPermissionAsync(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label6706;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 123: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = setInstallLocation(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 124: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getInstallLocation();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 125: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = installExistingPackageAsUser(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 126: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        verifyPendingInstall(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 127: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        extendVerificationTimeout(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 128: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        verifyIntentFilter(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createStringArrayList());
        paramParcel2.writeNoException();
        return true;
      case 129: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramInt1 = getIntentVerificationStatus(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 130: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = updateIntentVerificationStatus(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 131: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getIntentFilterVerifications(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 132: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getAllIntentFilters(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 133: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = setDefaultBrowserPackageName(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 134: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getDefaultBrowserPackageName(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 135: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getVerifierDeviceIdentity();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 136: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isFirstBoot();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 137: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isOnlyCoreApps();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 138: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isUpgrade();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 139: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setPermissionEnforced((String)localObject1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 140: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isPermissionEnforced(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 141: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isStorageLow();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 142: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          bool1 = setApplicationHiddenSettingAsUser((String)localObject1, bool1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool1) {
            break label7437;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
        }
      case 143: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = getApplicationHiddenSettingAsUser(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 144: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPackageInstaller();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 145: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          bool1 = setBlockUninstallForUser((String)localObject1, bool1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool1) {
            break label7583;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
        }
      case 146: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = getBlockUninstallForUser(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 147: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getKeySetByAlias(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 148: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getSigningKeySet(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 149: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (KeySet)KeySet.CREATOR.createFromParcel(paramParcel1);
          bool1 = isPackageSignedByKeySet((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label7787;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 150: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (KeySet)KeySet.CREATOR.createFromParcel(paramParcel1);
          bool1 = isPackageSignedByKeySetExactly((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label7856;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 151: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        addOnPermissionsChangeListener(IOnPermissionsChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 152: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        removeOnPermissionsChangeListener(IOnPermissionsChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 153: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        grantDefaultPermissionsToEnabledCarrierApps(paramParcel1.createStringArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 154: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isPermissionRevokedByPolicy(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 155: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPermissionControllerPackageName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 156: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getEphemeralApplications(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 157: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getEphemeralApplicationCookie(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 158: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = setEphemeralApplicationCookie(paramParcel1.readString(), paramParcel1.createByteArray(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 159: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getEphemeralApplicationIcon(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 160: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isEphemeralApplication(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 161: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          bool1 = setRequiredForSystemUser((String)localObject1, bool1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label8268;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
        }
      case 162: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getServicesSystemSharedLibraryPackageName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 163: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getSharedSystemSharedLibraryPackageName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 164: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        bool1 = isPackageDeviceAdminOnAnyUser(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 165: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        paramParcel1 = getPreviousCodePaths(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeStringList(paramParcel1);
        return true;
      case 166: 
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        grantSystemAppPermissions(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 167: 
        label6229:
        label6266:
        label6272:
        label6315:
        label6353:
        label6359:
        label6450:
        label6514:
        label6706:
        label7437:
        label7583:
        label7787:
        label7856:
        label8268:
        paramParcel1.enforceInterface("android.content.pm.IPackageManager");
        resetApplicationPermissions(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.content.pm.IPackageManager");
      boolean bool1 = inCompatConfigList(paramParcel1.readInt(), paramParcel1.readString());
      paramParcel2.writeNoException();
      if (bool1) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IPackageManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public boolean activitySupportsIntent(ComponentName paramComponentName, Intent paramIntent, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramIntent != null)
              {
                localParcel1.writeInt(1);
                paramIntent.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                this.mRemote.transact(14, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label138;
                }
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label138:
          boolean bool = false;
        }
      }
      
      /* Error */
      public void addCrossProfileIntentFilter(IntentFilter paramIntentFilter, String paramString, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +75 -> 93
        //   21: aload 6
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 6
        //   30: iconst_0
        //   31: invokevirtual 74	android/content/IntentFilter:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 6
        //   36: aload_2
        //   37: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 6
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload 6
        //   48: iload 4
        //   50: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   53: aload 6
        //   55: iload 5
        //   57: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   60: aload_0
        //   61: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   64: bipush 72
        //   66: aload 6
        //   68: aload 7
        //   70: iconst_0
        //   71: invokeinterface 58 5 0
        //   76: pop
        //   77: aload 7
        //   79: invokevirtual 61	android/os/Parcel:readException	()V
        //   82: aload 7
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload 6
        //   89: invokevirtual 68	android/os/Parcel:recycle	()V
        //   92: return
        //   93: aload 6
        //   95: iconst_0
        //   96: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   99: goto -65 -> 34
        //   102: astore_1
        //   103: aload 7
        //   105: invokevirtual 68	android/os/Parcel:recycle	()V
        //   108: aload 6
        //   110: invokevirtual 68	android/os/Parcel:recycle	()V
        //   113: aload_1
        //   114: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	115	0	this	Proxy
        //   0	115	1	paramIntentFilter	IntentFilter
        //   0	115	2	paramString	String
        //   0	115	3	paramInt1	int
        //   0	115	4	paramInt2	int
        //   0	115	5	paramInt3	int
        //   3	106	6	localParcel1	Parcel
        //   8	96	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	102	finally
        //   21	34	102	finally
        //   34	82	102	finally
        //   93	99	102	finally
      }
      
      public void addOnPermissionsChangeListener(IOnPermissionsChangeListener paramIOnPermissionsChangeListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          if (paramIOnPermissionsChangeListener != null) {
            localIBinder = paramIOnPermissionsChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(151, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean addPermission(PermissionInfo paramPermissionInfo)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramPermissionInfo != null)
            {
              localParcel1.writeInt(1);
              paramPermissionInfo.writeToParcel(localParcel1, 0);
              this.mRemote.transact(20, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean addPermissionAsync(PermissionInfo paramPermissionInfo)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramPermissionInfo != null)
            {
              localParcel1.writeInt(1);
              paramPermissionInfo.writeToParcel(localParcel1, 0);
              this.mRemote.transact(122, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void addPersistentPreferredActivity(IntentFilter paramIntentFilter, ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntentFilter != null)
            {
              localParcel1.writeInt(1);
              paramIntentFilter.writeToParcel(localParcel1, 0);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(70, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void addPreferredActivity(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntentFilter != null)
            {
              localParcel1.writeInt(1);
              paramIntentFilter.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeTypedArray(paramArrayOfComponentName, 0);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt2);
                this.mRemote.transact(66, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public boolean canForwardTo(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(41, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt1 = localParcel2.readInt();
              if (paramInt1 != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String[] canonicalToCurrentPackageNames(String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeStringArray(paramArrayOfString);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramArrayOfString = localParcel2.createStringArray();
          return paramArrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void checkPackageStartable(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int checkPermission(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int checkSignatures(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int checkUidPermission(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int checkUidSignatures(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearApplicationProfileData(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(98, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearApplicationUserData(String paramString, IPackageDataObserver paramIPackageDataObserver, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramIPackageDataObserver != null) {
            paramString = paramIPackageDataObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(97, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearCrossProfileIntentFilters(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(73, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearPackagePersistentPreferredActivities(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(71, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearPackagePreferredActivities(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(68, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] currentToCanonicalPackageNames(String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeStringArray(paramArrayOfString);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramArrayOfString = localParcel2.createStringArray();
          return paramArrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void deleteApplicationCacheFiles(String paramString, IPackageDataObserver paramIPackageDataObserver)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramIPackageDataObserver != null) {
            paramString = paramIPackageDataObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          this.mRemote.transact(95, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void deleteApplicationCacheFilesAsUser(String paramString, int paramInt, IPackageDataObserver paramIPackageDataObserver)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          paramString = (String)localObject;
          if (paramIPackageDataObserver != null) {
            paramString = paramIPackageDataObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          this.mRemote.transact(96, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void deletePackage(String paramString, IPackageDeleteObserver2 paramIPackageDeleteObserver2, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramIPackageDeleteObserver2 != null) {
            paramString = paramIPackageDeleteObserver2.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(61, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void deletePackageAsUser(String paramString, IPackageDeleteObserver paramIPackageDeleteObserver, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramIPackageDeleteObserver != null) {
            paramString = paramIPackageDeleteObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(60, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void dumpProfiles(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(113, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void enterSafeMode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(103, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void extendVerificationTimeout(int paramInt1, int paramInt2, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(127, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void finishPackageInstall(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(58, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void flushPackageRestrictionsAsUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(91, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void forceDexOpt(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(114, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void freeStorage(String paramString, long paramLong, IntentSender paramIntentSender)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: lload_2
        //   26: invokevirtual 154	android/os/Parcel:writeLong	(J)V
        //   29: aload 4
        //   31: ifnull +50 -> 81
        //   34: aload 5
        //   36: iconst_1
        //   37: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   40: aload 4
        //   42: aload 5
        //   44: iconst_0
        //   45: invokevirtual 163	android/content/IntentSender:writeToParcel	(Landroid/os/Parcel;I)V
        //   48: aload_0
        //   49: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   52: bipush 94
        //   54: aload 5
        //   56: aload 6
        //   58: iconst_0
        //   59: invokeinterface 58 5 0
        //   64: pop
        //   65: aload 6
        //   67: invokevirtual 61	android/os/Parcel:readException	()V
        //   70: aload 6
        //   72: invokevirtual 68	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: return
        //   81: aload 5
        //   83: iconst_0
        //   84: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   87: goto -39 -> 48
        //   90: astore_1
        //   91: aload 6
        //   93: invokevirtual 68	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramString	String
        //   0	103	2	paramLong	long
        //   0	103	4	paramIntentSender	IntentSender
        //   3	94	5	localParcel1	Parcel
        //   8	84	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	90	finally
        //   34	48	90	finally
        //   48	70	90	finally
        //   81	87	90	finally
      }
      
      public void freeStorageAndNotify(String paramString, long paramLong, IPackageDataObserver paramIPackageDataObserver)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
          paramString = (String)localObject;
          if (paramIPackageDataObserver != null) {
            paramString = paramIPackageDataObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          this.mRemote.transact(93, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ActivityInfo getActivityInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(13, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (ActivityInfo)ActivityInfo.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramComponentName = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public ParceledListSlice getAllIntentFilters(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: sipush 132
        //   26: aload_2
        //   27: aload_3
        //   28: iconst_0
        //   29: invokeinterface 58 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 61	android/os/Parcel:readException	()V
        //   39: aload_3
        //   40: invokevirtual 65	android/os/Parcel:readInt	()I
        //   43: ifeq +26 -> 69
        //   46: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   49: aload_3
        //   50: invokeinterface 189 2 0
        //   55: checkcast 183	android/content/pm/ParceledListSlice
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 68	android/os/Parcel:recycle	()V
        //   63: aload_2
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: aload_1
        //   68: areturn
        //   69: aconst_null
        //   70: astore_1
        //   71: goto -12 -> 59
        //   74: astore_1
        //   75: aload_3
        //   76: invokevirtual 68	android/os/Parcel:recycle	()V
        //   79: aload_2
        //   80: invokevirtual 68	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramString	String
        //   3	77	2	localParcel1	Parcel
        //   7	69	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	59	74	finally
      }
      
      public List<String> getAllPackages()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(32, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createStringArrayList();
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParceledListSlice getAllPermissionGroups(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 11
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 58 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 65	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   52: aload 4
        //   54: invokeinterface 189 2 0
        //   59: checkcast 183	android/content/pm/ParceledListSlice
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 68	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 68	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 68	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localParceledListSlice	ParceledListSlice
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public String[] getAppOpPermissionPackages(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(39, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createStringArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getApplicationEnabledSetting(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(89, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean getApplicationHiddenSettingAsUser(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 143
        //   36: aload 4
        //   38: aload 5
        //   40: iconst_0
        //   41: invokeinterface 58 5 0
        //   46: pop
        //   47: aload 5
        //   49: invokevirtual 61	android/os/Parcel:readException	()V
        //   52: aload 5
        //   54: invokevirtual 65	android/os/Parcel:readInt	()I
        //   57: istore_2
        //   58: iload_2
        //   59: ifeq +17 -> 76
        //   62: iconst_1
        //   63: istore_3
        //   64: aload 5
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload 4
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: iload_3
        //   75: ireturn
        //   76: iconst_0
        //   77: istore_3
        //   78: goto -14 -> 64
        //   81: astore_1
        //   82: aload 5
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 68	android/os/Parcel:recycle	()V
        //   92: aload_1
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramString	String
        //   0	94	2	paramInt	int
        //   63	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	58	81	finally
      }
      
      /* Error */
      public ApplicationInfo getApplicationInfo(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 12
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokeinterface 58 5 0
        //   51: pop
        //   52: aload 5
        //   54: invokevirtual 61	android/os/Parcel:readException	()V
        //   57: aload 5
        //   59: invokevirtual 65	android/os/Parcel:readInt	()I
        //   62: ifeq +29 -> 91
        //   65: getstatic 209	android/content/pm/ApplicationInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   68: aload 5
        //   70: invokeinterface 179 2 0
        //   75: checkcast 208	android/content/pm/ApplicationInfo
        //   78: astore_1
        //   79: aload 5
        //   81: invokevirtual 68	android/os/Parcel:recycle	()V
        //   84: aload 4
        //   86: invokevirtual 68	android/os/Parcel:recycle	()V
        //   89: aload_1
        //   90: areturn
        //   91: aconst_null
        //   92: astore_1
        //   93: goto -14 -> 79
        //   96: astore_1
        //   97: aload 5
        //   99: invokevirtual 68	android/os/Parcel:recycle	()V
        //   102: aload 4
        //   104: invokevirtual 68	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramString	String
        //   0	109	2	paramInt1	int
        //   0	109	3	paramInt2	int
        //   3	100	4	localParcel1	Parcel
        //   8	90	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	79	96	finally
      }
      
      /* Error */
      public boolean getBlockUninstallForUser(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 146
        //   36: aload 4
        //   38: aload 5
        //   40: iconst_0
        //   41: invokeinterface 58 5 0
        //   46: pop
        //   47: aload 5
        //   49: invokevirtual 61	android/os/Parcel:readException	()V
        //   52: aload 5
        //   54: invokevirtual 65	android/os/Parcel:readInt	()I
        //   57: istore_2
        //   58: iload_2
        //   59: ifeq +17 -> 76
        //   62: iconst_1
        //   63: istore_3
        //   64: aload 5
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload 4
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: iload_3
        //   75: ireturn
        //   76: iconst_0
        //   77: istore_3
        //   78: goto -14 -> 64
        //   81: astore_1
        //   82: aload 5
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 68	android/os/Parcel:recycle	()V
        //   92: aload_1
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramString	String
        //   0	94	2	paramInt	int
        //   63	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	58	81	finally
      }
      
      /* Error */
      public int getComponentEnabledSetting(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +57 -> 73
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 87
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 58 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 65	android/os/Parcel:readInt	()I
        //   61: istore_2
        //   62: aload 4
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 68	android/os/Parcel:recycle	()V
        //   71: iload_2
        //   72: ireturn
        //   73: aload_3
        //   74: iconst_0
        //   75: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   78: goto -48 -> 30
        //   81: astore_1
        //   82: aload 4
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload_3
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramComponentName	ComponentName
        //   0	93	2	paramInt	int
        //   3	85	3	localParcel1	Parcel
        //   7	76	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	81	finally
        //   19	30	81	finally
        //   30	62	81	finally
        //   73	78	81	finally
      }
      
      public byte[] getDefaultAppsBackup(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(78, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getDefaultBrowserPackageName(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(134, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public byte[] getEphemeralApplicationCookie(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(157, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createByteArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Bitmap getEphemeralApplicationIcon(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: sipush 159
        //   32: aload_3
        //   33: aload 4
        //   35: iconst_0
        //   36: invokeinterface 58 5 0
        //   41: pop
        //   42: aload 4
        //   44: invokevirtual 61	android/os/Parcel:readException	()V
        //   47: aload 4
        //   49: invokevirtual 65	android/os/Parcel:readInt	()I
        //   52: ifeq +28 -> 80
        //   55: getstatic 231	android/graphics/Bitmap:CREATOR	Landroid/os/Parcelable$Creator;
        //   58: aload 4
        //   60: invokeinterface 179 2 0
        //   65: checkcast 230	android/graphics/Bitmap
        //   68: astore_1
        //   69: aload 4
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: aload_3
        //   75: invokevirtual 68	android/os/Parcel:recycle	()V
        //   78: aload_1
        //   79: areturn
        //   80: aconst_null
        //   81: astore_1
        //   82: goto -13 -> 69
        //   85: astore_1
        //   86: aload 4
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: aload_3
        //   92: invokevirtual 68	android/os/Parcel:recycle	()V
        //   95: aload_1
        //   96: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	97	0	this	Proxy
        //   0	97	1	paramString	String
        //   0	97	2	paramInt	int
        //   3	89	3	localParcel1	Parcel
        //   7	80	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	69	85	finally
      }
      
      /* Error */
      public ParceledListSlice getEphemeralApplications(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: sipush 156
        //   27: aload_3
        //   28: aload 4
        //   30: iconst_0
        //   31: invokeinterface 58 5 0
        //   36: pop
        //   37: aload 4
        //   39: invokevirtual 61	android/os/Parcel:readException	()V
        //   42: aload 4
        //   44: invokevirtual 65	android/os/Parcel:readInt	()I
        //   47: ifeq +28 -> 75
        //   50: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   53: aload 4
        //   55: invokeinterface 189 2 0
        //   60: checkcast 183	android/content/pm/ParceledListSlice
        //   63: astore_2
        //   64: aload 4
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload_3
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: areturn
        //   75: aconst_null
        //   76: astore_2
        //   77: goto -13 -> 64
        //   80: astore_2
        //   81: aload 4
        //   83: invokevirtual 68	android/os/Parcel:recycle	()V
        //   86: aload_3
        //   87: invokevirtual 68	android/os/Parcel:recycle	()V
        //   90: aload_2
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramInt	int
        //   63	14	2	localParceledListSlice	ParceledListSlice
        //   80	11	2	localObject	Object
        //   3	84	3	localParcel1	Parcel
        //   7	75	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	64	80	finally
      }
      
      public int getFlagsForUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(36, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ComponentName getHomeActivities(List<ResolveInfo> paramList)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 84
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: ifeq +37 -> 78
        //   44: getstatic 237	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload 4
        //   49: invokeinterface 179 2 0
        //   54: checkcast 42	android/content/ComponentName
        //   57: astore_2
        //   58: aload 4
        //   60: aload_1
        //   61: getstatic 240	android/content/pm/ResolveInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   64: invokevirtual 244	android/os/Parcel:readTypedList	(Ljava/util/List;Landroid/os/Parcelable$Creator;)V
        //   67: aload 4
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: areturn
        //   78: aconst_null
        //   79: astore_2
        //   80: goto -22 -> 58
        //   83: astore_1
        //   84: aload 4
        //   86: invokevirtual 68	android/os/Parcel:recycle	()V
        //   89: aload_3
        //   90: invokevirtual 68	android/os/Parcel:recycle	()V
        //   93: aload_1
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramList	List<ResolveInfo>
        //   57	23	2	localComponentName	ComponentName
        //   3	87	3	localParcel1	Parcel
        //   7	78	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	58	83	finally
        //   58	67	83	finally
      }
      
      public int getInstallLocation()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(124, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParceledListSlice getInstalledApplications(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 50
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 58 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 61	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 65	android/os/Parcel:readInt	()I
        //   56: ifeq +29 -> 85
        //   59: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   62: aload 5
        //   64: invokeinterface 189 2 0
        //   69: checkcast 183	android/content/pm/ParceledListSlice
        //   72: astore_3
        //   73: aload 5
        //   75: invokevirtual 68	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 68	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: areturn
        //   85: aconst_null
        //   86: astore_3
        //   87: goto -14 -> 73
        //   90: astore_3
        //   91: aload 5
        //   93: invokevirtual 68	android/os/Parcel:recycle	()V
        //   96: aload 4
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload_3
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramInt1	int
        //   0	103	2	paramInt2	int
        //   72	15	3	localParceledListSlice	ParceledListSlice
        //   90	12	3	localObject	Object
        //   3	94	4	localParcel1	Parcel
        //   8	84	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	73	90	finally
      }
      
      /* Error */
      public ParceledListSlice getInstalledPackages(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 48
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 58 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 61	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 65	android/os/Parcel:readInt	()I
        //   56: ifeq +29 -> 85
        //   59: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   62: aload 5
        //   64: invokeinterface 189 2 0
        //   69: checkcast 183	android/content/pm/ParceledListSlice
        //   72: astore_3
        //   73: aload 5
        //   75: invokevirtual 68	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 68	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: areturn
        //   85: aconst_null
        //   86: astore_3
        //   87: goto -14 -> 73
        //   90: astore_3
        //   91: aload 5
        //   93: invokevirtual 68	android/os/Parcel:recycle	()V
        //   96: aload 4
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload_3
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramInt1	int
        //   0	103	2	paramInt2	int
        //   72	15	3	localParceledListSlice	ParceledListSlice
        //   90	12	3	localObject	Object
        //   3	94	4	localParcel1	Parcel
        //   8	84	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	73	90	finally
      }
      
      public String getInstallerPackageName(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(62, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public InstrumentationInfo getInstrumentationInfo(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(55, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (InstrumentationInfo)InstrumentationInfo.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramComponentName = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public byte[] getIntentFilterVerificationBackup(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(80, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParceledListSlice getIntentFilterVerifications(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: sipush 131
        //   26: aload_2
        //   27: aload_3
        //   28: iconst_0
        //   29: invokeinterface 58 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 61	android/os/Parcel:readException	()V
        //   39: aload_3
        //   40: invokevirtual 65	android/os/Parcel:readInt	()I
        //   43: ifeq +26 -> 69
        //   46: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   49: aload_3
        //   50: invokeinterface 189 2 0
        //   55: checkcast 183	android/content/pm/ParceledListSlice
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 68	android/os/Parcel:recycle	()V
        //   63: aload_2
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: aload_1
        //   68: areturn
        //   69: aconst_null
        //   70: astore_1
        //   71: goto -12 -> 59
        //   74: astore_1
        //   75: aload_3
        //   76: invokevirtual 68	android/os/Parcel:recycle	()V
        //   79: aload_2
        //   80: invokevirtual 68	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramString	String
        //   3	77	2	localParcel1	Parcel
        //   7	69	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	59	74	finally
      }
      
      public int getIntentVerificationStatus(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(129, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.pm.IPackageManager";
      }
      
      /* Error */
      public KeySet getKeySetByAlias(String paramString1, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: aload_2
        //   22: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: sipush 147
        //   32: aload_3
        //   33: aload 4
        //   35: iconst_0
        //   36: invokeinterface 58 5 0
        //   41: pop
        //   42: aload 4
        //   44: invokevirtual 61	android/os/Parcel:readException	()V
        //   47: aload 4
        //   49: invokevirtual 65	android/os/Parcel:readInt	()I
        //   52: ifeq +28 -> 80
        //   55: getstatic 265	android/content/pm/KeySet:CREATOR	Landroid/os/Parcelable$Creator;
        //   58: aload 4
        //   60: invokeinterface 179 2 0
        //   65: checkcast 264	android/content/pm/KeySet
        //   68: astore_1
        //   69: aload 4
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: aload_3
        //   75: invokevirtual 68	android/os/Parcel:recycle	()V
        //   78: aload_1
        //   79: areturn
        //   80: aconst_null
        //   81: astore_1
        //   82: goto -13 -> 69
        //   85: astore_1
        //   86: aload 4
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: aload_3
        //   92: invokevirtual 68	android/os/Parcel:recycle	()V
        //   95: aload_1
        //   96: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	97	0	this	Proxy
        //   0	97	1	paramString1	String
        //   0	97	2	paramString2	String
        //   3	89	3	localParcel1	Parcel
        //   7	80	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	69	85	finally
      }
      
      public ResolveInfo getLastChosenActivity(Intent paramIntent, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(64, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIntent = (ResolveInfo)ResolveInfo.CREATOR.createFromParcel(localParcel2);
                return paramIntent;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIntent = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getMoveStatus(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(117, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getNameForUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getPackageGids(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createIntArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public PackageInfo getPackageInfo(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_3
        //   40: aload 4
        //   42: aload 5
        //   44: iconst_0
        //   45: invokeinterface 58 5 0
        //   50: pop
        //   51: aload 5
        //   53: invokevirtual 61	android/os/Parcel:readException	()V
        //   56: aload 5
        //   58: invokevirtual 65	android/os/Parcel:readInt	()I
        //   61: ifeq +29 -> 90
        //   64: getstatic 280	android/content/pm/PackageInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   67: aload 5
        //   69: invokeinterface 179 2 0
        //   74: checkcast 279	android/content/pm/PackageInfo
        //   77: astore_1
        //   78: aload 5
        //   80: invokevirtual 68	android/os/Parcel:recycle	()V
        //   83: aload 4
        //   85: invokevirtual 68	android/os/Parcel:recycle	()V
        //   88: aload_1
        //   89: areturn
        //   90: aconst_null
        //   91: astore_1
        //   92: goto -14 -> 78
        //   95: astore_1
        //   96: aload 5
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 68	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramString	String
        //   0	108	2	paramInt1	int
        //   0	108	3	paramInt2	int
        //   3	99	4	localParcel1	Parcel
        //   8	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	78	95	finally
      }
      
      public IPackageInstaller getPackageInstaller()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(144, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IPackageInstaller localIPackageInstaller = IPackageInstaller.Stub.asInterface(localParcel2.readStrongBinder());
          return localIPackageInstaller;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void getPackageSizeInfo(String paramString, int paramInt, IPackageStatsObserver paramIPackageStatsObserver)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          paramString = (String)localObject;
          if (paramIPackageStatsObserver != null) {
            paramString = paramIPackageStatsObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          this.mRemote.transact(99, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getPackageUid(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] getPackagesForUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParceledListSlice getPackagesHoldingPermissions(String[] paramArrayOfString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 107	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 49
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokeinterface 58 5 0
        //   51: pop
        //   52: aload 5
        //   54: invokevirtual 61	android/os/Parcel:readException	()V
        //   57: aload 5
        //   59: invokevirtual 65	android/os/Parcel:readInt	()I
        //   62: ifeq +29 -> 91
        //   65: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   68: aload 5
        //   70: invokeinterface 189 2 0
        //   75: checkcast 183	android/content/pm/ParceledListSlice
        //   78: astore_1
        //   79: aload 5
        //   81: invokevirtual 68	android/os/Parcel:recycle	()V
        //   84: aload 4
        //   86: invokevirtual 68	android/os/Parcel:recycle	()V
        //   89: aload_1
        //   90: areturn
        //   91: aconst_null
        //   92: astore_1
        //   93: goto -14 -> 79
        //   96: astore_1
        //   97: aload 5
        //   99: invokevirtual 68	android/os/Parcel:recycle	()V
        //   102: aload 4
        //   104: invokevirtual 68	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramArrayOfString	String[]
        //   0	109	2	paramInt1	int
        //   0	109	3	paramInt2	int
        //   3	100	4	localParcel1	Parcel
        //   8	90	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	79	96	finally
      }
      
      public String getPermissionControllerPackageName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(155, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getPermissionFlags(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public byte[] getPermissionGrantBackup(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(82, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public PermissionGroupInfo getPermissionGroupInfo(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 10
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 58 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 61	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 65	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 310	android/content/pm/PermissionGroupInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   57: aload 4
        //   59: invokeinterface 179 2 0
        //   64: checkcast 309	android/content/pm/PermissionGroupInfo
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 68	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 68	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramInt	int
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      /* Error */
      public PermissionInfo getPermissionInfo(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 8
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 58 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 61	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 65	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 313	android/content/pm/PermissionInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   57: aload 4
        //   59: invokeinterface 179 2 0
        //   64: checkcast 89	android/content/pm/PermissionInfo
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 68	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 68	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramInt	int
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      /* Error */
      public ParceledListSlice getPersistentApplications(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 51
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 58 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 65	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   52: aload 4
        //   54: invokeinterface 189 2 0
        //   59: checkcast 183	android/content/pm/ParceledListSlice
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 68	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 68	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 68	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localParceledListSlice	ParceledListSlice
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public int getPreferredActivities(List<IntentFilter> paramList, List<ComponentName> paramList1, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(69, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          localParcel2.readTypedList(paramList, IntentFilter.CREATOR);
          localParcel2.readTypedList(paramList1, ComponentName.CREATOR);
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public byte[] getPreferredActivityBackup(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(76, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<String> getPreviousCodePaths(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(165, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createStringArrayList();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getPrivateFlagsForUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(37, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ProviderInfo getProviderInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(17, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (ProviderInfo)ProviderInfo.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramComponentName = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public ActivityInfo getReceiverInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(15, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (ActivityInfo)ActivityInfo.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramComponentName = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public ServiceInfo getServiceInfo(ComponentName paramComponentName, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(16, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (ServiceInfo)ServiceInfo.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramComponentName = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getServicesSystemSharedLibraryPackageName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(162, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getSharedSystemSharedLibraryPackageName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(163, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public KeySet getSigningKeySet(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: sipush 148
        //   26: aload_2
        //   27: aload_3
        //   28: iconst_0
        //   29: invokeinterface 58 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 61	android/os/Parcel:readException	()V
        //   39: aload_3
        //   40: invokevirtual 65	android/os/Parcel:readInt	()I
        //   43: ifeq +26 -> 69
        //   46: getstatic 265	android/content/pm/KeySet:CREATOR	Landroid/os/Parcelable$Creator;
        //   49: aload_3
        //   50: invokeinterface 179 2 0
        //   55: checkcast 264	android/content/pm/KeySet
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 68	android/os/Parcel:recycle	()V
        //   63: aload_2
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: aload_1
        //   68: areturn
        //   69: aconst_null
        //   70: astore_1
        //   71: goto -12 -> 59
        //   74: astore_1
        //   75: aload_3
        //   76: invokevirtual 68	android/os/Parcel:recycle	()V
        //   79: aload_2
        //   80: invokevirtual 68	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramString	String
        //   3	77	2	localParcel1	Parcel
        //   7	69	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	59	74	finally
      }
      
      /* Error */
      public ParceledListSlice getSystemAvailableFeatures()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 101
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 58 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 61	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 65	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   43: aload_3
        //   44: invokeinterface 189 2 0
        //   49: checkcast 183	android/content/pm/ParceledListSlice
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 68	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 68	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localParceledListSlice	ParceledListSlice
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String[] getSystemSharedLibraryNames()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(100, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getUidForSharedUser(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public VerifierDeviceIdentity getVerifierDeviceIdentity()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: sipush 135
        //   21: aload_2
        //   22: aload_3
        //   23: iconst_0
        //   24: invokeinterface 58 5 0
        //   29: pop
        //   30: aload_3
        //   31: invokevirtual 61	android/os/Parcel:readException	()V
        //   34: aload_3
        //   35: invokevirtual 65	android/os/Parcel:readInt	()I
        //   38: ifeq +26 -> 64
        //   41: getstatic 348	android/content/pm/VerifierDeviceIdentity:CREATOR	Landroid/os/Parcelable$Creator;
        //   44: aload_3
        //   45: invokeinterface 179 2 0
        //   50: checkcast 347	android/content/pm/VerifierDeviceIdentity
        //   53: astore_1
        //   54: aload_3
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: aload_2
        //   59: invokevirtual 68	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: areturn
        //   64: aconst_null
        //   65: astore_1
        //   66: goto -12 -> 54
        //   69: astore_1
        //   70: aload_3
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: aload_2
        //   75: invokevirtual 68	android/os/Parcel:recycle	()V
        //   78: aload_1
        //   79: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	80	0	this	Proxy
        //   53	13	1	localVerifierDeviceIdentity	VerifierDeviceIdentity
        //   69	10	1	localObject	Object
        //   3	72	2	localParcel1	Parcel
        //   7	64	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	54	69	finally
      }
      
      public void grantDefaultPermissionsToEnabledCarrierApps(String[] paramArrayOfString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeStringArray(paramArrayOfString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(153, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void grantRuntimePermission(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void grantSystemAppPermissions(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(166, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean hasSystemFeature(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 102
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 58 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 61	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 65	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 68	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 68	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean hasSystemUidErrors()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 106
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 68	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 68	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 68	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 68	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean inCompatConfigList(int paramInt, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 168
        //   36: aload 4
        //   38: aload 5
        //   40: iconst_0
        //   41: invokeinterface 58 5 0
        //   46: pop
        //   47: aload 5
        //   49: invokevirtual 61	android/os/Parcel:readException	()V
        //   52: aload 5
        //   54: invokevirtual 65	android/os/Parcel:readInt	()I
        //   57: istore_1
        //   58: iload_1
        //   59: ifeq +17 -> 76
        //   62: iconst_1
        //   63: istore_3
        //   64: aload 5
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload 4
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: iload_3
        //   75: ireturn
        //   76: iconst_0
        //   77: istore_3
        //   78: goto -14 -> 64
        //   81: astore_2
        //   82: aload 5
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 68	android/os/Parcel:recycle	()V
        //   92: aload_2
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramInt	int
        //   0	94	2	paramString	String
        //   63	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	58	81	finally
      }
      
      public int installExistingPackageAsUser(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(125, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void installPackageAsUser(String paramString1, IPackageInstallObserver2 paramIPackageInstallObserver2, int paramInt1, String paramString2, int paramInt2)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          paramString1 = (String)localObject;
          if (paramIPackageInstallObserver2 != null) {
            paramString1 = paramIPackageInstallObserver2.asBinder();
          }
          localParcel1.writeStrongBinder(paramString1);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(57, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isEphemeralApplication(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 160
        //   36: aload 4
        //   38: aload 5
        //   40: iconst_0
        //   41: invokeinterface 58 5 0
        //   46: pop
        //   47: aload 5
        //   49: invokevirtual 61	android/os/Parcel:readException	()V
        //   52: aload 5
        //   54: invokevirtual 65	android/os/Parcel:readInt	()I
        //   57: istore_2
        //   58: iload_2
        //   59: ifeq +17 -> 76
        //   62: iconst_1
        //   63: istore_3
        //   64: aload 5
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload 4
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: iload_3
        //   75: ireturn
        //   76: iconst_0
        //   77: istore_3
        //   78: goto -14 -> 64
        //   81: astore_1
        //   82: aload 5
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 68	android/os/Parcel:recycle	()V
        //   92: aload_1
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramString	String
        //   0	94	2	paramInt	int
        //   63	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	58	81	finally
      }
      
      /* Error */
      public boolean isFirstBoot()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 136
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 58 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 61	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 65	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 68	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload 5
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   42	2	1	i	int
        //   48	14	2	bool	boolean
        //   3	70	3	localParcel1	Parcel
        //   7	61	4	localParcel2	Parcel
        //   65	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	43	65	finally
      }
      
      /* Error */
      public boolean isOnlyCoreApps()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 137
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 58 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 61	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 65	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 68	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload 5
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   42	2	1	i	int
        //   48	14	2	bool	boolean
        //   3	70	3	localParcel1	Parcel
        //   7	61	4	localParcel2	Parcel
        //   65	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	43	65	finally
      }
      
      /* Error */
      public boolean isPackageAvailable(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_2
        //   34: aload 4
        //   36: aload 5
        //   38: iconst_0
        //   39: invokeinterface 58 5 0
        //   44: pop
        //   45: aload 5
        //   47: invokevirtual 61	android/os/Parcel:readException	()V
        //   50: aload 5
        //   52: invokevirtual 65	android/os/Parcel:readInt	()I
        //   55: istore_2
        //   56: iload_2
        //   57: ifeq +17 -> 74
        //   60: iconst_1
        //   61: istore_3
        //   62: aload 5
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: aload 4
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: iload_3
        //   73: ireturn
        //   74: iconst_0
        //   75: istore_3
        //   76: goto -14 -> 62
        //   79: astore_1
        //   80: aload 5
        //   82: invokevirtual 68	android/os/Parcel:recycle	()V
        //   85: aload 4
        //   87: invokevirtual 68	android/os/Parcel:recycle	()V
        //   90: aload_1
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramString	String
        //   0	92	2	paramInt	int
        //   61	15	3	bool	boolean
        //   3	83	4	localParcel1	Parcel
        //   8	73	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	56	79	finally
      }
      
      /* Error */
      public boolean isPackageDeviceAdminOnAnyUser(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: sipush 164
        //   30: aload 4
        //   32: aload 5
        //   34: iconst_0
        //   35: invokeinterface 58 5 0
        //   40: pop
        //   41: aload 5
        //   43: invokevirtual 61	android/os/Parcel:readException	()V
        //   46: aload 5
        //   48: invokevirtual 65	android/os/Parcel:readInt	()I
        //   51: istore_2
        //   52: iload_2
        //   53: ifeq +17 -> 70
        //   56: iconst_1
        //   57: istore_3
        //   58: aload 5
        //   60: invokevirtual 68	android/os/Parcel:recycle	()V
        //   63: aload 4
        //   65: invokevirtual 68	android/os/Parcel:recycle	()V
        //   68: iload_3
        //   69: ireturn
        //   70: iconst_0
        //   71: istore_3
        //   72: goto -14 -> 58
        //   75: astore_1
        //   76: aload 5
        //   78: invokevirtual 68	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 68	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramString	String
        //   51	2	2	i	int
        //   57	15	3	bool	boolean
        //   3	79	4	localParcel1	Parcel
        //   8	69	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	52	75	finally
      }
      
      public boolean isPackageSignedByKeySet(String paramString, KeySet paramKeySet)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            localParcel1.writeString(paramString);
            if (paramKeySet != null)
            {
              localParcel1.writeInt(1);
              paramKeySet.writeToParcel(localParcel1, 0);
              this.mRemote.transact(149, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean isPackageSignedByKeySetExactly(String paramString, KeySet paramKeySet)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            localParcel1.writeString(paramString);
            if (paramKeySet != null)
            {
              localParcel1.writeInt(1);
              paramKeySet.writeToParcel(localParcel1, 0);
              this.mRemote.transact(150, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean isPackageSuspendedForUser(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 75
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 58 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 61	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 65	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 68	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 68	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean isPermissionEnforced(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: sipush 140
        //   30: aload 4
        //   32: aload 5
        //   34: iconst_0
        //   35: invokeinterface 58 5 0
        //   40: pop
        //   41: aload 5
        //   43: invokevirtual 61	android/os/Parcel:readException	()V
        //   46: aload 5
        //   48: invokevirtual 65	android/os/Parcel:readInt	()I
        //   51: istore_2
        //   52: iload_2
        //   53: ifeq +17 -> 70
        //   56: iconst_1
        //   57: istore_3
        //   58: aload 5
        //   60: invokevirtual 68	android/os/Parcel:recycle	()V
        //   63: aload 4
        //   65: invokevirtual 68	android/os/Parcel:recycle	()V
        //   68: iload_3
        //   69: ireturn
        //   70: iconst_0
        //   71: istore_3
        //   72: goto -14 -> 58
        //   75: astore_1
        //   76: aload 5
        //   78: invokevirtual 68	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 68	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramString	String
        //   51	2	2	i	int
        //   57	15	3	bool	boolean
        //   3	79	4	localParcel1	Parcel
        //   8	69	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	52	75	finally
      }
      
      /* Error */
      public boolean isPermissionRevokedByPolicy(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: sipush 154
        //   42: aload 5
        //   44: aload 6
        //   46: iconst_0
        //   47: invokeinterface 58 5 0
        //   52: pop
        //   53: aload 6
        //   55: invokevirtual 61	android/os/Parcel:readException	()V
        //   58: aload 6
        //   60: invokevirtual 65	android/os/Parcel:readInt	()I
        //   63: istore_3
        //   64: iload_3
        //   65: ifeq +19 -> 84
        //   68: iconst_1
        //   69: istore 4
        //   71: aload 6
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload 5
        //   78: invokevirtual 68	android/os/Parcel:recycle	()V
        //   81: iload 4
        //   83: ireturn
        //   84: iconst_0
        //   85: istore 4
        //   87: goto -16 -> 71
        //   90: astore_1
        //   91: aload 6
        //   93: invokevirtual 68	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramString1	String
        //   0	103	2	paramString2	String
        //   0	103	3	paramInt	int
        //   69	17	4	bool	boolean
        //   3	94	5	localParcel1	Parcel
        //   8	84	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	64	90	finally
      }
      
      /* Error */
      public boolean isProtectedBroadcast(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 29
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 58 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 61	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 65	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 68	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 68	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isSafeMode()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 104
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 68	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 68	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 68	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 68	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      /* Error */
      public boolean isStorageLow()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 141
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 58 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 61	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 65	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 68	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload 5
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   42	2	1	i	int
        //   48	14	2	bool	boolean
        //   3	70	3	localParcel1	Parcel
        //   7	61	4	localParcel2	Parcel
        //   65	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	43	65	finally
      }
      
      /* Error */
      public boolean isUidPrivileged(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 38
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 58 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 65	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 68	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      /* Error */
      public boolean isUpgrade()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 138
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 58 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 61	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 65	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 68	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload 5
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   42	2	1	i	int
        //   48	14	2	bool	boolean
        //   3	70	3	localParcel1	Parcel
        //   7	61	4	localParcel2	Parcel
        //   65	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	43	65	finally
      }
      
      public void logAppProcessStartIfNeeded(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(90, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int movePackage(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(120, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int movePrimaryStorage(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(121, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public PackageCleanItem nextPackageToClean(PackageCleanItem paramPackageCleanItem)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramPackageCleanItem != null)
            {
              localParcel1.writeInt(1);
              paramPackageCleanItem.writeToParcel(localParcel1, 0);
              this.mRemote.transact(116, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramPackageCleanItem = (PackageCleanItem)PackageCleanItem.CREATOR.createFromParcel(localParcel2);
                return paramPackageCleanItem;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramPackageCleanItem = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void notifyPackageUse(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(109, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean performDexOpt(String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 6
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 8
        //   13: aload 7
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 7
        //   22: aload_1
        //   23: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: iload_2
        //   27: ifeq +79 -> 106
        //   30: iconst_1
        //   31: istore 5
        //   33: aload 7
        //   35: iload 5
        //   37: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   40: aload 7
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: iload 4
        //   48: ifeq +64 -> 112
        //   51: iload 6
        //   53: istore_3
        //   54: aload 7
        //   56: iload_3
        //   57: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   60: aload_0
        //   61: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   64: bipush 111
        //   66: aload 7
        //   68: aload 8
        //   70: iconst_0
        //   71: invokeinterface 58 5 0
        //   76: pop
        //   77: aload 8
        //   79: invokevirtual 61	android/os/Parcel:readException	()V
        //   82: aload 8
        //   84: invokevirtual 65	android/os/Parcel:readInt	()I
        //   87: istore_3
        //   88: iload_3
        //   89: ifeq +28 -> 117
        //   92: iconst_1
        //   93: istore_2
        //   94: aload 8
        //   96: invokevirtual 68	android/os/Parcel:recycle	()V
        //   99: aload 7
        //   101: invokevirtual 68	android/os/Parcel:recycle	()V
        //   104: iload_2
        //   105: ireturn
        //   106: iconst_0
        //   107: istore 5
        //   109: goto -76 -> 33
        //   112: iconst_0
        //   113: istore_3
        //   114: goto -60 -> 54
        //   117: iconst_0
        //   118: istore_2
        //   119: goto -25 -> 94
        //   122: astore_1
        //   123: aload 8
        //   125: invokevirtual 68	android/os/Parcel:recycle	()V
        //   128: aload 7
        //   130: invokevirtual 68	android/os/Parcel:recycle	()V
        //   133: aload_1
        //   134: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	135	0	this	Proxy
        //   0	135	1	paramString	String
        //   0	135	2	paramBoolean1	boolean
        //   0	135	3	paramInt	int
        //   0	135	4	paramBoolean2	boolean
        //   31	77	5	i	int
        //   1	51	6	j	int
        //   6	123	7	localParcel1	Parcel
        //   11	113	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	122	finally
        //   33	46	122	finally
        //   54	88	122	finally
      }
      
      /* Error */
      public boolean performDexOptIfNeeded(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 110
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 58 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 61	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 65	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 68	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 68	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean performDexOptMode(String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 6
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 8
        //   13: aload 7
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 7
        //   22: aload_1
        //   23: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: iload_2
        //   27: ifeq +83 -> 110
        //   30: iconst_1
        //   31: istore 5
        //   33: aload 7
        //   35: iload 5
        //   37: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   40: aload 7
        //   42: aload_3
        //   43: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: iload 4
        //   48: ifeq +68 -> 116
        //   51: iload 6
        //   53: istore 5
        //   55: aload 7
        //   57: iload 5
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: aload_0
        //   63: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   66: bipush 112
        //   68: aload 7
        //   70: aload 8
        //   72: iconst_0
        //   73: invokeinterface 58 5 0
        //   78: pop
        //   79: aload 8
        //   81: invokevirtual 61	android/os/Parcel:readException	()V
        //   84: aload 8
        //   86: invokevirtual 65	android/os/Parcel:readInt	()I
        //   89: istore 5
        //   91: iload 5
        //   93: ifeq +29 -> 122
        //   96: iconst_1
        //   97: istore_2
        //   98: aload 8
        //   100: invokevirtual 68	android/os/Parcel:recycle	()V
        //   103: aload 7
        //   105: invokevirtual 68	android/os/Parcel:recycle	()V
        //   108: iload_2
        //   109: ireturn
        //   110: iconst_0
        //   111: istore 5
        //   113: goto -80 -> 33
        //   116: iconst_0
        //   117: istore 5
        //   119: goto -64 -> 55
        //   122: iconst_0
        //   123: istore_2
        //   124: goto -26 -> 98
        //   127: astore_1
        //   128: aload 8
        //   130: invokevirtual 68	android/os/Parcel:recycle	()V
        //   133: aload 7
        //   135: invokevirtual 68	android/os/Parcel:recycle	()V
        //   138: aload_1
        //   139: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	140	0	this	Proxy
        //   0	140	1	paramString1	String
        //   0	140	2	paramBoolean1	boolean
        //   0	140	3	paramString2	String
        //   0	140	4	paramBoolean2	boolean
        //   31	87	5	i	int
        //   1	51	6	j	int
        //   6	128	7	localParcel1	Parcel
        //   11	118	8	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	127	finally
        //   33	46	127	finally
        //   55	91	127	finally
      }
      
      public void performFstrimIfNeeded()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(107, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParceledListSlice queryContentProviders(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 54
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokeinterface 58 5 0
        //   51: pop
        //   52: aload 5
        //   54: invokevirtual 61	android/os/Parcel:readException	()V
        //   57: aload 5
        //   59: invokevirtual 65	android/os/Parcel:readInt	()I
        //   62: ifeq +29 -> 91
        //   65: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   68: aload 5
        //   70: invokeinterface 189 2 0
        //   75: checkcast 183	android/content/pm/ParceledListSlice
        //   78: astore_1
        //   79: aload 5
        //   81: invokevirtual 68	android/os/Parcel:recycle	()V
        //   84: aload 4
        //   86: invokevirtual 68	android/os/Parcel:recycle	()V
        //   89: aload_1
        //   90: areturn
        //   91: aconst_null
        //   92: astore_1
        //   93: goto -14 -> 79
        //   96: astore_1
        //   97: aload 5
        //   99: invokevirtual 68	android/os/Parcel:recycle	()V
        //   102: aload 4
        //   104: invokevirtual 68	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramString	String
        //   0	109	2	paramInt1	int
        //   0	109	3	paramInt2	int
        //   3	100	4	localParcel1	Parcel
        //   8	90	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	79	96	finally
      }
      
      /* Error */
      public ParceledListSlice queryInstrumentation(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 56
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 58 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 61	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 65	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   57: aload 4
        //   59: invokeinterface 189 2 0
        //   64: checkcast 183	android/content/pm/ParceledListSlice
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 68	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 68	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramInt	int
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      public ParceledListSlice queryIntentActivities(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(42, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIntent = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
                return paramIntent;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIntent = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public ParceledListSlice queryIntentActivityOptions(ComponentName paramComponentName, Intent[] paramArrayOfIntent, String[] paramArrayOfString, Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeTypedArray(paramArrayOfIntent, 0);
              localParcel1.writeStringArray(paramArrayOfString);
              if (paramIntent != null)
              {
                localParcel1.writeInt(1);
                paramIntent.writeToParcel(localParcel1, 0);
                localParcel1.writeString(paramString);
                localParcel1.writeInt(paramInt1);
                localParcel1.writeInt(paramInt2);
                this.mRemote.transact(43, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label174;
                }
                paramComponentName = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label174:
          paramComponentName = null;
        }
      }
      
      public ParceledListSlice queryIntentContentProviders(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(47, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIntent = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
                return paramIntent;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIntent = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public ParceledListSlice queryIntentReceivers(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(44, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIntent = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
                return paramIntent;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIntent = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public ParceledListSlice queryIntentServices(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(46, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIntent = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
                return paramIntent;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIntent = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public ParceledListSlice queryPermissionsByGroup(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 9
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 58 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 61	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 65	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 186	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   57: aload 4
        //   59: invokeinterface 189 2 0
        //   64: checkcast 183	android/content/pm/ParceledListSlice
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 68	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 68	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramInt	int
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      public void querySyncProviders(List<String> paramList, List<ProviderInfo> paramList1)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeStringList(paramList);
          localParcel1.writeTypedList(paramList1);
          this.mRemote.transact(53, localParcel1, localParcel2, 0);
          localParcel2.readException();
          localParcel2.readStringList(paramList);
          localParcel2.readTypedList(paramList1, ProviderInfo.CREATOR);
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerMoveCallback(IPackageMoveObserver paramIPackageMoveObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          if (paramIPackageMoveObserver != null) {
            localIBinder = paramIPackageMoveObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(118, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeOnPermissionsChangeListener(IOnPermissionsChangeListener paramIOnPermissionsChangeListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          if (paramIOnPermissionsChangeListener != null) {
            localIBinder = paramIOnPermissionsChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(152, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removePermission(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void replacePreferredActivity(IntentFilter paramIntentFilter, int paramInt1, ComponentName[] paramArrayOfComponentName, ComponentName paramComponentName, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntentFilter != null)
            {
              localParcel1.writeInt(1);
              paramIntentFilter.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeTypedArray(paramArrayOfComponentName, 0);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt2);
                this.mRemote.transact(67, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void resetApplicationPermissions(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(167, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void resetApplicationPreferences(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(63, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void resetRuntimePermissions()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ProviderInfo resolveContentProvider(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 4
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 52
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokeinterface 58 5 0
        //   51: pop
        //   52: aload 5
        //   54: invokevirtual 61	android/os/Parcel:readException	()V
        //   57: aload 5
        //   59: invokevirtual 65	android/os/Parcel:readInt	()I
        //   62: ifeq +29 -> 91
        //   65: getstatic 328	android/content/pm/ProviderInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   68: aload 5
        //   70: invokeinterface 179 2 0
        //   75: checkcast 327	android/content/pm/ProviderInfo
        //   78: astore_1
        //   79: aload 5
        //   81: invokevirtual 68	android/os/Parcel:recycle	()V
        //   84: aload 4
        //   86: invokevirtual 68	android/os/Parcel:recycle	()V
        //   89: aload_1
        //   90: areturn
        //   91: aconst_null
        //   92: astore_1
        //   93: goto -14 -> 79
        //   96: astore_1
        //   97: aload 5
        //   99: invokevirtual 68	android/os/Parcel:recycle	()V
        //   102: aload 4
        //   104: invokevirtual 68	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramString	String
        //   0	109	2	paramInt1	int
        //   0	109	3	paramInt2	int
        //   3	100	4	localParcel1	Parcel
        //   8	90	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	79	96	finally
      }
      
      public ResolveInfo resolveIntent(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(40, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIntent = (ResolveInfo)ResolveInfo.CREATOR.createFromParcel(localParcel2);
                return paramIntent;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIntent = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public ResolveInfo resolveService(Intent paramIntent, String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(45, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIntent = (ResolveInfo)ResolveInfo.CREATOR.createFromParcel(localParcel2);
                return paramIntent;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIntent = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void restoreDefaultApps(byte[] paramArrayOfByte, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(79, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void restoreIntentFilterVerification(byte[] paramArrayOfByte, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(81, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void restorePermissionGrants(byte[] paramArrayOfByte, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(83, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void restorePreferredActivities(byte[] paramArrayOfByte, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(77, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void revokeRuntimePermission(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setApplicationEnabledSetting(String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(88, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean setApplicationHiddenSettingAsUser(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: aload_1
        //   23: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: iload_2
        //   27: ifeq +6 -> 33
        //   30: iconst_1
        //   31: istore 4
        //   33: aload 5
        //   35: iload 4
        //   37: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   40: aload 5
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: sipush 142
        //   53: aload 5
        //   55: aload 6
        //   57: iconst_0
        //   58: invokeinterface 58 5 0
        //   63: pop
        //   64: aload 6
        //   66: invokevirtual 61	android/os/Parcel:readException	()V
        //   69: aload 6
        //   71: invokevirtual 65	android/os/Parcel:readInt	()I
        //   74: istore_3
        //   75: iload_3
        //   76: ifeq +17 -> 93
        //   79: iconst_1
        //   80: istore_2
        //   81: aload 6
        //   83: invokevirtual 68	android/os/Parcel:recycle	()V
        //   86: aload 5
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: iload_2
        //   92: ireturn
        //   93: iconst_0
        //   94: istore_2
        //   95: goto -14 -> 81
        //   98: astore_1
        //   99: aload 6
        //   101: invokevirtual 68	android/os/Parcel:recycle	()V
        //   104: aload 5
        //   106: invokevirtual 68	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramString	String
        //   0	111	2	paramBoolean	boolean
        //   0	111	3	paramInt	int
        //   1	35	4	i	int
        //   6	99	5	localParcel1	Parcel
        //   11	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	98	finally
        //   33	75	98	finally
      }
      
      /* Error */
      public boolean setBlockUninstallForUser(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: aload_1
        //   23: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: iload_2
        //   27: ifeq +6 -> 33
        //   30: iconst_1
        //   31: istore 4
        //   33: aload 5
        //   35: iload 4
        //   37: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   40: aload 5
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: sipush 145
        //   53: aload 5
        //   55: aload 6
        //   57: iconst_0
        //   58: invokeinterface 58 5 0
        //   63: pop
        //   64: aload 6
        //   66: invokevirtual 61	android/os/Parcel:readException	()V
        //   69: aload 6
        //   71: invokevirtual 65	android/os/Parcel:readInt	()I
        //   74: istore_3
        //   75: iload_3
        //   76: ifeq +17 -> 93
        //   79: iconst_1
        //   80: istore_2
        //   81: aload 6
        //   83: invokevirtual 68	android/os/Parcel:recycle	()V
        //   86: aload 5
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: iload_2
        //   92: ireturn
        //   93: iconst_0
        //   94: istore_2
        //   95: goto -14 -> 81
        //   98: astore_1
        //   99: aload 6
        //   101: invokevirtual 68	android/os/Parcel:recycle	()V
        //   104: aload 5
        //   106: invokevirtual 68	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramString	String
        //   0	111	2	paramBoolean	boolean
        //   0	111	3	paramInt	int
        //   1	35	4	i	int
        //   6	99	5	localParcel1	Parcel
        //   11	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	98	finally
        //   33	75	98	finally
      }
      
      /* Error */
      public void setComponentEnabledSetting(ComponentName paramComponentName, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +68 -> 86
        //   21: aload 5
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 5
        //   30: iconst_0
        //   31: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 5
        //   36: iload_2
        //   37: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   40: aload 5
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload 5
        //   48: iload 4
        //   50: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   53: aload_0
        //   54: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 86
        //   59: aload 5
        //   61: aload 6
        //   63: iconst_0
        //   64: invokeinterface 58 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 61	android/os/Parcel:readException	()V
        //   75: aload 6
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: invokevirtual 68	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 5
        //   88: iconst_0
        //   89: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   92: goto -58 -> 34
        //   95: astore_1
        //   96: aload 6
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 68	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramComponentName	ComponentName
        //   0	108	2	paramInt1	int
        //   0	108	3	paramInt2	int
        //   0	108	4	paramInt3	int
        //   3	99	5	localParcel1	Parcel
        //   8	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	95	finally
        //   21	34	95	finally
        //   34	75	95	finally
        //   86	92	95	finally
      }
      
      /* Error */
      public boolean setDefaultBrowserPackageName(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 133
        //   36: aload 4
        //   38: aload 5
        //   40: iconst_0
        //   41: invokeinterface 58 5 0
        //   46: pop
        //   47: aload 5
        //   49: invokevirtual 61	android/os/Parcel:readException	()V
        //   52: aload 5
        //   54: invokevirtual 65	android/os/Parcel:readInt	()I
        //   57: istore_2
        //   58: iload_2
        //   59: ifeq +17 -> 76
        //   62: iconst_1
        //   63: istore_3
        //   64: aload 5
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload 4
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: iload_3
        //   75: ireturn
        //   76: iconst_0
        //   77: istore_3
        //   78: goto -14 -> 64
        //   81: astore_1
        //   82: aload 5
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 68	android/os/Parcel:recycle	()V
        //   92: aload_1
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramString	String
        //   0	94	2	paramInt	int
        //   63	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	58	81	finally
      }
      
      /* Error */
      public boolean setEphemeralApplicationCookie(String paramString, byte[] paramArrayOfByte, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 448	android/os/Parcel:writeByteArray	([B)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: sipush 158
        //   42: aload 5
        //   44: aload 6
        //   46: iconst_0
        //   47: invokeinterface 58 5 0
        //   52: pop
        //   53: aload 6
        //   55: invokevirtual 61	android/os/Parcel:readException	()V
        //   58: aload 6
        //   60: invokevirtual 65	android/os/Parcel:readInt	()I
        //   63: istore_3
        //   64: iload_3
        //   65: ifeq +19 -> 84
        //   68: iconst_1
        //   69: istore 4
        //   71: aload 6
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload 5
        //   78: invokevirtual 68	android/os/Parcel:recycle	()V
        //   81: iload 4
        //   83: ireturn
        //   84: iconst_0
        //   85: istore 4
        //   87: goto -16 -> 71
        //   90: astore_1
        //   91: aload 6
        //   93: invokevirtual 68	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramString	String
        //   0	103	2	paramArrayOfByte	byte[]
        //   0	103	3	paramInt	int
        //   69	17	4	bool	boolean
        //   3	94	5	localParcel1	Parcel
        //   8	84	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	64	90	finally
      }
      
      /* Error */
      public void setHomeActivity(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 85
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 58 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 61	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 68	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 68	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 68	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramComponentName	ComponentName
        //   0	86	2	paramInt	int
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      /* Error */
      public boolean setInstallLocation(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 123
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 58 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 65	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 68	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      public void setInstallerPackageName(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(59, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setLastChosenActivity(Intent paramIntent, String paramString, int paramInt1, IntentFilter paramIntentFilter, int paramInt2, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
            if (paramIntent != null)
            {
              localParcel1.writeInt(1);
              paramIntent.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt1);
              if (paramIntentFilter != null)
              {
                localParcel1.writeInt(1);
                paramIntentFilter.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt2);
                if (paramComponentName == null) {
                  break label155;
                }
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                this.mRemote.transact(65, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label155:
          localParcel1.writeInt(0);
        }
      }
      
      public void setPackageStoppedState(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(92, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] setPackagesSuspendedAsUser(String[] paramArrayOfString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeStringArray(paramArrayOfString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(74, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramArrayOfString = localParcel2.createStringArray();
          return paramArrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setPermissionEnforced(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(139, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean setRequiredForSystemUser(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: aload_1
        //   22: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   25: iload_2
        //   26: ifeq +5 -> 31
        //   29: iconst_1
        //   30: istore_3
        //   31: aload 4
        //   33: iload_3
        //   34: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   37: aload_0
        //   38: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   41: sipush 161
        //   44: aload 4
        //   46: aload 5
        //   48: iconst_0
        //   49: invokeinterface 58 5 0
        //   54: pop
        //   55: aload 5
        //   57: invokevirtual 61	android/os/Parcel:readException	()V
        //   60: aload 5
        //   62: invokevirtual 65	android/os/Parcel:readInt	()I
        //   65: istore_3
        //   66: iload_3
        //   67: ifeq +17 -> 84
        //   70: iconst_1
        //   71: istore_2
        //   72: aload 5
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: aload 4
        //   79: invokevirtual 68	android/os/Parcel:recycle	()V
        //   82: iload_2
        //   83: ireturn
        //   84: iconst_0
        //   85: istore_2
        //   86: goto -14 -> 72
        //   89: astore_1
        //   90: aload 5
        //   92: invokevirtual 68	android/os/Parcel:recycle	()V
        //   95: aload 4
        //   97: invokevirtual 68	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramString	String
        //   0	102	2	paramBoolean	boolean
        //   1	66	3	i	int
        //   5	91	4	localParcel1	Parcel
        //   10	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	25	89	finally
        //   31	66	89	finally
      }
      
      /* Error */
      public boolean shouldShowRequestPermissionRationale(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 28
        //   41: aload 5
        //   43: aload 6
        //   45: iconst_0
        //   46: invokeinterface 58 5 0
        //   51: pop
        //   52: aload 6
        //   54: invokevirtual 61	android/os/Parcel:readException	()V
        //   57: aload 6
        //   59: invokevirtual 65	android/os/Parcel:readInt	()I
        //   62: istore_3
        //   63: iload_3
        //   64: ifeq +19 -> 83
        //   67: iconst_1
        //   68: istore 4
        //   70: aload 6
        //   72: invokevirtual 68	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: iload 4
        //   82: ireturn
        //   83: iconst_0
        //   84: istore 4
        //   86: goto -16 -> 70
        //   89: astore_1
        //   90: aload 6
        //   92: invokevirtual 68	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 68	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramString1	String
        //   0	102	2	paramString2	String
        //   0	102	3	paramInt	int
        //   68	17	4	bool	boolean
        //   3	93	5	localParcel1	Parcel
        //   8	83	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	63	89	finally
      }
      
      public void systemReady()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(105, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterMoveCallback(IPackageMoveObserver paramIPackageMoveObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          if (paramIPackageMoveObserver != null) {
            localIBinder = paramIPackageMoveObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(119, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void updateExternalMediaStatus(boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: iload_1
        //   21: ifeq +57 -> 78
        //   24: iconst_1
        //   25: istore_3
        //   26: aload 5
        //   28: iload_3
        //   29: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   32: iload_2
        //   33: ifeq +50 -> 83
        //   36: iload 4
        //   38: istore_3
        //   39: aload 5
        //   41: iload_3
        //   42: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   45: aload_0
        //   46: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 115
        //   51: aload 5
        //   53: aload 6
        //   55: iconst_0
        //   56: invokeinterface 58 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 61	android/os/Parcel:readException	()V
        //   67: aload 6
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload 5
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: return
        //   78: iconst_0
        //   79: istore_3
        //   80: goto -54 -> 26
        //   83: iconst_0
        //   84: istore_3
        //   85: goto -46 -> 39
        //   88: astore 7
        //   90: aload 6
        //   92: invokevirtual 68	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 68	android/os/Parcel:recycle	()V
        //   100: aload 7
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramBoolean1	boolean
        //   0	103	2	paramBoolean2	boolean
        //   25	60	3	i	int
        //   1	36	4	j	int
        //   6	90	5	localParcel1	Parcel
        //   11	80	6	localParcel2	Parcel
        //   88	13	7	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   13	20	88	finally
        //   26	32	88	finally
        //   39	67	88	finally
      }
      
      /* Error */
      public boolean updateIntentVerificationStatus(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 52	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: sipush 130
        //   42: aload 5
        //   44: aload 6
        //   46: iconst_0
        //   47: invokeinterface 58 5 0
        //   52: pop
        //   53: aload 6
        //   55: invokevirtual 61	android/os/Parcel:readException	()V
        //   58: aload 6
        //   60: invokevirtual 65	android/os/Parcel:readInt	()I
        //   63: istore_2
        //   64: iload_2
        //   65: ifeq +19 -> 84
        //   68: iconst_1
        //   69: istore 4
        //   71: aload 6
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload 5
        //   78: invokevirtual 68	android/os/Parcel:recycle	()V
        //   81: iload 4
        //   83: ireturn
        //   84: iconst_0
        //   85: istore 4
        //   87: goto -16 -> 71
        //   90: astore_1
        //   91: aload 6
        //   93: invokevirtual 68	android/os/Parcel:recycle	()V
        //   96: aload 5
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramString	String
        //   0	103	2	paramInt1	int
        //   0	103	3	paramInt2	int
        //   69	17	4	bool	boolean
        //   3	94	5	localParcel1	Parcel
        //   8	84	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	64	90	finally
      }
      
      public void updatePackagesIfNeeded()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          this.mRemote.transact(108, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void updatePermissionFlags(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void updatePermissionFlagsForAllApps(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void verifyIntentFilter(int paramInt1, int paramInt2, List<String> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeStringList(paramList);
          this.mRemote.transact(128, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void verifyPendingInstall(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(126, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IPackageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */