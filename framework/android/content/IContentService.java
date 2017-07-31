package android.content;

import android.accounts.Account;
import android.database.IContentObserver;
import android.database.IContentObserver.Stub;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IContentService
  extends IInterface
{
  public abstract void addPeriodicSync(Account paramAccount, String paramString, Bundle paramBundle, long paramLong)
    throws RemoteException;
  
  public abstract void addStatusChangeListener(int paramInt, ISyncStatusObserver paramISyncStatusObserver)
    throws RemoteException;
  
  public abstract void cancelRequest(SyncRequest paramSyncRequest)
    throws RemoteException;
  
  public abstract void cancelSync(Account paramAccount, String paramString, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void cancelSyncAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract Bundle getCache(String paramString, Uri paramUri, int paramInt)
    throws RemoteException;
  
  public abstract List<SyncInfo> getCurrentSyncs()
    throws RemoteException;
  
  public abstract List<SyncInfo> getCurrentSyncsAsUser(int paramInt)
    throws RemoteException;
  
  public abstract int getIsSyncable(Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract int getIsSyncableAsUser(Account paramAccount, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean getMasterSyncAutomatically()
    throws RemoteException;
  
  public abstract boolean getMasterSyncAutomaticallyAsUser(int paramInt)
    throws RemoteException;
  
  public abstract List<PeriodicSync> getPeriodicSyncs(Account paramAccount, String paramString, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract String[] getSyncAdapterPackagesForAuthorityAsUser(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract SyncAdapterType[] getSyncAdapterTypes()
    throws RemoteException;
  
  public abstract SyncAdapterType[] getSyncAdapterTypesAsUser(int paramInt)
    throws RemoteException;
  
  public abstract boolean getSyncAutomatically(Account paramAccount, String paramString)
    throws RemoteException;
  
  public abstract boolean getSyncAutomaticallyAsUser(Account paramAccount, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract SyncStatusInfo getSyncStatus(Account paramAccount, String paramString, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract SyncStatusInfo getSyncStatusAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract boolean isSyncActive(Account paramAccount, String paramString, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean isSyncPending(Account paramAccount, String paramString, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract boolean isSyncPendingAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
    throws RemoteException;
  
  public abstract void notifyChange(Uri paramUri, IContentObserver paramIContentObserver, boolean paramBoolean, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void putCache(String paramString, Uri paramUri, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract void registerContentObserver(Uri paramUri, boolean paramBoolean, IContentObserver paramIContentObserver, int paramInt)
    throws RemoteException;
  
  public abstract void removePeriodicSync(Account paramAccount, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void removeStatusChangeListener(ISyncStatusObserver paramISyncStatusObserver)
    throws RemoteException;
  
  public abstract void requestSync(Account paramAccount, String paramString, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void setIsSyncable(Account paramAccount, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setMasterSyncAutomatically(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMasterSyncAutomaticallyAsUser(boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void setSyncAutomatically(Account paramAccount, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setSyncAutomaticallyAsUser(Account paramAccount, String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void sync(SyncRequest paramSyncRequest)
    throws RemoteException;
  
  public abstract void syncAsUser(SyncRequest paramSyncRequest, int paramInt)
    throws RemoteException;
  
  public abstract void unregisterContentObserver(IContentObserver paramIContentObserver)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IContentService
  {
    private static final String DESCRIPTOR = "android.content.IContentService";
    static final int TRANSACTION_addPeriodicSync = 15;
    static final int TRANSACTION_addStatusChangeListener = 34;
    static final int TRANSACTION_cancelRequest = 9;
    static final int TRANSACTION_cancelSync = 7;
    static final int TRANSACTION_cancelSyncAsUser = 8;
    static final int TRANSACTION_getCache = 37;
    static final int TRANSACTION_getCurrentSyncs = 24;
    static final int TRANSACTION_getCurrentSyncsAsUser = 25;
    static final int TRANSACTION_getIsSyncable = 17;
    static final int TRANSACTION_getIsSyncableAsUser = 18;
    static final int TRANSACTION_getMasterSyncAutomatically = 22;
    static final int TRANSACTION_getMasterSyncAutomaticallyAsUser = 23;
    static final int TRANSACTION_getPeriodicSyncs = 14;
    static final int TRANSACTION_getSyncAdapterPackagesForAuthorityAsUser = 28;
    static final int TRANSACTION_getSyncAdapterTypes = 26;
    static final int TRANSACTION_getSyncAdapterTypesAsUser = 27;
    static final int TRANSACTION_getSyncAutomatically = 10;
    static final int TRANSACTION_getSyncAutomaticallyAsUser = 11;
    static final int TRANSACTION_getSyncStatus = 30;
    static final int TRANSACTION_getSyncStatusAsUser = 31;
    static final int TRANSACTION_isSyncActive = 29;
    static final int TRANSACTION_isSyncPending = 32;
    static final int TRANSACTION_isSyncPendingAsUser = 33;
    static final int TRANSACTION_notifyChange = 3;
    static final int TRANSACTION_putCache = 36;
    static final int TRANSACTION_registerContentObserver = 2;
    static final int TRANSACTION_removePeriodicSync = 16;
    static final int TRANSACTION_removeStatusChangeListener = 35;
    static final int TRANSACTION_requestSync = 4;
    static final int TRANSACTION_setIsSyncable = 19;
    static final int TRANSACTION_setMasterSyncAutomatically = 20;
    static final int TRANSACTION_setMasterSyncAutomaticallyAsUser = 21;
    static final int TRANSACTION_setSyncAutomatically = 12;
    static final int TRANSACTION_setSyncAutomaticallyAsUser = 13;
    static final int TRANSACTION_sync = 5;
    static final int TRANSACTION_syncAsUser = 6;
    static final int TRANSACTION_unregisterContentObserver = 1;
    
    public Stub()
    {
      attachInterface(this, "android.content.IContentService");
    }
    
    public static IContentService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.content.IContentService");
      if ((localIInterface != null) && ((localIInterface instanceof IContentService))) {
        return (IContentService)localIInterface;
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
      Object localObject1;
      boolean bool;
      label425:
      label507:
      label587:
      label757:
      String str;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.content.IContentService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.content.IContentService");
        unregisterContentObserver(IContentObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label425;
          }
        }
        for (bool = true;; bool = false)
        {
          registerContentObserver((Uri)localObject1, bool, IContentObserver.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 3: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          localObject2 = IContentObserver.Stub.asInterface(paramParcel1.readStrongBinder());
          if (paramParcel1.readInt() == 0) {
            break label507;
          }
        }
        for (bool = true;; bool = false)
        {
          notifyChange((Uri)localObject1, (IContentObserver)localObject2, bool, paramParcel1.readInt(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label587;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestSync((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 5: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (SyncRequest)SyncRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          sync(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (SyncRequest)SyncRequest.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          syncAsUser((SyncRequest)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label757;
          }
        }
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          cancelSync((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label842;
          }
        }
        for (localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          cancelSyncAsUser((Account)localObject1, str, (ComponentName)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 9: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (SyncRequest)SyncRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          cancelRequest(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          bool = getSyncAutomatically((Account)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          if (!bool) {
            break label953;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 11: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          bool = getSyncAutomaticallyAsUser((Account)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label1025;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
        }
      case 12: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1095;
          }
        }
        for (bool = true;; bool = false)
        {
          setSyncAutomatically((Account)localObject1, (String)localObject2, bool);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 13: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1170;
          }
        }
        for (bool = true;; bool = false)
        {
          setSyncAutomaticallyAsUser((Account)localObject1, (String)localObject2, bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 14: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1256;
          }
        }
        for (paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = getPeriodicSyncs((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
          localObject1 = null;
          break;
        }
      case 15: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1341;
          }
        }
        for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          addPeriodicSync((Account)localObject1, str, (Bundle)localObject2, paramParcel1.readLong());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 16: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1421;
          }
        }
        for (paramParcel1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          removePeriodicSync((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 17: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramInt1 = getIsSyncable((Account)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 18: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          paramInt1 = getIsSyncableAsUser((Account)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          setIsSyncable((Account)localObject1, paramParcel1.readString(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setMasterSyncAutomatically(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 21: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setMasterSyncAutomaticallyAsUser(bool, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 22: 
        paramParcel1.enforceInterface("android.content.IContentService");
        bool = getMasterSyncAutomatically();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 23: 
        paramParcel1.enforceInterface("android.content.IContentService");
        bool = getMasterSyncAutomaticallyAsUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("android.content.IContentService");
        paramParcel1 = getCurrentSyncs();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.content.IContentService");
        paramParcel1 = getCurrentSyncsAsUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.content.IContentService");
        paramParcel1 = getSyncAdapterTypes();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.content.IContentService");
        paramParcel1 = getSyncAdapterTypesAsUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.content.IContentService");
        paramParcel1 = getSyncAdapterPackagesForAuthorityAsUser(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label1955;
          }
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isSyncActive((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1960;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label1920;
        }
      case 30: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label2055;
          }
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getSyncStatus((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2060;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label2018;
          paramParcel2.writeInt(0);
        }
      case 31: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label2164;
          }
          localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getSyncStatusAsUser((Account)localObject1, str, (ComponentName)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2170;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label2122;
          paramParcel2.writeInt(0);
        }
      case 32: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          localObject2 = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label2266;
          }
          paramParcel1 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isSyncPending((Account)localObject1, (String)localObject2, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label2271;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          paramParcel1 = null;
          break label2231;
        }
      case 33: 
        paramParcel1.enforceInterface("android.content.IContentService");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Account)Account.CREATOR.createFromParcel(paramParcel1);
          str = paramParcel1.readString();
          if (paramParcel1.readInt() == 0) {
            break label2370;
          }
          localObject2 = (ComponentName)ComponentName.CREATOR.createFromParcel(paramParcel1);
          bool = isSyncPendingAsUser((Account)localObject1, str, (ComponentName)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (!bool) {
            break label2376;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label2330;
        }
      case 34: 
        paramParcel1.enforceInterface("android.content.IContentService");
        addStatusChangeListener(paramParcel1.readInt(), ISyncStatusObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 35: 
        paramParcel1.enforceInterface("android.content.IContentService");
        removeStatusChangeListener(ISyncStatusObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 36: 
        label842:
        label953:
        label1025:
        label1095:
        label1170:
        label1256:
        label1341:
        label1421:
        label1920:
        label1955:
        label1960:
        label2018:
        label2055:
        label2060:
        label2122:
        label2164:
        label2170:
        label2231:
        label2266:
        label2271:
        label2330:
        label2370:
        label2376:
        paramParcel1.enforceInterface("android.content.IContentService");
        str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2511;
          }
        }
        label2511:
        for (localObject2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          putCache(str, (Uri)localObject1, (Bundle)localObject2, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      }
      paramParcel1.enforceInterface("android.content.IContentService");
      Object localObject2 = paramParcel1.readString();
      if (paramParcel1.readInt() != 0)
      {
        localObject1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);
        paramParcel1 = getCache((String)localObject2, (Uri)localObject1, paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (paramParcel1 == null) {
          break label2590;
        }
        paramParcel2.writeInt(1);
        paramParcel1.writeToParcel(paramParcel2, 1);
      }
      for (;;)
      {
        return true;
        localObject1 = null;
        break;
        label2590:
        paramParcel2.writeInt(0);
      }
    }
    
    private static class Proxy
      implements IContentService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addPeriodicSync(Account paramAccount, String paramString, Bundle paramBundle, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeLong(paramLong);
                this.mRemote.transact(15, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
        }
      }
      
      public void addStatusChangeListener(int paramInt, ISyncStatusObserver paramISyncStatusObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          localParcel1.writeInt(paramInt);
          if (paramISyncStatusObserver != null) {
            localIBinder = paramISyncStatusObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
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
      public void cancelRequest(SyncRequest paramSyncRequest)
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
        //   26: invokevirtual 85	android/content/SyncRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 9
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 62 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 65	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 68	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 68	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramSyncRequest	SyncRequest
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void cancelSync(Account paramAccount, String paramString, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                this.mRemote.transact(7, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
        }
      }
      
      public void cancelSyncAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(8, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
        }
      }
      
      public Bundle getCache(String paramString, Uri paramUri, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            localParcel1.writeString(paramString);
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(37, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramString = (Bundle)Bundle.CREATOR.createFromParcel(localParcel2);
                return paramString;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramString = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public List<SyncInfo> getCurrentSyncs()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(SyncInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<SyncInfo> getCurrentSyncsAsUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(SyncInfo.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.content.IContentService";
      }
      
      /* Error */
      public int getIsSyncable(Account paramAccount, String paramString)
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
        //   18: ifnull +62 -> 80
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload_0
        //   41: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   44: bipush 17
        //   46: aload 4
        //   48: aload 5
        //   50: iconst_0
        //   51: invokeinterface 62 5 0
        //   56: pop
        //   57: aload 5
        //   59: invokevirtual 65	android/os/Parcel:readException	()V
        //   62: aload 5
        //   64: invokevirtual 101	android/os/Parcel:readInt	()I
        //   67: istore_3
        //   68: aload 5
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 68	android/os/Parcel:recycle	()V
        //   78: iload_3
        //   79: ireturn
        //   80: aload 4
        //   82: iconst_0
        //   83: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   86: goto -52 -> 34
        //   89: astore_1
        //   90: aload 5
        //   92: invokevirtual 68	android/os/Parcel:recycle	()V
        //   95: aload 4
        //   97: invokevirtual 68	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramAccount	Account
        //   0	102	2	paramString	String
        //   67	12	3	i	int
        //   3	93	4	localParcel1	Parcel
        //   8	83	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	89	finally
        //   21	34	89	finally
        //   34	68	89	finally
        //   80	86	89	finally
      }
      
      /* Error */
      public int getIsSyncableAsUser(Account paramAccount, String paramString, int paramInt)
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
        //   18: ifnull +68 -> 86
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 18
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 62 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 65	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 101	android/os/Parcel:readInt	()I
        //   73: istore_3
        //   74: aload 5
        //   76: invokevirtual 68	android/os/Parcel:recycle	()V
        //   79: aload 4
        //   81: invokevirtual 68	android/os/Parcel:recycle	()V
        //   84: iload_3
        //   85: ireturn
        //   86: aload 4
        //   88: iconst_0
        //   89: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   92: goto -58 -> 34
        //   95: astore_1
        //   96: aload 5
        //   98: invokevirtual 68	android/os/Parcel:recycle	()V
        //   101: aload 4
        //   103: invokevirtual 68	android/os/Parcel:recycle	()V
        //   106: aload_1
        //   107: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	108	0	this	Proxy
        //   0	108	1	paramAccount	Account
        //   0	108	2	paramString	String
        //   0	108	3	paramInt	int
        //   3	99	4	localParcel1	Parcel
        //   8	89	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	95	finally
        //   21	34	95	finally
        //   34	74	95	finally
        //   86	92	95	finally
      }
      
      /* Error */
      public boolean getMasterSyncAutomatically()
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
        //   16: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 22
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 62 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 65	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 101	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 68	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 68	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 68	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 68	android/os/Parcel:recycle	()V
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
      public boolean getMasterSyncAutomaticallyAsUser(int paramInt)
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
        //   21: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 23
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 62 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 65	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 101	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 68	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 68	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
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
      
      public List<PeriodicSync> getPeriodicSyncs(Account paramAccount, String paramString, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                this.mRemote.transact(14, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramAccount = localParcel2.createTypedArrayList(PeriodicSync.CREATOR);
                return paramAccount;
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
        }
      }
      
      public String[] getSyncAdapterPackagesForAuthorityAsUser(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createStringArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public SyncAdapterType[] getSyncAdapterTypes()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
          localParcel2.readException();
          SyncAdapterType[] arrayOfSyncAdapterType = (SyncAdapterType[])localParcel2.createTypedArray(SyncAdapterType.CREATOR);
          return arrayOfSyncAdapterType;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public SyncAdapterType[] getSyncAdapterTypesAsUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
          localParcel2.readException();
          SyncAdapterType[] arrayOfSyncAdapterType = (SyncAdapterType[])localParcel2.createTypedArray(SyncAdapterType.CREATOR);
          return arrayOfSyncAdapterType;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean getSyncAutomatically(Account paramAccount, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public boolean getSyncAutomaticallyAsUser(Account paramAccount, String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
      
      public SyncStatusInfo getSyncStatus(Account paramAccount, String paramString, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                this.mRemote.transact(30, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label144;
                }
                paramAccount = (SyncStatusInfo)SyncStatusInfo.CREATOR.createFromParcel(localParcel2);
                return paramAccount;
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
          label144:
          paramAccount = null;
        }
      }
      
      public SyncStatusInfo getSyncStatusAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(31, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label151;
                }
                paramAccount = (SyncStatusInfo)SyncStatusInfo.CREATOR.createFromParcel(localParcel2);
                return paramAccount;
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
          label151:
          paramAccount = null;
        }
      }
      
      public boolean isSyncActive(Account paramAccount, String paramString, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                this.mRemote.transact(29, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label138;
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
          label138:
          boolean bool = false;
        }
      }
      
      public boolean isSyncPending(Account paramAccount, String paramString, ComponentName paramComponentName)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                this.mRemote.transact(32, localParcel1, localParcel2, 0);
                localParcel2.readException();
                int i = localParcel2.readInt();
                if (i == 0) {
                  break label138;
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
          label138:
          boolean bool = false;
        }
      }
      
      public boolean isSyncPendingAsUser(Account paramAccount, String paramString, ComponentName paramComponentName, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramComponentName != null)
              {
                localParcel1.writeInt(1);
                paramComponentName.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(33, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                if (paramInt == 0) {
                  break label145;
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
          label145:
          boolean bool = false;
        }
      }
      
      public void notifyChange(Uri paramUri, IContentObserver paramIContentObserver, boolean paramBoolean, int paramInt1, int paramInt2)
        throws RemoteException
      {
        int i = 1;
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              paramUri = (Uri)localObject;
              if (paramIContentObserver != null) {
                paramUri = paramIContentObserver.asBinder();
              }
              localParcel1.writeStrongBinder(paramUri);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                localParcel1.writeInt(paramInt1);
                localParcel1.writeInt(paramInt2);
                this.mRemote.transact(3, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void putCache(String paramString, Uri paramUri, Bundle paramBundle, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            localParcel1.writeString(paramString);
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(36, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
        }
      }
      
      public void registerContentObserver(Uri paramUri, boolean paramBoolean, IContentObserver paramIContentObserver, int paramInt)
        throws RemoteException
      {
        int i = 1;
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramUri != null)
            {
              localParcel1.writeInt(1);
              paramUri.writeToParcel(localParcel1, 0);
              break label137;
              localParcel1.writeInt(i);
              paramUri = (Uri)localObject;
              if (paramIContentObserver != null) {
                paramUri = paramIContentObserver.asBinder();
              }
              localParcel1.writeStrongBinder(paramUri);
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(2, localParcel1, localParcel2, 0);
              localParcel2.readException();
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
          label137:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      public void removePeriodicSync(Account paramAccount, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(16, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
        }
      }
      
      public void removeStatusChangeListener(ISyncStatusObserver paramISyncStatusObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          if (paramISyncStatusObserver != null) {
            localIBinder = paramISyncStatusObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void requestSync(Account paramAccount, String paramString, Bundle paramBundle)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBundle != null)
              {
                localParcel1.writeInt(1);
                paramBundle.writeToParcel(localParcel1, 0);
                this.mRemote.transact(4, localParcel1, localParcel2, 0);
                localParcel2.readException();
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
        }
      }
      
      /* Error */
      public void setIsSyncable(Account paramAccount, String paramString, int paramInt)
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
        //   18: ifnull +61 -> 79
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 46	android/accounts/Account:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 49	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 19
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 62 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 65	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 68	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   85: goto -51 -> 34
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 68	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 68	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramAccount	Account
        //   0	101	2	paramString	String
        //   0	101	3	paramInt	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	88	finally
        //   21	34	88	finally
        //   34	68	88	finally
        //   79	85	88	finally
      }
      
      public void setMasterSyncAutomatically(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setMasterSyncAutomaticallyAsUser(boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setSyncAutomatically(Account paramAccount, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                this.mRemote.transact(12, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void setSyncAutomaticallyAsUser(Account paramAccount, String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.content.IContentService");
            if (paramAccount != null)
            {
              localParcel1.writeInt(1);
              paramAccount.writeToParcel(localParcel1, 0);
              localParcel1.writeString(paramString);
              if (paramBoolean)
              {
                localParcel1.writeInt(i);
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(13, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            i = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void sync(SyncRequest paramSyncRequest)
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
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 85	android/content/SyncRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: iconst_5
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 62 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 65	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 68	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 68	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 40	android/os/Parcel:writeInt	(I)V
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
        //   0	75	1	paramSyncRequest	SyncRequest
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      /* Error */
      public void syncAsUser(SyncRequest paramSyncRequest, int paramInt)
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
        //   15: aload_1
        //   16: ifnull +50 -> 66
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 85	android/content/SyncRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/content/IContentService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 6
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 62 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 65	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 68	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 68	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 68	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramSyncRequest	SyncRequest
        //   0	86	2	paramInt	int
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void unregisterContentObserver(IContentObserver paramIContentObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.content.IContentService");
          if (paramIContentObserver != null) {
            localIBinder = paramIContentObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/IContentService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */