package android.hardware.hdmi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IHdmiControlService
  extends IInterface
{
  public abstract void addDeviceEventListener(IHdmiDeviceEventListener paramIHdmiDeviceEventListener)
    throws RemoteException;
  
  public abstract void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener paramIHdmiMhlVendorCommandListener)
    throws RemoteException;
  
  public abstract void addHotplugEventListener(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
    throws RemoteException;
  
  public abstract void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
    throws RemoteException;
  
  public abstract void addVendorCommandListener(IHdmiVendorCommandListener paramIHdmiVendorCommandListener, int paramInt)
    throws RemoteException;
  
  public abstract boolean canChangeSystemAudioMode()
    throws RemoteException;
  
  public abstract void clearTimerRecording(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void deviceSelect(int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
    throws RemoteException;
  
  public abstract HdmiDeviceInfo getActiveSource()
    throws RemoteException;
  
  public abstract List<HdmiDeviceInfo> getDeviceList()
    throws RemoteException;
  
  public abstract List<HdmiDeviceInfo> getInputDevices()
    throws RemoteException;
  
  public abstract List<HdmiPortInfo> getPortInfo()
    throws RemoteException;
  
  public abstract int[] getSupportedTypes()
    throws RemoteException;
  
  public abstract boolean getSystemAudioMode()
    throws RemoteException;
  
  public abstract void oneTouchPlay(IHdmiControlCallback paramIHdmiControlCallback)
    throws RemoteException;
  
  public abstract void portSelect(int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
    throws RemoteException;
  
  public abstract void queryDisplayStatus(IHdmiControlCallback paramIHdmiControlCallback)
    throws RemoteException;
  
  public abstract void removeHotplugEventListener(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
    throws RemoteException;
  
  public abstract void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
    throws RemoteException;
  
  public abstract void sendKeyEvent(int paramInt1, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void sendMhlVendorCommand(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void sendStandby(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void sendVendorCommand(int paramInt1, int paramInt2, byte[] paramArrayOfByte, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setArcMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setHdmiRecordListener(IHdmiRecordListener paramIHdmiRecordListener)
    throws RemoteException;
  
  public abstract void setInputChangeListener(IHdmiInputChangeListener paramIHdmiInputChangeListener)
    throws RemoteException;
  
  public abstract void setProhibitMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSystemAudioMode(boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
    throws RemoteException;
  
  public abstract void setSystemAudioMute(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSystemAudioVolume(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void startOneTouchRecord(int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void startTimerRecording(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void stopOneTouchRecord(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IHdmiControlService
  {
    private static final String DESCRIPTOR = "android.hardware.hdmi.IHdmiControlService";
    static final int TRANSACTION_addDeviceEventListener = 7;
    static final int TRANSACTION_addHdmiMhlVendorCommandListener = 33;
    static final int TRANSACTION_addHotplugEventListener = 5;
    static final int TRANSACTION_addSystemAudioModeChangeListener = 15;
    static final int TRANSACTION_addVendorCommandListener = 25;
    static final int TRANSACTION_canChangeSystemAudioMode = 12;
    static final int TRANSACTION_clearTimerRecording = 31;
    static final int TRANSACTION_deviceSelect = 8;
    static final int TRANSACTION_getActiveSource = 2;
    static final int TRANSACTION_getDeviceList = 23;
    static final int TRANSACTION_getInputDevices = 22;
    static final int TRANSACTION_getPortInfo = 11;
    static final int TRANSACTION_getSupportedTypes = 1;
    static final int TRANSACTION_getSystemAudioMode = 13;
    static final int TRANSACTION_oneTouchPlay = 3;
    static final int TRANSACTION_portSelect = 9;
    static final int TRANSACTION_queryDisplayStatus = 4;
    static final int TRANSACTION_removeHotplugEventListener = 6;
    static final int TRANSACTION_removeSystemAudioModeChangeListener = 16;
    static final int TRANSACTION_sendKeyEvent = 10;
    static final int TRANSACTION_sendMhlVendorCommand = 32;
    static final int TRANSACTION_sendStandby = 26;
    static final int TRANSACTION_sendVendorCommand = 24;
    static final int TRANSACTION_setArcMode = 17;
    static final int TRANSACTION_setHdmiRecordListener = 27;
    static final int TRANSACTION_setInputChangeListener = 21;
    static final int TRANSACTION_setProhibitMode = 18;
    static final int TRANSACTION_setSystemAudioMode = 14;
    static final int TRANSACTION_setSystemAudioMute = 20;
    static final int TRANSACTION_setSystemAudioVolume = 19;
    static final int TRANSACTION_startOneTouchRecord = 28;
    static final int TRANSACTION_startTimerRecording = 30;
    static final int TRANSACTION_stopOneTouchRecord = 29;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.hdmi.IHdmiControlService");
    }
    
    public static IHdmiControlService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.hdmi.IHdmiControlService");
      if ((localIInterface != null) && ((localIInterface instanceof IHdmiControlService))) {
        return (IHdmiControlService)localIInterface;
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
        paramParcel2.writeString("android.hardware.hdmi.IHdmiControlService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        paramParcel1 = getSupportedTypes();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        paramParcel1 = getActiveSource();
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
      case 3: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        oneTouchPlay(IHdmiControlCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        queryDisplayStatus(IHdmiControlCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        addHotplugEventListener(IHdmiHotplugEventListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        removeHotplugEventListener(IHdmiHotplugEventListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        addDeviceEventListener(IHdmiDeviceEventListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        deviceSelect(paramParcel1.readInt(), IHdmiControlCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        portSelect(paramParcel1.readInt(), IHdmiControlCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          sendKeyEvent(paramInt1, paramInt2, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        paramParcel1 = getPortInfo();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        bool = canChangeSystemAudioMode();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        bool = getSystemAudioMode();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setSystemAudioMode(bool, IHdmiControlCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setArcMode(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 18: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setProhibitMode(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        setSystemAudioVolume(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setSystemAudioMute(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 21: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        setInputChangeListener(IHdmiInputChangeListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        paramParcel1 = getInputDevices();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        paramParcel1 = getDeviceList();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        byte[] arrayOfByte = paramParcel1.createByteArray();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          sendVendorCommand(paramInt1, paramInt2, arrayOfByte, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 25: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        addVendorCommandListener(IHdmiVendorCommandListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        sendStandby(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        setHdmiRecordListener(IHdmiRecordListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        startOneTouchRecord(paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        stopOneTouchRecord(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 30: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        startTimerRecording(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 31: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        clearTimerRecording(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
        sendMhlVendorCommand(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.hdmi.IHdmiControlService");
      addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IHdmiControlService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addDeviceEventListener(IHdmiDeviceEventListener paramIHdmiDeviceEventListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiDeviceEventListener != null) {
            localIBinder = paramIHdmiDeviceEventListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener paramIHdmiMhlVendorCommandListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiMhlVendorCommandListener != null) {
            localIBinder = paramIHdmiMhlVendorCommandListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addHotplugEventListener(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiHotplugEventListener != null) {
            localIBinder = paramIHdmiHotplugEventListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiSystemAudioModeChangeListener != null) {
            localIBinder = paramIHdmiSystemAudioModeChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addVendorCommandListener(IHdmiVendorCommandListener paramIHdmiVendorCommandListener, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiVendorCommandListener != null) {
            localIBinder = paramIHdmiVendorCommandListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public boolean canChangeSystemAudioMode()
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
        //   16: getfield 19	android/hardware/hdmi/IHdmiControlService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 12
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 51 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 54	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 88	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 57	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 57	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 57	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void clearTimerRecording(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void deviceSelect(int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt);
          if (paramIHdmiControlCallback != null) {
            localIBinder = paramIHdmiControlCallback.asBinder();
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
      
      /* Error */
      public HdmiDeviceInfo getActiveSource()
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
        //   14: aload_0
        //   15: getfield 19	android/hardware/hdmi/IHdmiControlService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_2
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 51 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 54	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 88	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 107	android/hardware/hdmi/HdmiDeviceInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 113 2 0
        //   48: checkcast 103	android/hardware/hdmi/HdmiDeviceInfo
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 57	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 57	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 57	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localHdmiDeviceInfo	HdmiDeviceInfo
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public List<HdmiDeviceInfo> getDeviceList()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(HdmiDeviceInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<HdmiDeviceInfo> getInputDevices()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(HdmiDeviceInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.hdmi.IHdmiControlService";
      }
      
      public List<HdmiPortInfo> getPortInfo()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(HdmiPortInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getSupportedTypes()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean getSystemAudioMode()
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
        //   16: getfield 19	android/hardware/hdmi/IHdmiControlService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 13
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 51 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 54	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 88	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 57	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 57	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 57	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   41	2	1	i	int
        //   47	14	2	bool	boolean
        //   3	69	3	localParcel1	Parcel
        //   7	60	4	localParcel2	Parcel
        //   64	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	42	64	finally
      }
      
      public void oneTouchPlay(IHdmiControlCallback paramIHdmiControlCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiControlCallback != null) {
            localIBinder = paramIHdmiControlCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void portSelect(int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt);
          if (paramIHdmiControlCallback != null) {
            localIBinder = paramIHdmiControlCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void queryDisplayStatus(IHdmiControlCallback paramIHdmiControlCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiControlCallback != null) {
            localIBinder = paramIHdmiControlCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void removeHotplugEventListener(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiHotplugEventListener != null) {
            localIBinder = paramIHdmiHotplugEventListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiSystemAudioModeChangeListener != null) {
            localIBinder = paramIHdmiSystemAudioModeChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void sendKeyEvent(int paramInt1, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void sendMhlVendorCommand(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(32, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void sendStandby(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void sendVendorCommand(int paramInt1, int paramInt2, byte[] paramArrayOfByte, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeByteArray(paramArrayOfByte);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setArcMode(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setHdmiRecordListener(IHdmiRecordListener paramIHdmiRecordListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiRecordListener != null) {
            localIBinder = paramIHdmiRecordListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setInputChangeListener(IHdmiInputChangeListener paramIHdmiInputChangeListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramIHdmiInputChangeListener != null) {
            localIBinder = paramIHdmiInputChangeListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void setProhibitMode(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setSystemAudioMode(boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          if (paramIHdmiControlCallback != null) {
            localIBinder = paramIHdmiControlCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setSystemAudioMute(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setSystemAudioVolume(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
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
      
      public void startOneTouchRecord(int paramInt, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void startTimerRecording(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void stopOneTouchRecord(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.hdmi.IHdmiControlService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/IHdmiControlService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */