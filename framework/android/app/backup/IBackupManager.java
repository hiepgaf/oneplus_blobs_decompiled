package android.app.backup;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IBackupManager
  extends IInterface
{
  public abstract void acknowledgeFullBackupOrRestore(int paramInt, boolean paramBoolean, String paramString1, String paramString2, IFullBackupRestoreObserver paramIFullBackupRestoreObserver)
    throws RemoteException;
  
  public abstract void agentConnected(String paramString, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void agentDisconnected(String paramString)
    throws RemoteException;
  
  public abstract void backupNow()
    throws RemoteException;
  
  public abstract IRestoreSession beginRestoreSession(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void clearBackupData(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void dataChanged(String paramString)
    throws RemoteException;
  
  public abstract void fullBackup(ParcelFileDescriptor paramParcelFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void fullRestore(ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException;
  
  public abstract void fullTransportBackup(String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract long getAvailableRestoreToken(String paramString)
    throws RemoteException;
  
  public abstract Intent getConfigurationIntent(String paramString)
    throws RemoteException;
  
  public abstract String getCurrentTransport()
    throws RemoteException;
  
  public abstract Intent getDataManagementIntent(String paramString)
    throws RemoteException;
  
  public abstract String getDataManagementLabel(String paramString)
    throws RemoteException;
  
  public abstract String getDestinationString(String paramString)
    throws RemoteException;
  
  public abstract String[] getTransportWhitelist()
    throws RemoteException;
  
  public abstract boolean hasBackupPassword()
    throws RemoteException;
  
  public abstract boolean isAppEligibleForBackup(String paramString)
    throws RemoteException;
  
  public abstract boolean isBackupEnabled()
    throws RemoteException;
  
  public abstract boolean isBackupServiceActive(int paramInt)
    throws RemoteException;
  
  public abstract String[] listAllTransports()
    throws RemoteException;
  
  public abstract void opComplete(int paramInt, long paramLong)
    throws RemoteException;
  
  public abstract int requestBackup(String[] paramArrayOfString, IBackupObserver paramIBackupObserver)
    throws RemoteException;
  
  public abstract void restoreAtInstall(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract String selectBackupTransport(String paramString)
    throws RemoteException;
  
  public abstract void setAutoRestore(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setBackupEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setBackupPassword(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setBackupProvisioned(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setBackupServiceActive(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IBackupManager
  {
    private static final String DESCRIPTOR = "android.app.backup.IBackupManager";
    static final int TRANSACTION_acknowledgeFullBackupOrRestore = 16;
    static final int TRANSACTION_agentConnected = 3;
    static final int TRANSACTION_agentDisconnected = 4;
    static final int TRANSACTION_backupNow = 12;
    static final int TRANSACTION_beginRestoreSession = 25;
    static final int TRANSACTION_clearBackupData = 2;
    static final int TRANSACTION_dataChanged = 1;
    static final int TRANSACTION_fullBackup = 13;
    static final int TRANSACTION_fullRestore = 15;
    static final int TRANSACTION_fullTransportBackup = 14;
    static final int TRANSACTION_getAvailableRestoreToken = 29;
    static final int TRANSACTION_getConfigurationIntent = 21;
    static final int TRANSACTION_getCurrentTransport = 17;
    static final int TRANSACTION_getDataManagementIntent = 23;
    static final int TRANSACTION_getDataManagementLabel = 24;
    static final int TRANSACTION_getDestinationString = 22;
    static final int TRANSACTION_getTransportWhitelist = 19;
    static final int TRANSACTION_hasBackupPassword = 11;
    static final int TRANSACTION_isAppEligibleForBackup = 30;
    static final int TRANSACTION_isBackupEnabled = 9;
    static final int TRANSACTION_isBackupServiceActive = 28;
    static final int TRANSACTION_listAllTransports = 18;
    static final int TRANSACTION_opComplete = 26;
    static final int TRANSACTION_requestBackup = 31;
    static final int TRANSACTION_restoreAtInstall = 5;
    static final int TRANSACTION_selectBackupTransport = 20;
    static final int TRANSACTION_setAutoRestore = 7;
    static final int TRANSACTION_setBackupEnabled = 6;
    static final int TRANSACTION_setBackupPassword = 10;
    static final int TRANSACTION_setBackupProvisioned = 8;
    static final int TRANSACTION_setBackupServiceActive = 27;
    
    public Stub()
    {
      attachInterface(this, "android.app.backup.IBackupManager");
    }
    
    public static IBackupManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.app.backup.IBackupManager");
      if ((localIInterface != null) && ((localIInterface instanceof IBackupManager))) {
        return (IBackupManager)localIInterface;
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
      boolean bool1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.app.backup.IBackupManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        dataChanged(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        clearBackupData(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        agentConnected(paramParcel1.readString(), paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        agentDisconnected(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        restoreAtInstall(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setBackupEnabled(bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setAutoRestore(bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setBackupProvisioned(bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        bool1 = isBackupEnabled();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        bool1 = setBackupPassword(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        bool1 = hasBackupPassword();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        backupNow();
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        ParcelFileDescriptor localParcelFileDescriptor;
        boolean bool2;
        boolean bool3;
        boolean bool4;
        boolean bool5;
        boolean bool6;
        if (paramParcel1.readInt() != 0)
        {
          localParcelFileDescriptor = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label762;
          }
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label768;
          }
          bool2 = true;
          if (paramParcel1.readInt() == 0) {
            break label774;
          }
          bool3 = true;
          if (paramParcel1.readInt() == 0) {
            break label780;
          }
          bool4 = true;
          if (paramParcel1.readInt() == 0) {
            break label786;
          }
          bool5 = true;
          if (paramParcel1.readInt() == 0) {
            break label792;
          }
          bool6 = true;
          if (paramParcel1.readInt() == 0) {
            break label798;
          }
        }
        for (boolean bool7 = true;; bool7 = false)
        {
          fullBackup(localParcelFileDescriptor, bool1, bool2, bool3, bool4, bool5, bool6, bool7, paramParcel1.createStringArray());
          paramParcel2.writeNoException();
          return true;
          localParcelFileDescriptor = null;
          break;
          bool1 = false;
          break label666;
          bool2 = false;
          break label676;
          bool3 = false;
          break label686;
          bool4 = false;
          break label696;
          bool5 = false;
          break label706;
          bool6 = false;
          break label716;
        }
      case 14: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        fullTransportBackup(paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          fullRestore(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          acknowledgeFullBackupOrRestore(paramInt1, bool1, paramParcel1.readString(), paramParcel1.readString(), IFullBackupRestoreObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = getCurrentTransport();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = listAllTransports();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = getTransportWhitelist();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = selectBackupTransport(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = getConfigurationIntent(paramParcel1.readString());
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
      case 22: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = getDestinationString(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = getDataManagementIntent(paramParcel1.readString());
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
      case 24: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = getDataManagementLabel(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramParcel1 = beginRestoreSession(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 26: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        opComplete(paramParcel1.readInt(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setBackupServiceActive(paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 28: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        bool1 = isBackupServiceActive(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 29: 
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        long l = getAvailableRestoreToken(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 30: 
        label666:
        label676:
        label686:
        label696:
        label706:
        label716:
        label762:
        label768:
        label774:
        label780:
        label786:
        label792:
        label798:
        paramParcel1.enforceInterface("android.app.backup.IBackupManager");
        bool1 = isAppEligibleForBackup(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.app.backup.IBackupManager");
      paramInt1 = requestBackup(paramParcel1.createStringArray(), IBackupObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IBackupManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void acknowledgeFullBackupOrRestore(int paramInt, boolean paramBoolean, String paramString1, String paramString2, IFullBackupRestoreObserver paramIFullBackupRestoreObserver)
        throws RemoteException
      {
        Object localObject = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          paramString1 = (String)localObject;
          if (paramIFullBackupRestoreObserver != null) {
            paramString1 = paramIFullBackupRestoreObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString1);
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void agentConnected(String paramString, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
          localParcel1.writeStrongBinder(paramIBinder);
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
      
      public void agentDisconnected(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
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
      
      public void backupNow()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
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
      
      public IRestoreSession beginRestoreSession(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = IRestoreSession.Stub.asInterface(localParcel2.readStrongBinder());
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearBackupData(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
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
      
      public void dataChanged(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
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
      
      public void fullBackup(ParcelFileDescriptor paramParcelFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, boolean paramBoolean7, String[] paramArrayOfString)
        throws RemoteException
      {
        int j = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
            if (paramParcelFileDescriptor != null)
            {
              localParcel1.writeInt(1);
              paramParcelFileDescriptor.writeToParcel(localParcel1, 0);
              break label241;
              localParcel1.writeInt(i);
              if (paramBoolean2)
              {
                i = 1;
                label54:
                localParcel1.writeInt(i);
                if (!paramBoolean3) {
                  break label211;
                }
                i = 1;
                label69:
                localParcel1.writeInt(i);
                if (!paramBoolean4) {
                  break label217;
                }
                i = 1;
                label84:
                localParcel1.writeInt(i);
                if (!paramBoolean5) {
                  break label223;
                }
                i = 1;
                label99:
                localParcel1.writeInt(i);
                if (!paramBoolean6) {
                  break label229;
                }
                i = 1;
                label114:
                localParcel1.writeInt(i);
                if (!paramBoolean7) {
                  break label235;
                }
                i = j;
                label130:
                localParcel1.writeInt(i);
                localParcel1.writeStringArray(paramArrayOfString);
                this.mRemote.transact(13, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
          label211:
          label217:
          label223:
          label229:
          label235:
          label241:
          do
          {
            i = 0;
            break;
            i = 0;
            break label54;
            i = 0;
            break label69;
            i = 0;
            break label84;
            i = 0;
            break label99;
            i = 0;
            break label114;
            i = 0;
            break label130;
          } while (!paramBoolean1);
          int i = 1;
        }
      }
      
      /* Error */
      public void fullRestore(ParcelFileDescriptor paramParcelFileDescriptor)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 91	android/os/ParcelFileDescriptor:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 15
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 58 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 61	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 64	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 64	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 64	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 64	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramParcelFileDescriptor	ParcelFileDescriptor
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void fullTransportBackup(String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeStringArray(paramArrayOfString);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long getAvailableRestoreToken(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Intent getConfigurationIntent(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 21
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 58 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 61	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 110	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 116	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 122 2 0
        //   54: checkcast 112	android/content/Intent
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 64	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramString	String
        //   3	76	2	localParcel1	Parcel
        //   7	68	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	58	73	finally
      }
      
      public String getCurrentTransport()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public Intent getDataManagementIntent(String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_2
        //   15: aload_1
        //   16: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: bipush 23
        //   25: aload_2
        //   26: aload_3
        //   27: iconst_0
        //   28: invokeinterface 58 5 0
        //   33: pop
        //   34: aload_3
        //   35: invokevirtual 61	android/os/Parcel:readException	()V
        //   38: aload_3
        //   39: invokevirtual 110	android/os/Parcel:readInt	()I
        //   42: ifeq +26 -> 68
        //   45: getstatic 116	android/content/Intent:CREATOR	Landroid/os/Parcelable$Creator;
        //   48: aload_3
        //   49: invokeinterface 122 2 0
        //   54: checkcast 112	android/content/Intent
        //   57: astore_1
        //   58: aload_3
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload_2
        //   63: invokevirtual 64	android/os/Parcel:recycle	()V
        //   66: aload_1
        //   67: areturn
        //   68: aconst_null
        //   69: astore_1
        //   70: goto -12 -> 58
        //   73: astore_1
        //   74: aload_3
        //   75: invokevirtual 64	android/os/Parcel:recycle	()V
        //   78: aload_2
        //   79: invokevirtual 64	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramString	String
        //   3	76	2	localParcel1	Parcel
        //   7	68	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	58	73	finally
      }
      
      public String getDataManagementLabel(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getDestinationString(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.app.backup.IBackupManager";
      }
      
      public String[] getTransportWhitelist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
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
      public boolean hasBackupPassword()
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
        //   15: aload_0
        //   16: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 11
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 110	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      public boolean isAppEligibleForBackup(String paramString)
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
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 30
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 58 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 61	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 110	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 64	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 64	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramString	String
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean isBackupEnabled()
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
        //   15: aload_0
        //   16: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 9
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 58 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 61	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 110	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 64	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 64	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 64	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 64	android/os/Parcel:recycle	()V
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
      public boolean isBackupServiceActive(int paramInt)
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
        //   17: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 28
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 58 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 61	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 110	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 64	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 64	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 64	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 64	android/os/Parcel:recycle	()V
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
      
      public String[] listAllTransports()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
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
      
      public void opComplete(int paramInt, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeInt(paramInt);
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int requestBackup(String[] paramArrayOfString, IBackupObserver paramIBackupObserver)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeStringArray(paramArrayOfString);
          paramArrayOfString = (String[])localObject;
          if (paramIBackupObserver != null) {
            paramArrayOfString = paramIBackupObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramArrayOfString);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      
      public void restoreAtInstall(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
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
      
      public String selectBackupTransport(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.readString();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setAutoRestore(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setBackupEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      /* Error */
      public boolean setBackupPassword(String paramString1, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 5
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 6
        //   10: aload 5
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 5
        //   19: aload_1
        //   20: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 43	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_0
        //   30: getfield 19	android/app/backup/IBackupManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 10
        //   35: aload 5
        //   37: aload 6
        //   39: iconst_0
        //   40: invokeinterface 58 5 0
        //   45: pop
        //   46: aload 6
        //   48: invokevirtual 61	android/os/Parcel:readException	()V
        //   51: aload 6
        //   53: invokevirtual 110	android/os/Parcel:readInt	()I
        //   56: istore_3
        //   57: iload_3
        //   58: ifeq +19 -> 77
        //   61: iconst_1
        //   62: istore 4
        //   64: aload 6
        //   66: invokevirtual 64	android/os/Parcel:recycle	()V
        //   69: aload 5
        //   71: invokevirtual 64	android/os/Parcel:recycle	()V
        //   74: iload 4
        //   76: ireturn
        //   77: iconst_0
        //   78: istore 4
        //   80: goto -16 -> 64
        //   83: astore_1
        //   84: aload 6
        //   86: invokevirtual 64	android/os/Parcel:recycle	()V
        //   89: aload 5
        //   91: invokevirtual 64	android/os/Parcel:recycle	()V
        //   94: aload_1
        //   95: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	96	0	this	Proxy
        //   0	96	1	paramString1	String
        //   0	96	2	paramString2	String
        //   56	2	3	i	int
        //   62	17	4	bool	boolean
        //   3	87	5	localParcel1	Parcel
        //   8	77	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	83	finally
      }
      
      public void setBackupProvisioned(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setBackupServiceActive(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.app.backup.IBackupManager");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/backup/IBackupManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */