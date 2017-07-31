package android.accounts;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IAccountAuthenticator
  extends IInterface
{
  public abstract void addAccount(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void addAccountFromCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void confirmCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void editProperties(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString)
    throws RemoteException;
  
  public abstract void finishSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void getAccountCredentialsForCloning(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount)
    throws RemoteException;
  
  public abstract void getAccountRemovalAllowed(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount)
    throws RemoteException;
  
  public abstract void getAuthToken(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void getAuthTokenLabel(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString)
    throws RemoteException;
  
  public abstract void hasFeatures(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void isCredentialsUpdateSuggested(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract void startAddAccountSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void startUpdateCredentialsSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void updateCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IAccountAuthenticator
  {
    private static final String DESCRIPTOR = "android.accounts.IAccountAuthenticator";
    static final int TRANSACTION_addAccount = 1;
    static final int TRANSACTION_addAccountFromCredentials = 10;
    static final int TRANSACTION_confirmCredentials = 2;
    static final int TRANSACTION_editProperties = 6;
    static final int TRANSACTION_finishSession = 13;
    static final int TRANSACTION_getAccountCredentialsForCloning = 9;
    static final int TRANSACTION_getAccountRemovalAllowed = 8;
    static final int TRANSACTION_getAuthToken = 3;
    static final int TRANSACTION_getAuthTokenLabel = 4;
    static final int TRANSACTION_hasFeatures = 7;
    static final int TRANSACTION_isCredentialsUpdateSuggested = 14;
    static final int TRANSACTION_startAddAccountSession = 11;
    static final int TRANSACTION_startUpdateCredentialsSession = 12;
    static final int TRANSACTION_updateCredentials = 5;
    
    public Stub()
    {
      attachInterface(this, "android.accounts.IAccountAuthenticator");
    }
    
    public static IAccountAuthenticator asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.accounts.IAccountAuthenticator");
      if ((localIInterface != null) && ((localIInterface instanceof IAccountAuthenticator))) {
        return (IAccountAuthenticator)localIInterface;
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
      String str;
      String[] arrayOfString;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.accounts.IAccountAuthenticator");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        paramParcel2 = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject = paramParcel1.readString();
        str = paramParcel1.readString();
        arrayOfString = paramParcel1.createStringArray();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addAccount(paramParcel2, (String)localObject, str, arrayOfString, paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        localObject = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label291;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          confirmCredentials((IAccountAuthenticatorResponse)localObject, paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        localObject = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label374;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          getAuthToken((IAccountAuthenticatorResponse)localObject, paramParcel2, str, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        getAuthTokenLabel(IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        localObject = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label480;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          updateCredentials((IAccountAuthenticatorResponse)localObject, paramParcel2, str, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 6: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        editProperties(IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        localObject = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          hasFeatures((IAccountAuthenticatorResponse)localObject, paramParcel2, paramParcel1.createStringArray());
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        paramParcel2 = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          getAccountRemovalAllowed(paramParcel2, paramParcel1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        paramParcel2 = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          getAccountCredentialsForCloning(paramParcel2, paramParcel1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        localObject = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label725;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addAccountFromCredentials((IAccountAuthenticatorResponse)localObject, paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 11: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        paramParcel2 = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject = paramParcel1.readString();
        str = paramParcel1.readString();
        arrayOfString = paramParcel1.createStringArray();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startAddAccountSession(paramParcel2, (String)localObject, str, arrayOfString, paramParcel1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        localObject = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label879;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startUpdateCredentialsSession((IAccountAuthenticatorResponse)localObject, paramParcel2, str, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 13: 
        label291:
        label374:
        label480:
        label725:
        label879:
        paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
        paramParcel2 = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          finishSession(paramParcel2, (String)localObject, paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.accounts.IAccountAuthenticator");
      Object localObject = IAccountAuthenticatorResponse.Stub.asInterface(paramParcel1.readStrongBinder());
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel2 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
      {
        isCredentialsUpdateSuggested((IAccountAuthenticatorResponse)localObject, paramParcel2, paramParcel1.readString());
        return true;
      }
    }
    
    private static class Proxy
      implements IAccountAuthenticator
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void addAccount(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 6
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: aload 7
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +11 -> 27
        //   19: aload_1
        //   20: invokeinterface 42 1 0
        //   25: astore 6
        //   27: aload 7
        //   29: aload 6
        //   31: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload 7
        //   36: aload_2
        //   37: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 7
        //   42: aload_3
        //   43: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload 7
        //   48: aload 4
        //   50: invokevirtual 52	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   53: aload 5
        //   55: ifnull +38 -> 93
        //   58: aload 7
        //   60: iconst_1
        //   61: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   64: aload 5
        //   66: aload 7
        //   68: iconst_0
        //   69: invokevirtual 62	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   72: aload_0
        //   73: getfield 19	android/accounts/IAccountAuthenticator$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   76: iconst_1
        //   77: aload 7
        //   79: aconst_null
        //   80: iconst_1
        //   81: invokeinterface 68 5 0
        //   86: pop
        //   87: aload 7
        //   89: invokevirtual 71	android/os/Parcel:recycle	()V
        //   92: return
        //   93: aload 7
        //   95: iconst_0
        //   96: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   99: goto -27 -> 72
        //   102: astore_1
        //   103: aload 7
        //   105: invokevirtual 71	android/os/Parcel:recycle	()V
        //   108: aload_1
        //   109: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	110	0	this	Proxy
        //   0	110	1	paramIAccountAuthenticatorResponse	IAccountAuthenticatorResponse
        //   0	110	2	paramString1	String
        //   0	110	3	paramString2	String
        //   0	110	4	paramArrayOfString	String[]
        //   0	110	5	paramBundle	Bundle
        //   1	29	6	localIBinder	IBinder
        //   6	98	7	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	102	finally
        //   19	27	102	finally
        //   27	53	102	finally
        //   58	72	102	finally
        //   72	87	102	finally
        //   93	99	102	finally
      }
      
      public void addAccountFromCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.accounts.IAccountAuthenticator");
            if (paramIAccountAuthenticatorResponse != null) {
              localIBinder = paramIAccountAuthenticatorResponse.asBinder();
            }
            localParcel.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel.writeInt(1);
              paramAccount.writeToParcel(localParcel, 0);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(10, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void confirmCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, Bundle paramBundle)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.accounts.IAccountAuthenticator");
            if (paramIAccountAuthenticatorResponse != null) {
              localIBinder = paramIAccountAuthenticatorResponse.asBinder();
            }
            localParcel.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel.writeInt(1);
              paramAccount.writeToParcel(localParcel, 0);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(2, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void editProperties(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.accounts.IAccountAuthenticator");
          if (paramIAccountAuthenticatorResponse != null) {
            localIBinder = paramIAccountAuthenticatorResponse.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeString(paramString);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void finishSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +11 -> 27
        //   19: aload_1
        //   20: invokeinterface 42 1 0
        //   25: astore 4
        //   27: aload 5
        //   29: aload 4
        //   31: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload 5
        //   36: aload_2
        //   37: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload_3
        //   41: ifnull +38 -> 79
        //   44: aload 5
        //   46: iconst_1
        //   47: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   50: aload_3
        //   51: aload 5
        //   53: iconst_0
        //   54: invokevirtual 62	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   57: aload_0
        //   58: getfield 19	android/accounts/IAccountAuthenticator$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   61: bipush 13
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 68 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 71	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   85: goto -28 -> 57
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 71	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramIAccountAuthenticatorResponse	IAccountAuthenticatorResponse
        //   0	96	2	paramString	String
        //   0	96	3	paramBundle	Bundle
        //   1	29	4	localIBinder	IBinder
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	88	finally
        //   19	27	88	finally
        //   27	40	88	finally
        //   44	57	88	finally
        //   57	73	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void getAccountCredentialsForCloning(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +10 -> 25
        //   18: aload_1
        //   19: invokeinterface 42 1 0
        //   24: astore_3
        //   25: aload 4
        //   27: aload_3
        //   28: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +38 -> 70
        //   35: aload 4
        //   37: iconst_1
        //   38: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   41: aload_2
        //   42: aload 4
        //   44: iconst_0
        //   45: invokevirtual 77	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   48: aload_0
        //   49: getfield 19	android/accounts/IAccountAuthenticator$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   52: bipush 9
        //   54: aload 4
        //   56: aconst_null
        //   57: iconst_1
        //   58: invokeinterface 68 5 0
        //   63: pop
        //   64: aload 4
        //   66: invokevirtual 71	android/os/Parcel:recycle	()V
        //   69: return
        //   70: aload 4
        //   72: iconst_0
        //   73: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   76: goto -28 -> 48
        //   79: astore_1
        //   80: aload 4
        //   82: invokevirtual 71	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramIAccountAuthenticatorResponse	IAccountAuthenticatorResponse
        //   0	87	2	paramAccount	Account
        //   1	27	3	localIBinder	IBinder
        //   5	76	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	79	finally
        //   18	25	79	finally
        //   25	31	79	finally
        //   35	48	79	finally
        //   48	64	79	finally
        //   70	76	79	finally
      }
      
      /* Error */
      public void getAccountRemovalAllowed(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +10 -> 25
        //   18: aload_1
        //   19: invokeinterface 42 1 0
        //   24: astore_3
        //   25: aload 4
        //   27: aload_3
        //   28: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   31: aload_2
        //   32: ifnull +38 -> 70
        //   35: aload 4
        //   37: iconst_1
        //   38: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   41: aload_2
        //   42: aload 4
        //   44: iconst_0
        //   45: invokevirtual 77	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   48: aload_0
        //   49: getfield 19	android/accounts/IAccountAuthenticator$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   52: bipush 8
        //   54: aload 4
        //   56: aconst_null
        //   57: iconst_1
        //   58: invokeinterface 68 5 0
        //   63: pop
        //   64: aload 4
        //   66: invokevirtual 71	android/os/Parcel:recycle	()V
        //   69: return
        //   70: aload 4
        //   72: iconst_0
        //   73: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   76: goto -28 -> 48
        //   79: astore_1
        //   80: aload 4
        //   82: invokevirtual 71	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramIAccountAuthenticatorResponse	IAccountAuthenticatorResponse
        //   0	87	2	paramAccount	Account
        //   1	27	3	localIBinder	IBinder
        //   5	76	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   7	14	79	finally
        //   18	25	79	finally
        //   25	31	79	finally
        //   35	48	79	finally
        //   48	64	79	finally
        //   70	76	79	finally
      }
      
      public void getAuthToken(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.accounts.IAccountAuthenticator");
            if (paramIAccountAuthenticatorResponse != null) {
              localIBinder = paramIAccountAuthenticatorResponse.asBinder();
            }
            localParcel.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel.writeInt(1);
              paramAccount.writeToParcel(localParcel, 0);
              localParcel.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(3, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void getAuthTokenLabel(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.accounts.IAccountAuthenticator");
          if (paramIAccountAuthenticatorResponse != null) {
            localIBinder = paramIAccountAuthenticatorResponse.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeString(paramString);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.accounts.IAccountAuthenticator";
      }
      
      /* Error */
      public void hasFeatures(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String[] paramArrayOfString)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +11 -> 27
        //   19: aload_1
        //   20: invokeinterface 42 1 0
        //   25: astore 4
        //   27: aload 5
        //   29: aload 4
        //   31: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload_2
        //   35: ifnull +44 -> 79
        //   38: aload 5
        //   40: iconst_1
        //   41: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   44: aload_2
        //   45: aload 5
        //   47: iconst_0
        //   48: invokevirtual 77	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   51: aload 5
        //   53: aload_3
        //   54: invokevirtual 52	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   57: aload_0
        //   58: getfield 19	android/accounts/IAccountAuthenticator$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   61: bipush 7
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 68 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 71	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   85: goto -34 -> 51
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 71	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramIAccountAuthenticatorResponse	IAccountAuthenticatorResponse
        //   0	96	2	paramAccount	Account
        //   0	96	3	paramArrayOfString	String[]
        //   1	29	4	localIBinder	IBinder
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	88	finally
        //   19	27	88	finally
        //   27	34	88	finally
        //   38	51	88	finally
        //   51	73	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void isCredentialsUpdateSuggested(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +11 -> 27
        //   19: aload_1
        //   20: invokeinterface 42 1 0
        //   25: astore 4
        //   27: aload 5
        //   29: aload 4
        //   31: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload_2
        //   35: ifnull +44 -> 79
        //   38: aload 5
        //   40: iconst_1
        //   41: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   44: aload_2
        //   45: aload 5
        //   47: iconst_0
        //   48: invokevirtual 77	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   51: aload 5
        //   53: aload_3
        //   54: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   57: aload_0
        //   58: getfield 19	android/accounts/IAccountAuthenticator$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   61: bipush 14
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 68 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 71	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   85: goto -34 -> 51
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 71	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramIAccountAuthenticatorResponse	IAccountAuthenticatorResponse
        //   0	96	2	paramAccount	Account
        //   0	96	3	paramString	String
        //   1	29	4	localIBinder	IBinder
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	88	finally
        //   19	27	88	finally
        //   27	34	88	finally
        //   38	51	88	finally
        //   51	73	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void startAddAccountSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, String paramString1, String paramString2, String[] paramArrayOfString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 6
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: aload 7
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +11 -> 27
        //   19: aload_1
        //   20: invokeinterface 42 1 0
        //   25: astore 6
        //   27: aload 7
        //   29: aload 6
        //   31: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   34: aload 7
        //   36: aload_2
        //   37: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 7
        //   42: aload_3
        //   43: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload 7
        //   48: aload 4
        //   50: invokevirtual 52	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   53: aload 5
        //   55: ifnull +39 -> 94
        //   58: aload 7
        //   60: iconst_1
        //   61: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   64: aload 5
        //   66: aload 7
        //   68: iconst_0
        //   69: invokevirtual 62	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   72: aload_0
        //   73: getfield 19	android/accounts/IAccountAuthenticator$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   76: bipush 11
        //   78: aload 7
        //   80: aconst_null
        //   81: iconst_1
        //   82: invokeinterface 68 5 0
        //   87: pop
        //   88: aload 7
        //   90: invokevirtual 71	android/os/Parcel:recycle	()V
        //   93: return
        //   94: aload 7
        //   96: iconst_0
        //   97: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   100: goto -28 -> 72
        //   103: astore_1
        //   104: aload 7
        //   106: invokevirtual 71	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramIAccountAuthenticatorResponse	IAccountAuthenticatorResponse
        //   0	111	2	paramString1	String
        //   0	111	3	paramString2	String
        //   0	111	4	paramArrayOfString	String[]
        //   0	111	5	paramBundle	Bundle
        //   1	29	6	localIBinder	IBinder
        //   6	99	7	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	103	finally
        //   19	27	103	finally
        //   27	53	103	finally
        //   58	72	103	finally
        //   72	88	103	finally
        //   94	100	103	finally
      }
      
      public void startUpdateCredentialsSession(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.accounts.IAccountAuthenticator");
            if (paramIAccountAuthenticatorResponse != null) {
              localIBinder = paramIAccountAuthenticatorResponse.asBinder();
            }
            localParcel.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel.writeInt(1);
              paramAccount.writeToParcel(localParcel, 0);
              localParcel.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(12, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void updateCredentials(IAccountAuthenticatorResponse paramIAccountAuthenticatorResponse, Account paramAccount, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.accounts.IAccountAuthenticator");
            if (paramIAccountAuthenticatorResponse != null) {
              localIBinder = paramIAccountAuthenticatorResponse.asBinder();
            }
            localParcel.writeStrongBinder(localIBinder);
            if (paramAccount != null)
            {
              localParcel.writeInt(1);
              paramAccount.writeToParcel(localParcel, 0);
              localParcel.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(5, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/IAccountAuthenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */