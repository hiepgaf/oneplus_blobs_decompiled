package android.service.persistentdata;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IPersistentDataBlockService
  extends IInterface
{
  public abstract int getDataBlockSize()
    throws RemoteException;
  
  public abstract int getFlashLockState()
    throws RemoteException;
  
  public abstract long getMaximumDataBlockSize()
    throws RemoteException;
  
  public abstract boolean getOemUnlockEnabled()
    throws RemoteException;
  
  public abstract byte[] read()
    throws RemoteException;
  
  public abstract void setOemUnlockEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void wipe()
    throws RemoteException;
  
  public abstract int write(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPersistentDataBlockService
  {
    private static final String DESCRIPTOR = "android.service.persistentdata.IPersistentDataBlockService";
    static final int TRANSACTION_getDataBlockSize = 4;
    static final int TRANSACTION_getFlashLockState = 8;
    static final int TRANSACTION_getMaximumDataBlockSize = 5;
    static final int TRANSACTION_getOemUnlockEnabled = 7;
    static final int TRANSACTION_read = 2;
    static final int TRANSACTION_setOemUnlockEnabled = 6;
    static final int TRANSACTION_wipe = 3;
    static final int TRANSACTION_write = 1;
    
    public Stub()
    {
      attachInterface(this, "android.service.persistentdata.IPersistentDataBlockService");
    }
    
    public static IPersistentDataBlockService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.persistentdata.IPersistentDataBlockService");
      if ((localIInterface != null) && ((localIInterface instanceof IPersistentDataBlockService))) {
        return (IPersistentDataBlockService)localIInterface;
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
      int i = 0;
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.persistentdata.IPersistentDataBlockService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
        paramInt1 = write(paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
        paramParcel1 = read();
        paramParcel2.writeNoException();
        paramParcel2.writeByteArray(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
        wipe();
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
        paramInt1 = getDataBlockSize();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
        long l = getMaximumDataBlockSize();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        setOemUnlockEnabled(bool);
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
        bool = getOemUnlockEnabled();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.service.persistentdata.IPersistentDataBlockService");
      paramInt1 = getFlashLockState();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IPersistentDataBlockService
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
      
      public int getDataBlockSize()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.persistentdata.IPersistentDataBlockService");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getFlashLockState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.persistentdata.IPersistentDataBlockService");
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.persistentdata.IPersistentDataBlockService";
      }
      
      public long getMaximumDataBlockSize()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.persistentdata.IPersistentDataBlockService");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
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
      public boolean getOemUnlockEnabled()
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
        //   15: aload_0
        //   16: getfield 19	android/service/persistentdata/IPersistentDataBlockService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 44 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 47	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 50	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 53	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 53	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 53	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 53	android/os/Parcel:recycle	()V
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
      
      public byte[] read()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.persistentdata.IPersistentDataBlockService");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          byte[] arrayOfByte = localParcel2.createByteArray();
          return arrayOfByte;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setOemUnlockEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.persistentdata.IPersistentDataBlockService");
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
      
      public void wipe()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.persistentdata.IPersistentDataBlockService");
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
      
      public int write(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.persistentdata.IPersistentDataBlockService");
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/persistentdata/IPersistentDataBlockService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */