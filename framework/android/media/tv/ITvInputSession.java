package android.media.tv;

import android.graphics.Rect;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.Surface;

public abstract interface ITvInputSession
  extends IInterface
{
  public abstract void appPrivateCommand(String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void createOverlayView(IBinder paramIBinder, Rect paramRect)
    throws RemoteException;
  
  public abstract void dispatchSurfaceChanged(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void relayoutOverlayView(Rect paramRect)
    throws RemoteException;
  
  public abstract void release()
    throws RemoteException;
  
  public abstract void removeOverlayView()
    throws RemoteException;
  
  public abstract void selectTrack(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setCaptionEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMain(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSurface(Surface paramSurface)
    throws RemoteException;
  
  public abstract void setVolume(float paramFloat)
    throws RemoteException;
  
  public abstract void startRecording(Uri paramUri)
    throws RemoteException;
  
  public abstract void stopRecording()
    throws RemoteException;
  
  public abstract void timeShiftEnablePositionTracking(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void timeShiftPause()
    throws RemoteException;
  
  public abstract void timeShiftPlay(Uri paramUri)
    throws RemoteException;
  
  public abstract void timeShiftResume()
    throws RemoteException;
  
  public abstract void timeShiftSeekTo(long paramLong)
    throws RemoteException;
  
  public abstract void timeShiftSetPlaybackParams(PlaybackParams paramPlaybackParams)
    throws RemoteException;
  
  public abstract void tune(Uri paramUri, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void unblockContent(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITvInputSession
  {
    private static final String DESCRIPTOR = "android.media.tv.ITvInputSession";
    static final int TRANSACTION_appPrivateCommand = 9;
    static final int TRANSACTION_createOverlayView = 10;
    static final int TRANSACTION_dispatchSurfaceChanged = 4;
    static final int TRANSACTION_relayoutOverlayView = 11;
    static final int TRANSACTION_release = 1;
    static final int TRANSACTION_removeOverlayView = 12;
    static final int TRANSACTION_selectTrack = 8;
    static final int TRANSACTION_setCaptionEnabled = 7;
    static final int TRANSACTION_setMain = 2;
    static final int TRANSACTION_setSurface = 3;
    static final int TRANSACTION_setVolume = 5;
    static final int TRANSACTION_startRecording = 20;
    static final int TRANSACTION_stopRecording = 21;
    static final int TRANSACTION_timeShiftEnablePositionTracking = 19;
    static final int TRANSACTION_timeShiftPause = 15;
    static final int TRANSACTION_timeShiftPlay = 14;
    static final int TRANSACTION_timeShiftResume = 16;
    static final int TRANSACTION_timeShiftSeekTo = 17;
    static final int TRANSACTION_timeShiftSetPlaybackParams = 18;
    static final int TRANSACTION_tune = 6;
    static final int TRANSACTION_unblockContent = 13;
    
    public Stub()
    {
      attachInterface(this, "android.media.tv.ITvInputSession");
    }
    
    public static ITvInputSession asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.tv.ITvInputSession");
      if ((localIInterface != null) && ((localIInterface instanceof ITvInputSession))) {
        return (ITvInputSession)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.tv.ITvInputSession");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        release();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setMain(bool);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Surface)Surface.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setSurface(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        dispatchSurfaceChanged(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        setVolume(paramParcel1.readFloat());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label385;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          tune(paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 7: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setCaptionEnabled(bool);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        selectTrack(paramParcel1.readInt(), paramParcel1.readString());
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          appPrivateCommand(paramParcel2, paramParcel1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        paramParcel2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          createOverlayView(paramParcel2, paramParcel1);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Rect)Rect.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          relayoutOverlayView(paramParcel1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        removeOverlayView();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        unblockContent(paramParcel1.readString());
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          timeShiftPlay(paramParcel1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        timeShiftPause();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        timeShiftResume();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        timeShiftSeekTo(paramParcel1.readLong());
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PlaybackParams)PlaybackParams.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          timeShiftSetPlaybackParams(paramParcel1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          timeShiftEnablePositionTracking(bool);
          return true;
        }
      case 20: 
        label385:
        paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startRecording(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.media.tv.ITvInputSession");
      stopRecording();
      return true;
    }
    
    private static class Proxy
      implements ITvInputSession
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void appPrivateCommand(String paramString, Bundle paramBundle)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +34 -> 50
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 49	android/os/Bundle:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 9
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   55: goto -25 -> 30
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 58	android/os/Parcel:recycle	()V
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public void createOverlayView(IBinder paramIBinder, Rect paramRect)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 66	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   15: aload_2
        //   16: ifnull +34 -> 50
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 69	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 10
        //   36: aload_3
        //   37: aconst_null
        //   38: iconst_1
        //   39: invokeinterface 55 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 58	android/os/Parcel:recycle	()V
        //   49: return
        //   50: aload_3
        //   51: iconst_0
        //   52: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   55: goto -25 -> 30
        //   58: astore_1
        //   59: aload_3
        //   60: invokevirtual 58	android/os/Parcel:recycle	()V
        //   63: aload_1
        //   64: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	65	0	this	Proxy
        //   0	65	1	paramIBinder	IBinder
        //   0	65	2	paramRect	Rect
        //   3	57	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	58	finally
        //   19	30	58	finally
        //   30	45	58	finally
        //   50	55	58	finally
      }
      
      public void dispatchSurfaceChanged(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          localParcel.writeInt(paramInt1);
          localParcel.writeInt(paramInt2);
          localParcel.writeInt(paramInt3);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.tv.ITvInputSession";
      }
      
      /* Error */
      public void relayoutOverlayView(Rect paramRect)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 69	android/graphics/Rect:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 11
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 58	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramRect	Rect
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void release()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeOverlayView()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          this.mRemote.transact(12, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void selectTrack(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          localParcel.writeInt(paramInt);
          localParcel.writeString(paramString);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void setCaptionEnabled(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 32
        //   9: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +28 -> 41
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: bipush 7
        //   27: aload_3
        //   28: aconst_null
        //   29: iconst_1
        //   30: invokeinterface 55 5 0
        //   35: pop
        //   36: aload_3
        //   37: invokevirtual 58	android/os/Parcel:recycle	()V
        //   40: return
        //   41: iconst_0
        //   42: istore_2
        //   43: goto -27 -> 16
        //   46: astore 4
        //   48: aload_3
        //   49: invokevirtual 58	android/os/Parcel:recycle	()V
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
      public void setMain(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 32
        //   9: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +27 -> 40
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: iconst_2
        //   26: aload_3
        //   27: aconst_null
        //   28: iconst_1
        //   29: invokeinterface 55 5 0
        //   34: pop
        //   35: aload_3
        //   36: invokevirtual 58	android/os/Parcel:recycle	()V
        //   39: return
        //   40: iconst_0
        //   41: istore_2
        //   42: goto -26 -> 16
        //   45: astore 4
        //   47: aload_3
        //   48: invokevirtual 58	android/os/Parcel:recycle	()V
        //   51: aload 4
        //   53: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	54	0	this	Proxy
        //   0	54	1	paramBoolean	boolean
        //   1	41	2	i	int
        //   5	43	3	localParcel	Parcel
        //   45	7	4	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   6	12	45	finally
        //   16	35	45	finally
      }
      
      /* Error */
      public void setSurface(Surface paramSurface)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 87	android/view/Surface:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 55 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 58	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 58	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramSurface	Surface
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      public void setVolume(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          localParcel.writeFloat(paramFloat);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void startRecording(Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 97	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 20
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 58	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
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
      
      public void stopRecording()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          this.mRemote.transact(21, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void timeShiftEnablePositionTracking(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: aload_3
        //   7: ldc 32
        //   9: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: iload_1
        //   13: ifeq +28 -> 41
        //   16: aload_3
        //   17: iload_2
        //   18: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   21: aload_0
        //   22: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   25: bipush 19
        //   27: aload_3
        //   28: aconst_null
        //   29: iconst_1
        //   30: invokeinterface 55 5 0
        //   35: pop
        //   36: aload_3
        //   37: invokevirtual 58	android/os/Parcel:recycle	()V
        //   40: return
        //   41: iconst_0
        //   42: istore_2
        //   43: goto -27 -> 16
        //   46: astore 4
        //   48: aload_3
        //   49: invokevirtual 58	android/os/Parcel:recycle	()V
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
      
      public void timeShiftPause()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          this.mRemote.transact(15, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void timeShiftPlay(Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 97	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 14
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 58	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
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
      
      public void timeShiftResume()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          this.mRemote.transact(16, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void timeShiftSeekTo(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(17, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void timeShiftSetPlaybackParams(PlaybackParams paramPlaybackParams)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 112	android/media/PlaybackParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 18
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 55 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 58	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 58	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramPlaybackParams	PlaybackParams
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void tune(Uri paramUri, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
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
      
      public void unblockContent(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputSession");
          localParcel.writeString(paramString);
          this.mRemote.transact(13, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvInputSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */