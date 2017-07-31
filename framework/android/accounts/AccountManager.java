package android.accounts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.SQLException;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SeempLog;
import com.google.android.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class AccountManager
{
  public static final String ACCOUNT_ACCESS_TOKEN_TYPE = "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE";
  public static final String ACTION_AUTHENTICATOR_INTENT = "android.accounts.AccountAuthenticator";
  public static final String AUTHENTICATOR_ATTRIBUTES_NAME = "account-authenticator";
  public static final String AUTHENTICATOR_META_DATA_NAME = "android.accounts.AccountAuthenticator";
  public static final int ERROR_CODE_BAD_ARGUMENTS = 7;
  public static final int ERROR_CODE_BAD_AUTHENTICATION = 9;
  public static final int ERROR_CODE_BAD_REQUEST = 8;
  public static final int ERROR_CODE_CANCELED = 4;
  public static final int ERROR_CODE_INVALID_RESPONSE = 5;
  public static final int ERROR_CODE_MANAGEMENT_DISABLED_FOR_ACCOUNT_TYPE = 101;
  public static final int ERROR_CODE_NETWORK_ERROR = 3;
  public static final int ERROR_CODE_REMOTE_EXCEPTION = 1;
  public static final int ERROR_CODE_UNSUPPORTED_OPERATION = 6;
  public static final int ERROR_CODE_USER_RESTRICTED = 100;
  public static final String KEY_ACCOUNTS = "accounts";
  public static final String KEY_ACCOUNT_ACCESS_ID = "accountAccessId";
  public static final String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE = "accountAuthenticatorResponse";
  public static final String KEY_ACCOUNT_MANAGER_RESPONSE = "accountManagerResponse";
  public static final String KEY_ACCOUNT_NAME = "authAccount";
  public static final String KEY_ACCOUNT_SESSION_BUNDLE = "accountSessionBundle";
  public static final String KEY_ACCOUNT_STATUS_TOKEN = "accountStatusToken";
  public static final String KEY_ACCOUNT_TYPE = "accountType";
  public static final String KEY_ANDROID_PACKAGE_NAME = "androidPackageName";
  public static final String KEY_AUTHENTICATOR_TYPES = "authenticator_types";
  public static final String KEY_AUTHTOKEN = "authtoken";
  public static final String KEY_AUTH_FAILED_MESSAGE = "authFailedMessage";
  public static final String KEY_AUTH_TOKEN_LABEL = "authTokenLabelKey";
  public static final String KEY_BOOLEAN_RESULT = "booleanResult";
  public static final String KEY_CALLER_PID = "callerPid";
  public static final String KEY_CALLER_UID = "callerUid";
  public static final String KEY_ERROR_CODE = "errorCode";
  public static final String KEY_ERROR_MESSAGE = "errorMessage";
  public static final String KEY_INTENT = "intent";
  public static final String KEY_LAST_AUTHENTICATED_TIME = "lastAuthenticatedTime";
  public static final String KEY_NOTIFY_ON_FAILURE = "notifyOnAuthFailure";
  public static final String KEY_PASSWORD = "password";
  public static final String KEY_USERDATA = "userdata";
  public static final String LOGIN_ACCOUNTS_CHANGED_ACTION = "android.accounts.LOGIN_ACCOUNTS_CHANGED";
  private static final String TAG = "AccountManager";
  private final BroadcastReceiver mAccountsChangedBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      paramAnonymousIntent = AccountManager.this.getAccounts();
      synchronized (AccountManager.-get0(AccountManager.this))
      {
        Iterator localIterator = AccountManager.-get0(AccountManager.this).entrySet().iterator();
        if (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          AccountManager.-wrap3(AccountManager.this, (Handler)localEntry.getValue(), (OnAccountsUpdateListener)localEntry.getKey(), paramAnonymousIntent);
        }
      }
    }
  };
  private final HashMap<OnAccountsUpdateListener, Handler> mAccountsUpdatedListeners = Maps.newHashMap();
  private final Context mContext;
  private final Handler mMainHandler;
  private final IAccountManager mService;
  
  public AccountManager(Context paramContext, IAccountManager paramIAccountManager)
  {
    this.mContext = paramContext;
    this.mService = paramIAccountManager;
    this.mMainHandler = new Handler(this.mContext.getMainLooper());
  }
  
  public AccountManager(Context paramContext, IAccountManager paramIAccountManager, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mService = paramIAccountManager;
    this.mMainHandler = paramHandler;
  }
  
  private Exception convertErrorToException(int paramInt, String paramString)
  {
    if (paramInt == 3) {
      return new IOException(paramString);
    }
    if (paramInt == 6) {
      return new UnsupportedOperationException(paramString);
    }
    if (paramInt == 5) {
      return new AuthenticatorException(paramString);
    }
    if (paramInt == 7) {
      return new IllegalArgumentException(paramString);
    }
    return new AuthenticatorException(paramString);
  }
  
  private void ensureNotOnMainThread()
  {
    Object localObject = Looper.myLooper();
    if ((localObject != null) && (localObject == this.mContext.getMainLooper()))
    {
      localObject = new IllegalStateException("calling this from your main thread can lead to deadlock");
      Log.e("AccountManager", "calling this from your main thread can lead to deadlock and/or ANRs", (Throwable)localObject);
      if (this.mContext.getApplicationInfo().targetSdkVersion >= 8) {
        throw ((Throwable)localObject);
      }
    }
  }
  
  public static AccountManager get(Context paramContext)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("context is null");
    }
    return (AccountManager)paramContext.getSystemService("account");
  }
  
  @Deprecated
  public static Intent newChooseAccountIntent(Account paramAccount, ArrayList<Account> paramArrayList, String[] paramArrayOfString1, boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString2, Bundle paramBundle)
  {
    return newChooseAccountIntent(paramAccount, paramArrayList, paramArrayOfString1, paramString1, paramString2, paramArrayOfString2, paramBundle);
  }
  
  public static Intent newChooseAccountIntent(Account paramAccount, List<Account> paramList, String[] paramArrayOfString1, String paramString1, String paramString2, String[] paramArrayOfString2, Bundle paramBundle)
  {
    Object localObject = null;
    Intent localIntent = new Intent();
    ComponentName localComponentName = ComponentName.unflattenFromString(Resources.getSystem().getString(17039456));
    localIntent.setClassName(localComponentName.getPackageName(), localComponentName.getClassName());
    if (paramList == null) {}
    for (paramList = (List<Account>)localObject;; paramList = new ArrayList(paramList))
    {
      localIntent.putExtra("allowableAccounts", paramList);
      localIntent.putExtra("allowableAccountTypes", paramArrayOfString1);
      localIntent.putExtra("addAccountOptions", paramBundle);
      localIntent.putExtra("selectedAccount", paramAccount);
      localIntent.putExtra("descriptionTextOverride", paramString1);
      localIntent.putExtra("authTokenType", paramString2);
      localIntent.putExtra("addAccountRequiredFeatures", paramArrayOfString2);
      return localIntent;
    }
  }
  
  private void postToHandler(Handler paramHandler, final AccountManagerCallback<Bundle> paramAccountManagerCallback, final AccountManagerFuture<Bundle> paramAccountManagerFuture)
  {
    Handler localHandler = paramHandler;
    if (paramHandler == null) {
      localHandler = this.mMainHandler;
    }
    localHandler.post(new Runnable()
    {
      public void run()
      {
        paramAccountManagerCallback.run(paramAccountManagerFuture);
      }
    });
  }
  
  private void postToHandler(Handler paramHandler, final OnAccountsUpdateListener paramOnAccountsUpdateListener, Account[] paramArrayOfAccount)
  {
    final Account[] arrayOfAccount = new Account[paramArrayOfAccount.length];
    System.arraycopy(paramArrayOfAccount, 0, arrayOfAccount, 0, arrayOfAccount.length);
    paramArrayOfAccount = paramHandler;
    if (paramHandler == null) {
      paramArrayOfAccount = this.mMainHandler;
    }
    paramArrayOfAccount.post(new Runnable()
    {
      public void run()
      {
        try
        {
          paramOnAccountsUpdateListener.onAccountsUpdated(arrayOfAccount);
          return;
        }
        catch (SQLException localSQLException)
        {
          Log.e("AccountManager", "Can't update accounts", localSQLException);
        }
      }
    });
  }
  
  public static Bundle sanitizeResult(Bundle paramBundle)
  {
    if ((paramBundle == null) || (!paramBundle.containsKey("authtoken")) || (TextUtils.isEmpty(paramBundle.getString("authtoken")))) {
      return paramBundle;
    }
    paramBundle = new Bundle(paramBundle);
    paramBundle.putString("authtoken", "<omitted for logging purposes>");
    return paramBundle;
  }
  
  public AccountManagerFuture<Bundle> addAccount(final String paramString1, final String paramString2, final String[] paramArrayOfString, Bundle paramBundle, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    SeempLog.record(29);
    if (paramString1 == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    final Bundle localBundle = new Bundle();
    if (paramBundle != null) {
      localBundle.putAll(paramBundle);
    }
    localBundle.putString("androidPackageName", this.mContext.getPackageName());
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        String str1 = paramString1;
        String str2 = paramString2;
        String[] arrayOfString = paramArrayOfString;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.addAccount(localIAccountManagerResponse, str1, str2, arrayOfString, bool, localBundle);
          return;
        }
      }
    }.start();
  }
  
  public AccountManagerFuture<Bundle> addAccountAsUser(final String paramString1, final String paramString2, final String[] paramArrayOfString, Bundle paramBundle, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler, final UserHandle paramUserHandle)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    if (paramUserHandle == null) {
      throw new IllegalArgumentException("userHandle is null");
    }
    final Bundle localBundle = new Bundle();
    if (paramBundle != null) {
      localBundle.putAll(paramBundle);
    }
    localBundle.putString("androidPackageName", this.mContext.getPackageName());
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        String str1 = paramString1;
        String str2 = paramString2;
        String[] arrayOfString = paramArrayOfString;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.addAccountAsUser(localIAccountManagerResponse, str1, str2, arrayOfString, bool, localBundle, paramUserHandle.getIdentifier());
          return;
        }
      }
    }.start();
  }
  
  public boolean addAccountExplicitly(Account paramAccount, String paramString, Bundle paramBundle)
  {
    SeempLog.record(24);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    try
    {
      boolean bool = this.mService.addAccountExplicitly(paramAccount, paramString, paramBundle);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public void addOnAccountsUpdatedListener(OnAccountsUpdateListener paramOnAccountsUpdateListener, Handler paramHandler, boolean paramBoolean)
  {
    if (paramOnAccountsUpdateListener == null) {
      throw new IllegalArgumentException("the listener is null");
    }
    synchronized (this.mAccountsUpdatedListeners)
    {
      if (this.mAccountsUpdatedListeners.containsKey(paramOnAccountsUpdateListener)) {
        throw new IllegalStateException("this listener is already added");
      }
    }
    boolean bool = this.mAccountsUpdatedListeners.isEmpty();
    this.mAccountsUpdatedListeners.put(paramOnAccountsUpdateListener, paramHandler);
    if (bool)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.accounts.LOGIN_ACCOUNTS_CHANGED");
      localIntentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
      this.mContext.registerReceiver(this.mAccountsChangedBroadcastReceiver, localIntentFilter);
    }
    if (paramBoolean) {
      postToHandler(paramHandler, paramOnAccountsUpdateListener, getAccounts());
    }
  }
  
  public void addSharedAccountsFromParentUser(UserHandle paramUserHandle1, UserHandle paramUserHandle2)
  {
    try
    {
      this.mService.addSharedAccountsFromParentUser(paramUserHandle1.getIdentifier(), paramUserHandle2.getIdentifier());
      return;
    }
    catch (RemoteException paramUserHandle1)
    {
      throw paramUserHandle1.rethrowFromSystemServer();
    }
  }
  
  public String blockingGetAuthToken(Account paramAccount, String paramString, boolean paramBoolean)
    throws OperationCanceledException, IOException, AuthenticatorException
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    Bundle localBundle = (Bundle)getAuthToken(paramAccount, paramString, paramBoolean, null, null).getResult();
    if (localBundle == null)
    {
      Log.e("AccountManager", "blockingGetAuthToken: null was returned from getResult() for " + paramAccount + ", authTokenType " + paramString);
      return null;
    }
    return localBundle.getString("authtoken");
  }
  
  public void clearPassword(Account paramAccount)
  {
    SeempLog.record(27);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    try
    {
      this.mService.clearPassword(paramAccount);
      return;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Bundle> confirmCredentials(Account paramAccount, Bundle paramBundle, Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    return confirmCredentialsAsUser(paramAccount, paramBundle, paramActivity, paramAccountManagerCallback, paramHandler, Process.myUserHandle());
  }
  
  public AccountManagerFuture<Bundle> confirmCredentialsAsUser(final Account paramAccount, final Bundle paramBundle, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler, UserHandle paramUserHandle)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        Account localAccount = paramAccount;
        Bundle localBundle = paramBundle;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.confirmCredentialsAsUser(localIAccountManagerResponse, localAccount, localBundle, bool, this.val$userId);
          return;
        }
      }
    }.start();
  }
  
  public AccountManagerFuture<Boolean> copyAccountToUser(final Account paramAccount, final UserHandle paramUserHandle1, final UserHandle paramUserHandle2, AccountManagerCallback<Boolean> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if ((paramUserHandle2 == null) || (paramUserHandle1 == null)) {
      throw new IllegalArgumentException("fromUser and toUser cannot be null");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public Boolean bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        if (!paramAnonymousBundle.containsKey("booleanResult")) {
          throw new AuthenticatorException("no result in response");
        }
        return Boolean.valueOf(paramAnonymousBundle.getBoolean("booleanResult"));
      }
      
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(34);
        AccountManager.-get3(jdField_this).copyAccountToUser(this.mResponse, paramAccount, paramUserHandle1.getIdentifier(), paramUserHandle2.getIdentifier());
      }
    }.start();
  }
  
  public IntentSender createRequestAccountAccessIntentSenderAsUser(Account paramAccount, String paramString, UserHandle paramUserHandle)
  {
    try
    {
      paramAccount = this.mService.createRequestAccountAccessIntentSenderAsUser(paramAccount, paramString, paramUserHandle);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Bundle> editProperties(final String paramString, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    SeempLog.record(30);
    if (paramString == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        String str = paramString;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.editProperties(localIAccountManagerResponse, str, bool);
          return;
        }
      }
    }.start();
  }
  
  public AccountManagerFuture<Bundle> finishSession(Bundle paramBundle, Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    return finishSessionAsUser(paramBundle, paramActivity, Process.myUserHandle(), paramAccountManagerCallback, paramHandler);
  }
  
  public AccountManagerFuture<Bundle> finishSessionAsUser(final Bundle paramBundle, final Activity paramActivity, final UserHandle paramUserHandle, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramBundle == null) {
      throw new IllegalArgumentException("sessionBundle is null");
    }
    final Bundle localBundle = new Bundle();
    localBundle.putString("androidPackageName", this.mContext.getPackageName());
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        Bundle localBundle = paramBundle;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.finishSessionAsUser(localIAccountManagerResponse, localBundle, bool, localBundle, paramUserHandle.getIdentifier());
          return;
        }
      }
    }.start();
  }
  
  public Account[] getAccounts()
  {
    try
    {
      Account[] arrayOfAccount = this.mService.getAccounts(null, this.mContext.getOpPackageName());
      return arrayOfAccount;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Account[] getAccountsAsUser(int paramInt)
  {
    try
    {
      Account[] arrayOfAccount = this.mService.getAccountsAsUser(null, paramInt, this.mContext.getOpPackageName());
      return arrayOfAccount;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Account[] getAccountsByType(String paramString)
  {
    return getAccountsByTypeAsUser(paramString, Process.myUserHandle());
  }
  
  public AccountManagerFuture<Account[]> getAccountsByTypeAndFeatures(final String paramString, final String[] paramArrayOfString, AccountManagerCallback<Account[]> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("type is null");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public Account[] bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        if (!paramAnonymousBundle.containsKey("accounts")) {
          throw new AuthenticatorException("no result in response");
        }
        paramAnonymousBundle = paramAnonymousBundle.getParcelableArray("accounts");
        Account[] arrayOfAccount = new Account[paramAnonymousBundle.length];
        int i = 0;
        while (i < paramAnonymousBundle.length)
        {
          arrayOfAccount[i] = ((Account)paramAnonymousBundle[i]);
          i += 1;
        }
        return arrayOfAccount;
      }
      
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).getAccountsByFeatures(this.mResponse, paramString, paramArrayOfString, AccountManager.-get1(jdField_this).getOpPackageName());
      }
    }.start();
  }
  
  public Account[] getAccountsByTypeAsUser(String paramString, UserHandle paramUserHandle)
  {
    try
    {
      paramString = this.mService.getAccountsAsUser(paramString, paramUserHandle.getIdentifier(), this.mContext.getOpPackageName());
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public Account[] getAccountsByTypeForPackage(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = this.mService.getAccountsByTypeForPackage(paramString1, paramString2, this.mContext.getOpPackageName());
      return paramString1;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public Account[] getAccountsForPackage(String paramString, int paramInt)
  {
    try
    {
      paramString = this.mService.getAccountsForPackage(paramString, paramInt, this.mContext.getOpPackageName());
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Bundle> getAuthToken(final Account paramAccount, final String paramString, Bundle paramBundle, Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    final Bundle localBundle = new Bundle();
    if (paramBundle != null) {
      localBundle.putAll(paramBundle);
    }
    localBundle.putString("androidPackageName", this.mContext.getPackageName());
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).getAuthToken(this.mResponse, paramAccount, paramString, false, true, localBundle);
      }
    }.start();
  }
  
  public AccountManagerFuture<Bundle> getAuthToken(final Account paramAccount, final String paramString, Bundle paramBundle, final boolean paramBoolean, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    final Bundle localBundle = new Bundle();
    if (paramBundle != null) {
      localBundle.putAll(paramBundle);
    }
    localBundle.putString("androidPackageName", this.mContext.getPackageName());
    new AmsTask(this, null, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).getAuthToken(this.mResponse, paramAccount, paramString, paramBoolean, false, localBundle);
      }
    }.start();
  }
  
  @Deprecated
  public AccountManagerFuture<Bundle> getAuthToken(Account paramAccount, String paramString, boolean paramBoolean, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    return getAuthToken(paramAccount, paramString, null, paramBoolean, paramAccountManagerCallback, paramHandler);
  }
  
  public AccountManagerFuture<Bundle> getAuthTokenByFeatures(String paramString1, String paramString2, String[] paramArrayOfString, Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("account type is null");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    paramString1 = new GetAuthTokenByTypeAndFeaturesTask(paramString1, paramString2, paramArrayOfString, paramActivity, paramBundle1, paramBundle2, paramAccountManagerCallback, paramHandler);
    paramString1.start();
    return paramString1;
  }
  
  public AccountManagerFuture<String> getAuthTokenLabel(final String paramString1, final String paramString2, AccountManagerCallback<String> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public String bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        if (!paramAnonymousBundle.containsKey("authTokenLabelKey")) {
          throw new AuthenticatorException("no result in response");
        }
        return paramAnonymousBundle.getString("authTokenLabelKey");
      }
      
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).getAuthTokenLabel(this.mResponse, paramString1, paramString2);
      }
    }.start();
  }
  
  public AuthenticatorDescription[] getAuthenticatorTypes()
  {
    try
    {
      AuthenticatorDescription[] arrayOfAuthenticatorDescription = this.mService.getAuthenticatorTypes(UserHandle.getCallingUserId());
      return arrayOfAuthenticatorDescription;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public AuthenticatorDescription[] getAuthenticatorTypesAsUser(int paramInt)
  {
    try
    {
      AuthenticatorDescription[] arrayOfAuthenticatorDescription = this.mService.getAuthenticatorTypes(paramInt);
      return arrayOfAuthenticatorDescription;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public String getPassword(Account paramAccount)
  {
    SeempLog.record(22);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    try
    {
      paramAccount = this.mService.getPassword(paramAccount);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public String getPreviousName(Account paramAccount)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    try
    {
      paramAccount = this.mService.getPreviousName(paramAccount);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public Account[] getSharedAccounts(UserHandle paramUserHandle)
  {
    try
    {
      paramUserHandle = this.mService.getSharedAccountsAsUser(paramUserHandle.getIdentifier());
      return paramUserHandle;
    }
    catch (RemoteException paramUserHandle)
    {
      throw paramUserHandle.rethrowFromSystemServer();
    }
  }
  
  public String getUserData(Account paramAccount, String paramString)
  {
    SeempLog.record(23);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("key is null");
    }
    try
    {
      paramAccount = this.mService.getUserData(paramAccount, paramString);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public boolean hasAccountAccess(Account paramAccount, String paramString, UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mService.hasAccountAccess(paramAccount, paramString, paramUserHandle);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Boolean> hasFeatures(final Account paramAccount, final String[] paramArrayOfString, AccountManagerCallback<Boolean> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramArrayOfString == null) {
      throw new IllegalArgumentException("features is null");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public Boolean bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        if (!paramAnonymousBundle.containsKey("booleanResult")) {
          throw new AuthenticatorException("no result in response");
        }
        return Boolean.valueOf(paramAnonymousBundle.getBoolean("booleanResult"));
      }
      
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).hasFeatures(this.mResponse, paramAccount, paramArrayOfString, AccountManager.-get1(jdField_this).getOpPackageName());
      }
    }.start();
  }
  
  public void invalidateAuthToken(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    if (paramString2 != null) {}
    try
    {
      this.mService.invalidateAuthToken(paramString1, paramString2);
      return;
    }
    catch (RemoteException paramString1)
    {
      throw paramString1.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Boolean> isCredentialsUpdateSuggested(final Account paramAccount, final String paramString, AccountManagerCallback<Boolean> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("status token is empty");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public Boolean bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        if (!paramAnonymousBundle.containsKey("booleanResult")) {
          throw new AuthenticatorException("no result in response");
        }
        return Boolean.valueOf(paramAnonymousBundle.getBoolean("booleanResult"));
      }
      
      public void doWork()
        throws RemoteException
      {
        AccountManager.-get3(jdField_this).isCredentialsUpdateSuggested(this.mResponse, paramAccount, paramString);
      }
    }.start();
  }
  
  public boolean notifyAccountAuthenticated(Account paramAccount)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    try
    {
      boolean bool = this.mService.accountAuthenticated(paramAccount);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public String peekAuthToken(Account paramAccount, String paramString)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    try
    {
      paramAccount = this.mService.peekAuthToken(paramAccount, paramString);
      return paramAccount;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public AccountManagerFuture<Boolean> removeAccount(final Account paramAccount, AccountManagerCallback<Boolean> paramAccountManagerCallback, Handler paramHandler)
  {
    SeempLog.record(25);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public Boolean bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        if (!paramAnonymousBundle.containsKey("booleanResult")) {
          throw new AuthenticatorException("no result in response");
        }
        return Boolean.valueOf(paramAnonymousBundle.getBoolean("booleanResult"));
      }
      
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).removeAccount(this.mResponse, paramAccount, false);
      }
    }.start();
  }
  
  public AccountManagerFuture<Bundle> removeAccount(final Account paramAccount, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    SeempLog.record(28);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(34);
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        Account localAccount = paramAccount;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.removeAccount(localIAccountManagerResponse, localAccount, bool);
          return;
        }
      }
    }.start();
  }
  
  @Deprecated
  public AccountManagerFuture<Boolean> removeAccountAsUser(final Account paramAccount, AccountManagerCallback<Boolean> paramAccountManagerCallback, Handler paramHandler, final UserHandle paramUserHandle)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramUserHandle == null) {
      throw new IllegalArgumentException("userHandle is null");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public Boolean bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        if (!paramAnonymousBundle.containsKey("booleanResult")) {
          throw new AuthenticatorException("no result in response");
        }
        return Boolean.valueOf(paramAnonymousBundle.getBoolean("booleanResult"));
      }
      
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).removeAccountAsUser(this.mResponse, paramAccount, false, paramUserHandle.getIdentifier());
      }
    }.start();
  }
  
  public AccountManagerFuture<Bundle> removeAccountAsUser(final Account paramAccount, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler, final UserHandle paramUserHandle)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramUserHandle == null) {
      throw new IllegalArgumentException("userHandle is null");
    }
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(34);
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        Account localAccount = paramAccount;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.removeAccountAsUser(localIAccountManagerResponse, localAccount, bool, paramUserHandle.getIdentifier());
          return;
        }
      }
    }.start();
  }
  
  public boolean removeAccountExplicitly(Account paramAccount)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    try
    {
      boolean bool = this.mService.removeAccountExplicitly(paramAccount);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public void removeOnAccountsUpdatedListener(OnAccountsUpdateListener paramOnAccountsUpdateListener)
  {
    if (paramOnAccountsUpdateListener == null) {
      throw new IllegalArgumentException("listener is null");
    }
    synchronized (this.mAccountsUpdatedListeners)
    {
      if (!this.mAccountsUpdatedListeners.containsKey(paramOnAccountsUpdateListener))
      {
        Log.e("AccountManager", "Listener was not previously added");
        return;
      }
      this.mAccountsUpdatedListeners.remove(paramOnAccountsUpdateListener);
      if (this.mAccountsUpdatedListeners.isEmpty()) {
        this.mContext.unregisterReceiver(this.mAccountsChangedBroadcastReceiver);
      }
      return;
    }
  }
  
  public boolean removeSharedAccount(Account paramAccount, UserHandle paramUserHandle)
  {
    try
    {
      boolean bool = this.mService.removeSharedAccountAsUser(paramAccount, paramUserHandle.getIdentifier());
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Account> renameAccount(final Account paramAccount, final String paramString, AccountManagerCallback<Account> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null.");
    }
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("newName is empty or null.");
    }
    new Future2Task(this, paramHandler, paramAccountManagerCallback)
    {
      public Account bundleToResult(Bundle paramAnonymousBundle)
        throws AuthenticatorException
      {
        return new Account(paramAnonymousBundle.getString("authAccount"), paramAnonymousBundle.getString("accountType"), paramAnonymousBundle.getString("accountAccessId"));
      }
      
      public void doWork()
        throws RemoteException
      {
        SeempLog.record(31);
        AccountManager.-get3(jdField_this).renameAccount(this.mResponse, paramAccount, paramString);
      }
    }.start();
  }
  
  public void setAuthToken(Account paramAccount, String paramString1, String paramString2)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString1 == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }
    try
    {
      this.mService.setAuthToken(paramAccount, paramString1, paramString2);
      return;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public void setPassword(Account paramAccount, String paramString)
  {
    SeempLog.record(26);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    try
    {
      this.mService.setPassword(paramAccount, paramString);
      return;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public void setUserData(Account paramAccount, String paramString1, String paramString2)
  {
    SeempLog.record(28);
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (paramString1 == null) {
      throw new IllegalArgumentException("key is null");
    }
    try
    {
      this.mService.setUserData(paramAccount, paramString1, paramString2);
      return;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public boolean someUserHasAccount(Account paramAccount)
  {
    try
    {
      boolean bool = this.mService.someUserHasAccount(paramAccount);
      return bool;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Bundle> startAddAccountSession(final String paramString1, final String paramString2, final String[] paramArrayOfString, Bundle paramBundle, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("accountType is null");
    }
    final Bundle localBundle = new Bundle();
    if (paramBundle != null) {
      localBundle.putAll(paramBundle);
    }
    localBundle.putString("androidPackageName", this.mContext.getPackageName());
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        String str1 = paramString1;
        String str2 = paramString2;
        String[] arrayOfString = paramArrayOfString;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.startAddAccountSession(localIAccountManagerResponse, str1, str2, arrayOfString, bool, localBundle);
          return;
        }
      }
    }.start();
  }
  
  public AccountManagerFuture<Bundle> startUpdateCredentialsSession(final Account paramAccount, final String paramString, Bundle paramBundle, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    final Bundle localBundle = new Bundle();
    if (paramBundle != null) {
      localBundle.putAll(paramBundle);
    }
    localBundle.putString("androidPackageName", this.mContext.getPackageName());
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        Account localAccount = paramAccount;
        String str = paramString;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.startUpdateCredentialsSession(localIAccountManagerResponse, localAccount, str, bool, localBundle);
          return;
        }
      }
    }.start();
  }
  
  public void updateAppPermission(Account paramAccount, String paramString, int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mService.updateAppPermission(paramAccount, paramString, paramInt, paramBoolean);
      return;
    }
    catch (RemoteException paramAccount)
    {
      throw paramAccount.rethrowFromSystemServer();
    }
  }
  
  public AccountManagerFuture<Bundle> updateCredentials(final Account paramAccount, final String paramString, final Bundle paramBundle, final Activity paramActivity, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
  {
    if (paramAccount == null) {
      throw new IllegalArgumentException("account is null");
    }
    new AmsTask(this, paramActivity, paramHandler, paramAccountManagerCallback)
    {
      public void doWork()
        throws RemoteException
      {
        IAccountManager localIAccountManager = AccountManager.-get3(jdField_this);
        IAccountManagerResponse localIAccountManagerResponse = this.mResponse;
        Account localAccount = paramAccount;
        String str = paramString;
        if (paramActivity != null) {}
        for (boolean bool = true;; bool = false)
        {
          localIAccountManager.updateCredentials(localIAccountManagerResponse, localAccount, str, bool, paramBundle);
          return;
        }
      }
    }.start();
  }
  
  private abstract class AmsTask
    extends FutureTask<Bundle>
    implements AccountManagerFuture<Bundle>
  {
    final Activity mActivity;
    final AccountManagerCallback<Bundle> mCallback;
    final Handler mHandler;
    final IAccountManagerResponse mResponse;
    
    public AmsTask(Handler paramHandler, AccountManagerCallback<Bundle> paramAccountManagerCallback)
    {
      super()
      {
        public Bundle call()
          throws Exception
        {
          throw new IllegalStateException("this should never be called");
        }
      };
      this.mHandler = paramAccountManagerCallback;
      AccountManagerCallback localAccountManagerCallback;
      this.mCallback = localAccountManagerCallback;
      this.mActivity = paramHandler;
      this.mResponse = new Response(null);
    }
    
    /* Error */
    private Bundle internalGetResult(Long paramLong, TimeUnit paramTimeUnit)
      throws OperationCanceledException, IOException, AuthenticatorException
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 76	android/accounts/AccountManager$AmsTask:isDone	()Z
      //   4: ifne +10 -> 14
      //   7: aload_0
      //   8: getfield 37	android/accounts/AccountManager$AmsTask:this$0	Landroid/accounts/AccountManager;
      //   11: invokestatic 79	android/accounts/AccountManager:-wrap1	(Landroid/accounts/AccountManager;)V
      //   14: aload_1
      //   15: ifnonnull +19 -> 34
      //   18: aload_0
      //   19: invokevirtual 83	android/accounts/AccountManager$AmsTask:get	()Ljava/lang/Object;
      //   22: checkcast 85	android/os/Bundle
      //   25: astore_1
      //   26: aload_0
      //   27: iconst_1
      //   28: invokevirtual 89	android/accounts/AccountManager$AmsTask:cancel	(Z)Z
      //   31: pop
      //   32: aload_1
      //   33: areturn
      //   34: aload_0
      //   35: aload_1
      //   36: invokevirtual 95	java/lang/Long:longValue	()J
      //   39: aload_2
      //   40: invokevirtual 98	android/accounts/AccountManager$AmsTask:get	(JLjava/util/concurrent/TimeUnit;)Ljava/lang/Object;
      //   43: checkcast 85	android/os/Bundle
      //   46: astore_1
      //   47: aload_0
      //   48: iconst_1
      //   49: invokevirtual 89	android/accounts/AccountManager$AmsTask:cancel	(Z)Z
      //   52: pop
      //   53: aload_1
      //   54: areturn
      //   55: astore_1
      //   56: aload_1
      //   57: invokevirtual 102	java/util/concurrent/ExecutionException:getCause	()Ljava/lang/Throwable;
      //   60: astore_1
      //   61: aload_1
      //   62: instanceof 62
      //   65: ifeq +17 -> 82
      //   68: aload_1
      //   69: checkcast 62	java/io/IOException
      //   72: athrow
      //   73: astore_1
      //   74: aload_0
      //   75: iconst_1
      //   76: invokevirtual 89	android/accounts/AccountManager$AmsTask:cancel	(Z)Z
      //   79: pop
      //   80: aload_1
      //   81: athrow
      //   82: aload_1
      //   83: instanceof 104
      //   86: ifeq +12 -> 98
      //   89: new 64	android/accounts/AuthenticatorException
      //   92: dup
      //   93: aload_1
      //   94: invokespecial 106	android/accounts/AuthenticatorException:<init>	(Ljava/lang/Throwable;)V
      //   97: athrow
      //   98: aload_1
      //   99: instanceof 64
      //   102: ifeq +8 -> 110
      //   105: aload_1
      //   106: checkcast 64	android/accounts/AuthenticatorException
      //   109: athrow
      //   110: aload_1
      //   111: instanceof 108
      //   114: ifeq +8 -> 122
      //   117: aload_1
      //   118: checkcast 108	java/lang/RuntimeException
      //   121: athrow
      //   122: aload_1
      //   123: instanceof 110
      //   126: ifeq +8 -> 134
      //   129: aload_1
      //   130: checkcast 110	java/lang/Error
      //   133: athrow
      //   134: new 112	java/lang/IllegalStateException
      //   137: dup
      //   138: aload_1
      //   139: invokespecial 113	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
      //   142: athrow
      //   143: astore_1
      //   144: aload_0
      //   145: iconst_1
      //   146: invokevirtual 89	android/accounts/AccountManager$AmsTask:cancel	(Z)Z
      //   149: pop
      //   150: new 60	android/accounts/OperationCanceledException
      //   153: dup
      //   154: invokespecial 116	android/accounts/OperationCanceledException:<init>	()V
      //   157: athrow
      //   158: astore_1
      //   159: aload_0
      //   160: iconst_1
      //   161: invokevirtual 89	android/accounts/AccountManager$AmsTask:cancel	(Z)Z
      //   164: pop
      //   165: goto -15 -> 150
      //   168: astore_1
      //   169: new 60	android/accounts/OperationCanceledException
      //   172: dup
      //   173: invokespecial 116	android/accounts/OperationCanceledException:<init>	()V
      //   176: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	177	0	this	AmsTask
      //   0	177	1	paramLong	Long
      //   0	177	2	paramTimeUnit	TimeUnit
      // Exception table:
      //   from	to	target	type
      //   18	26	55	java/util/concurrent/ExecutionException
      //   34	47	55	java/util/concurrent/ExecutionException
      //   18	26	73	finally
      //   34	47	73	finally
      //   56	73	73	finally
      //   82	98	73	finally
      //   98	110	73	finally
      //   110	122	73	finally
      //   122	134	73	finally
      //   134	143	73	finally
      //   169	177	73	finally
      //   18	26	143	java/lang/InterruptedException
      //   34	47	143	java/lang/InterruptedException
      //   18	26	158	java/util/concurrent/TimeoutException
      //   34	47	158	java/util/concurrent/TimeoutException
      //   18	26	168	java/util/concurrent/CancellationException
      //   34	47	168	java/util/concurrent/CancellationException
    }
    
    public abstract void doWork()
      throws RemoteException;
    
    protected void done()
    {
      if (this.mCallback != null) {
        AccountManager.-wrap2(AccountManager.this, this.mHandler, this.mCallback, this);
      }
    }
    
    public Bundle getResult()
      throws OperationCanceledException, IOException, AuthenticatorException
    {
      return internalGetResult(null, null);
    }
    
    public Bundle getResult(long paramLong, TimeUnit paramTimeUnit)
      throws OperationCanceledException, IOException, AuthenticatorException
    {
      return internalGetResult(Long.valueOf(paramLong), paramTimeUnit);
    }
    
    protected void set(Bundle paramBundle)
    {
      if (paramBundle == null) {
        Log.e("AccountManager", "the bundle must not be null", new Exception());
      }
      super.set(paramBundle);
    }
    
    public final AccountManagerFuture<Bundle> start()
    {
      try
      {
        doWork();
        return this;
      }
      catch (RemoteException localRemoteException)
      {
        setException(localRemoteException);
      }
      return this;
    }
    
    private class Response
      extends IAccountManagerResponse.Stub
    {
      private Response() {}
      
      public void onError(int paramInt, String paramString)
      {
        if ((paramInt == 4) || (paramInt == 100)) {}
        while (paramInt == 101)
        {
          AccountManager.AmsTask.this.cancel(true);
          return;
        }
        AccountManager.AmsTask.-wrap0(AccountManager.AmsTask.this, AccountManager.-wrap0(AccountManager.this, paramInt, paramString));
      }
      
      public void onResult(Bundle paramBundle)
      {
        Intent localIntent = (Intent)paramBundle.getParcelable("intent");
        if ((localIntent != null) && (AccountManager.AmsTask.this.mActivity != null))
        {
          AccountManager.AmsTask.this.mActivity.startActivity(localIntent);
          return;
        }
        if (paramBundle.getBoolean("retry")) {
          try
          {
            AccountManager.AmsTask.this.doWork();
            return;
          }
          catch (RemoteException paramBundle)
          {
            throw paramBundle.rethrowFromSystemServer();
          }
        }
        AccountManager.AmsTask.this.set(paramBundle);
      }
    }
  }
  
  private abstract class BaseFutureTask<T>
    extends FutureTask<T>
  {
    final Handler mHandler;
    public final IAccountManagerResponse mResponse;
    
    public BaseFutureTask(Handler paramHandler)
    {
      super()
      {
        public T call()
          throws Exception
        {
          throw new IllegalStateException("this should never be called");
        }
      };
      this.mHandler = paramHandler;
      this.mResponse = new Response();
    }
    
    public abstract T bundleToResult(Bundle paramBundle)
      throws AuthenticatorException;
    
    public abstract void doWork()
      throws RemoteException;
    
    protected void postRunnableToHandler(Runnable paramRunnable)
    {
      if (this.mHandler == null) {}
      for (Handler localHandler = AccountManager.-get2(AccountManager.this);; localHandler = this.mHandler)
      {
        localHandler.post(paramRunnable);
        return;
      }
    }
    
    protected void startTask()
    {
      try
      {
        doWork();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        setException(localRemoteException);
      }
    }
    
    protected class Response
      extends IAccountManagerResponse.Stub
    {
      protected Response() {}
      
      public void onError(int paramInt, String paramString)
      {
        if ((paramInt == 4) || (paramInt == 100)) {}
        while (paramInt == 101)
        {
          AccountManager.BaseFutureTask.this.cancel(true);
          return;
        }
        AccountManager.BaseFutureTask.-wrap0(AccountManager.BaseFutureTask.this, AccountManager.-wrap0(AccountManager.this, paramInt, paramString));
      }
      
      public void onResult(Bundle paramBundle)
      {
        try
        {
          paramBundle = AccountManager.BaseFutureTask.this.bundleToResult(paramBundle);
          if (paramBundle == null) {
            return;
          }
          AccountManager.BaseFutureTask.-wrap1(AccountManager.BaseFutureTask.this, paramBundle);
          return;
        }
        catch (ClassCastException paramBundle)
        {
          onError(5, "no result in response");
          return;
        }
        catch (AuthenticatorException paramBundle)
        {
          for (;;) {}
        }
      }
    }
  }
  
  private abstract class Future2Task<T>
    extends AccountManager.BaseFutureTask<T>
    implements AccountManagerFuture<T>
  {
    final AccountManagerCallback<T> mCallback;
    
    public Future2Task(AccountManagerCallback<T> paramAccountManagerCallback)
    {
      super(paramAccountManagerCallback);
      AccountManagerCallback localAccountManagerCallback;
      this.mCallback = localAccountManagerCallback;
    }
    
    /* Error */
    private T internalGetResult(Long paramLong, TimeUnit paramTimeUnit)
      throws OperationCanceledException, IOException, AuthenticatorException
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 49	android/accounts/AccountManager$Future2Task:isDone	()Z
      //   4: ifne +10 -> 14
      //   7: aload_0
      //   8: getfield 21	android/accounts/AccountManager$Future2Task:this$0	Landroid/accounts/AccountManager;
      //   11: invokestatic 53	android/accounts/AccountManager:-wrap1	(Landroid/accounts/AccountManager;)V
      //   14: aload_1
      //   15: ifnonnull +16 -> 31
      //   18: aload_0
      //   19: invokevirtual 57	android/accounts/AccountManager$Future2Task:get	()Ljava/lang/Object;
      //   22: astore_1
      //   23: aload_0
      //   24: iconst_1
      //   25: invokevirtual 61	android/accounts/AccountManager$Future2Task:cancel	(Z)Z
      //   28: pop
      //   29: aload_1
      //   30: areturn
      //   31: aload_0
      //   32: aload_1
      //   33: invokevirtual 67	java/lang/Long:longValue	()J
      //   36: aload_2
      //   37: invokevirtual 70	android/accounts/AccountManager$Future2Task:get	(JLjava/util/concurrent/TimeUnit;)Ljava/lang/Object;
      //   40: astore_1
      //   41: aload_0
      //   42: iconst_1
      //   43: invokevirtual 61	android/accounts/AccountManager$Future2Task:cancel	(Z)Z
      //   46: pop
      //   47: aload_1
      //   48: areturn
      //   49: astore_1
      //   50: aload_1
      //   51: invokevirtual 74	java/util/concurrent/ExecutionException:getCause	()Ljava/lang/Throwable;
      //   54: astore_1
      //   55: aload_1
      //   56: instanceof 35
      //   59: ifeq +17 -> 76
      //   62: aload_1
      //   63: checkcast 35	java/io/IOException
      //   66: athrow
      //   67: astore_1
      //   68: aload_0
      //   69: iconst_1
      //   70: invokevirtual 61	android/accounts/AccountManager$Future2Task:cancel	(Z)Z
      //   73: pop
      //   74: aload_1
      //   75: athrow
      //   76: aload_1
      //   77: instanceof 76
      //   80: ifeq +12 -> 92
      //   83: new 37	android/accounts/AuthenticatorException
      //   86: dup
      //   87: aload_1
      //   88: invokespecial 79	android/accounts/AuthenticatorException:<init>	(Ljava/lang/Throwable;)V
      //   91: athrow
      //   92: aload_1
      //   93: instanceof 37
      //   96: ifeq +8 -> 104
      //   99: aload_1
      //   100: checkcast 37	android/accounts/AuthenticatorException
      //   103: athrow
      //   104: aload_1
      //   105: instanceof 81
      //   108: ifeq +8 -> 116
      //   111: aload_1
      //   112: checkcast 81	java/lang/RuntimeException
      //   115: athrow
      //   116: aload_1
      //   117: instanceof 83
      //   120: ifeq +8 -> 128
      //   123: aload_1
      //   124: checkcast 83	java/lang/Error
      //   127: athrow
      //   128: new 85	java/lang/IllegalStateException
      //   131: dup
      //   132: aload_1
      //   133: invokespecial 86	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
      //   136: athrow
      //   137: astore_1
      //   138: aload_0
      //   139: iconst_1
      //   140: invokevirtual 61	android/accounts/AccountManager$Future2Task:cancel	(Z)Z
      //   143: pop
      //   144: new 33	android/accounts/OperationCanceledException
      //   147: dup
      //   148: invokespecial 89	android/accounts/OperationCanceledException:<init>	()V
      //   151: athrow
      //   152: astore_1
      //   153: aload_0
      //   154: iconst_1
      //   155: invokevirtual 61	android/accounts/AccountManager$Future2Task:cancel	(Z)Z
      //   158: pop
      //   159: goto -15 -> 144
      //   162: astore_1
      //   163: aload_0
      //   164: iconst_1
      //   165: invokevirtual 61	android/accounts/AccountManager$Future2Task:cancel	(Z)Z
      //   168: pop
      //   169: goto -25 -> 144
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	172	0	this	Future2Task
      //   0	172	1	paramLong	Long
      //   0	172	2	paramTimeUnit	TimeUnit
      // Exception table:
      //   from	to	target	type
      //   18	23	49	java/util/concurrent/ExecutionException
      //   31	41	49	java/util/concurrent/ExecutionException
      //   18	23	67	finally
      //   31	41	67	finally
      //   50	67	67	finally
      //   76	92	67	finally
      //   92	104	67	finally
      //   104	116	67	finally
      //   116	128	67	finally
      //   128	137	67	finally
      //   18	23	137	java/util/concurrent/CancellationException
      //   31	41	137	java/util/concurrent/CancellationException
      //   18	23	152	java/util/concurrent/TimeoutException
      //   31	41	152	java/util/concurrent/TimeoutException
      //   18	23	162	java/lang/InterruptedException
      //   31	41	162	java/lang/InterruptedException
    }
    
    protected void done()
    {
      if (this.mCallback != null) {
        postRunnableToHandler(new Runnable()
        {
          public void run()
          {
            AccountManager.Future2Task.this.mCallback.run(AccountManager.Future2Task.this);
          }
        });
      }
    }
    
    public T getResult()
      throws OperationCanceledException, IOException, AuthenticatorException
    {
      return (T)internalGetResult(null, null);
    }
    
    public T getResult(long paramLong, TimeUnit paramTimeUnit)
      throws OperationCanceledException, IOException, AuthenticatorException
    {
      return (T)internalGetResult(Long.valueOf(paramLong), paramTimeUnit);
    }
    
    public Future2Task<T> start()
    {
      startTask();
      return this;
    }
  }
  
  private class GetAuthTokenByTypeAndFeaturesTask
    extends AccountManager.AmsTask
    implements AccountManagerCallback<Bundle>
  {
    final String mAccountType;
    final Bundle mAddAccountOptions;
    final String mAuthTokenType;
    final String[] mFeatures;
    volatile AccountManagerFuture<Bundle> mFuture = null;
    final Bundle mLoginOptions;
    final AccountManagerCallback<Bundle> mMyCallback;
    private volatile int mNumAccounts = 0;
    
    GetAuthTokenByTypeAndFeaturesTask(String paramString, String[] paramArrayOfString, Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2, AccountManagerCallback<Bundle> paramAccountManagerCallback, Handler paramHandler)
    {
      super(paramBundle1, localHandler, paramHandler);
      if (paramString == null) {
        throw new IllegalArgumentException("account type is null");
      }
      this.mAccountType = paramString;
      this.mAuthTokenType = paramArrayOfString;
      this.mFeatures = paramActivity;
      this.mAddAccountOptions = paramBundle2;
      this.mLoginOptions = paramAccountManagerCallback;
      this.mMyCallback = this;
    }
    
    public void doWork()
      throws RemoteException
    {
      SeempLog.record(31);
      AccountManager.this.getAccountsByTypeAndFeatures(this.mAccountType, this.mFeatures, new AccountManagerCallback()
      {
        public void run(AccountManagerFuture<Account[]> paramAnonymousAccountManagerFuture)
        {
          try
          {
            paramAnonymousAccountManagerFuture = (Account[])paramAnonymousAccountManagerFuture.getResult();
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.-set0(AccountManager.GetAuthTokenByTypeAndFeaturesTask.this, paramAnonymousAccountManagerFuture.length);
            if (paramAnonymousAccountManagerFuture.length != 0) {
              break label184;
            }
            if (AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mActivity != null)
            {
              AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mFuture = AccountManager.this.addAccount(AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mAccountType, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mFeatures, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mAddAccountOptions, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mActivity, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
              return;
            }
          }
          catch (AuthenticatorException paramAnonymousAccountManagerFuture)
          {
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.-wrap0(AccountManager.GetAuthTokenByTypeAndFeaturesTask.this, paramAnonymousAccountManagerFuture);
            return;
          }
          catch (IOException paramAnonymousAccountManagerFuture)
          {
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.-wrap0(AccountManager.GetAuthTokenByTypeAndFeaturesTask.this, paramAnonymousAccountManagerFuture);
            return;
          }
          catch (OperationCanceledException paramAnonymousAccountManagerFuture)
          {
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.-wrap0(AccountManager.GetAuthTokenByTypeAndFeaturesTask.this, paramAnonymousAccountManagerFuture);
            return;
          }
          paramAnonymousAccountManagerFuture = new Bundle();
          paramAnonymousAccountManagerFuture.putString("authAccount", null);
          paramAnonymousAccountManagerFuture.putString("accountType", null);
          paramAnonymousAccountManagerFuture.putString("authtoken", null);
          paramAnonymousAccountManagerFuture.putBinder("accountAccessId", null);
          try
          {
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mResponse.onResult(paramAnonymousAccountManagerFuture);
            return;
          }
          catch (RemoteException paramAnonymousAccountManagerFuture)
          {
            return;
          }
          label184:
          if (paramAnonymousAccountManagerFuture.length == 1)
          {
            if (AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mActivity == null)
            {
              AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mFuture = AccountManager.this.getAuthToken(paramAnonymousAccountManagerFuture[0], AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, false, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
              return;
            }
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mFuture = AccountManager.this.getAuthToken(paramAnonymousAccountManagerFuture[0], AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mLoginOptions, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mActivity, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
            return;
          }
          if (AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mActivity != null)
          {
            IAccountManagerResponse.Stub local1 = new IAccountManagerResponse.Stub()
            {
              public void onError(int paramAnonymous2Int, String paramAnonymous2String)
                throws RemoteException
              {
                AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mResponse.onError(paramAnonymous2Int, paramAnonymous2String);
              }
              
              public void onResult(Bundle paramAnonymous2Bundle)
                throws RemoteException
              {
                paramAnonymous2Bundle = new Account(paramAnonymous2Bundle.getString("authAccount"), paramAnonymous2Bundle.getString("accountType"), paramAnonymous2Bundle.getString("accountAccessId"));
                AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mFuture = AccountManager.this.getAuthToken(paramAnonymous2Bundle, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mAuthTokenType, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mLoginOptions, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mActivity, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mMyCallback, AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mHandler);
              }
            };
            Intent localIntent = new Intent();
            ComponentName localComponentName = ComponentName.unflattenFromString(Resources.getSystem().getString(17039455));
            localIntent.setClassName(localComponentName.getPackageName(), localComponentName.getClassName());
            localIntent.putExtra("accounts", paramAnonymousAccountManagerFuture);
            localIntent.putExtra("accountManagerResponse", new AccountManagerResponse(local1));
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mActivity.startActivity(localIntent);
            return;
          }
          paramAnonymousAccountManagerFuture = new Bundle();
          paramAnonymousAccountManagerFuture.putString("accounts", null);
          try
          {
            AccountManager.GetAuthTokenByTypeAndFeaturesTask.this.mResponse.onResult(paramAnonymousAccountManagerFuture);
            return;
          }
          catch (RemoteException paramAnonymousAccountManagerFuture) {}
        }
      }, this.mHandler);
    }
    
    public void run(AccountManagerFuture<Bundle> paramAccountManagerFuture)
    {
      try
      {
        paramAccountManagerFuture = (Bundle)paramAccountManagerFuture.getResult();
        if (this.mNumAccounts == 0)
        {
          String str1 = paramAccountManagerFuture.getString("authAccount");
          String str2 = paramAccountManagerFuture.getString("accountType");
          if ((TextUtils.isEmpty(str1)) || (TextUtils.isEmpty(str2)))
          {
            setException(new AuthenticatorException("account not in result"));
            return;
          }
          paramAccountManagerFuture = new Account(str1, str2, paramAccountManagerFuture.getString("accountAccessId"));
          this.mNumAccounts = 1;
          AccountManager.this.getAuthToken(paramAccountManagerFuture, this.mAuthTokenType, null, this.mActivity, this.mMyCallback, this.mHandler);
          return;
        }
        set(paramAccountManagerFuture);
        return;
      }
      catch (AuthenticatorException paramAccountManagerFuture)
      {
        setException(paramAccountManagerFuture);
        return;
      }
      catch (IOException paramAccountManagerFuture)
      {
        setException(paramAccountManagerFuture);
        return;
      }
      catch (OperationCanceledException paramAccountManagerFuture)
      {
        cancel(true);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AccountManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */