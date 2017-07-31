package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface INetworkPolicyManager
  extends IInterface
{
  public abstract void addRestrictBackgroundWhitelistedUid(int paramInt)
    throws RemoteException;
  
  public abstract void addUidPolicy(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void factoryReset(String paramString)
    throws RemoteException;
  
  public abstract NetworkPolicy[] getNetworkPolicies(String paramString)
    throws RemoteException;
  
  public abstract NetworkQuotaInfo getNetworkQuotaInfo(NetworkState paramNetworkState)
    throws RemoteException;
  
  public abstract boolean getRestrictBackground()
    throws RemoteException;
  
  public abstract int getRestrictBackgroundByCaller()
    throws RemoteException;
  
  public abstract int[] getRestrictBackgroundWhitelistedUids()
    throws RemoteException;
  
  public abstract int getUidPolicy(int paramInt)
    throws RemoteException;
  
  public abstract int[] getUidsWithPolicy(int paramInt)
    throws RemoteException;
  
  public abstract boolean isNetworkMetered(NetworkState paramNetworkState)
    throws RemoteException;
  
  public abstract boolean isUidForeground(int paramInt)
    throws RemoteException;
  
  public abstract void onTetheringChanged(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void registerListener(INetworkPolicyListener paramINetworkPolicyListener)
    throws RemoteException;
  
  public abstract void removeRestrictBackgroundWhitelistedUid(int paramInt)
    throws RemoteException;
  
  public abstract void removeUidPolicy(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setConnectivityListener(INetworkPolicyListener paramINetworkPolicyListener)
    throws RemoteException;
  
  public abstract void setDeviceIdleMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setNetworkPolicies(NetworkPolicy[] paramArrayOfNetworkPolicy)
    throws RemoteException;
  
  public abstract void setRestrictBackground(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUidPolicy(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void snoozeLimit(NetworkTemplate paramNetworkTemplate)
    throws RemoteException;
  
  public abstract void unregisterListener(INetworkPolicyListener paramINetworkPolicyListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkPolicyManager
  {
    private static final String DESCRIPTOR = "android.net.INetworkPolicyManager";
    static final int TRANSACTION_addRestrictBackgroundWhitelistedUid = 16;
    static final int TRANSACTION_addUidPolicy = 2;
    static final int TRANSACTION_factoryReset = 23;
    static final int TRANSACTION_getNetworkPolicies = 11;
    static final int TRANSACTION_getNetworkQuotaInfo = 21;
    static final int TRANSACTION_getRestrictBackground = 14;
    static final int TRANSACTION_getRestrictBackgroundByCaller = 19;
    static final int TRANSACTION_getRestrictBackgroundWhitelistedUids = 18;
    static final int TRANSACTION_getUidPolicy = 4;
    static final int TRANSACTION_getUidsWithPolicy = 5;
    static final int TRANSACTION_isNetworkMetered = 22;
    static final int TRANSACTION_isUidForeground = 6;
    static final int TRANSACTION_onTetheringChanged = 15;
    static final int TRANSACTION_registerListener = 8;
    static final int TRANSACTION_removeRestrictBackgroundWhitelistedUid = 17;
    static final int TRANSACTION_removeUidPolicy = 3;
    static final int TRANSACTION_setConnectivityListener = 7;
    static final int TRANSACTION_setDeviceIdleMode = 20;
    static final int TRANSACTION_setNetworkPolicies = 10;
    static final int TRANSACTION_setRestrictBackground = 13;
    static final int TRANSACTION_setUidPolicy = 1;
    static final int TRANSACTION_snoozeLimit = 12;
    static final int TRANSACTION_unregisterListener = 9;
    
    public Stub()
    {
      attachInterface(this, "android.net.INetworkPolicyManager");
    }
    
    public static INetworkPolicyManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.INetworkPolicyManager");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkPolicyManager))) {
        return (INetworkPolicyManager)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.INetworkPolicyManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        setUidPolicy(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        addUidPolicy(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        removeUidPolicy(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        paramInt1 = getUidPolicy(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        paramParcel1 = getUidsWithPolicy(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        bool = isUidForeground(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        setConnectivityListener(INetworkPolicyListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        registerListener(INetworkPolicyListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        unregisterListener(INetworkPolicyListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        setNetworkPolicies((NetworkPolicy[])paramParcel1.createTypedArray(NetworkPolicy.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        paramParcel1 = getNetworkPolicies(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NetworkTemplate)NetworkTemplate.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          snoozeLimit(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setRestrictBackground(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        bool = getRestrictBackground();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          onTetheringChanged(str, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        addRestrictBackgroundWhitelistedUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        removeRestrictBackgroundWhitelistedUid(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        paramParcel1 = getRestrictBackgroundWhitelistedUids();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        paramInt1 = getRestrictBackgroundByCaller();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setDeviceIdleMode(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 21: 
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (NetworkState)NetworkState.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getNetworkQuotaInfo(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label836;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 22: 
        label836:
        paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (NetworkState)NetworkState.CREATOR.createFromParcel(paramParcel1);
          bool = isNetworkMetered(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label900;
          }
        }
        label900:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.net.INetworkPolicyManager");
      factoryReset(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements INetworkPolicyManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addRestrictBackgroundWhitelistedUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addUidPolicy(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
      
      public void factoryReset(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.INetworkPolicyManager";
      }
      
      public NetworkPolicy[] getNetworkPolicies(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = (NetworkPolicy[])localParcel2.createTypedArray(NetworkPolicy.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public NetworkQuotaInfo getNetworkQuotaInfo(NetworkState paramNetworkState)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
            if (paramNetworkState != null)
            {
              localParcel1.writeInt(1);
              paramNetworkState.writeToParcel(localParcel1, 0);
              this.mRemote.transact(21, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetworkState = (NetworkQuotaInfo)NetworkQuotaInfo.CREATOR.createFromParcel(localParcel2);
                return paramNetworkState;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetworkState = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean getRestrictBackground()
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
        //   15: aload_0
        //   16: getfield 19	android/net/INetworkPolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 14
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 45 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 48	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 88	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 51	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 51	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 51	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 51	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public int getRestrictBackgroundByCaller()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getRestrictBackgroundWhitelistedUids()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getUidPolicy(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getUidsWithPolicy(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean isNetworkMetered(NetworkState paramNetworkState)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
            if (paramNetworkState != null)
            {
              localParcel1.writeInt(1);
              paramNetworkState.writeToParcel(localParcel1, 0);
              this.mRemote.transact(22, localParcel1, localParcel2, 0);
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
      public boolean isUidForeground(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/net/INetworkPolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 45 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 48	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 88	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 51	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 51	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 51	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 51	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      public void onTetheringChanged(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void registerListener(INetworkPolicyListener paramINetworkPolicyListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          if (paramINetworkPolicyListener != null) {
            localIBinder = paramINetworkPolicyListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeRestrictBackgroundWhitelistedUid(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeUidPolicy(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setConnectivityListener(INetworkPolicyListener paramINetworkPolicyListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          if (paramINetworkPolicyListener != null) {
            localIBinder = paramINetworkPolicyListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setDeviceIdleMode(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setNetworkPolicies(NetworkPolicy[] paramArrayOfNetworkPolicy)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeTypedArray(paramArrayOfNetworkPolicy, 0);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setRestrictBackground(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setUidPolicy(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      public void snoozeLimit(NetworkTemplate paramNetworkTemplate)
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
        //   20: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 142	android/net/NetworkTemplate:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/INetworkPolicyManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 12
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 45 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 48	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 51	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 51	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 51	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 51	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramNetworkTemplate	NetworkTemplate
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void unregisterListener(INetworkPolicyListener paramINetworkPolicyListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkPolicyManager");
          if (paramINetworkPolicyListener != null) {
            localIBinder = paramINetworkPolicyListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/INetworkPolicyManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */