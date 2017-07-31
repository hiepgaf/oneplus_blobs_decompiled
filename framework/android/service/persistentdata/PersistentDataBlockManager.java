package android.service.persistentdata;

import android.os.RemoteException;

public class PersistentDataBlockManager
{
  public static final int FLASH_LOCK_LOCKED = 1;
  public static final int FLASH_LOCK_UNKNOWN = -1;
  public static final int FLASH_LOCK_UNLOCKED = 0;
  private static final String TAG = PersistentDataBlockManager.class.getSimpleName();
  private IPersistentDataBlockService sService;
  
  public PersistentDataBlockManager(IPersistentDataBlockService paramIPersistentDataBlockService)
  {
    this.sService = paramIPersistentDataBlockService;
  }
  
  public int getDataBlockSize()
  {
    try
    {
      int i = this.sService.getDataBlockSize();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getFlashLockState()
  {
    try
    {
      int i = this.sService.getFlashLockState();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public long getMaximumDataBlockSize()
  {
    try
    {
      long l = this.sService.getMaximumDataBlockSize();
      return l;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean getOemUnlockEnabled()
  {
    try
    {
      boolean bool = this.sService.getOemUnlockEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public byte[] read()
  {
    try
    {
      byte[] arrayOfByte = this.sService.read();
      return arrayOfByte;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setOemUnlockEnabled(boolean paramBoolean)
  {
    try
    {
      this.sService.setOemUnlockEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void wipe()
  {
    try
    {
      this.sService.wipe();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int write(byte[] paramArrayOfByte)
  {
    try
    {
      int i = this.sService.write(paramArrayOfByte);
      return i;
    }
    catch (RemoteException paramArrayOfByte)
    {
      throw paramArrayOfByte.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/persistentdata/PersistentDataBlockManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */