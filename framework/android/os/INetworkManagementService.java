package android.os;

import android.net.INetd;
import android.net.INetd.Stub;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkManagementEventObserver.Stub;
import android.net.InterfaceConfiguration;
import android.net.Network;
import android.net.NetworkStats;
import android.net.RouteInfo;
import android.net.UidRange;
import android.net.wifi.WifiConfiguration;
import java.util.List;

public abstract interface INetworkManagementService
  extends IInterface
{
  public abstract void addIdleTimer(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void addInterfaceToLocalNetwork(String paramString, List<RouteInfo> paramList)
    throws RemoteException;
  
  public abstract void addInterfaceToNetwork(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void addLegacyRouteForNetId(int paramInt1, RouteInfo paramRouteInfo, int paramInt2)
    throws RemoteException;
  
  public abstract void addRoute(int paramInt, RouteInfo paramRouteInfo)
    throws RemoteException;
  
  public abstract void addVpnUidRanges(int paramInt, UidRange[] paramArrayOfUidRange)
    throws RemoteException;
  
  public abstract void allowProtect(int paramInt)
    throws RemoteException;
  
  public abstract void attachPppd(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws RemoteException;
  
  public abstract void blackListWifiDevice(String paramString1, boolean paramBoolean, String paramString2)
    throws RemoteException;
  
  public abstract void clearDefaultNetId()
    throws RemoteException;
  
  public abstract void clearInterfaceAddresses(String paramString)
    throws RemoteException;
  
  public abstract void clearPermission(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract void createPhysicalNetwork(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void createSoftApInterface(String paramString)
    throws RemoteException;
  
  public abstract void createVirtualNetwork(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void deleteSoftApInterface(String paramString)
    throws RemoteException;
  
  public abstract void denyProtect(int paramInt)
    throws RemoteException;
  
  public abstract void detachPppd(String paramString)
    throws RemoteException;
  
  public abstract void disableIpv6(String paramString)
    throws RemoteException;
  
  public abstract void disableNat(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void doOemMyftmCommand(String paramString)
    throws RemoteException;
  
  public abstract void enableIpv6(String paramString)
    throws RemoteException;
  
  public abstract void enableNat(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract String[] getDnsForwarders()
    throws RemoteException;
  
  public abstract InterfaceConfiguration getInterfaceConfig(String paramString)
    throws RemoteException;
  
  public abstract boolean getIpForwardingEnabled()
    throws RemoteException;
  
  public abstract INetd getNetdService()
    throws RemoteException;
  
  public abstract NetworkStats getNetworkStatsDetail()
    throws RemoteException;
  
  public abstract NetworkStats getNetworkStatsSummaryDev()
    throws RemoteException;
  
  public abstract NetworkStats getNetworkStatsSummaryXt()
    throws RemoteException;
  
  public abstract NetworkStats getNetworkStatsTethering()
    throws RemoteException;
  
  public abstract NetworkStats getNetworkStatsUidDetail(int paramInt)
    throws RemoteException;
  
  public abstract boolean isBandwidthControlEnabled()
    throws RemoteException;
  
  public abstract boolean isClatdStarted(String paramString)
    throws RemoteException;
  
  public abstract boolean isFirewallEnabled()
    throws RemoteException;
  
  public abstract boolean isNetworkActive()
    throws RemoteException;
  
  public abstract boolean isTetheringStarted()
    throws RemoteException;
  
  public abstract String[] listInterfaces()
    throws RemoteException;
  
  public abstract String[] listTetheredInterfaces()
    throws RemoteException;
  
  public abstract String[] listTtys()
    throws RemoteException;
  
  public abstract void registerNetworkActivityListener(INetworkActivityListener paramINetworkActivityListener)
    throws RemoteException;
  
  public abstract void registerObserver(INetworkManagementEventObserver paramINetworkManagementEventObserver)
    throws RemoteException;
  
  public abstract void removeIdleTimer(String paramString)
    throws RemoteException;
  
  public abstract void removeInterfaceAlert(String paramString)
    throws RemoteException;
  
  public abstract void removeInterfaceFromLocalNetwork(String paramString)
    throws RemoteException;
  
  public abstract void removeInterfaceFromNetwork(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void removeInterfaceQuota(String paramString)
    throws RemoteException;
  
  public abstract void removeNetwork(int paramInt)
    throws RemoteException;
  
  public abstract void removeRoute(int paramInt, RouteInfo paramRouteInfo)
    throws RemoteException;
  
  public abstract int removeRoutesFromLocalNetwork(List<RouteInfo> paramList)
    throws RemoteException;
  
  public abstract void removeVpnUidRanges(int paramInt, UidRange[] paramArrayOfUidRange)
    throws RemoteException;
  
  public abstract void setAccessPoint(WifiConfiguration paramWifiConfiguration, String paramString)
    throws RemoteException;
  
  public abstract void setAllowOnlyVpnForUids(boolean paramBoolean, UidRange[] paramArrayOfUidRange)
    throws RemoteException;
  
  public abstract boolean setDataSaverModeEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setDefaultNetId(int paramInt)
    throws RemoteException;
  
  public abstract void setDnsConfigurationForNetwork(int paramInt, String[] paramArrayOfString, String paramString)
    throws RemoteException;
  
  public abstract void setDnsForwarders(Network paramNetwork, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void setDnsServersForNetwork(int paramInt, String[] paramArrayOfString, String paramString)
    throws RemoteException;
  
  public abstract void setFirewallChainEnabled(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setFirewallEgressDestRule(String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setFirewallEgressSourceRule(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setFirewallEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setFirewallInterfaceRule(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setFirewallUidRule(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void setFirewallUidRules(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws RemoteException;
  
  public abstract void setGlobalAlert(long paramLong)
    throws RemoteException;
  
  public abstract void setInterfaceAlert(String paramString, long paramLong)
    throws RemoteException;
  
  public abstract void setInterfaceConfig(String paramString, InterfaceConfiguration paramInterfaceConfiguration)
    throws RemoteException;
  
  public abstract void setInterfaceDown(String paramString)
    throws RemoteException;
  
  public abstract void setInterfaceIpv6NdOffload(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setInterfaceIpv6PrivacyExtensions(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setInterfaceQuota(String paramString, long paramLong)
    throws RemoteException;
  
  public abstract void setInterfaceUp(String paramString)
    throws RemoteException;
  
  public abstract void setIpForwardingEnabled(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMtu(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setNetworkPermission(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void setPermission(String paramString, int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract void setPortForwardRules(boolean paramBoolean, String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract void setUidCleartextNetworkPolicy(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setUidMeteredNetworkBlacklist(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUidMeteredNetworkWhitelist(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void shutdown()
    throws RemoteException;
  
  public abstract void startAccessPoint(WifiConfiguration paramWifiConfiguration, String paramString)
    throws RemoteException;
  
  public abstract void startClatd(String paramString)
    throws RemoteException;
  
  public abstract void startInterfaceForwarding(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void startTethering(String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void startWigigAccessPoint()
    throws RemoteException;
  
  public abstract void stopAccessPoint(String paramString)
    throws RemoteException;
  
  public abstract void stopClatd(String paramString)
    throws RemoteException;
  
  public abstract void stopInterfaceForwarding(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void stopTethering()
    throws RemoteException;
  
  public abstract void stopWigigAccessPoint()
    throws RemoteException;
  
  public abstract void tetherInterface(String paramString)
    throws RemoteException;
  
  public abstract void unregisterNetworkActivityListener(INetworkActivityListener paramINetworkActivityListener)
    throws RemoteException;
  
  public abstract void unregisterObserver(INetworkManagementEventObserver paramINetworkManagementEventObserver)
    throws RemoteException;
  
  public abstract void untetherInterface(String paramString)
    throws RemoteException;
  
  public abstract void wifiFirmwareReload(String paramString1, String paramString2)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements INetworkManagementService
  {
    private static final String DESCRIPTOR = "android.os.INetworkManagementService";
    static final int TRANSACTION_addIdleTimer = 59;
    static final int TRANSACTION_addInterfaceToLocalNetwork = 92;
    static final int TRANSACTION_addInterfaceToNetwork = 82;
    static final int TRANSACTION_addLegacyRouteForNetId = 84;
    static final int TRANSACTION_addRoute = 14;
    static final int TRANSACTION_addVpnUidRanges = 71;
    static final int TRANSACTION_allowProtect = 90;
    static final int TRANSACTION_attachPppd = 34;
    static final int TRANSACTION_blackListWifiDevice = 42;
    static final int TRANSACTION_clearDefaultNetId = 86;
    static final int TRANSACTION_clearInterfaceAddresses = 7;
    static final int TRANSACTION_clearPermission = 89;
    static final int TRANSACTION_createPhysicalNetwork = 79;
    static final int TRANSACTION_createSoftApInterface = 96;
    static final int TRANSACTION_createVirtualNetwork = 80;
    static final int TRANSACTION_deleteSoftApInterface = 97;
    static final int TRANSACTION_denyProtect = 91;
    static final int TRANSACTION_detachPppd = 35;
    static final int TRANSACTION_disableIpv6 = 11;
    static final int TRANSACTION_disableNat = 31;
    static final int TRANSACTION_doOemMyftmCommand = 43;
    static final int TRANSACTION_enableIpv6 = 12;
    static final int TRANSACTION_enableNat = 30;
    static final int TRANSACTION_getDnsForwarders = 27;
    static final int TRANSACTION_getInterfaceConfig = 5;
    static final int TRANSACTION_getIpForwardingEnabled = 18;
    static final int TRANSACTION_getNetdService = 3;
    static final int TRANSACTION_getNetworkStatsDetail = 46;
    static final int TRANSACTION_getNetworkStatsSummaryDev = 44;
    static final int TRANSACTION_getNetworkStatsSummaryXt = 45;
    static final int TRANSACTION_getNetworkStatsTethering = 48;
    static final int TRANSACTION_getNetworkStatsUidDetail = 47;
    static final int TRANSACTION_isBandwidthControlEnabled = 58;
    static final int TRANSACTION_isClatdStarted = 75;
    static final int TRANSACTION_isFirewallEnabled = 64;
    static final int TRANSACTION_isNetworkActive = 78;
    static final int TRANSACTION_isTetheringStarted = 22;
    static final int TRANSACTION_listInterfaces = 4;
    static final int TRANSACTION_listTetheredInterfaces = 25;
    static final int TRANSACTION_listTtys = 33;
    static final int TRANSACTION_registerNetworkActivityListener = 76;
    static final int TRANSACTION_registerObserver = 1;
    static final int TRANSACTION_removeIdleTimer = 60;
    static final int TRANSACTION_removeInterfaceAlert = 52;
    static final int TRANSACTION_removeInterfaceFromLocalNetwork = 93;
    static final int TRANSACTION_removeInterfaceFromNetwork = 83;
    static final int TRANSACTION_removeInterfaceQuota = 50;
    static final int TRANSACTION_removeNetwork = 81;
    static final int TRANSACTION_removeRoute = 15;
    static final int TRANSACTION_removeRoutesFromLocalNetwork = 94;
    static final int TRANSACTION_removeVpnUidRanges = 72;
    static final int TRANSACTION_setAccessPoint = 41;
    static final int TRANSACTION_setAllowOnlyVpnForUids = 95;
    static final int TRANSACTION_setDataSaverModeEnabled = 56;
    static final int TRANSACTION_setDefaultNetId = 85;
    static final int TRANSACTION_setDnsConfigurationForNetwork = 61;
    static final int TRANSACTION_setDnsForwarders = 26;
    static final int TRANSACTION_setDnsServersForNetwork = 62;
    static final int TRANSACTION_setFirewallChainEnabled = 70;
    static final int TRANSACTION_setFirewallEgressDestRule = 67;
    static final int TRANSACTION_setFirewallEgressSourceRule = 66;
    static final int TRANSACTION_setFirewallEnabled = 63;
    static final int TRANSACTION_setFirewallInterfaceRule = 65;
    static final int TRANSACTION_setFirewallUidRule = 68;
    static final int TRANSACTION_setFirewallUidRules = 69;
    static final int TRANSACTION_setGlobalAlert = 53;
    static final int TRANSACTION_setInterfaceAlert = 51;
    static final int TRANSACTION_setInterfaceConfig = 6;
    static final int TRANSACTION_setInterfaceDown = 8;
    static final int TRANSACTION_setInterfaceIpv6NdOffload = 13;
    static final int TRANSACTION_setInterfaceIpv6PrivacyExtensions = 10;
    static final int TRANSACTION_setInterfaceQuota = 49;
    static final int TRANSACTION_setInterfaceUp = 9;
    static final int TRANSACTION_setIpForwardingEnabled = 19;
    static final int TRANSACTION_setMtu = 16;
    static final int TRANSACTION_setNetworkPermission = 87;
    static final int TRANSACTION_setPermission = 88;
    static final int TRANSACTION_setPortForwardRules = 32;
    static final int TRANSACTION_setUidCleartextNetworkPolicy = 57;
    static final int TRANSACTION_setUidMeteredNetworkBlacklist = 54;
    static final int TRANSACTION_setUidMeteredNetworkWhitelist = 55;
    static final int TRANSACTION_shutdown = 17;
    static final int TRANSACTION_startAccessPoint = 37;
    static final int TRANSACTION_startClatd = 73;
    static final int TRANSACTION_startInterfaceForwarding = 28;
    static final int TRANSACTION_startTethering = 20;
    static final int TRANSACTION_startWigigAccessPoint = 38;
    static final int TRANSACTION_stopAccessPoint = 39;
    static final int TRANSACTION_stopClatd = 74;
    static final int TRANSACTION_stopInterfaceForwarding = 29;
    static final int TRANSACTION_stopTethering = 21;
    static final int TRANSACTION_stopWigigAccessPoint = 40;
    static final int TRANSACTION_tetherInterface = 23;
    static final int TRANSACTION_unregisterNetworkActivityListener = 77;
    static final int TRANSACTION_unregisterObserver = 2;
    static final int TRANSACTION_untetherInterface = 24;
    static final int TRANSACTION_wifiFirmwareReload = 36;
    
    public Stub()
    {
      attachInterface(this, "android.os.INetworkManagementService");
    }
    
    public static INetworkManagementService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.os.INetworkManagementService");
      if ((localIInterface != null) && ((localIInterface instanceof INetworkManagementService))) {
        return (INetworkManagementService)localIInterface;
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
      Object localObject;
      boolean bool1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.os.INetworkManagementService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        registerObserver(INetworkManagementEventObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        unregisterObserver(INetworkManagementEventObserver.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getNetdService();
        paramParcel2.writeNoException();
        if (paramParcel1 != null) {}
        for (paramParcel1 = paramParcel1.asBinder();; paramParcel1 = null)
        {
          paramParcel2.writeStrongBinder(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = listInterfaces();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getInterfaceConfig(paramParcel1.readString());
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
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (InterfaceConfiguration)InterfaceConfiguration.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setInterfaceConfig((String)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        clearInterfaceAddresses(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setInterfaceDown(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setInterfaceUp(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setInterfaceIpv6PrivacyExtensions((String)localObject, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 11: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        disableIpv6(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 12: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        enableIpv6(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 13: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setInterfaceIpv6NdOffload((String)localObject, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (RouteInfo)RouteInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          addRoute(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (RouteInfo)RouteInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          removeRoute(paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 16: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setMtu(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        shutdown();
        paramParcel2.writeNoException();
        return true;
      case 18: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        bool1 = getIpForwardingEnabled();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setIpForwardingEnabled(bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        startTethering(paramParcel1.createStringArray());
        paramParcel2.writeNoException();
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        stopTethering();
        paramParcel2.writeNoException();
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        bool1 = isTetheringStarted();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 23: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        tetherInterface(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 24: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        untetherInterface(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 25: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = listTetheredInterfaces();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (Network)Network.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setDnsForwarders((Network)localObject, paramParcel1.createStringArray());
          paramParcel2.writeNoException();
          return true;
        }
      case 27: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getDnsForwarders();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        startInterfaceForwarding(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        stopInterfaceForwarding(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 30: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        enableNat(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 31: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        disableNat(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setPortForwardRules(bool1, paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 33: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = listTtys();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 34: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        attachPppd(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 35: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        detachPppd(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 36: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        wifiFirmwareReload(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 37: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (WifiConfiguration)WifiConfiguration.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          startAccessPoint((WifiConfiguration)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 38: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        startWigigAccessPoint();
        paramParcel2.writeNoException();
        return true;
      case 39: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        stopAccessPoint(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 40: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        stopWigigAccessPoint();
        paramParcel2.writeNoException();
        return true;
      case 41: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (WifiConfiguration)WifiConfiguration.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          setAccessPoint((WifiConfiguration)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 42: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          blackListWifiDevice((String)localObject, bool1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 43: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        doOemMyftmCommand(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 44: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getNetworkStatsSummaryDev();
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
      case 45: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getNetworkStatsSummaryXt();
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
      case 46: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getNetworkStatsDetail();
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
      case 47: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getNetworkStatsUidDetail(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramParcel1 = getNetworkStatsTethering();
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
      case 49: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setInterfaceQuota(paramParcel1.readString(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 50: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        removeInterfaceQuota(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 51: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setInterfaceAlert(paramParcel1.readString(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 52: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        removeInterfaceAlert(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 53: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setGlobalAlert(paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 54: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setUidMeteredNetworkBlacklist(paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 55: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setUidMeteredNetworkWhitelist(paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 56: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          bool1 = setDataSaverModeEnabled(bool1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2507;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
        }
      case 57: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setUidCleartextNetworkPolicy(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 58: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        bool1 = isBandwidthControlEnabled();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 59: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        addIdleTimer(paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 60: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        removeIdleTimer(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 61: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setDnsConfigurationForNetwork(paramParcel1.readInt(), paramParcel1.createStringArray(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 62: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setDnsServersForNetwork(paramParcel1.readInt(), paramParcel1.createStringArray(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 63: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setFirewallEnabled(bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 64: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        bool1 = isFirewallEnabled();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 65: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setFirewallInterfaceRule((String)localObject, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 66: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        localObject = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setFirewallEgressSourceRule((String)localObject, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 67: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        localObject = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setFirewallEgressDestRule((String)localObject, paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 68: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setFirewallUidRule(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 69: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setFirewallUidRules(paramParcel1.readInt(), paramParcel1.createIntArray(), paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        return true;
      case 70: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setFirewallChainEnabled(paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 71: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        addVpnUidRanges(paramParcel1.readInt(), (UidRange[])paramParcel1.createTypedArray(UidRange.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 72: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        removeVpnUidRanges(paramParcel1.readInt(), (UidRange[])paramParcel1.createTypedArray(UidRange.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 73: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        startClatd(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 74: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        stopClatd(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 75: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        bool1 = isClatdStarted(paramParcel1.readString());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 76: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        registerNetworkActivityListener(INetworkActivityListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 77: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        unregisterNetworkActivityListener(INetworkActivityListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 78: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        bool1 = isNetworkActive();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 79: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        createPhysicalNetwork(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 80: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label3268;
          }
        }
        for (boolean bool2 = true;; bool2 = false)
        {
          createVirtualNetwork(paramInt1, bool1, bool2);
          paramParcel2.writeNoException();
          return true;
          bool1 = false;
          break;
        }
      case 81: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        removeNetwork(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 82: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        addInterfaceToNetwork(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 83: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        removeInterfaceFromNetwork(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 84: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (RouteInfo)RouteInfo.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          addLegacyRouteForNetId(paramInt1, (RouteInfo)localObject, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      case 85: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setDefaultNetId(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 86: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        clearDefaultNetId();
        paramParcel2.writeNoException();
        return true;
      case 87: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setNetworkPermission(paramParcel1.readInt(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 88: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        setPermission(paramParcel1.readString(), paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        return true;
      case 89: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        clearPermission(paramParcel1.createIntArray());
        paramParcel2.writeNoException();
        return true;
      case 90: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        allowProtect(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 91: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        denyProtect(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 92: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        addInterfaceToLocalNetwork(paramParcel1.readString(), paramParcel1.createTypedArrayList(RouteInfo.CREATOR));
        paramParcel2.writeNoException();
        return true;
      case 93: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        removeInterfaceFromLocalNetwork(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 94: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        paramInt1 = removeRoutesFromLocalNetwork(paramParcel1.createTypedArrayList(RouteInfo.CREATOR));
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 95: 
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setAllowOnlyVpnForUids(bool1, (UidRange[])paramParcel1.createTypedArray(UidRange.CREATOR));
          paramParcel2.writeNoException();
          return true;
        }
      case 96: 
        label2507:
        label3268:
        paramParcel1.enforceInterface("android.os.INetworkManagementService");
        createSoftApInterface(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("android.os.INetworkManagementService");
      deleteSoftApInterface(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements INetworkManagementService
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public void addIdleTimer(String paramString, int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      public void addInterfaceToLocalNetwork(String paramString, List<RouteInfo> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeTypedList(paramList);
          this.mRemote.transact(92, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void addInterfaceToNetwork(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(82, localParcel1, localParcel2, 0);
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
      public void addLegacyRouteForNetId(int paramInt1, RouteInfo paramRouteInfo, int paramInt2)
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
        //   19: iload_1
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_2
        //   24: ifnull +55 -> 79
        //   27: aload 4
        //   29: iconst_1
        //   30: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   33: aload_2
        //   34: aload 4
        //   36: iconst_0
        //   37: invokevirtual 74	android/net/RouteInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   40: aload 4
        //   42: iload_3
        //   43: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: bipush 84
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 49 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 52	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: invokevirtual 55	android/os/Parcel:recycle	()V
        //   73: aload 4
        //   75: invokevirtual 55	android/os/Parcel:recycle	()V
        //   78: return
        //   79: aload 4
        //   81: iconst_0
        //   82: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   85: goto -45 -> 40
        //   88: astore_2
        //   89: aload 5
        //   91: invokevirtual 55	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 55	android/os/Parcel:recycle	()V
        //   99: aload_2
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramInt1	int
        //   0	101	2	paramRouteInfo	RouteInfo
        //   0	101	3	paramInt2	int
        //   3	92	4	localParcel1	Parcel
        //   8	82	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	23	88	finally
        //   27	40	88	finally
        //   40	68	88	finally
        //   79	85	88	finally
      }
      
      /* Error */
      public void addRoute(int paramInt, RouteInfo paramRouteInfo)
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
        //   17: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 74	android/net/RouteInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 14
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_2
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_2
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInt	int
        //   0	86	2	paramRouteInfo	RouteInfo
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public void addVpnUidRanges(int paramInt, UidRange[] paramArrayOfUidRange)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeTypedArray(paramArrayOfUidRange, 0);
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
      
      public void allowProtect(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(90, localParcel1, localParcel2, 0);
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
      
      public void attachPppd(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          localParcel1.writeString(paramString4);
          localParcel1.writeString(paramString5);
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
      
      public void blackListWifiDevice(String paramString1, boolean paramBoolean, String paramString2)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString1);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(42, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearDefaultNetId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(86, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void clearInterfaceAddresses(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void clearPermission(int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(89, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void createPhysicalNetwork(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(79, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void createSoftApInterface(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(96, localParcel1, localParcel2, 0);
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
      public void createVirtualNetwork(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: iload_1
        //   23: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   26: iload_2
        //   27: ifeq +57 -> 84
        //   30: iconst_1
        //   31: istore_1
        //   32: aload 5
        //   34: iload_1
        //   35: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   38: iload_3
        //   39: ifeq +50 -> 89
        //   42: iload 4
        //   44: istore_1
        //   45: aload 5
        //   47: iload_1
        //   48: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   51: aload_0
        //   52: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   55: bipush 80
        //   57: aload 5
        //   59: aload 6
        //   61: iconst_0
        //   62: invokeinterface 49 5 0
        //   67: pop
        //   68: aload 6
        //   70: invokevirtual 52	android/os/Parcel:readException	()V
        //   73: aload 6
        //   75: invokevirtual 55	android/os/Parcel:recycle	()V
        //   78: aload 5
        //   80: invokevirtual 55	android/os/Parcel:recycle	()V
        //   83: return
        //   84: iconst_0
        //   85: istore_1
        //   86: goto -54 -> 32
        //   89: iconst_0
        //   90: istore_1
        //   91: goto -46 -> 45
        //   94: astore 7
        //   96: aload 6
        //   98: invokevirtual 55	android/os/Parcel:recycle	()V
        //   101: aload 5
        //   103: invokevirtual 55	android/os/Parcel:recycle	()V
        //   106: aload 7
        //   108: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	109	0	this	Proxy
        //   0	109	1	paramInt	int
        //   0	109	2	paramBoolean1	boolean
        //   0	109	3	paramBoolean2	boolean
        //   1	42	4	i	int
        //   6	96	5	localParcel1	Parcel
        //   11	86	6	localParcel2	Parcel
        //   94	13	7	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   13	26	94	finally
        //   32	38	94	finally
        //   45	73	94	finally
      }
      
      public void deleteSoftApInterface(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(97, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void denyProtect(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(91, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void detachPppd(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void disableIpv6(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void disableNat(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void doOemMyftmCommand(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void enableIpv6(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void enableNat(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] getDnsForwarders()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(27, localParcel1, localParcel2, 0);
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
      public InterfaceConfiguration getInterfaceConfig(String paramString)
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
        //   16: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   19: aload_0
        //   20: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   23: iconst_5
        //   24: aload_2
        //   25: aload_3
        //   26: iconst_0
        //   27: invokeinterface 49 5 0
        //   32: pop
        //   33: aload_3
        //   34: invokevirtual 52	android/os/Parcel:readException	()V
        //   37: aload_3
        //   38: invokevirtual 121	android/os/Parcel:readInt	()I
        //   41: ifeq +26 -> 67
        //   44: getstatic 127	android/net/InterfaceConfiguration:CREATOR	Landroid/os/Parcelable$Creator;
        //   47: aload_3
        //   48: invokeinterface 133 2 0
        //   53: checkcast 123	android/net/InterfaceConfiguration
        //   56: astore_1
        //   57: aload_3
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_2
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: aload_1
        //   66: areturn
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -12 -> 57
        //   72: astore_1
        //   73: aload_3
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: aload_2
        //   78: invokevirtual 55	android/os/Parcel:recycle	()V
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
      
      public String getInterfaceDescriptor()
      {
        return "android.os.INetworkManagementService";
      }
      
      /* Error */
      public boolean getIpForwardingEnabled()
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
        //   16: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 18
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 121	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 55	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 55	android/os/Parcel:recycle	()V
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
      
      public INetd getNetdService()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          INetd localINetd = INetd.Stub.asInterface(localParcel2.readStrongBinder());
          return localINetd;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public NetworkStats getNetworkStatsDetail()
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
        //   15: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 46
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 49 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 52	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 121	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 153	android/net/NetworkStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 133 2 0
        //   49: checkcast 152	android/net/NetworkStats
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 55	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localNetworkStats	NetworkStats
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public NetworkStats getNetworkStatsSummaryDev()
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
        //   15: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 44
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 49 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 52	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 121	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 153	android/net/NetworkStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 133 2 0
        //   49: checkcast 152	android/net/NetworkStats
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 55	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localNetworkStats	NetworkStats
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public NetworkStats getNetworkStatsSummaryXt()
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
        //   15: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 45
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 49 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 52	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 121	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 153	android/net/NetworkStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 133 2 0
        //   49: checkcast 152	android/net/NetworkStats
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 55	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localNetworkStats	NetworkStats
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public NetworkStats getNetworkStatsTethering()
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
        //   15: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 48
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 49 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 52	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 121	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 153	android/net/NetworkStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 133 2 0
        //   49: checkcast 152	android/net/NetworkStats
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 55	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 55	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localNetworkStats	NetworkStats
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public NetworkStats getNetworkStatsUidDetail(int paramInt)
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
        //   17: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   20: aload_0
        //   21: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 47
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 49 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 52	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 121	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 153	android/net/NetworkStats:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 133 2 0
        //   59: checkcast 152	android/net/NetworkStats
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 55	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 55	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 55	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 55	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localNetworkStats	NetworkStats
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public boolean isBandwidthControlEnabled()
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
        //   16: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 58
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 121	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 55	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 55	android/os/Parcel:recycle	()V
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
      public boolean isClatdStarted(String paramString)
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
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload_0
        //   24: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   27: bipush 75
        //   29: aload 4
        //   31: aload 5
        //   33: iconst_0
        //   34: invokeinterface 49 5 0
        //   39: pop
        //   40: aload 5
        //   42: invokevirtual 52	android/os/Parcel:readException	()V
        //   45: aload 5
        //   47: invokevirtual 121	android/os/Parcel:readInt	()I
        //   50: istore_2
        //   51: iload_2
        //   52: ifeq +17 -> 69
        //   55: iconst_1
        //   56: istore_3
        //   57: aload 5
        //   59: invokevirtual 55	android/os/Parcel:recycle	()V
        //   62: aload 4
        //   64: invokevirtual 55	android/os/Parcel:recycle	()V
        //   67: iload_3
        //   68: ireturn
        //   69: iconst_0
        //   70: istore_3
        //   71: goto -14 -> 57
        //   74: astore_1
        //   75: aload 5
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload 4
        //   82: invokevirtual 55	android/os/Parcel:recycle	()V
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
      public boolean isFirewallEnabled()
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
        //   16: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 64
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 121	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 55	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 55	android/os/Parcel:recycle	()V
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
      public boolean isNetworkActive()
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
        //   16: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 78
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 121	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 55	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 55	android/os/Parcel:recycle	()V
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
      public boolean isTetheringStarted()
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
        //   16: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 22
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 121	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 55	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 55	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 55	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 55	android/os/Parcel:recycle	()V
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
      
      public String[] listInterfaces()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
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
      
      public String[] listTetheredInterfaces()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(25, localParcel1, localParcel2, 0);
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
      
      public String[] listTtys()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
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
      
      public void registerNetworkActivityListener(INetworkActivityListener paramINetworkActivityListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramINetworkActivityListener != null) {
            localIBinder = paramINetworkActivityListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(76, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void registerObserver(INetworkManagementEventObserver paramINetworkManagementEventObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramINetworkManagementEventObserver != null) {
            localIBinder = paramINetworkManagementEventObserver.asBinder();
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
      
      public void removeIdleTimer(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void removeInterfaceAlert(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
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
      
      public void removeInterfaceFromLocalNetwork(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(93, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeInterfaceFromNetwork(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(83, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void removeInterfaceQuota(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
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
      
      public void removeNetwork(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(81, localParcel1, localParcel2, 0);
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
      public void removeRoute(int paramInt, RouteInfo paramRouteInfo)
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
        //   17: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 74	android/net/RouteInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 15
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_2
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_2
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramInt	int
        //   0	86	2	paramRouteInfo	RouteInfo
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public int removeRoutesFromLocalNetwork(List<RouteInfo> paramList)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeTypedList(paramList);
          this.mRemote.transact(94, localParcel1, localParcel2, 0);
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
      
      public void removeVpnUidRanges(int paramInt, UidRange[] paramArrayOfUidRange)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeTypedArray(paramArrayOfUidRange, 0);
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
      
      /* Error */
      public void setAccessPoint(WifiConfiguration paramWifiConfiguration, String paramString)
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
        //   21: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 197	android/net/wifi/WifiConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 41
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramWifiConfiguration	WifiConfiguration
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void setAllowOnlyVpnForUids(boolean paramBoolean, UidRange[] paramArrayOfUidRange)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeTypedArray(paramArrayOfUidRange, 0);
          this.mRemote.transact(95, localParcel1, localParcel2, 0);
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
      public boolean setDataSaverModeEnabled(boolean paramBoolean)
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
        //   25: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   28: aload_0
        //   29: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   32: bipush 56
        //   34: aload_3
        //   35: aload 4
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload 4
        //   46: invokevirtual 52	android/os/Parcel:readException	()V
        //   49: aload 4
        //   51: invokevirtual 121	android/os/Parcel:readInt	()I
        //   54: istore_2
        //   55: iload_2
        //   56: ifeq +16 -> 72
        //   59: iconst_1
        //   60: istore_1
        //   61: aload 4
        //   63: invokevirtual 55	android/os/Parcel:recycle	()V
        //   66: aload_3
        //   67: invokevirtual 55	android/os/Parcel:recycle	()V
        //   70: iload_1
        //   71: ireturn
        //   72: iconst_0
        //   73: istore_1
        //   74: goto -13 -> 61
        //   77: astore 5
        //   79: aload 4
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 55	android/os/Parcel:recycle	()V
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
      
      public void setDefaultNetId(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(85, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setDnsConfigurationForNetwork(int paramInt, String[] paramArrayOfString, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeStringArray(paramArrayOfString);
          localParcel1.writeString(paramString);
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
      
      /* Error */
      public void setDnsForwarders(Network paramNetwork, String[] paramArrayOfString)
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
        //   21: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 213	android/net/Network:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 208	android/os/Parcel:writeStringArray	([Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 26
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramNetwork	Network
        //   0	86	2	paramArrayOfString	String[]
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void setDnsServersForNetwork(int paramInt, String[] paramArrayOfString, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeStringArray(paramArrayOfString);
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
      
      public void setFirewallChainEnabled(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(70, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setFirewallEgressDestRule(String paramString, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
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
      
      public void setFirewallEgressSourceRule(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(66, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setFirewallEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setFirewallInterfaceRule(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setFirewallUidRule(int paramInt1, int paramInt2, int paramInt3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          localParcel1.writeInt(paramInt3);
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
      
      public void setFirewallUidRules(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeIntArray(paramArrayOfInt1);
          localParcel1.writeIntArray(paramArrayOfInt2);
          this.mRemote.transact(69, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setGlobalAlert(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeLong(paramLong);
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
      
      public void setInterfaceAlert(String paramString, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
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
      
      /* Error */
      public void setInterfaceConfig(String paramString, InterfaceConfiguration paramInterfaceConfiguration)
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
        //   17: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   20: aload_2
        //   21: ifnull +45 -> 66
        //   24: aload_3
        //   25: iconst_1
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload_2
        //   30: aload_3
        //   31: iconst_0
        //   32: invokevirtual 237	android/net/InterfaceConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   35: aload_0
        //   36: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 6
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -36 -> 35
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramString	String
        //   0	86	2	paramInterfaceConfiguration	InterfaceConfiguration
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	74	finally
        //   24	35	74	finally
        //   35	56	74	finally
        //   66	71	74	finally
      }
      
      public void setInterfaceDown(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void setInterfaceIpv6NdOffload(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setInterfaceIpv6PrivacyExtensions(String paramString, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setInterfaceQuota(String paramString, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeLong(paramLong);
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
      
      public void setInterfaceUp(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void setIpForwardingEnabled(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public void setMtu(String paramString, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
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
      
      public void setNetworkPermission(int paramInt, String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
          this.mRemote.transact(87, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setPermission(String paramString, int[] paramArrayOfInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          localParcel1.writeIntArray(paramArrayOfInt);
          this.mRemote.transact(88, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setPortForwardRules(boolean paramBoolean, String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.mRemote.transact(32, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void setUidCleartextNetworkPolicy(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
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
      
      public void setUidMeteredNetworkBlacklist(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
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
      
      public void setUidMeteredNetworkWhitelist(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
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
      
      public void shutdown()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(17, localParcel1, localParcel2, 0);
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
      public void startAccessPoint(WifiConfiguration paramWifiConfiguration, String paramString)
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
        //   21: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 197	android/net/wifi/WifiConfiguration:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/os/INetworkManagementService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 37
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 55	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 55	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 55	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 55	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramWifiConfiguration	WifiConfiguration
        //   0	86	2	paramString	String
        //   3	78	3	localParcel1	Parcel
        //   7	69	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	74	finally
        //   19	30	74	finally
        //   30	56	74	finally
        //   66	71	74	finally
      }
      
      public void startClatd(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(73, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void startInterfaceForwarding(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void startTethering(String[] paramArrayOfString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeStringArray(paramArrayOfString);
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
      
      public void startWigigAccessPoint()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
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
      
      public void stopAccessPoint(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void stopClatd(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(74, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void stopInterfaceForwarding(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void stopTethering()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
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
      
      public void stopWigigAccessPoint()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          this.mRemote.transact(40, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void tetherInterface(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
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
      
      public void unregisterNetworkActivityListener(INetworkActivityListener paramINetworkActivityListener)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramINetworkActivityListener != null) {
            localIBinder = paramINetworkActivityListener.asBinder();
          }
          localParcel1.writeStrongBinder(localIBinder);
          this.mRemote.transact(77, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void unregisterObserver(INetworkManagementEventObserver paramINetworkManagementEventObserver)
        throws RemoteException
      {
        IBinder localIBinder = null;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          if (paramINetworkManagementEventObserver != null) {
            localIBinder = paramINetworkManagementEventObserver.asBinder();
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
      
      public void untetherInterface(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(24, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void wifiFirmwareReload(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.os.INetworkManagementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/INetworkManagementService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */