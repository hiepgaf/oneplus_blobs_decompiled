package android.hardware.camera2;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.Surface;

public abstract interface ICameraDeviceUser
  extends IInterface
{
  public static final int NO_IN_FLIGHT_REPEATING_FRAMES = -1;
  public static final int TEMPLATE_MANUAL = 6;
  public static final int TEMPLATE_PREVIEW = 1;
  public static final int TEMPLATE_RECORD = 3;
  public static final int TEMPLATE_STILL_CAPTURE = 2;
  public static final int TEMPLATE_VIDEO_SNAPSHOT = 4;
  public static final int TEMPLATE_ZERO_SHUTTER_LAG = 5;
  
  public abstract void beginConfigure()
    throws RemoteException;
  
  public abstract long cancelRequest(int paramInt)
    throws RemoteException;
  
  public abstract CameraMetadataNative createDefaultRequest(int paramInt)
    throws RemoteException;
  
  public abstract int createInputStream(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract int createStream(OutputConfiguration paramOutputConfiguration)
    throws RemoteException;
  
  public abstract void deleteStream(int paramInt)
    throws RemoteException;
  
  public abstract void disconnect()
    throws RemoteException;
  
  public abstract void endConfigure(boolean paramBoolean)
    throws RemoteException;
  
  public abstract long flush()
    throws RemoteException;
  
  public abstract CameraMetadataNative getCameraInfo()
    throws RemoteException;
  
  public abstract Surface getInputSurface()
    throws RemoteException;
  
  public abstract void prepare(int paramInt)
    throws RemoteException;
  
  public abstract void prepare2(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setDeferredConfiguration(int paramInt, OutputConfiguration paramOutputConfiguration)
    throws RemoteException;
  
  public abstract SubmitInfo submitRequest(CaptureRequest paramCaptureRequest, boolean paramBoolean)
    throws RemoteException;
  
  public abstract SubmitInfo submitRequestList(CaptureRequest[] paramArrayOfCaptureRequest, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void tearDown(int paramInt)
    throws RemoteException;
  
  public abstract void waitUntilIdle()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICameraDeviceUser
  {
    private static final String DESCRIPTOR = "android.hardware.camera2.ICameraDeviceUser";
    static final int TRANSACTION_beginConfigure = 5;
    static final int TRANSACTION_cancelRequest = 4;
    static final int TRANSACTION_createDefaultRequest = 11;
    static final int TRANSACTION_createInputStream = 9;
    static final int TRANSACTION_createStream = 8;
    static final int TRANSACTION_deleteStream = 7;
    static final int TRANSACTION_disconnect = 1;
    static final int TRANSACTION_endConfigure = 6;
    static final int TRANSACTION_flush = 14;
    static final int TRANSACTION_getCameraInfo = 12;
    static final int TRANSACTION_getInputSurface = 10;
    static final int TRANSACTION_prepare = 15;
    static final int TRANSACTION_prepare2 = 17;
    static final int TRANSACTION_setDeferredConfiguration = 18;
    static final int TRANSACTION_submitRequest = 2;
    static final int TRANSACTION_submitRequestList = 3;
    static final int TRANSACTION_tearDown = 16;
    static final int TRANSACTION_waitUntilIdle = 13;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.camera2.ICameraDeviceUser");
    }
    
    public static ICameraDeviceUser asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.camera2.ICameraDeviceUser");
      if ((localIInterface != null) && ((localIInterface instanceof ICameraDeviceUser))) {
        return (ICameraDeviceUser)localIInterface;
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
      Object localObject;
      boolean bool;
      label235:
      label271:
      label277:
      label349:
      long l;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.camera2.ICameraDeviceUser");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        disconnect();
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (CaptureRequest)CaptureRequest.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label271;
          }
          bool = true;
          paramParcel1 = submitRequest((CaptureRequest)localObject, bool);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label277;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject = null;
          break;
          bool = false;
          break label235;
          paramParcel2.writeInt(0);
        }
      case 3: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        localObject = (CaptureRequest[])paramParcel1.createTypedArray(CaptureRequest.CREATOR);
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          paramParcel1 = submitRequestList((CaptureRequest[])localObject, bool);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label349;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          bool = false;
          break;
          paramParcel2.writeInt(0);
        }
      case 4: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        l = cancelRequest(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        beginConfigure();
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          endConfigure(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        deleteStream(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (OutputConfiguration)OutputConfiguration.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = createStream(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        paramInt1 = createInputStream(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        paramParcel1 = getInputSurface();
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
      case 11: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        paramParcel1 = createDefaultRequest(paramParcel1.readInt());
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
      case 12: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        paramParcel1 = getCameraInfo();
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
      case 13: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        waitUntilIdle();
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        l = flush();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        prepare(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        tearDown(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
        prepare2(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceUser");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (OutputConfiguration)OutputConfiguration.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        setDeferredConfiguration(paramInt1, paramParcel1);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements ICameraDeviceUser
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
      
      public void beginConfigure()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
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
      
      public long cancelRequest(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public CameraMetadataNative createDefaultRequest(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/hardware/camera2/ICameraDeviceUser$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 11
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 43 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 46	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 66	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 72	android/hardware/camera2/impl/CameraMetadataNative:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 78 2 0
        //   59: checkcast 68	android/hardware/camera2/impl/CameraMetadataNative
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 49	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 49	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 49	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 49	android/os/Parcel:recycle	()V
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
      
      public int createInputStream(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public int createStream(OutputConfiguration paramOutputConfiguration)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 88	android/hardware/camera2/params/OutputConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/hardware/camera2/ICameraDeviceUser$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 8
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 43 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 46	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 66	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 49	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 49	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 49	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 49	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramOutputConfiguration	OutputConfiguration
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      public void deleteStream(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          localParcel1.writeInt(paramInt);
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
      
      public void disconnect()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void endConfigure(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public long flush()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public CameraMetadataNative getCameraInfo()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/hardware/camera2/ICameraDeviceUser$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 12
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 43 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 46	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 66	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 72	android/hardware/camera2/impl/CameraMetadataNative:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 78 2 0
        //   49: checkcast 68	android/hardware/camera2/impl/CameraMetadataNative
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 49	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 49	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localCameraMetadataNative	CameraMetadataNative
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public Surface getInputSurface()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 33
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/hardware/camera2/ICameraDeviceUser$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 10
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 43 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 46	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 66	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 100	android/view/Surface:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 78 2 0
        //   49: checkcast 99	android/view/Surface
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 49	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 49	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localSurface	Surface
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.camera2.ICameraDeviceUser";
      }
      
      public void prepare(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          localParcel1.writeInt(paramInt);
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
      
      public void prepare2(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      /* Error */
      public void setDeferredConfiguration(int paramInt, OutputConfiguration paramOutputConfiguration)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 88	android/hardware/camera2/params/OutputConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/camera2/ICameraDeviceUser$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 18
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 43 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 46	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 49	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_2
        //   75: aload 4
        //   77: invokevirtual 49	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 49	android/os/Parcel:recycle	()V
        //   84: aload_2
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInt	int
        //   0	86	2	paramOutputConfiguration	OutputConfiguration
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public SubmitInfo submitRequest(CaptureRequest paramCaptureRequest, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
            if (paramCaptureRequest != null)
            {
              localParcel1.writeInt(1);
              paramCaptureRequest.writeToParcel(localParcel1, 0);
              break label132;
              localParcel1.writeInt(i);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramCaptureRequest = (SubmitInfo)SubmitInfo.CREATOR.createFromParcel(localParcel2);
                label88:
                return paramCaptureRequest;
              }
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label132:
          do
          {
            i = 0;
            break;
            paramCaptureRequest = null;
            break label88;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public SubmitInfo submitRequestList(CaptureRequest[] paramArrayOfCaptureRequest, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 33
        //   16: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: aload_1
        //   22: iconst_0
        //   23: invokevirtual 121	android/os/Parcel:writeTypedArray	([Landroid/os/Parcelable;I)V
        //   26: iload_2
        //   27: ifeq +5 -> 32
        //   30: iconst_1
        //   31: istore_3
        //   32: aload 4
        //   34: iload_3
        //   35: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   38: aload_0
        //   39: getfield 19	android/hardware/camera2/ICameraDeviceUser$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   42: iconst_3
        //   43: aload 4
        //   45: aload 5
        //   47: iconst_0
        //   48: invokeinterface 43 5 0
        //   53: pop
        //   54: aload 5
        //   56: invokevirtual 46	android/os/Parcel:readException	()V
        //   59: aload 5
        //   61: invokevirtual 66	android/os/Parcel:readInt	()I
        //   64: ifeq +29 -> 93
        //   67: getstatic 115	android/hardware/camera2/utils/SubmitInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   70: aload 5
        //   72: invokeinterface 78 2 0
        //   77: checkcast 114	android/hardware/camera2/utils/SubmitInfo
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 49	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 49	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: areturn
        //   93: aconst_null
        //   94: astore_1
        //   95: goto -14 -> 81
        //   98: astore_1
        //   99: aload 5
        //   101: invokevirtual 49	android/os/Parcel:recycle	()V
        //   104: aload 4
        //   106: invokevirtual 49	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramArrayOfCaptureRequest	CaptureRequest[]
        //   0	111	2	paramBoolean	boolean
        //   1	34	3	i	int
        //   5	100	4	localParcel1	Parcel
        //   10	90	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	26	98	finally
        //   32	81	98	finally
      }
      
      public void tearDown(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
          localParcel1.writeInt(paramInt);
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
      
      public void waitUntilIdle()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.camera2.ICameraDeviceUser");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/ICameraDeviceUser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */