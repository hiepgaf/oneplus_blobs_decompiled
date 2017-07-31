package com.fingerprints.extension.navigation;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IFingerprintNavigation
  extends IInterface
{
  public abstract NavigationConfig getNavigationConfig()
    throws RemoteException;
  
  public abstract boolean isEnabled()
    throws RemoteException;
  
  public abstract void setNavigation(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setNavigationConfig(NavigationConfig paramNavigationConfig)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IFingerprintNavigation
  {
    private static final String DESCRIPTOR = "com.fingerprints.extension.navigation.IFingerprintNavigation";
    static final int TRANSACTION_getNavigationConfig = 2;
    static final int TRANSACTION_isEnabled = 4;
    static final int TRANSACTION_setNavigation = 1;
    static final int TRANSACTION_setNavigationConfig = 3;
    
    public Stub()
    {
      attachInterface(this, "com.fingerprints.extension.navigation.IFingerprintNavigation");
    }
    
    public static IFingerprintNavigation asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.fingerprints.extension.navigation.IFingerprintNavigation");
      if ((localIInterface != null) && ((localIInterface instanceof IFingerprintNavigation))) {
        return (IFingerprintNavigation)localIInterface;
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
        paramParcel2.writeString("com.fingerprints.extension.navigation.IFingerprintNavigation");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.fingerprints.extension.navigation.IFingerprintNavigation");
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        setNavigation(bool);
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.fingerprints.extension.navigation.IFingerprintNavigation");
        paramParcel1 = getNavigationConfig();
        paramParcel2.writeNoException();
        if (paramParcel1 != null)
        {
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.fingerprints.extension.navigation.IFingerprintNavigation");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NavigationConfig)NavigationConfig.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setNavigationConfig(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("com.fingerprints.extension.navigation.IFingerprintNavigation");
      bool = isEnabled();
      paramParcel2.writeNoException();
      paramInt1 = i;
      if (bool) {
        paramInt1 = 1;
      }
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IFingerprintNavigation
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
        return "com.fingerprints.extension.navigation.IFingerprintNavigation";
      }
      
      /* Error */
      public NavigationConfig getNavigationConfig()
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
        //   14: aload_0
        //   15: getfield 19	com/fingerprints/extension/navigation/IFingerprintNavigation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_2
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 46 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 49	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 53	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 59	com/fingerprints/extension/navigation/NavigationConfig:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 65 2 0
        //   48: checkcast 55	com/fingerprints/extension/navigation/NavigationConfig
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 68	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localNavigationConfig	NavigationConfig
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      /* Error */
      public boolean isEnabled()
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
        //   16: getfield 19	com/fingerprints/extension/navigation/IFingerprintNavigation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_4
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 46 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 49	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 53	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 68	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 68	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public void setNavigation(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 26
        //   14: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +38 -> 56
        //   21: aload_3
        //   22: iload_2
        //   23: invokevirtual 77	android/os/Parcel:writeInt	(I)V
        //   26: aload_0
        //   27: getfield 19	com/fingerprints/extension/navigation/IFingerprintNavigation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   30: iconst_1
        //   31: aload_3
        //   32: aload 4
        //   34: iconst_0
        //   35: invokeinterface 46 5 0
        //   40: pop
        //   41: aload 4
        //   43: invokevirtual 49	android/os/Parcel:readException	()V
        //   46: aload 4
        //   48: invokevirtual 68	android/os/Parcel:recycle	()V
        //   51: aload_3
        //   52: invokevirtual 68	android/os/Parcel:recycle	()V
        //   55: return
        //   56: iconst_0
        //   57: istore_2
        //   58: goto -37 -> 21
        //   61: astore 5
        //   63: aload 4
        //   65: invokevirtual 68	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload 5
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramBoolean	boolean
        //   1	57	2	i	int
        //   5	64	3	localParcel1	Parcel
        //   9	55	4	localParcel2	Parcel
        //   61	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	61	finally
        //   21	46	61	finally
      }
      
      /* Error */
      public void setNavigationConfig(NavigationConfig paramNavigationConfig)
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
        //   14: aload_1
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 77	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 83	com/fingerprints/extension/navigation/NavigationConfig:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	com/fingerprints/extension/navigation/IFingerprintNavigation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_3
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 46 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 49	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 68	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 68	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 77	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	Proxy
        //   0	75	1	paramNavigationConfig	NavigationConfig
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/navigation/IFingerprintNavigation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */