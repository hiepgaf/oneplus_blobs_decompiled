package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IPackageInstallerCallback
  extends IInterface
{
  public abstract void onSessionActiveChanged(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onSessionBadgingChanged(int paramInt)
    throws RemoteException;
  
  public abstract void onSessionCreated(int paramInt)
    throws RemoteException;
  
  public abstract void onSessionFinished(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void onSessionProgressChanged(int paramInt, float paramFloat)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IPackageInstallerCallback
  {
    private static final String DESCRIPTOR = "android.content.pm.IPackageInstallerCallback";
    static final int TRANSACTION_onSessionActiveChanged = 3;
    static final int TRANSACTION_onSessionBadgingChanged = 2;
    static final int TRANSACTION_onSessionCreated = 1;
    static final int TRANSACTION_onSessionFinished = 5;
    static final int TRANSACTION_onSessionProgressChanged = 4;
    
    public Stub()
    {
      attachInterface(this, "android.content.pm.IPackageInstallerCallback");
    }
    
    public static IPackageInstallerCallback asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.pm.IPackageInstallerCallback");
      if ((localIInterface != null) && ((localIInterface instanceof IPackageInstallerCallback))) {
        return (IPackageInstallerCallback)localIInterface;
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
      boolean bool2 = false;
      boolean bool1 = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.pm.IPackageInstallerCallback");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerCallback");
        onSessionCreated(paramParcel1.readInt());
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerCallback");
        onSessionBadgingChanged(paramParcel1.readInt());
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerCallback");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        onSessionActiveChanged(paramInt1, bool1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.content.pm.IPackageInstallerCallback");
        onSessionProgressChanged(paramParcel1.readInt(), paramParcel1.readFloat());
        return true;
      }
      paramParcel1.enforceInterface("android.content.pm.IPackageInstallerCallback");
      paramInt1 = paramParcel1.readInt();
      bool1 = bool2;
      if (paramParcel1.readInt() != 0) {
        bool1 = true;
      }
      onSessionFinished(paramInt1, bool1);
      return true;
    }
    
    private static class Proxy
      implements IPackageInstallerCallback
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
        return "android.content.pm.IPackageInstallerCallback";
      }
      
      /* Error */
      public void onSessionActiveChanged(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload 4
        //   16: iload_1
        //   17: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   20: iload_2
        //   21: ifeq +32 -> 53
        //   24: iload_3
        //   25: istore_1
        //   26: aload 4
        //   28: iload_1
        //   29: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   32: aload_0
        //   33: getfield 19	android/content/pm/IPackageInstallerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   36: iconst_3
        //   37: aload 4
        //   39: aconst_null
        //   40: iconst_1
        //   41: invokeinterface 50 5 0
        //   46: pop
        //   47: aload 4
        //   49: invokevirtual 53	android/os/Parcel:recycle	()V
        //   52: return
        //   53: iconst_0
        //   54: istore_1
        //   55: goto -29 -> 26
        //   58: astore 5
        //   60: aload 4
        //   62: invokevirtual 53	android/os/Parcel:recycle	()V
        //   65: aload 5
        //   67: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	68	0	this	Proxy
        //   0	68	1	paramInt	int
        //   0	68	2	paramBoolean	boolean
        //   1	24	3	i	int
        //   5	56	4	localParcel	Parcel
        //   58	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	20	58	finally
        //   26	47	58	finally
      }
      
      public void onSessionBadgingChanged(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.content.pm.IPackageInstallerCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void onSessionCreated(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.content.pm.IPackageInstallerCallback");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onSessionFinished(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_3
        //   2: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: aload 4
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload 4
        //   16: iload_1
        //   17: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   20: iload_2
        //   21: ifeq +32 -> 53
        //   24: iload_3
        //   25: istore_1
        //   26: aload 4
        //   28: iload_1
        //   29: invokevirtual 44	android/os/Parcel:writeInt	(I)V
        //   32: aload_0
        //   33: getfield 19	android/content/pm/IPackageInstallerCallback$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   36: iconst_5
        //   37: aload 4
        //   39: aconst_null
        //   40: iconst_1
        //   41: invokeinterface 50 5 0
        //   46: pop
        //   47: aload 4
        //   49: invokevirtual 53	android/os/Parcel:recycle	()V
        //   52: return
        //   53: iconst_0
        //   54: istore_1
        //   55: goto -29 -> 26
        //   58: astore 5
        //   60: aload 4
        //   62: invokevirtual 53	android/os/Parcel:recycle	()V
        //   65: aload 5
        //   67: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	68	0	this	Proxy
        //   0	68	1	paramInt	int
        //   0	68	2	paramBoolean	boolean
        //   1	24	3	i	int
        //   5	56	4	localParcel	Parcel
        //   58	8	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   7	20	58	finally
        //   26	47	58	finally
      }
      
      public void onSessionProgressChanged(int paramInt, float paramFloat)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.content.pm.IPackageInstallerCallback");
          localParcel.writeInt(paramInt);
          localParcel.writeFloat(paramFloat);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/IPackageInstallerCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */