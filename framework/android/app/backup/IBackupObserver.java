package android.app.backup;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IBackupObserver
  extends IInterface
{
  public abstract void backupFinished(int paramInt)
    throws RemoteException;
  
  public abstract void onResult(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void onUpdate(String paramString, BackupProgress paramBackupProgress)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBackupObserver
  {
    private static final String DESCRIPTOR = "android.app.backup.IBackupObserver";
    static final int TRANSACTION_backupFinished = 3;
    static final int TRANSACTION_onResult = 2;
    static final int TRANSACTION_onUpdate = 1;
    
    public Stub()
    {
      attachInterface(this, "android.app.backup.IBackupObserver");
    }
    
    public static IBackupObserver asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.backup.IBackupObserver");
      if ((localIInterface != null) && ((localIInterface instanceof IBackupObserver))) {
        return (IBackupObserver)localIInterface;
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
        paramParcel2.writeString("android.app.backup.IBackupObserver");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.backup.IBackupObserver");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (BackupProgress)BackupProgress.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          onUpdate(paramParcel2, paramParcel1);
          return true;
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.backup.IBackupObserver");
        onResult(paramParcel1.readString(), paramParcel1.readInt());
        return true;
      }
      paramParcel1.enforceInterface("android.app.backup.IBackupObserver");
      backupFinished(paramParcel1.readInt());
      return true;
    }
    
    private static class Proxy
      implements IBackupObserver
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
      
      public void backupFinished(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.backup.IBackupObserver");
          localParcel.writeInt(paramInt);
          this.mRemote.transact(3, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.backup.IBackupObserver";
      }
      
      public void onResult(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.backup.IBackupObserver");
          localParcel.writeString(paramString);
          localParcel.writeInt(paramInt);
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void onUpdate(String paramString, BackupProgress paramBackupProgress)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: aload_3
        //   5: ldc 34
        //   7: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_3
        //   11: aload_1
        //   12: invokevirtual 58	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   15: aload_2
        //   16: ifnull +33 -> 49
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 66	android/app/backup/BackupProgress:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/app/backup/IBackupObserver$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_3
        //   36: aconst_null
        //   37: iconst_1
        //   38: invokeinterface 47 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 50	android/os/Parcel:recycle	()V
        //   48: return
        //   49: aload_3
        //   50: iconst_0
        //   51: invokevirtual 41	android/os/Parcel:writeInt	(I)V
        //   54: goto -24 -> 30
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 50	android/os/Parcel:recycle	()V
        //   62: aload_1
        //   63: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	64	0	this	Proxy
        //   0	64	1	paramString	String
        //   0	64	2	paramBackupProgress	BackupProgress
        //   3	56	3	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	15	57	finally
        //   19	30	57	finally
        //   30	44	57	finally
        //   49	54	57	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/IBackupObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */