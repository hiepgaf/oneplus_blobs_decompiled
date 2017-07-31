package android.net.ip;

import android.content.Context;
import android.net.DhcpResults;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.LinkProperties.ProvisioningChange;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.apf.ApfCapabilities;
import android.net.apf.ApfFilter;
import android.net.dhcp.DhcpClient;
import android.net.metrics.IpConnectivityLog;
import android.net.metrics.IpManagerEvent;
import android.net.util.AvoidBadWifiTracker;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.LocalLog.ReadOnlyLocalLog;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.IState;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.MessageUtils;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.internal.util.WakeupMessage;
import com.android.server.net.NetlinkTracker;
import com.android.server.net.NetlinkTracker.Callback;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;

public class IpManager
  extends StateMachine
{
  private static final String CLAT_PREFIX = "v4-";
  private static final int CMD_CONFIRM = 3;
  private static final int CMD_SET_MULTICAST_FILTER = 8;
  private static final int CMD_START = 2;
  private static final int CMD_STOP = 1;
  private static final int CMD_UPDATE_HTTP_PROXY = 7;
  private static final int CMD_UPDATE_TCP_BUFFER_SIZES = 6;
  private static final boolean DBG = false;
  public static final String DUMP_ARG = "ipmanager";
  public static final String DUMP_ARG_CONFIRM = "confirm";
  private static final int EVENT_DHCPACTION_TIMEOUT = 10;
  private static final int EVENT_NETLINK_LINKPROPERTIES_CHANGED = 5;
  private static final int EVENT_PRE_DHCP_ACTION_COMPLETE = 4;
  private static final int EVENT_PROVISIONING_TIMEOUT = 9;
  private static final int MAX_LOG_RECORDS = 500;
  private static final boolean NO_CALLBACKS = false;
  private static final boolean SEND_CALLBACKS = true;
  private static final boolean VDBG = false;
  private static final Class[] sMessageClasses = { IpManager.class, DhcpClient.class };
  private static final SparseArray<String> sWhatToString = MessageUtils.findMessageNames(sMessageClasses);
  private ApfFilter mApfFilter;
  private final AvoidBadWifiTracker mAvoidBadWifiTracker;
  protected final Callback mCallback;
  private final String mClatInterfaceName;
  private ProvisioningConfiguration mConfiguration;
  private final Context mContext;
  private final WakeupMessage mDhcpActionTimeoutAlarm;
  private DhcpClient mDhcpClient;
  private DhcpResults mDhcpResults;
  private ProxyInfo mHttpProxy;
  private final String mInterfaceName;
  private IpReachabilityMonitor mIpReachabilityMonitor;
  private LinkProperties mLinkProperties;
  private final LocalLog mLocalLog;
  private final IpConnectivityLog mMetricsLog = new IpConnectivityLog();
  private final MessageHandlingLogger mMsgStateLogger;
  private boolean mMulticastFiltering;
  private final NetlinkTracker mNetlinkTracker;
  private NetworkInterface mNetworkInterface;
  private final INetworkManagementService mNwService;
  private final WakeupMessage mProvisioningTimeoutAlarm;
  private final State mRunningState = new RunningState();
  private long mStartTimeMillis;
  private final State mStartedState = new StartedState();
  private final State mStoppedState = new StoppedState();
  private final State mStoppingState = new StoppingState();
  private final String mTag = getName();
  private String mTcpBufferSizes;
  
  public IpManager(Context paramContext, String paramString, Callback paramCallback)
    throws IllegalArgumentException
  {
    this(paramContext, paramString, paramCallback, INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management")));
  }
  
  public IpManager(Context paramContext, String paramString, Callback paramCallback, INetworkManagementService paramINetworkManagementService)
    throws IllegalArgumentException
  {
    super(IpManager.class.getSimpleName() + "." + paramString);
    this.mContext = paramContext;
    this.mInterfaceName = paramString;
    this.mClatInterfaceName = ("v4-" + paramString);
    this.mCallback = new LoggingCallbackWrapper(paramCallback);
    this.mNwService = paramINetworkManagementService;
    this.mNetlinkTracker = new NetlinkTracker(this.mInterfaceName, new NetlinkTracker.Callback()
    {
      public void update()
      {
        IpManager.this.sendMessage(5);
      }
    })
    {
      public void interfaceAdded(String paramAnonymousString)
      {
        super.interfaceAdded(paramAnonymousString);
        if (IpManager.-get1(IpManager.this).equals(paramAnonymousString)) {
          IpManager.this.mCallback.setNeighborDiscoveryOffload(false);
        }
      }
      
      public void interfaceRemoved(String paramAnonymousString)
      {
        super.interfaceRemoved(paramAnonymousString);
        if (IpManager.-get1(IpManager.this).equals(paramAnonymousString)) {
          IpManager.this.mCallback.setNeighborDiscoveryOffload(true);
        }
      }
    };
    this.mAvoidBadWifiTracker = new AvoidBadWifiTracker(this.mContext, getHandler());
    resetLinkProperties();
    this.mProvisioningTimeoutAlarm = new WakeupMessage(this.mContext, getHandler(), this.mTag + ".EVENT_PROVISIONING_TIMEOUT", 9);
    this.mDhcpActionTimeoutAlarm = new WakeupMessage(this.mContext, getHandler(), this.mTag + ".EVENT_DHCPACTION_TIMEOUT", 10);
    addState(this.mStoppedState);
    addState(this.mStartedState);
    addState(this.mRunningState, this.mStartedState);
    addState(this.mStoppingState);
    setInitialState(this.mStoppedState);
    this.mLocalLog = new LocalLog(500);
    this.mMsgStateLogger = new MessageHandlingLogger(null);
    super.start();
    try
    {
      this.mNwService.registerObserver(this.mNetlinkTracker);
      return;
    }
    catch (RemoteException paramContext)
    {
      Log.e(this.mTag, "Couldn't register NetlinkTracker: " + paramContext.toString());
    }
  }
  
  private LinkProperties assembleLinkProperties()
  {
    LinkProperties localLinkProperties = new LinkProperties();
    localLinkProperties.setInterfaceName(this.mInterfaceName);
    Object localObject1 = this.mNetlinkTracker.getLinkProperties();
    localLinkProperties.setLinkAddresses(((LinkProperties)localObject1).getLinkAddresses());
    Object localObject2 = ((LinkProperties)localObject1).getRoutes().iterator();
    while (((Iterator)localObject2).hasNext()) {
      localLinkProperties.addRoute((RouteInfo)((Iterator)localObject2).next());
    }
    localObject1 = ((LinkProperties)localObject1).getDnsServers().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (InetAddress)((Iterator)localObject1).next();
      if (localLinkProperties.isReachable((InetAddress)localObject2)) {
        localLinkProperties.addDnsServer((InetAddress)localObject2);
      }
    }
    if (this.mDhcpResults != null)
    {
      localObject1 = this.mDhcpResults.getRoutes(this.mInterfaceName).iterator();
      while (((Iterator)localObject1).hasNext()) {
        localLinkProperties.addRoute((RouteInfo)((Iterator)localObject1).next());
      }
      localObject1 = this.mDhcpResults.dnsServers.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (InetAddress)((Iterator)localObject1).next();
        if (localLinkProperties.isReachable((InetAddress)localObject2)) {
          localLinkProperties.addDnsServer((InetAddress)localObject2);
        }
      }
      localLinkProperties.setDomains(this.mDhcpResults.domains);
      if (this.mDhcpResults.mtu != 0) {
        localLinkProperties.setMtu(this.mDhcpResults.mtu);
      }
    }
    if (!TextUtils.isEmpty(this.mTcpBufferSizes)) {
      localLinkProperties.setTcpBufferSizes(this.mTcpBufferSizes);
    }
    if (this.mHttpProxy != null) {
      localLinkProperties.setHttpProxy(this.mHttpProxy);
    }
    return localLinkProperties;
  }
  
  public static IpManager.ProvisioningConfiguration.Builder buildProvisioningConfiguration()
  {
    return new IpManager.ProvisioningConfiguration.Builder();
  }
  
  private void clearIPv4Address()
  {
    try
    {
      InterfaceConfiguration localInterfaceConfiguration = new InterfaceConfiguration();
      localInterfaceConfiguration.setLinkAddress(new LinkAddress("0.0.0.0/0"));
      this.mNwService.setInterfaceConfig(this.mInterfaceName, localInterfaceConfiguration);
      return;
    }
    catch (IllegalStateException|RemoteException localIllegalStateException)
    {
      Log.e(this.mTag, "ALERT: Failed to clear IPv4 address on interface " + this.mInterfaceName, localIllegalStateException);
    }
  }
  
  private LinkProperties.ProvisioningChange compareProvisioning(LinkProperties paramLinkProperties1, LinkProperties paramLinkProperties2)
  {
    boolean bool1 = isProvisioned(paramLinkProperties1);
    boolean bool2 = isProvisioned(paramLinkProperties2);
    LinkProperties.ProvisioningChange localProvisioningChange;
    int i;
    label43:
    int j;
    label60:
    int k;
    label77:
    int m;
    if ((!bool1) && (bool2))
    {
      localProvisioningChange = LinkProperties.ProvisioningChange.GAINED_PROVISIONING;
      if ((paramLinkProperties1.isIPv6Provisioned()) && (!paramLinkProperties2.isIPv6Provisioned())) {
        break label173;
      }
      i = 0;
      if ((paramLinkProperties1.hasIPv4Address()) && (!paramLinkProperties2.hasIPv4Address())) {
        break label178;
      }
      j = 0;
      if ((paramLinkProperties1.hasIPv6DefaultRoute()) && (!paramLinkProperties2.hasIPv6DefaultRoute())) {
        break label184;
      }
      k = 0;
      if (!this.mAvoidBadWifiTracker.currentValue()) {
        break label190;
      }
      m = 0;
      label90:
      if (j != 0) {
        break label196;
      }
      paramLinkProperties2 = localProvisioningChange;
      if (i != 0) {
        if (m == 0) {
          break label196;
        }
      }
    }
    label173:
    label178:
    label184:
    label190:
    label196:
    for (paramLinkProperties2 = localProvisioningChange;; paramLinkProperties2 = LinkProperties.ProvisioningChange.LOST_PROVISIONING)
    {
      if ((paramLinkProperties1.hasGlobalIPv6Address()) && (k != 0) && (m == 0)) {
        break label203;
      }
      return paramLinkProperties2;
      if ((bool1) && (bool2))
      {
        localProvisioningChange = LinkProperties.ProvisioningChange.STILL_PROVISIONED;
        break;
      }
      if ((bool1) || (bool2))
      {
        localProvisioningChange = LinkProperties.ProvisioningChange.LOST_PROVISIONING;
        break;
      }
      localProvisioningChange = LinkProperties.ProvisioningChange.STILL_NOT_PROVISIONED;
      break;
      i = 1;
      break label43;
      j = 1;
      break label60;
      k = 1;
      break label77;
      m = 1;
      break label90;
    }
    label203:
    return LinkProperties.ProvisioningChange.LOST_PROVISIONING;
  }
  
  private void dispatchCallback(LinkProperties.ProvisioningChange paramProvisioningChange, LinkProperties paramLinkProperties)
  {
    switch (-getandroid-net-LinkProperties$ProvisioningChangeSwitchesValues()[paramProvisioningChange.ordinal()])
    {
    default: 
      this.mCallback.onLinkPropertiesChange(paramLinkProperties);
      return;
    case 1: 
      recordMetric(1);
      this.mCallback.onProvisioningSuccess(paramLinkProperties);
      return;
    }
    recordMetric(2);
    this.mCallback.onProvisioningFailure(paramLinkProperties);
  }
  
  private void doImmediateProvisioningFailure(int paramInt)
  {
    recordMetric(paramInt);
    this.mCallback.onProvisioningFailure(new LinkProperties(this.mLinkProperties));
  }
  
  private void getNetworkInterface()
  {
    try
    {
      this.mNetworkInterface = NetworkInterface.getByName(this.mInterfaceName);
      return;
    }
    catch (SocketException|NullPointerException localSocketException)
    {
      Log.e(this.mTag, "ALERT: Failed to get interface object: ", localSocketException);
    }
  }
  
  private void handleIPv4Failure()
  {
    clearIPv4Address();
    this.mDhcpResults = null;
    this.mCallback.onNewDhcpResults(null);
    handleProvisioningFailure();
  }
  
  private void handleIPv4Success(DhcpResults paramDhcpResults)
  {
    this.mDhcpResults = new DhcpResults(paramDhcpResults);
    LinkProperties localLinkProperties = assembleLinkProperties();
    LinkProperties.ProvisioningChange localProvisioningChange = setLinkProperties(localLinkProperties);
    this.mCallback.onNewDhcpResults(paramDhcpResults);
    dispatchCallback(localProvisioningChange, localLinkProperties);
  }
  
  private boolean handleLinkPropertiesUpdate(boolean paramBoolean)
  {
    LinkProperties localLinkProperties = assembleLinkProperties();
    if (linkPropertiesUnchanged(localLinkProperties)) {
      return true;
    }
    LinkProperties.ProvisioningChange localProvisioningChange = setLinkProperties(localLinkProperties);
    if (paramBoolean) {
      dispatchCallback(localProvisioningChange, localLinkProperties);
    }
    return localProvisioningChange != LinkProperties.ProvisioningChange.LOST_PROVISIONING;
  }
  
  private void handleProvisioningFailure()
  {
    LinkProperties localLinkProperties = assembleLinkProperties();
    LinkProperties.ProvisioningChange localProvisioningChange2 = setLinkProperties(localLinkProperties);
    LinkProperties.ProvisioningChange localProvisioningChange1 = localProvisioningChange2;
    if (localProvisioningChange2 == LinkProperties.ProvisioningChange.STILL_NOT_PROVISIONED) {
      localProvisioningChange1 = LinkProperties.ProvisioningChange.LOST_PROVISIONING;
    }
    dispatchCallback(localProvisioningChange1, localLinkProperties);
    if (localProvisioningChange1 == LinkProperties.ProvisioningChange.LOST_PROVISIONING) {
      transitionTo(this.mStoppingState);
    }
  }
  
  private static boolean isProvisioned(LinkProperties paramLinkProperties)
  {
    if (!paramLinkProperties.isProvisioned()) {
      return paramLinkProperties.hasIPv4Address();
    }
    return true;
  }
  
  private boolean linkPropertiesUnchanged(LinkProperties paramLinkProperties)
  {
    return Objects.equals(paramLinkProperties, this.mLinkProperties);
  }
  
  private void logError(String paramString, Object... paramVarArgs)
  {
    this.mLocalLog.log("ERROR " + String.format(paramString, paramVarArgs));
  }
  
  private void recordMetric(int paramInt)
  {
    if (this.mStartTimeMillis <= 0L) {
      Log.wtf(this.mTag, "Start time undefined!");
    }
    long l1 = SystemClock.elapsedRealtime();
    long l2 = this.mStartTimeMillis;
    this.mMetricsLog.log(new IpManagerEvent(this.mInterfaceName, paramInt, l1 - l2));
  }
  
  private void resetLinkProperties()
  {
    this.mNetlinkTracker.clearLinkProperties();
    this.mConfiguration = null;
    this.mDhcpResults = null;
    this.mTcpBufferSizes = "";
    this.mHttpProxy = null;
    this.mLinkProperties = new LinkProperties();
    this.mLinkProperties.setInterfaceName(this.mInterfaceName);
  }
  
  private boolean setIPv4Address(LinkAddress paramLinkAddress)
  {
    InterfaceConfiguration localInterfaceConfiguration = new InterfaceConfiguration();
    localInterfaceConfiguration.setLinkAddress(paramLinkAddress);
    try
    {
      this.mNwService.setInterfaceConfig(this.mInterfaceName, localInterfaceConfiguration);
      return true;
    }
    catch (IllegalStateException|RemoteException paramLinkAddress)
    {
      logError("IPv4 configuration failed: %s", new Object[] { paramLinkAddress });
    }
    return false;
  }
  
  private LinkProperties.ProvisioningChange setLinkProperties(LinkProperties paramLinkProperties)
  {
    if (this.mApfFilter != null) {
      this.mApfFilter.setLinkProperties(paramLinkProperties);
    }
    if (this.mIpReachabilityMonitor != null) {
      this.mIpReachabilityMonitor.updateLinkProperties(paramLinkProperties);
    }
    LinkProperties.ProvisioningChange localProvisioningChange = compareProvisioning(this.mLinkProperties, paramLinkProperties);
    this.mLinkProperties = new LinkProperties(paramLinkProperties);
    if (localProvisioningChange == LinkProperties.ProvisioningChange.GAINED_PROVISIONING) {
      this.mProvisioningTimeoutAlarm.cancel();
    }
    return localProvisioningChange;
  }
  
  private boolean startIPv4()
  {
    if (this.mConfiguration.mStaticIpConfig != null) {
      if (setIPv4Address(this.mConfiguration.mStaticIpConfig.ipAddress)) {
        handleIPv4Success(new DhcpResults(this.mConfiguration.mStaticIpConfig));
      }
    }
    for (;;)
    {
      return true;
      return false;
      this.mDhcpClient = DhcpClient.makeDhcpClient(this.mContext, this, this.mInterfaceName);
      this.mDhcpClient.registerForPreDhcpNotification();
      this.mDhcpClient.sendMessage(196609);
    }
  }
  
  private boolean startIPv6()
  {
    try
    {
      this.mNwService.setInterfaceIpv6PrivacyExtensions(this.mInterfaceName, true);
      this.mNwService.enableIpv6(this.mInterfaceName);
      return true;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      logError("Unable to change interface settings: %s", new Object[] { localIllegalStateException });
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      logError("Unable to change interface settings: %s", new Object[] { localRemoteException });
    }
    return false;
  }
  
  private boolean startIpReachabilityMonitor()
  {
    try
    {
      this.mIpReachabilityMonitor = new IpReachabilityMonitor(this.mContext, this.mInterfaceName, new IpReachabilityMonitor.Callback()
      {
        public void notifyLost(InetAddress paramAnonymousInetAddress, String paramAnonymousString)
        {
          IpManager.this.mCallback.onReachabilityLost(paramAnonymousString);
        }
      }, this.mAvoidBadWifiTracker);
      if (this.mIpReachabilityMonitor != null) {
        return true;
      }
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;)
      {
        logError("IpReachabilityMonitor failure: %s", new Object[] { localIllegalArgumentException });
        this.mIpReachabilityMonitor = null;
      }
    }
    return false;
  }
  
  private void stopAllIP()
  {
    try
    {
      this.mNwService.disableIpv6(this.mInterfaceName);
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          this.mNwService.clearInterfaceAddresses(this.mInterfaceName);
          return;
        }
        catch (Exception localException2)
        {
          Log.e(this.mTag, "Failed to clear addresses " + localException2);
        }
        localException1 = localException1;
        Log.e(this.mTag, "Failed to disable IPv6" + localException1);
      }
    }
  }
  
  public void completedPreDhcpAction()
  {
    sendMessage(4);
  }
  
  public void confirmConfiguration()
  {
    sendMessage(3);
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0) && ("confirm".equals(paramArrayOfString[0])))
    {
      confirmConfiguration();
      return;
    }
    paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
    paramPrintWriter.println("APF dump:");
    paramPrintWriter.increaseIndent();
    ApfFilter localApfFilter = this.mApfFilter;
    if (localApfFilter != null) {
      localApfFilter.dump(paramPrintWriter);
    }
    for (;;)
    {
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.println(this.mTag + " StateMachine dump:");
      paramPrintWriter.increaseIndent();
      this.mLocalLog.readOnlyLocalLog().dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      paramPrintWriter.decreaseIndent();
      return;
      paramPrintWriter.println("No apf support");
    }
  }
  
  protected String getLogRecString(Message paramMessage)
  {
    String str = this.mInterfaceName;
    if (this.mNetworkInterface == null) {}
    for (int i = -1;; i = this.mNetworkInterface.getIndex())
    {
      str = String.format("%s/%d %d %d %s [%s]", new Object[] { str, Integer.valueOf(i), Integer.valueOf(paramMessage.arg1), Integer.valueOf(paramMessage.arg2), Objects.toString(paramMessage.obj), this.mMsgStateLogger });
      paramMessage = getWhatToString(paramMessage.what) + " " + str;
      this.mLocalLog.log(paramMessage);
      this.mMsgStateLogger.reset();
      return str;
    }
  }
  
  protected String getWhatToString(int paramInt)
  {
    return (String)sWhatToString.get(paramInt, "UNKNOWN: " + Integer.toString(paramInt));
  }
  
  protected void onQuitting()
  {
    this.mCallback.onQuit();
  }
  
  protected boolean recordLogRec(Message paramMessage)
  {
    if (paramMessage.what != 5) {}
    for (boolean bool = true;; bool = false)
    {
      if (!bool) {
        this.mMsgStateLogger.reset();
      }
      return bool;
    }
  }
  
  public void setHttpProxy(ProxyInfo paramProxyInfo)
  {
    sendMessage(7, paramProxyInfo);
  }
  
  public void setMulticastFilter(boolean paramBoolean)
  {
    sendMessage(8, Boolean.valueOf(paramBoolean));
  }
  
  public void setTcpBufferSizes(String paramString)
  {
    sendMessage(6, paramString);
  }
  
  public void shutdown()
  {
    stop();
    quit();
  }
  
  public void startProvisioning()
  {
    startProvisioning(new ProvisioningConfiguration());
  }
  
  public void startProvisioning(StaticIpConfiguration paramStaticIpConfiguration)
  {
    startProvisioning(buildProvisioningConfiguration().withStaticConfiguration(paramStaticIpConfiguration).build());
  }
  
  public void startProvisioning(ProvisioningConfiguration paramProvisioningConfiguration)
  {
    getNetworkInterface();
    this.mCallback.setNeighborDiscoveryOffload(true);
    sendMessage(2, new ProvisioningConfiguration(paramProvisioningConfiguration));
  }
  
  public void stop()
  {
    sendMessage(1);
  }
  
  public static class Callback
  {
    public void installPacketFilter(byte[] paramArrayOfByte) {}
    
    public void onLinkPropertiesChange(LinkProperties paramLinkProperties) {}
    
    public void onNewDhcpResults(DhcpResults paramDhcpResults) {}
    
    public void onPostDhcpAction() {}
    
    public void onPreDhcpAction() {}
    
    public void onProvisioningFailure(LinkProperties paramLinkProperties) {}
    
    public void onProvisioningSuccess(LinkProperties paramLinkProperties) {}
    
    public void onQuit() {}
    
    public void onReachabilityLost(String paramString) {}
    
    public void setFallbackMulticastFilter(boolean paramBoolean) {}
    
    public void setNeighborDiscoveryOffload(boolean paramBoolean) {}
  }
  
  private class LoggingCallbackWrapper
    extends IpManager.Callback
  {
    private static final String PREFIX = "INVOKE ";
    private IpManager.Callback mCallback;
    
    public LoggingCallbackWrapper(IpManager.Callback paramCallback)
    {
      this.mCallback = paramCallback;
    }
    
    private void log(String paramString)
    {
      IpManager.-get7(IpManager.this).log("INVOKE " + paramString);
    }
    
    public void installPacketFilter(byte[] paramArrayOfByte)
    {
      this.mCallback.installPacketFilter(paramArrayOfByte);
      log("installPacketFilter(byte[" + paramArrayOfByte.length + "])");
    }
    
    public void onLinkPropertiesChange(LinkProperties paramLinkProperties)
    {
      this.mCallback.onLinkPropertiesChange(paramLinkProperties);
      log("onLinkPropertiesChange({" + paramLinkProperties + "})");
    }
    
    public void onNewDhcpResults(DhcpResults paramDhcpResults)
    {
      this.mCallback.onNewDhcpResults(paramDhcpResults);
      log("onNewDhcpResults({" + paramDhcpResults + "})");
    }
    
    public void onPostDhcpAction()
    {
      this.mCallback.onPostDhcpAction();
      log("onPostDhcpAction()");
    }
    
    public void onPreDhcpAction()
    {
      this.mCallback.onPreDhcpAction();
      log("onPreDhcpAction()");
    }
    
    public void onProvisioningFailure(LinkProperties paramLinkProperties)
    {
      this.mCallback.onProvisioningFailure(paramLinkProperties);
      log("onProvisioningFailure({" + paramLinkProperties + "})");
    }
    
    public void onProvisioningSuccess(LinkProperties paramLinkProperties)
    {
      this.mCallback.onProvisioningSuccess(paramLinkProperties);
      log("onProvisioningSuccess({" + paramLinkProperties + "})");
    }
    
    public void onQuit()
    {
      this.mCallback.onQuit();
      log("onQuit()");
    }
    
    public void onReachabilityLost(String paramString)
    {
      this.mCallback.onReachabilityLost(paramString);
      log("onReachabilityLost(" + paramString + ")");
    }
    
    public void setFallbackMulticastFilter(boolean paramBoolean)
    {
      this.mCallback.setFallbackMulticastFilter(paramBoolean);
      log("setFallbackMulticastFilter(" + paramBoolean + ")");
    }
    
    public void setNeighborDiscoveryOffload(boolean paramBoolean)
    {
      this.mCallback.setNeighborDiscoveryOffload(paramBoolean);
      log("setNeighborDiscoveryOffload(" + paramBoolean + ")");
    }
  }
  
  private static class MessageHandlingLogger
  {
    public String processedInState;
    public String receivedInState;
    
    public void handled(State paramState, IState paramIState)
    {
      this.processedInState = paramState.getClass().getSimpleName();
      this.receivedInState = paramIState.getName();
    }
    
    public void reset()
    {
      this.processedInState = null;
      this.receivedInState = null;
    }
    
    public String toString()
    {
      return String.format("rcvd_in=%s, proc_in=%s", new Object[] { this.receivedInState, this.processedInState });
    }
  }
  
  public static class ProvisioningConfiguration
  {
    private static final int DEFAULT_TIMEOUT_MS = 36000;
    ApfCapabilities mApfCapabilities;
    boolean mEnableIPv4 = true;
    boolean mEnableIPv6 = true;
    int mProvisioningTimeoutMs = 36000;
    int mRequestedPreDhcpActionMs;
    StaticIpConfiguration mStaticIpConfig;
    boolean mUsingIpReachabilityMonitor = true;
    
    public ProvisioningConfiguration() {}
    
    public ProvisioningConfiguration(ProvisioningConfiguration paramProvisioningConfiguration)
    {
      this.mEnableIPv4 = paramProvisioningConfiguration.mEnableIPv4;
      this.mEnableIPv6 = paramProvisioningConfiguration.mEnableIPv6;
      this.mUsingIpReachabilityMonitor = paramProvisioningConfiguration.mUsingIpReachabilityMonitor;
      this.mRequestedPreDhcpActionMs = paramProvisioningConfiguration.mRequestedPreDhcpActionMs;
      this.mStaticIpConfig = paramProvisioningConfiguration.mStaticIpConfig;
      this.mApfCapabilities = paramProvisioningConfiguration.mApfCapabilities;
      this.mProvisioningTimeoutMs = paramProvisioningConfiguration.mProvisioningTimeoutMs;
    }
    
    public String toString()
    {
      return new StringJoiner(", ", getClass().getSimpleName() + "{", "}").add("mEnableIPv4: " + this.mEnableIPv4).add("mEnableIPv6: " + this.mEnableIPv6).add("mUsingIpReachabilityMonitor: " + this.mUsingIpReachabilityMonitor).add("mRequestedPreDhcpActionMs: " + this.mRequestedPreDhcpActionMs).add("mStaticIpConfig: " + this.mStaticIpConfig).add("mApfCapabilities: " + this.mApfCapabilities).add("mProvisioningTimeoutMs: " + this.mProvisioningTimeoutMs).toString();
    }
    
    public static class Builder
    {
      private IpManager.ProvisioningConfiguration mConfig = new IpManager.ProvisioningConfiguration();
      
      public IpManager.ProvisioningConfiguration build()
      {
        return new IpManager.ProvisioningConfiguration(this.mConfig);
      }
      
      public Builder withApfCapabilities(ApfCapabilities paramApfCapabilities)
      {
        this.mConfig.mApfCapabilities = paramApfCapabilities;
        return this;
      }
      
      public Builder withPreDhcpAction()
      {
        this.mConfig.mRequestedPreDhcpActionMs = 36000;
        return this;
      }
      
      public Builder withPreDhcpAction(int paramInt)
      {
        this.mConfig.mRequestedPreDhcpActionMs = paramInt;
        return this;
      }
      
      public Builder withProvisioningTimeoutMs(int paramInt)
      {
        this.mConfig.mProvisioningTimeoutMs = paramInt;
        return this;
      }
      
      public Builder withStaticConfiguration(StaticIpConfiguration paramStaticIpConfiguration)
      {
        this.mConfig.mStaticIpConfig = paramStaticIpConfiguration;
        return this;
      }
      
      public Builder withoutIPv4()
      {
        this.mConfig.mEnableIPv4 = false;
        return this;
      }
      
      public Builder withoutIPv6()
      {
        this.mConfig.mEnableIPv6 = false;
        return this;
      }
      
      public Builder withoutIpReachabilityMonitor()
      {
        this.mConfig.mUsingIpReachabilityMonitor = false;
        return this;
      }
    }
  }
  
  class RunningState
    extends State
  {
    private boolean mDhcpActionInFlight;
    
    RunningState() {}
    
    private void ensureDhcpAction()
    {
      if (!this.mDhcpActionInFlight)
      {
        IpManager.this.mCallback.onPreDhcpAction();
        this.mDhcpActionInFlight = true;
        long l1 = SystemClock.elapsedRealtime();
        long l2 = IpManager.-get2(IpManager.this).mRequestedPreDhcpActionMs;
        IpManager.-get3(IpManager.this).schedule(l1 + l2);
      }
    }
    
    private void stopDhcpAction()
    {
      IpManager.-get3(IpManager.this).cancel();
      if (this.mDhcpActionInFlight)
      {
        IpManager.this.mCallback.onPostDhcpAction();
        this.mDhcpActionInFlight = false;
      }
    }
    
    public void enter()
    {
      IpManager.-set0(IpManager.this, ApfFilter.maybeCreate(IpManager.-get2(IpManager.this).mApfCapabilities, IpManager.-get10(IpManager.this), IpManager.this.mCallback, IpManager.-get9(IpManager.this)));
      if (IpManager.-get0(IpManager.this) == null) {
        IpManager.this.mCallback.setFallbackMulticastFilter(IpManager.-get9(IpManager.this));
      }
      if ((!IpManager.-get2(IpManager.this).mEnableIPv6) || (IpManager.-wrap3(IpManager.this)))
      {
        if ((!IpManager.-get2(IpManager.this).mEnableIPv4) || (IpManager.-wrap2(IpManager.this))) {
          if ((IpManager.-get2(IpManager.this).mUsingIpReachabilityMonitor) && (!IpManager.-wrap4(IpManager.this))) {
            break label185;
          }
        }
      }
      else
      {
        IpManager.-wrap9(IpManager.this, 5);
        IpManager.-wrap16(IpManager.this, IpManager.-get16(IpManager.this));
        return;
      }
      IpManager.-wrap9(IpManager.this, 4);
      IpManager.-wrap16(IpManager.this, IpManager.-get16(IpManager.this));
      return;
      label185:
      IpManager.-wrap9(IpManager.this, 6);
      IpManager.-wrap16(IpManager.this, IpManager.-get16(IpManager.this));
    }
    
    public void exit()
    {
      stopDhcpAction();
      if (IpManager.-get5(IpManager.this) != null)
      {
        IpManager.-get5(IpManager.this).stop();
        IpManager.-set4(IpManager.this, null);
      }
      if (IpManager.-get4(IpManager.this) != null)
      {
        IpManager.-get4(IpManager.this).sendMessage(196610);
        IpManager.-get4(IpManager.this).doQuit();
      }
      if (IpManager.-get0(IpManager.this) != null)
      {
        IpManager.-get0(IpManager.this).shutdown();
        IpManager.-set0(IpManager.this, null);
      }
      IpManager.-wrap14(IpManager.this);
    }
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 1: 
        IpManager.-wrap16(IpManager.this, IpManager.-get16(IpManager.this));
      }
      for (;;)
      {
        IpManager.-get8(IpManager.this).handled(this, IpManager.-wrap5(IpManager.this));
        return true;
        Log.e(IpManager.-get17(IpManager.this), "ALERT: START received in StartedState. Please fix caller.");
        continue;
        if (IpManager.-get5(IpManager.this) != null)
        {
          IpManager.-get5(IpManager.this).probeAll();
          continue;
          if (IpManager.-get4(IpManager.this) != null)
          {
            IpManager.-get4(IpManager.this).sendMessage(196614);
            continue;
            if (!IpManager.-wrap0(IpManager.this, true))
            {
              IpManager.-wrap16(IpManager.this, IpManager.-get16(IpManager.this));
              continue;
              IpManager.-set7(IpManager.this, (String)paramMessage.obj);
              IpManager.-wrap0(IpManager.this, true);
              continue;
              IpManager.-set3(IpManager.this, (ProxyInfo)paramMessage.obj);
              IpManager.-wrap0(IpManager.this, true);
              continue;
              IpManager.-set5(IpManager.this, ((Boolean)paramMessage.obj).booleanValue());
              if (IpManager.-get0(IpManager.this) != null)
              {
                IpManager.-get0(IpManager.this).setMulticastFilter(IpManager.-get9(IpManager.this));
              }
              else
              {
                IpManager.this.mCallback.setFallbackMulticastFilter(IpManager.-get9(IpManager.this));
                continue;
                stopDhcpAction();
                continue;
                if (IpManager.-get2(IpManager.this).mRequestedPreDhcpActionMs > 0)
                {
                  ensureDhcpAction();
                }
                else
                {
                  IpManager.this.sendMessage(4);
                  continue;
                  IpManager.-wrap6(IpManager.this);
                  continue;
                  paramMessage = (LinkAddress)paramMessage.obj;
                  if (IpManager.-wrap1(IpManager.this, paramMessage))
                  {
                    IpManager.-get4(IpManager.this).sendMessage(196617);
                  }
                  else
                  {
                    Log.e(IpManager.-get17(IpManager.this), "Failed to set IPv4 address!");
                    IpManager.-wrap8(IpManager.this, LinkProperties.ProvisioningChange.LOST_PROVISIONING, new LinkProperties(IpManager.-get6(IpManager.this)));
                    IpManager.-wrap16(IpManager.this, IpManager.-get16(IpManager.this));
                    continue;
                    stopDhcpAction();
                    switch (paramMessage.arg1)
                    {
                    default: 
                      Log.e(IpManager.-get17(IpManager.this), "Unknown CMD_POST_DHCP_ACTION status:" + paramMessage.arg1);
                      break;
                    case 1: 
                      IpManager.-wrap11(IpManager.this, (DhcpResults)paramMessage.obj);
                      break;
                    case 2: 
                      IpManager.-wrap10(IpManager.this);
                      continue;
                      Log.e(IpManager.-get17(IpManager.this), "Unexpected CMD_ON_QUIT.");
                      IpManager.-set2(IpManager.this, null);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  class StartedState
    extends State
  {
    StartedState() {}
    
    public void enter()
    {
      IpManager.-set6(IpManager.this, SystemClock.elapsedRealtime());
      if (IpManager.-get2(IpManager.this).mProvisioningTimeoutMs > 0)
      {
        long l1 = SystemClock.elapsedRealtime();
        long l2 = IpManager.-get2(IpManager.this).mProvisioningTimeoutMs;
        IpManager.-get11(IpManager.this).schedule(l1 + l2);
      }
      if (readyToProceed())
      {
        IpManager.-wrap16(IpManager.this, IpManager.-get12(IpManager.this));
        return;
      }
      IpManager.-wrap15(IpManager.this);
    }
    
    public void exit()
    {
      IpManager.-get11(IpManager.this).cancel();
    }
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        IpManager.-wrap7(IpManager.this, paramMessage);
      }
      for (;;)
      {
        IpManager.-get8(IpManager.this).handled(this, IpManager.-wrap5(IpManager.this));
        return true;
        IpManager.-wrap16(IpManager.this, IpManager.-get16(IpManager.this));
        continue;
        IpManager.-wrap0(IpManager.this, false);
        if (readyToProceed())
        {
          IpManager.-wrap16(IpManager.this, IpManager.-get12(IpManager.this));
          continue;
          IpManager.-wrap12(IpManager.this);
        }
      }
    }
    
    boolean readyToProceed()
    {
      return (!IpManager.-get6(IpManager.this).hasIPv4Address()) && (!IpManager.-get6(IpManager.this).hasGlobalIPv6Address());
    }
  }
  
  class StoppedState
    extends State
  {
    StoppedState() {}
    
    public void enter()
    {
      IpManager.-wrap15(IpManager.this);
      IpManager.-wrap14(IpManager.this);
      if (IpManager.-get13(IpManager.this) > 0L)
      {
        IpManager.-wrap13(IpManager.this, 3);
        IpManager.-set6(IpManager.this, 0L);
      }
    }
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 2: 
        IpManager.-set1(IpManager.this, (IpManager.ProvisioningConfiguration)paramMessage.obj);
        IpManager.-wrap16(IpManager.this, IpManager.-get14(IpManager.this));
      }
      for (;;)
      {
        IpManager.-get8(IpManager.this).handled(this, IpManager.-wrap5(IpManager.this));
        return true;
        IpManager.-wrap0(IpManager.this, false);
        continue;
        IpManager.-set7(IpManager.this, (String)paramMessage.obj);
        IpManager.-wrap0(IpManager.this, false);
        continue;
        IpManager.-set3(IpManager.this, (ProxyInfo)paramMessage.obj);
        IpManager.-wrap0(IpManager.this, false);
        continue;
        IpManager.-set5(IpManager.this, ((Boolean)paramMessage.obj).booleanValue());
        continue;
        Log.e(IpManager.-get17(IpManager.this), "Unexpected CMD_ON_QUIT (already stopped).");
      }
    }
  }
  
  class StoppingState
    extends State
  {
    StoppingState() {}
    
    public void enter()
    {
      if (IpManager.-get4(IpManager.this) == null) {
        IpManager.-wrap16(IpManager.this, IpManager.-get15(IpManager.this));
      }
    }
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        IpManager.-wrap7(IpManager.this, paramMessage);
      }
      for (;;)
      {
        IpManager.-get8(IpManager.this).handled(this, IpManager.-wrap5(IpManager.this));
        return true;
        IpManager.-wrap6(IpManager.this);
        continue;
        IpManager.-set2(IpManager.this, null);
        IpManager.-wrap16(IpManager.this, IpManager.-get15(IpManager.this));
      }
    }
  }
  
  public static class WaitForProvisioningCallback
    extends IpManager.Callback
  {
    private LinkProperties mCallbackLinkProperties;
    
    public void onProvisioningFailure(LinkProperties paramLinkProperties)
    {
      try
      {
        this.mCallbackLinkProperties = null;
        notify();
        return;
      }
      finally
      {
        paramLinkProperties = finally;
        throw paramLinkProperties;
      }
    }
    
    public void onProvisioningSuccess(LinkProperties paramLinkProperties)
    {
      try
      {
        this.mCallbackLinkProperties = paramLinkProperties;
        notify();
        return;
      }
      finally
      {
        paramLinkProperties = finally;
        throw paramLinkProperties;
      }
    }
    
    public LinkProperties waitForProvisioning()
    {
      try
      {
        wait();
        LinkProperties localLinkProperties = this.mCallbackLinkProperties;
        return localLinkProperties;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;) {}
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ip/IpManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */