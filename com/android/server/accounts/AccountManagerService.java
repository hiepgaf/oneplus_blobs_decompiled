package com.android.server.accounts;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManagerInternal;
import android.accounts.AccountManagerInternal.OnAppPermissionChangeListener;
import android.accounts.AuthenticatorDescription;
import android.accounts.CantAddAccountActivity;
import android.accounts.GrantCredentialsPermissionActivity;
import android.accounts.IAccountAuthenticator;
import android.accounts.IAccountAuthenticator.Stub;
import android.accounts.IAccountAuthenticatorResponse.Stub;
import android.accounts.IAccountManager.Stub;
import android.accounts.IAccountManagerResponse;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OnOpChangedInternalListener;
import android.app.IActivityManager;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager.OnPermissionsChangedListener;
import android.content.pm.RegisteredServicesCache.ServiceInfo;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SeempLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.google.android.collect.Lists;
import com.google.android.collect.Sets;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AccountManagerService
  extends IAccountManager.Stub
  implements RegisteredServicesCacheListener<AuthenticatorDescription>
{
  private static final Intent ACCOUNTS_CHANGED_INTENT;
  static final String ACCOUNTS_ID = "_id";
  private static final String ACCOUNTS_LAST_AUTHENTICATE_TIME_EPOCH_MILLIS = "last_password_entry_time_millis_epoch";
  static final String ACCOUNTS_NAME = "name";
  private static final String ACCOUNTS_PASSWORD = "password";
  private static final String ACCOUNTS_PREVIOUS_NAME = "previous_name";
  private static final String ACCOUNTS_TYPE = "type";
  private static final String ACCOUNTS_TYPE_COUNT = "count(type)";
  private static final String[] ACCOUNT_TYPE_COUNT_PROJECTION = { "type", "count(type)" };
  private static final String AUTHTOKENS_ACCOUNTS_ID = "accounts_id";
  private static final String AUTHTOKENS_AUTHTOKEN = "authtoken";
  private static final String AUTHTOKENS_ID = "_id";
  private static final String AUTHTOKENS_TYPE = "type";
  private static final String CE_DATABASE_NAME = "accounts_ce.db";
  private static final int CE_DATABASE_VERSION = 10;
  private static final String CE_DB_PREFIX = "ceDb.";
  private static final String CE_TABLE_ACCOUNTS = "ceDb.accounts";
  private static final String CE_TABLE_AUTHTOKENS = "ceDb.authtokens";
  private static final String CE_TABLE_EXTRAS = "ceDb.extras";
  private static final String[] COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN;
  private static final String[] COLUMNS_EXTRAS_KEY_AND_VALUE;
  private static final String COUNT_OF_MATCHING_GRANTS = "SELECT COUNT(*) FROM grants, accounts WHERE accounts_id=_id AND uid=? AND auth_token_type=? AND name=? AND type=?";
  private static final String COUNT_OF_MATCHING_GRANTS_ANY_TOKEN = "SELECT COUNT(*) FROM grants, accounts WHERE accounts_id=_id AND uid=? AND name=? AND type=?";
  private static final String DATABASE_NAME = "accounts.db";
  private static final String DE_DATABASE_NAME = "accounts_de.db";
  private static final int DE_DATABASE_VERSION = 1;
  private static final Account[] EMPTY_ACCOUNT_ARRAY = new Account[0];
  private static final String EXTRAS_ACCOUNTS_ID = "accounts_id";
  private static final String EXTRAS_ID = "_id";
  private static final String EXTRAS_KEY = "key";
  private static final String EXTRAS_VALUE = "value";
  static final String GRANTS_ACCOUNTS_ID = "accounts_id";
  private static final String GRANTS_AUTH_TOKEN_TYPE = "auth_token_type";
  static final String GRANTS_GRANTEE_UID = "uid";
  private static final int MAX_DEBUG_DB_SIZE = 64;
  private static final int MESSAGE_COPY_SHARED_ACCOUNT = 4;
  private static final int MESSAGE_TIMED_OUT = 3;
  private static final String META_KEY = "key";
  private static final String META_KEY_DELIMITER = ":";
  private static final String META_KEY_FOR_AUTHENTICATOR_UID_FOR_TYPE_PREFIX = "auth_uid_for_type:";
  private static final String META_VALUE = "value";
  private static final String PRE_N_DATABASE_NAME = "accounts.db";
  private static final int PRE_N_DATABASE_VERSION = 9;
  private static final String SELECTION_AUTHTOKENS_BY_ACCOUNT = "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)";
  private static final String SELECTION_META_BY_AUTHENTICATOR_TYPE = "key LIKE ?";
  private static final String SELECTION_USERDATA_BY_ACCOUNT = "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)";
  private static final String SHARED_ACCOUNTS_ID = "_id";
  static final String TABLE_ACCOUNTS = "accounts";
  private static final String TABLE_AUTHTOKENS = "authtokens";
  private static final String TABLE_EXTRAS = "extras";
  static final String TABLE_GRANTS = "grants";
  private static final String TABLE_META = "meta";
  private static final String TABLE_SHARED_ACCOUNTS = "shared_accounts";
  private static final String TAG = "AccountManagerService";
  private static AtomicReference<AccountManagerService> sThis;
  private final AppOpsManager mAppOpsManager;
  private final CopyOnWriteArrayList<AccountManagerInternal.OnAppPermissionChangeListener> mAppPermissionChangeListeners = new CopyOnWriteArrayList();
  private final IAccountAuthenticatorCache mAuthenticatorCache;
  final Context mContext;
  private final SparseBooleanArray mLocalUnlockedUsers = new SparseBooleanArray();
  final MessageHandler mMessageHandler;
  private final AtomicInteger mNotificationIds = new AtomicInteger(1);
  private final PackageManager mPackageManager;
  private final LinkedHashMap<String, Session> mSessions = new LinkedHashMap();
  private UserManager mUserManager;
  private final SparseArray<UserAccounts> mUsers = new SparseArray();
  
  static
  {
    ACCOUNTS_CHANGED_INTENT = new Intent("android.accounts.LOGIN_ACCOUNTS_CHANGED");
    ACCOUNTS_CHANGED_INTENT.setFlags(67108864);
    COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN = new String[] { "type", "authtoken" };
    COLUMNS_EXTRAS_KEY_AND_VALUE = new String[] { "key", "value" };
    sThis = new AtomicReference();
  }
  
  public AccountManagerService(Context paramContext)
  {
    this(paramContext, paramContext.getPackageManager(), new AccountAuthenticatorCache(paramContext));
  }
  
  public AccountManagerService(Context paramContext, PackageManager paramPackageManager, IAccountAuthenticatorCache paramIAccountAuthenticatorCache)
  {
    this.mContext = paramContext;
    this.mPackageManager = paramPackageManager;
    this.mAppOpsManager = ((AppOpsManager)this.mContext.getSystemService(AppOpsManager.class));
    this.mMessageHandler = new MessageHandler(FgThread.get().getLooper());
    this.mAuthenticatorCache = paramIAccountAuthenticatorCache;
    this.mAuthenticatorCache.setListener(this, null);
    sThis.set(this);
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.PACKAGE_REMOVED");
    paramContext.addDataScheme("package");
    this.mContext.registerReceiver(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (!paramAnonymousIntent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
          new Thread(new Runnable()
          {
            public void run()
            {
              AccountManagerService.-wrap17(AccountManagerService.this);
            }
          }).start();
        }
      }
    }, paramContext);
    paramContext = new IntentFilter();
    paramContext.addAction("android.intent.action.USER_REMOVED");
    this.mContext.registerReceiverAsUser(new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if ("android.intent.action.USER_REMOVED".equals(paramAnonymousIntent.getAction())) {
          AccountManagerService.-wrap16(AccountManagerService.this, paramAnonymousIntent);
        }
      }
    }, UserHandle.ALL, paramContext, null, null);
    LocalServices.addService(AccountManagerInternal.class, new AccountManagerInternalImpl(null));
    new PackageMonitor()
    {
      public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
      {
        AccountManagerService.-wrap11(AccountManagerService.this, paramAnonymousInt, true);
      }
      
      public void onPackageUpdateFinished(String paramAnonymousString, int paramAnonymousInt)
      {
        AccountManagerService.-wrap11(AccountManagerService.this, paramAnonymousInt, true);
      }
    }.register(this.mContext, this.mMessageHandler.getLooper(), UserHandle.ALL, true);
    this.mAppOpsManager.startWatchingMode(62, null, new AppOpsManager.OnOpChangedInternalListener()
    {
      public void onOpChanged(int paramAnonymousInt, String paramAnonymousString)
      {
        try
        {
          paramAnonymousInt = ActivityManager.getCurrentUser();
          paramAnonymousInt = AccountManagerService.-get3(AccountManagerService.this).getPackageUidAsUser(paramAnonymousString, paramAnonymousInt);
          if (AccountManagerService.-get0(AccountManagerService.this).checkOpNoThrow(62, paramAnonymousInt, paramAnonymousString) == 0)
          {
            long l = Binder.clearCallingIdentity();
            try
            {
              AccountManagerService.-wrap12(AccountManagerService.this, paramAnonymousString, paramAnonymousInt, true);
              return;
            }
            finally
            {
              Binder.restoreCallingIdentity(l);
            }
          }
          return;
        }
        catch (PackageManager.NameNotFoundException paramAnonymousString) {}
      }
    });
    this.mPackageManager.addOnPermissionsChangeListener(new -void__init__android_content_Context_context_android_content_pm_PackageManager_packageManager_com_android_server_accounts_IAccountAuthenticatorCache_authenticatorCache_LambdaImpl0());
  }
  
  private boolean accountExistsCacheLocked(UserAccounts paramUserAccounts, Account paramAccount)
  {
    if (paramUserAccounts.accountCache.containsKey(paramAccount.type))
    {
      paramUserAccounts = (Account[])paramUserAccounts.accountCache.get(paramAccount.type);
      int j = paramUserAccounts.length;
      int i = 0;
      while (i < j)
      {
        if (paramUserAccounts[i].name.equals(paramAccount.name)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  private boolean addAccountInternal(UserAccounts paramUserAccounts, Account paramAccount, String paramString, Bundle paramBundle, int paramInt)
  {
    Bundle.setDefusable(paramBundle, true);
    if (paramAccount == null) {
      return false;
    }
    if (!isLocalUnlockedUser(UserAccounts.-get8(paramUserAccounts)))
    {
      Log.w("AccountManagerService", "Account " + paramAccount + " cannot be added - user " + UserAccounts.-get8(paramUserAccounts) + " is locked. callingUid=" + paramInt);
      return false;
    }
    synchronized (paramUserAccounts.cacheLock)
    {
      SQLiteDatabase localSQLiteDatabase = paramUserAccounts.openHelper.getWritableDatabaseUserIsUnlocked();
      localSQLiteDatabase.beginTransaction();
      try
      {
        if (DatabaseUtils.longForQuery(localSQLiteDatabase, "select count(*) from ceDb.accounts WHERE name=? AND type=?", new String[] { paramAccount.name, paramAccount.type }) > 0L)
        {
          Log.w("AccountManagerService", "insertAccountIntoDatabase: " + paramAccount + ", skipping since the account already exists");
          localSQLiteDatabase.endTransaction();
          return false;
        }
        Object localObject2 = new ContentValues();
        ((ContentValues)localObject2).put("name", paramAccount.name);
        ((ContentValues)localObject2).put("type", paramAccount.type);
        ((ContentValues)localObject2).put("password", paramString);
        long l = localSQLiteDatabase.insert("ceDb.accounts", "name", (ContentValues)localObject2);
        if (l < 0L)
        {
          Log.w("AccountManagerService", "insertAccountIntoDatabase: " + paramAccount + ", skipping the DB insert failed");
          localSQLiteDatabase.endTransaction();
          return false;
        }
        paramString = new ContentValues();
        paramString.put("_id", Long.valueOf(l));
        paramString.put("name", paramAccount.name);
        paramString.put("type", paramAccount.type);
        paramString.put("last_password_entry_time_millis_epoch", Long.valueOf(System.currentTimeMillis()));
        if (localSQLiteDatabase.insert("accounts", "name", paramString) < 0L)
        {
          Log.w("AccountManagerService", "insertAccountIntoDatabase: " + paramAccount + ", skipping the DB insert failed");
          localSQLiteDatabase.endTransaction();
          return false;
        }
        if (paramBundle != null)
        {
          paramString = paramBundle.keySet().iterator();
          while (paramString.hasNext())
          {
            localObject2 = (String)paramString.next();
            if (insertExtraLocked(localSQLiteDatabase, l, (String)localObject2, paramBundle.getString((String)localObject2)) < 0L)
            {
              Log.w("AccountManagerService", "insertAccountIntoDatabase: " + paramAccount + ", skipping since insertExtra failed for key " + (String)localObject2);
              localSQLiteDatabase.endTransaction();
              return false;
            }
          }
        }
        localSQLiteDatabase.setTransactionSuccessful();
        logRecord(localSQLiteDatabase, DebugDbHelper.-get0(), "accounts", l, paramUserAccounts, paramInt);
        insertAccountIntoCacheLocked(paramUserAccounts, paramAccount);
        localSQLiteDatabase.endTransaction();
        if (getUserManager().getUserInfo(UserAccounts.-get8(paramUserAccounts)).canHaveProfile()) {
          addAccountToLinkedRestrictedUsers(paramAccount, UserAccounts.-get8(paramUserAccounts));
        }
        sendAccountsChangedBroadcast(UserAccounts.-get8(paramUserAccounts));
        return true;
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
      }
    }
  }
  
  private void addAccountToLinkedRestrictedUsers(Account paramAccount, int paramInt)
  {
    Iterator localIterator = getUserManager().getUsers().iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      if ((localUserInfo.isRestricted()) && (paramInt == localUserInfo.restrictedProfileParentId))
      {
        addSharedAccountAsUser(paramAccount, localUserInfo.id);
        if (isLocalUnlockedUser(localUserInfo.id)) {
          this.mMessageHandler.sendMessage(this.mMessageHandler.obtainMessage(4, paramInt, localUserInfo.id, paramAccount));
        }
      }
    }
  }
  
  private boolean addSharedAccountAsUser(Account paramAccount, int paramInt)
  {
    UserAccounts localUserAccounts = getUserAccounts(handleIncomingUser(paramInt));
    SQLiteDatabase localSQLiteDatabase = localUserAccounts.openHelper.getWritableDatabase();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("name", paramAccount.name);
    localContentValues.put("type", paramAccount.type);
    localSQLiteDatabase.delete("shared_accounts", "name=? AND type=?", new String[] { paramAccount.name, paramAccount.type });
    long l = localSQLiteDatabase.insert("shared_accounts", "name", localContentValues);
    if (l < 0L)
    {
      Log.w("AccountManagerService", "insertAccountIntoDatabase: " + paramAccount + ", skipping the DB insert failed");
      return false;
    }
    logRecord(localSQLiteDatabase, DebugDbHelper.-get0(), "shared_accounts", l, localUserAccounts);
    return true;
  }
  
  private byte[] calculatePackageSignatureDigest(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest2 = MessageDigest.getInstance("SHA-256");
      Signature[] arrayOfSignature = this.mPackageManager.getPackageInfo(paramString, 64).signatures;
      int i = 0;
      int j = arrayOfSignature.length;
      for (;;)
      {
        MessageDigest localMessageDigest1 = localMessageDigest2;
        if (i >= j) {
          break;
        }
        localMessageDigest2.update(arrayOfSignature[i].toByteArray());
        i += 1;
      }
      Object localObject;
      return ((MessageDigest)localObject).digest();
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("AccountManagerService", "Could not find packageinfo for: " + paramString);
      localObject = null;
      if (localObject == null) {
        return null;
      }
    }
    catch (NoSuchAlgorithmException paramString)
    {
      for (;;)
      {
        Log.wtf("AccountManagerService", "SHA-256 should be available", paramString);
        localObject = null;
      }
    }
  }
  
  private boolean canHaveProfile(int paramInt)
  {
    UserInfo localUserInfo = getUserManager().getUserInfo(paramInt);
    if (localUserInfo != null) {
      return localUserInfo.canHaveProfile();
    }
    return false;
  }
  
  private boolean canUserModifyAccounts(int paramInt1, int paramInt2)
  {
    if (isProfileOwner(paramInt2)) {
      return true;
    }
    return !getUserManager().getUserRestrictions(new UserHandle(paramInt1)).getBoolean("no_modify_accounts");
  }
  
  private boolean canUserModifyAccountsForType(int paramInt1, String paramString, int paramInt2)
  {
    if (isProfileOwner(paramInt2)) {
      return true;
    }
    String[] arrayOfString = ((DevicePolicyManager)this.mContext.getSystemService("device_policy")).getAccountTypesWithManagementDisabledAsUser(paramInt1);
    if (arrayOfString == null) {
      return true;
    }
    paramInt2 = arrayOfString.length;
    paramInt1 = 0;
    while (paramInt1 < paramInt2)
    {
      if (arrayOfString[paramInt1].equals(paramString)) {
        return false;
      }
      paramInt1 += 1;
    }
    return true;
  }
  
  private void cancelAccountAccessRequestNotificationIfNeeded(int paramInt, boolean paramBoolean)
  {
    Account[] arrayOfAccount = getAccountsAsUser(null, UserHandle.getUserId(paramInt), "android");
    int i = 0;
    int j = arrayOfAccount.length;
    while (i < j)
    {
      cancelAccountAccessRequestNotificationIfNeeded(arrayOfAccount[i], paramInt, paramBoolean);
      i += 1;
    }
  }
  
  private void cancelAccountAccessRequestNotificationIfNeeded(Account paramAccount, int paramInt, String paramString, boolean paramBoolean)
  {
    if ((!paramBoolean) || (hasAccountAccess(paramAccount, paramString, UserHandle.getUserHandleForUid(paramInt)))) {
      cancelNotification(getCredentialPermissionNotificationId(paramAccount, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", paramInt).intValue(), paramString, UserHandle.getUserHandleForUid(paramInt));
    }
  }
  
  private void cancelAccountAccessRequestNotificationIfNeeded(Account paramAccount, int paramInt, boolean paramBoolean)
  {
    String[] arrayOfString = this.mPackageManager.getPackagesForUid(paramInt);
    if (arrayOfString != null)
    {
      int i = 0;
      int j = arrayOfString.length;
      while (i < j)
      {
        cancelAccountAccessRequestNotificationIfNeeded(paramAccount, paramInt, arrayOfString[i], paramBoolean);
        i += 1;
      }
    }
  }
  
  private void cancelAccountAccessRequestNotificationIfNeeded(String paramString, int paramInt, boolean paramBoolean)
  {
    Account[] arrayOfAccount = getAccountsAsUser(null, UserHandle.getUserId(paramInt), "android");
    int i = 0;
    int j = arrayOfAccount.length;
    while (i < j)
    {
      cancelAccountAccessRequestNotificationIfNeeded(arrayOfAccount[i], paramInt, paramString, paramBoolean);
      i += 1;
    }
  }
  
  private static void checkManageOrCreateUsersPermission(String paramString)
  {
    if ((ActivityManager.checkComponentPermission("android.permission.MANAGE_USERS", Binder.getCallingUid(), -1, true) != 0) && (ActivityManager.checkComponentPermission("android.permission.CREATE_USERS", Binder.getCallingUid(), -1, true) != 0)) {
      throw new SecurityException("You need MANAGE_USERS or CREATE_USERS permission to: " + paramString);
    }
  }
  
  private static void checkManageUsersPermission(String paramString)
  {
    if (ActivityManager.checkComponentPermission("android.permission.MANAGE_USERS", Binder.getCallingUid(), -1, true) != 0) {
      throw new SecurityException("You need MANAGE_USERS permission to: " + paramString);
    }
  }
  
  private void checkReadAccountsPermitted(int paramInt1, String paramString1, int paramInt2, String paramString2)
  {
    if (!isAccountVisibleToCaller(paramString1, paramInt1, paramInt2, paramString2))
    {
      paramString1 = String.format("caller uid %s cannot access %s accounts", new Object[] { Integer.valueOf(paramInt1), paramString1 });
      Log.w("AccountManagerService", "  " + paramString1);
      throw new SecurityException(paramString1);
    }
  }
  
  /* Error */
  private boolean checkUidPermission(String paramString1, int paramInt, String paramString2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 6
    //   3: invokestatic 970	android/os/Binder:clearCallingIdentity	()J
    //   6: lstore 7
    //   8: invokestatic 975	android/app/ActivityThread:getPackageManager	()Landroid/content/pm/IPackageManager;
    //   11: aload_1
    //   12: iload_2
    //   13: invokeinterface 980 3 0
    //   18: istore 4
    //   20: iload 4
    //   22: ifeq +10 -> 32
    //   25: lload 7
    //   27: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   30: iconst_0
    //   31: ireturn
    //   32: aload_1
    //   33: invokestatic 988	android/app/AppOpsManager:permissionToOpCode	(Ljava/lang/String;)I
    //   36: istore 4
    //   38: iload 6
    //   40: istore 5
    //   42: iload 4
    //   44: iconst_m1
    //   45: if_icmpeq +23 -> 68
    //   48: aload_0
    //   49: getfield 269	com/android/server/accounts/AccountManagerService:mAppOpsManager	Landroid/app/AppOpsManager;
    //   52: iload 4
    //   54: iload_2
    //   55: aload_3
    //   56: invokevirtual 992	android/app/AppOpsManager:noteOpNoThrow	(IILjava/lang/String;)I
    //   59: istore_2
    //   60: iload_2
    //   61: ifne +15 -> 76
    //   64: iload 6
    //   66: istore 5
    //   68: lload 7
    //   70: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   73: iload 5
    //   75: ireturn
    //   76: iconst_0
    //   77: istore 5
    //   79: goto -11 -> 68
    //   82: astore_1
    //   83: lload 7
    //   85: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   88: iconst_0
    //   89: ireturn
    //   90: astore_1
    //   91: lload 7
    //   93: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   96: aload_1
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	AccountManagerService
    //   0	98	1	paramString1	String
    //   0	98	2	paramInt	int
    //   0	98	3	paramString2	String
    //   18	35	4	i	int
    //   40	38	5	bool1	boolean
    //   1	64	6	bool2	boolean
    //   6	86	7	l	long
    // Exception table:
    //   from	to	target	type
    //   8	20	82	android/os/RemoteException
    //   32	38	82	android/os/RemoteException
    //   48	60	82	android/os/RemoteException
    //   8	20	90	finally
    //   32	38	90	finally
    //   48	60	90	finally
  }
  
  private void compileSqlStatementForLogging(SQLiteDatabase paramSQLiteDatabase, UserAccounts paramUserAccounts)
  {
    UserAccounts.-set1(paramUserAccounts, paramSQLiteDatabase.compileStatement("INSERT OR REPLACE INTO " + DebugDbHelper.-get15() + " VALUES (?,?,?,?,?,?)"));
  }
  
  private void completeCloningAccount(IAccountManagerResponse paramIAccountManagerResponse, final Bundle paramBundle, final Account paramAccount, UserAccounts paramUserAccounts, final int paramInt)
  {
    Bundle.setDefusable(paramBundle, true);
    long l = clearCallingIdentity();
    try
    {
      new Session(this, paramUserAccounts, paramIAccountManagerResponse, paramAccount.type, false, false, paramAccount.name, false)
      {
        public void onError(int paramAnonymousInt, String paramAnonymousString)
        {
          super.onError(paramAnonymousInt, paramAnonymousString);
        }
        
        public void onResult(Bundle paramAnonymousBundle)
        {
          Bundle.setDefusable(paramAnonymousBundle, true);
          super.onResult(paramAnonymousBundle);
        }
        
        public void run()
          throws RemoteException
        {
          synchronized (jdField_this.getUserAccounts(paramInt).cacheLock)
          {
            Account[] arrayOfAccount = jdField_this.getAccounts(paramInt, jdField_this.mContext.getOpPackageName());
            int i = 0;
            int j = arrayOfAccount.length;
            if (i < j)
            {
              if (arrayOfAccount[i].equals(paramAccount)) {
                this.mAuthenticator.addAccountFromCredentials(this, paramAccount, paramBundle);
              }
            }
            else {
              return;
            }
            i += 1;
          }
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          return super.toDebugString(paramAnonymousLong) + ", getAccountCredentialsForClone" + ", " + paramAccount.type;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  private void createNoCredentialsPermissionNotification(Account paramAccount, Intent paramIntent, String paramString, int paramInt)
  {
    int i = paramIntent.getIntExtra("uid", -1);
    String str2 = paramIntent.getStringExtra("authTokenType");
    Object localObject2 = this.mContext.getString(17040509, new Object[] { paramAccount.name });
    int j = ((String)localObject2).indexOf('\n');
    Object localObject1 = localObject2;
    String str1 = "";
    if (j > 0)
    {
      localObject1 = ((String)localObject2).substring(0, j);
      str1 = ((String)localObject2).substring(j + 1);
    }
    localObject2 = new UserHandle(paramInt);
    Context localContext = getContextForUser((UserHandle)localObject2);
    paramIntent = new Notification.Builder(localContext).setSmallIcon(17301642).setWhen(0L).setColor(localContext.getColor(17170523)).setContentTitle((CharSequence)localObject1).setContentText(str1).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, paramIntent, 268435456, null, (UserHandle)localObject2)).build();
    installNotification(getCredentialPermissionNotificationId(paramAccount, str2, i).intValue(), paramIntent, paramString, ((UserHandle)localObject2).getIdentifier());
  }
  
  private static void deleteDbFileWarnIfFailed(File paramFile)
  {
    if (!SQLiteDatabase.deleteDatabase(paramFile)) {
      Log.w("AccountManagerService", "Database at " + paramFile + " was not deleted successfully");
    }
  }
  
  /* Error */
  private void doNotification(UserAccounts paramUserAccounts, Account paramAccount, CharSequence paramCharSequence, Intent paramIntent, String paramString, int paramInt)
  {
    // Byte code:
    //   0: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   3: lstore 7
    //   5: ldc -29
    //   7: iconst_2
    //   8: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   11: ifeq +40 -> 51
    //   14: ldc -29
    //   16: new 613	java/lang/StringBuilder
    //   19: dup
    //   20: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   23: ldc_w 1109
    //   26: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: aload_3
    //   30: invokevirtual 1112	java/lang/StringBuilder:append	(Ljava/lang/CharSequence;)Ljava/lang/StringBuilder;
    //   33: ldc_w 1114
    //   36: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   39: aload 4
    //   41: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   44: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   47: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   50: pop
    //   51: aload 4
    //   53: invokevirtual 1121	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   56: ifnull +40 -> 96
    //   59: ldc_w 1123
    //   62: invokevirtual 1128	java/lang/Class:getName	()Ljava/lang/String;
    //   65: aload 4
    //   67: invokevirtual 1121	android/content/Intent:getComponent	()Landroid/content/ComponentName;
    //   70: invokevirtual 1133	android/content/ComponentName:getClassName	()Ljava/lang/String;
    //   73: invokevirtual 599	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   76: ifeq +20 -> 96
    //   79: aload_0
    //   80: aload_2
    //   81: aload 4
    //   83: aload 5
    //   85: iload 6
    //   87: invokespecial 1135	com/android/server/accounts/AccountManagerService:createNoCredentialsPermissionNotification	(Landroid/accounts/Account;Landroid/content/Intent;Ljava/lang/String;I)V
    //   90: lload 7
    //   92: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   95: return
    //   96: aload_0
    //   97: new 539	android/os/UserHandle
    //   100: dup
    //   101: iload 6
    //   103: invokespecial 860	android/os/UserHandle:<init>	(I)V
    //   106: invokespecial 1047	com/android/server/accounts/AccountManagerService:getContextForUser	(Landroid/os/UserHandle;)Landroid/content/Context;
    //   109: astore 9
    //   111: aload_0
    //   112: aload_1
    //   113: aload_2
    //   114: invokespecial 412	com/android/server/accounts/AccountManagerService:getSigninRequiredNotificationId	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;)Ljava/lang/Integer;
    //   117: astore_1
    //   118: aload 4
    //   120: aload_1
    //   121: invokestatic 1138	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   124: invokevirtual 1142	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   127: pop
    //   128: aload 9
    //   130: ldc_w 1143
    //   133: invokevirtual 1147	android/content/Context:getText	(I)Ljava/lang/CharSequence;
    //   136: invokeinterface 1150 1 0
    //   141: astore 10
    //   143: new 1049	android/app/Notification$Builder
    //   146: dup
    //   147: aload 9
    //   149: invokespecial 1050	android/app/Notification$Builder:<init>	(Landroid/content/Context;)V
    //   152: lconst_0
    //   153: invokevirtual 1059	android/app/Notification$Builder:setWhen	(J)Landroid/app/Notification$Builder;
    //   156: ldc_w 1051
    //   159: invokevirtual 1055	android/app/Notification$Builder:setSmallIcon	(I)Landroid/app/Notification$Builder;
    //   162: aload 9
    //   164: ldc_w 1060
    //   167: invokevirtual 1063	android/content/Context:getColor	(I)I
    //   170: invokevirtual 1066	android/app/Notification$Builder:setColor	(I)Landroid/app/Notification$Builder;
    //   173: aload 10
    //   175: iconst_1
    //   176: anewarray 954	java/lang/Object
    //   179: dup
    //   180: iconst_0
    //   181: aload_2
    //   182: getfield 596	android/accounts/Account:name	Ljava/lang/String;
    //   185: aastore
    //   186: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   189: invokevirtual 1070	android/app/Notification$Builder:setContentTitle	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
    //   192: aload_3
    //   193: invokevirtual 1073	android/app/Notification$Builder:setContentText	(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;
    //   196: aload_0
    //   197: getfield 484	com/android/server/accounts/AccountManagerService:mContext	Landroid/content/Context;
    //   200: iconst_0
    //   201: aload 4
    //   203: ldc_w 1074
    //   206: aconst_null
    //   207: new 539	android/os/UserHandle
    //   210: dup
    //   211: iload 6
    //   213: invokespecial 860	android/os/UserHandle:<init>	(I)V
    //   216: invokestatic 1080	android/app/PendingIntent:getActivityAsUser	(Landroid/content/Context;ILandroid/content/Intent;ILandroid/os/Bundle;Landroid/os/UserHandle;)Landroid/app/PendingIntent;
    //   219: invokevirtual 1084	android/app/Notification$Builder:setContentIntent	(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;
    //   222: invokevirtual 1088	android/app/Notification$Builder:build	()Landroid/app/Notification;
    //   225: astore_2
    //   226: aload_0
    //   227: aload_1
    //   228: invokevirtual 911	java/lang/Integer:intValue	()I
    //   231: aload_2
    //   232: aload 5
    //   234: iload 6
    //   236: invokespecial 1095	com/android/server/accounts/AccountManagerService:installNotification	(ILandroid/app/Notification;Ljava/lang/String;I)V
    //   239: goto -149 -> 90
    //   242: astore_1
    //   243: lload 7
    //   245: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   248: aload_1
    //   249: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	250	0	this	AccountManagerService
    //   0	250	1	paramUserAccounts	UserAccounts
    //   0	250	2	paramAccount	Account
    //   0	250	3	paramCharSequence	CharSequence
    //   0	250	4	paramIntent	Intent
    //   0	250	5	paramString	String
    //   0	250	6	paramInt	int
    //   3	241	7	l	long
    //   109	54	9	localContext	Context
    //   141	33	10	str	String
    // Exception table:
    //   from	to	target	type
    //   5	51	242	finally
    //   51	90	242	finally
    //   96	239	242	finally
  }
  
  private void dumpUser(UserAccounts paramUserAccounts, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString, boolean paramBoolean)
  {
    synchronized (paramUserAccounts.cacheLock)
    {
      ??? = paramUserAccounts.openHelper.getReadableDatabase();
      if (!paramBoolean) {
        break label125;
      }
      paramUserAccounts = ((SQLiteDatabase)???).query("accounts", ACCOUNT_TYPE_COUNT_PROJECTION, null, null, "type", null, null);
    }
    if (paramUserAccounts != null) {
      paramUserAccounts.close();
    }
    for (;;)
    {
      return;
      label125:
      Object localObject3 = getAccountsFromCacheLocked(paramUserAccounts, null, Process.myUid(), null);
      paramPrintWriter.println("Accounts: " + localObject3.length);
      int i = 0;
      int j = localObject3.length;
      Object localObject4;
      while (i < j)
      {
        localObject4 = localObject3[i];
        paramPrintWriter.println("  " + localObject4);
        i += 1;
      }
      paramPrintWriter.println();
      ??? = ((SQLiteDatabase)???).query(DebugDbHelper.-get15(), null, null, null, null, null, DebugDbHelper.-get17());
      paramPrintWriter.println("AccountId, Action_Type, timestamp, UID, TableName, Key");
      paramPrintWriter.println("Accounts History");
      try
      {
        while (((Cursor)???).moveToNext()) {
          paramPrintWriter.println(((Cursor)???).getString(0) + "," + ((Cursor)???).getString(1) + "," + ((Cursor)???).getString(2) + "," + ((Cursor)???).getString(3) + "," + ((Cursor)???).getString(4) + "," + ((Cursor)???).getString(5));
        }
        paramPrintWriter.println();
      }
      finally
      {
        ((Cursor)???).close();
      }
      synchronized (this.mSessions)
      {
        long l = SystemClock.elapsedRealtime();
        paramPrintWriter.println("Active Sessions: " + this.mSessions.size());
        localObject3 = this.mSessions.values().iterator();
        if (((Iterator)localObject3).hasNext())
        {
          localObject4 = (Session)((Iterator)localObject3).next();
          paramPrintWriter.println("  " + ((Session)localObject4).toDebugString(l));
        }
      }
      paramPrintWriter.println();
      this.mAuthenticatorCache.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString, UserAccounts.-get8(paramUserAccounts));
    }
  }
  
  private Account[] filterSharedAccounts(UserAccounts paramUserAccounts, Account[] paramArrayOfAccount, int paramInt, String paramString)
  {
    if ((getUserManager() == null) || (paramUserAccounts == null)) {}
    while ((UserAccounts.-get8(paramUserAccounts) < 0) || (paramInt == Process.myUid())) {
      return paramArrayOfAccount;
    }
    localObject1 = getUserManager().getUserInfo(UserAccounts.-get8(paramUserAccounts));
    String[] arrayOfString;
    Object localObject2;
    Account[] arrayOfAccount;
    if ((localObject1 != null) && (((UserInfo)localObject1).isRestricted()))
    {
      arrayOfString = this.mPackageManager.getPackagesForUid(paramInt);
      localObject1 = this.mContext.getResources().getString(17039461);
      paramInt = 0;
      i = arrayOfString.length;
      while (paramInt < i)
      {
        localObject2 = arrayOfString[paramInt];
        if (((String)localObject1).contains(";" + (String)localObject2 + ";")) {
          return paramArrayOfAccount;
        }
        paramInt += 1;
      }
      localObject2 = new ArrayList();
      arrayOfAccount = getSharedAccountsAsUser(UserAccounts.-get8(paramUserAccounts));
      if ((arrayOfAccount == null) || (arrayOfAccount.length == 0)) {
        return paramArrayOfAccount;
      }
      localObject1 = "";
      if (paramString == null) {}
    }
    try
    {
      paramString = this.mPackageManager.getPackageInfo(paramString, 0);
      paramUserAccounts = (UserAccounts)localObject1;
      if (paramString != null)
      {
        paramUserAccounts = (UserAccounts)localObject1;
        if (paramString.restrictedAccountType != null) {
          paramUserAccounts = paramString.restrictedAccountType;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramUserAccounts)
    {
      for (;;)
      {
        int m;
        int k;
        int n;
        paramUserAccounts = (UserAccounts)localObject1;
      }
    }
    m = paramArrayOfAccount.length;
    paramInt = 0;
    for (;;)
    {
      if (paramInt >= m) {
        break label384;
      }
      paramString = paramArrayOfAccount[paramInt];
      if (!paramString.type.equals(paramUserAccounts)) {
        break;
      }
      ((ArrayList)localObject2).add(paramString);
      paramInt += 1;
    }
    paramInt = 0;
    int i = arrayOfString.length;
    for (;;)
    {
      paramUserAccounts = (UserAccounts)localObject1;
      if (paramInt >= i) {
        break;
      }
      paramUserAccounts = arrayOfString[paramInt];
      paramUserAccounts = this.mPackageManager.getPackageInfo(paramUserAccounts, 0);
      if ((paramUserAccounts != null) && (paramUserAccounts.restrictedAccountType != null))
      {
        paramUserAccounts = paramUserAccounts.restrictedAccountType;
        break;
      }
      paramInt += 1;
    }
    k = 0;
    i = 0;
    n = arrayOfAccount.length;
    for (;;)
    {
      int j = k;
      if (i < n)
      {
        if (arrayOfAccount[i].equals(paramString)) {
          j = 1;
        }
      }
      else
      {
        if (j != 0) {
          break;
        }
        ((ArrayList)localObject2).add(paramString);
        break;
      }
      i += 1;
    }
    label384:
    paramUserAccounts = new Account[((ArrayList)localObject2).size()];
    ((ArrayList)localObject2).toArray(paramUserAccounts);
    return paramUserAccounts;
    return paramArrayOfAccount;
  }
  
  private long getAccountIdFromSharedTable(SQLiteDatabase paramSQLiteDatabase, Account paramAccount)
  {
    String str = paramAccount.name;
    paramAccount = paramAccount.type;
    paramSQLiteDatabase = paramSQLiteDatabase.query("shared_accounts", new String[] { "_id" }, "name=? AND type=?", new String[] { str, paramAccount }, null, null, null);
    try
    {
      if (paramSQLiteDatabase.moveToNext())
      {
        long l = paramSQLiteDatabase.getLong(0);
        return l;
      }
      return -1L;
    }
    finally
    {
      paramSQLiteDatabase.close();
    }
  }
  
  private long getAccountIdLocked(SQLiteDatabase paramSQLiteDatabase, Account paramAccount)
  {
    String str = paramAccount.name;
    paramAccount = paramAccount.type;
    paramSQLiteDatabase = paramSQLiteDatabase.query("accounts", new String[] { "_id" }, "name=? AND type=?", new String[] { str, paramAccount }, null, null, null);
    try
    {
      if (paramSQLiteDatabase.moveToNext())
      {
        long l = paramSQLiteDatabase.getLong(0);
        return l;
      }
      return -1L;
    }
    finally
    {
      paramSQLiteDatabase.close();
    }
  }
  
  private AccountAndUser[] getAccounts(int[] paramArrayOfInt)
  {
    ArrayList localArrayList = Lists.newArrayList();
    int i = 0;
    int k = paramArrayOfInt.length;
    if (i < k)
    {
      int m = paramArrayOfInt[i];
      Object localObject2 = getUserAccounts(m);
      if (localObject2 == null) {}
      for (;;)
      {
        i += 1;
        break;
        synchronized (((UserAccounts)localObject2).cacheLock)
        {
          localObject2 = getAccountsFromCacheLocked((UserAccounts)localObject2, null, Binder.getCallingUid(), null);
          int j = 0;
          while (j < localObject2.length)
          {
            localArrayList.add(new AccountAndUser(localObject2[j], m));
            j += 1;
          }
        }
      }
    }
    return (AccountAndUser[])localArrayList.toArray(new AccountAndUser[localArrayList.size()]);
  }
  
  private Account[] getAccountsAsUser(String paramString1, int paramInt1, String paramString2, int paramInt2, String paramString3)
  {
    int j = Binder.getCallingUid();
    if ((paramInt1 != UserHandle.getCallingUserId()) && (j != Process.myUid()) && (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0)) {
      throw new SecurityException("User " + UserHandle.getCallingUserId() + " trying to get account for " + paramInt1);
    }
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "getAccounts: accountType " + paramString1 + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
    }
    int i = j;
    Object localObject = paramString3;
    if (paramInt2 != -1)
    {
      i = j;
      localObject = paramString3;
      if (UserHandle.isSameApp(j, Process.myUid()))
      {
        localObject = paramString2;
        i = paramInt2;
      }
    }
    localObject = getTypesVisibleToCaller(i, paramInt1, (String)localObject);
    if ((!((List)localObject).isEmpty()) && ((paramString1 == null) || (((List)localObject).contains(paramString1))))
    {
      paramString3 = (String)localObject;
      if (((List)localObject).contains(paramString1))
      {
        paramString3 = new ArrayList();
        paramString3.add(paramString1);
      }
      l = clearCallingIdentity();
    }
    try
    {
      paramString1 = getAccountsInternal(getUserAccounts(paramInt1), i, paramString2, paramString3);
      return paramString1;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
    return new Account[0];
  }
  
  private Account[] getAccountsInternal(UserAccounts paramUserAccounts, int paramInt, String paramString, List<String> paramList)
  {
    ArrayList localArrayList;
    synchronized (paramUserAccounts.cacheLock)
    {
      localArrayList = new ArrayList();
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        Account[] arrayOfAccount = getAccountsFromCacheLocked(paramUserAccounts, (String)paramList.next(), paramInt, paramString);
        if (arrayOfAccount != null) {
          localArrayList.addAll(Arrays.asList(arrayOfAccount));
        }
      }
    }
    paramUserAccounts = new Account[localArrayList.size()];
    paramInt = 0;
    while (paramInt < localArrayList.size())
    {
      paramUserAccounts[paramInt] = ((Account)localArrayList.get(paramInt));
      paramInt += 1;
    }
    return paramUserAccounts;
  }
  
  private static HashMap<String, Integer> getAuthenticatorTypeAndUIDForUser(Context paramContext, int paramInt)
  {
    return getAuthenticatorTypeAndUIDForUser(new AccountAuthenticatorCache(paramContext), paramInt);
  }
  
  private static HashMap<String, Integer> getAuthenticatorTypeAndUIDForUser(IAccountAuthenticatorCache paramIAccountAuthenticatorCache, int paramInt)
  {
    HashMap localHashMap = new HashMap();
    paramIAccountAuthenticatorCache = paramIAccountAuthenticatorCache.getAllServices(paramInt).iterator();
    while (paramIAccountAuthenticatorCache.hasNext())
    {
      RegisteredServicesCache.ServiceInfo localServiceInfo = (RegisteredServicesCache.ServiceInfo)paramIAccountAuthenticatorCache.next();
      localHashMap.put(((AuthenticatorDescription)localServiceInfo.type).type, Integer.valueOf(localServiceInfo.uid));
    }
    return localHashMap;
  }
  
  private AuthenticatorDescription[] getAuthenticatorTypesInternal(int paramInt)
  {
    this.mAuthenticatorCache.updateServices(paramInt);
    Object localObject = this.mAuthenticatorCache.getAllServices(paramInt);
    AuthenticatorDescription[] arrayOfAuthenticatorDescription = new AuthenticatorDescription[((Collection)localObject).size()];
    paramInt = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      arrayOfAuthenticatorDescription[paramInt] = ((AuthenticatorDescription)((RegisteredServicesCache.ServiceInfo)((Iterator)localObject).next()).type);
      paramInt += 1;
    }
    return arrayOfAuthenticatorDescription;
  }
  
  private Context getContextForUser(UserHandle paramUserHandle)
  {
    try
    {
      paramUserHandle = this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, paramUserHandle);
      return paramUserHandle;
    }
    catch (PackageManager.NameNotFoundException paramUserHandle) {}
    return this.mContext;
  }
  
  private Integer getCredentialPermissionNotificationId(Account paramAccount, String paramString, int paramInt)
  {
    UserAccounts localUserAccounts = getUserAccounts(UserHandle.getUserId(paramInt));
    synchronized (UserAccounts.-get2(localUserAccounts))
    {
      Pair localPair = new Pair(new Pair(paramAccount, paramString), Integer.valueOf(paramInt));
      paramString = (Integer)UserAccounts.-get2(localUserAccounts).get(localPair);
      paramAccount = paramString;
      if (paramString == null)
      {
        paramAccount = Integer.valueOf(this.mNotificationIds.incrementAndGet());
        UserAccounts.-get2(localUserAccounts).put(localPair, paramAccount);
      }
      return paramAccount;
    }
  }
  
  private long getDebugTableInsertionPoint(SQLiteDatabase paramSQLiteDatabase)
  {
    return DatabaseUtils.longForQuery(paramSQLiteDatabase, "SELECT " + DebugDbHelper.-get14() + " FROM " + DebugDbHelper.-get15() + " ORDER BY " + DebugDbHelper.-get17() + "," + DebugDbHelper.-get14() + " LIMIT 1", null);
  }
  
  private long getDebugTableRowCount(SQLiteDatabase paramSQLiteDatabase)
  {
    return DatabaseUtils.longForQuery(paramSQLiteDatabase, "SELECT COUNT(*) FROM " + DebugDbHelper.-get15(), null);
  }
  
  private long getExtrasIdLocked(SQLiteDatabase paramSQLiteDatabase, long paramLong, String paramString)
  {
    String str = "accounts_id=" + paramLong + " AND " + "key" + "=?";
    paramSQLiteDatabase = paramSQLiteDatabase.query("ceDb.extras", new String[] { "_id" }, str, new String[] { paramString }, null, null, null);
    try
    {
      if (paramSQLiteDatabase.moveToNext())
      {
        paramLong = paramSQLiteDatabase.getLong(0);
        return paramLong;
      }
      return -1L;
    }
    finally
    {
      paramSQLiteDatabase.close();
    }
  }
  
  private Integer getSigninRequiredNotificationId(UserAccounts paramUserAccounts, Account paramAccount)
  {
    synchronized (UserAccounts.-get5(paramUserAccounts))
    {
      Integer localInteger2 = (Integer)UserAccounts.-get5(paramUserAccounts).get(paramAccount);
      Integer localInteger1 = localInteger2;
      if (localInteger2 == null)
      {
        localInteger1 = Integer.valueOf(this.mNotificationIds.incrementAndGet());
        UserAccounts.-get5(paramUserAccounts).put(paramAccount, localInteger1);
      }
      return localInteger1;
    }
  }
  
  public static AccountManagerService getSingleton()
  {
    return (AccountManagerService)sThis.get();
  }
  
  private List<String> getTypesForCaller(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    long l = Binder.clearCallingIdentity();
    try
    {
      Object localObject = this.mAuthenticatorCache.getAllServices(paramInt2);
      Binder.restoreCallingIdentity(l);
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        RegisteredServicesCache.ServiceInfo localServiceInfo = (RegisteredServicesCache.ServiceInfo)((Iterator)localObject).next();
        paramInt2 = this.mPackageManager.checkSignatures(localServiceInfo.uid, paramInt1);
        if ((paramBoolean) || (paramInt2 == 0)) {
          localArrayList.add(((AuthenticatorDescription)localServiceInfo.type).type);
        }
      }
      return localList;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  private List<String> getTypesManagedByCaller(int paramInt1, int paramInt2)
  {
    return getTypesForCaller(paramInt1, paramInt2, false);
  }
  
  private List<String> getTypesVisibleToCaller(int paramInt1, int paramInt2, String paramString)
  {
    return getTypesForCaller(paramInt1, paramInt2, isPermitted(paramString, paramInt1, new String[] { "android.permission.GET_ACCOUNTS", "android.permission.GET_ACCOUNTS_PRIVILEGED" }));
  }
  
  private SparseBooleanArray getUidsOfInstalledOrUpdatedPackagesAsUser(int paramInt)
  {
    Object localObject = this.mPackageManager.getInstalledPackagesAsUser(8192, paramInt);
    SparseBooleanArray localSparseBooleanArray = new SparseBooleanArray(((List)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      PackageInfo localPackageInfo = (PackageInfo)((Iterator)localObject).next();
      if ((localPackageInfo.applicationInfo != null) && ((localPackageInfo.applicationInfo.flags & 0x800000) != 0)) {
        localSparseBooleanArray.put(localPackageInfo.applicationInfo.uid, true);
      }
    }
    return localSparseBooleanArray;
  }
  
  private UserAccounts getUserAccountsForCaller()
  {
    return getUserAccounts(UserHandle.getCallingUserId());
  }
  
  private UserManager getUserManager()
  {
    if (this.mUserManager == null) {
      this.mUserManager = UserManager.get(this.mContext);
    }
    return this.mUserManager;
  }
  
  private int handleIncomingUser(int paramInt)
  {
    try
    {
      int i = ActivityManagerNative.getDefault().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), paramInt, true, true, "", null);
      return i;
    }
    catch (RemoteException localRemoteException) {}
    return paramInt;
  }
  
  private boolean hasAccountAccess(Account paramAccount, String paramString, int paramInt)
  {
    boolean bool = true;
    String str = paramString;
    if (paramString == null)
    {
      paramString = this.mPackageManager.getPackagesForUid(paramInt);
      if (ArrayUtils.isEmpty(paramString)) {
        return false;
      }
      str = paramString[0];
    }
    if (permissionIsGranted(paramAccount, null, paramInt, UserHandle.getUserId(paramInt))) {
      return true;
    }
    if (!checkUidPermission("android.permission.GET_ACCOUNTS_PRIVILEGED", paramInt, str)) {
      bool = checkUidPermission("android.permission.GET_ACCOUNTS", paramInt, str);
    }
    return bool;
  }
  
  private boolean hasExplicitlyGrantedPermission(Account paramAccount, String paramString, int paramInt)
  {
    boolean bool = false;
    if (UserHandle.getAppId(paramInt) == 1000) {
      return true;
    }
    Object localObject1 = getUserAccounts(UserHandle.getUserId(paramInt));
    synchronized (((UserAccounts)localObject1).cacheLock)
    {
      SQLiteDatabase localSQLiteDatabase = ((UserAccounts)localObject1).openHelper.getReadableDatabase();
      String str;
      if (paramString != null)
      {
        str = "SELECT COUNT(*) FROM grants, accounts WHERE accounts_id=_id AND uid=? AND auth_token_type=? AND name=? AND type=?";
        localObject1 = new String[4];
        localObject1[0] = String.valueOf(paramInt);
        localObject1[1] = paramString;
        localObject1[2] = paramAccount.name;
        localObject1[3] = paramAccount.type;
      }
      for (;;)
      {
        if (DatabaseUtils.longForQuery(localSQLiteDatabase, str, (String[])localObject1) != 0L) {
          bool = true;
        }
        if ((bool) || (!ActivityManager.isRunningInTestHarness())) {
          break;
        }
        Log.d("AccountManagerService", "no credentials permission for usage of " + paramAccount + ", " + paramString + " by uid " + paramInt + " but ignoring since device is in test harness.");
        return true;
        str = "SELECT COUNT(*) FROM grants, accounts WHERE accounts_id=_id AND uid=? AND name=? AND type=?";
        localObject1 = new String[3];
        localObject1[0] = String.valueOf(paramInt);
        localObject1[1] = paramAccount.name;
        localObject1[2] = paramAccount.type;
      }
      return bool;
    }
  }
  
  private void initializeDebugDbSizeAndCompileSqlStatementForLogging(SQLiteDatabase paramSQLiteDatabase, UserAccounts paramUserAccounts)
  {
    int i = (int)getDebugTableRowCount(paramSQLiteDatabase);
    if (i >= 64) {
      UserAccounts.-set0(paramUserAccounts, (int)getDebugTableInsertionPoint(paramSQLiteDatabase));
    }
    for (;;)
    {
      compileSqlStatementForLogging(paramSQLiteDatabase, paramUserAccounts);
      return;
      UserAccounts.-set0(paramUserAccounts, i);
    }
  }
  
  private Account insertAccountIntoCacheLocked(UserAccounts paramUserAccounts, Account paramAccount)
  {
    Object localObject = (Account[])paramUserAccounts.accountCache.get(paramAccount.type);
    int i;
    Account[] arrayOfAccount;
    if (localObject != null)
    {
      i = localObject.length;
      arrayOfAccount = new Account[i + 1];
      if (localObject != null) {
        System.arraycopy(localObject, 0, arrayOfAccount, 0, i);
      }
      if (paramAccount.getAccessId() == null) {
        break label99;
      }
    }
    label99:
    for (localObject = paramAccount.getAccessId();; localObject = UUID.randomUUID().toString())
    {
      arrayOfAccount[i] = new Account(paramAccount, (String)localObject);
      paramUserAccounts.accountCache.put(paramAccount.type, arrayOfAccount);
      return arrayOfAccount[i];
      i = 0;
      break;
    }
  }
  
  private long insertExtraLocked(SQLiteDatabase paramSQLiteDatabase, long paramLong, String paramString1, String paramString2)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("key", paramString1);
    localContentValues.put("accounts_id", Long.valueOf(paramLong));
    localContentValues.put("value", paramString2);
    return paramSQLiteDatabase.insert("ceDb.extras", "key", localContentValues);
  }
  
  /* Error */
  private void installNotification(int paramInt1, Notification paramNotification, String paramString, int paramInt2)
  {
    // Byte code:
    //   0: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   3: lstore 5
    //   5: invokestatic 1537	android/app/NotificationManager:getService	()Landroid/app/INotificationManager;
    //   8: astore 7
    //   10: aload 7
    //   12: aload_3
    //   13: aload_3
    //   14: aconst_null
    //   15: iload_1
    //   16: aload_2
    //   17: iconst_1
    //   18: newarray <illegal type>
    //   20: iload 4
    //   22: invokeinterface 1543 8 0
    //   27: lload 5
    //   29: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   32: return
    //   33: astore_2
    //   34: lload 5
    //   36: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   39: aload_2
    //   40: athrow
    //   41: astore_2
    //   42: goto -15 -> 27
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	this	AccountManagerService
    //   0	45	1	paramInt1	int
    //   0	45	2	paramNotification	Notification
    //   0	45	3	paramString	String
    //   0	45	4	paramInt2	int
    //   3	32	5	l	long
    //   8	3	7	localINotificationManager	INotificationManager
    // Exception table:
    //   from	to	target	type
    //   5	10	33	finally
    //   10	27	33	finally
    //   10	27	41	android/os/RemoteException
  }
  
  private void invalidateAuthTokenLocked(UserAccounts paramUserAccounts, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2)
  {
    if ((paramString2 == null) || (paramString1 == null)) {
      return;
    }
    paramString2 = paramSQLiteDatabase.rawQuery("SELECT ceDb.authtokens._id, ceDb.accounts.name, ceDb.authtokens.type FROM ceDb.accounts JOIN ceDb.authtokens ON ceDb.accounts._id = ceDb.authtokens.accounts_id WHERE ceDb.authtokens.authtoken = ? AND ceDb.accounts.type = ?", new String[] { paramString2, paramString1 });
    try
    {
      if (paramString2.moveToNext())
      {
        long l = paramString2.getLong(0);
        String str1 = paramString2.getString(1);
        String str2 = paramString2.getString(2);
        paramSQLiteDatabase.delete("ceDb.authtokens", "_id=" + l, null);
        writeAuthTokenIntoCacheLocked(paramUserAccounts, paramSQLiteDatabase, new Account(str1, paramString1), str2, null);
      }
      return;
    }
    finally
    {
      paramString2.close();
    }
  }
  
  private void invalidateCustomTokenLocked(UserAccounts paramUserAccounts, String paramString1, String paramString2)
  {
    if ((paramString2 == null) || (paramString1 == null)) {
      return;
    }
    UserAccounts.-get0(paramUserAccounts).remove(paramString1, paramString2);
  }
  
  private boolean isAccountManagedByCaller(String paramString, int paramInt1, int paramInt2)
  {
    if (paramString == null) {
      return false;
    }
    return getTypesManagedByCaller(paramInt1, paramInt2).contains(paramString);
  }
  
  private boolean isAccountPresentForCaller(String paramString1, String paramString2)
  {
    if (getUserAccountsForCaller().accountCache.containsKey(paramString2))
    {
      paramString2 = (Account[])getUserAccountsForCaller().accountCache.get(paramString2);
      int j = paramString2.length;
      int i = 0;
      while (i < j)
      {
        if (paramString2[i].name.equals(paramString1)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  private boolean isAccountVisibleToCaller(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    if (paramString1 == null) {
      return false;
    }
    return getTypesVisibleToCaller(paramInt1, paramInt2, paramString2).contains(paramString1);
  }
  
  private boolean isCrossUser(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt2 != UserHandle.getCallingUserId())
    {
      bool1 = bool2;
      if (paramInt1 != Process.myUid())
      {
        bool1 = bool2;
        if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  private boolean isLocalUnlockedUser(int paramInt)
  {
    synchronized (this.mUsers)
    {
      boolean bool = this.mLocalUnlockedUsers.get(paramInt);
      return bool;
    }
  }
  
  private boolean isPermitted(String paramString, int paramInt, String... paramVarArgs)
  {
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      String str = paramVarArgs[i];
      if (this.mContext.checkCallingOrSelfPermission(str) == 0)
      {
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", "  caller uid " + paramInt + " has " + str);
        }
        int k = AppOpsManager.permissionToOpCode(str);
        if ((k == -1) || (this.mAppOpsManager.noteOp(k, paramInt, paramString) == 0)) {
          return true;
        }
      }
      i += 1;
    }
    return false;
  }
  
  private boolean isPrivileged(int paramInt)
  {
    int i = UserHandle.getUserId(paramInt);
    label95:
    for (;;)
    {
      try
      {
        PackageManager localPackageManager = this.mContext.createPackageContextAsUser("android", 0, new UserHandle(i)).getPackageManager();
        String[] arrayOfString = localPackageManager.getPackagesForUid(paramInt);
        i = arrayOfString.length;
        paramInt = 0;
        if (paramInt >= i) {
          break;
        }
        Object localObject = arrayOfString[paramInt];
        int j;
        paramInt += 1;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        try
        {
          localObject = localPackageManager.getPackageInfo((String)localObject, 0);
          if (localObject == null) {
            break label95;
          }
          j = ((PackageInfo)localObject).applicationInfo.privateFlags;
          if ((j & 0x8) == 0) {
            break label95;
          }
          return true;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
          return false;
        }
        localNameNotFoundException1 = localNameNotFoundException1;
        return false;
      }
    }
    return false;
  }
  
  private boolean isProfileOwner(int paramInt)
  {
    DevicePolicyManagerInternal localDevicePolicyManagerInternal = (DevicePolicyManagerInternal)LocalServices.getService(DevicePolicyManagerInternal.class);
    if (localDevicePolicyManagerInternal != null) {
      return localDevicePolicyManagerInternal.isActiveAdminWithPolicy(paramInt, -1);
    }
    return false;
  }
  
  private boolean isSystemUid(int paramInt)
  {
    long l = Binder.clearCallingIdentity();
    try
    {
      String[] arrayOfString = this.mPackageManager.getPackagesForUid(paramInt);
      Binder.restoreCallingIdentity(l);
      if (arrayOfString != null)
      {
        int i = arrayOfString.length;
        paramInt = 0;
        String str;
        PackageInfo localPackageInfo;
        int j;
        while (paramInt < i) {
          str = arrayOfString[paramInt];
        }
      }
    }
    finally
    {
      try
      {
        localPackageInfo = this.mPackageManager.getPackageInfo(str, 0);
        if (localPackageInfo == null) {
          break label111;
        }
        j = localPackageInfo.applicationInfo.flags;
        if ((j & 0x1) == 0) {
          break label111;
        }
        return true;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.w("AccountManagerService", String.format("Could not find package [%s]", new Object[] { str }), localNameNotFoundException);
        paramInt += 1;
      }
      localObject = finally;
      Binder.restoreCallingIdentity(l);
    }
    label111:
    Log.w("AccountManagerService", "No known packages with uid " + paramInt);
    return false;
  }
  
  private void logRecord(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, long paramLong, UserAccounts paramUserAccounts)
  {
    logRecord(paramSQLiteDatabase, paramString1, paramString2, paramLong, paramUserAccounts, getCallingUid());
  }
  
  private void logRecord(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, long paramLong, UserAccounts paramUserAccounts, int paramInt)
  {
    paramSQLiteDatabase = UserAccounts.-get6(paramUserAccounts);
    paramSQLiteDatabase.bindLong(1, paramLong);
    paramSQLiteDatabase.bindString(2, paramString1);
    paramSQLiteDatabase.bindString(3, DebugDbHelper.-get18().format(new Date()));
    paramSQLiteDatabase.bindLong(4, paramInt);
    paramSQLiteDatabase.bindString(5, paramString2);
    paramSQLiteDatabase.bindLong(6, UserAccounts.-get3(paramUserAccounts));
    paramSQLiteDatabase.execute();
    paramSQLiteDatabase.clearBindings();
    UserAccounts.-set0(paramUserAccounts, (UserAccounts.-get3(paramUserAccounts) + 1) % 64);
  }
  
  private void logRecord(UserAccounts paramUserAccounts, String paramString1, String paramString2)
  {
    logRecord(paramUserAccounts.openHelper.getWritableDatabase(), paramString1, paramString2, -1L, paramUserAccounts);
  }
  
  private void logRecordWithUid(UserAccounts paramUserAccounts, String paramString1, String paramString2, int paramInt)
  {
    logRecord(paramUserAccounts.openHelper.getWritableDatabase(), paramString1, paramString2, -1L, paramUserAccounts, paramInt);
  }
  
  private Intent newGrantCredentialsPermissionIntent(Account paramAccount, String paramString1, int paramInt, AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString2, boolean paramBoolean)
  {
    Intent localIntent = new Intent(this.mContext, GrantCredentialsPermissionActivity.class);
    if (paramBoolean) {
      localIntent.setFlags(268435456);
    }
    StringBuilder localStringBuilder = new StringBuilder().append(getCredentialPermissionNotificationId(paramAccount, paramString2, paramInt));
    if (paramString1 != null) {}
    for (;;)
    {
      localIntent.addCategory(String.valueOf(paramString1));
      localIntent.putExtra("account", paramAccount);
      localIntent.putExtra("authTokenType", paramString2);
      localIntent.putExtra("response", paramAccountAuthenticatorResponse);
      localIntent.putExtra("uid", paramInt);
      return localIntent;
      paramString1 = "";
    }
  }
  
  private Intent newRequestAccountAccessIntent(final Account paramAccount, final String paramString, final int paramInt, final RemoteCallback paramRemoteCallback)
  {
    newGrantCredentialsPermissionIntent(paramAccount, paramString, paramInt, new AccountAuthenticatorResponse(new IAccountAuthenticatorResponse.Stub()
    {
      private void handleAuthenticatorResponse(boolean paramAnonymousBoolean)
        throws RemoteException
      {
        AccountManagerService.this.cancelNotification(AccountManagerService.-wrap8(AccountManagerService.this, paramAccount, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", paramInt).intValue(), paramString, UserHandle.getUserHandleForUid(paramInt));
        if (paramRemoteCallback != null)
        {
          Bundle localBundle = new Bundle();
          localBundle.putBoolean("booleanResult", paramAnonymousBoolean);
          paramRemoteCallback.sendResult(localBundle);
        }
      }
      
      public void onError(int paramAnonymousInt, String paramAnonymousString)
        throws RemoteException
      {
        handleAuthenticatorResponse(false);
      }
      
      public void onRequestContinued() {}
      
      public void onResult(Bundle paramAnonymousBundle)
        throws RemoteException
      {
        handleAuthenticatorResponse(true);
      }
    }), "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", false);
  }
  
  private void onResult(IAccountManagerResponse paramIAccountManagerResponse, Bundle paramBundle)
  {
    if (paramBundle == null) {
      Log.e("AccountManagerService", "the result is unexpectedly null", new Exception());
    }
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", getClass().getSimpleName() + " calling onResult() on response " + paramIAccountManagerResponse);
    }
    try
    {
      paramIAccountManagerResponse.onResult(paramBundle);
      return;
    }
    catch (RemoteException paramIAccountManagerResponse)
    {
      while (!Log.isLoggable("AccountManagerService", 2)) {}
      Log.v("AccountManagerService", "failure while notifying response", paramIAccountManagerResponse);
    }
  }
  
  private void onUserRemoved(Intent arg1)
  {
    int i = ???.getIntExtra("android.intent.extra.user_handle", -1);
    if (i < 1) {
      return;
    }
    UserAccounts localUserAccounts;
    boolean bool;
    synchronized (this.mUsers)
    {
      localUserAccounts = (UserAccounts)this.mUsers.get(i);
      this.mUsers.remove(i);
      bool = this.mLocalUnlockedUsers.get(i);
      this.mLocalUnlockedUsers.delete(i);
      if (localUserAccounts == null) {}
    }
    synchronized (localUserAccounts.cacheLock)
    {
      localUserAccounts.openHelper.close();
      Log.i("AccountManagerService", "Removing database files for user " + i);
      deleteDbFileWarnIfFailed(new File(getDeDatabaseName(i)));
      if ((!StorageManager.isFileEncryptedNativeOrEmulated()) || (bool))
      {
        ??? = new File(getCeDatabaseName(i));
        if (???.exists()) {
          deleteDbFileWarnIfFailed(???);
        }
      }
      return;
      localObject1 = finally;
      throw ((Throwable)localObject1);
    }
  }
  
  private boolean permissionIsGranted(Account paramAccount, String paramString, int paramInt1, int paramInt2)
  {
    if (UserHandle.getAppId(paramInt1) == 1000)
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "Access to " + paramAccount + " granted calling uid is system");
      }
      return true;
    }
    if (isPrivileged(paramInt1))
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "Access to " + paramAccount + " granted calling uid " + paramInt1 + " privileged");
      }
      return true;
    }
    if ((paramAccount != null) && (isAccountManagedByCaller(paramAccount.type, paramInt1, paramInt2)))
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "Access to " + paramAccount + " granted calling uid " + paramInt1 + " manages the account");
      }
      return true;
    }
    if ((paramAccount != null) && (hasExplicitlyGrantedPermission(paramAccount, paramString, paramInt1)))
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "Access to " + paramAccount + " granted calling uid " + paramInt1 + " user granted access");
      }
      return true;
    }
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "Access to " + paramAccount + " not granted for uid " + paramInt1);
    }
    return false;
  }
  
  private void purgeOldGrants(UserAccounts paramUserAccounts)
  {
    for (;;)
    {
      synchronized (paramUserAccounts.cacheLock)
      {
        SQLiteDatabase localSQLiteDatabase = paramUserAccounts.openHelper.getWritableDatabase();
        paramUserAccounts = localSQLiteDatabase.query("grants", new String[] { "uid" }, null, null, "uid", null, null);
        try
        {
          if (!paramUserAccounts.moveToNext()) {
            break;
          }
          int j = paramUserAccounts.getInt(0);
          if (this.mPackageManager.getPackagesForUid(j) != null)
          {
            i = 1;
            if (i != 0) {
              continue;
            }
            Log.d("AccountManagerService", "deleting grants for UID " + j + " because its package is no longer installed");
            localSQLiteDatabase.delete("grants", "uid=?", new String[] { Integer.toString(j) });
            continue;
            paramUserAccounts = finally;
          }
        }
        finally
        {
          paramUserAccounts.close();
        }
      }
      int i = 0;
    }
    paramUserAccounts.close();
  }
  
  private void purgeOldGrantsAll()
  {
    SparseArray localSparseArray = this.mUsers;
    int i = 0;
    try
    {
      while (i < this.mUsers.size())
      {
        purgeOldGrants((UserAccounts)this.mUsers.valueAt(i));
        i += 1;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private String readPasswordInternal(UserAccounts paramUserAccounts, Account paramAccount)
  {
    if (paramAccount == null) {
      return null;
    }
    if (!isLocalUnlockedUser(UserAccounts.-get8(paramUserAccounts)))
    {
      Log.w("AccountManagerService", "Password is not available - user " + UserAccounts.-get8(paramUserAccounts) + " data is locked");
      return null;
    }
    synchronized (paramUserAccounts.cacheLock)
    {
      paramUserAccounts = CeDatabaseHelper.findAccountPasswordByNameAndType(paramUserAccounts.openHelper.getReadableDatabaseUserIsUnlocked(), paramAccount.name, paramAccount.type);
      return paramUserAccounts;
    }
  }
  
  private String readPreviousNameInternal(UserAccounts paramUserAccounts, Account paramAccount)
  {
    if (paramAccount == null) {
      return null;
    }
    Object localObject2;
    synchronized (paramUserAccounts.cacheLock)
    {
      localObject2 = (AtomicReference)UserAccounts.-get4(paramUserAccounts).get(paramAccount);
      if (localObject2 == null)
      {
        localObject2 = paramUserAccounts.openHelper.getReadableDatabase();
        String str = paramAccount.name;
        Object localObject3 = paramAccount.type;
        localObject2 = ((SQLiteDatabase)localObject2).query("accounts", new String[] { "previous_name" }, "name=? AND type=?", new String[] { str, localObject3 }, null, null, null);
        try
        {
          if (((Cursor)localObject2).moveToNext())
          {
            str = ((Cursor)localObject2).getString(0);
            localObject3 = new AtomicReference(str);
          }
        }
        finally
        {
          paramUserAccounts = finally;
        }
      }
      try
      {
        UserAccounts.-get4(paramUserAccounts).put(paramAccount, localObject3);
        ((Cursor)localObject2).close();
        return str;
      }
      finally
      {
        for (;;)
        {
          paramUserAccounts = finally;
        }
      }
      ((Cursor)localObject2).close();
      return null;
      ((Cursor)localObject2).close();
      throw paramUserAccounts;
    }
    paramUserAccounts = (String)((AtomicReference)localObject2).get();
    return paramUserAccounts;
  }
  
  private void removeAccountFromCacheLocked(UserAccounts paramUserAccounts, Account paramAccount)
  {
    Account[] arrayOfAccount = (Account[])paramUserAccounts.accountCache.get(paramAccount.type);
    ArrayList localArrayList;
    if (arrayOfAccount != null)
    {
      localArrayList = new ArrayList();
      int i = 0;
      int j = arrayOfAccount.length;
      while (i < j)
      {
        Account localAccount = arrayOfAccount[i];
        if (!localAccount.equals(paramAccount)) {
          localArrayList.add(localAccount);
        }
        i += 1;
      }
      if (!localArrayList.isEmpty()) {
        break label121;
      }
      paramUserAccounts.accountCache.remove(paramAccount.type);
    }
    for (;;)
    {
      UserAccounts.-get7(paramUserAccounts).remove(paramAccount);
      UserAccounts.-get1(paramUserAccounts).remove(paramAccount);
      UserAccounts.-get4(paramUserAccounts).remove(paramAccount);
      return;
      label121:
      arrayOfAccount = (Account[])localArrayList.toArray(new Account[localArrayList.size()]);
      paramUserAccounts.accountCache.put(paramAccount.type, arrayOfAccount);
    }
  }
  
  private boolean removeAccountInternal(UserAccounts paramUserAccounts, Account paramAccount, int paramInt)
  {
    boolean bool1 = false;
    boolean bool2 = isLocalUnlockedUser(UserAccounts.-get8(paramUserAccounts));
    if (!bool2) {
      Slog.i("AccountManagerService", "Removing account " + paramAccount + " while user " + UserAccounts.-get8(paramUserAccounts) + " is still locked. CE data will be removed later");
    }
    Object localObject3 = paramUserAccounts.cacheLock;
    if (bool2) {}
    long l;
    Object localObject2;
    for (;;)
    {
      try
      {
        ??? = paramUserAccounts.openHelper.getWritableDatabaseUserIsUnlocked();
        ((SQLiteDatabase)???).beginTransaction();
        try
        {
          l = getAccountIdLocked((SQLiteDatabase)???, paramAccount);
          if (l >= 0L)
          {
            ((SQLiteDatabase)???).delete("accounts", "name=? AND type=?", new String[] { paramAccount.name, paramAccount.type });
            if (bool2) {
              ((SQLiteDatabase)???).delete("ceDb.accounts", "name=? AND type=?", new String[] { paramAccount.name, paramAccount.type });
            }
            ((SQLiteDatabase)???).setTransactionSuccessful();
            bool1 = true;
          }
          ((SQLiteDatabase)???).endTransaction();
          if (bool1)
          {
            removeAccountFromCacheLocked(paramUserAccounts, paramAccount);
            sendAccountsChangedBroadcast(UserAccounts.-get8(paramUserAccounts));
            if (bool2)
            {
              localObject2 = DebugDbHelper.-get1();
              logRecord((SQLiteDatabase)???, (String)localObject2, "accounts", l, paramUserAccounts);
            }
          }
          else
          {
            l = Binder.clearCallingIdentity();
            try
            {
              int i = UserAccounts.-get8(paramUserAccounts);
              if (!canHaveProfile(i)) {
                break;
              }
              ??? = getUserManager().getUsers(true).iterator();
              if (!((Iterator)???).hasNext()) {
                break;
              }
              localObject2 = (UserInfo)((Iterator)???).next();
              if ((!((UserInfo)localObject2).isRestricted()) || (i != ((UserInfo)localObject2).restrictedProfileParentId)) {
                continue;
              }
              removeSharedAccountAsUser(paramAccount, ((UserInfo)localObject2).id, paramInt);
              continue;
              ??? = paramUserAccounts.openHelper.getWritableDatabase();
            }
            finally
            {
              Binder.restoreCallingIdentity(l);
            }
            continue;
          }
        }
        finally
        {
          ((SQLiteDatabase)???).endTransaction();
        }
        localObject2 = DebugDbHelper.-get2();
      }
      finally {}
    }
    Binder.restoreCallingIdentity(l);
    if (bool1) {
      synchronized (UserAccounts.-get2(paramUserAccounts))
      {
        paramUserAccounts = UserAccounts.-get2(paramUserAccounts).keySet().iterator();
        while (paramUserAccounts.hasNext())
        {
          localObject2 = (Pair)paramUserAccounts.next();
          if ((paramAccount.equals(((Pair)((Pair)localObject2).first).first)) && ("com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE".equals(((Pair)((Pair)localObject2).first).second)))
          {
            paramInt = ((Integer)((Pair)localObject2).second).intValue();
            this.mMessageHandler.post(new -boolean_removeAccountInternal_com_android_server_accounts_AccountManagerService.UserAccounts_accounts_android_accounts_Account_account_int_callingUid_LambdaImpl0(paramAccount, paramInt));
          }
        }
      }
    }
    return bool1;
  }
  
  private boolean removeSharedAccountAsUser(Account paramAccount, int paramInt1, int paramInt2)
  {
    UserAccounts localUserAccounts = getUserAccounts(handleIncomingUser(paramInt1));
    SQLiteDatabase localSQLiteDatabase = localUserAccounts.openHelper.getWritableDatabase();
    long l = getAccountIdFromSharedTable(localSQLiteDatabase, paramAccount);
    paramInt1 = localSQLiteDatabase.delete("shared_accounts", "name=? AND type=?", new String[] { paramAccount.name, paramAccount.type });
    if (paramInt1 > 0)
    {
      logRecord(localSQLiteDatabase, DebugDbHelper.-get1(), "shared_accounts", l, localUserAccounts, paramInt2);
      removeAccountInternal(localUserAccounts, paramAccount, paramInt2);
    }
    return paramInt1 > 0;
  }
  
  private Account renameAccountInternal(UserAccounts paramUserAccounts, Account paramAccount, String paramString)
  {
    cancelNotification(getSigninRequiredNotificationId(paramUserAccounts, paramAccount).intValue(), new UserHandle(UserAccounts.-get8(paramUserAccounts)));
    Object localObject2;
    Object localObject3;
    synchronized (UserAccounts.-get2(paramUserAccounts))
    {
      localObject2 = UserAccounts.-get2(paramUserAccounts).keySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Pair)((Iterator)localObject2).next();
        if (paramAccount.equals(((Pair)((Pair)localObject3).first).first)) {
          cancelNotification(((Integer)UserAccounts.-get2(paramUserAccounts).get(localObject3)).intValue(), new UserHandle(UserAccounts.-get8(paramUserAccounts)));
        }
      }
    }
    synchronized (paramUserAccounts.cacheLock)
    {
      localObject2 = paramUserAccounts.openHelper.getWritableDatabaseUserIsUnlocked();
      ((SQLiteDatabase)localObject2).beginTransaction();
      localObject3 = new Account(paramString, paramAccount.type);
    }
    try
    {
      long l = getAccountIdLocked((SQLiteDatabase)localObject2, paramAccount);
      if (l >= 0L)
      {
        localObject4 = new ContentValues();
        ((ContentValues)localObject4).put("name", paramString);
        String[] arrayOfString = new String[1];
        arrayOfString[0] = String.valueOf(l);
        ((SQLiteDatabase)localObject2).update("ceDb.accounts", (ContentValues)localObject4, "_id=?", arrayOfString);
        ((ContentValues)localObject4).put("previous_name", paramAccount.name);
        ((SQLiteDatabase)localObject2).update("accounts", (ContentValues)localObject4, "_id=?", arrayOfString);
        ((SQLiteDatabase)localObject2).setTransactionSuccessful();
        logRecord((SQLiteDatabase)localObject2, DebugDbHelper.-get3(), "accounts", l, paramUserAccounts);
      }
      ((SQLiteDatabase)localObject2).endTransaction();
      localObject2 = insertAccountIntoCacheLocked(paramUserAccounts, (Account)localObject3);
      localObject3 = (HashMap)UserAccounts.-get7(paramUserAccounts).get(paramAccount);
      Object localObject4 = (HashMap)UserAccounts.-get1(paramUserAccounts).get(paramAccount);
      removeAccountFromCacheLocked(paramUserAccounts, paramAccount);
      UserAccounts.-get7(paramUserAccounts).put(localObject2, localObject3);
      UserAccounts.-get1(paramUserAccounts).put(localObject2, localObject4);
      UserAccounts.-get4(paramUserAccounts).put(localObject2, new AtomicReference(paramAccount.name));
      int i = UserAccounts.-get8(paramUserAccounts);
      if (canHaveProfile(i))
      {
        localObject3 = getUserManager().getUsers(true).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (UserInfo)((Iterator)localObject3).next();
          if ((((UserInfo)localObject4).isRestricted()) && (((UserInfo)localObject4).restrictedProfileParentId == i))
          {
            renameSharedAccountAsUser(paramAccount, paramString, ((UserInfo)localObject4).id);
            continue;
            paramUserAccounts = finally;
            throw paramUserAccounts;
          }
        }
      }
    }
    finally
    {
      ((SQLiteDatabase)localObject2).endTransaction();
    }
    sendAccountsChangedBroadcast(UserAccounts.-get8(paramUserAccounts));
    return (Account)localObject2;
  }
  
  private void revokeAppPermission(Account paramAccount, String paramString, int paramInt)
  {
    if ((paramAccount == null) || (paramString == null))
    {
      Log.e("AccountManagerService", "revokeAppPermission: called with invalid arguments", new Exception());
      return;
    }
    UserAccounts localUserAccounts = getUserAccounts(UserHandle.getUserId(paramInt));
    synchronized (localUserAccounts.cacheLock)
    {
      SQLiteDatabase localSQLiteDatabase = localUserAccounts.openHelper.getWritableDatabase();
      localSQLiteDatabase.beginTransaction();
      try
      {
        long l = getAccountIdLocked(localSQLiteDatabase, paramAccount);
        if (l >= 0L)
        {
          localSQLiteDatabase.delete("grants", "accounts_id=? AND auth_token_type=? AND uid=?", new String[] { String.valueOf(l), paramString, String.valueOf(paramInt) });
          localSQLiteDatabase.setTransactionSuccessful();
        }
        localSQLiteDatabase.endTransaction();
        cancelNotification(getCredentialPermissionNotificationId(paramAccount, paramString, paramInt).intValue(), new UserHandle(UserAccounts.-get8(localUserAccounts)));
        paramString = this.mAppPermissionChangeListeners.iterator();
        while (paramString.hasNext())
        {
          ??? = (AccountManagerInternal.OnAppPermissionChangeListener)paramString.next();
          this.mMessageHandler.post(new -void_revokeAppPermission_android_accounts_Account_account_java_lang_String_authTokenType_int_uid_LambdaImpl0((AccountManagerInternal.OnAppPermissionChangeListener)???, paramAccount, paramInt));
          continue;
          paramAccount = finally;
        }
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
      }
    }
  }
  
  private boolean saveAuthTokenToDatabase(UserAccounts paramUserAccounts, Account paramAccount, String paramString1, String paramString2)
  {
    if ((paramAccount == null) || (paramString1 == null)) {
      return false;
    }
    cancelNotification(getSigninRequiredNotificationId(paramUserAccounts, paramAccount).intValue(), UserHandle.of(UserAccounts.-get8(paramUserAccounts)));
    synchronized (paramUserAccounts.cacheLock)
    {
      SQLiteDatabase localSQLiteDatabase = paramUserAccounts.openHelper.getWritableDatabaseUserIsUnlocked();
      localSQLiteDatabase.beginTransaction();
      try
      {
        long l = getAccountIdLocked(localSQLiteDatabase, paramAccount);
        if (l < 0L)
        {
          localSQLiteDatabase.endTransaction();
          return false;
        }
        localSQLiteDatabase.delete("ceDb.authtokens", "accounts_id=" + l + " AND " + "type" + "=?", new String[] { paramString1 });
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("accounts_id", Long.valueOf(l));
        localContentValues.put("type", paramString1);
        localContentValues.put("authtoken", paramString2);
        if (localSQLiteDatabase.insert("ceDb.authtokens", "authtoken", localContentValues) >= 0L)
        {
          localSQLiteDatabase.setTransactionSuccessful();
          writeAuthTokenIntoCacheLocked(paramUserAccounts, localSQLiteDatabase, paramAccount, paramString1, paramString2);
          localSQLiteDatabase.endTransaction();
          return true;
        }
        localSQLiteDatabase.endTransaction();
        return false;
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
      }
    }
  }
  
  private void saveCachedToken(UserAccounts paramUserAccounts, Account paramAccount, String paramString1, byte[] paramArrayOfByte, String paramString2, String paramString3, long paramLong)
  {
    if ((paramAccount == null) || (paramString2 == null)) {}
    while ((paramString1 == null) || (paramArrayOfByte == null)) {
      return;
    }
    cancelNotification(getSigninRequiredNotificationId(paramUserAccounts, paramAccount).intValue(), UserHandle.of(UserAccounts.-get8(paramUserAccounts)));
    synchronized (paramUserAccounts.cacheLock)
    {
      UserAccounts.-get0(paramUserAccounts).put(paramAccount, paramString3, paramString2, paramString1, paramArrayOfByte, paramLong);
      return;
    }
  }
  
  private static boolean scanArgs(String[] paramArrayOfString, String paramString)
  {
    if (paramArrayOfString != null)
    {
      int j = paramArrayOfString.length;
      int i = 0;
      while (i < j)
      {
        if (paramString.equals(paramArrayOfString[i])) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  private void sendAccountsChangedBroadcast(int paramInt)
  {
    Log.i("AccountManagerService", "the accounts changed, sending broadcast of " + ACCOUNTS_CHANGED_INTENT.getAction());
    this.mContext.sendBroadcastAsUser(ACCOUNTS_CHANGED_INTENT, new UserHandle(paramInt));
  }
  
  private void sendErrorResponse(IAccountManagerResponse paramIAccountManagerResponse, int paramInt, String paramString)
  {
    try
    {
      paramIAccountManagerResponse.onError(paramInt, paramString);
      return;
    }
    catch (RemoteException paramIAccountManagerResponse)
    {
      while (!Log.isLoggable("AccountManagerService", 2)) {}
      Log.v("AccountManagerService", "failure while notifying response", paramIAccountManagerResponse);
    }
  }
  
  private void sendResponse(IAccountManagerResponse paramIAccountManagerResponse, Bundle paramBundle)
  {
    try
    {
      paramIAccountManagerResponse.onResult(paramBundle);
      return;
    }
    catch (RemoteException paramIAccountManagerResponse)
    {
      while (!Log.isLoggable("AccountManagerService", 2)) {}
      Log.v("AccountManagerService", "failure while notifying response", paramIAccountManagerResponse);
    }
  }
  
  private void setPasswordInternal(UserAccounts paramUserAccounts, Account paramAccount, String paramString, int paramInt)
  {
    if (paramAccount == null) {
      return;
    }
    int j = 0;
    int k = 0;
    synchronized (paramUserAccounts.cacheLock)
    {
      SQLiteDatabase localSQLiteDatabase = paramUserAccounts.openHelper.getWritableDatabaseUserIsUnlocked();
      localSQLiteDatabase.beginTransaction();
      int i = j;
      try
      {
        ContentValues localContentValues = new ContentValues();
        i = j;
        localContentValues.put("password", paramString);
        i = j;
        long l = getAccountIdLocked(localSQLiteDatabase, paramAccount);
        i = k;
        if (l >= 0L)
        {
          i = j;
          String[] arrayOfString = new String[1];
          i = j;
          arrayOfString[0] = String.valueOf(l);
          i = j;
          localSQLiteDatabase.update("ceDb.accounts", localContentValues, "_id=?", arrayOfString);
          i = j;
          localSQLiteDatabase.delete("ceDb.authtokens", "accounts_id=?", arrayOfString);
          i = j;
          UserAccounts.-get1(paramUserAccounts).remove(paramAccount);
          i = j;
          UserAccounts.-get0(paramUserAccounts).remove(paramAccount);
          i = j;
          localSQLiteDatabase.setTransactionSuccessful();
          k = 1;
          j = 1;
          if (paramString != null)
          {
            i = k;
            if (paramString.length() != 0) {
              break label249;
            }
          }
          i = k;
        }
        for (paramAccount = DebugDbHelper.-get9();; paramAccount = DebugDbHelper.-get10())
        {
          i = k;
          logRecord(localSQLiteDatabase, paramAccount, "accounts", l, paramUserAccounts, paramInt);
          i = j;
          localSQLiteDatabase.endTransaction();
          if (i != 0) {
            sendAccountsChangedBroadcast(UserAccounts.-get8(paramUserAccounts));
          }
          return;
          label249:
          i = k;
        }
        paramUserAccounts = finally;
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
        if (i != 0) {
          sendAccountsChangedBroadcast(UserAccounts.-get8(paramUserAccounts));
        }
      }
    }
  }
  
  private void setUserdataInternalLocked(UserAccounts paramUserAccounts, Account paramAccount, String paramString1, String paramString2)
  {
    if ((paramAccount == null) || (paramString1 == null)) {
      return;
    }
    SQLiteDatabase localSQLiteDatabase = paramUserAccounts.openHelper.getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      long l1 = getAccountIdLocked(localSQLiteDatabase, paramAccount);
      if (l1 < 0L) {
        return;
      }
      long l2 = getExtrasIdLocked(localSQLiteDatabase, l1, paramString1);
      if (l2 < 0L)
      {
        l1 = insertExtraLocked(localSQLiteDatabase, l1, paramString1, paramString2);
        if (l1 >= 0L) {}
      }
      else
      {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("value", paramString2);
        int i = localSQLiteDatabase.update("extras", localContentValues, "_id=" + l2, null);
        if (1 != i) {
          return;
        }
      }
      writeUserDataIntoCacheLocked(paramUserAccounts, localSQLiteDatabase, paramAccount, paramString1, paramString2);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  private void showCantAddAccount(int paramInt1, int paramInt2)
  {
    Intent localIntent = new Intent(this.mContext, CantAddAccountActivity.class);
    localIntent.putExtra("android.accounts.extra.ERROR_CODE", paramInt1);
    localIntent.addFlags(268435456);
    long l = clearCallingIdentity();
    try
    {
      this.mContext.startActivityAsUser(localIntent, new UserHandle(paramInt2));
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  private static final String stringArrayToString(String[] paramArrayOfString)
  {
    String str = null;
    if (paramArrayOfString != null) {
      str = "[" + TextUtils.join(",", paramArrayOfString) + "]";
    }
    return str;
  }
  
  private void syncDeCeAccountsLocked(UserAccounts paramUserAccounts)
  {
    Preconditions.checkState(Thread.holdsLock(this.mUsers), "mUsers lock must be held");
    Object localObject = CeDatabaseHelper.findCeAccountsNotInDe(paramUserAccounts.openHelper.getReadableDatabaseUserIsUnlocked());
    if (!((List)localObject).isEmpty())
    {
      Slog.i("AccountManagerService", "Accounts " + localObject + " were previously deleted while user " + UserAccounts.-get8(paramUserAccounts) + " was locked. Removing accounts from CE tables");
      logRecord(paramUserAccounts, DebugDbHelper.-get11(), "accounts");
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        removeAccountInternal(paramUserAccounts, (Account)((Iterator)localObject).next(), Process.myUid());
      }
    }
  }
  
  private void syncSharedAccounts(int paramInt)
  {
    int j = 0;
    Account[] arrayOfAccount1 = getSharedAccountsAsUser(paramInt);
    if ((arrayOfAccount1 == null) || (arrayOfAccount1.length == 0)) {
      return;
    }
    Account[] arrayOfAccount2 = getAccountsAsUser(null, paramInt, this.mContext.getOpPackageName());
    if (UserManager.isSplitSystemUser()) {}
    for (int i = getUserManager().getUserInfo(paramInt).restrictedProfileParentId; i < 0; i = 0)
    {
      Log.w("AccountManagerService", "User " + paramInt + " has shared accounts, but no parent user");
      return;
    }
    int k = arrayOfAccount1.length;
    if (j < k)
    {
      Account localAccount = arrayOfAccount1[j];
      if (ArrayUtils.contains(arrayOfAccount2, localAccount)) {}
      for (;;)
      {
        j += 1;
        break;
        copyAccountToUser(null, localAccount, i, paramInt);
      }
    }
  }
  
  private boolean updateLastAuthenticatedTime(Account paramAccount)
  {
    UserAccounts localUserAccounts = getUserAccountsForCaller();
    synchronized (localUserAccounts.cacheLock)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("last_password_entry_time_millis_epoch", Long.valueOf(System.currentTimeMillis()));
      int i = localUserAccounts.openHelper.getWritableDatabase().update("accounts", localContentValues, "name=? AND type=?", new String[] { paramAccount.name, paramAccount.type });
      return i > 0;
    }
  }
  
  private void validateAccountsInternal(UserAccounts paramUserAccounts, boolean paramBoolean)
  {
    if (Log.isLoggable("AccountManagerService", 3)) {
      Log.d("AccountManagerService", "validateAccountsInternal " + UserAccounts.-get8(paramUserAccounts) + " isCeDatabaseAttached=" + paramUserAccounts.openHelper.isCeDatabaseAttached() + " userLocked=" + this.mLocalUnlockedUsers.get(UserAccounts.-get8(paramUserAccounts)));
    }
    if (SystemProperties.getBoolean("ro.alarm_boot", false)) {
      return;
    }
    if (paramBoolean) {
      this.mAuthenticatorCache.invalidateCache(UserAccounts.-get8(paramUserAccounts));
    }
    Object localObject10 = getAuthenticatorTypeAndUIDForUser(this.mAuthenticatorCache, UserAccounts.-get8(paramUserAccounts));
    paramBoolean = isLocalUnlockedUser(UserAccounts.-get8(paramUserAccounts));
    Object localObject7;
    int k;
    int j;
    Object localObject8;
    String str1;
    String str2;
    Object localObject5;
    for (;;)
    {
      synchronized (paramUserAccounts.cacheLock)
      {
        localObject7 = paramUserAccounts.openHelper.getWritableDatabase();
        k = 0;
        j = 0;
        localObject9 = ((SQLiteDatabase)localObject7).query("meta", new String[] { "key", "value" }, "key LIKE ?", new String[] { "auth_uid_for_type:%" }, null, null, "key");
        localObject8 = Sets.newHashSet();
        localObject1 = null;
        try
        {
          if (!((Cursor)localObject9).moveToNext()) {
            break;
          }
          str1 = TextUtils.split(localObject9.getString(0), ":")[1];
          str2 = ((Cursor)localObject9).getString(1);
          if ((TextUtils.isEmpty(str1)) || (TextUtils.isEmpty(str2)))
          {
            Slog.e("AccountManagerService", "Auth type empty: " + TextUtils.isEmpty(str1) + ", uid empty: " + TextUtils.isEmpty(str2));
            continue;
            paramUserAccounts = finally;
          }
        }
        finally
        {
          ((Cursor)localObject9).close();
        }
      }
      localObject5 = (Integer)((HashMap)localObject10).get(str1);
      if ((localObject5 != null) && (str2.equals(((Integer)localObject5).toString())))
      {
        ((HashMap)localObject10).remove(str1);
      }
      else
      {
        localObject5 = localObject1;
        if (localObject1 == null) {
          localObject5 = getUidsOfInstalledOrUpdatedPackagesAsUser(UserAccounts.-get8(paramUserAccounts));
        }
        localObject1 = localObject5;
        if (!((SparseBooleanArray)localObject5).get(Integer.parseInt(str2)))
        {
          ((HashSet)localObject8).add(str1);
          ((SQLiteDatabase)localObject7).delete("meta", "key=? AND value=?", new String[] { "auth_uid_for_type:" + str1, str2 });
          localObject1 = localObject5;
        }
      }
    }
    ((Cursor)localObject9).close();
    Object localObject1 = ((HashMap)localObject10).entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject5 = (Map.Entry)((Iterator)localObject1).next();
      localObject9 = new ContentValues();
      ((ContentValues)localObject9).put("key", "auth_uid_for_type:" + (String)((Map.Entry)localObject5).getKey());
      ((ContentValues)localObject9).put("value", (Integer)((Map.Entry)localObject5).getValue());
      ((SQLiteDatabase)localObject7).insertWithOnConflict("meta", null, (ContentValues)localObject9, 5);
    }
    Object localObject9 = ((SQLiteDatabase)localObject7).query("accounts", new String[] { "_id", "type", "name" }, null, null, null, null, "_id");
    int i = k;
    for (;;)
    {
      try
      {
        paramUserAccounts.accountCache.clear();
        i = k;
        localObject10 = new LinkedHashMap();
      }
      finally
      {
        try
        {
          long l;
          ((SQLiteDatabase)localObject7).delete("accounts", "_id=" + l, null);
          if (paramBoolean) {
            ((SQLiteDatabase)localObject7).delete("ceDb.accounts", "_id=" + l, null);
          }
          ((SQLiteDatabase)localObject7).setTransactionSuccessful();
          i = j;
          ((SQLiteDatabase)localObject7).endTransaction();
          k = 1;
          j = 1;
          i = k;
          logRecord((SQLiteDatabase)localObject7, DebugDbHelper.-get4(), "accounts", l, paramUserAccounts);
          i = k;
          localObject1 = new Account(str2, str1);
          i = k;
          UserAccounts.-get7(paramUserAccounts).remove(localObject1);
          i = k;
          UserAccounts.-get1(paramUserAccounts).remove(localObject1);
          i = k;
          UserAccounts.-get0(paramUserAccounts).remove((Account)localObject1);
          continue;
        }
        finally
        {
          i = j;
          ((SQLiteDatabase)localObject7).endTransaction();
          i = j;
        }
        localObject2 = finally;
        ((Cursor)localObject9).close();
        if (i == 0) {
          continue;
        }
        sendAccountsChangedBroadcast(UserAccounts.-get8(paramUserAccounts));
      }
      i = j;
      if (!((Cursor)localObject9).moveToNext()) {
        break;
      }
      i = j;
      l = ((Cursor)localObject9).getLong(0);
      i = j;
      str1 = ((Cursor)localObject9).getString(1);
      i = j;
      str2 = ((Cursor)localObject9).getString(2);
      i = j;
      if (((HashSet)localObject8).contains(str1))
      {
        i = j;
        Slog.w("AccountManagerService", "deleting account " + str2 + " because type " + str1 + "'s registered authenticator no longer exist.");
        i = j;
        ((SQLiteDatabase)localObject7).beginTransaction();
      }
      i = j;
      localObject5 = (ArrayList)((HashMap)localObject10).get(str1);
      localObject4 = localObject5;
      if (localObject5 == null)
      {
        i = j;
        localObject4 = new ArrayList();
        i = j;
        ((HashMap)localObject10).put(str1, localObject4);
      }
      i = j;
      ((ArrayList)localObject4).add(str2);
    }
    i = j;
    Object localObject4 = ((HashMap)localObject10).entrySet().iterator();
    for (;;)
    {
      i = j;
      if (!((Iterator)localObject4).hasNext()) {
        break;
      }
      i = j;
      localObject7 = (Map.Entry)((Iterator)localObject4).next();
      i = j;
      localObject5 = (String)((Map.Entry)localObject7).getKey();
      i = j;
      localObject7 = (ArrayList)((Map.Entry)localObject7).getValue();
      i = j;
      localObject8 = new Account[((ArrayList)localObject7).size()];
      k = 0;
      for (;;)
      {
        i = j;
        if (k >= localObject8.length) {
          break;
        }
        i = j;
        localObject8[k] = new Account((String)((ArrayList)localObject7).get(k), (String)localObject5, UUID.randomUUID().toString());
        k += 1;
      }
      i = j;
      paramUserAccounts.accountCache.put(localObject5, localObject8);
    }
    ((Cursor)localObject9).close();
    if (j != 0) {
      sendAccountsChangedBroadcast(UserAccounts.-get8(paramUserAccounts));
    }
  }
  
  public boolean accountAuthenticated(Account paramAccount)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", String.format("accountAuthenticated( account: %s, callerUid: %s)", new Object[] { paramAccount, Integer.valueOf(i) }));
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot notify authentication for accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    if ((canUserModifyAccounts(j, i)) && (canUserModifyAccountsForType(j, paramAccount.type, i))) {
      l = clearCallingIdentity();
    }
    try
    {
      getUserAccounts(j);
      boolean bool = updateLastAuthenticatedTime(paramAccount);
      return bool;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
    return false;
  }
  
  /* Error */
  public void addAccount(IAccountManagerResponse paramIAccountManagerResponse, final String paramString1, final String paramString2, final String[] paramArrayOfString, boolean paramBoolean, final Bundle paramBundle)
  {
    // Byte code:
    //   0: bipush 16
    //   2: invokestatic 2086	android/util/SeempLog:record	(I)I
    //   5: pop
    //   6: aload 6
    //   8: iconst_1
    //   9: invokestatic 607	android/os/Bundle:setDefusable	(Landroid/os/Bundle;Z)Landroid/os/Bundle;
    //   12: pop
    //   13: ldc -29
    //   15: iconst_2
    //   16: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   19: ifeq +98 -> 117
    //   22: ldc -29
    //   24: new 613	java/lang/StringBuilder
    //   27: dup
    //   28: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   31: ldc_w 2088
    //   34: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload_2
    //   38: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: ldc_w 2090
    //   44: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: aload_1
    //   48: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   51: ldc_w 2092
    //   54: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: aload_3
    //   58: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: ldc_w 2094
    //   64: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: aload 4
    //   69: invokestatic 2096	com/android/server/accounts/AccountManagerService:stringArrayToString	([Ljava/lang/String;)Ljava/lang/String;
    //   72: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: ldc_w 2098
    //   78: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   81: iload 5
    //   83: invokevirtual 1980	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   86: ldc_w 1293
    //   89: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   92: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   95: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   98: ldc_w 1295
    //   101: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   107: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   110: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: aload_1
    //   118: ifnonnull +14 -> 132
    //   121: new 2070	java/lang/IllegalArgumentException
    //   124: dup
    //   125: ldc_w 2100
    //   128: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   131: athrow
    //   132: aload_2
    //   133: ifnonnull +14 -> 147
    //   136: new 2070	java/lang/IllegalArgumentException
    //   139: dup
    //   140: ldc_w 2102
    //   143: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   146: athrow
    //   147: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   150: istore 7
    //   152: iload 7
    //   154: invokestatic 886	android/os/UserHandle:getUserId	(I)I
    //   157: istore 8
    //   159: aload_0
    //   160: iload 8
    //   162: iload 7
    //   164: invokespecial 2077	com/android/server/accounts/AccountManagerService:canUserModifyAccounts	(II)Z
    //   167: ifne +23 -> 190
    //   170: aload_1
    //   171: bipush 100
    //   173: ldc_w 2104
    //   176: invokeinterface 1876 3 0
    //   181: aload_0
    //   182: bipush 100
    //   184: iload 8
    //   186: invokespecial 2106	com/android/server/accounts/AccountManagerService:showCantAddAccount	(II)V
    //   189: return
    //   190: aload_0
    //   191: iload 8
    //   193: aload_2
    //   194: iload 7
    //   196: invokespecial 2079	com/android/server/accounts/AccountManagerService:canUserModifyAccountsForType	(ILjava/lang/String;I)Z
    //   199: ifne +23 -> 222
    //   202: aload_1
    //   203: bipush 101
    //   205: ldc_w 2108
    //   208: invokeinterface 1876 3 0
    //   213: aload_0
    //   214: bipush 101
    //   216: iload 8
    //   218: invokespecial 2106	com/android/server/accounts/AccountManagerService:showCantAddAccount	(II)V
    //   221: return
    //   222: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   225: istore 8
    //   227: aload 6
    //   229: ifnonnull +97 -> 326
    //   232: new 603	android/os/Bundle
    //   235: dup
    //   236: invokespecial 2109	android/os/Bundle:<init>	()V
    //   239: astore 6
    //   241: aload 6
    //   243: ldc_w 2111
    //   246: iload 7
    //   248: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   251: aload 6
    //   253: ldc_w 2117
    //   256: iload 8
    //   258: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   261: invokestatic 1280	android/os/UserHandle:getCallingUserId	()I
    //   264: istore 8
    //   266: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   269: lstore 9
    //   271: aload_0
    //   272: iload 8
    //   274: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   277: astore 11
    //   279: aload_0
    //   280: aload 11
    //   282: invokestatic 2119	com/android/server/accounts/AccountManagerService$DebugDbHelper:-get5	()Ljava/lang/String;
    //   285: ldc -47
    //   287: iload 7
    //   289: invokespecial 2121	com/android/server/accounts/AccountManagerService:logRecordWithUid	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Ljava/lang/String;Ljava/lang/String;I)V
    //   292: new 57	com/android/server/accounts/AccountManagerService$9
    //   295: dup
    //   296: aload_0
    //   297: aload_0
    //   298: aload 11
    //   300: aload_1
    //   301: aload_2
    //   302: iload 5
    //   304: iconst_1
    //   305: aconst_null
    //   306: iconst_0
    //   307: iconst_1
    //   308: aload_3
    //   309: aload 4
    //   311: aload 6
    //   313: aload_2
    //   314: invokespecial 2124	com/android/server/accounts/AccountManagerService$9:<init>	(Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/IAccountManagerResponse;Ljava/lang/String;ZZLjava/lang/String;ZZLjava/lang/String;[Ljava/lang/String;Landroid/os/Bundle;Ljava/lang/String;)V
    //   317: invokevirtual 2125	com/android/server/accounts/AccountManagerService$9:bind	()V
    //   320: lload 9
    //   322: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   325: return
    //   326: goto -85 -> 241
    //   329: astore_1
    //   330: lload 9
    //   332: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   335: aload_1
    //   336: athrow
    //   337: astore_1
    //   338: goto -125 -> 213
    //   341: astore_1
    //   342: goto -161 -> 181
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	345	0	this	AccountManagerService
    //   0	345	1	paramIAccountManagerResponse	IAccountManagerResponse
    //   0	345	2	paramString1	String
    //   0	345	3	paramString2	String
    //   0	345	4	paramArrayOfString	String[]
    //   0	345	5	paramBoolean	boolean
    //   0	345	6	paramBundle	Bundle
    //   150	138	7	i	int
    //   157	116	8	j	int
    //   269	62	9	l	long
    //   277	22	11	localUserAccounts	UserAccounts
    // Exception table:
    //   from	to	target	type
    //   271	320	329	finally
    //   202	213	337	android/os/RemoteException
    //   170	181	341	android/os/RemoteException
  }
  
  /* Error */
  public void addAccountAsUser(IAccountManagerResponse paramIAccountManagerResponse, final String paramString1, final String paramString2, final String[] paramArrayOfString, boolean paramBoolean, final Bundle paramBundle, int paramInt)
  {
    // Byte code:
    //   0: aload 6
    //   2: iconst_1
    //   3: invokestatic 607	android/os/Bundle:setDefusable	(Landroid/os/Bundle;Z)Landroid/os/Bundle;
    //   6: pop
    //   7: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   10: istore 8
    //   12: ldc -29
    //   14: iconst_2
    //   15: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   18: ifeq +109 -> 127
    //   21: ldc -29
    //   23: new 613	java/lang/StringBuilder
    //   26: dup
    //   27: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   30: ldc_w 2088
    //   33: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   36: aload_2
    //   37: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: ldc_w 2090
    //   43: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   46: aload_1
    //   47: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   50: ldc_w 2092
    //   53: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: aload_3
    //   57: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: ldc_w 2094
    //   63: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: aload 4
    //   68: invokestatic 2096	com/android/server/accounts/AccountManagerService:stringArrayToString	([Ljava/lang/String;)Ljava/lang/String;
    //   71: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: ldc_w 2098
    //   77: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: iload 5
    //   82: invokevirtual 1980	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   85: ldc_w 1293
    //   88: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   94: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   97: ldc_w 1295
    //   100: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   106: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   109: ldc_w 2129
    //   112: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   115: iload 7
    //   117: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   120: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   123: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   126: pop
    //   127: aload_1
    //   128: ifnonnull +14 -> 142
    //   131: new 2070	java/lang/IllegalArgumentException
    //   134: dup
    //   135: ldc_w 2100
    //   138: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   141: athrow
    //   142: aload_2
    //   143: ifnonnull +14 -> 157
    //   146: new 2070	java/lang/IllegalArgumentException
    //   149: dup
    //   150: ldc_w 2102
    //   153: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   156: athrow
    //   157: aload_0
    //   158: iload 8
    //   160: iload 7
    //   162: invokespecial 2131	com/android/server/accounts/AccountManagerService:isCrossUser	(II)Z
    //   165: ifeq +38 -> 203
    //   168: new 938	java/lang/SecurityException
    //   171: dup
    //   172: ldc_w 2133
    //   175: iconst_2
    //   176: anewarray 954	java/lang/Object
    //   179: dup
    //   180: iconst_0
    //   181: invokestatic 1280	android/os/UserHandle:getCallingUserId	()I
    //   184: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   187: aastore
    //   188: dup
    //   189: iconst_1
    //   190: iload 7
    //   192: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   195: aastore
    //   196: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   199: invokespecial 941	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   202: athrow
    //   203: aload_0
    //   204: iload 7
    //   206: iload 8
    //   208: invokespecial 2077	com/android/server/accounts/AccountManagerService:canUserModifyAccounts	(II)Z
    //   211: ifne +23 -> 234
    //   214: aload_1
    //   215: bipush 100
    //   217: ldc_w 2104
    //   220: invokeinterface 1876 3 0
    //   225: aload_0
    //   226: bipush 100
    //   228: iload 7
    //   230: invokespecial 2106	com/android/server/accounts/AccountManagerService:showCantAddAccount	(II)V
    //   233: return
    //   234: aload_0
    //   235: iload 7
    //   237: aload_2
    //   238: iload 8
    //   240: invokespecial 2079	com/android/server/accounts/AccountManagerService:canUserModifyAccountsForType	(ILjava/lang/String;I)Z
    //   243: ifne +23 -> 266
    //   246: aload_1
    //   247: bipush 101
    //   249: ldc_w 2108
    //   252: invokeinterface 1876 3 0
    //   257: aload_0
    //   258: bipush 101
    //   260: iload 7
    //   262: invokespecial 2106	com/android/server/accounts/AccountManagerService:showCantAddAccount	(II)V
    //   265: return
    //   266: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   269: istore 8
    //   271: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   274: istore 9
    //   276: aload 6
    //   278: ifnonnull +92 -> 370
    //   281: new 603	android/os/Bundle
    //   284: dup
    //   285: invokespecial 2109	android/os/Bundle:<init>	()V
    //   288: astore 6
    //   290: aload 6
    //   292: ldc_w 2111
    //   295: iload 9
    //   297: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   300: aload 6
    //   302: ldc_w 2117
    //   305: iload 8
    //   307: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   310: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   313: lstore 10
    //   315: aload_0
    //   316: iload 7
    //   318: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   321: astore 12
    //   323: aload_0
    //   324: aload 12
    //   326: invokestatic 2119	com/android/server/accounts/AccountManagerService$DebugDbHelper:-get5	()Ljava/lang/String;
    //   329: ldc -47
    //   331: iload 7
    //   333: invokespecial 2121	com/android/server/accounts/AccountManagerService:logRecordWithUid	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Ljava/lang/String;Ljava/lang/String;I)V
    //   336: new 25	com/android/server/accounts/AccountManagerService$10
    //   339: dup
    //   340: aload_0
    //   341: aload_0
    //   342: aload 12
    //   344: aload_1
    //   345: aload_2
    //   346: iload 5
    //   348: iconst_1
    //   349: aconst_null
    //   350: iconst_0
    //   351: iconst_1
    //   352: aload_3
    //   353: aload 4
    //   355: aload 6
    //   357: aload_2
    //   358: invokespecial 2134	com/android/server/accounts/AccountManagerService$10:<init>	(Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/IAccountManagerResponse;Ljava/lang/String;ZZLjava/lang/String;ZZLjava/lang/String;[Ljava/lang/String;Landroid/os/Bundle;Ljava/lang/String;)V
    //   361: invokevirtual 2135	com/android/server/accounts/AccountManagerService$10:bind	()V
    //   364: lload 10
    //   366: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   369: return
    //   370: goto -80 -> 290
    //   373: astore_1
    //   374: lload 10
    //   376: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   379: aload_1
    //   380: athrow
    //   381: astore_1
    //   382: goto -125 -> 257
    //   385: astore_1
    //   386: goto -161 -> 225
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	389	0	this	AccountManagerService
    //   0	389	1	paramIAccountManagerResponse	IAccountManagerResponse
    //   0	389	2	paramString1	String
    //   0	389	3	paramString2	String
    //   0	389	4	paramArrayOfString	String[]
    //   0	389	5	paramBoolean	boolean
    //   0	389	6	paramBundle	Bundle
    //   0	389	7	paramInt	int
    //   10	296	8	i	int
    //   274	22	9	j	int
    //   313	62	10	l	long
    //   321	22	12	localUserAccounts	UserAccounts
    // Exception table:
    //   from	to	target	type
    //   315	364	373	finally
    //   246	257	381	android/os/RemoteException
    //   214	225	385	android/os/RemoteException
  }
  
  public boolean addAccountExplicitly(Account paramAccount, String paramString, Bundle paramBundle)
  {
    Bundle.setDefusable(paramBundle, true);
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "addAccountExplicitly: " + paramAccount + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot explicitly add accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    long l = clearCallingIdentity();
    try
    {
      boolean bool = addAccountInternal(getUserAccounts(j), paramAccount, paramString, paramBundle, i);
      return bool;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void addSharedAccountsFromParentUser(int paramInt1, int paramInt2)
  {
    checkManageOrCreateUsersPermission("addSharedAccountsFromParentUser");
    Account[] arrayOfAccount = getAccountsAsUser(null, paramInt1, this.mContext.getOpPackageName());
    paramInt1 = 0;
    int i = arrayOfAccount.length;
    while (paramInt1 < i)
    {
      addSharedAccountAsUser(arrayOfAccount[paramInt1], paramInt2);
      paramInt1 += 1;
    }
  }
  
  protected void cancelNotification(int paramInt, UserHandle paramUserHandle)
  {
    cancelNotification(paramInt, this.mContext.getPackageName(), paramUserHandle);
  }
  
  protected void cancelNotification(int paramInt, String paramString, UserHandle paramUserHandle)
  {
    long l = clearCallingIdentity();
    try
    {
      INotificationManager.Stub.asInterface(ServiceManager.getService("notification")).cancelNotificationWithTag(paramString, null, paramInt, paramUserHandle.getIdentifier());
      restoreCallingIdentity(l);
      return;
    }
    catch (RemoteException paramString)
    {
      paramString = paramString;
      restoreCallingIdentity(l);
      return;
    }
    finally
    {
      paramString = finally;
      restoreCallingIdentity(l);
      throw paramString;
    }
  }
  
  public void clearPassword(Account paramAccount)
  {
    SeempLog.record(19);
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "clearPassword: " + paramAccount + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot clear passwords for accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    long l = clearCallingIdentity();
    try
    {
      setPasswordInternal(getUserAccounts(j), paramAccount, null, i);
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void confirmCredentialsAsUser(IAccountManagerResponse paramIAccountManagerResponse, final Account paramAccount, final Bundle paramBundle, boolean paramBoolean, int paramInt)
  {
    Bundle.setDefusable(paramBundle, true);
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "confirmCredentials: " + paramAccount + ", response " + paramIAccountManagerResponse + ", expectActivityLaunch " + paramBoolean + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (isCrossUser(i, paramInt)) {
      throw new SecurityException(String.format("User %s trying to confirm account credentials for %s", new Object[] { Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(paramInt) }));
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    long l = clearCallingIdentity();
    try
    {
      new Session(this, getUserAccounts(paramInt), paramIAccountManagerResponse, paramAccount.type, paramBoolean, true, paramAccount.name, true, true)
      {
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.confirmCredentials(this, paramAccount, paramBundle);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          return super.toDebugString(paramAnonymousLong) + ", confirmCredentials" + ", " + paramAccount;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void copyAccountToUser(final IAccountManagerResponse paramIAccountManagerResponse, final Account paramAccount, final int paramInt1, int paramInt2)
  {
    if (isCrossUser(Binder.getCallingUid(), -1)) {
      throw new SecurityException("Calling copyAccountToUser requires android.permission.INTERACT_ACROSS_USERS_FULL");
    }
    UserAccounts localUserAccounts1 = getUserAccounts(paramInt1);
    final UserAccounts localUserAccounts2 = getUserAccounts(paramInt2);
    if ((localUserAccounts1 == null) || (localUserAccounts2 == null))
    {
      if (paramIAccountManagerResponse != null)
      {
        paramAccount = new Bundle();
        paramAccount.putBoolean("booleanResult", false);
      }
      try
      {
        paramIAccountManagerResponse.onResult(paramAccount);
        return;
      }
      catch (RemoteException paramIAccountManagerResponse)
      {
        Slog.w("AccountManagerService", "Failed to report error back to the client." + paramIAccountManagerResponse);
        return;
      }
    }
    Slog.d("AccountManagerService", "Copying account " + paramAccount.name + " from user " + paramInt1 + " to user " + paramInt2);
    long l = clearCallingIdentity();
    try
    {
      new Session(this, localUserAccounts1, paramIAccountManagerResponse, paramAccount.type, false, false, paramAccount.name, false)
      {
        public void onResult(Bundle paramAnonymousBundle)
        {
          Bundle.setDefusable(paramAnonymousBundle, true);
          if ((paramAnonymousBundle != null) && (paramAnonymousBundle.getBoolean("booleanResult", false)))
          {
            AccountManagerService.-wrap13(jdField_this, paramIAccountManagerResponse, paramAnonymousBundle, paramAccount, localUserAccounts2, paramInt1);
            return;
          }
          super.onResult(paramAnonymousBundle);
        }
        
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.getAccountCredentialsForCloning(this, paramAccount);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          return super.toDebugString(paramAnonymousLong) + ", getAccountCredentialsForClone" + ", " + paramAccount.type;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  public android.content.IntentSender createRequestAccountAccessIntentSenderAsUser(Account paramAccount, String paramString, UserHandle paramUserHandle)
  {
    // Byte code:
    //   0: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   3: invokestatic 1487	android/os/UserHandle:getAppId	(I)I
    //   6: sipush 1000
    //   9: if_icmpeq +14 -> 23
    //   12: new 938	java/lang/SecurityException
    //   15: dup
    //   16: ldc_w 2206
    //   19: invokespecial 941	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   22: athrow
    //   23: aload_1
    //   24: ldc_w 2208
    //   27: invokestatic 2211	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   30: pop
    //   31: aload_2
    //   32: ldc_w 2213
    //   35: invokestatic 2211	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   38: pop
    //   39: aload_3
    //   40: ldc_w 2215
    //   43: invokestatic 2211	com/android/internal/util/Preconditions:checkNotNull	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   46: pop
    //   47: aload_3
    //   48: invokevirtual 1091	android/os/UserHandle:getIdentifier	()I
    //   51: istore 4
    //   53: iload 4
    //   55: iconst_0
    //   56: ldc_w 2216
    //   59: ldc_w 2218
    //   62: invokestatic 2222	com/android/internal/util/Preconditions:checkArgumentInRange	(IIILjava/lang/String;)I
    //   65: pop
    //   66: aload_0
    //   67: getfield 281	com/android/server/accounts/AccountManagerService:mPackageManager	Landroid/content/pm/PackageManager;
    //   70: aload_2
    //   71: iload 4
    //   73: invokevirtual 2225	android/content/pm/PackageManager:getPackageUidAsUser	(Ljava/lang/String;I)I
    //   76: istore 5
    //   78: aload_0
    //   79: aload_1
    //   80: aload_2
    //   81: iload 5
    //   83: aconst_null
    //   84: invokespecial 301	com/android/server/accounts/AccountManagerService:newRequestAccountAccessIntent	(Landroid/accounts/Account;Ljava/lang/String;ILandroid/os/RemoteCallback;)Landroid/content/Intent;
    //   87: astore_1
    //   88: invokestatic 970	android/os/Binder:clearCallingIdentity	()J
    //   91: lstore 6
    //   93: aload_0
    //   94: getfield 484	com/android/server/accounts/AccountManagerService:mContext	Landroid/content/Context;
    //   97: iconst_0
    //   98: aload_1
    //   99: ldc_w 2226
    //   102: aconst_null
    //   103: new 539	android/os/UserHandle
    //   106: dup
    //   107: iload 4
    //   109: invokespecial 860	android/os/UserHandle:<init>	(I)V
    //   112: invokestatic 1080	android/app/PendingIntent:getActivityAsUser	(Landroid/content/Context;ILandroid/content/Intent;ILandroid/os/Bundle;Landroid/os/UserHandle;)Landroid/app/PendingIntent;
    //   115: invokevirtual 2230	android/app/PendingIntent:getIntentSender	()Landroid/content/IntentSender;
    //   118: astore_1
    //   119: lload 6
    //   121: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   124: aload_1
    //   125: areturn
    //   126: astore_1
    //   127: ldc -29
    //   129: new 613	java/lang/StringBuilder
    //   132: dup
    //   133: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   136: ldc_w 2232
    //   139: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload_2
    //   143: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokestatic 2012	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   152: pop
    //   153: aconst_null
    //   154: areturn
    //   155: astore_1
    //   156: lload 6
    //   158: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   161: aload_1
    //   162: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	163	0	this	AccountManagerService
    //   0	163	1	paramAccount	Account
    //   0	163	2	paramString	String
    //   0	163	3	paramUserHandle	UserHandle
    //   51	57	4	i	int
    //   76	6	5	j	int
    //   91	66	6	l	long
    // Exception table:
    //   from	to	target	type
    //   66	78	126	android/content/pm/PackageManager$NameNotFoundException
    //   93	119	155	finally
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump AccountsManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + "android.permission.DUMP");
      return;
    }
    if (!scanArgs(paramArrayOfString, "--checkin")) {}
    for (boolean bool = scanArgs(paramArrayOfString, "-c");; bool = true)
    {
      paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
      Iterator localIterator = getUserManager().getUsers().iterator();
      while (localIterator.hasNext())
      {
        UserInfo localUserInfo = (UserInfo)localIterator.next();
        paramPrintWriter.println("User " + localUserInfo + ":");
        paramPrintWriter.increaseIndent();
        dumpUser(getUserAccounts(localUserInfo.id), paramFileDescriptor, paramPrintWriter, paramArrayOfString, bool);
        paramPrintWriter.println();
        paramPrintWriter.decreaseIndent();
      }
    }
  }
  
  public void editProperties(IAccountManagerResponse paramIAccountManagerResponse, final String paramString, boolean paramBoolean)
  {
    SeempLog.record(21);
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "editProperties: accountType " + paramString + ", response " + paramIAccountManagerResponse + ", expectActivityLaunch " + paramBoolean + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    int j = UserHandle.getCallingUserId();
    if ((isAccountManagedByCaller(paramString, i, j)) || (isSystemUid(i))) {
      l = clearCallingIdentity();
    }
    try
    {
      new Session(this, getUserAccounts(j), paramIAccountManagerResponse, paramString, paramBoolean, true, null, false)
      {
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.editProperties(this, this.mAccountType);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          return super.toDebugString(paramAnonymousLong) + ", editProperties" + ", accountType " + paramString;
        }
      }.bind();
      restoreCallingIdentity(l);
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
    throw new SecurityException(String.format("uid %s cannot edit authenticator properites for account type: %s", new Object[] { Integer.valueOf(i), paramString }));
  }
  
  public void finishSessionAsUser(IAccountManagerResponse paramIAccountManagerResponse, final Bundle paramBundle1, boolean paramBoolean, Bundle paramBundle2, int paramInt)
  {
    Bundle.setDefusable(paramBundle1, true);
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "finishSession: response " + paramIAccountManagerResponse + ", expectActivityLaunch " + paramBoolean + ", caller's uid " + i + ", caller's user id " + UserHandle.getCallingUserId() + ", pid " + Binder.getCallingPid() + ", for user id " + paramInt);
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if ((paramBundle1 == null) || (paramBundle1.size() == 0)) {
      throw new IllegalArgumentException("sessionBundle is empty");
    }
    if (isCrossUser(i, paramInt)) {
      throw new SecurityException(String.format("User %s trying to finish session for %s without cross user permission", new Object[] { Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(paramInt) }));
    }
    if (!isSystemUid(i)) {
      throw new SecurityException(String.format("uid %s cannot finish session because it's not system uid.", new Object[] { Integer.valueOf(i) }));
    }
    if (!canUserModifyAccounts(paramInt, i))
    {
      sendErrorResponse(paramIAccountManagerResponse, 100, "User is not allowed to add an account!");
      showCantAddAccount(100, paramInt);
      return;
    }
    int j = Binder.getCallingPid();
    final String str;
    try
    {
      paramBundle1 = CryptoHelper.getInstance().decryptBundle(paramBundle1);
      if (paramBundle1 == null)
      {
        sendErrorResponse(paramIAccountManagerResponse, 8, "failed to decrypt session bundle");
        return;
      }
      str = paramBundle1.getString("accountType");
      if (TextUtils.isEmpty(str))
      {
        sendErrorResponse(paramIAccountManagerResponse, 7, "accountType is empty");
        return;
      }
      if (paramBundle2 != null) {
        paramBundle1.putAll(paramBundle2);
      }
      paramBundle1.putInt("callerUid", i);
      paramBundle1.putInt("callerPid", j);
      if (!canUserModifyAccountsForType(paramInt, str, i))
      {
        sendErrorResponse(paramIAccountManagerResponse, 101, "User cannot modify accounts of this type (policy).");
        showCantAddAccount(101, paramInt);
        return;
      }
    }
    catch (GeneralSecurityException paramBundle1)
    {
      if (Log.isLoggable("AccountManagerService", 3)) {
        Log.v("AccountManagerService", "Failed to decrypt session bundle!", paramBundle1);
      }
      sendErrorResponse(paramIAccountManagerResponse, 8, "failed to decrypt session bundle");
      return;
    }
    long l = clearCallingIdentity();
    try
    {
      paramBundle2 = getUserAccounts(paramInt);
      logRecordWithUid(paramBundle2, DebugDbHelper.-get7(), "accounts", i);
      new Session(this, paramBundle2, paramIAccountManagerResponse, str, paramBoolean, true, null, false, true)
      {
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.finishSession(this, this.mAccountType, paramBundle1);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          return super.toDebugString(paramAnonymousLong) + ", finishSession" + ", accountType " + str;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public Account[] getAccounts(int paramInt, String paramString)
  {
    int i = Binder.getCallingUid();
    paramString = getTypesVisibleToCaller(i, paramInt, paramString);
    if (paramString.isEmpty()) {
      return new Account[0];
    }
    long l = clearCallingIdentity();
    try
    {
      paramString = getAccountsInternal(getUserAccounts(paramInt), i, null, paramString);
      return paramString;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public Account[] getAccounts(String paramString1, String paramString2)
  {
    return getAccountsAsUser(paramString1, UserHandle.getCallingUserId(), paramString2);
  }
  
  public Account[] getAccountsAsUser(String paramString1, int paramInt, String paramString2)
  {
    return getAccountsAsUser(paramString1, paramInt, null, -1, paramString2);
  }
  
  public void getAccountsByFeatures(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String[] arg3, String paramString2)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "getAccounts: accountType " + paramString1 + ", response " + paramIAccountManagerResponse + ", features " + stringArrayToString(???) + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramString1 == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!getTypesVisibleToCaller(i, j, paramString2).contains(paramString1))
    {
      paramString1 = new Bundle();
      paramString1.putParcelableArray("accounts", new Account[0]);
      try
      {
        paramIAccountManagerResponse.onResult(paramString1);
        return;
      }
      catch (RemoteException paramIAccountManagerResponse)
      {
        Log.e("AccountManagerService", "Cannot respond to caller do to exception.", paramIAccountManagerResponse);
        return;
      }
    }
    long l = clearCallingIdentity();
    try
    {
      paramString2 = getUserAccounts(j);
      if ((??? == null) || (???.length == 0)) {
        synchronized (paramString2.cacheLock)
        {
          paramString1 = getAccountsFromCacheLocked(paramString2, paramString1, i, null);
          ??? = new Bundle();
          ???.putParcelableArray("accounts", paramString1);
          onResult(paramIAccountManagerResponse, ???);
          return;
        }
      }
      new GetAccountsByTypeAndFeatureSession(paramString2, paramIAccountManagerResponse, paramString1, ???, i).bind();
    }
    finally
    {
      restoreCallingIdentity(l);
    }
    restoreCallingIdentity(l);
  }
  
  public Account[] getAccountsByTypeForPackage(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      int i = AppGlobals.getPackageManager().getPackageUid(paramString2, 8192, UserHandle.getCallingUserId());
      return getAccountsAsUser(paramString1, UserHandle.getCallingUserId(), paramString2, i, paramString3);
    }
    catch (RemoteException paramString1)
    {
      Slog.e("AccountManagerService", "Couldn't determine the packageUid for " + paramString2 + paramString1);
    }
    return new Account[0];
  }
  
  public Account[] getAccountsForPackage(String paramString1, int paramInt, String paramString2)
  {
    int i = Binder.getCallingUid();
    if (!UserHandle.isSameApp(i, Process.myUid())) {
      throw new SecurityException("getAccountsForPackage() called from unauthorized uid " + i + " with uid=" + paramInt);
    }
    return getAccountsAsUser(null, UserHandle.getCallingUserId(), paramString1, paramInt, paramString2);
  }
  
  protected Account[] getAccountsFromCacheLocked(UserAccounts paramUserAccounts, String paramString1, int paramInt, String paramString2)
  {
    if (paramString1 != null)
    {
      paramString1 = (Account[])paramUserAccounts.accountCache.get(paramString1);
      if (paramString1 == null) {
        return EMPTY_ACCOUNT_ARRAY;
      }
      return filterSharedAccounts(paramUserAccounts, (Account[])Arrays.copyOf(paramString1, paramString1.length), paramInt, paramString2);
    }
    int i = 0;
    paramString1 = paramUserAccounts.accountCache.values().iterator();
    while (paramString1.hasNext()) {
      i += ((Account[])paramString1.next()).length;
    }
    if (i == 0) {
      return EMPTY_ACCOUNT_ARRAY;
    }
    paramString1 = new Account[i];
    i = 0;
    Iterator localIterator = paramUserAccounts.accountCache.values().iterator();
    while (localIterator.hasNext())
    {
      Account[] arrayOfAccount = (Account[])localIterator.next();
      System.arraycopy(arrayOfAccount, 0, paramString1, i, arrayOfAccount.length);
      i += arrayOfAccount.length;
    }
    return filterSharedAccounts(paramUserAccounts, paramString1, paramInt, paramString2);
  }
  
  public AccountAndUser[] getAllAccounts()
  {
    List localList = getUserManager().getUsers(true);
    int[] arrayOfInt = new int[localList.size()];
    int i = 0;
    while (i < arrayOfInt.length)
    {
      arrayOfInt[i] = ((UserInfo)localList.get(i)).id;
      i += 1;
    }
    return getAccounts(arrayOfInt);
  }
  
  /* Error */
  public void getAuthToken(IAccountManagerResponse paramIAccountManagerResponse, final Account paramAccount, final String paramString, final boolean paramBoolean1, boolean paramBoolean2, final Bundle paramBundle)
  {
    // Byte code:
    //   0: aload 6
    //   2: iconst_1
    //   3: invokestatic 607	android/os/Bundle:setDefusable	(Landroid/os/Bundle;Z)Landroid/os/Bundle;
    //   6: pop
    //   7: ldc -29
    //   9: iconst_2
    //   10: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   13: ifeq +95 -> 108
    //   16: ldc -29
    //   18: new 613	java/lang/StringBuilder
    //   21: dup
    //   22: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   25: ldc_w 2367
    //   28: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: aload_2
    //   32: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   35: ldc_w 2090
    //   38: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: aload_1
    //   42: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   45: ldc_w 2092
    //   48: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload_3
    //   52: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   55: ldc_w 2369
    //   58: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: iload 4
    //   63: invokevirtual 1980	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   66: ldc_w 2098
    //   69: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: iload 5
    //   74: invokevirtual 1980	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   77: ldc_w 1293
    //   80: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   86: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   89: ldc_w 1295
    //   92: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   98: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   101: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   107: pop
    //   108: aload_1
    //   109: ifnonnull +14 -> 123
    //   112: new 2070	java/lang/IllegalArgumentException
    //   115: dup
    //   116: ldc_w 2100
    //   119: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   122: athrow
    //   123: aload_2
    //   124: ifnonnull +24 -> 148
    //   127: ldc -29
    //   129: ldc_w 2371
    //   132: invokestatic 2055	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   135: pop
    //   136: aload_1
    //   137: bipush 7
    //   139: ldc_w 2072
    //   142: invokeinterface 1876 3 0
    //   147: return
    //   148: aload_3
    //   149: ifnonnull +52 -> 201
    //   152: ldc -29
    //   154: ldc_w 2373
    //   157: invokestatic 2055	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   160: pop
    //   161: aload_1
    //   162: bipush 7
    //   164: ldc_w 2375
    //   167: invokeinterface 1876 3 0
    //   172: return
    //   173: astore_1
    //   174: ldc -29
    //   176: new 613	java/lang/StringBuilder
    //   179: dup
    //   180: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   183: ldc_w 2191
    //   186: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   189: aload_1
    //   190: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   193: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   196: invokestatic 2055	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   199: pop
    //   200: return
    //   201: invokestatic 1280	android/os/UserHandle:getCallingUserId	()I
    //   204: istore 7
    //   206: invokestatic 970	android/os/Binder:clearCallingIdentity	()J
    //   209: lstore 11
    //   211: aload_0
    //   212: iload 7
    //   214: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   217: astore 13
    //   219: aload_0
    //   220: getfield 277	com/android/server/accounts/AccountManagerService:mAuthenticatorCache	Lcom/android/server/accounts/IAccountAuthenticatorCache;
    //   223: aload_2
    //   224: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   227: invokestatic 2379	android/accounts/AuthenticatorDescription:newKey	(Ljava/lang/String;)Landroid/accounts/AuthenticatorDescription;
    //   230: aload 13
    //   232: invokestatic 611	com/android/server/accounts/AccountManagerService$UserAccounts:-get8	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;)I
    //   235: invokeinterface 2383 3 0
    //   240: astore 14
    //   242: lload 11
    //   244: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   247: aload 14
    //   249: ifnull +220 -> 469
    //   252: aload 14
    //   254: getfield 1345	android/content/pm/RegisteredServicesCache$ServiceInfo:type	Ljava/lang/Object;
    //   257: checkcast 1347	android/accounts/AuthenticatorDescription
    //   260: getfield 2387	android/accounts/AuthenticatorDescription:customTokens	Z
    //   263: istore 9
    //   265: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   268: istore 8
    //   270: iload 9
    //   272: ifne +203 -> 475
    //   275: aload_0
    //   276: aload_2
    //   277: aload_3
    //   278: iload 8
    //   280: iload 7
    //   282: invokespecial 1481	com/android/server/accounts/AccountManagerService:permissionIsGranted	(Landroid/accounts/Account;Ljava/lang/String;II)Z
    //   285: istore 10
    //   287: aload 6
    //   289: ldc_w 2389
    //   292: invokevirtual 724	android/os/Bundle:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   295: astore 14
    //   297: invokestatic 970	android/os/Binder:clearCallingIdentity	()J
    //   300: lstore 11
    //   302: aload_0
    //   303: getfield 281	com/android/server/accounts/AccountManagerService:mPackageManager	Landroid/content/pm/PackageManager;
    //   306: iload 8
    //   308: invokevirtual 918	android/content/pm/PackageManager:getPackagesForUid	(I)[Ljava/lang/String;
    //   311: invokestatic 1323	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   314: astore 15
    //   316: lload 11
    //   318: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   321: aload 14
    //   323: ifnull +166 -> 489
    //   326: aload 15
    //   328: aload 14
    //   330: invokeinterface 1312 2 0
    //   335: ifeq +154 -> 489
    //   338: aload 6
    //   340: ldc_w 2111
    //   343: iload 8
    //   345: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   348: aload 6
    //   350: ldc_w 2117
    //   353: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   356: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   359: iload 4
    //   361: ifeq +12 -> 373
    //   364: aload 6
    //   366: ldc_w 2391
    //   369: iconst_1
    //   370: invokevirtual 2189	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
    //   373: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   376: lstore 11
    //   378: aload_0
    //   379: aload 14
    //   381: invokespecial 2393	com/android/server/accounts/AccountManagerService:calculatePackageSignatureDigest	(Ljava/lang/String;)[B
    //   384: astore 15
    //   386: iload 9
    //   388: ifne +132 -> 520
    //   391: iload 10
    //   393: ifeq +127 -> 520
    //   396: aload_0
    //   397: aload 13
    //   399: aload_2
    //   400: aload_3
    //   401: invokevirtual 2397	com/android/server/accounts/AccountManagerService:readAuthTokenInternal	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;Ljava/lang/String;)Ljava/lang/String;
    //   404: astore 16
    //   406: aload 16
    //   408: ifnull +112 -> 520
    //   411: new 603	android/os/Bundle
    //   414: dup
    //   415: invokespecial 2109	android/os/Bundle:<init>	()V
    //   418: astore_3
    //   419: aload_3
    //   420: ldc -128
    //   422: aload 16
    //   424: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   427: aload_3
    //   428: ldc_w 2402
    //   431: aload_2
    //   432: getfield 596	android/accounts/Account:name	Ljava/lang/String;
    //   435: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   438: aload_3
    //   439: ldc_w 2302
    //   442: aload_2
    //   443: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   446: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   449: aload_0
    //   450: aload_1
    //   451: aload_3
    //   452: invokespecial 2331	com/android/server/accounts/AccountManagerService:onResult	(Landroid/accounts/IAccountManagerResponse;Landroid/os/Bundle;)V
    //   455: lload 11
    //   457: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   460: return
    //   461: astore_1
    //   462: lload 11
    //   464: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   467: aload_1
    //   468: athrow
    //   469: iconst_0
    //   470: istore 9
    //   472: goto -207 -> 265
    //   475: iconst_1
    //   476: istore 10
    //   478: goto -191 -> 287
    //   481: astore_1
    //   482: lload 11
    //   484: invokestatic 984	android/os/Binder:restoreCallingIdentity	(J)V
    //   487: aload_1
    //   488: athrow
    //   489: new 938	java/lang/SecurityException
    //   492: dup
    //   493: ldc_w 2404
    //   496: iconst_2
    //   497: anewarray 954	java/lang/Object
    //   500: dup
    //   501: iconst_0
    //   502: iload 8
    //   504: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   507: aastore
    //   508: dup
    //   509: iconst_1
    //   510: aload 14
    //   512: aastore
    //   513: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   516: invokespecial 941	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   519: athrow
    //   520: iload 9
    //   522: ifeq +90 -> 612
    //   525: aload_0
    //   526: aload 13
    //   528: aload_2
    //   529: aload_3
    //   530: aload 14
    //   532: aload 15
    //   534: invokevirtual 2408	com/android/server/accounts/AccountManagerService:readCachedTokenInternal	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;Ljava/lang/String;Ljava/lang/String;[B)Ljava/lang/String;
    //   537: astore 16
    //   539: aload 16
    //   541: ifnull +71 -> 612
    //   544: ldc -29
    //   546: iconst_2
    //   547: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   550: ifeq +12 -> 562
    //   553: ldc -29
    //   555: ldc_w 2410
    //   558: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   561: pop
    //   562: new 603	android/os/Bundle
    //   565: dup
    //   566: invokespecial 2109	android/os/Bundle:<init>	()V
    //   569: astore_3
    //   570: aload_3
    //   571: ldc -128
    //   573: aload 16
    //   575: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   578: aload_3
    //   579: ldc_w 2402
    //   582: aload_2
    //   583: getfield 596	android/accounts/Account:name	Ljava/lang/String;
    //   586: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   589: aload_3
    //   590: ldc_w 2302
    //   593: aload_2
    //   594: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   597: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   600: aload_0
    //   601: aload_1
    //   602: aload_3
    //   603: invokespecial 2331	com/android/server/accounts/AccountManagerService:onResult	(Landroid/accounts/IAccountManagerResponse;Landroid/os/Bundle;)V
    //   606: lload 11
    //   608: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   611: return
    //   612: new 55	com/android/server/accounts/AccountManagerService$8
    //   615: dup
    //   616: aload_0
    //   617: aload_0
    //   618: aload 13
    //   620: aload_1
    //   621: aload_2
    //   622: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   625: iload 5
    //   627: iconst_0
    //   628: aload_2
    //   629: getfield 596	android/accounts/Account:name	Ljava/lang/String;
    //   632: iconst_0
    //   633: aload 6
    //   635: aload_2
    //   636: aload_3
    //   637: iload 4
    //   639: iload 10
    //   641: iload 8
    //   643: iload 9
    //   645: aload 14
    //   647: aload 15
    //   649: aload 13
    //   651: invokespecial 2413	com/android/server/accounts/AccountManagerService$8:<init>	(Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/IAccountManagerResponse;Ljava/lang/String;ZZLjava/lang/String;ZLandroid/os/Bundle;Landroid/accounts/Account;Ljava/lang/String;ZZIZLjava/lang/String;[BLcom/android/server/accounts/AccountManagerService$UserAccounts;)V
    //   654: invokevirtual 2414	com/android/server/accounts/AccountManagerService$8:bind	()V
    //   657: lload 11
    //   659: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   662: return
    //   663: astore_1
    //   664: lload 11
    //   666: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   669: aload_1
    //   670: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	671	0	this	AccountManagerService
    //   0	671	1	paramIAccountManagerResponse	IAccountManagerResponse
    //   0	671	2	paramAccount	Account
    //   0	671	3	paramString	String
    //   0	671	4	paramBoolean1	boolean
    //   0	671	5	paramBoolean2	boolean
    //   0	671	6	paramBundle	Bundle
    //   204	77	7	i	int
    //   268	374	8	j	int
    //   263	381	9	bool1	boolean
    //   285	355	10	bool2	boolean
    //   209	456	11	l	long
    //   217	433	13	localUserAccounts	UserAccounts
    //   240	406	14	localObject1	Object
    //   314	334	15	localObject2	Object
    //   404	170	16	str	String
    // Exception table:
    //   from	to	target	type
    //   127	147	173	android/os/RemoteException
    //   152	172	173	android/os/RemoteException
    //   211	242	461	finally
    //   302	316	481	finally
    //   378	386	663	finally
    //   396	406	663	finally
    //   411	455	663	finally
    //   525	539	663	finally
    //   544	562	663	finally
    //   562	606	663	finally
    //   612	657	663	finally
  }
  
  public void getAuthTokenLabel(IAccountManagerResponse paramIAccountManagerResponse, final String paramString1, final String paramString2)
    throws RemoteException
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    int i = getCallingUid();
    clearCallingIdentity();
    if (UserHandle.getAppId(i) != 1000) {
      throw new SecurityException("can only call from system");
    }
    i = UserHandle.getUserId(i);
    long l = clearCallingIdentity();
    try
    {
      new Session(this, getUserAccounts(i), paramIAccountManagerResponse, paramString1, false, false, null, false)
      {
        public void onResult(Bundle paramAnonymousBundle)
        {
          Bundle.setDefusable(paramAnonymousBundle, true);
          if (paramAnonymousBundle != null)
          {
            paramAnonymousBundle = paramAnonymousBundle.getString("authTokenLabelKey");
            Bundle localBundle = new Bundle();
            localBundle.putString("authTokenLabelKey", paramAnonymousBundle);
            super.onResult(localBundle);
            return;
          }
          super.onResult(paramAnonymousBundle);
        }
        
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.getAuthTokenLabel(this, paramString2);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          return super.toDebugString(paramAnonymousLong) + ", getAuthTokenLabel" + ", " + paramString1 + ", authTokenType " + paramString2;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public AuthenticatorDescription[] getAuthenticatorTypes(int paramInt)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "getAuthenticatorTypes: for user id " + paramInt + " caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (isCrossUser(i, paramInt)) {
      throw new SecurityException(String.format("User %s tying to get authenticator types for %s", new Object[] { Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(paramInt) }));
    }
    long l = clearCallingIdentity();
    try
    {
      AuthenticatorDescription[] arrayOfAuthenticatorDescription = getAuthenticatorTypesInternal(paramInt);
      return arrayOfAuthenticatorDescription;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  String getCeDatabaseName(int paramInt)
  {
    return new File(Environment.getDataSystemCeDirectory(paramInt), "accounts_ce.db").getPath();
  }
  
  String getDeDatabaseName(int paramInt)
  {
    return new File(Environment.getDataSystemDeDirectory(paramInt), "accounts_de.db").getPath();
  }
  
  public String getPassword(Account paramAccount)
  {
    SeempLog.record(14);
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "getPassword: " + paramAccount + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot get secrets for accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    long l = clearCallingIdentity();
    try
    {
      paramAccount = readPasswordInternal(getUserAccounts(j), paramAccount);
      return paramAccount;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  String getPreNDatabaseName(int paramInt)
  {
    File localFile2 = Environment.getDataSystemDirectory();
    File localFile1 = new File(Environment.getUserSystemDirectory(paramInt), "accounts.db");
    if (paramInt == 0)
    {
      localFile2 = new File(localFile2, "accounts.db");
      if ((localFile2.exists()) && (!localFile1.exists())) {
        break label52;
      }
    }
    label52:
    do
    {
      return localFile1.getPath();
      File localFile3 = Environment.getUserSystemDirectory(paramInt);
      if ((!localFile3.exists()) && (!localFile3.mkdirs())) {
        throw new IllegalStateException("User dir cannot be created: " + localFile3);
      }
    } while (localFile2.renameTo(localFile1));
    throw new IllegalStateException("User dir cannot be migrated: " + localFile1);
  }
  
  public String getPreviousName(Account paramAccount)
  {
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "getPreviousName: " + paramAccount + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      paramAccount = readPreviousNameInternal(getUserAccounts(i), paramAccount);
      return paramAccount;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public AccountAndUser[] getRunningAccounts()
  {
    try
    {
      int[] arrayOfInt = ActivityManagerNative.getDefault().getRunningUserIds();
      return getAccounts(arrayOfInt);
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException(localRemoteException);
    }
  }
  
  public Account[] getSharedAccountsAsUser(int paramInt)
  {
    Object localObject2 = getUserAccounts(handleIncomingUser(paramInt));
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = null;
    try
    {
      localObject2 = ((UserAccounts)localObject2).openHelper.getReadableDatabase().query("shared_accounts", new String[] { "name", "type" }, null, null, null, null, null);
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (((Cursor)localObject2).moveToFirst())
        {
          localObject1 = localObject2;
          paramInt = ((Cursor)localObject2).getColumnIndex("name");
          localObject1 = localObject2;
          int i = ((Cursor)localObject2).getColumnIndex("type");
          boolean bool;
          do
          {
            localObject1 = localObject2;
            localArrayList.add(new Account(((Cursor)localObject2).getString(paramInt), ((Cursor)localObject2).getString(i)));
            localObject1 = localObject2;
            bool = ((Cursor)localObject2).moveToNext();
          } while (bool);
        }
      }
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
      localObject1 = new Account[localArrayList.size()];
      localArrayList.toArray((Object[])localObject1);
      return (Account[])localObject1;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  protected UserAccounts getUserAccounts(int paramInt)
  {
    synchronized (this.mUsers)
    {
      ??? = (UserAccounts)this.mUsers.get(paramInt);
      int i = 0;
      Object localObject1 = ???;
      if (??? == null)
      {
        localObject1 = new File(getPreNDatabaseName(paramInt));
        ??? = new File(getDeDatabaseName(paramInt));
        localObject1 = new UserAccounts(this.mContext, paramInt, (File)localObject1, (File)???);
        initializeDebugDbSizeAndCompileSqlStatementForLogging(((UserAccounts)localObject1).openHelper.getWritableDatabase(), (UserAccounts)localObject1);
        this.mUsers.append(paramInt, localObject1);
        purgeOldGrants((UserAccounts)localObject1);
        i = 1;
      }
      if ((!((UserAccounts)localObject1).openHelper.isCeDatabaseAttached()) && (this.mLocalUnlockedUsers.get(paramInt))) {
        Log.i("AccountManagerService", "User " + paramInt + " is unlocked - opening CE database");
      }
      synchronized (((UserAccounts)localObject1).cacheLock)
      {
        File localFile1 = new File(getPreNDatabaseName(paramInt));
        File localFile2 = new File(getCeDatabaseName(paramInt));
        CeDatabaseHelper.create(this.mContext, paramInt, localFile1, localFile2);
        ((UserAccounts)localObject1).openHelper.attachCeDatabase(localFile2);
        syncDeCeAccountsLocked((UserAccounts)localObject1);
        if (i != 0) {
          validateAccountsInternal((UserAccounts)localObject1, true);
        }
        return (UserAccounts)localObject1;
      }
    }
  }
  
  /* Error */
  public String getUserData(Account paramAccount, String paramString)
  {
    // Byte code:
    //   0: bipush 15
    //   2: invokestatic 2086	android/util/SeempLog:record	(I)I
    //   5: pop
    //   6: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   9: istore_3
    //   10: ldc -29
    //   12: iconst_2
    //   13: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   16: ifeq +43 -> 59
    //   19: ldc -29
    //   21: ldc_w 2524
    //   24: iconst_4
    //   25: anewarray 954	java/lang/Object
    //   28: dup
    //   29: iconst_0
    //   30: aload_1
    //   31: aastore
    //   32: dup
    //   33: iconst_1
    //   34: aload_2
    //   35: aastore
    //   36: dup
    //   37: iconst_2
    //   38: iload_3
    //   39: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   48: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   51: aastore
    //   52: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   55: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   58: pop
    //   59: aload_1
    //   60: ifnonnull +14 -> 74
    //   63: new 2070	java/lang/IllegalArgumentException
    //   66: dup
    //   67: ldc_w 2072
    //   70: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   73: athrow
    //   74: aload_2
    //   75: ifnonnull +14 -> 89
    //   78: new 2070	java/lang/IllegalArgumentException
    //   81: dup
    //   82: ldc_w 2526
    //   85: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   88: athrow
    //   89: invokestatic 1280	android/os/UserHandle:getCallingUserId	()I
    //   92: istore 4
    //   94: aload_0
    //   95: aload_1
    //   96: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   99: iload_3
    //   100: iload 4
    //   102: invokespecial 1734	com/android/server/accounts/AccountManagerService:isAccountManagedByCaller	(Ljava/lang/String;II)Z
    //   105: ifne +35 -> 140
    //   108: new 938	java/lang/SecurityException
    //   111: dup
    //   112: ldc_w 2528
    //   115: iconst_2
    //   116: anewarray 954	java/lang/Object
    //   119: dup
    //   120: iconst_0
    //   121: iload_3
    //   122: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   125: aastore
    //   126: dup
    //   127: iconst_1
    //   128: aload_1
    //   129: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   132: aastore
    //   133: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   136: invokespecial 941	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   139: athrow
    //   140: aload_0
    //   141: iload 4
    //   143: invokespecial 382	com/android/server/accounts/AccountManagerService:isLocalUnlockedUser	(I)Z
    //   146: ifne +42 -> 188
    //   149: ldc -29
    //   151: new 613	java/lang/StringBuilder
    //   154: dup
    //   155: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   158: ldc_w 1287
    //   161: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: iload 4
    //   166: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   169: ldc_w 2530
    //   172: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: iload_3
    //   176: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   179: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: invokestatic 640	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   185: pop
    //   186: aconst_null
    //   187: areturn
    //   188: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   191: lstore 5
    //   193: aload_0
    //   194: iload 4
    //   196: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   199: astore 9
    //   201: aload 9
    //   203: getfield 644	com/android/server/accounts/AccountManagerService$UserAccounts:cacheLock	Ljava/lang/Object;
    //   206: astore 8
    //   208: aload 8
    //   210: monitorenter
    //   211: aload_0
    //   212: aload 9
    //   214: aload_1
    //   215: invokespecial 2532	com/android/server/accounts/AccountManagerService:accountExistsCacheLocked	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;)Z
    //   218: istore 7
    //   220: iload 7
    //   222: ifne +13 -> 235
    //   225: aload 8
    //   227: monitorexit
    //   228: lload 5
    //   230: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   233: aconst_null
    //   234: areturn
    //   235: aload_0
    //   236: aload 9
    //   238: aload_1
    //   239: aload_2
    //   240: invokevirtual 2535	com/android/server/accounts/AccountManagerService:readUserDataInternalLocked	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;Ljava/lang/String;)Ljava/lang/String;
    //   243: astore_1
    //   244: aload 8
    //   246: monitorexit
    //   247: lload 5
    //   249: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   252: aload_1
    //   253: areturn
    //   254: astore_1
    //   255: aload 8
    //   257: monitorexit
    //   258: aload_1
    //   259: athrow
    //   260: astore_1
    //   261: lload 5
    //   263: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   266: aload_1
    //   267: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	268	0	this	AccountManagerService
    //   0	268	1	paramAccount	Account
    //   0	268	2	paramString	String
    //   9	167	3	i	int
    //   92	103	4	j	int
    //   191	71	5	l	long
    //   218	3	7	bool	boolean
    //   199	38	9	localUserAccounts	UserAccounts
    // Exception table:
    //   from	to	target	type
    //   211	220	254	finally
    //   235	244	254	finally
    //   193	211	260	finally
    //   225	228	260	finally
    //   244	247	260	finally
    //   255	260	260	finally
  }
  
  void grantAppPermission(Account paramAccount, String paramString, int paramInt)
  {
    if ((paramAccount == null) || (paramString == null))
    {
      Log.e("AccountManagerService", "grantAppPermission: called with invalid arguments", new Exception());
      return;
    }
    UserAccounts localUserAccounts = getUserAccounts(UserHandle.getUserId(paramInt));
    synchronized (localUserAccounts.cacheLock)
    {
      SQLiteDatabase localSQLiteDatabase = localUserAccounts.openHelper.getWritableDatabase();
      localSQLiteDatabase.beginTransaction();
      try
      {
        long l = getAccountIdLocked(localSQLiteDatabase, paramAccount);
        if (l >= 0L)
        {
          ContentValues localContentValues = new ContentValues();
          localContentValues.put("accounts_id", Long.valueOf(l));
          localContentValues.put("auth_token_type", paramString);
          localContentValues.put("uid", Integer.valueOf(paramInt));
          localSQLiteDatabase.insert("grants", "accounts_id", localContentValues);
          localSQLiteDatabase.setTransactionSuccessful();
        }
        localSQLiteDatabase.endTransaction();
        cancelNotification(getCredentialPermissionNotificationId(paramAccount, paramString, paramInt).intValue(), UserHandle.of(UserAccounts.-get8(localUserAccounts)));
        cancelAccountAccessRequestNotificationIfNeeded(paramAccount, paramInt, true);
        paramString = this.mAppPermissionChangeListeners.iterator();
        while (paramString.hasNext())
        {
          ??? = (AccountManagerInternal.OnAppPermissionChangeListener)paramString.next();
          this.mMessageHandler.post(new -void_grantAppPermission_android_accounts_Account_account_java_lang_String_authTokenType_int_uid_LambdaImpl0((AccountManagerInternal.OnAppPermissionChangeListener)???, paramAccount, paramInt));
          continue;
          paramAccount = finally;
        }
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
      }
    }
  }
  
  public boolean hasAccountAccess(Account paramAccount, String paramString, UserHandle paramUserHandle)
  {
    if (UserHandle.getAppId(Binder.getCallingUid()) != 1000) {
      throw new SecurityException("Can be called only by system UID");
    }
    Preconditions.checkNotNull(paramAccount, "account cannot be null");
    Preconditions.checkNotNull(paramString, "packageName cannot be null");
    Preconditions.checkNotNull(paramUserHandle, "userHandle cannot be null");
    int i = paramUserHandle.getIdentifier();
    Preconditions.checkArgumentInRange(i, 0, Integer.MAX_VALUE, "user must be concrete");
    try
    {
      boolean bool = hasAccountAccess(paramAccount, paramString, this.mPackageManager.getPackageUidAsUser(paramString, i));
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramAccount) {}
    return false;
  }
  
  public void hasFeatures(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String[] paramArrayOfString, String paramString)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "hasFeatures: " + paramAccount + ", response " + paramIAccountManagerResponse + ", features " + stringArrayToString(paramArrayOfString) + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramArrayOfString == null) {
      throw new IllegalArgumentException("features is null");
    }
    int j = UserHandle.getCallingUserId();
    checkReadAccountsPermitted(i, paramAccount.type, j, paramString);
    long l = clearCallingIdentity();
    try
    {
      new TestFeaturesSession(getUserAccounts(j), paramIAccountManagerResponse, paramAccount, paramArrayOfString).bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  protected void installNotification(int paramInt, Notification paramNotification, UserHandle paramUserHandle)
  {
    installNotification(paramInt, paramNotification, "android", paramUserHandle.getIdentifier());
  }
  
  /* Error */
  public void invalidateAuthToken(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   3: istore_3
    //   4: ldc -29
    //   6: iconst_2
    //   7: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   10: ifeq +51 -> 61
    //   13: ldc -29
    //   15: new 613	java/lang/StringBuilder
    //   18: dup
    //   19: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   22: ldc_w 2555
    //   25: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: aload_1
    //   29: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   32: ldc_w 1293
    //   35: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: iload_3
    //   39: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   42: ldc_w 1295
    //   45: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   51: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   54: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   57: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   60: pop
    //   61: aload_1
    //   62: ifnonnull +14 -> 76
    //   65: new 2070	java/lang/IllegalArgumentException
    //   68: dup
    //   69: ldc_w 2102
    //   72: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   75: athrow
    //   76: aload_2
    //   77: ifnonnull +14 -> 91
    //   80: new 2070	java/lang/IllegalArgumentException
    //   83: dup
    //   84: ldc_w 2557
    //   87: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   90: athrow
    //   91: invokestatic 1280	android/os/UserHandle:getCallingUserId	()I
    //   94: istore_3
    //   95: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   98: lstore 4
    //   100: aload_0
    //   101: iload_3
    //   102: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   105: astore 8
    //   107: aload 8
    //   109: getfield 644	com/android/server/accounts/AccountManagerService$UserAccounts:cacheLock	Ljava/lang/Object;
    //   112: astore 6
    //   114: aload 6
    //   116: monitorenter
    //   117: aload 8
    //   119: getfield 648	com/android/server/accounts/AccountManagerService$UserAccounts:openHelper	Lcom/android/server/accounts/AccountManagerService$DeDatabaseHelper;
    //   122: invokevirtual 652	com/android/server/accounts/AccountManagerService$DeDatabaseHelper:getWritableDatabaseUserIsUnlocked	()Landroid/database/sqlite/SQLiteDatabase;
    //   125: astore 7
    //   127: aload 7
    //   129: invokevirtual 657	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   132: aload_0
    //   133: aload 8
    //   135: aload 7
    //   137: aload_1
    //   138: aload_2
    //   139: invokespecial 2559	com/android/server/accounts/AccountManagerService:invalidateAuthTokenLocked	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;)V
    //   142: aload_0
    //   143: aload 8
    //   145: aload_1
    //   146: aload_2
    //   147: invokespecial 2561	com/android/server/accounts/AccountManagerService:invalidateCustomTokenLocked	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Ljava/lang/String;Ljava/lang/String;)V
    //   150: aload 7
    //   152: invokevirtual 733	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   155: aload 7
    //   157: invokevirtual 672	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   160: aload 6
    //   162: monitorexit
    //   163: lload 4
    //   165: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   168: return
    //   169: astore_1
    //   170: aload 7
    //   172: invokevirtual 672	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   175: aload_1
    //   176: athrow
    //   177: astore_1
    //   178: aload 6
    //   180: monitorexit
    //   181: aload_1
    //   182: athrow
    //   183: astore_1
    //   184: lload 4
    //   186: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   189: aload_1
    //   190: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	191	0	this	AccountManagerService
    //   0	191	1	paramString1	String
    //   0	191	2	paramString2	String
    //   3	99	3	i	int
    //   98	87	4	l	long
    //   125	46	7	localSQLiteDatabase	SQLiteDatabase
    //   105	39	8	localUserAccounts	UserAccounts
    // Exception table:
    //   from	to	target	type
    //   132	155	169	finally
    //   117	132	177	finally
    //   155	160	177	finally
    //   170	177	177	finally
    //   100	117	183	finally
    //   160	163	183	finally
    //   178	183	183	finally
  }
  
  public void isCredentialsUpdateSuggested(IAccountManagerResponse paramIAccountManagerResponse, final Account paramAccount, final String paramString)
  {
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "isCredentialsUpdateSuggested: " + paramAccount + ", response " + paramIAccountManagerResponse + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("status token is empty");
    }
    int i = Binder.getCallingUid();
    if (!isSystemUid(i)) {
      throw new SecurityException(String.format("uid %s cannot stat add account session.", new Object[] { Integer.valueOf(i) }));
    }
    i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      new Session(this, getUserAccounts(i), paramIAccountManagerResponse, paramAccount.type, false, false, paramAccount.name, false)
      {
        public void onResult(Bundle paramAnonymousBundle)
        {
          Bundle.setDefusable(paramAnonymousBundle, true);
          IAccountManagerResponse localIAccountManagerResponse = getResponseAndClose();
          if (localIAccountManagerResponse == null) {
            return;
          }
          if (paramAnonymousBundle == null)
          {
            AccountManagerService.-wrap19(jdField_this, localIAccountManagerResponse, 5, "null bundle");
            return;
          }
          if (Log.isLoggable("AccountManagerService", 2)) {
            Log.v("AccountManagerService", getClass().getSimpleName() + " calling onResult() on response " + localIAccountManagerResponse);
          }
          if (paramAnonymousBundle.getInt("errorCode", -1) > 0)
          {
            AccountManagerService.-wrap19(jdField_this, localIAccountManagerResponse, paramAnonymousBundle.getInt("errorCode"), paramAnonymousBundle.getString("errorMessage"));
            return;
          }
          if (!paramAnonymousBundle.containsKey("booleanResult"))
          {
            AccountManagerService.-wrap19(jdField_this, localIAccountManagerResponse, 5, "no result in response");
            return;
          }
          Bundle localBundle = new Bundle();
          localBundle.putBoolean("booleanResult", paramAnonymousBundle.getBoolean("booleanResult", false));
          AccountManagerService.-wrap20(jdField_this, localIAccountManagerResponse, localBundle);
        }
        
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.isCredentialsUpdateSuggested(this, paramAccount, paramString);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          return super.toDebugString(paramAnonymousLong) + ", isCredentialsUpdateSuggested" + ", " + paramAccount;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void onAccountAccessed(String paramString)
    throws RemoteException
  {
    int j = Binder.getCallingUid();
    if (UserHandle.getAppId(j) == 1000) {
      return;
    }
    int i = UserHandle.getCallingUserId();
    long l = Binder.clearCallingIdentity();
    try
    {
      Account[] arrayOfAccount = getAccounts(i, this.mContext.getOpPackageName());
      i = 0;
      int k = arrayOfAccount.length;
      while (i < k)
      {
        Account localAccount = arrayOfAccount[i];
        if ((Objects.equals(localAccount.getAccessId(), paramString)) && (!hasAccountAccess(localAccount, null, j))) {
          updateAppPermission(localAccount, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", j, true);
        }
        i += 1;
      }
      return;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return asBinder();
  }
  
  public void onServiceChanged(AuthenticatorDescription paramAuthenticatorDescription, int paramInt, boolean paramBoolean)
  {
    validateAccountsInternal(getUserAccounts(paramInt), false);
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    try
    {
      boolean bool = super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      return bool;
    }
    catch (RuntimeException paramParcel1)
    {
      if (!(paramParcel1 instanceof SecurityException)) {
        Slog.wtf("AccountManagerService", "Account Manager Crash", paramParcel1);
      }
      throw paramParcel1;
    }
  }
  
  void onUnlockUser(int paramInt)
  {
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "onUserUnlocked " + paramInt);
    }
    synchronized (this.mUsers)
    {
      this.mLocalUnlockedUsers.put(paramInt, true);
      if (paramInt < 1) {
        return;
      }
    }
    syncSharedAccounts(paramInt);
  }
  
  void onUserUnlocked(Intent paramIntent)
  {
    onUnlockUser(paramIntent.getIntExtra("android.intent.extra.user_handle", -1));
  }
  
  public String peekAuthToken(Account paramAccount, String paramString)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "peekAuthToken: " + paramAccount + ", authTokenType " + paramString + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot peek the authtokens associated with accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    if (!isLocalUnlockedUser(j))
    {
      Log.w("AccountManagerService", "Authtoken not available - user " + j + " data is locked. callingUid " + i);
      return null;
    }
    long l = clearCallingIdentity();
    try
    {
      paramAccount = readAuthTokenInternal(getUserAccounts(j), paramAccount, paramString);
      return paramAccount;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  protected String readAuthTokenInternal(UserAccounts paramUserAccounts, Account paramAccount, String paramString)
  {
    synchronized (paramUserAccounts.cacheLock)
    {
      HashMap localHashMap2 = (HashMap)UserAccounts.-get1(paramUserAccounts).get(paramAccount);
      HashMap localHashMap1 = localHashMap2;
      if (localHashMap2 == null)
      {
        localHashMap1 = readAuthTokensForAccountFromDatabaseLocked(paramUserAccounts.openHelper.getReadableDatabaseUserIsUnlocked(), paramAccount);
        UserAccounts.-get1(paramUserAccounts).put(paramAccount, localHashMap1);
      }
      paramUserAccounts = (String)localHashMap1.get(paramString);
      return paramUserAccounts;
    }
  }
  
  protected HashMap<String, String> readAuthTokensForAccountFromDatabaseLocked(SQLiteDatabase paramSQLiteDatabase, Account paramAccount)
  {
    localHashMap = new HashMap();
    paramSQLiteDatabase = paramSQLiteDatabase.query("ceDb.authtokens", COLUMNS_AUTHTOKENS_TYPE_AND_AUTHTOKEN, "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)", new String[] { paramAccount.name, paramAccount.type }, null, null, null);
    try
    {
      if (paramSQLiteDatabase.moveToNext()) {
        localHashMap.put(paramSQLiteDatabase.getString(0), paramSQLiteDatabase.getString(1));
      }
      return localHashMap;
    }
    finally
    {
      paramSQLiteDatabase.close();
    }
  }
  
  protected String readCachedTokenInternal(UserAccounts paramUserAccounts, Account paramAccount, String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    synchronized (paramUserAccounts.cacheLock)
    {
      paramUserAccounts = UserAccounts.-get0(paramUserAccounts).get(paramAccount, paramString1, paramString2, paramArrayOfByte);
      return paramUserAccounts;
    }
  }
  
  protected HashMap<String, String> readUserDataForAccountFromDatabaseLocked(SQLiteDatabase paramSQLiteDatabase, Account paramAccount)
  {
    localHashMap = new HashMap();
    paramSQLiteDatabase = paramSQLiteDatabase.query("ceDb.extras", COLUMNS_EXTRAS_KEY_AND_VALUE, "accounts_id=(select _id FROM accounts WHERE name=? AND type=?)", new String[] { paramAccount.name, paramAccount.type }, null, null, null);
    try
    {
      if (paramSQLiteDatabase.moveToNext()) {
        localHashMap.put(paramSQLiteDatabase.getString(0), paramSQLiteDatabase.getString(1));
      }
      return localHashMap;
    }
    finally
    {
      paramSQLiteDatabase.close();
    }
  }
  
  protected String readUserDataInternalLocked(UserAccounts paramUserAccounts, Account paramAccount, String paramString)
  {
    HashMap localHashMap2 = (HashMap)UserAccounts.-get7(paramUserAccounts).get(paramAccount);
    HashMap localHashMap1 = localHashMap2;
    if (localHashMap2 == null)
    {
      localHashMap1 = readUserDataForAccountFromDatabaseLocked(paramUserAccounts.openHelper.getReadableDatabaseUserIsUnlocked(), paramAccount);
      UserAccounts.-get7(paramUserAccounts).put(paramAccount, localHashMap1);
    }
    return (String)localHashMap1.get(paramString);
  }
  
  public void removeAccount(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, boolean paramBoolean)
  {
    SeempLog.record(17);
    removeAccountAsUser(paramIAccountManagerResponse, paramAccount, paramBoolean, UserHandle.getCallingUserId());
  }
  
  public void removeAccountAsUser(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, boolean paramBoolean, int paramInt)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "removeAccount: " + paramAccount + ", response " + paramIAccountManagerResponse + ", caller's uid " + i + ", pid " + Binder.getCallingPid() + ", for user id " + paramInt);
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (isCrossUser(i, paramInt)) {
      throw new SecurityException(String.format("User %s tying remove account for %s", new Object[] { Integer.valueOf(UserHandle.getCallingUserId()), Integer.valueOf(paramInt) }));
    }
    UserHandle localUserHandle = UserHandle.of(paramInt);
    if (((!isAccountManagedByCaller(paramAccount.type, i, localUserHandle.getIdentifier())) && (!isSystemUid(i))) || (!canUserModifyAccounts(paramInt, i))) {}
    try
    {
      paramIAccountManagerResponse.onError(100, "User cannot modify accounts");
      return;
    }
    catch (RemoteException paramIAccountManagerResponse) {}
    throw new SecurityException(String.format("uid %s cannot remove accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    if (!canUserModifyAccountsForType(paramInt, paramAccount.type, i)) {}
    try
    {
      paramIAccountManagerResponse.onError(101, "User cannot modify accounts of this type (policy).");
      return;
    }
    catch (RemoteException paramIAccountManagerResponse)
    {
      long l1;
      UserAccounts localUserAccounts;
      long l2;
      return;
    }
    l1 = clearCallingIdentity();
    localUserAccounts = getUserAccounts(paramInt);
    cancelNotification(getSigninRequiredNotificationId(localUserAccounts, paramAccount).intValue(), localUserHandle);
    synchronized (UserAccounts.-get2(localUserAccounts))
    {
      Iterator localIterator = UserAccounts.-get2(localUserAccounts).keySet().iterator();
      while (localIterator.hasNext())
      {
        Pair localPair = (Pair)localIterator.next();
        if (paramAccount.equals(((Pair)localPair.first).first)) {
          cancelNotification(((Integer)UserAccounts.-get2(localUserAccounts).get(localPair)).intValue(), localUserHandle);
        }
      }
    }
    ??? = localUserAccounts.openHelper.getReadableDatabase();
    l2 = getAccountIdLocked((SQLiteDatabase)???, paramAccount);
    logRecord((SQLiteDatabase)???, DebugDbHelper.-get6(), "accounts", l2, localUserAccounts, i);
    try
    {
      new RemoveAccountSession(localUserAccounts, paramIAccountManagerResponse, paramAccount, paramBoolean).bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l1);
    }
  }
  
  public boolean removeAccountExplicitly(Account paramAccount)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "removeAccountExplicitly: " + paramAccount + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    int j = Binder.getCallingUserHandle().getIdentifier();
    if (paramAccount == null)
    {
      Log.e("AccountManagerService", "account is null");
      return false;
    }
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot explicitly add accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    UserAccounts localUserAccounts = getUserAccountsForCaller();
    SQLiteDatabase localSQLiteDatabase = localUserAccounts.openHelper.getReadableDatabase();
    long l = getAccountIdLocked(localSQLiteDatabase, paramAccount);
    logRecord(localSQLiteDatabase, DebugDbHelper.-get6(), "accounts", l, localUserAccounts, i);
    l = clearCallingIdentity();
    try
    {
      boolean bool = removeAccountInternal(localUserAccounts, paramAccount, i);
      return bool;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  protected void removeAccountInternal(Account paramAccount)
  {
    removeAccountInternal(getUserAccountsForCaller(), paramAccount, getCallingUid());
  }
  
  public boolean removeSharedAccountAsUser(Account paramAccount, int paramInt)
  {
    return removeSharedAccountAsUser(paramAccount, paramInt, getCallingUid());
  }
  
  /* Error */
  public void renameAccount(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString)
  {
    // Byte code:
    //   0: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   3: istore 4
    //   5: ldc -29
    //   7: iconst_2
    //   8: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   11: ifeq +62 -> 73
    //   14: ldc -29
    //   16: new 613	java/lang/StringBuilder
    //   19: dup
    //   20: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   23: ldc_w 2660
    //   26: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: aload_2
    //   30: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   33: ldc_w 2662
    //   36: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   39: aload_3
    //   40: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: ldc_w 1293
    //   46: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: iload 4
    //   51: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   54: ldc_w 1295
    //   57: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   63: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   66: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   72: pop
    //   73: aload_2
    //   74: ifnonnull +14 -> 88
    //   77: new 2070	java/lang/IllegalArgumentException
    //   80: dup
    //   81: ldc_w 2072
    //   84: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   87: athrow
    //   88: invokestatic 1280	android/os/UserHandle:getCallingUserId	()I
    //   91: istore 5
    //   93: aload_0
    //   94: aload_2
    //   95: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   98: iload 4
    //   100: iload 5
    //   102: invokespecial 1734	com/android/server/accounts/AccountManagerService:isAccountManagedByCaller	(Ljava/lang/String;II)Z
    //   105: ifne +36 -> 141
    //   108: new 938	java/lang/SecurityException
    //   111: dup
    //   112: ldc_w 2664
    //   115: iconst_2
    //   116: anewarray 954	java/lang/Object
    //   119: dup
    //   120: iconst_0
    //   121: iload 4
    //   123: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   126: aastore
    //   127: dup
    //   128: iconst_1
    //   129: aload_2
    //   130: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   133: aastore
    //   134: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   137: invokespecial 941	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   140: athrow
    //   141: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   144: lstore 6
    //   146: aload_0
    //   147: aload_0
    //   148: iload 5
    //   150: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   153: aload_2
    //   154: aload_3
    //   155: invokespecial 2666	com/android/server/accounts/AccountManagerService:renameAccountInternal	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;Ljava/lang/String;)Landroid/accounts/Account;
    //   158: astore_2
    //   159: new 603	android/os/Bundle
    //   162: dup
    //   163: invokespecial 2109	android/os/Bundle:<init>	()V
    //   166: astore_3
    //   167: aload_3
    //   168: ldc_w 2402
    //   171: aload_2
    //   172: getfield 596	android/accounts/Account:name	Ljava/lang/String;
    //   175: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   178: aload_3
    //   179: ldc_w 2302
    //   182: aload_2
    //   183: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   186: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   189: aload_3
    //   190: ldc_w 2668
    //   193: aload_2
    //   194: invokevirtual 1521	android/accounts/Account:getAccessId	()Ljava/lang/String;
    //   197: invokevirtual 2400	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   200: aload_1
    //   201: aload_3
    //   202: invokeinterface 1688 2 0
    //   207: lload 6
    //   209: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   212: return
    //   213: astore_1
    //   214: ldc -29
    //   216: aload_1
    //   217: invokevirtual 2671	android/os/RemoteException:getMessage	()Ljava/lang/String;
    //   220: invokestatic 640	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   223: pop
    //   224: goto -17 -> 207
    //   227: astore_1
    //   228: lload 6
    //   230: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   233: aload_1
    //   234: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	235	0	this	AccountManagerService
    //   0	235	1	paramIAccountManagerResponse	IAccountManagerResponse
    //   0	235	2	paramAccount	Account
    //   0	235	3	paramString	String
    //   3	119	4	i	int
    //   91	58	5	j	int
    //   144	85	6	l	long
    // Exception table:
    //   from	to	target	type
    //   200	207	213	android/os/RemoteException
    //   146	200	227	finally
    //   200	207	227	finally
    //   214	224	227	finally
  }
  
  public boolean renameSharedAccountAsUser(Account paramAccount, String paramString, int paramInt)
  {
    UserAccounts localUserAccounts = getUserAccounts(handleIncomingUser(paramInt));
    SQLiteDatabase localSQLiteDatabase = localUserAccounts.openHelper.getWritableDatabase();
    long l = getAccountIdFromSharedTable(localSQLiteDatabase, paramAccount);
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("name", paramString);
    paramInt = localSQLiteDatabase.update("shared_accounts", localContentValues, "name=? AND type=?", new String[] { paramAccount.name, paramAccount.type });
    if (paramInt > 0)
    {
      int i = getCallingUid();
      logRecord(localSQLiteDatabase, DebugDbHelper.-get3(), "shared_accounts", l, localUserAccounts, i);
      renameAccountInternal(localUserAccounts, paramAccount, paramString);
    }
    return paramInt > 0;
  }
  
  public void setAuthToken(Account paramAccount, String paramString1, String paramString2)
  {
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "setAuthToken: " + paramAccount + ", authTokenType " + paramString1 + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString1 == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot set auth tokens associated with accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    long l = clearCallingIdentity();
    try
    {
      saveAuthTokenToDatabase(getUserAccounts(j), paramAccount, paramString1, paramString2);
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void setPassword(Account paramAccount, String paramString)
  {
    SeempLog.record(18);
    int i = Binder.getCallingUid();
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "setAuthToken: " + paramAccount + ", caller's uid " + i + ", pid " + Binder.getCallingPid());
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int j = UserHandle.getCallingUserId();
    if (!isAccountManagedByCaller(paramAccount.type, i, j)) {
      throw new SecurityException(String.format("uid %s cannot set secrets for accounts of type: %s", new Object[] { Integer.valueOf(i), paramAccount.type }));
    }
    long l = clearCallingIdentity();
    try
    {
      setPasswordInternal(getUserAccounts(j), paramAccount, paramString, i);
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  public void setUserData(Account paramAccount, String paramString1, String paramString2)
  {
    // Byte code:
    //   0: bipush 20
    //   2: invokestatic 2086	android/util/SeempLog:record	(I)I
    //   5: pop
    //   6: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   9: istore 4
    //   11: ldc -29
    //   13: iconst_2
    //   14: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   17: ifeq +62 -> 79
    //   20: ldc -29
    //   22: new 613	java/lang/StringBuilder
    //   25: dup
    //   26: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   29: ldc_w 2683
    //   32: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: aload_1
    //   36: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   39: ldc_w 2685
    //   42: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: aload_2
    //   46: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: ldc_w 1293
    //   52: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   55: iload 4
    //   57: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   60: ldc_w 1295
    //   63: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   69: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   72: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   75: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   78: pop
    //   79: aload_2
    //   80: ifnonnull +14 -> 94
    //   83: new 2070	java/lang/IllegalArgumentException
    //   86: dup
    //   87: ldc_w 2526
    //   90: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   93: athrow
    //   94: aload_1
    //   95: ifnonnull +14 -> 109
    //   98: new 2070	java/lang/IllegalArgumentException
    //   101: dup
    //   102: ldc_w 2072
    //   105: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   108: athrow
    //   109: invokestatic 1280	android/os/UserHandle:getCallingUserId	()I
    //   112: istore 5
    //   114: aload_0
    //   115: aload_1
    //   116: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   119: iload 4
    //   121: iload 5
    //   123: invokespecial 1734	com/android/server/accounts/AccountManagerService:isAccountManagedByCaller	(Ljava/lang/String;II)Z
    //   126: ifne +36 -> 162
    //   129: new 938	java/lang/SecurityException
    //   132: dup
    //   133: ldc_w 2687
    //   136: iconst_2
    //   137: anewarray 954	java/lang/Object
    //   140: dup
    //   141: iconst_0
    //   142: iload 4
    //   144: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   147: aastore
    //   148: dup
    //   149: iconst_1
    //   150: aload_1
    //   151: getfield 584	android/accounts/Account:type	Ljava/lang/String;
    //   154: aastore
    //   155: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   158: invokespecial 941	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   161: athrow
    //   162: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   165: lstore 6
    //   167: aload_0
    //   168: iload 5
    //   170: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   173: astore 10
    //   175: aload 10
    //   177: getfield 644	com/android/server/accounts/AccountManagerService$UserAccounts:cacheLock	Ljava/lang/Object;
    //   180: astore 9
    //   182: aload 9
    //   184: monitorenter
    //   185: aload_0
    //   186: aload 10
    //   188: aload_1
    //   189: invokespecial 2532	com/android/server/accounts/AccountManagerService:accountExistsCacheLocked	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;)Z
    //   192: istore 8
    //   194: iload 8
    //   196: ifne +12 -> 208
    //   199: aload 9
    //   201: monitorexit
    //   202: lload 6
    //   204: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   207: return
    //   208: aload_0
    //   209: aload 10
    //   211: aload_1
    //   212: aload_2
    //   213: aload_3
    //   214: invokespecial 2689	com/android/server/accounts/AccountManagerService:setUserdataInternalLocked	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;Ljava/lang/String;Ljava/lang/String;)V
    //   217: aload 9
    //   219: monitorexit
    //   220: lload 6
    //   222: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   225: return
    //   226: astore_1
    //   227: aload 9
    //   229: monitorexit
    //   230: aload_1
    //   231: athrow
    //   232: astore_1
    //   233: lload 6
    //   235: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   238: aload_1
    //   239: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	240	0	this	AccountManagerService
    //   0	240	1	paramAccount	Account
    //   0	240	2	paramString1	String
    //   0	240	3	paramString2	String
    //   9	134	4	i	int
    //   112	57	5	j	int
    //   165	69	6	l	long
    //   192	3	8	bool	boolean
    //   173	37	10	localUserAccounts	UserAccounts
    // Exception table:
    //   from	to	target	type
    //   185	194	226	finally
    //   208	217	226	finally
    //   167	185	232	finally
    //   199	202	232	finally
    //   217	220	232	finally
    //   227	232	232	finally
  }
  
  public boolean someUserHasAccount(Account paramAccount)
  {
    if (!UserHandle.isSameApp(1000, Binder.getCallingUid())) {
      throw new SecurityException("Only system can check for accounts across users");
    }
    long l = Binder.clearCallingIdentity();
    try
    {
      AccountAndUser[] arrayOfAccountAndUser = getAllAccounts();
      int i = arrayOfAccountAndUser.length - 1;
      while (i >= 0)
      {
        boolean bool = arrayOfAccountAndUser[i].account.equals(paramAccount);
        if (bool) {
          return true;
        }
        i -= 1;
      }
      return false;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  /* Error */
  public void startAddAccountSession(IAccountManagerResponse paramIAccountManagerResponse, final String paramString1, final String paramString2, final String[] paramArrayOfString, boolean paramBoolean, Bundle paramBundle)
  {
    // Byte code:
    //   0: aload 6
    //   2: iconst_1
    //   3: invokestatic 607	android/os/Bundle:setDefusable	(Landroid/os/Bundle;Z)Landroid/os/Bundle;
    //   6: pop
    //   7: ldc -29
    //   9: iconst_2
    //   10: invokestatic 1107	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   13: ifeq +98 -> 111
    //   16: ldc -29
    //   18: new 613	java/lang/StringBuilder
    //   21: dup
    //   22: invokespecial 614	java/lang/StringBuilder:<init>	()V
    //   25: ldc_w 2700
    //   28: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   31: aload_2
    //   32: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: ldc_w 2090
    //   38: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: aload_1
    //   42: invokevirtual 623	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   45: ldc_w 2092
    //   48: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload_3
    //   52: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   55: ldc_w 2094
    //   58: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: aload 4
    //   63: invokestatic 2096	com/android/server/accounts/AccountManagerService:stringArrayToString	([Ljava/lang/String;)Ljava/lang/String;
    //   66: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: ldc_w 2098
    //   72: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: iload 5
    //   77: invokevirtual 1980	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   80: ldc_w 1293
    //   83: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   89: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   92: ldc_w 1295
    //   95: invokevirtual 620	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   101: invokevirtual 628	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   104: invokevirtual 634	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   107: invokestatic 1117	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   110: pop
    //   111: aload_1
    //   112: ifnonnull +14 -> 126
    //   115: new 2070	java/lang/IllegalArgumentException
    //   118: dup
    //   119: ldc_w 2100
    //   122: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   125: athrow
    //   126: aload_2
    //   127: ifnonnull +14 -> 141
    //   130: new 2070	java/lang/IllegalArgumentException
    //   133: dup
    //   134: ldc_w 2102
    //   137: invokespecial 2073	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   140: athrow
    //   141: invokestatic 928	android/os/Binder:getCallingUid	()I
    //   144: istore 7
    //   146: aload_0
    //   147: iload 7
    //   149: invokespecial 2268	com/android/server/accounts/AccountManagerService:isSystemUid	(I)Z
    //   152: ifne +29 -> 181
    //   155: new 938	java/lang/SecurityException
    //   158: dup
    //   159: ldc_w 2569
    //   162: iconst_1
    //   163: anewarray 954	java/lang/Object
    //   166: dup
    //   167: iconst_0
    //   168: iload 7
    //   170: invokestatic 957	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   173: aastore
    //   174: invokestatic 961	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   177: invokespecial 941	java/lang/SecurityException:<init>	(Ljava/lang/String;)V
    //   180: athrow
    //   181: iload 7
    //   183: invokestatic 886	android/os/UserHandle:getUserId	(I)I
    //   186: istore 8
    //   188: aload_0
    //   189: iload 8
    //   191: iload 7
    //   193: invokespecial 2077	com/android/server/accounts/AccountManagerService:canUserModifyAccounts	(II)Z
    //   196: ifne +23 -> 219
    //   199: aload_1
    //   200: bipush 100
    //   202: ldc_w 2104
    //   205: invokeinterface 1876 3 0
    //   210: aload_0
    //   211: bipush 100
    //   213: iload 8
    //   215: invokespecial 2106	com/android/server/accounts/AccountManagerService:showCantAddAccount	(II)V
    //   218: return
    //   219: aload_0
    //   220: iload 8
    //   222: aload_2
    //   223: iload 7
    //   225: invokespecial 2079	com/android/server/accounts/AccountManagerService:canUserModifyAccountsForType	(ILjava/lang/String;I)Z
    //   228: ifne +23 -> 251
    //   231: aload_1
    //   232: bipush 101
    //   234: ldc_w 2108
    //   237: invokeinterface 1876 3 0
    //   242: aload_0
    //   243: bipush 101
    //   245: iload 8
    //   247: invokespecial 2106	com/android/server/accounts/AccountManagerService:showCantAddAccount	(II)V
    //   250: return
    //   251: invokestatic 1298	android/os/Binder:getCallingPid	()I
    //   254: istore 9
    //   256: aload 6
    //   258: ifnonnull +119 -> 377
    //   261: new 603	android/os/Bundle
    //   264: dup
    //   265: invokespecial 2109	android/os/Bundle:<init>	()V
    //   268: astore 13
    //   270: aload 13
    //   272: ldc_w 2111
    //   275: iload 7
    //   277: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   280: aload 13
    //   282: ldc_w 2117
    //   285: iload 9
    //   287: invokevirtual 2115	android/os/Bundle:putInt	(Ljava/lang/String;I)V
    //   290: aload_0
    //   291: aload 6
    //   293: ldc_w 2389
    //   296: invokevirtual 724	android/os/Bundle:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   299: iload 7
    //   301: iconst_1
    //   302: anewarray 415	java/lang/String
    //   305: dup
    //   306: iconst_0
    //   307: ldc_w 2702
    //   310: aastore
    //   311: invokespecial 1433	com/android/server/accounts/AccountManagerService:isPermitted	(Ljava/lang/String;I[Ljava/lang/String;)Z
    //   314: istore 10
    //   316: invokestatic 1010	com/android/server/accounts/AccountManagerService:clearCallingIdentity	()J
    //   319: lstore 11
    //   321: aload_0
    //   322: iload 8
    //   324: invokevirtual 797	com/android/server/accounts/AccountManagerService:getUserAccounts	(I)Lcom/android/server/accounts/AccountManagerService$UserAccounts;
    //   327: astore 6
    //   329: aload_0
    //   330: aload 6
    //   332: invokestatic 2704	com/android/server/accounts/AccountManagerService$DebugDbHelper:-get8	()Ljava/lang/String;
    //   335: ldc -47
    //   337: iload 7
    //   339: invokespecial 2121	com/android/server/accounts/AccountManagerService:logRecordWithUid	(Lcom/android/server/accounts/AccountManagerService$UserAccounts;Ljava/lang/String;Ljava/lang/String;I)V
    //   342: new 27	com/android/server/accounts/AccountManagerService$11
    //   345: dup
    //   346: aload_0
    //   347: aload_0
    //   348: aload 6
    //   350: aload_1
    //   351: aload_2
    //   352: iload 5
    //   354: aconst_null
    //   355: iconst_0
    //   356: iconst_1
    //   357: iload 10
    //   359: aload_3
    //   360: aload 4
    //   362: aload 13
    //   364: aload_2
    //   365: invokespecial 2707	com/android/server/accounts/AccountManagerService$11:<init>	(Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/IAccountManagerResponse;Ljava/lang/String;ZLjava/lang/String;ZZZLjava/lang/String;[Ljava/lang/String;Landroid/os/Bundle;Ljava/lang/String;)V
    //   368: invokevirtual 2708	com/android/server/accounts/AccountManagerService$11:bind	()V
    //   371: lload 11
    //   373: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   376: return
    //   377: aload 6
    //   379: astore 13
    //   381: goto -111 -> 270
    //   384: astore_1
    //   385: lload 11
    //   387: invokestatic 1017	com/android/server/accounts/AccountManagerService:restoreCallingIdentity	(J)V
    //   390: aload_1
    //   391: athrow
    //   392: astore_1
    //   393: goto -151 -> 242
    //   396: astore_1
    //   397: goto -187 -> 210
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	400	0	this	AccountManagerService
    //   0	400	1	paramIAccountManagerResponse	IAccountManagerResponse
    //   0	400	2	paramString1	String
    //   0	400	3	paramString2	String
    //   0	400	4	paramArrayOfString	String[]
    //   0	400	5	paramBoolean	boolean
    //   0	400	6	paramBundle	Bundle
    //   144	194	7	i	int
    //   186	137	8	j	int
    //   254	32	9	k	int
    //   314	44	10	bool	boolean
    //   319	67	11	l	long
    //   268	112	13	localBundle	Bundle
    // Exception table:
    //   from	to	target	type
    //   321	371	384	finally
    //   231	242	392	android/os/RemoteException
    //   199	210	396	android/os/RemoteException
  }
  
  public void startUpdateCredentialsSession(IAccountManagerResponse paramIAccountManagerResponse, final Account paramAccount, final String paramString, boolean paramBoolean, final Bundle paramBundle)
  {
    Bundle.setDefusable(paramBundle, true);
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "startUpdateCredentialsSession: " + paramAccount + ", response " + paramIAccountManagerResponse + ", authTokenType " + paramString + ", expectActivityLaunch " + paramBoolean + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int i = Binder.getCallingUid();
    if (!isSystemUid(i)) {
      throw new SecurityException(String.format("uid %s cannot start update credentials session.", new Object[] { Integer.valueOf(i) }));
    }
    int j = UserHandle.getCallingUserId();
    boolean bool = isPermitted(paramBundle.getString("androidPackageName"), i, new String[] { "android.permission.GET_PASSWORD" });
    long l = clearCallingIdentity();
    try
    {
      new StartAccountSession(this, getUserAccounts(j), paramIAccountManagerResponse, paramAccount.type, paramBoolean, paramAccount.name, false, true, bool)
      {
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.startUpdateCredentialsSession(this, paramAccount, paramString, paramBundle);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          if (paramBundle != null) {
            paramBundle.keySet();
          }
          return super.toDebugString(paramAnonymousLong) + ", startUpdateCredentialsSession" + ", " + paramAccount + ", authTokenType " + paramString + ", loginOptions " + paramBundle;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void updateAppPermission(Account paramAccount, String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException
  {
    if (UserHandle.getAppId(getCallingUid()) != 1000) {
      throw new SecurityException();
    }
    if (paramBoolean)
    {
      grantAppPermission(paramAccount, paramString, paramInt);
      return;
    }
    revokeAppPermission(paramAccount, paramString, paramInt);
  }
  
  public void updateCredentials(IAccountManagerResponse paramIAccountManagerResponse, final Account paramAccount, final String paramString, boolean paramBoolean, final Bundle paramBundle)
  {
    Bundle.setDefusable(paramBundle, true);
    if (Log.isLoggable("AccountManagerService", 2)) {
      Log.v("AccountManagerService", "updateCredentials: " + paramAccount + ", response " + paramIAccountManagerResponse + ", authTokenType " + paramString + ", expectActivityLaunch " + paramBoolean + ", caller's uid " + Binder.getCallingUid() + ", pid " + Binder.getCallingPid());
    }
    if (paramIAccountManagerResponse == null) {
      throw new IllegalArgumentException("response is null");
    }
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    int i = UserHandle.getCallingUserId();
    long l = clearCallingIdentity();
    try
    {
      new Session(this, getUserAccounts(i), paramIAccountManagerResponse, paramAccount.type, paramBoolean, true, paramAccount.name, false, true)
      {
        public void run()
          throws RemoteException
        {
          this.mAuthenticator.updateCredentials(this, paramAccount, paramString, paramBundle);
        }
        
        protected String toDebugString(long paramAnonymousLong)
        {
          if (paramBundle != null) {
            paramBundle.keySet();
          }
          return super.toDebugString(paramAnonymousLong) + ", updateCredentials" + ", " + paramAccount + ", authTokenType " + paramString + ", loginOptions " + paramBundle;
        }
      }.bind();
      return;
    }
    finally
    {
      restoreCallingIdentity(l);
    }
  }
  
  public void validateAccounts(int paramInt)
  {
    validateAccountsInternal(getUserAccounts(paramInt), true);
  }
  
  protected void writeAuthTokenIntoCacheLocked(UserAccounts paramUserAccounts, SQLiteDatabase paramSQLiteDatabase, Account paramAccount, String paramString1, String paramString2)
  {
    HashMap localHashMap2 = (HashMap)UserAccounts.-get1(paramUserAccounts).get(paramAccount);
    HashMap localHashMap1 = localHashMap2;
    if (localHashMap2 == null)
    {
      localHashMap1 = readAuthTokensForAccountFromDatabaseLocked(paramSQLiteDatabase, paramAccount);
      UserAccounts.-get1(paramUserAccounts).put(paramAccount, localHashMap1);
    }
    if (paramString2 == null)
    {
      localHashMap1.remove(paramString1);
      return;
    }
    localHashMap1.put(paramString1, paramString2);
  }
  
  protected void writeUserDataIntoCacheLocked(UserAccounts paramUserAccounts, SQLiteDatabase paramSQLiteDatabase, Account paramAccount, String paramString1, String paramString2)
  {
    HashMap localHashMap2 = (HashMap)UserAccounts.-get7(paramUserAccounts).get(paramAccount);
    HashMap localHashMap1 = localHashMap2;
    if (localHashMap2 == null)
    {
      localHashMap1 = readUserDataForAccountFromDatabaseLocked(paramSQLiteDatabase, paramAccount);
      UserAccounts.-get7(paramUserAccounts).put(paramAccount, localHashMap1);
    }
    if (paramString2 == null)
    {
      localHashMap1.remove(paramString1);
      return;
    }
    localHashMap1.put(paramString1, paramString2);
  }
  
  private final class AccountManagerInternalImpl
    extends AccountManagerInternal
  {
    @GuardedBy("mLock")
    private AccountManagerBackupHelper mBackupHelper;
    private final Object mLock = new Object();
    
    private AccountManagerInternalImpl() {}
    
    public void addOnAppPermissionChangeListener(AccountManagerInternal.OnAppPermissionChangeListener paramOnAppPermissionChangeListener)
    {
      AccountManagerService.-get1(AccountManagerService.this).add(paramOnAppPermissionChangeListener);
    }
    
    public byte[] backupAccountAccessPermissions(int paramInt)
    {
      synchronized (this.mLock)
      {
        if (this.mBackupHelper == null) {
          this.mBackupHelper = new AccountManagerBackupHelper(AccountManagerService.this, this);
        }
        byte[] arrayOfByte = this.mBackupHelper.backupAccountAccessPermissions(paramInt);
        return arrayOfByte;
      }
    }
    
    public boolean hasAccountAccess(Account paramAccount, int paramInt)
    {
      return AccountManagerService.-wrap2(AccountManagerService.this, paramAccount, null, paramInt);
    }
    
    /* Error */
    public void requestAccountAccess(Account paramAccount, String paramString, int paramInt, RemoteCallback arg4)
    {
      // Byte code:
      //   0: aload_1
      //   1: ifnonnull +12 -> 13
      //   4: ldc 66
      //   6: ldc 68
      //   8: invokestatic 74	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   11: pop
      //   12: return
      //   13: aload_2
      //   14: ifnonnull +12 -> 26
      //   17: ldc 66
      //   19: ldc 76
      //   21: invokestatic 74	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   24: pop
      //   25: return
      //   26: iload_3
      //   27: ifge +12 -> 39
      //   30: ldc 66
      //   32: ldc 78
      //   34: invokestatic 74	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   37: pop
      //   38: return
      //   39: aload 4
      //   41: ifnonnull +12 -> 53
      //   44: ldc 66
      //   46: ldc 80
      //   48: invokestatic 74	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   51: pop
      //   52: return
      //   53: aload_0
      //   54: getfield 19	com/android/server/accounts/AccountManagerService$AccountManagerInternalImpl:this$0	Lcom/android/server/accounts/AccountManagerService;
      //   57: aload_1
      //   58: aload_2
      //   59: new 82	android/os/UserHandle
      //   62: dup
      //   63: iload_3
      //   64: invokespecial 85	android/os/UserHandle:<init>	(I)V
      //   67: invokevirtual 88	com/android/server/accounts/AccountManagerService:hasAccountAccess	(Landroid/accounts/Account;Ljava/lang/String;Landroid/os/UserHandle;)Z
      //   70: ifeq +25 -> 95
      //   73: new 90	android/os/Bundle
      //   76: dup
      //   77: invokespecial 91	android/os/Bundle:<init>	()V
      //   80: astore_1
      //   81: aload_1
      //   82: ldc 93
      //   84: iconst_1
      //   85: invokevirtual 97	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
      //   88: aload 4
      //   90: aload_1
      //   91: invokevirtual 103	android/os/RemoteCallback:sendResult	(Landroid/os/Bundle;)V
      //   94: return
      //   95: aload_0
      //   96: getfield 19	com/android/server/accounts/AccountManagerService$AccountManagerInternalImpl:this$0	Lcom/android/server/accounts/AccountManagerService;
      //   99: invokestatic 107	com/android/server/accounts/AccountManagerService:-get3	(Lcom/android/server/accounts/AccountManagerService;)Landroid/content/pm/PackageManager;
      //   102: aload_2
      //   103: iload_3
      //   104: invokevirtual 113	android/content/pm/PackageManager:getPackageUidAsUser	(Ljava/lang/String;I)I
      //   107: istore 5
      //   109: aload_0
      //   110: getfield 19	com/android/server/accounts/AccountManagerService$AccountManagerInternalImpl:this$0	Lcom/android/server/accounts/AccountManagerService;
      //   113: aload_1
      //   114: aload_2
      //   115: iload 5
      //   117: aload 4
      //   119: invokestatic 117	com/android/server/accounts/AccountManagerService:-wrap1	(Lcom/android/server/accounts/AccountManagerService;Landroid/accounts/Account;Ljava/lang/String;ILandroid/os/RemoteCallback;)Landroid/content/Intent;
      //   122: astore 6
      //   124: aload_0
      //   125: getfield 19	com/android/server/accounts/AccountManagerService$AccountManagerInternalImpl:this$0	Lcom/android/server/accounts/AccountManagerService;
      //   128: invokestatic 121	com/android/server/accounts/AccountManagerService:-get5	(Lcom/android/server/accounts/AccountManagerService;)Landroid/util/SparseArray;
      //   131: astore 4
      //   133: aload 4
      //   135: monitorenter
      //   136: aload_0
      //   137: getfield 19	com/android/server/accounts/AccountManagerService$AccountManagerInternalImpl:this$0	Lcom/android/server/accounts/AccountManagerService;
      //   140: invokestatic 121	com/android/server/accounts/AccountManagerService:-get5	(Lcom/android/server/accounts/AccountManagerService;)Landroid/util/SparseArray;
      //   143: iload_3
      //   144: invokevirtual 127	android/util/SparseArray:get	(I)Ljava/lang/Object;
      //   147: checkcast 129	com/android/server/accounts/AccountManagerService$UserAccounts
      //   150: astore 7
      //   152: aload 4
      //   154: monitorexit
      //   155: aload_0
      //   156: getfield 19	com/android/server/accounts/AccountManagerService$AccountManagerInternalImpl:this$0	Lcom/android/server/accounts/AccountManagerService;
      //   159: aload 7
      //   161: aload_1
      //   162: aconst_null
      //   163: aload 6
      //   165: aload_2
      //   166: iload_3
      //   167: invokestatic 133	com/android/server/accounts/AccountManagerService:-wrap15	(Lcom/android/server/accounts/AccountManagerService;Lcom/android/server/accounts/AccountManagerService$UserAccounts;Landroid/accounts/Account;Ljava/lang/CharSequence;Landroid/content/Intent;Ljava/lang/String;I)V
      //   170: return
      //   171: astore_1
      //   172: ldc 66
      //   174: new 135	java/lang/StringBuilder
      //   177: dup
      //   178: invokespecial 136	java/lang/StringBuilder:<init>	()V
      //   181: ldc -118
      //   183: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   186: aload_2
      //   187: invokevirtual 142	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   190: invokevirtual 146	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   193: invokestatic 149	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   196: pop
      //   197: return
      //   198: astore_1
      //   199: aload 4
      //   201: monitorexit
      //   202: aload_1
      //   203: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	204	0	this	AccountManagerInternalImpl
      //   0	204	1	paramAccount	Account
      //   0	204	2	paramString	String
      //   0	204	3	paramInt	int
      //   107	9	5	i	int
      //   122	42	6	localIntent	Intent
      //   150	10	7	localUserAccounts	AccountManagerService.UserAccounts
      // Exception table:
      //   from	to	target	type
      //   95	109	171	android/content/pm/PackageManager$NameNotFoundException
      //   136	152	198	finally
    }
    
    public void restoreAccountAccessPermissions(byte[] paramArrayOfByte, int paramInt)
    {
      synchronized (this.mLock)
      {
        if (this.mBackupHelper == null) {
          this.mBackupHelper = new AccountManagerBackupHelper(AccountManagerService.this, this);
        }
        this.mBackupHelper.restoreAccountAccessPermissions(paramArrayOfByte, paramInt);
        return;
      }
    }
  }
  
  static class CeDatabaseHelper
    extends SQLiteOpenHelper
  {
    public CeDatabaseHelper(Context paramContext, String paramString)
    {
      super(paramString, null, 10);
    }
    
    static CeDatabaseHelper create(Context paramContext, int paramInt, File paramFile1, File paramFile2)
    {
      boolean bool3 = paramFile2.exists();
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "CeDatabaseHelper.create userId=" + paramInt + " oldDbExists=" + paramFile1.exists() + " newDbExists=" + bool3);
      }
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (!bool3)
      {
        bool1 = bool2;
        if (paramFile1.exists()) {
          bool1 = migratePreNDbToCe(paramFile1, paramFile2);
        }
      }
      paramContext = new CeDatabaseHelper(paramContext, paramFile2.getPath());
      paramContext.getWritableDatabase();
      paramContext.close();
      if (bool1)
      {
        Slog.i("AccountManagerService", "Migration complete - removing pre-N db " + paramFile1);
        if (!SQLiteDatabase.deleteDatabase(paramFile1)) {
          Slog.e("AccountManagerService", "Cannot remove pre-N db " + paramFile1);
        }
      }
      return paramContext;
    }
    
    private void createAccountsDeletionTrigger(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM authtokens     WHERE accounts_id=OLD._id ;   DELETE FROM extras     WHERE accounts_id=OLD._id ; END");
    }
    
    static String findAccountPasswordByNameAndType(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2)
    {
      paramSQLiteDatabase = paramSQLiteDatabase.query("ceDb.accounts", new String[] { "password" }, "name=? AND type=?", new String[] { paramString1, paramString2 }, null, null, null);
      try
      {
        if (paramSQLiteDatabase.moveToNext())
        {
          paramString1 = paramSQLiteDatabase.getString(0);
          return paramString1;
        }
        return null;
      }
      finally
      {
        paramSQLiteDatabase.close();
      }
    }
    
    static List<Account> findCeAccountsNotInDe(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase = paramSQLiteDatabase.rawQuery("SELECT name,type FROM ceDb.accounts WHERE NOT EXISTS  (SELECT _id FROM accounts WHERE _id=ceDb.accounts._id )", null);
      try
      {
        ArrayList localArrayList = new ArrayList(paramSQLiteDatabase.getCount());
        while (paramSQLiteDatabase.moveToNext()) {
          localArrayList.add(new Account(paramSQLiteDatabase.getString(0), paramSQLiteDatabase.getString(1)));
        }
      }
      finally
      {
        paramSQLiteDatabase.close();
      }
      return localList;
    }
    
    private static boolean migratePreNDbToCe(File paramFile1, File paramFile2)
    {
      Slog.i("AccountManagerService", "Moving pre-N DB " + paramFile1 + " to CE " + paramFile2);
      try
      {
        FileUtils.copyFileOrThrow(paramFile1, paramFile2);
        return true;
      }
      catch (IOException localIOException)
      {
        Slog.e("AccountManagerService", "Cannot copy file to " + paramFile2 + " from " + paramFile1, localIOException);
        AccountManagerService.-wrap14(paramFile2);
      }
      return false;
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      Log.i("AccountManagerService", "Creating CE database " + getDatabaseName());
      paramSQLiteDatabase.execSQL("CREATE TABLE accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, password TEXT, UNIQUE(name,type))");
      paramSQLiteDatabase.execSQL("CREATE TABLE authtokens (  _id INTEGER PRIMARY KEY AUTOINCREMENT,  accounts_id INTEGER NOT NULL, type TEXT NOT NULL,  authtoken TEXT,  UNIQUE (accounts_id,type))");
      paramSQLiteDatabase.execSQL("CREATE TABLE extras ( _id INTEGER PRIMARY KEY AUTOINCREMENT, accounts_id INTEGER, key TEXT NOT NULL, value TEXT, UNIQUE(accounts_id,key))");
      createAccountsDeletionTrigger(paramSQLiteDatabase);
    }
    
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "opened database accounts_ce.db");
      }
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      Log.i("AccountManagerService", "Upgrade CE from version " + paramInt1 + " to version " + paramInt2);
      int i = paramInt1;
      if (paramInt1 == 9)
      {
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", "onUpgrade upgrading to v10");
        }
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS meta");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS shared_accounts");
        paramSQLiteDatabase.execSQL("DROP TRIGGER IF EXISTS accountsDelete");
        createAccountsDeletionTrigger(paramSQLiteDatabase);
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS grants");
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountManagerService.DebugDbHelper.-get15());
        i = paramInt1 + 1;
      }
      if (i != paramInt2) {
        Log.e("AccountManagerService", "failed to upgrade version " + i + " to version " + paramInt2);
      }
    }
  }
  
  static class DeDatabaseHelper
    extends SQLiteOpenHelper
  {
    private volatile boolean mCeAttached;
    private final int mUserId;
    
    private DeDatabaseHelper(Context paramContext, int paramInt, String paramString)
    {
      super(paramString, null, 1);
      this.mUserId = paramInt;
    }
    
    static DeDatabaseHelper create(Context paramContext, int paramInt, File paramFile1, File paramFile2)
    {
      boolean bool = paramFile2.exists();
      paramFile2 = new DeDatabaseHelper(paramContext, paramInt, paramFile2.getPath());
      if ((!bool) && (paramFile1.exists()))
      {
        paramContext = new AccountManagerService.PreNDatabaseHelper(paramContext, paramInt, paramFile1.getPath());
        paramContext.getWritableDatabase();
        paramContext.close();
        paramFile2.migratePreNDbToDe(paramFile1);
      }
      return paramFile2;
    }
    
    private void createAccountsDeletionTrigger(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM grants     WHERE accounts_id=OLD._id ; END");
    }
    
    private void createGrantsTable(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("CREATE TABLE grants (  accounts_id INTEGER NOT NULL, auth_token_type STRING NOT NULL,  uid INTEGER NOT NULL,  UNIQUE (accounts_id,auth_token_type,uid))");
    }
    
    private void createSharedAccountsTable(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("CREATE TABLE shared_accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, UNIQUE(name,type))");
    }
    
    private void migratePreNDbToDe(File paramFile)
    {
      Log.i("AccountManagerService", "Migrate pre-N database to DE preNDbFile=" + paramFile);
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      localSQLiteDatabase.execSQL("ATTACH DATABASE '" + paramFile.getPath() + "' AS preNDb");
      localSQLiteDatabase.beginTransaction();
      localSQLiteDatabase.execSQL("INSERT INTO accounts(_id,name,type, previous_name, last_password_entry_time_millis_epoch) SELECT _id,name,type, previous_name, last_password_entry_time_millis_epoch FROM preNDb.accounts");
      localSQLiteDatabase.execSQL("INSERT INTO shared_accounts(_id,name,type) SELECT _id,name,type FROM preNDb.shared_accounts");
      localSQLiteDatabase.execSQL("INSERT INTO " + AccountManagerService.DebugDbHelper.-get15() + "(" + "_id" + "," + AccountManagerService.DebugDbHelper.-get12() + "," + AccountManagerService.DebugDbHelper.-get17() + "," + AccountManagerService.DebugDbHelper.-get13() + "," + AccountManagerService.DebugDbHelper.-get16() + "," + AccountManagerService.DebugDbHelper.-get14() + ") " + "SELECT " + "_id" + "," + AccountManagerService.DebugDbHelper.-get12() + "," + AccountManagerService.DebugDbHelper.-get17() + "," + AccountManagerService.DebugDbHelper.-get13() + "," + AccountManagerService.DebugDbHelper.-get16() + "," + AccountManagerService.DebugDbHelper.-get14() + " FROM preNDb." + AccountManagerService.DebugDbHelper.-get15());
      localSQLiteDatabase.execSQL("INSERT INTO grants(accounts_id,auth_token_type,uid) SELECT accounts_id,auth_token_type,uid FROM preNDb.grants");
      localSQLiteDatabase.execSQL("INSERT INTO meta(key,value) SELECT key,value FROM preNDb.meta");
      localSQLiteDatabase.setTransactionSuccessful();
      localSQLiteDatabase.endTransaction();
      localSQLiteDatabase.execSQL("DETACH DATABASE preNDb");
    }
    
    public void attachCeDatabase(File paramFile)
    {
      getWritableDatabase().execSQL("ATTACH DATABASE '" + paramFile.getPath() + "' AS ceDb");
      this.mCeAttached = true;
    }
    
    public SQLiteDatabase getReadableDatabaseUserIsUnlocked()
    {
      if (!this.mCeAttached) {
        Log.wtf("AccountManagerService", "getReadableDatabaseUserIsUnlocked called while user " + this.mUserId + " is still locked. CE database is not yet available.", new Throwable());
      }
      return super.getReadableDatabase();
    }
    
    public SQLiteDatabase getWritableDatabaseUserIsUnlocked()
    {
      if (!this.mCeAttached) {
        Log.wtf("AccountManagerService", "getWritableDatabaseUserIsUnlocked called while user " + this.mUserId + " is still locked. CE database is not yet available.", new Throwable());
      }
      return super.getWritableDatabase();
    }
    
    public boolean isCeDatabaseAttached()
    {
      return this.mCeAttached;
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      Log.i("AccountManagerService", "Creating DE database for user " + this.mUserId);
      paramSQLiteDatabase.execSQL("CREATE TABLE accounts ( _id INTEGER PRIMARY KEY, name TEXT NOT NULL, type TEXT NOT NULL, previous_name TEXT, last_password_entry_time_millis_epoch INTEGER DEFAULT 0, UNIQUE(name,type))");
      paramSQLiteDatabase.execSQL("CREATE TABLE meta ( key TEXT PRIMARY KEY NOT NULL, value TEXT)");
      createGrantsTable(paramSQLiteDatabase);
      createSharedAccountsTable(paramSQLiteDatabase);
      createAccountsDeletionTrigger(paramSQLiteDatabase);
      AccountManagerService.DebugDbHelper.-wrap0(paramSQLiteDatabase);
    }
    
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "opened database accounts_de.db");
      }
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      Log.i("AccountManagerService", "upgrade from version " + paramInt1 + " to version " + paramInt2);
      if (paramInt1 != paramInt2) {
        Log.e("AccountManagerService", "failed to upgrade version " + paramInt1 + " to version " + paramInt2);
      }
    }
  }
  
  private static class DebugDbHelper
  {
    private static String ACTION_ACCOUNT_ADD;
    private static String ACTION_ACCOUNT_REMOVE;
    private static String ACTION_ACCOUNT_REMOVE_DE;
    private static String ACTION_ACCOUNT_RENAME;
    private static String ACTION_AUTHENTICATOR_REMOVE;
    private static String ACTION_CALLED_ACCOUNT_ADD;
    private static String ACTION_CALLED_ACCOUNT_REMOVE;
    private static String ACTION_CALLED_ACCOUNT_SESSION_FINISH = "action_called_account_session_finish";
    private static String ACTION_CALLED_START_ACCOUNT_ADD;
    private static String ACTION_CLEAR_PASSWORD;
    private static String ACTION_SET_PASSWORD;
    private static String ACTION_SYNC_DE_CE_ACCOUNTS;
    private static String ACTION_TYPE;
    private static String CALLER_UID;
    private static String KEY;
    private static String TABLE_DEBUG = "debug_table";
    private static String TABLE_NAME;
    private static String TIMESTAMP;
    private static SimpleDateFormat dateFromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    static
    {
      ACTION_TYPE = "action_type";
      TIMESTAMP = "time";
      CALLER_UID = "caller_uid";
      TABLE_NAME = "table_name";
      KEY = "primary_key";
      ACTION_SET_PASSWORD = "action_set_password";
      ACTION_CLEAR_PASSWORD = "action_clear_password";
      ACTION_ACCOUNT_ADD = "action_account_add";
      ACTION_ACCOUNT_REMOVE = "action_account_remove";
      ACTION_ACCOUNT_REMOVE_DE = "action_account_remove_de";
      ACTION_AUTHENTICATOR_REMOVE = "action_authenticator_remove";
      ACTION_ACCOUNT_RENAME = "action_account_rename";
      ACTION_CALLED_ACCOUNT_ADD = "action_called_account_add";
      ACTION_CALLED_ACCOUNT_REMOVE = "action_called_account_remove";
      ACTION_SYNC_DE_CE_ACCOUNTS = "action_sync_de_ce_accounts";
      ACTION_CALLED_START_ACCOUNT_ADD = "action_called_start_account_add";
    }
    
    private static void createDebugTable(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("CREATE TABLE " + TABLE_DEBUG + " ( " + "_id" + " INTEGER," + ACTION_TYPE + " TEXT NOT NULL, " + TIMESTAMP + " DATETIME," + CALLER_UID + " INTEGER NOT NULL," + TABLE_NAME + " TEXT NOT NULL," + KEY + " INTEGER PRIMARY KEY)");
      paramSQLiteDatabase.execSQL("CREATE INDEX timestamp_index ON " + TABLE_DEBUG + " (" + TIMESTAMP + ")");
    }
  }
  
  private class GetAccountsByTypeAndFeatureSession
    extends AccountManagerService.Session
  {
    private volatile Account[] mAccountsOfType = null;
    private volatile ArrayList<Account> mAccountsWithFeatures = null;
    private final int mCallingUid;
    private volatile int mCurrentAccount = 0;
    private final String[] mFeatures;
    
    public GetAccountsByTypeAndFeatureSession(AccountManagerService.UserAccounts paramUserAccounts, IAccountManagerResponse paramIAccountManagerResponse, String paramString, String[] paramArrayOfString, int paramInt)
    {
      super(paramUserAccounts, paramIAccountManagerResponse, paramString, false, true, null, false);
      this.mCallingUid = paramInt;
      this.mFeatures = paramArrayOfString;
    }
    
    public void checkAccount()
    {
      if (this.mCurrentAccount >= this.mAccountsOfType.length)
      {
        sendResult();
        return;
      }
      IAccountAuthenticator localIAccountAuthenticator = this.mAuthenticator;
      if (localIAccountAuthenticator == null)
      {
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", "checkAccount: aborting session since we are no longer connected to the authenticator, " + toDebugString());
        }
        return;
      }
      try
      {
        localIAccountAuthenticator.hasFeatures(this, this.mAccountsOfType[this.mCurrentAccount], this.mFeatures);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        onError(1, "remote exception");
      }
    }
    
    public void onResult(Bundle paramBundle)
    {
      Bundle.setDefusable(paramBundle, true);
      this.mNumResults += 1;
      if (paramBundle == null)
      {
        onError(5, "null bundle");
        return;
      }
      if (paramBundle.getBoolean("booleanResult", false)) {
        this.mAccountsWithFeatures.add(this.mAccountsOfType[this.mCurrentAccount]);
      }
      this.mCurrentAccount += 1;
      checkAccount();
    }
    
    public void run()
      throws RemoteException
    {
      synchronized (this.mAccounts.cacheLock)
      {
        this.mAccountsOfType = AccountManagerService.this.getAccountsFromCacheLocked(this.mAccounts, this.mAccountType, this.mCallingUid, null);
        this.mAccountsWithFeatures = new ArrayList(this.mAccountsOfType.length);
        this.mCurrentAccount = 0;
        checkAccount();
        return;
      }
    }
    
    public void sendResult()
    {
      IAccountManagerResponse localIAccountManagerResponse = getResponseAndClose();
      if (localIAccountManagerResponse != null) {}
      try
      {
        Account[] arrayOfAccount = new Account[this.mAccountsWithFeatures.size()];
        int i = 0;
        while (i < arrayOfAccount.length)
        {
          arrayOfAccount[i] = ((Account)this.mAccountsWithFeatures.get(i));
          i += 1;
        }
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", getClass().getSimpleName() + " calling onResult() on response " + localIAccountManagerResponse);
        }
        Bundle localBundle = new Bundle();
        localBundle.putParcelableArray("accounts", arrayOfAccount);
        localIAccountManagerResponse.onResult(localBundle);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        while (!Log.isLoggable("AccountManagerService", 2)) {}
        Log.v("AccountManagerService", "failure while notifying response", localRemoteException);
      }
    }
    
    protected String toDebugString(long paramLong)
    {
      String str = null;
      StringBuilder localStringBuilder = new StringBuilder().append(super.toDebugString(paramLong)).append(", getAccountsByTypeAndFeatures").append(", ");
      if (this.mFeatures != null) {
        str = TextUtils.join(",", this.mFeatures);
      }
      return str;
    }
  }
  
  public static class Lifecycle
    extends SystemService
  {
    private AccountManagerService mService;
    
    public Lifecycle(Context paramContext)
    {
      super();
    }
    
    public void onStart()
    {
      this.mService = new AccountManagerService(getContext());
      publishBinderService("account", this.mService);
    }
    
    public void onUnlockUser(int paramInt)
    {
      this.mService.onUnlockUser(paramInt);
    }
  }
  
  class MessageHandler
    extends Handler
  {
    MessageHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        throw new IllegalStateException("unhandled message: " + paramMessage.what);
      case 3: 
        ((AccountManagerService.Session)paramMessage.obj).onTimedOut();
        return;
      }
      AccountManagerService.this.copyAccountToUser(null, (Account)paramMessage.obj, paramMessage.arg1, paramMessage.arg2);
    }
  }
  
  static class PreNDatabaseHelper
    extends SQLiteOpenHelper
  {
    private final Context mContext;
    private final int mUserId;
    
    public PreNDatabaseHelper(Context paramContext, int paramInt, String paramString)
    {
      super(paramString, null, 9);
      this.mContext = paramContext;
      this.mUserId = paramInt;
    }
    
    private void addDebugTable(SQLiteDatabase paramSQLiteDatabase)
    {
      AccountManagerService.DebugDbHelper.-wrap0(paramSQLiteDatabase);
    }
    
    private void addLastSuccessfullAuthenticatedTimeColumn(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("ALTER TABLE accounts ADD COLUMN last_password_entry_time_millis_epoch DEFAULT 0");
    }
    
    private void addOldAccountNameColumn(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("ALTER TABLE accounts ADD COLUMN previous_name");
    }
    
    private void createAccountsDeletionTrigger(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL(" CREATE TRIGGER accountsDelete DELETE ON accounts BEGIN   DELETE FROM authtokens     WHERE accounts_id=OLD._id ;   DELETE FROM extras     WHERE accounts_id=OLD._id ;   DELETE FROM grants     WHERE accounts_id=OLD._id ; END");
    }
    
    private void createGrantsTable(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("CREATE TABLE grants (  accounts_id INTEGER NOT NULL, auth_token_type STRING NOT NULL,  uid INTEGER NOT NULL,  UNIQUE (accounts_id,auth_token_type,uid))");
    }
    
    private void createSharedAccountsTable(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("CREATE TABLE shared_accounts ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL, UNIQUE(name,type))");
    }
    
    private void populateMetaTableWithAuthTypeAndUID(SQLiteDatabase paramSQLiteDatabase, Map<String, Integer> paramMap)
    {
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("key", "auth_uid_for_type:" + (String)localEntry.getKey());
        localContentValues.put("value", (Integer)localEntry.getValue());
        paramSQLiteDatabase.insert("meta", null, localContentValues);
      }
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      throw new IllegalStateException("Legacy database cannot be created - only upgraded!");
    }
    
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "opened database accounts.db");
      }
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      Log.e("AccountManagerService", "upgrade from version " + paramInt1 + " to version " + paramInt2);
      int i = paramInt1;
      if (paramInt1 == 1) {
        i = paramInt1 + 1;
      }
      paramInt1 = i;
      if (i == 2)
      {
        createGrantsTable(paramSQLiteDatabase);
        paramSQLiteDatabase.execSQL("DROP TRIGGER accountsDelete");
        createAccountsDeletionTrigger(paramSQLiteDatabase);
        paramInt1 = i + 1;
      }
      i = paramInt1;
      if (paramInt1 == 3)
      {
        paramSQLiteDatabase.execSQL("UPDATE accounts SET type = 'com.google' WHERE type == 'com.google.GAIA'");
        i = paramInt1 + 1;
      }
      paramInt1 = i;
      if (i == 4)
      {
        createSharedAccountsTable(paramSQLiteDatabase);
        paramInt1 = i + 1;
      }
      i = paramInt1;
      if (paramInt1 == 5)
      {
        addOldAccountNameColumn(paramSQLiteDatabase);
        i = paramInt1 + 1;
      }
      paramInt1 = i;
      if (i == 6)
      {
        addLastSuccessfullAuthenticatedTimeColumn(paramSQLiteDatabase);
        paramInt1 = i + 1;
      }
      i = paramInt1;
      if (paramInt1 == 7)
      {
        addDebugTable(paramSQLiteDatabase);
        i = paramInt1 + 1;
      }
      paramInt1 = i;
      if (i == 8)
      {
        populateMetaTableWithAuthTypeAndUID(paramSQLiteDatabase, AccountManagerService.-wrap10(this.mContext, this.mUserId));
        paramInt1 = i + 1;
      }
      if (paramInt1 != paramInt2) {
        Log.e("AccountManagerService", "failed to upgrade version " + paramInt1 + " to version " + paramInt2);
      }
    }
  }
  
  private class RemoveAccountSession
    extends AccountManagerService.Session
  {
    final Account mAccount;
    
    public RemoveAccountSession(AccountManagerService.UserAccounts paramUserAccounts, IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, boolean paramBoolean)
    {
      super(paramUserAccounts, paramIAccountManagerResponse, paramAccount.type, paramBoolean, true, paramAccount.name, false);
      this.mAccount = paramAccount;
    }
    
    public void onResult(Bundle paramBundle)
    {
      Bundle.setDefusable(paramBundle, true);
      if ((paramBundle == null) || (!paramBundle.containsKey("booleanResult")) || (paramBundle.containsKey("intent"))) {}
      for (;;)
      {
        super.onResult(paramBundle);
        return;
        boolean bool = paramBundle.getBoolean("booleanResult");
        if (bool) {
          AccountManagerService.-wrap5(AccountManagerService.this, this.mAccounts, this.mAccount, getCallingUid());
        }
        IAccountManagerResponse localIAccountManagerResponse = getResponseAndClose();
        if (localIAccountManagerResponse != null)
        {
          if (Log.isLoggable("AccountManagerService", 2)) {
            Log.v("AccountManagerService", getClass().getSimpleName() + " calling onResult() on response " + localIAccountManagerResponse);
          }
          Bundle localBundle = new Bundle();
          localBundle.putBoolean("booleanResult", bool);
          try
          {
            localIAccountManagerResponse.onResult(localBundle);
          }
          catch (RemoteException localRemoteException) {}
        }
      }
    }
    
    public void run()
      throws RemoteException
    {
      this.mAuthenticator.getAccountRemovalAllowed(this, this.mAccount);
    }
    
    protected String toDebugString(long paramLong)
    {
      return super.toDebugString(paramLong) + ", removeAccount" + ", account " + this.mAccount;
    }
  }
  
  private abstract class Session
    extends IAccountAuthenticatorResponse.Stub
    implements IBinder.DeathRecipient, ServiceConnection
  {
    final String mAccountName;
    final String mAccountType;
    protected final AccountManagerService.UserAccounts mAccounts;
    final boolean mAuthDetailsRequired;
    IAccountAuthenticator mAuthenticator = null;
    final long mCreationTime;
    final boolean mExpectActivityLaunch;
    private int mNumErrors = 0;
    private int mNumRequestContinued = 0;
    public int mNumResults = 0;
    IAccountManagerResponse mResponse;
    private final boolean mStripAuthTokenFromResult;
    final boolean mUpdateLastAuthenticatedTime;
    
    public Session(AccountManagerService.UserAccounts paramUserAccounts, IAccountManagerResponse paramIAccountManagerResponse, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2, boolean paramBoolean3)
    {
      this(paramUserAccounts, paramIAccountManagerResponse, paramString1, paramBoolean1, paramBoolean2, paramString2, paramBoolean3, false);
    }
    
    public Session(AccountManagerService.UserAccounts arg2, IAccountManagerResponse paramIAccountManagerResponse, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2, boolean paramBoolean3, boolean paramBoolean4)
    {
      if (paramString1 == null) {
        throw new IllegalArgumentException("accountType is null");
      }
      this.mAccounts = ???;
      this.mStripAuthTokenFromResult = paramBoolean2;
      this.mResponse = paramIAccountManagerResponse;
      this.mAccountType = paramString1;
      this.mExpectActivityLaunch = paramBoolean1;
      this.mCreationTime = SystemClock.elapsedRealtime();
      this.mAccountName = paramString2;
      this.mAuthDetailsRequired = paramBoolean3;
      this.mUpdateLastAuthenticatedTime = paramBoolean4;
      synchronized (AccountManagerService.-get4(AccountManagerService.this))
      {
        AccountManagerService.-get4(AccountManagerService.this).put(toString(), this);
        if (paramIAccountManagerResponse == null) {}
      }
    }
    
    private boolean bindToAuthenticator(String paramString)
    {
      RegisteredServicesCache.ServiceInfo localServiceInfo = AccountManagerService.-get2(AccountManagerService.this).getServiceInfo(AuthenticatorDescription.newKey(paramString), AccountManagerService.UserAccounts.-get8(this.mAccounts));
      if (localServiceInfo == null)
      {
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", "there is no authenticator for " + paramString + ", bailing out");
        }
        return false;
      }
      if ((AccountManagerService.-wrap4(AccountManagerService.this, AccountManagerService.UserAccounts.-get8(this.mAccounts))) || (localServiceInfo.componentInfo.directBootAware))
      {
        paramString = new Intent();
        paramString.setAction("android.accounts.AccountAuthenticator");
        paramString.setComponent(localServiceInfo.componentName);
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", "performing bindService to " + localServiceInfo.componentName);
        }
        if (!AccountManagerService.this.mContext.bindServiceAsUser(paramString, this, 1, UserHandle.of(AccountManagerService.UserAccounts.-get8(this.mAccounts))))
        {
          if (Log.isLoggable("AccountManagerService", 2)) {
            Log.v("AccountManagerService", "bindService to " + localServiceInfo.componentName + " failed");
          }
          return false;
        }
      }
      else
      {
        Slog.w("AccountManagerService", "Blocking binding to authenticator " + localServiceInfo.componentName + " which isn't encryption aware");
        return false;
      }
      return true;
    }
    
    private void close()
    {
      synchronized (AccountManagerService.-get4(AccountManagerService.this))
      {
        Object localObject1 = AccountManagerService.-get4(AccountManagerService.this).remove(toString());
        if (localObject1 == null) {
          return;
        }
        if (this.mResponse != null)
        {
          this.mResponse.asBinder().unlinkToDeath(this, 0);
          this.mResponse = null;
        }
        cancelTimeout();
        unbind();
        return;
      }
    }
    
    private void unbind()
    {
      if (this.mAuthenticator != null)
      {
        this.mAuthenticator = null;
        AccountManagerService.this.mContext.unbindService(this);
      }
    }
    
    void bind()
    {
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", "initiating bind to authenticator type " + this.mAccountType);
      }
      if (!bindToAuthenticator(this.mAccountType))
      {
        Log.d("AccountManagerService", "bind attempt failed for " + toDebugString());
        onError(1, "bind failure");
      }
    }
    
    public void binderDied()
    {
      this.mResponse = null;
      close();
    }
    
    public void cancelTimeout()
    {
      AccountManagerService.this.mMessageHandler.removeMessages(3, this);
    }
    
    protected void checkKeyIntent(int paramInt, Intent paramIntent)
      throws SecurityException
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        Object localObject = AccountManagerService.this.mContext.getPackageManager();
        paramIntent = ((PackageManager)localObject).resolveActivityAsUser(paramIntent, 0, AccountManagerService.UserAccounts.-get8(this.mAccounts)).activityInfo;
        if (((PackageManager)localObject).checkSignatures(paramInt, paramIntent.applicationInfo.uid) != 0)
        {
          localObject = paramIntent.packageName;
          throw new SecurityException(String.format("KEY_INTENT resolved to an Activity (%s) in a package (%s) that does not share a signature with the supplying authenticator (%s).", new Object[] { paramIntent.name, localObject, this.mAccountType }));
        }
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
      Binder.restoreCallingIdentity(l);
    }
    
    IAccountManagerResponse getResponseAndClose()
    {
      if (this.mResponse == null) {
        return null;
      }
      IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
      close();
      return localIAccountManagerResponse;
    }
    
    public void onError(int paramInt, String paramString)
    {
      this.mNumErrors += 1;
      IAccountManagerResponse localIAccountManagerResponse = getResponseAndClose();
      if (localIAccountManagerResponse != null) {
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", getClass().getSimpleName() + " calling onError() on response " + localIAccountManagerResponse);
        }
      }
      while (!Log.isLoggable("AccountManagerService", 2)) {
        try
        {
          localIAccountManagerResponse.onError(paramInt, paramString);
          return;
        }
        catch (RemoteException paramString)
        {
          while (!Log.isLoggable("AccountManagerService", 2)) {}
          Log.v("AccountManagerService", "Session.onError: caught RemoteException while responding", paramString);
          return;
        }
      }
      Log.v("AccountManagerService", "Session.onError: already closed");
    }
    
    public void onRequestContinued()
    {
      this.mNumRequestContinued += 1;
    }
    
    public void onResult(Bundle paramBundle)
    {
      Bundle.setDefusable(paramBundle, true);
      this.mNumResults += 1;
      Object localObject1 = null;
      boolean bool1;
      if (paramBundle != null)
      {
        boolean bool2 = paramBundle.getBoolean("booleanResult", false);
        if (!paramBundle.containsKey("authAccount")) {
          break label329;
        }
        bool1 = paramBundle.containsKey("accountType");
        if (!this.mUpdateLastAuthenticatedTime) {
          break label339;
        }
        if (bool2) {
          break label334;
        }
        label61:
        if ((bool1) || (this.mAuthDetailsRequired))
        {
          bool2 = AccountManagerService.-wrap3(AccountManagerService.this, this.mAccountName, this.mAccountType);
          if ((bool1) && (bool2)) {
            AccountManagerService.-wrap7(AccountManagerService.this, new Account(this.mAccountName, this.mAccountType));
          }
          if (this.mAuthDetailsRequired)
          {
            long l = -1L;
            if (bool2) {
              l = DatabaseUtils.longForQuery(this.mAccounts.openHelper.getReadableDatabase(), "SELECT last_password_entry_time_millis_epoch FROM accounts WHERE name=? AND type=?", new String[] { this.mAccountName, this.mAccountType });
            }
            paramBundle.putLong("lastAuthenticatedTime", l);
          }
        }
      }
      if (paramBundle != null)
      {
        localObject2 = (Intent)paramBundle.getParcelable("intent");
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          checkKeyIntent(Binder.getCallingUid(), (Intent)localObject2);
          localObject1 = localObject2;
        }
      }
      if ((paramBundle == null) || (TextUtils.isEmpty(paramBundle.getString("authtoken")))) {
        label235:
        if ((!this.mExpectActivityLaunch) || (paramBundle == null) || (!paramBundle.containsKey("intent"))) {
          break label431;
        }
      }
      label329:
      label334:
      label339:
      label431:
      for (Object localObject2 = this.mResponse;; localObject2 = getResponseAndClose())
      {
        if ((localObject2 != null) && (paramBundle != null)) {
          break label440;
        }
        try
        {
          if (Log.isLoggable("AccountManagerService", 2)) {
            Log.v("AccountManagerService", getClass().getSimpleName() + " calling onError() on response " + localObject2);
          }
          ((IAccountManagerResponse)localObject2).onError(5, "null bundle returned");
          return;
        }
        catch (RemoteException paramBundle)
        {
          String str;
          while (!Log.isLoggable("AccountManagerService", 2)) {}
          Log.v("AccountManagerService", "failure while notifying response", paramBundle);
          return;
        }
        bool1 = false;
        break;
        bool1 = true;
        break label61;
        bool1 = false;
        break label61;
        localObject2 = paramBundle.getString("authAccount");
        str = paramBundle.getString("accountType");
        if ((TextUtils.isEmpty((CharSequence)localObject2)) || (TextUtils.isEmpty(str))) {
          break label235;
        }
        localObject2 = new Account((String)localObject2, str);
        AccountManagerService.this.cancelNotification(AccountManagerService.-wrap9(AccountManagerService.this, this.mAccounts, (Account)localObject2).intValue(), new UserHandle(AccountManagerService.UserAccounts.-get8(this.mAccounts)));
        break label235;
      }
      label440:
      if (this.mStripAuthTokenFromResult) {
        paramBundle.remove("authtoken");
      }
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", getClass().getSimpleName() + " calling onResult() on response " + localObject2);
      }
      if ((paramBundle.getInt("errorCode", -1) > 0) && (localObject1 == null))
      {
        ((IAccountManagerResponse)localObject2).onError(paramBundle.getInt("errorCode"), paramBundle.getString("errorMessage"));
        return;
      }
      ((IAccountManagerResponse)localObject2).onResult(paramBundle);
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      this.mAuthenticator = IAccountAuthenticator.Stub.asInterface(paramIBinder);
      try
      {
        run();
        return;
      }
      catch (RemoteException paramComponentName)
      {
        onError(1, "remote exception");
      }
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      this.mAuthenticator = null;
      paramComponentName = getResponseAndClose();
      if (paramComponentName != null) {}
      try
      {
        paramComponentName.onError(1, "disconnected");
        return;
      }
      catch (RemoteException paramComponentName)
      {
        while (!Log.isLoggable("AccountManagerService", 2)) {}
        Log.v("AccountManagerService", "Session.onServiceDisconnected: caught RemoteException while responding", paramComponentName);
      }
    }
    
    public void onTimedOut()
    {
      IAccountManagerResponse localIAccountManagerResponse = getResponseAndClose();
      if (localIAccountManagerResponse != null) {}
      try
      {
        localIAccountManagerResponse.onError(1, "timeout");
        return;
      }
      catch (RemoteException localRemoteException)
      {
        while (!Log.isLoggable("AccountManagerService", 2)) {}
        Log.v("AccountManagerService", "Session.onTimedOut: caught RemoteException while responding", localRemoteException);
      }
    }
    
    public abstract void run()
      throws RemoteException;
    
    protected String toDebugString()
    {
      return toDebugString(SystemClock.elapsedRealtime());
    }
    
    protected String toDebugString(long paramLong)
    {
      StringBuilder localStringBuilder = new StringBuilder().append("Session: expectLaunch ").append(this.mExpectActivityLaunch).append(", connected ");
      if (this.mAuthenticator != null) {}
      for (boolean bool = true;; bool = false) {
        return bool + ", stats (" + this.mNumResults + "/" + this.mNumRequestContinued + "/" + this.mNumErrors + ")" + ", lifetime " + (paramLong - this.mCreationTime) / 1000.0D;
      }
    }
  }
  
  private abstract class StartAccountSession
    extends AccountManagerService.Session
  {
    private final boolean mIsPasswordForwardingAllowed;
    
    public StartAccountSession(AccountManagerService.UserAccounts paramUserAccounts, IAccountManagerResponse paramIAccountManagerResponse, String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    {
      super(paramUserAccounts, paramIAccountManagerResponse, paramString1, paramBoolean1, true, paramString2, paramBoolean2, paramBoolean3);
      this.mIsPasswordForwardingAllowed = paramBoolean4;
    }
    
    public void onResult(Bundle paramBundle)
    {
      Bundle.setDefusable(paramBundle, true);
      this.mNumResults += 1;
      Object localObject1 = null;
      if (paramBundle != null)
      {
        localObject2 = (Intent)paramBundle.getParcelable("intent");
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          checkKeyIntent(Binder.getCallingUid(), (Intent)localObject2);
          localObject1 = localObject2;
        }
      }
      if ((this.mExpectActivityLaunch) && (paramBundle != null) && (paramBundle.containsKey("intent"))) {}
      for (Object localObject2 = this.mResponse; localObject2 == null; localObject2 = getResponseAndClose()) {
        return;
      }
      if (paramBundle == null)
      {
        if (Log.isLoggable("AccountManagerService", 2)) {
          Log.v("AccountManagerService", getClass().getSimpleName() + " calling onError() on response " + localObject2);
        }
        AccountManagerService.-wrap19(AccountManagerService.this, (IAccountManagerResponse)localObject2, 5, "null bundle returned");
        return;
      }
      if ((paramBundle.getInt("errorCode", -1) > 0) && (localObject1 == null))
      {
        AccountManagerService.-wrap19(AccountManagerService.this, (IAccountManagerResponse)localObject2, paramBundle.getInt("errorCode"), paramBundle.getString("errorMessage"));
        return;
      }
      if (!this.mIsPasswordForwardingAllowed) {
        paramBundle.remove("password");
      }
      paramBundle.remove("authtoken");
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", getClass().getSimpleName() + " calling onResult() on response " + localObject2);
      }
      localObject1 = paramBundle.getBundle("accountSessionBundle");
      if (localObject1 != null)
      {
        String str = ((Bundle)localObject1).getString("accountType");
        if ((TextUtils.isEmpty(str)) || (!this.mAccountType.equalsIgnoreCase(str))) {
          break label316;
        }
      }
      for (;;)
      {
        ((Bundle)localObject1).putString("accountType", this.mAccountType);
        try
        {
          paramBundle.putBundle("accountSessionBundle", CryptoHelper.getInstance().encryptBundle((Bundle)localObject1));
          AccountManagerService.-wrap20(AccountManagerService.this, (IAccountManagerResponse)localObject2, paramBundle);
          return;
        }
        catch (GeneralSecurityException paramBundle)
        {
          label316:
          if (!Log.isLoggable("AccountManagerService", 3)) {
            break;
          }
          Log.v("AccountManagerService", "Failed to encrypt session bundle!", paramBundle);
          AccountManagerService.-wrap19(AccountManagerService.this, (IAccountManagerResponse)localObject2, 5, "failed to encrypt session bundle");
        }
        Log.w("AccountManagerService", "Account type in session bundle doesn't match request.");
      }
    }
  }
  
  private class TestFeaturesSession
    extends AccountManagerService.Session
  {
    private final Account mAccount;
    private final String[] mFeatures;
    
    public TestFeaturesSession(AccountManagerService.UserAccounts paramUserAccounts, IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String[] paramArrayOfString)
    {
      super(paramUserAccounts, paramIAccountManagerResponse, paramAccount.type, false, true, paramAccount.name, false);
      this.mFeatures = paramArrayOfString;
      this.mAccount = paramAccount;
    }
    
    public void onResult(Bundle paramBundle)
    {
      Bundle.setDefusable(paramBundle, true);
      IAccountManagerResponse localIAccountManagerResponse = getResponseAndClose();
      if ((localIAccountManagerResponse == null) || (paramBundle == null)) {}
      try
      {
        localIAccountManagerResponse.onError(5, "null bundle");
        return;
      }
      catch (RemoteException paramBundle)
      {
        Bundle localBundle;
        while (!Log.isLoggable("AccountManagerService", 2)) {}
        Log.v("AccountManagerService", "failure while notifying response", paramBundle);
      }
      if (Log.isLoggable("AccountManagerService", 2)) {
        Log.v("AccountManagerService", getClass().getSimpleName() + " calling onResult() on response " + localIAccountManagerResponse);
      }
      localBundle = new Bundle();
      localBundle.putBoolean("booleanResult", paramBundle.getBoolean("booleanResult", false));
      localIAccountManagerResponse.onResult(localBundle);
      return;
    }
    
    public void run()
      throws RemoteException
    {
      try
      {
        this.mAuthenticator.hasFeatures(this, this.mAccount, this.mFeatures);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        onError(1, "remote exception");
      }
    }
    
    protected String toDebugString(long paramLong)
    {
      String str = null;
      StringBuilder localStringBuilder = new StringBuilder().append(super.toDebugString(paramLong)).append(", hasFeatures").append(", ").append(this.mAccount).append(", ");
      if (this.mFeatures != null) {
        str = TextUtils.join(",", this.mFeatures);
      }
      return str;
    }
  }
  
  static class UserAccounts
  {
    final HashMap<String, Account[]> accountCache = new LinkedHashMap();
    private final TokenCache accountTokenCaches = new TokenCache();
    private final HashMap<Account, HashMap<String, String>> authTokenCache = new HashMap();
    final Object cacheLock = new Object();
    private final HashMap<Pair<Pair<Account, String>, Integer>, Integer> credentialsPermissionNotificationIds = new HashMap();
    private int debugDbInsertionPoint = -1;
    final AccountManagerService.DeDatabaseHelper openHelper;
    private final HashMap<Account, AtomicReference<String>> previousNameCache = new HashMap();
    private final HashMap<Account, Integer> signinRequiredNotificationIds = new HashMap();
    private SQLiteStatement statementForLogging;
    private final HashMap<Account, HashMap<String, String>> userDataCache = new HashMap();
    private final int userId;
    
    UserAccounts(Context paramContext, int paramInt, File paramFile1, File paramFile2)
    {
      this.userId = paramInt;
      synchronized (this.cacheLock)
      {
        this.openHelper = AccountManagerService.DeDatabaseHelper.create(paramContext, paramInt, paramFile1, paramFile2);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accounts/AccountManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */