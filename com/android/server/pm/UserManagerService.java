package com.android.server.pm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.IStopUserCallback.Stub;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IUserManager.Stub;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.UserManagerInternal;
import android.os.UserManagerInternal.UserRestrictionsListener;
import android.os.storage.StorageManager;
import android.provider.Settings.Secure;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.IntArray;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;
import libcore.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class UserManagerService
  extends IUserManager.Stub
{
  private static final int ALLOWED_FLAGS_FOR_CREATE_USERS_PERMISSION = 812;
  private static final String ATTR_CREATION_TIME = "created";
  private static final String ATTR_FLAGS = "flags";
  private static final String ATTR_GUEST_TO_REMOVE = "guestToRemove";
  private static final String ATTR_ICON_PATH = "icon";
  private static final String ATTR_ID = "id";
  private static final String ATTR_KEY = "key";
  private static final String ATTR_LAST_LOGGED_IN_FINGERPRINT = "lastLoggedInFingerprint";
  private static final String ATTR_LAST_LOGGED_IN_TIME = "lastLoggedIn";
  private static final String ATTR_MULTIPLE = "m";
  private static final String ATTR_NEXT_SERIAL_NO = "nextSerialNumber";
  private static final String ATTR_PARTIAL = "partial";
  private static final String ATTR_PROFILE_GROUP_ID = "profileGroupId";
  private static final String ATTR_RESTRICTED_PROFILE_PARENT_ID = "restrictedProfileParentId";
  private static final String ATTR_SEED_ACCOUNT_NAME = "seedAccountName";
  private static final String ATTR_SEED_ACCOUNT_TYPE = "seedAccountType";
  private static final String ATTR_SERIAL_NO = "serialNumber";
  private static final String ATTR_TYPE_BOOLEAN = "b";
  private static final String ATTR_TYPE_BUNDLE = "B";
  private static final String ATTR_TYPE_BUNDLE_ARRAY = "BA";
  private static final String ATTR_TYPE_INTEGER = "i";
  private static final String ATTR_TYPE_STRING = "s";
  private static final String ATTR_TYPE_STRING_ARRAY = "sa";
  private static final String ATTR_USER_VERSION = "version";
  private static final String ATTR_VALUE_TYPE = "type";
  static final boolean DBG = false;
  private static final boolean DBG_WITH_STACKTRACE = false;
  private static final long EPOCH_PLUS_30_YEARS = 946080000000L;
  private static final String LOG_TAG = "UserManagerService";
  private static final int MAX_MANAGED_PROFILES = 1;
  private static final int MAX_USER_ID = 21474;
  private static final int MIN_USER_ID = 10;
  private static final String RESTRICTIONS_FILE_PREFIX = "res_";
  private static final String TAG_ACCOUNT = "account";
  private static final String TAG_DEVICE_POLICY_RESTRICTIONS = "device_policy_restrictions";
  private static final String TAG_ENTRY = "entry";
  private static final String TAG_GLOBAL_RESTRICTION_OWNER_ID = "globalRestrictionOwnerUserId";
  private static final String TAG_GUEST_RESTRICTIONS = "guestRestrictions";
  private static final String TAG_NAME = "name";
  private static final String TAG_RESTRICTIONS = "restrictions";
  private static final String TAG_SEED_ACCOUNT_OPTIONS = "seedAccountOptions";
  private static final String TAG_USER = "user";
  private static final String TAG_USERS = "users";
  private static final String TAG_VALUE = "value";
  private static final String TRON_GUEST_CREATED = "users_guest_created";
  private static final String TRON_USER_CREATED = "users_user_created";
  private static final String USER_INFO_DIR = "system" + File.separator + "users";
  private static final String USER_LIST_FILENAME = "userlist.xml";
  private static final String USER_PHOTO_FILENAME = "photo.png";
  private static final String USER_PHOTO_FILENAME_TMP = "photo.png.tmp";
  private static final int USER_VERSION = 6;
  static final int WRITE_USER_DELAY = 2000;
  static final int WRITE_USER_MSG = 1;
  private static final String XATTR_SERIAL = "user.serial";
  private static final String XML_SUFFIX = ".xml";
  private static final IBinder mUserRestriconToken = new Binder();
  private static UserManagerService sInstance;
  private final String ACTION_DISABLE_QUIET_MODE_AFTER_UNLOCK = "com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK";
  private IAppOpsService mAppOpsService;
  @GuardedBy("mRestrictionsLock")
  private final SparseArray<Bundle> mAppliedUserRestrictions = new SparseArray();
  @GuardedBy("mRestrictionsLock")
  private final SparseArray<Bundle> mBaseUserRestrictions = new SparseArray();
  @GuardedBy("mRestrictionsLock")
  private final SparseArray<Bundle> mCachedEffectiveUserRestrictions = new SparseArray();
  private final Context mContext;
  @GuardedBy("mRestrictionsLock")
  private Bundle mDevicePolicyGlobalUserRestrictions;
  @GuardedBy("mRestrictionsLock")
  private final SparseArray<Bundle> mDevicePolicyLocalUserRestrictions = new SparseArray();
  private final BroadcastReceiver mDisableQuietModeCallback = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK".equals(paramAnonymousIntent.getAction()))
      {
        paramAnonymousContext = (IntentSender)paramAnonymousIntent.getParcelableExtra("android.intent.extra.INTENT");
        int i = paramAnonymousIntent.getIntExtra("android.intent.extra.USER_ID", 0);
        UserManagerService.this.setQuietModeEnabled(i, false);
        if (paramAnonymousContext == null) {}
      }
      try
      {
        UserManagerService.-get2(UserManagerService.this).startIntentSender(paramAnonymousContext, null, 0, 0, 0);
        return;
      }
      catch (IntentSender.SendIntentException paramAnonymousContext) {}
    }
  };
  @GuardedBy("mUsersLock")
  private boolean mForceEphemeralUsers;
  @GuardedBy("mRestrictionsLock")
  private int mGlobalRestrictionOwnerUserId = 55536;
  @GuardedBy("mGuestRestrictions")
  private final Bundle mGuestRestrictions = new Bundle();
  private final Handler mHandler;
  @GuardedBy("mUsersLock")
  private boolean mIsDeviceManaged;
  @GuardedBy("mUsersLock")
  private final SparseBooleanArray mIsUserManaged = new SparseBooleanArray();
  private final LocalService mLocalService;
  private final LockPatternUtils mLockPatternUtils;
  @GuardedBy("mPackagesLock")
  private int mNextSerialNumber;
  private final Object mPackagesLock;
  private final PackageManagerService mPm;
  @GuardedBy("mUsersLock")
  private final SparseBooleanArray mRemovingUserIds = new SparseBooleanArray();
  private final Object mRestrictionsLock = new Object();
  @GuardedBy("mUsersLock")
  private int[] mUserIds;
  private final File mUserListFile;
  @GuardedBy("mUserRestrictionsListeners")
  private final ArrayList<UserManagerInternal.UserRestrictionsListener> mUserRestrictionsListeners = new ArrayList();
  @GuardedBy("mUserStates")
  private final SparseIntArray mUserStates = new SparseIntArray();
  private int mUserVersion = 0;
  @GuardedBy("mUsersLock")
  private final SparseArray<UserData> mUsers = new SparseArray();
  private final File mUsersDir;
  private final Object mUsersLock = new Object();
  
  UserManagerService(Context paramContext, PackageManagerService paramPackageManagerService, Object paramObject)
  {
    this(paramContext, paramPackageManagerService, paramObject, Environment.getDataDirectory());
  }
  
  private UserManagerService(Context arg1, PackageManagerService paramPackageManagerService, Object paramObject, File paramFile)
  {
    this.mContext = ???;
    this.mPm = paramPackageManagerService;
    this.mPackagesLock = paramObject;
    this.mHandler = new MainHandler();
    synchronized (this.mPackagesLock)
    {
      this.mUsersDir = new File(paramFile, USER_INFO_DIR);
      this.mUsersDir.mkdirs();
      new File(this.mUsersDir, String.valueOf(0)).mkdirs();
      FileUtils.setPermissions(this.mUsersDir.toString(), 509, -1, -1);
      this.mUserListFile = new File(this.mUsersDir, "userlist.xml");
      initDefaultGuestRestrictions();
      readUserListLP();
      sInstance = this;
      this.mLocalService = new LocalService(null);
      LocalServices.addService(UserManagerInternal.class, this.mLocalService);
      this.mLockPatternUtils = new LockPatternUtils(this.mContext);
      this.mUserStates.put(0, 0);
      return;
    }
  }
  
  UserManagerService(File paramFile)
  {
    this(null, null, new Object(), paramFile);
  }
  
  private void broadcastProfileAvailabilityChanges(UserHandle paramUserHandle1, UserHandle paramUserHandle2, boolean paramBoolean)
  {
    Intent localIntent = new Intent();
    if (paramBoolean) {
      localIntent.setAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
    }
    for (;;)
    {
      localIntent.putExtra("android.intent.extra.QUIET_MODE", paramBoolean);
      localIntent.putExtra("android.intent.extra.USER", paramUserHandle1);
      localIntent.putExtra("android.intent.extra.user_handle", paramUserHandle1.getIdentifier());
      localIntent.addFlags(1073741824);
      this.mContext.sendBroadcastAsUser(localIntent, paramUserHandle2);
      return;
      localIntent.setAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
    }
  }
  
  private static final void checkManageOrCreateUsersPermission(int paramInt)
  {
    if ((paramInt & 0xFCD3) == 0)
    {
      if (!hasManageOrCreateUsersPermission()) {
        throw new SecurityException("You either need MANAGE_USERS or CREATE_USERS permission to create an user with flags: " + paramInt);
      }
    }
    else if (!hasManageUsersPermission()) {
      throw new SecurityException("You need MANAGE_USERS permission to create an user  with flags: " + paramInt);
    }
  }
  
  private static final void checkManageOrCreateUsersPermission(String paramString)
  {
    if (!hasManageOrCreateUsersPermission()) {
      throw new SecurityException("You either need MANAGE_USERS or CREATE_USERS permission to: " + paramString);
    }
  }
  
  private static final void checkManageUserAndAcrossUsersFullPermission(String paramString)
  {
    int i = Binder.getCallingUid();
    if ((i != 1000) && (i != 0) && (ActivityManager.checkComponentPermission("android.permission.MANAGE_USERS", i, -1, true) != 0) && (ActivityManager.checkComponentPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i, -1, true) != 0)) {
      throw new SecurityException("You need MANAGE_USERS and INTERACT_ACROSS_USERS_FULL permission to: " + paramString);
    }
  }
  
  private static final void checkManageUsersPermission(String paramString)
  {
    if (!hasManageUsersPermission()) {
      throw new SecurityException("You need MANAGE_USERS permission to: " + paramString);
    }
  }
  
  private static void checkSystemOrRoot(String paramString)
  {
    int i = Binder.getCallingUid();
    if ((!UserHandle.isSameApp(i, 1000)) && (i != 0)) {
      throw new SecurityException("Only system may: " + paramString);
    }
  }
  
  private void cleanAppRestrictionsForPackage(String paramString, int paramInt)
  {
    synchronized (this.mPackagesLock)
    {
      paramString = new File(Environment.getUserSystemDirectory(paramInt), packageToRestrictionsFileName(paramString));
      if (paramString.exists()) {
        paramString.delete();
      }
      return;
    }
  }
  
  @GuardedBy("mRestrictionsLock")
  private Bundle computeEffectiveUserRestrictionsLR(int paramInt)
  {
    Bundle localBundle3 = UserRestrictionsUtils.nonNull((Bundle)this.mBaseUserRestrictions.get(paramInt));
    Bundle localBundle1 = this.mDevicePolicyGlobalUserRestrictions;
    Bundle localBundle2 = (Bundle)this.mDevicePolicyLocalUserRestrictions.get(paramInt);
    if ((UserRestrictionsUtils.isEmpty(localBundle1)) && (UserRestrictionsUtils.isEmpty(localBundle2))) {
      return localBundle3;
    }
    localBundle3 = UserRestrictionsUtils.clone(localBundle3);
    UserRestrictionsUtils.merge(localBundle3, localBundle1);
    UserRestrictionsUtils.merge(localBundle3, localBundle2);
    return localBundle3;
  }
  
  private UserInfo createUserInternal(String paramString, int paramInt1, int paramInt2)
  {
    if (hasUserRestriction("no_add_user", UserHandle.getCallingUserId()))
    {
      Log.w("UserManagerService", "Cannot add user. DISALLOW_ADD_USER is enabled.");
      return null;
    }
    return createUserInternalUnchecked(paramString, paramInt1, paramInt2);
  }
  
  /* Error */
  private UserInfo createUserInternalUnchecked(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: invokestatic 639	android/app/ActivityManager:isLowRamDeviceStatic	()Z
    //   3: ifeq +5 -> 8
    //   6: aconst_null
    //   7: areturn
    //   8: iload_2
    //   9: iconst_4
    //   10: iand
    //   11: ifeq +99 -> 110
    //   14: iconst_1
    //   15: istore 4
    //   17: iload_2
    //   18: bipush 32
    //   20: iand
    //   21: ifeq +95 -> 116
    //   24: iconst_1
    //   25: istore 5
    //   27: iload_2
    //   28: bipush 8
    //   30: iand
    //   31: ifeq +91 -> 122
    //   34: iconst_1
    //   35: istore 6
    //   37: iload_2
    //   38: sipush 512
    //   41: iand
    //   42: ifeq +86 -> 128
    //   45: iconst_1
    //   46: istore 7
    //   48: invokestatic 643	android/os/Binder:clearCallingIdentity	()J
    //   51: lstore 10
    //   53: aload_0
    //   54: getfield 272	com/android/server/pm/UserManagerService:mPackagesLock	Ljava/lang/Object;
    //   57: astore 15
    //   59: aload 15
    //   61: monitorenter
    //   62: aconst_null
    //   63: astore 13
    //   65: iload_3
    //   66: sipush 55536
    //   69: if_icmpeq +85 -> 154
    //   72: aload_0
    //   73: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   76: astore 13
    //   78: aload 13
    //   80: monitorenter
    //   81: aload_0
    //   82: iload_3
    //   83: invokespecial 646	com/android/server/pm/UserManagerService:getUserDataLU	(I)Lcom/android/server/pm/UserManagerService$UserData;
    //   86: astore 14
    //   88: aload 13
    //   90: monitorexit
    //   91: aload 14
    //   93: astore 13
    //   95: aload 14
    //   97: ifnonnull +57 -> 154
    //   100: aload 15
    //   102: monitorexit
    //   103: lload 10
    //   105: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   108: aconst_null
    //   109: areturn
    //   110: iconst_0
    //   111: istore 4
    //   113: goto -96 -> 17
    //   116: iconst_0
    //   117: istore 5
    //   119: goto -92 -> 27
    //   122: iconst_0
    //   123: istore 6
    //   125: goto -88 -> 37
    //   128: iconst_0
    //   129: istore 7
    //   131: goto -83 -> 48
    //   134: astore_1
    //   135: aload 13
    //   137: monitorexit
    //   138: aload_1
    //   139: athrow
    //   140: astore_1
    //   141: aload 15
    //   143: monitorexit
    //   144: aload_1
    //   145: athrow
    //   146: astore_1
    //   147: lload 10
    //   149: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   152: aload_1
    //   153: athrow
    //   154: iload 5
    //   156: ifeq +868 -> 1024
    //   159: aload_0
    //   160: iload_3
    //   161: iconst_0
    //   162: invokevirtual 654	com/android/server/pm/UserManagerService:canAddMoreManagedProfiles	(IZ)Z
    //   165: ifeq +32 -> 197
    //   168: goto +856 -> 1024
    //   171: iload 4
    //   173: ifeq +86 -> 259
    //   176: aload_0
    //   177: invokespecial 658	com/android/server/pm/UserManagerService:findCurrentGuestUser	()Landroid/content/pm/UserInfo;
    //   180: astore 14
    //   182: aload 14
    //   184: ifnull +75 -> 259
    //   187: aload 15
    //   189: monitorexit
    //   190: lload 10
    //   192: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   195: aconst_null
    //   196: areturn
    //   197: ldc 122
    //   199: new 347	java/lang/StringBuilder
    //   202: dup
    //   203: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   206: ldc_w 660
    //   209: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: iload_3
    //   213: invokevirtual 540	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   216: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   219: invokestatic 663	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   222: pop
    //   223: aload 15
    //   225: monitorexit
    //   226: lload 10
    //   228: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   231: aconst_null
    //   232: areturn
    //   233: iload 7
    //   235: ifne -64 -> 171
    //   238: aload_0
    //   239: invokespecial 666	com/android/server/pm/UserManagerService:isUserLimitReached	()Z
    //   242: istore 12
    //   244: iload 12
    //   246: ifeq -75 -> 171
    //   249: aload 15
    //   251: monitorexit
    //   252: lload 10
    //   254: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   257: aconst_null
    //   258: areturn
    //   259: iload 6
    //   261: ifeq +9 -> 270
    //   264: invokestatic 671	android/os/UserManager:isSplitSystemUser	()Z
    //   267: ifeq +38 -> 305
    //   270: iload 6
    //   272: ifeq +103 -> 375
    //   275: invokestatic 671	android/os/UserManager:isSplitSystemUser	()Z
    //   278: ifeq +97 -> 375
    //   281: aload 13
    //   283: ifnonnull +45 -> 328
    //   286: ldc 122
    //   288: ldc_w 673
    //   291: invokestatic 636	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   294: pop
    //   295: aload 15
    //   297: monitorexit
    //   298: lload 10
    //   300: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   303: aconst_null
    //   304: areturn
    //   305: iload_3
    //   306: ifeq -36 -> 270
    //   309: ldc 122
    //   311: ldc_w 675
    //   314: invokestatic 636	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   317: pop
    //   318: aload 15
    //   320: monitorexit
    //   321: lload 10
    //   323: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   326: aconst_null
    //   327: areturn
    //   328: aload 13
    //   330: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   333: invokevirtual 684	android/content/pm/UserInfo:canHaveProfile	()Z
    //   336: ifne +39 -> 375
    //   339: ldc 122
    //   341: new 347	java/lang/StringBuilder
    //   344: dup
    //   345: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   348: ldc_w 686
    //   351: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   354: iload_3
    //   355: invokevirtual 540	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   358: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   361: invokestatic 636	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   364: pop
    //   365: aload 15
    //   367: monitorexit
    //   368: lload 10
    //   370: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   373: aconst_null
    //   374: areturn
    //   375: invokestatic 671	android/os/UserManager:isSplitSystemUser	()Z
    //   378: ifne +38 -> 416
    //   381: iload_2
    //   382: sipush 256
    //   385: iand
    //   386: ifeq +30 -> 416
    //   389: iload_2
    //   390: sipush 512
    //   393: iand
    //   394: ifne +22 -> 416
    //   397: ldc 122
    //   399: ldc_w 688
    //   402: invokestatic 663	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   405: pop
    //   406: aload 15
    //   408: monitorexit
    //   409: lload 10
    //   411: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   414: aconst_null
    //   415: areturn
    //   416: iload_2
    //   417: istore_3
    //   418: invokestatic 671	android/os/UserManager:isSplitSystemUser	()Z
    //   421: ifeq +10 -> 431
    //   424: iload 4
    //   426: ifeq +453 -> 879
    //   429: iload_2
    //   430: istore_3
    //   431: aload_0
    //   432: invokespecial 691	com/android/server/pm/UserManagerService:getNextAvailableId	()I
    //   435: istore 7
    //   437: iload 7
    //   439: invokestatic 582	android/os/Environment:getUserSystemDirectory	(I)Ljava/io/File;
    //   442: invokevirtual 435	java/io/File:mkdirs	()Z
    //   445: pop
    //   446: invokestatic 697	android/content/res/Resources:getSystem	()Landroid/content/res/Resources;
    //   449: ldc_w 698
    //   452: invokevirtual 702	android/content/res/Resources:getBoolean	(I)Z
    //   455: istore 12
    //   457: aload_0
    //   458: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   461: astore 16
    //   463: aload 16
    //   465: monitorenter
    //   466: iload 4
    //   468: ifeq +8 -> 476
    //   471: iload 12
    //   473: ifne +564 -> 1037
    //   476: aload_0
    //   477: getfield 291	com/android/server/pm/UserManagerService:mForceEphemeralUsers	Z
    //   480: ifne +557 -> 1037
    //   483: iload_3
    //   484: istore_2
    //   485: aload 13
    //   487: ifnull +19 -> 506
    //   490: iload_3
    //   491: istore_2
    //   492: aload 13
    //   494: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   497: invokevirtual 705	android/content/pm/UserInfo:isEphemeral	()Z
    //   500: ifeq +6 -> 506
    //   503: goto +534 -> 1037
    //   506: new 681	android/content/pm/UserInfo
    //   509: dup
    //   510: iload 7
    //   512: aload_1
    //   513: aconst_null
    //   514: iload_2
    //   515: invokespecial 708	android/content/pm/UserInfo:<init>	(ILjava/lang/String;Ljava/lang/String;I)V
    //   518: astore 14
    //   520: aload_0
    //   521: getfield 710	com/android/server/pm/UserManagerService:mNextSerialNumber	I
    //   524: istore_2
    //   525: aload_0
    //   526: iload_2
    //   527: iconst_1
    //   528: iadd
    //   529: putfield 710	com/android/server/pm/UserManagerService:mNextSerialNumber	I
    //   532: aload 14
    //   534: iload_2
    //   535: putfield 712	android/content/pm/UserInfo:serialNumber	I
    //   538: invokestatic 717	java/lang/System:currentTimeMillis	()J
    //   541: lstore 8
    //   543: lload 8
    //   545: ldc2_w 118
    //   548: lcmp
    //   549: ifle +497 -> 1046
    //   552: aload 14
    //   554: lload 8
    //   556: putfield 720	android/content/pm/UserInfo:creationTime	J
    //   559: aload 14
    //   561: iconst_1
    //   562: putfield 722	android/content/pm/UserInfo:partial	Z
    //   565: aload 14
    //   567: getstatic 727	android/os/Build:FINGERPRINT	Ljava/lang/String;
    //   570: putfield 729	android/content/pm/UserInfo:lastLoggedInFingerprint	Ljava/lang/String;
    //   573: new 34	com/android/server/pm/UserManagerService$UserData
    //   576: dup
    //   577: aconst_null
    //   578: invokespecial 731	com/android/server/pm/UserManagerService$UserData:<init>	(Lcom/android/server/pm/UserManagerService$UserData;)V
    //   581: astore_1
    //   582: aload_1
    //   583: aload 14
    //   585: putfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   588: aload_0
    //   589: getfield 389	com/android/server/pm/UserManagerService:mUsers	Landroid/util/SparseArray;
    //   592: iload 7
    //   594: aload_1
    //   595: invokevirtual 734	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   598: aload 16
    //   600: monitorexit
    //   601: aload_0
    //   602: aload_1
    //   603: invokespecial 344	com/android/server/pm/UserManagerService:writeUserLP	(Lcom/android/server/pm/UserManagerService$UserData;)V
    //   606: aload_0
    //   607: invokespecial 737	com/android/server/pm/UserManagerService:writeUserListLP	()V
    //   610: aload 13
    //   612: ifnull +57 -> 669
    //   615: iload 5
    //   617: ifeq +326 -> 943
    //   620: aload 13
    //   622: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   625: getfield 739	android/content/pm/UserInfo:profileGroupId	I
    //   628: sipush 55536
    //   631: if_icmpne +25 -> 656
    //   634: aload 13
    //   636: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   639: aload 13
    //   641: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   644: getfield 741	android/content/pm/UserInfo:id	I
    //   647: putfield 739	android/content/pm/UserInfo:profileGroupId	I
    //   650: aload_0
    //   651: aload 13
    //   653: invokespecial 344	com/android/server/pm/UserManagerService:writeUserLP	(Lcom/android/server/pm/UserManagerService$UserData;)V
    //   656: aload 14
    //   658: aload 13
    //   660: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   663: getfield 739	android/content/pm/UserInfo:profileGroupId	I
    //   666: putfield 739	android/content/pm/UserInfo:profileGroupId	I
    //   669: aload 15
    //   671: monitorexit
    //   672: aload_0
    //   673: getfield 261	com/android/server/pm/UserManagerService:mContext	Landroid/content/Context;
    //   676: ldc_w 743
    //   679: invokevirtual 747	android/content/Context:getSystemService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   682: checkcast 743	android/os/storage/StorageManager
    //   685: iload 7
    //   687: aload 14
    //   689: getfield 712	android/content/pm/UserInfo:serialNumber	I
    //   692: aload 14
    //   694: invokevirtual 705	android/content/pm/UserInfo:isEphemeral	()Z
    //   697: invokevirtual 751	android/os/storage/StorageManager:createUserKey	(IIZ)V
    //   700: aload_0
    //   701: getfield 425	com/android/server/pm/UserManagerService:mPm	Lcom/android/server/pm/PackageManagerService;
    //   704: iload 7
    //   706: aload 14
    //   708: getfield 712	android/content/pm/UserInfo:serialNumber	I
    //   711: iconst_3
    //   712: invokevirtual 757	com/android/server/pm/PackageManagerService:prepareUserData	(III)V
    //   715: aload_0
    //   716: getfield 425	com/android/server/pm/UserManagerService:mPm	Lcom/android/server/pm/PackageManagerService;
    //   719: iload 7
    //   721: invokevirtual 760	com/android/server/pm/PackageManagerService:createNewUser	(I)V
    //   724: aload 14
    //   726: iconst_0
    //   727: putfield 722	android/content/pm/UserInfo:partial	Z
    //   730: aload_0
    //   731: getfield 272	com/android/server/pm/UserManagerService:mPackagesLock	Ljava/lang/Object;
    //   734: astore 13
    //   736: aload 13
    //   738: monitorenter
    //   739: aload_0
    //   740: aload_1
    //   741: invokespecial 344	com/android/server/pm/UserManagerService:writeUserLP	(Lcom/android/server/pm/UserManagerService$UserData;)V
    //   744: aload 13
    //   746: monitorexit
    //   747: aload_0
    //   748: invokespecial 763	com/android/server/pm/UserManagerService:updateUserIds	()V
    //   751: new 399	android/os/Bundle
    //   754: dup
    //   755: invokespecial 400	android/os/Bundle:<init>	()V
    //   758: astore_1
    //   759: iload 4
    //   761: ifeq +23 -> 784
    //   764: aload_0
    //   765: getfield 402	com/android/server/pm/UserManagerService:mGuestRestrictions	Landroid/os/Bundle;
    //   768: astore 13
    //   770: aload 13
    //   772: monitorenter
    //   773: aload_1
    //   774: aload_0
    //   775: getfield 402	com/android/server/pm/UserManagerService:mGuestRestrictions	Landroid/os/Bundle;
    //   778: invokevirtual 767	android/os/Bundle:putAll	(Landroid/os/Bundle;)V
    //   781: aload 13
    //   783: monitorexit
    //   784: aload_0
    //   785: getfield 275	com/android/server/pm/UserManagerService:mRestrictionsLock	Ljava/lang/Object;
    //   788: astore 13
    //   790: aload 13
    //   792: monitorenter
    //   793: aload_0
    //   794: getfield 253	com/android/server/pm/UserManagerService:mBaseUserRestrictions	Landroid/util/SparseArray;
    //   797: iload 7
    //   799: aload_1
    //   800: invokevirtual 769	android/util/SparseArray:append	(ILjava/lang/Object;)V
    //   803: aload 13
    //   805: monitorexit
    //   806: aload_0
    //   807: getfield 425	com/android/server/pm/UserManagerService:mPm	Lcom/android/server/pm/PackageManagerService;
    //   810: iload 7
    //   812: invokevirtual 772	com/android/server/pm/PackageManagerService:onNewUserCreated	(I)V
    //   815: new 487	android/content/Intent
    //   818: dup
    //   819: ldc_w 774
    //   822: invokespecial 775	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   825: astore_1
    //   826: aload_1
    //   827: ldc_w 507
    //   830: iload 7
    //   832: invokevirtual 516	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   835: pop
    //   836: aload_0
    //   837: getfield 261	com/android/server/pm/UserManagerService:mContext	Landroid/content/Context;
    //   840: aload_1
    //   841: getstatic 779	android/os/UserHandle:ALL	Landroid/os/UserHandle;
    //   844: ldc_w 556
    //   847: invokevirtual 782	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;Ljava/lang/String;)V
    //   850: aload_0
    //   851: getfield 261	com/android/server/pm/UserManagerService:mContext	Landroid/content/Context;
    //   854: astore 13
    //   856: iload 4
    //   858: ifeq +160 -> 1018
    //   861: ldc -89
    //   863: astore_1
    //   864: aload 13
    //   866: aload_1
    //   867: iconst_1
    //   868: invokestatic 788	com/android/internal/logging/MetricsLogger:count	(Landroid/content/Context;Ljava/lang/String;I)V
    //   871: lload 10
    //   873: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   876: aload 14
    //   878: areturn
    //   879: iload_2
    //   880: istore_3
    //   881: iload 5
    //   883: ifne -452 -> 431
    //   886: iload_2
    //   887: istore_3
    //   888: aload_0
    //   889: invokevirtual 791	com/android/server/pm/UserManagerService:getPrimaryUser	()Landroid/content/pm/UserInfo;
    //   892: ifnonnull -461 -> 431
    //   895: iload_2
    //   896: iconst_1
    //   897: ior
    //   898: istore_2
    //   899: aload_0
    //   900: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   903: astore 14
    //   905: aload 14
    //   907: monitorenter
    //   908: aload_0
    //   909: getfield 294	com/android/server/pm/UserManagerService:mIsDeviceManaged	Z
    //   912: istore 12
    //   914: iload_2
    //   915: istore_3
    //   916: iload 12
    //   918: ifne +7 -> 925
    //   921: iload_2
    //   922: iconst_2
    //   923: ior
    //   924: istore_3
    //   925: aload 14
    //   927: monitorexit
    //   928: goto -497 -> 431
    //   931: astore_1
    //   932: aload 14
    //   934: monitorexit
    //   935: aload_1
    //   936: athrow
    //   937: astore_1
    //   938: aload 16
    //   940: monitorexit
    //   941: aload_1
    //   942: athrow
    //   943: iload 6
    //   945: ifeq -276 -> 669
    //   948: aload 13
    //   950: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   953: getfield 793	android/content/pm/UserInfo:restrictedProfileParentId	I
    //   956: sipush 55536
    //   959: if_icmpne +25 -> 984
    //   962: aload 13
    //   964: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   967: aload 13
    //   969: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   972: getfield 741	android/content/pm/UserInfo:id	I
    //   975: putfield 793	android/content/pm/UserInfo:restrictedProfileParentId	I
    //   978: aload_0
    //   979: aload 13
    //   981: invokespecial 344	com/android/server/pm/UserManagerService:writeUserLP	(Lcom/android/server/pm/UserManagerService$UserData;)V
    //   984: aload 14
    //   986: aload 13
    //   988: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   991: getfield 793	android/content/pm/UserInfo:restrictedProfileParentId	I
    //   994: putfield 793	android/content/pm/UserInfo:restrictedProfileParentId	I
    //   997: goto -328 -> 669
    //   1000: astore_1
    //   1001: aload 13
    //   1003: monitorexit
    //   1004: aload_1
    //   1005: athrow
    //   1006: astore_1
    //   1007: aload 13
    //   1009: monitorexit
    //   1010: aload_1
    //   1011: athrow
    //   1012: astore_1
    //   1013: aload 13
    //   1015: monitorexit
    //   1016: aload_1
    //   1017: athrow
    //   1018: ldc -86
    //   1020: astore_1
    //   1021: goto -157 -> 864
    //   1024: iload 4
    //   1026: ifne -855 -> 171
    //   1029: iload 5
    //   1031: ifeq -798 -> 233
    //   1034: goto -863 -> 171
    //   1037: iload_3
    //   1038: sipush 256
    //   1041: ior
    //   1042: istore_2
    //   1043: goto -537 -> 506
    //   1046: lconst_0
    //   1047: lstore 8
    //   1049: goto -497 -> 552
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1052	0	this	UserManagerService
    //   0	1052	1	paramString	String
    //   0	1052	2	paramInt1	int
    //   0	1052	3	paramInt2	int
    //   15	1010	4	i	int
    //   25	1005	5	j	int
    //   35	909	6	k	int
    //   46	785	7	m	int
    //   541	507	8	l1	long
    //   51	821	10	l2	long
    //   242	675	12	bool	boolean
    //   57	613	15	localObject3	Object
    //   461	478	16	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   81	88	134	finally
    //   72	81	140	finally
    //   88	91	140	finally
    //   135	140	140	finally
    //   159	168	140	finally
    //   176	182	140	finally
    //   197	223	140	finally
    //   238	244	140	finally
    //   264	270	140	finally
    //   275	281	140	finally
    //   286	295	140	finally
    //   309	318	140	finally
    //   328	365	140	finally
    //   375	381	140	finally
    //   397	406	140	finally
    //   418	424	140	finally
    //   431	466	140	finally
    //   598	610	140	finally
    //   620	656	140	finally
    //   656	669	140	finally
    //   888	895	140	finally
    //   899	908	140	finally
    //   925	928	140	finally
    //   932	937	140	finally
    //   938	943	140	finally
    //   948	984	140	finally
    //   984	997	140	finally
    //   53	62	146	finally
    //   100	103	146	finally
    //   141	146	146	finally
    //   187	190	146	finally
    //   223	226	146	finally
    //   249	252	146	finally
    //   295	298	146	finally
    //   318	321	146	finally
    //   365	368	146	finally
    //   406	409	146	finally
    //   669	739	146	finally
    //   744	759	146	finally
    //   764	773	146	finally
    //   781	784	146	finally
    //   784	793	146	finally
    //   803	856	146	finally
    //   864	871	146	finally
    //   1001	1006	146	finally
    //   1007	1012	146	finally
    //   1013	1018	146	finally
    //   908	914	931	finally
    //   476	483	937	finally
    //   492	503	937	finally
    //   506	543	937	finally
    //   552	598	937	finally
    //   739	744	1000	finally
    //   773	781	1006	finally
    //   793	803	1012	finally
  }
  
  private static void debug(String paramString)
  {
    Log.d("UserManagerService", paramString + "");
  }
  
  public static void enforceSerialNumber(File paramFile, int paramInt)
    throws IOException
  {
    if (StorageManager.isFileEncryptedEmulatedOnly())
    {
      Slog.w("UserManagerService", "Device is emulating FBE; assuming current serial number is valid");
      return;
    }
    if ("true".equals(SystemProperties.get("ro.mount.tempfs", "false")))
    {
      Slog.i("UserManagerService", "Mounting tempfs data, enforceSerialNumber return.");
      return;
    }
    int i = getSerialNumber(paramFile);
    Slog.v("UserManagerService", "Found " + paramFile + " with serial number " + i);
    if (i == -1) {
      Slog.d("UserManagerService", "Serial number missing on " + paramFile + "; assuming current is valid");
    }
    while (i == paramInt) {
      try
      {
        setSerialNumber(paramFile, paramInt);
        return;
      }
      catch (IOException localIOException)
      {
        Slog.w("UserManagerService", "Failed to set serial number on " + paramFile, localIOException);
        return;
      }
    }
    throw new IOException("Found serial number " + i + " doesn't match expected " + paramInt);
  }
  
  private void fallbackToSingleUserLP()
  {
    int j = 0;
    int i = 16;
    if (!UserManager.isSplitSystemUser()) {
      i = 19;
    }
    Object localObject3 = new UserInfo(0, null, null, i);
    UserData localUserData1 = new UserData(null);
    localUserData1.info = ((UserInfo)localObject3);
    synchronized (this.mUsersLock)
    {
      this.mUsers.put(((UserInfo)localObject3).id, localUserData1);
      this.mNextSerialNumber = 10;
      this.mUserVersion = 6;
      ??? = new Bundle();
    }
    try
    {
      localObject3 = this.mContext.getResources().getStringArray(17236067);
      int k = localObject3.length;
      i = j;
      while (i < k)
      {
        String str = localObject3[i];
        if (UserRestrictionsUtils.isValidRestriction(str)) {
          ((Bundle)???).putBoolean(str, true);
        }
        i += 1;
      }
      localUserData2 = finally;
      throw localUserData2;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Log.e("UserManagerService", "Couldn't find resource: config_defaultFirstUserRestrictions", localNotFoundException);
      synchronized (this.mRestrictionsLock)
      {
        this.mBaseUserRestrictions.append(0, ???);
        updateUserIds();
        initDefaultGuestRestrictions();
        writeUserLP(localUserData2);
        writeUserListLP();
        return;
      }
    }
  }
  
  private UserInfo findCurrentGuestUser()
  {
    for (;;)
    {
      int i;
      synchronized (this.mUsersLock)
      {
        int j = this.mUsers.size();
        i = 0;
        if (i < j)
        {
          UserInfo localUserInfo = ((UserData)this.mUsers.valueAt(i)).info;
          if ((localUserInfo.isGuest()) && (!localUserInfo.guestToRemove))
          {
            boolean bool = this.mRemovingUserIds.get(localUserInfo.id);
            if (!bool) {
              return localUserInfo;
            }
          }
        }
        else
        {
          return null;
        }
      }
      i += 1;
    }
  }
  
  private int getAliveUsersExcludingGuestsCountLU()
  {
    int j = 0;
    int m = this.mUsers.size();
    int i = 0;
    if (i < m)
    {
      UserInfo localUserInfo = ((UserData)this.mUsers.valueAt(i)).info;
      int k = j;
      if (!this.mRemovingUserIds.get(localUserInfo.id))
      {
        if (!localUserInfo.isGuest()) {
          break label71;
        }
        k = j;
      }
      for (;;)
      {
        i += 1;
        j = k;
        break;
        label71:
        k = j;
        if (!localUserInfo.partial) {
          k = j + 1;
        }
      }
    }
    return j;
  }
  
  private Bundle getEffectiveUserRestrictions(int paramInt)
  {
    synchronized (this.mRestrictionsLock)
    {
      Bundle localBundle2 = (Bundle)this.mCachedEffectiveUserRestrictions.get(paramInt);
      Bundle localBundle1 = localBundle2;
      if (localBundle2 == null)
      {
        localBundle1 = computeEffectiveUserRestrictionsLR(paramInt);
        this.mCachedEffectiveUserRestrictions.put(paramInt, localBundle1);
      }
      return localBundle1;
    }
  }
  
  public static UserManagerService getInstance()
  {
    try
    {
      UserManagerService localUserManagerService = sInstance;
      return localUserManagerService;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private int getNextAvailableId()
  {
    localObject1 = this.mUsersLock;
    int i = 10;
    for (;;)
    {
      if (i < 21474) {}
      try
      {
        if (this.mUsers.indexOfKey(i) < 0)
        {
          boolean bool = this.mRemovingUserIds.get(i);
          if (!bool) {}
        }
        else
        {
          i += 1;
          continue;
        }
        return i;
      }
      finally {}
    }
    throw new IllegalStateException("No user id available!");
  }
  
  private String getOwnerName()
  {
    return this.mContext.getResources().getString(17040713);
  }
  
  private IntArray getProfileIdsLU(int paramInt, boolean paramBoolean)
  {
    UserInfo localUserInfo1 = getUserInfoLU(paramInt);
    IntArray localIntArray = new IntArray(this.mUsers.size());
    if (localUserInfo1 == null) {
      return localIntArray;
    }
    int i = this.mUsers.size();
    paramInt = 0;
    if (paramInt < i)
    {
      UserInfo localUserInfo2 = ((UserData)this.mUsers.valueAt(paramInt)).info;
      if (!isProfileOf(localUserInfo1, localUserInfo2)) {}
      for (;;)
      {
        paramInt += 1;
        break;
        if (((!paramBoolean) || (localUserInfo2.isEnabled())) && (!this.mRemovingUserIds.get(localUserInfo2.id)) && (!localUserInfo2.partial)) {
          localIntArray.add(localUserInfo2.id);
        }
      }
    }
    return localIntArray;
  }
  
  private UserInfo getProfileParentLU(int paramInt)
  {
    UserInfo localUserInfo = getUserInfoLU(paramInt);
    if (localUserInfo == null) {
      return null;
    }
    paramInt = localUserInfo.profileGroupId;
    if (paramInt == 55536) {
      return null;
    }
    return getUserInfoLU(paramInt);
  }
  
  private List<UserInfo> getProfilesLU(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    IntArray localIntArray = getProfileIdsLU(paramInt, paramBoolean1);
    ArrayList localArrayList = new ArrayList(localIntArray.size());
    paramInt = 0;
    if (paramInt < localIntArray.size())
    {
      int i = localIntArray.get(paramInt);
      UserInfo localUserInfo = ((UserData)this.mUsers.get(i)).info;
      if (!paramBoolean2)
      {
        localUserInfo = new UserInfo(localUserInfo);
        localUserInfo.name = null;
        localUserInfo.iconPath = null;
      }
      for (;;)
      {
        localArrayList.add(localUserInfo);
        paramInt += 1;
        break;
        localUserInfo = userWithName(localUserInfo);
      }
    }
    return localArrayList;
  }
  
  /* Error */
  private static int getSerialNumber(File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: sipush 256
    //   3: newarray <illegal type>
    //   5: astore_2
    //   6: new 437	java/lang/String
    //   9: dup
    //   10: aload_2
    //   11: iconst_0
    //   12: aload_0
    //   13: invokevirtual 967	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   16: ldc -68
    //   18: aload_2
    //   19: invokestatic 973	android/system/Os:getxattr	(Ljava/lang/String;Ljava/lang/String;[B)I
    //   22: invokespecial 976	java/lang/String:<init>	([BII)V
    //   25: astore_0
    //   26: aload_0
    //   27: invokestatic 982	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   30: istore_1
    //   31: iload_1
    //   32: ireturn
    //   33: astore_2
    //   34: new 803	java/io/IOException
    //   37: dup
    //   38: new 347	java/lang/StringBuilder
    //   41: dup
    //   42: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   45: ldc_w 984
    //   48: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload_0
    //   52: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   55: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   58: invokespecial 862	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   61: athrow
    //   62: astore_0
    //   63: aload_0
    //   64: getfield 987	android/system/ErrnoException:errno	I
    //   67: getstatic 992	android/system/OsConstants:ENODATA	I
    //   70: if_icmpne +5 -> 75
    //   73: iconst_m1
    //   74: ireturn
    //   75: aload_0
    //   76: invokevirtual 996	android/system/ErrnoException:rethrowAsIOException	()Ljava/io/IOException;
    //   79: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	paramFile	File
    //   30	2	1	i	int
    //   5	14	2	arrayOfByte	byte[]
    //   33	1	2	localNumberFormatException	NumberFormatException
    // Exception table:
    //   from	to	target	type
    //   26	31	33	java/lang/NumberFormatException
    //   0	26	62	android/system/ErrnoException
    //   26	31	62	android/system/ErrnoException
    //   34	62	62	android/system/ErrnoException
  }
  
  private int getUidForPackage(String paramString)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      int i = this.mContext.getPackageManager().getApplicationInfo(paramString, 8192).uid;
      Binder.restoreCallingIdentity(l);
      return i;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      paramString = paramString;
      Binder.restoreCallingIdentity(l);
      return -1;
    }
    finally
    {
      paramString = finally;
      Binder.restoreCallingIdentity(l);
      throw paramString;
    }
  }
  
  private UserData getUserDataLU(int paramInt)
  {
    UserData localUserData = (UserData)this.mUsers.get(paramInt);
    if ((localUserData == null) || (!localUserData.info.partial) || (this.mRemovingUserIds.get(paramInt))) {
      return localUserData;
    }
    return null;
  }
  
  private UserData getUserDataNoChecks(int paramInt)
  {
    synchronized (this.mUsersLock)
    {
      UserData localUserData = (UserData)this.mUsers.get(paramInt);
      return localUserData;
    }
  }
  
  private UserInfo getUserInfoLU(int paramInt)
  {
    UserInfo localUserInfo = null;
    UserData localUserData = (UserData)this.mUsers.get(paramInt);
    if ((localUserData == null) || (!localUserData.info.partial) || (this.mRemovingUserIds.get(paramInt)))
    {
      if (localUserData != null) {
        localUserInfo = localUserData.info;
      }
      return localUserInfo;
    }
    Slog.w("UserManagerService", "getUserInfo: unknown user #" + paramInt);
    return null;
  }
  
  private UserInfo getUserInfoNoChecks(int paramInt)
  {
    UserInfo localUserInfo = null;
    synchronized (this.mUsersLock)
    {
      UserData localUserData = (UserData)this.mUsers.get(paramInt);
      if (localUserData != null) {
        localUserInfo = localUserData.info;
      }
      return localUserInfo;
    }
  }
  
  private static final boolean hasManageOrCreateUsersPermission()
  {
    int i = Binder.getCallingUid();
    if ((UserHandle.isSameApp(i, 1000)) || (i == 0)) {}
    while ((ActivityManager.checkComponentPermission("android.permission.MANAGE_USERS", i, -1, true) == 0) || (ActivityManager.checkComponentPermission("android.permission.CREATE_USERS", i, -1, true) == 0)) {
      return true;
    }
    return false;
  }
  
  private static final boolean hasManageUsersPermission()
  {
    int i = Binder.getCallingUid();
    if ((UserHandle.isSameApp(i, 1000)) || (i == 0)) {}
    while (ActivityManager.checkComponentPermission("android.permission.MANAGE_USERS", i, -1, true) == 0) {
      return true;
    }
    return false;
  }
  
  private void initDefaultGuestRestrictions()
  {
    synchronized (this.mGuestRestrictions)
    {
      if (this.mGuestRestrictions.isEmpty())
      {
        this.mGuestRestrictions.putBoolean("no_config_wifi", true);
        this.mGuestRestrictions.putBoolean("no_install_unknown_sources", true);
        this.mGuestRestrictions.putBoolean("no_outgoing_calls", true);
        this.mGuestRestrictions.putBoolean("no_sms", true);
      }
      return;
    }
  }
  
  @GuardedBy("mRestrictionsLock")
  private void invalidateEffectiveUserRestrictionsLR(int paramInt)
  {
    this.mCachedEffectiveUserRestrictions.remove(paramInt);
  }
  
  private static boolean isProfileOf(UserInfo paramUserInfo1, UserInfo paramUserInfo2)
  {
    if (paramUserInfo1.id != paramUserInfo2.id)
    {
      if (paramUserInfo1.profileGroupId == 55536) {}
    }
    else {
      return paramUserInfo1.profileGroupId == paramUserInfo2.profileGroupId;
    }
    return false;
  }
  
  private boolean isSameProfileGroupLP(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    synchronized (this.mUsersLock)
    {
      UserInfo localUserInfo1 = getUserInfoLU(paramInt1);
      if (localUserInfo1 != null)
      {
        paramInt1 = localUserInfo1.profileGroupId;
        if (paramInt1 != 55536) {}
      }
      else
      {
        return false;
      }
      UserInfo localUserInfo2 = getUserInfoLU(paramInt2);
      if (localUserInfo2 != null)
      {
        paramInt1 = localUserInfo2.profileGroupId;
        if (paramInt1 != 55536) {}
      }
      else
      {
        return false;
      }
      paramInt1 = localUserInfo1.profileGroupId;
      paramInt2 = localUserInfo2.profileGroupId;
      if (paramInt1 == paramInt2) {
        bool = true;
      }
      return bool;
    }
  }
  
  private boolean isUserLimitReached()
  {
    synchronized (this.mUsersLock)
    {
      int i = getAliveUsersExcludingGuestsCountLU();
      if (i >= UserManager.getMaxSupportedUsers()) {
        return true;
      }
    }
    return false;
  }
  
  private void maybeInitializeDemoMode(int paramInt)
  {
    Object localObject;
    String str;
    if ((UserManager.isDeviceInDemoMode(this.mContext)) && (paramInt != 0))
    {
      localObject = this.mContext.getResources().getString(17039484);
      if (!TextUtils.isEmpty((CharSequence)localObject))
      {
        localObject = ComponentName.unflattenFromString((String)localObject);
        str = ((ComponentName)localObject).getPackageName();
      }
    }
    try
    {
      IPackageManager localIPackageManager = AppGlobals.getPackageManager();
      localIPackageManager.setComponentEnabledSetting((ComponentName)localObject, 1, 0, paramInt);
      localIPackageManager.setApplicationEnabledSetting(str, 1, 0, paramInt, null);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private String packageToRestrictionsFileName(String paramString)
  {
    return "res_" + paramString + ".xml";
  }
  
  private void propagateUserRestrictionsLR(final int paramInt, final Bundle paramBundle1, final Bundle paramBundle2)
  {
    if (UserRestrictionsUtils.areEqual(paramBundle1, paramBundle2)) {
      return;
    }
    paramBundle1 = new Bundle(paramBundle1);
    paramBundle2 = new Bundle(paramBundle2);
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        UserRestrictionsUtils.applyUserRestrictions(UserManagerService.-get2(UserManagerService.this), paramInt, paramBundle1, paramBundle2);
        synchronized (UserManagerService.-get8(UserManagerService.this))
        {
          UserManagerInternal.UserRestrictionsListener[] arrayOfUserRestrictionsListener = new UserManagerInternal.UserRestrictionsListener[UserManagerService.-get8(UserManagerService.this).size()];
          UserManagerService.-get8(UserManagerService.this).toArray(arrayOfUserRestrictionsListener);
          int i = 0;
          if (i < arrayOfUserRestrictionsListener.length)
          {
            arrayOfUserRestrictionsListener[i].onUserRestrictionsChanged(paramInt, paramBundle1, paramBundle2);
            i += 1;
          }
        }
      }
    });
  }
  
  /* Error */
  static Bundle readApplicationRestrictionsLP(AtomicFile paramAtomicFile)
  {
    // Byte code:
    //   0: new 399	android/os/Bundle
    //   3: dup
    //   4: invokespecial 400	android/os/Bundle:<init>	()V
    //   7: astore 4
    //   9: new 411	java/util/ArrayList
    //   12: dup
    //   13: invokespecial 412	java/util/ArrayList:<init>	()V
    //   16: astore 5
    //   18: aload_0
    //   19: invokevirtual 1101	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   22: invokevirtual 589	java/io/File:exists	()Z
    //   25: ifne +6 -> 31
    //   28: aload 4
    //   30: areturn
    //   31: aconst_null
    //   32: astore_2
    //   33: aconst_null
    //   34: astore_1
    //   35: aload_0
    //   36: invokevirtual 1105	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   39: astore_3
    //   40: aload_3
    //   41: astore_1
    //   42: aload_3
    //   43: astore_2
    //   44: invokestatic 1111	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   47: astore 6
    //   49: aload_3
    //   50: astore_1
    //   51: aload_3
    //   52: astore_2
    //   53: aload 6
    //   55: aload_3
    //   56: getstatic 1117	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   59: invokevirtual 1121	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   62: invokeinterface 1127 3 0
    //   67: aload_3
    //   68: astore_1
    //   69: aload_3
    //   70: astore_2
    //   71: aload 6
    //   73: invokestatic 1133	com/android/internal/util/XmlUtils:nextElement	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   76: aload_3
    //   77: astore_1
    //   78: aload_3
    //   79: astore_2
    //   80: aload 6
    //   82: invokeinterface 1136 1 0
    //   87: iconst_2
    //   88: if_icmpeq +43 -> 131
    //   91: aload_3
    //   92: astore_1
    //   93: aload_3
    //   94: astore_2
    //   95: ldc 122
    //   97: new 347	java/lang/StringBuilder
    //   100: dup
    //   101: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   104: ldc_w 1138
    //   107: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: aload_0
    //   111: invokevirtual 1101	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   114: invokevirtual 839	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   117: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   120: invokestatic 1139	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   123: pop
    //   124: aload_3
    //   125: invokestatic 1145	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   128: aload 4
    //   130: areturn
    //   131: aload_3
    //   132: astore_1
    //   133: aload_3
    //   134: astore_2
    //   135: aload 6
    //   137: invokeinterface 1148 1 0
    //   142: iconst_1
    //   143: if_icmpeq +59 -> 202
    //   146: aload_3
    //   147: astore_1
    //   148: aload_3
    //   149: astore_2
    //   150: aload 4
    //   152: aload 5
    //   154: aload 6
    //   156: invokestatic 1152	com/android/server/pm/UserManagerService:readEntry	(Landroid/os/Bundle;Ljava/util/ArrayList;Lorg/xmlpull/v1/XmlPullParser;)V
    //   159: goto -28 -> 131
    //   162: astore_3
    //   163: aload_1
    //   164: astore_2
    //   165: ldc 122
    //   167: new 347	java/lang/StringBuilder
    //   170: dup
    //   171: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   174: ldc_w 1154
    //   177: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: aload_0
    //   181: invokevirtual 1101	android/util/AtomicFile:getBaseFile	()Ljava/io/File;
    //   184: invokevirtual 839	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   187: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   190: aload_3
    //   191: invokestatic 1155	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   194: pop
    //   195: aload_1
    //   196: invokestatic 1145	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   199: aload 4
    //   201: areturn
    //   202: aload_3
    //   203: invokestatic 1145	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   206: aload 4
    //   208: areturn
    //   209: astore_0
    //   210: aload_2
    //   211: invokestatic 1145	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   214: aload_0
    //   215: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	216	0	paramAtomicFile	AtomicFile
    //   34	162	1	localObject1	Object
    //   32	179	2	localObject2	Object
    //   39	110	3	localFileInputStream	java.io.FileInputStream
    //   162	41	3	localIOException	IOException
    //   7	200	4	localBundle	Bundle
    //   16	137	5	localArrayList	ArrayList
    //   47	108	6	localXmlPullParser	XmlPullParser
    // Exception table:
    //   from	to	target	type
    //   35	40	162	java/io/IOException
    //   35	40	162	org/xmlpull/v1/XmlPullParserException
    //   44	49	162	java/io/IOException
    //   44	49	162	org/xmlpull/v1/XmlPullParserException
    //   53	67	162	java/io/IOException
    //   53	67	162	org/xmlpull/v1/XmlPullParserException
    //   71	76	162	java/io/IOException
    //   71	76	162	org/xmlpull/v1/XmlPullParserException
    //   80	91	162	java/io/IOException
    //   80	91	162	org/xmlpull/v1/XmlPullParserException
    //   95	124	162	java/io/IOException
    //   95	124	162	org/xmlpull/v1/XmlPullParserException
    //   135	146	162	java/io/IOException
    //   135	146	162	org/xmlpull/v1/XmlPullParserException
    //   150	159	162	java/io/IOException
    //   150	159	162	org/xmlpull/v1/XmlPullParserException
    //   35	40	209	finally
    //   44	49	209	finally
    //   53	67	209	finally
    //   71	76	209	finally
    //   80	91	209	finally
    //   95	124	209	finally
    //   135	146	209	finally
    //   150	159	209	finally
    //   165	195	209	finally
  }
  
  private Bundle readApplicationRestrictionsLP(String paramString, int paramInt)
  {
    return readApplicationRestrictionsLP(new AtomicFile(new File(Environment.getUserSystemDirectory(paramInt), packageToRestrictionsFileName(paramString))));
  }
  
  private static Bundle readBundleEntry(XmlPullParser paramXmlPullParser, ArrayList<String> paramArrayList)
    throws IOException, XmlPullParserException
  {
    Bundle localBundle = new Bundle();
    int i = paramXmlPullParser.getDepth();
    while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
      readEntry(localBundle, paramArrayList, paramXmlPullParser);
    }
    return localBundle;
  }
  
  private static void readEntry(Bundle paramBundle, ArrayList<String> paramArrayList, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1;
    Object localObject;
    int i;
    if ((paramXmlPullParser.getEventType() == 2) && (paramXmlPullParser.getName().equals("entry")))
    {
      str1 = paramXmlPullParser.getAttributeValue(null, "key");
      localObject = paramXmlPullParser.getAttributeValue(null, "type");
      String str2 = paramXmlPullParser.getAttributeValue(null, "m");
      if (str2 != null)
      {
        paramArrayList.clear();
        i = Integer.parseInt(str2);
        while (i > 0)
        {
          int j = paramXmlPullParser.next();
          if (j == 1) {
            break;
          }
          if ((j == 2) && (paramXmlPullParser.getName().equals("value")))
          {
            paramArrayList.add(paramXmlPullParser.nextText().trim());
            i -= 1;
          }
        }
        paramXmlPullParser = new String[paramArrayList.size()];
        paramArrayList.toArray(paramXmlPullParser);
        paramBundle.putStringArray(str1, paramXmlPullParser);
      }
    }
    else
    {
      return;
    }
    if ("B".equals(localObject))
    {
      paramBundle.putBundle(str1, readBundleEntry(paramXmlPullParser, paramArrayList));
      return;
    }
    if ("BA".equals(localObject))
    {
      i = paramXmlPullParser.getDepth();
      localObject = new ArrayList();
      while (XmlUtils.nextElementWithin(paramXmlPullParser, i)) {
        ((ArrayList)localObject).add(readBundleEntry(paramXmlPullParser, paramArrayList));
      }
      paramBundle.putParcelableArray(str1, (Parcelable[])((ArrayList)localObject).toArray(new Bundle[((ArrayList)localObject).size()]));
      return;
    }
    paramArrayList = paramXmlPullParser.nextText().trim();
    if ("b".equals(localObject))
    {
      paramBundle.putBoolean(str1, Boolean.parseBoolean(paramArrayList));
      return;
    }
    if ("i".equals(localObject))
    {
      paramBundle.putInt(str1, Integer.parseInt(paramArrayList));
      return;
    }
    paramBundle.putString(str1, paramArrayList);
  }
  
  private int readIntAttribute(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramInt;
    }
    try
    {
      int i = Integer.parseInt(paramXmlPullParser);
      return i;
    }
    catch (NumberFormatException paramXmlPullParser) {}
    return paramInt;
  }
  
  private long readLongAttribute(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    try
    {
      long l = Long.parseLong(paramXmlPullParser);
      return l;
    }
    catch (NumberFormatException paramXmlPullParser) {}
    return paramLong;
  }
  
  /* Error */
  private UserData readUserLP(int paramInt)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 7
    //   3: iload_1
    //   4: istore 6
    //   6: aconst_null
    //   7: astore 47
    //   9: aconst_null
    //   10: astore 31
    //   12: aconst_null
    //   13: astore 48
    //   15: aconst_null
    //   16: astore 32
    //   18: aconst_null
    //   19: astore 43
    //   21: lconst_0
    //   22: lstore 16
    //   24: lconst_0
    //   25: lstore 18
    //   27: aconst_null
    //   28: astore 44
    //   30: sipush 55536
    //   33: istore 8
    //   35: sipush 55536
    //   38: istore 9
    //   40: iconst_0
    //   41: istore 26
    //   43: iconst_0
    //   44: istore 20
    //   46: iconst_0
    //   47: istore 27
    //   49: iconst_0
    //   50: istore 21
    //   52: iconst_0
    //   53: istore 28
    //   55: iconst_0
    //   56: istore 22
    //   58: aconst_null
    //   59: astore 45
    //   61: aconst_null
    //   62: astore 46
    //   64: aconst_null
    //   65: astore 49
    //   67: aconst_null
    //   68: astore 30
    //   70: new 399	android/os/Bundle
    //   73: dup
    //   74: invokespecial 400	android/os/Bundle:<init>	()V
    //   77: astore 50
    //   79: new 399	android/os/Bundle
    //   82: dup
    //   83: invokespecial 400	android/os/Bundle:<init>	()V
    //   86: astore 51
    //   88: aconst_null
    //   89: astore 34
    //   91: aconst_null
    //   92: astore 35
    //   94: aconst_null
    //   95: astore 33
    //   97: new 1098	android/util/AtomicFile
    //   100: dup
    //   101: new 358	java/io/File
    //   104: dup
    //   105: aload_0
    //   106: getfield 431	com/android/server/pm/UserManagerService:mUsersDir	Ljava/io/File;
    //   109: new 347	java/lang/StringBuilder
    //   112: dup
    //   113: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   116: iload_1
    //   117: invokestatic 1232	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   120: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   123: ldc -65
    //   125: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   131: invokespecial 429	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   134: invokespecial 1158	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   137: invokevirtual 1105	android/util/AtomicFile:openRead	()Ljava/io/FileInputStream;
    //   140: astore 29
    //   142: aload 29
    //   144: astore 33
    //   146: aload 29
    //   148: astore 34
    //   150: aload 29
    //   152: astore 35
    //   154: invokestatic 1111	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   157: astore 52
    //   159: aload 29
    //   161: astore 33
    //   163: aload 29
    //   165: astore 34
    //   167: aload 29
    //   169: astore 35
    //   171: aload 52
    //   173: aload 29
    //   175: getstatic 1117	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   178: invokevirtual 1121	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   181: invokeinterface 1127 3 0
    //   186: aload 29
    //   188: astore 33
    //   190: aload 29
    //   192: astore 34
    //   194: aload 29
    //   196: astore 35
    //   198: aload 52
    //   200: invokeinterface 1148 1 0
    //   205: istore 10
    //   207: iload 10
    //   209: iconst_2
    //   210: if_icmpeq +9 -> 219
    //   213: iload 10
    //   215: iconst_1
    //   216: if_icmpne -30 -> 186
    //   219: iload 10
    //   221: iconst_2
    //   222: if_icmpeq +57 -> 279
    //   225: aload 29
    //   227: astore 33
    //   229: aload 29
    //   231: astore 34
    //   233: aload 29
    //   235: astore 35
    //   237: ldc 122
    //   239: new 347	java/lang/StringBuilder
    //   242: dup
    //   243: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   246: ldc_w 1234
    //   249: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: iload_1
    //   253: invokevirtual 540	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   256: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   259: invokestatic 1139	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   262: pop
    //   263: aload 29
    //   265: ifnull +8 -> 273
    //   268: aload 29
    //   270: invokevirtual 1239	java/io/FileInputStream:close	()V
    //   273: aconst_null
    //   274: areturn
    //   275: astore 29
    //   277: aconst_null
    //   278: areturn
    //   279: aload 48
    //   281: astore 36
    //   283: lload 16
    //   285: lstore 12
    //   287: iload 7
    //   289: istore_2
    //   290: iload 27
    //   292: istore 23
    //   294: aload 43
    //   296: astore 41
    //   298: aload 44
    //   300: astore 40
    //   302: lload 18
    //   304: lstore 14
    //   306: aload 47
    //   308: astore 42
    //   310: iload 26
    //   312: istore 24
    //   314: iload 28
    //   316: istore 25
    //   318: iload 8
    //   320: istore_3
    //   321: iload 9
    //   323: istore 4
    //   325: aload 45
    //   327: astore 37
    //   329: aload 49
    //   331: astore 38
    //   333: aload 46
    //   335: astore 39
    //   337: iload 6
    //   339: istore 5
    //   341: iload 10
    //   343: iconst_2
    //   344: if_icmpne +1015 -> 1359
    //   347: aload 29
    //   349: astore 33
    //   351: aload 29
    //   353: astore 34
    //   355: aload 48
    //   357: astore 36
    //   359: lload 16
    //   361: lstore 12
    //   363: iload 7
    //   365: istore_2
    //   366: iload 27
    //   368: istore 23
    //   370: aload 43
    //   372: astore 41
    //   374: aload 44
    //   376: astore 40
    //   378: lload 18
    //   380: lstore 14
    //   382: aload 47
    //   384: astore 42
    //   386: iload 26
    //   388: istore 24
    //   390: iload 28
    //   392: istore 25
    //   394: iload 8
    //   396: istore_3
    //   397: iload 9
    //   399: istore 4
    //   401: aload 45
    //   403: astore 37
    //   405: aload 49
    //   407: astore 38
    //   409: aload 46
    //   411: astore 39
    //   413: iload 6
    //   415: istore 5
    //   417: aload 29
    //   419: astore 35
    //   421: aload 52
    //   423: invokeinterface 1173 1 0
    //   428: ldc -98
    //   430: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   433: ifeq +926 -> 1359
    //   436: aload 29
    //   438: astore 33
    //   440: aload 29
    //   442: astore 34
    //   444: aload 29
    //   446: astore 35
    //   448: aload_0
    //   449: aload 52
    //   451: ldc 54
    //   453: iconst_m1
    //   454: invokespecial 1241	com/android/server/pm/UserManagerService:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   457: iload_1
    //   458: if_icmpeq +40 -> 498
    //   461: aload 29
    //   463: astore 33
    //   465: aload 29
    //   467: astore 34
    //   469: aload 29
    //   471: astore 35
    //   473: ldc 122
    //   475: ldc_w 1243
    //   478: invokestatic 1139	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   481: pop
    //   482: aload 29
    //   484: ifnull +8 -> 492
    //   487: aload 29
    //   489: invokevirtual 1239	java/io/FileInputStream:close	()V
    //   492: aconst_null
    //   493: areturn
    //   494: astore 29
    //   496: aconst_null
    //   497: areturn
    //   498: aload 29
    //   500: astore 33
    //   502: aload 29
    //   504: astore 34
    //   506: aload 29
    //   508: astore 35
    //   510: aload_0
    //   511: aload 52
    //   513: ldc 87
    //   515: iload_1
    //   516: invokespecial 1241	com/android/server/pm/UserManagerService:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   519: istore 6
    //   521: aload 29
    //   523: astore 33
    //   525: aload 29
    //   527: astore 34
    //   529: aload 29
    //   531: astore 35
    //   533: aload_0
    //   534: aload 52
    //   536: ldc 45
    //   538: iconst_0
    //   539: invokespecial 1241	com/android/server/pm/UserManagerService:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   542: istore 7
    //   544: aload 29
    //   546: astore 33
    //   548: aload 29
    //   550: astore 34
    //   552: aload 29
    //   554: astore 35
    //   556: aload 52
    //   558: aconst_null
    //   559: ldc 51
    //   561: invokeinterface 1176 3 0
    //   566: astore 43
    //   568: aload 29
    //   570: astore 33
    //   572: aload 29
    //   574: astore 34
    //   576: aload 29
    //   578: astore 35
    //   580: aload_0
    //   581: aload 52
    //   583: ldc 42
    //   585: lconst_0
    //   586: invokespecial 1245	com/android/server/pm/UserManagerService:readLongAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;J)J
    //   589: lstore 16
    //   591: aload 29
    //   593: astore 33
    //   595: aload 29
    //   597: astore 34
    //   599: aload 29
    //   601: astore 35
    //   603: aload_0
    //   604: aload 52
    //   606: ldc 63
    //   608: lconst_0
    //   609: invokespecial 1245	com/android/server/pm/UserManagerService:readLongAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;J)J
    //   612: lstore 18
    //   614: aload 29
    //   616: astore 33
    //   618: aload 29
    //   620: astore 34
    //   622: aload 29
    //   624: astore 35
    //   626: aload 52
    //   628: aconst_null
    //   629: ldc 60
    //   631: invokeinterface 1176 3 0
    //   636: astore 44
    //   638: aload 29
    //   640: astore 33
    //   642: aload 29
    //   644: astore 34
    //   646: aload 29
    //   648: astore 35
    //   650: aload_0
    //   651: aload 52
    //   653: ldc 75
    //   655: sipush 55536
    //   658: invokespecial 1241	com/android/server/pm/UserManagerService:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   661: istore 8
    //   663: aload 29
    //   665: astore 33
    //   667: aload 29
    //   669: astore 34
    //   671: aload 29
    //   673: astore 35
    //   675: aload_0
    //   676: aload 52
    //   678: ldc 78
    //   680: sipush 55536
    //   683: invokespecial 1241	com/android/server/pm/UserManagerService:readIntAttribute	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;I)I
    //   686: istore 9
    //   688: aload 29
    //   690: astore 33
    //   692: aload 29
    //   694: astore 34
    //   696: aload 29
    //   698: astore 35
    //   700: ldc_w 813
    //   703: aload 52
    //   705: aconst_null
    //   706: ldc 72
    //   708: invokeinterface 1176 3 0
    //   713: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   716: ifeq +6 -> 722
    //   719: iconst_1
    //   720: istore 20
    //   722: aload 29
    //   724: astore 33
    //   726: aload 29
    //   728: astore 34
    //   730: aload 29
    //   732: astore 35
    //   734: ldc_w 813
    //   737: aload 52
    //   739: aconst_null
    //   740: ldc 48
    //   742: invokeinterface 1176 3 0
    //   747: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   750: ifeq +6 -> 756
    //   753: iconst_1
    //   754: istore 21
    //   756: aload 29
    //   758: astore 33
    //   760: aload 29
    //   762: astore 34
    //   764: aload 29
    //   766: astore 35
    //   768: aload 52
    //   770: aconst_null
    //   771: ldc 81
    //   773: invokeinterface 1176 3 0
    //   778: astore 45
    //   780: aload 29
    //   782: astore 33
    //   784: aload 29
    //   786: astore 34
    //   788: aload 29
    //   790: astore 35
    //   792: aload 52
    //   794: aconst_null
    //   795: ldc 84
    //   797: invokeinterface 1176 3 0
    //   802: astore 46
    //   804: aload 45
    //   806: ifnonnull +1010 -> 1816
    //   809: aload 46
    //   811: ifnull +6 -> 817
    //   814: goto +1002 -> 1816
    //   817: aload 29
    //   819: astore 33
    //   821: aload 29
    //   823: astore 34
    //   825: aload 29
    //   827: astore 35
    //   829: aload 52
    //   831: invokeinterface 1165 1 0
    //   836: istore 10
    //   838: aload 29
    //   840: astore 33
    //   842: aload 29
    //   844: astore 34
    //   846: aload 29
    //   848: astore 35
    //   850: aload 52
    //   852: invokeinterface 1148 1 0
    //   857: istore 11
    //   859: aload 32
    //   861: astore 36
    //   863: lload 16
    //   865: lstore 12
    //   867: iload 7
    //   869: istore_2
    //   870: iload 21
    //   872: istore 23
    //   874: aload 43
    //   876: astore 41
    //   878: aload 44
    //   880: astore 40
    //   882: lload 18
    //   884: lstore 14
    //   886: aload 31
    //   888: astore 42
    //   890: iload 20
    //   892: istore 24
    //   894: iload 22
    //   896: istore 25
    //   898: iload 8
    //   900: istore_3
    //   901: iload 9
    //   903: istore 4
    //   905: aload 45
    //   907: astore 37
    //   909: aload 30
    //   911: astore 38
    //   913: aload 46
    //   915: astore 39
    //   917: iload 6
    //   919: istore 5
    //   921: iload 11
    //   923: iconst_1
    //   924: if_icmpeq +435 -> 1359
    //   927: iload 11
    //   929: iconst_3
    //   930: if_icmpne +89 -> 1019
    //   933: aload 29
    //   935: astore 33
    //   937: aload 29
    //   939: astore 34
    //   941: aload 32
    //   943: astore 36
    //   945: lload 16
    //   947: lstore 12
    //   949: iload 7
    //   951: istore_2
    //   952: iload 21
    //   954: istore 23
    //   956: aload 43
    //   958: astore 41
    //   960: aload 44
    //   962: astore 40
    //   964: lload 18
    //   966: lstore 14
    //   968: aload 31
    //   970: astore 42
    //   972: iload 20
    //   974: istore 24
    //   976: iload 22
    //   978: istore 25
    //   980: iload 8
    //   982: istore_3
    //   983: iload 9
    //   985: istore 4
    //   987: aload 45
    //   989: astore 37
    //   991: aload 30
    //   993: astore 38
    //   995: aload 46
    //   997: astore 39
    //   999: iload 6
    //   1001: istore 5
    //   1003: aload 29
    //   1005: astore 35
    //   1007: aload 52
    //   1009: invokeinterface 1165 1 0
    //   1014: iload 10
    //   1016: if_icmple +343 -> 1359
    //   1019: iload 11
    //   1021: iconst_3
    //   1022: if_icmpeq -184 -> 838
    //   1025: iload 11
    //   1027: iconst_4
    //   1028: if_icmpeq -190 -> 838
    //   1031: aload 29
    //   1033: astore 33
    //   1035: aload 29
    //   1037: astore 34
    //   1039: aload 29
    //   1041: astore 35
    //   1043: aload 52
    //   1045: invokeinterface 1173 1 0
    //   1050: astore 36
    //   1052: aload 29
    //   1054: astore 33
    //   1056: aload 29
    //   1058: astore 34
    //   1060: aload 29
    //   1062: astore 35
    //   1064: ldc -107
    //   1066: aload 36
    //   1068: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1071: ifeq +50 -> 1121
    //   1074: aload 29
    //   1076: astore 33
    //   1078: aload 29
    //   1080: astore 34
    //   1082: aload 29
    //   1084: astore 35
    //   1086: aload 52
    //   1088: invokeinterface 1148 1 0
    //   1093: iconst_4
    //   1094: if_icmpne -256 -> 838
    //   1097: aload 29
    //   1099: astore 33
    //   1101: aload 29
    //   1103: astore 34
    //   1105: aload 29
    //   1107: astore 35
    //   1109: aload 52
    //   1111: invokeinterface 1248 1 0
    //   1116: astore 31
    //   1118: goto -280 -> 838
    //   1121: aload 29
    //   1123: astore 33
    //   1125: aload 29
    //   1127: astore 34
    //   1129: aload 29
    //   1131: astore 35
    //   1133: ldc -104
    //   1135: aload 36
    //   1137: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1140: ifeq +39 -> 1179
    //   1143: aload 29
    //   1145: astore 33
    //   1147: aload 29
    //   1149: astore 34
    //   1151: aload 29
    //   1153: astore 35
    //   1155: aload 52
    //   1157: aload 50
    //   1159: invokestatic 1252	com/android/server/pm/UserRestrictionsUtils:readRestrictions	(Lorg/xmlpull/v1/XmlPullParser;Landroid/os/Bundle;)V
    //   1162: goto -324 -> 838
    //   1165: astore 29
    //   1167: aload 33
    //   1169: ifnull +8 -> 1177
    //   1172: aload 33
    //   1174: invokevirtual 1239	java/io/FileInputStream:close	()V
    //   1177: aconst_null
    //   1178: areturn
    //   1179: aload 29
    //   1181: astore 33
    //   1183: aload 29
    //   1185: astore 34
    //   1187: aload 29
    //   1189: astore 35
    //   1191: ldc -119
    //   1193: aload 36
    //   1195: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1198: ifeq +45 -> 1243
    //   1201: aload 29
    //   1203: astore 33
    //   1205: aload 29
    //   1207: astore 34
    //   1209: aload 29
    //   1211: astore 35
    //   1213: aload 52
    //   1215: aload 51
    //   1217: invokestatic 1252	com/android/server/pm/UserRestrictionsUtils:readRestrictions	(Lorg/xmlpull/v1/XmlPullParser;Landroid/os/Bundle;)V
    //   1220: goto -382 -> 838
    //   1223: astore 29
    //   1225: aload 34
    //   1227: ifnull -50 -> 1177
    //   1230: aload 34
    //   1232: invokevirtual 1239	java/io/FileInputStream:close	()V
    //   1235: goto -58 -> 1177
    //   1238: astore 29
    //   1240: goto -63 -> 1177
    //   1243: aload 29
    //   1245: astore 33
    //   1247: aload 29
    //   1249: astore 34
    //   1251: aload 29
    //   1253: astore 35
    //   1255: ldc -122
    //   1257: aload 36
    //   1259: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1262: ifeq +50 -> 1312
    //   1265: aload 29
    //   1267: astore 33
    //   1269: aload 29
    //   1271: astore 34
    //   1273: aload 29
    //   1275: astore 35
    //   1277: aload 52
    //   1279: invokeinterface 1148 1 0
    //   1284: iconst_4
    //   1285: if_icmpne -447 -> 838
    //   1288: aload 29
    //   1290: astore 33
    //   1292: aload 29
    //   1294: astore 34
    //   1296: aload 29
    //   1298: astore 35
    //   1300: aload 52
    //   1302: invokeinterface 1248 1 0
    //   1307: astore 32
    //   1309: goto -471 -> 838
    //   1312: aload 29
    //   1314: astore 33
    //   1316: aload 29
    //   1318: astore 34
    //   1320: aload 29
    //   1322: astore 35
    //   1324: ldc -101
    //   1326: aload 36
    //   1328: invokevirtual 826	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1331: ifeq -493 -> 838
    //   1334: aload 29
    //   1336: astore 33
    //   1338: aload 29
    //   1340: astore 34
    //   1342: aload 29
    //   1344: astore 35
    //   1346: aload 52
    //   1348: invokestatic 1258	android/os/PersistableBundle:restoreFromXml	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/os/PersistableBundle;
    //   1351: astore 30
    //   1353: iconst_1
    //   1354: istore 22
    //   1356: goto -518 -> 838
    //   1359: aload 29
    //   1361: astore 33
    //   1363: aload 29
    //   1365: astore 34
    //   1367: aload 29
    //   1369: astore 35
    //   1371: new 681	android/content/pm/UserInfo
    //   1374: dup
    //   1375: iload_1
    //   1376: aload 42
    //   1378: aload 41
    //   1380: iload_2
    //   1381: invokespecial 708	android/content/pm/UserInfo:<init>	(ILjava/lang/String;Ljava/lang/String;I)V
    //   1384: astore 31
    //   1386: aload 29
    //   1388: astore 33
    //   1390: aload 29
    //   1392: astore 34
    //   1394: aload 29
    //   1396: astore 35
    //   1398: aload 31
    //   1400: iload 5
    //   1402: putfield 712	android/content/pm/UserInfo:serialNumber	I
    //   1405: aload 29
    //   1407: astore 33
    //   1409: aload 29
    //   1411: astore 34
    //   1413: aload 29
    //   1415: astore 35
    //   1417: aload 31
    //   1419: lload 12
    //   1421: putfield 720	android/content/pm/UserInfo:creationTime	J
    //   1424: aload 29
    //   1426: astore 33
    //   1428: aload 29
    //   1430: astore 34
    //   1432: aload 29
    //   1434: astore 35
    //   1436: aload 31
    //   1438: lload 14
    //   1440: putfield 1261	android/content/pm/UserInfo:lastLoggedInTime	J
    //   1443: aload 29
    //   1445: astore 33
    //   1447: aload 29
    //   1449: astore 34
    //   1451: aload 29
    //   1453: astore 35
    //   1455: aload 31
    //   1457: aload 40
    //   1459: putfield 729	android/content/pm/UserInfo:lastLoggedInFingerprint	Ljava/lang/String;
    //   1462: aload 29
    //   1464: astore 33
    //   1466: aload 29
    //   1468: astore 34
    //   1470: aload 29
    //   1472: astore 35
    //   1474: aload 31
    //   1476: iload 24
    //   1478: putfield 722	android/content/pm/UserInfo:partial	Z
    //   1481: aload 29
    //   1483: astore 33
    //   1485: aload 29
    //   1487: astore 34
    //   1489: aload 29
    //   1491: astore 35
    //   1493: aload 31
    //   1495: iload 23
    //   1497: putfield 897	android/content/pm/UserInfo:guestToRemove	Z
    //   1500: aload 29
    //   1502: astore 33
    //   1504: aload 29
    //   1506: astore 34
    //   1508: aload 29
    //   1510: astore 35
    //   1512: aload 31
    //   1514: iload_3
    //   1515: putfield 739	android/content/pm/UserInfo:profileGroupId	I
    //   1518: aload 29
    //   1520: astore 33
    //   1522: aload 29
    //   1524: astore 34
    //   1526: aload 29
    //   1528: astore 35
    //   1530: aload 31
    //   1532: iload 4
    //   1534: putfield 793	android/content/pm/UserInfo:restrictedProfileParentId	I
    //   1537: aload 29
    //   1539: astore 33
    //   1541: aload 29
    //   1543: astore 34
    //   1545: aload 29
    //   1547: astore 35
    //   1549: new 34	com/android/server/pm/UserManagerService$UserData
    //   1552: dup
    //   1553: aconst_null
    //   1554: invokespecial 731	com/android/server/pm/UserManagerService$UserData:<init>	(Lcom/android/server/pm/UserManagerService$UserData;)V
    //   1557: astore 30
    //   1559: aload 29
    //   1561: astore 33
    //   1563: aload 29
    //   1565: astore 34
    //   1567: aload 29
    //   1569: astore 35
    //   1571: aload 30
    //   1573: aload 31
    //   1575: putfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   1578: aload 29
    //   1580: astore 33
    //   1582: aload 29
    //   1584: astore 34
    //   1586: aload 29
    //   1588: astore 35
    //   1590: aload 30
    //   1592: aload 36
    //   1594: putfield 1263	com/android/server/pm/UserManagerService$UserData:account	Ljava/lang/String;
    //   1597: aload 29
    //   1599: astore 33
    //   1601: aload 29
    //   1603: astore 34
    //   1605: aload 29
    //   1607: astore 35
    //   1609: aload 30
    //   1611: aload 37
    //   1613: putfield 1265	com/android/server/pm/UserManagerService$UserData:seedAccountName	Ljava/lang/String;
    //   1616: aload 29
    //   1618: astore 33
    //   1620: aload 29
    //   1622: astore 34
    //   1624: aload 29
    //   1626: astore 35
    //   1628: aload 30
    //   1630: aload 39
    //   1632: putfield 1267	com/android/server/pm/UserManagerService$UserData:seedAccountType	Ljava/lang/String;
    //   1635: aload 29
    //   1637: astore 33
    //   1639: aload 29
    //   1641: astore 34
    //   1643: aload 29
    //   1645: astore 35
    //   1647: aload 30
    //   1649: iload 25
    //   1651: putfield 1270	com/android/server/pm/UserManagerService$UserData:persistSeedData	Z
    //   1654: aload 29
    //   1656: astore 33
    //   1658: aload 29
    //   1660: astore 34
    //   1662: aload 29
    //   1664: astore 35
    //   1666: aload 30
    //   1668: aload 38
    //   1670: putfield 1273	com/android/server/pm/UserManagerService$UserData:seedAccountOptions	Landroid/os/PersistableBundle;
    //   1673: aload 29
    //   1675: astore 33
    //   1677: aload 29
    //   1679: astore 34
    //   1681: aload 29
    //   1683: astore 35
    //   1685: aload_0
    //   1686: getfield 275	com/android/server/pm/UserManagerService:mRestrictionsLock	Ljava/lang/Object;
    //   1689: astore 31
    //   1691: aload 29
    //   1693: astore 33
    //   1695: aload 29
    //   1697: astore 34
    //   1699: aload 29
    //   1701: astore 35
    //   1703: aload 31
    //   1705: monitorenter
    //   1706: aload_0
    //   1707: getfield 253	com/android/server/pm/UserManagerService:mBaseUserRestrictions	Landroid/util/SparseArray;
    //   1710: iload_1
    //   1711: aload 50
    //   1713: invokevirtual 734	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   1716: aload_0
    //   1717: getfield 397	com/android/server/pm/UserManagerService:mDevicePolicyLocalUserRestrictions	Landroid/util/SparseArray;
    //   1720: iload_1
    //   1721: aload 51
    //   1723: invokevirtual 734	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   1726: aload 29
    //   1728: astore 33
    //   1730: aload 29
    //   1732: astore 34
    //   1734: aload 29
    //   1736: astore 35
    //   1738: aload 31
    //   1740: monitorexit
    //   1741: aload 29
    //   1743: ifnull +8 -> 1751
    //   1746: aload 29
    //   1748: invokevirtual 1239	java/io/FileInputStream:close	()V
    //   1751: aload 30
    //   1753: areturn
    //   1754: astore 30
    //   1756: aload 29
    //   1758: astore 33
    //   1760: aload 29
    //   1762: astore 34
    //   1764: aload 29
    //   1766: astore 35
    //   1768: aload 31
    //   1770: monitorexit
    //   1771: aload 29
    //   1773: astore 33
    //   1775: aload 29
    //   1777: astore 34
    //   1779: aload 29
    //   1781: astore 35
    //   1783: aload 30
    //   1785: athrow
    //   1786: astore 29
    //   1788: aload 35
    //   1790: ifnull +8 -> 1798
    //   1793: aload 35
    //   1795: invokevirtual 1239	java/io/FileInputStream:close	()V
    //   1798: aload 29
    //   1800: athrow
    //   1801: astore 29
    //   1803: aload 30
    //   1805: areturn
    //   1806: astore 29
    //   1808: goto -631 -> 1177
    //   1811: astore 30
    //   1813: goto -15 -> 1798
    //   1816: iconst_1
    //   1817: istore 22
    //   1819: goto -1002 -> 817
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1822	0	this	UserManagerService
    //   0	1822	1	paramInt	int
    //   289	1092	2	i	int
    //   320	1195	3	j	int
    //   323	1210	4	k	int
    //   339	1062	5	m	int
    //   4	996	6	n	int
    //   1	949	7	i1	int
    //   33	948	8	i2	int
    //   38	946	9	i3	int
    //   205	812	10	i4	int
    //   857	172	11	i5	int
    //   285	1135	12	l1	long
    //   304	1135	14	l2	long
    //   22	924	16	l3	long
    //   25	940	18	l4	long
    //   44	929	20	bool1	boolean
    //   50	903	21	bool2	boolean
    //   56	1762	22	bool3	boolean
    //   292	1204	23	bool4	boolean
    //   312	1165	24	bool5	boolean
    //   316	1334	25	bool6	boolean
    //   41	346	26	bool7	boolean
    //   47	320	27	bool8	boolean
    //   53	338	28	bool9	boolean
    //   140	129	29	localFileInputStream	java.io.FileInputStream
    //   275	213	29	localIOException1	IOException
    //   494	658	29	localIOException2	IOException
    //   1165	45	29	localIOException3	IOException
    //   1223	1	29	localXmlPullParserException	XmlPullParserException
    //   1238	542	29	localIOException4	IOException
    //   1786	13	29	localObject1	Object
    //   1801	1	29	localIOException5	IOException
    //   1806	1	29	localIOException6	IOException
    //   68	1684	30	localObject2	Object
    //   1754	50	30	localUserData	UserData
    //   1811	1	30	localIOException7	IOException
    //   10	1759	31	localObject3	Object
    //   16	1292	32	str1	String
    //   95	1679	33	localObject4	Object
    //   89	1689	34	localObject5	Object
    //   92	1702	35	localObject6	Object
    //   281	1312	36	localObject7	Object
    //   327	1285	37	str2	String
    //   331	1338	38	localObject8	Object
    //   335	1296	39	str3	String
    //   300	1158	40	str4	String
    //   296	1083	41	str5	String
    //   308	1069	42	localObject9	Object
    //   19	938	43	str6	String
    //   28	933	44	str7	String
    //   59	929	45	str8	String
    //   62	934	46	str9	String
    //   7	376	47	localObject10	Object
    //   13	343	48	localObject11	Object
    //   65	341	49	localObject12	Object
    //   77	1635	50	localBundle1	Bundle
    //   86	1636	51	localBundle2	Bundle
    //   157	1190	52	localXmlPullParser	XmlPullParser
    // Exception table:
    //   from	to	target	type
    //   268	273	275	java/io/IOException
    //   487	492	494	java/io/IOException
    //   97	142	1165	java/io/IOException
    //   154	159	1165	java/io/IOException
    //   171	186	1165	java/io/IOException
    //   198	207	1165	java/io/IOException
    //   237	263	1165	java/io/IOException
    //   421	436	1165	java/io/IOException
    //   448	461	1165	java/io/IOException
    //   473	482	1165	java/io/IOException
    //   510	521	1165	java/io/IOException
    //   533	544	1165	java/io/IOException
    //   556	568	1165	java/io/IOException
    //   580	591	1165	java/io/IOException
    //   603	614	1165	java/io/IOException
    //   626	638	1165	java/io/IOException
    //   650	663	1165	java/io/IOException
    //   675	688	1165	java/io/IOException
    //   700	719	1165	java/io/IOException
    //   734	753	1165	java/io/IOException
    //   768	780	1165	java/io/IOException
    //   792	804	1165	java/io/IOException
    //   829	838	1165	java/io/IOException
    //   850	859	1165	java/io/IOException
    //   1007	1019	1165	java/io/IOException
    //   1043	1052	1165	java/io/IOException
    //   1064	1074	1165	java/io/IOException
    //   1086	1097	1165	java/io/IOException
    //   1109	1118	1165	java/io/IOException
    //   1133	1143	1165	java/io/IOException
    //   1155	1162	1165	java/io/IOException
    //   1191	1201	1165	java/io/IOException
    //   1213	1220	1165	java/io/IOException
    //   1255	1265	1165	java/io/IOException
    //   1277	1288	1165	java/io/IOException
    //   1300	1309	1165	java/io/IOException
    //   1324	1334	1165	java/io/IOException
    //   1346	1353	1165	java/io/IOException
    //   1371	1386	1165	java/io/IOException
    //   1398	1405	1165	java/io/IOException
    //   1417	1424	1165	java/io/IOException
    //   1436	1443	1165	java/io/IOException
    //   1455	1462	1165	java/io/IOException
    //   1474	1481	1165	java/io/IOException
    //   1493	1500	1165	java/io/IOException
    //   1512	1518	1165	java/io/IOException
    //   1530	1537	1165	java/io/IOException
    //   1549	1559	1165	java/io/IOException
    //   1571	1578	1165	java/io/IOException
    //   1590	1597	1165	java/io/IOException
    //   1609	1616	1165	java/io/IOException
    //   1628	1635	1165	java/io/IOException
    //   1647	1654	1165	java/io/IOException
    //   1666	1673	1165	java/io/IOException
    //   1685	1691	1165	java/io/IOException
    //   1703	1706	1165	java/io/IOException
    //   1738	1741	1165	java/io/IOException
    //   1768	1771	1165	java/io/IOException
    //   1783	1786	1165	java/io/IOException
    //   97	142	1223	org/xmlpull/v1/XmlPullParserException
    //   154	159	1223	org/xmlpull/v1/XmlPullParserException
    //   171	186	1223	org/xmlpull/v1/XmlPullParserException
    //   198	207	1223	org/xmlpull/v1/XmlPullParserException
    //   237	263	1223	org/xmlpull/v1/XmlPullParserException
    //   421	436	1223	org/xmlpull/v1/XmlPullParserException
    //   448	461	1223	org/xmlpull/v1/XmlPullParserException
    //   473	482	1223	org/xmlpull/v1/XmlPullParserException
    //   510	521	1223	org/xmlpull/v1/XmlPullParserException
    //   533	544	1223	org/xmlpull/v1/XmlPullParserException
    //   556	568	1223	org/xmlpull/v1/XmlPullParserException
    //   580	591	1223	org/xmlpull/v1/XmlPullParserException
    //   603	614	1223	org/xmlpull/v1/XmlPullParserException
    //   626	638	1223	org/xmlpull/v1/XmlPullParserException
    //   650	663	1223	org/xmlpull/v1/XmlPullParserException
    //   675	688	1223	org/xmlpull/v1/XmlPullParserException
    //   700	719	1223	org/xmlpull/v1/XmlPullParserException
    //   734	753	1223	org/xmlpull/v1/XmlPullParserException
    //   768	780	1223	org/xmlpull/v1/XmlPullParserException
    //   792	804	1223	org/xmlpull/v1/XmlPullParserException
    //   829	838	1223	org/xmlpull/v1/XmlPullParserException
    //   850	859	1223	org/xmlpull/v1/XmlPullParserException
    //   1007	1019	1223	org/xmlpull/v1/XmlPullParserException
    //   1043	1052	1223	org/xmlpull/v1/XmlPullParserException
    //   1064	1074	1223	org/xmlpull/v1/XmlPullParserException
    //   1086	1097	1223	org/xmlpull/v1/XmlPullParserException
    //   1109	1118	1223	org/xmlpull/v1/XmlPullParserException
    //   1133	1143	1223	org/xmlpull/v1/XmlPullParserException
    //   1155	1162	1223	org/xmlpull/v1/XmlPullParserException
    //   1191	1201	1223	org/xmlpull/v1/XmlPullParserException
    //   1213	1220	1223	org/xmlpull/v1/XmlPullParserException
    //   1255	1265	1223	org/xmlpull/v1/XmlPullParserException
    //   1277	1288	1223	org/xmlpull/v1/XmlPullParserException
    //   1300	1309	1223	org/xmlpull/v1/XmlPullParserException
    //   1324	1334	1223	org/xmlpull/v1/XmlPullParserException
    //   1346	1353	1223	org/xmlpull/v1/XmlPullParserException
    //   1371	1386	1223	org/xmlpull/v1/XmlPullParserException
    //   1398	1405	1223	org/xmlpull/v1/XmlPullParserException
    //   1417	1424	1223	org/xmlpull/v1/XmlPullParserException
    //   1436	1443	1223	org/xmlpull/v1/XmlPullParserException
    //   1455	1462	1223	org/xmlpull/v1/XmlPullParserException
    //   1474	1481	1223	org/xmlpull/v1/XmlPullParserException
    //   1493	1500	1223	org/xmlpull/v1/XmlPullParserException
    //   1512	1518	1223	org/xmlpull/v1/XmlPullParserException
    //   1530	1537	1223	org/xmlpull/v1/XmlPullParserException
    //   1549	1559	1223	org/xmlpull/v1/XmlPullParserException
    //   1571	1578	1223	org/xmlpull/v1/XmlPullParserException
    //   1590	1597	1223	org/xmlpull/v1/XmlPullParserException
    //   1609	1616	1223	org/xmlpull/v1/XmlPullParserException
    //   1628	1635	1223	org/xmlpull/v1/XmlPullParserException
    //   1647	1654	1223	org/xmlpull/v1/XmlPullParserException
    //   1666	1673	1223	org/xmlpull/v1/XmlPullParserException
    //   1685	1691	1223	org/xmlpull/v1/XmlPullParserException
    //   1703	1706	1223	org/xmlpull/v1/XmlPullParserException
    //   1738	1741	1223	org/xmlpull/v1/XmlPullParserException
    //   1768	1771	1223	org/xmlpull/v1/XmlPullParserException
    //   1783	1786	1223	org/xmlpull/v1/XmlPullParserException
    //   1230	1235	1238	java/io/IOException
    //   1706	1726	1754	finally
    //   97	142	1786	finally
    //   154	159	1786	finally
    //   171	186	1786	finally
    //   198	207	1786	finally
    //   237	263	1786	finally
    //   421	436	1786	finally
    //   448	461	1786	finally
    //   473	482	1786	finally
    //   510	521	1786	finally
    //   533	544	1786	finally
    //   556	568	1786	finally
    //   580	591	1786	finally
    //   603	614	1786	finally
    //   626	638	1786	finally
    //   650	663	1786	finally
    //   675	688	1786	finally
    //   700	719	1786	finally
    //   734	753	1786	finally
    //   768	780	1786	finally
    //   792	804	1786	finally
    //   829	838	1786	finally
    //   850	859	1786	finally
    //   1007	1019	1786	finally
    //   1043	1052	1786	finally
    //   1064	1074	1786	finally
    //   1086	1097	1786	finally
    //   1109	1118	1786	finally
    //   1133	1143	1786	finally
    //   1155	1162	1786	finally
    //   1191	1201	1786	finally
    //   1213	1220	1786	finally
    //   1255	1265	1786	finally
    //   1277	1288	1786	finally
    //   1300	1309	1786	finally
    //   1324	1334	1786	finally
    //   1346	1353	1786	finally
    //   1371	1386	1786	finally
    //   1398	1405	1786	finally
    //   1417	1424	1786	finally
    //   1436	1443	1786	finally
    //   1455	1462	1786	finally
    //   1474	1481	1786	finally
    //   1493	1500	1786	finally
    //   1512	1518	1786	finally
    //   1530	1537	1786	finally
    //   1549	1559	1786	finally
    //   1571	1578	1786	finally
    //   1590	1597	1786	finally
    //   1609	1616	1786	finally
    //   1628	1635	1786	finally
    //   1647	1654	1786	finally
    //   1666	1673	1786	finally
    //   1685	1691	1786	finally
    //   1703	1706	1786	finally
    //   1738	1741	1786	finally
    //   1768	1771	1786	finally
    //   1783	1786	1786	finally
    //   1746	1751	1801	java/io/IOException
    //   1172	1177	1806	java/io/IOException
    //   1793	1798	1811	java/io/IOException
  }
  
  private void readUserListLP()
  {
    if (!this.mUserListFile.exists())
    {
      fallbackToSingleUserLP();
      return;
    }
    Object localObject4 = null;
    Object localObject1 = null;
    Object localObject6 = new AtomicFile(this.mUserListFile);
    Object localObject8;
    for (;;)
    {
      try
      {
        localObject6 = ((AtomicFile)localObject6).openRead();
        localObject1 = localObject6;
        localObject4 = localObject6;
        localXmlPullParser1 = Xml.newPullParser();
        localObject1 = localObject6;
        localObject4 = localObject6;
        localXmlPullParser1.setInput((InputStream)localObject6, StandardCharsets.UTF_8.name());
        localObject1 = localObject6;
        localObject4 = localObject6;
        i = localXmlPullParser1.next();
        if ((i != 2) && (i != 1)) {
          continue;
        }
        if (i != 2)
        {
          localObject1 = localObject6;
          localObject4 = localObject6;
          Slog.e("UserManagerService", "Unable to read user list");
          localObject1 = localObject6;
          localObject4 = localObject6;
          fallbackToSingleUserLP();
          return;
        }
        localObject1 = localObject6;
        localObject4 = localObject6;
        this.mNextSerialNumber = -1;
        localObject1 = localObject6;
        localObject4 = localObject6;
        if (localXmlPullParser1.getName().equals("users"))
        {
          localObject1 = localObject6;
          localObject4 = localObject6;
          localObject8 = localXmlPullParser1.getAttributeValue(null, "nextSerialNumber");
          if (localObject8 != null)
          {
            localObject1 = localObject6;
            localObject4 = localObject6;
            this.mNextSerialNumber = Integer.parseInt((String)localObject8);
          }
          localObject1 = localObject6;
          localObject4 = localObject6;
          localObject8 = localXmlPullParser1.getAttributeValue(null, "version");
          if (localObject8 != null)
          {
            localObject1 = localObject6;
            localObject4 = localObject6;
            this.mUserVersion = Integer.parseInt((String)localObject8);
          }
        }
        localObject1 = localObject6;
        localObject4 = localObject6;
        localObject8 = new Bundle();
      }
      catch (IOException|XmlPullParserException localIOException)
      {
        XmlPullParser localXmlPullParser1;
        UserData localUserData;
        localIOException = localIOException;
        localObject5 = localObject1;
        fallbackToSingleUserLP();
        return;
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject5);
      }
      localObject1 = localObject6;
      localObject4 = localObject6;
      int i = localXmlPullParser1.next();
      if (i == 1) {
        break;
      }
      if (i == 2)
      {
        localObject1 = localObject6;
        localObject4 = localObject6;
        Object localObject10 = localXmlPullParser1.getName();
        localObject1 = localObject6;
        localObject4 = localObject6;
        if (((String)localObject10).equals("user"))
        {
          localObject1 = localObject6;
          localObject4 = localObject6;
          localUserData = readUserLP(Integer.parseInt(localXmlPullParser1.getAttributeValue(null, "id")));
          if (localUserData != null)
          {
            localObject1 = localObject6;
            localObject4 = localObject6;
            localObject10 = this.mUsersLock;
            localObject1 = localObject6;
            localObject4 = localObject6;
          }
        }
        else
        {
          localObject3 = localObject6;
          localObject5 = localObject6;
          if (((String)localObject10).equals("guestRestrictions"))
          {
            do
            {
              localObject3 = localObject6;
              localObject5 = localObject6;
              i = localXmlPullParser2.next();
              if ((i == 1) || (i == 3)) {
                break;
              }
            } while (i != 2);
            localObject3 = localObject6;
            localObject5 = localObject6;
            if (localXmlPullParser2.getName().equals("restrictions"))
            {
              localObject3 = localObject6;
              localObject5 = localObject6;
              localObject10 = this.mGuestRestrictions;
              localObject3 = localObject6;
              localObject5 = localObject6;
              try
              {
                UserRestrictionsUtils.readRestrictions(localXmlPullParser2, this.mGuestRestrictions);
                localObject3 = localObject6;
                localObject5 = localObject6;
                continue;
              }
              finally
              {
                localXmlPullParser3 = finally;
                localObject3 = localObject6;
                localObject5 = localObject6;
                localObject3 = localObject6;
                localObject5 = localObject6;
                throw localXmlPullParser3;
              }
            }
            else
            {
              localObject3 = localObject6;
              localObject5 = localObject6;
              if (localXmlPullParser3.getName().equals("device_policy_restrictions"))
              {
                localObject3 = localObject6;
                localObject5 = localObject6;
                UserRestrictionsUtils.readRestrictions(localXmlPullParser3, (Bundle)localObject8);
              }
            }
          }
          else
          {
            localObject3 = localObject6;
            localObject5 = localObject6;
            if (((String)localObject10).equals("globalRestrictionOwnerUserId"))
            {
              localObject3 = localObject6;
              localObject5 = localObject6;
              localObject10 = localXmlPullParser3.getAttributeValue(null, "id");
              if (localObject10 != null)
              {
                localObject3 = localObject6;
                localObject5 = localObject6;
                this.mGlobalRestrictionOwnerUserId = Integer.parseInt((String)localObject10);
              }
            }
          }
        }
      }
    }
    Object localObject3 = localObject6;
    Object localObject5 = localObject6;
    Object localObject7 = this.mRestrictionsLock;
    localObject3 = localObject6;
    localObject5 = localObject6;
    try
    {
      this.mDevicePolicyGlobalUserRestrictions = ((Bundle)localObject8);
      localObject3 = localObject6;
      localObject5 = localObject6;
      localObject3 = localObject6;
      localObject5 = localObject6;
      updateUserIds();
      localObject3 = localObject6;
      localObject5 = localObject6;
      upgradeIfNecessaryLP();
      IoUtils.closeQuietly((AutoCloseable)localObject6);
      return;
    }
    finally
    {
      localObject3 = localObject6;
      localObject5 = localObject6;
      localObject3 = localObject6;
      localObject5 = localObject6;
    }
  }
  
  private void removeNonSystemUsers()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this.mUsersLock)
    {
      int j = this.mUsers.size();
      int i = 0;
      while (i < j)
      {
        UserInfo localUserInfo = ((UserData)this.mUsers.valueAt(i)).info;
        if (localUserInfo.id != 0) {
          localArrayList.add(localUserInfo);
        }
        i += 1;
      }
      ??? = localArrayList.iterator();
      if (((Iterator)???).hasNext()) {
        removeUser(((UserInfo)((Iterator)???).next()).id);
      }
    }
  }
  
  /* Error */
  private void removeUserState(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 261	com/android/server/pm/UserManagerService:mContext	Landroid/content/Context;
    //   4: ldc_w 743
    //   7: invokevirtual 747	android/content/Context:getSystemService	(Ljava/lang/Class;)Ljava/lang/Object;
    //   10: checkcast 743	android/os/storage/StorageManager
    //   13: iload_1
    //   14: invokevirtual 1304	android/os/storage/StorageManager:destroyUserKey	(I)V
    //   17: invokestatic 1310	android/security/GateKeeper:getService	()Landroid/service/gatekeeper/IGateKeeperService;
    //   20: astore_2
    //   21: aload_2
    //   22: ifnull +10 -> 32
    //   25: aload_2
    //   26: iload_1
    //   27: invokeinterface 1315 2 0
    //   32: aload_0
    //   33: getfield 425	com/android/server/pm/UserManagerService:mPm	Lcom/android/server/pm/PackageManagerService;
    //   36: aload_0
    //   37: iload_1
    //   38: invokevirtual 1318	com/android/server/pm/PackageManagerService:cleanUpUser	(Lcom/android/server/pm/UserManagerService;I)V
    //   41: aload_0
    //   42: getfield 425	com/android/server/pm/UserManagerService:mPm	Lcom/android/server/pm/PackageManagerService;
    //   45: iload_1
    //   46: iconst_3
    //   47: invokevirtual 1321	com/android/server/pm/PackageManagerService:destroyUserData	(II)V
    //   50: aload_0
    //   51: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   54: astore_2
    //   55: aload_2
    //   56: monitorenter
    //   57: aload_0
    //   58: getfield 389	com/android/server/pm/UserManagerService:mUsers	Landroid/util/SparseArray;
    //   61: iload_1
    //   62: invokevirtual 1032	android/util/SparseArray:remove	(I)V
    //   65: aload_0
    //   66: getfield 269	com/android/server/pm/UserManagerService:mIsUserManaged	Landroid/util/SparseBooleanArray;
    //   69: iload_1
    //   70: invokevirtual 1323	android/util/SparseBooleanArray:delete	(I)V
    //   73: aload_2
    //   74: monitorexit
    //   75: aload_0
    //   76: getfield 287	com/android/server/pm/UserManagerService:mUserStates	Landroid/util/SparseIntArray;
    //   79: astore_2
    //   80: aload_2
    //   81: monitorenter
    //   82: aload_0
    //   83: getfield 287	com/android/server/pm/UserManagerService:mUserStates	Landroid/util/SparseIntArray;
    //   86: iload_1
    //   87: invokevirtual 1324	android/util/SparseIntArray:delete	(I)V
    //   90: aload_2
    //   91: monitorexit
    //   92: aload_0
    //   93: getfield 275	com/android/server/pm/UserManagerService:mRestrictionsLock	Ljava/lang/Object;
    //   96: astore_2
    //   97: aload_2
    //   98: monitorenter
    //   99: aload_0
    //   100: getfield 253	com/android/server/pm/UserManagerService:mBaseUserRestrictions	Landroid/util/SparseArray;
    //   103: iload_1
    //   104: invokevirtual 1032	android/util/SparseArray:remove	(I)V
    //   107: aload_0
    //   108: getfield 393	com/android/server/pm/UserManagerService:mAppliedUserRestrictions	Landroid/util/SparseArray;
    //   111: iload_1
    //   112: invokevirtual 1032	android/util/SparseArray:remove	(I)V
    //   115: aload_0
    //   116: getfield 391	com/android/server/pm/UserManagerService:mCachedEffectiveUserRestrictions	Landroid/util/SparseArray;
    //   119: iload_1
    //   120: invokevirtual 1032	android/util/SparseArray:remove	(I)V
    //   123: aload_0
    //   124: getfield 397	com/android/server/pm/UserManagerService:mDevicePolicyLocalUserRestrictions	Landroid/util/SparseArray;
    //   127: iload_1
    //   128: invokevirtual 1032	android/util/SparseArray:remove	(I)V
    //   131: aload_2
    //   132: monitorexit
    //   133: aload_0
    //   134: getfield 272	com/android/server/pm/UserManagerService:mPackagesLock	Ljava/lang/Object;
    //   137: astore_2
    //   138: aload_2
    //   139: monitorenter
    //   140: aload_0
    //   141: invokespecial 737	com/android/server/pm/UserManagerService:writeUserListLP	()V
    //   144: aload_2
    //   145: monitorexit
    //   146: new 1098	android/util/AtomicFile
    //   149: dup
    //   150: new 358	java/io/File
    //   153: dup
    //   154: aload_0
    //   155: getfield 431	com/android/server/pm/UserManagerService:mUsersDir	Ljava/io/File;
    //   158: new 347	java/lang/StringBuilder
    //   161: dup
    //   162: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   165: iload_1
    //   166: invokevirtual 540	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   169: ldc -65
    //   171: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   177: invokespecial 429	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   180: invokespecial 1158	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   183: invokevirtual 1326	android/util/AtomicFile:delete	()V
    //   186: aload_0
    //   187: invokespecial 763	com/android/server/pm/UserManagerService:updateUserIds	()V
    //   190: return
    //   191: astore_2
    //   192: ldc 122
    //   194: new 347	java/lang/StringBuilder
    //   197: dup
    //   198: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   201: ldc_w 1328
    //   204: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   207: iload_1
    //   208: invokevirtual 540	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   211: ldc_w 1330
    //   214: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   220: aload_2
    //   221: invokestatic 1332	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   224: pop
    //   225: goto -208 -> 17
    //   228: astore_2
    //   229: ldc 122
    //   231: ldc_w 1334
    //   234: invokestatic 811	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   237: pop
    //   238: goto -206 -> 32
    //   241: astore_3
    //   242: aload_2
    //   243: monitorexit
    //   244: aload_3
    //   245: athrow
    //   246: astore_3
    //   247: aload_2
    //   248: monitorexit
    //   249: aload_3
    //   250: athrow
    //   251: astore_3
    //   252: aload_2
    //   253: monitorexit
    //   254: aload_3
    //   255: athrow
    //   256: astore_3
    //   257: aload_2
    //   258: monitorexit
    //   259: aload_3
    //   260: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	261	0	this	UserManagerService
    //   0	261	1	paramInt	int
    //   191	30	2	localIllegalStateException	IllegalStateException
    //   228	30	2	localException	Exception
    //   241	4	3	localObject2	Object
    //   246	4	3	localObject3	Object
    //   251	4	3	localObject4	Object
    //   256	4	3	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   0	17	191	java/lang/IllegalStateException
    //   17	21	228	java/lang/Exception
    //   25	32	228	java/lang/Exception
    //   57	73	241	finally
    //   82	90	246	finally
    //   99	131	251	finally
    //   140	144	256	finally
  }
  
  private int runList(PrintWriter paramPrintWriter)
    throws RemoteException
  {
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    List localList = getUsers(false);
    if (localList == null)
    {
      paramPrintWriter.println("Error: couldn't get users");
      return 1;
    }
    paramPrintWriter.println("Users:");
    int i = 0;
    if (i < localList.size())
    {
      if (localIActivityManager.isUserRunning(((UserInfo)localList.get(i)).id, 0)) {}
      for (String str = " running";; str = "")
      {
        paramPrintWriter.println("\t" + ((UserInfo)localList.get(i)).toString() + str);
        i += 1;
        break;
      }
    }
    return 0;
  }
  
  private void scheduleWriteUser(UserData paramUserData)
  {
    if (!this.mHandler.hasMessages(1, paramUserData))
    {
      paramUserData = this.mHandler.obtainMessage(1, paramUserData);
      this.mHandler.sendMessageDelayed(paramUserData, 2000L);
    }
  }
  
  private void sendProfileRemovedBroadcast(int paramInt1, int paramInt2)
  {
    Intent localIntent = new Intent("android.intent.action.MANAGED_PROFILE_REMOVED");
    localIntent.addFlags(1342177280);
    localIntent.putExtra("android.intent.extra.USER", new UserHandle(paramInt2));
    localIntent.putExtra("android.intent.extra.user_handle", paramInt2);
    this.mContext.sendBroadcastAsUser(localIntent, new UserHandle(paramInt1), null);
  }
  
  private void sendUserInfoChangedBroadcast(int paramInt)
  {
    Intent localIntent = new Intent("android.intent.action.USER_INFO_CHANGED");
    localIntent.putExtra("android.intent.extra.user_handle", paramInt);
    localIntent.addFlags(1073741824);
    this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
  }
  
  private static void setSerialNumber(File paramFile, int paramInt)
    throws IOException
  {
    try
    {
      byte[] arrayOfByte = Integer.toString(paramInt).getBytes(StandardCharsets.UTF_8);
      Os.setxattr(paramFile.getAbsolutePath(), "user.serial", arrayOfByte, OsConstants.XATTR_CREATE);
      return;
    }
    catch (ErrnoException paramFile)
    {
      throw paramFile.rethrowAsIOException();
    }
  }
  
  private void updateUserIds()
  {
    int j = 0;
    for (;;)
    {
      int i;
      int k;
      synchronized (this.mUsersLock)
      {
        int m = this.mUsers.size();
        i = 0;
        if (i < m)
        {
          k = j;
          if (((UserData)this.mUsers.valueAt(i)).info.partial) {
            break label134;
          }
          k = j + 1;
          break label134;
        }
        int[] arrayOfInt = new int[j];
        j = 0;
        i = 0;
        if (j < m)
        {
          if (!((UserData)this.mUsers.valueAt(j)).info.partial)
          {
            k = i + 1;
            arrayOfInt[i] = this.mUsers.keyAt(j);
            i = k;
            break label143;
          }
        }
        else
        {
          this.mUserIds = arrayOfInt;
          return;
        }
      }
      break label143;
      label134:
      i += 1;
      j = k;
      continue;
      label143:
      j += 1;
    }
  }
  
  @GuardedBy("mRestrictionsLock")
  private void updateUserRestrictionsInternalLR(final Bundle paramBundle, final int paramInt)
  {
    boolean bool2 = true;
    Bundle localBundle1 = UserRestrictionsUtils.nonNull((Bundle)this.mAppliedUserRestrictions.get(paramInt));
    Bundle localBundle2;
    if (paramBundle != null)
    {
      localBundle2 = (Bundle)this.mBaseUserRestrictions.get(paramInt);
      if (localBundle2 == paramBundle) {
        break label159;
      }
      bool1 = true;
      Preconditions.checkState(bool1);
      if (this.mCachedEffectiveUserRestrictions.get(paramInt) == paramBundle) {
        break label164;
      }
    }
    label159:
    label164:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Preconditions.checkState(bool1);
      if (!UserRestrictionsUtils.areEqual(localBundle2, paramBundle))
      {
        this.mBaseUserRestrictions.put(paramInt, paramBundle);
        scheduleWriteUser(getUserDataNoChecks(paramInt));
      }
      paramBundle = computeEffectiveUserRestrictionsLR(paramInt);
      this.mCachedEffectiveUserRestrictions.put(paramInt, paramBundle);
      if (this.mAppOpsService != null) {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            try
            {
              UserManagerService.-get0(UserManagerService.this).setUserRestrictions(paramBundle, UserManagerService.-get7(), paramInt);
              return;
            }
            catch (RemoteException localRemoteException)
            {
              Log.w("UserManagerService", "Unable to notify AppOpsService of UserRestrictions");
            }
          }
        });
      }
      propagateUserRestrictionsLR(paramInt, paramBundle, localBundle1);
      this.mAppliedUserRestrictions.put(paramInt, new Bundle(paramBundle));
      return;
      bool1 = false;
      break;
    }
  }
  
  private void upgradeIfNecessaryLP()
  {
    int k = this.mUserVersion;
    int j = this.mUserVersion;
    int i = j;
    Object localObject1;
    if (j < 1)
    {
      localObject1 = getUserDataNoChecks(0);
      if ("Primary".equals(((UserData)localObject1).info.name))
      {
        ((UserData)localObject1).info.name = this.mContext.getResources().getString(17040713);
        scheduleWriteUser((UserData)localObject1);
      }
      i = 1;
    }
    j = i;
    Object localObject2;
    if (i < 2)
    {
      localObject1 = getUserDataNoChecks(0);
      if ((((UserData)localObject1).info.flags & 0x10) == 0)
      {
        localObject2 = ((UserData)localObject1).info;
        ((UserInfo)localObject2).flags |= 0x10;
        scheduleWriteUser((UserData)localObject1);
      }
      j = 2;
    }
    i = j;
    if (j < 4) {
      i = 4;
    }
    j = i;
    if (i < 5)
    {
      initDefaultGuestRestrictions();
      j = 5;
    }
    i = j;
    boolean bool;
    if (j < 6)
    {
      bool = UserManager.isSplitSystemUser();
      localObject1 = this.mUsersLock;
      i = 0;
    }
    do
    {
      try
      {
        while (i < this.mUsers.size())
        {
          localObject2 = (UserData)this.mUsers.valueAt(i);
          if ((!bool) && (((UserData)localObject2).info.isRestricted()) && (((UserData)localObject2).info.restrictedProfileParentId == 55536))
          {
            ((UserData)localObject2).info.restrictedProfileParentId = 0;
            scheduleWriteUser((UserData)localObject2);
          }
          i += 1;
        }
        i = 6;
        if (i < 6)
        {
          Slog.w("UserManagerService", "User version " + this.mUserVersion + " didn't upgrade as expected to " + 6);
          return;
        }
      }
      finally {}
      this.mUserVersion = i;
    } while (k >= this.mUserVersion);
    writeUserListLP();
  }
  
  private UserInfo userWithName(UserInfo paramUserInfo)
  {
    if ((paramUserInfo != null) && (paramUserInfo.name == null) && (paramUserInfo.id == 0))
    {
      paramUserInfo = new UserInfo(paramUserInfo);
      paramUserInfo.name = getOwnerName();
      return paramUserInfo;
    }
    return paramUserInfo;
  }
  
  static void writeApplicationRestrictionsLP(Bundle paramBundle, AtomicFile paramAtomicFile)
  {
    Object localObject = null;
    try
    {
      FileOutputStream localFileOutputStream = paramAtomicFile.startWrite();
      localObject = localFileOutputStream;
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
      localObject = localFileOutputStream;
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localObject = localFileOutputStream;
      localFastXmlSerializer.setOutput(localBufferedOutputStream, StandardCharsets.UTF_8.name());
      localObject = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject = localFileOutputStream;
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localObject = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "restrictions");
      localObject = localFileOutputStream;
      writeBundle(paramBundle, localFastXmlSerializer);
      localObject = localFileOutputStream;
      localFastXmlSerializer.endTag(null, "restrictions");
      localObject = localFileOutputStream;
      localFastXmlSerializer.endDocument();
      localObject = localFileOutputStream;
      paramAtomicFile.finishWrite(localFileOutputStream);
      return;
    }
    catch (Exception paramBundle)
    {
      paramAtomicFile.failWrite((FileOutputStream)localObject);
      Slog.e("UserManagerService", "Error writing application restrictions list", paramBundle);
    }
  }
  
  private void writeApplicationRestrictionsLP(String paramString, Bundle paramBundle, int paramInt)
  {
    writeApplicationRestrictionsLP(paramBundle, new AtomicFile(new File(Environment.getUserSystemDirectory(paramInt), packageToRestrictionsFileName(paramString))));
  }
  
  private void writeBitmapLP(UserInfo paramUserInfo, Bitmap paramBitmap)
  {
    try
    {
      Object localObject = new File(this.mUsersDir, Integer.toString(paramUserInfo.id));
      File localFile2 = new File((File)localObject, "photo.png");
      File localFile1 = new File((File)localObject, "photo.png.tmp");
      if (!((File)localObject).exists())
      {
        ((File)localObject).mkdir();
        FileUtils.setPermissions(((File)localObject).getPath(), 505, -1, -1);
      }
      localObject = Bitmap.CompressFormat.PNG;
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile1);
      if ((paramBitmap.compress((Bitmap.CompressFormat)localObject, 100, localFileOutputStream)) && (localFile1.renameTo(localFile2)) && (SELinux.restorecon(localFile2))) {
        paramUserInfo.iconPath = localFile2.getAbsolutePath();
      }
      try
      {
        localFileOutputStream.close();
        localFile1.delete();
        return;
      }
      catch (IOException paramUserInfo)
      {
        for (;;) {}
      }
      return;
    }
    catch (FileNotFoundException paramUserInfo)
    {
      Slog.w("UserManagerService", "Error setting photo for user ", paramUserInfo);
    }
  }
  
  private static void writeBundle(Bundle paramBundle, XmlSerializer paramXmlSerializer)
    throws IOException
  {
    Iterator localIterator = paramBundle.keySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject1 = (String)localIterator.next();
      Object localObject2 = paramBundle.get((String)localObject1);
      paramXmlSerializer.startTag(null, "entry");
      paramXmlSerializer.attribute(null, "key", (String)localObject1);
      if ((localObject2 instanceof Boolean))
      {
        paramXmlSerializer.attribute(null, "type", "b");
        paramXmlSerializer.text(localObject2.toString());
      }
      for (;;)
      {
        paramXmlSerializer.endTag(null, "entry");
        break;
        if ((localObject2 instanceof Integer))
        {
          paramXmlSerializer.attribute(null, "type", "i");
          paramXmlSerializer.text(localObject2.toString());
        }
        else
        {
          if ((localObject2 == null) || ((localObject2 instanceof String)))
          {
            paramXmlSerializer.attribute(null, "type", "s");
            if (localObject2 != null) {}
            for (localObject1 = (String)localObject2;; localObject1 = "")
            {
              paramXmlSerializer.text((String)localObject1);
              break;
            }
          }
          if ((localObject2 instanceof Bundle))
          {
            paramXmlSerializer.attribute(null, "type", "B");
            writeBundle((Bundle)localObject2, paramXmlSerializer);
          }
          else
          {
            if (!(localObject2 instanceof Parcelable[])) {
              break label343;
            }
            paramXmlSerializer.attribute(null, "type", "BA");
            localObject1 = (Parcelable[])localObject2;
            j = localObject1.length;
            i = 0;
            while (i < j)
            {
              localObject2 = localObject1[i];
              if (!(localObject2 instanceof Bundle)) {
                throw new IllegalArgumentException("bundle-array can only hold Bundles");
              }
              paramXmlSerializer.startTag(null, "entry");
              paramXmlSerializer.attribute(null, "type", "B");
              writeBundle((Bundle)localObject2, paramXmlSerializer);
              paramXmlSerializer.endTag(null, "entry");
              i += 1;
            }
          }
        }
      }
      label343:
      paramXmlSerializer.attribute(null, "type", "sa");
      localObject2 = (String[])localObject2;
      paramXmlSerializer.attribute(null, "m", Integer.toString(localObject2.length));
      int j = localObject2.length;
      int i = 0;
      label384:
      if (i < j)
      {
        localObject1 = localObject2[i];
        paramXmlSerializer.startTag(null, "value");
        if (localObject1 == null) {
          break label436;
        }
      }
      for (;;)
      {
        paramXmlSerializer.text((String)localObject1);
        paramXmlSerializer.endTag(null, "value");
        i += 1;
        break label384;
        break;
        label436:
        localObject1 = "";
      }
    }
  }
  
  private void writeUserLP(UserData paramUserData)
  {
    Object localObject1 = null;
    AtomicFile localAtomicFile = new AtomicFile(new File(this.mUsersDir, paramUserData.info.id + ".xml"));
    try
    {
      FileOutputStream localFileOutputStream = localAtomicFile.startWrite();
      localObject1 = localFileOutputStream;
      Object localObject3 = new BufferedOutputStream(localFileOutputStream);
      localObject1 = localFileOutputStream;
      FastXmlSerializer localFastXmlSerializer = new FastXmlSerializer();
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setOutput((OutputStream)localObject3, StandardCharsets.UTF_8.name());
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
      localObject1 = localFileOutputStream;
      UserInfo localUserInfo = paramUserData.info;
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.startTag(null, "user");
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "id", Integer.toString(localUserInfo.id));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "serialNumber", Integer.toString(localUserInfo.serialNumber));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "flags", Integer.toString(localUserInfo.flags));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "created", Long.toString(localUserInfo.creationTime));
      localObject1 = localFileOutputStream;
      localFastXmlSerializer.attribute(null, "lastLoggedIn", Long.toString(localUserInfo.lastLoggedInTime));
      localObject1 = localFileOutputStream;
      if (localUserInfo.lastLoggedInFingerprint != null)
      {
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "lastLoggedInFingerprint", localUserInfo.lastLoggedInFingerprint);
      }
      localObject1 = localFileOutputStream;
      if (localUserInfo.iconPath != null)
      {
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "icon", localUserInfo.iconPath);
      }
      localObject1 = localFileOutputStream;
      if (localUserInfo.partial)
      {
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "partial", "true");
      }
      localObject1 = localFileOutputStream;
      if (localUserInfo.guestToRemove)
      {
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "guestToRemove", "true");
      }
      localObject1 = localFileOutputStream;
      if (localUserInfo.profileGroupId != 55536)
      {
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "profileGroupId", Integer.toString(localUserInfo.profileGroupId));
      }
      localObject1 = localFileOutputStream;
      if (localUserInfo.restrictedProfileParentId != 55536)
      {
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.attribute(null, "restrictedProfileParentId", Integer.toString(localUserInfo.restrictedProfileParentId));
      }
      localObject1 = localFileOutputStream;
      if (paramUserData.persistSeedData)
      {
        localObject1 = localFileOutputStream;
        if (paramUserData.seedAccountName != null)
        {
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.attribute(null, "seedAccountName", paramUserData.seedAccountName);
        }
        localObject1 = localFileOutputStream;
        if (paramUserData.seedAccountType != null)
        {
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.attribute(null, "seedAccountType", paramUserData.seedAccountType);
        }
      }
      localObject1 = localFileOutputStream;
      if (localUserInfo.name != null)
      {
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.startTag(null, "name");
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.text(localUserInfo.name);
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "name");
      }
      localObject1 = localFileOutputStream;
      localObject3 = this.mRestrictionsLock;
      localObject1 = localFileOutputStream;
      try
      {
        UserRestrictionsUtils.writeRestrictions(localFastXmlSerializer, (Bundle)this.mBaseUserRestrictions.get(localUserInfo.id), "restrictions");
        UserRestrictionsUtils.writeRestrictions(localFastXmlSerializer, (Bundle)this.mDevicePolicyLocalUserRestrictions.get(localUserInfo.id), "device_policy_restrictions");
        localObject1 = localFileOutputStream;
        localObject1 = localFileOutputStream;
        if (paramUserData.account != null)
        {
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.startTag(null, "account");
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.text(paramUserData.account);
          localObject1 = localFileOutputStream;
          localFastXmlSerializer.endTag(null, "account");
        }
        localObject1 = localFileOutputStream;
        if (paramUserData.persistSeedData)
        {
          localObject1 = localFileOutputStream;
          if (paramUserData.seedAccountOptions != null)
          {
            localObject1 = localFileOutputStream;
            localFastXmlSerializer.startTag(null, "seedAccountOptions");
            localObject1 = localFileOutputStream;
            paramUserData.seedAccountOptions.saveToXml(localFastXmlSerializer);
            localObject1 = localFileOutputStream;
            localFastXmlSerializer.endTag(null, "seedAccountOptions");
          }
        }
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endTag(null, "user");
        localObject1 = localFileOutputStream;
        localFastXmlSerializer.endDocument();
        localObject1 = localFileOutputStream;
        localAtomicFile.finishWrite(localFileOutputStream);
        return;
      }
      finally
      {
        localObject1 = localFileOutputStream;
        localObject1 = localFileOutputStream;
      }
      return;
    }
    catch (Exception localException)
    {
      Slog.e("UserManagerService", "Error writing user info " + paramUserData.info.id, localException);
      localAtomicFile.failWrite((FileOutputStream)localObject1);
    }
  }
  
  /* Error */
  private void writeUserListLP()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: new 1098	android/util/AtomicFile
    //   6: dup
    //   7: aload_0
    //   8: getfield 450	com/android/server/pm/UserManagerService:mUserListFile	Ljava/io/File;
    //   11: invokespecial 1158	android/util/AtomicFile:<init>	(Ljava/io/File;)V
    //   14: astore 6
    //   16: aload 6
    //   18: invokevirtual 1441	android/util/AtomicFile:startWrite	()Ljava/io/FileOutputStream;
    //   21: astore 5
    //   23: aload 5
    //   25: astore 4
    //   27: new 1443	java/io/BufferedOutputStream
    //   30: dup
    //   31: aload 5
    //   33: invokespecial 1446	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   36: astore 8
    //   38: aload 5
    //   40: astore 4
    //   42: new 1448	com/android/internal/util/FastXmlSerializer
    //   45: dup
    //   46: invokespecial 1449	com/android/internal/util/FastXmlSerializer:<init>	()V
    //   49: astore 7
    //   51: aload 5
    //   53: astore 4
    //   55: aload 7
    //   57: aload 8
    //   59: getstatic 1117	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   62: invokevirtual 1121	java/nio/charset/Charset:name	()Ljava/lang/String;
    //   65: invokeinterface 1455 3 0
    //   70: aload 5
    //   72: astore 4
    //   74: aload 7
    //   76: aconst_null
    //   77: iconst_1
    //   78: invokestatic 1458	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   81: invokeinterface 1462 3 0
    //   86: aload 5
    //   88: astore 4
    //   90: aload 7
    //   92: ldc_w 1464
    //   95: iconst_1
    //   96: invokeinterface 1467 3 0
    //   101: aload 5
    //   103: astore 4
    //   105: aload 7
    //   107: aconst_null
    //   108: ldc -95
    //   110: invokeinterface 1471 3 0
    //   115: pop
    //   116: aload 5
    //   118: astore 4
    //   120: aload 7
    //   122: aconst_null
    //   123: ldc 69
    //   125: aload_0
    //   126: getfield 710	com/android/server/pm/UserManagerService:mNextSerialNumber	I
    //   129: invokestatic 1232	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   132: invokeinterface 1540 4 0
    //   137: pop
    //   138: aload 5
    //   140: astore 4
    //   142: aload 7
    //   144: aconst_null
    //   145: ldc 108
    //   147: aload_0
    //   148: getfield 409	com/android/server/pm/UserManagerService:mUserVersion	I
    //   151: invokestatic 1232	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   154: invokeinterface 1540 4 0
    //   159: pop
    //   160: aload 5
    //   162: astore 4
    //   164: aload 7
    //   166: aconst_null
    //   167: ldc -110
    //   169: invokeinterface 1471 3 0
    //   174: pop
    //   175: aload 5
    //   177: astore 4
    //   179: aload_0
    //   180: getfield 402	com/android/server/pm/UserManagerService:mGuestRestrictions	Landroid/os/Bundle;
    //   183: astore 8
    //   185: aload 5
    //   187: astore 4
    //   189: aload 8
    //   191: monitorenter
    //   192: aload 7
    //   194: aload_0
    //   195: getfield 402	com/android/server/pm/UserManagerService:mGuestRestrictions	Landroid/os/Bundle;
    //   198: ldc -104
    //   200: invokestatic 1559	com/android/server/pm/UserRestrictionsUtils:writeRestrictions	(Lorg/xmlpull/v1/XmlSerializer;Landroid/os/Bundle;Ljava/lang/String;)V
    //   203: aload 5
    //   205: astore 4
    //   207: aload 8
    //   209: monitorexit
    //   210: aload 5
    //   212: astore 4
    //   214: aload 7
    //   216: aconst_null
    //   217: ldc -110
    //   219: invokeinterface 1478 3 0
    //   224: pop
    //   225: aload 5
    //   227: astore 4
    //   229: aload_0
    //   230: getfield 275	com/android/server/pm/UserManagerService:mRestrictionsLock	Ljava/lang/Object;
    //   233: astore 8
    //   235: aload 5
    //   237: astore 4
    //   239: aload 8
    //   241: monitorenter
    //   242: aload 7
    //   244: aload_0
    //   245: getfield 606	com/android/server/pm/UserManagerService:mDevicePolicyGlobalUserRestrictions	Landroid/os/Bundle;
    //   248: ldc -119
    //   250: invokestatic 1559	com/android/server/pm/UserRestrictionsUtils:writeRestrictions	(Lorg/xmlpull/v1/XmlSerializer;Landroid/os/Bundle;Ljava/lang/String;)V
    //   253: aload 5
    //   255: astore 4
    //   257: aload 8
    //   259: monitorexit
    //   260: aload 5
    //   262: astore 4
    //   264: aload 7
    //   266: aconst_null
    //   267: ldc -113
    //   269: invokeinterface 1471 3 0
    //   274: pop
    //   275: aload 5
    //   277: astore 4
    //   279: aload 7
    //   281: aconst_null
    //   282: ldc 54
    //   284: aload_0
    //   285: getfield 395	com/android/server/pm/UserManagerService:mGlobalRestrictionOwnerUserId	I
    //   288: invokestatic 1232	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   291: invokeinterface 1540 4 0
    //   296: pop
    //   297: aload 5
    //   299: astore 4
    //   301: aload 7
    //   303: aconst_null
    //   304: ldc -113
    //   306: invokeinterface 1478 3 0
    //   311: pop
    //   312: aload 5
    //   314: astore 4
    //   316: aload_0
    //   317: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   320: astore 8
    //   322: aload 5
    //   324: astore 4
    //   326: aload 8
    //   328: monitorenter
    //   329: aload_0
    //   330: getfield 389	com/android/server/pm/UserManagerService:mUsers	Landroid/util/SparseArray;
    //   333: invokevirtual 889	android/util/SparseArray:size	()I
    //   336: newarray <illegal type>
    //   338: astore 9
    //   340: iconst_0
    //   341: istore_1
    //   342: iload_1
    //   343: aload 9
    //   345: arraylength
    //   346: if_icmpge +82 -> 428
    //   349: aload 9
    //   351: iload_1
    //   352: aload_0
    //   353: getfield 389	com/android/server/pm/UserManagerService:mUsers	Landroid/util/SparseArray;
    //   356: iload_1
    //   357: invokevirtual 892	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   360: checkcast 34	com/android/server/pm/UserManagerService$UserData
    //   363: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   366: getfield 741	android/content/pm/UserInfo:id	I
    //   369: iastore
    //   370: iload_1
    //   371: iconst_1
    //   372: iadd
    //   373: istore_1
    //   374: goto -32 -> 342
    //   377: astore 7
    //   379: aload 5
    //   381: astore 4
    //   383: aload 8
    //   385: monitorexit
    //   386: aload 5
    //   388: astore 4
    //   390: aload 7
    //   392: athrow
    //   393: astore 5
    //   395: aload 6
    //   397: aload 4
    //   399: invokevirtual 1488	android/util/AtomicFile:failWrite	(Ljava/io/FileOutputStream;)V
    //   402: ldc 122
    //   404: ldc_w 1567
    //   407: invokestatic 1139	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   410: pop
    //   411: return
    //   412: astore 7
    //   414: aload 5
    //   416: astore 4
    //   418: aload 8
    //   420: monitorexit
    //   421: aload 5
    //   423: astore 4
    //   425: aload 7
    //   427: athrow
    //   428: aload 5
    //   430: astore 4
    //   432: aload 8
    //   434: monitorexit
    //   435: iconst_0
    //   436: istore_1
    //   437: aload 5
    //   439: astore 4
    //   441: aload 9
    //   443: arraylength
    //   444: istore_2
    //   445: iload_1
    //   446: iload_2
    //   447: if_icmpge +80 -> 527
    //   450: aload 9
    //   452: iload_1
    //   453: iaload
    //   454: istore_3
    //   455: aload 5
    //   457: astore 4
    //   459: aload 7
    //   461: aconst_null
    //   462: ldc -98
    //   464: invokeinterface 1471 3 0
    //   469: pop
    //   470: aload 5
    //   472: astore 4
    //   474: aload 7
    //   476: aconst_null
    //   477: ldc 54
    //   479: iload_3
    //   480: invokestatic 1232	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   483: invokeinterface 1540 4 0
    //   488: pop
    //   489: aload 5
    //   491: astore 4
    //   493: aload 7
    //   495: aconst_null
    //   496: ldc -98
    //   498: invokeinterface 1478 3 0
    //   503: pop
    //   504: iload_1
    //   505: iconst_1
    //   506: iadd
    //   507: istore_1
    //   508: goto -63 -> 445
    //   511: astore 7
    //   513: aload 5
    //   515: astore 4
    //   517: aload 8
    //   519: monitorexit
    //   520: aload 5
    //   522: astore 4
    //   524: aload 7
    //   526: athrow
    //   527: aload 5
    //   529: astore 4
    //   531: aload 7
    //   533: aconst_null
    //   534: ldc -95
    //   536: invokeinterface 1478 3 0
    //   541: pop
    //   542: aload 5
    //   544: astore 4
    //   546: aload 7
    //   548: invokeinterface 1481 1 0
    //   553: aload 5
    //   555: astore 4
    //   557: aload 6
    //   559: aload 5
    //   561: invokevirtual 1485	android/util/AtomicFile:finishWrite	(Ljava/io/FileOutputStream;)V
    //   564: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	565	0	this	UserManagerService
    //   341	167	1	i	int
    //   444	4	2	j	int
    //   454	26	3	k	int
    //   1	555	4	localObject1	Object
    //   21	366	5	localFileOutputStream	FileOutputStream
    //   393	167	5	localException	Exception
    //   14	544	6	localAtomicFile	AtomicFile
    //   49	253	7	localFastXmlSerializer	FastXmlSerializer
    //   377	14	7	localObject2	Object
    //   412	82	7	localObject3	Object
    //   511	36	7	localObject4	Object
    //   36	482	8	localObject5	Object
    //   338	113	9	arrayOfInt	int[]
    // Exception table:
    //   from	to	target	type
    //   192	203	377	finally
    //   16	23	393	java/lang/Exception
    //   27	38	393	java/lang/Exception
    //   42	51	393	java/lang/Exception
    //   55	70	393	java/lang/Exception
    //   74	86	393	java/lang/Exception
    //   90	101	393	java/lang/Exception
    //   105	116	393	java/lang/Exception
    //   120	138	393	java/lang/Exception
    //   142	160	393	java/lang/Exception
    //   164	175	393	java/lang/Exception
    //   179	185	393	java/lang/Exception
    //   189	192	393	java/lang/Exception
    //   207	210	393	java/lang/Exception
    //   214	225	393	java/lang/Exception
    //   229	235	393	java/lang/Exception
    //   239	242	393	java/lang/Exception
    //   257	260	393	java/lang/Exception
    //   264	275	393	java/lang/Exception
    //   279	297	393	java/lang/Exception
    //   301	312	393	java/lang/Exception
    //   316	322	393	java/lang/Exception
    //   326	329	393	java/lang/Exception
    //   383	386	393	java/lang/Exception
    //   390	393	393	java/lang/Exception
    //   418	421	393	java/lang/Exception
    //   425	428	393	java/lang/Exception
    //   432	435	393	java/lang/Exception
    //   441	445	393	java/lang/Exception
    //   459	470	393	java/lang/Exception
    //   474	489	393	java/lang/Exception
    //   493	504	393	java/lang/Exception
    //   517	520	393	java/lang/Exception
    //   524	527	393	java/lang/Exception
    //   531	542	393	java/lang/Exception
    //   546	553	393	java/lang/Exception
    //   557	564	393	java/lang/Exception
    //   242	253	412	finally
    //   329	340	511	finally
    //   342	370	511	finally
  }
  
  @GuardedBy("mRestrictionsLock")
  void applyUserRestrictionsForAllUsersLR()
  {
    this.mCachedEffectiveUserRestrictions.clear();
    Runnable local4 = new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: invokestatic 29	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
        //   3: invokeinterface 35 1 0
        //   8: astore_3
        //   9: aload_0
        //   10: getfield 17	com/android/server/pm/UserManagerService$4:this$0	Lcom/android/server/pm/UserManagerService;
        //   13: invokestatic 39	com/android/server/pm/UserManagerService:-get6	(Lcom/android/server/pm/UserManagerService;)Ljava/lang/Object;
        //   16: astore_2
        //   17: aload_2
        //   18: monitorenter
        //   19: iconst_0
        //   20: istore_1
        //   21: iload_1
        //   22: aload_3
        //   23: arraylength
        //   24: if_icmpge +30 -> 54
        //   27: aload_0
        //   28: getfield 17	com/android/server/pm/UserManagerService$4:this$0	Lcom/android/server/pm/UserManagerService;
        //   31: aload_3
        //   32: iload_1
        //   33: iaload
        //   34: invokevirtual 43	com/android/server/pm/UserManagerService:applyUserRestrictionsLR	(I)V
        //   37: iload_1
        //   38: iconst_1
        //   39: iadd
        //   40: istore_1
        //   41: goto -20 -> 21
        //   44: astore_2
        //   45: ldc 45
        //   47: ldc 47
        //   49: invokestatic 53	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
        //   52: pop
        //   53: return
        //   54: aload_2
        //   55: monitorexit
        //   56: return
        //   57: astore_3
        //   58: aload_2
        //   59: monitorexit
        //   60: aload_3
        //   61: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	62	0	this	4
        //   20	21	1	i	int
        //   16	2	2	localObject1	Object
        //   44	15	2	localRemoteException	RemoteException
        //   8	24	3	arrayOfInt	int[]
        //   57	4	3	localObject2	Object
        // Exception table:
        //   from	to	target	type
        //   0	9	44	android/os/RemoteException
        //   21	37	57	finally
      }
    };
    this.mHandler.post(local4);
  }
  
  void applyUserRestrictionsLR(int paramInt)
  {
    updateUserRestrictionsInternalLR(null, paramInt);
  }
  
  public boolean canAddMoreManagedProfiles(int paramInt, boolean paramBoolean)
  {
    boolean bool = true;
    checkManageUsersPermission("check if more managed profiles can be added.");
    if (ActivityManager.isLowRamDeviceStatic()) {
      return false;
    }
    if (!this.mContext.getPackageManager().hasSystemFeature("android.software.managed_users")) {
      return false;
    }
    int j = getProfiles(paramInt, true).size() - 1;
    if ((j > 0) && (paramBoolean)) {}
    for (int i = 1; j - i >= 1; i = 0) {
      return false;
    }
    synchronized (this.mUsersLock)
    {
      paramBoolean = getUserInfoLU(paramInt).canHaveProfile();
      if (!paramBoolean) {
        return false;
      }
      paramInt = getAliveUsersExcludingGuestsCountLU() - i;
      paramBoolean = bool;
      if (paramInt != 1)
      {
        i = UserManager.getMaxSupportedUsers();
        if (paramInt < i) {
          paramBoolean = bool;
        }
      }
      else
      {
        return paramBoolean;
      }
      paramBoolean = false;
    }
  }
  
  public boolean canHaveRestrictedProfile(int paramInt)
  {
    boolean bool2 = false;
    checkManageUsersPermission("canHaveRestrictedProfile");
    synchronized (this.mUsersLock)
    {
      UserInfo localUserInfo = getUserInfoLU(paramInt);
      if ((localUserInfo != null) && (localUserInfo.canHaveProfile()))
      {
        bool1 = localUserInfo.isAdmin();
        if (!bool1) {
          return false;
        }
      }
      else
      {
        return false;
      }
      boolean bool1 = bool2;
      if (!this.mIsDeviceManaged)
      {
        bool1 = this.mIsUserManaged.get(paramInt);
        if (bool1) {
          bool1 = bool2;
        }
      }
      else
      {
        return bool1;
      }
      bool1 = true;
    }
  }
  
  void cleanupPartialUsers()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this.mUsersLock)
    {
      int j = this.mUsers.size();
      int i = 0;
      while (i < j)
      {
        UserInfo localUserInfo = ((UserData)this.mUsers.valueAt(i)).info;
        if (((localUserInfo.partial) || (localUserInfo.guestToRemove) || (localUserInfo.isEphemeral())) && (i != 0))
        {
          localArrayList.add(localUserInfo);
          this.mRemovingUserIds.append(localUserInfo.id, true);
          localUserInfo.partial = true;
        }
        i += 1;
      }
      j = localArrayList.size();
      i = 0;
      if (i < j)
      {
        ??? = (UserInfo)localArrayList.get(i);
        Slog.w("UserManagerService", "Removing partially created user " + ((UserInfo)???).id + " (name=" + ((UserInfo)???).name + ")");
        removeUserState(((UserInfo)???).id);
        i += 1;
      }
    }
  }
  
  public void clearSeedAccountData()
    throws RemoteException
  {
    checkManageUsersPermission("Cannot clear seed account information");
    synchronized (this.mPackagesLock)
    {
      synchronized (this.mUsersLock)
      {
        UserData localUserData = getUserDataLU(UserHandle.getCallingUserId());
        if (localUserData == null) {
          return;
        }
        localUserData.clearSeedAccountData();
        writeUserLP(localUserData);
        return;
      }
    }
  }
  
  public UserInfo createProfileForUser(String paramString, int paramInt1, int paramInt2)
  {
    checkManageOrCreateUsersPermission(paramInt1);
    return createUserInternal(paramString, paramInt1, paramInt2);
  }
  
  public UserInfo createRestrictedProfile(String paramString, int paramInt)
  {
    checkManageOrCreateUsersPermission("setupRestrictedProfile");
    paramString = createProfileForUser(paramString, 8, paramInt);
    if (paramString == null) {
      return null;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      setUserRestriction("no_modify_accounts", true, paramString.id);
      Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "location_mode", 0, paramString.id);
      setUserRestriction("no_share_location", true, paramString.id);
      return paramString;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public UserInfo createUser(String paramString, int paramInt)
  {
    checkManageOrCreateUsersPermission(paramInt);
    return createUserInternal(paramString, paramInt, 55536);
  }
  
  protected void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] arg3)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump UserManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + "android.permission.DUMP");
      return;
    }
    long l = System.currentTimeMillis();
    StringBuilder localStringBuilder = new StringBuilder();
    for (;;)
    {
      int i;
      UserData localUserData;
      UserInfo localUserInfo;
      synchronized (this.mPackagesLock)
      {
        synchronized (this.mUsersLock)
        {
          paramPrintWriter.println("Users:");
          i = 0;
          if (i < this.mUsers.size())
          {
            localUserData = (UserData)this.mUsers.valueAt(i);
            if (localUserData == null) {
              break label914;
            }
            localUserInfo = localUserData.info;
            int j = localUserInfo.id;
            paramPrintWriter.print("  ");
            paramPrintWriter.print(localUserInfo);
            paramPrintWriter.print(" serialNo=");
            paramPrintWriter.print(localUserInfo.serialNumber);
            if (this.mRemovingUserIds.get(j)) {
              paramPrintWriter.print(" <removing> ");
            }
            if (localUserInfo.partial) {
              paramPrintWriter.print(" <partial>");
            }
            paramPrintWriter.println();
            paramPrintWriter.print("    Created: ");
            if (localUserInfo.creationTime == 0L)
            {
              paramPrintWriter.println("<unknown>");
              paramPrintWriter.print("    Last logged in: ");
              if (localUserInfo.lastLoggedInTime != 0L) {
                break label589;
              }
              paramPrintWriter.println("<unknown>");
              paramPrintWriter.print("    Last logged in fingerprint: ");
              paramPrintWriter.println(localUserInfo.lastLoggedInFingerprint);
              paramPrintWriter.print("    Has profile owner: ");
              paramPrintWriter.println(this.mIsUserManaged.get(j));
              paramPrintWriter.println("    Restrictions:");
            }
          }
        }
      }
      synchronized (this.mRestrictionsLock)
      {
        UserRestrictionsUtils.dumpRestrictions(paramPrintWriter, "      ", (Bundle)this.mBaseUserRestrictions.get(localUserInfo.id));
        paramPrintWriter.println("    Device policy local restrictions:");
        UserRestrictionsUtils.dumpRestrictions(paramPrintWriter, "      ", (Bundle)this.mDevicePolicyLocalUserRestrictions.get(localUserInfo.id));
        paramPrintWriter.println("    Effective restrictions:");
        UserRestrictionsUtils.dumpRestrictions(paramPrintWriter, "      ", (Bundle)this.mCachedEffectiveUserRestrictions.get(localUserInfo.id));
        if (localUserData.account != null)
        {
          paramPrintWriter.print("    Account name: " + localUserData.account);
          paramPrintWriter.println();
        }
        if (localUserData.seedAccountName == null) {
          break label914;
        }
        paramPrintWriter.print("    Seed account name: " + localUserData.seedAccountName);
        paramPrintWriter.println();
        if (localUserData.seedAccountType != null)
        {
          paramPrintWriter.print("         account type: " + localUserData.seedAccountType);
          paramPrintWriter.println();
        }
        if (localUserData.seedAccountOptions == null) {
          break label914;
        }
        paramPrintWriter.print("         account options exist");
        paramPrintWriter.println();
        break label914;
        paramPrintWriter = finally;
        throw paramPrintWriter;
        paramPrintWriter = finally;
        throw paramPrintWriter;
        localStringBuilder.setLength(0);
        TimeUtils.formatDuration(l - localUserInfo.creationTime, localStringBuilder);
        localStringBuilder.append(" ago");
        paramPrintWriter.println(localStringBuilder);
        continue;
        label589:
        localStringBuilder.setLength(0);
        TimeUtils.formatDuration(l - localUserInfo.lastLoggedInTime, localStringBuilder);
        localStringBuilder.append(" ago");
        paramPrintWriter.println(localStringBuilder);
      }
      paramPrintWriter.println();
      paramPrintWriter.println("  Device policy global restrictions:");
      synchronized (this.mRestrictionsLock)
      {
        UserRestrictionsUtils.dumpRestrictions(paramPrintWriter, "    ", this.mDevicePolicyGlobalUserRestrictions);
        paramPrintWriter.println();
        paramPrintWriter.println("  Global restrictions owner id:" + this.mGlobalRestrictionOwnerUserId);
        paramPrintWriter.println();
        paramPrintWriter.println("  Guest restrictions:");
        synchronized (this.mGuestRestrictions)
        {
          UserRestrictionsUtils.dumpRestrictions(paramPrintWriter, "    ", this.mGuestRestrictions);
          synchronized (this.mUsersLock)
          {
            paramPrintWriter.println();
            paramPrintWriter.println("  Device managed: " + this.mIsDeviceManaged);
          }
        }
      }
      synchronized (this.mUserStates)
      {
        paramPrintWriter.println("  Started users state: " + this.mUserStates);
        paramPrintWriter.println();
        paramPrintWriter.println("  Max users: " + UserManager.getMaxSupportedUsers());
        paramPrintWriter.println("  Supports switchable users: " + UserManager.supportsMultipleUsers());
        paramPrintWriter.println("  All guests ephemeral: " + Resources.getSystem().getBoolean(17957041));
        return;
        paramPrintWriter = finally;
        throw paramPrintWriter;
        paramPrintWriter = finally;
        throw paramPrintWriter;
        paramPrintWriter = finally;
        throw paramPrintWriter;
      }
      label914:
      i += 1;
    }
  }
  
  public boolean exists(int paramInt)
  {
    return getUserInfoNoChecks(paramInt) != null;
  }
  
  void finishRemoveUser(final int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      Intent localIntent = new Intent("android.intent.action.USER_REMOVED");
      localIntent.putExtra("android.intent.extra.user_handle", paramInt);
      this.mContext.sendOrderedBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.MANAGE_USERS", new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          new Thread()
          {
            public void run()
            {
              ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)).onUserRemoved(this.val$userHandle);
              UserManagerService.-wrap5(UserManagerService.this, this.val$userHandle);
            }
          }.start();
        }
      }, null, -1, null, null);
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public Bundle getApplicationRestrictions(String paramString)
  {
    return getApplicationRestrictionsForUser(paramString, UserHandle.getCallingUserId());
  }
  
  public Bundle getApplicationRestrictionsForUser(String paramString, int paramInt)
  {
    if ((UserHandle.getCallingUserId() == paramInt) && (UserHandle.isSameApp(Binder.getCallingUid(), getUidForPackage(paramString)))) {}
    synchronized (this.mPackagesLock)
    {
      paramString = readApplicationRestrictionsLP(paramString, paramInt);
      return paramString;
      checkSystemOrRoot("get application restrictions for other users/apps");
    }
  }
  
  public int getCredentialOwnerProfile(int paramInt)
  {
    checkManageUsersPermission("get the credential owner");
    if (!this.mLockPatternUtils.isSeparateProfileChallengeEnabled(paramInt)) {}
    synchronized (this.mUsersLock)
    {
      UserInfo localUserInfo = getProfileParentLU(paramInt);
      if (localUserInfo != null)
      {
        paramInt = localUserInfo.id;
        return paramInt;
      }
      return paramInt;
    }
  }
  
  public Bundle getDefaultGuestRestrictions()
  {
    checkManageUsersPermission("getDefaultGuestRestrictions");
    synchronized (this.mGuestRestrictions)
    {
      Bundle localBundle2 = new Bundle(this.mGuestRestrictions);
      return localBundle2;
    }
  }
  
  public UserInfo getPrimaryUser()
  {
    checkManageUsersPermission("query users");
    synchronized (this.mUsersLock)
    {
      int j = this.mUsers.size();
      int i = 0;
      while (i < j)
      {
        UserInfo localUserInfo = ((UserData)this.mUsers.valueAt(i)).info;
        if (localUserInfo.isPrimary())
        {
          boolean bool = this.mRemovingUserIds.get(localUserInfo.id);
          if (!bool) {}
        }
        else
        {
          i += 1;
          continue;
        }
        return localUserInfo;
      }
      return null;
    }
  }
  
  /* Error */
  public int[] getProfileIds(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: iload_1
    //   1: invokestatic 624	android/os/UserHandle:getCallingUserId	()I
    //   4: if_icmpeq +26 -> 30
    //   7: new 347	java/lang/StringBuilder
    //   10: dup
    //   11: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   14: ldc_w 1788
    //   17: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: iload_1
    //   21: invokevirtual 540	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   24: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokestatic 1577	com/android/server/pm/UserManagerService:checkManageUsersPermission	(Ljava/lang/String;)V
    //   30: invokestatic 643	android/os/Binder:clearCallingIdentity	()J
    //   33: lstore_3
    //   34: aload_0
    //   35: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   38: astore 5
    //   40: aload 5
    //   42: monitorenter
    //   43: aload_0
    //   44: iload_1
    //   45: iload_2
    //   46: invokespecial 940	com/android/server/pm/UserManagerService:getProfileIdsLU	(IZ)Landroid/util/IntArray;
    //   49: invokevirtual 1791	android/util/IntArray:toArray	()[I
    //   52: astore 6
    //   54: aload 5
    //   56: monitorexit
    //   57: lload_3
    //   58: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   61: aload 6
    //   63: areturn
    //   64: astore 6
    //   66: aload 5
    //   68: monitorexit
    //   69: aload 6
    //   71: athrow
    //   72: astore 5
    //   74: lload_3
    //   75: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   78: aload 5
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	UserManagerService
    //   0	81	1	paramInt	int
    //   0	81	2	paramBoolean	boolean
    //   33	42	3	l	long
    //   72	7	5	localObject2	Object
    //   52	10	6	arrayOfInt	int[]
    //   64	6	6	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   43	54	64	finally
    //   34	43	72	finally
    //   54	57	72	finally
    //   66	72	72	finally
  }
  
  public UserInfo getProfileParent(int paramInt)
  {
    checkManageUsersPermission("get the profile parent");
    synchronized (this.mUsersLock)
    {
      UserInfo localUserInfo = getProfileParentLU(paramInt);
      return localUserInfo;
    }
  }
  
  /* Error */
  public List<UserInfo> getProfiles(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: iload_1
    //   3: invokestatic 624	android/os/UserHandle:getCallingUserId	()I
    //   6: if_icmpeq +60 -> 66
    //   9: new 347	java/lang/StringBuilder
    //   12: dup
    //   13: invokespecial 350	java/lang/StringBuilder:<init>	()V
    //   16: ldc_w 1788
    //   19: invokevirtual 356	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: iload_1
    //   23: invokevirtual 540	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   26: invokevirtual 365	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   29: invokestatic 1618	com/android/server/pm/UserManagerService:checkManageOrCreateUsersPermission	(Ljava/lang/String;)V
    //   32: invokestatic 643	android/os/Binder:clearCallingIdentity	()J
    //   35: lstore 4
    //   37: aload_0
    //   38: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   41: astore 6
    //   43: aload 6
    //   45: monitorenter
    //   46: aload_0
    //   47: iload_1
    //   48: iload_2
    //   49: iload_3
    //   50: invokespecial 1796	com/android/server/pm/UserManagerService:getProfilesLU	(IZZ)Ljava/util/List;
    //   53: astore 7
    //   55: aload 6
    //   57: monitorexit
    //   58: lload 4
    //   60: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   63: aload 7
    //   65: areturn
    //   66: invokestatic 546	com/android/server/pm/UserManagerService:hasManageUsersPermission	()Z
    //   69: istore_3
    //   70: goto -38 -> 32
    //   73: astore 7
    //   75: aload 6
    //   77: monitorexit
    //   78: aload 7
    //   80: athrow
    //   81: astore 6
    //   83: lload 4
    //   85: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   88: aload 6
    //   90: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	91	0	this	UserManagerService
    //   0	91	1	paramInt	int
    //   0	91	2	paramBoolean	boolean
    //   1	69	3	bool	boolean
    //   35	49	4	l	long
    //   81	8	6	localObject2	Object
    //   53	11	7	localList	List
    //   73	6	7	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   46	55	73	finally
    //   37	46	81	finally
    //   55	58	81	finally
    //   75	81	81	finally
  }
  
  public String getSeedAccountName()
    throws RemoteException
  {
    checkManageUsersPermission("Cannot get seed account information");
    synchronized (this.mUsersLock)
    {
      String str = getUserDataLU(UserHandle.getCallingUserId()).seedAccountName;
      return str;
    }
  }
  
  public PersistableBundle getSeedAccountOptions()
    throws RemoteException
  {
    checkManageUsersPermission("Cannot get seed account information");
    synchronized (this.mUsersLock)
    {
      PersistableBundle localPersistableBundle = getUserDataLU(UserHandle.getCallingUserId()).seedAccountOptions;
      return localPersistableBundle;
    }
  }
  
  public String getSeedAccountType()
    throws RemoteException
  {
    checkManageUsersPermission("Cannot get seed account information");
    synchronized (this.mUsersLock)
    {
      String str = getUserDataLU(UserHandle.getCallingUserId()).seedAccountType;
      return str;
    }
  }
  
  public String getUserAccount(int paramInt)
  {
    checkManageUserAndAcrossUsersFullPermission("get user account");
    synchronized (this.mUsersLock)
    {
      String str = ((UserData)this.mUsers.get(paramInt)).account;
      return str;
    }
  }
  
  public long getUserCreationTime(int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    Object localObject3 = null;
    Object localObject4 = this.mUsersLock;
    if (i == paramInt) {}
    for (;;)
    {
      try
      {
        localObject1 = getUserInfoLU(paramInt);
        if (localObject1 != null) {
          break;
        }
        throw new SecurityException("userHandle can only be the calling user or a managed profile associated with this user");
      }
      finally {}
      UserInfo localUserInfo = getProfileParentLU(paramInt);
      Object localObject1 = localObject3;
      if (localUserInfo != null)
      {
        localObject1 = localObject3;
        if (localUserInfo.id == i) {
          localObject1 = getUserInfoLU(paramInt);
        }
      }
    }
    return ((UserInfo)localObject2).creationTime;
  }
  
  public int getUserHandle(int paramInt)
  {
    synchronized (this.mUsersLock)
    {
      int[] arrayOfInt = this.mUserIds;
      int i = 0;
      int j = arrayOfInt.length;
      while (i < j)
      {
        int k = arrayOfInt[i];
        UserInfo localUserInfo = getUserInfoLU(k);
        if (localUserInfo != null)
        {
          int m = localUserInfo.serialNumber;
          if (m == paramInt) {
            return k;
          }
        }
        i += 1;
      }
      return -1;
    }
  }
  
  public ParcelFileDescriptor getUserIcon(int paramInt)
  {
    for (;;)
    {
      int j;
      int i;
      synchronized (this.mPackagesLock)
      {
        UserInfo localUserInfo = getUserInfoNoChecks(paramInt);
        if ((localUserInfo == null) || (localUserInfo.partial))
        {
          Slog.w("UserManagerService", "getUserIcon: unknown user #" + paramInt);
          return null;
        }
        j = UserHandle.getCallingUserId();
        i = getUserInfoNoChecks(j).profileGroupId;
        int k = localUserInfo.profileGroupId;
        if (i != 55536)
        {
          if (i == k)
          {
            i = 1;
            break label187;
            String str2 = localUserInfo.iconPath;
            if (str2 == null) {
              return null;
            }
          }
          else
          {
            i = 0;
            break label187;
          }
        }
        else
        {
          i = 0;
          break label187;
          checkManageUsersPermission("get the icon of a user who is not related");
        }
      }
      String str1 = ((UserInfo)localObject2).iconPath;
      try
      {
        ??? = ParcelFileDescriptor.open(new File(str1), 268435456);
        return (ParcelFileDescriptor)???;
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        Log.e("UserManagerService", "Couldn't find icon file", localFileNotFoundException);
        return null;
      }
      label187:
      if (j != paramInt) {
        if (i == 0) {}
      }
    }
  }
  
  public int[] getUserIds()
  {
    synchronized (this.mUsersLock)
    {
      int[] arrayOfInt = this.mUserIds;
      return arrayOfInt;
    }
  }
  
  public UserInfo getUserInfo(int paramInt)
  {
    checkManageOrCreateUsersPermission("query user");
    synchronized (this.mUsersLock)
    {
      UserInfo localUserInfo = userWithName(getUserInfoLU(paramInt));
      return localUserInfo;
    }
  }
  
  public int getUserRestrictionSource(String paramString, int paramInt)
  {
    checkManageUsersPermission("getUserRestrictionSource");
    int j = 0;
    if (!hasUserRestriction(paramString, paramInt)) {
      return 0;
    }
    if (hasBaseUserRestriction(paramString, paramInt)) {
      j = 1;
    }
    synchronized (this.mRestrictionsLock)
    {
      Bundle localBundle = (Bundle)this.mDevicePolicyLocalUserRestrictions.get(paramInt);
      int i = j;
      if (!UserRestrictionsUtils.isEmpty(localBundle))
      {
        i = j;
        if (localBundle.getBoolean(paramString))
        {
          if (this.mGlobalRestrictionOwnerUserId != paramInt) {
            break label128;
          }
          i = j | 0x2;
        }
      }
      paramInt = i;
      if (!UserRestrictionsUtils.isEmpty(this.mDevicePolicyGlobalUserRestrictions))
      {
        boolean bool = this.mDevicePolicyGlobalUserRestrictions.getBoolean(paramString);
        paramInt = i;
        if (bool) {
          paramInt = i | 0x2;
        }
      }
      return paramInt;
      label128:
      i = j | 0x4;
    }
  }
  
  public Bundle getUserRestrictions(int paramInt)
  {
    return UserRestrictionsUtils.clone(getEffectiveUserRestrictions(paramInt));
  }
  
  public int getUserSerialNumber(int paramInt)
  {
    synchronized (this.mUsersLock)
    {
      boolean bool = exists(paramInt);
      if (!bool) {
        return -1;
      }
      paramInt = getUserInfoLU(paramInt).serialNumber;
      return paramInt;
    }
  }
  
  public List<UserInfo> getUsers(boolean paramBoolean)
  {
    checkManageOrCreateUsersPermission("query users");
    for (;;)
    {
      int i;
      synchronized (this.mUsersLock)
      {
        ArrayList localArrayList = new ArrayList(this.mUsers.size());
        int j = this.mUsers.size();
        i = 0;
        if (i < j)
        {
          UserInfo localUserInfo = ((UserData)this.mUsers.valueAt(i)).info;
          if ((localUserInfo.partial) || ((paramBoolean) && (this.mRemovingUserIds.get(localUserInfo.id)))) {
            break label121;
          }
          localArrayList.add(userWithName(localUserInfo));
        }
      }
      return localList;
      label121:
      i += 1;
    }
  }
  
  public boolean hasBaseUserRestriction(String paramString, int paramInt)
  {
    boolean bool = false;
    checkManageUsersPermission("hasBaseUserRestriction");
    if (!UserRestrictionsUtils.isValidRestriction(paramString)) {
      return false;
    }
    synchronized (this.mRestrictionsLock)
    {
      Bundle localBundle = (Bundle)this.mBaseUserRestrictions.get(paramInt);
      if (localBundle != null) {
        bool = localBundle.getBoolean(paramString, false);
      }
      return bool;
    }
  }
  
  public boolean hasUserRestriction(String paramString, int paramInt)
  {
    boolean bool = false;
    if (!UserRestrictionsUtils.isValidRestriction(paramString)) {
      return false;
    }
    Bundle localBundle = getEffectiveUserRestrictions(paramInt);
    if (localBundle != null) {
      bool = localBundle.getBoolean(paramString);
    }
    return bool;
  }
  
  public boolean isDemoUser(int paramInt)
  {
    if ((UserHandle.getCallingUserId() == paramInt) || (hasManageUsersPermission())) {}
    synchronized (this.mUsersLock)
    {
      UserInfo localUserInfo = getUserInfoLU(paramInt);
      if (localUserInfo != null)
      {
        bool = localUserInfo.isDemo();
        return bool;
        throw new SecurityException("You need MANAGE_USERS permission to query if u=" + paramInt + " is a demo user");
      }
      boolean bool = false;
    }
  }
  
  boolean isInitialized(int paramInt)
  {
    boolean bool = false;
    if ((getUserInfo(paramInt).flags & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isManagedProfile(int paramInt)
  {
    int i = UserHandle.getCallingUserId();
    if ((i == paramInt) || (hasManageUsersPermission())) {}
    synchronized (this.mUsersLock)
    {
      for (;;)
      {
        UserInfo localUserInfo = getUserInfoLU(paramInt);
        if (localUserInfo == null) {
          break;
        }
        bool = localUserInfo.isManagedProfile();
        return bool;
        synchronized (this.mPackagesLock)
        {
          if (!isSameProfileGroupLP(i, paramInt)) {
            throw new SecurityException("You need MANAGE_USERS permission to: check if specified user a managed profile outside your profile group");
          }
        }
      }
      boolean bool = false;
    }
  }
  
  public boolean isQuietModeEnabled(int paramInt)
  {
    synchronized (this.mPackagesLock)
    {
      synchronized (this.mUsersLock)
      {
        UserInfo localUserInfo = getUserInfoLU(paramInt);
        if ((localUserInfo != null) && (localUserInfo.isManagedProfile()))
        {
          boolean bool = localUserInfo.isQuietModeEnabled();
          return bool;
        }
      }
    }
    return false;
  }
  
  public boolean isRestricted()
  {
    synchronized (this.mUsersLock)
    {
      boolean bool = getUserInfoLU(UserHandle.getCallingUserId()).isRestricted();
      return bool;
    }
  }
  
  public boolean isSameProfileGroup(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return true;
    }
    checkManageUsersPermission("check if in the same profile group");
    synchronized (this.mPackagesLock)
    {
      boolean bool = isSameProfileGroupLP(paramInt1, paramInt2);
      return bool;
    }
  }
  
  public void makeInitialized(int paramInt)
  {
    checkManageUsersPermission("makeInitialized");
    int i = 0;
    synchronized (this.mUsersLock)
    {
      UserData localUserData = (UserData)this.mUsers.get(paramInt);
      if ((localUserData == null) || (localUserData.info.partial))
      {
        Slog.w("UserManagerService", "makeInitialized: unknown user #" + paramInt);
        return;
      }
      paramInt = i;
      if ((localUserData.info.flags & 0x10) == 0)
      {
        UserInfo localUserInfo = localUserData.info;
        localUserInfo.flags |= 0x10;
        paramInt = 1;
      }
      if (paramInt != 0) {
        scheduleWriteUser(localUserData);
      }
      return;
    }
  }
  
  public boolean markGuestForDeletion(int paramInt)
  {
    checkManageUsersPermission("Only the system can remove users");
    if (getUserRestrictions(UserHandle.getCallingUserId()).getBoolean("no_remove_user", false))
    {
      Log.w("UserManagerService", "Cannot remove user. DISALLOW_REMOVE_USER is enabled.");
      return false;
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      synchronized (this.mPackagesLock)
      {
        synchronized (this.mUsersLock)
        {
          UserData localUserData1 = (UserData)this.mUsers.get(paramInt);
          if ((paramInt == 0) || (localUserData1 == null)) {}
          do
          {
            return false;
            bool = this.mRemovingUserIds.get(paramInt);
          } while (bool);
          boolean bool = localUserData1.info.isGuest();
          if (!bool) {
            return false;
          }
        }
      }
      localUserData2.info.guestToRemove = true;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    UserInfo localUserInfo = localUserData2.info;
    localUserInfo.flags |= 0x40;
    writeUserLP(localUserData2);
    Binder.restoreCallingIdentity(l);
    return true;
  }
  
  public void onBeforeStartUser(int paramInt)
  {
    int i = getUserSerialNumber(paramInt);
    this.mPm.prepareUserData(paramInt, i, 1);
    this.mPm.reconcileAppsData(paramInt, 1);
    if (paramInt != 0) {}
    synchronized (this.mRestrictionsLock)
    {
      applyUserRestrictionsLR(paramInt);
      maybeInitializeDemoMode(paramInt);
      return;
    }
  }
  
  public void onBeforeUnlockUser(int paramInt)
  {
    int i = getUserSerialNumber(paramInt);
    this.mPm.prepareUserData(paramInt, i, 2);
    this.mPm.reconcileAppsData(paramInt, 2);
  }
  
  int onShellCommand(Shell paramShell, String paramString)
  {
    if (paramString == null) {
      return paramShell.handleDefaultCommands(paramString);
    }
    paramShell = paramShell.getOutPrintWriter();
    try
    {
      if (paramString.equals("list"))
      {
        int i = runList(paramShell);
        return i;
      }
    }
    catch (RemoteException paramString)
    {
      paramShell.println("Remote exception: " + paramString);
    }
    return -1;
  }
  
  public void onShellCommand(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, FileDescriptor paramFileDescriptor3, String[] paramArrayOfString, ResultReceiver paramResultReceiver)
  {
    new Shell(null).exec(this, paramFileDescriptor1, paramFileDescriptor2, paramFileDescriptor3, paramArrayOfString, paramResultReceiver);
  }
  
  public void onUserLoggedIn(int paramInt)
  {
    UserData localUserData = getUserDataNoChecks(paramInt);
    if ((localUserData == null) || (localUserData.info.partial))
    {
      Slog.w("UserManagerService", "userForeground: unknown user #" + paramInt);
      return;
    }
    long l = System.currentTimeMillis();
    if (l > 946080000000L) {
      localUserData.info.lastLoggedInTime = l;
    }
    localUserData.info.lastLoggedInFingerprint = Build.FINGERPRINT;
    scheduleWriteUser(localUserData);
  }
  
  /* Error */
  public boolean removeUser(int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: ldc_w 1883
    //   5: invokestatic 1618	com/android/server/pm/UserManagerService:checkManageOrCreateUsersPermission	(Ljava/lang/String;)V
    //   8: aload_0
    //   9: invokestatic 624	android/os/UserHandle:getCallingUserId	()I
    //   12: invokevirtual 1885	com/android/server/pm/UserManagerService:getUserRestrictions	(I)Landroid/os/Bundle;
    //   15: ldc_w 1887
    //   18: iconst_0
    //   19: invokevirtual 1852	android/os/Bundle:getBoolean	(Ljava/lang/String;Z)Z
    //   22: ifeq +14 -> 36
    //   25: ldc 122
    //   27: ldc_w 1889
    //   30: invokestatic 636	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   33: pop
    //   34: iconst_0
    //   35: ireturn
    //   36: invokestatic 643	android/os/Binder:clearCallingIdentity	()J
    //   39: lstore_3
    //   40: invokestatic 1929	android/app/ActivityManager:getCurrentUser	()I
    //   43: iload_1
    //   44: if_icmpne +18 -> 62
    //   47: ldc 122
    //   49: ldc_w 1931
    //   52: invokestatic 636	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   55: pop
    //   56: lload_3
    //   57: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   60: iconst_0
    //   61: ireturn
    //   62: aload_0
    //   63: getfield 272	com/android/server/pm/UserManagerService:mPackagesLock	Ljava/lang/Object;
    //   66: astore 5
    //   68: aload 5
    //   70: monitorenter
    //   71: aload_0
    //   72: getfield 257	com/android/server/pm/UserManagerService:mUsersLock	Ljava/lang/Object;
    //   75: astore 7
    //   77: aload 7
    //   79: monitorenter
    //   80: aload_0
    //   81: getfield 389	com/android/server/pm/UserManagerService:mUsers	Landroid/util/SparseArray;
    //   84: iload_1
    //   85: invokevirtual 598	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   88: checkcast 34	com/android/server/pm/UserManagerService$UserData
    //   91: astore 6
    //   93: iload_1
    //   94: ifeq +8 -> 102
    //   97: aload 6
    //   99: ifnonnull +15 -> 114
    //   102: aload 7
    //   104: monitorexit
    //   105: aload 5
    //   107: monitorexit
    //   108: lload_3
    //   109: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   112: iconst_0
    //   113: ireturn
    //   114: aload_0
    //   115: getfield 407	com/android/server/pm/UserManagerService:mRemovingUserIds	Landroid/util/SparseBooleanArray;
    //   118: iload_1
    //   119: invokevirtual 899	android/util/SparseBooleanArray:get	(I)Z
    //   122: ifne -20 -> 102
    //   125: aload_0
    //   126: getfield 407	com/android/server/pm/UserManagerService:mRemovingUserIds	Landroid/util/SparseBooleanArray;
    //   129: iload_1
    //   130: iconst_1
    //   131: invokevirtual 1933	android/util/SparseBooleanArray:put	(IZ)V
    //   134: aload 7
    //   136: monitorexit
    //   137: aload_0
    //   138: getfield 248	com/android/server/pm/UserManagerService:mAppOpsService	Lcom/android/internal/app/IAppOpsService;
    //   141: iload_1
    //   142: invokeinterface 1937 2 0
    //   147: aload 6
    //   149: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   152: iconst_1
    //   153: putfield 722	android/content/pm/UserInfo:partial	Z
    //   156: aload 6
    //   158: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   161: astore 7
    //   163: aload 7
    //   165: aload 7
    //   167: getfield 1426	android/content/pm/UserInfo:flags	I
    //   170: bipush 64
    //   172: ior
    //   173: putfield 1426	android/content/pm/UserInfo:flags	I
    //   176: aload_0
    //   177: aload 6
    //   179: invokespecial 344	com/android/server/pm/UserManagerService:writeUserLP	(Lcom/android/server/pm/UserManagerService$UserData;)V
    //   182: aload 5
    //   184: monitorexit
    //   185: aload 6
    //   187: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   190: getfield 739	android/content/pm/UserInfo:profileGroupId	I
    //   193: sipush 55536
    //   196: if_icmpeq +34 -> 230
    //   199: aload 6
    //   201: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   204: invokevirtual 1866	android/content/pm/UserInfo:isManagedProfile	()Z
    //   207: ifeq +23 -> 230
    //   210: aload_0
    //   211: aload 6
    //   213: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   216: getfield 739	android/content/pm/UserInfo:profileGroupId	I
    //   219: aload 6
    //   221: getfield 679	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
    //   224: getfield 741	android/content/pm/UserInfo:id	I
    //   227: invokespecial 1939	com/android/server/pm/UserManagerService:sendProfileRemovedBroadcast	(II)V
    //   230: invokestatic 1342	android/app/ActivityManagerNative:getDefault	()Landroid/app/IActivityManager;
    //   233: iload_1
    //   234: iconst_1
    //   235: new 14	com/android/server/pm/UserManagerService$5
    //   238: dup
    //   239: aload_0
    //   240: invokespecial 1940	com/android/server/pm/UserManagerService$5:<init>	(Lcom/android/server/pm/UserManagerService;)V
    //   243: invokeinterface 1944 4 0
    //   248: istore_1
    //   249: iload_1
    //   250: ifne +58 -> 308
    //   253: lload_3
    //   254: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   257: iload_2
    //   258: ireturn
    //   259: astore 6
    //   261: aload 7
    //   263: monitorexit
    //   264: aload 6
    //   266: athrow
    //   267: astore 6
    //   269: aload 5
    //   271: monitorexit
    //   272: aload 6
    //   274: athrow
    //   275: astore 5
    //   277: lload_3
    //   278: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   281: aload 5
    //   283: athrow
    //   284: astore 7
    //   286: ldc 122
    //   288: ldc_w 1946
    //   291: aload 7
    //   293: invokestatic 1155	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   296: pop
    //   297: goto -150 -> 147
    //   300: astore 5
    //   302: lload_3
    //   303: invokestatic 650	android/os/Binder:restoreCallingIdentity	(J)V
    //   306: iconst_0
    //   307: ireturn
    //   308: iconst_0
    //   309: istore_2
    //   310: goto -57 -> 253
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	313	0	this	UserManagerService
    //   0	313	1	paramInt	int
    //   1	309	2	bool	boolean
    //   39	264	3	l	long
    //   275	7	5	localObject2	Object
    //   300	1	5	localRemoteException1	RemoteException
    //   91	129	6	localUserData	UserData
    //   259	6	6	localObject3	Object
    //   267	6	6	localObject4	Object
    //   284	8	7	localRemoteException2	RemoteException
    // Exception table:
    //   from	to	target	type
    //   80	93	259	finally
    //   114	134	259	finally
    //   71	80	267	finally
    //   102	105	267	finally
    //   134	137	267	finally
    //   137	147	267	finally
    //   147	182	267	finally
    //   261	267	267	finally
    //   286	297	267	finally
    //   40	56	275	finally
    //   62	71	275	finally
    //   105	108	275	finally
    //   182	230	275	finally
    //   230	249	275	finally
    //   269	275	275	finally
    //   137	147	284	android/os/RemoteException
    //   230	249	300	android/os/RemoteException
  }
  
  /* Error */
  public void setApplicationRestrictions(String paramString, Bundle paramBundle, int paramInt)
  {
    // Byte code:
    //   0: ldc_w 1949
    //   3: invokestatic 1768	com/android/server/pm/UserManagerService:checkSystemOrRoot	(Ljava/lang/String;)V
    //   6: aload_2
    //   7: ifnull +8 -> 15
    //   10: aload_2
    //   11: iconst_1
    //   12: invokevirtual 1952	android/os/Bundle:setDefusable	(Z)V
    //   15: aload_0
    //   16: getfield 272	com/android/server/pm/UserManagerService:mPackagesLock	Ljava/lang/Object;
    //   19: astore 4
    //   21: aload 4
    //   23: monitorenter
    //   24: aload_2
    //   25: ifnull +10 -> 35
    //   28: aload_2
    //   29: invokevirtual 1021	android/os/Bundle:isEmpty	()Z
    //   32: ifeq +50 -> 82
    //   35: aload_0
    //   36: aload_1
    //   37: iload_3
    //   38: invokespecial 1954	com/android/server/pm/UserManagerService:cleanAppRestrictionsForPackage	(Ljava/lang/String;I)V
    //   41: aload 4
    //   43: monitorexit
    //   44: new 487	android/content/Intent
    //   47: dup
    //   48: ldc_w 1956
    //   51: invokespecial 775	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   54: astore_2
    //   55: aload_2
    //   56: aload_1
    //   57: invokevirtual 1959	android/content/Intent:setPackage	(Ljava/lang/String;)Landroid/content/Intent;
    //   60: pop
    //   61: aload_2
    //   62: ldc_w 517
    //   65: invokevirtual 521	android/content/Intent:addFlags	(I)Landroid/content/Intent;
    //   68: pop
    //   69: aload_0
    //   70: getfield 261	com/android/server/pm/UserManagerService:mContext	Landroid/content/Context;
    //   73: aload_2
    //   74: iload_3
    //   75: invokestatic 1963	android/os/UserHandle:of	(I)Landroid/os/UserHandle;
    //   78: invokevirtual 527	android/content/Context:sendBroadcastAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   81: return
    //   82: aload_0
    //   83: aload_1
    //   84: aload_2
    //   85: iload_3
    //   86: invokespecial 1965	com/android/server/pm/UserManagerService:writeApplicationRestrictionsLP	(Ljava/lang/String;Landroid/os/Bundle;I)V
    //   89: goto -48 -> 41
    //   92: astore_1
    //   93: aload 4
    //   95: monitorexit
    //   96: aload_1
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	UserManagerService
    //   0	98	1	paramString	String
    //   0	98	2	paramBundle	Bundle
    //   0	98	3	paramInt	int
    //   19	75	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   28	35	92	finally
    //   35	41	92	finally
    //   82	89	92	finally
  }
  
  public void setDefaultGuestRestrictions(Bundle arg1)
  {
    checkManageUsersPermission("setDefaultGuestRestrictions");
    synchronized (this.mGuestRestrictions)
    {
      this.mGuestRestrictions.clear();
      this.mGuestRestrictions.putAll(???);
    }
    synchronized (this.mPackagesLock)
    {
      writeUserListLP();
      return;
      ??? = finally;
      throw ???;
    }
  }
  
  void setDevicePolicyUserRestrictionsInner(int paramInt, Bundle paramBundle1, Bundle paramBundle2)
  {
    Preconditions.checkNotNull(paramBundle1);
    int j = 0;
    Object localObject = this.mRestrictionsLock;
    if (paramBundle2 != null) {}
    for (;;)
    {
      try
      {
        if (UserRestrictionsUtils.areEqual(this.mDevicePolicyGlobalUserRestrictions, paramBundle2))
        {
          i = 0;
          if (i != 0) {
            this.mDevicePolicyGlobalUserRestrictions = paramBundle2;
          }
          this.mGlobalRestrictionOwnerUserId = paramInt;
          if (!UserRestrictionsUtils.areEqual((Bundle)this.mDevicePolicyLocalUserRestrictions.get(paramInt), paramBundle1)) {
            break label177;
          }
          j = 0;
          if (j != 0) {
            this.mDevicePolicyLocalUserRestrictions.put(paramInt, paramBundle1);
          }
          paramBundle1 = this.mPackagesLock;
          if (j == 0) {}
        }
      }
      finally {}
      try
      {
        writeUserLP(getUserDataNoChecks(paramInt));
        if (i != 0) {
          writeUserListLP();
        }
        paramBundle1 = this.mRestrictionsLock;
        if (i == 0) {}
      }
      finally {}
      try
      {
        applyUserRestrictionsForAllUsersLR();
        return;
      }
      finally {}
      int i = 1;
      continue;
      i = j;
      if (this.mGlobalRestrictionOwnerUserId == paramInt)
      {
        this.mGlobalRestrictionOwnerUserId = 55536;
        i = j;
        continue;
        label177:
        j = 1;
        continue;
        if (j != 0) {
          applyUserRestrictionsLR(paramInt);
        }
      }
    }
  }
  
  public void setQuietModeEnabled(int paramInt, boolean paramBoolean)
  {
    checkManageUsersPermission("silence profile");
    int i = 0;
    for (;;)
    {
      synchronized (this.mPackagesLock)
      {
        synchronized (this.mUsersLock)
        {
          UserInfo localUserInfo1 = getUserInfoLU(paramInt);
          UserInfo localUserInfo2 = getProfileParentLU(paramInt);
          if ((localUserInfo1 != null) && (localUserInfo1.isManagedProfile()))
          {
            if (localUserInfo1.isQuietModeEnabled() != paramBoolean)
            {
              localUserInfo1.flags ^= 0x80;
              writeUserLP(getUserDataLU(localUserInfo1.id));
              i = 1;
            }
            if (i != 0)
            {
              l = Binder.clearCallingIdentity();
              if (!paramBoolean) {
                break label206;
              }
            }
          }
          try
          {
            ActivityManagerNative.getDefault().stopUser(paramInt, true, null);
            ((ActivityManagerInternal)LocalServices.getService(ActivityManagerInternal.class)).killForegroundAppsForUser(paramInt);
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("UserManagerService", "fail to start/stop user for quiet mode", localRemoteException);
            Binder.restoreCallingIdentity(l);
            continue;
          }
          finally
          {
            Binder.restoreCallingIdentity(l);
          }
          broadcastProfileAvailabilityChanges(localUserInfo1.getUserHandle(), localUserInfo2.getUserHandle(), paramBoolean);
          return;
        }
      }
      throw new IllegalArgumentException("User " + paramInt + " is not a profile");
      label206:
      ActivityManagerNative.getDefault().startUserInBackground(paramInt);
    }
  }
  
  public void setSeedAccountData(int paramInt, String paramString1, String paramString2, PersistableBundle paramPersistableBundle, boolean paramBoolean)
  {
    checkManageUsersPermission("Require MANAGE_USERS permission to set user seed data");
    synchronized (this.mPackagesLock)
    {
      synchronized (this.mUsersLock)
      {
        UserData localUserData = getUserDataLU(paramInt);
        if (localUserData == null)
        {
          Slog.e("UserManagerService", "No such user for settings seed data u=" + paramInt);
          return;
        }
        localUserData.seedAccountName = paramString1;
        localUserData.seedAccountType = paramString2;
        localUserData.seedAccountOptions = paramPersistableBundle;
        localUserData.persistSeedData = paramBoolean;
        if (paramBoolean) {
          writeUserLP(localUserData);
        }
        return;
      }
    }
  }
  
  public void setUserAccount(int paramInt, String paramString)
  {
    checkManageUserAndAcrossUsersFullPermission("set user account");
    Object localObject1 = null;
    synchronized (this.mPackagesLock)
    {
      synchronized (this.mUsersLock)
      {
        UserData localUserData = (UserData)this.mUsers.get(paramInt);
        if (localUserData == null)
        {
          Slog.e("UserManagerService", "User not found for setting user account: u" + paramInt);
          return;
        }
        if (!Objects.equal(localUserData.account, paramString))
        {
          localUserData.account = paramString;
          localObject1 = localUserData;
        }
        if (localObject1 != null) {
          writeUserLP((UserData)localObject1);
        }
        return;
      }
    }
  }
  
  public void setUserEnabled(int paramInt)
  {
    checkManageUsersPermission("enable user");
    for (;;)
    {
      synchronized (this.mPackagesLock)
      {
        synchronized (this.mUsersLock)
        {
          UserInfo localUserInfo = getUserInfoLU(paramInt);
          if (localUserInfo != null)
          {
            boolean bool = localUserInfo.isEnabled();
            if (!bool) {}
          }
          else
          {
            return;
          }
        }
      }
      ((UserInfo)localObject4).flags ^= 0x40;
      writeUserLP(getUserDataLU(((UserInfo)localObject4).id));
    }
  }
  
  public void setUserIcon(int paramInt, Bitmap paramBitmap)
  {
    checkManageUsersPermission("update users");
    if (hasUserRestriction("no_set_user_icon", paramInt))
    {
      Log.w("UserManagerService", "Cannot set user icon. DISALLOW_SET_USER_ICON is enabled.");
      return;
    }
    this.mLocalService.setUserIcon(paramInt, paramBitmap);
  }
  
  public void setUserName(int paramInt, String paramString)
  {
    checkManageUsersPermission("rename users");
    int j = 0;
    synchronized (this.mPackagesLock)
    {
      UserData localUserData = getUserDataNoChecks(paramInt);
      if ((localUserData == null) || (localUserData.info.partial))
      {
        Slog.w("UserManagerService", "setUserName: unknown user #" + paramInt);
        return;
      }
      int i = j;
      if (paramString != null)
      {
        boolean bool = paramString.equals(localUserData.info.name);
        if (bool) {
          i = j;
        }
      }
      else
      {
        if (i != 0) {
          sendUserInfoChangedBroadcast(paramInt);
        }
        return;
      }
      localUserData.info.name = paramString;
      writeUserLP(localUserData);
      i = 1;
    }
  }
  
  public void setUserRestriction(String paramString, boolean paramBoolean, int paramInt)
  {
    checkManageUsersPermission("setUserRestriction");
    if (!UserRestrictionsUtils.isValidRestriction(paramString)) {
      return;
    }
    synchronized (this.mRestrictionsLock)
    {
      Bundle localBundle = UserRestrictionsUtils.clone((Bundle)this.mBaseUserRestrictions.get(paramInt));
      localBundle.putBoolean(paramString, paramBoolean);
      updateUserRestrictionsInternalLR(localBundle, paramInt);
      return;
    }
  }
  
  public boolean someUserHasSeedAccount(String paramString1, String paramString2)
    throws RemoteException
  {
    checkManageUsersPermission("Cannot check seed account information");
    for (;;)
    {
      int i;
      synchronized (this.mUsersLock)
      {
        int j = this.mUsers.size();
        i = 0;
        if (i < j)
        {
          UserData localUserData = (UserData)this.mUsers.valueAt(i);
          if ((!localUserData.info.isInitialized()) && (localUserData.seedAccountName != null) && (localUserData.seedAccountName.equals(paramString1)) && (localUserData.seedAccountType != null))
          {
            boolean bool = localUserData.seedAccountType.equals(paramString2);
            if (bool) {
              return true;
            }
          }
        }
        else
        {
          return false;
        }
      }
      i += 1;
    }
  }
  
  void systemReady()
  {
    this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    for (;;)
    {
      synchronized (this.mRestrictionsLock)
      {
        applyUserRestrictionsLR(0);
        ??? = findCurrentGuestUser();
        if ((??? == null) || (hasUserRestriction("no_config_wifi", ((UserInfo)???).id)))
        {
          this.mContext.registerReceiver(this.mDisableQuietModeCallback, new IntentFilter("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK"), null, this.mHandler);
          return;
        }
      }
      setUserRestriction("no_config_wifi", true, ((UserInfo)???).id);
    }
  }
  
  public boolean trySetQuietModeDisabled(int paramInt, IntentSender paramIntentSender)
  {
    checkManageUsersPermission("silence profile");
    long l;
    if ((!StorageManager.isUserKeyUnlocked(paramInt)) && (this.mLockPatternUtils.isSecure(paramInt))) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      Intent localIntent1 = ((KeyguardManager)this.mContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, paramInt);
      if (localIntent1 == null)
      {
        return false;
        setQuietModeEnabled(paramInt, false);
        return true;
      }
      Intent localIntent2 = new Intent("com.android.server.pm.DISABLE_QUIET_MODE_AFTER_UNLOCK");
      if (paramIntentSender != null) {
        localIntent2.putExtra("android.intent.extra.INTENT", paramIntentSender);
      }
      localIntent2.putExtra("android.intent.extra.USER_ID", paramInt);
      localIntent2.setPackage(this.mContext.getPackageName());
      localIntent2.addFlags(268435456);
      localIntent1.putExtra("android.intent.extra.INTENT", PendingIntent.getBroadcast(this.mContext, 0, localIntent2, 1409286144).getIntentSender());
      localIntent1.setFlags(276824064);
      this.mContext.startActivity(localIntent1);
      return false;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public static class LifeCycle
    extends SystemService
  {
    private UserManagerService mUms;
    
    public LifeCycle(Context paramContext)
    {
      super();
    }
    
    public void onBootPhase(int paramInt)
    {
      if (paramInt == 550) {
        this.mUms.cleanupPartialUsers();
      }
    }
    
    public void onStart()
    {
      this.mUms = UserManagerService.getInstance();
      publishBinderService("user", this.mUms);
    }
  }
  
  private class LocalService
    extends UserManagerInternal
  {
    private LocalService() {}
    
    public void addUserRestrictionsListener(UserManagerInternal.UserRestrictionsListener paramUserRestrictionsListener)
    {
      synchronized (UserManagerService.-get8(UserManagerService.this))
      {
        UserManagerService.-get8(UserManagerService.this).add(paramUserRestrictionsListener);
        return;
      }
    }
    
    public UserInfo createUserEvenWhenDisallowed(String paramString, int paramInt)
    {
      paramString = UserManagerService.-wrap0(UserManagerService.this, paramString, paramInt, 55536);
      if ((paramString == null) || (paramString.isAdmin())) {
        return paramString;
      }
      UserManagerService.this.setUserRestriction("no_sms", true, paramString.id);
      UserManagerService.this.setUserRestriction("no_outgoing_calls", true, paramString.id);
      return paramString;
    }
    
    public Bundle getBaseUserRestrictions(int paramInt)
    {
      synchronized (UserManagerService.-get6(UserManagerService.this))
      {
        Bundle localBundle = (Bundle)UserManagerService.-get1(UserManagerService.this).get(paramInt);
        return localBundle;
      }
    }
    
    public boolean getUserRestriction(int paramInt, String paramString)
    {
      return UserManagerService.this.getUserRestrictions(paramInt).getBoolean(paramString);
    }
    
    public boolean isUserRunning(int paramInt)
    {
      boolean bool = false;
      synchronized (UserManagerService.-get9(UserManagerService.this))
      {
        paramInt = UserManagerService.-get9(UserManagerService.this).get(paramInt, -1);
        if (paramInt >= 0) {
          bool = true;
        }
        return bool;
      }
    }
    
    public boolean isUserUnlockingOrUnlocked(int paramInt)
    {
      boolean bool2 = true;
      synchronized (UserManagerService.-get9(UserManagerService.this))
      {
        paramInt = UserManagerService.-get9(UserManagerService.this).get(paramInt, -1);
        boolean bool1 = bool2;
        if (paramInt != 2)
        {
          if (paramInt == 3) {
            bool1 = bool2;
          }
        }
        else {
          return bool1;
        }
        bool1 = false;
      }
    }
    
    public void onEphemeralUserStop(int paramInt)
    {
      synchronized (UserManagerService.-get10(UserManagerService.this))
      {
        UserInfo localUserInfo = UserManagerService.-wrap1(UserManagerService.this, paramInt);
        if ((localUserInfo != null) && (localUserInfo.isEphemeral()))
        {
          localUserInfo.flags |= 0x40;
          if (localUserInfo.isGuest()) {
            localUserInfo.guestToRemove = true;
          }
        }
        return;
      }
    }
    
    public void removeAllUsers()
    {
      if (ActivityManager.getCurrentUser() == 0)
      {
        UserManagerService.-wrap4(UserManagerService.this);
        return;
      }
      BroadcastReceiver local1 = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          if (paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", 55536) != 0) {
            return;
          }
          UserManagerService.-get2(UserManagerService.this).unregisterReceiver(this);
          UserManagerService.-wrap4(UserManagerService.this);
        }
      };
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.USER_SWITCHED");
      UserManagerService.-get2(UserManagerService.this).registerReceiver(local1, localIntentFilter, null, UserManagerService.-get3(UserManagerService.this));
      ((ActivityManager)UserManagerService.-get2(UserManagerService.this).getSystemService("activity")).switchUser(0);
    }
    
    public void removeUserRestrictionsListener(UserManagerInternal.UserRestrictionsListener paramUserRestrictionsListener)
    {
      synchronized (UserManagerService.-get8(UserManagerService.this))
      {
        UserManagerService.-get8(UserManagerService.this).remove(paramUserRestrictionsListener);
        return;
      }
    }
    
    public void removeUserState(int paramInt)
    {
      synchronized (UserManagerService.-get9(UserManagerService.this))
      {
        UserManagerService.-get9(UserManagerService.this).delete(paramInt);
        return;
      }
    }
    
    public void setBaseUserRestrictionsByDpmsForMigration(int paramInt, Bundle paramBundle)
    {
      for (;;)
      {
        synchronized (UserManagerService.-get6(UserManagerService.this))
        {
          UserManagerService.-get1(UserManagerService.this).put(paramInt, new Bundle(paramBundle));
          UserManagerService.-wrap3(UserManagerService.this, paramInt);
          ??? = UserManagerService.-wrap2(UserManagerService.this, paramInt);
          paramBundle = UserManagerService.-get5(UserManagerService.this);
          if (??? == null) {}
        }
        Slog.w("UserManagerService", "UserInfo not found for " + paramInt);
      }
    }
    
    public void setDeviceManaged(boolean paramBoolean)
    {
      synchronized (UserManagerService.-get10(UserManagerService.this))
      {
        UserManagerService.-set1(UserManagerService.this, paramBoolean);
        return;
      }
    }
    
    public void setDevicePolicyUserRestrictions(int paramInt, Bundle paramBundle1, Bundle paramBundle2)
    {
      UserManagerService.this.setDevicePolicyUserRestrictionsInner(paramInt, paramBundle1, paramBundle2);
    }
    
    public void setForceEphemeralUsers(boolean paramBoolean)
    {
      synchronized (UserManagerService.-get10(UserManagerService.this))
      {
        UserManagerService.-set0(UserManagerService.this, paramBoolean);
        return;
      }
    }
    
    /* Error */
    public void setUserIcon(int paramInt, Bitmap paramBitmap)
    {
      // Byte code:
      //   0: invokestatic 243	android/os/Binder:clearCallingIdentity	()J
      //   3: lstore_3
      //   4: aload_0
      //   5: getfield 15	com/android/server/pm/UserManagerService$LocalService:this$0	Lcom/android/server/pm/UserManagerService;
      //   8: invokestatic 192	com/android/server/pm/UserManagerService:-get5	(Lcom/android/server/pm/UserManagerService;)Ljava/lang/Object;
      //   11: astore 5
      //   13: aload 5
      //   15: monitorenter
      //   16: aload_0
      //   17: getfield 15	com/android/server/pm/UserManagerService$LocalService:this$0	Lcom/android/server/pm/UserManagerService;
      //   20: iload_1
      //   21: invokestatic 189	com/android/server/pm/UserManagerService:-wrap2	(Lcom/android/server/pm/UserManagerService;I)Lcom/android/server/pm/UserManagerService$UserData;
      //   24: astore 6
      //   26: aload 6
      //   28: ifnull +14 -> 42
      //   31: aload 6
      //   33: getfield 249	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
      //   36: getfield 252	android/content/pm/UserInfo:partial	Z
      //   39: ifeq +36 -> 75
      //   42: ldc -58
      //   44: new 200	java/lang/StringBuilder
      //   47: dup
      //   48: invokespecial 201	java/lang/StringBuilder:<init>	()V
      //   51: ldc -2
      //   53: invokevirtual 207	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   56: iload_1
      //   57: invokevirtual 210	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   60: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   63: invokestatic 220	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   66: pop
      //   67: aload 5
      //   69: monitorexit
      //   70: lload_3
      //   71: invokestatic 258	android/os/Binder:restoreCallingIdentity	(J)V
      //   74: return
      //   75: aload_0
      //   76: getfield 15	com/android/server/pm/UserManagerService$LocalService:this$0	Lcom/android/server/pm/UserManagerService;
      //   79: aload 6
      //   81: getfield 249	com/android/server/pm/UserManagerService$UserData:info	Landroid/content/pm/UserInfo;
      //   84: aload_2
      //   85: invokestatic 262	com/android/server/pm/UserManagerService:-wrap7	(Lcom/android/server/pm/UserManagerService;Landroid/content/pm/UserInfo;Landroid/graphics/Bitmap;)V
      //   88: aload_0
      //   89: getfield 15	com/android/server/pm/UserManagerService$LocalService:this$0	Lcom/android/server/pm/UserManagerService;
      //   92: aload 6
      //   94: invokestatic 196	com/android/server/pm/UserManagerService:-wrap8	(Lcom/android/server/pm/UserManagerService;Lcom/android/server/pm/UserManagerService$UserData;)V
      //   97: aload 5
      //   99: monitorexit
      //   100: aload_0
      //   101: getfield 15	com/android/server/pm/UserManagerService$LocalService:this$0	Lcom/android/server/pm/UserManagerService;
      //   104: iload_1
      //   105: invokestatic 265	com/android/server/pm/UserManagerService:-wrap6	(Lcom/android/server/pm/UserManagerService;I)V
      //   108: lload_3
      //   109: invokestatic 258	android/os/Binder:restoreCallingIdentity	(J)V
      //   112: return
      //   113: astore_2
      //   114: aload 5
      //   116: monitorexit
      //   117: aload_2
      //   118: athrow
      //   119: astore_2
      //   120: lload_3
      //   121: invokestatic 258	android/os/Binder:restoreCallingIdentity	(J)V
      //   124: aload_2
      //   125: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	126	0	this	LocalService
      //   0	126	1	paramInt	int
      //   0	126	2	paramBitmap	Bitmap
      //   3	118	3	l	long
      //   24	69	6	localUserData	UserManagerService.UserData
      // Exception table:
      //   from	to	target	type
      //   16	26	113	finally
      //   31	42	113	finally
      //   42	67	113	finally
      //   75	97	113	finally
      //   4	16	119	finally
      //   67	70	119	finally
      //   97	108	119	finally
      //   114	119	119	finally
    }
    
    public void setUserManaged(int paramInt, boolean paramBoolean)
    {
      synchronized (UserManagerService.-get10(UserManagerService.this))
      {
        UserManagerService.-get4(UserManagerService.this).put(paramInt, paramBoolean);
        return;
      }
    }
    
    public void setUserState(int paramInt1, int paramInt2)
    {
      synchronized (UserManagerService.-get9(UserManagerService.this))
      {
        UserManagerService.-get9(UserManagerService.this).put(paramInt1, paramInt2);
        return;
      }
    }
  }
  
  final class MainHandler
    extends Handler
  {
    MainHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      removeMessages(1, paramMessage.obj);
      synchronized (UserManagerService.-get5(UserManagerService.this))
      {
        int i = ((UserManagerService.UserData)paramMessage.obj).info.id;
        paramMessage = UserManagerService.-wrap2(UserManagerService.this, i);
        if (paramMessage != null) {
          UserManagerService.-wrap8(UserManagerService.this, paramMessage);
        }
        return;
      }
    }
  }
  
  private class Shell
    extends ShellCommand
  {
    private Shell() {}
    
    public int onCommand(String paramString)
    {
      return UserManagerService.this.onShellCommand(this, paramString);
    }
    
    public void onHelp()
    {
      PrintWriter localPrintWriter = getOutPrintWriter();
      localPrintWriter.println("User manager (user) commands:");
      localPrintWriter.println("  help");
      localPrintWriter.println("    Print this help text.");
      localPrintWriter.println("");
      localPrintWriter.println("  list");
      localPrintWriter.println("    Prints all users on the system.");
    }
  }
  
  private static class UserData
  {
    String account;
    UserInfo info;
    boolean persistSeedData;
    String seedAccountName;
    PersistableBundle seedAccountOptions;
    String seedAccountType;
    
    void clearSeedAccountData()
    {
      this.seedAccountName = null;
      this.seedAccountType = null;
      this.seedAccountOptions = null;
      this.persistSeedData = false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/UserManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */