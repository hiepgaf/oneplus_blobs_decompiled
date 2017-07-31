package android.hardware.fingerprint;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public abstract interface IFingerprintService
  extends IInterface
{
  public abstract void addLockoutResetCallback(IFingerprintServiceLockoutResetCallback paramIFingerprintServiceLockoutResetCallback)
    throws RemoteException;
  
  public abstract void authenticate(IBinder paramIBinder, long paramLong, int paramInt1, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt2, String paramString)
    throws RemoteException;
  
  public abstract void cancelAuthentication(IBinder paramIBinder, String paramString)
    throws RemoteException;
  
  public abstract void cancelEnrollment(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void enroll(IBinder paramIBinder, byte[] paramArrayOfByte, int paramInt1, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt2, String paramString)
    throws RemoteException;
  
  public abstract void forceStopAuthentication(String paramString)
    throws RemoteException;
  
  public abstract String getAuthenticatedPackage()
    throws RemoteException;
  
  public abstract long getAuthenticatorId(String paramString)
    throws RemoteException;
  
  public abstract List<Fingerprint> getEnrolledFingerprints(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract int getStatus()
    throws RemoteException;
  
  public abstract boolean hasEnrolledFingerprints(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract boolean isHardwareDetected(long paramLong, String paramString)
    throws RemoteException;
  
  public abstract int postEnroll(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract long preEnroll(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void remove(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, IFingerprintServiceReceiver paramIFingerprintServiceReceiver)
    throws RemoteException;
  
  public abstract void rename(int paramInt1, int paramInt2, String paramString)
    throws RemoteException;
  
  public abstract void resetTimeout(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void setActiveUser(int paramInt)
    throws RemoteException;
  
  public abstract int updateStatus(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintService
  {
    private static final String DESCRIPTOR = "android.hardware.fingerprint.IFingerprintService";
    static final int TRANSACTION_addLockoutResetCallback = 14;
    static final int TRANSACTION_authenticate = 1;
    static final int TRANSACTION_cancelAuthentication = 2;
    static final int TRANSACTION_cancelEnrollment = 4;
    static final int TRANSACTION_enroll = 3;
    static final int TRANSACTION_forceStopAuthentication = 16;
    static final int TRANSACTION_getAuthenticatedPackage = 19;
    static final int TRANSACTION_getAuthenticatorId = 12;
    static final int TRANSACTION_getEnrolledFingerprints = 7;
    static final int TRANSACTION_getStatus = 18;
    static final int TRANSACTION_hasEnrolledFingerprints = 11;
    static final int TRANSACTION_isHardwareDetected = 8;
    static final int TRANSACTION_postEnroll = 10;
    static final int TRANSACTION_preEnroll = 9;
    static final int TRANSACTION_remove = 5;
    static final int TRANSACTION_rename = 6;
    static final int TRANSACTION_resetTimeout = 13;
    static final int TRANSACTION_setActiveUser = 15;
    static final int TRANSACTION_updateStatus = 17;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.fingerprint.IFingerprintService");
    }
    
    public static IFingerprintService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.fingerprint.IFingerprintService");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintService))) {
        return (IFingerprintService)localIInterface;
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
      long l;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.fingerprint.IFingerprintService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        authenticate(paramParcel1.readStrongBinder(), paramParcel1.readLong(), paramParcel1.readInt(), IFingerprintServiceReceiver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        cancelAuthentication(paramParcel1.readStrongBinder(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        enroll(paramParcel1.readStrongBinder(), paramParcel1.createByteArray(), paramParcel1.readInt(), IFingerprintServiceReceiver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        cancelEnrollment(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        remove(paramParcel1.readStrongBinder(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt(), IFingerprintServiceReceiver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        rename(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        paramParcel1 = getEnrolledFingerprints(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        bool = isHardwareDetected(paramParcel1.readLong(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        l = preEnroll(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        paramInt1 = postEnroll(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        bool = hasEnrolledFingerprints(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        l = getAuthenticatorId(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        resetTimeout(paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        addLockoutResetCallback(IFingerprintServiceLockoutResetCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        setActiveUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        forceStopAuthentication(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        paramInt1 = updateStatus(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
        paramInt1 = getStatus();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
      paramParcel1.enforceInterface("android.hardware.fingerprint.IFingerprintService");
      paramParcel1 = getAuthenticatedPackage();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements IFingerprintService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addLockoutResetCallback(IFingerprintServiceLockoutResetCallback paramIFingerprintServiceLockoutResetCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          if (paramIFingerprintServiceLockoutResetCallback != null) {
            localIBinder = paramIFingerprintServiceLockoutResetCallback.asBinder();
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void authenticate(IBinder paramIBinder, long paramLong, int paramInt1, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt2, String paramString)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeLong(paramLong);
          localParcel1.writeInt(paramInt1);
          paramIBinder = (IBinder)localObject;
          if (paramIFingerprintServiceReceiver != null) {
            paramIBinder = paramIFingerprintServiceReceiver.asBinder();
          }
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString);
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
      
      public void cancelAuthentication(IBinder paramIBinder, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString);
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
      
      public void cancelEnrollment(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void enroll(IBinder paramIBinder, byte[] paramArrayOfByte, int paramInt1, IFingerprintServiceReceiver paramIFingerprintServiceReceiver, int paramInt2, String paramString)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeByteArray(paramArrayOfByte);
          localParcel1.writeInt(paramInt1);
          paramIBinder = (IBinder)localObject;
          if (paramIFingerprintServiceReceiver != null) {
            paramIBinder = paramIFingerprintServiceReceiver.asBinder();
          }
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString);
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
      
      public void forceStopAuthentication(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
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
      
      public String getAuthenticatedPackage()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
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
      
      public long getAuthenticatorId(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      
      public List<Fingerprint> getEnrolledFingerprints(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createTypedArrayList(Fingerprint.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.fingerprint.IFingerprintService";
      }
      
      public int getStatus()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public boolean hasEnrolledFingerprints(int paramInt, String paramString)
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
        //   20: invokevirtual 68	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 74	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/fingerprint/IFingerprintService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 115	android/os/Parcel:readInt	()I
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
        //   80: astore_2
        //   81: aload 5
        //   83: invokevirtual 57	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 57	android/os/Parcel:recycle	()V
        //   91: aload_2
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramInt	int
        //   0	93	2	paramString	String
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      /* Error */
      public boolean isHardwareDetected(long paramLong, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: lload_1
        //   20: invokevirtual 64	android/os/Parcel:writeLong	(J)V
        //   23: aload 6
        //   25: aload_3
        //   26: invokevirtual 74	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	android/hardware/fingerprint/IFingerprintService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 8
        //   35: aload 6
        //   37: aload 7
        //   39: iconst_0
        //   40: invokeinterface 51 5 0
        //   45: pop
        //   46: aload 7
        //   48: invokevirtual 54	android/os/Parcel:readException	()V
        //   51: aload 7
        //   53: invokevirtual 115	android/os/Parcel:readInt	()I
        //   56: istore 4
        //   58: iload 4
        //   60: ifeq +19 -> 79
        //   63: iconst_1
        //   64: istore 5
        //   66: aload 7
        //   68: invokevirtual 57	android/os/Parcel:recycle	()V
        //   71: aload 6
        //   73: invokevirtual 57	android/os/Parcel:recycle	()V
        //   76: iload 5
        //   78: ireturn
        //   79: iconst_0
        //   80: istore 5
        //   82: goto -16 -> 66
        //   85: astore_3
        //   86: aload 7
        //   88: invokevirtual 57	android/os/Parcel:recycle	()V
        //   91: aload 6
        //   93: invokevirtual 57	android/os/Parcel:recycle	()V
        //   96: aload_3
        //   97: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	98	0	this	Proxy
        //   0	98	1	paramLong	long
        //   0	98	3	paramString	String
        //   56	3	4	i	int
        //   64	17	5	bool	boolean
        //   3	89	6	localParcel1	Parcel
        //   8	79	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	58	85	finally
      }
      
      public int postEnroll(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public long preEnroll(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void remove(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, IFingerprintServiceReceiver paramIFingerprintServiceReceiver)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          paramIBinder = (IBinder)localObject;
          if (paramIFingerprintServiceReceiver != null) {
            paramIBinder = paramIFingerprintServiceReceiver.asBinder();
          }
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void rename(int paramInt1, int paramInt2, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeString(paramString);
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
      
      public void resetTimeout(byte[] paramArrayOfByte)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeByteArray(paramArrayOfByte);
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
      
      public void setActiveUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
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
      
      public int updateStatus(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.fingerprint.IFingerprintService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/fingerprint/IFingerprintService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */