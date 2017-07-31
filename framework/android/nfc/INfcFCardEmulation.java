package android.nfc;

import android.content.ComponentName;
import android.nfc.cardemulation.NfcFServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface INfcFCardEmulation
  extends IInterface
{
  public abstract boolean disableNfcFForegroundService()
    throws RemoteException;
  
  public abstract boolean enableNfcFForegroundService(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract int getMaxNumOfRegisterableSystemCodes()
    throws RemoteException;
  
  public abstract List<NfcFServiceInfo> getNfcFServices(int paramInt)
    throws RemoteException;
  
  public abstract String getNfcid2ForService(int paramInt, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract String getSystemCodeForService(int paramInt, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean registerSystemCodeForService(int paramInt, ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean removeSystemCodeForService(int paramInt, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean setNfcid2ForService(int paramInt, ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INfcFCardEmulation
  {
    private static final String DESCRIPTOR = "android.nfc.INfcFCardEmulation";
    static final int TRANSACTION_disableNfcFForegroundService = 7;
    static final int TRANSACTION_enableNfcFForegroundService = 6;
    static final int TRANSACTION_getMaxNumOfRegisterableSystemCodes = 9;
    static final int TRANSACTION_getNfcFServices = 8;
    static final int TRANSACTION_getNfcid2ForService = 4;
    static final int TRANSACTION_getSystemCodeForService = 1;
    static final int TRANSACTION_registerSystemCodeForService = 2;
    static final int TRANSACTION_removeSystemCodeForService = 3;
    static final int TRANSACTION_setNfcid2ForService = 5;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.INfcFCardEmulation");
    }
    
    public static INfcFCardEmulation asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.INfcFCardEmulation");
      if ((localIInterface != null) && ((localIInterface instanceof INfcFCardEmulation))) {
        return (INfcFCardEmulation)localIInterface;
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
      int k = 0;
      int m = 0;
      int n = 0;
      int i = 0;
      ComponentName localComponentName;
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.nfc.INfcFCardEmulation");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getSystemCodeForService(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localComponentName = null)
        {
          bool = registerSystemCodeForService(paramInt1, localComponentName, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramInt1 = i;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 3: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          bool = removeSystemCodeForService(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = j;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getNfcid2ForService(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localComponentName = null)
        {
          bool = setNfcid2ForService(paramInt1, localComponentName, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramInt1 = k;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          bool = enableNfcFForegroundService(paramParcel1);
          paramParcel2.writeNoException();
          paramInt1 = m;
          if (bool) {
            paramInt1 = 1;
          }
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        bool = disableNfcFForegroundService();
        paramParcel2.writeNoException();
        paramInt1 = n;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
        paramParcel1 = getNfcFServices(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("android.nfc.INfcFCardEmulation");
      paramInt1 = getMaxNumOfRegisterableSystemCodes();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements INfcFCardEmulation
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
      public boolean disableNfcFForegroundService()
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
        //   16: getfield 19	android/nfc/INfcFCardEmulation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 7
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 44 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 47	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public boolean enableNfcFForegroundService(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcFCardEmulation");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(6, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.nfc.INfcFCardEmulation";
      }
      
      public int getMaxNumOfRegisterableSystemCodes()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcFCardEmulation");
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
      
      public List<NfcFServiceInfo> getNfcFServices(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcFCardEmulation");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(NfcFServiceInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public String getNfcid2ForService(int paramInt, ComponentName paramComponentName)
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
        //   17: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +51 -> 72
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 67	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/nfc/INfcFCardEmulation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_4
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 44 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 47	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 89	android/os/Parcel:readString	()Ljava/lang/String;
        //   60: astore_2
        //   61: aload 4
        //   63: invokevirtual 54	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: areturn
        //   72: aload_3
        //   73: iconst_0
        //   74: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   77: goto -42 -> 35
        //   80: astore_2
        //   81: aload 4
        //   83: invokevirtual 54	android/os/Parcel:recycle	()V
        //   86: aload_3
        //   87: invokevirtual 54	android/os/Parcel:recycle	()V
        //   90: aload_2
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramInt	int
        //   0	92	2	paramComponentName	ComponentName
        //   3	84	3	localParcel1	Parcel
        //   7	75	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	80	finally
        //   24	35	80	finally
        //   35	61	80	finally
        //   72	77	80	finally
      }
      
      /* Error */
      public String getSystemCodeForService(int paramInt, ComponentName paramComponentName)
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
        //   17: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +51 -> 72
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 67	android/content/ComponentName:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/nfc/INfcFCardEmulation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: iconst_1
        //   40: aload_3
        //   41: aload 4
        //   43: iconst_0
        //   44: invokeinterface 44 5 0
        //   49: pop
        //   50: aload 4
        //   52: invokevirtual 47	android/os/Parcel:readException	()V
        //   55: aload 4
        //   57: invokevirtual 89	android/os/Parcel:readString	()Ljava/lang/String;
        //   60: astore_2
        //   61: aload 4
        //   63: invokevirtual 54	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: areturn
        //   72: aload_3
        //   73: iconst_0
        //   74: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   77: goto -42 -> 35
        //   80: astore_2
        //   81: aload 4
        //   83: invokevirtual 54	android/os/Parcel:recycle	()V
        //   86: aload_3
        //   87: invokevirtual 54	android/os/Parcel:recycle	()V
        //   90: aload_2
        //   91: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	92	0	this	Proxy
        //   0	92	1	paramInt	int
        //   0	92	2	paramComponentName	ComponentName
        //   3	84	3	localParcel1	Parcel
        //   7	75	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	80	finally
        //   24	35	80	finally
        //   35	61	80	finally
        //   72	77	80	finally
      }
      
      public boolean registerSystemCodeForService(int paramInt, ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcFCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean removeSystemCodeForService(int paramInt, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcFCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(3, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public boolean setNfcid2ForService(int paramInt, ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcFCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(5, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramInt = localParcel2.readInt();
              if (paramInt != 0)
              {
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            boolean bool = false;
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
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/INfcFCardEmulation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */