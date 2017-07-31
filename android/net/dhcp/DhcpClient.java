package android.net.dhcp;

import android.content.Context;
import android.net.DhcpResults;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.metrics.DhcpClientEvent;
import android.net.metrics.DhcpErrorEvent;
import android.net.metrics.IpConnectivityLog;
import android.os.Message;
import android.os.SystemClock;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.PacketSocketAddress;
import android.util.EventLog;
import android.util.Log;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.util.HexDump;
import com.android.internal.util.MessageUtils;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.internal.util.WakeupMessage;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import libcore.io.IoBridge;

public class DhcpClient
  extends StateMachine
{
  public static final int CMD_CLEAR_LINKADDRESS = 196615;
  public static final int CMD_CONFIGURE_LINKADDRESS = 196616;
  private static final int CMD_EXPIRE_DHCP = 196714;
  private static final int CMD_KICK = 196709;
  public static final int CMD_ON_QUIT = 196613;
  public static final int CMD_POST_DHCP_ACTION = 196612;
  public static final int CMD_PRE_DHCP_ACTION = 196611;
  public static final int CMD_PRE_DHCP_ACTION_COMPLETE = 196614;
  private static final int CMD_REBIND_DHCP = 196713;
  private static final int CMD_RECEIVED_PACKET = 196710;
  private static final int CMD_RENEW_DHCP = 196712;
  public static final int CMD_START_DHCP = 196609;
  public static final int CMD_STOP_DHCP = 196610;
  private static final int CMD_TIMEOUT = 196711;
  private static final boolean DBG = true;
  public static final int DHCP_FAILURE = 2;
  public static final int DHCP_SUCCESS = 1;
  private static final int DHCP_TIMEOUT_MS = 36000;
  private static final boolean DO_UNICAST = false;
  public static final int EVENT_LINKADDRESS_CONFIGURED = 196617;
  private static final int FIRST_TIMEOUT_MS = 2000;
  private static final int MAX_TIMEOUT_MS = 128000;
  private static final boolean MSG_DBG = false;
  private static final boolean PACKET_DBG = false;
  private static final int PRIVATE_BASE = 196708;
  private static final int PUBLIC_BASE = 196608;
  static final byte[] REQUESTED_PARAMS = { 1, 3, 6, 15, 26, 28, 51, 58, 59, 43 };
  private static final int SECONDS = 1000;
  private static final boolean STATE_DBG = false;
  private static final String TAG = "DhcpClient";
  private static final Class[] sMessageClasses = { DhcpClient.class };
  private static final SparseArray<String> sMessageNames = MessageUtils.findMessageNames(sMessageClasses);
  private State mConfiguringInterfaceState = new ConfiguringInterfaceState();
  private final Context mContext;
  private final StateMachine mController;
  private State mDhcpBoundState = new DhcpBoundState();
  private State mDhcpHaveLeaseState = new DhcpHaveLeaseState();
  private State mDhcpInitRebootState = new DhcpInitRebootState();
  private State mDhcpInitState = new DhcpInitState();
  private DhcpResults mDhcpLease;
  private long mDhcpLeaseExpiry;
  private State mDhcpRebindingState = new DhcpRebindingState();
  private State mDhcpRebootingState = new DhcpRebootingState();
  private State mDhcpRenewingState = new DhcpRenewingState();
  private State mDhcpRequestingState = new DhcpRequestingState();
  private State mDhcpSelectingState = new DhcpSelectingState();
  private State mDhcpState = new DhcpState();
  private final WakeupMessage mExpiryAlarm;
  private byte[] mHwAddr;
  private NetworkInterface mIface;
  private final String mIfaceName;
  private PacketSocketAddress mInterfaceBroadcastAddr;
  private final WakeupMessage mKickAlarm;
  private long mLastBoundExitTime;
  private long mLastInitEnterTime;
  private final IpConnectivityLog mMetricsLog = new IpConnectivityLog();
  private DhcpResults mOffer;
  private FileDescriptor mPacketSock;
  private final Random mRandom;
  private final WakeupMessage mRebindAlarm;
  private ReceiveThread mReceiveThread;
  private boolean mRegisteredForPreDhcpNotification;
  private final WakeupMessage mRenewAlarm;
  private State mStoppedState = new StoppedState();
  private final WakeupMessage mTimeoutAlarm;
  private int mTransactionId;
  private long mTransactionStartMillis;
  private FileDescriptor mUdpSock;
  private State mWaitBeforeRenewalState = new WaitBeforeRenewalState(this.mDhcpRenewingState);
  private State mWaitBeforeStartState = new WaitBeforeStartState(this.mDhcpInitState);
  
  private DhcpClient(Context paramContext, StateMachine paramStateMachine, String paramString)
  {
    super("DhcpClient");
    this.mContext = paramContext;
    this.mController = paramStateMachine;
    this.mIfaceName = paramString;
    addState(this.mStoppedState);
    addState(this.mDhcpState);
    addState(this.mDhcpInitState, this.mDhcpState);
    addState(this.mWaitBeforeStartState, this.mDhcpState);
    addState(this.mDhcpSelectingState, this.mDhcpState);
    addState(this.mDhcpRequestingState, this.mDhcpState);
    addState(this.mDhcpHaveLeaseState, this.mDhcpState);
    addState(this.mConfiguringInterfaceState, this.mDhcpHaveLeaseState);
    addState(this.mDhcpBoundState, this.mDhcpHaveLeaseState);
    addState(this.mWaitBeforeRenewalState, this.mDhcpHaveLeaseState);
    addState(this.mDhcpRenewingState, this.mDhcpHaveLeaseState);
    addState(this.mDhcpRebindingState, this.mDhcpHaveLeaseState);
    addState(this.mDhcpInitRebootState, this.mDhcpState);
    addState(this.mDhcpRebootingState, this.mDhcpState);
    setInitialState(this.mStoppedState);
    this.mRandom = new Random();
    this.mKickAlarm = makeWakeupMessage("KICK", 196709);
    this.mTimeoutAlarm = makeWakeupMessage("TIMEOUT", 196711);
    this.mRenewAlarm = makeWakeupMessage("RENEW", 196712);
    this.mRebindAlarm = makeWakeupMessage("REBIND", 196713);
    this.mExpiryAlarm = makeWakeupMessage("EXPIRY", 196714);
  }
  
  private void acceptDhcpResults(DhcpResults paramDhcpResults, String paramString)
  {
    this.mDhcpLease = paramDhcpResults;
    this.mOffer = null;
    Log.d("DhcpClient", paramString + " lease: " + this.mDhcpLease);
    notifySuccess();
  }
  
  private void clearDhcpState()
  {
    this.mDhcpLease = null;
    this.mDhcpLeaseExpiry = 0L;
    this.mOffer = null;
  }
  
  private static void closeQuietly(FileDescriptor paramFileDescriptor)
  {
    try
    {
      IoBridge.closeAndSignalBlockedThreads(paramFileDescriptor);
      return;
    }
    catch (IOException paramFileDescriptor) {}
  }
  
  private void closeSockets()
  {
    closeQuietly(this.mUdpSock);
    closeQuietly(this.mPacketSock);
  }
  
  private boolean connectUdpSock(Inet4Address paramInet4Address)
  {
    try
    {
      Os.connect(this.mUdpSock, paramInet4Address, 67);
      return true;
    }
    catch (SocketException|ErrnoException paramInet4Address)
    {
      Log.e("DhcpClient", "Error connecting UDP socket", paramInet4Address);
    }
    return false;
  }
  
  private short getSecs()
  {
    return (short)(int)((SystemClock.elapsedRealtime() - this.mTransactionStartMillis) / 1000L);
  }
  
  private boolean initInterface()
  {
    try
    {
      this.mIface = NetworkInterface.getByName(this.mIfaceName);
      this.mHwAddr = this.mIface.getHardwareAddress();
      this.mInterfaceBroadcastAddr = new PacketSocketAddress(this.mIface.getIndex(), DhcpPacket.ETHER_BROADCAST);
      return true;
    }
    catch (SocketException|NullPointerException localSocketException)
    {
      Log.e("DhcpClient", "Can't determine ifindex or MAC address for " + this.mIfaceName, localSocketException);
    }
    return false;
  }
  
  private boolean initPacketSocket()
  {
    try
    {
      this.mPacketSock = Os.socket(OsConstants.AF_PACKET, OsConstants.SOCK_RAW, OsConstants.ETH_P_IP);
      PacketSocketAddress localPacketSocketAddress = new PacketSocketAddress((short)OsConstants.ETH_P_IP, this.mIface.getIndex());
      Os.bind(this.mPacketSock, localPacketSocketAddress);
      NetworkUtils.attachDhcpFilter(this.mPacketSock);
      return true;
    }
    catch (SocketException|ErrnoException localSocketException)
    {
      Log.e("DhcpClient", "Error creating packet socket", localSocketException);
    }
    return false;
  }
  
  private boolean initSockets()
  {
    if (initPacketSocket()) {
      return initUdpSocket();
    }
    return false;
  }
  
  private boolean initUdpSocket()
  {
    try
    {
      this.mUdpSock = Os.socket(OsConstants.AF_INET, OsConstants.SOCK_DGRAM, OsConstants.IPPROTO_UDP);
      Os.setsockoptInt(this.mUdpSock, OsConstants.SOL_SOCKET, OsConstants.SO_REUSEADDR, 1);
      Os.setsockoptIfreq(this.mUdpSock, OsConstants.SOL_SOCKET, OsConstants.SO_BINDTODEVICE, this.mIfaceName);
      Os.setsockoptInt(this.mUdpSock, OsConstants.SOL_SOCKET, OsConstants.SO_BROADCAST, 1);
      Os.setsockoptInt(this.mUdpSock, OsConstants.SOL_SOCKET, OsConstants.SO_RCVBUF, 0);
      Os.bind(this.mUdpSock, Inet4Address.ANY, 68);
      NetworkUtils.protectFromVpn(this.mUdpSock);
      return true;
    }
    catch (SocketException|ErrnoException localSocketException)
    {
      Log.e("DhcpClient", "Error creating UDP socket", localSocketException);
    }
    return false;
  }
  
  private void logError(int paramInt)
  {
    this.mMetricsLog.log(new DhcpErrorEvent(this.mIfaceName, paramInt));
  }
  
  private void logState(String paramString, int paramInt)
  {
    this.mMetricsLog.log(new DhcpClientEvent(this.mIfaceName, paramString, paramInt));
  }
  
  public static DhcpClient makeDhcpClient(Context paramContext, StateMachine paramStateMachine, String paramString)
  {
    paramContext = new DhcpClient(paramContext, paramStateMachine, paramString);
    paramContext.start();
    return paramContext;
  }
  
  private WakeupMessage makeWakeupMessage(String paramString, int paramInt)
  {
    paramString = DhcpClient.class.getSimpleName() + "." + this.mIfaceName + "." + paramString;
    return new WakeupMessage(this.mContext, getHandler(), paramString, paramInt);
  }
  
  private void notifyFailure()
  {
    this.mController.sendMessage(196612, 2, 0, null);
  }
  
  private void notifySuccess()
  {
    this.mController.sendMessage(196612, 1, 0, new DhcpResults(this.mDhcpLease));
  }
  
  private void scheduleLeaseTimers()
  {
    if (this.mDhcpLeaseExpiry == 0L)
    {
      Log.d("DhcpClient", "Infinite lease, no timer scheduling needed");
      return;
    }
    long l1 = SystemClock.elapsedRealtime();
    long l2 = this.mDhcpLeaseExpiry - l1;
    long l3 = l2 / 2L;
    long l4 = 7L * l2 / 8L;
    this.mRenewAlarm.schedule(l1 + l3);
    this.mRebindAlarm.schedule(l1 + l4);
    this.mExpiryAlarm.schedule(l1 + l2);
    Log.d("DhcpClient", "Scheduling renewal in " + l3 / 1000L + "s");
    Log.d("DhcpClient", "Scheduling rebind in " + l4 / 1000L + "s");
    Log.d("DhcpClient", "Scheduling expiry in " + l2 / 1000L + "s");
  }
  
  private boolean sendDiscoverPacket()
  {
    return transmitPacket(DhcpPacket.buildDiscoverPacket(0, this.mTransactionId, getSecs(), this.mHwAddr, false, REQUESTED_PARAMS), "DHCPDISCOVER", 0, DhcpPacket.INADDR_BROADCAST);
  }
  
  private boolean sendRequestPacket(Inet4Address paramInet4Address1, Inet4Address paramInet4Address2, Inet4Address paramInet4Address3, Inet4Address paramInet4Address4)
  {
    int i;
    ByteBuffer localByteBuffer;
    if (DhcpPacket.INADDR_ANY.equals(paramInet4Address1))
    {
      i = 0;
      localByteBuffer = DhcpPacket.buildRequestPacket(i, this.mTransactionId, getSecs(), paramInet4Address1, false, this.mHwAddr, paramInet4Address2, paramInet4Address3, REQUESTED_PARAMS, null);
      if (paramInet4Address3 == null) {
        break label112;
      }
    }
    label112:
    for (paramInet4Address3 = paramInet4Address3.getHostAddress();; paramInet4Address3 = null)
    {
      return transmitPacket(localByteBuffer, "DHCPREQUEST ciaddr=" + paramInet4Address1.getHostAddress() + " request=" + paramInet4Address2.getHostAddress() + " serverid=" + paramInet4Address3, i, paramInet4Address4);
      i = 2;
      break;
    }
  }
  
  private void startNewTransaction()
  {
    this.mTransactionId = this.mRandom.nextInt();
    this.mTransactionStartMillis = SystemClock.elapsedRealtime();
  }
  
  private boolean transmitPacket(ByteBuffer paramByteBuffer, String paramString, int paramInt, Inet4Address paramInet4Address)
  {
    if (paramInt == 0) {}
    try
    {
      Log.d("DhcpClient", "Broadcasting " + paramString);
      Os.sendto(this.mPacketSock, paramByteBuffer.array(), 0, paramByteBuffer.limit(), 0, this.mInterfaceBroadcastAddr);
      return true;
    }
    catch (ErrnoException|IOException paramByteBuffer)
    {
      Log.e("DhcpClient", "Can't send packet: ", paramByteBuffer);
      return false;
    }
    if ((paramInt == 2) && (paramInet4Address.equals(DhcpPacket.INADDR_BROADCAST)))
    {
      Log.d("DhcpClient", "Broadcasting " + paramString);
      Os.sendto(this.mUdpSock, paramByteBuffer, 0, paramInet4Address, 67);
      return true;
    }
    Log.d("DhcpClient", String.format("Unicasting %s to %s", new Object[] { paramString, Os.getpeername(this.mUdpSock) }));
    Os.write(this.mUdpSock, paramByteBuffer);
    return true;
  }
  
  public void doQuit()
  {
    Log.d("DhcpClient", "doQuit");
    quit();
  }
  
  public boolean isValidPacket(DhcpPacket paramDhcpPacket)
  {
    int i = paramDhcpPacket.getTransactionId();
    if (i != this.mTransactionId)
    {
      Log.d("DhcpClient", "Unexpected transaction ID " + i + ", expected " + this.mTransactionId);
      return false;
    }
    if (!Arrays.equals(paramDhcpPacket.getClientMac(), this.mHwAddr))
    {
      Log.d("DhcpClient", "MAC addr mismatch: got " + HexDump.toHexString(paramDhcpPacket.getClientMac()) + ", expected " + HexDump.toHexString(paramDhcpPacket.getClientMac()));
      return false;
    }
    return true;
  }
  
  protected void onQuitting()
  {
    Log.d("DhcpClient", "onQuitting");
    this.mController.sendMessage(196613);
  }
  
  public void registerForPreDhcpNotification()
  {
    this.mRegisteredForPreDhcpNotification = true;
  }
  
  public void setDhcpLeaseExpiry(DhcpPacket paramDhcpPacket)
  {
    long l1 = 0L;
    long l2 = paramDhcpPacket.getLeaseTimeMillis();
    if (l2 > 0L) {
      l1 = SystemClock.elapsedRealtime() + l2;
    }
    this.mDhcpLeaseExpiry = l1;
  }
  
  class ConfiguringInterfaceState
    extends DhcpClient.LoggingState
  {
    ConfiguringInterfaceState()
    {
      super();
    }
    
    public void enter()
    {
      super.enter();
      DhcpClient.-get1(DhcpClient.this).sendMessage(196616, DhcpClient.-get4(DhcpClient.this).ipAddress);
    }
    
    public boolean processMessage(Message paramMessage)
    {
      super.processMessage(paramMessage);
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get2(DhcpClient.this));
      return true;
    }
  }
  
  class DhcpBoundState
    extends DhcpClient.LoggingState
  {
    DhcpBoundState()
    {
      super();
    }
    
    private void logTimeToBoundState()
    {
      long l = SystemClock.elapsedRealtime();
      if (DhcpClient.-get10(DhcpClient.this) > DhcpClient.-get11(DhcpClient.this))
      {
        DhcpClient.-wrap11(DhcpClient.this, "RenewingBoundState", (int)(l - DhcpClient.-get10(DhcpClient.this)));
        return;
      }
      DhcpClient.-wrap11(DhcpClient.this, "InitialBoundState", (int)(l - DhcpClient.-get11(DhcpClient.this)));
    }
    
    public void enter()
    {
      super.enter();
      if ((DhcpClient.-get4(DhcpClient.this).serverAddress == null) || (DhcpClient.-wrap0(DhcpClient.this, DhcpClient.-get4(DhcpClient.this).serverAddress))) {}
      for (;;)
      {
        DhcpClient.-wrap13(DhcpClient.this);
        logTimeToBoundState();
        return;
        DhcpClient.-wrap12(DhcpClient.this);
        DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get19(DhcpClient.this));
      }
    }
    
    public void exit()
    {
      super.exit();
      DhcpClient.-set0(DhcpClient.this, SystemClock.elapsedRealtime());
    }
    
    public boolean processMessage(Message paramMessage)
    {
      super.processMessage(paramMessage);
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      if (DhcpClient.-get17(DhcpClient.this)) {
        DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get22(DhcpClient.this));
      }
      for (;;)
      {
        return true;
        DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get6(DhcpClient.this));
      }
    }
  }
  
  class DhcpHaveLeaseState
    extends State
  {
    DhcpHaveLeaseState() {}
    
    public void exit()
    {
      DhcpClient.-get18(DhcpClient.this).cancel();
      DhcpClient.-get15(DhcpClient.this).cancel();
      DhcpClient.-get8(DhcpClient.this).cancel();
      DhcpClient.-wrap7(DhcpClient.this);
      DhcpClient.-get1(DhcpClient.this).sendMessage(196615);
    }
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      Log.d("DhcpClient", "Lease expired!");
      DhcpClient.-wrap12(DhcpClient.this);
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get3(DhcpClient.this));
      return true;
    }
  }
  
  class DhcpInitRebootState
    extends DhcpClient.LoggingState
  {
    DhcpInitRebootState()
    {
      super();
    }
  }
  
  class DhcpInitState
    extends DhcpClient.PacketRetransmittingState
  {
    public DhcpInitState()
    {
      super();
    }
    
    public void enter()
    {
      super.enter();
      DhcpClient.-wrap14(DhcpClient.this);
      DhcpClient.-set1(DhcpClient.this, SystemClock.elapsedRealtime());
    }
    
    protected void receivePacket(DhcpPacket paramDhcpPacket)
    {
      if (!DhcpClient.this.isValidPacket(paramDhcpPacket)) {
        return;
      }
      if (!(paramDhcpPacket instanceof DhcpOfferPacket)) {
        return;
      }
      DhcpClient.-set2(DhcpClient.this, paramDhcpPacket.toDhcpResults());
      if (DhcpClient.-get12(DhcpClient.this) != null)
      {
        Log.d("DhcpClient", "Got pending lease: " + DhcpClient.-get12(DhcpClient.this));
        DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get7(DhcpClient.this));
      }
    }
    
    protected boolean sendPacket()
    {
      return DhcpClient.-wrap4(DhcpClient.this);
    }
  }
  
  abstract class DhcpReacquiringState
    extends DhcpClient.PacketRetransmittingState
  {
    protected String mLeaseMsg;
    
    DhcpReacquiringState()
    {
      super();
    }
    
    public void enter()
    {
      super.enter();
      DhcpClient.-wrap14(DhcpClient.this);
    }
    
    protected abstract Inet4Address packetDestination();
    
    protected void receivePacket(DhcpPacket paramDhcpPacket)
    {
      if (!DhcpClient.this.isValidPacket(paramDhcpPacket)) {
        return;
      }
      if ((paramDhcpPacket instanceof DhcpAckPacket))
      {
        localDhcpResults = paramDhcpPacket.toDhcpResults();
        if (localDhcpResults != null)
        {
          if (!DhcpClient.-get4(DhcpClient.this).ipAddress.equals(localDhcpResults.ipAddress))
          {
            Log.d("DhcpClient", "Renewed lease not for our current IP address!");
            DhcpClient.-wrap12(DhcpClient.this);
            DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get3(DhcpClient.this));
          }
          DhcpClient.this.setDhcpLeaseExpiry(paramDhcpPacket);
          DhcpClient.-wrap6(DhcpClient.this, localDhcpResults, this.mLeaseMsg);
          DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get2(DhcpClient.this));
        }
      }
      while (!(paramDhcpPacket instanceof DhcpNakPacket))
      {
        DhcpResults localDhcpResults;
        return;
      }
      Log.d("DhcpClient", "Received NAK, returning to INIT");
      DhcpClient.-wrap12(DhcpClient.this);
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get3(DhcpClient.this));
    }
    
    protected boolean sendPacket()
    {
      return DhcpClient.-wrap5(DhcpClient.this, (Inet4Address)DhcpClient.-get4(DhcpClient.this).ipAddress.getAddress(), DhcpPacket.INADDR_ANY, null, packetDestination());
    }
  }
  
  class DhcpRebindingState
    extends DhcpClient.DhcpReacquiringState
  {
    public DhcpRebindingState()
    {
      super();
      this.mLeaseMsg = "Rebound";
    }
    
    public void enter()
    {
      super.enter();
      DhcpClient.-wrap8(DhcpClient.-get21(DhcpClient.this));
      if (!DhcpClient.-wrap3(DhcpClient.this))
      {
        Log.e("DhcpClient", "Failed to recreate UDP socket");
        DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get3(DhcpClient.this));
      }
    }
    
    protected Inet4Address packetDestination()
    {
      return DhcpPacket.INADDR_BROADCAST;
    }
  }
  
  class DhcpRebootingState
    extends DhcpClient.LoggingState
  {
    DhcpRebootingState()
    {
      super();
    }
  }
  
  class DhcpRenewingState
    extends DhcpClient.DhcpReacquiringState
  {
    public DhcpRenewingState()
    {
      super();
      this.mLeaseMsg = "Renewed";
    }
    
    protected Inet4Address packetDestination()
    {
      if (DhcpClient.-get4(DhcpClient.this).serverAddress != null) {
        return DhcpClient.-get4(DhcpClient.this).serverAddress;
      }
      return DhcpPacket.INADDR_BROADCAST;
    }
    
    public boolean processMessage(Message paramMessage)
    {
      if (super.processMessage(paramMessage)) {
        return true;
      }
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get5(DhcpClient.this));
      return true;
    }
  }
  
  class DhcpRequestingState
    extends DhcpClient.PacketRetransmittingState
  {
    public DhcpRequestingState()
    {
      super();
      this.mTimeout = 18000;
    }
    
    protected void receivePacket(DhcpPacket paramDhcpPacket)
    {
      if (!DhcpClient.this.isValidPacket(paramDhcpPacket)) {
        return;
      }
      if ((paramDhcpPacket instanceof DhcpAckPacket))
      {
        localDhcpResults = paramDhcpPacket.toDhcpResults();
        if (localDhcpResults != null)
        {
          DhcpClient.this.setDhcpLeaseExpiry(paramDhcpPacket);
          DhcpClient.-wrap6(DhcpClient.this, localDhcpResults, "Confirmed");
          DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get0(DhcpClient.this));
        }
      }
      while (!(paramDhcpPacket instanceof DhcpNakPacket))
      {
        DhcpResults localDhcpResults;
        return;
      }
      Log.d("DhcpClient", "Received NAK, returning to INIT");
      DhcpClient.-set2(DhcpClient.this, null);
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get3(DhcpClient.this));
    }
    
    protected boolean sendPacket()
    {
      return DhcpClient.-wrap5(DhcpClient.this, DhcpPacket.INADDR_ANY, (Inet4Address)DhcpClient.-get12(DhcpClient.this).ipAddress.getAddress(), DhcpClient.-get12(DhcpClient.this).serverAddress, DhcpPacket.INADDR_BROADCAST);
    }
    
    protected void timeout()
    {
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get3(DhcpClient.this));
    }
  }
  
  class DhcpSelectingState
    extends DhcpClient.LoggingState
  {
    DhcpSelectingState()
    {
      super();
    }
  }
  
  class DhcpState
    extends State
  {
    DhcpState() {}
    
    public void enter()
    {
      DhcpClient.-wrap7(DhcpClient.this);
      if ((DhcpClient.-wrap1(DhcpClient.this)) && (DhcpClient.-wrap2(DhcpClient.this)))
      {
        DhcpClient.-set3(DhcpClient.this, new DhcpClient.ReceiveThread(DhcpClient.this));
        DhcpClient.-get16(DhcpClient.this).start();
        return;
      }
      DhcpClient.-wrap12(DhcpClient.this);
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get19(DhcpClient.this));
    }
    
    public void exit()
    {
      if (DhcpClient.-get16(DhcpClient.this) != null)
      {
        DhcpClient.-get16(DhcpClient.this).halt();
        DhcpClient.-set3(DhcpClient.this, null);
      }
      DhcpClient.-wrap7(DhcpClient.this);
    }
    
    public boolean processMessage(Message paramMessage)
    {
      super.processMessage(paramMessage);
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get19(DhcpClient.this));
      return true;
    }
  }
  
  abstract class LoggingState
    extends State
  {
    private long mEnterTimeMs;
    
    LoggingState() {}
    
    private String messageName(int paramInt)
    {
      return (String)DhcpClient.-get24().get(paramInt, Integer.toString(paramInt));
    }
    
    private String messageToString(Message paramMessage)
    {
      long l = SystemClock.uptimeMillis();
      StringBuilder localStringBuilder = new StringBuilder(" ");
      TimeUtils.formatDuration(paramMessage.getWhen() - l, localStringBuilder);
      localStringBuilder.append(" ").append(messageName(paramMessage.what)).append(" ").append(paramMessage.arg1).append(" ").append(paramMessage.arg2).append(" ").append(paramMessage.obj);
      return localStringBuilder.toString();
    }
    
    public void enter()
    {
      this.mEnterTimeMs = SystemClock.elapsedRealtime();
    }
    
    public void exit()
    {
      long l1 = SystemClock.elapsedRealtime();
      long l2 = this.mEnterTimeMs;
      DhcpClient.-wrap11(DhcpClient.this, getName(), (int)(l1 - l2));
    }
    
    public String getName()
    {
      return getClass().getSimpleName();
    }
    
    public boolean processMessage(Message paramMessage)
    {
      return false;
    }
  }
  
  abstract class PacketRetransmittingState
    extends DhcpClient.LoggingState
  {
    protected int mTimeout = 0;
    private int mTimer;
    
    PacketRetransmittingState()
    {
      super();
    }
    
    public void enter()
    {
      super.enter();
      initTimer();
      maybeInitTimeout();
      DhcpClient.this.sendMessage(196709);
    }
    
    public void exit()
    {
      super.exit();
      DhcpClient.-get9(DhcpClient.this).cancel();
      DhcpClient.-get20(DhcpClient.this).cancel();
    }
    
    protected void initTimer()
    {
      this.mTimer = 2000;
    }
    
    protected int jitterTimer(int paramInt)
    {
      int i = paramInt / 10;
      return paramInt + (DhcpClient.-get14(DhcpClient.this).nextInt(i * 2) - i);
    }
    
    protected void maybeInitTimeout()
    {
      if (this.mTimeout > 0)
      {
        long l1 = SystemClock.elapsedRealtime();
        long l2 = this.mTimeout;
        DhcpClient.-get20(DhcpClient.this).schedule(l1 + l2);
      }
    }
    
    public boolean processMessage(Message paramMessage)
    {
      super.processMessage(paramMessage);
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 196709: 
        sendPacket();
        scheduleKick();
        return true;
      case 196710: 
        receivePacket((DhcpPacket)paramMessage.obj);
        return true;
      }
      timeout();
      return true;
    }
    
    protected abstract void receivePacket(DhcpPacket paramDhcpPacket);
    
    protected void scheduleKick()
    {
      long l1 = SystemClock.elapsedRealtime();
      long l2 = jitterTimer(this.mTimer);
      DhcpClient.-get9(DhcpClient.this).schedule(l1 + l2);
      this.mTimer *= 2;
      if (this.mTimer > 128000) {
        this.mTimer = 128000;
      }
    }
    
    protected abstract boolean sendPacket();
    
    protected void timeout() {}
  }
  
  class ReceiveThread
    extends Thread
  {
    private final byte[] mPacket = new byte['ל'];
    private volatile boolean mStopped = false;
    
    ReceiveThread() {}
    
    public void halt()
    {
      this.mStopped = true;
      DhcpClient.-wrap9(DhcpClient.this);
    }
    
    public void run()
    {
      Log.d("DhcpClient", "Receive thread started");
      while (!this.mStopped) {
        try
        {
          int i = Os.read(DhcpClient.-get13(DhcpClient.this), this.mPacket, 0, this.mPacket.length);
          DhcpPacket localDhcpPacket = DhcpPacket.decodeFullPacket(this.mPacket, i, 0);
          Log.d("DhcpClient", "Received packet: " + localDhcpPacket);
          DhcpClient.this.sendMessage(196710, localDhcpPacket);
        }
        catch (IOException|ErrnoException localIOException)
        {
          if (!this.mStopped)
          {
            Log.e("DhcpClient", "Read error", localIOException);
            DhcpClient.-wrap10(DhcpClient.this, DhcpErrorEvent.RECEIVE_ERROR);
          }
        }
        catch (DhcpPacket.ParseException localParseException)
        {
          Log.e("DhcpClient", "Can't parse packet: " + localParseException.getMessage());
          if (localParseException.errorCode == DhcpErrorEvent.DHCP_NO_COOKIE) {
            EventLog.writeEvent(1397638484, new Object[] { "31850211", Integer.valueOf(-1), DhcpPacket.ParseException.class.getName() });
          }
          DhcpClient.-wrap10(DhcpClient.this, localParseException.errorCode);
        }
      }
      Log.d("DhcpClient", "Receive thread stopped");
    }
  }
  
  class StoppedState
    extends State
  {
    StoppedState() {}
    
    public boolean processMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      if (DhcpClient.-get17(DhcpClient.this)) {
        DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get23(DhcpClient.this));
      }
      for (;;)
      {
        return true;
        DhcpClient.-wrap15(DhcpClient.this, DhcpClient.-get3(DhcpClient.this));
      }
    }
  }
  
  abstract class WaitBeforeOtherState
    extends DhcpClient.LoggingState
  {
    protected State mOtherState;
    
    WaitBeforeOtherState()
    {
      super();
    }
    
    public void enter()
    {
      super.enter();
      DhcpClient.-get1(DhcpClient.this).sendMessage(196611);
    }
    
    public boolean processMessage(Message paramMessage)
    {
      super.processMessage(paramMessage);
      switch (paramMessage.what)
      {
      default: 
        return false;
      }
      DhcpClient.-wrap15(DhcpClient.this, this.mOtherState);
      return true;
    }
  }
  
  class WaitBeforeRenewalState
    extends DhcpClient.WaitBeforeOtherState
  {
    public WaitBeforeRenewalState(State paramState)
    {
      super();
      this.mOtherState = paramState;
    }
  }
  
  class WaitBeforeStartState
    extends DhcpClient.WaitBeforeOtherState
  {
    public WaitBeforeStartState(State paramState)
    {
      super();
      this.mOtherState = paramState;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/dhcp/DhcpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */