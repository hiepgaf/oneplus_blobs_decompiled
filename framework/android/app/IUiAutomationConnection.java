package android.app;

import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceClient.Stub;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.view.InputEvent;
import android.view.WindowAnimationFrameStats;
import android.view.WindowContentFrameStats;

public abstract interface IUiAutomationConnection
  extends IInterface
{
  public abstract void clearWindowAnimationFrameStats()
    throws RemoteException;
  
  public abstract boolean clearWindowContentFrameStats(int paramInt)
    throws RemoteException;
  
  public abstract void connect(IAccessibilityServiceClient paramIAccessibilityServiceClient, int paramInt)
    throws RemoteException;
  
  public abstract void disconnect()
    throws RemoteException;
  
  public abstract void executeShellCommand(String paramString, ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException;
  
  public abstract WindowAnimationFrameStats getWindowAnimationFrameStats()
    throws RemoteException;
  
  public abstract WindowContentFrameStats getWindowContentFrameStats(int paramInt)
    throws RemoteException;
  
  public abstract void grantRuntimePermission(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract boolean injectInputEvent(InputEvent paramInputEvent, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void revokeRuntimePermission(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract boolean setRotation(int paramInt)
    throws RemoteException;
  
  public abstract void shutdown()
    throws RemoteException;
  
  public abstract Bitmap takeScreenshot(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IUiAutomationConnection
  {
    private static final String DESCRIPTOR = "android.app.IUiAutomationConnection";
    static final int TRANSACTION_clearWindowAnimationFrameStats = 8;
    static final int TRANSACTION_clearWindowContentFrameStats = 6;
    static final int TRANSACTION_connect = 1;
    static final int TRANSACTION_disconnect = 2;
    static final int TRANSACTION_executeShellCommand = 10;
    static final int TRANSACTION_getWindowAnimationFrameStats = 9;
    static final int TRANSACTION_getWindowContentFrameStats = 7;
    static final int TRANSACTION_grantRuntimePermission = 11;
    static final int TRANSACTION_injectInputEvent = 3;
    static final int TRANSACTION_revokeRuntimePermission = 12;
    static final int TRANSACTION_setRotation = 4;
    static final int TRANSACTION_shutdown = 13;
    static final int TRANSACTION_takeScreenshot = 5;
    
    public Stub()
    {
      attachInterface(this, "android.app.IUiAutomationConnection");
    }
    
    public static IUiAutomationConnection asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IUiAutomationConnection");
      if ((localIInterface != null) && ((localIInterface instanceof IUiAutomationConnection))) {
        return (IUiAutomationConnection)localIInterface;
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
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.IUiAutomationConnection");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        connect(IAccessibilityServiceClient.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        disconnect();
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (InputEvent)InputEvent.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label256;
          }
          bool = true;
          bool = injectInputEvent((InputEvent)localObject, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label262;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject = null;
          break;
          bool = false;
          break label222;
        }
      case 4: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        bool = setRotation(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        paramParcel1 = takeScreenshot(paramParcel1.readInt(), paramParcel1.readInt());
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
      case 6: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        bool = clearWindowContentFrameStats(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        paramParcel1 = getWindowContentFrameStats(paramParcel1.readInt());
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
      case 8: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        clearWindowAnimationFrameStats();
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        paramParcel1 = getWindowAnimationFrameStats();
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
      case 10: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          executeShellCommand((String)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        grantRuntimePermission(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        label222:
        label256:
        label262:
        paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
        revokeRuntimePermission(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.app.IUiAutomationConnection");
      shutdown();
      return true;
    }
    
    private static class Proxy
      implements IUiAutomationConnection
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
      
      public void clearWindowAnimationFrameStats()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiAutomationConnection");
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
      
      /* Error */
      public boolean clearWindowContentFrameStats(int paramInt)
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
        //   21: getfield 19	android/app/IUiAutomationConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 43 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 46	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 60	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 49	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 49	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 49	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 49	android/os/Parcel:recycle	()V
        //   80: aload 5
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramInt	int
        //   52	14	2	bool	boolean
        //   3	74	3	localParcel1	Parcel
        //   7	65	4	localParcel2	Parcel
        //   69	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	47	69	finally
      }
      
      public void connect(IAccessibilityServiceClient paramIAccessibilityServiceClient, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiAutomationConnection");
          if (paramIAccessibilityServiceClient != null) {
            localIBinder = paramIAccessibilityServiceClient.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
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
      
      public void disconnect()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiAutomationConnection");
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
      
      /* Error */
      public void executeShellCommand(String paramString, ParcelFileDescriptor paramParcelFileDescriptor)
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
        //   16: aload_1
        //   17: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 81	android/os/ParcelFileDescriptor:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/app/IUiAutomationConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 10
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
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 49	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 49	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramParcelFileDescriptor	ParcelFileDescriptor
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IUiAutomationConnection";
      }
      
      /* Error */
      public WindowAnimationFrameStats getWindowAnimationFrameStats()
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
        //   15: getfield 19	android/app/IUiAutomationConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 9
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 43 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 46	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 60	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 91	android/view/WindowAnimationFrameStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 97 2 0
        //   49: checkcast 87	android/view/WindowAnimationFrameStats
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
        //   52	13	1	localWindowAnimationFrameStats	WindowAnimationFrameStats
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public WindowContentFrameStats getWindowContentFrameStats(int paramInt)
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
        //   21: getfield 19	android/app/IUiAutomationConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 7
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 43 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 46	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 60	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 102	android/view/WindowContentFrameStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 97 2 0
        //   59: checkcast 101	android/view/WindowContentFrameStats
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
        //   62	14	2	localWindowContentFrameStats	WindowContentFrameStats
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public void grantRuntimePermission(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiAutomationConnection");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
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
      
      public boolean injectInputEvent(InputEvent paramInputEvent, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.IUiAutomationConnection");
            if (paramInputEvent != null)
            {
              localParcel1.writeInt(1);
              paramInputEvent.writeToParcel(localParcel1, 0);
              break label122;
              localParcel1.writeInt(i);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
              i = localParcel2.readInt();
              if (i != 0)
              {
                paramBoolean = true;
                label78:
                return paramBoolean;
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
          label122:
          do
          {
            i = 0;
            break;
            paramBoolean = false;
            break label78;
          } while (!paramBoolean);
        }
      }
      
      public void revokeRuntimePermission(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.IUiAutomationConnection");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
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
      
      /* Error */
      public boolean setRotation(int paramInt)
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
        //   21: getfield 19	android/app/IUiAutomationConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_4
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 43 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 46	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 60	android/os/Parcel:readInt	()I
        //   45: istore_1
        //   46: iload_1
        //   47: ifeq +16 -> 63
        //   50: iconst_1
        //   51: istore_2
        //   52: aload 4
        //   54: invokevirtual 49	android/os/Parcel:recycle	()V
        //   57: aload_3
        //   58: invokevirtual 49	android/os/Parcel:recycle	()V
        //   61: iload_2
        //   62: ireturn
        //   63: iconst_0
        //   64: istore_2
        //   65: goto -13 -> 52
        //   68: astore 5
        //   70: aload 4
        //   72: invokevirtual 49	android/os/Parcel:recycle	()V
        //   75: aload_3
        //   76: invokevirtual 49	android/os/Parcel:recycle	()V
        //   79: aload 5
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	Proxy
        //   0	82	1	paramInt	int
        //   51	14	2	bool	boolean
        //   3	73	3	localParcel1	Parcel
        //   7	64	4	localParcel2	Parcel
        //   68	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   9	46	68	finally
      }
      
      public void shutdown()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IUiAutomationConnection");
          this.mRemote.transact(13, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public Bitmap takeScreenshot(int paramInt1, int paramInt2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 33
        //   14: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: iload_1
        //   20: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 56	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/app/IUiAutomationConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_5
        //   34: aload 4
        //   36: aload 5
        //   38: iconst_0
        //   39: invokeinterface 43 5 0
        //   44: pop
        //   45: aload 5
        //   47: invokevirtual 46	android/os/Parcel:readException	()V
        //   50: aload 5
        //   52: invokevirtual 60	android/os/Parcel:readInt	()I
        //   55: ifeq +29 -> 84
        //   58: getstatic 117	android/graphics/Bitmap:CREATOR	Landroid/os/Parcelable$Creator;
        //   61: aload 5
        //   63: invokeinterface 97 2 0
        //   68: checkcast 116	android/graphics/Bitmap
        //   71: astore_3
        //   72: aload 5
        //   74: invokevirtual 49	android/os/Parcel:recycle	()V
        //   77: aload 4
        //   79: invokevirtual 49	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: areturn
        //   84: aconst_null
        //   85: astore_3
        //   86: goto -14 -> 72
        //   89: astore_3
        //   90: aload 5
        //   92: invokevirtual 49	android/os/Parcel:recycle	()V
        //   95: aload 4
        //   97: invokevirtual 49	android/os/Parcel:recycle	()V
        //   100: aload_3
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramInt1	int
        //   0	102	2	paramInt2	int
        //   71	15	3	localBitmap	Bitmap
        //   89	12	3	localObject	Object
        //   3	93	4	localParcel1	Parcel
        //   8	83	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	72	89	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IUiAutomationConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */