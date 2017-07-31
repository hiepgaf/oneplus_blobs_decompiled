package android.app.trust;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ITrustManager
  extends IInterface
{
  public abstract boolean isDeviceLocked(int paramInt)
    throws RemoteException;
  
  public abstract boolean isDeviceSecure(int paramInt)
    throws RemoteException;
  
  public abstract boolean isTrustUsuallyManaged(int paramInt)
    throws RemoteException;
  
  public abstract void registerTrustListener(ITrustListener paramITrustListener)
    throws RemoteException;
  
  public abstract void reportEnabledTrustAgentsChanged(int paramInt)
    throws RemoteException;
  
  public abstract void reportKeyguardShowingChanged()
    throws RemoteException;
  
  public abstract void reportUnlockAttempt(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setDeviceLockedForUser(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void unregisterTrustListener(ITrustListener paramITrustListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITrustManager
  {
    private static final String DESCRIPTOR = "android.app.trust.ITrustManager";
    static final int TRANSACTION_isDeviceLocked = 7;
    static final int TRANSACTION_isDeviceSecure = 8;
    static final int TRANSACTION_isTrustUsuallyManaged = 9;
    static final int TRANSACTION_registerTrustListener = 3;
    static final int TRANSACTION_reportEnabledTrustAgentsChanged = 2;
    static final int TRANSACTION_reportKeyguardShowingChanged = 5;
    static final int TRANSACTION_reportUnlockAttempt = 1;
    static final int TRANSACTION_setDeviceLockedForUser = 6;
    static final int TRANSACTION_unregisterTrustListener = 4;
    
    public Stub()
    {
      attachInterface(this, "android.app.trust.ITrustManager");
    }
    
    public static ITrustManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.trust.ITrustManager");
      if ((localIInterface != null) && ((localIInterface instanceof ITrustManager))) {
        return (ITrustManager)localIInterface;
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
      int i = 0;
      int j = 0;
      int k = 0;
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.trust.ITrustManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          reportUnlockAttempt(bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        reportEnabledTrustAgentsChanged(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        registerTrustListener(ITrustListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        unregisterTrustListener(ITrustListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        reportKeyguardShowingChanged();
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        setDeviceLockedForUser(paramInt1, bool);
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        bool = isDeviceLocked(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.app.trust.ITrustManager");
        bool = isDeviceSecure(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.app.trust.ITrustManager");
      bool = isTrustUsuallyManaged(paramParcel1.readInt());
      paramParcel2.writeNoException();
      paramInt1 = k;
      if (bool) {
        paramInt1 = 1;
      }
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements ITrustManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.trust.ITrustManager";
      }
      
      /* Error */
      public boolean isDeviceLocked(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/app/trust/ITrustManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 7
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 50 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 53	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 57	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 60	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 60	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 60	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 60	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean isDeviceSecure(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/app/trust/ITrustManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 8
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 50 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 53	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 57	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 60	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 60	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 60	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 60	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean isTrustUsuallyManaged(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/app/trust/ITrustManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 9
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 50 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 53	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 57	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 60	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 60	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 60	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 60	android/os/Parcel:recycle	()V
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
      
      public void registerTrustListener(ITrustListener paramITrustListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.trust.ITrustManager");
          if (paramITrustListener != null) {
            localIBinder = paramITrustListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void reportEnabledTrustAgentsChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.trust.ITrustManager");
          localParcel1.writeInt(paramInt);
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
      
      public void reportKeyguardShowingChanged()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.trust.ITrustManager");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
      public void reportUnlockAttempt(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 26
        //   16: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: iload_1
        //   20: ifeq +47 -> 67
        //   23: aload 4
        //   25: iload_3
        //   26: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   29: aload 4
        //   31: iload_2
        //   32: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/app/trust/ITrustManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_1
        //   40: aload 4
        //   42: aload 5
        //   44: iconst_0
        //   45: invokeinterface 50 5 0
        //   50: pop
        //   51: aload 5
        //   53: invokevirtual 53	android/os/Parcel:readException	()V
        //   56: aload 5
        //   58: invokevirtual 60	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 60	android/os/Parcel:recycle	()V
        //   66: return
        //   67: iconst_0
        //   68: istore_3
        //   69: goto -46 -> 23
        //   72: astore 6
        //   74: aload 5
        //   76: invokevirtual 60	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 60	android/os/Parcel:recycle	()V
        //   84: aload 6
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramBoolean	boolean
        //   0	87	2	paramInt	int
        //   1	68	3	i	int
        //   5	75	4	localParcel1	Parcel
        //   10	65	5	localParcel2	Parcel
        //   72	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   12	19	72	finally
        //   23	56	72	finally
      }
      
      public void setDeviceLockedForUser(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.trust.ITrustManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterTrustListener(ITrustListener paramITrustListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.trust.ITrustManager");
          if (paramITrustListener != null) {
            localIBinder = paramITrustListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/trust/ITrustManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */