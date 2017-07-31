package android.net;

import android.app.PendingIntent;
import android.net.wifi.WifiDevice;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnInfo;
import com.android.internal.net.VpnProfile;
import java.util.ArrayList;
import java.util.List;

public abstract interface IConnectivityManager
  extends IInterface
{
  public abstract boolean addVpnAddress(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean blackListWifiDevice(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int checkMobileProvisioning(int paramInt)
    throws RemoteException;
  
  public abstract ParcelFileDescriptor establishVpn(VpnConfig paramVpnConfig)
    throws RemoteException;
  
  public abstract void factoryReset()
    throws RemoteException;
  
  public abstract LinkProperties getActiveLinkProperties()
    throws RemoteException;
  
  public abstract Network getActiveNetwork()
    throws RemoteException;
  
  public abstract Network getActiveNetworkForUid(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract NetworkInfo getActiveNetworkInfo()
    throws RemoteException;
  
  public abstract NetworkInfo getActiveNetworkInfoForUid(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract NetworkQuotaInfo getActiveNetworkQuotaInfo()
    throws RemoteException;
  
  public abstract NetworkInfo[] getAllNetworkInfo()
    throws RemoteException;
  
  public abstract NetworkState[] getAllNetworkState()
    throws RemoteException;
  
  public abstract Network[] getAllNetworks()
    throws RemoteException;
  
  public abstract VpnInfo[] getAllVpnInfo()
    throws RemoteException;
  
  public abstract String getAlwaysOnVpnPackage(int paramInt)
    throws RemoteException;
  
  public abstract String getCaptivePortalServerUrl()
    throws RemoteException;
  
  public abstract NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(int paramInt)
    throws RemoteException;
  
  public abstract ProxyInfo getGlobalProxy()
    throws RemoteException;
  
  public abstract int getLastTetherError(String paramString)
    throws RemoteException;
  
  public abstract LegacyVpnInfo getLegacyVpnInfo(int paramInt)
    throws RemoteException;
  
  public abstract LinkProperties getLinkProperties(Network paramNetwork)
    throws RemoteException;
  
  public abstract LinkProperties getLinkPropertiesForType(int paramInt)
    throws RemoteException;
  
  public abstract String getMobileProvisioningUrl()
    throws RemoteException;
  
  public abstract NetworkCapabilities getNetworkCapabilities(Network paramNetwork)
    throws RemoteException;
  
  public abstract Network getNetworkForType(int paramInt)
    throws RemoteException;
  
  public abstract NetworkInfo getNetworkInfo(int paramInt)
    throws RemoteException;
  
  public abstract NetworkInfo getNetworkInfoForUid(Network paramNetwork, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ProxyInfo getProxyForNetwork(Network paramNetwork)
    throws RemoteException;
  
  public abstract int getRestoreDefaultNetworkDelay(int paramInt)
    throws RemoteException;
  
  public abstract List<WifiDevice> getTetherConnectedSta()
    throws RemoteException;
  
  public abstract List<WifiDevice> getTetherSoftApSta(int paramInt)
    throws RemoteException;
  
  public abstract String[] getTetherableBluetoothRegexs()
    throws RemoteException;
  
  public abstract String[] getTetherableIfaces()
    throws RemoteException;
  
  public abstract String[] getTetherableUsbRegexs()
    throws RemoteException;
  
  public abstract String[] getTetherableWifiRegexs()
    throws RemoteException;
  
  public abstract String[] getTetheredDhcpRanges()
    throws RemoteException;
  
  public abstract String[] getTetheredIfaces()
    throws RemoteException;
  
  public abstract String[] getTetheringErroredIfaces()
    throws RemoteException;
  
  public abstract VpnConfig getVpnConfig(int paramInt)
    throws RemoteException;
  
  public abstract boolean isActiveNetworkMetered()
    throws RemoteException;
  
  public abstract boolean isNetworkSupported(int paramInt)
    throws RemoteException;
  
  public abstract boolean isTetheringSupported()
    throws RemoteException;
  
  public abstract NetworkRequest listenForNetwork(NetworkCapabilities paramNetworkCapabilities, Messenger paramMessenger, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void pendingListenForNetwork(NetworkCapabilities paramNetworkCapabilities, PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public abstract NetworkRequest pendingRequestForNetwork(NetworkCapabilities paramNetworkCapabilities, PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public abstract boolean prepareVpn(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract int registerNetworkAgent(Messenger paramMessenger, NetworkInfo paramNetworkInfo, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, int paramInt, NetworkMisc paramNetworkMisc)
    throws RemoteException;
  
  public abstract void registerNetworkFactory(Messenger paramMessenger, String paramString)
    throws RemoteException;
  
  public abstract void releaseNetworkRequest(NetworkRequest paramNetworkRequest)
    throws RemoteException;
  
  public abstract void releasePendingNetworkRequest(PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public abstract boolean removeVpnAddress(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void reportInetCondition(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void reportNetworkConnectivity(Network paramNetwork, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean requestBandwidthUpdate(Network paramNetwork)
    throws RemoteException;
  
  public abstract void requestLinkProperties(NetworkRequest paramNetworkRequest)
    throws RemoteException;
  
  public abstract NetworkRequest requestNetwork(NetworkCapabilities paramNetworkCapabilities, Messenger paramMessenger, int paramInt1, IBinder paramIBinder, int paramInt2)
    throws RemoteException;
  
  public abstract void requestNetworkCapabilities(NetworkRequest paramNetworkRequest)
    throws RemoteException;
  
  public abstract boolean requestRouteToHostAddress(int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void setAcceptUnvalidated(Network paramNetwork, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void setAirplaneMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean setAlwaysOnVpnPackage(int paramInt, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setAvoidUnvalidated(Network paramNetwork)
    throws RemoteException;
  
  public abstract void setGlobalProxy(ProxyInfo paramProxyInfo)
    throws RemoteException;
  
  public abstract void setProvisioningNotificationVisible(boolean paramBoolean, int paramInt, String paramString)
    throws RemoteException;
  
  public abstract boolean setUnderlyingNetworksForVpn(Network[] paramArrayOfNetwork)
    throws RemoteException;
  
  public abstract int setUsbTethering(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setVpnPackageAuthorization(String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void startLegacyVpn(VpnProfile paramVpnProfile)
    throws RemoteException;
  
  public abstract void startNattKeepalive(Network paramNetwork, int paramInt1, Messenger paramMessenger, IBinder paramIBinder, String paramString1, int paramInt2, String paramString2)
    throws RemoteException;
  
  public abstract void startTethering(int paramInt, ResultReceiver paramResultReceiver, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void stopKeepalive(Network paramNetwork, int paramInt)
    throws RemoteException;
  
  public abstract void stopTethering(int paramInt)
    throws RemoteException;
  
  public abstract int tether(String paramString)
    throws RemoteException;
  
  public abstract void unregisterNetworkFactory(Messenger paramMessenger)
    throws RemoteException;
  
  public abstract int untether(String paramString)
    throws RemoteException;
  
  public abstract boolean updateLockdownVpn()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IConnectivityManager
  {
    private static final String DESCRIPTOR = "android.net.IConnectivityManager";
    static final int TRANSACTION_addVpnAddress = 71;
    static final int TRANSACTION_blackListWifiDevice = 36;
    static final int TRANSACTION_checkMobileProvisioning = 52;
    static final int TRANSACTION_establishVpn = 44;
    static final int TRANSACTION_factoryReset = 74;
    static final int TRANSACTION_getActiveLinkProperties = 12;
    static final int TRANSACTION_getActiveNetwork = 1;
    static final int TRANSACTION_getActiveNetworkForUid = 2;
    static final int TRANSACTION_getActiveNetworkInfo = 3;
    static final int TRANSACTION_getActiveNetworkInfoForUid = 4;
    static final int TRANSACTION_getActiveNetworkQuotaInfo = 17;
    static final int TRANSACTION_getAllNetworkInfo = 7;
    static final int TRANSACTION_getAllNetworkState = 16;
    static final int TRANSACTION_getAllNetworks = 9;
    static final int TRANSACTION_getAllVpnInfo = 48;
    static final int TRANSACTION_getAlwaysOnVpnPackage = 51;
    static final int TRANSACTION_getCaptivePortalServerUrl = 77;
    static final int TRANSACTION_getDefaultNetworkCapabilitiesForUser = 10;
    static final int TRANSACTION_getGlobalProxy = 39;
    static final int TRANSACTION_getLastTetherError = 22;
    static final int TRANSACTION_getLegacyVpnInfo = 47;
    static final int TRANSACTION_getLinkProperties = 14;
    static final int TRANSACTION_getLinkPropertiesForType = 13;
    static final int TRANSACTION_getMobileProvisioningUrl = 53;
    static final int TRANSACTION_getNetworkCapabilities = 15;
    static final int TRANSACTION_getNetworkForType = 8;
    static final int TRANSACTION_getNetworkInfo = 5;
    static final int TRANSACTION_getNetworkInfoForUid = 6;
    static final int TRANSACTION_getProxyForNetwork = 41;
    static final int TRANSACTION_getRestoreDefaultNetworkDelay = 70;
    static final int TRANSACTION_getTetherConnectedSta = 34;
    static final int TRANSACTION_getTetherSoftApSta = 35;
    static final int TRANSACTION_getTetherableBluetoothRegexs = 32;
    static final int TRANSACTION_getTetherableIfaces = 26;
    static final int TRANSACTION_getTetherableUsbRegexs = 30;
    static final int TRANSACTION_getTetherableWifiRegexs = 31;
    static final int TRANSACTION_getTetheredDhcpRanges = 29;
    static final int TRANSACTION_getTetheredIfaces = 27;
    static final int TRANSACTION_getTetheringErroredIfaces = 28;
    static final int TRANSACTION_getVpnConfig = 45;
    static final int TRANSACTION_isActiveNetworkMetered = 18;
    static final int TRANSACTION_isNetworkSupported = 11;
    static final int TRANSACTION_isTetheringSupported = 23;
    static final int TRANSACTION_listenForNetwork = 63;
    static final int TRANSACTION_pendingListenForNetwork = 64;
    static final int TRANSACTION_pendingRequestForNetwork = 61;
    static final int TRANSACTION_prepareVpn = 42;
    static final int TRANSACTION_registerNetworkAgent = 59;
    static final int TRANSACTION_registerNetworkFactory = 56;
    static final int TRANSACTION_releaseNetworkRequest = 67;
    static final int TRANSACTION_releasePendingNetworkRequest = 62;
    static final int TRANSACTION_removeVpnAddress = 72;
    static final int TRANSACTION_reportInetCondition = 37;
    static final int TRANSACTION_reportNetworkConnectivity = 38;
    static final int TRANSACTION_requestBandwidthUpdate = 57;
    static final int TRANSACTION_requestLinkProperties = 65;
    static final int TRANSACTION_requestNetwork = 60;
    static final int TRANSACTION_requestNetworkCapabilities = 66;
    static final int TRANSACTION_requestRouteToHostAddress = 19;
    static final int TRANSACTION_setAcceptUnvalidated = 68;
    static final int TRANSACTION_setAirplaneMode = 55;
    static final int TRANSACTION_setAlwaysOnVpnPackage = 50;
    static final int TRANSACTION_setAvoidUnvalidated = 69;
    static final int TRANSACTION_setGlobalProxy = 40;
    static final int TRANSACTION_setProvisioningNotificationVisible = 54;
    static final int TRANSACTION_setUnderlyingNetworksForVpn = 73;
    static final int TRANSACTION_setUsbTethering = 33;
    static final int TRANSACTION_setVpnPackageAuthorization = 43;
    static final int TRANSACTION_startLegacyVpn = 46;
    static final int TRANSACTION_startNattKeepalive = 75;
    static final int TRANSACTION_startTethering = 24;
    static final int TRANSACTION_stopKeepalive = 76;
    static final int TRANSACTION_stopTethering = 25;
    static final int TRANSACTION_tether = 20;
    static final int TRANSACTION_unregisterNetworkFactory = 58;
    static final int TRANSACTION_untether = 21;
    static final int TRANSACTION_updateLockdownVpn = 49;
    
    public Stub()
    {
      attachInterface(this, "android.net.IConnectivityManager");
    }
    
    public static IConnectivityManager asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("android.net.IConnectivityManager");
      if ((localIInterface != null) && ((localIInterface instanceof IConnectivityManager))) {
        return (IConnectivityManager)localIInterface;
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
      label750:
      label854:
      Object localObject1;
      label948:
      label985:
      label991:
      label1297:
      label1363:
      label1688:
      label2012:
      label2098:
      label2244:
      label2405:
      label2663:
      label2923:
      Object localObject2;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("android.net.IConnectivityManager");
        return true;
      case 1: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getActiveNetwork();
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
      case 2: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          paramParcel1 = getActiveNetworkForUid(paramInt1, bool1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label750;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          bool1 = false;
          break;
          paramParcel2.writeInt(0);
        }
      case 3: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getActiveNetworkInfo();
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
      case 4: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          paramParcel1 = getActiveNetworkInfoForUid(paramInt1, bool1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label854;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          bool1 = false;
          break;
          paramParcel2.writeInt(0);
        }
      case 5: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getNetworkInfo(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label985;
          }
          bool1 = true;
          paramParcel1 = getNetworkInfoForUid((Network)localObject1, paramInt1, bool1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label991;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
        }
        for (;;)
        {
          return true;
          localObject1 = null;
          break;
          bool1 = false;
          break label948;
          paramParcel2.writeInt(0);
        }
      case 7: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getAllNetworkInfo();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getNetworkForType(paramParcel1.readInt());
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
      case 9: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getAllNetworks();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 10: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getDefaultNetworkCapabilitiesForUser(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = isNetworkSupported(paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getActiveLinkProperties();
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
      case 13: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getLinkPropertiesForType(paramParcel1.readInt());
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
      case 14: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getLinkProperties(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1297;
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
      case 15: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getNetworkCapabilities(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label1363;
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
      case 16: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getAllNetworkState();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 17: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getActiveNetworkQuotaInfo();
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
      case 18: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = isActiveNetworkMetered();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 19: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = requestRouteToHostAddress(paramParcel1.readInt(), paramParcel1.createByteArray());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 20: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = tether(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 21: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = untether(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 22: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = getLastTetherError(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 23: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = isTetheringSupported();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 24: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (ResultReceiver)ResultReceiver.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label1688;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          startTethering(paramInt1, (ResultReceiver)localObject1, bool1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 25: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        stopTethering(paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 26: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetherableIfaces();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 27: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetheredIfaces();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 28: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetheringErroredIfaces();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 29: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetheredDhcpRanges();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 30: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetherableUsbRegexs();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 31: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetherableWifiRegexs();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 32: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetherableBluetoothRegexs();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 33: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          paramInt1 = setUsbTethering(bool1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 34: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetherConnectedSta();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 35: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getTetherSoftApSta(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 36: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          bool1 = blackListWifiDevice((String)localObject1, bool1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2012;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
        }
      case 37: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        reportInetCondition(paramParcel1.readInt(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 38: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label2098;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          reportNetworkConnectivity((Network)localObject1, bool1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 39: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getGlobalProxy();
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
      case 40: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (ProxyInfo)ProxyInfo.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setGlobalProxy(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 41: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = getProxyForNetwork(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2244;
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
      case 42: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = prepareVpn(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 43: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        localObject1 = paramParcel1.readString();
        paramInt1 = paramParcel1.readInt();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setVpnPackageAuthorization((String)localObject1, paramInt1, bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 44: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (VpnConfig)VpnConfig.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = establishVpn(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label2405;
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
      case 45: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getVpnConfig(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (VpnProfile)VpnProfile.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          startLegacyVpn(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 47: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getLegacyVpnInfo(paramParcel1.readInt());
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
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getAllVpnInfo();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedArray(paramParcel1, 1);
        return true;
      case 49: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = updateLockdownVpn();
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 50: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = paramParcel1.readInt();
        localObject1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          bool1 = setAlwaysOnVpnPackage(paramInt1, (String)localObject1, bool1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2663;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          bool1 = false;
          break;
        }
      case 51: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getAlwaysOnVpnPackage(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 52: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = checkMobileProvisioning(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 53: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramParcel1 = getMobileProvisioningUrl();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 54: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setProvisioningNotificationVisible(bool1, paramParcel1.readInt(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 55: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          setAirplaneMode(bool1);
          paramParcel2.writeNoException();
          return true;
        }
      case 56: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Messenger)Messenger.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          registerNetworkFactory((Messenger)localObject1, paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 57: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          paramParcel1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          bool1 = requestBandwidthUpdate(paramParcel1);
          paramParcel2.writeNoException();
          if (!bool1) {
            break label2923;
          }
        }
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
          paramParcel1 = null;
          break;
        }
      case 58: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Messenger)Messenger.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          unregisterNetworkFactory(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 59: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        LinkProperties localLinkProperties;
        NetworkCapabilities localNetworkCapabilities;
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Messenger)Messenger.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3117;
          }
          localObject2 = (NetworkInfo)NetworkInfo.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3123;
          }
          localLinkProperties = (LinkProperties)LinkProperties.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3129;
          }
          localNetworkCapabilities = (NetworkCapabilities)NetworkCapabilities.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label3135;
          }
        }
        for (paramParcel1 = (NetworkMisc)NetworkMisc.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramInt1 = registerNetworkAgent((Messenger)localObject1, (NetworkInfo)localObject2, localLinkProperties, localNetworkCapabilities, paramInt1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeInt(paramInt1);
          return true;
          localObject1 = null;
          break;
          localObject2 = null;
          break label3018;
          localLinkProperties = null;
          break label3039;
          localNetworkCapabilities = null;
          break label3060;
        }
      case 60: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (NetworkCapabilities)NetworkCapabilities.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3236;
          }
          localObject2 = (Messenger)Messenger.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = requestNetwork((NetworkCapabilities)localObject1, (Messenger)localObject2, paramParcel1.readInt(), paramParcel1.readStrongBinder(), paramParcel1.readInt());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3242;
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
          break label3188;
          paramParcel2.writeInt(0);
        }
      case 61: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (NetworkCapabilities)NetworkCapabilities.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3332;
          }
          paramParcel1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = pendingRequestForNetwork((NetworkCapabilities)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3337;
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
          break label3297;
          paramParcel2.writeInt(0);
        }
      case 62: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          releasePendingNetworkRequest(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 63: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (NetworkCapabilities)NetworkCapabilities.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3475;
          }
          localObject2 = (Messenger)Messenger.CREATOR.createFromParcel(paramParcel1);
          paramParcel1 = listenForNetwork((NetworkCapabilities)localObject1, (Messenger)localObject2, paramParcel1.readStrongBinder());
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break label3481;
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
          break label3435;
          paramParcel2.writeInt(0);
        }
      case 64: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (NetworkCapabilities)NetworkCapabilities.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3555;
          }
        }
        for (paramParcel1 = (PendingIntent)PendingIntent.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          pendingListenForNetwork((NetworkCapabilities)localObject1, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 65: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NetworkRequest)NetworkRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestLinkProperties(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 66: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NetworkRequest)NetworkRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          requestNetworkCapabilities(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 67: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (NetworkRequest)NetworkRequest.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          releaseNetworkRequest(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 68: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label3755;
          }
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label3761;
          }
        }
        for (boolean bool2 = true;; bool2 = false)
        {
          setAcceptUnvalidated((Network)localObject1, bool1, bool2);
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
          bool1 = false;
          break label3723;
        }
      case 69: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          setAvoidUnvalidated(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 70: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        paramInt1 = getRestoreDefaultNetworkDelay(paramParcel1.readInt());
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 71: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = addVpnAddress(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 72: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = removeVpnAddress(paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 73: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        bool1 = setUnderlyingNetworksForVpn((Network[])paramParcel1.createTypedArray(Network.CREATOR));
        paramParcel2.writeNoException();
        if (bool1) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 74: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        factoryReset();
        paramParcel2.writeNoException();
        return true;
      case 75: 
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0)
        {
          localObject1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);
          paramInt1 = paramParcel1.readInt();
          if (paramParcel1.readInt() == 0) {
            break label4072;
          }
        }
        for (localObject2 = (Messenger)Messenger.CREATOR.createFromParcel(paramParcel1);; localObject2 = null)
        {
          startNattKeepalive((Network)localObject1, paramInt1, (Messenger)localObject2, paramParcel1.readStrongBinder(), paramParcel1.readString(), paramParcel1.readInt(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
          localObject1 = null;
          break;
        }
      case 76: 
        label3018:
        label3039:
        label3060:
        label3117:
        label3123:
        label3129:
        label3135:
        label3188:
        label3236:
        label3242:
        label3297:
        label3332:
        label3337:
        label3435:
        label3475:
        label3481:
        label3555:
        label3723:
        label3755:
        label3761:
        label4072:
        paramParcel1.enforceInterface("android.net.IConnectivityManager");
        if (paramParcel1.readInt() != 0) {}
        for (localObject1 = (Network)Network.CREATOR.createFromParcel(paramParcel1);; localObject1 = null)
        {
          stopKeepalive((Network)localObject1, paramParcel1.readInt());
          paramParcel2.writeNoException();
          return true;
        }
      }
      paramParcel1.enforceInterface("android.net.IConnectivityManager");
      paramParcel1 = getCaptivePortalServerUrl();
      paramParcel2.writeNoException();
      paramParcel2.writeString(paramParcel1);
      return true;
    }
    
    private static class Proxy
      implements IConnectivityManager
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public boolean addVpnAddress(String paramString, int paramInt)
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
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 71
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 49 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 52	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 56	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 59	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 59	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 59	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public boolean blackListWifiDevice(String paramString, boolean paramBoolean)
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
        //   21: aload_1
        //   22: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   25: iload_2
        //   26: ifeq +5 -> 31
        //   29: iconst_1
        //   30: istore_3
        //   31: aload 4
        //   33: iload_3
        //   34: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   37: aload_0
        //   38: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   41: bipush 36
        //   43: aload 4
        //   45: aload 5
        //   47: iconst_0
        //   48: invokeinterface 49 5 0
        //   53: pop
        //   54: aload 5
        //   56: invokevirtual 52	android/os/Parcel:readException	()V
        //   59: aload 5
        //   61: invokevirtual 56	android/os/Parcel:readInt	()I
        //   64: istore_3
        //   65: iload_3
        //   66: ifeq +17 -> 83
        //   69: iconst_1
        //   70: istore_2
        //   71: aload 5
        //   73: invokevirtual 59	android/os/Parcel:recycle	()V
        //   76: aload 4
        //   78: invokevirtual 59	android/os/Parcel:recycle	()V
        //   81: iload_2
        //   82: ireturn
        //   83: iconst_0
        //   84: istore_2
        //   85: goto -14 -> 71
        //   88: astore_1
        //   89: aload 5
        //   91: invokevirtual 59	android/os/Parcel:recycle	()V
        //   94: aload 4
        //   96: invokevirtual 59	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	101	0	this	Proxy
        //   0	101	1	paramString	String
        //   0	101	2	paramBoolean	boolean
        //   1	65	3	i	int
        //   5	90	4	localParcel1	Parcel
        //   10	80	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	25	88	finally
        //   31	65	88	finally
      }
      
      public int checkMobileProvisioning(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(52, localParcel1, localParcel2, 0);
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
      
      public ParcelFileDescriptor establishVpn(VpnConfig paramVpnConfig)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramVpnConfig != null)
            {
              localParcel1.writeInt(1);
              paramVpnConfig.writeToParcel(localParcel1, 0);
              this.mRemote.transact(44, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramVpnConfig = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(localParcel2);
                return paramVpnConfig;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramVpnConfig = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void factoryReset()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
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
      
      /* Error */
      public LinkProperties getActiveLinkProperties()
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
        //   15: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 12
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 49 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 52	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 56	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 92	android/net/LinkProperties:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 86 2 0
        //   49: checkcast 91	android/net/LinkProperties
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 59	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 59	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 59	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localLinkProperties	LinkProperties
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      /* Error */
      public Network getActiveNetwork()
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
        //   15: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_1
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 49 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 52	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 56	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 97	android/net/Network:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 86 2 0
        //   48: checkcast 96	android/net/Network
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 59	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 59	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 59	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localNetwork	Network
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      /* Error */
      public Network getActiveNetworkForUid(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 5
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 6
        //   12: aload 5
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 5
        //   21: iload_1
        //   22: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   25: iload_3
        //   26: istore_1
        //   27: iload_2
        //   28: ifeq +5 -> 33
        //   31: iconst_1
        //   32: istore_1
        //   33: aload 5
        //   35: iload_1
        //   36: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   39: aload_0
        //   40: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: iconst_2
        //   44: aload 5
        //   46: aload 6
        //   48: iconst_0
        //   49: invokeinterface 49 5 0
        //   54: pop
        //   55: aload 6
        //   57: invokevirtual 52	android/os/Parcel:readException	()V
        //   60: aload 6
        //   62: invokevirtual 56	android/os/Parcel:readInt	()I
        //   65: ifeq +31 -> 96
        //   68: getstatic 97	android/net/Network:CREATOR	Landroid/os/Parcelable$Creator;
        //   71: aload 6
        //   73: invokeinterface 86 2 0
        //   78: checkcast 96	android/net/Network
        //   81: astore 4
        //   83: aload 6
        //   85: invokevirtual 59	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: invokevirtual 59	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: areturn
        //   96: aconst_null
        //   97: astore 4
        //   99: goto -16 -> 83
        //   102: astore 4
        //   104: aload 6
        //   106: invokevirtual 59	android/os/Parcel:recycle	()V
        //   109: aload 5
        //   111: invokevirtual 59	android/os/Parcel:recycle	()V
        //   114: aload 4
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	Proxy
        //   0	117	1	paramInt	int
        //   0	117	2	paramBoolean	boolean
        //   1	25	3	i	int
        //   81	17	4	localNetwork	Network
        //   102	13	4	localObject	Object
        //   5	105	5	localParcel1	Parcel
        //   10	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	25	102	finally
        //   33	83	102	finally
      }
      
      /* Error */
      public NetworkInfo getActiveNetworkInfo()
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
        //   15: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: iconst_3
        //   19: aload_2
        //   20: aload_3
        //   21: iconst_0
        //   22: invokeinterface 49 5 0
        //   27: pop
        //   28: aload_3
        //   29: invokevirtual 52	android/os/Parcel:readException	()V
        //   32: aload_3
        //   33: invokevirtual 56	android/os/Parcel:readInt	()I
        //   36: ifeq +26 -> 62
        //   39: getstatic 104	android/net/NetworkInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   42: aload_3
        //   43: invokeinterface 86 2 0
        //   48: checkcast 103	android/net/NetworkInfo
        //   51: astore_1
        //   52: aload_3
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: aload_2
        //   57: invokevirtual 59	android/os/Parcel:recycle	()V
        //   60: aload_1
        //   61: areturn
        //   62: aconst_null
        //   63: astore_1
        //   64: goto -12 -> 52
        //   67: astore_1
        //   68: aload_3
        //   69: invokevirtual 59	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: invokevirtual 59	android/os/Parcel:recycle	()V
        //   76: aload_1
        //   77: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	78	0	this	Proxy
        //   51	13	1	localNetworkInfo	NetworkInfo
        //   67	10	1	localObject	Object
        //   3	70	2	localParcel1	Parcel
        //   7	62	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	52	67	finally
      }
      
      /* Error */
      public NetworkInfo getActiveNetworkInfoForUid(int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 5
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 6
        //   12: aload 5
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload 5
        //   21: iload_1
        //   22: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   25: iload_3
        //   26: istore_1
        //   27: iload_2
        //   28: ifeq +5 -> 33
        //   31: iconst_1
        //   32: istore_1
        //   33: aload 5
        //   35: iload_1
        //   36: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   39: aload_0
        //   40: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   43: iconst_4
        //   44: aload 5
        //   46: aload 6
        //   48: iconst_0
        //   49: invokeinterface 49 5 0
        //   54: pop
        //   55: aload 6
        //   57: invokevirtual 52	android/os/Parcel:readException	()V
        //   60: aload 6
        //   62: invokevirtual 56	android/os/Parcel:readInt	()I
        //   65: ifeq +31 -> 96
        //   68: getstatic 104	android/net/NetworkInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   71: aload 6
        //   73: invokeinterface 86 2 0
        //   78: checkcast 103	android/net/NetworkInfo
        //   81: astore 4
        //   83: aload 6
        //   85: invokevirtual 59	android/os/Parcel:recycle	()V
        //   88: aload 5
        //   90: invokevirtual 59	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: areturn
        //   96: aconst_null
        //   97: astore 4
        //   99: goto -16 -> 83
        //   102: astore 4
        //   104: aload 6
        //   106: invokevirtual 59	android/os/Parcel:recycle	()V
        //   109: aload 5
        //   111: invokevirtual 59	android/os/Parcel:recycle	()V
        //   114: aload 4
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	Proxy
        //   0	117	1	paramInt	int
        //   0	117	2	paramBoolean	boolean
        //   1	25	3	i	int
        //   81	17	4	localNetworkInfo	NetworkInfo
        //   102	13	4	localObject	Object
        //   5	105	5	localParcel1	Parcel
        //   10	95	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	25	102	finally
        //   33	83	102	finally
      }
      
      /* Error */
      public NetworkQuotaInfo getActiveNetworkQuotaInfo()
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
        //   15: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 17
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 49 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 52	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 56	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 111	android/net/NetworkQuotaInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 86 2 0
        //   49: checkcast 110	android/net/NetworkQuotaInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 59	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 59	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 59	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localNetworkQuotaInfo	NetworkQuotaInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public NetworkInfo[] getAllNetworkInfo()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          NetworkInfo[] arrayOfNetworkInfo = (NetworkInfo[])localParcel2.createTypedArray(NetworkInfo.CREATOR);
          return arrayOfNetworkInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public NetworkState[] getAllNetworkState()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
          localParcel2.readException();
          NetworkState[] arrayOfNetworkState = (NetworkState[])localParcel2.createTypedArray(NetworkState.CREATOR);
          return arrayOfNetworkState;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public Network[] getAllNetworks()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          Network[] arrayOfNetwork = (Network[])localParcel2.createTypedArray(Network.CREATOR);
          return arrayOfNetwork;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public VpnInfo[] getAllVpnInfo()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(48, localParcel1, localParcel2, 0);
          localParcel2.readException();
          VpnInfo[] arrayOfVpnInfo = (VpnInfo[])localParcel2.createTypedArray(VpnInfo.CREATOR);
          return arrayOfVpnInfo;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getAlwaysOnVpnPackage(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
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
      
      public String getCaptivePortalServerUrl()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(77, localParcel1, localParcel2, 0);
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
      
      public NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          NetworkCapabilities[] arrayOfNetworkCapabilities = (NetworkCapabilities[])localParcel2.createTypedArray(NetworkCapabilities.CREATOR);
          return arrayOfNetworkCapabilities;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public ProxyInfo getGlobalProxy()
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
        //   15: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   18: bipush 39
        //   20: aload_2
        //   21: aload_3
        //   22: iconst_0
        //   23: invokeinterface 49 5 0
        //   28: pop
        //   29: aload_3
        //   30: invokevirtual 52	android/os/Parcel:readException	()V
        //   33: aload_3
        //   34: invokevirtual 56	android/os/Parcel:readInt	()I
        //   37: ifeq +26 -> 63
        //   40: getstatic 156	android/net/ProxyInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   43: aload_3
        //   44: invokeinterface 86 2 0
        //   49: checkcast 155	android/net/ProxyInfo
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 59	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aconst_null
        //   64: astore_1
        //   65: goto -12 -> 53
        //   68: astore_1
        //   69: aload_3
        //   70: invokevirtual 59	android/os/Parcel:recycle	()V
        //   73: aload_2
        //   74: invokevirtual 59	android/os/Parcel:recycle	()V
        //   77: aload_1
        //   78: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	79	0	this	Proxy
        //   52	13	1	localProxyInfo	ProxyInfo
        //   68	10	1	localObject	Object
        //   3	71	2	localParcel1	Parcel
        //   7	63	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	53	68	finally
      }
      
      public String getInterfaceDescriptor()
      {
        return "android.net.IConnectivityManager";
      }
      
      public int getLastTetherError(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(22, localParcel1, localParcel2, 0);
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
      public LegacyVpnInfo getLegacyVpnInfo(int paramInt)
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
        //   21: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 47
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 49 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 52	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 56	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 164	com/android/internal/net/LegacyVpnInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 86 2 0
        //   59: checkcast 163	com/android/internal/net/LegacyVpnInfo
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 59	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 59	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 59	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localLegacyVpnInfo	LegacyVpnInfo
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public LinkProperties getLinkProperties(Network paramNetwork)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              this.mRemote.transact(14, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetwork = (LinkProperties)LinkProperties.CREATOR.createFromParcel(localParcel2);
                return paramNetwork;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetwork = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public LinkProperties getLinkPropertiesForType(int paramInt)
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
        //   21: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 13
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 49 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 52	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 56	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 92	android/net/LinkProperties:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 86 2 0
        //   59: checkcast 91	android/net/LinkProperties
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 59	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 59	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 59	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localLinkProperties	LinkProperties
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      public String getMobileProvisioningUrl()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(53, localParcel1, localParcel2, 0);
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
      
      public NetworkCapabilities getNetworkCapabilities(Network paramNetwork)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              this.mRemote.transact(15, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetwork = (NetworkCapabilities)NetworkCapabilities.CREATOR.createFromParcel(localParcel2);
                return paramNetwork;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetwork = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public Network getNetworkForType(int paramInt)
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
        //   21: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 8
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 49 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 52	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 56	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 97	android/net/Network:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 86 2 0
        //   59: checkcast 96	android/net/Network
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 59	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 59	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 59	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localNetwork	Network
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public NetworkInfo getNetworkInfo(int paramInt)
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
        //   21: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: iconst_5
        //   25: aload_3
        //   26: aload 4
        //   28: iconst_0
        //   29: invokeinterface 49 5 0
        //   34: pop
        //   35: aload 4
        //   37: invokevirtual 52	android/os/Parcel:readException	()V
        //   40: aload 4
        //   42: invokevirtual 56	android/os/Parcel:readInt	()I
        //   45: ifeq +28 -> 73
        //   48: getstatic 104	android/net/NetworkInfo:CREATOR	Landroid/os/Parcelable$Creator;
        //   51: aload 4
        //   53: invokeinterface 86 2 0
        //   58: checkcast 103	android/net/NetworkInfo
        //   61: astore_2
        //   62: aload 4
        //   64: invokevirtual 59	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 59	android/os/Parcel:recycle	()V
        //   71: aload_2
        //   72: areturn
        //   73: aconst_null
        //   74: astore_2
        //   75: goto -13 -> 62
        //   78: astore_2
        //   79: aload 4
        //   81: invokevirtual 59	android/os/Parcel:recycle	()V
        //   84: aload_3
        //   85: invokevirtual 59	android/os/Parcel:recycle	()V
        //   88: aload_2
        //   89: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	90	0	this	Proxy
        //   0	90	1	paramInt	int
        //   61	14	2	localNetworkInfo	NetworkInfo
        //   78	11	2	localObject	Object
        //   3	82	3	localParcel1	Parcel
        //   7	73	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	62	78	finally
      }
      
      public NetworkInfo getNetworkInfoForUid(Network paramNetwork, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt);
              if (paramBoolean)
              {
                paramInt = i;
                localParcel1.writeInt(paramInt);
                this.mRemote.transact(6, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label139;
                }
                paramNetwork = (NetworkInfo)NetworkInfo.CREATOR.createFromParcel(localParcel2);
                return paramNetwork;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramInt = 0;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          continue;
          label139:
          paramNetwork = null;
        }
      }
      
      public ProxyInfo getProxyForNetwork(Network paramNetwork)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              this.mRemote.transact(41, localParcel1, localParcel2, 0);
              localParcel2.readException();
              if (localParcel2.readInt() != 0)
              {
                paramNetwork = (ProxyInfo)ProxyInfo.CREATOR.createFromParcel(localParcel2);
                return paramNetwork;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramNetwork = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public int getRestoreDefaultNetworkDelay(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(70, localParcel1, localParcel2, 0);
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
      
      public List<WifiDevice> getTetherConnectedSta()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(34, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(WifiDevice.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public List<WifiDevice> getTetherSoftApSta(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(35, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.createTypedArrayList(WifiDevice.CREATOR);
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String[] getTetherableBluetoothRegexs()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(32, localParcel1, localParcel2, 0);
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
      
      public String[] getTetherableIfaces()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(26, localParcel1, localParcel2, 0);
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
      
      public String[] getTetherableUsbRegexs()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(30, localParcel1, localParcel2, 0);
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
      
      public String[] getTetherableWifiRegexs()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(31, localParcel1, localParcel2, 0);
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
      
      public String[] getTetheredDhcpRanges()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(29, localParcel1, localParcel2, 0);
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
      
      public String[] getTetheredIfaces()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
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
      
      public String[] getTetheringErroredIfaces()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          this.mRemote.transact(28, localParcel1, localParcel2, 0);
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
      public VpnConfig getVpnConfig(int paramInt)
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
        //   21: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 45
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 49 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 52	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 56	android/os/Parcel:readInt	()I
        //   46: ifeq +28 -> 74
        //   49: getstatic 209	com/android/internal/net/VpnConfig:CREATOR	Landroid/os/Parcelable$Creator;
        //   52: aload 4
        //   54: invokeinterface 86 2 0
        //   59: checkcast 70	com/android/internal/net/VpnConfig
        //   62: astore_2
        //   63: aload 4
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: aload_3
        //   69: invokevirtual 59	android/os/Parcel:recycle	()V
        //   72: aload_2
        //   73: areturn
        //   74: aconst_null
        //   75: astore_2
        //   76: goto -13 -> 63
        //   79: astore_2
        //   80: aload 4
        //   82: invokevirtual 59	android/os/Parcel:recycle	()V
        //   85: aload_3
        //   86: invokevirtual 59	android/os/Parcel:recycle	()V
        //   89: aload_2
        //   90: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	91	0	this	Proxy
        //   0	91	1	paramInt	int
        //   62	14	2	localVpnConfig	VpnConfig
        //   79	11	2	localObject	Object
        //   3	83	3	localParcel1	Parcel
        //   7	74	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	63	79	finally
      }
      
      /* Error */
      public boolean isActiveNetworkMetered()
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
        //   16: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 18
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 56	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 59	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 59	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 59	android/os/Parcel:recycle	()V
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
      public boolean isNetworkSupported(int paramInt)
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
        //   21: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   24: bipush 11
        //   26: aload_3
        //   27: aload 4
        //   29: iconst_0
        //   30: invokeinterface 49 5 0
        //   35: pop
        //   36: aload 4
        //   38: invokevirtual 52	android/os/Parcel:readException	()V
        //   41: aload 4
        //   43: invokevirtual 56	android/os/Parcel:readInt	()I
        //   46: istore_1
        //   47: iload_1
        //   48: ifeq +16 -> 64
        //   51: iconst_1
        //   52: istore_2
        //   53: aload 4
        //   55: invokevirtual 59	android/os/Parcel:recycle	()V
        //   58: aload_3
        //   59: invokevirtual 59	android/os/Parcel:recycle	()V
        //   62: iload_2
        //   63: ireturn
        //   64: iconst_0
        //   65: istore_2
        //   66: goto -13 -> 53
        //   69: astore 5
        //   71: aload 4
        //   73: invokevirtual 59	android/os/Parcel:recycle	()V
        //   76: aload_3
        //   77: invokevirtual 59	android/os/Parcel:recycle	()V
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
      public boolean isTetheringSupported()
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
        //   16: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 23
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 56	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 59	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 59	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 59	android/os/Parcel:recycle	()V
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
      
      public NetworkRequest listenForNetwork(NetworkCapabilities paramNetworkCapabilities, Messenger paramMessenger, IBinder paramIBinder)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetworkCapabilities != null)
            {
              localParcel1.writeInt(1);
              paramNetworkCapabilities.writeToParcel(localParcel1, 0);
              if (paramMessenger != null)
              {
                localParcel1.writeInt(1);
                paramMessenger.writeToParcel(localParcel1, 0);
                localParcel1.writeStrongBinder(paramIBinder);
                this.mRemote.transact(63, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label144;
                }
                paramNetworkCapabilities = (NetworkRequest)NetworkRequest.CREATOR.createFromParcel(localParcel2);
                return paramNetworkCapabilities;
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
          paramNetworkCapabilities = null;
        }
      }
      
      public void pendingListenForNetwork(NetworkCapabilities paramNetworkCapabilities, PendingIntent paramPendingIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetworkCapabilities != null)
            {
              localParcel1.writeInt(1);
              paramNetworkCapabilities.writeToParcel(localParcel1, 0);
              if (paramPendingIntent != null)
              {
                localParcel1.writeInt(1);
                paramPendingIntent.writeToParcel(localParcel1, 0);
                this.mRemote.transact(64, localParcel1, localParcel2, 0);
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
      
      public NetworkRequest pendingRequestForNetwork(NetworkCapabilities paramNetworkCapabilities, PendingIntent paramPendingIntent)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetworkCapabilities != null)
            {
              localParcel1.writeInt(1);
              paramNetworkCapabilities.writeToParcel(localParcel1, 0);
              if (paramPendingIntent != null)
              {
                localParcel1.writeInt(1);
                paramPendingIntent.writeToParcel(localParcel1, 0);
                this.mRemote.transact(61, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label127;
                }
                paramNetworkCapabilities = (NetworkRequest)NetworkRequest.CREATOR.createFromParcel(localParcel2);
                return paramNetworkCapabilities;
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
          label127:
          paramNetworkCapabilities = null;
        }
      }
      
      /* Error */
      public boolean prepareVpn(String paramString1, String paramString2, int paramInt)
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
        //   20: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 5
        //   25: aload_2
        //   26: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload 5
        //   31: iload_3
        //   32: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 42
        //   41: aload 5
        //   43: aload 6
        //   45: iconst_0
        //   46: invokeinterface 49 5 0
        //   51: pop
        //   52: aload 6
        //   54: invokevirtual 52	android/os/Parcel:readException	()V
        //   57: aload 6
        //   59: invokevirtual 56	android/os/Parcel:readInt	()I
        //   62: istore_3
        //   63: iload_3
        //   64: ifeq +19 -> 83
        //   67: iconst_1
        //   68: istore 4
        //   70: aload 6
        //   72: invokevirtual 59	android/os/Parcel:recycle	()V
        //   75: aload 5
        //   77: invokevirtual 59	android/os/Parcel:recycle	()V
        //   80: iload 4
        //   82: ireturn
        //   83: iconst_0
        //   84: istore 4
        //   86: goto -16 -> 70
        //   89: astore_1
        //   90: aload 6
        //   92: invokevirtual 59	android/os/Parcel:recycle	()V
        //   95: aload 5
        //   97: invokevirtual 59	android/os/Parcel:recycle	()V
        //   100: aload_1
        //   101: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	102	0	this	Proxy
        //   0	102	1	paramString1	String
        //   0	102	2	paramString2	String
        //   0	102	3	paramInt	int
        //   68	17	4	bool	boolean
        //   3	93	5	localParcel1	Parcel
        //   8	83	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	63	89	finally
      }
      
      public int registerNetworkAgent(Messenger paramMessenger, NetworkInfo paramNetworkInfo, LinkProperties paramLinkProperties, NetworkCapabilities paramNetworkCapabilities, int paramInt, NetworkMisc paramNetworkMisc)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramMessenger != null)
            {
              localParcel1.writeInt(1);
              paramMessenger.writeToParcel(localParcel1, 0);
              if (paramNetworkInfo != null)
              {
                localParcel1.writeInt(1);
                paramNetworkInfo.writeToParcel(localParcel1, 0);
                if (paramLinkProperties == null) {
                  break label186;
                }
                localParcel1.writeInt(1);
                paramLinkProperties.writeToParcel(localParcel1, 0);
                if (paramNetworkCapabilities == null) {
                  break label195;
                }
                localParcel1.writeInt(1);
                paramNetworkCapabilities.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt);
                if (paramNetworkMisc == null) {
                  break label204;
                }
                localParcel1.writeInt(1);
                paramNetworkMisc.writeToParcel(localParcel1, 0);
                this.mRemote.transact(59, localParcel1, localParcel2, 0);
                localParcel2.readException();
                paramInt = localParcel2.readInt();
                return paramInt;
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
          label186:
          localParcel1.writeInt(0);
          continue;
          label195:
          localParcel1.writeInt(0);
          continue;
          label204:
          localParcel1.writeInt(0);
        }
      }
      
      /* Error */
      public void registerNetworkFactory(Messenger paramMessenger, String paramString)
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
        //   27: invokevirtual 220	android/os/Messenger:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 56
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 59	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 59	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 59	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 59	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramMessenger	Messenger
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
      
      /* Error */
      public void releaseNetworkRequest(NetworkRequest paramNetworkRequest)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 247	android/net/NetworkRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 67
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramNetworkRequest	NetworkRequest
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void releasePendingNetworkRequest(PendingIntent paramPendingIntent)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 231	android/app/PendingIntent:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 62
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramPendingIntent	PendingIntent
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public boolean removeVpnAddress(String paramString, int paramInt)
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
        //   23: aload 4
        //   25: iload_2
        //   26: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 72
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 49 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 52	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 56	android/os/Parcel:readInt	()I
        //   56: istore_2
        //   57: iload_2
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 59	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_1
        //   81: aload 5
        //   83: invokevirtual 59	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 59	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramString	String
        //   0	93	2	paramInt	int
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public void reportInetCondition(int paramInt1, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeInt(paramInt1);
          localParcel1.writeInt(paramInt2);
          this.mRemote.transact(37, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void reportNetworkConnectivity(Network paramNetwork, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              break label105;
              localParcel1.writeInt(i);
              this.mRemote.transact(38, localParcel1, localParcel2, 0);
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
      
      public boolean requestBandwidthUpdate(Network paramNetwork)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              this.mRemote.transact(57, localParcel1, localParcel2, 0);
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
      
      /* Error */
      public void requestLinkProperties(NetworkRequest paramNetworkRequest)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 247	android/net/NetworkRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 65
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramNetworkRequest	NetworkRequest
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public NetworkRequest requestNetwork(NetworkCapabilities paramNetworkCapabilities, Messenger paramMessenger, int paramInt1, IBinder paramIBinder, int paramInt2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetworkCapabilities != null)
            {
              localParcel1.writeInt(1);
              paramNetworkCapabilities.writeToParcel(localParcel1, 0);
              if (paramMessenger != null)
              {
                localParcel1.writeInt(1);
                paramMessenger.writeToParcel(localParcel1, 0);
                localParcel1.writeInt(paramInt1);
                localParcel1.writeStrongBinder(paramIBinder);
                localParcel1.writeInt(paramInt2);
                this.mRemote.transact(60, localParcel1, localParcel2, 0);
                localParcel2.readException();
                if (localParcel2.readInt() == 0) {
                  break label158;
                }
                paramNetworkCapabilities = (NetworkRequest)NetworkRequest.CREATOR.createFromParcel(localParcel2);
                return paramNetworkCapabilities;
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
          label158:
          paramNetworkCapabilities = null;
        }
      }
      
      /* Error */
      public void requestNetworkCapabilities(NetworkRequest paramNetworkRequest)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 247	android/net/NetworkRequest:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 66
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramNetworkRequest	NetworkRequest
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public boolean requestRouteToHostAddress(int paramInt, byte[] paramArrayOfByte)
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
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 266	android/os/Parcel:writeByteArray	([B)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 19
        //   35: aload 4
        //   37: aload 5
        //   39: iconst_0
        //   40: invokeinterface 49 5 0
        //   45: pop
        //   46: aload 5
        //   48: invokevirtual 52	android/os/Parcel:readException	()V
        //   51: aload 5
        //   53: invokevirtual 56	android/os/Parcel:readInt	()I
        //   56: istore_1
        //   57: iload_1
        //   58: ifeq +17 -> 75
        //   61: iconst_1
        //   62: istore_3
        //   63: aload 5
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: aload 4
        //   70: invokevirtual 59	android/os/Parcel:recycle	()V
        //   73: iload_3
        //   74: ireturn
        //   75: iconst_0
        //   76: istore_3
        //   77: goto -14 -> 63
        //   80: astore_2
        //   81: aload 5
        //   83: invokevirtual 59	android/os/Parcel:recycle	()V
        //   86: aload 4
        //   88: invokevirtual 59	android/os/Parcel:recycle	()V
        //   91: aload_2
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	Proxy
        //   0	93	1	paramInt	int
        //   0	93	2	paramArrayOfByte	byte[]
        //   62	15	3	bool	boolean
        //   3	84	4	localParcel1	Parcel
        //   8	74	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	57	80	finally
      }
      
      public void setAcceptUnvalidated(Network paramNetwork, boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        int j = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              break label129;
              localParcel1.writeInt(i);
              if (paramBoolean2)
              {
                i = j;
                label55:
                localParcel1.writeInt(i);
                this.mRemote.transact(68, localParcel1, localParcel2, 0);
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
          label129:
          do
          {
            i = 0;
            break;
            i = 0;
            break label55;
          } while (!paramBoolean1);
          int i = 1;
        }
      }
      
      public void setAirplaneMode(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      /* Error */
      public boolean setAlwaysOnVpnPackage(int paramInt, String paramString, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
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
        //   26: aload 5
        //   28: aload_2
        //   29: invokevirtual 39	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   32: iload 4
        //   34: istore_1
        //   35: iload_3
        //   36: ifeq +5 -> 41
        //   39: iconst_1
        //   40: istore_1
        //   41: aload 5
        //   43: iload_1
        //   44: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   47: aload_0
        //   48: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   51: bipush 50
        //   53: aload 5
        //   55: aload 6
        //   57: iconst_0
        //   58: invokeinterface 49 5 0
        //   63: pop
        //   64: aload 6
        //   66: invokevirtual 52	android/os/Parcel:readException	()V
        //   69: aload 6
        //   71: invokevirtual 56	android/os/Parcel:readInt	()I
        //   74: istore_1
        //   75: iload_1
        //   76: ifeq +17 -> 93
        //   79: iconst_1
        //   80: istore_3
        //   81: aload 6
        //   83: invokevirtual 59	android/os/Parcel:recycle	()V
        //   86: aload 5
        //   88: invokevirtual 59	android/os/Parcel:recycle	()V
        //   91: iload_3
        //   92: ireturn
        //   93: iconst_0
        //   94: istore_3
        //   95: goto -14 -> 81
        //   98: astore_2
        //   99: aload 6
        //   101: invokevirtual 59	android/os/Parcel:recycle	()V
        //   104: aload 5
        //   106: invokevirtual 59	android/os/Parcel:recycle	()V
        //   109: aload_2
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	Proxy
        //   0	111	1	paramInt	int
        //   0	111	2	paramString	String
        //   0	111	3	paramBoolean	boolean
        //   1	32	4	i	int
        //   6	99	5	localParcel1	Parcel
        //   11	89	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	98	finally
        //   41	75	98	finally
      }
      
      /* Error */
      public void setAvoidUnvalidated(Network paramNetwork)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 167	android/net/Network:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 69
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramNetwork	Network
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void setGlobalProxy(ProxyInfo paramProxyInfo)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 277	android/net/ProxyInfo:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 40
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramProxyInfo	ProxyInfo
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void setProvisioningNotificationVisible(boolean paramBoolean, int paramInt, String paramString)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          localParcel1.writeInt(paramInt);
          localParcel1.writeString(paramString);
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
      public boolean setUnderlyingNetworksForVpn(Network[] paramArrayOfNetwork)
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
        //   20: iconst_0
        //   21: invokevirtual 285	android/os/Parcel:writeTypedArray	([Landroid/os/Parcelable;I)V
        //   24: aload_0
        //   25: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   28: bipush 73
        //   30: aload 4
        //   32: aload 5
        //   34: iconst_0
        //   35: invokeinterface 49 5 0
        //   40: pop
        //   41: aload 5
        //   43: invokevirtual 52	android/os/Parcel:readException	()V
        //   46: aload 5
        //   48: invokevirtual 56	android/os/Parcel:readInt	()I
        //   51: istore_2
        //   52: iload_2
        //   53: ifeq +17 -> 70
        //   56: iconst_1
        //   57: istore_3
        //   58: aload 5
        //   60: invokevirtual 59	android/os/Parcel:recycle	()V
        //   63: aload 4
        //   65: invokevirtual 59	android/os/Parcel:recycle	()V
        //   68: iload_3
        //   69: ireturn
        //   70: iconst_0
        //   71: istore_3
        //   72: goto -14 -> 58
        //   75: astore_1
        //   76: aload 5
        //   78: invokevirtual 59	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 59	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramArrayOfNetwork	Network[]
        //   51	2	2	i	int
        //   57	15	3	bool	boolean
        //   3	79	4	localParcel1	Parcel
        //   8	69	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	52	75	finally
      }
      
      public int setUsbTethering(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.mRemote.transact(33, localParcel1, localParcel2, 0);
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
      
      public void setVpnPackageAuthorization(String paramString, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeString(paramString);
          localParcel1.writeInt(paramInt);
          paramInt = i;
          if (paramBoolean) {
            paramInt = 1;
          }
          localParcel1.writeInt(paramInt);
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
      
      /* Error */
      public void startLegacyVpn(VpnProfile paramVpnProfile)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 294	com/android/internal/net/VpnProfile:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 46
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramVpnProfile	VpnProfile
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void startNattKeepalive(Network paramNetwork, int paramInt1, Messenger paramMessenger, IBinder paramIBinder, String paramString1, int paramInt2, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            if (paramNetwork != null)
            {
              localParcel1.writeInt(1);
              paramNetwork.writeToParcel(localParcel1, 0);
              localParcel1.writeInt(paramInt1);
              if (paramMessenger != null)
              {
                localParcel1.writeInt(1);
                paramMessenger.writeToParcel(localParcel1, 0);
                localParcel1.writeStrongBinder(paramIBinder);
                localParcel1.writeString(paramString1);
                localParcel1.writeInt(paramInt2);
                localParcel1.writeString(paramString2);
                this.mRemote.transact(75, localParcel1, localParcel2, 0);
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
      
      public void startTethering(int paramInt, ResultReceiver paramResultReceiver, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
            localParcel1.writeInt(paramInt);
            if (paramResultReceiver != null)
            {
              localParcel1.writeInt(1);
              paramResultReceiver.writeToParcel(localParcel1, 0);
              break label112;
              localParcel1.writeInt(paramInt);
              this.mRemote.transact(24, localParcel1, localParcel2, 0);
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
          label112:
          do
          {
            paramInt = 0;
            break;
          } while (!paramBoolean);
          paramInt = i;
        }
      }
      
      /* Error */
      public void stopKeepalive(Network paramNetwork, int paramInt)
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
        //   27: invokevirtual 167	android/net/Network:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: iload_2
        //   32: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   35: aload_0
        //   36: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   39: bipush 76
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 49 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 52	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 59	android/os/Parcel:recycle	()V
        //   61: aload_3
        //   62: invokevirtual 59	android/os/Parcel:recycle	()V
        //   65: return
        //   66: aload_3
        //   67: iconst_0
        //   68: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   71: goto -41 -> 30
        //   74: astore_1
        //   75: aload 4
        //   77: invokevirtual 59	android/os/Parcel:recycle	()V
        //   80: aload_3
        //   81: invokevirtual 59	android/os/Parcel:recycle	()V
        //   84: aload_1
        //   85: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	86	0	this	Proxy
        //   0	86	1	paramNetwork	Network
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
      
      public void stopTethering(int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeInt(paramInt);
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
      
      public int tether(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(20, localParcel1, localParcel2, 0);
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
      public void unregisterNetworkFactory(Messenger paramMessenger)
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
        //   20: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 220	android/os/Messenger:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 58
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 43	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 59	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 59	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramMessenger	Messenger
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public int untether(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("android.net.IConnectivityManager");
          localParcel1.writeString(paramString);
          this.mRemote.transact(21, localParcel1, localParcel2, 0);
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
      public boolean updateLockdownVpn()
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
        //   16: getfield 19	android/net/IConnectivityManager$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   19: bipush 49
        //   21: aload_3
        //   22: aload 4
        //   24: iconst_0
        //   25: invokeinterface 49 5 0
        //   30: pop
        //   31: aload 4
        //   33: invokevirtual 52	android/os/Parcel:readException	()V
        //   36: aload 4
        //   38: invokevirtual 56	android/os/Parcel:readInt	()I
        //   41: istore_1
        //   42: iload_1
        //   43: ifeq +16 -> 59
        //   46: iconst_1
        //   47: istore_2
        //   48: aload 4
        //   50: invokevirtual 59	android/os/Parcel:recycle	()V
        //   53: aload_3
        //   54: invokevirtual 59	android/os/Parcel:recycle	()V
        //   57: iload_2
        //   58: ireturn
        //   59: iconst_0
        //   60: istore_2
        //   61: goto -13 -> 48
        //   64: astore 5
        //   66: aload 4
        //   68: invokevirtual 59	android/os/Parcel:recycle	()V
        //   71: aload_3
        //   72: invokevirtual 59	android/os/Parcel:recycle	()V
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
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/IConnectivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */