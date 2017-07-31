package com.android.providers.settings;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.hardware.camera2.utils.ArrayUtils;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.os.UserManagerInternal.UserRestrictionsListener;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.provider.Settings.System.Validator;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.OpFeatures;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsProvider
  extends ContentProvider
{
  private static final String[] ALL_COLUMNS;
  private static final boolean DEBUG = Build.DEBUG_ONEPLUS;
  private static final Bundle NULL_SETTING_BUNDLE;
  private static final Set<String> REMOVED_LEGACY_TABLES = new ArraySet();
  static final Set<String> sGlobalMovedToSecureSettings;
  private static final Set<String> sSecureCloneToManagedSettings;
  static final Set<String> sSecureMovedToGlobalSettings;
  private static final Set<String> sSystemCloneToManagedSettings;
  static final Set<String> sSystemMovedToGlobalSettings;
  static final Set<String> sSystemMovedToSecureSettings;
  private final Object mAndroidIdLock = new Object();
  @GuardedBy("mLock")
  private HandlerThread mHandlerThread;
  private final Object mLock = new Object();
  private volatile IPackageManager mPackageManager;
  @GuardedBy("mLock")
  private SettingsRegistry mSettingsRegistry;
  private volatile UserManager mUserManager;
  
  static
  {
    REMOVED_LEGACY_TABLES.add("favorites");
    REMOVED_LEGACY_TABLES.add("old_favorites");
    REMOVED_LEGACY_TABLES.add("bluetooth_devices");
    REMOVED_LEGACY_TABLES.add("bookmarks");
    REMOVED_LEGACY_TABLES.add("android_metadata");
    ALL_COLUMNS = new String[] { "_id", "name", "value" };
    NULL_SETTING_BUNDLE = Bundle.forPair("value", null);
    sSecureMovedToGlobalSettings = new ArraySet();
    Settings.Secure.getMovedToGlobalSettings(sSecureMovedToGlobalSettings);
    sSystemMovedToGlobalSettings = new ArraySet();
    Settings.System.getMovedToGlobalSettings(sSystemMovedToGlobalSettings);
    sSystemMovedToSecureSettings = new ArraySet();
    Settings.System.getMovedToSecureSettings(sSystemMovedToSecureSettings);
    sGlobalMovedToSecureSettings = new ArraySet();
    Settings.Global.getMovedToSecureSettings(sGlobalMovedToSecureSettings);
    sSecureCloneToManagedSettings = new ArraySet();
    Settings.Secure.getCloneToManagedProfileSettings(sSecureCloneToManagedSettings);
    sSystemCloneToManagedSettings = new ArraySet();
    Settings.System.getCloneToManagedProfileSettings(sSystemCloneToManagedSettings);
  }
  
  private static void appendSettingToCursor(MatrixCursor paramMatrixCursor, SettingsState.Setting paramSetting)
  {
    if (paramSetting.isNull()) {
      return;
    }
    int j = paramMatrixCursor.getColumnCount();
    String[] arrayOfString = new String[j];
    int i = 0;
    if (i < j)
    {
      String str = paramMatrixCursor.getColumnName(i);
      if (str.equals("_id")) {
        arrayOfString[i] = paramSetting.getId();
      }
      for (;;)
      {
        i += 1;
        break;
        if (str.equals("name")) {
          arrayOfString[i] = paramSetting.getName();
        } else if (str.equals("value")) {
          arrayOfString[i] = paramSetting.getValue();
        }
      }
    }
    paramMatrixCursor.addRow(arrayOfString);
  }
  
  private boolean deleteGlobalSetting(String paramString, int paramInt, boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "deleteGlobalSettingLocked(" + paramString + ")");
    }
    return mutateGlobalSetting(paramString, null, paramInt, 2, paramBoolean);
  }
  
  private boolean deleteSecureSetting(String paramString, int paramInt, boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "deleteSecureSetting(" + paramString + ", " + paramInt + ")");
    }
    return mutateSecureSetting(paramString, null, paramInt, 2, paramBoolean);
  }
  
  private boolean deleteSystemSetting(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "deleteSystemSetting(" + paramString + ", " + paramInt + ")");
    }
    return mutateSystemSetting(paramString, null, paramInt, 2);
  }
  
  private void dumpForUserLocked(int paramInt, PrintWriter paramPrintWriter)
  {
    if (paramInt == 0)
    {
      paramPrintWriter.println("GLOBAL SETTINGS (user " + paramInt + ")");
      dumpSettings(getAllGlobalSettings(ALL_COLUMNS), paramPrintWriter);
      paramPrintWriter.println();
      this.mSettingsRegistry.getSettingsLocked(0, 0).dumpHistoricalOperations(paramPrintWriter);
    }
    paramPrintWriter.println("SECURE SETTINGS (user " + paramInt + ")");
    dumpSettings(getAllSecureSettings(paramInt, ALL_COLUMNS), paramPrintWriter);
    paramPrintWriter.println();
    this.mSettingsRegistry.getSettingsLocked(2, paramInt).dumpHistoricalOperations(paramPrintWriter);
    paramPrintWriter.println("SYSTEM SETTINGS (user " + paramInt + ")");
    dumpSettings(getAllSystemSettings(paramInt, ALL_COLUMNS), paramPrintWriter);
    paramPrintWriter.println();
    this.mSettingsRegistry.getSettingsLocked(1, paramInt).dumpHistoricalOperations(paramPrintWriter);
  }
  
  private void dumpSettings(Cursor paramCursor, PrintWriter paramPrintWriter)
  {
    if ((paramCursor != null) && (paramCursor.moveToFirst()))
    {
      int i = paramCursor.getColumnIndex("_id");
      int j = paramCursor.getColumnIndex("name");
      int k = paramCursor.getColumnIndex("value");
      do
      {
        paramPrintWriter.append("_id:").append(toDumpString(paramCursor.getString(i)));
        paramPrintWriter.append(" name:").append(toDumpString(paramCursor.getString(j)));
        paramPrintWriter.append(" value:").append(toDumpString(paramCursor.getString(k)));
        paramPrintWriter.println();
      } while (paramCursor.moveToNext());
      return;
    }
  }
  
  private void enforceRestrictedSystemSettingsMutationForCallingPackage(int paramInt1, String paramString, int paramInt2)
  {
    int i = Binder.getCallingUid();
    if ((i == 1000) || (i == 2000)) {}
    while (i == 0) {
      return;
    }
    switch (paramInt1)
    {
    default: 
      return;
    case 1: 
    case 3: 
      if (Settings.System.PUBLIC_SETTINGS.contains(paramString)) {
        return;
      }
      localPackageInfo = getCallingPackageInfoOrThrow(paramInt2);
      if ((localPackageInfo.applicationInfo.privateFlags & 0x8) != 0) {
        return;
      }
      warnOrThrowForUndesiredSecureSettingsMutationForTargetSdk(localPackageInfo.applicationInfo.targetSdkVersion, paramString);
      return;
    }
    if ((Settings.System.PUBLIC_SETTINGS.contains(paramString)) || (Settings.System.PRIVATE_SETTINGS.contains(paramString))) {
      throw new IllegalArgumentException("You cannot delete system defined secure settings.");
    }
    PackageInfo localPackageInfo = getCallingPackageInfoOrThrow(paramInt2);
    if ((localPackageInfo.applicationInfo.privateFlags & 0x8) != 0) {
      return;
    }
    warnOrThrowForUndesiredSecureSettingsMutationForTargetSdk(localPackageInfo.applicationInfo.targetSdkVersion, paramString);
  }
  
  private void enforceWritePermission(String paramString)
  {
    if (getContext().checkCallingOrSelfPermission(paramString) != 0) {
      throw new SecurityException("Permission denial: writing to settings requires:" + paramString);
    }
  }
  
  private Cursor getAllGlobalSettings(String[] paramArrayOfString)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "getAllGlobalSettings()");
    }
    synchronized (this.mLock)
    {
      SettingsState localSettingsState = this.mSettingsRegistry.getSettingsLocked(0, 0);
      List localList = localSettingsState.getSettingNamesLocked();
      int j = localList.size();
      paramArrayOfString = new MatrixCursor(normalizeProjection(paramArrayOfString), j);
      int i = 0;
      while (i < j)
      {
        appendSettingToCursor(paramArrayOfString, localSettingsState.getSettingLocked((String)localList.get(i)));
        i += 1;
      }
      return paramArrayOfString;
    }
  }
  
  private Cursor getAllSecureSettings(int paramInt, String[] paramArrayOfString)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "getAllSecureSettings(" + paramInt + ")");
    }
    int i = resolveCallingUserIdEnforcingPermissionsLocked(paramInt);
    for (;;)
    {
      synchronized (this.mLock)
      {
        List localList = this.mSettingsRegistry.getSettingsNamesLocked(2, i);
        int j = localList.size();
        paramArrayOfString = new MatrixCursor(normalizeProjection(paramArrayOfString), j);
        paramInt = 0;
        if (paramInt < j)
        {
          String str = (String)localList.get(paramInt);
          int k = resolveOwningUserIdForSecureSettingLocked(i, str);
          if (isLocationProvidersAllowedRestricted(str, i, k)) {
            break label160;
          }
          appendSettingToCursor(paramArrayOfString, this.mSettingsRegistry.getSettingLocked(2, k, str));
        }
      }
      return paramArrayOfString;
      label160:
      paramInt += 1;
    }
  }
  
  private Cursor getAllSystemSettings(int paramInt, String[] paramArrayOfString)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "getAllSecureSystem(" + paramInt + ")");
    }
    int i = resolveCallingUserIdEnforcingPermissionsLocked(paramInt);
    synchronized (this.mLock)
    {
      List localList = this.mSettingsRegistry.getSettingsNamesLocked(1, i);
      int j = localList.size();
      paramArrayOfString = new MatrixCursor(normalizeProjection(paramArrayOfString), j);
      paramInt = 0;
      while (paramInt < j)
      {
        String str = (String)localList.get(paramInt);
        int k = resolveOwningUserIdForSystemSettingLocked(i, str);
        appendSettingToCursor(paramArrayOfString, this.mSettingsRegistry.getSettingLocked(1, k, str));
        paramInt += 1;
      }
      return paramArrayOfString;
    }
  }
  
  private PackageInfo getCallingPackageInfoOrThrow(int paramInt)
  {
    try
    {
      PackageInfo localPackageInfo = this.mPackageManager.getPackageInfo(getCallingPackage(), 0, paramInt);
      if (localPackageInfo != null) {
        return localPackageInfo;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw new IllegalStateException("Calling package doesn't exist");
    }
  }
  
  private SettingsState.Setting getGlobalSetting(String paramString)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "getGlobalSetting(" + paramString + ")");
    }
    synchronized (this.mLock)
    {
      paramString = this.mSettingsRegistry.getSettingLocked(0, 0, paramString);
      return paramString;
    }
  }
  
  private int getGroupParentLocked(int paramInt)
  {
    if (paramInt == 0) {
      return paramInt;
    }
    long l = Binder.clearCallingIdentity();
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
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private static int getRequestingUserId(Bundle paramBundle)
  {
    int j = UserHandle.getCallingUserId();
    int i = j;
    if (paramBundle != null) {
      i = paramBundle.getInt("_user", j);
    }
    return i;
  }
  
  private File getRingtoneCacheDir(int paramInt)
  {
    File localFile = new File(Environment.getDataSystemDeDirectory(paramInt), "ringtones");
    localFile.mkdir();
    SELinux.restorecon(localFile);
    return localFile;
  }
  
  private SettingsState.Setting getSecureSetting(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "getSecureSetting(" + paramString + ", " + paramInt + ")");
    }
    paramInt = resolveCallingUserIdEnforcingPermissionsLocked(paramInt);
    int i = resolveOwningUserIdForSecureSettingLocked(paramInt, paramString);
    if (isLocationProvidersAllowedRestricted(paramString, paramInt, i)) {
      return this.mSettingsRegistry.getSettingsLocked(2, i).getNullSetting();
    }
    synchronized (this.mLock)
    {
      paramString = this.mSettingsRegistry.getSettingLocked(2, i, paramString);
      return paramString;
    }
  }
  
  private static String getSettingValue(Bundle paramBundle)
  {
    String str = null;
    if (paramBundle != null) {
      str = paramBundle.getString("value");
    }
    return str;
  }
  
  private SettingsState.Setting getSystemSetting(String paramString, int paramInt)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "getSystemSetting(" + paramString + ", " + paramInt + ")");
    }
    paramInt = resolveOwningUserIdForSystemSettingLocked(resolveCallingUserIdEnforcingPermissionsLocked(paramInt), paramString);
    synchronized (this.mLock)
    {
      paramString = this.mSettingsRegistry.getSettingLocked(1, paramInt, paramString);
      return paramString;
    }
  }
  
  public static int getTypeFromKey(int paramInt)
  {
    return paramInt >>> 28;
  }
  
  public static int getUserIdFromKey(int paramInt)
  {
    return 0xFFFFFFF & paramInt;
  }
  
  private static String getValidTableOrThrow(Uri paramUri)
  {
    if (paramUri.getPathSegments().size() > 0)
    {
      paramUri = (String)paramUri.getPathSegments().get(0);
      if (DatabaseHelper.isValidTable(paramUri)) {
        return paramUri;
      }
      throw new IllegalArgumentException("Bad root path: " + paramUri);
    }
    throw new IllegalArgumentException("Invalid URI:" + paramUri);
  }
  
  private boolean hasWriteSecureSettingsPermission()
  {
    return getContext().checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0;
  }
  
  private boolean insertGlobalSetting(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "insertGlobalSetting(" + paramString1 + ", " + paramString2 + ")");
    }
    return mutateGlobalSetting(paramString1, paramString2, paramInt, 1, paramBoolean);
  }
  
  private boolean insertSecureSetting(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "insertSecureSetting(" + paramString1 + ", " + paramString2 + ", " + paramInt + ")");
    }
    return mutateSecureSetting(paramString1, paramString2, paramInt, 1, paramBoolean);
  }
  
  private boolean insertSystemSetting(String paramString1, String paramString2, int paramInt)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "insertSystemSetting(" + paramString1 + ", " + paramString2 + ", " + paramInt + ")");
    }
    return mutateSystemSetting(paramString1, paramString2, paramInt, 1);
  }
  
  private boolean isGlobalOrSecureSettingRestrictedForUser(String paramString1, int paramInt1, String paramString2, int paramInt2)
  {
    if (paramString1.equals("location_mode"))
    {
      if (String.valueOf(0).equals(paramString2)) {
        return false;
      }
    }
    else
    {
      if (paramString1.equals("location_providers_allowed"))
      {
        if ((paramString2 == null) || (!paramString2.startsWith("-"))) {
          break label216;
        }
        return false;
      }
      if (paramString1.equals("install_non_market_apps"))
      {
        if (!"0".equals(paramString2)) {
          break label223;
        }
        return false;
      }
      if (paramString1.equals("adb_enabled"))
      {
        if (!"0".equals(paramString2)) {
          break label230;
        }
        return false;
      }
      if (paramString1.equals("package_verifier_enable")) {}
      for (;;)
      {
        if ("1".equals(paramString2))
        {
          return false;
          if (!paramString1.equals("verifier_verify_adb_installs")) {
            if (paramString1.equals("preferred_network_mode")) {
              paramString1 = "no_config_mobile_networks";
            }
          }
        }
      }
    }
    for (;;)
    {
      return this.mUserManager.hasUserRestriction(paramString1, UserHandle.of(paramInt1));
      if (paramString1.equals("always_on_vpn_app")) {}
      for (;;)
      {
        if ((paramInt2 == 1000) || (paramInt2 == 0))
        {
          return false;
          if (!paramString1.equals("always_on_vpn_lockdown"))
          {
            if (!paramString1.equals("safe_boot_disallowed")) {
              break label258;
            }
            if (!"1".equals(paramString2)) {
              break label251;
            }
            return false;
            paramString1 = "no_share_location";
            break;
            label216:
            paramString1 = "no_share_location";
            break;
            label223:
            paramString1 = "no_install_unknown_sources";
            break;
            label230:
            paramString1 = "no_debugging_features";
            break;
            paramString1 = "ensure_verify_apps";
            break;
          }
        }
      }
      paramString1 = "no_config_vpn";
      continue;
      label251:
      paramString1 = "no_safe_boot";
      continue;
      label258:
      if ((paramString1 == null) || (!paramString1.startsWith("data_roaming"))) {
        break;
      }
      if ("0".equals(paramString2)) {
        return false;
      }
      paramString1 = "no_data_roaming";
    }
    return false;
  }
  
  private static boolean isKeyValid(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (!SettingsState.isBinary(paramString));
  }
  
  private boolean isLocationProvidersAllowedRestricted(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return false;
    }
    return ("location_providers_allowed".equals(paramString)) && (this.mUserManager.hasUserRestriction("no_share_location", new UserHandle(paramInt1)));
  }
  
  private boolean isTrackingGeneration(Bundle paramBundle)
  {
    if (paramBundle != null) {
      return paramBundle.containsKey("_track_generation");
    }
    return false;
  }
  
  public static int makeKey(int paramInt1, int paramInt2)
  {
    return paramInt1 << 28 | paramInt2;
  }
  
  private boolean mutateGlobalSetting(String paramString1, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    enforceWritePermission("android.permission.WRITE_SECURE_SETTINGS");
    if (isGlobalOrSecureSettingRestrictedForUser(paramString1, resolveCallingUserIdEnforcingPermissionsLocked(paramInt1), paramString2, Binder.getCallingUid())) {
      return false;
    }
    localObject = this.mLock;
    switch (paramInt2)
    {
    default: 
      return false;
    }
    try
    {
      paramBoolean = this.mSettingsRegistry.insertSettingLocked(0, 0, paramString1, paramString2, getCallingPackage(), paramBoolean);
      return paramBoolean;
    }
    finally {}
    paramBoolean = this.mSettingsRegistry.deleteSettingLocked(0, 0, paramString1, paramBoolean);
    return paramBoolean;
    paramBoolean = this.mSettingsRegistry.updateSettingLocked(0, 0, paramString1, paramString2, getCallingPackage(), paramBoolean);
    return paramBoolean;
  }
  
  private boolean mutateSecureSetting(String paramString1, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    enforceWritePermission("android.permission.WRITE_SECURE_SETTINGS");
    paramInt1 = resolveCallingUserIdEnforcingPermissionsLocked(paramInt1);
    if (isGlobalOrSecureSettingRestrictedForUser(paramString1, paramInt1, paramString2, Binder.getCallingUid())) {
      return false;
    }
    int i = resolveOwningUserIdForSecureSettingLocked(paramInt1, paramString1);
    if (i != paramInt1) {
      return false;
    }
    if ("location_providers_allowed".equals(paramString1)) {
      return updateLocationProvidersAllowedLocked(paramString2, i, paramBoolean);
    }
    localObject = this.mLock;
    switch (paramInt2)
    {
    default: 
      return false;
    }
    try
    {
      paramBoolean = this.mSettingsRegistry.insertSettingLocked(2, i, paramString1, paramString2, getCallingPackage(), paramBoolean);
      return paramBoolean;
    }
    finally {}
    paramBoolean = this.mSettingsRegistry.deleteSettingLocked(2, i, paramString1, paramBoolean);
    return paramBoolean;
    paramBoolean = this.mSettingsRegistry.updateSettingLocked(2, i, paramString1, paramString2, getCallingPackage(), paramBoolean);
    return paramBoolean;
  }
  
  private boolean mutateSystemSetting(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    if ((!hasWriteSecureSettingsPermission()) && (!Settings.checkAndNoteWriteSettingsOperation(getContext(), Binder.getCallingUid(), getCallingPackage(), true))) {
      return false;
    }
    paramInt1 = resolveCallingUserIdEnforcingPermissionsLocked(paramInt1);
    enforceRestrictedSystemSettingsMutationForCallingPackage(paramInt2, paramString1, paramInt1);
    int i = resolveOwningUserIdForSystemSettingLocked(paramInt1, paramString1);
    if (i != paramInt1) {
      return false;
    }
    localObject = null;
    if ("ringtone".equals(paramString1)) {
      localObject = "ringtone_cache";
    }
    for (;;)
    {
      if (localObject != null) {
        new File(getRingtoneCacheDir(UserHandle.getCallingUserId()), (String)localObject).delete();
      }
      localObject = this.mLock;
      switch (paramInt2)
      {
      default: 
        return false;
        if ("notification_sound".equals(paramString1)) {
          localObject = "notification_sound_cache";
        } else if ("alarm_alert".equals(paramString1)) {
          localObject = "alarm_alert_cache";
        }
        break;
      }
    }
    try
    {
      validateSystemSettingValue(paramString1, paramString2);
      bool = this.mSettingsRegistry.insertSettingLocked(1, i, paramString1, paramString2, getCallingPackage(), false);
      return bool;
    }
    finally {}
    boolean bool = this.mSettingsRegistry.deleteSettingLocked(1, i, paramString1, false);
    return bool;
    validateSystemSettingValue(paramString1, paramString2);
    bool = this.mSettingsRegistry.updateSettingLocked(1, i, paramString1, paramString2, getCallingPackage(), false);
    return bool;
  }
  
  private static String[] normalizeProjection(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null) {
      return ALL_COLUMNS;
    }
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if (!ArrayUtils.contains(ALL_COLUMNS, str)) {
        throw new IllegalArgumentException("Invalid column: " + str);
      }
      i += 1;
    }
    return paramArrayOfString;
  }
  
  private static MatrixCursor packageSettingForQuery(SettingsState.Setting paramSetting, String[] paramArrayOfString)
  {
    if (paramSetting.isNull()) {
      return new MatrixCursor(paramArrayOfString, 0);
    }
    paramArrayOfString = new MatrixCursor(paramArrayOfString, 1);
    appendSettingToCursor(paramArrayOfString, paramSetting);
    return paramArrayOfString;
  }
  
  private Bundle packageValueForCallResult(SettingsState.Setting paramSetting, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      if (paramSetting.isNull()) {
        return NULL_SETTING_BUNDLE;
      }
      return Bundle.forPair("value", paramSetting.getValue());
    }
    Bundle localBundle = new Bundle();
    if (!paramSetting.isNull()) {}
    for (String str = paramSetting.getValue();; str = null)
    {
      localBundle.putString("value", str);
      SettingsRegistry.-get1(this.mSettingsRegistry).addGenerationData(localBundle, paramSetting.getkey());
      return localBundle;
    }
  }
  
  private void registerBroadcastReceivers()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.USER_REMOVED");
    localIntentFilter.addAction("android.intent.action.USER_STOPPED");
    getContext().registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context arg1, Intent paramAnonymousIntent)
      {
        int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 0);
        ??? = paramAnonymousIntent.getAction();
        if (???.equals("android.intent.action.USER_REMOVED")) {}
        synchronized (SettingsProvider.-get4(SettingsProvider.this))
        {
          SettingsProvider.-get5(SettingsProvider.this).removeUserStateLocked(i, true);
          do
          {
            return;
          } while (!???.equals("android.intent.action.USER_STOPPED"));
        }
      }
    }, localIntentFilter);
    new PackageMonitor()
    {
      public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
      {
        synchronized (SettingsProvider.-get4(SettingsProvider.this))
        {
          SettingsProvider.-get5(SettingsProvider.this).onPackageRemovedLocked(paramAnonymousString, UserHandle.getUserId(paramAnonymousInt));
          return;
        }
      }
    }.register(getContext(), BackgroundThread.getHandler().getLooper(), UserHandle.ALL, true);
  }
  
  private static int resolveCallingUserIdEnforcingPermissionsLocked(int paramInt)
  {
    if (paramInt == UserHandle.getCallingUserId()) {
      return paramInt;
    }
    return ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt, false, true, "get/set setting for user", null);
  }
  
  private int resolveOwningUserIdForSecureSettingLocked(int paramInt, String paramString)
  {
    return resolveOwningUserIdLocked(paramInt, sSecureCloneToManagedSettings, paramString);
  }
  
  private int resolveOwningUserIdForSystemSettingLocked(int paramInt, String paramString)
  {
    return resolveOwningUserIdLocked(paramInt, sSystemCloneToManagedSettings, paramString);
  }
  
  private int resolveOwningUserIdLocked(int paramInt, Set<String> paramSet, String paramString)
  {
    int i = getGroupParentLocked(paramInt);
    if ((i != paramInt) && (paramSet.contains(paramString))) {
      return i;
    }
    return paramInt;
  }
  
  private void restoreAllLoseData()
  {
    try
    {
      File localFile1 = new File("data/system/users/0/settings_global.xml");
      File localFile2 = new File("data/system/users/0/settings_global.xml.bak");
      Slog.i("SettingsProvider", "SettingsProvider start restoreAllLoseData");
      if ((!localFile1.exists()) && (localFile2.exists()))
      {
        localFile2.renameTo(localFile1);
        Slog.i("SettingsProvider", "SettingsProvider restore settings_global Data");
      }
      localFile1 = new File("data/system/users/0/settings_secure.xml");
      localFile2 = new File("data/system/users/0/settings_secure.xml.bak");
      if ((!localFile1.exists()) && (localFile2.exists()))
      {
        localFile2.renameTo(localFile1);
        Slog.i("SettingsProvider", "SettingsProvider restore settings_secure Data");
      }
      localFile1 = new File("data/system/users/0/settings_system.xml");
      localFile2 = new File("data/system/users/0/settings_system.xml.bak");
      if ((!localFile1.exists()) && (localFile2.exists()))
      {
        localFile2.renameTo(localFile1);
        Slog.i("SettingsProvider", "SettingsProvider restore settings_system Data");
      }
      return;
    }
    catch (Exception localException)
    {
      Slog.i("SettingsProvider", "SettingsProvider restore file data exception:");
      localException.printStackTrace();
    }
  }
  
  private void startWatchingUserRestrictionChanges()
  {
    ((UserManagerInternal)LocalServices.getService(UserManagerInternal.class)).addUserRestrictionsListener(new -void_startWatchingUserRestrictionChanges__LambdaImpl0());
  }
  
  private static String toDumpString(String paramString)
  {
    if (paramString != null) {
      return paramString;
    }
    return "{null}";
  }
  
  private boolean updateGlobalSetting(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "updateGlobalSetting(" + paramString1 + ", " + paramString2 + ")");
    }
    return mutateGlobalSetting(paramString1, paramString2, paramInt, 3, paramBoolean);
  }
  
  private boolean updateLocationProvidersAllowedLocked(String paramString, int paramInt, boolean paramBoolean)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    int k = paramString.charAt(0);
    if ((k != 43) && (k != 45))
    {
      if (paramBoolean)
      {
        paramInt = makeKey(2, paramInt);
        SettingsRegistry.-wrap1(this.mSettingsRegistry, paramInt, "location_providers_allowed");
      }
      return false;
    }
    paramString = paramString.substring(1);
    Object localObject = getSecureSetting("location_providers_allowed", paramInt);
    int i;
    int m;
    int j;
    if (localObject != null)
    {
      localObject = ((SettingsState.Setting)localObject).getValue();
      i = ((String)localObject).indexOf(paramString);
      m = i + paramString.length();
      j = i;
      if (i > 0)
      {
        j = i;
        if (((String)localObject).charAt(i - 1) != ',') {
          j = -1;
        }
      }
      i = j;
      if (m < ((String)localObject).length())
      {
        i = j;
        if (((String)localObject).charAt(m) != ',') {
          i = -1;
        }
      }
      if ((k != 43) || (i >= 0)) {
        break label236;
      }
      if (((String)localObject).length() != 0) {
        break label208;
      }
    }
    for (;;)
    {
      return this.mSettingsRegistry.insertSettingLocked(2, paramInt, "location_providers_allowed", paramString, getCallingPackage(), paramBoolean);
      localObject = "";
      break;
      label208:
      paramString = (String)localObject + ',' + paramString;
    }
    label236:
    if ((k == 45) && (i >= 0))
    {
      if (i > 0)
      {
        k = i - 1;
        j = m;
      }
      for (;;)
      {
        String str = ((String)localObject).substring(0, k);
        paramString = str;
        if (j >= ((String)localObject).length()) {
          break;
        }
        paramString = str + ((String)localObject).substring(j);
        break;
        j = m;
        k = i;
        if (m < ((String)localObject).length())
        {
          j = m + 1;
          k = i;
        }
      }
    }
    if (paramBoolean)
    {
      paramInt = makeKey(2, paramInt);
      SettingsRegistry.-wrap1(this.mSettingsRegistry, paramInt, "location_providers_allowed");
    }
    return false;
  }
  
  private boolean updateSecureSetting(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "updateSecureSetting(" + paramString1 + ", " + paramString2 + ", " + paramInt + ")");
    }
    return mutateSecureSetting(paramString1, paramString2, paramInt, 3, paramBoolean);
  }
  
  private boolean updateSystemSetting(String paramString1, String paramString2, int paramInt)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "updateSystemSetting(" + paramString1 + ", " + paramString2 + ", " + paramInt + ")");
    }
    return mutateSystemSetting(paramString1, paramString2, paramInt, 3);
  }
  
  private void validateSystemSettingValue(String paramString1, String paramString2)
  {
    Settings.System.Validator localValidator = (Settings.System.Validator)Settings.System.VALIDATORS.get(paramString1);
    if ((localValidator == null) || (localValidator.validate(paramString2))) {
      return;
    }
    throw new IllegalArgumentException("Invalid value: " + paramString2 + " for setting: " + paramString1);
  }
  
  private static void warnOrThrowForUndesiredSecureSettingsMutationForTargetSdk(int paramInt, String paramString)
  {
    if (paramInt <= 22)
    {
      if (Settings.System.PRIVATE_SETTINGS.contains(paramString))
      {
        Slog.w("SettingsProvider", "You shouldn't not change private system settings. This will soon become an error.");
        return;
      }
      Slog.w("SettingsProvider", "You shouldn't keep your settings in the secure settings. This will soon become an error.");
      return;
    }
    if (Settings.System.PRIVATE_SETTINGS.contains(paramString)) {
      throw new IllegalArgumentException("You cannot change private secure settings.");
    }
    throw new IllegalArgumentException("You cannot keep your settings in the secure settings.");
  }
  
  public int bulkInsert(Uri paramUri, ContentValues[] paramArrayOfContentValues)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "bulkInsert() for user: " + UserHandle.getCallingUserId());
    }
    int j = 0;
    int m = paramArrayOfContentValues.length;
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (insert(paramUri, paramArrayOfContentValues[i]) != null) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public Bundle call(String paramString1, String paramString2, Bundle paramBundle)
  {
    int i = getRequestingUserId(paramBundle);
    if (paramString1.equals("GET_global")) {
      return packageValueForCallResult(getGlobalSetting(paramString2), isTrackingGeneration(paramBundle));
    }
    if (paramString1.equals("GET_secure")) {
      return packageValueForCallResult(getSecureSetting(paramString2, i), isTrackingGeneration(paramBundle));
    }
    if (paramString1.equals("GET_system")) {
      return packageValueForCallResult(getSystemSetting(paramString2, i), isTrackingGeneration(paramBundle));
    }
    if (paramString1.equals("PUT_global")) {
      insertGlobalSetting(paramString2, getSettingValue(paramBundle), i, false);
    }
    for (;;)
    {
      return null;
      if (paramString1.equals("PUT_secure")) {
        insertSecureSetting(paramString2, getSettingValue(paramBundle), i, false);
      } else if (paramString1.equals("PUT_system")) {
        insertSystemSetting(paramString2, getSettingValue(paramBundle), i);
      } else {
        Slog.w("SettingsProvider", "call() with invalid method: " + paramString1);
      }
    }
  }
  
  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "delete() for user: " + UserHandle.getCallingUserId());
    }
    paramString = new Arguments(paramUri, paramString, paramArrayOfString, false);
    if (REMOVED_LEGACY_TABLES.contains(paramString.table)) {
      return 0;
    }
    if (!isKeyValid(paramString.name)) {
      return 0;
    }
    paramArrayOfString = paramString.table;
    int i;
    if (paramArrayOfString.equals("global"))
    {
      i = UserHandle.getCallingUserId();
      if (deleteGlobalSetting(paramString.name, i, false)) {
        return 1;
      }
    }
    else
    {
      if (paramArrayOfString.equals("secure"))
      {
        i = UserHandle.getCallingUserId();
        if (!deleteSecureSetting(paramString.name, i, false)) {
          break label174;
        }
        return 1;
      }
      if (!paramArrayOfString.equals("system")) {
        break label178;
      }
      i = UserHandle.getCallingUserId();
      if (!deleteSystemSetting(paramString.name, i)) {
        break label176;
      }
      return 1;
    }
    return 0;
    label174:
    return 0;
    label176:
    return 0;
    label178:
    throw new IllegalArgumentException("Bad Uri path:" + paramUri);
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    synchronized (this.mLock)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        paramArrayOfString = this.mUserManager.getUsers(true);
        int j = paramArrayOfString.size();
        int i = 0;
        while (i < j)
        {
          dumpForUserLocked(((UserInfo)paramArrayOfString.get(i)).id, paramPrintWriter);
          i += 1;
        }
        Binder.restoreCallingIdentity(l);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  public String getType(Uri paramUri)
  {
    paramUri = new Arguments(paramUri, null, null, true);
    if (TextUtils.isEmpty(paramUri.name)) {
      return "vnd.android.cursor.dir/" + paramUri.table;
    }
    return "vnd.android.cursor.item/" + paramUri.table;
  }
  
  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "insert() for user: " + UserHandle.getCallingUserId());
    }
    String str1 = getValidTableOrThrow(paramUri);
    if (REMOVED_LEGACY_TABLES.contains(str1)) {
      return null;
    }
    String str2 = paramContentValues.getAsString("name");
    if (!isKeyValid(str2)) {
      return null;
    }
    paramContentValues = paramContentValues.getAsString("value");
    if (str1.equals("global"))
    {
      if (insertGlobalSetting(str2, paramContentValues, UserHandle.getCallingUserId(), false)) {
        return Uri.withAppendedPath(Settings.Global.CONTENT_URI, str2);
      }
    }
    else if (str1.equals("secure"))
    {
      if (insertSecureSetting(str2, paramContentValues, UserHandle.getCallingUserId(), false)) {
        return Uri.withAppendedPath(Settings.Secure.CONTENT_URI, str2);
      }
    }
    else if (str1.equals("system"))
    {
      if (insertSystemSetting(str2, paramContentValues, UserHandle.getCallingUserId())) {
        return Uri.withAppendedPath(Settings.System.CONTENT_URI, str2);
      }
    }
    else {
      throw new IllegalArgumentException("Bad Uri path:" + paramUri);
    }
    return null;
  }
  
  public boolean onCreate()
  {
    synchronized (this.mLock)
    {
      restoreAllLoseData();
      this.mUserManager = UserManager.get(getContext());
      this.mPackageManager = AppGlobals.getPackageManager();
      this.mHandlerThread = new HandlerThread("SettingsProvider", 10);
      this.mHandlerThread.start();
      this.mSettingsRegistry = new SettingsRegistry();
      registerBroadcastReceivers();
      startWatchingUserRestrictionChanges();
      return true;
    }
  }
  
  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    if (Settings.System.RINGTONE_CACHE_URI.equals(paramUri)) {
      paramUri = "ringtone_cache";
    }
    for (;;)
    {
      return ParcelFileDescriptor.open(new File(getRingtoneCacheDir(UserHandle.getCallingUserId()), paramUri), ParcelFileDescriptor.parseMode(paramString));
      if (Settings.System.NOTIFICATION_SOUND_CACHE_URI.equals(paramUri))
      {
        paramUri = "notification_sound_cache";
      }
      else if (Settings.System.ALARM_ALERT_CACHE_URI.equals(paramUri))
      {
        paramUri = "alarm_alert_cache";
      }
      else
      {
        if (!Settings.System.MMS_NOTIFICATION_CACHE_URI.equals(paramUri)) {
          break;
        }
        paramUri = "mms_notification_cache";
      }
    }
    throw new FileNotFoundException("Direct file access no longer supported; ringtone playback is available through android.media.Ringtone");
  }
  
  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "query() for user: " + UserHandle.getCallingUserId());
    }
    paramString1 = new Arguments(paramUri, paramString1, paramArrayOfString2, true);
    paramArrayOfString2 = normalizeProjection(paramArrayOfString1);
    if (REMOVED_LEGACY_TABLES.contains(paramString1.table)) {
      return new MatrixCursor(paramArrayOfString2, 0);
    }
    paramString2 = paramString1.table;
    int i;
    if (paramString2.equals("global"))
    {
      if (paramString1.name != null) {
        return packageSettingForQuery(getGlobalSetting(paramString1.name), paramArrayOfString2);
      }
    }
    else
    {
      if (paramString2.equals("secure"))
      {
        i = UserHandle.getCallingUserId();
        if (paramString1.name == null) {
          break label201;
        }
        return packageSettingForQuery(getSecureSetting(paramString1.name, i), paramArrayOfString2);
      }
      if (!paramString2.equals("system")) {
        break label217;
      }
      i = UserHandle.getCallingUserId();
      if (paramString1.name == null) {
        break label209;
      }
      return packageSettingForQuery(getSystemSetting(paramString1.name, i), paramArrayOfString2);
    }
    return getAllGlobalSettings(paramArrayOfString1);
    label201:
    return getAllSecureSettings(i, paramArrayOfString1);
    label209:
    return getAllSystemSettings(i, paramArrayOfString1);
    label217:
    throw new IllegalArgumentException("Invalid Uri path:" + paramUri);
  }
  
  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    if (DEBUG) {
      Slog.v("SettingsProvider", "update() for user: " + UserHandle.getCallingUserId());
    }
    paramString = new Arguments(paramUri, paramString, paramArrayOfString, false);
    if (REMOVED_LEGACY_TABLES.contains(paramString.table)) {
      return 0;
    }
    if (!isKeyValid(paramContentValues.getAsString("name"))) {
      return 0;
    }
    paramContentValues = paramContentValues.getAsString("value");
    paramArrayOfString = paramString.table;
    int i;
    if (paramArrayOfString.equals("global"))
    {
      i = UserHandle.getCallingUserId();
      if (updateGlobalSetting(paramString.name, paramContentValues, i, false)) {
        return 1;
      }
    }
    else
    {
      if (paramArrayOfString.equals("secure"))
      {
        i = UserHandle.getCallingUserId();
        if (!updateSecureSetting(paramString.name, paramContentValues, i, false)) {
          break label191;
        }
        return 1;
      }
      if (!paramArrayOfString.equals("system")) {
        break label195;
      }
      i = UserHandle.getCallingUserId();
      if (!updateSystemSetting(paramString.name, paramContentValues, i)) {
        break label193;
      }
      return 1;
    }
    return 0;
    label191:
    return 0;
    label193:
    return 0;
    label195:
    throw new IllegalArgumentException("Invalid Uri path:" + paramUri);
  }
  
  private static final class Arguments
  {
    private static final Pattern WHERE_PATTERN_NO_PARAM_IN_BRACKETS = Pattern.compile("[\\s]*\\([\\s]*name[\\s]*=[\\s]*['\"].*['\"][\\s]*\\)[\\s]*");
    private static final Pattern WHERE_PATTERN_NO_PARAM_NO_BRACKETS = Pattern.compile("[\\s]*name[\\s]*=[\\s]*['\"].*['\"][\\s]*");
    private static final Pattern WHERE_PATTERN_WITH_PARAM_IN_BRACKETS;
    private static final Pattern WHERE_PATTERN_WITH_PARAM_NO_BRACKETS = Pattern.compile("[\\s]*name[\\s]*=[\\s]*\\?[\\s]*");
    public final String name;
    public final String table;
    
    static
    {
      WHERE_PATTERN_WITH_PARAM_IN_BRACKETS = Pattern.compile("[\\s]*\\([\\s]*name[\\s]*=[\\s]*\\?[\\s]*\\)[\\s]*");
    }
    
    public Arguments(Uri paramUri, String paramString, String[] paramArrayOfString, boolean paramBoolean)
    {
      switch (paramUri.getPathSegments().size())
      {
      }
      do
      {
        do
        {
          EventLogTags.writeUnsupportedSettingsQuery(paramUri.toSafeString(), paramString, Arrays.toString(paramArrayOfString));
          throw new IllegalArgumentException(String.format("Supported SQL:\n  uri content://some_table/some_property with null where and where args\n  uri content://some_table with query name=? and single name as arg\n  uri content://some_table with query name=some_name and null args\n  but got - uri:%1s, where:%2s whereArgs:%3s", new Object[] { paramUri, paramString, Arrays.toString(paramArrayOfString) }));
          if ((paramString != null) && ((WHERE_PATTERN_WITH_PARAM_NO_BRACKETS.matcher(paramString).matches()) || (WHERE_PATTERN_WITH_PARAM_IN_BRACKETS.matcher(paramString).matches())) && (paramArrayOfString.length == 1))
          {
            this.name = paramArrayOfString[0];
            this.table = computeTableForSetting(paramUri, this.name);
            return;
          }
          if ((paramString != null) && ((WHERE_PATTERN_NO_PARAM_NO_BRACKETS.matcher(paramString).matches()) || (WHERE_PATTERN_NO_PARAM_IN_BRACKETS.matcher(paramString).matches())))
          {
            this.name = paramString.substring(Math.max(paramString.indexOf("'"), paramString.indexOf("\"")) + 1, Math.max(paramString.lastIndexOf("'"), paramString.lastIndexOf("\"")));
            this.table = computeTableForSetting(paramUri, this.name);
            return;
          }
        } while ((!paramBoolean) || (paramString != null) || (paramArrayOfString != null));
        this.name = null;
        this.table = computeTableForSetting(paramUri, null);
        return;
      } while ((paramString != null) || (paramArrayOfString != null));
      this.name = ((String)paramUri.getPathSegments().get(1));
      this.table = computeTableForSetting(paramUri, this.name);
    }
    
    private static String computeTableForSetting(Uri paramUri, String paramString)
    {
      String str = SettingsProvider.-wrap0(paramUri);
      paramUri = str;
      if (paramString != null)
      {
        paramUri = str;
        if (SettingsProvider.sSystemMovedToSecureSettings.contains(paramString)) {
          paramUri = "secure";
        }
        if (SettingsProvider.sSystemMovedToGlobalSettings.contains(paramString)) {
          paramUri = "global";
        }
        if (SettingsProvider.sSecureMovedToGlobalSettings.contains(paramString)) {
          paramUri = "global";
        }
        if (SettingsProvider.sGlobalMovedToSecureSettings.contains(paramString)) {
          paramUri = "secure";
        }
      }
      return paramUri;
    }
  }
  
  final class SettingsRegistry
  {
    private final BackupManager mBackupManager = new BackupManager(SettingsProvider.this.getContext());
    private GenerationRegistry mGenerationRegistry = new GenerationRegistry(SettingsProvider.-get4(SettingsProvider.this));
    private final Handler mHandler = new MyHandler(SettingsProvider.this.getContext().getMainLooper());
    private final SparseArray<SettingsState> mSettingsStates = new SparseArray();
    
    public SettingsRegistry()
    {
      migrateAllLegacySettingsIfNeeded();
    }
    
    private void ensureSecureSettingAndroidIdSetLocked(SettingsState paramSettingsState)
    {
      if (!paramSettingsState.getSettingLocked("android_id").isNull()) {
        return;
      }
      int i;
      UserInfo localUserInfo;
      synchronized (SettingsProvider.-get2(SettingsProvider.this))
      {
        i = SettingsProvider.getUserIdFromKey(paramSettingsState.mKey);
        long l = Binder.clearCallingIdentity();
        try
        {
          localUserInfo = SettingsProvider.-get6(SettingsProvider.this).getUserInfo(i);
          Binder.restoreCallingIdentity(l);
          if (localUserInfo == null) {
            return;
          }
        }
        finally
        {
          paramSettingsState = finally;
          Binder.restoreCallingIdentity(l);
          throw paramSettingsState;
        }
      }
      String str = Long.toHexString(new SecureRandom().nextLong());
      paramSettingsState.insertSettingLocked("android_id", str, "android");
      Slog.d("SettingsProvider", "Generated and saved new ANDROID_ID [" + str + "] for user " + i);
      if (localUserInfo.isRestricted())
      {
        paramSettingsState = (DropBoxManager)SettingsProvider.this.getContext().getSystemService("dropbox");
        if ((paramSettingsState != null) && (paramSettingsState.isTagEnabled("restricted_profile_ssaid"))) {
          paramSettingsState.addText("restricted_profile_ssaid", System.currentTimeMillis() + "," + "restricted_profile_ssaid" + "," + str + "\n");
        }
      }
    }
    
    private void ensureSettingsStateLocked(int paramInt)
    {
      if (this.mSettingsStates.get(paramInt) == null)
      {
        int i = getMaxBytesPerPackageForType(SettingsProvider.getTypeFromKey(paramInt));
        SettingsState localSettingsState = new SettingsState(SettingsProvider.-get4(SettingsProvider.this), getSettingsFile(paramInt), paramInt, i, SettingsProvider.-get3(SettingsProvider.this).getLooper());
        this.mSettingsStates.put(paramInt, localSettingsState);
      }
    }
    
    private int getMaxBytesPerPackageForType(int paramInt)
    {
      switch (paramInt)
      {
      case 1: 
      default: 
        return 20000;
      }
      return -1;
    }
    
    private Uri getNotificationUriFor(int paramInt, String paramString)
    {
      if (isGlobalSettingsKey(paramInt))
      {
        if (paramString != null) {
          return Uri.withAppendedPath(Settings.Global.CONTENT_URI, paramString);
        }
        return Settings.Global.CONTENT_URI;
      }
      if (isSecureSettingsKey(paramInt))
      {
        if (paramString != null) {
          return Uri.withAppendedPath(Settings.Secure.CONTENT_URI, paramString);
        }
        return Settings.Secure.CONTENT_URI;
      }
      if (isSystemSettingsKey(paramInt))
      {
        if (paramString != null) {
          return Uri.withAppendedPath(Settings.System.CONTENT_URI, paramString);
        }
        return Settings.System.CONTENT_URI;
      }
      throw new IllegalArgumentException("Invalid settings key:" + paramInt);
    }
    
    private File getSettingsFile(int paramInt)
    {
      if (isGlobalSettingsKey(paramInt)) {
        return new File(Environment.getUserSystemDirectory(SettingsProvider.getUserIdFromKey(paramInt)), "settings_global.xml");
      }
      if (isSystemSettingsKey(paramInt)) {
        return new File(Environment.getUserSystemDirectory(SettingsProvider.getUserIdFromKey(paramInt)), "settings_system.xml");
      }
      if (isSecureSettingsKey(paramInt)) {
        return new File(Environment.getUserSystemDirectory(SettingsProvider.getUserIdFromKey(paramInt)), "settings_secure.xml");
      }
      throw new IllegalArgumentException("Invalid settings key:" + paramInt);
    }
    
    private boolean isGlobalSettingsKey(int paramInt)
    {
      boolean bool = false;
      if (SettingsProvider.getTypeFromKey(paramInt) == 0) {
        bool = true;
      }
      return bool;
    }
    
    private boolean isSecureSettingsKey(int paramInt)
    {
      return SettingsProvider.getTypeFromKey(paramInt) == 2;
    }
    
    private boolean isSystemSettingsKey(int paramInt)
    {
      return SettingsProvider.getTypeFromKey(paramInt) == 1;
    }
    
    private void maybeNotifyProfiles(int paramInt1, int paramInt2, Uri paramUri, String paramString, Set<String> paramSet)
    {
      if (paramSet.contains(paramString))
      {
        paramString = SettingsProvider.-get6(SettingsProvider.this).getProfileIdsWithDisabled(paramInt2);
        int j = paramString.length;
        int i = 0;
        while (i < j)
        {
          int k = paramString[i];
          if (k != paramInt2)
          {
            this.mHandler.obtainMessage(1, k, 0, paramUri).sendToTarget();
            k = SettingsProvider.makeKey(paramInt1, k);
            this.mGenerationRegistry.incrementGeneration(k);
            this.mHandler.obtainMessage(2).sendToTarget();
          }
          i += 1;
        }
      }
    }
    
    private void migrateAllLegacySettingsIfNeeded()
    {
      synchronized (SettingsProvider.-get4(SettingsProvider.this))
      {
        boolean bool = getSettingsFile(SettingsProvider.makeKey(0, 0)).exists();
        if (bool) {
          return;
        }
        long l = Binder.clearCallingIdentity();
        try
        {
          List localList = SettingsProvider.-get6(SettingsProvider.this).getUsers(true);
          int j = localList.size();
          int i = 0;
          while (i < j)
          {
            int k = ((UserInfo)localList.get(i)).id;
            DatabaseHelper localDatabaseHelper = new DatabaseHelper(SettingsProvider.this.getContext(), k);
            migrateLegacySettingsForUserLocked(localDatabaseHelper, localDatabaseHelper.getWritableDatabase(), k);
            new UpgradeController(k).upgradeIfNeededLocked();
            if (!SettingsProvider.-get6(SettingsProvider.this).isUserRunning(new UserHandle(k))) {
              removeUserStateLocked(k, false);
            }
            i += 1;
          }
          Binder.restoreCallingIdentity(l);
          return;
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
      }
    }
    
    private void migrateLegacySettingsForUserIfNeededLocked(int paramInt)
    {
      if (getSettingsFile(SettingsProvider.makeKey(2, paramInt)).exists()) {
        return;
      }
      DatabaseHelper localDatabaseHelper = new DatabaseHelper(SettingsProvider.this.getContext(), paramInt);
      migrateLegacySettingsForUserLocked(localDatabaseHelper, localDatabaseHelper.getWritableDatabase(), paramInt);
    }
    
    private void migrateLegacySettingsForUserLocked(DatabaseHelper paramDatabaseHelper, SQLiteDatabase paramSQLiteDatabase, int paramInt)
    {
      int i = SettingsProvider.makeKey(1, paramInt);
      ensureSettingsStateLocked(i);
      SettingsState localSettingsState = (SettingsState)this.mSettingsStates.get(i);
      migrateLegacySettingsLocked(localSettingsState, paramSQLiteDatabase, "system");
      localSettingsState.persistSyncLocked();
      i = SettingsProvider.makeKey(2, paramInt);
      ensureSettingsStateLocked(i);
      localSettingsState = (SettingsState)this.mSettingsStates.get(i);
      migrateLegacySettingsLocked(localSettingsState, paramSQLiteDatabase, "secure");
      ensureSecureSettingAndroidIdSetLocked(localSettingsState);
      localSettingsState.persistSyncLocked();
      if (paramInt == 0)
      {
        paramInt = SettingsProvider.makeKey(0, paramInt);
        ensureSettingsStateLocked(paramInt);
        localSettingsState = (SettingsState)this.mSettingsStates.get(paramInt);
        migrateLegacySettingsLocked(localSettingsState, paramSQLiteDatabase, "global");
        localSettingsState.persistSyncLocked();
      }
      paramDatabaseHelper.dropDatabase();
    }
    
    private void migrateLegacySettingsLocked(SettingsState paramSettingsState, SQLiteDatabase paramSQLiteDatabase, String paramString)
    {
      SQLiteQueryBuilder localSQLiteQueryBuilder = new SQLiteQueryBuilder();
      localSQLiteQueryBuilder.setTables(paramString);
      paramString = localSQLiteQueryBuilder.query(paramSQLiteDatabase, SettingsProvider.-get0(), null, null, null, null, null);
      if (paramString == null) {
        return;
      }
      try
      {
        boolean bool = paramString.moveToFirst();
        if (!bool) {
          return;
        }
        int i = paramString.getColumnIndex("name");
        int j = paramString.getColumnIndex("value");
        paramSettingsState.setVersionLocked(paramSQLiteDatabase.getVersion());
        while (!paramString.isAfterLast())
        {
          paramSettingsState.insertSettingLocked(paramString.getString(i), paramString.getString(j), "android");
          paramString.moveToNext();
        }
      }
      finally
      {
        paramString.close();
      }
    }
    
    private void notifyForSettingsChange(int paramInt, String paramString)
    {
      int i = SettingsProvider.getUserIdFromKey(paramInt);
      Uri localUri = getNotificationUriFor(paramInt, paramString);
      this.mGenerationRegistry.incrementGeneration(paramInt);
      this.mHandler.obtainMessage(1, i, 0, localUri).sendToTarget();
      if (isSecureSettingsKey(paramInt)) {
        maybeNotifyProfiles(SettingsProvider.getTypeFromKey(paramInt), i, localUri, paramString, SettingsProvider.-get7());
      }
      for (;;)
      {
        this.mHandler.obtainMessage(2).sendToTarget();
        return;
        if (isSystemSettingsKey(paramInt)) {
          maybeNotifyProfiles(SettingsProvider.getTypeFromKey(paramInt), i, localUri, paramString, SettingsProvider.-get8());
        }
      }
    }
    
    private SettingsState peekSettingsStateLocked(int paramInt)
    {
      SettingsState localSettingsState = (SettingsState)this.mSettingsStates.get(paramInt);
      if (localSettingsState != null) {
        return localSettingsState;
      }
      ensureSettingsForUserLocked(SettingsProvider.getUserIdFromKey(paramInt));
      return (SettingsState)this.mSettingsStates.get(paramInt);
    }
    
    public boolean deleteSettingLocked(int paramInt1, int paramInt2, String paramString, boolean paramBoolean)
    {
      paramInt1 = SettingsProvider.makeKey(paramInt1, paramInt2);
      boolean bool = peekSettingsStateLocked(paramInt1).deleteSettingLocked(paramString);
      if ((paramBoolean) || (bool)) {
        notifyForSettingsChange(paramInt1, paramString);
      }
      return bool;
    }
    
    public void ensureSettingsForUserLocked(int paramInt)
    {
      migrateLegacySettingsForUserIfNeededLocked(paramInt);
      if (paramInt == 0) {
        ensureSettingsStateLocked(SettingsProvider.makeKey(0, 0));
      }
      ensureSettingsStateLocked(SettingsProvider.makeKey(2, paramInt));
      ensureSecureSettingAndroidIdSetLocked(getSettingsLocked(2, paramInt));
      ensureSettingsStateLocked(SettingsProvider.makeKey(1, paramInt));
      new UpgradeController(paramInt).upgradeIfNeededLocked();
    }
    
    public SettingsState.Setting getSettingLocked(int paramInt1, int paramInt2, String paramString)
    {
      return peekSettingsStateLocked(SettingsProvider.makeKey(paramInt1, paramInt2)).getSettingLocked(paramString);
    }
    
    public SettingsState getSettingsLocked(int paramInt1, int paramInt2)
    {
      return peekSettingsStateLocked(SettingsProvider.makeKey(paramInt1, paramInt2));
    }
    
    public List<String> getSettingsNamesLocked(int paramInt1, int paramInt2)
    {
      return peekSettingsStateLocked(SettingsProvider.makeKey(paramInt1, paramInt2)).getSettingNamesLocked();
    }
    
    public boolean insertSettingLocked(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    {
      paramInt1 = SettingsProvider.makeKey(paramInt1, paramInt2);
      boolean bool = peekSettingsStateLocked(paramInt1).insertSettingLocked(paramString1, paramString2, paramString3);
      if ((paramBoolean) || (bool)) {
        notifyForSettingsChange(paramInt1, paramString1);
      }
      return bool;
    }
    
    public void onPackageRemovedLocked(String paramString, int paramInt)
    {
      paramInt = SettingsProvider.makeKey(1, paramInt);
      SettingsState localSettingsState = (SettingsState)this.mSettingsStates.get(paramInt);
      if (localSettingsState != null) {
        localSettingsState.onPackageRemovedLocked(paramString);
      }
    }
    
    public void removeUserStateLocked(int paramInt, boolean paramBoolean)
    {
      final int i = SettingsProvider.makeKey(1, paramInt);
      SettingsState localSettingsState = (SettingsState)this.mSettingsStates.get(i);
      if (localSettingsState != null)
      {
        if (paramBoolean)
        {
          this.mSettingsStates.remove(i);
          localSettingsState.destroyLocked(null);
        }
      }
      else
      {
        i = SettingsProvider.makeKey(2, paramInt);
        localSettingsState = (SettingsState)this.mSettingsStates.get(i);
        if (localSettingsState != null)
        {
          if (!paramBoolean) {
            break label110;
          }
          this.mSettingsStates.remove(i);
          localSettingsState.destroyLocked(null);
        }
      }
      for (;;)
      {
        this.mGenerationRegistry.onUserRemoved(paramInt);
        return;
        localSettingsState.destroyLocked(new Runnable()
        {
          public void run()
          {
            SettingsProvider.SettingsRegistry.-get2(SettingsProvider.SettingsRegistry.this).remove(i);
          }
        });
        break;
        label110:
        localSettingsState.destroyLocked(new Runnable()
        {
          public void run()
          {
            SettingsProvider.SettingsRegistry.-get2(SettingsProvider.SettingsRegistry.this).remove(i);
          }
        });
      }
    }
    
    public boolean updateSettingLocked(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    {
      paramInt1 = SettingsProvider.makeKey(paramInt1, paramInt2);
      boolean bool = peekSettingsStateLocked(paramInt1).updateSettingLocked(paramString1, paramString2, paramString3);
      if ((paramBoolean) || (bool)) {
        notifyForSettingsChange(paramInt1, paramString1);
      }
      return bool;
    }
    
    private final class MyHandler
      extends Handler
    {
      public MyHandler(Looper paramLooper)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
        case 1: 
          int i;
          do
          {
            return;
            i = paramMessage.arg1;
            paramMessage = (Uri)paramMessage.obj;
            SettingsProvider.this.getContext().getContentResolver().notifyChange(paramMessage, null, true, i);
          } while (!SettingsProvider.-get1());
          Slog.v("SettingsProvider", "Notifying for " + i + ": " + paramMessage);
          return;
        }
        SettingsProvider.SettingsRegistry.-get0(SettingsProvider.SettingsRegistry.this).dataChanged();
      }
    }
    
    private final class UpgradeController
    {
      private final int mUserId;
      
      public UpgradeController(int paramInt)
      {
        this.mUserId = paramInt;
      }
      
      private int getColorIndex(boolean paramBoolean)
      {
        if (paramBoolean)
        {
          int i = Settings.System.getInt(SettingsProvider.this.getContext().getContentResolver(), "oem_black_mode_accent_color_index", 0);
          if (i > 7) {
            return i - 7;
          }
          return i;
        }
        return Settings.System.getInt(SettingsProvider.this.getContext().getContentResolver(), "oem_white_mode_accent_color_index", 0);
      }
      
      private SettingsState getGlobalSettingsLocked()
      {
        return SettingsProvider.SettingsRegistry.this.getSettingsLocked(0, 0);
      }
      
      private SettingsState getSecureSettingsLocked(int paramInt)
      {
        return SettingsProvider.SettingsRegistry.this.getSettingsLocked(2, paramInt);
      }
      
      private SettingsState getSystemSettingsLocked(int paramInt)
      {
        return SettingsProvider.SettingsRegistry.this.getSettingsLocked(1, paramInt);
      }
      
      private void loadCustomizedVolumeLevels(SettingsState paramSettingsState)
      {
        paramSettingsState.updateSettingLocked("volume_music", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034131)), "android");
        paramSettingsState.updateSettingLocked("volume_ring", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034130)), "android");
        paramSettingsState.updateSettingLocked("volume_voice", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034132)), "android");
        paramSettingsState.updateSettingLocked("volume_alarm", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034133)), "android");
        paramSettingsState.updateSettingLocked("volume_notification", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034134)), "android");
        paramSettingsState.insertSettingLocked("volume_music_headset", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034136)), "android");
        paramSettingsState.insertSettingLocked("volume_ring_headset", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034135)), "android");
        paramSettingsState.insertSettingLocked("volume_voice_headset", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034137)), "android");
        paramSettingsState.insertSettingLocked("volume_alarm_headset", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034138)), "android");
        paramSettingsState.insertSettingLocked("volume_notification_headset", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034139)), "android");
        paramSettingsState.insertSettingLocked("volume_music_headphone", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034136)), "android");
        paramSettingsState.insertSettingLocked("volume_ring_headphone", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034135)), "android");
        paramSettingsState.insertSettingLocked("volume_voice_headphone", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034137)), "android");
        paramSettingsState.insertSettingLocked("volume_alarm_headphone", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034138)), "android");
        paramSettingsState.insertSettingLocked("volume_notification_headphone", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034139)), "android");
        paramSettingsState.insertSettingLocked("volume_music_speaker", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034141)), "android");
        paramSettingsState.insertSettingLocked("volume_ring_speaker", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034140)), "android");
        paramSettingsState.insertSettingLocked("volume_voice_speaker", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034142)), "android");
        paramSettingsState.insertSettingLocked("volume_alarm_speaker", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034143)), "android");
        paramSettingsState.insertSettingLocked("volume_notification_speaker", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034144)), "android");
        paramSettingsState.insertSettingLocked("volume_voice_earpiece", Integer.toString(SettingsProvider.this.getContext().getResources().getInteger(2131034145)), "android");
      }
      
      private int onUpgradeLocked(int paramInt1, int paramInt2, int paramInt3)
      {
        if (SettingsProvider.-get1()) {
          Slog.w("SettingsProvider", "Upgrading settings for user: " + paramInt1 + " from version: " + paramInt2 + " to version: " + paramInt3);
        }
        int i = paramInt2;
        Object localObject1;
        if (paramInt2 == 118)
        {
          if (paramInt1 == 0)
          {
            localObject1 = getGlobalSettingsLocked();
            ((SettingsState)localObject1).updateSettingLocked("zen_mode", Integer.toString(0), "android");
            ((SettingsState)localObject1).updateSettingLocked("mode_ringer", Integer.toString(2), "android");
          }
          i = 119;
        }
        paramInt2 = i;
        Object localObject2;
        Object localObject3;
        label229:
        label563:
        label812:
        label871:
        label940:
        label1145:
        label1260:
        label1356:
        label1410:
        label1640:
        Object localObject4;
        if (i == 119)
        {
          localObject2 = getSecureSettingsLocked(paramInt1);
          if (SettingsProvider.this.getContext().getResources().getBoolean(2130968617))
          {
            localObject1 = "1";
            ((SettingsState)localObject2).insertSettingLocked("double_tap_to_wake", (String)localObject1, "android");
            paramInt2 = 120;
          }
        }
        else
        {
          i = paramInt2;
          if (paramInt2 == 120) {
            i = 121;
          }
          paramInt2 = i;
          if (i == 121)
          {
            localObject1 = getSecureSettingsLocked(paramInt1);
            localObject2 = SettingsProvider.this.getContext().getResources().getString(2131099668);
            localObject3 = ((SettingsState)localObject1).getSettingLocked("nfc_payment_default_component");
            if ((localObject2 != null) && (!((String)localObject2).isEmpty())) {
              break label2462;
            }
            if (SettingsProvider.this.getContext().getResources().getBoolean(2130968619)) {
              loadCustomizedVolumeLevels(getSystemSettingsLocked(paramInt1));
            }
            localObject2 = getSystemSettingsLocked(paramInt1);
            localObject3 = SettingsProvider.this.getContext().getResources().getString(2131099669);
            if (!TextUtils.isEmpty((CharSequence)localObject3)) {
              ((SettingsState)localObject2).insertSettingLocked("date_format", (String)localObject3, "android");
            }
            localObject3 = SettingsProvider.this.getContext().getResources().getString(2131099670);
            if (!TextUtils.isEmpty((CharSequence)localObject3)) {
              ((SettingsState)localObject2).insertSettingLocked("time_12_24", (String)localObject3, "android");
            }
            paramInt2 = SettingsProvider.this.getContext().getResources().getInteger(2131034146);
            if (paramInt2 != 0) {
              ((SettingsState)localObject1).insertSettingLocked("accessibility_enabled", String.valueOf(paramInt2), "android");
            }
            localObject2 = SettingsProvider.this.getContext().getResources().getString(2131099671);
            if (!TextUtils.isEmpty((CharSequence)localObject2)) {
              ((SettingsState)localObject1).insertSettingLocked("enabled_accessibility_services", (String)localObject2, "android");
            }
            localObject2 = SettingsProvider.this.getContext().getResources().getString(2131099672);
            if (!TextUtils.isEmpty((CharSequence)localObject2)) {
              ((SettingsState)localObject1).insertSettingLocked("default_input_method", (String)localObject2, "android");
            }
            localObject2 = SettingsProvider.this.getContext().getResources().getString(2131099673);
            if (!TextUtils.isEmpty((CharSequence)localObject2)) {
              ((SettingsState)localObject1).insertSettingLocked("enabled_input_methods", (String)localObject2, "android");
            }
            paramInt2 = 122;
          }
          i = paramInt2;
          if (paramInt2 == 122)
          {
            if (paramInt1 == 0)
            {
              localObject2 = getGlobalSettingsLocked();
              if (((SettingsState)localObject2).getSettingLocked("add_users_when_locked").isNull())
              {
                if (!SettingsProvider.this.getContext().getResources().getBoolean(2130968618)) {
                  break label2485;
                }
                localObject1 = "1";
                ((SettingsState)localObject2).insertSettingLocked("add_users_when_locked", (String)localObject1, "android");
              }
            }
            localObject1 = getSystemSettingsLocked(paramInt1);
            ((SettingsState)localObject1).insertSettingLocked("oem_allow_suspend_notification", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034205)), "android");
            ((SettingsState)localObject1).insertSettingLocked("oem_allow_led_light", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034206)), "android");
            ((SettingsState)localObject1).insertSettingLocked("oem_zen_media_switch", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034207)), "android");
            ((SettingsState)localObject1).insertSettingLocked("oem_screenshot_sound_enable", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034208)), "android");
            i = 123;
          }
          paramInt2 = i;
          if (i == 123)
          {
            getGlobalSettingsLocked().insertSettingLocked("bluetooth_disabled_profiles", SettingsProvider.this.getContext().getResources().getString(2131099650), "android");
            localObject1 = getSystemSettingsLocked(paramInt1);
            if (!OpFeatures.isSupport(new int[] { 1 })) {
              break label2493;
            }
            ((SettingsState)localObject1).insertSettingLocked("oem_clear_way", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034209)), "android");
            paramInt2 = 124;
          }
          i = paramInt2;
          if (paramInt2 == 124)
          {
            localObject2 = getSecureSettingsLocked(paramInt1);
            if (((SettingsState)localObject2).getSettingLocked("show_ime_with_hard_keyboard").isNull())
            {
              if (!SettingsProvider.this.getContext().getResources().getBoolean(2130968610)) {
                break label2529;
              }
              localObject1 = "1";
              ((SettingsState)localObject2).insertSettingLocked("show_ime_with_hard_keyboard", (String)localObject1, "android");
            }
            i = 125;
          }
          paramInt2 = i;
          if (i == 125)
          {
            localObject1 = getSecureSettingsLocked(paramInt1);
            if (((SettingsState)localObject1).getSettingLocked("enabled_vr_listeners").isNull())
            {
              localObject3 = SystemConfig.getInstance().getDefaultVrComponents();
              if ((localObject3 != null) && (!((ArraySet)localObject3).isEmpty())) {
                break label2537;
              }
            }
            paramInt2 = 126;
          }
          i = paramInt2;
          if (paramInt2 == 126)
          {
            if (SettingsProvider.-get6(SettingsProvider.this).isManagedProfile(paramInt1))
            {
              localObject1 = getSecureSettingsLocked(0);
              localObject2 = ((SettingsState)localObject1).getSettingLocked("lock_screen_show_notifications");
              if (!((SettingsState.Setting)localObject2).isNull()) {
                getSecureSettingsLocked(paramInt1).insertSettingLocked("lock_screen_show_notifications", ((SettingsState.Setting)localObject2).getValue(), "android");
              }
              localObject1 = ((SettingsState)localObject1).getSettingLocked("lock_screen_allow_private_notifications");
              if (!((SettingsState.Setting)localObject1).isNull()) {
                getSecureSettingsLocked(paramInt1).insertSettingLocked("lock_screen_allow_private_notifications", ((SettingsState.Setting)localObject1).getValue(), "android");
              }
            }
            i = 127;
          }
          paramInt2 = i;
          if (i == 127) {
            paramInt2 = 128;
          }
          i = paramInt2;
          if (paramInt2 == 128)
          {
            localObject1 = getSecureSettingsLocked(paramInt1);
            localObject3 = ((SettingsState)localObject1).getSettingLocked("enabled_notification_policy_access_packages");
            localObject2 = SettingsProvider.this.getContext().getResources().getString(17039487);
            if (!TextUtils.isEmpty((CharSequence)localObject2))
            {
              if (!((SettingsState.Setting)localObject3).isNull()) {
                break label2626;
              }
              ((SettingsState)localObject1).insertSettingLocked("enabled_notification_policy_access_packages", (String)localObject2, "android");
            }
            i = 129;
          }
          paramInt2 = i;
          if (i == 129)
          {
            localObject1 = getSecureSettingsLocked(paramInt1);
            if (TextUtils.equals("500", ((SettingsState)localObject1).getSettingLocked("long_press_timeout").getValue())) {
              ((SettingsState)localObject1).insertSettingLocked("long_press_timeout", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034123)), "android");
            }
            localObject3 = getSystemSettingsLocked(paramInt1);
            localObject2 = getGlobalSettingsLocked();
            if (!SettingsProvider.this.getContext().getResources().getBoolean(2130968624)) {
              break label2676;
            }
            localObject1 = "1";
            if (OpFeatures.isSupport(new int[] { 1 })) {
              ((SettingsState)localObject3).insertSettingLocked("oem_acc_breath_light", (String)localObject1, "android");
            }
            if (Settings.System.getInt(SettingsProvider.this.getContext().getContentResolver(), "status_bar_battery_style", 0) == 2) {
              ((SettingsState)localObject3).insertSettingLocked("status_bar_show_battery_percent", "1", "android");
            }
            if (!OpFeatures.isSupport(new int[] { 1 })) {
              break label2684;
            }
            paramInt2 = SettingsProvider.this.getContext().getResources().getInteger(2131034199);
            ((SettingsState)localObject2).insertSettingLocked("wifi_auto_change_to_mobile_data", String.valueOf(paramInt2), "android");
            if (!OpFeatures.isSupport(new int[] { 1 })) {
              break label2715;
            }
            if (!SettingsProvider.this.getContext().getResources().getBoolean(2130968634)) {
              break label2707;
            }
            localObject1 = "1";
            ((SettingsState)localObject2).insertSettingLocked("captive_portal_detection_enabled", (String)localObject1, "android");
            paramInt2 = 130;
          }
          i = paramInt2;
          if (paramInt2 == 130)
          {
            localObject1 = getSecureSettingsLocked(paramInt1);
            if ("0".equals(((SettingsState)localObject1).getSettingLocked("doze_enabled").getValue()))
            {
              ((SettingsState)localObject1).insertSettingLocked("doze_pulse_on_pick_up", "0", "android");
              ((SettingsState)localObject1).insertSettingLocked("doze_pulse_on_double_tap", "0", "android");
            }
            paramInt2 = SettingsProvider.this.getContext().getResources().getInteger(2131034127);
            getGlobalSettingsLocked().updateSettingLocked("wifi_scan_always_enabled", String.valueOf(paramInt2), "android");
            i = 131;
          }
          paramInt2 = i;
          if (i == 131)
          {
            localObject1 = getSystemSettingsLocked(paramInt1);
            if (!OpFeatures.isSupport(new int[] { 1 })) {
              break label2980;
            }
            localObject2 = SystemProperties.get("persist.sys.oxygentheme", "0");
            Slog.w("SettingsProvider", "currentVersion = " + i + "  darkmodestate = " + (String)localObject2);
            if (!"0".equals(localObject2)) {
              break label2752;
            }
            ((SettingsState)localObject1).insertSettingLocked("oem_black_mode", String.valueOf(2), "android");
            paramInt2 = 132;
          }
          i = paramInt2;
          if (paramInt2 == 132)
          {
            localObject1 = getSystemSettingsLocked(paramInt1);
            localObject2 = getGlobalSettingsLocked();
            localObject3 = new String[8];
            localObject3[0] = "#FF2196F3";
            localObject3[1] = "#FFCC6F4E";
            localObject3[2] = "#FFEB9413";
            localObject3[3] = "#FF8BC34A";
            localObject3[4] = "#FF673AB7";
            localObject3[5] = "#FF02BCD4";
            localObject3[6] = "#FFE91E63";
            localObject3[7] = "#FF9C27B0";
            localObject4 = new String[8];
            localObject4[0] = "#FF42A5F5";
            localObject4[1] = "#FFCC6F4E";
            localObject4[2] = "#FFE6A545";
            localObject4[3] = "#FF7DC22F";
            localObject4[4] = "#FF9575CD";
            localObject4[5] = "#FF26C6DA";
            localObject4[6] = "#FFF06292";
            localObject4[7] = "#FFBA68C8";
            Slog.w("SettingsProvider", "currentVersion = " + paramInt2 + "  whiteColors[getColorIndex(false)] = " + localObject3[getColorIndex(false)]);
            ((SettingsState)localObject1).updateSettingLocked("oem_white_mode_accent_color", localObject3[getColorIndex(false)], "android");
            Slog.w("SettingsProvider", "currentVersion = " + paramInt2 + " blackColors[getColorIndex(true)] = " + localObject4[getColorIndex(true)]);
            ((SettingsState)localObject1).updateSettingLocked("oem_black_mode_accent_color", localObject4[getColorIndex(true)], "android");
            if (!OpFeatures.isSupport(new int[] { 1 })) {
              ((SettingsState)localObject2).insertSettingLocked("night_mode_enabled", String.valueOf(Settings.System.getInt(SettingsProvider.this.getContext().getContentResolver(), "oem_eyecare_enable", 0)), "android");
            }
            ((SettingsState)localObject1).insertSettingLocked("oem_screen_better_value", String.valueOf(Settings.System.getInt(SettingsProvider.this.getContext().getContentResolver(), "screen_saturation_level", 57)), "android");
            i = 133;
          }
          paramInt2 = i;
          if (i == 133) {
            paramInt2 = 140;
          }
          i = paramInt2;
          if (paramInt2 == 140)
          {
            getGlobalSettingsLocked().insertSettingLocked("op_enable_wifi_multi_broadcast", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034215)), "android");
            i = 141;
          }
          paramInt2 = i;
          if (i == 141)
          {
            getGlobalSettingsLocked().insertSettingLocked("wifi_ipv6_supported", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034216)), "android");
            paramInt2 = 142;
          }
          i = paramInt2;
          if (paramInt2 == 142)
          {
            localObject1 = getSystemSettingsLocked(paramInt1);
            ((SettingsState)localObject1).insertSettingLocked("game_mode_block_notification", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034218)), "android");
            ((SettingsState)localObject1).insertSettingLocked("game_mode_lock_buttons", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034219)), "android");
            i = 143;
          }
          paramInt2 = i;
          if (i == 143)
          {
            paramInt2 = Settings.Global.getInt(SettingsProvider.this.getContext().getContentResolver(), "night_mode_enabled", 0);
            getSecureSettingsLocked(paramInt1).insertSettingLocked("night_display_activated", String.valueOf(paramInt2), "android");
            getSystemSettingsLocked(paramInt1).updateSettingLocked("oem_nightmode_progress_status", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034220)), "android");
            paramInt2 = 144;
          }
          i = paramInt2;
          if (paramInt2 == 144)
          {
            paramInt2 = Settings.Secure.getInt(SettingsProvider.this.getContext().getContentResolver(), "bluetooth_aptx_hd", 0);
            localObject1 = getSecureSettingsLocked(paramInt1);
            if (paramInt2 != 0) {
              break label3021;
            }
            ((SettingsState)localObject1).updateSettingLocked("bluetooth_aptx_hd", String.valueOf(2), "android");
          }
        }
        for (;;)
        {
          i = 145;
          paramInt2 = i;
          if (i == 145)
          {
            getSystemSettingsLocked(paramInt1).updateSettingLocked("wifi_should_switch_network", String.valueOf(0), "android");
            paramInt2 = 146;
          }
          if (paramInt2 != paramInt3) {
            Slog.wtf("SettingsProvider", "warning: upgrading settings database to version " + paramInt3 + " left it at " + paramInt2 + " instead; this is probably a bug", new Throwable());
          }
          return 146;
          localObject1 = "0";
          break;
          label2462:
          if (!((SettingsState.Setting)localObject3).isNull()) {
            break label229;
          }
          ((SettingsState)localObject1).insertSettingLocked("nfc_payment_default_component", (String)localObject2, "android");
          break label229;
          label2485:
          localObject1 = "0";
          break label563;
          label2493:
          ((SettingsState)localObject1).insertSettingLocked("oem_clear_way", String.valueOf(SettingsProvider.this.getContext().getResources().getInteger(2131034210)), "android");
          break label812;
          label2529:
          localObject1 = "0";
          break label871;
          label2537:
          localObject2 = new StringBuilder();
          paramInt2 = 1;
          localObject3 = ((Iterable)localObject3).iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localObject4 = (ComponentName)((Iterator)localObject3).next();
            if (paramInt2 == 0) {
              ((StringBuilder)localObject2).append(':');
            }
            ((StringBuilder)localObject2).append(((ComponentName)localObject4).flattenToString());
            paramInt2 = 0;
          }
          ((SettingsState)localObject1).insertSettingLocked("enabled_vr_listeners", ((StringBuilder)localObject2).toString(), "android");
          break label940;
          label2626:
          localObject3 = new StringBuilder(((SettingsState.Setting)localObject3).getValue());
          ((StringBuilder)localObject3).append(":");
          ((StringBuilder)localObject3).append((String)localObject2);
          ((SettingsState)localObject1).updateSettingLocked("enabled_notification_policy_access_packages", ((StringBuilder)localObject3).toString(), "android");
          break label1145;
          label2676:
          localObject1 = "0";
          break label1260;
          label2684:
          paramInt2 = SettingsProvider.this.getContext().getResources().getInteger(2131034198);
          break label1356;
          label2707:
          localObject1 = "0";
          break label1410;
          label2715:
          if (SettingsProvider.this.getContext().getResources().getBoolean(2130968633))
          {
            localObject1 = "1";
            break label1410;
          }
          localObject1 = "0";
          break label1410;
          label2752:
          localObject3 = new String[8];
          localObject3[0] = "#FF42A5F5";
          localObject3[1] = "#FFCC6F4E";
          localObject3[2] = "#FFE6A545";
          localObject3[3] = "#FF7DC22F";
          localObject3[4] = "#FF9575CD";
          localObject3[5] = "#FF26C6DA";
          localObject3[6] = "#FFF06292";
          localObject3[7] = "#FFBA68C8";
          ((SettingsState)localObject1).insertSettingLocked("oem_black_mode", String.valueOf(1), "android");
          if (Integer.parseInt((String)localObject2) > 1)
          {
            paramInt2 = Integer.parseInt((String)localObject2) - 2;
            Slog.w("SettingsProvider", "blackColors[ " + paramInt2 + "] =" + localObject3[paramInt2] + " OEM_BLACK_MODE_ACCENT_COLOR_INDEX = " + String.valueOf(paramInt2));
            ((SettingsState)localObject1).insertSettingLocked("oem_black_mode_accent_color", localObject3[paramInt2], "android");
            ((SettingsState)localObject1).insertSettingLocked("oem_black_mode_accent_color_index", String.valueOf(paramInt2), "android");
            break label1640;
          }
          Slog.w("SettingsProvider", "blackColors[0] = " + localObject3[0]);
          ((SettingsState)localObject1).insertSettingLocked("oem_black_mode_accent_color", localObject3[0], "android");
          break label1640;
          label2980:
          if (Settings.System.getInt(SettingsProvider.this.getContext().getContentResolver(), "oem_black_mode", 2) != 0) {
            break label1640;
          }
          ((SettingsState)localObject1).updateSettingLocked("oem_black_mode", String.valueOf(2), "android");
          break label1640;
          label3021:
          ((SettingsState)localObject1).updateSettingLocked("bluetooth_aptx_hd", String.valueOf(0), "android");
        }
      }
      
      public void upgradeIfNeededLocked()
      {
        SettingsState localSettingsState = SettingsProvider.SettingsRegistry.this.getSettingsLocked(2, this.mUserId);
        int i = localSettingsState.getVersionLocked();
        if (i == 146) {
          return;
        }
        int j = onUpgradeLocked(this.mUserId, i, 146);
        if (j != 146)
        {
          SettingsProvider.SettingsRegistry.this.removeUserStateLocked(this.mUserId, true);
          Object localObject = new DatabaseHelper(SettingsProvider.this.getContext(), this.mUserId);
          SQLiteDatabase localSQLiteDatabase = ((DatabaseHelper)localObject).getWritableDatabase();
          ((DatabaseHelper)localObject).recreateDatabase(localSQLiteDatabase, 146, j, i);
          SettingsProvider.SettingsRegistry.-wrap0(SettingsProvider.SettingsRegistry.this, (DatabaseHelper)localObject, localSQLiteDatabase, this.mUserId);
          onUpgradeLocked(this.mUserId, i, 146);
          localObject = "Settings rebuilt! Current version: " + j + " while expected: " + 146;
          getGlobalSettingsLocked().insertSettingLocked("database_downgrade_reason", (String)localObject, "android");
        }
        if (this.mUserId == 0) {
          SettingsProvider.SettingsRegistry.this.getSettingsLocked(0, this.mUserId).setVersionLocked(146);
        }
        localSettingsState.setVersionLocked(146);
        SettingsProvider.SettingsRegistry.this.getSettingsLocked(1, this.mUserId).setVersionLocked(146);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/providers/settings/SettingsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */