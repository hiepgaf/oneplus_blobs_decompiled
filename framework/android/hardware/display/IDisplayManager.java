package android.hardware.display;

import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjection.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.DisplayInfo;
import android.view.Surface;

public abstract interface IDisplayManager
  extends IInterface
{
  public abstract void connectWifiDisplay(String paramString)
    throws RemoteException;
  
  public abstract int createVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback, IMediaProjection paramIMediaProjection, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface, int paramInt4)
    throws RemoteException;
  
  public abstract void disconnectWifiDisplay()
    throws RemoteException;
  
  public abstract void forgetWifiDisplay(String paramString)
    throws RemoteException;
  
  public abstract int[] getDisplayIds()
    throws RemoteException;
  
  public abstract DisplayInfo getDisplayInfo(int paramInt)
    throws RemoteException;
  
  public abstract WifiDisplayStatus getWifiDisplayStatus()
    throws RemoteException;
  
  public abstract void pauseWifiDisplay()
    throws RemoteException;
  
  public abstract void registerCallback(IDisplayManagerCallback paramIDisplayManagerCallback)
    throws RemoteException;
  
  public abstract void releaseVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback)
    throws RemoteException;
  
  public abstract void renameWifiDisplay(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void requestColorMode(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void resizeVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void resumeWifiDisplay()
    throws RemoteException;
  
  public abstract void setVirtualDisplaySurface(IVirtualDisplayCallback paramIVirtualDisplayCallback, Surface paramSurface)
    throws RemoteException;
  
  public abstract void startWifiDisplayScan()
    throws RemoteException;
  
  public abstract void stopWifiDisplayScan()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IDisplayManager
  {
    private static final String DESCRIPTOR = "android.hardware.display.IDisplayManager";
    static final int TRANSACTION_connectWifiDisplay = 6;
    static final int TRANSACTION_createVirtualDisplay = 14;
    static final int TRANSACTION_disconnectWifiDisplay = 7;
    static final int TRANSACTION_forgetWifiDisplay = 9;
    static final int TRANSACTION_getDisplayIds = 2;
    static final int TRANSACTION_getDisplayInfo = 1;
    static final int TRANSACTION_getWifiDisplayStatus = 12;
    static final int TRANSACTION_pauseWifiDisplay = 10;
    static final int TRANSACTION_registerCallback = 3;
    static final int TRANSACTION_releaseVirtualDisplay = 17;
    static final int TRANSACTION_renameWifiDisplay = 8;
    static final int TRANSACTION_requestColorMode = 13;
    static final int TRANSACTION_resizeVirtualDisplay = 15;
    static final int TRANSACTION_resumeWifiDisplay = 11;
    static final int TRANSACTION_setVirtualDisplaySurface = 16;
    static final int TRANSACTION_startWifiDisplayScan = 4;
    static final int TRANSACTION_stopWifiDisplayScan = 5;
    
    public Stub()
    {
      attachInterface(this, "android.hardware.display.IDisplayManager");
    }
    
    public static IDisplayManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.hardware.display.IDisplayManager");
      if ((localIInterface != null) && ((localIInterface instanceof IDisplayManager))) {
        return (IDisplayManager)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.hardware.display.IDisplayManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        paramParcel1 = getDisplayInfo(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        paramParcel1 = getDisplayIds();
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        registerCallback(IDisplayManagerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        startWifiDisplayScan();
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        stopWifiDisplayScan();
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        connectWifiDisplay(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        disconnectWifiDisplay();
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        renameWifiDisplay(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        forgetWifiDisplay(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        pauseWifiDisplay();
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        resumeWifiDisplay();
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        paramParcel1 = getWifiDisplayStatus();
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
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        requestColorMode(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        IVirtualDisplayCallback localIVirtualDisplayCallback = IVirtualDisplayCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        IMediaProjection localIMediaProjection = IMediaProjection.Stub.asInterface(paramParcel1.readStrongBinder());
        String str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        int i = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (Surface)Surface.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          paramInt1 = createVirtualDisplay(localIVirtualDisplayCallback, localIMediaProjection, str1, str2, paramInt1, paramInt2, i, (Surface)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        resizeVirtualDisplay(IVirtualDisplayCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 16: 
        paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
        localObject = IVirtualDisplayCallback.Stub.asInterface(paramParcel1.readStrongBinder());
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Surface)Surface.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setVirtualDisplaySurface((IVirtualDisplayCallback)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.hardware.display.IDisplayManager");
      releaseVirtualDisplay(IVirtualDisplayCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IDisplayManager
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
      
      public void connectWifiDisplay(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
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
      
      /* Error */
      public int createVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback, IMediaProjection paramIMediaProjection, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface, int paramInt4)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 10
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 11
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 12
        //   13: aload 11
        //   15: ldc 34
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload_1
        //   21: ifnull +138 -> 159
        //   24: aload_1
        //   25: invokeinterface 59 1 0
        //   30: astore_1
        //   31: aload 11
        //   33: aload_1
        //   34: invokevirtual 62	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   37: aload 10
        //   39: astore_1
        //   40: aload_2
        //   41: ifnull +10 -> 51
        //   44: aload_2
        //   45: invokeinterface 65 1 0
        //   50: astore_1
        //   51: aload 11
        //   53: aload_1
        //   54: invokevirtual 62	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   57: aload 11
        //   59: aload_3
        //   60: invokevirtual 40	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   63: aload 11
        //   65: aload 4
        //   67: invokevirtual 40	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   70: aload 11
        //   72: iload 5
        //   74: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   77: aload 11
        //   79: iload 6
        //   81: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   84: aload 11
        //   86: iload 7
        //   88: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   91: aload 8
        //   93: ifnull +71 -> 164
        //   96: aload 11
        //   98: iconst_1
        //   99: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   102: aload 8
        //   104: aload 11
        //   106: iconst_0
        //   107: invokevirtual 75	android/view/Surface:writeToParcel	(Landroid/os/Parcel;I)V
        //   110: aload 11
        //   112: iload 9
        //   114: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   117: aload_0
        //   118: getfield 19	android/hardware/display/IDisplayManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   121: bipush 14
        //   123: aload 11
        //   125: aload 12
        //   127: iconst_0
        //   128: invokeinterface 46 5 0
        //   133: pop
        //   134: aload 12
        //   136: invokevirtual 49	android/os/Parcel:readException	()V
        //   139: aload 12
        //   141: invokevirtual 79	android/os/Parcel:readInt	()I
        //   144: istore 5
        //   146: aload 12
        //   148: invokevirtual 52	android/os/Parcel:recycle	()V
        //   151: aload 11
        //   153: invokevirtual 52	android/os/Parcel:recycle	()V
        //   156: iload 5
        //   158: ireturn
        //   159: aconst_null
        //   160: astore_1
        //   161: goto -130 -> 31
        //   164: aload 11
        //   166: iconst_0
        //   167: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   170: goto -60 -> 110
        //   173: astore_1
        //   174: aload 12
        //   176: invokevirtual 52	android/os/Parcel:recycle	()V
        //   179: aload 11
        //   181: invokevirtual 52	android/os/Parcel:recycle	()V
        //   184: aload_1
        //   185: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	186	0	this	Proxy
        //   0	186	1	paramIVirtualDisplayCallback	IVirtualDisplayCallback
        //   0	186	2	paramIMediaProjection	IMediaProjection
        //   0	186	3	paramString1	String
        //   0	186	4	paramString2	String
        //   0	186	5	paramInt1	int
        //   0	186	6	paramInt2	int
        //   0	186	7	paramInt3	int
        //   0	186	8	paramSurface	Surface
        //   0	186	9	paramInt4	int
        //   1	37	10	localObject	Object
        //   6	174	11	localParcel1	Parcel
        //   11	164	12	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	20	173	finally
        //   24	31	173	finally
        //   31	37	173	finally
        //   44	51	173	finally
        //   51	91	173	finally
        //   96	110	173	finally
        //   110	146	173	finally
        //   164	170	173	finally
      }
      
      public void disconnectWifiDisplay()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
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
      
      public void forgetWifiDisplay(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int[] getDisplayIds()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
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
      public DisplayInfo getDisplayInfo(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 34
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/hardware/display/IDisplayManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_1
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 46 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 49	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 79	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 94	android/view/DisplayInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 100 2 0
        //   58: checkcast 90	android/view/DisplayInfo
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 52	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 52	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 52	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 52	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt	int
        //   61	14	2	localDisplayInfo	DisplayInfo
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.hardware.display.IDisplayManager";
      }
      
      /* Error */
      public WifiDisplayStatus getWifiDisplayStatus()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_0
        //   15: getfield 19	android/hardware/display/IDisplayManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 12
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 46 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 49	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 79	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 107	android/hardware/display/WifiDisplayStatus:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 100 2 0
        //   49: checkcast 106	android/hardware/display/WifiDisplayStatus
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 52	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 52	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 52	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 52	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localWifiDisplayStatus	WifiDisplayStatus
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public void pauseWifiDisplay()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerCallback(IDisplayManagerCallback paramIDisplayManagerCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
          if (paramIDisplayManagerCallback != null) {
            localIBinder = paramIDisplayManagerCallback.asBinder();
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
      
      public void releaseVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
          if (paramIVirtualDisplayCallback != null) {
            localIBinder = paramIVirtualDisplayCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void renameWifiDisplay(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void requestColorMode(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      public void resizeVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
          if (paramIVirtualDisplayCallback != null) {
            localIBinder = paramIVirtualDisplayCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
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
      
      public void resumeWifiDisplay()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
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
      
      /* Error */
      public void setVirtualDisplaySurface(IVirtualDisplayCallback paramIVirtualDisplayCallback, Surface paramSurface)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 34
        //   16: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +10 -> 30
        //   23: aload_1
        //   24: invokeinterface 59 1 0
        //   29: astore_3
        //   30: aload 4
        //   32: aload_3
        //   33: invokevirtual 62	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   36: aload_2
        //   37: ifnull +49 -> 86
        //   40: aload 4
        //   42: iconst_1
        //   43: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   46: aload_2
        //   47: aload 4
        //   49: iconst_0
        //   50: invokevirtual 75	android/view/Surface:writeToParcel	(Landroid/os/Parcel;I)V
        //   53: aload_0
        //   54: getfield 19	android/hardware/display/IDisplayManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   57: bipush 16
        //   59: aload 4
        //   61: aload 5
        //   63: iconst_0
        //   64: invokeinterface 46 5 0
        //   69: pop
        //   70: aload 5
        //   72: invokevirtual 49	android/os/Parcel:readException	()V
        //   75: aload 5
        //   77: invokevirtual 52	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 52	android/os/Parcel:recycle	()V
        //   85: return
        //   86: aload 4
        //   88: iconst_0
        //   89: invokevirtual 69	android/os/Parcel:writeInt	(I)V
        //   92: goto -39 -> 53
        //   95: astore_1
        //   96: aload 5
        //   98: invokevirtual 52	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 52	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramIVirtualDisplayCallback	IVirtualDisplayCallback
        //   0	108	2	paramSurface	Surface
        //   1	32	3	localIBinder	IBinder
        //   5	97	4	localParcel1	Parcel
        //   10	87	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	95	finally
        //   23	30	95	finally
        //   30	36	95	finally
        //   40	53	95	finally
        //   53	75	95	finally
        //   86	92	95	finally
      }
      
      public void startWifiDisplayScan()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
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
      
      public void stopWifiDisplayScan()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.hardware.display.IDisplayManager");
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/IDisplayManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */