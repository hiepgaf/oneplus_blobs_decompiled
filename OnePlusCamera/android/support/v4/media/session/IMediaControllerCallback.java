package android.support.v4.media.session;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import java.util.List;

public abstract interface IMediaControllerCallback
  extends IInterface
{
  public abstract void onEvent(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onExtrasChanged(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onMetadataChanged(MediaMetadataCompat paramMediaMetadataCompat)
    throws RemoteException;
  
  public abstract void onPlaybackStateChanged(PlaybackStateCompat paramPlaybackStateCompat)
    throws RemoteException;
  
  public abstract void onQueueChanged(List<MediaSessionCompat.QueueItem> paramList)
    throws RemoteException;
  
  public abstract void onQueueTitleChanged(CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract void onSessionDestroyed()
    throws RemoteException;
  
  public abstract void onVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaControllerCallback
  {
    private static final String DESCRIPTOR = "android.support.v4.media.session.IMediaControllerCallback";
    static final int TRANSACTION_onEvent = 1;
    static final int TRANSACTION_onExtrasChanged = 7;
    static final int TRANSACTION_onMetadataChanged = 4;
    static final int TRANSACTION_onPlaybackStateChanged = 3;
    static final int TRANSACTION_onQueueChanged = 5;
    static final int TRANSACTION_onQueueTitleChanged = 6;
    static final int TRANSACTION_onSessionDestroyed = 2;
    static final int TRANSACTION_onVolumeInfoChanged = 8;
    
    public Stub()
    {
      attachInterface(this, "android.support.v4.media.session.IMediaControllerCallback");
    }
    
    public static IMediaControllerCallback asInterface(IBinder paramIBinder)
    {
      IInterface localIInterface;
      if (paramIBinder != null)
      {
        localIInterface = paramIBinder.queryLocalInterface("android.support.v4.media.session.IMediaControllerCallback");
        if (localIInterface != null) {
          break label28;
        }
      }
      label28:
      while (!(localIInterface instanceof IMediaControllerCallback))
      {
        return new Proxy(paramIBinder);
        return null;
      }
      return (IMediaControllerCallback)localIInterface;
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      Object localObject5 = null;
      Object localObject6 = null;
      Object localObject1 = null;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.support.v4.media.session.IMediaControllerCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject1;; paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1))
        {
          onEvent(paramParcel2, paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
        onSessionDestroyed();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject2;; paramParcel1 = (PlaybackStateCompat)PlaybackStateCompat.CREATOR.createFromParcel(paramParcel1))
        {
          onPlaybackStateChanged(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject3;; paramParcel1 = (MediaMetadataCompat)MediaMetadataCompat.CREATOR.createFromParcel(paramParcel1))
        {
          onMetadataChanged(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
        onQueueChanged(paramParcel1.createTypedArrayList(MediaSessionCompat.QueueItem.CREATOR));
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject4;; paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1))
        {
          onQueueTitleChanged(paramParcel1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject5;; paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1))
        {
          onExtrasChanged(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.support.v4.media.session.IMediaControllerCallback");
      if (paramParcel1.readInt() == 0) {}
      for (paramParcel1 = (Parcel)localObject6;; paramParcel1 = (ParcelableVolumeInfo)ParcelableVolumeInfo.CREATOR.createFromParcel(paramParcel1))
      {
        onVolumeInfoChanged(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IMediaControllerCallback
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
        return "android.support.v4.media.session.IMediaControllerCallback";
      }
      
      /* Error */
      public void onEvent(String paramString, Bundle paramBundle)
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
        //   16: ifnonnull +27 -> 43
        //   19: aload_3
        //   20: iconst_0
        //   21: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   24: aload_0
        //   25: getfield 19	android/support/v4/media/session/IMediaControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   28: iconst_1
        //   29: aload_3
        //   30: aconst_null
        //   31: iconst_1
        //   32: invokeinterface 53 5 0
        //   37: pop
        //   38: aload_3
        //   39: invokevirtual 56	android/os/Parcel:recycle	()V
        //   42: return
        //   43: aload_3
        //   44: iconst_1
        //   45: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   48: aload_2
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 62	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   54: goto -30 -> 24
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 56	android/os/Parcel:recycle	()V
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
        //   19	24	57	finally
        //   24	38	57	finally
        //   43	54	57	finally
      }
      
      /* Error */
      public void onExtrasChanged(Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnonnull +28 -> 39
        //   14: aload_2
        //   15: iconst_0
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_0
        //   20: getfield 19	android/support/v4/media/session/IMediaControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 7
        //   25: aload_2
        //   26: aconst_null
        //   27: iconst_1
        //   28: invokeinterface 53 5 0
        //   33: pop
        //   34: aload_2
        //   35: invokevirtual 56	android/os/Parcel:recycle	()V
        //   38: return
        //   39: aload_2
        //   40: iconst_1
        //   41: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   44: aload_1
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 62	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   50: goto -31 -> 19
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 56	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramBundle	Bundle
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	19	53	finally
        //   19	34	53	finally
        //   39	50	53	finally
      }
      
      /* Error */
      public void onMetadataChanged(MediaMetadataCompat paramMediaMetadataCompat)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnonnull +27 -> 38
        //   14: aload_2
        //   15: iconst_0
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_0
        //   20: getfield 19	android/support/v4/media/session/IMediaControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_4
        //   24: aload_2
        //   25: aconst_null
        //   26: iconst_1
        //   27: invokeinterface 53 5 0
        //   32: pop
        //   33: aload_2
        //   34: invokevirtual 56	android/os/Parcel:recycle	()V
        //   37: return
        //   38: aload_2
        //   39: iconst_1
        //   40: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   43: aload_1
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 70	android/support/v4/media/MediaMetadataCompat:writeToParcel	(Landroid/os/Parcel;I)V
        //   49: goto -30 -> 19
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 56	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramMediaMetadataCompat	MediaMetadataCompat
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	19	52	finally
        //   19	33	52	finally
        //   38	49	52	finally
      }
      
      /* Error */
      public void onPlaybackStateChanged(PlaybackStateCompat paramPlaybackStateCompat)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnonnull +27 -> 38
        //   14: aload_2
        //   15: iconst_0
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_0
        //   20: getfield 19	android/support/v4/media/session/IMediaControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_3
        //   24: aload_2
        //   25: aconst_null
        //   26: iconst_1
        //   27: invokeinterface 53 5 0
        //   32: pop
        //   33: aload_2
        //   34: invokevirtual 56	android/os/Parcel:recycle	()V
        //   37: return
        //   38: aload_2
        //   39: iconst_1
        //   40: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   43: aload_1
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 75	android/support/v4/media/session/PlaybackStateCompat:writeToParcel	(Landroid/os/Parcel;I)V
        //   49: goto -30 -> 19
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 56	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramPlaybackStateCompat	PlaybackStateCompat
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	19	52	finally
        //   19	33	52	finally
        //   38	49	52	finally
      }
      
      public void onQueueChanged(List<MediaSessionCompat.QueueItem> paramList)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
          localParcel.writeTypedList(paramList);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onQueueTitleChanged(CharSequence paramCharSequence)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnonnull +28 -> 39
        //   14: aload_2
        //   15: iconst_0
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_0
        //   20: getfield 19	android/support/v4/media/session/IMediaControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 6
        //   25: aload_2
        //   26: aconst_null
        //   27: iconst_1
        //   28: invokeinterface 53 5 0
        //   33: pop
        //   34: aload_2
        //   35: invokevirtual 56	android/os/Parcel:recycle	()V
        //   38: return
        //   39: aload_2
        //   40: iconst_1
        //   41: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   44: aload_1
        //   45: aload_2
        //   46: iconst_0
        //   47: invokestatic 89	android/text/TextUtils:writeToParcel	(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V
        //   50: goto -31 -> 19
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 56	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramCharSequence	CharSequence
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	19	53	finally
        //   19	34	53	finally
        //   39	50	53	finally
      }
      
      public void onSessionDestroyed()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.support.v4.media.session.IMediaControllerCallback");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnonnull +28 -> 39
        //   14: aload_2
        //   15: iconst_0
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_0
        //   20: getfield 19	android/support/v4/media/session/IMediaControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 8
        //   25: aload_2
        //   26: aconst_null
        //   27: iconst_1
        //   28: invokeinterface 53 5 0
        //   33: pop
        //   34: aload_2
        //   35: invokevirtual 56	android/os/Parcel:recycle	()V
        //   38: return
        //   39: aload_2
        //   40: iconst_1
        //   41: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   44: aload_1
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 95	android/support/v4/media/session/ParcelableVolumeInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   50: goto -31 -> 19
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 56	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramParcelableVolumeInfo	ParcelableVolumeInfo
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	19	53	finally
        //   19	34	53	finally
        //   39	50	53	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/session/IMediaControllerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */