package android.hardware.usb;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IUsbManager
  extends IInterface
{
  public abstract void allowUsbDebugging(boolean paramBoolean, String paramString)
    throws RemoteException;
  
  public abstract void clearDefaults(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void clearUsbDebuggingKeys()
    throws RemoteException;
  
  public abstract void denyUsbDebugging()
    throws RemoteException;
  
  public abstract UsbAccessory getCurrentAccessory()
    throws RemoteException;
  
  public abstract void getDeviceList(Bundle paramBundle)
    throws RemoteException;
  
  public abstract UsbPortStatus getPortStatus(String paramString)
    throws RemoteException;
  
  public abstract UsbPort[] getPorts()
    throws RemoteException;
  
  public abstract void grantAccessoryPermission(UsbAccessory paramUsbAccessory, int paramInt)
    throws RemoteException;
  
  public abstract void grantDevicePermission(UsbDevice paramUsbDevice, int paramInt)
    throws RemoteException;
  
  public abstract boolean hasAccessoryPermission(UsbAccessory paramUsbAccessory)
    throws RemoteException;
  
  public abstract boolean hasDefaults(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean hasDevicePermission(UsbDevice paramUsbDevice)
    throws RemoteException;
  
  public abstract boolean isFunctionEnabled(String paramString)
    throws RemoteException;
  
  public abstract boolean isUsbDataUnlocked()
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openAccessory(UsbAccessory paramUsbAccessory)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openDevice(String paramString)
    throws RemoteException;
  
  public abstract void requestAccessoryPermission(UsbAccessory paramUsbAccessory, String paramString, PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public abstract void requestDevicePermission(UsbDevice paramUsbDevice, String paramString, PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public abstract void setAccessoryPackage(UsbAccessory paramUsbAccessory, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setCurrentFunction(String paramString)
    throws RemoteException;
  
  public abstract void setDevicePackage(UsbDevice paramUsbDevice, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setPortRoles(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setUsbDataUnlocked(boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IUsbManager
  {
    private static final String DESCRIPTOR = "android.hardware.usb.IUsbManager";
    static final int TRANSACTION_allowUsbDebugging = 19;
    static final int TRANSACTION_clearDefaults = 14;
    static final int TRANSACTION_clearUsbDebuggingKeys = 21;
    static final int TRANSACTION_denyUsbDebugging = 20;
    static final int TRANSACTION_getCurrentAccessory = 3;
    static final int TRANSACTION_getDeviceList = 1;
    static final int TRANSACTION_getPortStatus = 23;
    static final int TRANSACTION_getPorts = 22;
    static final int TRANSACTION_grantAccessoryPermission = 12;
    static final int TRANSACTION_grantDevicePermission = 11;
    static final int TRANSACTION_hasAccessoryPermission = 8;
    static final int TRANSACTION_hasDefaults = 13;
    static final int TRANSACTION_hasDevicePermission = 7;
    static final int TRANSACTION_isFunctionEnabled = 15;
    static final int TRANSACTION_isUsbDataUnlocked = 18;
    static final int TRANSACTION_openAccessory = 4;
    static final int TRANSACTION_openDevice = 2;
    static final int TRANSACTION_requestAccessoryPermission = 10;
    static final int TRANSACTION_requestDevicePermission = 9;
    static final int TRANSACTION_setAccessoryPackage = 6;
    static final int TRANSACTION_setCurrentFunction = 16;
    static final int TRANSACTION_setDevicePackage = 5;
    static final int TRANSACTION_setPortRoles = 24;
    static final int TRANSACTION_setUsbDataUnlocked = 17;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.usb.IUsbManager");
    }
    
    public static IUsbManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.usb.IUsbManager");
      if ((localIInterface != null) && ((localIInterface instanceof IUsbManager))) {
        return (IUsbManager)localIInterface;
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
      label420:
      Object localObject;
      boolean bool;
      label590:
      label651:
      String str;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.usb.IUsbManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        paramParcel1 = new Bundle();
        getDeviceList(paramParcel1);
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
      case 2: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        paramParcel1 = openDevice(paramParcel1.readString());
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
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        paramParcel1 = getCurrentAccessory();
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
      case 4: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (UsbAccessory)UsbAccessory.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = openAccessory(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label420;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (UsbDevice)UsbDevice.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setDevicePackage((UsbDevice)localObject, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (UsbAccessory)UsbAccessory.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setAccessoryPackage((UsbAccessory)localObject, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (UsbDevice)UsbDevice.CREATOR.createFromParcel(paramParcel1);
          bool = hasDevicePermission(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label590;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (UsbAccessory)UsbAccessory.CREATOR.createFromParcel(paramParcel1);
          bool = hasAccessoryPermission(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label651;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (UsbDevice)UsbDevice.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label730;
          }
        }
        for (paramParcel1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestDevicePermission((UsbDevice)localObject, str, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 10: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (UsbAccessory)UsbAccessory.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label809;
          }
        }
        for (paramParcel1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestAccessoryPermission((UsbAccessory)localObject, str, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 11: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (UsbDevice)UsbDevice.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          grantDevicePermission((UsbDevice)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (UsbAccessory)UsbAccessory.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          grantAccessoryPermission((UsbAccessory)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        bool = hasDefaults(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        clearDefaults(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        bool = isFunctionEnabled(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        setCurrentFunction(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setUsbDataUnlocked(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 18: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        bool = isUsbDataUnlocked();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          allowUsbDebugging(bool, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        denyUsbDebugging();
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        clearUsbDebuggingKeys();
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        paramParcel1 = getPorts();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 23: 
        label730:
        label809:
        paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
        paramParcel1 = getPortStatus(paramParcel1.readString());
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
      }
      paramParcel1.enforceInterface("android.hardware.usb.IUsbManager");
      setPortRoles(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IUsbManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void allowUsbDebugging(boolean paramBoolean, String paramString)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeString(paramString);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void clearDefaults(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
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
      
      public void clearUsbDebuggingKeys()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
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
      
      public void denyUsbDebugging()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
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
      public UsbAccessory getCurrentAccessory()
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
        //   15: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_3
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 49 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 52	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 68	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 74	android/hardware/usb/UsbAccessory:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 80 2 0
        //   48: checkcast 70	android/hardware/usb/UsbAccessory
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 55	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 55	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 55	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 55	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localUsbAccessory	UsbAccessory
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      public void getDeviceList(Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          if (localParcel2.readInt() != 0) {
            paramBundle.readFromParcel(localParcel2);
          }
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.usb.IUsbManager";
      }
      
      /* Error */
      public UsbPortStatus getPortStatus(String paramString)
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
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 23
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 49 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 52	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 68	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 95	android/hardware/usb/UsbPortStatus:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 80 2 0
        //   54: checkcast 94	android/hardware/usb/UsbPortStatus
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 55	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 55	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 55	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 55	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramString	String
        //   3	76	2	localParcel1	Parcel
        //   7	68	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	58	73	finally
      }
      
      public UsbPort[] getPorts()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          UsbPort[] arrayOfUsbPort = (UsbPort[])localParcel2.createTypedArray(UsbPort.CREATOR);
          return arrayOfUsbPort;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void grantAccessoryPermission(UsbAccessory paramUsbAccessory, int paramInt)
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
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 112	android/hardware/usb/UsbAccessory:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 12
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
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
        //   0	86	1	paramUsbAccessory	UsbAccessory
        //   0	86	2	paramInt	int
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      /* Error */
      public void grantDevicePermission(UsbDevice paramUsbDevice, int paramInt)
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
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 117	android/hardware/usb/UsbDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 11
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
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
        //   0	86	1	paramUsbDevice	UsbDevice
        //   0	86	2	paramInt	int
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public boolean hasAccessoryPermission(UsbAccessory paramUsbAccessory)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
            if (paramUsbAccessory != null)
            {
              localParcel1.writeInt(1);
              paramUsbAccessory.writeToParcel(localParcel1, 0);
              this.mRemote.transact(8, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean hasDefaults(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 13
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 49 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 52	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 68	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 55	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 55	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 55	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public boolean hasDevicePermission(UsbDevice paramUsbDevice)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
            if (paramUsbDevice != null)
            {
              localParcel1.writeInt(1);
              paramUsbDevice.writeToParcel(localParcel1, 0);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public boolean isFunctionEnabled(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 15
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 49 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 52	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 68	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 55	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 55	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 55	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isUsbDataUnlocked()
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
        //   16: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 18
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 68	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 55	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 55	android/os/Parcel:recycle	()V
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
      
      public ParcelFileDescriptor openAccessory(UsbAccessory paramUsbAccessory)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
            if (paramUsbAccessory != null)
            {
              localParcel1.writeInt(1);
              paramUsbAccessory.writeToParcel(localParcel1, 0);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramUsbAccessory = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
                return paramUsbAccessory;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramUsbAccessory = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public ParcelFileDescriptor openDevice(String paramString)
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
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_2
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 49 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 52	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 68	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 132	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 80 2 0
        //   53: checkcast 131	android/os/ParcelFileDescriptor
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 55	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramString	String
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	57	72	finally
      }
      
      public void requestAccessoryPermission(UsbAccessory paramUsbAccessory, String paramString, PendingIntent paramPendingIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
            if (paramUsbAccessory != null)
            {
              localParcel1.writeInt(1);
              paramUsbAccessory.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramPendingIntent != null)
              {
                localParcel1.writeInt(1);
                paramPendingIntent.writeToParcel(localParcel1, 0);
                this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
        }
      }
      
      public void requestDevicePermission(UsbDevice paramUsbDevice, String paramString, PendingIntent paramPendingIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
            if (paramUsbDevice != null)
            {
              localParcel1.writeInt(1);
              paramUsbDevice.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramPendingIntent != null)
              {
                localParcel1.writeInt(1);
                paramPendingIntent.writeToParcel(localParcel1, 0);
                this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
        }
      }
      
      /* Error */
      public void setAccessoryPackage(UsbAccessory paramUsbAccessory, String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +61 -> 79
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 112	android/hardware/usb/UsbAccessory:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 6
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 49 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 52	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 55	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 55	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -51 -> 34
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 55	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 55	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramUsbAccessory	UsbAccessory
        //   0	101	2	paramString	String
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	88	finally
        //   21	34	88	finally
        //   34	68	88	finally
        //   79	85	88	finally
      }
      
      public void setCurrentFunction(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
          localParcel1.writeString(paramString);
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
      
      /* Error */
      public void setDevicePackage(UsbDevice paramUsbDevice, String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +60 -> 78
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 117	android/hardware/usb/UsbDevice:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/hardware/usb/IUsbManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_5
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 49 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 52	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 55	android/os/Parcel:recycle	()V
        //   72: aload 4
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: return
        //   78: aload 4
        //   80: iconst_0
        //   81: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   84: goto -50 -> 34
        //   87: astore_1
        //   88: aload 5
        //   90: invokevirtual 55	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: invokevirtual 55	android/os/Parcel:recycle	()V
        //   98: aload_1
        //   99: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	100	0	this	Proxy
        //   0	100	1	paramUsbDevice	UsbDevice
        //   0	100	2	paramString	String
        //   0	100	3	paramInt	int
        //   3	91	4	localParcel1	Parcel
        //   8	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	87	finally
        //   21	34	87	finally
        //   34	67	87	finally
        //   78	84	87	finally
      }
      
      public void setPortRoles(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      public void setUsbDataUnlocked(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.usb.IUsbManager");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/IUsbManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */