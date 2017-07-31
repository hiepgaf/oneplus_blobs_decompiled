package android.media;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;

public abstract interface IRingtonePlayer
  extends IInterface
{
  public abstract String getTitle(Uri paramUri)
    throws RemoteException;
  
  public abstract boolean isPlaying(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openRingtone(Uri paramUri)
    throws RemoteException;
  
  public abstract void play(IBinder paramIBinder, Uri paramUri, AudioAttributes paramAudioAttributes, float paramFloat, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void playAsync(Uri paramUri, UserHandle paramUserHandle, boolean paramBoolean, AudioAttributes paramAudioAttributes)
    throws RemoteException;
  
  public abstract void setPlaybackProperties(IBinder paramIBinder, float paramFloat, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void stop(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void stopAsync()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRingtonePlayer
  {
    private static final String DESCRIPTOR = "android.media.IRingtonePlayer";
    static final int TRANSACTION_getTitle = 7;
    static final int TRANSACTION_isPlaying = 3;
    static final int TRANSACTION_openRingtone = 8;
    static final int TRANSACTION_play = 1;
    static final int TRANSACTION_playAsync = 5;
    static final int TRANSACTION_setPlaybackProperties = 4;
    static final int TRANSACTION_stop = 2;
    static final int TRANSACTION_stopAsync = 6;
    
    public Stub()
    {
      attachInterface(this, "android.media.IRingtonePlayer");
    }
    
    public static IRingtonePlayer asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.IRingtonePlayer");
      if ((localIInterface != null) && ((localIInterface instanceof IRingtonePlayer))) {
        return (IRingtonePlayer)localIInterface;
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
      Object localObject1;
      Object localObject2;
      label156:
      float f;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.IRingtonePlayer");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.IRingtonePlayer");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label198;
          }
          localObject2 = (AudioAttributes)AudioAttributes.CREATOR.createFromParcel(paramParcel1);
          f = paramParcel1.readFloat();
          if (paramParcel1.readInt() == 0) {
            break label204;
          }
        }
        for (bool = true;; bool = false)
        {
          play(localIBinder, (Uri)localObject1, (AudioAttributes)localObject2, f, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label156;
        }
      case 2: 
        paramParcel1.enforceInterface("android.media.IRingtonePlayer");
        stop(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.IRingtonePlayer");
        bool = isPlaying(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.IRingtonePlayer");
        localObject1 = paramParcel1.readStrongBinder();
        f = paramParcel1.readFloat();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setPlaybackProperties((IBinder)localObject1, f, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.media.IRingtonePlayer");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label420;
          }
          localObject2 = (UserHandle)UserHandle.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label426;
          }
          bool = true;
          if (paramParcel1.readInt() == 0) {
            break label432;
          }
        }
        for (paramParcel1 = (AudioAttributes)AudioAttributes.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          playAsync((Uri)localObject1, (UserHandle)localObject2, bool, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label367;
          bool = false;
          break label377;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.IRingtonePlayer");
        stopAsync();
        paramParcel2.writeNoException();
        return true;
      case 7: 
        label198:
        label204:
        label367:
        label377:
        label420:
        label426:
        label432:
        paramParcel1.enforceInterface("android.media.IRingtonePlayer");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getTitle(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.media.IRingtonePlayer");
      if (paramParcel1.readInt() != 0)
      {
        paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
        paramParcel1 = openRingtone(paramParcel1);
        paramParcel2.writeNoException();
        if (paramParcel1 == null) {
          break label559;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        paramParcel1 = null;
        break;
        label559:
        paramParcel2.writeInt(0);
      }
    }
    
    private static class Proxy
      implements IRingtonePlayer
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
        return "android.media.IRingtonePlayer";
      }
      
      /* Error */
      public String getTitle(Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/media/IRingtonePlayer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 7
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 56 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 59	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 62	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 65	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 65	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 65	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 65	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramUri	Uri
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      /* Error */
      public boolean isPlaying(IBinder paramIBinder)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 71	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_0
        //   24: getfield 19	android/media/IRingtonePlayer$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: iconst_3
        //   28: aload 4
        //   30: aload 5
        //   32: iconst_0
        //   33: invokeinterface 56 5 0
        //   38: pop
        //   39: aload 5
        //   41: invokevirtual 59	android/os/Parcel:readException	()V
        //   44: aload 5
        //   46: invokevirtual 75	android/os/Parcel:readInt	()I
        //   49: istore_2
        //   50: iload_2
        //   51: ifeq +17 -> 68
        //   54: iconst_1
        //   55: istore_3
        //   56: aload 5
        //   58: invokevirtual 65	android/os/Parcel:recycle	()V
        //   61: aload 4
        //   63: invokevirtual 65	android/os/Parcel:recycle	()V
        //   66: iload_3
        //   67: ireturn
        //   68: iconst_0
        //   69: istore_3
        //   70: goto -14 -> 56
        //   73: astore_1
        //   74: aload 5
        //   76: invokevirtual 65	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 65	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramIBinder	IBinder
        //   49	2	2	i	int
        //   55	15	3	bool	boolean
        //   3	77	4	localParcel1	Parcel
        //   8	67	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	50	73	finally
      }
      
      public ParcelFileDescriptor openRingtone(Uri paramUri)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.IRingtonePlayer");
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              this.mRemote.transact(8, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramUri = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
                return paramUri;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramUri = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void play(IBinder paramIBinder, Uri paramUri, AudioAttributes paramAudioAttributes, float paramFloat, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.IRingtonePlayer");
            localParcel1.writeStrongBinder(paramIBinder);
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              if (paramAudioAttributes != null)
              {
                localParcel1.writeInt(1);
                paramAudioAttributes.writeToParcel(localParcel1, 0);
                localParcel1.writeFloat(paramFloat);
                if (!paramBoolean) {
                  break label142;
                }
                localParcel1.writeInt(i);
                this.mRemote.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label142:
          i = 0;
        }
      }
      
      public void playAsync(Uri paramUri, UserHandle paramUserHandle, boolean paramBoolean, AudioAttributes paramAudioAttributes)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.IRingtonePlayer");
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              if (paramUserHandle != null)
              {
                localParcel1.writeInt(1);
                paramUserHandle.writeToParcel(localParcel1, 0);
                break label155;
                localParcel1.writeInt(i);
                if (paramAudioAttributes == null) {
                  break label146;
                }
                localParcel1.writeInt(1);
                paramAudioAttributes.writeToParcel(localParcel1, 0);
                this.mRemote.transact(5, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          break label155;
          label146:
          localParcel1.writeInt(0);
          continue;
          label155:
          if (!paramBoolean) {
            i = 0;
          }
        }
      }
      
      public void setPlaybackProperties(IBinder paramIBinder, float paramFloat, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IRingtonePlayer");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeFloat(paramFloat);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void stop(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IRingtonePlayer");
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void stopAsync()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.IRingtonePlayer");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/IRingtonePlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */