package android.media.tv;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface ITvInputSessionCallback
  extends IInterface
{
  public abstract void onChannelRetuned(Uri paramUri)
    throws RemoteException;
  
  public abstract void onContentAllowed()
    throws RemoteException;
  
  public abstract void onContentBlocked(String paramString)
    throws RemoteException;
  
  public abstract void onError(int paramInt)
    throws RemoteException;
  
  public abstract void onLayoutSurface(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract void onRecordingStopped(Uri paramUri)
    throws RemoteException;
  
  public abstract void onSessionCreated(ITvInputSession paramITvInputSession, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void onSessionEvent(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onTimeShiftCurrentPositionChanged(long paramLong)
    throws RemoteException;
  
  public abstract void onTimeShiftStartPositionChanged(long paramLong)
    throws RemoteException;
  
  public abstract void onTimeShiftStatusChanged(int paramInt)
    throws RemoteException;
  
  public abstract void onTrackSelected(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void onTracksChanged(List<TvTrackInfo> paramList)
    throws RemoteException;
  
  public abstract void onTuned(Uri paramUri)
    throws RemoteException;
  
  public abstract void onVideoAvailable()
    throws RemoteException;
  
  public abstract void onVideoUnavailable(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITvInputSessionCallback
  {
    private static final String DESCRIPTOR = "android.media.tv.ITvInputSessionCallback";
    static final int TRANSACTION_onChannelRetuned = 3;
    static final int TRANSACTION_onContentAllowed = 8;
    static final int TRANSACTION_onContentBlocked = 9;
    static final int TRANSACTION_onError = 16;
    static final int TRANSACTION_onLayoutSurface = 10;
    static final int TRANSACTION_onRecordingStopped = 15;
    static final int TRANSACTION_onSessionCreated = 1;
    static final int TRANSACTION_onSessionEvent = 2;
    static final int TRANSACTION_onTimeShiftCurrentPositionChanged = 13;
    static final int TRANSACTION_onTimeShiftStartPositionChanged = 12;
    static final int TRANSACTION_onTimeShiftStatusChanged = 11;
    static final int TRANSACTION_onTrackSelected = 5;
    static final int TRANSACTION_onTracksChanged = 4;
    static final int TRANSACTION_onTuned = 14;
    static final int TRANSACTION_onVideoAvailable = 6;
    static final int TRANSACTION_onVideoUnavailable = 7;
    
    public Stub()
    {
      attachInterface(this, "android.media.tv.ITvInputSessionCallback");
    }
    
    public static ITvInputSessionCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.tv.ITvInputSessionCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ITvInputSessionCallback))) {
        return (ITvInputSessionCallback)localIInterface;
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
        paramParcel2.writeString("android.media.tv.ITvInputSessionCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onSessionCreated(ITvInputSession.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readStrongBinder());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onSessionEvent(paramParcel2, paramParcel1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onChannelRetuned(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onTracksChanged(paramParcel1.createTypedArrayList(TvTrackInfo.CREATOR));
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onTrackSelected(paramParcel1.readInt(), paramParcel1.readString());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onVideoAvailable();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onVideoUnavailable(paramParcel1.readInt());
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onContentAllowed();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onContentBlocked(paramParcel1.readString());
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onLayoutSurface(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onTimeShiftStatusChanged(paramParcel1.readInt());
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onTimeShiftStartPositionChanged(paramParcel1.readLong());
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        onTimeShiftCurrentPositionChanged(paramParcel1.readLong());
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onTuned(paramParcel1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onRecordingStopped(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.media.tv.ITvInputSessionCallback");
      onError(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ITvInputSessionCallback
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
        return "android.media.tv.ITvInputSessionCallback";
      }
      
      /* Error */
      public void onChannelRetuned(Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 56 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 59	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramUri	Uri
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void onContentAllowed()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onContentBlocked(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeString(paramString);
          this.mRemote.transact(9, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onError(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(16, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onLayoutSurface(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeInt(paramInt4);
          this.mRemote.transact(10, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onRecordingStopped(Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 15
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 56 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 59	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 59	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramUri	Uri
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void onSessionCreated(ITvInputSession paramITvInputSession, IBinder paramIBinder)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          if (paramITvInputSession != null) {
            localIBinder = paramITvInputSession.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeStrongBinder(paramIBinder);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onSessionEvent(String paramString, Bundle paramBundle)
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
        //   12: invokevirtual 65	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 83	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/tv/ITvInputSessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_2
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramString	String
        //   0	64	2	paramBundle	Bundle
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
      
      public void onTimeShiftCurrentPositionChanged(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(13, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTimeShiftStartPositionChanged(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(12, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTimeShiftStatusChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTrackSelected(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeInt(paramInt);
          localParcel.writeString(paramString);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTracksChanged(List<TvTrackInfo> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeTypedList(paramList);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onTuned(Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 14
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 56 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 59	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 59	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramUri	Uri
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void onVideoAvailable()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onVideoUnavailable(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSessionCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(7, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvInputSessionCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */