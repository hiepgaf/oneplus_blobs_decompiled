package android.app.admin;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.graphics.Bitmap;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public abstract interface IDevicePolicyManager
  extends IInterface
{
  public abstract void addCrossProfileIntentFilter(ComponentName paramComponentName, IntentFilter paramIntentFilter, int paramInt)
    throws RemoteException;
  
  public abstract boolean addCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract void addPersistentPreferredActivity(ComponentName paramComponentName1, IntentFilter paramIntentFilter, ComponentName paramComponentName2)
    throws RemoteException;
  
  public abstract boolean approveCaCert(String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void choosePrivateKeyAlias(int paramInt, Uri paramUri, String paramString, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void clearCrossProfileIntentFilters(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void clearDeviceOwner(String paramString)
    throws RemoteException;
  
  public abstract void clearPackagePersistentPreferredActivities(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract void clearProfileOwner(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract UserHandle createAndManageUser(ComponentName paramComponentName1, String paramString, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle, int paramInt)
    throws RemoteException;
  
  public abstract void enableSystemApp(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract int enableSystemAppWithIntent(ComponentName paramComponentName, Intent paramIntent)
    throws RemoteException;
  
  public abstract void enforceCanManageCaCerts(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void forceRemoveActiveAdmin(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract String[] getAccountTypesWithManagementDisabled()
    throws RemoteException;
  
  public abstract String[] getAccountTypesWithManagementDisabledAsUser(int paramInt)
    throws RemoteException;
  
  public abstract List<ComponentName> getActiveAdmins(int paramInt)
    throws RemoteException;
  
  public abstract String getAlwaysOnVpnPackage(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract Bundle getApplicationRestrictions(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract String getApplicationRestrictionsManagingPackage(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean getAutoTimeRequired()
    throws RemoteException;
  
  public abstract boolean getBluetoothContactSharingDisabled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean getBluetoothContactSharingDisabledForUser(int paramInt)
    throws RemoteException;
  
  public abstract boolean getCameraDisabled(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract String getCertInstallerPackage(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean getCrossProfileCallerIdDisabled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean getCrossProfileCallerIdDisabledForUser(int paramInt)
    throws RemoteException;
  
  public abstract boolean getCrossProfileContactsSearchDisabled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean getCrossProfileContactsSearchDisabledForUser(int paramInt)
    throws RemoteException;
  
  public abstract List<String> getCrossProfileWidgetProviders(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract int getCurrentFailedPasswordAttempts(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ComponentName getDeviceOwnerComponent(boolean paramBoolean)
    throws RemoteException;
  
  public abstract CharSequence getDeviceOwnerLockScreenInfo()
    throws RemoteException;
  
  public abstract String getDeviceOwnerName()
    throws RemoteException;
  
  public abstract int getDeviceOwnerUserId()
    throws RemoteException;
  
  public abstract boolean getDoNotAskCredentialsOnBoot()
    throws RemoteException;
  
  public abstract boolean getForceEphemeralUsers(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract ComponentName getGlobalProxyAdmin(int paramInt)
    throws RemoteException;
  
  public abstract List<String> getKeepUninstalledPackages(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract int getKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract String[] getLockTaskPackages(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract CharSequence getLongSupportMessage(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract CharSequence getLongSupportMessageForUser(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract int getMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract long getMaximumTimeToLock(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract long getMaximumTimeToLockForUserAndProfiles(int paramInt)
    throws RemoteException;
  
  public abstract int getOrganizationColor(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract int getOrganizationColorForUser(int paramInt)
    throws RemoteException;
  
  public abstract CharSequence getOrganizationName(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract CharSequence getOrganizationNameForUser(int paramInt)
    throws RemoteException;
  
  public abstract long getPasswordExpiration(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract long getPasswordExpirationTimeout(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordHistoryLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordMinimumLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordMinimumLetters(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPasswordQuality(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract int getPermissionPolicy(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract List getPermittedAccessibilityServices(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract List getPermittedAccessibilityServicesForUser(int paramInt)
    throws RemoteException;
  
  public abstract List getPermittedInputMethods(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract List getPermittedInputMethodsForCurrentUser()
    throws RemoteException;
  
  public abstract ComponentName getProfileOwner(int paramInt)
    throws RemoteException;
  
  public abstract String getProfileOwnerName(int paramInt)
    throws RemoteException;
  
  public abstract int getProfileWithMinimumFailedPasswordsForWipe(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void getRemoveWarning(ComponentName paramComponentName, RemoteCallback paramRemoteCallback, int paramInt)
    throws RemoteException;
  
  public abstract long getRequiredStrongAuthTimeout(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ComponentName getRestrictionsProvider(int paramInt)
    throws RemoteException;
  
  public abstract boolean getScreenCaptureDisabled(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract CharSequence getShortSupportMessage(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract CharSequence getShortSupportMessageForUser(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean getStorageEncryption(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract int getStorageEncryptionStatus(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract SystemUpdatePolicy getSystemUpdatePolicy()
    throws RemoteException;
  
  public abstract List<PersistableBundle> getTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getUserProvisioningState()
    throws RemoteException;
  
  public abstract Bundle getUserRestrictions(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract String getWifiMacAddress(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean hasGrantedPolicy(ComponentName paramComponentName, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean hasUserSetupCompleted()
    throws RemoteException;
  
  public abstract boolean installCaCert(ComponentName paramComponentName, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract boolean installKeyPair(ComponentName paramComponentName, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean isAccessibilityServicePermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isActivePasswordSufficient(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean isAdminActive(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean isAffiliatedUser()
    throws RemoteException;
  
  public abstract boolean isApplicationHidden(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean isBackupServiceEnabled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean isCaCertApproved(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isCallerApplicationRestrictionsManagingPackage()
    throws RemoteException;
  
  public abstract boolean isDeviceProvisioned()
    throws RemoteException;
  
  public abstract boolean isDeviceProvisioningConfigApplied()
    throws RemoteException;
  
  public abstract boolean isInputMethodPermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isLockTaskPermitted(String paramString)
    throws RemoteException;
  
  public abstract boolean isManagedProfile(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean isMasterVolumeMuted(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean isPackageSuspended(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean isProfileActivePasswordSufficientForParent(int paramInt)
    throws RemoteException;
  
  public abstract boolean isProvisioningAllowed(String paramString)
    throws RemoteException;
  
  public abstract boolean isRemovingAdmin(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean isSecurityLoggingEnabled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean isSeparateProfileChallengeAllowed(int paramInt)
    throws RemoteException;
  
  public abstract boolean isSystemOnlyUser(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean isUninstallBlocked(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean isUninstallInQueue(String paramString)
    throws RemoteException;
  
  public abstract void lockNow(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void notifyLockTaskModeChanged(boolean paramBoolean, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void notifyPendingSystemUpdate(long paramLong)
    throws RemoteException;
  
  public abstract boolean packageHasActiveAdmins(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void reboot(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void removeActiveAdmin(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean removeCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean removeKeyPair(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean removeUser(ComponentName paramComponentName, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void reportFailedFingerprintAttempt(int paramInt)
    throws RemoteException;
  
  public abstract void reportFailedPasswordAttempt(int paramInt)
    throws RemoteException;
  
  public abstract void reportKeyguardDismissed(int paramInt)
    throws RemoteException;
  
  public abstract void reportKeyguardSecured(int paramInt)
    throws RemoteException;
  
  public abstract void reportPasswordChanged(int paramInt)
    throws RemoteException;
  
  public abstract void reportSuccessfulFingerprintAttempt(int paramInt)
    throws RemoteException;
  
  public abstract void reportSuccessfulPasswordAttempt(int paramInt)
    throws RemoteException;
  
  public abstract boolean requestBugreport(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean resetPassword(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice retrievePreRebootSecurityLogs(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract ParceledListSlice retrieveSecurityLogs(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void setAccountManagementDisabled(ComponentName paramComponentName, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setActiveAdmin(ComponentName paramComponentName, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setActivePasswordState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
    throws RemoteException;
  
  public abstract void setAffiliationIds(ComponentName paramComponentName, List<String> paramList)
    throws RemoteException;
  
  public abstract boolean setAlwaysOnVpnPackage(ComponentName paramComponentName, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setApplicationHidden(ComponentName paramComponentName, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setApplicationRestrictions(ComponentName paramComponentName, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean setApplicationRestrictionsManagingPackage(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract void setAutoTimeRequired(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setBackupServiceEnabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setBluetoothContactSharingDisabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setCameraDisabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setCertInstallerPackage(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract void setCrossProfileCallerIdDisabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setCrossProfileContactsSearchDisabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setDeviceOwner(ComponentName paramComponentName, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setDeviceOwnerLockScreenInfo(ComponentName paramComponentName, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract void setDeviceProvisioningConfigApplied()
    throws RemoteException;
  
  public abstract void setForceEphemeralUsers(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ComponentName setGlobalProxy(ComponentName paramComponentName, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setGlobalSetting(ComponentName paramComponentName, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setKeepUninstalledPackages(ComponentName paramComponentName, List<String> paramList)
    throws RemoteException;
  
  public abstract boolean setKeyguardDisabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setLockTaskPackages(ComponentName paramComponentName, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void setLongSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract void setMasterVolumeMuted(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMaximumTimeToLock(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setOrganizationColor(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract void setOrganizationColorForUser(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setOrganizationName(ComponentName paramComponentName, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract String[] setPackagesSuspended(ComponentName paramComponentName, String[] paramArrayOfString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordExpirationTimeout(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordHistoryLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordMinimumLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordMinimumLetters(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPasswordQuality(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void setPermissionPolicy(ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean setPermittedAccessibilityServices(ComponentName paramComponentName, List paramList)
    throws RemoteException;
  
  public abstract boolean setPermittedInputMethods(ComponentName paramComponentName, List paramList)
    throws RemoteException;
  
  public abstract void setProfileEnabled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void setProfileName(ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean setProfileOwner(ComponentName paramComponentName, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setRecommendedGlobalProxy(ComponentName paramComponentName, ProxyInfo paramProxyInfo)
    throws RemoteException;
  
  public abstract void setRequiredStrongAuthTimeout(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setRestrictionsProvider(ComponentName paramComponentName1, ComponentName paramComponentName2)
    throws RemoteException;
  
  public abstract void setScreenCaptureDisabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSecureSetting(ComponentName paramComponentName, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setSecurityLoggingEnabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setShortSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract boolean setStatusBarDisabled(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int setStorageEncryption(ComponentName paramComponentName, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSystemUpdatePolicy(ComponentName paramComponentName, SystemUpdatePolicy paramSystemUpdatePolicy)
    throws RemoteException;
  
  public abstract void setTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUninstallBlocked(ComponentName paramComponentName, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUserIcon(ComponentName paramComponentName, Bitmap paramBitmap)
    throws RemoteException;
  
  public abstract void setUserProvisioningState(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setUserRestriction(ComponentName paramComponentName, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void startManagedQuickContact(String paramString, long paramLong1, boolean paramBoolean, long paramLong2, Intent paramIntent)
    throws RemoteException;
  
  public abstract boolean switchUser(ComponentName paramComponentName, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void uninstallCaCerts(ComponentName paramComponentName, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void uninstallPackageWithActiveAdmins(String paramString)
    throws RemoteException;
  
  public abstract void wipeData(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IDevicePolicyManager
  {
    private static final String DESCRIPTOR = "android.app.admin.IDevicePolicyManager";
    static final int TRANSACTION_addCrossProfileIntentFilter = 104;
    static final int TRANSACTION_addCrossProfileWidgetProvider = 146;
    static final int TRANSACTION_addPersistentPreferredActivity = 93;
    static final int TRANSACTION_approveCaCert = 84;
    static final int TRANSACTION_choosePrivateKeyAlias = 88;
    static final int TRANSACTION_clearCrossProfileIntentFilters = 105;
    static final int TRANSACTION_clearDeviceOwner = 68;
    static final int TRANSACTION_clearPackagePersistentPreferredActivities = 94;
    static final int TRANSACTION_clearProfileOwner = 75;
    static final int TRANSACTION_createAndManageUser = 116;
    static final int TRANSACTION_enableSystemApp = 119;
    static final int TRANSACTION_enableSystemAppWithIntent = 120;
    static final int TRANSACTION_enforceCanManageCaCerts = 83;
    static final int TRANSACTION_forceRemoveActiveAdmin = 55;
    static final int TRANSACTION_getAccountTypesWithManagementDisabled = 122;
    static final int TRANSACTION_getAccountTypesWithManagementDisabledAsUser = 123;
    static final int TRANSACTION_getActiveAdmins = 51;
    static final int TRANSACTION_getAlwaysOnVpnPackage = 92;
    static final int TRANSACTION_getApplicationRestrictions = 96;
    static final int TRANSACTION_getApplicationRestrictionsManagingPackage = 98;
    static final int TRANSACTION_getAutoTimeRequired = 150;
    static final int TRANSACTION_getBluetoothContactSharingDisabled = 142;
    static final int TRANSACTION_getBluetoothContactSharingDisabledForUser = 143;
    static final int TRANSACTION_getCameraDisabled = 44;
    static final int TRANSACTION_getCertInstallerPackage = 90;
    static final int TRANSACTION_getCrossProfileCallerIdDisabled = 135;
    static final int TRANSACTION_getCrossProfileCallerIdDisabledForUser = 136;
    static final int TRANSACTION_getCrossProfileContactsSearchDisabled = 138;
    static final int TRANSACTION_getCrossProfileContactsSearchDisabledForUser = 139;
    static final int TRANSACTION_getCrossProfileWidgetProviders = 148;
    static final int TRANSACTION_getCurrentFailedPasswordAttempts = 24;
    static final int TRANSACTION_getDeviceOwnerComponent = 66;
    static final int TRANSACTION_getDeviceOwnerLockScreenInfo = 78;
    static final int TRANSACTION_getDeviceOwnerName = 67;
    static final int TRANSACTION_getDeviceOwnerUserId = 69;
    static final int TRANSACTION_getDoNotAskCredentialsOnBoot = 159;
    static final int TRANSACTION_getForceEphemeralUsers = 152;
    static final int TRANSACTION_getGlobalProxyAdmin = 37;
    static final int TRANSACTION_getKeepUninstalledPackages = 167;
    static final int TRANSACTION_getKeyguardDisabledFeatures = 48;
    static final int TRANSACTION_getLockTaskPackages = 125;
    static final int TRANSACTION_getLongSupportMessage = 175;
    static final int TRANSACTION_getLongSupportMessageForUser = 177;
    static final int TRANSACTION_getMaximumFailedPasswordsForWipe = 27;
    static final int TRANSACTION_getMaximumTimeToLock = 30;
    static final int TRANSACTION_getMaximumTimeToLockForUserAndProfiles = 31;
    static final int TRANSACTION_getOrganizationColor = 181;
    static final int TRANSACTION_getOrganizationColorForUser = 182;
    static final int TRANSACTION_getOrganizationName = 184;
    static final int TRANSACTION_getOrganizationNameForUser = 185;
    static final int TRANSACTION_getPasswordExpiration = 21;
    static final int TRANSACTION_getPasswordExpirationTimeout = 20;
    static final int TRANSACTION_getPasswordHistoryLength = 18;
    static final int TRANSACTION_getPasswordMinimumLength = 4;
    static final int TRANSACTION_getPasswordMinimumLetters = 10;
    static final int TRANSACTION_getPasswordMinimumLowerCase = 8;
    static final int TRANSACTION_getPasswordMinimumNonLetter = 16;
    static final int TRANSACTION_getPasswordMinimumNumeric = 12;
    static final int TRANSACTION_getPasswordMinimumSymbols = 14;
    static final int TRANSACTION_getPasswordMinimumUpperCase = 6;
    static final int TRANSACTION_getPasswordQuality = 2;
    static final int TRANSACTION_getPermissionGrantState = 164;
    static final int TRANSACTION_getPermissionPolicy = 162;
    static final int TRANSACTION_getPermittedAccessibilityServices = 107;
    static final int TRANSACTION_getPermittedAccessibilityServicesForUser = 108;
    static final int TRANSACTION_getPermittedInputMethods = 111;
    static final int TRANSACTION_getPermittedInputMethodsForCurrentUser = 112;
    static final int TRANSACTION_getProfileOwner = 71;
    static final int TRANSACTION_getProfileOwnerName = 72;
    static final int TRANSACTION_getProfileWithMinimumFailedPasswordsForWipe = 25;
    static final int TRANSACTION_getRemoveWarning = 53;
    static final int TRANSACTION_getRequiredStrongAuthTimeout = 33;
    static final int TRANSACTION_getRestrictionsProvider = 101;
    static final int TRANSACTION_getScreenCaptureDisabled = 46;
    static final int TRANSACTION_getShortSupportMessage = 173;
    static final int TRANSACTION_getShortSupportMessageForUser = 176;
    static final int TRANSACTION_getStorageEncryption = 40;
    static final int TRANSACTION_getStorageEncryptionStatus = 41;
    static final int TRANSACTION_getSystemUpdatePolicy = 156;
    static final int TRANSACTION_getTrustAgentConfiguration = 145;
    static final int TRANSACTION_getUserProvisioningState = 186;
    static final int TRANSACTION_getUserRestrictions = 103;
    static final int TRANSACTION_getWifiMacAddress = 170;
    static final int TRANSACTION_hasGrantedPolicy = 56;
    static final int TRANSACTION_hasUserSetupCompleted = 76;
    static final int TRANSACTION_installCaCert = 81;
    static final int TRANSACTION_installKeyPair = 86;
    static final int TRANSACTION_isAccessibilityServicePermittedByAdmin = 109;
    static final int TRANSACTION_isActivePasswordSufficient = 22;
    static final int TRANSACTION_isAdminActive = 50;
    static final int TRANSACTION_isAffiliatedUser = 189;
    static final int TRANSACTION_isApplicationHidden = 115;
    static final int TRANSACTION_isBackupServiceEnabled = 200;
    static final int TRANSACTION_isCaCertApproved = 85;
    static final int TRANSACTION_isCallerApplicationRestrictionsManagingPackage = 99;
    static final int TRANSACTION_isDeviceProvisioned = 196;
    static final int TRANSACTION_isDeviceProvisioningConfigApplied = 197;
    static final int TRANSACTION_isInputMethodPermittedByAdmin = 113;
    static final int TRANSACTION_isLockTaskPermitted = 126;
    static final int TRANSACTION_isManagedProfile = 168;
    static final int TRANSACTION_isMasterVolumeMuted = 130;
    static final int TRANSACTION_isPackageSuspended = 80;
    static final int TRANSACTION_isProfileActivePasswordSufficientForParent = 23;
    static final int TRANSACTION_isProvisioningAllowed = 165;
    static final int TRANSACTION_isRemovingAdmin = 153;
    static final int TRANSACTION_isSecurityLoggingEnabled = 191;
    static final int TRANSACTION_isSeparateProfileChallengeAllowed = 178;
    static final int TRANSACTION_isSystemOnlyUser = 169;
    static final int TRANSACTION_isUninstallBlocked = 133;
    static final int TRANSACTION_isUninstallInQueue = 194;
    static final int TRANSACTION_lockNow = 34;
    static final int TRANSACTION_notifyLockTaskModeChanged = 131;
    static final int TRANSACTION_notifyPendingSystemUpdate = 160;
    static final int TRANSACTION_packageHasActiveAdmins = 52;
    static final int TRANSACTION_reboot = 171;
    static final int TRANSACTION_removeActiveAdmin = 54;
    static final int TRANSACTION_removeCrossProfileWidgetProvider = 147;
    static final int TRANSACTION_removeKeyPair = 87;
    static final int TRANSACTION_removeUser = 117;
    static final int TRANSACTION_reportFailedFingerprintAttempt = 61;
    static final int TRANSACTION_reportFailedPasswordAttempt = 59;
    static final int TRANSACTION_reportKeyguardDismissed = 63;
    static final int TRANSACTION_reportKeyguardSecured = 64;
    static final int TRANSACTION_reportPasswordChanged = 58;
    static final int TRANSACTION_reportSuccessfulFingerprintAttempt = 62;
    static final int TRANSACTION_reportSuccessfulPasswordAttempt = 60;
    static final int TRANSACTION_requestBugreport = 42;
    static final int TRANSACTION_resetPassword = 28;
    static final int TRANSACTION_retrievePreRebootSecurityLogs = 193;
    static final int TRANSACTION_retrieveSecurityLogs = 192;
    static final int TRANSACTION_setAccountManagementDisabled = 121;
    static final int TRANSACTION_setActiveAdmin = 49;
    static final int TRANSACTION_setActivePasswordState = 57;
    static final int TRANSACTION_setAffiliationIds = 188;
    static final int TRANSACTION_setAlwaysOnVpnPackage = 91;
    static final int TRANSACTION_setApplicationHidden = 114;
    static final int TRANSACTION_setApplicationRestrictions = 95;
    static final int TRANSACTION_setApplicationRestrictionsManagingPackage = 97;
    static final int TRANSACTION_setAutoTimeRequired = 149;
    static final int TRANSACTION_setBackupServiceEnabled = 199;
    static final int TRANSACTION_setBluetoothContactSharingDisabled = 141;
    static final int TRANSACTION_setCameraDisabled = 43;
    static final int TRANSACTION_setCertInstallerPackage = 89;
    static final int TRANSACTION_setCrossProfileCallerIdDisabled = 134;
    static final int TRANSACTION_setCrossProfileContactsSearchDisabled = 137;
    static final int TRANSACTION_setDeviceOwner = 65;
    static final int TRANSACTION_setDeviceOwnerLockScreenInfo = 77;
    static final int TRANSACTION_setDeviceProvisioningConfigApplied = 198;
    static final int TRANSACTION_setForceEphemeralUsers = 151;
    static final int TRANSACTION_setGlobalProxy = 36;
    static final int TRANSACTION_setGlobalSetting = 127;
    static final int TRANSACTION_setKeepUninstalledPackages = 166;
    static final int TRANSACTION_setKeyguardDisabled = 157;
    static final int TRANSACTION_setKeyguardDisabledFeatures = 47;
    static final int TRANSACTION_setLockTaskPackages = 124;
    static final int TRANSACTION_setLongSupportMessage = 174;
    static final int TRANSACTION_setMasterVolumeMuted = 129;
    static final int TRANSACTION_setMaximumFailedPasswordsForWipe = 26;
    static final int TRANSACTION_setMaximumTimeToLock = 29;
    static final int TRANSACTION_setOrganizationColor = 179;
    static final int TRANSACTION_setOrganizationColorForUser = 180;
    static final int TRANSACTION_setOrganizationName = 183;
    static final int TRANSACTION_setPackagesSuspended = 79;
    static final int TRANSACTION_setPasswordExpirationTimeout = 19;
    static final int TRANSACTION_setPasswordHistoryLength = 17;
    static final int TRANSACTION_setPasswordMinimumLength = 3;
    static final int TRANSACTION_setPasswordMinimumLetters = 9;
    static final int TRANSACTION_setPasswordMinimumLowerCase = 7;
    static final int TRANSACTION_setPasswordMinimumNonLetter = 15;
    static final int TRANSACTION_setPasswordMinimumNumeric = 11;
    static final int TRANSACTION_setPasswordMinimumSymbols = 13;
    static final int TRANSACTION_setPasswordMinimumUpperCase = 5;
    static final int TRANSACTION_setPasswordQuality = 1;
    static final int TRANSACTION_setPermissionGrantState = 163;
    static final int TRANSACTION_setPermissionPolicy = 161;
    static final int TRANSACTION_setPermittedAccessibilityServices = 106;
    static final int TRANSACTION_setPermittedInputMethods = 110;
    static final int TRANSACTION_setProfileEnabled = 73;
    static final int TRANSACTION_setProfileName = 74;
    static final int TRANSACTION_setProfileOwner = 70;
    static final int TRANSACTION_setRecommendedGlobalProxy = 38;
    static final int TRANSACTION_setRequiredStrongAuthTimeout = 32;
    static final int TRANSACTION_setRestrictionsProvider = 100;
    static final int TRANSACTION_setScreenCaptureDisabled = 45;
    static final int TRANSACTION_setSecureSetting = 128;
    static final int TRANSACTION_setSecurityLoggingEnabled = 190;
    static final int TRANSACTION_setShortSupportMessage = 172;
    static final int TRANSACTION_setStatusBarDisabled = 158;
    static final int TRANSACTION_setStorageEncryption = 39;
    static final int TRANSACTION_setSystemUpdatePolicy = 155;
    static final int TRANSACTION_setTrustAgentConfiguration = 144;
    static final int TRANSACTION_setUninstallBlocked = 132;
    static final int TRANSACTION_setUserIcon = 154;
    static final int TRANSACTION_setUserProvisioningState = 187;
    static final int TRANSACTION_setUserRestriction = 102;
    static final int TRANSACTION_startManagedQuickContact = 140;
    static final int TRANSACTION_switchUser = 118;
    static final int TRANSACTION_uninstallCaCerts = 82;
    static final int TRANSACTION_uninstallPackageWithActiveAdmins = 195;
    static final int TRANSACTION_wipeData = 35;
    
    public Stub()
    {
      attachInterface(this, "android.app.admin.IDevicePolicyManager");
    }
    
    public static IDevicePolicyManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.admin.IDevicePolicyManager");
      if ((localIInterface != null) && ((localIInterface instanceof IDevicePolicyManager))) {
        return (IDevicePolicyManager)localIInterface;
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
      boolean bool;
      label1701:
      label1776:
      label1845:
      label1920:
      label1989:
      label2064:
      label2133:
      label2208:
      label2277:
      label2352:
      label2421:
      label2496:
      label2565:
      label2640:
      label2709:
      label2784:
      label2853:
      label2928:
      long l1;
      label2999:
      label3076:
      label3153:
      label3213:
      label3412:
      label3487:
      label3601:
      label3678:
      label3777:
      label3854:
      label3983:
      label4101:
      label4169:
      label4238:
      label4329:
      label4391:
      label4460:
      label4522:
      label4591:
      label4659:
      label4734:
      label4801:
      label4870:
      Object localObject2;
      label5016:
      label5187:
      label5451:
      label5506:
      label5645:
      label5954:
      label6070:
      label6139:
      label6207:
      label6365:
      Object localObject3;
      Object localObject4;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.admin.IDevicePolicyManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1701;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordQuality((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1776;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordQuality((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1845;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordMinimumLength((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1920;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordMinimumLength((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 5: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1989;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordMinimumUpperCase((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 6: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2064;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordMinimumUpperCase((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 7: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2133;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordMinimumLowerCase((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2208;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordMinimumLowerCase((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2277;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordMinimumLetters((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 10: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2352;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordMinimumLetters((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 11: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2421;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordMinimumNumeric((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 12: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2496;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordMinimumNumeric((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 13: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2565;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordMinimumSymbols((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 14: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2640;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordMinimumSymbols((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 15: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2709;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordMinimumNonLetter((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 16: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2784;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordMinimumNonLetter((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 17: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2853;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordHistoryLength((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 18: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label2928;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getPasswordHistoryLength((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 19: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          l1 = paramParcel1.readLong();
          if (paramParcel1.readInt() == 0) {
            break label2999;
          }
        }
        for (bool = true;; bool = false)
        {
          setPasswordExpirationTimeout((ComponentName)localObject1, l1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 20: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label3076;
          }
        }
        for (bool = true;; bool = false)
        {
          l1 = getPasswordExpirationTimeout((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l1);
          return true;
          localObject1 = null;
          break;
        }
      case 21: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label3153;
          }
        }
        for (bool = true;; bool = false)
        {
          l1 = getPasswordExpiration((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l1);
          return true;
          localObject1 = null;
          break;
        }
      case 22: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = isActivePasswordSufficient(paramInt1, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label3213;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 23: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isProfileActivePasswordSufficientForParent(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramInt1 = getCurrentFailedPasswordAttempts(paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 25: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramInt1 = getProfileWithMinimumFailedPasswordsForWipe(paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 26: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label3412;
          }
        }
        for (bool = true;; bool = false)
        {
          setMaximumFailedPasswordsForWipe((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 27: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label3487;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getMaximumFailedPasswordsForWipe((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 28: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = resetPassword(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          l1 = paramParcel1.readLong();
          if (paramParcel1.readInt() == 0) {
            break label3601;
          }
        }
        for (bool = true;; bool = false)
        {
          setMaximumTimeToLock((ComponentName)localObject1, l1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 30: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label3678;
          }
        }
        for (bool = true;; bool = false)
        {
          l1 = getMaximumTimeToLock((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l1);
          return true;
          localObject1 = null;
          break;
        }
      case 31: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        l1 = getMaximumTimeToLockForUserAndProfiles(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l1);
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          l1 = paramParcel1.readLong();
          if (paramParcel1.readInt() == 0) {
            break label3777;
          }
        }
        for (bool = true;; bool = false)
        {
          setRequiredStrongAuthTimeout((ComponentName)localObject1, l1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 33: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label3854;
          }
        }
        for (bool = true;; bool = false)
        {
          l1 = getRequiredStrongAuthTimeout((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeLong(l1);
          return true;
          localObject1 = null;
          break;
        }
      case 34: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          lockNow(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 35: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        wipeData(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 36: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = setGlobalProxy((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3983;
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
      case 37: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getGlobalProxyAdmin(paramParcel1.readInt());
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
      case 38: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label4101;
          }
        }
        for (paramParcel1 = (ProxyInfo)ProxyInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setRecommendedGlobalProxy((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 39: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label4169;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = setStorageEncryption((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 40: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = getStorageEncryption((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label4238;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 41: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = getStorageEncryptionStatus(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 42: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = requestBugreport(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label4329;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 43: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label4391;
          }
        }
        for (bool = true;; bool = false)
        {
          setCameraDisabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 44: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = getCameraDisabled((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label4460;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 45: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label4522;
          }
        }
        for (bool = true;; bool = false)
        {
          setScreenCaptureDisabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 46: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = getScreenCaptureDisabled((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label4591;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 47: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label4659;
          }
        }
        for (bool = true;; bool = false)
        {
          setKeyguardDisabledFeatures((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 48: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label4734;
          }
        }
        for (bool = true;; bool = false)
        {
          paramInt1 = getKeyguardDisabledFeatures((ComponentName)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 49: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label4801;
          }
        }
        for (bool = true;; bool = false)
        {
          setActiveAdmin((ComponentName)localObject1, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 50: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isAdminActive((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label4870;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 51: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getActiveAdmins(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 52: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = packageHasActiveAdmins(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 53: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label5016;
          }
        }
        for (localObject2 = (RemoteCallback)RemoteCallback.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          getRemoveWarning((ComponentName)localObject1, (RemoteCallback)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 54: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          removeActiveAdmin((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 55: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          forceRemoveActiveAdmin((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 56: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = hasGrantedPolicy((ComponentName)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label5187;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 57: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        setActivePasswordState(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 58: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        reportPasswordChanged(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 59: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        reportFailedPasswordAttempt(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 60: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        reportSuccessfulPasswordAttempt(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 61: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        reportFailedFingerprintAttempt(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 62: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        reportSuccessfulFingerprintAttempt(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 63: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        reportKeyguardDismissed(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 64: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        reportKeyguardSecured(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 65: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setDeviceOwner((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label5451;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 66: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          paramParcel1 = getDeviceOwnerComponent(bool);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label5506;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          bool = false;
          break;
          paramParcel2.writeInt(0);
        }
      case 67: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getDeviceOwnerName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 68: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        clearDeviceOwner(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 69: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = getDeviceOwnerUserId();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 70: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setProfileOwner((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label5645;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 71: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getProfileOwner(paramParcel1.readInt());
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
      case 72: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getProfileOwnerName(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 73: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setProfileEnabled(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 74: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setProfileName((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 75: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          clearProfileOwner(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 76: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = hasUserSetupCompleted();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 77: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label5954;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setDeviceOwnerLockScreenInfo((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 78: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getDeviceOwnerLockScreenInfo();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 79: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.createStringArray();
          if (paramParcel1.readInt() == 0) {
            break label6070;
          }
        }
        for (bool = true;; bool = false)
        {
          paramParcel1 = setPackagesSuspended((ComponentName)localObject1, (String[])localObject2, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeStringArray(paramParcel1);
          return true;
          localObject1 = null;
          break;
        }
      case 80: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isPackageSuspended((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label6139;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 81: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = installCaCert((ComponentName)localObject1, paramParcel1.createByteArray());
          paramParcel2.writeNoException();
          if (!bool) {
            break label6207;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 82: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          uninstallCaCerts((ComponentName)localObject1, paramParcel1.createStringArray());
          paramParcel2.writeNoException();
          return true;
        }
      case 83: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          enforceCanManageCaCerts(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 84: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        localObject1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = approveCaCert((String)localObject1, paramInt1, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label6365;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 85: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isCaCertApproved(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 86: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.createByteArray();
          localObject3 = paramParcel1.createByteArray();
          localObject4 = paramParcel1.createByteArray();
          String str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label6516;
          }
          bool = true;
          bool = installKeyPair((ComponentName)localObject1, (byte[])localObject2, (byte[])localObject3, (byte[])localObject4, str, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label6522;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label6474;
        }
      case 87: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = removeKeyPair((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label6590;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 88: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          choosePrivateKeyAlias(paramInt1, (Uri)localObject1, paramParcel1.readString(), paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          return true;
        }
      case 89: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setCertInstallerPackage((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 90: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getCertInstallerPackage(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 91: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label6830;
          }
          bool = true;
          bool = setAlwaysOnVpnPackage((ComponentName)localObject1, (String)localObject2, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label6836;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label6794;
        }
      case 92: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getAlwaysOnVpnPackage(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 93: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label6978;
          }
          localObject2 = (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label6984;
          }
        }
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addPersistentPreferredActivity((ComponentName)localObject1, (IntentFilter)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label6937;
        }
      case 94: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          clearPackagePersistentPreferredActivities((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 95: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label7112;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setApplicationRestrictions((ComponentName)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 96: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getApplicationRestrictions((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label7182;
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
      case 97: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setApplicationRestrictionsManagingPackage((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label7253;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 98: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getApplicationRestrictionsManagingPackage(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 99: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isCallerApplicationRestrictionsManagingPackage();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 100: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label7407;
          }
        }
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setRestrictionsProvider((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 101: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getRestrictionsProvider(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label7521;
          }
        }
        for (bool = true;; bool = false)
        {
          setUserRestriction((ComponentName)localObject1, (String)localObject2, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 103: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getUserRestrictions(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label7585;
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
      case 104: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label7665;
          }
        }
        for (localObject2 = (IntentFilter)IntentFilter.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          addCrossProfileIntentFilter((ComponentName)localObject1, (IntentFilter)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 105: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          clearCrossProfileIntentFilters(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 106: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setPermittedAccessibilityServices((ComponentName)localObject1, paramParcel1.readArrayList(getClass().getClassLoader()));
          paramParcel2.writeNoException();
          if (!bool) {
            break label7783;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 107: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getPermittedAccessibilityServices(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeList(paramParcel1);
          return true;
        }
      case 108: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getPermittedAccessibilityServicesForUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeList(paramParcel1);
        return true;
      case 109: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isAccessibilityServicePermittedByAdmin((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label7929;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 110: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setPermittedInputMethods((ComponentName)localObject1, paramParcel1.readArrayList(getClass().getClassLoader()));
          paramParcel2.writeNoException();
          if (!bool) {
            break label8004;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 111: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getPermittedInputMethods(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeList(paramParcel1);
          return true;
        }
      case 112: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getPermittedInputMethodsForCurrentUser();
        paramParcel2.writeNoException();
        paramParcel2.writeList(paramParcel1);
        return true;
      case 113: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isInputMethodPermittedByAdmin((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label8146;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 114: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label8230;
          }
          bool = true;
          bool = setApplicationHidden((ComponentName)localObject1, (String)localObject2, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label8236;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label8194;
        }
      case 115: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isApplicationHidden((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label8304;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 116: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject4 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label8428;
          }
          localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label8434;
          }
          localObject3 = (PersistableBundle)PersistableBundle.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = createAndManageUser((ComponentName)localObject1, (String)localObject4, (ComponentName)localObject2, (PersistableBundle)localObject3, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label8440;
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
          break label8363;
          localObject3 = null;
          break label8384;
          paramParcel2.writeInt(0);
        }
      case 117: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label8528;
          }
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          bool = removeUser((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label8533;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label8495;
        }
      case 118: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label8618;
          }
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          bool = switchUser((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label8623;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label8585;
        }
      case 119: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          enableSystemApp((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 120: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label8749;
          }
        }
        for (paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = enableSystemAppWithIntent((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 121: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label8819;
          }
        }
        for (bool = true;; bool = false)
        {
          setAccountManagementDisabled((ComponentName)localObject1, (String)localObject2, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 122: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getAccountTypesWithManagementDisabled();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 123: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getAccountTypesWithManagementDisabledAsUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 124: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setLockTaskPackages((ComponentName)localObject1, paramParcel1.createStringArray());
          paramParcel2.writeNoException();
          return true;
        }
      case 125: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getLockTaskPackages(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeStringArray(paramParcel1);
          return true;
        }
      case 126: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isLockTaskPermitted(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 127: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setGlobalSetting((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 128: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setSecureSetting((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 129: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label9172;
          }
        }
        for (bool = true;; bool = false)
        {
          setMasterVolumeMuted((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 130: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isMasterVolumeMuted(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label9234;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 131: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          notifyLockTaskModeChanged(bool, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 132: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label9346;
          }
        }
        for (bool = true;; bool = false)
        {
          setUninstallBlocked((ComponentName)localObject1, (String)localObject2, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 133: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isUninstallBlocked((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label9415;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 134: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label9477;
          }
        }
        for (bool = true;; bool = false)
        {
          setCrossProfileCallerIdDisabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 135: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = getCrossProfileCallerIdDisabled(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label9539;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 136: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = getCrossProfileCallerIdDisabledForUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 137: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label9640;
          }
        }
        for (bool = true;; bool = false)
        {
          setCrossProfileContactsSearchDisabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 138: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = getCrossProfileContactsSearchDisabled(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label9702;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 139: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = getCrossProfileContactsSearchDisabledForUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 140: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        localObject1 = paramParcel1.readString();
        l1 = paramParcel1.readLong();
        long l2;
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          l2 = paramParcel1.readLong();
          if (paramParcel1.readInt() == 0) {
            break label9825;
          }
        }
        for (paramParcel1 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startManagedQuickContact((String)localObject1, l1, bool, l2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          bool = false;
          break;
        }
      case 141: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label9887;
          }
        }
        for (bool = true;; bool = false)
        {
          setBluetoothContactSharingDisabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 142: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = getBluetoothContactSharingDisabled(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label9949;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 143: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = getBluetoothContactSharingDisabledForUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 144: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10096;
          }
          localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10102;
          }
          localObject3 = (PersistableBundle)PersistableBundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10108;
          }
        }
        for (bool = true;; bool = false)
        {
          setTrustAgentConfiguration((ComponentName)localObject1, (ComponentName)localObject2, (PersistableBundle)localObject3, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label10041;
          localObject3 = null;
          break label10062;
        }
      case 145: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10206;
          }
          localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label10212;
          }
        }
        for (bool = true;; bool = false)
        {
          paramParcel1 = getTrustAgentConfiguration((ComponentName)localObject1, (ComponentName)localObject2, paramInt1, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label10162;
        }
      case 146: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = addCrossProfileWidgetProvider((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label10281;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 147: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = removeCrossProfileWidgetProvider((ComponentName)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label10349;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 148: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getCrossProfileWidgetProviders(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        }
      case 149: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10459;
          }
        }
        for (bool = true;; bool = false)
        {
          setAutoTimeRequired((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 150: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = getAutoTimeRequired();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 151: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10557;
          }
        }
        for (bool = true;; bool = false)
        {
          setForceEphemeralUsers((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 152: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = getForceEphemeralUsers(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label10619;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 153: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isRemovingAdmin((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label10687;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 154: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10758;
          }
        }
        for (paramParcel1 = (Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setUserIcon((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 155: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10829;
          }
        }
        for (paramParcel1 = (SystemUpdatePolicy)SystemUpdatePolicy.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setSystemUpdatePolicy((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 156: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getSystemUpdatePolicy();
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
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label10945;
          }
          bool = true;
          bool = setKeyguardDisabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label10951;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label10911;
        }
      case 158: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label11027;
          }
          bool = true;
          bool = setStatusBarDisabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label11033;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          bool = false;
          break label10993;
        }
      case 159: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = getDoNotAskCredentialsOnBoot();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 160: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        notifyPendingSystemUpdate(paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 161: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setPermissionPolicy((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 162: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getPermissionPolicy(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 163: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setPermissionGrantState((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label11261;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 164: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramInt1 = getPermissionGrantState((ComponentName)localObject1, paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 165: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isProvisioningAllowed(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 166: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setKeepUninstalledPackages((ComponentName)localObject1, paramParcel1.createStringArrayList());
          paramParcel2.writeNoException();
          return true;
        }
      case 167: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getKeepUninstalledPackages(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeStringList(paramParcel1);
          return true;
        }
      case 168: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isManagedProfile(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label11517;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 169: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isSystemOnlyUser(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label11578;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 170: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getWifiMacAddress(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 171: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          reboot(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 172: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label11739;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setShortSupportMessage((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 173: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getShortSupportMessage(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label11802;
          }
          paramParcel2.writeInt(1);
          TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 174: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label11876;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setLongSupportMessage((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 175: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getLongSupportMessage(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label11939;
          }
          paramParcel2.writeInt(1);
          TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 176: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getShortSupportMessageForUser((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label12012;
          }
          paramParcel2.writeInt(1);
          TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 177: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getLongSupportMessageForUser((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label12085;
          }
          paramParcel2.writeInt(1);
          TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 178: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isSeparateProfileChallengeAllowed(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 179: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setOrganizationColor((ComponentName)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 180: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        setOrganizationColorForUser(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 181: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = getOrganizationColor(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 182: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = getOrganizationColorForUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 183: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label12345;
          }
        }
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setOrganizationName((ComponentName)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 184: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getOrganizationName(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label12408;
          }
          paramParcel2.writeInt(1);
          TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 185: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramParcel1 = getOrganizationNameForUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 186: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        paramInt1 = getUserProvisioningState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 187: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        setUserProvisioningState(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 188: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setAffiliationIds((ComponentName)localObject1, paramParcel1.createStringArrayList());
          paramParcel2.writeNoException();
          return true;
        }
      case 189: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isAffiliatedUser();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 190: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label12647;
          }
        }
        for (bool = true;; bool = false)
        {
          setSecurityLoggingEnabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 191: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isSecurityLoggingEnabled(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label12709;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 192: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = retrieveSecurityLogs(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label12772;
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
      case 193: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = retrievePreRebootSecurityLogs(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label12838;
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
      case 194: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isUninstallInQueue(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 195: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        uninstallPackageWithActiveAdmins(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 196: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isDeviceProvisioned();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 197: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        bool = isDeviceProvisioningConfigApplied();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 198: 
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        setDeviceProvisioningConfigApplied();
        paramParcel2.writeNoException();
        return true;
      case 199: 
        label6474:
        label6516:
        label6522:
        label6590:
        label6794:
        label6830:
        label6836:
        label6937:
        label6978:
        label6984:
        label7112:
        label7182:
        label7253:
        label7407:
        label7521:
        label7585:
        label7665:
        label7783:
        label7929:
        label8004:
        label8146:
        label8194:
        label8230:
        label8236:
        label8304:
        label8363:
        label8384:
        label8428:
        label8434:
        label8440:
        label8495:
        label8528:
        label8533:
        label8585:
        label8618:
        label8623:
        label8749:
        label8819:
        label9172:
        label9234:
        label9346:
        label9415:
        label9477:
        label9539:
        label9640:
        label9702:
        label9825:
        label9887:
        label9949:
        label10041:
        label10062:
        label10096:
        label10102:
        label10108:
        label10162:
        label10206:
        label10212:
        label10281:
        label10349:
        label10459:
        label10557:
        label10619:
        label10687:
        label10758:
        label10829:
        label10911:
        label10945:
        label10951:
        label10993:
        label11027:
        label11033:
        label11261:
        label11517:
        label11578:
        label11739:
        label11802:
        label11876:
        label11939:
        label12012:
        label12085:
        label12345:
        label12408:
        label12647:
        label12709:
        label12772:
        label12838:
        paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label13048;
          }
        }
        label13048:
        for (bool = true;; bool = false)
        {
          setBackupServiceEnabled((ComponentName)localObject1, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.app.admin.IDevicePolicyManager");
      if (paramParcel1.readInt() != 0)
      {
        paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
        bool = isBackupServiceEnabled(paramParcel1);
        paramParcel2.writeNoException();
        if (!bool) {
          break label13110;
        }
      }
      label13110:
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        paramParcel1 = null;
        break;
      }
    }
    
    private static class Proxy
      implements IDevicePolicyManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addCrossProfileIntentFilter(ComponentName paramComponentName, IntentFilter paramIntentFilter, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramIntentFilter != null)
              {
                localParcel1.writeInt(1);
                paramIntentFilter.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(104, localParcel1, localParcel2, 0);
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
      
      public boolean addCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(146, localParcel1, localParcel2, 0);
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
      
      public void addPersistentPreferredActivity(ComponentName paramComponentName1, IntentFilter paramIntentFilter, ComponentName paramComponentName2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName1 != null)
            {
              localParcel1.writeInt(1);
              paramComponentName1.writeToParcel(localParcel1, 0);
              if (paramIntentFilter != null)
              {
                localParcel1.writeInt(1);
                paramIntentFilter.writeToParcel(localParcel1, 0);
                if (paramComponentName2 == null) {
                  break label132;
                }
                localParcel1.writeInt(1);
                paramComponentName2.writeToParcel(localParcel1, 0);
                this.mRemote.transact(93, localParcel1, localParcel2, 0);
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
          label132:
          localParcel1.writeInt(0);
        }
      }
      
      /* Error */
      public boolean approveCaCert(String paramString, int paramInt, boolean paramBoolean)
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
        //   23: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: aload 5
        //   28: iload_2
        //   29: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   32: iload 4
        //   34: istore_2
        //   35: iload_3
        //   36: ifeq +5 -> 41
        //   39: iconst_1
        //   40: istore_2
        //   41: aload 5
        //   43: iload_2
        //   44: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   47: aload_0
        //   48: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   51: bipush 84
        //   53: aload 5
        //   55: aload 6
        //   57: iconst_0
        //   58: invokeinterface 55 5 0
        //   63: pop
        //   64: aload 6
        //   66: invokevirtual 58	android/os/Parcel:readException	()V
        //   69: aload 6
        //   71: invokevirtual 71	android/os/Parcel:readInt	()I
        //   74: istore_2
        //   75: iload_2
        //   76: ifeq +17 -> 93
        //   79: iconst_1
        //   80: istore_3
        //   81: aload 6
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 5
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
        //   91: iload_3
        //   92: ireturn
        //   93: iconst_0
        //   94: istore_3
        //   95: goto -14 -> 81
        //   98: astore_1
        //   99: aload 6
        //   101: invokevirtual 61	android/os/Parcel:recycle	()V
        //   104: aload 5
        //   106: invokevirtual 61	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramString	String
        //   0	111	2	paramInt	int
        //   0	111	3	paramBoolean	boolean
        //   1	32	4	i	int
        //   6	99	5	localParcel1	Parcel
        //   11	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	98	finally
        //   41	75	98	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public void choosePrivateKeyAlias(int paramInt, Uri paramUri, String paramString, IBinder paramIBinder)
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
        //   19: iload_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_2
        //   24: ifnull +62 -> 86
        //   27: aload 5
        //   29: iconst_1
        //   30: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 5
        //   36: iconst_0
        //   37: invokevirtual 82	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 5
        //   42: aload_3
        //   43: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload 5
        //   48: aload 4
        //   50: invokevirtual 85	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   53: aload_0
        //   54: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 88
        //   59: aload 5
        //   61: aload 6
        //   63: iconst_0
        //   64: invokeinterface 55 5 0
        //   69: pop
        //   70: aload 6
        //   72: invokevirtual 58	android/os/Parcel:readException	()V
        //   75: aload 6
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 5
        //   88: iconst_0
        //   89: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   92: goto -52 -> 40
        //   95: astore_2
        //   96: aload 6
        //   98: invokevirtual 61	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 61	android/os/Parcel:recycle	()V
        //   106: aload_2
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramInt	int
        //   0	108	2	paramUri	Uri
        //   0	108	3	paramString	String
        //   0	108	4	paramIBinder	IBinder
        //   3	99	5	localParcel1	Parcel
        //   8	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	95	finally
        //   27	40	95	finally
        //   40	75	95	finally
        //   86	92	95	finally
      }
      
      /* Error */
      public void clearCrossProfileIntentFilters(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 105
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramComponentName	ComponentName
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void clearDeviceOwner(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
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
      
      /* Error */
      public void clearPackagePersistentPreferredActivities(ComponentName paramComponentName, String paramString)
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
        //   31: aload_2
        //   32: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 94
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramComponentName	ComponentName
        //   0	86	2	paramString	String
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
      public void clearProfileOwner(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 75
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramComponentName	ComponentName
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public UserHandle createAndManageUser(ComponentName paramComponentName1, String paramString, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName1 != null)
            {
              localParcel1.writeInt(1);
              paramComponentName1.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName2 != null)
              {
                localParcel1.writeInt(1);
                paramComponentName2.writeToParcel(localParcel1, 0);
                if (paramPersistableBundle == null) {
                  break label170;
                }
                localParcel1.writeInt(1);
                paramPersistableBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(116, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label179;
                }
                paramComponentName1 = (UserHandle)UserHandle.CREATOR.createFromParcel(localParcel2);
                return paramComponentName1;
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
          label170:
          localParcel1.writeInt(0);
          continue;
          label179:
          paramComponentName1 = null;
        }
      }
      
      /* Error */
      public void enableSystemApp(ComponentName paramComponentName, String paramString)
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
        //   31: aload_2
        //   32: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 119
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramComponentName	ComponentName
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public int enableSystemAppWithIntent(ComponentName paramComponentName, Intent paramIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramIntent != null)
              {
                localParcel1.writeInt(1);
                paramIntent.writeToParcel(localParcel1, 0);
                this.mRemote.transact(120, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                return i;
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
      
      /* Error */
      public void enforceCanManageCaCerts(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 83
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramComponentName	ComponentName
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void forceRemoveActiveAdmin(ComponentName paramComponentName, int paramInt)
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
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 55
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public String[] getAccountTypesWithManagementDisabled()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          this.mRemote.transact(122, localParcel1, localParcel2, 0);
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
      
      public String[] getAccountTypesWithManagementDisabledAsUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(123, localParcel1, localParcel2, 0);
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
      
      public List<ComponentName> getActiveAdmins(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(51, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(ComponentName.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public String getAlwaysOnVpnPackage(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 92
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 139	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramComponentName	ComponentName
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      public Bundle getApplicationRestrictions(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(96, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
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
      public String getApplicationRestrictionsManagingPackage(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 98
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 139	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramComponentName	ComponentName
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      /* Error */
      public boolean getAutoTimeRequired()
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
        //   16: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 150
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 55 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 58	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 71	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 61	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean getBluetoothContactSharingDisabled(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(142, localParcel1, localParcel2, 0);
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
      public boolean getBluetoothContactSharingDisabledForUser(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: sipush 143
        //   27: aload_3
        //   28: aload 4
        //   30: iconst_0
        //   31: invokeinterface 55 5 0
        //   36: pop
        //   37: aload 4
        //   39: invokevirtual 58	android/os/Parcel:readException	()V
        //   42: aload 4
        //   44: invokevirtual 71	android/os/Parcel:readInt	()I
        //   47: istore_1
        //   48: iload_1
        //   49: ifeq +16 -> 65
        //   52: iconst_1
        //   53: istore_2
        //   54: aload 4
        //   56: invokevirtual 61	android/os/Parcel:recycle	()V
        //   59: aload_3
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: iload_2
        //   64: ireturn
        //   65: iconst_0
        //   66: istore_2
        //   67: goto -13 -> 54
        //   70: astore 5
        //   72: aload 4
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_3
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramInt	int
        //   53	14	2	bool	boolean
        //   3	75	3	localParcel1	Parcel
        //   7	66	4	localParcel2	Parcel
        //   70	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	48	70	finally
      }
      
      public boolean getCameraDisabled(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(44, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      public String getCertInstallerPackage(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 90
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 139	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramComponentName	ComponentName
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      public boolean getCrossProfileCallerIdDisabled(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(135, localParcel1, localParcel2, 0);
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
      public boolean getCrossProfileCallerIdDisabledForUser(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: sipush 136
        //   27: aload_3
        //   28: aload 4
        //   30: iconst_0
        //   31: invokeinterface 55 5 0
        //   36: pop
        //   37: aload 4
        //   39: invokevirtual 58	android/os/Parcel:readException	()V
        //   42: aload 4
        //   44: invokevirtual 71	android/os/Parcel:readInt	()I
        //   47: istore_1
        //   48: iload_1
        //   49: ifeq +16 -> 65
        //   52: iconst_1
        //   53: istore_2
        //   54: aload 4
        //   56: invokevirtual 61	android/os/Parcel:recycle	()V
        //   59: aload_3
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: iload_2
        //   64: ireturn
        //   65: iconst_0
        //   66: istore_2
        //   67: goto -13 -> 54
        //   70: astore 5
        //   72: aload 4
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_3
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramInt	int
        //   53	14	2	bool	boolean
        //   3	75	3	localParcel1	Parcel
        //   7	66	4	localParcel2	Parcel
        //   70	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	48	70	finally
      }
      
      public boolean getCrossProfileContactsSearchDisabled(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(138, localParcel1, localParcel2, 0);
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
      public boolean getCrossProfileContactsSearchDisabledForUser(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: sipush 139
        //   27: aload_3
        //   28: aload 4
        //   30: iconst_0
        //   31: invokeinterface 55 5 0
        //   36: pop
        //   37: aload 4
        //   39: invokevirtual 58	android/os/Parcel:readException	()V
        //   42: aload 4
        //   44: invokevirtual 71	android/os/Parcel:readInt	()I
        //   47: istore_1
        //   48: iload_1
        //   49: ifeq +16 -> 65
        //   52: iconst_1
        //   53: istore_2
        //   54: aload 4
        //   56: invokevirtual 61	android/os/Parcel:recycle	()V
        //   59: aload_3
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: iload_2
        //   64: ireturn
        //   65: iconst_0
        //   66: istore_2
        //   67: goto -13 -> 54
        //   70: astore 5
        //   72: aload 4
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_3
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramInt	int
        //   53	14	2	bool	boolean
        //   3	75	3	localParcel1	Parcel
        //   7	66	4	localParcel2	Parcel
        //   70	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	48	70	finally
      }
      
      /* Error */
      public List<String> getCrossProfileWidgetProviders(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +49 -> 64
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 148
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 164	android/os/Parcel:createStringArrayList	()Ljava/util/ArrayList;
        //   53: astore_1
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: aload_2
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: areturn
        //   64: aload_2
        //   65: iconst_0
        //   66: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   69: goto -40 -> 29
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramComponentName	ComponentName
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	72	finally
        //   18	29	72	finally
        //   29	54	72	finally
        //   64	69	72	finally
      }
      
      public int getCurrentFailedPasswordAttempts(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
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
      public ComponentName getDeviceOwnerComponent(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: iload_1
        //   20: ifeq +5 -> 25
        //   23: iconst_1
        //   24: istore_2
        //   25: aload 4
        //   27: iload_2
        //   28: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   31: aload_0
        //   32: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   35: bipush 66
        //   37: aload 4
        //   39: aload 5
        //   41: iconst_0
        //   42: invokeinterface 55 5 0
        //   47: pop
        //   48: aload 5
        //   50: invokevirtual 58	android/os/Parcel:readException	()V
        //   53: aload 5
        //   55: invokevirtual 71	android/os/Parcel:readInt	()I
        //   58: ifeq +29 -> 87
        //   61: getstatic 127	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   64: aload 5
        //   66: invokeinterface 108 2 0
        //   71: checkcast 42	android/content/ComponentName
        //   74: astore_3
        //   75: aload 5
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: areturn
        //   87: aconst_null
        //   88: astore_3
        //   89: goto -14 -> 75
        //   92: astore_3
        //   93: aload 5
        //   95: invokevirtual 61	android/os/Parcel:recycle	()V
        //   98: aload 4
        //   100: invokevirtual 61	android/os/Parcel:recycle	()V
        //   103: aload_3
        //   104: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	105	0	this	Proxy
        //   0	105	1	paramBoolean	boolean
        //   1	27	2	i	int
        //   74	15	3	localComponentName	ComponentName
        //   92	12	3	localObject	Object
        //   5	94	4	localParcel1	Parcel
        //   10	84	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	92	finally
        //   25	75	92	finally
      }
      
      /* Error */
      public CharSequence getDeviceOwnerLockScreenInfo()
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
        //   15: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 78
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 55 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 58	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 176	android/text/TextUtils:CHAR_SEQUENCE_CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 108 2 0
        //   49: checkcast 178	java/lang/CharSequence
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localCharSequence	CharSequence
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getDeviceOwnerName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          this.mRemote.transact(67, localParcel1, localParcel2, 0);
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
      
      public int getDeviceOwnerUserId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          this.mRemote.transact(69, localParcel1, localParcel2, 0);
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
      public boolean getDoNotAskCredentialsOnBoot()
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
        //   16: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 159
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 55 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 58	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 71	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 61	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean getForceEphemeralUsers(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(152, localParcel1, localParcel2, 0);
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
      public ComponentName getGlobalProxyAdmin(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 37
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 55 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 58	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 127	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 108 2 0
        //   59: checkcast 42	android/content/ComponentName
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 61	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localComponentName	ComponentName
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.admin.IDevicePolicyManager";
      }
      
      /* Error */
      public List<String> getKeepUninstalledPackages(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +49 -> 64
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 167
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 164	android/os/Parcel:createStringArrayList	()Ljava/util/ArrayList;
        //   53: astore_1
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: aload_2
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: areturn
        //   64: aload_2
        //   65: iconst_0
        //   66: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   69: goto -40 -> 29
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramComponentName	ComponentName
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	72	finally
        //   18	29	72	finally
        //   29	54	72	finally
        //   64	69	72	finally
      }
      
      public int getKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(48, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public String[] getLockTaskPackages(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 125
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 122	android/os/Parcel:createStringArray	()[Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramComponentName	ComponentName
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      public CharSequence getLongSupportMessage(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(175, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(localParcel2);
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
      
      public CharSequence getLongSupportMessageForUser(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(177, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(localParcel2);
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
      
      public int getMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(27, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public long getMaximumTimeToLock(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(30, localParcel1, localParcel2, 0);
                localParcel2.readException();
                long l = localParcel2.readLong();
                return l;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public long getMaximumTimeToLockForUserAndProfiles(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public int getOrganizationColor(ComponentName paramComponentName)
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
        //   16: ifnull +53 -> 69
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: sipush 181
        //   37: aload_3
        //   38: aload 4
        //   40: iconst_0
        //   41: invokeinterface 55 5 0
        //   46: pop
        //   47: aload 4
        //   49: invokevirtual 58	android/os/Parcel:readException	()V
        //   52: aload 4
        //   54: invokevirtual 71	android/os/Parcel:readInt	()I
        //   57: istore_2
        //   58: aload 4
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: aload_3
        //   64: invokevirtual 61	android/os/Parcel:recycle	()V
        //   67: iload_2
        //   68: ireturn
        //   69: aload_3
        //   70: iconst_0
        //   71: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   74: goto -44 -> 30
        //   77: astore_1
        //   78: aload 4
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: invokevirtual 61	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	89	0	this	Proxy
        //   0	89	1	paramComponentName	ComponentName
        //   57	11	2	i	int
        //   3	81	3	localParcel1	Parcel
        //   7	72	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	77	finally
        //   19	30	77	finally
        //   30	58	77	finally
        //   69	74	77	finally
      }
      
      public int getOrganizationColorForUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(182, localParcel1, localParcel2, 0);
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
      
      public CharSequence getOrganizationName(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(184, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(localParcel2);
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
      public CharSequence getOrganizationNameForUser(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: sipush 185
        //   27: aload_3
        //   28: aload 4
        //   30: iconst_0
        //   31: invokeinterface 55 5 0
        //   36: pop
        //   37: aload 4
        //   39: invokevirtual 58	android/os/Parcel:readException	()V
        //   42: aload 4
        //   44: invokevirtual 71	android/os/Parcel:readInt	()I
        //   47: ifeq +28 -> 75
        //   50: getstatic 176	android/text/TextUtils:CHAR_SEQUENCE_CREATOR	Landroid/os/Parcelable$Creator;
        //   53: aload 4
        //   55: invokeinterface 108 2 0
        //   60: checkcast 178	java/lang/CharSequence
        //   63: astore_2
        //   64: aload 4
        //   66: invokevirtual 61	android/os/Parcel:recycle	()V
        //   69: aload_3
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: areturn
        //   75: aconst_null
        //   76: astore_2
        //   77: goto -13 -> 64
        //   80: astore_2
        //   81: aload 4
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload_3
        //   87: invokevirtual 61	android/os/Parcel:recycle	()V
        //   90: aload_2
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramInt	int
        //   63	14	2	localCharSequence	CharSequence
        //   80	11	2	localObject	Object
        //   3	84	3	localParcel1	Parcel
        //   7	75	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	64	80	finally
      }
      
      public long getPasswordExpiration(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(21, localParcel1, localParcel2, 0);
                localParcel2.readException();
                long l = localParcel2.readLong();
                return l;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public long getPasswordExpirationTimeout(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(20, localParcel1, localParcel2, 0);
                localParcel2.readException();
                long l = localParcel2.readLong();
                return l;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordHistoryLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(18, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordMinimumLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordMinimumLetters(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(10, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(8, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(16, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(12, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(14, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(6, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getPasswordQuality(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(2, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public int getPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2)
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
        //   18: ifnull +71 -> 89
        //   21: aload 5
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 5
        //   30: iconst_0
        //   31: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 5
        //   36: aload_2
        //   37: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 5
        //   42: aload_3
        //   43: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: sipush 164
        //   53: aload 5
        //   55: aload 6
        //   57: iconst_0
        //   58: invokeinterface 55 5 0
        //   63: pop
        //   64: aload 6
        //   66: invokevirtual 58	android/os/Parcel:readException	()V
        //   69: aload 6
        //   71: invokevirtual 71	android/os/Parcel:readInt	()I
        //   74: istore 4
        //   76: aload 6
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: iload 4
        //   88: ireturn
        //   89: aload 5
        //   91: iconst_0
        //   92: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   95: goto -61 -> 34
        //   98: astore_1
        //   99: aload 6
        //   101: invokevirtual 61	android/os/Parcel:recycle	()V
        //   104: aload 5
        //   106: invokevirtual 61	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramComponentName	ComponentName
        //   0	111	2	paramString1	String
        //   0	111	3	paramString2	String
        //   74	13	4	i	int
        //   3	102	5	localParcel1	Parcel
        //   8	92	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	98	finally
        //   21	34	98	finally
        //   34	76	98	finally
        //   89	95	98	finally
      }
      
      /* Error */
      public int getPermissionPolicy(ComponentName paramComponentName)
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
        //   16: ifnull +53 -> 69
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: sipush 162
        //   37: aload_3
        //   38: aload 4
        //   40: iconst_0
        //   41: invokeinterface 55 5 0
        //   46: pop
        //   47: aload 4
        //   49: invokevirtual 58	android/os/Parcel:readException	()V
        //   52: aload 4
        //   54: invokevirtual 71	android/os/Parcel:readInt	()I
        //   57: istore_2
        //   58: aload 4
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: aload_3
        //   64: invokevirtual 61	android/os/Parcel:recycle	()V
        //   67: iload_2
        //   68: ireturn
        //   69: aload_3
        //   70: iconst_0
        //   71: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   74: goto -44 -> 30
        //   77: astore_1
        //   78: aload 4
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: aload_3
        //   84: invokevirtual 61	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	89	0	this	Proxy
        //   0	89	1	paramComponentName	ComponentName
        //   57	11	2	i	int
        //   3	81	3	localParcel1	Parcel
        //   7	72	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	77	finally
        //   19	30	77	finally
        //   30	58	77	finally
        //   69	74	77	finally
      }
      
      /* Error */
      public List getPermittedAccessibilityServices(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +55 -> 70
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 107
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: aload_0
        //   50: invokevirtual 229	android/app/admin/IDevicePolicyManager$Stub$Proxy:getClass	()Ljava/lang/Class;
        //   53: invokevirtual 235	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
        //   56: invokevirtual 239	android/os/Parcel:readArrayList	(Ljava/lang/ClassLoader;)Ljava/util/ArrayList;
        //   59: astore_1
        //   60: aload_3
        //   61: invokevirtual 61	android/os/Parcel:recycle	()V
        //   64: aload_2
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_1
        //   69: areturn
        //   70: aload_2
        //   71: iconst_0
        //   72: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   75: goto -46 -> 29
        //   78: astore_1
        //   79: aload_3
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: aload_2
        //   84: invokevirtual 61	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	89	0	this	Proxy
        //   0	89	1	paramComponentName	ComponentName
        //   3	81	2	localParcel1	Parcel
        //   7	73	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	78	finally
        //   18	29	78	finally
        //   29	60	78	finally
        //   70	75	78	finally
      }
      
      public List getPermittedAccessibilityServicesForUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(108, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.readArrayList(getClass().getClassLoader());
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public List getPermittedInputMethods(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +55 -> 70
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 111
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: aload_0
        //   50: invokevirtual 229	android/app/admin/IDevicePolicyManager$Stub$Proxy:getClass	()Ljava/lang/Class;
        //   53: invokevirtual 235	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
        //   56: invokevirtual 239	android/os/Parcel:readArrayList	(Ljava/lang/ClassLoader;)Ljava/util/ArrayList;
        //   59: astore_1
        //   60: aload_3
        //   61: invokevirtual 61	android/os/Parcel:recycle	()V
        //   64: aload_2
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_1
        //   69: areturn
        //   70: aload_2
        //   71: iconst_0
        //   72: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   75: goto -46 -> 29
        //   78: astore_1
        //   79: aload_3
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: aload_2
        //   84: invokevirtual 61	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	89	0	this	Proxy
        //   0	89	1	paramComponentName	ComponentName
        //   3	81	2	localParcel1	Parcel
        //   7	73	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	78	finally
        //   18	29	78	finally
        //   29	60	78	finally
        //   70	75	78	finally
      }
      
      public List getPermittedInputMethodsForCurrentUser()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          this.mRemote.transact(112, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.readArrayList(getClass().getClassLoader());
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ComponentName getProfileOwner(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 71
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 55 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 58	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 127	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 108 2 0
        //   59: checkcast 42	android/content/ComponentName
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 61	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localComponentName	ComponentName
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public String getProfileOwnerName(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(72, localParcel1, localParcel2, 0);
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
      
      public int getProfileWithMinimumFailedPasswordsForWipe(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
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
      
      public void getRemoveWarning(ComponentName paramComponentName, RemoteCallback paramRemoteCallback, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramRemoteCallback != null)
              {
                localParcel1.writeInt(1);
                paramRemoteCallback.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(53, localParcel1, localParcel2, 0);
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
      
      public long getRequiredStrongAuthTimeout(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(33, localParcel1, localParcel2, 0);
                localParcel2.readException();
                long l = localParcel2.readLong();
                return l;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public ComponentName getRestrictionsProvider(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 101
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 55 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 58	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 127	android/content/ComponentName:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 108 2 0
        //   59: checkcast 42	android/content/ComponentName
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 61	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localComponentName	ComponentName
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public boolean getScreenCaptureDisabled(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(46, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      
      public CharSequence getShortSupportMessage(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(173, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(localParcel2);
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
      
      public CharSequence getShortSupportMessageForUser(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(176, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(localParcel2);
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
      
      public boolean getStorageEncryption(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(40, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      
      public int getStorageEncryptionStatus(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(41, localParcel1, localParcel2, 0);
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
      public SystemUpdatePolicy getSystemUpdatePolicy()
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
        //   15: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: sipush 156
        //   21: aload_2
        //   22: aload_3
        //   23: iconst_0
        //   24: invokeinterface 55 5 0
        //   29: pop
        //   30: aload_3
        //   31: invokevirtual 58	android/os/Parcel:readException	()V
        //   34: aload_3
        //   35: invokevirtual 71	android/os/Parcel:readInt	()I
        //   38: ifeq +26 -> 64
        //   41: getstatic 265	android/app/admin/SystemUpdatePolicy:CREATOR	Landroid/os/Parcelable$Creator;
        //   44: aload_3
        //   45: invokeinterface 108 2 0
        //   50: checkcast 264	android/app/admin/SystemUpdatePolicy
        //   53: astore_1
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: aload_2
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: areturn
        //   64: aconst_null
        //   65: astore_1
        //   66: goto -12 -> 54
        //   69: astore_1
        //   70: aload_3
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_2
        //   75: invokevirtual 61	android/os/Parcel:recycle	()V
        //   78: aload_1
        //   79: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	80	0	this	Proxy
        //   53	13	1	localSystemUpdatePolicy	SystemUpdatePolicy
        //   69	10	1	localObject	Object
        //   3	72	2	localParcel1	Parcel
        //   7	64	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	54	69	finally
      }
      
      public List<PersistableBundle> getTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName1 != null)
            {
              localParcel1.writeInt(1);
              paramComponentName1.writeToParcel(localParcel1, 0);
              if (paramComponentName2 != null)
              {
                localParcel1.writeInt(1);
                paramComponentName2.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                if (!paramBoolean) {
                  break label149;
                }
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(145, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramComponentName1 = localParcel2.createTypedArrayList(PersistableBundle.CREATOR);
                return paramComponentName1;
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
          label149:
          paramInt = 0;
        }
      }
      
      public int getUserProvisioningState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          this.mRemote.transact(186, localParcel1, localParcel2, 0);
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
      
      public Bundle getUserRestrictions(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(103, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
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
      public String getWifiMacAddress(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +49 -> 64
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 170
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 139	android/os/Parcel:readString	()Ljava/lang/String;
        //   53: astore_1
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: aload_2
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: areturn
        //   64: aload_2
        //   65: iconst_0
        //   66: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   69: goto -40 -> 29
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramComponentName	ComponentName
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	72	finally
        //   18	29	72	finally
        //   29	54	72	finally
        //   64	69	72	finally
      }
      
      public boolean hasGrantedPolicy(ComponentName paramComponentName, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              localParcel1.writeInt(paramInt2);
              this.mRemote.transact(56, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public boolean hasUserSetupCompleted()
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
        //   16: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 76
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean installCaCert(ComponentName paramComponentName, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeByteArray(paramArrayOfByte);
              this.mRemote.transact(81, localParcel1, localParcel2, 0);
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
      
      public boolean installKeyPair(ComponentName paramComponentName, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeByteArray(paramArrayOfByte1);
              localParcel1.writeByteArray(paramArrayOfByte2);
              localParcel1.writeByteArray(paramArrayOfByte3);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(86, localParcel1, localParcel2, 0);
                localParcel2.readException();
                i = localParcel2.readInt();
                if (i == 0) {
                  break label153;
                }
                paramBoolean = true;
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label153:
          paramBoolean = false;
        }
      }
      
      public boolean isAccessibilityServicePermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(109, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      public boolean isActivePasswordSufficient(int paramInt, boolean paramBoolean)
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
        //   21: iload_1
        //   22: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   25: iload_3
        //   26: istore_1
        //   27: iload_2
        //   28: ifeq +5 -> 33
        //   31: iconst_1
        //   32: istore_1
        //   33: aload 4
        //   35: iload_1
        //   36: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   39: aload_0
        //   40: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: bipush 22
        //   45: aload 4
        //   47: aload 5
        //   49: iconst_0
        //   50: invokeinterface 55 5 0
        //   55: pop
        //   56: aload 5
        //   58: invokevirtual 58	android/os/Parcel:readException	()V
        //   61: aload 5
        //   63: invokevirtual 71	android/os/Parcel:readInt	()I
        //   66: istore_1
        //   67: iload_1
        //   68: ifeq +17 -> 85
        //   71: iconst_1
        //   72: istore_2
        //   73: aload 5
        //   75: invokevirtual 61	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 61	android/os/Parcel:recycle	()V
        //   83: iload_2
        //   84: ireturn
        //   85: iconst_0
        //   86: istore_2
        //   87: goto -14 -> 73
        //   90: astore 6
        //   92: aload 5
        //   94: invokevirtual 61	android/os/Parcel:recycle	()V
        //   97: aload 4
        //   99: invokevirtual 61	android/os/Parcel:recycle	()V
        //   102: aload 6
        //   104: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	105	0	this	Proxy
        //   0	105	1	paramInt	int
        //   0	105	2	paramBoolean	boolean
        //   1	25	3	i	int
        //   5	93	4	localParcel1	Parcel
        //   10	83	5	localParcel2	Parcel
        //   90	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   12	25	90	finally
        //   33	67	90	finally
      }
      
      public boolean isAdminActive(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(50, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      public boolean isAffiliatedUser()
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
        //   16: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 189
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 55 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 58	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 71	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 61	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean isApplicationHidden(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(115, localParcel1, localParcel2, 0);
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
      
      public boolean isBackupServiceEnabled(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(200, localParcel1, localParcel2, 0);
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
      public boolean isCaCertApproved(String paramString, int paramInt)
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
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 85
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 55 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 58	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 71	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
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
      public boolean isCallerApplicationRestrictionsManagingPackage()
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
        //   16: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 99
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
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
      public boolean isDeviceProvisioned()
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
        //   16: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 196
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 55 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 58	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 71	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 61	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
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
      public boolean isDeviceProvisioningConfigApplied()
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
        //   16: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: sipush 197
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 55 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 58	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 71	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +16 -> 60
        //   47: iconst_1
        //   48: istore_2
        //   49: aload 4
        //   51: invokevirtual 61	android/os/Parcel:recycle	()V
        //   54: aload_3
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: iload_2
        //   59: ireturn
        //   60: iconst_0
        //   61: istore_2
        //   62: goto -13 -> 49
        //   65: astore 5
        //   67: aload 4
        //   69: invokevirtual 61	android/os/Parcel:recycle	()V
        //   72: aload_3
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean isInputMethodPermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(113, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      public boolean isLockTaskPermitted(String paramString)
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
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 126
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 58	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 71	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 61	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean isManagedProfile(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(168, localParcel1, localParcel2, 0);
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
      
      public boolean isMasterVolumeMuted(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(130, localParcel1, localParcel2, 0);
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
      
      public boolean isPackageSuspended(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(80, localParcel1, localParcel2, 0);
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
      public boolean isProfileActivePasswordSufficientForParent(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 23
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 55 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 58	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 61	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 61	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
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
      public boolean isProvisioningAllowed(String paramString)
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
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: sipush 165
        //   30: aload 4
        //   32: aload 5
        //   34: iconst_0
        //   35: invokeinterface 55 5 0
        //   40: pop
        //   41: aload 5
        //   43: invokevirtual 58	android/os/Parcel:readException	()V
        //   46: aload 5
        //   48: invokevirtual 71	android/os/Parcel:readInt	()I
        //   51: istore_2
        //   52: iload_2
        //   53: ifeq +17 -> 70
        //   56: iconst_1
        //   57: istore_3
        //   58: aload 5
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: aload 4
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: iload_3
        //   69: ireturn
        //   70: iconst_0
        //   71: istore_3
        //   72: goto -14 -> 58
        //   75: astore_1
        //   76: aload 5
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean isRemovingAdmin(ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(153, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      
      public boolean isSecurityLoggingEnabled(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(191, localParcel1, localParcel2, 0);
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
      public boolean isSeparateProfileChallengeAllowed(int paramInt)
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
        //   21: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: sipush 178
        //   27: aload_3
        //   28: aload 4
        //   30: iconst_0
        //   31: invokeinterface 55 5 0
        //   36: pop
        //   37: aload 4
        //   39: invokevirtual 58	android/os/Parcel:readException	()V
        //   42: aload 4
        //   44: invokevirtual 71	android/os/Parcel:readInt	()I
        //   47: istore_1
        //   48: iload_1
        //   49: ifeq +16 -> 65
        //   52: iconst_1
        //   53: istore_2
        //   54: aload 4
        //   56: invokevirtual 61	android/os/Parcel:recycle	()V
        //   59: aload_3
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: iload_2
        //   64: ireturn
        //   65: iconst_0
        //   66: istore_2
        //   67: goto -13 -> 54
        //   70: astore 5
        //   72: aload 4
        //   74: invokevirtual 61	android/os/Parcel:recycle	()V
        //   77: aload_3
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload 5
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramInt	int
        //   53	14	2	bool	boolean
        //   3	75	3	localParcel1	Parcel
        //   7	66	4	localParcel2	Parcel
        //   70	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	48	70	finally
      }
      
      public boolean isSystemOnlyUser(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(169, localParcel1, localParcel2, 0);
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
      
      public boolean isUninstallBlocked(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(133, localParcel1, localParcel2, 0);
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
      public boolean isUninstallInQueue(String paramString)
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
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: sipush 194
        //   30: aload 4
        //   32: aload 5
        //   34: iconst_0
        //   35: invokeinterface 55 5 0
        //   40: pop
        //   41: aload 5
        //   43: invokevirtual 58	android/os/Parcel:readException	()V
        //   46: aload 5
        //   48: invokevirtual 71	android/os/Parcel:readInt	()I
        //   51: istore_2
        //   52: iload_2
        //   53: ifeq +17 -> 70
        //   56: iconst_1
        //   57: istore_3
        //   58: aload 5
        //   60: invokevirtual 61	android/os/Parcel:recycle	()V
        //   63: aload 4
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: iload_3
        //   69: ireturn
        //   70: iconst_0
        //   71: istore_3
        //   72: goto -14 -> 58
        //   75: astore_1
        //   76: aload 5
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public void lockNow(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void notifyLockTaskModeChanged(boolean paramBoolean, String paramString, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(131, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void notifyPendingSystemUpdate(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(160, localParcel1, localParcel2, 0);
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
      public boolean packageHasActiveAdmins(String paramString, int paramInt)
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
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 52
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 55 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 58	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 71	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
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
      public void reboot(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +43 -> 58
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: sipush 171
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 61	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 61	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aload_2
        //   59: iconst_0
        //   60: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   63: goto -34 -> 29
        //   66: astore_1
        //   67: aload_3
        //   68: invokevirtual 61	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: invokevirtual 61	android/os/Parcel:recycle	()V
        //   75: aload_1
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   0	77	1	paramComponentName	ComponentName
        //   3	69	2	localParcel1	Parcel
        //   7	61	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	66	finally
        //   18	29	66	finally
        //   29	49	66	finally
        //   58	63	66	finally
      }
      
      /* Error */
      public void removeActiveAdmin(ComponentName paramComponentName, int paramInt)
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
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 54
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public boolean removeCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(147, localParcel1, localParcel2, 0);
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
      
      public boolean removeKeyPair(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(87, localParcel1, localParcel2, 0);
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
      
      public boolean removeUser(ComponentName paramComponentName, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(117, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label130;
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
          label130:
          boolean bool = false;
        }
      }
      
      public void reportFailedFingerprintAttempt(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
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
      
      public void reportFailedPasswordAttempt(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
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
      
      public void reportKeyguardDismissed(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
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
      
      public void reportKeyguardSecured(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(64, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void reportPasswordChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
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
      
      public void reportSuccessfulFingerprintAttempt(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(62, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void reportSuccessfulPasswordAttempt(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
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
      
      public boolean requestBugreport(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(42, localParcel1, localParcel2, 0);
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
      public boolean resetPassword(String paramString, int paramInt)
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
        //   20: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 28
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 55 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 58	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 71	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 61	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 61	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 61	android/os/Parcel:recycle	()V
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
      
      public ParceledListSlice retrievePreRebootSecurityLogs(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(193, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
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
      
      public ParceledListSlice retrieveSecurityLogs(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(192, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(localParcel2);
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
      
      public void setAccountManagementDisabled(ComponentName paramComponentName, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(121, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setActiveAdmin(ComponentName paramComponentName, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label114;
              localParcel1.writeInt(i);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(49, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label114:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setActivePasswordState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeInt(paramInt4);
          localParcel1.writeInt(paramInt5);
          localParcel1.writeInt(paramInt6);
          localParcel1.writeInt(paramInt7);
          localParcel1.writeInt(paramInt8);
          localParcel1.writeInt(paramInt9);
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
      public void setAffiliationIds(ComponentName paramComponentName, List<String> paramList)
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
        //   16: ifnull +51 -> 67
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 360	android/os/Parcel:writeStringList	(Ljava/util/List;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: sipush 188
        //   42: aload_3
        //   43: aload 4
        //   45: iconst_0
        //   46: invokeinterface 55 5 0
        //   51: pop
        //   52: aload 4
        //   54: invokevirtual 58	android/os/Parcel:readException	()V
        //   57: aload 4
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: return
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramComponentName	ComponentName
        //   0	87	2	paramList	List<String>
        //   3	79	3	localParcel1	Parcel
        //   7	70	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	75	finally
        //   19	30	75	finally
        //   30	57	75	finally
        //   67	72	75	finally
      }
      
      public boolean setAlwaysOnVpnPackage(ComponentName paramComponentName, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(91, localParcel1, localParcel2, 0);
                localParcel2.readException();
                i = localParcel2.readInt();
                if (i == 0) {
                  break label130;
                }
                paramBoolean = true;
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label130:
          paramBoolean = false;
        }
      }
      
      public boolean setApplicationHidden(ComponentName paramComponentName, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(114, localParcel1, localParcel2, 0);
                localParcel2.readException();
                i = localParcel2.readInt();
                if (i == 0) {
                  break label130;
                }
                paramBoolean = true;
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label130:
          paramBoolean = false;
        }
      }
      
      public void setApplicationRestrictions(ComponentName paramComponentName, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(95, localParcel1, localParcel2, 0);
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
      
      public boolean setApplicationRestrictionsManagingPackage(ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(97, localParcel1, localParcel2, 0);
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
      
      public void setAutoTimeRequired(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(149, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setBackupServiceEnabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(199, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setBluetoothContactSharingDisabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(141, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setCameraDisabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label105;
              localParcel1.writeInt(i);
              this.mRemote.transact(43, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label105:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public void setCertInstallerPackage(ComponentName paramComponentName, String paramString)
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
        //   31: aload_2
        //   32: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 89
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramComponentName	ComponentName
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void setCrossProfileCallerIdDisabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(134, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setCrossProfileContactsSearchDisabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(137, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public boolean setDeviceOwner(ComponentName paramComponentName, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(65, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      
      public void setDeviceOwnerLockScreenInfo(ComponentName paramComponentName, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramCharSequence != null)
              {
                localParcel1.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
                this.mRemote.transact(77, localParcel1, localParcel2, 0);
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
      
      public void setDeviceProvisioningConfigApplied()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          this.mRemote.transact(198, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setForceEphemeralUsers(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(151, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public ComponentName setGlobalProxy(ComponentName paramComponentName, String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString1);
              localParcel1.writeString(paramString2);
              this.mRemote.transact(36, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(localParcel2);
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
      public void setGlobalSetting(ComponentName paramComponentName, String paramString1, String paramString2)
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
        //   17: aload_1
        //   18: ifnull +61 -> 79
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 127
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 55 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 58	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 61	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 61	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -51 -> 34
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 61	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 61	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramComponentName	ComponentName
        //   0	101	2	paramString1	String
        //   0	101	3	paramString2	String
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	88	finally
        //   21	34	88	finally
        //   34	68	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void setKeepUninstalledPackages(ComponentName paramComponentName, List<String> paramList)
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
        //   16: ifnull +51 -> 67
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 360	android/os/Parcel:writeStringList	(Ljava/util/List;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: sipush 166
        //   42: aload_3
        //   43: aload 4
        //   45: iconst_0
        //   46: invokeinterface 55 5 0
        //   51: pop
        //   52: aload 4
        //   54: invokevirtual 58	android/os/Parcel:readException	()V
        //   57: aload 4
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: return
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramComponentName	ComponentName
        //   0	87	2	paramList	List<String>
        //   3	79	3	localParcel1	Parcel
        //   7	70	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	75	finally
        //   19	30	75	finally
        //   30	57	75	finally
        //   67	72	75	finally
      }
      
      public boolean setKeyguardDisabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label124;
              localParcel1.writeInt(i);
              this.mRemote.transact(157, localParcel1, localParcel2, 0);
              localParcel2.readException();
              i = localParcel2.readInt();
              if (i != 0)
              {
                paramBoolean = true;
                label80:
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label124:
          do
          {
            i = 0;
            break;
            paramBoolean = false;
            break label80;
          } while (!paramBoolean);
        }
      }
      
      public void setKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(47, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void setLockTaskPackages(ComponentName paramComponentName, String[] paramArrayOfString)
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
        //   31: aload_2
        //   32: invokevirtual 399	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 124
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramComponentName	ComponentName
        //   0	86	2	paramArrayOfString	String[]
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void setLongSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramCharSequence != null)
              {
                localParcel1.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
                this.mRemote.transact(174, localParcel1, localParcel2, 0);
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
      
      public void setMasterVolumeMuted(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(129, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(26, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setMaximumTimeToLock(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeLong(paramLong);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(29, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void setOrganizationColor(ComponentName paramComponentName, int paramInt)
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
        //   16: ifnull +51 -> 67
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
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: sipush 179
        //   42: aload_3
        //   43: aload 4
        //   45: iconst_0
        //   46: invokeinterface 55 5 0
        //   51: pop
        //   52: aload 4
        //   54: invokevirtual 58	android/os/Parcel:readException	()V
        //   57: aload 4
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: return
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramComponentName	ComponentName
        //   0	87	2	paramInt	int
        //   3	79	3	localParcel1	Parcel
        //   7	70	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	75	finally
        //   19	30	75	finally
        //   30	57	75	finally
        //   67	72	75	finally
      }
      
      public void setOrganizationColorForUser(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(180, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setOrganizationName(ComponentName paramComponentName, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramCharSequence != null)
              {
                localParcel1.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
                this.mRemote.transact(183, localParcel1, localParcel2, 0);
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
      
      public String[] setPackagesSuspended(ComponentName paramComponentName, String[] paramArrayOfString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeStringArray(paramArrayOfString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(79, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramComponentName = localParcel2.createStringArray();
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordExpirationTimeout(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeLong(paramLong);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(19, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordHistoryLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(17, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordMinimumLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(3, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordMinimumLetters(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(9, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(7, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(15, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(11, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(13, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(5, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setPasswordQuality(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean setPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString1);
              localParcel1.writeString(paramString2);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(163, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      public void setPermissionPolicy(ComponentName paramComponentName, int paramInt)
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
        //   16: ifnull +51 -> 67
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
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: sipush 161
        //   42: aload_3
        //   43: aload 4
        //   45: iconst_0
        //   46: invokeinterface 55 5 0
        //   51: pop
        //   52: aload 4
        //   54: invokevirtual 58	android/os/Parcel:readException	()V
        //   57: aload 4
        //   59: invokevirtual 61	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 61	android/os/Parcel:recycle	()V
        //   66: return
        //   67: aload_3
        //   68: iconst_0
        //   69: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   72: goto -42 -> 30
        //   75: astore_1
        //   76: aload 4
        //   78: invokevirtual 61	android/os/Parcel:recycle	()V
        //   81: aload_3
        //   82: invokevirtual 61	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramComponentName	ComponentName
        //   0	87	2	paramInt	int
        //   3	79	3	localParcel1	Parcel
        //   7	70	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	75	finally
        //   19	30	75	finally
        //   30	57	75	finally
        //   67	72	75	finally
      }
      
      public boolean setPermittedAccessibilityServices(ComponentName paramComponentName, List paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeList(paramList);
              this.mRemote.transact(106, localParcel1, localParcel2, 0);
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
      
      public boolean setPermittedInputMethods(ComponentName paramComponentName, List paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeList(paramList);
              this.mRemote.transact(110, localParcel1, localParcel2, 0);
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
      public void setProfileEnabled(ComponentName paramComponentName)
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
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 73
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 58	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 61	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 61	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 61	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramComponentName	ComponentName
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void setProfileName(ComponentName paramComponentName, String paramString)
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
        //   31: aload_2
        //   32: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 74
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramComponentName	ComponentName
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public boolean setProfileOwner(ComponentName paramComponentName, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(70, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
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
      
      public void setRecommendedGlobalProxy(ComponentName paramComponentName, ProxyInfo paramProxyInfo)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramProxyInfo != null)
              {
                localParcel1.writeInt(1);
                paramProxyInfo.writeToParcel(localParcel1, 0);
                this.mRemote.transact(38, localParcel1, localParcel2, 0);
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
      
      public void setRequiredStrongAuthTimeout(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeLong(paramLong);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(32, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setRestrictionsProvider(ComponentName paramComponentName1, ComponentName paramComponentName2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName1 != null)
            {
              localParcel1.writeInt(1);
              paramComponentName1.writeToParcel(localParcel1, 0);
              if (paramComponentName2 != null)
              {
                localParcel1.writeInt(1);
                paramComponentName2.writeToParcel(localParcel1, 0);
                this.mRemote.transact(100, localParcel1, localParcel2, 0);
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
      
      public void setScreenCaptureDisabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label105;
              localParcel1.writeInt(i);
              this.mRemote.transact(45, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label105:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public void setSecureSetting(ComponentName paramComponentName, String paramString1, String paramString2)
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
        //   17: aload_1
        //   18: ifnull +62 -> 80
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 46	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: sipush 128
        //   53: aload 4
        //   55: aload 5
        //   57: iconst_0
        //   58: invokeinterface 55 5 0
        //   63: pop
        //   64: aload 5
        //   66: invokevirtual 58	android/os/Parcel:readException	()V
        //   69: aload 5
        //   71: invokevirtual 61	android/os/Parcel:recycle	()V
        //   74: aload 4
        //   76: invokevirtual 61	android/os/Parcel:recycle	()V
        //   79: return
        //   80: aload 4
        //   82: iconst_0
        //   83: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   86: goto -52 -> 34
        //   89: astore_1
        //   90: aload 5
        //   92: invokevirtual 61	android/os/Parcel:recycle	()V
        //   95: aload 4
        //   97: invokevirtual 61	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramComponentName	ComponentName
        //   0	102	2	paramString1	String
        //   0	102	3	paramString2	String
        //   3	93	4	localParcel1	Parcel
        //   8	83	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	89	finally
        //   21	34	89	finally
        //   34	69	89	finally
        //   80	86	89	finally
      }
      
      public void setSecurityLoggingEnabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label106;
              localParcel1.writeInt(i);
              this.mRemote.transact(190, localParcel1, localParcel2, 0);
              localParcel2.readException();
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label106:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setShortSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramCharSequence != null)
              {
                localParcel1.writeInt(1);
                TextUtils.writeToParcel(paramCharSequence, localParcel1, 0);
                this.mRemote.transact(172, localParcel1, localParcel2, 0);
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
      
      public boolean setStatusBarDisabled(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label124;
              localParcel1.writeInt(i);
              this.mRemote.transact(158, localParcel1, localParcel2, 0);
              localParcel2.readException();
              i = localParcel2.readInt();
              if (i != 0)
              {
                paramBoolean = true;
                label80:
                return paramBoolean;
              }
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label124:
          do
          {
            i = 0;
            break;
            paramBoolean = false;
            break label80;
          } while (!paramBoolean);
        }
      }
      
      public int setStorageEncryption(ComponentName paramComponentName, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              break label112;
              localParcel1.writeInt(i);
              this.mRemote.transact(39, localParcel1, localParcel2, 0);
              localParcel2.readException();
              i = localParcel2.readInt();
              return i;
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label112:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void setSystemUpdatePolicy(ComponentName paramComponentName, SystemUpdatePolicy paramSystemUpdatePolicy)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramSystemUpdatePolicy != null)
              {
                localParcel1.writeInt(1);
                paramSystemUpdatePolicy.writeToParcel(localParcel1, 0);
                this.mRemote.transact(155, localParcel1, localParcel2, 0);
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
      
      public void setTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName1 != null)
            {
              localParcel1.writeInt(1);
              paramComponentName1.writeToParcel(localParcel1, 0);
              if (paramComponentName2 != null)
              {
                localParcel1.writeInt(1);
                paramComponentName2.writeToParcel(localParcel1, 0);
                if (paramPersistableBundle == null) {
                  break label146;
                }
                localParcel1.writeInt(1);
                paramPersistableBundle.writeToParcel(localParcel1, 0);
                break label161;
                localParcel1.writeInt(i);
                this.mRemote.transact(144, localParcel1, localParcel2, 0);
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
          label146:
          localParcel1.writeInt(0);
          label161:
          while (!paramBoolean)
          {
            i = 0;
            break;
          }
        }
      }
      
      public void setUninstallBlocked(ComponentName paramComponentName, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(132, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setUserIcon(ComponentName paramComponentName, Bitmap paramBitmap)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramBitmap != null)
              {
                localParcel1.writeInt(1);
                paramBitmap.writeToParcel(localParcel1, 0);
                this.mRemote.transact(154, localParcel1, localParcel2, 0);
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
      
      public void setUserProvisioningState(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(187, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setUserRestriction(ComponentName paramComponentName, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(102, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void startManagedQuickContact(String paramString, long paramLong1, boolean paramBoolean, long paramLong2, Intent paramIntent)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 8
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 9
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 10
        //   13: aload 9
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 9
        //   22: aload_1
        //   23: invokevirtual 67	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: aload 9
        //   28: lload_2
        //   29: invokevirtual 320	android/os/Parcel:writeLong	(J)V
        //   32: iload 4
        //   34: ifeq +70 -> 104
        //   37: aload 9
        //   39: iload 8
        //   41: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   44: aload 9
        //   46: lload 5
        //   48: invokevirtual 320	android/os/Parcel:writeLong	(J)V
        //   51: aload 7
        //   53: ifnull +57 -> 110
        //   56: aload 9
        //   58: iconst_1
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: aload 7
        //   64: aload 9
        //   66: iconst_0
        //   67: invokevirtual 114	android/content/Intent:writeToParcel	(Landroid/os/Parcel;I)V
        //   70: aload_0
        //   71: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   74: sipush 140
        //   77: aload 9
        //   79: aload 10
        //   81: iconst_0
        //   82: invokeinterface 55 5 0
        //   87: pop
        //   88: aload 10
        //   90: invokevirtual 58	android/os/Parcel:readException	()V
        //   93: aload 10
        //   95: invokevirtual 61	android/os/Parcel:recycle	()V
        //   98: aload 9
        //   100: invokevirtual 61	android/os/Parcel:recycle	()V
        //   103: return
        //   104: iconst_0
        //   105: istore 8
        //   107: goto -70 -> 37
        //   110: aload 9
        //   112: iconst_0
        //   113: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   116: goto -46 -> 70
        //   119: astore_1
        //   120: aload 10
        //   122: invokevirtual 61	android/os/Parcel:recycle	()V
        //   125: aload 9
        //   127: invokevirtual 61	android/os/Parcel:recycle	()V
        //   130: aload_1
        //   131: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	132	0	this	Proxy
        //   0	132	1	paramString	String
        //   0	132	2	paramLong1	long
        //   0	132	4	paramBoolean	boolean
        //   0	132	5	paramLong2	long
        //   0	132	7	paramIntent	Intent
        //   1	105	8	i	int
        //   6	120	9	localParcel1	Parcel
        //   11	110	10	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	119	finally
        //   37	51	119	finally
        //   56	70	119	finally
        //   70	93	119	finally
        //   110	116	119	finally
      }
      
      public boolean switchUser(ComponentName paramComponentName, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(118, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label130;
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
          label130:
          boolean bool = false;
        }
      }
      
      /* Error */
      public void uninstallCaCerts(ComponentName paramComponentName, String[] paramArrayOfString)
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
        //   31: aload_2
        //   32: invokevirtual 399	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/app/admin/IDevicePolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 82
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 55 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 58	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 61	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 61	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 61	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramComponentName	ComponentName
        //   0	86	2	paramArrayOfString	String[]
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void uninstallPackageWithActiveAdmins(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(195, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void wipeData(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.admin.IDevicePolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/IDevicePolicyManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */