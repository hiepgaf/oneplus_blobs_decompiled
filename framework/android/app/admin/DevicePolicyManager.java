package android.app.admin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.security.Credentials;
import android.util.Log;
import com.android.org.conscrypt.TrustedCertificateStore;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DevicePolicyManager
{
  public static final String ACCOUNT_FEATURE_DEVICE_OR_PROFILE_OWNER_ALLOWED = "android.account.DEVICE_OR_PROFILE_OWNER_ALLOWED";
  public static final String ACCOUNT_FEATURE_DEVICE_OR_PROFILE_OWNER_DISALLOWED = "android.account.DEVICE_OR_PROFILE_OWNER_DISALLOWED";
  public static final String ACTION_ADD_DEVICE_ADMIN = "android.app.action.ADD_DEVICE_ADMIN";
  public static final String ACTION_BUGREPORT_SHARING_ACCEPTED = "com.android.server.action.BUGREPORT_SHARING_ACCEPTED";
  public static final String ACTION_BUGREPORT_SHARING_DECLINED = "com.android.server.action.BUGREPORT_SHARING_DECLINED";
  public static final String ACTION_DEVICE_OWNER_CHANGED = "android.app.action.DEVICE_OWNER_CHANGED";
  public static final String ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED";
  public static final String ACTION_MANAGED_PROFILE_PROVISIONED = "android.app.action.MANAGED_PROFILE_PROVISIONED";
  public static final String ACTION_PROVISION_FINALIZATION = "android.app.action.PROVISION_FINALIZATION";
  public static final String ACTION_PROVISION_MANAGED_DEVICE = "android.app.action.PROVISION_MANAGED_DEVICE";
  public static final String ACTION_PROVISION_MANAGED_DEVICE_FROM_TRUSTED_SOURCE = "android.app.action.PROVISION_MANAGED_DEVICE_FROM_TRUSTED_SOURCE";
  public static final String ACTION_PROVISION_MANAGED_PROFILE = "android.app.action.PROVISION_MANAGED_PROFILE";
  public static final String ACTION_PROVISION_MANAGED_SHAREABLE_DEVICE = "android.app.action.PROVISION_MANAGED_SHAREABLE_DEVICE";
  public static final String ACTION_PROVISION_MANAGED_USER = "android.app.action.PROVISION_MANAGED_USER";
  public static final String ACTION_REMOTE_BUGREPORT_DISPATCH = "android.intent.action.REMOTE_BUGREPORT_DISPATCH";
  public static final String ACTION_SET_NEW_PARENT_PROFILE_PASSWORD = "android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD";
  public static final String ACTION_SET_NEW_PASSWORD = "android.app.action.SET_NEW_PASSWORD";
  public static final String ACTION_SET_PROFILE_OWNER = "android.app.action.SET_PROFILE_OWNER";
  public static final String ACTION_START_ENCRYPTION = "android.app.action.START_ENCRYPTION";
  public static final String ACTION_SYSTEM_UPDATE_POLICY_CHANGED = "android.app.action.SYSTEM_UPDATE_POLICY_CHANGED";
  public static final long DEFAULT_STRONG_AUTH_TIMEOUT_MS = 259200000L;
  public static final int ENCRYPTION_STATUS_ACTIVATING = 2;
  public static final int ENCRYPTION_STATUS_ACTIVE = 3;
  public static final int ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY = 4;
  public static final int ENCRYPTION_STATUS_ACTIVE_PER_USER = 5;
  public static final int ENCRYPTION_STATUS_INACTIVE = 1;
  public static final int ENCRYPTION_STATUS_UNSUPPORTED = 0;
  public static final String EXTRA_ADD_EXPLANATION = "android.app.extra.ADD_EXPLANATION";
  public static final String EXTRA_BUGREPORT_NOTIFICATION_TYPE = "android.app.extra.bugreport_notification_type";
  public static final String EXTRA_DEVICE_ADMIN = "android.app.extra.DEVICE_ADMIN";
  public static final String EXTRA_PROFILE_OWNER_NAME = "android.app.extra.PROFILE_OWNER_NAME";
  public static final String EXTRA_PROVISIONING_ACCOUNT_TO_MIGRATE = "android.app.extra.PROVISIONING_ACCOUNT_TO_MIGRATE";
  public static final String EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE = "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE";
  public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME = "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME";
  public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE = "android.app.extra.PROVISIONING_DEVICE_ADMIN_MINIMUM_VERSION_CODE";
  public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM";
  public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER";
  public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION";
  @Deprecated
  public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME";
  public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM = "android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM";
  public static final String EXTRA_PROVISIONING_EMAIL_ADDRESS = "android.app.extra.PROVISIONING_EMAIL_ADDRESS";
  public static final String EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED = "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED";
  public static final String EXTRA_PROVISIONING_LOCALE = "android.app.extra.PROVISIONING_LOCALE";
  public static final String EXTRA_PROVISIONING_LOCAL_TIME = "android.app.extra.PROVISIONING_LOCAL_TIME";
  public static final String EXTRA_PROVISIONING_LOGO_URI = "android.app.extra.PROVISIONING_LOGO_URI";
  public static final String EXTRA_PROVISIONING_MAIN_COLOR = "android.app.extra.PROVISIONING_MAIN_COLOR";
  public static final String EXTRA_PROVISIONING_SKIP_ENCRYPTION = "android.app.extra.PROVISIONING_SKIP_ENCRYPTION";
  public static final String EXTRA_PROVISIONING_SKIP_USER_SETUP = "android.app.extra.PROVISIONING_SKIP_USER_SETUP";
  public static final String EXTRA_PROVISIONING_TIME_ZONE = "android.app.extra.PROVISIONING_TIME_ZONE";
  public static final String EXTRA_PROVISIONING_WIFI_HIDDEN = "android.app.extra.PROVISIONING_WIFI_HIDDEN";
  public static final String EXTRA_PROVISIONING_WIFI_PAC_URL = "android.app.extra.PROVISIONING_WIFI_PAC_URL";
  public static final String EXTRA_PROVISIONING_WIFI_PASSWORD = "android.app.extra.PROVISIONING_WIFI_PASSWORD";
  public static final String EXTRA_PROVISIONING_WIFI_PROXY_BYPASS = "android.app.extra.PROVISIONING_WIFI_PROXY_BYPASS";
  public static final String EXTRA_PROVISIONING_WIFI_PROXY_HOST = "android.app.extra.PROVISIONING_WIFI_PROXY_HOST";
  public static final String EXTRA_PROVISIONING_WIFI_PROXY_PORT = "android.app.extra.PROVISIONING_WIFI_PROXY_PORT";
  public static final String EXTRA_PROVISIONING_WIFI_SECURITY_TYPE = "android.app.extra.PROVISIONING_WIFI_SECURITY_TYPE";
  public static final String EXTRA_PROVISIONING_WIFI_SSID = "android.app.extra.PROVISIONING_WIFI_SSID";
  public static final String EXTRA_REMOTE_BUGREPORT_HASH = "android.intent.extra.REMOTE_BUGREPORT_HASH";
  public static final int FLAG_MANAGED_CAN_ACCESS_PARENT = 2;
  public static final int FLAG_PARENT_CAN_ACCESS_MANAGED = 1;
  public static final int KEYGUARD_DISABLE_FEATURES_ALL = Integer.MAX_VALUE;
  public static final int KEYGUARD_DISABLE_FEATURES_NONE = 0;
  public static final int KEYGUARD_DISABLE_FINGERPRINT = 32;
  public static final int KEYGUARD_DISABLE_REMOTE_INPUT = 64;
  public static final int KEYGUARD_DISABLE_SECURE_CAMERA = 2;
  public static final int KEYGUARD_DISABLE_SECURE_NOTIFICATIONS = 4;
  public static final int KEYGUARD_DISABLE_TRUST_AGENTS = 16;
  public static final int KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS = 8;
  public static final int KEYGUARD_DISABLE_WIDGETS_ALL = 1;
  public static final int MAKE_USER_EPHEMERAL = 2;
  public static final String MIME_TYPE_PROVISIONING_NFC = "application/com.android.managedprovisioning";
  public static final int NOTIFICATION_BUGREPORT_ACCEPTED_NOT_FINISHED = 2;
  public static final int NOTIFICATION_BUGREPORT_FINISHED_NOT_ACCEPTED = 3;
  public static final int NOTIFICATION_BUGREPORT_STARTED = 1;
  public static final int PASSWORD_QUALITY_ALPHABETIC = 262144;
  public static final int PASSWORD_QUALITY_ALPHANUMERIC = 327680;
  public static final int PASSWORD_QUALITY_BIOMETRIC_WEAK = 32768;
  public static final int PASSWORD_QUALITY_COMPLEX = 393216;
  public static final int PASSWORD_QUALITY_MANAGED = 524288;
  public static final int PASSWORD_QUALITY_NUMERIC = 131072;
  public static final int PASSWORD_QUALITY_NUMERIC_COMPLEX = 196608;
  public static final int PASSWORD_QUALITY_SOMETHING = 65536;
  public static final int PASSWORD_QUALITY_UNSPECIFIED = 0;
  public static final int PERMISSION_GRANT_STATE_DEFAULT = 0;
  public static final int PERMISSION_GRANT_STATE_DENIED = 2;
  public static final int PERMISSION_GRANT_STATE_GRANTED = 1;
  public static final int PERMISSION_POLICY_AUTO_DENY = 2;
  public static final int PERMISSION_POLICY_AUTO_GRANT = 1;
  public static final int PERMISSION_POLICY_PROMPT = 0;
  public static final int RESET_PASSWORD_DO_NOT_ASK_CREDENTIALS_ON_BOOT = 2;
  public static final int RESET_PASSWORD_REQUIRE_ENTRY = 1;
  public static final int SKIP_SETUP_WIZARD = 1;
  public static final int STATE_USER_PROFILE_COMPLETE = 4;
  public static final int STATE_USER_SETUP_COMPLETE = 2;
  public static final int STATE_USER_SETUP_FINALIZED = 3;
  public static final int STATE_USER_SETUP_INCOMPLETE = 1;
  public static final int STATE_USER_UNMANAGED = 0;
  private static String TAG = "DevicePolicyManager";
  public static final int WIPE_EXTERNAL_STORAGE = 1;
  public static final int WIPE_RESET_PROTECTION_DATA = 2;
  private final Context mContext;
  private final boolean mParentInstance;
  private final IDevicePolicyManager mService;
  
  protected DevicePolicyManager(Context paramContext, IDevicePolicyManager paramIDevicePolicyManager, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mService = paramIDevicePolicyManager;
    this.mParentInstance = paramBoolean;
  }
  
  private DevicePolicyManager(Context paramContext, boolean paramBoolean)
  {
    this(paramContext, IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy")), paramBoolean);
  }
  
  public static DevicePolicyManager create(Context paramContext)
  {
    paramContext = new DevicePolicyManager(paramContext, false);
    if (paramContext.mService != null) {
      return paramContext;
    }
    return null;
  }
  
  private static String getCaCertAlias(byte[] paramArrayOfByte)
    throws CertificateException
  {
    paramArrayOfByte = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramArrayOfByte));
    return new TrustedCertificateStore().getCertificateAlias(paramArrayOfByte);
  }
  
  private ComponentName getDeviceOwnerComponentInner(boolean paramBoolean)
  {
    if (this.mService != null) {
      try
      {
        ComponentName localComponentName = this.mService.getDeviceOwnerComponent(paramBoolean);
        return localComponentName;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  private boolean isDeviceOwnerAppOnAnyUserInner(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return false;
    }
    ComponentName localComponentName = getDeviceOwnerComponentInner(paramBoolean);
    if (localComponentName == null) {
      return false;
    }
    return paramString.equals(localComponentName.getPackageName());
  }
  
  private void throwIfParentInstance(String paramString)
  {
    if (this.mParentInstance) {
      throw new SecurityException(paramString + " cannot be called on the parent instance");
    }
  }
  
  public void addCrossProfileIntentFilter(ComponentName paramComponentName, IntentFilter paramIntentFilter, int paramInt)
  {
    throwIfParentInstance("addCrossProfileIntentFilter");
    if (this.mService != null) {}
    try
    {
      this.mService.addCrossProfileIntentFilter(paramComponentName, paramIntentFilter, paramInt);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean addCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("addCrossProfileWidgetProvider");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.addCrossProfileWidgetProvider(paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void addPersistentPreferredActivity(ComponentName paramComponentName1, IntentFilter paramIntentFilter, ComponentName paramComponentName2)
  {
    throwIfParentInstance("addPersistentPreferredActivity");
    if (this.mService != null) {}
    try
    {
      this.mService.addPersistentPreferredActivity(paramComponentName1, paramIntentFilter, paramComponentName2);
      return;
    }
    catch (RemoteException paramComponentName1)
    {
      throw paramComponentName1.rethrowFromSystemServer();
    }
  }
  
  public void addUserRestriction(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("addUserRestriction");
    if (this.mService != null) {}
    try
    {
      this.mService.setUserRestriction(paramComponentName, paramString, true);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean approveCaCert(String paramString, int paramInt, boolean paramBoolean)
  {
    if (this.mService != null) {
      try
      {
        paramBoolean = this.mService.approveCaCert(paramString, paramInt, paramBoolean);
        return paramBoolean;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void clearCrossProfileIntentFilters(ComponentName paramComponentName)
  {
    throwIfParentInstance("clearCrossProfileIntentFilters");
    if (this.mService != null) {}
    try
    {
      this.mService.clearCrossProfileIntentFilters(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void clearDeviceOwnerApp(String paramString)
  {
    throwIfParentInstance("clearDeviceOwnerApp");
    if (this.mService != null) {}
    try
    {
      this.mService.clearDeviceOwner(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void clearPackagePersistentPreferredActivities(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("clearPackagePersistentPreferredActivities");
    if (this.mService != null) {}
    try
    {
      this.mService.clearPackagePersistentPreferredActivities(paramComponentName, paramString);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void clearProfileOwner(ComponentName paramComponentName)
  {
    throwIfParentInstance("clearProfileOwner");
    if (this.mService != null) {}
    try
    {
      this.mService.clearProfileOwner(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void clearUserRestriction(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("clearUserRestriction");
    if (this.mService != null) {}
    try
    {
      this.mService.setUserRestriction(paramComponentName, paramString, false);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public UserHandle createAndInitializeUser(ComponentName paramComponentName1, String paramString1, String paramString2, ComponentName paramComponentName2, Bundle paramBundle)
  {
    return null;
  }
  
  public UserHandle createAndManageUser(ComponentName paramComponentName1, String paramString, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle, int paramInt)
  {
    throwIfParentInstance("createAndManageUser");
    try
    {
      paramComponentName1 = this.mService.createAndManageUser(paramComponentName1, paramString, paramComponentName2, paramPersistableBundle, paramInt);
      return paramComponentName1;
    }
    catch (RemoteException paramComponentName1)
    {
      throw paramComponentName1.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public UserHandle createUser(ComponentName paramComponentName, String paramString)
  {
    return null;
  }
  
  public int enableSystemApp(ComponentName paramComponentName, Intent paramIntent)
  {
    throwIfParentInstance("enableSystemApp");
    if (this.mService != null) {
      try
      {
        int i = this.mService.enableSystemAppWithIntent(paramComponentName, paramIntent);
        return i;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public void enableSystemApp(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("enableSystemApp");
    if (this.mService != null) {}
    try
    {
      this.mService.enableSystemApp(paramComponentName, paramString);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void forceRemoveActiveAdmin(ComponentName paramComponentName, int paramInt)
  {
    try
    {
      this.mService.forceRemoveActiveAdmin(paramComponentName, paramInt);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public String[] getAccountTypesWithManagementDisabled()
  {
    throwIfParentInstance("getAccountTypesWithManagementDisabled");
    return getAccountTypesWithManagementDisabledAsUser(myUserId());
  }
  
  public String[] getAccountTypesWithManagementDisabledAsUser(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        String[] arrayOfString = this.mService.getAccountTypesWithManagementDisabledAsUser(paramInt);
        return arrayOfString;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public List<ComponentName> getActiveAdmins()
  {
    throwIfParentInstance("getActiveAdmins");
    return getActiveAdminsAsUser(myUserId());
  }
  
  public List<ComponentName> getActiveAdminsAsUser(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        List localList = this.mService.getActiveAdmins(paramInt);
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public String getAlwaysOnVpnPackage(ComponentName paramComponentName)
  {
    throwIfParentInstance("getAlwaysOnVpnPackage");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getAlwaysOnVpnPackage(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public Bundle getApplicationRestrictions(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("getApplicationRestrictions");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getApplicationRestrictions(paramComponentName, paramString);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public String getApplicationRestrictionsManagingPackage(ComponentName paramComponentName)
  {
    throwIfParentInstance("getApplicationRestrictionsManagingPackage");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getApplicationRestrictionsManagingPackage(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public boolean getAutoTimeRequired()
  {
    throwIfParentInstance("getAutoTimeRequired");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getAutoTimeRequired();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean getBluetoothContactSharingDisabled(ComponentName paramComponentName)
  {
    throwIfParentInstance("getBluetoothContactSharingDisabled");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getBluetoothContactSharingDisabled(paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return true;
  }
  
  public boolean getBluetoothContactSharingDisabled(UserHandle paramUserHandle)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getBluetoothContactSharingDisabledForUser(paramUserHandle.getIdentifier());
        return bool;
      }
      catch (RemoteException paramUserHandle)
      {
        throw paramUserHandle.rethrowFromSystemServer();
      }
    }
    return true;
  }
  
  public boolean getCameraDisabled(ComponentName paramComponentName)
  {
    throwIfParentInstance("getCameraDisabled");
    return getCameraDisabled(paramComponentName, myUserId());
  }
  
  public boolean getCameraDisabled(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getCameraDisabled(paramComponentName, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public String getCertInstallerPackage(ComponentName paramComponentName)
    throws SecurityException
  {
    throwIfParentInstance("getCertInstallerPackage");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getCertInstallerPackage(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public boolean getCrossProfileCallerIdDisabled(ComponentName paramComponentName)
  {
    throwIfParentInstance("getCrossProfileCallerIdDisabled");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getCrossProfileCallerIdDisabled(paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean getCrossProfileCallerIdDisabled(UserHandle paramUserHandle)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getCrossProfileCallerIdDisabledForUser(paramUserHandle.getIdentifier());
        return bool;
      }
      catch (RemoteException paramUserHandle)
      {
        throw paramUserHandle.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean getCrossProfileContactsSearchDisabled(ComponentName paramComponentName)
  {
    throwIfParentInstance("getCrossProfileContactsSearchDisabled");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getCrossProfileContactsSearchDisabled(paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean getCrossProfileContactsSearchDisabled(UserHandle paramUserHandle)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getCrossProfileContactsSearchDisabledForUser(paramUserHandle.getIdentifier());
        return bool;
      }
      catch (RemoteException paramUserHandle)
      {
        throw paramUserHandle.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public List<String> getCrossProfileWidgetProviders(ComponentName paramComponentName)
  {
    throwIfParentInstance("getCrossProfileWidgetProviders");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getCrossProfileWidgetProviders(paramComponentName);
        if (paramComponentName != null) {
          return paramComponentName;
        }
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return Collections.emptyList();
  }
  
  public int getCurrentFailedPasswordAttempts()
  {
    return getCurrentFailedPasswordAttempts(myUserId());
  }
  
  public int getCurrentFailedPasswordAttempts(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getCurrentFailedPasswordAttempts(paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return -1;
  }
  
  @Deprecated
  public String getDeviceInitializerApp()
  {
    return null;
  }
  
  @Deprecated
  public ComponentName getDeviceInitializerComponent()
  {
    return null;
  }
  
  public String getDeviceOwner()
  {
    String str = null;
    throwIfParentInstance("getDeviceOwner");
    ComponentName localComponentName = getDeviceOwnerComponentOnCallingUser();
    if (localComponentName != null) {
      str = localComponentName.getPackageName();
    }
    return str;
  }
  
  public ComponentName getDeviceOwnerComponentOnAnyUser()
  {
    return getDeviceOwnerComponentInner(false);
  }
  
  public ComponentName getDeviceOwnerComponentOnCallingUser()
  {
    return getDeviceOwnerComponentInner(true);
  }
  
  public CharSequence getDeviceOwnerLockScreenInfo()
  {
    throwIfParentInstance("getDeviceOwnerLockScreenInfo");
    if (this.mService != null) {
      try
      {
        CharSequence localCharSequence = this.mService.getDeviceOwnerLockScreenInfo();
        return localCharSequence;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public String getDeviceOwnerNameOnAnyUser()
  {
    throwIfParentInstance("getDeviceOwnerNameOnAnyUser");
    if (this.mService != null) {
      try
      {
        String str = this.mService.getDeviceOwnerName();
        return str;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public int getDeviceOwnerUserId()
  {
    if (this.mService != null) {
      try
      {
        int i = this.mService.getDeviceOwnerUserId();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return 55536;
  }
  
  public boolean getDoNotAskCredentialsOnBoot()
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getDoNotAskCredentialsOnBoot();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean getForceEphemeralUsers(ComponentName paramComponentName)
  {
    throwIfParentInstance("getForceEphemeralUsers");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getForceEphemeralUsers(paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public ComponentName getGlobalProxyAdmin()
  {
    if (this.mService != null) {
      try
      {
        ComponentName localComponentName = this.mService.getGlobalProxyAdmin(myUserId());
        return localComponentName;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public boolean getGuestUserDisabled(ComponentName paramComponentName)
  {
    return false;
  }
  
  public List<byte[]> getInstalledCaCerts(ComponentName paramComponentName)
  {
    localArrayList = new ArrayList();
    throwIfParentInstance("getInstalledCaCerts");
    if (this.mService != null) {
      try
      {
        this.mService.enforceCanManageCaCerts(paramComponentName);
        paramComponentName = new TrustedCertificateStore();
        Iterator localIterator = paramComponentName.userAliases().iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          try
          {
            localArrayList.add(paramComponentName.getCertificate(str).getEncoded());
          }
          catch (CertificateException localCertificateException)
          {
            Log.w(TAG, "Could not encode certificate: " + str, localCertificateException);
          }
        }
        return localArrayList;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
  }
  
  public List<String> getKeepUninstalledPackages(ComponentName paramComponentName)
  {
    throwIfParentInstance("getKeepUninstalledPackages");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getKeepUninstalledPackages(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public int getKeyguardDisabledFeatures(ComponentName paramComponentName)
  {
    return getKeyguardDisabledFeatures(paramComponentName, myUserId());
  }
  
  public int getKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getKeyguardDisabledFeatures(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public String[] getLockTaskPackages(ComponentName paramComponentName)
  {
    throwIfParentInstance("getLockTaskPackages");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getLockTaskPackages(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public CharSequence getLongSupportMessage(ComponentName paramComponentName)
  {
    throwIfParentInstance("getLongSupportMessage");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getLongSupportMessage(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public CharSequence getLongSupportMessageForUser(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getLongSupportMessageForUser(paramComponentName, paramInt);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public int getMaximumFailedPasswordsForWipe(ComponentName paramComponentName)
  {
    return getMaximumFailedPasswordsForWipe(paramComponentName, myUserId());
  }
  
  public int getMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getMaximumFailedPasswordsForWipe(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public long getMaximumTimeToLock(ComponentName paramComponentName)
  {
    return getMaximumTimeToLock(paramComponentName, myUserId());
  }
  
  public long getMaximumTimeToLock(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        long l = this.mService.getMaximumTimeToLock(paramComponentName, paramInt, this.mParentInstance);
        return l;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0L;
  }
  
  public long getMaximumTimeToLockForUserAndProfiles(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        long l = this.mService.getMaximumTimeToLockForUserAndProfiles(paramInt);
        return l;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return 0L;
  }
  
  public int getOrganizationColor(ComponentName paramComponentName)
  {
    throwIfParentInstance("getOrganizationColor");
    try
    {
      int i = this.mService.getOrganizationColor(paramComponentName);
      return i;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public int getOrganizationColorForUser(int paramInt)
  {
    try
    {
      paramInt = this.mService.getOrganizationColorForUser(paramInt);
      return paramInt;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public CharSequence getOrganizationName(ComponentName paramComponentName)
  {
    throwIfParentInstance("getOrganizationName");
    try
    {
      paramComponentName = this.mService.getOrganizationName(paramComponentName);
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public CharSequence getOrganizationNameForUser(int paramInt)
  {
    try
    {
      CharSequence localCharSequence = this.mService.getOrganizationNameForUser(paramInt);
      return localCharSequence;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public DevicePolicyManager getParentProfileInstance(ComponentName paramComponentName)
  {
    throwIfParentInstance("getParentProfileInstance");
    try
    {
      if (!this.mService.isManagedProfile(paramComponentName)) {
        throw new SecurityException("The current user does not have a parent profile.");
      }
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
    paramComponentName = new DevicePolicyManager(this.mContext, true);
    return paramComponentName;
  }
  
  public DevicePolicyManager getParentProfileInstance(UserInfo paramUserInfo)
  {
    this.mContext.checkSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS");
    if (!paramUserInfo.isManagedProfile()) {
      throw new SecurityException("The user " + paramUserInfo.id + " does not have a parent profile.");
    }
    return new DevicePolicyManager(this.mContext, true);
  }
  
  public long getPasswordExpiration(ComponentName paramComponentName)
  {
    if (this.mService != null) {
      try
      {
        long l = this.mService.getPasswordExpiration(paramComponentName, myUserId(), this.mParentInstance);
        return l;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0L;
  }
  
  public long getPasswordExpirationTimeout(ComponentName paramComponentName)
  {
    if (this.mService != null) {
      try
      {
        long l = this.mService.getPasswordExpirationTimeout(paramComponentName, myUserId(), this.mParentInstance);
        return l;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0L;
  }
  
  public int getPasswordHistoryLength(ComponentName paramComponentName)
  {
    return getPasswordHistoryLength(paramComponentName, myUserId());
  }
  
  public int getPasswordHistoryLength(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordHistoryLength(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordMaximumLength(int paramInt)
  {
    return 16;
  }
  
  public int getPasswordMinimumLength(ComponentName paramComponentName)
  {
    return getPasswordMinimumLength(paramComponentName, myUserId());
  }
  
  public int getPasswordMinimumLength(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordMinimumLength(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordMinimumLetters(ComponentName paramComponentName)
  {
    return getPasswordMinimumLetters(paramComponentName, myUserId());
  }
  
  public int getPasswordMinimumLetters(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordMinimumLetters(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordMinimumLowerCase(ComponentName paramComponentName)
  {
    return getPasswordMinimumLowerCase(paramComponentName, myUserId());
  }
  
  public int getPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordMinimumLowerCase(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordMinimumNonLetter(ComponentName paramComponentName)
  {
    return getPasswordMinimumNonLetter(paramComponentName, myUserId());
  }
  
  public int getPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordMinimumNonLetter(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordMinimumNumeric(ComponentName paramComponentName)
  {
    return getPasswordMinimumNumeric(paramComponentName, myUserId());
  }
  
  public int getPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordMinimumNumeric(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordMinimumSymbols(ComponentName paramComponentName)
  {
    return getPasswordMinimumSymbols(paramComponentName, myUserId());
  }
  
  public int getPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordMinimumSymbols(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordMinimumUpperCase(ComponentName paramComponentName)
  {
    return getPasswordMinimumUpperCase(paramComponentName, myUserId());
  }
  
  public int getPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordMinimumUpperCase(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPasswordQuality(ComponentName paramComponentName)
  {
    return getPasswordQuality(paramComponentName, myUserId());
  }
  
  public int getPasswordQuality(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getPasswordQuality(paramComponentName, paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public int getPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2)
  {
    throwIfParentInstance("getPermissionGrantState");
    try
    {
      int i = this.mService.getPermissionGrantState(paramComponentName, paramString1, paramString2);
      return i;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public int getPermissionPolicy(ComponentName paramComponentName)
  {
    throwIfParentInstance("getPermissionPolicy");
    try
    {
      int i = this.mService.getPermissionPolicy(paramComponentName);
      return i;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public List<String> getPermittedAccessibilityServices(int paramInt)
  {
    throwIfParentInstance("getPermittedAccessibilityServices");
    if (this.mService != null) {
      try
      {
        List localList = this.mService.getPermittedAccessibilityServicesForUser(paramInt);
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public List<String> getPermittedAccessibilityServices(ComponentName paramComponentName)
  {
    throwIfParentInstance("getPermittedAccessibilityServices");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getPermittedAccessibilityServices(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public List<String> getPermittedInputMethods(ComponentName paramComponentName)
  {
    throwIfParentInstance("getPermittedInputMethods");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getPermittedInputMethods(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public List<String> getPermittedInputMethodsForCurrentUser()
  {
    throwIfParentInstance("getPermittedInputMethodsForCurrentUser");
    if (this.mService != null) {
      try
      {
        List localList = this.mService.getPermittedInputMethodsForCurrentUser();
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public ComponentName getProfileOwner()
    throws IllegalArgumentException
  {
    throwIfParentInstance("getProfileOwner");
    return getProfileOwnerAsUser(Process.myUserHandle().getIdentifier());
  }
  
  public ComponentName getProfileOwnerAsUser(int paramInt)
    throws IllegalArgumentException
  {
    if (this.mService != null) {
      try
      {
        ComponentName localComponentName = this.mService.getProfileOwner(paramInt);
        return localComponentName;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public String getProfileOwnerName()
    throws IllegalArgumentException
  {
    if (this.mService != null) {
      try
      {
        String str = this.mService.getProfileOwnerName(Process.myUserHandle().getIdentifier());
        return str;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public String getProfileOwnerNameAsUser(int paramInt)
    throws IllegalArgumentException
  {
    throwIfParentInstance("getProfileOwnerNameAsUser");
    if (this.mService != null) {
      try
      {
        String str = this.mService.getProfileOwnerName(paramInt);
        return str;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public int getProfileWithMinimumFailedPasswordsForWipe(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getProfileWithMinimumFailedPasswordsForWipe(paramInt, this.mParentInstance);
        return paramInt;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return 55536;
  }
  
  public void getRemoveWarning(ComponentName paramComponentName, RemoteCallback paramRemoteCallback)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.getRemoveWarning(paramComponentName, paramRemoteCallback, myUserId());
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public long getRequiredStrongAuthTimeout(ComponentName paramComponentName)
  {
    return getRequiredStrongAuthTimeout(paramComponentName, myUserId());
  }
  
  public long getRequiredStrongAuthTimeout(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        long l = this.mService.getRequiredStrongAuthTimeout(paramComponentName, paramInt, this.mParentInstance);
        return l;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 259200000L;
  }
  
  public boolean getScreenCaptureDisabled(ComponentName paramComponentName)
  {
    throwIfParentInstance("getScreenCaptureDisabled");
    return getScreenCaptureDisabled(paramComponentName, myUserId());
  }
  
  public boolean getScreenCaptureDisabled(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getScreenCaptureDisabled(paramComponentName, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public CharSequence getShortSupportMessage(ComponentName paramComponentName)
  {
    throwIfParentInstance("getShortSupportMessage");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getShortSupportMessage(paramComponentName);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public CharSequence getShortSupportMessageForUser(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.getShortSupportMessageForUser(paramComponentName, paramInt);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public boolean getStorageEncryption(ComponentName paramComponentName)
  {
    throwIfParentInstance("getStorageEncryption");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.getStorageEncryption(paramComponentName, myUserId());
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public int getStorageEncryptionStatus()
  {
    throwIfParentInstance("getStorageEncryptionStatus");
    return getStorageEncryptionStatus(myUserId());
  }
  
  public int getStorageEncryptionStatus(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramInt = this.mService.getStorageEncryptionStatus(this.mContext.getPackageName(), paramInt);
        return paramInt;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public SystemUpdatePolicy getSystemUpdatePolicy()
  {
    throwIfParentInstance("getSystemUpdatePolicy");
    if (this.mService != null) {
      try
      {
        SystemUpdatePolicy localSystemUpdatePolicy = this.mService.getSystemUpdatePolicy();
        return localSystemUpdatePolicy;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return null;
  }
  
  public List<PersistableBundle> getTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2)
  {
    return getTrustAgentConfiguration(paramComponentName1, paramComponentName2, myUserId());
  }
  
  public List<PersistableBundle> getTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        paramComponentName1 = this.mService.getTrustAgentConfiguration(paramComponentName1, paramComponentName2, paramInt, this.mParentInstance);
        return paramComponentName1;
      }
      catch (RemoteException paramComponentName1)
      {
        throw paramComponentName1.rethrowFromSystemServer();
      }
    }
    return new ArrayList();
  }
  
  public int getUserProvisioningState()
  {
    throwIfParentInstance("getUserProvisioningState");
    if (this.mService != null) {
      try
      {
        int i = this.mService.getUserProvisioningState();
        return i;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public Bundle getUserRestrictions(ComponentName paramComponentName)
  {
    throwIfParentInstance("getUserRestrictions");
    Bundle localBundle = null;
    if (this.mService != null) {}
    try
    {
      localBundle = this.mService.getUserRestrictions(paramComponentName);
      paramComponentName = localBundle;
      if (localBundle == null) {
        paramComponentName = new Bundle();
      }
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public String getWifiMacAddress(ComponentName paramComponentName)
  {
    throwIfParentInstance("getWifiMacAddress");
    try
    {
      paramComponentName = this.mService.getWifiMacAddress(paramComponentName);
      return paramComponentName;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean hasCaCertInstalled(ComponentName paramComponentName, byte[] paramArrayOfByte)
  {
    boolean bool = false;
    throwIfParentInstance("hasCaCertInstalled");
    if (this.mService != null) {}
    try
    {
      this.mService.enforceCanManageCaCerts(paramComponentName);
      paramComponentName = getCaCertAlias(paramArrayOfByte);
      if (paramComponentName != null) {
        bool = true;
      }
      return bool;
    }
    catch (CertificateException paramComponentName)
    {
      Log.w(TAG, "Could not parse certificate", paramComponentName);
      return false;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean hasGrantedPolicy(ComponentName paramComponentName, int paramInt)
  {
    throwIfParentInstance("hasGrantedPolicy");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.hasGrantedPolicy(paramComponentName, paramInt, myUserId());
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean hasUserSetupCompleted()
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.hasUserSetupCompleted();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return true;
  }
  
  public boolean installCaCert(ComponentName paramComponentName, byte[] paramArrayOfByte)
  {
    throwIfParentInstance("installCaCert");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.installCaCert(paramComponentName, paramArrayOfByte);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean installKeyPair(ComponentName paramComponentName, PrivateKey paramPrivateKey, Certificate paramCertificate, String paramString)
  {
    return installKeyPair(paramComponentName, paramPrivateKey, new Certificate[] { paramCertificate }, paramString, false);
  }
  
  public boolean installKeyPair(ComponentName paramComponentName, PrivateKey paramPrivateKey, Certificate[] paramArrayOfCertificate, String paramString, boolean paramBoolean)
  {
    throwIfParentInstance("installKeyPair");
    try
    {
      byte[] arrayOfByte2 = Credentials.convertToPem(new Certificate[] { paramArrayOfCertificate[0] });
      byte[] arrayOfByte1 = null;
      if (paramArrayOfCertificate.length > 1) {
        arrayOfByte1 = Credentials.convertToPem((Certificate[])Arrays.copyOfRange(paramArrayOfCertificate, 1, paramArrayOfCertificate.length));
      }
      paramPrivateKey = ((PKCS8EncodedKeySpec)KeyFactory.getInstance(paramPrivateKey.getAlgorithm()).getKeySpec(paramPrivateKey, PKCS8EncodedKeySpec.class)).getEncoded();
      paramBoolean = this.mService.installKeyPair(paramComponentName, paramPrivateKey, arrayOfByte2, arrayOfByte1, paramString, paramBoolean);
      return paramBoolean;
    }
    catch (CertificateException|IOException paramComponentName)
    {
      Log.w(TAG, "Could not pem-encode certificate", paramComponentName);
      return false;
    }
    catch (NoSuchAlgorithmException|InvalidKeySpecException paramComponentName)
    {
      for (;;)
      {
        Log.w(TAG, "Failed to obtain private key material", paramComponentName);
      }
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean isAccessibilityServicePermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isAccessibilityServicePermittedByAdmin(paramComponentName, paramString, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isActivePasswordSufficient()
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isActivePasswordSufficient(myUserId(), this.mParentInstance);
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isAdminActive(ComponentName paramComponentName)
  {
    return isAdminActiveAsUser(paramComponentName, myUserId());
  }
  
  public boolean isAdminActiveAsUser(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isAdminActive(paramComponentName, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isAffiliatedUser()
  {
    throwIfParentInstance("isAffiliatedUser");
    try
    {
      if (this.mService != null)
      {
        boolean bool = this.mService.isAffiliatedUser();
        return bool;
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isApplicationHidden(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("isApplicationHidden");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isApplicationHidden(paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isBackupServiceEnabled(ComponentName paramComponentName)
  {
    try
    {
      boolean bool = this.mService.isBackupServiceEnabled(paramComponentName);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean isCaCertApproved(String paramString, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isCaCertApproved(paramString, paramInt);
        return bool;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isCallerApplicationRestrictionsManagingPackage()
  {
    throwIfParentInstance("isCallerApplicationRestrictionsManagingPackage");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isCallerApplicationRestrictionsManagingPackage();
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isDeviceManaged()
  {
    return getDeviceOwnerComponentOnAnyUser() != null;
  }
  
  public boolean isDeviceOwnerApp(String paramString)
  {
    throwIfParentInstance("isDeviceOwnerApp");
    return isDeviceOwnerAppOnCallingUser(paramString);
  }
  
  public boolean isDeviceOwnerAppOnAnyUser(String paramString)
  {
    return isDeviceOwnerAppOnAnyUserInner(paramString, false);
  }
  
  public boolean isDeviceOwnerAppOnCallingUser(String paramString)
  {
    return isDeviceOwnerAppOnAnyUserInner(paramString, true);
  }
  
  public boolean isDeviceProvisioned()
  {
    try
    {
      boolean bool = this.mService.isDeviceProvisioned();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isDeviceProvisioningConfigApplied()
  {
    try
    {
      boolean bool = this.mService.isDeviceProvisioningConfigApplied();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isInputMethodPermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isInputMethodPermittedByAdmin(paramComponentName, paramString, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isLockTaskPermitted(String paramString)
  {
    throwIfParentInstance("isLockTaskPermitted");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isLockTaskPermitted(paramString);
        return bool;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isManagedProfile(ComponentName paramComponentName)
  {
    throwIfParentInstance("isManagedProfile");
    try
    {
      boolean bool = this.mService.isManagedProfile(paramComponentName);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean isMasterVolumeMuted(ComponentName paramComponentName)
  {
    throwIfParentInstance("isMasterVolumeMuted");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isMasterVolumeMuted(paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isPackageSuspended(ComponentName paramComponentName, String paramString)
    throws PackageManager.NameNotFoundException
  {
    throwIfParentInstance("isPackageSuspended");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isPackageSuspended(paramComponentName, paramString);
        return bool;
      }
      catch (IllegalArgumentException paramComponentName)
      {
        throw new PackageManager.NameNotFoundException(paramString);
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isProfileActivePasswordSufficientForParent(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isProfileActivePasswordSufficientForParent(paramInt);
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isProfileOwnerApp(String paramString)
  {
    boolean bool = false;
    throwIfParentInstance("isProfileOwnerApp");
    if (this.mService != null) {
      try
      {
        ComponentName localComponentName = this.mService.getProfileOwner(myUserId());
        if (localComponentName != null) {
          bool = localComponentName.getPackageName().equals(paramString);
        }
        return bool;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isProvisioningAllowed(String paramString)
  {
    throwIfParentInstance("isProvisioningAllowed");
    try
    {
      boolean bool = this.mService.isProvisioningAllowed(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isRemovingAdmin(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isRemovingAdmin(paramComponentName, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isSecurityLoggingEnabled(ComponentName paramComponentName)
  {
    throwIfParentInstance("isSecurityLoggingEnabled");
    try
    {
      boolean bool = this.mService.isSecurityLoggingEnabled(paramComponentName);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean isSeparateProfileChallengeAllowed(int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isSeparateProfileChallengeAllowed(paramInt);
        return bool;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isSystemOnlyUser(ComponentName paramComponentName)
  {
    try
    {
      boolean bool = this.mService.isSystemOnlyUser(paramComponentName);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean isUninstallBlocked(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("isUninstallBlocked");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.isUninstallBlocked(paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean isUninstallInQueue(String paramString)
  {
    try
    {
      boolean bool = this.mService.isUninstallInQueue(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void lockNow()
  {
    if (this.mService != null) {}
    try
    {
      this.mService.lockNow(this.mParentInstance);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  protected int myUserId()
  {
    return UserHandle.myUserId();
  }
  
  public void notifyPendingSystemUpdate(long paramLong)
  {
    throwIfParentInstance("notifyPendingSystemUpdate");
    if (this.mService != null) {}
    try
    {
      this.mService.notifyPendingSystemUpdate(paramLong);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean packageHasActiveAdmins(String paramString)
  {
    return packageHasActiveAdmins(paramString, myUserId());
  }
  
  public boolean packageHasActiveAdmins(String paramString, int paramInt)
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.packageHasActiveAdmins(paramString, paramInt);
        return bool;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void reboot(ComponentName paramComponentName)
  {
    throwIfParentInstance("reboot");
    try
    {
      this.mService.reboot(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void removeActiveAdmin(ComponentName paramComponentName)
  {
    throwIfParentInstance("removeActiveAdmin");
    if (this.mService != null) {}
    try
    {
      this.mService.removeActiveAdmin(paramComponentName, myUserId());
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean removeCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("removeCrossProfileWidgetProvider");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.removeCrossProfileWidgetProvider(paramComponentName, paramString);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean removeKeyPair(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("removeKeyPair");
    try
    {
      boolean bool = this.mService.removeKeyPair(paramComponentName, paramString);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean removeUser(ComponentName paramComponentName, UserHandle paramUserHandle)
  {
    throwIfParentInstance("removeUser");
    try
    {
      boolean bool = this.mService.removeUser(paramComponentName, paramUserHandle);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void reportFailedFingerprintAttempt(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.reportFailedFingerprintAttempt(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportFailedPasswordAttempt(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.reportFailedPasswordAttempt(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportKeyguardDismissed(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.reportKeyguardDismissed(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportKeyguardSecured(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.reportKeyguardSecured(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportPasswordChanged(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.reportPasswordChanged(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportSuccessfulFingerprintAttempt(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.reportSuccessfulFingerprintAttempt(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void reportSuccessfulPasswordAttempt(int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.reportSuccessfulPasswordAttempt(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean requestBugreport(ComponentName paramComponentName)
  {
    throwIfParentInstance("requestBugreport");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.requestBugreport(paramComponentName);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean resetPassword(String paramString, int paramInt)
  {
    throwIfParentInstance("resetPassword");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.resetPassword(paramString, paramInt);
        return bool;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public List<SecurityLog.SecurityEvent> retrievePreRebootSecurityLogs(ComponentName paramComponentName)
  {
    throwIfParentInstance("retrievePreRebootSecurityLogs");
    try
    {
      paramComponentName = this.mService.retrievePreRebootSecurityLogs(paramComponentName);
      if (paramComponentName != null)
      {
        paramComponentName = paramComponentName.getList();
        return paramComponentName;
      }
      return null;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public List<SecurityLog.SecurityEvent> retrieveSecurityLogs(ComponentName paramComponentName)
  {
    throwIfParentInstance("retrieveSecurityLogs");
    try
    {
      paramComponentName = this.mService.retrieveSecurityLogs(paramComponentName);
      if (paramComponentName != null)
      {
        paramComponentName = paramComponentName.getList();
        return paramComponentName;
      }
      return null;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setAccountManagementDisabled(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    throwIfParentInstance("setAccountManagementDisabled");
    if (this.mService != null) {}
    try
    {
      this.mService.setAccountManagementDisabled(paramComponentName, paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setActiveAdmin(ComponentName paramComponentName, boolean paramBoolean)
  {
    setActiveAdmin(paramComponentName, paramBoolean, myUserId());
  }
  
  public void setActiveAdmin(ComponentName paramComponentName, boolean paramBoolean, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setActiveAdmin(paramComponentName, paramBoolean, paramInt);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setActivePasswordState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setActivePasswordState(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramInt9);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean setActiveProfileOwner(ComponentName paramComponentName, @Deprecated String paramString)
    throws IllegalArgumentException
  {
    throwIfParentInstance("setActiveProfileOwner");
    if (this.mService != null) {
      try
      {
        int i = myUserId();
        this.mService.setActiveAdmin(paramComponentName, false, i);
        boolean bool = this.mService.setProfileOwner(paramComponentName, paramString, i);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void setAffiliationIds(ComponentName paramComponentName, Set<String> paramSet)
  {
    throwIfParentInstance("setAffiliationIds");
    try
    {
      this.mService.setAffiliationIds(paramComponentName, new ArrayList(paramSet));
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setAlwaysOnVpnPackage(ComponentName paramComponentName, String paramString)
    throws PackageManager.NameNotFoundException, UnsupportedOperationException
  {
    setAlwaysOnVpnPackage(paramComponentName, paramString, true);
  }
  
  public void setAlwaysOnVpnPackage(ComponentName paramComponentName, String paramString, boolean paramBoolean)
    throws PackageManager.NameNotFoundException, UnsupportedOperationException
  {
    throwIfParentInstance("setAlwaysOnVpnPackage");
    if (this.mService != null) {
      try
      {
        if (!this.mService.setAlwaysOnVpnPackage(paramComponentName, paramString, paramBoolean)) {
          throw new PackageManager.NameNotFoundException(paramString);
        }
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
  }
  
  public boolean setApplicationHidden(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    throwIfParentInstance("setApplicationHidden");
    if (this.mService != null) {
      try
      {
        paramBoolean = this.mService.setApplicationHidden(paramComponentName, paramString, paramBoolean);
        return paramBoolean;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void setApplicationRestrictions(ComponentName paramComponentName, String paramString, Bundle paramBundle)
  {
    throwIfParentInstance("setApplicationRestrictions");
    if (this.mService != null) {}
    try
    {
      this.mService.setApplicationRestrictions(paramComponentName, paramString, paramBundle);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setApplicationRestrictionsManagingPackage(ComponentName paramComponentName, String paramString)
    throws PackageManager.NameNotFoundException
  {
    throwIfParentInstance("setApplicationRestrictionsManagingPackage");
    if (this.mService != null) {
      try
      {
        if (!this.mService.setApplicationRestrictionsManagingPackage(paramComponentName, paramString)) {
          throw new PackageManager.NameNotFoundException(paramString);
        }
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
  }
  
  public void setAutoTimeRequired(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setAutoTimeRequired");
    if (this.mService != null) {}
    try
    {
      this.mService.setAutoTimeRequired(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setBackupServiceEnabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    try
    {
      this.mService.setBackupServiceEnabled(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setBluetoothContactSharingDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setBluetoothContactSharingDisabled");
    if (this.mService != null) {}
    try
    {
      this.mService.setBluetoothContactSharingDisabled(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setCameraDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setCameraDisabled");
    if (this.mService != null) {}
    try
    {
      this.mService.setCameraDisabled(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setCertInstallerPackage(ComponentName paramComponentName, String paramString)
    throws SecurityException
  {
    throwIfParentInstance("setCertInstallerPackage");
    if (this.mService != null) {}
    try
    {
      this.mService.setCertInstallerPackage(paramComponentName, paramString);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setCrossProfileCallerIdDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setCrossProfileCallerIdDisabled");
    if (this.mService != null) {}
    try
    {
      this.mService.setCrossProfileCallerIdDisabled(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setCrossProfileContactsSearchDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setCrossProfileContactsSearchDisabled");
    if (this.mService != null) {}
    try
    {
      this.mService.setCrossProfileContactsSearchDisabled(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean setDeviceOwner(ComponentName paramComponentName)
  {
    return setDeviceOwner(paramComponentName, null);
  }
  
  public boolean setDeviceOwner(ComponentName paramComponentName, int paramInt)
  {
    return setDeviceOwner(paramComponentName, null, paramInt);
  }
  
  public boolean setDeviceOwner(ComponentName paramComponentName, String paramString)
  {
    return setDeviceOwner(paramComponentName, paramString, 0);
  }
  
  public boolean setDeviceOwner(ComponentName paramComponentName, String paramString, int paramInt)
    throws IllegalArgumentException, IllegalStateException
  {
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.setDeviceOwner(paramComponentName, paramString, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void setDeviceOwnerLockScreenInfo(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    throwIfParentInstance("setDeviceOwnerLockScreenInfo");
    if (this.mService != null) {}
    try
    {
      this.mService.setDeviceOwnerLockScreenInfo(paramComponentName, paramCharSequence);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setDeviceProvisioningConfigApplied()
  {
    try
    {
      this.mService.setDeviceProvisioningConfigApplied();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setForceEphemeralUsers(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setForceEphemeralUsers");
    if (this.mService != null) {}
    try
    {
      this.mService.setForceEphemeralUsers(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public ComponentName setGlobalProxy(ComponentName paramComponentName, java.net.Proxy paramProxy, List<String> paramList)
  {
    throwIfParentInstance("setGlobalProxy");
    if (paramProxy == null) {
      throw new NullPointerException();
    }
    int i;
    label183:
    String str3;
    if (this.mService != null)
    {
      String str2;
      int j;
      do
      {
        try
        {
          if (paramProxy.equals(java.net.Proxy.NO_PROXY))
          {
            str1 = null;
            paramList = null;
            return this.mService.setGlobalProxy(paramComponentName, str1, paramList);
          }
          if (!paramProxy.type().equals(Proxy.Type.HTTP)) {
            throw new IllegalArgumentException();
          }
        }
        catch (RemoteException paramComponentName)
        {
          throw paramComponentName.rethrowFromSystemServer();
        }
        paramProxy = (InetSocketAddress)paramProxy.address();
        str2 = paramProxy.getHostName();
        j = paramProxy.getPort();
        String str1 = str2 + ":" + Integer.toString(j);
        if (paramList != null) {
          break;
        }
        paramProxy = "";
        paramList = paramProxy;
      } while (android.net.Proxy.validate(str2, Integer.toString(j), paramProxy) == 0);
      throw new IllegalArgumentException();
      paramProxy = new StringBuilder();
      i = 1;
      paramList = paramList.iterator();
      if (paramList.hasNext())
      {
        str3 = (String)paramList.next();
        if (i != 0) {
          break label239;
        }
        paramProxy = paramProxy.append(",");
      }
    }
    for (;;)
    {
      paramProxy = paramProxy.append(str3.trim());
      break label183;
      paramProxy = paramProxy.toString();
      break;
      return null;
      label239:
      i = 0;
    }
  }
  
  public void setGlobalSetting(ComponentName paramComponentName, String paramString1, String paramString2)
  {
    throwIfParentInstance("setGlobalSetting");
    if (this.mService != null) {}
    try
    {
      this.mService.setGlobalSetting(paramComponentName, paramString1, paramString2);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setKeepUninstalledPackages(ComponentName paramComponentName, List<String> paramList)
  {
    throwIfParentInstance("setKeepUninstalledPackages");
    if (this.mService != null) {}
    try
    {
      this.mService.setKeepUninstalledPackages(paramComponentName, paramList);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean setKeyguardDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setKeyguardDisabled");
    try
    {
      paramBoolean = this.mService.setKeyguardDisabled(paramComponentName, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setKeyguardDisabledFeatures(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setLockTaskPackages(ComponentName paramComponentName, String[] paramArrayOfString)
    throws SecurityException
  {
    throwIfParentInstance("setLockTaskPackages");
    if (this.mService != null) {}
    try
    {
      this.mService.setLockTaskPackages(paramComponentName, paramArrayOfString);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setLongSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    throwIfParentInstance("setLongSupportMessage");
    if (this.mService != null) {}
    try
    {
      this.mService.setLongSupportMessage(paramComponentName, paramCharSequence);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setMasterVolumeMuted(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setMasterVolumeMuted");
    if (this.mService != null) {}
    try
    {
      this.mService.setMasterVolumeMuted(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setMaximumFailedPasswordsForWipe(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setMaximumTimeToLock(ComponentName paramComponentName, long paramLong)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setMaximumTimeToLock(paramComponentName, paramLong, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setOrganizationColor(ComponentName paramComponentName, int paramInt)
  {
    throwIfParentInstance("setOrganizationColor");
    try
    {
      this.mService.setOrganizationColor(paramComponentName, paramInt | 0xFF000000);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setOrganizationColorForUser(int paramInt1, int paramInt2)
  {
    try
    {
      this.mService.setOrganizationColorForUser(paramInt1 | 0xFF000000, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setOrganizationName(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    throwIfParentInstance("setOrganizationName");
    try
    {
      this.mService.setOrganizationName(paramComponentName, paramCharSequence);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public String[] setPackagesSuspended(ComponentName paramComponentName, String[] paramArrayOfString, boolean paramBoolean)
  {
    throwIfParentInstance("setPackagesSuspended");
    if (this.mService != null) {
      try
      {
        paramComponentName = this.mService.setPackagesSuspended(paramComponentName, paramArrayOfString, paramBoolean);
        return paramComponentName;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return paramArrayOfString;
  }
  
  public void setPasswordExpirationTimeout(ComponentName paramComponentName, long paramLong)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordExpirationTimeout(paramComponentName, paramLong, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordHistoryLength(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordHistoryLength(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordMinimumLength(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordMinimumLength(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordMinimumLetters(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordMinimumLetters(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordMinimumLowerCase(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordMinimumNonLetter(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordMinimumNumeric(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordMinimumSymbols(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordMinimumUpperCase(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPasswordQuality(ComponentName paramComponentName, int paramInt)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setPasswordQuality(paramComponentName, paramInt, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean setPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2, int paramInt)
  {
    throwIfParentInstance("setPermissionGrantState");
    try
    {
      boolean bool = this.mService.setPermissionGrantState(paramComponentName, paramString1, paramString2, paramInt);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setPermissionPolicy(ComponentName paramComponentName, int paramInt)
  {
    throwIfParentInstance("setPermissionPolicy");
    try
    {
      this.mService.setPermissionPolicy(paramComponentName, paramInt);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean setPermittedAccessibilityServices(ComponentName paramComponentName, List<String> paramList)
  {
    throwIfParentInstance("setPermittedAccessibilityServices");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.setPermittedAccessibilityServices(paramComponentName, paramList);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public boolean setPermittedInputMethods(ComponentName paramComponentName, List<String> paramList)
  {
    throwIfParentInstance("setPermittedInputMethods");
    if (this.mService != null) {
      try
      {
        boolean bool = this.mService.setPermittedInputMethods(paramComponentName, paramList);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void setProfileEnabled(ComponentName paramComponentName)
  {
    throwIfParentInstance("setProfileEnabled");
    if (this.mService != null) {}
    try
    {
      this.mService.setProfileEnabled(paramComponentName);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setProfileName(ComponentName paramComponentName, String paramString)
  {
    throwIfParentInstance("setProfileName");
    if (this.mService != null) {}
    try
    {
      this.mService.setProfileName(paramComponentName, paramString);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean setProfileOwner(ComponentName paramComponentName, @Deprecated String paramString, int paramInt)
    throws IllegalArgumentException
  {
    if (this.mService != null)
    {
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      try
      {
        boolean bool = this.mService.setProfileOwner(paramComponentName, str, paramInt);
        return bool;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return false;
  }
  
  public void setRecommendedGlobalProxy(ComponentName paramComponentName, ProxyInfo paramProxyInfo)
  {
    throwIfParentInstance("setRecommendedGlobalProxy");
    if (this.mService != null) {}
    try
    {
      this.mService.setRecommendedGlobalProxy(paramComponentName, paramProxyInfo);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setRequiredStrongAuthTimeout(ComponentName paramComponentName, long paramLong)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setRequiredStrongAuthTimeout(paramComponentName, paramLong, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setRestrictionsProvider(ComponentName paramComponentName1, ComponentName paramComponentName2)
  {
    throwIfParentInstance("setRestrictionsProvider");
    if (this.mService != null) {}
    try
    {
      this.mService.setRestrictionsProvider(paramComponentName1, paramComponentName2);
      return;
    }
    catch (RemoteException paramComponentName1)
    {
      throw paramComponentName1.rethrowFromSystemServer();
    }
  }
  
  public void setScreenCaptureDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setScreenCaptureDisabled");
    if (this.mService != null) {}
    try
    {
      this.mService.setScreenCaptureDisabled(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setSecureSetting(ComponentName paramComponentName, String paramString1, String paramString2)
  {
    throwIfParentInstance("setSecureSetting");
    if (this.mService != null) {}
    try
    {
      this.mService.setSecureSetting(paramComponentName, paramString1, paramString2);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setSecurityLoggingEnabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setSecurityLoggingEnabled");
    try
    {
      this.mService.setSecurityLoggingEnabled(paramComponentName, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setShortSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    throwIfParentInstance("setShortSupportMessage");
    if (this.mService != null) {}
    try
    {
      this.mService.setShortSupportMessage(paramComponentName, paramCharSequence);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public boolean setStatusBarDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setStatusBarDisabled");
    try
    {
      paramBoolean = this.mService.setStatusBarDisabled(paramComponentName, paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public int setStorageEncryption(ComponentName paramComponentName, boolean paramBoolean)
  {
    throwIfParentInstance("setStorageEncryption");
    if (this.mService != null) {
      try
      {
        int i = this.mService.setStorageEncryption(paramComponentName, paramBoolean);
        return i;
      }
      catch (RemoteException paramComponentName)
      {
        throw paramComponentName.rethrowFromSystemServer();
      }
    }
    return 0;
  }
  
  public void setSystemUpdatePolicy(ComponentName paramComponentName, SystemUpdatePolicy paramSystemUpdatePolicy)
  {
    throwIfParentInstance("setSystemUpdatePolicy");
    if (this.mService != null) {}
    try
    {
      this.mService.setSystemUpdatePolicy(paramComponentName, paramSystemUpdatePolicy);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setTrustAgentConfiguration(paramComponentName1, paramComponentName2, paramPersistableBundle, this.mParentInstance);
      return;
    }
    catch (RemoteException paramComponentName1)
    {
      throw paramComponentName1.rethrowFromSystemServer();
    }
  }
  
  public void setUninstallBlocked(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    throwIfParentInstance("setUninstallBlocked");
    if (this.mService != null) {}
    try
    {
      this.mService.setUninstallBlocked(paramComponentName, paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setUserIcon(ComponentName paramComponentName, Bitmap paramBitmap)
  {
    throwIfParentInstance("setUserIcon");
    try
    {
      this.mService.setUserIcon(paramComponentName, paramBitmap);
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void setUserProvisioningState(int paramInt1, int paramInt2)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.setUserProvisioningState(paramInt1, paramInt2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void startManagedQuickContact(String paramString, long paramLong, Intent paramIntent)
  {
    startManagedQuickContact(paramString, paramLong, false, 0L, paramIntent);
  }
  
  public void startManagedQuickContact(String paramString, long paramLong1, boolean paramBoolean, long paramLong2, Intent paramIntent)
  {
    if (this.mService != null) {}
    try
    {
      this.mService.startManagedQuickContact(paramString, paramLong1, paramBoolean, paramLong2, paramIntent);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean switchUser(ComponentName paramComponentName, UserHandle paramUserHandle)
  {
    throwIfParentInstance("switchUser");
    try
    {
      boolean bool = this.mService.switchUser(paramComponentName, paramUserHandle);
      return bool;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void uninstallAllUserCaCerts(ComponentName paramComponentName)
  {
    throwIfParentInstance("uninstallAllUserCaCerts");
    if (this.mService != null) {}
    try
    {
      this.mService.uninstallCaCerts(paramComponentName, (String[])new TrustedCertificateStore().userAliases().toArray(new String[0]));
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
  }
  
  public void uninstallCaCert(ComponentName paramComponentName, byte[] paramArrayOfByte)
  {
    throwIfParentInstance("uninstallCaCert");
    if (this.mService != null) {}
    try
    {
      paramArrayOfByte = getCaCertAlias(paramArrayOfByte);
      this.mService.uninstallCaCerts(paramComponentName, new String[] { paramArrayOfByte });
      return;
    }
    catch (RemoteException paramComponentName)
    {
      throw paramComponentName.rethrowFromSystemServer();
    }
    catch (CertificateException paramComponentName)
    {
      Log.w(TAG, "Unable to parse certificate", paramComponentName);
    }
  }
  
  public void uninstallPackageWithActiveAdmins(String paramString)
  {
    try
    {
      this.mService.uninstallPackageWithActiveAdmins(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void wipeData(int paramInt)
  {
    throwIfParentInstance("wipeData");
    if (this.mService != null) {}
    try
    {
      this.mService.wipeData(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/admin/DevicePolicyManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */