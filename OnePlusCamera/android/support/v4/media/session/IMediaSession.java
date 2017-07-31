package android.support.v4.media.session;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public abstract interface IMediaSession
  extends IInterface
{
  public abstract void adjustVolume(int paramInt1, int paramInt2, String paramString)
    throws RemoteException;
  
  public abstract void fastForward()
    throws RemoteException;
  
  public abstract Bundle getExtras()
    throws RemoteException;
  
  public abstract long getFlags()
    throws RemoteException;
  
  public abstract PendingIntent getLaunchPendingIntent()
    throws RemoteException;
  
  public abstract MediaMetadataCompat getMetadata()
    throws RemoteException;
  
  public abstract String getPackageName()
    throws RemoteException;
  
  public abstract PlaybackStateCompat getPlaybackState()
    throws RemoteException;
  
  public abstract List<MediaSessionCompat.QueueItem> getQueue()
    throws RemoteException;
  
  public abstract CharSequence getQueueTitle()
    throws RemoteException;
  
  public abstract int getRatingType()
    throws RemoteException;
  
  public abstract String getTag()
    throws RemoteException;
  
  public abstract ParcelableVolumeInfo getVolumeAttributes()
    throws RemoteException;
  
  public abstract boolean isTransportControlEnabled()
    throws RemoteException;
  
  public abstract void next()
    throws RemoteException;
  
  public abstract void pause()
    throws RemoteException;
  
  public abstract void play()
    throws RemoteException;
  
  public abstract void playFromMediaId(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void playFromSearch(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void previous()
    throws RemoteException;
  
  public abstract void rate(RatingCompat paramRatingCompat)
    throws RemoteException;
  
  public abstract void registerCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
    throws RemoteException;
  
  public abstract void rewind()
    throws RemoteException;
  
  public abstract void seekTo(long paramLong)
    throws RemoteException;
  
  public abstract void sendCommand(String paramString, Bundle paramBundle, MediaSessionCompat.ResultReceiverWrapper paramResultReceiverWrapper)
    throws RemoteException;
  
  public abstract void sendCustomAction(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean sendMediaButton(KeyEvent paramKeyEvent)
    throws RemoteException;
  
  public abstract void setVolumeTo(int paramInt1, int paramInt2, String paramString)
    throws RemoteException;
  
  public abstract void skipToQueueItem(long paramLong)
    throws RemoteException;
  
  public abstract void stop()
    throws RemoteException;
  
  public abstract void unregisterCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMediaSession
  {
    private static final String DESCRIPTOR = "android.support.v4.media.session.IMediaSession";
    static final int TRANSACTION_adjustVolume = 11;
    static final int TRANSACTION_fastForward = 21;
    static final int TRANSACTION_getExtras = 30;
    static final int TRANSACTION_getFlags = 9;
    static final int TRANSACTION_getLaunchPendingIntent = 8;
    static final int TRANSACTION_getMetadata = 26;
    static final int TRANSACTION_getPackageName = 6;
    static final int TRANSACTION_getPlaybackState = 27;
    static final int TRANSACTION_getQueue = 28;
    static final int TRANSACTION_getQueueTitle = 29;
    static final int TRANSACTION_getRatingType = 31;
    static final int TRANSACTION_getTag = 7;
    static final int TRANSACTION_getVolumeAttributes = 10;
    static final int TRANSACTION_isTransportControlEnabled = 5;
    static final int TRANSACTION_next = 19;
    static final int TRANSACTION_pause = 17;
    static final int TRANSACTION_play = 13;
    static final int TRANSACTION_playFromMediaId = 14;
    static final int TRANSACTION_playFromSearch = 15;
    static final int TRANSACTION_previous = 20;
    static final int TRANSACTION_rate = 24;
    static final int TRANSACTION_registerCallbackListener = 3;
    static final int TRANSACTION_rewind = 22;
    static final int TRANSACTION_seekTo = 23;
    static final int TRANSACTION_sendCommand = 1;
    static final int TRANSACTION_sendCustomAction = 25;
    static final int TRANSACTION_sendMediaButton = 2;
    static final int TRANSACTION_setVolumeTo = 12;
    static final int TRANSACTION_skipToQueueItem = 16;
    static final int TRANSACTION_stop = 18;
    static final int TRANSACTION_unregisterCallbackListener = 4;
    
    public Stub()
    {
      attachInterface(this, "android.support.v4.media.session.IMediaSession");
    }
    
    public static IMediaSession asInterface(IBinder paramIBinder)
    {
      IInterface localIInterface;
      if (paramIBinder != null)
      {
        localIInterface = paramIBinder.queryLocalInterface("android.support.v4.media.session.IMediaSession");
        if (localIInterface != null) {
          break label28;
        }
      }
      label28:
      while (!(localIInterface instanceof IMediaSession))
      {
        return new Proxy(paramIBinder);
        return null;
      }
      return (IMediaSession)localIInterface;
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      Object localObject3 = null;
      Object localObject1 = null;
      String str2 = null;
      Object localObject4 = null;
      Object localObject2 = null;
      String str1 = null;
      int i = 0;
      label370:
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.support.v4.media.session.IMediaSession");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        str2 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0)
        {
          localObject1 = null;
          if (paramParcel1.readInt() != 0) {
            break label370;
          }
        }
        for (paramParcel1 = str1;; paramParcel1 = (MediaSessionCompat.ResultReceiverWrapper)MediaSessionCompat.ResultReceiverWrapper.CREATOR.createFromParcel(paramParcel1))
        {
          sendCommand(str2, (Bundle)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        if (paramParcel1.readInt() == 0)
        {
          paramParcel1 = (Parcel)localObject3;
          bool = sendMediaButton(paramParcel1);
          paramParcel2.writeNoException();
          if (bool) {
            break label443;
          }
        }
        for (paramInt1 = 0;; paramInt1 = 1)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = (KeyEvent)KeyEvent.CREATOR.createFromParcel(paramParcel1);
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        registerCallbackListener(IMediaControllerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        unregisterCallbackListener(IMediaControllerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        bool = isTransportControlEnabled();
        paramParcel2.writeNoException();
        if (!bool) {}
        for (paramInt1 = i;; paramInt1 = 1)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getPackageName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getTag();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getLaunchPendingIntent();
        paramParcel2.writeNoException();
        if (paramParcel1 == null)
        {
          paramParcel2.writeInt(0);
          return true;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        long l = getFlags();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getVolumeAttributes();
        paramParcel2.writeNoException();
        if (paramParcel1 == null)
        {
          paramParcel2.writeInt(0);
          return true;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        adjustVolume(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        setVolumeTo(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        play();
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject1;; paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1))
        {
          playFromMediaId(str1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = str2;; paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1))
        {
          playFromSearch((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        skipToQueueItem(paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        pause();
        paramParcel2.writeNoException();
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        stop();
        paramParcel2.writeNoException();
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        next();
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        previous();
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        fastForward();
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        rewind();
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        seekTo(paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject4;; paramParcel1 = (RatingCompat)RatingCompat.CREATOR.createFromParcel(paramParcel1))
        {
          rate(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 25: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() == 0) {}
        for (paramParcel1 = (Parcel)localObject2;; paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1))
        {
          sendCustomAction((String)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 26: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getMetadata();
        paramParcel2.writeNoException();
        if (paramParcel1 == null)
        {
          paramParcel2.writeInt(0);
          return true;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getPlaybackState();
        paramParcel2.writeNoException();
        if (paramParcel1 == null)
        {
          paramParcel2.writeInt(0);
          return true;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getQueue();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getQueueTitle();
        paramParcel2.writeNoException();
        if (paramParcel1 == null)
        {
          paramParcel2.writeInt(0);
          return true;
        }
        paramParcel2.writeInt(1);
        TextUtils.writeToParcel(paramParcel1, paramParcel2, 1);
        return true;
      case 30: 
        label443:
        paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
        paramParcel1 = getExtras();
        paramParcel2.writeNoException();
        if (paramParcel1 == null)
        {
          paramParcel2.writeInt(0);
          return true;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
        return true;
      }
      paramParcel1.enforceInterface("android.support.v4.media.session.IMediaSession");
      paramInt1 = getRatingType();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IMediaSession
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void adjustVolume(int paramInt1, int paramInt2, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
      
      public void fastForward()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
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
      public Bundle getExtras()
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
        //   16: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 30
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifne +16 -> 59
        //   46: aconst_null
        //   47: astore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: areturn
        //   59: getstatic 71	android/os/Bundle:CREATOR	Landroid/os/Parcelable$Creator;
        //   62: aload 4
        //   64: invokeinterface 77 2 0
        //   69: checkcast 67	android/os/Bundle
        //   72: astore_2
        //   73: goto -25 -> 48
        //   76: astore_2
        //   77: aload 4
        //   79: invokevirtual 55	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: aload_2
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   41	2	1	i	int
        //   47	26	2	localBundle	Bundle
        //   76	11	2	localObject	Object
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	42	76	finally
        //   59	73	76	finally
      }
      
      public long getFlags()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.support.v4.media.session.IMediaSession";
      }
      
      /* Error */
      public PendingIntent getLaunchPendingIntent()
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
        //   16: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 8
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifne +16 -> 59
        //   46: aconst_null
        //   47: astore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: areturn
        //   59: getstatic 89	android/app/PendingIntent:CREATOR	Landroid/os/Parcelable$Creator;
        //   62: aload 4
        //   64: invokeinterface 77 2 0
        //   69: checkcast 88	android/app/PendingIntent
        //   72: astore_2
        //   73: goto -25 -> 48
        //   76: astore_2
        //   77: aload 4
        //   79: invokevirtual 55	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: aload_2
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   41	2	1	i	int
        //   47	26	2	localPendingIntent	PendingIntent
        //   76	11	2	localObject	Object
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	42	76	finally
        //   59	73	76	finally
      }
      
      /* Error */
      public MediaMetadataCompat getMetadata()
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
        //   16: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 26
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifne +16 -> 59
        //   46: aconst_null
        //   47: astore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: areturn
        //   59: getstatic 94	android/support/v4/media/MediaMetadataCompat:CREATOR	Landroid/os/Parcelable$Creator;
        //   62: aload 4
        //   64: invokeinterface 77 2 0
        //   69: checkcast 93	android/support/v4/media/MediaMetadataCompat
        //   72: astore_2
        //   73: goto -25 -> 48
        //   76: astore_2
        //   77: aload 4
        //   79: invokevirtual 55	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: aload_2
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   41	2	1	i	int
        //   47	26	2	localMediaMetadataCompat	MediaMetadataCompat
        //   76	11	2	localObject	Object
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	42	76	finally
        //   59	73	76	finally
      }
      
      public String getPackageName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public PlaybackStateCompat getPlaybackState()
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
        //   16: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 27
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifne +16 -> 59
        //   46: aconst_null
        //   47: astore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: areturn
        //   59: getstatic 103	android/support/v4/media/session/PlaybackStateCompat:CREATOR	Landroid/os/Parcelable$Creator;
        //   62: aload 4
        //   64: invokeinterface 77 2 0
        //   69: checkcast 102	android/support/v4/media/session/PlaybackStateCompat
        //   72: astore_2
        //   73: goto -25 -> 48
        //   76: astore_2
        //   77: aload 4
        //   79: invokevirtual 55	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: aload_2
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   41	2	1	i	int
        //   47	26	2	localPlaybackStateCompat	PlaybackStateCompat
        //   76	11	2	localObject	Object
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	42	76	finally
        //   59	73	76	finally
      }
      
      public List<MediaSessionCompat.QueueItem> getQueue()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(MediaSessionCompat.QueueItem.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public CharSequence getQueueTitle()
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
        //   16: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 29
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifne +16 -> 59
        //   46: aconst_null
        //   47: astore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: areturn
        //   59: getstatic 121	android/text/TextUtils:CHAR_SEQUENCE_CREATOR	Landroid/os/Parcelable$Creator;
        //   62: aload 4
        //   64: invokeinterface 77 2 0
        //   69: checkcast 123	java/lang/CharSequence
        //   72: astore_2
        //   73: goto -25 -> 48
        //   76: astore_2
        //   77: aload 4
        //   79: invokevirtual 55	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: aload_2
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   41	2	1	i	int
        //   47	26	2	localCharSequence	CharSequence
        //   76	11	2	localObject	Object
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	42	76	finally
        //   59	73	76	finally
      }
      
      public int getRatingType()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      
      public String getTag()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParcelableVolumeInfo getVolumeAttributes()
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
        //   16: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 10
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifne +16 -> 59
        //   46: aconst_null
        //   47: astore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: areturn
        //   59: getstatic 130	android/support/v4/media/session/ParcelableVolumeInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   62: aload 4
        //   64: invokeinterface 77 2 0
        //   69: checkcast 129	android/support/v4/media/session/ParcelableVolumeInfo
        //   72: astore_2
        //   73: goto -25 -> 48
        //   76: astore_2
        //   77: aload 4
        //   79: invokevirtual 55	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: aload_2
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   41	2	1	i	int
        //   47	26	2	localParcelableVolumeInfo	ParcelableVolumeInfo
        //   76	11	2	localObject	Object
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	42	76	finally
        //   59	73	76	finally
      }
      
      /* Error */
      public boolean isTransportControlEnabled()
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_0
        //   18: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   21: iconst_5
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 49 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 52	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 65	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifne +14 -> 58
        //   47: aload 4
        //   49: invokevirtual 55	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 55	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_1
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 55	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 55	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   42	2	1	i	int
        //   1	59	2	bool	boolean
        //   5	66	3	localParcel1	Parcel
        //   9	57	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	43	63	finally
      }
      
      public void next()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void pause()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
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
      
      public void play()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
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
      
      /* Error */
      public void playFromMediaId(String paramString, Bundle paramBundle)
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
        //   16: aload_1
        //   17: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnonnull +39 -> 60
        //   24: aload_3
        //   25: iconst_0
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 14
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 49 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 52	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 55	android/os/Parcel:recycle	()V
        //   55: aload_3
        //   56: invokevirtual 55	android/os/Parcel:recycle	()V
        //   59: return
        //   60: aload_3
        //   61: iconst_1
        //   62: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   65: aload_2
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 141	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   71: goto -42 -> 29
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramBundle	Bundle
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	29	74	finally
        //   29	50	74	finally
        //   60	71	74	finally
      }
      
      /* Error */
      public void playFromSearch(String paramString, Bundle paramBundle)
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
        //   16: aload_1
        //   17: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnonnull +39 -> 60
        //   24: aload_3
        //   25: iconst_0
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 15
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 49 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 52	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 55	android/os/Parcel:recycle	()V
        //   55: aload_3
        //   56: invokevirtual 55	android/os/Parcel:recycle	()V
        //   59: return
        //   60: aload_3
        //   61: iconst_1
        //   62: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   65: aload_2
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 141	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   71: goto -42 -> 29
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramBundle	Bundle
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	29	74	finally
        //   29	50	74	finally
        //   60	71	74	finally
      }
      
      public void previous()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
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
      
      /* Error */
      public void rate(RatingCompat paramRatingCompat)
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
        //   15: ifnonnull +36 -> 51
        //   18: aload_2
        //   19: iconst_0
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_0
        //   24: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 24
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokeinterface 49 5 0
        //   37: pop
        //   38: aload_3
        //   39: invokevirtual 52	android/os/Parcel:readException	()V
        //   42: aload_3
        //   43: invokevirtual 55	android/os/Parcel:recycle	()V
        //   46: aload_2
        //   47: invokevirtual 55	android/os/Parcel:recycle	()V
        //   50: return
        //   51: aload_2
        //   52: iconst_1
        //   53: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   56: aload_1
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 148	android/support/v4/media/RatingCompat:writeToParcel	(Landroid/os/Parcel;I)V
        //   62: goto -39 -> 23
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 55	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 55	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramRatingCompat	RatingCompat
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	23	65	finally
        //   23	42	65	finally
        //   51	62	65	finally
      }
      
      /* Error */
      public void registerCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnonnull +40 -> 58
        //   21: aload_2
        //   22: astore_1
        //   23: aload_3
        //   24: aload_1
        //   25: invokevirtual 153	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   28: aload_0
        //   29: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: iconst_3
        //   33: aload_3
        //   34: aload 4
        //   36: iconst_0
        //   37: invokeinterface 49 5 0
        //   42: pop
        //   43: aload 4
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aload_1
        //   59: invokeinterface 157 1 0
        //   64: astore_1
        //   65: goto -42 -> 23
        //   68: astore_1
        //   69: aload 4
        //   71: invokevirtual 55	android/os/Parcel:recycle	()V
        //   74: aload_3
        //   75: invokevirtual 55	android/os/Parcel:recycle	()V
        //   78: aload_1
        //   79: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	80	0	this	Proxy
        //   0	80	1	paramIMediaControllerCallback	IMediaControllerCallback
        //   1	21	2	localObject	Object
        //   5	70	3	localParcel1	Parcel
        //   9	61	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	17	68	finally
        //   23	48	68	finally
        //   58	65	68	finally
      }
      
      public void rewind()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void seekTo(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          localParcel1.writeLong(paramLong);
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
      
      public void sendCommand(String paramString, Bundle paramBundle, MediaSessionCompat.ResultReceiverWrapper paramResultReceiverWrapper)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
            localParcel1.writeString(paramString);
            if (paramBundle == null)
            {
              localParcel1.writeInt(0);
              if (paramResultReceiverWrapper == null)
              {
                localParcel1.writeInt(0);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              continue;
            }
            localParcel1.writeInt(1);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          paramResultReceiverWrapper.writeToParcel(localParcel1, 0);
        }
      }
      
      /* Error */
      public void sendCustomAction(String paramString, Bundle paramBundle)
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
        //   16: aload_1
        //   17: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnonnull +39 -> 60
        //   24: aload_3
        //   25: iconst_0
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 25
        //   35: aload_3
        //   36: aload 4
        //   38: iconst_0
        //   39: invokeinterface 49 5 0
        //   44: pop
        //   45: aload 4
        //   47: invokevirtual 52	android/os/Parcel:readException	()V
        //   50: aload 4
        //   52: invokevirtual 55	android/os/Parcel:recycle	()V
        //   55: aload_3
        //   56: invokevirtual 55	android/os/Parcel:recycle	()V
        //   59: return
        //   60: aload_3
        //   61: iconst_1
        //   62: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   65: aload_2
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 141	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   71: goto -42 -> 29
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramBundle	Bundle
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	29	74	finally
        //   29	50	74	finally
        //   60	71	74	finally
      }
      
      public boolean sendMediaButton(KeyEvent paramKeyEvent)
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
            if (paramKeyEvent == null)
            {
              localParcel1.writeInt(0);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i == 0) {
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(1);
              paramKeyEvent.writeToParcel(localParcel1, 0);
              continue;
            }
            bool = true;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setVolumeTo(int paramInt1, int paramInt2, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void skipToQueueItem(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          localParcel1.writeLong(paramLong);
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
      
      public void stop()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
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
      public void unregisterCallbackListener(IMediaControllerCallback paramIMediaControllerCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnonnull +40 -> 58
        //   21: aload_2
        //   22: astore_1
        //   23: aload_3
        //   24: aload_1
        //   25: invokevirtual 153	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   28: aload_0
        //   29: getfield 19	android/support/v4/media/session/IMediaSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: iconst_4
        //   33: aload_3
        //   34: aload 4
        //   36: iconst_0
        //   37: invokeinterface 49 5 0
        //   42: pop
        //   43: aload 4
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aload_1
        //   59: invokeinterface 157 1 0
        //   64: astore_1
        //   65: goto -42 -> 23
        //   68: astore_1
        //   69: aload 4
        //   71: invokevirtual 55	android/os/Parcel:recycle	()V
        //   74: aload_3
        //   75: invokevirtual 55	android/os/Parcel:recycle	()V
        //   78: aload_1
        //   79: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	80	0	this	Proxy
        //   0	80	1	paramIMediaControllerCallback	IMediaControllerCallback
        //   1	21	2	localObject	Object
        //   5	70	3	localParcel1	Parcel
        //   9	61	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   11	17	68	finally
        //   23	48	68	finally
        //   58	65	68	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/session/IMediaSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */