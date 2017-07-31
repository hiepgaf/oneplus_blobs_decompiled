package com.android.server.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.AuthorityEntry;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageCleanItem;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;
import android.content.pm.PackageParser.Permission;
import android.content.pm.PackageUserState;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.pm.VerifierDeviceIdentity;
import android.net.Uri.Builder;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.os.PatternMatcher;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Log;
import android.util.LogPrinter;
import android.util.OpFeatures;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.InstallerConnection.InstallerException;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.XmlUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

final class Settings
{
  private static final String ATTR_APP_LINK_GENERATION = "app-link-generation";
  private static final String ATTR_BLOCKED = "blocked";
  private static final String ATTR_BLOCK_UNINSTALL = "blockUninstall";
  private static final String ATTR_CE_DATA_INODE = "ceDataInode";
  private static final String ATTR_CODE = "code";
  private static final String ATTR_DATABASE_VERSION = "databaseVersion";
  private static final String ATTR_DOMAIN_VERIFICATON_STATE = "domainVerificationStatus";
  private static final String ATTR_DONE = "done";
  private static final String ATTR_ENABLED = "enabled";
  private static final String ATTR_ENABLED_CALLER = "enabledCaller";
  private static final String ATTR_ENFORCEMENT = "enforcement";
  private static final String ATTR_FINGERPRINT = "fingerprint";
  private static final String ATTR_FLAGS = "flags";
  private static final String ATTR_GRANTED = "granted";
  private static final String ATTR_HIDDEN = "hidden";
  private static final String ATTR_INSTALLED = "inst";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_NOT_LAUNCHED = "nl";
  private static final String ATTR_PACKAGE_NAME = "packageName";
  private static final String ATTR_REVOKE_ON_UPGRADE = "rou";
  private static final String ATTR_SDK_VERSION = "sdkVersion";
  private static final String ATTR_STOPPED = "stopped";
  private static final String ATTR_SUSPENDED = "suspended";
  private static final String ATTR_USER = "user";
  private static final String ATTR_USER_FIXED = "fixed";
  private static final String ATTR_USER_SET = "set";
  private static final String ATTR_VOLUME_UUID = "volumeUuid";
  public static final int CURRENT_DATABASE_VERSION = 3;
  private static final boolean DEBUG_KERNEL = false;
  private static final boolean DEBUG_MU = false;
  private static final boolean DEBUG_STOPPED = false;
  static final Object[] FLAG_DUMP_SPEC = { Integer.valueOf(1), "SYSTEM", Integer.valueOf(2), "DEBUGGABLE", Integer.valueOf(4), "HAS_CODE", Integer.valueOf(8), "PERSISTENT", Integer.valueOf(16), "FACTORY_TEST", Integer.valueOf(32), "ALLOW_TASK_REPARENTING", Integer.valueOf(64), "ALLOW_CLEAR_USER_DATA", Integer.valueOf(128), "UPDATED_SYSTEM_APP", Integer.valueOf(256), "TEST_ONLY", Integer.valueOf(16384), "VM_SAFE_MODE", Integer.valueOf(32768), "ALLOW_BACKUP", Integer.valueOf(65536), "KILL_AFTER_RESTORE", Integer.valueOf(131072), "RESTORE_ANY_VERSION", Integer.valueOf(262144), "EXTERNAL_STORAGE", Integer.valueOf(1048576), "LARGE_HEAP" };
  private static int PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE = 0;
  private static int PRE_M_APP_INFO_FLAG_FORWARD_LOCK = 0;
  private static int PRE_M_APP_INFO_FLAG_HIDDEN = 0;
  private static int PRE_M_APP_INFO_FLAG_PRIVILEGED = 0;
  static final Object[] PRIVATE_FLAG_DUMP_SPEC = { Integer.valueOf(1), "HIDDEN", Integer.valueOf(2), "CANT_SAVE_STATE", Integer.valueOf(4), "FORWARD_LOCK", Integer.valueOf(8), "PRIVILEGED", Integer.valueOf(16), "HAS_DOMAIN_URLS", Integer.valueOf(32), "DEFAULT_TO_DEVICE_PROTECTED_STORAGE", Integer.valueOf(64), "DIRECT_BOOT_AWARE", Integer.valueOf(128), "AUTOPLAY", Integer.valueOf(256), "PARTIALLY_DIRECT_BOOT_AWARE", Integer.valueOf(512), "EPHEMERAL", Integer.valueOf(1024), "REQUIRED_FOR_SYSTEM_USER", Integer.valueOf(2048), "RESIZEABLE_ACTIVITIES", Integer.valueOf(4096), "BACKUP_IN_FOREGROUND" };
  private static final String RUNTIME_PERMISSIONS_FILE_NAME = "runtime-permissions.xml";
  private static final String TAG = "PackageSettings";
  private static final String TAG_ALL_INTENT_FILTER_VERIFICATION = "all-intent-filter-verifications";
  private static final String TAG_CHILD_PACKAGE = "child-package";
  static final String TAG_CROSS_PROFILE_INTENT_FILTERS = "crossProfile-intent-filters";
  private static final String TAG_DEFAULT_APPS = "default-apps";
  private static final String TAG_DEFAULT_BROWSER = "default-browser";
  private static final String TAG_DEFAULT_DIALER = "default-dialer";
  private static final String TAG_DISABLED_COMPONENTS = "disabled-components";
  private static final String TAG_DOMAIN_VERIFICATION = "domain-verification";
  private static final String TAG_ENABLED_COMPONENTS = "enabled-components";
  private static final String TAG_ITEM = "item";
  private static final String TAG_PACKAGE = "pkg";
  private static final String TAG_PACKAGE_RESTRICTIONS = "package-restrictions";
  private static final String TAG_PERMISSIONS = "perms";
  private static final String TAG_PERMISSION_ENTRY = "perm";
  private static final String TAG_PERSISTENT_PREFERRED_ACTIVITIES = "persistent-preferred-activities";
  private static final String TAG_READ_EXTERNAL_STORAGE = "read-external-storage";
  private static final String TAG_RESTORED_RUNTIME_PERMISSIONS = "restored-perms";
  private static final String TAG_RUNTIME_PERMISSIONS = "runtime-permissions";
  private static final String TAG_SHARED_USER = "shared-user";
  private static final String TAG_VERSION = "version";
  private static final int USER_RUNTIME_GRANT_MASK = 11;
  private static int mFirstAvailableUid = 0;
  private final File mBackupSettingsFilename;
  private final File mBackupStoppedPackagesFilename;
  final SparseArray<CrossProfileIntentResolver> mCrossProfileIntentResolvers = new SparseArray();
  final SparseArray<String> mDefaultBrowserApp = new SparseArray();
  final SparseArray<String> mDefaultDialerApp = new SparseArray();
  private final ArrayMap<String, PackageSetting> mDisabledSysPackages = new ArrayMap();
  final ArraySet<String> mInstallerPackages = new ArraySet();
  private final ArrayMap<String, Integer> mKernelMapping = new ArrayMap();
  private final File mKernelMappingFilename;
  public final KeySetManagerService mKeySetManagerService = new KeySetManagerService(this.mPackages);
  private final ArrayMap<Long, Integer> mKeySetRefs = new ArrayMap();
  private final Object mLock;
  final SparseIntArray mNextAppLinkGeneration = new SparseIntArray();
  private final SparseArray<Object> mOtherUserIds = new SparseArray();
  private final File mPackageListFilename;
  final ArrayMap<String, PackageSetting> mPackages = new ArrayMap();
  final ArrayList<PackageCleanItem> mPackagesToBeCleaned = new ArrayList();
  private final ArrayList<Signature> mPastSignatures = new ArrayList();
  private final ArrayList<PendingPackage> mPendingPackages = new ArrayList();
  final ArrayMap<String, BasePermission> mPermissionTrees = new ArrayMap();
  final ArrayMap<String, BasePermission> mPermissions = new ArrayMap();
  final SparseArray<PersistentPreferredIntentResolver> mPersistentPreferredActivities = new SparseArray();
  final SparseArray<PreferredIntentResolver> mPreferredActivities = new SparseArray();
  Boolean mReadExternalStorageEnforced;
  final StringBuilder mReadMessages = new StringBuilder();
  final ArrayMap<String, String> mRenamedPackages = new ArrayMap();
  private final ArrayMap<String, IntentFilterVerificationInfo> mRestoredIntentFilterVerifications = new ArrayMap();
  private final SparseArray<ArrayMap<String, ArraySet<RestoredPermissionGrant>>> mRestoredUserGrants = new SparseArray();
  private final RuntimePermissionPersistence mRuntimePermissionsPersistence;
  private final File mSettingsFilename;
  final ArrayMap<String, SharedUserSetting> mSharedUsers = new ArrayMap();
  private final File mStoppedPackagesFilename;
  private final File mSystemDir;
  private final ArrayList<Object> mUserIds = new ArrayList();
  private VerifierDeviceIdentity mVerifierDeviceIdentity;
  private ArrayMap<String, VersionInfo> mVersion = new ArrayMap();
  
  static
  {
    PRE_M_APP_INFO_FLAG_HIDDEN = 134217728;
    PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE = 268435456;
    PRE_M_APP_INFO_FLAG_FORWARD_LOCK = 536870912;
    PRE_M_APP_INFO_FLAG_PRIVILEGED = 1073741824;
  }
  
  Settings(File paramFile, Object paramObject)
  {
    this.mLock = paramObject;
    this.mRuntimePermissionsPersistence = new RuntimePermissionPersistence(this.mLock);
    this.mSystemDir = new File(paramFile, "system");
    this.mSystemDir.mkdirs();
    FileUtils.setPermissions(this.mSystemDir.toString(), 509, -1, -1);
    this.mSettingsFilename = new File(this.mSystemDir, "packages.xml");
    this.mBackupSettingsFilename = new File(this.mSystemDir, "packages-backup.xml");
    this.mPackageListFilename = new File(this.mSystemDir, "packages.list");
    FileUtils.setPermissions(this.mPackageListFilename, 416, 1000, 1032);
    paramFile = new File("/config/sdcardfs");
    if (paramFile.exists()) {}
    for (;;)
    {
      this.mKernelMappingFilename = paramFile;
      this.mStoppedPackagesFilename = new File(this.mSystemDir, "packages-stopped.xml");
      this.mBackupStoppedPackagesFilename = new File(this.mSystemDir, "packages-stopped-backup.xml");
      return;
      paramFile = null;
    }
  }
  
  Settings(Object paramObject)
  {
    this(Environment.getDataDirectory(), paramObject);
  }
  
  private void addPackageSettingLPw(PackageSetting paramPackageSetting, String paramString, SharedUserSetting paramSharedUserSetting)
  {
    this.mPackages.put(paramString, paramPackageSetting);
    Object localObject;
    if (paramSharedUserSetting != null)
    {
      if ((paramPackageSetting.sharedUser != null) && (paramPackageSetting.sharedUser != paramSharedUserSetting))
      {
        PackageManagerService.reportSettingsProblem(6, "Package " + paramPackageSetting.name + " was user " + paramPackageSetting.sharedUser + " but is now " + paramSharedUserSetting + "; I am not changing its files so it will probably fail!");
        paramPackageSetting.sharedUser.removePackage(paramPackageSetting);
        paramSharedUserSetting.addPackage(paramPackageSetting);
        paramPackageSetting.sharedUser = paramSharedUserSetting;
        paramPackageSetting.appId = paramSharedUserSetting.userId;
      }
    }
    else
    {
      localObject = getUserIdLPr(paramPackageSetting.appId);
      if (paramSharedUserSetting != null) {
        break label306;
      }
      if ((localObject != null) && (localObject != paramPackageSetting)) {
        replaceUserIdLPw(paramPackageSetting.appId, paramPackageSetting);
      }
    }
    for (;;)
    {
      paramSharedUserSetting = (IntentFilterVerificationInfo)this.mRestoredIntentFilterVerifications.get(paramString);
      if (paramSharedUserSetting != null)
      {
        if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
          Slog.i("PackageSettings", "Applying restored IVI for " + paramString + " : " + paramSharedUserSetting.getStatusString());
        }
        this.mRestoredIntentFilterVerifications.remove(paramString);
        paramPackageSetting.setIntentFilterVerificationInfo(paramSharedUserSetting);
      }
      return;
      if (paramPackageSetting.appId == paramSharedUserSetting.userId) {
        break;
      }
      PackageManagerService.reportSettingsProblem(6, "Package " + paramPackageSetting.name + " was user id " + paramPackageSetting.appId + " but is now user " + paramSharedUserSetting + " with id " + paramSharedUserSetting.userId + "; I am not changing its files so it will probably fail!");
      break;
      label306:
      if ((localObject != null) && (localObject != paramSharedUserSetting)) {
        replaceUserIdLPw(paramPackageSetting.appId, paramSharedUserSetting);
      }
    }
  }
  
  private boolean addUserIdLPw(int paramInt, Object paramObject1, Object paramObject2)
  {
    if (paramInt > 19999) {
      return false;
    }
    if (paramInt >= 10000)
    {
      int i = this.mUserIds.size();
      int j = paramInt - 10000;
      while (j >= i)
      {
        this.mUserIds.add(null);
        i += 1;
      }
      if (this.mUserIds.get(j) != null)
      {
        PackageManagerService.reportSettingsProblem(6, "Adding duplicate user id: " + paramInt + " name=" + paramObject2);
        return false;
      }
      this.mUserIds.set(j, paramObject1);
    }
    for (;;)
    {
      return true;
      if (this.mOtherUserIds.get(paramInt) != null)
      {
        PackageManagerService.reportSettingsProblem(6, "Adding duplicate shared id: " + paramInt + " name=" + paramObject2);
        return false;
      }
      this.mOtherUserIds.put(paramInt, paramObject1);
    }
  }
  
  private void applyDefaultPreferredActivityLPw(PackageManagerService paramPackageManagerService, Intent paramIntent, int paramInt1, ComponentName paramComponentName, String paramString, PatternMatcher paramPatternMatcher1, IntentFilter.AuthorityEntry paramAuthorityEntry, PatternMatcher paramPatternMatcher2, int paramInt2)
  {
    int n = paramPackageManagerService.updateFlagsForResolve(paramInt1, paramInt2, paramIntent);
    List localList = paramPackageManagerService.mActivities.queryIntent(paramIntent, paramIntent.getType(), n, 0);
    if (PackageManagerService.DEBUG_PREFERRED) {
      Log.d("PackageSettings", "Queried " + paramIntent + " results: " + localList);
    }
    int k = 0;
    if ((localList != null) && (localList.size() > 1))
    {
      int i = 0;
      Object localObject = null;
      ComponentName[] arrayOfComponentName = new ComponentName[localList.size()];
      paramInt1 = 0;
      paramPackageManagerService = (PackageManagerService)localObject;
      int m;
      int j;
      if (paramInt1 < localList.size())
      {
        paramPackageManagerService = ((ResolveInfo)localList.get(paramInt1)).activityInfo;
        arrayOfComponentName[paramInt1] = new ComponentName(paramPackageManagerService.packageName, paramPackageManagerService.name);
        if ((paramPackageManagerService.applicationInfo.flags & 0x1) == 0)
        {
          m = k;
          j = i;
          if (((ResolveInfo)localList.get(paramInt1)).match < 0) {
            break label452;
          }
          if (PackageManagerService.DEBUG_PREFERRED) {
            Log.d("PackageSettings", "Result " + paramPackageManagerService.packageName + "/" + paramPackageManagerService.name + ": non-system!");
          }
          paramPackageManagerService = arrayOfComponentName[paramInt1];
        }
      }
      else
      {
        localObject = paramPackageManagerService;
        if (paramPackageManagerService != null)
        {
          localObject = paramPackageManagerService;
          if (k > 0) {
            localObject = null;
          }
        }
        if ((i == 0) || (localObject != null)) {
          break label695;
        }
        paramPackageManagerService = new IntentFilter();
        if (paramIntent.getAction() != null) {
          paramPackageManagerService.addAction(paramIntent.getAction());
        }
        if (paramIntent.getCategories() == null) {
          break label540;
        }
        localObject = paramIntent.getCategories().iterator();
        while (((Iterator)localObject).hasNext()) {
          paramPackageManagerService.addCategory((String)((Iterator)localObject).next());
        }
      }
      if ((paramComponentName.getPackageName().equals(paramPackageManagerService.packageName)) && (paramComponentName.getClassName().equals(paramPackageManagerService.name)))
      {
        if (PackageManagerService.DEBUG_PREFERRED) {
          Log.d("PackageSettings", "Result " + paramPackageManagerService.packageName + "/" + paramPackageManagerService.name + ": default!");
        }
        j = 1;
        m = ((ResolveInfo)localList.get(paramInt1)).match;
      }
      for (;;)
      {
        label452:
        paramInt1 += 1;
        k = m;
        i = j;
        break;
        m = k;
        j = i;
        if (PackageManagerService.DEBUG_PREFERRED)
        {
          Log.d("PackageSettings", "Result " + paramPackageManagerService.packageName + "/" + paramPackageManagerService.name + ": skipped");
          m = k;
          j = i;
        }
      }
      label540:
      if ((0x10000 & n) != 0) {
        paramPackageManagerService.addCategory("android.intent.category.DEFAULT");
      }
      if (paramString != null) {
        paramPackageManagerService.addDataScheme(paramString);
      }
      if (paramPatternMatcher1 != null) {
        paramPackageManagerService.addDataSchemeSpecificPart(paramPatternMatcher1.getPath(), paramPatternMatcher1.getType());
      }
      if (paramAuthorityEntry != null) {
        paramPackageManagerService.addDataAuthority(paramAuthorityEntry);
      }
      if (paramPatternMatcher2 != null) {
        paramPackageManagerService.addDataPath(paramPatternMatcher2);
      }
      if (paramIntent.getType() != null) {}
      try
      {
        paramPackageManagerService.addDataType(paramIntent.getType());
        paramPackageManagerService = new PreferredActivity(paramPackageManagerService, k, arrayOfComponentName, paramComponentName, true);
        editPreferredActivitiesLPw(paramInt2).addFilter(paramPackageManagerService);
        return;
      }
      catch (IntentFilter.MalformedMimeTypeException paramString)
      {
        for (;;)
        {
          Slog.w("PackageSettings", "Malformed mimetype " + paramIntent.getType() + " for " + paramComponentName);
        }
      }
      label695:
      if (localObject == null)
      {
        paramPackageManagerService = new StringBuilder();
        paramPackageManagerService.append("No component ");
        paramPackageManagerService.append(paramComponentName.flattenToShortString());
        paramPackageManagerService.append(" found setting preferred ");
        paramPackageManagerService.append(paramIntent);
        paramPackageManagerService.append("; possible matches are ");
        paramInt1 = 0;
        while (paramInt1 < arrayOfComponentName.length)
        {
          if (paramInt1 > 0) {
            paramPackageManagerService.append(", ");
          }
          paramPackageManagerService.append(arrayOfComponentName[paramInt1].flattenToShortString());
          paramInt1 += 1;
        }
        Slog.w("PackageSettings", paramPackageManagerService.toString());
        return;
      }
      Slog.i("PackageSettings", "Not setting preferred " + paramIntent + "; found third party match " + ((ComponentName)localObject).flattenToShortString());
      return;
    }
    Slog.w("PackageSettings", "No potential matches found for " + paramIntent + " while setting preferred " + paramComponentName.flattenToShortString());
  }
  
  private void applyDefaultPreferredActivityLPw(PackageManagerService paramPackageManagerService, IntentFilter paramIntentFilter, ComponentName paramComponentName, int paramInt)
  {
    if (PackageManagerService.DEBUG_PREFERRED)
    {
      Log.d("PackageSettings", "Processing preferred:");
      paramIntentFilter.dump(new LogPrinter(3, "PackageSettings"), "  ");
    }
    Intent localIntent1 = new Intent();
    int j = 786432;
    localIntent1.setAction(paramIntentFilter.getAction(0));
    int i = 0;
    String str;
    if (i < paramIntentFilter.countCategories())
    {
      str = paramIntentFilter.getCategory(i);
      if (str.equals("android.intent.category.DEFAULT")) {
        j |= 0x10000;
      }
      for (;;)
      {
        i += 1;
        break;
        localIntent1.addCategory(str);
      }
    }
    i = 1;
    int n = 0;
    int k = 0;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    while (k < paramIntentFilter.countDataSchemes())
    {
      i = 1;
      str = paramIntentFilter.getDataScheme(k);
      int m = n;
      if (str != null) {
        if (!str.isEmpty()) {
          break label266;
        }
      }
      label266:
      for (m = n;; m = 1)
      {
        n = 0;
        while (n < paramIntentFilter.countDataSchemeSpecificParts())
        {
          localObject1 = new Uri.Builder();
          ((Uri.Builder)localObject1).scheme(str);
          localObject2 = paramIntentFilter.getDataSchemeSpecificPart(n);
          ((Uri.Builder)localObject1).opaquePart(((PatternMatcher)localObject2).getPath());
          localObject3 = new Intent(localIntent1);
          ((Intent)localObject3).setData(((Uri.Builder)localObject1).build());
          applyDefaultPreferredActivityLPw(paramPackageManagerService, (Intent)localObject3, j, paramComponentName, str, (PatternMatcher)localObject2, null, null, paramInt);
          i = 0;
          n += 1;
        }
      }
      n = 0;
      while (n < paramIntentFilter.countDataAuthorities())
      {
        int i2 = 1;
        localObject1 = paramIntentFilter.getDataAuthority(n);
        int i1 = 0;
        while (i1 < paramIntentFilter.countDataPaths())
        {
          localObject2 = new Uri.Builder();
          ((Uri.Builder)localObject2).scheme(str);
          if (((IntentFilter.AuthorityEntry)localObject1).getHost() != null) {
            ((Uri.Builder)localObject2).authority(((IntentFilter.AuthorityEntry)localObject1).getHost());
          }
          localObject3 = paramIntentFilter.getDataPath(i1);
          ((Uri.Builder)localObject2).path(((PatternMatcher)localObject3).getPath());
          Intent localIntent2 = new Intent(localIntent1);
          localIntent2.setData(((Uri.Builder)localObject2).build());
          applyDefaultPreferredActivityLPw(paramPackageManagerService, localIntent2, j, paramComponentName, str, null, (IntentFilter.AuthorityEntry)localObject1, (PatternMatcher)localObject3, paramInt);
          i = 0;
          i2 = 0;
          i1 += 1;
        }
        if (i2 != 0)
        {
          localObject2 = new Uri.Builder();
          ((Uri.Builder)localObject2).scheme(str);
          if (((IntentFilter.AuthorityEntry)localObject1).getHost() != null) {
            ((Uri.Builder)localObject2).authority(((IntentFilter.AuthorityEntry)localObject1).getHost());
          }
          localObject3 = new Intent(localIntent1);
          ((Intent)localObject3).setData(((Uri.Builder)localObject2).build());
          applyDefaultPreferredActivityLPw(paramPackageManagerService, (Intent)localObject3, j, paramComponentName, str, null, (IntentFilter.AuthorityEntry)localObject1, null, paramInt);
          i = 0;
        }
        n += 1;
      }
      if (i != 0)
      {
        localObject1 = new Uri.Builder();
        ((Uri.Builder)localObject1).scheme(str);
        localObject2 = new Intent(localIntent1);
        ((Intent)localObject2).setData(((Uri.Builder)localObject1).build());
        applyDefaultPreferredActivityLPw(paramPackageManagerService, (Intent)localObject2, j, paramComponentName, str, null, null, null, paramInt);
      }
      i = 0;
      k += 1;
      n = m;
    }
    k = 0;
    while (k < paramIntentFilter.countDataTypes())
    {
      str = paramIntentFilter.getDataType(k);
      if (n != 0)
      {
        localObject1 = new Uri.Builder();
        i = 0;
        if (i < paramIntentFilter.countDataSchemes())
        {
          localObject2 = paramIntentFilter.getDataScheme(i);
          if ((localObject2 == null) || (((String)localObject2).isEmpty())) {}
          for (;;)
          {
            i += 1;
            break;
            localObject3 = new Intent(localIntent1);
            ((Uri.Builder)localObject1).scheme((String)localObject2);
            ((Intent)localObject3).setDataAndType(((Uri.Builder)localObject1).build(), str);
            applyDefaultPreferredActivityLPw(paramPackageManagerService, (Intent)localObject3, j, paramComponentName, (String)localObject2, null, null, null, paramInt);
          }
        }
      }
      else
      {
        localObject1 = new Intent(localIntent1);
        ((Intent)localObject1).setType(str);
        applyDefaultPreferredActivityLPw(paramPackageManagerService, (Intent)localObject1, j, paramComponentName, null, null, null, null, paramInt);
      }
      i = 0;
      k += 1;
    }
    if (i != 0) {
      applyDefaultPreferredActivityLPw(paramPackageManagerService, localIntent1, j, paramComponentName, null, null, null, null, paramInt);
    }
  }
  
  private String compToString(ArraySet<String> paramArraySet)
  {
    if (paramArraySet != null) {
      return Arrays.toString(paramArraySet.toArray());
    }
    return "[]";
  }
  
  private static void dumpSplitNames(PrintWriter paramPrintWriter, PackageParser.Package paramPackage)
  {
    if (paramPackage == null)
    {
      paramPrintWriter.print("unknown");
      return;
    }
    paramPrintWriter.print("[");
    paramPrintWriter.print("base");
    if (paramPackage.baseRevisionCode != 0)
    {
      paramPrintWriter.print(":");
      paramPrintWriter.print(paramPackage.baseRevisionCode);
    }
    if (paramPackage.splitNames != null)
    {
      int i = 0;
      while (i < paramPackage.splitNames.length)
      {
        paramPrintWriter.print(", ");
        paramPrintWriter.print(paramPackage.splitNames[i]);
        if (paramPackage.splitRevisionCodes[i] != 0)
        {
          paramPrintWriter.print(":");
          paramPrintWriter.print(paramPackage.splitRevisionCodes[i]);
        }
        i += 1;
      }
    }
    paramPrintWriter.print("]");
  }
  
  private PackageSetting getPackageLPw(String paramString1, PackageSetting paramPackageSetting, String paramString2, SharedUserSetting paramSharedUserSetting, File paramFile1, File paramFile2, String paramString3, String paramString4, String paramString5, int paramInt1, int paramInt2, int paramInt3, UserHandle paramUserHandle, boolean paramBoolean1, boolean paramBoolean2, String paramString6, List<String> paramList)
  {
    Object localObject2 = (PackageSetting)this.mPackages.get(paramString1);
    UserManagerService localUserManagerService = UserManagerService.getInstance();
    Object localObject1 = localObject2;
    if (localObject2 != null)
    {
      ((PackageSetting)localObject2).primaryCpuAbiString = paramString4;
      ((PackageSetting)localObject2).secondaryCpuAbiString = paramString5;
      if (paramList != null) {
        ((PackageSetting)localObject2).childPackageNames = new ArrayList(paramList);
      }
      if (!((PackageSetting)localObject2).codePath.equals(paramFile1))
      {
        if ((((PackageSetting)localObject2).pkgFlags & 0x1) != 0) {
          Slog.w("PackageManager", "Trying to update system app code path from " + ((PackageSetting)localObject2).codePathString + " to " + paramFile1.toString());
        }
      }
      else
      {
        if (((PackageSetting)localObject2).sharedUser == paramSharedUserSetting) {
          break label596;
        }
        StringBuilder localStringBuilder = new StringBuilder().append("Package ").append(paramString1).append(" shared user changed from ");
        if (((PackageSetting)localObject2).sharedUser == null) {
          break label580;
        }
        localObject1 = ((PackageSetting)localObject2).sharedUser.name;
        label181:
        localObject2 = localStringBuilder.append((String)localObject1).append(" to ");
        if (paramSharedUserSetting == null) {
          break label588;
        }
        localObject1 = paramSharedUserSetting.name;
        label208:
        PackageManagerService.reportSettingsProblem(5, (String)localObject1 + "; replacing with new");
        localObject1 = null;
      }
    }
    else
    {
      label231:
      if (localObject1 != null) {
        break label1036;
      }
      if (paramPackageSetting == null) {
        break label634;
      }
      paramFile2 = new PackageSetting(paramPackageSetting.name, paramString1, paramFile1, paramFile2, paramString3, paramString4, paramString5, null, paramInt1, paramInt2, paramInt3, paramString6, paramList);
      if (PackageManagerService.DEBUG_UPGRADE) {
        Log.v("PackageManager", "Package " + paramString1 + " is adopting original package " + paramPackageSetting.name);
      }
      paramString2 = paramFile2.signatures;
      paramFile2.copyFrom(paramPackageSetting);
      paramFile2.signatures = paramString2;
      paramFile2.sharedUser = paramPackageSetting.sharedUser;
      paramFile2.appId = paramPackageSetting.appId;
      paramFile2.origPackage = paramPackageSetting;
      paramFile2.getPermissionsState().copyFrom(paramPackageSetting.getPermissionsState());
      this.mRenamedPackages.put(paramString1, paramPackageSetting.name);
      paramString2 = paramPackageSetting.name;
      paramFile2.setTimeStamp(paramFile1.lastModified());
      paramPackageSetting = paramFile2;
    }
    for (;;)
    {
      if (paramPackageSetting.appId >= 0) {
        break label1017;
      }
      PackageManagerService.reportSettingsProblem(5, "Package " + paramString2 + " could not be assigned a valid uid");
      return null;
      Slog.i("PackageManager", "Package " + paramString1 + " codePath changed from " + ((PackageSetting)localObject2).codePath + " to " + paramFile1 + "; Retaining data and using new");
      if (((paramInt2 & 0x1) != 0) && (getDisabledSystemPkgLPr(paramString1) == null))
      {
        localObject1 = getAllUsers();
        if (localObject1 != null)
        {
          localObject1 = ((Iterable)localObject1).iterator();
          while (((Iterator)localObject1).hasNext()) {
            ((PackageSetting)localObject2).setInstalled(true, ((UserInfo)((Iterator)localObject1).next()).id);
          }
        }
      }
      ((PackageSetting)localObject2).legacyNativeLibraryPathString = paramString3;
      break;
      label580:
      localObject1 = "<nothing>";
      break label181;
      label588:
      localObject1 = "<nothing>";
      break label208;
      label596:
      ((PackageSetting)localObject2).pkgFlags |= paramInt2 & 0x1;
      ((PackageSetting)localObject2).pkgPrivateFlags |= paramInt3 & 0x8;
      localObject1 = localObject2;
      break label231;
      label634:
      paramPackageSetting = new PackageSetting(paramString1, paramString2, paramFile1, paramFile2, paramString3, paramString4, paramString5, null, paramInt1, paramInt2, paramInt3, paramString6, paramList);
      paramPackageSetting.setTimeStamp(paramFile1.lastModified());
      paramPackageSetting.sharedUser = paramSharedUserSetting;
      if ((paramInt2 & 0x1) == 0)
      {
        paramString2 = getAllUsers();
        if (paramUserHandle != null)
        {
          paramInt1 = paramUserHandle.getIdentifier();
          if ((paramString2 == null) || (!paramBoolean2)) {
            break label831;
          }
          paramString2 = paramString2.iterator();
          label720:
          if (!paramString2.hasNext()) {
            break label831;
          }
          paramFile1 = (UserInfo)paramString2.next();
          if ((paramUserHandle == null) || ((paramInt1 == -1) && (!isAdbInstallDisallowed(localUserManagerService, paramFile1.id)))) {
            break label819;
          }
          if (paramInt1 != paramFile1.id) {
            break label825;
          }
          paramBoolean2 = true;
        }
        for (;;)
        {
          paramPackageSetting.setUserState(paramFile1.id, 0L, 0, paramBoolean2, true, true, false, false, null, null, null, false, 0, 0);
          writePackageRestrictionsLPr(paramFile1.id);
          break label720;
          paramInt1 = 0;
          break;
          label819:
          paramBoolean2 = true;
          continue;
          label825:
          paramBoolean2 = false;
        }
      }
      label831:
      if (paramSharedUserSetting != null)
      {
        paramPackageSetting.appId = paramSharedUserSetting.userId;
        paramString2 = paramString1;
      }
      else
      {
        paramString2 = (PackageSetting)this.mDisabledSysPackages.get(paramString1);
        if (paramString2 != null)
        {
          if (paramString2.signatures.mSignatures != null) {
            paramPackageSetting.signatures.mSignatures = ((Signature[])paramString2.signatures.mSignatures.clone());
          }
          paramPackageSetting.appId = paramString2.appId;
          paramPackageSetting.getPermissionsState().copyFrom(paramString2.getPermissionsState());
          paramFile1 = getAllUsers();
          if (paramFile1 != null)
          {
            paramFile1 = paramFile1.iterator();
            while (paramFile1.hasNext())
            {
              paramInt1 = ((UserInfo)paramFile1.next()).id;
              paramPackageSetting.setDisabledComponentsCopy(paramString2.getDisabledComponents(paramInt1), paramInt1);
              paramPackageSetting.setEnabledComponentsCopy(paramString2.getEnabledComponents(paramInt1), paramInt1);
            }
          }
          addUserIdLPw(paramPackageSetting.appId, paramPackageSetting, paramString1);
          paramString2 = paramString1;
        }
        else
        {
          paramPackageSetting.appId = newUserIdLPw(paramPackageSetting);
          paramString2 = paramString1;
        }
      }
    }
    label1017:
    paramString1 = paramPackageSetting;
    if (paramBoolean1)
    {
      addPackageSettingLPw(paramPackageSetting, paramString2, paramSharedUserSetting);
      paramString1 = paramPackageSetting;
    }
    label1036:
    do
    {
      do
      {
        do
        {
          return paramString1;
          paramString1 = (String)localObject1;
        } while (paramUserHandle == null);
        paramString1 = (String)localObject1;
      } while (!paramBoolean2);
      paramPackageSetting = getAllUsers();
      paramString1 = (String)localObject1;
    } while (paramPackageSetting == null);
    paramPackageSetting = paramPackageSetting.iterator();
    for (;;)
    {
      paramString1 = (String)localObject1;
      if (!paramPackageSetting.hasNext()) {
        break;
      }
      paramString1 = (UserInfo)paramPackageSetting.next();
      if (((paramUserHandle.getIdentifier() == -1) && (!isAdbInstallDisallowed(localUserManagerService, paramString1.id))) || ((paramUserHandle.getIdentifier() == paramString1.id) && (!((PackageSetting)localObject1).getInstalled(paramString1.id))))
      {
        ((PackageSetting)localObject1).setInstalled(true, paramString1.id);
        writePackageRestrictionsLPr(paramString1.id);
      }
    }
  }
  
  private File getUserPackagesStateBackupFile(int paramInt)
  {
    return new File(Environment.getUserSystemDirectory(paramInt), "package-restrictions-backup.xml");
  }
  
  private File getUserPackagesStateFile(int paramInt)
  {
    return new File(new File(new File(this.mSystemDir, "users"), Integer.toString(paramInt)), "package-restrictions.xml");
  }
  
  private File getUserRuntimePermissionsFile(int paramInt)
  {
    return new File(new File(new File(this.mSystemDir, "users"), Integer.toString(paramInt)), "runtime-permissions.xml");
  }
  
  private int newUserIdLPw(Object paramObject)
  {
    int j = this.mUserIds.size();
    int i = mFirstAvailableUid;
    while (i < j)
    {
      if (this.mUserIds.get(i) == null)
      {
        this.mUserIds.set(i, paramObject);
        return i + 10000;
      }
      i += 1;
    }
    if (j > 9999) {
      return -1;
    }
    this.mUserIds.add(paramObject);
    return j + 10000;
  }
  
  private static String permissionFlagsToString(String paramString, int paramInt)
  {
    Object localObject2;
    for (Object localObject1 = null; paramInt != 0; localObject1 = localObject2)
    {
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append(paramString);
        ((StringBuilder)localObject2).append("[ ");
      }
      int i = 1 << Integer.numberOfTrailingZeros(paramInt);
      paramInt &= i;
      ((StringBuilder)localObject2).append(PackageManager.permissionFlagToString(i));
      ((StringBuilder)localObject2).append(' ');
    }
    if (localObject1 != null)
    {
      ((StringBuilder)localObject1).append(']');
      return ((StringBuilder)localObject1).toString();
    }
    return "";
  }
  
  static void printFlags(PrintWriter paramPrintWriter, int paramInt, Object[] paramArrayOfObject)
  {
    paramPrintWriter.print("[ ");
    int i = 0;
    while (i < paramArrayOfObject.length)
    {
      if ((paramInt & ((Integer)paramArrayOfObject[i]).intValue()) != 0)
      {
        paramPrintWriter.print(paramArrayOfObject[(i + 1)]);
        paramPrintWriter.print(" ");
      }
      i += 2;
    }
    paramPrintWriter.print("]");
  }
  
  private ArraySet<String> readComponentsLPr(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    Object localObject1 = null;
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4) && (paramXmlPullParser.getName().equals("item")))
      {
        String str = paramXmlPullParser.getAttributeValue(null, "name");
        if (str != null)
        {
          Object localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = new ArraySet();
          }
          ((ArraySet)localObject2).add(str);
          localObject1 = localObject2;
        }
      }
    }
    return (ArraySet<String>)localObject1;
  }
  
  private void readCrossProfileIntentFiltersLPw(XmlPullParser paramXmlPullParser, int paramInt)
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
        Object localObject = paramXmlPullParser.getName();
        if (((String)localObject).equals("item"))
        {
          localObject = new CrossProfileIntentFilter(paramXmlPullParser);
          editCrossProfileIntentResolverLPw(paramInt).addFilter((IntentFilter)localObject);
        }
        else
        {
          PackageManagerService.reportSettingsProblem(5, "Unknown element under crossProfile-intent-filters: " + (String)localObject);
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  private void readDefaultPreferredActivitiesLPw(PackageManagerService paramPackageManagerService, XmlPullParser paramXmlPullParser, int paramInt)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if (paramXmlPullParser.getName().equals("item"))
        {
          PreferredActivity localPreferredActivity = new PreferredActivity(paramXmlPullParser);
          if (localPreferredActivity.mPref.getParseError() == null) {
            applyDefaultPreferredActivityLPw(paramPackageManagerService, localPreferredActivity, localPreferredActivity.mPref.mComponent, paramInt);
          } else {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + localPreferredActivity.mPref.getParseError() + " at " + paramXmlPullParser.getPositionDescription());
          }
        }
        else
        {
          PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  private void readDisabledComponentsLPw(PackageSettingBase paramPackageSettingBase, XmlPullParser paramXmlPullParser, int paramInt)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    int j;
    do
    {
      j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
    } while ((j == 3) || (j == 4));
    if (paramXmlPullParser.getName().equals("item"))
    {
      String str = paramXmlPullParser.getAttributeValue(null, "name");
      if (str != null) {
        paramPackageSettingBase.addDisabledComponent(str.intern(), paramInt);
      }
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      break;
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <disabled-components> has no name at " + paramXmlPullParser.getPositionDescription());
      continue;
      PackageManagerService.reportSettingsProblem(5, "Unknown element under <disabled-components>: " + paramXmlPullParser.getName());
    }
  }
  
  /* Error */
  private void readDisabledSysPackageLPw(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: aconst_null
    //   2: ldc 71
    //   4: invokeinterface 1184 3 0
    //   9: astore 9
    //   11: aload_1
    //   12: aconst_null
    //   13: ldc_w 1254
    //   16: invokeinterface 1184 3 0
    //   21: astore 10
    //   23: aload_1
    //   24: aconst_null
    //   25: ldc_w 1255
    //   28: invokeinterface 1184 3 0
    //   33: astore 6
    //   35: aload_1
    //   36: aconst_null
    //   37: ldc_w 1257
    //   40: invokeinterface 1184 3 0
    //   45: astore 7
    //   47: aload_1
    //   48: aconst_null
    //   49: ldc_w 1259
    //   52: invokeinterface 1184 3 0
    //   57: astore 5
    //   59: aload_1
    //   60: aconst_null
    //   61: ldc_w 1261
    //   64: invokeinterface 1184 3 0
    //   69: astore 11
    //   71: aload_1
    //   72: aconst_null
    //   73: ldc_w 1263
    //   76: invokeinterface 1184 3 0
    //   81: astore 12
    //   83: aload_1
    //   84: aconst_null
    //   85: ldc_w 1265
    //   88: invokeinterface 1184 3 0
    //   93: astore 8
    //   95: aload_1
    //   96: aconst_null
    //   97: ldc_w 1267
    //   100: invokeinterface 1184 3 0
    //   105: astore 13
    //   107: aload_1
    //   108: aconst_null
    //   109: ldc_w 1269
    //   112: invokeinterface 1184 3 0
    //   117: astore 14
    //   119: aload 8
    //   121: astore 4
    //   123: aload 8
    //   125: ifnonnull +16 -> 141
    //   128: aload 8
    //   130: astore 4
    //   132: aload 5
    //   134: ifnull +7 -> 141
    //   137: aload 5
    //   139: astore 4
    //   141: aload 7
    //   143: astore 5
    //   145: aload 7
    //   147: ifnonnull +7 -> 154
    //   150: aload 6
    //   152: astore 5
    //   154: aload_1
    //   155: aconst_null
    //   156: ldc -74
    //   158: invokeinterface 1184 3 0
    //   163: astore 7
    //   165: iconst_0
    //   166: istore_3
    //   167: iload_3
    //   168: istore_2
    //   169: aload 7
    //   171: ifnull +9 -> 180
    //   174: aload 7
    //   176: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   179: istore_2
    //   180: iconst_0
    //   181: istore_3
    //   182: new 434	java/io/File
    //   185: dup
    //   186: aload 6
    //   188: invokespecial 475	java/io/File:<init>	(Ljava/lang/String;)V
    //   191: astore 6
    //   193: aload 6
    //   195: invokestatic 1277	com/android/server/pm/PackageManagerService:locationIsPrivileged	(Ljava/io/File;)Z
    //   198: ifeq +6 -> 204
    //   201: bipush 8
    //   203: istore_3
    //   204: new 505	com/android/server/pm/PackageSetting
    //   207: dup
    //   208: aload 9
    //   210: aload 10
    //   212: aload 6
    //   214: new 434	java/io/File
    //   217: dup
    //   218: aload 5
    //   220: invokespecial 475	java/io/File:<init>	(Ljava/lang/String;)V
    //   223: aload 11
    //   225: aload 4
    //   227: aload 13
    //   229: aload 14
    //   231: iload_2
    //   232: iconst_1
    //   233: iload_3
    //   234: aload 12
    //   236: aconst_null
    //   237: invokespecial 1003	com/android/server/pm/PackageSetting:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;Ljava/io/File;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIILjava/lang/String;Ljava/util/List;)V
    //   240: astore 4
    //   242: aload_1
    //   243: aconst_null
    //   244: ldc_w 1279
    //   247: invokeinterface 1184 3 0
    //   252: astore 5
    //   254: aload 5
    //   256: ifnull +217 -> 473
    //   259: aload 4
    //   261: aload 5
    //   263: bipush 16
    //   265: invokestatic 1285	java/lang/Long:parseLong	(Ljava/lang/String;I)J
    //   268: invokevirtual 1040	com/android/server/pm/PackageSetting:setTimeStamp	(J)V
    //   271: aload_1
    //   272: aconst_null
    //   273: ldc_w 1287
    //   276: invokeinterface 1184 3 0
    //   281: astore 5
    //   283: aload 5
    //   285: ifnull +15 -> 300
    //   288: aload 4
    //   290: aload 5
    //   292: bipush 16
    //   294: invokestatic 1285	java/lang/Long:parseLong	(Ljava/lang/String;I)J
    //   297: putfield 1291	com/android/server/pm/PackageSetting:firstInstallTime	J
    //   300: aload_1
    //   301: aconst_null
    //   302: ldc_w 1293
    //   305: invokeinterface 1184 3 0
    //   310: astore 5
    //   312: aload 5
    //   314: ifnull +15 -> 329
    //   317: aload 4
    //   319: aload 5
    //   321: bipush 16
    //   323: invokestatic 1285	java/lang/Long:parseLong	(Ljava/lang/String;I)J
    //   326: putfield 1296	com/android/server/pm/PackageSetting:lastUpdateTime	J
    //   329: aload_1
    //   330: aconst_null
    //   331: ldc_w 1297
    //   334: invokeinterface 1184 3 0
    //   339: astore 5
    //   341: aload 5
    //   343: ifnull +165 -> 508
    //   346: aload 5
    //   348: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   351: istore_2
    //   352: aload 4
    //   354: iload_2
    //   355: putfield 548	com/android/server/pm/PackageSetting:appId	I
    //   358: aload 4
    //   360: getfield 548	com/android/server/pm/PackageSetting:appId	I
    //   363: ifgt +32 -> 395
    //   366: aload_1
    //   367: aconst_null
    //   368: ldc_w 1299
    //   371: invokeinterface 1184 3 0
    //   376: astore 5
    //   378: aload 5
    //   380: ifnull +133 -> 513
    //   383: aload 5
    //   385: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   388: istore_2
    //   389: aload 4
    //   391: iload_2
    //   392: putfield 548	com/android/server/pm/PackageSetting:appId	I
    //   395: aload_1
    //   396: invokeinterface 1175 1 0
    //   401: istore_2
    //   402: aload_1
    //   403: invokeinterface 1177 1 0
    //   408: istore_3
    //   409: iload_3
    //   410: iconst_1
    //   411: if_icmpeq +204 -> 615
    //   414: iload_3
    //   415: iconst_3
    //   416: if_icmpne +13 -> 429
    //   419: aload_1
    //   420: invokeinterface 1175 1 0
    //   425: iload_2
    //   426: if_icmple +189 -> 615
    //   429: iload_3
    //   430: iconst_3
    //   431: if_icmpeq -29 -> 402
    //   434: iload_3
    //   435: iconst_4
    //   436: if_icmpeq -34 -> 402
    //   439: aload_1
    //   440: invokeinterface 1180 1 0
    //   445: ldc -95
    //   447: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   450: ifeq +68 -> 518
    //   453: aload_0
    //   454: aload_1
    //   455: aload 4
    //   457: invokevirtual 1027	com/android/server/pm/PackageSetting:getPermissionsState	()Lcom/android/server/pm/PermissionsState;
    //   460: invokevirtual 1303	com/android/server/pm/Settings:readInstallPermissionsLPr	(Lorg/xmlpull/v1/XmlPullParser;Lcom/android/server/pm/PermissionsState;)V
    //   463: goto -61 -> 402
    //   466: astore 7
    //   468: iload_3
    //   469: istore_2
    //   470: goto -290 -> 180
    //   473: aload_1
    //   474: aconst_null
    //   475: ldc_w 1305
    //   478: invokeinterface 1184 3 0
    //   483: astore 5
    //   485: aload 5
    //   487: ifnull -216 -> 271
    //   490: aload 4
    //   492: aload 5
    //   494: invokestatic 1308	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   497: invokevirtual 1040	com/android/server/pm/PackageSetting:setTimeStamp	(J)V
    //   500: goto -229 -> 271
    //   503: astore 5
    //   505: goto -234 -> 271
    //   508: iconst_0
    //   509: istore_2
    //   510: goto -158 -> 352
    //   513: iconst_0
    //   514: istore_2
    //   515: goto -126 -> 389
    //   518: aload_1
    //   519: invokeinterface 1180 1 0
    //   524: ldc -128
    //   526: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   529: ifeq +50 -> 579
    //   532: aload_1
    //   533: aconst_null
    //   534: ldc 71
    //   536: invokeinterface 1184 3 0
    //   541: astore 5
    //   543: aload 4
    //   545: getfield 979	com/android/server/pm/PackageSetting:childPackageNames	Ljava/util/List;
    //   548: ifnonnull +15 -> 563
    //   551: aload 4
    //   553: new 385	java/util/ArrayList
    //   556: dup
    //   557: invokespecial 386	java/util/ArrayList:<init>	()V
    //   560: putfield 979	com/android/server/pm/PackageSetting:childPackageNames	Ljava/util/List;
    //   563: aload 4
    //   565: getfield 979	com/android/server/pm/PackageSetting:childPackageNames	Ljava/util/List;
    //   568: aload 5
    //   570: invokeinterface 1309 2 0
    //   575: pop
    //   576: goto -174 -> 402
    //   579: iconst_5
    //   580: new 413	java/lang/StringBuilder
    //   583: dup
    //   584: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   587: ldc_w 1311
    //   590: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   593: aload_1
    //   594: invokeinterface 1180 1 0
    //   599: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   602: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   605: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   608: aload_1
    //   609: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   612: goto -210 -> 402
    //   615: aload_0
    //   616: getfield 368	com/android/server/pm/Settings:mDisabledSysPackages	Landroid/util/ArrayMap;
    //   619: aload 9
    //   621: aload 4
    //   623: invokevirtual 503	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   626: pop
    //   627: return
    //   628: astore 5
    //   630: goto -301 -> 329
    //   633: astore 5
    //   635: goto -335 -> 300
    //   638: astore 5
    //   640: goto -369 -> 271
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	643	0	this	Settings
    //   0	643	1	paramXmlPullParser	XmlPullParser
    //   168	347	2	i	int
    //   166	303	3	j	int
    //   121	501	4	localObject1	Object
    //   57	436	5	localObject2	Object
    //   503	1	5	localNumberFormatException1	NumberFormatException
    //   541	28	5	str1	String
    //   628	1	5	localNumberFormatException2	NumberFormatException
    //   633	1	5	localNumberFormatException3	NumberFormatException
    //   638	1	5	localNumberFormatException4	NumberFormatException
    //   33	180	6	localObject3	Object
    //   45	130	7	str2	String
    //   466	1	7	localNumberFormatException5	NumberFormatException
    //   93	36	8	str3	String
    //   9	611	9	str4	String
    //   21	190	10	str5	String
    //   69	155	11	str6	String
    //   81	154	12	str7	String
    //   105	123	13	str8	String
    //   117	113	14	str9	String
    // Exception table:
    //   from	to	target	type
    //   174	180	466	java/lang/NumberFormatException
    //   490	500	503	java/lang/NumberFormatException
    //   317	329	628	java/lang/NumberFormatException
    //   288	300	633	java/lang/NumberFormatException
    //   259	271	638	java/lang/NumberFormatException
  }
  
  private void readDomainVerificationLPw(XmlPullParser paramXmlPullParser, PackageSettingBase paramPackageSettingBase)
    throws XmlPullParserException, IOException
  {
    paramXmlPullParser = new IntentFilterVerificationInfo(paramXmlPullParser);
    paramPackageSettingBase.setIntentFilterVerificationInfo(paramXmlPullParser);
    Log.d("PackageSettings", "Read domain verification for package: " + paramXmlPullParser.getPackageName());
  }
  
  private void readEnabledComponentsLPw(PackageSettingBase paramPackageSettingBase, XmlPullParser paramXmlPullParser, int paramInt)
    throws IOException, XmlPullParserException
  {
    int i = paramXmlPullParser.getDepth();
    int j;
    do
    {
      j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
    } while ((j == 3) || (j == 4));
    if (paramXmlPullParser.getName().equals("item"))
    {
      String str = paramXmlPullParser.getAttributeValue(null, "name");
      if (str != null) {
        paramPackageSettingBase.addEnabledComponent(str.intern(), paramInt);
      }
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      break;
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <enabled-components> has no name at " + paramXmlPullParser.getPositionDescription());
      continue;
      PackageManagerService.reportSettingsProblem(5, "Unknown element under <enabled-components>: " + paramXmlPullParser.getName());
    }
  }
  
  private int readInt(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, int paramInt)
  {
    paramString1 = paramXmlPullParser.getAttributeValue(paramString1, paramString2);
    if (paramString1 == null) {
      return paramInt;
    }
    try
    {
      int i = Integer.parseInt(paramString1);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: attribute " + paramString2 + " has bad integer value " + paramString1 + " at " + paramXmlPullParser.getPositionDescription());
    }
    return paramInt;
  }
  
  /* Error */
  private void readPackageLPw(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 29
    //   3: aconst_null
    //   4: astore 19
    //   6: aconst_null
    //   7: astore 18
    //   9: aconst_null
    //   10: astore 21
    //   12: aconst_null
    //   13: astore 17
    //   15: aconst_null
    //   16: astore 34
    //   18: aconst_null
    //   19: astore 24
    //   21: aconst_null
    //   22: astore 25
    //   24: aconst_null
    //   25: astore 22
    //   27: iconst_0
    //   28: istore_2
    //   29: iconst_0
    //   30: istore 4
    //   32: iconst_0
    //   33: istore_3
    //   34: iconst_0
    //   35: istore 6
    //   37: lconst_0
    //   38: lstore 10
    //   40: lconst_0
    //   41: lstore 12
    //   43: lconst_0
    //   44: lstore 14
    //   46: iconst_0
    //   47: istore 7
    //   49: aload 18
    //   51: astore 33
    //   53: aload 21
    //   55: astore 16
    //   57: aload 17
    //   59: astore 32
    //   61: aload 19
    //   63: astore 31
    //   65: aload 34
    //   67: astore 30
    //   69: aload 24
    //   71: astore 28
    //   73: aload 22
    //   75: astore 27
    //   77: aload 25
    //   79: astore 26
    //   81: aload_1
    //   82: aconst_null
    //   83: ldc 71
    //   85: invokeinterface 1184 3 0
    //   90: astore 20
    //   92: aload 18
    //   94: astore 33
    //   96: aload 21
    //   98: astore 16
    //   100: aload 17
    //   102: astore 32
    //   104: aload 19
    //   106: astore 31
    //   108: aload 34
    //   110: astore 30
    //   112: aload 24
    //   114: astore 28
    //   116: aload 20
    //   118: astore 29
    //   120: aload 22
    //   122: astore 27
    //   124: aload 25
    //   126: astore 26
    //   128: aload_1
    //   129: aconst_null
    //   130: ldc_w 1254
    //   133: invokeinterface 1184 3 0
    //   138: astore 36
    //   140: aload 18
    //   142: astore 33
    //   144: aload 21
    //   146: astore 16
    //   148: aload 17
    //   150: astore 32
    //   152: aload 19
    //   154: astore 31
    //   156: aload 34
    //   158: astore 30
    //   160: aload 24
    //   162: astore 28
    //   164: aload 20
    //   166: astore 29
    //   168: aload 22
    //   170: astore 27
    //   172: aload 25
    //   174: astore 26
    //   176: aload_1
    //   177: aconst_null
    //   178: ldc_w 1297
    //   181: invokeinterface 1184 3 0
    //   186: astore 19
    //   188: aload 18
    //   190: astore 33
    //   192: aload 21
    //   194: astore 16
    //   196: aload 17
    //   198: astore 32
    //   200: aload 19
    //   202: astore 31
    //   204: aload 34
    //   206: astore 30
    //   208: aload 24
    //   210: astore 28
    //   212: aload 20
    //   214: astore 29
    //   216: aload 22
    //   218: astore 27
    //   220: aload 25
    //   222: astore 26
    //   224: aload_1
    //   225: aconst_null
    //   226: ldc_w 1335
    //   229: invokeinterface 1184 3 0
    //   234: astore 22
    //   236: aload 18
    //   238: astore 33
    //   240: aload 21
    //   242: astore 16
    //   244: aload 17
    //   246: astore 32
    //   248: aload 19
    //   250: astore 31
    //   252: aload 34
    //   254: astore 30
    //   256: aload 24
    //   258: astore 28
    //   260: aload 20
    //   262: astore 29
    //   264: aload 22
    //   266: astore 27
    //   268: aload 25
    //   270: astore 26
    //   272: aload_1
    //   273: aconst_null
    //   274: ldc_w 1299
    //   277: invokeinterface 1184 3 0
    //   282: astore 39
    //   284: aload 18
    //   286: astore 33
    //   288: aload 21
    //   290: astore 16
    //   292: aload 17
    //   294: astore 32
    //   296: aload 19
    //   298: astore 31
    //   300: aload 34
    //   302: astore 30
    //   304: aload 24
    //   306: astore 28
    //   308: aload 20
    //   310: astore 29
    //   312: aload 22
    //   314: astore 27
    //   316: aload 25
    //   318: astore 26
    //   320: aload_1
    //   321: aconst_null
    //   322: ldc_w 1255
    //   325: invokeinterface 1184 3 0
    //   330: astore 37
    //   332: aload 18
    //   334: astore 33
    //   336: aload 21
    //   338: astore 16
    //   340: aload 17
    //   342: astore 32
    //   344: aload 19
    //   346: astore 31
    //   348: aload 34
    //   350: astore 30
    //   352: aload 24
    //   354: astore 28
    //   356: aload 20
    //   358: astore 29
    //   360: aload 22
    //   362: astore 27
    //   364: aload 25
    //   366: astore 26
    //   368: aload_1
    //   369: aconst_null
    //   370: ldc_w 1257
    //   373: invokeinterface 1184 3 0
    //   378: astore 35
    //   380: aload 18
    //   382: astore 33
    //   384: aload 21
    //   386: astore 16
    //   388: aload 17
    //   390: astore 32
    //   392: aload 19
    //   394: astore 31
    //   396: aload 34
    //   398: astore 30
    //   400: aload 24
    //   402: astore 28
    //   404: aload 20
    //   406: astore 29
    //   408: aload 22
    //   410: astore 27
    //   412: aload 25
    //   414: astore 26
    //   416: aload_1
    //   417: aconst_null
    //   418: ldc_w 1259
    //   421: invokeinterface 1184 3 0
    //   426: astore 38
    //   428: aload 18
    //   430: astore 33
    //   432: aload 21
    //   434: astore 16
    //   436: aload 17
    //   438: astore 32
    //   440: aload 19
    //   442: astore 31
    //   444: aload 34
    //   446: astore 30
    //   448: aload 24
    //   450: astore 28
    //   452: aload 20
    //   454: astore 29
    //   456: aload 22
    //   458: astore 27
    //   460: aload 25
    //   462: astore 26
    //   464: aload_1
    //   465: aconst_null
    //   466: ldc_w 1263
    //   469: invokeinterface 1184 3 0
    //   474: astore 40
    //   476: aload 18
    //   478: astore 33
    //   480: aload 21
    //   482: astore 16
    //   484: aload 17
    //   486: astore 32
    //   488: aload 19
    //   490: astore 31
    //   492: aload 34
    //   494: astore 30
    //   496: aload 24
    //   498: astore 28
    //   500: aload 20
    //   502: astore 29
    //   504: aload 22
    //   506: astore 27
    //   508: aload 25
    //   510: astore 26
    //   512: aload_1
    //   513: aconst_null
    //   514: ldc_w 1261
    //   517: invokeinterface 1184 3 0
    //   522: astore 18
    //   524: aload 18
    //   526: astore 33
    //   528: aload 21
    //   530: astore 16
    //   532: aload 17
    //   534: astore 32
    //   536: aload 19
    //   538: astore 31
    //   540: aload 34
    //   542: astore 30
    //   544: aload 24
    //   546: astore 28
    //   548: aload 20
    //   550: astore 29
    //   552: aload 22
    //   554: astore 27
    //   556: aload 25
    //   558: astore 26
    //   560: aload_1
    //   561: aconst_null
    //   562: ldc_w 1265
    //   565: invokeinterface 1184 3 0
    //   570: astore 23
    //   572: aload 18
    //   574: astore 33
    //   576: aload 23
    //   578: astore 16
    //   580: aload 17
    //   582: astore 32
    //   584: aload 19
    //   586: astore 31
    //   588: aload 34
    //   590: astore 30
    //   592: aload 24
    //   594: astore 28
    //   596: aload 20
    //   598: astore 29
    //   600: aload 22
    //   602: astore 27
    //   604: aload 25
    //   606: astore 26
    //   608: aload_1
    //   609: aconst_null
    //   610: ldc_w 1267
    //   613: invokeinterface 1184 3 0
    //   618: astore 21
    //   620: aload 18
    //   622: astore 33
    //   624: aload 23
    //   626: astore 16
    //   628: aload 21
    //   630: astore 32
    //   632: aload 19
    //   634: astore 31
    //   636: aload 34
    //   638: astore 30
    //   640: aload 24
    //   642: astore 28
    //   644: aload 20
    //   646: astore 29
    //   648: aload 22
    //   650: astore 27
    //   652: aload 25
    //   654: astore 26
    //   656: aload_1
    //   657: aconst_null
    //   658: ldc_w 1269
    //   661: invokeinterface 1184 3 0
    //   666: astore 41
    //   668: aload 23
    //   670: astore 17
    //   672: aload 23
    //   674: ifnonnull +16 -> 690
    //   677: aload 23
    //   679: astore 17
    //   681: aload 38
    //   683: ifnull +7 -> 690
    //   686: aload 38
    //   688: astore 17
    //   690: aload 18
    //   692: astore 33
    //   694: aload 17
    //   696: astore 16
    //   698: aload 21
    //   700: astore 32
    //   702: aload 19
    //   704: astore 31
    //   706: aload 34
    //   708: astore 30
    //   710: aload 24
    //   712: astore 28
    //   714: aload 20
    //   716: astore 29
    //   718: aload 22
    //   720: astore 27
    //   722: aload 25
    //   724: astore 26
    //   726: aload_1
    //   727: aconst_null
    //   728: ldc -74
    //   730: invokeinterface 1184 3 0
    //   735: astore 23
    //   737: iload 7
    //   739: istore 5
    //   741: aload 23
    //   743: ifnull +10 -> 753
    //   746: aload 23
    //   748: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   751: istore 5
    //   753: aload 18
    //   755: astore 33
    //   757: aload 17
    //   759: astore 16
    //   761: aload 21
    //   763: astore 32
    //   765: aload 19
    //   767: astore 31
    //   769: aload 34
    //   771: astore 30
    //   773: aload 24
    //   775: astore 28
    //   777: aload 20
    //   779: astore 29
    //   781: aload 22
    //   783: astore 27
    //   785: aload 25
    //   787: astore 26
    //   789: aload_1
    //   790: aconst_null
    //   791: ldc_w 1337
    //   794: invokeinterface 1184 3 0
    //   799: astore 23
    //   801: aload 18
    //   803: astore 33
    //   805: aload 17
    //   807: astore 16
    //   809: aload 21
    //   811: astore 32
    //   813: aload 19
    //   815: astore 31
    //   817: aload 23
    //   819: astore 30
    //   821: aload 24
    //   823: astore 28
    //   825: aload 20
    //   827: astore 29
    //   829: aload 22
    //   831: astore 27
    //   833: aload 25
    //   835: astore 26
    //   837: aload_1
    //   838: aconst_null
    //   839: ldc_w 1339
    //   842: invokeinterface 1184 3 0
    //   847: astore 24
    //   849: aload 18
    //   851: astore 33
    //   853: aload 17
    //   855: astore 16
    //   857: aload 21
    //   859: astore 32
    //   861: aload 19
    //   863: astore 31
    //   865: aload 23
    //   867: astore 30
    //   869: aload 24
    //   871: astore 28
    //   873: aload 20
    //   875: astore 29
    //   877: aload 22
    //   879: astore 27
    //   881: aload 25
    //   883: astore 26
    //   885: aload_1
    //   886: aconst_null
    //   887: ldc 101
    //   889: invokeinterface 1184 3 0
    //   894: astore 25
    //   896: aload 18
    //   898: astore 33
    //   900: aload 17
    //   902: astore 16
    //   904: aload 21
    //   906: astore 32
    //   908: aload 19
    //   910: astore 31
    //   912: aload 23
    //   914: astore 30
    //   916: aload 24
    //   918: astore 28
    //   920: aload 20
    //   922: astore 29
    //   924: aload 22
    //   926: astore 27
    //   928: aload 25
    //   930: astore 26
    //   932: aload_1
    //   933: aconst_null
    //   934: ldc_w 1341
    //   937: invokeinterface 1184 3 0
    //   942: astore 34
    //   944: aload 34
    //   946: ifnull +855 -> 1801
    //   949: aload 34
    //   951: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   954: istore_2
    //   955: iload_2
    //   956: istore 4
    //   958: aload 18
    //   960: astore 33
    //   962: aload 17
    //   964: astore 16
    //   966: aload 21
    //   968: astore 32
    //   970: aload 19
    //   972: astore 31
    //   974: aload 23
    //   976: astore 30
    //   978: aload 24
    //   980: astore 28
    //   982: aload 20
    //   984: astore 29
    //   986: aload 22
    //   988: astore 27
    //   990: aload 25
    //   992: astore 26
    //   994: aload_1
    //   995: aconst_null
    //   996: ldc_w 1343
    //   999: invokeinterface 1184 3 0
    //   1004: astore 34
    //   1006: iload 4
    //   1008: istore_2
    //   1009: iload 6
    //   1011: istore_3
    //   1012: aload 34
    //   1014: ifnull +12 -> 1026
    //   1017: aload 34
    //   1019: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1022: istore_3
    //   1023: iload 4
    //   1025: istore_2
    //   1026: aload 18
    //   1028: astore 33
    //   1030: aload 17
    //   1032: astore 16
    //   1034: aload 21
    //   1036: astore 32
    //   1038: aload 19
    //   1040: astore 31
    //   1042: aload 23
    //   1044: astore 30
    //   1046: aload 24
    //   1048: astore 28
    //   1050: aload 20
    //   1052: astore 29
    //   1054: aload 22
    //   1056: astore 27
    //   1058: aload 25
    //   1060: astore 26
    //   1062: aload_1
    //   1063: aconst_null
    //   1064: ldc_w 1279
    //   1067: invokeinterface 1184 3 0
    //   1072: astore 34
    //   1074: aload 34
    //   1076: ifnull +1152 -> 2228
    //   1079: aload 34
    //   1081: bipush 16
    //   1083: invokestatic 1285	java/lang/Long:parseLong	(Ljava/lang/String;I)J
    //   1086: lstore 8
    //   1088: aload 18
    //   1090: astore 33
    //   1092: aload 17
    //   1094: astore 16
    //   1096: aload 21
    //   1098: astore 32
    //   1100: aload 19
    //   1102: astore 31
    //   1104: aload 23
    //   1106: astore 30
    //   1108: aload 24
    //   1110: astore 28
    //   1112: aload 20
    //   1114: astore 29
    //   1116: aload 22
    //   1118: astore 27
    //   1120: aload 25
    //   1122: astore 26
    //   1124: aload_1
    //   1125: aconst_null
    //   1126: ldc_w 1287
    //   1129: invokeinterface 1184 3 0
    //   1134: astore 34
    //   1136: lload 12
    //   1138: lstore 10
    //   1140: aload 34
    //   1142: ifnull +12 -> 1154
    //   1145: aload 34
    //   1147: bipush 16
    //   1149: invokestatic 1285	java/lang/Long:parseLong	(Ljava/lang/String;I)J
    //   1152: lstore 10
    //   1154: aload 18
    //   1156: astore 33
    //   1158: aload 17
    //   1160: astore 16
    //   1162: aload 21
    //   1164: astore 32
    //   1166: aload 19
    //   1168: astore 31
    //   1170: aload 23
    //   1172: astore 30
    //   1174: aload 24
    //   1176: astore 28
    //   1178: aload 20
    //   1180: astore 29
    //   1182: aload 22
    //   1184: astore 27
    //   1186: aload 25
    //   1188: astore 26
    //   1190: aload_1
    //   1191: aconst_null
    //   1192: ldc_w 1293
    //   1195: invokeinterface 1184 3 0
    //   1200: astore 34
    //   1202: lload 14
    //   1204: lstore 12
    //   1206: aload 34
    //   1208: ifnull +12 -> 1220
    //   1211: aload 34
    //   1213: bipush 16
    //   1215: invokestatic 1285	java/lang/Long:parseLong	(Ljava/lang/String;I)J
    //   1218: lstore 12
    //   1220: aload 18
    //   1222: astore 33
    //   1224: aload 17
    //   1226: astore 16
    //   1228: aload 21
    //   1230: astore 32
    //   1232: aload 19
    //   1234: astore 31
    //   1236: aload 23
    //   1238: astore 30
    //   1240: aload 24
    //   1242: astore 28
    //   1244: aload 20
    //   1246: astore 29
    //   1248: aload 22
    //   1250: astore 27
    //   1252: aload 25
    //   1254: astore 26
    //   1256: getstatic 1346	com/android/server/pm/PackageManagerService:DEBUG_SETTINGS	Z
    //   1259: ifeq +89 -> 1348
    //   1262: aload 18
    //   1264: astore 33
    //   1266: aload 17
    //   1268: astore 16
    //   1270: aload 21
    //   1272: astore 32
    //   1274: aload 19
    //   1276: astore 31
    //   1278: aload 23
    //   1280: astore 30
    //   1282: aload 24
    //   1284: astore 28
    //   1286: aload 20
    //   1288: astore 29
    //   1290: aload 22
    //   1292: astore 27
    //   1294: aload 25
    //   1296: astore 26
    //   1298: ldc_w 988
    //   1301: new 413	java/lang/StringBuilder
    //   1304: dup
    //   1305: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   1308: ldc_w 1348
    //   1311: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1314: aload 20
    //   1316: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1319: ldc_w 1350
    //   1322: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1325: aload 19
    //   1327: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1330: ldc_w 1352
    //   1333: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1336: aload 39
    //   1338: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1341: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1344: invokestatic 1011	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   1347: pop
    //   1348: aload 19
    //   1350: ifnull +972 -> 2322
    //   1353: aload 18
    //   1355: astore 33
    //   1357: aload 17
    //   1359: astore 16
    //   1361: aload 21
    //   1363: astore 32
    //   1365: aload 19
    //   1367: astore 31
    //   1369: aload 23
    //   1371: astore 30
    //   1373: aload 24
    //   1375: astore 28
    //   1377: aload 20
    //   1379: astore 29
    //   1381: aload 22
    //   1383: astore 27
    //   1385: aload 25
    //   1387: astore 26
    //   1389: aload 19
    //   1391: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1394: istore 4
    //   1396: goto +2721 -> 4117
    //   1399: aload 36
    //   1401: astore 35
    //   1403: aload 36
    //   1405: ifnull +46 -> 1451
    //   1408: aload 18
    //   1410: astore 33
    //   1412: aload 17
    //   1414: astore 16
    //   1416: aload 21
    //   1418: astore 32
    //   1420: aload 19
    //   1422: astore 31
    //   1424: aload 23
    //   1426: astore 30
    //   1428: aload 24
    //   1430: astore 28
    //   1432: aload 20
    //   1434: astore 29
    //   1436: aload 22
    //   1438: astore 27
    //   1440: aload 25
    //   1442: astore 26
    //   1444: aload 36
    //   1446: invokevirtual 1240	java/lang/String:intern	()Ljava/lang/String;
    //   1449: astore 35
    //   1451: aload 20
    //   1453: ifnonnull +875 -> 2328
    //   1456: aload 18
    //   1458: astore 33
    //   1460: aload 17
    //   1462: astore 16
    //   1464: aload 21
    //   1466: astore 32
    //   1468: aload 19
    //   1470: astore 31
    //   1472: aload 23
    //   1474: astore 30
    //   1476: aload 24
    //   1478: astore 28
    //   1480: aload 20
    //   1482: astore 29
    //   1484: aload 22
    //   1486: astore 27
    //   1488: aload 25
    //   1490: astore 26
    //   1492: iconst_5
    //   1493: new 413	java/lang/StringBuilder
    //   1496: dup
    //   1497: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   1500: ldc_w 1354
    //   1503: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1506: aload_1
    //   1507: invokeinterface 1233 1 0
    //   1512: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1515: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1518: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   1521: aconst_null
    //   1522: astore 16
    //   1524: aload 25
    //   1526: astore 36
    //   1528: aload 22
    //   1530: astore 35
    //   1532: aload 20
    //   1534: astore 32
    //   1536: aload 24
    //   1538: astore 33
    //   1540: aload 23
    //   1542: astore 31
    //   1544: aload 19
    //   1546: astore 29
    //   1548: aload 21
    //   1550: astore 30
    //   1552: aload 17
    //   1554: astore 28
    //   1556: aload 18
    //   1558: astore 27
    //   1560: aload 16
    //   1562: ifnull +2506 -> 4068
    //   1565: aload 16
    //   1567: ldc_w 1356
    //   1570: aload 35
    //   1572: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1575: putfield 1358	com/android/server/pm/PackageSettingBase:uidError	Z
    //   1578: aload 16
    //   1580: aload 31
    //   1582: putfield 1361	com/android/server/pm/PackageSettingBase:installerPackageName	Ljava/lang/String;
    //   1585: aload 16
    //   1587: ldc_w 1356
    //   1590: aload 33
    //   1592: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1595: putfield 1363	com/android/server/pm/PackageSettingBase:isOrphaned	Z
    //   1598: aload 16
    //   1600: aload 36
    //   1602: putfield 1365	com/android/server/pm/PackageSettingBase:volumeUuid	Ljava/lang/String;
    //   1605: aload 16
    //   1607: aload 27
    //   1609: putfield 1366	com/android/server/pm/PackageSettingBase:legacyNativeLibraryPathString	Ljava/lang/String;
    //   1612: aload 16
    //   1614: aload 28
    //   1616: putfield 1367	com/android/server/pm/PackageSettingBase:primaryCpuAbiString	Ljava/lang/String;
    //   1619: aload 16
    //   1621: aload 30
    //   1623: putfield 1368	com/android/server/pm/PackageSettingBase:secondaryCpuAbiString	Ljava/lang/String;
    //   1626: aload_1
    //   1627: aconst_null
    //   1628: ldc 47
    //   1630: invokeinterface 1184 3 0
    //   1635: astore 17
    //   1637: aload 17
    //   1639: ifnull +1947 -> 3586
    //   1642: aload 16
    //   1644: aload 17
    //   1646: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1649: iconst_0
    //   1650: aconst_null
    //   1651: invokevirtual 1372	com/android/server/pm/PackageSettingBase:setEnabled	(IILjava/lang/String;)V
    //   1654: aload 31
    //   1656: ifnull +13 -> 1669
    //   1659: aload_0
    //   1660: getfield 364	com/android/server/pm/Settings:mInstallerPackages	Landroid/util/ArraySet;
    //   1663: aload 31
    //   1665: invokevirtual 1185	android/util/ArraySet:add	(Ljava/lang/Object;)Z
    //   1668: pop
    //   1669: aload_1
    //   1670: aconst_null
    //   1671: ldc_w 1374
    //   1674: invokeinterface 1184 3 0
    //   1679: astore 17
    //   1681: aload 17
    //   1683: ifnull +20 -> 1703
    //   1686: aload 17
    //   1688: ldc_w 1376
    //   1691: invokevirtual 1380	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   1694: ifeq +1903 -> 3597
    //   1697: aload 16
    //   1699: iconst_0
    //   1700: putfield 1382	com/android/server/pm/PackageSettingBase:installStatus	I
    //   1703: aload_1
    //   1704: invokeinterface 1175 1 0
    //   1709: istore_2
    //   1710: aload_1
    //   1711: invokeinterface 1177 1 0
    //   1716: istore_3
    //   1717: iload_3
    //   1718: iconst_1
    //   1719: if_icmpeq +2353 -> 4072
    //   1722: iload_3
    //   1723: iconst_3
    //   1724: if_icmpne +13 -> 1737
    //   1727: aload_1
    //   1728: invokeinterface 1175 1 0
    //   1733: iload_2
    //   1734: if_icmple +2338 -> 4072
    //   1737: iload_3
    //   1738: iconst_3
    //   1739: if_icmpeq -29 -> 1710
    //   1742: iload_3
    //   1743: iconst_4
    //   1744: if_icmpeq -34 -> 1710
    //   1747: aload_1
    //   1748: invokeinterface 1180 1 0
    //   1753: astore 17
    //   1755: aload 17
    //   1757: ldc -113
    //   1759: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1762: ifeq +1844 -> 3606
    //   1765: aload_0
    //   1766: aload 16
    //   1768: aload_1
    //   1769: iconst_0
    //   1770: invokespecial 1384	com/android/server/pm/Settings:readDisabledComponentsLPw	(Lcom/android/server/pm/PackageSettingBase;Lorg/xmlpull/v1/XmlPullParser;I)V
    //   1773: goto -63 -> 1710
    //   1776: astore 16
    //   1778: iload 7
    //   1780: istore 5
    //   1782: goto -1029 -> 753
    //   1785: astore 16
    //   1787: goto -829 -> 958
    //   1790: astore 16
    //   1792: iload 4
    //   1794: istore_2
    //   1795: iload 6
    //   1797: istore_3
    //   1798: goto -772 -> 1026
    //   1801: aload 18
    //   1803: astore 33
    //   1805: aload 17
    //   1807: astore 16
    //   1809: aload 21
    //   1811: astore 32
    //   1813: aload 19
    //   1815: astore 31
    //   1817: aload 23
    //   1819: astore 30
    //   1821: aload 24
    //   1823: astore 28
    //   1825: aload 20
    //   1827: astore 29
    //   1829: aload 22
    //   1831: astore 27
    //   1833: aload 25
    //   1835: astore 26
    //   1837: aload_1
    //   1838: aconst_null
    //   1839: ldc 59
    //   1841: invokeinterface 1184 3 0
    //   1846: astore 34
    //   1848: aload 34
    //   1850: ifnull +273 -> 2123
    //   1853: aload 34
    //   1855: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   1858: istore 6
    //   1860: aload 18
    //   1862: astore 33
    //   1864: aload 17
    //   1866: astore 16
    //   1868: aload 21
    //   1870: astore 32
    //   1872: aload 19
    //   1874: astore 31
    //   1876: aload 23
    //   1878: astore 30
    //   1880: aload 24
    //   1882: astore 28
    //   1884: aload 20
    //   1886: astore 29
    //   1888: aload 22
    //   1890: astore 27
    //   1892: aload 25
    //   1894: astore 26
    //   1896: getstatic 270	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_HIDDEN	I
    //   1899: iload 6
    //   1901: iand
    //   1902: ifeq +5 -> 1907
    //   1905: iconst_1
    //   1906: istore_3
    //   1907: iload_3
    //   1908: istore_2
    //   1909: aload 18
    //   1911: astore 33
    //   1913: aload 17
    //   1915: astore 16
    //   1917: aload 21
    //   1919: astore 32
    //   1921: aload 19
    //   1923: astore 31
    //   1925: aload 23
    //   1927: astore 30
    //   1929: aload 24
    //   1931: astore 28
    //   1933: aload 20
    //   1935: astore 29
    //   1937: aload 22
    //   1939: astore 27
    //   1941: aload 25
    //   1943: astore 26
    //   1945: getstatic 273	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE	I
    //   1948: iload 6
    //   1950: iand
    //   1951: ifeq +7 -> 1958
    //   1954: iload_3
    //   1955: iconst_2
    //   1956: ior
    //   1957: istore_2
    //   1958: iload_2
    //   1959: istore 4
    //   1961: aload 18
    //   1963: astore 33
    //   1965: aload 17
    //   1967: astore 16
    //   1969: aload 21
    //   1971: astore 32
    //   1973: aload 19
    //   1975: astore 31
    //   1977: aload 23
    //   1979: astore 30
    //   1981: aload 24
    //   1983: astore 28
    //   1985: aload 20
    //   1987: astore 29
    //   1989: aload 22
    //   1991: astore 27
    //   1993: aload 25
    //   1995: astore 26
    //   1997: getstatic 276	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_FORWARD_LOCK	I
    //   2000: iload 6
    //   2002: iand
    //   2003: ifeq +8 -> 2011
    //   2006: iload_2
    //   2007: iconst_4
    //   2008: ior
    //   2009: istore 4
    //   2011: iload 4
    //   2013: istore_3
    //   2014: aload 18
    //   2016: astore 33
    //   2018: aload 17
    //   2020: astore 16
    //   2022: aload 21
    //   2024: astore 32
    //   2026: aload 19
    //   2028: astore 31
    //   2030: aload 23
    //   2032: astore 30
    //   2034: aload 24
    //   2036: astore 28
    //   2038: aload 20
    //   2040: astore 29
    //   2042: aload 22
    //   2044: astore 27
    //   2046: aload 25
    //   2048: astore 26
    //   2050: getstatic 279	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_PRIVILEGED	I
    //   2053: iload 6
    //   2055: iand
    //   2056: ifeq +9 -> 2065
    //   2059: iload 4
    //   2061: bipush 8
    //   2063: ior
    //   2064: istore_3
    //   2065: aload 18
    //   2067: astore 33
    //   2069: aload 17
    //   2071: astore 16
    //   2073: aload 21
    //   2075: astore 32
    //   2077: aload 19
    //   2079: astore 31
    //   2081: aload 23
    //   2083: astore 30
    //   2085: aload 24
    //   2087: astore 28
    //   2089: aload 20
    //   2091: astore 29
    //   2093: aload 22
    //   2095: astore 27
    //   2097: aload 25
    //   2099: astore 26
    //   2101: iload 6
    //   2103: getstatic 270	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_HIDDEN	I
    //   2106: getstatic 273	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE	I
    //   2109: ior
    //   2110: getstatic 276	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_FORWARD_LOCK	I
    //   2113: ior
    //   2114: getstatic 279	com/android/server/pm/Settings:PRE_M_APP_INFO_FLAG_PRIVILEGED	I
    //   2117: ior
    //   2118: iand
    //   2119: istore_2
    //   2120: goto -1094 -> 1026
    //   2123: aload 18
    //   2125: astore 33
    //   2127: aload 17
    //   2129: astore 16
    //   2131: aload 21
    //   2133: astore 32
    //   2135: aload 19
    //   2137: astore 31
    //   2139: aload 23
    //   2141: astore 30
    //   2143: aload 24
    //   2145: astore 28
    //   2147: aload 20
    //   2149: astore 29
    //   2151: aload 22
    //   2153: astore 27
    //   2155: aload 25
    //   2157: astore 26
    //   2159: aload_1
    //   2160: aconst_null
    //   2161: ldc_w 436
    //   2164: invokeinterface 1184 3 0
    //   2169: astore 34
    //   2171: aload 34
    //   2173: ifnull +1983 -> 4156
    //   2176: aload 18
    //   2178: astore 33
    //   2180: aload 17
    //   2182: astore 16
    //   2184: aload 21
    //   2186: astore 32
    //   2188: aload 19
    //   2190: astore 31
    //   2192: aload 23
    //   2194: astore 30
    //   2196: aload 24
    //   2198: astore 28
    //   2200: aload 20
    //   2202: astore 29
    //   2204: aload 22
    //   2206: astore 27
    //   2208: aload 25
    //   2210: astore 26
    //   2212: ldc_w 1356
    //   2215: aload 34
    //   2217: invokevirtual 1380	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   2220: ifeq +1931 -> 4151
    //   2223: iconst_1
    //   2224: istore_2
    //   2225: goto +1916 -> 4141
    //   2228: aload 18
    //   2230: astore 33
    //   2232: aload 17
    //   2234: astore 16
    //   2236: aload 21
    //   2238: astore 32
    //   2240: aload 19
    //   2242: astore 31
    //   2244: aload 23
    //   2246: astore 30
    //   2248: aload 24
    //   2250: astore 28
    //   2252: aload 20
    //   2254: astore 29
    //   2256: aload 22
    //   2258: astore 27
    //   2260: aload 25
    //   2262: astore 26
    //   2264: aload_1
    //   2265: aconst_null
    //   2266: ldc_w 1305
    //   2269: invokeinterface 1184 3 0
    //   2274: astore 34
    //   2276: lload 10
    //   2278: lstore 8
    //   2280: aload 34
    //   2282: ifnull -1194 -> 1088
    //   2285: aload 34
    //   2287: invokestatic 1308	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   2290: lstore 8
    //   2292: goto -1204 -> 1088
    //   2295: astore 16
    //   2297: lload 10
    //   2299: lstore 8
    //   2301: goto -1213 -> 1088
    //   2304: astore 16
    //   2306: lload 12
    //   2308: lstore 10
    //   2310: goto -1156 -> 1154
    //   2313: astore 16
    //   2315: lload 14
    //   2317: lstore 12
    //   2319: goto -1099 -> 1220
    //   2322: iconst_0
    //   2323: istore 4
    //   2325: goto +1792 -> 4117
    //   2328: aload 37
    //   2330: ifnonnull +110 -> 2440
    //   2333: aload 18
    //   2335: astore 33
    //   2337: aload 17
    //   2339: astore 16
    //   2341: aload 21
    //   2343: astore 32
    //   2345: aload 19
    //   2347: astore 31
    //   2349: aload 23
    //   2351: astore 30
    //   2353: aload 24
    //   2355: astore 28
    //   2357: aload 20
    //   2359: astore 29
    //   2361: aload 22
    //   2363: astore 27
    //   2365: aload 25
    //   2367: astore 26
    //   2369: iconst_5
    //   2370: new 413	java/lang/StringBuilder
    //   2373: dup
    //   2374: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   2377: ldc_w 1386
    //   2380: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2383: aload_1
    //   2384: invokeinterface 1233 1 0
    //   2389: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2392: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2395: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   2398: aconst_null
    //   2399: astore 16
    //   2401: aload 18
    //   2403: astore 27
    //   2405: aload 17
    //   2407: astore 28
    //   2409: aload 21
    //   2411: astore 30
    //   2413: aload 19
    //   2415: astore 29
    //   2417: aload 23
    //   2419: astore 31
    //   2421: aload 24
    //   2423: astore 33
    //   2425: aload 20
    //   2427: astore 32
    //   2429: aload 22
    //   2431: astore 35
    //   2433: aload 25
    //   2435: astore 36
    //   2437: goto -877 -> 1560
    //   2440: iload 4
    //   2442: ifle +416 -> 2858
    //   2445: aload 18
    //   2447: astore 33
    //   2449: aload 17
    //   2451: astore 16
    //   2453: aload 21
    //   2455: astore 32
    //   2457: aload 19
    //   2459: astore 31
    //   2461: aload 23
    //   2463: astore 30
    //   2465: aload 24
    //   2467: astore 28
    //   2469: aload 20
    //   2471: astore 29
    //   2473: aload 22
    //   2475: astore 27
    //   2477: aload 25
    //   2479: astore 26
    //   2481: aload_0
    //   2482: aload 20
    //   2484: invokevirtual 1240	java/lang/String:intern	()Ljava/lang/String;
    //   2487: aload 35
    //   2489: new 434	java/io/File
    //   2492: dup
    //   2493: aload 37
    //   2495: invokespecial 475	java/io/File:<init>	(Ljava/lang/String;)V
    //   2498: new 434	java/io/File
    //   2501: dup
    //   2502: aload 34
    //   2504: invokespecial 475	java/io/File:<init>	(Ljava/lang/String;)V
    //   2507: aload 18
    //   2509: aload 17
    //   2511: aload 21
    //   2513: aload 41
    //   2515: iload 4
    //   2517: iload 5
    //   2519: iload_2
    //   2520: iload_3
    //   2521: aload 40
    //   2523: aconst_null
    //   2524: invokevirtual 1390	com/android/server/pm/Settings:addPackageLPw	(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;Ljava/io/File;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIILjava/lang/String;Ljava/util/List;)Lcom/android/server/pm/PackageSetting;
    //   2527: astore 34
    //   2529: aload 34
    //   2531: astore 16
    //   2533: aload 16
    //   2535: astore 26
    //   2537: getstatic 1346	com/android/server/pm/PackageManagerService:DEBUG_SETTINGS	Z
    //   2540: ifeq +57 -> 2597
    //   2543: aload 16
    //   2545: astore 26
    //   2547: ldc_w 988
    //   2550: new 413	java/lang/StringBuilder
    //   2553: dup
    //   2554: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   2557: ldc_w 1392
    //   2560: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2563: aload 20
    //   2565: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2568: ldc_w 1394
    //   2571: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2574: iload 4
    //   2576: invokevirtual 590	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2579: ldc_w 1396
    //   2582: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2585: aload 16
    //   2587: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2590: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2593: invokestatic 1397	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   2596: pop
    //   2597: aload 16
    //   2599: ifnonnull +187 -> 2786
    //   2602: aload 16
    //   2604: astore 26
    //   2606: bipush 6
    //   2608: new 413	java/lang/StringBuilder
    //   2611: dup
    //   2612: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   2615: ldc_w 1399
    //   2618: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2621: iload 4
    //   2623: invokevirtual 590	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2626: ldc_w 1401
    //   2629: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2632: aload_1
    //   2633: invokeinterface 1233 1 0
    //   2638: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2641: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2644: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   2647: aload 18
    //   2649: astore 27
    //   2651: aload 17
    //   2653: astore 28
    //   2655: aload 21
    //   2657: astore 30
    //   2659: aload 19
    //   2661: astore 29
    //   2663: aload 23
    //   2665: astore 31
    //   2667: aload 24
    //   2669: astore 33
    //   2671: aload 20
    //   2673: astore 32
    //   2675: aload 22
    //   2677: astore 35
    //   2679: aload 25
    //   2681: astore 36
    //   2683: goto -1123 -> 1560
    //   2686: astore 16
    //   2688: aload 26
    //   2690: astore 34
    //   2692: iconst_5
    //   2693: new 413	java/lang/StringBuilder
    //   2696: dup
    //   2697: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   2700: ldc_w 1403
    //   2703: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2706: aload 20
    //   2708: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2711: ldc_w 1405
    //   2714: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2717: aload 19
    //   2719: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2722: ldc_w 1230
    //   2725: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2728: aload_1
    //   2729: invokeinterface 1233 1 0
    //   2734: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2737: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2740: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   2743: aload 34
    //   2745: astore 16
    //   2747: aload 18
    //   2749: astore 27
    //   2751: aload 17
    //   2753: astore 28
    //   2755: aload 21
    //   2757: astore 30
    //   2759: aload 19
    //   2761: astore 29
    //   2763: aload 23
    //   2765: astore 31
    //   2767: aload 24
    //   2769: astore 33
    //   2771: aload 20
    //   2773: astore 32
    //   2775: aload 22
    //   2777: astore 35
    //   2779: aload 25
    //   2781: astore 36
    //   2783: goto -1223 -> 1560
    //   2786: aload 16
    //   2788: astore 26
    //   2790: aload 16
    //   2792: lload 8
    //   2794: invokevirtual 1406	com/android/server/pm/PackageSettingBase:setTimeStamp	(J)V
    //   2797: aload 16
    //   2799: astore 26
    //   2801: aload 16
    //   2803: lload 10
    //   2805: putfield 1407	com/android/server/pm/PackageSettingBase:firstInstallTime	J
    //   2808: aload 16
    //   2810: astore 26
    //   2812: aload 16
    //   2814: lload 12
    //   2816: putfield 1408	com/android/server/pm/PackageSettingBase:lastUpdateTime	J
    //   2819: aload 18
    //   2821: astore 27
    //   2823: aload 17
    //   2825: astore 28
    //   2827: aload 21
    //   2829: astore 30
    //   2831: aload 19
    //   2833: astore 29
    //   2835: aload 23
    //   2837: astore 31
    //   2839: aload 24
    //   2841: astore 33
    //   2843: aload 20
    //   2845: astore 32
    //   2847: aload 22
    //   2849: astore 35
    //   2851: aload 25
    //   2853: astore 36
    //   2855: goto -1295 -> 1560
    //   2858: aload 39
    //   2860: ifnull +475 -> 3335
    //   2863: aload 39
    //   2865: ifnull +335 -> 3200
    //   2868: aload 18
    //   2870: astore 33
    //   2872: aload 17
    //   2874: astore 16
    //   2876: aload 21
    //   2878: astore 32
    //   2880: aload 19
    //   2882: astore 31
    //   2884: aload 23
    //   2886: astore 30
    //   2888: aload 24
    //   2890: astore 28
    //   2892: aload 20
    //   2894: astore 29
    //   2896: aload 22
    //   2898: astore 27
    //   2900: aload 25
    //   2902: astore 26
    //   2904: aload 39
    //   2906: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   2909: istore 4
    //   2911: iload 4
    //   2913: ifle +293 -> 3206
    //   2916: aload 18
    //   2918: astore 33
    //   2920: aload 17
    //   2922: astore 16
    //   2924: aload 21
    //   2926: astore 32
    //   2928: aload 19
    //   2930: astore 31
    //   2932: aload 23
    //   2934: astore 30
    //   2936: aload 24
    //   2938: astore 28
    //   2940: aload 20
    //   2942: astore 29
    //   2944: aload 22
    //   2946: astore 27
    //   2948: aload 25
    //   2950: astore 26
    //   2952: new 1410	com/android/server/pm/PendingPackage
    //   2955: dup
    //   2956: aload 20
    //   2958: invokevirtual 1240	java/lang/String:intern	()Ljava/lang/String;
    //   2961: aload 35
    //   2963: new 434	java/io/File
    //   2966: dup
    //   2967: aload 37
    //   2969: invokespecial 475	java/io/File:<init>	(Ljava/lang/String;)V
    //   2972: new 434	java/io/File
    //   2975: dup
    //   2976: aload 34
    //   2978: invokespecial 475	java/io/File:<init>	(Ljava/lang/String;)V
    //   2981: aload 18
    //   2983: aload 17
    //   2985: aload 21
    //   2987: aload 41
    //   2989: iload 4
    //   2991: iload 5
    //   2993: iload_2
    //   2994: iload_3
    //   2995: aload 40
    //   2997: aconst_null
    //   2998: invokespecial 1413	com/android/server/pm/PendingPackage:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;Ljava/io/File;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIILjava/lang/String;Ljava/util/List;)V
    //   3001: astore 34
    //   3003: aload 34
    //   3005: astore 26
    //   3007: aload 34
    //   3009: lload 8
    //   3011: invokevirtual 1406	com/android/server/pm/PackageSettingBase:setTimeStamp	(J)V
    //   3014: aload 34
    //   3016: astore 26
    //   3018: aload 34
    //   3020: lload 10
    //   3022: putfield 1407	com/android/server/pm/PackageSettingBase:firstInstallTime	J
    //   3025: aload 34
    //   3027: astore 26
    //   3029: aload 34
    //   3031: lload 12
    //   3033: putfield 1408	com/android/server/pm/PackageSettingBase:lastUpdateTime	J
    //   3036: aload 34
    //   3038: astore 26
    //   3040: aload_0
    //   3041: getfield 418	com/android/server/pm/Settings:mPendingPackages	Ljava/util/ArrayList;
    //   3044: aload 34
    //   3046: checkcast 1410	com/android/server/pm/PendingPackage
    //   3049: invokevirtual 604	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3052: pop
    //   3053: aload 34
    //   3055: astore 16
    //   3057: aload 18
    //   3059: astore 27
    //   3061: aload 17
    //   3063: astore 28
    //   3065: aload 21
    //   3067: astore 30
    //   3069: aload 19
    //   3071: astore 29
    //   3073: aload 23
    //   3075: astore 31
    //   3077: aload 24
    //   3079: astore 33
    //   3081: aload 20
    //   3083: astore 32
    //   3085: aload 22
    //   3087: astore 35
    //   3089: aload 25
    //   3091: astore 36
    //   3093: aload 34
    //   3095: astore 26
    //   3097: getstatic 1346	com/android/server/pm/PackageManagerService:DEBUG_SETTINGS	Z
    //   3100: ifeq -1540 -> 1560
    //   3103: aload 34
    //   3105: astore 26
    //   3107: ldc_w 988
    //   3110: new 413	java/lang/StringBuilder
    //   3113: dup
    //   3114: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   3117: ldc_w 1392
    //   3120: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3123: aload 20
    //   3125: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3128: ldc_w 1415
    //   3131: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3134: iload 4
    //   3136: invokevirtual 590	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   3139: ldc_w 1396
    //   3142: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3145: aload 34
    //   3147: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   3150: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3153: invokestatic 1397	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   3156: pop
    //   3157: aload 34
    //   3159: astore 16
    //   3161: aload 18
    //   3163: astore 27
    //   3165: aload 17
    //   3167: astore 28
    //   3169: aload 21
    //   3171: astore 30
    //   3173: aload 19
    //   3175: astore 29
    //   3177: aload 23
    //   3179: astore 31
    //   3181: aload 24
    //   3183: astore 33
    //   3185: aload 20
    //   3187: astore 32
    //   3189: aload 22
    //   3191: astore 35
    //   3193: aload 25
    //   3195: astore 36
    //   3197: goto -1637 -> 1560
    //   3200: iconst_0
    //   3201: istore 4
    //   3203: goto -292 -> 2911
    //   3206: aload 18
    //   3208: astore 33
    //   3210: aload 17
    //   3212: astore 16
    //   3214: aload 21
    //   3216: astore 32
    //   3218: aload 19
    //   3220: astore 31
    //   3222: aload 23
    //   3224: astore 30
    //   3226: aload 24
    //   3228: astore 28
    //   3230: aload 20
    //   3232: astore 29
    //   3234: aload 22
    //   3236: astore 27
    //   3238: aload 25
    //   3240: astore 26
    //   3242: iconst_5
    //   3243: new 413	java/lang/StringBuilder
    //   3246: dup
    //   3247: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   3250: ldc_w 1403
    //   3253: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3256: aload 20
    //   3258: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3261: ldc_w 1417
    //   3264: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3267: aload 39
    //   3269: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3272: ldc_w 1230
    //   3275: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3278: aload_1
    //   3279: invokeinterface 1233 1 0
    //   3284: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3287: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3290: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   3293: aconst_null
    //   3294: astore 16
    //   3296: aload 18
    //   3298: astore 27
    //   3300: aload 17
    //   3302: astore 28
    //   3304: aload 21
    //   3306: astore 30
    //   3308: aload 19
    //   3310: astore 29
    //   3312: aload 23
    //   3314: astore 31
    //   3316: aload 24
    //   3318: astore 33
    //   3320: aload 20
    //   3322: astore 32
    //   3324: aload 22
    //   3326: astore 35
    //   3328: aload 25
    //   3330: astore 36
    //   3332: goto -1772 -> 1560
    //   3335: aload 18
    //   3337: astore 33
    //   3339: aload 17
    //   3341: astore 16
    //   3343: aload 21
    //   3345: astore 32
    //   3347: aload 19
    //   3349: astore 31
    //   3351: aload 23
    //   3353: astore 30
    //   3355: aload 24
    //   3357: astore 28
    //   3359: aload 20
    //   3361: astore 29
    //   3363: aload 22
    //   3365: astore 27
    //   3367: aload 25
    //   3369: astore 26
    //   3371: iconst_5
    //   3372: new 413	java/lang/StringBuilder
    //   3375: dup
    //   3376: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   3379: ldc_w 1403
    //   3382: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3385: aload 20
    //   3387: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3390: ldc_w 1405
    //   3393: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3396: aload 19
    //   3398: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3401: ldc_w 1230
    //   3404: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3407: aload_1
    //   3408: invokeinterface 1233 1 0
    //   3413: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3416: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3419: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   3422: aconst_null
    //   3423: astore 16
    //   3425: aload 18
    //   3427: astore 27
    //   3429: aload 17
    //   3431: astore 28
    //   3433: aload 21
    //   3435: astore 30
    //   3437: aload 19
    //   3439: astore 29
    //   3441: aload 23
    //   3443: astore 31
    //   3445: aload 24
    //   3447: astore 33
    //   3449: aload 20
    //   3451: astore 32
    //   3453: aload 22
    //   3455: astore 35
    //   3457: aload 25
    //   3459: astore 36
    //   3461: goto -1901 -> 1560
    //   3464: astore 18
    //   3466: aload 17
    //   3468: ldc_w 1356
    //   3471: invokevirtual 1380	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   3474: ifeq +14 -> 3488
    //   3477: aload 16
    //   3479: iconst_1
    //   3480: iconst_0
    //   3481: aconst_null
    //   3482: invokevirtual 1372	com/android/server/pm/PackageSettingBase:setEnabled	(IILjava/lang/String;)V
    //   3485: goto -1831 -> 1654
    //   3488: aload 17
    //   3490: ldc_w 1376
    //   3493: invokevirtual 1380	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   3496: ifeq +14 -> 3510
    //   3499: aload 16
    //   3501: iconst_2
    //   3502: iconst_0
    //   3503: aconst_null
    //   3504: invokevirtual 1372	com/android/server/pm/PackageSettingBase:setEnabled	(IILjava/lang/String;)V
    //   3507: goto -1853 -> 1654
    //   3510: aload 17
    //   3512: ldc_w 1419
    //   3515: invokevirtual 1380	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   3518: ifeq +14 -> 3532
    //   3521: aload 16
    //   3523: iconst_0
    //   3524: iconst_0
    //   3525: aconst_null
    //   3526: invokevirtual 1372	com/android/server/pm/PackageSettingBase:setEnabled	(IILjava/lang/String;)V
    //   3529: goto -1875 -> 1654
    //   3532: iconst_5
    //   3533: new 413	java/lang/StringBuilder
    //   3536: dup
    //   3537: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   3540: ldc_w 1403
    //   3543: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3546: aload 32
    //   3548: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3551: ldc_w 1421
    //   3554: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3557: aload 29
    //   3559: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3562: ldc_w 1230
    //   3565: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3568: aload_1
    //   3569: invokeinterface 1233 1 0
    //   3574: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3577: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3580: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   3583: goto -1929 -> 1654
    //   3586: aload 16
    //   3588: iconst_0
    //   3589: iconst_0
    //   3590: aconst_null
    //   3591: invokevirtual 1372	com/android/server/pm/PackageSettingBase:setEnabled	(IILjava/lang/String;)V
    //   3594: goto -1940 -> 1654
    //   3597: aload 16
    //   3599: iconst_1
    //   3600: putfield 1382	com/android/server/pm/PackageSettingBase:installStatus	I
    //   3603: goto -1900 -> 1703
    //   3606: aload 17
    //   3608: ldc -107
    //   3610: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3613: ifeq +14 -> 3627
    //   3616: aload_0
    //   3617: aload 16
    //   3619: aload_1
    //   3620: iconst_0
    //   3621: invokespecial 1423	com/android/server/pm/Settings:readEnabledComponentsLPw	(Lcom/android/server/pm/PackageSettingBase;Lorg/xmlpull/v1/XmlPullParser;I)V
    //   3624: goto -1914 -> 1710
    //   3627: aload 17
    //   3629: ldc_w 1425
    //   3632: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3635: ifeq +19 -> 3654
    //   3638: aload 16
    //   3640: getfield 1426	com/android/server/pm/PackageSettingBase:signatures	Lcom/android/server/pm/PackageSignatures;
    //   3643: aload_1
    //   3644: aload_0
    //   3645: getfield 392	com/android/server/pm/Settings:mPastSignatures	Ljava/util/ArrayList;
    //   3648: invokevirtual 1430	com/android/server/pm/PackageSignatures:readXml	(Lorg/xmlpull/v1/XmlPullParser;Ljava/util/ArrayList;)V
    //   3651: goto -1941 -> 1710
    //   3654: aload 17
    //   3656: ldc -95
    //   3658: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3661: ifeq +22 -> 3683
    //   3664: aload_0
    //   3665: aload_1
    //   3666: aload 16
    //   3668: invokevirtual 1431	com/android/server/pm/PackageSettingBase:getPermissionsState	()Lcom/android/server/pm/PermissionsState;
    //   3671: invokevirtual 1303	com/android/server/pm/Settings:readInstallPermissionsLPr	(Lorg/xmlpull/v1/XmlPullParser;Lcom/android/server/pm/PermissionsState;)V
    //   3674: aload 16
    //   3676: iconst_1
    //   3677: putfield 1434	com/android/server/pm/PackageSettingBase:installPermissionsFixed	Z
    //   3680: goto -1970 -> 1710
    //   3683: aload 17
    //   3685: ldc_w 1436
    //   3688: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3691: ifeq +96 -> 3787
    //   3694: aload_1
    //   3695: aconst_null
    //   3696: ldc_w 1438
    //   3699: invokeinterface 1184 3 0
    //   3704: invokestatic 1308	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   3707: lstore 8
    //   3709: aload_0
    //   3710: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   3713: lload 8
    //   3715: invokestatic 1441	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3718: invokevirtual 560	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3721: checkcast 281	java/lang/Integer
    //   3724: astore 17
    //   3726: aload 17
    //   3728: ifnull +39 -> 3767
    //   3731: aload_0
    //   3732: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   3735: lload 8
    //   3737: invokestatic 1441	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3740: aload 17
    //   3742: invokevirtual 1160	java/lang/Integer:intValue	()I
    //   3745: iconst_1
    //   3746: iadd
    //   3747: invokestatic 285	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3750: invokevirtual 503	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3753: pop
    //   3754: aload 16
    //   3756: getfield 1445	com/android/server/pm/PackageSettingBase:keySetData	Lcom/android/server/pm/PackageKeySetData;
    //   3759: lload 8
    //   3761: invokevirtual 1450	com/android/server/pm/PackageKeySetData:setProperSigningKeySet	(J)V
    //   3764: goto -2054 -> 1710
    //   3767: aload_0
    //   3768: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   3771: lload 8
    //   3773: invokestatic 1441	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3776: iconst_1
    //   3777: invokestatic 285	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3780: invokevirtual 503	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3783: pop
    //   3784: goto -30 -> 3754
    //   3787: aload 17
    //   3789: ldc_w 1452
    //   3792: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3795: ifne -2085 -> 1710
    //   3798: aload 17
    //   3800: ldc_w 1454
    //   3803: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3806: ifeq +31 -> 3837
    //   3809: aload_1
    //   3810: aconst_null
    //   3811: ldc_w 1438
    //   3814: invokeinterface 1184 3 0
    //   3819: invokestatic 1308	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   3822: lstore 8
    //   3824: aload 16
    //   3826: getfield 1445	com/android/server/pm/PackageSettingBase:keySetData	Lcom/android/server/pm/PackageKeySetData;
    //   3829: lload 8
    //   3831: invokevirtual 1457	com/android/server/pm/PackageKeySetData:addUpgradeKeySetById	(J)V
    //   3834: goto -2124 -> 1710
    //   3837: aload 17
    //   3839: ldc_w 1459
    //   3842: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3845: ifeq +110 -> 3955
    //   3848: aload_1
    //   3849: aconst_null
    //   3850: ldc_w 1438
    //   3853: invokeinterface 1184 3 0
    //   3858: invokestatic 1308	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   3861: lstore 8
    //   3863: aload_1
    //   3864: aconst_null
    //   3865: ldc_w 1461
    //   3868: invokeinterface 1184 3 0
    //   3873: astore 17
    //   3875: aload_0
    //   3876: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   3879: lload 8
    //   3881: invokestatic 1441	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3884: invokevirtual 560	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3887: checkcast 281	java/lang/Integer
    //   3890: astore 18
    //   3892: aload 18
    //   3894: ifnull +41 -> 3935
    //   3897: aload_0
    //   3898: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   3901: lload 8
    //   3903: invokestatic 1441	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3906: aload 18
    //   3908: invokevirtual 1160	java/lang/Integer:intValue	()I
    //   3911: iconst_1
    //   3912: iadd
    //   3913: invokestatic 285	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3916: invokevirtual 503	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3919: pop
    //   3920: aload 16
    //   3922: getfield 1445	com/android/server/pm/PackageSettingBase:keySetData	Lcom/android/server/pm/PackageKeySetData;
    //   3925: lload 8
    //   3927: aload 17
    //   3929: invokevirtual 1465	com/android/server/pm/PackageKeySetData:addDefinedKeySet	(JLjava/lang/String;)V
    //   3932: goto -2222 -> 1710
    //   3935: aload_0
    //   3936: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   3939: lload 8
    //   3941: invokestatic 1441	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3944: iconst_1
    //   3945: invokestatic 285	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3948: invokevirtual 503	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   3951: pop
    //   3952: goto -32 -> 3920
    //   3955: aload 17
    //   3957: ldc -110
    //   3959: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3962: ifeq +13 -> 3975
    //   3965: aload_0
    //   3966: aload_1
    //   3967: aload 16
    //   3969: invokespecial 1467	com/android/server/pm/Settings:readDomainVerificationLPw	(Lorg/xmlpull/v1/XmlPullParser;Lcom/android/server/pm/PackageSettingBase;)V
    //   3972: goto -2262 -> 1710
    //   3975: aload 17
    //   3977: ldc -128
    //   3979: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3982: ifeq +50 -> 4032
    //   3985: aload_1
    //   3986: aconst_null
    //   3987: ldc 71
    //   3989: invokeinterface 1184 3 0
    //   3994: astore 17
    //   3996: aload 16
    //   3998: getfield 1468	com/android/server/pm/PackageSettingBase:childPackageNames	Ljava/util/List;
    //   4001: ifnonnull +15 -> 4016
    //   4004: aload 16
    //   4006: new 385	java/util/ArrayList
    //   4009: dup
    //   4010: invokespecial 386	java/util/ArrayList:<init>	()V
    //   4013: putfield 1468	com/android/server/pm/PackageSettingBase:childPackageNames	Ljava/util/List;
    //   4016: aload 16
    //   4018: getfield 1468	com/android/server/pm/PackageSettingBase:childPackageNames	Ljava/util/List;
    //   4021: aload 17
    //   4023: invokeinterface 1309 2 0
    //   4028: pop
    //   4029: goto -2319 -> 1710
    //   4032: iconst_5
    //   4033: new 413	java/lang/StringBuilder
    //   4036: dup
    //   4037: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   4040: ldc_w 1470
    //   4043: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4046: aload_1
    //   4047: invokeinterface 1180 1 0
    //   4052: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4055: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4058: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   4061: aload_1
    //   4062: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   4065: goto -2355 -> 1710
    //   4068: aload_1
    //   4069: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   4072: return
    //   4073: astore 17
    //   4075: aconst_null
    //   4076: astore 34
    //   4078: aload 33
    //   4080: astore 18
    //   4082: aload 16
    //   4084: astore 17
    //   4086: aload 32
    //   4088: astore 21
    //   4090: aload 31
    //   4092: astore 19
    //   4094: aload 30
    //   4096: astore 23
    //   4098: aload 28
    //   4100: astore 24
    //   4102: aload 29
    //   4104: astore 20
    //   4106: aload 27
    //   4108: astore 22
    //   4110: aload 26
    //   4112: astore 25
    //   4114: goto -1422 -> 2692
    //   4117: aload 35
    //   4119: astore 34
    //   4121: aload 35
    //   4123: ifnonnull -2724 -> 1399
    //   4126: aload 37
    //   4128: astore 34
    //   4130: goto -2731 -> 1399
    //   4133: astore 16
    //   4135: iload_2
    //   4136: istore 6
    //   4138: goto -2278 -> 1860
    //   4141: iload_2
    //   4142: iconst_0
    //   4143: ior
    //   4144: istore_2
    //   4145: iload 6
    //   4147: istore_3
    //   4148: goto -3122 -> 1026
    //   4151: iconst_0
    //   4152: istore_2
    //   4153: goto -12 -> 4141
    //   4156: iconst_1
    //   4157: istore_2
    //   4158: iload 6
    //   4160: istore_3
    //   4161: goto -3135 -> 1026
    //   4164: astore 16
    //   4166: lload 10
    //   4168: lstore 8
    //   4170: goto -3082 -> 1088
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	4173	0	this	Settings
    //   0	4173	1	paramXmlPullParser	XmlPullParser
    //   28	4130	2	i	int
    //   33	4128	3	j	int
    //   30	3172	4	k	int
    //   739	2253	5	m	int
    //   35	4124	6	n	int
    //   47	1732	7	i1	int
    //   1086	3083	8	l1	long
    //   38	4129	10	l2	long
    //   41	2991	12	l3	long
    //   44	2272	14	l4	long
    //   55	1712	16	localObject1	Object
    //   1776	1	16	localNumberFormatException1	NumberFormatException
    //   1785	1	16	localNumberFormatException2	NumberFormatException
    //   1790	1	16	localNumberFormatException3	NumberFormatException
    //   1807	428	16	localObject2	Object
    //   2295	1	16	localNumberFormatException4	NumberFormatException
    //   2304	1	16	localNumberFormatException5	NumberFormatException
    //   2313	1	16	localNumberFormatException6	NumberFormatException
    //   2339	264	16	localObject3	Object
    //   2686	1	16	localNumberFormatException7	NumberFormatException
    //   2745	1338	16	localObject4	Object
    //   4133	1	16	localNumberFormatException8	NumberFormatException
    //   4164	1	16	localNumberFormatException9	NumberFormatException
    //   13	4009	17	localObject5	Object
    //   4073	1	17	localNumberFormatException10	NumberFormatException
    //   4084	1	17	localObject6	Object
    //   7	3419	18	str1	String
    //   3464	1	18	localNumberFormatException11	NumberFormatException
    //   3890	191	18	localObject7	Object
    //   4	4089	19	localObject8	Object
    //   90	4015	20	localObject9	Object
    //   10	4079	21	localObject10	Object
    //   25	4084	22	localObject11	Object
    //   570	3527	23	localObject12	Object
    //   19	4082	24	localObject13	Object
    //   22	4091	25	localObject14	Object
    //   79	4032	26	localObject15	Object
    //   75	4032	27	localObject16	Object
    //   71	4028	28	localObject17	Object
    //   1	4102	29	localObject18	Object
    //   67	4028	30	localObject19	Object
    //   63	4028	31	localObject20	Object
    //   59	4028	32	localObject21	Object
    //   51	4028	33	localObject22	Object
    //   16	4113	34	localObject23	Object
    //   378	3744	35	localObject24	Object
    //   138	3322	36	localObject25	Object
    //   330	3797	37	str2	String
    //   426	261	38	str3	String
    //   282	2986	39	str4	String
    //   474	2522	40	str5	String
    //   666	2322	41	str6	String
    // Exception table:
    //   from	to	target	type
    //   746	753	1776	java/lang/NumberFormatException
    //   949	955	1785	java/lang/NumberFormatException
    //   1017	1023	1790	java/lang/NumberFormatException
    //   2285	2292	2295	java/lang/NumberFormatException
    //   1145	1154	2304	java/lang/NumberFormatException
    //   1211	1220	2313	java/lang/NumberFormatException
    //   2537	2543	2686	java/lang/NumberFormatException
    //   2547	2597	2686	java/lang/NumberFormatException
    //   2606	2647	2686	java/lang/NumberFormatException
    //   2790	2797	2686	java/lang/NumberFormatException
    //   2801	2808	2686	java/lang/NumberFormatException
    //   2812	2819	2686	java/lang/NumberFormatException
    //   3007	3014	2686	java/lang/NumberFormatException
    //   3018	3025	2686	java/lang/NumberFormatException
    //   3029	3036	2686	java/lang/NumberFormatException
    //   3040	3053	2686	java/lang/NumberFormatException
    //   3097	3103	2686	java/lang/NumberFormatException
    //   3107	3157	2686	java/lang/NumberFormatException
    //   1642	1654	3464	java/lang/NumberFormatException
    //   81	92	4073	java/lang/NumberFormatException
    //   128	140	4073	java/lang/NumberFormatException
    //   176	188	4073	java/lang/NumberFormatException
    //   224	236	4073	java/lang/NumberFormatException
    //   272	284	4073	java/lang/NumberFormatException
    //   320	332	4073	java/lang/NumberFormatException
    //   368	380	4073	java/lang/NumberFormatException
    //   416	428	4073	java/lang/NumberFormatException
    //   464	476	4073	java/lang/NumberFormatException
    //   512	524	4073	java/lang/NumberFormatException
    //   560	572	4073	java/lang/NumberFormatException
    //   608	620	4073	java/lang/NumberFormatException
    //   656	668	4073	java/lang/NumberFormatException
    //   726	737	4073	java/lang/NumberFormatException
    //   789	801	4073	java/lang/NumberFormatException
    //   837	849	4073	java/lang/NumberFormatException
    //   885	896	4073	java/lang/NumberFormatException
    //   932	944	4073	java/lang/NumberFormatException
    //   994	1006	4073	java/lang/NumberFormatException
    //   1062	1074	4073	java/lang/NumberFormatException
    //   1124	1136	4073	java/lang/NumberFormatException
    //   1190	1202	4073	java/lang/NumberFormatException
    //   1256	1262	4073	java/lang/NumberFormatException
    //   1298	1348	4073	java/lang/NumberFormatException
    //   1389	1396	4073	java/lang/NumberFormatException
    //   1444	1451	4073	java/lang/NumberFormatException
    //   1492	1521	4073	java/lang/NumberFormatException
    //   1837	1848	4073	java/lang/NumberFormatException
    //   1896	1905	4073	java/lang/NumberFormatException
    //   1945	1954	4073	java/lang/NumberFormatException
    //   1997	2006	4073	java/lang/NumberFormatException
    //   2050	2059	4073	java/lang/NumberFormatException
    //   2101	2120	4073	java/lang/NumberFormatException
    //   2159	2171	4073	java/lang/NumberFormatException
    //   2212	2223	4073	java/lang/NumberFormatException
    //   2264	2276	4073	java/lang/NumberFormatException
    //   2369	2398	4073	java/lang/NumberFormatException
    //   2481	2529	4073	java/lang/NumberFormatException
    //   2904	2911	4073	java/lang/NumberFormatException
    //   2952	3003	4073	java/lang/NumberFormatException
    //   3242	3293	4073	java/lang/NumberFormatException
    //   3371	3422	4073	java/lang/NumberFormatException
    //   1853	1860	4133	java/lang/NumberFormatException
    //   1079	1088	4164	java/lang/NumberFormatException
  }
  
  private void readPermissionsLPw(ArrayMap<String, BasePermission> paramArrayMap, XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    int j = paramXmlPullParser.getDepth();
    int i;
    do
    {
      i = paramXmlPullParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() <= j))) {
        break;
      }
    } while ((i == 3) || (i == 4));
    if (paramXmlPullParser.getName().equals("item"))
    {
      String str1 = paramXmlPullParser.getAttributeValue(null, "name");
      String str2 = paramXmlPullParser.getAttributeValue(null, "package");
      Object localObject1 = paramXmlPullParser.getAttributeValue(null, "type");
      if ((str1 != null) && (str2 != null))
      {
        boolean bool = "dynamic".equals(localObject1);
        Object localObject2 = (BasePermission)paramArrayMap.get(str1);
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (((BasePermission)localObject2).type == 1) {}
        }
        else
        {
          localObject1 = str1.intern();
          if (!bool) {
            break label300;
          }
          i = 2;
          label158:
          localObject1 = new BasePermission((String)localObject1, str2, i);
        }
        ((BasePermission)localObject1).protectionLevel = readInt(paramXmlPullParser, null, "protection", 0);
        ((BasePermission)localObject1).protectionLevel = PermissionInfo.fixProtectionLevel(((BasePermission)localObject1).protectionLevel);
        if (bool)
        {
          localObject2 = new PermissionInfo();
          ((PermissionInfo)localObject2).packageName = str2.intern();
          ((PermissionInfo)localObject2).name = str1.intern();
          ((PermissionInfo)localObject2).icon = readInt(paramXmlPullParser, null, "icon", 0);
          ((PermissionInfo)localObject2).nonLocalizedLabel = paramXmlPullParser.getAttributeValue(null, "label");
          ((PermissionInfo)localObject2).protectionLevel = ((BasePermission)localObject1).protectionLevel;
          ((BasePermission)localObject1).pendingInfo = ((PermissionInfo)localObject2);
        }
        paramArrayMap.put(((BasePermission)localObject1).name, localObject1);
      }
    }
    for (;;)
    {
      XmlUtils.skipCurrentTag(paramXmlPullParser);
      break;
      label300:
      i = 0;
      break label158;
      PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: permissions has no name at " + paramXmlPullParser.getPositionDescription());
      continue;
      PackageManagerService.reportSettingsProblem(5, "Unknown element reading permissions: " + paramXmlPullParser.getName() + " at " + paramXmlPullParser.getPositionDescription());
    }
  }
  
  private void readPersistentPreferredActivitiesLPw(XmlPullParser paramXmlPullParser, int paramInt)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if (paramXmlPullParser.getName().equals("item"))
        {
          PersistentPreferredActivity localPersistentPreferredActivity = new PersistentPreferredActivity(paramXmlPullParser);
          editPersistentPreferredActivitiesLPw(paramInt).addFilter(localPersistentPreferredActivity);
        }
        else
        {
          PackageManagerService.reportSettingsProblem(5, "Unknown element under <persistent-preferred-activities>: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  private void readRestoredIntentFilterVerifications(XmlPullParser paramXmlPullParser)
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
        Object localObject = paramXmlPullParser.getName();
        if (((String)localObject).equals("domain-verification"))
        {
          localObject = new IntentFilterVerificationInfo(paramXmlPullParser);
          if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
            Slog.i("PackageSettings", "Restored IVI for " + ((IntentFilterVerificationInfo)localObject).getPackageName() + " status=" + ((IntentFilterVerificationInfo)localObject).getStatusString());
          }
          this.mRestoredIntentFilterVerifications.put(((IntentFilterVerificationInfo)localObject).getPackageName(), localObject);
        }
        else
        {
          Slog.w("PackageSettings", "Unknown element: " + (String)localObject);
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  private void readSharedUserLPw(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject2 = null;
    String str3 = null;
    int j = 0;
    Object localObject4 = null;
    Object localObject3 = null;
    String str1 = str3;
    Object localObject1 = localObject4;
    for (;;)
    {
      try
      {
        str2 = paramXmlPullParser.getAttributeValue(null, "name");
        str1 = str3;
        localObject2 = str2;
        localObject1 = localObject4;
        str3 = paramXmlPullParser.getAttributeValue(null, "userId");
        if (str3 == null) {
          continue;
        }
        str1 = str3;
        localObject2 = str2;
        localObject1 = localObject4;
        i = Integer.parseInt(str3);
        str1 = str3;
        localObject2 = str2;
        localObject1 = localObject4;
        if ("true".equals(paramXmlPullParser.getAttributeValue(null, "system"))) {
          j = 1;
        }
        if (str2 != null) {
          continue;
        }
        str1 = str3;
        localObject2 = str2;
        localObject1 = localObject4;
        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <shared-user> has no name at " + paramXmlPullParser.getPositionDescription());
        localObject1 = localObject3;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        String str2;
        int i;
        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: package " + (String)localObject2 + " has bad userId " + str1 + " at " + paramXmlPullParser.getPositionDescription());
        continue;
        str1 = str3;
        localObject2 = str2;
        localObject1 = localObject4;
        SharedUserSetting localSharedUserSetting = addSharedUserLPw(str2.intern(), i, j, 0);
        localObject1 = localSharedUserSetting;
        if (localSharedUserSetting != null) {
          continue;
        }
        str1 = str3;
        localObject2 = str2;
        localObject1 = localSharedUserSetting;
        PackageManagerService.reportSettingsProblem(6, "Occurred while parsing settings at " + paramXmlPullParser.getPositionDescription());
        localObject1 = localSharedUserSetting;
        continue;
        if (!str1.equals("perms")) {
          continue;
        }
        readInstallPermissionsLPr(paramXmlPullParser, ((SharedUserSetting)localObject1).getPermissionsState());
        continue;
        PackageManagerService.reportSettingsProblem(5, "Unknown element under <shared-user>: " + paramXmlPullParser.getName());
        XmlUtils.skipCurrentTag(paramXmlPullParser);
        continue;
        XmlUtils.skipCurrentTag(paramXmlPullParser);
      }
      if (localObject1 == null) {
        continue;
      }
      i = paramXmlPullParser.getDepth();
      j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        return;
      }
      if ((j != 3) && (j != 4))
      {
        str1 = paramXmlPullParser.getName();
        if (!str1.equals("sigs")) {
          continue;
        }
        ((SharedUserSetting)localObject1).signatures.readXml(paramXmlPullParser, this.mPastSignatures);
        continue;
        i = 0;
        continue;
        if (i != 0) {
          continue;
        }
        str1 = str3;
        localObject2 = str2;
        localObject1 = localObject4;
        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: shared-user " + str2 + " has bad userId " + str3 + " at " + paramXmlPullParser.getPositionDescription());
        localObject1 = localObject3;
      }
    }
  }
  
  private void removeInstallerPackageStatus(String paramString)
  {
    if (!this.mInstallerPackages.contains(paramString)) {
      return;
    }
    int i = 0;
    while (i < this.mPackages.size())
    {
      PackageSetting localPackageSetting = (PackageSetting)this.mPackages.valueAt(i);
      String str = localPackageSetting.getInstallerPackageName();
      if ((str != null) && (str.equals(paramString)))
      {
        localPackageSetting.setInstallerPackageName(null);
        localPackageSetting.isOrphaned = true;
      }
      i += 1;
    }
    this.mInstallerPackages.remove(paramString);
  }
  
  private void removeUserIdLPw(int paramInt)
  {
    if (paramInt >= 10000)
    {
      int i = this.mUserIds.size();
      int j = paramInt - 10000;
      if (j < i) {
        this.mUserIds.set(j, null);
      }
    }
    for (;;)
    {
      setFirstAvailableUid(paramInt + 1);
      return;
      this.mOtherUserIds.remove(paramInt);
    }
  }
  
  private void replacePackageLPw(String paramString, PackageSetting paramPackageSetting)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting != null)
    {
      if (localPackageSetting.sharedUser == null) {
        break label50;
      }
      localPackageSetting.sharedUser.removePackage(localPackageSetting);
      localPackageSetting.sharedUser.addPackage(paramPackageSetting);
    }
    for (;;)
    {
      this.mPackages.put(paramString, paramPackageSetting);
      return;
      label50:
      replaceUserIdLPw(localPackageSetting.appId, paramPackageSetting);
    }
  }
  
  private void replaceUserIdLPw(int paramInt, Object paramObject)
  {
    if (paramInt >= 10000)
    {
      int i = this.mUserIds.size();
      paramInt -= 10000;
      if (paramInt < i) {
        this.mUserIds.set(paramInt, paramObject);
      }
      return;
    }
    this.mOtherUserIds.put(paramInt, paramObject);
  }
  
  private void setFirstAvailableUid(int paramInt)
  {
    if (paramInt > mFirstAvailableUid) {
      mFirstAvailableUid = paramInt;
    }
  }
  
  PackageSetting addPackageLPw(String paramString1, String paramString2, File paramFile1, File paramFile2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString7, List<String> paramList)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString1);
    if (localPackageSetting != null)
    {
      if (localPackageSetting.appId == paramInt1) {
        return localPackageSetting;
      }
      PackageManagerService.reportSettingsProblem(6, "Adding duplicate package, keeping first: " + paramString1);
      return null;
    }
    paramString2 = new PackageSetting(paramString1, paramString2, paramFile1, paramFile2, paramString3, paramString4, paramString5, paramString6, paramInt2, paramInt3, paramInt4, paramString7, paramList);
    paramString2.appId = paramInt1;
    if (addUserIdLPw(paramInt1, paramString2, paramString1))
    {
      this.mPackages.put(paramString1, paramString2);
      return paramString2;
    }
    return null;
  }
  
  void addPackageToCleanLPw(PackageCleanItem paramPackageCleanItem)
  {
    if (!this.mPackagesToBeCleaned.contains(paramPackageCleanItem)) {
      this.mPackagesToBeCleaned.add(paramPackageCleanItem);
    }
  }
  
  SharedUserSetting addSharedUserLPw(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    SharedUserSetting localSharedUserSetting = (SharedUserSetting)this.mSharedUsers.get(paramString);
    if (localSharedUserSetting != null)
    {
      if (localSharedUserSetting.userId == paramInt1) {
        return localSharedUserSetting;
      }
      PackageManagerService.reportSettingsProblem(6, "Adding duplicate shared user, keeping first: " + paramString);
      return null;
    }
    localSharedUserSetting = new SharedUserSetting(paramString, paramInt2, paramInt3);
    localSharedUserSetting.userId = paramInt1;
    if (addUserIdLPw(paramInt1, localSharedUserSetting, paramString))
    {
      this.mSharedUsers.put(paramString, localSharedUserSetting);
      return localSharedUserSetting;
    }
    return null;
  }
  
  /* Error */
  void applyDefaultPreferredAppsLPw(PackageManagerService paramPackageManagerService, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 359	com/android/server/pm/Settings:mPackages	Landroid/util/ArrayMap;
    //   4: invokevirtual 1598	android/util/ArrayMap:values	()Ljava/util/Collection;
    //   7: invokeinterface 709 1 0
    //   12: astore 6
    //   14: aload 6
    //   16: invokeinterface 714 1 0
    //   21: ifeq +111 -> 132
    //   24: aload 6
    //   26: invokeinterface 718 1 0
    //   31: checkcast 505	com/android/server/pm/PackageSetting
    //   34: astore 7
    //   36: aload 7
    //   38: getfield 986	com/android/server/pm/PackageSetting:pkgFlags	I
    //   41: iconst_1
    //   42: iand
    //   43: ifeq -29 -> 14
    //   46: aload 7
    //   48: getfield 1601	com/android/server/pm/PackageSetting:pkg	Landroid/content/pm/PackageParser$Package;
    //   51: ifnull -37 -> 14
    //   54: aload 7
    //   56: getfield 1601	com/android/server/pm/PackageSetting:pkg	Landroid/content/pm/PackageParser$Package;
    //   59: getfield 1604	android/content/pm/PackageParser$Package:preferredActivityFilters	Ljava/util/ArrayList;
    //   62: ifnull -48 -> 14
    //   65: aload 7
    //   67: getfield 1601	com/android/server/pm/PackageSetting:pkg	Landroid/content/pm/PackageParser$Package;
    //   70: getfield 1604	android/content/pm/PackageParser$Package:preferredActivityFilters	Ljava/util/ArrayList;
    //   73: astore 8
    //   75: iconst_0
    //   76: istore_3
    //   77: iload_3
    //   78: aload 8
    //   80: invokevirtual 600	java/util/ArrayList:size	()I
    //   83: if_icmpge -69 -> 14
    //   86: aload 8
    //   88: iload_3
    //   89: invokevirtual 606	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   92: checkcast 1606	android/content/pm/PackageParser$ActivityIntentInfo
    //   95: astore 9
    //   97: aload_0
    //   98: aload_1
    //   99: aload 9
    //   101: new 658	android/content/ComponentName
    //   104: dup
    //   105: aload 7
    //   107: getfield 517	com/android/server/pm/PackageSetting:name	Ljava/lang/String;
    //   110: aload 9
    //   112: getfield 1610	android/content/pm/PackageParser$ActivityIntentInfo:activity	Landroid/content/pm/PackageParser$Activity;
    //   115: getfield 1615	android/content/pm/PackageParser$Activity:className	Ljava/lang/String;
    //   118: invokespecial 673	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   121: iload_2
    //   122: invokespecial 1226	com/android/server/pm/Settings:applyDefaultPreferredActivityLPw	(Lcom/android/server/pm/PackageManagerService;Landroid/content/IntentFilter;Landroid/content/ComponentName;I)V
    //   125: iload_3
    //   126: iconst_1
    //   127: iadd
    //   128: istore_3
    //   129: goto -52 -> 77
    //   132: new 434	java/io/File
    //   135: dup
    //   136: invokestatic 1618	android/os/Environment:getRootDirectory	()Ljava/io/File;
    //   139: ldc_w 1620
    //   142: invokespecial 439	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   145: astore 10
    //   147: aload 10
    //   149: invokevirtual 478	java/io/File:exists	()Z
    //   152: ifeq +53 -> 205
    //   155: aload 10
    //   157: invokevirtual 1623	java/io/File:isDirectory	()Z
    //   160: ifeq +45 -> 205
    //   163: aload 10
    //   165: invokevirtual 1626	java/io/File:canRead	()Z
    //   168: ifne +38 -> 206
    //   171: ldc 122
    //   173: new 413	java/lang/StringBuilder
    //   176: dup
    //   177: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   180: ldc_w 1628
    //   183: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   186: aload 10
    //   188: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   191: ldc_w 1630
    //   194: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   200: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   203: pop
    //   204: return
    //   205: return
    //   206: aload 10
    //   208: invokevirtual 1634	java/io/File:listFiles	()[Ljava/io/File;
    //   211: astore 11
    //   213: iconst_0
    //   214: istore_3
    //   215: aload 11
    //   217: arraylength
    //   218: istore 4
    //   220: iload_3
    //   221: iload 4
    //   223: if_icmpge +500 -> 723
    //   226: aload 11
    //   228: iload_3
    //   229: aaload
    //   230: astore 12
    //   232: aload 12
    //   234: invokevirtual 1635	java/io/File:getPath	()Ljava/lang/String;
    //   237: ldc_w 1637
    //   240: invokevirtual 1640	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   243: ifne +54 -> 297
    //   246: ldc 122
    //   248: new 413	java/lang/StringBuilder
    //   251: dup
    //   252: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   255: ldc_w 1642
    //   258: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   261: aload 12
    //   263: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   266: ldc_w 1644
    //   269: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   272: aload 10
    //   274: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   277: ldc_w 1646
    //   280: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   283: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   286: invokestatic 578	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   289: pop
    //   290: iload_3
    //   291: iconst_1
    //   292: iadd
    //   293: istore_3
    //   294: goto -74 -> 220
    //   297: aload 12
    //   299: invokevirtual 1626	java/io/File:canRead	()Z
    //   302: ifne +39 -> 341
    //   305: ldc 122
    //   307: new 413	java/lang/StringBuilder
    //   310: dup
    //   311: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   314: ldc_w 1648
    //   317: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   320: aload 12
    //   322: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   325: ldc_w 1630
    //   328: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   331: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   334: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   337: pop
    //   338: goto -48 -> 290
    //   341: getstatic 644	com/android/server/pm/PackageManagerService:DEBUG_PREFERRED	Z
    //   344: ifeq +30 -> 374
    //   347: ldc 122
    //   349: new 413	java/lang/StringBuilder
    //   352: dup
    //   353: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   356: ldc_w 1650
    //   359: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   362: aload 12
    //   364: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   367: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   370: invokestatic 653	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   373: pop
    //   374: aconst_null
    //   375: astore 9
    //   377: aconst_null
    //   378: astore 6
    //   380: aconst_null
    //   381: astore 8
    //   383: new 1652	java/io/BufferedInputStream
    //   386: dup
    //   387: new 1654	java/io/FileInputStream
    //   390: dup
    //   391: aload 12
    //   393: invokespecial 1657	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   396: invokespecial 1660	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   399: astore 7
    //   401: invokestatic 1666	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   404: astore 6
    //   406: aload 6
    //   408: aload 7
    //   410: aconst_null
    //   411: invokeinterface 1670 3 0
    //   416: aload 6
    //   418: invokeinterface 1177 1 0
    //   423: istore 5
    //   425: iload 5
    //   427: iconst_2
    //   428: if_icmpeq +9 -> 437
    //   431: iload 5
    //   433: iconst_1
    //   434: if_icmpne -18 -> 416
    //   437: iload 5
    //   439: iconst_2
    //   440: if_icmpeq +54 -> 494
    //   443: ldc 122
    //   445: new 413	java/lang/StringBuilder
    //   448: dup
    //   449: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   452: ldc_w 1648
    //   455: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   458: aload 12
    //   460: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   463: ldc_w 1672
    //   466: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   469: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   472: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   475: pop
    //   476: aload 7
    //   478: ifnull -188 -> 290
    //   481: aload 7
    //   483: invokevirtual 1677	java/io/InputStream:close	()V
    //   486: goto -196 -> 290
    //   489: astore 6
    //   491: goto -201 -> 290
    //   494: ldc_w 1679
    //   497: aload 6
    //   499: invokeinterface 1180 1 0
    //   504: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   507: ifne +54 -> 561
    //   510: ldc 122
    //   512: new 413	java/lang/StringBuilder
    //   515: dup
    //   516: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   519: ldc_w 1648
    //   522: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   525: aload 12
    //   527: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   530: ldc_w 1681
    //   533: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   536: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   539: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   542: pop
    //   543: aload 7
    //   545: ifnull -255 -> 290
    //   548: aload 7
    //   550: invokevirtual 1677	java/io/InputStream:close	()V
    //   553: goto -263 -> 290
    //   556: astore 6
    //   558: goto -268 -> 290
    //   561: aload_0
    //   562: aload_1
    //   563: aload 6
    //   565: iload_2
    //   566: invokespecial 1683	com/android/server/pm/Settings:readDefaultPreferredActivitiesLPw	(Lcom/android/server/pm/PackageManagerService;Lorg/xmlpull/v1/XmlPullParser;I)V
    //   569: aload 7
    //   571: ifnull -281 -> 290
    //   574: aload 7
    //   576: invokevirtual 1677	java/io/InputStream:close	()V
    //   579: goto -289 -> 290
    //   582: astore 6
    //   584: goto -294 -> 290
    //   587: astore 6
    //   589: aload 8
    //   591: astore 7
    //   593: aload 6
    //   595: astore 8
    //   597: aload 7
    //   599: astore 6
    //   601: ldc 122
    //   603: new 413	java/lang/StringBuilder
    //   606: dup
    //   607: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   610: ldc_w 1685
    //   613: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   616: aload 12
    //   618: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   621: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   624: aload 8
    //   626: invokestatic 1688	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   629: pop
    //   630: aload 7
    //   632: ifnull -342 -> 290
    //   635: aload 7
    //   637: invokevirtual 1677	java/io/InputStream:close	()V
    //   640: goto -350 -> 290
    //   643: astore 6
    //   645: goto -355 -> 290
    //   648: astore 8
    //   650: aload 9
    //   652: astore 7
    //   654: aload 7
    //   656: astore 6
    //   658: ldc 122
    //   660: new 413	java/lang/StringBuilder
    //   663: dup
    //   664: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   667: ldc_w 1685
    //   670: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   673: aload 12
    //   675: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   678: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   681: aload 8
    //   683: invokestatic 1688	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   686: pop
    //   687: aload 7
    //   689: ifnull -399 -> 290
    //   692: aload 7
    //   694: invokevirtual 1677	java/io/InputStream:close	()V
    //   697: goto -407 -> 290
    //   700: astore 6
    //   702: goto -412 -> 290
    //   705: astore_1
    //   706: aload 6
    //   708: ifnull +8 -> 716
    //   711: aload 6
    //   713: invokevirtual 1677	java/io/InputStream:close	()V
    //   716: aload_1
    //   717: athrow
    //   718: astore 6
    //   720: goto -4 -> 716
    //   723: return
    //   724: astore_1
    //   725: aload 7
    //   727: astore 6
    //   729: goto -23 -> 706
    //   732: astore 8
    //   734: goto -80 -> 654
    //   737: astore 8
    //   739: goto -142 -> 597
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	742	0	this	Settings
    //   0	742	1	paramPackageManagerService	PackageManagerService
    //   0	742	2	paramInt	int
    //   76	218	3	i	int
    //   218	6	4	j	int
    //   423	18	5	k	int
    //   12	405	6	localObject1	Object
    //   489	9	6	localIOException1	IOException
    //   556	8	6	localIOException2	IOException
    //   582	1	6	localIOException3	IOException
    //   587	7	6	localIOException4	IOException
    //   599	1	6	localObject2	Object
    //   643	1	6	localIOException5	IOException
    //   656	1	6	localObject3	Object
    //   700	12	6	localIOException6	IOException
    //   718	1	6	localIOException7	IOException
    //   727	1	6	localObject4	Object
    //   34	692	7	localObject5	Object
    //   73	552	8	localObject6	Object
    //   648	34	8	localXmlPullParserException1	XmlPullParserException
    //   732	1	8	localXmlPullParserException2	XmlPullParserException
    //   737	1	8	localIOException8	IOException
    //   95	556	9	localActivityIntentInfo	android.content.pm.PackageParser.ActivityIntentInfo
    //   145	128	10	localFile1	File
    //   211	16	11	arrayOfFile	File[]
    //   230	444	12	localFile2	File
    // Exception table:
    //   from	to	target	type
    //   481	486	489	java/io/IOException
    //   548	553	556	java/io/IOException
    //   574	579	582	java/io/IOException
    //   383	401	587	java/io/IOException
    //   635	640	643	java/io/IOException
    //   383	401	648	org/xmlpull/v1/XmlPullParserException
    //   692	697	700	java/io/IOException
    //   383	401	705	finally
    //   601	630	705	finally
    //   658	687	705	finally
    //   711	716	718	java/io/IOException
    //   401	416	724	finally
    //   416	425	724	finally
    //   443	476	724	finally
    //   494	543	724	finally
    //   561	569	724	finally
    //   401	416	732	org/xmlpull/v1/XmlPullParserException
    //   416	425	732	org/xmlpull/v1/XmlPullParserException
    //   443	476	732	org/xmlpull/v1/XmlPullParserException
    //   494	543	732	org/xmlpull/v1/XmlPullParserException
    //   561	569	732	org/xmlpull/v1/XmlPullParserException
    //   401	416	737	java/io/IOException
    //   416	425	737	java/io/IOException
    //   443	476	737	java/io/IOException
    //   494	543	737	java/io/IOException
    //   561	569	737	java/io/IOException
  }
  
  void applyPendingPermissionGrantsLPw(String paramString, int paramInt)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mRestoredUserGrants.get(paramInt);
    if ((localArrayMap == null) || (localArrayMap.size() == 0)) {
      return;
    }
    Object localObject2 = (ArraySet)localArrayMap.get(paramString);
    if ((localObject2 == null) || (((ArraySet)localObject2).size() == 0)) {
      return;
    }
    Object localObject1 = (PackageSetting)this.mPackages.get(paramString);
    if (localObject1 == null)
    {
      Slog.e("PackageSettings", "Can't find supposedly installed package " + paramString);
      return;
    }
    localObject1 = ((PackageSetting)localObject1).getPermissionsState();
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      RestoredPermissionGrant localRestoredPermissionGrant = (RestoredPermissionGrant)((Iterator)localObject2).next();
      BasePermission localBasePermission = (BasePermission)this.mPermissions.get(localRestoredPermissionGrant.permissionName);
      if (localBasePermission != null)
      {
        if (localRestoredPermissionGrant.granted) {
          ((PermissionsState)localObject1).grantRuntimePermission(localBasePermission, paramInt);
        }
        ((PermissionsState)localObject1).updatePermissionFlags(localBasePermission, paramInt, 11, localRestoredPermissionGrant.grantBits);
      }
    }
    localArrayMap.remove(paramString);
    if (localArrayMap.size() < 1) {
      this.mRestoredUserGrants.remove(paramInt);
    }
    writeRuntimePermissionsForUserLPr(paramInt, false);
  }
  
  boolean areDefaultRuntimePermissionsGrantedLPr(int paramInt)
  {
    return this.mRuntimePermissionsPersistence.areDefaultRuntimPermissionsGrantedLPr(paramInt);
  }
  
  IntentFilterVerificationInfo createIntentFilterVerificationIfNeededLPw(String paramString, ArrayList<String> paramArrayList)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null)
    {
      if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
        Slog.w("PackageManager", "No package known: " + paramString);
      }
      return null;
    }
    IntentFilterVerificationInfo localIntentFilterVerificationInfo = localPackageSetting.getIntentFilterVerificationInfo();
    if (localIntentFilterVerificationInfo == null)
    {
      localIntentFilterVerificationInfo = new IntentFilterVerificationInfo(paramString, paramArrayList);
      localPackageSetting.setIntentFilterVerificationInfo(localIntentFilterVerificationInfo);
      paramArrayList = localIntentFilterVerificationInfo;
      if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION)
      {
        Slog.d("PackageManager", "Creating new IntentFilterVerificationInfo for pkg: " + paramString);
        paramArrayList = localIntentFilterVerificationInfo;
      }
    }
    do
    {
      return paramArrayList;
      localIntentFilterVerificationInfo.setDomains(paramArrayList);
      paramArrayList = localIntentFilterVerificationInfo;
    } while (!PackageManagerService.DEBUG_DOMAIN_VERIFICATION);
    Slog.d("PackageManager", "Setting domains to existing IntentFilterVerificationInfo for pkg: " + paramString + " and with domains: " + localIntentFilterVerificationInfo.getDomainsString());
    return localIntentFilterVerificationInfo;
  }
  
  void createNewUserLI(PackageManagerService paramPackageManagerService, Installer arg2, int paramInt)
  {
    for (;;)
    {
      int j;
      String[] arrayOfString1;
      String[] arrayOfString2;
      int[] arrayOfInt1;
      String[] arrayOfString3;
      int[] arrayOfInt2;
      synchronized (this.mPackages)
      {
        Object localObject = this.mPackages.values();
        j = ((Collection)localObject).size();
        arrayOfString1 = new String[j];
        arrayOfString2 = new String[j];
        arrayOfInt1 = new int[j];
        arrayOfString3 = new String[j];
        arrayOfInt2 = new int[j];
        localObject = ((Collection)localObject).iterator();
        i = 0;
        if (i >= j) {
          break label232;
        }
        PackageSetting localPackageSetting = (PackageSetting)((Iterator)localObject).next();
        if ((localPackageSetting.pkg == null) || (localPackageSetting.pkg.applicationInfo == null)) {
          break label359;
        }
        if (((localPackageSetting.pkgFlags & 0x1) == 0) && (!localPackageSetting.codePathString.startsWith("/system/reserve")))
        {
          bool = OemPackageManagerHelper.checkAppHasDeleted(localPackageSetting.name);
          localPackageSetting.setInstalled(bool, paramInt);
          arrayOfString1[i] = localPackageSetting.volumeUuid;
          arrayOfString2[i] = localPackageSetting.name;
          arrayOfInt1[i] = localPackageSetting.appId;
          arrayOfString3[i] = localPackageSetting.pkg.applicationInfo.seinfo;
          arrayOfInt2[i] = localPackageSetting.pkg.applicationInfo.targetSdkVersion;
        }
      }
      boolean bool = true;
      continue;
      label232:
      int i = 0;
      if (i < j)
      {
        if (arrayOfString2[i] == null) {}
        for (;;)
        {
          i += 1;
          break;
          try
          {
            ???.createAppData(arrayOfString1[i], arrayOfString2[i], paramInt, 3, arrayOfInt1[i], arrayOfString3[i], arrayOfInt2[i]);
          }
          catch (InstallerConnection.InstallerException localInstallerException)
          {
            Slog.w("PackageSettings", "Failed to prepare app data", localInstallerException);
          }
        }
      }
      synchronized (this.mPackages)
      {
        applyDefaultPreferredAppsLPw(paramPackageManagerService, paramInt);
        if (OpFeatures.isSupport(new int[] { 0 })) {
          paramPackageManagerService.setGMSEnabledSetting(new UserHandle(paramInt), 2);
        }
        return;
      }
      label359:
      i += 1;
    }
  }
  
  boolean disableSystemPackageLPw(String paramString, boolean paramBoolean)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null)
    {
      Log.w("PackageManager", "Package " + paramString + " is not an installed package");
      return false;
    }
    if (((PackageSetting)this.mDisabledSysPackages.get(paramString) != null) || (localPackageSetting.pkg == null) || (!localPackageSetting.pkg.isSystemApp()) || (localPackageSetting.pkg.isUpdatedSystemApp())) {
      return false;
    }
    if ((localPackageSetting.pkg != null) && (localPackageSetting.pkg.applicationInfo != null))
    {
      ApplicationInfo localApplicationInfo = localPackageSetting.pkg.applicationInfo;
      localApplicationInfo.flags |= 0x80;
    }
    this.mDisabledSysPackages.put(paramString, localPackageSetting);
    if (paramBoolean) {
      replacePackageLPw(paramString, new PackageSetting(localPackageSetting));
    }
    return true;
  }
  
  void dumpGidsLPr(PrintWriter paramPrintWriter, String paramString, int[] paramArrayOfInt)
  {
    if (!ArrayUtils.isEmpty(paramArrayOfInt))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("gids=");
      paramPrintWriter.println(PackageManagerService.arrayToString(paramArrayOfInt));
    }
  }
  
  void dumpInstallPermissionsLPr(PrintWriter paramPrintWriter, String paramString, ArraySet<String> paramArraySet, PermissionsState paramPermissionsState)
  {
    paramPermissionsState = paramPermissionsState.getInstallPermissionStates();
    if (!paramPermissionsState.isEmpty())
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("install permissions:");
      paramPermissionsState = paramPermissionsState.iterator();
      while (paramPermissionsState.hasNext())
      {
        PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)paramPermissionsState.next();
        if ((paramArraySet == null) || (paramArraySet.contains(localPermissionState.getName())))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  ");
          paramPrintWriter.print(localPermissionState.getName());
          paramPrintWriter.print(": granted=");
          paramPrintWriter.print(localPermissionState.isGranted());
          paramPrintWriter.println(permissionFlagsToString(", flags=", localPermissionState.getFlags()));
        }
      }
    }
  }
  
  void dumpPackageLPr(PrintWriter paramPrintWriter, String paramString1, String paramString2, ArraySet<String> paramArraySet, PackageSetting paramPackageSetting, SimpleDateFormat paramSimpleDateFormat, Date paramDate, List<UserInfo> paramList, boolean paramBoolean)
  {
    int i;
    if (paramString2 != null)
    {
      paramPrintWriter.print(paramString2);
      paramPrintWriter.print(",");
      if (paramPackageSetting.realName != null)
      {
        paramString1 = paramPackageSetting.realName;
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print(",");
        paramPrintWriter.print(paramPackageSetting.appId);
        paramPrintWriter.print(",");
        paramPrintWriter.print(paramPackageSetting.versionCode);
        paramPrintWriter.print(",");
        paramPrintWriter.print(paramPackageSetting.firstInstallTime);
        paramPrintWriter.print(",");
        paramPrintWriter.print(paramPackageSetting.lastUpdateTime);
        paramPrintWriter.print(",");
        if (paramPackageSetting.installerPackageName == null) {
          break label277;
        }
      }
      label277:
      for (paramString1 = paramPackageSetting.installerPackageName;; paramString1 = "?")
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.println();
        if (paramPackageSetting.pkg == null) {
          break label284;
        }
        paramPrintWriter.print(paramString2);
        paramPrintWriter.print("-");
        paramPrintWriter.print("splt,");
        paramPrintWriter.print("base,");
        paramPrintWriter.println(paramPackageSetting.pkg.baseRevisionCode);
        if (paramPackageSetting.pkg.splitNames == null) {
          break label284;
        }
        i = 0;
        while (i < paramPackageSetting.pkg.splitNames.length)
        {
          paramPrintWriter.print(paramString2);
          paramPrintWriter.print("-");
          paramPrintWriter.print("splt,");
          paramPrintWriter.print(paramPackageSetting.pkg.splitNames[i]);
          paramPrintWriter.print(",");
          paramPrintWriter.println(paramPackageSetting.pkg.splitRevisionCodes[i]);
          i += 1;
        }
        paramString1 = paramPackageSetting.name;
        break;
      }
      label284:
      paramArraySet = paramList.iterator();
      if (paramArraySet.hasNext())
      {
        paramSimpleDateFormat = (UserInfo)paramArraySet.next();
        paramPrintWriter.print(paramString2);
        paramPrintWriter.print("-");
        paramPrintWriter.print("usr");
        paramPrintWriter.print(",");
        paramPrintWriter.print(paramSimpleDateFormat.id);
        paramPrintWriter.print(",");
        if (paramPackageSetting.getInstalled(paramSimpleDateFormat.id))
        {
          paramString1 = "I";
          label374:
          paramPrintWriter.print(paramString1);
          if (!paramPackageSetting.getHidden(paramSimpleDateFormat.id)) {
            break label529;
          }
          paramString1 = "B";
          label396:
          paramPrintWriter.print(paramString1);
          if (!paramPackageSetting.getSuspended(paramSimpleDateFormat.id)) {
            break label536;
          }
          paramString1 = "SU";
          label418:
          paramPrintWriter.print(paramString1);
          if (!paramPackageSetting.getStopped(paramSimpleDateFormat.id)) {
            break label543;
          }
          paramString1 = "S";
          label440:
          paramPrintWriter.print(paramString1);
          if (!paramPackageSetting.getNotLaunched(paramSimpleDateFormat.id)) {
            break label550;
          }
          paramString1 = "l";
          label462:
          paramPrintWriter.print(paramString1);
          paramPrintWriter.print(",");
          paramPrintWriter.print(paramPackageSetting.getEnabled(paramSimpleDateFormat.id));
          paramString1 = paramPackageSetting.getLastDisabledAppCaller(paramSimpleDateFormat.id);
          paramPrintWriter.print(",");
          if (paramString1 == null) {
            break label557;
          }
        }
        for (;;)
        {
          paramPrintWriter.print(paramString1);
          paramPrintWriter.println();
          break;
          paramString1 = "i";
          break label374;
          label529:
          paramString1 = "b";
          break label396;
          label536:
          paramString1 = "su";
          break label418;
          label543:
          paramString1 = "s";
          break label440;
          label550:
          paramString1 = "L";
          break label462;
          label557:
          paramString1 = "?";
        }
      }
      return;
    }
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("Package [");
    PackageParser.Package localPackage;
    if (paramPackageSetting.realName != null)
    {
      paramString2 = paramPackageSetting.realName;
      paramPrintWriter.print(paramString2);
      paramPrintWriter.print("] (");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(paramPackageSetting)));
      paramPrintWriter.println("):");
      if (paramPackageSetting.realName != null)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  compat name=");
        paramPrintWriter.println(paramPackageSetting.name);
      }
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  userId=");
      paramPrintWriter.println(paramPackageSetting.appId);
      if (paramPackageSetting.sharedUser != null)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  sharedUser=");
        paramPrintWriter.println(paramPackageSetting.sharedUser);
      }
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  pkg=");
      paramPrintWriter.println(paramPackageSetting.pkg);
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  codePath=");
      paramPrintWriter.println(paramPackageSetting.codePathString);
      if (paramArraySet == null)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  resourcePath=");
        paramPrintWriter.println(paramPackageSetting.resourcePathString);
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  legacyNativeLibraryDir=");
        paramPrintWriter.println(paramPackageSetting.legacyNativeLibraryPathString);
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  primaryCpuAbi=");
        paramPrintWriter.println(paramPackageSetting.primaryCpuAbiString);
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  secondaryCpuAbi=");
        paramPrintWriter.println(paramPackageSetting.secondaryCpuAbiString);
      }
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  versionCode=");
      paramPrintWriter.print(paramPackageSetting.versionCode);
      if (paramPackageSetting.pkg != null)
      {
        paramPrintWriter.print(" minSdk=");
        paramPrintWriter.print(paramPackageSetting.pkg.applicationInfo.minSdkVersion);
        paramPrintWriter.print(" targetSdk=");
        paramPrintWriter.print(paramPackageSetting.pkg.applicationInfo.targetSdkVersion);
      }
      paramPrintWriter.println();
      if (paramPackageSetting.pkg == null) {
        break label2091;
      }
      if (paramPackageSetting.pkg.parentPackage == null) {
        break label1631;
      }
      localPackage = paramPackageSetting.pkg.parentPackage;
      paramString2 = (PackageSetting)this.mPackages.get(localPackage.packageName);
      if ((paramString2 == null) || (!paramString2.codePathString.equals(localPackage.codePath))) {
        break label1604;
      }
      label973:
      if (paramString2 != null)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  parentPackage=");
        if (paramString2.realName == null) {
          break label1623;
        }
        paramString2 = paramString2.realName;
        label1001:
        paramPrintWriter.println(paramString2);
      }
    }
    for (;;)
    {
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  versionName=");
      paramPrintWriter.println(paramPackageSetting.pkg.mVersionName);
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  splits=");
      dumpSplitNames(paramPrintWriter, paramPackageSetting.pkg);
      paramPrintWriter.println();
      i = PackageParser.getApkSigningVersion(paramPackageSetting.pkg);
      if (i != 0)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  apkSigningVersion=");
        paramPrintWriter.println(i);
      }
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  applicationInfo=");
      paramPrintWriter.println(paramPackageSetting.pkg.applicationInfo.toString());
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  flags=");
      printFlags(paramPrintWriter, paramPackageSetting.pkg.applicationInfo.flags, FLAG_DUMP_SPEC);
      paramPrintWriter.println();
      if (paramPackageSetting.pkg.applicationInfo.privateFlags != 0)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  privateFlags=");
        printFlags(paramPrintWriter, paramPackageSetting.pkg.applicationInfo.privateFlags, PRIVATE_FLAG_DUMP_SPEC);
        paramPrintWriter.println();
      }
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  dataDir=");
      paramPrintWriter.println(paramPackageSetting.pkg.applicationInfo.dataDir);
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  supportsScreens=[");
      int j = 1;
      if ((paramPackageSetting.pkg.applicationInfo.flags & 0x200) != 0)
      {
        if (1 == 0) {
          paramPrintWriter.print(", ");
        }
        j = 0;
        paramPrintWriter.print("small");
      }
      i = j;
      if ((paramPackageSetting.pkg.applicationInfo.flags & 0x400) != 0)
      {
        if (j == 0) {
          paramPrintWriter.print(", ");
        }
        i = 0;
        paramPrintWriter.print("medium");
      }
      j = i;
      if ((paramPackageSetting.pkg.applicationInfo.flags & 0x800) != 0)
      {
        if (i == 0) {
          paramPrintWriter.print(", ");
        }
        j = 0;
        paramPrintWriter.print("large");
      }
      i = j;
      if ((paramPackageSetting.pkg.applicationInfo.flags & 0x80000) != 0)
      {
        if (j == 0) {
          paramPrintWriter.print(", ");
        }
        i = 0;
        paramPrintWriter.print("xlarge");
      }
      j = i;
      if ((paramPackageSetting.pkg.applicationInfo.flags & 0x1000) != 0)
      {
        if (i == 0) {
          paramPrintWriter.print(", ");
        }
        j = 0;
        paramPrintWriter.print("resizeable");
      }
      if ((paramPackageSetting.pkg.applicationInfo.flags & 0x2000) != 0)
      {
        if (j == 0) {
          paramPrintWriter.print(", ");
        }
        paramPrintWriter.print("anyDensity");
      }
      paramPrintWriter.println("]");
      if ((paramPackageSetting.pkg.libraryNames == null) || (paramPackageSetting.pkg.libraryNames.size() <= 0)) {
        break label1809;
      }
      paramPrintWriter.print(paramString1);
      paramPrintWriter.println("  libraries:");
      i = 0;
      while (i < paramPackageSetting.pkg.libraryNames.size())
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("    ");
        paramPrintWriter.println((String)paramPackageSetting.pkg.libraryNames.get(i));
        i += 1;
      }
      paramString2 = paramPackageSetting.name;
      break;
      label1604:
      paramString2 = (PackageSetting)this.mDisabledSysPackages.get(localPackage.packageName);
      break label973;
      label1623:
      paramString2 = paramString2.name;
      break label1001;
      label1631:
      if (paramPackageSetting.pkg.childPackages != null)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("  childPackages=[");
        j = paramPackageSetting.pkg.childPackages.size();
        i = 0;
        if (i < j)
        {
          localPackage = (PackageParser.Package)paramPackageSetting.pkg.childPackages.get(i);
          paramString2 = (PackageSetting)this.mPackages.get(localPackage.packageName);
          if ((paramString2 != null) && (paramString2.codePathString.equals(localPackage.codePath))) {
            label1730:
            if (paramString2 != null)
            {
              if (i > 0) {
                paramPrintWriter.print(", ");
              }
              if (paramString2.realName == null) {
                break label1791;
              }
            }
          }
          label1791:
          for (paramString2 = paramString2.realName;; paramString2 = paramString2.name)
          {
            paramPrintWriter.print(paramString2);
            i += 1;
            break;
            paramString2 = (PackageSetting)this.mDisabledSysPackages.get(localPackage.packageName);
            break label1730;
          }
        }
        paramPrintWriter.println("]");
      }
    }
    label1809:
    if ((paramPackageSetting.pkg.usesLibraries != null) && (paramPackageSetting.pkg.usesLibraries.size() > 0))
    {
      paramPrintWriter.print(paramString1);
      paramPrintWriter.println("  usesLibraries:");
      i = 0;
      while (i < paramPackageSetting.pkg.usesLibraries.size())
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("    ");
        paramPrintWriter.println((String)paramPackageSetting.pkg.usesLibraries.get(i));
        i += 1;
      }
    }
    if ((paramPackageSetting.pkg.usesOptionalLibraries != null) && (paramPackageSetting.pkg.usesOptionalLibraries.size() > 0))
    {
      paramPrintWriter.print(paramString1);
      paramPrintWriter.println("  usesOptionalLibraries:");
      i = 0;
      while (i < paramPackageSetting.pkg.usesOptionalLibraries.size())
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("    ");
        paramPrintWriter.println((String)paramPackageSetting.pkg.usesOptionalLibraries.get(i));
        i += 1;
      }
    }
    if ((paramPackageSetting.pkg.usesLibraryFiles != null) && (paramPackageSetting.pkg.usesLibraryFiles.length > 0))
    {
      paramPrintWriter.print(paramString1);
      paramPrintWriter.println("  usesLibraryFiles:");
      i = 0;
      while (i < paramPackageSetting.pkg.usesLibraryFiles.length)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("    ");
        paramPrintWriter.println(paramPackageSetting.pkg.usesLibraryFiles[i]);
        i += 1;
      }
    }
    label2091:
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("  timeStamp=");
    paramDate.setTime(paramPackageSetting.timeStamp);
    paramPrintWriter.println(paramSimpleDateFormat.format(paramDate));
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("  firstInstallTime=");
    paramDate.setTime(paramPackageSetting.firstInstallTime);
    paramPrintWriter.println(paramSimpleDateFormat.format(paramDate));
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("  lastUpdateTime=");
    paramDate.setTime(paramPackageSetting.lastUpdateTime);
    paramPrintWriter.println(paramSimpleDateFormat.format(paramDate));
    if (paramPackageSetting.installerPackageName != null)
    {
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  installerPackageName=");
      paramPrintWriter.println(paramPackageSetting.installerPackageName);
    }
    if (paramPackageSetting.volumeUuid != null)
    {
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  volumeUuid=");
      paramPrintWriter.println(paramPackageSetting.volumeUuid);
    }
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("  signatures=");
    paramPrintWriter.println(paramPackageSetting.signatures);
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("  installPermissionsFixed=");
    paramPrintWriter.print(paramPackageSetting.installPermissionsFixed);
    paramPrintWriter.print(" installStatus=");
    paramPrintWriter.println(paramPackageSetting.installStatus);
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print("  pkgFlags=");
    printFlags(paramPrintWriter, paramPackageSetting.pkgFlags, FLAG_DUMP_SPEC);
    paramPrintWriter.println();
    if ((paramPackageSetting.pkg != null) && (paramPackageSetting.pkg.permissions != null) && (paramPackageSetting.pkg.permissions.size() > 0))
    {
      paramString2 = paramPackageSetting.pkg.permissions;
      paramPrintWriter.print(paramString1);
      paramPrintWriter.println("  declared permissions:");
      i = 0;
      while (i < paramString2.size())
      {
        paramSimpleDateFormat = (PackageParser.Permission)paramString2.get(i);
        if ((paramArraySet == null) || (paramArraySet.contains(paramSimpleDateFormat.info.name)))
        {
          paramPrintWriter.print(paramString1);
          paramPrintWriter.print("    ");
          paramPrintWriter.print(paramSimpleDateFormat.info.name);
          paramPrintWriter.print(": prot=");
          paramPrintWriter.print(PermissionInfo.protectionToString(paramSimpleDateFormat.info.protectionLevel));
          if ((paramSimpleDateFormat.info.flags & 0x1) != 0) {
            paramPrintWriter.print(", COSTS_MONEY");
          }
          if ((paramSimpleDateFormat.info.flags & 0x2) != 0) {
            paramPrintWriter.print(", HIDDEN");
          }
          if ((paramSimpleDateFormat.info.flags & 0x40000000) != 0) {
            paramPrintWriter.print(", INSTALLED");
          }
          paramPrintWriter.println();
        }
        i += 1;
      }
    }
    if (((paramArraySet != null) || (paramBoolean)) && (paramPackageSetting.pkg != null) && (paramPackageSetting.pkg.requestedPermissions != null) && (paramPackageSetting.pkg.requestedPermissions.size() > 0))
    {
      paramString2 = paramPackageSetting.pkg.requestedPermissions;
      paramPrintWriter.print(paramString1);
      paramPrintWriter.println("  requested permissions:");
      i = 0;
      while (i < paramString2.size())
      {
        paramSimpleDateFormat = (String)paramString2.get(i);
        if ((paramArraySet == null) || (paramArraySet.contains(paramSimpleDateFormat)))
        {
          paramPrintWriter.print(paramString1);
          paramPrintWriter.print("    ");
          paramPrintWriter.println(paramSimpleDateFormat);
        }
        i += 1;
      }
    }
    if ((paramPackageSetting.sharedUser == null) || (paramArraySet != null))
    {
      paramString2 = paramPackageSetting.getPermissionsState();
      dumpInstallPermissionsLPr(paramPrintWriter, paramString1 + "  ", paramArraySet, paramString2);
      label2729:
      paramString2 = paramList.iterator();
    }
    for (;;)
    {
      if (!paramString2.hasNext()) {
        return;
      }
      paramSimpleDateFormat = (UserInfo)paramString2.next();
      paramPrintWriter.print(paramString1);
      paramPrintWriter.print("  User ");
      paramPrintWriter.print(paramSimpleDateFormat.id);
      paramPrintWriter.print(": ");
      paramPrintWriter.print("ceDataInode=");
      paramPrintWriter.print(paramPackageSetting.getCeDataInode(paramSimpleDateFormat.id));
      paramPrintWriter.print(" installed=");
      paramPrintWriter.print(paramPackageSetting.getInstalled(paramSimpleDateFormat.id));
      paramPrintWriter.print(" hidden=");
      paramPrintWriter.print(paramPackageSetting.getHidden(paramSimpleDateFormat.id));
      paramPrintWriter.print(" suspended=");
      paramPrintWriter.print(paramPackageSetting.getSuspended(paramSimpleDateFormat.id));
      paramPrintWriter.print(" stopped=");
      paramPrintWriter.print(paramPackageSetting.getStopped(paramSimpleDateFormat.id));
      paramPrintWriter.print(" notLaunched=");
      paramPrintWriter.print(paramPackageSetting.getNotLaunched(paramSimpleDateFormat.id));
      paramPrintWriter.print(" enabled=");
      paramPrintWriter.println(paramPackageSetting.getEnabled(paramSimpleDateFormat.id));
      paramDate = paramPackageSetting.getLastDisabledAppCaller(paramSimpleDateFormat.id);
      if (paramDate != null)
      {
        paramPrintWriter.print(paramString1);
        paramPrintWriter.print("    lastDisabledCaller: ");
        paramPrintWriter.println(paramDate);
      }
      if (paramPackageSetting.sharedUser == null)
      {
        paramDate = paramPackageSetting.getPermissionsState();
        dumpGidsLPr(paramPrintWriter, paramString1 + "    ", paramDate.computeGids(paramSimpleDateFormat.id));
        dumpRuntimePermissionsLPr(paramPrintWriter, paramString1 + "    ", paramArraySet, paramDate.getRuntimePermissionStates(paramSimpleDateFormat.id), paramBoolean);
      }
      if (paramArraySet == null)
      {
        paramDate = paramPackageSetting.getDisabledComponents(paramSimpleDateFormat.id);
        if ((paramDate != null) && (paramDate.size() > 0))
        {
          paramPrintWriter.print(paramString1);
          paramPrintWriter.println("    disabledComponents:");
          paramDate = paramDate.iterator();
          for (;;)
          {
            if (paramDate.hasNext())
            {
              paramList = (String)paramDate.next();
              paramPrintWriter.print(paramString1);
              paramPrintWriter.print("      ");
              paramPrintWriter.println(paramList);
              continue;
              if (!paramBoolean) {
                break label2729;
              }
              break;
            }
          }
        }
        paramSimpleDateFormat = paramPackageSetting.getEnabledComponents(paramSimpleDateFormat.id);
        if ((paramSimpleDateFormat != null) && (paramSimpleDateFormat.size() > 0))
        {
          paramPrintWriter.print(paramString1);
          paramPrintWriter.println("    enabledComponents:");
          paramSimpleDateFormat = paramSimpleDateFormat.iterator();
          while (paramSimpleDateFormat.hasNext())
          {
            paramDate = (String)paramSimpleDateFormat.next();
            paramPrintWriter.print(paramString1);
            paramPrintWriter.print("      ");
            paramPrintWriter.println(paramDate);
          }
        }
      }
    }
  }
  
  void dumpPackagesLPr(PrintWriter paramPrintWriter, String paramString, ArraySet<String> paramArraySet, PackageManagerService.DumpState paramDumpState, boolean paramBoolean)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date localDate = new Date();
    int i = 0;
    List localList = getAllUsers();
    Iterator localIterator = this.mPackages.values().iterator();
    Object localObject;
    label82:
    int j;
    label135:
    String str;
    label144:
    boolean bool;
    if (localIterator.hasNext())
    {
      localObject = (PackageSetting)localIterator.next();
      if ((paramString == null) || (paramString.equals(((PackageSetting)localObject).realName)))
      {
        if ((paramArraySet != null) && (!((PackageSetting)localObject).getPermissionsState().hasRequestedPermission(paramArraySet))) {
          break label192;
        }
        if ((!paramBoolean) && (paramString != null)) {
          paramDumpState.setSharedUser(((PackageSetting)localObject).sharedUser);
        }
        j = i;
        if (!paramBoolean)
        {
          if (i == 0) {
            break label194;
          }
          j = i;
        }
        if (!paramBoolean) {
          break label219;
        }
        str = "pkg";
        if (paramString == null) {
          break label225;
        }
      }
      label192:
      label194:
      label219:
      label225:
      for (bool = true;; bool = false)
      {
        dumpPackageLPr(paramPrintWriter, "  ", str, paramArraySet, (PackageSetting)localObject, localSimpleDateFormat, localDate, localList, bool);
        i = j;
        break;
        if (!paramString.equals(((PackageSetting)localObject).name)) {
          break;
        }
        break label82;
        break;
        if (paramDumpState.onTitlePrinted()) {
          paramPrintWriter.println();
        }
        paramPrintWriter.println("Packages:");
        j = 1;
        break label135;
        str = null;
        break label144;
      }
    }
    i = 0;
    if ((this.mRenamedPackages.size() > 0) && (paramArraySet == null))
    {
      localIterator = this.mRenamedPackages.entrySet().iterator();
      if (localIterator.hasNext())
      {
        localObject = (Map.Entry)localIterator.next();
        if ((paramString == null) || (paramString.equals(((Map.Entry)localObject).getKey())))
        {
          label302:
          if (paramBoolean) {
            break label413;
          }
          j = i;
          if (i == 0)
          {
            if (paramDumpState.onTitlePrinted()) {
              paramPrintWriter.println();
            }
            paramPrintWriter.println("Renamed packages:");
            j = 1;
          }
          paramPrintWriter.print("  ");
          i = j;
          label349:
          paramPrintWriter.print((String)((Map.Entry)localObject).getKey());
          if (!paramBoolean) {
            break label423;
          }
        }
        label413:
        label423:
        for (str = " -> ";; str = ",")
        {
          paramPrintWriter.print(str);
          paramPrintWriter.println((String)((Map.Entry)localObject).getValue());
          break;
          if (!paramString.equals(((Map.Entry)localObject).getValue())) {
            break;
          }
          break label302;
          paramPrintWriter.print("ren,");
          break label349;
        }
      }
    }
    i = 0;
    if ((this.mDisabledSysPackages.size() > 0) && (paramArraySet == null))
    {
      localIterator = this.mDisabledSysPackages.values().iterator();
      if (localIterator.hasNext())
      {
        localObject = (PackageSetting)localIterator.next();
        if ((paramString == null) || (paramString.equals(((PackageSetting)localObject).realName)))
        {
          label500:
          j = i;
          if (!paramBoolean)
          {
            if (i == 0) {
              break label578;
            }
            j = i;
          }
          label518:
          if (!paramBoolean) {
            break label603;
          }
          str = "dis";
          label528:
          if (paramString == null) {
            break label609;
          }
        }
        label578:
        label603:
        label609:
        for (bool = true;; bool = false)
        {
          dumpPackageLPr(paramPrintWriter, "  ", str, paramArraySet, (PackageSetting)localObject, localSimpleDateFormat, localDate, localList, bool);
          i = j;
          break;
          if (!paramString.equals(((PackageSetting)localObject).name)) {
            break;
          }
          break label500;
          if (paramDumpState.onTitlePrinted()) {
            paramPrintWriter.println();
          }
          paramPrintWriter.println("Hidden system packages:");
          j = 1;
          break label518;
          str = null;
          break label528;
        }
      }
    }
  }
  
  void dumpPermissionsLPr(PrintWriter paramPrintWriter, String paramString, ArraySet<String> paramArraySet, PackageManagerService.DumpState paramDumpState)
  {
    int i = 0;
    Iterator localIterator = this.mPermissions.values().iterator();
    while (localIterator.hasNext())
    {
      BasePermission localBasePermission = (BasePermission)localIterator.next();
      if (((paramString == null) || (paramString.equals(localBasePermission.sourcePackage))) && ((paramArraySet == null) || (paramArraySet.contains(localBasePermission.name))))
      {
        int j = i;
        if (i == 0)
        {
          if (paramDumpState.onTitlePrinted()) {
            paramPrintWriter.println();
          }
          paramPrintWriter.println("Permissions:");
          j = 1;
        }
        paramPrintWriter.print("  Permission [");
        paramPrintWriter.print(localBasePermission.name);
        paramPrintWriter.print("] (");
        paramPrintWriter.print(Integer.toHexString(System.identityHashCode(localBasePermission)));
        paramPrintWriter.println("):");
        paramPrintWriter.print("    sourcePackage=");
        paramPrintWriter.println(localBasePermission.sourcePackage);
        paramPrintWriter.print("    uid=");
        paramPrintWriter.print(localBasePermission.uid);
        paramPrintWriter.print(" gids=");
        paramPrintWriter.print(Arrays.toString(localBasePermission.computeGids(0)));
        paramPrintWriter.print(" type=");
        paramPrintWriter.print(localBasePermission.type);
        paramPrintWriter.print(" prot=");
        paramPrintWriter.println(PermissionInfo.protectionToString(localBasePermission.protectionLevel));
        if (localBasePermission.perm != null)
        {
          paramPrintWriter.print("    perm=");
          paramPrintWriter.println(localBasePermission.perm);
          if (((localBasePermission.perm.info.flags & 0x40000000) == 0) || ((localBasePermission.perm.info.flags & 0x2) != 0))
          {
            paramPrintWriter.print("    flags=0x");
            paramPrintWriter.println(Integer.toHexString(localBasePermission.perm.info.flags));
          }
        }
        if (localBasePermission.packageSetting != null)
        {
          paramPrintWriter.print("    packageSetting=");
          paramPrintWriter.println(localBasePermission.packageSetting);
        }
        i = j;
        if ("android.permission.READ_EXTERNAL_STORAGE".equals(localBasePermission.name))
        {
          paramPrintWriter.print("    enforced=");
          paramPrintWriter.println(this.mReadExternalStorageEnforced);
          i = j;
        }
      }
    }
  }
  
  void dumpReadMessagesLPr(PrintWriter paramPrintWriter, PackageManagerService.DumpState paramDumpState)
  {
    paramPrintWriter.println("Settings parse messages:");
    paramPrintWriter.print(this.mReadMessages.toString());
  }
  
  void dumpRestoredPermissionGrantsLPr(PrintWriter paramPrintWriter, PackageManagerService.DumpState paramDumpState)
  {
    if (this.mRestoredUserGrants.size() > 0)
    {
      paramPrintWriter.println();
      paramPrintWriter.println("Restored (pending) permission grants:");
      int i = 0;
      while (i < this.mRestoredUserGrants.size())
      {
        paramDumpState = (ArrayMap)this.mRestoredUserGrants.valueAt(i);
        if ((paramDumpState != null) && (paramDumpState.size() > 0))
        {
          int j = this.mRestoredUserGrants.keyAt(i);
          paramPrintWriter.print("  User ");
          paramPrintWriter.println(j);
          j = 0;
          while (j < paramDumpState.size())
          {
            Object localObject1 = (ArraySet)paramDumpState.valueAt(j);
            if ((localObject1 != null) && (((ArraySet)localObject1).size() > 0))
            {
              Object localObject2 = (String)paramDumpState.keyAt(j);
              paramPrintWriter.print("    ");
              paramPrintWriter.print((String)localObject2);
              paramPrintWriter.println(" :");
              localObject1 = ((Iterable)localObject1).iterator();
              while (((Iterator)localObject1).hasNext())
              {
                localObject2 = (RestoredPermissionGrant)((Iterator)localObject1).next();
                paramPrintWriter.print("      ");
                paramPrintWriter.print(((RestoredPermissionGrant)localObject2).permissionName);
                if (((RestoredPermissionGrant)localObject2).granted) {
                  paramPrintWriter.print(" GRANTED");
                }
                if ((((RestoredPermissionGrant)localObject2).grantBits & 0x1) != 0) {
                  paramPrintWriter.print(" user_set");
                }
                if ((((RestoredPermissionGrant)localObject2).grantBits & 0x2) != 0) {
                  paramPrintWriter.print(" user_fixed");
                }
                if ((((RestoredPermissionGrant)localObject2).grantBits & 0x8) != 0) {
                  paramPrintWriter.print(" revoke_on_upgrade");
                }
                paramPrintWriter.println();
              }
            }
            j += 1;
          }
        }
        i += 1;
      }
      paramPrintWriter.println();
    }
  }
  
  void dumpRuntimePermissionsLPr(PrintWriter paramPrintWriter, String paramString, ArraySet<String> paramArraySet, List<PermissionsState.PermissionState> paramList, boolean paramBoolean)
  {
    if ((!paramList.isEmpty()) || (paramBoolean))
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.println("runtime permissions:");
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)paramList.next();
        if ((paramArraySet == null) || (paramArraySet.contains(localPermissionState.getName())))
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("  ");
          paramPrintWriter.print(localPermissionState.getName());
          paramPrintWriter.print(": granted=");
          paramPrintWriter.print(localPermissionState.isGranted());
          paramPrintWriter.println(permissionFlagsToString(", flags=", localPermissionState.getFlags()));
        }
      }
    }
  }
  
  void dumpSharedUsersLPr(PrintWriter paramPrintWriter, String paramString, ArraySet<String> paramArraySet, PackageManagerService.DumpState paramDumpState, boolean paramBoolean)
  {
    int k = 0;
    Iterator localIterator = this.mSharedUsers.values().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (SharedUserSetting)localIterator.next();
      if (((paramString == null) || (localObject1 == paramDumpState.getSharedUser())) && ((paramArraySet == null) || (((SharedUserSetting)localObject1).getPermissionsState().hasRequestedPermission(paramArraySet))))
      {
        if (!paramBoolean)
        {
          int i = k;
          if (k == 0)
          {
            if (paramDumpState.onTitlePrinted()) {
              paramPrintWriter.println();
            }
            paramPrintWriter.println("Shared users:");
            i = 1;
          }
          paramPrintWriter.print("  SharedUser [");
          paramPrintWriter.print(((SharedUserSetting)localObject1).name);
          paramPrintWriter.print("] (");
          paramPrintWriter.print(Integer.toHexString(System.identityHashCode(localObject1)));
          paramPrintWriter.println("):");
          paramPrintWriter.print("    ");
          paramPrintWriter.print("userId=");
          paramPrintWriter.println(((SharedUserSetting)localObject1).userId);
          localObject1 = ((SharedUserSetting)localObject1).getPermissionsState();
          dumpInstallPermissionsLPr(paramPrintWriter, "    ", paramArraySet, (PermissionsState)localObject1);
          int[] arrayOfInt = UserManagerService.getInstance().getUserIds();
          int m = arrayOfInt.length;
          int j = 0;
          List localList;
          for (;;)
          {
            k = i;
            if (j >= m) {
              break;
            }
            k = arrayOfInt[j];
            localObject2 = ((PermissionsState)localObject1).computeGids(k);
            localList = ((PermissionsState)localObject1).getRuntimePermissionStates(k);
            if ((!ArrayUtils.isEmpty((int[])localObject2)) || (!localList.isEmpty())) {
              break label267;
            }
            j += 1;
          }
          label267:
          paramPrintWriter.print("    ");
          paramPrintWriter.print("User ");
          paramPrintWriter.print(k);
          paramPrintWriter.println(": ");
          dumpGidsLPr(paramPrintWriter, "    " + "  ", (int[])localObject2);
          Object localObject2 = "    " + "  ";
          if (paramString != null) {}
          for (boolean bool = true;; bool = false)
          {
            dumpRuntimePermissionsLPr(paramPrintWriter, (String)localObject2, paramArraySet, localList, bool);
            break;
          }
        }
        paramPrintWriter.print("suid,");
        paramPrintWriter.print(((SharedUserSetting)localObject1).userId);
        paramPrintWriter.print(",");
        paramPrintWriter.println(((SharedUserSetting)localObject1).name);
      }
    }
  }
  
  void dumpVersionLPr(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.increaseIndent();
    int i = 0;
    if (i < this.mVersion.size())
    {
      String str = (String)this.mVersion.keyAt(i);
      VersionInfo localVersionInfo = (VersionInfo)this.mVersion.valueAt(i);
      if (Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, str)) {
        paramIndentingPrintWriter.println("Internal:");
      }
      for (;;)
      {
        paramIndentingPrintWriter.increaseIndent();
        paramIndentingPrintWriter.printPair("sdkVersion", Integer.valueOf(localVersionInfo.sdkVersion));
        paramIndentingPrintWriter.printPair("databaseVersion", Integer.valueOf(localVersionInfo.databaseVersion));
        paramIndentingPrintWriter.println();
        paramIndentingPrintWriter.printPair("fingerprint", localVersionInfo.fingerprint);
        paramIndentingPrintWriter.println();
        paramIndentingPrintWriter.decreaseIndent();
        i += 1;
        break;
        if (Objects.equals("primary_physical", str)) {
          paramIndentingPrintWriter.println("External:");
        } else {
          paramIndentingPrintWriter.println("UUID " + str + ":");
        }
      }
    }
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  CrossProfileIntentResolver editCrossProfileIntentResolverLPw(int paramInt)
  {
    CrossProfileIntentResolver localCrossProfileIntentResolver2 = (CrossProfileIntentResolver)this.mCrossProfileIntentResolvers.get(paramInt);
    CrossProfileIntentResolver localCrossProfileIntentResolver1 = localCrossProfileIntentResolver2;
    if (localCrossProfileIntentResolver2 == null)
    {
      localCrossProfileIntentResolver1 = new CrossProfileIntentResolver();
      this.mCrossProfileIntentResolvers.put(paramInt, localCrossProfileIntentResolver1);
    }
    return localCrossProfileIntentResolver1;
  }
  
  PersistentPreferredIntentResolver editPersistentPreferredActivitiesLPw(int paramInt)
  {
    PersistentPreferredIntentResolver localPersistentPreferredIntentResolver2 = (PersistentPreferredIntentResolver)this.mPersistentPreferredActivities.get(paramInt);
    PersistentPreferredIntentResolver localPersistentPreferredIntentResolver1 = localPersistentPreferredIntentResolver2;
    if (localPersistentPreferredIntentResolver2 == null)
    {
      localPersistentPreferredIntentResolver1 = new PersistentPreferredIntentResolver();
      this.mPersistentPreferredActivities.put(paramInt, localPersistentPreferredIntentResolver1);
    }
    return localPersistentPreferredIntentResolver1;
  }
  
  PreferredIntentResolver editPreferredActivitiesLPw(int paramInt)
  {
    PreferredIntentResolver localPreferredIntentResolver2 = (PreferredIntentResolver)this.mPreferredActivities.get(paramInt);
    PreferredIntentResolver localPreferredIntentResolver1 = localPreferredIntentResolver2;
    if (localPreferredIntentResolver2 == null)
    {
      localPreferredIntentResolver1 = new PreferredIntentResolver();
      this.mPreferredActivities.put(paramInt, localPreferredIntentResolver1);
    }
    return localPreferredIntentResolver1;
  }
  
  PackageSetting enableSystemPackageLPw(String paramString)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mDisabledSysPackages.get(paramString);
    if (localPackageSetting == null)
    {
      Log.w("PackageManager", "Package " + paramString + " is not disabled");
      return null;
    }
    if ((localPackageSetting.pkg != null) && (localPackageSetting.pkg.applicationInfo != null))
    {
      ApplicationInfo localApplicationInfo = localPackageSetting.pkg.applicationInfo;
      localApplicationInfo.flags &= 0xFF7F;
    }
    localPackageSetting = addPackageLPw(paramString, localPackageSetting.realName, localPackageSetting.codePath, localPackageSetting.resourcePath, localPackageSetting.legacyNativeLibraryPathString, localPackageSetting.primaryCpuAbiString, localPackageSetting.secondaryCpuAbiString, localPackageSetting.cpuAbiOverrideString, localPackageSetting.appId, localPackageSetting.versionCode, localPackageSetting.pkgFlags, localPackageSetting.pkgPrivateFlags, localPackageSetting.parentPackageName, localPackageSetting.childPackageNames);
    this.mDisabledSysPackages.remove(paramString);
    return localPackageSetting;
  }
  
  public VersionInfo findOrCreateVersion(String paramString)
  {
    VersionInfo localVersionInfo2 = (VersionInfo)this.mVersion.get(paramString);
    VersionInfo localVersionInfo1 = localVersionInfo2;
    if (localVersionInfo2 == null)
    {
      localVersionInfo1 = new VersionInfo();
      localVersionInfo1.forceCurrent();
      this.mVersion.put(paramString, localVersionInfo1);
    }
    return localVersionInfo1;
  }
  
  Collection<SharedUserSetting> getAllSharedUsersLPw()
  {
    return this.mSharedUsers.values();
  }
  
  List<UserInfo> getAllUsers()
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      List localList = UserManagerService.getInstance().getUsers(false);
      Binder.restoreCallingIdentity(l);
      return localList;
    }
    catch (NullPointerException localNullPointerException)
    {
      localNullPointerException = localNullPointerException;
      Binder.restoreCallingIdentity(l);
      return null;
    }
    finally
    {
      localObject = finally;
      Binder.restoreCallingIdentity(l);
      throw ((Throwable)localObject);
    }
  }
  
  int getApplicationEnabledSettingLPr(String paramString, int paramInt)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null) {
      throw new IllegalArgumentException("Unknown package: " + paramString);
    }
    return localPackageSetting.getEnabled(paramInt);
  }
  
  int getComponentEnabledSettingLPr(ComponentName paramComponentName, int paramInt)
  {
    Object localObject = paramComponentName.getPackageName();
    localObject = (PackageSetting)this.mPackages.get(localObject);
    if (localObject == null) {
      throw new IllegalArgumentException("Unknown component: " + paramComponentName);
    }
    return ((PackageSetting)localObject).getCurrentEnabledStateLPr(paramComponentName.getClassName(), paramInt);
  }
  
  String getDefaultBrowserPackageNameLPw(int paramInt)
  {
    if (paramInt == -1) {
      return null;
    }
    return (String)this.mDefaultBrowserApp.get(paramInt);
  }
  
  String getDefaultDialerPackageNameLPw(int paramInt)
  {
    if (paramInt == -1) {
      return null;
    }
    return (String)this.mDefaultDialerApp.get(paramInt);
  }
  
  public PackageSetting getDisabledSystemPkgLPr(String paramString)
  {
    return (PackageSetting)this.mDisabledSysPackages.get(paramString);
  }
  
  public VersionInfo getExternalVersion()
  {
    return (VersionInfo)this.mVersion.get("primary_physical");
  }
  
  String getInstallerPackageNameLPr(String paramString)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null) {
      throw new IllegalArgumentException("Unknown package: " + paramString);
    }
    return localPackageSetting.installerPackageName;
  }
  
  IntentFilterVerificationInfo getIntentFilterVerificationLPr(String paramString)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null)
    {
      if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
        Slog.w("PackageManager", "No package known: " + paramString);
      }
      return null;
    }
    return localPackageSetting.getIntentFilterVerificationInfo();
  }
  
  int getIntentFilterVerificationStatusLPr(String paramString, int paramInt)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null)
    {
      if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
        Slog.w("PackageManager", "No package known: " + paramString);
      }
      return 0;
    }
    return (int)(localPackageSetting.getDomainVerificationStatusForUser(paramInt) >> 32);
  }
  
  List<IntentFilterVerificationInfo> getIntentFilterVerificationsLPr(String paramString)
  {
    if (paramString == null) {
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mPackages.values().iterator();
    while (localIterator.hasNext())
    {
      IntentFilterVerificationInfo localIntentFilterVerificationInfo = ((PackageSetting)localIterator.next()).getIntentFilterVerificationInfo();
      if ((localIntentFilterVerificationInfo != null) && (!TextUtils.isEmpty(localIntentFilterVerificationInfo.getPackageName())) && (localIntentFilterVerificationInfo.getPackageName().equalsIgnoreCase(paramString))) {
        localArrayList.add(localIntentFilterVerificationInfo);
      }
    }
    return localArrayList;
  }
  
  public VersionInfo getInternalVersion()
  {
    return (VersionInfo)this.mVersion.get(StorageManager.UUID_PRIVATE_INTERNAL);
  }
  
  ArrayList<PackageSetting> getListOfIncompleteInstallPackagesLPr()
  {
    Iterator localIterator = new ArraySet(this.mPackages.keySet()).iterator();
    ArrayList localArrayList = new ArrayList();
    while (localIterator.hasNext())
    {
      Object localObject = (String)localIterator.next();
      localObject = (PackageSetting)this.mPackages.get(localObject);
      if (((PackageSetting)localObject).getInstallStatus() == 0) {
        localArrayList.add(localObject);
      }
    }
    return localArrayList;
  }
  
  PackageSetting getPackageLPw(PackageParser.Package paramPackage, PackageSetting paramPackageSetting, String paramString1, SharedUserSetting paramSharedUserSetting, File paramFile1, File paramFile2, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, UserHandle paramUserHandle, boolean paramBoolean)
  {
    String str2 = paramPackage.packageName;
    if (paramPackage.parentPackage != null) {}
    Object localObject;
    for (String str1 = paramPackage.parentPackage.packageName;; str1 = null)
    {
      localObject = null;
      if (paramPackage.childPackages == null) {
        break;
      }
      int j = paramPackage.childPackages.size();
      ArrayList localArrayList = new ArrayList(j);
      int i = 0;
      for (;;)
      {
        localObject = localArrayList;
        if (i >= j) {
          break;
        }
        localArrayList.add(((PackageParser.Package)paramPackage.childPackages.get(i)).packageName);
        i += 1;
      }
    }
    return getPackageLPw(str2, paramPackageSetting, paramString1, paramSharedUserSetting, paramFile1, paramFile2, paramString2, paramString3, paramString4, paramPackage.mVersionCode, paramInt1, paramInt2, paramUserHandle, paramBoolean, true, str1, (List)localObject);
  }
  
  SharedUserSetting getSharedUserLPw(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    SharedUserSetting localSharedUserSetting2 = (SharedUserSetting)this.mSharedUsers.get(paramString);
    SharedUserSetting localSharedUserSetting1 = localSharedUserSetting2;
    if (localSharedUserSetting2 == null)
    {
      if (!paramBoolean) {
        return null;
      }
      localSharedUserSetting2 = new SharedUserSetting(paramString, paramInt1, paramInt2);
      localSharedUserSetting2.userId = newUserIdLPw(localSharedUserSetting2);
      Log.i("PackageManager", "New shared user " + paramString + ": id=" + localSharedUserSetting2.userId);
      localSharedUserSetting1 = localSharedUserSetting2;
      if (localSharedUserSetting2.userId >= 0)
      {
        this.mSharedUsers.put(paramString, localSharedUserSetting2);
        localSharedUserSetting1 = localSharedUserSetting2;
      }
    }
    return localSharedUserSetting1;
  }
  
  public Object getUserIdLPr(int paramInt)
  {
    if (paramInt >= 10000)
    {
      int i = this.mUserIds.size();
      paramInt -= 10000;
      if (paramInt < i) {
        return this.mUserIds.get(paramInt);
      }
      return null;
    }
    return this.mOtherUserIds.get(paramInt);
  }
  
  public VerifierDeviceIdentity getVerifierDeviceIdentityLPw()
  {
    if (this.mVerifierDeviceIdentity == null)
    {
      this.mVerifierDeviceIdentity = VerifierDeviceIdentity.generate();
      writeLPr();
    }
    return this.mVerifierDeviceIdentity;
  }
  
  List<PackageSetting> getVolumePackagesLPr(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < this.mPackages.size())
    {
      PackageSetting localPackageSetting = (PackageSetting)this.mPackages.valueAt(i);
      if (Objects.equals(paramString, localPackageSetting.volumeUuid)) {
        localArrayList.add(localPackageSetting);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public boolean hasOtherDisabledSystemPkgWithChildLPr(String paramString1, String paramString2)
  {
    int k = this.mDisabledSysPackages.size();
    int i = 0;
    if (i < k)
    {
      PackageSetting localPackageSetting = (PackageSetting)this.mDisabledSysPackages.valueAt(i);
      if ((localPackageSetting.childPackageNames == null) || (localPackageSetting.childPackageNames.isEmpty())) {}
      for (;;)
      {
        i += 1;
        break;
        if (!localPackageSetting.name.equals(paramString1))
        {
          int m = localPackageSetting.childPackageNames.size();
          int j = 0;
          while (j < m)
          {
            if (((String)localPackageSetting.childPackageNames.get(j)).equals(paramString2)) {
              return true;
            }
            j += 1;
          }
        }
      }
    }
    return false;
  }
  
  void insertPackageSettingLPw(PackageSetting paramPackageSetting, PackageParser.Package paramPackage)
  {
    paramPackageSetting.pkg = paramPackage;
    String str1 = paramPackage.applicationInfo.volumeUuid;
    String str2 = paramPackage.applicationInfo.getCodePath();
    String str3 = paramPackage.applicationInfo.getResourcePath();
    String str4 = paramPackage.applicationInfo.nativeLibraryRootDir;
    if (!Objects.equals(str1, paramPackageSetting.volumeUuid))
    {
      Slog.w("PackageManager", "Volume for " + paramPackageSetting.pkg.packageName + " changing from " + paramPackageSetting.volumeUuid + " to " + str1);
      paramPackageSetting.volumeUuid = str1;
    }
    if (!Objects.equals(str2, paramPackageSetting.codePathString))
    {
      Slog.w("PackageManager", "Code path for " + paramPackageSetting.pkg.packageName + " changing from " + paramPackageSetting.codePathString + " to " + str2);
      paramPackageSetting.codePath = new File(str2);
      paramPackageSetting.codePathString = str2;
    }
    if (!Objects.equals(str3, paramPackageSetting.resourcePathString))
    {
      Slog.w("PackageManager", "Resource path for " + paramPackageSetting.pkg.packageName + " changing from " + paramPackageSetting.resourcePathString + " to " + str3);
      paramPackageSetting.resourcePath = new File(str3);
      paramPackageSetting.resourcePathString = str3;
    }
    if (!Objects.equals(str4, paramPackageSetting.legacyNativeLibraryPathString)) {
      paramPackageSetting.legacyNativeLibraryPathString = str4;
    }
    paramPackageSetting.primaryCpuAbiString = paramPackage.applicationInfo.primaryCpuAbi;
    paramPackageSetting.secondaryCpuAbiString = paramPackage.applicationInfo.secondaryCpuAbi;
    paramPackageSetting.cpuAbiOverrideString = paramPackage.cpuAbiOverride;
    if (paramPackage.mVersionCode != paramPackageSetting.versionCode) {
      paramPackageSetting.versionCode = paramPackage.mVersionCode;
    }
    if (paramPackageSetting.signatures.mSignatures == null) {
      paramPackageSetting.signatures.assignSignatures(paramPackage.mSignatures);
    }
    if (paramPackage.applicationInfo.flags != paramPackageSetting.pkgFlags) {
      paramPackageSetting.pkgFlags = paramPackage.applicationInfo.flags;
    }
    if ((paramPackageSetting.sharedUser != null) && (paramPackageSetting.sharedUser.signatures.mSignatures == null)) {
      paramPackageSetting.sharedUser.signatures.assignSignatures(paramPackage.mSignatures);
    }
    addPackageSettingLPw(paramPackageSetting, paramPackage.packageName, paramPackageSetting.sharedUser);
  }
  
  boolean isAdbInstallDisallowed(UserManagerService paramUserManagerService, int paramInt)
  {
    return paramUserManagerService.hasUserRestriction("no_debugging_features", paramInt);
  }
  
  boolean isDisabledSystemPackageLPr(String paramString)
  {
    return this.mDisabledSysPackages.containsKey(paramString);
  }
  
  boolean isEnabledAndMatchLPr(ComponentInfo paramComponentInfo, int paramInt1, int paramInt2)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramComponentInfo.packageName);
    if (localPackageSetting == null) {
      return false;
    }
    return localPackageSetting.readUserState(paramInt2).isMatch(paramComponentInfo, paramInt1);
  }
  
  boolean isOrphaned(String paramString)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null) {
      throw new IllegalArgumentException("Unknown package: " + paramString);
    }
    return localPackageSetting.isOrphaned;
  }
  
  void onDefaultRuntimePermissionsGrantedLPr(int paramInt)
  {
    this.mRuntimePermissionsPersistence.onDefaultRuntimePermissionsGrantedLPr(paramInt);
  }
  
  public void onVolumeForgotten(String paramString)
  {
    this.mVersion.remove(paramString);
  }
  
  PackageSetting peekPackageLPr(String paramString)
  {
    return (PackageSetting)this.mPackages.get(paramString);
  }
  
  public void processRestoredPermissionGrantLPr(String paramString1, String paramString2, boolean paramBoolean, int paramInt1, int paramInt2)
    throws IOException, XmlPullParserException
  {
    this.mRuntimePermissionsPersistence.rememberRestoredUserGrantLPr(paramString1, paramString2, paramBoolean, paramInt1, paramInt2);
  }
  
  void pruneSharedUsersLPw()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = this.mSharedUsers.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      SharedUserSetting localSharedUserSetting = (SharedUserSetting)localEntry.getValue();
      if (localSharedUserSetting == null)
      {
        localArrayList.add((String)localEntry.getKey());
      }
      else
      {
        Iterator localIterator2 = localSharedUserSetting.packages.iterator();
        while (localIterator2.hasNext())
        {
          PackageSetting localPackageSetting = (PackageSetting)localIterator2.next();
          if (this.mPackages.get(localPackageSetting.name) == null) {
            localIterator2.remove();
          }
        }
        if (localSharedUserSetting.packages.size() == 0) {
          localArrayList.add((String)localEntry.getKey());
        }
      }
    }
    int i = 0;
    while (i < localArrayList.size())
    {
      this.mSharedUsers.remove(localArrayList.get(i));
      i += 1;
    }
  }
  
  void readAllDomainVerificationsLPr(XmlPullParser paramXmlPullParser, int paramInt)
    throws XmlPullParserException, IOException
  {
    this.mRestoredIntentFilterVerifications.clear();
    paramInt = paramXmlPullParser.getDepth();
    for (;;)
    {
      int i = paramXmlPullParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() <= paramInt))) {
        break;
      }
      if ((i != 3) && (i != 4)) {
        if (paramXmlPullParser.getName().equals("domain-verification"))
        {
          IntentFilterVerificationInfo localIntentFilterVerificationInfo = new IntentFilterVerificationInfo(paramXmlPullParser);
          String str = localIntentFilterVerificationInfo.getPackageName();
          PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(str);
          if (localPackageSetting != null)
          {
            localPackageSetting.setIntentFilterVerificationInfo(localIntentFilterVerificationInfo);
            if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
              Slog.d("PackageSettings", "Restored IVI for existing app " + str + " status=" + localIntentFilterVerificationInfo.getStatusString());
            }
          }
          else
          {
            this.mRestoredIntentFilterVerifications.put(str, localIntentFilterVerificationInfo);
            if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
              Slog.d("PackageSettings", "Restored IVI for pending app " + str + " status=" + localIntentFilterVerificationInfo.getStatusString());
            }
          }
        }
        else
        {
          PackageManagerService.reportSettingsProblem(5, "Unknown element under <all-intent-filter-verification>: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  void readDefaultAppsLPw(XmlPullParser paramXmlPullParser, int paramInt)
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
        if (str.equals("default-browser"))
        {
          str = paramXmlPullParser.getAttributeValue(null, "packageName");
          this.mDefaultBrowserApp.put(paramInt, str);
        }
        else if (str.equals("default-dialer"))
        {
          str = paramXmlPullParser.getAttributeValue(null, "packageName");
          this.mDefaultDialerApp.put(paramInt, str);
        }
        else
        {
          PackageManagerService.reportSettingsProblem(5, "Unknown element under default-apps: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  void readInstallPermissionsLPr(XmlPullParser paramXmlPullParser, PermissionsState paramPermissionsState)
    throws IOException, XmlPullParserException
  {
    int j = paramXmlPullParser.getDepth();
    for (;;)
    {
      int i = paramXmlPullParser.next();
      if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() <= j))) {
        break;
      }
      if ((i != 3) && (i != 4)) {
        if (paramXmlPullParser.getName().equals("item"))
        {
          String str1 = paramXmlPullParser.getAttributeValue(null, "name");
          BasePermission localBasePermission = (BasePermission)this.mPermissions.get(str1);
          if (localBasePermission == null)
          {
            Slog.w("PackageManager", "Unknown permission: " + str1);
            XmlUtils.skipCurrentTag(paramXmlPullParser);
          }
          else
          {
            String str2 = paramXmlPullParser.getAttributeValue(null, "granted");
            boolean bool;
            if (str2 != null)
            {
              bool = Boolean.parseBoolean(str2);
              label148:
              str2 = paramXmlPullParser.getAttributeValue(null, "flags");
              if (str2 == null) {
                break label228;
              }
              i = Integer.parseInt(str2, 16);
            }
            for (;;)
            {
              if (bool)
              {
                if (paramPermissionsState.grantInstallPermission(localBasePermission) == -1)
                {
                  Slog.w("PackageManager", "Permission already added: " + str1);
                  XmlUtils.skipCurrentTag(paramXmlPullParser);
                  break;
                  bool = true;
                  break label148;
                  label228:
                  i = 0;
                  continue;
                }
                paramPermissionsState.updatePermissionFlags(localBasePermission, -1, 255, i);
                break;
              }
            }
            if (paramPermissionsState.revokeInstallPermission(localBasePermission) == -1)
            {
              Slog.w("PackageManager", "Permission already added: " + str1);
              XmlUtils.skipCurrentTag(paramXmlPullParser);
            }
            else
            {
              paramPermissionsState.updatePermissionFlags(localBasePermission, -1, 255, i);
            }
          }
        }
        else
        {
          Slog.w("PackageManager", "Unknown element under <permissions>: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  /* Error */
  boolean readLPw(List<UserInfo> paramList)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload 7
    //   5: astore 6
    //   7: aload_0
    //   8: getfield 463	com/android/server/pm/Settings:mBackupSettingsFilename	Ljava/io/File;
    //   11: invokevirtual 478	java/io/File:exists	()Z
    //   14: ifeq +82 -> 96
    //   17: new 1654	java/io/FileInputStream
    //   20: dup
    //   21: aload_0
    //   22: getfield 463	com/android/server/pm/Settings:mBackupSettingsFilename	Ljava/io/File;
    //   25: invokespecial 1657	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   28: astore 6
    //   30: aload_0
    //   31: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   34: ldc_w 2549
    //   37: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: pop
    //   41: iconst_4
    //   42: ldc_w 2551
    //   45: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   48: aload_0
    //   49: getfield 459	com/android/server/pm/Settings:mSettingsFilename	Ljava/io/File;
    //   52: invokevirtual 478	java/io/File:exists	()Z
    //   55: ifeq +41 -> 96
    //   58: ldc_w 988
    //   61: new 413	java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   68: ldc_w 2553
    //   71: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: aload_0
    //   75: getfield 459	com/android/server/pm/Settings:mSettingsFilename	Ljava/io/File;
    //   78: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   81: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   87: pop
    //   88: aload_0
    //   89: getfield 459	com/android/server/pm/Settings:mSettingsFilename	Ljava/io/File;
    //   92: invokevirtual 2556	java/io/File:delete	()Z
    //   95: pop
    //   96: aload_0
    //   97: getfield 418	com/android/server/pm/Settings:mPendingPackages	Ljava/util/ArrayList;
    //   100: invokevirtual 2557	java/util/ArrayList:clear	()V
    //   103: aload_0
    //   104: getfield 392	com/android/server/pm/Settings:mPastSignatures	Ljava/util/ArrayList;
    //   107: invokevirtual 2557	java/util/ArrayList:clear	()V
    //   110: aload_0
    //   111: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   114: invokevirtual 2516	android/util/ArrayMap:clear	()V
    //   117: aload_0
    //   118: getfield 364	com/android/server/pm/Settings:mInstallerPackages	Landroid/util/ArraySet;
    //   121: invokevirtual 2558	android/util/ArraySet:clear	()V
    //   124: aload 6
    //   126: astore 7
    //   128: aload 6
    //   130: ifnonnull +62 -> 192
    //   133: aload_0
    //   134: getfield 459	com/android/server/pm/Settings:mSettingsFilename	Ljava/io/File;
    //   137: invokevirtual 478	java/io/File:exists	()Z
    //   140: ifne +39 -> 179
    //   143: aload_0
    //   144: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   147: ldc_w 2560
    //   150: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: pop
    //   154: iconst_4
    //   155: ldc_w 2562
    //   158: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   161: aload_0
    //   162: getstatic 2296	android/os/storage/StorageManager:UUID_PRIVATE_INTERNAL	Ljava/lang/String;
    //   165: invokevirtual 2564	com/android/server/pm/Settings:findOrCreateVersion	(Ljava/lang/String;)Lcom/android/server/pm/Settings$VersionInfo;
    //   168: pop
    //   169: aload_0
    //   170: ldc_w 2320
    //   173: invokevirtual 2564	com/android/server/pm/Settings:findOrCreateVersion	(Ljava/lang/String;)Lcom/android/server/pm/Settings$VersionInfo;
    //   176: pop
    //   177: iconst_0
    //   178: ireturn
    //   179: new 1654	java/io/FileInputStream
    //   182: dup
    //   183: aload_0
    //   184: getfield 459	com/android/server/pm/Settings:mSettingsFilename	Ljava/io/File;
    //   187: invokespecial 1657	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   190: astore 7
    //   192: invokestatic 1666	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   195: astore 6
    //   197: aload 6
    //   199: aload 7
    //   201: getstatic 2570	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   204: invokevirtual 2574	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   207: invokeinterface 1670 3 0
    //   212: aload 6
    //   214: invokeinterface 1177 1 0
    //   219: istore_2
    //   220: iload_2
    //   221: iconst_2
    //   222: if_icmpeq +8 -> 230
    //   225: iload_2
    //   226: iconst_1
    //   227: if_icmpne -15 -> 212
    //   230: iload_2
    //   231: iconst_2
    //   232: if_icmpeq +33 -> 265
    //   235: aload_0
    //   236: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   239: ldc_w 2576
    //   242: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: pop
    //   246: iconst_5
    //   247: ldc_w 2578
    //   250: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   253: ldc_w 988
    //   256: ldc_w 2578
    //   259: invokestatic 2581	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   262: pop
    //   263: iconst_0
    //   264: ireturn
    //   265: aload 6
    //   267: invokeinterface 1175 1 0
    //   272: istore 4
    //   274: aload 6
    //   276: invokeinterface 1177 1 0
    //   281: istore_2
    //   282: iload_2
    //   283: iconst_1
    //   284: if_icmpeq +1090 -> 1374
    //   287: iload_2
    //   288: iconst_3
    //   289: if_icmpne +15 -> 304
    //   292: aload 6
    //   294: invokeinterface 1175 1 0
    //   299: iload 4
    //   301: if_icmple +1073 -> 1374
    //   304: iload_2
    //   305: iconst_3
    //   306: if_icmpeq -32 -> 274
    //   309: iload_2
    //   310: iconst_4
    //   311: if_icmpeq -37 -> 274
    //   314: aload 6
    //   316: invokeinterface 1180 1 0
    //   321: astore 8
    //   323: aload 8
    //   325: ldc_w 1474
    //   328: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   331: ifeq +238 -> 569
    //   334: aload_0
    //   335: aload 6
    //   337: invokespecial 2583	com/android/server/pm/Settings:readPackageLPw	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   340: goto -66 -> 274
    //   343: astore 6
    //   345: aload_0
    //   346: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   349: ldc_w 2585
    //   352: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   355: aload 6
    //   357: invokevirtual 2586	org/xmlpull/v1/XmlPullParserException:toString	()Ljava/lang/String;
    //   360: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: pop
    //   364: bipush 6
    //   366: new 413	java/lang/StringBuilder
    //   369: dup
    //   370: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   373: ldc_w 2588
    //   376: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   379: aload 6
    //   381: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   384: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   387: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   390: ldc_w 988
    //   393: ldc_w 2590
    //   396: aload 6
    //   398: invokestatic 2592	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   401: pop
    //   402: aload_0
    //   403: getfield 418	com/android/server/pm/Settings:mPendingPackages	Ljava/util/ArrayList;
    //   406: invokevirtual 600	java/util/ArrayList:size	()I
    //   409: istore_3
    //   410: iconst_0
    //   411: istore_2
    //   412: iload_2
    //   413: iload_3
    //   414: if_icmpge +1115 -> 1529
    //   417: aload_0
    //   418: getfield 418	com/android/server/pm/Settings:mPendingPackages	Ljava/util/ArrayList;
    //   421: iload_2
    //   422: invokevirtual 606	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   425: checkcast 1410	com/android/server/pm/PendingPackage
    //   428: astore 6
    //   430: aload_0
    //   431: aload 6
    //   433: getfield 2595	com/android/server/pm/PendingPackage:sharedId	I
    //   436: invokevirtual 552	com/android/server/pm/Settings:getUserIdLPr	(I)Ljava/lang/Object;
    //   439: astore 7
    //   441: aload 7
    //   443: ifnull +949 -> 1392
    //   446: aload 7
    //   448: instanceof 535
    //   451: ifeq +941 -> 1392
    //   454: aload_0
    //   455: aload 6
    //   457: getfield 2596	com/android/server/pm/PendingPackage:name	Ljava/lang/String;
    //   460: aconst_null
    //   461: aload 6
    //   463: getfield 2597	com/android/server/pm/PendingPackage:realName	Ljava/lang/String;
    //   466: aload 7
    //   468: checkcast 535	com/android/server/pm/SharedUserSetting
    //   471: aload 6
    //   473: getfield 2598	com/android/server/pm/PendingPackage:codePath	Ljava/io/File;
    //   476: aload 6
    //   478: getfield 2599	com/android/server/pm/PendingPackage:resourcePath	Ljava/io/File;
    //   481: aload 6
    //   483: getfield 2600	com/android/server/pm/PendingPackage:legacyNativeLibraryPathString	Ljava/lang/String;
    //   486: aload 6
    //   488: getfield 2601	com/android/server/pm/PendingPackage:primaryCpuAbiString	Ljava/lang/String;
    //   491: aload 6
    //   493: getfield 2602	com/android/server/pm/PendingPackage:secondaryCpuAbiString	Ljava/lang/String;
    //   496: aload 6
    //   498: getfield 2603	com/android/server/pm/PendingPackage:versionCode	I
    //   501: aload 6
    //   503: getfield 2604	com/android/server/pm/PendingPackage:pkgFlags	I
    //   506: aload 6
    //   508: getfield 2605	com/android/server/pm/PendingPackage:pkgPrivateFlags	I
    //   511: aconst_null
    //   512: iconst_1
    //   513: iconst_0
    //   514: aload 6
    //   516: getfield 2606	com/android/server/pm/PendingPackage:parentPackageName	Ljava/lang/String;
    //   519: aload 6
    //   521: getfield 2607	com/android/server/pm/PendingPackage:childPackageNames	Ljava/util/List;
    //   524: invokespecial 2418	com/android/server/pm/Settings:getPackageLPw	(Ljava/lang/String;Lcom/android/server/pm/PackageSetting;Ljava/lang/String;Lcom/android/server/pm/SharedUserSetting;Ljava/io/File;Ljava/io/File;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIILandroid/os/UserHandle;ZZLjava/lang/String;Ljava/util/List;)Lcom/android/server/pm/PackageSetting;
    //   527: astore 7
    //   529: aload 7
    //   531: ifnonnull +851 -> 1382
    //   534: iconst_5
    //   535: new 413	java/lang/StringBuilder
    //   538: dup
    //   539: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   542: ldc_w 2609
    //   545: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   548: aload 6
    //   550: getfield 2596	com/android/server/pm/PendingPackage:name	Ljava/lang/String;
    //   553: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   556: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   559: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   562: iload_2
    //   563: iconst_1
    //   564: iadd
    //   565: istore_2
    //   566: goto -154 -> 412
    //   569: aload 8
    //   571: ldc_w 2610
    //   574: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   577: ifeq +78 -> 655
    //   580: aload_0
    //   581: aload_0
    //   582: getfield 396	com/android/server/pm/Settings:mPermissions	Landroid/util/ArrayMap;
    //   585: aload 6
    //   587: invokespecial 2612	com/android/server/pm/Settings:readPermissionsLPw	(Landroid/util/ArrayMap;Lorg/xmlpull/v1/XmlPullParser;)V
    //   590: goto -316 -> 274
    //   593: astore 6
    //   595: aload_0
    //   596: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   599: ldc_w 2585
    //   602: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   605: aload 6
    //   607: invokevirtual 2613	java/io/IOException:toString	()Ljava/lang/String;
    //   610: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   613: pop
    //   614: bipush 6
    //   616: new 413	java/lang/StringBuilder
    //   619: dup
    //   620: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   623: ldc_w 2588
    //   626: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   629: aload 6
    //   631: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   634: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   637: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   640: ldc_w 988
    //   643: ldc_w 2590
    //   646: aload 6
    //   648: invokestatic 2592	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   651: pop
    //   652: goto -250 -> 402
    //   655: aload 8
    //   657: ldc_w 2615
    //   660: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   663: ifeq +16 -> 679
    //   666: aload_0
    //   667: aload_0
    //   668: getfield 398	com/android/server/pm/Settings:mPermissionTrees	Landroid/util/ArrayMap;
    //   671: aload 6
    //   673: invokespecial 2612	com/android/server/pm/Settings:readPermissionsLPw	(Landroid/util/ArrayMap;Lorg/xmlpull/v1/XmlPullParser;)V
    //   676: goto -402 -> 274
    //   679: aload 8
    //   681: ldc -77
    //   683: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   686: ifeq +12 -> 698
    //   689: aload_0
    //   690: aload 6
    //   692: invokespecial 2617	com/android/server/pm/Settings:readSharedUserLPw	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   695: goto -421 -> 274
    //   698: aload 8
    //   700: ldc_w 2619
    //   703: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   706: ifne -432 -> 274
    //   709: aload 8
    //   711: ldc_w 1679
    //   714: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   717: ifeq +13 -> 730
    //   720: aload_0
    //   721: aload 6
    //   723: iconst_0
    //   724: invokevirtual 2622	com/android/server/pm/Settings:readPreferredActivitiesLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   727: goto -453 -> 274
    //   730: aload 8
    //   732: ldc -89
    //   734: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   737: ifeq +13 -> 750
    //   740: aload_0
    //   741: aload 6
    //   743: iconst_0
    //   744: invokespecial 2624	com/android/server/pm/Settings:readPersistentPreferredActivitiesLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   747: goto -473 -> 274
    //   750: aload 8
    //   752: ldc -125
    //   754: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   757: ifeq +13 -> 770
    //   760: aload_0
    //   761: aload 6
    //   763: iconst_0
    //   764: invokespecial 2626	com/android/server/pm/Settings:readCrossProfileIntentFiltersLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   767: goto -493 -> 274
    //   770: aload 8
    //   772: ldc -119
    //   774: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   777: ifeq +13 -> 790
    //   780: aload_0
    //   781: aload 6
    //   783: iconst_0
    //   784: invokevirtual 2628	com/android/server/pm/Settings:readDefaultAppsLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   787: goto -513 -> 274
    //   790: aload 8
    //   792: ldc_w 2630
    //   795: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   798: ifeq +12 -> 810
    //   801: aload_0
    //   802: aload 6
    //   804: invokespecial 2632	com/android/server/pm/Settings:readDisabledSysPackageLPw	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   807: goto -533 -> 274
    //   810: aload 8
    //   812: ldc_w 2634
    //   815: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   818: ifeq +93 -> 911
    //   821: aload 6
    //   823: aconst_null
    //   824: ldc 71
    //   826: invokeinterface 1184 3 0
    //   831: astore 8
    //   833: aload 6
    //   835: aconst_null
    //   836: ldc 92
    //   838: invokeinterface 1184 3 0
    //   843: astore 10
    //   845: aload 6
    //   847: aconst_null
    //   848: ldc 35
    //   850: invokeinterface 1184 3 0
    //   855: astore 9
    //   857: aload 8
    //   859: ifnull -585 -> 274
    //   862: iconst_0
    //   863: istore_3
    //   864: iconst_1
    //   865: istore 5
    //   867: iload_3
    //   868: istore_2
    //   869: aload 10
    //   871: ifnull +9 -> 880
    //   874: aload 10
    //   876: invokestatic 1273	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   879: istore_2
    //   880: aload 9
    //   882: ifnull +10 -> 892
    //   885: aload 9
    //   887: invokestatic 2532	java/lang/Boolean:parseBoolean	(Ljava/lang/String;)Z
    //   890: istore 5
    //   892: aload_0
    //   893: new 2636	android/content/pm/PackageCleanItem
    //   896: dup
    //   897: iload_2
    //   898: aload 8
    //   900: iload 5
    //   902: invokespecial 2639	android/content/pm/PackageCleanItem:<init>	(ILjava/lang/String;Z)V
    //   905: invokevirtual 2641	com/android/server/pm/Settings:addPackageToCleanLPw	(Landroid/content/pm/PackageCleanItem;)V
    //   908: goto -634 -> 274
    //   911: aload 8
    //   913: ldc_w 2643
    //   916: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   919: ifeq +54 -> 973
    //   922: aload 6
    //   924: aconst_null
    //   925: ldc_w 2645
    //   928: invokeinterface 1184 3 0
    //   933: astore 8
    //   935: aload 6
    //   937: aconst_null
    //   938: ldc_w 2647
    //   941: invokeinterface 1184 3 0
    //   946: astore 9
    //   948: aload 8
    //   950: ifnull -676 -> 274
    //   953: aload 9
    //   955: ifnull -681 -> 274
    //   958: aload_0
    //   959: getfield 402	com/android/server/pm/Settings:mRenamedPackages	Landroid/util/ArrayMap;
    //   962: aload 8
    //   964: aload 9
    //   966: invokevirtual 503	android/util/ArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   969: pop
    //   970: goto -696 -> 274
    //   973: aload 8
    //   975: ldc_w 2649
    //   978: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   981: ifeq +12 -> 993
    //   984: aload_0
    //   985: aload 6
    //   987: invokespecial 2651	com/android/server/pm/Settings:readRestoredIntentFilterVerifications	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   990: goto -716 -> 274
    //   993: aload 8
    //   995: ldc_w 2653
    //   998: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1001: ifeq +75 -> 1076
    //   1004: aload_0
    //   1005: getstatic 2296	android/os/storage/StorageManager:UUID_PRIVATE_INTERNAL	Ljava/lang/String;
    //   1008: invokevirtual 2564	com/android/server/pm/Settings:findOrCreateVersion	(Ljava/lang/String;)Lcom/android/server/pm/Settings$VersionInfo;
    //   1011: astore 8
    //   1013: aload_0
    //   1014: ldc_w 2320
    //   1017: invokevirtual 2564	com/android/server/pm/Settings:findOrCreateVersion	(Ljava/lang/String;)Lcom/android/server/pm/Settings$VersionInfo;
    //   1020: astore 9
    //   1022: aload 8
    //   1024: aload 6
    //   1026: ldc_w 2655
    //   1029: iconst_0
    //   1030: invokestatic 2659	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   1033: putfield 2306	com/android/server/pm/Settings$VersionInfo:sdkVersion	I
    //   1036: aload 9
    //   1038: aload 6
    //   1040: ldc_w 2661
    //   1043: iconst_0
    //   1044: invokestatic 2659	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   1047: putfield 2306	com/android/server/pm/Settings$VersionInfo:sdkVersion	I
    //   1050: aload 6
    //   1052: ldc 56
    //   1054: invokestatic 2665	com/android/internal/util/XmlUtils:readStringAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)Ljava/lang/String;
    //   1057: astore 10
    //   1059: aload 9
    //   1061: aload 10
    //   1063: putfield 2315	com/android/server/pm/Settings$VersionInfo:fingerprint	Ljava/lang/String;
    //   1066: aload 8
    //   1068: aload 10
    //   1070: putfield 2315	com/android/server/pm/Settings$VersionInfo:fingerprint	Ljava/lang/String;
    //   1073: goto -799 -> 274
    //   1076: aload 8
    //   1078: ldc_w 2667
    //   1081: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1084: ifeq +52 -> 1136
    //   1087: aload_0
    //   1088: getstatic 2296	android/os/storage/StorageManager:UUID_PRIVATE_INTERNAL	Ljava/lang/String;
    //   1091: invokevirtual 2564	com/android/server/pm/Settings:findOrCreateVersion	(Ljava/lang/String;)Lcom/android/server/pm/Settings$VersionInfo;
    //   1094: astore 8
    //   1096: aload_0
    //   1097: ldc_w 2320
    //   1100: invokevirtual 2564	com/android/server/pm/Settings:findOrCreateVersion	(Ljava/lang/String;)Lcom/android/server/pm/Settings$VersionInfo;
    //   1103: astore 9
    //   1105: aload 8
    //   1107: aload 6
    //   1109: ldc_w 2655
    //   1112: iconst_0
    //   1113: invokestatic 2659	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   1116: putfield 2312	com/android/server/pm/Settings$VersionInfo:databaseVersion	I
    //   1119: aload 9
    //   1121: aload 6
    //   1123: ldc_w 2661
    //   1126: iconst_0
    //   1127: invokestatic 2659	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   1130: putfield 2312	com/android/server/pm/Settings$VersionInfo:databaseVersion	I
    //   1133: goto -859 -> 274
    //   1136: aload 8
    //   1138: ldc_w 2669
    //   1141: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1144: ifeq +64 -> 1208
    //   1147: aload 6
    //   1149: aconst_null
    //   1150: ldc_w 2671
    //   1153: invokeinterface 1184 3 0
    //   1158: astore 8
    //   1160: aload_0
    //   1161: aload 8
    //   1163: invokestatic 2675	android/content/pm/VerifierDeviceIdentity:parse	(Ljava/lang/String;)Landroid/content/pm/VerifierDeviceIdentity;
    //   1166: putfield 2428	com/android/server/pm/Settings:mVerifierDeviceIdentity	Landroid/content/pm/VerifierDeviceIdentity;
    //   1169: goto -895 -> 274
    //   1172: astore 8
    //   1174: ldc_w 988
    //   1177: new 413	java/lang/StringBuilder
    //   1180: dup
    //   1181: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   1184: ldc_w 2677
    //   1187: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1190: aload 8
    //   1192: invokevirtual 2680	java/lang/IllegalArgumentException:getMessage	()Ljava/lang/String;
    //   1195: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1198: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1201: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1204: pop
    //   1205: goto -931 -> 274
    //   1208: ldc -86
    //   1210: aload 8
    //   1212: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1215: ifeq +29 -> 1244
    //   1218: aload_0
    //   1219: ldc_w 2682
    //   1222: aload 6
    //   1224: aconst_null
    //   1225: ldc 53
    //   1227: invokeinterface 1184 3 0
    //   1232: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1235: invokestatic 2685	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1238: putfield 2237	com/android/server/pm/Settings:mReadExternalStorageEnforced	Ljava/lang/Boolean;
    //   1241: goto -967 -> 274
    //   1244: aload 8
    //   1246: ldc_w 2687
    //   1249: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1252: ifeq +19 -> 1271
    //   1255: aload_0
    //   1256: getfield 425	com/android/server/pm/Settings:mKeySetManagerService	Lcom/android/server/pm/KeySetManagerService;
    //   1259: aload 6
    //   1261: aload_0
    //   1262: getfield 394	com/android/server/pm/Settings:mKeySetRefs	Landroid/util/ArrayMap;
    //   1265: invokevirtual 2691	com/android/server/pm/KeySetManagerService:readKeySetsLPw	(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/ArrayMap;)V
    //   1268: goto -994 -> 274
    //   1271: ldc -74
    //   1273: aload 8
    //   1275: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1278: ifeq +55 -> 1333
    //   1281: aload_0
    //   1282: aload 6
    //   1284: ldc 101
    //   1286: invokestatic 2665	com/android/internal/util/XmlUtils:readStringAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)Ljava/lang/String;
    //   1289: invokevirtual 2564	com/android/server/pm/Settings:findOrCreateVersion	(Ljava/lang/String;)Lcom/android/server/pm/Settings$VersionInfo;
    //   1292: astore 8
    //   1294: aload 8
    //   1296: aload 6
    //   1298: ldc 83
    //   1300: invokestatic 2694	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)I
    //   1303: putfield 2306	com/android/server/pm/Settings$VersionInfo:sdkVersion	I
    //   1306: aload 8
    //   1308: aload 6
    //   1310: ldc 83
    //   1312: invokestatic 2694	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)I
    //   1315: putfield 2312	com/android/server/pm/Settings$VersionInfo:databaseVersion	I
    //   1318: aload 8
    //   1320: aload 6
    //   1322: ldc 56
    //   1324: invokestatic 2665	com/android/internal/util/XmlUtils:readStringAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)Ljava/lang/String;
    //   1327: putfield 2315	com/android/server/pm/Settings$VersionInfo:fingerprint	Ljava/lang/String;
    //   1330: goto -1056 -> 274
    //   1333: ldc_w 988
    //   1336: new 413	java/lang/StringBuilder
    //   1339: dup
    //   1340: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   1343: ldc_w 2696
    //   1346: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1349: aload 6
    //   1351: invokeinterface 1180 1 0
    //   1356: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1359: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1362: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1365: pop
    //   1366: aload 6
    //   1368: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1371: goto -1097 -> 274
    //   1374: aload 7
    //   1376: invokevirtual 2697	java/io/FileInputStream:close	()V
    //   1379: goto -977 -> 402
    //   1382: aload 7
    //   1384: aload 6
    //   1386: invokevirtual 1019	com/android/server/pm/PackageSetting:copyFrom	(Lcom/android/server/pm/PackageSettingBase;)V
    //   1389: goto -827 -> 562
    //   1392: aload 7
    //   1394: ifnull +69 -> 1463
    //   1397: new 413	java/lang/StringBuilder
    //   1400: dup
    //   1401: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   1404: ldc_w 2699
    //   1407: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1410: aload 6
    //   1412: getfield 2596	com/android/server/pm/PendingPackage:name	Ljava/lang/String;
    //   1415: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1418: ldc_w 2701
    //   1421: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1424: aload 6
    //   1426: getfield 2595	com/android/server/pm/PendingPackage:sharedId	I
    //   1429: invokevirtual 590	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1432: ldc_w 2703
    //   1435: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1438: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1441: astore 6
    //   1443: aload_0
    //   1444: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   1447: aload 6
    //   1449: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1452: pop
    //   1453: bipush 6
    //   1455: aload 6
    //   1457: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   1460: goto -898 -> 562
    //   1463: new 413	java/lang/StringBuilder
    //   1466: dup
    //   1467: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   1470: ldc_w 2699
    //   1473: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1476: aload 6
    //   1478: getfield 2596	com/android/server/pm/PendingPackage:name	Ljava/lang/String;
    //   1481: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1484: ldc_w 2701
    //   1487: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1490: aload 6
    //   1492: getfield 2595	com/android/server/pm/PendingPackage:sharedId	I
    //   1495: invokevirtual 590	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1498: ldc_w 2705
    //   1501: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1504: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1507: astore 6
    //   1509: aload_0
    //   1510: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   1513: aload 6
    //   1515: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1518: pop
    //   1519: bipush 6
    //   1521: aload 6
    //   1523: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   1526: goto -964 -> 562
    //   1529: aload_0
    //   1530: getfield 418	com/android/server/pm/Settings:mPendingPackages	Ljava/util/ArrayList;
    //   1533: invokevirtual 2557	java/util/ArrayList:clear	()V
    //   1536: aload_0
    //   1537: getfield 488	com/android/server/pm/Settings:mBackupStoppedPackagesFilename	Ljava/io/File;
    //   1540: invokevirtual 478	java/io/File:exists	()Z
    //   1543: ifne +13 -> 1556
    //   1546: aload_0
    //   1547: getfield 484	com/android/server/pm/Settings:mStoppedPackagesFilename	Ljava/io/File;
    //   1550: invokevirtual 478	java/io/File:exists	()Z
    //   1553: ifeq +70 -> 1623
    //   1556: aload_0
    //   1557: invokevirtual 2708	com/android/server/pm/Settings:readStoppedLPw	()V
    //   1560: aload_0
    //   1561: getfield 488	com/android/server/pm/Settings:mBackupStoppedPackagesFilename	Ljava/io/File;
    //   1564: invokevirtual 2556	java/io/File:delete	()Z
    //   1567: pop
    //   1568: aload_0
    //   1569: getfield 484	com/android/server/pm/Settings:mStoppedPackagesFilename	Ljava/io/File;
    //   1572: invokevirtual 2556	java/io/File:delete	()Z
    //   1575: pop
    //   1576: aload_0
    //   1577: iconst_0
    //   1578: invokevirtual 1087	com/android/server/pm/Settings:writePackageRestrictionsLPr	(I)V
    //   1581: aload_1
    //   1582: invokeinterface 709 1 0
    //   1587: astore_1
    //   1588: aload_1
    //   1589: invokeinterface 714 1 0
    //   1594: ifeq +67 -> 1661
    //   1597: aload_1
    //   1598: invokeinterface 718 1 0
    //   1603: checkcast 1056	android/content/pm/UserInfo
    //   1606: astore 6
    //   1608: aload_0
    //   1609: getfield 432	com/android/server/pm/Settings:mRuntimePermissionsPersistence	Lcom/android/server/pm/Settings$RuntimePermissionPersistence;
    //   1612: aload 6
    //   1614: getfield 1059	android/content/pm/UserInfo:id	I
    //   1617: invokevirtual 2711	com/android/server/pm/Settings$RuntimePermissionPersistence:readStateForUserSyncLPr	(I)V
    //   1620: goto -32 -> 1588
    //   1623: aload_1
    //   1624: invokeinterface 709 1 0
    //   1629: astore 6
    //   1631: aload 6
    //   1633: invokeinterface 714 1 0
    //   1638: ifeq -57 -> 1581
    //   1641: aload_0
    //   1642: aload 6
    //   1644: invokeinterface 718 1 0
    //   1649: checkcast 1056	android/content/pm/UserInfo
    //   1652: getfield 1059	android/content/pm/UserInfo:id	I
    //   1655: invokevirtual 2714	com/android/server/pm/Settings:readPackageRestrictionsLPr	(I)V
    //   1658: goto -27 -> 1631
    //   1661: aload_0
    //   1662: getfield 368	com/android/server/pm/Settings:mDisabledSysPackages	Landroid/util/ArrayMap;
    //   1665: invokevirtual 1598	android/util/ArrayMap:values	()Ljava/util/Collection;
    //   1668: invokeinterface 1753 1 0
    //   1673: astore_1
    //   1674: aload_1
    //   1675: invokeinterface 714 1 0
    //   1680: ifeq +51 -> 1731
    //   1683: aload_1
    //   1684: invokeinterface 718 1 0
    //   1689: checkcast 505	com/android/server/pm/PackageSetting
    //   1692: astore 6
    //   1694: aload_0
    //   1695: aload 6
    //   1697: getfield 548	com/android/server/pm/PackageSetting:appId	I
    //   1700: invokevirtual 552	com/android/server/pm/Settings:getUserIdLPr	(I)Ljava/lang/Object;
    //   1703: astore 7
    //   1705: aload 7
    //   1707: ifnull -33 -> 1674
    //   1710: aload 7
    //   1712: instanceof 535
    //   1715: ifeq -41 -> 1674
    //   1718: aload 6
    //   1720: aload 7
    //   1722: checkcast 535	com/android/server/pm/SharedUserSetting
    //   1725: putfield 509	com/android/server/pm/PackageSetting:sharedUser	Lcom/android/server/pm/SharedUserSetting;
    //   1728: goto -54 -> 1674
    //   1731: aload_0
    //   1732: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   1735: ldc_w 2716
    //   1738: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1741: aload_0
    //   1742: getfield 359	com/android/server/pm/Settings:mPackages	Landroid/util/ArrayMap;
    //   1745: invokevirtual 1561	android/util/ArrayMap:size	()I
    //   1748: invokevirtual 590	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1751: ldc_w 2718
    //   1754: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1757: aload_0
    //   1758: getfield 383	com/android/server/pm/Settings:mSharedUsers	Landroid/util/ArrayMap;
    //   1761: invokevirtual 1561	android/util/ArrayMap:size	()I
    //   1764: invokevirtual 590	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1767: ldc_w 2720
    //   1770: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1773: pop
    //   1774: aload_0
    //   1775: invokevirtual 2723	com/android/server/pm/Settings:writeKernelMappingLPr	()V
    //   1778: iconst_1
    //   1779: ireturn
    //   1780: astore 6
    //   1782: aload 7
    //   1784: astore 6
    //   1786: goto -1690 -> 96
    //   1789: astore 7
    //   1791: goto -1695 -> 96
    //   1794: astore 10
    //   1796: iload_3
    //   1797: istore_2
    //   1798: goto -918 -> 880
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1801	0	this	Settings
    //   0	1801	1	paramList	List<UserInfo>
    //   219	1579	2	i	int
    //   409	1388	3	j	int
    //   272	30	4	k	int
    //   865	36	5	bool	boolean
    //   5	331	6	localObject1	Object
    //   343	54	6	localXmlPullParserException	XmlPullParserException
    //   428	158	6	localPendingPackage	PendingPackage
    //   593	832	6	localIOException1	IOException
    //   1441	278	6	localObject2	Object
    //   1780	1	6	localIOException2	IOException
    //   1784	1	6	localObject3	Object
    //   1	1782	7	localObject4	Object
    //   1789	1	7	localIOException3	IOException
    //   321	841	8	localObject5	Object
    //   1172	102	8	localIllegalArgumentException	IllegalArgumentException
    //   1292	27	8	localVersionInfo	VersionInfo
    //   855	265	9	localObject6	Object
    //   843	226	10	str	String
    //   1794	1	10	localNumberFormatException	NumberFormatException
    // Exception table:
    //   from	to	target	type
    //   133	177	343	org/xmlpull/v1/XmlPullParserException
    //   179	192	343	org/xmlpull/v1/XmlPullParserException
    //   192	212	343	org/xmlpull/v1/XmlPullParserException
    //   212	220	343	org/xmlpull/v1/XmlPullParserException
    //   235	263	343	org/xmlpull/v1/XmlPullParserException
    //   265	274	343	org/xmlpull/v1/XmlPullParserException
    //   274	282	343	org/xmlpull/v1/XmlPullParserException
    //   292	304	343	org/xmlpull/v1/XmlPullParserException
    //   314	340	343	org/xmlpull/v1/XmlPullParserException
    //   569	590	343	org/xmlpull/v1/XmlPullParserException
    //   655	676	343	org/xmlpull/v1/XmlPullParserException
    //   679	695	343	org/xmlpull/v1/XmlPullParserException
    //   698	727	343	org/xmlpull/v1/XmlPullParserException
    //   730	747	343	org/xmlpull/v1/XmlPullParserException
    //   750	767	343	org/xmlpull/v1/XmlPullParserException
    //   770	787	343	org/xmlpull/v1/XmlPullParserException
    //   790	807	343	org/xmlpull/v1/XmlPullParserException
    //   810	857	343	org/xmlpull/v1/XmlPullParserException
    //   874	880	343	org/xmlpull/v1/XmlPullParserException
    //   885	892	343	org/xmlpull/v1/XmlPullParserException
    //   892	908	343	org/xmlpull/v1/XmlPullParserException
    //   911	948	343	org/xmlpull/v1/XmlPullParserException
    //   958	970	343	org/xmlpull/v1/XmlPullParserException
    //   973	990	343	org/xmlpull/v1/XmlPullParserException
    //   993	1073	343	org/xmlpull/v1/XmlPullParserException
    //   1076	1133	343	org/xmlpull/v1/XmlPullParserException
    //   1136	1160	343	org/xmlpull/v1/XmlPullParserException
    //   1160	1169	343	org/xmlpull/v1/XmlPullParserException
    //   1174	1205	343	org/xmlpull/v1/XmlPullParserException
    //   1208	1241	343	org/xmlpull/v1/XmlPullParserException
    //   1244	1268	343	org/xmlpull/v1/XmlPullParserException
    //   1271	1330	343	org/xmlpull/v1/XmlPullParserException
    //   1333	1371	343	org/xmlpull/v1/XmlPullParserException
    //   1374	1379	343	org/xmlpull/v1/XmlPullParserException
    //   133	177	593	java/io/IOException
    //   179	192	593	java/io/IOException
    //   192	212	593	java/io/IOException
    //   212	220	593	java/io/IOException
    //   235	263	593	java/io/IOException
    //   265	274	593	java/io/IOException
    //   274	282	593	java/io/IOException
    //   292	304	593	java/io/IOException
    //   314	340	593	java/io/IOException
    //   569	590	593	java/io/IOException
    //   655	676	593	java/io/IOException
    //   679	695	593	java/io/IOException
    //   698	727	593	java/io/IOException
    //   730	747	593	java/io/IOException
    //   750	767	593	java/io/IOException
    //   770	787	593	java/io/IOException
    //   790	807	593	java/io/IOException
    //   810	857	593	java/io/IOException
    //   874	880	593	java/io/IOException
    //   885	892	593	java/io/IOException
    //   892	908	593	java/io/IOException
    //   911	948	593	java/io/IOException
    //   958	970	593	java/io/IOException
    //   973	990	593	java/io/IOException
    //   993	1073	593	java/io/IOException
    //   1076	1133	593	java/io/IOException
    //   1136	1160	593	java/io/IOException
    //   1160	1169	593	java/io/IOException
    //   1174	1205	593	java/io/IOException
    //   1208	1241	593	java/io/IOException
    //   1244	1268	593	java/io/IOException
    //   1271	1330	593	java/io/IOException
    //   1333	1371	593	java/io/IOException
    //   1374	1379	593	java/io/IOException
    //   1160	1169	1172	java/lang/IllegalArgumentException
    //   17	30	1780	java/io/IOException
    //   30	96	1789	java/io/IOException
    //   874	880	1794	java/lang/NumberFormatException
  }
  
  /* Error */
  void readPackageRestrictionsLPr(int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 17
    //   3: aload_0
    //   4: iload_1
    //   5: invokespecial 2726	com/android/server/pm/Settings:getUserPackagesStateFile	(I)Ljava/io/File;
    //   8: astore 19
    //   10: aload_0
    //   11: iload_1
    //   12: invokespecial 2728	com/android/server/pm/Settings:getUserPackagesStateBackupFile	(I)Ljava/io/File;
    //   15: astore 18
    //   17: aload 18
    //   19: invokevirtual 478	java/io/File:exists	()Z
    //   22: ifeq +975 -> 997
    //   25: new 1654	java/io/FileInputStream
    //   28: dup
    //   29: aload 18
    //   31: invokespecial 1657	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   34: astore 18
    //   36: aload_0
    //   37: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   40: ldc_w 2730
    //   43: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: pop
    //   47: iconst_4
    //   48: ldc_w 2732
    //   51: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   54: aload 18
    //   56: astore 17
    //   58: aload 19
    //   60: invokevirtual 478	java/io/File:exists	()Z
    //   63: ifeq +41 -> 104
    //   66: ldc_w 988
    //   69: new 413	java/lang/StringBuilder
    //   72: dup
    //   73: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   76: ldc_w 2734
    //   79: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: aload 19
    //   84: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   87: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   90: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   93: pop
    //   94: aload 19
    //   96: invokevirtual 2556	java/io/File:delete	()Z
    //   99: pop
    //   100: aload 18
    //   102: astore 17
    //   104: aload 17
    //   106: ifnonnull +888 -> 994
    //   109: aload 19
    //   111: invokevirtual 478	java/io/File:exists	()Z
    //   114: ifne +141 -> 255
    //   117: aload_0
    //   118: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   121: ldc_w 2736
    //   124: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: pop
    //   128: iconst_4
    //   129: ldc_w 2738
    //   132: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   135: aload_0
    //   136: getfield 359	com/android/server/pm/Settings:mPackages	Landroid/util/ArrayMap;
    //   139: invokevirtual 1598	android/util/ArrayMap:values	()Ljava/util/Collection;
    //   142: invokeinterface 709 1 0
    //   147: astore 17
    //   149: aload 17
    //   151: invokeinterface 714 1 0
    //   156: ifeq +98 -> 254
    //   159: aload 17
    //   161: invokeinterface 718 1 0
    //   166: checkcast 505	com/android/server/pm/PackageSetting
    //   169: iload_1
    //   170: lconst_0
    //   171: iconst_0
    //   172: iconst_1
    //   173: iconst_0
    //   174: iconst_0
    //   175: iconst_0
    //   176: iconst_0
    //   177: aconst_null
    //   178: aconst_null
    //   179: aconst_null
    //   180: iconst_0
    //   181: iconst_0
    //   182: iconst_0
    //   183: invokevirtual 1084	com/android/server/pm/PackageSetting:setUserState	(IJIZZZZZLjava/lang/String;Landroid/util/ArraySet;Landroid/util/ArraySet;ZII)V
    //   186: goto -37 -> 149
    //   189: astore 17
    //   191: aload_0
    //   192: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   195: ldc_w 2585
    //   198: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: aload 17
    //   203: invokevirtual 2586	org/xmlpull/v1/XmlPullParserException:toString	()Ljava/lang/String;
    //   206: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: pop
    //   210: bipush 6
    //   212: new 413	java/lang/StringBuilder
    //   215: dup
    //   216: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   219: ldc_w 2740
    //   222: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   225: aload 17
    //   227: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   230: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   233: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   236: ldc_w 988
    //   239: ldc_w 2742
    //   242: aload 17
    //   244: invokestatic 2592	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   247: pop
    //   248: return
    //   249: astore 18
    //   251: goto -147 -> 104
    //   254: return
    //   255: new 1654	java/io/FileInputStream
    //   258: dup
    //   259: aload 19
    //   261: invokespecial 1657	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   264: astore 17
    //   266: invokestatic 1666	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   269: astore 20
    //   271: aload 20
    //   273: aload 17
    //   275: getstatic 2570	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   278: invokevirtual 2574	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   281: invokeinterface 1670 3 0
    //   286: aload 20
    //   288: invokeinterface 1177 1 0
    //   293: istore_2
    //   294: iload_2
    //   295: iconst_2
    //   296: if_icmpeq +8 -> 304
    //   299: iload_2
    //   300: iconst_1
    //   301: if_icmpne -15 -> 286
    //   304: iload_2
    //   305: iconst_2
    //   306: if_icmpeq +22 -> 328
    //   309: aload_0
    //   310: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   313: ldc_w 2744
    //   316: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   319: pop
    //   320: iconst_5
    //   321: ldc_w 2746
    //   324: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   327: return
    //   328: iconst_0
    //   329: istore_3
    //   330: aload 20
    //   332: invokeinterface 1175 1 0
    //   337: istore 5
    //   339: aload 20
    //   341: invokeinterface 1177 1 0
    //   346: istore_2
    //   347: iload_2
    //   348: iconst_1
    //   349: if_icmpeq +614 -> 963
    //   352: iload_2
    //   353: iconst_3
    //   354: if_icmpne +15 -> 369
    //   357: aload 20
    //   359: invokeinterface 1175 1 0
    //   364: iload 5
    //   366: if_icmple +597 -> 963
    //   369: iload_2
    //   370: iconst_3
    //   371: if_icmpeq -32 -> 339
    //   374: iload_2
    //   375: iconst_4
    //   376: if_icmpeq -37 -> 339
    //   379: aload 20
    //   381: invokeinterface 1180 1 0
    //   386: astore 18
    //   388: aload 18
    //   390: ldc -101
    //   392: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   395: ifeq +446 -> 841
    //   398: aload 20
    //   400: aconst_null
    //   401: ldc 71
    //   403: invokeinterface 1184 3 0
    //   408: astore 18
    //   410: aload_0
    //   411: getfield 359	com/android/server/pm/Settings:mPackages	Landroid/util/ArrayMap;
    //   414: aload 18
    //   416: invokevirtual 560	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   419: checkcast 505	com/android/server/pm/PackageSetting
    //   422: astore 21
    //   424: aload 21
    //   426: ifnonnull +39 -> 465
    //   429: ldc_w 988
    //   432: new 413	java/lang/StringBuilder
    //   435: dup
    //   436: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   439: ldc_w 2748
    //   442: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   445: aload 18
    //   447: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   450: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   453: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   456: pop
    //   457: aload 20
    //   459: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   462: goto -123 -> 339
    //   465: aload 20
    //   467: ldc 32
    //   469: lconst_0
    //   470: invokestatic 2752	com/android/internal/util/XmlUtils:readLongAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;J)J
    //   473: lstore 9
    //   475: aload 20
    //   477: ldc 68
    //   479: iconst_1
    //   480: invokestatic 2756	com/android/internal/util/XmlUtils:readBooleanAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;Z)Z
    //   483: istore 12
    //   485: aload 20
    //   487: ldc 86
    //   489: iconst_0
    //   490: invokestatic 2756	com/android/internal/util/XmlUtils:readBooleanAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;Z)Z
    //   493: istore 13
    //   495: aload 20
    //   497: ldc 74
    //   499: iconst_0
    //   500: invokestatic 2756	com/android/internal/util/XmlUtils:readBooleanAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;Z)Z
    //   503: istore 14
    //   505: aload 20
    //   507: aconst_null
    //   508: ldc 26
    //   510: invokeinterface 1184 3 0
    //   515: astore 18
    //   517: aload 18
    //   519: ifnonnull +184 -> 703
    //   522: iconst_0
    //   523: istore 11
    //   525: aload 20
    //   527: aconst_null
    //   528: ldc 65
    //   530: invokeinterface 1184 3 0
    //   535: astore 18
    //   537: aload 18
    //   539: ifnonnull +174 -> 713
    //   542: aload 20
    //   544: ldc 89
    //   546: iconst_0
    //   547: invokestatic 2756	com/android/internal/util/XmlUtils:readBooleanAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;Z)Z
    //   550: istore 15
    //   552: aload 20
    //   554: ldc 29
    //   556: iconst_0
    //   557: invokestatic 2756	com/android/internal/util/XmlUtils:readBooleanAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;Z)Z
    //   560: istore 16
    //   562: aload 20
    //   564: ldc 47
    //   566: iconst_0
    //   567: invokestatic 2659	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   570: istore 6
    //   572: aload 20
    //   574: aconst_null
    //   575: ldc 50
    //   577: invokeinterface 1184 3 0
    //   582: astore 22
    //   584: aload 20
    //   586: ldc 41
    //   588: iconst_0
    //   589: invokestatic 2659	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   592: istore 7
    //   594: aload 20
    //   596: ldc 23
    //   598: iconst_0
    //   599: invokestatic 2659	com/android/internal/util/XmlUtils:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   602: istore 4
    //   604: iload_3
    //   605: istore_2
    //   606: iload 4
    //   608: iload_3
    //   609: if_icmple +6 -> 615
    //   612: iload 4
    //   614: istore_2
    //   615: aconst_null
    //   616: astore 19
    //   618: aconst_null
    //   619: astore 18
    //   621: aload 20
    //   623: invokeinterface 1175 1 0
    //   628: istore_3
    //   629: aload 20
    //   631: invokeinterface 1177 1 0
    //   636: istore 8
    //   638: iload 8
    //   640: iconst_1
    //   641: if_icmpeq +103 -> 744
    //   644: iload 8
    //   646: iconst_3
    //   647: if_icmpne +14 -> 661
    //   650: aload 20
    //   652: invokeinterface 1175 1 0
    //   657: iload_3
    //   658: if_icmple +86 -> 744
    //   661: iload 8
    //   663: iconst_3
    //   664: if_icmpeq -35 -> 629
    //   667: iload 8
    //   669: iconst_4
    //   670: if_icmpeq -41 -> 629
    //   673: aload 20
    //   675: invokeinterface 1180 1 0
    //   680: astore 23
    //   682: aload 23
    //   684: ldc -107
    //   686: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   689: ifeq +34 -> 723
    //   692: aload_0
    //   693: aload 20
    //   695: invokespecial 2758	com/android/server/pm/Settings:readComponentsLPr	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/ArraySet;
    //   698: astore 19
    //   700: goto -71 -> 629
    //   703: aload 18
    //   705: invokestatic 2532	java/lang/Boolean:parseBoolean	(Ljava/lang/String;)Z
    //   708: istore 11
    //   710: goto -185 -> 525
    //   713: aload 18
    //   715: invokestatic 2532	java/lang/Boolean:parseBoolean	(Ljava/lang/String;)Z
    //   718: istore 11
    //   720: goto -178 -> 542
    //   723: aload 23
    //   725: ldc -113
    //   727: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   730: ifeq -101 -> 629
    //   733: aload_0
    //   734: aload 20
    //   736: invokespecial 2758	com/android/server/pm/Settings:readComponentsLPr	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/ArraySet;
    //   739: astore 18
    //   741: goto -112 -> 629
    //   744: aload 21
    //   746: iload_1
    //   747: lload 9
    //   749: iload 6
    //   751: iload 12
    //   753: iload 13
    //   755: iload 14
    //   757: iload 11
    //   759: iload 15
    //   761: aload 22
    //   763: aload 19
    //   765: aload 18
    //   767: iload 16
    //   769: iload 7
    //   771: iload 4
    //   773: invokevirtual 1084	com/android/server/pm/PackageSetting:setUserState	(IJIZZZZZLjava/lang/String;Landroid/util/ArraySet;Landroid/util/ArraySet;ZII)V
    //   776: iload_2
    //   777: istore_3
    //   778: goto -439 -> 339
    //   781: astore 17
    //   783: aload_0
    //   784: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   787: ldc_w 2585
    //   790: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   793: aload 17
    //   795: invokevirtual 2613	java/io/IOException:toString	()Ljava/lang/String;
    //   798: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   801: pop
    //   802: bipush 6
    //   804: new 413	java/lang/StringBuilder
    //   807: dup
    //   808: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   811: ldc_w 2588
    //   814: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   817: aload 17
    //   819: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   822: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   825: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   828: ldc_w 988
    //   831: ldc_w 2742
    //   834: aload 17
    //   836: invokestatic 2592	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   839: pop
    //   840: return
    //   841: aload 18
    //   843: ldc_w 1679
    //   846: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   849: ifeq +13 -> 862
    //   852: aload_0
    //   853: aload 20
    //   855: iload_1
    //   856: invokevirtual 2622	com/android/server/pm/Settings:readPreferredActivitiesLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   859: goto -520 -> 339
    //   862: aload 18
    //   864: ldc -89
    //   866: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   869: ifeq +13 -> 882
    //   872: aload_0
    //   873: aload 20
    //   875: iload_1
    //   876: invokespecial 2624	com/android/server/pm/Settings:readPersistentPreferredActivitiesLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   879: goto -540 -> 339
    //   882: aload 18
    //   884: ldc -125
    //   886: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   889: ifeq +13 -> 902
    //   892: aload_0
    //   893: aload 20
    //   895: iload_1
    //   896: invokespecial 2626	com/android/server/pm/Settings:readCrossProfileIntentFiltersLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   899: goto -560 -> 339
    //   902: aload 18
    //   904: ldc -122
    //   906: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   909: ifeq +13 -> 922
    //   912: aload_0
    //   913: aload 20
    //   915: iload_1
    //   916: invokevirtual 2628	com/android/server/pm/Settings:readDefaultAppsLPw	(Lorg/xmlpull/v1/XmlPullParser;I)V
    //   919: goto -580 -> 339
    //   922: ldc_w 988
    //   925: new 413	java/lang/StringBuilder
    //   928: dup
    //   929: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   932: ldc_w 2760
    //   935: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   938: aload 20
    //   940: invokeinterface 1180 1 0
    //   945: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   948: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   951: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   954: pop
    //   955: aload 20
    //   957: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   960: goto -621 -> 339
    //   963: aload 17
    //   965: invokevirtual 2697	java/io/FileInputStream:close	()V
    //   968: aload_0
    //   969: getfield 411	com/android/server/pm/Settings:mNextAppLinkGeneration	Landroid/util/SparseIntArray;
    //   972: iload_1
    //   973: iload_3
    //   974: iconst_1
    //   975: iadd
    //   976: invokevirtual 2763	android/util/SparseIntArray:put	(II)V
    //   979: return
    //   980: astore 17
    //   982: goto -199 -> 783
    //   985: astore 17
    //   987: aload 18
    //   989: astore 17
    //   991: goto -740 -> 251
    //   994: goto -728 -> 266
    //   997: aconst_null
    //   998: astore 17
    //   1000: goto -896 -> 104
    //   1003: astore 17
    //   1005: goto -814 -> 191
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1008	0	this	Settings
    //   0	1008	1	paramInt	int
    //   293	484	2	i	int
    //   329	647	3	j	int
    //   602	170	4	k	int
    //   337	30	5	m	int
    //   570	180	6	n	int
    //   592	178	7	i1	int
    //   636	35	8	i2	int
    //   473	275	9	l	long
    //   523	235	11	bool1	boolean
    //   483	269	12	bool2	boolean
    //   493	261	13	bool3	boolean
    //   503	253	14	bool4	boolean
    //   550	210	15	bool5	boolean
    //   560	208	16	bool6	boolean
    //   1	159	17	localObject1	Object
    //   189	54	17	localXmlPullParserException1	XmlPullParserException
    //   264	10	17	localFileInputStream	java.io.FileInputStream
    //   781	183	17	localIOException1	IOException
    //   980	1	17	localIOException2	IOException
    //   985	1	17	localIOException3	IOException
    //   989	10	17	localObject2	Object
    //   1003	1	17	localXmlPullParserException2	XmlPullParserException
    //   15	86	18	localObject3	Object
    //   249	1	18	localIOException4	IOException
    //   386	602	18	localObject4	Object
    //   8	756	19	localObject5	Object
    //   269	687	20	localXmlPullParser	XmlPullParser
    //   422	323	21	localPackageSetting	PackageSetting
    //   582	180	22	str1	String
    //   680	44	23	str2	String
    // Exception table:
    //   from	to	target	type
    //   109	149	189	org/xmlpull/v1/XmlPullParserException
    //   149	186	189	org/xmlpull/v1/XmlPullParserException
    //   255	266	189	org/xmlpull/v1/XmlPullParserException
    //   25	36	249	java/io/IOException
    //   266	286	781	java/io/IOException
    //   286	294	781	java/io/IOException
    //   309	327	781	java/io/IOException
    //   330	339	781	java/io/IOException
    //   339	347	781	java/io/IOException
    //   357	369	781	java/io/IOException
    //   379	424	781	java/io/IOException
    //   429	462	781	java/io/IOException
    //   465	517	781	java/io/IOException
    //   525	537	781	java/io/IOException
    //   542	604	781	java/io/IOException
    //   621	629	781	java/io/IOException
    //   629	638	781	java/io/IOException
    //   650	661	781	java/io/IOException
    //   673	700	781	java/io/IOException
    //   703	710	781	java/io/IOException
    //   713	720	781	java/io/IOException
    //   723	741	781	java/io/IOException
    //   744	776	781	java/io/IOException
    //   841	859	781	java/io/IOException
    //   862	879	781	java/io/IOException
    //   882	899	781	java/io/IOException
    //   902	919	781	java/io/IOException
    //   922	960	781	java/io/IOException
    //   963	979	781	java/io/IOException
    //   109	149	980	java/io/IOException
    //   149	186	980	java/io/IOException
    //   255	266	980	java/io/IOException
    //   36	54	985	java/io/IOException
    //   58	100	985	java/io/IOException
    //   266	286	1003	org/xmlpull/v1/XmlPullParserException
    //   286	294	1003	org/xmlpull/v1/XmlPullParserException
    //   309	327	1003	org/xmlpull/v1/XmlPullParserException
    //   330	339	1003	org/xmlpull/v1/XmlPullParserException
    //   339	347	1003	org/xmlpull/v1/XmlPullParserException
    //   357	369	1003	org/xmlpull/v1/XmlPullParserException
    //   379	424	1003	org/xmlpull/v1/XmlPullParserException
    //   429	462	1003	org/xmlpull/v1/XmlPullParserException
    //   465	517	1003	org/xmlpull/v1/XmlPullParserException
    //   525	537	1003	org/xmlpull/v1/XmlPullParserException
    //   542	604	1003	org/xmlpull/v1/XmlPullParserException
    //   621	629	1003	org/xmlpull/v1/XmlPullParserException
    //   629	638	1003	org/xmlpull/v1/XmlPullParserException
    //   650	661	1003	org/xmlpull/v1/XmlPullParserException
    //   673	700	1003	org/xmlpull/v1/XmlPullParserException
    //   703	710	1003	org/xmlpull/v1/XmlPullParserException
    //   713	720	1003	org/xmlpull/v1/XmlPullParserException
    //   723	741	1003	org/xmlpull/v1/XmlPullParserException
    //   744	776	1003	org/xmlpull/v1/XmlPullParserException
    //   841	859	1003	org/xmlpull/v1/XmlPullParserException
    //   862	879	1003	org/xmlpull/v1/XmlPullParserException
    //   882	899	1003	org/xmlpull/v1/XmlPullParserException
    //   902	919	1003	org/xmlpull/v1/XmlPullParserException
    //   922	960	1003	org/xmlpull/v1/XmlPullParserException
    //   963	979	1003	org/xmlpull/v1/XmlPullParserException
  }
  
  void readPreferredActivitiesLPw(XmlPullParser paramXmlPullParser, int paramInt)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if ((j == 1) || ((j == 3) && (paramXmlPullParser.getDepth() <= i))) {
        break;
      }
      if ((j != 3) && (j != 4)) {
        if (paramXmlPullParser.getName().equals("item"))
        {
          PreferredActivity localPreferredActivity = new PreferredActivity(paramXmlPullParser);
          if (localPreferredActivity.mPref.getParseError() == null) {
            editPreferredActivitiesLPw(paramInt).addFilter(localPreferredActivity);
          } else {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + localPreferredActivity.mPref.getParseError() + " at " + paramXmlPullParser.getPositionDescription());
          }
        }
        else
        {
          PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + paramXmlPullParser.getName());
          XmlUtils.skipCurrentTag(paramXmlPullParser);
        }
      }
    }
  }
  
  /* Error */
  void readStoppedLPw()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_0
    //   3: getfield 488	com/android/server/pm/Settings:mBackupStoppedPackagesFilename	Ljava/io/File;
    //   6: invokevirtual 478	java/io/File:exists	()Z
    //   9: ifeq +581 -> 590
    //   12: new 1654	java/io/FileInputStream
    //   15: dup
    //   16: aload_0
    //   17: getfield 488	com/android/server/pm/Settings:mBackupStoppedPackagesFilename	Ljava/io/File;
    //   20: invokespecial 1657	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   23: astore 4
    //   25: aload_0
    //   26: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   29: ldc_w 2730
    //   32: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: pop
    //   36: iconst_4
    //   37: ldc_w 2732
    //   40: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   43: aload 4
    //   45: astore_3
    //   46: aload_0
    //   47: getfield 459	com/android/server/pm/Settings:mSettingsFilename	Ljava/io/File;
    //   50: invokevirtual 478	java/io/File:exists	()Z
    //   53: ifeq +44 -> 97
    //   56: ldc_w 988
    //   59: new 413	java/lang/StringBuilder
    //   62: dup
    //   63: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   66: ldc_w 2734
    //   69: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: aload_0
    //   73: getfield 484	com/android/server/pm/Settings:mStoppedPackagesFilename	Ljava/io/File;
    //   76: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   79: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   82: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   85: pop
    //   86: aload_0
    //   87: getfield 484	com/android/server/pm/Settings:mStoppedPackagesFilename	Ljava/io/File;
    //   90: invokevirtual 2556	java/io/File:delete	()Z
    //   93: pop
    //   94: aload 4
    //   96: astore_3
    //   97: aload_3
    //   98: ifnonnull +489 -> 587
    //   101: aload_0
    //   102: getfield 484	com/android/server/pm/Settings:mStoppedPackagesFilename	Ljava/io/File;
    //   105: invokevirtual 478	java/io/File:exists	()Z
    //   108: ifne +133 -> 241
    //   111: aload_0
    //   112: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   115: ldc_w 2736
    //   118: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: pop
    //   122: iconst_4
    //   123: ldc_w 2765
    //   126: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   129: aload_0
    //   130: getfield 359	com/android/server/pm/Settings:mPackages	Landroid/util/ArrayMap;
    //   133: invokevirtual 1598	android/util/ArrayMap:values	()Ljava/util/Collection;
    //   136: invokeinterface 709 1 0
    //   141: astore_3
    //   142: aload_3
    //   143: invokeinterface 714 1 0
    //   148: ifeq +92 -> 240
    //   151: aload_3
    //   152: invokeinterface 718 1 0
    //   157: checkcast 505	com/android/server/pm/PackageSetting
    //   160: astore 4
    //   162: aload 4
    //   164: iconst_0
    //   165: iconst_0
    //   166: invokevirtual 2768	com/android/server/pm/PackageSetting:setStopped	(ZI)V
    //   169: aload 4
    //   171: iconst_0
    //   172: iconst_0
    //   173: invokevirtual 2771	com/android/server/pm/PackageSetting:setNotLaunched	(ZI)V
    //   176: goto -34 -> 142
    //   179: astore_3
    //   180: aload_0
    //   181: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   184: ldc_w 2585
    //   187: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: aload_3
    //   191: invokevirtual 2586	org/xmlpull/v1/XmlPullParserException:toString	()Ljava/lang/String;
    //   194: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   197: pop
    //   198: bipush 6
    //   200: new 413	java/lang/StringBuilder
    //   203: dup
    //   204: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   207: ldc_w 2740
    //   210: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   213: aload_3
    //   214: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   217: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   223: ldc_w 988
    //   226: ldc_w 2742
    //   229: aload_3
    //   230: invokestatic 2592	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   233: pop
    //   234: return
    //   235: astore 4
    //   237: goto -140 -> 97
    //   240: return
    //   241: new 1654	java/io/FileInputStream
    //   244: dup
    //   245: aload_0
    //   246: getfield 484	com/android/server/pm/Settings:mStoppedPackagesFilename	Ljava/io/File;
    //   249: invokespecial 1657	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   252: astore_3
    //   253: invokestatic 1666	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   256: astore 4
    //   258: aload 4
    //   260: aload_3
    //   261: aconst_null
    //   262: invokeinterface 1670 3 0
    //   267: aload 4
    //   269: invokeinterface 1177 1 0
    //   274: istore_1
    //   275: iload_1
    //   276: iconst_2
    //   277: if_icmpeq +8 -> 285
    //   280: iload_1
    //   281: iconst_1
    //   282: if_icmpne -15 -> 267
    //   285: iload_1
    //   286: iconst_2
    //   287: if_icmpeq +22 -> 309
    //   290: aload_0
    //   291: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   294: ldc_w 2773
    //   297: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: pop
    //   301: iconst_5
    //   302: ldc_w 2746
    //   305: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   308: return
    //   309: aload 4
    //   311: invokeinterface 1175 1 0
    //   316: istore_1
    //   317: aload 4
    //   319: invokeinterface 1177 1 0
    //   324: istore_2
    //   325: iload_2
    //   326: iconst_1
    //   327: if_icmpeq +244 -> 571
    //   330: iload_2
    //   331: iconst_3
    //   332: if_icmpne +14 -> 346
    //   335: aload 4
    //   337: invokeinterface 1175 1 0
    //   342: iload_1
    //   343: if_icmple +228 -> 571
    //   346: iload_2
    //   347: iconst_3
    //   348: if_icmpeq -31 -> 317
    //   351: iload_2
    //   352: iconst_4
    //   353: if_icmpeq -36 -> 317
    //   356: aload 4
    //   358: invokeinterface 1180 1 0
    //   363: ldc -101
    //   365: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   368: ifeq +162 -> 530
    //   371: aload 4
    //   373: aconst_null
    //   374: ldc 71
    //   376: invokeinterface 1184 3 0
    //   381: astore 5
    //   383: aload_0
    //   384: getfield 359	com/android/server/pm/Settings:mPackages	Landroid/util/ArrayMap;
    //   387: aload 5
    //   389: invokevirtual 560	android/util/ArrayMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   392: checkcast 505	com/android/server/pm/PackageSetting
    //   395: astore 6
    //   397: aload 6
    //   399: ifnull +44 -> 443
    //   402: aload 6
    //   404: iconst_1
    //   405: iconst_0
    //   406: invokevirtual 2768	com/android/server/pm/PackageSetting:setStopped	(ZI)V
    //   409: ldc_w 2682
    //   412: aload 4
    //   414: aconst_null
    //   415: ldc 74
    //   417: invokeinterface 1184 3 0
    //   422: invokevirtual 729	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   425: ifeq +10 -> 435
    //   428: aload 6
    //   430: iconst_1
    //   431: iconst_0
    //   432: invokevirtual 2771	com/android/server/pm/PackageSetting:setNotLaunched	(ZI)V
    //   435: aload 4
    //   437: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   440: goto -123 -> 317
    //   443: ldc_w 988
    //   446: new 413	java/lang/StringBuilder
    //   449: dup
    //   450: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   453: ldc_w 2748
    //   456: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   459: aload 5
    //   461: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   464: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   467: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   470: pop
    //   471: goto -36 -> 435
    //   474: astore_3
    //   475: aload_0
    //   476: getfield 416	com/android/server/pm/Settings:mReadMessages	Ljava/lang/StringBuilder;
    //   479: ldc_w 2585
    //   482: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   485: aload_3
    //   486: invokevirtual 2613	java/io/IOException:toString	()Ljava/lang/String;
    //   489: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   492: pop
    //   493: bipush 6
    //   495: new 413	java/lang/StringBuilder
    //   498: dup
    //   499: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   502: ldc_w 2588
    //   505: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   508: aload_3
    //   509: invokevirtual 522	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   512: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   515: invokestatic 533	com/android/server/pm/PackageManagerService:reportSettingsProblem	(ILjava/lang/String;)V
    //   518: ldc_w 988
    //   521: ldc_w 2742
    //   524: aload_3
    //   525: invokestatic 2592	android/util/Slog:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   528: pop
    //   529: return
    //   530: ldc_w 988
    //   533: new 413	java/lang/StringBuilder
    //   536: dup
    //   537: invokespecial 414	java/lang/StringBuilder:<init>	()V
    //   540: ldc_w 2760
    //   543: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   546: aload 4
    //   548: invokeinterface 1180 1 0
    //   553: invokevirtual 515	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   556: invokevirtual 527	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   559: invokestatic 785	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   562: pop
    //   563: aload 4
    //   565: invokestatic 1208	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   568: goto -251 -> 317
    //   571: aload_3
    //   572: invokevirtual 2697	java/io/FileInputStream:close	()V
    //   575: return
    //   576: astore_3
    //   577: goto -102 -> 475
    //   580: astore_3
    //   581: aload 4
    //   583: astore_3
    //   584: goto -347 -> 237
    //   587: goto -334 -> 253
    //   590: aconst_null
    //   591: astore_3
    //   592: goto -495 -> 97
    //   595: astore_3
    //   596: goto -416 -> 180
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	599	0	this	Settings
    //   274	70	1	i	int
    //   324	30	2	j	int
    //   1	151	3	localObject1	Object
    //   179	51	3	localXmlPullParserException1	XmlPullParserException
    //   252	9	3	localFileInputStream	java.io.FileInputStream
    //   474	98	3	localIOException1	IOException
    //   576	1	3	localIOException2	IOException
    //   580	1	3	localIOException3	IOException
    //   583	9	3	localObject2	Object
    //   595	1	3	localXmlPullParserException2	XmlPullParserException
    //   23	147	4	localObject3	Object
    //   235	1	4	localIOException4	IOException
    //   256	326	4	localXmlPullParser	XmlPullParser
    //   381	79	5	str	String
    //   395	34	6	localPackageSetting	PackageSetting
    // Exception table:
    //   from	to	target	type
    //   101	142	179	org/xmlpull/v1/XmlPullParserException
    //   142	176	179	org/xmlpull/v1/XmlPullParserException
    //   241	253	179	org/xmlpull/v1/XmlPullParserException
    //   12	25	235	java/io/IOException
    //   253	267	474	java/io/IOException
    //   267	275	474	java/io/IOException
    //   290	308	474	java/io/IOException
    //   309	317	474	java/io/IOException
    //   317	325	474	java/io/IOException
    //   335	346	474	java/io/IOException
    //   356	397	474	java/io/IOException
    //   402	435	474	java/io/IOException
    //   435	440	474	java/io/IOException
    //   443	471	474	java/io/IOException
    //   530	568	474	java/io/IOException
    //   571	575	474	java/io/IOException
    //   101	142	576	java/io/IOException
    //   142	176	576	java/io/IOException
    //   241	253	576	java/io/IOException
    //   25	43	580	java/io/IOException
    //   46	94	580	java/io/IOException
    //   253	267	595	org/xmlpull/v1/XmlPullParserException
    //   267	275	595	org/xmlpull/v1/XmlPullParserException
    //   290	308	595	org/xmlpull/v1/XmlPullParserException
    //   309	317	595	org/xmlpull/v1/XmlPullParserException
    //   317	325	595	org/xmlpull/v1/XmlPullParserException
    //   335	346	595	org/xmlpull/v1/XmlPullParserException
    //   356	397	595	org/xmlpull/v1/XmlPullParserException
    //   402	435	595	org/xmlpull/v1/XmlPullParserException
    //   435	440	595	org/xmlpull/v1/XmlPullParserException
    //   443	471	595	org/xmlpull/v1/XmlPullParserException
    //   530	568	595	org/xmlpull/v1/XmlPullParserException
    //   571	575	595	org/xmlpull/v1/XmlPullParserException
  }
  
  void removeCrossProfileIntentFiltersLPw(int paramInt)
  {
    for (;;)
    {
      int i;
      int m;
      int j;
      synchronized (this.mCrossProfileIntentResolvers)
      {
        if (this.mCrossProfileIntentResolvers.get(paramInt) != null)
        {
          this.mCrossProfileIntentResolvers.remove(paramInt);
          writePackageRestrictionsLPr(paramInt);
        }
        int k = this.mCrossProfileIntentResolvers.size();
        i = 0;
        if (i >= k) {
          break;
        }
        m = this.mCrossProfileIntentResolvers.keyAt(i);
        CrossProfileIntentResolver localCrossProfileIntentResolver = (CrossProfileIntentResolver)this.mCrossProfileIntentResolvers.get(m);
        j = 0;
        Iterator localIterator = new ArraySet(localCrossProfileIntentResolver.filterSet()).iterator();
        if (localIterator.hasNext())
        {
          CrossProfileIntentFilter localCrossProfileIntentFilter = (CrossProfileIntentFilter)localIterator.next();
          if (localCrossProfileIntentFilter.getTargetUserId() != paramInt) {
            continue;
          }
          j = 1;
          localCrossProfileIntentResolver.removeFilter(localCrossProfileIntentFilter);
        }
      }
      if (j != 0) {
        writePackageRestrictionsLPr(m);
      }
      i += 1;
    }
  }
  
  void removeDisabledSystemPackageLPw(String paramString)
  {
    this.mDisabledSysPackages.remove(paramString);
  }
  
  boolean removeIntentFilterVerificationLPw(String paramString, int paramInt)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null)
    {
      if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
        Slog.w("PackageManager", "No package known: " + paramString);
      }
      return false;
    }
    localPackageSetting.clearDomainVerificationStatusForUser(paramInt);
    return true;
  }
  
  boolean removeIntentFilterVerificationLPw(String paramString, int[] paramArrayOfInt)
  {
    boolean bool = false;
    int i = 0;
    int j = paramArrayOfInt.length;
    while (i < j)
    {
      bool |= removeIntentFilterVerificationLPw(paramString, paramArrayOfInt[i]);
      i += 1;
    }
    return bool;
  }
  
  int removePackageLPw(String paramString)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting != null)
    {
      this.mPackages.remove(paramString);
      removeInstallerPackageStatus(paramString);
      if (localPackageSetting.sharedUser != null)
      {
        localPackageSetting.sharedUser.removePackage(localPackageSetting);
        if (localPackageSetting.sharedUser.packages.size() == 0)
        {
          this.mSharedUsers.remove(localPackageSetting.sharedUser.name);
          removeUserIdLPw(localPackageSetting.sharedUser.userId);
          return localPackageSetting.sharedUser.userId;
        }
      }
      else
      {
        removeUserIdLPw(localPackageSetting.appId);
        return localPackageSetting.appId;
      }
    }
    return -1;
  }
  
  void removeUserLPw(int paramInt)
  {
    Iterator localIterator = this.mPackages.entrySet().iterator();
    while (localIterator.hasNext()) {
      ((PackageSetting)((Map.Entry)localIterator.next()).getValue()).removeUser(paramInt);
    }
    this.mPreferredActivities.remove(paramInt);
    getUserPackagesStateFile(paramInt).delete();
    getUserPackagesStateBackupFile(paramInt).delete();
    removeCrossProfileIntentFiltersLPw(paramInt);
    RuntimePermissionPersistence.-wrap0(this.mRuntimePermissionsPersistence, paramInt);
    writePackageListLPr();
  }
  
  boolean setDefaultBrowserPackageNameLPw(String paramString, int paramInt)
  {
    if (paramInt == -1) {
      return false;
    }
    this.mDefaultBrowserApp.put(paramInt, paramString);
    writePackageRestrictionsLPr(paramInt);
    return true;
  }
  
  boolean setDefaultDialerPackageNameLPw(String paramString, int paramInt)
  {
    if (paramInt == -1) {
      return false;
    }
    this.mDefaultDialerApp.put(paramInt, paramString);
    writePackageRestrictionsLPr(paramInt);
    return true;
  }
  
  void setInstallStatus(String paramString, int paramInt)
  {
    paramString = (PackageSetting)this.mPackages.get(paramString);
    if ((paramString != null) && (paramString.getInstallStatus() != paramInt)) {
      paramString.setInstallStatus(paramInt);
    }
  }
  
  void setInstallerPackageName(String paramString1, String paramString2)
  {
    paramString1 = (PackageSetting)this.mPackages.get(paramString1);
    if (paramString1 != null)
    {
      paramString1.setInstallerPackageName(paramString2);
      if (paramString2 != null) {
        this.mInstallerPackages.add(paramString2);
      }
    }
  }
  
  boolean setPackageStoppedStateLPw(PackageManagerService paramPackageManagerService, String paramString, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    int i = UserHandle.getAppId(paramInt1);
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null) {
      throw new IllegalArgumentException("Unknown package: " + paramString);
    }
    if ((!paramBoolean2) && (i != localPackageSetting.appId)) {
      throw new SecurityException("Permission Denial: attempt to change stopped state from pid=" + Binder.getCallingPid() + ", uid=" + paramInt1 + ", package uid=" + localPackageSetting.appId);
    }
    if (localPackageSetting.getStopped(paramInt2) != paramBoolean1)
    {
      localPackageSetting.setStopped(paramBoolean1, paramInt2);
      if (localPackageSetting.getNotLaunched(paramInt2))
      {
        if (localPackageSetting.installerPackageName != null) {
          paramPackageManagerService.notifyFirstLaunch(localPackageSetting.name, localPackageSetting.installerPackageName, paramInt2);
        }
        localPackageSetting.setNotLaunched(false, paramInt2);
      }
      return true;
    }
    return false;
  }
  
  void transferPermissionsLPw(String paramString1, String paramString2)
  {
    int i = 0;
    while (i < 2)
    {
      if (i == 0) {}
      for (Object localObject = this.mPermissionTrees;; localObject = this.mPermissions)
      {
        localObject = ((ArrayMap)localObject).values().iterator();
        while (((Iterator)localObject).hasNext())
        {
          BasePermission localBasePermission = (BasePermission)((Iterator)localObject).next();
          if (paramString1.equals(localBasePermission.sourcePackage))
          {
            if (PackageManagerService.DEBUG_UPGRADE) {
              Log.v("PackageManager", "Moving permission " + localBasePermission.name + " from pkg " + localBasePermission.sourcePackage + " to " + paramString2);
            }
            localBasePermission.sourcePackage = paramString2;
            localBasePermission.packageSetting = null;
            localBasePermission.perm = null;
            if (localBasePermission.pendingInfo != null) {
              localBasePermission.pendingInfo.packageName = paramString2;
            }
            localBasePermission.uid = 0;
            localBasePermission.setGids(null, false);
          }
        }
      }
      i += 1;
    }
  }
  
  boolean updateIntentFilterVerificationStatusLPw(String paramString, int paramInt1, int paramInt2)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null)
    {
      if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
        Slog.w("PackageManager", "No package known: " + paramString);
      }
      return false;
    }
    int i;
    if (paramInt1 == 2)
    {
      i = this.mNextAppLinkGeneration.get(paramInt2) + 1;
      this.mNextAppLinkGeneration.put(paramInt2, i);
    }
    for (;;)
    {
      localPackageSetting.setDomainVerificationStatusForUser(paramInt1, i, paramInt2);
      return true;
      i = 0;
    }
  }
  
  int updateSharedUserPermsLPw(PackageSetting paramPackageSetting, int paramInt)
  {
    if ((paramPackageSetting == null) || (paramPackageSetting.pkg == null))
    {
      Slog.i("PackageManager", "Trying to update info for null package. Just ignoring");
      return 55536;
    }
    if (paramPackageSetting.sharedUser == null) {
      return 55536;
    }
    SharedUserSetting localSharedUserSetting = paramPackageSetting.sharedUser;
    Iterator localIterator = paramPackageSetting.pkg.requestedPermissions.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      BasePermission localBasePermission = (BasePermission)this.mPermissions.get(str);
      if (localBasePermission != null)
      {
        int j = 0;
        Object localObject1 = localSharedUserSetting.packages.iterator();
        Object localObject2;
        do
        {
          i = j;
          if (!((Iterator)localObject1).hasNext()) {
            break;
          }
          localObject2 = (PackageSetting)((Iterator)localObject1).next();
        } while ((((PackageSetting)localObject2).pkg == null) || (((PackageSetting)localObject2).pkg.packageName.equals(paramPackageSetting.pkg.packageName)) || (!((PackageSetting)localObject2).pkg.requestedPermissions.contains(str)));
        int i = 1;
        if (i == 0)
        {
          localObject1 = localSharedUserSetting.getPermissionsState();
          localObject2 = getDisabledSystemPkgLPr(paramPackageSetting.pkg.packageName);
          if (localObject2 != null)
          {
            j = 0;
            localObject2 = ((PackageSetting)localObject2).pkg.requestedPermissions.iterator();
            do
            {
              i = j;
              if (!((Iterator)localObject2).hasNext()) {
                break;
              }
            } while (!((String)((Iterator)localObject2).next()).equals(str));
            i = 1;
            if (i != 0) {
              break;
            }
          }
          else
          {
            ((PermissionsState)localObject1).updatePermissionFlags(localBasePermission, paramInt, 255, 0);
            if (((PermissionsState)localObject1).revokeInstallPermission(localBasePermission) == 1) {
              return -1;
            }
            if (((PermissionsState)localObject1).revokeRuntimePermission(localBasePermission, paramInt) == 1) {
              return paramInt;
            }
          }
        }
      }
    }
    return 55536;
  }
  
  boolean wasPackageEverLaunchedLPr(String paramString, int paramInt)
  {
    PackageSetting localPackageSetting = (PackageSetting)this.mPackages.get(paramString);
    if (localPackageSetting == null) {
      throw new IllegalArgumentException("Unknown package: " + paramString);
    }
    return !localPackageSetting.getNotLaunched(paramInt);
  }
  
  void writeAllDomainVerificationsLPr(XmlSerializer paramXmlSerializer, int paramInt)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    paramXmlSerializer.startTag(null, "all-intent-filter-verifications");
    int i = this.mPackages.size();
    paramInt = 0;
    while (paramInt < i)
    {
      IntentFilterVerificationInfo localIntentFilterVerificationInfo = ((PackageSetting)this.mPackages.valueAt(paramInt)).getIntentFilterVerificationInfo();
      if (localIntentFilterVerificationInfo != null) {
        writeDomainVerificationsLPr(paramXmlSerializer, localIntentFilterVerificationInfo);
      }
      paramInt += 1;
    }
    paramXmlSerializer.endTag(null, "all-intent-filter-verifications");
  }
  
  void writeAllRuntimePermissionsLPr()
  {
    int[] arrayOfInt = UserManagerService.getInstance().getUserIds();
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      int k = arrayOfInt[i];
      this.mRuntimePermissionsPersistence.writePermissionsForUserAsyncLPr(k);
      i += 1;
    }
  }
  
  void writeAllUsersPackageRestrictionsLPr()
  {
    Object localObject = getAllUsers();
    if (localObject == null) {
      return;
    }
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      writePackageRestrictionsLPr(((UserInfo)((Iterator)localObject).next()).id);
    }
  }
  
  void writeChildPackagesLPw(XmlSerializer paramXmlSerializer, List<String> paramList)
    throws IOException
  {
    if (paramList == null) {
      return;
    }
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      String str = (String)paramList.get(i);
      paramXmlSerializer.startTag(null, "child-package");
      paramXmlSerializer.attribute(null, "name", str);
      paramXmlSerializer.endTag(null, "child-package");
      i += 1;
    }
  }
  
  void writeCrossProfileIntentFiltersLPr(XmlSerializer paramXmlSerializer, int paramInt)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    paramXmlSerializer.startTag(null, "crossProfile-intent-filters");
    Object localObject = (CrossProfileIntentResolver)this.mCrossProfileIntentResolvers.get(paramInt);
    if (localObject != null)
    {
      localObject = ((CrossProfileIntentResolver)localObject).filterSet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        CrossProfileIntentFilter localCrossProfileIntentFilter = (CrossProfileIntentFilter)((Iterator)localObject).next();
        paramXmlSerializer.startTag(null, "item");
        localCrossProfileIntentFilter.writeToXml(paramXmlSerializer);
        paramXmlSerializer.endTag(null, "item");
      }
    }
    paramXmlSerializer.endTag(null, "crossProfile-intent-filters");
  }
  
  void writeDefaultAppsLPr(XmlSerializer paramXmlSerializer, int paramInt)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    paramXmlSerializer.startTag(null, "default-apps");
    String str = (String)this.mDefaultBrowserApp.get(paramInt);
    if (!TextUtils.isEmpty(str))
    {
      paramXmlSerializer.startTag(null, "default-browser");
      paramXmlSerializer.attribute(null, "packageName", str);
      paramXmlSerializer.endTag(null, "default-browser");
    }
    str = (String)this.mDefaultDialerApp.get(paramInt);
    if (!TextUtils.isEmpty(str))
    {
      paramXmlSerializer.startTag(null, "default-dialer");
      paramXmlSerializer.attribute(null, "packageName", str);
      paramXmlSerializer.endTag(null, "default-dialer");
    }
    paramXmlSerializer.endTag(null, "default-apps");
  }
  
  void writeDisabledSysPackageLPr(XmlSerializer paramXmlSerializer, PackageSetting paramPackageSetting)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "updated-package");
    paramXmlSerializer.attribute(null, "name", paramPackageSetting.name);
    if (paramPackageSetting.realName != null) {
      paramXmlSerializer.attribute(null, "realName", paramPackageSetting.realName);
    }
    paramXmlSerializer.attribute(null, "codePath", paramPackageSetting.codePathString);
    paramXmlSerializer.attribute(null, "ft", Long.toHexString(paramPackageSetting.timeStamp));
    paramXmlSerializer.attribute(null, "it", Long.toHexString(paramPackageSetting.firstInstallTime));
    paramXmlSerializer.attribute(null, "ut", Long.toHexString(paramPackageSetting.lastUpdateTime));
    paramXmlSerializer.attribute(null, "version", String.valueOf(paramPackageSetting.versionCode));
    if (!paramPackageSetting.resourcePathString.equals(paramPackageSetting.codePathString)) {
      paramXmlSerializer.attribute(null, "resourcePath", paramPackageSetting.resourcePathString);
    }
    if (paramPackageSetting.legacyNativeLibraryPathString != null) {
      paramXmlSerializer.attribute(null, "nativeLibraryPath", paramPackageSetting.legacyNativeLibraryPathString);
    }
    if (paramPackageSetting.primaryCpuAbiString != null) {
      paramXmlSerializer.attribute(null, "primaryCpuAbi", paramPackageSetting.primaryCpuAbiString);
    }
    if (paramPackageSetting.secondaryCpuAbiString != null) {
      paramXmlSerializer.attribute(null, "secondaryCpuAbi", paramPackageSetting.secondaryCpuAbiString);
    }
    if (paramPackageSetting.cpuAbiOverrideString != null) {
      paramXmlSerializer.attribute(null, "cpuAbiOverride", paramPackageSetting.cpuAbiOverrideString);
    }
    if (paramPackageSetting.sharedUser == null) {
      paramXmlSerializer.attribute(null, "userId", Integer.toString(paramPackageSetting.appId));
    }
    for (;;)
    {
      if (paramPackageSetting.parentPackageName != null) {
        paramXmlSerializer.attribute(null, "parentPackageName", paramPackageSetting.parentPackageName);
      }
      writeChildPackagesLPw(paramXmlSerializer, paramPackageSetting.childPackageNames);
      if (paramPackageSetting.sharedUser == null) {
        writePermissionsLPr(paramXmlSerializer, paramPackageSetting.getPermissionsState().getInstallPermissionStates());
      }
      paramXmlSerializer.endTag(null, "updated-package");
      return;
      paramXmlSerializer.attribute(null, "sharedUserId", Integer.toString(paramPackageSetting.appId));
    }
  }
  
  void writeDomainVerificationsLPr(XmlSerializer paramXmlSerializer, IntentFilterVerificationInfo paramIntentFilterVerificationInfo)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    if ((paramIntentFilterVerificationInfo != null) && (paramIntentFilterVerificationInfo.getPackageName() != null))
    {
      paramXmlSerializer.startTag(null, "domain-verification");
      paramIntentFilterVerificationInfo.writeToXml(paramXmlSerializer);
      if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
        Slog.d("PackageSettings", "Wrote domain verification for package: " + paramIntentFilterVerificationInfo.getPackageName());
      }
      paramXmlSerializer.endTag(null, "domain-verification");
    }
  }
  
  void writeKernelMappingLPr()
  {
    if (this.mKernelMappingFilename == null) {
      return;
    }
    Object localObject = this.mKernelMappingFilename.list();
    ArraySet localArraySet = new ArraySet(localObject.length);
    int i = 0;
    int j = localObject.length;
    while (i < j)
    {
      localArraySet.add(localObject[i]);
      i += 1;
    }
    localObject = this.mPackages.values().iterator();
    while (((Iterator)localObject).hasNext())
    {
      PackageSetting localPackageSetting = (PackageSetting)((Iterator)localObject).next();
      localArraySet.remove(localPackageSetting.name);
      writeKernelMappingLPr(localPackageSetting);
    }
    i = 0;
    while (i < localArraySet.size())
    {
      localObject = (String)localArraySet.valueAt(i);
      this.mKernelMapping.remove(localObject);
      new File(this.mKernelMappingFilename, (String)localObject).delete();
      i += 1;
    }
  }
  
  void writeKernelMappingLPr(PackageSetting paramPackageSetting)
  {
    if (this.mKernelMappingFilename == null) {
      return;
    }
    Object localObject = (Integer)this.mKernelMapping.get(paramPackageSetting.name);
    if ((localObject != null) && (((Integer)localObject).intValue() == paramPackageSetting.appId)) {
      return;
    }
    localObject = new File(this.mKernelMappingFilename, paramPackageSetting.name);
    ((File)localObject).mkdir();
    localObject = new File((File)localObject, "appid");
    try
    {
      FileUtils.stringToFile((File)localObject, Integer.toString(paramPackageSetting.appId));
      this.mKernelMapping.put(paramPackageSetting.name, Integer.valueOf(paramPackageSetting.appId));
      return;
    }
    catch (IOException paramPackageSetting) {}
  }
  
  void writeKeySetAliasesLPr(XmlSerializer paramXmlSerializer, PackageKeySetData paramPackageKeySetData)
    throws IOException
  {
    paramPackageKeySetData = paramPackageKeySetData.getAliases().entrySet().iterator();
    while (paramPackageKeySetData.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramPackageKeySetData.next();
      paramXmlSerializer.startTag(null, "defined-keyset");
      paramXmlSerializer.attribute(null, "alias", (String)localEntry.getKey());
      paramXmlSerializer.attribute(null, "identifier", Long.toString(((Long)localEntry.getValue()).longValue()));
      paramXmlSerializer.endTag(null, "defined-keyset");
    }
  }
  
  void writeLPr()
  {
    if (this.mSettingsFilename.exists()) {
      if (!this.mBackupSettingsFilename.exists())
      {
        if (!this.mSettingsFilename.renameTo(this.mBackupSettingsFilename)) {
          Slog.wtf("PackageManager", "Unable to backup package manager settings,  current changes will be lost at reboot");
        }
      }
      else
      {
        this.mSettingsFilename.delete();
        Slog.w("PackageManager", "Preserving older settings backup");
      }
    }
    this.mPastSignatures.clear();
    FileOutputStream localFileOutputStream;
    BufferedOutputStream localBufferedOutputStream;
    FastXmlSerializer localFastXmlSerializer;
    int i;
    try
    {
      localFileOutputStream = new FileOutputStream(this.mSettingsFilename);
      localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
      localFastXmlSerializer = new FastXmlSerializer();
      localFastXmlSerializer.setOutput(localBufferedOutputStream, StandardCharsets.UTF_8.name());
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localFastXmlSerializer.startTag(null, "packages");
      i = 0;
      while (i < this.mVersion.size())
      {
        localObject1 = (String)this.mVersion.keyAt(i);
        localObject4 = (VersionInfo)this.mVersion.valueAt(i);
        localFastXmlSerializer.startTag(null, "version");
        XmlUtils.writeStringAttribute(localFastXmlSerializer, "volumeUuid", (String)localObject1);
        XmlUtils.writeIntAttribute(localFastXmlSerializer, "sdkVersion", ((VersionInfo)localObject4).sdkVersion);
        XmlUtils.writeIntAttribute(localFastXmlSerializer, "databaseVersion", ((VersionInfo)localObject4).databaseVersion);
        XmlUtils.writeStringAttribute(localFastXmlSerializer, "fingerprint", ((VersionInfo)localObject4).fingerprint);
        localFastXmlSerializer.endTag(null, "version");
        i += 1;
      }
      if (this.mVerifierDeviceIdentity != null)
      {
        localFastXmlSerializer.startTag(null, "verifier");
        localFastXmlSerializer.attribute(null, "device", this.mVerifierDeviceIdentity.toString());
        localFastXmlSerializer.endTag(null, "verifier");
      }
      if (this.mReadExternalStorageEnforced == null) {
        break label369;
      }
      localFastXmlSerializer.startTag(null, "read-external-storage");
      if (!this.mReadExternalStorageEnforced.booleanValue()) {
        break label484;
      }
      localObject1 = "1";
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      for (;;)
      {
        Object localObject1;
        Slog.wtf("PackageManager", "Unable to write package manager settings, current changes will be lost at reboot", localXmlPullParserException);
        if ((this.mSettingsFilename.exists()) && (!this.mSettingsFilename.delete())) {
          Slog.wtf("PackageManager", "Failed to clean up mangled file: " + this.mSettingsFilename);
        }
        return;
        localObject2 = "0";
      }
      localFastXmlSerializer.endTag(null, "permission-trees");
      localFastXmlSerializer.startTag(null, "permissions");
      Object localObject2 = this.mPermissions.values().iterator();
      while (((Iterator)localObject2).hasNext()) {
        writePermissionLPr(localFastXmlSerializer, (BasePermission)((Iterator)localObject2).next());
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Slog.wtf("PackageManager", "Unable to write package manager settings, current changes will be lost at reboot", localIOException);
      }
      localFastXmlSerializer.endTag(null, "permissions");
      localObject3 = this.mPackages.values().iterator();
      while (((Iterator)localObject3).hasNext()) {
        writePackageLPr(localFastXmlSerializer, (PackageSetting)((Iterator)localObject3).next());
      }
      localObject3 = this.mDisabledSysPackages.values().iterator();
      while (((Iterator)localObject3).hasNext()) {
        writeDisabledSysPackageLPr(localFastXmlSerializer, (PackageSetting)((Iterator)localObject3).next());
      }
      localObject3 = this.mSharedUsers.values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (SharedUserSetting)((Iterator)localObject3).next();
        localFastXmlSerializer.startTag(null, "shared-user");
        localFastXmlSerializer.attribute(null, "name", ((SharedUserSetting)localObject4).name);
        localFastXmlSerializer.attribute(null, "userId", Integer.toString(((SharedUserSetting)localObject4).userId));
        ((SharedUserSetting)localObject4).signatures.writeXml(localFastXmlSerializer, "sigs", this.mPastSignatures);
        writePermissionsLPr(localFastXmlSerializer, ((SharedUserSetting)localObject4).getPermissionsState().getInstallPermissionStates());
        localFastXmlSerializer.endTag(null, "shared-user");
      }
      if (this.mPackagesToBeCleaned.size() <= 0) {
        break label916;
      }
    }
    localFastXmlSerializer.attribute(null, "enforcement", (String)localObject1);
    localFastXmlSerializer.endTag(null, "read-external-storage");
    label369:
    localFastXmlSerializer.startTag(null, "permission-trees");
    localObject1 = this.mPermissionTrees.values().iterator();
    while (((Iterator)localObject1).hasNext()) {
      writePermissionLPr(localFastXmlSerializer, (BasePermission)((Iterator)localObject1).next());
    }
    label484:
    Object localObject4 = this.mPackagesToBeCleaned.iterator();
    String str;
    if (((Iterator)localObject4).hasNext())
    {
      localObject3 = (PackageCleanItem)((Iterator)localObject4).next();
      str = Integer.toString(((PackageCleanItem)localObject3).userId);
      localFastXmlSerializer.startTag(null, "cleaning-package");
      localFastXmlSerializer.attribute(null, "name", ((PackageCleanItem)localObject3).packageName);
      if (!((PackageCleanItem)localObject3).andCode) {
        break label1215;
      }
    }
    label916:
    label1215:
    for (Object localObject3 = "true";; localObject3 = "false")
    {
      localFastXmlSerializer.attribute(null, "code", (String)localObject3);
      localFastXmlSerializer.attribute(null, "user", str);
      localFastXmlSerializer.endTag(null, "cleaning-package");
      break;
      if (this.mRenamedPackages.size() > 0)
      {
        localObject3 = this.mRenamedPackages.entrySet().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (Map.Entry)((Iterator)localObject3).next();
          localFastXmlSerializer.startTag(null, "renamed-package");
          localFastXmlSerializer.attribute(null, "new", (String)((Map.Entry)localObject4).getKey());
          localFastXmlSerializer.attribute(null, "old", (String)((Map.Entry)localObject4).getValue());
          localFastXmlSerializer.endTag(null, "renamed-package");
        }
      }
      int j = this.mRestoredIntentFilterVerifications.size();
      if (j > 0)
      {
        if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
          Slog.i("PackageSettings", "Writing restored-ivi entries to packages.xml");
        }
        localFastXmlSerializer.startTag(null, "restored-ivi");
        i = 0;
        while (i < j)
        {
          writeDomainVerificationsLPr(localFastXmlSerializer, (IntentFilterVerificationInfo)this.mRestoredIntentFilterVerifications.valueAt(i));
          i += 1;
        }
        localFastXmlSerializer.endTag(null, "restored-ivi");
      }
      for (;;)
      {
        this.mKeySetManagerService.writeKeySetManagerServiceLPr(localFastXmlSerializer);
        localFastXmlSerializer.endTag(null, "packages");
        localFastXmlSerializer.endDocument();
        localBufferedOutputStream.flush();
        FileUtils.sync(localFileOutputStream);
        localBufferedOutputStream.close();
        this.mBackupSettingsFilename.delete();
        FileUtils.setPermissions(this.mSettingsFilename.toString(), 432, -1, -1);
        writeKernelMappingLPr();
        writePackageListLPr();
        writeAllUsersPackageRestrictionsLPr();
        writeAllRuntimePermissionsLPr();
        return;
        if (PackageManagerService.DEBUG_DOMAIN_VERIFICATION) {
          Slog.i("PackageSettings", "  no restored IVI entries to write");
        }
      }
    }
  }
  
  void writePackageLPr(XmlSerializer paramXmlSerializer, PackageSetting paramPackageSetting)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "package");
    paramXmlSerializer.attribute(null, "name", paramPackageSetting.name);
    if (paramPackageSetting.realName != null) {
      paramXmlSerializer.attribute(null, "realName", paramPackageSetting.realName);
    }
    paramXmlSerializer.attribute(null, "codePath", paramPackageSetting.codePathString);
    if (!paramPackageSetting.resourcePathString.equals(paramPackageSetting.codePathString)) {
      paramXmlSerializer.attribute(null, "resourcePath", paramPackageSetting.resourcePathString);
    }
    if (paramPackageSetting.legacyNativeLibraryPathString != null) {
      paramXmlSerializer.attribute(null, "nativeLibraryPath", paramPackageSetting.legacyNativeLibraryPathString);
    }
    if (paramPackageSetting.primaryCpuAbiString != null) {
      paramXmlSerializer.attribute(null, "primaryCpuAbi", paramPackageSetting.primaryCpuAbiString);
    }
    if (paramPackageSetting.secondaryCpuAbiString != null) {
      paramXmlSerializer.attribute(null, "secondaryCpuAbi", paramPackageSetting.secondaryCpuAbiString);
    }
    if (paramPackageSetting.cpuAbiOverrideString != null) {
      paramXmlSerializer.attribute(null, "cpuAbiOverride", paramPackageSetting.cpuAbiOverrideString);
    }
    paramXmlSerializer.attribute(null, "publicFlags", Integer.toString(paramPackageSetting.pkgFlags));
    paramXmlSerializer.attribute(null, "privateFlags", Integer.toString(paramPackageSetting.pkgPrivateFlags));
    paramXmlSerializer.attribute(null, "ft", Long.toHexString(paramPackageSetting.timeStamp));
    paramXmlSerializer.attribute(null, "it", Long.toHexString(paramPackageSetting.firstInstallTime));
    paramXmlSerializer.attribute(null, "ut", Long.toHexString(paramPackageSetting.lastUpdateTime));
    paramXmlSerializer.attribute(null, "version", String.valueOf(paramPackageSetting.versionCode));
    if (paramPackageSetting.sharedUser == null) {
      paramXmlSerializer.attribute(null, "userId", Integer.toString(paramPackageSetting.appId));
    }
    for (;;)
    {
      if (paramPackageSetting.uidError) {
        paramXmlSerializer.attribute(null, "uidError", "true");
      }
      if (paramPackageSetting.installStatus == 0) {
        paramXmlSerializer.attribute(null, "installStatus", "false");
      }
      if (paramPackageSetting.installerPackageName != null) {
        paramXmlSerializer.attribute(null, "installer", paramPackageSetting.installerPackageName);
      }
      if (paramPackageSetting.isOrphaned) {
        paramXmlSerializer.attribute(null, "isOrphaned", "true");
      }
      if (paramPackageSetting.volumeUuid != null) {
        paramXmlSerializer.attribute(null, "volumeUuid", paramPackageSetting.volumeUuid);
      }
      if (paramPackageSetting.parentPackageName != null) {
        paramXmlSerializer.attribute(null, "parentPackageName", paramPackageSetting.parentPackageName);
      }
      writeChildPackagesLPw(paramXmlSerializer, paramPackageSetting.childPackageNames);
      paramPackageSetting.signatures.writeXml(paramXmlSerializer, "sigs", this.mPastSignatures);
      writePermissionsLPr(paramXmlSerializer, paramPackageSetting.getPermissionsState().getInstallPermissionStates());
      writeSigningKeySetLPr(paramXmlSerializer, paramPackageSetting.keySetData);
      writeUpgradeKeySetsLPr(paramXmlSerializer, paramPackageSetting.keySetData);
      writeKeySetAliasesLPr(paramXmlSerializer, paramPackageSetting.keySetData);
      writeDomainVerificationsLPr(paramXmlSerializer, paramPackageSetting.verificationInfo);
      paramXmlSerializer.endTag(null, "package");
      return;
      paramXmlSerializer.attribute(null, "sharedUserId", Integer.toString(paramPackageSetting.appId));
    }
  }
  
  void writePackageListLPr()
  {
    writePackageListLPr(-1);
  }
  
  void writePackageListLPr(int paramInt)
  {
    Object localObject1 = UserManagerService.getInstance().getUsers(true);
    localObject2 = new int[((List)localObject1).size()];
    int i = 0;
    while (i < localObject2.length)
    {
      localObject2[i] = ((UserInfo)((List)localObject1).get(i)).id;
      i += 1;
    }
    localObject1 = localObject2;
    if (paramInt != -1) {
      localObject1 = ArrayUtils.appendInt((int[])localObject2, paramInt);
    }
    localObject2 = new File(this.mPackageListFilename.getAbsolutePath() + ".tmp");
    JournaledFile localJournaledFile = new JournaledFile(this.mPackageListFilename, (File)localObject2);
    Object localObject3 = localJournaledFile.chooseForWrite();
    localObject2 = null;
    for (;;)
    {
      try
      {
        localFileOutputStream = new FileOutputStream((File)localObject3);
        localObject3 = new BufferedWriter(new OutputStreamWriter(localFileOutputStream, Charset.defaultCharset()));
        try
        {
          FileUtils.setPermissions(localFileOutputStream.getFD(), 416, 1000, 1032);
          localStringBuilder = new StringBuilder();
          Iterator localIterator = this.mPackages.values().iterator();
          if (!localIterator.hasNext()) {
            continue;
          }
          localObject2 = (PackageSetting)localIterator.next();
          if ((((PackageSetting)localObject2).pkg != null) && (((PackageSetting)localObject2).pkg.applicationInfo != null)) {
            continue;
          }
          if ("android".equals(((PackageSetting)localObject2).name)) {
            continue;
          }
          Slog.w("PackageSettings", "Skipping " + localObject2 + " due to missing metadata");
          continue;
          Slog.wtf("PackageSettings", "Failed to write packages.list", localException1);
        }
        catch (Exception localException1)
        {
          localObject2 = localObject3;
        }
      }
      catch (Exception localException2)
      {
        FileOutputStream localFileOutputStream;
        StringBuilder localStringBuilder;
        ApplicationInfo localApplicationInfo;
        String str;
        int[] arrayOfInt;
        continue;
        paramInt = 0;
        continue;
        localObject2 = " 0 ";
        continue;
      }
      IoUtils.closeQuietly((AutoCloseable)localObject2);
      localJournaledFile.rollback();
      return;
      if (((PackageSetting)localObject2).pkg.applicationInfo.dataDir != null)
      {
        localApplicationInfo = ((PackageSetting)localObject2).pkg.applicationInfo;
        str = localApplicationInfo.dataDir;
        if ((localApplicationInfo.flags & 0x2) == 0) {
          continue;
        }
        paramInt = 1;
        arrayOfInt = ((PackageSetting)localObject2).getPermissionsState().computeGids(localException1);
        if (str.indexOf(' ') < 0)
        {
          localStringBuilder.setLength(0);
          localStringBuilder.append(localApplicationInfo.packageName);
          localStringBuilder.append(" ");
          localStringBuilder.append(localApplicationInfo.uid);
          if (paramInt == 0) {
            continue;
          }
          localObject2 = " 1 ";
          localStringBuilder.append((String)localObject2);
          localStringBuilder.append(str);
          localStringBuilder.append(" ");
          localStringBuilder.append(localApplicationInfo.seinfo);
          localStringBuilder.append(" ");
          if ((arrayOfInt != null) && (arrayOfInt.length > 0))
          {
            localStringBuilder.append(arrayOfInt[0]);
            paramInt = 1;
            if (paramInt < arrayOfInt.length)
            {
              localStringBuilder.append(",");
              localStringBuilder.append(arrayOfInt[paramInt]);
              paramInt += 1;
              continue;
            }
          }
          else
          {
            localStringBuilder.append("none");
          }
          localStringBuilder.append("\n");
          ((BufferedWriter)localObject3).append(localStringBuilder);
        }
      }
    }
    ((BufferedWriter)localObject3).flush();
    FileUtils.sync(localFileOutputStream);
    ((BufferedWriter)localObject3).close();
    localJournaledFile.commit();
  }
  
  void writePackageRestrictionsLPr(int paramInt)
  {
    File localFile1 = getUserPackagesStateFile(paramInt);
    File localFile2 = getUserPackagesStateBackupFile(paramInt);
    new File(localFile1.getParent()).mkdirs();
    if (localFile1.exists()) {
      if (!localFile2.exists())
      {
        if (!localFile1.renameTo(localFile2)) {
          Slog.wtf("PackageManager", "Unable to backup user packages state file, current changes will be lost at reboot");
        }
      }
      else
      {
        localFile1.delete();
        Slog.w("PackageManager", "Preserving older stopped packages backup");
      }
    }
    FileOutputStream localFileOutputStream;
    BufferedOutputStream localBufferedOutputStream;
    FastXmlSerializer localFastXmlSerializer;
    for (;;)
    {
      Object localObject2;
      Object localObject1;
      try
      {
        localFileOutputStream = new FileOutputStream(localFile1);
        localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
        localFastXmlSerializer = new FastXmlSerializer();
        localFastXmlSerializer.setOutput(localBufferedOutputStream, StandardCharsets.UTF_8.name());
        localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
        localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        localFastXmlSerializer.startTag(null, "package-restrictions");
        Iterator localIterator = this.mPackages.values().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        localObject2 = (PackageSetting)localIterator.next();
        localObject1 = ((PackageSetting)localObject2).readUserState(paramInt);
        localFastXmlSerializer.startTag(null, "pkg");
        localFastXmlSerializer.attribute(null, "name", ((PackageSetting)localObject2).name);
        if (((PackageUserState)localObject1).ceDataInode != 0L) {
          XmlUtils.writeLongAttribute(localFastXmlSerializer, "ceDataInode", ((PackageUserState)localObject1).ceDataInode);
        }
        if (!((PackageUserState)localObject1).installed) {
          localFastXmlSerializer.attribute(null, "inst", "false");
        }
        if (((PackageUserState)localObject1).stopped) {
          localFastXmlSerializer.attribute(null, "stopped", "true");
        }
        if (((PackageUserState)localObject1).notLaunched) {
          localFastXmlSerializer.attribute(null, "nl", "true");
        }
        if (((PackageUserState)localObject1).hidden) {
          localFastXmlSerializer.attribute(null, "hidden", "true");
        }
        if (((PackageUserState)localObject1).suspended) {
          localFastXmlSerializer.attribute(null, "suspended", "true");
        }
        if (((PackageUserState)localObject1).blockUninstall) {
          localFastXmlSerializer.attribute(null, "blockUninstall", "true");
        }
        if (((PackageUserState)localObject1).enabled != 0)
        {
          localFastXmlSerializer.attribute(null, "enabled", Integer.toString(((PackageUserState)localObject1).enabled));
          if (((PackageUserState)localObject1).lastDisableAppCaller != null) {
            localFastXmlSerializer.attribute(null, "enabledCaller", ((PackageUserState)localObject1).lastDisableAppCaller);
          }
        }
        if (((PackageUserState)localObject1).domainVerificationStatus != 0) {
          XmlUtils.writeIntAttribute(localFastXmlSerializer, "domainVerificationStatus", ((PackageUserState)localObject1).domainVerificationStatus);
        }
        if (((PackageUserState)localObject1).appLinkGeneration != 0) {
          XmlUtils.writeIntAttribute(localFastXmlSerializer, "app-link-generation", ((PackageUserState)localObject1).appLinkGeneration);
        }
        if (!ArrayUtils.isEmpty(((PackageUserState)localObject1).enabledComponents))
        {
          localFastXmlSerializer.startTag(null, "enabled-components");
          localObject2 = ((PackageUserState)localObject1).enabledComponents.iterator();
          if (((Iterator)localObject2).hasNext())
          {
            String str = (String)((Iterator)localObject2).next();
            localFastXmlSerializer.startTag(null, "item");
            localFastXmlSerializer.attribute(null, "name", str);
            localFastXmlSerializer.endTag(null, "item");
            continue;
          }
          localFastXmlSerializer.endTag(null, "enabled-components");
        }
      }
      catch (IOException localIOException)
      {
        Slog.wtf("PackageManager", "Unable to write package manager user packages state,  current changes will be lost at reboot", localIOException);
        if ((localFile1.exists()) && (!localFile1.delete())) {
          Log.i("PackageManager", "Failed to clean up mangled file: " + this.mStoppedPackagesFilename);
        }
        return;
      }
      if (!ArrayUtils.isEmpty(((PackageUserState)localObject1).disabledComponents))
      {
        localFastXmlSerializer.startTag(null, "disabled-components");
        localObject1 = ((PackageUserState)localObject1).disabledComponents.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (String)((Iterator)localObject1).next();
          localFastXmlSerializer.startTag(null, "item");
          localFastXmlSerializer.attribute(null, "name", (String)localObject2);
          localFastXmlSerializer.endTag(null, "item");
        }
        localFastXmlSerializer.endTag(null, "disabled-components");
      }
      localFastXmlSerializer.endTag(null, "pkg");
    }
    writePreferredActivitiesLPr(localFastXmlSerializer, paramInt, true);
    writePersistentPreferredActivitiesLPr(localFastXmlSerializer, paramInt);
    writeCrossProfileIntentFiltersLPr(localFastXmlSerializer, paramInt);
    writeDefaultAppsLPr(localFastXmlSerializer, paramInt);
    localFastXmlSerializer.endTag(null, "package-restrictions");
    localFastXmlSerializer.endDocument();
    localBufferedOutputStream.flush();
    FileUtils.sync(localFileOutputStream);
    localBufferedOutputStream.close();
    localIOException.delete();
    FileUtils.setPermissions(localFile1.toString(), 432, -1, -1);
  }
  
  void writePermissionLPr(XmlSerializer paramXmlSerializer, BasePermission paramBasePermission)
    throws XmlPullParserException, IOException
  {
    if (paramBasePermission.sourcePackage != null)
    {
      paramXmlSerializer.startTag(null, "item");
      paramXmlSerializer.attribute(null, "name", paramBasePermission.name);
      paramXmlSerializer.attribute(null, "package", paramBasePermission.sourcePackage);
      if (paramBasePermission.protectionLevel != 0) {
        paramXmlSerializer.attribute(null, "protection", Integer.toString(paramBasePermission.protectionLevel));
      }
      if (PackageManagerService.DEBUG_SETTINGS) {
        Log.v("PackageManager", "Writing perm: name=" + paramBasePermission.name + " type=" + paramBasePermission.type);
      }
      if (paramBasePermission.type == 2) {
        if (paramBasePermission.perm == null) {
          break label224;
        }
      }
    }
    label224:
    for (paramBasePermission = paramBasePermission.perm.info;; paramBasePermission = paramBasePermission.pendingInfo)
    {
      if (paramBasePermission != null)
      {
        paramXmlSerializer.attribute(null, "type", "dynamic");
        if (paramBasePermission.icon != 0) {
          paramXmlSerializer.attribute(null, "icon", Integer.toString(paramBasePermission.icon));
        }
        if (paramBasePermission.nonLocalizedLabel != null) {
          paramXmlSerializer.attribute(null, "label", paramBasePermission.nonLocalizedLabel.toString());
        }
      }
      paramXmlSerializer.endTag(null, "item");
      return;
    }
  }
  
  void writePermissionsLPr(XmlSerializer paramXmlSerializer, List<PermissionsState.PermissionState> paramList)
    throws IOException
  {
    if (paramList.isEmpty()) {
      return;
    }
    paramXmlSerializer.startTag(null, "perms");
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)paramList.next();
      paramXmlSerializer.startTag(null, "item");
      paramXmlSerializer.attribute(null, "name", localPermissionState.getName());
      paramXmlSerializer.attribute(null, "granted", String.valueOf(localPermissionState.isGranted()));
      paramXmlSerializer.attribute(null, "flags", Integer.toHexString(localPermissionState.getFlags()));
      paramXmlSerializer.endTag(null, "item");
    }
    paramXmlSerializer.endTag(null, "perms");
  }
  
  void writePersistentPreferredActivitiesLPr(XmlSerializer paramXmlSerializer, int paramInt)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    paramXmlSerializer.startTag(null, "persistent-preferred-activities");
    Object localObject = (PersistentPreferredIntentResolver)this.mPersistentPreferredActivities.get(paramInt);
    if (localObject != null)
    {
      localObject = ((PersistentPreferredIntentResolver)localObject).filterSet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        PersistentPreferredActivity localPersistentPreferredActivity = (PersistentPreferredActivity)((Iterator)localObject).next();
        paramXmlSerializer.startTag(null, "item");
        localPersistentPreferredActivity.writeToXml(paramXmlSerializer);
        paramXmlSerializer.endTag(null, "item");
      }
    }
    paramXmlSerializer.endTag(null, "persistent-preferred-activities");
  }
  
  void writePreferredActivitiesLPr(XmlSerializer paramXmlSerializer, int paramInt, boolean paramBoolean)
    throws IllegalArgumentException, IllegalStateException, IOException
  {
    paramXmlSerializer.startTag(null, "preferred-activities");
    Object localObject = (PreferredIntentResolver)this.mPreferredActivities.get(paramInt);
    if (localObject != null)
    {
      localObject = ((PreferredIntentResolver)localObject).filterSet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        PreferredActivity localPreferredActivity = (PreferredActivity)((Iterator)localObject).next();
        paramXmlSerializer.startTag(null, "item");
        localPreferredActivity.writeToXml(paramXmlSerializer, paramBoolean);
        paramXmlSerializer.endTag(null, "item");
      }
    }
    paramXmlSerializer.endTag(null, "preferred-activities");
  }
  
  public void writeRuntimePermissionsForUserLPr(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mRuntimePermissionsPersistence.writePermissionsForUserSyncLPr(paramInt);
      return;
    }
    this.mRuntimePermissionsPersistence.writePermissionsForUserAsyncLPr(paramInt);
  }
  
  void writeSigningKeySetLPr(XmlSerializer paramXmlSerializer, PackageKeySetData paramPackageKeySetData)
    throws IOException
  {
    paramXmlSerializer.startTag(null, "proper-signing-keyset");
    paramXmlSerializer.attribute(null, "identifier", Long.toString(paramPackageKeySetData.getProperSigningKeySet()));
    paramXmlSerializer.endTag(null, "proper-signing-keyset");
  }
  
  void writeUpgradeKeySetsLPr(XmlSerializer paramXmlSerializer, PackageKeySetData paramPackageKeySetData)
    throws IOException
  {
    paramPackageKeySetData.getProperSigningKeySet();
    if (paramPackageKeySetData.isUsingUpgradeKeySets())
    {
      paramPackageKeySetData = paramPackageKeySetData.getUpgradeKeySets();
      int i = 0;
      int j = paramPackageKeySetData.length;
      while (i < j)
      {
        long l = paramPackageKeySetData[i];
        paramXmlSerializer.startTag(null, "upgrade-keyset");
        paramXmlSerializer.attribute(null, "identifier", Long.toString(l));
        paramXmlSerializer.endTag(null, "upgrade-keyset");
        i += 1;
      }
    }
  }
  
  public static class DatabaseVersion
  {
    public static final int FIRST_VERSION = 1;
    public static final int SIGNATURE_END_ENTITY = 2;
    public static final int SIGNATURE_MALFORMED_RECOVER = 3;
  }
  
  final class RestoredPermissionGrant
  {
    int grantBits;
    boolean granted;
    String permissionName;
    
    RestoredPermissionGrant(String paramString, boolean paramBoolean, int paramInt)
    {
      this.permissionName = paramString;
      this.granted = paramBoolean;
      this.grantBits = paramInt;
    }
  }
  
  private final class RuntimePermissionPersistence
  {
    private static final long MAX_WRITE_PERMISSIONS_DELAY_MILLIS = 2000L;
    private static final long WRITE_PERMISSIONS_DELAY_MILLIS = 200L;
    @GuardedBy("mLock")
    private final SparseBooleanArray mDefaultPermissionsGranted = new SparseBooleanArray();
    @GuardedBy("mLock")
    private final SparseArray<String> mFingerprints = new SparseArray();
    private final Handler mHandler = new MyHandler();
    @GuardedBy("mLock")
    private final SparseLongArray mLastNotWrittenMutationTimesMillis = new SparseLongArray();
    private final Object mLock;
    @GuardedBy("mLock")
    private final SparseBooleanArray mWriteScheduled = new SparseBooleanArray();
    
    public RuntimePermissionPersistence(Object paramObject)
    {
      this.mLock = paramObject;
    }
    
    private void onUserRemovedLPw(int paramInt)
    {
      this.mHandler.removeMessages(paramInt);
      Iterator localIterator = Settings.this.mPackages.values().iterator();
      while (localIterator.hasNext()) {
        revokeRuntimePermissionsAndClearFlags((PackageSetting)localIterator.next(), paramInt);
      }
      localIterator = Settings.this.mSharedUsers.values().iterator();
      while (localIterator.hasNext()) {
        revokeRuntimePermissionsAndClearFlags((SharedUserSetting)localIterator.next(), paramInt);
      }
      this.mDefaultPermissionsGranted.delete(paramInt);
      this.mFingerprints.remove(paramInt);
    }
    
    private void parsePermissionsLPr(XmlPullParser paramXmlPullParser, PermissionsState paramPermissionsState, int paramInt)
      throws IOException, XmlPullParserException
    {
      int j = paramXmlPullParser.getDepth();
      for (;;)
      {
        int i = paramXmlPullParser.next();
        if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() <= j))) {
          break;
        }
        if ((i != 3) && (i != 4) && (paramXmlPullParser.getName().equals("item")))
        {
          String str = paramXmlPullParser.getAttributeValue(null, "name");
          BasePermission localBasePermission = (BasePermission)Settings.this.mPermissions.get(str);
          if (localBasePermission == null)
          {
            Slog.w("PackageManager", "Unknown permission:" + str);
            XmlUtils.skipCurrentTag(paramXmlPullParser);
          }
          else
          {
            str = paramXmlPullParser.getAttributeValue(null, "granted");
            boolean bool;
            if (str != null)
            {
              bool = Boolean.parseBoolean(str);
              label154:
              str = paramXmlPullParser.getAttributeValue(null, "flags");
              if (str == null) {
                break label214;
              }
            }
            label214:
            for (i = Integer.parseInt(str, 16);; i = 0)
            {
              if (!bool) {
                break label220;
              }
              paramPermissionsState.grantRuntimePermission(localBasePermission, paramInt);
              paramPermissionsState.updatePermissionFlags(localBasePermission, paramInt, 255, i);
              break;
              bool = true;
              break label154;
            }
            label220:
            paramPermissionsState.updatePermissionFlags(localBasePermission, paramInt, 255, i);
          }
        }
      }
    }
    
    private void parseRestoredRuntimePermissionsLPr(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
      throws IOException, XmlPullParserException
    {
      int k = paramXmlPullParser.getDepth();
      for (;;)
      {
        int i = paramXmlPullParser.next();
        if ((i == 1) || ((i == 3) && (paramXmlPullParser.getDepth() <= k))) {
          break;
        }
        if ((i != 3) && (i != 4) && (paramXmlPullParser.getName().equals("perm")))
        {
          String str = paramXmlPullParser.getAttributeValue(null, "name");
          boolean bool = "true".equals(paramXmlPullParser.getAttributeValue(null, "granted"));
          int j = 0;
          if ("true".equals(paramXmlPullParser.getAttributeValue(null, "set"))) {
            j = 1;
          }
          i = j;
          if ("true".equals(paramXmlPullParser.getAttributeValue(null, "fixed"))) {
            i = j | 0x2;
          }
          j = i;
          if ("true".equals(paramXmlPullParser.getAttributeValue(null, "rou"))) {
            j = i | 0x8;
          }
          if ((bool) || (j != 0)) {
            rememberRestoredUserGrantLPr(paramString, str, bool, j, paramInt);
          }
        }
      }
    }
    
    private void parseRuntimePermissionsLPr(XmlPullParser paramXmlPullParser, int paramInt)
      throws IOException, XmlPullParserException
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
          if (str.equals("runtime-permissions"))
          {
            str = paramXmlPullParser.getAttributeValue(null, "fingerprint");
            this.mFingerprints.put(paramInt, str);
            boolean bool = Build.FINGERPRINT.equals(str);
            this.mDefaultPermissionsGranted.put(paramInt, bool);
          }
          else
          {
            Object localObject;
            if (str.equals("pkg"))
            {
              str = paramXmlPullParser.getAttributeValue(null, "name");
              localObject = (PackageSetting)Settings.this.mPackages.get(str);
              if (localObject == null)
              {
                Slog.w("PackageManager", "Unknown package:" + str);
                XmlUtils.skipCurrentTag(paramXmlPullParser);
              }
            }
            else
            {
              if (str.equals("shared-user"))
              {
                str = paramXmlPullParser.getAttributeValue(null, "name");
                localObject = (SharedUserSetting)Settings.this.mSharedUsers.get(str);
                if (localObject != null) {
                  break label310;
                }
                Slog.w("PackageManager", "Unknown shared user:" + str);
                XmlUtils.skipCurrentTag(paramXmlPullParser);
                continue;
              }
              if (!str.equals("restored-perms")) {
                continue;
              }
              parseRestoredRuntimePermissionsLPr(paramXmlPullParser, paramXmlPullParser.getAttributeValue(null, "packageName"), paramInt);
              continue;
            }
            parsePermissionsLPr(paramXmlPullParser, ((PackageSetting)localObject).getPermissionsState(), paramInt);
            continue;
            label310:
            parsePermissionsLPr(paramXmlPullParser, ((SharedUserSetting)localObject).getPermissionsState(), paramInt);
          }
        }
      }
    }
    
    private void revokeRuntimePermissionsAndClearFlags(SettingBase paramSettingBase, int paramInt)
    {
      paramSettingBase = paramSettingBase.getPermissionsState();
      Iterator localIterator = paramSettingBase.getRuntimePermissionStates(paramInt).iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (PermissionsState.PermissionState)localIterator.next();
        localObject = (BasePermission)Settings.this.mPermissions.get(((PermissionsState.PermissionState)localObject).getName());
        if (localObject != null)
        {
          paramSettingBase.revokeRuntimePermission((BasePermission)localObject, paramInt);
          paramSettingBase.updatePermissionFlags((BasePermission)localObject, paramInt, 255, 0);
        }
      }
    }
    
    private void writePermissions(XmlSerializer paramXmlSerializer, List<PermissionsState.PermissionState> paramList)
      throws IOException
    {
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        PermissionsState.PermissionState localPermissionState = (PermissionsState.PermissionState)paramList.next();
        paramXmlSerializer.startTag(null, "item");
        paramXmlSerializer.attribute(null, "name", localPermissionState.getName());
        paramXmlSerializer.attribute(null, "granted", String.valueOf(localPermissionState.isGranted()));
        paramXmlSerializer.attribute(null, "flags", Integer.toHexString(localPermissionState.getFlags()));
        paramXmlSerializer.endTag(null, "item");
      }
    }
    
    private void writePermissionsSync(int paramInt)
    {
      AtomicFile localAtomicFile = new AtomicFile(Settings.-wrap0(Settings.this, paramInt));
      Object localObject7 = new ArrayMap();
      ArrayMap localArrayMap = new ArrayMap();
      int j;
      int i;
      Object localObject3;
      Object localObject6;
      synchronized (this.mLock)
      {
        this.mWriteScheduled.delete(paramInt);
        j = Settings.this.mPackages.size();
        i = 0;
        if (i < j)
        {
          localObject3 = (String)Settings.this.mPackages.keyAt(i);
          localObject6 = (PackageSetting)Settings.this.mPackages.valueAt(i);
          if (((PackageSetting)localObject6).sharedUser == null)
          {
            localObject6 = ((PackageSetting)localObject6).getPermissionsState().getRuntimePermissionStates(paramInt);
            if (!((List)localObject6).isEmpty()) {
              ((ArrayMap)localObject7).put(localObject3, localObject6);
            }
          }
        }
        else
        {
          j = Settings.this.mSharedUsers.size();
          i = 0;
          while (i < j)
          {
            localObject3 = (String)Settings.this.mSharedUsers.keyAt(i);
            localObject6 = ((SharedUserSetting)Settings.this.mSharedUsers.valueAt(i)).getPermissionsState().getRuntimePermissionStates(paramInt);
            if (!((List)localObject6).isEmpty()) {
              localArrayMap.put(localObject3, localObject6);
            }
            i += 1;
          }
          localObject3 = null;
          ??? = null;
        }
      }
      for (;;)
      {
        try
        {
          localObject6 = localAtomicFile.startWrite();
          ??? = localObject6;
          localObject3 = localObject6;
          XmlSerializer localXmlSerializer = Xml.newSerializer();
          ??? = localObject6;
          localObject3 = localObject6;
          localXmlSerializer.setOutput((OutputStream)localObject6, StandardCharsets.UTF_8.name());
          ??? = localObject6;
          localObject3 = localObject6;
          localXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
          ??? = localObject6;
          localObject3 = localObject6;
          localXmlSerializer.startDocument(null, Boolean.valueOf(true));
          ??? = localObject6;
          localObject3 = localObject6;
          localXmlSerializer.startTag(null, "runtime-permissions");
          ??? = localObject6;
          localObject3 = localObject6;
          String str = (String)this.mFingerprints.get(paramInt);
          if (str != null)
          {
            ??? = localObject6;
            localObject3 = localObject6;
            localXmlSerializer.attribute(null, "fingerprint", str);
          }
          ??? = localObject6;
          localObject3 = localObject6;
          j = ((ArrayMap)localObject7).size();
          i = 0;
          Object localObject8;
          if (i < j)
          {
            ??? = localObject6;
            localObject3 = localObject6;
            localObject8 = (String)((ArrayMap)localObject7).keyAt(i);
            ??? = localObject6;
            localObject3 = localObject6;
            List localList = (List)((ArrayMap)localObject7).valueAt(i);
            ??? = localObject6;
            localObject3 = localObject6;
            localXmlSerializer.startTag(null, "pkg");
            ??? = localObject6;
            localObject3 = localObject6;
            localXmlSerializer.attribute(null, "name", (String)localObject8);
            ??? = localObject6;
            localObject3 = localObject6;
            writePermissions(localXmlSerializer, localList);
            ??? = localObject6;
            localObject3 = localObject6;
            localXmlSerializer.endTag(null, "pkg");
            i += 1;
            continue;
            localObject4 = finally;
            throw ((Throwable)localObject4);
          }
          ??? = localObject6;
          localObject5 = localObject6;
          j = localArrayMap.size();
          i = 0;
          if (i < j)
          {
            ??? = localObject6;
            localObject5 = localObject6;
            localObject7 = (String)localArrayMap.keyAt(i);
            ??? = localObject6;
            localObject5 = localObject6;
            localObject8 = (List)localArrayMap.valueAt(i);
            ??? = localObject6;
            localObject5 = localObject6;
            localXmlSerializer.startTag(null, "shared-user");
            ??? = localObject6;
            localObject5 = localObject6;
            localXmlSerializer.attribute(null, "name", (String)localObject7);
            ??? = localObject6;
            localObject5 = localObject6;
            writePermissions(localXmlSerializer, (List)localObject8);
            ??? = localObject6;
            localObject5 = localObject6;
            localXmlSerializer.endTag(null, "shared-user");
            i += 1;
            continue;
          }
          ??? = localObject6;
          localObject5 = localObject6;
          localXmlSerializer.endTag(null, "runtime-permissions");
          ??? = localObject6;
          localObject5 = localObject6;
          if (Settings.-get0(Settings.this).get(paramInt) != null)
          {
            ??? = localObject6;
            localObject5 = localObject6;
            localArrayMap = (ArrayMap)Settings.-get0(Settings.this).get(paramInt);
            if (localArrayMap != null)
            {
              ??? = localObject6;
              localObject5 = localObject6;
              int k = localArrayMap.size();
              i = 0;
              if (i < k)
              {
                ??? = localObject6;
                localObject5 = localObject6;
                localObject7 = (ArraySet)localArrayMap.valueAt(i);
                if (localObject7 == null) {
                  break label1283;
                }
                ??? = localObject6;
                localObject5 = localObject6;
                if (((ArraySet)localObject7).size() <= 0) {
                  break label1283;
                }
                ??? = localObject6;
                localObject5 = localObject6;
                localObject8 = (String)localArrayMap.keyAt(i);
                ??? = localObject6;
                localObject5 = localObject6;
                localXmlSerializer.startTag(null, "restored-perms");
                ??? = localObject6;
                localObject5 = localObject6;
                localXmlSerializer.attribute(null, "packageName", (String)localObject8);
                ??? = localObject6;
                localObject5 = localObject6;
                int m = ((ArraySet)localObject7).size();
                j = 0;
                if (j < m)
                {
                  ??? = localObject6;
                  localObject5 = localObject6;
                  localObject8 = (Settings.RestoredPermissionGrant)((ArraySet)localObject7).valueAt(j);
                  ??? = localObject6;
                  localObject5 = localObject6;
                  localXmlSerializer.startTag(null, "perm");
                  ??? = localObject6;
                  localObject5 = localObject6;
                  localXmlSerializer.attribute(null, "name", ((Settings.RestoredPermissionGrant)localObject8).permissionName);
                  ??? = localObject6;
                  localObject5 = localObject6;
                  if (((Settings.RestoredPermissionGrant)localObject8).granted)
                  {
                    ??? = localObject6;
                    localObject5 = localObject6;
                    localXmlSerializer.attribute(null, "granted", "true");
                  }
                  ??? = localObject6;
                  localObject5 = localObject6;
                  if ((((Settings.RestoredPermissionGrant)localObject8).grantBits & 0x1) != 0)
                  {
                    ??? = localObject6;
                    localObject5 = localObject6;
                    localXmlSerializer.attribute(null, "set", "true");
                  }
                  ??? = localObject6;
                  localObject5 = localObject6;
                  if ((((Settings.RestoredPermissionGrant)localObject8).grantBits & 0x2) != 0)
                  {
                    ??? = localObject6;
                    localObject5 = localObject6;
                    localXmlSerializer.attribute(null, "fixed", "true");
                  }
                  ??? = localObject6;
                  localObject5 = localObject6;
                  if ((((Settings.RestoredPermissionGrant)localObject8).grantBits & 0x8) != 0)
                  {
                    ??? = localObject6;
                    localObject5 = localObject6;
                    localXmlSerializer.attribute(null, "rou", "true");
                  }
                  ??? = localObject6;
                  localObject5 = localObject6;
                  localXmlSerializer.endTag(null, "perm");
                  j += 1;
                  continue;
                }
                ??? = localObject6;
                localObject5 = localObject6;
                localXmlSerializer.endTag(null, "restored-perms");
                break label1283;
              }
            }
          }
          ??? = localObject6;
          localObject5 = localObject6;
          localXmlSerializer.endDocument();
          ??? = localObject6;
          localObject5 = localObject6;
          localAtomicFile.finishWrite((FileOutputStream)localObject6);
          ??? = localObject6;
          localObject5 = localObject6;
          if (Build.FINGERPRINT.equals(str))
          {
            ??? = localObject6;
            localObject5 = localObject6;
            this.mDefaultPermissionsGranted.put(paramInt, true);
          }
          return;
        }
        catch (Throwable localThrowable)
        {
          localObject5 = ???;
          Slog.wtf("PackageManager", "Failed to write settings, restoring backup", localThrowable);
          localObject5 = ???;
          localAtomicFile.failWrite((FileOutputStream)???);
          return;
        }
        finally
        {
          Object localObject5;
          IoUtils.closeQuietly((AutoCloseable)localObject5);
        }
        i += 1;
        break;
        label1283:
        i += 1;
      }
    }
    
    public boolean areDefaultRuntimPermissionsGrantedLPr(int paramInt)
    {
      return this.mDefaultPermissionsGranted.get(paramInt);
    }
    
    public void deleteUserRuntimePermissionsFile(int paramInt)
    {
      Settings.-wrap0(Settings.this, paramInt).delete();
    }
    
    public void onDefaultRuntimePermissionsGrantedLPr(int paramInt)
    {
      this.mFingerprints.put(paramInt, Build.FINGERPRINT);
      writePermissionsForUserAsyncLPr(paramInt);
    }
    
    /* Error */
    public void readStateForUserSyncLPr(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 48	com/android/server/pm/Settings$RuntimePermissionPersistence:this$0	Lcom/android/server/pm/Settings;
      //   4: iload_1
      //   5: invokestatic 324	com/android/server/pm/Settings:-wrap0	(Lcom/android/server/pm/Settings;I)Ljava/io/File;
      //   8: astore_3
      //   9: aload_3
      //   10: invokevirtual 452	java/io/File:exists	()Z
      //   13: ifne +4 -> 17
      //   16: return
      //   17: new 321	android/util/AtomicFile
      //   20: dup
      //   21: aload_3
      //   22: invokespecial 327	android/util/AtomicFile:<init>	(Ljava/io/File;)V
      //   25: invokevirtual 456	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
      //   28: astore_2
      //   29: invokestatic 460	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
      //   32: astore 4
      //   34: aload 4
      //   36: aload_2
      //   37: aconst_null
      //   38: invokeinterface 464 3 0
      //   43: aload_0
      //   44: aload 4
      //   46: iload_1
      //   47: invokespecial 466	com/android/server/pm/Settings$RuntimePermissionPersistence:parseRuntimePermissionsLPr	(Lorg/xmlpull/v1/XmlPullParser;I)V
      //   50: aload_2
      //   51: invokestatic 424	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   54: return
      //   55: astore_2
      //   56: ldc -90
      //   58: ldc_w 468
      //   61: invokestatic 471	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   64: pop
      //   65: return
      //   66: astore 4
      //   68: new 473	java/lang/IllegalStateException
      //   71: dup
      //   72: new 168	java/lang/StringBuilder
      //   75: dup
      //   76: invokespecial 169	java/lang/StringBuilder:<init>	()V
      //   79: ldc_w 475
      //   82: invokevirtual 175	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   85: aload_3
      //   86: invokevirtual 478	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   89: invokevirtual 178	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   92: aload 4
      //   94: invokespecial 481	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   97: athrow
      //   98: astore_3
      //   99: aload_2
      //   100: invokestatic 424	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   103: aload_3
      //   104: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	105	0	this	RuntimePermissionPersistence
      //   0	105	1	paramInt	int
      //   28	23	2	localFileInputStream	java.io.FileInputStream
      //   55	45	2	localFileNotFoundException	java.io.FileNotFoundException
      //   8	78	3	localFile	File
      //   98	6	3	localObject	Object
      //   32	13	4	localXmlPullParser	XmlPullParser
      //   66	27	4	localXmlPullParserException	XmlPullParserException
      // Exception table:
      //   from	to	target	type
      //   17	29	55	java/io/FileNotFoundException
      //   29	50	66	org/xmlpull/v1/XmlPullParserException
      //   29	50	66	java/io/IOException
      //   29	50	98	finally
      //   68	98	98	finally
    }
    
    public void rememberRestoredUserGrantLPr(String paramString1, String paramString2, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      Object localObject2 = (ArrayMap)Settings.-get0(Settings.this).get(paramInt2);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new ArrayMap();
        Settings.-get0(Settings.this).put(paramInt2, localObject1);
      }
      ArraySet localArraySet = (ArraySet)((ArrayMap)localObject1).get(paramString1);
      localObject2 = localArraySet;
      if (localArraySet == null)
      {
        localObject2 = new ArraySet();
        ((ArrayMap)localObject1).put(paramString1, localObject2);
      }
      ((ArraySet)localObject2).add(new Settings.RestoredPermissionGrant(Settings.this, paramString2, paramBoolean, paramInt1));
    }
    
    public void writePermissionsForUserAsyncLPr(int paramInt)
    {
      long l1 = SystemClock.uptimeMillis();
      if (this.mWriteScheduled.get(paramInt))
      {
        this.mHandler.removeMessages(paramInt);
        long l2 = this.mLastNotWrittenMutationTimesMillis.get(paramInt);
        if (l1 - l2 >= 2000L)
        {
          this.mHandler.obtainMessage(paramInt).sendToTarget();
          return;
        }
        l1 = Math.min(200L, Math.max(2000L + l2 - l1, 0L));
        localMessage = this.mHandler.obtainMessage(paramInt);
        this.mHandler.sendMessageDelayed(localMessage, l1);
        return;
      }
      this.mLastNotWrittenMutationTimesMillis.put(paramInt, l1);
      Message localMessage = this.mHandler.obtainMessage(paramInt);
      this.mHandler.sendMessageDelayed(localMessage, 200L);
      this.mWriteScheduled.put(paramInt, true);
    }
    
    public void writePermissionsForUserSyncLPr(int paramInt)
    {
      this.mHandler.removeMessages(paramInt);
      writePermissionsSync(paramInt);
    }
    
    private final class MyHandler
      extends Handler
    {
      public MyHandler()
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        int i = paramMessage.what;
        paramMessage = (Runnable)paramMessage.obj;
        Settings.RuntimePermissionPersistence.-wrap1(Settings.RuntimePermissionPersistence.this, i);
        if (paramMessage != null) {
          paramMessage.run();
        }
      }
    }
  }
  
  public static class VersionInfo
  {
    int databaseVersion;
    String fingerprint;
    int sdkVersion;
    
    public void forceCurrent()
    {
      this.sdkVersion = Build.VERSION.SDK_INT;
      this.databaseVersion = 3;
      this.fingerprint = Build.FINGERPRINT;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/Settings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */