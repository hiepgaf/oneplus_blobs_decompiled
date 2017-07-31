package android.net.wifi;

import android.net.DhcpInfo;
import android.net.Network;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.WorkSource;
import java.util.ArrayList;
import java.util.List;

public abstract interface IWifiManager
  extends IInterface
{
  public abstract void acquireMulticastLock(IBinder paramIBinder, String paramString)
    throws RemoteException;
  
  public abstract boolean acquireWifiLock(IBinder paramIBinder, int paramInt, String paramString, WorkSource paramWorkSource)
    throws RemoteException;
  
  public abstract int addOrUpdateNetwork(WifiConfiguration paramWifiConfiguration)
    throws RemoteException;
  
  public abstract int addPasspointManagementObject(String paramString)
    throws RemoteException;
  
  public abstract void addToBlacklist(String paramString)
    throws RemoteException;
  
  public abstract WifiConfiguration buildWifiConfig(String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void clearBlacklist()
    throws RemoteException;
  
  public abstract void deauthenticateNetwork(long paramLong, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void disableEphemeralNetwork(String paramString)
    throws RemoteException;
  
  public abstract boolean disableNetwork(int paramInt)
    throws RemoteException;
  
  public abstract void disconnect()
    throws RemoteException;
  
  public abstract void enableAggressiveHandover(int paramInt)
    throws RemoteException;
  
  public abstract boolean enableNetwork(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableTdls(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableTdlsWithMacAddress(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void enableVerboseLogging(int paramInt)
    throws RemoteException;
  
  public abstract void enableWifiConnectivityManager(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void factoryReset()
    throws RemoteException;
  
  public abstract int getAggressiveHandover()
    throws RemoteException;
  
  public abstract int getAllowScansWithTraffic()
    throws RemoteException;
  
  public abstract String getConfigFile()
    throws RemoteException;
  
  public abstract List<WifiConfiguration> getConfiguredNetworks()
    throws RemoteException;
  
  public abstract WifiInfo getConnectionInfo()
    throws RemoteException;
  
  public abstract WifiConnectionStatistics getConnectionStatistics()
    throws RemoteException;
  
  public abstract String getCountryCode()
    throws RemoteException;
  
  public abstract Network getCurrentNetwork()
    throws RemoteException;
  
  public abstract DhcpInfo getDhcpInfo()
    throws RemoteException;
  
  public abstract boolean getEnableAutoJoinWhenAssociated()
    throws RemoteException;
  
  public abstract int getFrequencyBand()
    throws RemoteException;
  
  public abstract WifiConfiguration getMatchingWifiConfig(ScanResult paramScanResult)
    throws RemoteException;
  
  public abstract List<WifiConfiguration> getPrivilegedConfiguredNetworks()
    throws RemoteException;
  
  public abstract List<ScanResult> getScanResults(String paramString)
    throws RemoteException;
  
  public abstract String getSoftApInterfaceName()
    throws RemoteException;
  
  public abstract int getSupportedFeatures()
    throws RemoteException;
  
  public abstract int getVerboseLoggingLevel()
    throws RemoteException;
  
  public abstract WifiConfiguration getWifiApConfiguration()
    throws RemoteException;
  
  public abstract int getWifiApEnabledState()
    throws RemoteException;
  
  public abstract int getWifiEnabledState()
    throws RemoteException;
  
  public abstract Messenger getWifiServiceMessenger()
    throws RemoteException;
  
  public abstract boolean getWifiStaSapConcurrency()
    throws RemoteException;
  
  public abstract String getWpsNfcConfigurationToken(int paramInt)
    throws RemoteException;
  
  public abstract void initializeMulticastFiltering()
    throws RemoteException;
  
  public abstract boolean isDualBandSupported()
    throws RemoteException;
  
  public abstract boolean isMulticastEnabled()
    throws RemoteException;
  
  public abstract boolean isScanAlwaysAvailable()
    throws RemoteException;
  
  public abstract boolean loadFtmDriver()
    throws RemoteException;
  
  public abstract int matchProviderWithCurrentNetwork(String paramString)
    throws RemoteException;
  
  public abstract int modifyPasspointManagementObject(String paramString, List<PasspointManagementObjectDefinition> paramList)
    throws RemoteException;
  
  public abstract boolean pingSupplicant()
    throws RemoteException;
  
  public abstract void queryPasspointIcon(long paramLong, String paramString)
    throws RemoteException;
  
  public abstract void reassociate()
    throws RemoteException;
  
  public abstract void reconnect()
    throws RemoteException;
  
  public abstract void releaseMulticastLock()
    throws RemoteException;
  
  public abstract boolean releaseWifiLock(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean removeNetwork(int paramInt)
    throws RemoteException;
  
  public abstract WifiActivityEnergyInfo reportActivityInfo()
    throws RemoteException;
  
  public abstract void requestActivityInfo(ResultReceiver paramResultReceiver)
    throws RemoteException;
  
  public abstract boolean requestRunningP2p()
    throws RemoteException;
  
  public abstract boolean requestRunningSoftap()
    throws RemoteException;
  
  public abstract boolean saveConfiguration()
    throws RemoteException;
  
  public abstract void setAllowScansWithTraffic(int paramInt)
    throws RemoteException;
  
  public abstract void setCountryCode(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setEnableAutoJoinWhenAssociated(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setFrequencyBand(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setWifiApConfiguration(WifiConfiguration paramWifiConfiguration)
    throws RemoteException;
  
  public abstract void setWifiApEnabled(WifiConfiguration paramWifiConfiguration, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setWifiEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setWifiStaSapConcurrencyEnabled(int paramInt)
    throws RemoteException;
  
  public abstract void startScan(ScanSettings paramScanSettings, WorkSource paramWorkSource)
    throws RemoteException;
  
  public abstract boolean unloadFtmDriver()
    throws RemoteException;
  
  public abstract void updateWifiLockWorkSource(IBinder paramIBinder, WorkSource paramWorkSource)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IWifiManager
  {
    private static final String DESCRIPTOR = "android.net.wifi.IWifiManager";
    static final int TRANSACTION_acquireMulticastLock = 38;
    static final int TRANSACTION_acquireWifiLock = 33;
    static final int TRANSACTION_addOrUpdateNetwork = 7;
    static final int TRANSACTION_addPasspointManagementObject = 8;
    static final int TRANSACTION_addToBlacklist = 45;
    static final int TRANSACTION_buildWifiConfig = 43;
    static final int TRANSACTION_clearBlacklist = 46;
    static final int TRANSACTION_deauthenticateNetwork = 12;
    static final int TRANSACTION_disableEphemeralNetwork = 62;
    static final int TRANSACTION_disableNetwork = 15;
    static final int TRANSACTION_disconnect = 19;
    static final int TRANSACTION_enableAggressiveHandover = 54;
    static final int TRANSACTION_enableNetwork = 14;
    static final int TRANSACTION_enableTdls = 49;
    static final int TRANSACTION_enableTdlsWithMacAddress = 50;
    static final int TRANSACTION_enableVerboseLogging = 52;
    static final int TRANSACTION_enableWifiConnectivityManager = 60;
    static final int TRANSACTION_factoryReset = 63;
    static final int TRANSACTION_getAggressiveHandover = 55;
    static final int TRANSACTION_getAllowScansWithTraffic = 57;
    static final int TRANSACTION_getConfigFile = 48;
    static final int TRANSACTION_getConfiguredNetworks = 4;
    static final int TRANSACTION_getConnectionInfo = 22;
    static final int TRANSACTION_getConnectionStatistics = 61;
    static final int TRANSACTION_getCountryCode = 26;
    static final int TRANSACTION_getCurrentNetwork = 64;
    static final int TRANSACTION_getDhcpInfo = 31;
    static final int TRANSACTION_getEnableAutoJoinWhenAssociated = 59;
    static final int TRANSACTION_getFrequencyBand = 28;
    static final int TRANSACTION_getMatchingWifiConfig = 6;
    static final int TRANSACTION_getPrivilegedConfiguredNetworks = 5;
    static final int TRANSACTION_getScanResults = 18;
    static final int TRANSACTION_getSoftApInterfaceName = 69;
    static final int TRANSACTION_getSupportedFeatures = 1;
    static final int TRANSACTION_getVerboseLoggingLevel = 53;
    static final int TRANSACTION_getWifiApConfiguration = 42;
    static final int TRANSACTION_getWifiApEnabledState = 41;
    static final int TRANSACTION_getWifiEnabledState = 24;
    static final int TRANSACTION_getWifiServiceMessenger = 47;
    static final int TRANSACTION_getWifiStaSapConcurrency = 65;
    static final int TRANSACTION_getWpsNfcConfigurationToken = 51;
    static final int TRANSACTION_initializeMulticastFiltering = 36;
    static final int TRANSACTION_isDualBandSupported = 29;
    static final int TRANSACTION_isMulticastEnabled = 37;
    static final int TRANSACTION_isScanAlwaysAvailable = 32;
    static final int TRANSACTION_loadFtmDriver = 66;
    static final int TRANSACTION_matchProviderWithCurrentNetwork = 11;
    static final int TRANSACTION_modifyPasspointManagementObject = 9;
    static final int TRANSACTION_pingSupplicant = 16;
    static final int TRANSACTION_queryPasspointIcon = 10;
    static final int TRANSACTION_reassociate = 21;
    static final int TRANSACTION_reconnect = 20;
    static final int TRANSACTION_releaseMulticastLock = 39;
    static final int TRANSACTION_releaseWifiLock = 35;
    static final int TRANSACTION_removeNetwork = 13;
    static final int TRANSACTION_reportActivityInfo = 2;
    static final int TRANSACTION_requestActivityInfo = 3;
    static final int TRANSACTION_requestRunningP2p = 71;
    static final int TRANSACTION_requestRunningSoftap = 70;
    static final int TRANSACTION_saveConfiguration = 30;
    static final int TRANSACTION_setAllowScansWithTraffic = 56;
    static final int TRANSACTION_setCountryCode = 25;
    static final int TRANSACTION_setEnableAutoJoinWhenAssociated = 58;
    static final int TRANSACTION_setFrequencyBand = 27;
    static final int TRANSACTION_setWifiApConfiguration = 44;
    static final int TRANSACTION_setWifiApEnabled = 40;
    static final int TRANSACTION_setWifiEnabled = 23;
    static final int TRANSACTION_setWifiStaSapConcurrencyEnabled = 68;
    static final int TRANSACTION_startScan = 17;
    static final int TRANSACTION_unloadFtmDriver = 67;
    static final int TRANSACTION_updateWifiLockWorkSource = 34;
    
    public Stub()
    {
      attachInterface(this, "android.net.wifi.IWifiManager");
    }
    
    public static IWifiManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.wifi.IWifiManager");
      if ((localIInterface != null) && ((localIInterface instanceof IWifiManager))) {
        return (IWifiManager)localIInterface;
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
      label808:
      label1108:
      Object localObject;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.wifi.IWifiManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = getSupportedFeatures();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = reportActivityInfo();
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
      case 3: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestActivityInfo(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getConfiguredNetworks();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getPrivilegedConfiguredNetworks();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (ScanResult)ScanResult.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getMatchingWifiConfig(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label808;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          paramParcel1 = null;
          break;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (WifiConfiguration)WifiConfiguration.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = addOrUpdateNetwork(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 8: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = addPasspointManagementObject(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = modifyPasspointManagementObject(paramParcel1.readString(), paramParcel1.createTypedArrayList(PasspointManagementObjectDefinition.CREATOR));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        queryPasspointIcon(paramParcel1.readLong(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = matchProviderWithCurrentNetwork(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        long l = paramParcel1.readLong();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          deauthenticateNetwork(l, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 13: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = removeNetwork(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = enableNetwork(paramInt1, bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1108;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 15: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = disableNetwork(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = pingSupplicant();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 17: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (ScanSettings)ScanSettings.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1253;
          }
        }
        for (paramParcel1 = (WorkSource)WorkSource.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startScan((ScanSettings)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 18: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getScanResults(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 19: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        disconnect();
        paramParcel2.writeNoException();
        return true;
      case 20: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        reconnect();
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        reassociate();
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getConnectionInfo();
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
      case 23: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = setWifiEnabled(bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1420;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 24: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = getWifiEnabledState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setCountryCode((String)localObject, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 26: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getCountryCode();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          setFrequencyBand(paramInt1, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 28: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = getFrequencyBand();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = isDualBandSupported();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 30: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = saveConfiguration();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 31: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getDhcpInfo();
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
      case 32: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = isScanAlwaysAvailable();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 33: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        localObject = paramParcel1.readStrongBinder();
        paramInt1 = paramParcel1.readInt();
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (WorkSource)WorkSource.CREATOR.createFromParcel(paramParcel1);
          bool = acquireWifiLock((IBinder)localObject, paramInt1, str, paramParcel1);
          paramParcel2.writeNoException();
          if (!bool) {
            break label1796;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 34: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        localObject = paramParcel1.readStrongBinder();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (WorkSource)WorkSource.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          updateWifiLockWorkSource((IBinder)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 35: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = releaseWifiLock(paramParcel1.readStrongBinder());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 36: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        initializeMulticastFiltering();
        paramParcel2.writeNoException();
        return true;
      case 37: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = isMulticastEnabled();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 38: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        acquireMulticastLock(paramParcel1.readStrongBinder(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 39: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        releaseMulticastLock();
        paramParcel2.writeNoException();
        return true;
      case 40: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (WifiConfiguration)WifiConfiguration.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2038;
          }
        }
        for (bool = true;; bool = false)
        {
          setWifiApEnabled((WifiConfiguration)localObject, bool);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 41: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = getWifiApEnabledState();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 42: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getWifiApConfiguration();
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
      case 43: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = buildWifiConfig(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.createByteArray());
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
      case 44: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (WifiConfiguration)WifiConfiguration.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setWifiApConfiguration(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 45: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        addToBlacklist(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 46: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        clearBlacklist();
        paramParcel2.writeNoException();
        return true;
      case 47: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getWifiServiceMessenger();
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
      case 48: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getConfigFile();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 49: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          enableTdls((String)localObject, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 50: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          enableTdlsWithMacAddress((String)localObject, bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 51: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getWpsNfcConfigurationToken(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 52: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        enableVerboseLogging(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 53: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = getVerboseLoggingLevel();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 54: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        enableAggressiveHandover(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 55: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = getAggressiveHandover();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 56: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        setAllowScansWithTraffic(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 57: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramInt1 = getAllowScansWithTraffic();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 58: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0)
        {
          bool = true;
          bool = setEnableAutoJoinWhenAssociated(bool);
          paramParcel2.writeNoException();
          if (!bool) {
            break label2582;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool = false;
          break;
        }
      case 59: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = getEnableAutoJoinWhenAssociated();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 60: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          enableWifiConnectivityManager(bool);
          paramParcel2.writeNoException();
          return true;
        }
      case 61: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getConnectionStatistics();
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
      case 62: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        disableEphemeralNetwork(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 63: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        factoryReset();
        paramParcel2.writeNoException();
        return true;
      case 64: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getCurrentNetwork();
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
      case 65: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = getWifiStaSapConcurrency();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 66: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = loadFtmDriver();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 67: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = unloadFtmDriver();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 68: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = setWifiStaSapConcurrencyEnabled(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 69: 
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        paramParcel1 = getSoftApInterfaceName();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 70: 
        label1253:
        label1420:
        label1796:
        label2038:
        label2582:
        paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
        bool = requestRunningSoftap();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      }
      paramParcel1.enforceInterface("android.net.wifi.IWifiManager");
      boolean bool = requestRunningP2p();
      paramParcel2.writeNoException();
      if (bool) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
      }
    }
    
    private static class Proxy
      implements IWifiManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void acquireMulticastLock(IBinder paramIBinder, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeStrongBinder(paramIBinder);
          localParcel1.writeString(paramString);
          this.mRemote.transact(38, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean acquireWifiLock(IBinder paramIBinder, int paramInt, String paramString, WorkSource paramWorkSource)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
            localParcel1.writeStrongBinder(paramIBinder);
            localParcel1.writeInt(paramInt);
            localParcel1.writeString(paramString);
            if (paramWorkSource != null)
            {
              localParcel1.writeInt(1);
              paramWorkSource.writeToParcel(localParcel1, 0);
              this.mRemote.transact(33, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public int addOrUpdateNetwork(WifiConfiguration paramWifiConfiguration)
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
        //   16: ifnull +52 -> 68
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 76	android/net/wifi/WifiConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_0
        //   31: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 7
        //   36: aload_3
        //   37: aload 4
        //   39: iconst_0
        //   40: invokeinterface 48 5 0
        //   45: pop
        //   46: aload 4
        //   48: invokevirtual 51	android/os/Parcel:readException	()V
        //   51: aload 4
        //   53: invokevirtual 71	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: aload 4
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 54	android/os/Parcel:recycle	()V
        //   66: iload_2
        //   67: ireturn
        //   68: aload_3
        //   69: iconst_0
        //   70: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   73: goto -43 -> 30
        //   76: astore_1
        //   77: aload 4
        //   79: invokevirtual 54	android/os/Parcel:recycle	()V
        //   82: aload_3
        //   83: invokevirtual 54	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramWifiConfiguration	WifiConfiguration
        //   56	11	2	i	int
        //   3	80	3	localParcel1	Parcel
        //   7	71	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	76	finally
        //   19	30	76	finally
        //   30	57	76	finally
        //   68	73	76	finally
      }
      
      public int addPasspointManagementObject(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
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
      
      public void addToBlacklist(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(45, localParcel1, localParcel2, 0);
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
      public WifiConfiguration buildWifiConfig(String paramString1, String paramString2, byte[] paramArrayOfByte)
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
        //   20: invokevirtual 42	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 42	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 4
        //   31: aload_3
        //   32: invokevirtual 87	android/os/Parcel:writeByteArray	([B)V
        //   35: aload_0
        //   36: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 43
        //   41: aload 4
        //   43: aload 5
        //   45: iconst_0
        //   46: invokeinterface 48 5 0
        //   51: pop
        //   52: aload 5
        //   54: invokevirtual 51	android/os/Parcel:readException	()V
        //   57: aload 5
        //   59: invokevirtual 71	android/os/Parcel:readInt	()I
        //   62: ifeq +29 -> 91
        //   65: getstatic 91	android/net/wifi/WifiConfiguration:CREATOR	Landroid/os/Parcelable$Creator;
        //   68: aload 5
        //   70: invokeinterface 97 2 0
        //   75: checkcast 75	android/net/wifi/WifiConfiguration
        //   78: astore_1
        //   79: aload 5
        //   81: invokevirtual 54	android/os/Parcel:recycle	()V
        //   84: aload 4
        //   86: invokevirtual 54	android/os/Parcel:recycle	()V
        //   89: aload_1
        //   90: areturn
        //   91: aconst_null
        //   92: astore_1
        //   93: goto -14 -> 79
        //   96: astore_1
        //   97: aload 5
        //   99: invokevirtual 54	android/os/Parcel:recycle	()V
        //   102: aload 4
        //   104: invokevirtual 54	android/os/Parcel:recycle	()V
        //   107: aload_1
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramString1	String
        //   0	109	2	paramString2	String
        //   0	109	3	paramArrayOfByte	byte[]
        //   3	100	4	localParcel1	Parcel
        //   8	90	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	79	96	finally
      }
      
      public void clearBlacklist()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(46, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void deauthenticateNetwork(long paramLong, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeLong(paramLong);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void disableEphemeralNetwork(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
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
      
      /* Error */
      public boolean disableNetwork(int paramInt)
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
        //   17: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 15
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 54	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 54	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public void disconnect()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(19, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void enableAggressiveHandover(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeInt(paramInt);
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
      
      /* Error */
      public boolean enableNetwork(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 4
        //   21: iload_1
        //   22: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   25: iload_3
        //   26: istore_1
        //   27: iload_2
        //   28: ifeq +5 -> 33
        //   31: iconst_1
        //   32: istore_1
        //   33: aload 4
        //   35: iload_1
        //   36: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   39: aload_0
        //   40: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: bipush 14
        //   45: aload 4
        //   47: aload 5
        //   49: iconst_0
        //   50: invokeinterface 48 5 0
        //   55: pop
        //   56: aload 5
        //   58: invokevirtual 51	android/os/Parcel:readException	()V
        //   61: aload 5
        //   63: invokevirtual 71	android/os/Parcel:readInt	()I
        //   66: istore_1
        //   67: iload_1
        //   68: ifeq +17 -> 85
        //   71: iconst_1
        //   72: istore_2
        //   73: aload 5
        //   75: invokevirtual 54	android/os/Parcel:recycle	()V
        //   78: aload 4
        //   80: invokevirtual 54	android/os/Parcel:recycle	()V
        //   83: iload_2
        //   84: ireturn
        //   85: iconst_0
        //   86: istore_2
        //   87: goto -14 -> 73
        //   90: astore 6
        //   92: aload 5
        //   94: invokevirtual 54	android/os/Parcel:recycle	()V
        //   97: aload 4
        //   99: invokevirtual 54	android/os/Parcel:recycle	()V
        //   102: aload 6
        //   104: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	105	0	this	Proxy
        //   0	105	1	paramInt	int
        //   0	105	2	paramBoolean	boolean
        //   1	25	3	i	int
        //   5	93	4	localParcel1	Parcel
        //   10	83	5	localParcel2	Parcel
        //   90	13	6	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   12	25	90	finally
        //   33	67	90	finally
      }
      
      public void enableTdls(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void enableTdlsWithMacAddress(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void enableVerboseLogging(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeInt(paramInt);
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
      
      public void enableWifiConnectivityManager(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(60, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void factoryReset()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
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
      
      public int getAggressiveHandover()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(55, localParcel1, localParcel2, 0);
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
      
      public int getAllowScansWithTraffic()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(57, localParcel1, localParcel2, 0);
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
      
      public String getConfigFile()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(48, localParcel1, localParcel2, 0);
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
      
      public List<WifiConfiguration> getConfiguredNetworks()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(WifiConfiguration.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public WifiInfo getConnectionInfo()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 22
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 138	android/net/wifi/WifiInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 97 2 0
        //   49: checkcast 137	android/net/wifi/WifiInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 54	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 54	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localWifiInfo	WifiInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public WifiConnectionStatistics getConnectionStatistics()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 61
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 143	android/net/wifi/WifiConnectionStatistics:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 97 2 0
        //   49: checkcast 142	android/net/wifi/WifiConnectionStatistics
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 54	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 54	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localWifiConnectionStatistics	WifiConnectionStatistics
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getCountryCode()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
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
      public Network getCurrentNetwork()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 64
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 149	android/net/Network:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 97 2 0
        //   49: checkcast 148	android/net/Network
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 54	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 54	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localNetwork	Network
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public DhcpInfo getDhcpInfo()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 31
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 154	android/net/DhcpInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 97 2 0
        //   49: checkcast 153	android/net/DhcpInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 54	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 54	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localDhcpInfo	DhcpInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public boolean getEnableAutoJoinWhenAssociated()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 59
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public int getFrequencyBand()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
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
      
      public String getInterfaceDescriptor()
      {
        return "android.net.wifi.IWifiManager";
      }
      
      public WifiConfiguration getMatchingWifiConfig(ScanResult paramScanResult)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
            if (paramScanResult != null)
            {
              localParcel1.writeInt(1);
              paramScanResult.writeToParcel(localParcel1, 0);
              this.mRemote.transact(6, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramScanResult = (WifiConfiguration)WifiConfiguration.CREATOR.createFromParcel(localParcel2);
                return paramScanResult;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramScanResult = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public List<WifiConfiguration> getPrivilegedConfiguredNetworks()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(WifiConfiguration.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<ScanResult> getScanResults(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(18, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = localParcel2.createTypedArrayList(ScanResult.CREATOR);
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getSoftApInterfaceName()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(69, localParcel1, localParcel2, 0);
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
      
      public int getSupportedFeatures()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public int getVerboseLoggingLevel()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(53, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public WifiConfiguration getWifiApConfiguration()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 42
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 91	android/net/wifi/WifiConfiguration:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 97 2 0
        //   49: checkcast 75	android/net/wifi/WifiConfiguration
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 54	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 54	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localWifiConfiguration	WifiConfiguration
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public int getWifiApEnabledState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(41, localParcel1, localParcel2, 0);
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
      
      public int getWifiEnabledState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public Messenger getWifiServiceMessenger()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 47
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 48 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 51	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 71	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 180	android/os/Messenger:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 97 2 0
        //   49: checkcast 179	android/os/Messenger
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 54	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 54	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localMessenger	Messenger
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public boolean getWifiStaSapConcurrency()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 65
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public String getWpsNfcConfigurationToken(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(51, localParcel1, localParcel2, 0);
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
      
      public void initializeMulticastFiltering()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(36, localParcel1, localParcel2, 0);
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
      public boolean isDualBandSupported()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 29
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      public boolean isMulticastEnabled()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 37
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      public boolean isScanAlwaysAvailable()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 32
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      public boolean loadFtmDriver()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 66
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public int matchProviderWithCurrentNetwork(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
      
      public int modifyPasspointManagementObject(String paramString, List<PasspointManagementObjectDefinition> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
          localParcel1.writeTypedList(paramList);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public boolean pingSupplicant()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 16
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public void queryPasspointIcon(long paramLong, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeLong(paramLong);
          localParcel1.writeString(paramString);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void reassociate()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
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
      
      public void reconnect()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
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
      
      public void releaseMulticastLock()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          this.mRemote.transact(39, localParcel1, localParcel2, 0);
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
      public boolean releaseWifiLock(IBinder paramIBinder)
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
        //   20: invokevirtual 39	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   23: aload_0
        //   24: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 35
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 48 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 51	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 71	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 54	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 54	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 54	android/os/Parcel:recycle	()V
        //   85: aload_1
        //   86: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	87	0	this	Proxy
        //   0	87	1	paramIBinder	IBinder
        //   50	2	2	i	int
        //   56	15	3	bool	boolean
        //   3	78	4	localParcel1	Parcel
        //   8	68	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	51	74	finally
      }
      
      /* Error */
      public boolean removeNetwork(int paramInt)
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
        //   17: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 13
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 54	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 54	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      /* Error */
      public WifiActivityEnergyInfo reportActivityInfo()
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
        //   14: aload_0
        //   15: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_2
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 48 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 51	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 71	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 210	android/net/wifi/WifiActivityEnergyInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 97 2 0
        //   48: checkcast 209	android/net/wifi/WifiActivityEnergyInfo
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 54	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 54	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 54	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 54	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localWifiActivityEnergyInfo	WifiActivityEnergyInfo
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      /* Error */
      public void requestActivityInfo(ResultReceiver paramResultReceiver)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +33 -> 44
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 215	android/os/ResultReceiver:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: iconst_3
        //   30: aload_2
        //   31: aconst_null
        //   32: iconst_1
        //   33: invokeinterface 48 5 0
        //   38: pop
        //   39: aload_2
        //   40: invokevirtual 54	android/os/Parcel:recycle	()V
        //   43: return
        //   44: aload_2
        //   45: iconst_0
        //   46: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   49: goto -24 -> 25
        //   52: astore_1
        //   53: aload_2
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: aload_1
        //   58: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	59	0	this	Proxy
        //   0	59	1	paramResultReceiver	ResultReceiver
        //   3	51	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	52	finally
        //   14	25	52	finally
        //   25	39	52	finally
        //   44	49	52	finally
      }
      
      /* Error */
      public boolean requestRunningP2p()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 71
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      public boolean requestRunningSoftap()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 70
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      public boolean saveConfiguration()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 30
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public void setAllowScansWithTraffic(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeInt(paramInt);
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
      
      public void setCountryCode(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
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
      public boolean setEnableAutoJoinWhenAssociated(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +5 -> 23
        //   21: iconst_1
        //   22: istore_2
        //   23: aload_3
        //   24: iload_2
        //   25: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 58
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 48 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 51	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 71	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 54	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 54	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 54	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramBoolean	boolean
        //   1	55	2	i	int
        //   5	80	3	localParcel1	Parcel
        //   9	71	4	localParcel2	Parcel
        //   77	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	77	finally
        //   23	55	77	finally
      }
      
      public void setFrequencyBand(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
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
      
      /* Error */
      public void setWifiApConfiguration(WifiConfiguration paramWifiConfiguration)
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
        //   20: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 76	android/net/wifi/WifiConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 44
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 48 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 51	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 54	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 54	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 54	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramWifiConfiguration	WifiConfiguration
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void setWifiApEnabled(WifiConfiguration paramWifiConfiguration, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
            if (paramWifiConfiguration != null)
            {
              localParcel1.writeInt(1);
              paramWifiConfiguration.writeToParcel(localParcel1, 0);
              break label105;
              localParcel1.writeInt(i);
              this.mRemote.transact(40, localParcel1, localParcel2, 0);
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
          label105:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public boolean setWifiEnabled(boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: iload_1
        //   18: ifeq +5 -> 23
        //   21: iconst_1
        //   22: istore_2
        //   23: aload_3
        //   24: iload_2
        //   25: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 23
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 48 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 51	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 71	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 54	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 54	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 54	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 54	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramBoolean	boolean
        //   1	55	2	i	int
        //   5	80	3	localParcel1	Parcel
        //   9	71	4	localParcel2	Parcel
        //   77	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	17	77	finally
        //   23	55	77	finally
      }
      
      /* Error */
      public boolean setWifiStaSapConcurrencyEnabled(int paramInt)
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
        //   17: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 68
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 48 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 51	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 71	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 54	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 54	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 54	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 54	android/os/Parcel:recycle	()V
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
      
      public void startScan(ScanSettings paramScanSettings, WorkSource paramWorkSource)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.wifi.IWifiManager");
            if (paramScanSettings != null)
            {
              localParcel1.writeInt(1);
              paramScanSettings.writeToParcel(localParcel1, 0);
              if (paramWorkSource != null)
              {
                localParcel1.writeInt(1);
                paramWorkSource.writeToParcel(localParcel1, 0);
                this.mRemote.transact(17, localParcel1, localParcel2, 0);
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
      public boolean unloadFtmDriver()
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
        //   16: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 67
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 48 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 51	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 71	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 54	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 54	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 54	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 54	android/os/Parcel:recycle	()V
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
      public void updateWifiLockWorkSource(IBinder paramIBinder, WorkSource paramWorkSource)
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
        //   16: aload_1
        //   17: invokevirtual 39	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 67	android/os/WorkSource:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/net/wifi/IWifiManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 34
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 48 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 51	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 54	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 54	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 61	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 54	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 54	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramIBinder	IBinder
        //   0	86	2	paramWorkSource	WorkSource
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/IWifiManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */