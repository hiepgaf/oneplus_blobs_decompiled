package android.os.storage;

import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageMoveObserver.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

public abstract interface IMountService
  extends IInterface
{
  public static final int ENCRYPTION_STATE_ERROR_CORRUPT = -4;
  public static final int ENCRYPTION_STATE_ERROR_INCOMPLETE = -2;
  public static final int ENCRYPTION_STATE_ERROR_INCONSISTENT = -3;
  public static final int ENCRYPTION_STATE_ERROR_MDTP_ACTIVATED = -5;
  public static final int ENCRYPTION_STATE_ERROR_UNKNOWN = -1;
  public static final int ENCRYPTION_STATE_NONE = 1;
  public static final int ENCRYPTION_STATE_OK = 0;
  public static final int ENCRYPTION_STATE_OK_MDTP_ACTIVATED = 2;
  
  public abstract void addUserKeyAuth(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException;
  
  public abstract long benchmark(String paramString)
    throws RemoteException;
  
  public abstract int changeEncryptionPassword(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void clearPassword()
    throws RemoteException;
  
  public abstract int createSecureContainer(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void createUserKey(int paramInt1, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int decryptStorage(String paramString)
    throws RemoteException;
  
  public abstract int destroySecureContainer(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void destroyUserKey(int paramInt)
    throws RemoteException;
  
  public abstract void destroyUserStorage(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int encryptStorage(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract int encryptWipeStorage(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract int finalizeSecureContainer(String paramString)
    throws RemoteException;
  
  public abstract void finishMediaUpdate()
    throws RemoteException;
  
  public abstract int fixPermissionsSecureContainer(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract void fixateNewestUserKeyAuth(int paramInt)
    throws RemoteException;
  
  public abstract void forgetAllVolumes()
    throws RemoteException;
  
  public abstract void forgetVolume(String paramString)
    throws RemoteException;
  
  public abstract void format(String paramString)
    throws RemoteException;
  
  public abstract int formatVolume(String paramString)
    throws RemoteException;
  
  public abstract DiskInfo[] getDisks()
    throws RemoteException;
  
  public abstract int getEncryptionState()
    throws RemoteException;
  
  public abstract String getField(String paramString)
    throws RemoteException;
  
  public abstract String getMountedObbPath(String paramString)
    throws RemoteException;
  
  public abstract String getPassword()
    throws RemoteException;
  
  public abstract int getPasswordType()
    throws RemoteException;
  
  public abstract String getPrimaryStorageUuid()
    throws RemoteException;
  
  public abstract String getSecureContainerFilesystemPath(String paramString)
    throws RemoteException;
  
  public abstract String[] getSecureContainerList()
    throws RemoteException;
  
  public abstract String getSecureContainerPath(String paramString)
    throws RemoteException;
  
  public abstract int[] getStorageUsers(String paramString)
    throws RemoteException;
  
  public abstract StorageVolume[] getVolumeList(int paramInt1, String paramString, int paramInt2)
    throws RemoteException;
  
  public abstract VolumeRecord[] getVolumeRecords(int paramInt)
    throws RemoteException;
  
  public abstract String getVolumeState(String paramString)
    throws RemoteException;
  
  public abstract VolumeInfo[] getVolumes(int paramInt)
    throws RemoteException;
  
  public abstract boolean isConvertibleToFBE()
    throws RemoteException;
  
  public abstract boolean isExternalStorageEmulated()
    throws RemoteException;
  
  public abstract boolean isObbMounted(String paramString)
    throws RemoteException;
  
  public abstract boolean isSecureContainerMounted(String paramString)
    throws RemoteException;
  
  public abstract boolean isUsbMassStorageConnected()
    throws RemoteException;
  
  public abstract boolean isUsbMassStorageEnabled()
    throws RemoteException;
  
  public abstract boolean isUserKeyUnlocked(int paramInt)
    throws RemoteException;
  
  public abstract long lastMaintenance()
    throws RemoteException;
  
  public abstract void lockUserKey(int paramInt)
    throws RemoteException;
  
  public abstract int mkdirs(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void mount(String paramString)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor mountAppFuse(String paramString)
    throws RemoteException;
  
  public abstract void mountObb(String paramString1, String paramString2, String paramString3, IObbActionListener paramIObbActionListener, int paramInt)
    throws RemoteException;
  
  public abstract int mountSecureContainer(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int mountVolume(String paramString)
    throws RemoteException;
  
  public abstract void partitionMixed(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void partitionPrivate(String paramString)
    throws RemoteException;
  
  public abstract void partitionPublic(String paramString)
    throws RemoteException;
  
  public abstract void prepareUserStorage(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void registerListener(IMountServiceListener paramIMountServiceListener)
    throws RemoteException;
  
  public abstract int renameSecureContainer(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract int resizeSecureContainer(String paramString1, int paramInt, String paramString2)
    throws RemoteException;
  
  public abstract void runMaintenance()
    throws RemoteException;
  
  public abstract void setDebugFlags(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setField(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setPrimaryStorageUuid(String paramString, IPackageMoveObserver paramIPackageMoveObserver)
    throws RemoteException;
  
  public abstract void setUsbMassStorageEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setVolumeNickname(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void setVolumeUserFlags(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void shutdown(IMountShutdownObserver paramIMountShutdownObserver)
    throws RemoteException;
  
  public abstract void unlockUserKey(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws RemoteException;
  
  public abstract void unmount(String paramString)
    throws RemoteException;
  
  public abstract void unmountObb(String paramString, boolean paramBoolean, IObbActionListener paramIObbActionListener, int paramInt)
    throws RemoteException;
  
  public abstract int unmountSecureContainer(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void unmountVolume(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void unregisterListener(IMountServiceListener paramIMountServiceListener)
    throws RemoteException;
  
  public abstract int verifyEncryptionPassword(String paramString)
    throws RemoteException;
  
  public abstract void waitForAsecScan()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IMountService
  {
    private static final String DESCRIPTOR = "IMountService";
    static final int TRANSACTION_addUserKeyAuth = 71;
    static final int TRANSACTION_benchmark = 60;
    static final int TRANSACTION_changeEncryptionPassword = 29;
    static final int TRANSACTION_clearPassword = 38;
    static final int TRANSACTION_createSecureContainer = 11;
    static final int TRANSACTION_createUserKey = 62;
    static final int TRANSACTION_decryptStorage = 27;
    static final int TRANSACTION_destroySecureContainer = 13;
    static final int TRANSACTION_destroyUserKey = 63;
    static final int TRANSACTION_destroyUserStorage = 68;
    static final int TRANSACTION_encryptStorage = 28;
    static final int TRANSACTION_encryptWipeStorage = 73;
    static final int TRANSACTION_finalizeSecureContainer = 12;
    static final int TRANSACTION_finishMediaUpdate = 21;
    static final int TRANSACTION_fixPermissionsSecureContainer = 34;
    static final int TRANSACTION_fixateNewestUserKeyAuth = 72;
    static final int TRANSACTION_forgetAllVolumes = 57;
    static final int TRANSACTION_forgetVolume = 56;
    static final int TRANSACTION_format = 50;
    static final int TRANSACTION_formatVolume = 8;
    static final int TRANSACTION_getDisks = 45;
    static final int TRANSACTION_getEncryptionState = 32;
    static final int TRANSACTION_getField = 40;
    static final int TRANSACTION_getMountedObbPath = 25;
    static final int TRANSACTION_getPassword = 37;
    static final int TRANSACTION_getPasswordType = 36;
    static final int TRANSACTION_getPrimaryStorageUuid = 58;
    static final int TRANSACTION_getSecureContainerFilesystemPath = 31;
    static final int TRANSACTION_getSecureContainerList = 19;
    static final int TRANSACTION_getSecureContainerPath = 18;
    static final int TRANSACTION_getStorageUsers = 9;
    static final int TRANSACTION_getVolumeList = 30;
    static final int TRANSACTION_getVolumeRecords = 47;
    static final int TRANSACTION_getVolumeState = 10;
    static final int TRANSACTION_getVolumes = 46;
    static final int TRANSACTION_isConvertibleToFBE = 69;
    static final int TRANSACTION_isExternalStorageEmulated = 26;
    static final int TRANSACTION_isObbMounted = 24;
    static final int TRANSACTION_isSecureContainerMounted = 16;
    static final int TRANSACTION_isUsbMassStorageConnected = 3;
    static final int TRANSACTION_isUsbMassStorageEnabled = 5;
    static final int TRANSACTION_isUserKeyUnlocked = 66;
    static final int TRANSACTION_lastMaintenance = 42;
    static final int TRANSACTION_lockUserKey = 65;
    static final int TRANSACTION_mkdirs = 35;
    static final int TRANSACTION_mount = 48;
    static final int TRANSACTION_mountAppFuse = 70;
    static final int TRANSACTION_mountObb = 22;
    static final int TRANSACTION_mountSecureContainer = 14;
    static final int TRANSACTION_mountVolume = 6;
    static final int TRANSACTION_partitionMixed = 53;
    static final int TRANSACTION_partitionPrivate = 52;
    static final int TRANSACTION_partitionPublic = 51;
    static final int TRANSACTION_prepareUserStorage = 67;
    static final int TRANSACTION_registerListener = 1;
    static final int TRANSACTION_renameSecureContainer = 17;
    static final int TRANSACTION_resizeSecureContainer = 41;
    static final int TRANSACTION_runMaintenance = 43;
    static final int TRANSACTION_setDebugFlags = 61;
    static final int TRANSACTION_setField = 39;
    static final int TRANSACTION_setPrimaryStorageUuid = 59;
    static final int TRANSACTION_setUsbMassStorageEnabled = 4;
    static final int TRANSACTION_setVolumeNickname = 54;
    static final int TRANSACTION_setVolumeUserFlags = 55;
    static final int TRANSACTION_shutdown = 20;
    static final int TRANSACTION_unlockUserKey = 64;
    static final int TRANSACTION_unmount = 49;
    static final int TRANSACTION_unmountObb = 23;
    static final int TRANSACTION_unmountSecureContainer = 15;
    static final int TRANSACTION_unmountVolume = 7;
    static final int TRANSACTION_unregisterListener = 2;
    static final int TRANSACTION_verifyEncryptionPassword = 33;
    static final int TRANSACTION_waitForAsecScan = 44;
    
    public Stub()
    {
      attachInterface(this, "IMountService");
    }
    
    public static IMountService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("IMountService");
      if ((localIInterface != null) && ((localIInterface instanceof IMountService))) {
        return (IMountService)localIInterface;
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
      String str1;
      label844:
      String str2;
      long l;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("IMountService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("IMountService");
        registerListener(IMountServiceListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("IMountService");
        unregisterListener(IMountServiceListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("IMountService");
        bool1 = isUsbMassStorageConnected();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("IMountService");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setUsbMassStorageEnabled(bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("IMountService");
        bool1 = isUsbMassStorageEnabled();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = mountVolume(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("IMountService");
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label844;
          }
        }
        for (boolean bool2 = true;; bool2 = false)
        {
          unmountVolume(str1, bool1, bool2);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 8: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = formatVolume(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getStorageUsers(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeIntArray(paramParcel1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getVolumeState(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("IMountService");
        str1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        str2 = paramParcel1.readString();
        String str3 = paramParcel1.readString();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          paramInt1 = createSecureContainer(str1, paramInt1, str2, str3, paramInt2, bool1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = finalizeSecureContainer(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("IMountService");
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          paramInt1 = destroySecureContainer(str1, bool1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("IMountService");
        str1 = paramParcel1.readString();
        str2 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          paramInt1 = mountSecureContainer(str1, str2, paramInt1, bool1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("IMountService");
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          paramInt1 = unmountSecureContainer(str1, bool1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("IMountService");
        bool1 = isSecureContainerMounted(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = renameSecureContainer(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 18: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getSecureContainerPath(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getSecureContainerList();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 20: 
        paramParcel1.enforceInterface("IMountService");
        shutdown(IMountShutdownObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("IMountService");
        finishMediaUpdate();
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("IMountService");
        mountObb(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), IObbActionListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 23: 
        paramParcel1.enforceInterface("IMountService");
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          unmountObb(str1, bool1, IObbActionListener.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("IMountService");
        bool1 = isObbMounted(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 25: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getMountedObbPath(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 26: 
        paramParcel1.enforceInterface("IMountService");
        bool1 = isExternalStorageEmulated();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 27: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = decryptStorage(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 28: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = encryptStorage(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 73: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = encryptWipeStorage(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 29: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = changeEncryptionPassword(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 30: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getVolumeList(paramParcel1.readInt(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 31: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getSecureContainerFilesystemPath(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 32: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = getEncryptionState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 34: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = fixPermissionsSecureContainer(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 35: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = mkdirs(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 36: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = getPasswordType();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 37: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getPassword();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 38: 
        paramParcel1.enforceInterface("IMountService");
        clearPassword();
        paramParcel2.writeNoException();
        return true;
      case 39: 
        paramParcel1.enforceInterface("IMountService");
        setField(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 40: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getField(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 69: 
        paramParcel1.enforceInterface("IMountService");
        if (isConvertibleToFBE()) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 41: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = resizeSecureContainer(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 42: 
        paramParcel1.enforceInterface("IMountService");
        l = lastMaintenance();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 43: 
        paramParcel1.enforceInterface("IMountService");
        runMaintenance();
        paramParcel2.writeNoException();
        return true;
      case 44: 
        paramParcel1.enforceInterface("IMountService");
        waitForAsecScan();
        paramParcel2.writeNoException();
        return true;
      case 45: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getDisks();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 46: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getVolumes(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 47: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getVolumeRecords(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 48: 
        paramParcel1.enforceInterface("IMountService");
        mount(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 49: 
        paramParcel1.enforceInterface("IMountService");
        unmount(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 50: 
        paramParcel1.enforceInterface("IMountService");
        format(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 60: 
        paramParcel1.enforceInterface("IMountService");
        l = benchmark(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 51: 
        paramParcel1.enforceInterface("IMountService");
        partitionPublic(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 52: 
        paramParcel1.enforceInterface("IMountService");
        partitionPrivate(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 53: 
        paramParcel1.enforceInterface("IMountService");
        partitionMixed(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 54: 
        paramParcel1.enforceInterface("IMountService");
        setVolumeNickname(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 55: 
        paramParcel1.enforceInterface("IMountService");
        setVolumeUserFlags(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 56: 
        paramParcel1.enforceInterface("IMountService");
        forgetVolume(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 57: 
        paramParcel1.enforceInterface("IMountService");
        forgetAllVolumes();
        paramParcel2.writeNoException();
        return true;
      case 61: 
        paramParcel1.enforceInterface("IMountService");
        setDebugFlags(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 58: 
        paramParcel1.enforceInterface("IMountService");
        paramParcel1 = getPrimaryStorageUuid();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 59: 
        paramParcel1.enforceInterface("IMountService");
        setPrimaryStorageUuid(paramParcel1.readString(), IPackageMoveObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 62: 
        paramParcel1.enforceInterface("IMountService");
        paramInt1 = paramParcel1.readInt();
        paramInt2 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          createUserKey(paramInt1, paramInt2, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 63: 
        paramParcel1.enforceInterface("IMountService");
        destroyUserKey(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 71: 
        paramParcel1.enforceInterface("IMountService");
        addUserKeyAuth(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 72: 
        paramParcel1.enforceInterface("IMountService");
        fixateNewestUserKeyAuth(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 64: 
        paramParcel1.enforceInterface("IMountService");
        unlockUserKey(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.createByteArray(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        return true;
      case 65: 
        paramParcel1.enforceInterface("IMountService");
        lockUserKey(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 66: 
        paramParcel1.enforceInterface("IMountService");
        bool1 = isUserKeyUnlocked(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 67: 
        paramParcel1.enforceInterface("IMountService");
        prepareUserStorage(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 68: 
        paramParcel1.enforceInterface("IMountService");
        destroyUserStorage(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("IMountService");
      paramParcel1 = mountAppFuse(paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeParcelable(paramParcel1, 1);
      return true;
    }
    
    private static class Proxy
      implements IMountService
    {
      private final IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addUserKeyAuth(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeByteArray(paramArrayOfByte1);
          localParcel1.writeByteArray(paramArrayOfByte2);
          this.mRemote.transact(71, localParcel1, localParcel2, 0);
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
      
      public long benchmark(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(60, localParcel1, localParcel2, 0);
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
      
      public int changeEncryptionPassword(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearPassword()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(38, localParcel1, localParcel2, 1);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int createSecureContainer(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          localParcel1.writeInt(paramInt2);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt1 = localParcel2.readInt();
          return paramInt1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void createUserKey(int paramInt1, int paramInt2, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          paramInt1 = i;
          if (paramBoolean) {
            paramInt1 = 1;
          }
          localParcel1.writeInt(paramInt1);
          this.mRemote.transact(62, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int decryptStorage(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
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
      
      public int destroySecureContainer(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void destroyUserKey(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(63, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void destroyUserStorage(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(68, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int encryptStorage(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int encryptWipeStorage(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(73, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int finalizeSecureContainer(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      
      public void finishMediaUpdate()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
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
      
      public int fixPermissionsSecureContainer(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void fixateNewestUserKeyAuth(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(72, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void forgetAllVolumes()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(57, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void forgetVolume(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(56, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void format(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(50, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int formatVolume(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
      
      public DiskInfo[] getDisks()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(45, localParcel1, localParcel2, 0);
          localParcel2.readException();
          DiskInfo[] arrayOfDiskInfo = (DiskInfo[])localParcel2.createTypedArray(DiskInfo.CREATOR);
          return arrayOfDiskInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getEncryptionState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(32, localParcel1, localParcel2, 0);
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
      
      public String getField(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(40, localParcel1, localParcel2, 0);
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
        return "IMountService";
      }
      
      public String getMountedObbPath(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
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
      
      public String getPassword()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(37, localParcel1, localParcel2, 0);
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
      
      public int getPasswordType()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(36, localParcel1, localParcel2, 0);
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
      
      public String getPrimaryStorageUuid()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(58, localParcel1, localParcel2, 0);
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
      
      public String getSecureContainerFilesystemPath(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      
      public String[] getSecureContainerList()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
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
      
      public String getSecureContainerPath(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
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
      
      public int[] getStorageUsers(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createIntArray();
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public StorageVolume[] getVolumeList(int paramInt1, String paramString, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = (StorageVolume[])localParcel2.createTypedArray(StorageVolume.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public VolumeRecord[] getVolumeRecords(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(47, localParcel1, localParcel2, 0);
          localParcel2.readException();
          VolumeRecord[] arrayOfVolumeRecord = (VolumeRecord[])localParcel2.createTypedArray(VolumeRecord.CREATOR);
          return arrayOfVolumeRecord;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getVolumeState(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public VolumeInfo[] getVolumes(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(46, localParcel1, localParcel2, 0);
          localParcel2.readException();
          VolumeInfo[] arrayOfVolumeInfo = (VolumeInfo[])localParcel2.createTypedArray(VolumeInfo.CREATOR);
          return arrayOfVolumeInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public boolean isConvertibleToFBE()
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
        //   16: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 69
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 50 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 53	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 74	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 56	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 56	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 56	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 56	android/os/Parcel:recycle	()V
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
      public boolean isExternalStorageEmulated()
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
        //   16: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 26
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 50 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 53	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 74	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 56	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 56	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 56	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 56	android/os/Parcel:recycle	()V
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
      public boolean isObbMounted(String paramString)
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
        //   20: invokevirtual 64	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 24
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 50 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 53	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 74	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 56	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 56	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 56	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 56	android/os/Parcel:recycle	()V
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
      public boolean isSecureContainerMounted(String paramString)
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
        //   20: invokevirtual 64	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 16
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 50 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 53	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 74	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 56	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 56	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 56	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 56	android/os/Parcel:recycle	()V
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
      public boolean isUsbMassStorageConnected()
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
        //   16: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_3
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 50 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 53	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 74	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 56	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 56	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 56	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 56	android/os/Parcel:recycle	()V
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
      public boolean isUsbMassStorageEnabled()
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
        //   16: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: iconst_5
        //   20: aload_3
        //   21: aload 4
        //   23: iconst_0
        //   24: invokeinterface 50 5 0
        //   29: pop
        //   30: aload 4
        //   32: invokevirtual 53	android/os/Parcel:readException	()V
        //   35: aload 4
        //   37: invokevirtual 74	android/os/Parcel:readInt	()I
        //   40: istore_1
        //   41: iload_1
        //   42: ifeq +16 -> 58
        //   45: iconst_1
        //   46: istore_2
        //   47: aload 4
        //   49: invokevirtual 56	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 56	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 56	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 56	android/os/Parcel:recycle	()V
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
      public boolean isUserKeyUnlocked(int paramInt)
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
        //   21: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 66
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 50 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 53	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 74	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 56	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 56	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 56	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 56	android/os/Parcel:recycle	()V
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
      
      public long lastMaintenance()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(42, localParcel1, localParcel2, 0);
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
      
      public void lockUserKey(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(65, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int mkdirs(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
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
      
      public void mount(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(48, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public ParcelFileDescriptor mountAppFuse(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(70, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = (ParcelFileDescriptor)localParcel2.readParcelable(ClassLoader.getSystemClassLoader());
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void mountObb(String paramString1, String paramString2, String paramString3, IObbActionListener paramIObbActionListener, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          paramString1 = (String)localObject;
          if (paramIObbActionListener != null) {
            paramString1 = paramIObbActionListener.asBinder();
          }
          localParcel1.writeStrongBinder(paramString1);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int mountSecureContainer(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int mountVolume(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
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
      
      public void partitionMixed(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(53, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void partitionPrivate(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(52, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void partitionPublic(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(51, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void prepareUserStorage(String paramString, int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
          this.mRemote.transact(67, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerListener(IMountServiceListener paramIMountServiceListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          if (paramIMountServiceListener != null) {
            localIBinder = paramIMountServiceListener.asBinder();
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
      
      public int renameSecureContainer(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
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
      
      public int resizeSecureContainer(String paramString1, int paramInt, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(41, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramInt = localParcel2.readInt();
          return paramInt;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void runMaintenance()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(43, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setDebugFlags(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(61, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setField(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(39, localParcel1, localParcel2, 1);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setPrimaryStorageUuid(String paramString, IPackageMoveObserver paramIPackageMoveObserver)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          paramString = (String)localObject;
          if (paramIPackageMoveObserver != null) {
            paramString = paramIPackageMoveObserver.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          this.mRemote.transact(59, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setUsbMassStorageEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setVolumeNickname(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(54, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setVolumeUserFlags(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(55, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void shutdown(IMountShutdownObserver paramIMountShutdownObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          if (paramIMountShutdownObserver != null) {
            localIBinder = paramIMountShutdownObserver.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public void unlockUserKey(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeByteArray(paramArrayOfByte1);
          localParcel1.writeByteArray(paramArrayOfByte2);
          this.mRemote.transact(64, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unmount(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(49, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unmountObb(String paramString, boolean paramBoolean, IObbActionListener paramIObbActionListener, int paramInt)
        throws RemoteException
      {
        Object localObject = null;
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          paramString = (String)localObject;
          if (paramIObbActionListener != null) {
            paramString = paramIObbActionListener.asBinder();
          }
          localParcel1.writeStrongBinder(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(23, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int unmountSecureContainer(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void unmountVolume(String paramString, boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 6
        //   22: aload_1
        //   23: invokevirtual 64	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: iload_2
        //   27: ifeq +61 -> 88
        //   30: iconst_1
        //   31: istore 4
        //   33: aload 6
        //   35: iload 4
        //   37: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   40: iload_3
        //   41: ifeq +53 -> 94
        //   44: iload 5
        //   46: istore 4
        //   48: aload 6
        //   50: iload 4
        //   52: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   55: aload_0
        //   56: getfield 19	android/os/storage/IMountService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   59: bipush 7
        //   61: aload 6
        //   63: aload 7
        //   65: iconst_0
        //   66: invokeinterface 50 5 0
        //   71: pop
        //   72: aload 7
        //   74: invokevirtual 53	android/os/Parcel:readException	()V
        //   77: aload 7
        //   79: invokevirtual 56	android/os/Parcel:recycle	()V
        //   82: aload 6
        //   84: invokevirtual 56	android/os/Parcel:recycle	()V
        //   87: return
        //   88: iconst_0
        //   89: istore 4
        //   91: goto -58 -> 33
        //   94: iconst_0
        //   95: istore 4
        //   97: goto -49 -> 48
        //   100: astore_1
        //   101: aload 7
        //   103: invokevirtual 56	android/os/Parcel:recycle	()V
        //   106: aload 6
        //   108: invokevirtual 56	android/os/Parcel:recycle	()V
        //   111: aload_1
        //   112: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	113	0	this	Proxy
        //   0	113	1	paramString	String
        //   0	113	2	paramBoolean1	boolean
        //   0	113	3	paramBoolean2	boolean
        //   31	65	4	i	int
        //   1	44	5	j	int
        //   6	101	6	localParcel1	Parcel
        //   11	91	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	26	100	finally
        //   33	40	100	finally
        //   48	77	100	finally
      }
      
      public void unregisterListener(IMountServiceListener paramIMountServiceListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          if (paramIMountServiceListener != null) {
            localIBinder = paramIMountServiceListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
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
      
      public int verifyEncryptionPassword(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
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
      
      public void waitForAsecScan()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("IMountService");
          this.mRemote.transact(44, localParcel1, localParcel2, 0);
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/storage/IMountService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */