package com.android.server.devicepolicy;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.IntDef;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminInfo.PolicyInfo;
import android.app.admin.DevicePolicyManagerInternal;
import android.app.admin.DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener;
import android.app.admin.IDevicePolicyManager.Stub;
import android.app.admin.SecurityLog;
import android.app.admin.SecurityLog.SecurityEvent;
import android.app.admin.SystemUpdatePolicy;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.IAudioService;
import android.media.IAudioService.Stub;
import android.net.ConnectivityManager;
import android.net.ProxyInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RecoverySystem;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.os.storage.StorageManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.security.IKeyChainAliasCallback;
import android.security.IKeyChainAliasCallback.Stub;
import android.security.IKeyChainService;
import android.security.KeyChain;
import android.security.KeyChain.KeyChainConnection;
import android.service.persistentdata.PersistentDataBlockManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManager.Stub;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.pm.UserRestrictionsUtils;
import com.google.android.collect.Sets;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class DevicePolicyManagerService
  extends IDevicePolicyManager.Stub
{
  private static final String ACTION_EXPIRED_PASSWORD_NOTIFICATION = "com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION";
  private static final String ATTR_APPLICATION_RESTRICTIONS_MANAGER = "application-restrictions-manager";
  private static final String ATTR_DELEGATED_CERT_INSTALLER = "delegated-cert-installer";
  private static final String ATTR_DEVICE_PROVISIONING_CONFIG_APPLIED = "device-provisioning-config-applied";
  private static final String ATTR_DISABLED = "disabled";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_PERMISSION_POLICY = "permission-policy";
  private static final String ATTR_PERMISSION_PROVIDER = "permission-provider";
  private static final String ATTR_PROVISIONING_STATE = "provisioning-state";
  private static final String ATTR_SETUP_COMPLETE = "setup-complete";
  private static final String ATTR_VALUE = "value";
  private static final int CODE_ACCOUNTS_NOT_EMPTY = 6;
  private static final int CODE_HAS_DEVICE_OWNER = 1;
  private static final int CODE_NONSYSTEM_USER_EXISTS = 5;
  private static final int CODE_NOT_SYSTEM_USER = 7;
  private static final int CODE_OK = 0;
  private static final int CODE_USER_HAS_PROFILE_OWNER = 2;
  private static final int CODE_USER_NOT_RUNNING = 3;
  private static final int CODE_USER_SETUP_COMPLETED = 4;
  private static final int DEVICE_ADMIN_DEACTIVATE_TIMEOUT = 10000;
  private static final String DEVICE_POLICIES_XML = "device_policies.xml";
  private static final String DO_NOT_ASK_CREDENTIALS_ON_BOOT_XML = "do-not-ask-credentials-on-boot";
  private static final long EXPIRATION_GRACE_PERIOD_MS = 432000000L;
  private static final Set<String> GLOBAL_SETTINGS_DEPRECATED;
  private static final Set<String> GLOBAL_SETTINGS_WHITELIST;
  private static final String LOG_TAG = "DevicePolicyManagerService";
  private static final long MINIMUM_STRONG_AUTH_TIMEOUT_MS = 3600000L;
  private static final int MONITORING_CERT_NOTIFICATION_ID = 18087937;
  private static final long MS_PER_DAY = 86400000L;
  private static final int PROFILE_KEYGUARD_FEATURES = 56;
  private static final int PROFILE_KEYGUARD_FEATURES_AFFECT_OWNER = 48;
  private static final int PROFILE_KEYGUARD_FEATURES_PROFILE_ONLY = 8;
  private static final int PROFILE_WIPED_NOTIFICATION_ID = 1001;
  private static final String PROPERTY_DEVICE_OWNER_PRESENT = "ro.device_owner";
  private static final int REQUEST_EXPIRE_PASSWORD = 5571;
  private static final Set<String> SECURE_SETTINGS_DEVICEOWNER_WHITELIST;
  private static final Set<String> SECURE_SETTINGS_WHITELIST = new ArraySet();
  private static final int STATUS_BAR_DISABLE2_MASK = 1;
  private static final int STATUS_BAR_DISABLE_MASK = 34013184;
  private static final String TAG_ACCEPTED_CA_CERTIFICATES = "accepted-ca-certificate";
  private static final String TAG_ADMIN_BROADCAST_PENDING = "admin-broadcast-pending";
  private static final String TAG_AFFILIATION_ID = "affiliation-id";
  private static final String TAG_INITIALIZATION_BUNDLE = "initialization-bundle";
  private static final String TAG_LOCK_TASK_COMPONENTS = "lock-task-component";
  private static final String TAG_STATUS_BAR = "statusbar";
  private static final boolean VERBOSE_LOG = false;
  final Context mContext;
  final Handler mHandler;
  boolean mHasFeature;
  final IPackageManager mIPackageManager;
  final Injector mInjector;
  final LocalService mLocalService;
  private final LockPatternUtils mLockPatternUtils;
  final Owners mOwners;
  private final Set<Pair<String, Integer>> mPackagesToRemove = new ArraySet();
  BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      ??? = paramAnonymousIntent.getAction();
      final int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", getSendingUserId());
      if (("android.intent.action.BOOT_COMPLETED".equals(???)) && (i == DevicePolicyManagerService.this.mOwners.getDeviceOwnerUserId()) && (DevicePolicyManagerService.-wrap3(DevicePolicyManagerService.this) != null))
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("com.android.server.action.BUGREPORT_SHARING_DECLINED");
        localIntentFilter.addAction("com.android.server.action.BUGREPORT_SHARING_ACCEPTED");
        DevicePolicyManagerService.this.mContext.registerReceiver(DevicePolicyManagerService.-get0(DevicePolicyManagerService.this), localIntentFilter);
        DevicePolicyManagerService.this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManagerService", 678432343, RemoteBugreportUtils.buildNotification(DevicePolicyManagerService.this.mContext, 3), UserHandle.ALL);
      }
      if (("android.intent.action.BOOT_COMPLETED".equals(???)) || ("com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION".equals(???))) {
        DevicePolicyManagerService.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            DevicePolicyManagerService.-wrap7(DevicePolicyManagerService.this, i);
          }
        });
      }
      if (("android.intent.action.USER_UNLOCKED".equals(???)) || ("android.intent.action.USER_STARTED".equals(???)) || ("android.security.STORAGE_CHANGED".equals(???)))
      {
        int j = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
        new DevicePolicyManagerService.MonitoringCertNotificationTask(DevicePolicyManagerService.this, null).execute(new Integer[] { Integer.valueOf(j) });
      }
      if ("android.intent.action.USER_ADDED".equals(???)) {
        DevicePolicyManagerService.-wrap5(DevicePolicyManagerService.this);
      }
      do
      {
        return;
        if ("android.intent.action.USER_REMOVED".equals(???))
        {
          DevicePolicyManagerService.-wrap5(DevicePolicyManagerService.this);
          DevicePolicyManagerService.this.removeUserData(i);
          return;
        }
        if ("android.intent.action.USER_STARTED".equals(???)) {
          synchronized (DevicePolicyManagerService.this)
          {
            DevicePolicyManagerService.this.mUserData.remove(i);
            DevicePolicyManagerService.-wrap16(DevicePolicyManagerService.this, i);
            DevicePolicyManagerService.-wrap6(DevicePolicyManagerService.this, null, i);
            return;
          }
        }
        if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(???))
        {
          DevicePolicyManagerService.-wrap6(DevicePolicyManagerService.this, null, i);
          return;
        }
        if (("android.intent.action.PACKAGE_CHANGED".equals(???)) || (("android.intent.action.PACKAGE_ADDED".equals(???)) && (paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false))))
        {
          DevicePolicyManagerService.-wrap6(DevicePolicyManagerService.this, paramAnonymousIntent.getData().getSchemeSpecificPart(), i);
          return;
        }
        if (("android.intent.action.PACKAGE_REMOVED".equals(???)) && (!paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false))) {
          break;
        }
      } while (!"android.intent.action.MANAGED_PROFILE_ADDED".equals(???));
      DevicePolicyManagerService.-wrap4(DevicePolicyManagerService.this);
      return;
      DevicePolicyManagerService.-wrap6(DevicePolicyManagerService.this, paramAnonymousIntent.getData().getSchemeSpecificPart(), i);
    }
  };
  private final BroadcastReceiver mRemoteBugreportConsentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      DevicePolicyManagerService.this.mInjector.getNotificationManager().cancel("DevicePolicyManagerService", 678432343);
      if ("com.android.server.action.BUGREPORT_SHARING_ACCEPTED".equals(paramAnonymousContext)) {
        DevicePolicyManagerService.-wrap10(DevicePolicyManagerService.this);
      }
      for (;;)
      {
        DevicePolicyManagerService.this.mContext.unregisterReceiver(DevicePolicyManagerService.-get0(DevicePolicyManagerService.this));
        return;
        if ("com.android.server.action.BUGREPORT_SHARING_DECLINED".equals(paramAnonymousContext)) {
          DevicePolicyManagerService.-wrap11(DevicePolicyManagerService.this);
        }
      }
    }
  };
  private final BroadcastReceiver mRemoteBugreportFinishedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (("android.intent.action.REMOTE_BUGREPORT_DISPATCH".equals(paramAnonymousIntent.getAction())) && (DevicePolicyManagerService.-get1(DevicePolicyManagerService.this).get())) {
        DevicePolicyManagerService.-wrap9(DevicePolicyManagerService.this, paramAnonymousIntent);
      }
    }
  };
  private final AtomicBoolean mRemoteBugreportServiceIsActive = new AtomicBoolean();
  private final AtomicBoolean mRemoteBugreportSharingAccepted = new AtomicBoolean();
  private final Runnable mRemoteBugreportTimeoutRunnable = new Runnable()
  {
    public void run()
    {
      if (DevicePolicyManagerService.-get1(DevicePolicyManagerService.this).get()) {
        DevicePolicyManagerService.-wrap8(DevicePolicyManagerService.this);
      }
    }
  };
  private final SecurityLogMonitor mSecurityLogMonitor;
  final TelephonyManager mTelephonyManager;
  private final Binder mToken = new Binder();
  final SparseArray<DevicePolicyData> mUserData = new SparseArray();
  final UserManager mUserManager;
  final UserManagerInternal mUserManagerInternal;
  
  static
  {
    SECURE_SETTINGS_WHITELIST.add("default_input_method");
    SECURE_SETTINGS_WHITELIST.add("skip_first_use_hints");
    SECURE_SETTINGS_WHITELIST.add("install_non_market_apps");
    SECURE_SETTINGS_DEVICEOWNER_WHITELIST = new ArraySet();
    SECURE_SETTINGS_DEVICEOWNER_WHITELIST.addAll(SECURE_SETTINGS_WHITELIST);
    SECURE_SETTINGS_DEVICEOWNER_WHITELIST.add("location_mode");
    GLOBAL_SETTINGS_WHITELIST = new ArraySet();
    GLOBAL_SETTINGS_WHITELIST.add("adb_enabled");
    GLOBAL_SETTINGS_WHITELIST.add("auto_time");
    GLOBAL_SETTINGS_WHITELIST.add("auto_time_zone");
    GLOBAL_SETTINGS_WHITELIST.add("data_roaming");
    GLOBAL_SETTINGS_WHITELIST.add("usb_mass_storage_enabled");
    GLOBAL_SETTINGS_WHITELIST.add("wifi_sleep_policy");
    GLOBAL_SETTINGS_WHITELIST.add("stay_on_while_plugged_in");
    GLOBAL_SETTINGS_WHITELIST.add("wifi_device_owner_configs_lockdown");
    GLOBAL_SETTINGS_DEPRECATED = new ArraySet();
    GLOBAL_SETTINGS_DEPRECATED.add("bluetooth_on");
    GLOBAL_SETTINGS_DEPRECATED.add("development_settings_enabled");
    GLOBAL_SETTINGS_DEPRECATED.add("mode_ringer");
    GLOBAL_SETTINGS_DEPRECATED.add("network_preference");
    GLOBAL_SETTINGS_DEPRECATED.add("wifi_on");
  }
  
  public DevicePolicyManagerService(Context paramContext)
  {
    this(new Injector(paramContext));
  }
  
  DevicePolicyManagerService(Injector paramInjector)
  {
    this.mInjector = paramInjector;
    this.mContext = ((Context)Preconditions.checkNotNull(Injector.-get0(paramInjector)));
    this.mHandler = new Handler((Looper)Preconditions.checkNotNull(paramInjector.getMyLooper()));
    this.mOwners = ((Owners)Preconditions.checkNotNull(paramInjector.newOwners()));
    this.mUserManager = ((UserManager)Preconditions.checkNotNull(paramInjector.getUserManager()));
    this.mUserManagerInternal = ((UserManagerInternal)Preconditions.checkNotNull(paramInjector.getUserManagerInternal()));
    this.mIPackageManager = ((IPackageManager)Preconditions.checkNotNull(paramInjector.getIPackageManager()));
    this.mTelephonyManager = ((TelephonyManager)Preconditions.checkNotNull(paramInjector.getTelephonyManager()));
    this.mLocalService = new LocalService();
    this.mLockPatternUtils = paramInjector.newLockPatternUtils();
    this.mSecurityLogMonitor = new SecurityLogMonitor(this);
    this.mHasFeature = this.mContext.getPackageManager().hasSystemFeature("android.software.device_admin");
    if (!this.mHasFeature) {
      return;
    }
    paramInjector = new IntentFilter();
    paramInjector.addAction("android.intent.action.BOOT_COMPLETED");
    paramInjector.addAction("com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION");
    paramInjector.addAction("android.intent.action.USER_ADDED");
    paramInjector.addAction("android.intent.action.USER_REMOVED");
    paramInjector.addAction("android.intent.action.USER_STARTED");
    paramInjector.addAction("android.intent.action.USER_UNLOCKED");
    paramInjector.addAction("android.security.STORAGE_CHANGED");
    paramInjector.setPriority(1000);
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, paramInjector, null, this.mHandler);
    paramInjector = new IntentFilter();
    paramInjector.addAction("android.intent.action.PACKAGE_CHANGED");
    paramInjector.addAction("android.intent.action.PACKAGE_REMOVED");
    paramInjector.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
    paramInjector.addAction("android.intent.action.PACKAGE_ADDED");
    paramInjector.addDataScheme("package");
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, paramInjector, null, this.mHandler);
    paramInjector = new IntentFilter();
    paramInjector.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
    this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, paramInjector, null, this.mHandler);
    LocalServices.addService(DevicePolicyManagerInternal.class, this.mLocalService);
  }
  
  private boolean checkCallerIsCurrentUserOrProfile()
  {
    int i = UserHandle.getCallingUserId();
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      UserInfo localUserInfo1 = getUserInfo(i);
      UserInfo localUserInfo2;
      try
      {
        localUserInfo2 = this.mInjector.getIActivityManager().getCurrentUser();
        if ((localUserInfo1.isManagedProfile()) && (localUserInfo1.profileGroupId != localUserInfo2.id))
        {
          Slog.e("DevicePolicyManagerService", "Cannot set permitted input methods for managed profile of a user that isn't the foreground user.");
          return false;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("DevicePolicyManagerService", "Failed to talk to activity managed.", localRemoteException);
        return false;
      }
      if ((!localRemoteException.isManagedProfile()) && (i != localUserInfo2.id))
      {
        Slog.e("DevicePolicyManagerService", "Cannot set permitted input methods of a user that isn't the foreground user.");
        return false;
      }
      return true;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private boolean checkPackagesInPermittedListOrSystem(List<String> paramList1, List<String> paramList2, int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    for (;;)
    {
      try
      {
        Object localObject = getUserInfo(paramInt);
        int i = paramInt;
        if (((UserInfo)localObject).isManagedProfile()) {
          i = ((UserInfo)localObject).profileGroupId;
        }
        paramList1 = paramList1.iterator();
        if (!paramList1.hasNext()) {
          break;
        }
        localObject = (String)paramList1.next();
        paramInt = 0;
        try
        {
          int j = this.mIPackageManager.getApplicationInfo((String)localObject, 8192, i).flags;
          if ((j & 0x1) == 0) {
            continue;
          }
          paramInt = 1;
        }
        catch (RemoteException localRemoteException)
        {
          boolean bool;
          Log.i("DevicePolicyManagerService", "Can't talk to package managed", localRemoteException);
          continue;
        }
        if (paramInt != 0) {
          continue;
        }
        bool = paramList2.contains(localObject);
        if (bool) {
          continue;
        }
        return false;
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      paramInt = 0;
    }
    this.mInjector.binderRestoreCallingIdentity(l);
    return true;
  }
  
  private int checkSetDeviceOwnerPreConditionLocked(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    try
    {
      boolean bool = this.mOwners.hasDeviceOwner();
      if (bool) {
        return 1;
      }
      bool = this.mOwners.hasProfileOwner(paramInt);
      if (bool) {
        return 2;
      }
      bool = this.mUserManager.isUserRunning(new UserHandle(paramInt));
      if (!bool) {
        return 3;
      }
      if (paramBoolean)
      {
        if ((hasUserSetupCompleted(0)) && (!this.mInjector.userManagerIsSplitSystemUser()))
        {
          paramInt = this.mUserManager.getUserCount();
          if (paramInt > 1) {
            return 5;
          }
          paramBoolean = hasIncompatibleAccountsLocked(0, paramComponentName);
          if (paramBoolean) {
            return 6;
          }
        }
        return 0;
      }
      paramBoolean = this.mInjector.userManagerIsSplitSystemUser();
      if (!paramBoolean)
      {
        if (paramInt != 0) {
          return 7;
        }
        paramBoolean = hasUserSetupCompleted(0);
        if (paramBoolean) {
          return 4;
        }
      }
      return 0;
    }
    finally {}
  }
  
  private void checkUserProvisioningStateTransition(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    }
    do
    {
      do
      {
        do
        {
          throw new IllegalStateException("Cannot move to user provisioning state [" + paramInt2 + "] " + "from state [" + paramInt1 + "]");
        } while (paramInt2 == 0);
        return;
      } while (paramInt2 != 3);
      return;
    } while (paramInt2 != 0);
  }
  
  private void cleanUpOldUsers()
  {
    try
    {
      Object localObject2 = this.mOwners.getProfileOwnerKeys();
      ArraySet localArraySet2 = new ArraySet();
      int i = 0;
      while (i < this.mUserData.size())
      {
        localArraySet2.add(Integer.valueOf(this.mUserData.keyAt(i)));
        i += 1;
      }
      List localList = this.mUserManager.getUsers();
      ArraySet localArraySet1 = new ArraySet();
      localArraySet1.addAll((Collection)localObject2);
      localArraySet1.addAll(localArraySet2);
      localObject2 = localList.iterator();
      while (((Iterator)localObject2).hasNext()) {
        localArraySet1.remove(Integer.valueOf(((UserInfo)((Iterator)localObject2).next()).id));
      }
      localIterator = ((Iterable)localObject1).iterator();
    }
    finally {}
    Iterator localIterator;
    while (localIterator.hasNext()) {
      removeUserData(((Integer)localIterator.next()).intValue());
    }
  }
  
  private void clearDeviceOwnerLocked(ActiveAdmin paramActiveAdmin, int paramInt)
  {
    if (paramActiveAdmin != null)
    {
      paramActiveAdmin.disableCamera = false;
      paramActiveAdmin.userRestrictions = null;
      paramActiveAdmin.forceEphemeralUsers = false;
      this.mUserManagerInternal.setForceEphemeralUsers(paramActiveAdmin.forceEphemeralUsers);
    }
    clearUserPoliciesLocked(paramInt);
    this.mOwners.clearDeviceOwner();
    this.mOwners.writeDeviceOwner();
    updateDeviceOwnerLocked();
    disableDeviceOwnerManagedSingleUserFeaturesIfNeeded();
    try
    {
      this.mInjector.getIBackupManager().setBackupServiceActive(0, true);
      return;
    }
    catch (RemoteException paramActiveAdmin)
    {
      throw new IllegalStateException("Failed reactivating backup service.", paramActiveAdmin);
    }
  }
  
  private void clearUserPoliciesLocked(int paramInt)
  {
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    localDevicePolicyData.mPermissionPolicy = 0;
    localDevicePolicyData.mDelegatedCertInstallerPackage = null;
    localDevicePolicyData.mApplicationRestrictionsManagingPackage = null;
    localDevicePolicyData.mStatusBarDisabled = false;
    localDevicePolicyData.mUserProvisioningState = 0;
    saveSettingsLocked(paramInt);
    try
    {
      this.mIPackageManager.updatePermissionFlagsForAllApps(4, 0, paramInt);
      pushUserRestrictions(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void clearWipeProfileNotification()
  {
    this.mInjector.getNotificationManager().cancel(1001);
  }
  
  private void disableDeviceOwnerManagedSingleUserFeaturesIfNeeded()
  {
    try
    {
      if (!isDeviceOwnerManagedSingleUserDevice())
      {
        this.mInjector.securityLogSetLoggingEnabledProperty(false);
        Slog.w("DevicePolicyManagerService", "Security logging turned off as it's no longer a single user device.");
        if (this.mOwners.hasDeviceOwner())
        {
          setBackupServiceEnabledInternal(false);
          Slog.w("DevicePolicyManagerService", "Backup is off as it's a managed device that has more that one user.");
        }
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void enableIfNecessary(String paramString, int paramInt)
  {
    try
    {
      if (this.mIPackageManager.getApplicationInfo(paramString, 32768, paramInt).enabledSetting == 4) {
        this.mIPackageManager.setApplicationEnabledSetting(paramString, 0, 1, paramInt, "DevicePolicyManager");
      }
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  private void enforceCanManageApplicationRestrictions(ComponentName paramComponentName)
  {
    if (paramComponentName != null) {}
    while (isCallerApplicationRestrictionsManagingPackage()) {
      try
      {
        getActiveAdminForCallerLocked(paramComponentName, -1);
        return;
      }
      finally
      {
        paramComponentName = finally;
        throw paramComponentName;
      }
    }
    throw new SecurityException("No admin component given, and caller cannot manage application restrictions for other apps.");
  }
  
  private void enforceCanManageDeviceAdmin()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS", null);
  }
  
  private void enforceCanManageInstalledKeys(ComponentName paramComponentName)
  {
    if (paramComponentName == null)
    {
      if (isCallerDelegatedCertInstaller()) {
        break label33;
      }
      throw new SecurityException("who == null, but caller is not cert installer");
    }
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      label33:
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  private void enforceCanManageProfileAndDeviceOwners()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS", null);
  }
  
  private void enforceCanSetDeviceOwnerLocked(ComponentName paramComponentName, int paramInt)
  {
    boolean bool2 = true;
    int i = this.mInjector.binderGetCallingUid();
    boolean bool1 = bool2;
    if (i != 2000) {
      if (i != 0) {
        break label124;
      }
    }
    label124:
    for (bool1 = bool2;; bool1 = false)
    {
      if (!bool1) {
        enforceCanManageProfileAndDeviceOwners();
      }
      i = checkSetDeviceOwnerPreConditionLocked(paramComponentName, paramInt, bool1);
      switch (i)
      {
      default: 
        throw new IllegalStateException("Unknown @DeviceOwnerPreConditionCode " + i);
      }
    }
    return;
    throw new IllegalStateException("Trying to set the device owner, but device owner is already set.");
    throw new IllegalStateException("Trying to set the device owner, but the user already has a profile owner.");
    throw new IllegalStateException("User not running: " + paramInt);
    throw new IllegalStateException("User is not system user");
    throw new IllegalStateException("Cannot set the device owner if the device is already set-up");
    throw new IllegalStateException("Not allowed to set the device owner because there are already several users on the device");
    throw new IllegalStateException("Not allowed to set the device owner because there are already some accounts on the device");
  }
  
  private void enforceCanSetProfileOwnerLocked(ComponentName paramComponentName, int paramInt)
  {
    UserInfo localUserInfo = getUserInfo(paramInt);
    if (localUserInfo == null) {
      throw new IllegalArgumentException("Attempted to set profile owner for invalid userId: " + paramInt);
    }
    if (localUserInfo.isGuest()) {
      throw new IllegalStateException("Cannot set a profile owner on a guest");
    }
    if (this.mOwners.hasProfileOwner(paramInt)) {
      throw new IllegalStateException("Trying to set the profile owner, but profile owner is already set.");
    }
    if ((this.mOwners.hasDeviceOwner()) && (this.mOwners.getDeviceOwnerUserId() == paramInt)) {
      throw new IllegalStateException("Trying to set the profile owner, but the user already has a device owner.");
    }
    int i = this.mInjector.binderGetCallingUid();
    if ((i == 2000) || (i == 0))
    {
      if ((hasUserSetupCompleted(paramInt)) && (hasIncompatibleAccountsLocked(paramInt, paramComponentName))) {
        throw new IllegalStateException("Not allowed to set the profile owner because there are already some accounts on the profile");
      }
      return;
    }
    enforceCanManageProfileAndDeviceOwners();
    if ((!hasUserSetupCompleted(paramInt)) || (isCallerWithSystemUid())) {
      return;
    }
    throw new IllegalStateException("Cannot set the profile owner on a user which is already set-up");
  }
  
  private void enforceCrossUsersPermission(int paramInt)
  {
    enforceSystemUserOrPermission(paramInt, "android.permission.INTERACT_ACROSS_USERS");
  }
  
  private void enforceFullCrossUsersPermission(int paramInt)
  {
    enforceSystemUserOrPermission(paramInt, "android.permission.INTERACT_ACROSS_USERS_FULL");
  }
  
  private void enforceManageUsers()
  {
    int j = 1;
    int k = this.mInjector.binderGetCallingUid();
    int i = j;
    if (!isCallerWithSystemUid()) {
      if (k != 0) {
        break label41;
      }
    }
    label41:
    for (i = j;; i = 0)
    {
      if (i == 0) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USERS", null);
      }
      return;
    }
  }
  
  private void enforceManagedProfile(int paramInt, String paramString)
  {
    if (!isManagedProfile(paramInt)) {
      throw new SecurityException("You can not " + paramString + " outside a managed profile.");
    }
  }
  
  private void enforceNotManagedProfile(int paramInt, String paramString)
  {
    if (isManagedProfile(paramInt)) {
      throw new SecurityException("You can not " + paramString + " for a managed profile.");
    }
  }
  
  private void enforceShell(String paramString)
  {
    int i = Binder.getCallingUid();
    if ((i != 2000) && (i != 0)) {
      throw new SecurityException("Non-shell user attempted to call " + paramString);
    }
  }
  
  private void enforceSystemUserOrPermission(int paramInt, String paramString)
  {
    int i = 1;
    if (paramInt < 0) {
      throw new IllegalArgumentException("Invalid userId " + paramInt);
    }
    int j = this.mInjector.binderGetCallingUid();
    if (paramInt == UserHandle.getUserId(j)) {
      return;
    }
    paramInt = i;
    if (!isCallerWithSystemUid()) {
      if (j != 0) {
        break label108;
      }
    }
    label108:
    for (paramInt = i;; paramInt = 0)
    {
      if (paramInt == 0) {
        this.mContext.enforceCallingOrSelfPermission(paramString, "Must be system or have " + paramString + " permission");
      }
      return;
    }
  }
  
  private void enforceUserUnlocked(int paramInt)
  {
    Preconditions.checkState(this.mUserManager.isUserUnlocked(paramInt), "User must be running and unlocked");
  }
  
  private void enforceUserUnlocked(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      enforceUserUnlocked(getProfileParentId(paramInt));
      return;
    }
    enforceUserUnlocked(paramInt);
  }
  
  private void ensureCallerPackage(String paramString)
  {
    boolean bool = false;
    if (paramString == null)
    {
      Preconditions.checkState(isCallerWithSystemUid(), "Only caller can omit package name");
      return;
    }
    int i = this.mInjector.binderGetCallingUid();
    int j = this.mInjector.userHandleGetCallingUserId();
    try
    {
      if (this.mIPackageManager.getApplicationInfo(paramString, 0, j).uid == i) {
        bool = true;
      }
      Preconditions.checkState(bool, "Unmatching package name");
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  private void ensureDeviceOwnerManagingSingleUser(ComponentName paramComponentName)
    throws SecurityException
  {
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      if (!isDeviceOwnerManagedSingleUserDevice()) {
        throw new SecurityException("There should only be one user, managed by Device Owner");
      }
    }
    finally {}
  }
  
  private void ensureDeviceOwnerUserStarted()
  {
    try
    {
      boolean bool = this.mOwners.hasDeviceOwner();
      if (!bool) {
        return;
      }
      int i = this.mOwners.getDeviceOwnerUserId();
      if (i != 0) {}
      return;
    }
    finally
    {
      try
      {
        this.mInjector.getIActivityManager().startUserInBackground(i);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("DevicePolicyManagerService", "Exception starting user", localRemoteException);
      }
      localObject = finally;
    }
  }
  
  private ComponentName findAdminComponentWithPackageLocked(String paramString, int paramInt)
  {
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    int k = localDevicePolicyData.mAdminList.size();
    Object localObject1 = null;
    int i = 0;
    paramInt = 0;
    while (paramInt < k)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)localDevicePolicyData.mAdminList.get(paramInt);
      Object localObject2 = localObject1;
      int j = i;
      if (paramString.equals(localActiveAdmin.info.getPackageName()))
      {
        if (i == 0) {
          localObject1 = localActiveAdmin.info.getComponent();
        }
        j = i + 1;
        localObject2 = localObject1;
      }
      paramInt += 1;
      localObject1 = localObject2;
      i = j;
    }
    if (i > 1) {
      Slog.w("DevicePolicyManagerService", "Multiple DA found; assume the first one is DO.");
    }
    return (ComponentName)localObject1;
  }
  
  private void findOwnerComponentIfNecessaryLocked()
  {
    if (!this.mOwners.hasDeviceOwner()) {
      return;
    }
    ComponentName localComponentName = this.mOwners.getDeviceOwnerComponent();
    if (!TextUtils.isEmpty(localComponentName.getClassName())) {
      return;
    }
    localComponentName = findAdminComponentWithPackageLocked(localComponentName.getPackageName(), this.mOwners.getDeviceOwnerUserId());
    if (localComponentName == null)
    {
      Slog.e("DevicePolicyManagerService", "Device-owner isn't registered as device-admin");
      return;
    }
    Owners localOwners = this.mOwners;
    String str = this.mOwners.getDeviceOwnerName();
    int i = this.mOwners.getDeviceOwnerUserId();
    if (this.mOwners.getDeviceOwnerUserRestrictionsNeedsMigration()) {}
    for (boolean bool = false;; bool = true)
    {
      localOwners.setDeviceOwnerWithRestrictionsMigrated(localComponentName, str, i, bool);
      this.mOwners.writeDeviceOwner();
      return;
    }
  }
  
  private AccessibilityManager getAccessibilityManagerForUser(int paramInt)
  {
    Object localObject = ServiceManager.getService("accessibility");
    if (localObject == null) {}
    for (localObject = null;; localObject = IAccessibilityManager.Stub.asInterface((IBinder)localObject)) {
      return new AccessibilityManager(this.mContext, (IAccessibilityManager)localObject, paramInt);
    }
  }
  
  private ActiveAdmin getActiveAdminForUidLocked(ComponentName paramComponentName, int paramInt)
  {
    ActiveAdmin localActiveAdmin = (ActiveAdmin)getUserData(UserHandle.getUserId(paramInt)).mAdminMap.get(paramComponentName);
    if (localActiveAdmin == null) {
      throw new SecurityException("No active admin " + paramComponentName);
    }
    if (localActiveAdmin.getUid() != paramInt) {
      throw new SecurityException("Admin " + paramComponentName + " is not owned by uid " + paramInt);
    }
    return localActiveAdmin;
  }
  
  private ActiveAdmin getActiveAdminWithPolicyForUidLocked(ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    int i = UserHandle.getUserId(paramInt2);
    Object localObject = getUserData(i);
    if (paramComponentName != null)
    {
      localObject = (ActiveAdmin)((DevicePolicyData)localObject).mAdminMap.get(paramComponentName);
      if (localObject == null) {
        throw new SecurityException("No active admin " + paramComponentName);
      }
      if (((ActiveAdmin)localObject).getUid() != paramInt2) {
        throw new SecurityException("Admin " + paramComponentName + " is not owned by uid " + paramInt2);
      }
      if (isActiveAdminWithPolicyForUserLocked((ActiveAdmin)localObject, paramInt1, i)) {
        return (ActiveAdmin)localObject;
      }
    }
    else
    {
      paramComponentName = ((DevicePolicyData)localObject).mAdminList.iterator();
      while (paramComponentName.hasNext())
      {
        localObject = (ActiveAdmin)paramComponentName.next();
        if ((((ActiveAdmin)localObject).getUid() == paramInt2) && (isActiveAdminWithPolicyForUserLocked((ActiveAdmin)localObject, paramInt1, i))) {
          return (ActiveAdmin)localObject;
        }
      }
    }
    return null;
  }
  
  private List<ActiveAdmin> getActiveAdminsForLockscreenPoliciesLocked(int paramInt, boolean paramBoolean)
  {
    if ((!paramBoolean) && (isSeparateProfileChallengeEnabled(paramInt))) {
      return getUserDataUnchecked(paramInt).mAdminList;
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mUserManager.getProfiles(paramInt).iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (UserInfo)localIterator.next();
      DevicePolicyData localDevicePolicyData = getUserData(((UserInfo)localObject).id);
      if (!((UserInfo)localObject).isManagedProfile())
      {
        localArrayList.addAll(localDevicePolicyData.mAdminList);
      }
      else
      {
        paramBoolean = isSeparateProfileChallengeEnabled(((UserInfo)localObject).id);
        int i = localDevicePolicyData.mAdminList.size();
        paramInt = 0;
        while (paramInt < i)
        {
          localObject = (ActiveAdmin)localDevicePolicyData.mAdminList.get(paramInt);
          if (((ActiveAdmin)localObject).hasParentActiveAdmin()) {
            localArrayList.add(((ActiveAdmin)localObject).getParentActiveAdmin());
          }
          if (!paramBoolean) {
            localArrayList.add(localObject);
          }
          paramInt += 1;
        }
      }
    }
    return localArrayList;
  }
  
  private ActiveAdmin getAdminWithMinimumFailedPasswordsForWipeLocked(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    Object localObject1 = null;
    List localList = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = localList.size();
    paramInt = 0;
    while (paramInt < k)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)localList.get(paramInt);
      Object localObject2;
      int j;
      if (localActiveAdmin.maximumFailedPasswordsForWipe == 0)
      {
        localObject2 = localObject1;
        j = i;
        paramInt += 1;
        i = j;
        localObject1 = localObject2;
      }
      else
      {
        int m = localActiveAdmin.getUserHandle().getIdentifier();
        if ((i == 0) || (i > localActiveAdmin.maximumFailedPasswordsForWipe)) {}
        for (;;)
        {
          j = localActiveAdmin.maximumFailedPasswordsForWipe;
          localObject2 = localActiveAdmin;
          break;
          j = i;
          localObject2 = localObject1;
          if (i != localActiveAdmin.maximumFailedPasswordsForWipe) {
            break;
          }
          j = i;
          localObject2 = localObject1;
          if (!getUserInfo(m).isPrimary()) {
            break;
          }
        }
      }
    }
    return (ActiveAdmin)localObject1;
  }
  
  private String getApplicationLabel(String paramString, int paramInt)
  {
    String str = null;
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      Object localObject = new UserHandle(paramInt);
      localObject = this.mContext.createPackageContextAsUser(paramString, 0, (UserHandle)localObject);
      ApplicationInfo localApplicationInfo = ((Context)localObject).getApplicationInfo();
      paramString = null;
      if (localApplicationInfo != null) {
        paramString = ((Context)localObject).getPackageManager().getApplicationLabel(localApplicationInfo);
      }
      if (paramString != null) {
        str = paramString.toString();
      }
      return str;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("DevicePolicyManagerService", paramString + " is not installed for user " + paramInt, localNameNotFoundException);
      return null;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private boolean getCameraDisabled(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    if (!this.mHasFeature) {
      return false;
    }
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      paramBoolean = bool;
      if (paramComponentName != null) {
        paramBoolean = paramComponentName.disableCamera;
      }
      return paramBoolean;
    }
    finally {}
    if (paramBoolean)
    {
      paramComponentName = getDeviceOwnerAdminLocked();
      if (paramComponentName != null)
      {
        paramBoolean = paramComponentName.disableCamera;
        if (paramBoolean) {
          return true;
        }
      }
    }
    paramComponentName = getUserData(paramInt);
    int i = paramComponentName.mAdminList.size();
    paramInt = 0;
    while (paramInt < i)
    {
      paramBoolean = ((ActiveAdmin)paramComponentName.mAdminList.get(paramInt)).disableCamera;
      if (paramBoolean) {
        return true;
      }
      paramInt += 1;
    }
    return false;
  }
  
  private int getCredentialOwner(int paramInt, boolean paramBoolean)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    int i = paramInt;
    if (paramBoolean) {}
    try
    {
      UserInfo localUserInfo = this.mUserManager.getProfileParent(paramInt);
      i = paramInt;
      if (localUserInfo != null) {
        i = localUserInfo.id;
      }
      paramInt = this.mUserManager.getCredentialOwnerProfile(i);
      return paramInt;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private String getDeviceOwnerRemoteBugreportUri()
  {
    try
    {
      String str = this.mOwners.getDeviceOwnerRemoteBugreportUri();
      return str;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private int getEncryptionStatus()
  {
    if (this.mInjector.storageManagerIsFileBasedEncryptionEnabled()) {
      return 5;
    }
    if (this.mInjector.storageManagerIsNonDefaultBlockEncrypted()) {
      return 3;
    }
    if (this.mInjector.storageManagerIsEncrypted()) {
      return 4;
    }
    if (this.mInjector.storageManagerIsEncryptable()) {
      return 1;
    }
    return 0;
  }
  
  private String getEncryptionStatusName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "unknown";
    case 1: 
      return "inactive";
    case 4: 
      return "block default key";
    case 3: 
      return "block";
    case 5: 
      return "per-user";
    case 0: 
      return "unsupported";
    }
    return "activating";
  }
  
  private List<String> getKeepUninstalledPackagesLocked()
  {
    List localList = null;
    ActiveAdmin localActiveAdmin = getDeviceOwnerAdminLocked();
    if (localActiveAdmin != null) {
      localList = localActiveAdmin.keepUninstalledPackages;
    }
    return localList;
  }
  
  private List<String> getLockTaskPackagesLocked(int paramInt)
  {
    return getUserData(paramInt).mLockTaskPackages;
  }
  
  private long getMaximumTimeToLockPolicyFromAdmins(List<ActiveAdmin> paramList)
  {
    long l2 = 0L;
    int j = paramList.size();
    int i = 0;
    if (i < j)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramList.get(i);
      long l1;
      if (l2 == 0L) {
        l1 = localActiveAdmin.maximumTimeToUnlock;
      }
      for (;;)
      {
        i += 1;
        l2 = l1;
        break;
        l1 = l2;
        if (localActiveAdmin.maximumTimeToUnlock != 0L)
        {
          l1 = l2;
          if (l2 > localActiveAdmin.maximumTimeToUnlock) {
            l1 = localActiveAdmin.maximumTimeToUnlock;
          }
        }
      }
    }
    return l2;
  }
  
  private long getPasswordExpirationLocked(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    long l1 = 0L;
    long l2 = 0L;
    if (paramComponentName != null)
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      if (paramComponentName != null) {
        l1 = paramComponentName.passwordExpirationDate;
      }
      return l1;
    }
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int i = paramComponentName.size();
    paramInt = 0;
    for (l1 = l2; paramInt < i; l1 = l2)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
      if (l1 != 0L)
      {
        l2 = l1;
        if (localActiveAdmin.passwordExpirationDate != 0L)
        {
          l2 = l1;
          if (l1 <= localActiveAdmin.passwordExpirationDate) {}
        }
      }
      else
      {
        l2 = localActiveAdmin.passwordExpirationDate;
      }
      paramInt += 1;
    }
    return l1;
  }
  
  private int getProfileParentId(int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      UserInfo localUserInfo = this.mUserManager.getProfileParent(paramInt);
      if (localUserInfo != null) {
        paramInt = localUserInfo.id;
      }
      return paramInt;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private int getTargetSdk(String paramString, int paramInt)
  {
    try
    {
      paramString = this.mIPackageManager.getApplicationInfo(paramString, 0, paramInt);
      if (paramString == null) {
        return 0;
      }
      paramInt = paramString.targetSdkVersion;
      return paramInt;
    }
    catch (RemoteException paramString) {}
    return 0;
  }
  
  private UserInfo getUserInfo(int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      UserInfo localUserInfo = this.mUserManager.getUserInfo(paramInt);
      return localUserInfo;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private int getUserProvisioningState(int paramInt)
  {
    return getUserData(paramInt).mUserProvisioningState;
  }
  
  private void handlePackagesChanged(String paramString, int paramInt)
  {
    int j = 0;
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    for (;;)
    {
      int m;
      ActiveAdmin localActiveAdmin;
      String str;
      try
      {
        m = localDevicePolicyData.mAdminList.size() - 1;
        if (m >= 0)
        {
          localActiveAdmin = (ActiveAdmin)localDevicePolicyData.mAdminList.get(m);
          i = j;
        }
      }
      finally {}
      try
      {
        str = localActiveAdmin.info.getPackageName();
        if (paramString != null)
        {
          k = j;
          i = j;
          if (!paramString.equals(str)) {}
        }
        else
        {
          i = j;
          if (this.mIPackageManager.getPackageInfo(str, 0, paramInt) != null)
          {
            k = j;
            i = j;
            if (this.mIPackageManager.getReceiverInfo(localActiveAdmin.info.getComponent(), 786432, paramInt) != null) {}
          }
          else
          {
            j = 1;
            k = 1;
            i = j;
            localDevicePolicyData.mAdminList.remove(m);
            i = j;
            localDevicePolicyData.mAdminMap.remove(localActiveAdmin.info.getComponent());
          }
        }
      }
      catch (RemoteException localRemoteException)
      {
        k = i;
        continue;
      }
      m -= 1;
      j = k;
    }
    if (j != 0)
    {
      validatePasswordOwnerLocked(localDevicePolicyData);
      saveSettingsLocked(localDevicePolicyData.mUserHandle);
    }
    if (isRemovedPackage(paramString, localDevicePolicyData.mDelegatedCertInstallerPackage, paramInt))
    {
      localDevicePolicyData.mDelegatedCertInstallerPackage = null;
      saveSettingsLocked(localDevicePolicyData.mUserHandle);
    }
    if (isRemovedPackage(paramString, localDevicePolicyData.mApplicationRestrictionsManagingPackage, paramInt))
    {
      localDevicePolicyData.mApplicationRestrictionsManagingPackage = null;
      saveSettingsLocked(localDevicePolicyData.mUserHandle);
    }
    if (j != 0) {
      pushUserRestrictions(paramInt);
    }
  }
  
  private void handlePasswordExpirationNotification(int paramInt)
  {
    for (;;)
    {
      int i;
      try
      {
        long l = System.currentTimeMillis();
        List localList = getActiveAdminsForLockscreenPoliciesLocked(paramInt, false);
        int j = localList.size();
        i = 0;
        if (i < j)
        {
          ActiveAdmin localActiveAdmin = (ActiveAdmin)localList.get(i);
          if ((localActiveAdmin.info.usesPolicy(6)) && (localActiveAdmin.passwordExpirationTimeout > 0L) && (l >= localActiveAdmin.passwordExpirationDate - 432000000L) && (localActiveAdmin.passwordExpirationDate > 0L)) {
            sendAdminCommandLocked(localActiveAdmin, "android.app.action.ACTION_PASSWORD_EXPIRING");
          }
        }
        else
        {
          setExpirationAlarmCheckLocked(this.mContext, paramInt, false);
          return;
        }
      }
      finally {}
      i += 1;
    }
  }
  
  private boolean hasAccountFeatures(AccountManager paramAccountManager, Account paramAccount, String[] paramArrayOfString)
  {
    try
    {
      boolean bool = ((Boolean)paramAccountManager.hasFeatures(paramAccount, paramArrayOfString, null, null).getResult()).booleanValue();
      return bool;
    }
    catch (Exception paramAccountManager)
    {
      Log.w("DevicePolicyManagerService", "Failed to get account feature", paramAccountManager);
    }
    return false;
  }
  
  private boolean hasFeatureManagedUsers()
  {
    try
    {
      boolean bool = this.mIPackageManager.hasSystemFeature("android.software.managed_users", 0);
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  private boolean hasIncompatibleAccountsLocked(int paramInt, ComponentName paramComponentName)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    for (;;)
    {
      int j;
      label212:
      label223:
      boolean bool;
      try
      {
        AccountManager localAccountManager = AccountManager.get(this.mContext);
        Account[] arrayOfAccount = localAccountManager.getAccountsAsUser(paramInt);
        i = arrayOfAccount.length;
        if (i == 0) {
          return false;
        }
        String[] arrayOfString1 = new String[1];
        arrayOfString1[0] = "android.account.DEVICE_OR_PROFILE_OWNER_ALLOWED";
        String[] arrayOfString2 = new String[1];
        arrayOfString2[0] = "android.account.DEVICE_OR_PROFILE_OWNER_DISALLOWED";
        i = 1;
        j = 0;
        int k = arrayOfAccount.length;
        if (j < k)
        {
          Account localAccount = arrayOfAccount[j];
          if (hasAccountFeatures(localAccountManager, localAccount, arrayOfString2))
          {
            Log.e("DevicePolicyManagerService", localAccount + " has " + arrayOfString2[0]);
            i = 0;
          }
          if (!hasAccountFeatures(localAccountManager, localAccount, arrayOfString1))
          {
            Log.e("DevicePolicyManagerService", localAccount + " doesn't have " + arrayOfString1[0]);
            i = 0;
          }
        }
        else if (i != 0)
        {
          Log.w("DevicePolicyManagerService", "All accounts are compatible");
          break label390;
          if (i != 0)
          {
            Log.w("DevicePolicyManagerService", paramComponentName);
            if (i == 0) {
              break label375;
            }
            bool = false;
            label230:
            return bool;
          }
        }
        else
        {
          Log.e("DevicePolicyManagerService", "Found incompatible accounts");
        }
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      label375:
      label390:
      do
      {
        if (isAdminTestOnlyLocked(paramComponentName, paramInt))
        {
          if (i != 0)
          {
            paramComponentName = "Installing test-only owner " + paramComponentName;
            break label212;
          }
          paramComponentName = "Can't install test-only owner " + paramComponentName + " with incompatible accounts";
          break label212;
        }
        i = 0;
        paramComponentName = "Can't install non test-only owner " + paramComponentName + " with accounts";
        break label212;
        Log.e("DevicePolicyManagerService", paramComponentName);
        break label223;
        bool = true;
        break label230;
        j += 1;
        break;
      } while (paramComponentName != null);
      int i = 0;
      paramComponentName = "Only test-only device/profile owner can be installed with accounts";
    }
  }
  
  private boolean hasUserSetupCompleted(int paramInt)
  {
    if (!this.mHasFeature) {
      return true;
    }
    return getUserData(paramInt).mUserSetupComplete;
  }
  
  private boolean isActivePasswordSufficientForUserLocked(DevicePolicyData paramDevicePolicyData, int paramInt, boolean paramBoolean)
  {
    enforceUserUnlocked(paramInt, paramBoolean);
    int i = getPasswordQuality(null, paramInt, paramBoolean);
    if (paramDevicePolicyData.mActivePasswordQuality < i) {
      return false;
    }
    if ((i >= 131072) && (paramDevicePolicyData.mActivePasswordLength < getPasswordMinimumLength(null, paramInt, paramBoolean))) {
      return false;
    }
    if (i != 393216) {
      return true;
    }
    if ((paramDevicePolicyData.mActivePasswordUpperCase >= getPasswordMinimumUpperCase(null, paramInt, paramBoolean)) && (paramDevicePolicyData.mActivePasswordLowerCase >= getPasswordMinimumLowerCase(null, paramInt, paramBoolean)) && (paramDevicePolicyData.mActivePasswordLetters >= getPasswordMinimumLetters(null, paramInt, paramBoolean)) && (paramDevicePolicyData.mActivePasswordNumeric >= getPasswordMinimumNumeric(null, paramInt, paramBoolean)) && (paramDevicePolicyData.mActivePasswordSymbols >= getPasswordMinimumSymbols(null, paramInt, paramBoolean))) {
      return paramDevicePolicyData.mActivePasswordNonLetter >= getPasswordMinimumNonLetter(null, paramInt, paramBoolean);
    }
    return false;
  }
  
  private boolean isAdminTestOnlyLocked(ComponentName paramComponentName, int paramInt)
  {
    paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
    if (paramComponentName != null) {
      return paramComponentName.testOnlyAdmin;
    }
    return false;
  }
  
  /* Error */
  private boolean isCallerDelegatedCertInstaller()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   6: invokevirtual 887	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderGetCallingUid	()I
    //   9: istore_1
    //   10: iload_1
    //   11: invokestatic 964	android/os/UserHandle:getUserId	(I)I
    //   14: istore_2
    //   15: aload_0
    //   16: monitorenter
    //   17: aload_0
    //   18: iload_2
    //   19: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   22: astore 4
    //   24: aload 4
    //   26: getfield 801	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mDelegatedCertInstallerPackage	Ljava/lang/String;
    //   29: astore 5
    //   31: aload 5
    //   33: ifnonnull +7 -> 40
    //   36: aload_0
    //   37: monitorexit
    //   38: iconst_0
    //   39: ireturn
    //   40: aload_0
    //   41: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   44: invokevirtual 502	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   47: aload 4
    //   49: getfield 801	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mDelegatedCertInstallerPackage	Ljava/lang/String;
    //   52: iload_2
    //   53: invokevirtual 1425	android/content/pm/PackageManager:getPackageUidAsUser	(Ljava/lang/String;I)I
    //   56: istore_2
    //   57: iload_2
    //   58: iload_1
    //   59: if_icmpne +5 -> 64
    //   62: iconst_1
    //   63: istore_3
    //   64: aload_0
    //   65: monitorexit
    //   66: iload_3
    //   67: ireturn
    //   68: astore 4
    //   70: aload_0
    //   71: monitorexit
    //   72: iconst_0
    //   73: ireturn
    //   74: astore 4
    //   76: aload_0
    //   77: monitorexit
    //   78: aload 4
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	DevicePolicyManagerService
    //   9	51	1	i	int
    //   14	46	2	j	int
    //   1	66	3	bool	boolean
    //   22	26	4	localDevicePolicyData	DevicePolicyData
    //   68	1	4	localNameNotFoundException	PackageManager.NameNotFoundException
    //   74	5	4	localObject	Object
    //   29	3	5	str	String
    // Exception table:
    //   from	to	target	type
    //   40	57	68	android/content/pm/PackageManager$NameNotFoundException
    //   17	31	74	finally
    //   40	57	74	finally
  }
  
  private boolean isCallerWithSystemUid()
  {
    return UserHandle.isSameApp(this.mInjector.binderGetCallingUid(), 1000);
  }
  
  private boolean isCrossProfileQuickContactDisabled(int paramInt)
  {
    if (getCrossProfileCallerIdDisabledForUser(paramInt)) {
      return getCrossProfileContactsSearchDisabledForUser(paramInt);
    }
    return false;
  }
  
  /* Error */
  private boolean isDeviceOwnerManagedSingleUserDevice()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: iconst_1
    //   3: istore_2
    //   4: aload_0
    //   5: monitorenter
    //   6: aload_0
    //   7: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   10: invokevirtual 669	com/android/server/devicepolicy/Owners:hasDeviceOwner	()Z
    //   13: istore 4
    //   15: iload 4
    //   17: ifne +7 -> 24
    //   20: aload_0
    //   21: monitorexit
    //   22: iconst_0
    //   23: ireturn
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_0
    //   27: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   30: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   33: lstore 5
    //   35: aload_0
    //   36: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   39: invokevirtual 684	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:userManagerIsSplitSystemUser	()Z
    //   42: ifeq +54 -> 96
    //   45: aload_0
    //   46: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   49: invokevirtual 687	android/os/UserManager:getUserCount	()I
    //   52: iconst_2
    //   53: if_icmpne +38 -> 91
    //   56: aload_0
    //   57: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   60: invokevirtual 923	com/android/server/devicepolicy/Owners:getDeviceOwnerUserId	()I
    //   63: istore_1
    //   64: iload_1
    //   65: ifeq +21 -> 86
    //   68: aload_0
    //   69: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   72: lload 5
    //   74: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   77: iload_2
    //   78: ireturn
    //   79: astore 7
    //   81: aload_0
    //   82: monitorexit
    //   83: aload 7
    //   85: athrow
    //   86: iconst_0
    //   87: istore_2
    //   88: goto -20 -> 68
    //   91: iconst_0
    //   92: istore_2
    //   93: goto -25 -> 68
    //   96: aload_0
    //   97: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   100: invokevirtual 687	android/os/UserManager:getUserCount	()I
    //   103: istore_1
    //   104: iload_1
    //   105: iconst_1
    //   106: if_icmpne +16 -> 122
    //   109: iload_3
    //   110: istore_2
    //   111: aload_0
    //   112: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   115: lload 5
    //   117: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   120: iload_2
    //   121: ireturn
    //   122: iconst_0
    //   123: istore_2
    //   124: goto -13 -> 111
    //   127: astore 7
    //   129: aload_0
    //   130: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   133: lload 5
    //   135: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   138: aload 7
    //   140: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	141	0	this	DevicePolicyManagerService
    //   63	44	1	i	int
    //   3	121	2	bool1	boolean
    //   1	109	3	bool2	boolean
    //   13	3	4	bool3	boolean
    //   33	101	5	l	long
    //   79	5	7	localObject1	Object
    //   127	12	7	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   6	15	79	finally
    //   35	64	127	finally
    //   96	104	127	finally
  }
  
  private boolean isDeviceOwnerProvisioningAllowed(int paramInt)
  {
    boolean bool = false;
    try
    {
      paramInt = checkSetDeviceOwnerPreConditionLocked(null, paramInt, false);
      if (paramInt == 0) {
        bool = true;
      }
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private boolean isEncryptionSupported()
  {
    boolean bool = false;
    if (getEncryptionStatus() != 0) {
      bool = true;
    }
    return bool;
  }
  
  private static boolean isLimitPasswordAllowed(ActiveAdmin paramActiveAdmin, int paramInt)
  {
    if (paramActiveAdmin.passwordQuality < paramInt) {
      return false;
    }
    return paramActiveAdmin.info.usesPolicy(0);
  }
  
  private boolean isLockScreenSecureUnchecked(int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      boolean bool = this.mLockPatternUtils.isSecure(paramInt);
      return bool;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private boolean isManagedProfile(int paramInt)
  {
    return getUserInfo(paramInt).isManagedProfile();
  }
  
  private boolean isPackageTestOnly(String paramString, int paramInt)
  {
    boolean bool = false;
    ApplicationInfo localApplicationInfo;
    try
    {
      localApplicationInfo = this.mIPackageManager.getApplicationInfo(paramString, 786432, paramInt);
      if (localApplicationInfo == null) {
        throw new IllegalStateException("Couldn't find package: " + paramString + " on user " + paramInt);
      }
    }
    catch (RemoteException paramString)
    {
      throw new IllegalStateException(paramString);
    }
    if ((localApplicationInfo.flags & 0x100) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isRemovedPackage(String paramString1, String paramString2, int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramString2 != null) {
      if (paramString1 != null) {
        bool1 = bool2;
      }
    }
    try
    {
      if (paramString1.equals(paramString2))
      {
        paramString1 = this.mIPackageManager.getPackageInfo(paramString2, 0, paramInt);
        bool1 = bool2;
        if (paramString1 == null) {
          bool1 = true;
        }
      }
      return bool1;
    }
    catch (RemoteException paramString1) {}
    return false;
  }
  
  private boolean isSeparateProfileChallengeEnabled(int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      boolean bool = this.mLockPatternUtils.isSeparateProfileChallengeEnabled(paramInt);
      return bool;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private boolean isSystemApp(IPackageManager paramIPackageManager, String paramString, int paramInt)
    throws RemoteException
  {
    boolean bool = false;
    paramIPackageManager = paramIPackageManager.getApplicationInfo(paramString, 8192, paramInt);
    if (paramIPackageManager == null) {
      throw new IllegalArgumentException("The application " + paramString + " is not present on this device");
    }
    if ((paramIPackageManager.flags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  /* Error */
  private void loadSettingsLocked(DevicePolicyData paramDevicePolicyData, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_2
    //   2: invokespecial 1486	com/android/server/devicepolicy/DevicePolicyManagerService:makeJournaledFile	(I)Lcom/android/internal/util/JournaledFile;
    //   5: astore 9
    //   7: aconst_null
    //   8: astore 11
    //   10: aconst_null
    //   11: astore 10
    //   13: aload 9
    //   15: invokevirtual 1492	com/android/internal/util/JournaledFile:chooseForRead	()Ljava/io/File;
    //   18: astore 12
    //   20: iconst_0
    //   21: istore 4
    //   23: iconst_0
    //   24: istore 6
    //   26: iconst_0
    //   27: istore 7
    //   29: iconst_0
    //   30: istore 5
    //   32: iconst_0
    //   33: istore_3
    //   34: new 1494	java/io/FileInputStream
    //   37: dup
    //   38: aload 12
    //   40: invokespecial 1497	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   43: astore 9
    //   45: iload_3
    //   46: istore 4
    //   48: iload 7
    //   50: istore 5
    //   52: invokestatic 1503	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   55: astore 10
    //   57: iload_3
    //   58: istore 4
    //   60: iload 7
    //   62: istore 5
    //   64: aload 10
    //   66: aload 9
    //   68: getstatic 1509	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   71: invokevirtual 1513	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   74: invokeinterface 1519 3 0
    //   79: iload_3
    //   80: istore 4
    //   82: iload 7
    //   84: istore 5
    //   86: aload 10
    //   88: invokeinterface 1521 1 0
    //   93: istore 8
    //   95: iload 8
    //   97: iconst_1
    //   98: if_icmpeq +9 -> 107
    //   101: iload 8
    //   103: iconst_2
    //   104: if_icmpne -25 -> 79
    //   107: iload_3
    //   108: istore 4
    //   110: iload 7
    //   112: istore 5
    //   114: aload 10
    //   116: invokeinterface 1524 1 0
    //   121: astore 11
    //   123: iload_3
    //   124: istore 4
    //   126: iload 7
    //   128: istore 5
    //   130: ldc_w 1526
    //   133: aload 11
    //   135: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   138: ifne +115 -> 253
    //   141: iload_3
    //   142: istore 4
    //   144: iload 7
    //   146: istore 5
    //   148: new 1476	org/xmlpull/v1/XmlPullParserException
    //   151: dup
    //   152: new 697	java/lang/StringBuilder
    //   155: dup
    //   156: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   159: ldc_w 1528
    //   162: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: aload 11
    //   167: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   173: invokespecial 1529	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   176: athrow
    //   177: astore 10
    //   179: iload 4
    //   181: istore_3
    //   182: aload 9
    //   184: ifnull +8 -> 192
    //   187: aload 9
    //   189: invokevirtual 1532	java/io/FileInputStream:close	()V
    //   192: aload_1
    //   193: getfield 1009	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminList	Ljava/util/ArrayList;
    //   196: aload_1
    //   197: getfield 1090	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminMap	Landroid/util/ArrayMap;
    //   200: invokevirtual 1536	android/util/ArrayMap:values	()Ljava/util/Collection;
    //   203: invokevirtual 1124	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   206: pop
    //   207: iload_3
    //   208: ifeq +8 -> 216
    //   211: aload_0
    //   212: iload_2
    //   213: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   216: aload_0
    //   217: aload_1
    //   218: invokevirtual 1267	com/android/server/devicepolicy/DevicePolicyManagerService:validatePasswordOwnerLocked	(Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;)V
    //   221: aload_0
    //   222: iload_2
    //   223: invokevirtual 1539	com/android/server/devicepolicy/DevicePolicyManagerService:updateMaximumTimeToLockLocked	(I)V
    //   226: aload_0
    //   227: aload_1
    //   228: getfield 1228	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mLockTaskPackages	Ljava/util/List;
    //   231: iload_2
    //   232: invokespecial 1543	com/android/server/devicepolicy/DevicePolicyManagerService:updateLockTaskPackagesLocked	(Ljava/util/List;I)V
    //   235: aload_1
    //   236: getfield 807	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mStatusBarDisabled	Z
    //   239: ifeq +13 -> 252
    //   242: aload_0
    //   243: aload_1
    //   244: getfield 807	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mStatusBarDisabled	Z
    //   247: iload_2
    //   248: invokespecial 1547	com/android/server/devicepolicy/DevicePolicyManagerService:setStatusBarDisabledInternal	(ZI)Z
    //   251: pop
    //   252: return
    //   253: iload_3
    //   254: istore 4
    //   256: iload 7
    //   258: istore 5
    //   260: aload 10
    //   262: aconst_null
    //   263: ldc 80
    //   265: invokeinterface 1551 3 0
    //   270: astore 11
    //   272: aload 11
    //   274: ifnull +19 -> 293
    //   277: iload_3
    //   278: istore 4
    //   280: iload 7
    //   282: istore 5
    //   284: aload_1
    //   285: aload 11
    //   287: invokestatic 1555	android/content/ComponentName:unflattenFromString	(Ljava/lang/String;)Landroid/content/ComponentName;
    //   290: putfield 1559	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mRestrictionsProvider	Landroid/content/ComponentName;
    //   293: iload_3
    //   294: istore 4
    //   296: iload 7
    //   298: istore 5
    //   300: aload 10
    //   302: aconst_null
    //   303: ldc 86
    //   305: invokeinterface 1551 3 0
    //   310: astore 11
    //   312: aload 11
    //   314: ifnull +34 -> 348
    //   317: iload_3
    //   318: istore 4
    //   320: iload 7
    //   322: istore 5
    //   324: iconst_1
    //   325: invokestatic 1562	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   328: aload 11
    //   330: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   333: ifeq +15 -> 348
    //   336: iload_3
    //   337: istore 4
    //   339: iload 7
    //   341: istore 5
    //   343: aload_1
    //   344: iconst_1
    //   345: putfield 1365	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mUserSetupComplete	Z
    //   348: iload_3
    //   349: istore 4
    //   351: iload 7
    //   353: istore 5
    //   355: aload 10
    //   357: aconst_null
    //   358: ldc 68
    //   360: invokeinterface 1551 3 0
    //   365: astore 11
    //   367: aload 11
    //   369: ifnull +34 -> 403
    //   372: iload_3
    //   373: istore 4
    //   375: iload 7
    //   377: istore 5
    //   379: iconst_1
    //   380: invokestatic 1562	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   383: aload 11
    //   385: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   388: ifeq +15 -> 403
    //   391: iload_3
    //   392: istore 4
    //   394: iload 7
    //   396: istore 5
    //   398: aload_1
    //   399: iconst_1
    //   400: putfield 1565	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mDeviceProvisioningConfigApplied	Z
    //   403: iload_3
    //   404: istore 4
    //   406: iload 7
    //   408: istore 5
    //   410: aload 10
    //   412: aconst_null
    //   413: ldc 83
    //   415: invokeinterface 1551 3 0
    //   420: astore 11
    //   422: iload_3
    //   423: istore 4
    //   425: iload 7
    //   427: istore 5
    //   429: aload 11
    //   431: invokestatic 1049	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   434: ifne +19 -> 453
    //   437: iload_3
    //   438: istore 4
    //   440: iload 7
    //   442: istore 5
    //   444: aload_1
    //   445: aload 11
    //   447: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   450: putfield 810	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mUserProvisioningState	I
    //   453: iload_3
    //   454: istore 4
    //   456: iload 7
    //   458: istore 5
    //   460: aload 10
    //   462: aconst_null
    //   463: ldc 77
    //   465: invokeinterface 1551 3 0
    //   470: astore 11
    //   472: iload_3
    //   473: istore 4
    //   475: iload 7
    //   477: istore 5
    //   479: aload 11
    //   481: invokestatic 1049	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   484: ifne +19 -> 503
    //   487: iload_3
    //   488: istore 4
    //   490: iload 7
    //   492: istore 5
    //   494: aload_1
    //   495: aload 11
    //   497: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   500: putfield 798	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPermissionPolicy	I
    //   503: iload_3
    //   504: istore 4
    //   506: iload 7
    //   508: istore 5
    //   510: aload_1
    //   511: aload 10
    //   513: aconst_null
    //   514: ldc 65
    //   516: invokeinterface 1551 3 0
    //   521: putfield 801	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mDelegatedCertInstallerPackage	Ljava/lang/String;
    //   524: iload_3
    //   525: istore 4
    //   527: iload 7
    //   529: istore 5
    //   531: aload_1
    //   532: aload 10
    //   534: aconst_null
    //   535: ldc 62
    //   537: invokeinterface 1551 3 0
    //   542: putfield 804	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mApplicationRestrictionsManagingPackage	Ljava/lang/String;
    //   545: iload_3
    //   546: istore 4
    //   548: iload 7
    //   550: istore 5
    //   552: aload 10
    //   554: invokeinterface 1521 1 0
    //   559: pop
    //   560: iload_3
    //   561: istore 4
    //   563: iload 7
    //   565: istore 5
    //   567: aload 10
    //   569: invokeinterface 1572 1 0
    //   574: istore 8
    //   576: iload_3
    //   577: istore 4
    //   579: iload 7
    //   581: istore 5
    //   583: aload_1
    //   584: getfield 1228	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mLockTaskPackages	Ljava/util/List;
    //   587: invokeinterface 1575 1 0
    //   592: iload_3
    //   593: istore 4
    //   595: iload 7
    //   597: istore 5
    //   599: aload_1
    //   600: getfield 1009	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminList	Ljava/util/ArrayList;
    //   603: invokevirtual 1576	java/util/ArrayList:clear	()V
    //   606: iload_3
    //   607: istore 4
    //   609: iload 7
    //   611: istore 5
    //   613: aload_1
    //   614: getfield 1090	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminMap	Landroid/util/ArrayMap;
    //   617: invokevirtual 1577	android/util/ArrayMap:clear	()V
    //   620: iload_3
    //   621: istore 4
    //   623: iload 7
    //   625: istore 5
    //   627: aload_1
    //   628: getfield 1580	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAffiliationIds	Ljava/util/Set;
    //   631: invokeinterface 1581 1 0
    //   636: iload 6
    //   638: istore_3
    //   639: iload_3
    //   640: istore 4
    //   642: iload_3
    //   643: istore 5
    //   645: aload 10
    //   647: invokeinterface 1521 1 0
    //   652: istore 6
    //   654: iload 6
    //   656: iconst_1
    //   657: if_icmpeq +910 -> 1567
    //   660: iload 6
    //   662: iconst_3
    //   663: if_icmpne +21 -> 684
    //   666: iload_3
    //   667: istore 4
    //   669: iload_3
    //   670: istore 5
    //   672: aload 10
    //   674: invokeinterface 1572 1 0
    //   679: iload 8
    //   681: if_icmple +886 -> 1567
    //   684: iload 6
    //   686: iconst_3
    //   687: if_icmpeq -48 -> 639
    //   690: iload 6
    //   692: iconst_4
    //   693: if_icmpeq -54 -> 639
    //   696: iload_3
    //   697: istore 4
    //   699: iload_3
    //   700: istore 5
    //   702: aload 10
    //   704: invokeinterface 1524 1 0
    //   709: astore 11
    //   711: iload_3
    //   712: istore 4
    //   714: iload_3
    //   715: istore 5
    //   717: ldc_w 1583
    //   720: aload 11
    //   722: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   725: ifeq +180 -> 905
    //   728: iload_3
    //   729: istore 4
    //   731: iload_3
    //   732: istore 5
    //   734: aload 10
    //   736: aconst_null
    //   737: ldc 74
    //   739: invokeinterface 1551 3 0
    //   744: astore 11
    //   746: iload_3
    //   747: istore 4
    //   749: iload_3
    //   750: istore 5
    //   752: aload_0
    //   753: aload 11
    //   755: invokestatic 1555	android/content/ComponentName:unflattenFromString	(Ljava/lang/String;)Landroid/content/ComponentName;
    //   758: iload_2
    //   759: iconst_0
    //   760: invokevirtual 1587	com/android/server/devicepolicy/DevicePolicyManagerService:findAdmin	(Landroid/content/ComponentName;IZ)Landroid/app/admin/DeviceAdminInfo;
    //   763: astore 13
    //   765: aload 13
    //   767: ifnull -128 -> 639
    //   770: iload_3
    //   771: istore 4
    //   773: iload_3
    //   774: istore 5
    //   776: new 30	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin
    //   779: dup
    //   780: aload 13
    //   782: iconst_0
    //   783: invokespecial 1590	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:<init>	(Landroid/app/admin/DeviceAdminInfo;Z)V
    //   786: astore 13
    //   788: iload_3
    //   789: istore 4
    //   791: iload_3
    //   792: istore 5
    //   794: aload 13
    //   796: aload 10
    //   798: invokevirtual 1594	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:readFromXml	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   801: iload_3
    //   802: istore 4
    //   804: iload_3
    //   805: istore 5
    //   807: aload_1
    //   808: getfield 1090	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminMap	Landroid/util/ArrayMap;
    //   811: aload 13
    //   813: getfield 1020	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:info	Landroid/app/admin/DeviceAdminInfo;
    //   816: invokevirtual 1032	android/app/admin/DeviceAdminInfo:getComponent	()Landroid/content/ComponentName;
    //   819: aload 13
    //   821: invokevirtual 1598	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   824: pop
    //   825: goto -186 -> 639
    //   828: astore 13
    //   830: iload_3
    //   831: istore 4
    //   833: iload_3
    //   834: istore 5
    //   836: ldc 125
    //   838: new 697	java/lang/StringBuilder
    //   841: dup
    //   842: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   845: ldc_w 1600
    //   848: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   851: aload 11
    //   853: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   856: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   859: aload 13
    //   861: invokestatic 1003	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   864: pop
    //   865: goto -226 -> 639
    //   868: astore 10
    //   870: iload 5
    //   872: istore_3
    //   873: ldc 125
    //   875: new 697	java/lang/StringBuilder
    //   878: dup
    //   879: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   882: ldc_w 1602
    //   885: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   888: aload 12
    //   890: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   893: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   896: aload 10
    //   898: invokestatic 1003	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   901: pop
    //   902: goto -720 -> 182
    //   905: iload_3
    //   906: istore 4
    //   908: iload_3
    //   909: istore 5
    //   911: ldc_w 1604
    //   914: aload 11
    //   916: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   919: ifeq +29 -> 948
    //   922: iload_3
    //   923: istore 4
    //   925: iload_3
    //   926: istore 5
    //   928: aload_1
    //   929: aload 10
    //   931: aconst_null
    //   932: ldc 89
    //   934: invokeinterface 1551 3 0
    //   939: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   942: putfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   945: goto -306 -> 639
    //   948: iload_3
    //   949: istore 4
    //   951: iload_3
    //   952: istore 5
    //   954: ldc_w 1609
    //   957: aload 11
    //   959: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   962: ifeq +29 -> 991
    //   965: iload_3
    //   966: istore 4
    //   968: iload_3
    //   969: istore 5
    //   971: aload_1
    //   972: aload 10
    //   974: aconst_null
    //   975: ldc 89
    //   977: invokeinterface 1551 3 0
    //   982: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   985: putfield 1612	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPasswordOwner	I
    //   988: goto -349 -> 639
    //   991: iload_3
    //   992: istore 4
    //   994: iload_3
    //   995: istore 5
    //   997: ldc -102
    //   999: aload 11
    //   1001: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1004: ifeq +30 -> 1034
    //   1007: iload_3
    //   1008: istore 4
    //   1010: iload_3
    //   1011: istore 5
    //   1013: aload_1
    //   1014: getfield 1616	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAcceptedCaCertificates	Landroid/util/ArraySet;
    //   1017: aload 10
    //   1019: aconst_null
    //   1020: ldc 74
    //   1022: invokeinterface 1551 3 0
    //   1027: invokevirtual 1617	android/util/ArraySet:add	(Ljava/lang/Object;)Z
    //   1030: pop
    //   1031: goto -392 -> 639
    //   1034: iload_3
    //   1035: istore 4
    //   1037: iload_3
    //   1038: istore 5
    //   1040: ldc -90
    //   1042: aload 11
    //   1044: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1047: ifeq +32 -> 1079
    //   1050: iload_3
    //   1051: istore 4
    //   1053: iload_3
    //   1054: istore 5
    //   1056: aload_1
    //   1057: getfield 1228	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mLockTaskPackages	Ljava/util/List;
    //   1060: aload 10
    //   1062: aconst_null
    //   1063: ldc 74
    //   1065: invokeinterface 1551 3 0
    //   1070: invokeinterface 1618 2 0
    //   1075: pop
    //   1076: goto -437 -> 639
    //   1079: iload_3
    //   1080: istore 4
    //   1082: iload_3
    //   1083: istore 5
    //   1085: ldc -87
    //   1087: aload 11
    //   1089: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1092: ifeq +29 -> 1121
    //   1095: iload_3
    //   1096: istore 4
    //   1098: iload_3
    //   1099: istore 5
    //   1101: aload_1
    //   1102: aload 10
    //   1104: aconst_null
    //   1105: ldc 71
    //   1107: invokeinterface 1551 3 0
    //   1112: invokestatic 1621	java/lang/Boolean:parseBoolean	(Ljava/lang/String;)Z
    //   1115: putfield 807	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mStatusBarDisabled	Z
    //   1118: goto -479 -> 639
    //   1121: iload_3
    //   1122: istore 4
    //   1124: iload_3
    //   1125: istore 5
    //   1127: ldc 114
    //   1129: aload 11
    //   1131: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1134: ifeq +17 -> 1151
    //   1137: iload_3
    //   1138: istore 4
    //   1140: iload_3
    //   1141: istore 5
    //   1143: aload_1
    //   1144: iconst_1
    //   1145: putfield 1624	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:doNotAskCredentialsOnBoot	Z
    //   1148: goto -509 -> 639
    //   1151: iload_3
    //   1152: istore 4
    //   1154: iload_3
    //   1155: istore 5
    //   1157: ldc -96
    //   1159: aload 11
    //   1161: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1164: ifeq +33 -> 1197
    //   1167: iload_3
    //   1168: istore 4
    //   1170: iload_3
    //   1171: istore 5
    //   1173: aload_1
    //   1174: getfield 1580	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAffiliationIds	Ljava/util/Set;
    //   1177: aload 10
    //   1179: aconst_null
    //   1180: ldc_w 1625
    //   1183: invokeinterface 1551 3 0
    //   1188: invokeinterface 339 2 0
    //   1193: pop
    //   1194: goto -555 -> 639
    //   1197: iload_3
    //   1198: istore 4
    //   1200: iload_3
    //   1201: istore 5
    //   1203: ldc -99
    //   1205: aload 11
    //   1207: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1210: ifeq +43 -> 1253
    //   1213: iload_3
    //   1214: istore 4
    //   1216: iload_3
    //   1217: istore 5
    //   1219: aload 10
    //   1221: aconst_null
    //   1222: ldc 89
    //   1224: invokeinterface 1551 3 0
    //   1229: astore 11
    //   1231: iload_3
    //   1232: istore 4
    //   1234: iload_3
    //   1235: istore 5
    //   1237: aload_1
    //   1238: iconst_1
    //   1239: invokestatic 1562	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   1242: aload 11
    //   1244: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1247: putfield 1628	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminBroadcastPending	Z
    //   1250: goto -611 -> 639
    //   1253: iload_3
    //   1254: istore 4
    //   1256: iload_3
    //   1257: istore 5
    //   1259: ldc -93
    //   1261: aload 11
    //   1263: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1266: ifeq +21 -> 1287
    //   1269: iload_3
    //   1270: istore 4
    //   1272: iload_3
    //   1273: istore 5
    //   1275: aload_1
    //   1276: aload 10
    //   1278: invokestatic 1634	android/os/PersistableBundle:restoreFromXml	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/os/PersistableBundle;
    //   1281: putfield 1638	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mInitBundle	Landroid/os/PersistableBundle;
    //   1284: goto -645 -> 639
    //   1287: iload_3
    //   1288: istore 4
    //   1290: iload_3
    //   1291: istore 5
    //   1293: ldc_w 1640
    //   1296: aload 11
    //   1298: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1301: ifeq +219 -> 1520
    //   1304: iload_3
    //   1305: istore 4
    //   1307: iload_3
    //   1308: istore 5
    //   1310: aload_0
    //   1311: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   1314: invokevirtual 1193	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:storageManagerIsFileBasedEncryptionEnabled	()Z
    //   1317: ifeq +8 -> 1325
    //   1320: iconst_1
    //   1321: istore_3
    //   1322: goto -683 -> 639
    //   1325: iload_3
    //   1326: istore 4
    //   1328: iload_3
    //   1329: istore 5
    //   1331: aload_1
    //   1332: aload 10
    //   1334: aconst_null
    //   1335: ldc_w 1642
    //   1338: invokeinterface 1551 3 0
    //   1343: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1346: putfield 1375	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordQuality	I
    //   1349: iload_3
    //   1350: istore 4
    //   1352: iload_3
    //   1353: istore 5
    //   1355: aload_1
    //   1356: aload 10
    //   1358: aconst_null
    //   1359: ldc_w 1644
    //   1362: invokeinterface 1551 3 0
    //   1367: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1370: putfield 1379	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLength	I
    //   1373: iload_3
    //   1374: istore 4
    //   1376: iload_3
    //   1377: istore 5
    //   1379: aload_1
    //   1380: aload 10
    //   1382: aconst_null
    //   1383: ldc_w 1646
    //   1386: invokeinterface 1551 3 0
    //   1391: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1394: putfield 1386	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordUpperCase	I
    //   1397: iload_3
    //   1398: istore 4
    //   1400: iload_3
    //   1401: istore 5
    //   1403: aload_1
    //   1404: aload 10
    //   1406: aconst_null
    //   1407: ldc_w 1648
    //   1410: invokeinterface 1551 3 0
    //   1415: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1418: putfield 1392	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLowerCase	I
    //   1421: iload_3
    //   1422: istore 4
    //   1424: iload_3
    //   1425: istore 5
    //   1427: aload_1
    //   1428: aload 10
    //   1430: aconst_null
    //   1431: ldc_w 1650
    //   1434: invokeinterface 1551 3 0
    //   1439: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1442: putfield 1398	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLetters	I
    //   1445: iload_3
    //   1446: istore 4
    //   1448: iload_3
    //   1449: istore 5
    //   1451: aload_1
    //   1452: aload 10
    //   1454: aconst_null
    //   1455: ldc_w 1652
    //   1458: invokeinterface 1551 3 0
    //   1463: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1466: putfield 1404	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNumeric	I
    //   1469: iload_3
    //   1470: istore 4
    //   1472: iload_3
    //   1473: istore 5
    //   1475: aload_1
    //   1476: aload 10
    //   1478: aconst_null
    //   1479: ldc_w 1654
    //   1482: invokeinterface 1551 3 0
    //   1487: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1490: putfield 1410	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordSymbols	I
    //   1493: iload_3
    //   1494: istore 4
    //   1496: iload_3
    //   1497: istore 5
    //   1499: aload_1
    //   1500: aload 10
    //   1502: aconst_null
    //   1503: ldc_w 1656
    //   1506: invokeinterface 1551 3 0
    //   1511: invokestatic 1569	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1514: putfield 1416	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNonLetter	I
    //   1517: goto -878 -> 639
    //   1520: iload_3
    //   1521: istore 4
    //   1523: iload_3
    //   1524: istore 5
    //   1526: ldc 125
    //   1528: new 697	java/lang/StringBuilder
    //   1531: dup
    //   1532: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   1535: ldc_w 1658
    //   1538: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1541: aload 11
    //   1543: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1546: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1549: invokestatic 837	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1552: pop
    //   1553: iload_3
    //   1554: istore 4
    //   1556: iload_3
    //   1557: istore 5
    //   1559: aload 10
    //   1561: invokestatic 1663	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1564: goto -925 -> 639
    //   1567: goto -1385 -> 182
    //   1570: astore 9
    //   1572: goto -1380 -> 192
    //   1575: astore 9
    //   1577: iload 4
    //   1579: istore_3
    //   1580: aload 10
    //   1582: astore 9
    //   1584: goto -1402 -> 182
    //   1587: astore 10
    //   1589: iload 5
    //   1591: istore_3
    //   1592: aload 11
    //   1594: astore 9
    //   1596: goto -723 -> 873
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1599	0	this	DevicePolicyManagerService
    //   0	1599	1	paramDevicePolicyData	DevicePolicyData
    //   0	1599	2	paramInt	int
    //   33	1559	3	i	int
    //   21	1557	4	j	int
    //   30	1560	5	k	int
    //   24	670	6	m	int
    //   27	597	7	n	int
    //   93	589	8	i1	int
    //   5	183	9	localObject1	Object
    //   1570	1	9	localIOException	IOException
    //   1575	1	9	localFileNotFoundException1	java.io.FileNotFoundException
    //   1582	13	9	localObject2	Object
    //   11	104	10	localXmlPullParser	XmlPullParser
    //   177	620	10	localFileNotFoundException2	java.io.FileNotFoundException
    //   868	713	10	localNullPointerException1	NullPointerException
    //   1587	1	10	localNullPointerException2	NullPointerException
    //   8	1585	11	str	String
    //   18	871	12	localFile	File
    //   763	57	13	localObject3	Object
    //   828	32	13	localRuntimeException	RuntimeException
    // Exception table:
    //   from	to	target	type
    //   52	57	177	java/io/FileNotFoundException
    //   64	79	177	java/io/FileNotFoundException
    //   86	95	177	java/io/FileNotFoundException
    //   114	123	177	java/io/FileNotFoundException
    //   130	141	177	java/io/FileNotFoundException
    //   148	177	177	java/io/FileNotFoundException
    //   260	272	177	java/io/FileNotFoundException
    //   284	293	177	java/io/FileNotFoundException
    //   300	312	177	java/io/FileNotFoundException
    //   324	336	177	java/io/FileNotFoundException
    //   343	348	177	java/io/FileNotFoundException
    //   355	367	177	java/io/FileNotFoundException
    //   379	391	177	java/io/FileNotFoundException
    //   398	403	177	java/io/FileNotFoundException
    //   410	422	177	java/io/FileNotFoundException
    //   429	437	177	java/io/FileNotFoundException
    //   444	453	177	java/io/FileNotFoundException
    //   460	472	177	java/io/FileNotFoundException
    //   479	487	177	java/io/FileNotFoundException
    //   494	503	177	java/io/FileNotFoundException
    //   510	524	177	java/io/FileNotFoundException
    //   531	545	177	java/io/FileNotFoundException
    //   552	560	177	java/io/FileNotFoundException
    //   567	576	177	java/io/FileNotFoundException
    //   583	592	177	java/io/FileNotFoundException
    //   599	606	177	java/io/FileNotFoundException
    //   613	620	177	java/io/FileNotFoundException
    //   627	636	177	java/io/FileNotFoundException
    //   645	654	177	java/io/FileNotFoundException
    //   672	684	177	java/io/FileNotFoundException
    //   702	711	177	java/io/FileNotFoundException
    //   717	728	177	java/io/FileNotFoundException
    //   734	746	177	java/io/FileNotFoundException
    //   752	765	177	java/io/FileNotFoundException
    //   776	788	177	java/io/FileNotFoundException
    //   794	801	177	java/io/FileNotFoundException
    //   807	825	177	java/io/FileNotFoundException
    //   836	865	177	java/io/FileNotFoundException
    //   911	922	177	java/io/FileNotFoundException
    //   928	945	177	java/io/FileNotFoundException
    //   954	965	177	java/io/FileNotFoundException
    //   971	988	177	java/io/FileNotFoundException
    //   997	1007	177	java/io/FileNotFoundException
    //   1013	1031	177	java/io/FileNotFoundException
    //   1040	1050	177	java/io/FileNotFoundException
    //   1056	1076	177	java/io/FileNotFoundException
    //   1085	1095	177	java/io/FileNotFoundException
    //   1101	1118	177	java/io/FileNotFoundException
    //   1127	1137	177	java/io/FileNotFoundException
    //   1143	1148	177	java/io/FileNotFoundException
    //   1157	1167	177	java/io/FileNotFoundException
    //   1173	1194	177	java/io/FileNotFoundException
    //   1203	1213	177	java/io/FileNotFoundException
    //   1219	1231	177	java/io/FileNotFoundException
    //   1237	1250	177	java/io/FileNotFoundException
    //   1259	1269	177	java/io/FileNotFoundException
    //   1275	1284	177	java/io/FileNotFoundException
    //   1293	1304	177	java/io/FileNotFoundException
    //   1310	1320	177	java/io/FileNotFoundException
    //   1331	1349	177	java/io/FileNotFoundException
    //   1355	1373	177	java/io/FileNotFoundException
    //   1379	1397	177	java/io/FileNotFoundException
    //   1403	1421	177	java/io/FileNotFoundException
    //   1427	1445	177	java/io/FileNotFoundException
    //   1451	1469	177	java/io/FileNotFoundException
    //   1475	1493	177	java/io/FileNotFoundException
    //   1499	1517	177	java/io/FileNotFoundException
    //   1526	1553	177	java/io/FileNotFoundException
    //   1559	1564	177	java/io/FileNotFoundException
    //   752	765	828	java/lang/RuntimeException
    //   776	788	828	java/lang/RuntimeException
    //   794	801	828	java/lang/RuntimeException
    //   807	825	828	java/lang/RuntimeException
    //   52	57	868	java/lang/NullPointerException
    //   52	57	868	java/lang/NumberFormatException
    //   52	57	868	org/xmlpull/v1/XmlPullParserException
    //   52	57	868	java/io/IOException
    //   52	57	868	java/lang/IndexOutOfBoundsException
    //   64	79	868	java/lang/NullPointerException
    //   64	79	868	java/lang/NumberFormatException
    //   64	79	868	org/xmlpull/v1/XmlPullParserException
    //   64	79	868	java/io/IOException
    //   64	79	868	java/lang/IndexOutOfBoundsException
    //   86	95	868	java/lang/NullPointerException
    //   86	95	868	java/lang/NumberFormatException
    //   86	95	868	org/xmlpull/v1/XmlPullParserException
    //   86	95	868	java/io/IOException
    //   86	95	868	java/lang/IndexOutOfBoundsException
    //   114	123	868	java/lang/NullPointerException
    //   114	123	868	java/lang/NumberFormatException
    //   114	123	868	org/xmlpull/v1/XmlPullParserException
    //   114	123	868	java/io/IOException
    //   114	123	868	java/lang/IndexOutOfBoundsException
    //   130	141	868	java/lang/NullPointerException
    //   130	141	868	java/lang/NumberFormatException
    //   130	141	868	org/xmlpull/v1/XmlPullParserException
    //   130	141	868	java/io/IOException
    //   130	141	868	java/lang/IndexOutOfBoundsException
    //   148	177	868	java/lang/NullPointerException
    //   148	177	868	java/lang/NumberFormatException
    //   148	177	868	org/xmlpull/v1/XmlPullParserException
    //   148	177	868	java/io/IOException
    //   148	177	868	java/lang/IndexOutOfBoundsException
    //   260	272	868	java/lang/NullPointerException
    //   260	272	868	java/lang/NumberFormatException
    //   260	272	868	org/xmlpull/v1/XmlPullParserException
    //   260	272	868	java/io/IOException
    //   260	272	868	java/lang/IndexOutOfBoundsException
    //   284	293	868	java/lang/NullPointerException
    //   284	293	868	java/lang/NumberFormatException
    //   284	293	868	org/xmlpull/v1/XmlPullParserException
    //   284	293	868	java/io/IOException
    //   284	293	868	java/lang/IndexOutOfBoundsException
    //   300	312	868	java/lang/NullPointerException
    //   300	312	868	java/lang/NumberFormatException
    //   300	312	868	org/xmlpull/v1/XmlPullParserException
    //   300	312	868	java/io/IOException
    //   300	312	868	java/lang/IndexOutOfBoundsException
    //   324	336	868	java/lang/NullPointerException
    //   324	336	868	java/lang/NumberFormatException
    //   324	336	868	org/xmlpull/v1/XmlPullParserException
    //   324	336	868	java/io/IOException
    //   324	336	868	java/lang/IndexOutOfBoundsException
    //   343	348	868	java/lang/NullPointerException
    //   343	348	868	java/lang/NumberFormatException
    //   343	348	868	org/xmlpull/v1/XmlPullParserException
    //   343	348	868	java/io/IOException
    //   343	348	868	java/lang/IndexOutOfBoundsException
    //   355	367	868	java/lang/NullPointerException
    //   355	367	868	java/lang/NumberFormatException
    //   355	367	868	org/xmlpull/v1/XmlPullParserException
    //   355	367	868	java/io/IOException
    //   355	367	868	java/lang/IndexOutOfBoundsException
    //   379	391	868	java/lang/NullPointerException
    //   379	391	868	java/lang/NumberFormatException
    //   379	391	868	org/xmlpull/v1/XmlPullParserException
    //   379	391	868	java/io/IOException
    //   379	391	868	java/lang/IndexOutOfBoundsException
    //   398	403	868	java/lang/NullPointerException
    //   398	403	868	java/lang/NumberFormatException
    //   398	403	868	org/xmlpull/v1/XmlPullParserException
    //   398	403	868	java/io/IOException
    //   398	403	868	java/lang/IndexOutOfBoundsException
    //   410	422	868	java/lang/NullPointerException
    //   410	422	868	java/lang/NumberFormatException
    //   410	422	868	org/xmlpull/v1/XmlPullParserException
    //   410	422	868	java/io/IOException
    //   410	422	868	java/lang/IndexOutOfBoundsException
    //   429	437	868	java/lang/NullPointerException
    //   429	437	868	java/lang/NumberFormatException
    //   429	437	868	org/xmlpull/v1/XmlPullParserException
    //   429	437	868	java/io/IOException
    //   429	437	868	java/lang/IndexOutOfBoundsException
    //   444	453	868	java/lang/NullPointerException
    //   444	453	868	java/lang/NumberFormatException
    //   444	453	868	org/xmlpull/v1/XmlPullParserException
    //   444	453	868	java/io/IOException
    //   444	453	868	java/lang/IndexOutOfBoundsException
    //   460	472	868	java/lang/NullPointerException
    //   460	472	868	java/lang/NumberFormatException
    //   460	472	868	org/xmlpull/v1/XmlPullParserException
    //   460	472	868	java/io/IOException
    //   460	472	868	java/lang/IndexOutOfBoundsException
    //   479	487	868	java/lang/NullPointerException
    //   479	487	868	java/lang/NumberFormatException
    //   479	487	868	org/xmlpull/v1/XmlPullParserException
    //   479	487	868	java/io/IOException
    //   479	487	868	java/lang/IndexOutOfBoundsException
    //   494	503	868	java/lang/NullPointerException
    //   494	503	868	java/lang/NumberFormatException
    //   494	503	868	org/xmlpull/v1/XmlPullParserException
    //   494	503	868	java/io/IOException
    //   494	503	868	java/lang/IndexOutOfBoundsException
    //   510	524	868	java/lang/NullPointerException
    //   510	524	868	java/lang/NumberFormatException
    //   510	524	868	org/xmlpull/v1/XmlPullParserException
    //   510	524	868	java/io/IOException
    //   510	524	868	java/lang/IndexOutOfBoundsException
    //   531	545	868	java/lang/NullPointerException
    //   531	545	868	java/lang/NumberFormatException
    //   531	545	868	org/xmlpull/v1/XmlPullParserException
    //   531	545	868	java/io/IOException
    //   531	545	868	java/lang/IndexOutOfBoundsException
    //   552	560	868	java/lang/NullPointerException
    //   552	560	868	java/lang/NumberFormatException
    //   552	560	868	org/xmlpull/v1/XmlPullParserException
    //   552	560	868	java/io/IOException
    //   552	560	868	java/lang/IndexOutOfBoundsException
    //   567	576	868	java/lang/NullPointerException
    //   567	576	868	java/lang/NumberFormatException
    //   567	576	868	org/xmlpull/v1/XmlPullParserException
    //   567	576	868	java/io/IOException
    //   567	576	868	java/lang/IndexOutOfBoundsException
    //   583	592	868	java/lang/NullPointerException
    //   583	592	868	java/lang/NumberFormatException
    //   583	592	868	org/xmlpull/v1/XmlPullParserException
    //   583	592	868	java/io/IOException
    //   583	592	868	java/lang/IndexOutOfBoundsException
    //   599	606	868	java/lang/NullPointerException
    //   599	606	868	java/lang/NumberFormatException
    //   599	606	868	org/xmlpull/v1/XmlPullParserException
    //   599	606	868	java/io/IOException
    //   599	606	868	java/lang/IndexOutOfBoundsException
    //   613	620	868	java/lang/NullPointerException
    //   613	620	868	java/lang/NumberFormatException
    //   613	620	868	org/xmlpull/v1/XmlPullParserException
    //   613	620	868	java/io/IOException
    //   613	620	868	java/lang/IndexOutOfBoundsException
    //   627	636	868	java/lang/NullPointerException
    //   627	636	868	java/lang/NumberFormatException
    //   627	636	868	org/xmlpull/v1/XmlPullParserException
    //   627	636	868	java/io/IOException
    //   627	636	868	java/lang/IndexOutOfBoundsException
    //   645	654	868	java/lang/NullPointerException
    //   645	654	868	java/lang/NumberFormatException
    //   645	654	868	org/xmlpull/v1/XmlPullParserException
    //   645	654	868	java/io/IOException
    //   645	654	868	java/lang/IndexOutOfBoundsException
    //   672	684	868	java/lang/NullPointerException
    //   672	684	868	java/lang/NumberFormatException
    //   672	684	868	org/xmlpull/v1/XmlPullParserException
    //   672	684	868	java/io/IOException
    //   672	684	868	java/lang/IndexOutOfBoundsException
    //   702	711	868	java/lang/NullPointerException
    //   702	711	868	java/lang/NumberFormatException
    //   702	711	868	org/xmlpull/v1/XmlPullParserException
    //   702	711	868	java/io/IOException
    //   702	711	868	java/lang/IndexOutOfBoundsException
    //   717	728	868	java/lang/NullPointerException
    //   717	728	868	java/lang/NumberFormatException
    //   717	728	868	org/xmlpull/v1/XmlPullParserException
    //   717	728	868	java/io/IOException
    //   717	728	868	java/lang/IndexOutOfBoundsException
    //   734	746	868	java/lang/NullPointerException
    //   734	746	868	java/lang/NumberFormatException
    //   734	746	868	org/xmlpull/v1/XmlPullParserException
    //   734	746	868	java/io/IOException
    //   734	746	868	java/lang/IndexOutOfBoundsException
    //   752	765	868	java/lang/NullPointerException
    //   752	765	868	java/lang/NumberFormatException
    //   752	765	868	org/xmlpull/v1/XmlPullParserException
    //   752	765	868	java/io/IOException
    //   752	765	868	java/lang/IndexOutOfBoundsException
    //   776	788	868	java/lang/NullPointerException
    //   776	788	868	java/lang/NumberFormatException
    //   776	788	868	org/xmlpull/v1/XmlPullParserException
    //   776	788	868	java/io/IOException
    //   776	788	868	java/lang/IndexOutOfBoundsException
    //   794	801	868	java/lang/NullPointerException
    //   794	801	868	java/lang/NumberFormatException
    //   794	801	868	org/xmlpull/v1/XmlPullParserException
    //   794	801	868	java/io/IOException
    //   794	801	868	java/lang/IndexOutOfBoundsException
    //   807	825	868	java/lang/NullPointerException
    //   807	825	868	java/lang/NumberFormatException
    //   807	825	868	org/xmlpull/v1/XmlPullParserException
    //   807	825	868	java/io/IOException
    //   807	825	868	java/lang/IndexOutOfBoundsException
    //   836	865	868	java/lang/NullPointerException
    //   836	865	868	java/lang/NumberFormatException
    //   836	865	868	org/xmlpull/v1/XmlPullParserException
    //   836	865	868	java/io/IOException
    //   836	865	868	java/lang/IndexOutOfBoundsException
    //   911	922	868	java/lang/NullPointerException
    //   911	922	868	java/lang/NumberFormatException
    //   911	922	868	org/xmlpull/v1/XmlPullParserException
    //   911	922	868	java/io/IOException
    //   911	922	868	java/lang/IndexOutOfBoundsException
    //   928	945	868	java/lang/NullPointerException
    //   928	945	868	java/lang/NumberFormatException
    //   928	945	868	org/xmlpull/v1/XmlPullParserException
    //   928	945	868	java/io/IOException
    //   928	945	868	java/lang/IndexOutOfBoundsException
    //   954	965	868	java/lang/NullPointerException
    //   954	965	868	java/lang/NumberFormatException
    //   954	965	868	org/xmlpull/v1/XmlPullParserException
    //   954	965	868	java/io/IOException
    //   954	965	868	java/lang/IndexOutOfBoundsException
    //   971	988	868	java/lang/NullPointerException
    //   971	988	868	java/lang/NumberFormatException
    //   971	988	868	org/xmlpull/v1/XmlPullParserException
    //   971	988	868	java/io/IOException
    //   971	988	868	java/lang/IndexOutOfBoundsException
    //   997	1007	868	java/lang/NullPointerException
    //   997	1007	868	java/lang/NumberFormatException
    //   997	1007	868	org/xmlpull/v1/XmlPullParserException
    //   997	1007	868	java/io/IOException
    //   997	1007	868	java/lang/IndexOutOfBoundsException
    //   1013	1031	868	java/lang/NullPointerException
    //   1013	1031	868	java/lang/NumberFormatException
    //   1013	1031	868	org/xmlpull/v1/XmlPullParserException
    //   1013	1031	868	java/io/IOException
    //   1013	1031	868	java/lang/IndexOutOfBoundsException
    //   1040	1050	868	java/lang/NullPointerException
    //   1040	1050	868	java/lang/NumberFormatException
    //   1040	1050	868	org/xmlpull/v1/XmlPullParserException
    //   1040	1050	868	java/io/IOException
    //   1040	1050	868	java/lang/IndexOutOfBoundsException
    //   1056	1076	868	java/lang/NullPointerException
    //   1056	1076	868	java/lang/NumberFormatException
    //   1056	1076	868	org/xmlpull/v1/XmlPullParserException
    //   1056	1076	868	java/io/IOException
    //   1056	1076	868	java/lang/IndexOutOfBoundsException
    //   1085	1095	868	java/lang/NullPointerException
    //   1085	1095	868	java/lang/NumberFormatException
    //   1085	1095	868	org/xmlpull/v1/XmlPullParserException
    //   1085	1095	868	java/io/IOException
    //   1085	1095	868	java/lang/IndexOutOfBoundsException
    //   1101	1118	868	java/lang/NullPointerException
    //   1101	1118	868	java/lang/NumberFormatException
    //   1101	1118	868	org/xmlpull/v1/XmlPullParserException
    //   1101	1118	868	java/io/IOException
    //   1101	1118	868	java/lang/IndexOutOfBoundsException
    //   1127	1137	868	java/lang/NullPointerException
    //   1127	1137	868	java/lang/NumberFormatException
    //   1127	1137	868	org/xmlpull/v1/XmlPullParserException
    //   1127	1137	868	java/io/IOException
    //   1127	1137	868	java/lang/IndexOutOfBoundsException
    //   1143	1148	868	java/lang/NullPointerException
    //   1143	1148	868	java/lang/NumberFormatException
    //   1143	1148	868	org/xmlpull/v1/XmlPullParserException
    //   1143	1148	868	java/io/IOException
    //   1143	1148	868	java/lang/IndexOutOfBoundsException
    //   1157	1167	868	java/lang/NullPointerException
    //   1157	1167	868	java/lang/NumberFormatException
    //   1157	1167	868	org/xmlpull/v1/XmlPullParserException
    //   1157	1167	868	java/io/IOException
    //   1157	1167	868	java/lang/IndexOutOfBoundsException
    //   1173	1194	868	java/lang/NullPointerException
    //   1173	1194	868	java/lang/NumberFormatException
    //   1173	1194	868	org/xmlpull/v1/XmlPullParserException
    //   1173	1194	868	java/io/IOException
    //   1173	1194	868	java/lang/IndexOutOfBoundsException
    //   1203	1213	868	java/lang/NullPointerException
    //   1203	1213	868	java/lang/NumberFormatException
    //   1203	1213	868	org/xmlpull/v1/XmlPullParserException
    //   1203	1213	868	java/io/IOException
    //   1203	1213	868	java/lang/IndexOutOfBoundsException
    //   1219	1231	868	java/lang/NullPointerException
    //   1219	1231	868	java/lang/NumberFormatException
    //   1219	1231	868	org/xmlpull/v1/XmlPullParserException
    //   1219	1231	868	java/io/IOException
    //   1219	1231	868	java/lang/IndexOutOfBoundsException
    //   1237	1250	868	java/lang/NullPointerException
    //   1237	1250	868	java/lang/NumberFormatException
    //   1237	1250	868	org/xmlpull/v1/XmlPullParserException
    //   1237	1250	868	java/io/IOException
    //   1237	1250	868	java/lang/IndexOutOfBoundsException
    //   1259	1269	868	java/lang/NullPointerException
    //   1259	1269	868	java/lang/NumberFormatException
    //   1259	1269	868	org/xmlpull/v1/XmlPullParserException
    //   1259	1269	868	java/io/IOException
    //   1259	1269	868	java/lang/IndexOutOfBoundsException
    //   1275	1284	868	java/lang/NullPointerException
    //   1275	1284	868	java/lang/NumberFormatException
    //   1275	1284	868	org/xmlpull/v1/XmlPullParserException
    //   1275	1284	868	java/io/IOException
    //   1275	1284	868	java/lang/IndexOutOfBoundsException
    //   1293	1304	868	java/lang/NullPointerException
    //   1293	1304	868	java/lang/NumberFormatException
    //   1293	1304	868	org/xmlpull/v1/XmlPullParserException
    //   1293	1304	868	java/io/IOException
    //   1293	1304	868	java/lang/IndexOutOfBoundsException
    //   1310	1320	868	java/lang/NullPointerException
    //   1310	1320	868	java/lang/NumberFormatException
    //   1310	1320	868	org/xmlpull/v1/XmlPullParserException
    //   1310	1320	868	java/io/IOException
    //   1310	1320	868	java/lang/IndexOutOfBoundsException
    //   1331	1349	868	java/lang/NullPointerException
    //   1331	1349	868	java/lang/NumberFormatException
    //   1331	1349	868	org/xmlpull/v1/XmlPullParserException
    //   1331	1349	868	java/io/IOException
    //   1331	1349	868	java/lang/IndexOutOfBoundsException
    //   1355	1373	868	java/lang/NullPointerException
    //   1355	1373	868	java/lang/NumberFormatException
    //   1355	1373	868	org/xmlpull/v1/XmlPullParserException
    //   1355	1373	868	java/io/IOException
    //   1355	1373	868	java/lang/IndexOutOfBoundsException
    //   1379	1397	868	java/lang/NullPointerException
    //   1379	1397	868	java/lang/NumberFormatException
    //   1379	1397	868	org/xmlpull/v1/XmlPullParserException
    //   1379	1397	868	java/io/IOException
    //   1379	1397	868	java/lang/IndexOutOfBoundsException
    //   1403	1421	868	java/lang/NullPointerException
    //   1403	1421	868	java/lang/NumberFormatException
    //   1403	1421	868	org/xmlpull/v1/XmlPullParserException
    //   1403	1421	868	java/io/IOException
    //   1403	1421	868	java/lang/IndexOutOfBoundsException
    //   1427	1445	868	java/lang/NullPointerException
    //   1427	1445	868	java/lang/NumberFormatException
    //   1427	1445	868	org/xmlpull/v1/XmlPullParserException
    //   1427	1445	868	java/io/IOException
    //   1427	1445	868	java/lang/IndexOutOfBoundsException
    //   1451	1469	868	java/lang/NullPointerException
    //   1451	1469	868	java/lang/NumberFormatException
    //   1451	1469	868	org/xmlpull/v1/XmlPullParserException
    //   1451	1469	868	java/io/IOException
    //   1451	1469	868	java/lang/IndexOutOfBoundsException
    //   1475	1493	868	java/lang/NullPointerException
    //   1475	1493	868	java/lang/NumberFormatException
    //   1475	1493	868	org/xmlpull/v1/XmlPullParserException
    //   1475	1493	868	java/io/IOException
    //   1475	1493	868	java/lang/IndexOutOfBoundsException
    //   1499	1517	868	java/lang/NullPointerException
    //   1499	1517	868	java/lang/NumberFormatException
    //   1499	1517	868	org/xmlpull/v1/XmlPullParserException
    //   1499	1517	868	java/io/IOException
    //   1499	1517	868	java/lang/IndexOutOfBoundsException
    //   1526	1553	868	java/lang/NullPointerException
    //   1526	1553	868	java/lang/NumberFormatException
    //   1526	1553	868	org/xmlpull/v1/XmlPullParserException
    //   1526	1553	868	java/io/IOException
    //   1526	1553	868	java/lang/IndexOutOfBoundsException
    //   1559	1564	868	java/lang/NullPointerException
    //   1559	1564	868	java/lang/NumberFormatException
    //   1559	1564	868	org/xmlpull/v1/XmlPullParserException
    //   1559	1564	868	java/io/IOException
    //   1559	1564	868	java/lang/IndexOutOfBoundsException
    //   187	192	1570	java/io/IOException
    //   34	45	1575	java/io/FileNotFoundException
    //   34	45	1587	java/lang/NullPointerException
    //   34	45	1587	java/lang/NumberFormatException
    //   34	45	1587	org/xmlpull/v1/XmlPullParserException
    //   34	45	1587	java/io/IOException
    //   34	45	1587	java/lang/IndexOutOfBoundsException
  }
  
  private JournaledFile makeJournaledFile(int paramInt)
  {
    if (paramInt == 0) {}
    for (String str = this.mInjector.getDevicePolicyFilePathForSystemUser() + "device_policies.xml";; str = new File(this.mInjector.environmentGetUserSystemDirectory(paramInt), "device_policies.xml").getAbsolutePath()) {
      return new JournaledFile(new File(str), new File(str + ".tmp"));
    }
  }
  
  private void migrateUserRestrictionsForUser(UserHandle paramUserHandle, ActiveAdmin paramActiveAdmin, Set<String> paramSet, boolean paramBoolean)
  {
    Bundle localBundle1 = this.mUserManagerInternal.getBaseUserRestrictions(paramUserHandle.getIdentifier());
    Bundle localBundle2 = new Bundle();
    Bundle localBundle3 = new Bundle();
    Iterator localIterator = localBundle1.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (localBundle1.getBoolean(str))
      {
        if (paramBoolean) {}
        for (boolean bool = UserRestrictionsUtils.canDeviceOwnerChange(str);; bool = UserRestrictionsUtils.canProfileOwnerChange(str, paramUserHandle.getIdentifier()))
        {
          if ((bool) && ((paramSet == null) || (!paramSet.contains(str)))) {
            break label132;
          }
          localBundle2.putBoolean(str, true);
          break;
        }
        label132:
        localBundle3.putBoolean(str, true);
      }
    }
    this.mUserManagerInternal.setBaseUserRestrictionsByDpmsForMigration(paramUserHandle.getIdentifier(), localBundle2);
    if (paramActiveAdmin != null)
    {
      paramActiveAdmin.ensureUserRestrictions().clear();
      paramActiveAdmin.ensureUserRestrictions().putAll(localBundle3);
    }
    for (;;)
    {
      saveSettingsLocked(paramUserHandle.getIdentifier());
      return;
      Slog.w("DevicePolicyManagerService", "ActiveAdmin for DO/PO not found. user=" + paramUserHandle.getIdentifier());
    }
  }
  
  private void migrateUserRestrictionsIfNecessaryLocked()
  {
    Object localObject;
    if (this.mOwners.getDeviceOwnerUserRestrictionsNeedsMigration())
    {
      localObject = getDeviceOwnerAdminLocked();
      migrateUserRestrictionsForUser(UserHandle.SYSTEM, (ActiveAdmin)localObject, null, true);
      pushUserRestrictions(0);
      this.mOwners.setDeviceOwnerUserRestrictionsMigrated();
    }
    ArraySet localArraySet = Sets.newArraySet(new String[] { "no_outgoing_calls", "no_sms" });
    Iterator localIterator = this.mUserManager.getUsers().iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      int i = localUserInfo.id;
      if (this.mOwners.getProfileOwnerUserRestrictionsNeedsMigration(i))
      {
        ActiveAdmin localActiveAdmin = getProfileOwnerAdminLocked(i);
        if (i == 0) {}
        for (localObject = null;; localObject = localArraySet)
        {
          migrateUserRestrictionsForUser(localUserInfo.getUserHandle(), localActiveAdmin, (Set)localObject, false);
          pushUserRestrictions(i);
          this.mOwners.setProfileOwnerUserRestrictionsMigrated(i);
          break;
        }
      }
    }
  }
  
  private void onBugreportFailed()
  {
    this.mRemoteBugreportServiceIsActive.set(false);
    this.mInjector.systemPropertiesSet("ctl.stop", "bugreportremote");
    this.mRemoteBugreportSharingAccepted.set(false);
    setDeviceOwnerRemoteBugreportUriAndHash(null, null);
    this.mInjector.getNotificationManager().cancel("DevicePolicyManagerService", 678432343);
    Bundle localBundle = new Bundle();
    localBundle.putInt("android.app.extra.BUGREPORT_FAILURE_REASON", 0);
    sendDeviceOwnerCommand("android.app.action.BUGREPORT_FAILED", localBundle);
    this.mContext.unregisterReceiver(this.mRemoteBugreportConsentReceiver);
    this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
  }
  
  private void onBugreportFinished(Intent paramIntent)
  {
    this.mHandler.removeCallbacks(this.mRemoteBugreportTimeoutRunnable);
    this.mRemoteBugreportServiceIsActive.set(false);
    Uri localUri = paramIntent.getData();
    String str = null;
    if (localUri != null) {
      str = localUri.toString();
    }
    paramIntent = paramIntent.getStringExtra("android.intent.extra.REMOTE_BUGREPORT_HASH");
    if (this.mRemoteBugreportSharingAccepted.get())
    {
      shareBugreportWithDeviceOwnerIfExists(str, paramIntent);
      this.mInjector.getNotificationManager().cancel("DevicePolicyManagerService", 678432343);
    }
    for (;;)
    {
      this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
      return;
      setDeviceOwnerRemoteBugreportUriAndHash(str, paramIntent);
      this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManagerService", 678432343, RemoteBugreportUtils.buildNotification(this.mContext, 3), UserHandle.ALL);
    }
  }
  
  private void onBugreportSharingAccepted()
  {
    this.mRemoteBugreportSharingAccepted.set(true);
    do
    {
      try
      {
        String str1 = getDeviceOwnerRemoteBugreportUri();
        String str2 = this.mOwners.getDeviceOwnerRemoteBugreportHash();
        if (str1 != null)
        {
          shareBugreportWithDeviceOwnerIfExists(str1, str2);
          return;
        }
      }
      finally {}
    } while (!this.mRemoteBugreportServiceIsActive.get());
    this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManagerService", 678432343, RemoteBugreportUtils.buildNotification(this.mContext, 2), UserHandle.ALL);
  }
  
  private void onBugreportSharingDeclined()
  {
    if (this.mRemoteBugreportServiceIsActive.get())
    {
      this.mInjector.systemPropertiesSet("ctl.stop", "bugreportremote");
      this.mRemoteBugreportServiceIsActive.set(false);
      this.mHandler.removeCallbacks(this.mRemoteBugreportTimeoutRunnable);
      this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
    }
    this.mRemoteBugreportSharingAccepted.set(false);
    setDeviceOwnerRemoteBugreportUriAndHash(null, null);
    sendDeviceOwnerCommand("android.app.action.BUGREPORT_SHARING_DECLINED", null);
  }
  
  /* Error */
  private void onLockSettingsReady()
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   5: pop
    //   6: aload_0
    //   7: invokevirtual 1832	com/android/server/devicepolicy/DevicePolicyManagerService:loadOwners	()V
    //   10: aload_0
    //   11: invokespecial 1834	com/android/server/devicepolicy/DevicePolicyManagerService:cleanUpOldUsers	()V
    //   14: aload_0
    //   15: iconst_0
    //   16: invokespecial 247	com/android/server/devicepolicy/DevicePolicyManagerService:onStartUser	(I)V
    //   19: new 54	com/android/server/devicepolicy/DevicePolicyManagerService$SetupContentObserver
    //   22: dup
    //   23: aload_0
    //   24: aload_0
    //   25: getfield 444	com/android/server/devicepolicy/DevicePolicyManagerService:mHandler	Landroid/os/Handler;
    //   28: invokespecial 1837	com/android/server/devicepolicy/DevicePolicyManagerService$SetupContentObserver:<init>	(Lcom/android/server/devicepolicy/DevicePolicyManagerService;Landroid/os/Handler;)V
    //   31: invokevirtual 1840	com/android/server/devicepolicy/DevicePolicyManagerService$SetupContentObserver:register	()V
    //   34: aload_0
    //   35: invokevirtual 1843	com/android/server/devicepolicy/DevicePolicyManagerService:updateUserSetupComplete	()V
    //   38: aload_0
    //   39: monitorenter
    //   40: aload_0
    //   41: invokespecial 1845	com/android/server/devicepolicy/DevicePolicyManagerService:getKeepUninstalledPackagesLocked	()Ljava/util/List;
    //   44: astore_1
    //   45: aload_0
    //   46: monitorexit
    //   47: aload_1
    //   48: ifnull +14 -> 62
    //   51: aload_0
    //   52: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   55: invokevirtual 1849	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getPackageManagerInternal	()Landroid/content/pm/PackageManagerInternal;
    //   58: aload_1
    //   59: invokevirtual 1855	android/content/pm/PackageManagerInternal:setKeepUninstalledPackages	(Ljava/util/List;)V
    //   62: aload_0
    //   63: monitorenter
    //   64: aload_0
    //   65: invokevirtual 1180	com/android/server/devicepolicy/DevicePolicyManagerService:getDeviceOwnerAdminLocked	()Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   68: astore_1
    //   69: aload_1
    //   70: ifnull +14 -> 84
    //   73: aload_0
    //   74: getfield 468	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManagerInternal	Landroid/os/UserManagerInternal;
    //   77: aload_1
    //   78: getfield 760	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:forceEphemeralUsers	Z
    //   81: invokevirtual 764	android/os/UserManagerInternal:setForceEphemeralUsers	(Z)V
    //   84: aload_0
    //   85: monitorexit
    //   86: return
    //   87: astore_1
    //   88: aload_0
    //   89: monitorexit
    //   90: aload_1
    //   91: athrow
    //   92: astore_1
    //   93: aload_0
    //   94: monitorexit
    //   95: aload_1
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	DevicePolicyManagerService
    //   44	34	1	localObject1	Object
    //   87	4	1	localObject2	Object
    //   92	4	1	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   40	45	87	finally
    //   64	69	92	finally
    //   73	84	92	finally
  }
  
  private void onStartUser(int paramInt)
  {
    updateScreenCaptureDisabledInWindowManager(paramInt, getScreenCaptureDisabled(null, paramInt));
    pushUserRestrictions(paramInt);
  }
  
  private static X509Certificate parseCert(byte[] paramArrayOfByte)
    throws CertificateException
  {
    return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramArrayOfByte));
  }
  
  /* Error */
  private void pushUserRestrictions(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 1692	android/os/Bundle
    //   5: dup
    //   6: invokespecial 1693	android/os/Bundle:<init>	()V
    //   9: astore 4
    //   11: aload_0
    //   12: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   15: iload_1
    //   16: invokevirtual 1887	com/android/server/devicepolicy/Owners:isDeviceOwnerUserId	(I)Z
    //   19: ifeq +89 -> 108
    //   22: new 1692	android/os/Bundle
    //   25: dup
    //   26: invokespecial 1693	android/os/Bundle:<init>	()V
    //   29: astore_3
    //   30: aload_0
    //   31: invokevirtual 1180	com/android/server/devicepolicy/DevicePolicyManagerService:getDeviceOwnerAdminLocked	()Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   34: astore 5
    //   36: aload 5
    //   38: ifnonnull +6 -> 44
    //   41: aload_0
    //   42: monitorexit
    //   43: return
    //   44: aload 5
    //   46: getfield 757	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:userRestrictions	Landroid/os/Bundle;
    //   49: aload_3
    //   50: aload 4
    //   52: invokestatic 1891	com/android/server/pm/UserRestrictionsUtils:sortToGlobalAndLocal	(Landroid/os/Bundle;Landroid/os/Bundle;Landroid/os/Bundle;)V
    //   55: aload_3
    //   56: astore_2
    //   57: aload 5
    //   59: getfield 753	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:disableCamera	Z
    //   62: ifeq +13 -> 75
    //   65: aload_3
    //   66: ldc_w 1893
    //   69: iconst_1
    //   70: invokevirtual 1709	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   73: aload_3
    //   74: astore_2
    //   75: aload_0
    //   76: aconst_null
    //   77: iload_1
    //   78: iconst_0
    //   79: invokespecial 1895	com/android/server/devicepolicy/DevicePolicyManagerService:getCameraDisabled	(Landroid/content/ComponentName;IZ)Z
    //   82: ifeq +12 -> 94
    //   85: aload 4
    //   87: ldc_w 1893
    //   90: iconst_1
    //   91: invokevirtual 1709	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   94: aload_0
    //   95: getfield 468	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManagerInternal	Landroid/os/UserManagerInternal;
    //   98: iload_1
    //   99: aload 4
    //   101: aload_2
    //   102: invokevirtual 1899	android/os/UserManagerInternal:setDevicePolicyUserRestrictions	(ILandroid/os/Bundle;Landroid/os/Bundle;)V
    //   105: aload_0
    //   106: monitorexit
    //   107: return
    //   108: aconst_null
    //   109: astore_3
    //   110: aload_0
    //   111: iload_1
    //   112: invokevirtual 1754	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwnerAdminLocked	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   115: astore 5
    //   117: aload_3
    //   118: astore_2
    //   119: aload 5
    //   121: ifnull -46 -> 75
    //   124: aload 4
    //   126: aload 5
    //   128: getfield 757	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:userRestrictions	Landroid/os/Bundle;
    //   131: invokestatic 1903	com/android/server/pm/UserRestrictionsUtils:merge	(Landroid/os/Bundle;Landroid/os/Bundle;)V
    //   134: aload_3
    //   135: astore_2
    //   136: goto -61 -> 75
    //   139: astore_2
    //   140: aload_0
    //   141: monitorexit
    //   142: aload_2
    //   143: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	144	0	this	DevicePolicyManagerService
    //   0	144	1	paramInt	int
    //   56	80	2	localBundle1	Bundle
    //   139	4	2	localObject	Object
    //   29	106	3	localBundle2	Bundle
    //   9	116	4	localBundle3	Bundle
    //   34	93	5	localActiveAdmin	ActiveAdmin
    // Exception table:
    //   from	to	target	type
    //   2	36	139	finally
    //   44	55	139	finally
    //   57	73	139	finally
    //   75	94	139	finally
    //   94	105	139	finally
    //   110	117	139	finally
    //   124	134	139	finally
  }
  
  private void registerRemoteBugreportReceivers()
  {
    try
    {
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.REMOTE_BUGREPORT_DISPATCH", "application/vnd.android.bugreport");
      this.mContext.registerReceiver(this.mRemoteBugreportFinishedReceiver, localIntentFilter);
      localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("com.android.server.action.BUGREPORT_SHARING_DECLINED");
      localIntentFilter.addAction("com.android.server.action.BUGREPORT_SHARING_ACCEPTED");
      this.mContext.registerReceiver(this.mRemoteBugreportConsentReceiver, localIntentFilter);
      return;
    }
    catch (IntentFilter.MalformedMimeTypeException localMalformedMimeTypeException)
    {
      for (;;)
      {
        Slog.w("DevicePolicyManagerService", "Failed to set type application/vnd.android.bugreport", localMalformedMimeTypeException);
      }
    }
  }
  
  private void removeAdminArtifacts(ComponentName paramComponentName, int paramInt)
  {
    try
    {
      ActiveAdmin localActiveAdmin = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (localActiveAdmin == null) {
        return;
      }
      DevicePolicyData localDevicePolicyData = getUserData(paramInt);
      boolean bool = localActiveAdmin.info.usesPolicy(5);
      localDevicePolicyData.mAdminList.remove(localActiveAdmin);
      localDevicePolicyData.mAdminMap.remove(paramComponentName);
      validatePasswordOwnerLocked(localDevicePolicyData);
      if (bool) {
        resetGlobalProxyLocked(localDevicePolicyData);
      }
      saveSettingsLocked(paramInt);
      updateMaximumTimeToLockLocked(paramInt);
      localDevicePolicyData.mRemovingAdmins.remove(paramComponentName);
      Slog.i("DevicePolicyManagerService", "Device admin " + paramComponentName + " removed from user " + paramInt);
      pushUserRestrictions(paramInt);
      return;
    }
    finally {}
  }
  
  /* Error */
  private void removeCaApprovalsIfNeeded(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   4: iload_1
    //   5: invokevirtual 1123	android/os/UserManager:getProfiles	(I)Ljava/util/List;
    //   8: invokeinterface 630 1 0
    //   13: astore 4
    //   15: aload 4
    //   17: invokeinterface 635 1 0
    //   22: ifeq +127 -> 149
    //   25: aload 4
    //   27: invokeinterface 639 1 0
    //   32: checkcast 595	android/content/pm/UserInfo
    //   35: astore 5
    //   37: aload_0
    //   38: getfield 493	com/android/server/devicepolicy/DevicePolicyManagerService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   41: aload 5
    //   43: getfield 603	android/content/pm/UserInfo:id	I
    //   46: invokevirtual 1451	com/android/internal/widget/LockPatternUtils:isSecure	(I)Z
    //   49: istore_3
    //   50: iload_3
    //   51: istore_2
    //   52: aload 5
    //   54: invokevirtual 597	android/content/pm/UserInfo:isManagedProfile	()Z
    //   57: ifeq +22 -> 79
    //   60: iload_3
    //   61: aload_0
    //   62: getfield 493	com/android/server/devicepolicy/DevicePolicyManagerService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   65: aload_0
    //   66: aload 5
    //   68: getfield 603	android/content/pm/UserInfo:id	I
    //   71: invokespecial 287	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileParentId	(I)I
    //   74: invokevirtual 1451	com/android/internal/widget/LockPatternUtils:isSecure	(I)Z
    //   77: ior
    //   78: istore_2
    //   79: iload_2
    //   80: ifne -65 -> 15
    //   83: aload_0
    //   84: monitorenter
    //   85: aload_0
    //   86: aload 5
    //   88: getfield 603	android/content/pm/UserInfo:id	I
    //   91: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   94: getfield 1616	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAcceptedCaCertificates	Landroid/util/ArraySet;
    //   97: invokevirtual 1937	android/util/ArraySet:clear	()V
    //   100: aload_0
    //   101: aload 5
    //   103: getfield 603	android/content/pm/UserInfo:id	I
    //   106: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   109: aload_0
    //   110: monitorexit
    //   111: new 51	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask
    //   114: dup
    //   115: aload_0
    //   116: aconst_null
    //   117: invokespecial 1940	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:<init>	(Lcom/android/server/devicepolicy/DevicePolicyManagerService;Lcom/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask;)V
    //   120: iconst_1
    //   121: anewarray 731	java/lang/Integer
    //   124: dup
    //   125: iconst_0
    //   126: aload 5
    //   128: getfield 603	android/content/pm/UserInfo:id	I
    //   131: invokestatic 735	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   134: aastore
    //   135: invokevirtual 1944	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:execute	([Ljava/lang/Object;)Landroid/os/AsyncTask;
    //   138: pop
    //   139: goto -124 -> 15
    //   142: astore 4
    //   144: aload_0
    //   145: monitorexit
    //   146: aload 4
    //   148: athrow
    //   149: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	this	DevicePolicyManagerService
    //   0	150	1	paramInt	int
    //   51	29	2	bool1	boolean
    //   49	29	3	bool2	boolean
    //   13	13	4	localIterator	Iterator
    //   142	5	4	localObject	Object
    //   35	92	5	localUserInfo	UserInfo
    // Exception table:
    //   from	to	target	type
    //   85	109	142	finally
  }
  
  private void removePackageIfRequired(String paramString, int paramInt)
  {
    if (!packageHasActiveAdmins(paramString, paramInt)) {
      startUninstallIntent(paramString, paramInt);
    }
  }
  
  private void resetGlobalProxyLocked(DevicePolicyData paramDevicePolicyData)
  {
    int j = paramDevicePolicyData.mAdminList.size();
    int i = 0;
    while (i < j)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramDevicePolicyData.mAdminList.get(i);
      if (localActiveAdmin.specifiesGlobalProxy)
      {
        saveGlobalProxyLocked(localActiveAdmin.globalProxySpec, localActiveAdmin.globalProxyExclusionList);
        return;
      }
      i += 1;
    }
    saveGlobalProxyLocked(null, null);
  }
  
  private void saveGlobalProxyLocked(String paramString1, String paramString2)
  {
    Object localObject = paramString2;
    if (paramString2 == null) {
      localObject = "";
    }
    paramString2 = paramString1;
    if (paramString1 == null) {
      paramString2 = "";
    }
    paramString1 = paramString2.trim().split(":");
    int j = 8080;
    int i = j;
    if (paramString1.length > 1) {}
    try
    {
      i = Integer.parseInt(paramString1[1]);
      paramString2 = ((String)localObject).trim();
      localObject = new ProxyInfo(paramString1[0], i, paramString2);
      if (!((ProxyInfo)localObject).isValid())
      {
        Slog.e("DevicePolicyManagerService", "Invalid proxy properties, ignoring: " + ((ProxyInfo)localObject).toString());
        return;
      }
      this.mInjector.settingsGlobalPutString("global_http_proxy_host", paramString1[0]);
      this.mInjector.settingsGlobalPutInt("global_http_proxy_port", i);
      this.mInjector.settingsGlobalPutString("global_http_proxy_exclusion_list", paramString2);
      return;
    }
    catch (NumberFormatException paramString2)
    {
      for (;;)
      {
        i = j;
      }
    }
  }
  
  /* Error */
  private void saveSettingsLocked(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   5: astore 4
    //   7: aload_0
    //   8: iload_1
    //   9: invokespecial 1486	com/android/server/devicepolicy/DevicePolicyManagerService:makeJournaledFile	(I)Lcom/android/internal/util/JournaledFile;
    //   12: astore 7
    //   14: aconst_null
    //   15: astore 6
    //   17: new 1995	java/io/FileOutputStream
    //   20: dup
    //   21: aload 7
    //   23: invokevirtual 1998	com/android/internal/util/JournaledFile:chooseForWrite	()Ljava/io/File;
    //   26: iconst_0
    //   27: invokespecial 2001	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   30: astore 5
    //   32: new 2003	com/android/internal/util/FastXmlSerializer
    //   35: dup
    //   36: invokespecial 2004	com/android/internal/util/FastXmlSerializer:<init>	()V
    //   39: astore 6
    //   41: aload 6
    //   43: aload 5
    //   45: getstatic 1509	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   48: invokevirtual 1513	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   51: invokeinterface 2010 3 0
    //   56: aload 6
    //   58: aconst_null
    //   59: iconst_1
    //   60: invokestatic 2013	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   63: invokeinterface 2017 3 0
    //   68: aload 6
    //   70: aconst_null
    //   71: ldc_w 1526
    //   74: invokeinterface 2021 3 0
    //   79: pop
    //   80: aload 4
    //   82: getfield 1559	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mRestrictionsProvider	Landroid/content/ComponentName;
    //   85: ifnull +22 -> 107
    //   88: aload 6
    //   90: aconst_null
    //   91: ldc 80
    //   93: aload 4
    //   95: getfield 1559	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mRestrictionsProvider	Landroid/content/ComponentName;
    //   98: invokevirtual 2024	android/content/ComponentName:flattenToString	()Ljava/lang/String;
    //   101: invokeinterface 2028 4 0
    //   106: pop
    //   107: aload 4
    //   109: getfield 1365	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mUserSetupComplete	Z
    //   112: ifeq +18 -> 130
    //   115: aload 6
    //   117: aconst_null
    //   118: ldc 86
    //   120: iconst_1
    //   121: invokestatic 1562	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   124: invokeinterface 2028 4 0
    //   129: pop
    //   130: aload 4
    //   132: getfield 1565	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mDeviceProvisioningConfigApplied	Z
    //   135: ifeq +18 -> 153
    //   138: aload 6
    //   140: aconst_null
    //   141: ldc 68
    //   143: iconst_1
    //   144: invokestatic 1562	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   147: invokeinterface 2028 4 0
    //   152: pop
    //   153: aload 4
    //   155: getfield 810	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mUserProvisioningState	I
    //   158: ifeq +22 -> 180
    //   161: aload 6
    //   163: aconst_null
    //   164: ldc 83
    //   166: aload 4
    //   168: getfield 810	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mUserProvisioningState	I
    //   171: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   174: invokeinterface 2028 4 0
    //   179: pop
    //   180: aload 4
    //   182: getfield 798	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPermissionPolicy	I
    //   185: ifeq +22 -> 207
    //   188: aload 6
    //   190: aconst_null
    //   191: ldc 77
    //   193: aload 4
    //   195: getfield 798	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPermissionPolicy	I
    //   198: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   201: invokeinterface 2028 4 0
    //   206: pop
    //   207: aload 4
    //   209: getfield 801	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mDelegatedCertInstallerPackage	Ljava/lang/String;
    //   212: ifnull +19 -> 231
    //   215: aload 6
    //   217: aconst_null
    //   218: ldc 65
    //   220: aload 4
    //   222: getfield 801	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mDelegatedCertInstallerPackage	Ljava/lang/String;
    //   225: invokeinterface 2028 4 0
    //   230: pop
    //   231: aload 4
    //   233: getfield 804	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mApplicationRestrictionsManagingPackage	Ljava/lang/String;
    //   236: ifnull +19 -> 255
    //   239: aload 6
    //   241: aconst_null
    //   242: ldc 62
    //   244: aload 4
    //   246: getfield 804	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mApplicationRestrictionsManagingPackage	Ljava/lang/String;
    //   249: invokeinterface 2028 4 0
    //   254: pop
    //   255: aload 4
    //   257: getfield 1009	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminList	Ljava/util/ArrayList;
    //   260: invokevirtual 1012	java/util/ArrayList:size	()I
    //   263: istore_3
    //   264: iconst_0
    //   265: istore_2
    //   266: iload_2
    //   267: iload_3
    //   268: if_icmpge +78 -> 346
    //   271: aload 4
    //   273: getfield 1009	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminList	Ljava/util/ArrayList;
    //   276: iload_2
    //   277: invokevirtual 1016	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   280: checkcast 30	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin
    //   283: astore 8
    //   285: aload 8
    //   287: ifnull +891 -> 1178
    //   290: aload 6
    //   292: aconst_null
    //   293: ldc_w 1583
    //   296: invokeinterface 2021 3 0
    //   301: pop
    //   302: aload 6
    //   304: aconst_null
    //   305: ldc 74
    //   307: aload 8
    //   309: getfield 1020	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:info	Landroid/app/admin/DeviceAdminInfo;
    //   312: invokevirtual 1032	android/app/admin/DeviceAdminInfo:getComponent	()Landroid/content/ComponentName;
    //   315: invokevirtual 2024	android/content/ComponentName:flattenToString	()Ljava/lang/String;
    //   318: invokeinterface 2028 4 0
    //   323: pop
    //   324: aload 8
    //   326: aload 6
    //   328: invokevirtual 2034	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:writeToXml	(Lorg/xmlpull/v1/XmlSerializer;)V
    //   331: aload 6
    //   333: aconst_null
    //   334: ldc_w 1583
    //   337: invokeinterface 2037 3 0
    //   342: pop
    //   343: goto +835 -> 1178
    //   346: aload 4
    //   348: getfield 1612	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPasswordOwner	I
    //   351: iflt +46 -> 397
    //   354: aload 6
    //   356: aconst_null
    //   357: ldc_w 1609
    //   360: invokeinterface 2021 3 0
    //   365: pop
    //   366: aload 6
    //   368: aconst_null
    //   369: ldc 89
    //   371: aload 4
    //   373: getfield 1612	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPasswordOwner	I
    //   376: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   379: invokeinterface 2028 4 0
    //   384: pop
    //   385: aload 6
    //   387: aconst_null
    //   388: ldc_w 1609
    //   391: invokeinterface 2037 3 0
    //   396: pop
    //   397: aload 4
    //   399: getfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   402: ifeq +46 -> 448
    //   405: aload 6
    //   407: aconst_null
    //   408: ldc_w 1604
    //   411: invokeinterface 2021 3 0
    //   416: pop
    //   417: aload 6
    //   419: aconst_null
    //   420: ldc 89
    //   422: aload 4
    //   424: getfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   427: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   430: invokeinterface 2028 4 0
    //   435: pop
    //   436: aload 6
    //   438: aconst_null
    //   439: ldc_w 1604
    //   442: invokeinterface 2037 3 0
    //   447: pop
    //   448: aload_0
    //   449: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   452: invokevirtual 1193	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:storageManagerIsFileBasedEncryptionEnabled	()Z
    //   455: ifne +730 -> 1185
    //   458: aload 4
    //   460: getfield 1375	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordQuality	I
    //   463: ifne +11 -> 474
    //   466: aload 4
    //   468: getfield 1379	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLength	I
    //   471: ifeq +254 -> 725
    //   474: aload 6
    //   476: aconst_null
    //   477: ldc_w 1640
    //   480: invokeinterface 2021 3 0
    //   485: pop
    //   486: aload 6
    //   488: aconst_null
    //   489: ldc_w 1642
    //   492: aload 4
    //   494: getfield 1375	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordQuality	I
    //   497: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   500: invokeinterface 2028 4 0
    //   505: pop
    //   506: aload 6
    //   508: aconst_null
    //   509: ldc_w 1644
    //   512: aload 4
    //   514: getfield 1379	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLength	I
    //   517: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   520: invokeinterface 2028 4 0
    //   525: pop
    //   526: aload 6
    //   528: aconst_null
    //   529: ldc_w 1646
    //   532: aload 4
    //   534: getfield 1386	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordUpperCase	I
    //   537: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   540: invokeinterface 2028 4 0
    //   545: pop
    //   546: aload 6
    //   548: aconst_null
    //   549: ldc_w 1648
    //   552: aload 4
    //   554: getfield 1392	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLowerCase	I
    //   557: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   560: invokeinterface 2028 4 0
    //   565: pop
    //   566: aload 6
    //   568: aconst_null
    //   569: ldc_w 1650
    //   572: aload 4
    //   574: getfield 1398	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLetters	I
    //   577: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   580: invokeinterface 2028 4 0
    //   585: pop
    //   586: aload 6
    //   588: aconst_null
    //   589: ldc_w 1652
    //   592: aload 4
    //   594: getfield 1404	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNumeric	I
    //   597: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   600: invokeinterface 2028 4 0
    //   605: pop
    //   606: aload 6
    //   608: aconst_null
    //   609: ldc_w 1654
    //   612: aload 4
    //   614: getfield 1410	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordSymbols	I
    //   617: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   620: invokeinterface 2028 4 0
    //   625: pop
    //   626: aload 6
    //   628: aconst_null
    //   629: ldc_w 1656
    //   632: aload 4
    //   634: getfield 1416	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNonLetter	I
    //   637: invokestatic 2030	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   640: invokeinterface 2028 4 0
    //   645: pop
    //   646: aload 6
    //   648: aconst_null
    //   649: ldc_w 1640
    //   652: invokeinterface 2037 3 0
    //   657: pop
    //   658: goto +527 -> 1185
    //   661: iload_2
    //   662: aload 4
    //   664: getfield 1616	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAcceptedCaCertificates	Landroid/util/ArraySet;
    //   667: invokevirtual 2038	android/util/ArraySet:size	()I
    //   670: if_icmpge +520 -> 1190
    //   673: aload 6
    //   675: aconst_null
    //   676: ldc -102
    //   678: invokeinterface 2021 3 0
    //   683: pop
    //   684: aload 6
    //   686: aconst_null
    //   687: ldc 74
    //   689: aload 4
    //   691: getfield 1616	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAcceptedCaCertificates	Landroid/util/ArraySet;
    //   694: iload_2
    //   695: invokevirtual 2041	android/util/ArraySet:valueAt	(I)Ljava/lang/Object;
    //   698: checkcast 641	java/lang/String
    //   701: invokeinterface 2028 4 0
    //   706: pop
    //   707: aload 6
    //   709: aconst_null
    //   710: ldc -102
    //   712: invokeinterface 2037 3 0
    //   717: pop
    //   718: iload_2
    //   719: iconst_1
    //   720: iadd
    //   721: istore_2
    //   722: goto -61 -> 661
    //   725: aload 4
    //   727: getfield 1386	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordUpperCase	I
    //   730: ifne -256 -> 474
    //   733: aload 4
    //   735: getfield 1392	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLowerCase	I
    //   738: ifne -264 -> 474
    //   741: aload 4
    //   743: getfield 1398	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLetters	I
    //   746: ifne -272 -> 474
    //   749: aload 4
    //   751: getfield 1404	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNumeric	I
    //   754: ifne -280 -> 474
    //   757: aload 4
    //   759: getfield 1410	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordSymbols	I
    //   762: ifne -288 -> 474
    //   765: aload 4
    //   767: getfield 1416	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNonLetter	I
    //   770: ifeq +415 -> 1185
    //   773: goto -299 -> 474
    //   776: iload_2
    //   777: aload 4
    //   779: getfield 1228	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mLockTaskPackages	Ljava/util/List;
    //   782: invokeinterface 1138 1 0
    //   787: if_icmpge +61 -> 848
    //   790: aload 4
    //   792: getfield 1228	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mLockTaskPackages	Ljava/util/List;
    //   795: iload_2
    //   796: invokeinterface 1139 2 0
    //   801: checkcast 641	java/lang/String
    //   804: astore 8
    //   806: aload 6
    //   808: aconst_null
    //   809: ldc -90
    //   811: invokeinterface 2021 3 0
    //   816: pop
    //   817: aload 6
    //   819: aconst_null
    //   820: ldc 74
    //   822: aload 8
    //   824: invokeinterface 2028 4 0
    //   829: pop
    //   830: aload 6
    //   832: aconst_null
    //   833: ldc -90
    //   835: invokeinterface 2037 3 0
    //   840: pop
    //   841: iload_2
    //   842: iconst_1
    //   843: iadd
    //   844: istore_2
    //   845: goto -69 -> 776
    //   848: aload 4
    //   850: getfield 807	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mStatusBarDisabled	Z
    //   853: ifeq +44 -> 897
    //   856: aload 6
    //   858: aconst_null
    //   859: ldc -87
    //   861: invokeinterface 2021 3 0
    //   866: pop
    //   867: aload 6
    //   869: aconst_null
    //   870: ldc 71
    //   872: aload 4
    //   874: getfield 807	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mStatusBarDisabled	Z
    //   877: invokestatic 1562	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   880: invokeinterface 2028 4 0
    //   885: pop
    //   886: aload 6
    //   888: aconst_null
    //   889: ldc -87
    //   891: invokeinterface 2037 3 0
    //   896: pop
    //   897: aload 4
    //   899: getfield 1624	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:doNotAskCredentialsOnBoot	Z
    //   902: ifeq +25 -> 927
    //   905: aload 6
    //   907: aconst_null
    //   908: ldc 114
    //   910: invokeinterface 2021 3 0
    //   915: pop
    //   916: aload 6
    //   918: aconst_null
    //   919: ldc 114
    //   921: invokeinterface 2037 3 0
    //   926: pop
    //   927: aload 4
    //   929: getfield 1580	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAffiliationIds	Ljava/util/Set;
    //   932: invokeinterface 630 1 0
    //   937: astore 8
    //   939: aload 8
    //   941: invokeinterface 635 1 0
    //   946: ifeq +83 -> 1029
    //   949: aload 8
    //   951: invokeinterface 639 1 0
    //   956: checkcast 641	java/lang/String
    //   959: astore 9
    //   961: aload 6
    //   963: aconst_null
    //   964: ldc -96
    //   966: invokeinterface 2021 3 0
    //   971: pop
    //   972: aload 6
    //   974: aconst_null
    //   975: ldc_w 1625
    //   978: aload 9
    //   980: invokeinterface 2028 4 0
    //   985: pop
    //   986: aload 6
    //   988: aconst_null
    //   989: ldc -96
    //   991: invokeinterface 2037 3 0
    //   996: pop
    //   997: goto -58 -> 939
    //   1000: astore 4
    //   1002: ldc 125
    //   1004: ldc_w 2043
    //   1007: aload 4
    //   1009: invokestatic 1003	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   1012: pop
    //   1013: aload 5
    //   1015: ifnull +8 -> 1023
    //   1018: aload 5
    //   1020: invokevirtual 2044	java/io/FileOutputStream:close	()V
    //   1023: aload 7
    //   1025: invokevirtual 2047	com/android/internal/util/JournaledFile:rollback	()V
    //   1028: return
    //   1029: aload 4
    //   1031: getfield 1628	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminBroadcastPending	Z
    //   1034: ifeq +44 -> 1078
    //   1037: aload 6
    //   1039: aconst_null
    //   1040: ldc -99
    //   1042: invokeinterface 2021 3 0
    //   1047: pop
    //   1048: aload 6
    //   1050: aconst_null
    //   1051: ldc 89
    //   1053: aload 4
    //   1055: getfield 1628	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminBroadcastPending	Z
    //   1058: invokestatic 1562	java/lang/Boolean:toString	(Z)Ljava/lang/String;
    //   1061: invokeinterface 2028 4 0
    //   1066: pop
    //   1067: aload 6
    //   1069: aconst_null
    //   1070: ldc -99
    //   1072: invokeinterface 2037 3 0
    //   1077: pop
    //   1078: aload 4
    //   1080: getfield 1638	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mInitBundle	Landroid/os/PersistableBundle;
    //   1083: ifnull +35 -> 1118
    //   1086: aload 6
    //   1088: aconst_null
    //   1089: ldc -93
    //   1091: invokeinterface 2021 3 0
    //   1096: pop
    //   1097: aload 4
    //   1099: getfield 1638	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mInitBundle	Landroid/os/PersistableBundle;
    //   1102: aload 6
    //   1104: invokevirtual 2050	android/os/PersistableBundle:saveToXml	(Lorg/xmlpull/v1/XmlSerializer;)V
    //   1107: aload 6
    //   1109: aconst_null
    //   1110: ldc -93
    //   1112: invokeinterface 2037 3 0
    //   1117: pop
    //   1118: aload 6
    //   1120: aconst_null
    //   1121: ldc_w 1526
    //   1124: invokeinterface 2037 3 0
    //   1129: pop
    //   1130: aload 6
    //   1132: invokeinterface 2053 1 0
    //   1137: aload 5
    //   1139: invokevirtual 2056	java/io/FileOutputStream:flush	()V
    //   1142: aload 5
    //   1144: invokestatic 2062	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
    //   1147: pop
    //   1148: aload 5
    //   1150: invokevirtual 2044	java/io/FileOutputStream:close	()V
    //   1153: aload 7
    //   1155: invokevirtual 2065	com/android/internal/util/JournaledFile:commit	()V
    //   1158: aload_0
    //   1159: iload_1
    //   1160: invokespecial 2068	com/android/server/devicepolicy/DevicePolicyManagerService:sendChangedNotification	(I)V
    //   1163: return
    //   1164: astore 4
    //   1166: goto -143 -> 1023
    //   1169: astore 4
    //   1171: aload 6
    //   1173: astore 5
    //   1175: goto -173 -> 1002
    //   1178: iload_2
    //   1179: iconst_1
    //   1180: iadd
    //   1181: istore_2
    //   1182: goto -916 -> 266
    //   1185: iconst_0
    //   1186: istore_2
    //   1187: goto -526 -> 661
    //   1190: iconst_0
    //   1191: istore_2
    //   1192: goto -416 -> 776
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1195	0	this	DevicePolicyManagerService
    //   0	1195	1	paramInt	int
    //   265	927	2	i	int
    //   263	6	3	j	int
    //   5	923	4	localDevicePolicyData	DevicePolicyData
    //   1000	98	4	localXmlPullParserException1	XmlPullParserException
    //   1164	1	4	localIOException	IOException
    //   1169	1	4	localXmlPullParserException2	XmlPullParserException
    //   30	1144	5	localObject1	Object
    //   15	1157	6	localFastXmlSerializer	com.android.internal.util.FastXmlSerializer
    //   12	1142	7	localJournaledFile	JournaledFile
    //   283	667	8	localObject2	Object
    //   959	20	9	str	String
    // Exception table:
    //   from	to	target	type
    //   32	107	1000	org/xmlpull/v1/XmlPullParserException
    //   32	107	1000	java/io/IOException
    //   107	130	1000	org/xmlpull/v1/XmlPullParserException
    //   107	130	1000	java/io/IOException
    //   130	153	1000	org/xmlpull/v1/XmlPullParserException
    //   130	153	1000	java/io/IOException
    //   153	180	1000	org/xmlpull/v1/XmlPullParserException
    //   153	180	1000	java/io/IOException
    //   180	207	1000	org/xmlpull/v1/XmlPullParserException
    //   180	207	1000	java/io/IOException
    //   207	231	1000	org/xmlpull/v1/XmlPullParserException
    //   207	231	1000	java/io/IOException
    //   231	255	1000	org/xmlpull/v1/XmlPullParserException
    //   231	255	1000	java/io/IOException
    //   255	264	1000	org/xmlpull/v1/XmlPullParserException
    //   255	264	1000	java/io/IOException
    //   271	285	1000	org/xmlpull/v1/XmlPullParserException
    //   271	285	1000	java/io/IOException
    //   290	343	1000	org/xmlpull/v1/XmlPullParserException
    //   290	343	1000	java/io/IOException
    //   346	397	1000	org/xmlpull/v1/XmlPullParserException
    //   346	397	1000	java/io/IOException
    //   397	448	1000	org/xmlpull/v1/XmlPullParserException
    //   397	448	1000	java/io/IOException
    //   448	474	1000	org/xmlpull/v1/XmlPullParserException
    //   448	474	1000	java/io/IOException
    //   474	658	1000	org/xmlpull/v1/XmlPullParserException
    //   474	658	1000	java/io/IOException
    //   661	718	1000	org/xmlpull/v1/XmlPullParserException
    //   661	718	1000	java/io/IOException
    //   725	773	1000	org/xmlpull/v1/XmlPullParserException
    //   725	773	1000	java/io/IOException
    //   776	841	1000	org/xmlpull/v1/XmlPullParserException
    //   776	841	1000	java/io/IOException
    //   848	897	1000	org/xmlpull/v1/XmlPullParserException
    //   848	897	1000	java/io/IOException
    //   897	927	1000	org/xmlpull/v1/XmlPullParserException
    //   897	927	1000	java/io/IOException
    //   927	939	1000	org/xmlpull/v1/XmlPullParserException
    //   927	939	1000	java/io/IOException
    //   939	997	1000	org/xmlpull/v1/XmlPullParserException
    //   939	997	1000	java/io/IOException
    //   1029	1078	1000	org/xmlpull/v1/XmlPullParserException
    //   1029	1078	1000	java/io/IOException
    //   1078	1118	1000	org/xmlpull/v1/XmlPullParserException
    //   1078	1118	1000	java/io/IOException
    //   1118	1163	1000	org/xmlpull/v1/XmlPullParserException
    //   1118	1163	1000	java/io/IOException
    //   1018	1023	1164	java/io/IOException
    //   17	32	1169	org/xmlpull/v1/XmlPullParserException
    //   17	32	1169	java/io/IOException
  }
  
  private void sendAdminCommandForLockscreenPoliciesLocked(String paramString, int paramInt1, int paramInt2)
  {
    if (isSeparateProfileChallengeEnabled(paramInt2))
    {
      sendAdminCommandLocked(paramString, paramInt1, paramInt2);
      return;
    }
    sendAdminCommandToSelfAndProfilesLocked(paramString, paramInt1, paramInt2);
  }
  
  private void sendAdminCommandToSelfAndProfilesLocked(String paramString, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = this.mUserManager.getProfileIdsWithDisabled(paramInt2);
    paramInt2 = 0;
    int i = arrayOfInt.length;
    while (paramInt2 < i)
    {
      sendAdminCommandLocked(paramString, paramInt1, arrayOfInt[paramInt2]);
      paramInt2 += 1;
    }
  }
  
  private void sendAdminEnabledBroadcastLocked(int paramInt)
  {
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    ActiveAdmin localActiveAdmin;
    if (localDevicePolicyData.mAdminBroadcastPending)
    {
      localActiveAdmin = getProfileOwnerAdminLocked(paramInt);
      if (localActiveAdmin != null)
      {
        localObject = localDevicePolicyData.mInitBundle;
        if (localObject != null) {
          break label63;
        }
      }
    }
    label63:
    for (Object localObject = null;; localObject = new Bundle((PersistableBundle)localObject))
    {
      sendAdminCommandLocked(localActiveAdmin, "android.app.action.DEVICE_ADMIN_ENABLED", (Bundle)localObject, null);
      localDevicePolicyData.mInitBundle = null;
      localDevicePolicyData.mAdminBroadcastPending = false;
      saveSettingsLocked(paramInt);
      return;
    }
  }
  
  private void sendChangedNotification(int paramInt)
  {
    Intent localIntent = new Intent("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
    localIntent.setFlags(1073741824);
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      this.mContext.sendBroadcastAsUser(localIntent, new UserHandle(paramInt));
      return;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private void sendPrivateKeyAliasResponse(final String paramString, IBinder paramIBinder)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        try
        {
          this.val$keyChainAliasResponse.alias(paramString);
          return null;
        }
        catch (Exception paramAnonymousVarArgs)
        {
          for (;;)
          {
            Log.e("DevicePolicyManagerService", "error while responding to callback", paramAnonymousVarArgs);
          }
        }
      }
    }.execute(new Void[0]);
  }
  
  private void sendWipeProfileNotification()
  {
    Object localObject = this.mContext.getString(17039646);
    localObject = new Notification.Builder(this.mContext).setSmallIcon(17301642).setContentTitle(this.mContext.getString(17039643)).setContentText((CharSequence)localObject).setColor(this.mContext.getColor(17170523)).setStyle(new Notification.BigTextStyle().bigText((CharSequence)localObject)).build();
    this.mInjector.getNotificationManager().notify(1001, (Notification)localObject);
  }
  
  private void setActiveAdmin(ComponentName paramComponentName, boolean paramBoolean, int paramInt, Bundle paramBundle)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEVICE_ADMINS", null);
    enforceFullCrossUsersPermission(paramInt);
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    DeviceAdminInfo localDeviceAdminInfo = findAdmin(paramComponentName, paramInt, true);
    if (localDeviceAdminInfo == null) {
      throw new IllegalArgumentException("Bad admin: " + paramComponentName);
    }
    if (!localDeviceAdminInfo.getActivityInfo().applicationInfo.isInternal()) {
      throw new IllegalArgumentException("Only apps in internal storage can be active admin: " + paramComponentName);
    }
    long l;
    ActiveAdmin localActiveAdmin1;
    try
    {
      l = this.mInjector.binderClearCallingIdentity();
      try
      {
        localActiveAdmin1 = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
        if ((!paramBoolean) && (localActiveAdmin1 != null)) {
          throw new IllegalArgumentException("Admin is already added");
        }
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      if (!localDevicePolicyData.mRemovingAdmins.contains(paramComponentName)) {
        break label186;
      }
    }
    finally {}
    throw new IllegalArgumentException("Trying to set an admin which is being removed");
    label186:
    ActiveAdmin localActiveAdmin2 = new ActiveAdmin(localDeviceAdminInfo, false);
    int k;
    int m;
    int i;
    if (localActiveAdmin1 != null)
    {
      paramBoolean = localActiveAdmin1.testOnlyAdmin;
      localActiveAdmin2.testOnlyAdmin = paramBoolean;
      localDevicePolicyData.mAdminMap.put(paramComponentName, localActiveAdmin2);
      k = -1;
      m = localDevicePolicyData.mAdminList.size();
      i = 0;
    }
    for (;;)
    {
      int j = k;
      if (i < m)
      {
        if (((ActiveAdmin)localDevicePolicyData.mAdminList.get(i)).info.getComponent().equals(paramComponentName)) {
          j = i;
        }
      }
      else
      {
        if (j == -1)
        {
          localDevicePolicyData.mAdminList.add(localActiveAdmin2);
          enableIfNecessary(localDeviceAdminInfo.getPackageName(), paramInt);
        }
        for (;;)
        {
          saveSettingsLocked(paramInt);
          sendAdminCommandLocked(localActiveAdmin2, "android.app.action.DEVICE_ADMIN_ENABLED", paramBundle, null);
          this.mInjector.binderRestoreCallingIdentity(l);
          return;
          paramBoolean = isPackageTestOnly(paramComponentName.getPackageName(), paramInt);
          break;
          localDevicePolicyData.mAdminList.set(j, localActiveAdmin2);
        }
      }
      i += 1;
    }
  }
  
  private void setBackupServiceEnabledInternal(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        long l = this.mInjector.binderClearCallingIdentity();
        try
        {
          localObject1 = this.mInjector.getIBackupManager();
          if (localObject1 != null) {
            ((IBackupManager)localObject1).setBackupServiceActive(0, paramBoolean);
          }
          this.mInjector.binderRestoreCallingIdentity(l);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Object localObject1;
          StringBuilder localStringBuilder = new StringBuilder().append("Failed ");
          if (paramBoolean)
          {
            localObject1 = "";
            throw new IllegalStateException((String)localObject1 + "activating backup service.", localRemoteException);
          }
        }
        finally
        {
          this.mInjector.binderRestoreCallingIdentity(l);
        }
        String str = "de";
      }
      finally {}
    }
  }
  
  private void setDeviceOwnerRemoteBugreportUriAndHash(String paramString1, String paramString2)
  {
    try
    {
      this.mOwners.setDeviceOwnerRemoteBugreportUriAndHash(paramString1, paramString2);
      return;
    }
    finally
    {
      paramString1 = finally;
      throw paramString1;
    }
  }
  
  private void setDeviceOwnerSystemPropertyLocked()
  {
    if (this.mInjector.settingsGlobalGetInt("device_provisioned", 0) == 0) {
      return;
    }
    if (StorageManager.inCryptKeeperBounce()) {
      return;
    }
    if (!TextUtils.isEmpty(this.mInjector.systemPropertiesGet("ro.device_owner"))) {
      Slog.w("DevicePolicyManagerService", "Trying to set ro.device_owner, but it has already been set?");
    }
    do
    {
      return;
      if (!this.mOwners.hasDeviceOwner()) {
        break;
      }
      this.mInjector.systemPropertiesSet("ro.device_owner", "true");
      Slog.i("DevicePolicyManagerService", "Set ro.device_owner property to true");
      disableDeviceOwnerManagedSingleUserFeaturesIfNeeded();
    } while (!this.mInjector.securityLogGetLoggingEnabledProperty());
    this.mSecurityLogMonitor.start();
    return;
    this.mInjector.systemPropertiesSet("ro.device_owner", "false");
    Slog.i("DevicePolicyManagerService", "Set ro.device_owner property to false");
  }
  
  private void setDoNotAskCredentialsOnBoot()
  {
    try
    {
      DevicePolicyData localDevicePolicyData = getUserData(0);
      if (!localDevicePolicyData.doNotAskCredentialsOnBoot)
      {
        localDevicePolicyData.doNotAskCredentialsOnBoot = true;
        saveSettingsLocked(0);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void setEncryptionRequested(boolean paramBoolean) {}
  
  private void setExpirationAlarmCheckLocked(Context paramContext, int paramInt, boolean paramBoolean)
  {
    long l1 = getPasswordExpirationLocked(null, paramInt, paramBoolean);
    long l3 = System.currentTimeMillis();
    l2 = l1 - l3;
    if (l1 == 0L)
    {
      l1 = 0L;
      l2 = this.mInjector.binderClearCallingIdentity();
      if (!paramBoolean) {
        break label170;
      }
    }
    for (;;)
    {
      try
      {
        paramInt = getProfileParentId(paramInt);
        AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
        paramContext = PendingIntent.getBroadcastAsUser(paramContext, 5571, new Intent("com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION"), 1207959552, UserHandle.of(paramInt));
        localAlarmManager.cancel(paramContext);
        if (l1 != 0L) {
          localAlarmManager.set(1, l1, paramContext);
        }
        return;
      }
      finally
      {
        label170:
        this.mInjector.binderRestoreCallingIdentity(l2);
      }
      if (l2 <= 0L)
      {
        l1 = l3 + 86400000L;
        break;
      }
      l2 %= 86400000L;
      l1 = l2;
      if (l2 == 0L) {
        l1 = 86400000L;
      }
      l1 = l3 + l1;
      break;
    }
  }
  
  private void setLockTaskPackagesLocked(int paramInt, List<String> paramList)
  {
    getUserData(paramInt).mLockTaskPackages = paramList;
    saveSettingsLocked(paramInt);
    updateLockTaskPackagesLocked(paramList, paramInt);
  }
  
  private boolean setStatusBarDisabledInternal(boolean paramBoolean, int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    for (;;)
    {
      int j;
      try
      {
        IStatusBarService localIStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.checkService("statusbar"));
        if (localIStatusBarService != null)
        {
          if (paramBoolean)
          {
            i = 34013184;
            break label147;
            localIStatusBarService.disableForUser(i, this.mToken, this.mContext.getPackageName(), paramInt);
            localIStatusBarService.disable2ForUser(j, this.mToken, this.mContext.getPackageName(), paramInt);
            return true;
          }
          int i = 0;
          break label147;
          j = 0;
          continue;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Slog.e("DevicePolicyManagerService", "Failed to disable the status bar", localRemoteException);
        this.mInjector.binderRestoreCallingIdentity(l);
        continue;
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      return false;
      label147:
      if (paramBoolean) {
        j = 1;
      }
    }
  }
  
  /* Error */
  private void shareBugreportWithDeviceOwnerIfExists(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aconst_null
    //   4: astore 5
    //   6: aload_1
    //   7: ifnonnull +75 -> 82
    //   10: aload 5
    //   12: astore_3
    //   13: aload 6
    //   15: astore 4
    //   17: new 1470	java/io/FileNotFoundException
    //   20: dup
    //   21: invokespecial 2279	java/io/FileNotFoundException:<init>	()V
    //   24: athrow
    //   25: astore_1
    //   26: aload_3
    //   27: astore 4
    //   29: new 1692	android/os/Bundle
    //   32: dup
    //   33: invokespecial 1693	android/os/Bundle:<init>	()V
    //   36: astore_1
    //   37: aload_3
    //   38: astore 4
    //   40: aload_1
    //   41: ldc_w 1776
    //   44: iconst_1
    //   45: invokevirtual 1779	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   48: aload_3
    //   49: astore 4
    //   51: aload_0
    //   52: ldc_w 1781
    //   55: aload_1
    //   56: invokevirtual 1785	com/android/server/devicepolicy/DevicePolicyManagerService:sendDeviceOwnerCommand	(Ljava/lang/String;Landroid/os/Bundle;)V
    //   59: aload_3
    //   60: ifnull +7 -> 67
    //   63: aload_3
    //   64: invokevirtual 2282	android/os/ParcelFileDescriptor:close	()V
    //   67: aload_0
    //   68: getfield 400	com/android/server/devicepolicy/DevicePolicyManagerService:mRemoteBugreportSharingAccepted	Ljava/util/concurrent/atomic/AtomicBoolean;
    //   71: iconst_0
    //   72: invokevirtual 1761	java/util/concurrent/atomic/AtomicBoolean:set	(Z)V
    //   75: aload_0
    //   76: aconst_null
    //   77: aconst_null
    //   78: invokespecial 1771	com/android/server/devicepolicy/DevicePolicyManagerService:setDeviceOwnerRemoteBugreportUriAndHash	(Ljava/lang/String;Ljava/lang/String;)V
    //   81: return
    //   82: aload 5
    //   84: astore_3
    //   85: aload 6
    //   87: astore 4
    //   89: aload_1
    //   90: invokestatic 2286	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   93: astore 7
    //   95: aload 5
    //   97: astore_3
    //   98: aload 6
    //   100: astore 4
    //   102: aload_0
    //   103: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   106: invokevirtual 2290	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   109: aload 7
    //   111: ldc_w 2292
    //   114: invokevirtual 2298	android/content/ContentResolver:openFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    //   117: astore_1
    //   118: aload_1
    //   119: astore_3
    //   120: aload_1
    //   121: astore 4
    //   123: aload_0
    //   124: monitorenter
    //   125: new 1795	android/content/Intent
    //   128: dup
    //   129: ldc_w 2300
    //   132: invokespecial 2090	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   135: astore_3
    //   136: aload_3
    //   137: aload_0
    //   138: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   141: invokevirtual 1038	com/android/server/devicepolicy/Owners:getDeviceOwnerComponent	()Landroid/content/ComponentName;
    //   144: invokevirtual 2304	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   147: pop
    //   148: aload_3
    //   149: aload 7
    //   151: ldc_w 1910
    //   154: invokevirtual 2308	android/content/Intent:setDataAndType	(Landroid/net/Uri;Ljava/lang/String;)Landroid/content/Intent;
    //   157: pop
    //   158: aload_3
    //   159: ldc_w 2310
    //   162: aload_2
    //   163: invokevirtual 2314	android/content/Intent:putExtra	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   166: pop
    //   167: aload_0
    //   168: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   171: aload_0
    //   172: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   175: invokevirtual 1038	com/android/server/devicepolicy/Owners:getDeviceOwnerComponent	()Landroid/content/ComponentName;
    //   178: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   181: aload 7
    //   183: iconst_1
    //   184: invokevirtual 2318	android/content/Context:grantUriPermission	(Ljava/lang/String;Landroid/net/Uri;I)V
    //   187: aload_0
    //   188: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   191: aload_3
    //   192: aload_0
    //   193: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   196: invokevirtual 923	com/android/server/devicepolicy/Owners:getDeviceOwnerUserId	()I
    //   199: invokestatic 2243	android/os/UserHandle:of	(I)Landroid/os/UserHandle;
    //   202: invokevirtual 2099	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   205: aload_1
    //   206: astore_3
    //   207: aload_1
    //   208: astore 4
    //   210: aload_0
    //   211: monitorexit
    //   212: aload_1
    //   213: ifnull +7 -> 220
    //   216: aload_1
    //   217: invokevirtual 2282	android/os/ParcelFileDescriptor:close	()V
    //   220: aload_0
    //   221: getfield 400	com/android/server/devicepolicy/DevicePolicyManagerService:mRemoteBugreportSharingAccepted	Ljava/util/concurrent/atomic/AtomicBoolean;
    //   224: iconst_0
    //   225: invokevirtual 1761	java/util/concurrent/atomic/AtomicBoolean:set	(Z)V
    //   228: aload_0
    //   229: aconst_null
    //   230: aconst_null
    //   231: invokespecial 1771	com/android/server/devicepolicy/DevicePolicyManagerService:setDeviceOwnerRemoteBugreportUriAndHash	(Ljava/lang/String;Ljava/lang/String;)V
    //   234: return
    //   235: astore_2
    //   236: aload_1
    //   237: astore_3
    //   238: aload_1
    //   239: astore 4
    //   241: aload_0
    //   242: monitorexit
    //   243: aload_1
    //   244: astore_3
    //   245: aload_1
    //   246: astore 4
    //   248: aload_2
    //   249: athrow
    //   250: astore_1
    //   251: aload 4
    //   253: ifnull +8 -> 261
    //   256: aload 4
    //   258: invokevirtual 2282	android/os/ParcelFileDescriptor:close	()V
    //   261: aload_0
    //   262: getfield 400	com/android/server/devicepolicy/DevicePolicyManagerService:mRemoteBugreportSharingAccepted	Ljava/util/concurrent/atomic/AtomicBoolean;
    //   265: iconst_0
    //   266: invokevirtual 1761	java/util/concurrent/atomic/AtomicBoolean:set	(Z)V
    //   269: aload_0
    //   270: aconst_null
    //   271: aconst_null
    //   272: invokespecial 1771	com/android/server/devicepolicy/DevicePolicyManagerService:setDeviceOwnerRemoteBugreportUriAndHash	(Ljava/lang/String;Ljava/lang/String;)V
    //   275: aload_1
    //   276: athrow
    //   277: astore_1
    //   278: goto -58 -> 220
    //   281: astore_1
    //   282: goto -215 -> 67
    //   285: astore_2
    //   286: goto -25 -> 261
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	289	0	this	DevicePolicyManagerService
    //   0	289	1	paramString1	String
    //   0	289	2	paramString2	String
    //   12	233	3	localObject1	Object
    //   15	242	4	localObject2	Object
    //   4	92	5	localObject3	Object
    //   1	98	6	localObject4	Object
    //   93	89	7	localUri	Uri
    // Exception table:
    //   from	to	target	type
    //   17	25	25	java/io/FileNotFoundException
    //   89	95	25	java/io/FileNotFoundException
    //   102	118	25	java/io/FileNotFoundException
    //   123	125	25	java/io/FileNotFoundException
    //   210	212	25	java/io/FileNotFoundException
    //   241	243	25	java/io/FileNotFoundException
    //   248	250	25	java/io/FileNotFoundException
    //   125	205	235	finally
    //   17	25	250	finally
    //   29	37	250	finally
    //   40	48	250	finally
    //   51	59	250	finally
    //   89	95	250	finally
    //   102	118	250	finally
    //   123	125	250	finally
    //   210	212	250	finally
    //   241	243	250	finally
    //   248	250	250	finally
    //   216	220	277	java/io/IOException
    //   63	67	281	java/io/IOException
    //   256	261	285	java/io/IOException
  }
  
  private void startUninstallIntent(String paramString, int paramInt)
  {
    Object localObject = new Pair(paramString, Integer.valueOf(paramInt));
    try
    {
      boolean bool = this.mPackagesToRemove.contains(localObject);
      if (!bool) {
        return;
      }
      this.mPackagesToRemove.remove(localObject);
      try
      {
        this.mInjector.getIActivityManager().forceStopPackage(paramString, paramInt);
        paramString = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + paramString));
        paramString.setFlags(268435456);
        this.mContext.startActivityAsUser(paramString, UserHandle.of(paramInt));
        return;
      }
      catch (RemoteException localRemoteException2)
      {
        for (;;)
        {
          Log.e("DevicePolicyManagerService", "Failure talking to ActivityManager while force stopping package");
        }
      }
    }
    finally
    {
      try
      {
        localObject = this.mInjector.getIPackageManager().getPackageInfo(paramString, 0, paramInt);
        if (localObject != null) {
          break label88;
        }
        return;
      }
      catch (RemoteException localRemoteException1)
      {
        Log.e("DevicePolicyManagerService", "Failure talking to PackageManager while getting package info");
      }
      paramString = finally;
    }
  }
  
  private void updateDeviceOwnerLocked()
  {
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      ComponentName localComponentName = this.mOwners.getDeviceOwnerComponent();
      if (localComponentName != null) {
        this.mInjector.getIActivityManager().updateDeviceOwner(localComponentName.getPackageName());
      }
      return;
    }
    catch (RemoteException localRemoteException) {}finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  private void updateLockTaskPackagesLocked(List<String> paramList, int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      this.mInjector.getIActivityManager().updateLockTaskPackages(paramInt, (String[])paramList.toArray(new String[paramList.size()]));
      this.mInjector.binderRestoreCallingIdentity(l);
      return;
    }
    catch (RemoteException paramList)
    {
      paramList = paramList;
      this.mInjector.binderRestoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramList = finally;
      this.mInjector.binderRestoreCallingIdentity(l);
      throw paramList;
    }
  }
  
  private void updatePasswordExpirationsLocked(int paramInt)
  {
    Object localObject = new ArraySet();
    List localList = getActiveAdminsForLockscreenPoliciesLocked(paramInt, false);
    int i = localList.size();
    paramInt = 0;
    if (paramInt < i)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)localList.get(paramInt);
      long l;
      if (localActiveAdmin.info.usesPolicy(6))
      {
        ((ArraySet)localObject).add(Integer.valueOf(localActiveAdmin.getUserHandle().getIdentifier()));
        l = localActiveAdmin.passwordExpirationTimeout;
        if (l <= 0L) {
          break label106;
        }
      }
      label106:
      for (l += System.currentTimeMillis();; l = 0L)
      {
        localActiveAdmin.passwordExpirationDate = l;
        paramInt += 1;
        break;
      }
    }
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      saveSettingsLocked(((Integer)((Iterator)localObject).next()).intValue());
    }
  }
  
  private void updateScreenCaptureDisabledInWindowManager(final int paramInt, final boolean paramBoolean)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        try
        {
          DevicePolicyManagerService.this.mInjector.getIWindowManager().setScreenCaptureDisabled(paramInt, paramBoolean);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("DevicePolicyManagerService", "Unable to notify WindowManager.", localRemoteException);
        }
      }
    });
  }
  
  static void validateQualityConstant(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Invalid quality constant: 0x" + Integer.toHexString(paramInt));
    }
  }
  
  private void wipeDataNoLock(boolean paramBoolean, String paramString)
  {
    if (paramBoolean) {
      ((StorageManager)this.mContext.getSystemService("storage")).wipeAdoptableDisks();
    }
    try
    {
      RecoverySystem.rebootWipeUserData(this.mContext, paramString);
      return;
    }
    catch (IOException|SecurityException paramString)
    {
      Slog.w("DevicePolicyManagerService", "Failed requesting data wipe", paramString);
    }
  }
  
  private void wipeDeviceNoLock(boolean paramBoolean, final int paramInt, String paramString)
  {
    l = this.mInjector.binderClearCallingIdentity();
    if (paramInt == 0) {}
    for (;;)
    {
      try
      {
        wipeDataNoLock(paramBoolean, paramString);
        return;
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          try
          {
            IActivityManager localIActivityManager = DevicePolicyManagerService.this.mInjector.getIActivityManager();
            if (localIActivityManager.getCurrentUser().id == paramInt) {
              localIActivityManager.switchUser(0);
            }
            boolean bool = DevicePolicyManagerService.-wrap0(DevicePolicyManagerService.this, paramInt);
            if (!DevicePolicyManagerService.this.mUserManager.removeUser(paramInt))
            {
              Slog.w("DevicePolicyManagerService", "Couldn't remove user " + paramInt);
              return;
            }
            if (bool)
            {
              DevicePolicyManagerService.-wrap18(DevicePolicyManagerService.this);
              return;
            }
          }
          catch (RemoteException localRemoteException) {}
        }
      });
    }
  }
  
  /* Error */
  public void addCrossProfileIntentFilter(ComponentName paramComponentName, IntentFilter paramIntentFilter, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore 4
    //   13: aload_0
    //   14: monitorenter
    //   15: aload_0
    //   16: aload_1
    //   17: iconst_m1
    //   18: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   21: pop
    //   22: aload_0
    //   23: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   26: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   29: lstore 5
    //   31: aload_0
    //   32: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   35: iload 4
    //   37: invokevirtual 1185	android/os/UserManager:getProfileParent	(I)Landroid/content/pm/UserInfo;
    //   40: astore 7
    //   42: aload 7
    //   44: ifnonnull +24 -> 68
    //   47: ldc 125
    //   49: ldc_w 2397
    //   52: invokestatic 611	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   55: pop
    //   56: aload_0
    //   57: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   60: lload 5
    //   62: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   65: aload_0
    //   66: monitorexit
    //   67: return
    //   68: iload_3
    //   69: iconst_1
    //   70: iand
    //   71: ifeq +25 -> 96
    //   74: aload_0
    //   75: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   78: aload_2
    //   79: aload_1
    //   80: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   83: iload 4
    //   85: aload 7
    //   87: getfield 603	android/content/pm/UserInfo:id	I
    //   90: iconst_0
    //   91: invokeinterface 2400 6 0
    //   96: iload_3
    //   97: iconst_2
    //   98: iand
    //   99: ifeq +25 -> 124
    //   102: aload_0
    //   103: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   106: aload_2
    //   107: aload_1
    //   108: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   111: aload 7
    //   113: getfield 603	android/content/pm/UserInfo:id	I
    //   116: iload 4
    //   118: iconst_0
    //   119: invokeinterface 2400 6 0
    //   124: aload_0
    //   125: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   128: lload 5
    //   130: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   133: aload_0
    //   134: monitorexit
    //   135: return
    //   136: astore_1
    //   137: aload_0
    //   138: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   141: lload 5
    //   143: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   146: goto -13 -> 133
    //   149: astore_1
    //   150: aload_0
    //   151: monitorexit
    //   152: aload_1
    //   153: athrow
    //   154: astore_1
    //   155: aload_0
    //   156: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   159: lload 5
    //   161: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   164: aload_1
    //   165: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	166	0	this	DevicePolicyManagerService
    //   0	166	1	paramComponentName	ComponentName
    //   0	166	2	paramIntentFilter	IntentFilter
    //   0	166	3	paramInt	int
    //   11	106	4	i	int
    //   29	131	5	l	long
    //   40	72	7	localUserInfo	UserInfo
    // Exception table:
    //   from	to	target	type
    //   31	42	136	android/os/RemoteException
    //   47	56	136	android/os/RemoteException
    //   74	96	136	android/os/RemoteException
    //   102	124	136	android/os/RemoteException
    //   15	31	149	finally
    //   56	65	149	finally
    //   124	133	149	finally
    //   137	146	149	finally
    //   155	166	149	finally
    //   31	42	154	finally
    //   47	56	154	finally
    //   74	96	154	finally
    //   102	124	154	finally
  }
  
  /* Error */
  public boolean addCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   3: istore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: aload_1
    //   11: iconst_m1
    //   12: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   15: astore_1
    //   16: aload_1
    //   17: getfield 2405	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:crossProfileWidgetProviders	Ljava/util/List;
    //   20: ifnonnull +14 -> 34
    //   23: aload_1
    //   24: new 1011	java/util/ArrayList
    //   27: dup
    //   28: invokespecial 1119	java/util/ArrayList:<init>	()V
    //   31: putfield 2405	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:crossProfileWidgetProviders	Ljava/util/List;
    //   34: aload_1
    //   35: getfield 2405	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:crossProfileWidgetProviders	Ljava/util/List;
    //   38: astore 5
    //   40: aload 4
    //   42: astore_1
    //   43: aload 5
    //   45: aload_2
    //   46: invokeinterface 655 2 0
    //   51: ifne +27 -> 78
    //   54: aload 5
    //   56: aload_2
    //   57: invokeinterface 1618 2 0
    //   62: pop
    //   63: new 1011	java/util/ArrayList
    //   66: dup
    //   67: aload 5
    //   69: invokespecial 2408	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
    //   72: astore_1
    //   73: aload_0
    //   74: iload_3
    //   75: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   78: aload_0
    //   79: monitorexit
    //   80: aload_1
    //   81: ifnull +19 -> 100
    //   84: aload_0
    //   85: getfield 487	com/android/server/devicepolicy/DevicePolicyManagerService:mLocalService	Lcom/android/server/devicepolicy/DevicePolicyManagerService$LocalService;
    //   88: iload_3
    //   89: aload_1
    //   90: invokestatic 2411	com/android/server/devicepolicy/DevicePolicyManagerService$LocalService:-wrap0	(Lcom/android/server/devicepolicy/DevicePolicyManagerService$LocalService;ILjava/util/List;)V
    //   93: iconst_1
    //   94: ireturn
    //   95: astore_1
    //   96: aload_0
    //   97: monitorexit
    //   98: aload_1
    //   99: athrow
    //   100: iconst_0
    //   101: ireturn
    //   102: astore_1
    //   103: goto -7 -> 96
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	DevicePolicyManagerService
    //   0	106	1	paramComponentName	ComponentName
    //   0	106	2	paramString	String
    //   3	86	3	i	int
    //   5	36	4	localObject	Object
    //   38	30	5	localList	List
    // Exception table:
    //   from	to	target	type
    //   9	34	95	finally
    //   34	40	95	finally
    //   43	73	95	finally
    //   73	78	102	finally
  }
  
  /* Error */
  public void addPersistentPreferredActivity(ComponentName paramComponentName1, IntentFilter paramIntentFilter, ComponentName paramComponentName2)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore 4
    //   13: aload_0
    //   14: monitorenter
    //   15: aload_0
    //   16: aload_1
    //   17: iconst_m1
    //   18: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   21: pop
    //   22: aload_0
    //   23: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   26: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   29: lstore 5
    //   31: aload_0
    //   32: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   35: aload_2
    //   36: aload_3
    //   37: iload 4
    //   39: invokeinterface 2416 4 0
    //   44: aload_0
    //   45: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   48: lload 5
    //   50: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   53: aload_0
    //   54: monitorexit
    //   55: return
    //   56: astore_1
    //   57: aload_0
    //   58: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   61: lload 5
    //   63: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   66: goto -13 -> 53
    //   69: astore_1
    //   70: aload_0
    //   71: monitorexit
    //   72: aload_1
    //   73: athrow
    //   74: astore_1
    //   75: aload_0
    //   76: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   79: lload 5
    //   81: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   84: aload_1
    //   85: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	86	0	this	DevicePolicyManagerService
    //   0	86	1	paramComponentName1	ComponentName
    //   0	86	2	paramIntentFilter	IntentFilter
    //   0	86	3	paramComponentName2	ComponentName
    //   11	27	4	i	int
    //   29	51	5	l	long
    // Exception table:
    //   from	to	target	type
    //   31	44	56	android/os/RemoteException
    //   15	31	69	finally
    //   44	53	69	finally
    //   57	66	69	finally
    //   75	86	69	finally
    //   31	44	74	finally
  }
  
  public boolean approveCaCert(String paramString, int paramInt, boolean paramBoolean)
  {
    enforceManageUsers();
    try
    {
      ArraySet localArraySet = getUserData(paramInt).mAcceptedCaCertificates;
      if (paramBoolean) {}
      for (paramBoolean = localArraySet.add(paramString); !paramBoolean; paramBoolean = localArraySet.remove(paramString)) {
        return false;
      }
      saveSettingsLocked(paramInt);
      new MonitoringCertNotificationTask(null).execute(new Integer[] { Integer.valueOf(paramInt) });
      return true;
    }
    finally {}
  }
  
  public void choosePrivateKeyAlias(int paramInt, Uri paramUri, String paramString, final IBinder paramIBinder)
  {
    if (!isCallerWithSystemUid()) {
      return;
    }
    UserHandle localUserHandle = this.mInjector.binderGetCallingUserHandle();
    Object localObject2 = getProfileOwner(localUserHandle.getIdentifier());
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = localObject2;
      if (localUserHandle.isSystem())
      {
        ActiveAdmin localActiveAdmin = getDeviceOwnerAdminLocked();
        localObject1 = localObject2;
        if (localActiveAdmin != null) {
          localObject1 = localActiveAdmin.info.getComponent();
        }
      }
    }
    if (localObject1 == null)
    {
      sendPrivateKeyAliasResponse(null, paramIBinder);
      return;
    }
    localObject2 = new Intent("android.app.action.CHOOSE_PRIVATE_KEY_ALIAS");
    ((Intent)localObject2).setComponent((ComponentName)localObject1);
    ((Intent)localObject2).putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_SENDER_UID", paramInt);
    ((Intent)localObject2).putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_URI", paramUri);
    ((Intent)localObject2).putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_ALIAS", paramString);
    ((Intent)localObject2).putExtra("android.app.extra.CHOOSE_PRIVATE_KEY_RESPONSE", paramIBinder);
    ((Intent)localObject2).addFlags(268435456);
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      this.mContext.sendOrderedBroadcastAsUser((Intent)localObject2, localUserHandle, null, new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          paramAnonymousContext = getResultData();
          DevicePolicyManagerService.-wrap17(DevicePolicyManagerService.this, paramAnonymousContext, paramIBinder);
        }
      }, null, -1, null, null);
      return;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  /* Error */
  public void clearCrossProfileIntentFilters(ComponentName paramComponentName)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore_2
    //   12: aload_0
    //   13: monitorenter
    //   14: aload_0
    //   15: aload_1
    //   16: iconst_m1
    //   17: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   20: pop
    //   21: aload_0
    //   22: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   25: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   28: lstore_3
    //   29: aload_0
    //   30: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   33: iload_2
    //   34: invokevirtual 1185	android/os/UserManager:getProfileParent	(I)Landroid/content/pm/UserInfo;
    //   37: astore 5
    //   39: aload 5
    //   41: ifnonnull +23 -> 64
    //   44: ldc 125
    //   46: ldc_w 2464
    //   49: invokestatic 611	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: aload_0
    //   54: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   57: lload_3
    //   58: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   61: aload_0
    //   62: monitorexit
    //   63: return
    //   64: aload_0
    //   65: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   68: iload_2
    //   69: aload_1
    //   70: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   73: invokeinterface 2466 3 0
    //   78: aload_0
    //   79: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   82: aload 5
    //   84: getfield 603	android/content/pm/UserInfo:id	I
    //   87: aload_1
    //   88: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   91: invokeinterface 2466 3 0
    //   96: aload_0
    //   97: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   100: lload_3
    //   101: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   104: aload_0
    //   105: monitorexit
    //   106: return
    //   107: astore_1
    //   108: aload_0
    //   109: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   112: lload_3
    //   113: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   116: goto -12 -> 104
    //   119: astore_1
    //   120: aload_0
    //   121: monitorexit
    //   122: aload_1
    //   123: athrow
    //   124: astore_1
    //   125: aload_0
    //   126: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   129: lload_3
    //   130: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   133: aload_1
    //   134: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	this	DevicePolicyManagerService
    //   0	135	1	paramComponentName	ComponentName
    //   11	58	2	i	int
    //   28	102	3	l	long
    //   37	46	5	localUserInfo	UserInfo
    // Exception table:
    //   from	to	target	type
    //   29	39	107	android/os/RemoteException
    //   44	53	107	android/os/RemoteException
    //   64	96	107	android/os/RemoteException
    //   14	29	119	finally
    //   53	61	119	finally
    //   96	104	119	finally
    //   108	116	119	finally
    //   125	135	119	finally
    //   29	39	124	finally
    //   44	53	124	finally
    //   64	96	124	finally
  }
  
  public void clearDeviceOwner(String paramString)
  {
    Preconditions.checkNotNull(paramString, "packageName is null");
    int i = this.mInjector.binderGetCallingUid();
    try
    {
      if (this.mContext.getPackageManager().getPackageUidAsUser(paramString, UserHandle.getUserId(i)) != i) {
        throw new SecurityException("Invalid packageName");
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      throw new SecurityException(paramString);
    }
    ComponentName localComponentName;
    int j;
    long l;
    try
    {
      localComponentName = this.mOwners.getDeviceOwnerComponent();
      j = this.mOwners.getDeviceOwnerUserId();
      if ((!this.mOwners.hasDeviceOwner()) || (!localComponentName.getPackageName().equals(paramString)) || (j != UserHandle.getUserId(i))) {
        throw new SecurityException("clearDeviceOwner can only be called by the device owner");
      }
    }
    finally
    {
      throw paramString;
      enforceUserUnlocked(j);
      paramString = getDeviceOwnerAdminLocked();
    }
  }
  
  /* Error */
  public void clearPackagePersistentPreferredActivities(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore_3
    //   12: aload_0
    //   13: monitorenter
    //   14: aload_0
    //   15: aload_1
    //   16: iconst_m1
    //   17: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   20: pop
    //   21: aload_0
    //   22: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   25: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   28: lstore 4
    //   30: aload_0
    //   31: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   34: aload_2
    //   35: iload_3
    //   36: invokeinterface 2484 3 0
    //   41: aload_0
    //   42: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   45: lload 4
    //   47: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   50: aload_0
    //   51: monitorexit
    //   52: return
    //   53: astore_1
    //   54: aload_0
    //   55: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   58: lload 4
    //   60: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   63: goto -13 -> 50
    //   66: astore_1
    //   67: aload_0
    //   68: monitorexit
    //   69: aload_1
    //   70: athrow
    //   71: astore_1
    //   72: aload_0
    //   73: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   76: lload 4
    //   78: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   81: aload_1
    //   82: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	DevicePolicyManagerService
    //   0	83	1	paramComponentName	ComponentName
    //   0	83	2	paramString	String
    //   11	25	3	i	int
    //   28	49	4	l	long
    // Exception table:
    //   from	to	target	type
    //   30	41	53	android/os/RemoteException
    //   14	30	66	finally
    //   41	50	66	finally
    //   54	63	66	finally
    //   72	83	66	finally
    //   30	41	71	finally
  }
  
  /* Error */
  public void clearProfileOwner(ComponentName paramComponentName)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   12: invokevirtual 2425	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderGetCallingUserHandle	()Landroid/os/UserHandle;
    //   15: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   18: istore_2
    //   19: aload_0
    //   20: iload_2
    //   21: ldc_w 2487
    //   24: invokespecial 2489	com/android/server/devicepolicy/DevicePolicyManagerService:enforceNotManagedProfile	(ILjava/lang/String;)V
    //   27: aload_0
    //   28: iload_2
    //   29: invokespecial 980	com/android/server/devicepolicy/DevicePolicyManagerService:enforceUserUnlocked	(I)V
    //   32: aload_0
    //   33: aload_1
    //   34: iconst_m1
    //   35: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   38: astore 5
    //   40: aload_0
    //   41: monitorenter
    //   42: aload_0
    //   43: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   46: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   49: lstore_3
    //   50: aload_0
    //   51: aload 5
    //   53: iload_2
    //   54: invokevirtual 2492	com/android/server/devicepolicy/DevicePolicyManagerService:clearProfileOwnerLocked	(Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;I)V
    //   57: aload_0
    //   58: aload_1
    //   59: iload_2
    //   60: invokevirtual 2478	com/android/server/devicepolicy/DevicePolicyManagerService:removeActiveAdminLocked	(Landroid/content/ComponentName;I)V
    //   63: aload_0
    //   64: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   67: lload_3
    //   68: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   71: ldc 125
    //   73: new 697	java/lang/StringBuilder
    //   76: dup
    //   77: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   80: ldc_w 2494
    //   83: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_1
    //   87: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: ldc_w 1933
    //   93: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: iload_2
    //   97: invokevirtual 707	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   100: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   103: invokestatic 1935	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   106: pop
    //   107: aload_0
    //   108: monitorexit
    //   109: return
    //   110: astore_1
    //   111: aload_0
    //   112: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   115: lload_3
    //   116: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   119: aload_1
    //   120: athrow
    //   121: astore_1
    //   122: aload_0
    //   123: monitorexit
    //   124: aload_1
    //   125: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	DevicePolicyManagerService
    //   0	126	1	paramComponentName	ComponentName
    //   18	79	2	i	int
    //   49	67	3	l	long
    //   38	14	5	localActiveAdmin	ActiveAdmin
    // Exception table:
    //   from	to	target	type
    //   50	63	110	finally
    //   42	50	121	finally
    //   63	107	121	finally
    //   111	121	121	finally
  }
  
  public void clearProfileOwnerLocked(ActiveAdmin paramActiveAdmin, int paramInt)
  {
    if (paramActiveAdmin != null)
    {
      paramActiveAdmin.disableCamera = false;
      paramActiveAdmin.userRestrictions = null;
    }
    clearUserPoliciesLocked(paramInt);
    this.mOwners.removeProfileOwner(paramInt);
    this.mOwners.writeProfileOwner(paramInt);
  }
  
  /* Error */
  public UserHandle createAndManageUser(ComponentName paramComponentName1, String paramString, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2504
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: aload_3
    //   9: ldc_w 2506
    //   12: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_1
    //   17: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   20: aload_3
    //   21: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   24: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   27: ifne +47 -> 74
    //   30: new 910	java/lang/IllegalArgumentException
    //   33: dup
    //   34: new 697	java/lang/StringBuilder
    //   37: dup
    //   38: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   41: ldc_w 2508
    //   44: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: aload_3
    //   48: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   51: ldc_w 2510
    //   54: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: aload_1
    //   58: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   61: ldc_w 2512
    //   64: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   70: invokespecial 913	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   73: athrow
    //   74: aload_0
    //   75: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   78: invokevirtual 2425	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderGetCallingUserHandle	()Landroid/os/UserHandle;
    //   81: invokevirtual 2432	android/os/UserHandle:isSystem	()Z
    //   84: ifne +14 -> 98
    //   87: new 864	java/lang/SecurityException
    //   90: dup
    //   91: ldc_w 2514
    //   94: invokespecial 867	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   97: athrow
    //   98: aload_0
    //   99: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   102: invokevirtual 684	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:userManagerIsSplitSystemUser	()Z
    //   105: ifne +21 -> 126
    //   108: iload 5
    //   110: iconst_2
    //   111: iand
    //   112: ifeq +14 -> 126
    //   115: new 910	java/lang/IllegalArgumentException
    //   118: dup
    //   119: ldc_w 2516
    //   122: invokespecial 913	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   125: athrow
    //   126: aconst_null
    //   127: astore 9
    //   129: aload_0
    //   130: monitorenter
    //   131: aload_0
    //   132: aload_1
    //   133: bipush -2
    //   135: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   138: pop
    //   139: aload_0
    //   140: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   143: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   146: lstore 7
    //   148: iconst_0
    //   149: istore 6
    //   151: iload 5
    //   153: iconst_2
    //   154: iand
    //   155: ifeq +8 -> 163
    //   158: sipush 256
    //   161: istore 6
    //   163: aload_0
    //   164: getfield 468	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManagerInternal	Landroid/os/UserManagerInternal;
    //   167: aload_2
    //   168: iload 6
    //   170: invokevirtual 2520	android/os/UserManagerInternal:createUserEvenWhenDisallowed	(Ljava/lang/String;I)Landroid/content/pm/UserInfo;
    //   173: astore 10
    //   175: aload 9
    //   177: astore_2
    //   178: aload 10
    //   180: ifnull +9 -> 189
    //   183: aload 10
    //   185: invokevirtual 1755	android/content/pm/UserInfo:getUserHandle	()Landroid/os/UserHandle;
    //   188: astore_2
    //   189: aload_0
    //   190: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   193: lload 7
    //   195: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   198: aload_0
    //   199: monitorexit
    //   200: aload_2
    //   201: ifnonnull +22 -> 223
    //   204: aconst_null
    //   205: areturn
    //   206: astore_1
    //   207: aload_0
    //   208: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   211: lload 7
    //   213: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   216: aload_1
    //   217: athrow
    //   218: astore_1
    //   219: aload_0
    //   220: monitorexit
    //   221: aload_1
    //   222: athrow
    //   223: aload_0
    //   224: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   227: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   230: lstore 7
    //   232: aload_1
    //   233: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   236: astore_1
    //   237: aload_2
    //   238: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   241: istore 6
    //   243: aload_0
    //   244: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   247: aload_1
    //   248: iload 6
    //   250: invokeinterface 2523 3 0
    //   255: ifne +16 -> 271
    //   258: aload_0
    //   259: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   262: aload_1
    //   263: iload 6
    //   265: invokeinterface 2526 3 0
    //   270: pop
    //   271: aload_0
    //   272: aload_3
    //   273: iconst_1
    //   274: iload 6
    //   276: invokevirtual 2529	com/android/server/devicepolicy/DevicePolicyManagerService:setActiveAdmin	(Landroid/content/ComponentName;ZI)V
    //   279: aload_0
    //   280: monitorenter
    //   281: aload_0
    //   282: iload 6
    //   284: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   287: astore_1
    //   288: aload_1
    //   289: aload 4
    //   291: putfield 1638	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mInitBundle	Landroid/os/PersistableBundle;
    //   294: aload_1
    //   295: iconst_1
    //   296: putfield 1628	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAdminBroadcastPending	Z
    //   299: aload_0
    //   300: iload 6
    //   302: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   305: aload_0
    //   306: monitorexit
    //   307: aload_0
    //   308: aload_3
    //   309: aload_0
    //   310: invokestatic 2534	android/os/Process:myUserHandle	()Landroid/os/UserHandle;
    //   313: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   316: invokevirtual 2537	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwnerName	(I)Ljava/lang/String;
    //   319: iload 6
    //   321: invokevirtual 2541	com/android/server/devicepolicy/DevicePolicyManagerService:setProfileOwner	(Landroid/content/ComponentName;Ljava/lang/String;I)Z
    //   324: pop
    //   325: iload 5
    //   327: iconst_1
    //   328: iand
    //   329: ifeq +20 -> 349
    //   332: aload_0
    //   333: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   336: invokevirtual 2290	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   339: ldc_w 2543
    //   342: iconst_1
    //   343: iload 6
    //   345: invokestatic 2549	android/provider/Settings$Secure:putIntForUser	(Landroid/content/ContentResolver;Ljava/lang/String;II)Z
    //   348: pop
    //   349: aload_0
    //   350: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   353: lload 7
    //   355: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   358: aload_2
    //   359: areturn
    //   360: astore_1
    //   361: ldc 125
    //   363: ldc_w 2551
    //   366: aload_1
    //   367: invokestatic 620	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   370: pop
    //   371: aload_0
    //   372: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   375: aload_2
    //   376: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   379: invokevirtual 2554	android/os/UserManager:removeUser	(I)Z
    //   382: pop
    //   383: aload_0
    //   384: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   387: lload 7
    //   389: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   392: aconst_null
    //   393: areturn
    //   394: astore_1
    //   395: aload_0
    //   396: monitorexit
    //   397: aload_1
    //   398: athrow
    //   399: astore_1
    //   400: aload_0
    //   401: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   404: lload 7
    //   406: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   409: aload_1
    //   410: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	411	0	this	DevicePolicyManagerService
    //   0	411	1	paramComponentName1	ComponentName
    //   0	411	2	paramString	String
    //   0	411	3	paramComponentName2	ComponentName
    //   0	411	4	paramPersistableBundle	PersistableBundle
    //   0	411	5	paramInt	int
    //   149	195	6	i	int
    //   146	259	7	l	long
    //   127	49	9	localObject	Object
    //   173	11	10	localUserInfo	UserInfo
    // Exception table:
    //   from	to	target	type
    //   163	175	206	finally
    //   183	189	206	finally
    //   131	148	218	finally
    //   189	198	218	finally
    //   207	218	218	finally
    //   243	271	360	android/os/RemoteException
    //   281	305	394	finally
    //   232	243	399	finally
    //   243	271	399	finally
    //   271	281	399	finally
    //   305	325	399	finally
    //   332	349	399	finally
    //   361	383	399	finally
    //   395	399	399	finally
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump DevicePolicyManagerService from from pid=" + this.mInjector.binderGetCallingPid() + ", uid=" + this.mInjector.binderGetCallingUid());
      return;
    }
    for (;;)
    {
      int j;
      try
      {
        paramPrintWriter.println("Current Device Policy Manager state:");
        this.mOwners.dump("  ", paramPrintWriter);
        int k = this.mUserData.size();
        int i = 0;
        if (i < k)
        {
          paramFileDescriptor = getUserData(this.mUserData.keyAt(i));
          paramPrintWriter.println();
          paramPrintWriter.println("  Enabled Device Admins (User " + paramFileDescriptor.mUserHandle + ", provisioningState: " + paramFileDescriptor.mUserProvisioningState + "):");
          int m = paramFileDescriptor.mAdminList.size();
          j = 0;
          if (j < m)
          {
            paramArrayOfString = (ActiveAdmin)paramFileDescriptor.mAdminList.get(j);
            if (paramArrayOfString != null)
            {
              paramPrintWriter.print("    ");
              paramPrintWriter.print(paramArrayOfString.info.getComponent().flattenToShortString());
              paramPrintWriter.println(":");
              paramArrayOfString.dump("      ", paramPrintWriter);
            }
          }
          else
          {
            if (!paramFileDescriptor.mRemovingAdmins.isEmpty()) {
              paramPrintWriter.println("    Removing Device Admins (User " + paramFileDescriptor.mUserHandle + "): " + paramFileDescriptor.mRemovingAdmins);
            }
            paramPrintWriter.println(" ");
            paramPrintWriter.print("    mPasswordOwner=");
            paramPrintWriter.println(paramFileDescriptor.mPasswordOwner);
            i += 1;
          }
        }
        else
        {
          paramPrintWriter.println();
          paramPrintWriter.println("Encryption Status: " + getEncryptionStatusName(getEncryptionStatus()));
          return;
        }
      }
      finally {}
      j += 1;
    }
  }
  
  /* Error */
  public void enableSystemApp(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: aload_1
    //   12: iconst_m1
    //   13: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   16: pop
    //   17: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   20: istore_3
    //   21: aload_0
    //   22: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   25: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   28: lstore 5
    //   30: aload_0
    //   31: iload_3
    //   32: invokespecial 287	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileParentId	(I)I
    //   35: istore 4
    //   37: aload_0
    //   38: aload_0
    //   39: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   42: aload_2
    //   43: iload 4
    //   45: invokespecial 2618	com/android/server/devicepolicy/DevicePolicyManagerService:isSystemApp	(Landroid/content/pm/IPackageManager;Ljava/lang/String;I)Z
    //   48: ifne +54 -> 102
    //   51: new 910	java/lang/IllegalArgumentException
    //   54: dup
    //   55: ldc_w 2620
    //   58: invokespecial 913	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   61: athrow
    //   62: astore_1
    //   63: ldc 125
    //   65: new 697	java/lang/StringBuilder
    //   68: dup
    //   69: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   72: ldc_w 2622
    //   75: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   78: aload_2
    //   79: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   85: aload_1
    //   86: invokestatic 2625	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   89: pop
    //   90: aload_0
    //   91: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   94: lload 5
    //   96: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   99: aload_0
    //   100: monitorexit
    //   101: return
    //   102: aload_0
    //   103: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   106: aload_2
    //   107: iload_3
    //   108: invokeinterface 2526 3 0
    //   113: pop
    //   114: aload_0
    //   115: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   118: lload 5
    //   120: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   123: goto -24 -> 99
    //   126: astore_1
    //   127: aload_0
    //   128: monitorexit
    //   129: aload_1
    //   130: athrow
    //   131: astore_1
    //   132: aload_0
    //   133: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   136: lload 5
    //   138: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   141: aload_1
    //   142: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	143	0	this	DevicePolicyManagerService
    //   0	143	1	paramComponentName	ComponentName
    //   0	143	2	paramString	String
    //   20	88	3	i	int
    //   35	9	4	j	int
    //   28	109	5	l	long
    // Exception table:
    //   from	to	target	type
    //   30	62	62	android/os/RemoteException
    //   102	114	62	android/os/RemoteException
    //   10	30	126	finally
    //   90	99	126	finally
    //   114	123	126	finally
    //   132	143	126	finally
    //   30	62	131	finally
    //   63	90	131	finally
    //   102	114	131	finally
  }
  
  public int enableSystemAppWithIntent(ComponentName paramComponentName, Intent paramIntent)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int j;
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      int k = UserHandle.getCallingUserId();
      long l = this.mInjector.binderClearCallingIdentity();
      for (;;)
      {
        try
        {
          m = getProfileParentId(k);
          paramComponentName = this.mIPackageManager.queryIntentActivities(paramIntent, paramIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 786432, m).getList();
          j = 0;
          i = 0;
          if (paramComponentName == null) {
            break label261;
          }
          paramComponentName = paramComponentName.iterator();
        }
        catch (RemoteException paramComponentName)
        {
          int m;
          int i;
          Object localObject;
          Slog.wtf("DevicePolicyManagerService", "Failed to resolve intent for: " + paramIntent);
          this.mInjector.binderRestoreCallingIdentity(l);
          return 0;
          Slog.d("DevicePolicyManagerService", "Not enabling " + (String)localObject + " since is not a" + " system app");
          continue;
        }
        finally
        {
          this.mInjector.binderRestoreCallingIdentity(l);
        }
        j = i;
        if (!paramComponentName.hasNext()) {
          break label261;
        }
        localObject = (ResolveInfo)paramComponentName.next();
        if (((ResolveInfo)localObject).activityInfo != null)
        {
          localObject = ((ResolveInfo)localObject).activityInfo.packageName;
          if (!isSystemApp(this.mIPackageManager, (String)localObject, m)) {
            continue;
          }
          i += 1;
          this.mIPackageManager.installExistingPackageAsUser((String)localObject, k);
        }
      }
      this.mInjector.binderRestoreCallingIdentity(l);
    }
    finally {}
    label261:
    return j;
  }
  
  public void enforceCanManageCaCerts(ComponentName paramComponentName)
  {
    if (paramComponentName == null)
    {
      if (!isCallerDelegatedCertInstaller()) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_CA_CERTIFICATES", null);
      }
      return;
    }
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public DeviceAdminInfo findAdmin(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return null;
    }
    enforceFullCrossUsersPermission(paramInt);
    Object localObject1 = null;
    try
    {
      localObject2 = this.mIPackageManager.getReceiverInfo(paramComponentName, 819328, paramInt);
      localObject1 = localObject2;
    }
    catch (RemoteException localRemoteException)
    {
      Object localObject2;
      for (;;) {}
    }
    if (localObject1 == null) {
      throw new IllegalArgumentException("Unknown admin: " + paramComponentName);
    }
    if (!"android.permission.BIND_DEVICE_ADMIN".equals(((ActivityInfo)localObject1).permission))
    {
      localObject2 = "DeviceAdminReceiver " + paramComponentName + " must be protected with " + "android.permission.BIND_DEVICE_ADMIN";
      Slog.w("DevicePolicyManagerService", (String)localObject2);
      if ((paramBoolean) && (((ActivityInfo)localObject1).applicationInfo.targetSdkVersion > 23)) {
        throw new IllegalArgumentException((String)localObject2);
      }
    }
    try
    {
      localObject1 = new DeviceAdminInfo(this.mContext, (ActivityInfo)localObject1);
      return (DeviceAdminInfo)localObject1;
    }
    catch (XmlPullParserException|IOException localXmlPullParserException)
    {
      Slog.w("DevicePolicyManagerService", "Bad device admin requested for user=" + paramInt + ": " + paramComponentName, localXmlPullParserException);
      return null;
    }
  }
  
  public void forceRemoveActiveAdmin(ComponentName paramComponentName, int paramInt)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    enforceShell("forceRemoveActiveAdmin");
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      try
      {
        if (!isAdminTestOnlyLocked(paramComponentName, paramInt)) {
          throw new SecurityException("Attempt to remove non-test admin " + paramComponentName + " " + paramInt);
        }
      }
      finally {}
      if (!isDeviceOwner(paramComponentName, paramInt)) {
        break label114;
      }
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
    clearDeviceOwnerLocked(getDeviceOwnerAdminLocked(), paramInt);
    label114:
    if (isProfileOwner(paramComponentName, paramInt)) {
      clearProfileOwnerLocked(getActiveAdminUncheckedLocked(paramComponentName, paramInt, false), paramInt);
    }
    removeAdminArtifacts(paramComponentName, paramInt);
    Slog.i("DevicePolicyManagerService", "Admin " + paramComponentName + " removed from user " + paramInt);
    this.mInjector.binderRestoreCallingIdentity(l);
  }
  
  public String[] getAccountTypesWithManagementDisabled()
  {
    return getAccountTypesWithManagementDisabledAsUser(UserHandle.getCallingUserId());
  }
  
  public String[] getAccountTypesWithManagementDisabledAsUser(int paramInt)
  {
    enforceFullCrossUsersPermission(paramInt);
    if (!this.mHasFeature) {
      return null;
    }
    try
    {
      Object localObject1 = getUserData(paramInt);
      int i = ((DevicePolicyData)localObject1).mAdminList.size();
      ArraySet localArraySet = new ArraySet();
      paramInt = 0;
      while (paramInt < i)
      {
        localArraySet.addAll(((ActiveAdmin)((DevicePolicyData)localObject1).mAdminList.get(paramInt)).accountTypesWithManagementDisabled);
        paramInt += 1;
      }
      localObject1 = (String[])localArraySet.toArray(new String[localArraySet.size()]);
      return (String[])localObject1;
    }
    finally {}
  }
  
  ActiveAdmin getActiveAdminForCallerLocked(ComponentName paramComponentName, int paramInt)
    throws SecurityException
  {
    int i = this.mInjector.binderGetCallingUid();
    ActiveAdmin localActiveAdmin = getActiveAdminWithPolicyForUidLocked(paramComponentName, paramInt, i);
    if (localActiveAdmin != null) {
      return localActiveAdmin;
    }
    if (paramComponentName != null)
    {
      paramComponentName = (ActiveAdmin)getUserData(UserHandle.getUserId(i)).mAdminMap.get(paramComponentName);
      if (paramInt == -2) {
        throw new SecurityException("Admin " + paramComponentName.info.getComponent() + " does not own the device");
      }
      if (paramInt == -1) {
        throw new SecurityException("Admin " + paramComponentName.info.getComponent() + " does not own the profile");
      }
      throw new SecurityException("Admin " + paramComponentName.info.getComponent() + " did not specify uses-policy for: " + paramComponentName.info.getTagForPolicy(paramInt));
    }
    throw new SecurityException("No active admin owned by uid " + this.mInjector.binderGetCallingUid() + " for policy #" + paramInt);
  }
  
  ActiveAdmin getActiveAdminForCallerLocked(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    throws SecurityException
  {
    if (paramBoolean) {
      enforceManagedProfile(this.mInjector.userHandleGetCallingUserId(), "call APIs on the parent profile");
    }
    ActiveAdmin localActiveAdmin = getActiveAdminForCallerLocked(paramComponentName, paramInt);
    paramComponentName = localActiveAdmin;
    if (paramBoolean) {
      paramComponentName = localActiveAdmin.getParentActiveAdmin();
    }
    return paramComponentName;
  }
  
  ActiveAdmin getActiveAdminUncheckedLocked(ComponentName paramComponentName, int paramInt)
  {
    ActiveAdmin localActiveAdmin = (ActiveAdmin)getUserData(paramInt).mAdminMap.get(paramComponentName);
    if ((localActiveAdmin != null) && (paramComponentName.getPackageName().equals(localActiveAdmin.info.getActivityInfo().packageName)) && (paramComponentName.getClassName().equals(localActiveAdmin.info.getActivityInfo().name))) {
      return localActiveAdmin;
    }
    return null;
  }
  
  ActiveAdmin getActiveAdminUncheckedLocked(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      enforceManagedProfile(paramInt, "call APIs on the parent profile");
    }
    ActiveAdmin localActiveAdmin = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
    paramComponentName = localActiveAdmin;
    if (localActiveAdmin != null)
    {
      paramComponentName = localActiveAdmin;
      if (paramBoolean) {
        paramComponentName = localActiveAdmin.getParentActiveAdmin();
      }
    }
    return paramComponentName;
  }
  
  public List<ComponentName> getActiveAdmins(int paramInt)
  {
    if (!this.mHasFeature) {
      return Collections.EMPTY_LIST;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      DevicePolicyData localDevicePolicyData = getUserData(paramInt);
      int i = localDevicePolicyData.mAdminList.size();
      if (i <= 0) {
        return null;
      }
      ArrayList localArrayList = new ArrayList(i);
      paramInt = 0;
      while (paramInt < i)
      {
        localArrayList.add(((ActiveAdmin)localDevicePolicyData.mAdminList.get(paramInt)).info.getComponent());
        paramInt += 1;
      }
      return localArrayList;
    }
    finally {}
  }
  
  /* Error */
  public String getAlwaysOnVpnPackage(ComponentName paramComponentName)
    throws SecurityException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: iconst_m1
    //   5: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   8: pop
    //   9: aload_0
    //   10: monitorexit
    //   11: aload_0
    //   12: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   15: invokevirtual 986	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:userHandleGetCallingUserId	()I
    //   18: istore_2
    //   19: aload_0
    //   20: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   23: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   26: lstore_3
    //   27: aload_0
    //   28: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   31: ldc_w 2738
    //   34: invokevirtual 2236	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   37: checkcast 2740	android/net/ConnectivityManager
    //   40: iload_2
    //   41: invokevirtual 2743	android/net/ConnectivityManager:getAlwaysOnVpnPackageForUser	(I)Ljava/lang/String;
    //   44: astore_1
    //   45: aload_0
    //   46: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   49: lload_3
    //   50: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   53: aload_1
    //   54: areturn
    //   55: astore_1
    //   56: aload_0
    //   57: monitorexit
    //   58: aload_1
    //   59: athrow
    //   60: astore_1
    //   61: aload_0
    //   62: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   65: lload_3
    //   66: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   69: aload_1
    //   70: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	71	0	this	DevicePolicyManagerService
    //   0	71	1	paramComponentName	ComponentName
    //   18	23	2	i	int
    //   26	40	3	l	long
    // Exception table:
    //   from	to	target	type
    //   2	9	55	finally
    //   27	45	60	finally
  }
  
  /* Error */
  public Bundle getApplicationRestrictions(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 2747	com/android/server/devicepolicy/DevicePolicyManagerService:enforceCanManageApplicationRestrictions	(Landroid/content/ComponentName;)V
    //   5: aload_0
    //   6: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   9: invokevirtual 2425	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderGetCallingUserHandle	()Landroid/os/UserHandle;
    //   12: astore_1
    //   13: aload_0
    //   14: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   17: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   20: lstore_3
    //   21: aload_0
    //   22: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   25: aload_2
    //   26: aload_1
    //   27: invokevirtual 2750	android/os/UserManager:getApplicationRestrictions	(Ljava/lang/String;Landroid/os/UserHandle;)Landroid/os/Bundle;
    //   30: astore_1
    //   31: aload_1
    //   32: ifnull +13 -> 45
    //   35: aload_0
    //   36: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   39: lload_3
    //   40: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   43: aload_1
    //   44: areturn
    //   45: getstatic 2753	android/os/Bundle:EMPTY	Landroid/os/Bundle;
    //   48: astore_1
    //   49: goto -14 -> 35
    //   52: astore_1
    //   53: aload_0
    //   54: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   57: lload_3
    //   58: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   61: aload_1
    //   62: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	63	0	this	DevicePolicyManagerService
    //   0	63	1	paramComponentName	ComponentName
    //   0	63	2	paramString	String
    //   20	38	3	l	long
    // Exception table:
    //   from	to	target	type
    //   21	31	52	finally
    //   45	49	52	finally
  }
  
  public String getApplicationRestrictionsManagingPackage(ComponentName paramComponentName)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      paramComponentName = getUserData(i).mApplicationRestrictionsManagingPackage;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean getAutoTimeRequired()
  {
    boolean bool = false;
    if (!this.mHasFeature) {
      return false;
    }
    try
    {
      ActiveAdmin localActiveAdmin = getDeviceOwnerAdminLocked();
      if (localActiveAdmin != null) {
        bool = localActiveAdmin.requireAutoTime;
      }
      return bool;
    }
    finally {}
  }
  
  public boolean getBluetoothContactSharingDisabled(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return false;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      boolean bool = getActiveAdminForCallerLocked(paramComponentName, -1).disableBluetoothContactSharing;
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public boolean getBluetoothContactSharingDisabledForUser(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: iload_1
    //   4: invokevirtual 1754	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwnerAdminLocked	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   7: astore_3
    //   8: aload_3
    //   9: ifnull +12 -> 21
    //   12: aload_3
    //   13: getfield 2763	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:disableBluetoothContactSharing	Z
    //   16: istore_2
    //   17: aload_0
    //   18: monitorexit
    //   19: iload_2
    //   20: ireturn
    //   21: iconst_0
    //   22: istore_2
    //   23: goto -6 -> 17
    //   26: astore_3
    //   27: aload_0
    //   28: monitorexit
    //   29: aload_3
    //   30: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	DevicePolicyManagerService
    //   0	31	1	paramInt	int
    //   16	7	2	bool	boolean
    //   7	6	3	localActiveAdmin	ActiveAdmin
    //   26	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	8	26	finally
    //   12	17	26	finally
  }
  
  public boolean getCameraDisabled(ComponentName paramComponentName, int paramInt)
  {
    return getCameraDisabled(paramComponentName, paramInt, true);
  }
  
  public String getCertInstallerPackage(ComponentName paramComponentName)
    throws SecurityException
  {
    int i = UserHandle.getCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      paramComponentName = getUserData(i).mDelegatedCertInstallerPackage;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean getCrossProfileCallerIdDisabled(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return false;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      boolean bool = getActiveAdminForCallerLocked(paramComponentName, -1).disableCallerId;
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public boolean getCrossProfileCallerIdDisabledForUser(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokespecial 2771	com/android/server/devicepolicy/DevicePolicyManagerService:enforceCrossUsersPermission	(I)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_0
    //   8: iload_1
    //   9: invokevirtual 1754	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwnerAdminLocked	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   12: astore_3
    //   13: aload_3
    //   14: ifnull +12 -> 26
    //   17: aload_3
    //   18: getfield 2769	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:disableCallerId	Z
    //   21: istore_2
    //   22: aload_0
    //   23: monitorexit
    //   24: iload_2
    //   25: ireturn
    //   26: iconst_0
    //   27: istore_2
    //   28: goto -6 -> 22
    //   31: astore_3
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_3
    //   35: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	DevicePolicyManagerService
    //   0	36	1	paramInt	int
    //   21	7	2	bool	boolean
    //   12	6	3	localActiveAdmin	ActiveAdmin
    //   31	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	31	finally
    //   17	22	31	finally
  }
  
  public boolean getCrossProfileContactsSearchDisabled(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return false;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      boolean bool = getActiveAdminForCallerLocked(paramComponentName, -1).disableContactsSearch;
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public boolean getCrossProfileContactsSearchDisabledForUser(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokespecial 2771	com/android/server/devicepolicy/DevicePolicyManagerService:enforceCrossUsersPermission	(I)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_0
    //   8: iload_1
    //   9: invokevirtual 1754	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwnerAdminLocked	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   12: astore_3
    //   13: aload_3
    //   14: ifnull +12 -> 26
    //   17: aload_3
    //   18: getfield 2775	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:disableContactsSearch	Z
    //   21: istore_2
    //   22: aload_0
    //   23: monitorexit
    //   24: iload_2
    //   25: ireturn
    //   26: iconst_0
    //   27: istore_2
    //   28: goto -6 -> 22
    //   31: astore_3
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_3
    //   35: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	36	0	this	DevicePolicyManagerService
    //   0	36	1	paramInt	int
    //   21	7	2	bool	boolean
    //   12	6	3	localActiveAdmin	ActiveAdmin
    //   31	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	31	finally
    //   17	22	31	finally
  }
  
  public List<String> getCrossProfileWidgetProviders(ComponentName paramComponentName)
  {
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1);
      if (paramComponentName.crossProfileWidgetProviders != null)
      {
        boolean bool = paramComponentName.crossProfileWidgetProviders.isEmpty();
        if (!bool) {}
      }
      else
      {
        return null;
      }
      if (this.mInjector.binderIsCallingUidMyUid())
      {
        paramComponentName = new ArrayList(paramComponentName.crossProfileWidgetProviders);
        return paramComponentName;
      }
      paramComponentName = paramComponentName.crossProfileWidgetProviders;
      return paramComponentName;
    }
    finally {}
  }
  
  public int getCurrentFailedPasswordAttempts(int paramInt, boolean paramBoolean)
  {
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      if (!isCallerWithSystemUid()) {
        getActiveAdminForCallerLocked(null, 1, paramBoolean);
      }
      paramInt = getUserDataUnchecked(getCredentialOwner(paramInt, paramBoolean)).mFailedPasswordAttempts;
      return paramInt;
    }
    finally {}
  }
  
  ActiveAdmin getDeviceOwnerAdminLocked()
  {
    ComponentName localComponentName = this.mOwners.getDeviceOwnerComponent();
    if (localComponentName == null) {
      return null;
    }
    DevicePolicyData localDevicePolicyData = getUserData(this.mOwners.getDeviceOwnerUserId());
    int j = localDevicePolicyData.mAdminList.size();
    int i = 0;
    while (i < j)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)localDevicePolicyData.mAdminList.get(i);
      if (localComponentName.equals(localActiveAdmin.info.getComponent())) {
        return localActiveAdmin;
      }
      i += 1;
    }
    Slog.wtf("DevicePolicyManagerService", "Active admin for device owner not found. component=" + localComponentName);
    return null;
  }
  
  public ComponentName getDeviceOwnerComponent(boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return null;
    }
    if (!paramBoolean) {
      enforceManageUsers();
    }
    try
    {
      boolean bool = this.mOwners.hasDeviceOwner();
      if (!bool) {
        return null;
      }
      if (paramBoolean)
      {
        int i = this.mInjector.userHandleGetCallingUserId();
        int j = this.mOwners.getDeviceOwnerUserId();
        if (i != j) {
          return null;
        }
      }
      ComponentName localComponentName = this.mOwners.getDeviceOwnerComponent();
      return localComponentName;
    }
    finally {}
  }
  
  public CharSequence getDeviceOwnerLockScreenInfo()
  {
    return this.mLockPatternUtils.getDeviceOwnerInfo();
  }
  
  public String getDeviceOwnerName()
  {
    if (!this.mHasFeature) {
      return null;
    }
    enforceManageUsers();
    try
    {
      boolean bool = this.mOwners.hasDeviceOwner();
      if (!bool) {
        return null;
      }
      String str = getApplicationLabel(this.mOwners.getDeviceOwnerPackageName(), 0);
      return str;
    }
    finally {}
  }
  
  public int getDeviceOwnerUserId()
  {
    int i = 55536;
    if (!this.mHasFeature) {
      return 55536;
    }
    enforceManageUsers();
    try
    {
      if (this.mOwners.hasDeviceOwner()) {
        i = this.mOwners.getDeviceOwnerUserId();
      }
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean getDoNotAskCredentialsOnBoot()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.QUERY_DO_NOT_ASK_CREDENTIALS_ON_BOOT", null);
    try
    {
      boolean bool = getUserData(0).doNotAskCredentialsOnBoot;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean getForceEphemeralUsers(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return false;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      boolean bool = getActiveAdminForCallerLocked(paramComponentName, -2).forceEphemeralUsers;
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public ComponentName getGlobalProxyAdmin(int paramInt)
  {
    if (!this.mHasFeature) {
      return null;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      Object localObject1 = getUserData(0);
      int i = ((DevicePolicyData)localObject1).mAdminList.size();
      paramInt = 0;
      while (paramInt < i)
      {
        ActiveAdmin localActiveAdmin = (ActiveAdmin)((DevicePolicyData)localObject1).mAdminList.get(paramInt);
        if (localActiveAdmin.specifiesGlobalProxy)
        {
          localObject1 = localActiveAdmin.info.getComponent();
          return (ComponentName)localObject1;
        }
        paramInt += 1;
      }
      return null;
    }
    finally {}
  }
  
  public List<String> getKeepUninstalledPackages(ComponentName paramComponentName)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    if (!this.mHasFeature) {
      return null;
    }
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      paramComponentName = getKeepUninstalledPackagesLocked();
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public int getKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int i = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    long l = this.mInjector.binderClearCallingIdentity();
    for (;;)
    {
      int j;
      try
      {
        if (paramComponentName != null) {}
        ActiveAdmin localActiveAdmin;
        try
        {
          paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
          paramInt = i;
          if (paramComponentName != null) {
            paramInt = paramComponentName.disabledKeyguardFeatures;
          }
          return paramInt;
        }
        finally {}
        if ((!paramBoolean) && (isManagedProfile(paramInt)))
        {
          paramComponentName = getUserDataUnchecked(paramInt).mAdminList;
          i = 0;
          int m = paramComponentName.size();
          j = 0;
          if (j >= m) {
            continue;
          }
          localActiveAdmin = (ActiveAdmin)paramComponentName.get(j);
          int n = localActiveAdmin.getUserHandle().getIdentifier();
          if ((paramBoolean) || (n != paramInt)) {
            break label240;
          }
          k = 1;
          if ((k == 0) && (isManagedProfile(n)))
          {
            i |= localActiveAdmin.disabledKeyguardFeatures & 0x30;
            break label231;
          }
        }
        else
        {
          paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
          continue;
        }
        k = localActiveAdmin.disabledKeyguardFeatures;
        i |= k;
        break label231;
        return i;
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      label231:
      j += 1;
      continue;
      label240:
      int k = 0;
    }
  }
  
  public String[] getLockTaskPackages(ComponentName paramComponentName)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      paramComponentName = getLockTaskPackagesLocked(this.mInjector.binderGetCallingUserHandle().getIdentifier());
      paramComponentName = (String[])paramComponentName.toArray(new String[paramComponentName.size()]);
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public CharSequence getLongSupportMessage(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForUidLocked(paramComponentName, this.mInjector.binderGetCallingUid()).longSupportMessage;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public CharSequence getLongSupportMessageForUser(ComponentName paramComponentName, int paramInt)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    if (!isCallerWithSystemUid()) {
      throw new SecurityException("Only the system can query support message for user");
    }
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName != null)
      {
        paramComponentName = paramComponentName.longSupportMessage;
        return paramComponentName;
      }
      return null;
    }
    finally {}
  }
  
  public int getManagedUserId(int paramInt)
  {
    Iterator localIterator = this.mUserManager.getProfiles(paramInt).iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      if ((localUserInfo.id != paramInt) && (localUserInfo.isManagedProfile())) {
        return localUserInfo.id;
      }
    }
    return -1;
  }
  
  public int getMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int i = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    if (paramComponentName != null) {}
    for (;;)
    {
      try
      {
        paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
        paramInt = i;
        if (paramComponentName != null) {
          paramInt = paramComponentName.maximumFailedPasswordsForWipe;
        }
        return paramInt;
      }
      finally {}
      paramComponentName = getAdminWithMinimumFailedPasswordsForWipeLocked(paramInt, paramBoolean);
    }
  }
  
  public long getMaximumTimeToLock(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    long l = 0L;
    if (!this.mHasFeature) {
      return 0L;
    }
    enforceFullCrossUsersPermission(paramInt);
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      if (paramComponentName != null) {
        l = paramComponentName.maximumTimeToUnlock;
      }
      return l;
    }
    finally {}
    l = getMaximumTimeToLockPolicyFromAdmins(getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean));
    return l;
  }
  
  public long getMaximumTimeToLockForUserAndProfiles(int paramInt)
  {
    if (!this.mHasFeature) {
      return 0L;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.mUserManager.getProfiles(paramInt).iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = (UserInfo)localIterator.next();
        Object localObject2 = getUserData(((UserInfo)localObject1).id);
        localArrayList.addAll(((DevicePolicyData)localObject2).mAdminList);
        if (((UserInfo)localObject1).isManagedProfile())
        {
          localObject1 = ((DevicePolicyData)localObject2).mAdminList.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (ActiveAdmin)((Iterator)localObject1).next();
            if (((ActiveAdmin)localObject2).hasParentActiveAdmin()) {
              localArrayList.add(((ActiveAdmin)localObject2).getParentActiveAdmin());
            }
          }
        }
      }
      l = getMaximumTimeToLockPolicyFromAdmins(localList);
    }
    finally {}
    long l;
    return l;
  }
  
  public int getOrganizationColor(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return ActiveAdmin.DEF_ORGANIZATION_COLOR;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    enforceManagedProfile(this.mInjector.userHandleGetCallingUserId(), "get organization color");
    try
    {
      int i = getActiveAdminForCallerLocked(paramComponentName, -1).organizationColor;
      return i;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public int getOrganizationColorForUser(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +7 -> 11
    //   7: getstatic 2840	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:DEF_ORGANIZATION_COLOR	I
    //   10: ireturn
    //   11: aload_0
    //   12: iload_1
    //   13: invokespecial 2160	com/android/server/devicepolicy/DevicePolicyManagerService:enforceFullCrossUsersPermission	(I)V
    //   16: aload_0
    //   17: iload_1
    //   18: ldc_w 2842
    //   21: invokespecial 2724	com/android/server/devicepolicy/DevicePolicyManagerService:enforceManagedProfile	(ILjava/lang/String;)V
    //   24: aload_0
    //   25: monitorenter
    //   26: aload_0
    //   27: iload_1
    //   28: invokevirtual 1754	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwnerAdminLocked	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   31: astore_2
    //   32: aload_2
    //   33: ifnull +12 -> 45
    //   36: aload_2
    //   37: getfield 2845	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:organizationColor	I
    //   40: istore_1
    //   41: aload_0
    //   42: monitorexit
    //   43: iload_1
    //   44: ireturn
    //   45: getstatic 2840	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:DEF_ORGANIZATION_COLOR	I
    //   48: istore_1
    //   49: goto -8 -> 41
    //   52: astore_2
    //   53: aload_0
    //   54: monitorexit
    //   55: aload_2
    //   56: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	57	0	this	DevicePolicyManagerService
    //   0	57	1	paramInt	int
    //   31	6	2	localActiveAdmin	ActiveAdmin
    //   52	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   26	32	52	finally
    //   36	41	52	finally
    //   45	49	52	finally
  }
  
  public CharSequence getOrganizationName(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    enforceManagedProfile(this.mInjector.userHandleGetCallingUserId(), "get organization name");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1).organizationName;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public CharSequence getOrganizationNameForUser(int paramInt)
  {
    String str = null;
    if (!this.mHasFeature) {
      return null;
    }
    enforceFullCrossUsersPermission(paramInt);
    enforceManagedProfile(paramInt, "get organization name");
    try
    {
      ActiveAdmin localActiveAdmin = getProfileOwnerAdminLocked(paramInt);
      if (localActiveAdmin != null) {
        str = localActiveAdmin.organizationName;
      }
      return str;
    }
    finally {}
  }
  
  public long getPasswordExpiration(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return 0L;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      long l = getPasswordExpirationLocked(paramComponentName, paramInt, paramBoolean);
      return l;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public long getPasswordExpirationTimeout(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    long l1 = 0L;
    if (!this.mHasFeature) {
      return 0L;
    }
    enforceFullCrossUsersPermission(paramInt);
    long l2 = 0L;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      if (paramComponentName != null) {
        l1 = paramComponentName.passwordExpirationTimeout;
      }
      return l1;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int i = paramComponentName.size();
    paramInt = 0;
    for (l1 = l2; paramInt < i; l1 = l2)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
      if (l1 != 0L)
      {
        l2 = l1;
        if (localActiveAdmin.passwordExpirationTimeout != 0L)
        {
          l2 = l1;
          if (l1 <= localActiveAdmin.passwordExpirationTimeout) {}
        }
      }
      else
      {
        l2 = localActiveAdmin.passwordExpirationTimeout;
      }
      paramInt += 1;
    }
    return l1;
  }
  
  public int getPasswordHistoryLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.passwordHistoryLength;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    while (paramInt < k)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
      j = i;
      if (i < localActiveAdmin.passwordHistoryLength) {
        j = localActiveAdmin.passwordHistoryLength;
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }
  
  public int getPasswordMinimumLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.minimumPasswordLength;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    while (paramInt < k)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
      j = i;
      if (i < localActiveAdmin.minimumPasswordLength) {
        j = localActiveAdmin.minimumPasswordLength;
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }
  
  public int getPasswordMinimumLetters(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.minimumPasswordLetters;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    for (;;)
    {
      if (paramInt < k)
      {
        ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
        if (!isLimitPasswordAllowed(localActiveAdmin, 393216))
        {
          j = i;
        }
        else
        {
          j = i;
          if (i < localActiveAdmin.minimumPasswordLetters) {
            j = localActiveAdmin.minimumPasswordLetters;
          }
        }
      }
      else
      {
        return i;
      }
      paramInt += 1;
      i = j;
    }
  }
  
  public int getPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.minimumPasswordLowerCase;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    while (paramInt < k)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
      j = i;
      if (i < localActiveAdmin.minimumPasswordLowerCase) {
        j = localActiveAdmin.minimumPasswordLowerCase;
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }
  
  public int getPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.minimumPasswordNonLetter;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    for (;;)
    {
      if (paramInt < k)
      {
        ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
        if (!isLimitPasswordAllowed(localActiveAdmin, 393216))
        {
          j = i;
        }
        else
        {
          j = i;
          if (i < localActiveAdmin.minimumPasswordNonLetter) {
            j = localActiveAdmin.minimumPasswordNonLetter;
          }
        }
      }
      else
      {
        return i;
      }
      paramInt += 1;
      i = j;
    }
  }
  
  public int getPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.minimumPasswordNumeric;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    for (;;)
    {
      if (paramInt < k)
      {
        ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
        if (!isLimitPasswordAllowed(localActiveAdmin, 393216))
        {
          j = i;
        }
        else
        {
          j = i;
          if (i < localActiveAdmin.minimumPasswordNumeric) {
            j = localActiveAdmin.minimumPasswordNumeric;
          }
        }
      }
      else
      {
        return i;
      }
      paramInt += 1;
      i = j;
    }
  }
  
  public int getPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.minimumPasswordSymbols;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    for (;;)
    {
      if (paramInt < k)
      {
        ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
        if (!isLimitPasswordAllowed(localActiveAdmin, 393216))
        {
          j = i;
        }
        else
        {
          j = i;
          if (i < localActiveAdmin.minimumPasswordSymbols) {
            j = localActiveAdmin.minimumPasswordSymbols;
          }
        }
      }
      else
      {
        return i;
      }
      paramInt += 1;
      i = j;
    }
  }
  
  public int getPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.minimumPasswordUpperCase;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    while (paramInt < k)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
      j = i;
      if (i < localActiveAdmin.minimumPasswordUpperCase) {
        j = localActiveAdmin.minimumPasswordUpperCase;
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }
  
  public int getPasswordQuality(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    int j = 0;
    if (!this.mHasFeature) {
      return 0;
    }
    enforceFullCrossUsersPermission(paramInt);
    int i = 0;
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      paramInt = j;
      if (paramComponentName != null) {
        paramInt = paramComponentName.passwordQuality;
      }
      return paramInt;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    int k = paramComponentName.size();
    paramInt = 0;
    while (paramInt < k)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)paramComponentName.get(paramInt);
      j = i;
      if (i < localActiveAdmin.passwordQuality) {
        j = localActiveAdmin.passwordQuality;
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }
  
  /* Error */
  public int getPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2)
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   4: invokevirtual 502	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   7: astore 8
    //   9: aload_0
    //   10: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   13: invokevirtual 2425	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderGetCallingUserHandle	()Landroid/os/UserHandle;
    //   16: astore 9
    //   18: aload_0
    //   19: monitorenter
    //   20: aload_0
    //   21: aload_1
    //   22: iconst_m1
    //   23: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   26: pop
    //   27: aload_0
    //   28: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   31: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   34: lstore 6
    //   36: aload_0
    //   37: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   40: aload_3
    //   41: aload_2
    //   42: aload 9
    //   44: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   47: invokeinterface 2889 4 0
    //   52: istore 4
    //   54: aload 8
    //   56: aload_3
    //   57: aload_2
    //   58: aload 9
    //   60: invokevirtual 2893	android/content/pm/PackageManager:getPermissionFlags	(Ljava/lang/String;Ljava/lang/String;Landroid/os/UserHandle;)I
    //   63: istore 5
    //   65: iload 5
    //   67: iconst_4
    //   68: iand
    //   69: iconst_4
    //   70: if_icmpeq +16 -> 86
    //   73: aload_0
    //   74: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   77: lload 6
    //   79: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   82: aload_0
    //   83: monitorexit
    //   84: iconst_0
    //   85: ireturn
    //   86: iload 4
    //   88: ifne +20 -> 108
    //   91: iconst_1
    //   92: istore 4
    //   94: aload_0
    //   95: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   98: lload 6
    //   100: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   103: aload_0
    //   104: monitorexit
    //   105: iload 4
    //   107: ireturn
    //   108: iconst_2
    //   109: istore 4
    //   111: goto -17 -> 94
    //   114: astore_1
    //   115: aload_0
    //   116: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   119: lload 6
    //   121: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   124: aload_1
    //   125: athrow
    //   126: astore_1
    //   127: aload_0
    //   128: monitorexit
    //   129: aload_1
    //   130: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	131	0	this	DevicePolicyManagerService
    //   0	131	1	paramComponentName	ComponentName
    //   0	131	2	paramString1	String
    //   0	131	3	paramString2	String
    //   52	58	4	i	int
    //   63	6	5	j	int
    //   34	86	6	l	long
    //   7	48	8	localPackageManager	PackageManager
    //   16	43	9	localUserHandle	UserHandle
    // Exception table:
    //   from	to	target	type
    //   36	65	114	finally
    //   20	36	126	finally
    //   73	82	126	finally
    //   94	103	126	finally
    //   115	126	126	finally
  }
  
  public int getPermissionPolicy(ComponentName paramComponentName)
    throws RemoteException
  {
    int i = UserHandle.getCallingUserId();
    try
    {
      i = getUserData(i).mPermissionPolicy;
      return i;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public List getPermittedAccessibilityServices(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1).permittedAccessiblityServices;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public List getPermittedAccessibilityServicesForUser(int paramInt)
  {
    if (!this.mHasFeature) {
      return null;
    }
    ArrayList localArrayList = null;
    int j;
    label282:
    for (;;)
    {
      Object localObject3;
      int i;
      Object localObject4;
      List localList2;
      try
      {
        localObject3 = this.mUserManager.getProfileIdsWithDisabled(paramInt);
        i = 0;
        int k = localObject3.length;
        if (i < k)
        {
          localObject4 = getUserDataUnchecked(localObject3[i]);
          int m = ((DevicePolicyData)localObject4).mAdminList.size();
          j = 0;
          if (j >= m) {}
        }
      }
      finally {}
      try
      {
        localList2 = ((ActiveAdmin)((DevicePolicyData)localObject4).mAdminList.get(j)).permittedAccessiblityServices;
        if (localList2 == null) {
          break label282;
        }
        if (localArrayList == null) {
          localArrayList = new ArrayList(localList2);
        } else {
          localArrayList.retainAll(localList2);
        }
      }
      finally
      {
        continue;
        j += 1;
      }
      i += 1;
      continue;
      if (localArrayList != null)
      {
        long l = this.mInjector.binderClearCallingIdentity();
        try
        {
          localObject3 = getUserInfo(paramInt);
          if (((UserInfo)localObject3).isManagedProfile()) {
            paramInt = ((UserInfo)localObject3).profileGroupId;
          }
          localObject3 = getAccessibilityManagerForUser(paramInt).getInstalledAccessibilityServiceList();
          if (localObject3 != null)
          {
            localObject3 = ((Iterable)localObject3).iterator();
            if (((Iterator)localObject3).hasNext())
            {
              localObject4 = ((AccessibilityServiceInfo)((Iterator)localObject3).next()).getResolveInfo().serviceInfo;
              if ((((ServiceInfo)localObject4).applicationInfo.flags & 0x1) == 0) {
                continue;
              }
              localArrayList.add(((ServiceInfo)localObject4).packageName);
              continue;
              localList1 = finally;
            }
          }
        }
        finally
        {
          this.mInjector.binderRestoreCallingIdentity(l);
        }
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      return localList1;
    }
  }
  
  public List getPermittedInputMethods(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1).permittedInputMethods;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public List getPermittedInputMethodsForCurrentUser()
  {
    Object localObject4;
    Object localObject5;
    for (;;)
    {
      int i;
      List localList2;
      try
      {
        localObject1 = this.mInjector.getIActivityManager().getCurrentUser();
        i = ((UserInfo)localObject1).id;
        localObject1 = null;
      }
      catch (RemoteException localRemoteException)
      {
        Object localObject1;
        int k;
        int j;
        Slog.e("DevicePolicyManagerService", "Failed to make remote calls to get current user", localRemoteException);
        return null;
      }
      for (;;)
      {
        long l;
        try
        {
          localObject4 = this.mUserManager.getProfileIdsWithDisabled(i);
          i = 0;
          k = localObject4.length;
          if (i < k)
          {
            localObject5 = getUserDataUnchecked(localObject4[i]);
            int m = ((DevicePolicyData)localObject5).mAdminList.size();
            j = 0;
            if (j >= m) {}
          }
        }
        finally {}
        try
        {
          localList2 = ((ActiveAdmin)((DevicePolicyData)localObject5).mAdminList.get(j)).permittedInputMethods;
          if (localList2 == null) {
            break label294;
          }
          if (localObject1 != null) {
            continue;
          }
          localObject1 = new ArrayList(localList2);
        }
        finally
        {
          continue;
          break;
        }
      }
      j += 1;
      continue;
      localRemoteException.retainAll(localList2);
      continue;
      i += 1;
    }
    if (localRemoteException != null)
    {
      localObject4 = ((InputMethodManager)this.mContext.getSystemService(InputMethodManager.class)).getInputMethodList();
      l = this.mInjector.binderClearCallingIdentity();
      if (localObject4 != null) {
        try
        {
          localObject4 = ((Iterable)localObject4).iterator();
          while (((Iterator)localObject4).hasNext())
          {
            localObject5 = ((InputMethodInfo)((Iterator)localObject4).next()).getServiceInfo();
            if ((((ServiceInfo)localObject5).applicationInfo.flags & 0x1) != 0)
            {
              localRemoteException.add(((ServiceInfo)localObject5).packageName);
              continue;
              localList1 = finally;
            }
          }
        }
        finally
        {
          this.mInjector.binderRestoreCallingIdentity(l);
        }
      }
      this.mInjector.binderRestoreCallingIdentity(l);
    }
    return localList1;
  }
  
  public ComponentName getProfileOwner(int paramInt)
  {
    if (!this.mHasFeature) {
      return null;
    }
    try
    {
      ComponentName localComponentName = this.mOwners.getProfileOwnerComponent(paramInt);
      return localComponentName;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  ActiveAdmin getProfileOwnerAdminLocked(int paramInt)
  {
    ComponentName localComponentName = this.mOwners.getProfileOwnerComponent(paramInt);
    if (localComponentName == null) {
      return null;
    }
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    int i = localDevicePolicyData.mAdminList.size();
    paramInt = 0;
    while (paramInt < i)
    {
      ActiveAdmin localActiveAdmin = (ActiveAdmin)localDevicePolicyData.mAdminList.get(paramInt);
      if (localComponentName.equals(localActiveAdmin.info.getComponent())) {
        return localActiveAdmin;
      }
      paramInt += 1;
    }
    return null;
  }
  
  public String getProfileOwnerName(int paramInt)
  {
    if (!this.mHasFeature) {
      return null;
    }
    enforceManageUsers();
    ComponentName localComponentName = getProfileOwner(paramInt);
    if (localComponentName == null) {
      return null;
    }
    return getApplicationLabel(localComponentName.getPackageName(), paramInt);
  }
  
  public int getProfileWithMinimumFailedPasswordsForWipe(int paramInt, boolean paramBoolean)
  {
    int i = 55536;
    if (!this.mHasFeature) {
      return 55536;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      ActiveAdmin localActiveAdmin = getAdminWithMinimumFailedPasswordsForWipeLocked(paramInt, paramBoolean);
      paramInt = i;
      if (localActiveAdmin != null) {
        paramInt = localActiveAdmin.getUserHandle().getIdentifier();
      }
      return paramInt;
    }
    finally {}
  }
  
  public void getRemoveWarning(ComponentName paramComponentName, final RemoteCallback paramRemoteCallback, int paramInt)
  {
    if (!this.mHasFeature) {
      return;
    }
    enforceFullCrossUsersPermission(paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", null);
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName == null)
      {
        paramRemoteCallback.sendResult(null);
        return;
      }
      Intent localIntent = new Intent("android.app.action.DEVICE_ADMIN_DISABLE_REQUESTED");
      localIntent.setFlags(268435456);
      localIntent.setComponent(paramComponentName.info.getComponent());
      this.mContext.sendOrderedBroadcastAsUser(localIntent, new UserHandle(paramInt), null, new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          paramRemoteCallback.sendResult(getResultExtras(false));
        }
      }, null, -1, null, null);
      return;
    }
    finally {}
  }
  
  public long getRequiredStrongAuthTimeout(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    long l1 = 0L;
    if (!this.mHasFeature) {
      return 259200000L;
    }
    enforceFullCrossUsersPermission(paramInt);
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt, paramBoolean);
      if (paramComponentName != null) {
        l1 = paramComponentName.strongAuthUnlockTimeout;
      }
      return l1;
    }
    finally {}
    paramComponentName = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
    l1 = 259200000L;
    paramInt = 0;
    for (;;)
    {
      long l2;
      if (paramInt < paramComponentName.size())
      {
        long l3 = ((ActiveAdmin)paramComponentName.get(paramInt)).strongAuthUnlockTimeout;
        l2 = l1;
        if (l3 != 0L) {
          l2 = Math.min(l3, l1);
        }
      }
      else
      {
        l1 = Math.max(l1, 3600000L);
        return l1;
      }
      paramInt += 1;
      l1 = l2;
    }
  }
  
  public ComponentName getRestrictionsProvider(int paramInt)
  {
    Object localObject1 = null;
    try
    {
      if (!isCallerWithSystemUid()) {
        throw new SecurityException("Only the system can query the permission provider");
      }
    }
    finally {}
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    ComponentName localComponentName;
    if (localDevicePolicyData != null) {
      localComponentName = localDevicePolicyData.mRestrictionsProvider;
    }
    return localComponentName;
  }
  
  public boolean getScreenCaptureDisabled(ComponentName paramComponentName, int paramInt)
  {
    boolean bool = false;
    if (!this.mHasFeature) {
      return false;
    }
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName != null) {
        bool = paramComponentName.disableScreenCapture;
      }
      return bool;
    }
    finally {}
    paramComponentName = getUserData(paramInt);
    int i = paramComponentName.mAdminList.size();
    paramInt = 0;
    while (paramInt < i)
    {
      bool = ((ActiveAdmin)paramComponentName.mAdminList.get(paramInt)).disableScreenCapture;
      if (bool) {
        return true;
      }
      paramInt += 1;
    }
    return false;
  }
  
  public CharSequence getShortSupportMessage(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForUidLocked(paramComponentName, this.mInjector.binderGetCallingUid()).shortSupportMessage;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public CharSequence getShortSupportMessageForUser(ComponentName paramComponentName, int paramInt)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    if (!isCallerWithSystemUid()) {
      throw new SecurityException("Only the system can query support message for user");
    }
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName != null)
      {
        paramComponentName = paramComponentName.shortSupportMessage;
        return paramComponentName;
      }
      return null;
    }
    finally {}
  }
  
  public boolean getStorageEncryption(ComponentName paramComponentName, int paramInt)
  {
    if (!this.mHasFeature) {
      return false;
    }
    enforceFullCrossUsersPermission(paramInt);
    if (paramComponentName != null) {}
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName != null) {}
      for (boolean bool = paramComponentName.encryptionRequested;; bool = false) {
        return bool;
      }
      paramComponentName = getUserData(paramInt);
      int i = paramComponentName.mAdminList.size();
      paramInt = 0;
      while (paramInt < i)
      {
        bool = ((ActiveAdmin)paramComponentName.mAdminList.get(paramInt)).encryptionRequested;
        if (bool) {
          return true;
        }
        paramInt += 1;
      }
      return false;
    }
    finally {}
  }
  
  public int getStorageEncryptionStatus(String paramString, int paramInt)
  {
    if (!this.mHasFeature) {}
    enforceFullCrossUsersPermission(paramInt);
    ensureCallerPackage(paramString);
    int i;
    try
    {
      paramString = this.mIPackageManager.getApplicationInfo(paramString, 0, paramInt);
      paramInt = 0;
      if (paramString.targetSdkVersion <= 23) {
        paramInt = 1;
      }
      i = getEncryptionStatus();
      if ((i == 5) && (paramInt != 0)) {
        return 3;
      }
    }
    catch (RemoteException paramString)
    {
      throw new SecurityException(paramString);
    }
    return i;
  }
  
  public SystemUpdatePolicy getSystemUpdatePolicy()
  {
    if (UserManager.isDeviceInDemoMode(this.mContext)) {
      return SystemUpdatePolicy.createAutomaticInstallPolicy();
    }
    try
    {
      SystemUpdatePolicy localSystemUpdatePolicy = this.mOwners.getSystemUpdatePolicy();
      if (localSystemUpdatePolicy != null)
      {
        boolean bool = localSystemUpdatePolicy.isValid();
        if (!bool) {}
      }
      else
      {
        return localSystemUpdatePolicy;
      }
      Slog.w("DevicePolicyManagerService", "Stored system update policy is invalid, return null instead.");
      return null;
    }
    finally {}
  }
  
  public List<PersistableBundle> getTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName2, "agent null");
    enforceFullCrossUsersPermission(paramInt);
    for (;;)
    {
      try
      {
        String str = paramComponentName2.flattenToString();
        if (paramComponentName1 != null)
        {
          paramComponentName1 = getActiveAdminUncheckedLocked(paramComponentName1, paramInt, paramBoolean);
          if (paramComponentName1 == null) {
            return null;
          }
          paramComponentName1 = (DevicePolicyManagerService.ActiveAdmin.TrustAgentInfo)paramComponentName1.trustAgentInfos.get(str);
          if (paramComponentName1 != null)
          {
            paramComponentName2 = paramComponentName1.options;
            if (paramComponentName2 != null) {}
          }
          else
          {
            return null;
          }
          paramComponentName2 = new ArrayList();
          paramComponentName2.add(paramComponentName1.options);
          return paramComponentName2;
        }
        paramComponentName1 = null;
        List localList = getActiveAdminsForLockscreenPoliciesLocked(paramInt, paramBoolean);
        int j = 1;
        int k = localList.size();
        paramInt = 0;
        int i = j;
        DevicePolicyManagerService.ActiveAdmin.TrustAgentInfo localTrustAgentInfo;
        if (paramInt < k)
        {
          paramComponentName2 = (ActiveAdmin)localList.get(paramInt);
          if ((paramComponentName2.disabledKeyguardFeatures & 0x10) != 0)
          {
            i = 1;
            localTrustAgentInfo = (DevicePolicyManagerService.ActiveAdmin.TrustAgentInfo)paramComponentName2.trustAgentInfos.get(str);
            if ((localTrustAgentInfo != null) && (localTrustAgentInfo.options != null))
            {
              paramBoolean = localTrustAgentInfo.options.isEmpty();
              if (!paramBoolean) {
                continue;
              }
            }
            paramComponentName2 = paramComponentName1;
            if (i == 0) {
              break label321;
            }
            i = 0;
          }
        }
        else
        {
          if (i == 0) {
            break label316;
          }
          return paramComponentName1;
        }
        i = 0;
        continue;
        if (i != 0)
        {
          paramComponentName2 = paramComponentName1;
          if (paramComponentName1 == null) {
            paramComponentName2 = new ArrayList();
          }
          paramComponentName2.add(localTrustAgentInfo.options);
        }
        else
        {
          Log.w("DevicePolicyManagerService", "Ignoring admin " + paramComponentName2.info + " because it has trust options but doesn't declare " + "KEYGUARD_DISABLE_TRUST_AGENTS");
          paramComponentName2 = paramComponentName1;
        }
      }
      finally {}
      label316:
      paramComponentName1 = null;
      continue;
      label321:
      paramInt += 1;
      paramComponentName1 = paramComponentName2;
    }
  }
  
  DevicePolicyData getUserData(int paramInt)
  {
    try
    {
      DevicePolicyData localDevicePolicyData2 = (DevicePolicyData)this.mUserData.get(paramInt);
      DevicePolicyData localDevicePolicyData1 = localDevicePolicyData2;
      if (localDevicePolicyData2 == null)
      {
        localDevicePolicyData1 = new DevicePolicyData(paramInt);
        this.mUserData.append(paramInt, localDevicePolicyData1);
        loadSettingsLocked(localDevicePolicyData1, paramInt);
      }
      return localDevicePolicyData1;
    }
    finally {}
  }
  
  DevicePolicyData getUserDataUnchecked(int paramInt)
  {
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      DevicePolicyData localDevicePolicyData = getUserData(paramInt);
      return localDevicePolicyData;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  public int getUserProvisioningState()
  {
    if (!this.mHasFeature) {
      return 0;
    }
    return getUserProvisioningState(this.mInjector.userHandleGetCallingUserId());
  }
  
  public Bundle getUserRestrictions(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return null;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1).userRestrictions;
      return paramComponentName;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public String getWifiMacAddress(ComponentName paramComponentName)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: monitorenter
    //   5: aload_0
    //   6: aload_1
    //   7: bipush -2
    //   9: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   12: pop
    //   13: aload_0
    //   14: monitorexit
    //   15: aload_0
    //   16: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   19: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   22: lstore_2
    //   23: aload_0
    //   24: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   27: invokevirtual 3041	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getWifiManager	()Landroid/net/wifi/WifiManager;
    //   30: invokevirtual 3047	android/net/wifi/WifiManager:getConnectionInfo	()Landroid/net/wifi/WifiInfo;
    //   33: astore 5
    //   35: aload 5
    //   37: ifnonnull +18 -> 55
    //   40: aload_0
    //   41: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   44: lload_2
    //   45: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   48: aconst_null
    //   49: areturn
    //   50: astore_1
    //   51: aload_0
    //   52: monitorexit
    //   53: aload_1
    //   54: athrow
    //   55: aload 4
    //   57: astore_1
    //   58: aload 5
    //   60: invokevirtual 3052	android/net/wifi/WifiInfo:hasRealMacAddress	()Z
    //   63: ifeq +9 -> 72
    //   66: aload 5
    //   68: invokevirtual 3055	android/net/wifi/WifiInfo:getMacAddress	()Ljava/lang/String;
    //   71: astore_1
    //   72: aload_0
    //   73: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   76: lload_2
    //   77: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   80: aload_1
    //   81: areturn
    //   82: astore_1
    //   83: aload_0
    //   84: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   87: lload_2
    //   88: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   91: aload_1
    //   92: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	this	DevicePolicyManagerService
    //   0	93	1	paramComponentName	ComponentName
    //   22	66	2	l	long
    //   1	55	4	localObject	Object
    //   33	34	5	localWifiInfo	android.net.wifi.WifiInfo
    // Exception table:
    //   from	to	target	type
    //   5	13	50	finally
    //   23	35	82	finally
    //   58	72	82	finally
  }
  
  public boolean hasGrantedPolicy(ComponentName paramComponentName, int paramInt1, int paramInt2)
  {
    if (!this.mHasFeature) {
      return false;
    }
    enforceFullCrossUsersPermission(paramInt2);
    ActiveAdmin localActiveAdmin;
    try
    {
      localActiveAdmin = getActiveAdminUncheckedLocked(paramComponentName, paramInt2);
      if (localActiveAdmin == null) {
        throw new SecurityException("No active admin " + paramComponentName);
      }
    }
    finally {}
    boolean bool = localActiveAdmin.info.usesPolicy(paramInt1);
    return bool;
  }
  
  public boolean hasUserSetupCompleted()
  {
    return hasUserSetupCompleted(UserHandle.getCallingUserId());
  }
  
  /* Error */
  public boolean installCaCert(ComponentName paramComponentName, byte[] paramArrayOfByte)
    throws RemoteException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokevirtual 3063	com/android/server/devicepolicy/DevicePolicyManagerService:enforceCanManageCaCerts	(Landroid/content/ComponentName;)V
    //   5: iconst_1
    //   6: anewarray 3065	java/security/cert/Certificate
    //   9: dup
    //   10: iconst_0
    //   11: aload_2
    //   12: invokestatic 3067	com/android/server/devicepolicy/DevicePolicyManagerService:parseCert	([B)Ljava/security/cert/X509Certificate;
    //   15: aastore
    //   16: invokestatic 3073	android/security/Credentials:convertToPem	([Ljava/security/cert/Certificate;)[B
    //   19: astore_2
    //   20: new 536	android/os/UserHandle
    //   23: dup
    //   24: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   27: invokespecial 674	android/os/UserHandle:<init>	(I)V
    //   30: astore_1
    //   31: aload_0
    //   32: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   35: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   38: lstore_3
    //   39: aload_0
    //   40: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   43: aload_1
    //   44: invokestatic 3079	android/security/KeyChain:bindAsUser	(Landroid/content/Context;Landroid/os/UserHandle;)Landroid/security/KeyChain$KeyChainConnection;
    //   47: astore_1
    //   48: aload_1
    //   49: invokevirtual 3084	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
    //   52: aload_2
    //   53: invokeinterface 3089 2 0
    //   58: aload_1
    //   59: invokevirtual 3090	android/security/KeyChain$KeyChainConnection:close	()V
    //   62: aload_0
    //   63: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   66: lload_3
    //   67: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   70: iconst_1
    //   71: ireturn
    //   72: astore_1
    //   73: ldc 125
    //   75: ldc_w 3092
    //   78: aload_1
    //   79: invokestatic 3093	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   82: pop
    //   83: iconst_0
    //   84: ireturn
    //   85: astore_1
    //   86: ldc 125
    //   88: ldc_w 3095
    //   91: aload_1
    //   92: invokestatic 3093	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   95: pop
    //   96: iconst_0
    //   97: ireturn
    //   98: astore_2
    //   99: ldc 125
    //   101: ldc_w 3097
    //   104: aload_2
    //   105: invokestatic 3093	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   108: pop
    //   109: aload_1
    //   110: invokevirtual 3090	android/security/KeyChain$KeyChainConnection:close	()V
    //   113: aload_0
    //   114: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   117: lload_3
    //   118: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   121: iconst_0
    //   122: ireturn
    //   123: astore_2
    //   124: aload_1
    //   125: invokevirtual 3090	android/security/KeyChain$KeyChainConnection:close	()V
    //   128: aload_2
    //   129: athrow
    //   130: astore_1
    //   131: ldc 125
    //   133: ldc_w 3097
    //   136: aload_1
    //   137: invokestatic 1172	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   140: pop
    //   141: invokestatic 3103	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   144: invokevirtual 3106	java/lang/Thread:interrupt	()V
    //   147: aload_0
    //   148: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   151: lload_3
    //   152: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   155: goto -34 -> 121
    //   158: astore_1
    //   159: aload_0
    //   160: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   163: lload_3
    //   164: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   167: aload_1
    //   168: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	this	DevicePolicyManagerService
    //   0	169	1	paramComponentName	ComponentName
    //   0	169	2	paramArrayOfByte	byte[]
    //   38	126	3	l	long
    // Exception table:
    //   from	to	target	type
    //   5	20	72	java/io/IOException
    //   5	20	85	java/security/cert/CertificateException
    //   48	58	98	android/os/RemoteException
    //   48	58	123	finally
    //   99	109	123	finally
    //   39	48	130	java/lang/InterruptedException
    //   58	62	130	java/lang/InterruptedException
    //   109	113	130	java/lang/InterruptedException
    //   124	130	130	java/lang/InterruptedException
    //   39	48	158	finally
    //   58	62	158	finally
    //   109	113	158	finally
    //   124	130	158	finally
    //   131	147	158	finally
  }
  
  public boolean installKeyPair(ComponentName paramComponentName, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, String paramString, boolean paramBoolean)
  {
    enforceCanManageInstalledKeys(paramComponentName);
    int i = this.mInjector.binderGetCallingUid();
    l = this.mInjector.binderClearCallingIdentity();
    for (;;)
    {
      try
      {
        paramComponentName = KeyChain.bindAsUser(this.mContext, UserHandle.getUserHandleForUid(i));
      }
      catch (InterruptedException paramComponentName)
      {
        IKeyChainService localIKeyChainService;
        boolean bool;
        Log.w("DevicePolicyManagerService", "Interrupted while installing certificate", paramComponentName);
        Thread.currentThread().interrupt();
        this.mInjector.binderRestoreCallingIdentity(l);
        continue;
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      try
      {
        localIKeyChainService = paramComponentName.getService();
        bool = localIKeyChainService.installKeyPair(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramString);
        if (!bool)
        {
          paramComponentName.close();
          this.mInjector.binderRestoreCallingIdentity(l);
          return false;
        }
        if (paramBoolean) {
          localIKeyChainService.setGrant(i, paramString, true);
        }
        paramComponentName.close();
        this.mInjector.binderRestoreCallingIdentity(l);
        return true;
      }
      catch (RemoteException paramArrayOfByte1)
      {
        Log.e("DevicePolicyManagerService", "Installing certificate", paramArrayOfByte1);
        paramComponentName.close();
        this.mInjector.binderRestoreCallingIdentity(l);
        return false;
      }
      finally
      {
        paramComponentName.close();
      }
    }
  }
  
  public boolean isAccessibilityServicePermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
  {
    if (!this.mHasFeature) {
      return true;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    Preconditions.checkStringNotEmpty(paramString, "packageName is null");
    if (!isCallerWithSystemUid()) {
      throw new SecurityException("Only the system can query if an accessibility service is disabled by admin");
    }
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName == null) {
        return false;
      }
      List localList = paramComponentName.permittedAccessiblityServices;
      if (localList == null) {
        return true;
      }
      boolean bool = checkPackagesInPermittedListOrSystem(Arrays.asList(new String[] { paramString }), paramComponentName.permittedAccessiblityServices, paramInt);
      return bool;
    }
    finally {}
  }
  
  boolean isActiveAdminWithPolicyForUserLocked(ActiveAdmin paramActiveAdmin, int paramInt1, int paramInt2)
  {
    boolean bool1 = isDeviceOwner(paramActiveAdmin.info.getComponent(), paramInt2);
    boolean bool2 = isProfileOwner(paramActiveAdmin.info.getComponent(), paramInt2);
    if (paramInt1 == -2) {
      return bool1;
    }
    if (paramInt1 == -1)
    {
      if (!bool1) {
        return bool2;
      }
      return true;
    }
    return paramActiveAdmin.info.usesPolicy(paramInt1);
  }
  
  public boolean isActivePasswordSufficient(int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return true;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      getActiveAdminForCallerLocked(null, 0, paramBoolean);
      paramBoolean = isActivePasswordSufficientForUserLocked(getUserDataUnchecked(getCredentialOwner(paramInt, paramBoolean)), paramInt, paramBoolean);
      return paramBoolean;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isAdminActive(ComponentName paramComponentName, int paramInt)
  {
    boolean bool = false;
    if (!this.mHasFeature) {
      return false;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName != null) {
        bool = true;
      }
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean isAffiliatedUser()
  {
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      int j = this.mOwners.getDeviceOwnerUserId();
      if (j == i) {
        return true;
      }
      Object localObject1 = getProfileOwner(i);
      Object localObject3;
      if ((localObject1 != null) && (((ComponentName)localObject1).getPackageName().equals(this.mOwners.getDeviceOwnerPackageName())))
      {
        localObject3 = getUserData(i).mAffiliationIds;
        localObject1 = getUserData(0).mAffiliationIds;
        localObject3 = ((Iterable)localObject3).iterator();
      }
      while (((Iterator)localObject3).hasNext())
      {
        boolean bool = ((Set)localObject1).contains((String)((Iterator)localObject3).next());
        if (bool)
        {
          return true;
          return false;
        }
      }
      return false;
    }
    finally {}
  }
  
  /* Error */
  public boolean isApplicationHidden(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore_3
    //   12: aload_0
    //   13: monitorenter
    //   14: aload_0
    //   15: aload_1
    //   16: iconst_m1
    //   17: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   20: pop
    //   21: aload_0
    //   22: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   25: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   28: lstore 4
    //   30: aload_0
    //   31: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   34: aload_2
    //   35: iload_3
    //   36: invokeinterface 3149 3 0
    //   41: istore 6
    //   43: aload_0
    //   44: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   47: lload 4
    //   49: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   52: aload_0
    //   53: monitorexit
    //   54: iload 6
    //   56: ireturn
    //   57: astore_1
    //   58: ldc 125
    //   60: ldc_w 3151
    //   63: aload_1
    //   64: invokestatic 620	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   67: pop
    //   68: aload_0
    //   69: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   72: lload 4
    //   74: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   77: aload_0
    //   78: monitorexit
    //   79: iconst_0
    //   80: ireturn
    //   81: astore_1
    //   82: aload_0
    //   83: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   86: lload 4
    //   88: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   91: aload_1
    //   92: athrow
    //   93: astore_1
    //   94: aload_0
    //   95: monitorexit
    //   96: aload_1
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	DevicePolicyManagerService
    //   0	98	1	paramComponentName	ComponentName
    //   0	98	2	paramString	String
    //   11	25	3	i	int
    //   28	59	4	l	long
    //   41	14	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   30	43	57	android/os/RemoteException
    //   30	43	81	finally
    //   58	68	81	finally
    //   14	30	93	finally
    //   43	52	93	finally
    //   68	77	93	finally
    //   82	93	93	finally
  }
  
  /* Error */
  public boolean isBackupServiceEnabled(ComponentName paramComponentName)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_1
    //   3: invokestatic 427	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   6: pop
    //   7: aload_0
    //   8: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   11: ifne +5 -> 16
    //   14: iconst_1
    //   15: ireturn
    //   16: aload_0
    //   17: monitorenter
    //   18: aload_0
    //   19: aload_1
    //   20: bipush -2
    //   22: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   25: pop
    //   26: aload_0
    //   27: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   30: invokevirtual 780	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getIBackupManager	()Landroid/app/backup/IBackupManager;
    //   33: astore_1
    //   34: aload_1
    //   35: ifnull +11 -> 46
    //   38: aload_1
    //   39: iconst_0
    //   40: invokeinterface 3155 2 0
    //   45: istore_2
    //   46: aload_0
    //   47: monitorexit
    //   48: iload_2
    //   49: ireturn
    //   50: astore_1
    //   51: new 695	java/lang/IllegalStateException
    //   54: dup
    //   55: ldc_w 3157
    //   58: aload_1
    //   59: invokespecial 791	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   62: athrow
    //   63: astore_1
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_1
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	DevicePolicyManagerService
    //   0	68	1	paramComponentName	ComponentName
    //   1	48	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   26	34	50	android/os/RemoteException
    //   38	46	50	android/os/RemoteException
    //   18	26	63	finally
    //   26	34	63	finally
    //   38	46	63	finally
    //   51	63	63	finally
  }
  
  public boolean isCaCertApproved(String paramString, int paramInt)
  {
    enforceManageUsers();
    try
    {
      boolean bool = getUserData(paramInt).mAcceptedCaCertificates.contains(paramString);
      return bool;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  /* Error */
  public boolean isCallerApplicationRestrictionsManagingPackage()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   6: invokevirtual 887	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderGetCallingUid	()I
    //   9: istore_1
    //   10: iload_1
    //   11: invokestatic 964	android/os/UserHandle:getUserId	(I)I
    //   14: istore_2
    //   15: aload_0
    //   16: monitorenter
    //   17: aload_0
    //   18: iload_2
    //   19: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   22: astore 4
    //   24: aload 4
    //   26: getfield 804	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mApplicationRestrictionsManagingPackage	Ljava/lang/String;
    //   29: astore 5
    //   31: aload 5
    //   33: ifnonnull +7 -> 40
    //   36: aload_0
    //   37: monitorexit
    //   38: iconst_0
    //   39: ireturn
    //   40: aload_0
    //   41: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   44: invokevirtual 502	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   47: aload 4
    //   49: getfield 804	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mApplicationRestrictionsManagingPackage	Ljava/lang/String;
    //   52: iload_2
    //   53: invokevirtual 1425	android/content/pm/PackageManager:getPackageUidAsUser	(Ljava/lang/String;I)I
    //   56: istore_2
    //   57: iload_2
    //   58: iload_1
    //   59: if_icmpne +5 -> 64
    //   62: iconst_1
    //   63: istore_3
    //   64: aload_0
    //   65: monitorexit
    //   66: iload_3
    //   67: ireturn
    //   68: astore 4
    //   70: aload_0
    //   71: monitorexit
    //   72: iconst_0
    //   73: ireturn
    //   74: astore 4
    //   76: aload_0
    //   77: monitorexit
    //   78: aload 4
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	DevicePolicyManagerService
    //   9	51	1	i	int
    //   14	46	2	j	int
    //   1	66	3	bool	boolean
    //   22	26	4	localDevicePolicyData	DevicePolicyData
    //   68	1	4	localNameNotFoundException	PackageManager.NameNotFoundException
    //   74	5	4	localObject	Object
    //   29	3	5	str	String
    // Exception table:
    //   from	to	target	type
    //   40	57	68	android/content/pm/PackageManager$NameNotFoundException
    //   17	31	74	finally
    //   40	57	74	finally
  }
  
  boolean isCallerDeviceOwner(int paramInt)
  {
    try
    {
      boolean bool = this.mOwners.hasDeviceOwner();
      if (!bool) {
        return false;
      }
      int i = UserHandle.getUserId(paramInt);
      int j = this.mOwners.getDeviceOwnerUserId();
      if (i != j) {
        return false;
      }
      String str = this.mOwners.getDeviceOwnerComponent().getPackageName();
      String[] arrayOfString = this.mContext.getPackageManager().getPackagesForUid(paramInt);
      i = arrayOfString.length;
      paramInt = 0;
      while (paramInt < i)
      {
        bool = str.equals(arrayOfString[paramInt]);
        if (bool) {
          return true;
        }
        paramInt += 1;
      }
      return false;
    }
    finally {}
  }
  
  /* Error */
  public boolean isDeviceOwner(ComponentName paramComponentName, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   6: invokevirtual 669	com/android/server/devicepolicy/Owners:hasDeviceOwner	()Z
    //   9: ifeq +30 -> 39
    //   12: aload_0
    //   13: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   16: invokevirtual 923	com/android/server/devicepolicy/Owners:getDeviceOwnerUserId	()I
    //   19: iload_2
    //   20: if_icmpne +19 -> 39
    //   23: aload_0
    //   24: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   27: invokevirtual 1038	com/android/server/devicepolicy/Owners:getDeviceOwnerComponent	()Landroid/content/ComponentName;
    //   30: aload_1
    //   31: invokevirtual 2183	android/content/ComponentName:equals	(Ljava/lang/Object;)Z
    //   34: istore_3
    //   35: aload_0
    //   36: monitorexit
    //   37: iload_3
    //   38: ireturn
    //   39: iconst_0
    //   40: istore_3
    //   41: goto -6 -> 35
    //   44: astore_1
    //   45: aload_0
    //   46: monitorexit
    //   47: aload_1
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	DevicePolicyManagerService
    //   0	49	1	paramComponentName	ComponentName
    //   0	49	2	paramInt	int
    //   34	7	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	35	44	finally
  }
  
  public boolean isDeviceProvisioned()
  {
    return !TextUtils.isEmpty(this.mInjector.systemPropertiesGet("ro.device_owner"));
  }
  
  public boolean isDeviceProvisioningConfigApplied()
  {
    enforceManageUsers();
    try
    {
      boolean bool = getUserData(0).mDeviceProvisioningConfigApplied;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isInputMethodPermittedByAdmin(ComponentName paramComponentName, String paramString, int paramInt)
  {
    if (!this.mHasFeature) {
      return true;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    Preconditions.checkStringNotEmpty(paramString, "packageName is null");
    if (!isCallerWithSystemUid()) {
      throw new SecurityException("Only the system can query if an input method is disabled by admin");
    }
    try
    {
      paramComponentName = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
      if (paramComponentName == null) {
        return false;
      }
      List localList = paramComponentName.permittedInputMethods;
      if (localList == null) {
        return true;
      }
      boolean bool = checkPackagesInPermittedListOrSystem(Arrays.asList(new String[] { paramString }), paramComponentName.permittedInputMethods, paramInt);
      return bool;
    }
    finally {}
  }
  
  public boolean isLockTaskPermitted(String paramString)
  {
    DevicePolicyData localDevicePolicyData = getUserData(UserHandle.getUserId(this.mInjector.binderGetCallingUid()));
    int i = 0;
    try
    {
      while (i < localDevicePolicyData.mLockTaskPackages.size())
      {
        boolean bool = ((String)localDevicePolicyData.mLockTaskPackages.get(i)).equals(paramString);
        if (bool) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public boolean isManagedProfile(ComponentName paramComponentName)
  {
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      paramComponentName = getUserInfo(this.mInjector.userHandleGetCallingUserId());
      if (paramComponentName != null) {
        return paramComponentName.isManagedProfile();
      }
    }
    finally {}
    return false;
  }
  
  public boolean isMasterVolumeMuted(ComponentName paramComponentName)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      boolean bool = ((AudioManager)this.mContext.getSystemService("audio")).isMasterMute();
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  boolean isPackageInstalledForUser(String paramString, int paramInt)
  {
    boolean bool2 = false;
    try
    {
      paramString = this.mInjector.getIPackageManager().getPackageInfo(paramString, 0, paramInt);
      boolean bool1 = bool2;
      if (paramString != null)
      {
        paramInt = paramString.applicationInfo.flags;
        bool1 = bool2;
        if (paramInt != 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeException("Package manager has died", paramString);
    }
  }
  
  /* Error */
  public boolean isPackageSuspended(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore_3
    //   12: aload_0
    //   13: monitorenter
    //   14: aload_0
    //   15: aload_1
    //   16: iconst_m1
    //   17: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   20: pop
    //   21: aload_0
    //   22: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   25: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   28: lstore 4
    //   30: aload_0
    //   31: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   34: aload_2
    //   35: iload_3
    //   36: invokeinterface 3188 3 0
    //   41: istore 6
    //   43: aload_0
    //   44: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   47: lload 4
    //   49: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   52: aload_0
    //   53: monitorexit
    //   54: iload 6
    //   56: ireturn
    //   57: astore_1
    //   58: ldc 125
    //   60: ldc_w 3190
    //   63: aload_1
    //   64: invokestatic 620	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   67: pop
    //   68: aload_0
    //   69: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   72: lload 4
    //   74: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   77: aload_0
    //   78: monitorexit
    //   79: iconst_0
    //   80: ireturn
    //   81: astore_1
    //   82: aload_0
    //   83: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   86: lload 4
    //   88: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   91: aload_1
    //   92: athrow
    //   93: astore_1
    //   94: aload_0
    //   95: monitorexit
    //   96: aload_1
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	DevicePolicyManagerService
    //   0	98	1	paramComponentName	ComponentName
    //   0	98	2	paramString	String
    //   11	25	3	i	int
    //   28	59	4	l	long
    //   41	14	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   30	43	57	android/os/RemoteException
    //   30	43	81	finally
    //   58	68	81	finally
    //   14	30	93	finally
    //   43	52	93	finally
    //   68	77	93	finally
    //   82	93	93	finally
  }
  
  public boolean isProfileActivePasswordSufficientForParent(int paramInt)
  {
    if (!this.mHasFeature) {
      return true;
    }
    enforceFullCrossUsersPermission(paramInt);
    enforceManagedProfile(paramInt, "call APIs refering to the parent profile");
    try
    {
      int i = getProfileParentId(paramInt);
      boolean bool = isActivePasswordSufficientForUserLocked(getUserDataUnchecked(getCredentialOwner(paramInt, false)), i, false);
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isProfileOwner(ComponentName paramComponentName, int paramInt)
  {
    ComponentName localComponentName = getProfileOwner(paramInt);
    if (paramComponentName != null) {
      return paramComponentName.equals(localComponentName);
    }
    return false;
  }
  
  public boolean isProvisioningAllowed(String paramString)
  {
    if (!this.mHasFeature) {
      return false;
    }
    int i = this.mInjector.userHandleGetCallingUserId();
    if ("android.app.action.PROVISION_MANAGED_PROFILE".equals(paramString))
    {
      if (!hasFeatureManagedUsers()) {
        return false;
      }
      boolean bool;
      try
      {
        if (this.mOwners.hasDeviceOwner())
        {
          bool = this.mInjector.userManagerIsSplitSystemUser();
          if (!bool) {
            return false;
          }
          int j = this.mOwners.getDeviceOwnerUserId();
          if (j != 0) {
            return false;
          }
          if (i == 0) {
            return false;
          }
        }
        if (getProfileOwner(i) != null) {
          return false;
        }
      }
      finally {}
      long l = this.mInjector.binderClearCallingIdentity();
      try
      {
        bool = this.mUserManager.canAddMoreManagedProfiles(i, true);
        return bool;
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
    }
    if ("android.app.action.PROVISION_MANAGED_DEVICE".equals(paramString)) {
      return isDeviceOwnerProvisioningAllowed(i);
    }
    if ("android.app.action.PROVISION_MANAGED_USER".equals(paramString))
    {
      if (!hasFeatureManagedUsers()) {
        return false;
      }
      if (!this.mInjector.userManagerIsSplitSystemUser()) {
        return false;
      }
      if (i == 0) {
        return false;
      }
      return !hasUserSetupCompleted(i);
    }
    if ("android.app.action.PROVISION_MANAGED_SHAREABLE_DEVICE".equals(paramString))
    {
      if (!this.mInjector.userManagerIsSplitSystemUser()) {
        return false;
      }
      return isDeviceOwnerProvisioningAllowed(i);
    }
    throw new IllegalArgumentException("Unknown provisioning action " + paramString);
  }
  
  public boolean isRemovingAdmin(ComponentName paramComponentName, int paramInt)
  {
    if (!this.mHasFeature) {
      return false;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      boolean bool = getUserData(paramInt).mRemovingAdmins.contains(paramComponentName);
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean isSecurityLoggingEnabled(ComponentName paramComponentName)
  {
    Preconditions.checkNotNull(paramComponentName);
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      boolean bool = this.mInjector.securityLogGetLoggingEnabledProperty();
      return bool;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean isSeparateProfileChallengeAllowed(int paramInt)
  {
    boolean bool2 = false;
    ComponentName localComponentName = getProfileOwner(paramInt);
    boolean bool1 = bool2;
    if (localComponentName != null)
    {
      bool1 = bool2;
      if (getTargetSdk(localComponentName.getPackageName(), paramInt) > 23) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isSystemOnlyUser(ComponentName paramComponentName)
  {
    boolean bool2 = false;
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      int i = this.mInjector.userHandleGetCallingUserId();
      boolean bool1 = bool2;
      if (UserManager.isSplitSystemUser())
      {
        bool1 = bool2;
        if (i == 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    finally {}
  }
  
  /* Error */
  public boolean isUninstallBlocked(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   3: istore_3
    //   4: aload_0
    //   5: monitorenter
    //   6: aload_1
    //   7: ifnull +10 -> 17
    //   10: aload_0
    //   11: aload_1
    //   12: iconst_m1
    //   13: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   16: pop
    //   17: aload_0
    //   18: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   21: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   24: lstore 4
    //   26: aload_0
    //   27: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   30: aload_2
    //   31: iload_3
    //   32: invokeinterface 3224 3 0
    //   37: istore 6
    //   39: aload_0
    //   40: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   43: lload 4
    //   45: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   48: aload_0
    //   49: monitorexit
    //   50: iload 6
    //   52: ireturn
    //   53: astore_1
    //   54: ldc 125
    //   56: ldc_w 3226
    //   59: aload_1
    //   60: invokestatic 620	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   63: pop
    //   64: aload_0
    //   65: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   68: lload 4
    //   70: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   73: aload_0
    //   74: monitorexit
    //   75: iconst_0
    //   76: ireturn
    //   77: astore_1
    //   78: aload_0
    //   79: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   82: lload 4
    //   84: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   87: aload_1
    //   88: athrow
    //   89: astore_1
    //   90: aload_0
    //   91: monitorexit
    //   92: aload_1
    //   93: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	DevicePolicyManagerService
    //   0	94	1	paramComponentName	ComponentName
    //   0	94	2	paramString	String
    //   3	29	3	i	int
    //   24	59	4	l	long
    //   37	14	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   26	39	53	android/os/RemoteException
    //   26	39	77	finally
    //   54	64	77	finally
    //   10	17	89	finally
    //   17	26	89	finally
    //   39	48	89	finally
    //   64	73	89	finally
    //   78	89	89	finally
  }
  
  public boolean isUninstallInQueue(String paramString)
  {
    enforceCanManageDeviceAdmin();
    paramString = new Pair(paramString, Integer.valueOf(this.mInjector.userHandleGetCallingUserId()));
    try
    {
      boolean bool = this.mPackagesToRemove.contains(paramString);
      return bool;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  void loadOwners()
  {
    try
    {
      this.mOwners.load();
      setDeviceOwnerSystemPropertyLocked();
      findOwnerComponentIfNecessaryLocked();
      migrateUserRestrictionsIfNecessaryLocked();
      updateDeviceOwnerLocked();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void lockNow(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: aconst_null
    //   12: iconst_3
    //   13: iload_1
    //   14: invokevirtual 2785	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;IZ)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   17: pop
    //   18: aload_0
    //   19: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   22: invokevirtual 986	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:userHandleGetCallingUserId	()I
    //   25: istore_2
    //   26: iload_1
    //   27: ifne +69 -> 96
    //   30: aload_0
    //   31: iload_2
    //   32: invokespecial 1115	com/android/server/devicepolicy/DevicePolicyManagerService:isSeparateProfileChallengeEnabled	(I)Z
    //   35: ifeq +61 -> 96
    //   38: aload_0
    //   39: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   42: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   45: lstore_3
    //   46: aload_0
    //   47: getfield 493	com/android/server/devicepolicy/DevicePolicyManagerService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   50: iconst_2
    //   51: iload_2
    //   52: invokevirtual 3240	com/android/internal/widget/LockPatternUtils:requireStrongAuth	(II)V
    //   55: iload_2
    //   56: iconst_m1
    //   57: if_icmpne +44 -> 101
    //   60: aload_0
    //   61: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   64: invokestatic 3245	android/os/SystemClock:uptimeMillis	()J
    //   67: iconst_1
    //   68: iconst_0
    //   69: invokevirtual 3249	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:powerManagerGoToSleep	(JII)V
    //   72: aload_0
    //   73: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   76: invokevirtual 3253	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getIWindowManager	()Landroid/view/IWindowManager;
    //   79: aconst_null
    //   80: invokeinterface 3257 2 0
    //   85: aload_0
    //   86: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   89: lload_3
    //   90: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   93: aload_0
    //   94: monitorexit
    //   95: return
    //   96: iconst_m1
    //   97: istore_2
    //   98: goto -60 -> 38
    //   101: aload_0
    //   102: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   105: invokevirtual 3261	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getTrustManager	()Landroid/app/trust/TrustManager;
    //   108: iload_2
    //   109: iconst_1
    //   110: invokevirtual 3266	android/app/trust/TrustManager:setDeviceLockedForUser	(IZ)V
    //   113: goto -28 -> 85
    //   116: astore 5
    //   118: aload_0
    //   119: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   122: lload_3
    //   123: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   126: goto -33 -> 93
    //   129: astore 5
    //   131: aload_0
    //   132: monitorexit
    //   133: aload 5
    //   135: athrow
    //   136: astore 5
    //   138: aload_0
    //   139: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   142: lload_3
    //   143: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   146: aload 5
    //   148: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	DevicePolicyManagerService
    //   0	149	1	paramBoolean	boolean
    //   25	84	2	i	int
    //   45	98	3	l	long
    //   116	1	5	localRemoteException	RemoteException
    //   129	5	5	localObject1	Object
    //   136	11	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   46	55	116	android/os/RemoteException
    //   60	85	116	android/os/RemoteException
    //   101	113	116	android/os/RemoteException
    //   10	26	129	finally
    //   30	38	129	finally
    //   38	46	129	finally
    //   85	93	129	finally
    //   118	126	129	finally
    //   138	149	129	finally
    //   46	55	136	finally
    //   60	85	136	finally
    //   101	113	136	finally
  }
  
  public void notifyLockTaskModeChanged(boolean paramBoolean, String paramString, int paramInt)
  {
    if (!isCallerWithSystemUid()) {
      throw new SecurityException("notifyLockTaskModeChanged can only be called by system");
    }
    for (;;)
    {
      try
      {
        Object localObject = getUserData(paramInt);
        Bundle localBundle = new Bundle();
        localBundle.putString("android.app.extra.LOCK_TASK_PACKAGE", paramString);
        paramString = ((DevicePolicyData)localObject).mAdminList.iterator();
        if (!paramString.hasNext()) {
          break;
        }
        localObject = (ActiveAdmin)paramString.next();
        boolean bool1 = isDeviceOwner(((ActiveAdmin)localObject).info.getComponent(), paramInt);
        boolean bool2 = isProfileOwner(((ActiveAdmin)localObject).info.getComponent(), paramInt);
        if ((bool1) || (bool2)) {
          if (paramBoolean) {
            sendAdminCommandLocked((ActiveAdmin)localObject, "android.app.action.LOCK_TASK_ENTERING", localBundle, null);
          } else {
            sendAdminCommandLocked((ActiveAdmin)localObject, "android.app.action.LOCK_TASK_EXITING");
          }
        }
      }
      finally {}
    }
  }
  
  /* Error */
  public void notifyPendingSystemUpdate(long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 3282
    //   7: ldc_w 3284
    //   10: invokevirtual 874	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   13: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   16: ifeq +13 -> 29
    //   19: ldc 125
    //   21: ldc_w 3286
    //   24: invokestatic 837	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   27: pop
    //   28: return
    //   29: new 1795	android/content/Intent
    //   32: dup
    //   33: ldc_w 3288
    //   36: invokespecial 2090	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   39: astore 7
    //   41: aload 7
    //   43: ldc_w 3290
    //   46: lload_1
    //   47: invokevirtual 3293	android/content/Intent:putExtra	(Ljava/lang/String;J)Landroid/content/Intent;
    //   50: pop
    //   51: aload_0
    //   52: monitorenter
    //   53: aload_0
    //   54: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   57: invokevirtual 669	com/android/server/devicepolicy/Owners:hasDeviceOwner	()Z
    //   60: ifeq +23 -> 83
    //   63: aload_0
    //   64: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   67: invokevirtual 1038	com/android/server/devicepolicy/Owners:getDeviceOwnerComponent	()Landroid/content/ComponentName;
    //   70: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   73: astore 4
    //   75: aload 4
    //   77: ifnonnull +12 -> 89
    //   80: aload_0
    //   81: monitorexit
    //   82: return
    //   83: aconst_null
    //   84: astore 4
    //   86: goto -11 -> 75
    //   89: new 536	android/os/UserHandle
    //   92: dup
    //   93: aload_0
    //   94: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   97: invokevirtual 923	com/android/server/devicepolicy/Owners:getDeviceOwnerUserId	()I
    //   100: invokespecial 674	android/os/UserHandle:<init>	(I)V
    //   103: astore 8
    //   105: aconst_null
    //   106: astore 5
    //   108: aload_0
    //   109: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   112: invokevirtual 502	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   115: aload 4
    //   117: iconst_2
    //   118: invokevirtual 3296	android/content/pm/PackageManager:getPackageInfo	(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
    //   121: getfield 3300	android/content/pm/PackageInfo:receivers	[Landroid/content/pm/ActivityInfo;
    //   124: astore 6
    //   126: aload 6
    //   128: astore 5
    //   130: aload 5
    //   132: ifnull +107 -> 239
    //   135: aload_0
    //   136: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   139: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   142: lstore_1
    //   143: iconst_0
    //   144: istore_3
    //   145: iload_3
    //   146: aload 5
    //   148: arraylength
    //   149: if_icmpge +82 -> 231
    //   152: ldc_w 2670
    //   155: aload 5
    //   157: iload_3
    //   158: aaload
    //   159: getfield 2673	android/content/pm/ActivityInfo:permission	Ljava/lang/String;
    //   162: invokevirtual 1028	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   165: ifeq +36 -> 201
    //   168: aload 7
    //   170: new 1040	android/content/ComponentName
    //   173: dup
    //   174: aload 4
    //   176: aload 5
    //   178: iload_3
    //   179: aaload
    //   180: getfield 2726	android/content/pm/ActivityInfo:name	Ljava/lang/String;
    //   183: invokespecial 3301	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   186: invokevirtual 2304	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   189: pop
    //   190: aload_0
    //   191: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   194: aload 7
    //   196: aload 8
    //   198: invokevirtual 2099	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   201: iload_3
    //   202: iconst_1
    //   203: iadd
    //   204: istore_3
    //   205: goto -60 -> 145
    //   208: astore 6
    //   210: ldc 125
    //   212: ldc_w 3303
    //   215: aload 6
    //   217: invokestatic 3093	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   220: pop
    //   221: goto -91 -> 130
    //   224: astore 4
    //   226: aload_0
    //   227: monitorexit
    //   228: aload 4
    //   230: athrow
    //   231: aload_0
    //   232: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   235: lload_1
    //   236: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   239: aload_0
    //   240: monitorexit
    //   241: return
    //   242: astore 4
    //   244: aload_0
    //   245: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   248: lload_1
    //   249: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   252: aload 4
    //   254: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	255	0	this	DevicePolicyManagerService
    //   0	255	1	paramLong	long
    //   144	61	3	i	int
    //   73	102	4	str	String
    //   224	5	4	localObject1	Object
    //   242	11	4	localObject2	Object
    //   106	71	5	localObject3	Object
    //   124	3	6	arrayOfActivityInfo	ActivityInfo[]
    //   208	8	6	localNameNotFoundException	PackageManager.NameNotFoundException
    //   39	156	7	localIntent	Intent
    //   103	94	8	localUserHandle	UserHandle
    // Exception table:
    //   from	to	target	type
    //   108	126	208	android/content/pm/PackageManager$NameNotFoundException
    //   53	75	224	finally
    //   89	105	224	finally
    //   108	126	224	finally
    //   135	143	224	finally
    //   210	221	224	finally
    //   231	239	224	finally
    //   244	255	224	finally
    //   145	201	242	finally
  }
  
  public boolean packageHasActiveAdmins(String paramString, int paramInt)
  {
    if (!this.mHasFeature) {
      return false;
    }
    enforceFullCrossUsersPermission(paramInt);
    try
    {
      DevicePolicyData localDevicePolicyData = getUserData(paramInt);
      int i = localDevicePolicyData.mAdminList.size();
      paramInt = 0;
      while (paramInt < i)
      {
        boolean bool = ((ActiveAdmin)localDevicePolicyData.mAdminList.get(paramInt)).info.getPackageName().equals(paramString);
        if (bool) {
          return true;
        }
        paramInt += 1;
      }
      return false;
    }
    finally {}
  }
  
  public void reboot(ComponentName paramComponentName)
  {
    Preconditions.checkNotNull(paramComponentName);
    long l;
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      l = this.mInjector.binderClearCallingIdentity();
      try
      {
        if (this.mTelephonyManager.getCallState() != 0) {
          throw new IllegalStateException("Cannot be called with ongoing call on the device");
        }
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      this.mInjector.powerManagerReboot("deviceowner");
    }
    finally {}
    this.mInjector.binderRestoreCallingIdentity(l);
  }
  
  /* Error */
  public void removeActiveAdmin(ComponentName paramComponentName, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: iload_2
    //   10: invokespecial 2160	com/android/server/devicepolicy/DevicePolicyManagerService:enforceFullCrossUsersPermission	(I)V
    //   13: aload_0
    //   14: iload_2
    //   15: invokespecial 980	com/android/server/devicepolicy/DevicePolicyManagerService:enforceUserUnlocked	(I)V
    //   18: aload_0
    //   19: monitorenter
    //   20: aload_0
    //   21: aload_1
    //   22: iload_2
    //   23: invokevirtual 1177	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminUncheckedLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   26: astore 5
    //   28: aload 5
    //   30: ifnonnull +6 -> 36
    //   33: aload_0
    //   34: monitorexit
    //   35: return
    //   36: aload_0
    //   37: aload_1
    //   38: iload_2
    //   39: invokevirtual 2693	com/android/server/devicepolicy/DevicePolicyManagerService:isDeviceOwner	(Landroid/content/ComponentName;I)Z
    //   42: ifne +12 -> 54
    //   45: aload_0
    //   46: aload_1
    //   47: iload_2
    //   48: invokevirtual 2696	com/android/server/devicepolicy/DevicePolicyManagerService:isProfileOwner	(Landroid/content/ComponentName;I)Z
    //   51: ifeq +32 -> 83
    //   54: ldc 125
    //   56: new 697	java/lang/StringBuilder
    //   59: dup
    //   60: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   63: ldc_w 3317
    //   66: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_1
    //   70: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   73: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   76: invokestatic 611	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   79: pop
    //   80: aload_0
    //   81: monitorexit
    //   82: return
    //   83: aload 5
    //   85: invokevirtual 1102	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:getUid	()I
    //   88: aload_0
    //   89: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   92: invokevirtual 887	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderGetCallingUid	()I
    //   95: if_icmpeq +14 -> 109
    //   98: aload_0
    //   99: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   102: ldc_w 870
    //   105: aconst_null
    //   106: invokevirtual 874	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   109: aload_0
    //   110: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   113: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   116: lstore_3
    //   117: aload_0
    //   118: aload_1
    //   119: iload_2
    //   120: invokevirtual 2478	com/android/server/devicepolicy/DevicePolicyManagerService:removeActiveAdminLocked	(Landroid/content/ComponentName;I)V
    //   123: aload_0
    //   124: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   127: lload_3
    //   128: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   131: aload_0
    //   132: monitorexit
    //   133: return
    //   134: astore_1
    //   135: aload_0
    //   136: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   139: lload_3
    //   140: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   143: aload_1
    //   144: athrow
    //   145: astore_1
    //   146: aload_0
    //   147: monitorexit
    //   148: aload_1
    //   149: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	this	DevicePolicyManagerService
    //   0	150	1	paramComponentName	ComponentName
    //   0	150	2	paramInt	int
    //   116	24	3	l	long
    //   26	58	5	localActiveAdmin	ActiveAdmin
    // Exception table:
    //   from	to	target	type
    //   117	123	134	finally
    //   20	28	145	finally
    //   36	54	145	finally
    //   54	80	145	finally
    //   83	109	145	finally
    //   109	117	145	finally
    //   123	131	145	finally
    //   135	145	145	finally
  }
  
  void removeActiveAdminLocked(final ComponentName paramComponentName, final int paramInt)
  {
    ActiveAdmin localActiveAdmin = getActiveAdminUncheckedLocked(paramComponentName, paramInt);
    DevicePolicyData localDevicePolicyData = getUserData(paramInt);
    if ((localActiveAdmin == null) || (localDevicePolicyData.mRemovingAdmins.contains(paramComponentName))) {
      return;
    }
    localDevicePolicyData.mRemovingAdmins.add(paramComponentName);
    sendAdminCommandLocked(localActiveAdmin, "android.app.action.DEVICE_ADMIN_DISABLED", new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        DevicePolicyManagerService.-wrap13(DevicePolicyManagerService.this, paramComponentName, paramInt);
        DevicePolicyManagerService.-wrap14(DevicePolicyManagerService.this, paramComponentName.getPackageName(), paramInt);
      }
    });
  }
  
  /* Error */
  public boolean removeCrossProfileWidgetProvider(ComponentName paramComponentName, String paramString)
  {
    // Byte code:
    //   0: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   3: istore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: aload_1
    //   11: iconst_m1
    //   12: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   15: astore_1
    //   16: aload_1
    //   17: getfield 2405	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:crossProfileWidgetProviders	Ljava/util/List;
    //   20: astore 5
    //   22: aload 5
    //   24: ifnonnull +7 -> 31
    //   27: aload_0
    //   28: monitorexit
    //   29: iconst_0
    //   30: ireturn
    //   31: aload_1
    //   32: getfield 2405	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:crossProfileWidgetProviders	Ljava/util/List;
    //   35: astore 5
    //   37: aload 4
    //   39: astore_1
    //   40: aload 5
    //   42: aload_2
    //   43: invokeinterface 3326 2 0
    //   48: ifeq +18 -> 66
    //   51: new 1011	java/util/ArrayList
    //   54: dup
    //   55: aload 5
    //   57: invokespecial 2408	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
    //   60: astore_1
    //   61: aload_0
    //   62: iload_3
    //   63: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   66: aload_0
    //   67: monitorexit
    //   68: aload_1
    //   69: ifnull +19 -> 88
    //   72: aload_0
    //   73: getfield 487	com/android/server/devicepolicy/DevicePolicyManagerService:mLocalService	Lcom/android/server/devicepolicy/DevicePolicyManagerService$LocalService;
    //   76: iload_3
    //   77: aload_1
    //   78: invokestatic 2411	com/android/server/devicepolicy/DevicePolicyManagerService$LocalService:-wrap0	(Lcom/android/server/devicepolicy/DevicePolicyManagerService$LocalService;ILjava/util/List;)V
    //   81: iconst_1
    //   82: ireturn
    //   83: astore_1
    //   84: aload_0
    //   85: monitorexit
    //   86: aload_1
    //   87: athrow
    //   88: iconst_0
    //   89: ireturn
    //   90: astore_1
    //   91: goto -7 -> 84
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	DevicePolicyManagerService
    //   0	94	1	paramComponentName	ComponentName
    //   0	94	2	paramString	String
    //   3	74	3	i	int
    //   5	33	4	localObject	Object
    //   20	36	5	localList	List
    // Exception table:
    //   from	to	target	type
    //   9	22	83	finally
    //   31	37	83	finally
    //   40	61	83	finally
    //   61	66	90	finally
  }
  
  public boolean removeKeyPair(ComponentName paramComponentName, String paramString)
  {
    enforceCanManageInstalledKeys(paramComponentName);
    paramComponentName = new UserHandle(UserHandle.getCallingUserId());
    l = Binder.clearCallingIdentity();
    for (;;)
    {
      try
      {
        paramComponentName = KeyChain.bindAsUser(this.mContext, paramComponentName);
      }
      catch (InterruptedException paramComponentName)
      {
        boolean bool;
        Log.w("DevicePolicyManagerService", "Interrupted while removing keypair", paramComponentName);
        Thread.currentThread().interrupt();
        Binder.restoreCallingIdentity(l);
        continue;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      try
      {
        bool = paramComponentName.getService().removeKeyPair(paramString);
        paramComponentName.close();
        Binder.restoreCallingIdentity(l);
        return bool;
      }
      catch (RemoteException paramString)
      {
        Log.e("DevicePolicyManagerService", "Removing keypair", paramString);
        paramComponentName.close();
        Binder.restoreCallingIdentity(l);
        return false;
      }
      finally
      {
        paramComponentName.close();
      }
    }
  }
  
  /* Error */
  public boolean removeUser(ComponentName paramComponentName, UserHandle paramUserHandle)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: aload_1
    //   12: bipush -2
    //   14: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   17: pop
    //   18: aload_0
    //   19: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   22: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   25: lstore_3
    //   26: aload_0
    //   27: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   30: aload_2
    //   31: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   34: invokevirtual 2554	android/os/UserManager:removeUser	(I)Z
    //   37: istore 5
    //   39: aload_0
    //   40: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   43: lload_3
    //   44: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   47: aload_0
    //   48: monitorexit
    //   49: iload 5
    //   51: ireturn
    //   52: astore_1
    //   53: aload_0
    //   54: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   57: lload_3
    //   58: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   61: aload_1
    //   62: athrow
    //   63: astore_1
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_1
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	DevicePolicyManagerService
    //   0	68	1	paramComponentName	ComponentName
    //   0	68	2	paramUserHandle	UserHandle
    //   25	33	3	l	long
    //   37	13	5	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   26	39	52	finally
    //   10	26	63	finally
    //   39	47	63	finally
    //   53	63	63	finally
  }
  
  void removeUserData(int paramInt)
  {
    if (paramInt == 0) {}
    try
    {
      Slog.w("DevicePolicyManagerService", "Tried to remove device policy file for user 0! Ignoring.");
      return;
    }
    finally {}
    this.mOwners.removeProfileOwner(paramInt);
    this.mOwners.writeProfileOwner(paramInt);
    if ((DevicePolicyData)this.mUserData.get(paramInt) != null) {
      this.mUserData.remove(paramInt);
    }
    File localFile = new File(this.mInjector.environmentGetUserSystemDirectory(paramInt), "device_policies.xml");
    localFile.delete();
    Slog.i("DevicePolicyManagerService", "Removed device policy file " + localFile.getAbsolutePath());
    updateScreenCaptureDisabledInWindowManager(paramInt, false);
  }
  
  public void reportFailedFingerprintAttempt(int paramInt)
  {
    enforceFullCrossUsersPermission(paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", null);
    if (this.mInjector.securityLogIsLoggingEnabled()) {
      SecurityLog.writeEvent(210007, new Object[] { Integer.valueOf(0), Integer.valueOf(0) });
    }
  }
  
  /* Error */
  public void reportFailedPasswordAttempt(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokespecial 2160	com/android/server/devicepolicy/DevicePolicyManagerService:enforceFullCrossUsersPermission	(I)V
    //   5: aload_0
    //   6: iload_1
    //   7: invokespecial 1115	com/android/server/devicepolicy/DevicePolicyManagerService:isSeparateProfileChallengeEnabled	(I)Z
    //   10: ifne +11 -> 21
    //   13: aload_0
    //   14: iload_1
    //   15: ldc_w 3365
    //   18: invokespecial 2489	com/android/server/devicepolicy/DevicePolicyManagerService:enforceNotManagedProfile	(ILjava/lang/String;)V
    //   21: aload_0
    //   22: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   25: ldc_w 2670
    //   28: aconst_null
    //   29: invokevirtual 874	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   32: aload_0
    //   33: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   36: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   39: lstore 7
    //   41: iconst_0
    //   42: istore_3
    //   43: iconst_0
    //   44: istore 5
    //   46: iconst_0
    //   47: istore_2
    //   48: iconst_0
    //   49: istore 6
    //   51: aload_0
    //   52: monitorenter
    //   53: aload_0
    //   54: iload_1
    //   55: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   58: astore 9
    //   60: aload 9
    //   62: aload 9
    //   64: getfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   67: iconst_1
    //   68: iadd
    //   69: putfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   72: aload_0
    //   73: iload_1
    //   74: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   77: aload_0
    //   78: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   81: ifeq +70 -> 151
    //   84: aload_0
    //   85: iload_1
    //   86: iconst_0
    //   87: invokespecial 2830	com/android/server/devicepolicy/DevicePolicyManagerService:getAdminWithMinimumFailedPasswordsForWipeLocked	(IZ)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   90: astore 10
    //   92: aload 10
    //   94: ifnull +117 -> 211
    //   97: aload 10
    //   99: getfield 1142	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:maximumFailedPasswordsForWipe	I
    //   102: istore 4
    //   104: iload 6
    //   106: istore_2
    //   107: iload 5
    //   109: istore_3
    //   110: iload 4
    //   112: ifle +30 -> 142
    //   115: iload 6
    //   117: istore_2
    //   118: iload 5
    //   120: istore_3
    //   121: aload 9
    //   123: getfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   126: iload 4
    //   128: if_icmplt +14 -> 142
    //   131: iconst_1
    //   132: istore_3
    //   133: aload 10
    //   135: invokevirtual 1146	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:getUserHandle	()Landroid/os/UserHandle;
    //   138: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   141: istore_2
    //   142: aload_0
    //   143: ldc_w 3367
    //   146: iconst_1
    //   147: iload_1
    //   148: invokespecial 3369	com/android/server/devicepolicy/DevicePolicyManagerService:sendAdminCommandForLockscreenPoliciesLocked	(Ljava/lang/String;II)V
    //   151: aload_0
    //   152: monitorexit
    //   153: iload_3
    //   154: ifeq +12 -> 166
    //   157: aload_0
    //   158: iconst_0
    //   159: iload_2
    //   160: ldc_w 3371
    //   163: invokespecial 3373	com/android/server/devicepolicy/DevicePolicyManagerService:wipeDeviceNoLock	(ZILjava/lang/String;)V
    //   166: aload_0
    //   167: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   170: lload 7
    //   172: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   175: aload_0
    //   176: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   179: invokevirtual 3353	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:securityLogIsLoggingEnabled	()Z
    //   182: ifeq +28 -> 210
    //   185: ldc_w 3354
    //   188: iconst_2
    //   189: anewarray 3356	java/lang/Object
    //   192: dup
    //   193: iconst_0
    //   194: iconst_0
    //   195: invokestatic 735	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   198: aastore
    //   199: dup
    //   200: iconst_1
    //   201: iconst_1
    //   202: invokestatic 735	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   205: aastore
    //   206: invokestatic 3362	android/app/admin/SecurityLog:writeEvent	(I[Ljava/lang/Object;)I
    //   209: pop
    //   210: return
    //   211: iconst_0
    //   212: istore 4
    //   214: goto -110 -> 104
    //   217: astore 9
    //   219: aload_0
    //   220: monitorexit
    //   221: aload 9
    //   223: athrow
    //   224: astore 9
    //   226: aload_0
    //   227: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   230: lload 7
    //   232: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   235: aload 9
    //   237: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	238	0	this	DevicePolicyManagerService
    //   0	238	1	paramInt	int
    //   47	113	2	i	int
    //   42	112	3	j	int
    //   102	111	4	k	int
    //   44	75	5	m	int
    //   49	67	6	n	int
    //   39	192	7	l	long
    //   58	64	9	localDevicePolicyData	DevicePolicyData
    //   217	5	9	localObject1	Object
    //   224	12	9	localObject2	Object
    //   90	44	10	localActiveAdmin	ActiveAdmin
    // Exception table:
    //   from	to	target	type
    //   53	77	217	finally
    //   77	92	217	finally
    //   97	104	217	finally
    //   121	131	217	finally
    //   133	142	217	finally
    //   142	151	217	finally
    //   51	53	224	finally
    //   151	153	224	finally
    //   157	166	224	finally
    //   219	224	224	finally
  }
  
  public void reportKeyguardDismissed(int paramInt)
  {
    enforceFullCrossUsersPermission(paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", null);
    if (this.mInjector.securityLogIsLoggingEnabled()) {
      SecurityLog.writeEvent(210006, new Object[0]);
    }
  }
  
  public void reportKeyguardSecured(int paramInt)
  {
    enforceFullCrossUsersPermission(paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", null);
    if (this.mInjector.securityLogIsLoggingEnabled()) {
      SecurityLog.writeEvent(210008, new Object[0]);
    }
  }
  
  /* Error */
  public void reportPasswordChanged(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: iload_1
    //   10: invokespecial 2160	com/android/server/devicepolicy/DevicePolicyManagerService:enforceFullCrossUsersPermission	(I)V
    //   13: aload_0
    //   14: iload_1
    //   15: invokespecial 1115	com/android/server/devicepolicy/DevicePolicyManagerService:isSeparateProfileChallengeEnabled	(I)Z
    //   18: ifne +11 -> 29
    //   21: aload_0
    //   22: iload_1
    //   23: ldc_w 3380
    //   26: invokespecial 2489	com/android/server/devicepolicy/DevicePolicyManagerService:enforceNotManagedProfile	(ILjava/lang/String;)V
    //   29: aload_0
    //   30: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   33: ldc_w 2670
    //   36: aconst_null
    //   37: invokevirtual 874	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   40: aload_0
    //   41: iload_1
    //   42: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   45: astore 4
    //   47: aload_0
    //   48: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   51: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   54: lstore_2
    //   55: aload_0
    //   56: monitorenter
    //   57: aload 4
    //   59: iconst_0
    //   60: putfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   63: aload_0
    //   64: iload_1
    //   65: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   68: aload_0
    //   69: iload_1
    //   70: invokespecial 3382	com/android/server/devicepolicy/DevicePolicyManagerService:updatePasswordExpirationsLocked	(I)V
    //   73: aload_0
    //   74: aload_0
    //   75: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   78: iload_1
    //   79: iconst_0
    //   80: invokespecial 1295	com/android/server/devicepolicy/DevicePolicyManagerService:setExpirationAlarmCheckLocked	(Landroid/content/Context;IZ)V
    //   83: aload_0
    //   84: ldc_w 3384
    //   87: iconst_0
    //   88: iload_1
    //   89: invokespecial 3369	com/android/server/devicepolicy/DevicePolicyManagerService:sendAdminCommandForLockscreenPoliciesLocked	(Ljava/lang/String;II)V
    //   92: aload_0
    //   93: monitorexit
    //   94: aload_0
    //   95: iload_1
    //   96: invokespecial 3386	com/android/server/devicepolicy/DevicePolicyManagerService:removeCaApprovalsIfNeeded	(I)V
    //   99: aload_0
    //   100: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   103: lload_2
    //   104: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   107: return
    //   108: astore 4
    //   110: aload_0
    //   111: monitorexit
    //   112: aload 4
    //   114: athrow
    //   115: astore 4
    //   117: aload_0
    //   118: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   121: lload_2
    //   122: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   125: aload 4
    //   127: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	128	0	this	DevicePolicyManagerService
    //   0	128	1	paramInt	int
    //   54	68	2	l	long
    //   45	13	4	localDevicePolicyData	DevicePolicyData
    //   108	5	4	localObject1	Object
    //   115	11	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   57	92	108	finally
    //   55	57	115	finally
    //   92	99	115	finally
    //   110	115	115	finally
  }
  
  public void reportSuccessfulFingerprintAttempt(int paramInt)
  {
    enforceFullCrossUsersPermission(paramInt);
    this.mContext.enforceCallingOrSelfPermission("android.permission.BIND_DEVICE_ADMIN", null);
    if (this.mInjector.securityLogIsLoggingEnabled()) {
      SecurityLog.writeEvent(210007, new Object[] { Integer.valueOf(1), Integer.valueOf(0) });
    }
  }
  
  /* Error */
  public void reportSuccessfulPasswordAttempt(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokespecial 2160	com/android/server/devicepolicy/DevicePolicyManagerService:enforceFullCrossUsersPermission	(I)V
    //   5: aload_0
    //   6: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   9: ldc_w 2670
    //   12: aconst_null
    //   13: invokevirtual 874	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   16: aload_0
    //   17: monitorenter
    //   18: aload_0
    //   19: iload_1
    //   20: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   23: astore 4
    //   25: aload 4
    //   27: getfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   30: ifne +11 -> 41
    //   33: aload 4
    //   35: getfield 1612	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPasswordOwner	I
    //   38: iflt +52 -> 90
    //   41: aload_0
    //   42: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   45: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   48: lstore_2
    //   49: aload 4
    //   51: iconst_0
    //   52: putfield 1607	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mFailedPasswordAttempts	I
    //   55: aload 4
    //   57: iconst_m1
    //   58: putfield 1612	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mPasswordOwner	I
    //   61: aload_0
    //   62: iload_1
    //   63: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   66: aload_0
    //   67: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   70: ifeq +12 -> 82
    //   73: aload_0
    //   74: ldc_w 3390
    //   77: iconst_1
    //   78: iload_1
    //   79: invokespecial 3369	com/android/server/devicepolicy/DevicePolicyManagerService:sendAdminCommandForLockscreenPoliciesLocked	(Ljava/lang/String;II)V
    //   82: aload_0
    //   83: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   86: lload_2
    //   87: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   90: aload_0
    //   91: monitorexit
    //   92: aload_0
    //   93: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   96: invokevirtual 3353	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:securityLogIsLoggingEnabled	()Z
    //   99: ifeq +28 -> 127
    //   102: ldc_w 3354
    //   105: iconst_2
    //   106: anewarray 3356	java/lang/Object
    //   109: dup
    //   110: iconst_0
    //   111: iconst_1
    //   112: invokestatic 735	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   115: aastore
    //   116: dup
    //   117: iconst_1
    //   118: iconst_1
    //   119: invokestatic 735	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   122: aastore
    //   123: invokestatic 3362	android/app/admin/SecurityLog:writeEvent	(I[Ljava/lang/Object;)I
    //   126: pop
    //   127: return
    //   128: astore 4
    //   130: aload_0
    //   131: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   134: lload_2
    //   135: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   138: aload 4
    //   140: athrow
    //   141: astore 4
    //   143: aload_0
    //   144: monitorexit
    //   145: aload 4
    //   147: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	148	0	this	DevicePolicyManagerService
    //   0	148	1	paramInt	int
    //   48	87	2	l	long
    //   23	33	4	localDevicePolicyData	DevicePolicyData
    //   128	11	4	localObject1	Object
    //   141	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   49	82	128	finally
    //   18	41	141	finally
    //   41	49	141	finally
    //   82	90	141	finally
    //   130	141	141	finally
  }
  
  public boolean requestBugreport(ComponentName paramComponentName)
  {
    if (!this.mHasFeature) {
      return false;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    ensureDeviceOwnerManagingSingleUser(paramComponentName);
    if ((this.mRemoteBugreportServiceIsActive.get()) || (getDeviceOwnerRemoteBugreportUri() != null))
    {
      Slog.d("DevicePolicyManagerService", "Remote bugreport wasn't started because there's already one running.");
      return false;
    }
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      ActivityManagerNative.getDefault().requestBugReport(2);
      this.mRemoteBugreportServiceIsActive.set(true);
      this.mRemoteBugreportSharingAccepted.set(false);
      registerRemoteBugreportReceivers();
      this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManagerService", 678432343, RemoteBugreportUtils.buildNotification(this.mContext, 1), UserHandle.ALL);
      this.mHandler.postDelayed(this.mRemoteBugreportTimeoutRunnable, 600000L);
      return true;
    }
    catch (RemoteException paramComponentName)
    {
      Slog.e("DevicePolicyManagerService", "Failed to make remote calls to start bugreportremote service", paramComponentName);
      return false;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  public boolean resetPassword(String paramString, int paramInt)
    throws RemoteException
  {
    if (!this.mHasFeature) {
      return false;
    }
    int i4 = this.mInjector.binderGetCallingUid();
    int i5 = this.mInjector.userHandleGetCallingUserId();
    if (paramString != null) {
      if (TextUtils.isEmpty(paramString)) {
        enforceNotManagedProfile(i5, "clear the active password");
      }
    }
    label163:
    label168:
    do
    {
      for (;;)
      {
        try
        {
          localObject = getActiveAdminWithPolicyForUidLocked(null, -1, i4);
          if (localObject == null) {
            break label168;
          }
          if (getTargetSdk(((ActiveAdmin)localObject).info.getPackageName(), i5) > 23) {
            break label163;
          }
          i = 1;
          if (isManagedProfile(i5)) {
            break label284;
          }
          localObject = this.mUserManager.getProfiles(i5).iterator();
          if (!((Iterator)localObject).hasNext()) {
            break label284;
          }
          if (!((UserInfo)((Iterator)localObject).next()).isManagedProfile()) {
            continue;
          }
          if (i != 0) {
            break label271;
          }
          throw new IllegalStateException("Cannot reset password on user has managed profile");
        }
        finally {}
        paramString = "";
        break;
        i = 0;
      }
      if (getTargetSdk(getActiveAdminForCallerLocked(null, 2).info.getPackageName(), i5) > 23) {
        break label1097;
      }
      j = 1;
      if (TextUtils.isEmpty(paramString))
      {
        if (j == 0) {
          throw new SecurityException("Cannot call with null password");
        }
        Slog.e("DevicePolicyManagerService", "Cannot call with null password");
        return false;
      }
      i = j;
    } while (!isLockScreenSecureUnchecked(i5));
    if (j == 0) {
      throw new SecurityException("Admin cannot change current password");
    }
    Slog.e("DevicePolicyManagerService", "Admin cannot change current password");
    return false;
    label271:
    Slog.e("DevicePolicyManagerService", "Cannot reset password on user has managed profile");
    return false;
    label284:
    if (!this.mUserManager.isUserUnlocked(i5))
    {
      if (i == 0) {
        throw new IllegalStateException("Cannot reset password when user is locked");
      }
      Slog.e("DevicePolicyManagerService", "Cannot reset password when user is locked");
      return false;
    }
    int j = getPasswordQuality(null, i5, false);
    int i = j;
    if (j == 524288) {
      i = 0;
    }
    int k = i;
    if (i != 0)
    {
      j = LockPatternUtils.computePasswordQuality(paramString);
      if ((j < i) && (i != 393216))
      {
        Slog.w("DevicePolicyManagerService", "resetPassword: password quality 0x" + Integer.toHexString(j) + " does not meet required quality 0x" + Integer.toHexString(i));
        return false;
      }
      k = Math.max(j, i);
    }
    i = getPasswordMinimumLength(null, i5, false);
    if (paramString.length() < i)
    {
      Slog.w("DevicePolicyManagerService", "resetPassword: password length " + paramString.length() + " does not meet required length " + i);
      return false;
    }
    int i1;
    int i2;
    int n;
    int i3;
    int i6;
    if (k == 393216)
    {
      i = 0;
      int m = 0;
      i1 = 0;
      i2 = 0;
      n = 0;
      j = 0;
      i3 = 0;
      if (i3 < paramString.length())
      {
        i6 = paramString.charAt(i3);
        if ((i6 < 65) || (i6 > 90)) {
          break label1112;
        }
        i += 1;
        m += 1;
        break label1103;
      }
      i3 = getPasswordMinimumLetters(null, i5, false);
      if (i < i3)
      {
        Slog.w("DevicePolicyManagerService", "resetPassword: number of letters " + i + " does not meet required number of letters " + i3);
        return false;
      }
      i = getPasswordMinimumNumeric(null, i5, false);
      if (i2 < i)
      {
        Slog.w("DevicePolicyManagerService", "resetPassword: number of numerical digits " + i2 + " does not meet required number of numerical digits " + i);
        return false;
      }
      i = getPasswordMinimumLowerCase(null, i5, false);
      if (i1 < i)
      {
        Slog.w("DevicePolicyManagerService", "resetPassword: number of lowercase letters " + i1 + " does not meet required number of lowercase letters " + i);
        return false;
      }
      i = getPasswordMinimumUpperCase(null, i5, false);
      if (m < i)
      {
        Slog.w("DevicePolicyManagerService", "resetPassword: number of uppercase letters " + m + " does not meet required number of uppercase letters " + i);
        return false;
      }
      i = getPasswordMinimumSymbols(null, i5, false);
      if (n < i)
      {
        Slog.w("DevicePolicyManagerService", "resetPassword: number of special symbols " + n + " does not meet required number of special symbols " + i);
        return false;
      }
      i = getPasswordMinimumNonLetter(null, i5, false);
      if (j < i)
      {
        Slog.w("DevicePolicyManagerService", "resetPassword: number of non-letter characters " + j + " does not meet required number of non-letter characters " + i);
        return false;
      }
    }
    Object localObject = getUserData(i5);
    if ((((DevicePolicyData)localObject).mPasswordOwner >= 0) && (((DevicePolicyData)localObject).mPasswordOwner != i4))
    {
      Slog.w("DevicePolicyManagerService", "resetPassword: already set by another uid and not entered by user");
      return false;
    }
    boolean bool = isCallerDeviceOwner(i4);
    label952:
    long l;
    if ((paramInt & 0x2) != 0)
    {
      i = 1;
      if ((bool) && (i != 0)) {
        setDoNotAskCredentialsOnBoot();
      }
      l = this.mInjector.binderClearCallingIdentity();
    }
    for (;;)
    {
      try
      {
        if (!TextUtils.isEmpty(paramString))
        {
          this.mLockPatternUtils.saveLockPassword(paramString, null, k, i5);
          break label1183;
          label997:
          if (paramInt != 0) {
            this.mLockPatternUtils.requireStrongAuth(2, -1);
          }
          if (paramInt == 0) {
            break label1087;
          }
          paramInt = i4;
        }
      }
      finally
      {
        label1019:
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      try
      {
        if (((DevicePolicyData)localObject).mPasswordOwner != paramInt)
        {
          ((DevicePolicyData)localObject).mPasswordOwner = paramInt;
          saveSettingsLocked(i5);
        }
        return true;
      }
      finally {}
      i = 0;
      break label952;
      this.mLockPatternUtils.clearLock(i5);
      label1087:
      label1097:
      label1103:
      label1112:
      label1183:
      while ((paramInt & 0x1) == 0)
      {
        paramInt = 0;
        break label997;
        paramInt = -1;
        break label1019;
        break;
        for (;;)
        {
          i3 += 1;
          break;
          if ((i6 >= 97) && (i6 <= 122))
          {
            i += 1;
            i1 += 1;
          }
          else if ((i6 >= 48) && (i6 <= 57))
          {
            i2 += 1;
            j += 1;
          }
          else
          {
            n += 1;
            j += 1;
          }
        }
      }
      paramInt = 1;
    }
  }
  
  public ParceledListSlice<SecurityLog.SecurityEvent> retrievePreRebootSecurityLogs(ComponentName paramComponentName)
  {
    Preconditions.checkNotNull(paramComponentName);
    ensureDeviceOwnerManagingSingleUser(paramComponentName);
    if (!this.mContext.getResources().getBoolean(17957057)) {
      return null;
    }
    paramComponentName = new ArrayList();
    try
    {
      SecurityLog.readPreviousEvents(paramComponentName);
      paramComponentName = new ParceledListSlice(paramComponentName);
      return paramComponentName;
    }
    catch (IOException paramComponentName)
    {
      Slog.w("DevicePolicyManagerService", "Fail to read previous events", paramComponentName);
    }
    return new ParceledListSlice(Collections.emptyList());
  }
  
  public ParceledListSlice<SecurityLog.SecurityEvent> retrieveSecurityLogs(ComponentName paramComponentName)
  {
    Object localObject = null;
    Preconditions.checkNotNull(paramComponentName);
    ensureDeviceOwnerManagingSingleUser(paramComponentName);
    List localList = this.mSecurityLogMonitor.retrieveLogs();
    paramComponentName = (ComponentName)localObject;
    if (localList != null) {
      paramComponentName = new ParceledListSlice(localList);
    }
    return paramComponentName;
  }
  
  void sendAdminCommandLocked(ActiveAdmin paramActiveAdmin, String paramString)
  {
    sendAdminCommandLocked(paramActiveAdmin, paramString, null);
  }
  
  void sendAdminCommandLocked(ActiveAdmin paramActiveAdmin, String paramString, BroadcastReceiver paramBroadcastReceiver)
  {
    sendAdminCommandLocked(paramActiveAdmin, paramString, null, paramBroadcastReceiver);
  }
  
  void sendAdminCommandLocked(ActiveAdmin paramActiveAdmin, String paramString, Bundle paramBundle, BroadcastReceiver paramBroadcastReceiver)
  {
    Intent localIntent = new Intent(paramString);
    localIntent.setComponent(paramActiveAdmin.info.getComponent());
    if (paramString.equals("android.app.action.ACTION_PASSWORD_EXPIRING")) {
      localIntent.putExtra("expiration", paramActiveAdmin.passwordExpirationDate);
    }
    if (paramBundle != null) {
      localIntent.putExtras(paramBundle);
    }
    if (paramBroadcastReceiver != null)
    {
      this.mContext.sendOrderedBroadcastAsUser(localIntent, paramActiveAdmin.getUserHandle(), null, paramBroadcastReceiver, this.mHandler, -1, null, null);
      return;
    }
    this.mContext.sendBroadcastAsUser(localIntent, paramActiveAdmin.getUserHandle());
  }
  
  void sendAdminCommandLocked(String paramString, int paramInt1, int paramInt2)
  {
    DevicePolicyData localDevicePolicyData = getUserData(paramInt2);
    int i = localDevicePolicyData.mAdminList.size();
    if (i > 0)
    {
      paramInt2 = 0;
      while (paramInt2 < i)
      {
        ActiveAdmin localActiveAdmin = (ActiveAdmin)localDevicePolicyData.mAdminList.get(paramInt2);
        if (localActiveAdmin.info.usesPolicy(paramInt1)) {
          sendAdminCommandLocked(localActiveAdmin, paramString);
        }
        paramInt2 += 1;
      }
    }
  }
  
  void sendDeviceOwnerCommand(String paramString, Bundle paramBundle)
  {
    try
    {
      paramString = new Intent(paramString);
      paramString.setComponent(this.mOwners.getDeviceOwnerComponent());
      if (paramBundle != null) {
        paramString.putExtras(paramBundle);
      }
      this.mContext.sendBroadcastAsUser(paramString, UserHandle.of(this.mOwners.getDeviceOwnerUserId()));
      return;
    }
    finally {}
  }
  
  /* Error */
  public void setAccountManagementDisabled(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_1
    //   9: ldc_w 2393
    //   12: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_0
    //   17: monitorenter
    //   18: aload_0
    //   19: aload_1
    //   20: iconst_m1
    //   21: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   24: astore_1
    //   25: iload_3
    //   26: ifeq +24 -> 50
    //   29: aload_1
    //   30: getfield 2705	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:accountTypesWithManagementDisabled	Ljava/util/Set;
    //   33: aload_2
    //   34: invokeinterface 339 2 0
    //   39: pop
    //   40: aload_0
    //   41: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   44: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: aload_1
    //   51: getfield 2705	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:accountTypesWithManagementDisabled	Ljava/util/Set;
    //   54: aload_2
    //   55: invokeinterface 742 2 0
    //   60: pop
    //   61: goto -21 -> 40
    //   64: astore_1
    //   65: aload_0
    //   66: monitorexit
    //   67: aload_1
    //   68: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	69	0	this	DevicePolicyManagerService
    //   0	69	1	paramComponentName	ComponentName
    //   0	69	2	paramString	String
    //   0	69	3	paramBoolean	boolean
    // Exception table:
    //   from	to	target	type
    //   18	25	64	finally
    //   29	40	64	finally
    //   40	47	64	finally
    //   50	61	64	finally
  }
  
  public void setActiveAdmin(ComponentName paramComponentName, boolean paramBoolean, int paramInt)
  {
    if (!this.mHasFeature) {
      return;
    }
    setActiveAdmin(paramComponentName, paramBoolean, paramInt, null);
  }
  
  /* Error */
  public void setActivePasswordState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: iload 9
    //   11: invokespecial 2160	com/android/server/devicepolicy/DevicePolicyManagerService:enforceFullCrossUsersPermission	(I)V
    //   14: aload_0
    //   15: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   18: ldc_w 2670
    //   21: aconst_null
    //   22: invokevirtual 874	android/content/Context:enforceCallingOrSelfPermission	(Ljava/lang/String;Ljava/lang/String;)V
    //   25: iload_1
    //   26: istore 10
    //   28: iload_2
    //   29: istore 11
    //   31: iload_3
    //   32: istore 12
    //   34: iload 4
    //   36: istore 13
    //   38: iload 5
    //   40: istore 14
    //   42: iload 6
    //   44: istore 15
    //   46: iload 7
    //   48: istore 16
    //   50: iload 8
    //   52: istore 17
    //   54: aload_0
    //   55: iload 9
    //   57: invokespecial 225	com/android/server/devicepolicy/DevicePolicyManagerService:isManagedProfile	(I)Z
    //   60: ifeq +41 -> 101
    //   63: aload_0
    //   64: iload 9
    //   66: invokespecial 1115	com/android/server/devicepolicy/DevicePolicyManagerService:isSeparateProfileChallengeEnabled	(I)Z
    //   69: ifeq +106 -> 175
    //   72: iload 8
    //   74: istore 17
    //   76: iload 7
    //   78: istore 16
    //   80: iload 6
    //   82: istore 15
    //   84: iload 5
    //   86: istore 14
    //   88: iload 4
    //   90: istore 13
    //   92: iload_3
    //   93: istore 12
    //   95: iload_2
    //   96: istore 11
    //   98: iload_1
    //   99: istore 10
    //   101: iload 10
    //   103: invokestatic 3524	com/android/server/devicepolicy/DevicePolicyManagerService:validateQualityConstant	(I)V
    //   106: aload_0
    //   107: iload 9
    //   109: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   112: astore 18
    //   114: aload_0
    //   115: monitorenter
    //   116: aload 18
    //   118: iload 10
    //   120: putfield 1375	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordQuality	I
    //   123: aload 18
    //   125: iload 11
    //   127: putfield 1379	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLength	I
    //   130: aload 18
    //   132: iload 12
    //   134: putfield 1398	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLetters	I
    //   137: aload 18
    //   139: iload 14
    //   141: putfield 1392	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordLowerCase	I
    //   144: aload 18
    //   146: iload 13
    //   148: putfield 1386	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordUpperCase	I
    //   151: aload 18
    //   153: iload 15
    //   155: putfield 1404	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNumeric	I
    //   158: aload 18
    //   160: iload 16
    //   162: putfield 1410	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordSymbols	I
    //   165: aload 18
    //   167: iload 17
    //   169: putfield 1416	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mActivePasswordNonLetter	I
    //   172: aload_0
    //   173: monitorexit
    //   174: return
    //   175: iconst_0
    //   176: istore 10
    //   178: iconst_0
    //   179: istore 11
    //   181: iconst_0
    //   182: istore 12
    //   184: iconst_0
    //   185: istore 13
    //   187: iconst_0
    //   188: istore 14
    //   190: iconst_0
    //   191: istore 15
    //   193: iconst_0
    //   194: istore 16
    //   196: iconst_0
    //   197: istore 17
    //   199: goto -98 -> 101
    //   202: astore 18
    //   204: aload_0
    //   205: monitorexit
    //   206: aload 18
    //   208: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	209	0	this	DevicePolicyManagerService
    //   0	209	1	paramInt1	int
    //   0	209	2	paramInt2	int
    //   0	209	3	paramInt3	int
    //   0	209	4	paramInt4	int
    //   0	209	5	paramInt5	int
    //   0	209	6	paramInt6	int
    //   0	209	7	paramInt7	int
    //   0	209	8	paramInt8	int
    //   0	209	9	paramInt9	int
    //   26	151	10	i	int
    //   29	151	11	j	int
    //   32	151	12	k	int
    //   36	150	13	m	int
    //   40	149	14	n	int
    //   44	148	15	i1	int
    //   48	147	16	i2	int
    //   52	146	17	i3	int
    //   112	54	18	localDevicePolicyData	DevicePolicyData
    //   202	5	18	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   116	172	202	finally
  }
  
  public void setAffiliationIds(ComponentName paramComponentName, List<String> paramList)
  {
    paramList = new ArraySet(paramList);
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      getUserData(i).mAffiliationIds = paramList;
      saveSettingsLocked(i);
      if ((i != 0) && (isDeviceOwner(paramComponentName, i)))
      {
        getUserData(0).mAffiliationIds = paramList;
        saveSettingsLocked(0);
      }
      return;
    }
    finally {}
  }
  
  public boolean setAlwaysOnVpnPackage(ComponentName paramComponentName, String paramString, boolean paramBoolean)
    throws SecurityException
  {
    long l;
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      int i = this.mInjector.userHandleGetCallingUserId();
      l = this.mInjector.binderClearCallingIdentity();
      if (paramString != null) {}
      try
      {
        if (isPackageInstalledForUser(paramString, i))
        {
          if (((ConnectivityManager)this.mContext.getSystemService("connectivity")).setAlwaysOnVpnPackageForUser(i, paramString, paramBoolean)) {
            break label102;
          }
          throw new UnsupportedOperationException();
        }
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      this.mInjector.binderRestoreCallingIdentity(l);
    }
    finally {}
    return false;
    label102:
    this.mInjector.binderRestoreCallingIdentity(l);
    return true;
  }
  
  /* Error */
  public boolean setApplicationHidden(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore 4
    //   13: aload_0
    //   14: monitorenter
    //   15: aload_0
    //   16: aload_1
    //   17: iconst_m1
    //   18: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   21: pop
    //   22: aload_0
    //   23: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   26: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   29: lstore 5
    //   31: aload_0
    //   32: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   35: aload_2
    //   36: iload_3
    //   37: iload 4
    //   39: invokeinterface 3544 4 0
    //   44: istore_3
    //   45: aload_0
    //   46: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   49: lload 5
    //   51: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   54: aload_0
    //   55: monitorexit
    //   56: iload_3
    //   57: ireturn
    //   58: astore_1
    //   59: ldc 125
    //   61: ldc_w 3546
    //   64: aload_1
    //   65: invokestatic 620	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   68: pop
    //   69: aload_0
    //   70: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   73: lload 5
    //   75: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   78: aload_0
    //   79: monitorexit
    //   80: iconst_0
    //   81: ireturn
    //   82: astore_1
    //   83: aload_0
    //   84: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   87: lload 5
    //   89: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   92: aload_1
    //   93: athrow
    //   94: astore_1
    //   95: aload_0
    //   96: monitorexit
    //   97: aload_1
    //   98: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	99	0	this	DevicePolicyManagerService
    //   0	99	1	paramComponentName	ComponentName
    //   0	99	2	paramString	String
    //   0	99	3	paramBoolean	boolean
    //   11	27	4	i	int
    //   29	59	5	l	long
    // Exception table:
    //   from	to	target	type
    //   31	45	58	android/os/RemoteException
    //   31	45	82	finally
    //   59	69	82	finally
    //   15	31	94	finally
    //   45	54	94	finally
    //   69	78	94	finally
    //   83	94	94	finally
  }
  
  public void setApplicationRestrictions(ComponentName paramComponentName, String paramString, Bundle paramBundle)
  {
    enforceCanManageApplicationRestrictions(paramComponentName);
    paramComponentName = this.mInjector.binderGetCallingUserHandle();
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      this.mUserManager.setApplicationRestrictions(paramString, paramBundle, paramComponentName);
      return;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  public boolean setApplicationRestrictionsManagingPackage(ComponentName paramComponentName, String paramString)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      if ((paramString == null) || (isPackageInstalledForUser(paramString, i)))
      {
        getUserData(i).mApplicationRestrictionsManagingPackage = paramString;
        saveSettingsLocked(i);
        return true;
      }
      return false;
    }
    finally {}
  }
  
  /* Error */
  public void setAutoTimeRequired(ComponentName paramComponentName, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_1
    //   9: ldc_w 2393
    //   12: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   19: istore_3
    //   20: aload_0
    //   21: monitorenter
    //   22: aload_0
    //   23: aload_1
    //   24: bipush -2
    //   26: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   29: astore_1
    //   30: aload_1
    //   31: getfield 2758	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:requireAutoTime	Z
    //   34: iload_2
    //   35: if_icmpeq +13 -> 48
    //   38: aload_1
    //   39: iload_2
    //   40: putfield 2758	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:requireAutoTime	Z
    //   43: aload_0
    //   44: iload_3
    //   45: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   48: aload_0
    //   49: monitorexit
    //   50: iload_2
    //   51: ifeq +32 -> 83
    //   54: aload_0
    //   55: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   58: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   61: lstore 4
    //   63: aload_0
    //   64: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   67: ldc_w 357
    //   70: iconst_1
    //   71: invokevirtual 1991	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:settingsGlobalPutInt	(Ljava/lang/String;I)V
    //   74: aload_0
    //   75: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   78: lload 4
    //   80: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   83: return
    //   84: astore_1
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_1
    //   88: athrow
    //   89: astore_1
    //   90: aload_0
    //   91: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   94: lload 4
    //   96: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   99: aload_1
    //   100: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	this	DevicePolicyManagerService
    //   0	101	1	paramComponentName	ComponentName
    //   0	101	2	paramBoolean	boolean
    //   19	26	3	i	int
    //   61	34	4	l	long
    // Exception table:
    //   from	to	target	type
    //   22	48	84	finally
    //   63	74	89	finally
  }
  
  public void setBackupServiceEnabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramComponentName);
    if (!this.mHasFeature) {
      return;
    }
    ensureDeviceOwnerManagingSingleUser(paramComponentName);
    setBackupServiceEnabledInternal(paramBoolean);
  }
  
  public void setBluetoothContactSharingDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1);
      if (paramComponentName.disableBluetoothContactSharing != paramBoolean)
      {
        paramComponentName.disableBluetoothContactSharing = paramBoolean;
        saveSettingsLocked(UserHandle.getCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setCameraDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 8);
      if (paramComponentName.disableCamera != paramBoolean)
      {
        paramComponentName.disableCamera = paramBoolean;
        saveSettingsLocked(i);
      }
      pushUserRestrictions(i);
      return;
    }
    finally {}
  }
  
  public void setCertInstallerPackage(ComponentName paramComponentName, String paramString)
    throws SecurityException
  {
    int i = UserHandle.getCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      if ((getTargetSdk(paramComponentName.getPackageName(), i) < 24) || (paramString == null) || (isPackageInstalledForUser(paramString, i)))
      {
        getUserData(i).mDelegatedCertInstallerPackage = paramString;
        saveSettingsLocked(i);
        return;
      }
      throw new IllegalArgumentException("Package " + paramString + " is not installed on the current user");
    }
    finally {}
  }
  
  public void setCrossProfileCallerIdDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1);
      if (paramComponentName.disableCallerId != paramBoolean)
      {
        paramComponentName.disableCallerId = paramBoolean;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setCrossProfileContactsSearchDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1);
      if (paramComponentName.disableContactsSearch != paramBoolean)
      {
        paramComponentName.disableContactsSearch = paramBoolean;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public boolean setDeviceOwner(ComponentName paramComponentName, String paramString, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_1
    //   10: ifnull +80 -> 90
    //   13: aload_0
    //   14: aload_1
    //   15: invokevirtual 1050	android/content/ComponentName:getPackageName	()Ljava/lang/String;
    //   18: iload_3
    //   19: invokevirtual 3532	com/android/server/devicepolicy/DevicePolicyManagerService:isPackageInstalledForUser	(Ljava/lang/String;I)Z
    //   22: ifeq +68 -> 90
    //   25: aload_0
    //   26: monitorenter
    //   27: aload_0
    //   28: aload_1
    //   29: iload_3
    //   30: invokespecial 3567	com/android/server/devicepolicy/DevicePolicyManagerService:enforceCanSetDeviceOwnerLocked	(Landroid/content/ComponentName;I)V
    //   33: aload_0
    //   34: aload_1
    //   35: iload_3
    //   36: invokevirtual 1177	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminUncheckedLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   39: ifnull +18 -> 57
    //   42: aload_0
    //   43: iload_3
    //   44: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   47: getfield 1929	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mRemovingAdmins	Ljava/util/ArrayList;
    //   50: aload_1
    //   51: invokevirtual 2180	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   54: ifeq +70 -> 124
    //   57: new 910	java/lang/IllegalArgumentException
    //   60: dup
    //   61: new 697	java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   68: ldc_w 3569
    //   71: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: aload_1
    //   75: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   78: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   81: invokespecial 913	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   84: athrow
    //   85: astore_1
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_1
    //   89: athrow
    //   90: new 910	java/lang/IllegalArgumentException
    //   93: dup
    //   94: new 697	java/lang/StringBuilder
    //   97: dup
    //   98: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   101: ldc_w 3571
    //   104: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: aload_1
    //   108: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   111: ldc_w 3573
    //   114: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   117: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   120: invokespecial 913	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   123: athrow
    //   124: aload_0
    //   125: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   128: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   131: lstore 4
    //   133: aload_0
    //   134: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   137: invokevirtual 780	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getIBackupManager	()Landroid/app/backup/IBackupManager;
    //   140: ifnull +17 -> 157
    //   143: aload_0
    //   144: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   147: invokevirtual 780	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getIBackupManager	()Landroid/app/backup/IBackupManager;
    //   150: iconst_0
    //   151: iconst_0
    //   152: invokeinterface 786 3 0
    //   157: aload_0
    //   158: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   161: lload 4
    //   163: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   166: aload_0
    //   167: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   170: aload_1
    //   171: aload_2
    //   172: iload_3
    //   173: invokevirtual 3576	com/android/server/devicepolicy/Owners:setDeviceOwner	(Landroid/content/ComponentName;Ljava/lang/String;I)V
    //   176: aload_0
    //   177: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   180: invokevirtual 773	com/android/server/devicepolicy/Owners:writeDeviceOwner	()V
    //   183: aload_0
    //   184: invokespecial 776	com/android/server/devicepolicy/DevicePolicyManagerService:updateDeviceOwnerLocked	()V
    //   187: aload_0
    //   188: invokespecial 281	com/android/server/devicepolicy/DevicePolicyManagerService:setDeviceOwnerSystemPropertyLocked	()V
    //   191: new 1795	android/content/Intent
    //   194: dup
    //   195: ldc_w 3578
    //   198: invokespecial 2090	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   201: astore_2
    //   202: aload_0
    //   203: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   206: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   209: lstore 4
    //   211: aload_0
    //   212: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   215: aload_2
    //   216: new 536	android/os/UserHandle
    //   219: dup
    //   220: iload_3
    //   221: invokespecial 674	android/os/UserHandle:<init>	(I)V
    //   224: invokevirtual 2099	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   227: aload_0
    //   228: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   231: lload 4
    //   233: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   236: ldc 125
    //   238: new 697	java/lang/StringBuilder
    //   241: dup
    //   242: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   245: ldc_w 3580
    //   248: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   251: aload_1
    //   252: invokevirtual 1099	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   255: ldc_w 1456
    //   258: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   261: iload_3
    //   262: invokevirtual 707	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   265: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   268: invokestatic 1935	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   271: pop
    //   272: aload_0
    //   273: monitorexit
    //   274: iconst_1
    //   275: ireturn
    //   276: astore_1
    //   277: new 695	java/lang/IllegalStateException
    //   280: dup
    //   281: ldc_w 3582
    //   284: aload_1
    //   285: invokespecial 791	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   288: athrow
    //   289: astore_1
    //   290: aload_0
    //   291: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   294: lload 4
    //   296: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   299: aload_1
    //   300: athrow
    //   301: astore_1
    //   302: aload_0
    //   303: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   306: lload 4
    //   308: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   311: aload_1
    //   312: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	313	0	this	DevicePolicyManagerService
    //   0	313	1	paramComponentName	ComponentName
    //   0	313	2	paramString	String
    //   0	313	3	paramInt	int
    //   131	176	4	l	long
    // Exception table:
    //   from	to	target	type
    //   27	57	85	finally
    //   57	85	85	finally
    //   124	133	85	finally
    //   157	211	85	finally
    //   227	272	85	finally
    //   290	301	85	finally
    //   302	313	85	finally
    //   133	157	276	android/os/RemoteException
    //   133	157	289	finally
    //   277	289	289	finally
    //   211	227	301	finally
  }
  
  /* Error */
  public void setDeviceOwnerLockScreenInfo(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: ldc_w 2393
    //   7: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   10: pop
    //   11: aload_0
    //   12: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   15: ifne +4 -> 19
    //   18: return
    //   19: aload_0
    //   20: monitorenter
    //   21: aload_0
    //   22: aload_1
    //   23: bipush -2
    //   25: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   28: pop
    //   29: aload_0
    //   30: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   33: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   36: lstore_3
    //   37: aload_0
    //   38: getfield 493	com/android/server/devicepolicy/DevicePolicyManagerService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   41: astore 6
    //   43: aload 5
    //   45: astore_1
    //   46: aload_2
    //   47: ifnull +10 -> 57
    //   50: aload_2
    //   51: invokeinterface 1169 1 0
    //   56: astore_1
    //   57: aload 6
    //   59: aload_1
    //   60: invokevirtual 3587	com/android/internal/widget/LockPatternUtils:setDeviceOwnerInfo	(Ljava/lang/String;)V
    //   63: aload_0
    //   64: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   67: lload_3
    //   68: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   71: aload_0
    //   72: monitorexit
    //   73: return
    //   74: astore_1
    //   75: aload_0
    //   76: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   79: lload_3
    //   80: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   83: aload_1
    //   84: athrow
    //   85: astore_1
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_1
    //   89: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	DevicePolicyManagerService
    //   0	90	1	paramComponentName	ComponentName
    //   0	90	2	paramCharSequence	CharSequence
    //   36	44	3	l	long
    //   1	43	5	localObject	Object
    //   41	17	6	localLockPatternUtils	LockPatternUtils
    // Exception table:
    //   from	to	target	type
    //   37	43	74	finally
    //   50	57	74	finally
    //   57	63	74	finally
    //   21	37	85	finally
    //   63	71	85	finally
    //   75	85	85	finally
  }
  
  public void setDeviceProvisioningConfigApplied()
  {
    enforceManageUsers();
    try
    {
      getUserData(0).mDeviceProvisioningConfigApplied = true;
      saveSettingsLocked(0);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setForceEphemeralUsers(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    boolean bool;
    if ((!paramBoolean) || (this.mInjector.userManagerIsSplitSystemUser())) {
      bool = false;
    }
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -2);
      if (paramComponentName.forceEphemeralUsers != paramBoolean)
      {
        paramComponentName.forceEphemeralUsers = paramBoolean;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
        this.mUserManagerInternal.setForceEphemeralUsers(paramBoolean);
        bool = paramBoolean;
      }
      if (bool) {
        l = this.mInjector.binderClearCallingIdentity();
      }
    }
    finally {}
    try
    {
      this.mUserManagerInternal.removeAllUsers();
      this.mInjector.binderRestoreCallingIdentity(l);
      return;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
    throw new UnsupportedOperationException("Cannot force ephemeral users on systems without split system user.");
  }
  
  public ComponentName setGlobalProxy(ComponentName paramComponentName, String paramString1, String paramString2)
  {
    if (!this.mHasFeature) {
      return null;
    }
    for (;;)
    {
      DevicePolicyData localDevicePolicyData;
      ActiveAdmin localActiveAdmin;
      try
      {
        Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
        localDevicePolicyData = getUserData(0);
        localActiveAdmin = getActiveAdminForCallerLocked(paramComponentName, 5);
        Iterator localIterator = localDevicePolicyData.mAdminMap.keySet().iterator();
        if (localIterator.hasNext())
        {
          ComponentName localComponentName = (ComponentName)localIterator.next();
          if (!((ActiveAdmin)localDevicePolicyData.mAdminMap.get(localComponentName)).specifiesGlobalProxy) {
            continue;
          }
          boolean bool = localComponentName.equals(paramComponentName);
          if (bool) {
            continue;
          }
          return localComponentName;
        }
        if (UserHandle.getCallingUserId() != 0)
        {
          Slog.w("DevicePolicyManagerService", "Only the owner is allowed to set the global proxy. User " + UserHandle.getCallingUserId() + " is not permitted.");
          return null;
        }
        if (paramString1 == null)
        {
          localActiveAdmin.specifiesGlobalProxy = false;
          localActiveAdmin.globalProxySpec = null;
          localActiveAdmin.globalProxyExclusionList = null;
          l = this.mInjector.binderClearCallingIdentity();
        }
      }
      finally {}
      try
      {
        resetGlobalProxyLocked(localDevicePolicyData);
        this.mInjector.binderRestoreCallingIdentity(l);
        return null;
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      localActiveAdmin.specifiesGlobalProxy = true;
      localActiveAdmin.globalProxySpec = paramString1;
      localActiveAdmin.globalProxyExclusionList = paramString2;
    }
  }
  
  public void setGlobalSetting(ComponentName paramComponentName, String paramString1, String paramString2)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      if (GLOBAL_SETTINGS_DEPRECATED.contains(paramString1))
      {
        Log.i("DevicePolicyManagerService", "Global setting no longer supported: " + paramString1);
        return;
      }
      if (!GLOBAL_SETTINGS_WHITELIST.contains(paramString1)) {
        throw new SecurityException(String.format("Permission denial: device owners cannot update %1$s", new Object[] { paramString1 }));
      }
    }
    finally {}
    if ("stay_on_while_plugged_in".equals(paramString1))
    {
      l = getMaximumTimeToLock(paramComponentName, this.mInjector.userHandleGetCallingUserId(), false);
      if ((l > 0L) && (l < 2147483647L)) {
        return;
      }
    }
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      this.mInjector.settingsGlobalPutString(paramString1, paramString2);
      this.mInjector.binderRestoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramComponentName = finally;
      this.mInjector.binderRestoreCallingIdentity(l);
      throw paramComponentName;
    }
  }
  
  public void setKeepUninstalledPackages(ComponentName paramComponentName, List<String> paramList)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    Preconditions.checkNotNull(paramList, "packageList is null");
    int i = UserHandle.getCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2).keepUninstalledPackages = paramList;
      saveSettingsLocked(i);
      this.mInjector.getPackageManagerInternal().setKeepUninstalledPackages(paramList);
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public boolean setKeyguardDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: aload_1
    //   12: bipush -2
    //   14: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   17: pop
    //   18: aload_0
    //   19: monitorexit
    //   20: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   23: istore_3
    //   24: aload_0
    //   25: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   28: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   31: lstore 5
    //   33: iload_2
    //   34: ifeq +34 -> 68
    //   37: aload_0
    //   38: getfield 493	com/android/server/devicepolicy/DevicePolicyManagerService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   41: iload_3
    //   42: invokevirtual 1451	com/android/internal/widget/LockPatternUtils:isSecure	(I)Z
    //   45: istore 4
    //   47: iload 4
    //   49: ifeq +19 -> 68
    //   52: aload_0
    //   53: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   56: lload 5
    //   58: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   61: iconst_0
    //   62: ireturn
    //   63: astore_1
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_1
    //   67: athrow
    //   68: aload_0
    //   69: getfield 493	com/android/server/devicepolicy/DevicePolicyManagerService:mLockPatternUtils	Lcom/android/internal/widget/LockPatternUtils;
    //   72: iload_2
    //   73: iload_3
    //   74: invokevirtual 3624	com/android/internal/widget/LockPatternUtils:setLockScreenDisabled	(ZI)V
    //   77: aload_0
    //   78: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   81: lload 5
    //   83: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   86: iconst_1
    //   87: ireturn
    //   88: astore_1
    //   89: aload_0
    //   90: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   93: lload 5
    //   95: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   98: aload_1
    //   99: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	this	DevicePolicyManagerService
    //   0	100	1	paramComponentName	ComponentName
    //   0	100	2	paramBoolean	boolean
    //   23	51	3	i	int
    //   45	3	4	bool	boolean
    //   31	63	5	l	long
    // Exception table:
    //   from	to	target	type
    //   10	18	63	finally
    //   37	47	88	finally
    //   68	77	88	finally
  }
  
  public void setKeyguardDisabledFeatures(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int j = this.mInjector.userHandleGetCallingUserId();
    int i = paramInt;
    if (isManagedProfile(j)) {
      if (!paramBoolean) {
        break label82;
      }
    }
    for (i = paramInt & 0x30;; i = paramInt & 0x38) {
      label82:
      try
      {
        paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 9, paramBoolean);
        if (paramComponentName.disabledKeyguardFeatures != i)
        {
          paramComponentName.disabledKeyguardFeatures = i;
          saveSettingsLocked(j);
        }
        return;
      }
      finally {}
    }
  }
  
  public void setLockTaskPackages(ComponentName paramComponentName, String[] paramArrayOfString)
    throws SecurityException
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      ActiveAdmin localActiveAdmin1 = getActiveAdminWithPolicyForUidLocked(paramComponentName, -2, this.mInjector.binderGetCallingUid());
      ActiveAdmin localActiveAdmin2 = getActiveAdminWithPolicyForUidLocked(paramComponentName, -1, this.mInjector.binderGetCallingUid());
      if ((localActiveAdmin1 != null) || ((localActiveAdmin2 != null) && (isAffiliatedUser())))
      {
        setLockTaskPackagesLocked(this.mInjector.userHandleGetCallingUserId(), new ArrayList(Arrays.asList(paramArrayOfString)));
        return;
      }
      throw new SecurityException("Admin " + paramComponentName + " is neither the device owner or affiliated user's profile owner.");
    }
    finally {}
  }
  
  public void setLongSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      paramComponentName = getActiveAdminForUidLocked(paramComponentName, this.mInjector.binderGetCallingUid());
      if (!TextUtils.equals(paramComponentName.longSupportMessage, paramCharSequence))
      {
        paramComponentName.longSupportMessage = paramCharSequence;
        saveSettingsLocked(i);
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setMasterVolumeMuted(ComponentName paramComponentName, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      setUserRestriction(paramComponentName, "disallow_unmute_device", paramBoolean);
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setMaximumFailedPasswordsForWipe(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, 4, paramBoolean);
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 1, paramBoolean);
      if (paramComponentName.maximumFailedPasswordsForWipe != paramInt)
      {
        paramComponentName.maximumFailedPasswordsForWipe = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setMaximumTimeToLock(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 3, paramBoolean);
      if (paramComponentName.maximumTimeToUnlock != paramLong)
      {
        paramComponentName.maximumTimeToUnlock = paramLong;
        saveSettingsLocked(i);
        updateMaximumTimeToLockLocked(i);
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setOrganizationColor(ComponentName paramComponentName, int paramInt)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    enforceManagedProfile(i, "set organization color");
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1).organizationColor = paramInt;
      saveSettingsLocked(i);
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setOrganizationColorForUser(int paramInt1, int paramInt2)
  {
    if (!this.mHasFeature) {
      return;
    }
    enforceFullCrossUsersPermission(paramInt2);
    enforceManageUsers();
    enforceManagedProfile(paramInt2, "set organization color");
    try
    {
      getProfileOwnerAdminLocked(paramInt2).organizationColor = paramInt1;
      saveSettingsLocked(paramInt2);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void setOrganizationName(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   7: ifne +4 -> 11
    //   10: return
    //   11: aload_1
    //   12: ldc_w 2393
    //   15: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   18: pop
    //   19: aload_0
    //   20: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   23: invokevirtual 986	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:userHandleGetCallingUserId	()I
    //   26: istore_3
    //   27: aload_0
    //   28: iload_3
    //   29: ldc_w 3654
    //   32: invokespecial 2724	com/android/server/devicepolicy/DevicePolicyManagerService:enforceManagedProfile	(ILjava/lang/String;)V
    //   35: aload_0
    //   36: monitorenter
    //   37: aload_0
    //   38: aload_1
    //   39: iconst_m1
    //   40: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   43: astore 5
    //   45: aload 5
    //   47: getfield 2852	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:organizationName	Ljava/lang/String;
    //   50: aload_2
    //   51: invokestatic 3638	android/text/TextUtils:equals	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z
    //   54: ifne +33 -> 87
    //   57: aload 4
    //   59: astore_1
    //   60: aload_2
    //   61: ifnull +15 -> 76
    //   64: aload_2
    //   65: invokeinterface 3655 1 0
    //   70: ifne +20 -> 90
    //   73: aload 4
    //   75: astore_1
    //   76: aload 5
    //   78: aload_1
    //   79: putfield 2852	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:organizationName	Ljava/lang/String;
    //   82: aload_0
    //   83: iload_3
    //   84: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   87: aload_0
    //   88: monitorexit
    //   89: return
    //   90: aload_2
    //   91: invokeinterface 1169 1 0
    //   96: astore_1
    //   97: goto -21 -> 76
    //   100: astore_1
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_1
    //   104: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	DevicePolicyManagerService
    //   0	105	1	paramComponentName	ComponentName
    //   0	105	2	paramCharSequence	CharSequence
    //   26	58	3	i	int
    //   1	73	4	localObject	Object
    //   43	34	5	localActiveAdmin	ActiveAdmin
    // Exception table:
    //   from	to	target	type
    //   37	57	100	finally
    //   64	73	100	finally
    //   76	87	100	finally
    //   90	97	100	finally
  }
  
  /* Error */
  public String[] setPackagesSuspended(ComponentName paramComponentName, String[] paramArrayOfString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore 4
    //   13: aload_0
    //   14: monitorenter
    //   15: aload_0
    //   16: aload_1
    //   17: iconst_m1
    //   18: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   21: pop
    //   22: aload_0
    //   23: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   26: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   29: lstore 5
    //   31: aload_0
    //   32: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   35: aload_2
    //   36: iload_3
    //   37: iload 4
    //   39: invokeinterface 3661 4 0
    //   44: astore_1
    //   45: aload_0
    //   46: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   49: lload 5
    //   51: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   54: aload_0
    //   55: monitorexit
    //   56: aload_1
    //   57: areturn
    //   58: astore_1
    //   59: ldc 125
    //   61: ldc_w 3190
    //   64: aload_1
    //   65: invokestatic 620	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   68: pop
    //   69: aload_0
    //   70: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   73: lload 5
    //   75: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   78: aload_0
    //   79: monitorexit
    //   80: aload_2
    //   81: areturn
    //   82: astore_1
    //   83: aload_0
    //   84: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   87: lload 5
    //   89: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   92: aload_1
    //   93: athrow
    //   94: astore_1
    //   95: aload_0
    //   96: monitorexit
    //   97: aload_1
    //   98: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	99	0	this	DevicePolicyManagerService
    //   0	99	1	paramComponentName	ComponentName
    //   0	99	2	paramArrayOfString	String[]
    //   0	99	3	paramBoolean	boolean
    //   11	27	4	i	int
    //   29	59	5	l	long
    // Exception table:
    //   from	to	target	type
    //   31	45	58	android/os/RemoteException
    //   31	45	82	finally
    //   59	69	82	finally
    //   15	31	94	finally
    //   45	54	94	finally
    //   69	78	94	finally
    //   83	94	94	finally
  }
  
  /* Error */
  public void setPasswordExpirationTimeout(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_1
    //   9: ldc_w 2393
    //   12: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: lload_2
    //   17: ldc_w 3664
    //   20: invokestatic 3668	com/android/internal/util/Preconditions:checkArgumentNonnegative	(JLjava/lang/String;)J
    //   23: pop2
    //   24: aload_0
    //   25: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   28: invokevirtual 986	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:userHandleGetCallingUserId	()I
    //   31: istore 5
    //   33: aload_0
    //   34: monitorenter
    //   35: aload_0
    //   36: aload_1
    //   37: bipush 6
    //   39: iload 4
    //   41: invokevirtual 2785	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;IZ)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   44: astore_1
    //   45: lload_2
    //   46: lconst_0
    //   47: lcmp
    //   48: ifle +90 -> 138
    //   51: lload_2
    //   52: invokestatic 1279	java/lang/System:currentTimeMillis	()J
    //   55: ladd
    //   56: lstore 6
    //   58: aload_1
    //   59: lload 6
    //   61: putfield 1243	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:passwordExpirationDate	J
    //   64: aload_1
    //   65: lload_2
    //   66: putfield 1285	com/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin:passwordExpirationTimeout	J
    //   69: lload_2
    //   70: lconst_0
    //   71: lcmp
    //   72: ifle +45 -> 117
    //   75: ldc 125
    //   77: new 697	java/lang/StringBuilder
    //   80: dup
    //   81: invokespecial 698	java/lang/StringBuilder:<init>	()V
    //   84: ldc_w 3670
    //   87: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: iconst_2
    //   91: iconst_2
    //   92: invokestatic 3676	java/text/DateFormat:getDateTimeInstance	(II)Ljava/text/DateFormat;
    //   95: new 3678	java/util/Date
    //   98: dup
    //   99: lload 6
    //   101: invokespecial 3680	java/util/Date:<init>	(J)V
    //   104: invokevirtual 3683	java/text/DateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   107: invokevirtual 704	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: invokevirtual 716	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokestatic 837	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: aload_0
    //   118: iload 5
    //   120: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   123: aload_0
    //   124: aload_0
    //   125: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   128: iload 5
    //   130: iload 4
    //   132: invokespecial 1295	com/android/server/devicepolicy/DevicePolicyManagerService:setExpirationAlarmCheckLocked	(Landroid/content/Context;IZ)V
    //   135: aload_0
    //   136: monitorexit
    //   137: return
    //   138: lconst_0
    //   139: lstore 6
    //   141: goto -83 -> 58
    //   144: astore_1
    //   145: aload_0
    //   146: monitorexit
    //   147: aload_1
    //   148: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	DevicePolicyManagerService
    //   0	149	1	paramComponentName	ComponentName
    //   0	149	2	paramLong	long
    //   0	149	4	paramBoolean	boolean
    //   31	98	5	i	int
    //   56	84	6	l	long
    // Exception table:
    //   from	to	target	type
    //   35	45	144	finally
    //   51	58	144	finally
    //   58	69	144	finally
    //   75	117	144	finally
    //   117	135	144	finally
  }
  
  public void setPasswordHistoryLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.passwordHistoryLength != paramInt)
      {
        paramComponentName.passwordHistoryLength = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordMinimumLength(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.minimumPasswordLength != paramInt)
      {
        paramComponentName.minimumPasswordLength = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordMinimumLetters(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.minimumPasswordLetters != paramInt)
      {
        paramComponentName.minimumPasswordLetters = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordMinimumLowerCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.minimumPasswordLowerCase != paramInt)
      {
        paramComponentName.minimumPasswordLowerCase = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordMinimumNonLetter(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.minimumPasswordNonLetter != paramInt)
      {
        paramComponentName.minimumPasswordNonLetter = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordMinimumNumeric(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.minimumPasswordNumeric != paramInt)
      {
        paramComponentName.minimumPasswordNumeric = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordMinimumSymbols(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.minimumPasswordSymbols != paramInt)
      {
        paramComponentName.minimumPasswordSymbols = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordMinimumUpperCase(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.minimumPasswordUpperCase != paramInt)
      {
        paramComponentName.minimumPasswordUpperCase = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setPasswordQuality(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    validateQualityConstant(paramInt);
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 0, paramBoolean);
      if (paramComponentName.passwordQuality != paramInt)
      {
        paramComponentName.passwordQuality = paramInt;
        saveSettingsLocked(this.mInjector.userHandleGetCallingUserId());
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean setPermissionGrantState(ComponentName paramComponentName, String paramString1, String paramString2, int paramInt)
    throws RemoteException
  {
    UserHandle localUserHandle = this.mInjector.binderGetCallingUserHandle();
    for (;;)
    {
      long l;
      try
      {
        getActiveAdminForCallerLocked(paramComponentName, -1);
        l = this.mInjector.binderClearCallingIdentity();
        try
        {
          int i = getTargetSdk(paramString1, localUserHandle.getIdentifier());
          if (i < 23)
          {
            this.mInjector.binderRestoreCallingIdentity(l);
            return false;
          }
          paramComponentName = this.mContext.getPackageManager();
          switch (paramInt)
          {
          }
        }
        catch (SecurityException paramComponentName)
        {
          this.mInjector.binderRestoreCallingIdentity(l);
          return false;
          paramComponentName.revokeRuntimePermission(paramString1, paramString2, localUserHandle);
          paramComponentName.updatePermissionFlags(paramString2, paramString1, 4, 4, localUserHandle);
          continue;
        }
        finally
        {
          this.mInjector.binderRestoreCallingIdentity(l);
        }
        this.mInjector.binderRestoreCallingIdentity(l);
        return true;
      }
      finally {}
      paramComponentName.grantRuntimePermission(paramString1, paramString2, localUserHandle);
      paramComponentName.updatePermissionFlags(paramString2, paramString1, 4, 4, localUserHandle);
      continue;
      paramComponentName.updatePermissionFlags(paramString2, paramString1, 4, 0, localUserHandle);
    }
  }
  
  public void setPermissionPolicy(ComponentName paramComponentName, int paramInt)
    throws RemoteException
  {
    int i = UserHandle.getCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      paramComponentName = getUserData(i);
      if (paramComponentName.mPermissionPolicy != paramInt)
      {
        paramComponentName.mPermissionPolicy = paramInt;
        saveSettingsLocked(i);
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean setPermittedAccessibilityServices(ComponentName paramComponentName, List paramList)
  {
    if (!this.mHasFeature) {
      return false;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    if (paramList != null)
    {
      int i = UserHandle.getCallingUserId();
      long l = this.mInjector.binderClearCallingIdentity();
      try
      {
        Object localObject1 = getUserInfo(i);
        if (((UserInfo)localObject1).isManagedProfile()) {
          i = ((UserInfo)localObject1).profileGroupId;
        }
        Object localObject2 = getAccessibilityManagerForUser(i).getEnabledAccessibilityServiceList(-1);
        this.mInjector.binderRestoreCallingIdentity(l);
        if (localObject2 == null) {
          break label172;
        }
        localObject1 = new ArrayList();
        localObject2 = ((Iterable)localObject2).iterator();
        while (((Iterator)localObject2).hasNext()) {
          ((List)localObject1).add(((AccessibilityServiceInfo)((Iterator)localObject2).next()).getResolveInfo().serviceInfo.packageName);
        }
        if (checkPackagesInPermittedListOrSystem((List)localObject1, paramList, i)) {
          break label172;
        }
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      Slog.e("DevicePolicyManagerService", "Cannot set permitted accessibility services, because it contains already enabled accesibility services.");
      return false;
    }
    try
    {
      label172:
      getActiveAdminForCallerLocked(paramComponentName, -1).permittedAccessiblityServices = paramList;
      saveSettingsLocked(UserHandle.getCallingUserId());
      return true;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean setPermittedInputMethods(ComponentName paramComponentName, List paramList)
  {
    if (!this.mHasFeature) {
      return false;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    if (!checkCallerIsCurrentUserOrProfile()) {
      return false;
    }
    if (paramList != null)
    {
      Object localObject = ((InputMethodManager)this.mContext.getSystemService(InputMethodManager.class)).getEnabledInputMethodList();
      if (localObject != null)
      {
        ArrayList localArrayList = new ArrayList();
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          localArrayList.add(((InputMethodInfo)((Iterator)localObject).next()).getPackageName());
        }
        if (!checkPackagesInPermittedListOrSystem(localArrayList, paramList, this.mInjector.binderGetCallingUserHandle().getIdentifier()))
        {
          Slog.e("DevicePolicyManagerService", "Cannot set permitted input methods, because it contains already enabled input method.");
          return false;
        }
      }
    }
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1).permittedInputMethods = paramList;
      saveSettingsLocked(UserHandle.getCallingUserId());
      return true;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  /* Error */
  public void setProfileEnabled(ComponentName paramComponentName)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 512	com/android/server/devicepolicy/DevicePolicyManagerService:mHasFeature	Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_1
    //   9: ldc_w 2393
    //   12: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_0
    //   17: monitorenter
    //   18: aload_0
    //   19: aload_1
    //   20: iconst_m1
    //   21: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   24: pop
    //   25: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   28: istore_2
    //   29: aload_0
    //   30: iload_2
    //   31: ldc_w 3725
    //   34: invokespecial 2724	com/android/server/devicepolicy/DevicePolicyManagerService:enforceManagedProfile	(ILjava/lang/String;)V
    //   37: aload_0
    //   38: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   41: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   44: lstore_3
    //   45: aload_0
    //   46: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   49: iload_2
    //   50: invokevirtual 3728	android/os/UserManager:setUserEnabled	(I)V
    //   53: aload_0
    //   54: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   57: iload_2
    //   58: invokevirtual 1185	android/os/UserManager:getProfileParent	(I)Landroid/content/pm/UserInfo;
    //   61: astore_1
    //   62: new 1795	android/content/Intent
    //   65: dup
    //   66: ldc_w 559
    //   69: invokespecial 2090	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   72: astore 5
    //   74: aload 5
    //   76: ldc_w 3730
    //   79: new 536	android/os/UserHandle
    //   82: dup
    //   83: iload_2
    //   84: invokespecial 674	android/os/UserHandle:<init>	(I)V
    //   87: invokevirtual 2444	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
    //   90: pop
    //   91: aload 5
    //   93: ldc_w 3731
    //   96: invokevirtual 2454	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   99: pop
    //   100: aload_0
    //   101: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   104: aload 5
    //   106: new 536	android/os/UserHandle
    //   109: dup
    //   110: aload_1
    //   111: getfield 603	android/content/pm/UserInfo:id	I
    //   114: invokespecial 674	android/os/UserHandle:<init>	(I)V
    //   117: invokevirtual 2099	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   120: aload_0
    //   121: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   124: lload_3
    //   125: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   128: aload_0
    //   129: monitorexit
    //   130: return
    //   131: astore_1
    //   132: aload_0
    //   133: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   136: lload_3
    //   137: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   140: aload_1
    //   141: athrow
    //   142: astore_1
    //   143: aload_0
    //   144: monitorexit
    //   145: aload_1
    //   146: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	147	0	this	DevicePolicyManagerService
    //   0	147	1	paramComponentName	ComponentName
    //   28	56	2	i	int
    //   44	93	3	l	long
    //   72	33	5	localIntent	Intent
    // Exception table:
    //   from	to	target	type
    //   45	120	131	finally
    //   18	45	142	finally
    //   120	128	142	finally
    //   132	142	142	finally
  }
  
  public void setProfileName(ComponentName paramComponentName, String paramString)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = UserHandle.getCallingUserId();
    getActiveAdminForCallerLocked(paramComponentName, -1);
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      this.mUserManager.setUserName(i, paramString);
      return;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l);
    }
  }
  
  public boolean setProfileOwner(ComponentName paramComponentName, String paramString, int paramInt)
  {
    if (!this.mHasFeature) {
      return false;
    }
    if ((paramComponentName != null) && (isPackageInstalledForUser(paramComponentName.getPackageName(), paramInt))) {}
    try
    {
      enforceCanSetProfileOwnerLocked(paramComponentName, paramInt);
      if ((getActiveAdminUncheckedLocked(paramComponentName, paramInt) == null) || (getUserData(paramInt).mRemovingAdmins.contains(paramComponentName))) {
        throw new IllegalArgumentException("Not active admin: " + paramComponentName);
      }
    }
    finally
    {
      throw paramComponentName;
      throw new IllegalArgumentException("Component " + paramComponentName + " not installed for userId:" + paramInt);
      this.mOwners.setProfileOwner(paramComponentName, paramString, paramInt);
      this.mOwners.writeProfileOwner(paramInt);
    }
    return true;
  }
  
  /* Error */
  public void setRecommendedGlobalProxy(ComponentName paramComponentName, ProxyInfo paramProxyInfo)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: bipush -2
    //   6: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   9: pop
    //   10: aload_0
    //   11: monitorexit
    //   12: aload_0
    //   13: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   16: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   19: lstore_3
    //   20: aload_0
    //   21: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   24: ldc_w 2738
    //   27: invokevirtual 2236	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   30: checkcast 2740	android/net/ConnectivityManager
    //   33: aload_2
    //   34: invokevirtual 3750	android/net/ConnectivityManager:setGlobalProxy	(Landroid/net/ProxyInfo;)V
    //   37: aload_0
    //   38: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   41: lload_3
    //   42: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   45: return
    //   46: astore_1
    //   47: aload_0
    //   48: monitorexit
    //   49: aload_1
    //   50: athrow
    //   51: astore_1
    //   52: aload_0
    //   53: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   56: lload_3
    //   57: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   60: aload_1
    //   61: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	DevicePolicyManagerService
    //   0	62	1	paramComponentName	ComponentName
    //   0	62	2	paramProxyInfo	ProxyInfo
    //   19	38	3	l	long
    // Exception table:
    //   from	to	target	type
    //   2	10	46	finally
    //   20	37	51	finally
  }
  
  public void setRequiredStrongAuthTimeout(ComponentName paramComponentName, long paramLong, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    if (paramLong >= 0L) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool, "Timeout must not be a negative number.");
      long l = paramLong;
      if (paramLong != 0L)
      {
        l = paramLong;
        if (paramLong < 3600000L) {
          l = 3600000L;
        }
      }
      paramLong = l;
      if (l > 259200000L) {
        paramLong = 259200000L;
      }
      int i = this.mInjector.userHandleGetCallingUserId();
      try
      {
        paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1, paramBoolean);
        if (paramComponentName.strongAuthUnlockTimeout != paramLong)
        {
          paramComponentName.strongAuthUnlockTimeout = paramLong;
          saveSettingsLocked(i);
        }
        return;
      }
      finally {}
    }
  }
  
  public void setRestrictionsProvider(ComponentName paramComponentName1, ComponentName paramComponentName2)
  {
    Preconditions.checkNotNull(paramComponentName1, "ComponentName is null");
    try
    {
      getActiveAdminForCallerLocked(paramComponentName1, -1);
      int i = UserHandle.getCallingUserId();
      getUserData(i).mRestrictionsProvider = paramComponentName2;
      saveSettingsLocked(i);
      return;
    }
    finally
    {
      paramComponentName1 = finally;
      throw paramComponentName1;
    }
  }
  
  public void setScreenCaptureDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = UserHandle.getCallingUserId();
    try
    {
      paramComponentName = getActiveAdminForCallerLocked(paramComponentName, -1);
      if (paramComponentName.disableScreenCapture != paramBoolean)
      {
        paramComponentName.disableScreenCapture = paramBoolean;
        saveSettingsLocked(i);
        updateScreenCaptureDisabledInWindowManager(i, paramBoolean);
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public void setSecureSetting(ComponentName paramComponentName, String paramString1, String paramString2)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -1);
      if (isDeviceOwner(paramComponentName, i))
      {
        if (SECURE_SETTINGS_DEVICEOWNER_WHITELIST.contains(paramString1)) {
          break label109;
        }
        throw new SecurityException(String.format("Permission denial: Device owners cannot update %1$s", new Object[] { paramString1 }));
      }
    }
    finally {}
    if (!SECURE_SETTINGS_WHITELIST.contains(paramString1)) {
      throw new SecurityException(String.format("Permission denial: Profile owners cannot update %1$s", new Object[] { paramString1 }));
    }
    label109:
    long l = this.mInjector.binderClearCallingIdentity();
    try
    {
      this.mInjector.settingsSecurePutStringForUser(paramString1, paramString2, i);
      this.mInjector.binderRestoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramComponentName = finally;
      this.mInjector.binderRestoreCallingIdentity(l);
      throw paramComponentName;
    }
  }
  
  /* Error */
  public void setSecurityLoggingEnabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 427	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: aload_0
    //   6: aload_1
    //   7: invokespecial 3393	com/android/server/devicepolicy/DevicePolicyManagerService:ensureDeviceOwnerManagingSingleUser	(Landroid/content/ComponentName;)V
    //   10: aload_0
    //   11: monitorenter
    //   12: aload_0
    //   13: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   16: invokevirtual 2219	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:securityLogGetLoggingEnabledProperty	()Z
    //   19: istore_3
    //   20: iload_2
    //   21: iload_3
    //   22: if_icmpne +6 -> 28
    //   25: aload_0
    //   26: monitorexit
    //   27: return
    //   28: aload_0
    //   29: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   32: iload_2
    //   33: invokevirtual 832	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:securityLogSetLoggingEnabledProperty	(Z)V
    //   36: iload_2
    //   37: ifeq +13 -> 50
    //   40: aload_0
    //   41: getfield 498	com/android/server/devicepolicy/DevicePolicyManagerService:mSecurityLogMonitor	Lcom/android/server/devicepolicy/SecurityLogMonitor;
    //   44: invokevirtual 2222	com/android/server/devicepolicy/SecurityLogMonitor:start	()V
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: aload_0
    //   51: getfield 498	com/android/server/devicepolicy/DevicePolicyManagerService:mSecurityLogMonitor	Lcom/android/server/devicepolicy/SecurityLogMonitor;
    //   54: invokevirtual 3773	com/android/server/devicepolicy/SecurityLogMonitor:stop	()V
    //   57: goto -10 -> 47
    //   60: astore_1
    //   61: aload_0
    //   62: monitorexit
    //   63: aload_1
    //   64: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	this	DevicePolicyManagerService
    //   0	65	1	paramComponentName	ComponentName
    //   0	65	2	paramBoolean	boolean
    //   19	4	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   12	20	60	finally
    //   28	36	60	finally
    //   40	47	60	finally
    //   50	57	60	finally
  }
  
  public void setShortSupportMessage(ComponentName paramComponentName, CharSequence paramCharSequence)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = this.mInjector.userHandleGetCallingUserId();
    try
    {
      paramComponentName = getActiveAdminForUidLocked(paramComponentName, this.mInjector.binderGetCallingUid());
      if (!TextUtils.equals(paramComponentName.shortSupportMessage, paramCharSequence))
      {
        paramComponentName.shortSupportMessage = paramCharSequence;
        saveSettingsLocked(i);
      }
      return;
    }
    finally
    {
      paramComponentName = finally;
      throw paramComponentName;
    }
  }
  
  public boolean setStatusBarDisabled(ComponentName paramComponentName, boolean paramBoolean)
  {
    int i = UserHandle.getCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName, -2);
      paramComponentName = getUserData(i);
      if (paramComponentName.mStatusBarDisabled != paramBoolean)
      {
        boolean bool = setStatusBarDisabledInternal(paramBoolean, i);
        if (!bool) {
          return false;
        }
        paramComponentName.mStatusBarDisabled = paramBoolean;
        saveSettingsLocked(i);
      }
      return true;
    }
    finally {}
  }
  
  public int setStorageEncryption(ComponentName paramComponentName, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return 0;
    }
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    int i = UserHandle.getCallingUserId();
    if (i != 0) {}
    try
    {
      Slog.w("DevicePolicyManagerService", "Only owner/system user is allowed to set storage encryption. User " + UserHandle.getCallingUserId() + " is not permitted.");
      return 0;
    }
    finally {}
    paramComponentName = getActiveAdminForCallerLocked(paramComponentName, 7);
    boolean bool = isEncryptionSupported();
    if (!bool) {
      return 0;
    }
    if (paramComponentName.encryptionRequested != paramBoolean)
    {
      paramComponentName.encryptionRequested = paramBoolean;
      saveSettingsLocked(i);
    }
    paramComponentName = getUserData(0);
    paramBoolean = false;
    int j = paramComponentName.mAdminList.size();
    i = 0;
    while (i < j)
    {
      paramBoolean |= ((ActiveAdmin)paramComponentName.mAdminList.get(i)).encryptionRequested;
      i += 1;
    }
    setEncryptionRequested(paramBoolean);
    if (paramBoolean) {}
    for (i = 3;; i = 1) {
      return i;
    }
  }
  
  /* Error */
  public void setSystemUpdatePolicy(ComponentName paramComponentName, SystemUpdatePolicy paramSystemUpdatePolicy)
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnull +10 -> 11
    //   4: aload_2
    //   5: invokevirtual 3005	android/app/admin/SystemUpdatePolicy:isValid	()Z
    //   8: ifeq +54 -> 62
    //   11: aload_0
    //   12: monitorenter
    //   13: aload_0
    //   14: aload_1
    //   15: bipush -2
    //   17: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   20: pop
    //   21: aload_2
    //   22: ifnonnull +51 -> 73
    //   25: aload_0
    //   26: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   29: invokevirtual 3788	com/android/server/devicepolicy/Owners:clearSystemUpdatePolicy	()V
    //   32: aload_0
    //   33: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   36: invokevirtual 773	com/android/server/devicepolicy/Owners:writeDeviceOwner	()V
    //   39: aload_0
    //   40: monitorexit
    //   41: aload_0
    //   42: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   45: new 1795	android/content/Intent
    //   48: dup
    //   49: ldc_w 3790
    //   52: invokespecial 2090	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   55: getstatic 1732	android/os/UserHandle:SYSTEM	Landroid/os/UserHandle;
    //   58: invokevirtual 2099	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   61: return
    //   62: new 910	java/lang/IllegalArgumentException
    //   65: dup
    //   66: ldc_w 3792
    //   69: invokespecial 913	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   72: athrow
    //   73: aload_0
    //   74: getfield 452	com/android/server/devicepolicy/DevicePolicyManagerService:mOwners	Lcom/android/server/devicepolicy/Owners;
    //   77: aload_2
    //   78: invokevirtual 3795	com/android/server/devicepolicy/Owners:setSystemUpdatePolicy	(Landroid/app/admin/SystemUpdatePolicy;)V
    //   81: goto -49 -> 32
    //   84: astore_1
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_1
    //   88: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	89	0	this	DevicePolicyManagerService
    //   0	89	1	paramComponentName	ComponentName
    //   0	89	2	paramSystemUpdatePolicy	SystemUpdatePolicy
    // Exception table:
    //   from	to	target	type
    //   13	21	84	finally
    //   25	32	84	finally
    //   32	39	84	finally
    //   73	81	84	finally
  }
  
  public void setTrustAgentConfiguration(ComponentName paramComponentName1, ComponentName paramComponentName2, PersistableBundle paramPersistableBundle, boolean paramBoolean)
  {
    if (!this.mHasFeature) {
      return;
    }
    Preconditions.checkNotNull(paramComponentName1, "admin is null");
    Preconditions.checkNotNull(paramComponentName2, "agent is null");
    int i = UserHandle.getCallingUserId();
    try
    {
      getActiveAdminForCallerLocked(paramComponentName1, 9, paramBoolean).trustAgentInfos.put(paramComponentName2.flattenToString(), new DevicePolicyManagerService.ActiveAdmin.TrustAgentInfo(paramPersistableBundle));
      saveSettingsLocked(i);
      return;
    }
    finally
    {
      paramComponentName1 = finally;
      throw paramComponentName1;
    }
  }
  
  /* Error */
  public void setUninstallBlocked(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   11: istore 4
    //   13: aload_0
    //   14: monitorenter
    //   15: aload_0
    //   16: aload_1
    //   17: iconst_m1
    //   18: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   21: pop
    //   22: aload_0
    //   23: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   26: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   29: lstore 5
    //   31: aload_0
    //   32: getfield 476	com/android/server/devicepolicy/DevicePolicyManagerService:mIPackageManager	Landroid/content/pm/IPackageManager;
    //   35: aload_2
    //   36: iload_3
    //   37: iload 4
    //   39: invokeinterface 3804 4 0
    //   44: pop
    //   45: aload_0
    //   46: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   49: lload 5
    //   51: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   54: aload_0
    //   55: monitorexit
    //   56: return
    //   57: astore_1
    //   58: ldc 125
    //   60: ldc_w 3806
    //   63: aload_1
    //   64: invokestatic 620	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   67: pop
    //   68: aload_0
    //   69: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   72: lload 5
    //   74: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   77: goto -23 -> 54
    //   80: astore_1
    //   81: aload_0
    //   82: monitorexit
    //   83: aload_1
    //   84: athrow
    //   85: astore_1
    //   86: aload_0
    //   87: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   90: lload 5
    //   92: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   95: aload_1
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	DevicePolicyManagerService
    //   0	97	1	paramComponentName	ComponentName
    //   0	97	2	paramString	String
    //   0	97	3	paramBoolean	boolean
    //   11	27	4	i	int
    //   29	62	5	l	long
    // Exception table:
    //   from	to	target	type
    //   31	45	57	android/os/RemoteException
    //   15	31	80	finally
    //   45	54	80	finally
    //   68	77	80	finally
    //   86	97	80	finally
    //   31	45	85	finally
    //   58	68	85	finally
  }
  
  /* Error */
  public void setUserIcon(ComponentName paramComponentName, android.graphics.Bitmap paramBitmap)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: ldc_w 2393
    //   6: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   9: pop
    //   10: aload_0
    //   11: aload_1
    //   12: iconst_m1
    //   13: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   16: pop
    //   17: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   20: istore_3
    //   21: aload_0
    //   22: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   25: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   28: lstore 4
    //   30: aload_0
    //   31: getfield 468	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManagerInternal	Landroid/os/UserManagerInternal;
    //   34: iload_3
    //   35: aload_2
    //   36: invokevirtual 3811	android/os/UserManagerInternal:setUserIcon	(ILandroid/graphics/Bitmap;)V
    //   39: aload_0
    //   40: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   43: lload 4
    //   45: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   48: aload_0
    //   49: monitorexit
    //   50: return
    //   51: astore_1
    //   52: aload_0
    //   53: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   56: lload 4
    //   58: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   61: aload_1
    //   62: athrow
    //   63: astore_1
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_1
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	DevicePolicyManagerService
    //   0	68	1	paramComponentName	ComponentName
    //   0	68	2	paramBitmap	android.graphics.Bitmap
    //   20	15	3	i	int
    //   28	29	4	l	long
    // Exception table:
    //   from	to	target	type
    //   30	39	51	finally
    //   2	30	63	finally
    //   39	48	63	finally
    //   52	63	63	finally
  }
  
  public void setUserProvisioningState(int paramInt1, int paramInt2)
  {
    if (!this.mHasFeature) {
      return;
    }
    if ((paramInt2 == this.mOwners.getDeviceOwnerUserId()) || (this.mOwners.hasProfileOwner(paramInt2))) {}
    while (getManagedUserId(paramInt2) != -1)
    {
      i = 1;
      try
      {
        int j = this.mInjector.binderGetCallingUid();
        if ((j != 2000) && (j != 0)) {
          break label144;
        }
        if ((getUserProvisioningState(paramInt2) == 0) && (paramInt1 == 3)) {
          break;
        }
        throw new IllegalStateException("Not allowed to change provisioning state unless current provisioning state is unmanaged, and new state is finalized.");
      }
      finally {}
    }
    throw new IllegalStateException("Not allowed to change provisioning state unless a device or profile owner is set.");
    int i = 0;
    for (;;)
    {
      DevicePolicyData localDevicePolicyData = getUserData(paramInt2);
      if (i != 0) {
        checkUserProvisioningStateTransition(localDevicePolicyData.mUserProvisioningState, paramInt1);
      }
      localDevicePolicyData.mUserProvisioningState = paramInt1;
      saveSettingsLocked(paramInt2);
      return;
      label144:
      enforceCanManageProfileAndDeviceOwners();
    }
  }
  
  public void setUserRestriction(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    Preconditions.checkNotNull(paramComponentName, "ComponentName is null");
    if (!UserRestrictionsUtils.isValidRestriction(paramString)) {
      return;
    }
    int i = this.mInjector.userHandleGetCallingUserId();
    ActiveAdmin localActiveAdmin;
    try
    {
      localActiveAdmin = getActiveAdminForCallerLocked(paramComponentName, -1);
      if (isDeviceOwner(paramComponentName, i))
      {
        if (UserRestrictionsUtils.canDeviceOwnerChange(paramString)) {
          break label122;
        }
        throw new SecurityException("Device owner cannot set user restriction " + paramString);
      }
    }
    finally {}
    if (!UserRestrictionsUtils.canProfileOwnerChange(paramString, i)) {
      throw new SecurityException("Profile owner cannot set user restriction " + paramString);
    }
    label122:
    localActiveAdmin.ensureUserRestrictions().putBoolean(paramString, paramBoolean);
    saveSettingsLocked(i);
    pushUserRestrictions(i);
    sendChangedNotification(i);
  }
  
  /* Error */
  public void startManagedQuickContact(String paramString, long paramLong1, boolean paramBoolean, long paramLong2, Intent paramIntent)
  {
    // Byte code:
    //   0: aload_1
    //   1: lload_2
    //   2: iload 4
    //   4: lload 5
    //   6: aload 7
    //   8: invokestatic 3835	android/provider/ContactsContract$QuickContact:rebuildManagedQuickContactsIntent	(Ljava/lang/String;JZJLandroid/content/Intent;)Landroid/content/Intent;
    //   11: astore_1
    //   12: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   15: istore 8
    //   17: aload_0
    //   18: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   21: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   24: lstore_2
    //   25: aload_0
    //   26: monitorenter
    //   27: aload_0
    //   28: iload 8
    //   30: invokevirtual 3816	com/android/server/devicepolicy/DevicePolicyManagerService:getManagedUserId	(I)I
    //   33: istore 8
    //   35: iload 8
    //   37: ifge +14 -> 51
    //   40: aload_0
    //   41: monitorexit
    //   42: aload_0
    //   43: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   46: lload_2
    //   47: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   50: return
    //   51: aload_0
    //   52: iload 8
    //   54: invokespecial 3837	com/android/server/devicepolicy/DevicePolicyManagerService:isCrossProfileQuickContactDisabled	(I)Z
    //   57: istore 4
    //   59: iload 4
    //   61: ifeq +14 -> 75
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_0
    //   67: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   70: lload_2
    //   71: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   74: return
    //   75: aload_0
    //   76: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   79: aload_1
    //   80: new 536	android/os/UserHandle
    //   83: dup
    //   84: iload 8
    //   86: invokespecial 674	android/os/UserHandle:<init>	(I)V
    //   89: invokestatic 3843	android/provider/ContactsInternal:startQuickContactWithErrorToastForUser	(Landroid/content/Context;Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   92: aload_0
    //   93: monitorexit
    //   94: aload_0
    //   95: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   98: lload_2
    //   99: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   102: return
    //   103: astore_1
    //   104: aload_0
    //   105: monitorexit
    //   106: aload_1
    //   107: athrow
    //   108: astore_1
    //   109: aload_0
    //   110: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   113: lload_2
    //   114: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   117: aload_1
    //   118: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	119	0	this	DevicePolicyManagerService
    //   0	119	1	paramString	String
    //   0	119	2	paramLong1	long
    //   0	119	4	paramBoolean	boolean
    //   0	119	5	paramLong2	long
    //   0	119	7	paramIntent	Intent
    //   15	70	8	i	int
    // Exception table:
    //   from	to	target	type
    //   27	35	103	finally
    //   51	59	103	finally
    //   75	92	103	finally
    //   25	27	108	finally
    //   40	42	108	finally
    //   64	66	108	finally
    //   92	94	108	finally
    //   104	108	108	finally
  }
  
  /* Error */
  public boolean switchUser(ComponentName paramComponentName, UserHandle paramUserHandle)
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 2393
    //   4: invokestatic 2395	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   7: pop
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: aload_1
    //   12: bipush -2
    //   14: invokevirtual 859	com/android/server/devicepolicy/DevicePolicyManagerService:getActiveAdminForCallerLocked	(Landroid/content/ComponentName;I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$ActiveAdmin;
    //   17: pop
    //   18: aload_0
    //   19: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   22: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   25: lstore 4
    //   27: iconst_0
    //   28: istore_3
    //   29: aload_2
    //   30: ifnull +8 -> 38
    //   33: aload_2
    //   34: invokevirtual 1149	android/os/UserHandle:getIdentifier	()I
    //   37: istore_3
    //   38: aload_0
    //   39: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   42: invokevirtual 587	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getIActivityManager	()Landroid/app/IActivityManager;
    //   45: iload_3
    //   46: invokeinterface 3846 2 0
    //   51: istore 6
    //   53: aload_0
    //   54: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   57: lload 4
    //   59: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   62: aload_0
    //   63: monitorexit
    //   64: iload 6
    //   66: ireturn
    //   67: astore_1
    //   68: ldc 125
    //   70: ldc_w 3848
    //   73: aload_1
    //   74: invokestatic 3093	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   77: pop
    //   78: aload_0
    //   79: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   82: lload 4
    //   84: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   87: aload_0
    //   88: monitorexit
    //   89: iconst_0
    //   90: ireturn
    //   91: astore_1
    //   92: aload_0
    //   93: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   96: lload 4
    //   98: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   101: aload_1
    //   102: athrow
    //   103: astore_1
    //   104: aload_0
    //   105: monitorexit
    //   106: aload_1
    //   107: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	this	DevicePolicyManagerService
    //   0	108	1	paramComponentName	ComponentName
    //   0	108	2	paramUserHandle	UserHandle
    //   28	18	3	i	int
    //   25	72	4	l	long
    //   51	14	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   33	38	67	android/os/RemoteException
    //   38	53	67	android/os/RemoteException
    //   33	38	91	finally
    //   38	53	91	finally
    //   68	78	91	finally
    //   10	27	103	finally
    //   53	62	103	finally
    //   78	87	103	finally
    //   92	103	103	finally
  }
  
  void systemReady(int paramInt)
  {
    if (!this.mHasFeature) {
      return;
    }
    switch (paramInt)
    {
    default: 
      return;
    case 480: 
      onLockSettingsReady();
      return;
    }
    ensureDeviceOwnerUserStarted();
  }
  
  /* Error */
  public void uninstallCaCerts(ComponentName paramComponentName, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokevirtual 3063	com/android/server/devicepolicy/DevicePolicyManagerService:enforceCanManageCaCerts	(Landroid/content/ComponentName;)V
    //   5: new 536	android/os/UserHandle
    //   8: dup
    //   9: invokestatic 575	android/os/UserHandle:getCallingUserId	()I
    //   12: invokespecial 674	android/os/UserHandle:<init>	(I)V
    //   15: astore_1
    //   16: aload_0
    //   17: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   20: invokevirtual 579	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderClearCallingIdentity	()J
    //   23: lstore 4
    //   25: aload_0
    //   26: getfield 431	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
    //   29: aload_1
    //   30: invokestatic 3079	android/security/KeyChain:bindAsUser	(Landroid/content/Context;Landroid/os/UserHandle;)Landroid/security/KeyChain$KeyChainConnection;
    //   33: astore_1
    //   34: iconst_0
    //   35: istore_3
    //   36: iload_3
    //   37: aload_2
    //   38: arraylength
    //   39: if_icmpge +23 -> 62
    //   42: aload_1
    //   43: invokevirtual 3084	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
    //   46: aload_2
    //   47: iload_3
    //   48: aaload
    //   49: invokeinterface 3857 2 0
    //   54: pop
    //   55: iload_3
    //   56: iconst_1
    //   57: iadd
    //   58: istore_3
    //   59: goto -23 -> 36
    //   62: aload_1
    //   63: invokevirtual 3090	android/security/KeyChain$KeyChainConnection:close	()V
    //   66: aload_0
    //   67: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   70: lload 4
    //   72: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   75: return
    //   76: astore_2
    //   77: ldc 125
    //   79: ldc_w 3859
    //   82: aload_2
    //   83: invokestatic 3093	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   86: pop
    //   87: aload_1
    //   88: invokevirtual 3090	android/security/KeyChain$KeyChainConnection:close	()V
    //   91: goto -25 -> 66
    //   94: astore_1
    //   95: ldc 125
    //   97: ldc_w 3861
    //   100: aload_1
    //   101: invokestatic 1172	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   104: pop
    //   105: invokestatic 3103	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   108: invokevirtual 3106	java/lang/Thread:interrupt	()V
    //   111: aload_0
    //   112: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   115: lload 4
    //   117: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   120: return
    //   121: astore_2
    //   122: aload_1
    //   123: invokevirtual 3090	android/security/KeyChain$KeyChainConnection:close	()V
    //   126: aload_2
    //   127: athrow
    //   128: astore_1
    //   129: aload_0
    //   130: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   133: lload 4
    //   135: invokevirtual 615	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:binderRestoreCallingIdentity	(J)V
    //   138: aload_1
    //   139: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	140	0	this	DevicePolicyManagerService
    //   0	140	1	paramComponentName	ComponentName
    //   0	140	2	paramArrayOfString	String[]
    //   35	24	3	i	int
    //   23	111	4	l	long
    // Exception table:
    //   from	to	target	type
    //   36	55	76	android/os/RemoteException
    //   25	34	94	java/lang/InterruptedException
    //   62	66	94	java/lang/InterruptedException
    //   87	91	94	java/lang/InterruptedException
    //   122	128	94	java/lang/InterruptedException
    //   36	55	121	finally
    //   77	87	121	finally
    //   25	34	128	finally
    //   62	66	128	finally
    //   87	91	128	finally
    //   95	111	128	finally
    //   122	128	128	finally
  }
  
  public void uninstallPackageWithActiveAdmins(final String paramString)
  {
    enforceCanManageDeviceAdmin();
    if (TextUtils.isEmpty(paramString)) {}
    final int i;
    for (boolean bool = false;; bool = true)
    {
      Preconditions.checkArgument(bool);
      i = this.mInjector.userHandleGetCallingUserId();
      enforceUserUnlocked(i);
      localObject1 = getProfileOwner(i);
      if ((localObject1 == null) || (!paramString.equals(((ComponentName)localObject1).getPackageName()))) {
        break;
      }
      throw new IllegalArgumentException("Cannot uninstall a package with a profile owner");
    }
    final Object localObject1 = getDeviceOwnerComponent(false);
    if ((getDeviceOwnerUserId() == i) && (localObject1 != null) && (paramString.equals(((ComponentName)localObject1).getPackageName()))) {
      throw new IllegalArgumentException("Cannot uninstall a package with a device owner");
    }
    localObject1 = new Pair(paramString, Integer.valueOf(i));
    try
    {
      this.mPackagesToRemove.add(localObject1);
      Object localObject2 = getActiveAdmins(i);
      localObject1 = new ArrayList();
      if (localObject2 != null)
      {
        localObject2 = ((Iterable)localObject2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          ComponentName localComponentName = (ComponentName)((Iterator)localObject2).next();
          if (paramString.equals(localComponentName.getPackageName()))
          {
            ((List)localObject1).add(localComponentName);
            removeActiveAdmin(localComponentName, i);
          }
        }
      }
      startUninstallIntent(paramString, i);
    }
    finally {}
    return;
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        Iterator localIterator = localObject1.iterator();
        while (localIterator.hasNext())
        {
          ComponentName localComponentName = (ComponentName)localIterator.next();
          DevicePolicyManagerService.-wrap13(DevicePolicyManagerService.this, localComponentName, i);
        }
        DevicePolicyManagerService.-wrap20(DevicePolicyManagerService.this, paramString, i);
      }
    }, 10000L);
  }
  
  void updateMaximumTimeToLockLocked(int paramInt)
  {
    long l2 = Long.MAX_VALUE;
    Object localObject1 = this.mUserManager.getProfileIdsWithDisabled(paramInt);
    int i = 0;
    int k = localObject1.length;
    while (i < k)
    {
      DevicePolicyData localDevicePolicyData = getUserDataUnchecked(localObject1[i]);
      int m = localDevicePolicyData.mAdminList.size();
      int j = 0;
      while (j < m)
      {
        ActiveAdmin localActiveAdmin = (ActiveAdmin)localDevicePolicyData.mAdminList.get(j);
        l1 = l2;
        if (localActiveAdmin.maximumTimeToUnlock > 0L)
        {
          l1 = l2;
          if (l2 > localActiveAdmin.maximumTimeToUnlock) {
            l1 = localActiveAdmin.maximumTimeToUnlock;
          }
        }
        l2 = l1;
        if (localActiveAdmin.hasParentActiveAdmin())
        {
          localActiveAdmin = localActiveAdmin.getParentActiveAdmin();
          l2 = l1;
          if (localActiveAdmin.maximumTimeToUnlock > 0L)
          {
            l2 = l1;
            if (l1 > localActiveAdmin.maximumTimeToUnlock) {
              l2 = localActiveAdmin.maximumTimeToUnlock;
            }
          }
        }
        j += 1;
      }
      i += 1;
    }
    localObject1 = getUserDataUnchecked(getProfileParentId(paramInt));
    if (((DevicePolicyData)localObject1).mLastMaximumTimeToLock == l2) {
      return;
    }
    ((DevicePolicyData)localObject1).mLastMaximumTimeToLock = l2;
    long l1 = this.mInjector.binderClearCallingIdentity();
    try
    {
      if (((DevicePolicyData)localObject1).mLastMaximumTimeToLock != Long.MAX_VALUE) {
        this.mInjector.settingsGlobalPutInt("stay_on_while_plugged_in", 0);
      }
      this.mInjector.getPowerManagerInternal().setMaximumScreenOffTimeoutFromDeviceAdmin((int)Math.min(((DevicePolicyData)localObject1).mLastMaximumTimeToLock, 2147483647L));
      return;
    }
    finally
    {
      this.mInjector.binderRestoreCallingIdentity(l1);
    }
  }
  
  /* Error */
  void updateUserSetupComplete()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 460	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
    //   4: iconst_1
    //   5: invokevirtual 3897	android/os/UserManager:getUsers	(Z)Ljava/util/List;
    //   8: astore 4
    //   10: aload 4
    //   12: invokeinterface 1138 1 0
    //   17: istore_2
    //   18: iconst_0
    //   19: istore_1
    //   20: iload_1
    //   21: iload_2
    //   22: if_icmpge +77 -> 99
    //   25: aload 4
    //   27: iload_1
    //   28: invokeinterface 1139 2 0
    //   33: checkcast 595	android/content/pm/UserInfo
    //   36: getfield 603	android/content/pm/UserInfo:id	I
    //   39: istore_3
    //   40: aload_0
    //   41: getfield 418	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
    //   44: ldc_w 2543
    //   47: iconst_0
    //   48: iload_3
    //   49: invokevirtual 3901	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:settingsSecureGetIntForUser	(Ljava/lang/String;II)I
    //   52: ifeq +33 -> 85
    //   55: aload_0
    //   56: iload_3
    //   57: invokevirtual 795	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
    //   60: astore 5
    //   62: aload 5
    //   64: getfield 1365	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mUserSetupComplete	Z
    //   67: ifne +18 -> 85
    //   70: aload 5
    //   72: iconst_1
    //   73: putfield 1365	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mUserSetupComplete	Z
    //   76: aload_0
    //   77: monitorenter
    //   78: aload_0
    //   79: iload_3
    //   80: invokespecial 263	com/android/server/devicepolicy/DevicePolicyManagerService:saveSettingsLocked	(I)V
    //   83: aload_0
    //   84: monitorexit
    //   85: iload_1
    //   86: iconst_1
    //   87: iadd
    //   88: istore_1
    //   89: goto -69 -> 20
    //   92: astore 4
    //   94: aload_0
    //   95: monitorexit
    //   96: aload 4
    //   98: athrow
    //   99: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	this	DevicePolicyManagerService
    //   19	70	1	i	int
    //   17	6	2	j	int
    //   39	41	3	k	int
    //   8	18	4	localList	List
    //   92	5	4	localObject	Object
    //   60	11	5	localDevicePolicyData	DevicePolicyData
    // Exception table:
    //   from	to	target	type
    //   78	83	92	finally
  }
  
  void validatePasswordOwnerLocked(DevicePolicyData paramDevicePolicyData)
  {
    int k;
    int i;
    if (paramDevicePolicyData.mPasswordOwner >= 0)
    {
      k = 0;
      i = paramDevicePolicyData.mAdminList.size() - 1;
    }
    for (;;)
    {
      int j = k;
      if (i >= 0)
      {
        if (((ActiveAdmin)paramDevicePolicyData.mAdminList.get(i)).getUid() == paramDevicePolicyData.mPasswordOwner) {
          j = 1;
        }
      }
      else
      {
        if (j == 0)
        {
          Slog.w("DevicePolicyManagerService", "Previous password owner " + paramDevicePolicyData.mPasswordOwner + " no longer active; disabling");
          paramDevicePolicyData.mPasswordOwner = -1;
        }
        return;
      }
      i -= 1;
    }
  }
  
  public void wipeData(int paramInt)
  {
    if (!this.mHasFeature) {
      return;
    }
    int i = this.mInjector.userHandleGetCallingUserId();
    enforceFullCrossUsersPermission(i);
    Object localObject2;
    long l;
    try
    {
      localObject2 = getActiveAdminForCallerLocked(null, 4);
      String str1 = ((ActiveAdmin)localObject2).info.getComponent().flattenToShortString();
      l = this.mInjector.binderClearCallingIdentity();
      if ((paramInt & 0x2) == 0) {
        break label130;
      }
      try
      {
        if (!isDeviceOwner(((ActiveAdmin)localObject2).info.getComponent(), i)) {
          throw new SecurityException("Only device owner admins can set WIPE_RESET_PROTECTION_DATA");
        }
      }
      finally
      {
        this.mInjector.binderRestoreCallingIdentity(l);
      }
      localObject2 = (PersistentDataBlockManager)this.mContext.getSystemService("persistent_data_block");
    }
    finally {}
    if (localObject2 != null) {
      ((PersistentDataBlockManager)localObject2).wipe();
    }
    label130:
    this.mInjector.binderRestoreCallingIdentity(l);
    if ((paramInt & 0x1) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      wipeDeviceNoLock(bool, i, "DevicePolicyManager.wipeData() from " + str2);
      return;
    }
  }
  
  static class ActiveAdmin
  {
    private static final String ATTR_VALUE = "value";
    static final int DEF_KEYGUARD_FEATURES_DISABLED = 0;
    static final int DEF_MAXIMUM_FAILED_PASSWORDS_FOR_WIPE = 0;
    static final long DEF_MAXIMUM_TIME_TO_UNLOCK = 0L;
    static final int DEF_MINIMUM_PASSWORD_LENGTH = 0;
    static final int DEF_MINIMUM_PASSWORD_LETTERS = 1;
    static final int DEF_MINIMUM_PASSWORD_LOWER_CASE = 0;
    static final int DEF_MINIMUM_PASSWORD_NON_LETTER = 0;
    static final int DEF_MINIMUM_PASSWORD_NUMERIC = 1;
    static final int DEF_MINIMUM_PASSWORD_SYMBOLS = 1;
    static final int DEF_MINIMUM_PASSWORD_UPPER_CASE = 0;
    static final int DEF_ORGANIZATION_COLOR = Color.parseColor("#00796B");
    static final long DEF_PASSWORD_EXPIRATION_DATE = 0L;
    static final long DEF_PASSWORD_EXPIRATION_TIMEOUT = 0L;
    static final int DEF_PASSWORD_HISTORY_LENGTH = 0;
    private static final String TAG_ACCOUNT_TYPE = "account-type";
    private static final String TAG_CROSS_PROFILE_WIDGET_PROVIDERS = "cross-profile-widget-providers";
    private static final String TAG_DISABLE_ACCOUNT_MANAGEMENT = "disable-account-management";
    private static final String TAG_DISABLE_BLUETOOTH_CONTACT_SHARING = "disable-bt-contacts-sharing";
    private static final String TAG_DISABLE_CALLER_ID = "disable-caller-id";
    private static final String TAG_DISABLE_CAMERA = "disable-camera";
    private static final String TAG_DISABLE_CONTACTS_SEARCH = "disable-contacts-search";
    private static final String TAG_DISABLE_KEYGUARD_FEATURES = "disable-keyguard-features";
    private static final String TAG_DISABLE_SCREEN_CAPTURE = "disable-screen-capture";
    private static final String TAG_ENCRYPTION_REQUESTED = "encryption-requested";
    private static final String TAG_FORCE_EPHEMERAL_USERS = "force_ephemeral_users";
    private static final String TAG_GLOBAL_PROXY_EXCLUSION_LIST = "global-proxy-exclusion-list";
    private static final String TAG_GLOBAL_PROXY_SPEC = "global-proxy-spec";
    private static final String TAG_KEEP_UNINSTALLED_PACKAGES = "keep-uninstalled-packages";
    private static final String TAG_LONG_SUPPORT_MESSAGE = "long-support-message";
    private static final String TAG_MANAGE_TRUST_AGENT_FEATURES = "manage-trust-agent-features";
    private static final String TAG_MAX_FAILED_PASSWORD_WIPE = "max-failed-password-wipe";
    private static final String TAG_MAX_TIME_TO_UNLOCK = "max-time-to-unlock";
    private static final String TAG_MIN_PASSWORD_LENGTH = "min-password-length";
    private static final String TAG_MIN_PASSWORD_LETTERS = "min-password-letters";
    private static final String TAG_MIN_PASSWORD_LOWERCASE = "min-password-lowercase";
    private static final String TAG_MIN_PASSWORD_NONLETTER = "min-password-nonletter";
    private static final String TAG_MIN_PASSWORD_NUMERIC = "min-password-numeric";
    private static final String TAG_MIN_PASSWORD_SYMBOLS = "min-password-symbols";
    private static final String TAG_MIN_PASSWORD_UPPERCASE = "min-password-uppercase";
    private static final String TAG_ORGANIZATION_COLOR = "organization-color";
    private static final String TAG_ORGANIZATION_NAME = "organization-name";
    private static final String TAG_PACKAGE_LIST_ITEM = "item";
    private static final String TAG_PARENT_ADMIN = "parent-admin";
    private static final String TAG_PASSWORD_EXPIRATION_DATE = "password-expiration-date";
    private static final String TAG_PASSWORD_EXPIRATION_TIMEOUT = "password-expiration-timeout";
    private static final String TAG_PASSWORD_HISTORY_LENGTH = "password-history-length";
    private static final String TAG_PASSWORD_QUALITY = "password-quality";
    private static final String TAG_PERMITTED_ACCESSIBILITY_SERVICES = "permitted-accessiblity-services";
    private static final String TAG_PERMITTED_IMES = "permitted-imes";
    private static final String TAG_POLICIES = "policies";
    private static final String TAG_PROVIDER = "provider";
    private static final String TAG_REQUIRE_AUTO_TIME = "require_auto_time";
    private static final String TAG_SHORT_SUPPORT_MESSAGE = "short-support-message";
    private static final String TAG_SPECIFIES_GLOBAL_PROXY = "specifies-global-proxy";
    private static final String TAG_STRONG_AUTH_UNLOCK_TIMEOUT = "strong-auth-unlock-timeout";
    private static final String TAG_TEST_ONLY_ADMIN = "test-only-admin";
    private static final String TAG_TRUST_AGENT_COMPONENT = "component";
    private static final String TAG_TRUST_AGENT_COMPONENT_OPTIONS = "trust-agent-component-options";
    private static final String TAG_USER_RESTRICTIONS = "user-restrictions";
    Set<String> accountTypesWithManagementDisabled = new ArraySet();
    List<String> crossProfileWidgetProviders;
    boolean disableBluetoothContactSharing = true;
    boolean disableCallerId = false;
    boolean disableCamera = false;
    boolean disableContactsSearch = false;
    boolean disableScreenCapture = false;
    int disabledKeyguardFeatures = 0;
    boolean encryptionRequested = false;
    boolean forceEphemeralUsers = false;
    String globalProxyExclusionList = null;
    String globalProxySpec = null;
    final DeviceAdminInfo info;
    final boolean isParent;
    List<String> keepUninstalledPackages;
    CharSequence longSupportMessage = null;
    int maximumFailedPasswordsForWipe = 0;
    long maximumTimeToUnlock = 0L;
    int minimumPasswordLength = 0;
    int minimumPasswordLetters = 1;
    int minimumPasswordLowerCase = 0;
    int minimumPasswordNonLetter = 0;
    int minimumPasswordNumeric = 1;
    int minimumPasswordSymbols = 1;
    int minimumPasswordUpperCase = 0;
    int organizationColor = DEF_ORGANIZATION_COLOR;
    String organizationName = null;
    ActiveAdmin parentAdmin;
    long passwordExpirationDate = 0L;
    long passwordExpirationTimeout = 0L;
    int passwordHistoryLength = 0;
    int passwordQuality = 0;
    List<String> permittedAccessiblityServices;
    List<String> permittedInputMethods;
    boolean requireAutoTime = false;
    CharSequence shortSupportMessage = null;
    boolean specifiesGlobalProxy = false;
    long strongAuthUnlockTimeout = 0L;
    boolean testOnlyAdmin = false;
    ArrayMap<String, TrustAgentInfo> trustAgentInfos = new ArrayMap();
    Bundle userRestrictions;
    
    ActiveAdmin(DeviceAdminInfo paramDeviceAdminInfo, boolean paramBoolean)
    {
      this.info = paramDeviceAdminInfo;
      this.isParent = paramBoolean;
    }
    
    private ArrayMap<String, TrustAgentInfo> getAllTrustAgentInfos(XmlPullParser paramXmlPullParser, String paramString)
      throws XmlPullParserException, IOException
    {
      int i = paramXmlPullParser.getDepth();
      ArrayMap localArrayMap = new ArrayMap();
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
        if ((j != 3) && (j != 4))
        {
          String str = paramXmlPullParser.getName();
          if ("component".equals(str)) {
            localArrayMap.put(paramXmlPullParser.getAttributeValue(null, "value"), getTrustAgentInfo(paramXmlPullParser, paramString));
          } else {
            Slog.w("DevicePolicyManagerService", "Unknown tag under " + paramString + ": " + str);
          }
        }
      }
      return localArrayMap;
    }
    
    private List<String> getCrossProfileWidgetProviders(XmlPullParser paramXmlPullParser, String paramString)
      throws XmlPullParserException, IOException
    {
      int i = paramXmlPullParser.getDepth();
      Object localObject1 = null;
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
        if ((j != 3) && (j != 4))
        {
          Object localObject2 = paramXmlPullParser.getName();
          if ("provider".equals(localObject2))
          {
            String str = paramXmlPullParser.getAttributeValue(null, "value");
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new ArrayList();
            }
            ((ArrayList)localObject2).add(str);
            localObject1 = localObject2;
          }
          else
          {
            Slog.w("DevicePolicyManagerService", "Unknown tag under " + paramString + ": " + (String)localObject2);
          }
        }
      }
      return (List<String>)localObject1;
    }
    
    private TrustAgentInfo getTrustAgentInfo(XmlPullParser paramXmlPullParser, String paramString)
      throws XmlPullParserException, IOException
    {
      int i = paramXmlPullParser.getDepth();
      TrustAgentInfo localTrustAgentInfo = new TrustAgentInfo(null);
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
        if ((j != 3) && (j != 4))
        {
          String str = paramXmlPullParser.getName();
          if ("trust-agent-component-options".equals(str)) {
            localTrustAgentInfo.options = PersistableBundle.restoreFromXml(paramXmlPullParser);
          } else {
            Slog.w("DevicePolicyManagerService", "Unknown tag under " + paramString + ": " + str);
          }
        }
      }
      return localTrustAgentInfo;
    }
    
    private Set<String> readDisableAccountInfo(XmlPullParser paramXmlPullParser, String paramString)
      throws XmlPullParserException, IOException
    {
      int i = paramXmlPullParser.getDepth();
      ArraySet localArraySet = new ArraySet();
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
        if ((j != 3) && (j != 4))
        {
          String str = paramXmlPullParser.getName();
          if ("account-type".equals(str)) {
            localArraySet.add(paramXmlPullParser.getAttributeValue(null, "value"));
          } else {
            Slog.w("DevicePolicyManagerService", "Unknown tag under " + paramString + ": " + str);
          }
        }
      }
      return localArraySet;
    }
    
    private List<String> readPackageList(XmlPullParser paramXmlPullParser, String paramString)
      throws XmlPullParserException, IOException
    {
      ArrayList localArrayList = new ArrayList();
      int i = paramXmlPullParser.getDepth();
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
        if ((j != 3) && (j != 4))
        {
          String str1 = paramXmlPullParser.getName();
          if ("item".equals(str1))
          {
            String str2 = paramXmlPullParser.getAttributeValue(null, "value");
            if (str2 != null) {
              localArrayList.add(str2);
            } else {
              Slog.w("DevicePolicyManagerService", "Package name missing under " + str1);
            }
          }
          else
          {
            Slog.w("DevicePolicyManagerService", "Unknown tag under " + paramString + ": " + str1);
          }
        }
      }
      return localArrayList;
    }
    
    void dump(String paramString, PrintWriter paramPrintWriter)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("uid=");
      paramPrintWriter.println(getUid());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("testOnlyAdmin=");
      paramPrintWriter.println(this.testOnlyAdmin);
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("policies:");
      ArrayList localArrayList = this.info.getUsedPolicies();
      if (localArrayList != null)
      {
        int i = 0;
        while (i < localArrayList.size())
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  ");
          paramPrintWriter.println(((DeviceAdminInfo.PolicyInfo)localArrayList.get(i)).tag);
          i += 1;
        }
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("passwordQuality=0x");
      paramPrintWriter.println(Integer.toHexString(this.passwordQuality));
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("minimumPasswordLength=");
      paramPrintWriter.println(this.minimumPasswordLength);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("passwordHistoryLength=");
      paramPrintWriter.println(this.passwordHistoryLength);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("minimumPasswordUpperCase=");
      paramPrintWriter.println(this.minimumPasswordUpperCase);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("minimumPasswordLowerCase=");
      paramPrintWriter.println(this.minimumPasswordLowerCase);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("minimumPasswordLetters=");
      paramPrintWriter.println(this.minimumPasswordLetters);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("minimumPasswordNumeric=");
      paramPrintWriter.println(this.minimumPasswordNumeric);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("minimumPasswordSymbols=");
      paramPrintWriter.println(this.minimumPasswordSymbols);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("minimumPasswordNonLetter=");
      paramPrintWriter.println(this.minimumPasswordNonLetter);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("maximumTimeToUnlock=");
      paramPrintWriter.println(this.maximumTimeToUnlock);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("strongAuthUnlockTimeout=");
      paramPrintWriter.println(this.strongAuthUnlockTimeout);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("maximumFailedPasswordsForWipe=");
      paramPrintWriter.println(this.maximumFailedPasswordsForWipe);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("specifiesGlobalProxy=");
      paramPrintWriter.println(this.specifiesGlobalProxy);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("passwordExpirationTimeout=");
      paramPrintWriter.println(this.passwordExpirationTimeout);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("passwordExpirationDate=");
      paramPrintWriter.println(this.passwordExpirationDate);
      if (this.globalProxySpec != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("globalProxySpec=");
        paramPrintWriter.println(this.globalProxySpec);
      }
      if (this.globalProxyExclusionList != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("globalProxyEclusionList=");
        paramPrintWriter.println(this.globalProxyExclusionList);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("encryptionRequested=");
      paramPrintWriter.println(this.encryptionRequested);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("disableCamera=");
      paramPrintWriter.println(this.disableCamera);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("disableCallerId=");
      paramPrintWriter.println(this.disableCallerId);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("disableContactsSearch=");
      paramPrintWriter.println(this.disableContactsSearch);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("disableBluetoothContactSharing=");
      paramPrintWriter.println(this.disableBluetoothContactSharing);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("disableScreenCapture=");
      paramPrintWriter.println(this.disableScreenCapture);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("requireAutoTime=");
      paramPrintWriter.println(this.requireAutoTime);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("forceEphemeralUsers=");
      paramPrintWriter.println(this.forceEphemeralUsers);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("disabledKeyguardFeatures=");
      paramPrintWriter.println(this.disabledKeyguardFeatures);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("crossProfileWidgetProviders=");
      paramPrintWriter.println(this.crossProfileWidgetProviders);
      if (this.permittedAccessiblityServices != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("permittedAccessibilityServices=");
        paramPrintWriter.println(this.permittedAccessiblityServices);
      }
      if (this.permittedInputMethods != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("permittedInputMethods=");
        paramPrintWriter.println(this.permittedInputMethods);
      }
      if (this.keepUninstalledPackages != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("keepUninstalledPackages=");
        paramPrintWriter.println(this.keepUninstalledPackages);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("organizationColor=");
      paramPrintWriter.println(this.organizationColor);
      if (this.organizationName != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("organizationName=");
        paramPrintWriter.println(this.organizationName);
      }
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("userRestrictions:");
      UserRestrictionsUtils.dumpRestrictions(paramPrintWriter, paramString + "  ", this.userRestrictions);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("isParent=");
      paramPrintWriter.println(this.isParent);
      if (this.parentAdmin != null)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.println("parentAdmin:");
        this.parentAdmin.dump(paramString + "  ", paramPrintWriter);
      }
    }
    
    Bundle ensureUserRestrictions()
    {
      if (this.userRestrictions == null) {
        this.userRestrictions = new Bundle();
      }
      return this.userRestrictions;
    }
    
    ActiveAdmin getParentActiveAdmin()
    {
      if (this.isParent) {}
      for (boolean bool = false;; bool = true)
      {
        Preconditions.checkState(bool);
        if (this.parentAdmin == null) {
          this.parentAdmin = new ActiveAdmin(this.info, true);
        }
        return this.parentAdmin;
      }
    }
    
    int getUid()
    {
      return this.info.getActivityInfo().applicationInfo.uid;
    }
    
    public UserHandle getUserHandle()
    {
      return UserHandle.of(UserHandle.getUserId(this.info.getActivityInfo().applicationInfo.uid));
    }
    
    boolean hasParentActiveAdmin()
    {
      return this.parentAdmin != null;
    }
    
    boolean hasUserRestrictions()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.userRestrictions != null)
      {
        bool1 = bool2;
        if (this.userRestrictions.size() > 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    void readFromXml(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException
    {
      int i = paramXmlPullParser.getDepth();
      for (;;)
      {
        int j = paramXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
          break;
        }
        if ((j != 3) && (j != 4))
        {
          String str = paramXmlPullParser.getName();
          if ("policies".equals(str))
          {
            this.info.readPoliciesFromXml(paramXmlPullParser);
          }
          else if ("password-quality".equals(str))
          {
            this.passwordQuality = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("min-password-length".equals(str))
          {
            this.minimumPasswordLength = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("password-history-length".equals(str))
          {
            this.passwordHistoryLength = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("min-password-uppercase".equals(str))
          {
            this.minimumPasswordUpperCase = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("min-password-lowercase".equals(str))
          {
            this.minimumPasswordLowerCase = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("min-password-letters".equals(str))
          {
            this.minimumPasswordLetters = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("min-password-numeric".equals(str))
          {
            this.minimumPasswordNumeric = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("min-password-symbols".equals(str))
          {
            this.minimumPasswordSymbols = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("min-password-nonletter".equals(str))
          {
            this.minimumPasswordNonLetter = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("max-time-to-unlock".equals(str))
          {
            this.maximumTimeToUnlock = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("strong-auth-unlock-timeout".equals(str))
          {
            this.strongAuthUnlockTimeout = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("max-failed-password-wipe".equals(str))
          {
            this.maximumFailedPasswordsForWipe = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("specifies-global-proxy".equals(str))
          {
            this.specifiesGlobalProxy = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("global-proxy-spec".equals(str))
          {
            this.globalProxySpec = paramXmlPullParser.getAttributeValue(null, "value");
          }
          else if ("global-proxy-exclusion-list".equals(str))
          {
            this.globalProxyExclusionList = paramXmlPullParser.getAttributeValue(null, "value");
          }
          else if ("password-expiration-timeout".equals(str))
          {
            this.passwordExpirationTimeout = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("password-expiration-date".equals(str))
          {
            this.passwordExpirationDate = Long.parseLong(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("encryption-requested".equals(str))
          {
            this.encryptionRequested = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("test-only-admin".equals(str))
          {
            this.testOnlyAdmin = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("disable-camera".equals(str))
          {
            this.disableCamera = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("disable-caller-id".equals(str))
          {
            this.disableCallerId = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("disable-contacts-search".equals(str))
          {
            this.disableContactsSearch = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("disable-bt-contacts-sharing".equals(str))
          {
            this.disableBluetoothContactSharing = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("disable-screen-capture".equals(str))
          {
            this.disableScreenCapture = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("require_auto_time".equals(str))
          {
            this.requireAutoTime = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("force_ephemeral_users".equals(str))
          {
            this.forceEphemeralUsers = Boolean.parseBoolean(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("disable-keyguard-features".equals(str))
          {
            this.disabledKeyguardFeatures = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
          }
          else if ("disable-account-management".equals(str))
          {
            this.accountTypesWithManagementDisabled = readDisableAccountInfo(paramXmlPullParser, str);
          }
          else if ("manage-trust-agent-features".equals(str))
          {
            this.trustAgentInfos = getAllTrustAgentInfos(paramXmlPullParser, str);
          }
          else if ("cross-profile-widget-providers".equals(str))
          {
            this.crossProfileWidgetProviders = getCrossProfileWidgetProviders(paramXmlPullParser, str);
          }
          else if ("permitted-accessiblity-services".equals(str))
          {
            this.permittedAccessiblityServices = readPackageList(paramXmlPullParser, str);
          }
          else if ("permitted-imes".equals(str))
          {
            this.permittedInputMethods = readPackageList(paramXmlPullParser, str);
          }
          else if ("keep-uninstalled-packages".equals(str))
          {
            this.keepUninstalledPackages = readPackageList(paramXmlPullParser, str);
          }
          else if ("user-restrictions".equals(str))
          {
            UserRestrictionsUtils.readRestrictions(paramXmlPullParser, ensureUserRestrictions());
          }
          else if ("short-support-message".equals(str))
          {
            if (paramXmlPullParser.next() == 4) {
              this.shortSupportMessage = paramXmlPullParser.getText();
            } else {
              Log.w("DevicePolicyManagerService", "Missing text when loading short support message");
            }
          }
          else if ("long-support-message".equals(str))
          {
            if (paramXmlPullParser.next() == 4) {
              this.longSupportMessage = paramXmlPullParser.getText();
            } else {
              Log.w("DevicePolicyManagerService", "Missing text when loading long support message");
            }
          }
          else
          {
            if ("parent-admin".equals(str))
            {
              if (this.isParent) {}
              for (boolean bool = false;; bool = true)
              {
                Preconditions.checkState(bool);
                this.parentAdmin = new ActiveAdmin(this.info, true);
                this.parentAdmin.readFromXml(paramXmlPullParser);
                break;
              }
            }
            if ("organization-color".equals(str))
            {
              this.organizationColor = Integer.parseInt(paramXmlPullParser.getAttributeValue(null, "value"));
            }
            else if ("organization-name".equals(str))
            {
              if (paramXmlPullParser.next() == 4) {
                this.organizationName = paramXmlPullParser.getText();
              } else {
                Log.w("DevicePolicyManagerService", "Missing text when loading organization name");
              }
            }
            else
            {
              Slog.w("DevicePolicyManagerService", "Unknown admin tag: " + str);
              XmlUtils.skipCurrentTag(paramXmlPullParser);
            }
          }
        }
      }
    }
    
    void writePackageListToXml(XmlSerializer paramXmlSerializer, String paramString, List<String> paramList)
      throws IllegalArgumentException, IllegalStateException, IOException
    {
      if (paramList == null) {
        return;
      }
      paramXmlSerializer.startTag(null, paramString);
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        String str = (String)paramList.next();
        paramXmlSerializer.startTag(null, "item");
        paramXmlSerializer.attribute(null, "value", str);
        paramXmlSerializer.endTag(null, "item");
      }
      paramXmlSerializer.endTag(null, paramString);
    }
    
    void writeToXml(XmlSerializer paramXmlSerializer)
      throws IllegalArgumentException, IllegalStateException, IOException
    {
      paramXmlSerializer.startTag(null, "policies");
      this.info.writePoliciesToXml(paramXmlSerializer);
      paramXmlSerializer.endTag(null, "policies");
      if (this.passwordQuality != 0)
      {
        paramXmlSerializer.startTag(null, "password-quality");
        paramXmlSerializer.attribute(null, "value", Integer.toString(this.passwordQuality));
        paramXmlSerializer.endTag(null, "password-quality");
        if (this.minimumPasswordLength != 0)
        {
          paramXmlSerializer.startTag(null, "min-password-length");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.minimumPasswordLength));
          paramXmlSerializer.endTag(null, "min-password-length");
        }
        if (this.passwordHistoryLength != 0)
        {
          paramXmlSerializer.startTag(null, "password-history-length");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.passwordHistoryLength));
          paramXmlSerializer.endTag(null, "password-history-length");
        }
        if (this.minimumPasswordUpperCase != 0)
        {
          paramXmlSerializer.startTag(null, "min-password-uppercase");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.minimumPasswordUpperCase));
          paramXmlSerializer.endTag(null, "min-password-uppercase");
        }
        if (this.minimumPasswordLowerCase != 0)
        {
          paramXmlSerializer.startTag(null, "min-password-lowercase");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.minimumPasswordLowerCase));
          paramXmlSerializer.endTag(null, "min-password-lowercase");
        }
        if (this.minimumPasswordLetters != 1)
        {
          paramXmlSerializer.startTag(null, "min-password-letters");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.minimumPasswordLetters));
          paramXmlSerializer.endTag(null, "min-password-letters");
        }
        if (this.minimumPasswordNumeric != 1)
        {
          paramXmlSerializer.startTag(null, "min-password-numeric");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.minimumPasswordNumeric));
          paramXmlSerializer.endTag(null, "min-password-numeric");
        }
        if (this.minimumPasswordSymbols != 1)
        {
          paramXmlSerializer.startTag(null, "min-password-symbols");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.minimumPasswordSymbols));
          paramXmlSerializer.endTag(null, "min-password-symbols");
        }
        if (this.minimumPasswordNonLetter > 0)
        {
          paramXmlSerializer.startTag(null, "min-password-nonletter");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.minimumPasswordNonLetter));
          paramXmlSerializer.endTag(null, "min-password-nonletter");
        }
      }
      if (this.maximumTimeToUnlock != 0L)
      {
        paramXmlSerializer.startTag(null, "max-time-to-unlock");
        paramXmlSerializer.attribute(null, "value", Long.toString(this.maximumTimeToUnlock));
        paramXmlSerializer.endTag(null, "max-time-to-unlock");
      }
      if (this.strongAuthUnlockTimeout != 259200000L)
      {
        paramXmlSerializer.startTag(null, "strong-auth-unlock-timeout");
        paramXmlSerializer.attribute(null, "value", Long.toString(this.strongAuthUnlockTimeout));
        paramXmlSerializer.endTag(null, "strong-auth-unlock-timeout");
      }
      if (this.maximumFailedPasswordsForWipe != 0)
      {
        paramXmlSerializer.startTag(null, "max-failed-password-wipe");
        paramXmlSerializer.attribute(null, "value", Integer.toString(this.maximumFailedPasswordsForWipe));
        paramXmlSerializer.endTag(null, "max-failed-password-wipe");
      }
      if (this.specifiesGlobalProxy)
      {
        paramXmlSerializer.startTag(null, "specifies-global-proxy");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.specifiesGlobalProxy));
        paramXmlSerializer.endTag(null, "specifies-global-proxy");
        if (this.globalProxySpec != null)
        {
          paramXmlSerializer.startTag(null, "global-proxy-spec");
          paramXmlSerializer.attribute(null, "value", this.globalProxySpec);
          paramXmlSerializer.endTag(null, "global-proxy-spec");
        }
        if (this.globalProxyExclusionList != null)
        {
          paramXmlSerializer.startTag(null, "global-proxy-exclusion-list");
          paramXmlSerializer.attribute(null, "value", this.globalProxyExclusionList);
          paramXmlSerializer.endTag(null, "global-proxy-exclusion-list");
        }
      }
      if (this.passwordExpirationTimeout != 0L)
      {
        paramXmlSerializer.startTag(null, "password-expiration-timeout");
        paramXmlSerializer.attribute(null, "value", Long.toString(this.passwordExpirationTimeout));
        paramXmlSerializer.endTag(null, "password-expiration-timeout");
      }
      if (this.passwordExpirationDate != 0L)
      {
        paramXmlSerializer.startTag(null, "password-expiration-date");
        paramXmlSerializer.attribute(null, "value", Long.toString(this.passwordExpirationDate));
        paramXmlSerializer.endTag(null, "password-expiration-date");
      }
      if (this.encryptionRequested)
      {
        paramXmlSerializer.startTag(null, "encryption-requested");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.encryptionRequested));
        paramXmlSerializer.endTag(null, "encryption-requested");
      }
      if (this.testOnlyAdmin)
      {
        paramXmlSerializer.startTag(null, "test-only-admin");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.testOnlyAdmin));
        paramXmlSerializer.endTag(null, "test-only-admin");
      }
      if (this.disableCamera)
      {
        paramXmlSerializer.startTag(null, "disable-camera");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.disableCamera));
        paramXmlSerializer.endTag(null, "disable-camera");
      }
      if (this.disableCallerId)
      {
        paramXmlSerializer.startTag(null, "disable-caller-id");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.disableCallerId));
        paramXmlSerializer.endTag(null, "disable-caller-id");
      }
      if (this.disableContactsSearch)
      {
        paramXmlSerializer.startTag(null, "disable-contacts-search");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.disableContactsSearch));
        paramXmlSerializer.endTag(null, "disable-contacts-search");
      }
      if (!this.disableBluetoothContactSharing)
      {
        paramXmlSerializer.startTag(null, "disable-bt-contacts-sharing");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.disableBluetoothContactSharing));
        paramXmlSerializer.endTag(null, "disable-bt-contacts-sharing");
      }
      if (this.disableScreenCapture)
      {
        paramXmlSerializer.startTag(null, "disable-screen-capture");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.disableScreenCapture));
        paramXmlSerializer.endTag(null, "disable-screen-capture");
      }
      if (this.requireAutoTime)
      {
        paramXmlSerializer.startTag(null, "require_auto_time");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.requireAutoTime));
        paramXmlSerializer.endTag(null, "require_auto_time");
      }
      if (this.forceEphemeralUsers)
      {
        paramXmlSerializer.startTag(null, "force_ephemeral_users");
        paramXmlSerializer.attribute(null, "value", Boolean.toString(this.forceEphemeralUsers));
        paramXmlSerializer.endTag(null, "force_ephemeral_users");
      }
      if (this.disabledKeyguardFeatures != 0)
      {
        paramXmlSerializer.startTag(null, "disable-keyguard-features");
        paramXmlSerializer.attribute(null, "value", Integer.toString(this.disabledKeyguardFeatures));
        paramXmlSerializer.endTag(null, "disable-keyguard-features");
      }
      Object localObject1;
      Object localObject2;
      if (!this.accountTypesWithManagementDisabled.isEmpty())
      {
        paramXmlSerializer.startTag(null, "disable-account-management");
        localObject1 = this.accountTypesWithManagementDisabled.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (String)((Iterator)localObject1).next();
          paramXmlSerializer.startTag(null, "account-type");
          paramXmlSerializer.attribute(null, "value", (String)localObject2);
          paramXmlSerializer.endTag(null, "account-type");
        }
        paramXmlSerializer.endTag(null, "disable-account-management");
      }
      if (!this.trustAgentInfos.isEmpty())
      {
        localObject1 = this.trustAgentInfos.entrySet();
        paramXmlSerializer.startTag(null, "manage-trust-agent-features");
        localObject1 = ((Iterable)localObject1).iterator();
        for (;;)
        {
          if (((Iterator)localObject1).hasNext())
          {
            localObject2 = (Map.Entry)((Iterator)localObject1).next();
            TrustAgentInfo localTrustAgentInfo = (TrustAgentInfo)((Map.Entry)localObject2).getValue();
            paramXmlSerializer.startTag(null, "component");
            paramXmlSerializer.attribute(null, "value", (String)((Map.Entry)localObject2).getKey());
            if (localTrustAgentInfo.options != null) {
              paramXmlSerializer.startTag(null, "trust-agent-component-options");
            }
            try
            {
              localTrustAgentInfo.options.saveToXml(paramXmlSerializer);
              paramXmlSerializer.endTag(null, "trust-agent-component-options");
              paramXmlSerializer.endTag(null, "component");
            }
            catch (XmlPullParserException localXmlPullParserException)
            {
              for (;;)
              {
                Log.e("DevicePolicyManagerService", "Failed to save TrustAgent options", localXmlPullParserException);
              }
            }
          }
        }
        paramXmlSerializer.endTag(null, "manage-trust-agent-features");
      }
      if ((this.crossProfileWidgetProviders == null) || (this.crossProfileWidgetProviders.isEmpty())) {}
      for (;;)
      {
        writePackageListToXml(paramXmlSerializer, "permitted-accessiblity-services", this.permittedAccessiblityServices);
        writePackageListToXml(paramXmlSerializer, "permitted-imes", this.permittedInputMethods);
        writePackageListToXml(paramXmlSerializer, "keep-uninstalled-packages", this.keepUninstalledPackages);
        if (hasUserRestrictions()) {
          UserRestrictionsUtils.writeRestrictions(paramXmlSerializer, this.userRestrictions, "user-restrictions");
        }
        if (!TextUtils.isEmpty(this.shortSupportMessage))
        {
          paramXmlSerializer.startTag(null, "short-support-message");
          paramXmlSerializer.text(this.shortSupportMessage.toString());
          paramXmlSerializer.endTag(null, "short-support-message");
        }
        if (!TextUtils.isEmpty(this.longSupportMessage))
        {
          paramXmlSerializer.startTag(null, "long-support-message");
          paramXmlSerializer.text(this.longSupportMessage.toString());
          paramXmlSerializer.endTag(null, "long-support-message");
        }
        if (this.parentAdmin != null)
        {
          paramXmlSerializer.startTag(null, "parent-admin");
          this.parentAdmin.writeToXml(paramXmlSerializer);
          paramXmlSerializer.endTag(null, "parent-admin");
        }
        if (this.organizationColor != DEF_ORGANIZATION_COLOR)
        {
          paramXmlSerializer.startTag(null, "organization-color");
          paramXmlSerializer.attribute(null, "value", Integer.toString(this.organizationColor));
          paramXmlSerializer.endTag(null, "organization-color");
        }
        if (this.organizationName != null)
        {
          paramXmlSerializer.startTag(null, "organization-name");
          paramXmlSerializer.text(this.organizationName);
          paramXmlSerializer.endTag(null, "organization-name");
        }
        return;
        paramXmlSerializer.startTag(null, "cross-profile-widget-providers");
        int j = this.crossProfileWidgetProviders.size();
        int i = 0;
        while (i < j)
        {
          localObject1 = (String)this.crossProfileWidgetProviders.get(i);
          paramXmlSerializer.startTag(null, "provider");
          paramXmlSerializer.attribute(null, "value", (String)localObject1);
          paramXmlSerializer.endTag(null, "provider");
          i += 1;
        }
        paramXmlSerializer.endTag(null, "cross-profile-widget-providers");
      }
    }
    
    static class TrustAgentInfo
    {
      public PersistableBundle options;
      
      TrustAgentInfo(PersistableBundle paramPersistableBundle)
      {
        this.options = paramPersistableBundle;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L, 3L, 4L, 7L})
  private static @interface DeviceOwnerPreConditionCode {}
  
  public static class DevicePolicyData
  {
    boolean doNotAskCredentialsOnBoot = false;
    final ArraySet<String> mAcceptedCaCertificates = new ArraySet();
    int mActivePasswordLength = 0;
    int mActivePasswordLetters = 0;
    int mActivePasswordLowerCase = 0;
    int mActivePasswordNonLetter = 0;
    int mActivePasswordNumeric = 0;
    int mActivePasswordQuality = 0;
    int mActivePasswordSymbols = 0;
    int mActivePasswordUpperCase = 0;
    boolean mAdminBroadcastPending = false;
    final ArrayList<DevicePolicyManagerService.ActiveAdmin> mAdminList = new ArrayList();
    final ArrayMap<ComponentName, DevicePolicyManagerService.ActiveAdmin> mAdminMap = new ArrayMap();
    Set<String> mAffiliationIds = new ArraySet();
    String mApplicationRestrictionsManagingPackage;
    String mDelegatedCertInstallerPackage;
    boolean mDeviceProvisioningConfigApplied = false;
    int mFailedPasswordAttempts = 0;
    PersistableBundle mInitBundle = null;
    long mLastMaximumTimeToLock = -1L;
    List<String> mLockTaskPackages = new ArrayList();
    int mPasswordOwner = -1;
    int mPermissionPolicy;
    final ArrayList<ComponentName> mRemovingAdmins = new ArrayList();
    ComponentName mRestrictionsProvider;
    boolean mStatusBarDisabled = false;
    int mUserHandle;
    int mUserProvisioningState;
    boolean mUserSetupComplete = false;
    
    public DevicePolicyData(int paramInt)
    {
      this.mUserHandle = paramInt;
    }
  }
  
  static class Injector
  {
    private final Context mContext;
    
    Injector(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    long binderClearCallingIdentity()
    {
      return Binder.clearCallingIdentity();
    }
    
    int binderGetCallingPid()
    {
      return Binder.getCallingPid();
    }
    
    int binderGetCallingUid()
    {
      return Binder.getCallingUid();
    }
    
    UserHandle binderGetCallingUserHandle()
    {
      return Binder.getCallingUserHandle();
    }
    
    boolean binderIsCallingUidMyUid()
    {
      return DevicePolicyManagerService.getCallingUid() == Process.myUid();
    }
    
    void binderRestoreCallingIdentity(long paramLong)
    {
      Binder.restoreCallingIdentity(paramLong);
    }
    
    File environmentGetUserSystemDirectory(int paramInt)
    {
      return Environment.getUserSystemDirectory(paramInt);
    }
    
    String getDevicePolicyFilePathForSystemUser()
    {
      return "/data/system/";
    }
    
    IActivityManager getIActivityManager()
    {
      return ActivityManagerNative.getDefault();
    }
    
    IAudioService getIAudioService()
    {
      return IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
    }
    
    IBackupManager getIBackupManager()
    {
      return IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    }
    
    IPackageManager getIPackageManager()
    {
      return AppGlobals.getPackageManager();
    }
    
    IWindowManager getIWindowManager()
    {
      return IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    }
    
    Looper getMyLooper()
    {
      return Looper.myLooper();
    }
    
    NotificationManager getNotificationManager()
    {
      return (NotificationManager)this.mContext.getSystemService(NotificationManager.class);
    }
    
    PackageManagerInternal getPackageManagerInternal()
    {
      return (PackageManagerInternal)LocalServices.getService(PackageManagerInternal.class);
    }
    
    PowerManagerInternal getPowerManagerInternal()
    {
      return (PowerManagerInternal)LocalServices.getService(PowerManagerInternal.class);
    }
    
    TelephonyManager getTelephonyManager()
    {
      return TelephonyManager.from(this.mContext);
    }
    
    TrustManager getTrustManager()
    {
      return (TrustManager)this.mContext.getSystemService("trust");
    }
    
    UserManager getUserManager()
    {
      return UserManager.get(this.mContext);
    }
    
    UserManagerInternal getUserManagerInternal()
    {
      return (UserManagerInternal)LocalServices.getService(UserManagerInternal.class);
    }
    
    WifiManager getWifiManager()
    {
      return (WifiManager)this.mContext.getSystemService(WifiManager.class);
    }
    
    LockPatternUtils newLockPatternUtils()
    {
      return new LockPatternUtils(this.mContext);
    }
    
    Owners newOwners()
    {
      return new Owners(getUserManager(), getUserManagerInternal(), getPackageManagerInternal());
    }
    
    void powerManagerGoToSleep(long paramLong, int paramInt1, int paramInt2)
    {
      ((PowerManager)this.mContext.getSystemService(PowerManager.class)).goToSleep(paramLong, paramInt1, paramInt2);
    }
    
    void powerManagerReboot(String paramString)
    {
      ((PowerManager)this.mContext.getSystemService(PowerManager.class)).reboot(paramString);
    }
    
    void registerContentObserver(Uri paramUri, boolean paramBoolean, ContentObserver paramContentObserver, int paramInt)
    {
      this.mContext.getContentResolver().registerContentObserver(paramUri, paramBoolean, paramContentObserver, paramInt);
    }
    
    boolean securityLogGetLoggingEnabledProperty()
    {
      return SecurityLog.getLoggingEnabledProperty();
    }
    
    boolean securityLogIsLoggingEnabled()
    {
      return SecurityLog.isLoggingEnabled();
    }
    
    void securityLogSetLoggingEnabledProperty(boolean paramBoolean)
    {
      SecurityLog.setLoggingEnabledProperty(paramBoolean);
    }
    
    int settingsGlobalGetInt(String paramString, int paramInt)
    {
      return Settings.Global.getInt(this.mContext.getContentResolver(), paramString, paramInt);
    }
    
    void settingsGlobalPutInt(String paramString, int paramInt)
    {
      Settings.Global.putInt(this.mContext.getContentResolver(), paramString, paramInt);
    }
    
    void settingsGlobalPutString(String paramString1, String paramString2)
    {
      Settings.Global.putString(this.mContext.getContentResolver(), paramString1, paramString2);
    }
    
    void settingsGlobalPutStringForUser(String paramString1, String paramString2, int paramInt)
    {
      Settings.Global.putStringForUser(this.mContext.getContentResolver(), paramString1, paramString2, paramInt);
    }
    
    int settingsSecureGetIntForUser(String paramString, int paramInt1, int paramInt2)
    {
      return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), paramString, paramInt1, paramInt2);
    }
    
    void settingsSecurePutInt(String paramString, int paramInt)
    {
      Settings.Secure.putInt(this.mContext.getContentResolver(), paramString, paramInt);
    }
    
    void settingsSecurePutIntForUser(String paramString, int paramInt1, int paramInt2)
    {
      Settings.Secure.putIntForUser(this.mContext.getContentResolver(), paramString, paramInt1, paramInt2);
    }
    
    void settingsSecurePutString(String paramString1, String paramString2)
    {
      Settings.Secure.putString(this.mContext.getContentResolver(), paramString1, paramString2);
    }
    
    void settingsSecurePutStringForUser(String paramString1, String paramString2, int paramInt)
    {
      Settings.Secure.putStringForUser(this.mContext.getContentResolver(), paramString1, paramString2, paramInt);
    }
    
    boolean storageManagerIsEncryptable()
    {
      return StorageManager.isEncryptable();
    }
    
    boolean storageManagerIsEncrypted()
    {
      return StorageManager.isEncrypted();
    }
    
    boolean storageManagerIsFileBasedEncryptionEnabled()
    {
      return StorageManager.isFileEncryptedNativeOnly();
    }
    
    boolean storageManagerIsNonDefaultBlockEncrypted()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        boolean bool = StorageManager.isNonDefaultBlockEncrypted();
        return bool;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    String systemPropertiesGet(String paramString)
    {
      return SystemProperties.get(paramString);
    }
    
    String systemPropertiesGet(String paramString1, String paramString2)
    {
      return SystemProperties.get(paramString1, paramString2);
    }
    
    boolean systemPropertiesGetBoolean(String paramString, boolean paramBoolean)
    {
      return SystemProperties.getBoolean(paramString, paramBoolean);
    }
    
    long systemPropertiesGetLong(String paramString, long paramLong)
    {
      return SystemProperties.getLong(paramString, paramLong);
    }
    
    void systemPropertiesSet(String paramString1, String paramString2)
    {
      SystemProperties.set(paramString1, paramString2);
    }
    
    final int userHandleGetCallingUserId()
    {
      return UserHandle.getUserId(binderGetCallingUid());
    }
    
    boolean userManagerIsSplitSystemUser()
    {
      return UserManager.isSplitSystemUser();
    }
  }
  
  public static final class Lifecycle
    extends SystemService
  {
    private DevicePolicyManagerService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
      this.mService = new DevicePolicyManagerService(paramContext);
    }
    
    public void onBootPhase(int paramInt)
    {
      this.mService.systemReady(paramInt);
    }
    
    public void onStart()
    {
      publishBinderService("device_policy", this.mService);
    }
    
    public void onStartUser(int paramInt)
    {
      DevicePolicyManagerService.-wrap12(this.mService, paramInt);
    }
  }
  
  final class LocalService
    extends DevicePolicyManagerInternal
  {
    private List<DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener> mWidgetProviderListeners;
    
    LocalService() {}
    
    private void notifyCrossProfileProvidersChanged(int paramInt, List<String> paramList)
    {
      synchronized (DevicePolicyManagerService.this)
      {
        ArrayList localArrayList = new ArrayList(this.mWidgetProviderListeners);
        int j = localArrayList.size();
        int i = 0;
        if (i < j)
        {
          ((DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener)localArrayList.get(i)).onCrossProfileWidgetProvidersChanged(paramInt, paramList);
          i += 1;
        }
      }
    }
    
    public void addOnCrossProfileWidgetProvidersChangeListener(DevicePolicyManagerInternal.OnCrossProfileWidgetProvidersChangeListener paramOnCrossProfileWidgetProvidersChangeListener)
    {
      synchronized (DevicePolicyManagerService.this)
      {
        if (this.mWidgetProviderListeners == null) {
          this.mWidgetProviderListeners = new ArrayList();
        }
        if (!this.mWidgetProviderListeners.contains(paramOnCrossProfileWidgetProvidersChangeListener)) {
          this.mWidgetProviderListeners.add(paramOnCrossProfileWidgetProvidersChangeListener);
        }
        return;
      }
    }
    
    public Intent createPackageSuspendedDialogIntent(String paramString, int paramInt)
    {
      paramString = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
      paramString.putExtra("android.intent.extra.USER_ID", paramInt);
      paramString.setFlags(268435456);
      Object localObject = DevicePolicyManagerService.this.mOwners.getProfileOwnerComponent(paramInt);
      if (localObject != null)
      {
        paramString.putExtra("android.app.extra.DEVICE_ADMIN", (Parcelable)localObject);
        return paramString;
      }
      localObject = DevicePolicyManagerService.this.mOwners.getDeviceOwnerUserIdAndComponent();
      if ((localObject != null) && (((Integer)((Pair)localObject).first).intValue() == paramInt))
      {
        paramString.putExtra("android.app.extra.DEVICE_ADMIN", (Parcelable)((Pair)localObject).second);
        return paramString;
      }
      return paramString;
    }
    
    public List<String> getCrossProfileWidgetProviders(int paramInt)
    {
      synchronized (DevicePolicyManagerService.this)
      {
        if (DevicePolicyManagerService.this.mOwners == null)
        {
          localObject1 = Collections.emptyList();
          return (List<String>)localObject1;
        }
        Object localObject1 = DevicePolicyManagerService.this.mOwners.getProfileOwnerComponent(paramInt);
        if (localObject1 == null)
        {
          localObject1 = Collections.emptyList();
          return (List<String>)localObject1;
        }
        localObject1 = (DevicePolicyManagerService.ActiveAdmin)DevicePolicyManagerService.this.getUserDataUnchecked(paramInt).mAdminMap.get(localObject1);
        if ((localObject1 == null) || (((DevicePolicyManagerService.ActiveAdmin)localObject1).crossProfileWidgetProviders == null)) {}
        while (((DevicePolicyManagerService.ActiveAdmin)localObject1).crossProfileWidgetProviders.isEmpty())
        {
          localObject1 = Collections.emptyList();
          return (List<String>)localObject1;
        }
        localObject1 = ((DevicePolicyManagerService.ActiveAdmin)localObject1).crossProfileWidgetProviders;
        return (List<String>)localObject1;
      }
    }
    
    public boolean isActiveAdminWithPolicy(int paramInt1, int paramInt2)
    {
      synchronized (DevicePolicyManagerService.this)
      {
        DevicePolicyManagerService.ActiveAdmin localActiveAdmin = DevicePolicyManagerService.-wrap1(DevicePolicyManagerService.this, null, paramInt2, paramInt1);
        if (localActiveAdmin != null)
        {
          bool = true;
          return bool;
        }
        boolean bool = false;
      }
    }
  }
  
  private class MonitoringCertNotificationTask
    extends AsyncTask<Integer, Void, Void>
  {
    private MonitoringCertNotificationTask() {}
    
    /* Error */
    private List<String> getInstalledCaCertificates(UserHandle paramUserHandle)
      throws RemoteException, RuntimeException
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_3
      //   2: aconst_null
      //   3: astore 5
      //   5: aconst_null
      //   6: astore 4
      //   8: aload_0
      //   9: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   12: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   15: aload_1
      //   16: invokestatic 41	android/security/KeyChain:bindAsUser	(Landroid/content/Context;Landroid/os/UserHandle;)Landroid/security/KeyChain$KeyChainConnection;
      //   19: astore_1
      //   20: aload_1
      //   21: astore 4
      //   23: aload_1
      //   24: astore_3
      //   25: aload_1
      //   26: astore 5
      //   28: aload_1
      //   29: invokevirtual 47	android/security/KeyChain$KeyChainConnection:getService	()Landroid/security/IKeyChainService;
      //   32: invokeinterface 53 1 0
      //   37: invokevirtual 59	android/content/pm/ParceledListSlice:getList	()Ljava/util/List;
      //   40: astore 6
      //   42: aload_1
      //   43: astore 4
      //   45: aload_1
      //   46: astore_3
      //   47: aload_1
      //   48: astore 5
      //   50: new 61	java/util/ArrayList
      //   53: dup
      //   54: aload 6
      //   56: invokeinterface 67 1 0
      //   61: invokespecial 70	java/util/ArrayList:<init>	(I)V
      //   64: astore 7
      //   66: iconst_0
      //   67: istore_2
      //   68: aload_1
      //   69: astore 4
      //   71: aload_1
      //   72: astore_3
      //   73: aload_1
      //   74: astore 5
      //   76: iload_2
      //   77: aload 6
      //   79: invokeinterface 67 1 0
      //   84: if_icmpge +40 -> 124
      //   87: aload_1
      //   88: astore 4
      //   90: aload_1
      //   91: astore_3
      //   92: aload_1
      //   93: astore 5
      //   95: aload 7
      //   97: aload 6
      //   99: iload_2
      //   100: invokeinterface 74 2 0
      //   105: checkcast 76	com/android/internal/util/ParcelableString
      //   108: getfield 80	com/android/internal/util/ParcelableString:string	Ljava/lang/String;
      //   111: invokeinterface 84 2 0
      //   116: pop
      //   117: iload_2
      //   118: iconst_1
      //   119: iadd
      //   120: istore_2
      //   121: goto -53 -> 68
      //   124: aload_1
      //   125: ifnull +7 -> 132
      //   128: aload_1
      //   129: invokevirtual 87	android/security/KeyChain$KeyChainConnection:close	()V
      //   132: aload 7
      //   134: areturn
      //   135: astore_1
      //   136: aload 4
      //   138: astore_3
      //   139: new 27	java/lang/RuntimeException
      //   142: dup
      //   143: aload_1
      //   144: invokespecial 90	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
      //   147: athrow
      //   148: astore_1
      //   149: aload_3
      //   150: ifnull +7 -> 157
      //   153: aload_3
      //   154: invokevirtual 87	android/security/KeyChain$KeyChainConnection:close	()V
      //   157: aload_1
      //   158: athrow
      //   159: astore_1
      //   160: aload 5
      //   162: astore_3
      //   163: invokestatic 96	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   166: invokevirtual 99	java/lang/Thread:interrupt	()V
      //   169: aload 5
      //   171: ifnull +8 -> 179
      //   174: aload 5
      //   176: invokevirtual 87	android/security/KeyChain$KeyChainConnection:close	()V
      //   179: aconst_null
      //   180: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	181	0	this	MonitoringCertNotificationTask
      //   0	181	1	paramUserHandle	UserHandle
      //   67	54	2	i	int
      //   1	162	3	localObject	Object
      //   6	131	4	localUserHandle1	UserHandle
      //   3	172	5	localUserHandle2	UserHandle
      //   40	58	6	localList	List
      //   64	69	7	localArrayList	ArrayList
      // Exception table:
      //   from	to	target	type
      //   8	20	135	java/lang/AssertionError
      //   28	42	135	java/lang/AssertionError
      //   50	66	135	java/lang/AssertionError
      //   76	87	135	java/lang/AssertionError
      //   95	117	135	java/lang/AssertionError
      //   8	20	148	finally
      //   28	42	148	finally
      //   50	66	148	finally
      //   76	87	148	finally
      //   95	117	148	finally
      //   139	148	148	finally
      //   163	169	148	finally
      //   8	20	159	java/lang/InterruptedException
      //   28	42	159	java/lang/InterruptedException
      //   50	66	159	java/lang/InterruptedException
      //   76	87	159	java/lang/InterruptedException
      //   95	117	159	java/lang/InterruptedException
    }
    
    /* Error */
    private void manageNotification(UserHandle paramUserHandle)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   4: getfield 110	com/android/server/devicepolicy/DevicePolicyManagerService:mUserManager	Landroid/os/UserManager;
      //   7: aload_1
      //   8: invokevirtual 116	android/os/UserManager:isUserUnlocked	(Landroid/os/UserHandle;)Z
      //   11: ifne +4 -> 15
      //   14: return
      //   15: aload_0
      //   16: aload_1
      //   17: invokespecial 118	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:getInstalledCaCertificates	(Landroid/os/UserHandle;)Ljava/util/List;
      //   20: astore 6
      //   22: aload_0
      //   23: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   26: astore 5
      //   28: aload 5
      //   30: monitorenter
      //   31: aload_0
      //   32: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   35: aload_1
      //   36: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   39: invokevirtual 127	com/android/server/devicepolicy/DevicePolicyManagerService:getUserData	(I)Lcom/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData;
      //   42: astore 7
      //   44: aload 7
      //   46: getfield 133	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAcceptedCaCertificates	Landroid/util/ArraySet;
      //   49: aload 6
      //   51: invokevirtual 139	android/util/ArraySet:retainAll	(Ljava/util/Collection;)Z
      //   54: ifeq +14 -> 68
      //   57: aload_0
      //   58: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   61: aload_1
      //   62: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   65: invokestatic 143	com/android/server/devicepolicy/DevicePolicyManagerService:-wrap15	(Lcom/android/server/devicepolicy/DevicePolicyManagerService;I)V
      //   68: aload 6
      //   70: aload 7
      //   72: getfield 133	com/android/server/devicepolicy/DevicePolicyManagerService$DevicePolicyData:mAcceptedCaCertificates	Landroid/util/ArraySet;
      //   75: invokeinterface 146 2 0
      //   80: pop
      //   81: aload 5
      //   83: monitorexit
      //   84: aload 6
      //   86: invokeinterface 150 1 0
      //   91: ifeq +38 -> 129
      //   94: aload_0
      //   95: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   98: getfield 154	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
      //   101: invokevirtual 160	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getNotificationManager	()Landroid/app/NotificationManager;
      //   104: aconst_null
      //   105: ldc -95
      //   107: aload_1
      //   108: invokevirtual 167	android/app/NotificationManager:cancelAsUser	(Ljava/lang/String;ILandroid/os/UserHandle;)V
      //   111: return
      //   112: astore_1
      //   113: ldc -87
      //   115: ldc -85
      //   117: aload_1
      //   118: invokestatic 177	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   121: pop
      //   122: return
      //   123: astore_1
      //   124: aload 5
      //   126: monitorexit
      //   127: aload_1
      //   128: athrow
      //   129: aload_1
      //   130: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   133: istore_3
      //   134: aload_0
      //   135: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   138: aload_1
      //   139: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   142: invokevirtual 181	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwner	(I)Landroid/content/ComponentName;
      //   145: ifnull +254 -> 399
      //   148: aload_0
      //   149: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   152: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   155: ldc -74
      //   157: iconst_1
      //   158: anewarray 184	java/lang/Object
      //   161: dup
      //   162: iconst_0
      //   163: aload_0
      //   164: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   167: aload_1
      //   168: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   171: invokevirtual 188	com/android/server/devicepolicy/DevicePolicyManagerService:getProfileOwnerName	(I)Ljava/lang/String;
      //   174: aastore
      //   175: invokevirtual 194	android/content/Context:getString	(I[Ljava/lang/Object;)Ljava/lang/String;
      //   178: astore 5
      //   180: ldc -61
      //   182: istore_2
      //   183: aload_0
      //   184: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   187: aload_1
      //   188: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   191: invokestatic 199	com/android/server/devicepolicy/DevicePolicyManagerService:-wrap2	(Lcom/android/server/devicepolicy/DevicePolicyManagerService;I)I
      //   194: istore_3
      //   195: aload 6
      //   197: invokeinterface 67 1 0
      //   202: istore 4
      //   204: new 201	android/content/Intent
      //   207: dup
      //   208: ldc -53
      //   210: invokespecial 206	android/content/Intent:<init>	(Ljava/lang/String;)V
      //   213: astore 6
      //   215: aload 6
      //   217: ldc -49
      //   219: invokevirtual 211	android/content/Intent:setFlags	(I)Landroid/content/Intent;
      //   222: pop
      //   223: aload 6
      //   225: ldc -43
      //   227: invokevirtual 217	android/content/Intent:setPackage	(Ljava/lang/String;)Landroid/content/Intent;
      //   230: pop
      //   231: aload 6
      //   233: ldc -37
      //   235: iload 4
      //   237: invokevirtual 223	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
      //   240: pop
      //   241: aload 6
      //   243: ldc -31
      //   245: aload_1
      //   246: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   249: invokevirtual 223	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
      //   252: pop
      //   253: aload_0
      //   254: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   257: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   260: iconst_0
      //   261: aload 6
      //   263: ldc -30
      //   265: aconst_null
      //   266: new 120	android/os/UserHandle
      //   269: dup
      //   270: iload_3
      //   271: invokespecial 227	android/os/UserHandle:<init>	(I)V
      //   274: invokestatic 233	android/app/PendingIntent:getActivityAsUser	(Landroid/content/Context;ILandroid/content/Intent;ILandroid/os/Bundle;Landroid/os/UserHandle;)Landroid/app/PendingIntent;
      //   277: astore 6
      //   279: aload_0
      //   280: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   283: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   286: invokevirtual 237	android/content/Context:getPackageName	()Ljava/lang/String;
      //   289: astore 7
      //   291: aload_0
      //   292: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   295: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   298: aload 7
      //   300: iconst_0
      //   301: aload_1
      //   302: invokevirtual 241	android/content/Context:createPackageContextAsUser	(Ljava/lang/String;ILandroid/os/UserHandle;)Landroid/content/Context;
      //   305: astore 7
      //   307: new 243	android/app/Notification$Builder
      //   310: dup
      //   311: aload 7
      //   313: invokespecial 246	android/app/Notification$Builder:<init>	(Landroid/content/Context;)V
      //   316: iload_2
      //   317: invokevirtual 250	android/app/Notification$Builder:setSmallIcon	(I)Landroid/app/Notification$Builder;
      //   320: aload_0
      //   321: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   324: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   327: invokevirtual 254	android/content/Context:getResources	()Landroid/content/res/Resources;
      //   330: ldc -95
      //   332: iload 4
      //   334: invokevirtual 260	android/content/res/Resources:getQuantityText	(II)Ljava/lang/CharSequence;
      //   337: invokevirtual 264	android/app/Notification$Builder:setContentTitle	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
      //   340: aload 5
      //   342: invokevirtual 267	android/app/Notification$Builder:setContentText	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
      //   345: aload 6
      //   347: invokevirtual 271	android/app/Notification$Builder:setContentIntent	(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;
      //   350: iconst_1
      //   351: invokevirtual 274	android/app/Notification$Builder:setPriority	(I)Landroid/app/Notification$Builder;
      //   354: iconst_0
      //   355: invokevirtual 278	android/app/Notification$Builder:setShowWhen	(Z)Landroid/app/Notification$Builder;
      //   358: aload_0
      //   359: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   362: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   365: ldc_w 279
      //   368: invokevirtual 283	android/content/Context:getColor	(I)I
      //   371: invokevirtual 286	android/app/Notification$Builder:setColor	(I)Landroid/app/Notification$Builder;
      //   374: invokevirtual 290	android/app/Notification$Builder:build	()Landroid/app/Notification;
      //   377: astore 5
      //   379: aload_0
      //   380: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   383: getfield 154	com/android/server/devicepolicy/DevicePolicyManagerService:mInjector	Lcom/android/server/devicepolicy/DevicePolicyManagerService$Injector;
      //   386: invokevirtual 160	com/android/server/devicepolicy/DevicePolicyManagerService$Injector:getNotificationManager	()Landroid/app/NotificationManager;
      //   389: aconst_null
      //   390: ldc -95
      //   392: aload 5
      //   394: aload_1
      //   395: invokevirtual 294	android/app/NotificationManager:notifyAsUser	(Ljava/lang/String;ILandroid/app/Notification;Landroid/os/UserHandle;)V
      //   398: return
      //   399: aload_0
      //   400: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   403: invokevirtual 297	com/android/server/devicepolicy/DevicePolicyManagerService:getDeviceOwnerUserId	()I
      //   406: aload_1
      //   407: invokevirtual 123	android/os/UserHandle:getIdentifier	()I
      //   410: if_icmpne +37 -> 447
      //   413: aload_0
      //   414: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   417: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   420: ldc -74
      //   422: iconst_1
      //   423: anewarray 184	java/lang/Object
      //   426: dup
      //   427: iconst_0
      //   428: aload_0
      //   429: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   432: invokevirtual 300	com/android/server/devicepolicy/DevicePolicyManagerService:getDeviceOwnerName	()Ljava/lang/String;
      //   435: aastore
      //   436: invokevirtual 194	android/content/Context:getString	(I[Ljava/lang/Object;)Ljava/lang/String;
      //   439: astore 5
      //   441: ldc -61
      //   443: istore_2
      //   444: goto -249 -> 195
      //   447: aload_0
      //   448: getfield 14	com/android/server/devicepolicy/DevicePolicyManagerService$MonitoringCertNotificationTask:this$0	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
      //   451: getfield 35	com/android/server/devicepolicy/DevicePolicyManagerService:mContext	Landroid/content/Context;
      //   454: ldc_w 301
      //   457: invokevirtual 303	android/content/Context:getString	(I)Ljava/lang/String;
      //   460: astore 5
      //   462: ldc_w 304
      //   465: istore_2
      //   466: goto -271 -> 195
      //   469: astore 5
      //   471: ldc -87
      //   473: new 306	java/lang/StringBuilder
      //   476: dup
      //   477: invokespecial 307	java/lang/StringBuilder:<init>	()V
      //   480: ldc_w 309
      //   483: invokevirtual 313	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   486: aload_1
      //   487: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   490: ldc_w 318
      //   493: invokevirtual 313	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   496: invokevirtual 321	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   499: aload 5
      //   501: invokestatic 177	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   504: pop
      //   505: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	506	0	this	MonitoringCertNotificationTask
      //   0	506	1	paramUserHandle	UserHandle
      //   182	284	2	i	int
      //   133	138	3	j	int
      //   202	131	4	k	int
      //   469	31	5	localNameNotFoundException	PackageManager.NameNotFoundException
      //   20	326	6	localObject2	Object
      //   42	270	7	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   15	22	112	android/os/RemoteException
      //   15	22	112	java/lang/RuntimeException
      //   31	68	123	finally
      //   68	81	123	finally
      //   279	307	469	android/content/pm/PackageManager$NameNotFoundException
    }
    
    protected Void doInBackground(Integer... paramVarArgs)
    {
      int i = paramVarArgs[0].intValue();
      if (i == -1)
      {
        paramVarArgs = DevicePolicyManagerService.this.mUserManager.getUsers(true).iterator();
        while (paramVarArgs.hasNext()) {
          manageNotification(((UserInfo)paramVarArgs.next()).getUserHandle());
        }
      }
      manageNotification(UserHandle.of(i));
      return null;
    }
  }
  
  private class SetupContentObserver
    extends ContentObserver
  {
    private final Uri mDeviceProvisioned = Settings.Global.getUriFor("device_provisioned");
    private final Uri mUserSetupComplete = Settings.Secure.getUriFor("user_setup_complete");
    
    public SetupContentObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri arg2)
    {
      if (this.mUserSetupComplete.equals(???)) {
        DevicePolicyManagerService.this.updateUserSetupComplete();
      }
      while (!this.mDeviceProvisioned.equals(???)) {
        return;
      }
      synchronized (DevicePolicyManagerService.this)
      {
        DevicePolicyManagerService.-wrap19(DevicePolicyManagerService.this);
        return;
      }
    }
    
    void register()
    {
      DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mUserSetupComplete, false, this, -1);
      DevicePolicyManagerService.this.mInjector.registerContentObserver(this.mDeviceProvisioned, false, this, -1);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/devicepolicy/DevicePolicyManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */