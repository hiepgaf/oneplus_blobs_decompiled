package android.service.gatekeeper;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGateKeeperService
  extends IInterface
{
  public abstract void clearSecureUserId(int paramInt)
    throws RemoteException;
  
  public abstract GateKeeperResponse enroll(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws RemoteException;
  
  public abstract long getSecureUserId(int paramInt)
    throws RemoteException;
  
  public abstract GateKeeperResponse verify(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException;
  
  public abstract GateKeeperResponse verifyChallenge(int paramInt, long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGateKeeperService
  {
    private static final String DESCRIPTOR = "android.service.gatekeeper.IGateKeeperService";
    static final int TRANSACTION_clearSecureUserId = 5;
    static final int TRANSACTION_enroll = 1;
    static final int TRANSACTION_getSecureUserId = 4;
    static final int TRANSACTION_verify = 2;
    static final int TRANSACTION_verifyChallenge = 3;
    
    public Stub()
    {
      attachInterface(this, "android.service.gatekeeper.IGateKeeperService");
    }
    
    public static IGateKeeperService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.gatekeeper.IGateKeeperService");
      if ((localIInterface != null) && ((localIInterface instanceof IGateKeeperService))) {
        return (IGateKeeperService)localIInterface;
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
        paramParcel2.writeString("android.service.gatekeeper.IGateKeeperService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.gatekeeper.IGateKeeperService");
        paramParcel1 = enroll(paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.createByteArray(), paramParcel1.createByteArray());
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
        paramParcel1.enforceInterface("android.service.gatekeeper.IGateKeeperService");
        paramParcel1 = verify(paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.createByteArray());
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
        paramParcel1.enforceInterface("android.service.gatekeeper.IGateKeeperService");
        paramParcel1 = verifyChallenge(paramParcel1.readInt(), paramParcel1.readLong(), paramParcel1.createByteArray(), paramParcel1.createByteArray());
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
        paramParcel1.enforceInterface("android.service.gatekeeper.IGateKeeperService");
        long l = getSecureUserId(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      }
      paramParcel1.enforceInterface("android.service.gatekeeper.IGateKeeperService");
      clearSecureUserId(paramParcel1.readInt());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IGateKeeperService
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
      
      public void clearSecureUserId(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
          localParcel1.writeInt(paramInt);
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
      
      /* Error */
      public GateKeeperResponse enroll(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: iload_1
        //   20: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 60	android/os/Parcel:writeByteArray	([B)V
        //   29: aload 5
        //   31: aload_3
        //   32: invokevirtual 60	android/os/Parcel:writeByteArray	([B)V
        //   35: aload 5
        //   37: aload 4
        //   39: invokevirtual 60	android/os/Parcel:writeByteArray	([B)V
        //   42: aload_0
        //   43: getfield 19	android/service/gatekeeper/IGateKeeperService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   46: iconst_1
        //   47: aload 5
        //   49: aload 6
        //   51: iconst_0
        //   52: invokeinterface 47 5 0
        //   57: pop
        //   58: aload 6
        //   60: invokevirtual 50	android/os/Parcel:readException	()V
        //   63: aload 6
        //   65: invokevirtual 64	android/os/Parcel:readInt	()I
        //   68: ifeq +29 -> 97
        //   71: getstatic 70	android/service/gatekeeper/GateKeeperResponse:CREATOR	Landroid/os/Parcelable$Creator;
        //   74: aload 6
        //   76: invokeinterface 76 2 0
        //   81: checkcast 66	android/service/gatekeeper/GateKeeperResponse
        //   84: astore_2
        //   85: aload 6
        //   87: invokevirtual 53	android/os/Parcel:recycle	()V
        //   90: aload 5
        //   92: invokevirtual 53	android/os/Parcel:recycle	()V
        //   95: aload_2
        //   96: areturn
        //   97: aconst_null
        //   98: astore_2
        //   99: goto -14 -> 85
        //   102: astore_2
        //   103: aload 6
        //   105: invokevirtual 53	android/os/Parcel:recycle	()V
        //   108: aload 5
        //   110: invokevirtual 53	android/os/Parcel:recycle	()V
        //   113: aload_2
        //   114: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	115	0	this	Proxy
        //   0	115	1	paramInt	int
        //   0	115	2	paramArrayOfByte1	byte[]
        //   0	115	3	paramArrayOfByte2	byte[]
        //   0	115	4	paramArrayOfByte3	byte[]
        //   3	106	5	localParcel1	Parcel
        //   8	96	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	85	102	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.service.gatekeeper.IGateKeeperService";
      }
      
      public long getSecureUserId(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
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
      public GateKeeperResponse verify(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 60	android/os/Parcel:writeByteArray	([B)V
        //   29: aload 4
        //   31: aload_3
        //   32: invokevirtual 60	android/os/Parcel:writeByteArray	([B)V
        //   35: aload_0
        //   36: getfield 19	android/service/gatekeeper/IGateKeeperService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_2
        //   40: aload 4
        //   42: aload 5
        //   44: iconst_0
        //   45: invokeinterface 47 5 0
        //   50: pop
        //   51: aload 5
        //   53: invokevirtual 50	android/os/Parcel:readException	()V
        //   56: aload 5
        //   58: invokevirtual 64	android/os/Parcel:readInt	()I
        //   61: ifeq +29 -> 90
        //   64: getstatic 70	android/service/gatekeeper/GateKeeperResponse:CREATOR	Landroid/os/Parcelable$Creator;
        //   67: aload 5
        //   69: invokeinterface 76 2 0
        //   74: checkcast 66	android/service/gatekeeper/GateKeeperResponse
        //   77: astore_2
        //   78: aload 5
        //   80: invokevirtual 53	android/os/Parcel:recycle	()V
        //   83: aload 4
        //   85: invokevirtual 53	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: areturn
        //   90: aconst_null
        //   91: astore_2
        //   92: goto -14 -> 78
        //   95: astore_2
        //   96: aload 5
        //   98: invokevirtual 53	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 53	android/os/Parcel:recycle	()V
        //   106: aload_2
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramInt	int
        //   0	108	2	paramArrayOfByte1	byte[]
        //   0	108	3	paramArrayOfByte2	byte[]
        //   3	99	4	localParcel1	Parcel
        //   8	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	78	95	finally
      }
      
      /* Error */
      public GateKeeperResponse verifyChallenge(int paramInt, long paramLong, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: iload_1
        //   20: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   23: aload 6
        //   25: lload_2
        //   26: invokevirtual 92	android/os/Parcel:writeLong	(J)V
        //   29: aload 6
        //   31: aload 4
        //   33: invokevirtual 60	android/os/Parcel:writeByteArray	([B)V
        //   36: aload 6
        //   38: aload 5
        //   40: invokevirtual 60	android/os/Parcel:writeByteArray	([B)V
        //   43: aload_0
        //   44: getfield 19	android/service/gatekeeper/IGateKeeperService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   47: iconst_3
        //   48: aload 6
        //   50: aload 7
        //   52: iconst_0
        //   53: invokeinterface 47 5 0
        //   58: pop
        //   59: aload 7
        //   61: invokevirtual 50	android/os/Parcel:readException	()V
        //   64: aload 7
        //   66: invokevirtual 64	android/os/Parcel:readInt	()I
        //   69: ifeq +31 -> 100
        //   72: getstatic 70	android/service/gatekeeper/GateKeeperResponse:CREATOR	Landroid/os/Parcelable$Creator;
        //   75: aload 7
        //   77: invokeinterface 76 2 0
        //   82: checkcast 66	android/service/gatekeeper/GateKeeperResponse
        //   85: astore 4
        //   87: aload 7
        //   89: invokevirtual 53	android/os/Parcel:recycle	()V
        //   92: aload 6
        //   94: invokevirtual 53	android/os/Parcel:recycle	()V
        //   97: aload 4
        //   99: areturn
        //   100: aconst_null
        //   101: astore 4
        //   103: goto -16 -> 87
        //   106: astore 4
        //   108: aload 7
        //   110: invokevirtual 53	android/os/Parcel:recycle	()V
        //   113: aload 6
        //   115: invokevirtual 53	android/os/Parcel:recycle	()V
        //   118: aload 4
        //   120: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	121	0	this	Proxy
        //   0	121	1	paramInt	int
        //   0	121	2	paramLong	long
        //   0	121	4	paramArrayOfByte1	byte[]
        //   0	121	5	paramArrayOfByte2	byte[]
        //   3	111	6	localParcel1	Parcel
        //   8	101	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	87	106	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/gatekeeper/IGateKeeperService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */