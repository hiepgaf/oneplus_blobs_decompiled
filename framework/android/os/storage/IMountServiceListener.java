package android.os.storage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IMountServiceListener
  extends IInterface
{
  public abstract void onDiskDestroyed(DiskInfo paramDiskInfo)
    throws RemoteException;
  
  public abstract void onDiskScanned(DiskInfo paramDiskInfo, int paramInt)
    throws RemoteException;
  
  public abstract void onStorageStateChanged(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract void onUsbMassStorageConnectionChanged(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onVolumeForgotten(String paramString)
    throws RemoteException;
  
  public abstract void onVolumeRecordChanged(VolumeRecord paramVolumeRecord)
    throws RemoteException;
  
  public abstract void onVolumeStateChanged(VolumeInfo paramVolumeInfo, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMountServiceListener
  {
    private static final String DESCRIPTOR = "IMountServiceListener";
    static final int TRANSACTION_onDiskDestroyed = 7;
    static final int TRANSACTION_onDiskScanned = 6;
    static final int TRANSACTION_onStorageStateChanged = 2;
    static final int TRANSACTION_onUsbMassStorageConnectionChanged = 1;
    static final int TRANSACTION_onVolumeForgotten = 5;
    static final int TRANSACTION_onVolumeRecordChanged = 4;
    static final int TRANSACTION_onVolumeStateChanged = 3;
    
    public Stub()
    {
      attachInterface(this, "IMountServiceListener");
    }
    
    public static IMountServiceListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("IMountServiceListener");
      if ((localIInterface != null) && ((localIInterface instanceof IMountServiceListener))) {
        return (IMountServiceListener)localIInterface;
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
        paramParcel2.writeString("IMountServiceListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("IMountServiceListener");
        if (paramParcel1.readInt() != 0) {}
        for (boolean bool = true;; bool = false)
        {
          onUsbMassStorageConnectionChanged(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("IMountServiceListener");
        onStorageStateChanged(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("IMountServiceListener");
        onVolumeStateChanged((VolumeInfo)paramParcel1.readParcelable(null), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("IMountServiceListener");
        onVolumeRecordChanged((VolumeRecord)paramParcel1.readParcelable(null));
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("IMountServiceListener");
        onVolumeForgotten(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("IMountServiceListener");
        onDiskScanned((DiskInfo)paramParcel1.readParcelable(null), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("IMountServiceListener");
      onDiskDestroyed((DiskInfo)paramParcel1.readParcelable(null));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IMountServiceListener
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
        return "IMountServiceListener";
      }
      
      public void onDiskDestroyed(DiskInfo paramDiskInfo)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountServiceListener");
          localParcel1.writeParcelable(paramDiskInfo, 0);
          this.mRemote.transact(7, localParcel1, localParcel2, 1);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onDiskScanned(DiskInfo paramDiskInfo, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountServiceListener");
          localParcel1.writeParcelable(paramDiskInfo, 0);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(6, localParcel1, localParcel2, 1);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onStorageStateChanged(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountServiceListener");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(2, localParcel1, localParcel2, 1);
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
      public void onUsbMassStorageConnectionChanged(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +38 -> 56
        //   21: aload_3
        //   22: iload_2
        //   23: invokevirtual 63	android/os/Parcel:writeInt	(I)V
        //   26: aload_0
        //   27: getfield 19	android/os/storage/IMountServiceListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   30: iconst_1
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_1
        //   35: invokeinterface 50 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 53	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 56	android/os/Parcel:recycle	()V
        //   51: aload_3
        //   52: invokevirtual 56	android/os/Parcel:recycle	()V
        //   55: return
        //   56: iconst_0
        //   57: istore_2
        //   58: goto -37 -> 21
        //   61: astore 5
        //   63: aload 4
        //   65: invokevirtual 56	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 56	android/os/Parcel:recycle	()V
        //   72: aload 5
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramBoolean	boolean
        //   1	57	2	i	int
        //   5	64	3	localParcel1	Parcel
        //   9	55	4	localParcel2	Parcel
        //   61	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	61	finally
        //   21	46	61	finally
      }
      
      public void onVolumeForgotten(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountServiceListener");
          localParcel1.writeString(paramString);
          this.mRemote.transact(5, localParcel1, localParcel2, 1);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onVolumeRecordChanged(VolumeRecord paramVolumeRecord)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountServiceListener");
          localParcel1.writeParcelable(paramVolumeRecord, 0);
          this.mRemote.transact(4, localParcel1, localParcel2, 1);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onVolumeStateChanged(VolumeInfo paramVolumeInfo, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountServiceListener");
          localParcel1.writeParcelable(paramVolumeInfo, 0);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(3, localParcel1, localParcel2, 1);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/IMountServiceListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */