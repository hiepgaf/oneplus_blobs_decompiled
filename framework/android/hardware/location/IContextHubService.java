package android.hardware.location;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IContextHubService
  extends IInterface
{
  public abstract int[] findNanoAppOnHub(int paramInt, NanoAppFilter paramNanoAppFilter)
    throws RemoteException;
  
  public abstract int[] getContextHubHandles()
    throws RemoteException;
  
  public abstract ContextHubInfo getContextHubInfo(int paramInt)
    throws RemoteException;
  
  public abstract NanoAppInstanceInfo getNanoAppInstanceInfo(int paramInt)
    throws RemoteException;
  
  public abstract int loadNanoApp(int paramInt, NanoApp paramNanoApp)
    throws RemoteException;
  
  public abstract int registerCallback(IContextHubCallback paramIContextHubCallback)
    throws RemoteException;
  
  public abstract int sendMessage(int paramInt1, int paramInt2, ContextHubMessage paramContextHubMessage)
    throws RemoteException;
  
  public abstract int unloadNanoApp(int paramInt)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IContextHubService
  {
    private static final String DESCRIPTOR = "android.hardware.location.IContextHubService";
    static final int TRANSACTION_findNanoAppOnHub = 7;
    static final int TRANSACTION_getContextHubHandles = 2;
    static final int TRANSACTION_getContextHubInfo = 3;
    static final int TRANSACTION_getNanoAppInstanceInfo = 6;
    static final int TRANSACTION_loadNanoApp = 4;
    static final int TRANSACTION_registerCallback = 1;
    static final int TRANSACTION_sendMessage = 8;
    static final int TRANSACTION_unloadNanoApp = 5;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.location.IContextHubService");
    }
    
    public static IContextHubService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.location.IContextHubService");
      if ((localIInterface != null) && ((localIInterface instanceof IContextHubService))) {
        return (IContextHubService)localIInterface;
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
        paramParcel2.writeString("android.hardware.location.IContextHubService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
        paramInt1 = registerCallback(IContextHubCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
        paramParcel1 = getContextHubHandles();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
        paramParcel1 = getContextHubInfo(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NanoApp)NanoApp.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = loadNanoApp(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
        paramInt1 = unloadNanoApp(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
        paramParcel1 = getNanoAppInstanceInfo(paramParcel1.readInt());
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
      case 7: 
        paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NanoAppFilter)NanoAppFilter.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = findNanoAppOnHub(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeIntArray(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.hardware.location.IContextHubService");
      paramInt1 = paramParcel1.readInt();
      paramInt2 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (ContextHubMessage)ContextHubMessage.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        paramInt1 = sendMessage(paramInt1, paramInt2, paramParcel1);
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IContextHubService
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
      public int[] findNanoAppOnHub(int paramInt, NanoAppFilter paramNanoAppFilter)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +52 -> 73
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 48	android/hardware/location/NanoAppFilter:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/location/IContextHubService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 7
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 54 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 57	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 61	android/os/Parcel:createIntArray	()[I
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 64	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aload_3
        //   74: iconst_0
        //   75: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   78: goto -43 -> 35
        //   81: astore_2
        //   82: aload 4
        //   84: invokevirtual 64	android/os/Parcel:recycle	()V
        //   87: aload_3
        //   88: invokevirtual 64	android/os/Parcel:recycle	()V
        //   91: aload_2
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramInt	int
        //   0	93	2	paramNanoAppFilter	NanoAppFilter
        //   3	85	3	localParcel1	Parcel
        //   7	76	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	81	finally
        //   24	35	81	finally
        //   35	62	81	finally
        //   73	78	81	finally
      }
      
      public int[] getContextHubHandles()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IContextHubService");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int[] arrayOfInt = localParcel2.createIntArray();
          return arrayOfInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ContextHubInfo getContextHubInfo(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/hardware/location/IContextHubService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_3
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 54 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 57	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 72	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 78	android/hardware/location/ContextHubInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 84 2 0
        //   58: checkcast 74	android/hardware/location/ContextHubInfo
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 64	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 64	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 64	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt	int
        //   61	14	2	localContextHubInfo	ContextHubInfo
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.location.IContextHubService";
      }
      
      /* Error */
      public NanoAppInstanceInfo getNanoAppInstanceInfo(int paramInt)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/hardware/location/IContextHubService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 54 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 57	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 72	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 91	android/hardware/location/NanoAppInstanceInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 84 2 0
        //   59: checkcast 90	android/hardware/location/NanoAppInstanceInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 64	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 64	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 64	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localNanoAppInstanceInfo	NanoAppInstanceInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public int loadNanoApp(int paramInt, NanoApp paramNanoApp)
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
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +51 -> 72
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 96	android/hardware/location/NanoApp:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/hardware/location/IContextHubService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_4
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 54 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 57	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 72	android/os/Parcel:readInt	()I
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: aload_3
        //   73: iconst_0
        //   74: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   77: goto -42 -> 35
        //   80: astore_2
        //   81: aload 4
        //   83: invokevirtual 64	android/os/Parcel:recycle	()V
        //   86: aload_3
        //   87: invokevirtual 64	android/os/Parcel:recycle	()V
        //   90: aload_2
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramInt	int
        //   0	92	2	paramNanoApp	NanoApp
        //   3	84	3	localParcel1	Parcel
        //   7	75	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	80	finally
        //   24	35	80	finally
        //   35	61	80	finally
        //   72	77	80	finally
      }
      
      public int registerCallback(IContextHubCallback paramIContextHubCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IContextHubService");
          if (paramIContextHubCallback != null) {
            localIBinder = paramIContextHubCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      /* Error */
      public int sendMessage(int paramInt1, int paramInt2, ContextHubMessage paramContextHubMessage)
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
        //   20: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   29: aload_3
        //   30: ifnull +56 -> 86
        //   33: aload 4
        //   35: iconst_1
        //   36: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 4
        //   42: iconst_0
        //   43: invokevirtual 110	android/hardware/location/ContextHubMessage:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 19	android/hardware/location/IContextHubService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 8
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 54 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 57	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 72	android/os/Parcel:readInt	()I
        //   73: istore_1
        //   74: aload 5
        //   76: invokevirtual 64	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 64	android/os/Parcel:recycle	()V
        //   84: iload_1
        //   85: ireturn
        //   86: aload 4
        //   88: iconst_0
        //   89: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   92: goto -46 -> 46
        //   95: astore_3
        //   96: aload 5
        //   98: invokevirtual 64	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 64	android/os/Parcel:recycle	()V
        //   106: aload_3
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramInt1	int
        //   0	108	2	paramInt2	int
        //   0	108	3	paramContextHubMessage	ContextHubMessage
        //   3	99	4	localParcel1	Parcel
        //   8	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	95	finally
        //   33	46	95	finally
        //   46	74	95	finally
        //   86	92	95	finally
      }
      
      public int unloadNanoApp(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.location.IContextHubService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/IContextHubService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */