package android.nfc;

import android.content.ComponentName;
import android.nfc.cardemulation.AidGroup;
import android.nfc.cardemulation.ApduServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface INfcCardEmulation
  extends IInterface
{
  public abstract AidGroup getAidGroupForService(int paramInt, ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract List<ApduServiceInfo> getServices(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract boolean isDefaultServiceForAid(int paramInt, ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean isDefaultServiceForCategory(int paramInt, ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean registerAidGroupForService(int paramInt, ComponentName paramComponentName, AidGroup paramAidGroup)
    throws RemoteException;
  
  public abstract boolean removeAidGroupForService(int paramInt, ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean setDefaultForNextTap(int paramInt, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean setDefaultServiceForCategory(int paramInt, ComponentName paramComponentName, String paramString)
    throws RemoteException;
  
  public abstract boolean setPreferredService(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean supportsAidPrefixRegistration()
    throws RemoteException;
  
  public abstract boolean unsetPreferredService()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INfcCardEmulation
  {
    private static final String DESCRIPTOR = "android.nfc.INfcCardEmulation";
    static final int TRANSACTION_getAidGroupForService = 6;
    static final int TRANSACTION_getServices = 8;
    static final int TRANSACTION_isDefaultServiceForAid = 2;
    static final int TRANSACTION_isDefaultServiceForCategory = 1;
    static final int TRANSACTION_registerAidGroupForService = 5;
    static final int TRANSACTION_removeAidGroupForService = 7;
    static final int TRANSACTION_setDefaultForNextTap = 4;
    static final int TRANSACTION_setDefaultServiceForCategory = 3;
    static final int TRANSACTION_setPreferredService = 9;
    static final int TRANSACTION_supportsAidPrefixRegistration = 11;
    static final int TRANSACTION_unsetPreferredService = 10;
    
    public Stub()
    {
      attachInterface(this, "android.nfc.INfcCardEmulation");
    }
    
    public static INfcCardEmulation asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.nfc.INfcCardEmulation");
      if ((localIInterface != null) && ((localIInterface instanceof INfcCardEmulation))) {
        return (INfcCardEmulation)localIInterface;
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
      ComponentName localComponentName;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.nfc.INfcCardEmulation");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isDefaultServiceForCategory(paramInt1, localComponentName, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label195;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localComponentName = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isDefaultServiceForAid(paramInt1, localComponentName, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label269;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localComponentName = null;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setDefaultServiceForCategory(paramInt1, localComponentName, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label343;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localComponentName = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setDefaultForNextTap(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label410;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 5: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label501;
          }
          paramParcel1 = (AidGroup)AidGroup.CREATOR.createFromParcel(paramParcel1);
          bool = registerAidGroupForService(paramInt1, localComponentName, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label506;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localComponentName = null;
          break;
          paramParcel1 = null;
          break label467;
        }
      case 6: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getAidGroupForService(paramInt1, localComponentName, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label582;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localComponentName = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localComponentName = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = removeAidGroupForService(paramInt1, localComponentName, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label659;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localComponentName = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        paramParcel1 = getServices(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = setPreferredService(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label750;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 10: 
        label195:
        label269:
        label343:
        label410:
        label467:
        label501:
        label506:
        label582:
        label659:
        label750:
        paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
        bool = unsetPreferredService();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.nfc.INfcCardEmulation");
      boolean bool = supportsAidPrefixRegistration();
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements INfcCardEmulation
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
      
      public AidGroup getAidGroupForService(int paramInt, ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(6, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramComponentName = (AidGroup)AidGroup.CREATOR.createFromParcel(localParcel2);
                return paramComponentName;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramComponentName = null;
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
        return "android.nfc.INfcCardEmulation";
      }
      
      public List<ApduServiceInfo> getServices(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createTypedArrayList(ApduServiceInfo.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean isDefaultServiceForAid(int paramInt, ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
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
      
      public boolean isDefaultServiceForCategory(int paramInt, ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public boolean registerAidGroupForService(int paramInt, ComponentName paramComponentName, AidGroup paramAidGroup)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              if (paramAidGroup != null)
              {
                localParcel1.writeInt(1);
                paramAidGroup.writeToParcel(localParcel1, 0);
                this.mRemote.transact(5, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (paramInt == 0) {
                  break label135;
                }
                bool = true;
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label135:
          boolean bool = false;
        }
      }
      
      public boolean removeAidGroupForService(int paramInt, ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
      
      public boolean setDefaultForNextTap(int paramInt, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      
      public boolean setDefaultServiceForCategory(int paramInt, ComponentName paramComponentName, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
            localParcel1.writeInt(paramInt);
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
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
      
      public boolean setPreferredService(ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.nfc.INfcCardEmulation");
            if (paramComponentName != null)
            {
              localParcel1.writeInt(1);
              paramComponentName.writeToParcel(localParcel1, 0);
              this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public boolean supportsAidPrefixRegistration()
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
        //   16: getfield 19	android/nfc/INfcCardEmulation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 11
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 57 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 60	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 64	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 79	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 79	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 79	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 79	android/os/Parcel:recycle	()V
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
      public boolean unsetPreferredService()
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
        //   16: getfield 19	android/nfc/INfcCardEmulation$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 10
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 57 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 60	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 64	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 79	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 79	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 79	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 79	android/os/Parcel:recycle	()V
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/nfc/INfcCardEmulation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */