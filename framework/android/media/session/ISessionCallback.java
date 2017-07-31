package android.media.session;

import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;

public abstract interface ISessionCallback
  extends IInterface
{
  public abstract void getRemoteControlClientNowPlayingEntries()
    throws RemoteException;
  
  public abstract void onAdjustVolume(int paramInt)
    throws RemoteException;
  
  public abstract void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
    throws RemoteException;
  
  public abstract void onCustomAction(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onFastForward()
    throws RemoteException;
  
  public abstract void onMediaButton(Intent paramIntent, int paramInt, ResultReceiver paramResultReceiver)
    throws RemoteException;
  
  public abstract void onNext()
    throws RemoteException;
  
  public abstract void onPause()
    throws RemoteException;
  
  public abstract void onPlay()
    throws RemoteException;
  
  public abstract void onPlayFromMediaId(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPlayFromSearch(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPlayFromUri(Uri paramUri, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPrepare()
    throws RemoteException;
  
  public abstract void onPrepareFromMediaId(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPrepareFromSearch(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPrepareFromUri(Uri paramUri, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void onPrevious()
    throws RemoteException;
  
  public abstract void onRate(Rating paramRating)
    throws RemoteException;
  
  public abstract void onRewind()
    throws RemoteException;
  
  public abstract void onSeekTo(long paramLong)
    throws RemoteException;
  
  public abstract void onSetVolumeTo(int paramInt)
    throws RemoteException;
  
  public abstract void onSkipToTrack(long paramLong)
    throws RemoteException;
  
  public abstract void onStop()
    throws RemoteException;
  
  public abstract void setRemoteControlClientBrowsedPlayer()
    throws RemoteException;
  
  public abstract void setRemoteControlClientPlayItem(long paramLong, int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISessionCallback
  {
    private static final String DESCRIPTOR = "android.media.session.ISessionCallback";
    static final int TRANSACTION_getRemoteControlClientNowPlayingEntries = 21;
    static final int TRANSACTION_onAdjustVolume = 24;
    static final int TRANSACTION_onCommand = 1;
    static final int TRANSACTION_onCustomAction = 23;
    static final int TRANSACTION_onFastForward = 16;
    static final int TRANSACTION_onMediaButton = 2;
    static final int TRANSACTION_onNext = 14;
    static final int TRANSACTION_onPause = 12;
    static final int TRANSACTION_onPlay = 7;
    static final int TRANSACTION_onPlayFromMediaId = 8;
    static final int TRANSACTION_onPlayFromSearch = 9;
    static final int TRANSACTION_onPlayFromUri = 10;
    static final int TRANSACTION_onPrepare = 3;
    static final int TRANSACTION_onPrepareFromMediaId = 4;
    static final int TRANSACTION_onPrepareFromSearch = 5;
    static final int TRANSACTION_onPrepareFromUri = 6;
    static final int TRANSACTION_onPrevious = 15;
    static final int TRANSACTION_onRate = 22;
    static final int TRANSACTION_onRewind = 17;
    static final int TRANSACTION_onSeekTo = 18;
    static final int TRANSACTION_onSetVolumeTo = 25;
    static final int TRANSACTION_onSkipToTrack = 11;
    static final int TRANSACTION_onStop = 13;
    static final int TRANSACTION_setRemoteControlClientBrowsedPlayer = 19;
    static final int TRANSACTION_setRemoteControlClientPlayItem = 20;
    
    public Stub()
    {
      attachInterface(this, "android.media.session.ISessionCallback");
    }
    
    public static ISessionCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.session.ISessionCallback");
      if ((localIInterface != null) && ((localIInterface instanceof ISessionCallback))) {
        return (ISessionCallback)localIInterface;
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
        paramParcel2.writeString("android.media.session.ISessionCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label305;
          }
        }
        for (paramParcel1 = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onCommand(str, paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Intent)Intent.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label375;
          }
        }
        for (paramParcel1 = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onMediaButton(paramParcel2, paramInt1, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onPrepare();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPrepareFromMediaId(paramParcel2, paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPrepareFromSearch(paramParcel2, paramParcel1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label539;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPrepareFromUri(paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 7: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onPlay();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPlayFromMediaId(paramParcel2, paramParcel1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPlayFromSearch(paramParcel2, paramParcel1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label703;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onPlayFromUri(paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 11: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onSkipToTrack(paramParcel1.readLong());
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onPause();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onStop();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onNext();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onPrevious();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onFastForward();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onRewind();
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onSeekTo(paramParcel1.readLong());
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        setRemoteControlClientBrowsedPlayer();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        setRemoteControlClientPlayItem(paramParcel1.readLong(), paramParcel1.readInt());
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        getRemoteControlClientNowPlayingEntries();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Rating)Rating.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onRate(paramParcel1);
          return true;
        }
      case 23: 
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onCustomAction(paramParcel2, paramParcel1);
          return true;
        }
      case 24: 
        label305:
        label375:
        label539:
        label703:
        paramParcel1.enforceInterface("android.media.session.ISessionCallback");
        onAdjustVolume(paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.media.session.ISessionCallback");
      onSetVolumeTo(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ISessionCallback
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
        return "android.media.session.ISessionCallback";
      }
      
      public void getRemoteControlClientNowPlayingEntries()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(21, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onAdjustVolume(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(24, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onCommand(String paramString, Bundle paramBundle, ResultReceiver paramResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
            localParcel.writeString(paramString);
            if (paramBundle != null)
            {
              localParcel.writeInt(1);
              paramBundle.writeToParcel(localParcel, 0);
              if (paramResultReceiver != null)
              {
                localParcel.writeInt(1);
                paramResultReceiver.writeToParcel(localParcel, 0);
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
      
      /* Error */
      public void onCustomAction(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 59	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +34 -> 50
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 65	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/session/ISessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 23
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 45 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 48	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   55: goto -25 -> 30
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 48	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramString	String
        //   0	65	2	paramBundle	Bundle
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	58	finally
        //   19	30	58	finally
        //   30	45	58	finally
        //   50	55	58	finally
      }
      
      public void onFastForward()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(16, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onMediaButton(Intent paramIntent, int paramInt, ResultReceiver paramResultReceiver)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
            if (paramIntent != null)
            {
              localParcel.writeInt(1);
              paramIntent.writeToParcel(localParcel, 0);
              localParcel.writeInt(paramInt);
              if (paramResultReceiver != null)
              {
                localParcel.writeInt(1);
                paramResultReceiver.writeToParcel(localParcel, 0);
                this.mRemote.transact(2, localParcel, null, 1);
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
      
      public void onNext()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(14, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPause()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(12, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPlay()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onPlayFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 59	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +34 -> 50
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 65	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/session/ISessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 8
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 45 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 48	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   55: goto -25 -> 30
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 48	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramString	String
        //   0	65	2	paramBundle	Bundle
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	58	finally
        //   19	30	58	finally
        //   30	45	58	finally
        //   50	55	58	finally
      }
      
      /* Error */
      public void onPlayFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 59	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +34 -> 50
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 65	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/session/ISessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 9
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 45 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 48	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   55: goto -25 -> 30
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 48	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramString	String
        //   0	65	2	paramBundle	Bundle
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	58	finally
        //   19	30	58	finally
        //   30	45	58	finally
        //   50	55	58	finally
      }
      
      public void onPlayFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
            if (paramUri != null)
            {
              localParcel.writeInt(1);
              paramUri.writeToParcel(localParcel, 0);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(10, localParcel, null, 1);
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
      
      public void onPrepare()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onPrepareFromMediaId(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 59	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 65	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/session/ISessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_4
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 45 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 48	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 48	android/os/Parcel:recycle	()V
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
      public void onPrepareFromSearch(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 59	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 65	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/session/ISessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_5
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 45 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 48	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 48	android/os/Parcel:recycle	()V
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
      
      public void onPrepareFromUri(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
            if (paramUri != null)
            {
              localParcel.writeInt(1);
              paramUri.writeToParcel(localParcel, 0);
              if (paramBundle != null)
              {
                localParcel.writeInt(1);
                paramBundle.writeToParcel(localParcel, 0);
                this.mRemote.transact(6, localParcel, null, 1);
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
      
      public void onPrevious()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(15, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onRate(Rating paramRating)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 35	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 26
        //   7: invokevirtual 39	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 96	android/media/Rating:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/session/ISessionCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 22
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 45 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 48	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 54	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 48	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramRating	Rating
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void onRewind()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(17, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onSeekTo(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(18, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onSetVolumeTo(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(25, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onSkipToTrack(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onStop()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(13, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setRemoteControlClientBrowsedPlayer()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          this.mRemote.transact(19, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void setRemoteControlClientPlayItem(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.session.ISessionCallback");
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(20, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/session/ISessionCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */