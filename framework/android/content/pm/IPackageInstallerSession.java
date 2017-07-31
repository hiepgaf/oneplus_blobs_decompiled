package android.content.pm;

import android.content.IntentSender;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IPackageInstallerSession
  extends IInterface
{
  public abstract void abandon()
    throws RemoteException;
  
  public abstract void addClientProgress(float paramFloat)
    throws RemoteException;
  
  public abstract void close()
    throws RemoteException;
  
  public abstract void commit(IntentSender paramIntentSender)
    throws RemoteException;
  
  public abstract String[] getNames()
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openRead(String paramString)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openWrite(String paramString, long paramLong1, long paramLong2)
    throws RemoteException;
  
  public abstract void removeSplit(String paramString)
    throws RemoteException;
  
  public abstract void setClientProgress(float paramFloat)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPackageInstallerSession
  {
    private static final String DESCRIPTOR = "android.content.pm.IPackageInstallerSession";
    static final int TRANSACTION_abandon = 9;
    static final int TRANSACTION_addClientProgress = 2;
    static final int TRANSACTION_close = 7;
    static final int TRANSACTION_commit = 8;
    static final int TRANSACTION_getNames = 3;
    static final int TRANSACTION_openRead = 5;
    static final int TRANSACTION_openWrite = 4;
    static final int TRANSACTION_removeSplit = 6;
    static final int TRANSACTION_setClientProgress = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IPackageInstallerSession");
    }
    
    public static IPackageInstallerSession asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IPackageInstallerSession");
      if ((localIInterface != null) && ((localIInterface instanceof IPackageInstallerSession))) {
        return (IPackageInstallerSession)localIInterface;
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
        paramParcel2.writeString("android.content.pm.IPackageInstallerSession");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        setClientProgress(paramParcel1.readFloat());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        addClientProgress(paramParcel1.readFloat());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        paramParcel1 = getNames();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        paramParcel1 = openWrite(paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readLong());
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
      case 5: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        paramParcel1 = openRead(paramParcel1.readString());
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
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        removeSplit(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        close();
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (IntentSender)IntentSender.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          commit(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.content.pm.IPackageInstallerSession");
      abandon();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IPackageInstallerSession
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void abandon()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstallerSession");
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
      
      public void addClientProgress(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstallerSession");
          localParcel1.writeFloat(paramFloat);
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
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public void close()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstallerSession");
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
      
      /* Error */
      public void commit(IntentSender paramIntentSender)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 68	android/content/IntentSender:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/content/pm/IPackageInstallerSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 8
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 41 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 44	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 47	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 47	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 62	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 47	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 47	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramIntentSender	IntentSender
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.pm.IPackageInstallerSession";
      }
      
      public String[] getNames()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstallerSession");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ParcelFileDescriptor openRead(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 31
        //   11: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 80	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/content/pm/IPackageInstallerSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_5
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 41 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 44	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 84	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 90	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 96 2 0
        //   53: checkcast 86	android/os/ParcelFileDescriptor
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 47	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 47	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 47	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 47	android/os/Parcel:recycle	()V
        //   81: aload_1
        //   82: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	83	0	this	Proxy
        //   0	83	1	paramString	String
        //   3	75	2	localParcel1	Parcel
        //   7	67	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	57	72	finally
      }
      
      /* Error */
      public ParcelFileDescriptor openWrite(String paramString, long paramLong1, long paramLong2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 6
        //   5: invokestatic 29	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 7
        //   10: aload 6
        //   12: ldc 31
        //   14: invokevirtual 35	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 6
        //   19: aload_1
        //   20: invokevirtual 80	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 6
        //   25: lload_2
        //   26: invokevirtual 102	android/os/Parcel:writeLong	(J)V
        //   29: aload 6
        //   31: lload 4
        //   33: invokevirtual 102	android/os/Parcel:writeLong	(J)V
        //   36: aload_0
        //   37: getfield 19	android/content/pm/IPackageInstallerSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   40: iconst_4
        //   41: aload 6
        //   43: aload 7
        //   45: iconst_0
        //   46: invokeinterface 41 5 0
        //   51: pop
        //   52: aload 7
        //   54: invokevirtual 44	android/os/Parcel:readException	()V
        //   57: aload 7
        //   59: invokevirtual 84	android/os/Parcel:readInt	()I
        //   62: ifeq +29 -> 91
        //   65: getstatic 90	android/os/ParcelFileDescriptor:CREATOR	Landroid/os/Parcelable$Creator;
        //   68: aload 7
        //   70: invokeinterface 96 2 0
        //   75: checkcast 86	android/os/ParcelFileDescriptor
        //   78: astore_1
        //   79: aload 7
        //   81: invokevirtual 47	android/os/Parcel:recycle	()V
        //   84: aload 6
        //   86: invokevirtual 47	android/os/Parcel:recycle	()V
        //   89: aload_1
        //   90: areturn
        //   91: aconst_null
        //   92: astore_1
        //   93: goto -14 -> 79
        //   96: astore_1
        //   97: aload 7
        //   99: invokevirtual 47	android/os/Parcel:recycle	()V
        //   102: aload 6
        //   104: invokevirtual 47	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramString	String
        //   0	109	2	paramLong1	long
        //   0	109	4	paramLong2	long
        //   3	100	6	localParcel1	Parcel
        //   8	90	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	79	96	finally
      }
      
      public void removeSplit(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstallerSession");
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
      
      public void setClientProgress(float paramFloat)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.pm.IPackageInstallerSession");
          localParcel1.writeFloat(paramFloat);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IPackageInstallerSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */