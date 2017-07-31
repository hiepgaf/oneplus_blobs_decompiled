package android.media.tv;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.InputChannel;
import java.util.List;

public abstract interface ITvInputClient
  extends IInterface
{
  public abstract void onChannelRetuned(Uri paramUri, int paramInt)
    throws RemoteException;
  
  public abstract void onContentAllowed(int paramInt)
    throws RemoteException;
  
  public abstract void onContentBlocked(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onError(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onLayoutSurface(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws RemoteException;
  
  public abstract void onRecordingStopped(Uri paramUri, int paramInt)
    throws RemoteException;
  
  public abstract void onSessionCreated(String paramString, IBinder paramIBinder, InputChannel paramInputChannel, int paramInt)
    throws RemoteException;
  
  public abstract void onSessionEvent(String paramString, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract void onSessionReleased(int paramInt)
    throws RemoteException;
  
  public abstract void onTimeShiftCurrentPositionChanged(long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onTimeShiftStartPositionChanged(long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract void onTimeShiftStatusChanged(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void onTrackSelected(int paramInt1, String paramString, int paramInt2)
    throws RemoteException;
  
  public abstract void onTracksChanged(List<TvTrackInfo> paramList, int paramInt)
    throws RemoteException;
  
  public abstract void onTuned(int paramInt, Uri paramUri)
    throws RemoteException;
  
  public abstract void onVideoAvailable(int paramInt)
    throws RemoteException;
  
  public abstract void onVideoUnavailable(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITvInputClient
  {
    private static final String DESCRIPTOR = "android.media.tv.ITvInputClient";
    static final int TRANSACTION_onChannelRetuned = 4;
    static final int TRANSACTION_onContentAllowed = 9;
    static final int TRANSACTION_onContentBlocked = 10;
    static final int TRANSACTION_onError = 17;
    static final int TRANSACTION_onLayoutSurface = 11;
    static final int TRANSACTION_onRecordingStopped = 16;
    static final int TRANSACTION_onSessionCreated = 1;
    static final int TRANSACTION_onSessionEvent = 3;
    static final int TRANSACTION_onSessionReleased = 2;
    static final int TRANSACTION_onTimeShiftCurrentPositionChanged = 14;
    static final int TRANSACTION_onTimeShiftStartPositionChanged = 13;
    static final int TRANSACTION_onTimeShiftStatusChanged = 12;
    static final int TRANSACTION_onTrackSelected = 6;
    static final int TRANSACTION_onTracksChanged = 5;
    static final int TRANSACTION_onTuned = 15;
    static final int TRANSACTION_onVideoAvailable = 7;
    static final int TRANSACTION_onVideoUnavailable = 8;
    
    public Stub()
    {
      attachInterface(this, "android.media.tv.ITvInputClient");
    }
    
    public static ITvInputClient asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.tv.ITvInputClient");
      if ((localIInterface != null) && ((localIInterface instanceof ITvInputClient))) {
        return (ITvInputClient)localIInterface;
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
        paramParcel2.writeString("android.media.tv.ITvInputClient");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        str = paramParcel1.readString();
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (InputChannel)InputChannel.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onSessionCreated(str, localIBinder, paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onSessionReleased(paramParcel1.readInt());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onSessionEvent(str, paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onChannelRetuned(paramParcel2, paramParcel1.readInt());
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onTracksChanged(paramParcel1.createTypedArrayList(TvTrackInfo.CREATOR), paramParcel1.readInt());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onTrackSelected(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onVideoAvailable(paramParcel1.readInt());
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onVideoUnavailable(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onContentAllowed(paramParcel1.readInt());
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onContentBlocked(paramParcel1.readString(), paramParcel1.readInt());
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onLayoutSurface(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onTimeShiftStatusChanged(paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onTimeShiftStartPositionChanged(paramParcel1.readLong(), paramParcel1.readInt());
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        onTimeShiftCurrentPositionChanged(paramParcel1.readLong(), paramParcel1.readInt());
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onTuned(paramInt1, paramParcel1);
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onRecordingStopped(paramParcel2, paramParcel1.readInt());
          return true;
        }
      }
      paramParcel1.enforceInterface("android.media.tv.ITvInputClient");
      onError(paramParcel1.readInt(), paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements ITvInputClient
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
        return "android.media.tv.ITvInputClient";
      }
      
      /* Error */
      public void onChannelRetuned(Uri paramUri, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +38 -> 49
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/media/tv/ITvInputClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_4
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
        //   54: goto -29 -> 25
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramUri	Uri
        //   0	64	2	paramInt	int
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	57	finally
        //   14	25	57	finally
        //   25	44	57	finally
        //   49	54	57	finally
      }
      
      public void onContentAllowed(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(9, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onContentBlocked(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(10, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onError(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(17, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onLayoutSurface(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          localParcel.writeInt(paramInt4);
          localParcel.writeInt(paramInt5);
          this.mRemote.transact(11, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onRecordingStopped(Uri paramUri, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +39 -> 50
        //   14: aload_3
        //   15: iconst_1
        //   16: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_3
        //   21: iconst_0
        //   22: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_3
        //   26: iload_2
        //   27: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   30: aload_0
        //   31: getfield 19	android/media/tv/ITvInputClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 16
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 56 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 59	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   55: goto -30 -> 25
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 59	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramUri	Uri
        //   0	65	2	paramInt	int
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	58	finally
        //   14	25	58	finally
        //   25	45	58	finally
        //   50	55	58	finally
      }
      
      /* Error */
      public void onSessionCreated(String paramString, IBinder paramIBinder, InputChannel paramInputChannel, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: aload 5
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 5
        //   14: aload_1
        //   15: invokevirtual 66	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   18: aload 5
        //   20: aload_2
        //   21: invokevirtual 76	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   24: aload_3
        //   25: ifnull +44 -> 69
        //   28: aload 5
        //   30: iconst_1
        //   31: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   34: aload_3
        //   35: aload 5
        //   37: iconst_0
        //   38: invokevirtual 79	android/view/InputChannel:writeToParcel	(Landroid/os/Parcel;I)V
        //   41: aload 5
        //   43: iload 4
        //   45: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   48: aload_0
        //   49: getfield 19	android/media/tv/ITvInputClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   52: iconst_1
        //   53: aload 5
        //   55: aconst_null
        //   56: iconst_1
        //   57: invokeinterface 56 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: return
        //   69: aload 5
        //   71: iconst_0
        //   72: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   75: goto -34 -> 41
        //   78: astore_1
        //   79: aload 5
        //   81: invokevirtual 59	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramIBinder	IBinder
        //   0	86	3	paramInputChannel	InputChannel
        //   0	86	4	paramInt	int
        //   3	77	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	24	78	finally
        //   28	41	78	finally
        //   41	63	78	finally
        //   69	75	78	finally
      }
      
      /* Error */
      public void onSessionEvent(String paramString, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: aload 4
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload 4
        //   14: aload_1
        //   15: invokevirtual 66	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   18: aload_2
        //   19: ifnull +43 -> 62
        //   22: aload 4
        //   24: iconst_1
        //   25: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   28: aload_2
        //   29: aload 4
        //   31: iconst_0
        //   32: invokevirtual 84	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload 4
        //   37: iload_3
        //   38: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   41: aload_0
        //   42: getfield 19	android/media/tv/ITvInputClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   45: iconst_3
        //   46: aload 4
        //   48: aconst_null
        //   49: iconst_1
        //   50: invokeinterface 56 5 0
        //   55: pop
        //   56: aload 4
        //   58: invokevirtual 59	android/os/Parcel:recycle	()V
        //   61: return
        //   62: aload 4
        //   64: iconst_0
        //   65: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   68: goto -33 -> 35
        //   71: astore_1
        //   72: aload 4
        //   74: invokevirtual 59	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   0	79	1	paramString	String
        //   0	79	2	paramBundle	Bundle
        //   0	79	3	paramInt	int
        //   3	70	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	18	71	finally
        //   22	35	71	finally
        //   35	56	71	finally
        //   62	68	71	finally
      }
      
      public void onSessionReleased(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTimeShiftCurrentPositionChanged(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(14, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTimeShiftStartPositionChanged(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeLong(paramLong);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(13, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTimeShiftStatusChanged(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(12, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTrackSelected(int paramInt1, String paramString, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt1);
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onTracksChanged(List<TvTrackInfo> paramList, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeTypedList(paramList);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onTuned(int paramInt, Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 26
        //   7: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: iload_1
        //   12: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   15: aload_2
        //   16: ifnull +34 -> 50
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 50	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/tv/ITvInputClient$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 15
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 56 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 59	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   55: goto -25 -> 30
        //   58: astore_2
        //   59: aload_3
        //   60: invokevirtual 59	android/os/Parcel:recycle	()V
        //   63: aload_2
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramInt	int
        //   0	65	2	paramUri	Uri
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	58	finally
        //   19	30	58	finally
        //   30	45	58	finally
        //   50	55	58	finally
      }
      
      public void onVideoAvailable(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onVideoUnavailable(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputClient");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          this.mRemote.transact(8, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvInputClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */