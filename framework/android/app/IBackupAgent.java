package android.app;

import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IBackupAgent
  extends IInterface
{
  public abstract void doBackup(ParcelFileDescriptor paramParcelFileDescriptor1, ParcelFileDescriptor paramParcelFileDescriptor2, ParcelFileDescriptor paramParcelFileDescriptor3, int paramInt, IBackupManager paramIBackupManager)
    throws RemoteException;
  
  public abstract void doFullBackup(ParcelFileDescriptor paramParcelFileDescriptor, int paramInt, IBackupManager paramIBackupManager)
    throws RemoteException;
  
  public abstract void doMeasureFullBackup(int paramInt, IBackupManager paramIBackupManager)
    throws RemoteException;
  
  public abstract void doQuotaExceeded(long paramLong1, long paramLong2)
    throws RemoteException;
  
  public abstract void doRestore(ParcelFileDescriptor paramParcelFileDescriptor1, int paramInt1, ParcelFileDescriptor paramParcelFileDescriptor2, int paramInt2, IBackupManager paramIBackupManager)
    throws RemoteException;
  
  public abstract void doRestoreFile(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, int paramInt1, String paramString1, String paramString2, long paramLong2, long paramLong3, int paramInt2, IBackupManager paramIBackupManager)
    throws RemoteException;
  
  public abstract void doRestoreFinished(int paramInt, IBackupManager paramIBackupManager)
    throws RemoteException;
  
  public abstract void fail(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBackupAgent
  {
    private static final String DESCRIPTOR = "android.app.IBackupAgent";
    static final int TRANSACTION_doBackup = 1;
    static final int TRANSACTION_doFullBackup = 3;
    static final int TRANSACTION_doMeasureFullBackup = 4;
    static final int TRANSACTION_doQuotaExceeded = 5;
    static final int TRANSACTION_doRestore = 2;
    static final int TRANSACTION_doRestoreFile = 6;
    static final int TRANSACTION_doRestoreFinished = 7;
    static final int TRANSACTION_fail = 8;
    
    public Stub()
    {
      attachInterface(this, "android.app.IBackupAgent");
    }
    
    public static IBackupAgent asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.IBackupAgent");
      if ((localIInterface != null) && ((localIInterface instanceof IBackupAgent))) {
        return (IBackupAgent)localIInterface;
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
      ParcelFileDescriptor localParcelFileDescriptor1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.IBackupAgent");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.IBackupAgent");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label197;
          }
          localParcelFileDescriptor1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label203;
          }
        }
        for (ParcelFileDescriptor localParcelFileDescriptor2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; localParcelFileDescriptor2 = null)
        {
          doBackup(paramParcel2, localParcelFileDescriptor1, localParcelFileDescriptor2, paramParcel1.readInt(), IBackupManager.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
          paramParcel2 = null;
          break;
          localParcelFileDescriptor1 = null;
          break label149;
        }
      case 2: 
        paramParcel1.enforceInterface("android.app.IBackupAgent");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label287;
          }
        }
        for (localParcelFileDescriptor1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; localParcelFileDescriptor1 = null)
        {
          doRestore(paramParcel2, paramInt1, localParcelFileDescriptor1, paramParcel1.readInt(), IBackupManager.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
          paramParcel2 = null;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.app.IBackupAgent");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          doFullBackup(paramParcel2, paramParcel1.readInt(), IBackupManager.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.app.IBackupAgent");
        doMeasureFullBackup(paramParcel1.readInt(), IBackupManager.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.app.IBackupAgent");
        doQuotaExceeded(paramParcel1.readLong(), paramParcel1.readLong());
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.IBackupAgent");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel2 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; paramParcel2 = null)
        {
          doRestoreFile(paramParcel2, paramParcel1.readLong(), paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readLong(), paramParcel1.readLong(), paramParcel1.readInt(), IBackupManager.Stub.asInterface(paramParcel1.readStrongBinder()));
          return true;
        }
      case 7: 
        label149:
        label197:
        label203:
        label287:
        paramParcel1.enforceInterface("android.app.IBackupAgent");
        doRestoreFinished(paramParcel1.readInt(), IBackupManager.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      }
      paramParcel1.enforceInterface("android.app.IBackupAgent");
      fail(paramParcel1.readString());
      return true;
    }
    
    private static class Proxy
      implements IBackupAgent
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
      
      public void doBackup(ParcelFileDescriptor paramParcelFileDescriptor1, ParcelFileDescriptor paramParcelFileDescriptor2, ParcelFileDescriptor paramParcelFileDescriptor3, int paramInt, IBackupManager paramIBackupManager)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.app.IBackupAgent");
            if (paramParcelFileDescriptor1 != null)
            {
              localParcel.writeInt(1);
              paramParcelFileDescriptor1.writeToParcel(localParcel, 0);
              if (paramParcelFileDescriptor2 != null)
              {
                localParcel.writeInt(1);
                paramParcelFileDescriptor2.writeToParcel(localParcel, 0);
                if (paramParcelFileDescriptor3 == null) {
                  break label142;
                }
                localParcel.writeInt(1);
                paramParcelFileDescriptor3.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt);
                paramParcelFileDescriptor1 = (ParcelFileDescriptor)localObject;
                if (paramIBackupManager != null) {
                  paramParcelFileDescriptor1 = paramIBackupManager.asBinder();
                }
                localParcel.writeStrongBinder(paramParcelFileDescriptor1);
                this.mRemote.transact(1, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
          continue;
          label142:
          localParcel.writeInt(0);
        }
      }
      
      /* Error */
      public void doFullBackup(ParcelFileDescriptor paramParcelFileDescriptor, int paramInt, IBackupManager paramIBackupManager)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 34
        //   12: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +63 -> 79
        //   19: aload 5
        //   21: iconst_1
        //   22: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   25: aload_1
        //   26: aload 5
        //   28: iconst_0
        //   29: invokevirtual 48	android/os/ParcelFileDescriptor:writeToParcel	(Landroid/os/Parcel;I)V
        //   32: aload 5
        //   34: iload_2
        //   35: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   38: aload 4
        //   40: astore_1
        //   41: aload_3
        //   42: ifnull +10 -> 52
        //   45: aload_3
        //   46: invokeinterface 52 1 0
        //   51: astore_1
        //   52: aload 5
        //   54: aload_1
        //   55: invokevirtual 55	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   58: aload_0
        //   59: getfield 19	android/app/IBackupAgent$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   62: iconst_3
        //   63: aload 5
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 61 5 0
        //   72: pop
        //   73: aload 5
        //   75: invokevirtual 64	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 5
        //   81: iconst_0
        //   82: invokevirtual 42	android/os/Parcel:writeInt	(I)V
        //   85: goto -53 -> 32
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 64	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramParcelFileDescriptor	ParcelFileDescriptor
        //   0	96	2	paramInt	int
        //   0	96	3	paramIBackupManager	IBackupManager
        //   1	38	4	localObject	Object
        //   6	84	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	15	88	finally
        //   19	32	88	finally
        //   32	38	88	finally
        //   45	52	88	finally
        //   52	73	88	finally
        //   79	85	88	finally
      }
      
      public void doMeasureFullBackup(int paramInt, IBackupManager paramIBackupManager)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IBackupAgent");
          localParcel.writeInt(paramInt);
          if (paramIBackupManager != null) {
            localIBinder = paramIBackupManager.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void doQuotaExceeded(long paramLong1, long paramLong2)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IBackupAgent");
          localParcel.writeLong(paramLong1);
          localParcel.writeLong(paramLong2);
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void doRestore(ParcelFileDescriptor paramParcelFileDescriptor1, int paramInt1, ParcelFileDescriptor paramParcelFileDescriptor2, int paramInt2, IBackupManager paramIBackupManager)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.app.IBackupAgent");
            if (paramParcelFileDescriptor1 != null)
            {
              localParcel.writeInt(1);
              paramParcelFileDescriptor1.writeToParcel(localParcel, 0);
              localParcel.writeInt(paramInt1);
              if (paramParcelFileDescriptor2 != null)
              {
                localParcel.writeInt(1);
                paramParcelFileDescriptor2.writeToParcel(localParcel, 0);
                localParcel.writeInt(paramInt2);
                paramParcelFileDescriptor1 = (ParcelFileDescriptor)localObject;
                if (paramIBackupManager != null) {
                  paramParcelFileDescriptor1 = paramIBackupManager.asBinder();
                }
                localParcel.writeStrongBinder(paramParcelFileDescriptor1);
                this.mRemote.transact(2, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            localParcel.writeInt(0);
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void doRestoreFile(ParcelFileDescriptor paramParcelFileDescriptor, long paramLong1, int paramInt1, String paramString1, String paramString2, long paramLong2, long paramLong3, int paramInt2, IBackupManager paramIBackupManager)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel.writeInterfaceToken("android.app.IBackupAgent");
            if (paramParcelFileDescriptor != null)
            {
              localParcel.writeInt(1);
              paramParcelFileDescriptor.writeToParcel(localParcel, 0);
              localParcel.writeLong(paramLong1);
              localParcel.writeInt(paramInt1);
              localParcel.writeString(paramString1);
              localParcel.writeString(paramString2);
              localParcel.writeLong(paramLong2);
              localParcel.writeLong(paramLong3);
              localParcel.writeInt(paramInt2);
              if (paramIBackupManager != null)
              {
                paramParcelFileDescriptor = paramIBackupManager.asBinder();
                localParcel.writeStrongBinder(paramParcelFileDescriptor);
                this.mRemote.transact(6, localParcel, null, 1);
              }
            }
            else
            {
              localParcel.writeInt(0);
              continue;
            }
            paramParcelFileDescriptor = null;
          }
          finally
          {
            localParcel.recycle();
          }
        }
      }
      
      public void doRestoreFinished(int paramInt, IBackupManager paramIBackupManager)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IBackupAgent");
          localParcel.writeInt(paramInt);
          if (paramIBackupManager != null) {
            localIBinder = paramIBackupManager.asBinder();
          }
          localParcel.writeStrongBinder(localIBinder);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void fail(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("android.app.IBackupAgent");
          localParcel.writeString(paramString);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.IBackupAgent";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IBackupAgent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */