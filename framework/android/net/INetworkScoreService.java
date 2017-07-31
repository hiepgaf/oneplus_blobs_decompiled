package android.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface INetworkScoreService
  extends IInterface
{
  public abstract boolean clearScores()
    throws RemoteException;
  
  public abstract void disableScoring()
    throws RemoteException;
  
  public abstract void registerNetworkScoreCache(int paramInt, INetworkScoreCache paramINetworkScoreCache)
    throws RemoteException;
  
  public abstract boolean setActiveScorer(String paramString)
    throws RemoteException;
  
  public abstract boolean updateScores(ScoredNetwork[] paramArrayOfScoredNetwork)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkScoreService
  {
    private static final String DESCRIPTOR = "android.net.INetworkScoreService";
    static final int TRANSACTION_clearScores = 2;
    static final int TRANSACTION_disableScoring = 4;
    static final int TRANSACTION_registerNetworkScoreCache = 5;
    static final int TRANSACTION_setActiveScorer = 3;
    static final int TRANSACTION_updateScores = 1;
    
    public Stub()
    {
      attachInterface(this, "android.net.INetworkScoreService");
    }
    
    public static INetworkScoreService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.INetworkScoreService");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkScoreService))) {
        return (INetworkScoreService)localIInterface;
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
      int j = 0;
      int k = 0;
      int i = 0;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.INetworkScoreService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.INetworkScoreService");
        bool = updateScores((ScoredNetwork[])paramParcel1.createTypedArray(ScoredNetwork.CREATOR));
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.net.INetworkScoreService");
        bool = clearScores();
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.net.INetworkScoreService");
        bool = setActiveScorer(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramInt1 = k;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.net.INetworkScoreService");
        disableScoring();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.net.INetworkScoreService");
      registerNetworkScoreCache(paramParcel1.readInt(), INetworkScoreCache.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements INetworkScoreService
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
      
      /* Error */
      public boolean clearScores()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/net/INetworkScoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_2
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 44 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 47	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 51	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 54	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 54	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 54	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public void disableScoring()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkScoreService");
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
      
      public String getInterfaceDescriptor()
      {
        return "android.net.INetworkScoreService";
      }
      
      public void registerNetworkScoreCache(int paramInt, INetworkScoreCache paramINetworkScoreCache)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.INetworkScoreService");
          localParcel1.writeInt(paramInt);
          if (paramINetworkScoreCache != null) {
            localIBinder = paramINetworkScoreCache.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      public boolean setActiveScorer(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 76	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/net/INetworkScoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_3
        //   28: aload 4
        //   30: aload 5
        //   32: iconst_0
        //   33: invokeinterface 44 5 0
        //   38: pop
        //   39: aload 5
        //   41: invokevirtual 47	android/os/Parcel:readException	()V
        //   44: aload 5
        //   46: invokevirtual 51	android/os/Parcel:readInt	()I
        //   49: istore_2
        //   50: iload_2
        //   51: ifeq +17 -> 68
        //   54: iconst_1
        //   55: istore_3
        //   56: aload 5
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 54	android/os/Parcel:recycle	()V
        //   66: iload_3
        //   67: ireturn
        //   68: iconst_0
        //   69: istore_3
        //   70: goto -14 -> 56
        //   73: astore_1
        //   74: aload 5
        //   76: invokevirtual 54	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 54	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   49	2	2	i	int
        //   55	15	3	bool	boolean
        //   3	77	4	localParcel1	Parcel
        //   8	67	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	50	73	finally
      }
      
      /* Error */
      public boolean updateScores(ScoredNetwork[] paramArrayOfScoredNetwork)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: iconst_0
        //   21: invokevirtual 82	android/os/Parcel:writeTypedArray	([Landroid/os/Parcelable;I)V
        //   24: aload_0
        //   25: getfield 19	android/net/INetworkScoreService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   28: iconst_1
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 44 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 47	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 51	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 54	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 54	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 54	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramArrayOfScoredNetwork	ScoredNetwork[]
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/INetworkScoreService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */