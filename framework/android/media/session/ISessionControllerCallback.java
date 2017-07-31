package android.media.session;

import android.content.pm.ParceledListSlice;
import android.media.MediaMetadata;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.text.TextUtils;

public abstract interface ISessionControllerCallback
  extends IInterface
{
  public abstract void onEvent(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onExtrasChanged(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onMetadataChanged(MediaMetadata paramMediaMetadata)
    throws RemoteException;
  
  public abstract void onPlayItemResponse(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onPlaybackStateChanged(PlaybackState paramPlaybackState)
    throws RemoteException;
  
  public abstract void onQueueChanged(ParceledListSlice paramParceledListSlice)
    throws RemoteException;
  
  public abstract void onQueueTitleChanged(CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract void onSessionDestroyed()
    throws RemoteException;
  
  public abstract void onUpdateFolderInfoBrowsedPlayer(String paramString)
    throws RemoteException;
  
  public abstract void onUpdateNowPlayingContentChange()
    throws RemoteException;
  
  public abstract void onUpdateNowPlayingEntries(long[] paramArrayOfLong)
    throws RemoteException;
  
  public abstract void onVolumeInfoChanged(ParcelableVolumeInfo paramParcelableVolumeInfo)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISessionControllerCallback
  {
    private static final String DESCRIPTOR = "android.media.session.ISessionControllerCallback";
    static final int TRANSACTION_onEvent = 1;
    static final int TRANSACTION_onExtrasChanged = 7;
    static final int TRANSACTION_onMetadataChanged = 4;
    static final int TRANSACTION_onPlayItemResponse = 9;
    static final int TRANSACTION_onPlaybackStateChanged = 3;
    static final int TRANSACTION_onQueueChanged = 5;
    static final int TRANSACTION_onQueueTitleChanged = 6;
    static final int TRANSACTION_onSessionDestroyed = 2;
    static final int TRANSACTION_onUpdateFolderInfoBrowsedPlayer = 11;
    static final int TRANSACTION_onUpdateNowPlayingContentChange = 12;
    static final int TRANSACTION_onUpdateNowPlayingEntries = 10;
    static final int TRANSACTION_onVolumeInfoChanged = 8;
    
    public Stub()
    {
      attachInterface(this, "android.media.session.ISessionControllerCallback");
    }
    
    public static ISessionControllerCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.session.ISessionControllerCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ISessionControllerCallback))) {
        return (ISessionControllerCallback)localIInterface;
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
        paramParcel2.writeString("android.media.session.ISessionControllerCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onEvent(paramParcel2, paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        onSessionDestroyed();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PlaybackState)PlaybackState.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPlaybackStateChanged(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (MediaMetadata)MediaMetadata.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onMetadataChanged(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParceledListSlice)ParceledListSlice.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onQueueChanged(paramParcel1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onQueueTitleChanged(paramParcel1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onExtrasChanged(paramParcel1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParcelableVolumeInfo)ParcelableVolumeInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onVolumeInfoChanged(paramParcel1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        if (paramParcel1.readInt() != 0) {}
        for (boolean bool = true;; bool = false)
        {
          onPlayItemResponse(bool);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        onUpdateNowPlayingEntries(paramParcel1.createLongArray());
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
        onUpdateFolderInfoBrowsedPlayer(paramParcel1.readString());
        return true;
      }
      paramParcel1.enforceInterface("android.media.session.ISessionControllerCallback");
      onUpdateNowPlayingContentChange();
      return true;
    }
    
    private static class Proxy
      implements ISessionControllerCallback
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
        return "android.media.session.ISessionControllerCallback";
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
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 53	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 59 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 62	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 62	android/os/Parcel:recycle	()V
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
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 53	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 7
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 59 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 62	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 62	android/os/Parcel:recycle	()V
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
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      /* Error */
      public void onMetadataChanged(MediaMetadata paramMediaMetadata)
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
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 70	android/media/MediaMetadata:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_4
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 59 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 62	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramMediaMetadata	MediaMetadata
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void onPlayItemResponse(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +28 -> 41
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: bipush 9
        //   27: aload_3
        //   28: aconst_null
        //   29: iconst_1
        //   30: invokeinterface 59 5 0
        //   35: pop
        //   36: aload_3
        //   37: invokevirtual 62	android/os/Parcel:recycle	()V
        //   40: return
        //   41: iconst_0
        //   42: istore_2
        //   43: goto -27 -> 16
        //   46: astore 4
        //   48: aload_3
        //   49: invokevirtual 62	android/os/Parcel:recycle	()V
        //   52: aload 4
        //   54: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	55	0	this	Proxy
        //   0	55	1	paramBoolean	boolean
        //   1	42	2	i	int
        //   5	44	3	localParcel	Parcel
        //   46	7	4	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   6	12	46	finally
        //   16	36	46	finally
      }
      
      /* Error */
      public void onPlaybackStateChanged(PlaybackState paramPlaybackState)
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
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 77	android/media/session/PlaybackState:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 59 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 62	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramPlaybackState	PlaybackState
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void onQueueChanged(ParceledListSlice paramParceledListSlice)
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
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 82	android/content/pm/ParceledListSlice:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_5
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 59 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 62	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 62	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramParceledListSlice	ParceledListSlice
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
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
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokestatic 89	android/text/TextUtils:writeToParcel	(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 6
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 59 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 62	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 62	android/os/Parcel:recycle	()V
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
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void onSessionDestroyed()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionControllerCallback");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onUpdateFolderInfoBrowsedPlayer(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionControllerCallback");
          localParcel.writeString(paramString);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onUpdateNowPlayingContentChange()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionControllerCallback");
          this.mRemote.transact(12, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onUpdateNowPlayingEntries(long[] paramArrayOfLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionControllerCallback");
          localParcel.writeLongArray(paramArrayOfLong);
          this.mRemote.transact(10, localParcel, null, 1);
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
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 102	android/media/session/ParcelableVolumeInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/session/ISessionControllerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 8
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 59 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 62	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 47	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 62	android/os/Parcel:recycle	()V
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
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/ISessionControllerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */