package android.hardware;

import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraDeviceCallbacks.Stub;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.ICameraDeviceUser.Stub;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.VendorTagDescriptor;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ICameraService
  extends IInterface
{
  public static final int API_VERSION_1 = 1;
  public static final int API_VERSION_2 = 2;
  public static final int CAMERA_HAL_API_VERSION_UNSPECIFIED = -1;
  public static final int CAMERA_TYPE_ALL = 1;
  public static final int CAMERA_TYPE_BACKWARD_COMPATIBLE = 0;
  public static final int ERROR_ALREADY_EXISTS = 2;
  public static final int ERROR_CAMERA_IN_USE = 7;
  public static final int ERROR_DEPRECATED_HAL = 9;
  public static final int ERROR_DISABLED = 6;
  public static final int ERROR_DISCONNECTED = 4;
  public static final int ERROR_ILLEGAL_ARGUMENT = 3;
  public static final int ERROR_INVALID_OPERATION = 10;
  public static final int ERROR_MAX_CAMERAS_IN_USE = 8;
  public static final int ERROR_PERMISSION_DENIED = 1;
  public static final int ERROR_TIMED_OUT = 5;
  public static final int EVENT_NONE = 0;
  public static final int EVENT_USER_SWITCHED = 1;
  public static final int USE_CALLING_PID = -1;
  public static final int USE_CALLING_UID = -1;
  
  public abstract void addListener(ICameraServiceListener paramICameraServiceListener)
    throws RemoteException;
  
  public abstract ICamera connect(ICameraClient paramICameraClient, int paramInt1, String paramString, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract ICameraDeviceUser connectDevice(ICameraDeviceCallbacks paramICameraDeviceCallbacks, int paramInt1, String paramString, int paramInt2)
    throws RemoteException;
  
  public abstract ICamera connectLegacy(ICameraClient paramICameraClient, int paramInt1, int paramInt2, String paramString, int paramInt3)
    throws RemoteException;
  
  public abstract CameraMetadataNative getCameraCharacteristics(int paramInt)
    throws RemoteException;
  
  public abstract CameraInfo getCameraInfo(int paramInt)
    throws RemoteException;
  
  public abstract VendorTagDescriptor getCameraVendorTagDescriptor()
    throws RemoteException;
  
  public abstract String getLegacyParameters(int paramInt)
    throws RemoteException;
  
  public abstract int getNumberOfCameras(int paramInt)
    throws RemoteException;
  
  public abstract void notifySystemEvent(int paramInt, int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract void removeListener(ICameraServiceListener paramICameraServiceListener)
    throws RemoteException;
  
  public abstract void setTorchMode(String paramString, boolean paramBoolean, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean supportsCameraApi(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICameraService
  {
    private static final String DESCRIPTOR = "android.hardware.ICameraService";
    static final int TRANSACTION_addListener = 6;
    static final int TRANSACTION_connect = 3;
    static final int TRANSACTION_connectDevice = 4;
    static final int TRANSACTION_connectLegacy = 5;
    static final int TRANSACTION_getCameraCharacteristics = 8;
    static final int TRANSACTION_getCameraInfo = 2;
    static final int TRANSACTION_getCameraVendorTagDescriptor = 9;
    static final int TRANSACTION_getLegacyParameters = 10;
    static final int TRANSACTION_getNumberOfCameras = 1;
    static final int TRANSACTION_notifySystemEvent = 13;
    static final int TRANSACTION_removeListener = 7;
    static final int TRANSACTION_setTorchMode = 12;
    static final int TRANSACTION_supportsCameraApi = 11;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.ICameraService");
    }
    
    public static ICameraService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.ICameraService");
      if ((localIInterface != null) && ((localIInterface instanceof ICameraService))) {
        return (ICameraService)localIInterface;
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
        paramParcel2.writeString("android.hardware.ICameraService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramInt1 = getNumberOfCameras(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramParcel1 = getCameraInfo(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramParcel1 = connect(ICameraClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramParcel1 = connectDevice(ICameraDeviceCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramParcel1 = connectLegacy(ICameraClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        addListener(ICameraServiceListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        removeListener(ICameraServiceListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramParcel1 = getCameraCharacteristics(paramParcel1.readInt());
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
      case 9: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramParcel1 = getCameraVendorTagDescriptor();
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
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        paramParcel1 = getLegacyParameters(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        bool = supportsCameraApi(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.hardware.ICameraService");
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setTorchMode(str, bool, paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.hardware.ICameraService");
      notifySystemEvent(paramParcel1.readInt(), paramParcel1.createIntArray());
      return true;
    }
    
    private static class Proxy
      implements ICameraService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addListener(ICameraServiceListener paramICameraServiceListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          if (paramICameraServiceListener != null) {
            localIBinder = paramICameraServiceListener.asBinder();
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public ICamera connect(ICameraClient paramICameraClient, int paramInt1, String paramString, int paramInt2, int paramInt3)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          if (paramICameraClient != null) {
            localIBinder = paramICameraClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramICameraClient = ICamera.Stub.asInterface(localParcel2.readStrongBinder());
          return paramICameraClient;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ICameraDeviceUser connectDevice(ICameraDeviceCallbacks paramICameraDeviceCallbacks, int paramInt1, String paramString, int paramInt2)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          if (paramICameraDeviceCallbacks != null) {
            localIBinder = paramICameraDeviceCallbacks.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramICameraDeviceCallbacks = ICameraDeviceUser.Stub.asInterface(localParcel2.readStrongBinder());
          return paramICameraDeviceCallbacks;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ICamera connectLegacy(ICameraClient paramICameraClient, int paramInt1, int paramInt2, String paramString, int paramInt3)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          if (paramICameraClient != null) {
            localIBinder = paramICameraClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramICameraClient = ICamera.Stub.asInterface(localParcel2.readStrongBinder());
          return paramICameraClient;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public CameraMetadataNative getCameraCharacteristics(int paramInt)
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
        //   16: iload_1
        //   17: invokevirtual 67	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/hardware/ICameraService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 8
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 51 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 54	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 97	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 103	android/hardware/camera2/impl/CameraMetadataNative:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 109 2 0
        //   59: checkcast 99	android/hardware/camera2/impl/CameraMetadataNative
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 57	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 57	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 57	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localCameraMetadataNative	CameraMetadataNative
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public CameraInfo getCameraInfo(int paramInt)
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
        //   16: iload_1
        //   17: invokevirtual 67	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/hardware/ICameraService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_2
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 51 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 54	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 97	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 114	android/hardware/CameraInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 109 2 0
        //   58: checkcast 113	android/hardware/CameraInfo
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 57	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 57	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 57	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 57	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt	int
        //   61	14	2	localCameraInfo	CameraInfo
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      /* Error */
      public VendorTagDescriptor getCameraVendorTagDescriptor()
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
        //   15: getfield 19	android/hardware/ICameraService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 9
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 51 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 54	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 97	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 119	android/hardware/camera2/params/VendorTagDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 109 2 0
        //   49: checkcast 118	android/hardware/camera2/params/VendorTagDescriptor
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 57	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 57	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 57	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localVendorTagDescriptor	VendorTagDescriptor
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.ICameraService";
      }
      
      public String getLegacyParameters(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public int getNumberOfCameras(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void notifySystemEvent(int paramInt, int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.ICameraService");
          localParcel.writeInt(paramInt);
          localParcel.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(13, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void removeListener(ICameraServiceListener paramICameraServiceListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          if (paramICameraServiceListener != null) {
            localIBinder = paramICameraServiceListener.asBinder();
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
      
      public void setTorchMode(String paramString, boolean paramBoolean, IBinder paramIBinder)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.ICameraService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      /* Error */
      public boolean supportsCameraApi(int paramInt1, int paramInt2)
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
        //   19: iload_1
        //   20: invokevirtual 67	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 67	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/ICameraService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 97	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 57	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 57	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore 6
        //   82: aload 5
        //   84: invokevirtual 57	android/os/Parcel:recycle	()V
        //   87: aload 4
        //   89: invokevirtual 57	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	95	0	this	Proxy
        //   0	95	1	paramInt1	int
        //   0	95	2	paramInt2	int
        //   62	15	3	bool	boolean
        //   3	85	4	localParcel1	Parcel
        //   8	75	5	localParcel2	Parcel
        //   80	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/ICameraService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */