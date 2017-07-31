package com.android.server.connectivity;

import android.net.ConnectivityMetricsEvent;
import android.net.metrics.ApfProgramEvent;
import android.net.metrics.ApfStats;
import android.net.metrics.DefaultNetworkEvent;
import android.net.metrics.DhcpClientEvent;
import android.net.metrics.DhcpErrorEvent;
import android.net.metrics.DnsEvent;
import android.net.metrics.IpManagerEvent;
import android.net.metrics.IpReachabilityEvent;
import android.net.metrics.NetworkEvent;
import android.net.metrics.RaEvent;
import android.net.metrics.ValidationProbeEvent;
import android.os.Parcelable;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.ApfProgramEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.ApfStatistics;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.DHCPEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.DNSLookupBatch;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.DefaultNetworkEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.IpConnectivityEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.IpConnectivityLog;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.IpProvisioningEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.IpReachabilityEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.NetworkEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.NetworkId;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.RaEvent;
import com.android.server.connectivity.metrics.IpConnectivityLogClass.ValidationProbeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class IpConnectivityEventBuilder
{
  private static int[] bytesToInts(byte[] paramArrayOfByte)
  {
    int[] arrayOfInt = new int[paramArrayOfByte.length];
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      paramArrayOfByte[i] &= 0xFF;
      i += 1;
    }
    return arrayOfInt;
  }
  
  private static int ipSupportOf(DefaultNetworkEvent paramDefaultNetworkEvent)
  {
    if ((paramDefaultNetworkEvent.prevIPv4) && (paramDefaultNetworkEvent.prevIPv6)) {
      return 3;
    }
    if (paramDefaultNetworkEvent.prevIPv6) {
      return 2;
    }
    if (paramDefaultNetworkEvent.prevIPv4) {
      return 1;
    }
    return 0;
  }
  
  private static boolean isBitSet(int paramInt1, int paramInt2)
  {
    return (1 << paramInt2 & paramInt1) != 0;
  }
  
  private static IpConnectivityLogClass.NetworkId netIdOf(int paramInt)
  {
    IpConnectivityLogClass.NetworkId localNetworkId = new IpConnectivityLogClass.NetworkId();
    localNetworkId.networkId = paramInt;
    return localNetworkId;
  }
  
  public static byte[] serialize(int paramInt, List<ConnectivityMetricsEvent> paramList)
    throws IOException
  {
    IpConnectivityLogClass.IpConnectivityLog localIpConnectivityLog = new IpConnectivityLogClass.IpConnectivityLog();
    localIpConnectivityLog.events = toProto(paramList);
    localIpConnectivityLog.droppedEvents = paramInt;
    return IpConnectivityLogClass.IpConnectivityLog.toByteArray(localIpConnectivityLog);
  }
  
  private static void setApfProgramEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, ApfProgramEvent paramApfProgramEvent)
  {
    paramIpConnectivityEvent.apfProgramEvent = new IpConnectivityLogClass.ApfProgramEvent();
    paramIpConnectivityEvent.apfProgramEvent.lifetime = paramApfProgramEvent.lifetime;
    paramIpConnectivityEvent.apfProgramEvent.filteredRas = paramApfProgramEvent.filteredRas;
    paramIpConnectivityEvent.apfProgramEvent.currentRas = paramApfProgramEvent.currentRas;
    paramIpConnectivityEvent.apfProgramEvent.programLength = paramApfProgramEvent.programLength;
    if (isBitSet(paramApfProgramEvent.flags, 0)) {
      paramIpConnectivityEvent.apfProgramEvent.dropMulticast = true;
    }
    if (isBitSet(paramApfProgramEvent.flags, 1)) {
      paramIpConnectivityEvent.apfProgramEvent.hasIpv4Addr = true;
    }
  }
  
  private static void setApfStats(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, ApfStats paramApfStats)
  {
    paramIpConnectivityEvent.apfStatistics = new IpConnectivityLogClass.ApfStatistics();
    paramIpConnectivityEvent.apfStatistics.durationMs = paramApfStats.durationMs;
    paramIpConnectivityEvent.apfStatistics.receivedRas = paramApfStats.receivedRas;
    paramIpConnectivityEvent.apfStatistics.matchingRas = paramApfStats.matchingRas;
    paramIpConnectivityEvent.apfStatistics.droppedRas = paramApfStats.droppedRas;
    paramIpConnectivityEvent.apfStatistics.zeroLifetimeRas = paramApfStats.zeroLifetimeRas;
    paramIpConnectivityEvent.apfStatistics.parseErrors = paramApfStats.parseErrors;
    paramIpConnectivityEvent.apfStatistics.programUpdates = paramApfStats.programUpdates;
    paramIpConnectivityEvent.apfStatistics.maxProgramSize = paramApfStats.maxProgramSize;
  }
  
  private static void setDefaultNetworkEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, DefaultNetworkEvent paramDefaultNetworkEvent)
  {
    paramIpConnectivityEvent.defaultNetworkEvent = new IpConnectivityLogClass.DefaultNetworkEvent();
    paramIpConnectivityEvent.defaultNetworkEvent.networkId = netIdOf(paramDefaultNetworkEvent.netId);
    paramIpConnectivityEvent.defaultNetworkEvent.previousNetworkId = netIdOf(paramDefaultNetworkEvent.prevNetId);
    paramIpConnectivityEvent.defaultNetworkEvent.transportTypes = paramDefaultNetworkEvent.transportTypes;
    paramIpConnectivityEvent.defaultNetworkEvent.previousNetworkIpSupport = ipSupportOf(paramDefaultNetworkEvent);
  }
  
  private static void setDhcpClientEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, DhcpClientEvent paramDhcpClientEvent)
  {
    paramIpConnectivityEvent.dhcpEvent = new IpConnectivityLogClass.DHCPEvent();
    paramIpConnectivityEvent.dhcpEvent.ifName = paramDhcpClientEvent.ifName;
    paramIpConnectivityEvent.dhcpEvent.stateTransition = paramDhcpClientEvent.msg;
    paramIpConnectivityEvent.dhcpEvent.durationMs = paramDhcpClientEvent.durationMs;
  }
  
  private static void setDhcpErrorEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, DhcpErrorEvent paramDhcpErrorEvent)
  {
    paramIpConnectivityEvent.dhcpEvent = new IpConnectivityLogClass.DHCPEvent();
    paramIpConnectivityEvent.dhcpEvent.ifName = paramDhcpErrorEvent.ifName;
    paramIpConnectivityEvent.dhcpEvent.errorCode = paramDhcpErrorEvent.errorCode;
  }
  
  private static void setDnsEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, DnsEvent paramDnsEvent)
  {
    paramIpConnectivityEvent.dnsLookupBatch = new IpConnectivityLogClass.DNSLookupBatch();
    paramIpConnectivityEvent.dnsLookupBatch.networkId = netIdOf(paramDnsEvent.netId);
    paramIpConnectivityEvent.dnsLookupBatch.eventTypes = bytesToInts(paramDnsEvent.eventTypes);
    paramIpConnectivityEvent.dnsLookupBatch.returnCodes = bytesToInts(paramDnsEvent.returnCodes);
    paramIpConnectivityEvent.dnsLookupBatch.latenciesMs = paramDnsEvent.latenciesMs;
  }
  
  private static boolean setEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof DhcpErrorEvent))
    {
      setDhcpErrorEvent(paramIpConnectivityEvent, (DhcpErrorEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof DhcpClientEvent))
    {
      setDhcpClientEvent(paramIpConnectivityEvent, (DhcpClientEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof DnsEvent))
    {
      setDnsEvent(paramIpConnectivityEvent, (DnsEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof IpManagerEvent))
    {
      setIpManagerEvent(paramIpConnectivityEvent, (IpManagerEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof IpReachabilityEvent))
    {
      setIpReachabilityEvent(paramIpConnectivityEvent, (IpReachabilityEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof DefaultNetworkEvent))
    {
      setDefaultNetworkEvent(paramIpConnectivityEvent, (DefaultNetworkEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof NetworkEvent))
    {
      setNetworkEvent(paramIpConnectivityEvent, (NetworkEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof ValidationProbeEvent))
    {
      setValidationProbeEvent(paramIpConnectivityEvent, (ValidationProbeEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof ApfProgramEvent))
    {
      setApfProgramEvent(paramIpConnectivityEvent, (ApfProgramEvent)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof ApfStats))
    {
      setApfStats(paramIpConnectivityEvent, (ApfStats)paramParcelable);
      return true;
    }
    if ((paramParcelable instanceof RaEvent))
    {
      setRaEvent(paramIpConnectivityEvent, (RaEvent)paramParcelable);
      return true;
    }
    return false;
  }
  
  private static void setIpManagerEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, IpManagerEvent paramIpManagerEvent)
  {
    paramIpConnectivityEvent.ipProvisioningEvent = new IpConnectivityLogClass.IpProvisioningEvent();
    paramIpConnectivityEvent.ipProvisioningEvent.ifName = paramIpManagerEvent.ifName;
    paramIpConnectivityEvent.ipProvisioningEvent.eventType = paramIpManagerEvent.eventType;
    paramIpConnectivityEvent.ipProvisioningEvent.latencyMs = ((int)paramIpManagerEvent.durationMs);
  }
  
  private static void setIpReachabilityEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, IpReachabilityEvent paramIpReachabilityEvent)
  {
    paramIpConnectivityEvent.ipReachabilityEvent = new IpConnectivityLogClass.IpReachabilityEvent();
    paramIpConnectivityEvent.ipReachabilityEvent.ifName = paramIpReachabilityEvent.ifName;
    paramIpConnectivityEvent.ipReachabilityEvent.eventType = paramIpReachabilityEvent.eventType;
  }
  
  private static void setNetworkEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, NetworkEvent paramNetworkEvent)
  {
    paramIpConnectivityEvent.networkEvent = new IpConnectivityLogClass.NetworkEvent();
    paramIpConnectivityEvent.networkEvent.networkId = netIdOf(paramNetworkEvent.netId);
    paramIpConnectivityEvent.networkEvent.eventType = paramNetworkEvent.eventType;
    paramIpConnectivityEvent.networkEvent.latencyMs = ((int)paramNetworkEvent.durationMs);
  }
  
  private static void setRaEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, RaEvent paramRaEvent)
  {
    paramIpConnectivityEvent.raEvent = new IpConnectivityLogClass.RaEvent();
    paramIpConnectivityEvent.raEvent.routerLifetime = paramRaEvent.routerLifetime;
    paramIpConnectivityEvent.raEvent.prefixValidLifetime = paramRaEvent.prefixValidLifetime;
    paramIpConnectivityEvent.raEvent.prefixPreferredLifetime = paramRaEvent.prefixPreferredLifetime;
    paramIpConnectivityEvent.raEvent.routeInfoLifetime = paramRaEvent.routeInfoLifetime;
    paramIpConnectivityEvent.raEvent.rdnssLifetime = paramRaEvent.rdnssLifetime;
    paramIpConnectivityEvent.raEvent.dnsslLifetime = paramRaEvent.dnsslLifetime;
  }
  
  private static void setValidationProbeEvent(IpConnectivityLogClass.IpConnectivityEvent paramIpConnectivityEvent, ValidationProbeEvent paramValidationProbeEvent)
  {
    paramIpConnectivityEvent.validationProbeEvent = new IpConnectivityLogClass.ValidationProbeEvent();
    paramIpConnectivityEvent.validationProbeEvent.networkId = netIdOf(paramValidationProbeEvent.netId);
    paramIpConnectivityEvent.validationProbeEvent.latencyMs = ((int)paramValidationProbeEvent.durationMs);
    paramIpConnectivityEvent.validationProbeEvent.probeType = paramValidationProbeEvent.probeType;
    paramIpConnectivityEvent.validationProbeEvent.probeResult = paramValidationProbeEvent.returnCode;
  }
  
  public static IpConnectivityLogClass.IpConnectivityEvent toProto(ConnectivityMetricsEvent paramConnectivityMetricsEvent)
  {
    IpConnectivityLogClass.IpConnectivityEvent localIpConnectivityEvent = new IpConnectivityLogClass.IpConnectivityEvent();
    if (!setEvent(localIpConnectivityEvent, paramConnectivityMetricsEvent.data)) {
      return null;
    }
    localIpConnectivityEvent.timeMs = paramConnectivityMetricsEvent.timestamp;
    return localIpConnectivityEvent;
  }
  
  public static IpConnectivityLogClass.IpConnectivityEvent[] toProto(List<ConnectivityMetricsEvent> paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      IpConnectivityLogClass.IpConnectivityEvent localIpConnectivityEvent = toProto((ConnectivityMetricsEvent)paramList.next());
      if (localIpConnectivityEvent != null) {
        localArrayList.add(localIpConnectivityEvent);
      }
    }
    return (IpConnectivityLogClass.IpConnectivityEvent[])localArrayList.toArray(new IpConnectivityLogClass.IpConnectivityEvent[localArrayList.size()]);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/IpConnectivityEventBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */