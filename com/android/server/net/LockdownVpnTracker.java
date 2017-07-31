package com.android.server.net;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.security.KeyStore;
import android.system.Os;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.Preconditions;
import com.android.server.ConnectivityService;
import com.android.server.EventLogTags;
import com.android.server.connectivity.Vpn;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

public class LockdownVpnTracker
{
  private static final String ACTION_LOCKDOWN_RESET = "com.android.server.action.LOCKDOWN_RESET";
  private static final int MAX_ERROR_COUNT = 4;
  private static final int ROOT_UID = 0;
  private static final String TAG = "LockdownVpnTracker";
  private String mAcceptedEgressIface;
  private String mAcceptedIface;
  private List<LinkAddress> mAcceptedSourceAddr;
  private final PendingIntent mConfigIntent;
  private final ConnectivityService mConnService;
  private final Context mContext;
  private int mErrorCount;
  private final INetworkManagementService mNetService;
  private final VpnProfile mProfile;
  private final PendingIntent mResetIntent;
  private BroadcastReceiver mResetReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      LockdownVpnTracker.this.reset();
    }
  };
  private final Object mStateLock = new Object();
  private final Vpn mVpn;
  
  public LockdownVpnTracker(Context paramContext, INetworkManagementService paramINetworkManagementService, ConnectivityService paramConnectivityService, Vpn paramVpn, VpnProfile paramVpnProfile)
  {
    this.mContext = ((Context)Preconditions.checkNotNull(paramContext));
    this.mNetService = ((INetworkManagementService)Preconditions.checkNotNull(paramINetworkManagementService));
    this.mConnService = ((ConnectivityService)Preconditions.checkNotNull(paramConnectivityService));
    this.mVpn = ((Vpn)Preconditions.checkNotNull(paramVpn));
    this.mProfile = ((VpnProfile)Preconditions.checkNotNull(paramVpnProfile));
    paramContext = new Intent("android.settings.VPN_SETTINGS");
    this.mConfigIntent = PendingIntent.getActivity(this.mContext, 0, paramContext, 0);
    paramContext = new Intent("com.android.server.action.LOCKDOWN_RESET");
    paramContext.addFlags(1073741824);
    this.mResetIntent = PendingIntent.getBroadcast(this.mContext, 0, paramContext, 0);
  }
  
  private void clearSourceRulesLocked()
  {
    try
    {
      if (this.mAcceptedIface != null)
      {
        this.mNetService.setFirewallInterfaceRule(this.mAcceptedIface, false);
        this.mAcceptedIface = null;
      }
      if (this.mAcceptedSourceAddr == null) {
        return;
      }
      Iterator localIterator = this.mAcceptedSourceAddr.iterator();
      while (localIterator.hasNext()) {
        setFirewallEgressSourceRule((LinkAddress)localIterator.next(), false);
      }
      this.mNetService.setFirewallUidRule(0, 0, 0);
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("Problem setting firewall rules", localRemoteException);
    }
    this.mNetService.setFirewallUidRule(0, Os.getuid(), 0);
    this.mAcceptedSourceAddr = null;
  }
  
  private void handleStateChangedLocked()
  {
    NetworkInfo localNetworkInfo2 = this.mConnService.getActiveNetworkInfoUnfiltered();
    LinkProperties localLinkProperties = this.mConnService.getActiveLinkProperties();
    NetworkInfo localNetworkInfo3 = this.mVpn.getNetworkInfo();
    Object localObject2 = this.mVpn.getLegacyVpnConfig();
    boolean bool;
    if (localNetworkInfo2 != null)
    {
      bool = NetworkInfo.State.DISCONNECTED.equals(localNetworkInfo2.getState());
      if (localLinkProperties == null) {
        break label175;
      }
      if (!TextUtils.equals(this.mAcceptedEgressIface, localLinkProperties.getInterfaceName())) {
        break label180;
      }
      i = 0;
      label75:
      if (localNetworkInfo2 != null) {
        break label185;
      }
      str = null;
      label82:
      if (localLinkProperties != null) {
        break label197;
      }
    }
    label175:
    label180:
    label185:
    label197:
    for (Object localObject1 = null;; localObject1 = localLinkProperties.getInterfaceName())
    {
      Slog.d("LockdownVpnTracker", "handleStateChanged: egress=" + str + " " + this.mAcceptedEgressIface + "->" + (String)localObject1);
      if ((bool) || (i != 0))
      {
        clearSourceRulesLocked();
        this.mAcceptedEgressIface = null;
        this.mVpn.stopLegacyVpnPrivileged();
      }
      if (!bool) {
        break label207;
      }
      hideNotification();
      return;
      bool = true;
      break;
      i = 1;
      break label75;
      i = 1;
      break label75;
      str = ConnectivityManager.getNetworkTypeName(localNetworkInfo2.getType());
      break label82;
    }
    label207:
    int i = localNetworkInfo2.getType();
    if (localNetworkInfo3.getDetailedState() == NetworkInfo.DetailedState.FAILED) {
      EventLogTags.writeLockdownVpnError(i);
    }
    if (this.mErrorCount > 4) {
      showNotification(17040529, 17303511);
    }
    do
    {
      return;
      if ((localNetworkInfo2.isConnected()) && (!localNetworkInfo3.isConnectedOrConnecting())) {
        break;
      }
    } while ((!localNetworkInfo3.isConnected()) || (localObject2 == null));
    String str = ((VpnConfig)localObject2).interfaze;
    localObject1 = ((VpnConfig)localObject2).addresses;
    if ((TextUtils.equals(str, this.mAcceptedIface)) && (((List)localObject1).equals(this.mAcceptedSourceAddr)))
    {
      return;
      if (this.mProfile.isValidLockdownProfile())
      {
        Slog.d("LockdownVpnTracker", "Active network connected; starting VPN");
        EventLogTags.writeLockdownVpnConnecting(i);
        showNotification(17040526, 17303511);
        this.mAcceptedEgressIface = localLinkProperties.getInterfaceName();
        try
        {
          this.mVpn.startLegacyVpnPrivileged(this.mProfile, KeyStore.getInstance(), localLinkProperties);
          return;
        }
        catch (IllegalStateException localIllegalStateException)
        {
          this.mAcceptedEgressIface = null;
          Slog.e("LockdownVpnTracker", "Failed to start VPN", localIllegalStateException);
          showNotification(17040529, 17303511);
          return;
        }
      }
      Slog.e("LockdownVpnTracker", "Invalid VPN profile; requires IP-based server and DNS");
      showNotification(17040529, 17303511);
      return;
    }
    Slog.d("LockdownVpnTracker", "VPN connected using iface=" + localIllegalStateException + ", sourceAddr=" + localObject1.toString());
    EventLogTags.writeLockdownVpnConnected(i);
    showNotification(17040527, 17303510);
    try
    {
      clearSourceRulesLocked();
      this.mNetService.setFirewallInterfaceRule(localIllegalStateException, true);
      localObject2 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject2).hasNext()) {
        setFirewallEgressSourceRule((LinkAddress)((Iterator)localObject2).next(), true);
      }
      this.mNetService.setFirewallUidRule(0, 0, 1);
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("Problem setting firewall rules", localRemoteException);
    }
    this.mNetService.setFirewallUidRule(0, Os.getuid(), 1);
    this.mErrorCount = 0;
    this.mAcceptedIface = localRemoteException;
    this.mAcceptedSourceAddr = ((List)localObject1);
    NetworkInfo localNetworkInfo1 = new NetworkInfo(localNetworkInfo2);
    augmentNetworkInfo(localNetworkInfo1);
    this.mConnService.sendConnectedBroadcast(localNetworkInfo1);
  }
  
  private void hideNotification()
  {
    NotificationManager.from(this.mContext).cancel("LockdownVpnTracker", 0);
  }
  
  private void initLocked()
  {
    Slog.d("LockdownVpnTracker", "initLocked()");
    this.mVpn.setEnableTeardown(false);
    IntentFilter localIntentFilter = new IntentFilter("com.android.server.action.LOCKDOWN_RESET");
    this.mContext.registerReceiver(this.mResetReceiver, localIntentFilter, "android.permission.CONNECTIVITY_INTERNAL", null);
    try
    {
      this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 500, true);
      this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 4500, true);
      this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 1701, true);
      handleStateChangedLocked();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("Problem setting firewall rules", localRemoteException);
    }
  }
  
  public static boolean isEnabled()
  {
    return KeyStore.getInstance().contains("LOCKDOWN_VPN");
  }
  
  private void setFirewallEgressSourceRule(LinkAddress paramLinkAddress, boolean paramBoolean)
    throws RemoteException
  {
    paramLinkAddress = paramLinkAddress.getAddress().getHostAddress();
    this.mNetService.setFirewallEgressSourceRule(paramLinkAddress, paramBoolean);
  }
  
  private void showNotification(int paramInt1, int paramInt2)
  {
    Notification.Builder localBuilder = new Notification.Builder(this.mContext).setWhen(0L).setSmallIcon(paramInt2).setContentTitle(this.mContext.getString(paramInt1)).setContentText(this.mContext.getString(17040530)).setContentIntent(this.mConfigIntent).setPriority(-1).setOngoing(true).addAction(17302526, this.mContext.getString(17040533), this.mResetIntent).setColor(this.mContext.getColor(17170523));
    NotificationManager.from(this.mContext).notify("LockdownVpnTracker", 0, localBuilder.build());
  }
  
  private void shutdownLocked()
  {
    Slog.d("LockdownVpnTracker", "shutdownLocked()");
    this.mAcceptedEgressIface = null;
    this.mErrorCount = 0;
    this.mVpn.stopLegacyVpnPrivileged();
    try
    {
      this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 500, false);
      this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 4500, false);
      this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 1701, false);
      clearSourceRulesLocked();
      hideNotification();
      this.mContext.unregisterReceiver(this.mResetReceiver);
      this.mVpn.setEnableTeardown(true);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("Problem setting firewall rules", localRemoteException);
    }
  }
  
  public void augmentNetworkInfo(NetworkInfo paramNetworkInfo)
  {
    if (paramNetworkInfo.isConnected())
    {
      NetworkInfo localNetworkInfo = this.mVpn.getNetworkInfo();
      paramNetworkInfo.setDetailedState(localNetworkInfo.getDetailedState(), localNetworkInfo.getReason(), null);
    }
  }
  
  public void init()
  {
    synchronized (this.mStateLock)
    {
      initLocked();
      return;
    }
  }
  
  public void onNetworkInfoChanged()
  {
    synchronized (this.mStateLock)
    {
      handleStateChangedLocked();
      return;
    }
  }
  
  public void onVpnStateChanged(NetworkInfo arg1)
  {
    if (???.getDetailedState() == NetworkInfo.DetailedState.FAILED) {
      this.mErrorCount += 1;
    }
    synchronized (this.mStateLock)
    {
      handleStateChangedLocked();
      return;
    }
  }
  
  public void reset()
  {
    Slog.d("LockdownVpnTracker", "reset()");
    synchronized (this.mStateLock)
    {
      shutdownLocked();
      initLocked();
      handleStateChangedLocked();
      return;
    }
  }
  
  public void shutdown()
  {
    synchronized (this.mStateLock)
    {
      shutdownLocked();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/LockdownVpnTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */