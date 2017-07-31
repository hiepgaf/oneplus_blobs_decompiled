package android.media.tv;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.InputChannel;

public abstract interface ITvInputService
  extends IInterface
{
  public abstract void createRecordingSession(ITvInputSessionCallback paramITvInputSessionCallback, String paramString)
    throws RemoteException;
  
  public abstract void createSession(InputChannel paramInputChannel, ITvInputSessionCallback paramITvInputSessionCallback, String paramString)
    throws RemoteException;
  
  public abstract void notifyHardwareAdded(TvInputHardwareInfo paramTvInputHardwareInfo)
    throws RemoteException;
  
  public abstract void notifyHardwareRemoved(TvInputHardwareInfo paramTvInputHardwareInfo)
    throws RemoteException;
  
  public abstract void notifyHdmiDeviceAdded(HdmiDeviceInfo paramHdmiDeviceInfo)
    throws RemoteException;
  
  public abstract void notifyHdmiDeviceRemoved(HdmiDeviceInfo paramHdmiDeviceInfo)
    throws RemoteException;
  
  public abstract void registerCallback(ITvInputServiceCallback paramITvInputServiceCallback)
    throws RemoteException;
  
  public abstract void unregisterCallback(ITvInputServiceCallback paramITvInputServiceCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ITvInputService
  {
    private static final String DESCRIPTOR = "android.media.tv.ITvInputService";
    static final int TRANSACTION_createRecordingSession = 4;
    static final int TRANSACTION_createSession = 3;
    static final int TRANSACTION_notifyHardwareAdded = 5;
    static final int TRANSACTION_notifyHardwareRemoved = 6;
    static final int TRANSACTION_notifyHdmiDeviceAdded = 7;
    static final int TRANSACTION_notifyHdmiDeviceRemoved = 8;
    static final int TRANSACTION_registerCallback = 1;
    static final int TRANSACTION_unregisterCallback = 2;
    
    public Stub()
    {
      attachInterface(this, "android.media.tv.ITvInputService");
    }
    
    public static ITvInputService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.tv.ITvInputService");
      if ((localIInterface != null) && ((localIInterface instanceof ITvInputService))) {
        return (ITvInputService)localIInterface;
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
        paramParcel2.writeString("android.media.tv.ITvInputService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputService");
        registerCallback(ITvInputServiceCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputService");
        unregisterCallback(ITvInputServiceCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (InputChannel)InputChannel.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          createSession(paramParcel2, ITvInputSessionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputService");
        createRecordingSession(ITvInputSessionCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readString());
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (TvInputHardwareInfo)TvInputHardwareInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          notifyHardwareAdded(paramParcel1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (TvInputHardwareInfo)TvInputHardwareInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          notifyHardwareRemoved(paramParcel1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.media.tv.ITvInputService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (HdmiDeviceInfo)HdmiDeviceInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          notifyHdmiDeviceAdded(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.media.tv.ITvInputService");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (HdmiDeviceInfo)HdmiDeviceInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        notifyHdmiDeviceRemoved(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements ITvInputService
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
      
      public void createRecordingSession(ITvInputSessionCallback paramITvInputSessionCallback, String paramString)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputService");
          if (paramITvInputSessionCallback != null) {
            localIBinder = paramITvInputSessionCallback.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          localParcel.writeString(paramString);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void createSession(InputChannel paramInputChannel, ITvInputSessionCallback paramITvInputSessionCallback, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +63 -> 79
        //   19: aload 5
        //   21: iconst_1
        //   22: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 5
        //   28: iconst_0
        //   29: invokevirtual 70	android/view/InputChannel:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 4
        //   34: astore_1
        //   35: aload_2
        //   36: ifnull +10 -> 46
        //   39: aload_2
        //   40: invokeinterface 42 1 0
        //   45: astore_1
        //   46: aload 5
        //   48: aload_1
        //   49: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   52: aload 5
        //   54: aload_3
        //   55: invokevirtual 48	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   58: aload_0
        //   59: getfield 19	android/media/tv/ITvInputService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   62: iconst_3
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 54 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 57	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   85: goto -53 -> 32
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 57	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramInputChannel	InputChannel
        //   0	96	2	paramITvInputSessionCallback	ITvInputSessionCallback
        //   0	96	3	paramString	String
        //   1	32	4	localObject	Object
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	88	finally
        //   19	32	88	finally
        //   39	46	88	finally
        //   46	73	88	finally
        //   79	85	88	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.tv.ITvInputService";
      }
      
      /* Error */
      public void notifyHardwareAdded(TvInputHardwareInfo paramTvInputHardwareInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 77	android/media/tv/TvInputHardwareInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_5
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 54 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 57	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramTvInputHardwareInfo	TvInputHardwareInfo
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public void notifyHardwareRemoved(TvInputHardwareInfo paramTvInputHardwareInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 77	android/media/tv/TvInputHardwareInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 6
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 57	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 57	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramTvInputHardwareInfo	TvInputHardwareInfo
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      /* Error */
      public void notifyHdmiDeviceAdded(HdmiDeviceInfo paramHdmiDeviceInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 83	android/hardware/hdmi/HdmiDeviceInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 7
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 57	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 57	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramHdmiDeviceInfo	HdmiDeviceInfo
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      /* Error */
      public void notifyHdmiDeviceRemoved(HdmiDeviceInfo paramHdmiDeviceInfo)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 83	android/hardware/hdmi/HdmiDeviceInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/media/tv/ITvInputService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 8
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 54 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 57	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 57	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramHdmiDeviceInfo	HdmiDeviceInfo
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void registerCallback(ITvInputServiceCallback paramITvInputServiceCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputService");
          if (paramITvInputServiceCallback != null) {
            localIBinder = paramITvInputServiceCallback.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void unregisterCallback(ITvInputServiceCallback paramITvInputServiceCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.media.tv.ITvInputService");
          if (paramITvInputServiceCallback != null) {
            localIBinder = paramITvInputServiceCallback.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(2, localParcel, null, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/ITvInputService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */