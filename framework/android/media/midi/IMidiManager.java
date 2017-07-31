package android.media.midi;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IMidiManager
  extends IInterface
{
  public abstract void closeDevice(IBinder paramIBinder1, IBinder paramIBinder2)
    throws RemoteException;
  
  public abstract MidiDeviceStatus getDeviceStatus(MidiDeviceInfo paramMidiDeviceInfo)
    throws RemoteException;
  
  public abstract MidiDeviceInfo[] getDevices()
    throws RemoteException;
  
  public abstract MidiDeviceInfo getServiceDeviceInfo(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void openBluetoothDevice(IBinder paramIBinder, BluetoothDevice paramBluetoothDevice, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
    throws RemoteException;
  
  public abstract void openDevice(IBinder paramIBinder, MidiDeviceInfo paramMidiDeviceInfo, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
    throws RemoteException;
  
  public abstract MidiDeviceInfo registerDeviceServer(IMidiDeviceServer paramIMidiDeviceServer, int paramInt1, int paramInt2, String[] paramArrayOfString1, String[] paramArrayOfString2, Bundle paramBundle, int paramInt3)
    throws RemoteException;
  
  public abstract void registerListener(IBinder paramIBinder, IMidiDeviceListener paramIMidiDeviceListener)
    throws RemoteException;
  
  public abstract void setDeviceStatus(IMidiDeviceServer paramIMidiDeviceServer, MidiDeviceStatus paramMidiDeviceStatus)
    throws RemoteException;
  
  public abstract void unregisterDeviceServer(IMidiDeviceServer paramIMidiDeviceServer)
    throws RemoteException;
  
  public abstract void unregisterListener(IBinder paramIBinder, IMidiDeviceListener paramIMidiDeviceListener)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMidiManager
  {
    private static final String DESCRIPTOR = "android.media.midi.IMidiManager";
    static final int TRANSACTION_closeDevice = 6;
    static final int TRANSACTION_getDeviceStatus = 10;
    static final int TRANSACTION_getDevices = 1;
    static final int TRANSACTION_getServiceDeviceInfo = 9;
    static final int TRANSACTION_openBluetoothDevice = 5;
    static final int TRANSACTION_openDevice = 4;
    static final int TRANSACTION_registerDeviceServer = 7;
    static final int TRANSACTION_registerListener = 2;
    static final int TRANSACTION_setDeviceStatus = 11;
    static final int TRANSACTION_unregisterDeviceServer = 8;
    static final int TRANSACTION_unregisterListener = 3;
    
    public Stub()
    {
      attachInterface(this, "android.media.midi.IMidiManager");
    }
    
    public static IMidiManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.media.midi.IMidiManager");
      if ((localIInterface != null) && ((localIInterface instanceof IMidiManager))) {
        return (IMidiManager)localIInterface;
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
      Object localObject2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.media.midi.IMidiManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        paramParcel1 = getDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        registerListener(paramParcel1.readStrongBinder(), IMidiDeviceListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        unregisterListener(paramParcel1.readStrongBinder(), IMidiDeviceListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (MidiDeviceInfo)MidiDeviceInfo.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          openDevice((IBinder)localObject2, (MidiDeviceInfo)localObject1, IMidiDeviceOpenCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        localObject2 = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (BluetoothDevice)BluetoothDevice.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          openBluetoothDevice((IBinder)localObject2, (BluetoothDevice)localObject1, IMidiDeviceOpenCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        closeDevice(paramParcel1.readStrongBinder(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        localObject2 = IMidiDeviceServer.Stub.asInterface(paramParcel1.readStrongBinder());
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        String[] arrayOfString1 = paramParcel1.createStringArray();
        String[] arrayOfString2 = paramParcel1.createStringArray();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = registerDeviceServer((IMidiDeviceServer)localObject2, paramInt1, paramInt2, arrayOfString1, arrayOfString2, (Bundle)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label453;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 8: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        unregisterDeviceServer(IMidiDeviceServer.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        paramParcel1 = getServiceDeviceInfo(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel2.writeInt(0);
        }
      case 10: 
        label453:
        paramParcel1.enforceInterface("android.media.midi.IMidiManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (MidiDeviceInfo)MidiDeviceInfo.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getDeviceStatus(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label590;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          label590:
          paramParcel2.writeInt(0);
        }
      }
      paramParcel1.enforceInterface("android.media.midi.IMidiManager");
      Object localObject1 = IMidiDeviceServer.Stub.asInterface(paramParcel1.readStrongBinder());
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (MidiDeviceStatus)MidiDeviceStatus.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        setDeviceStatus((IMidiDeviceServer)localObject1, paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IMidiManager
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
      
      public void closeDevice(IBinder paramIBinder1, IBinder paramIBinder2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.midi.IMidiManager");
          localParcel1.writeStrongBinder(paramIBinder1);
          localParcel1.writeStrongBinder(paramIBinder2);
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
      
      public MidiDeviceStatus getDeviceStatus(MidiDeviceInfo paramMidiDeviceInfo)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.midi.IMidiManager");
            if (paramMidiDeviceInfo != null)
            {
              localParcel1.writeInt(1);
              paramMidiDeviceInfo.writeToParcel(localParcel1, 0);
              this.mRemote.transact(10, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramMidiDeviceInfo = (MidiDeviceStatus)MidiDeviceStatus.CREATOR.createFromParcel(localParcel2);
                return paramMidiDeviceInfo;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramMidiDeviceInfo = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public MidiDeviceInfo[] getDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.midi.IMidiManager");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          MidiDeviceInfo[] arrayOfMidiDeviceInfo = (MidiDeviceInfo[])localParcel2.createTypedArray(MidiDeviceInfo.CREATOR);
          return arrayOfMidiDeviceInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.media.midi.IMidiManager";
      }
      
      /* Error */
      public MidiDeviceInfo getServiceDeviceInfo(String paramString1, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 98	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: aload_2
        //   22: invokevirtual 98	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   25: aload_0
        //   26: getfield 19	android/media/midi/IMidiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 9
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 47 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 50	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 70	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 85	android/media/midi/MidiDeviceInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   57: aload 4
        //   59: invokeinterface 82 2 0
        //   64: checkcast 62	android/media/midi/MidiDeviceInfo
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 53	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 53	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 53	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 53	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString1	String
        //   0	96	2	paramString2	String
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      /* Error */
      public void openBluetoothDevice(IBinder paramIBinder, BluetoothDevice paramBluetoothDevice, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 34
        //   17: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: aload_1
        //   23: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   26: aload_2
        //   27: ifnull +68 -> 95
        //   30: aload 5
        //   32: iconst_1
        //   33: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   36: aload_2
        //   37: aload 5
        //   39: iconst_0
        //   40: invokevirtual 103	android/bluetooth/BluetoothDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   43: aload 4
        //   45: astore_1
        //   46: aload_3
        //   47: ifnull +10 -> 57
        //   50: aload_3
        //   51: invokeinterface 107 1 0
        //   56: astore_1
        //   57: aload 5
        //   59: aload_1
        //   60: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   63: aload_0
        //   64: getfield 19	android/media/midi/IMidiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   67: iconst_5
        //   68: aload 5
        //   70: aload 6
        //   72: iconst_0
        //   73: invokeinterface 47 5 0
        //   78: pop
        //   79: aload 6
        //   81: invokevirtual 50	android/os/Parcel:readException	()V
        //   84: aload 6
        //   86: invokevirtual 53	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 53	android/os/Parcel:recycle	()V
        //   94: return
        //   95: aload 5
        //   97: iconst_0
        //   98: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   101: goto -58 -> 43
        //   104: astore_1
        //   105: aload 6
        //   107: invokevirtual 53	android/os/Parcel:recycle	()V
        //   110: aload 5
        //   112: invokevirtual 53	android/os/Parcel:recycle	()V
        //   115: aload_1
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	Proxy
        //   0	117	1	paramIBinder	IBinder
        //   0	117	2	paramBluetoothDevice	BluetoothDevice
        //   0	117	3	paramIMidiDeviceOpenCallback	IMidiDeviceOpenCallback
        //   1	43	4	localObject	Object
        //   6	105	5	localParcel1	Parcel
        //   11	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	104	finally
        //   30	43	104	finally
        //   50	57	104	finally
        //   57	84	104	finally
        //   95	101	104	finally
      }
      
      /* Error */
      public void openDevice(IBinder paramIBinder, MidiDeviceInfo paramMidiDeviceInfo, IMidiDeviceOpenCallback paramIMidiDeviceOpenCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 34
        //   17: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: aload_1
        //   23: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   26: aload_2
        //   27: ifnull +68 -> 95
        //   30: aload 5
        //   32: iconst_1
        //   33: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   36: aload_2
        //   37: aload 5
        //   39: iconst_0
        //   40: invokevirtual 66	android/media/midi/MidiDeviceInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   43: aload 4
        //   45: astore_1
        //   46: aload_3
        //   47: ifnull +10 -> 57
        //   50: aload_3
        //   51: invokeinterface 107 1 0
        //   56: astore_1
        //   57: aload 5
        //   59: aload_1
        //   60: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   63: aload_0
        //   64: getfield 19	android/media/midi/IMidiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   67: iconst_4
        //   68: aload 5
        //   70: aload 6
        //   72: iconst_0
        //   73: invokeinterface 47 5 0
        //   78: pop
        //   79: aload 6
        //   81: invokevirtual 50	android/os/Parcel:readException	()V
        //   84: aload 6
        //   86: invokevirtual 53	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 53	android/os/Parcel:recycle	()V
        //   94: return
        //   95: aload 5
        //   97: iconst_0
        //   98: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   101: goto -58 -> 43
        //   104: astore_1
        //   105: aload 6
        //   107: invokevirtual 53	android/os/Parcel:recycle	()V
        //   110: aload 5
        //   112: invokevirtual 53	android/os/Parcel:recycle	()V
        //   115: aload_1
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	Proxy
        //   0	117	1	paramIBinder	IBinder
        //   0	117	2	paramMidiDeviceInfo	MidiDeviceInfo
        //   0	117	3	paramIMidiDeviceOpenCallback	IMidiDeviceOpenCallback
        //   1	43	4	localObject	Object
        //   6	105	5	localParcel1	Parcel
        //   11	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	104	finally
        //   30	43	104	finally
        //   50	57	104	finally
        //   57	84	104	finally
        //   95	101	104	finally
      }
      
      public MidiDeviceInfo registerDeviceServer(IMidiDeviceServer paramIMidiDeviceServer, int paramInt1, int paramInt2, String[] paramArrayOfString1, String[] paramArrayOfString2, Bundle paramBundle, int paramInt3)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.media.midi.IMidiManager");
            if (paramIMidiDeviceServer != null) {
              localIBinder = paramIMidiDeviceServer.asBinder();
            }
            localParcel1.writeStrongBinder(localIBinder);
            localParcel1.writeInt(paramInt1);
            localParcel1.writeInt(paramInt2);
            localParcel1.writeStringArray(paramArrayOfString1);
            localParcel1.writeStringArray(paramArrayOfString2);
            if (paramBundle != null)
            {
              localParcel1.writeInt(1);
              paramBundle.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt3);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramIMidiDeviceServer = (MidiDeviceInfo)MidiDeviceInfo.CREATOR.createFromParcel(localParcel2);
                return paramIMidiDeviceServer;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramIMidiDeviceServer = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void registerListener(IBinder paramIBinder, IMidiDeviceListener paramIMidiDeviceListener)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.midi.IMidiManager");
          localParcel1.writeStrongBinder(paramIBinder);
          paramIBinder = (IBinder)localObject;
          if (paramIMidiDeviceListener != null) {
            paramIBinder = paramIMidiDeviceListener.asBinder();
          }
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
      
      /* Error */
      public void setDeviceStatus(IMidiDeviceServer paramIMidiDeviceServer, MidiDeviceStatus paramMidiDeviceStatus)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 34
        //   16: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +10 -> 30
        //   23: aload_1
        //   24: invokeinterface 114 1 0
        //   29: astore_3
        //   30: aload 4
        //   32: aload_3
        //   33: invokevirtual 41	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   36: aload_2
        //   37: ifnull +49 -> 86
        //   40: aload 4
        //   42: iconst_1
        //   43: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   46: aload_2
        //   47: aload 4
        //   49: iconst_0
        //   50: invokevirtual 129	android/media/midi/MidiDeviceStatus:writeToParcel	(Landroid/os/Parcel;I)V
        //   53: aload_0
        //   54: getfield 19	android/media/midi/IMidiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 11
        //   59: aload 4
        //   61: aload 5
        //   63: iconst_0
        //   64: invokeinterface 47 5 0
        //   69: pop
        //   70: aload 5
        //   72: invokevirtual 50	android/os/Parcel:readException	()V
        //   75: aload 5
        //   77: invokevirtual 53	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 53	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 4
        //   88: iconst_0
        //   89: invokevirtual 60	android/os/Parcel:writeInt	(I)V
        //   92: goto -39 -> 53
        //   95: astore_1
        //   96: aload 5
        //   98: invokevirtual 53	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 53	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramIMidiDeviceServer	IMidiDeviceServer
        //   0	108	2	paramMidiDeviceStatus	MidiDeviceStatus
        //   1	32	3	localIBinder	IBinder
        //   5	97	4	localParcel1	Parcel
        //   10	87	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	95	finally
        //   23	30	95	finally
        //   30	36	95	finally
        //   40	53	95	finally
        //   53	75	95	finally
        //   86	92	95	finally
      }
      
      public void unregisterDeviceServer(IMidiDeviceServer paramIMidiDeviceServer)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.midi.IMidiManager");
          if (paramIMidiDeviceServer != null) {
            localIBinder = paramIMidiDeviceServer.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterListener(IBinder paramIBinder, IMidiDeviceListener paramIMidiDeviceListener)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.media.midi.IMidiManager");
          localParcel1.writeStrongBinder(paramIBinder);
          paramIBinder = (IBinder)localObject;
          if (paramIMidiDeviceListener != null) {
            paramIBinder = paramIMidiDeviceListener.asBinder();
          }
          localParcel1.writeStrongBinder(paramIBinder);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/IMidiManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */