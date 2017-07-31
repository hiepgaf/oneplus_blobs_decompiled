package android.service.media;

import android.content.pm.ParceledListSlice;
import android.media.session.MediaSession.Token;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IMediaBrowserServiceCallbacks
  extends IInterface
{
  public abstract void onConnect(String paramString, MediaSession.Token paramToken, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onConnectFailed()
    throws RemoteException;
  
  public abstract void onLoadChildren(String paramString, ParceledListSlice paramParceledListSlice)
    throws RemoteException;
  
  public abstract void onLoadChildrenWithOptions(String paramString, ParceledListSlice paramParceledListSlice, Bundle paramBundle)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaBrowserServiceCallbacks
  {
    private static final String DESCRIPTOR = "android.service.media.IMediaBrowserServiceCallbacks";
    static final int TRANSACTION_onConnect = 1;
    static final int TRANSACTION_onConnectFailed = 2;
    static final int TRANSACTION_onLoadChildren = 3;
    static final int TRANSACTION_onLoadChildrenWithOptions = 4;
    
    public Stub()
    {
      attachInterface(this, "android.service.media.IMediaBrowserServiceCallbacks");
    }
    
    public static IMediaBrowserServiceCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.media.IMediaBrowserServiceCallbacks");
      if ((localIInterface != null) && ((localIInterface instanceof IMediaBrowserServiceCallbacks))) {
        return (IMediaBrowserServiceCallbacks)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.media.IMediaBrowserServiceCallbacks");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserServiceCallbacks");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (MediaSession.Token)MediaSession.Token.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label137;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onConnect(str, paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserServiceCallbacks");
        onConnectFailed();
        return true;
      case 3: 
        label137:
        paramParcel1.enforceInterface("android.service.media.IMediaBrowserServiceCallbacks");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onLoadChildren(paramParcel2, paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.service.media.IMediaBrowserServiceCallbacks");
      String str = paramParcel1.readString();
      if (paramParcel1.readInt() != 0)
      {
        paramParcel2 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);
        if (paramParcel1.readInt() == 0) {
          break label265;
        }
      }
      label265:
      for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onLoadChildrenWithOptions(str, paramParcel2, paramParcel1);
        return true;
        paramParcel2 = null;
        break;
      }
    }
    
    private static class Proxy
      implements IMediaBrowserServiceCallbacks
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
        return "android.service.media.IMediaBrowserServiceCallbacks";
      }
      
      public void onConnect(String paramString, MediaSession.Token paramToken, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.service.media.IMediaBrowserServiceCallbacks");
            localParcel.writeString(paramString);
            if (paramToken != null)
            {
              localParcel.writeInt(1);
              paramToken.writeToParcel(localParcel, 0);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(1, localParcel, null, 1);
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
      
      public void onConnectFailed()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.service.media.IMediaBrowserServiceCallbacks");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onLoadChildren(String paramString, ParceledListSlice paramParceledListSlice)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 72	android/content/pm/ParceledListSlice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/service/media/IMediaBrowserServiceCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_3
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 62 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 65	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 65	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramString	String
        //   0	64	2	paramParceledListSlice	ParceledListSlice
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
      
      public void onLoadChildrenWithOptions(String paramString, ParceledListSlice paramParceledListSlice, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.service.media.IMediaBrowserServiceCallbacks");
            localParcel.writeString(paramString);
            if (paramParceledListSlice != null)
            {
              localParcel.writeInt(1);
              paramParceledListSlice.writeToParcel(localParcel, 0);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(4, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/media/IMediaBrowserServiceCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */