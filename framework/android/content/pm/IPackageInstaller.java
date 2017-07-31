package android.content.pm;

import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPackageInstaller
  extends IInterface
{
  public abstract void abandonSession(int paramInt)
    throws RemoteException;
  
  public abstract int createSession(PackageInstaller.SessionParams paramSessionParams, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getAllSessions(int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice getMySessions(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract PackageInstaller.SessionInfo getSessionInfo(int paramInt)
    throws RemoteException;
  
  public abstract IPackageInstallerSession openSession(int paramInt)
    throws RemoteException;
  
  public abstract void registerCallback(IPackageInstallerCallback paramIPackageInstallerCallback, int paramInt)
    throws RemoteException;
  
  public abstract void setPermissionsResult(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void uninstall(String paramString1, String paramString2, int paramInt1, IntentSender paramIntentSender, int paramInt2)
    throws RemoteException;
  
  public abstract void unregisterCallback(IPackageInstallerCallback paramIPackageInstallerCallback)
    throws RemoteException;
  
  public abstract void updateSessionAppIcon(int paramInt, Bitmap paramBitmap)
    throws RemoteException;
  
  public abstract void updateSessionAppLabel(int paramInt, String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPackageInstaller
  {
    private static final String DESCRIPTOR = "android.content.pm.IPackageInstaller";
    static final int TRANSACTION_abandonSession = 4;
    static final int TRANSACTION_createSession = 1;
    static final int TRANSACTION_getAllSessions = 7;
    static final int TRANSACTION_getMySessions = 8;
    static final int TRANSACTION_getSessionInfo = 6;
    static final int TRANSACTION_openSession = 5;
    static final int TRANSACTION_registerCallback = 9;
    static final int TRANSACTION_setPermissionsResult = 12;
    static final int TRANSACTION_uninstall = 11;
    static final int TRANSACTION_unregisterCallback = 10;
    static final int TRANSACTION_updateSessionAppIcon = 2;
    static final int TRANSACTION_updateSessionAppLabel = 3;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IPackageInstaller");
    }
    
    public static IPackageInstaller asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IPackageInstaller");
      if ((localIInterface != null) && ((localIInterface instanceof IPackageInstaller))) {
        return (IPackageInstaller)localIInterface;
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
        paramParcel2.writeString("android.content.pm.IPackageInstaller");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (PackageInstaller.SessionParams)PackageInstaller.SessionParams.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          paramInt1 = createSession((PackageInstaller.SessionParams)localObject, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          updateSessionAppIcon(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        updateSessionAppLabel(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        abandonSession(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        paramParcel1 = openSession(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        paramParcel1 = getSessionInfo(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        paramParcel1 = getAllSessions(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        paramParcel1 = getMySessions(paramParcel1.readString(), paramParcel1.readInt());
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
      case 9: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        registerCallback(IPackageInstallerCallback.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        unregisterCallback(IPackageInstallerCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
        String str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (IntentSender)IntentSender.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          uninstall(str1, str2, paramInt1, (IntentSender)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.content.pm.IPackageInstaller");
      paramInt1 = paramParcel1.readInt();
      if (paramParcel1.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        setPermissionsResult(paramInt1, bool);
        paramParcel2.writeNoException();
        return true;
      }
    }
    
    private static class Proxy
      implements IPackageInstaller
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void abandonSession(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstaller");
          localParcel1.writeInt(paramInt);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public int createSession(PackageInstaller.SessionParams paramSessionParams, String paramString, int paramInt)
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
        //   17: aload_1
        //   18: ifnull +67 -> 85
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 62	android/content/pm/PackageInstaller$SessionParams:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 65	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/content/pm/IPackageInstaller$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_1
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 45 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 48	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 69	android/os/Parcel:readInt	()I
        //   72: istore_3
        //   73: aload 5
        //   75: invokevirtual 51	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 51	android/os/Parcel:recycle	()V
        //   83: iload_3
        //   84: ireturn
        //   85: aload 4
        //   87: iconst_0
        //   88: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   91: goto -57 -> 34
        //   94: astore_1
        //   95: aload 5
        //   97: invokevirtual 51	android/os/Parcel:recycle	()V
        //   100: aload 4
        //   102: invokevirtual 51	android/os/Parcel:recycle	()V
        //   105: aload_1
        //   106: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	107	0	this	Proxy
        //   0	107	1	paramSessionParams	PackageInstaller.SessionParams
        //   0	107	2	paramString	String
        //   0	107	3	paramInt	int
        //   3	98	4	localParcel1	Parcel
        //   8	88	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	94	finally
        //   21	34	94	finally
        //   34	73	94	finally
        //   85	91	94	finally
      }
      
      /* Error */
      public ParceledListSlice getAllSessions(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/content/pm/IPackageInstaller$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 7
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 45 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 48	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 69	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 77	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   52: aload 4
        //   54: invokeinterface 83 2 0
        //   59: checkcast 73	android/content/pm/ParceledListSlice
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 51	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 51	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 51	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 51	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localParceledListSlice	ParceledListSlice
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.pm.IPackageInstaller";
      }
      
      /* Error */
      public ParceledListSlice getMySessions(String paramString, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 65	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_3
        //   21: iload_2
        //   22: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   25: aload_0
        //   26: getfield 19	android/content/pm/IPackageInstaller$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 8
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 45 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 48	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 69	android/os/Parcel:readInt	()I
        //   51: ifeq +28 -> 79
        //   54: getstatic 77	android/content/pm/ParceledListSlice:CREATOR	Landroid/os/Parcelable$ClassLoaderCreator;
        //   57: aload 4
        //   59: invokeinterface 83 2 0
        //   64: checkcast 73	android/content/pm/ParceledListSlice
        //   67: astore_1
        //   68: aload 4
        //   70: invokevirtual 51	android/os/Parcel:recycle	()V
        //   73: aload_3
        //   74: invokevirtual 51	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: areturn
        //   79: aconst_null
        //   80: astore_1
        //   81: goto -13 -> 68
        //   84: astore_1
        //   85: aload 4
        //   87: invokevirtual 51	android/os/Parcel:recycle	()V
        //   90: aload_3
        //   91: invokevirtual 51	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString	String
        //   0	96	2	paramInt	int
        //   3	88	3	localParcel1	Parcel
        //   7	79	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	68	84	finally
      }
      
      /* Error */
      public PackageInstaller.SessionInfo getSessionInfo(int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/content/pm/IPackageInstaller$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 6
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 45 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 48	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 69	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 94	android/content/pm/PackageInstaller$SessionInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 97 2 0
        //   59: checkcast 91	android/content/pm/PackageInstaller$SessionInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 51	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 51	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 51	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 51	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localSessionInfo	PackageInstaller.SessionInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public IPackageInstallerSession openSession(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstaller");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IPackageInstallerSession localIPackageInstallerSession = IPackageInstallerSession.Stub.asInterface(localParcel2.readStrongBinder());
          return localIPackageInstallerSession;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerCallback(IPackageInstallerCallback paramIPackageInstallerCallback, int paramInt)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstaller");
          if (paramIPackageInstallerCallback != null) {
            localIBinder = paramIPackageInstallerCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          localParcel1.writeInt(paramInt);
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
      
      public void setPermissionsResult(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstaller");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
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
      public void uninstall(String paramString1, String paramString2, int paramInt1, IntentSender paramIntentSender, int paramInt2)
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
        //   19: aload_1
        //   20: invokevirtual 65	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 6
        //   25: aload_2
        //   26: invokevirtual 65	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 6
        //   31: iload_3
        //   32: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   35: aload 4
        //   37: ifnull +57 -> 94
        //   40: aload 6
        //   42: iconst_1
        //   43: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   46: aload 4
        //   48: aload 6
        //   50: iconst_0
        //   51: invokevirtual 124	android/content/IntentSender:writeToParcel	(Landroid/os/Parcel;I)V
        //   54: aload 6
        //   56: iload 5
        //   58: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   61: aload_0
        //   62: getfield 19	android/content/pm/IPackageInstaller$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   65: bipush 11
        //   67: aload 6
        //   69: aload 7
        //   71: iconst_0
        //   72: invokeinterface 45 5 0
        //   77: pop
        //   78: aload 7
        //   80: invokevirtual 48	android/os/Parcel:readException	()V
        //   83: aload 7
        //   85: invokevirtual 51	android/os/Parcel:recycle	()V
        //   88: aload 6
        //   90: invokevirtual 51	android/os/Parcel:recycle	()V
        //   93: return
        //   94: aload 6
        //   96: iconst_0
        //   97: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   100: goto -46 -> 54
        //   103: astore_1
        //   104: aload 7
        //   106: invokevirtual 51	android/os/Parcel:recycle	()V
        //   109: aload 6
        //   111: invokevirtual 51	android/os/Parcel:recycle	()V
        //   114: aload_1
        //   115: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	116	0	this	Proxy
        //   0	116	1	paramString1	String
        //   0	116	2	paramString2	String
        //   0	116	3	paramInt1	int
        //   0	116	4	paramIntentSender	IntentSender
        //   0	116	5	paramInt2	int
        //   3	107	6	localParcel1	Parcel
        //   8	97	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	35	103	finally
        //   40	54	103	finally
        //   54	83	103	finally
        //   94	100	103	finally
      }
      
      public void unregisterCallback(IPackageInstallerCallback paramIPackageInstallerCallback)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstaller");
          if (paramIPackageInstallerCallback != null) {
            localIBinder = paramIPackageInstallerCallback.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      /* Error */
      public void updateSessionAppIcon(int paramInt, Bitmap paramBitmap)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: iload_1
        //   17: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +44 -> 65
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 131	android/graphics/Bitmap:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/content/pm/IPackageInstaller$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_2
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 45 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 48	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 51	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 51	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 39	android/os/Parcel:writeInt	(I)V
        //   70: goto -35 -> 35
        //   73: astore_2
        //   74: aload 4
        //   76: invokevirtual 51	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 51	android/os/Parcel:recycle	()V
        //   83: aload_2
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramInt	int
        //   0	85	2	paramBitmap	Bitmap
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	73	finally
        //   24	35	73	finally
        //   35	55	73	finally
        //   65	70	73	finally
      }
      
      public void updateSessionAppLabel(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstaller");
          localParcel1.writeInt(paramInt);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IPackageInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */