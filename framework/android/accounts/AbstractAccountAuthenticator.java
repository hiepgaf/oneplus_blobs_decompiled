package android.accounts;

import android.content.Context;
import android.os.BaseBundle;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import java.util.Arrays;

public abstract class AbstractAccountAuthenticator
{
  private static final String KEY_ACCOUNT = "android.accounts.AbstractAccountAuthenticator.KEY_ACCOUNT";
  private static final String KEY_AUTH_TOKEN_TYPE = "android.accounts.AbstractAccountAuthenticato.KEY_AUTH_TOKEN_TYPE";
  public static final String KEY_CUSTOM_TOKEN_EXPIRY = "android.accounts.expiry";
  private static final String KEY_OPTIONS = "android.accounts.AbstractAccountAuthenticator.KEY_OPTIONS";
  private static final String KEY_REQUIRED_FEATURES = "android.accounts.AbstractAccountAuthenticator.KEY_REQUIRED_FEATURES";
  private static final String TAG = "AccountAuthenticator";
  private final Context mContext;
  private Transport mTransport = new Transport(null);
  
  public AbstractAccountAuthenticator(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void checkBinderPermission()
  {
    int i = Binder.getCallingUid();
    if (this.mContext.checkCallingOrSelfPermission("android.permission.ACCOUNT_MANAGER") != 0) {
      throw new SecurityException("caller uid " + i + " lacks " + "android.permission.ACCOUNT_MANAGER");
    }
  }
  
  private void handleException(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString1, String paramString2, Exception paramException)
    throws RemoteException
  {
    if ((paramException instanceof NetworkErrorException))
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", paramString1 + "(" + paramString2 + ")", paramException);
      }
      paramIAccountAuthenticatorResponse.onError(3, paramException.getMessage());
      return;
    }
    if ((paramException instanceof UnsupportedOperationException))
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", paramString1 + "(" + paramString2 + ")", paramException);
      }
      paramIAccountAuthenticatorResponse.onError(6, paramString1 + " not supported");
      return;
    }
    if ((paramException instanceof IllegalArgumentException))
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", paramString1 + "(" + paramString2 + ")", paramException);
      }
      paramIAccountAuthenticatorResponse.onError(7, paramString1 + " not supported");
      return;
    }
    Log.w("AccountAuthenticator", paramString1 + "(" + paramString2 + ")", paramException);
    paramIAccountAuthenticatorResponse.onError(1, paramString1 + " failed");
  }
  
  public abstract Bundle addAccount(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
    throws NetworkErrorException;
  
  public Bundle addAccountFromCredentials(final AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
    throws NetworkErrorException
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        Bundle localBundle = new Bundle();
        localBundle.putBoolean("booleanResult", false);
        paramAccountAuthenticatorResponse.onResult(localBundle);
      }
    }).start();
    return null;
  }
  
  public abstract Bundle confirmCredentials(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
    throws NetworkErrorException;
  
  public abstract Bundle editProperties(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString);
  
  public Bundle finishSession(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString, Bundle paramBundle)
    throws NetworkErrorException
  {
    if (TextUtils.isEmpty(paramString))
    {
      Log.e("AccountAuthenticator", "Account type cannot be empty.");
      paramAccountAuthenticatorResponse = new Bundle();
      paramAccountAuthenticatorResponse.putInt("errorCode", 7);
      paramAccountAuthenticatorResponse.putString("errorMessage", "accountType cannot be empty.");
      return paramAccountAuthenticatorResponse;
    }
    if (paramBundle == null)
    {
      Log.e("AccountAuthenticator", "Session bundle cannot be null.");
      paramAccountAuthenticatorResponse = new Bundle();
      paramAccountAuthenticatorResponse.putInt("errorCode", 7);
      paramAccountAuthenticatorResponse.putString("errorMessage", "sessionBundle cannot be null.");
      return paramAccountAuthenticatorResponse;
    }
    if (!paramBundle.containsKey("android.accounts.AbstractAccountAuthenticato.KEY_AUTH_TOKEN_TYPE"))
    {
      paramString = new Bundle();
      paramString.putInt("errorCode", 6);
      paramString.putString("errorMessage", "Authenticator must override finishSession if startAddAccountSession or startUpdateCredentialsSession is overridden.");
      paramAccountAuthenticatorResponse.onResult(paramString);
      return paramString;
    }
    String str = paramBundle.getString("android.accounts.AbstractAccountAuthenticato.KEY_AUTH_TOKEN_TYPE");
    Bundle localBundle1 = paramBundle.getBundle("android.accounts.AbstractAccountAuthenticator.KEY_OPTIONS");
    String[] arrayOfString = paramBundle.getStringArray("android.accounts.AbstractAccountAuthenticator.KEY_REQUIRED_FEATURES");
    Account localAccount = (Account)paramBundle.getParcelable("android.accounts.AbstractAccountAuthenticator.KEY_ACCOUNT");
    boolean bool = paramBundle.containsKey("android.accounts.AbstractAccountAuthenticator.KEY_ACCOUNT");
    Bundle localBundle2 = new Bundle(paramBundle);
    localBundle2.remove("android.accounts.AbstractAccountAuthenticato.KEY_AUTH_TOKEN_TYPE");
    localBundle2.remove("android.accounts.AbstractAccountAuthenticator.KEY_REQUIRED_FEATURES");
    localBundle2.remove("android.accounts.AbstractAccountAuthenticator.KEY_OPTIONS");
    localBundle2.remove("android.accounts.AbstractAccountAuthenticator.KEY_ACCOUNT");
    paramBundle = localBundle2;
    if (localBundle1 != null)
    {
      localBundle1.putAll(localBundle2);
      paramBundle = localBundle1;
    }
    if (bool) {
      return updateCredentials(paramAccountAuthenticatorResponse, localAccount, str, localBundle1);
    }
    return addAccount(paramAccountAuthenticatorResponse, paramString, str, arrayOfString, paramBundle);
  }
  
  public Bundle getAccountCredentialsForCloning(final AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount)
    throws NetworkErrorException
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        Bundle localBundle = new Bundle();
        localBundle.putBoolean("booleanResult", false);
        paramAccountAuthenticatorResponse.onResult(localBundle);
      }
    }).start();
    return null;
  }
  
  public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount)
    throws NetworkErrorException
  {
    paramAccountAuthenticatorResponse = new Bundle();
    paramAccountAuthenticatorResponse.putBoolean("booleanResult", true);
    return paramAccountAuthenticatorResponse;
  }
  
  public abstract Bundle getAuthToken(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
    throws NetworkErrorException;
  
  public abstract String getAuthTokenLabel(String paramString);
  
  public final IBinder getIBinder()
  {
    return this.mTransport.asBinder();
  }
  
  public abstract Bundle hasFeatures(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String[] paramArrayOfString)
    throws NetworkErrorException;
  
  public Bundle isCredentialsUpdateSuggested(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String paramString)
    throws NetworkErrorException
  {
    paramAccountAuthenticatorResponse = new Bundle();
    paramAccountAuthenticatorResponse.putBoolean("booleanResult", false);
    return paramAccountAuthenticatorResponse;
  }
  
  public Bundle startAddAccountSession(final AccountAuthenticatorResponse paramAccountAuthenticatorResponse, String paramString1, final String paramString2, final String[] paramArrayOfString, final Bundle paramBundle)
    throws NetworkErrorException
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        Bundle localBundle1 = new Bundle();
        localBundle1.putString("android.accounts.AbstractAccountAuthenticato.KEY_AUTH_TOKEN_TYPE", paramString2);
        localBundle1.putStringArray("android.accounts.AbstractAccountAuthenticator.KEY_REQUIRED_FEATURES", paramArrayOfString);
        localBundle1.putBundle("android.accounts.AbstractAccountAuthenticator.KEY_OPTIONS", paramBundle);
        Bundle localBundle2 = new Bundle();
        localBundle2.putBundle("accountSessionBundle", localBundle1);
        paramAccountAuthenticatorResponse.onResult(localBundle2);
      }
    }).start();
    return null;
  }
  
  public Bundle startUpdateCredentialsSession(final AccountAuthenticatorResponse paramAccountAuthenticatorResponse, final Account paramAccount, final String paramString, final Bundle paramBundle)
    throws NetworkErrorException
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        Bundle localBundle1 = new Bundle();
        localBundle1.putString("android.accounts.AbstractAccountAuthenticato.KEY_AUTH_TOKEN_TYPE", paramString);
        localBundle1.putParcelable("android.accounts.AbstractAccountAuthenticator.KEY_ACCOUNT", paramAccount);
        localBundle1.putBundle("android.accounts.AbstractAccountAuthenticator.KEY_OPTIONS", paramBundle);
        Bundle localBundle2 = new Bundle();
        localBundle2.putBundle("accountSessionBundle", localBundle1);
        paramAccountAuthenticatorResponse.onResult(localBundle2);
      }
    }).start();
    return null;
  }
  
  public abstract Bundle updateCredentials(AccountAuthenticatorResponse paramAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
    throws NetworkErrorException;
  
  private class Transport
    extends IAccountAuthenticator.Stub
  {
    private Transport() {}
    
    public void addAccount(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
      throws RemoteException
    {
      StringBuilder localStringBuilder;
      if (Log.isLoggable("AccountAuthenticator", 2))
      {
        localStringBuilder = new StringBuilder().append("addAccount: accountType ").append(paramString1).append(", authTokenType ").append(paramString2).append(", features ");
        if (paramArrayOfString != null) {
          break label153;
        }
      }
      for (String str = "[]";; str = Arrays.toString(paramArrayOfString))
      {
        Log.v("AccountAuthenticator", str);
        AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
        try
        {
          paramString2 = AbstractAccountAuthenticator.this.addAccount(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramString1, paramString2, paramArrayOfString, paramBundle);
          if (Log.isLoggable("AccountAuthenticator", 2))
          {
            if (paramString2 != null) {
              paramString2.keySet();
            }
            Log.v("AccountAuthenticator", "addAccount: result " + AccountManager.sanitizeResult(paramString2));
          }
          if (paramString2 != null) {
            paramIAccountAuthenticatorResponse.onResult(paramString2);
          }
          return;
        }
        catch (Exception paramString2)
        {
          label153:
          AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "addAccount", paramString1, paramString2);
        }
      }
    }
    
    public void addAccountFromCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
      throws RemoteException
    {
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramBundle = AbstractAccountAuthenticator.this.addAccountFromCredentials(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount, paramBundle);
        if (paramBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(paramBundle);
        }
        return;
      }
      catch (Exception paramBundle)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "addAccountFromCredentials", paramAccount.toString(), paramBundle);
      }
    }
    
    public void confirmCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
      throws RemoteException
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", "confirmCredentials: " + paramAccount);
      }
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramBundle = AbstractAccountAuthenticator.this.confirmCredentials(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount, paramBundle);
        if (Log.isLoggable("AccountAuthenticator", 2))
        {
          if (paramBundle != null) {
            paramBundle.keySet();
          }
          Log.v("AccountAuthenticator", "confirmCredentials: result " + AccountManager.sanitizeResult(paramBundle));
        }
        if (paramBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(paramBundle);
        }
        return;
      }
      catch (Exception paramBundle)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "confirmCredentials", paramAccount.toString(), paramBundle);
      }
    }
    
    public void editProperties(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString)
      throws RemoteException
    {
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        Bundle localBundle = AbstractAccountAuthenticator.this.editProperties(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramString);
        if (localBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(localBundle);
        }
        return;
      }
      catch (Exception localException)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "editProperties", paramString, localException);
      }
    }
    
    public void finishSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString, Bundle paramBundle)
      throws RemoteException
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", "finishSession: accountType " + paramString);
      }
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramBundle = AbstractAccountAuthenticator.this.finishSession(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramString, paramBundle);
        if (paramBundle != null) {
          paramBundle.keySet();
        }
        if (Log.isLoggable("AccountAuthenticator", 2)) {
          Log.v("AccountAuthenticator", "finishSession: result " + AccountManager.sanitizeResult(paramBundle));
        }
        if (paramBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(paramBundle);
        }
        return;
      }
      catch (Exception paramBundle)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "finishSession", paramString, paramBundle);
      }
    }
    
    public void getAccountCredentialsForCloning(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount)
      throws RemoteException
    {
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        Bundle localBundle = AbstractAccountAuthenticator.this.getAccountCredentialsForCloning(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount);
        if (localBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(localBundle);
        }
        return;
      }
      catch (Exception localException)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "getAccountCredentialsForCloning", paramAccount.toString(), localException);
      }
    }
    
    public void getAccountRemovalAllowed(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount)
      throws RemoteException
    {
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        Bundle localBundle = AbstractAccountAuthenticator.this.getAccountRemovalAllowed(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount);
        if (localBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(localBundle);
        }
        return;
      }
      catch (Exception localException)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "getAccountRemovalAllowed", paramAccount.toString(), localException);
      }
    }
    
    public void getAuthToken(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
      throws RemoteException
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", "getAuthToken: " + paramAccount + ", authTokenType " + paramString);
      }
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramBundle = AbstractAccountAuthenticator.this.getAuthToken(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount, paramString, paramBundle);
        if (Log.isLoggable("AccountAuthenticator", 2))
        {
          if (paramBundle != null) {
            paramBundle.keySet();
          }
          Log.v("AccountAuthenticator", "getAuthToken: result " + AccountManager.sanitizeResult(paramBundle));
        }
        if (paramBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(paramBundle);
        }
        return;
      }
      catch (Exception paramBundle)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "getAuthToken", paramAccount.toString() + "," + paramString, paramBundle);
      }
    }
    
    public void getAuthTokenLabel(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString)
      throws RemoteException
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", "getAuthTokenLabel: authTokenType " + paramString);
      }
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        Bundle localBundle = new Bundle();
        localBundle.putString("authTokenLabelKey", AbstractAccountAuthenticator.this.getAuthTokenLabel(paramString));
        if (Log.isLoggable("AccountAuthenticator", 2))
        {
          if (localBundle != null) {
            localBundle.keySet();
          }
          Log.v("AccountAuthenticator", "getAuthTokenLabel: result " + AccountManager.sanitizeResult(localBundle));
        }
        paramIAccountAuthenticatorResponse.onResult(localBundle);
        return;
      }
      catch (Exception localException)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "getAuthTokenLabel", paramString, localException);
      }
    }
    
    public void hasFeatures(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String[] paramArrayOfString)
      throws RemoteException
    {
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramArrayOfString = AbstractAccountAuthenticator.this.hasFeatures(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount, paramArrayOfString);
        if (paramArrayOfString != null) {
          paramIAccountAuthenticatorResponse.onResult(paramArrayOfString);
        }
        return;
      }
      catch (Exception paramArrayOfString)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "hasFeatures", paramAccount.toString(), paramArrayOfString);
      }
    }
    
    public void isCredentialsUpdateSuggested(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString)
      throws RemoteException
    {
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramString = AbstractAccountAuthenticator.this.isCredentialsUpdateSuggested(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount, paramString);
        if (paramString != null) {
          paramIAccountAuthenticatorResponse.onResult(paramString);
        }
        return;
      }
      catch (Exception paramString)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "isCredentialsUpdateSuggested", paramAccount.toString(), paramString);
      }
    }
    
    public void startAddAccountSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
      throws RemoteException
    {
      StringBuilder localStringBuilder;
      if (Log.isLoggable("AccountAuthenticator", 2))
      {
        localStringBuilder = new StringBuilder().append("startAddAccountSession: accountType ").append(paramString1).append(", authTokenType ").append(paramString2).append(", features ");
        if (paramArrayOfString != null) {
          break label153;
        }
      }
      for (String str = "[]";; str = Arrays.toString(paramArrayOfString))
      {
        Log.v("AccountAuthenticator", str);
        AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
        try
        {
          paramString2 = AbstractAccountAuthenticator.this.startAddAccountSession(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramString1, paramString2, paramArrayOfString, paramBundle);
          if (Log.isLoggable("AccountAuthenticator", 2))
          {
            if (paramString2 != null) {
              paramString2.keySet();
            }
            Log.v("AccountAuthenticator", "startAddAccountSession: result " + AccountManager.sanitizeResult(paramString2));
          }
          if (paramString2 != null) {
            paramIAccountAuthenticatorResponse.onResult(paramString2);
          }
          return;
        }
        catch (Exception paramString2)
        {
          label153:
          AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "startAddAccountSession", paramString1, paramString2);
        }
      }
    }
    
    public void startUpdateCredentialsSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
      throws RemoteException
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", "startUpdateCredentialsSession: " + paramAccount + ", authTokenType " + paramString);
      }
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramBundle = AbstractAccountAuthenticator.this.startUpdateCredentialsSession(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount, paramString, paramBundle);
        if (Log.isLoggable("AccountAuthenticator", 2))
        {
          if (paramBundle != null) {
            paramBundle.keySet();
          }
          Log.v("AccountAuthenticator", "startUpdateCredentialsSession: result " + AccountManager.sanitizeResult(paramBundle));
        }
        if (paramBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(paramBundle);
        }
        return;
      }
      catch (Exception paramBundle)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "startUpdateCredentialsSession", paramAccount.toString() + "," + paramString, paramBundle);
      }
    }
    
    public void updateCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
      throws RemoteException
    {
      if (Log.isLoggable("AccountAuthenticator", 2)) {
        Log.v("AccountAuthenticator", "updateCredentials: " + paramAccount + ", authTokenType " + paramString);
      }
      AbstractAccountAuthenticator.-wrap0(AbstractAccountAuthenticator.this);
      try
      {
        paramBundle = AbstractAccountAuthenticator.this.updateCredentials(new AccountAuthenticatorResponse(paramIAccountAuthenticatorResponse), paramAccount, paramString, paramBundle);
        if (Log.isLoggable("AccountAuthenticator", 2))
        {
          if (paramBundle != null) {
            paramBundle.keySet();
          }
          Log.v("AccountAuthenticator", "updateCredentials: result " + AccountManager.sanitizeResult(paramBundle));
        }
        if (paramBundle != null) {
          paramIAccountAuthenticatorResponse.onResult(paramBundle);
        }
        return;
      }
      catch (Exception paramBundle)
      {
        AbstractAccountAuthenticator.-wrap1(AbstractAccountAuthenticator.this, paramIAccountAuthenticatorResponse, "updateCredentials", paramAccount.toString() + "," + paramString, paramBundle);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AbstractAccountAuthenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */