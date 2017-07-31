package android.service.quicksettings;

import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IQSService
  extends IInterface
{
  public abstract Tile getTile(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean isLocked()
    throws RemoteException;
  
  public abstract boolean isSecure()
    throws RemoteException;
  
  public abstract void onDialogHidden(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void onShowDialog(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void onStartActivity(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void onStartSuccessful(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void startUnlockAndRun(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void updateQsTile(Tile paramTile, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void updateStatusIcon(IBinder paramIBinder, Icon paramIcon, String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IQSService
  {
    private static final String DESCRIPTOR = "android.service.quicksettings.IQSService";
    static final int TRANSACTION_getTile = 1;
    static final int TRANSACTION_isLocked = 6;
    static final int TRANSACTION_isSecure = 7;
    static final int TRANSACTION_onDialogHidden = 9;
    static final int TRANSACTION_onShowDialog = 4;
    static final int TRANSACTION_onStartActivity = 5;
    static final int TRANSACTION_onStartSuccessful = 10;
    static final int TRANSACTION_startUnlockAndRun = 8;
    static final int TRANSACTION_updateQsTile = 2;
    static final int TRANSACTION_updateStatusIcon = 3;
    
    public Stub()
    {
      attachInterface(this, "android.service.quicksettings.IQSService");
    }
    
    public static IQSService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.service.quicksettings.IQSService");
      if ((localIInterface != null) && ((localIInterface instanceof IQSService))) {
        return (IQSService)localIInterface;
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
      int j = 0;
      int i = 0;
      Object localObject;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.service.quicksettings.IQSService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        paramParcel1 = getTile(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (Tile)Tile.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          updateQsTile((Tile)localObject, paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        IBinder localIBinder = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (Icon)Icon.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          updateStatusIcon(localIBinder, (Icon)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        onShowDialog(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        onStartActivity(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        bool = isLocked();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        bool = isSecure();
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        startUnlockAndRun(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
        onDialogHidden(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.service.quicksettings.IQSService");
      onStartSuccessful(paramParcel1.readStrongBinder());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IQSService
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
        return "android.service.quicksettings.IQSService";
      }
      
      /* Error */
      public Tile getTile(IBinder paramIBinder)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   19: aload_0
        //   20: getfield 19	android/service/quicksettings/IQSService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_1
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 49 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 52	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 56	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 62	android/service/quicksettings/Tile:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 68 2 0
        //   53: checkcast 58	android/service/quicksettings/Tile
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 71	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 71	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 71	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 71	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramIBinder	IBinder
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	57	72	finally
      }
      
      /* Error */
      public boolean isLocked()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/service/quicksettings/IQSService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 6
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 56	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 71	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 71	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 71	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 71	android/os/Parcel:recycle	()V
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
      public boolean isSecure()
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_0
        //   16: getfield 19	android/service/quicksettings/IQSService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 56	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 71	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 71	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 71	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 71	android/os/Parcel:recycle	()V
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
      
      public void onDialogHidden(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.quicksettings.IQSService");
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void onShowDialog(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.quicksettings.IQSService");
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
      
      public void onStartActivity(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.quicksettings.IQSService");
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
      
      public void onStartSuccessful(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.quicksettings.IQSService");
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void startUnlockAndRun(IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.service.quicksettings.IQSService");
          localParcel1.writeStrongBinder(paramIBinder);
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
      public void updateQsTile(Tile paramTile, IBinder paramIBinder)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 26
        //   12: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +49 -> 65
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 86	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 90	android/service/quicksettings/Tile:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   35: aload_0
        //   36: getfield 19	android/service/quicksettings/IQSService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_2
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 49 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 52	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 71	android/os/Parcel:recycle	()V
        //   60: aload_3
        //   61: invokevirtual 71	android/os/Parcel:recycle	()V
        //   64: return
        //   65: aload_3
        //   66: iconst_0
        //   67: invokevirtual 86	android/os/Parcel:writeInt	(I)V
        //   70: goto -40 -> 30
        //   73: astore_1
        //   74: aload 4
        //   76: invokevirtual 71	android/os/Parcel:recycle	()V
        //   79: aload_3
        //   80: invokevirtual 71	android/os/Parcel:recycle	()V
        //   83: aload_1
        //   84: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	85	0	this	Proxy
        //   0	85	1	paramTile	Tile
        //   0	85	2	paramIBinder	IBinder
        //   3	77	3	localParcel1	Parcel
        //   7	68	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	73	finally
        //   19	30	73	finally
        //   30	55	73	finally
        //   65	70	73	finally
      }
      
      /* Error */
      public void updateStatusIcon(IBinder paramIBinder, Icon paramIcon, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_2
        //   24: ifnull +54 -> 78
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 86	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 95	android/graphics/drawable/Icon:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 98	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 19	android/service/quicksettings/IQSService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_3
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 49 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 52	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 71	android/os/Parcel:recycle	()V
        //   72: aload 4
        //   74: invokevirtual 71	android/os/Parcel:recycle	()V
        //   77: return
        //   78: aload 4
        //   80: iconst_0
        //   81: invokevirtual 86	android/os/Parcel:writeInt	(I)V
        //   84: goto -44 -> 40
        //   87: astore_1
        //   88: aload 5
        //   90: invokevirtual 71	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: invokevirtual 71	android/os/Parcel:recycle	()V
        //   98: aload_1
        //   99: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	100	0	this	Proxy
        //   0	100	1	paramIBinder	IBinder
        //   0	100	2	paramIcon	Icon
        //   0	100	3	paramString	String
        //   3	91	4	localParcel1	Parcel
        //   8	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	87	finally
        //   27	40	87	finally
        //   40	67	87	finally
        //   78	84	87	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/quicksettings/IQSService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */