package com.android.server.connectivity.tethering;

import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkState;
import android.net.RouteInfo;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class IPv6TetheringCoordinator
{
  private static final boolean DBG = false;
  private static final String TAG = IPv6TetheringCoordinator.class.getSimpleName();
  private static final boolean VDBG = false;
  private final LinkedList<TetherInterfaceStateMachine> mActiveDownstreams;
  private final ArrayList<TetherInterfaceStateMachine> mNotifyList;
  private NetworkState mUpstreamNetworkState;
  
  public IPv6TetheringCoordinator(ArrayList<TetherInterfaceStateMachine> paramArrayList)
  {
    this.mNotifyList = paramArrayList;
    this.mActiveDownstreams = new LinkedList();
  }
  
  private static boolean canTetherIPv6(NetworkState paramNetworkState)
  {
    boolean bool3 = false;
    boolean bool2;
    Object localObject2;
    Object localObject1;
    Object localObject3;
    RouteInfo localRouteInfo;
    if ((paramNetworkState != null) && (paramNetworkState.network != null) && (paramNetworkState.linkProperties != null) && (paramNetworkState.networkCapabilities != null) && (paramNetworkState.linkProperties.isProvisioned()) && (paramNetworkState.linkProperties.hasIPv6DefaultRoute()) && (paramNetworkState.linkProperties.hasGlobalIPv6Address()))
    {
      bool2 = paramNetworkState.networkCapabilities.hasTransport(0);
      localObject2 = null;
      localObject1 = null;
      localObject3 = null;
      localRouteInfo = null;
      if (bool2)
      {
        Iterator localIterator = paramNetworkState.linkProperties.getAllRoutes().iterator();
        paramNetworkState = localRouteInfo;
        label143:
        do
        {
          do
          {
            localObject2 = localObject1;
            localObject3 = paramNetworkState;
            if (!localIterator.hasNext()) {
              break;
            }
            localRouteInfo = (RouteInfo)localIterator.next();
            if (!localRouteInfo.isIPv4Default()) {
              break label212;
            }
            localObject2 = localRouteInfo;
            localObject3 = paramNetworkState;
            localObject1 = localObject2;
            paramNetworkState = (NetworkState)localObject3;
          } while (localObject2 == null);
          localObject1 = localObject2;
          paramNetworkState = (NetworkState)localObject3;
        } while (localObject3 == null);
      }
      if ((localObject2 == null) || (localObject3 == null) || (((RouteInfo)localObject2).getInterface() == null)) {
        break label238;
      }
    }
    label212:
    label238:
    for (boolean bool1 = ((RouteInfo)localObject2).getInterface().equals(((RouteInfo)localObject3).getInterface());; bool1 = false)
    {
      if (bool2) {
        bool3 = bool1;
      }
      return bool3;
      bool2 = false;
      break;
      localObject2 = localObject1;
      localObject3 = paramNetworkState;
      if (!localRouteInfo.isIPv6Default()) {
        break label143;
      }
      localObject3 = localRouteInfo;
      localObject2 = localObject1;
      break label143;
    }
  }
  
  private static LinkProperties getIPv6OnlyLinkProperties(LinkProperties paramLinkProperties)
  {
    LinkProperties localLinkProperties = new LinkProperties();
    if (paramLinkProperties == null) {
      return localLinkProperties;
    }
    localLinkProperties.setInterfaceName(paramLinkProperties.getInterfaceName());
    localLinkProperties.setMtu(paramLinkProperties.getMtu());
    Iterator localIterator = paramLinkProperties.getLinkAddresses().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (LinkAddress)localIterator.next();
      if ((((LinkAddress)localObject).isGlobalPreferred()) && (((LinkAddress)localObject).getPrefixLength() == 64)) {
        localLinkProperties.addLinkAddress((LinkAddress)localObject);
      }
    }
    localIterator = paramLinkProperties.getRoutes().iterator();
    while (localIterator.hasNext())
    {
      localObject = (RouteInfo)localIterator.next();
      IpPrefix localIpPrefix = ((RouteInfo)localObject).getDestination();
      if (((localIpPrefix.getAddress() instanceof Inet6Address)) && (localIpPrefix.getPrefixLength() <= 64)) {
        localLinkProperties.addRoute((RouteInfo)localObject);
      }
    }
    localIterator = paramLinkProperties.getDnsServers().iterator();
    while (localIterator.hasNext())
    {
      localObject = (InetAddress)localIterator.next();
      if (isIPv6GlobalAddress((InetAddress)localObject)) {
        localLinkProperties.addDnsServer((InetAddress)localObject);
      }
    }
    localLinkProperties.setDomains(paramLinkProperties.getDomains());
    return localLinkProperties;
  }
  
  private LinkProperties getInterfaceIPv6LinkProperties(TetherInterfaceStateMachine paramTetherInterfaceStateMachine)
  {
    if (this.mUpstreamNetworkState == null) {
      return null;
    }
    if (paramTetherInterfaceStateMachine.interfaceType() == 2) {
      return null;
    }
    TetherInterfaceStateMachine localTetherInterfaceStateMachine = (TetherInterfaceStateMachine)this.mActiveDownstreams.peek();
    if ((localTetherInterfaceStateMachine != null) && (localTetherInterfaceStateMachine == paramTetherInterfaceStateMachine))
    {
      paramTetherInterfaceStateMachine = getIPv6OnlyLinkProperties(this.mUpstreamNetworkState.linkProperties);
      if ((paramTetherInterfaceStateMachine.hasIPv6DefaultRoute()) && (paramTetherInterfaceStateMachine.hasGlobalIPv6Address())) {
        return paramTetherInterfaceStateMachine;
      }
    }
    return null;
  }
  
  private static boolean isIPv6GlobalAddress(InetAddress paramInetAddress)
  {
    if ((!(paramInetAddress instanceof Inet6Address)) || (paramInetAddress.isAnyLocalAddress())) {}
    while ((paramInetAddress.isLoopbackAddress()) || (paramInetAddress.isLinkLocalAddress()) || (paramInetAddress.isSiteLocalAddress()) || (paramInetAddress.isMulticastAddress())) {
      return false;
    }
    return true;
  }
  
  private void setUpstreamNetworkState(NetworkState paramNetworkState)
  {
    if (paramNetworkState == null)
    {
      this.mUpstreamNetworkState = null;
      return;
    }
    this.mUpstreamNetworkState = new NetworkState(null, new LinkProperties(paramNetworkState.linkProperties), new NetworkCapabilities(paramNetworkState.networkCapabilities), new Network(paramNetworkState.network), null, null);
  }
  
  private static void stopIPv6TetheringOn(TetherInterfaceStateMachine paramTetherInterfaceStateMachine)
  {
    paramTetherInterfaceStateMachine.sendMessage(327793, 0, 0, null);
  }
  
  private void stopIPv6TetheringOnAllInterfaces()
  {
    Iterator localIterator = this.mNotifyList.iterator();
    while (localIterator.hasNext()) {
      stopIPv6TetheringOn((TetherInterfaceStateMachine)localIterator.next());
    }
  }
  
  private static String toDebugString(NetworkState paramNetworkState)
  {
    if (paramNetworkState == null) {
      return "NetworkState{null}";
    }
    return String.format("NetworkState{%s, %s, %s}", new Object[] { paramNetworkState.network, paramNetworkState.networkCapabilities, paramNetworkState.linkProperties });
  }
  
  private void updateIPv6TetheringInterfaces()
  {
    Object localObject = this.mNotifyList.iterator();
    if (((Iterator)localObject).hasNext())
    {
      localObject = (TetherInterfaceStateMachine)((Iterator)localObject).next();
      ((TetherInterfaceStateMachine)localObject).sendMessage(327793, 0, 0, getInterfaceIPv6LinkProperties((TetherInterfaceStateMachine)localObject));
    }
  }
  
  public void addActiveDownstream(TetherInterfaceStateMachine paramTetherInterfaceStateMachine)
  {
    if (this.mActiveDownstreams.indexOf(paramTetherInterfaceStateMachine) == -1)
    {
      this.mActiveDownstreams.offer(paramTetherInterfaceStateMachine);
      updateIPv6TetheringInterfaces();
    }
  }
  
  public void removeActiveDownstream(TetherInterfaceStateMachine paramTetherInterfaceStateMachine)
  {
    stopIPv6TetheringOn(paramTetherInterfaceStateMachine);
    if (this.mActiveDownstreams.remove(paramTetherInterfaceStateMachine)) {
      updateIPv6TetheringInterfaces();
    }
  }
  
  public void updateUpstreamNetworkState(NetworkState paramNetworkState)
  {
    if (!canTetherIPv6(paramNetworkState))
    {
      stopIPv6TetheringOnAllInterfaces();
      setUpstreamNetworkState(null);
      return;
    }
    if ((this.mUpstreamNetworkState == null) || (paramNetworkState.network.equals(this.mUpstreamNetworkState.network))) {}
    for (;;)
    {
      setUpstreamNetworkState(paramNetworkState);
      updateIPv6TetheringInterfaces();
      return;
      stopIPv6TetheringOnAllInterfaces();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/tethering/IPv6TetheringCoordinator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */