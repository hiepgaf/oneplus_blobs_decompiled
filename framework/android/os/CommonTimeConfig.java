package android.os;

import java.net.InetSocketAddress;
import java.util.NoSuchElementException;

public class CommonTimeConfig
{
  public static final int ERROR = -1;
  public static final int ERROR_BAD_VALUE = -4;
  public static final int ERROR_DEAD_OBJECT = -7;
  public static final long INVALID_GROUP_ID = -1L;
  private static final int METHOD_FORCE_NETWORKLESS_MASTER_MODE = 17;
  private static final int METHOD_GET_AUTO_DISABLE = 15;
  private static final int METHOD_GET_CLIENT_SYNC_INTERVAL = 11;
  private static final int METHOD_GET_INTERFACE_BINDING = 7;
  private static final int METHOD_GET_MASTER_ANNOUNCE_INTERVAL = 9;
  private static final int METHOD_GET_MASTER_ELECTION_ENDPOINT = 3;
  private static final int METHOD_GET_MASTER_ELECTION_GROUP_ID = 5;
  private static final int METHOD_GET_MASTER_ELECTION_PRIORITY = 1;
  private static final int METHOD_GET_PANIC_THRESHOLD = 13;
  private static final int METHOD_SET_AUTO_DISABLE = 16;
  private static final int METHOD_SET_CLIENT_SYNC_INTERVAL = 12;
  private static final int METHOD_SET_INTERFACE_BINDING = 8;
  private static final int METHOD_SET_MASTER_ANNOUNCE_INTERVAL = 10;
  private static final int METHOD_SET_MASTER_ELECTION_ENDPOINT = 4;
  private static final int METHOD_SET_MASTER_ELECTION_GROUP_ID = 6;
  private static final int METHOD_SET_MASTER_ELECTION_PRIORITY = 2;
  private static final int METHOD_SET_PANIC_THRESHOLD = 14;
  public static final String SERVICE_NAME = "common_time.config";
  public static final int SUCCESS = 0;
  private IBinder.DeathRecipient mDeathHandler = new IBinder.DeathRecipient()
  {
    public void binderDied()
    {
      synchronized (CommonTimeConfig.-get0(CommonTimeConfig.this))
      {
        if (CommonTimeConfig.-get1(CommonTimeConfig.this) != null) {
          CommonTimeConfig.-get1(CommonTimeConfig.this).onServerDied();
        }
        return;
      }
    }
  };
  private String mInterfaceDesc = "";
  private final Object mListenerLock = new Object();
  private IBinder mRemote = null;
  private OnServerDiedListener mServerDiedListener = null;
  private CommonTimeUtils mUtils;
  
  public CommonTimeConfig()
    throws RemoteException
  {
    if (this.mRemote == null) {
      throw new RemoteException();
    }
    this.mInterfaceDesc = this.mRemote.getInterfaceDescriptor();
    this.mUtils = new CommonTimeUtils(this.mRemote, this.mInterfaceDesc);
    this.mRemote.linkToDeath(this.mDeathHandler, 0);
  }
  
  private boolean checkDeadServer()
  {
    return (this.mRemote == null) || (this.mUtils == null);
  }
  
  public static CommonTimeConfig create()
  {
    try
    {
      CommonTimeConfig localCommonTimeConfig = new CommonTimeConfig();
      return localCommonTimeConfig;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  private void throwOnDeadServer()
    throws RemoteException
  {
    if (checkDeadServer()) {
      throw new RemoteException();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    release();
  }
  
  public int forceNetworklessMasterMode()
  {
    Parcel localParcel1 = Parcel.obtain();
    Parcel localParcel2 = Parcel.obtain();
    try
    {
      localParcel1.writeInterfaceToken(this.mInterfaceDesc);
      this.mRemote.transact(17, localParcel1, localParcel2, 0);
      int i = localParcel2.readInt();
      localParcel2.recycle();
      localParcel1.recycle();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      localRemoteException = localRemoteException;
      localParcel2.recycle();
      localParcel1.recycle();
      return -7;
    }
    finally
    {
      localObject = finally;
      localParcel2.recycle();
      localParcel1.recycle();
      throw ((Throwable)localObject);
    }
  }
  
  public boolean getAutoDisable()
    throws RemoteException
  {
    throwOnDeadServer();
    return 1 == this.mUtils.transactGetInt(15, 1);
  }
  
  public int getClientSyncInterval()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetInt(11, -1);
  }
  
  public String getInterfaceBinding()
    throws RemoteException
  {
    throwOnDeadServer();
    String str = this.mUtils.transactGetString(7, null);
    if ((str != null) && (str.length() == 0)) {
      return null;
    }
    return str;
  }
  
  public int getMasterAnnounceInterval()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetInt(9, -1);
  }
  
  public InetSocketAddress getMasterElectionEndpoint()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetSockaddr(3);
  }
  
  public long getMasterElectionGroupId()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetLong(5, -1L);
  }
  
  public byte getMasterElectionPriority()
    throws RemoteException
  {
    throwOnDeadServer();
    return (byte)this.mUtils.transactGetInt(1, -1);
  }
  
  public int getPanicThreshold()
    throws RemoteException
  {
    throwOnDeadServer();
    return this.mUtils.transactGetInt(13, -1);
  }
  
  public void release()
  {
    if (this.mRemote != null) {}
    try
    {
      this.mRemote.unlinkToDeath(this.mDeathHandler, 0);
      this.mRemote = null;
      this.mUtils = null;
      return;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      for (;;) {}
    }
  }
  
  public int setAutoDisable(boolean paramBoolean)
  {
    if (checkDeadServer()) {
      return -7;
    }
    CommonTimeUtils localCommonTimeUtils = this.mUtils;
    if (paramBoolean) {}
    for (int i = 1;; i = 0) {
      return localCommonTimeUtils.transactSetInt(16, i);
    }
  }
  
  public int setClientSyncInterval(int paramInt)
  {
    if (checkDeadServer()) {
      return -7;
    }
    return this.mUtils.transactSetInt(12, paramInt);
  }
  
  public int setMasterAnnounceInterval(int paramInt)
  {
    if (checkDeadServer()) {
      return -7;
    }
    return this.mUtils.transactSetInt(10, paramInt);
  }
  
  public int setMasterElectionEndpoint(InetSocketAddress paramInetSocketAddress)
  {
    if (checkDeadServer()) {
      return -7;
    }
    return this.mUtils.transactSetSockaddr(4, paramInetSocketAddress);
  }
  
  public int setMasterElectionGroupId(long paramLong)
  {
    if (checkDeadServer()) {
      return -7;
    }
    return this.mUtils.transactSetLong(6, paramLong);
  }
  
  public int setMasterElectionPriority(byte paramByte)
  {
    if (checkDeadServer()) {
      return -7;
    }
    return this.mUtils.transactSetInt(2, paramByte);
  }
  
  public int setNetworkBinding(String paramString)
  {
    if (checkDeadServer()) {
      return -7;
    }
    CommonTimeUtils localCommonTimeUtils = this.mUtils;
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    return localCommonTimeUtils.transactSetString(8, str);
  }
  
  public int setPanicThreshold(int paramInt)
  {
    if (checkDeadServer()) {
      return -7;
    }
    return this.mUtils.transactSetInt(14, paramInt);
  }
  
  public void setServerDiedListener(OnServerDiedListener paramOnServerDiedListener)
  {
    synchronized (this.mListenerLock)
    {
      this.mServerDiedListener = paramOnServerDiedListener;
      return;
    }
  }
  
  public static abstract interface OnServerDiedListener
  {
    public abstract void onServerDied();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/CommonTimeConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */