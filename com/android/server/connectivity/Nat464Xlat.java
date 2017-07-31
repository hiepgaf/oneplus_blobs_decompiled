package com.android.server.connectivity;

import android.content.Context;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.server.net.BaseNetworkObserver;
import java.net.Inet4Address;
import java.util.Iterator;
import java.util.List;

public class Nat464Xlat
  extends BaseNetworkObserver
{
  private static final String CLAT_PREFIX = "v4-";
  private static final int[] NETWORK_TYPES = { 0, 1, 9 };
  private static final String TAG = "Nat464Xlat";
  private String mBaseIface;
  private final Handler mHandler;
  private String mIface;
  private boolean mIsRunning;
  private final INetworkManagementService mNMService;
  private final NetworkAgentInfo mNetwork;
  
  public Nat464Xlat(Context paramContext, INetworkManagementService paramINetworkManagementService, Handler paramHandler, NetworkAgentInfo paramNetworkAgentInfo)
  {
    this.mNMService = paramINetworkManagementService;
    this.mHandler = paramHandler;
    this.mNetwork = paramNetworkAgentInfo;
  }
  
  private void clear()
  {
    this.mIface = null;
    this.mBaseIface = null;
    this.mIsRunning = false;
  }
  
  private LinkAddress getLinkAddress(String paramString)
  {
    try
    {
      paramString = this.mNMService.getInterfaceConfig(paramString).getLinkAddress();
      return paramString;
    }
    catch (RemoteException|IllegalStateException paramString)
    {
      Slog.e("Nat464Xlat", "Error getting link properties: " + paramString);
    }
    return null;
  }
  
  private LinkProperties makeLinkProperties(LinkAddress paramLinkAddress)
  {
    LinkProperties localLinkProperties = new LinkProperties();
    localLinkProperties.setInterfaceName(this.mIface);
    localLinkProperties.addRoute(new RouteInfo(new LinkAddress(Inet4Address.ANY, 0), paramLinkAddress.getAddress(), this.mIface));
    localLinkProperties.addLinkAddress(paramLinkAddress);
    return localLinkProperties;
  }
  
  private void maybeSetIpv6NdOffload(String paramString, boolean paramBoolean)
  {
    if (this.mNetwork.networkInfo.getType() != 1) {
      return;
    }
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (paramBoolean) {}
      for (String str = "En";; str = "Dis")
      {
        Slog.d("Nat464Xlat", str + "abling ND offload on " + paramString);
        this.mNMService.setInterfaceIpv6NdOffload(paramString, paramBoolean);
        return;
      }
      return;
    }
    catch (RemoteException|IllegalStateException localRemoteException)
    {
      Slog.w("Nat464Xlat", "Changing IPv6 ND offload on " + paramString + "failed: " + localRemoteException);
    }
  }
  
  public static boolean requiresClat(NetworkAgentInfo paramNetworkAgentInfo)
  {
    boolean bool2 = true;
    int i = paramNetworkAgentInfo.networkInfo.getType();
    boolean bool3 = paramNetworkAgentInfo.networkInfo.isConnected();
    boolean bool1;
    if (paramNetworkAgentInfo.linkProperties != null)
    {
      bool1 = paramNetworkAgentInfo.linkProperties.hasIPv4Address();
      Slog.i("Nat464Xlat", "Android Xlat enabled is doXlat=" + true);
      if (1 == 0) {
        Slog.i("Nat464Xlat", "Android Xlat is disabled");
      }
      if ((bool3) && (!bool1)) {
        break label89;
      }
      label80:
      bool1 = false;
    }
    label89:
    do
    {
      return bool1;
      bool1 = false;
      break;
      if (!ArrayUtils.contains(NETWORK_TYPES, i)) {
        break label80;
      }
      bool1 = bool2;
    } while (i != 0);
    return true;
  }
  
  private void updateConnectivityService(LinkProperties paramLinkProperties)
  {
    paramLinkProperties = this.mHandler.obtainMessage(528387, paramLinkProperties);
    paramLinkProperties.replyTo = this.mNetwork.messenger;
    Slog.i("Nat464Xlat", "sending message to ConnectivityService: " + paramLinkProperties);
    paramLinkProperties.sendToTarget();
  }
  
  public void fixupLinkProperties(LinkProperties paramLinkProperties)
  {
    if ((this.mNetwork.clatd == null) || (!this.mIsRunning) || (this.mNetwork.linkProperties == null) || (this.mNetwork.linkProperties.getAllInterfaceNames().contains(this.mIface))) {}
    LinkProperties localLinkProperties;
    do
    {
      return;
      while (!paramLinkProperties.hasNext())
      {
        Slog.d("Nat464Xlat", "clatd running, updating NAI for " + this.mIface);
        paramLinkProperties = paramLinkProperties.getStackedLinks().iterator();
      }
      localLinkProperties = (LinkProperties)paramLinkProperties.next();
    } while (!this.mIface.equals(localLinkProperties.getInterfaceName()));
    this.mNetwork.linkProperties.addStackedLink(localLinkProperties);
  }
  
  public void interfaceLinkStateChanged(String paramString, boolean paramBoolean)
  {
    if ((isStarted()) && (paramBoolean) && (this.mIface.equals(paramString)))
    {
      Slog.i("Nat464Xlat", "interface " + paramString + " is up, mIsRunning " + this.mIsRunning + "->true");
      if (!this.mIsRunning)
      {
        paramString = getLinkAddress(paramString);
        if (paramString == null) {
          return;
        }
        this.mIsRunning = true;
        maybeSetIpv6NdOffload(this.mBaseIface, false);
        LinkProperties localLinkProperties = new LinkProperties(this.mNetwork.linkProperties);
        localLinkProperties.addStackedLink(makeLinkProperties(paramString));
        Slog.i("Nat464Xlat", "Adding stacked link " + this.mIface + " on top of " + this.mBaseIface);
        updateConnectivityService(localLinkProperties);
      }
    }
  }
  
  public void interfaceRemoved(String paramString)
  {
    if ((isStarted()) && (this.mIface.equals(paramString)))
    {
      Slog.i("Nat464Xlat", "interface " + paramString + " removed, mIsRunning " + this.mIsRunning + "->false");
      if (!this.mIsRunning) {}
    }
    try
    {
      this.mNMService.unregisterObserver(this);
      this.mNMService.stopClatd(this.mBaseIface);
      maybeSetIpv6NdOffload(this.mBaseIface, true);
      paramString = new LinkProperties(this.mNetwork.linkProperties);
      paramString.removeStackedLink(this.mIface);
      clear();
      updateConnectivityService(paramString);
      return;
    }
    catch (RemoteException|IllegalStateException paramString)
    {
      for (;;) {}
    }
  }
  
  public boolean isStarted()
  {
    return this.mIface != null;
  }
  
  public void start()
  {
    if (isStarted())
    {
      Slog.e("Nat464Xlat", "startClat: already started");
      return;
    }
    if (this.mNetwork.linkProperties == null)
    {
      Slog.e("Nat464Xlat", "startClat: Can't start clat with null LinkProperties");
      return;
    }
    try
    {
      this.mNMService.registerObserver(this);
      this.mBaseIface = this.mNetwork.linkProperties.getInterfaceName();
      if (this.mBaseIface == null)
      {
        Slog.e("Nat464Xlat", "startClat: Can't start clat on null interface");
        return;
      }
    }
    catch (RemoteException localRemoteException1)
    {
      Slog.e("Nat464Xlat", "startClat: Can't register interface observer for clat on " + this.mNetwork);
      return;
    }
    this.mIface = ("v4-" + this.mBaseIface);
    Slog.i("Nat464Xlat", "Starting clatd on " + this.mBaseIface);
    try
    {
      this.mNMService.startClatd(this.mBaseIface);
      return;
    }
    catch (RemoteException|IllegalStateException localRemoteException2)
    {
      Slog.e("Nat464Xlat", "Error starting clatd: " + localRemoteException2);
    }
  }
  
  public void stop()
  {
    if (isStarted())
    {
      Slog.i("Nat464Xlat", "Stopping clatd");
      try
      {
        this.mNMService.stopClatd(this.mBaseIface);
        return;
      }
      catch (RemoteException|IllegalStateException localRemoteException)
      {
        Slog.e("Nat464Xlat", "Error stopping clatd: " + localRemoteException);
        return;
      }
    }
    Slog.e("Nat464Xlat", "clatd: already stopped");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/connectivity/Nat464Xlat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */