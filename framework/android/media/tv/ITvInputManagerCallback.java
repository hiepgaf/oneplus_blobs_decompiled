package android.media.tv;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ITvInputManagerCallback
  extends IInterface
{
  public abstract void onInputAdded(String paramString)
    throws RemoteException;
  
  public abstract void onInputRemoved(String paramString)
    throws RemoteException;
  
  public abstract void onInputStateChanged(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onInputUpdated(String paramString)
    throws RemoteException;
  
  public abstract void onTvInputInfoUpdated(TvInputInfo paramTvInputInfo)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITvInputManagerCallback
  {
    private static final String DESCRIPTOR = "android.media.tv.ITvInputManagerCallback";
    static final int TRANSACTION_onInputAdded = 1;
    static final int TRANSACTION_onInputRemoved = 2;
    static final int TRANSACTION_onInputStateChanged = 4;
    static final int TRANSACTION_onInputUpdated = 3;
    static final int TRANSACTION_onTvInputInfoUpdated = 5;
    
    public Stub()
    {
      attachInterface(this, "android.media.tv.ITvInputManagerCallback");
    }
    
    public static ITvInputManagerCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.tv.ITvInputManagerCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ITvInputManagerCallback))) {
        return (ITvInputManagerCallback)localIInterface;
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
        paramParcel2.writeString("android.media.tv.ITvInputManagerCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManagerCallback");
        onInputAdded(paramParcel1.readString());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManagerCallback");
        onInputRemoved(paramParcel1.readString());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManagerCallback");
        onInputUpdated(paramParcel1.readString());
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputManagerCallback");
        onInputStateChanged(paramParcel1.readString(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.media.tv.ITvInputManagerCallback");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (TvInputInfo)TvInputInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        onTvInputInfoUpdated(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements ITvInputManagerCallback
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
        return "android.media.tv.ITvInputManagerCallback";
      }
      
      public void onInputAdded(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputManagerCallback");
          localParcel.writeString(paramString);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onInputRemoved(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputManagerCallback");
          localParcel.writeString(paramString);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onInputStateChanged(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputManagerCallback");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onInputUpdated(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputManagerCallback");
          localParcel.writeString(paramString);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onTvInputInfoUpdated(TvInputInfo paramTvInputInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 68	android/media/tv/TvInputInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputManagerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_5
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 48 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 51	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 59	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 51	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramTvInputInfo	TvInputInfo
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvInputManagerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */