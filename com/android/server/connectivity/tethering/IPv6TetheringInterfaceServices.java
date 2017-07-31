package com.android.server.connectivity.tethering;

import android.net.INetd;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.net.ip.RouterAdvertisementDaemon;
import android.net.ip.RouterAdvertisementDaemon.RaParams;
import android.net.util.NetdService;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.util.Log;
import android.util.Slog;
import java.net.Inet6Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

class IPv6TetheringInterfaceServices
{
  private static final IpPrefix LINK_LOCAL_PREFIX = new IpPrefix("fe80::/64");
  private static final int RFC7421_IP_PREFIX_LENGTH = 64;
  private static final String TAG = IPv6TetheringInterfaceServices.class.getSimpleName();
  private byte[] mHwAddr;
  private final String mIfName;
  private LinkProperties mLastIPv6LinkProperties;
  private RouterAdvertisementDaemon.RaParams mLastRaParams;
  private final INetworkManagementService mNMService;
  private NetworkInterface mNetworkInterface;
  private RouterAdvertisementDaemon mRaDaemon;
  
  IPv6TetheringInterfaceServices(String paramString, INetworkManagementService paramINetworkManagementService)
  {
    this.mIfName = paramString;
    this.mNMService = paramINetworkManagementService;
  }
  
  private void configureLocalDns(HashSet<Inet6Address> paramHashSet1, HashSet<Inet6Address> paramHashSet2)
  {
    INetd localINetd = NetdService.getInstance();
    if (localINetd == null)
    {
      if (paramHashSet2 != null) {
        paramHashSet2.clear();
      }
      Log.e(TAG, "No netd service instance available; not setting local IPv6 addresses");
      return;
    }
    Object localObject;
    if (!paramHashSet1.isEmpty())
    {
      paramHashSet1 = paramHashSet1.iterator();
      while (paramHashSet1.hasNext())
      {
        localObject = ((Inet6Address)paramHashSet1.next()).getHostAddress();
        try
        {
          localINetd.interfaceDelAddress(this.mIfName, (String)localObject, 64);
        }
        catch (ServiceSpecificException|RemoteException localServiceSpecificException1)
        {
          Log.e(TAG, "Failed to remove local dns IP: " + (String)localObject, localServiceSpecificException1);
        }
      }
    }
    if ((paramHashSet2 == null) || (paramHashSet2.isEmpty())) {}
    try
    {
      localINetd.tetherApplyDnsInterfaces();
      return;
      paramHashSet1 = (HashSet)paramHashSet2.clone();
      if (this.mLastRaParams != null) {
        paramHashSet1.removeAll(this.mLastRaParams.dnses);
      }
      paramHashSet1 = paramHashSet1.iterator();
      while (paramHashSet1.hasNext())
      {
        localObject = (Inet6Address)paramHashSet1.next();
        String str = ((Inet6Address)localObject).getHostAddress();
        try
        {
          localINetd.interfaceAddAddress(this.mIfName, str, 64);
        }
        catch (ServiceSpecificException|RemoteException localServiceSpecificException2)
        {
          Log.e(TAG, "Failed to add local dns IP: " + str, localServiceSpecificException2);
          paramHashSet2.remove(localObject);
        }
      }
    }
    catch (ServiceSpecificException|RemoteException paramHashSet1)
    {
      do
      {
        Log.e(TAG, "Failed to update local DNS caching server");
      } while (paramHashSet2 == null);
      paramHashSet2.clear();
    }
  }
  
  private void configureLocalRoutes(HashSet<IpPrefix> paramHashSet1, HashSet<IpPrefix> paramHashSet2)
  {
    if (!paramHashSet1.isEmpty()) {
      paramHashSet1 = getLocalRoutesFor(paramHashSet1);
    }
    try
    {
      int i = this.mNMService.removeRoutesFromLocalNetwork(paramHashSet1);
      if (i > 0) {
        Log.e(TAG, String.format("Failed to remove %d IPv6 routes from local table.", new Object[] { Integer.valueOf(i) }));
      }
    }
    catch (RemoteException paramHashSet1)
    {
      do
      {
        for (;;)
        {
          Log.e(TAG, "Failed to remove IPv6 routes from local table: ", paramHashSet1);
        }
        paramHashSet1 = (HashSet)paramHashSet2.clone();
        if (this.mLastRaParams != null) {
          paramHashSet1.removeAll(this.mLastRaParams.prefixes);
        }
        if ((this.mLastRaParams == null) || (this.mLastRaParams.prefixes.isEmpty())) {
          paramHashSet1.add(LINK_LOCAL_PREFIX);
        }
      } while (paramHashSet1.isEmpty());
      paramHashSet1 = getLocalRoutesFor(paramHashSet1);
      try
      {
        this.mNMService.addInterfaceToLocalNetwork(this.mIfName, paramHashSet1);
        return;
      }
      catch (RemoteException paramHashSet1)
      {
        Log.e(TAG, "Failed to add IPv6 routes to local table: ", paramHashSet1);
      }
    }
    if ((paramHashSet2 == null) || (paramHashSet2.isEmpty())) {
      return;
    }
  }
  
  private static Inet6Address getLocalDnsIpFor(IpPrefix paramIpPrefix)
  {
    Object localObject = paramIpPrefix.getRawAddress();
    localObject[(localObject.length - 1)] = 1;
    try
    {
      localObject = Inet6Address.getByAddress(null, (byte[])localObject, 0);
      return (Inet6Address)localObject;
    }
    catch (UnknownHostException localUnknownHostException)
    {
      Slog.wtf(TAG, "Failed to construct Inet6Address from: " + paramIpPrefix);
    }
    return null;
  }
  
  private ArrayList<RouteInfo> getLocalRoutesFor(HashSet<IpPrefix> paramHashSet)
  {
    ArrayList localArrayList = new ArrayList();
    paramHashSet = paramHashSet.iterator();
    while (paramHashSet.hasNext()) {
      localArrayList.add(new RouteInfo((IpPrefix)paramHashSet.next(), null, this.mIfName));
    }
    return localArrayList;
  }
  
  private void setRaParams(RouterAdvertisementDaemon.RaParams paramRaParams)
  {
    Object localObject2 = null;
    RouterAdvertisementDaemon.RaParams localRaParams;
    HashSet localHashSet;
    if (this.mRaDaemon != null)
    {
      localRaParams = RouterAdvertisementDaemon.RaParams.getDeprecatedRaParams(this.mLastRaParams, paramRaParams);
      localHashSet = localRaParams.prefixes;
      if (paramRaParams == null) {
        break label83;
      }
    }
    label83:
    for (Object localObject1 = paramRaParams.prefixes;; localObject1 = null)
    {
      configureLocalRoutes(localHashSet, (HashSet)localObject1);
      localHashSet = localRaParams.dnses;
      localObject1 = localObject2;
      if (paramRaParams != null) {
        localObject1 = paramRaParams.dnses;
      }
      configureLocalDns(localHashSet, (HashSet)localObject1);
      this.mRaDaemon.buildNewRa(localRaParams, paramRaParams);
      this.mLastRaParams = paramRaParams;
      return;
    }
  }
  
  public boolean start()
  {
    try
    {
      this.mNetworkInterface = NetworkInterface.getByName(this.mIfName);
      int i;
      return true;
    }
    catch (SocketException localSocketException1)
    {
      try
      {
        this.mHwAddr = this.mNetworkInterface.getHardwareAddress();
        i = this.mNetworkInterface.getIndex();
        this.mRaDaemon = new RouterAdvertisementDaemon(this.mIfName, i, this.mHwAddr);
        if (this.mRaDaemon.start()) {
          break label142;
        }
        stop();
        return false;
      }
      catch (SocketException localSocketException2)
      {
        Log.e(TAG, "Failed to find hardware address for " + this.mIfName, localSocketException2);
        stop();
        return false;
      }
      localSocketException1 = localSocketException1;
      Log.e(TAG, "Failed to find NetworkInterface for " + this.mIfName, localSocketException1);
      stop();
      return false;
    }
  }
  
  public void stop()
  {
    this.mNetworkInterface = null;
    this.mHwAddr = null;
    setRaParams(null);
    if (this.mRaDaemon != null)
    {
      this.mRaDaemon.stop();
      this.mRaDaemon = null;
    }
  }
  
  public void updateUpstreamIPv6LinkProperties(LinkProperties paramLinkProperties)
  {
    if (this.mRaDaemon == null) {
      return;
    }
    if (Objects.equals(this.mLastIPv6LinkProperties, paramLinkProperties)) {
      return;
    }
    Object localObject = null;
    if (paramLinkProperties != null)
    {
      RouterAdvertisementDaemon.RaParams localRaParams = new RouterAdvertisementDaemon.RaParams();
      localRaParams.mtu = paramLinkProperties.getMtu();
      localRaParams.hasDefaultRoute = paramLinkProperties.hasIPv6DefaultRoute();
      Iterator localIterator = paramLinkProperties.getLinkAddresses().iterator();
      for (;;)
      {
        localObject = localRaParams;
        if (!localIterator.hasNext()) {
          break;
        }
        localObject = (LinkAddress)localIterator.next();
        if (((LinkAddress)localObject).getPrefixLength() == 64)
        {
          localObject = new IpPrefix(((LinkAddress)localObject).getAddress(), ((LinkAddress)localObject).getPrefixLength());
          localRaParams.prefixes.add(localObject);
          localObject = getLocalDnsIpFor((IpPrefix)localObject);
          if (localObject != null) {
            localRaParams.dnses.add(localObject);
          }
        }
      }
    }
    setRaParams((RouterAdvertisementDaemon.RaParams)localObject);
    this.mLastIPv6LinkProperties = paramLinkProperties;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/tethering/IPv6TetheringInterfaceServices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */