package android.accounts;

import android.content.IntentSender;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;

public abstract interface IAccountManager
  extends IInterface
{
  public abstract boolean accountAuthenticated(Account paramAccount)
    throws RemoteException;
  
  public abstract void addAccount(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void addAccountAsUser(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract boolean addAccountExplicitly(Account paramAccount, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void addSharedAccountsFromParentUser(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void clearPassword(Account paramAccount)
    throws RemoteException;
  
  public abstract void confirmCredentialsAsUser(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, Bundle paramBundle, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void copyAccountToUser(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract IntentSender createRequestAccountAccessIntentSenderAsUser(Account paramAccount, String paramString, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void editProperties(IAccountManagerResponse paramIAccountManagerResponse, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void finishSessionAsUser(IAccountManagerResponse paramIAccountManagerResponse, Bundle paramBundle1, boolean paramBoolean, Bundle paramBundle2, int paramInt)
    throws RemoteException;
  
  public abstract Account[] getAccounts(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract Account[] getAccountsAsUser(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract void getAccountsByFeatures(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String[] paramArrayOfString, String paramString2)
    throws RemoteException;
  
  public abstract Account[] getAccountsByTypeForPackage(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract Account[] getAccountsForPackage(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract void getAuthToken(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString, boolean paramBoolean1, boolean paramBoolean2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void getAuthTokenLabel(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract AuthenticatorDescription[] getAuthenticatorTypes(int paramInt)
    throws RemoteException;
  
  public abstract String getPassword(Account paramAccount)
    throws RemoteException;
  
  public abstract String getPreviousName(Account paramAccount)
    throws RemoteException;
  
  public abstract Account[] getSharedAccountsAsUser(int paramInt)
    throws RemoteException;
  
  public abstract String getUserData(Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract boolean hasAccountAccess(Account paramAccount, String paramString, UserHandle paramUserHandle)
    throws RemoteException;
  
  public abstract void hasFeatures(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String[] paramArrayOfString, String paramString)
    throws RemoteException;
  
  public abstract void invalidateAuthToken(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void isCredentialsUpdateSuggested(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract void onAccountAccessed(String paramString)
    throws RemoteException;
  
  public abstract String peekAuthToken(Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract void removeAccount(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void removeAccountAsUser(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract boolean removeAccountExplicitly(Account paramAccount)
    throws RemoteException;
  
  public abstract boolean removeSharedAccountAsUser(Account paramAccount, int paramInt)
    throws RemoteException;
  
  public abstract void renameAccount(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract boolean renameSharedAccountAsUser(Account paramAccount, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setAuthToken(Account paramAccount, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setPassword(Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract void setUserData(Account paramAccount, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract boolean someUserHasAccount(Account paramAccount)
    throws RemoteException;
  
  public abstract void startAddAccountSession(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void startUpdateCredentialsSession(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void updateAppPermission(Account paramAccount, String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void updateCredentials(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAccountManager
  {
    private static final String DESCRIPTOR = "android.accounts.IAccountManager";
    static final int TRANSACTION_accountAuthenticated = 28;
    static final int TRANSACTION_addAccount = 23;
    static final int TRANSACTION_addAccountAsUser = 24;
    static final int TRANSACTION_addAccountExplicitly = 10;
    static final int TRANSACTION_addSharedAccountsFromParentUser = 32;
    static final int TRANSACTION_clearPassword = 19;
    static final int TRANSACTION_confirmCredentialsAsUser = 27;
    static final int TRANSACTION_copyAccountToUser = 14;
    static final int TRANSACTION_createRequestAccountAccessIntentSenderAsUser = 42;
    static final int TRANSACTION_editProperties = 26;
    static final int TRANSACTION_finishSessionAsUser = 38;
    static final int TRANSACTION_getAccounts = 4;
    static final int TRANSACTION_getAccountsAsUser = 7;
    static final int TRANSACTION_getAccountsByFeatures = 9;
    static final int TRANSACTION_getAccountsByTypeForPackage = 6;
    static final int TRANSACTION_getAccountsForPackage = 5;
    static final int TRANSACTION_getAuthToken = 22;
    static final int TRANSACTION_getAuthTokenLabel = 29;
    static final int TRANSACTION_getAuthenticatorTypes = 3;
    static final int TRANSACTION_getPassword = 1;
    static final int TRANSACTION_getPreviousName = 34;
    static final int TRANSACTION_getSharedAccountsAsUser = 30;
    static final int TRANSACTION_getUserData = 2;
    static final int TRANSACTION_hasAccountAccess = 41;
    static final int TRANSACTION_hasFeatures = 8;
    static final int TRANSACTION_invalidateAuthToken = 15;
    static final int TRANSACTION_isCredentialsUpdateSuggested = 40;
    static final int TRANSACTION_onAccountAccessed = 43;
    static final int TRANSACTION_peekAuthToken = 16;
    static final int TRANSACTION_removeAccount = 11;
    static final int TRANSACTION_removeAccountAsUser = 12;
    static final int TRANSACTION_removeAccountExplicitly = 13;
    static final int TRANSACTION_removeSharedAccountAsUser = 31;
    static final int TRANSACTION_renameAccount = 33;
    static final int TRANSACTION_renameSharedAccountAsUser = 35;
    static final int TRANSACTION_setAuthToken = 17;
    static final int TRANSACTION_setPassword = 18;
    static final int TRANSACTION_setUserData = 20;
    static final int TRANSACTION_someUserHasAccount = 39;
    static final int TRANSACTION_startAddAccountSession = 36;
    static final int TRANSACTION_startUpdateCredentialsSession = 37;
    static final int TRANSACTION_updateAppPermission = 21;
    static final int TRANSACTION_updateCredentials = 25;
    
    public Stub()
    {
      attachInterface(this, "android.accounts.IAccountManager");
    }
    
    public static IAccountManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.accounts.IAccountManager");
      if ((localIInterface != null) && ((localIInterface instanceof IAccountManager))) {
        return (IAccountManager)localIInterface;
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
      Object localObject2;
      label800:
      boolean bool1;
      label835:
      label840:
      label913:
      label991:
      label1053:
      label1469:
      Object localObject3;
      label1527:
      label1537:
      label1584:
      label1590:
      label1596:
      Object localObject4;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.accounts.IAccountManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getPassword(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramParcel1 = getUserData((Account)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        paramParcel1 = getAuthenticatorTypes(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        paramParcel1 = getAccounts(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        paramParcel1 = getAccountsForPackage(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        paramParcel1 = getAccountsByTypeForPackage(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        paramParcel1 = getAccountsAsUser(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          hasFeatures((IAccountManagerResponse)localObject2, (Account)localObject1, paramParcel1.createStringArray(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        getAccountsByFeatures(IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.createStringArray(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label835;
          }
          paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          bool1 = addAccountExplicitly((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label840;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label800;
        }
      case 11: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label913;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          removeAccount((IAccountManagerResponse)localObject2, (Account)localObject1, bool1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 12: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label991;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          removeAccountAsUser((IAccountManagerResponse)localObject2, (Account)localObject1, bool1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 13: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          bool1 = removeAccountExplicitly(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label1053;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 14: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          copyAccountToUser((IAccountManagerResponse)localObject2, (Account)localObject1, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        invalidateAuthToken(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramParcel1 = peekAuthToken((Account)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setAuthToken((Account)localObject1, paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 18: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setPassword((Account)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          clearPassword(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setUserData((Account)localObject1, paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 21: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label1469;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          updateAppPermission((Account)localObject1, (String)localObject2, paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 22: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        boolean bool2;
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject3 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1584;
          }
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label1590;
          }
          bool2 = true;
          if (paramParcel1.readInt() == 0) {
            break label1596;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          getAuthToken((IAccountManagerResponse)localObject2, (Account)localObject1, (String)localObject3, bool1, bool2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          bool1 = false;
          break label1527;
          bool2 = false;
          break label1537;
        }
      case 23: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject1 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject2 = paramParcel1.readString();
        localObject3 = paramParcel1.readString();
        localObject4 = paramParcel1.createStringArray();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label1691;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addAccount((IAccountManagerResponse)localObject1, (String)localObject2, (String)localObject3, (String[])localObject4, bool1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 24: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject3 = paramParcel1.readString();
        localObject4 = paramParcel1.readString();
        String[] arrayOfString = paramParcel1.createStringArray();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label1792;
          }
        }
        for (localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          addAccountAsUser((IAccountManagerResponse)localObject2, (String)localObject3, (String)localObject4, arrayOfString, bool1, (Bundle)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 25: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject3 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1895;
          }
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label1901;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          updateCredentials((IAccountManagerResponse)localObject2, (Account)localObject1, (String)localObject3, bool1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          bool1 = false;
          break label1850;
        }
      case 26: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject1 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          editProperties((IAccountManagerResponse)localObject1, (String)localObject2, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 27: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject3 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2054;
          }
          localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2060;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          confirmCredentialsAsUser((IAccountManagerResponse)localObject3, (Account)localObject1, (Bundle)localObject2, bool1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label2016;
        }
      case 28: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          bool1 = accountAuthenticated(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2122;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 29: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        getAuthTokenLabel(IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 30: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        paramParcel1 = getSharedAccountsAsUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 31: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          bool1 = removeSharedAccountAsUser((Account)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2248;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 32: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        addSharedAccountsFromParentUser(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 33: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          renameAccount((IAccountManagerResponse)localObject2, (Account)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 34: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getPreviousName(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 35: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          bool1 = renameSharedAccountAsUser((Account)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2452;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 36: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject1 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject2 = paramParcel1.readString();
        localObject3 = paramParcel1.readString();
        localObject4 = paramParcel1.createStringArray();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label2547;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startAddAccountSession((IAccountManagerResponse)localObject1, (String)localObject2, (String)localObject3, (String[])localObject4, bool1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 37: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject3 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label2649;
          }
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label2655;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startUpdateCredentialsSession((IAccountManagerResponse)localObject2, (Account)localObject1, (String)localObject3, bool1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          bool1 = false;
          break label2604;
        }
      case 38: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject3 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2755;
          }
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label2761;
          }
        }
        for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          finishSessionAsUser((IAccountManagerResponse)localObject3, (Bundle)localObject1, bool1, (Bundle)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          bool1 = false;
          break label2706;
        }
      case 39: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          bool1 = someUserHasAccount(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2823;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 40: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        localObject2 = IAccountManagerResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          isCredentialsUpdateSuggested((IAccountManagerResponse)localObject2, (Account)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 41: 
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label2976;
          }
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          bool1 = hasAccountAccess((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2981;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label2941;
        }
      case 42: 
        label1691:
        label1792:
        label1850:
        label1895:
        label1901:
        label2016:
        label2054:
        label2060:
        label2122:
        label2248:
        label2452:
        label2547:
        label2604:
        label2649:
        label2655:
        label2706:
        label2755:
        label2761:
        label2823:
        label2941:
        label2976:
        label2981:
        paramParcel1.enforceInterface("android.accounts.IAccountManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label3076;
          }
          paramParcel1 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          label3039:
          paramParcel1 = createRequestAccountAccessIntentSenderAsUser((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3081;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          label3076:
          paramParcel1 = null;
          break label3039;
          label3081:
          paramParcel2.writeInt(0);
        }
      }
      paramParcel1.enforceInterface("android.accounts.IAccountManager");
      onAccountAccessed(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IAccountManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public boolean accountAuthenticated(Account paramAccount)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              this.mRemote.transact(28, localParcel1, localParcel2, 0);
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
      public void addAccount(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 7
        //   3: aconst_null
        //   4: astore 8
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 9
        //   11: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   14: astore 10
        //   16: aload 9
        //   18: ldc 32
        //   20: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   23: aload_1
        //   24: ifnull +11 -> 35
        //   27: aload_1
        //   28: invokeinterface 71 1 0
        //   33: astore 8
        //   35: aload 9
        //   37: aload 8
        //   39: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   42: aload 9
        //   44: aload_2
        //   45: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   48: aload 9
        //   50: aload_3
        //   51: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   54: aload 9
        //   56: aload 4
        //   58: invokevirtual 81	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   61: iload 5
        //   63: ifeq +62 -> 125
        //   66: aload 9
        //   68: iload 7
        //   70: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   73: aload 6
        //   75: ifnull +56 -> 131
        //   78: aload 9
        //   80: iconst_1
        //   81: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   84: aload 6
        //   86: aload 9
        //   88: iconst_0
        //   89: invokevirtual 84	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   92: aload_0
        //   93: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   96: bipush 23
        //   98: aload 9
        //   100: aload 10
        //   102: iconst_0
        //   103: invokeinterface 52 5 0
        //   108: pop
        //   109: aload 10
        //   111: invokevirtual 55	android/os/Parcel:readException	()V
        //   114: aload 10
        //   116: invokevirtual 62	android/os/Parcel:recycle	()V
        //   119: aload 9
        //   121: invokevirtual 62	android/os/Parcel:recycle	()V
        //   124: return
        //   125: iconst_0
        //   126: istore 7
        //   128: goto -62 -> 66
        //   131: aload 9
        //   133: iconst_0
        //   134: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   137: goto -45 -> 92
        //   140: astore_1
        //   141: aload 10
        //   143: invokevirtual 62	android/os/Parcel:recycle	()V
        //   146: aload 9
        //   148: invokevirtual 62	android/os/Parcel:recycle	()V
        //   151: aload_1
        //   152: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	153	0	this	Proxy
        //   0	153	1	paramIAccountManagerResponse	IAccountManagerResponse
        //   0	153	2	paramString1	String
        //   0	153	3	paramString2	String
        //   0	153	4	paramArrayOfString	String[]
        //   0	153	5	paramBoolean	boolean
        //   0	153	6	paramBundle	Bundle
        //   1	126	7	i	int
        //   4	34	8	localIBinder	IBinder
        //   9	138	9	localParcel1	Parcel
        //   14	128	10	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   16	23	140	finally
        //   27	35	140	finally
        //   35	61	140	finally
        //   66	73	140	finally
        //   78	92	140	finally
        //   92	114	140	finally
        //   131	137	140	finally
      }
      
      /* Error */
      public void addAccountAsUser(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 8
        //   3: aconst_null
        //   4: astore 9
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 10
        //   11: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   14: astore 11
        //   16: aload 10
        //   18: ldc 32
        //   20: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   23: aload_1
        //   24: ifnull +11 -> 35
        //   27: aload_1
        //   28: invokeinterface 71 1 0
        //   33: astore 9
        //   35: aload 10
        //   37: aload 9
        //   39: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   42: aload 10
        //   44: aload_2
        //   45: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   48: aload 10
        //   50: aload_3
        //   51: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   54: aload 10
        //   56: aload 4
        //   58: invokevirtual 81	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   61: iload 5
        //   63: ifeq +69 -> 132
        //   66: aload 10
        //   68: iload 8
        //   70: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   73: aload 6
        //   75: ifnull +63 -> 138
        //   78: aload 10
        //   80: iconst_1
        //   81: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   84: aload 6
        //   86: aload 10
        //   88: iconst_0
        //   89: invokevirtual 84	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   92: aload 10
        //   94: iload 7
        //   96: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   99: aload_0
        //   100: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   103: bipush 24
        //   105: aload 10
        //   107: aload 11
        //   109: iconst_0
        //   110: invokeinterface 52 5 0
        //   115: pop
        //   116: aload 11
        //   118: invokevirtual 55	android/os/Parcel:readException	()V
        //   121: aload 11
        //   123: invokevirtual 62	android/os/Parcel:recycle	()V
        //   126: aload 10
        //   128: invokevirtual 62	android/os/Parcel:recycle	()V
        //   131: return
        //   132: iconst_0
        //   133: istore 8
        //   135: goto -69 -> 66
        //   138: aload 10
        //   140: iconst_0
        //   141: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   144: goto -52 -> 92
        //   147: astore_1
        //   148: aload 11
        //   150: invokevirtual 62	android/os/Parcel:recycle	()V
        //   153: aload 10
        //   155: invokevirtual 62	android/os/Parcel:recycle	()V
        //   158: aload_1
        //   159: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	160	0	this	Proxy
        //   0	160	1	paramIAccountManagerResponse	IAccountManagerResponse
        //   0	160	2	paramString1	String
        //   0	160	3	paramString2	String
        //   0	160	4	paramArrayOfString	String[]
        //   0	160	5	paramBoolean	boolean
        //   0	160	6	paramBundle	Bundle
        //   0	160	7	paramInt	int
        //   1	133	8	i	int
        //   4	34	9	localIBinder	IBinder
        //   9	145	10	localParcel1	Parcel
        //   14	135	11	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   16	23	147	finally
        //   27	35	147	finally
        //   35	61	147	finally
        //   66	73	147	finally
        //   78	92	147	finally
        //   92	121	147	finally
        //   138	144	147	finally
      }
      
      public boolean addAccountExplicitly(Account paramAccount, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public void addSharedAccountsFromParentUser(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(32, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public void clearPassword(Account paramAccount)
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
        //   26: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 19
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 52 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 55	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 62	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 62	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 62	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 62	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramAccount	Account
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void confirmCredentialsAsUser(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, Bundle paramBundle, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 1;
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramIAccountManagerResponse != null) {
              localIBinder = paramIAccountManagerResponse.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                break label163;
                localParcel1.writeInt(i);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(27, localParcel1, localParcel2, 0);
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
          label163:
          while (!paramBoolean)
          {
            i = 0;
            break;
          }
        }
      }
      
      /* Error */
      public void copyAccountToUser(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 71 1 0
        //   30: astore 5
        //   32: aload 6
        //   34: aload 5
        //   36: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_2
        //   40: ifnull +62 -> 102
        //   43: aload 6
        //   45: iconst_1
        //   46: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   49: aload_2
        //   50: aload 6
        //   52: iconst_0
        //   53: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload 6
        //   58: iload_3
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: aload 6
        //   64: iload 4
        //   66: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   69: aload_0
        //   70: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   73: bipush 14
        //   75: aload 6
        //   77: aload 7
        //   79: iconst_0
        //   80: invokeinterface 52 5 0
        //   85: pop
        //   86: aload 7
        //   88: invokevirtual 55	android/os/Parcel:readException	()V
        //   91: aload 7
        //   93: invokevirtual 62	android/os/Parcel:recycle	()V
        //   96: aload 6
        //   98: invokevirtual 62	android/os/Parcel:recycle	()V
        //   101: return
        //   102: aload 6
        //   104: iconst_0
        //   105: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   108: goto -52 -> 56
        //   111: astore_1
        //   112: aload 7
        //   114: invokevirtual 62	android/os/Parcel:recycle	()V
        //   117: aload 6
        //   119: invokevirtual 62	android/os/Parcel:recycle	()V
        //   122: aload_1
        //   123: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	124	0	this	Proxy
        //   0	124	1	paramIAccountManagerResponse	IAccountManagerResponse
        //   0	124	2	paramAccount	Account
        //   0	124	3	paramInt1	int
        //   0	124	4	paramInt2	int
        //   1	34	5	localIBinder	IBinder
        //   6	112	6	localParcel1	Parcel
        //   11	102	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	111	finally
        //   24	32	111	finally
        //   32	39	111	finally
        //   43	56	111	finally
        //   56	91	111	finally
        //   102	108	111	finally
      }
      
      public IntentSender createRequestAccountAccessIntentSenderAsUser(Account paramAccount, String paramString, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(42, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label144;
                }
                paramAccount = (IntentSender)IntentSender.CREATOR.createFromParcel(localParcel2);
                return paramAccount;
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
          label144:
          paramAccount = null;
        }
      }
      
      public void editProperties(IAccountManagerResponse paramIAccountManagerResponse, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        IBinder localIBinder = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          if (paramIAccountManagerResponse != null) {
            localIBinder = paramIAccountManagerResponse.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void finishSessionAsUser(IAccountManagerResponse paramIAccountManagerResponse, Bundle paramBundle1, boolean paramBoolean, Bundle paramBundle2, int paramInt)
        throws RemoteException
      {
        int i = 1;
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramIAccountManagerResponse != null) {
              localIBinder = paramIAccountManagerResponse.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramBundle1 != null)
            {
              localParcel1.writeInt(1);
              paramBundle1.writeToParcel(localParcel1, 0);
              break label165;
              localParcel1.writeInt(i);
              if (paramBundle2 != null)
              {
                localParcel1.writeInt(1);
                paramBundle2.writeToParcel(localParcel1, 0);
                label88:
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(38, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
          label165:
          do
          {
            i = 0;
            break;
            localParcel1.writeInt(0);
            break label88;
          } while (!paramBoolean);
        }
      }
      
      public Account[] getAccounts(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = (Account[])localParcel2.createTypedArray(Account.CREATOR);
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public Account[] getAccountsAsUser(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = (Account[])localParcel2.createTypedArray(Account.CREATOR);
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void getAccountsByFeatures(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String[] paramArrayOfString, String paramString2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          if (paramIAccountManagerResponse != null) {
            localIBinder = paramIAccountManagerResponse.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString1);
          localParcel1.writeStringArray(paramArrayOfString);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public Account[] getAccountsByTypeForPackage(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = (Account[])localParcel2.createTypedArray(Account.CREATOR);
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public Account[] getAccountsForPackage(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = (Account[])localParcel2.createTypedArray(Account.CREATOR);
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void getAuthToken(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString, boolean paramBoolean1, boolean paramBoolean2, Bundle paramBundle)
        throws RemoteException
      {
        IBinder localIBinder = null;
        int j = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramIAccountManagerResponse != null) {
              localIBinder = paramIAccountManagerResponse.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean1)
              {
                i = 1;
                localParcel1.writeInt(i);
                if (!paramBoolean2) {
                  break label176;
                }
                i = j;
                localParcel1.writeInt(i);
                if (paramBundle == null) {
                  break label182;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(22, localParcel1, localParcel2, 0);
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
          continue;
          label176:
          int i = 0;
          continue;
          label182:
          localParcel1.writeInt(0);
        }
      }
      
      public void getAuthTokenLabel(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          if (paramIAccountManagerResponse != null) {
            localIBinder = paramIAccountManagerResponse.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public AuthenticatorDescription[] getAuthenticatorTypes(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          AuthenticatorDescription[] arrayOfAuthenticatorDescription = (AuthenticatorDescription[])localParcel2.createTypedArray(AuthenticatorDescription.CREATOR);
          return arrayOfAuthenticatorDescription;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.accounts.IAccountManager";
      }
      
      /* Error */
      public String getPassword(Account paramAccount)
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
        //   15: ifnull +47 -> 62
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_1
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 52 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 55	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 151	android/os/Parcel:readString	()Ljava/lang/String;
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 62	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 62	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aload_2
        //   63: iconst_0
        //   64: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   67: goto -38 -> 29
        //   70: astore_1
        //   71: aload_3
        //   72: invokevirtual 62	android/os/Parcel:recycle	()V
        //   75: aload_2
        //   76: invokevirtual 62	android/os/Parcel:recycle	()V
        //   79: aload_1
        //   80: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	81	0	this	Proxy
        //   0	81	1	paramAccount	Account
        //   3	73	2	localParcel1	Parcel
        //   7	65	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	70	finally
        //   18	29	70	finally
        //   29	52	70	finally
        //   62	67	70	finally
      }
      
      /* Error */
      public String getPreviousName(Account paramAccount)
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
        //   26: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 34
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 52 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 55	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 151	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 62	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramAccount	Account
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      public Account[] getSharedAccountsAsUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
          localParcel2.readException();
          Account[] arrayOfAccount = (Account[])localParcel2.createTypedArray(Account.CREATOR);
          return arrayOfAccount;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public String getUserData(Account paramAccount, String paramString)
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
        //   16: ifnull +56 -> 72
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_2
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 52 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 55	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 151	android/os/Parcel:readString	()Ljava/lang/String;
        //   60: astore_1
        //   61: aload 4
        //   63: invokevirtual 62	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 62	android/os/Parcel:recycle	()V
        //   70: aload_1
        //   71: areturn
        //   72: aload_3
        //   73: iconst_0
        //   74: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   77: goto -47 -> 30
        //   80: astore_1
        //   81: aload 4
        //   83: invokevirtual 62	android/os/Parcel:recycle	()V
        //   86: aload_3
        //   87: invokevirtual 62	android/os/Parcel:recycle	()V
        //   90: aload_1
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramAccount	Account
        //   0	92	2	paramString	String
        //   3	84	3	localParcel1	Parcel
        //   7	75	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	80	finally
        //   19	30	80	finally
        //   30	61	80	finally
        //   72	77	80	finally
      }
      
      public boolean hasAccountAccess(Account paramAccount, String paramString, UserHandle paramUserHandle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(41, localParcel1, localParcel2, 0);
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
      public void hasFeatures(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String[] paramArrayOfString, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 71 1 0
        //   30: astore 5
        //   32: aload 6
        //   34: aload 5
        //   36: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_2
        //   40: ifnull +62 -> 102
        //   43: aload 6
        //   45: iconst_1
        //   46: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   49: aload_2
        //   50: aload 6
        //   52: iconst_0
        //   53: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload 6
        //   58: aload_3
        //   59: invokevirtual 81	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   62: aload 6
        //   64: aload 4
        //   66: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   69: aload_0
        //   70: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   73: bipush 8
        //   75: aload 6
        //   77: aload 7
        //   79: iconst_0
        //   80: invokeinterface 52 5 0
        //   85: pop
        //   86: aload 7
        //   88: invokevirtual 55	android/os/Parcel:readException	()V
        //   91: aload 7
        //   93: invokevirtual 62	android/os/Parcel:recycle	()V
        //   96: aload 6
        //   98: invokevirtual 62	android/os/Parcel:recycle	()V
        //   101: return
        //   102: aload 6
        //   104: iconst_0
        //   105: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   108: goto -52 -> 56
        //   111: astore_1
        //   112: aload 7
        //   114: invokevirtual 62	android/os/Parcel:recycle	()V
        //   117: aload 6
        //   119: invokevirtual 62	android/os/Parcel:recycle	()V
        //   122: aload_1
        //   123: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	124	0	this	Proxy
        //   0	124	1	paramIAccountManagerResponse	IAccountManagerResponse
        //   0	124	2	paramAccount	Account
        //   0	124	3	paramArrayOfString	String[]
        //   0	124	4	paramString	String
        //   1	34	5	localIBinder	IBinder
        //   6	112	6	localParcel1	Parcel
        //   11	102	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	111	finally
        //   24	32	111	finally
        //   32	39	111	finally
        //   43	56	111	finally
        //   56	91	111	finally
        //   102	108	111	finally
      }
      
      public void invalidateAuthToken(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
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
      public void isCredentialsUpdateSuggested(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 71 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_2
        //   40: ifnull +55 -> 95
        //   43: aload 5
        //   45: iconst_1
        //   46: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   49: aload_2
        //   50: aload 5
        //   52: iconst_0
        //   53: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload 5
        //   58: aload_3
        //   59: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   62: aload_0
        //   63: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   66: bipush 40
        //   68: aload 5
        //   70: aload 6
        //   72: iconst_0
        //   73: invokeinterface 52 5 0
        //   78: pop
        //   79: aload 6
        //   81: invokevirtual 55	android/os/Parcel:readException	()V
        //   84: aload 6
        //   86: invokevirtual 62	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 62	android/os/Parcel:recycle	()V
        //   94: return
        //   95: aload 5
        //   97: iconst_0
        //   98: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   101: goto -45 -> 56
        //   104: astore_1
        //   105: aload 6
        //   107: invokevirtual 62	android/os/Parcel:recycle	()V
        //   110: aload 5
        //   112: invokevirtual 62	android/os/Parcel:recycle	()V
        //   115: aload_1
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	Proxy
        //   0	117	1	paramIAccountManagerResponse	IAccountManagerResponse
        //   0	117	2	paramAccount	Account
        //   0	117	3	paramString	String
        //   1	34	4	localIBinder	IBinder
        //   6	105	5	localParcel1	Parcel
        //   11	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	104	finally
        //   24	32	104	finally
        //   32	39	104	finally
        //   43	56	104	finally
        //   56	84	104	finally
        //   95	101	104	finally
      }
      
      public void onAccountAccessed(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(43, localParcel1, localParcel2, 0);
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
      public String peekAuthToken(Account paramAccount, String paramString)
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
        //   27: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 16
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 52 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 55	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 151	android/os/Parcel:readString	()Ljava/lang/String;
        //   61: astore_1
        //   62: aload 4
        //   64: invokevirtual 62	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 62	android/os/Parcel:recycle	()V
        //   71: aload_1
        //   72: areturn
        //   73: aload_3
        //   74: iconst_0
        //   75: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   78: goto -48 -> 30
        //   81: astore_1
        //   82: aload 4
        //   84: invokevirtual 62	android/os/Parcel:recycle	()V
        //   87: aload_3
        //   88: invokevirtual 62	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramAccount	Account
        //   0	93	2	paramString	String
        //   3	85	3	localParcel1	Parcel
        //   7	76	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	81	finally
        //   19	30	81	finally
        //   30	62	81	finally
        //   73	78	81	finally
      }
      
      public void removeAccount(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramIAccountManagerResponse != null) {
              localIBinder = paramIAccountManagerResponse.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              break label130;
              localParcel1.writeInt(i);
              this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
          label130:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void removeAccountAsUser(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 1;
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramIAccountManagerResponse != null) {
              localIBinder = paramIAccountManagerResponse.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              break label137;
              localParcel1.writeInt(i);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
          label137:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public boolean removeAccountExplicitly(Account paramAccount)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              this.mRemote.transact(13, localParcel1, localParcel2, 0);
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
      
      public boolean removeSharedAccountAsUser(Account paramAccount, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      public void renameAccount(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +11 -> 32
        //   24: aload_1
        //   25: invokeinterface 71 1 0
        //   30: astore 4
        //   32: aload 5
        //   34: aload 4
        //   36: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   39: aload_2
        //   40: ifnull +55 -> 95
        //   43: aload 5
        //   45: iconst_1
        //   46: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   49: aload_2
        //   50: aload 5
        //   52: iconst_0
        //   53: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   56: aload 5
        //   58: aload_3
        //   59: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   62: aload_0
        //   63: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   66: bipush 33
        //   68: aload 5
        //   70: aload 6
        //   72: iconst_0
        //   73: invokeinterface 52 5 0
        //   78: pop
        //   79: aload 6
        //   81: invokevirtual 55	android/os/Parcel:readException	()V
        //   84: aload 6
        //   86: invokevirtual 62	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 62	android/os/Parcel:recycle	()V
        //   94: return
        //   95: aload 5
        //   97: iconst_0
        //   98: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   101: goto -45 -> 56
        //   104: astore_1
        //   105: aload 6
        //   107: invokevirtual 62	android/os/Parcel:recycle	()V
        //   110: aload 5
        //   112: invokevirtual 62	android/os/Parcel:recycle	()V
        //   115: aload_1
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	Proxy
        //   0	117	1	paramIAccountManagerResponse	IAccountManagerResponse
        //   0	117	2	paramAccount	Account
        //   0	117	3	paramString	String
        //   1	34	4	localIBinder	IBinder
        //   6	105	5	localParcel1	Parcel
        //   11	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	104	finally
        //   24	32	104	finally
        //   32	39	104	finally
        //   43	56	104	finally
        //   56	84	104	finally
        //   95	101	104	finally
      }
      
      public boolean renameSharedAccountAsUser(Account paramAccount, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(35, localParcel1, localParcel2, 0);
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
      public void setAuthToken(Account paramAccount, String paramString1, String paramString2)
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
        //   31: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 17
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 52 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 55	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 62	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -51 -> 34
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 62	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 62	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramAccount	Account
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
      public void setPassword(Account paramAccount, String paramString)
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
        //   27: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 18
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 52 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 55	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 62	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 62	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 62	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 62	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramAccount	Account
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
      public void setUserData(Account paramAccount, String paramString1, String paramString2)
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
        //   31: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 20
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 52 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 55	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 62	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 62	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -51 -> 34
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 62	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 62	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramAccount	Account
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
      
      public boolean someUserHasAccount(Account paramAccount)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              this.mRemote.transact(39, localParcel1, localParcel2, 0);
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
      public void startAddAccountSession(IAccountManagerResponse paramIAccountManagerResponse, String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 7
        //   3: aconst_null
        //   4: astore 8
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 9
        //   11: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   14: astore 10
        //   16: aload 9
        //   18: ldc 32
        //   20: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   23: aload_1
        //   24: ifnull +11 -> 35
        //   27: aload_1
        //   28: invokeinterface 71 1 0
        //   33: astore 8
        //   35: aload 9
        //   37: aload 8
        //   39: invokevirtual 74	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   42: aload 9
        //   44: aload_2
        //   45: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   48: aload 9
        //   50: aload_3
        //   51: invokevirtual 77	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   54: aload 9
        //   56: aload 4
        //   58: invokevirtual 81	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   61: iload 5
        //   63: ifeq +62 -> 125
        //   66: aload 9
        //   68: iload 7
        //   70: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   73: aload 6
        //   75: ifnull +56 -> 131
        //   78: aload 9
        //   80: iconst_1
        //   81: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   84: aload 6
        //   86: aload 9
        //   88: iconst_0
        //   89: invokevirtual 84	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   92: aload_0
        //   93: getfield 19	android/accounts/IAccountManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   96: bipush 36
        //   98: aload 9
        //   100: aload 10
        //   102: iconst_0
        //   103: invokeinterface 52 5 0
        //   108: pop
        //   109: aload 10
        //   111: invokevirtual 55	android/os/Parcel:readException	()V
        //   114: aload 10
        //   116: invokevirtual 62	android/os/Parcel:recycle	()V
        //   119: aload 9
        //   121: invokevirtual 62	android/os/Parcel:recycle	()V
        //   124: return
        //   125: iconst_0
        //   126: istore 7
        //   128: goto -62 -> 66
        //   131: aload 9
        //   133: iconst_0
        //   134: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   137: goto -45 -> 92
        //   140: astore_1
        //   141: aload 10
        //   143: invokevirtual 62	android/os/Parcel:recycle	()V
        //   146: aload 9
        //   148: invokevirtual 62	android/os/Parcel:recycle	()V
        //   151: aload_1
        //   152: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	153	0	this	Proxy
        //   0	153	1	paramIAccountManagerResponse	IAccountManagerResponse
        //   0	153	2	paramString1	String
        //   0	153	3	paramString2	String
        //   0	153	4	paramArrayOfString	String[]
        //   0	153	5	paramBoolean	boolean
        //   0	153	6	paramBundle	Bundle
        //   1	126	7	i	int
        //   4	34	8	localIBinder	IBinder
        //   9	138	9	localParcel1	Parcel
        //   14	128	10	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   16	23	140	finally
        //   27	35	140	finally
        //   35	61	140	finally
        //   66	73	140	finally
        //   78	92	140	finally
        //   92	114	140	finally
        //   131	137	140	finally
      }
      
      public void startUpdateCredentialsSession(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString, boolean paramBoolean, Bundle paramBundle)
        throws RemoteException
      {
        int i = 1;
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramIAccountManagerResponse != null) {
              localIBinder = paramIAccountManagerResponse.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                if (paramBundle == null) {
                  break label157;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(37, localParcel1, localParcel2, 0);
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
          continue;
          label157:
          localParcel1.writeInt(0);
        }
      }
      
      public void updateAppPermission(Account paramAccount, String paramString, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(21, localParcel1, localParcel2, 0);
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
      
      public void updateCredentials(IAccountManagerResponse paramIAccountManagerResponse, Account paramAccount, String paramString, boolean paramBoolean, Bundle paramBundle)
        throws RemoteException
      {
        int i = 1;
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.accounts.IAccountManager");
            if (paramIAccountManagerResponse != null) {
              localIBinder = paramIAccountManagerResponse.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                if (paramBundle == null) {
                  break label157;
                }
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(25, localParcel1, localParcel2, 0);
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
          continue;
          label157:
          localParcel1.writeInt(0);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/IAccountManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */