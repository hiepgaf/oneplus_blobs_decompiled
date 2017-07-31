package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMediaRouterService
  extends IInterface
{
  public abstract MediaRouterClientState getState(IMediaRouterClient paramIMediaRouterClient)
    throws RemoteException;
  
  public abstract void registerClientAsUser(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void requestSetVolume(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void requestUpdateVolume(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setDiscoveryRequest(IMediaRouterClient paramIMediaRouterClient, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSelectedRoute(IMediaRouterClient paramIMediaRouterClient, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void unregisterClient(IMediaRouterClient paramIMediaRouterClient)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaRouterService
  {
    private static final String DESCRIPTOR = "android.media.IMediaRouterService";
    static final int TRANSACTION_getState = 3;
    static final int TRANSACTION_registerClientAsUser = 1;
    static final int TRANSACTION_requestSetVolume = 6;
    static final int TRANSACTION_requestUpdateVolume = 7;
    static final int TRANSACTION_setDiscoveryRequest = 4;
    static final int TRANSACTION_setSelectedRoute = 5;
    static final int TRANSACTION_unregisterClient = 2;
    
    public Stub()
    {
      attachInterface(this, "android.media.IMediaRouterService");
    }
    
    public static IMediaRouterService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IMediaRouterService");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaRouterService))) {
        return (IMediaRouterService)localIInterface;
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
      boolean bool2 = false;
      boolean bool1 = false;
      IMediaRouterClient localIMediaRouterClient;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.IMediaRouterService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.IMediaRouterService");
        registerClientAsUser(IMediaRouterClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.IMediaRouterService");
        unregisterClient(IMediaRouterClient.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.IMediaRouterService");
        paramParcel1 = getState(IMediaRouterClient.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.media.IMediaRouterService");
        localIMediaRouterClient = IMediaRouterClient.Stub.asInterface(paramParcel1.readStrongBinder());
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        setDiscoveryRequest(localIMediaRouterClient, paramInt1, bool1);
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.IMediaRouterService");
        localIMediaRouterClient = IMediaRouterClient.Stub.asInterface(paramParcel1.readStrongBinder());
        String str = paramParcel1.readString();
        bool1 = bool2;
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        setSelectedRoute(localIMediaRouterClient, str, bool1);
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.media.IMediaRouterService");
        requestSetVolume(IMediaRouterClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.media.IMediaRouterService");
      requestUpdateVolume(IMediaRouterClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IMediaRouterService
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
        return "android.media.IMediaRouterService";
      }
      
      /* Error */
      public MediaRouterClientState getState(IMediaRouterClient paramIMediaRouterClient)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +10 -> 28
        //   21: aload_1
        //   22: invokeinterface 44 1 0
        //   27: astore_2
        //   28: aload_3
        //   29: aload_2
        //   30: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   33: aload_0
        //   34: getfield 19	android/media/IMediaRouterService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   37: iconst_3
        //   38: aload_3
        //   39: aload 4
        //   41: iconst_0
        //   42: invokeinterface 53 5 0
        //   47: pop
        //   48: aload 4
        //   50: invokevirtual 56	android/os/Parcel:readException	()V
        //   53: aload 4
        //   55: invokevirtual 60	android/os/Parcel:readInt	()I
        //   58: ifeq +28 -> 86
        //   61: getstatic 66	android/media/MediaRouterClientState:CREATOR	Landroid/os/Parcelable$Creator;
        //   64: aload 4
        //   66: invokeinterface 72 2 0
        //   71: checkcast 62	android/media/MediaRouterClientState
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 75	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 75	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: areturn
        //   86: aconst_null
        //   87: astore_1
        //   88: goto -13 -> 75
        //   91: astore_1
        //   92: aload 4
        //   94: invokevirtual 75	android/os/Parcel:recycle	()V
        //   97: aload_3
        //   98: invokevirtual 75	android/os/Parcel:recycle	()V
        //   101: aload_1
        //   102: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	103	0	this	Proxy
        //   0	103	1	paramIMediaRouterClient	IMediaRouterClient
        //   1	29	2	localIBinder	IBinder
        //   5	93	3	localParcel1	Parcel
        //   9	84	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	17	91	finally
        //   21	28	91	finally
        //   28	75	91	finally
      }
      
      public void registerClientAsUser(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaRouterService");
          if (paramIMediaRouterClient != null) {
            localIBinder = paramIMediaRouterClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
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
      
      public void requestSetVolume(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaRouterService");
          if (paramIMediaRouterClient != null) {
            localIBinder = paramIMediaRouterClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString);
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
      
      public void requestUpdateVolume(IMediaRouterClient paramIMediaRouterClient, String paramString, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaRouterService");
          if (paramIMediaRouterClient != null) {
            localIBinder = paramIMediaRouterClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
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
      
      public void setDiscoveryRequest(IMediaRouterClient paramIMediaRouterClient, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        IBinder localIBinder = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaRouterService");
          if (paramIMediaRouterClient != null) {
            localIBinder = paramIMediaRouterClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
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
      
      public void setSelectedRoute(IMediaRouterClient paramIMediaRouterClient, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        IBinder localIBinder = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaRouterService");
          if (paramIMediaRouterClient != null) {
            localIBinder = paramIMediaRouterClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void unregisterClient(IMediaRouterClient paramIMediaRouterClient)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IMediaRouterService");
          if (paramIMediaRouterClient != null) {
            localIBinder = paramIMediaRouterClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IMediaRouterService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */