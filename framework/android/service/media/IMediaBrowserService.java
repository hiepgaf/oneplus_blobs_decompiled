package android.service.media;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;

public abstract interface IMediaBrowserService
  extends IInterface
{
  public abstract void addSubscription(String paramString, IBinder paramIBinder, Bundle paramBundle, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    throws RemoteException;
  
  public abstract void addSubscriptionDeprecated(String paramString, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    throws RemoteException;
  
  public abstract void connect(String paramString, Bundle paramBundle, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    throws RemoteException;
  
  public abstract void disconnect(IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    throws RemoteException;
  
  public abstract void getMediaItem(String paramString, ResultReceiver paramResultReceiver, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    throws RemoteException;
  
  public abstract void removeSubscription(String paramString, IBinder paramIBinder, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    throws RemoteException;
  
  public abstract void removeSubscriptionDeprecated(String paramString, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaBrowserService
  {
    private static final String DESCRIPTOR = "android.service.media.IMediaBrowserService";
    static final int TRANSACTION_addSubscription = 6;
    static final int TRANSACTION_addSubscriptionDeprecated = 3;
    static final int TRANSACTION_connect = 1;
    static final int TRANSACTION_disconnect = 2;
    static final int TRANSACTION_getMediaItem = 5;
    static final int TRANSACTION_removeSubscription = 7;
    static final int TRANSACTION_removeSubscriptionDeprecated = 4;
    
    public Stub()
    {
      attachInterface(this, "android.service.media.IMediaBrowserService");
    }
    
    public static IMediaBrowserService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.media.IMediaBrowserService");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaBrowserService))) {
        return (IMediaBrowserService)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.media.IMediaBrowserService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          connect(str, paramParcel2, IMediaBrowserServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserService");
        disconnect(IMediaBrowserServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserService");
        addSubscriptionDeprecated(paramParcel1.readString(), IMediaBrowserServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserService");
        removeSubscriptionDeprecated(paramParcel1.readString(), IMediaBrowserServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          getMediaItem(str, paramParcel2, IMediaBrowserServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserService");
        str = paramParcel1.readString();
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          addSubscription(str, localIBinder, paramParcel2, IMediaBrowserServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      }
      paramParcel1.enforceInterface("android.service.media.IMediaBrowserService");
      removeSubscription(paramParcel1.readString(), paramParcel1.readStrongBinder(), IMediaBrowserServiceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
      return true;
    }
    
    private static class Proxy
      implements IMediaBrowserService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void addSubscription(String paramString, IBinder paramIBinder, Bundle paramBundle, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: aload 6
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 6
        //   17: aload_1
        //   18: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   21: aload 6
        //   23: aload_2
        //   24: invokevirtual 42	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   27: aload_3
        //   28: ifnull +60 -> 88
        //   31: aload 6
        //   33: iconst_1
        //   34: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   37: aload_3
        //   38: aload 6
        //   40: iconst_0
        //   41: invokevirtual 52	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   44: aload 5
        //   46: astore_1
        //   47: aload 4
        //   49: ifnull +11 -> 60
        //   52: aload 4
        //   54: invokeinterface 58 1 0
        //   59: astore_1
        //   60: aload 6
        //   62: aload_1
        //   63: invokevirtual 42	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   66: aload_0
        //   67: getfield 19	android/service/media/IMediaBrowserService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   70: bipush 6
        //   72: aload 6
        //   74: aconst_null
        //   75: iconst_1
        //   76: invokeinterface 64 5 0
        //   81: pop
        //   82: aload 6
        //   84: invokevirtual 67	android/os/Parcel:recycle	()V
        //   87: return
        //   88: aload 6
        //   90: iconst_0
        //   91: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   94: goto -50 -> 44
        //   97: astore_1
        //   98: aload 6
        //   100: invokevirtual 67	android/os/Parcel:recycle	()V
        //   103: aload_1
        //   104: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	105	0	this	Proxy
        //   0	105	1	paramString	String
        //   0	105	2	paramIBinder	IBinder
        //   0	105	3	paramBundle	Bundle
        //   0	105	4	paramIMediaBrowserServiceCallbacks	IMediaBrowserServiceCallbacks
        //   1	44	5	localObject	Object
        //   6	93	6	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	27	97	finally
        //   31	44	97	finally
        //   52	60	97	finally
        //   60	82	97	finally
        //   88	94	97	finally
      }
      
      public void addSubscriptionDeprecated(String paramString, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.media.IMediaBrowserService");
          localParcel.writeString(paramString);
          paramString = (String)localObject;
          if (paramIMediaBrowserServiceCallbacks != null) {
            paramString = paramIMediaBrowserServiceCallbacks.asBinder();
          }
          localParcel.writeStrongBinder(paramString);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public void connect(String paramString, Bundle paramBundle, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
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
        //   15: aload 5
        //   17: aload_1
        //   18: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   21: aload_2
        //   22: ifnull +57 -> 79
        //   25: aload 5
        //   27: iconst_1
        //   28: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   31: aload_2
        //   32: aload 5
        //   34: iconst_0
        //   35: invokevirtual 52	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   38: aload 4
        //   40: astore_1
        //   41: aload_3
        //   42: ifnull +10 -> 52
        //   45: aload_3
        //   46: invokeinterface 58 1 0
        //   51: astore_1
        //   52: aload 5
        //   54: aload_1
        //   55: invokevirtual 42	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   58: aload_0
        //   59: getfield 19	android/service/media/IMediaBrowserService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   62: iconst_1
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 64 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 67	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   85: goto -47 -> 38
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 67	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramBundle	Bundle
        //   0	96	3	paramIMediaBrowserServiceCallbacks	IMediaBrowserServiceCallbacks
        //   1	38	4	localObject	Object
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	21	88	finally
        //   25	38	88	finally
        //   45	52	88	finally
        //   52	73	88	finally
        //   79	85	88	finally
      }
      
      public void disconnect(IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.media.IMediaBrowserService");
          if (paramIMediaBrowserServiceCallbacks != null) {
            localIBinder = paramIMediaBrowserServiceCallbacks.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.media.IMediaBrowserService";
      }
      
      /* Error */
      public void getMediaItem(String paramString, ResultReceiver paramResultReceiver, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
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
        //   15: aload 5
        //   17: aload_1
        //   18: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   21: aload_2
        //   22: ifnull +57 -> 79
        //   25: aload 5
        //   27: iconst_1
        //   28: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   31: aload_2
        //   32: aload 5
        //   34: iconst_0
        //   35: invokevirtual 81	android/os/ResultReceiver:writeToParcel	(Landroid/os/Parcel;I)V
        //   38: aload 4
        //   40: astore_1
        //   41: aload_3
        //   42: ifnull +10 -> 52
        //   45: aload_3
        //   46: invokeinterface 58 1 0
        //   51: astore_1
        //   52: aload 5
        //   54: aload_1
        //   55: invokevirtual 42	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   58: aload_0
        //   59: getfield 19	android/service/media/IMediaBrowserService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   62: iconst_5
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 64 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 67	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 46	android/os/Parcel:writeInt	(I)V
        //   85: goto -47 -> 38
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 67	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramResultReceiver	ResultReceiver
        //   0	96	3	paramIMediaBrowserServiceCallbacks	IMediaBrowserServiceCallbacks
        //   1	38	4	localObject	Object
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	21	88	finally
        //   25	38	88	finally
        //   45	52	88	finally
        //   52	73	88	finally
        //   79	85	88	finally
      }
      
      public void removeSubscription(String paramString, IBinder paramIBinder, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.media.IMediaBrowserService");
          localParcel.writeString(paramString);
          localParcel.writeStrongBinder(paramIBinder);
          paramString = (String)localObject;
          if (paramIMediaBrowserServiceCallbacks != null) {
            paramString = paramIMediaBrowserServiceCallbacks.asBinder();
          }
          localParcel.writeStrongBinder(paramString);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeSubscriptionDeprecated(String paramString, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.media.IMediaBrowserService");
          localParcel.writeString(paramString);
          paramString = (String)localObject;
          if (paramIMediaBrowserServiceCallbacks != null) {
            paramString = paramIMediaBrowserServiceCallbacks.asBinder();
          }
          localParcel.writeStrongBinder(paramString);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/media/IMediaBrowserService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */