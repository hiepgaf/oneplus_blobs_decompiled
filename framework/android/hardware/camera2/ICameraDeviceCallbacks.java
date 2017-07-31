package android.hardware.camera2;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface ICameraDeviceCallbacks
  extends IInterface
{
  public static final int ERROR_CAMERA_BUFFER = 5;
  public static final int ERROR_CAMERA_DEVICE = 1;
  public static final int ERROR_CAMERA_DISCONNECTED = 0;
  public static final int ERROR_CAMERA_INVALID_ERROR = -1;
  public static final int ERROR_CAMERA_REQUEST = 3;
  public static final int ERROR_CAMERA_RESULT = 4;
  public static final int ERROR_CAMERA_SERVICE = 2;
  
  public abstract void onCaptureStarted(CaptureResultExtras paramCaptureResultExtras, long paramLong)
    throws RemoteException;
  
  public abstract void onDeviceError(int paramInt, CaptureResultExtras paramCaptureResultExtras)
    throws RemoteException;
  
  public abstract void onDeviceIdle()
    throws RemoteException;
  
  public abstract void onPrepared(int paramInt)
    throws RemoteException;
  
  public abstract void onRepeatingRequestError(long paramLong)
    throws RemoteException;
  
  public abstract void onResultReceived(CameraMetadataNative paramCameraMetadataNative, CaptureResultExtras paramCaptureResultExtras)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ICameraDeviceCallbacks
  {
    private static final String DESCRIPTOR = "android.hardware.camera2.ICameraDeviceCallbacks";
    static final int TRANSACTION_onCaptureStarted = 3;
    static final int TRANSACTION_onDeviceError = 1;
    static final int TRANSACTION_onDeviceIdle = 2;
    static final int TRANSACTION_onPrepared = 5;
    static final int TRANSACTION_onRepeatingRequestError = 6;
    static final int TRANSACTION_onResultReceived = 4;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.camera2.ICameraDeviceCallbacks");
    }
    
    public static ICameraDeviceCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.camera2.ICameraDeviceCallbacks");
      if ((localIInterface != null) && ((localIInterface instanceof ICameraDeviceCallbacks))) {
        return (ICameraDeviceCallbacks)localIInterface;
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
        paramParcel2.writeString("android.hardware.camera2.ICameraDeviceCallbacks");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceCallbacks");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (CaptureResultExtras)CaptureResultExtras.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onDeviceError(paramInt1, paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceCallbacks");
        onDeviceIdle();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceCallbacks");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (CaptureResultExtras)CaptureResultExtras.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          onCaptureStarted(paramParcel2, paramParcel1.readLong());
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceCallbacks");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (CameraMetadataNative)CameraMetadataNative.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label243;
          }
        }
        for (paramParcel1 = (CaptureResultExtras)CaptureResultExtras.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onResultReceived(paramParcel2, paramParcel1);
          return true;
          paramParcel2 = null;
          break;
        }
      case 5: 
        label243:
        paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceCallbacks");
        onPrepared(paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.camera2.ICameraDeviceCallbacks");
      onRepeatingRequestError(paramParcel1.readLong());
      return true;
    }
    
    private static class Proxy
      implements ICameraDeviceCallbacks
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
        return "android.hardware.camera2.ICameraDeviceCallbacks";
      }
      
      /* Error */
      public void onCaptureStarted(CaptureResultExtras paramCaptureResultExtras, long paramLong)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: aload 4
        //   7: ldc 26
        //   9: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   12: aload_1
        //   13: ifnull +43 -> 56
        //   16: aload 4
        //   18: iconst_1
        //   19: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   22: aload_1
        //   23: aload 4
        //   25: iconst_0
        //   26: invokevirtual 50	android/hardware/camera2/impl/CaptureResultExtras:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload 4
        //   31: lload_2
        //   32: invokevirtual 54	android/os/Parcel:writeLong	(J)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/camera2/ICameraDeviceCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_3
        //   40: aload 4
        //   42: aconst_null
        //   43: iconst_1
        //   44: invokeinterface 60 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 63	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload 4
        //   58: iconst_0
        //   59: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload 4
        //   68: invokevirtual 63	android/os/Parcel:recycle	()V
        //   71: aload_1
        //   72: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	73	0	this	Proxy
        //   0	73	1	paramCaptureResultExtras	CaptureResultExtras
        //   0	73	2	paramLong	long
        //   3	64	4	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   5	12	65	finally
        //   16	29	65	finally
        //   29	50	65	finally
        //   56	62	65	finally
      }
      
      /* Error */
      public void onDeviceError(int paramInt, CaptureResultExtras paramCaptureResultExtras)
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
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 50	android/hardware/camera2/impl/CaptureResultExtras:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/hardware/camera2/ICameraDeviceCallbacks$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 60 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 63	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_2
        //   58: aload_3
        //   59: invokevirtual 63	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramInt	int
        //   0	64	2	paramCaptureResultExtras	CaptureResultExtras
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
      
      public void onDeviceIdle()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.camera2.ICameraDeviceCallbacks");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onPrepared(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.camera2.ICameraDeviceCallbacks");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onRepeatingRequestError(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.hardware.camera2.ICameraDeviceCallbacks");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onResultReceived(CameraMetadataNative paramCameraMetadataNative, CaptureResultExtras paramCaptureResultExtras)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.hardware.camera2.ICameraDeviceCallbacks");
            if (paramCameraMetadataNative != null)
            {
              localParcel.writeInt(1);
              paramCameraMetadataNative.writeToParcel(localParcel, 0);
              if (paramCaptureResultExtras != null)
              {
                localParcel.writeInt(1);
                paramCaptureResultExtras.writeToParcel(localParcel, 0);
                this.mRemote.transact(4, localParcel, null, 1);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/ICameraDeviceCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */