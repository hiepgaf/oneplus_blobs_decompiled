package android.hardware.fingerprint;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IFingerprintDaemon
  extends IInterface
{
  public abstract int authenticate(long paramLong, int paramInt)
    throws RemoteException;
  
  public abstract int cancelAuthentication()
    throws RemoteException;
  
  public abstract int cancelEnrollment()
    throws RemoteException;
  
  public abstract int cancelEnumeration()
    throws RemoteException;
  
  public abstract int closeHal()
    throws RemoteException;
  
  public abstract int enroll(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int enumerate()
    throws RemoteException;
  
  public abstract long getAuthenticatorId()
    throws RemoteException;
  
  public abstract int getStatus()
    throws RemoteException;
  
  public abstract void init(IFingerprintDaemonCallback paramIFingerprintDaemonCallback)
    throws RemoteException;
  
  public abstract long openHal()
    throws RemoteException;
  
  public abstract int postEnroll()
    throws RemoteException;
  
  public abstract long preEnroll()
    throws RemoteException;
  
  public abstract int remove(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int setActiveGroup(int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract int updateStatus(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintDaemon
  {
    private static final String DESCRIPTOR = "android.hardware.fingerprint.IFingerprintDaemon";
    static final int TRANSACTION_authenticate = 1;
    static final int TRANSACTION_cancelAuthentication = 2;
    static final int TRANSACTION_cancelEnrollment = 4;
    static final int TRANSACTION_cancelEnumeration = 14;
    static final int TRANSACTION_closeHal = 10;
    static final int TRANSACTION_enroll = 3;
    static final int TRANSACTION_enumerate = 13;
    static final int TRANSACTION_getAuthenticatorId = 7;
    static final int TRANSACTION_getStatus = 16;
    static final int TRANSACTION_init = 11;
    static final int TRANSACTION_openHal = 9;
    static final int TRANSACTION_postEnroll = 12;
    static final int TRANSACTION_preEnroll = 5;
    static final int TRANSACTION_remove = 6;
    static final int TRANSACTION_setActiveGroup = 8;
    static final int TRANSACTION_updateStatus = 15;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.fingerprint.IFingerprintDaemon");
    }
    
    public static IFingerprintDaemon asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.fingerprint.IFingerprintDaemon");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintDaemon))) {
        return (IFingerprintDaemon)localIInterface;
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
      long l;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.fingerprint.IFingerprintDaemon");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = authenticate(paramParcel1.readLong(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = cancelAuthentication();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = enroll(paramParcel1.createByteArray(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = cancelEnrollment();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        l = preEnroll();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = remove(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        l = getAuthenticatorId();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = setActiveGroup(paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        l = openHal();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = closeHal();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        init(IFingerprintDaemonCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = postEnroll();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = enumerate();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = cancelEnumeration();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
        paramInt1 = updateStatus(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintDaemon");
      paramInt1 = getStatus();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IFingerprintDaemon
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
      
      public int authenticate(long paramLong, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          localParcel1.writeLong(paramLong);
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
      
      public int cancelAuthentication()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
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
      
      public int cancelEnrollment()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
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
      
      public int cancelEnumeration()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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
      
      public int closeHal()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public int enroll(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      
      public int enumerate()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
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
      
      public long getAuthenticatorId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.fingerprint.IFingerprintDaemon";
      }
      
      public int getStatus()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
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
      
      public void init(IFingerprintDaemonCallback paramIFingerprintDaemonCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          if (paramIFingerprintDaemonCallback != null) {
            localIBinder = paramIFingerprintDaemonCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long openHal()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      
      public int postEnroll()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      
      public long preEnroll()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
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
      
      public int remove(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
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
      
      public int setActiveGroup(int paramInt, byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          localParcel1.writeInt(paramInt);
          localParcel1.writeByteArray(paramArrayOfByte);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
      
      public int updateStatus(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintDaemon");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/fingerprint/IFingerprintDaemon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */