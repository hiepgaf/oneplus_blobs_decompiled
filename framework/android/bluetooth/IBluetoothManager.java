package android.bluetooth;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IBluetoothManager
  extends IInterface
{
  public abstract boolean bindBluetoothProfileService(int paramInt, IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
    throws RemoteException;
  
  public abstract boolean disable(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean enable()
    throws RemoteException;
  
  public abstract boolean enableNoAutoConnect()
    throws RemoteException;
  
  public abstract String getAddress()
    throws RemoteException;
  
  public abstract IBluetoothGatt getBluetoothGatt()
    throws RemoteException;
  
  public abstract String getName()
    throws RemoteException;
  
  public abstract int getState()
    throws RemoteException;
  
  public abstract boolean isBleAppPresent()
    throws RemoteException;
  
  public abstract boolean isBleScanAlwaysAvailable()
    throws RemoteException;
  
  public abstract boolean isEnabled()
    throws RemoteException;
  
  public abstract IBluetooth registerAdapter(IBluetoothManagerCallback paramIBluetoothManagerCallback)
    throws RemoteException;
  
  public abstract void registerStateChangeCallback(IBluetoothStateChangeCallback paramIBluetoothStateChangeCallback)
    throws RemoteException;
  
  public abstract void unbindBluetoothProfileService(int paramInt, IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
    throws RemoteException;
  
  public abstract void unregisterAdapter(IBluetoothManagerCallback paramIBluetoothManagerCallback)
    throws RemoteException;
  
  public abstract void unregisterStateChangeCallback(IBluetoothStateChangeCallback paramIBluetoothStateChangeCallback)
    throws RemoteException;
  
  public abstract int updateBleAppCount(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBluetoothManager
  {
    private static final String DESCRIPTOR = "android.bluetooth.IBluetoothManager";
    static final int TRANSACTION_bindBluetoothProfileService = 11;
    static final int TRANSACTION_disable = 8;
    static final int TRANSACTION_enable = 6;
    static final int TRANSACTION_enableNoAutoConnect = 7;
    static final int TRANSACTION_getAddress = 13;
    static final int TRANSACTION_getBluetoothGatt = 10;
    static final int TRANSACTION_getName = 14;
    static final int TRANSACTION_getState = 9;
    static final int TRANSACTION_isBleAppPresent = 17;
    static final int TRANSACTION_isBleScanAlwaysAvailable = 15;
    static final int TRANSACTION_isEnabled = 5;
    static final int TRANSACTION_registerAdapter = 1;
    static final int TRANSACTION_registerStateChangeCallback = 3;
    static final int TRANSACTION_unbindBluetoothProfileService = 12;
    static final int TRANSACTION_unregisterAdapter = 2;
    static final int TRANSACTION_unregisterStateChangeCallback = 4;
    static final int TRANSACTION_updateBleAppCount = 16;
    
    public Stub()
    {
      attachInterface(this, "android.bluetooth.IBluetoothManager");
    }
    
    public static IBluetoothManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.bluetooth.IBluetoothManager");
      if ((localIInterface != null) && ((localIInterface instanceof IBluetoothManager))) {
        return (IBluetoothManager)localIInterface;
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
        paramParcel2.writeString("android.bluetooth.IBluetoothManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        paramParcel1 = registerAdapter(IBluetoothManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        unregisterAdapter(IBluetoothManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        registerStateChangeCallback(IBluetoothStateChangeCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        unregisterStateChangeCallback(IBluetoothStateChangeCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        bool = isEnabled();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        bool = enable();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        bool = enableNoAutoConnect();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = disable(bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label441;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        paramInt1 = getState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        paramParcel1 = getBluetoothGatt();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        bool = bindBluetoothProfileService(paramParcel1.readInt(), IBluetoothProfileServiceConnection.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        unbindBluetoothProfileService(paramParcel1.readInt(), IBluetoothProfileServiceConnection.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        paramParcel1 = getAddress();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        paramParcel1 = getName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        bool = isBleScanAlwaysAvailable();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 16: 
        label441:
        paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramInt1 = updateBleAppCount(localIBinder, bool);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.bluetooth.IBluetoothManager");
      boolean bool = isBleAppPresent();
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IBluetoothManager
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
      
      /* Error */
      public boolean bindBluetoothProfileService(int paramInt, IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 34
        //   17: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: iload_1
        //   23: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   26: aload_2
        //   27: ifnull +11 -> 38
        //   30: aload_2
        //   31: invokeinterface 46 1 0
        //   36: astore 4
        //   38: aload 5
        //   40: aload 4
        //   42: invokevirtual 49	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   45: aload_0
        //   46: getfield 19	android/bluetooth/IBluetoothManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   49: bipush 11
        //   51: aload 5
        //   53: aload 6
        //   55: iconst_0
        //   56: invokeinterface 55 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 58	android/os/Parcel:readException	()V
        //   67: aload 6
        //   69: invokevirtual 62	android/os/Parcel:readInt	()I
        //   72: istore_1
        //   73: iload_1
        //   74: ifeq +17 -> 91
        //   77: iconst_1
        //   78: istore_3
        //   79: aload 6
        //   81: invokevirtual 65	android/os/Parcel:recycle	()V
        //   84: aload 5
        //   86: invokevirtual 65	android/os/Parcel:recycle	()V
        //   89: iload_3
        //   90: ireturn
        //   91: iconst_0
        //   92: istore_3
        //   93: goto -14 -> 79
        //   96: astore_2
        //   97: aload 6
        //   99: invokevirtual 65	android/os/Parcel:recycle	()V
        //   102: aload 5
        //   104: invokevirtual 65	android/os/Parcel:recycle	()V
        //   107: aload_2
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramInt	int
        //   0	109	2	paramIBluetoothProfileServiceConnection	IBluetoothProfileServiceConnection
        //   78	15	3	bool	boolean
        //   1	40	4	localIBinder	IBinder
        //   6	97	5	localParcel1	Parcel
        //   11	87	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	96	finally
        //   30	38	96	finally
        //   38	73	96	finally
      }
      
      /* Error */
      public boolean disable(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 34
        //   14: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +5 -> 23
        //   21: iconst_1
        //   22: istore_2
        //   23: aload_3
        //   24: iload_2
        //   25: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/bluetooth/IBluetoothManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 8
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 55 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 58	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 62	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 65	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 65	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 65	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 65	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramBoolean	boolean
        //   1	55	2	i	int
        //   5	80	3	localParcel1	Parcel
        //   9	71	4	localParcel2	Parcel
        //   77	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	77	finally
        //   23	55	77	finally
      }
      
      /* Error */
      public boolean enable()
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
        //   16: getfield 19	android/bluetooth/IBluetoothManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 6
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 62	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 65	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 65	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 65	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 65	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean enableNoAutoConnect()
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
        //   16: getfield 19	android/bluetooth/IBluetoothManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 62	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 65	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 65	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 65	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 65	android/os/Parcel:recycle	()V
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
      
      public String getAddress()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
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
      
      public IBluetoothGatt getBluetoothGatt()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IBluetoothGatt localIBluetoothGatt = IBluetoothGatt.Stub.asInterface(localParcel2.readStrongBinder());
          return localIBluetoothGatt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.bluetooth.IBluetoothManager";
      }
      
      public String getName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
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
      
      public int getState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      public boolean isBleAppPresent()
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
        //   16: getfield 19	android/bluetooth/IBluetoothManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 17
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 62	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 65	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 65	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 65	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 65	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean isBleScanAlwaysAvailable()
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
        //   16: getfield 19	android/bluetooth/IBluetoothManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 15
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 55 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 58	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 62	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 65	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 65	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 65	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 65	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public boolean isEnabled()
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
        //   16: getfield 19	android/bluetooth/IBluetoothManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_5
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 55 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 58	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 62	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 65	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 65	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 65	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 65	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   40	2	1	i	int
        //   46	14	2	bool	boolean
        //   3	68	3	localParcel1	Parcel
        //   7	59	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	41	63	finally
      }
      
      public IBluetooth registerAdapter(IBluetoothManagerCallback paramIBluetoothManagerCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          if (paramIBluetoothManagerCallback != null) {
            localIBinder = paramIBluetoothManagerCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramIBluetoothManagerCallback = IBluetooth.Stub.asInterface(localParcel2.readStrongBinder());
          return paramIBluetoothManagerCallback;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerStateChangeCallback(IBluetoothStateChangeCallback paramIBluetoothStateChangeCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          if (paramIBluetoothStateChangeCallback != null) {
            localIBinder = paramIBluetoothStateChangeCallback.asBinder();
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
      
      public void unbindBluetoothProfileService(int paramInt, IBluetoothProfileServiceConnection paramIBluetoothProfileServiceConnection)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          localParcel1.writeInt(paramInt);
          if (paramIBluetoothProfileServiceConnection != null) {
            localIBinder = paramIBluetoothProfileServiceConnection.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void unregisterAdapter(IBluetoothManagerCallback paramIBluetoothManagerCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          if (paramIBluetoothManagerCallback != null) {
            localIBinder = paramIBluetoothManagerCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void unregisterStateChangeCallback(IBluetoothStateChangeCallback paramIBluetoothStateChangeCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          if (paramIBluetoothStateChangeCallback != null) {
            localIBinder = paramIBluetoothStateChangeCallback.asBinder();
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
      
      public int updateBleAppCount(IBinder paramIBinder, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.bluetooth.IBluetoothManager");
          localParcel1.writeStrongBinder(paramIBinder);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          i = localParcel2.readInt();
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/IBluetoothManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */